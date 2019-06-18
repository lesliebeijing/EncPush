package cn.encmed.push.packet;

import cn.encmed.push.entity.Message;

public class MessagePacket extends Packet {
    private Message message;

    @Override
    public int getCmd() {
        return Command.MESSAGE;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
