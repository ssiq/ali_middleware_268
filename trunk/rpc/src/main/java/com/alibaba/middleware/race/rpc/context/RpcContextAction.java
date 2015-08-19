package com.alibaba.middleware.race.rpc.context;

import java.util.Map;

/**
 * Created by wlw on 15-7-30.
 */
public interface RpcContextAction {
    public void addProp(String key, Object value);
    public Object getProp(String key);
    public Map<String,Object> getProps();
}
