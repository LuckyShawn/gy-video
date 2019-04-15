package com.shawn.video.controller;

import com.shawn.video.service.BgmService;
import com.shawn.video.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description BgmController
 * @Author shawn
 * @create 2019/4/15 0011
 */
@RestController
@Api(value = "背景音乐业务接口", tags = {"背景音乐业务的controller"})
@RequestMapping("/bgm")
public class BgmController {

    @Autowired
    private BgmService bgmService;

    @ApiOperation(value = "获取背景音乐列表" ,notes = "获取背景音乐列表的接口")
    //@RequestMapping("/list")    //swagger2中包含所有类型的list  post,get,del...
    @PostMapping("/list")
    public JSONResult hello(){
        return JSONResult.ok(bgmService.queryBgmList());
    }
}
