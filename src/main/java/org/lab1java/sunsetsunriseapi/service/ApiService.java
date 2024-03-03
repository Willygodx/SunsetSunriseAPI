package org.lab1java.sunsetsunriseapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ApiService {

    private final ObjectMapper objectMapper;

    @Value("${external.api.urlSunInfo}")
    private String externalApiUrlSunsetSunriseInfo;

    @Value("${external.api.urlTimeZone}")
    private String externalApiUrlTimeZone;

    @Value("${external.api.urlCountry}")
    private String externalApiUrlCountry;

    private final Logger logger = LoggerFactory.getLogger(ApiService.class);

    public ApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getCountryNameByCode(String countryCode) {
        Locale locale = new Locale.Builder().setRegion(countryCode.trim()).build();
        return locale.getDisplayCountry();
    }

    public String getCountry(double latitude, double longitude) {
        try {
            String apiUrl = String.format("%s?lat=%f&lng=%f&username=%s",
                    externalApiUrlCountry, latitude, longitude, "willygodx");
            ResponseEntity<String> apiResponseEntity = new RestTemplate().getForEntity(apiUrl, String.class);
            return apiResponseEntity.getBody();
        } catch (Exception e) {
            logger.error("Error getting country", e);
        }
        return null;
    }

    public String getTimeZone(double latitude, double longitude) {
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

    public String extractTimeZoneFromResponse(String response) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.get("timezoneId").getAsString();
        } catch (Exception e) {
            logger.error("Error extracting timezone from response", e);
        }
        return null;
    }

    public String getApiResponse(SunRequestDto request) {
        try {
            String apiUrl = String.format("%s?lat=%f&lng=%f&date=%s",
                    externalApiUrlSunsetSunriseInfo, request.getLatitude(), request.getLongitude(), request.getDate());
            ResponseEntity<String> apiResponseEntity = new RestTemplate().getForEntity(apiUrl, String.class);
            return apiResponseEntity.getBody();
        } catch (Exception e) {
            logger.error("Error while getting response from API", e);
        }
        return null;
    }

    public SunResponseDto extractSunInfoFromApiResponse(String apiResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(apiResponse);
            JsonNode resultsNode = jsonNode.get("results");

            if (resultsNode != null) {
                LocalTime sunriseTime = parseTime(resultsNode, "sunrise");
                LocalTime sunsetTime = parseTime(resultsNode, "sunset");

                return new SunResponseDto(sunriseTime, sunsetTime);

            }
        } catch (IOException e) {
            logger.error("Error while extracting sun info from API response", e);
        }
        return null;
    }

    private LocalTime parseTime(JsonNode parentNode, String fieldName) {
        try {
            JsonNode timeNode = parentNode.get(fieldName);

            if (timeNode != null) {
                String timeString = timeNode.asText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ss a");
                return LocalTime.parse(timeString, formatter);
            }
        } catch (Exception e) {
            logger.error("Error while parsing time", e);
        }
        return null;
    }
}
