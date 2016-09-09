package com.mission.schedule.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.fragment.YangLiCalendarFragment;
import com.mission.schedule.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarGridViewAdapter extends BaseAdapter {

	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历

	public void setSelectedDate(Calendar cal) {
		calSelected = cal;
	}

	private Calendar calToday = Calendar.getInstance(); // 今日
	private int iMonthViewCurrentMonth = 0; // 当前视图月

	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月

		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}

	ArrayList<java.util.Date> titles;

	private ArrayList<java.util.Date> getDates() {

		UpdateStartDateForMonth();

		ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>();

		for (int i = 1; i <= 35; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}

		return alArrayList;
	}

	private Activity activity;
	Resources resources;
	int width;
	Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	private Handler handler;
	List<Integer> list = new ArrayList<Integer>();
	List<String> listdata = new ArrayList<String>();
	String monthday;

	// construct
	public CalendarGridViewAdapter(Activity a, Calendar cal, int width,
			Handler handler,String monthday,List<String> listdata) {
		calStartDate = cal;
		activity = a;
		resources = activity.getResources();
		titles = getDates();
		this.width = width;
		this.handler = handler;
		this.monthday = monthday;
		this.listdata = listdata;
		setChooseData();
	}

	public CalendarGridViewAdapter(Activity a) {
		activity = a;
		resources = activity.getResources();

	}

	public void setChooseData() {
		for (int i = 0; i < titles.size(); i++) {
			if(!"".equals(monthday)){
				if(monthday.equals(DateUtil.formatDateMMDD(titles.get(i)))){
					map.put(i, false);
				}else {
					map.put(i, false);
					if(listdata!=null&&listdata.size()>0){
						for(int j=0;j<listdata.size();j++){
							if(listdata.get(j).equals(DateUtil.formatDateMMDD(titles.get(i)))){
								map.put(i, true);
							}
						}
					}
				}
			}else{
				map.put(i, false);
				if(listdata!=null&&listdata.size()>0){
					for(int j=0;j<listdata.size();j++){
						if(listdata.get(j).equals(DateUtil.formatDateMMDD(titles.get(i)))){
							map.put(i, true);
						}
					}
				}else{
					map.put(i, false);
				}
			}
		}
	}

	public int getCount() {
		return titles.size();
	}

	public Object getItem(int position) {
		return titles.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setClick(int position,Date date,int what) {
		Message message = Message.obtain();
		message.arg1 = position;
		message.obj = date;
		message.what = what;
		handler.sendMessage(message);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout iv = new LinearLayout(activity);
		iv.setId(position + 5000);
		LinearLayout imageLayout = new LinearLayout(activity);
		imageLayout.setOrientation(LinearLayout.HORIZONTAL);
		iv.setGravity(Gravity.CENTER);
		iv.setOrientation(LinearLayout.VERTICAL);
		iv.setBackgroundColor(resources.getColor(R.color.white));

		Date myDate = (Date) getItem(position);
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);

//		if (equalsDate(calToday.getTime(), myDate)) {
//			// 当前日期
//			iv.setBackgroundColor(resources.getColor(R.color.event_center));
//		}
		// 日期开始
		TextView txtDay = new TextView(activity);// 日期
		txtDay.setGravity(Gravity.CENTER);
		txtDay.setTextSize(16);

		int day = myDate.getDate(); // 日期
		// 判断是否是当前月
		if (iMonth == iMonthViewCurrentMonth) {
			txtDay.setText(String.valueOf(day));
			txtDay.setTextColor(resources.getColor(R.color.black));
			txtDay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(map.get(position)){
						map.put(position, false);
						setClick(position, titles.get(position),0);
						for(int i=0;i<list.size();i++){
							if(position==list.get(i)){
								list.remove(i);
							}
						}
						
					}else{
						for (int i = 0; i < list.size(); i++) {
							for (int j = i + 1; j < list.size(); j++) {
								if (list.get(i) == list.get(j)) {
									list.remove(j);
								}
							}
						}
//						if(list.size()<=5){
//							map.put(position, true);
//							setClick(position, titles.get(position),1);
//						}
						if(YangLiCalendarFragment.strlist.size()<5){
							map.put(position, true);
							setClick(position, titles.get(position),1);
						}else {
							setClick(position, titles.get(position),3);
						}
					}
					notifyDataSetChanged();
				}
			});
			if(map.get(position)){
				list.add(position);
				txtDay.setBackgroundColor(resources.getColor(R.color.selection));
				txtDay.setTextColor(resources.getColor(R.color.sunday_txt));
			}else{
				txtDay.setBackgroundColor(resources
						.getColor(R.color.white));
				txtDay.setTextColor(resources.getColor(R.color.black));
			}
		} 
		if(!"".equals(monthday)){
			if(monthday.equals(DateUtil.formatDateMMDD(titles.get(position)))){
				map.put(position, false);
			}
		}
		txtDay.setId(position + 500);
		iv.setTag(myDate);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, width / 7);
		iv.addView(txtDay, lp);
		
		return iv;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

//	private Boolean equalsDate(Date date1, Date date2) {
//
//		if (date1.getMonth() == date2.getMonth()
//				&& date1.getDate() == date2.getDate()) {
//			return true;
//		} else {
//			return false;
//		}
//
//	}

}
