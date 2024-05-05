package org.core.sunsetsunrise.dao;

import java.util.Optional;
import org.core.sunsetsunrise.entity.Coordinates;
import org.core.sunsetsunrise.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);

  Optional<User> findByNickname(String nickname);

  Optional<User> findById(int id);

  Page<User> findByCoordinatesSetContaining(Coordinates coordinates, Pageable pageable);

  Optional<User> findByNicknameAndPassword(String nickname, String password);
}
