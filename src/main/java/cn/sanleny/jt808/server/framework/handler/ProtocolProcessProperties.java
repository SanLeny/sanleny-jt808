package cn.sanleny.jt808.server.framework.handler;

import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: LG
 * @Date: 2019/11/4
 * @Version: 1.0
 **/
@Data
@Configuration
@ConfigurationProperties("sanleny.jt808.protocol")
public class ProtocolProcessProperties {

    private Map<Jt808MessageType,String> process = new HashMap<>();

}
