package cn.encmed.push.entity;

public class User {
    private String deviceId;
    private String deviceType;
    private String ip;

    public User() {

    }

    public User(String deviceType, String deviceId) {
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    /**
     * @return 用户唯一标识
     */
    public int getUserUniqueKey() {
        return (ip + deviceId + deviceType).hashCode();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
