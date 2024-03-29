package org.lab1java.sunsetsunriseapi.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling user-related endpoints.
 */
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

  /**
   * Retrieves a user by ID.
   *
   * @param id the ID of the user to retrieve
   * @return ResponseEntity containing the user information
   */
  @GetMapping("/get-by-id/{id}")
  public ResponseEntity<User> getUserById(@PathVariable int id) {
    logger.info("GET endpoint /users/get-by-id/{id} was called.");

    User user = userService.getUserById(id);

    logger.info(GET_SUCCESS_MESSAGE);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Retrieves a user by email.
   *
   * @param email the email of the user to retrieve
   * @return ResponseEntity containing the user information
   */
  @GetMapping("/get-by-email/{email}")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    logger.info("GET endpoint /users/get-by-email/{email} was called.");

    User user = userService.getUserByEmail(email);

    logger.info(GET_SUCCESS_MESSAGE);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Retrieves a user by nickname.
   *
   * @param nickname the nickname of the user to retrieve
   * @return ResponseEntity containing the user information
   */
  @GetMapping("/get-by-nickname/{nickname}")
  public ResponseEntity<User> getUserByNickname(@PathVariable String nickname) {
    logger.info("GET endpoint /users/get-by-nickname/{nickname} was called.");

    User user = userService.getUserByNickname(nickname);

    logger.info(GET_SUCCESS_MESSAGE);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Retrieves a page of coordinates associated with a user.
   *
   * @param nickname the nickname of the user
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of coordinates
   */
  @GetMapping("/get-coordinates/{nickname}")
  public ResponseEntity<Page<Coordinates>> getUserCoordinatesList(@PathVariable String nickname,
                                                                  @RequestParam(defaultValue = "0")
                                                                  Integer pageNumber,
                                                                  @RequestParam(defaultValue = "10")
                                                                  Integer pageSize) {
    logger.info("GET endpoint /users/get-coordinates/{nickname} was called.");

    Page<Coordinates> coordinatesPage =
        userService.getUserCoordinatesListByNickname(nickname, pageNumber, pageSize);

    logger.info("User's coordinates list was retrieved successfully.");
    return new ResponseEntity<>(coordinatesPage, HttpStatus.OK);
  }

  /**
   * Retrieves a page of all users.
   *
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of users
   */
  @GetMapping("/get-all-users")
  public ResponseEntity<Page<User>> getAllUsers(
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    logger.info("GET endpoint /users/get-all-users was called.");

    Page<User> userPage = userService.getAllUsers(pageNumber, pageSize);

    logger.info("All users list was retrieved successfully.");
    return new ResponseEntity<>(userPage, HttpStatus.OK);
  }

  /**
   * Creates a new user.
   *
   * @param userDto the DTO containing the user information to be created
   * @return ResponseEntity indicating the success of the operation
   */
  @PostMapping("/create")
  public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
    logger.info("POST endpoint /users/create was called.");

    userService.createUser(userDto);

    logger.info("User information was created successfully.");
    return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);
  }

  /**
   * Creates multiple users in bulk.
   *
   * @param userDtoList the list of DTOs containing the user information to be created
   * @return ResponseEntity indicating the success of the operation
   */
  @PostMapping("/create-bulk")
  public ResponseEntity<String> createUsersBulk(@RequestBody List<UserDto> userDtoList) {
    logger.info("POST endpoint /users/create-bulk was called.");

    userService.createUsersBulk(userDtoList);

    logger.info("Users array was created successfully.");
    return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.OK);
  }

  /**
   * Updates a user by ID.
   *
   * @param id the ID of the user to update
   * @param updateDto the DTO containing the updated user information
   * @return ResponseEntity containing the updated user information
   */
  @PutMapping("/update-by-id/{id}")
  public ResponseEntity<User> updateUserById(@PathVariable int id,
                                             @RequestBody UserDto updateDto) {
    logger.info("PUT endpoint /users/update-by-id/{id} was called.");

    User user = userService.updateUserById(id, updateDto);

    logger.info(PUT_SUCCESS_MESSAGE);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Updates a user by email.
   *
   * @param email the email of the user to update
   * @param updateDto the DTO containing the updated user information
   * @return ResponseEntity containing the updated user information
   */
  @PutMapping("/update-by-email/{email}")
  public ResponseEntity<User> updateUserByEmail(@PathVariable String email,
                                                @RequestBody UserDto updateDto) {
    logger.info("PUT endpoint /users/update-by-email/{email} was called.");

    User user = userService.updateUserByEmail(email, updateDto);

    logger.info(PUT_SUCCESS_MESSAGE);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Updates a user by nickname.
   *
   * @param nickname the nickname of the user to update
   * @param updateDto the DTO containing the updated user information
   * @return ResponseEntity containing the updated user information
   */
  @PutMapping("/update-by-nickname/{nickname}")
  public ResponseEntity<User> updateUserByNickname(@PathVariable String nickname,
                                                   @RequestBody UserDto updateDto) {
    logger.info("PUT endpoint /users/update-by-nickname/{nickname} was called.");

    User user = userService.updateUserByNickname(nickname, updateDto);

    logger.info(PUT_SUCCESS_MESSAGE);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Deletes a user by ID.
   *
   * @param id the ID of the user to delete
   * @return ResponseEntity indicating the success of the operation
   */
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteTimeZoneById(@PathVariable int id) {
    logger.info("DELETE endpoint /users/delete/{id} was called.");

    userService.deleteUserFromDatabaseById(id);

    logger.info("User information was deleted successfully.");
    return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
  }
}
