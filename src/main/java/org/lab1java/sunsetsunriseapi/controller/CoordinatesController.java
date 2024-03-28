package org.lab1java.sunsetsunriseapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.CoordinatesDto;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.CoordinatesService;
import org.lab1java.sunsetsunriseapi.service.RequestCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling coordinate-related endpoints.
 */
@RestController
@RequestMapping("/coordinates")
@AllArgsConstructor
public class CoordinatesController {
  private final CoordinatesService coordinatesService;
  private final RequestCounterService counterService;
  private final Logger logger = LoggerFactory.getLogger(CoordinatesController.class);
  private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
  private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";
  private static final String GET_SUCCESS_MESSAGE =
      "Coordinates information was retrieved successfully.";

  @GetMapping("/get-request-count")
  public String getRequestCount() {
    int totalRequestCount = counterService.getRequestCount();
    return "Requests count: " + totalRequestCount;
  }

  /**
   * Retrieves coordinates information for a specific user at a given date and location.
   *
   * @param userId the ID of the user
   * @param latitude the latitude coordinate
   * @param longitude the longitude coordinate
   * @param date the date for which the information is requested
   * @return ResponseEntity containing the coordinates information
   * @throws JsonProcessingException if there is an issue processing JSON data
   */
  @GetMapping("/get-info/{userId}")
  public ResponseEntity<ResponseDto> getCoordinatesInfo(@PathVariable int userId,
                                                        @RequestParam() Double latitude,
                                                        @RequestParam() Double longitude,
                                                        @RequestParam() String date)
      throws JsonProcessingException {
    counterService.requestIncrement();
    logger.info("GET endpoint /coordinates/get-info/{userId} was called.");

    RequestDto request = new RequestDto(latitude, longitude, LocalDate.parse(date));
    ResponseDto response = coordinatesService.getCoordinatesInfo(userId, request);

    logger.info(GET_SUCCESS_MESSAGE);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Retrieves coordinates information for sunrise starting at a specific hour.
   *
   * @param hour the hour at which sunrise starts
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of coordinates information
   */
  @GetMapping("/get-info-sunrise-hour/{hour}")
  public ResponseEntity<Page<Coordinates>> getCoordinatesInfoBySunriseStartingHour(
                                              @PathVariable int hour,
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    counterService.requestIncrement();
    logger.info("GET endpoint /coordinates/get-info-sunrise-hour/{hour} was called.");

    Page<Coordinates> coordinatesPage =
        coordinatesService.getCoordinatesInfoBySunriseStartingHour(hour, pageNumber, pageSize);

    logger.info(GET_SUCCESS_MESSAGE);
    return new ResponseEntity<>(coordinatesPage, HttpStatus.OK);
  }

  /**
   * Retrieves coordinates information for sunset starting at a specific hour.
   *
   * @param hour the hour at which sunset starts
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of coordinates information
   */
  @GetMapping("/get-info-sunset-hour/{hour}")
  public ResponseEntity<Page<Coordinates>> getCoordinatesInfoBySunsetStartingHour(
                                              @PathVariable int hour,
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    counterService.requestIncrement();
    logger.info("GET endpoint /coordinates/get-info-sunset-hour/{hour} was called.");

    Page<Coordinates> coordinatesPage =
        coordinatesService.getCoordinatesInfoBySunsetStartingHour(hour, pageNumber, pageSize);

    logger.info(GET_SUCCESS_MESSAGE);
    return new ResponseEntity<>(coordinatesPage, HttpStatus.OK);
  }

  /**
   * Retrieves users associated with a specific coordinate.
   *
   * @param id the ID of the coordinate
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of user information
   */
  @GetMapping("/get-users/{id}")
  public ResponseEntity<Page<User>> getUsersFromCoordinates(@PathVariable long id,
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    counterService.requestIncrement();
    logger.info("GET endpoint /coordinates/get-users/{id} was called.");

    Page<User> userPage = coordinatesService.getUsersFromCoordinates(id, pageNumber, pageSize);

    logger.info("Coordinate's users information was retrieved successfully.");
    return new ResponseEntity<>(userPage, HttpStatus.OK);
  }

  /**
   * Retrieves information for all coordinates.
   *
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of coordinates information
   */
  @GetMapping("/get-all-coordinates-info")
  public ResponseEntity<Page<CoordinatesDto>> getAllCoordinateInfo(
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    counterService.requestIncrement();
    logger.info("GET endpoint /coordinates/get-all-coordinates-info was called.");

    Page<CoordinatesDto> coordinatesDtoPage =
        coordinatesService.getAllCoordinatesInfo(pageNumber, pageSize);

    logger.info("All coordinates information was retrieved successfully.");
    return new ResponseEntity<>(coordinatesDtoPage, HttpStatus.OK);
  }

  /**
   * Creates new coordinates information.
   *
   * @param requestDto the DTO containing the coordinates information to be created
   * @return ResponseEntity indicating the success of the operation
   */
  @PostMapping("/create")
  public ResponseEntity<String> addCoordinatesInfo(@RequestBody RequestDto requestDto)
      throws JsonProcessingException {
    counterService.requestIncrement();
    logger.info("POST endpoint /coordinates/create was called.");

    coordinatesService.createCoordinatesInfo(requestDto);

    logger.info("Coordinates information was created successfully.");
    return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);
  }

  /**
   * Creates new coordinates information in bulk.
   *
   * @param requestDtoList the list of DTOs containing the coordinates information to be created
   * @return ResponseEntity indicating the success of the operation
   */
  @PostMapping("/create-bulk")
  public ResponseEntity<String> addCoordinatesInfoBulk(
                                                    @RequestBody List<RequestDto> requestDtoList) {
    counterService.requestIncrement();
    logger.info("POST endpoint /coordinates/create-bulk was called.");

    coordinatesService.createCoordinatesInfoBulk(requestDtoList);

    logger.info("Coordinates information array was created successfully.");
    return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.OK);
  }

  /**
   * Updates coordinates information.
   *
   * @param id the ID of the coordinates to update
   * @param updateDto the DTO containing the updated coordinates information
   * @return ResponseEntity containing the updated coordinates information
   */
  @PutMapping("/update/{id}")
  public ResponseEntity<Coordinates> updateCoordinatesInfo(@PathVariable Long id,
                                                           @RequestBody CoordinatesDto updateDto) {
    counterService.requestIncrement();
    logger.info("PUT endpoint /coordinates/update/{id} was called.");

    Coordinates coordinates = coordinatesService.updateCoordinatesInfo(id, updateDto);

    logger.info("Coordinates information was updated successfully.");
    return new ResponseEntity<>(coordinates, HttpStatus.OK);
  }

  /**
   * Deletes coordinates information.
   *
   * @param id the ID of the coordinates to delete
   * @return ResponseEntity indicating the success of the operation
   */
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteCoordinatesInfo(@PathVariable Long id) {
    counterService.requestIncrement();
    logger.info("DELETE endpoint /coordinates/delete/{id} was called.");

    coordinatesService.deleteCoordinatesInfoFromDatabase(id);

    logger.info("Coordinates information was deleted successfully.");
    return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
  }
}
