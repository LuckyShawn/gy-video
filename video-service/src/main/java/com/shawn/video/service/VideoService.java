package com.shawn.video.service;


import com.shawn.video.pojo.Bgm;
import com.shawn.video.pojo.Videos;
import com.shawn.video.utils.PagedResult;

import java.util.List;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/15 0011
 */
public interface VideoService {

    /**
     * 保存视频信息
     * @param video
     * @return
     */
    String saveVideo(Videos video);

    /**
     * 修改视频的封面
     * @param videoId
     * @param coverPath
     * @return
     */
    void updateVideo(String videoId,String coverPath);

    /**
     * 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllVideo(Videos video,Integer isSaveRecord,Integer page, Integer pageSize);


    /**
     * 获取热搜词列表
     * @return
     */
    List<String> getHotWords();

    /**
     * 用户喜欢视频（点赞）
     * @param userId
     */
    void userLikeVideo(String userId,String videoId,String videoCreaterId);
    /**
     * 用户不喜欢视频（取消点赞）
     * @param userId
     */
    void userUnLikeVideo(String userId,String videoId,String videoCreaterId);
}
