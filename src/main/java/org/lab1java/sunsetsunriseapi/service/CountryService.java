package org.lab1java.sunsetsunriseapi.service;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CoordinatesRepository;
import org.lab1java.sunsetsunriseapi.dao.CountryRepository;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.exception.BadRequestErrorException;
import org.lab1java.sunsetsunriseapi.exception.InvalidDataException;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    private final CoordinatesRepository coordinatesRepository;

    private final EntityCache<Integer, Object> cacheMap;
    private static final String COUNTRY_NOT_FOUND_MESSAGE = "Country not found!";

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

    public Page<Coordinates> getCoordinatesInfoForCountry(String countryName, Integer pageNumber, Integer pageSize) {
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
            throw new IllegalArgumentException("Errors occurred during bulk creation: " + String.join("   ||||   ", errors));
        }
    }

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

    public void deleteCountryFromDatabase(int id) {
        if (countryRepository.existsById(id)) {
            cacheMap.clear();
            countryRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(COUNTRY_NOT_FOUND_MESSAGE);
        }
    }
}

