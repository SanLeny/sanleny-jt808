package cn.sanleny.jt808.server.protocol.process;

import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import lombok.extern.slf4j.Slf4j;

/**
 * 【0x0002】链路心跳
 * @Author: LG
 * @Date: 2019/11/5
 * @Version: 1.0
 **/
@Slf4j
public class LinkHeartbeatProcess extends AbstractProtocolProcess {

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        return message;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        log.debug(">>>链路心跳：{}",message);
        return message;
    }
}
