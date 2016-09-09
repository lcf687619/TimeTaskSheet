package com.mission.schedule.comparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.mission.schedule.bean.FriendsChongFuBean;

public class RepeatYearComparator implements Comparator<FriendsChongFuBean> {

	@Override
	public int compare(FriendsChongFuBean o1, FriendsChongFuBean o2) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		String date = o1.repTypeParameter.replace("[", "").replace("]", "").replace("\"", "");
		String date1 = o2.repTypeParameter.replace("[", "").replace("]", "").replace("\"", "");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(date);
			d2 = format.parse(date1);
		} catch (ParseException e) {
			e.printStackTrace();
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
