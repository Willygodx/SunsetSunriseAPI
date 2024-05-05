package org.lab1java.sunsetsunriseapi.cache;

import org.core.sunsetsunrise.cache.EntityCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntityCacheTest {
  private EntityCache<Integer, String> cacheMap;

  @BeforeEach
  void setUp() {
    cacheMap = new EntityCache<>();
  }

  @Test
  void testPutAndGet_Success() {
    int key = 123;
    String value = "hey";

    cacheMap.put(key, value);
    String retrievedValue = cacheMap.get(key);

    assertEquals(retrievedValue, value);
  }

  @Test
  void testGet_NoKey() {
    assertNull(cacheMap.get(133));
  }

  @Test
  void testClear() {
    cacheMap.put(123, "value");
    cacheMap.put(124, "value1");

    cacheMap.clear();

    assertNull(cacheMap.get(123));
    assertNull(cacheMap.get(124));
  }
}
