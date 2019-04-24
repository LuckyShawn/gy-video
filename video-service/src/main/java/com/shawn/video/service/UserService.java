package com.shawn.video.service;

import com.shawn.video.pojo.Users;
import com.shawn.video.pojo.UsersReport;

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

    /**
     * 修改用户信息
     * @param user
     */
    void updateUserInfo(Users user);


    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    Users queryUserInfo(String userId);

    /**
     *  查询用户是否喜欢点赞视频
     * @param userId
     * @param videoId
     * @return
     */
    boolean isUserLikeVideo(String userId,String videoId);

    /**
     * 增加用户和粉丝的关系
     * @param userId
     * @param fansId
     */
    void saveUserFanRelation(String userId,String fansId);
    /**
     * 删除用户和粉丝的关系
     * @param userId
     * @param fansId
     */
    void deleteUserFanRelation(String userId,String fansId);

    /**
     * 查询用户是否关注
     * @param userId
     * @param fansId
     * @return
     */
    boolean queryIfFollow(String userId,String fansId);

    /**
     * 举报用户
     * @param usersReport
     */
    void reportUser(UsersReport usersReport);
}
