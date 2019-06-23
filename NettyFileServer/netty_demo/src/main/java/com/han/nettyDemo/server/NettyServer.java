package com.han.nettyDemo.server;

import com.han.nettyDemo.protobuf.SubscribeReqProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        startServer(port);
        LOGGER.info("Server is processing...................");
    }

    private static void startServer(int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup listenerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(listenerGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder()); // 半包处理
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            /**
                             * 貌似是将数据前加上一个头
                             *  * BEFORE DECODE (300 bytes)       AFTER DECODE (302 bytes)
                             *  * +---------------+               +--------+---------------+
                             *  * | Protobuf Data |-------------->| Length | Protobuf Data |
                             *  * |  (300 bytes)  |               | 0xAC02 |  (300 bytes)  |
                             *  * +---------------+               +--------+---------------+
                             */
                            ch.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new SubReqServerHandler());
                        }
                    });

            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            listenerGroup.shutdownGracefully();
        }


    }
}
