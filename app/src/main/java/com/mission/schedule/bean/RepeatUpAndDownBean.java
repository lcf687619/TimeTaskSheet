package com.mission.schedule.bean;

import java.io.Serializable;

public class RepeatUpAndDownBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String cTags;
	public  String repId;              //重复记事ID
	public  String repBeforeTime ;      //提前时间
	public  String repColorType ;       //分类：1工作 | 2生活 | 3其他
	public  String repDisplayTime;     //显示时间：0不显示 | 1显示
	public  String repType;            //重复类型 1.每天 | 2.每周 | 3.每月 | 4.每年 | 5.工作日
	public  String repIsAlarm ;         //共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
	public  String repIsPuase ;         //暂停：0 未暂停 | 1 暂停
	public  String repIsImportant;     //重要：0 未标记重要 | 1 标记重要
	public  String repSourceType ;      //0 普通 | 1 全链接（发现）| 2 ...
	public  String repChangeState;     //0 不需要上传 | 1 新添加 | 2 已更改 | 3 已删除
	public  String repOpenState;       //公开状态
	public  String repstateone;        //上一条子记事状态：0 普通 | 1 脱钩 | 2 删除 | 3 结束
	public  String repstatetwo ;        //下一条子记事状态
	public  String recommendedUserId ; //来自某人：ID 默认为0

	/**根据重复类型不同的参数
	 * 每天
	 * 每周 - 1、2、3...7
	 * 每月 - 1、2、3...31
	 * 每年 - 01-01、01-02、01-03...12-31
	 */
	public  String repTypeParameter;
	public String repStartDate;           //重复起始日期
	public  String repNextCreatedTime;     //下一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm
	public  String repLastCreatedTime;     //上一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm 无则为 @“”
	public  String repInitialCreatedTime;  //母记事创建的时间             格式 - yyyy-mm-dd hh:mm
	public  String repContent;             //内容
	public  String repCreateTime;          //创建时间
	public  String repSourceDesc;          //链接
	public  String repSourceDescSpare;     //链接描述
	public  String repTime;                //时间
	public  String repRingDesc;            //铃声文字
	public  String repRingCode;            //铃声文件名
	public  String repUpdateTime;          //更新时间
	public  String recommendedUserName ;   //来自
	public  String repdateone;             //上一条子记事标记时间   格式 - yyyy-MM-dd HH:mm
	public  String repdatetwo ;             //下一条子记事标记时间   格式 - yyyy-MM-dd HH:mm 无则为 @""
	public Integer aType;
	public String webUrl;
	public String imgPath;
	public int repInSTable;
	public int repEndState;
	public String parReamrk;
	public int repRead;
}

