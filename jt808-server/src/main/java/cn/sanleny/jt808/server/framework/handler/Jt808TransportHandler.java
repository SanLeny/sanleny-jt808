package cn.sanleny.jt808.server.framework.handler;

import cn.hutool.core.util.StrUtil;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.handler.session.SessionStore;
import cn.sanleny.jt808.server.framework.utils.SnowflakeUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: LG
 * @Date: 2019/11/1
 * @Version: 1.0
 **/
@Slf4j
@Component
@ChannelHandler.Sharable
public class Jt808TransportHandler extends SimpleChannelInboundHandler<Jt808Message> {

    @Autowired
    private ProtocolProcessFactory protocolProcessFactory;

    @Autowired
    private SessionStoreService sessionStoreService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Jt808Message message) throws Exception {
        this.initSession(ctx.channel());
        if(null != message.getThrowable()){
            //TODO 返回错误信息给客户端
            message.setReplyCode(Jt808Constants.RESP_FAILURE);
            ctx.channel().writeAndFlush(message);
            return ;
        }
        Jt808MessageType messageType = message.getHeader().getMessageType();
        log.info(">>>> msgId:{},phone:{}",messageType,message.getHeader().getTerminalPhone());
        ProtocolProcess instance = protocolProcessFactory.getInstance(messageType);
        if(null == instance){
            log.warn(">>>> msgId:{} 对应的方法未实现,phone:{} ",messageType,message.getHeader().getTerminalPhone());
            return;
        }
        instance.messageHandler(ctx.channel(),message);
    }

    /**
     * 异常情况
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.cleanSession(ctx.channel());
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * 连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        long id = SnowflakeUtils.nextId();
        ctx.channel().attr(AttributeKey.valueOf("id")).set(id);
        log.debug("终端连接:{}", id);
        super.channelActive(ctx);
    }

    /**
     * 断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("终端断开连接:{}", ctx.channel().attr(AttributeKey.valueOf("id")).get());
        this.cleanSession(ctx.channel());
        super.channelInactive(ctx);
    }

    /**
     * 心跳检测异常
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {//超过设置的段时间没有接收或发送数据
                this.cleanSession(ctx.channel());
                log.error("服务器主动断开连接:{}", ctx.channel().attr(AttributeKey.valueOf("id")).get());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 清除session
     * @param channel
     */
    public void cleanSession(Channel channel){
        AttributeKey<String> attributeKey = AttributeKey.valueOf("clientId");
        String clientId = channel.attr(attributeKey).get();
        if(StrUtil.isNotBlank(clientId)){
            sessionStoreService.remove(clientId);
        }

        //测试用，避免同一连接出现多个 session
        String id = channel.attr(AttributeKey.valueOf("id")).get().toString();
        sessionStoreService.connectClean(id);
    }

    /**
     * 初始化session
     * @param channel
     */
    public void initSession(Channel channel){
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore session = sessionStoreService.get(clientId);
        if(session == null){
            channel.attr(AttributeKey.valueOf("clientId")).set(clientId);
            session = new SessionStore(clientId,channel);
            sessionStoreService.put(clientId,session);

            //测试用，避免同一连接出现多个 session
            String id = channel.attr(AttributeKey.valueOf("id")).get().toString();
            sessionStoreService.connectRemove(id,clientId);
        }
    }

}
