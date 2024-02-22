package org.lab1java.sunsetsunriseapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lab1java.sunsetsunriseapi.model.SunInfoRequest;
import org.lab1java.sunsetsunriseapi.model.SunInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
public class ApiService {

    private final ObjectMapper objectMapper;

    @Value("${external.api.url}")
    private String externalApiUrl;

    public ApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getApiResponse(SunInfoRequest request) {
        String apiUrl = String.format("%s?lat=%f&lng=%f&date=%s",
                externalApiUrl, request.getLatitude(), request.getLongitude(), request.getDate());
        ResponseEntity<String> apiResponseEntity = new RestTemplate().getForEntity(apiUrl, String.class);
        return apiResponseEntity.getBody();
    }

    public SunInfoResponse extractSunInfoFromApiResponse(String apiResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(apiResponse);
            JsonNode resultsNode = jsonNode.get("results");

            if (resultsNode != null) {
                LocalTime sunriseTime = parseTime(resultsNode, "sunrise");
                LocalTime sunsetTime = parseTime(resultsNode, "sunset");

                return new SunInfoResponse(sunriseTime, sunsetTime);

            }
        } catch (IOException e) {
           return null;
        }

        return null;
    }

    private LocalTime parseTime(JsonNode parentNode, String fieldName) {
        JsonNode timeNode = parentNode.get(fieldName);

        if (timeNode != null) {
            String timeString = timeNode.asText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ss a");
            return LocalTime.parse(timeString, formatter);
        }

        return null;
    }
}
