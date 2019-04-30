package cn.encmed.push.packet;

public class OnlinePacket extends Packet {
    @Override
    public int getCmd() {
        return Command.ONLINE;
    }

    private String deviceType;

    private String deviceId;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
