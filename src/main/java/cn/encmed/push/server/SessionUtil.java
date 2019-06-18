package cn.encmed.push.server;

import cn.encmed.push.entity.User;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionUtil {
    private static AttributeKey<User> sessionId = AttributeKey.valueOf("sessionId");

    private static Map<Integer, Channel> sessionMap = new ConcurrentHashMap<>();

    public static void bindSession(User user, Channel channel) {
        if (user == null) {
            return;
        }
        channel.attr(sessionId).set(user);
        sessionMap.put(user.getUserUniqueKey(), channel);
    }

    public static void unbindSession(Channel channel) {
        User user = channel.attr(sessionId).get();
        if (user != null) {
            sessionMap.remove(user.getUserUniqueKey());
            channel.attr(sessionId).set(null);
        }
    }

    public static Channel getChannel(User user) {
        if (user == null) {
            return null;
        }
        return sessionMap.getOrDefault(user.getUserUniqueKey(), null);
    }

    public static Collection<Channel> getAllChannel() {
        return sessionMap.values();
    }
}
