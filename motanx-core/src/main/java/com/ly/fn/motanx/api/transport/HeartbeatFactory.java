package com.ly.fn.motanx.api.transport;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.Request;

/**
 * heartbeat的消息保持和正常请求的Request一致，这样以便更能反应service端的可用情况
 */
@Spi(scope = Scope.SINGLETON)
public interface HeartbeatFactory {

    /**
     * 创建心跳包
     * 
     * @return
     */
    Request createRequest();

    /**
     * 包装 handler，支持心跳包的处理
     * 
     * @param handler
     * @return
     */
    MessageHandler wrapMessageHandler(MessageHandler handler);
}
