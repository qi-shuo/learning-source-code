package com.qis.provider.server;

import com.qis.rpc.registry.impl.RpcRegistryFactory;
import com.qis.rpc.service.RpcRegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import com.qis.provider.handler.RpcServiceHandler;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.qis.provider.ProviderBootstrap.SERVICE_INSTANCE_MAP;
import static com.qis.provider.config.NettyConfig.IP;
import static com.qis.provider.config.NettyConfig.PORT;


/**
 * @author qishuo
 * @date 2021/4/24 10:33 下午
 */
@Service
@Slf4j
public class NettyServer implements CommandLineRunner, DisposableBean {

    @Resource
    private RpcServiceHandler rpcServiceHandler;
    @Resource
    private RpcRegistryFactory registryFactory;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;


    @Override
    public void run(String... args) throws Exception {
        new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>())
                .execute(this::startServer);
    }

    private void startServer() {
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(rpcServiceHandler);

                    }
                });
        try {
            ChannelFuture sync = serverBootstrap.bind(IP, PORT).sync();
            System.out.println("==========服务端启动成功==========");
            registry(IP, PORT);
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            destroy();
        }


    }

    @Override
    public void destroy() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }

    private void registry(final String ip, final int port) throws Exception {
        RpcRegistryService rpcRegistryService = registryFactory.getObject();
        if (null == rpcRegistryService) {
            System.out.println("registryHandler is null");
            throw new RuntimeException("registryHandler is null");
        }
        SERVICE_INSTANCE_MAP.forEach((key, value) -> {
            rpcRegistryService.registry(ip, port, key);
        });

    }
}
