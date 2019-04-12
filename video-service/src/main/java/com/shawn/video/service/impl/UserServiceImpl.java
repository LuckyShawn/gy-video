package com.shawn.video.service.impl;

import com.shawn.video.dao.UsersMapper;
import com.shawn.video.idworker.Sid;
import com.shawn.video.pojo.Users;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
}
