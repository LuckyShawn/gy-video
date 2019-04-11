package com.shawn.video.dao;


import com.shawn.video.pojo.SearchRecords;
import com.shawn.video.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}