package com.xq.work.examples;

import com.alibaba.fastjson.JSON;
import com.xq.work.examples.dto.EventInfo;
import com.xq.work.examples.kafka.KafkaOperations;
import com.xq.work.examples.kafka.OrigeKafkaOperations;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * @author sk-qianxiao
 * @date 2019/12/10
 */
@Service
public class ServerSendEvent {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootServers;
    @Value("${iot.kafka.producer.topic.origin.data}")
    private String originData;

    @Autowired
    private KafkaOperations kafkaOperations;
    @Autowired
    private KafkaTemplate<Bytes, Bytes> kafkaTemplate;

    public void startServer() {
        LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("UTC"));
        EventInfo eventInfo = EventInfo.builder()
                //温度
//                .eventId(700001)
                //湿度
//                .eventId(700002)
                //水位
//                .eventId(700003)
                //水压
                .eventId(700004)
                .deviceMac("12456789")
                .eventLastTime(nowTime.toEpochSecond(ZoneOffset.UTC))
                .eventFirstTime(nowTime.toEpochSecond(ZoneOffset.UTC))
                .eventFreq(333)
                //设备类型
                .sensorType("sensorType_xq")
                //事件描述
                .description("description_xq")
                //可能原因
                .probReason("probReason_xq")
                //确认原因
                .specReason("specReason_xq")
                .build();
        //发达方式一
        kafkaOperations.sendData(originData, (eventInfo.getDeviceMac() + "_" + eventInfo.getEventId()).getBytes(), JSON.toJSONString(eventInfo).getBytes());
        //发送方式二
        kafkaTemplate.send(originData, Bytes.wrap((eventInfo.getDeviceMac() + "_" + eventInfo.getEventId()).getBytes()), Bytes.wrap(JSON.toJSONString(eventInfo).getBytes()));
        kafkaTemplate.flush();
        //发送方式三
        OrigeKafkaOperations.send(bootServers, originData, Bytes.wrap((eventInfo.getDeviceMac() + "_" + eventInfo.getEventId()).getBytes()), Bytes.wrap(JSON.toJSONString(eventInfo).getBytes()));

    }
}
