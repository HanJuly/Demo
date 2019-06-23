package com.han.nettyDemo.fileserver;

import com.han.nettyDemo.protobuf.SubscribeReqProto;
import com.han.nettyDemo.server.SubReqServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyFileServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyFileServer.class);

    public static void main(String[] args) throws Exception {
        int port = 8081;
        String url = "/src/main/java/com/han/nettyDemo";
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
            if(args.length >1){
                url = args[1];
            }
        }
        startServer(port,url);
        LOGGER.info("Server is processing...................");
    }

    private static void startServer(int port,String url) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup listenerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(listenerGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder",new HttpRequestDecoder())// 半包处理
                                         .addLast("http-aggregator",new HttpObjectAggregator(65536))
                                         .addLast("http-encoder",new HttpResponseEncoder())
                                         .addLast("http-chunked",new ChunkedWriteHandler())
                                         .addLast("fileServerHandler",new HttpFileServerHandler(url));
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
