package com.ly.fn.motanx.api.util;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月18日 下午8:48:45
 */
public class GuidUtils {

    public static String genGuid() {
        String uuid = UUID.fromString(
                Long.toHexString(System.nanoTime() >> 2) + "-" + 
                Long.toHexString(System.nanoTime() >> 3) + "-" + 
                Long.toHexString(System.nanoTime() >> 4) + "-" + 
                Long.toHexString(System.nanoTime() >> 5) + "-" + 
                Long.toHexString(System.nanoTime() >> 6)).toString();
        uuid = StringUtils.upperCase(StringUtils.replace(uuid, "-", StringUtils.EMPTY));
        return uuid;
    }
}
