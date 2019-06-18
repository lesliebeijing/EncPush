package cn.encmed.push.server;

import cn.encmed.push.server.handler.IdleStateTrigger;
import cn.encmed.push.packet.MessageCodec;
import cn.encmed.push.packet.MessagePacket;
import cn.encmed.push.entity.Message;
import cn.encmed.push.entity.User;
import cn.encmed.push.server.handler.OfflineHandler;
import cn.encmed.push.server.handler.OnlineHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PushServer {
    private Logger logger = LoggerFactory.getLogger(PushServer.class);

    private static volatile PushServer singleton;

    public static PushServer getSingleton() {
        if (singleton == null) {
            synchronized (PushServer.class) {
                if (singleton == null) {
                    singleton = new PushServer();
                }
                return singleton;
            }
        }
        return singleton;
    }

    public void start() {
        this.start(null);
    }

    public void start(PushServerConfig config) {
        final PushServerConfig serverConfig;
        if (config != null) {
            serverConfig = config;
        } else {
            serverConfig = getDefaultConfig();
        }

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, serverConfig.getSO_BACKLOG())
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(serverConfig.getReaderIdleTime(), serverConfig.getWriterIdleTime(), serverConfig.getAllIdleTime(), TimeUnit.SECONDS));
                            ch.pipeline().addLast(new IdleStateTrigger());
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(50000, 8, 4));
                            ch.pipeline().addLast(new MessageCodec());
                            ch.pipeline().addLast(new OnlineHandler());
                            ch.pipeline().addLast(new OfflineHandler());
                        }
                    });

            ChannelFuture f = serverBootstrap.bind(serverConfig.getPort()).sync();

            logger.debug("push server start at " + serverConfig.getPort());

            f.channel().closeFuture().sync();

            logger.debug("push server close");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * publish the message to all client
     */
    public void publishToAll(Message message) {
        Collection<Channel> allChannel = SessionUtil.getAllChannel();
        logger.debug("publishToAll {} channels size {}", message.getType(), allChannel.size());
        allChannel.forEach(channel -> {
            MessagePacket messagePacket = new MessagePacket();
            messagePacket.setMessage(message);
            channel.writeAndFlush(messagePacket);
        });
    }

    public void publishToUser(User user, Message message) {
        if (user == null) {
            return;
        }
        logger.debug("publishToUser {} {}", message.getType(), user.getDeviceId());
        Channel channel = SessionUtil.getChannel(user);
        if (channel != null) {
            MessagePacket messagePacket = new MessagePacket();
            messagePacket.setMessage(message);
            channel.writeAndFlush(messagePacket);
        }
    }

    private PushServerConfig getDefaultConfig() {
        PushServerConfig defaultConfig = new PushServerConfig();
        defaultConfig.setPort(5589);
        defaultConfig.setSO_BACKLOG(512);
        defaultConfig.setReaderIdleTime(15000);
        return defaultConfig;
    }
}
