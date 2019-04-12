package com.shawn.video.controller;

import com.shawn.video.pojo.Users;
import com.shawn.video.pojo.vo.UsersVO;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.JSONResult;
import com.shawn.video.utils.MD5Utils;
import com.shawn.video.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@RestController
@Api(value = "用户注册登录的接口",tags = {"注册和登录的controller"})
public class RegisterLoginController extends BasicController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册接口", notes = "用于用户注册新账户")
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
        }else{
            return JSONResult.errorMsg("用户名已经存在，请换一个用户名！");
        }
        user.setPassword("");
        UsersVO usersVO = setUserRedisSessionToken(user);
        return JSONResult.ok(usersVO);
    }

    /**
     * 将session token存入redis
     * @param users
     * @return
     */
    public UsersVO setUserRedisSessionToken(Users users){
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + users.getId(),uniqueToken,1000 * 60 * 30);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }


    @ApiOperation(value = "用户登录接口", notes = "用于用户登录")
    @PostMapping("/login")
    public JSONResult login(@RequestBody Users user) throws Exception {
        //1.判断用户名和密码必须不为空
        JSONResult jsonResult = checkUsernameAndPwd(user);
        if(jsonResult != null){
            return jsonResult;
        }

        //2.匹配用户名密码
        Users result = userService.login(user);
        if(result == null){
            return JSONResult.errorMsg("用户名或密码不正确！");
        }
        result.setPassword("");
        UsersVO usersVO = setUserRedisSessionToken(result);
        return result!=null?JSONResult.ok(usersVO):JSONResult.errorMsg("用户名或密码不正确！");
    }

    @ApiOperation(value = "用户注销接口", notes = "用于注销登录")
    @ApiImplicitParam(name="userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/logout")
    public JSONResult logout(String userId) throws Exception {
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return JSONResult.ok("注销成功");
    }


    /**
     * 判断用户名密码不为空
     * @param user
     * @return
     */
    public JSONResult checkUsernameAndPwd(Users user){
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return JSONResult.errorMsg("用户名或密码不能为空！");
        }else{
            return null;
        }
    }
}
