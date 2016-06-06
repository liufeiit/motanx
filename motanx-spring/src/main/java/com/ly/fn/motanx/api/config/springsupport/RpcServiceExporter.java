package com.ly.fn.motanx.api.config.springsupport;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import com.ly.fn.motanx.api.annotaion.RpcService;
import com.ly.fn.motanx.api.config.ServiceConfig;
import com.ly.fn.motanx.api.config.utils.ServiceExportConfigUtils;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.ReflectUtils;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月17日 下午9:57:25
 */
public class RpcServiceExporter extends BasicRpcServiceExporter implements BeanPostProcessor, PriorityOrdered, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        if (async) {
            executor.submit(new Runnable() {
                public void run() {
                    export(bean);
                }
            });
            return bean;
        }
        export(bean);
        return bean;
    }

    private void export(Object beanRef) throws BeansException {
        Class<?> beanClass = beanRef.getClass();
        Set<Class<?>> serviceInterfaces = new HashSet<Class<?>>();
        ReflectUtils.findInterfaces(beanClass, serviceInterfaces);
        if (!CollectionUtils.isEmpty(serviceInterfaces)) {
            RpcService rpcService = AnnotationUtils.findAnnotation(beanClass, RpcService.class);
            for (Class<?> serviceInterface : serviceInterfaces) {
                serviceExport(serviceInterface, beanRef);
                if (rpcService == null) {
                    continue;
                }
                serviceExport(serviceInterface, beanRef, rpcService);
            }
        }
    }

    private void serviceExport(Class<?> serviceInterface, Object ref, RpcService rpcService) {
        try {
            String key = serviceInterface.getName() + rpcService.version();
            if (exportedServices.containsKey(key)) {
                return;
            }
            ServiceConfig<?> rpcServiceConfig = ServiceExportConfigUtils.buildServiceConfig(rpcService, serviceInterface, ref, beanFactory);
            ServiceExportConfigUtils.basicServiceConfig(rpcServiceConfig, beanFactory);
            ServiceExportConfigUtils.serviceExportConfig(rpcServiceConfig, beanFactory);
            ServiceExportConfigUtils.serviceRegistryConfig(rpcServiceConfig, beanFactory);
            rpcServiceConfig.export();
            exportedServices.putIfAbsent(key, rpcServiceConfig);
            LoggerUtil.info("Success Export Service[{}] ref : {}", serviceInterface.getName(), ref);
            System.err.println(String.format("Success Export Service[%s] ref : %s", serviceInterface.getName(), ref));
        } catch (Exception e) {
            LoggerUtil.error("RpcServiceExporter Export[" + serviceInterface.getName() + "] Error.", e);
        }
    }

    private void serviceExport(Class<?> serviceInterface, Object ref) {
        try {
            RpcService rpcService = AnnotationUtils.findAnnotation(serviceInterface, RpcService.class);
            if (rpcService == null) {
                return;
            }
            String key = serviceInterface.getName() + rpcService.version();
            if (exportedServices.containsKey(key)) {
                return;
            }
            ServiceConfig<?> rpcServiceConfig = ServiceExportConfigUtils.buildServiceConfig(rpcService, serviceInterface, ref, beanFactory);
            ServiceExportConfigUtils.basicServiceConfig(rpcServiceConfig, beanFactory);
            ServiceExportConfigUtils.serviceExportConfig(rpcServiceConfig, beanFactory);
            ServiceExportConfigUtils.serviceRegistryConfig(rpcServiceConfig, beanFactory);
            rpcServiceConfig.export();
            exportedServices.putIfAbsent(key, rpcServiceConfig);
            LoggerUtil.info("Success Export Service[{}] ref : {}", serviceInterface.getName(), ref);
            System.err.println(String.format("Success Export Service[%s] ref : %s", serviceInterface.getName(), ref));
        } catch (Exception e) {
            LoggerUtil.error("RpcServiceExporter Export[" + serviceInterface.getName() + "] Error.", e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }
}
