package com.pyl.coupon.service.Impl;

import com.pyl.coupon.dao.CouponTemplateDao;
import com.pyl.coupon.entity.CouponTemplate;
import com.pyl.coupon.service.IAsyncService;
import com.pyl.coupon.service.IBuildTemplateService;
import com.pyl.coupon.vo.TemplateRequest;
import com.pyl.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *构建优惠券模板接口实现
 * Created by PYL
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {
    /** 异步服务 */
    private final IAsyncService asyncService;
    /** CouponTemplate */
    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService,CouponTemplateDao templateDao){
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    /**
     * 创建优惠券模板
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return  {@link CouponTemplate} 优惠券模板实体
     * */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
        //参数合法性校验
        if (!request.validate()){
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }
        // 判断同名的优惠券模板是否存在
        if (null != templateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Name Template!");
        }

        //构造CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);

        //根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /**
     * 将TemplateReques 转换成 CouponTemplate
     * */
    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
