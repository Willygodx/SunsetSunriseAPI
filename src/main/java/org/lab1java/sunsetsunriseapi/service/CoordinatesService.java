package org.lab1java.sunsetsunriseapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CountryRepository;
import org.lab1java.sunsetsunriseapi.dao.CoordinatesRepository;
import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.CoordinatesDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.exception.BadRequestErrorException;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class CoordinatesService {

    private final ApiService externalApiService;

    private final CoordinatesRepository coordinatesRepository;

    private final UserRepository userRepository;

    private final CountryRepository countryRepository;

    private final EntityCache<Integer, Object> cacheMap;
    private static final String COORDINATES_NOT_FOUND_MESSAGE = "Coordinates information not found!";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";

    public ResponseDto getCoordinatesInfo(int userId, RequestDto request) throws JsonProcessingException {

        Optional<Coordinates> optionalSunHistory = coordinatesRepository.findByLatitudeAndLongitudeAndDate(request.getLatitude(), request.getLongitude(), request.getDate());
        if (optionalSunHistory.isPresent()) {
            Coordinates coordinates = optionalSunHistory.get();

            if (coordinates.getCountry() == null) {
                String countryName = getCountryName(request);
                Country country = new Country(countryName);
                coordinates.setCountry(country);
                coordinatesSave(userId, countryName, coordinates);
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
            Set<Coordinates> coordinatesSet = user.getCoordinatesSet();

            if (!coordinatesSet.contains(coordinates)) {
                coordinatesSave(userId, getCountryName(request), coordinates);
            }

            return new ResponseDto(coordinates.getSunrise(), coordinates.getSunset(), coordinates.getTimeZone(), coordinates.getCountry().getName(), coordinates.getCity());
        } else {
            ResponseDto response = getCheckedResponseFromApi(request);
            Coordinates coordinates = getCoordinatesEntity(request, response);
            coordinatesSave(userId, response.getCountry(), coordinates);

            return response;
        }
    }

    private ResponseDto getCheckedResponseFromApi(RequestDto request) throws JsonProcessingException {
        String apiResponse = externalApiService.getApiResponse(request);
        ResponseDto responseDto = externalApiService.extractCoordinatesInfoFromApiResponse(apiResponse);

        String timeZone = externalApiService.getTimeZone(request.getLatitude(), request.getLongitude());

        ZoneId defaultTimeZone = ZoneId.of("UTC+0");
        ZonedDateTime zonedDateTimeSunrise = ZonedDateTime.of(
                ZonedDateTime.now(defaultTimeZone).toLocalDate(),
                responseDto.getSunrise(),
                defaultTimeZone
        );
        ZonedDateTime zonedDateTimeSunset = ZonedDateTime.of(
                ZonedDateTime.now(defaultTimeZone).toLocalDate(),
                responseDto.getSunset(),
                defaultTimeZone
        );

        ZoneId requestedTimeZone = ZoneId.of(timeZone);
        ZonedDateTime requestedZonedDateTimeSunrise = zonedDateTimeSunrise.withZoneSameInstant(requestedTimeZone);
        ZonedDateTime requestedZonedDateTimeSunset = zonedDateTimeSunset.withZoneSameInstant(requestedTimeZone);

        String countryName = getCountryName(request);
        String city = requestedTimeZone.toString().split("/")[1];

        responseDto.setSunrise(requestedZonedDateTimeSunrise.toLocalTime());
        responseDto.setSunset(requestedZonedDateTimeSunset.toLocalTime());
        responseDto.setCountry(countryName);
        responseDto.setTimeZone(timeZone);
        responseDto.setCity(city);

        return responseDto;
    }

    private String getCountryName(RequestDto request) {
        String countryCode = externalApiService.getCountry(request.getLatitude(), request.getLongitude());
        if (countryCode == null) {
            throw new ResourceNotFoundException("Country not found!");
        }

        String trimmedCode = countryCode.trim();
        return externalApiService.getCountryNameByCode(trimmedCode);
    }

    private void coordinatesSave(int userId, String countryName, Coordinates coordinates) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Country> optionalCountry = countryRepository.findByName(countryName);
            Country country = optionalCountry.orElseGet(() -> new Country(countryName));

            user.getCoordinatesSet().add(coordinates);
            coordinates.getUserSet().add(user);

            country.getCoordinatesList().add(coordinates);
            coordinates.setCountry(country);

            coordinatesRepository.save(coordinates);
        } else {
            throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
    }

    private Coordinates getCoordinatesEntity(RequestDto request, ResponseDto responseDto) {
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(request.getLatitude());
        coordinates.setLongitude(request.getLongitude());
        coordinates.setDate(request.getDate());
        coordinates.setSunrise(responseDto.getSunrise());
        coordinates.setSunset(responseDto.getSunset());
        coordinates.setTimeZone(responseDto.getTimeZone());
        coordinates.setCity(responseDto.getCity());
        return coordinates;
    }

    public List<Coordinates> getCoordinatesInfoBySunriseStartingHour(int hour) {
        int hashCode = Objects.hash(hour, 61 * 32);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (List<Coordinates>) cachedData;
        } else {
            List<Coordinates> coordinatesList = coordinatesRepository.findBySunriseStartingHour(hour);
            if (!coordinatesList.isEmpty()) {
                cacheMap.put(hashCode, coordinatesList);

                return coordinatesList;
            } else {
                throw new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE);
            }
        }
    }

    public List<Coordinates> getCoordinatesInfoBySunsetStartingHour(int hour) {
        int hashCode = Objects.hash(hour, 62 * 33);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (List<Coordinates>) cachedData;
        } else {
            List<Coordinates> coordinatesList = coordinatesRepository.findBySunsetStartingHour(hour);
            if (!coordinatesList.isEmpty()) {
                cacheMap.put(hashCode, coordinatesList);

                return coordinatesList;
            } else {
                throw new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE);
            }
        }
    }

    public Page<User> getUsersFromCoordinates(long id, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));

        return userRepository.findByCoordinatesSetContaining(coordinates, pageable);

    }

    public Page<CoordinatesDto> getAllCoordinatesInfo(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<Coordinates> coordinatesPage = coordinatesRepository.findAll(PageRequest.of(pageNumber, pageSize));

        return coordinatesPage.map(this::mapToCoordinatesDTO);
    }

    private CoordinatesDto mapToCoordinatesDTO(Coordinates coordinates) {
        CoordinatesDto coordinatesDto = new CoordinatesDto();
        coordinatesDto.setLatitude(coordinates.getLatitude());
        coordinatesDto.setLongitude(coordinates.getLongitude());
        coordinatesDto.setDate(coordinates.getDate());
        coordinatesDto.setSunrise(coordinates.getSunrise());
        coordinatesDto.setSunset(coordinates.getSunset());
        coordinatesDto.setTimeZone(coordinates.getTimeZone());
        coordinatesDto.setCity(coordinates.getCity());
        coordinatesDto.setCountry(coordinates.getCountry().getName());
        return coordinatesDto;
    }

    public Coordinates updateCoordinatesInfo(Long id, CoordinatesDto updateDto) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE));

        try {
            clearCache(coordinates);

            Country country = new Country(updateDto.getCountry());
            coordinates.setLatitude(updateDto.getLatitude());
            coordinates.setLongitude(updateDto.getLongitude());
            coordinates.setDate(updateDto.getDate());
            coordinates.setSunrise(updateDto.getSunrise());
            coordinates.setSunset(updateDto.getSunset());
            coordinates.setCity(updateDto.getCity());
            coordinates.setTimeZone(updateDto.getTimeZone());

            country.getCoordinatesList().add(coordinates);
            coordinates.setCountry(country);
            coordinatesRepository.save(coordinates);
            return coordinates;
        } catch (Exception e) {
            throw new BadRequestErrorException("This information already exists.");
        }
    }

    public void createCoordinatesInfo(RequestDto request) {
        try {
            ResponseDto responseDto = getCheckedResponseFromApi(request);
            Coordinates coordinates = getCoordinatesEntity(request, responseDto);
            Country country = new Country(responseDto.getCountry());
            country.getCoordinatesList().add(coordinates);
            coordinates.setCountry(country);

            clearCache(coordinates);
            coordinatesRepository.save(coordinates);
        } catch (Exception e) {
            throw new BadRequestErrorException("This information already exists.");
        }
    }

    public void deleteCoordinatesInfoFromDatabase(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE));

        clearCache(coordinates);
        coordinatesRepository.deleteById(id);
    }

    private void clearCache(Coordinates coordinates) {
        cacheMap.remove(Objects.hash(coordinates.getSunset().getHour(), 61 * 32));
        cacheMap.remove(Objects.hash(coordinates.getSunrise().getHour(), 62 * 33));
    }

}
