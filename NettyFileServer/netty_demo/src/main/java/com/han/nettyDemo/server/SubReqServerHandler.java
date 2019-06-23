package com.han.nettyDemo.server;

import com.han.nettyDemo.protobuf.SubscribeReqProto;
import com.han.nettyDemo.protobuf.SubscribeRespProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubReqServerHandler extends ChannelHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(SubReqServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        SubscribeReqProto.SubscribeReq subscribeReq = (SubscribeReqProto.SubscribeReq )msg;
        LOGGER.info("==================================================");
        LOGGER.info(String.format("Server reciveced request: \n username : %s \n product: %s",subscribeReq.getUserName(),subscribeReq.getProductName()));
        LOGGER.info("==================================================");
        ctx.writeAndFlush(builderResp(subscribeReq));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    private SubscribeRespProto.SubscribeResp builderResp(SubscribeReqProto.SubscribeReq subscribeReq){
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqID(subscribeReq.getSubReqID())
               .setRespCode(200)
               .setDesc("我是一个Subscribe response");

        return builder.build();

    }
}
