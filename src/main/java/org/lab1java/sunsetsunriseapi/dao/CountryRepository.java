package org.lab1java.sunsetsunriseapi.dao;

import java.util.Optional;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Country entities.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
  Optional<Country> findByName(String name);
}
