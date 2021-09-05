package com.ply.coupon.converter;

import com.pyl.coupon.constant.DistributeTarget;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 分发目标枚举转换器
 * Created by PYL
 */
@Convert
public class DistributeTargetConverter
        implements AttributeConverter<DistributeTarget,Integer> {

    @Override
    public Integer convertToDatabaseColumn(DistributeTarget distributeTarget) {
        return distributeTarget.getCode();
    }

    @Override
    public DistributeTarget convertToEntityAttribute(Integer code) {
        return DistributeTarget.of(code);
    }
}
