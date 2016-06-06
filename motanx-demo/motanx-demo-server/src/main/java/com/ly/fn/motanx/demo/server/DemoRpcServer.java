package com.ly.fn.motanx.demo.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.util.MotanxSwitcherUtil;

public class DemoRpcServer {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"classpath*:motanx_demo_server.xml"});
        MotanxSwitcherUtil.setSwitcherValue(MotanxConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        System.out.println("server start...");
    }
}
