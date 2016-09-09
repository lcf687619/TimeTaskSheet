package com.mission.schedule.bean;

import java.io.Serializable;
import java.util.List;

public class NewMyFoundFragmentPageBeen implements Serializable{
	private static final long serialVersionUID = 1L;
	public int currentPage;
	public List<NewMyFoundFragmentItemsBeen> items;
	public int pageSize;
	public int totalCount;
	public int totalPage;
}
