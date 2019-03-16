package org.elcer.accounts.services;


import org.springframework.stereotype.Component;

/**
 * Synchcronizer used to manage concurrent operations
 * @param <T>
 */

@Component
public interface Synchronizer<T extends Comparable<T>> {
    void withLock(T one, T second, Runnable action);
}
