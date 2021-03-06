package com.shawn.video.dao;


import com.shawn.video.pojo.Users;
import com.shawn.video.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
	
	/**
	 * @Description: 用户受喜欢数累加
	 */
	void addReceiveLikeCount(String userId);
	
	/**
	 * @Description: 用户受喜欢数累减
	 */
	void reduceReceiveLikeCount(String userId);
	
	/**
	 * @Description: 增加粉丝数
	 */
	void addFansCount(String userId);
	
	/**
	 * @Description: 增加关注数
	 */
	void addFollersCount(String userId);
	
	/**
	 * @Description: 减少粉丝数
	 */
	void reduceFansCount(String userId);
	
	/**
	 * @Description: 减少关注数
	 */
	void reduceFollersCount(String userId);
}