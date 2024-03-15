package org.lab1java.sunsetsunriseapi.service;

import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EntityCache<Integer, Object> cacheMap;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";

    public UserService(UserRepository userRepository, EntityCache<Integer, Object> cacheMap) {
        this.userRepository = userRepository;
        this.cacheMap = cacheMap;
    }

    public User getUserById(int id) throws Exception {
        Object cachedData = cacheMap.get(Objects.hash(id, 2 * 31));

        if (cachedData != null) {
            return (User)cachedData;
        } else {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));
            cacheMap.put(Objects.hash(id, 2 * 31), user);

            return user;
        }
    }

    public User getUserByEmail(String email) throws Exception {
        Object cachedData = cacheMap.get(Objects.hash(email, 3 * 32));

        if (cachedData != null) {
            return (User) cachedData;
        } else {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));
            cacheMap.put(Objects.hash(email, 3 * 32), user);

            return user;
        }
    }

    public User getUserByNickname(String nickname) throws Exception {
        int hashCode = Objects.hash(nickname, 4 * 33);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (User) cachedData;
        } else {
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));
            cacheMap.put(hashCode, user);

            return user;
        }
    }

    public List<SunHistory> getUserSunHistoryByNickname(String nickname) throws Exception {
        int hashCode = Objects.hash(nickname, 5 * 34);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (List<SunHistory>) cachedData;
        } else {
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));

            List<SunHistory> sunHistoryList = user.getSunHistoryList();
            cacheMap.put(hashCode, sunHistoryList);

            return sunHistoryList;
        }
    }

    public Set<TimeZone> getUserTimeZoneByNickname(String nickname) throws Exception {
        int hashCode = Objects.hash(nickname, 6 * 35);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (Set<TimeZone>) cachedData;
        } else {
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));

            Set<TimeZone> timeZoneSet = user.getTimeZoneSet();
            cacheMap.put(hashCode, timeZoneSet);

            return timeZoneSet;
        }
    }

    public void createUser(UserDto userDto) {
        User user = new User(userDto.getEmail(), userDto.getNickname());
        userRepository.save(user);
    }

    public User updateUserById(int id, UserDto updateDto) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public User updateUserByEmail(String email, UserDto updateDto) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public User updateUserByNickname(String nickname, UserDto updateDto) throws Exception {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));
        clearUserCache(user);

        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public void deleteUserFromDatabaseById(int id) throws Exception {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new Exception(USER_NOT_FOUND_MESSAGE));
            clearUserCache(user);

            userRepository.deleteById(id);
        }
    }

    private void clearUserCache(User user) {
        cacheMap.remove(Objects.hash(user.getId(), 2 * 31));
        cacheMap.remove(Objects.hash(user.getEmail(), 3 * 32));
        cacheMap.remove(Objects.hash(user.getNickname(), 4 * 33));
    }
}
