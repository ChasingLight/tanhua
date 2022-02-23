package com.tanhua.server.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 利用sentinel实现网关gateway授权规则【在sentinel-dashboard配置，授权规则-白名单】
 * ---从gateway经过，路由访问app-server的，请求头部含有origin:gateway
 * ---直接访问app-server的，请求头部不含有origin:gateway
 */
@Component
public class HeaderOriginParser implements RequestOriginParser {

    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 1.获取请求头
        String origin = request.getHeader("origin");
        // 2.非空判断
        if (StringUtils.isEmpty(origin)) {
            origin = "blank";
        }
        return origin;
    }
}
