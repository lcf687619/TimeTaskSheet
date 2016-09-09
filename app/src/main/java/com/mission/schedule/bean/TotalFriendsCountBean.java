package com.mission.schedule.bean;

import java.io.Serializable;

public class TotalFriendsCountBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int bsqCount;//":被申请数量,
	public int count;//":聊天未读数量+被申请数量,
	public String message;//":"成功",
	public int status;//":0,
	public int wdltCount;//":聊天未读总数量
}
