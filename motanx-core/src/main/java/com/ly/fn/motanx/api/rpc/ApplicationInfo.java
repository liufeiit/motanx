package com.ly.fn.motanx.api.rpc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;

public class ApplicationInfo {

    public static final String STATISTIC = "statisitic";
    public static final ConcurrentMap<String, Application> applications = new ConcurrentHashMap<String, Application>();
    private static String CLIENT = "-client";

    public static Application getApplication(URL url) {
        Application application = applications.get(url.getPath());
        if (application == null && MotanxConstants.NODE_TYPE_REFERER.equals(url.getParameter(URLParamType.nodeType.getName()))) {
            String app = url.getParameter(URLParamType.application.getName(), URLParamType.application.getValue()) + CLIENT;
            String module = url.getParameter(URLParamType.module.getName(), URLParamType.module.getValue()) + CLIENT;

            applications.putIfAbsent(url.getPath() + CLIENT, new Application(app, module));
            application = applications.get(url.getPath() + CLIENT);
        }

        return application;
    }

    public static void addService(URL url) {
        Application application = applications.get(url.getPath());
        if (application == null) {
            String app = url.getParameter(URLParamType.application.getName(), URLParamType.application.getValue());
            String module = url.getParameter(URLParamType.module.getName(), URLParamType.module.getValue());

            applications.putIfAbsent(url.getPath(), new Application(app, module));
        }
    }
}
