package com.ly.fn.motanx.api.config.springsupport;

import java.lang.reflect.Field;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.ly.fn.motanx.api.annotaion.Rpcwired;
import com.ly.fn.motanx.api.config.RefererConfig;
import com.ly.fn.motanx.api.config.utils.ServiceRefererConfigUtils;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月18日 下午8:57:51
 */
public class RpcwiredFieldCallback implements FieldCallback {
    
    private RpcwiredBeanPostProcessor processor;

    private final Object target;

    private final DefaultListableBeanFactory beanFactory;

    RpcwiredFieldCallback(Object target, DefaultListableBeanFactory beanFactory, RpcwiredBeanPostProcessor processor) {
        this.target = target;
        this.beanFactory = beanFactory;
        this.processor = processor;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        Rpcwired anno = field.getAnnotation(Rpcwired.class);
        final String key = field.getType().getName() + anno.version();
        RefererConfig<?> refererConfig = null;
        if (processor.refererConfigs.get(key) == null) {
            refererConfig = ServiceRefererConfigUtils.buildRefererConfig(anno, field.getType(), beanFactory);
            ServiceRefererConfigUtils.basicRefererConfig(refererConfig, beanFactory);
            ServiceRefererConfigUtils.protocolConfig(refererConfig, beanFactory);
            ServiceRefererConfigUtils.registryConfig(refererConfig, beanFactory);
            refererConfig.initRef();
            processor.refererConfigs.putIfAbsent(key, refererConfig);
        } else {
            refererConfig = processor.refererConfigs.get(key);
        }
        BeanWrapper accessor = PropertyAccessorFactory.forBeanPropertyAccess(target);
        accessor.setPropertyValue(field.getName(), refererConfig.getRef());
    }
}