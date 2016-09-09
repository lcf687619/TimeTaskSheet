package com.mission.schedule.bean;

import java.io.Serializable;

public class NewFocusDeleteSchDataBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int uid;//日程订阅下行
	public int dataId;// 数据id
	public int state;//0普通  1重复   日程订阅下线
	
	public int type;//7关注日程  8关注重复
	public int uId;//分享下行日程  控件id
}
