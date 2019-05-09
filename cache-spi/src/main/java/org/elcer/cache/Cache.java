package org.elcer.cache;

import java.util.*;

public interface Cache<K, V> {
    /**
     * Gets an entry from the cache.
     *
     * @param key the key whose associated value is to be returned
     * @return the element, or null, if it does not exist.
     * @throws NullPointerException if the key is null.
     */
    V get(K key)  ;

    /**
     * Associates the specified value with the specified key in the cache.
     * <p>
     * If the {@link Cache} previously contained a mapping for the key, the old
     * value is replaced by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param val value to be associated with the specified key.
     * @throws NullPointerException if key is null or if value is null.
     */
    void put(K key, V val)  ;

    /**
     * Determines if the {@link Cache} contains an entry for the specified key.
     * <p>
     * More formally, returns <tt>true</tt> if and only if this cache contains a
     * mapping for a key <tt>k</tt> such that <tt>key.equals(k)</tt>.
     * (There can be at most one such mapping)
     *
     * @param key key whose presence in this cache is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
     */
    boolean containsKey(K key)  ;

    /**
     * @return The name of the cache.
     */
    String getName();




    /**
     * Gets a collection of entries from the {@link Cache}, returning them as
     * {@link Map} of the values associated with the set of keys requested.
     *
     * @param keys The keys whose associated values are to be returned.
     * @return A map of entries that were found for the given keys. Keys not found
     * in the cache are not in the returned map.
     */
    Map<K, V> getAll(Set<? extends K> keys) ;

    /**
     * Copies all of the entries from the specified map to the {@link Cache}.
     * <p>
     * The effect of this call is equivalent to that of calling
     * {@link #put(Object, Object) put(k, v)} on this cache once for each mapping
     * from key <tt>k</tt> to value <tt>v</tt> in the specified map.
     * <p>
     * The order in which the individual puts occur is undefined.
     * <p>
     * The behavior of this operation is undefined if entries in the cache
     * corresponding to entries in the map are modified or removed while this
     * operation is in progress. or if map is modified while the operation is in
     * progress.
     * <p>
     *
     * @param map Mappings to be stored in this cache.
     */
    void putAll(Map<? extends K, ? extends V> map);

    /**
     * Atomically replaces the entry for a key only if currently mapped to a given value.
     * <p>
     * This is equivalent to:
     * <pre><code>
     * if (cache.containsKey(key) &amp;&amp; equals(cache.get(key), oldValue)) {
     *  cache.put(key, newValue);
     * return true;
     * } else {
     *  return false;
     * }
     * </code></pre>
     * except that the action is performed atomically.
     *
     * @param key Key with which the specified value is associated.
     * @param oldVal Value expected to be associated with the specified key.
     * @param newVal Value to be associated with the specified key.
     * @return <tt>true</tt> if the value was replaced
     */
    boolean replace(K key, V oldVal, V newVal);

    /**
     * Atomically replaces the entry for a key only if currently mapped to some
     * value.
     * <p>
     * This is equivalent to
     * <pre><code>
     * if (cache.containsKey(key)) {
     *   cache.put(key, value);
     *   return true;
     * } else {
     *   return false;
     * }</code></pre>
     * except that the action is performed atomically.
     *
     * @param key The key with which the specified value is associated.
     * @param val The value to be associated with the specified key.
     * @return <tt>true</tt> if the value was replaced.
     */
    boolean replace(K key, V val);

    /**
     * Removes the mapping for a key from this cache if it is present.
     * <p>
     * More formally, if this cache contains a mapping from key <tt>k</tt> to value <tt>v</tt> such that
     * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping is removed.
     * (The cache can contain at most one such mapping.)
     *
     * <p>Returns <tt>true</tt> if this cache previously associated the key, or <tt>false</tt> if the cache
     * contained no mapping for the key.
     * <p>
     * The cache will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key Key whose mapping is to be removed from the cache.
     * @return <tt>false</tt> if there was no matching key.
     */
    boolean remove(K key);

    /**
     * Atomically removes the mapping for a key only if currently mapped to the given value.
     * <p>
     * This is equivalent to:
     * <pre><code>
     * if (cache.containsKey(key) &amp;&amp; equals(cache.get(key), oldValue) {
     *   cache.remove(key);
     *   return true;
     * } else {
     *   return false;
     * }
     * </code></pre>
     * except that the action is performed atomically.
     *
     * @param key Key whose mapping is to be removed from the cache.
     * @param oldVal Value expected to be associated with the specified key.
     * @return <tt>false</tt> if there was no matching key.
     */
    boolean remove(K key, V oldVal);

    /**
     * Removes entries for the specified keys.
     * <p>
     * The order in which the individual entries are removed is undefined.
     *
     * @param keys The keys to remove.
     */
    void removeAll(Set<? extends K> keys);

    /**
     * Removes all of the mappings from this cache.
     * <p>
     * The order that the individual entries are removed is undefined.
     */
    void removeAll();

    /**
     * Associates the specified value with the specified key in this cache, returning an existing value if one existed.
     * <p>
     * If the cache previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A cache
     * <tt>c</tt> is said to contain a mapping for a key <tt>k</tt> if and only
     * if {@link #containsKey(Object) c.containsKey(k)} would return
     * <tt>true</tt>.)
     * <p>
     * The previous value is returned, or null if there was no value associated
     * with the key previously.
     *
     * @param key Key with which the specified value is to be associated.
     * @param val Value to be associated with the specified key.
     * @return The value associated with the key at the start of the operation or
     * null if none was associated.
     */
    V getAndPut(K key, V val);

    /**
     * Atomically removes the entry for a key only if currently mapped to some value.
     * <p>
     * This is equivalent to:
     * <pre><code>
     * if (cache.containsKey(key)) {
     *   V oldValue = cache.get(key);
     *   cache.remove(key);
     *   return oldValue;
     * } else {
     *   return null;
     * }
     * </code></pre>
     * except that the action is performed atomically.
     *
     * @param key Key with which the specified value is associated.
     * @return The value if one existed or null if no mapping existed for this key.
     */
    V getAndRemove(K key);

    /**
     * Atomically replaces the value for a given key if and only if there is a value currently mapped by the key.
     * <p>
     * This is equivalent to
     * <pre><code>
     * if (cache.containsKey(key)) {
     *   V oldValue = cache.get(key);
     *   cache.put(key, value);
     *   return oldValue;
     * } else {
     *   return null;
     * }
     * </code></pre>
     * except that the action is performed atomically.
     *
     * @param key Key with which the specified value is associated.
     * @param val Value to be associated with the specified key.
     * @return The previous value associated with the specified key, or
     * <tt>null</tt> if there was no mapping for the key.
     */
    V getAndReplace(K key, V val);

    /**
     * Atomically associates the specified key with the given value if it is not already associated with a value.
     * <p>
     * This is equivalent to:
     * <pre><code>
     * if (!cache.containsKey(key)) {}
     *   cache.put(key, value);
     *   return true;
     * } else {
     *   return false;
     * }
     * </code></pre>
     * except that the action is performed atomically.
     *
     * @param key Key with which the specified value is to be associated.
     * @param val Value to be associated with the specified key.
     * @return <tt>true</tt> if a value was set.
     */
    boolean putIfAbsent(K key, V val);

    /**
     * Clears the contents of the cache.
     */
    void clear();




}
