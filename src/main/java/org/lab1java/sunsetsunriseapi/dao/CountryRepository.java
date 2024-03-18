package org.lab1java.sunsetsunriseapi.dao;

import org.lab1java.sunsetsunriseapi.entity.Country;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByName(String name);
}
