package org.lab1java.sunsetsunriseapi.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.IdDto;
import org.lab1java.sunsetsunriseapi.dto.LoginDto;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.RequestCounterService;
import org.lab1java.sunsetsunriseapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling user-related endpoints.
 */
@CrossOrigin(origins = {"http://localhost:3000"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowedHeaders = {"Authorization", "Content-Type"})
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
  private final UserService userService;
  private RequestCounterService counterService;
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
    counterService.requestIncrement();
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
    counterService.requestIncrement();
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
    counterService.requestIncrement();
    logger.info("GET endpoint /users/get-by-nickname/{nickname} was called.");

    User user = userService.getUserByNickname(nickname);

    logger.info(GET_SUCCESS_MESSAGE);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Retrieves a page of coordinates associated with a user.
   *
   * @param userId the nickname of the user
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of coordinates
   */
  @GetMapping("/get-coordinates/{userId}")
  public ResponseEntity<Page<Coordinates>> getUserCoordinatesList(@PathVariable int userId,
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    counterService.requestIncrement();
    logger.info("GET endpoint /users/get-coordinates/{nickname} was called.");

    Page<Coordinates> coordinatesPage =
        userService.getUserCoordinatesListById(userId, pageNumber, pageSize);

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
    counterService.requestIncrement();
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
    counterService.requestIncrement();
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
    counterService.requestIncrement();
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
    counterService.requestIncrement();
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
    counterService.requestIncrement();
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
    counterService.requestIncrement();
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
  public ResponseEntity<String> deleteUserById(@PathVariable int id) {
    counterService.requestIncrement();
    logger.info("DELETE endpoint /users/delete/{id} was called.");

    userService.deleteUserFromDatabaseById(id);

    logger.info("User information was deleted successfully.");
    return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
  }

  /**
   * Handles requests to delete coordinates information associated with a user.
   *
   * @param userId        The ID of the user whose coordinates information is to be deleted.
   * @param coordinatesId The ID of the coordinates information to be deleted.
   * @return ResponseEntity indicating successful deletion of coordinates information.
   */
  @DeleteMapping("delete-coordinates")
  public ResponseEntity<String> deleteUsersCoordinatesInfo(@RequestParam int userId,
                                                           @RequestParam long coordinatesId) {
    counterService.requestIncrement();
    logger.info("DELETE endpoint /users/delete-coordinates was called.");

    userService.deleteUsersCoordinatesInformation(userId, coordinatesId);

    logger.info("User's coordinates information was deleted successfully.");
    return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
  }

  /**
   * Handles user login requests.
   *
   * @param loginDto The DTO containing the user's login credentials (nickname and password).
   * @return ResponseEntity containing the ID of the logged-in user if successful.
   */
  @PostMapping("/login")
  public ResponseEntity<IdDto> userLogin(@RequestBody LoginDto loginDto) {
    counterService.requestIncrement();
    logger.info("POST endpoint /users/login was called.");

    IdDto id = new IdDto(userService.checkLogin(loginDto.getNickname(), loginDto.getPassword()));

    logger.info("User was logged successfully.");
    return new ResponseEntity<>(id, HttpStatus.OK);
  }
}
