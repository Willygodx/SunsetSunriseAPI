package org.lab1java.sunsetsunriseapi.controller;

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
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String DELETE_ERROR_MESSAGE = "Error deleting user!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating sun info!";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            logger.error("Error while getting user by id!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-by-email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        } catch (Exception e) {
            logger.error("Error while getting user by email!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-by-nickname/{nickname}")
    public ResponseEntity<User> getUserByNickname(@PathVariable String nickname) {
        try {
            return ResponseEntity.ok(userService.getUserByNickname(nickname));
        } catch (Exception e) {
            logger.error("Error while getting user by id!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-history/{nickname}")
    public ResponseEntity<List<SunHistory>> getUserSunHistoryList(@PathVariable String nickname) {
        try {
            return ResponseEntity.ok(userService.getUserSunHistoryByNickname(nickname));
        } catch (Exception e) {
            logger.error("Error while getting user's sun history by nickname!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-time-zones/{nickname}")
    public ResponseEntity<Set<TimeZone>> getUserTimeZoneSet(@PathVariable String nickname) {
        try {
            return ResponseEntity.ok(userService.getUserTimeZoneByNickname(nickname));
        } catch (Exception e) {
            logger.error("Error while getting user's time zones by nickname!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {
            userService.createUser(userDto);
            return ResponseEntity.ok("User was added successfully!");
        } catch (Exception e) {
            logger.error("Error while creating user!", e);
            return ResponseEntity.badRequest().build();
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
            return ResponseEntity.badRequest().body(null);
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
            return ResponseEntity.badRequest().body(null);
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
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteTimeZoneById(@PathVariable int id) {
        try {
            userService.deleteUserFromDatabaseById(id);
            return ResponseEntity.ok(DELETE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error("Error while deleting user by id!", e);
            return ResponseEntity.badRequest().body(DELETE_ERROR_MESSAGE);
        }
    }
}
