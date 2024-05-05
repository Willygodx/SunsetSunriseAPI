package org.core.sunsetsunrise.service;

import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.core.sunsetsunrise.cache.EntityCache;
import org.core.sunsetsunrise.dao.CoordinatesRepository;
import org.core.sunsetsunrise.dao.CountryRepository;
import org.core.sunsetsunrise.dto.CountryDto;
import org.core.sunsetsunrise.entity.Coordinates;
import org.core.sunsetsunrise.entity.Country;
import org.core.sunsetsunrise.exception.BadRequestErrorException;
import org.core.sunsetsunrise.exception.InvalidDataException;
import org.core.sunsetsunrise.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Service class for handling operations related to countries.
 */
@Service
@AllArgsConstructor
public class CountryService {
  private final CountryRepository countryRepository;

  private final CoordinatesRepository coordinatesRepository;

  private final EntityCache<Integer, Object> cacheMap;
  private static final String COUNTRY_NOT_FOUND_MESSAGE = "Country not found!";

  /**
   * Retrieves a country by its ID.
   *
   * @param id the ID of the country
   * @return the country with the specified ID
   * @throws ResourceNotFoundException if the country with the given ID is not found
   */
  public Country getCountryById(int id) {
    int hashCode = Objects.hash(id, 30 * 31);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (Country) cachedData;
    } else {
      Country country = countryRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException(COUNTRY_NOT_FOUND_MESSAGE));
      cacheMap.put(hashCode, country);

      return country;
    }
  }

  /**
   * Retrieves coordinates information for a given country.
   *
   * @param countryName the name of the country
   * @param pageNumber  the page number of the results
   * @param pageSize    the size of each page
   * @return a page of coordinates information for the specified country
   * @throws ResourceNotFoundException if the country with the given name is not found
   */
  public Page<Coordinates> getCoordinatesInfoForCountry(String countryName, Integer pageNumber,
                                                        Integer pageSize) {
    int hashCode = Objects.hash(countryName, pageNumber, pageSize, 31 * 32);
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

      Country country = new Country(countryName);

      if (country.getName().isEmpty()) {
        throw new ResourceNotFoundException(COUNTRY_NOT_FOUND_MESSAGE);
      }

      PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

      Page<Coordinates> coordinatesPage = coordinatesRepository.findByCountry(country, pageRequest);

      cacheMap.put(hashCode, coordinatesPage);
      return coordinatesPage;
    }
  }

  /**
   * Retrieves a page of all countries.
   *
   * @param pageNumber the page number of the results
   * @param pageSize   the size of each page
   * @return a page of all countries
   */
  public Page<Country> getAllCountries(Integer pageNumber, Integer pageSize) {
    int hashCode = Objects.hash(pageNumber, pageSize, 32 * 33);
    Object cachedData = cacheMap.get(hashCode);

    if (cachedData != null) {
      return (Page<Country>) cachedData;
    } else {

      if (pageNumber == null || pageNumber < 0) {
        pageNumber = 0;
      }

      if (pageSize == null || pageSize < 1) {
        pageSize = 10;
      }
      Page<Country> countryPage = countryRepository.findAll(PageRequest.of(pageNumber, pageSize));

      cacheMap.put(hashCode, countryPage);
      return countryPage;
    }
  }

  /**
   * Creates a new country.
   *
   * @param countryDto the DTO containing the country data
   * @throws InvalidDataException if the country data is invalid
   * @throws BadRequestErrorException if the country already exists
   */
  public void createCountry(CountryDto countryDto) {
    if (countryDto.getCountry() == null) {
      throw new InvalidDataException("Invalid country data!");
    }
    try {
      Country country = new Country(countryDto.getCountry());

      cacheMap.clear();
      countryRepository.save(country);
    } catch (Exception e) {
      throw new BadRequestErrorException("This country already exists!");
    }
  }

  /**
   * Creates multiple countries in bulk.
   *
   * @param countryDtoList the list of DTOs containing the country data
   * @throws ResourceNotFoundException if the list is empty or null
   * @throws IllegalArgumentException if errors occur during bulk creation
   */
  public void createCountryBulk(List<CountryDto> countryDtoList) {
    if (countryDtoList == null || countryDtoList.isEmpty()) {
      throw new ResourceNotFoundException(COUNTRY_NOT_FOUND_MESSAGE);
    }

    List<String> errors = countryDtoList.stream()
        .map(request -> {
          try {
            createCountry(request);
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
   * Updates the information of a country.
   *
   * @param id        the ID of the country to update
   * @param updateDto the DTO containing the updated country data
   * @return the updated country entity
   * @throws ResourceNotFoundException    if the country with the given ID is not found
   * @throws BadRequestErrorException      if the country already exists
   */
  public Country updateCountry(int id, CountryDto updateDto) {
    Country country = countryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(COUNTRY_NOT_FOUND_MESSAGE));
    try {
      country.setName(updateDto.getCountry());

      countryRepository.save(country);
      cacheMap.clear();
      return country;
    } catch (Exception e) {
      throw new BadRequestErrorException("This country already exists!");
    }
  }

  /**
   * Deletes a country from the database based on its ID.
   *
   * @param id the ID of the country to delete
   * @throws ResourceNotFoundException if the country with the given ID is not found
   */
  public void deleteCountryFromDatabase(int id) {
    if (countryRepository.existsById(id)) {
      cacheMap.clear();
      countryRepository.deleteById(id);
    } else {
      throw new ResourceNotFoundException(COUNTRY_NOT_FOUND_MESSAGE);
    }
  }
}

