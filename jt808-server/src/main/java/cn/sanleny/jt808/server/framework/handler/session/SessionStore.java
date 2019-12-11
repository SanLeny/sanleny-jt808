package cn.sanleny.jt808.server.framework.handler.session;


import io.netty.channel.Channel;

import java.io.Serializable;

public class SessionStore implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientId;    //terminalPhone

    private Channel channel;

    private boolean cleanSession;

    public SessionStore(String clientId, Channel channel, boolean cleanSession) {
        this.clientId = clientId;
        this.channel = channel;
        this.cleanSession = cleanSession;
    }

    public SessionStore(String clientId, Channel channel) {
        this.clientId = clientId;
        this.channel = channel;
        this.cleanSession = true;
    }

    public String getClientId() {
        return clientId;
    }

    public SessionStore setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public Channel getChannel() {
        return channel;
    }

    public SessionStore setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public SessionStore setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
        return this;
    }

}
