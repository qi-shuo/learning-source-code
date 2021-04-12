package com.qis.rpc.provider.server;

import com.qis.rpc.provider.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author qishuo1
 * @create 2021/4/12 17:49
 */
@Component
public class RpcServer implements DisposableBean, CommandLineRunner {

    private NioEventLoopGroup bossEventGroup;
    private NioEventLoopGroup workerEventGroup;

    public void startServer(String ip, int port) {
        try {
            bossEventGroup = new NioEventLoopGroup(1);
            workerEventGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventGroup, workerEventGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast(new StringDecoder());
                            channelPipeline.addLast(new StringEncoder());
                            channelPipeline.addLast(new RpcServerHandler());
                        }
                    });
            ChannelFuture sync = serverBootstrap.bind(ip, port).sync();
            System.out.println("==========服务端启动成功==========");
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }

    @Override
    public void destroy() {
        if (bossEventGroup != null) {
            bossEventGroup.shutdownGracefully();
        }
        if (workerEventGroup != null) {
            workerEventGroup.shutdownGracefully();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>())
                .execute(() -> {
                    startServer("127.0.0.1", 8899);
                });
    }
}
