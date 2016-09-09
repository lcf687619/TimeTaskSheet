package com.mission.schedule.utils;

import java.io.File;

import android.os.Environment;

public class ParameterUtil {
	/**
	 * 全局输出锁
	 */
	public static final boolean printLogFlag = false;

	/**
	 * 服务器连接地址
	 */

	public static String webServiceUrl = "http://121.40.19.103/timetable/TbDU_showImage.htm?imageName=";
	//public static String webServiceUrl = "http://192.168.2.88/jointimetable/";
	public static String webWs_userInfo = "calendarCatService.ws";
	public static String webWs_timeInfo = "CalendarWebService.ws";
	public static String webWs_remindInfo = "timePreinstallWebService.ws";
	public static String webWs_syTags = "synps.ws";

	public static String webVali = "3bb4190b257316dece09bfa65af64660";

	public static String actionUrl = "https://api.weibo.com/2/statuses/update.json";// 文字微博
	public static String actionUrl_1 = "https://upload.api.weibo.com/2/statuses/upload.json"; // 图片微博
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String MP_BOUNDARY = "--" + BOUNDARY;
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";

	// 下载apk升级
	public static final String savePath = getSDPath()+"LocalSchedule/updatedemo/";
	public static final String saveFileName = savePath + "Schedule.apk";
	
	/**
	 * 用户头像目录
	 */
	public static String userHeadImg = getSDPath()+"LocalSchedule/data/headImg/";
	/**
	 * 好友头像目录
	 */
	public static String friendHeadIma = getSDPath()+"LocalSchedule/data/myfriendImg/";
	/**
	 * 重复推荐目录 
	 */
	public static String repeatHeadImg = getSDPath()+"LocalSchedule/data/repeatImg/";
	/**
	 * 应用推荐图标目录
	 */
	public static String appRecomHeadImg = getSDPath()+"LocalSchedule/data/appRecomHeadImg/";
	/**
	 * 图片目录
	 */
	public static String tableDeskTopImg = getSDPath()+"LocalSchedule/data/tableImg/";
	/**
	 * 列表图片目录
	 */
	public static String listImg = getSDPath()+"LocalSchedule/data/listImg/";
	/**
	 * 详情页面图片
	 */
	public static String saveDetailPath = getSDPath()+"LocalSchedule/data/detailImg/";
	/**
	 * 桌面图片文件夹
	 */
	public static String saveImgPath = getSDPath()+"LocalSchedule/data/img/";
	/**
	 * 本地临时图片存放路径
	 */
	public static String saveImgTempPath = getSDPath()+"LocalSchedule/data/img/temp/";
	/**
	 * 导出图片存放路径
	 */
	public static String saveExportImgPath = getSDPath()+"LocalSchedule/data/img/exportimg/";
	
	
	public static String getSDPath(){
		String sdDirStr="/sdcard/";
		try {
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
			if (sdCardExist) {
				sdDir = Environment.getExternalStorageDirectory();//获取跟目录
				sdDirStr = sdDir.toString() + "/";
			}
		} catch (Exception e) {
			sdDirStr="/sdcard/";
		}
		return sdDirStr;
	}
}
