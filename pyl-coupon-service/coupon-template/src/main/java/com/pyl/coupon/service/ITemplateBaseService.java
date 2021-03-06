package com.pyl.coupon.service;

import com.pyl.coupon.entity.CouponTemplate;
import com.pyl.coupon.exception.CouponException;
import com.pyl.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板基础服务定义
 * Created by PYL
 */
public interface ITemplateBaseService {
    /**
     * 根据优惠券模板Id获取优惠券模板信息
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     * */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * 查找所有可用的优惠券模板
     * @return {@link CouponTemplateSDK}s
     * */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板ids到CouponTemplateSDK的映射
     * @param ids 模板 ids
     * @return Map<key:模板 id,value: CouponTemplateSDK>
     * */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
