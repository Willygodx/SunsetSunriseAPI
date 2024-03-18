package org.lab1java.sunsetsunriseapi.controller;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {

            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-by-email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {

            return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-by-nickname/{nickname}")
    public ResponseEntity<User> getUserByNickname(@PathVariable String nickname) {
        try {

            return new ResponseEntity<>(userService.getUserByNickname(nickname), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-countries/{nickname}")
    public ResponseEntity<Set<Country>> getUserCountrySet(@PathVariable String nickname) {
        try {

            return new ResponseEntity<>(userService.getUserCountriesByNickname(nickname), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        try {

            return new ResponseEntity<>(userService.getAllUsers(pageNumber, pageSize), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {

            userService.createUser(userDto);
            return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable int id,
                                               @RequestBody UserDto updateDto) {
        try {

            return new ResponseEntity<>(userService.updateUserById(id, updateDto), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-by-email/{email}")
    public ResponseEntity<User> updateUserByEmail(@PathVariable String email,
                                                  @RequestBody UserDto updateDto) {
        try {

            return new ResponseEntity<>(userService.updateUserByEmail(email, updateDto), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-by-nickname/{nickname}")
    public ResponseEntity<User> updateUserByNickname(@PathVariable String nickname,
                                                     @RequestBody UserDto updateDto) {
        try {

            return new ResponseEntity<>(userService.updateUserByNickname(nickname, updateDto), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteTimeZoneById(@PathVariable int id) {
        try {

            userService.deleteUserFromDatabaseById(id);
            return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
