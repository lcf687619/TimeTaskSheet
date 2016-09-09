package com.mission.schedule.entity;

public class CLCategoryTable {
	public static final String CLCategoryTable = "CLCategoryTable";//表名
	public static final String ctgId = "ctgId";//int
	public static final String ctgType = "ctgType";//0、不可改分类标签 | 1、可修改分类标签  int
	public static final String ctgOrder = "ctgOrder";//排序值  int 
	public static final String ctgUpdateState = "ctgUpdateState";//0 不需要上传 | 1 新添加 | 2 已更改 | 3 已删除

	public static final String ctgText = "ctgText";//String
	public static final String ctgColor = "ctgColor";//颜色 String
	public static final String ctgDesc = "ctgDesc";//说明  String
	public static final String ctgCount = "ctgCount";// String

}
