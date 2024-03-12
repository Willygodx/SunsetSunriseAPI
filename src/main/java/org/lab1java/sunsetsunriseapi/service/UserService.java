package org.lab1java.sunsetsunriseapi.service;

import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElse(null);
    }

    public User createUser(UserDto userDto) {
        User user = new User(userDto.getEmail(), userDto.getNickname());
        return userRepository.save(user);
    }

    public User updateUser(int id, UserDto updateDto) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public void deleteUserFromDatabaseById(int id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error! There is no users with this id!", e);
        }
    }
}
