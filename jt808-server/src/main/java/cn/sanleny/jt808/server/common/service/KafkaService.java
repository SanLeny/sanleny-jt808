package cn.sanleny.jt808.server.common.service;

/**
 * kafka
 * @Author: sanleny
 * @Date: 2019-12-10
 * @Version: 1.0
 */
public interface KafkaService {

    void send(String topic, Object msg);

}
