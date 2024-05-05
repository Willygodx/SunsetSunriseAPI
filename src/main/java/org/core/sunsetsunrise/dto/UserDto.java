package org.core.sunsetsunrise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for User.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
  private String email;

  private String nickname;

  private String password;
}
