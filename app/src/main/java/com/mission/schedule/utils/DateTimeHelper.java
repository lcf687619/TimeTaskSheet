package com.mission.schedule.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.net.ParseException;

public final class DateTimeHelper {
	private static boolean isLeap=false;
	public static boolean isRed=false;
	private static ArrayList<HashMap<String, String>> data;
	private static HashMap<String, String> map = null;
	private static String today;
	private static int to_day;
	private static int temp_day;
	private static int temp_month;
	private static int temp_year;
	
	/**
	 * 把时间格式化成如：2002-08 格式的字符串
	 */
	public final static String FMT_yyyyMM = "yyyy-MM";
	/**
	 * 把时间格式化成如：01 格式的字符串
	 */
	public final static String FMT_dd = "dd";
	/**
	 * 把时间格式化成如：2002 格式的字符串
	 */
	public final static String FMT_yyyy = "yyyy";
	/**
	 * 把时间格式化成如：22:04 格式的字符串
	 */
	public final static String FMT_HHmm = "HH:mm";
	/**
	 * 把时间格式化成如：2002-08-03 格式的字符串
	 */
	public final static String FMT_yyyyMMdd = "yyyy-MM-dd";
	/**
	 * 把时间格式化成如：2002-08-03 08:26 格式的字符串
	 */
	public final static String FMT_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
	/**
	 * 把时间格式化成如：2002-08-03 08:26:16 格式的字符串
	 */
	public final static String FMT_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";





	/**
	 * 格式化时间
	 * @param inputDate 需要格式化大的时间对象
	 * @param formatStr 需要返回的时间格式
	 * @return
	 */
	public static String formatDateTimetoString(Date inputDate,String formatStr){
		String reStr = "";
		if (inputDate == null || formatStr == null || formatStr.trim().length() < 1) {
			return reStr;
		}
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(formatStr);
		reStr = sdf.format(inputDate);
		return reStr == null ? "" : reStr;
	}
	
