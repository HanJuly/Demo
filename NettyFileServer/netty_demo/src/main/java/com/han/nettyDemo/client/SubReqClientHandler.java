package com.han.nettyDemo.client;

import com.han.nettyDemo.protobuf.SubscribeReqProto;
import com.han.nettyDemo.protobuf.SubscribeRespProto;
import com.han.nettyDemo.server.SubReqServerHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubReqClientHandler extends ChannelHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(SubReqClientHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        for(int i = 0; i < 10; i++){
            ctx.write(builderSubreq());
        }
        ctx.flush();
    }

    private Object builderSubreq() {
        SubscribeReqProto.SubscribeReq.Builder builder
                = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1)
                .setAddress("我在宝安")
                .setProductName("手机")
                .setUserName("han");
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        SubscribeRespProto.SubscribeResp subscribeResp= (SubscribeRespProto.SubscribeResp)msg;
        LOGGER.info("==================================================");
        LOGGER.info(String.format("Receive server response :\n code:%s \n desc:%s",subscribeResp.getRespCode(),subscribeResp.getDesc()));
        LOGGER.info("==================================================");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }
}
