package cn.sanleny.jt808.server.framework.constants;

/**
 * jt808 消息ID 类型
 * @Author: LG
 * @Date: 2019/11/2
 * @Version: 1.0
 **/
public enum Jt808MessageType {

    //终端通用应答  上    行
    RESPONSE_COMMON_UP(0x0001)
    //平台通用应答  下    行
    ,RESPONSE_COMMON_DOWN(0x8001)

    //链路心跳
    ,LINK_HEARTBEAT(0x0002)

    //终端注册
    ,REGISTER_UP(0x0100)
    ,REGISTER_DOWN(0x8100)

    //终端注销
    ,LOGOUT(0x0003)

    //终端鉴权
    ,AUTHENTICATION(0x0102)

    // 位置信息汇报
    ,LOCATION_INFO_UP(0x0200)

    //终端请求时间
    ,REQUEST_TIME(0x0F00)
    ,REQUEST_TIME_DOWN(0x8F00)

    // 数据上行透传
    ,DATA_TRANSMISSION_UP(0x0900)
    ,DATA_TRANSMISSION_DOWN(0x8900)
    //OBD行车数据
    ,OBD_DRIVING_DATA(0xF0)
    //OBD行程报告数据
    ,OBD_TRAVEL_REPORT_DATA(0xF1)
    //OBD故障码
    ,OBD_FAULT_CODE(0xF2)


    ;


    private int value;

    Jt808MessageType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Jt808MessageType valueOf(int type) {
        for (Jt808MessageType t : values()) {
            if (t.value == type) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown message type: " + type);
    }

}
