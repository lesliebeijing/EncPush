package cn.encmed.push.packet;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageCodec extends ByteToMessageCodec<Packet> {
    private Logger logger = LoggerFactory.getLogger(MessageCodec.class);

    private final Map<Integer, Class<? extends Packet>> packetMap = new HashMap<>();

    public MessageCodec() {
        // server only packet
        packetMap.put(Command.HEAT_BEAT, HeartbeatPacket.class);
        packetMap.put(Command.ONLINE, OnlinePacket.class);
        packetMap.put(Command.OFFLINE, OfflinePacket.class);
        // client only packet
        packetMap.put(Command.MESSAGE, MessagePacket.class);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        out.writeInt(Packet.MAGIC_NUMBER);
        out.writeInt(msg.getCmd());
        byte[] data = JSON.toJSONBytes(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNumber = in.getInt(0);
        if (magicNumber != Packet.MAGIC_NUMBER) {
            logger.debug("push：魔数错误");
            return;
        }
        in.readInt();

        int cmd = in.getInt(4);
        if (!packetMap.containsKey(cmd)) {
            logger.debug("push：未注册的Packet类型 " + cmd);
            return;
        }
        in.readInt();

        int length = in.readInt();
        byte[] data = new byte[length];
        in.readBytes(data);

        Packet packet = JSON.parseObject(data, packetMap.get(cmd));

        out.add(packet);
    }
}
