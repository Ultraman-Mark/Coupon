package com.pyl.coupon.executor;

import com.pyl.coupon.constant.RuleFlag;
import com.pyl.coupon.vo.SettlementInfo;

/**
 * 优惠券模板规则处理器接口定义
 * Created by PYL
 */
public interface RuleExecutor {
    /**
     * 规则类型标记
     * @return {@link RuleFlag}
     * */
    RuleFlag ruleConfig();

    /**
     * 优惠券规则计算
     * @param settlement {@link SettlementInfo}包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的计算信息
     * */
    SettlementInfo computerRule(SettlementInfo settlement);
}
