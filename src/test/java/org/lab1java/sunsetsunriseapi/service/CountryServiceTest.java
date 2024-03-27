package org.lab1java.sunsetsunriseapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CoordinatesRepository;
import org.lab1java.sunsetsunriseapi.dao.CountryRepository;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.exception.BadRequestErrorException;
import org.lab1java.sunsetsunriseapi.exception.InvalidDataException;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    void testGetCountryById_CountryInCache() {
        Country cachedCountry = new Country("England");
        when(cacheMap.get(Objects.hash(1, 30 * 31))).thenReturn(cachedCountry);

        Country result = countryService.getCountryById(1);

        assertNotNull(result);
        assertEquals(cachedCountry, result);
        verify(countryRepository, never()).findById(anyInt());
    }

    @Test
    void testGetCountryById_CountryNotInCache() {
        Country countryFromDatabase = new Country("England");
        when(cacheMap.get(Objects.hash(1, 30 * 31))).thenReturn(null);
        when(countryRepository.findById(1)).thenReturn(Optional.of(countryFromDatabase));

        Country result = countryService.getCountryById(1);

        assertNotNull(result);
        assertEquals(countryFromDatabase, result);
        verify(cacheMap, times(1)).put(Objects.hash(1, 30 * 31), countryFromDatabase);
    }

    @Test
    void testGetCountryById_CountryNotFound() {
        when(cacheMap.get(Objects.hash(1, 30 * 31))).thenReturn(null);
        when(countryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> countryService.getCountryById(1));
    }

    @Test
    void testGetCoordinatesInfoForCountry_DataInCache() {
        Page<Coordinates> cachedCoordinatesPage = new PageImpl<>(Collections.singletonList(new Coordinates()));
        when(cacheMap.get(Objects.hash("England", 0, 10, 31 * 32))).thenReturn(cachedCoordinatesPage);

        Page<Coordinates> result = countryService.getCoordinatesInfoForCountry("England", 0, 10);

        assertNotNull(result);
        assertEquals(cachedCoordinatesPage, result);
        verify(countryRepository, never()).findByName(anyString());
        verify(coordinatesRepository, never()).findByCountry(any(Country.class), any(PageRequest.class));
    }

    @Test
    void testGetCoordinatesInfoForCountry_DataNotInCache() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Coordinates> coordinatesPage = new PageImpl<>(Collections.singletonList(new Coordinates()));
        when(cacheMap.get(Objects.hash("England", 0, 10, 31 * 32))).thenReturn(null);
        when(coordinatesRepository.findByCountry(any(), eq(pageRequest))).thenReturn(coordinatesPage);

        Page<Coordinates> result = countryService.getCoordinatesInfoForCountry("England", 0, 10);

        assertNotNull(result);
        assertEquals(coordinatesPage, result);
        verify(cacheMap, times(1)).put(Objects.hash("England", 0, 10, 31 * 32), coordinatesPage);
    }

    @Test
    void testGetCoordinatesInfoForCountry_UserNotFound() {
        when(cacheMap.get(any())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> countryService.getCoordinatesInfoForCountry("", 0, 10));
    }

    @Test
    void testGetAllCountries_DataInCache() {
        Page<Country> cachedUserPage = new PageImpl<>(Collections.singletonList(new Country()));
        when(cacheMap.get(Objects.hash(0, 10, 32 * 33))).thenReturn(cachedUserPage);

        Page<Country> result = countryService.getAllCountries(0, 10);

        assertNotNull(result);
        assertEquals(cachedUserPage, result);
        verify(countryRepository, never()).findAll(any(Pageable.class));
    }
    @Test
    void testGetAllCountries_DataNotInCache() {
        Page<Country> countryPage = new PageImpl<>(Collections.singletonList(new Country()));
        when(cacheMap.get(Objects.hash(0, 10, 32 * 33))).thenReturn(null);
        when(countryRepository.findAll(PageRequest.of(0, 10))).thenReturn(countryPage);

        Page<Country> result = countryService.getAllCountries(0, 10);

        assertNotNull(result);
        assertEquals(countryPage, result);
        verify(cacheMap, times(1)).put(Objects.hash(0, 10, 32 * 33), countryPage);
    }

    @Test
    void testCreateCountry_Success() {
        CountryDto countryDto = new CountryDto("England");

        countryService.createCountry(countryDto);

        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    void testCreateCountry_InvalidData() {
        CountryDto countryDto = new CountryDto(null);

        assertThrows(InvalidDataException.class, () -> countryService.createCountry(countryDto));
    }

    @Test
    void testCreateCountry_Failure() {
        CountryDto countryDto = new CountryDto("England");
        Country country = new Country("England");

        doThrow(new RuntimeException()).when(countryRepository).save(country);

        assertThrows(BadRequestErrorException.class, () -> countryService.createCountry(countryDto));
    }

    @Test
    void testCreateCountryBulk_Success() {
        List<CountryDto> countryDtoList = new ArrayList<>();
        countryDtoList.add(new CountryDto("England"));
        countryDtoList.add(new CountryDto("Russia"));

        countryService.createCountryBulk(countryDtoList);

        verify(countryRepository, times(2)).save(any());
    }

    @Test
    void testCreateCountryBulk_NullList() {
        assertThrows(ResourceNotFoundException.class, () -> countryService.createCountryBulk(null));
    }

    @Test
    void testCreateUsersBulk_EmptyList() {
        List<CountryDto> countryDtoList = new ArrayList<>();

        assertThrows(ResourceNotFoundException.class, () -> countryService.createCountryBulk(countryDtoList));
    }

    @Test
    void testUpdateCountry_Success() {
        Country country = new Country("England");
        CountryDto updateDto = mock(CountryDto.class);

        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(updateDto.getCountry()).thenReturn("England");

        Country updatedCountry = countryService.updateCountry(1, updateDto);

        assertEquals(updatedCountry.getName(), updateDto.getCountry());
        verify(countryRepository, times(1)).findById(1);
        verify(countryRepository, times(1)).save(updatedCountry);
    }

    @Test
    void testUpdateCountry_CountryNotFound() {
        CountryDto updateDto = new CountryDto("England");

        when(countryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> countryService.updateCountry(1, updateDto));
    }

    @Test
    void testUpdateCountry_Failure() {
        Country country = new Country("England");
        CountryDto updateDto = new CountryDto("Russia");

        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        doThrow(new RuntimeException()).when(countryRepository).save(country);

        assertThrows(BadRequestErrorException.class, () -> countryService.updateCountry(1, updateDto));
    }

    @Test
    void testDeleteCountryFromDatabase_Success() {
        when(countryRepository.existsById(1)).thenReturn(true);
        countryService.deleteCountryFromDatabase(1);

        verify(countryRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteCountryFromDatabaseById_CountryNotFound() {
        when(countryRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> countryService.deleteCountryFromDatabase(1));
    }
}
