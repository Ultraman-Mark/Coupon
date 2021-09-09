package com.pyl.coupon.service;

import com.pyl.coupon.entity.CouponTemplate;
import com.pyl.coupon.vo.TemplateRequest;
import com.pyl.coupon.exception.CouponException;

/**
 * 构建优惠券模板接口定义
 * Created by PYL
 */
public interface IBuildTemplateService {
    /**
     * 创建优惠券模板
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return  {@link CouponTemplate} 优惠券模板实体
     * */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
