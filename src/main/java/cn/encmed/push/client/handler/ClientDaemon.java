package cn.encmed.push.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Sharable
public abstract class ClientDaemon extends ChannelInboundHandlerAdapter implements TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(ClientDaemon.class);
    private final Bootstrap bootstrap;
    private final String host;
    private final int port;
    private volatile boolean reconnect;
    private int attempts;
    private Channel channel;
    private HashedWheelTimer timer = new HashedWheelTimer();
    private int reconnectDelay = 5;

    public ClientDaemon(Bootstrap bootstrap, String host, int port, boolean reconnect) {
        this.bootstrap = bootstrap;
        this.host = host;
        this.port = port;
        this.reconnect = reconnect;
    }

    public abstract ChannelHandler[] handlers();

    public Channel getChannel() {
        return this.channel;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelActive: {}", ctx.channel().remoteAddress());
        this.channel = ctx.channel();
        ctx.fireChannelActive();
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelInactive: {}", ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
        this.channel = null;

        if (this.reconnect) {
            this.attempts = 0;
            this.scheduleReconnect();
        }
    }

    public void startup() {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(ClientDaemon.this.handlers());
            }
        });
        connect();
    }

    private void connect() {
        this.bootstrap.connect(this.host, this.port).addListener((future) -> {
            if (future.isSuccess()) {
                logger.debug("connected to {}:{}", host, port);
                this.attempts = 0;
            } else {
                logger.debug("connect failed {} , to reconnect in {}s", this.attempts, this.reconnectDelay);
                this.scheduleReconnect();
            }
        });
    }

    public void run(Timeout timeout) {
        synchronized (this.bootstrap) {
            ++this.attempts;
            connect();
        }
    }

    private void scheduleReconnect() {
        timer.newTimeout(this, reconnectDelay, TimeUnit.SECONDS);
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public void setReconnectDelay(int reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }
}

