/*
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.ly.fn.motanx.api.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ly.fn.motanx.api.cluster.Cluster;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.ApplicationInfo;
import com.ly.fn.motanx.api.rpc.DefaultRequest;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.switcher.Switcher;
import com.ly.fn.motanx.api.switcher.SwitcherService;
import com.ly.fn.motanx.api.util.ExceptionUtil;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;
import com.ly.fn.motanx.api.util.ReflectUtil;
import com.ly.fn.motanx.api.util.RequestIdGenerator;

/**
 * 
 * @author maijunsheng
 * 
 * @param <T>
 */
public class RefererInvocationHandler<T> implements InvocationHandler {

    private List<Cluster<T>> clusters;
    private Class<T> clz;
    private SwitcherService switcherService = null;

    public RefererInvocationHandler(Class<T> clz, Cluster<T> cluster) {
        this.clz = clz;
        this.clusters = new ArrayList<Cluster<T>>(1);
        this.clusters.add(cluster);

        init();
    }

    public RefererInvocationHandler(Class<T> clz, List<Cluster<T>> clusters) {
        this.clz = clz;
        this.clusters = clusters;

        init();
    }

    private void init() {
        // clusters 不应该为空
        String switchName = this.clusters.get(0).getUrl().getParameter(URLParamType.switcherService.getName(), URLParamType.switcherService.getValue());
        switcherService = ExtensionLoader.getExtensionLoader(SwitcherService.class).getExtension(switchName);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // add by 刘飞 start
        String methodName = method.getName();
        if (methodName.equals("toString")) {
            return "Rpc Service : " + clz.getName();
        }
        if (methodName.equals("getClass")) {
            return (clz);
        }
        if (methodName.equals("hashCode")) {
            return (clz).hashCode();
        }
        if (methodName.equals("equals")) {
            return false;
        }
        if (methodName.equals("clone")) {
            return null;
        }
        // add by 刘飞 start
        DefaultRequest request = new DefaultRequest();

        request.setRequestId(RequestIdGenerator.getRequestId());
        request.setArguments(args);
        request.setMethodName(method.getName());
        request.setParamtersDesc(ReflectUtil.getMethodParamDesc(method));
        request.setInterfaceName(clz.getName());
        request.setAttachment(URLParamType.requestIdFromClient.getName(), String.valueOf(RequestIdGenerator.getRequestIdFromClient()));

        // 当 referer配置多个protocol的时候，比如A,B,C，
        // 那么正常情况下只会使用A，如果A被开关降级，那么就会使用B，B也被降级，那么会使用C
        for (Cluster<T> cluster : clusters) {
            String protocolSwitcher = MotanxConstants.PROTOCOL_SWITCHER_PREFIX + cluster.getUrl().getProtocol();

            Switcher switcher = switcherService.getSwitcher(protocolSwitcher);

            if (switcher != null && !switcher.isOn()) {
                continue;
            }

            request.setAttachment(URLParamType.version.getName(), cluster.getUrl().getVersion());
            request.setAttachment(URLParamType.clientGroup.getName(), cluster.getUrl().getGroup());
            // 带上client的application和module
            request.setAttachment(URLParamType.application.getName(), ApplicationInfo.getApplication(cluster.getUrl()).getApplication());
            request.setAttachment(URLParamType.module.getName(), ApplicationInfo.getApplication(cluster.getUrl()).getModule());
            Response response = null;
            boolean throwException = Boolean.parseBoolean(cluster.getUrl().getParameter(URLParamType.throwException.getName(), URLParamType.throwException.getValue()));
            try {
                response = cluster.call(request);
                return response.getValue();
            } catch (RuntimeException e) {
                if (ExceptionUtil.isBizException(e)) {
                    Throwable t = e.getCause();
                    // 只抛出Exception，防止抛出远程的Error
                    if (t != null && t instanceof Exception) {
                        throw t;
                    } else {
                        String msg = t == null ? "biz exception cause is null" : ("biz exception cause is throwable error:" + t.getClass() + ", errmsg:" + t.getMessage());
                        throw new MotanxServiceException(msg, MotanxErrorMsgConstant.SERVICE_DEFAULT_ERROR);
                    }
                } else if (!throwException) {
                    LoggerUtil.warn("RefererInvocationHandler invoke false, so return default value: uri=" + cluster.getUrl().getUri() + " " + MotanxFrameworkUtil.toString(request), e);
                    return getDefaultReturnValue(method.getReturnType());
                } else {
                    LoggerUtil.error("RefererInvocationHandler invoke Error: uri=" + cluster.getUrl().getUri() + " " + MotanxFrameworkUtil.toString(request), e);
                    throw e;
                }
            }
        }

        throw new MotanxServiceException("Referer call Error: cluster not exist, interface=" + clz.getName() + " " + MotanxFrameworkUtil.toString(request), MotanxErrorMsgConstant.SERVICE_UNFOUND);

    }

    private Object getDefaultReturnValue(Class<?> returnType) {
        if (returnType != null && returnType.isPrimitive()) {
            return PrimitiveDefault.getDefaultReturnValue(returnType);
        }
        return null;
    }

    private static class PrimitiveDefault {
        private static boolean defaultBoolean;
        private static char defaultChar;
        private static byte defaultByte;
        private static short defaultShort;
        private static int defaultInt;
        private static long defaultLong;
        private static float defaultFloat;
        private static double defaultDouble;

        private static Map<Class<?>, Object> primitiveValues = new HashMap<Class<?>, Object>();

        static {
            primitiveValues.put(boolean.class, defaultBoolean);
            primitiveValues.put(char.class, defaultChar);
            primitiveValues.put(byte.class, defaultByte);
            primitiveValues.put(short.class, defaultShort);
            primitiveValues.put(int.class, defaultInt);
            primitiveValues.put(long.class, defaultLong);
            primitiveValues.put(float.class, defaultFloat);
            primitiveValues.put(double.class, defaultDouble);
        }

        public static Object getDefaultReturnValue(Class<?> returnType) {
            return primitiveValues.get(returnType);
        }

    }
}
