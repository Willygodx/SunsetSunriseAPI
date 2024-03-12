package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String DELETE_ERROR_MESSAGE = "Error deleting user!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-info-by-id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            logger.error("Error while getting user by id!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-info-by-email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        } catch (Exception e) {
            logger.error("Error while getting user by email!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-info-by-nickname/{nickname}")
    public ResponseEntity<User> getUserByNickname(@PathVariable String nickname) {
        try {
            return ResponseEntity.ok(userService.getUserByNickname(nickname));
        } catch (Exception e) {
            logger.error("Error while getting user by id!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.createUser(userDto));
        } catch (Exception e) {
            logger.error("Error while creating user!", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id,
                                           @RequestBody UserDto updateDto) {
        try {
            User updatedUser = userService.updateUser(id, updateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error while updating sun info", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete-user/{id}")
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
