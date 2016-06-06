package com.ly.fn.motanx.api.registry.consul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.registry.consul.ConsulConstants;
import com.ly.fn.motanx.api.registry.consul.ConsulService;
import com.ly.fn.motanx.api.registry.consul.ConsulUtils;
import com.ly.fn.motanx.api.rpc.URL;

/**
 * 
 * @Description MockUtils
 * @author zhanglei28
 * @date 2016年3月22日
 *
 */
public class MockUtils {
    //  mock service info
    private static String path = "mockService";
    private static String group = "mockGroup";
    private static String address = "127.0.0.1";
    private static String protocol = "motan";

    public static ConsulService getMockService(int id) {

        ConsulService service = new ConsulService();
        service.setAddress(address);
        service.setId(ConsulUtils.convertServiceId(address, id, path));
        service.setName(ConsulUtils.convertGroupToServiceName(group));
        service.setPort(id);
        List<String> tags = new ArrayList<String>();
        tags.add(ConsulConstants.CONSUL_TAG_MOTAN_PROTOCOL + protocol);
        service.setTags(tags);

        return service;
    }

    /**
     * 获取mock的url信息，可以通过此url查询对应的mock service
     * 
     * @return
     */
    public static URL getMockUrl(int port) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLParamType.group.getName(), group);
        URL url = new URL(protocol, address, port, path, params);
        return url;
    }

}
