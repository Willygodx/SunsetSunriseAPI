package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    private List<User> userList = new ArrayList<>();

    public TimeZone() {
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

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userSet) {
        this.userList = userSet;
    }
}
