package com.mission.schedule.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.bean.FriendsRiChengBean;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendsChongFuAdapter extends CommonAdapter<FriendsRiChengBean> {

	List<FriendsRiChengBean> mList;
	private Handler handler;

	public FriendsChongFuAdapter(Context context,
			List<FriendsRiChengBean> lDatas, int layoutItemID, Handler handler) {
		super(context, lDatas, layoutItemID);
		this.mList = lDatas;
		this.handler = handler;
	}

	private void setOnclick(int positions, FriendsRiChengBean item, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = item;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public void getViewItem(
			com.mission.schedule.adapter.utils.ViewHolder holder,
			final FriendsRiChengBean item, final int position) {
		LinearLayout bottomitem_ll = holder.getView(R.id.bottomitem_ll);
		TextView timeall_tv_daycount = holder.getView(R.id.timeall_tv_daycount);
		TextView timeall_tv_month = holder.getView(R.id.timeall_tv_month);
		RelativeLayout date_all = (RelativeLayout) holder
				.getView(R.id.date_all);
		TextView new_time_data = (TextView) holder.getView(R.id.date_new_time);		
		TextView content_tv = (TextView) holder.getView(R.id.content_tv);
		View item_view1 = holder.getView(R.id.item_view1);

		bottomitem_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, item, 2);
			}
		});

		String yesterday, today, tomorrow;
		String week = "";
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday = DateUtil.formatDate(calendar.getTime());
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());
		Date dateStr = DateUtil.parseDate(item.CDate);
		Date dateToday = DateUtil.parseDate(today);
		long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
		long day = betweem / (24 * 3600);

		week = CharacterUtil.getWeekOfDate(context,
				DateUtil.parseDate(item.CDate));
		Date dateTime = DateUtil.parseDateTime(item.CDate + " " + item.CTime);
		Date date = DateUtil
				.parseDateTime((DateUtil.formatDateTime(new Date())));
		if (position == 0) {
			date_all.setVisibility(View.VISIBLE);
			item_view1.setVisibility(View.VISIBLE);
//			if(mList!=null&&mList.size()>=2){
//				if(mList.get(position).CDate
//						.equals(mList.get(position + 1).CDate)){
//					item_view1.setVisibility(View.VISIBLE);
//				}else{
//					item_view1.setVisibility(View.GONE);
//				}
//			}
			if (today
					.equals(DateUtil.formatDate(DateUtil.parseDate(item.CDate)))) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_today));
				timeall_tv_month.setText("   "+item.CDate.substring(5, 10));
				timeall_tv_month.setTextColor(context.getResources().getColor(R.color.sunday_txt));
				new_time_data.setText(week);
			} else if (tomorrow.equals(DateUtil.formatDate(DateUtil
					.parseDate(item.CDate)))) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_tomorrow));
				timeall_tv_month.setText("   "+item.CDate.substring(5, 10));
				new_time_data.setText(week );
			} else if (yesterday.equals(DateUtil.formatDate(DateUtil
					.parseDate(item.CDate)))) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_yesterday));
				timeall_tv_month.setText("   "+item.CDate.substring(5, 10));
				timeall_tv_month.setTextColor(context.getResources().getColor(R.color.gongkai_txt));
				new_time_data.setText(week );
			} else {
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_month.setText(item.CDate.substring(5, 10).trim());
				if (dateTime.getTime() > date.getTime()) {
					new_time_data.setText(week + "   "
							+ Math.abs(day) + "天后");
					timeall_tv_month.setTextColor(context.getResources().getColor(R.color.mingtian_color));
				} else {
					timeall_tv_month.setTextColor(context.getResources().getColor(R.color.gongkai_txt));
					new_time_data.setText(week + "   "
							+ Math.abs(day) + "天前");
				}
			}
		} else if (mList.get(position).CDate
				.equals(mList.get(position - 1).CDate)) {
			date_all.setVisibility(View.GONE);
			item_view1.setVisibility(View.GONE);
//			if(mList!=null&&mList.size()>=position+2){
//				if(mList.get(position).CDate
//						.equals(mList.get(position + 1).CDate)){
//					item_view1.setVisibility(View.VISIBLE);
//				}else{
//					item_view1.setVisibility(View.GONE);
//				}
//			}
		} else {
			date_all.setVisibility(View.VISIBLE);
			item_view1.setVisibility(View.VISIBLE);
//			if(mList!=null&&mList.size()>=position+2){
//				if(mList.get(position).CDate
//						.equals(mList.get(position + 1).CDate)){
//					item_view1.setVisibility(View.VISIBLE);
//				}else{
//					item_view1.setVisibility(View.GONE);
//				}
//			}
			if (today
					.equals(DateUtil.formatDate(DateUtil.parseDate(item.CDate)))) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_today));
				timeall_tv_month.setText("   "+item.CDate.substring(5, 10));
				timeall_tv_daycount.setTextColor(context.getResources().getColor(R.color.sunday_txt));
				timeall_tv_month.setTextColor(context.getResources().getColor(R.color.sunday_txt));
				new_time_data.setText(week );
			} else if (tomorrow.equals(DateUtil.formatDate(DateUtil
					.parseDate(item.CDate)))) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_tomorrow));
				timeall_tv_daycount.setTextColor(context.getResources().getColor(R.color.mingtian_color));
				timeall_tv_month.setText("   "+item.CDate.substring(5, 10));
				new_time_data.setText(week);
			} else if (yesterday.equals(DateUtil.formatDate(DateUtil
					.parseDate(item.CDate)))) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_yesterday));
				timeall_tv_month.setText("   "+item.CDate.substring(5, 10));
				timeall_tv_daycount.setTextColor(context.getResources().getColor(R.color.gongkai_txt));
				timeall_tv_month.setTextColor(context.getResources().getColor(R.color.gongkai_txt));
				new_time_data.setText(week );
			} else {
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_month.setText(item.CDate.substring(5, 10).trim());
				if (dateTime.getTime() > date.getTime()) {
					new_time_data.setText(week + "   "
							+ Math.abs(day) + "天后");
					timeall_tv_month.setTextColor(context.getResources().getColor(R.color.mingtian_color));
				} else {
					timeall_tv_month.setTextColor(context.getResources().getColor(R.color.gongkai_txt));
					new_time_data.setText(week + "   "
							+ Math.abs(day) + "天前");
				}
			}
		}

		if(item.CTime.trim()!=null&&!item.CTime.trim().equals("")&&!item.CTime.trim().equals("null")){
			content_tv.setText(item.CTime.trim()+"  "+item.CContent.trim());			
		}else{
			content_tv.setText(item.CContent.trim());	
		}

	}
}
