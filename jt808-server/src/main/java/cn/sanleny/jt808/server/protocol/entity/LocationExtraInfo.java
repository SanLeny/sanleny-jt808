package cn.sanleny.jt808.server.protocol.entity;

import lombok.Data;

/**
 * 位置附加信息
 * @Author: LG
 * @Date: 2020-03-04
 * @Version: 1.0
 **/
@Data
public class LocationExtraInfo {

    private Integer id;             //附加信息 ID(byte)
    private Integer length;         //附加信息长度 (byte)
    private byte[] bytesValue;      //附加信息 (不定长)

}
