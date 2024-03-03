package org.lab1java.sunsetsunriseapi.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lab1java.sunsetsunriseapi.dao.SunRepo;
import org.lab1java.sunsetsunriseapi.entity.SunEntity;
import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class SunService {

    private final ApiService externalApiService;

    private final SunRepo sunRepo;

    @Value("${external.api.urlTimeZone}")
    private String externalApiUrlTimeZone;
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

    private String getTimeZone(double latitude, double longitude) {
        try {
            String apiUrl = String.format("%s?lat=%f&lng=%f&username=%s",
                    externalApiUrlTimeZone, latitude, longitude, "willygodx");
            ResponseEntity<String> apiResponseEntity = new RestTemplate().getForEntity(apiUrl, String.class);
            return extractTimeZoneFromResponse(apiResponseEntity.getBody());
        } catch (Exception e) {
            logger.error("Error getting timezone", e);
        }
        return null;
    }

    private String extractTimeZoneFromResponse(String response) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.get("timezoneId").getAsString();
        } catch (Exception e) {
            logger.error("Error extracting timezone from response", e);
        }
        return null;
    }

    public SunResponseDto getSunInfo(SunRequestDto request) {
        try {
            Optional<SunEntity> optionalSunEntity = sunRepo.findByLatitudeAndLongitudeAndDate(request.getLatitude(), request.getLongitude(), request.getDate());
            if (optionalSunEntity.isPresent()) {
                SunEntity sunEntity = optionalSunEntity.get();
                return new SunResponseDto(sunEntity.getSunrise(), sunEntity.getSunset());
            } else {
                String apiResponse = externalApiService.getApiResponse(request);
                SunResponseDto sunResponseDto = externalApiService.extractSunInfoFromApiResponse(apiResponse);

                String timeZone = getTimeZone(request.getLatitude(), request.getLongitude());
                ZoneId utc0Zone = ZoneId.of("UTC+0");
                ZonedDateTime zonedDateTimeUtc0 = ZonedDateTime.of(
                        ZonedDateTime.now(utc0Zone).toLocalDate(),
                        sunResponseDto.getSunrise(),
                        utc0Zone
                );
                ZonedDateTime zonedDateTimeUtc0z = ZonedDateTime.of(
                        ZonedDateTime.now(utc0Zone).toLocalDate(),
                        sunResponseDto.getSunset(),
                        utc0Zone
                );

                assert timeZone != null;
                ZoneId utc9Zone = ZoneId.of(timeZone);
                ZonedDateTime zonedDateTimeUtc9 = zonedDateTimeUtc0.withZoneSameInstant(utc9Zone);
                ZonedDateTime zonedDateTimeUtc9z = zonedDateTimeUtc0z.withZoneSameInstant(utc9Zone);
                sunResponseDto.setSunrise(zonedDateTimeUtc9.toLocalTime());
                sunResponseDto.setSunset(zonedDateTimeUtc9z.toLocalTime());

                SunEntity sunEntity = new SunEntity();
                sunEntity.setLatitude(request.getLatitude());
                sunEntity.setLongitude(request.getLongitude());
                sunEntity.setDate(request.getDate());
                sunEntity.setSunrise(sunResponseDto.getSunrise());
                sunEntity.setSunset(sunResponseDto.getSunset());
                sunRepo.save(sunEntity);

                return sunResponseDto;
            }
        } catch (Exception e) {
            logger.error("Error processing SunInfo", e);
        }
        return null;
    }
}
