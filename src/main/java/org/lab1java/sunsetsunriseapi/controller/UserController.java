package org.lab1java.sunsetsunriseapi.controller;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";
    private static final String GET_SUCCESS_MESSAGE = "User information was retrieved successfully.";
    private static final String PUT_SUCCESS_MESSAGE = "User information was updated successfully.";

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        logger.info("GET endpoint /users/get-by-id/{id} was called.");

        User user = userService.getUserById(id);

        logger.info(GET_SUCCESS_MESSAGE);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get-by-email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.info("GET endpoint /users/get-by-email/{email} was called.");

        User user = userService.getUserByEmail(email);

        logger.info(GET_SUCCESS_MESSAGE);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get-by-nickname/{nickname}")
    public ResponseEntity<User> getUserByNickname(@PathVariable String nickname) {
        logger.info("GET endpoint /users/get-by-nickname/{nickname} was called.");

        User user = userService.getUserByNickname(nickname);

        logger.info(GET_SUCCESS_MESSAGE);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get-coordinates/{userName}")
    public ResponseEntity<Page<Coordinates>> getUserCoordinatesList(@PathVariable String userName,
                                                               @RequestParam(defaultValue = "0") Integer pageNumber,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("GET endpoint /users/get-coordinates/{userName} was called.");

        Page<Coordinates> coordinatesPage = userService.getUserCoordinatesListByNickname(userName, pageNumber, pageSize);

        logger.info("User's coordinates list was retrieved successfully.");
        return new ResponseEntity<>(coordinatesPage, HttpStatus.OK);
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("GET endpoint /users/get-all-users was called.");

        Page<User> userPage = userService.getAllUsers(pageNumber, pageSize);

        logger.info("All users list was retrieved successfully.");
        return new ResponseEntity<>(userPage, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        logger.info("POST endpoint /users/create was called.");

        userService.createUser(userDto);

        logger.info("User information was created successfully.");
        return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable int id,
                                               @RequestBody UserDto updateDto) {
        logger.info("PUT endpoint /users/update-by-id/{id} was called.");

        User user = userService.updateUserById(id, updateDto);

        logger.info(PUT_SUCCESS_MESSAGE);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/update-by-email/{email}")
    public ResponseEntity<User> updateUserByEmail(@PathVariable String email,
                                                  @RequestBody UserDto updateDto) {
        logger.info("PUT endpoint /users/update-by-email/{email} was called.");

        User user = userService.updateUserByEmail(email, updateDto);

        logger.info(PUT_SUCCESS_MESSAGE);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/update-by-nickname/{nickname}")
    public ResponseEntity<User> updateUserByNickname(@PathVariable String nickname,
                                                     @RequestBody UserDto updateDto) {
        logger.info("PUT endpoint /users/update-by-nickname/{nickname} was called.");

        User user = userService.updateUserByNickname(nickname, updateDto);

        logger.info(PUT_SUCCESS_MESSAGE);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTimeZoneById(@PathVariable int id) {
        logger.info("DELETE endpoint /users/delete/{id} was called.");

        userService.deleteUserFromDatabaseById(id);

        logger.info("User information was deleted successfully.");
        return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
    }
}
