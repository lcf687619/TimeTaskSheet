package com.mission.schedule.entity;

public class CLRepeatTable {
	public static final String CLRepeatTable = "CLRepeatTable";//表名
	public static final String repID = "repID";              //重复记事ID
	public static final String repBeforeTime = "repBeforeTime";      //提前时间
	public static final String repColorType = "repColorType";       //分类：1工作 | 2生活 | 3其他
	public static final String repDisplayTime = "repDisplayTime";     //显示时间：0不显示 | 1显示
	public static final String repType = "repType";            //重复类型 1.每天 | 2.每周 | 3.每月 | 4.每年 | 5.工作日
	public static final String repIsAlarm = "repIsAlarm";         //共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
	public static final String repIsPuase = "repIsPuase";         //暂停：0 未暂停 | 1 暂停
	public static final String repIsImportant = "repIsImportant";     //重要：0 未标记重要 | 1 标记重要
	public static final String repSourceType = "repSourceType";      //0 普通 | 1 全链接（发现）| 2 ...
	public static final String repUpdateState = "repUpdateState";     //0 不需要上传 | 1 新添加 | 2 已更改 | 3 已删除
	public static final String repOpenState = "repOpenState";       //公开状态
	public static final String repStateOne = "repStateOne";        //上一条子记事状态：0 普通 | 1 脱钩 | 2 删除 | 3 结束
	public static final String repStateTwo = "repStateTwo";        //下一条子记事状态
	public static final String repcommendedUserId = "repcommendedUserId"; //来自某人：ID 默认为0

	/**根据重复类型不同的参数
	 * 每天
	 * 每周 - 1、2、3...7
	 * 每月 - 1、2、3...31
	 * 每年 - 01-01、01-02、01-03...12-31
	 */
	public static final String repTypeParameter = "repTypeParameter";
	public static final String repStartDate = "repStartDate";           //重复起始日期
	public static final String repNextCreatedTime = "repNextCreatedTime";     //下一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm
	public static final String repLastCreatedTime = "repLastCreatedTime";     //上一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm 无则为 @“”
	public static final String repInitialCreatedTime = "repInitialCreatedTime";  //母记事创建的时间             格式 - yyyy-mm-dd hh:mm
	public static final String repContent = "repContent";             //内容
	public static final String repCreateTime = "repCreateTime";          //创建时间
	public static final String repSourceDesc = "repSourceDesc";          //链接
	public static final String repSourceDescSpare = "repSourceDescSpare";     //链接描述
	public static final String repTime = "repTime";                //时间
	public static final String repRingDesc = "repRingDesc";            //铃声文字
	public static final String repRingCode = "repRingCode";            //铃声文件名
	public static final String repUpdateTime = "repUpdateTime";          //更新时间
	public static final String repcommendedUserName = "repcommendedUserName";   //来自
	public static final String repDateOne = "repDateOne";             //上一条子记事标记时间   格式 - yyyy-MM-dd HH:mm
	public static final String repDateTwo = "repDateTwo";             //下一条子记事标记时间   格式 - yyyy-MM-dd HH:mm 无则为 @""
	
	public static final String repAType = "repAType";           //附加信息类型 0 没有附加信息 | 1 附加链接| 2 附加图片 | 3 附加链接和图片
	public static final String repWebURL = "repWebURL";
	public static final String repImagePath = "repImagePath";
	public static final String repInSTable = "repInSTable";//是否生成子日程 0 生成 | 1 不生成
	public static final String repEndState = "repEndState";//标记  1修改子记事  2删除 3修改
	public static final String parReamrk = "parReamrk";
	public static final String repRead = "repRead";// 0已读  1 未读
	public static final String repPostState = "repPostState";
	
}
