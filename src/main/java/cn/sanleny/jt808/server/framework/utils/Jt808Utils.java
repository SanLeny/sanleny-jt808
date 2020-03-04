package cn.sanleny.jt808.server.framework.utils;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * jt808 通用工具
 * @Author: LG
 * @Date: 2019/11/1
 * @Version: 1.0
 **/
@Slf4j
public class Jt808Utils {

    /**
     * 校验码指从消息头开始，同后一字节异或，直到效验码前一个字节，占用一个字节
     * @param bs
     * @param start
     * @param end
     * @return
     */
    public static int getCheckSum(byte[] bs, int start, int end) {
        int cs = 0;
        for (int i = start; i < end; i++) {
            cs ^= bs[i];
        }
        return cs;
    }

    /**
     * 转义
     * 0x7d 0x02←→ 0x7E
     * 0x7d 0x01 ←→ 0x7d
     * @param content
     * @return
     */
    public static ByteBuf escape(byte[] content){
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < content.length; i++) {
            //这里如果最后一位是0x7d会导致index溢出，说明原始报文转义有误
            if (content[i] == 0x7d && content[i + 1] == 0x01) {
                buffer.writeByte(0x7d);
                i++;
            } else if (content[i] == 0x7d && content[i + 1] == 0x02) {
                buffer.writeByte(0x7e);
                i++;
            } else {
                buffer.writeByte(content[i]);
            }
        }
        return buffer;
    }

    /**
     * 转义
     * 0x7E ←→ 0x7d 0x02
     * 0x7d ←→ 0x7d 0x01
     * @param content
     * @return
     */
    public static byte[] descape(byte[] content){
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < content.length; i++) {
            if (content[i] == 0x7e) {
                buffer.writeByte(0x7d);
                buffer.writeByte(0x02);
            } else if(content[i] == 0x7d) {
                buffer.writeByte(0x7d);
                buffer.writeByte(0x01);
            } else {
                buffer.writeByte(content[i]);
            }
        }
        byte[] res = new byte[buffer.writerIndex()];
        buffer.readBytes(res);
        return res;
    }

    public static int parseIntFromBytes(byte[] data, int startIndex, int length) {
        return parseIntFromBytes(data, startIndex, length, 0);
    }

    public static String parseBcdStringFromBytes(byte[] data, int startIndex, int length){
        byte[] tmp = new byte[length];
        System.arraycopy(data, startIndex, tmp, 0, length);
        return BCD.bcdToStr(tmp);
    }

    protected static int parseIntFromBytes(byte[] data, int startIndex, int length, int defaultVal) {
        try{
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            int len = length > 4 ? 4 : length;
            byte[] tmp = new byte[4];
            System.arraycopy(data, startIndex, tmp, 4-len, len);
            return Convert.bytesToInt(tmp);
        } catch (Exception e){
            log.error("转换异常：{}",e.toString(),e);
            return defaultVal;
        }
    }

    public static String parseStringFromBytes(byte[] data, int startIndex, int lenth) {
        return parseStringFromBytes(data, startIndex, lenth, null);
    }

    protected static String parseStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
        try {
            byte[] tmp = new byte[lenth];
            System.arraycopy(data, startIndex, tmp, 0, lenth);
            return StrUtil.str(tmp, CharsetUtil.CHARSET_GBK);
        } catch (Exception e) {
            log.error("解析字符串出错:{}", e.toString(),e);
            return defaultVal;
        }
    }

    public static int generateMsgBodyProps(int msgLen, int enctyptionType, boolean isSubPackage, int reversed_14_15) {
        // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
        // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
        // [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
        // [14-15] 1100,0000,0000,0000(C000)(保留位)
        if (msgLen >= 1024)
            log.warn("The max value of msgLen is 1023, but {} .", msgLen);
        int subPkg = isSubPackage ? 1 : 0;
        int ret = (msgLen & 0x3FF) | ((enctyptionType << 10) & 0x1C00) | ((subPkg << 13) & 0x2000) | ((reversed_14_15 << 14) & 0xC000);
        return ret & 0xffff;
    }

    public static byte[] generateMsgHeader(String terminalPhone, int msgType, byte[] msgBody, int msgBodyProps, int flowId) {
        byte[] bytes = ArrayUtil.addAll(
                // 1. 消息ID word(16)
                Convert.shortToBytes((short) msgType)
                // 2. 消息体属性 word(16)
                , Convert.shortToBytes((short) msgBodyProps)
                // 3. 终端手机号 bcd[6]
                , BCD.strToBcd(terminalPhone)
                // 4. 消息流水号 word(16),按发送顺序从 0 开始循环累加
                , Convert.shortToBytes((short) flowId)
                // 消息包封装项 此处不予考虑
        );
        return bytes;
    }

    public static Date generateDate(byte[] data, int start, int length){
        String dateStr = parseBcdStringFromBytes(data,start,length);
        return  DateUtil.parse(dateStr, "yyMMddHHmmss");
    }

    public static int getFlowId(Channel channel,String flowType){
        AtomicInteger flow = (AtomicInteger) channel.attr(AttributeKey.valueOf(flowType)).get();
        if(flow==null){
            flow = new AtomicInteger();
        }
        int flowId = flow.get();
        flow.incrementAndGet();
        channel.attr(AttributeKey.valueOf(flowType)).set(flow);
        return flowId;
    }
}
