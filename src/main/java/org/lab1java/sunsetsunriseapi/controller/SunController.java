package org.lab1java.sunsetsunriseapi.controller;

import org.lab1java.sunsetsunriseapi.model.SunInfoRequest;
import org.lab1java.sunsetsunriseapi.model.SunInfoResponse;
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
    public SunInfoResponse getSunInfo(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String date)  {

        return sunService.getSunInfo(new SunInfoRequest(latitude, longitude, LocalDate.parse(date)));
    }
}
