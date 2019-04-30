package cn.encmed.push.client.handler;

import cn.encmed.push.packet.HeartbeatPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class HeartbeatKeeper extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                HeartbeatPacket heartbeatPacket = new HeartbeatPacket();
                ctx.channel().writeAndFlush(heartbeatPacket);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}