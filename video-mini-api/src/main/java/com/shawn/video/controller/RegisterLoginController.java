package com.shawn.video.controller;

import com.shawn.video.pojo.Users;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.JSONResult;
import com.shawn.video.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@RestController
@Api(value = "用户注册登录的接口",tags = {"注册和登录的controller"})
public class RegisterLoginController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登录接口", notes = "用于用户注册")
    @PostMapping("/register")
    public JSONResult register(@RequestBody Users user){
        //1.判断用户名和密码必须不为空
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return JSONResult.errorMsg("用户名或密码不能为空！");
        }
        //2.判断用户名是否存在
        boolean flag = userService.queryUserNameIsExist(user.getUsername());

        //3.保存用户，注册信息
        if(!flag){
            try {
                user.setNickname(user.getUsername());
                user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
                user.setFansCounts(0);
                user.setReceiveLikeCounts(0);
                user.setFollowCounts(0);
                userService.saveUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       return JSONResult.ok();
    }
}
