package org.lab1java.sunsetsunriseapi.service;

import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.TimeZoneRepository;
import org.lab1java.sunsetsunriseapi.dto.TimeZoneDto;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class TimeZoneService {
    private final TimeZoneRepository timeZoneRepository;
    private final EntityCache<Integer, Object> cacheMap;
    private static final String TIME_ZONE_NOT_FOUND_MESSAGE = "Time zone not found!";

    public TimeZoneService(TimeZoneRepository timeZoneRepository, EntityCache<Integer, Object> cacheMap) {
        this.timeZoneRepository = timeZoneRepository;
        this.cacheMap = cacheMap;
    }

    public TimeZone getTimeZoneById(int id) {
        int hashCode = Objects.hash(id, 30 * 31);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (TimeZone) cachedData;
        } else {
            TimeZone timeZone = timeZoneRepository.findById(id).orElse(null);
            cacheMap.put(hashCode, timeZone);

            return timeZone;
        }
    }

    public Set<User> getTimeZoneUsers(String timeZoneName) throws Exception {
        int hashCode = Objects.hash(timeZoneName, 31 * 32);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (Set<User>) cachedData;
        } else {
            TimeZone timeZone = timeZoneRepository.findByName(timeZoneName)
                    .orElseThrow(() -> new Exception(TIME_ZONE_NOT_FOUND_MESSAGE));
            cacheMap.put(hashCode, timeZone.getUserSet());

            return timeZone.getUserSet();
        }
    }

    public void createTimeZone(TimeZoneDto timeZoneDto) {
        TimeZone timeZone = new TimeZone(timeZoneDto.getTimeZone());
        timeZoneRepository.save(timeZone);
    }

    public TimeZone updateTimeZone(int id, TimeZoneDto updateDto) throws Exception{
        TimeZone timeZone = timeZoneRepository.findById(id)
                .orElseThrow(() -> new Exception(TIME_ZONE_NOT_FOUND_MESSAGE));
        cacheMap.remove(Objects.hash(id, 30 * 31));

        timeZone.setName(updateDto.getTimeZone());
        timeZoneRepository.save(timeZone);

        return timeZone;
    }

    public void deleteTimeZoneFromDatabase(int id) {
        if (timeZoneRepository.existsById(id)) {
            cacheMap.remove(Objects.hash(id, 30 * 31));
            timeZoneRepository.deleteById(id);
        }
    }
}

