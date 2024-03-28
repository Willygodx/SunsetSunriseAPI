package org.lab1java.sunsetsunriseapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CoordinatesRepository;
import org.lab1java.sunsetsunriseapi.dao.CountryRepository;
import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.CoordinatesDto;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.exception.BadRequestErrorException;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service class that handles operations related to coordinates, including retrieval, creation,
 * updating, and deletion.
 */
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
  private static final String ALREADY_EXISTS = "This information already exists.";

  /**
   * Retrieves coordinates information for a given user and request. If the coordinates information
   * is found in the database, it returns the sunrise time, sunset time, time zone, country name,
   * city associated with the coordinates. If the information is not found, it retrieves it from an
   * external API, adjusts the times based on the requested time zone, and returns the response DTO
   * containing adjusted sunrise and sunset times, country name, and city.
   *
   * @param userId  the ID of the user
   * @param request the request DTO containing latitude, longitude, and date
   * @return a response DTO containing sunrise time, sunset time, time zone, country name, and city
   * @throws JsonProcessingException if there's an error processing JSON data
   */
  public ResponseDto getCoordinatesInfo(int userId, RequestDto request)
      throws JsonProcessingException {

    Optional<Coordinates> optionalSunHistory =
        coordinatesRepository.findByLatitudeAndLongitudeAndDate(request.getLatitude(),
            request.getLongitude(), request.getDate());
    if (optionalSunHistory.isPresent()) {
      Coordinates coordinates = optionalSunHistory.get();

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
      Set<Coordinates> coordinatesSet = user.getCoordinatesSet();

      if (!coordinatesSet.contains(coordinates)) {
        coordinatesSave(userId, getCountryName(request), coordinates);
      }

      return new ResponseDto(coordinates.getSunrise(), coordinates.getSunset(),
          coordinates.getTimeZone(), coordinates.getCountry().getName(), coordinates.getCity());
    } else {
      ResponseDto response = getCheckedResponseFromApi(request);
      Coordinates coordinates = getCoordinatesEntity(request, response);
      coordinatesSave(userId, response.getCountry(), coordinates);

      return response;
    }
  }

  /**
   * Retrieves the response DTO containing adjusted sunrise and sunset times, country name, and city
   * from an external API based on the provided request DTO. Adjusts the sunrise and sunset times
   * based on the requested time zone.
   *
   * @param request the request DTO containing latitude, longitude, and date
   * @return the response DTO containing adjusted sunrise and sunset times, country name, and city
   * @throws JsonProcessingException if there's an error processing JSON data
   */
  public ResponseDto getCheckedResponseFromApi(RequestDto request) throws JsonProcessingException {
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
    ZonedDateTime requestedZonedDateTimeSunrise =
        zonedDateTimeSunrise.withZoneSameInstant(requestedTimeZone);
    ZonedDateTime requestedZonedDateTimeSunset =
        zonedDateTimeSunset.withZoneSameInstant(requestedTimeZone);

    String countryName = getCountryName(request);
    String city = requestedTimeZone.toString().split("/")[1];

    responseDto.setSunrise(requestedZonedDateTimeSunrise.toLocalTime());
    responseDto.setSunset(requestedZonedDateTimeSunset.toLocalTime());
    responseDto.setCountry(countryName);
    responseDto.setTimeZone(timeZone);
    responseDto.setCity(city);

    return responseDto;
  }

  /**
   * Retrieves the name of the country based on the latitude and longitude provided in the request.
   * Uses an external API service to fetch the country code based on the coordinates, then retrieves
   * the country name using the country code.
   *
   * @param request the request DTO containing latitude and longitude
   * @return the name of the country
   * @throws ResourceNotFoundException if the country code
   *                                   is not found or if the country name cannot be retrieved
   */
  public String getCountryName(RequestDto request) {
    String countryCode =
        externalApiService.getCountry(request.getLatitude(), request.getLongitude());
    if (countryCode == null) {
      throw new ResourceNotFoundException("Country not found!");
    }

    String trimmedCode = countryCode.trim();
    return externalApiService.getCountryNameByCode(trimmedCode);
  }

  /**
   * Saves the coordinates information associated with a user and a country.
   *
   * @param userId       the ID of the user to associate the coordinates with
   * @param countryName  the name of the country associated with the coordinates
   * @param coordinates  the coordinates information to be saved
   * @throws ResourceNotFoundException if the user with the given ID is not found
   */
  public void coordinatesSave(int userId, String countryName, Coordinates coordinates) {
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

  /**
   * Creates a new Coordinates object based on the provided request and response DTOs.
   *
   * @param request     the request DTO containing latitude, longitude, and date information
   * @param responseDto the response DTO containing sunrise, sunset, time zone, and city information
   * @return a new Coordinates object initialized with the provided data
   */
  public Coordinates getCoordinatesEntity(RequestDto request, ResponseDto responseDto) {
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

  /**
   * Retrieves a page of coordinates with sunrise starting at the specified hour.
   *
   * @param hour       the hour at which the sunrise starts
   * @param pageNumber the page number to retrieve (starting from 0)
   * @param pageSize   the size of each page
   * @return a Page object containing the coordinates with sunrise starting at the specified hour
   */
  public Page<Coordinates> getCoordinatesInfoBySunriseStartingHour(int hour, Integer pageNumber,
                                                                   Integer pageSize) {
    int hashCode = Objects.hash(hour, pageNumber, pageSize, 61 * 32);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (Page<Coordinates>) cachedData;
    } else {

      if (pageNumber == null || pageNumber < 0) {
        pageNumber = 0;
      }

      if (pageSize == null || pageSize < 1) {
        pageSize = 10;
      }

      Page<Coordinates> coordinatesPage = coordinatesRepository.findBySunriseStartingHour(hour,
          PageRequest.of(pageNumber, pageSize));
      cacheMap.put(hashCode, coordinatesPage);

      return coordinatesPage;
    }
  }

  /**
   * Retrieves a page of coordinates with sunset starting at the specified hour.
   *
   * @param hour       the hour at which the sunset starts
   * @param pageNumber the page number to retrieve (starting from 0)
   * @param pageSize   the size of each page
   * @return a Page object containing the coordinates with sunset starting at the specified hour
   */
  public Page<Coordinates> getCoordinatesInfoBySunsetStartingHour(int hour, Integer pageNumber,
                                                                  Integer pageSize) {
    int hashCode = Objects.hash(hour, pageNumber, pageSize, 62 * 33);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (Page<Coordinates>) cachedData;
    } else {

      if (pageNumber == null || pageNumber < 0) {
        pageNumber = 0;
      }

      if (pageSize == null || pageSize < 1) {
        pageSize = 10;
      }

      Page<Coordinates> coordinatesPage = coordinatesRepository.findBySunsetStartingHour(hour,
          PageRequest.of(pageNumber, pageSize));
      cacheMap.put(hashCode, coordinatesPage);

      return coordinatesPage;
    }
  }

  /**
   * Retrieves a page of users associated with the coordinates identified by the specified ID.
   *
   * @param id         the ID of the coordinates
   * @param pageNumber the page number to retrieve (starting from 0)
   * @param pageSize   the size of each page
   * @return a Page object containing the users associated with the specified coordinates
   * @throws ResourceNotFoundException if the coordinates with the specified ID are not found
   */
  public Page<User> getUsersFromCoordinates(long id, Integer pageNumber, Integer pageSize) {
    int hashCode = Objects.hash(id, pageNumber, pageSize, 63 * 34);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (Page<User>) cachedData;
    } else {
      if (pageNumber == null || pageNumber < 0) {
        pageNumber = 0;
      }

      if (pageSize == null || pageSize < 1) {
        pageSize = 10;
      }

      Coordinates coordinates = coordinatesRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE));

      Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
      Page<User> pageUser = userRepository.findByCoordinatesSetContaining(coordinates, pageable);
      cacheMap.put(hashCode, pageUser);

      return pageUser;
    }
  }

  /**
   * Retrieves a page of coordinates information.
   *
   * @param pageNumber the page number to retrieve (starting from 0)
   * @param pageSize   the size of each page
   * @return a Page object containing the coordinates information
   */
  public Page<CoordinatesDto> getAllCoordinatesInfo(Integer pageNumber, Integer pageSize) {
    int hashCode = Objects.hash(pageNumber, pageSize, 64 * 35);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (Page<CoordinatesDto>) cachedData;
    } else {
      if (pageNumber == null || pageNumber < 0) {
        pageNumber = 0;
      }

      if (pageSize == null || pageSize < 1) {
        pageSize = 10;
      }

      Page<CoordinatesDto> coordinatesPage =
          coordinatesRepository.findAll(PageRequest.of(pageNumber, pageSize))
              .map(this::mapToCoordinatesDto);
      cacheMap.put(hashCode, coordinatesPage);

      return coordinatesPage;
    }
  }

  /**
   * Maps a Coordinates entity to a CoordinatesDto object.
   *
   * @param coordinates the Coordinates entity to map
   * @return a CoordinatesDto object containing the mapped data
   */
  public CoordinatesDto mapToCoordinatesDto(Coordinates coordinates) {
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

  /**
   * Updates the information of a coordinates entity with the provided data.
   *
   * @param id        the ID of the coordinates entity to update
   * @param updateDto the DTO containing the updated coordinates information
   * @return the updated Coordinates entity
   * @throws ResourceNotFoundException   if the coordinates entity with the given ID is not found
   * @throws BadRequestErrorException     if an error occurs during the update process
   */
  public Coordinates updateCoordinatesInfo(Long id, CoordinatesDto updateDto) {
    Coordinates coordinates = coordinatesRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE));

    try {
      final Country country = new Country(updateDto.getCountry());
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

      cacheMap.clear();
      return coordinates;
    } catch (Exception e) {
      throw new BadRequestErrorException(ALREADY_EXISTS);
    }
  }

  /**
   * Creates coordinates information based on the provided request data.
   *
   * @param request the DTO containing the request data
   * @throws BadRequestErrorException if coordinates information already
   *                                  exists or an error occurs during creation
   */
  public void createCoordinatesInfo(RequestDto request) {
    try {
      ResponseDto responseDto = getCheckedResponseFromApi(request);
      Coordinates coordinates = getCoordinatesEntity(request, responseDto);
      String countryName = responseDto.getCountry();

      Optional<Coordinates> existingCoordinates =
          coordinatesRepository.findByLatitudeAndLongitudeAndDate(coordinates.getLatitude(),
              coordinates.getLongitude(), coordinates.getDate());
      if (existingCoordinates.isPresent()) {
        throw new BadRequestErrorException(ALREADY_EXISTS);
      }

      Country country = countryRepository.findByName(countryName)
          .orElseGet(() -> new Country(countryName));

      country.getCoordinatesList().add(coordinates);
      coordinates.setCountry(country);

      coordinatesRepository.save(coordinates);

      cacheMap.clear();
    } catch (Exception e) {
      throw new BadRequestErrorException(ALREADY_EXISTS);
    }
  }

  /**
   * Creates coordinates information in bulk based on the provided list of request data.
   *
   * @param requestDtoList the list of DTOs containing the request data
   * @throws ResourceNotFoundException if the list is null or empty
   * @throws IllegalArgumentException  if errors occur during bulk creation
   */
  public void createCoordinatesInfoBulk(List<RequestDto> requestDtoList) {
    if (requestDtoList == null || requestDtoList.isEmpty()) {
      throw new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE);
    }

    List<String> errors = requestDtoList.stream()
        .map(request -> {
          try {
            createCoordinatesInfo(request);
            return null;
          } catch (Exception e) {
            return e.getMessage();
          }
        })
        .filter(Objects::nonNull)
        .toList();

    cacheMap.clear();
    if (!errors.isEmpty()) {
      throw new IllegalArgumentException(
          "Errors occurred during bulk creation: " + String.join("   ||||   ", errors));
    }
  }

  /**
   * Deletes coordinates information from the database based on the provided ID.
   *
   * @param id the ID of the coordinates information to be deleted
   * @throws ResourceNotFoundException if the coordinates information with the given ID is not found
   */
  public void deleteCoordinatesInfoFromDatabase(Long id) {
    if (coordinatesRepository.existsById(id)) {
      cacheMap.clear();
      coordinatesRepository.deleteById(id);
    } else {
      throw new ResourceNotFoundException(COORDINATES_NOT_FOUND_MESSAGE);
    }
  }
}
