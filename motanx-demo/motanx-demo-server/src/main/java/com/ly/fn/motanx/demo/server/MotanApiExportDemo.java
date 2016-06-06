
package com.ly.fn.motanx.demo.server;

import com.ly.fn.motanx.api.config.ProtocolConfig;
import com.ly.fn.motanx.api.config.RegistryConfig;
import com.ly.fn.motanx.api.config.ServiceConfig;
import com.ly.fn.motanx.demo.service.MotanxDemoService;

public class MotanApiExportDemo {

    public static void main(String[] args) throws InterruptedException {
        ServiceConfig<MotanxDemoService> motanDemoService = new ServiceConfig<MotanxDemoService>();
        
        //设置接口及实现类
        motanDemoService.setInterface(MotanxDemoService.class);  
        motanDemoService.setRef(new MotanDemoServiceImpl());

        // 配置服务的group以及版本号
        motanDemoService.setGroup("motanx-demo-rpc");
        motanDemoService.setVersion("1.0");

        // 配置注册中心
        RegistryConfig registry = new RegistryConfig();
        registry.setRegProtocol("local");
        registry.setCheck("false"); //不检查是否注册成功
        motanDemoService.setRegistry(registry);

        // 配置RPC协议
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setId("motanx");
        protocol.setName("motanx");
        motanDemoService.setProtocol(protocol);

        motanDemoService.setExport("motanx:8002");
        motanDemoService.export();
        
        
        Thread.sleep(Long.MAX_VALUE);
    }

}
