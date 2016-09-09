package com.mission.schedule.tools;

import java.io.IOException;

/**
 * 运行命令管理器 等同于在PC端adb shell之后执行命令，pm clear packageName 但是只能回自己操作，不能清除其他应用的数据；
 * 卸载其他应用(限root设备或者厂商提供接口)
 * 
 * @author hulk
 * 
 */
public class RuntimeCmdManager {

	private static final String TAG = "RuntimeCmdManager";

	/**
	 * 清除应用缓存的用户数据，同时停止所有服务和Alarm定时task String cmd = "pm clear " + packageName;
	 * String cmd = "pm clear " + packageName + " HERE";
	 * Runtime.getRuntime().exec(cmd)
	 * 
	 * @param packageName
	 * @return
	 */
	public static Process clearAppUserData(String packageName) {
		Process p = execRuntimeProcess("pm clear " + packageName);
		if (p == null) {
//			Logger.d("Clear app data packageName:" + packageName + ", FAILED !");
		} else {
//			Logger.d("Clear app data packageName:" + packageName
//					+ ", SUCCESS !");
		}
		return p;
	}

	/**
	 * 卸载应用，只能对root设备有效 String cmd = "pm uninstall " + packageName;
	 * Runtime.getRuntime().exec("pm uninstall " + packageName)
	 * 
	 * @param packageName
	 * @return
	 */
	public static Process uninstallApp(String packageName) {
		Process p = execRuntimeProcess("pm uninstall " + packageName);
		if (p == null) {
//			Logger.d("Uninstall app packageName:" + packageName + ", FAILED !");
		} else {
//			Logger.d("Uninstall app packageName:" + packageName + ", SUCCESS !");
		}
		return p;
	}

	public static Process execRuntimeProcess(String commond) {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(commond);
		} catch (IOException e) {
//			Logger.d("exec Runtime commond:" + commond + ", IOException" + e);
			e.printStackTrace();
		}
//		Logger.d("exec Runtime commond:" + commond + ", Process:" + p);
		return p;
	}
}
