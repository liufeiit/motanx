package com.ly.fn.motanx.api.config;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

import com.ly.fn.motanx.api.cluster.Cluster;
import com.ly.fn.motanx.api.cluster.support.ClusterSupport;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.config.annotation.ConfigDesc;
import com.ly.fn.motanx.api.config.handler.ConfigHandler;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.registry.RegistryService;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.CollectionUtil;
import com.ly.fn.motanx.api.util.NetUtils;
import com.ly.fn.motanx.api.util.StringTools;

public class RefererConfig<T> extends AbstractRefererConfig {

    private static final long serialVersionUID = -2299754608229467887L;

    private Class<T> interfaceClass;

    // 具体到方法的配置
    protected List<MethodConfig> methods;

    // 点对点直连服务提供地址
    private String directUrl;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private T ref;

    private BasicRefererInterfaceConfig basicReferer;

    private List<ClusterSupport<T>> clusterSupports;

    public List<MethodConfig> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodConfig> methods) {
        this.methods = methods;
    }

    public void setMethods(MethodConfig methods) {
        this.methods = Collections.singletonList(methods);
    }

    public boolean hasMethods() {
        return this.methods != null && !this.methods.isEmpty();
    }

    public T getRef() {
        if (ref == null) {
            initRef();
        }
        return ref;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized void initRef() {
        if (initialized.get()) {
            return;
        }

        try {
            interfaceClass = (Class) Class.forName(interfaceClass.getName(), true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new MotanxFrameworkException("ReferereConfig initRef Error: Class not found " + interfaceClass.getName(), e, MotanxErrorMsgConstant.FRAMEWORK_INIT_ERROR);
        }

        if (CollectionUtil.isEmpty(protocols)) {
            throw new MotanxFrameworkException(String.format("%s RefererConfig is malformed, for protocol not set correctly!", interfaceClass.getName()));
        }

        checkInterfaceAndMethods(interfaceClass, methods);

        clusterSupports = new ArrayList<ClusterSupport<T>>(protocols.size());
        List<Cluster<T>> clusters = new ArrayList<Cluster<T>>(protocols.size());
        String proxy = null;

        ConfigHandler configHandler = ExtensionLoader.getExtensionLoader(ConfigHandler.class).getExtension(MotanxConstants.DEFAULT_VALUE);

        List<URL> registryUrls = loadRegistryUrls();
        String localIp = getLocalHostAddress(registryUrls);
        for (ProtocolConfig protocol : protocols) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(URLParamType.nodeType.getName(), MotanxConstants.NODE_TYPE_REFERER);
            params.put(URLParamType.version.getName(), URLParamType.version.getValue());
            params.put(URLParamType.refreshTimestamp.getName(), String.valueOf(System.currentTimeMillis()));

            collectConfigParams(params, protocol, basicReferer, extConfig, this);
            collectMethodConfigParams(params, this.getMethods());

            URL refUrl = new URL(protocol.getName(), localIp, MotanxConstants.DEFAULT_INT_VALUE, interfaceClass.getName(), params);
            ClusterSupport<T> clusterSupport = createClusterSupport(refUrl, configHandler, registryUrls);

            clusterSupports.add(clusterSupport);
            clusters.add(clusterSupport.getCluster());

            proxy = (proxy == null) ? refUrl.getParameter(URLParamType.proxy.getName(), URLParamType.proxy.getValue()) : proxy;

        }

        ref = configHandler.refer(interfaceClass, clusters, proxy);

        initialized.set(true);
    }

    private ClusterSupport<T> createClusterSupport(URL refUrl, ConfigHandler configHandler, List<URL> registryUrls) {
        List<URL> regUrls = new ArrayList<URL>();

        // 如果用户指定directUrls 或者 injvm协议访问，则使用local registry
        if (StringUtils.isNotBlank(directUrl) || MotanxConstants.PROTOCOL_INJVM.equals(refUrl.getProtocol())) {
            URL regUrl = new URL(MotanxConstants.REGISTRY_PROTOCOL_LOCAL, NetUtils.LOCALHOST, MotanxConstants.DEFAULT_INT_VALUE, RegistryService.class.getName());
            if (StringUtils.isNotBlank(directUrl)) {
                StringBuilder duBuf = new StringBuilder(128);
                String[] dus = MotanxConstants.COMMA_SPLIT_PATTERN.split(directUrl);
                for (String du : dus) {
                    if (du.contains(":")) {
                        String[] hostPort = du.split(":");
                        URL durl = refUrl.createCopy();
                        durl.setHost(hostPort[0].trim());
                        durl.setPort(Integer.parseInt(hostPort[1].trim()));
                        durl.addParameter(URLParamType.nodeType.getName(), MotanxConstants.NODE_TYPE_SERVICE);
                        duBuf.append(StringTools.urlDecode(durl.toFullStr())).append(MotanxConstants.COMMA_SEPARATOR);
                    }
                }
                if (duBuf.length() > 0) {
                    duBuf.deleteCharAt(duBuf.length() - 1);
                    regUrl.addParameter(URLParamType.directUrl.getName(), duBuf.toString());
                }
            }
            regUrls.add(regUrl);
        } else { // 通过注册中心配置拼装URL，注册中心可能在本地，也可能在远端
            if (registryUrls == null || registryUrls.isEmpty()) {
                throw new IllegalStateException(
                        String.format("No registry to reference %s on the consumer %s , please config <motan:registry address=\"...\" /> in your spring config.", interfaceClass, NetUtils.LOCALHOST));
            }
            for (URL url : registryUrls) {
                regUrls.add(url.createCopy());
            }
        }

        for (URL url : regUrls) {
            url.addParameter(URLParamType.embed.getName(), StringTools.urlEncode(refUrl.toFullStr()));
        }
        return configHandler.buildClusterSupport(interfaceClass, regUrls);
    }

    public synchronized void destroy() {
        if (clusterSupports != null) {
            for (ClusterSupport<T> clusterSupport : clusterSupports) {
                clusterSupport.destroy();
            }
        }
        ref = null;
        initialized.set(false);
    }

    public void setInterface(Class<T> interfaceClass) {
        if (interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        this.interfaceClass = interfaceClass;
    }

    public Class<?> getInterface() {
        return interfaceClass;
    }

    public String getDirectUrl() {
        return directUrl;
    }

    public void setDirectUrl(String directUrl) {
        this.directUrl = directUrl;
    }

    @ConfigDesc(excluded = true)
    public BasicRefererInterfaceConfig getBasicReferer() {
        return basicReferer;
    }

    public void setBasicReferer(BasicRefererInterfaceConfig basicReferer) {
        this.basicReferer = basicReferer;
    }

    public List<ClusterSupport<T>> getClusterSupports() {
        return clusterSupports;
    }

    public AtomicBoolean getInitialized() {
        return initialized;
    }


}
