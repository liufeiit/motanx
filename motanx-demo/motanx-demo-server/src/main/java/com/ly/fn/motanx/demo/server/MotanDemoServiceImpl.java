package com.ly.fn.motanx.demo.server;

import com.ly.fn.motanx.demo.service.MotanxDemoService;

public class MotanDemoServiceImpl implements MotanxDemoService {

    public String hello(String name) {
        System.out.println(name);
        return "Hello " + name + "!";
    }

    @Override
    public String hello2(String name) {
        System.out.println(name);
        return "Hello2 " + name + "!";
    }

}
