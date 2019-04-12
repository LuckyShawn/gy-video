package com.shawn.video;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@SpringBootApplication
@MapperScan(basePackages = "com.shawn.video.dao")   //tk.mybatis.spring.annotation.MapperScan;
@ComponentScan(basePackages = {"com.shawn.video","com.shawn.video.idworker"})
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
