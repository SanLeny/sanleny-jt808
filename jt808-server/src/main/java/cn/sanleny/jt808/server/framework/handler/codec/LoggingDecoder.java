package cn.sanleny.jt808.server.framework.handler.codec;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 记录日志
 **/
@Slf4j
public class LoggingDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] content = new byte[in.readableBytes()];
        in.getBytes(0,content);
        String  hexString = HexUtil.encodeHexStr(content,Boolean.FALSE);
        log.debug("ip={},hex = {}", ctx.channel().remoteAddress(), hexString);

        ByteBuf buf = Unpooled.buffer();
        while (in.isReadable()) {
            buf.writeByte(in.readByte());
        }
        out.add(buf);
    }

}
