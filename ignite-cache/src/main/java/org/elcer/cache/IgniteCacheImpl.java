package org.elcer.cache;

import org.apache.ignite.client.ClientCache;
import org.elcer.cache.Cache;

import java.util.Map;
import java.util.Set;

public class IgniteCacheImpl<K, V> implements Cache<K, V> {
    private final ClientCache<K, V> delegate;


    public IgniteCacheImpl(ClientCache<K, V> delegate) {
        this.delegate = delegate;
    }


    @Override
    public V get(K key) {
        return delegate.get(key);
    }

    @Override
    public void put(K key, V val) {
        delegate.put(key, val);
    }

    @Override
    public boolean containsKey(K key) {
        return delegate.containsKey(key);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        return delegate.getAll(keys);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        delegate.putAll(map);
    }

    @Override
    public boolean replace(K key, V oldVal, V newVal) {
        return delegate.replace(key, oldVal, newVal);
    }

    @Override
    public boolean replace(K key, V val) {
        return delegate.replace(key, val);
    }

    @Override
    public boolean remove(K key) {
        return delegate.remove(key);
    }

    @Override
    public boolean remove(K key, V oldVal) {
        return delegate.remove(key, oldVal);
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        delegate.removeAll(keys);

    }

    @Override
    public void removeAll() {
        delegate.removeAll();

    }

    @Override
    public V getAndPut(K key, V val) {
        return delegate.getAndPut(key, val);
    }

    @Override
    public V getAndRemove(K key) {
        return delegate.getAndRemove(key);
    }

    @Override
    public V getAndReplace(K key, V val) {
        return delegate.getAndReplace(key, val);
    }

    @Override
    public boolean putIfAbsent(K key, V val) {
        return delegate.putIfAbsent(key, val);
    }

    @Override
    public void clear() {
        delegate.clear();

    }
}
