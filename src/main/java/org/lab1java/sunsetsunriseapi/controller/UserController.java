package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final EntityCache<Integer, Object> cacheMap;
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    public UserController(UserService userService, EntityCache<Integer, Object> cacheMap) {
        this.userService = userService;
        this.cacheMap = cacheMap;
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            Object cachedData = cacheMap.get(Objects.hash(id, 2 * 31));

            if (cachedData != null) {
                return ResponseEntity.ok((User)cachedData);
            } else {
                User user = userService.getUserById(id);
                cacheMap.put(Objects.hash(id), user);

                return ResponseEntity.ok(user);
            }

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-by-email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            Object cachedData = cacheMap.get(Objects.hash(email, 3 * 32));

            if (cachedData != null) {
                return ResponseEntity.ok((User) cachedData);
            } else {
                User user = userService.getUserByEmail(email);
                cacheMap.put(Objects.hash(email), user);

                return ResponseEntity.ok(user);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-by-nickname/{nickname}")
    public ResponseEntity<User> getUserByNickname(@PathVariable String nickname) {
        try {
            int hashCode = Objects.hash(nickname, 4 * 33);
            Object cachedData = cacheMap.get(hashCode);

            if (cachedData != null) {
                return ResponseEntity.ok((User) cachedData);
            } else {
                User user = userService.getUserByNickname(nickname);
                cacheMap.put(hashCode, user);

                return ResponseEntity.ok(user);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-history/{nickname}")
    public ResponseEntity<List<SunHistory>> getUserSunHistoryList(@PathVariable String nickname) {
        try {
            int hashCode = Objects.hash(nickname, 5 * 34);
            Object cachedData = cacheMap.get(hashCode);

            if (cachedData != null) {
                return ResponseEntity.ok((List<SunHistory>) cachedData);
            } else {
                List<SunHistory> sunHistoryList = userService.getUserSunHistoryByNickname(nickname);
                cacheMap.put(hashCode, sunHistoryList);

                return ResponseEntity.ok(sunHistoryList);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-time-zones/{nickname}")
    public ResponseEntity<Set<TimeZone>> getUserTimeZoneSet(@PathVariable String nickname) {
        try {
            int hashCode = Objects.hash(nickname, 6 * 35);
            Object cachedData = cacheMap.get(hashCode);

            if (cachedData != null) {
                return ResponseEntity.ok((Set<TimeZone>) cachedData);
            } else {
                Set<TimeZone> timeZoneSet = userService.getUserTimeZoneByNickname(nickname);
                cacheMap.put(hashCode, timeZoneSet);

                return ResponseEntity.ok(timeZoneSet);
            }
        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {
            userService.createUser(userDto);
            return ResponseEntity.ok(CREATE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().body(CREATE_ERROR_MESSAGE);
        }
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable int id,
                                               @RequestBody UserDto updateDto) {
        try {
            User updatedUser = userService.updateUserById(id, updateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update-by-email/{email}")
    public ResponseEntity<User> updateUserByEmail(@PathVariable String email,
                                                  @RequestBody UserDto updateDto) {
        try {
            User updatedUser = userService.updateUserByEmail(email, updateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update-by-nickname/{nickname}")
    public ResponseEntity<User> updateUserByNickname(@PathVariable String nickname,
                                                     @RequestBody UserDto updateDto) {
        try {
            User updatedUser = userService.updateUserByNickname(nickname, updateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteTimeZoneById(@PathVariable int id) {
        try {
            userService.deleteUserFromDatabaseById(id);
            return ResponseEntity.ok(DELETE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return ResponseEntity.badRequest().body(DELETE_ERROR_MESSAGE);
        }
    }
}
