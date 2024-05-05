package org.core.sunsetsunrise.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents geographical coordinates including latitude, longitude, date, sunrise time,
 * sunset time, time zone, city, and associated country and users.
 */
@Entity
@Table(name = "coordinates")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Coordinates {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double latitude;

  private double longitude;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime sunrise;

  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime sunset;

  private String timeZone;

  private String city;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "country_id", referencedColumnName = "id", nullable = true,
      foreignKey = @ForeignKey(name = "FK_TIMEZONE",
          foreignKeyDefinition = "FOREIGN KEY (country_id) REFERENCES country(id)"))
  private Country country;

  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(name = "coordinates_user",
      joinColumns = @JoinColumn(name = "coordinates_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  @JsonIgnore
  private Set<User> userSet = new HashSet<>();

  /**
   * Constructs a new Coordinates object with the specified latitude, longitude, date,
   * sunrise time, sunset time, time zone, and city.
   *
   * @param latitude  the latitude of the coordinates
   * @param longitude the longitude of the coordinates
   * @param date      the date associated with the coordinates
   * @param sunrise   the time of sunrise at the coordinates
   * @param sunset    the time of sunset at the coordinates
   * @param timeZone  the time zone of the coordinates
   * @param city      the city associated with the coordinates
   */
  public Coordinates(double latitude, double longitude, LocalDate date, LocalTime sunrise,
                     LocalTime sunset, String timeZone, String city) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.date = date;
    this.sunrise = sunrise;
    this.sunset = sunset;
    this.timeZone = timeZone;
    this.city = city;
  }
}
