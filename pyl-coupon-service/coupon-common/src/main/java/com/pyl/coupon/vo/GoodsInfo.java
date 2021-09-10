package com.pyl.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * fake商品信息
 * Created by PYL
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo {
    /**商品类型：{@link com.pyl.coupon.constant.GoodsType}*/
    private Integer type;

    /** 商品价格 */
    private Double price;

    /** 商品数量 */
    private Integer count;


}
