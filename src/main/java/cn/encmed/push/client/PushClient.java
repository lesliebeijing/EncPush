package cn.encmed.push.client;

import cn.encmed.push.client.handler.MessageHandler;
import cn.encmed.push.packet.MessageCodec;
import cn.encmed.push.client.handler.ClientDaemon;
import cn.encmed.push.client.handler.HeartbeatKeeper;
import cn.encmed.push.packet.OnlinePacket;
import cn.encmed.push.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PushClient {
    private Logger logger = LoggerFactory.getLogger(PushClient.class);

    private ClientDaemon daemon;
    private static volatile PushClient singleton;

    public static PushClient getSingleton() {
        if (singleton == null) {
            synchronized (PushClient.class) {
                if (singleton == null) {
                    singleton = new PushClient();
                }
                return singleton;
            }
        }
        return singleton;
    }

    public void connect(String host, int port, ConnectStateChangeListener connectStateChangeListener, MessageEventListener messageEventListener) {
        Bootstrap bootstrap = new Bootstrap().group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        daemon = new ClientDaemon(bootstrap, host, port, connectStateChangeListener, true) {
            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                        this,
                        new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS),
                        new HeartbeatKeeper(),
                        new LengthFieldBasedFrameDecoder(50000, 8, 4),
                        new MessageCodec(),
                        new MessageHandler(messageEventListener)
                };
            }
        };
        daemon.startup();
    }

    public void registerToServer(String deviceId, String deviceType) {
        logger.debug("registerToServer {} {}", deviceId, deviceType);
        OnlinePacket onlinePacket = new OnlinePacket();
        onlinePacket.setDeviceId(deviceId);
        onlinePacket.setDeviceType(deviceType);
        post(onlinePacket);
    }

    public void disconnect() {
        if (daemon != null) {
            daemon.setReconnect(false);
            if (daemon.getChannel() != null && daemon.getChannel().isActive()) {
                daemon.getChannel().close();
            }
        }
    }

    private void post(Packet packet) {
        logger.debug("post {}", packet.getCmd());
        if (connected()) {
            daemon.getChannel().writeAndFlush(packet);
        } else {
            logger.debug("post !!! not connected {}", packet.getCmd());
        }
    }

    public void setReConnectDelay(int reConnectDelay) {
        if (daemon != null) {
            daemon.setReconnectDelay(reConnectDelay);
        }
    }

    public boolean connected() {
        return daemon != null && daemon.getChannel() != null && daemon.getChannel().isActive();
    }
}
