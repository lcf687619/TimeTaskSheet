package com.mission.schedule.bean;

import java.io.Serializable;

public class AddFriendsBean implements Serializable{

	private static final long serialVersionUID = 1L;
	public int uid;//好友id
	public String uname;//好友名称
	public String titleImg;//头像图片地址
	public String attentionState;
	public String backImage;
	public int attState;//:0 以关注 1待关注
	public int isFrends;//:0 是 1否
    public int isV;//:0 是 1否
	public boolean flag = true;
	
}
