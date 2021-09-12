package com.pyl.coupon.executor;

import com.pyl.coupon.constant.CouponCategory;
import com.pyl.coupon.constant.RuleFlag;
import com.pyl.coupon.exception.CouponException;
import com.pyl.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券结算规则执行管理器
 * 即根据用户的请求(SettlementInfo)找到对应的 Executor, 去做结算
 * BeanPostProcessor: Bean 后置处理器
 * Created by PYL
 */
@Slf4j
@Component
public class ExecuteManager implements BeanPostProcessor {
    /** 规则执行器映射 */
    private static Map<RuleFlag,RuleExecutor> executorIndex =
            new HashMap<>(RuleFlag.values().length);

    /**
     * 优惠券结算规则计算入口
     * 注意：一定要保证传递进来的优惠券个数 >= 1
     * */
    public SettlementInfo computeRule(SettlementInfo settlement)
            throws CouponException{
        SettlementInfo result = null;
        //单类优惠券
        if (settlement.getCouponAndTemplateInfos().size()==1){
            //获取优惠券类别
            CouponCategory category = CouponCategory.of(
                    settlement.getCouponAndTemplateInfos().get(0)
                            .getTemplate().getCateagory()
            );
            switch (category){
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.MANJIAN)
                            .computerRule(settlement);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU)
                            .computerRule(settlement);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN)
                            .computerRule(settlement);
                    break;
            }
        } else{
            //多类优惠券
            List<CouponCategory> categories = new ArrayList<>(
                    settlement.getCouponAndTemplateInfos().size()
            );
            settlement.getCouponAndTemplateInfos().forEach(ct->
                    categories.add(CouponCategory.of(ct.getTemplate().getCateagory()
                    )));
            if (categories.size()!=2){
                throw new CouponException("Not Support For More " +
                        "Template Category");
            }else{
                if (categories.contains(CouponCategory.MANJIAN)
                        && categories.contains(CouponCategory.ZHEKOU)) {
                    result = executorIndex.get(RuleFlag.MANJIAN_ZHEKOU)
                            .computerRule(settlement);
                } else {
                    throw new CouponException("Not Support For Other " +
                            "Template Category");
                }
            }
        }
        return result;
    }

    /**
     * 在bean初始化之前去执行
     * */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof RuleExecutor)){
            return bean;
        }
        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        if (executorIndex.containsKey(ruleFlag)){
            throw new IllegalStateException("There is alreay an executor"+
                    "for rule flag" + ruleFlag);
        }

        log.info("Load executor {} for rule flag {}.",
                executor.getClass(),ruleFlag);
        executorIndex.put(ruleFlag,executor);

        return null;
    }

    /**
     * 在bean初始化之后去执行
     * */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
