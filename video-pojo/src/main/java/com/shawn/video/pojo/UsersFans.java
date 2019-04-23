package com.shawn.video.pojo;

import javax.persistence.*;

@Table(name = "users_fans")
public class UsersFans {
    @Id
    private String id;

    /**
     * 用户
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 粉丝
     */
    @Column(name = "fan_id")
    private String fansId;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户
     *
     * @return user_id - 用户
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户
     *
     * @param userId 用户
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取粉丝
     *
     * @return fan_id - 粉丝
     */
    public String getFansId() {
        return fansId;
    }

    /**
     * 设置粉丝
     *
     * @param fansId 粉丝
     */
    public void setFansId(String fansId) {
        this.fansId = fansId;
    }
}