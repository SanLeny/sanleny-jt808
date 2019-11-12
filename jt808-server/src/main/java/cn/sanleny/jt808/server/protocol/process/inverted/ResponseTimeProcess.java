package cn.sanleny.jt808.server.protocol.process.inverted;

import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 响应时间包解析，仅测试用，方便对照
 * @Author: sanleny
 * @Date: 2019-11-12
 * @Description: cn.sanleny.jt808.server.protocol.process.inverted
 * @Version: 1.0
 */
@Slf4j
public class ResponseTimeProcess extends AbstractProtocolProcess {

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        byte[] data = message.getMsgBodyBytes();
        Date date = Jt808Utils.generateDate(data, 0, data.length);
        message.setReplayTime(date);
        log.info("<<< 响应时间包解析 replayTime:{}", date);
        return message;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        return message;
    }

}
