package com.ly.fn.motanx.api.common;

import com.ly.fn.motanx.api.config.RegistryConfig.Excise;

public enum URLParamType {
    /** version **/
    version("version", MotanxConstants.DEFAULT_VERSION),
    /** request timeout **/
    requestTimeout("requestTimeout", 200),
    /** request id from http interface **/
    requestIdFromClient("requestIdFromClient", 0),
    /** connect timeout **/
    connectTimeout("connectTimeout", 1000),
    /** service min worker threads **/
    minWorkerThread("minWorkerThread", 20),
    /** service max worker threads **/
    maxWorkerThread("maxWorkerThread", 200),
    /** pool min conn number **/
    minClientConnection("minClientConnection", 2),
    /** pool max conn number **/
    maxClientConnection("maxClientConnection", 10),
    /** pool max conn number **/
    maxContentLength("maxContentLength", 10 * 1024 * 1024),
    /** max server conn (all clients conn) **/
    maxServerConnection("maxServerConnection", 100000),
    /** pool conn manger stragy **/
    poolLifo("poolLifo", true),

    lazyInit("lazyInit", false),
    /** multi referer share the same channel **/
    shareChannel("shareChannel", false),

    /************************** SPI start ******************************/

    /** serialize **/
    serialize("serialization", "hessian2"),
    /** codec **/
    codec("codec", "motanx"),
    /** endpointFactory **/
    endpointFactory("endpointFactory", "motanx"),
    /** heartbeatFactory **/
    heartbeatFactory("heartbeatFactory", "motanx"),
    /** switcherService **/
    switcherService("switcherService", "localSwitcherService"),

    /************************** SPI end ******************************/

    group("group", "default_rpc"), 
    clientGroup("clientGroup", "default_rpc"), 
    accessLog("accessLog", false),

    // 0为不做并发限制
    actives("actives", 0),

    refreshTimestamp("refreshTimestamp", 0), 
    nodeType("nodeType", MotanxConstants.NODE_TYPE_SERVICE),

    // 格式为protocol:port
    export("export", ""),
    embed("embed", ""),

    registryRetryPeriod("registryRetryPeriod", 30 * MotanxConstants.SECOND_MILLS),
    /* 注册中心不可用节点剔除方式 */
    excise("excise", Excise.excise_dynamic.getName()), 
    cluster("cluster", MotanxConstants.DEFAULT_VALUE), 
    loadbalance("loadbalance", "activeWeight"), 
    haStrategy("haStrategy", "failover"), 
    protocol("protocol", MotanxConstants.PROTOCOL_MOTANX), 
    path("path", ""), 
    host("host", ""), 
    port("port", 0), 
    iothreads("iothreads", Runtime.getRuntime().availableProcessors() + 1), 
    workerQueueSize("workerQueueSize", 0), 
    acceptConnections("acceptConnections", 0), 
    proxy("proxy", MotanxConstants.PROXY_JDK), 
    filter("filter", ""),

    usegz("usegz", false), // 是否开启gzip压缩
    mingzSize("mingzSize", 1000), // 进行gz压缩的最小数据大小。超过此阈值才进行gz压缩


    application("application", MotanxConstants.FRAMEWORK_NAME), 
    module("module", MotanxConstants.FRAMEWORK_NAME),

    retries("retries", 0), 
    async("async", false), 
    mock("mock", "false"), 
    mean("mean", "2"), 
    p90("p90", "4"), 
    p99("p99", "10"), 
    p999("p999", "70"), 
    errorRate("errorRate", "0.01"), 
    check("check", "true"), 
    directUrl("directUrl", ""), 
    registrySessionTimeout("registrySessionTimeout", 1 * MotanxConstants.MINUTE_MILLS),

    register("register", true), 
    subscribe("subscribe", true), 
    throwException("throwException", "true"),

    localServiceAddress("localServiceAddress", ""),

    // 切换group时，各个group的权重比。默认无权重
    weights("weights", "");

    private String name;
    private String value;
    private long longValue;
    private int intValue;
    private boolean boolValue;

    private URLParamType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private URLParamType(String name, long longValue) {
        this.name = name;
        this.value = String.valueOf(longValue);
        this.longValue = longValue;
    }

    private URLParamType(String name, int intValue) {
        this.name = name;
        this.value = String.valueOf(intValue);
        this.intValue = intValue;
    }

    private URLParamType(String name, boolean boolValue) {
        this.name = name;
        this.value = String.valueOf(boolValue);
        this.boolValue = boolValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public boolean getBooleanValue() {
        return boolValue;
    }

}
