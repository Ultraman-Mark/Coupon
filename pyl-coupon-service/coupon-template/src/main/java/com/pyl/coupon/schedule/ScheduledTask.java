package com.pyl.coupon.schedule;

import com.pyl.coupon.dao.CouponTemplateDao;
import com.pyl.coupon.entity.CouponTemplate;
import com.pyl.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时清理已过期的优惠券模板
 * Created by PYL
 */
@Slf4j
@Component
public class ScheduledTask {
    /**CouponTemplate Dao接口*/
    private final CouponTemplateDao templateDao;

    @Autowired
    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * 下线已过期优惠券模板
     * */
    @Scheduled(fixedRate = 60*60*100)
    private void offlineCouponTemplate(){
        log.info("Start To Expire CouponTemplate");
        List<CouponTemplate> templates =
                templateDao.findAllByExpired(false);
        if(CollectionUtils.isEmpty(templates)){
            log.info("Done To Expire CouponTemplate.");
            return;
        }

        Date cur = new Date();
        List<CouponTemplate> expiredTemplates =
                new ArrayList<>(templates.size());
        templates.forEach(t ->{
            //根据优惠券模板规则中的过期规则，校验是否过期
            TemplateRule rule = t.getRule();
            if(rule.getExpiration().getDeadline() < cur.getTime()){
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });

        if (CollectionUtils.isNotEmpty(expiredTemplates)){
            log.info("Expired CouponTemplate Num:{}",
                    templateDao.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate.");
    }
}
