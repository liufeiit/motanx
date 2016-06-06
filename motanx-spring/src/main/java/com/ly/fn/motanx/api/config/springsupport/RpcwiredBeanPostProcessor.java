package com.ly.fn.motanx.api.config.springsupport;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

import com.ly.fn.motanx.api.annotaion.Rpcwired;
import com.ly.fn.motanx.api.config.AbstractConfig;
import com.ly.fn.motanx.api.config.RefererConfig;

/**
 * @Rpcwired 注入处理器
 * @author Ethan Finch
 */
public class RpcwiredBeanPostProcessor implements BeanPostProcessor, PriorityOrdered, BeanFactoryAware, DisposableBean {

	private DefaultListableBeanFactory beanFactory;
	
	final ConcurrentMap<String, RefererConfig<?>> refererConfigs = new ConcurrentHashMap<String, RefererConfig<?>>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new RpcwiredFieldCallback(bean, beanFactory, this), new RpcwiredFieldFilter());
        return bean;
    }
	
	@Override
	public void destroy() throws Exception {
		for (RefererConfig<?> config : refererConfigs.values()) {
			config.destroy();
		}
	}

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    private final class RpcwiredFieldFilter implements FieldFilter {

        @Override
        public boolean matches(Field field) {
            return field.isAnnotationPresent(Rpcwired.class) && isNotConfigClass(field.getType());
        }

        private boolean isNotConfigClass(Class<?> klass) {
            return !AbstractConfig.class.isAssignableFrom((Class<?>) klass);
        }
    }
}