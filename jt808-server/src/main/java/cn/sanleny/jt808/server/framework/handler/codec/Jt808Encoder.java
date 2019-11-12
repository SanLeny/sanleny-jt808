package cn.sanleny.jt808.server.framework.handler.codec;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: LG
 * @Date: 2019/11/4
 * @Version: 1.0
 **/
@Slf4j
@ChannelHandler.Sharable
public class Jt808Encoder extends MessageToMessageEncoder<Jt808Message> {

    public static final Jt808Encoder INSTANCE = new Jt808Encoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, Jt808Message msg, List out) throws Exception {
        out.add(doEncode(ctx, msg));
    }

    private ByteBuf doEncode(ChannelHandlerContext ctx, Jt808Message message) {
        log.debug("<<< 响应 message:{}", message);
        switch (message.getHeader().getMessageType()){
            case REQUEST_TIME_DOWN:
            case DATA_TRANSMISSION_UP:
                return encodeServerTimeMesssage(ctx, message);
            case REGISTER_UP:
                return encodeServerRegisterMessage(ctx,message);
            default:
                return encodeServerCommontMessage(ctx, message);
        }
    }

    /**
     * 平台通用应答  下行  0x8001
     * @param ctx
     * @param message
     * @return
     */
    private ByteBuf encodeServerCommontMessage(ChannelHandlerContext ctx, Jt808Message message) {
        //消息体
        byte[] msgBody = ArrayUtil.addAll(Convert.shortToBytes((short) message.getHeader().getFlowId()) // 应答流水号
                , Convert.shortToBytes((short) message.getHeader().getMessageType().value()) // 应答ID,对应的终端消息的ID
                , new byte[]{message.getReplyCode()}
        );
        // 消息头
        int msgBodyProps = Jt808Utils.generateMsgBodyProps(msgBody.length, Jt808Constants.ENCTYPTION_TYPE, Boolean.FALSE, 0);

        byte[] msgHeader = Jt808Utils.generateMsgHeader(message.getHeader().getTerminalPhone(),
                Jt808MessageType.RESPONSE_COMMON_DOWN.value(), msgBody, msgBodyProps, Jt808Utils.getFlowId(ctx.channel()));
        return getByteBuf(ctx, msgHeader, msgBody);
    }

    /**
     * 平台通用响应时间包 下行
     * @param ctx
     * @param message
     * @return
     */
    private ByteBuf encodeServerTimeMesssage(ChannelHandlerContext ctx, Jt808Message message) {
        String msgBody = DateUtil.format(message.getReplayTime(),"yyMMddHHmmss");
        int msgBodyByteSize = 6;
        int msgBodyProps = Jt808Utils.generateMsgBodyProps(msgBodyByteSize, Jt808Constants.ENCTYPTION_TYPE, Boolean.FALSE, 0);
        byte[] msgHeaders = Jt808Utils.generateMsgHeader(message.getHeader().getTerminalPhone(),
                message.getReplayType().value(), null, msgBodyProps, Jt808Utils.getFlowId(ctx.channel()));
        byte[] msgBodys = BCD.strToBcd(msgBody);
        return getByteBuf(ctx, msgHeaders, msgBodys);
    }

    /**
     * 终端注册应答包 下行  0x8100
     * @param ctx
     * @param message
     * @return
     */
    private ByteBuf encodeServerRegisterMessage(ChannelHandlerContext ctx, Jt808Message message) {
        // 消息体
        byte[] msgBody = ArrayUtil.addAll(Convert.shortToBytes((short) message.getHeader().getFlowId()) // 应答流水号
                , new byte[]{message.getReplyCode()}
        );
        if( message.getReplyCode() == Jt808Constants.RESP_SUCCESS){
            msgBody = ArrayUtil.addAll(msgBody, StrUtil.bytes(message.getReplayToken(), CharsetUtil.CHARSET_GBK));
        }
        // 消息头
        int msgBodyProps = Jt808Utils.generateMsgBodyProps(msgBody.length, Jt808Constants.ENCTYPTION_TYPE, Boolean.FALSE, 0);
        byte[] msgHeader = Jt808Utils.generateMsgHeader(message.getHeader().getTerminalPhone(),
                Jt808MessageType.REGISTER_DOWN.value(), msgBody, msgBodyProps, Jt808Utils.getFlowId(ctx.channel()));

        return getByteBuf(ctx, msgHeader, msgBody);
    }

    /**
     * 拼接并转义消息
     * @param ctx
     * @param msgHeaders
     * @param msgBodys
     * @return
     */
    private ByteBuf getByteBuf(ChannelHandlerContext ctx, byte[] msgHeaders, byte[] msgBodys) {
        byte[] headerAndBody = ArrayUtil.addAll(msgHeaders, msgBodys);
        //转义
        byte[] descape = Jt808Utils.descape(headerAndBody);
        // 校验码
        byte checkSum = Jt808Utils.getCheckSum(Unpooled.wrappedBuffer(headerAndBody));
        // 连接
        byte[] resBytes = ArrayUtil.addAll(
                new byte[]{Jt808Constants.PKG_DELIMITER}
                , descape
                , new byte[]{checkSum}
                , new byte[]{Jt808Constants.PKG_DELIMITER} // 0x7e
        );
        log.debug("<<< 响应终端：{}", HexUtil.encodeHexStr(resBytes,Boolean.FALSE));
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(resBytes);
        return buffer;
    }
}