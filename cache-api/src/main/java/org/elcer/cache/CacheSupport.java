package org.elcer.cache;

public interface CacheSupport {

    <K, V> Cache<K, V> getOrCreateCache(String name);

}
