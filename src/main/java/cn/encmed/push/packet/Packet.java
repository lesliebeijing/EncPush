package cn.encmed.push.packet;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 魔术(4字节)cmd(4字节)length(4字节)data(json数据)
 */
public abstract class Packet {
    public static final int MAGIC_NUMBER = 0x764209;

    @JSONField(serialize = false)
    public abstract int getCmd();
}
