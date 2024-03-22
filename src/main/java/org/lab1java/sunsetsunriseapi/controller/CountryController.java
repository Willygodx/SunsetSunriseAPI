package org.lab1java.sunsetsunriseapi.controller;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.service.CountryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/countries")
@AllArgsConstructor
public class CountryController {
    private final CountryService countryService;
    private final Logger logger = LoggerFactory.getLogger(CountryController.class);
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    @GetMapping("/get/{id}")
    public ResponseEntity<Country> getCountry(@PathVariable int id) {
        logger.info("GET endpoint /countries/get/{id} was called.");

        Country country = countryService.getCountryById(id);

        logger.info("Country information was retrieved successfully.");
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @GetMapping("/get-coordinates-info/{countryName}")
    public ResponseEntity<Page<Coordinates>> getCoordinatesInfoForCountry(@PathVariable String countryName,
                                                                          @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("GET endpoint /countries/get-coordinates-info/{countryName} was called.");

        Page<Coordinates> coordinatesPage = countryService.getCoordinatesInfoForCountry(countryName, pageNumber, pageSize);

        logger.info("Country's coordinates information was retrieved successfully.");
        return new ResponseEntity<>(coordinatesPage, HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<Country>> getAllCountries(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("GET endpoint /countries/get-all was called.");

        Page<Country> countryPage = countryService.getAllCountries(pageNumber, pageSize);

        logger.info("All countries information was retrieved successfully.");
        return new ResponseEntity<>(countryPage, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCountry(@RequestBody CountryDto countryDto) {
        logger.info("GET endpoint /countries/create was called.");

        countryService.createCountry(countryDto);

        logger.info("Country information was created successfully.");
        return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);
    }

    @PostMapping("/create-bulk")
    public ResponseEntity<String> createCountryBulk(@RequestBody List<CountryDto> countryDtoList) {
        logger.info("POST endpoint /countries/create-bulk was called.");

        countryService.createCountryBulk(countryDtoList);

        logger.info("Country array was created successfully.");
        return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable int id,
                                                 @RequestBody CountryDto countryDto) {
        logger.info("GET endpoint /countries/update/{id} was called.");

        Country country = countryService.updateCountry(id, countryDto);

        logger.info("Country information was updated successfully.");
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCountry(@PathVariable int id) {
        logger.info("GET endpoint /countries/delete/{id} was called.");

        countryService.deleteCountryFromDatabase(id);

        logger.info("Country information was deleted successfully.");
        return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
    }
}
