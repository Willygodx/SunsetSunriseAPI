package org.lab1java.sunsetsunriseapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CoordinatesRepository;
import org.lab1java.sunsetsunriseapi.dao.CountryRepository;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CoordinatesRepository coordinatesRepository;

    @Mock
    private EntityCache<Integer, Object> cacheMap;

    @InjectMocks
    private CountryService countryService;

    private static final String COUNTRY_NAME = "Test Country";
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Test
    void testGetCountryById_CachedData() {
        int id = 1;
        Country cachedCountry = new Country("Test Country");
        when(cacheMap.get(anyInt())).thenReturn(cachedCountry);

        Country result = countryService.getCountryById(id);

        assertEquals(cachedCountry, result);
    }

    @Test
    void testGetCountryById_NotFound() {
        int id = 1;
        when(cacheMap.get(anyInt())).thenReturn(null);
        when(countryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> countryService.getCountryById(id));

        verify(countryRepository, times(1)).findById(id);
    }

    @Test
    void testGetCoordinatesInfoForCountry() {
        double latitude = 51.5074;
        double longitude = -0.1278;
        LocalDate date = LocalDate.now();
        LocalTime sunrise = LocalTime.of(6, 30, 0);
        LocalTime sunset = LocalTime.of(18, 0, 0);
        String timeZone = "Europe/London";
        String city = "London";

        Country country = new Country(COUNTRY_NAME);
        when(countryRepository.findByName(COUNTRY_NAME)).thenReturn(Optional.of(country));

        List<Coordinates> coordinatesList = new ArrayList<>();
        coordinatesList.add(new Coordinates(latitude, longitude, date, sunrise, sunset, timeZone, city));

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<Coordinates> coordinatesPage = new PageImpl<>(coordinatesList);
        when(coordinatesRepository.findByCountry(country, pageRequest)).thenReturn(coordinatesPage);

        Page<Coordinates> result = countryService.getCoordinatesInfoForCountry(COUNTRY_NAME, PAGE_NUMBER, PAGE_SIZE);

        assertNotNull(result);
        assertEquals(coordinatesPage, result);
        verify(countryRepository, times(1)).findByName(COUNTRY_NAME);
        verify(coordinatesRepository, times(1)).findByCountry(country, pageRequest);
    }

    @Test
    void testGetAllCountries() {
        int pageNumber = 0;
        int pageSize = 10;

        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country("Country1"));
        countryList.add(new Country("Country2"));
        countryList.add(new Country("Country3"));

        Page<Country> countryPage = new PageImpl<>(countryList);

        when(countryRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(countryPage);

        Page<Country> result = countryService.getAllCountries(pageNumber, pageSize);

        assertNotNull(result);
        assertEquals(countryPage, result);
        verify(countryRepository, times(1)).findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Test
    void testCreateCountry_WithValidDto() {
        CountryDto countryDto = new CountryDto("Test Country");
        Country country = new Country("Test Country");
        when(countryRepository.save(any())).thenReturn(country);

        countryService.createCountry(countryDto);

        verify(countryRepository, times(1)).save(any());
    }

}
