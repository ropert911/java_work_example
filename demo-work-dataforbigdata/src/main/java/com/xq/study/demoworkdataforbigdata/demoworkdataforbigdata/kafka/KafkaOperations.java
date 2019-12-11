package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.kafka;

import org.apache.kafka.common.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @author sk-litao
 * @date 2019/6/6
 */
@Component
public class KafkaOperations {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaOperations.class);

    @Autowired
    private KafkaTemplate<Bytes, Bytes> kafkaTemplate;


    public void sendData(String topic, byte[] bytes) {
        ListenableFuture<SendResult<Bytes, Bytes>> future = kafkaTemplate.send(topic, Bytes.wrap(bytes));
        future.addCallback(new ListenableFutureCallback<SendResult<Bytes, Bytes>>() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("topic:{" + topic + "}, msg:{" + Bytes.wrap(bytes).toString() + "} Fail to send message", ex);
            }

            @Override
            public void onSuccess(SendResult<Bytes, Bytes> result) {
                LOGGER.debug("Send data successfully. topic:{}, partitions:{}, offset:{}, msg:{}",
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), Bytes.wrap(bytes).toString());
            }
        });
    }

    public void sendData(String topic,byte[] key, byte[] value) {
        ListenableFuture<SendResult<Bytes, Bytes>> future = kafkaTemplate.send(topic,Bytes.wrap(key), Bytes.wrap(value));
        future.addCallback(new ListenableFutureCallback<SendResult<Bytes, Bytes>>() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("topic:{" + topic + "}, msg:{" + Bytes.wrap(value).toString() + "} Fail to send message", ex);
            }

            @Override
            public void onSuccess(SendResult<Bytes, Bytes> result) {
                LOGGER.debug("Send data successfully. topic:{}, partitions:{}, offset:{}, msg:{}",
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), Bytes.wrap(value).toString());
            }
        });
    }
}
