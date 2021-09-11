package com.pyl.coupon.service;

import com.pyl.coupon.entity.Coupon;
import com.pyl.coupon.exception.CouponException;

import java.util.List;

/**
 * Redis相关操作服务接口定义
 * 1.用户的三个优惠券cache相关操作
 * 2.优惠券模板生成的优惠券码Cache操作
 * Created by PYL
 */
public interface IRedisService {
    /**
     * 根据优惠券id+状态 找到缓存的优惠券列表数据
     * @param userId 用户 id
     * @param status 优惠券状态 {@link com.pyl.coupon.constant.CouponStatus}
     * @return {@link Coupon}s，注意，可能返回 null，代表从来没有记录
     * */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    /**
     * 保存空的优惠券列表到缓存中
     * @param userId 用户 id
     * @param status 优惠券列表
     * */
    void saveEmptyCouponListToCache(Long userId,List<Integer> status);

    /**
     * 尝试从Cache中获取一个优惠券吗
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     * */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * 将优惠券保存到 Cache 中
     * @param userId 用户 id
     * @param coupons {@link Coupon}s
     * @param status 优惠券状态
     * @return 保存成功的个数
     * */
    Integer addCouponToCache(Long userId,List<Coupon> coupons, Integer status) throws ClassCastException, CouponException;
}
