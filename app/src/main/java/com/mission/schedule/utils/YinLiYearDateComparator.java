package com.mission.schedule.utils;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import com.mission.schedule.entity.CLRepeatTable;

public class YinLiYearDateComparator implements Comparator<Map<String, String>> {

	@Override
	public int compare(Map<String, String> o1, Map<String, String> o2) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		String date = o1.get(CLRepeatTable.parReamrk).replace("[", "")
				.replace("]", "").replace("\"", "");
		String date1 = o2.get(CLRepeatTable.parReamrk).replace("[", "")
				.replace("]", "").replace("\"", "");

		Date d1 = null;
		Date d2 = null;
		for (String k : o1.keySet()) {
			try {
				d1 = format.parse(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (String k : o2.keySet()) {
			try {
				d2 = format.parse(date1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (d1.before(d2)) {
			return -1;
		} else if (d1.after(d2)) {
			return 1;
		} else {
			return 0;
		}
	}
}
