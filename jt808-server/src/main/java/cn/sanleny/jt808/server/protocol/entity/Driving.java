package cn.sanleny.jt808.server.protocol.entity;

import cn.hutool.core.util.NumberUtil;
import cn.sanleny.jt808.server.framework.annotation.Jt808Field;
import cn.sanleny.jt808.server.framework.annotation.Jt808Math;
import cn.sanleny.jt808.server.framework.annotation.Jt808Resolver;
import lombok.Data;

import java.util.Date;

/**
 * OBD行车数据
 * @Author: LG
 * @Date: 2019/11/5
 * @Version: 1.0
 **/
@Data
@Jt808Resolver
public class Driving extends DataTransmission {

    //1. 上传时间 BCD[0-5]
    @Jt808Field(index = 0,length = 6)
    private Date time;		//时间
    //2. 上传类型 byte[6] BYTE
    @Jt808Field(index = 6,length = 1)
    private int uploadType;		// 上传类型：0-主动上传；1-查询上传
    // 3. OBD报警标志位 byte[7-10] BYTE
    @Jt808Field(index = 7,length = 4)
    private int alarmFlag;		// OBD报警标志位
    // 4. OBD状态标志位 byte[11-14] BYTE
    @Jt808Field(index = 11,length = 4)
    private int statusFlag;		// OBD状态标志位
    // 5. 瞬时速度 byte[15-16] BYTE
    @Jt808Field(index = 15,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double instantVelocity;		// 瞬时速度


    // 6. 发动机转速 byte[17-18] BYTE
    @Jt808Field(index = 17,length = 2)
    private int engineSpeed;		// 发动机转速
    // 7. 电瓶电压V byte[19-20] BYTE
    @Jt808Field(index = 19,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double batteryVoltage;		// 电瓶电压V
    // 8. 车辆总里程 byte[21-24] BYTE
    @Jt808Field(index = 21,length = 4)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double vehicleMileage;		// 车辆总里程
    // 9. 插上OBD后总里程 byte[25-28] BYTE
    @Jt808Field(index = 25,length = 4)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double obdMileage;		// 插上OBD后总里程
    // 10. 怠速瞬时油耗 byte[29-30] BYTE
    @Jt808Field(index = 29,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double idleInstantFuelConsump;		// 怠速瞬时油耗

    // 11. 行驶瞬时油耗  byte[31-32] BYTE
    @Jt808Field(index = 31,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double travelInstantFuelConsump;		// 行驶瞬时油耗
    // 12. 发动机负荷  byte[33] BYTE
    @Jt808Field(index = 33,length = 1)
    private double engineLoad;		// 发动机负荷
    // 13. 冷却液温度  byte[34-35] BYTE
    @Jt808Field(index = 34,length = 2)
    private int coolantTemper;		// 冷却液温度
    // 14. 燃油压力  byte[36-37] BYTE
    @Jt808Field(index = 36,length = 2)
    private int fuelPress;		// 燃油压力
    // 15. 进气歧管绝对压力  byte[38-39] BYTE
    @Jt808Field(index = 38,length = 2)
    private int absPressIntakeManifold;		// 进气歧管绝对压力

    // 16. 进气温度  byte[40-41] BYTE
    @Jt808Field(index = 40,length = 2)
    private int intakeTemper;		// 进气温度
    // 17. 进气流量  byte[42-43] BYTE
    @Jt808Field(index = 42,length = 2)
    private double inletAirFlow;		// 进气流量
    // 18. 节气门绝对位置  byte[44] BYTE
    @Jt808Field(index = 44,length = 1)
    private double absThrottlePosit;		// 节气门绝对位置
    // 19. 行车状态  byte[45] BYTE
    @Jt808Field(index = 45,length = 1)
    private int drivingState;		// 行车状态
    // 20. 瞬时油耗  byte[46-47] BYTE
    @Jt808Field(index = 46,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double instantFuelConsump;		// 瞬时油耗

    // 21. 油门踏板相对位置  byte[48-49] BYTE 单位：0.01%
    @Jt808Field(index = 48,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div",number = 100)
    private double relaPositThrottlePedal;		// 油门踏板相对位置
    // 22. 油门踏板状态  byte[50] BYTE
    @Jt808Field(index = 50,length = 1)
    private int accelPedalStatus;		// 油门踏板状态
    // 23. 剩余油量（%）  byte[51-52] BYTE 单位：0.1%
    @Jt808Field(index = 51,length = 2)
    @Jt808Math(aClass = NumberUtil.class, method = "div")
    private double residualOil;		// 剩余油量（%）
    // 24. ACC状态  byte[53] BYTE
    @Jt808Field(index = 53,length = 1)
    private int accStatus;		// ACC状态

//    private int flowId;		// 流水号
//    private Date createDate;		// 创建时间

    public Driving(DataTransmission message) {
        super(message);
        this.messageType = message.getMessageType();
    }
}
