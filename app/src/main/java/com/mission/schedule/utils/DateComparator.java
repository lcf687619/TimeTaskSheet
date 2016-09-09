package com.mission.schedule.utils;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class DateComparator implements Comparator<Map<String, String>> {

	@Override
	public int compare(Map<String, String> o1, Map<String, String> o2) {
		String date = o1.get("calendar");
		String date1 = o2.get("calendar");
		Date d1 = null;
		Date d2 = null;
		d1 = DateUtil.parseDate(date);
		d2 = DateUtil.parseDate(date1);
		if (d1.before(d2)) {
			return -1;
		} else if (d1.after(d2)) {
			return 1;
		} else {
			return 0;
		}
	}
}
