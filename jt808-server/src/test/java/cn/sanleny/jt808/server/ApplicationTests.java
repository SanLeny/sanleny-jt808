package cn.sanleny.jt808.server;

import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import cn.sanleny.jt808.server.framework.utils.SnowflakeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
        System.out.println("plate:" + stringRedisTemplate.opsForValue().get("hzdl_vehicle_502820408000"));
    }

    @Test
    public void testSnowflakeUtils(){
        System.out.println(SnowflakeUtils.nextId());
        Integer msgId = 512;
        Jt808MessageType type = Jt808MessageType.valueOf(msgId);
        System.out.println(type);
    }

}
