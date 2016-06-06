package com.ly.fn.motanx.api.annotaion;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

import com.ly.fn.motanx.api.common.MotanxConstants;

/**
 * rpc服务注入注解
 * @author Ethan Finch
 * 
 * 配置约定
 * 	  1 service 和 referer 端相同的参数的含义一定相同；
 *    2 service端参数的覆盖策略：protocol--basicConfig--service，前面的配置会被后面的config参数覆盖；
 *    3 registry 参数不进入service、referer端的参数列表；
 *    4 referer端从注册中心拿到参数后，先用referer端的参数覆盖，然后再使用该service
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface Rpcwired {
	
	String basicRefererConfig() default StringUtils.EMPTY;
	
	/**
	 * 应用名
	 */
	String application() default StringUtils.EMPTY;
	
	/**
	 * 模块名
	 */
	String module() default StringUtils.EMPTY;
	
	/**
	 * 分组
	 */
	String group() default StringUtils.EMPTY;
	
	/**
	 * 服务版本
	 */
	String version() default MotanxConstants.DEFAULT_VERSION;
	
	/**
	 * 代理类型
	 * @return
	 */
	String proxy() default MotanxConstants.PROXY_JDK;
	
	/**
	 * 过滤器
	 */
	String filter() default "";
	
	/**
	 * 最大并发调用
	 */
	int actives() default 0;
	
	/**
	 * 是否异步
	 */
	boolean async() default false;
	
	/**
	 * 是否抛出异常 if throw exception when call failure，the default value is ture
	 */
	boolean throwException() default true;
	
	/**
	 * 调用超时时间
	 */
	int requestTimeout() default 200;
	
	/**
	 * 是否注册
	 */
	boolean register() default false;
	
	// 是否记录访问日志，true记录，false不记录
	String accessLog() default "false";
	
	// 是否进行check，如果为true，则在监测失败后抛异常
	String check() default "true";
	
	/**
	 * 服务接口的失败mock实现类名
	 */
    String mock() default "false";

    /**
     * 是否共享 channel
     */
    boolean shareChannel() default false;

    /**
     * 重试次数
     */
    int retries() default 0;

    /**
     * 是否开启gzip压缩
     */
    boolean usegz() default false;

    /**
     * 进行gzip压缩的最小阈值，usegz开启，且大于此值时才进行gzip压缩。单位Byte
     */
    int mingzSize() default 1000;

    String codec() default "motanx";
    
    // 服务接口的mock类SLA
    String mean() default "2";
    
    String p90() default "4";
    
    String p99() default "10";
    
    String p999() default "70";
    
    String errorRate() default "0.01";
    
    /**
     * 直连地址，跳过注册中心
     * @return
     */
    String directUrl() default "";
    
    String localServiceAddress() default "";
	
}