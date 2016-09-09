package com.mission.schedule.entity;

public class ScheduleTable {

	public static final String ScheduleTable = "ScheduleTable";// 表名
	public static final String ID = "ID";// ID
	public static final String schID = "schID";// ID
	public static final String schContent = "schContent";// 内容
	public static final String schDate = "schDate";// 日期
	public static final String schTime = "schTime";// 时间
	public static final String schIsAlarm = "schIsAlarm";// 是否有提醒 默认0无1有
	public static final String schBeforeTime = "schBeforeTime";// 提前时间 0
	public static final String schDisplayTime = "schDisplayTime";// 是否显示时间 0
	public static final String schIsPostpone = "schIsPostpone";// 是否顺延 0
	public static final String schIsImportant = "schIsImportant";// 是否重要 0
	public static final String schColorType = "schColorType";// 颜色分类 0
	public static final String schIsEnd = "schIsEnd";// 是否结束 0  2删除 3修改
	public static final String schCreateTime = "schCreateTime";// 创建时间
	public static final String schTags = "schTags";// 所在分类 ""
	public static final String schSourceType = "schSourceType";// //0 普通 | 1 全链接（发现）| 2 ...
	public static final String schSourceDesc = "schSourceDesc";// 来源描述(链接)
	public static final String schSourceDescSpare = "schSourceDescSpare";// 来源备用(链接描述)
	public static final String schRepeatID = "schRepeatID";// 重复ID
	public static final String schRepeatDate = "schRepeatDate";// 重复时间
	public static final String schUpdateTime = "schUpdateTime";// 同步更新时间
	public static final String schUpdateState = "schUpdateState";// 同步更新状态  0不上传 1 新建 2修改 3删除
	public static final String schOpenState = "schOpenState";// 打开状态
	public static final String schRepeatLink = "schRepeatLink";//是否与母计时脱钩 0 1关联，2是删除，3是结束
	public static final String schRingDesc = "schRingDesc";//铃声描述
	public static final String schRingCode = "schRingCode";//铃声编码
	public static final String schcRecommendName = "schcRecommendName";//昵称
	public static final String schRead = "schRead";//0已读,1未读
	public static final String schAID = "schAID";//int
	public static final String schAType = "schAType";         //int附加信息类型 0 没有附加信息 | 1 附加链接| 2 附加图片 | 3 附加链接和图片
	public static final String schWebURL = "schWebURL";         //附加链接
	public static final String schImagePath = "schImagePath";      //附加图片地址
	public static final String schFocusState = "schFocusState"; //0未进行任何修改的  1进行过编辑修改的
	public static final String schFriendID = "schFriendID";//对应关注好友id
	public static final String schcRecommendId = "schcRecommendId";
}
