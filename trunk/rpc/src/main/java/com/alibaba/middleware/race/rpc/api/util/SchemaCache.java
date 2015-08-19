package com.alibaba.middleware.race.rpc.api.util;

import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlw on 15-7-29.
 */
public class SchemaCache {
//    private static SchemaCache SCHEMACHE=new SchemaCache();
//
//    public static SchemaCache instance()
//    {
//        return SCHEMACHE;
//    }

    public static void load() {}
    private static Map<Class<?>, Schema<?>> map;

    static {
        map=new HashMap<Class<?>, Schema<?>>();
        map.put(RpcRequest.class, RuntimeSchema.getSchema(RpcRequest.class));
        map.put(RpcResponse.class,RuntimeSchema.getSchema(RpcResponse.class));
    }

    public static Schema getSchema(Class<?> clazz)
    {
        return RuntimeSchema.getSchema(clazz);
    }
}
