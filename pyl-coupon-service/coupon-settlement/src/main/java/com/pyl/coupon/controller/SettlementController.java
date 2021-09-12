package com.pyl.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.pyl.coupon.exception.CouponException;
import com.pyl.coupon.executor.ExecuteManager;
import com.pyl.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结算服务的controller
 * Created by PYL
 */
@Slf4j
@RestController
public class SettlementController {

    /**结算规则执行管理器*/
    private ExecuteManager executeManager;

    @Autowired
    public SettlementController(ExecuteManager executeManager){
        this.executeManager = executeManager;
    }

    /**
     * 优惠券计算
     * localhost:7003/coupon-settlement/settlement/compute
     * localhost:9000/imooc/coupon-settlement/settlement/compute
     * */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException {
        log.info("settlement:{}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
