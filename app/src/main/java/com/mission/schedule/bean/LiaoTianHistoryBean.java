package com.mission.schedule.bean;

import java.io.Serializable;

public class LiaoTianHistoryBean implements Serializable{
	private static final long serialVersionUID = 1L;
	public String cAlarmSound;//":铃声,
	public String cAlarmSoundDesc;//":铃声描述,
	public int cBeforTime;//":提前时间,
	public int cIsAlarm;//":'共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟,
	public String cLightAppId;//":'轻应用与记事绑定的唯一ID',,
	public int cOpenstate;//":'公开状态(0否,1是,2仅好友可见)',
	public int cPostpone;//":'是否顺延(0否,1是)',,"
	public String cRecommendName;//":昵称,
	public String cTags;//":'分类标签',
	public int cType;//":'记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
	public String cTypeDesc;//": '当记事类别为1时所带的url链接',
	public String cTypeSpare;//":'当记事类别为1时所带的url链接描述',
	public String cdate;//":"2015-05-29",
	public int cpId;//":好友ID,"
	public String cretetime;//":"创建日期","
	public String ctime;//":"时间 HH:mm","
	public int id;//":消息ID,"
	public String messge;//":消息",
	public int repType;//":'记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
	public String repTypeParameter;//":'重复记事规则[]',
	public String repcolortype;//":记事颜色类别,
	public int repdisplaytime;//":是否显示时间(0否1是),
	public String repinitialcreatedtime;//":初始化创建时间,
	public String replastcreatedtime;//":之前最后一次生成时间,
	public String repnextcreatedtime;//":下一次生成时间,
	public String repstartdate;//":下一次重复闹钟起始时间,
	public int status;//":0 普通 1提醒 2重复
	public int uid;//":用户ID
	public int aType;
	public String imgPath;
	public String webUrl;
	public int repInSTable;
}
