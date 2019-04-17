package com.shawn.video.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shawn.video.dao.BgmMapper;
import com.shawn.video.dao.VideosMapper;
import com.shawn.video.dao.VideosMapperCustom;
import com.shawn.video.idworker.Sid;
import com.shawn.video.pojo.Bgm;
import com.shawn.video.pojo.Videos;
import com.shawn.video.pojo.vo.VideosVO;
import com.shawn.video.service.BgmService;
import com.shawn.video.service.VideoService;
import com.shawn.video.utils.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private VideosMapperCustom videosMapperCustom;

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

    @Override
    public PagedResult getAllVideo(Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(null,null);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult result = new PagedResult();
        result.setPage(page);
        result.setTotal(pageList.getPages());
        result.setRows(list);
        result.setRecords(pageList.getTotal());

        return result;
    }

}
