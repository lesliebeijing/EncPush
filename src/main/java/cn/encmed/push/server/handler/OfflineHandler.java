package cn.encmed.push.server.handler;

import cn.encmed.push.server.SessionUtil;
import cn.encmed.push.packet.OfflinePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class OfflineHandler extends SimpleChannelInboundHandler<OfflinePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OfflinePacket msg) throws Exception {
        SessionUtil.unbindSession(ctx.channel());
    }
}
