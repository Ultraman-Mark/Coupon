package com.pyl.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 有效期的类型
 * Created by PYL
 */
@Getter
@AllArgsConstructor
public enum PeriodType {

    REGULAR("固定的(固定日期)",1),
    SHIFT("变动的(以领取日期计算)",2);

    /**有效期描述*/
    private String description;

    /**有效期编码 */
    private Integer code;

    public static PeriodType of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean->bean.code.equals(code))
                .findAny()
                .orElseThrow(()->new IllegalArgumentException(code+"no exists!"));
    }
}
