package com.webflux.study.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class NettyMain {
    public static void main(String[] args) throws InterruptedException {

        // 서버 실행
        Thread serverThread = new Thread(() -> {
            try {
                startServer();
            } catch (InterruptedException e) {
                log.info("[Main] InterruptedException", e);
                Thread.currentThread().interrupt();
            }
        });

        serverThread.start();

        Thread.sleep(2000);

        // 클라이언트 실행
        startClient();
    }

    private static void startServer() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ExecutorService executors = Executors.newFixedThreadPool(10);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.info("Received: {}, Thread: {}", msg, Thread.currentThread().getName());

                                    executors.submit(() -> {
                                        try {
                                            log.info("Processing: {}, Thread: {}", msg, Thread.currentThread().getName());
                                            Thread.sleep(2000);

                                            String response = "Processed: " + msg;
                                            ctx.writeAndFlush(response);
                                            log.info("Response sent: {}, Thread: {}", response, Thread.currentThread().getName());
                                        } catch (InterruptedException e) {
                                            log.error("[startServer] InterruptedException", e);
                                            Thread.currentThread().interrupt();
                                        }
                                    });
                                }
                            });

                        }
                    });

            ChannelFuture future = bootstrap.bind(8080).sync();
            log.info("#### Server started on port 8080 ####");

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            executors.shutdown();
        }
    }

    private static void startClient() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        ExecutorService executors = Executors.newFixedThreadPool(5);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new LineEndingEncoder());
                        }
                    });

            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();

            IntStream.rangeClosed(0, 5)
                    .forEach(resultId ->
                        executors.submit(() -> {
                           String message = "Message " + resultId;
                           log.info("Sending request: {}", message);

                           ChannelFuture future = channel.writeAndFlush(message);
                           future.addListener(f -> {
                               if (f.isSuccess())
                                   log.info("Request success: {}", message);
                               else
                                   log.error("Request failed: {}", message, f.cause());
                           });
                        })
                    );

            log.info("Client Sleep Start...");
            Thread.sleep(5000);
            log.info("Client Sleep End");
        } finally {
            group.shutdownGracefully();
            executors.shutdown();
            executors.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
