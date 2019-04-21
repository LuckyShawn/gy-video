package com.shawn.video;

import com.shawn.video.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/15 0015
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:F:/WechatDev/javaworkspace/wechat_resource/");
    }

    @Bean
    public MiniInterceptor miniInterceptor(){
        return new MiniInterceptor();
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
                .addPathPatterns("/bgm/**")
                .addPathPatterns("/video/upload","/video/uploadCover")
                .addPathPatterns("/video/userLike","/video/userUnLike");
        super.addInterceptors(registry);
    }

}
