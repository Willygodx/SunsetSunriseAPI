package org.lab1java.sunsetsunriseapi.dto;

import org.core.sunsetsunrise.dto.IdDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdDtoTest {

  @Test
  void testConstructorAndGetters() {
    Integer id = 123;

    IdDto idDto = new IdDto(id);

    assertEquals(id, idDto.getId());
  }

  @Test
  void testSetter() {
    Integer id = 123;
    IdDto idDto = new IdDto();

    idDto.setId(id);

    assertEquals(id, idDto.getId());
  }
}

