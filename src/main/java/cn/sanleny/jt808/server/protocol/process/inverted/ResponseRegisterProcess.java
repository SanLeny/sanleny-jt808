package cn.sanleny.jt808.server.protocol.process.inverted;

import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端注册应答包解析，仅测试用，方便对照
 * @Author: sanleny
 * @Date: 2019-11-13
 * @Description: cn.sanleny.jt808.server.protocol.process.inverted
 * @Version: 1.0
 */
@Slf4j
public class ResponseRegisterProcess extends AbstractProtocolProcess {
    @Override
    protected Jt808Message resolve(Jt808Message message) {
        byte[] data = message.getMsgBodyBytes();
        int flowId = Jt808Utils.parseIntFromBytes(data, 0, 2);
        /**
         * 0：成功；
         * 1：车辆已经注册；
         * 2：数据库中无该车辆；
         * 3：终端已经被注册；
         * 4：数据库中无该终端；
         */
        byte replyCode = data[2];
        String replayToken = Jt808Utils.parseStringFromBytes(data, 3, data.length - 3);
        log.info("<<< 终端注册应答包解析 flowId:{},replyCode:{},replayToken:{}",flowId,replyCode,replayToken);
        return message;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        return message;
    }
}
