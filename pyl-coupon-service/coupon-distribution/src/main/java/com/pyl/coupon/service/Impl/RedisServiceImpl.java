package com.pyl.coupon.service.Impl;

import com.alibaba.fastjson.JSON;
import com.pyl.coupon.constant.Constant;
import com.pyl.coupon.constant.CouponStatus;
import com.pyl.coupon.entity.Coupon;
import com.pyl.coupon.exception.CouponException;
import com.pyl.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis相关的服务接口实现
 * Created by PYL
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    /**redis客户端*/
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据优惠券id+状态 找到缓存的优惠券列表数据
     * @param userId 用户 id
     * @param status 优惠券状态 {@link com.pyl.coupon.constant.CouponStatus}
     * @return {@link Coupon}s，注意，可能返回 null，代表从来没有记录
     * */
    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache:{}, {}",userId,status);
        String redisKey = status2RedisKey(status,userId);

        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o-> Objects.toString(o,null))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStrs)){
            saveEmptyCouponListToCache(userId,Collections.singletonList(status));
            return Collections.emptyList();
        }

        return couponStrs.stream().map(cs->JSON.parseObject(cs,Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * 避免空的优惠券列表到缓存中
     * 目的：避免缓存穿透
     * @param userId 用户 id
     * @param status 优惠券列表
     * */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User:{} Status:{}",
                userId, JSON.toJSONString(status));

        /**key是coupon_id,value是序列化的Coupon*/
        Map<String,String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1",JSON.toJSONString(Coupon.invalidCoupon()));

        //用户优惠券缓存信息
        //KV
        //K: status->redisKey
        //V: {coupon_id:序列化的Coupon}

        /**使用SessionCallback把数据命令放到Redis的pipeline*/
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                status.forEach(s->{
                    String redisKey = status2RedisKey(s,userId);
                    operations.opsForHash().putAll(redisKey,invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    /**
     * 尝试从Cache中获取一个优惠券吗
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     * */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        // 因为优惠券码不存在顺序关系, 左边 pop 或右边 pop, 没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code: {}, {}, {}",
                templateId, redisKey, couponCode);

        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons,
                                    Integer status) throws ClassCastException, CouponException {
        log.info("Add Coupon To Cache:{},{},{}",
                userId,JSON.toJSONString(coupons),status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus){
            case USABLE:
                result = addCouponToCacheForUsable(userId,coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId,coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId,coupons);
                break;
        }
        return result;
    }

    /** 新增优惠券到cache中*/
    private Integer addCouponToCacheForUsable(Long userId,List<Coupon> coupons){
        //如果status是USABLE,代表新增优惠券
        //只会影响一个Cache:USER_COUPON_USEABLE
        log.debug("Add Coupon To Cache For Usable.");

        Map<String,String> needCachedObejct = new HashMap<>();
        coupons.forEach(c->
                needCachedObejct.put(
                        c.getId().toString(),
                        JSON.toJSONString(c)
                ));
        String redisKey = status2RedisKey(
                CouponStatus.USABLE.getCode(),userId);
        redisTemplate.opsForHash().putAll(redisKey,needCachedObejct);
        log.info("Add {} Coupon To Cache:{}, {}",
                needCachedObejct.size(),userId,redisKey);

        redisTemplate.expire(
                redisKey,
                getRandomExpirationTime(1,2),
                TimeUnit.SECONDS
        );
        return needCachedObejct.size();
    }

    /** 将已使用的优惠券加入到 Cache 中 */
    private Integer addCouponToCacheForUsed(Long userId,List<Coupon> coupons)
            throws CouponException {

        //如果status是USED,代表用户操作是使用当前优惠券，影响两个Cache
        //USABLE,USED
        log.debug("Add Coupon To Cache For Used.");

        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForUsed = status2RedisKey(
                CouponStatus.USED.getCode(), userId
        );
        //获取当前用户可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupons(
                userId,CouponStatus.USABLE.getCode()
        );
        //当前可用的优惠券个数一定>1;
        assert curUsableCoupons.size() >coupons.size();
        
        coupons.forEach(c->needCachedForUsed.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));
        //校验当前的优惠券参数是否与Cache匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        
        if (!CollectionUtils.isSubCollection(paramIds,curUsableIds)){
            log.error("CurCoupons Is Not Equal To Cache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal To Cache.");
        }

        List<String> needCleanKey = paramIds.stream()
                .map(i->i.toString()).collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 1.已使用的优惠券 Cache 缓存添加
                operations.opsForHash().putAll(
                        redisKeyForUsable,needCachedForUsed
                );
                //2.可用的优惠券Cache需要清理
                operations.opsForHash().delete(
                        redisKeyForUsable,needCleanKey.toArray()
                );
                //3.重置过期时间
                operations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                operations.expire(
                        redisKeyForUsed,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Piplin Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /** 将过期优惠券加入到 Cache 中 */
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons)
            throws CouponException {

        // status 是 EXPIRED, 代表是已有的优惠券过期了, 影响到两个 Cache
        // USABLE, EXPIRED
        log.debug("Add Coupon To Cache For Expired.");

        //最终需要保存的 Cache
        Map<String, String> needCachedForExpired = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(),userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(),userId
        );
        List<Coupon> curUsableCoupons = getCachedCoupons(
                userId,CouponStatus.USABLE.getCode()
        );
        List<Coupon> curExpiredCoupons = getCachedCoupons(
                userId,CouponStatus.EXPIRED.getCode()
        );
        //当前可用的优惠券个数一定是大于1的
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c->needCachedForExpired.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));
        //校验当前的优惠券参数是否与Cached中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if(CollectionUtils.isSubCollection(paramIds,curUsableIds)){
            log.error("CurCoupons Is Not Equal To Cache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal To Cache.");
        }
        List<String> needCleanKey = paramIds.stream()
                .map(i->i.toString()).collect(Collectors.toList());

        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations operations) throws DataAccessException {
                // 1. 已过期的优惠券 Cache 缓存
                operations.opsForHash().putAll(
                        redisKeyForExpired, needCachedForExpired
                );
                // 2. 可用的优惠券 Cache 需要清理
                operations.opsForHash().delete(
                        redisKeyForUsable, needCleanKey.toArray()
                );
                // 3. 重置过期时间
                operations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                operations.expire(
                        redisKeyForExpired,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(
                        redisTemplate.executePipelined(sessionCallback)
                ));
        return coupons.size();
    }

    /**
     * 根据status获取对应的Redis key
     * */
    private String status2RedisKey(Integer status,Long userId){
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus){
            case USABLE:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USABLE,userId);
                break;
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED,userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED,userId);
                break;
        }
        return redisKey;
    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩：key在同一时间失效
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回[min,max]之间的随机秒数
     * */
    private Long getRandomExpirationTime(Integer min,Integer max){
        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60
        );
    }
}