	/**
	 * 获取一年中每月的日期
	 * @param standard_year
	 * @param standard_month
	 * @param standard_day
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> GenData(int standard_year,int standard_month,int standard_day){
		   data=new ArrayList<HashMap<String,String>>();
		   temp_day=standard_day;
		   temp_month=standard_month;
		   temp_year=standard_year;
		   today=temp_year+"-"+(temp_month>9?""+temp_month:"0"+temp_month)+"-"+(temp_day>9?""+temp_day:"0"+temp_day);
		   to_day=standard_day;
		   
		    map=new HashMap<String, String>();   
		    String tempStrT=temp_year+"-"+(temp_month>9?""+temp_month:"0"+temp_month)+"-"+(temp_day>9?""+temp_day:"0"+temp_day);
		    map.put("time", tempStrT);
			data.add(map);
			for(int i=temp_day-1;i>0;i--){
				String tempStr=temp_year+"-"+(temp_month>9?""+temp_month:"0"+temp_month)+"-"+(i>9?""+i:"0"+i);
				map=new HashMap<String, String>();
				map.put("time", tempStr);
				data.add(0,map);
			}
			   if(temp_month==1){    
				   temp_year--;
				   temp_month=12;					   
			   }else{
				   temp_month--;
			   }
			  calLeapYear(temp_year);
			   if(temp_month==12){
				   temp_year++;
				   temp_month=1;
			   }else{
				   temp_month=temp_month+1;
			   }
			   calLeapYear(temp_year);
			   if(temp_month==4||temp_month==6||temp_month==9||temp_month==11){
				   for(int i=to_day+1;i<=30;i++){
					   String tempStr=temp_year+"-"+(temp_month>9?""+temp_month:"0"+temp_month)+"-"+(i>9?""+i:"0"+i);
					   map=new HashMap<String, String>();
					   map.put("time", tempStr);
					   data.add(map);
				   }
			   }else if(temp_month==2){
				   if(isLeap){
					   for(int i=to_day+1;i<=29;i++){
						   String tempStr=temp_year+"-"+(temp_month>9?""+temp_month:"0"+temp_month)+"-"+(i>9?""+i:"0"+i);
							map=new HashMap<String, String>();
							map.put("time", tempStr);
							data.add(map);
					   }
				   }else{
					   for(int i=to_day+1;i<=28;i++){
						   String tempStr=temp_year+"-"+(temp_month>9?""+temp_month:"0"+temp_month)+"-"+(i>9?""+i:"0"+to_day);
							map=new HashMap<String, String>();
							map.put("time", tempStr);
							data.add(map);
					   }
				   }
			   }else{
				   for(int i=to_day+1;i<=31;i++){
					   String tempStr=temp_year+"-"+(temp_month>9?""+temp_month:"0"+temp_month)+"-"+(i>9?""+i:"0"+i);
						map=new HashMap<String, String>();
						map.put("time", tempStr);
						data.add(map);
				   }
			  }
			  if(temp_month>=12){
				  temp_year++;
				  temp_month=1;
			  }else{
				  temp_month++;
			  }
			  return data;
	   }
	/**
	    * 平年 闰年
	    * @param i
	    */
	   private static void calLeapYear(int i){
		   if(i%100==0){
			   if(i%400==0){
				   isLeap=true;
			   }else{
				   isLeap=false;
			   }
		   }else{
			   if(i%4==0){
				   isLeap=true;
			   }else{
				   isLeap=false;
			   }
		   }
	   }
	/**
	 * Parse date like "yyyy-MM-dd".
	 */
	public static Date parseDate(String d) {
		try {
			return new SimpleDateFormat(FMT_yyyyMMdd).parse(d);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Parse date and time like "yyyy-MM-dd hh:mm".
	 */
	public static Date parseDateTime(String dt) {
		try {
			return new SimpleDateFormat(FMT_yyyyMMddHHmm).parse(dt);
		} catch (Exception e) {
		}
		return null;
	}

	public static Date parseDateTimeSs(String dt) {
		try {
			return new SimpleDateFormat(FMT_yyyyMMddHHmmss).parse(dt);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 将字符串日期增加或者减少多少天。返回增加或者减少的天数
	 * @param s 当前要增加或者减少的字符串日期，格式为yyyy-MM-dd
	 * @param n 增加天数，减少用负数。
	 * @return 返回增加或者减少的天数
	 */
	public static String addDay(String s, int n) {   
		try {   
			SimpleDateFormat sdf = new SimpleDateFormat(FMT_yyyyMMdd);   
			Calendar cd = Calendar.getInstance();   
			cd.setTime(sdf.parse(s));   
			cd.add(Calendar.DATE, n);//增加一天   
			//cd.add(Calendar.MONTH, n);//增加一个月   
			return sdf.format(cd.getTime());   
		} catch (Exception e) {   
			return null;   
		}   
	}
	
	/**
	 * 将字符串日期增加或者减少多少天。返回增加或者减少的天数
	 * @param s 当前要增加或者减少的日期，
	 * @param n 增加天数，减少用负数。
	 * @return 返回增加或者减少的天数 格式为yyyy-MM-dd HH:mmss
	 */
	public static String addDayDetail(Date s, int n) {   
		try {   
			SimpleDateFormat sdf = new SimpleDateFormat(FMT_yyyyMMddHHmmss);   
			Calendar cd = Calendar.getInstance();   
			cd.setTime(s);   
			cd.add(Calendar.DATE, n);//增加一天   
			return sdf.format(cd.getTime());   
		} catch (Exception e) {   
			return null;   
		}   
	}
	
	/**
	 * 传入字符串时间返回与当前日期相差的天数
	 * @param s yyyy-MM-dd
	 * @return 如果传入的日期比当期日期大返回正数，小返回负数
	 */
	public static Long getDifferDay(String s,String strFormat) {   
		try {   
			SimpleDateFormat sdf = new SimpleDateFormat(strFormat);   
			Calendar cd = Calendar.getInstance();   
			long num=-111;
			try 
			{
				num = getDistinceDay(sdf.format(cd.getTime()),s,strFormat);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return num;   
		} catch (Exception e) {   
			return (long)0;   
		}   
	}

	/** 
	 * 两个日期做减法，返回相差天数 
	 *  
	 * @throws ParseException 
	 * @throws ParseException 
	 */  
	public static long datesub(Date date1, Date date2) throws ParseException {  
		// 日期相减得到相差的日期  
		long day = (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000) > 0 ? (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000) : (date2  
				.getTime() - date1.getTime())  
				/ (24 * 60 * 60 * 1000);  
		return day + 1;  
	}  

	// 获取相隔天数  
	public static long getDistinceDay(String beforedate, String afterdate,String strFormat)  
			throws ParseException, java.text.ParseException {  
		SimpleDateFormat d = new SimpleDateFormat(strFormat);  
		long dayCount = 0;  
		try {  
			Date d1 = d.parse(beforedate);  
			Date d2 = d.parse(afterdate);  

			dayCount = (d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000);  

		} catch (ParseException e) {  
			System.out.println("Date parse error!");  
			// throw e;  
		}  
		return dayCount;  
	}  
	
	/**
	 *格式化double数据
	 * @param money
	 * @return
	 */
	public static String delMoneyNorm(double money){
		String m="0.00";
		try{
			DecimalFormat df=new DecimalFormat("0.00");
			m=df.format(money);
		}catch (Exception e) {
			System.out.println("delMoneyNorm:"+e.getMessage());
		}
		return m;
	}
	
	/**
	 * 第一个时间是否比第二个时间大
	 * @param date1 第一个
	 * @param date2 第二个
	 * @return
	 */
	public static boolean compareDate(Date date1,Date date2){
		boolean bool = false;
		
		bool=date1.after(date2);
		
		return bool;
	}

	/**
	 * 第一个时间是否比第二个时间大
	 * @param strDate1 第一个时间字符串
	 * @param strDate2 第二个时间字符串
	 * @param format 时间格式
	 * @return
	 */
	public static boolean compareDate(String strDate1,String strDate2,String format){
		boolean bool = false;
		Date date1=new Date();
		Date date2=date1;
		try {
			date1 = new SimpleDateFormat(format).parse(strDate1);
			date2 = new SimpleDateFormat(format).parse(strDate2);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bool=date1.after(date2);
		
		return bool;
	}
	
	
}
