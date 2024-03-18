package org.lab1java.sunsetsunriseapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Entity
@Table (name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nickname", "email"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    private String email;

    private String nickname;

    @ManyToMany (mappedBy = "userSet", cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Set<Country> countrySet;

    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
