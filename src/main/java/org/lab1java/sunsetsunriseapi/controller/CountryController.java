package org.lab1java.sunsetsunriseapi.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.lab1java.sunsetsunriseapi.dto.CountryDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.service.CountryService;
import org.lab1java.sunsetsunriseapi.service.RequestCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling country-related endpoints.
 */
@CrossOrigin(origins = {"http://localhost:3000"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowedHeaders = {"Authorization", "Content-Type"})
@RestController
@RequestMapping("/countries")
@AllArgsConstructor
public class CountryController {
  private final CountryService countryService;
  private RequestCounterService counterService;
  private final Logger logger = LoggerFactory.getLogger(CountryController.class);
  private static final String DELETE_SUCCESS_MESSAGE = "Deleted successfully!";
  private static final String CREATE_SUCCESS_MESSAGE = "Created successfully!";

  /**
   * Retrieves information for a specific country.
   *
   * @param id the ID of the country
   * @return ResponseEntity containing the country information
   */
  @GetMapping("/get/{id}")
  public ResponseEntity<Country> getCountry(@PathVariable int id) {
    counterService.requestIncrement();
    logger.info("GET endpoint /countries/get/{id} was called.");

    Country country = countryService.getCountryById(id);

    logger.info("Country information was retrieved successfully.");
    return new ResponseEntity<>(country, HttpStatus.OK);
  }

  /**
   * Retrieves coordinates information for a specific country.
   *
   * @param countryName the name of the country
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of coordinates information for the country
   */
  @GetMapping("/get-coordinates-info/{countryName}")
  public ResponseEntity<Page<Coordinates>> getCoordinatesInfoForCountry(
                                              @PathVariable String countryName,
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    counterService.requestIncrement();
    logger.info("GET endpoint /countries/get-coordinates-info/{countryName} was called.");

    Page<Coordinates> coordinatesPage =
        countryService.getCoordinatesInfoForCountry(countryName, pageNumber, pageSize);

    logger.info("Country's coordinates information was retrieved successfully.");
    return new ResponseEntity<>(coordinatesPage, HttpStatus.OK);
  }

  /**
   * Retrieves information for all countries.
   *
   * @param pageNumber the page number for pagination
   * @param pageSize the size of each page for pagination
   * @return ResponseEntity containing a page of country information
   */
  @GetMapping("/get-all")
  public ResponseEntity<Page<Country>> getAllCountries(
                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
    counterService.requestIncrement();
    logger.info("GET endpoint /countries/get-all was called.");

    Page<Country> countryPage = countryService.getAllCountries(pageNumber, pageSize);

    logger.info("All countries information was retrieved successfully.");
    return new ResponseEntity<>(countryPage, HttpStatus.OK);
  }

  /**
   * Creates a new country.
   *
   * @param countryDto the DTO containing the country information to be created
   * @return ResponseEntity indicating the success of the operation
   */
  @PostMapping("/create")
  public ResponseEntity<String> createCountry(@RequestBody CountryDto countryDto) {
    counterService.requestIncrement();
    logger.info("GET endpoint /countries/create was called.");

    countryService.createCountry(countryDto);

    logger.info("Country information was created successfully.");
    return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.CREATED);
  }

  /**
   * Creates multiple countries in bulk.
   *
   * @param countryDtoList the list of DTOs containing the country information to be created
   * @return ResponseEntity indicating the success of the operation
   */
  @PostMapping("/create-bulk")
  public ResponseEntity<String> createCountryBulk(@RequestBody List<CountryDto> countryDtoList) {
    counterService.requestIncrement();
    logger.info("POST endpoint /countries/create-bulk was called.");

    countryService.createCountryBulk(countryDtoList);

    logger.info("Country array was created successfully.");
    return new ResponseEntity<>(CREATE_SUCCESS_MESSAGE, HttpStatus.OK);
  }

  /**
   * Updates country information.
   *
   * @param id the ID of the country to update
   * @param countryDto the DTO containing the updated country information
   * @return ResponseEntity containing the updated country information
   */
  @PutMapping("/update/{id}")
  public ResponseEntity<Country> updateCountry(@PathVariable int id,
                                               @RequestBody CountryDto countryDto) {
    counterService.requestIncrement();
    logger.info("GET endpoint /countries/update/{id} was called.");

    Country country = countryService.updateCountry(id, countryDto);

    logger.info("Country information was updated successfully.");
    return new ResponseEntity<>(country, HttpStatus.OK);
  }

  /**
   * Deletes country information.
   *
   * @param id the ID of the country to delete
   * @return ResponseEntity indicating the success of the operation
   */
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteCountry(@PathVariable int id) {
    counterService.requestIncrement();
    logger.info("GET endpoint /countries/delete/{id} was called.");

    countryService.deleteCountryFromDatabase(id);

    logger.info("Country information was deleted successfully.");
    return new ResponseEntity<>(DELETE_SUCCESS_MESSAGE, HttpStatus.NO_CONTENT);
  }
}
