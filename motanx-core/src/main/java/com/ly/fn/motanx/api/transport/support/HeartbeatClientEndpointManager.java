package com.ly.fn.motanx.api.transport.support;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.transport.Client;
import com.ly.fn.motanx.api.transport.Endpoint;
import com.ly.fn.motanx.api.transport.EndpointManager;
import com.ly.fn.motanx.api.transport.HeartbeatFactory;
import com.ly.fn.motanx.api.util.LoggerUtil;

public class HeartbeatClientEndpointManager implements EndpointManager {

    private ConcurrentMap<Client, HeartbeatFactory> endpoints = new ConcurrentHashMap<Client, HeartbeatFactory>();

    // 一般这个类创建的实例会比较少，如果共享的话，容易“被影响”，如果某个任务阻塞了
    private ScheduledExecutorService executorService = null;

    @Override
    public void init() {
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                for (Map.Entry<Client, HeartbeatFactory> entry : endpoints.entrySet()) {
                    Client endpoint = entry.getKey();

                    try {
                        // 如果节点是存活状态，那么没必要走心跳
                        if (endpoint.isAvailable()) {
                            continue;
                        }

                        HeartbeatFactory factory = entry.getValue();
                        endpoint.heartbeat(factory.createRequest());
                    } catch (Exception e) {
                        LoggerUtil.error("HeartbeatEndpointManager send heartbeat Error: url=" + endpoint.getUrl().getUri(), e);
                    }
                }

            }
        }, MotanxConstants.HEARTBEAT_PERIOD, MotanxConstants.HEARTBEAT_PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        executorService.shutdownNow();
    }

    @Override
    public void addEndpoint(Endpoint endpoint) {
        if (!(endpoint instanceof Client)) {
            throw new MotanxFrameworkException("HeartbeatClientEndpointManager addEndpoint Error: class not support " + endpoint.getClass());
        }

        Client client = (Client) endpoint;

        URL url = endpoint.getUrl();

        String heartbeatFactoryName = url.getParameter(URLParamType.heartbeatFactory.getName(), URLParamType.heartbeatFactory.getValue());

        HeartbeatFactory heartbeatFactory = ExtensionLoader.getExtensionLoader(HeartbeatFactory.class).getExtension(heartbeatFactoryName);

        if (heartbeatFactory == null) {
            throw new MotanxFrameworkException("HeartbeatFactory not exist: " + heartbeatFactoryName);
        }

        endpoints.put(client, heartbeatFactory);
    }

    @Override
    public void removeEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint);
    }

    public Set<Client> getClients() {
        return Collections.unmodifiableSet(endpoints.keySet());
    }
}
