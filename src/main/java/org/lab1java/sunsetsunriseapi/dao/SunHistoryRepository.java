package org.lab1java.sunsetsunriseapi.dao;
import org.lab1java.sunsetsunriseapi.entity.SunHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SunHistoryRepository extends JpaRepository<SunHistory, Long>{
    List<SunHistory> findByLatitudeAndLongitudeAndDate(double latitude, double longitude, LocalDate date);
    @Query("SELECT s FROM SunHistory s WHERE s.country LIKE :prefix%")
    List<SunHistory> findByCountryStartingWith(String prefix);
}
