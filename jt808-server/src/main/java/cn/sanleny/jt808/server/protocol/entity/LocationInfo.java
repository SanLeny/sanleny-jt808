package cn.sanleny.jt808.server.protocol.entity;

import cn.hutool.core.util.NumberUtil;
import cn.sanleny.jt808.server.framework.annotation.Jt808Field;
import cn.sanleny.jt808.server.framework.annotation.Jt808Math;
import cn.sanleny.jt808.server.framework.annotation.Jt808Resolver;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import lombok.Data;

import java.util.Date;

/**
 * @Author: LG
 * @Date: 2019/11/2
 * @Version: 1.0
 **/
@Data
@Jt808Resolver
public class LocationInfo extends Jt808Message {

    // byte[0-3] 报警标志(DWord(32))
    @Jt808Field(index = 0,length = 4)
    private int warningFlagField;

    // byte[4-7] 状态(DWORD(32))
    @Jt808Field(index = 4,length = 4)
    private int statusField;

    // byte[8-11] 纬度(DWORD(32)) 以度位单位的纬度值乘以10的6次方，精确到百万分之一度
    @Jt808Field(index = 8,length = 4)
    @Jt808Math(aClass = NumberUtil.class, method = "div", number = 100_0000 )
    private double latitude;

    // byte[12-15] 经度(DWORD(32)) 以度位单位的纬度值乘以10的6次方，精确到百万分之一度
    @Jt808Field(index = 12,length = 4)
    @Jt808Math(aClass = NumberUtil.class, method = "div", number = 100_0000 )
    private double longitude;

    // byte[16-17] 高程(WORD(16)) 海拔高度，单位为米（ m）
    @Jt808Field(index = 16,length = 2)
    private int elevation;

    // byte[18-19] 速度(WORD) 1/10km/h
    @Jt808Field(index = 18,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div", number = 10 )
    private double speed;

    // byte[20-21] 方向(WORD) 0-359，正北为 0，顺时针
    @Jt808Field(index = 20,length = 2)
    private int direction;

    // byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss (GMT+8 时间，本标准中之后涉及的时间均采用此时区)
    @Jt808Field(index = 22,length = 6)
    private Date time;

    public LocationInfo(){

    }

    public LocationInfo(Jt808Message message) {
        this();
        this.header = message.getHeader();
        this.checkSum = message.getCheckSum();
        this.msgBodyBytes = message.getMsgBodyBytes();
    }

}
