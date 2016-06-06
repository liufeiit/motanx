package com.ly.fn.motanx.demo.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DemoRpcClient {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"classpath:motan_demo_client.xml"});
        MotanApiClientDemo service = (MotanApiClientDemo) ctx.getBean("MotanApiClientDemo");
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            System.out.println(service.service.hello("motanx : " + i + ", ") + service.motanxDemoService2.hello2("motanx2 : " + i));
            Thread.sleep(500);
        }
        System.out.println("motanx demo is finish.");
        System.exit(0);
    }
}