package com.shawn.video;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @Description 继承SpringBootServletInitializer，相当于使用web.xml的形式启动部署
 * @Author shawn
 * @create 2019/5/6 0006
 */
public class WarStartApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //使用web.xml运行应用程序，指向Application，启动springboot
        return builder.sources(Application.class);
    }
}
