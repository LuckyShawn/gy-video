package com.shawn.video.service.impl;

import com.shawn.video.dao.BgmMapper;
import com.shawn.video.dao.UsersMapper;
import com.shawn.video.idworker.Sid;
import com.shawn.video.pojo.Bgm;
import com.shawn.video.pojo.Users;
import com.shawn.video.service.BgmService;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Description BGM Service实现类
 * @Author shawn
 * @create 2019/4/11 0011
 */
@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

}
