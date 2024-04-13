package org.lab1java.sunsetsunriseapi.dto;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab1java.sunsetsunriseapi.controller.UserController;
import org.lab1java.sunsetsunriseapi.service.RequestCounterService;
import org.lab1java.sunsetsunriseapi.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginDtoTest {

  @Mock
  private RequestCounterService counterService;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  @Test
  void testUserLogin_Success() {
    String nickname = "testUser";
    String password = "testPassword";
    int userId = 1;
    LoginDto loginDto = new LoginDto(nickname, password);
    IdDto idDto = new IdDto(userId);
    when(userService.checkLogin(nickname, password)).thenReturn(userId);

    ResponseEntity<IdDto> responseEntity = userController.userLogin(loginDto);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(idDto.getId(), Objects.requireNonNull(responseEntity.getBody()).getId());
    verify(counterService, times(1)).requestIncrement();
  }
}

