package org.lab1java.sunsetsunriseapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.core.sunsetsunrise.service.ApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.core.sunsetsunrise.dto.ResponseDto;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiServiceTest {
  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private ApiService apiService;

  @Test
  void testGetCountryNameByCode_Success() {
    String countryCode = "US";
    String expectedCountryName = "United States";

    String actualCountryName = apiService.getCountryNameByCode(countryCode);

    assertEquals(expectedCountryName, actualCountryName);
  }

  @Test
  void testGetCountryNameByCode_WithSpaces() {
    String countryCodeWithSpaces = " US ";
    String expectedCountryName = "United States";

    String actualCountryName = apiService.getCountryNameByCode(countryCodeWithSpaces);

    assertEquals(expectedCountryName, actualCountryName);
  }

  @Test
  void testExtractTimeZoneFromResponse() {
    String response = "{\"timezoneId\":\"America/New_York\"}";
    String expectedTimeZone = "America/New_York";

    String actualTimeZone = apiService.extractTimeZoneFromResponse(response);

    assertEquals(expectedTimeZone, actualTimeZone);
  }

  @Test
  void testExtractCoordinatesInfoFromApiResponse() throws JsonProcessingException {
    String apiResponse = "{\"results\":{\"sunrise\":\"6:00:00 AM\",\"sunset\":\"6:00:00 PM\"}}";
    JsonNode jsonNode = new ObjectMapper().readTree(apiResponse);
    when(objectMapper.readTree(anyString())).thenReturn(jsonNode);

    ResponseDto expectedDto = new ResponseDto(LocalTime.of(6, 0), LocalTime.of(18, 0));

    ResponseDto actualDto = apiService.extractCoordinatesInfoFromApiResponse(apiResponse);

    assertEquals(expectedDto.getSunrise(), actualDto.getSunrise());
    assertEquals(expectedDto.getSunset(), actualDto.getSunset());
  }

}
