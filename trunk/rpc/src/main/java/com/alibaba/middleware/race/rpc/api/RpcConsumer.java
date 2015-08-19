package com.alibaba.middleware.race.rpc.api;

import com.alibaba.middleware.race.rpc.aop.ConsumerHook;
import com.alibaba.middleware.race.rpc.async.ResponseCallbackListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by huangsheng.hs on 2015/3/26.
 */
public class RpcConsumer implements InvocationHandler {
    private Class<?> interfaceClazz;
    public RpcConsumer() {
    }

    /**
     * init a rpc consumer
     */
    private void init() {
        //TODO
    }

    /**
     * set the interface which this consumer want to use
     * actually,it will call a remote service to get the result of this interface's methods
     *
     * @param interfaceClass
     * @return
     */
    public RpcConsumer interfaceClass(Class<?> interfaceClass) {
        this.interfaceClazz = interfaceClass;
        return this;
    }

    ;

    /**
     * set the version of the service
     *
     * @param version
     * @return
     */
    public RpcConsumer version(String version) {
        //TODO
        return this;
    }

    /**
     * set the timeout of the service
     * consumer's time will take precedence of the provider's timeout
     *
     * @param clientTimeout
     * @return
     */
    public RpcConsumer clientTimeout(int clientTimeout) {
        //TODO
        return this;
    }

    /**
     * register a consumer hook to this service
     * @param hook
     * @return
     */
    public RpcConsumer hook(ConsumerHook hook) {
        return this;
    }

    /**
     * return an Object which can cast to the interface class
     *
     * @return
     */
    public Object instance() {
        //TODO return an Proxy
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{this.interfaceClazz},this);
    }

    /**
     * mark a async method,default future call
     *
     * @param methodName
     */
    public void asynCall(String methodName) {
        asynCall(methodName, null);
    }

    /**
     * mark a async method with a callback listener
     *
     * @param methodName
     * @param callbackListener
     */
    public <T extends ResponseCallbackListener> void asynCall(String methodName, T callbackListener) {
        //TODO
    }

    public void cancelAsyn(String methodName) {
        //TODO
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
