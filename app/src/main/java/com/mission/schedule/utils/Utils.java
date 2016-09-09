package com.mission.schedule.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 1,验证邮箱输入是否合法 dang-- 2,简单判断电话号码格式是否正确 dang -- 3,判断手机号码格式是否正确 dang-- 4,MD5加密算法
 * dang-- 5,格式化当前时间 dang-- 6,根据字体大小获取字体高度 dang-- 7,将textview中的字符全角化 dang--
 * 8,从文件读取信息 dang-- 9,比较两个时间大小 dang-- 10,日期格式化，将date类型转成string类型 dang--
 * 11,把一个字符串（yyyy-MM-dd）转化成Date dang--
 * 
 * @author Administrator
 * 
 */

public class Utils {
	private static final String TAG = "Utils";

	/*
	 * global-phone-number = ["+"] 1*( DIGIT / written-sep ) written-sep =
	 * ("-"/".")
	 */
	private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN = Pattern
	// .compile("[\\+]?[0-9.-]+");
	// .compile("^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$");
			.compile("(1[3,5,8][0-9])\\d{8}$");

	private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN1 = Pattern
	// .compile("[\\+]?[0-9.-]+");
	// .compile("^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$");
			.compile("(1[4][7])\\d{8}$");

	/**
	 * 验证邮箱输入是否合法 dang
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
//		String strPattern = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
		String strPattern = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		if(m.matches()){
			return true;
		}
		return false;
//		return m.matches();
	}
	
	/**
	 * 输入小数
	 * @param params
	 * @return
	 */
	public static boolean isNumPoint(String params){
		String strPattern = "^([1-9][0-9]*(\\.[0-9]{1,4})?|0\\.(?!0+$)[0-9]{1,4})$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(params);
		return m.matches();
	}

	public static int diffDay(Date lastDate) {
		Date date = new Date();
		long i = date.getTime();
		long j = lastDate.getTime();
		if (j > i) {
			return 0;
		}
		if (i == j) {
			return 0;
		}
		long diff = i - j;
		int day = (int) (diff / (24 * 60 * 60 * 1000));
		return day;
	}

	/**
	 * 判断密码格式是否正确
	 * 
	 * @param password
	 *            dang
	 * @return
	 */
	public static boolean checkPassword(String password) {
		String strPattern = "^[a-zA-Z0-9_-]{3,16}$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(password);
		return m.matches();
	}

	/**
	 * 字符串是否为空判断 dang 是空的返回true；否则返回false
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return true;
		}
		return false;
	}

	public static String getEmptyStr(String str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return "暂无";
		}
		return str;
	}

	/**
	 * 简单判断电话号码格式是否正确 dang
	 * 
	 * @param cellPhone
	 * @return
	 */
	public static boolean checkPhone(String cellPhone) {
		if (TextUtils.isEmpty(cellPhone)) {
			return false;
		}

		Matcher match = GLOBAL_PHONE_NUMBER_PATTERN.matcher(cellPhone);
		if (match.matches()) {
			return true;
		} else {
			match = GLOBAL_PHONE_NUMBER_PATTERN1.matcher(cellPhone);
		}
		return match.matches();

	}

