package com.mission.schedule.bean;

import java.io.Serializable;
import java.util.List;

public class RepeatTuiJianBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String currentPage;
	public String pageSize;
	public String totalCount;
	public String totalPage;
	public List<RepeatTuiJianItemBean> items;
    
}
