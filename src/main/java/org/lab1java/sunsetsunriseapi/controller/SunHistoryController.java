package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.dto.SunHistoryDto;
import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.lab1java.sunsetsunriseapi.service.SunHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/sunset-sunrise")
public class SunHistoryController {
    private final SunHistoryService sunHistoryService;
    private final Logger logger = LoggerFactory.getLogger(SunHistoryController.class);

    public SunHistoryController(SunHistoryService sunHistoryService) {
        this.sunHistoryService = sunHistoryService;
    }

    @GetMapping("/get-info")
    public ResponseEntity<SunResponseDto> getSunHistory(@RequestParam() Double latitude,
                                                        @RequestParam() Double longitude,
                                                        @RequestParam() String date) {
        try {
            return ResponseEntity.ok(sunHistoryService.getSunInfo(new SunRequestDto(latitude, longitude, LocalDate.parse(date))));
        } catch (Exception e) {
            logger.error("Error while getting sun info!", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/get-info-from-db")
    public ResponseEntity<SunResponseDto> getSunHistoryFromDatabase(@RequestParam() Double latitude,
                                                                    @RequestParam() Double longitude,
                                                                    @RequestParam() String date) {
        try {
            return ResponseEntity.ok(sunHistoryService.getSunInfoFromDatabase(latitude, longitude, LocalDate.parse(date)));
        } catch (Exception e) {
            logger.error("Error while getting sun info from database!", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/add-info")
    public ResponseEntity<SunHistory> addSunInfo(@RequestBody SunHistoryDto sunHistoryDto) {
        try {
            return ResponseEntity.ok(sunHistoryService.createSunHistory(sunHistoryDto));
        } catch (Exception e) {
            logger.error("Error while adding sun info!", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/update-info/{id}")
    public ResponseEntity<SunHistoryDto> updateSunInfo(@PathVariable Long id,
                                                       @RequestBody SunHistoryDto updateDto) {
        try {
            SunHistoryDto updatedSunHistory = sunHistoryService.updateSunInfo(id, updateDto);
            return ResponseEntity.ok(updatedSunHistory);
        } catch (Exception e) {
            logger.error("Error while updating sun info", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete-info/{id}")
    public ResponseEntity<String> deleteSunInfo(@PathVariable Long id) {
        try {
            sunHistoryService.deleteSunInfoFromDatabase(id);
            return ResponseEntity.ok("Deleted successfully!");
        } catch (Exception e) {
            logger.error("Error while deleting sun info", e);
            return ResponseEntity.badRequest().body("Error deleting sun info");
        }
    }
}