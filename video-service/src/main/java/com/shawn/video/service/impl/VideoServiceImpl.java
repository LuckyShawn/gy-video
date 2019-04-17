package com.shawn.video.service.impl;

import com.shawn.video.dao.BgmMapper;
import com.shawn.video.dao.VideosMapper;
import com.shawn.video.idworker.Sid;
import com.shawn.video.pojo.Bgm;
import com.shawn.video.pojo.Videos;
import com.shawn.video.service.BgmService;
import com.shawn.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description BGM Service实现类
 * @Author shawn
 * @create 2019/4/11 0011
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private Sid sid;

    @Override
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);
        return id;
    }

    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }
}
