package com.pyl.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 结算信息对象定义
 * 包含：
 * 1.userId
 * 2.商品信息（列表）
 * 3.优惠券列表
 * 3.结算结果金额
 * Created by PYL
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {

    /** 用户id */
    private Long userId;

    /** 商品的信息 */
    private List<GoodsInfo> goodsInfos;

    /** 优惠券列表*/
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    /** 是否使结算生效，即核销 */
    private Boolean employ;

    /** 结果结算金额*/
    private Double cost;

    /** 优惠券和模板信息 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndTemplateInfo{
        /**Coupon 的主键*/
        private Integer id;

        /** 优惠券对应的模板对象*/
        private CouponTemplateSDK template;
    }
}
