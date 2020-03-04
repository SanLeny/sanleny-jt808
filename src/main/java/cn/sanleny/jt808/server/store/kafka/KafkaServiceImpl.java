package cn.sanleny.jt808.server.store.kafka;


import cn.sanleny.jt808.server.common.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: sanleny
 * @Date: 2019-12-10
 * @Version: 1.0
 */
@Service
public class KafkaServiceImpl implements KafkaService {

//    @Autowired
//    private KafkaTemplate kafkaTemplate;

    @Override
    public void send(String topic, Object msg) {
        System.out.println(">>> kafkaf send <<< "+msg);
//        kafkaTemplate.send(topic,msg);
    }
}
