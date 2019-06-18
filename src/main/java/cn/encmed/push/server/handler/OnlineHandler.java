package cn.encmed.push.server.handler;

import cn.encmed.push.entity.Message;
import cn.encmed.push.entity.MessageType;
import cn.encmed.push.entity.User;
import cn.encmed.push.packet.MessageCodec;
import cn.encmed.push.packet.MessagePacket;
import cn.encmed.push.server.SessionUtil;
import cn.encmed.push.packet.OnlinePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class OnlineHandler extends SimpleChannelInboundHandler<OnlinePacket> {
    private Logger logger = LoggerFactory.getLogger(MessageCodec.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OnlinePacket msg) throws Exception {
        User user = new User(msg.getDeviceType(), msg.getDeviceId());
        user.setIp(ctx.channel().remoteAddress().toString());
        SessionUtil.bindSession(user, ctx.channel());

        logger.debug("OnlineHandler : {} {}", user.getDeviceId(), user.getDeviceType());

        /**
         * 客户端上线时发送一个返回服务器当前时间的 message
         */
        Message serverTimeMessage = new Message(MessageType.SERVER_TIME, System.currentTimeMillis() + "");
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.setMessage(serverTimeMessage);
        ctx.channel().writeAndFlush(messagePacket);
    }
}
