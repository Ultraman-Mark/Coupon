package com.pyl.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 优惠券Kafka消息对象定义
 * Created by PYL
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {
    /** 优惠券状态 */
    private Integer status;
    /** 优惠券主键*/
    private List<Integer> ids;
}
