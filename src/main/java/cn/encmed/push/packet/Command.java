package cn.encmed.push.packet;

public interface Command {
    int HEAT_BEAT = 0x1234;
    int ONLINE = 0x1235;
    int OFFLINE = 0x1236;
    int MESSAGE = 0x1256;
}

