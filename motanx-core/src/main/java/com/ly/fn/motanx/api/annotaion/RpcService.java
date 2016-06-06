package com.ly.fn.motanx.api.annotaion;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

import com.ly.fn.motanx.api.common.MotanxConstants;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月10日 下午5:19:08
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface RpcService {
    
    /**
     * 注册中心的id列表，多个用“,”分隔，如果为空，则使用所有的配置中心.
     */
    public String registry() default StringUtils.EMPTY;
    
    /**
     * 暴露协议的id列表，多个用“,”分隔
     */
    public String protocol() default StringUtils.EMPTY;
    
    /**
     * 基础的ServiceExporter配置Bean
     */
    public String basicService() default StringUtils.EMPTY;

    /**
     * 应用名称
     */
    public String application() default StringUtils.EMPTY;

    /**
     * 分组
     */
    public String group() default StringUtils.EMPTY;

    /**
     * 模块名称
     */
    public String module() default StringUtils.EMPTY;

    /**
     * 一个service可以按多个protocol提供服务，不同protocol使用不同port 利用export来设置protocol和port，格式如下： protocol1:port1,protocol2:port2
     */
    public String export() default StringUtils.EMPTY;

    /**
     * 服务版本
     */
    public String version() default MotanxConstants.DEFAULT_VERSION;

    /**
     * 协议编码
     */
    public String codec() default StringUtils.EMPTY;

    /**
     * 代理类型
     */
    public String proxy() default MotanxConstants.PROXY_JDK;

    /**
     * 过滤器
     */
    public String filter() default StringUtils.EMPTY;

    /**
     * 进行gzip压缩的最小阈值，usegz开启，且大于此值时才进行gzip压缩。单位Byte
     */
    public int mingzSize() default Integer.MAX_VALUE;

    /**
     * The service path.
     */
    public String path() default StringUtils.EMPTY;

    /**
     * 序列化方式.
     */
    public String serialization() default StringUtils.EMPTY;

    /**
     * 是否开启gzip压缩
     */
    public boolean usegz() default true;

    /**
     * 请求超时时间
     */
    public int requestTimeout() default 3000;

    /**
     * 重试次数
     */
    public int retries() default 3;
    
    /**
     * 当使用VintageRetryLookupRegistry时，从config server同步三次失败并且feature.motanmcq.loadaddressfromfs开关打开时，把这个配置指定的地址作为服务地址，以逗号分割
     */
    public String localServiceAddress() default StringUtils.EMPTY;

    /**
     * 在该注册中心上服务是否暴露.
     */
    public boolean register() default true;

    /**
     * 是否共享 channel
     */
    public boolean shareChannel() default false;

    /**
     * if throw exception when call failure，the default value is ture
     */
    public boolean throwException() default true;

    /**
     * 一般不用设置，由服务自己获取，但如果有多个ip，而只想用指定ip，则可以在此处指定
     */
    public String host() default StringUtils.EMPTY;

    /**
     * 是否异步
     */
    public boolean async() default false;

    /**
     * 是否进行check，如果为true，则在监测失败后抛异常
     */
    public boolean check() default false;

    /**
     * 服务接口的失败mock实现类名
     */
    public String mock() default StringUtils.EMPTY;

    /**
     * 最大并发调用
     */
    public int actives() default 1000;

    /**
     * 是否记录访问日志，true记录，false不记录
     */
    public boolean accessLog() default true;
}