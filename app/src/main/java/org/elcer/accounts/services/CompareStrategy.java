package org.elcer.accounts.services;

public interface CompareStrategy<T extends Comparable<T>> {
    boolean compare(T candidate1, T candidate2);
}
