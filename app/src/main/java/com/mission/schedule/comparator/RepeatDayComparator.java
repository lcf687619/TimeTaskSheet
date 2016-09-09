package com.mission.schedule.comparator;

import java.util.Comparator;

import com.mission.schedule.bean.FriendsChongFuBean;

public class RepeatDayComparator implements Comparator<FriendsChongFuBean> {
	@Override
	public int compare(FriendsChongFuBean o1, FriendsChongFuBean o2) {
		String date = o1.repTypeParameter.replace("[", "")
				.replace("]", "").replace("\"", "");
		String date1 = o2.repTypeParameter.replace("[", "")
				.replace("]", "").replace("\"", "");
		int d1 = Integer.parseInt(date) ;
		int d2 = Integer.parseInt(date1);
		if (d1 > d2) {
			return 1;
		} else if (d1<d2) {
			return -1;
		} else {
			return 0;
		}
	}
}
