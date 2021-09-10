package com.pyl.coupon.dao;

import com.pyl.coupon.constant.CouponStatus;
import com.pyl.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * CouponDao接口定义
 * Created by PYL
 */
public interface CouponDao extends JpaRepository<Coupon,Integer> {
    /**
     * 根据 userId+状态 寻找优惠券记录
     * where userId = ... and status = ...
     * */
    List<Coupon> findAllUserIdAndStatus(Long userId, CouponStatus status);
}
