package com.shawn.video.dao;


import com.shawn.video.pojo.Comments;
import com.shawn.video.pojo.vo.CommentsVO;
import com.shawn.video.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}