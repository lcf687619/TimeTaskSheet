package com.mission.schedule.db;

import java.io.File;

import android.util.Log;

public class DBSourse {
	/**
	 * 数据库名
	 */
	public static final String dataBaseName = "data";

	/**
	 * 创建数据库文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean createFile(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				return true;
			}
			if (!file.getParentFile().exists()) {
				Log.d("TAG", "目标文件所在路径不存在，准备创建。。。");
				if (file.getParentFile().mkdirs()) {
					Log.d("TAG","创建目录文件所在的目录成功！");
				} else {
					Log.d("TAG","创建目录文件所在的目录失败！");
					return false;
				}
			}
			if (file.createNewFile()) {
				Log.d("TAG","创建文件" + fileName + "成功！");
				return true;
			} else {
				Log.d("TAG","创建文件" + fileName + "失败！");
				return false;
			}
		} catch (Exception e) {
			Log.d("TAG","创建文件异常" + fileName + "失败！");
			return false;
		}
	}
}