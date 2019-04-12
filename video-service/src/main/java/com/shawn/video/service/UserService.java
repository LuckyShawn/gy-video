package com.shawn.video.service;

import com.shawn.video.pojo.Users;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     * @return
     */
    boolean queryUserNameIsExist(String userName);

    /**
     * 注册保存用户信息
     * @param users
     */
    void saveUser(Users users);

    /**
     * 用户登录
     * @param users
     * @return
     */
    Users login(Users users) throws Exception;
}
