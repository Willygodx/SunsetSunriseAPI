package org.lab1java.sunsetsunriseapi.service;

import org.lab1java.sunsetsunriseapi.dao.TimeZoneRepository;
import org.lab1java.sunsetsunriseapi.dto.TimeZoneDto;
import org.lab1java.sunsetsunriseapi.entity.TimeZone;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TimeZoneService {
    private final TimeZoneRepository timeZoneRepository;

    public TimeZoneService(TimeZoneRepository timeZoneRepository) {
        this.timeZoneRepository = timeZoneRepository;
    }

    public TimeZone getTimeZoneById(int id) {
        return timeZoneRepository.findById(id).orElse(null);
    }

    public Set<User> getTimeZoneUsers(String timeZoneName) throws Exception {
        TimeZone timeZone = timeZoneRepository.findByName(timeZoneName)
                .orElseThrow(() -> new Exception("Time zone not found"));
        return timeZone.getUserSet();
    }

    public TimeZone createTimeZone(TimeZoneDto timeZoneDto) {
        TimeZone timeZone = new TimeZone(timeZoneDto.getTimeZone());
        return timeZoneRepository.save(timeZone);
    }

    public TimeZone updateTimeZone(int id, TimeZoneDto updateDto) throws Exception{
        TimeZone timeZone = timeZoneRepository.findById(id)
                .orElseThrow(() -> new Exception("Time one not found"));

        timeZone.setName(updateDto.getTimeZone());
        timeZoneRepository.save(timeZone);

        return timeZone;
    }

    public void deleteTimeZoneFromDatabase(int id) {
        if (timeZoneRepository.existsById(id)) {
            timeZoneRepository.deleteById(id);
        }
    }
}

