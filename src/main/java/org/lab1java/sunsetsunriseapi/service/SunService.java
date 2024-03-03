package org.lab1java.sunsetsunriseapi.service;

import org.lab1java.sunsetsunriseapi.dao.SunRepo;
import org.lab1java.sunsetsunriseapi.entity.SunEntity;
import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SunService {

    private final ApiService externalApiService;
    private final SunRepo sunRepo;
    private final Logger logger = LoggerFactory.getLogger(SunService.class);

    public SunService(ApiService externalApiService, SunRepo sunRepo) {
        this.externalApiService = externalApiService;
        this.sunRepo = sunRepo;
    }

    public SunEntity sunResponse(SunEntity sun) {
        try {
            return sunRepo.save(sun);
        } catch (Exception e) {
            logger.error("Error saving SunEntity", e);
        }
        return null;
    }

    public SunResponseDto getSunInfo(SunRequestDto request) {
        try {
            Optional<SunEntity> optionalSunEntity = sunRepo.findByLatitudeAndLongitudeAndDate(request.getLatitude(), request.getLongitude(), request.getDate());
            if (optionalSunEntity.isPresent()) {
                SunEntity sunEntity = optionalSunEntity.get();
                return new SunResponseDto(sunEntity.getSunrise(), sunEntity.getSunset(), sunEntity.getTimeZone(), sunEntity.getCountry(), sunEntity.getCity());
            } else {
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
                sunResponseDto.setTimeZone(requestedTimeZone.toString());
                sunResponseDto.setCountry(countryName);
                sunResponseDto.setCity(city);

                SunEntity sunEntity = getSunEntity(request, sunResponseDto);
                sunRepo.save(sunEntity);

                return sunResponseDto;
            }
        } catch (Exception e) {
            logger.error("Error processing SunInfo", e);
        }
        return null;
    }

    private SunEntity getSunEntity(SunRequestDto request, SunResponseDto sunResponseDto) {
        SunEntity sunEntity = new SunEntity();
        sunEntity.setLatitude(request.getLatitude());
        sunEntity.setLongitude(request.getLongitude());
        sunEntity.setDate(request.getDate());
        sunEntity.setSunrise(sunResponseDto.getSunrise());
        sunEntity.setSunset(sunResponseDto.getSunset());
        sunEntity.setTimeZone(sunResponseDto.getTimeZone());
        sunEntity.setCountry(sunResponseDto.getCountry());
        sunEntity.setCity(sunResponseDto.getCity());
        return sunEntity;
    }
}
