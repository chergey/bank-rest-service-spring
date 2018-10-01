package org.elcer.accounts.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public final class RandomUtils {

    public static int getGtZeroRandom() {
        return ThreadLocalRandom.current().nextInt(10, 1000);
    }

    public static int getGtZeroRandom(int hbound) {
        return ThreadLocalRandom.current().nextInt(1, hbound);
    }
}
