package com.ly.fn.motanx.api.config.springsupport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.ly.fn.motanx.api.config.ServiceConfig;
import com.ly.fn.motanx.api.config.utils.ServiceExportConfigUtils;

public class ServiceConfigBean<T> extends ServiceConfig<T> implements BeanPostProcessor, BeanFactoryAware, InitializingBean, DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private static final long serialVersionUID = -7247592395983804440L;

    private transient DefaultListableBeanFactory beanFactory;

    @Override
    public void destroy() throws Exception {
        unexport();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceExportConfigUtils.basicServiceConfig(this, beanFactory);
        ServiceExportConfigUtils.serviceExportConfig(this, beanFactory);
        ServiceExportConfigUtils.serviceRegistryConfig(this, beanFactory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!getExported().get()) {
            export();
        }
    }
}
