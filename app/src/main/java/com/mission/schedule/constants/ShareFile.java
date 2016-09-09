package com.mission.schedule.constants;

import android.os.Environment;

public class ShareFile {
	
	public static final String HOST="host";

	/**
	 * 图片保存路径
	 */
	public static final String TEMPORARYFILE =Environment.getExternalStorageDirectory()+ "/temporaryfileServer/";
	
	
	/**
	 * 用户文件
	 */
	public static final String USERFILE="userFile";
	/**
	 * 用户名
	 */
	public static final String USERNAME="userName";
	/**
	 * 用户密码
	 */
	public static final String USERPWD="userPwd";
	/**
	 * 用户登录状态
	 */
	public static final String USERSTATE = "userstate";
	/**
	 * 用户ID
	 */
	public static final String USERID="userId";
	/**
	 * 用户手机号
	 */
	public static final String TELEPHONE = "telephone";
	/**
	 * 用户头像地址
	 */
	public static final String USERPHOTOPATH = "userPhotoPath";
	/**
	 * 用户邮箱
	 */
	public static final String USEREMAIL="userEmail";
	/**
	 * 个人签名
	 */
	public static final String PERSONREMARK = "personRemark";
	/**
	 * 用户背景地址
	 */
	public static final String USERBACKGROUNDPATH="userBackgroundPath";
	/**
	 * 旧的用户ID
	 */
	public static final String OLDUSERID = "olduserId";
	/**
	 * 上次更新时间
	 */
	public static final String OLDUPDATETIME="oldUpdateTime";
	
	/**
	 * 极光推送
	 */
	public static final String PUSH_ALIAS = "Push_alias";
//	/**
//	 * 有效时间
//	 */
//	public static final String USERVALIDTIME="userValidTime";
//	
//	/**
//	 * 登录状态
//	 */
//	public static final String LOGIN_STATE="loginState";
//	/**
//	 * 是否选中记住密码
//	 */
//	public static final String ISCHECKED="isChecked";
//	public static final String ISLOGINSELF="isLoginSelf";
	/**
	 * 存储同步自增数据
	 */
	public static final String INDEX = "index";
	/**
	 * 
	 */
	public static final String COUNT = "count";
	
	/**
	 * 选择图片的路径
	 */
	public static final String LOCALPATH = "localpath";
	/**
	 * 是否显示待办中结束的  0 不显示  1显示
	 */
	public static final String UNTASKEND = "unTaskEnd";
	/**
	 * 展开一周后记录
	 */
	public static final String OUTWEEKFAG = "outWeekfag";
	
	/**
	 * 第一次登录进行数据加载同步
	 */
	public static final String FIRSTLOGIN = "firstLogin";
	/**
	 * 默认铃声
	 */
	public static final String MUSICDESC = "musicDesc";
	/**
	 * 默认铃声编码
	 */
	public static final String MUSICCODE = "musicCode";
	/**
	 * 默认提前提醒时间
	 */
	public static final String BEFORETIME = "beforeTime";
	/**
	 * 每天问候时间，早上
	 */
	public static final String MORNINGTIME = "morningTime";
	public static final String MORNINGSTATE = "morningState";
	/**
	 * 每天问候时间，下午
	 */
	public static final String NIGHTTIME = "nightTime";
	public static final String NIGHTSTATE = "nightState";
	/**
	 * 全天提醒时间
	 */
	public static final String ALLTIME = "allTime";
	/**
	 * 全天响铃 0响铃 1不响铃
	 */
	public static final String ALLSTATE = "allState";
	/**
	 * 设置更新时间
	 */
	public static final String UPDATESETTIME = "updateSetTime";
	/**
	 * 设置的主ID
	 */
	public static final String SETID = "setId";
	/**
	 * 重复选择是否进入日程
	 */
	public static final String REPSELECTSTATE = "repSelectState";
	
	/**
	 * 每天打开应用的时间
	 */
	public static final String EVERYDAY = "everyDay";
	
	/**
	 * 内部号
	 */
	public static final String U_ACC_NO = "U_ACC_NO";
	
	/**
	 * 关注最大ID
	 */
	public static final String MAXFOCUSID = "maxFocusId";
	
