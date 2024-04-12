package org.lab1java.sunsetsunriseapi.service;

import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CoordinatesRepository;
import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.exception.BadRequestErrorException;
import org.lab1java.sunsetsunriseapi.exception.InvalidDataException;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class that provides operations related to users.
 */
@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  private final CoordinatesRepository coordinatesRepository;

  private final EntityCache<Integer, Object> cacheMap;
  private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
  private static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists!";

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user entity
   * @throws ResourceNotFoundException if the user with the given ID is not found
   */
  public User getUserById(int id) {
    Object cachedData = cacheMap.get(Objects.hash(id, 2 * 31));

    if (cachedData != null) {
      return (User) cachedData;
    } else {
      User user = userRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
      cacheMap.put(Objects.hash(id, 2 * 31), user);

      return user;
    }
  }

  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user to retrieve
   * @return the user entity
   * @throws ResourceNotFoundException if the user with the given email address is not found
   */
  public User getUserByEmail(String email) {
    Object cachedData = cacheMap.get(Objects.hash(email, 3 * 32));

    if (cachedData != null) {
      return (User) cachedData;
    } else {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
      cacheMap.put(Objects.hash(email, 3 * 32), user);

      return user;
    }
  }

  /**
   * Retrieves a user by their nickname.
   *
   * @param nickname the nickname of the user to retrieve
   * @return the user entity
   * @throws ResourceNotFoundException if the user with the given nickname is not found
   */
  public User getUserByNickname(String nickname) {
    int hashCode = Objects.hash(nickname, 4 * 33);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (User) cachedData;
    } else {
      User user = userRepository.findByNickname(nickname)
          .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
      cacheMap.put(hashCode, user);

      return user;
    }
  }

  /**
   * Retrieves a page of coordinates associated with a user by their nickname.
   *
   * @param userId     the nickname of the user
   * @param pageNumber the page number (zero-based) of the result set to retrieve
   * @param pageSize   the number of items per page
   * @return a page of coordinates associated with the user
   * @throws ResourceNotFoundException if the user with the given nickname is not found
   */
  public Page<Coordinates> getUserCoordinatesListById(int userId, Integer pageNumber,
                                                            Integer pageSize) {
    if (pageNumber == null || pageNumber < 0) {
      pageNumber = 0;
    }

    if (pageSize == null || pageSize < 1) {
      pageSize = 10;
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
    return coordinatesRepository.findByUserSetContaining(user, pageable);
  }

  /**
   * Retrieves a page of all users.
   *
   * @param pageNumber the page number (zero-based) of the result set to retrieve
   * @param pageSize   the number of items per page
   * @return a page of all users
   */
  public Page<User> getAllUsers(Integer pageNumber, Integer pageSize) {
    int hashCode = Objects.hash(pageNumber, pageSize, 6 * 35);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (Page<User>) cachedData;
    } else {
      if (pageNumber == null || pageNumber < 0) {
        pageNumber = 0;
      }

      if (pageSize == null || pageSize < 1) {
        pageSize = 10;
      }
      Page<User> userPage = userRepository.findAll(PageRequest.of(pageNumber, pageSize));

      cacheMap.put(hashCode, userPage);
      return userPage;
    }
  }

  /**
   * Creates a new user.
   *
   * @param userDto the user data to create
   * @throws InvalidDataException     if the user data is invalid
   * @throws BadRequestErrorException if the user already exists
   */
  public void createUser(UserDto userDto) {
    if (userDto.getEmail() == null || userDto.getNickname() == null) {
      throw new InvalidDataException("Invalid user data!");
    }

    try {
      User user = new User(userDto.getEmail(), userDto.getNickname(), userDto.getPassword());

      cacheMap.clear();
      userRepository.save(user);
    } catch (Exception e) {
      throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
    }
  }

  /**
   * Creates users in bulk based on the provided list of UserDto objects.
   *
   * @param userDtoList The list of UserDto objects representing the users to be created.
   * @throws ResourceNotFoundException If the userDtoList is null or empty.
   * @throws IllegalArgumentException  If errors occur during bulk creation.
   */
  public void createUsersBulk(List<UserDto> userDtoList) {
    if (userDtoList == null || userDtoList.isEmpty()) {
      throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE);
    }

    List<String> errors = userDtoList.stream()
        .map(request -> {
          try {
            createUser(request);
            return null;
          } catch (Exception e) {
            return e.getMessage();
          }
        })
        .filter(Objects::nonNull)
        .toList();

    cacheMap.clear();
    if (!errors.isEmpty()) {
      throw new IllegalArgumentException(
          "Errors occurred during bulk creation: " + String.join("   ||||   ", errors));
    }
  }

  /**
   * Updates a user with the specified ID using the provided UserDto object.
   *
   * @param id        The ID of the user to be updated.
   * @param updateDto The UserDto object containing the updated user information.
   * @return The updated User object.
   * @throws ResourceNotFoundException If the user with the specified ID is not found.
   * @throws BadRequestErrorException  If the updated user information is invalid
   *                                   or conflicts with existing data.
   */
  public User updateUserById(int id, UserDto updateDto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    try {
      formUserFromDto(updateDto, user);

      userRepository.save(user);
      cacheMap.clear();
      return user;
    } catch (Exception e) {
      throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
    }
  }

  /**
   * Updates a user with the specified email using the provided UserDto object.
   *
   * @param email     The email of the user to be updated.
   * @param updateDto The UserDto object containing the updated user information.
   * @return The updated User object.
   * @throws ResourceNotFoundException If the user with the specified email is not found.
   * @throws BadRequestErrorException  If the updated user information is invalid
   *                                   or conflicts with existing data.
   */
  public User updateUserByEmail(String email, UserDto updateDto) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    try {
      formUserFromDto(updateDto, user);

      userRepository.save(user);
      cacheMap.clear();
      return user;
    } catch (Exception e) {
      throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
    }
  }

  /**
   * Updates a user with the specified nickname using the provided UserDto object.
   *
   * @param nickname  The nickname of the user to be updated.
   * @param updateDto The UserDto object containing the updated user information.
   * @return The updated User object.
   * @throws ResourceNotFoundException If the user with the specified nickname is not found.
   * @throws BadRequestErrorException  If the updated user information is invalid
   *                                   or conflicts with existing data.
   */
  public User updateUserByNickname(String nickname, UserDto updateDto) {
    User user = userRepository.findByNickname(nickname)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    try {
      formUserFromDto(updateDto, user);

      userRepository.save(user);
      cacheMap.clear();
      return user;
    } catch (Exception e) {
      throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
    }
  }

  /**
   * Deletes a user from the database based on the specified ID.
   *
   * @param id The ID of the user to be deleted.
   * @throws ResourceNotFoundException If the user with the specified ID is not found.
   */
  public void deleteUserFromDatabaseById(int id) {
    if (userRepository.existsById(id)) {
      cacheMap.clear();
      userRepository.deleteById(id);
    } else {
      throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE);
    }
  }

  /**
   * Validates the login credentials provided by a user.
   *
   * @param nickname The nickname of the user attempting to log in.
   * @param password The password associated with the user's account.
   * @return The ID of the user if login is successful.
   * @throws ResourceNotFoundException If the provided nickname and password combination
   *                                   does not match any existing user in the repository.
   */
  public int checkLogin(String nickname, String password) {
    User user = userRepository.findByNicknameAndPassword(nickname, password)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    return user.getId();
  }

  /**
   * Deletes the coordinates information associated with a specific user.
   *
   * @param userId        The ID of the user whose coordinates information is to be deleted.
   * @param coordinatesId The ID of the coordinates information to be deleted.
   * @throws ResourceNotFoundException    If either the user or the coordinates information
   *                                       associated with the given IDs are not found.
   * @throws BadRequestErrorException      If the provided coordinates information does not
   *                                       belong to the specified user.
   */
  @Transactional
  public void deleteUsersCoordinatesInformation(int userId, long coordinatesId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    Coordinates coordinates = coordinatesRepository.findById(coordinatesId)
        .orElseThrow(() -> new ResourceNotFoundException("Coordinates info not found!"));
    if (user.getCoordinatesSet().contains(coordinates)) {
      user.removeCoordinates(coordinates);
      userRepository.save(user);
    } else {
      throw new BadRequestErrorException("bad request");
    }
  }

  private void formUserFromDto(UserDto updateDto, User user) {
    user.setNickname(updateDto.getNickname());
    user.setEmail(updateDto.getEmail());
    user.setPassword(updateDto.getPassword());
  }
}
