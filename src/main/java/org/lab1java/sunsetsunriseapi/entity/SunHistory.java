package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table (name = "sun_history")
public class SunHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double latitude;
    private double longitude;
    private LocalDate date;
    private LocalTime sunrise;
    private LocalTime sunset;
    private String timeZone;
    private String country;
    private String city;
    @ManyToOne (cascade = {CascadeType.PERSIST})
    @JoinColumn (name = "user_id", referencedColumnName = "id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JsonIgnore
    private User user;

    public SunHistory(double latitude, double longitude, LocalDate date, LocalTime sunrise, LocalTime sunset, String country, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.country = country;
        this.city = city;
    }

    public SunHistory() {
        // empty
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getSunrise() {
        return sunrise;
    }

    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }

    public LocalTime getSunset() {
        return sunset;
    }

    public void setSunset(LocalTime sunset) {
        this.sunset = sunset;
    }
}
