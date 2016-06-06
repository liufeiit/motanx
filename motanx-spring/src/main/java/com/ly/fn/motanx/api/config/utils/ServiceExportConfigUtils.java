package com.ly.fn.motanx.api.config.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.CollectionUtils;

import com.ly.fn.motanx.api.annotaion.RpcService;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.config.BasicServiceInterfaceConfig;
import com.ly.fn.motanx.api.config.ProtocolConfig;
import com.ly.fn.motanx.api.config.RegistryConfig;
import com.ly.fn.motanx.api.config.ServiceConfig;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.util.CollectionUtil;
import com.ly.fn.motanx.api.util.GuidUtils;
import com.ly.fn.motanx.api.util.MathUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月18日 下午2:53:08
 */
public class ServiceExportConfigUtils {

    @SuppressWarnings("unchecked")
    public static <T> ServiceConfig<T> buildServiceConfig(RpcService rpcService, Class<?> beanInterfaceClass, Object ref, ConfigurableListableBeanFactory beanFactory) {
        ServiceConfig<T> rpcServiceConfig = new ServiceConfig<T>();
        rpcServiceConfig.setRef((T) ref);
        rpcServiceConfig.setInterface((Class<T>) beanInterfaceClass);
        rpcServiceConfig.setAccessLog(String.valueOf(rpcService.accessLog()));
        rpcServiceConfig.setActives(rpcService.actives());
        rpcServiceConfig.setApplication(rpcService.application());
        rpcServiceConfig.setAsync(rpcService.async());
        rpcServiceConfig.setCheck(String.valueOf(rpcService.check()));
        rpcServiceConfig.setCodec(rpcService.codec());
        String basicService = rpcService.basicService();
        if (StringUtils.isNotBlank(basicService)) {
            rpcServiceConfig.setBasicServiceConfig(beanFactory.getBean(basicService, BasicServiceInterfaceConfig.class));
        }
        String export = rpcService.export();
        if (StringUtils.isNotBlank(export)) {
            rpcServiceConfig.setExport(export);
            String[] exports = StringUtils.split(export, ":");
            if (exports.length >= 2) {
                String protocolName = exports[0];
                ProtocolConfig protocolConfig = beanFactory.getBean(protocolName, ProtocolConfig.class);
                if (protocolConfig != null) {
                    rpcServiceConfig.setProtocol(protocolConfig);
                }
            }
        }
        rpcServiceConfig.setFilter(rpcService.filter());
        rpcServiceConfig.setGroup(rpcService.group());
        rpcServiceConfig.setHost(rpcService.host());
        rpcServiceConfig.setId(GuidUtils.genGuid());
        rpcServiceConfig.setLocalServiceAddress(rpcService.localServiceAddress());
        // rpcServiceConfig.setMethods(methods);
        rpcServiceConfig.setMingzSize(rpcService.mingzSize());
        rpcServiceConfig.setMock(rpcService.mock());
        rpcServiceConfig.setModule(rpcService.module());
        String protocol = rpcService.protocol();
        if (StringUtils.isNotBlank(protocol)) {
            String[] protocolsNames = StringUtils.split(protocol, ",");
            if (ArrayUtils.isNotEmpty(protocolsNames)) {
                List<ProtocolConfig> protocols = new ArrayList<ProtocolConfig>();
                for (String protocolName : protocolsNames) {
                    ProtocolConfig protocolConfig = beanFactory.getBean(protocolName, ProtocolConfig.class);
                    if (protocolConfig == null) {
                        continue;
                    }
                    protocols.add(protocolConfig);
                }
                if (!CollectionUtils.isEmpty(protocols)) {
                    rpcServiceConfig.setProtocols(protocols);
                }
            }
        }
        rpcServiceConfig.setProxy(rpcService.proxy());
        rpcServiceConfig.setRegister(rpcService.register());
        String registry = rpcService.registry();
        if (StringUtils.isNotBlank(registry)) {
            String[] registriesNames = StringUtils.split(registry, ",");
            if (ArrayUtils.isNotEmpty(registriesNames)) {
                List<RegistryConfig> registries = new ArrayList<RegistryConfig>();
                for (String registryName : registriesNames) {
                    RegistryConfig registryConfig = beanFactory.getBean(registryName, RegistryConfig.class);
                    if (registryConfig == null) {
                        continue;
                    }
                    registries.add(registryConfig);
                }
                if (!CollectionUtils.isEmpty(registries)) {
                    rpcServiceConfig.setRegistries(registries);
                }
            }
        }
        rpcServiceConfig.setRequestTimeout(rpcService.requestTimeout());
        rpcServiceConfig.setRetries(rpcService.retries());
        rpcServiceConfig.setShareChannel(rpcService.shareChannel());
        rpcServiceConfig.setThrowException(rpcService.throwException());
        rpcServiceConfig.setUsegz(rpcService.usegz());
        rpcServiceConfig.setVersion(rpcService.version());
        return rpcServiceConfig;
    }

