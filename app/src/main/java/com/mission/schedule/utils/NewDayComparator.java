package com.mission.schedule.utils;

import java.util.Comparator;
import java.util.Map;

import com.mission.schedule.entity.CLNFMessage;

public class NewDayComparator implements Comparator<Map<String, String>> {
	@Override
	public int compare(Map<String, String> o1, Map<String, String> o2) {
		String date = o1.get(CLNFMessage.nfmParameter).replace("[", "")
				.replace("]", "").replace("\"", "");
		String date1 = o2.get(CLNFMessage.nfmParameter).replace("[", "")
				.replace("]", "").replace("\"", "");
		int d1 = 0 ;
		int d2 = 0;
		for (String k : o1.keySet()) {
			try {
				d1 = Integer.parseInt(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (String k : o2.keySet()) {
			try {
				d2 = Integer.parseInt(date1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (d1 > d2) {
			return 1;
		} else if (d1<d2) {
			return -1;
		} else {
			return 0;
		}
	}
}
