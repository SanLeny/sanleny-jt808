package cn.sanleny.jt808.server.protocol.process;

import cn.hutool.core.date.DateUtil;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 【0x0F00】终端请求时间
 * @Author: LG
 * @Date: 2019/11/7
 * @Version: 1.0
 **/
@Slf4j
public class RequestTimeProcess extends AbstractProtocolProcess {

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        return message;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        message.setReplayType(Jt808MessageType.REQUEST_TIME_DOWN);
        message.setReplayTime(DateUtil.date());
//        log.debug(">>>终端请求时间：{}",message);
        return message;
    }
}
