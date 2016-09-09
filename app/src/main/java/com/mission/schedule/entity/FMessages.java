package com.mission.schedule.entity;

public class FMessages {
	
	public static final String FMessages = "FMessages";//表名
	public static final String fmID = "fmID";              //消息ID
	public static final String fmSendID = "fmSendID";          //发送者ID
	public static final String fmGetID = "fmGetID";           //接受者ID
	public static final String fmIsAlarm = "fmIsAlarm";         //是否响铃：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
	public static final String fmOpenState = "fmOpenState";       //公开类型；0 私密 | 1 公开 | 2 仅好友可见
	public static final String fmPostpone = "fmPostpone";        //自动顺延：0 无顺延 | 1 顺延
	public static final String fmColorType = "fmColorType";       //分类：   1 工作 | 2生活 | 3其他
	public static final String fmDisplayTime = "fmDisplayTime";     //显示时间：0 不显示 | 1显示
	public static final String fmBeforeTime = "fmBeforeTime";      //提前时间
	public static final String fmSourceType = "fmSourceType";      //链接类型：0 普通 | 1 全链接（发现）| 2 ...
	public static final String fmType = "fmType";            //重复类型：1.每天 | 2.每周 | 3.每月 | 4.每年 | 5.工作日
	public static final String fmStatus = "fmStatus";          //消息类型：0.普通消息 | 1.提醒消息 | 2.重复提醒消息

	/**根据重复类型不同的参数
	 * 每天
	 * 每周 - 1、2、3...7
	 * 每月 - 1、2、3...31
	 * 每年 - 01-01、01-02、01-03...12-31
	 */
	public static final String fmParameter = "fmParameter";         
	public static final String fmContent = "fmContent";           //消息类容
	public static final String fmCreateTime = "fmCreateTime";        //消息创建时间
	public static final String fmDate = "fmDate";              //记事日期
	public static final String fmTime = "fmTime";              //记事时间
	public static final String fmSourceDesc = "fmSourceDesc";        //链接
	public static final String fmSourceDescSpare = "fmSourceDescSpare";   //链接描述
	public static final String fmTags = "fmTags";              //分类标记
	public static final String fmRingDesc = "fmRingDesc";          //铃声中文名
	public static final String fmRingCode = "fmRingCode";          //铃声文件名
	public static final String fmStartDate = "fmStartDate";         //重复起始日期
	public static final String fmInitialCreatedTime = "fmInitialCreatedTime";//母记事创建的时间             格式 - yyyy-mm-dd hh:mm
	public static final String fmLastCreatedTime = "fmLastCreatedTime";   //上一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm 无则为 @“”
	public static final String fmNextCreatedTime = "fmNextCreatedTime";   //下一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm
	public static final String fmAType = "fmAType";         //int附加信息类型 0 没有附加信息 | 1 附加链接| 2 附加图片 | 3 附加链接和图片
	public static final String fmWebURL = "fmWebURL";         //附加链接
	public static final String fmImagePath = "fmImagePath";      //附加图片地址
	public static final String fmInSTable = "fmInSTable";
}
