package com.mission.schedule.bean;

import java.io.Serializable;

public class FriendsBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public int fId;//好友Id
	public int state;//申请好友状态,0是好友1不是好友
	public int uId;//1,用户ID
	public String uName;//好友名称
	public String titleImg;//头像图片地址
	public int type;//0被申请，1表示向别人申请
    public String backImage;//：背景图片
    public int attentionState;//": 0以关注 1没有关注,
    public int redCount;//":未读取数量
    public int readState;
    public int isV;//0 是V 1不是V
    public int attState;//0 以关注1待关注
    public int isFrends;//0 是好友1否
    
}
