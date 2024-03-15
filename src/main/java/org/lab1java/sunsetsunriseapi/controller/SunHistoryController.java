package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.dto.SunHistoryDto;
import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.lab1java.sunsetsunriseapi.service.SunHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/history")
public class SunHistoryController {
    private final SunHistoryService sunHistoryService;
    private final Logger logger = LoggerFactory.getLogger(SunHistoryController.class);
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    public SunHistoryController(SunHistoryService sunHistoryService) {
        this.sunHistoryService = sunHistoryService;
    }

    @GetMapping("/get-info/{id}")
    public ResponseEntity<SunResponseDto> getSunHistory(@PathVariable int id,
                                                        @RequestParam() Double latitude,
                                                        @RequestParam() Double longitude,
                                                        @RequestParam() String date) {
        try {

            SunRequestDto request = new SunRequestDto(latitude, longitude, LocalDate.parse(date));
            return new ResponseEntity<>(sunHistoryService.getSunInfo(id, request), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-by-countries")
    public ResponseEntity<List<SunHistory>> getSunHistoryByCountryStartingWith(@RequestParam("prefix") String prefix) {
        try {

            return new ResponseEntity<>(sunHistoryService.findSunHistoryByCountryStartingWithPrefix(prefix), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addSunInfo(@RequestBody SunHistoryDto sunHistoryDto) {
        try {

            sunHistoryService.createSunHistory(sunHistoryDto);
            return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(CREATE_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SunHistoryDto> updateSunInfo(@PathVariable Long id,
                                                       @RequestBody SunHistoryDto updateDto) {
        try {

            return new ResponseEntity<>(sunHistoryService.updateSunInfo(id, updateDto), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSunInfo(@PathVariable Long id) {
        try {

            sunHistoryService.deleteSunInfoFromDatabase(id);
            return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(DELETE_ERROR_MESSAGE, HttpStatus.NOT_FOUND);
        }
    }
}
