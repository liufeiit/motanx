package com.ly.fn.motanx.api.transport.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.ly.fn.motanx.api.codec.Codec;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.DefaultResponse;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.util.LoggerUtil;

public class NettyDecoder extends FrameDecoder {

    private Codec codec;
    private com.ly.fn.motanx.api.transport.Channel client;
    private int maxContentLength = 0;

    public NettyDecoder(Codec codec, com.ly.fn.motanx.api.transport.Channel client, int maxContentLength) {
        this.codec = codec;
        this.client = client;
        this.maxContentLength = maxContentLength;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() <= MotanxConstants.NETTY_HEADER) {
            return null;
        }

        buffer.markReaderIndex();

        short type = buffer.readShort();

        if (type != MotanxConstants.NETTY_MAGIC_TYPE) {
            buffer.resetReaderIndex();
            throw new MotanxFrameworkException("NettyDecoder transport header not support, type: " + type);
        }

        byte messageType = (byte) buffer.readShort();
        long requestId = buffer.readLong();

        int dataLength = buffer.readInt();

        // FIXME 如果dataLength过大，可能导致问题
        if (buffer.readableBytes() < dataLength) {
            buffer.resetReaderIndex();
            return null;
        }

        if (maxContentLength > 0 && dataLength > maxContentLength) {
            LoggerUtil.warn("NettyDecoder transport data content length over of limit, size: {}  > {}. remote={} local={}", dataLength, maxContentLength, ctx.getChannel().getRemoteAddress(),
                    ctx.getChannel().getLocalAddress());
            Exception e = new MotanxServiceException("NettyDecoder transport data content length over of limit, size: " + dataLength + " > " + maxContentLength);

            if (messageType == MotanxConstants.FLAG_REQUEST) {
                Response response = buildExceptionResponse(requestId, e);
                channel.write(response);
                throw e;
            } else {
                throw e;
            }
        }


        byte[] data = new byte[dataLength];

        buffer.readBytes(data);

        try {
            String remoteIp = getRemoteIp(channel);
            return codec.decode(client, remoteIp, data);
        } catch (Exception e) {
            if (messageType == MotanxConstants.FLAG_REQUEST) {
                Response resonse = buildExceptionResponse(requestId, e);
                channel.write(resonse);
                return null;
            } else {
                Response resonse = buildExceptionResponse(requestId, e);

                return resonse;
            }
        }
    }

    private Response buildExceptionResponse(long requestId, Exception e) {
        DefaultResponse response = new DefaultResponse();
        response.setRequestId(requestId);
        response.setException(e);
        return response;
    }


    private String getRemoteIp(Channel channel) {
        String ip = "";
        SocketAddress remote = channel.getRemoteAddress();
        if (remote != null) {
            try {
                ip = ((InetSocketAddress) remote).getAddress().getHostAddress();
            } catch (Exception e) {
                LoggerUtil.warn("get remoteIp error!dedault will use. msg:" + e.getMessage() + ", remote:" + remote.toString());
            }
        }
        return ip;

    }
}
