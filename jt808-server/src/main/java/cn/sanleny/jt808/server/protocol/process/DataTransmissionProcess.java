package cn.sanleny.jt808.server.protocol.process;

import cn.hutool.core.date.DateUtil;
import cn.sanleny.jt808.server.common.service.KafkaService;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.exception.GlobalFallbackException;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import cn.sanleny.jt808.server.protocol.entity.DataTransmission;
import cn.sanleny.jt808.server.protocol.entity.Driving;
import cn.sanleny.jt808.server.protocol.entity.FaultCode;
import cn.sanleny.jt808.server.protocol.entity.TravelReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 【0x0900】数据上行透传
 * @Author: LG
 * @Date: 2019/11/5
 * @Version: 1.0
 **/
@Slf4j
public class DataTransmissionProcess extends AbstractProtocolProcess {

    @Autowired
    private KafkaService kafkaService;

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        DataTransmission msg = new DataTransmission(message);
        try {
            byte[] data = message.getMsgBodyBytes();
            Jt808MessageType messageType = Jt808MessageType.valueOf(Jt808Utils.parseIntFromBytes(data,0,1));
            byte[] tmp = new byte[data.length - 1];
            System.arraycopy(data, 1, tmp, 0, tmp.length);
            msg.setMsgBodyBytes(tmp);
            msg.setMessageType(messageType);
            switch (messageType){
                case OBD_DRIVING_DATA:
                    //OBD行车数据
                    return new Driving(msg);
                case OBD_TRAVEL_REPORT_DATA:
                    //OBD行程报告数据
                    return new TravelReport(msg);
                case OBD_FAULT_CODE:
                    //OBD故障码
                    return new FaultCode(msg);
                default:
                    throw new GlobalFallbackException("OBD透传消息类型:"+ messageType + "暂时不支持！");
            }
        }catch (Exception e){
            msg.setReplyCode(Jt808Constants.RESP_MSG_ERROR);
            log.error(e.toString(),e);
        }
        return msg;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        if(message.getReplyCode() != Jt808Constants.RESP_SUCCESS){
            return message;
        }
        DataTransmission dataMsg = (DataTransmission) message;
        dataMsg.setReplayTime(DateUtil.date());
        switch (dataMsg.getMessageType()){
            case OBD_DRIVING_DATA:
                //OBD行车数据
                Driving driving = (Driving) dataMsg;
                log.debug("OBD行车数据:{}", driving);
                kafkaService.send("obd_driving" ,driving);
                return driving;
            case OBD_TRAVEL_REPORT_DATA:
                //OBD行程报告数据
                TravelReport travelReportMsg = (TravelReport) dataMsg;
                log.debug("OBD行程报告数据:{}",travelReportMsg);
                kafkaService.send("obd_travelReport" ,travelReportMsg);
                return travelReportMsg;
            case OBD_FAULT_CODE:
                //OBD故障码
                FaultCode faultCodeMsg = (FaultCode) dataMsg;
                log.debug("OBD故障码:{}",faultCodeMsg);
                kafkaService.send("obd_faultCode" ,faultCodeMsg);
                return faultCodeMsg;
        }
        return message;
    }

}
