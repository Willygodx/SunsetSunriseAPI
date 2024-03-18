package org.lab1java.sunsetsunriseapi.controller;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.CoordinatesDto;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.service.CoordinatesService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/coordinates")
@AllArgsConstructor
public class CoordinatesController {
    private final CoordinatesService coordinatesService;
    private final Logger logger = LoggerFactory.getLogger(CoordinatesController.class);
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    @GetMapping("/get-info/{userId}")
    public ResponseEntity<ResponseDto> getCoordinatesInfo(@PathVariable int userId,
                                                          @RequestParam() Double latitude,
                                                          @RequestParam() Double longitude,
                                                          @RequestParam() String date) {
        try {

            RequestDto request = new RequestDto(latitude, longitude, LocalDate.parse(date));
            return new ResponseEntity<>(coordinatesService.getCoordinatesInfo(userId, request), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-info-sunrise-hour")
    public ResponseEntity<List<Coordinates>> getCoordinatesInfoBySunriseStartingHour(@RequestParam int hour) {
        try {

            return new ResponseEntity<>(coordinatesService.getCoordinatesInfoBySunriseStartingHour(hour), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-info-sunset-hour")
    public ResponseEntity<List<Coordinates>> getCoordinatesInfoBySunsetStartingHour(@RequestParam int hour) {
        try {

            return new ResponseEntity<>(coordinatesService.getCoordinatesInfoBySunsetStartingHour(hour), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all-coordinates-info")
    public ResponseEntity<Page<CoordinatesDto>> getAllCoordinateInfo(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        try {

            return new ResponseEntity<>(coordinatesService.getAllCoordinatesInfo(pageNumber, pageSize), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> addCoordinatesInfo(@RequestBody RequestDto requestDto) {
        try {

            coordinatesService.createCoordinatesInfo(requestDto);
            return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(CREATE_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Coordinates> updateCoordinatesInfo(@PathVariable Long id,
                                                             @RequestBody CoordinatesDto updateDto) {
        try {

            return new ResponseEntity<>(coordinatesService.updateCoordinatesInfo(id, updateDto), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoordinatesInfo(@PathVariable Long id) {
        try {

            coordinatesService.deleteCoordinatesInfoFromDatabase(id);
            return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(DELETE_ERROR_MESSAGE, HttpStatus.NOT_FOUND);
        }
    }
}
