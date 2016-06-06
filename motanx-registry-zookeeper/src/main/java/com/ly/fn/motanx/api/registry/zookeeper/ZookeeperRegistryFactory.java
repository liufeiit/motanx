package com.ly.fn.motanx.api.registry.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;

import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.registry.Registry;
import com.ly.fn.motanx.api.registry.support.AbstractRegistryFactory;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.LoggerUtil;

/**
 * registry factory.
 */
@SpiMeta(name = "zookeeper")
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL registryUrl) {
        try {
            int timeout = registryUrl.getIntParameter(URLParamType.requestTimeout.getName(), URLParamType.requestTimeout.getIntValue());
            int sessionTimeout =
                    registryUrl.getIntParameter(URLParamType.registrySessionTimeout.getName(),
                            URLParamType.registrySessionTimeout.getIntValue());
            ZkClient zkClient = new ZkClient(registryUrl.getParameter("address"), sessionTimeout, timeout);
            return new ZookeeperRegistry(registryUrl, zkClient);
        } catch (ZkException e) {
            LoggerUtil.error("[ZookeeperRegistry] fail to connect zookeeper, cause: " + e.getMessage());
            throw e;
        }
    }
}
