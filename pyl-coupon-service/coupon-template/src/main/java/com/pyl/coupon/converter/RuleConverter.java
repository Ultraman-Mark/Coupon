package com.pyl.coupon.converter;

import com.alibaba.fastjson.JSON;
import com.pyl.coupon.vo.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券规则属性转换器
 * Created by PYL
 */
@Convert
public class RuleConverter
        implements AttributeConverter<TemplateRule,String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule,TemplateRule.class);
    }
}
