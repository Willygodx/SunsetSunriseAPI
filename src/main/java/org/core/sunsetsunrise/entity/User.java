package org.core.sunsetsunrise.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents a user entity with its email, nickname, and associated coordinates.
 */
@Entity
@Table(name = "user", uniqueConstraints = {
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

  @JsonIgnore
  private String password;

  @ManyToMany(mappedBy = "userSet", cascade = CascadeType.PERSIST)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Set<Coordinates> coordinatesSet;

  /**
   * Constructs a new User with the specified email, nickname, password.
   *
   * @param email  user's email
   * @param nickname user's nickname
   * @param password      user's password
   */
  public User(String email, String nickname, String password) {
    this.email = email;
    this.nickname = nickname;
    this.password = password;
  }

  public void removeCoordinates(Coordinates coordinates) {
    this.coordinatesSet.remove(coordinates);
    coordinates.getUserSet().remove(this);
  }
}
