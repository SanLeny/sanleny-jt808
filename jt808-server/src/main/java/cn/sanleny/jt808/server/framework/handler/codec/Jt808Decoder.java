package cn.sanleny.jt808.server.framework.handler.codec;

import cn.hutool.core.codec.BCD;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.exception.GlobalFallbackException;
import cn.sanleny.jt808.server.framework.handler.Jt808FixedHeader;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.handler.codec.Jt808Decoder.DecoderState;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JT808 解码
 * 转义规则：
 * 0x7E ←→ 0x7d 0x02
 * 0x7d ←→ 0x7d 0x01
 * @Author: LG
 * @Date: 2019/11/1
 * @Version: 1.0
 **/
@Slf4j
public class Jt808Decoder extends ReplayingDecoder<DecoderState> {

    /**
     * States of the decoder.
     * We start at ESCAPE_MESSAGE, followed by
     * CHECK_MESSAGE and finally READ_MESSAGE_HEADER.
     */
    enum DecoderState {
        ESCAPE_MESSAGE,
        CHECK_MESSAGE,
        READ_MESSAGE_HEADER,
        BAD_MESSAGE,
    }

    private ByteBuf buf;
    private String hex;
    private byte checkSum;
    private Jt808FixedHeader header;

    public Jt808Decoder() {
        super(DecoderState.ESCAPE_MESSAGE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()){
            case ESCAPE_MESSAGE:
                try {
                    byte[] content = new byte[in.writerIndex()];
                    in.readBytes(content);
                    buf = Jt808Utils.escape(content);
                    hex = ByteBufUtil.hexDump(buf).toUpperCase();
                    checkpoint(DecoderState.CHECK_MESSAGE);
                }catch (Exception e){
                    log.error(">>> 原始报文转义有误:" + e.toString(),e);
                    out.add(invalidMessage(new GlobalFallbackException("原始报文转义有误!")));
                    return;
                }
            case CHECK_MESSAGE:
                checkSum = buf.getByte(buf.writerIndex() - 1);//校验码
                byte calCheckSum = Jt808Utils.getCheckSum(buf);
                if(checkSum != checkSum){
                    log.error(">>> 校验码错误: checkSum:{},calCheckSum:{}",checkSum,calCheckSum);
                    out.add(invalidMessage(new GlobalFallbackException("校验码错误: checkSum:" + checkSum + ",calCheckSum:" + calCheckSum)));
                    return;
                }
                buf.writerIndex(buf.writerIndex() - 1);//排除校验码
                checkpoint(DecoderState.READ_MESSAGE_HEADER);
            case READ_MESSAGE_HEADER:
                try {
                    byte[] jt808Data = new byte[buf.readableBytes()];
                    buf.readBytes(jt808Data);
                    header = this.getMsgHeader(jt808Data);

                    checkpoint(DecoderState.ESCAPE_MESSAGE);

                    //将clientId存储到channel的map中
//                    ctx.channel().attr(AttributeKey.valueOf("clientId")).set(header.getTerminalPhone());

                    // 消息体数据
                    int messageBodyStartIndex = header.isHasSubPackage() ? 16 : 12;
                    byte[] tmp = new byte[header.getMsgBodyLength()];
                    System.arraycopy(jt808Data, messageBodyStartIndex, tmp, 0, tmp.length);

                    Jt808Message message = new Jt808Message(header,tmp,checkSum,hex);
                    header = null;
                    hex = null;
                    checkSum = 0;
                    out.add(message);
                    break;
                }catch (Exception e){
                    log.error(">>> 解析消息头出错误:" + e.toString(),e);
                    out.add(invalidMessage(new GlobalFallbackException("消息头错误!")));
                    return;
                }
            case BAD_MESSAGE:
                // Keep discarding until disconnection.
                log.error(">>> Keep discarding until disconnection.");
                in.skipBytes(actualReadableBytes());
                break;
            default:
                // Shouldn't reach here.
                throw new Error();
        }
    }

    private Jt808Message invalidMessage(Throwable cause) {
        checkpoint(DecoderState.BAD_MESSAGE);
        return new Jt808Message(header,hex,cause);
    }

    /**
     * 得到封装的 jt808 消息头信息
     * @param jt808Data
     * @return
     */
    public static Jt808FixedHeader getMsgHeader(byte[] jt808Data) {
        Jt808FixedHeader header =new Jt808FixedHeader();
        //[0-1] 字节为 消息ID
        Integer msgId = Jt808Utils.parseIntFromBytes(jt808Data,0,2);
        header.setMessageType(Jt808MessageType.valueOf(msgId));

        //[2-3] 字节为 消息体属性
        Integer msgBodyPropsField = Jt808Utils.parseIntFromBytes(jt808Data,2,2);
        header.setMsgBodyPropsField(msgBodyPropsField);
        // ---------------------------------------------------------------------
        //| 15 | 14 | 13 | 12 | 11 | 10 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
        //|---------------------------------------------------------------------|
        //|保留	    |分包| 数据加密方式 |	      消息体长度                    |
        // ---------------------------------------------------------------------
        // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
        int msgBodyLength = msgBodyPropsField & 0x3ff;
        header.setMsgBodyLength(msgBodyLength);
        // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
        int encryptionType = (msgBodyPropsField & 0x1c00) >> 10 ;
        header.setEncryptionType(encryptionType);
        // [ 13 ] 0010,0000,0000,0000(2000)(是否有子包)
        boolean hasSubPackage = ((msgBodyPropsField & 0x2000) >> 13) == 1 ;
        header.setHasSubPackage(hasSubPackage);
        // [14-15] 1100,0000,0000,0000(C000)(保留位)
        String reservedBit = ((msgBodyPropsField & 0xc000) >> 14) + "";
        header.setReservedBit(reservedBit);

        //[4-9] 字节为 终端手机号
        byte[] tmp = new byte[6];
        System.arraycopy(jt808Data, 4, tmp, 0, tmp.length);
        header.setTerminalPhone(BCD.bcdToStr(tmp));

        //[10-11] 字节为 消息流水号
        header.setFlowId(Jt808Utils.parseIntFromBytes(jt808Data,10,2));

        //[12-15] 字节为 消息包封装项
        if(hasSubPackage){//有子包信息
            // 消息包封装项字段
            int packageInfoField = Jt808Utils.parseIntFromBytes(jt808Data,12,4);
            header.setPackageInfoField(packageInfoField);
            // byte[0-1] 消息包总数
            int totalSubPackage = Jt808Utils.parseIntFromBytes(jt808Data,12,2);
            header.setTotalSubPackage(totalSubPackage);

            // byte[2-3] 包序号    从 1 开始
            int subPackageSeq = Jt808Utils.parseIntFromBytes(jt808Data,14,2);
            header.setSubPackageSeq(subPackageSeq);
        }
        return header;
    }

}
