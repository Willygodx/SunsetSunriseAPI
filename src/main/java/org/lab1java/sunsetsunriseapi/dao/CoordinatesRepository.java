package org.lab1java.sunsetsunriseapi.dao;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
    Optional<Coordinates> findByLatitudeAndLongitudeAndDate(double latitude, double longitude, LocalDate date);

    Page<Coordinates> findByCountry(Country country, PageRequest pageRequest);

    @Query("SELECT sh FROM Coordinates sh WHERE HOUR(sh.sunrise) = :hour")
    Page<Coordinates> findBySunriseStartingHour(int hour, Pageable pageable);

    @Query("SELECT sh FROM Coordinates sh WHERE HOUR(sh.sunset) = :hour")
    Page<Coordinates> findBySunsetStartingHour(int hour, Pageable pageable);

    Page<Coordinates> findByUserSetContaining(User user, Pageable pageable);
}
