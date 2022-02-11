package com.tanhua.server.interceptor;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import io.jsonwebtoken.Claims;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器:拦截器主要拦截请求controller请求
 */
public class TokenInterceptor implements HandlerInterceptor {
    //前置拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、获取请求头
        String token = request.getHeader("Authorization");

        //2、判断token是否有效  如果token失效，返回状态码401，拦截

        // 判断token是否有效，已放到Gateway网关中判断
       /* boolean verifyToken = JwtUtils.verifyToken(token);
        if(!verifyToken) {
            response.setStatus(401);
            return false;
        }*/

        //3、如果token正常可用，放行
        //解析token,获取id和手机号,构造User对象,存入ThreadLocal
        Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");
        String mobile = (String) claims.get("phone");

        User user = new User();
        user.setMobile(mobile);
        user.setId(Long.valueOf(id));
        UserHolder.set(user);
        return true;
    }

    //响应处理
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
    }

    //最终增强
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserHolder.remove();
    }

}
