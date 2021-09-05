package com.ply.coupon.converter;

import com.pyl.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.print.attribute.Attribute;

/**
 * 优惠券分类枚举属性转化器
 * AttributeConverter<x,y>
 * x:实体属性的类型
 * y:数据库字段的类型
 * Created by PYL
 */
@Convert
public class CouponCategoryConverter
        implements AttributeConverter<CouponCategory,String> {
    /**
     * 将实体属性X转换为Y存储到数据库中
     * */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * 将数据库中的字段Y转换成实体属性X,查询操作时执行的动作
     * */
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
