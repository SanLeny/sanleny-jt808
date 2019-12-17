package cn.sanleny.jt808.server.protocol.process;

import cn.hutool.core.util.NumberUtil;
import cn.sanleny.jt808.server.common.service.KafkaService;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import cn.sanleny.jt808.server.protocol.entity.LocationInfo;
import cn.sanleny.jt808.server.protocol.entity.LocationInfoBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static cn.sanleny.jt808.server.framework.utils.Jt808Utils.parseIntFromBytes;

/**
 * 定位数据批量上传
 * @Author: sanleny
 * @Date: 2019-12-09
 * @Version: 1.0
 */
@Slf4j
public class LocationInfoBatchProcess extends AbstractProtocolProcess {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KafkaService kafkaService;

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        LocationInfoBatch msg = new LocationInfoBatch(message);
        try {
            byte[] data = message.getMsgBodyBytes();
            msg.setDataNumber(parseIntFromBytes(data, 0, 2));
            msg.setType(parseIntFromBytes(data, 2, 1));
            int dataLength = parseIntFromBytes(data, 3, 2);
            msg.setDataLength(parseIntFromBytes(data, 3, 2));
            byte[] locationData = new byte[dataLength];
            System.arraycopy(data, 5, locationData, 0, locationData.length);
            msg.setLocationData(locationData);
            int srcPos = 0;
            List<LocationInfo> locationInfos = new ArrayList<>();
            for (int i = 0; i < msg.getDataNumber(); i++) {
                int tmpLength = 28; //位置基本信息
                tmpLength += 2; //位置附件信息 附件信息ID(Byte) + 附件信息长度（Byte）
                int len = parseIntFromBytes(locationData, srcPos + tmpLength - 1, 1);
                tmpLength += len;

                tmpLength += 2; //位置附件信息 附件信息ID(Byte) + 附件信息长度（Byte）
                len = parseIntFromBytes(locationData, srcPos + tmpLength - 1, 1); //fuj
                tmpLength += len;
                byte[] tmp = new byte[tmpLength];

                System.arraycopy(locationData, srcPos, tmp, 0, tmp.length);
                message.setMsgBodyBytes(tmp);
                LocationInfo info = this.getLocationInfo(tmp);
                locationInfos.add(info);
                srcPos += tmpLength;
            }
            msg.setLocationInfos(locationInfos);
        }catch (Exception e){
            msg.setReplyCode(Jt808Constants.RESP_MSG_ERROR);
            log.error("解析定位数据批量上传数据错误：{}" + e.toString(),e);
        }
        return msg;
    }

    private LocationInfo getLocationInfo(byte[] data){
        LocationInfo msg = new LocationInfo();
        // 1. byte[0-3] 报警标志(DWORD(32))
        msg.setWarningFlagField(parseIntFromBytes(data, 0, 4));
        // 2. byte[4-7] 状态(DWORD(32))
        msg.setStatusField(parseIntFromBytes(data, 4, 4));
        // 3. byte[8-11] 纬度(DWORD(32)) 以度为单位的纬度值乘以10^6，精确到百万分之一度
        msg.setLatitude(NumberUtil.div(parseIntFromBytes(data, 8, 4),100_0000));
        // 4. byte[12-15] 经度(DWORD(32)) 以度为单位的经度值乘以10^6，精确到百万分之一度
        msg.setLongitude(NumberUtil.div(parseIntFromBytes(data, 12, 4),100_0000));
        // 5. byte[16-17] 高程(WORD(16)) 海拔高度，单位为米（ m）
        msg.setElevation(parseIntFromBytes(data, 16, 2));
        // byte[18-19] 速度(WORD) 1/10km/h
        msg.setSpeed(NumberUtil.div(parseIntFromBytes(data, 18, 2),10));
        // byte[20-21] 方向(WORD) 0-359，正北为 0，顺时针
        msg.setDirection(parseIntFromBytes(data, 20, 2));
        // byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
        // GMT+8 时间，本标准中之后涉及的时间均采用此时区
        msg.setTime(Jt808Utils.generateDate(data,22,6));

        msg.setMileage(NumberUtil.div(parseIntFromBytes(data, 30, 4),10));
        msg.setOilMass(NumberUtil.div(parseIntFromBytes(data, 36, 2),10));
        return msg;
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        LocationInfoBatch msg = (LocationInfoBatch) message;
        if(msg.getReplyCode() != Jt808Constants.RESP_SUCCESS){
            return msg;
        }
        for (LocationInfo locationInfo : msg.getLocationInfos()) {
            locationInfo.setHeader(message.getHeader());
            kafkaService.send("obd_location" ,locationInfo);
        }
        return msg;
    }

}
