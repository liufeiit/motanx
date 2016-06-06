package com.ly.fn.motanx.api.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * rpc session context
 */
public class RpcContext {
    private Map<Object, Object> attribute = new HashMap<Object, Object>(1);

    private static ThreadLocal<RpcContext> localContext = new ThreadLocal<RpcContext>() {
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return localContext.get();
    }

    public static void destroy() {
        localContext.set(null);
    }

    public Object getAttribute(Object key) {
        return attribute.get(key);
    }
}
