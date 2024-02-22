package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.dto.SunRequestDto;
import org.lab1java.sunsetsunriseapi.dto.SunResponseDto;
import org.lab1java.sunsetsunriseapi.service.SunService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/sunset-sunrise")
public class SunController {

    private final SunService sunService;

    public SunController(SunService sunService) {
        this.sunService = sunService;
    }

    @GetMapping("/get-info")
    public SunResponseDto getSunInfo(@RequestParam() Double latitude,
                                     @RequestParam() Double longitude,
                                     @RequestParam() String date) {

        return sunService.getSunInfo(new SunRequestDto(latitude, longitude, LocalDate.parse(date)));
    }
}
