package org.lab1java.sunsetsunriseapi.service;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CountryRepository;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.exception.CountryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    private final EntityCache<Integer, Object> cacheMap;
    private static final String COUNTRY_NOT_FOUND_MESSAGE = "Country not found!";

    public Country getCountryById(int id) {
        int hashCode = Objects.hash(id, 30 * 31);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (Country) cachedData;
        } else {
            Country country = countryRepository.findById(id)
                            .orElseThrow(() -> new CountryNotFoundException(COUNTRY_NOT_FOUND_MESSAGE));
            cacheMap.put(hashCode, country);

            return country;
        }
    }

    public Set<User> getCountryUsers(String countryName) {
        int hashCode = Objects.hash(countryName, 31 * 32);
        Object cachedData = cacheMap.get(hashCode);

        if (cachedData != null) {
            return (Set<User>) cachedData;
        } else {
            Country country = countryRepository.findByName(countryName)
                    .orElseThrow(() -> new CountryNotFoundException(COUNTRY_NOT_FOUND_MESSAGE));
            cacheMap.put(hashCode, country.getUserSet());

            return country.getUserSet();
        }
    }

    public Page<Country> getAllCountries(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }
        if(pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        return countryRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public void createCountry(CountryDto countryDto) {
        Country country = new Country(countryDto.getCountry());
        cacheMap.remove(Objects.hash(countryDto.getCountry(), 31 * 32));
        countryRepository.save(country);
    }

    public Country updateCountry(int id, CountryDto updateDto) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(COUNTRY_NOT_FOUND_MESSAGE));
        cacheMap.remove(Objects.hash(id, 30 * 31));

        country.setName(updateDto.getCountry());
        countryRepository.save(country);

        return country;
    }

    public void deleteCountryFromDatabase(int id) {
        if (countryRepository.existsById(id)) {
            cacheMap.remove(Objects.hash(id, 30 * 31));
            countryRepository.deleteById(id);
        } else {
            throw new CountryNotFoundException(COUNTRY_NOT_FOUND_MESSAGE);
        }
    }
}

