package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.lab1java.sunsetsunriseapi.service.SunService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/sunset-sunrise")
public class SunController {

    private final SunService sunService;
    private final Logger logger = LoggerFactory.getLogger(SunController.class);

    public SunController(SunService sunService) {
        this.sunService = sunService;
    }

    @GetMapping("/get-info")
    public SunResponseDto getSunInfo(@RequestParam() Double latitude,
                                     @RequestParam() Double longitude,
                                     @RequestParam() String date) {
        try {
            return sunService.getSunInfo(new SunRequestDto(latitude, longitude, LocalDate.parse(date)));
        } catch (Exception e) {
            logger.error("Error while making GET-request", e);
        }
        return null;
    }
}
