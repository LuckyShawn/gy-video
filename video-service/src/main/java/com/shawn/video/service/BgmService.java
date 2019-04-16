package com.shawn.video.service;


import com.shawn.video.pojo.Bgm;

import java.util.List;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/15 0011
 */
public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    List<Bgm> queryBgmList();

    /**
     * 根据bgmId查询bgm信息
     * @param bgmId
     * @return
     */
    Bgm queryBgmById(String bgmId);

}
