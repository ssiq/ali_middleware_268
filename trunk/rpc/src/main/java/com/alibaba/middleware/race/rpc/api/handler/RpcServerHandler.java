package com.alibaba.middleware.race.rpc.api.handler;

import com.alibaba.middleware.race.rpc.api.util.MethodCache;
import com.alibaba.middleware.race.rpc.context.ServerContextAction;
import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wlw on 15-7-27.
 */
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private Class<?> serviceInterface;
    private Object serviceInstance;
    private Object contextRealObject= ServerContextAction.getInstance();
//    private Set<Method> methodSet;

    public RpcServerHandler(Class<?> serviceInterface,Object serviceInstance) {
        this.serviceInstance=serviceInstance;
        this.serviceInterface=serviceInterface;
//        this.methodSet=new HashSet<Method>(serviceInterface.getMethods().);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("one client connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        RpcRequest rpcRequest=(RpcRequest)msg;
        RpcRequest rpcRequest=(RpcRequest)msg;
        try{
            Object doObject=null;
            Method method=null;
            Object[] args=rpcRequest.getArgs();
            if(rpcRequest.isContext())
            {
                method=MethodCache.getContextMethodCache().getMethod(rpcRequest.getMethodId());
                doObject=contextRealObject;
            }else{
                method= MethodCache.getInstance().getMethod(rpcRequest.getMethodId());
                doObject=serviceInstance;
            }
//            System.out.println("do cation:" + method.getName());
            Object appResponse=method.invoke(doObject, args);
//            System.out.println("request is:"+rpcRequest.toString());
//            System.out.println("the result is:"+((appResponse==null)?null:appResponse.toString()));
            ctx.writeAndFlush(new RpcResponse(appResponse,null,rpcRequest.getThreadId(),rpcRequest.getRequestId()));
        }catch (InvocationTargetException throwable){
//            System.out.println("The exception type is:" + throwable.getCause().getClass().getName());
//            System.out.println("The exception is:"+throwable.getMessage());
            ctx.writeAndFlush(new RpcResponse(null, throwable.getCause(),rpcRequest.getThreadId(),rpcRequest.getRequestId()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("some error occured");
        super.exceptionCaught(ctx, cause);
    }
}
