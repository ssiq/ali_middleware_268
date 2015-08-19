package com.alibaba.middleware.race.mom.model;

import com.alibaba.middleware.race.mom.service.Pair;
import com.alibaba.middleware.race.mom.service.RequestProcesser;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wlw on 15-8-4.
 */
public class ResponseFuture {

    private CountDownLatch countDownLatch=new CountDownLatch(1);
    private long beginTimestamp = System.currentTimeMillis();
    private CallbackListener callbackListener;
    private TransmittingMessage transmittingMessage;
    private OnceSempore onceSempore;

    public TransmittingMessage getResponse(long timeOut, TimeUnit timeUnit) throws InterruptedException {
        countDownLatch.await(timeOut,timeUnit);
        return transmittingMessage;
    }

    public CallbackListener getCallbackListener() {
        return callbackListener;
    }

    public void setCallbackListener(CallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    public void invokeListener()
    {
        callbackListener.invoke(this);
    }

    public void onResponse(TransmittingMessage transmittingMessage)
    {
        this.transmittingMessage=transmittingMessage;
        countDownLatch.countDown();
    }

    private boolean state;

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isOK() {
        return state;
    }

    Throwable throwable;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setOnceSempore(OnceSempore onceSempore) {
        this.onceSempore = onceSempore;
    }

    public void release()
    {
        onceSempore.release();
    }

    public TransmittingMessage getTransmittingMessage() {
        return transmittingMessage;
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }
}
