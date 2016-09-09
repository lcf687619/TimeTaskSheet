package com.mission.schedule.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mission.schedule.bean.FocusOnAllBean;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FocusOnRiChengAdapter extends BaseAdapter {

	Context context;
	List<FocusOnAllBean> mList;
	private LayoutInflater mInflater;
	boolean fag = true;
	boolean fag1 = true;
	boolean fag2 = true;
	int i;
	int j;
	int k;

	public FocusOnRiChengAdapter(Context context, List<FocusOnAllBean> mList) {
		this.context = context;
		this.mList = mList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FocusOnAllBean bean = mList.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_focusonricheng,
					null);

			viewHolder = new ViewHolder();
			viewHolder.date_ll = (RelativeLayout) convertView
					.findViewById(R.id.date_ll);
			viewHolder.date_tv = (TextView) convertView
					.findViewById(R.id.date_tv);
			viewHolder.time_tv = (TextView) convertView
					.findViewById(R.id.time_tv);
			viewHolder.content_tv = (TextView) convertView
					.findViewById(R.id.content_tv);
			viewHolder.friendsname_tv = (TextView) convertView
					.findViewById(R.id.friendsname_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());
		Date dateStr = DateUtil.parseDate(bean.cDate);
		Date dateToday = DateUtil.parseDate(today);
		long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
		long day = betweem / (24 * 3600);

		if (position == 0) {
			viewHolder.date_ll.setVisibility(View.VISIBLE);
			if (today
					.equals(DateUtil.formatDate(DateUtil.parseDate(bean.cDate)))) {
				viewHolder.date_tv.setText("今天");
			} else if (tomorrow.equals(DateUtil.formatDate(DateUtil
					.parseDate(bean.cDate)))) {
				if (fag2) {
					k = position;
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("明天");
					fag2 = false;
				} else {
					fag2 = false;
				}
				if (position == k) {
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("明天");
				} else {
					viewHolder.date_ll.setVisibility(View.GONE);
				}
			} else if (Math.abs(day) <= 7 && Math.abs(day) > 1) {
				if (fag) {
					i = position;
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("一周以内");
					fag = false;
				} else {
					fag = false;
				}
				String str = DateUtil.formatDate(DateUtil.parseDate(mList
						.get(position).cDate));
				Date date1 = DateUtil.parseDate(str);
				Date date2 = DateUtil.parseDate(today.substring(0, 10)
						.toString());
				long between = (long) (date1.getTime() - date2.getTime()) / 1000;
				long day1 = between / (24 * 3600);
				if (Math.abs(day1) <= 7) {
					if (position == i) {
						viewHolder.date_ll.setVisibility(View.VISIBLE);
						viewHolder.date_tv.setText("一周以内");
					} else {
						viewHolder.date_ll.setVisibility(View.GONE);
					}
				}
			} else {
				if (fag1) {
					j = position;
					fag1 = false;
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("一周以后");
				} else {
					fag1 = false;
				}
				String str = DateUtil.formatDate(DateUtil.parseDate(mList
						.get(position).cDate));
				Date date1 = DateUtil.parseDate(str);
				Date date2 = DateUtil.parseDate(today);
				long between = (long) (date1.getTime() - date2.getTime()) / 1000;
				long day1 = between / (24 * 3600);
				if (Math.abs(day1) > 7) {
					if (position == j) {
						viewHolder.date_ll.setVisibility(View.VISIBLE);
						viewHolder.date_tv.setText("一周以后");
					} else {
						viewHolder.date_ll.setVisibility(View.GONE);
					}
				}
			}
		} else if (mList.get(position).cDate
				.equals(mList.get(position - 1).cDate)) {
			viewHolder.date_ll.setVisibility(View.GONE);
		} else {
			if (today
					.equals(DateUtil.formatDate(DateUtil.parseDate(bean.cDate)))) {
				viewHolder.date_tv.setText("今天");
			} else if (tomorrow.equals(DateUtil.formatDate(DateUtil
					.parseDate(bean.cDate)))) {
				if (fag2) {
					k = position;
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("明天");
					fag2 = false;
				} else {
					fag2 = false;
				}
				if (position == k) {
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("明天");
				} else {
					viewHolder.date_ll.setVisibility(View.GONE);
				}
			} else if (Math.abs(day) <= 7 && Math.abs(day) > 1) {
				if (fag) {
					i = position;
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("一周以内");
					fag = false;
				} else {
					fag = false;
				}
				String str = DateUtil.formatDate(DateUtil.parseDate(mList
						.get(position).cDate));
				Date date1 = DateUtil.parseDate(str);
				Date date2 = DateUtil.parseDate(today);
				long between = (long) (date1.getTime() - date2.getTime()) / 1000;
				long day1 = between / (24 * 3600);
				if (Math.abs(day1) <= 7) {
					if (position == i) {
						viewHolder.date_ll.setVisibility(View.VISIBLE);
						viewHolder.date_tv.setText("一周以内");
					} else {
						viewHolder.date_ll.setVisibility(View.GONE);
					}
				}
			} else {
				if (fag1) {
					j = position;
					fag1 = false;
					viewHolder.date_ll.setVisibility(View.VISIBLE);
					viewHolder.date_tv.setText("一周以后");
				} else {
					fag1 = false;
				}
				String str = DateUtil.formatDate(DateUtil.parseDate(mList
						.get(position).cDate));
				Date date1 = DateUtil.parseDate(str);
				Date date2 = DateUtil.parseDate(today);
				long between = (long) (date1.getTime() - date2.getTime()) / 1000;
				long day1 = between / (24 * 3600);
				if (Math.abs(day1) > 7) {
					if (position == j) {
						viewHolder.date_ll.setVisibility(View.VISIBLE);
						viewHolder.date_tv.setText("一周以后");
					} else {
						viewHolder.date_ll.setVisibility(View.GONE);
					}
				}
			}
		}

		viewHolder.time_tv.setText(bean.cDate.subSequence(5, 10) + " "
				+ bean.cTime);
		viewHolder.content_tv.setText(bean.cContent);
		viewHolder.friendsname_tv.setText(bean.cuIckName);

		return convertView;
	}

	static class ViewHolder {
		public RelativeLayout date_ll;
		public TextView date_tv;
		public TextView time_tv;
		public TextView content_tv;
		public TextView friendsname_tv;
	}

}
