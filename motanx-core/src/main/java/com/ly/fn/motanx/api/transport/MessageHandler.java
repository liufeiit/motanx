package com.ly.fn.motanx.api.transport;

public interface MessageHandler {

    Object handle(Channel channel, Object message);

}
