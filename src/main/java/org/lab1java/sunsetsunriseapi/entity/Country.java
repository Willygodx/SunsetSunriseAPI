package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table (name = "country", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @Column(name = "name", nullable = true)
    private String name;

    @ManyToMany (cascade = CascadeType.PERSIST)
    @JoinTable (
            name = "country_user",
            joinColumns = @JoinColumn (name = "country_id"),
            inverseJoinColumns = @JoinColumn (name = "user_id"))
    @JsonIgnore
    private Set<User> userSet = new HashSet<>();

    @OneToMany (mappedBy = "country", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Coordinates> coordinatesList = new ArrayList<>();

    public Country(String name) {
        this.name = name;
    }
}
