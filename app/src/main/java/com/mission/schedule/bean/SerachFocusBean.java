package com.mission.schedule.bean;

import java.io.Serializable;
import java.util.List;

public class SerachFocusBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int currentPage;
	public List<FocusFriendsBean> items;
	public int pageSize;
	public int totalCount;
	public int totalPage;
}
