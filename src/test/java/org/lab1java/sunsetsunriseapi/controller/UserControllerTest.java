package org.lab1java.sunsetsunriseapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab1java.sunsetsunriseapi.dto.UserDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.exception.GlobalExceptionHandler;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.lab1java.sunsetsunriseapi.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUserByIdTest_Success() {
        User user = new User("Linkong344@gmail.com", "Willygodx");

        int userId = 1337;

        when(userService.getUserById(userId)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());

        Mockito.verify(userService, Mockito.times(1)).getUserById(userId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByIdTest_UserNotFound() throws Exception {
        int userId = 228;

        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/get-by-id/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));

        Mockito.verify(userService, Mockito.times(1)).getUserById(userId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByEmailTest_Success() {
        User user = new User("Linkong344@gmail.com", "Willygodx");

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByEmail(user.getEmail());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());

        Mockito.verify(userService, Mockito.times(1)).getUserByEmail(user.getEmail());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByEmailTest_UserNotFound() throws Exception {
        String email = "Linkong344@gmail.com";

        when(userService.getUserByEmail(email)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/get-by-email/{email}", email))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));

        Mockito.verify(userService, Mockito.times(1)).getUserByEmail(email);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByNicknameTest_Success() {
        User user = new User("Linkong344@gmail.com", "Willygodx");

        when(userService.getUserByNickname(user.getNickname())).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByNickname(user.getNickname());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());

        Mockito.verify(userService, Mockito.times(1)).getUserByNickname(user.getNickname());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByNicknameTest_UserNotFound() throws Exception {
        String nickname = "Willygodx";

        when(userService.getUserByNickname(nickname)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/get-by-nickname/{nickname}", nickname))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));

        Mockito.verify(userService, Mockito.times(1)).getUserByNickname(nickname);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserCoordinatesListTest_Success() {
        Page<Coordinates> coordinatesPage = mock(Page.class);

        when(userService.getUserCoordinatesListByNickname("Willygodx", 0, 10)).thenReturn(coordinatesPage);

        UserController userController = new UserController(userService);

        ResponseEntity<Page<Coordinates>> response = userController.getUserCoordinatesList("Willygodx", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coordinatesPage, response.getBody());

        Mockito.verify(userService, Mockito.times(1)).getUserCoordinatesListByNickname("Willygodx", 0, 10);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserCoordinatesListTest_UserNotFound() throws Exception {
        String nickname = "Anna";
        int pageNumber = 0;
        int pageSize = 10;

        when(userService.getUserCoordinatesListByNickname(nickname, pageNumber, pageSize))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/get-coordinates/{nickname}", nickname)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));

        Mockito.verify(userService, Mockito.times(1))
                .getUserCoordinatesListByNickname(nickname, pageNumber, pageSize);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getAllUsersTest() {
        Page<User> userPage = mock(Page.class);

        when(userService.getAllUsers(0, 10)).thenReturn(userPage);

        ResponseEntity<Page<User>> response = userController.getAllUsers(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userPage, response.getBody());

        Mockito.verify(userService, Mockito.times(1)).getAllUsers(0, 10);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void createUserTest_Success() {
        UserDto userDto = new UserDto("Linkong344@gmail.com", "Willygodx");

        ResponseEntity<String> response = userController.createUser(userDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Created successfully!", response.getBody());

        Mockito.verify(userService, Mockito.times(1)).createUser(userDto);
        Mockito.verifyNoMoreInteractions(userService);
    }

}
