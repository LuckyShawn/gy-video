package com.shawn.video.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shawn.video.dao.*;
import com.shawn.video.idworker.Sid;
import com.shawn.video.pojo.SearchRecords;
import com.shawn.video.pojo.UsersLikeVideos;
import com.shawn.video.pojo.Videos;
import com.shawn.video.pojo.vo.VideosVO;
import com.shawn.video.service.VideoService;
import com.shawn.video.utils.PagedResult;
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
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersMapper usersMapper;

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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

        //2.视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);
        //3.用户收到喜欢数量累加
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.删除用户和视频的喜欢点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);

        usersLikeVideosMapper.deleteByExample(example);

        //2.视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);
        //3.用户收到喜欢数量累减
        usersMapper.reduceReceiveLikeCount(videoCreaterId);
    }

}
