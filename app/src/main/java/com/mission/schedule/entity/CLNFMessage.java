package com.mission.schedule.entity;

public class CLNFMessage {
	public static final String CLNFMessage = "CLNFMessage";
	public static final String nfmId = "nfmId";         //Id of message.
	public static final String nfmSendId = "nfmSendId";     //Id of sender.
	public static final String nfmGetId = "nfmGetId";
	public static final String nfmCalendarId = "nfmCalendarId"; //Calendar's Id that relation message
	public static final String nfmOpenState = "nfmOpenState";  //Open state, 0 static | 1 public | 2 firend can read.
	public static final String nfmStatus = "nfmStatus";     //Status of message, 0 common | 1 schedule | 2 repeat
	public static final String nfmIsAlarm = "nfmIsAlarm";    //是否响铃：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
	public static final String nfmPostpone = "nfmPostpone";   //自动顺延：0 无顺延 | 1 顺延
	public static final String nfmColorType = "nfmColorType";  //分类：   1 工作 | 2生活 | 3其他
	public static final String nfmDisplayTime = "nfmDisplayTime";//显示时间：0 不显示 | 1显示
	public static final String nfmBeforeTime = "nfmBeforeTime"; //提前时间
	public static final String nfmSourceType = "nfmSourceType"; //链接类型：0 普通 | 1 全链接（发现）| 2 ...
	public static final String nfmType = "nfmType";       //重复类型：1.每天 | 2.每周 | 3.每月 | 4.每年 | 5.工作日
	public static final String nfmAType = "nfmAType";      //附加信息类型 0 没有附加信息 | 1 附加链接| 2 附加图片 | 3 附加链接和图片
	public static final String nfmInSTable = "nfmInSTable";   //是否生成子日程 0 生成 | 1 不生成,default 0
	public static final String nfmIsEnd = "nfmIsEnd";      //是否完成：0.未完成 | 1.完成
	public static final String nfmDownState = "nfmDownState";  //是否下行：0.未下行 | 1.下行
	public static final String nfmPostState = "nfmPostState";  //修改类型：0.普通 | 1.撤销 | 2.完成 | 3.修改     0 未结束  1 已结束
	public static final String nfmUpdateState = "nfmUpdateState";//上传状态：0.普通 | 1.新建 | 2.修改 | 3.删除
	public static final String nfmPId = "nfmPId";        //父ID
	public static final String nfmSubState = "nfmSubState";   //子记事状态：0.普通 |  1 脱钩 | 2 删除 | 3 结束
	public static final String nfmSubEnd = "nfmSubEnd";     //对方子记事结束状态：0未结束 | 1已结束
	public static final String nfmCState = "nfmCState";     //修改对象：0 本身 | 1 子记事

	/**根据重复类型不同的参数
	 * 每天
	 * 每周 - 1、2、3...7
	 * 每月 - 1、2、3...31
	 * 每年 - 01-01、01-02、01-03...12-31
	 */
	public static final String nfmParameter = "nfmParameter";
	public static final String nfmContent = "nfmContent";           //消息内容
	public static final String nfmDate = "nfmDate";              //记事日期
	public static final String nfmTime = "nfmTime";              //记事时间
	public static final String nfmSourceDesc = "nfmSourceDesc";        //链接
	public static final String nfmSourceDescSpare = "nfmSourceDescSpare";   //链接描述
	public static final String nfmTags = "nfmTags";              //分类标记
	public static final String nfmRingDesc = "nfmRingDesc";          //铃声中文名
	public static final String nfmRingCode = "nfmRingCode";          //铃声文件名
	public static final String nfmStartDate = "nfmStartDate";         //重复起始日期
	public static final String nfmInitialCreatedTime = "nfmInitialCreatedTime";//母记事创建的时间             格式 - yyyy-mm-dd hh:mm
	public static final String nfmLastCreatedTime = "nfmLastCreatedTime";   //上一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm 无则为 @“”
	public static final String nfmNextCreatedTime = "nfmNextCreatedTime";   //下一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm
	public static final String nfmWebURL = "nfmWebURL";            //链接地址
	public static final String nfmImagePath = "nfmImagePath";         //图片地址
	public static final String nfmSendName = "nfmSendName";          //发送者昵称
	public static final String nfmSubDate = "nfmSubDate";           //子记事修改时间           格式 - yyyy-mm-dd hh:mm
	public static final String nfmRemark = "nfmRemark";            //备注
	public static final String nfmSubEndDate = "nfmSubEndDate";        //子记事结束时间
	public static final String nfmIsPuase = "nfmIsPuase";
	public static final String parReamrk = "parReamrk"; //
	public static final String nfmCreateTime = "nfmCreateTime";//创建时间
}
