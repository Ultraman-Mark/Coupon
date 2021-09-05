package com.ply.coupon.dao;

import com.ply.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * CouponTemplate Dao接口定义
 * Created by PYL
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Integer> {
    /**
     * 根据模板名称查询模板
     * where name = ...
     * */
    CouponTemplate findByName(String name);

    /**
     * 根据availale和expired标记查找模板记录
     * where available = ...  and expired = ...
     * */
    List<CouponTemplate> findAllByAvailableAndExpired(
            boolean available, Boolean expired
    );

    /**
     * 根据expired标记查找模板记录
     * where expired = ...
     * */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
