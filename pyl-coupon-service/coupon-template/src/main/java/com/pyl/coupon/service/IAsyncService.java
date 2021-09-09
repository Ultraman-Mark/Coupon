package com.pyl.coupon.service;

import com.pyl.coupon.entity.CouponTemplate;

/**
 * 异步服务接口定义
 * Created by PYL
 */
public interface IAsyncService {
    /**
     * 根据模板异步创建优惠券码
     * @param template {@link CouponTemplate} 优惠券模板实体
    * */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
