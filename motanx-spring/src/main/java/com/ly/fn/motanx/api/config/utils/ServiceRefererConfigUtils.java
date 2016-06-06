package com.ly.fn.motanx.api.config.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.CollectionUtils;

import com.ly.fn.motanx.api.annotaion.Rpcwired;
import com.ly.fn.motanx.api.config.BasicRefererInterfaceConfig;
import com.ly.fn.motanx.api.config.ProtocolConfig;
import com.ly.fn.motanx.api.config.RefererConfig;
import com.ly.fn.motanx.api.config.RegistryConfig;
import com.ly.fn.motanx.api.util.CollectionUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月18日 下午6:19:54
 */
public class ServiceRefererConfigUtils {
    
    public static <T> RefererConfig<T> buildRefererConfig(Rpcwired anno, Class<T> klass, ConfigurableListableBeanFactory beanFactory) {
        RefererConfig<T> config = new RefererConfig<>();
        String basicRefererConfig = anno.basicRefererConfig();
        if (StringUtils.isNotBlank(basicRefererConfig)) {
            config.setBasicReferer(beanFactory.getBean(basicRefererConfig, BasicRefererInterfaceConfig.class));
        }
        config.setInterface(klass);
        config.setAccessLog(anno.accessLog());
        config.setActives(anno.actives());
        config.setApplication(anno.application());
        config.setAsync(anno.async());
        config.setCheck(anno.check());
        config.setCodec(anno.codec());
        config.setDirectUrl(anno.directUrl());
        config.setErrorRate(anno.errorRate());
        config.setFilter(anno.filter());
        config.setGroup(anno.group());
        //config.setId(id);
        config.setLocalServiceAddress(anno.localServiceAddress());
        config.setMean(anno.mean());
        //config.setMethods(Lists.newArrayList(anno.methodConfig()));
        //config.setMethods(methods);
        config.setMingzSize(anno.mingzSize());
        config.setMock(anno.mock());
        config.setModule(anno.module());
        config.setP90(anno.p90());
        config.setP99(anno.p99());
        config.setP999(anno.p999());
        config.setProxy(anno.proxy());
        config.setRegister(anno.register());
        config.setRequestTimeout(anno.requestTimeout());
        config.setRetries(anno.retries());
        config.setShareChannel(anno.shareChannel());
        config.setThrowException(anno.throwException());
        config.setUsegz(anno.usegz());
        config.setVersion(anno.version());
        return config;
    }

    public static void basicRefererConfig(RefererConfig<?> refererConfig, ConfigurableListableBeanFactory beanFactory) {
        if (refererConfig.getBasicReferer() != null) {
            return;
        }
        for (String name : PropertiesHolder.getBasicRefererConfigNames()) {
            BasicRefererInterfaceConfig basicRefererInterfaceConfig = beanFactory.getBean(name, BasicRefererInterfaceConfig.class);
            if (basicRefererInterfaceConfig == null) {
                continue;
            }
            if (PropertiesHolder.getBasicRefererConfigNames().size() == 1) {
                refererConfig.setBasicReferer(basicRefererInterfaceConfig);
            } else if (Boolean.TRUE.equals(basicRefererInterfaceConfig.isDefault())) {
                refererConfig.setBasicReferer(basicRefererInterfaceConfig);
                break;
            }
        }
        if (refererConfig.getBasicReferer() == null) {
            String[] names = beanFactory.getBeanNamesForType(BasicRefererInterfaceConfig.class);
            for (String name : names) {
                BasicRefererInterfaceConfig basicRefererInterfaceConfig = beanFactory.getBean(name, BasicRefererInterfaceConfig.class);
                if (basicRefererInterfaceConfig == null) {
                    continue;
                }
                if (names.length == 1) {
                    refererConfig.setBasicReferer(basicRefererInterfaceConfig);
                } else if (Boolean.TRUE.equals(basicRefererInterfaceConfig.isDefault())) {
                    refererConfig.setBasicReferer(basicRefererInterfaceConfig);
                    break;
                }
            }
        }
    }

