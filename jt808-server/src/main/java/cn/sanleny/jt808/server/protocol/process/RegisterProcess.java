package cn.sanleny.jt808.server.protocol.process;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.protocol.entity.Register;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.utils.RsaKeyUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.RSAPrivateKey;

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
        RSAPrivateKey privateKey = RsaKeyUtil.getRsaPrivateKey();
        RSA rsa = new RSA(privateKey, null);
        String tocken = rsa.encryptBase64(StrUtil.bytes(message.getHeader().getTerminalPhone(), CharsetUtil.CHARSET_GBK), KeyType.PrivateKey);
        msg.setReplayToken(tocken);
        msg.setReplayType(Jt808MessageType.REGISTER_DOWN);
        return msg;
    }

}
