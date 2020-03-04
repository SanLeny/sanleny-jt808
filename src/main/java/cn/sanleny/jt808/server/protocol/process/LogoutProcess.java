package cn.sanleny.jt808.server.protocol.process;

import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import lombok.extern.slf4j.Slf4j;

/**
 * 【0x0003】终端注销
 * @Author: LG
 * @Date: 2019/11/7
 * @Version: 1.0
 **/
@Slf4j
public class LogoutProcess extends AbstractProtocolProcess {
    @Override
    protected Jt808Message resolve(Jt808Message message) {
        return message;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        log.debug(">>>终端注销：{}",message);
        return message;
    }
}
