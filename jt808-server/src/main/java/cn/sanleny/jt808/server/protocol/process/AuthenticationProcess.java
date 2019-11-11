package cn.sanleny.jt808.server.protocol.process;

import cn.sanleny.jt808.server.protocol.entity.Authentication;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 【0x0102】终端鉴权
 * @Author: LG
 * @Date: 2019/11/7
 * @Version: 1.0
 **/
@Slf4j
public class AuthenticationProcess extends AbstractProtocolProcess {

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        return new Authentication(message);
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        if(message.getReplyCode() != Jt808Constants.RESP_SUCCESS) return message;
        //TODO 先去除校验，假设都成功
        Authentication msg = (Authentication) message;
        log.debug(">>>终端鉴权：{}",msg);
//        if(!StrUtil.equals("123",msg.getAuthToken())){
//            msg.setReplyCode(Jt808Constants.RESP_FAILURE);
//            log.error("终端鉴权失败，鉴权验证码：{}" + msg.getAuthToken());
//        }
        return message;
    }
}
