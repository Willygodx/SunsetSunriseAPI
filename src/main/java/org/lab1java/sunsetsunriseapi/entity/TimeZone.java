package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table (name = "time_zone", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class TimeZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", nullable = true)
    private String name;
    @ManyToMany (cascade = CascadeType.PERSIST)
    @JoinTable (
            name = "time_zone_user",
            joinColumns = @JoinColumn (name = "time_zone_id"),
            inverseJoinColumns = @JoinColumn (name = "user_id"))
    @JsonIgnore
    private Set<User> userSet = new HashSet<>();

    @OneToMany (mappedBy = "timeZone", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<SunHistory> sunHistoryList = new ArrayList<>();

    public TimeZone() {
    }

    public List<SunHistory> getSunHistoryList() {
        return sunHistoryList;
    }

    public void setSunHistoryList(List<SunHistory> sunHistoryList) {
        this.sunHistoryList = sunHistoryList;
    }

    public TimeZone(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String timeZone) {
        this.name = timeZone;
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }
}
