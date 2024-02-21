package org.lab1java.sunsetsunriseapi.service;

import org.lab1java.sunsetsunriseapi.dao.SunRepo;
import org.lab1java.sunsetsunriseapi.entity.SunEntity;
import org.lab1java.sunsetsunriseapi.model.SunInfoRequest;
import org.lab1java.sunsetsunriseapi.model.SunInfoResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SunService {

    private final ApiService externalApiService;

    private final SunRepo sunRepo;

    public SunService(ApiService externalApiService, SunRepo sunRepo) {
        this.externalApiService = externalApiService;
        this.sunRepo = sunRepo;
    }

    public SunEntity sunResponse(SunEntity sun) {
        return sunRepo.save(sun);
    }

    public SunInfoResponse getSunInfo(SunInfoRequest request) {

        Optional<SunEntity> optionalSunEntity = sunRepo.findByLatitudeAndLongitudeAndDate(request.getLatitude(), request.getLongitude(), request.getDate());
        if (optionalSunEntity.isPresent()) {
            SunEntity sunEntity = optionalSunEntity.get();
            return new SunInfoResponse(sunEntity.getSunrise(), sunEntity.getSunset());
        } else {
            String apiResponse = externalApiService.getApiResponse(request);
            SunInfoResponse sunInfoResponse = externalApiService.extractSunInfoFromApiResponse(apiResponse);

            SunEntity sunEntity = new SunEntity();
            sunEntity.setLatitude(request.getLatitude());
            sunEntity.setLongitude(request.getLongitude());
            sunEntity.setDate(request.getDate());
            sunEntity.setSunrise(sunInfoResponse.getSunrise());
            sunEntity.setSunset(sunInfoResponse.getSunset());
            sunRepo.save(sunEntity);

            return sunInfoResponse;
        }
    }
}
