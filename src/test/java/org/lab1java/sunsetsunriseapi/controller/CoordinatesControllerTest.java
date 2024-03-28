package org.lab1java.sunsetsunriseapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab1java.sunsetsunriseapi.dto.CoordinatesDto;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.CoordinatesService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class CoordinatesControllerTest {
  @Mock
  private CoordinatesService coordinatesService;

  @InjectMocks
  private CoordinatesController coordinatesController;

  @Test
  void testGetCoordinatesInfo() throws JsonProcessingException {
    int userId = 1;
    double latitude = 17.735620;
    double longitude = 9.323843;
    String date = "2024-03-20";
    LocalTime sunrise = LocalTime.parse("06:25:13");
    LocalTime sunset = LocalTime.parse("18:34:49");
    String timeZone = "Africa/Niamey";
    String country = "Niger";
    String city = "Niamey";

    ResponseDto responseDto = new ResponseDto(sunrise, sunset, timeZone, country, city);
    when(coordinatesService.getCoordinatesInfo(eq(userId), any(RequestDto.class))).thenReturn(
        responseDto);

    ResponseEntity<ResponseDto> response =
        coordinatesController.getCoordinatesInfo(userId, latitude, longitude, date);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseDto, response.getBody());
    verify(coordinatesService, times(1)).getCoordinatesInfo(eq(userId), any(RequestDto.class));
  }

  @Test
  void testGetCoordinatesInfoBySunriseStartingHour() {
    int hour = 6;

    List<Coordinates> coordinatesList = new ArrayList<>();
    double latitude = 51.5074;
    double longitude = -0.1278;
    LocalDate date = LocalDate.now();
    LocalTime sunrise = LocalTime.of(6, 30, 0);
    LocalTime sunset = LocalTime.of(18, 0, 0);
    String timeZone = "Europe/London";
    String city = "London";
    coordinatesList.add(
        new Coordinates(latitude, longitude, date, sunrise, sunset, timeZone, city));
    Page<Coordinates> coordinatesPage =
        new PageImpl<>(coordinatesList, PageRequest.of(0, 10), coordinatesList.size());
    when(coordinatesService.getCoordinatesInfoBySunriseStartingHour(hour, 0, 10)).thenReturn(
        coordinatesPage);

    ResponseEntity<Page<Coordinates>> response =
        coordinatesController.getCoordinatesInfoBySunriseStartingHour(hour, 0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(coordinatesPage, response.getBody());
    verify(coordinatesService, times(1)).getCoordinatesInfoBySunriseStartingHour(hour, 0, 10);
  }

  @Test
  void testGetCoordinatesInfoBySunsetStartingHour() {
    int hour = 18;

    List<Coordinates> coordinatesList = new ArrayList<>();
    double latitude = 51.5074;
    double longitude = -0.1278;
    LocalDate date = LocalDate.now();
    LocalTime sunrise = LocalTime.of(6, 30, 0);
    LocalTime sunset = LocalTime.of(18, 0, 0);
    String timeZone = "Europe/London";
    String city = "London";
    coordinatesList.add(
        new Coordinates(latitude, longitude, date, sunrise, sunset, timeZone, city));
    Page<Coordinates> coordinatesPage =
        new PageImpl<>(coordinatesList, PageRequest.of(0, 10), coordinatesList.size());
    when(coordinatesService.getCoordinatesInfoBySunsetStartingHour(hour, 0, 10)).thenReturn(
        coordinatesPage);

    ResponseEntity<Page<Coordinates>> response =
        coordinatesController.getCoordinatesInfoBySunsetStartingHour(hour, 0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(coordinatesPage, response.getBody());
    verify(coordinatesService, times(1)).getCoordinatesInfoBySunsetStartingHour(hour, 0, 10);
  }

  @Test
  void testGetUsersFromCoordinates() {
    // Arrange
    long id = 1L;
    int pageNumber = 0;
    int pageSize = 10;
    List<User> userList = new ArrayList<>();
    userList.add(new User("Linkong344@gmail.com", "Willygodx"));
    userList.add(new User("Enotland34@yandex.ru", "Sombrero2312"));
    Page<User> userPage =
        new PageImpl<>(userList, PageRequest.of(pageNumber, pageSize), userList.size());
    when(coordinatesService.getUsersFromCoordinates(id, pageNumber, pageSize)).thenReturn(userPage);

    ResponseEntity<Page<User>> response =
        coordinatesController.getUsersFromCoordinates(id, pageNumber, pageSize);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(userPage, response.getBody());
    verify(coordinatesService, times(1)).getUsersFromCoordinates(id, pageNumber, pageSize);
  }

  @Test
  void testGetAllCoordinateInfo() {
    int pageNumber = 0;
    int pageSize = 10;
    List<CoordinatesDto> coordinatesDtoList = new ArrayList<>();
    coordinatesDtoList.add(new CoordinatesDto(17.73562, 9.323843, LocalDate.parse("2024-03-20"),
        LocalTime.of(6, 25, 13),
        LocalTime.of(18, 34, 49), "Africa/Niamey", "Niger", "Niamey"));
    coordinatesDtoList.add(new CoordinatesDto(51.508530, -0.125740, LocalDate.parse("2024-03-20"),
        LocalTime.of(5, 55, 24),
        LocalTime.of(18, 19, 1), "Europe/London", "England", "London"));
    Page<CoordinatesDto> coordinatesDtoPage =
        new PageImpl<>(coordinatesDtoList, PageRequest.of(pageNumber, pageSize),
            coordinatesDtoList.size());
    when(coordinatesService.getAllCoordinatesInfo(pageNumber, pageSize)).thenReturn(
        coordinatesDtoPage);

    ResponseEntity<Page<CoordinatesDto>> response =
        coordinatesController.getAllCoordinateInfo(pageNumber, pageSize);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(coordinatesDtoPage, response.getBody());
    verify(coordinatesService, times(1)).getAllCoordinatesInfo(pageNumber, pageSize);
  }

  @Test
  void testAddCoordinatesInfo() throws JsonProcessingException {
    RequestDto requestDto = new RequestDto(51.508530, -0.125740, LocalDate.parse("2024-03-20"));

    ResponseEntity<String> response = coordinatesController.addCoordinatesInfo(requestDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Created successfully!", response.getBody());
    verify(coordinatesService, times(1)).createCoordinatesInfo(requestDto);
  }

  @Test
  void testAddCoordinatesInfoBulk() {
    List<RequestDto> requestDtoList = Arrays.asList(
        new RequestDto(51.508530, -0.125740, LocalDate.parse("2024-03-20")),
        new RequestDto(1.533355, 32.216660, LocalDate.parse("2024-03-20"))
    );

    ResponseEntity<String> response = coordinatesController.addCoordinatesInfoBulk(requestDtoList);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Created successfully!", response.getBody());
    verify(coordinatesService, times(1)).createCoordinatesInfoBulk(requestDtoList);
  }

  @Test
  void testUpdateCoordinatesInfo() {
    Long id = 1L;
    double latitude = 51.5074;
    double longitude = -0.1278;
    LocalDate date = LocalDate.now();
    LocalTime sunrise = LocalTime.of(6, 30, 0);
    LocalTime sunset = LocalTime.of(18, 0, 0);
    String timeZone = "Europe/London";
    String city = "London";

    CoordinatesDto updateDto = new CoordinatesDto(17.73562, 9.323843, LocalDate.parse("2024-03-20"),
        LocalTime.of(6, 25, 13),
        LocalTime.of(18, 34, 49), "Africa/Niamey", "Niger", "Niamey");
    Coordinates updatedCoordinates =
        new Coordinates(latitude, longitude, date, sunrise, sunset, timeZone, city);
    when(coordinatesService.updateCoordinatesInfo(id, updateDto)).thenReturn(updatedCoordinates);

    ResponseEntity<Coordinates> response =
        coordinatesController.updateCoordinatesInfo(id, updateDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedCoordinates, response.getBody());
    verify(coordinatesService, times(1)).updateCoordinatesInfo(id, updateDto);
  }

  @Test
  void testDeleteCoordinatesInfo() {
    Long id = 1L;

    ResponseEntity<String> response = coordinatesController.deleteCoordinatesInfo(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals("Deleted successfully!", response.getBody());
    verify(coordinatesService, times(1)).deleteCoordinatesInfoFromDatabase(id);
  }
}
