package cn.sanleny.jt808.server.framework.handler.codec;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.framework.utils.Jt808Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.sanleny.jt808.server.framework.constants.FlowType.INITIATIVE;
import static cn.sanleny.jt808.server.framework.constants.FlowType.PASSIVE;

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
        out.add(doEncode(ctx.channel(), msg));
    }

    public ByteBuf doEncode(Channel channel, Jt808Message message) {
        switch (message.getReplayType()){
            case REQUEST_TIME_DOWN:
            case DATA_TRANSMISSION_DOWN:
                return encodeServerTimeMesssage(channel, message);
            case REGISTER_DOWN:
                return encodeServerRegisterMessage(channel,message);
            case CLIENT_LOCATION_INFO_DOWN:
            case CLIENT_PROPERTY_DOWN:
                return encodeServerCommontSearchClientMessage(channel, message);
            default:
                return encodeServerCommontMessage(channel, message);
        }
    }

    /**
     * 平台通用应答  下行  0x8001
     * @param channel
     * @param message
     * @return
     */
    private ByteBuf encodeServerCommontMessage(Channel channel, Jt808Message message) {
        log.debug("<<< 响应通用 message:{}", message);
        //消息体
        byte[] msgBody = ArrayUtil.addAll(Convert.shortToBytes((short) message.getHeader().getFlowId()) // 应答流水号
                , Convert.shortToBytes((short) message.getHeader().getMessageType().value()) // 应答ID,对应的终端消息的ID
                , new byte[]{message.getReplyCode()}
        );
        // 消息头
        int msgBodyProps = Jt808Utils.generateMsgBodyProps(msgBody.length, Jt808Constants.ENCTYPTION_TYPE, Boolean.FALSE, 0);

        byte[] msgHeader = Jt808Utils.generateMsgHeader(message.getHeader().getTerminalPhone(),
                Jt808MessageType.RESPONSE_COMMON_DOWN.value(), msgBody, msgBodyProps, Jt808Utils.getFlowId(channel,PASSIVE.value()));
        return getByteBuf(channel, msgHeader, msgBody);
    }


    /**
     * 通用查询终端
     * @param channel
     * @param message
     * @return
     */
    private ByteBuf encodeServerCommontSearchClientMessage(Channel channel, Jt808Message message) {
        log.debug("<<< 响应通用 message:{}", message);
        //消息体
        byte[] msgBody = new byte[0];
        // 消息头
        int msgBodyProps = Jt808Utils.generateMsgBodyProps(msgBody.length, Jt808Constants.ENCTYPTION_TYPE, Boolean.FALSE, 0);

        byte[] msgHeader = Jt808Utils.generateMsgHeader(message.getHeader().getTerminalPhone(),
                message.getReplayType().value(), msgBody, msgBodyProps,Jt808Utils.getFlowId(channel,INITIATIVE.value()));
        return getByteBuf(channel, msgHeader, msgBody);
    }

    /**
     * 平台通用响应时间包 下行
     * @param channel
     * @param message
     * @return
     */
    private ByteBuf encodeServerTimeMesssage(Channel channel, Jt808Message message) {
        log.debug("<<< 响应时间包 message:{}", message);
        String msgBody = DateUtil.format(message.getReplayTime(),"yyMMddHHmmss");
        int msgBodyByteSize = 6;
        int msgBodyProps = Jt808Utils.generateMsgBodyProps(msgBodyByteSize, Jt808Constants.ENCTYPTION_TYPE, Boolean.FALSE, 0);
        byte[] msgHeaders = Jt808Utils.generateMsgHeader(message.getHeader().getTerminalPhone(),
                message.getReplayType().value(), null, msgBodyProps, Jt808Utils.getFlowId(channel,PASSIVE.value()));
        byte[] msgBodys = BCD.strToBcd(msgBody);
        return getByteBuf(channel, msgHeaders, msgBodys);
    }

    /**
     * 终端注册应答包 下行  0x8100
     * @param channel
     * @param message
     * @return
     */
    private ByteBuf encodeServerRegisterMessage(Channel channel, Jt808Message message) {
        log.debug("<<< 响应注册应答包 message:{},replayToken:{},checkSum:{}", message,message.getReplayToken(),
                message.getCheckSum());
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
                Jt808MessageType.REGISTER_DOWN.value(), msgBody, msgBodyProps, Jt808Utils.getFlowId(channel,PASSIVE.value()));

        return getByteBuf(channel, msgHeader, msgBody);
    }

    /**
     * 拼接并转义消息
     * @param channel
     * @param msgHeaders
     * @param msgBodys
     * @return
     */
    private ByteBuf getByteBuf(Channel channel, byte[] msgHeaders, byte[] msgBodys) {
        byte[] headerAndBody = ArrayUtil.addAll(msgHeaders, msgBodys);
        // 校验码
        int checkSum = Jt808Utils.getCheckSum(headerAndBody, 0, headerAndBody.length);
        //转义
        byte[] descape = Jt808Utils.descape(headerAndBody);
        // 连接
        byte[] resBytes = ArrayUtil.addAll(
                new byte[]{Jt808Constants.PKG_DELIMITER}
                , descape
                ,new byte[]{Convert.intToByte(checkSum)}
                , new byte[]{Jt808Constants.PKG_DELIMITER} // 0x7e
        );
        log.debug("<<< 响应终端：{}", HexUtil.encodeHexStr(resBytes,Boolean.FALSE));
        ByteBuf buffer = channel.alloc().buffer();
        buffer.writeBytes(resBytes);
        return buffer;
    }
}
