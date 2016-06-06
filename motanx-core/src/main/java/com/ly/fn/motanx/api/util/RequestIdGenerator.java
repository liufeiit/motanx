package com.ly.fn.motanx.api.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过requestId能够知道大致请求的时间
 * 
 * <pre>
 * 		目前是 currentTimeMillis * 100000 + offset.incrementAndGet()
 * 
 * 		通过 requestId / (100000 * 1000) 能够得到秒
 * </pre>
 */
public class RequestIdGenerator {
    private static AtomicLong offset = new AtomicLong(0);

    /**
     * 获取 requestId
     * 
     * @return
     */
    public static long getRequestId() {
        return System.currentTimeMillis() * 100000 + offset.incrementAndGet();
    }

    public static long getRequestIdFromClient() {
        return 0;
    }

}
