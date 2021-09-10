package com.pyl.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka相关服务接口定义
 * Created by PYL
 */
public interface IKafkaService {
    /**
     * <h2>消费优惠券 Kafka 消息</h2>
     *
     * @param record {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
