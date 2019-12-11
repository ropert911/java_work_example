package com.xq.work.examples.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Bytes;

import java.util.Properties;

/**
 * @author sk-qianxiao
 * @date 2019/12/10
 */
public class OrigeKafkaOperations {
    public static void send(String bootstrapServers, String topic, Bytes key, Bytes values) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 1024 * 1024);
        props.put("key.serializer", "org.apache.kafka.common.serialization.BytesSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.BytesSerializer");
        Producer<Bytes, Bytes> producer = new KafkaProducer<>(props);
        producer.send(new ProducerRecord<>(topic, key, values));

        producer.close();
    }
}
