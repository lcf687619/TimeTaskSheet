package com.mission.schedule.entity;

public class LocateAllMemoTable {
	
	public static final String LocateAllMemoTable="LocateAllMemoTable";//表名
	public static final String ID="ID";
	public static final String orderId="orderId";//主键id
	public static final String createTime="createTime";//创建时间
	public static final String imageName="imageName";//记事图片
	public static final String noticeType="noticeType";//记事类型
	public static final String noticeContent="noticeContent";//记事内容
	public static final String tType="tType";//0普通记事,1协作记事
	public static final String toUserName="toUserName";
	public static final String parentId="parentId";
	public static final String locateUpdateState="locateUpdateState";//本地状态（0已同步，1新加，2修改，3，删除）
	public static final String calendarDetailLastUpdateTime="calendarDetailLastUpdateTime";//记事最后更新时间
	public static final String orderVauue="orderVauue";//记事在分类中的顺序 start、1--2--3  end  在分类中查询时倒序查询
	
}
