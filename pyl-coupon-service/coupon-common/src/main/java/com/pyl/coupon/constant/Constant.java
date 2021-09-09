package com.pyl.coupon.constant;

/**
 * 通用常量定义
 * Created by PYL
 */
public class Constant {
    /**kafka消息 Topic*/
    public static final String TOPIC = "pyl_user_coupon_op";

    /**
     * Redis key前缀定义
     */
    public static class RedisPrefix{
        /** 优惠券码key前缀*/
        public static final String COUPON_TEMPLATE =
                "pyl_coupon_template_code_";

        /** 用户当前所有可用的优惠券 key 前缀 */
        public static final String USER_COUPON_USABLE =
                "pyl_user_coupon_usable_";

        /** 用户当前所有已使用的优惠券 key 前缀 */
        public static final String USER_COUPON_USED =
                "pyl_user_coupon_used_";

        /** 用户当前所有已过期的优惠券 key 前缀*/
        public static final String USER_COUPON_EXPIRED =
                "pyl_user_coupon_expired_";
    }
}
