package org.elcer.accounts.services;

import org.elcer.accounts.utils.ExceptionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Alternative mechanism of synchronization on primitives
 * @param <T>
 */
public class EquivalenceLock<T> {
    private final Set<T> queue = new HashSet<>();

    public void lock(final T object) {
        synchronized (queue) {
            while (queue.contains(object)) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    ExceptionUtils.sneakyThrow(e);
                }
            }
            queue.add(object);
        }
    }

    public void unlock(final T ticket) {
        synchronized (queue) {
            queue.remove(ticket);
            queue.notifyAll();
        }
    }
}
