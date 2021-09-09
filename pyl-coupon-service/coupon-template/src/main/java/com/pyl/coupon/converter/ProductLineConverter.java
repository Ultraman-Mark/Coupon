package com.pyl.coupon.converter;

import com.pyl.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 产品线枚举属性转化器
 * Created by PYL
 */
@Convert
public class ProductLineConverter
        implements AttributeConverter<ProductLine,Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer code) {
        return ProductLine.of(code);
    }
}
