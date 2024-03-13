package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dto.TimeZoneDto;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.TimeZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/time-zone")
public class TimeZoneController {
    private final TimeZoneService timeZoneService;
    private final Logger logger = LoggerFactory.getLogger(TimeZoneController.class);
    private final EntityCache<Integer, Object> cacheMap;
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    public TimeZoneController(TimeZoneService timeZoneService, EntityCache<Integer, Object> cacheMap) {
        this.timeZoneService = timeZoneService;
        this.cacheMap = cacheMap;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TimeZone> getTimeZone(@PathVariable int id) {
        try {
            int hashCode = Objects.hash(id, 7 * 36);
            Object cachedData = cacheMap.get(hashCode);

            if (cachedData != null) {
                return ResponseEntity.ok((TimeZone) cachedData);
            } else {
                TimeZone timeZone = timeZoneService.getTimeZoneById(id);
                cacheMap.put(hashCode, timeZone);

                return ResponseEntity.ok(timeZone);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-users")
    public ResponseEntity<Set<User>> getTimeZoneUsers(@RequestParam() String name) {
        try {
            int hashCode = Objects.hash(name, 8 * 37);
            Object cachedData = cacheMap.get(hashCode);

            if (cachedData != null) {
                return ResponseEntity.ok((Set<User>) cachedData);
            } else {
                Set<User> userSet = timeZoneService.getTimeZoneUsers(name);
                cacheMap.put(hashCode, userSet);

                return ResponseEntity.ok(userSet);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTimeZone(@RequestBody TimeZoneDto timeZoneDto) {
        try {
            timeZoneService.createTimeZone(timeZoneDto);
            return ResponseEntity.ok(CREATE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().body(CREATE_ERROR_MESSAGE);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TimeZone> updateTimeZone(@PathVariable int id,
                                                   @RequestBody TimeZoneDto timeZoneDto) {
        try {
            return ResponseEntity.ok(timeZoneService.updateTimeZone(id, timeZoneDto));
        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTimeZone(@PathVariable int id) {
        try {
            timeZoneService.deleteTimeZoneFromDatabase(id);
            return ResponseEntity.ok(DELETE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().body(DELETE_ERROR_MESSAGE);
        }
    }
}
