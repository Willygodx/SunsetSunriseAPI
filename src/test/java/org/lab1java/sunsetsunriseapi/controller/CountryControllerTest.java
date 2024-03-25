package org.lab1java.sunsetsunriseapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.service.CountryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {
    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    @Test
    void testGetCountry() {
        int id = 123;
        Country mockCountry = new Country("Niger");
        when(countryService.getCountryById(id)).thenReturn(mockCountry);

        ResponseEntity<Country> response = countryController.getCountry(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCountry, response.getBody());
        verify(countryService, Mockito.times(1)).getCountryById(id);
    }

    @Test
    void testGetCoordinatesInfoForCountry() {
        String countryName = "Niger";
        int pageNumber = 0;
        int pageSize = 10;
        Page<Coordinates> mockPage = mock(Page.class);
        when(countryService.getCoordinatesInfoForCountry(countryName, pageNumber, pageSize)).thenReturn(mockPage);

        ResponseEntity<Page<Coordinates>> response = countryController.getCoordinatesInfoForCountry(countryName, pageNumber, pageSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
        verify(countryService, Mockito.times(1)).getCoordinatesInfoForCountry(countryName, pageNumber, pageSize);
    }

    @Test
    void testGetAllCountries() {
        int pageNumber = 0;
        int pageSize = 10;
        Page<Country> mockPage = mock(Page.class);
        when(countryService.getAllCountries(pageNumber, pageSize)).thenReturn(mockPage);

        ResponseEntity<Page<Country>> response = countryController.getAllCountries(pageNumber, pageSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
        verify(countryService, times(1)).getAllCountries(pageNumber, pageSize);
    }

    @Test
    void testCreateCountry() {
        CountryDto countryDto = new CountryDto("Niger");

        ResponseEntity<String> response = countryController.createCountry(countryDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Created successfully!", response.getBody());
        verify(countryService, times(1)).createCountry(countryDto);
    }

    @Test
    void testCreateCountryBulk() {
        List<CountryDto> countryDtoList = new ArrayList<>();
        countryDtoList.add(new CountryDto("Niger"));
        countryDtoList.add(new CountryDto("Uganda"));

        ResponseEntity<String> response = countryController.createCountryBulk(countryDtoList);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Created successfully!", response.getBody());
        verify(countryService, times(1)).createCountryBulk(countryDtoList);
    }

    @Test
    void testUpdateCountry() {
        int id = 228;
        CountryDto countryDto = new CountryDto("Niger");
        Country updatedCountry = new Country(countryDto.getCountry());
        when(countryService.updateCountry(id, countryDto)).thenReturn(updatedCountry);

        ResponseEntity<Country> response = countryController.updateCountry(id, countryDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCountry, response.getBody());
        verify(countryService, times(1)).updateCountry(id, countryDto);
    }

    @Test
    void testDeleteCountry() {
        int id = 228;

        ResponseEntity<String> response = countryController.deleteCountry(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Deleted successfully!", response.getBody());
        verify(countryService, times(1)).deleteCountryFromDatabase(id);
    }
}
