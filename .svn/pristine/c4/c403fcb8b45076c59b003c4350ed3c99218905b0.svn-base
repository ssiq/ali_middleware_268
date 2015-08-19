package com.alibaba.middleware.race.mom.broker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wlw on 15-8-18.
 */
public class ContextExecutorService {
    private static ExecutorService executorService;

    static {
        int cores=Runtime.getRuntime().availableProcessors();
        executorService= Executors.newFixedThreadPool(cores*10);
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
