package org.lab1java.sunsetsunriseapi.controller;

import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.service.CountryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@RestController
@RequestMapping("/countries")
@AllArgsConstructor
public class CountryController {
    private final CountryService countryService;
    private final Logger logger = LoggerFactory.getLogger(CountryController.class);
    private static final String DELETE_ERROR_MESSAGE = "Error while deleting!";
    private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
    private static final String UPDATE_ERROR_MESSAGE = "Error while updating!";
    private static final String GET_ERROR_MESSAGE = "Error while getting!";
    private static final String CREATE_ERROR_MESSAGE = "Error while creating!";
    private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

    @GetMapping("/get/{id}")
    public ResponseEntity<Country> getCountry(@PathVariable int id) {
        try {

            return new ResponseEntity<>(countryService.getCountryById(id), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-users")
    public ResponseEntity<Set<User>> getCountryUsers(@RequestParam() String name) {
        try {

            return new ResponseEntity<>(countryService.getCountryUsers(name), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all-countries")
    public ResponseEntity<Page<Country>> getAllCountries(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        try {

            return new ResponseEntity<>(countryService.getAllCountries(pageNumber, pageSize), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(GET_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCountry(@RequestBody CountryDto countryDto) {
        try {

            countryService.createCountry(countryDto);
            return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error(CREATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable int id,
                                                 @RequestBody CountryDto countryDto) {
        try {

            return new ResponseEntity<>(countryService.updateCountry(id, countryDto), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(UPDATE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCountry(@PathVariable int id) {
        try {

            countryService.deleteCountryFromDatabase(id);
            return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            logger.error(DELETE_ERROR_MESSAGE, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
