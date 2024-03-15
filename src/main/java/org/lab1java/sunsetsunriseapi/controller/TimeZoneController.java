package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.dto.TimeZoneDto;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.TimeZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@RestController
@RequestMapping("/time-zones")
public class TimeZoneController {
    private final TimeZoneService timeZoneService;
    private final Logger logger = LoggerFactory.getLogger(TimeZoneController.class);
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    public TimeZoneController(TimeZoneService timeZoneService) {
        this.timeZoneService = timeZoneService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TimeZone> getTimeZone(@PathVariable int id) {
        try {

            return new ResponseEntity<>(timeZoneService.getTimeZoneById(id), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-users")
    public ResponseEntity<Set<User>> getTimeZoneUsers(@RequestParam() String name) {
        try {

            return new ResponseEntity<>(timeZoneService.getTimeZoneUsers(name), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTimeZone(@RequestBody TimeZoneDto timeZoneDto) {
        try {

            timeZoneService.createTimeZone(timeZoneDto);
            return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TimeZone> updateTimeZone(@PathVariable int id,
                                                   @RequestBody TimeZoneDto timeZoneDto) {
        try {

            return new ResponseEntity<>(timeZoneService.updateTimeZone(id, timeZoneDto), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTimeZone(@PathVariable int id) {
        try {

            timeZoneService.deleteTimeZoneFromDatabase(id);
            return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