    public static void serviceRegistryConfig(ServiceConfig<?> serviceConfig, ConfigurableListableBeanFactory beanFactory) {
        if (CollectionUtil.isEmpty(serviceConfig.getRegistries()) && serviceConfig.getBasicServiceConfig() != null && !CollectionUtil.isEmpty(serviceConfig.getBasicServiceConfig().getRegistries())) {
            serviceConfig.setRegistries(serviceConfig.getBasicServiceConfig().getRegistries());
        }
        if (CollectionUtil.isEmpty(serviceConfig.getRegistries())) {
            for (String name : PropertiesHolder.getRegistryNames()) {
                RegistryConfig registryConfig = beanFactory.getBean(name, RegistryConfig.class);
                if (registryConfig == null) {
                    continue;
                }
                if (PropertiesHolder.getRegistryNames().size() == 1) {
                    serviceConfig.setRegistry(registryConfig);
                } else if (registryConfig.isDefault() != null && registryConfig.isDefault().booleanValue()) {
                    serviceConfig.setRegistry(registryConfig);
                }
            }
        }
        if (CollectionUtil.isEmpty(serviceConfig.getRegistries())) {
            String[] registriesNames = beanFactory.getBeanNamesForType(RegistryConfig.class);
            for (String name : registriesNames) {
                RegistryConfig registryConfig = beanFactory.getBean(name, RegistryConfig.class);
                if (registryConfig == null) {
                    continue;
                }
                if (registriesNames.length == 1) {
                    serviceConfig.setRegistry(registryConfig);
                } else if (registryConfig.isDefault() != null && registryConfig.isDefault().booleanValue()) {
                    serviceConfig.setRegistry(registryConfig);
                }
            }
        }
        if (CollectionUtil.isEmpty(serviceConfig.getRegistries())) {
            serviceConfig.setRegistry(MotanxFrameworkUtil.getDefaultRegistryConfig());
        }
    }

    public static void serviceExportConfig(ServiceConfig<?> serviceConfig, ConfigurableListableBeanFactory beanFactory) {
        BasicServiceInterfaceConfig basicServiceConfig = serviceConfig.getBasicServiceConfig();
        if (StringUtils.isBlank(serviceConfig.getExport()) && basicServiceConfig != null && !StringUtils.isBlank(basicServiceConfig.getExport())) {
            serviceConfig.setExport(basicServiceConfig.getExport());
            if (basicServiceConfig.getProtocols() != null) {
                serviceConfig.setProtocols(new ArrayList<ProtocolConfig>(basicServiceConfig.getProtocols()));
            }
        }
        if (CollectionUtil.isEmpty(serviceConfig.getProtocols()) && StringUtils.isNotEmpty(serviceConfig.getExport())) {
            int port = MathUtil.parseInt(serviceConfig.getExport(), 0);
            if (port > 0) {
                serviceConfig.setExport(MotanxConstants.PROTOCOL_MOTANX + ":" + serviceConfig.getExport());
                serviceConfig.setProtocol(MotanxFrameworkUtil.getDefaultProtocolConfig());
            }
        }
        if (StringUtils.isEmpty(serviceConfig.getExport()) || CollectionUtil.isEmpty(serviceConfig.getProtocols())) {
            throw new MotanxFrameworkException(String.format("%s ServiceConfig must config right export value!", serviceConfig.getInterface().getName()), MotanxErrorMsgConstant.FRAMEWORK_INIT_ERROR);
        }
    }

    public static void basicServiceConfig(ServiceConfig<?> serviceConfig, ConfigurableListableBeanFactory beanFactory) {
        if (serviceConfig.getBasicServiceConfig() != null) {
            return;
        }
        for (String name : PropertiesHolder.getBasicServiceConfigNames()) {
            BasicServiceInterfaceConfig basicServiceInterfaceConfig = beanFactory.getBean(name, BasicServiceInterfaceConfig.class);
            if (basicServiceInterfaceConfig == null) {
                continue;
            }
            if (PropertiesHolder.getBasicServiceConfigNames().size() == 1) {
                serviceConfig.setBasicServiceConfig(basicServiceInterfaceConfig);
            } else if (basicServiceInterfaceConfig.isDefault() != null && basicServiceInterfaceConfig.isDefault().booleanValue()) {
                serviceConfig.setBasicServiceConfig(basicServiceInterfaceConfig);
            }
        }
        if (serviceConfig.getBasicServiceConfig() == null) {
            String[] names = beanFactory.getBeanNamesForType(BasicServiceInterfaceConfig.class);
            for (String name : names) {
                BasicServiceInterfaceConfig basicServiceInterfaceConfig = beanFactory.getBean(name, BasicServiceInterfaceConfig.class);
                if (basicServiceInterfaceConfig == null) {
                    continue;
                }
                if (names.length == 1) {
                    serviceConfig.setBasicServiceConfig(basicServiceInterfaceConfig);
                } else if (basicServiceInterfaceConfig.isDefault() != null && basicServiceInterfaceConfig.isDefault().booleanValue()) {
                    serviceConfig.setBasicServiceConfig(basicServiceInterfaceConfig);
                }
            }
        }
    }
}
