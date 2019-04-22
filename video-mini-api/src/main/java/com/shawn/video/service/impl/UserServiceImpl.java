package com.shawn.video.service.impl;

import com.shawn.video.dao.UsersFansMapper;
import com.shawn.video.dao.UsersLikeVideosMapper;
import com.shawn.video.dao.UsersMapper;
import com.shawn.video.idworker.Sid;
import com.shawn.video.pojo.Users;
import com.shawn.video.pojo.UsersFans;
import com.shawn.video.pojo.UsersLikeVideos;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserNameIsExist(String userName) {
        Users user = new Users();
        user.setUsername(userName);
        Users result = usersMapper.selectOne(user);
        return result == null ? false : true;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        user.setId(sid.nextShort());
        usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users login(Users user) throws Exception {
        Users result = usersMapper.selectOne(new Users(user.getUsername()));
        if(result==null){
            return null;
        }
        if(result.getPassword().equals(MD5Utils.getMD5Str(user.getPassword()))){
            return result;
        }else{
            return null;
        }
    }

    @Override
    public void updateUserInfo(Users user) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",user.getId());
        usersMapper.updateByExampleSelective(user,userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)){
            return false;
        }
        Example userExample = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);

        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(userExample);
        if(list != null && list.size() > 0){
            return true;
        }
        return false;
    }

    /**
     * 保存用户的关系（关注和被关注）
     * @param userId
     * @param fansId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fansId) {
        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setUserId(userId);
        usersFans.setFanId(fansId);
        usersFansMapper.insert(usersFans);
        //增加粉丝数
        usersMapper.addFansCount(userId);
        //增加关注数
        usersMapper.addFollersCount(fansId);
    }

    /**
     * 删除用户的关系（取消关注和被取消关注）
     * @param userId
     * @param fansId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fansId) {
        Example userExample = new Example(UsersFans.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fansId",fansId);
        usersFansMapper.deleteByExample(userExample);
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fansId);
    }


}
