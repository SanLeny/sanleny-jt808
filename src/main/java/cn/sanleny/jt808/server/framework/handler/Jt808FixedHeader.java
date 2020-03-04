package cn.sanleny.jt808.server.framework.handler;

import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import lombok.Data;


//*********************消息头
//+-------------------------------------------------------------------------------------------------------+
//|  15  |  14  |  13  |  12  |  11  |  10  |  9  |  8  |  7  |  6  |  5  |  4  |  3   |  2   |  1  |  0  |
//|-------------------------------------------------------------------------------------------------------|
//|        消息包封装项       |  消息流水号 |         终端手机号(BCD[6])        | 消息体属性  |  消息 ID  |
//+-------------------------------------------------------------------------------------------------------+

//*********************消息体属性
//+---------------------------------------------------------------------+
//| 15 | 14 | 13 | 12 | 11 | 10 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
//|---------------------------------------------------------------------|
//|   保留  |分包| 数据加密方式 |	          消息体长度                |
//+---------------------------------------------------------------------+


/**
 * JT808 消息头
 * @Author: LG
 * @Date: 2019/11/1
 * @Version: 1.0
 **/
@Data
public class Jt808FixedHeader {

    // [0-1] 消息ID 2字节
    private Jt808MessageType messageType;

    // ========消息体属性
    // byte[2-3] 消息体属性 2字节
    private int msgBodyPropsField;
    // 消息体长度    msgBodyPropsField & 0x3ff                 [ 0-9 ] 0000,0011,1111,1111(3FF)
    private int msgBodyLength;
    // 数据加密方式  (msgBodyPropsField & 0x1c00) >> 10        [10-12] 0001,1100,0000,0000(1C00)(加密类型)
    private int encryptionType;
    // 是否分包,有消息包封装项  ((msgBodyPropsField & 0x2000) >> 13) == 1       [ 13 ] 0010,0000,0000,0000(2000)(是否有子包)
    private boolean hasSubPackage;
    // 保留位  ((msgBodyPropsField & 0xc000) >> 14) + ""        [14-15] 1100,0000,0000,0000(C000)(保留位)
    private String reservedBit;
    // ========消息体属性

    //[4-9] 终端手机号 6字节
    private String terminalPhone;
    //[10-11]  流水号 2字节
    private int flowId;

    // =====消息包封装项
    // [12-15] 消息包封装项
    private int packageInfoField;
    // [12,13] 消息包总数
    private long totalSubPackage;
    // [14,15] 包序号 这次发送的这个消息包是分包中的第几个消息包, 从 1 开始
    private long subPackageSeq;
    // =====消息包封装项

}
