package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Set;

@Entity
@Table (name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nickname", "email"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String nickname;
    @ManyToMany (mappedBy = "userList", cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Set<TimeZone> timeZoneSet;
    @OneToMany (mappedBy = "user", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<SunHistory> sunHistoryList;

    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public User() {
        // empty
    }

    public Set<TimeZone> getTimeZoneSet() {
        return timeZoneSet;
    }

    public void setTimeZoneSet(Set<TimeZone> timeZoneSet) {
        this.timeZoneSet = timeZoneSet;
    }

    public List<SunHistory> getSunHistoryList() {
        return sunHistoryList;
    }

    public void setSunHistoryList(List<SunHistory> sunHistoryList) {
        this.sunHistoryList = sunHistoryList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
