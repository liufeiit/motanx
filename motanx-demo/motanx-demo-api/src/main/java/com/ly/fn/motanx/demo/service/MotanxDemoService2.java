package com.ly.fn.motanx.demo.service;

import com.ly.fn.motanx.api.annotaion.RpcService;

@RpcService(export = "demomotanx:9203", basicService = "serviceBasicConfig")
public interface MotanxDemoService2 {
    public String hello2(String name);

}
