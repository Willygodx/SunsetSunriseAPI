package org.lab1java.sunsetsunriseapi.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lab1java.sunsetsunriseapi.dao.SunRepo;
import org.lab1java.sunsetsunriseapi.entity.SunEntity;
import org.lab1java.sunsetsunriseapi.model.SunInfoRequest;
import org.lab1java.sunsetsunriseapi.model.SunInfoResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    private static String getTimeZone(double latitude, double longitude) {
        try {
            String apiUrl = "http://api.geonames.org/timezoneJSON?lat=" + latitude + "&lng=" + longitude + "&username=willygodx";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

            return jsonResponse.get("timezoneId").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SunInfoResponse getSunInfo(SunInfoRequest request) {

        Optional<SunEntity> optionalSunEntity = sunRepo.findByLatitudeAndLongitudeAndDate(request.getLatitude(), request.getLongitude(), request.getDate());
        if (optionalSunEntity.isPresent()) {
            SunEntity sunEntity = optionalSunEntity.get();
            return new SunInfoResponse(sunEntity.getSunrise(), sunEntity.getSunset());
        } else {
            String apiResponse = externalApiService.getApiResponse(request);
            SunInfoResponse sunInfoResponse = externalApiService.extractSunInfoFromApiResponse(apiResponse);

            String timeZone = getTimeZone(request.getLatitude(), request.getLongitude());
            ZoneId utc0Zone = ZoneId.of("UTC+0");
            ZonedDateTime zonedDateTimeUtc0 = ZonedDateTime.of(
                    ZonedDateTime.now(utc0Zone).toLocalDate(),
                    sunInfoResponse.getSunrise(),
                    utc0Zone
            );
            ZonedDateTime zonedDateTimeUtc0z = ZonedDateTime.of(
                    ZonedDateTime.now(utc0Zone).toLocalDate(),
                    sunInfoResponse.getSunset(),
                    utc0Zone
            );
            ZoneId utc9Zone = ZoneId.of(timeZone);
            ZonedDateTime zonedDateTimeUtc9 = zonedDateTimeUtc0.withZoneSameInstant(utc9Zone);
            ZonedDateTime zonedDateTimeUtc9z = zonedDateTimeUtc0z.withZoneSameInstant(utc9Zone);
            sunInfoResponse.setSunrise(zonedDateTimeUtc9.toLocalTime());
            sunInfoResponse.setSunset(zonedDateTimeUtc9z.toLocalTime());

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
