package com.shawn.video.interceptor;


import com.shawn.video.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 拦截器
 * @Author shawn
 * @create 2019/4/18 0018
 */
public class MiniInterceptor implements HandlerInterceptor {
    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";
    /**
     * 拦截请求，在controller之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");
        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken =  redis.get(USER_REDIS_SESSION + ":"+userId);
            if(StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)){
                System.out.println("请登录...");
                return false;
            }else {
                if(uniqueToken.equals(userToken)){
                    System.out.println("帐号在别的手机端登录了...");
                    return false;
                }
            }

            System.out.println("请求拦截...");
            return false;
        }
        /**
         * 返回false：请求被拦截，返回.
         * 返回true：请求ok，可以继续执行
         */
        return false;
    }

    /**
     * 请求controller之后，渲染师徒之前
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求controller之后，试视图渲染之后
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
