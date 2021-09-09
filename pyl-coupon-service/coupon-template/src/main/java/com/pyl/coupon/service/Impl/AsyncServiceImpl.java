package com.pyl.coupon.service.Impl;

import com.google.common.base.Stopwatch;
import com.pyl.coupon.dao.CouponTemplateDao;
import com.pyl.coupon.entity.CouponTemplate;
import com.pyl.coupon.service.IAsyncService;
import com.pyl.coupon.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 * Created by PYL
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {
    /**CouponTemplate Dao*/
    private final CouponTemplateDao templateDao;

    /**注入Redis模板类*/
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao,
                            StringRedisTemplate redisTemplate){
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据模板异步创建优惠券码
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    @Async("getAsyncExecutor")
    @Override
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> couponCodes = buildCouponCode(template);
        // pyl_coupon_template_code_1
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,template.getId().toString());
        log.info("Push CouponCode To Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey,couponCodes));
        template.setAvailable(true);
        templateDao.save(template);

        watch.stop();
        log.info("Construct CouponCode By Template Cost:{}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));
        //TODO 发生短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) Is Available!",template.getId());
    }

    /**
     * 构造优惠券码
     * 优惠券码（18位）
     * 前四位：产品线 + 类型
     * 中八位：日期随机
     * 后八位： 0~9随机
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与 template.count 相同个数的优惠券码
     **/
    private Set<String> buildCouponCode(CouponTemplate template){
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCount());
        //前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();

        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());

        for (int i=0; i != template.getCount(); ++i){
            result.add(prefix4+buildCouponCodeSuffix14(date));
        }

        while (result.size()<template.getCount()){
            result.add((prefix4 + buildCouponCodeSuffix14(date)));
        }

        assert  result.size() == template.getCount();
        watch.stop();
        log.info("Build Coupon Code Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * 构建优惠券码后14位
     * @param date 创建优惠券的日期
     * @return 14 位优惠券码
     * */
    private String buildCouponCodeSuffix14(String date){
        char[] bases = new char[]{'1','2','3','4','5','6','7','8','9'};

        //中间6位
        List<Character> chars = date.chars()
                .mapToObj(e -> (char)e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 =chars.stream()
                .map(Objects::toString).collect(Collectors.joining());

        //后八位
        String suffix8 = RandomStringUtils.random(1,bases)
                + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
