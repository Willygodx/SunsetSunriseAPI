package org.lab1java.sunsetsunriseapi.service;

import jakarta.transaction.Transactional;
import org.core.sunsetsunrise.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.core.sunsetsunrise.cache.EntityCache;
import org.core.sunsetsunrise.dao.CoordinatesRepository;
import org.core.sunsetsunrise.dao.UserRepository;
import org.core.sunsetsunrise.dto.UserDto;
import org.core.sunsetsunrise.entity.Coordinates;
import org.core.sunsetsunrise.entity.User;
import org.core.sunsetsunrise.exception.BadRequestErrorException;
import org.core.sunsetsunrise.exception.InvalidDataException;
import org.core.sunsetsunrise.exception.ResourceNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private CoordinatesRepository coordinatesRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EntityCache<Integer, Object> cacheMap;

  @InjectMocks
  private UserService userService;

  @Test
  void testGetUserById_UserInCache() {
    User cachedUser = new User("Linkong344@gmail.com", "Willygodx", "12345");
    when(cacheMap.get(Objects.hash(1, 2 * 31))).thenReturn(cachedUser);

    User result = userService.getUserById(1);

    assertNotNull(result);
    assertEquals(cachedUser, result);
    verify(userRepository, never()).findById(anyInt());
  }

  @Test
  void testGetUserById_UserNotInCache() {
    User userFromDatabase = new User("Linkong344@gmail.com", "Willygodx", "12345");
    when(cacheMap.get(Objects.hash(1, 2 * 31))).thenReturn(null);
    when(userRepository.findById(1)).thenReturn(Optional.of(userFromDatabase));

    User result = userService.getUserById(1);

    assertNotNull(result);
    assertEquals(userFromDatabase, result);
    verify(cacheMap, times(1)).put(Objects.hash(1, 2 * 31), userFromDatabase);
  }

  @Test
  void testGetUserById_UserNotFound() {
    when(cacheMap.get(Objects.hash(1, 2 * 31))).thenReturn(null);
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
  }

  @Test
  void testGetUserByEmail_UserInCache() {
    User cachedUser = new User("Linkong344@gmail.com", "Willygodx", "12345");
    when(cacheMap.get(Objects.hash("Linkong344@gmail.com", 3 * 32))).thenReturn(cachedUser);

    User result = userService.getUserByEmail("Linkong344@gmail.com");

    assertNotNull(result);
    assertEquals(cachedUser, result);
    verify(userRepository, never()).findByEmail(anyString());
  }

  @Test
  void testGetUserByEmail_UserNotInCache() {
    User userFromDatabase = new User("Linkong344@gmail.com", "Willygodx", "12345");
    when(cacheMap.get(Objects.hash("Linkong344@gmail.com", 3 * 32))).thenReturn(null);
    when(userRepository.findByEmail("Linkong344@gmail.com")).thenReturn(
        Optional.of(userFromDatabase));

    User result = userService.getUserByEmail("Linkong344@gmail.com");

    assertNotNull(result);
    assertEquals(userFromDatabase, result);
    verify(cacheMap, times(1)).put(Objects.hash("Linkong344@gmail.com", 3 * 32), userFromDatabase);
  }

  @Test
  void testGetUserByEmail_UserNotFound() {
    when(cacheMap.get(Objects.hash("Linkong344@gmail.com", 3 * 32))).thenReturn(null);
    when(userRepository.findByEmail("Linkong344@gmail.com")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.getUserByEmail("Linkong344@gmail.com"));
  }

  @Test
  void testGetUserByNickname_UserInCache() {
    User cachedUser = new User("Linkong344@gmail.com", "Willygodx", "12345");
    when(cacheMap.get(Objects.hash("Willygodx", 4 * 33))).thenReturn(cachedUser);

    User result = userService.getUserByNickname("Willygodx");

    assertNotNull(result);
    assertEquals(cachedUser, result);
    verify(userRepository, never()).findByNickname(anyString());
  }

  @Test
  void testGetUserByNickname_UserNotInCache() {
    User userFromDatabase = new User("Linkong344@gmail.com", "Willygodx", "12345");
    when(cacheMap.get(Objects.hash("Willygodx", 4 * 33))).thenReturn(null);
    when(userRepository.findByNickname("Willygodx")).thenReturn(Optional.of(userFromDatabase));

    User result = userService.getUserByNickname("Willygodx");

    assertNotNull(result);
    assertEquals(userFromDatabase, result);
    verify(cacheMap, times(1)).put(Objects.hash("Willygodx", 4 * 33), userFromDatabase);
  }

  @Test
  void testGetUserByNickname_UserNotFound() {
    when(cacheMap.get(Objects.hash("Willygodx", 4 * 33))).thenReturn(null);
    when(userRepository.findByNickname("Willygodx")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserByNickname("Willygodx"));
  }

  @Test
  void testGetUserCoordinatesListByNickname_DataNotInCache() {
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");
    user.setId(1);
    Page<Coordinates> coordinatesPage =
        new PageImpl<>(Collections.singletonList(new Coordinates()));
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(coordinatesRepository.findByUserSetContaining(eq(user), any(Pageable.class))).thenReturn(
        coordinatesPage);

    Page<Coordinates> result = userService.getUserCoordinatesListById(1, 0, 10);

    assertNotNull(result);
    assertEquals(coordinatesPage, result);
  }

  @Test
  void testGetUserCoordinatesListByNickname_UserNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.getUserCoordinatesListById(1, 0, 10));
  }

  @Test
  void testGetAllUsers_DataInCache() {
    Page<User> cachedUserPage = new PageImpl<>(Collections.singletonList(new User()));
    when(cacheMap.get(Objects.hash(0, 10, 6 * 35))).thenReturn(cachedUserPage);

    Page<User> result = userService.getAllUsers(0, 10);

    assertNotNull(result);
    assertEquals(cachedUserPage, result);
    verify(userRepository, never()).findAll(any(Pageable.class));
  }

  @Test
  void testGetAllUsers_DataNotInCache() {
    Page<User> userPage = new PageImpl<>(Collections.singletonList(new User()));
    when(cacheMap.get(Objects.hash(0, 10, 6 * 35))).thenReturn(null);
    when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

    Page<User> result = userService.getAllUsers(0, 10);

    assertNotNull(result);
    assertEquals(userPage, result);
    verify(cacheMap, times(1)).put(Objects.hash(0, 10, 6 * 35), userPage);
  }

  @Test
  void testCreateUser_Success() {
    UserDto userDto = new UserDto("Linkong344@gmail.com", "Willygodx", "12345");

    userService.createUser(userDto);

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testCreateUser_InvalidData() {
    UserDto userDto = new UserDto(null, "Willygodx", "12345");

    assertThrows(InvalidDataException.class, () -> userService.createUser(userDto));
  }

  @Test
  void testCreateUser_Failure() {
    UserDto userDto = new UserDto("Linkong344@gmail.com", "Willygodx", "12345");
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");

    doThrow(new RuntimeException()).when(userRepository).save(user);

    assertThrows(BadRequestErrorException.class, () -> userService.createUser(userDto));
  }

  @Test
  void testCreateUsersBulk_Success() {
    List<UserDto> userDtoList = new ArrayList<>();
    userDtoList.add(new UserDto("Linkong344@gmail.com", "Willygodx", "12345"));
    userDtoList.add(new UserDto("Enotland34@yandex.ru", "JohnDoe", "12345"));

    userService.createUsersBulk(userDtoList);

    verify(userRepository, times(2)).save(any());
  }

  @Test
  void testCreateUsersBulk_NullList() {
    assertThrows(ResourceNotFoundException.class, () -> userService.createUsersBulk(null));
  }

  @Test
  void testCreateUsersBulk_EmptyList() {
    List<UserDto> userDtoList = new ArrayList<>();

    assertThrows(ResourceNotFoundException.class, () -> userService.createUsersBulk(userDtoList));
  }

  @Test
  void testUpdateUserById_Success() {
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");
    UserDto updateDto = mock(UserDto.class);

    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(updateDto.getEmail()).thenReturn("Enotland34@yandex.ru");
    when(updateDto.getNickname()).thenReturn("JohnDoe");

    User updatedUser = userService.updateUserById(1, updateDto);

    assertEquals(updatedUser.getEmail(), updateDto.getEmail());
    assertEquals(updatedUser.getNickname(), updateDto.getNickname());
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).save(updatedUser);
  }

  @Test
  void testUpdateUserById_UserNotFound() {
    UserDto updateDto = new UserDto("Linkong344@gmail.com", "Willygodx", "12345");

    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.updateUserById(1, updateDto));
  }

  @Test
  void testUpdateUserById_Failure() {
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");
    UserDto updateDto = new UserDto("Enotland34@yandex.ru", "JohnDoe", "12345");

    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    doThrow(new RuntimeException()).when(userRepository).save(user);

    assertThrows(BadRequestErrorException.class, () -> userService.updateUserById(1, updateDto));
  }

  @Test
  void testUpdateUserByNickname_Success() {
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");
    UserDto updateDto = mock(UserDto.class);

    when(userRepository.findByNickname("Willygodx")).thenReturn(Optional.of(user));
    when(updateDto.getEmail()).thenReturn("Enotland34@yandex.ru");
    when(updateDto.getNickname()).thenReturn("JohnDoe");

    User updatedUser = userService.updateUserByNickname("Willygodx", updateDto);

    assertEquals(updatedUser.getEmail(), updateDto.getEmail());
    assertEquals(updatedUser.getNickname(), updateDto.getNickname());
    verify(userRepository, times(1)).findByNickname("Willygodx");
    verify(userRepository, times(1)).save(updatedUser);
  }

  @Test
  void testUpdateUserByNickname_UserNotFound() {
    UserDto updateDto = new UserDto("Linkong344@gmail.com", "Willygodx", "12345");

    when(userRepository.findByNickname("Willygodx")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.updateUserByNickname("Willygodx", updateDto));
  }

  @Test
  void testUpdateUserByNickname_Failure() {
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");
    UserDto updateDto = new UserDto("Enotland34@yandex.ru", "JohnDoe", "12345");

    when(userRepository.findByNickname("Willygodx")).thenReturn(Optional.of(user));
    doThrow(new RuntimeException()).when(userRepository).save(user);

    assertThrows(BadRequestErrorException.class,
        () -> userService.updateUserByNickname("Willygodx", updateDto));
  }

  @Test
  void testUpdateUserByEmail_Success() {
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");
    UserDto updateDto = mock(UserDto.class);

    when(userRepository.findByEmail("Linkong344@gmail.com")).thenReturn(Optional.of(user));
    when(updateDto.getEmail()).thenReturn("Enotland34@yandex.ru");
    when(updateDto.getNickname()).thenReturn("JohnDoe");

    User updatedUser = userService.updateUserByEmail("Linkong344@gmail.com", updateDto);

    assertEquals(updatedUser.getEmail(), updateDto.getEmail());
    assertEquals(updatedUser.getNickname(), updateDto.getNickname());
    verify(userRepository, times(1)).findByEmail("Linkong344@gmail.com");
    verify(userRepository, times(1)).save(updatedUser);
  }

  @Test
  void testUpdateUserByEmail_UserNotFound() {
    UserDto updateDto = new UserDto("Linkong344@gmail.com", "Willygodx", "12345");

    when(userRepository.findByEmail("Linkong344@gmail.com")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.updateUserByEmail("Linkong344@gmail.com", updateDto));
  }

  @Test
  void testUpdateUserByEmail_Failure() {
    User user = new User("Linkong344@gmail.com", "Willygodx", "12345");
    UserDto updateDto = new UserDto("Enotland34@yandex.ru", "JohnDoe", "12345");

    when(userRepository.findByEmail("Linkong344@gmail.com")).thenReturn(Optional.of(user));
    doThrow(new RuntimeException()).when(userRepository).save(user);

    assertThrows(BadRequestErrorException.class,
        () -> userService.updateUserByEmail("Linkong344@gmail.com", updateDto));
  }

  @Test
  void testDeleteUserFromDatabaseById_UserFound() {
    when(userRepository.existsById(1)).thenReturn(true);
    userService.deleteUserFromDatabaseById(1);

    verify(userRepository, times(1)).deleteById(1);
  }

  @Test
  void testDeleteUserFromDatabaseById_UserNotFound() {
    when(userRepository.existsById(1)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserFromDatabaseById(1));
  }

  @Test
  void testDeleteUsersCoordinatesInformation_UserNotFound() {
    int userId = 1;
    long coordinatesId = 1;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.deleteUsersCoordinatesInformation(userId, coordinatesId));
    verify(userRepository, times(1)).findById(userId);
    verifyNoInteractions(coordinatesRepository);
  }

  @Test
  void testDeleteUsersCoordinatesInformation_CoordinatesNotFound() {
    int userId = 1;
    long coordinatesId = 1;
    User user = new User();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(coordinatesRepository.findById(coordinatesId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.deleteUsersCoordinatesInformation(userId, coordinatesId));
    verify(userRepository, times(1)).findById(userId);
    verify(coordinatesRepository, times(1)).findById(coordinatesId);
  }

  @Test
  @Transactional
  void testDeleteUsersCoordinatesInformation_CoordinatesNotBelongToUser() {
    int userId = 1;
    long coordinatesId = 1;
    User user = new User();
    Coordinates coordinates = new Coordinates();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(coordinatesRepository.findById(coordinatesId)).thenReturn(Optional.of(coordinates));

    assertThrows(NullPointerException.class,
        () -> userService.deleteUsersCoordinatesInformation(userId, coordinatesId));
    verify(userRepository, times(1)).findById(userId);
    verify(coordinatesRepository, times(1)).findById(coordinatesId);
  }

  @Test
  void testCheckLogin_Success() {
    String nickname = "testUser";
    String password = "testPassword";
    int userId = 1;
    User user = new User();
    user.setId(userId);
    when(userRepository.findByNicknameAndPassword(nickname, password)).thenReturn(Optional.of(user));

    int result = userService.checkLogin(nickname, password);

    assertEquals(userId, result);
  }

  @Test
  void testCheckLogin_UserNotFound() {
    String nickname = "testUser";
    String password = "testPassword";
    when(userRepository.findByNicknameAndPassword(nickname, password)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.checkLogin(nickname, password));
  }
}