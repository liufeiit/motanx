package com.ly.fn.motanx.api.config;

import org.apache.commons.lang3.StringUtils;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.config.annotation.ConfigDesc;
import com.ly.fn.motanx.api.config.handler.ConfigHandler;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.registry.RegistryService;
import com.ly.fn.motanx.api.rpc.Exporter;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.ConcurrentHashSet;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.NetUtils;
import com.ly.fn.motanx.api.util.StringTools;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceConfig<T> extends AbstractServiceConfig {

    private static final long serialVersionUID = -3342374271064293224L;

    private static ConcurrentHashSet<String> existingServices = new ConcurrentHashSet<String>();

    // 具体到方法的配置
    protected List<MethodConfig> methods;

    // 接口实现类引用
    private T ref;

    // service 对应的exporters，用于管理service服务的生命周期
    private List<Exporter<T>> exporters = new CopyOnWriteArrayList<Exporter<T>>();

    private Class<T> interfaceClass;

    private BasicServiceInterfaceConfig basicServiceConfig;

    private AtomicBoolean exported = new AtomicBoolean(false);

    // service的用于注册的url，用于管理service注册的生命周期，url为regitry url，内部嵌套service url。
    private ConcurrentHashSet<URL> registereUrls = new ConcurrentHashSet<URL>();

    public static ConcurrentHashSet<String> getExistingServices() {
        return existingServices;
    }

    public Class<?> getInterface() {
        return interfaceClass;
    }

    public void setInterface(Class<T> interfaceClass) {
        if (interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        this.interfaceClass = interfaceClass;
    }

    public List<MethodConfig> getMethods() {
        return methods;
    }

    public void setMethod(MethodConfig methods) {
        this.methods = Collections.singletonList(methods);
    }

    public void setMethods(List<MethodConfig> methods) {
        this.methods = methods;
    }

    public boolean hasMethods() {
        return this.methods != null && this.methods.size() > 0;
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    public List<Exporter<T>> getExporters() {
        return Collections.unmodifiableList(exporters);
    }

    protected boolean serviceExists(URL url) {
        return existingServices.contains(url.getIdentity());
    }

    public synchronized void export() {
        if (exported.get()) {
            LoggerUtil.warn(String.format("%s has already been expoted, so ignore the export request!", interfaceClass.getName()));
            return;
        }

        checkInterfaceAndMethods(interfaceClass, methods);

        List<URL> registryUrls = loadRegistryUrls();
        if (registryUrls == null || registryUrls.size() == 0) {
            throw new IllegalStateException("Should set registry config for service:" + interfaceClass.getName());
        }

        Map<String, Integer> protocolPorts = getProtocolAndPort();
        for (ProtocolConfig protocolConfig : protocols) {
            Integer port = protocolPorts.get(protocolConfig.getId());
            if (port == null) {
                throw new MotanxServiceException(String.format("Unknow port in service:%s, protocol:%s", interfaceClass.getName(), protocolConfig.getId()));
            }
            doExport(protocolConfig, port, registryUrls);
        }

        afterExport();
    }

    public synchronized void unexport() {
        if (!exported.get()) {
            return;
        }
        try {
            ConfigHandler configHandler = ExtensionLoader.getExtensionLoader(ConfigHandler.class).getExtension(MotanxConstants.DEFAULT_VALUE);
            configHandler.unexport(exporters, registereUrls);
        } finally {
            afterUnexport();
        }
    }

    private void doExport(ProtocolConfig protocolConfig, int port, List<URL> registryURLs) {
        String protocolName = protocolConfig.getName();
        if (protocolName == null || protocolName.length() == 0) {
            protocolName = URLParamType.protocol.getValue();
        }

        String hostAddress = host;
        if (StringUtils.isBlank(hostAddress) && basicServiceConfig != null) {
            hostAddress = basicServiceConfig.getHost();
        }
        if (NetUtils.isInvalidLocalHost(hostAddress)) {
            hostAddress = getLocalHostAddress(registryURLs);
        }

        Map<String, String> map = new HashMap<String, String>();

        map.put(URLParamType.nodeType.getName(), MotanxConstants.NODE_TYPE_SERVICE);
        map.put(URLParamType.refreshTimestamp.getName(), String.valueOf(System.currentTimeMillis()));

        collectConfigParams(map, protocolConfig, basicServiceConfig, extConfig, this);
        collectMethodConfigParams(map, this.getMethods());

        URL serviceUrl = new URL(protocolName, hostAddress, port, interfaceClass.getName(), map);

        if (serviceExists(serviceUrl)) {
            LoggerUtil.warn(String.format("%s configService is malformed, for same service (%s) already exists ", interfaceClass.getName(), serviceUrl.getIdentity()));
            throw new MotanxFrameworkException(String.format("%s configService is malformed, for same service (%s) already exists ", interfaceClass.getName(), serviceUrl.getIdentity()),
                    MotanxErrorMsgConstant.FRAMEWORK_INIT_ERROR);
        }

        List<URL> urls = new ArrayList<URL>();

        // injvm 协议只支持注册到本地，其他协议可以注册到local、remote
        if (MotanxConstants.PROTOCOL_INJVM.equals(protocolConfig.getId())) {
            URL localRegistryUrl = null;
            for (URL ru : registryURLs) {
                if (MotanxConstants.REGISTRY_PROTOCOL_LOCAL.equals(ru.getProtocol())) {
                    localRegistryUrl = ru.createCopy();
                    break;
                }
            }
            if (localRegistryUrl == null) {
                localRegistryUrl = new URL(MotanxConstants.REGISTRY_PROTOCOL_LOCAL, hostAddress, MotanxConstants.DEFAULT_INT_VALUE, RegistryService.class.getName());
            }

            urls.add(localRegistryUrl);
        } else {
            for (URL ru : registryURLs) {
                urls.add(ru.createCopy());
            }
        }

        for (URL u : urls) {
            u.addParameter(URLParamType.embed.getName(), StringTools.urlEncode(serviceUrl.toFullStr()));
            registereUrls.add(u.createCopy());
        }

        ConfigHandler configHandler = ExtensionLoader.getExtensionLoader(ConfigHandler.class).getExtension(MotanxConstants.DEFAULT_VALUE);

        exporters.add(configHandler.export(interfaceClass, ref, urls));

        initLocalAppInfo(serviceUrl);
    }

    private void afterExport() {
        exported.set(true);
        for (Exporter<T> ep : exporters) {
            existingServices.add(ep.getProvider().getUrl().getIdentity());
        }
    }

    private void afterUnexport() {
        exported.set(false);
        for (Exporter<T> ep : exporters) {
            existingServices.remove(ep.getProvider().getUrl().getIdentity());
            exporters.remove(ep);
        }
        exporters.clear();
        registereUrls.clear();
    }

    @ConfigDesc(excluded = true)
    public BasicServiceInterfaceConfig getBasicServiceConfig() {
        return basicServiceConfig;
    }

    public void setBasicServiceConfig(BasicServiceInterfaceConfig basicServiceConfig) {
        this.basicServiceConfig = basicServiceConfig;
    }

    public Map<String, Integer> getProtocolAndPort() {
        if (StringUtils.isBlank(export)) {
            throw new MotanxServiceException("export should not empty in service config:" + interfaceClass.getName());
        }
        return ConfigUtil.parseExport(this.export);
    }

    @ConfigDesc(excluded = true)
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public AtomicBoolean getExported() {
        return exported;
    }

    public ConcurrentHashSet<URL> getRegistereUrls() {
        return registereUrls;
    }

}
