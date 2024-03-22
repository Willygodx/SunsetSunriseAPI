package org.lab1java.sunsetsunriseapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.CoordinatesDto;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.CoordinatesService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coordinates")
@AllArgsConstructor
public class CoordinatesController {
    private final CoordinatesService coordinatesService;
    private final Logger logger = LoggerFactory.getLogger(CoordinatesController.class);
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";
    private static final String GET_SUCCESS_MESSAGE = "Coordinates information was retrieved successfully.";

    @GetMapping("/get-info/{userId}")
    public ResponseEntity<ResponseDto> getCoordinatesInfo(@PathVariable int userId,
                                                          @RequestParam() Double latitude,
                                                          @RequestParam() Double longitude,
                                                          @RequestParam() String date) throws JsonProcessingException {
        logger.info("GET endpoint /coordinates/get-info/{userId} was called.");

        RequestDto request = new RequestDto(latitude, longitude, LocalDate.parse(date));
        ResponseDto response = coordinatesService.getCoordinatesInfo(userId, request);

        logger.info(GET_SUCCESS_MESSAGE);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-info-sunrise-hour/{hour}")
    public ResponseEntity<List<Coordinates>> getCoordinatesInfoBySunriseStartingHour(@PathVariable int hour) {
        logger.info("GET endpoint /coordinates/get-info-sunrise-hour/{hour} was called.");

        List<Coordinates> coordinatesList = coordinatesService.getCoordinatesInfoBySunriseStartingHour(hour);

        logger.info(GET_SUCCESS_MESSAGE);
        return new ResponseEntity<>(coordinatesList, HttpStatus.OK);
    }

    @GetMapping("/get-info-sunset-hour/{hour}")
    public ResponseEntity<List<Coordinates>> getCoordinatesInfoBySunsetStartingHour(@PathVariable int hour) {
        logger.info("GET endpoint /coordinates/get-info-sunset-hour/{hour} was called.");

        List<Coordinates> coordinatesList = coordinatesService.getCoordinatesInfoBySunsetStartingHour(hour);

        logger.info(GET_SUCCESS_MESSAGE);
        return new ResponseEntity<>(coordinatesList, HttpStatus.OK);
    }

    @GetMapping("/get-users/{id}")
    public ResponseEntity<Page<User>> getUsersFromCoordinates(@PathVariable long id,
                                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("GET endpoint /coordinates/get-users/{id} was called.");

        Page<User> userPage = coordinatesService.getUsersFromCoordinates(id, pageNumber, pageSize);

        logger.info("Coordinate's users information was retrieved successfully.");
        return new ResponseEntity<>(userPage, HttpStatus.OK);
    }

    @GetMapping("/get-all-coordinates-info")
    public ResponseEntity<Page<CoordinatesDto>> getAllCoordinateInfo(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("GET endpoint /coordinates/get-all-coordinates-info was called.");

        Page<CoordinatesDto> coordinatesDtoPage = coordinatesService.getAllCoordinatesInfo(pageNumber, pageSize);

        logger.info("All coordinates information was retrieved successfully.");
        return new ResponseEntity<>(coordinatesDtoPage, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> addCoordinatesInfo(@RequestBody RequestDto requestDto) {
        logger.info("POST endpoint /coordinates/create was called.");

        coordinatesService.createCoordinatesInfo(requestDto);

        logger.info("Coordinates information was created successfully.");
        return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);
    }

    @PostMapping("/create-bulk")
    public ResponseEntity<String> addCoordinatesInfoBulk(@RequestBody List<RequestDto> requestDtoList) {
        logger.info("POST endpoint /coordinates/create-bulk was called.");

        coordinatesService.createCoordinatesInfoBulk(requestDtoList);

        logger.info("Coordinates information array was created successfully.");
        return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Coordinates> updateCoordinatesInfo(@PathVariable Long id,
                                                             @RequestBody CoordinatesDto updateDto) {
        logger.info("PUT endpoint /coordinates/update/{id} was called.");

        Coordinates coordinates = coordinatesService.updateCoordinatesInfo(id, updateDto);

        logger.info("Coordinates information was updated successfully.");
        return new ResponseEntity<>(coordinates, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoordinatesInfo(@PathVariable Long id) {
        logger.info("DELETE endpoint /coordinates/delete/{id} was called.");

        coordinatesService.deleteCoordinatesInfoFromDatabase(id);

        logger.info("Coordinates information was deleted successfully.");
        return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
    }
}
