package com.ly.fn.motanx.api.transport.support;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.DefaultRequest;
import com.ly.fn.motanx.api.rpc.DefaultResponse;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.transport.Channel;
import com.ly.fn.motanx.api.transport.HeartbeatFactory;
import com.ly.fn.motanx.api.transport.MessageHandler;
import com.ly.fn.motanx.api.util.RequestIdGenerator;

@SpiMeta(name = "motanx")
public class DefaultRpcHeartbeatFactory implements HeartbeatFactory {

    @Override
    public Request createRequest() {
        DefaultRequest request = new DefaultRequest();

        request.setRequestId(RequestIdGenerator.getRequestId());
        request.setInterfaceName(MotanxConstants.HEARTBEAT_INTERFACE_NAME);
        request.setMethodName(MotanxConstants.HEARTBEAT_METHOD_NAME);
        request.setParamtersDesc(MotanxConstants.HHEARTBEAT_PARAM);

        return request;
    }

    @Override
    public MessageHandler wrapMessageHandler(MessageHandler handler) {
        return new HeartMessageHandleWrapper(handler);
    }

    public static boolean isHeartbeatRequest(Object message) {
        if (!(message instanceof Request)) {
            return false;
        }

        Request request = (Request) message;

        return MotanxConstants.HEARTBEAT_INTERFACE_NAME.equals(request.getInterfaceName())
                && MotanxConstants.HEARTBEAT_METHOD_NAME.equals(request.getMethodName())
                && MotanxConstants.HHEARTBEAT_PARAM.endsWith(request.getParamtersDesc());
    }


    private class HeartMessageHandleWrapper implements MessageHandler {
        private MessageHandler messageHandler;

        public HeartMessageHandleWrapper(MessageHandler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public Object handle(Channel channel, Object message) {
            if (isHeartbeatRequest(message)) {
                DefaultResponse response = new DefaultResponse();
                response.setValue("heartbeat");
                return response;
            }

            return messageHandler.handle(channel, message);
        }


    }
}
