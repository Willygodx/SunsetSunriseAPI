package org.lab1java.sunsetsunriseapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * The main class for the SunsetSunriseApi application.
 */
@SpringBootApplication
@EnableWebMvc
public class SunsetSunriseApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(SunsetSunriseApiApplication.class, args);
  }
}
