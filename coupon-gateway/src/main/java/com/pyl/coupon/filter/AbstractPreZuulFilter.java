package com.pyl.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import java.io.FileFilter;
import java.util.logging.Filter;

/**
 * Created by PYL
 */
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}
