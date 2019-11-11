package cn.sanleny.jt808.server.protocol.process;

import cn.sanleny.jt808.server.framework.handler.Jt808Message;
import cn.sanleny.jt808.server.protocol.entity.LocationInfo;
import cn.sanleny.jt808.server.framework.constants.Jt808Constants;
import cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 【0x0200】位置信息汇报
 * @Author: LG
 * @Date: 2019/11/2
 * @Version: 1.0
 **/
@Slf4j
public class LocationInfoProcess extends AbstractProtocolProcess {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected Jt808Message resolve(Jt808Message message) {
        return new LocationInfo(message);
    }

    @Override
    protected Jt808Message process(Jt808Message message) {
        LocationInfo msg = (LocationInfo) message;
        if(msg.getReplyCode() != Jt808Constants.RESP_SUCCESS){
            return msg;
        }
//        String plate = stringRedisTemplate.opsForValue().get("vehicle:" + msg.getHeader().getTerminalPhone());
//        log.debug("车辆:{} 的位置信息:{}",plate,msg);
        log.debug("位置信息:{}",msg);
        return msg;
    }
}
