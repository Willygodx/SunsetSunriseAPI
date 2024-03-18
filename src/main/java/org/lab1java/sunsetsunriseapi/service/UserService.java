package org.lab1java.sunsetsunriseapi.service;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final EntityCache<Integer, Object> cacheMap;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";

    public User getUserById(int id) {
        Object cachedData = cacheMap.get(Objects.hash(id, 2 * 31));

        if (cachedData != null) {
            return (User)cachedData;
        } else {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
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
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
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
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
            cacheMap.put(hashCode, user);

            return user;
        }
    }

    public Set<Country> getUserCountriesByNickname(String nickname) {
        int hashCode = Objects.hash(nickname, 6 * 35);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (Set<Country>) cachedData;
        } else {
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

            Set<Country> countrySet = user.getCountrySet();
            cacheMap.put(hashCode, countrySet);

            return countrySet;
        }
    }

    public Page<User> getAllUsers(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }
        if(pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        return userRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public void createUser(UserDto userDto) {
        User user = new User(userDto.getEmail(), userDto.getNickname());
        userRepository.save(user);
    }

    public User updateUserById(int id, UserDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public User updateUserByEmail(String email, UserDto updateDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public User updateUserByNickname(String nickname, UserDto updateDto) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public void deleteUserFromDatabaseById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        userRepository.deleteById(id);
    }

    private void clearUserCache(User user) {
        cacheMap.remove(Objects.hash(user.getId(), 2 * 31));
        cacheMap.remove(Objects.hash(user.getEmail(), 3 * 32));
        cacheMap.remove(Objects.hash(user.getNickname(), 4 * 33));
    }
}
