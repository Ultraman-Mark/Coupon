package com.pyl.coupon.advice;

import com.pyl.coupon.exception.CouponException;
import com.pyl.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * Created by PYL
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 对CouponException 进行统一处理
     * */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest req, CouponException ex){
        CommonResponse<String> response = new CommonResponse<>(-1,"business error");
        response.setData(ex.getMessage());
        return response;
    }
}
