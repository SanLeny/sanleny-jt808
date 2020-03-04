package cn.sanleny.jt808.server.protocol.process;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.protocol.entity.Authentication;
import cn.sanleny.jt808.server.utils.RsaKeyUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.RSAPrivateKey;

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
        Authentication msg = (Authentication) message;
        log.debug(">>>终端鉴权：{}",msg);
        RSAPrivateKey privateKey = RsaKeyUtil.getRsaPrivateKey();
        RSA rsa = new RSA(privateKey, null);
        if(!StrUtil.equals(msg.getAuthToken(),rsa.encryptBcd(message.getHeader().getTerminalPhone(), KeyType.PrivateKey))){
            msg.setReplyCode(Jt808Constants.RESP_FAILURE);
            log.error("终端鉴权失败，鉴权验证码：{}" , msg.getAuthToken());
        }
        return message;
    }
}
