package org.elcer.accounts.services;

interface DeadlockStrategy<T extends Comparable<T>> {
    boolean resolve(T candidate1, T candidate2);
}
