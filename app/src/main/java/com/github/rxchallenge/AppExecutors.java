package com.github.rxchallenge;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Sebastian Schipor
 */
public class AppExecutors {

    private static AppExecutors INSTANCE;
    private final Executor IO_EXECUTOR = Executors.newSingleThreadExecutor();

    public static AppExecutors getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppExecutors();
        }

        return INSTANCE;
    }

    //execute a task on IO thread
    public void ioThread(Runnable runnable) {
        IO_EXECUTOR.execute(runnable);
    }
}
