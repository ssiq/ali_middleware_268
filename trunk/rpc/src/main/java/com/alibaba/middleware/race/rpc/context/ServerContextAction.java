package com.alibaba.middleware.race.rpc.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlw on 15-7-30.
 */
public class ServerContextAction implements RpcContextAction {

    private static ServerContextAction serverContextAction=new ServerContextAction();

    public static ServerContextAction getInstance()
    {
        return serverContextAction;
    }

    private ServerContextAction(){}

    public Map<String,Object> props = new HashMap<String, Object>();

    public void addProp(String key ,Object value){
        props.put(key,value);
    }

    public Object getProp(String key){
        return props.get(key);
    }

    public Map<String,Object> getProps(){
        return Collections.unmodifiableMap(props);
    }
}
