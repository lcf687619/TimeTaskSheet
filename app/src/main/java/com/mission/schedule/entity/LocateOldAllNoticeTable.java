package com.mission.schedule.entity;

public class LocateOldAllNoticeTable {
	
	public static final String LocateAllNoticeTable="LocateAllNoticeTable";//表名
	public static final String ID="ID";
	public static final String orderId="orderId";//主键id
	public static final String dataId="dataId";//无用id 与网上相同
	public static final String alarmClockTime="alarmClockTime";//闹钟时间
	public static final String alarmSound="alarmSound";//闹钟铃声
	public static final String alarmSoundDesc="alarmSoundDesc";//闹钟描述
	public static final String beforTime="beforTime";//闹钟提前时间
	public static final String calendarDetailLastUpdateTime="calendarDetailLastUpdateTime";//最后修改的时间
	public static final String cdD_value="cdD_value";//截止天数--暂时没用
	public static final String cdLastDate="cdLastDate";//截至日期
	public static final String colorType="colorType";//颜色值14为结束  2默认
	public static final String createTime="createTime";//创建时间
	public static final String displayAlarm="displayAlarm";//是否显示闹钟时间 1显示，0不显示
	public static final String displayDate="displayDate";//是否显示日期  1显示，0不显示
	public static final String fromUserImg="fromUserImg";//用户头像
	public static final String fromUserNickName="fromUserNickName";//用户名称
	public static final String imageName="imageName";//记事图片
	public static final String isNeedPush="isNeedPush";//是否推送闹钟
	public static final String noticeContent="noticeContent";//记事内容
	public static final String noticeDate="noticeDate";//记事时间
	public static final String postpone="postpone";//是否顺延   1顺延  0  不顺
	public static final String readTimes="readTimes";//真实访问量(每次+1)
	public static final String senduid="senduid";
	public static final String tags="tags";//所在分类
	public static final String totalGrade="totalGrade";//总评分值
	public static final String aFinishCount="aFinishCount";//完成次数
	public static final String allFinishCount="allFinishCount";//所有完成次数
	public static final String tType="tType";//0普通记事,1协作记事
	public static final String toUserName="toUserName";
	public static final String parentId="parentId";
	public static final String alarmResultTime="alarmResultTime";//闹钟真正执行的时间
	public static final String locateUpdateState="locateUpdateState";//本地状态（0已同步，1新加，2修改，3，删除）
	public static final String locateNoId="locateNoId";//协作主记事状态
	public static final String cdType="cdType";
	public static final String tpId="tpId";//重复记事ID
	public static final String orderVauue="orderVauue";//记事在分类中的顺序 start、1--2--3  end  在分类中查询时倒序查询
	public static final String source="source";
	public static final String noticeOriginalDate="noticeOriginalDate";//重复记事初始所在日期 2013-04-08 不改变
	public static final String d_postponeCount="d_postponeCount";//顺延次数
	public static final String d_endDate="d_endDate";//设为已结束日期 2013-02-02 22:00:00
	public static final String sourceType="sourceType";//数据类型 0---普通记事  1---其他记事（ex.信箱）
	public static final String sourceId="sourceId";//infobox的ID
	public static final String alarmType="alarmType";//闹钟分类，0 普通,1每天，2每周，3每月，4每年
	public static final String alarmId="alarmId";//闹钟Id
	public static final String noticeIsStarred="noticeIsStarred";//0不加星，1加星（重要）
	public static final String teamNoticeReadState="teamNoticeReadState";//0--已读  1--未读...
	public static final String teamNoticeLocateSign="teamNoticeLocateSign";//0--不进  1--进入时间表
	public static final String teamIsParentFinish="teamIsParentFinish";//是否是 发起人已结束 0否,1是
	
	

}
