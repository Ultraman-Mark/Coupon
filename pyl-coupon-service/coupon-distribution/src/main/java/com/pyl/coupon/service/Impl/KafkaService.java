package com.pyl.coupon.service.Impl;

import com.alibaba.fastjson.JSON;
import com.pyl.coupon.constant.Constant;
import com.pyl.coupon.constant.CouponStatus;
import com.pyl.coupon.dao.CouponDao;
import com.pyl.coupon.entity.Coupon;
import com.pyl.coupon.service.IKafkaService;
import com.pyl.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * kafka相关的服务接口实现
 * 核心思想：是将Cache中的Coupon的状态变化同步到DB中
 * Created by PYL
 */
@Slf4j
@Component
public class KafkaService implements IKafkaService {
    /**CouponDao接口*/
    private final CouponDao couponDao;

    @Autowired
    public KafkaService(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * 消费优惠券 Kafka 消息
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC},groupId = "pyl-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if(kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );
            log.info("Receive CouponKafkaMessage: {}", message.toString());

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo,status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo,status);
                    break;
            }
        }
    }

    /**
     * 处理已使用的用户优惠券
     * */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage,CouponStatus status){
        //TODO 给用户发送短信
        processCouponsByStatus(kafkaMessage,status);
    }

    /**
     * 处理已过期的用户优惠券
     * */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage,CouponStatus status){
        //TODO 给用户发送推送
        processCouponsByStatus(kafkaMessage,status);
    }

    /**
     * 根据状态处理优惠券信息
     * */
    private void processCouponsByStatus(CouponKafkaMessage message,CouponStatus status){
        List<Coupon> coupons = couponDao.findAllById(
                message.getIds()
        );
        if (CollectionUtils.isEmpty(coupons)
                || coupons.size()!=message.getIds().size()){
            log.error("Can Not Find Right Coupon Info:{}",
                    JSON.toJSONString(message));
            //TODO 发生邮件
            return;
        }
        coupons.forEach(c-> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}",
                couponDao.saveAll(coupons).size());
    }
}
