package com.mission.schedule.bean;

import java.io.Serializable;
import java.util.Date;

/***
 * 记事表
 * @author pc-2014-2
 *
 */
public class ScheduBean implements Serializable{
	
	public static final long serialVersionUID = -1570729158910219998L;

	public int cId;
	public int cUid=0;//关联用户
	public String cContent="";//记事内容
	public String cDate;//记事日期
	public String cTime="";//记事时间
	public int cIsAlarm;//共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
	public int cBeforTime;//闹钟提前时间
	public String cAlarmSound;//闹钟铃声(无后缀名)
	public String cAlarmSoundDesc;//闹钟铃声描述
	public int cDisplayAlarm;//是否显示时间(0否,1是)
	public int cPostpone;//是否顺延(0否,1是)
	public int cImportant;//是否重要(0否,1是)
	public int cColorType;//颜色值(1,2,3,4,...)
	public int cIsEnd;//结束状态(0否,1是)
	public String cCreateTime;//创建时间
	public String cTags;//分类标签
	public int cType;//记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)
	public String cTypeDesc;//当记事类别为1时所带的url链接
	public String cTypeSpare;//当记事类别为1时所带的url链接描述
	public int cRepeatId;//引用的重复记事ID
	public String cRepeatDate;//重复记事的原始日期
	public String cUpdateTime;//修改时间
	public int cOpenState;//公开状态(0否,1是,2仅好友可见)
	public String cBackupTime;//备份时间
	public String cLightAppID;//轻应用与记事绑定的唯一ID
	public int cStoreParentId;//收藏的父级数据ID
	public int cSchRepeatLink;//是否与重複記事關聯(0否,1是)
	public String cRecommendName;//好友昵称
	public String calendaReamrk;
	public int schRead;
	public int aType;
	public int attentionid;
	public String webUrl;
	public String imgPath;
	public int cRecommendId;
}
