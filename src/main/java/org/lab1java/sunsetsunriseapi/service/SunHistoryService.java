package org.lab1java.sunsetsunriseapi.service;

import org.lab1java.sunsetsunriseapi.dao.SunHistoryRepository;
import org.lab1java.sunsetsunriseapi.dao.TimeZoneRepository;
import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.SunHistoryDto;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SunHistoryService {

    private final ApiService externalApiService;
    private final SunHistoryRepository sunHistoryRepository;
    private final UserRepository userRepository;
    private final TimeZoneRepository timeZoneRepository;
    private final Logger logger = LoggerFactory.getLogger(SunHistoryService.class);

    public SunHistoryService(ApiService externalApiService, SunHistoryRepository sunHistoryRepository, UserRepository userRepository, TimeZoneRepository timeZoneRepository) {
        this.externalApiService = externalApiService;
        this.sunHistoryRepository = sunHistoryRepository;
        this.userRepository = userRepository;
        this.timeZoneRepository = timeZoneRepository;
    }

    public SunResponseDto getSunInfo(int id, SunRequestDto request) {
        try {
            String apiResponse = externalApiService.getApiResponse(request);
            SunResponseDto sunResponseDto = externalApiService.extractSunInfoFromApiResponse(apiResponse);

            String timeZone = externalApiService.getTimeZone(request.getLatitude(), request.getLongitude());
            ZoneId defaultTimeZone = ZoneId.of("UTC+0");
            ZonedDateTime zonedDateTimeSunrise = ZonedDateTime.of(
                    ZonedDateTime.now(defaultTimeZone).toLocalDate(),
                    sunResponseDto.getSunrise(),
                    defaultTimeZone
            );
            ZonedDateTime zonedDateTimeSunset = ZonedDateTime.of(
                    ZonedDateTime.now(defaultTimeZone).toLocalDate(),
                    sunResponseDto.getSunset(),
                    defaultTimeZone
            );

            assert timeZone != null;
            ZoneId requestedTimeZone = ZoneId.of(timeZone);
            ZonedDateTime requestedZonedDateTimeSunrise = zonedDateTimeSunrise.withZoneSameInstant(requestedTimeZone);
            ZonedDateTime requestedZonedDateTimeSunset = zonedDateTimeSunset.withZoneSameInstant(requestedTimeZone);

            String countryCode = externalApiService.getCountry(request.getLatitude(), request.getLongitude());
            assert countryCode != null;
            String trimmedCode = countryCode.trim();
            String countryName = externalApiService.getCountryNameByCode(trimmedCode);

            String city = requestedTimeZone.toString().split("/")[1];

            sunResponseDto.setSunrise(requestedZonedDateTimeSunrise.toLocalTime());
            sunResponseDto.setSunset(requestedZonedDateTimeSunset.toLocalTime());
            sunResponseDto.setCountry(countryName);
            sunResponseDto.setTimeZone(timeZone);
            sunResponseDto.setCity(city);

            SunHistory sunHistory = getSunEntity(request, sunResponseDto);


            Optional<User> optionalUser = userRepository.findById(id);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Optional<TimeZone> optionalTimeZone = timeZoneRepository.findByName(timeZone);
                TimeZone temp = optionalTimeZone.orElseGet(() -> new TimeZone(timeZone));

                user.getTimeZoneSet().add(temp);
                temp.getUserSet().add(user);

                user.getSunHistoryList().add(sunHistory);
                sunHistory.setUser(user);

                temp.getSunHistoryList().add(sunHistory);
                sunHistory.setTimeZone(temp);


                sunHistoryRepository.save(sunHistory);
                return sunResponseDto;
            }
        } catch (Exception e) {
            logger.error("Error processing SunInfo", e);
        }
        return null;
    }

    private SunHistory getSunEntity(SunRequestDto request, SunResponseDto sunResponseDto) {
        SunHistory sunHistory = new SunHistory();
        sunHistory.setLatitude(request.getLatitude());
        sunHistory.setLongitude(request.getLongitude());
        sunHistory.setDate(request.getDate());
        sunHistory.setSunrise(sunResponseDto.getSunrise());
        sunHistory.setSunset(sunResponseDto.getSunset());
        sunHistory.setCountry(sunResponseDto.getCountry());
        sunHistory.setCity(sunResponseDto.getCity());
        return sunHistory;
    }

    public SunResponseDto getSunInfoFromDatabase(double latitude, double longitude, LocalDate date) {
        Optional<SunHistory> optionalSunEntity = sunHistoryRepository.findByLatitudeAndLongitudeAndDate(latitude, longitude, date);
        if (optionalSunEntity.isPresent()) {
            SunHistory sunHistory = optionalSunEntity.get();
            SunResponseDto dto = new SunResponseDto(sunHistory.getSunrise(), sunHistory.getSunset(), sunHistory.getTimeZone().getName(), sunHistory.getCountry(), sunHistory.getCity());
            dto.setTimeZone(sunHistory.getTimeZone().getName());
            return dto;
        } else {
            return null;
        }
    }

    public void deleteSunInfoFromDatabase(Long id) {
        if (sunHistoryRepository.existsById(id)) {
            sunHistoryRepository.deleteById(id);
        }
    }

    public SunHistoryDto updateSunInfo(Long id, SunHistoryDto updateDto) throws Exception {
        SunHistory sunHistory = sunHistoryRepository.findById(id)
                .orElseThrow(() -> new Exception("SunHistory not found"));

        sunHistory.setLatitude(updateDto.getLatitude());
        sunHistory.setLongitude(updateDto.getLongitude());
        sunHistory.setDate(updateDto.getDate());
        sunHistory.setSunrise(updateDto.getSunrise());
        sunHistory.setSunset(updateDto.getSunset());
        sunHistory.setCity(updateDto.getCity());
        sunHistory.setCountry(updateDto.getCountry());

        sunHistoryRepository.save(sunHistory);
        return new SunHistoryDto(sunHistory.getLatitude(), sunHistory.getLongitude(), sunHistory.getDate(), sunHistory.getSunrise(), sunHistory.getSunset(), sunHistory.getCountry(), sunHistory.getCity());
    }

    public SunHistory createSunHistory(SunHistoryDto sunHistoryDto) {
        SunHistory sunHistory = new SunHistory(sunHistoryDto.getLatitude(), sunHistoryDto.getLongitude(), sunHistoryDto.getDate(), sunHistoryDto.getSunrise(), sunHistoryDto.getSunset(), sunHistoryDto.getCountry(), sunHistoryDto.getCity());
        return sunHistoryRepository.save(sunHistory);
    }

}
