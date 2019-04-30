package cn.encmed.push.client;

import cn.encmed.push.entity.Message;

public interface MessageEventListener {
    void onReceiveMessage(Message message);
}
