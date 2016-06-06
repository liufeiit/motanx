package com.ly.fn.motanx.api.transport.netty;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.ly.fn.motanx.api.codec.Codec;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.rpc.DefaultResponse;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.util.ByteUtil;
import com.ly.fn.motanx.api.util.LoggerUtil;

public class NettyEncoder extends OneToOneEncoder {
    private Codec codec;
    private com.ly.fn.motanx.api.transport.Channel client;

    public NettyEncoder(Codec codec, com.ly.fn.motanx.api.transport.Channel client) {
        this.codec = codec;
        this.client = client;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel nettyChannel, Object message) throws Exception {

        long requestId = getRequestId(message);
        byte[] data = null;

        if (message instanceof Response) {
            try {
                data = codec.encode(client, message);
            } catch (Exception e) {
                LoggerUtil.error("NettyEncoder encode error, identity=" + client.getUrl().getIdentity(), e);
                Response response = buildExceptionResponse(requestId, e);
                data = codec.encode(client, response);
            }
        } else {
            data = codec.encode(client, message);
        }

        byte[] transportHeader = new byte[MotanxConstants.NETTY_HEADER];
        ByteUtil.short2bytes(MotanxConstants.NETTY_MAGIC_TYPE, transportHeader, 0);
        transportHeader[3] = getType(message);
        ByteUtil.long2bytes(getRequestId(message), transportHeader, 4);
        ByteUtil.int2bytes(data.length, transportHeader, 12);

        return ChannelBuffers.wrappedBuffer(transportHeader, data);
    }

    private long getRequestId(Object message) {
        if (message instanceof Request) {
            return ((Request) message).getRequestId();
        } else if (message instanceof Response) {
            return ((Response) message).getRequestId();
        } else {
            return 0;
        }
    }

    private byte getType(Object message) {
        if (message instanceof Request) {
            return MotanxConstants.FLAG_REQUEST;
        } else if (message instanceof Response) {
            return MotanxConstants.FLAG_RESPONSE;
        } else {
            return MotanxConstants.FLAG_OTHER;
        }
    }

    private Response buildExceptionResponse(long requestId, Exception e) {
        DefaultResponse response = new DefaultResponse();
        response.setRequestId(requestId);
        response.setException(e);
        return response;
    }
}
