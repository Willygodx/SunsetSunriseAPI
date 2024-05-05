package org.core.sunsetsunrise.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.core.sunsetsunrise.dto.RequestDto;
import org.core.sunsetsunrise.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for interacting with external APIs.
 */
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

  /**
   * Retrieves the country information based on latitude and longitude.
   *
   * @param latitude  the latitude
   * @param longitude the longitude
   * @return the country information
   */
  public String getCountry(double latitude, double longitude) {
    String apiUrl = String.format("%s?lat=%f&lng=%f&username=%s",
        externalApiUrlCountry, latitude, longitude, "willygodx");
    ResponseEntity<String> apiResponseEntity =
        new RestTemplate().getForEntity(apiUrl, String.class);
    return apiResponseEntity.getBody();
  }

  /**
   * Retrieves the time zone information based on latitude and longitude.
   *
   * @param latitude  the latitude
   * @param longitude the longitude
   * @return the time zone information
   */
  public String getTimeZone(double latitude, double longitude) {
    String apiUrl = String.format("%s?lat=%f&lng=%f&username=%s",
        externalApiUrlTimeZone, latitude, longitude, "willygodx");
    ResponseEntity<String> apiResponseEntity =
        new RestTemplate().getForEntity(apiUrl, String.class);
    return extractTimeZoneFromResponse(apiResponseEntity.getBody());
  }

  public String extractTimeZoneFromResponse(String response) {
    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
    return jsonResponse.get("timezoneId").getAsString();
  }

  /**
   * Retrieves the sunset and sunrise times from the external API based on the provided request.
   *
   * @param request the request containing latitude, longitude, and date
   * @return the API response
   */
  public String getApiResponse(RequestDto request) {
    String apiUrl = String.format("%s?lat=%f&lng=%f&date=%s",
        externalApiUrlSunsetSunriseInfo, request.getLatitude(), request.getLongitude(),
        request.getDate());
    ResponseEntity<String> apiResponseEntity =
        new RestTemplate().getForEntity(apiUrl, String.class);
    return apiResponseEntity.getBody();
  }

  /**
   * Extracts sunset and sunrise times from the API response and returns them as a ResponseDto.
   *
   * @param apiResponse the API response
   * @return the extracted sunset and sunrise times
   * @throws JsonProcessingException if there is an error processing the JSON response
   */
  public ResponseDto extractCoordinatesInfoFromApiResponse(String apiResponse)
      throws JsonProcessingException {
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