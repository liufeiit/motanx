package com.ly.fn.motanx.demo.service;

import com.ly.fn.motanx.api.annotaion.RpcService;

@RpcService(export = "demomotanx:9103", basicService = "serviceBasicConfig")
public interface MotanxDemoService extends MotanxDemoService2 {
    public String hello(String name);

}
