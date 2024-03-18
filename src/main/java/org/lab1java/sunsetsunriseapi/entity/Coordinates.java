package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table (name = "coordinates")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private double latitude;

    private double longitude;

    private LocalDate date;

    private LocalTime sunrise;

    private LocalTime sunset;

    private String timeZone;

    private String city;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn (name = "country", referencedColumnName = "name", nullable = true,
            foreignKey = @ForeignKey(name = "FK_TIMEZONE",
                    foreignKeyDefinition = "FOREIGN KEY (country) REFERENCES country(name) ON UPDATE CASCADE ON DELETE SET NULL"))
    @JsonIgnore
    private Country country;

    public Coordinates(double latitude, double longitude, LocalDate date, LocalTime sunrise, LocalTime sunset, String timeZone, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.timeZone = timeZone;
        this.city = city;
    }
}