	/**
	 * 用户最后登录时间
	 */
	public static final String ENDLOGINDATE = "endLoginDate";
	/**
	 * 日程下载时间
	 */
	public static final String DOWNSCHTIME = "downSchTime";
	/**
	 * 重复下载时间
	 */
	public static final String DOWNREPTIME = "downRepTime";
	/**
	 * 头像存储位置
	 */
	public static final String TOUXIANGPATH = "touXiangPath";
	/**
	 * 头像是否上传成功状态 0成功 1失败
	 */
	public static final String TOUXIANGSTATE = "touXiangState";
	/**
	 * 铃声提醒模式 0语音 1静音  2短音
	 */
	public static final String RINGSTATE = "ringState";
	
	/**
	 * 判断是否为游客登录 1为正常 2为游客
	 */
	public static final String ISYOUKE = "isYouKe";
	/**
	 * 设置结束音效 0为开启1为关闭
	 */
	public static final String ENDSOUNDSTATE = "endSoundState";
	/**
	 * 设置结束震动 0为开启 1为关闭 
	 */
	public static final String ENDWAKESTATE = "endWakeState";
	
	/**
	 * 记录是否进行过更新  0否  1是
	 */
	public static final String UPDATESTATE = "updateState";
	/**
	 * 刷新时间
	 */
	public static final String UPDATETIME = "updateTime";
	/**
	 * 结束更新时间
	 */
	public static final String ENDUPDATETIME = "endUpdateTime";
	/**
	 * 启动程序在oncreate 为1  
	 */
//	public static final String MAINONCREATE = "mainOnCreate";
	/**
	 * 新标签下载时间
	 */
	public static final String DOWNTAGDATE = "downTagDate";
	/**
	 * 签到下载时间
	 */
	public static final String QIANDAODOWNDATE = "QianDaoDownDate";
	/**
	 * 签到图片下载时间
	 */
	public static final String QIANDAODOWNIMGDATE = "QianDaoDownImgDate";
	/**
	 * 签到日期
	 */
	public static final String QIANDAODATE = "QianDaoDate";
	/**
	 * 最小化进行同步  0正常进入，1最小化后进入
	 */
	public static final String OPENSTYLESTATE = "openStyleState";
	/**
	 * 好友日程,重复，以前下载时间
	 */
	public static final String FRIENDDOWNSCHTIME = "friendDownSchTime";
	public static final String FRIENDDOWNRepTIME = "friendDownRepTime";
	public static final String FRIENDDOWNOldTIME = "friendDownOldTime";
	/**
	 * 在好友界面点击home键，进来后刷新好友列表  
	 */
	public static final String REFRESHFRIEND = "refreshFriend";
	/**
	 * 好友刷新时间
	 */
	public static final String FRIENDUPDATETIME = "friendUpdateTime";
	
	/**
	 * 是否弹出遮盖页面
	 */
	public static final String ISSHOWFIRSTDIALOG = "isShowFirstDialog";
	/**
	 * 设置通知栏是否显示 0显示 1不显示
	 */
	public static final String ISNOTIFICATION = "isNotification";
	/**
	 * 第一次进入是否显示对话框 0显示  1不显示
	 */
	public static final String ISSHOWDIALOG = "isShowDialog";
	/**
	 * 第一次进入是否显示进度条 0不显示  1显示
	 */
	public static final String ISSHOWPROGRESS = "isShowProgress";
	/**
	 * 第一次订阅下载日程时间
	 */
	public static final String FIRSTDOWNFOCUSSCH = "firstDownFocusSch";
	/**
	 * 收藏列表数据缓存
	 */
	public static final String SHOUCANGDATA = "shouCangData";
	/**
	 * 分享列表数据缓存
	 */
	public static final String SHAREDATA = "shareData";
	/**
	 * 新版发现分享日程下载
	 */
	public static final String DOWNNEWFOCUSSHARESCHDATE = "downNewFocusShareSchDate";
	/**
	 * 新版发现分享重复下载
	 */
	public static final String DOWNNEWFOCUSSHAREREPDATE = "downNewFocusShareRepDate";
	/**
	 * 新版分享刷新同步时间
	 */
	public static final String FOCUSUPDATETIME = "focusUpdateTime";
	/**
	* 用户的快捷搜索本地缓存内容
	*/
	public static final String KuaiJieSouSuo = "KuaiJieSouSuo";
	/**
	* 用户的发现初始化
	*/
	public static final String NewMyFoundFenXiangFirst = "NewMyFoundFenXiangFirst";
	/**
	 * 用户权限设置 0提示  1 不提示
	 */
	public static final String PERMISSIONSTATE = "permissionState";

}
