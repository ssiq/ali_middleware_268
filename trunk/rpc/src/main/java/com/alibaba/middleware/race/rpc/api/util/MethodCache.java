package com.alibaba.middleware.race.rpc.api.util;

import com.alibaba.middleware.race.rpc.context.RpcContextAction;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.protostuff.ProtobufIOUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by wlw on 15-7-28.
 */
public class MethodCache {
    private static MethodCache METHODCACHE=new MethodCache();
    private static MethodCache CONTEXTCACHE=null;

    static {
        CONTEXTCACHE=new MethodCache();
        CONTEXTCACHE.init(RpcContextAction.class);
    }

    private BiMap<Short, Method> biMap= HashBiMap.create();
    private BiMap<Method, Short> reverseMap=null;
    private short maxId=0;

    private MethodCache() {}

    public void init(final Class<?> clazz)
    {
        Method[] methods=clazz.getMethods();
        Arrays.sort(methods, new Comparator<Method>() {
            public int compare(Method method, Method t1) {
                if(method.getName().compareTo(t1.getName())!=0)
                {
                    return method.getName().compareTo(t1.getName());
                }else{
                    Class<?>[]clazz_method=method.getParameterTypes();
                    Class<?>[]clazz_t1=t1.getParameterTypes();
                    if(clazz_method.length<clazz_t1.length)
                    {
                        return -1;
                    }else if(clazz_method.length>clazz_t1.length){
                        return 1;
                    }else{
                        for(int i=0;i<clazz_method.length;++i)
                        {
                            int t=clazz_method[i].getName().compareTo(clazz_t1[i].getName());
                            if(t!=0)
                            {
                                return t;
                            }
                        }
                    }
                    return method.getReturnType().getName().compareTo(t1.getReturnType().getName());
                }
            }
        });
        for(int i=0;i<methods.length;++i)
        {
            System.out.println(maxId+":"+methods[i].getName());
            biMap.put(maxId, methods[i]);
            ++maxId;
        }
        reverseMap=biMap.inverse();
    }

    public short getMethodId(Method method)
    {
        return reverseMap.get(method);
    }

    public Method getMethod(short methodId)
    {
        return biMap.get(methodId);
    }

    public static MethodCache getInstance()
    {
        return METHODCACHE;
    }

    public static MethodCache getContextMethodCache()
    {
        return CONTEXTCACHE;
    }
}
