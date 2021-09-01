package com.pyl.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 网关应用启动入口
 * 1.@EnableZuulProxy标识当前应用是Zuul Server
 * 2.@SpringCloudApplication组合springboot应用+服务发现+熔断
 * Created by PYL
 */
@EnableZuulProxy
@SpringCloudApplication
public class ZuulGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGateWayApplication.class,args);
    }
}
