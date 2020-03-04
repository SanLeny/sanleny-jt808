package cn.sanleny.jt808.server.protocol.process.inverted;

import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用应答解析，仅测试用，方便对照
 * @Author: sanleny
 * @Date: 2019-11-12
 * @Description: cn.sanleny.jt808.server.protocol.process.inverted
 * @Version: 1.0
 */
@Slf4j
public class ResponseCommonProcess extends AbstractProtocolProcess {

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        byte[] data = message.getMsgBodyBytes();
        int flowId = Jt808Utils.parseIntFromBytes(data, 0, 2);
        Jt808MessageType messageType = Jt808MessageType.valueOf(Jt808Utils.parseIntFromBytes(data, 2, 2));
        byte replyCode = data[4];
        log.info("<<< 通用应答解析 flowId:{},messageType:{},replyCode:{}",flowId,messageType,replyCode);
        return message;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        return message;
    }
}
