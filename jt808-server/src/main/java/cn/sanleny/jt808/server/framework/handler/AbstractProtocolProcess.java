package cn.sanleny.jt808.server.framework.handler;

import cn.sanleny.jt808.server.framework.aop.ResolveMethodInterceptor;
import io.netty.channel.Channel;
import org.springframework.cglib.proxy.Enhancer;

/**
 * 协议处理抽象类
 * @Author: LG
 * @Date: 2019/11/2
 * @Version: 1.0
 **/
public abstract class AbstractProtocolProcess implements ProtocolProcess {

    public void messageHandler(Channel channel, Jt808Message message){
        Enhancer enhancer=new Enhancer();
        enhancer.setCallback(new ResolveMethodInterceptor(message));
        enhancer.setSuperclass(this.getClass());
        AbstractProtocolProcess o = (AbstractProtocolProcess) enhancer.create();
        Jt808Message obj = o.resolve(message);
        obj = this.process(obj);
        this.send(channel,obj);
    }

    /**
     * 解析数据
     * @param message
     * @return
     */
    protected abstract Jt808Message resolve(Jt808Message message);

    /**
     * 处理逻辑
     * @param message
     * @return
     */
    protected abstract Jt808Message process(Jt808Message message);

    /**
     * 回应客户端
     * @param channel
     * @param message
     */
    protected void send(Channel channel,Jt808Message message){
        channel.writeAndFlush(message);
    }

}
