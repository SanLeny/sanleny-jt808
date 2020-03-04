package cn.sanleny.jt808.server.framework.utils;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author: LG
 * @Date: 2019-10-20
 * @Version: 1.0
 */
@Configuration
public class SnowflakeUtils {

    @Autowired
    private SnowflakeProperties snowflakeProperties;

    private static SnowflakeProperties staticSnowflakeProperties;

    @PostConstruct
    public void init(){
        staticSnowflakeProperties = this.snowflakeProperties;
    }

    public static long nextId(){
        return IdUtil.getSnowflake(staticSnowflakeProperties.getWorkerId(), staticSnowflakeProperties.getDatacenterId()).nextId();
    }

}
