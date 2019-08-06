package org.elcer.accounts;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Arrays;


@UtilityClass
public class ExecutorUtils {

    @SneakyThrows
    public static void runConcurrentlyFJP(Runnable... tasks) {
        Arrays.stream(tasks).parallel().forEach(Runnable::run);

    }
}
