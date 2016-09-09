package com.mission.schedule.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class DateUtilHelper {

	public final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

	public final static String DATETIME_FORMAT_SS = "yyyy-MM-dd HH:mm:ss";

	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String DATE_FORMAT_YEAR = "yyyy";
	public final static String DATE_FORMAT_YEAR_MONTH = "yyyy-MM";
	
	public final static String DATE_FORMAT_HM = "HH:mm";
	
	public final static String YYYYMMDD = "yyyyMMdd";

	public final static String YYYYMM = "yyyyMM";

	public final static String DD = "dd";

	/**
	 * Get the previous time, from how many days to now.
	 * 
	 * @param days
	 *            How many days.
	 * @return The new previous time.
	 */
	public static Date previous(int days) {
		return new Date(System.currentTimeMillis() - days * 3600000L * 24L);
	}

	/**
	 * Convert date and time to string like "yyyyMMdd".
	 */
	public static String formatDateYYYYMMDD(Date d) {
		return new SimpleDateFormat(YYYYMMDD).format(d);
	}

	/**
	 * Convert date and time to string like "yyyyMM".
	 */
	public static String formatDateYYYYMM(Date d) {
		return new SimpleDateFormat(YYYYMM).format(d);
	}

	/**
	 * Convert date and time to string like "DD".
	 */
	public static String formatDateDD(Date d) {
		return new SimpleDateFormat(DD).format(d);
	}
	/**
	 * Convert date and time to string like "yyyy-MM-dd HH:mm".
	 */
	public static String formatDateTime(Date d) {
		return new SimpleDateFormat(DATETIME_FORMAT).format(d);
	}

	public static String formatDateTime(Date d, String formatType) {
		return new SimpleDateFormat(formatType).format(d);
	}

	public static String formatDateTimeSs(Date d) {
		return new SimpleDateFormat(DATETIME_FORMAT_SS).format(d);
	}

	/**
	 * Convert date and time to string like "yyyy-MM-dd HH:mm".
	 */
	public static String formatDateTime(long d) {
		return new SimpleDateFormat(DATETIME_FORMAT).format(d);
	}

	/**
	 * Convert date to String like "yyyy-MM-dd".
	 */
	public static String formatDate(Date d) {
		return new SimpleDateFormat(DATE_FORMAT).format(d);
	}

	/**
	 * Parse date like "yyyy-MM-dd".
	 */
	public static Date parseDate(String d) {
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse(d);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Parse date and time like "yyyy-MM-dd hh:mm".
	 */
	public static Date parseDateTime(String dt) {
		try {
			return new SimpleDateFormat(DATETIME_FORMAT).parse(dt);
		} catch (Exception e) {
		}
		return null;
	}

	public static Date parseDateTimeSs(String dt) {
		try {
			return new SimpleDateFormat(DATETIME_FORMAT_SS).parse(dt);
		} catch (Exception e) {
		}
		return null;
	}


	public void test() {
	}
	/**
	 * 
	 * @param begin 第一个时间
	 * @param end 第二个时间
	 * @param diffStr 需要返回时间差精确度,默认为秒,提供以下值(区分大小写)选择"s","m","h","d"
	 * @return
	 */
	public static long DateTimeDiffer(Date begin,Date end,String diffStr){
		//System.out.println(DateUtil.formatDateTime(begin, DateUtil.DATE_FORMAT));
		//System.out.println(DateUtil.formatDateTime(end, DateUtil.DATE_FORMAT));
		long between=(begin.getTime()-end.getTime())/1000;//除以1000是为了转换成秒
				long day1=between/(24*3600);
				long hour1=between%(24*3600)/3600;
				long minute1=between%3600/60;
				long second1=between%60/60;

		//System.out.println(""+day1+"天"+hour1+"小时"+minute1+"分"+second1+"秒");
		if(diffStr.equals("m")){
			return minute1;
		}else if(diffStr.equals("s")){
			return second1;
		}else if(diffStr.equals("h")){
			return hour1;
		}else if(diffStr.equals("d")){
			return day1;
		}else{
			return second1;
		}
	}
public static void main(String[] args) {
	Date date = new Date();
	System.out.println(DateUtilHelper.formatDateTimeSs(date));
	date = DateUtilHelper.offsetDate(date, Calendar.MINUTE, 1);
	System.out.println(DateUtilHelper.formatDateTimeSs(date));
	
}

	/* 返回指定日期相应位移后的日期
	 * 
	 * @param date
	 *            参考日期
	 * @param field
	 *            位移单位，见 年Calendar.YEAR ,时，Calendar.HOUR，分，Calendar.MINUTE；秒:Calendar.SECOND
	 * @param offset
	 *            位移数量，正数表示之后的时间，负数表示之前的时间
	 * @return 位移后的日期
	 */
	public static Date offsetDate(Date date, int field, int offset) {
		Calendar calendar = convert(date);
		calendar.add(field, offset);
		return calendar.getTime();
	}
	private static Calendar convert(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}


}
