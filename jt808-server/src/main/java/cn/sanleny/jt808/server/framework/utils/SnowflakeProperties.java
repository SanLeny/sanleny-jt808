package cn.sanleny.jt808.server.framework.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: LG
 * @Date: 2019/11/2
 * @Version: 1.0
 **/
@Data
@Configuration
@ConfigurationProperties("snowflake")
public class SnowflakeProperties {

    private long workerId;
    private long datacenterId;

}
