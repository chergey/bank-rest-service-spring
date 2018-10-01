package org.elcer.accounts;

import org.elcer.accounts.utils.ExceptionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ExecutorUtils {

    public static void runConcurrently(Runnable... tasks) {
        if (tasks.length == 0)
            throw new IllegalArgumentException("number of tasks must be > 0");

        ExecutorService executor = Executors.newFixedThreadPool(tasks.length);

        List<Callable<Void>> adaptedTasks = Arrays.stream(tasks).map(r -> (Callable<Void>) () -> {
            r.run();
            return null;
        }).collect(Collectors.toList());

        try {
            @SuppressWarnings("unused")
            List<Future<Void>> futures = executor.invokeAll(adaptedTasks);
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            ExceptionUtils.sneakyThrow(e);
        }

        executor.shutdown();

    }
}
