package com.mission.schedule.bean;

import java.io.Serializable;
import java.util.List;

public class TagBackBean implements Serializable{

	private static final long serialVersionUID = 1L;

	public List<TagDelBean> delList;
	public String downTime;
	public List<TagBean> list;
	public String message;
	public int status;
}
