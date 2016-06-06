package com.ly.fn.motanx.api.config.handler;

import com.ly.fn.motanx.api.cluster.Cluster;
import com.ly.fn.motanx.api.cluster.support.ClusterSupport;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.protocol.support.ProtocolFilterDecorator;
import com.ly.fn.motanx.api.proxy.ProxyFactory;
import com.ly.fn.motanx.api.proxy.RefererInvocationHandler;
import com.ly.fn.motanx.api.registry.Registry;
import com.ly.fn.motanx.api.registry.RegistryFactory;
import com.ly.fn.motanx.api.rpc.*;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.StringTools;

import java.util.Collection;
import java.util.List;

/**
 * Handle refUrl to get referers, assemble to a cluster, create a proxy
 */
@SpiMeta(name = MotanxConstants.DEFAULT_VALUE)
public class SimpleConfigHandler implements ConfigHandler {

    @Override
    public <T> ClusterSupport<T> buildClusterSupport(Class<T> interfaceClass, List<URL> registryUrls) {
        ClusterSupport<T> clusterSupport = new ClusterSupport<T>(interfaceClass, registryUrls);
        clusterSupport.init();

        return clusterSupport;
    }

    @Override
    public <T> T refer(Class<T> interfaceClass, List<Cluster<T>> clusters, String proxyType) {
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension(proxyType);
        return proxyFactory.getProxy(interfaceClass, new RefererInvocationHandler<T>(interfaceClass, clusters));
    }

    @Override
    public <T> Exporter<T> export(Class<T> interfaceClass, T ref, List<URL> registryUrls) {
        String serviceStr = StringTools.urlDecode(registryUrls.get(0).getParameter(URLParamType.embed.getName()));
        URL serviceUrl = URL.valueOf(serviceStr);
        // export service
        // 利用protocol decorator来增加filter特性
        String protocolName = serviceUrl.getParameter(URLParamType.protocol.getName(), URLParamType.protocol.getValue());
        Protocol extensionProtocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(protocolName);
        Protocol protocol = new ProtocolFilterDecorator(extensionProtocol);
        Provider<T> provider = new DefaultProvider<T>(ref, serviceUrl, interfaceClass);
        Exporter<T> exporter = protocol.export(provider, serviceUrl);

        // register service
        register(registryUrls, serviceUrl);

        return exporter;
    }

    @Override
    public <T> void unexport(List<Exporter<T>> exporters, Collection<URL> registryUrls) {
        try {
            unRegister(registryUrls);
        } catch (Exception e1) {
            LoggerUtil.warn("Exception when unregister urls:" + registryUrls);
        }
        try {
            for (Exporter<T> exporter : exporters) {
                exporter.unexport();
            }
        } catch (Exception e) {
            LoggerUtil.warn("Exception when unexport exporters:" + exporters);
        }
    }

    private void register(List<URL> registryUrls, URL serviceUrl) {
        for (URL url : registryUrls) {
            // 根据check参数的设置，register失败可能会抛异常，上层应该知晓
            RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(url.getProtocol());
            Registry registry = registryFactory.getRegistry(url);
            registry.register(serviceUrl);
        }
    }

    private void unRegister(Collection<URL> registryUrls) {
        for (URL url : registryUrls) {
            // 不管check的设置如何，做完所有unregistry，做好清理工作
            try {
                String serviceStr = StringTools.urlDecode(url.getParameter(URLParamType.embed.getName()));
                URL serviceUrl = URL.valueOf(serviceStr);

                RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(url.getProtocol());
                Registry registry = registryFactory.getRegistry(url);
                registry.unregister(serviceUrl);
            } catch (Exception e) {
                LoggerUtil.warn(String.format("unregister url false:%s", url), e);
            }
        }
    }

}
