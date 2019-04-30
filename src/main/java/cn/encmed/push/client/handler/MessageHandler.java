package cn.encmed.push.client.handler;

import cn.encmed.push.client.MessageEventListener;
import cn.encmed.push.entity.Message;
import cn.encmed.push.packet.MessagePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<MessagePacket> {
    private Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private MessageEventListener messageEventListener;

    public MessageHandler(MessageEventListener messageEventListener) {
        this.messageEventListener = messageEventListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePacket msg) throws Exception {
        if (messageEventListener != null) {
            Message message = msg.getMessage();
            logger.debug("receive message {} {} ", message.getType(), message.getContent());
            messageEventListener.onReceiveMessage(message);
        }
    }
}
