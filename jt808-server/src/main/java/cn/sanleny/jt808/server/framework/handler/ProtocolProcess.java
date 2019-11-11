package cn.sanleny.jt808.server.framework.handler;

import io.netty.channel.Channel;

/**
 * @Author: LG
 * @Date: 2019/11/4
 * @Version: 1.0
 **/
public interface ProtocolProcess {

    void messageHandler(Channel channel, Jt808Message message);

}
