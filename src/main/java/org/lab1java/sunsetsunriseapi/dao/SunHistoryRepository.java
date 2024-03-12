package org.lab1java.sunsetsunriseapi.dao;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.springframework.data.jpa.repository.*;

import java.time.LocalDate;
import java.util.Optional;

public interface SunHistoryRepository extends JpaRepository<SunHistory, Long>{
    Optional<SunHistory> findByLatitudeAndLongitudeAndDate(double latitude, double longitude, LocalDate date);
}
