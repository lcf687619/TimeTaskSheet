package com.mission.schedule.entity;

public class CLFindScheduleTable {
	public static final String CLFindScheduleTable = "CLFindScheduleTable";
	public static final String fstID = "fstID";//发现记事ID  int
	public static final String fstType = "fstType";//记事类型：0 普通记事 | 1 重复记事
	public static final String fstSchID = "fstSchID";//记事ID
	public static final String fstFID = "fstFID";//发现空间账号ID
	public static final String fstBeforeTime = "fstBeforeTime";//提前时间以分钟为单位
	public static final String fstIsAlarm = "fstIsAlarm";//共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
	public static final String fstDisplayTime = "fstDisplayTime";//显示时间：0不显示 | 1显示
	public static final String fstColorType = "fstColorType";//分类
	public static final String fstIsPostpone = "fstIsPostpone";//自动顺延：0不顺延 | 1自动顺延
	public static final String fstIsImportant = "fstIsImportant";//重要标记：0不标记 | 1标记为重要
	public static final String fstIsEnd = "fstIsEnd";//结束： 0 未结束 | 1 结束 | 2 删除 | 3 修改
	public static final String fstSourceType = "fstSourceType";//0 普通 | 1 全链接（发现）| 2 ...
	public static final String fstRepeatId = "fstRepeatId";//关联母记事ID
	public static final String fstOpenState = "fstOpenState";//0 所有可见 | 1 仅好友可见
	public static final String fstRepeatLink = "fstRepeatLink";//关联母记事状态 0 断关联 | 1 关联
	public static final String fstRecommendId = "fstRecommendId";//来自某人：ID 默认为0
	public static final String fstIsRead = "fstIsRead";//0 已读 | 1 未读
	public static final String fstAID = "fstAID";//关注日程ID
	public static final String fstIsPuase = "fstIsPuase";//暂停：0 未暂停 | 1 暂停
	public static final String fstRepStateOne = "fstRepStateOne";//上一条子记事状态：0 普通 | 1 脱钩 | 2 删除 | 3 结束 | 4 重要 | 5 重要并结束
	public static final String fstRepStateTwo = "fstRepStateTwo";//下一条子记事状态
	public static final String fstRepInStable = "fstRepInStable";//是否生成子日程 0 生成 | 1 不生成,default 0
	public static final String fstPostState = "fstPostState";//来自好友提醒状态 0 普通 | 1 修改子记事 | 2 删除 | 3 修改
	public static final String fstRepType = "fstRepType";//重复类型 1.每天 | 2.每周 | 3.每月 | 4.每年 | 5.工作日 | 6.农历
	public static final String fstAType = "fstAType";//附加信息类型 0 没有附加信息 | 1 附加链接| 2 附加图片 | 3 附加链接和图片
	public static final String fstUpdateState = "fstUpdateState"; //0 不需要上传 | 1 新添加 | 2 已更改 | 3 已删除
	/**根据重复类型不同的参数
	 * 每天
	 * 每周 - 1、2、3...7
	 * 每月 - 1、2、3...31
	 * 每年 - 01-01、01-02、01-03...12-31
	 * 农历 - 正月初一、正月初二...腊月三十
	 */
	public static final String fstParameter = "fstParameter";
	public static final String fstContent = "fstContent";//提醒内容
	public static final String fstDate = "fstDate";//提醒日期
	public static final String fstTime = "fstTime";//提醒时间
	public static final String fstRingCode = "fstRingCode";//铃声文件名
	public static final String fstRingDesc = "fstRingDesc";//铃声文件说明
	public static final String fstTags = "fstTags";//分类标记
	public static final String fstSourceDesc = "fstSourceDesc";//链接
	public static final String fstSourceDescSpare = "fstSourceDescSpare";//链接描述
	public static final String fstRepeatDate = "fstRepeatDate";//关联母记事生成的日期
	public static final String fstRepStartDate = "fstRepStartDate";//重复起始日期
	public static final String fstRpNextCreatedTime = "fstRpNextCreatedTime";//下一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm
	public static final String fstRepLastCreatedTime = "fstRepLastCreatedTime";//上一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm 无则为 @“”
	public static final String fstRepInitialCreatedTime = "fstRepInitialCreatedTime";//母记事创建的时间             格式 - yyyy-mm-dd hh:mm
	public static final String fstRepDateOne = "fstRepDateOne";//上一条子记事标记时间   格式 - yyyy-MM-dd HH:mm
	public static final String fstRepDateTwo = "fstRepDateTwo";//下一条子记事标记时间   格式 - yyyy-MM-dd HH:mm 无则为 @""
	public static final String fstRecommendName = "fstRecommendName";//来自
	public static final String fstWebURL = "fstWebURL";//附加链接
	public static final String fstImagePath = "fstImagePath";//附加图片
	public static final String fstParReamrk = "fstParReamrk";//农历日期:01-01、01-02、01-03...12-30
	public static final String fstCreateTime = "fstCreateTime";//记事创建时间
	public static final String fstUpdateTime = "fstUpdateTime";//记事修改时间
	public static final String fstReamrk1 = "fstReamrk1";//备注1
	public static final String fstReamrk2 = "fstReamrk2";//备注2
	public static final String fstReamrk3 = "fstReamrk3";//备注3
	public static final String fstReamrk4 = "fstReamrk4";//备注4
	public static final String fstReamrk5 = "fstReamrk5";//备注5
}
