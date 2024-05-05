package org.core.sunsetsunrise.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * This class represents a generic entity cache.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
@Component
public class EntityCache<K, V> {
  Map<K, V> cacheMap = new ConcurrentHashMap<>();

  private static final int MAX_SIZE = 100;

  /**
   * Puts the specified key-value pair into the cache.
   *
   * @param key the key with which the specified value is to be associated
   * @param value the value to be associated with the specified key
   */
  public void put(K key, V value) {
    cacheMap.put(key, value);
    if (cacheMap.size() >= MAX_SIZE) {
      cacheMap.clear();
    }
  }

  public V get(K key) {
    return cacheMap.get(key);
  }

  public void clear() {
    cacheMap.clear();
  }
}