    public static void protocolConfig(RefererConfig<?> refererConfig, ConfigurableListableBeanFactory beanFactory) {
        List<ProtocolConfig> protocols = refererConfig.getProtocols();
        BasicRefererInterfaceConfig basicReferer = refererConfig.getBasicReferer();
        if (CollectionUtil.isEmpty(protocols) && basicReferer != null && !CollectionUtil.isEmpty(basicReferer.getProtocols())) {
            refererConfig.setProtocols(basicReferer.getProtocols());
        }
        if (CollectionUtils.isEmpty(refererConfig.getProtocols())) {
            for (String name : PropertiesHolder.getProtocolNames()) {
                ProtocolConfig protocolConfig = beanFactory.getBean(name, ProtocolConfig.class);
                if (protocolConfig == null) {
                    continue;
                }
                if (PropertiesHolder.getProtocolNames().size() == 1) {
                    refererConfig.setProtocol(protocolConfig);
                } else if (Boolean.TRUE.equals(protocolConfig.isDefault())) {
                    refererConfig.setProtocol(protocolConfig);
                    break;
                }
            }
        }
        if (CollectionUtils.isEmpty(refererConfig.getProtocols())) {
            String[] names = beanFactory.getBeanNamesForType(ProtocolConfig.class);
            for (String name : names) {
                ProtocolConfig protocolConfig = beanFactory.getBean(name, ProtocolConfig.class);
                if (protocolConfig == null) {
                    continue;
                }
                if (names.length == 1) {
                    refererConfig.setProtocol(protocolConfig);
                } else if (Boolean.TRUE.equals(protocolConfig.isDefault())) {
                    refererConfig.setProtocol(protocolConfig);
                    break;
                }
            }
        }
        if (CollectionUtil.isEmpty(refererConfig.getProtocols())) {
            refererConfig.setProtocol(MotanxFrameworkUtil.getDefaultProtocolConfig());
        }
    }

    public static void registryConfig(RefererConfig<?> refererConfig, ConfigurableListableBeanFactory beanFactory) {
        List<RegistryConfig> registries = refererConfig.getRegistries();
        BasicRefererInterfaceConfig basicReferer = refererConfig.getBasicReferer();
        if (CollectionUtil.isEmpty(registries) && basicReferer != null && !CollectionUtil.isEmpty(basicReferer.getRegistries())) {
            refererConfig.setRegistries(basicReferer.getRegistries());
        }
        if (CollectionUtils.isEmpty(refererConfig.getRegistries())) {
            for (String name : PropertiesHolder.getRegistryNames()) {
                RegistryConfig registryConfig = beanFactory.getBean(name, RegistryConfig.class);
                if (registryConfig == null) {
                    continue;
                }
                if (PropertiesHolder.getRegistryNames().size() == 1) {
                    refererConfig.setRegistry(registryConfig);
                } else if (Boolean.TRUE.equals(registryConfig.isDefault())) {
                    refererConfig.setRegistry(registryConfig);
                    break;
                }
            }
        }
        if (CollectionUtils.isEmpty(refererConfig.getRegistries())) {
            String[] names = beanFactory.getBeanNamesForType(RegistryConfig.class);
            for (String name : names) {
                RegistryConfig registryConfig = beanFactory.getBean(name, RegistryConfig.class);
                if (registryConfig == null) {
                    continue;
                }
                if (names.length == 1) {
                    refererConfig.setRegistry(registryConfig);
                } else if (Boolean.TRUE.equals(registryConfig.isDefault())) {
                    refererConfig.setRegistry(registryConfig);
                    break;
                }
            }
        }
        if (CollectionUtil.isEmpty(refererConfig.getRegistries())) {
            refererConfig.setRegistry(MotanxFrameworkUtil.getDefaultRegistryConfig());
        }
    }
}
