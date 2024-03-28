package org.lab1java.sunsetsunriseapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab1java.sunsetsunriseapi.cache.EntityCache;
import org.lab1java.sunsetsunriseapi.dao.CoordinatesRepository;
import org.lab1java.sunsetsunriseapi.dao.CountryRepository;
import org.lab1java.sunsetsunriseapi.dao.UserRepository;
import org.lab1java.sunsetsunriseapi.dto.CoordinatesDto;
import org.lab1java.sunsetsunriseapi.dto.RequestDto;
import org.lab1java.sunsetsunriseapi.dto.ResponseDto;
import org.lab1java.sunsetsunriseapi.entity.Coordinates;
import org.lab1java.sunsetsunriseapi.entity.Country;
import org.lab1java.sunsetsunriseapi.entity.User;
import org.lab1java.sunsetsunriseapi.exception.BadRequestErrorException;
import org.lab1java.sunsetsunriseapi.exception.ResourceNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoordinatesServiceTest {
    @Mock
    private EntityCache<Integer, Object> cacheMap;
    @Mock
    private CoordinatesRepository coordinatesRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ApiService externalApiService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CoordinatesService coordinatesService;

    @Test
    void testGetCoordinatesInfo() throws Exception {
        User user = new User("Linkong344@gmail.com", "Willygodx");
        RequestDto request = new RequestDto(43.117122, 131.896018, LocalDate.of(2024, 3, 26));
        ResponseDto expectedResponse = new ResponseDto(LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Russia", "Vladivostok");
        Coordinates coordinates = new Coordinates(43.117122, 131.896018, LocalDate.of(2024, 3, 26), LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Vladivostok");

        when(coordinatesRepository.findByLatitudeAndLongitudeAndDate(43.117122, 131.896018, LocalDate.of(2024, 3, 26))).thenReturn(Optional.of(coordinates));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Set<Coordinates> coordinatesSet = new HashSet<>();
        coordinatesSet.add(coordinates);
        user.setCoordinatesSet(coordinatesSet);
        coordinates.setCountry(new Country("Russia"));

        ResponseDto responseDto = coordinatesService.getCoordinatesInfo(1, request);

        assertEquals(responseDto.getSunrise(), expectedResponse.getSunrise());
        assertEquals(responseDto.getSunset(), expectedResponse.getSunset());
        assertEquals(responseDto.getTimeZone(), expectedResponse.getTimeZone());
        assertEquals(responseDto.getCountry(), expectedResponse.getCountry());
        assertEquals(responseDto.getCity(), expectedResponse.getCity());
        verify(coordinatesRepository, times(1)).findByLatitudeAndLongitudeAndDate(anyDouble(), anyDouble(), any(LocalDate.class));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetCheckedResponseFromApi() throws JsonProcessingException {
        RequestDto requestDto = new RequestDto(43.117122, 131.896018, LocalDate.of(2024, 3, 26));

        ResponseDto expectedResponseDto = new ResponseDto(LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Russia", "Vladivostok");

        when(externalApiService.getApiResponse(requestDto)).thenReturn("{\"results\":{\"sunrise\":\"9:03:07 PM\",\"sunset\":\"9:32:57 AM\",\"solar_noon\":\"3:18:02 AM\",\"day_length\":\"12:29:50\",\"civil_twilight_begin\":\"8:36:04 PM\",\"civil_twilight_end\":\"9:59:59 AM\",\"nautical_twilight_begin\":\"8:02:28 PM\",\"nautical_twilight_end\":\"10:33:36 AM\",\"astronomical_twilight_begin\":\"7:27:51 PM\",\"astronomical_twilight_end\":\"11:08:12 AM\"},\"status\":\"OK\",\"tzid\":\"UTC\"}");
        when(externalApiService.extractCoordinatesInfoFromApiResponse(anyString())).thenReturn(expectedResponseDto);
        when(externalApiService.getTimeZone(43.117122, 131.896018)).thenReturn("Asia/Vladivostok");
        when(externalApiService.getCountry(43.117122, 131.896018)).thenReturn("RU\r\n");
        when(externalApiService.getCountryNameByCode("RU")).thenReturn("Russia");
        when(coordinatesService.getCountryName(requestDto)).thenReturn("Russia");

        ResponseDto actualResponseDto = coordinatesService.getCheckedResponseFromApi(requestDto);

        assertEquals(expectedResponseDto.getSunrise(), actualResponseDto.getSunrise());
        assertEquals(expectedResponseDto.getSunset(), actualResponseDto.getSunset());
        assertEquals(expectedResponseDto.getTimeZone(), actualResponseDto.getTimeZone());
        assertEquals(expectedResponseDto.getCountry(), actualResponseDto.getCountry());
        assertEquals(expectedResponseDto.getCity(), actualResponseDto.getCity());
    }

    @Test
    void testCoordinatesSave_UserExists_CountryExists() {
        User user = new User("Linkong344@gmail.com", "Willygodx");
        user.setId(1);
        Coordinates coordinates = new Coordinates(10.0, 20.0, LocalDate.now(), LocalTime.now(), LocalTime.now(), "UTC+0", "Moscow");
        user.setCoordinatesSet(new HashSet<>());

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(countryRepository.findByName(anyString())).thenReturn(Optional.of(new Country("Russia")));

        coordinatesService.coordinatesSave(1, "Russia", coordinates);

        assertTrue(user.getCoordinatesSet().contains(coordinates));
        verify(coordinatesRepository, times(1)).save(coordinates);
    }

    @Test
    void testCoordinatesSave_UserNotFound() {
        String countryName = "Russia";

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> coordinatesService.coordinatesSave(1, countryName, new Coordinates()));

        verify(userRepository).findById(1);
        verifyNoInteractions(countryRepository);
        verifyNoInteractions(coordinatesRepository);
    }

    @Test
    void testGetCoordinatesEntity() {
        RequestDto requestDto = mock(RequestDto.class);
        ResponseDto responseDto = mock(ResponseDto.class);
        double latitude = 43.117122;
        double longitude = 131.896018;
        LocalDate date = LocalDate.of(2024, 3, 26);
        LocalTime sunrise = LocalTime.of(7, 3, 7);
        LocalTime sunset = LocalTime.of(19, 32, 57);
        String timeZone = "UTC+3";
        String city = "Moscow";

        when(requestDto.getLatitude()).thenReturn(latitude);
        when(requestDto.getLongitude()).thenReturn(longitude);
        when(requestDto.getDate()).thenReturn(date);
        when(responseDto.getSunrise()).thenReturn(sunrise);
        when(responseDto.getSunset()).thenReturn(sunset);
        when(responseDto.getTimeZone()).thenReturn(timeZone);
        when(responseDto.getCity()).thenReturn(city);

        Coordinates coordinates = coordinatesService.getCoordinatesEntity(requestDto, responseDto);

        assertEquals(latitude, coordinates.getLatitude());
        assertEquals(longitude, coordinates.getLongitude());
        assertEquals(date, coordinates.getDate());
        assertEquals(sunrise, coordinates.getSunrise());
        assertEquals(sunset, coordinates.getSunset());
        assertEquals(timeZone, coordinates.getTimeZone());
        assertEquals(city, coordinates.getCity());
    }

    @Test
    void testGetCoordinatesInfoBySunriseStartingHour_CoordinatesInCache() {
        Page<Coordinates> cachedCoordinatesPage = new PageImpl<>(Collections.singletonList(new Coordinates()));
        when(cacheMap.get(Objects.hash(6, 0, 10, 61 * 32))).thenReturn(cachedCoordinatesPage);

        Page<Coordinates> result = coordinatesService.getCoordinatesInfoBySunriseStartingHour(6, 0, 10);

        assertNotNull(result);
        assertEquals(cachedCoordinatesPage, result);
        verify(coordinatesRepository, never()).findBySunriseStartingHour(eq(6), any(Pageable.class));
    }

    @Test
    void testGetCoordinatesInfoBySunriseStartingHour_CoordinatesNotInCache() {
        Page<Coordinates> coordinatesPage = new PageImpl<>(Collections.singletonList(new Coordinates()));
        when(cacheMap.get(Objects.hash(6, 0, 10, 61 * 32))).thenReturn(null);
        when(coordinatesRepository.findBySunriseStartingHour(eq(6), any(Pageable.class))).thenReturn(coordinatesPage);

        Page<Coordinates> result = coordinatesService.getCoordinatesInfoBySunriseStartingHour(6, 0, 10);

        assertNotNull(result);
        assertEquals(coordinatesPage, result);
        verify(cacheMap, times(1)).put(Objects.hash(6, 0, 10, 61 * 32), coordinatesPage);
    }

    @Test
    void testGetCoordinatesInfoBySunsetStartingHour_CoordinatesInCache() {
        Page<Coordinates> cachedCoordinatesPage = new PageImpl<>(Collections.singletonList(new Coordinates()));
        when(cacheMap.get(Objects.hash(18, 0, 10, 62 * 33))).thenReturn(cachedCoordinatesPage);

        Page<Coordinates> result = coordinatesService.getCoordinatesInfoBySunsetStartingHour(18, 0, 10);

        assertNotNull(result);
        assertEquals(cachedCoordinatesPage, result);
        verify(coordinatesRepository, never()).findBySunsetStartingHour(eq(18), any(Pageable.class));
    }

    @Test
    void testGetCoordinatesInfoBySunsetStartingHour_CoordinatesNotInCache() {
        Page<Coordinates> coordinatesPage = new PageImpl<>(Collections.singletonList(new Coordinates()));
        when(cacheMap.get(Objects.hash(18, 0, 10, 62 * 33))).thenReturn(null);
        when(coordinatesRepository.findBySunsetStartingHour(eq(18), any(Pageable.class))).thenReturn(coordinatesPage);

        Page<Coordinates> result = coordinatesService.getCoordinatesInfoBySunsetStartingHour(18, 0, 10);

        assertNotNull(result);
        assertEquals(coordinatesPage, result);
        verify(cacheMap, times(1)).put(Objects.hash(18, 0, 10, 62 * 33), coordinatesPage);
    }

    @Test
    void testGetUsersFromCoordinates_DataInCache() {
        Page<User> cachedUserPage = new PageImpl<>(Collections.singletonList(new User()));
        when(cacheMap.get(Objects.hash(1, 0, 10, 63 * 34))).thenReturn(cachedUserPage);

        Page<User> result = coordinatesService.getUsersFromCoordinates(1, 0, 10);

        assertNotNull(result);
        assertEquals(cachedUserPage, result);
        verify(coordinatesRepository, never()).findById(anyLong());
        verify(userRepository, never()).findByCoordinatesSetContaining(any(Coordinates.class), any(Pageable.class));
    }

    @Test
    void testGetUsersFromCoordinates_DataNotInCache() {
        Coordinates coordinates = new Coordinates(43.117122, 131.896018, LocalDate.of(2024, 3, 26), LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Vladivostok");
        coordinates.setCountry(new Country("Russia"));
        Page<User> userPage = new PageImpl<>(Collections.singletonList(new User()));
        when(cacheMap.get(Objects.hash(1, 0, 10, 63 * 34))).thenReturn(null);
        when(coordinatesRepository.findById(1L)).thenReturn(Optional.of(coordinates));
        when(userRepository.findByCoordinatesSetContaining(eq(coordinates), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = coordinatesService.getUsersFromCoordinates(1, 0, 10);

        assertNotNull(result);
        assertEquals(userPage, result);
        verify(cacheMap, times(1)).put(Objects.hash(1, 0, 10, 63 * 34), userPage);
    }

    @Test
    void testGetUsersFromCoordinates_CoordinatesNotFound() {
        when(cacheMap.get(Objects.hash(1, 0, 10, 63 * 34))).thenReturn(null);
        when(coordinatesRepository.findById((1L))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> coordinatesService.getUsersFromCoordinates(1, 0, 10));
    }

    @Test
    void testGetAllCoordinatesInfo() {
        Page<CoordinatesDto> cachedCoordinatesPage = new PageImpl<>(Collections.singletonList(new CoordinatesDto()));
        when(cacheMap.get(Objects.hash(0, 10, 64 * 35))).thenReturn(cachedCoordinatesPage);

        Page<CoordinatesDto> result = coordinatesService.getAllCoordinatesInfo(0, 10);

        assertNotNull(result);
        assertEquals(cachedCoordinatesPage, result);
        verify(coordinatesRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void testMapToCoordinatesDto() {
        Coordinates coordinates = new Coordinates(43.117122, 131.896018, LocalDate.of(2024, 3, 26), LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Vladivostok");

        Country countryMock = mock(Country.class);
        when(countryMock.getName()).thenReturn("Russia");
        coordinates.setCountry(countryMock);

        CoordinatesDto coordinatesDto = coordinatesService.mapToCoordinatesDTO(coordinates);

        assertEquals(coordinates.getLatitude(), coordinatesDto.getLatitude());
        assertEquals(coordinates.getLongitude(), coordinatesDto.getLongitude());
        assertEquals(coordinates.getDate(), coordinatesDto.getDate());
        assertEquals(coordinates.getSunrise(), coordinatesDto.getSunrise());
        assertEquals(coordinates.getSunset(), coordinatesDto.getSunset());
        assertEquals(coordinates.getTimeZone(), coordinatesDto.getTimeZone());
        assertEquals(coordinates.getCity(), coordinatesDto.getCity());
        assertEquals(coordinates.getCountry().getName(), coordinatesDto.getCountry());
    }

    @Test
    void testUpdateCoordinatesInfo_Success() {
        Coordinates coordinates = new Coordinates(43.117122, 131.896018, LocalDate.of(2024, 3, 26), LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Vladivostok");
        CoordinatesDto updateDto = mock(CoordinatesDto.class);

        when(coordinatesRepository.findById(1L)).thenReturn(Optional.of(coordinates));
        when(updateDto.getCity()).thenReturn("Novosibirsk");
        when(updateDto.getLongitude()).thenReturn(82.9179378);
        when(updateDto.getLatitude()).thenReturn(55.0291091);
        when(updateDto.getSunrise()).thenReturn(LocalTime.of(7, 12, 19));
        when(updateDto.getSunset()).thenReturn(LocalTime.of(19, 55, 29));
        when(updateDto.getDate()).thenReturn(LocalDate.of(2024, 3, 26));
        when(updateDto.getTimeZone()).thenReturn("Asia/Novosibirsk");
        when(updateDto.getCountry()).thenReturn("Russia");

        Coordinates updatedCoordinates = coordinatesService.updateCoordinatesInfo(1L, updateDto);

        verify(coordinatesRepository, times(1)).findById(1L);
        verify(coordinatesRepository, times(1)).save(updatedCoordinates);
    }

    @Test
    void testUpdateCoordinatesInfo_UserNotFound() {
        CoordinatesDto coordinatesDto = new CoordinatesDto(43.117122, 131.896018, LocalDate.of(2024, 3, 26), LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Russia", "Vladivostok");

        when(coordinatesRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coordinatesService.updateCoordinatesInfo(1L, coordinatesDto));
    }

    @Test
    void testUpdateCoordinatesInfo_Failure() {
        Coordinates coordinates = new Coordinates(43.117122, 131.896018, LocalDate.of(2024, 3, 26), LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Vladivostok");
        coordinates.setCountry(new Country("Russia"));
        CoordinatesDto updateDto = new CoordinatesDto(43.117122, 131.896018, LocalDate.of(2024, 3, 26), LocalTime.of(7, 3, 7), LocalTime.of(19, 32, 57), "Asia/Vladivostok", "Russia", "Vladivostok");

        when(coordinatesRepository.findById(1L)).thenReturn(Optional.of(coordinates));
        doThrow(new RuntimeException()).when(coordinatesRepository).save(coordinates);

        assertThrows(BadRequestErrorException.class, () -> coordinatesService.updateCoordinatesInfo(1L, updateDto));
    }

    @Test
    void testCreateCoordinatesInfoBulk_NullList() {
        assertThrows(ResourceNotFoundException.class, () -> coordinatesService.createCoordinatesInfoBulk(null));
    }

    @Test
    void testCreateCoordinatesInfoBulk_EmptyList() {
        List<RequestDto> requestDtoList = new ArrayList<>();

        assertThrows(ResourceNotFoundException.class, () -> coordinatesService.createCoordinatesInfoBulk(requestDtoList));
    }

    @Test
    void testDeleteCoordinatesInfoFromDatabase_CoordinatesFound() {
        when(coordinatesRepository.existsById(1L)).thenReturn(true);
        coordinatesService.deleteCoordinatesInfoFromDatabase(1L);

        verify(coordinatesRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCoordinatesInfoFromDatabase_CoordinatesNotFound() {
        when(coordinatesRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> coordinatesService.deleteCoordinatesInfoFromDatabase(1L));
    }
}
