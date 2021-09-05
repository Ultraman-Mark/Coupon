package com.ply.coupon.vo;

import com.pyl.coupon.constant.CouponCategory;
import com.pyl.coupon.constant.DistributeTarget;
import com.pyl.coupon.constant.ProductLine;
import com.pyl.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠券模板创建请求对象
 * Created by PYL
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {
    /** 优惠券名称 */
    private String name;

    /** 优惠券 logo */
    private String logo;

    /** 优惠券描述 */
    private String desc;

    /** 优惠券分类 */
    private String category;

    /** 产品线 */
    private Integer productLine;

    /** 总数 */
    private Integer count;

    /** 创建用户 */
    private Long userId;

    /** 目标用户 */
    private Integer target;

    /** 优惠券规则 */
    private TemplateRule rule;

    /**校验对象的合法性*/
    public boolean validate(){
        boolean stringVaild = StringUtils.isNotEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);

        boolean enumVaild = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);

        boolean numVaild = count>0 && userId>0;

        return stringVaild && enumVaild && numVaild && rule.validate();
    }

}
