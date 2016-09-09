package com.mission.schedule.entity;
/**
 * 重复记事
 * @author MR-WU
 *
 */
public class LocateRepeatNoticeTable {
	public static final String TABLE_NAME="LocateRepeatNoticeTable";
	
	public static final String ID="ID";
	public static final String key_id="key_id";//主键ID
	public static final String key_tpAlarmSound="key_tpAlarmSound";//闹钟铃声
	public static final String key_tpAlarmSoundDesc="key_tpAlarmSoundDesc";//闹钟描述内容
	public static final String key_tpBeforTime="key_tpBeforTime";//闹钟提前时间(单位:分钟)
	
	public static final String key_tpContent="key_tpContent";//记事内容
	public static final String key_tpCreateTime="key_tpCreateTime";//创建时间
	public static final String key_tpDataType="key_tpDataType";//数据类别(4每年,3每月,2每周,1每日,10重要日)
	public static final String key_tpDate="key_tpDate";//日期
	public static final String key_tpDisplayAlarm="key_tpDisplayAlarm";//是否显示时间,0不显示,1显示
	
	public static final String key_tpIsRemind="key_tpIsRemind";//是否提醒(0否,1是)
	public static final String key_tpLastDate="key_tpLastDate";
	public static final String key_tpOpenState="key_tpOpenState";//开启状态(0关闭,1开启,...)
	public static final String key_tpPostpone="key_tpPostpone";//是否可顺延(0否,1是)
	public static final String key_tpTime="key_tpTime";//时间
	
	public static final String key_tpType="key_tpType";//类别(0普通用户的个人预设数据,1普通用户对别人的预设数据,2系统)
	public static final String key_tpUpdateTime="key_tpUpdateTime";//修改时间
	public static final String key_tpUserId="key_tpUserId";//关联用户ID
	public static final String locateUpdateState="locateUpdateState";//0未更改不需要提交(默认值),1新加 ,2更改 ,3删除
	public static final String locateNoId="locateNoId";
	
	public static final String locateNextCreatTime="locateNextCreatTime";//下一个执行日期
	public static final String key_tpCurWeek="key_tpCurWeek";//周几
	public static final String key_tpDay="key_tpDay";//几号
	public static final String key_tpMonthDay="key_tpMonthDay";//日月
	public static final String key_tpLcDate="key_tpLcDate";//农历日月
	public static final String key_tpSeparateFlag="key_tpSeparateFlag";//分隔子记事（0生成子记事，1修改后的子记事）
	public static final String key_tpColorType="key_tpColorType";//母记事颜色
	public static final String key_tpLocateAlarmFromTime="key_tpLocateAlarmFromTime";//循环闹钟下一次执行开始时间
	public static final String key_tpHolidayRepeatType="key_tpHolidayRepeatType";// 0---固定每年阳历重复  1---固定每年阴历重复/以及阳历不重复的节日
	public static final String key_tpHolidayName="key_tpHolidayName";//重要节日名称--- ex.春节 当且仅当 key_tpDataType = 10（重要日）时起作用
	public static final String key_tpIsStarred="key_tpIsStarred";
	
	public static final String key_tpParentId="key_tpParentId";
	public static final String key_tpTType="key_tpTType";//1协作发起者，0接受
	public static final String key_tpMemberCount="key_tpMemberCount";//参与人数
	public static final String key_tpToUserName="key_tpToUserName";//置顶人的名字
	
	public static final String D_value="D_value";
	
	
}
