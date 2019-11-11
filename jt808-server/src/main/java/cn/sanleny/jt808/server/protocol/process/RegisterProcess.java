package cn.sanleny.jt808.server.protocol.process;

import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.protocol.entity.Register;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import lombok.extern.slf4j.Slf4j;

/**
 * 【0x0100】终端注册
 * @Author: LG
 * @Date: 2019/11/7
 * @Version: 1.0
 **/
@Slf4j
public class RegisterProcess extends AbstractProtocolProcess {

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        return new Register(message);
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        if(message.getReplyCode() != Jt808Constants.RESP_SUCCESS) return message;
        Register msg = (Register) message;
        //TODO 注册逻辑,鉴权码暂时写死
        msg.setReplayToken("123");
        log.debug("终端注册:{}",msg);
        return msg;
    }

}
