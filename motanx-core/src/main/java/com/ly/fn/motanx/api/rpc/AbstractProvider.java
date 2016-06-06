package com.ly.fn.motanx.api.rpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ly.fn.motanx.api.util.ReflectUtil;

public abstract class AbstractProvider<T> implements Provider<T> {
    protected Class<T> clz;
    protected URL url;
    protected boolean alive = false;
    protected boolean close = false;

    protected Map<String, Method> methodMap = new HashMap<String, Method>();

    public AbstractProvider(URL url, Class<T> clz) {
        this.url = url;
        this.clz = clz;

        initMethodMap(clz);
    }

    @Override
    public Response call(Request request) {
        Response response = invoke(request);

        return response;
    }

    protected abstract Response invoke(Request request);

    @Override
    public void init() {
        alive = true;
    }

    @Override
    public void destroy() {
        alive = false;
        close = true;
    }

    @Override
    public boolean isAvailable() {
        return alive;
    }

    @Override
    public String desc() {
        if (url != null) {
            return url.toString();
        }

        return null;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    protected Method lookup(Request request) {
        String methodDesc = ReflectUtil.getMethodDesc(request.getMethodName(), request.getParamtersDesc());

        return methodMap.get(methodDesc);
    }

    private void initMethodMap(Class<T> clz) {
        Method[] methods = clz.getMethods();

        for (Method method : methods) {
            String methodDesc = ReflectUtil.getMethodDesc(method);
            methodMap.put(methodDesc, method);
        }
    }

}
