package com.mission.schedule.bean;

import java.io.Serializable;

public class MySchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String schID;// ID
	public String schContent;// 内容
	public String schDate;// 日期
	public String schTime;// 时间
	public String schIsAlarm;// 是否有提醒 默认0无1有
	public String schBeforeTime;// 提前时间 0
	public String schDisplayTime;// 是否显示时间 0
	public String schIsPostpone;// 是否顺延 0
	public String schIsImportant;// 是否重要 0
	public String schColorType;// 颜色分类 0
	public String schIsEnd;// 是否结束 0
	public String schCreateTime;// 创建时间
	public String schTags;// 所在分类 ""
	public String schSourceType;// //0 普通 | 1 全链接（发现）| 2 ...
	public String schSourceDesc;// 来源描述(链接)
	public String schSourceDescSpare;// 来源备用(链接描述)
	public String schRepeatID;// 重复ID
	public String schRepeatDate;// 重复时间
	public String schUpdateTime;// 同步更新时间
	public String schUpdateState;// 同步更新状态 0不上传 1 新建 2修改 3删除
	public String schOpenState;// 打开状态
	public String schRepeatLink;// 是否与母计时脱钩 0 1关联，2是删除，3是结束
	public String schRingDesc;// 铃声描述
	public String schRingCode;// 铃声编码
	public String schcRecommendName;// 昵称
	public String schRead;// 0已读,1未读
	public Integer aType;
	public Integer attentionid;
	public String webUrl;
	public String imgPath;

}
