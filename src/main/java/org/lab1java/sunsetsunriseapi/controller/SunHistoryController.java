package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dto.SunHistoryDto;
import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.lab1java.sunsetsunriseapi.service.SunHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/sunset-sunrise")
public class SunHistoryController {
    private final SunHistoryService sunHistoryService;
    private final Logger logger = LoggerFactory.getLogger(SunHistoryController.class);
    private final EntityCache<Integer, Object> cacheMap;
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    public SunHistoryController(SunHistoryService sunHistoryService, EntityCache<Integer, Object> cacheMap) {
        this.sunHistoryService = sunHistoryService;
        this.cacheMap = cacheMap;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SunResponseDto> getSunHistory(@PathVariable int id,
                                                        @RequestParam() Double latitude,
                                                        @RequestParam() Double longitude,
                                                        @RequestParam() String date) {
        try {
            return ResponseEntity.ok(sunHistoryService.getSunInfo(id, (new SunRequestDto(latitude, longitude, LocalDate.parse(date)))));

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-from-db")
    public ResponseEntity<List<SunHistory>> getSunHistoryFromDatabase(@RequestParam() Double latitude,
                                                                    @RequestParam() Double longitude,
                                                                    @RequestParam() String date) {
        try {
            int hashCode = Objects.hash(latitude, longitude, date, 10 * 39);
            Object cachedData = cacheMap.get(hashCode);

            if (cachedData != null) {
                return ResponseEntity.ok((List<SunHistory>) cachedData);
            } else {
                List<SunHistory> sunHistoryList = sunHistoryService.getSunInfoFromDatabase(latitude, longitude, LocalDate.parse(date));
                cacheMap.put(hashCode, sunHistoryList);

                return ResponseEntity.ok(sunHistoryList);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/custom-get")
    public ResponseEntity<List<SunHistory>> getSunHistoryByCountryStartingWith(@RequestParam("prefix") String prefix) {
        try {
            int hashCode = Objects.hash(prefix, 11 * 40);
            Object cachedData = cacheMap.get(hashCode);

            if (cachedData != null) {
                return ResponseEntity.ok((List<SunHistory>) cachedData);

            } else {
                List<SunHistory> sunHistoryList = sunHistoryService.findSunHistoryByCountryStartingWithPrefix(prefix);
                cacheMap.put(hashCode, sunHistoryList);

                return ResponseEntity.ok(sunHistoryList);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addSunInfo(@RequestBody SunHistoryDto sunHistoryDto) {
        try {
            sunHistoryService.createSunHistory(sunHistoryDto);
            return ResponseEntity.ok(CREATE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().body(CREATE_ERROR_MESSAGE);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SunHistoryDto> updateSunInfo(@PathVariable Long id,
                                                       @RequestBody SunHistoryDto updateDto) {
        try {
            return ResponseEntity.ok(sunHistoryService.updateSunInfo(id, updateDto));
        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSunInfo(@PathVariable Long id) {
        try {
            sunHistoryService.deleteSunInfoFromDatabase(id);
            return ResponseEntity.ok(DELETE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().body(DELETE_ERROR_MESSAGE);
        }
    }
}
