package com.ly.fn.motanx.api.codec;

import java.io.IOException;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.transport.Channel;

@Spi(scope = Scope.PROTOTYPE)
public interface Codec {

    byte[] encode(Channel channel, Object message) throws IOException;

    /**
     * 
     * @param channel
     * @param remoteIp 用来在server端decode request时能获取到client的ip。
     * @param buffer
     * @return
     * @throws IOException
     */
    Object decode(Channel channel, String remoteIp, byte[] buffer) throws IOException;

}
