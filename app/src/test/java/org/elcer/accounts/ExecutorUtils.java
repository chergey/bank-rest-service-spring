package org.elcer.accounts;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@UtilityClass
public class ExecutorUtils {


    @SneakyThrows
    public static void runConcurrently(Runnable... tasks) {
        if (tasks == null || tasks.length == 0)
            throw new IllegalArgumentException("number of tasks must be > 0");

        var executor = Executors.newFixedThreadPool(tasks.length);

        var adaptedTasks = Arrays.stream(tasks).map(r -> (Callable<Void>) () -> {
            r.run();
            return null;
        }).collect(Collectors.toList());

        @SuppressWarnings("unused")
        var futures = executor.invokeAll(adaptedTasks);
        for (var future : futures) {
            future.get();
        }

        executor.shutdown();

    }

    @SneakyThrows
    public static void runConcurrentlyFJP(Runnable... tasks) {
        Arrays.stream(tasks).parallel().forEach(Runnable::run);

    }
}
