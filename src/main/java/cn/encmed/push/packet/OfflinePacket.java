package cn.encmed.push.packet;

public class OfflinePacket extends Packet {
    @Override
    public int getCmd() {
        return Command.OFFLINE;
    }
}
