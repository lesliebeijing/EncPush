package cn.encmed.push.packet;

public class HeartbeatPacket extends Packet {
    @Override
    public int getCmd() {
        return Command.HEAT_BEAT;
    }
}
