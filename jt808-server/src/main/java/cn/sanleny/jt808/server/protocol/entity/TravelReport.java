package cn.sanleny.jt808.server.protocol.entity;

import cn.hutool.core.util.NumberUtil;
import cn.sanleny.jt808.server.framework.annotation.Jt808Field;
import cn.sanleny.jt808.server.framework.annotation.Jt808Math;
import cn.sanleny.jt808.server.framework.annotation.Jt808Resolver;
import lombok.Data;

import java.util.Date;

/**
 * OBD行程报告数据
 * @Author: LG
 * @Date: 2019/11/5
 * @Version: 1.0
 **/
@Data
@Jt808Resolver
public class TravelReport extends DataTransmission {

    // 1. byte[0-5] 开始时间(BCD[6]) 时间格式：YYMMDDhhmmss
    @Jt808Field(index = 0,length = 6)
    private Date beginTime;
    // 2. byte[0-5] 结束时间(BCD[6]) 时间格式：YYMMDDhhmmss
    @Jt808Field(index = 6,length = 6)
    private Date endTime;
    // 3. 上传类型 byte[12] BYTE  0-主动上传；1-查询上传
    @Jt808Field(index = 12,length = 1)
    private int uploadType;
    // 4. 总里程 byte[13-14] BYTE 从点火到熄火（或熄火前的某记录时间点）的本次行程总里程（重新插拔后清零）,单位： 0.1km
    @Jt808Field(index = 13,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double totalMileage;
    // 5. 累计行驶里程 byte[15-18] BYTE  此车辆插上终端至今的累计行驶里程（重新插拔后清零），单位：0.1km
    @Jt808Field(index = 15,length = 4)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double accumMileage;


    // 6. 总耗油量 byte[19-20] BYTE  从点火到熄火（或熄火前的某记录时间点）的本次行程总耗油量（重新插拔后清零），单位： 0.1L
    @Jt808Field(index = 19,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double totalFuelConsump;
    // 7. 累计耗油量 byte[21-24] BYTE 此车辆插上终端至今的累计耗油量（重新插拔后清零）,单位：0.1L
    @Jt808Field(index = 21,length = 4)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double cumuFuelConsump;
    // 8. 平均油耗 byte[25-24] BYTE 本次行程平均油耗,单位：0.1L/100km
    @Jt808Field(index = 25,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double aveFuelConsump;
    // 9. 超速时长 byte[27-30] BYTE 本次行程超速时长：单位：秒
    @Jt808Field(index = 27,length = 4)
    private int overspeedHours;
    // 10. 发动机高转速次数 byte[31-32] BYTE  本次行程发动机高转速次数，高转速阀值可配置
    @Jt808Field(index = 31,length = 2)
    private int numHighSpeedEngine;


    // 11. 高转速行驶时长  byte[33-36] BYTE 本次行程高转速行驶时长：单位：秒
    @Jt808Field(index = 33,length = 4)
    private int highSpeedHours;
    // 12. 超长怠速次数  byte[37-38] BYTE 本次行程超长怠速次数，超长怠速时间阀值可配置
    @Jt808Field(index = 37,length = 4)
    private int numOTIdle;
    // 13. 怠速总时长  byte[39-42] BYTE 本次行程怠速总时长（本次怠速时间），单位：秒
    @Jt808Field(index = 39,length = 4)
    private int totalIdleHours;
    // 14. 怠速总耗油量  byte[43-44] BYTE 本次行程怠速总耗油量:单位0.1L
    @Jt808Field(index = 43,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double totalIdlelFuelConsump;
    // 15. 疲劳驾驶总时长  byte[45-48] BYTE 本次行程疲劳驾驶总时长：单位秒
    @Jt808Field(index = 45,length = 4)
    private int totalFatigueDriving;

    // 16. 平均车速  byte[49-50] BYTE 本次行程平均车速,单位：0.1km/h
    @Jt808Field(index = 49,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double aveSpeed;
    // 17. 最大车速  byte[51-52] BYTE 本次行程平均车速,单位：0.1km/h
    @Jt808Field(index = 51,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double maxSpeed;
    // 18. 最高转速  byte[53-54] BYTE 本次行程最高转速，单位：RPM
    @Jt808Field(index = 53,length = 2)
    private int maxSpeedRotation;
    // 19. 发动机最高水温  byte[55-56] BYTE 本次行程发动机最高水温，单位：℃，有符号整数
    @Jt808Field(index = 55,length = 2)
    private int maxTemperEngine;

    public TravelReport(DataTransmission message) {
        super(message);
        this.messageType = message.getMessageType();
    }
}
