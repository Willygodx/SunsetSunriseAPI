package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.dto.TimeZoneDto;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.TimeZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@RestController
@RequestMapping("/time-zone")
public class TimeZoneController {
    private final TimeZoneService timeZoneService;
    private final Logger logger = LoggerFactory.getLogger(TimeZoneController.class);

    public TimeZoneController(TimeZoneService timeZoneService) {
        this.timeZoneService = timeZoneService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TimeZone> getTimeZone(@PathVariable int id) {
        try {
            return ResponseEntity.ok(timeZoneService.getTimeZoneById(id));
        } catch (Exception e) {
            logger.error("Error while getting time zone!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-users")
    public ResponseEntity<Set<User>> getTimeZoneUsers(@RequestParam() String name) {
        try {
            return ResponseEntity.ok(timeZoneService.getTimeZoneUsers(name));
        } catch (Exception e) {
            logger.error("Error while getting time zone!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTimeZone(@RequestBody TimeZoneDto timeZoneDto) {
        try {
            timeZoneService.createTimeZone(timeZoneDto);
            return ResponseEntity.ok("Time zone was added successfully!");
        } catch (Exception e) {
            logger.error("Error while creating time zone!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TimeZone> updateTimeZone(@PathVariable int id,
                                                   @RequestBody TimeZoneDto timeZoneDto) {
        try {
            return ResponseEntity.ok(timeZoneService.updateTimeZone(id, timeZoneDto));
        } catch (Exception e) {
            logger.error("Error while updating time zone!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTimeZone(@PathVariable int id) {
        try {
            timeZoneService.deleteTimeZoneFromDatabase(id);
            return ResponseEntity.ok("Deleted successfully!");
        } catch (Exception e) {
            logger.error("Error while deleting time zone", e);
            return ResponseEntity.badRequest().body("Error deleting time zone");
        }
    }
}
