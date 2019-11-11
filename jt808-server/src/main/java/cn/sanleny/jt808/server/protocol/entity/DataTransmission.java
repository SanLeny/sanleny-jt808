package cn.sanleny.jt808.server.protocol.entity;

import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import lombok.Data;

/**
 * 数据上行透传
 * @Author: LG
 * @Date: 2019/11/5
 * @Version: 1.0
 **/
@Data
public class DataTransmission extends Jt808Message {

    protected Jt808MessageType messageType;

    public DataTransmission(Jt808Message message) {
        this.header = message.getHeader();
        this.checkSum = message.getCheckSum();
        this.msgBodyBytes = message.getMsgBodyBytes();
        this.replayType = Jt808MessageType.DATA_TRANSMISSION_DOWN;
    }

}
