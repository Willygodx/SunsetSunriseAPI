package org.core.sunsetsunrise.dao;

import java.util.Optional;
import org.core.sunsetsunrise.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Country entities.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
  Optional<Country> findByName(String name);
}
