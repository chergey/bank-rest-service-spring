package org.elcer.accounts.utils;

import lombok.experimental.UtilityClass;


@UtilityClass
public class ExceptionUtils {

    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }

    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    @SuppressWarnings("unchecked")
    public static void sneakyThrow(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            sneakyThrow(e);
        }
    }
}
