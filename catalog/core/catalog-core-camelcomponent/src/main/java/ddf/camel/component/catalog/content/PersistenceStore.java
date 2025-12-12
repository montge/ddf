/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.camel.component.catalog.content;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Interface for storing and loading objects to persistent storage.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface PersistenceStore<K, V> {

  /**
   * Store a value with the given key.
   *
   * @param key the key
   * @param value the value to store
   */
  void store(K key, V value);

  /**
   * Store all key-value pairs.
   *
   * @param map the map of key-value pairs to store
   */
  void storeAll(Map<K, V> map);

  /**
   * Delete the value associated with the given key.
   *
   * @param key the key to delete
   */
  void delete(K key);

  /**
   * Delete all values associated with the given keys.
   *
   * @param keys the keys to delete
   */
  void deleteAll(Collection<K> keys);

  /**
   * Load a value by key. This method may return null if lazy loading is not supported.
   *
   * @param key the key to load
   * @return the loaded value, or null if not found or not supported
   */
  V load(K key);

  /**
   * Load all values for the given keys.
   *
   * @param keys the keys to load
   * @return a map of loaded key-value pairs
   */
  Map<K, V> loadAll(Collection<K> keys);

  /**
   * Load all keys from persistence.
   *
   * @return a set of all persisted keys
   */
  Set<K> loadAllKeys();
}
