package org.elcer.accounts.services.synchronizers;

import org.elcer.accounts.services.CompareStrategy;
import org.elcer.accounts.services.Synchronizer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Synchronization mechanism on primitives
 * @param <T>
 */
@Component
public class IntrinsicSynchronizer<T extends Comparable<T>> implements Synchronizer<T> {
    private final Map<T, Object> slots = new ConcurrentHashMap<>();

    private CompareStrategy<T> compareStrategy =
            (candidate1, candidate2) -> candidate1.compareTo(candidate2) > 0;

    public void withLock(final T one, final T second, Runnable action) {
        Object o1 = slots.computeIfAbsent(one, (k) -> new Object()),
                o2 = slots.computeIfAbsent(second, (k) -> new Object()),
                firstToTake, secondToTake;
        if (compareStrategy.compare(one, second)) {
            firstToTake = o1;
            secondToTake = o2;
        } else {
            firstToTake = o2;
            secondToTake = o1;
        }

        synchronized (firstToTake) {
            synchronized (secondToTake) {
                action.run();
            }
        }
    }

}
