package com.ly.fn.motanx.api.config.utils;

import java.util.Set;

import com.ly.fn.motanx.api.util.ConcurrentHashSet;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月18日 下午5:15:42
 */
public class PropertiesHolder {

    private final static Set<String> PROTOCOL_DEFINE_NAMES = new ConcurrentHashSet<String>();
    
    private final static Set<String> REGISTRY_DEFINE_NAMES = new ConcurrentHashSet<String>();
    
    private final static Set<String> BASIC_SERVICECONFIG_DEFINE_NAMES = new ConcurrentHashSet<String>();
    
    private final static Set<String> BASIC_REFERERCONFIG_DEFINE_NAMES = new ConcurrentHashSet<String>();
    
    public static void addBasicRefererConfigNames(String basicRefererSonfigNames) {
        PropertiesHolder.BASIC_REFERERCONFIG_DEFINE_NAMES.add(basicRefererSonfigNames);
    }
    
    public static Set<String> getBasicRefererConfigNames() {
        return PropertiesHolder.BASIC_REFERERCONFIG_DEFINE_NAMES;
    }
    
    public static void addBasicServiceConfigNames(String basicServiceConfigNames) {
        PropertiesHolder.BASIC_SERVICECONFIG_DEFINE_NAMES.add(basicServiceConfigNames);
    }
    
    public static Set<String> getBasicServiceConfigNames() {
        return PropertiesHolder.BASIC_SERVICECONFIG_DEFINE_NAMES;
    }
    
    public static void addRegistryName(String registryName) {
        PropertiesHolder.REGISTRY_DEFINE_NAMES.add(registryName);
    }
    
    public static Set<String> getRegistryNames() {
        return PropertiesHolder.REGISTRY_DEFINE_NAMES;
    }
    
    public static void addProtocolName(String protocolName) {
        PropertiesHolder.PROTOCOL_DEFINE_NAMES.add(protocolName);
    }
    
    public static Set<String> getProtocolNames() {
        return PropertiesHolder.PROTOCOL_DEFINE_NAMES;
    }
}