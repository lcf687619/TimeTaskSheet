package com.mission.schedule.utils;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class InWeekUtils {
	private static InWeekUtils instance;

	public synchronized static InWeekUtils getInstance() {
		if (instance == null) {
			instance = new InWeekUtils();
		}
		return instance;
	}

	/**
	 * @author lcf 根据传入的日期，格式为 yyyy-MM-dd 判断传入日期是否为在当前日期本周内 如果是返回true，否则返回false
	 */
	public boolean getNextWeek(Context context, String date) {
		Calendar calendar = Calendar.getInstance();
		String currentweek = CharacterUtil
				.getWeekOfDate(context, DateUtilHelper.parseDate(DateUtilHelper
						.formatDate(new Date())));
		calendar.setTime(DateUtilHelper.parseDate(DateUtilHelper
				.formatDate(new Date())));
		if ("周一".equals(currentweek)) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 6);
		} else if ("周二".equals(currentweek)) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 5);
		} else if ("周三".equals(currentweek)) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 4);
		} else if ("周四".equals(currentweek)) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 3);
		} else if ("周五".equals(currentweek)) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 2);
		} else if ("周六".equals(currentweek)) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 1);
		} else {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 0);
		}
		String currentdate = DateUtilHelper.formatDate(calendar.getTime());
		if (DateUtilHelper.parseDate(date).getTime() <= DateUtilHelper
				.parseDate(currentdate).getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是一周以内,也就是7天之内
	 */
	public boolean isInWeek(String date) {
		String currentdate = DateUtilHelper.formatDate(new Date());
		long betweenday = DateUtilHelper.parseDate(currentdate).getTime()
				- DateUtilHelper.parseDate(date).getTime();
		int day = (int) Math.abs(betweenday / (1000 * 60 * 60 * 24));
		if (day <= 7) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是一个月内
	 */
	public boolean isInMonth(String date) {
		String currentdate = DateUtilHelper.formatDate(new Date());
		long betweenday = DateUtilHelper.parseDate(currentdate).getTime()
				- DateUtilHelper.parseDate(date).getTime();
		int day = (int) Math.abs(betweenday / (1000 * 60 * 60 * 24));
		if (day <= 30) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 推后一个小时 传入日期和时间，返回相应的日期和时间
	 */
	public String AfterOneHours(String date, String time) {
		Date yyyyMMddHHmm = DateUtilHelper.parseDateTime(date + " " + time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(yyyyMMddHHmm);
		calendar.add(Calendar.HOUR, 1);
		return DateUtilHelper.formatDateTime(calendar.getTime());
	}

	/**
	 * 推后一天
	 */
	public String AfterOneDay(String date) {
		Date yyyyMMdd = DateUtilHelper.parseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(yyyyMMdd);
		calendar.add(Calendar.DATE, 1);
		return DateUtilHelper.formatDate(calendar.getTime());
	}

	/**
	 * 推后一周
	 */
	public String AfterOneWeek(String date) {
		Date yyyyMMdd = DateUtilHelper.parseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(yyyyMMdd);
		calendar.add(Calendar.DATE, 7);
		return DateUtilHelper.formatDate(calendar.getTime());
	}

	/**
	 * 推后一个月
	 */
	public String AfterOneMonth(String date) {
		Date yyyyMMdd = DateUtilHelper.parseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(yyyyMMdd);
		calendar.add(Calendar.DATE, 30);
		return DateUtilHelper.formatDate(calendar.getTime());
	}

	/**
	 * 推后到下周一
	 */
	public String AfterNextWeekFirstDay(Context context, String date) {
		Calendar calendar = Calendar.getInstance();
		if (DateUtilHelper.parseDate(date).getTime() >= DateUtilHelper
				.parseDate(DateUtilHelper.formatDate(new Date())).getTime()) {
			String currentweek = CharacterUtil.getWeekOfDate(context,
					DateUtilHelper.parseDate(date));
			calendar.setTime(DateUtilHelper.parseDate(date));
			if ("周一".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 7);
			} else if ("周二".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 6);
			} else if ("周三".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 5);
			} else if ("周四".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 4);
			} else if ("周五".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 3);
			} else if ("周六".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 2);
			} else {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 1);
			}
			return DateUtilHelper.formatDate(calendar.getTime());
		}else{
			String currentweek = CharacterUtil.getWeekOfDate(context,
					DateUtilHelper.parseDate(DateUtilHelper.formatDate(new Date())));
			calendar.setTime(DateUtilHelper.parseDate(currentweek));
			if ("周一".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 7);
			} else if ("周二".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 6);
			} else if ("周三".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 5);
			} else if ("周四".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 4);
			} else if ("周五".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 3);
			} else if ("周六".equals(currentweek)) {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 2);
			} else {
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 1);
			}
			return DateUtilHelper.formatDate(calendar.getTime());
		}
	}
}
