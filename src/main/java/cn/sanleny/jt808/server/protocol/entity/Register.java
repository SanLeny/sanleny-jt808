package cn.sanleny.jt808.server.protocol.entity;

import cn.sanleny.jt808.server.framework.annotation.Jt808Field;
import cn.sanleny.jt808.server.framework.annotation.Jt808Resolver;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import lombok.Data;

/**
 * 终端注册
 * @Author: LG
 * @Date: 2019/11/7
 * @Version: 1.0
 **/
@Data
@Jt808Resolver
public class Register extends Jt808Message {

    // 省域ID(WORD),标示终端安装车辆所在的省域，0 保留，由平台取默认值，省域ID采用GB/T2260中规定的行政区划代码6位中前两位
    // 0保留，由平台取默认值（0x3434）byte[0-1]
    @Jt808Field(index = 0,length = 2)
    private int provinceId;

    // 市县域ID(WORD) 标示终端安装车辆所在的市域和县域，0 保留，由平台取默认值,市县域ID采用GB/T2260中规定的行 政区划代码6位中后四位
    // 0保留，由平台取默认值（0x3033）byte[2-3]
    @Jt808Field(index = 2,length = 2)
    private int cityId;

    // 制造商ID(BYTE[5]) 5 个字节，终端制造商编码 （默认值 44030）byte[4-8]
    @Jt808Field(index = 4,length = 5)
    private String manufacturerId;

    // 终端型号(BYTE[20]) 20个字节，此终端型号由制造商自行定义，位数不足时，后补“0X00” (即补0) （默认值 E102AB） byte[9-29]
    @Jt808Field(index = 9,length = 20)
    private String terminalType;

    // 终端ID(BYTE[7]) 7 个字节，由大写字母和数字组成，此终端ID 由制造商自行定义，位数不足时，后补“0X00” (即补0) byte[29-35]
    @Jt808Field(index = 29,length = 7)
    private String terminalId;
    /**
     *
     * byte[36]
     * 车牌颜色(BYTE) 车牌颜色，按照 JT/T415-2006 的 5.4.12 未上牌时，取值为0<br> 默认值 1
     * 0===未上车牌<br>
     * 1===蓝色<br>
     * 2===黄色<br>
     * 3===黑色<br>
     * 4===白色<br>
     * 9===其他
     */
    @Jt808Field(index = 36,length = 1)
    private int licensePlateColor;

    // 车辆标识(STRING) 车牌颜色为0时，表示车辆VIN；否则，表示公安交通管理部门颁发的机动车号牌。 （默认值：粤B12345） byte[37-x]
    @Jt808Field(index = 37,length = -1)
    private String licensePlate;

    public Register(Jt808Message message) {
        this.header = message.getHeader();
        this.checkSum = message.getCheckSum();
        this.msgBodyBytes = message.getMsgBodyBytes();
    }
}
