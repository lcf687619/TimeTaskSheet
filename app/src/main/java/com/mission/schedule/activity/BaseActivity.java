package com.mission.schedule.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.db.DBSourse;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseActivity extends FragmentActivity {

	protected Dialog dialog;
	/** 上下文 */
	protected Context mContext;
	/** 网络工具 */
	protected NetUtil mNetUtils;

	protected InputMethodManager IMM;

	/** 屏幕的宽度 */
	protected int mScreenWidth;
	/** 屏幕的高度 */
	protected int mScreenHeight;
	SharedPrefUtil sp;
	int heigh = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		sp = new SharedPrefUtil(this, ShareFile.USERFILE);
		setContentView();
		
		initCommonData();
		findViewsByResId();

		init(savedInstanceState);
		setAdapter();
		setListener();
	}

	/**
	 * 设置各种监听事件<br>
	 * (setContentView --> init --> setAdapter --> setListener)
	 */
	protected abstract void setListener();

	private void initCommonData() {
		mContext = this;
		mNetUtils = new NetUtil();
		IMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		heigh = Utils.dipTopx(mContext,
				(Utils.pxTodip(mContext, mScreenHeight) / 11) * 10);
		// mTimer = new Timer();
	}

	/**
	 * 利用注解反射实例化View，省去手动findViewById。
	 */
	private void findViewsByResId() {
		try {
			final Method method = getClass().getMethod("findViewById",
					int.class);
			method.setAccessible(true);// 类中的成员变量为private,故必须进行此操作
			final Field[] fields = getClass().getDeclaredFields();

			int size = fields.length;
			for (int i = 0; i < size; i++) {
				final Field f = fields[i];
				f.setAccessible(true);
				if (!f.isAnnotationPresent(ViewResId.class))
					continue;
				ViewResId viewResId = f.getAnnotation(ViewResId.class);
				final int resId = viewResId.id();
				final Object view = method.invoke(this, resId);
				f.set(this, view);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置Activity的布局<br>
	 * (setContentView --> init --> setAdapter --> setListener)
	 */
	protected abstract void setContentView();

	/**
	 * 各种初始化操作<br>
	 * (setContentView --> init --> setAdapter --> setListener) <br>
	 * <strong>Note: </strong>这个时候标注<strong>{link
	 * @ViewResId}</strong>注解的View已经完成实例化。
	 * 
	 * @param savedInstanceState
	 */
	protected abstract void init(Bundle savedInstanceState);

	/**
	 * 设置各种适配器<br>
	 * (setContentView --> init --> setAdapter --> setListener)
	 */
	protected abstract void setAdapter();

	// ====================读写私有文件==========================

	/**
	 * 
	 * @param key
	 * @param defaultVal
	 *            默认值
	 * @return
	 */
	public String getPrivateXml(String key, String defaultVal) {
		try {
			return sp.getString(key, defaultVal);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 根据键名在xml文件读取相应值
	 * 
	 * @param key
	 * @param defaultVal
	 *            默认值
	 * @return
	 */
	public int getPrivateXml(String key, int defaultVal) {
		try {
			return sp.getInt(key, defaultVal);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 退出登录-并标记为未登录状态
	 * 
	 * @return
	 */
	protected boolean loginOutUser() {
		try {
			sp.putString(ShareFile.USERSTATE, "0");// 登录状态(0未登录,1登录)
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取当前登录用户的状态(0未登录,1已登录)
	 * 
	 * @return
	 */
	public String getUserState() {
		try {
			return getPrivateXml(ShareFile.USERSTATE, "0");
		} catch (Exception e) {
			return "0";
		}
	}

//	public void createDialog(String msg) {
//		View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout,
//				null);
//
//		LinearLayout dialogLL = (LinearLayout) view;
//		TextView tv_msg = (TextView) dialogLL.findViewById(R.id.tv_msg);
//		tv_msg.setText(msg);
//		dialog = new Dialog(this, R.style.dialog_mass);
//		Window win = dialog.getWindow();// 获取所在window
//		android.view.WindowManager.LayoutParams params = win.getAttributes();// 获取LayoutParams
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		win.setAttributes(params);// 设置生效
//		dialog.setContentView(dialogLL);
//		dialog.show();
//	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != dialog)
			dialog.dismiss();
	}

	/**
	 * 创建数据库文件
	 * 
	 * @param fileName
	 * @return
	 */
	protected boolean createFile(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				return true;
			}
			if (!file.getParentFile().exists()) {
				System.out.println("目标文件所在路径不存在，准备创建。。。");
				if (file.getParentFile().mkdirs()) {
					System.out.println("创建目录文件所在的目录成功！");
				} else {
					System.out.println("创建目录文件所在的目录失败！");
					return false;
				}
			}
			if (file.createNewFile()) {
				System.out.println("创建文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("创建文件" + fileName + "失败！");
				return false;
			}
		} catch (Exception e) {
			System.out.println("创建文件异常" + fileName + "失败！");
			return false;
		}
	}

	/**
	 * 复制手机数据库到sdcard
	 * 
	 * @return
	 */
	protected boolean copyDb() {
		boolean bo = true;
		FileOutputStream fos;
		FileInputStream is;
		try {
			String fileName = "/sdcard/LocalSchedule/db";
			String str = "/data/data/com.lcf.timetasksheet/databases/"
					+ DBSourse.dataBaseName;
			is = new FileInputStream(str);
			if (createFile(fileName)) {
				fos = new FileOutputStream(fileName);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) != -1) {
					fos.write(buffer, 0, count);
				}
				fos.flush();
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			bo = false;
		}
		return bo;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
