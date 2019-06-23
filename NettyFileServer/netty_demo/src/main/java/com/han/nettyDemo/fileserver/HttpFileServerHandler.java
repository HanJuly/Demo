package com.han.nettyDemo.fileserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpFileServerHandler.class);

    private String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.getUri();
        String path = System.getProperty("user.dir") + File.separator + uri;

        File file = new File(path);
        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                listFiles(ctx, file);
            } else {
                redirect(ctx, uri + "/");
            }
            return;
        }

        if (!file.isFile()) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(CONTENT_LENGTH, fileLength);
        response.headers().set(CONTENT_TYPE, "application/octet-stream");
        response.headers().add(CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        ctx.write(response);
        ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
                LOGGER.info("file {} transfer complete.", file.getName());
                raf.close();
            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                                            long progress, long total) throws Exception {
                if (total < 0) {
                    LOGGER.warn("file {} transfer progress: {}", file.getName(), progress);
                } else {
                    LOGGER.debug("file {} transfer progress: {}/{}", file.getName(), progress, total);
                }
            }
        });
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void redirect(ChannelHandlerContext ctx, String url) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set("location", url);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void listFiles(ChannelHandlerContext cts, File dir) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\r\n")
                .append("<html >\r\n")
                .append("<head>\r\n")
                .append("<meta charset=\"utf-8\">\r\n")
                .append("<title>\r\n")
                .append("Netty 文件服务器")
                .append("</title>\r\n")
                .append("</head>\r\n")
                .append("<body>\r\n")
                .append("<h3>\r\n")
                .append("当前目录：" + dir.getPath())
                .append("</h3>\r\n")
                .append("<ul>\r\n")
                .append("<li><a href=\"..\">..</a></li>\r\n");
        for (File f : dir.listFiles()) {
            if (f.isHidden() || !f.canRead()) {
                continue;
            }
            String name = f.getName();

            html.append("<li><a href=\"" + name + "\">" + name + "</a></li>\r\n");
        }
        html.append("</ul>\r\n")
                .append("</body>\r\n")
                .append("</html>\r\n");
        ByteBuf buf = Unpooled.copiedBuffer(html, CharsetUtil.UTF_8);
        response.content().writeBytes(buf);
        buf.release();
        cts.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        LOGGER.info("======================================");
        LOGGER.info(html.toString());
        LOGGER.info("======================================");

    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
                Unpooled.copiedBuffer(String.format("Failure: %s\r\n", status), CharsetUtil.UTF_8));
        response.headers().set("content-type", "text/plain;charset=utf-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
