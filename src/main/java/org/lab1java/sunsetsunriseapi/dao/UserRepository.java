package org.lab1java.sunsetsunriseapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findById(int id);
}
