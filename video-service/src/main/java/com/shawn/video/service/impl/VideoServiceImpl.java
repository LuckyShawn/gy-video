package com.shawn.video.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shawn.video.dao.BgmMapper;
import com.shawn.video.dao.SearchRecordsMapper;
import com.shawn.video.dao.VideosMapper;
import com.shawn.video.dao.VideosMapperCustom;
import com.shawn.video.idworker.Sid;
import com.shawn.video.pojo.Bgm;
import com.shawn.video.pojo.SearchRecords;
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
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private Sid sid;
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);
        return id;
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllVideo(Videos video,Integer isSaveRecord, Integer page, Integer pageSize) {

        //保存热搜词
        String desc = video.getVideoDesc();
        if(isSaveRecord != null && isSaveRecord == 1){
            SearchRecords searchRecords = new SearchRecords();
            String recordId = sid.nextShort();
            searchRecords.setId(recordId);
            searchRecords.setContent(desc);
            searchRecordsMapper.insert(searchRecords);
        }

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,null);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult result = new PagedResult();
        result.setPage(page);
        result.setTotal(pageList.getPages());
        result.setRows(list);
        result.setRecords(pageList.getTotal());

        return result;
    }

    /**
     * 获取热词列表
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotwords();
    }

}
