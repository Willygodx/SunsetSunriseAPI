package org.lab1java.sunsetsunriseapi.dao;

import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeZoneRepository extends JpaRepository<TimeZone, Integer> {
    Optional<TimeZone> findByName(String name);
}
