package com.mission.schedule.utils;

import java.util.Calendar;
import java.util.Date;



import com.mission.schedule.R;

import android.content.Context;

public class CharacterUtil {
	
	/**
	 * 根据日期获得星期
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekOfDate(Context context, Date date) {
		String[] weekDaysName = { context.getResources().getString(R.string.sunday), context.getResources().getString(R.string.monday), 
				context.getResources().getString(R.string.tuesday), context.getResources().getString(R.string.wednesday), 
				context.getResources().getString(R.string.thursday), context.getResources().getString(R.string.friday), 
				context.getResources().getString(R.string.saturday) };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}
	/**
	 * 根据日期获得星期
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekOfDate1(Context context, Date date) {
		String[] weekDaysName = { context.getResources().getString(R.string.mysunday), context.getResources().getString(R.string.mymonday), 
				context.getResources().getString(R.string.mytuesday), context.getResources().getString(R.string.mywednesday), 
				context.getResources().getString(R.string.mythursday), context.getResources().getString(R.string.myfriday), 
				context.getResources().getString(R.string.mysaturday) };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}
}
