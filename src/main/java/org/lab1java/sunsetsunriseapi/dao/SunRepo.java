package org.lab1java.sunsetsunriseapi.dao;

import org.lab1java.sunsetsunriseapi.entity.SunEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;


public interface SunRepo extends CrudRepository<SunEntity, Long> {

    Optional<SunEntity> findByLatitudeAndLongitudeAndDate(Double latitude, Double longitude, LocalDate date);
}
