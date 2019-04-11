package com.shawn.video;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@SpringBootApplication
@MapperScan(basePackages = "com.shawn.video")
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
