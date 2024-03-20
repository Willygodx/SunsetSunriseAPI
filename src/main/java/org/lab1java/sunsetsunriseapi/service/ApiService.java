package org.lab1java.sunsetsunriseapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ApiService {
    private final ObjectMapper objectMapper;

    @Value("${external.api.urlSunInfo}")
    private String externalApiUrlSunsetSunriseInfo;

    @Value("${external.api.urlTimeZone}")
    private String externalApiUrlTimeZone;

    @Value("${external.api.urlCountry}")
    private String externalApiUrlCountry;

    public ApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getCountryNameByCode(String countryCode) {
        Locale locale = new Locale.Builder().setRegion(countryCode.trim()).build();
        return locale.getDisplayCountry();
    }

    public String getCountry(double latitude, double longitude) {
        String apiUrl = String.format("%s?lat=%f&lng=%f&username=%s",
                externalApiUrlCountry, latitude, longitude, "willygodx");
        ResponseEntity<String> apiResponseEntity = new RestTemplate().getForEntity(apiUrl, String.class);
        return apiResponseEntity.getBody();
    }

    public String getTimeZone(double latitude, double longitude) {
        String apiUrl = String.format("%s?lat=%f&lng=%f&username=%s",
                externalApiUrlTimeZone, latitude, longitude, "willygodx");
        ResponseEntity<String> apiResponseEntity = new RestTemplate().getForEntity(apiUrl, String.class);
        return extractTimeZoneFromResponse(apiResponseEntity.getBody());
    }

    public String extractTimeZoneFromResponse(String response) {
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        return jsonResponse.get("timezoneId").getAsString();
    }

    public String getApiResponse(RequestDto request) {
        String apiUrl = String.format("%s?lat=%f&lng=%f&date=%s",
                externalApiUrlSunsetSunriseInfo, request.getLatitude(), request.getLongitude(), request.getDate());
        ResponseEntity<String> apiResponseEntity = new RestTemplate().getForEntity(apiUrl, String.class);
        return apiResponseEntity.getBody();
    }

    public ResponseDto extractCoordinatesInfoFromApiResponse(String apiResponse) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(apiResponse);
        JsonNode resultsNode = jsonNode.get("results");

        LocalTime sunriseTime = parseTime(resultsNode, "sunrise");
        LocalTime sunsetTime = parseTime(resultsNode, "sunset");

        return new ResponseDto(sunriseTime, sunsetTime);
    }

    private LocalTime parseTime(JsonNode parentNode, String fieldName) {
        JsonNode timeNode = parentNode.get(fieldName);

        String timeString = timeNode.asText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ss a");
        return LocalTime.parse(timeString, formatter);
    }
}