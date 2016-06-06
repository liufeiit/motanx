package com.ly.fn.motanx.api.config.springsupport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.ly.fn.motanx.api.config.RefererConfig;
import com.ly.fn.motanx.api.config.utils.ServiceRefererConfigUtils;

public class RefererConfigBean<T> extends RefererConfig<T> implements FactoryBean<T>, BeanFactoryAware, InitializingBean, DisposableBean {

    private static final long serialVersionUID = 8381310907161365567L;

    private transient DefaultListableBeanFactory beanFactory;

    @Override
    public T getObject() throws Exception {
        return getRef();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterface();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public final void destroy() {
        super.destroy();
    }

    @Override
    public final void afterPropertiesSet() throws Exception {
        ServiceRefererConfigUtils.basicRefererConfig(this, beanFactory);
        ServiceRefererConfigUtils.protocolConfig(this, beanFactory);
        ServiceRefererConfigUtils.registryConfig(this, beanFactory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
