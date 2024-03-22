package org.lab1java.sunsetsunriseapi.service;

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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final CoordinatesRepository coordinatesRepository;

    private final EntityCache<Integer, Object> cacheMap;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists!";

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

    public Page<Coordinates> getUserCoordinatesListByNickname(String nickname, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));

        return coordinatesRepository.findByUserSetContaining(user, pageable);
    }

    public Page<User> getAllUsers(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        return userRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public void createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getNickname() == null) {
            throw new InvalidDataException("Invalid user data!");
        }
        
        try {
            User user = new User(userDto.getEmail(), userDto.getNickname());
            userRepository.save(user);
        } catch (Exception e) {
            throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
        }
    }

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

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Errors occurred during bulk creation: " + String.join("   ||||   ", errors));
        }
    }

    public User updateUserById(int id, UserDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        try {
            clearUserCache(user);

            user.setNickname(updateDto.getNickname());
            user.setEmail(updateDto.getEmail());

            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
        }
    }

    public User updateUserByEmail(String email, UserDto updateDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        try {
            clearUserCache(user);

            user.setNickname(updateDto.getNickname());
            user.setEmail(updateDto.getEmail());

            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
        }
    }

    public User updateUserByNickname(String nickname, UserDto updateDto) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
       try {
           clearUserCache(user);

           user.setNickname(updateDto.getNickname());
           user.setEmail(updateDto.getEmail());

           userRepository.save(user);
           return user;
       } catch (Exception e) {
           throw new BadRequestErrorException(USER_ALREADY_EXISTS_MESSAGE);
       }
    }

    public void deleteUserFromDatabaseById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        userRepository.deleteById(id);
    }

    private void clearUserCache(User user) {
        cacheMap.remove(Objects.hash(user.getId(), 2 * 31));
        cacheMap.remove(Objects.hash(user.getEmail(), 3 * 32));
        cacheMap.remove(Objects.hash(user.getNickname(), 4 * 33));
    }
}
