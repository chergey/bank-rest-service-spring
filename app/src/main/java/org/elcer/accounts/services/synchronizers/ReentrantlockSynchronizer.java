package org.elcer.accounts.services.synchronizers;

import org.elcer.accounts.services.CompareStrategy;
import org.elcer.accounts.services.Synchronizer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Default synchronization making use of ReentrantLocks
 * Locks are not disposed
 *
 * @param <T>
 * @see Synchronizer
 */

@Component
@Primary
public class ReentrantlockSynchronizer<T extends Comparable<T>> implements Synchronizer<T> {
    private final Map<T, Lock> slots = new ConcurrentHashMap<>();

    private CompareStrategy<T> compareStrategy =
            (candidate1, candidate2) -> candidate1.compareTo(candidate2) > 0;

    @Override
    public void withLock(final T one, final T second, Runnable action) {
        final Lock o1 = slots.computeIfAbsent(one, (k) -> new ReentrantLock()),
                o2 = slots.computeIfAbsent(second, (k) -> new ReentrantLock());

        try {
            if (compareStrategy.compare(one, second)) {
                o1.lock();
                o2.lock();
            } else {
                o2.lock();
                o1.lock();
            }

            action.run();
        } finally {
            if (compareStrategy.compare(one, second)) {
                o1.unlock();
                o2.unlock();
            } else {
                o2.unlock();
                o1.unlock();
            }
        }
    }
}
