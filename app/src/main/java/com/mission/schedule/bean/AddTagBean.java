package com.mission.schedule.bean;

import java.io.Serializable;

public class AddTagBean implements Serializable{
	private static final long serialVersionUID = 1L;

	public int originalId;//":null,  老ID
	public int dataState;//":"0", 0添加  1修改  2删除
	public int id;//":142,
	public int state;//":0成功   1失败
}
