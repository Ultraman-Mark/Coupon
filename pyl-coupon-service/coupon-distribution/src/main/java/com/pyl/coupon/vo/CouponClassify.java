package com.pyl.coupon.vo;

import com.pyl.coupon.constant.CouponStatus;
import com.pyl.coupon.constant.PeriodType;
import com.pyl.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户优惠券分类
 * Created by PYL
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {
    /** 可以使用的 */
    private List<Coupon> usable;

    /** 已使用的 */
    private List<Coupon> used;

    /** 已过期的 */
    private List<Coupon> expired;

    /**
     * 对当前的优惠券进行分类
     * */
    public static CouponClassify classify(List<Coupon> coupons){
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c->{
            //判断优惠券是否过期
            boolean isTimeExpire;
            long curTime = new Date().getTime();

            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )){
                isTimeExpire = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;
            }else {
                isTimeExpire = DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }
            if (c.getStatus() == CouponStatus.USED){
                used.add(c);
            }else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpire){
                expired.add(c);
            }else {
                usable.add(c);
            }
        });
        return new CouponClassify(usable,used,expired);
    }

}
