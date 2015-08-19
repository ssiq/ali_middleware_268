package com.alibaba.middleware.race.mom.util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wlw on 15-8-15.
 */
public interface SimpleQueue <T> {
    boolean put(T t);
    boolean putOnce(T t,AtomicBoolean once);
    T get();
    int size();
}