	/**
	 * 判断手机号码格式是否正确 dang
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkMobilePhone(String phone) {
		if(phone.trim().length()==11){
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9])|(14[0-9])|)\\d{8}$");
			Matcher matcher = p.matcher(phone);

			if (matcher.matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * GBK转UTF-8 dang
	 * 
	 * @param str
	 * @return
	 */
	public static String toUtf8(String str) {
		try {
			str = URLEncoder.encode(str, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * MD5加密算法 dang
	 * 
	 * @param secretKey
	 * @return
	 */
	public static String md5Sign(String secretKey) {
		if (Utils.isEmpty(secretKey)) {
			return "";
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(secretKey.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.exit(-1);
			Log.e(TAG, "md5 sign error " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "md5 sign error " + e.getMessage());
		}

		byte[] byteArray = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				sb.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			} else {
				sb.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		return sb.toString();
	}

	/**
	 * 格式化当前时间 dang
	 * 
	 * @return
	 */
	public static String formatDateTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
		return format.format(date);
	}

	/**
	 * 根据字体大小获取字体高度 dang
	 * 
	 * @param fontSize
	 * @return
	 */
	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent);
	}

	/**
	 * 将textview中的字符全角化 dang
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 从文件读取信息 dang
	 * 
	 * @param path
	 * @return
	 */
	public static String getContentFromFile(String path) {
		StringBuffer sb = new StringBuffer();
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while (true) {
					String content = reader.readLine();
					if (content == null) {
						break;
					}
					sb.append(content);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 比较两个时间大小 dang
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean diffTime(Date date1, Date date2) {
		if (date1.getTime() > date2.getTime()) {
			return true;
		}
		return false;
	}

	/**
	 * 日期格式化，将date类型转成string类型 dang
	 * 
	 * @param date_str
	 * @return
	 */

	// 日期转换成字符串 日期转换成字符串 获取当前详细时间
	public static String dateToString(Date date_str) {
		if (date_str == null) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date_str);
	}

	/**
	 * 日期格式化，将date类型转成string类型 dang
	 * 
	 * @param date_str
	 * @return
	 */
	public static String dateToString(java.util.Date date_str, String str) {
		if (date_str == null) {
			return "";
		}
		String datestr = "";
		try {
			java.text.DateFormat df = new java.text.SimpleDateFormat(str);
			datestr = df.format(date_str);
		} catch (Exception ex) {
			Log.e(TAG, "date to string error " + ex.getMessage());
		}
		return datestr;
	}

	/**
	 * 把一个字符串（yyyy-MM-dd）转化成Date dang
	 * 
	 * @param Date
	 *            date_str;
	 * @return String
	 */
	public static Date getDateByStr(String str) {
		Date date = new Date();
		try {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			date = sdf.parse(str);
		} catch (Exception e) {
			System.out.println("String to Date error" + e.getMessage());
		}
		return date;
	}

	/**
	 * 把一个字符串（yyyy-MM-dd）转化成Date dang
	 * 
	 * @param Date
	 *            date_str;
	 * @return String
	 */
	public static Date getDateByStr(String str, String format) {
		Date date = new Date();
		try {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					format);
			date = sdf.parse(str);
		} catch (Exception e) {
			System.out.println("String to Date error" + e.getMessage());
		}
		return date;
	}

	/**
	 * 获取月，天，小时和分钟组成的时间 dang
	 * 
	 * @param str1
	 * @return
	 */
	public static String getShortTime(String str1) {
		Date date = new Date();
		date = getDateByStr(str1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int mMouth = cal.get(Calendar.MONTH) + 1;
		int mDay = cal.get(Calendar.DAY_OF_MONTH);
		int mHour = cal.get(Calendar.HOUR_OF_DAY);
		int mMinuts = cal.get(Calendar.MINUTE);
		String mMouthStr = mMouth + "";
		String mDayStr = mDay + "";
		String mHourStr = mHour + "";
		String mMinutsStr = mMinuts + "";
		if (mMouth < 10) {
			mMouthStr = "0" + mMouth + "";
		}
		if (mDay < 10) {
			mDayStr = "0" + mDay + "";
		}
		if (mHour < 10) {
			mHourStr = "0" + mHour + "";
		}
		if (mMinuts < 10) {
			mMinutsStr = "0" + mMinuts + "";
		}
		return (mMouthStr + "-" + mDayStr + " " + mHourStr + ":" + mMinutsStr);
	}

	/**
	 * 退出一个异步任务,如果这个任务正在运行中，则这个任务会被中断 dang
	 * 
	 * @param task
	 */
	public static void cancelTaskInterrupt(AsyncTask<?, ?, ?> task) {
		cancelTask(task, true);
	}

	/**
	 * 退出一个异步任务 dang
	 * 
	 * @param task
	 * @param b
	 */
	public static void cancelTask(AsyncTask<?, ?, ?> task,
			boolean mayInterruptIfRunning) {
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
			task.cancel(mayInterruptIfRunning);
		}
	}

	public static String getEndDayStr(Integer endTime) {
		long diff = endTime * 1000l - System.currentTimeMillis();
		if (diff <= 0) {
			return "已结束";
		}
		int days = (int) (diff / (24 * 60 * 60 * 1000));
		diff = diff % (24 * 60 * 60 * 100);
		int hours = (int) (diff / (60 * 60 * 1000));
		diff = diff % (60 * 60 * 100);
		int minues = (int) (diff / (60 * 1000));
		String returnStr = "";
		if (days > 0) {
			returnStr = "" + days + "天";
		}
		hours = hours + 8;
		if (hours > 24) {
			days += 1;
			hours -= 24;
		}
		if (hours > 0) {
			returnStr += "" + hours + "小时";
		} else {
			if (!Utils.isEmpty(returnStr)) {
				returnStr += "0小时";
			}
		}
		if (minues > 0) {
			returnStr += "" + minues + "分";
		} else {
			if (!Utils.isEmpty(returnStr)) {
				returnStr += "0分";
			}
		}
		return returnStr;
	}

	public static String diffTime(Date firstDate) {
		long firstTime = firstDate.getTime();
		long nowTime = new Date().getTime();
		long diff = (nowTime - firstTime) / 1000; // 秒数
		if (diff <= 60) {
			return "1分钟前";
		} else if (diff <= (60 * 60)) {
			int i = (int) (diff / 60);
			int j = (int) (diff % 60);
			if (i == 60) {
				return "1小时前";
			}
			if (j == 0) {
				return "" + i + "分钟前";
			} else {
				if (i == 59) {
					return "1小时前";
				}
				return "" + (i + 1) + "分钟前";
			}
		} else if (diff <= (60 * 60 * 24)) {
			int i = (int) (diff / (60 * 60));
			int j = (int) (diff % (60 * 60));
			if (i == 24) {
				return "1天前";
			}
			if (j == 0) {
				return "" + i + "小时前";
			} else {
				if (i == 23) {
					return "1天前";
				}
				return "" + (i + 1) + "小时前";
			}
		} else if (diff <= (60 * 60 * 24 * 7)) {
			int i = (int) (diff / (60 * 60 * 24));
			int j = (int) (diff % (60 * 60 * 24));
			if (i == 7) {
				return "1周前";
			}
			if (j == 0) {
				return "" + i + "天前";
			} else {
				if (i == 6) {
					return "1周前";
				}
				return "" + (i + 1) + "天前";
			}
		} else {
			return dateToString(firstDate, "yyyy-MM-dd");
		}
	}

	// MD5是一种不可逆的加密算法，也就是说只能加密，不能解密；
	// 只有判断加密后的字符串是否相同来判断是否为同一对象
	/**
	 * MD5加密算法 (MD5是一种不可逆的加密算法，也就是说只能加密，不能解密；只有判断加密后的字符串是否相同来判断是否为同一对象)
	 * 
	 * @param inStr
	 * @return
	 */
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// 可逆的加密算法
	/**
	 * 可逆的加密算法（用此方法可以解密KL(KL(inStr))）
	 * 
	 * @param inStr
	 * @return
	 */
	public static String KL(String inStr) {
		// String s = new String(inStr);
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;
	}

	/**
	 * 获取设备号id dang
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * 判断2个字符串是否相等 dang
	 * 
	 * @param str
	 * @param str1
	 * @return
	 */
	public static boolean isStringEquals(String str, String str1) {
		if (str.equals(str1) || (str == str1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 计算字符串的字符数 dang
	 * 
	 * @param str
	 * @return
	 */
	public static int countContentLength(String str) {
		int length = 0;
		str = filterHtml(str);
		String target = "http://";
		int targetLen = target.length();
		int begin = str.indexOf(target, 0);
		if (begin != -1) {
			while (begin != -1) {
				length += begin;
				if (begin + targetLen == str.length()) {
					str = str.substring(begin);
					break;
				}
				int i = begin + targetLen;
				char c = str.charAt(i);
				while (((c <= 'Z') && (c >= 'A')) || ((c <= 'z') && (c >= 'a'))
						|| ((c <= '9') && (c >= '0')) || (c == '_')
						|| (c == '.') || (c == '?') || (c == '/') || (c == '%')
						|| (c == '&') || (c == ':') || (c == '=') || (c == '-')) {
					i++;
					if (i < str.length()) {
						c = str.charAt(i);
					} else {
						i--;
						length--;
						break;
					}
				}

				length += 10;

				str = str.substring(i);
				begin = str.indexOf(target, 0);
			}

			length += str.length();
		} else {
			length = str.length();
		}

		return length;
	}

	private static String filterHtml(String str) {
		str = str.replaceAll("<(?!br|img)[^>]+>", "").trim();
		str = unicodeToGBK(str);
		str = parseHtml(str);
		str = str.trim();

		return str;
	}

	private static String parseHtml(String newStatus) {
		String temp = "";
		String target = "<img src=";
		int begin = newStatus.indexOf(target, 0);
		if (begin != -1) {
			while (begin != -1) {
				temp = temp + newStatus.substring(0, begin);
				int end = newStatus.indexOf(">", begin + target.length());
				// String t = newStatus.substring(begin + 10, end - 1);

				// temp = temp + (String)ImageAdapter.hashmap.get(t);

				newStatus = newStatus.substring(end + 1);
				begin = newStatus.indexOf(target);
			}
			temp = temp + newStatus;
		} else {
			temp = newStatus;
		}

		return temp;
	}

	private static String unicodeToGBK(String s) {
		String[] k = s.split(";");
		String rs = "";
		for (int i = 0; i < k.length; i++) {
			int strIndex = k[i].indexOf("&#");
			String newstr = k[i];
			if (strIndex > -1) {
				String kstr = "";
				if (strIndex > 0) {
					kstr = newstr.substring(0, strIndex);
					rs = rs + kstr;
					newstr = newstr.substring(strIndex);
				}

				int m = Integer.parseInt(newstr.replace("&#", ""));
				char c = (char) m;
				rs = rs + c;
			} else {
				rs = rs + k[i];
			}
		}
		return rs;
	}

	/**
	 * 悬浮框提示 dang
	 * 
	 * @param context
	 * @param message
	 */
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获得当前日期前多少天 dang
	 * 
	 * @param days
	 * @return
	 */
	public static String getBeforeDays(int days) {
		Date d = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - days);
		Date beforeDate = now.getTime();
		String s = Utils.dateToString(beforeDate, "yyyy-MM-dd");
		return s;
	}

	/**
	 * 两个地理位置的间距 返回单位暂未处理 dang
	 * 
	 * @param wd1
	 * @param jd1
	 * @param wd2
	 * @param jd2
	 * @return
	 */
	public static double D_jw(double wd1, double jd1, double wd2, double jd2) {
		double x, y, out;
		double PI = 3.14159265;
		double R = 6.371229 * 1e6;
		x = (jd2 - jd1) * PI * R * Math.cos(((wd1 + wd2) / 2) * PI / 180) / 180;
		y = (wd2 - wd1) * PI * R / 180;
		out = Math.hypot(x, y);
		return out / 1000;
	}

	/**
	 * double转str并格式化 dang
	 * 
	 * @param d
	 * @return
	 */
	public static String doubleToStr(double d) {
		DecimalFormat df = new DecimalFormat("0.##");
		String str = df.format(d);
		return str;
	}

	/**
	 * 拨打电话 dang
	 * 
	 * @param context
	 * @param phoneNum
	 */
	public static void makeCall(Context context, String phoneNum) {
		if (Utils.isEmpty(phoneNum)) {
			Utils.showToast(context, "号码为空");
			return;
		}
		Intent intent = new Intent("android.intent.action.CALL",
				Uri.parse("tel:" + phoneNum));
		context.startActivity(intent);
	}

	public static String getTxtContent(Context context, String fileName) {
		try {
			StringBuilder builder = new StringBuilder();
			InputStream input = context.getResources().getAssets()
					.open(fileName);
			InputStreamReader reader = new InputStreamReader(input);
			BufferedReader mReader = new BufferedReader(reader);
			String content = null;
			while ((content = mReader.readLine()) != null) {
				builder.append(content);
			}
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断当前日期是星期几 dang
	 * 
	 * @param pTime
	 *            修要判断的时间
	 * @return dayForWeek 判断结果
	 * @Exception 发生异常
	 */
	public static int dayForWeek(String pTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(pTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		Log.i(TAG, "DAY OF WEEK:" + dayForWeek);
		return dayForWeek;
	}

	public static void browser(Context context, String url) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(url);
			intent.setData(content_url);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(Utils.class.getName(), "browser url is error");
		}
	}

	/**
	 * 得到当天的零点时分 dang
	 * 
	 * @return String
	 */
	public static String getTodayDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date zero = cal.getTime();
		long l = zero.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date(l - 1));
		return date;
	}

	/**
	 * 利用时间戳创建图片名
	 * 
	 * @return
	 */
	public static String getPicName() {
		/*
		 * Random random=new Random(); int num=random.nextInt(100); Calendar
		 * calendar=Calendar.getInstance(); int
		 * year=calendar.get(Calendar.YEAR); int
		 * month=calendar.get(Calendar.MONTH); int
		 * day=calendar.get(Calendar.DAY_OF_MONTH); int
		 * hour=calendar.get(Calendar.HOUR_OF_DAY); int
		 * minute=calendar.get(Calendar.MINUTE); String
		 * randomNum=year+""+month+""+day+""+hour+""+minute+""+num; return
		 * randomNum;
		 */

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'yyyyMMddHHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 获取当前日期
	 * 
	 * @param str
	 * @return
	 */
	public static String getNowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	/**
	 * 获取当前详细时间
	 * 
	 * @param str
	 * @return
	 */
	public static String getNowDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 获取当前时间
	 * 
	 * @param str
	 * @return
	 */
	public static String getNowTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Date());
	}

	/**
	 * 日期比较
	 * 
	 * @param date1
	 *            第一个日期
	 * @param date2
	 *            第二个日期
	 * @return true date1日期小
	 */
	public static boolean getDateCompare(String date1, String date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		try {
			Date d1 = sdf.parse(date1);
			Date d2 = sdf.parse(date2);
			if (d1.equals(d2)) {
				return true;
			}
			return d1.before(d2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取硬件SN码
	 * 
	 * @return
	 */
	public static String getSN() {
		String v = "";
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			v = (String) (get.invoke(c, "ro.serialno", "unknown"));
		} catch (Exception e) {
		}
		return v;
	}

	/**
	 * 获取当时连接网络的类型
	 * 
	 * @param context
	 * @return 1:手机移动网络
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 判断是否有网络
	 * 
	 * @return true:有,false：无
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是有效ip地址
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isReachable(String address) {
		boolean result = false;
		try {
			InetAddress.getAllByName(address);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 检查手机网络状况 dang
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetStatus(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "CHECK NETWORK STATUS ERROR!");
		}
		return false;
	}

	/**
	 * 检查当前网络状况是否为WIFI或3G dang
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkWifiOr3gNet(Context context) {
		try {

			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null || !cm.getBackgroundDataSetting()) {
				return false;
			}

			int netType = info.getType();
			int netSubtype = info.getSubtype();

			if (netType == ConnectivityManager.TYPE_WIFI) { // WIFI
				return info.isConnected();
			} else if (netType == ConnectivityManager.TYPE_MOBILE
					&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
					&& !tm.isNetworkRoaming()) { // 3G
				return info.isConnected();
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, "CHECK WIFI OR 3G NET ERROR!");
		}
		return false;
	}

	/**
	 * 判断是否为2G网络 dang
	 * 
	 * @param context
	 * @return
	 */
	public static boolean is2gNet(Context context) {
		try {
			ConnectivityManager mConnectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = mConnectivity.getActiveNetworkInfo();
			if (info == null || !mConnectivity.getBackgroundDataSetting()) {
				return false;
			}
			int netType = info.getType();
			int netSubtype = info.getSubtype();
			if (netType == ConnectivityManager.TYPE_MOBILE
					&& netSubtype != TelephonyManager.NETWORK_TYPE_UMTS) {
				return info.isConnected();
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, "CHECK 2g NET STATUS ERROR!");
		}
		return false;
	}

	/**
	 * 检查是否有wifi dang
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 检查网络连接，如果无网络可用，就不需要进行连网操作等
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return false;
		}
		// 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * 获取手机的ip地址 dang
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return null;
	}

	/**
	 * dip值转px值
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dipTopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px值转dip值
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int pxTodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
