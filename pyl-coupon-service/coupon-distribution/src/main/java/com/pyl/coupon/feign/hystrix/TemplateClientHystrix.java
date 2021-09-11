package com.pyl.coupon.feign.hystrix;

import com.pyl.coupon.feign.TemplateClient;
import com.pyl.coupon.vo.CommonResponse;
import com.pyl.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 优惠券模板 Feign 接口的熔断降级策略
 * Created by PYL
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {
    /**
     * 查找所有可用的优惠券模板
     * */
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate" +
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                Collections.emptyList()
        );
    }

    /**
     * 获取模板ids到CouponTemplatSDK的映射
     * @param ids 优惠券模板 id
     * */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK"+
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                new HashMap<>()
        );
    }
}
