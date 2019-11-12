package cn.sanleny.jt808.server.framework.handler;

import cn.sanleny.jt808.server.Application;
import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: LG
 * @Date: 2019/11/4
 * @Version: 1.0
 **/
@Component
@Slf4j
public class ProtocolProcessFactory {

    @Autowired
    private ProtocolProcessProperties protocolProcessProperties;

    public ProtocolProcess getInstance(Jt808MessageType type){
        try {
            ProtocolProcess bean = (ProtocolProcess) Class.forName(protocolProcessProperties.getProcess().get(type)).getDeclaredConstructor().newInstance();
            Application.applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
            return bean;
        } catch (NullPointerException e){
            return null;
        } catch (Exception e) {
            log.error("getInstance error:{} ",e.toString(), e);
        }
        return null;
    }

}
