package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime sunrise;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime sunset;

    private String timeZone;

    private String city;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn (name = "country_id", referencedColumnName = "id", nullable = true,
            foreignKey = @ForeignKey(name = "FK_TIMEZONE",
                    foreignKeyDefinition = "FOREIGN KEY (country_id) REFERENCES country(id)"))
    private Country country;

    @ManyToMany (cascade = CascadeType.PERSIST)
    @JoinTable (name = "coordinates_user",
                joinColumns = @JoinColumn(name = "coordinates_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private Set<User> userSet = new HashSet<>();

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
