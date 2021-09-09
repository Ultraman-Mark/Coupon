package com.pyl.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pyl.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 优惠券模板实体类自定义序列化器
 * Created by PYL
 */
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {
    @Override
    public void serialize(CouponTemplate template,
                          JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
        //开始序列化对象
        generator.writeStartObject();

        generator.writeStringField("id",template.getId().toString());
        generator.writeStringField("name",template.getName());
        generator.writeStringField("desc",template.getLogo());
        generator.writeStringField("category",
                template.getCategory().getDescription());
        generator.writeStringField("productLine",
                template.getProductLine().getDescription());
        generator.writeStringField("count",template.getCount().toString());
        generator.writeStringField("creatTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(template.getCreateTime()));
        generator.writeStringField("userId",template.getUserId().toString());
        generator.writeStringField("key",
                template.getKey() + String.format("%04d",template.getId()));
        generator.writeStringField("target",template.getTarget().getDescription());
        generator.writeStringField("rule",
                JSON.toJSONString(template.getRule()));

        //结束序列化对象
        generator.writeEndObject();
    }
}
