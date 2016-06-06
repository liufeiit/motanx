package com.ly.fn.motanx.demo.client;

import com.ly.fn.motanx.api.annotaion.Rpcwired;
import com.ly.fn.motanx.api.config.ProtocolConfig;
import com.ly.fn.motanx.api.config.RefererConfig;
import com.ly.fn.motanx.api.config.RegistryConfig;
import com.ly.fn.motanx.demo.service.MotanxDemoService;
import com.ly.fn.motanx.demo.service.MotanxDemoService2;

public class MotanApiClientDemo {
    
    @Rpcwired
    public MotanxDemoService service;

    @Rpcwired
    public MotanxDemoService2 motanxDemoService2;
    
    public void setService(MotanxDemoService service) {
        this.service = service;
    }
    
    public void setMotanxDemoService2(MotanxDemoService2 motanxDemoService2) {
        this.motanxDemoService2 = motanxDemoService2;
    }

    public static void main(String[] args) {
        RefererConfig<MotanxDemoService> motanDemoServiceReferer = new RefererConfig<MotanxDemoService>();

        // 设置接口及实现类
        motanDemoServiceReferer.setInterface(MotanxDemoService.class);

        // 配置服务的group以及版本号
        motanDemoServiceReferer.setGroup("motan-demo-rpc");
        motanDemoServiceReferer.setVersion("1.0");
        motanDemoServiceReferer.setRequestTimeout(300);

        // 配置注册中心
        RegistryConfig registry = new RegistryConfig();
        registry.setRegProtocol("local");
        motanDemoServiceReferer.setRegistry(registry);

        // 配置RPC协议
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setId("motan");
        protocol.setName("motan");
        motanDemoServiceReferer.setProtocol(protocol);

        // 使用服务
        MotanxDemoService service = motanDemoServiceReferer.getRef();
        System.out.println(service.hello("motan"));

        System.exit(0);

    }

}
