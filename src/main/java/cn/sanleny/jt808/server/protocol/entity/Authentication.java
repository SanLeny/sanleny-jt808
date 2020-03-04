package cn.sanleny.jt808.server.protocol.entity;

import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.annotation.Jt808Field;
import cn.sanleny.jt808.server.framework.annotation.Jt808Resolver;
import lombok.Data;

/**
 * 终端鉴权
 * @Author: LG
 * @Date: 2019/11/7
 * @Version: 1.0
 **/
@Data
@Jt808Resolver
public class Authentication extends Jt808Message {


    @Jt808Field(index = 0,length = -1)
    private String authToken;

    public Authentication(Jt808Message message) {
        this.header = message.getHeader();
        this.checkSum = message.getCheckSum();
        this.msgBodyBytes = message.getMsgBodyBytes();
    }
}
