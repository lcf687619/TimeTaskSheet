package com.mission.schedule.constants;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class URLConstants {// 192.168.1.100:8004
	public static List<Activity> activityList = new ArrayList<Activity>();

	public static void doEdit() {
		for (Activity list : activityList) {
			list.finish();
		}
	}
	//192.168.2.16:8080
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
	public static final String APP_ID = "wx5add723149fa57f5";// 时间表(张的账号)
	// 192.168.2.22:8080 121.40.19.103  http://121.40.19.103:8099
	public static final String HTTP = "http://121.40.19.103:8088";
	public static final String HOST = "http://121.40.19.103/timetable";//121.40.19.103
	public static final String 图片 = HOST +"/TbDU_showImage.htm?imageName=";
	public static final String 默认背景图片 = HOST
			+ "/ImageTest_getImage.htm?U_BACKGROUND_IMAGE";
	public static final String 背景图片 = HOST
			+ "/ImageTest_getImage.htm?U_BACKGROUND_IMAGE";
	public static final String 修改背景图片 = HOST + "/ImageTest_uploadImage.htm";
	public static final String 注册 = HOST + "/user_regis.do";// ?name=dhh&password=123456&tel=15835801463";
	public static final String 登录 = HOST + "/user_loginUser.do";
	public static final String 查询个人信息 = HOST + "/user_getUserMes.do";
	public static final String 同步数据接口 = HOST
			+ "/schedule_SynchronousDataManipulation.htm";
	public static final String 数据下载同步 = HOST + "/schedule_getAllTbCalendar.htm";
	public static final String 重复数据上传 = HOST + "/repeat_synUpRep.htm";
	public static final String 重复数据下载 = HOST + "/repeat_synDownloadData.htm";
	public static final String 好友列表查询 = HOST
			+ "/appFrends_getTbUserFrendsAll.do";
	public static final String 搜索添加好友 = HOST
			+ "/appFrends_getTbUserFrendsByName.do";
	public static final String 添加好友申请 = HOST + "/appFrends_addTbuserFreads.do";
	public static final String 好友申请和被申请 = HOST
			+ "/appFrends_getAdddFrendsMess.do";
	public static final String 确认好友申请 = HOST
			+ "/appFrends_upTbuserFreadsStatus.do";
	public static final String 忽略好友申请 = HOST
			+ "/appFrends_upTbFreandsStatus.do";
	public static final String 删除申请好友 = HOST
			+ "/appFrends_delTbuserFreadsByUid.do";
	public static final String 查询聊天记录 = HOST
			+ "/appFrends_getFrendsChatMess.do";
	public static final String 添加聊天信息 = HOST
			+ "/appFrends_addUserFrendsMess.do";
	public static final String 好友日程 = HOST
			+ "/schedule_getFriendCalendarsNoverdue.htm";
	public static final String 好友重复日程 = HOST
			+ "/repeat_getFriendTimepreinstalls.htm";
	public static final String 好友以前日程 = HOST + "/before_getAllCalendars.htm";
	public static final String 添加关注 = HOST + "/attention_addAttentionMess.do";
	public static final String 删除关注 = HOST
			+ "/attention_deleteAttentionMess.do";
	public static final String 修改关注状态 = HOST
			+ "/attention_upAttentionState.do?uid=";
	public static final String 全部好友日程 = HOST
			+ "/schedule_getAllFriendCalendarsNoverdue.htm";
	public static final String 关注的好友 = HOST
			+ "/attention_queryAttentionMess.do";
	public static final String 查询推荐关注信息 = HOST
			+ "/attention_getSperAttentionMess.do";
	public static final String 搜索关注信息 = HOST + "/attention_serchUser.do";
	public static final String 关注所有日程 = HOST
			+ "/attention_findAllAttentionCalendarMess.do";
	public static final String 修改个人信息 = HOST + "/user_updateUserMess.do";
	public static final String 修改个人头像 = HOST + "/user_uploadTitleImg.do";
	public static final String 修改手机号 = HOST + "/user_updateMobile.do?uMobile=";
	/************************************* 重复接口 ******************************************/
	public static final String 重复推荐 = HOST
			+ "/attention_findSperdMess.do?pushType=0";

//	public static final String 修改用户极光注册码 = HOST + "/user_updateMac.do";
	public static final String 统计好友操作数量 = HOST + "/appFrends_tjHYCount.do";
	public static final String 找回密码 = HOST + "/user_mailDelivery.htm";
	public static final String 新找回密码 = HOST + "/user_newMailDelivery.htm?uEmail=";

	public static final String 好友关注重复生成 = HOST
			+ "/appFrends_createTbCaldar.do?uId=";

	// 查询广告信息
	public static final String 查询广告 = HOST + "/user_queryADS.do";
	public static final String 展示广告图片 = HOST
			+ "/user_showADSImage.do?adsImageName=";

	// 分享日程加载webview
	public static final String 分享日程 = HOST + "/TbCA_mytbu.htm?uId=";
	public static final String 重复到期日管理 = HOST + "/mytb/CountdownList.html?uid=";
	public static final String 新建到期日 = HOST + "/mytb/addCountdown.html?uid=";
	public static final String 下载农历 = HOST
			+ "/holiday_getAllLunarCalendaTime.htm";
	public static final String 更新农历 = HOST + "/holiday_contrastMaxTime.htm?maxTime=";

	public static final String 积分兑换 = HOST + "/score/Sudoku.html?uid=";
	public static final String 积分规则 = HOST + "/score/SysScore.html";

	public static final String 帮助 = HOST + "/score/HelpList.html";
	public static final String 申请加V = HOST
			+ "/score/VAuthentication.html?userID=";

	public static final String 积分计算 = HOST
			+ "/integral_scoreEdit.do?num=10002&type=0&uid=";
	public static final String 修改MAC地址 = HOST + "/user_updateMac.do?uid=";

	public static final String 刷新下载关注数据 = HOST
			+ "/attention_refreshAttentionCalend.htm?cUid=";// attentionId
	
	public static final String  查询设置信息 = HOST + "/user_findTbUserMannge.do?uid=";
	public static final String 添加用户设置 = HOST + "/user_addTbUserMannge.do";
	public static final String 修改用户设置 = HOST + "/user_updateTbUserMannge.do";
	
	public static final String 重要通知 = HOST + "/user_findSysMessOpen.do";
	public static final String 下载关注数据 = HOST + "/attention_downloadAttentionCalend.htm?cUid=";

	public static final String 更新用户最后登录时间 = HOST + "/user_addStatisticalRecord.htm?uid=";
	
	public static final String 老用户数据查询 = HTTP + "/DataBase/syn_getUserMess.do?val=";
	public static final String 老用户信息迁移 = HTTP + "/DataBase/syn_synUpOldUser.do?email=";
	
	public static final String 新版好友同步 = HOST + "/postCalenda_synFrendUp.do";
	public static final String 新版好友查询日程和以前信息 = HOST + "/postCalenda_queryTbUserFrendsPostCalendar.do?uid=";
	public static final String 新版好友查询重复信息 = HOST + "/postCalenda_queryTbUserFrendsPostRepCalendar.do?uid=";
	public static final String 标签下载 = HOST + "/attentionNew_queryTbTagByUserId.do?uid=";
	public static final String 标签同步 = HOST + "/attentionNew_synTbTag.do";
	
	public static final String 签到下行  = HOST + "/iga_mobileDownSignActivityMess.htm?downTime=";
	public static final String 签到图片下载 = HOST + "/iga_mobileDownActivityImg.htm";//imgType
	public static final String 签到 = HOST + "/iga_addSignActivityOpdata.htm?tp.uid=";// tp.signDate tp.signIntegral tp.remark
	
	public static final String 发现手机端添加统计数量 = HOST + "/attentionNew_addAttentionOpdata.do?uid=";

	public static final String 新版发现热门推荐更多 = HOST + "/attentionNew_addAttentionOpdata.do?uid=";
	public static final String 新版发现查询广告 = HOST + "/user_queryADS.do?type=1";
	public static final String 新版发现热门推荐 = HOST + "/attentionUser_querySperadAttentionSpacePhone.do?pageNum=1000&nowPage=1";
	public static final String 新版发现热门推荐点击增加点击数量 = HOST + "/attentionUser_updateClickCount.do";
	
	public static final String 新版发现获取今后日程 = HOST + "/find_getUserShareCalendarMobile.htm";
	public static final String 新版发现获取以前日程 = HOST + "/find_getUserBeforeShareCalendarMobile.htm";
	public static final String 新版发现获取全部日程 = HOST + "/find_getUserAllCalendar.htm";
	
	public static final String 新版发现生成重复子记事 = HOST + "/attentionNew_createRep.htm";
	
	public static final String 新版发现我的收藏 = HOST + "/attentionUser_queryAttentionUserSpaceUser.do";
	public static final String 新版发现收藏修改订阅 = HOST + "/attentionUser_updateAttentionSpaceUser.htm";
	public static final String 新版发现收藏下行数据到日程 = HOST + "/dis_AttentionSpaceUsersDownlinkData.do";
	
	public static final String 新版发现点击收藏 = HOST + "/attentionUser_addAttentionUserSpaceUser.do";
	public static final String 新版发现取消收藏 = HOST + "/attentionUser_delAttentionUserSpaceUser.do";
	public static final String 新版发现判断是否收藏 = HOST + "/attentionUser_isAttentionUserSpaceUser.do";
	
	public static final String 新版发现大事件 = HOST + "/bignews/bignew.html?uid=";
	
	public static final String 新版发现我的分享 = HOST + "/attentionUser_queryTbAttentionUserSpace.do";
	public static final String 新版发现新建分享 = HOST + "/attentionUser_addTbAttentionUserSpace.do";
	public static final String 新版发现修改分享 = HOST + "/attentionUser_updateTbAttentionUserSpace.do";
	public static final String 新版发现删除分享 = HOST + "/attentionUser_delTbAttentionUserSpaceById.do";
	public static final String 新版发现上传分享背景图或头像 = HOST + "/attentionUser_updateHeadImgOrBackgroudImg.do";
	
	public static final String 新版发现重复上传 = HOST + "/attentionNew_synUpTbAttentionUserTimepreinstall.do";
	public static final String 新版发现普通上传 = HOST + "/attentionNew_synUTbAttentionUserCalendar.do";
	public static final String 新版发现下行重复 = HOST + "/attentionNew_syndownTbAttentionUserTimepreinstall.do";
	public static final String 新版发现下行普通 = HOST + "/attentionNew_syndownTbAttentionUserCalendar.do";

	public static final String 新版发现搜索 = HOST + "/attentionNew_serchAttetnionAllMess.htm";
	public static final String 新版发现获取推荐关键字 = HOST + "/attentionNew_findAttentionRecommend.do";
	public static final String 新版发现日程图片上传 = HOST + "/attentionNew_uploadCalendarImg.htm";
	
	public static final String 新版发现搜索日程 = HOST + "/attentionNew_serchAttetnionCalendarMess.htm";
	public static final String 新版发现搜索用户 = HOST + "/attentionNew_serchAttetnionUserMess.htm";
	public static final String 新版发现初始化一个自己的空间 = HOST + "/attentionUser_addStartTbAttentionUserSpace.do";

	public static final String 短信登录获取验证码  = HOST + "/user_getYzm.do?uMobile=";
	public static final String 手机号找回密码 = HOST + "/user_dxUpdatePwd.do";
	public static final String 重复记事完成率 = HOST + "/qiandao/index.html?repId=";
}
