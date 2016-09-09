package com.mission.schedule.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.applcation.App;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.onRightViewWidthListener;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TagAllSchAdapter extends CommonAdapter<Map<String, String>>
		implements onRightViewWidthListener {

	private Context context;
	private List<Map<String, String>> mList;
	private Handler handler;
	private SwipeXListViewNoHead swipeXlistview;
	int width;
	String tagname;

	public TagAllSchAdapter(Context context, List<Map<String, String>> lDatas,
			int layoutItemID, Handler handler,
			SwipeXListViewNoHead swipeXlistview, int width, String tagname) {
		super(context, lDatas, layoutItemID);
		this.context = context;
		this.mList = lDatas;
		this.handler = handler;
		this.swipeXlistview = swipeXlistview;
		this.width = width;
		this.tagname = tagname;
		swipeXlistview.setonRightViewWidthListener(this);
	}

	private void setOnclick(int positions, Map<String, String> map, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = map;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public void onRightViewWidth(int position) {
		swipeXlistview.setRightViewWidth(context.getResources()
				.getDimensionPixelSize(R.dimen.friends_item_80));
	}

	private int measureTextViewHeight(String text, int textSize, int deviceWidth) {
		TextView textView = new TextView(context);
		textView.setText(text);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth,
				MeasureSpec.AT_MOST);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED);
		textView.measure(widthMeasureSpec, heightMeasureSpec);
		return textView.getMeasuredHeight();
	}

	@SuppressLint("NewApi")
	@Override
	public void getViewItem(ViewHolder holder, final Map<String, String> mMap,
			final int position) {
		LinearLayout top_ll = holder.getView(R.id.top_ll);
		LinearLayout timeall_ll = holder.getView(R.id.timeall_ll); // 标题栏，包含今天，日期，农历等
		TextView timeall_tv_month = holder.getView(R.id.timeall_tv_month); // 日期
		// 如：08-31
		TextView timeall_tv_week = holder.getView(R.id.timeall_tv_week); // 周几
																			// 如：周二
		TextView timeall_tv_daycount = holder.getView(R.id.timeall_tv_daycount); // 今天，明天，几天后
		RelativeLayout timeall_rela_right = holder
				.getView(R.id.timeall_rela_right); // 下面整体布局
		TextView timeall_tv_time = holder.getView(R.id.timeall_tv_time); // 时间
		// 如：全天或08：00
		TextView timeall_tv_shun = holder.getView(R.id.timeall_tv_shun); // 顺延
																			// 如：顺
		TextView timeall_tv_lastday = holder.getView(R.id.timeall_tv_lastday); // 多少小时后，多少天后
		TextView timeall_tv_content = holder.getView(R.id.timeall_tv_content); // 日程内容
		// TextView timeall_tv_has = holder.getTimeall_tv_has(); // 标题栏天后
		TextView personstate_tv = holder.getView(R.id.personstate_tv);// 公开状态
		TextView tv_delete = holder.getView(R.id.tv_delete);// 删除
		ImageView guoqi_img = holder.getView(R.id.guoqi_img);// 过期未结束图标，在今天中才显示
		TextView comename_tv = holder.getView(R.id.comename_tv);// 来自谁的日程
		LinearLayout delete_ll = holder.getView(R.id.delete_ll);// 删除布局
		LinearLayout datebackground_ll = holder.getView(R.id.datebackground_ll);// 今天和日期的背景
		CheckBox select_cb = holder.getView(R.id.select_cb);
		TextView timeall_tv_nongli = holder.getView(R.id.timeall_tv_nongli);// 农历日期显示
		TextView bottom_month = holder.getView(R.id.bottom_month);
		TextView bottom_week = holder.getView(R.id.bottom_week);
		LinearLayout select_cb_ll = holder.getView(R.id.select_cb_ll);
		TextView timeall_after = holder.getView(R.id.timeall_after);

		timeall_rela_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击详情菜单
				setOnclick(position, mMap, 0);
			}
		});
		tv_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 右滑删除
				setOnclick(position, mMap, 3);
			}
		});
		select_cb_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 选中表示结束
				setOnclick(position, mMap, 2);
			}
		});
		select_cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 选中表示结束
				setOnclick(position, mMap, 2);
			}
		});
		String displayAlarm = mMap.get(ScheduleTable.schDisplayTime);
		String postpone = mMap.get(ScheduleTable.schIsPostpone);
		String alarmClockTime = mMap.get(ScheduleTable.schTime);
		String isEnd = mMap.get(ScheduleTable.schIsEnd);
		int beforTime = Integer.parseInt(mMap.get(ScheduleTable.schBeforeTime));
		String important = mMap.get(ScheduleTable.schIsImportant);

		String yestoday, today, tomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) - 1);
		yestoday = DateUtil.formatDate(calendar.getTime());
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());

		String key = mMap.get(ScheduleTable.schDate);
		String timeKey = mMap.get(ScheduleTable.schTime);

		Date dateStr = DateUtil.parseDateTime(key + " " + timeKey);
		String[] ymd = key.split("-");

		String clockTime = "";
		String point = "";
		String sequence = "";
		int shunBackKuang = 0;
		String colorState = context.getResources().getColor(R.color.mingtian_color)+"";
		String beforeStr = "";

		if (beforTime == 0) {
			beforeStr = "0";
		} else if (beforTime == 5) {
			beforeStr = "-5";
		} else if (beforTime == 15) {
			beforeStr = "-15";
		} else if (beforTime == 30) {
			beforeStr = "-30";
		} else if (beforTime == 60) {
			beforeStr = "-1h";
		} else if (beforTime == 120) {
			beforeStr = "-2h";
		} else if (beforTime == 1440) {
			beforeStr = "-1d";
		} else if (beforTime == 2 * 1440) {
			beforeStr = "-2d";
		} else if (beforTime == 7 * 1440) {
			beforeStr = "-1w";
		} else {
			beforeStr = "0";
		}

		if ("1".equals(isEnd)) {
			colorState = "#7F7F7F";// "#9f9f9f";
			shunBackKuang = R.drawable.tv_kuang_before;
		} else {
			if (today.substring(0, 10).equals(key)) {
				colorState = ""
						+ context.getResources().getColor(
								R.color.mingtian_color);
			} else if (tomorrow.substring(0, 10).equals(key)) {
				colorState = ""
						+ context.getResources().getColor(
								R.color.mingtian_color);
			} else {
				colorState = ""
						+ context.getResources().getColor(
								R.color.mingtian_color);
			}

			shunBackKuang = R.drawable.tv_kuang_aftertime;
		}
		if (beforeStr.equals("0")) {
			clockTime = "<font color='" + colorState + "' size='5px'>"
					+ alarmClockTime + "</font>";
		} else {
			clockTime = "<font color='" + colorState + "' size='5px'>"
					+ alarmClockTime + "(" + beforeStr + ")" + "</font>";
		}
		// clockTime = "<font color='" + colorState + "' size='5px'>"
		// + alarmClockTime + "(" + beforeStr + ")" + "</font>";
		// 全天
		point = "<font color='" + colorState + "' size='5px'>"
				+ context.getString(R.string.adapter_allday) + "</font>";
		// 顺延
		sequence = "<font color='" + colorState + "'>"
				+ context.getString(R.string.adapter_shun) + "</font>";

		timeall_tv_content.setText(mMap.get(ScheduleTable.schContent));
		if ("1".equals(mMap.get(ScheduleTable.schIsEnd))) {
			timeall_tv_content.setTextColor(Color.parseColor(colorState));
			timeall_tv_time.setTextColor(Color.parseColor(colorState));
			bottom_month.setTextColor(Color.parseColor(colorState));
			bottom_week.setTextColor(Color.parseColor(colorState));
			timeall_tv_content.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		} else {
			timeall_tv_time.setTextColor(context.getResources().getColor(
					R.color.mingtian_color));
			bottom_month.setTextColor(context.getResources().getColor(
					R.color.mingtian_color));
			bottom_week.setTextColor(context.getResources().getColor(
					R.color.mingtian_color));
			timeall_tv_content.setTextColor(Color.BLACK);
			timeall_tv_content.getPaint().setFlags(0 | Paint.ANTI_ALIAS_FLAG);
		}

		personstate_tv.setText(tagname);

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(DateUtil.parseDate(mMap.get(ScheduleTable.schDate)));

		Date dateToday = DateUtil.parseDateTime(DateUtil
				.formatDateTime(new Date()));
		long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
		long day = betweem / (24 * 3600);
		long hour = betweem % (24 * 3600) / 3600;
		long min = betweem % 3600 / 60;
		long daystr = ((long) (DateUtil.parseDate(
				DateUtil.formatDate(new Date())).getTime() - DateUtil
				.parseDate(key).getTime()) / 1000) / (24 * 3600);
		if (position == 0) {// 当position=0，判断今天中是否有待办日程，有就隐藏标题栏，隐藏顺和天后，如果今天没有日程，然后判断明天和之后的
			timeall_ll.setVisibility(View.VISIBLE);
			if (DateUtil.parseDate(key).before(DateUtil.parseDate(yestoday))) {
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_after.setVisibility(View.VISIBLE);
				guoqi_img.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_twodayago));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_after.setText(Math.abs(daystr) + "天前");
				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else if (yestoday.equals(key)) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_after.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_yesterday));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_lastday.setText("1天前");
				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else if (today.equals(key)) {// &&
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_after.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				// timeall_tv_has.setVisibility(View.GONE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_today));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_todaytextview);

				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if ("0".equals(displayAlarm)) {
					timeall_tv_lastday.setVisibility(View.GONE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
					Date date1 = DateUtil.parseDateTime((DateUtil
							.formatDateTime(new Date())));
					if (dateStr.getTime() < date1.getTime()) {
						guoqi_img.setVisibility(View.VISIBLE);
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						guoqi_img.setVisibility(View.GONE);
						timeall_tv_lastday.setVisibility(View.VISIBLE);
					}
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(1 + "天后");
				} else if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (DateUtil.parseDateTimeHm(timeKey).getTime() > DateUtil
						.parseDateTimeHm(DateUtil.formatDateTimeHm(new Date()))
						.getTime()) {
					if (Math.abs(min) == 0) {
						timeall_tv_lastday.setText("");
					} else {
						timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
					}
				}
			} else if (tomorrow.equals(key)) {
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_after.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				// timeall_tv_has.setVisibility(View.GONE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_tomorrow));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_lastday.setText("2天后");
				if ("0".equals(displayAlarm)) {
					timeall_tv_lastday.setVisibility(View.GONE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					timeall_tv_lastday.setVisibility(View.VISIBLE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(1 + "天后");
				} else if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (DateUtil.parseDateTimeHm(timeKey).getTime() > DateUtil
						.parseDateTimeHm(DateUtil.formatDateTimeHm(new Date()))
						.getTime()) {
					if (Math.abs(min) == 0) {
						timeall_tv_lastday.setText("");
					} else {
						timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
					}
				}
			} else {
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_after.setVisibility(View.VISIBLE);
				guoqi_img.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.GONE);

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_outweek));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_after.setText(Math.abs(daystr) + "天后");
				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			}
		} else if (mList.get(position).get(ScheduleTable.schDate)
				.equals(mList.get(position - 1).get(ScheduleTable.schDate))) {
			if (DateUtil.parseDate(key).before(DateUtil.parseDate(yestoday))) {
				if (mList
						.get(position)
						.get(ScheduleTable.schDate)
						.equals(mList.get(position - 1).get(
								ScheduleTable.schDate))) {
					timeall_after.setVisibility(View.GONE);
					timeall_tv_month.setVisibility(View.GONE);
					timeall_tv_week.setVisibility(View.GONE);
					timeall_tv_nongli.setVisibility(View.GONE);
					timeall_ll.setVisibility(View.GONE);
				} else {
					timeall_after.setVisibility(View.VISIBLE);
					timeall_tv_month.setVisibility(View.VISIBLE);
					timeall_tv_week.setVisibility(View.VISIBLE);
					timeall_tv_nongli.setVisibility(View.VISIBLE);
					timeall_ll.setVisibility(View.VISIBLE);
				}
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_after.setText(Math.abs(daystr) + "天前");

				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));

				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else if (yestoday.equals(key)) {
				timeall_tv_daycount.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.GONE);
				timeall_tv_week.setVisibility(View.GONE);
				timeall_tv_nongli.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_after.setVisibility(View.GONE);
				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else if (today.equals(key)) {// &&
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_after.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.GONE);
				timeall_tv_week.setVisibility(View.GONE);
				timeall_tv_nongli.setVisibility(View.GONE);
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (DateUtil.parseDateTimeHm(timeKey).getTime() > DateUtil
						.parseDateTimeHm(DateUtil.formatDateTimeHm(new Date()))
						.getTime()) {
					if (Math.abs(min) == 0) {
						timeall_tv_lastday.setText("");
					} else {
						timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
					}
				} else {
					if (Math.abs(min) == 0) {
						timeall_tv_lastday.setText("");
					} else {
						timeall_tv_lastday.setText(Math.abs(min) + "分钟前");
					}
				}
				if ("0".equals(displayAlarm)) {
					timeall_tv_lastday.setVisibility(View.GONE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
					Date date1 = DateUtil.parseDateTime((DateUtil
							.formatDateTime(new Date())));
					if (dateStr.getTime() < date1.getTime()) {
						guoqi_img.setVisibility(View.VISIBLE);
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						guoqi_img.setVisibility(View.GONE);
						timeall_tv_lastday.setVisibility(View.VISIBLE);
					}
				}
			} else if (tomorrow.equals(key)) {
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_after.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				if ("0".equals(displayAlarm)) {
					timeall_tv_lastday.setVisibility(View.GONE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					timeall_tv_lastday.setVisibility(View.VISIBLE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(1 + "天后");
				} else if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (DateUtil.parseDateTimeHm(timeKey).getTime() > DateUtil
						.parseDateTimeHm(DateUtil.formatDateTimeHm(new Date()))
						.getTime()) {
					if (Math.abs(min) == 0) {
						timeall_tv_lastday.setText("");
					} else {
						timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
					}
				}
			} else {
				if (mList
						.get(position)
						.get(ScheduleTable.schDate)
						.equals(mList.get(position - 1).get(
								ScheduleTable.schDate))) {
					timeall_after.setVisibility(View.GONE);
					timeall_tv_month.setVisibility(View.GONE);
					timeall_tv_week.setVisibility(View.GONE);
					timeall_tv_nongli.setVisibility(View.GONE);
					timeall_ll.setVisibility(View.GONE);
				} else {
					timeall_after.setVisibility(View.VISIBLE);
					timeall_tv_month.setVisibility(View.VISIBLE);
					timeall_tv_week.setVisibility(View.VISIBLE);
					timeall_tv_nongli.setVisibility(View.VISIBLE);
					timeall_ll.setVisibility(View.VISIBLE);
				}
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_after.setText(Math.abs(daystr) + "天后");

				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_inweek));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			}
		} else {
			timeall_ll.setVisibility(View.VISIBLE);
			if (DateUtil.parseDate(key).before(DateUtil.parseDate(yestoday))) {
				if (mList
						.get(position)
						.get(ScheduleTable.schDate)
						.equals(mList.get(position - 1).get(
								ScheduleTable.schDate))) {
					timeall_after.setVisibility(View.GONE);
					timeall_tv_month.setVisibility(View.GONE);
					timeall_tv_week.setVisibility(View.GONE);
					timeall_tv_nongli.setVisibility(View.GONE);
					timeall_ll.setVisibility(View.GONE);
				} else {
					timeall_after.setVisibility(View.VISIBLE);
					timeall_tv_month.setVisibility(View.VISIBLE);
					timeall_tv_week.setVisibility(View.VISIBLE);
					timeall_tv_nongli.setVisibility(View.VISIBLE);
					timeall_ll.setVisibility(View.VISIBLE);
				}
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_after.setText(Math.abs(daystr) + "天前");

				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_inweek));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else if (yestoday.equals(key)) {
				timeall_after.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_yesterday));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else if (today.equals(key)) {// &&
				timeall_after.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				// timeall_tv_has.setVisibility(View.GONE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_today));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_todaytextview);

				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if ("0".equals(displayAlarm)) {
					timeall_tv_lastday.setVisibility(View.GONE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
					Date date1 = DateUtil.parseDateTime((DateUtil
							.formatDateTime(new Date())));
					if (dateStr.getTime() < date1.getTime()) {
						guoqi_img.setVisibility(View.VISIBLE);
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						guoqi_img.setVisibility(View.GONE);
						timeall_tv_lastday.setVisibility(View.VISIBLE);
					}
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(1 + "天后");
				} else if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (DateUtil.parseDateTimeHm(timeKey).getTime() > DateUtil
						.parseDateTimeHm(DateUtil.formatDateTimeHm(new Date()))
						.getTime()) {
					if (Math.abs(min) == 0) {
						timeall_tv_lastday.setText("");
					} else {
						timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
					}
				}
			} else if (tomorrow.equals(key)) {
				timeall_after.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				// timeall_tv_has.setVisibility(View.GONE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_tomorrow));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if ("0".equals(displayAlarm)) {
					timeall_tv_lastday.setVisibility(View.GONE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					timeall_tv_lastday.setVisibility(View.VISIBLE);
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(1 + "天后");
				} else if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (DateUtil.parseDateTimeHm(timeKey).getTime() > DateUtil
						.parseDateTimeHm(DateUtil.formatDateTimeHm(new Date()))
						.getTime()) {
					if (Math.abs(min) == 0) {
						timeall_tv_lastday.setText("");
					} else {
						timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
					}
				}
			} else {
				guoqi_img.setVisibility(View.GONE);
				if (mList
						.get(position)
						.get(ScheduleTable.schDate)
						.equals(mList.get(position - 1).get(
								ScheduleTable.schDate))) {
					timeall_after.setVisibility(View.GONE);
					timeall_tv_month.setVisibility(View.GONE);
					timeall_tv_week.setVisibility(View.GONE);
					timeall_tv_nongli.setVisibility(View.GONE);
					timeall_ll.setVisibility(View.GONE);
				} else {
					timeall_after.setVisibility(View.VISIBLE);
					timeall_tv_month.setVisibility(View.VISIBLE);
					timeall_tv_week.setVisibility(View.VISIBLE);
					timeall_tv_nongli.setVisibility(View.VISIBLE);
					timeall_ll.setVisibility(View.VISIBLE);
				}

				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				guoqi_img.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_after.setText(Math.abs(daystr) + "天后");

				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));

				if ("0".equals(displayAlarm)) {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			}
		}

		if (timeall_tv_nongli.getVisibility() == View.VISIBLE) {
			if (mList.size() > 0) {
				String nongli = App
						.getDBcApplication()
						.queryLunartoSolarList(mMap.get(ScheduleTable.schDate),
								0).get("lunarCalendar");
				timeall_tv_nongli.setText(nongli);
			}
		}

		// 设置时间距离前一个的距离
		LinearLayout.LayoutParams timeparams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (bottom_month.getVisibility() == View.VISIBLE) {
			timeparams.setMargins(Utils.dipTopx(context, 5), 0, 0, 0);
			timeall_tv_time.setLayoutParams(timeparams);
		}
		// 设置右边删除
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				Utils.dipTopx(context, 80), LayoutParams.WRAP_CONTENT);
		// 设置下面布局
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置距离顶部距离
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		int heght = Utils.pxTodip(
				context,
				measureTextViewHeight(mMap.get(ScheduleTable.schContent),
						Utils.dipTopx(context, 17),
						width - Utils.dipTopx(context, 80)));
		if (heght < 30) {
			params.height = Utils.dipTopx(context, 70);
			params1.height = Utils.dipTopx(context, 70);
		} else {
			params.height = Utils.dipTopx(context, 90);
			params1.height = Utils.dipTopx(context, 90);
		}
		if (timeall_ll.getVisibility() == View.VISIBLE) {
			params.setMargins(0, Utils.dipTopx(context, 67), 0, 0);
			params1.setMargins(0, Utils.dipTopx(context, 10), 0, 0);
			params2.setMargins(Utils.dipTopx(context, 8),
					Utils.dipTopx(context, 30), Utils.dipTopx(context, 8), 0);
			delete_ll.setLayoutParams(params);
			top_ll.setLayoutParams(params2);
		} else {
			params.setMargins(0, Utils.dipTopx(context, 10), 0, 0);
			params2.setMargins(Utils.dipTopx(context, 8),
					Utils.dipTopx(context, 10), Utils.dipTopx(context, 8), 0);
			delete_ll.setLayoutParams(params);
			top_ll.setLayoutParams(params2);
		}
		timeall_rela_right.setLayoutParams(params1);
		if ("1".equals(mMap.get(ScheduleTable.schRead))) {
			timeall_rela_right.setBackground(context.getResources()
					.getDrawable(R.drawable.bg_sch_normal));
		} else if ("1".equals(important)) {
			timeall_rela_right.setBackground(context.getResources()
					.getDrawable(R.drawable.bg_sch_important));
		} else {
			timeall_rela_right.setBackground(context.getResources()
					.getDrawable(R.drawable.bg_sch_normal));
		}

		String date;
		if ("0".equals(isEnd) && "1".equals(postpone)) {
			if (DateUtil.parseDate(mMap.get(ScheduleTable.schDate)).before(
					DateUtil.parseDate(DateUtil.formatDate(new Date())))) {
				calendar1.set(Calendar.DAY_OF_MONTH,
						calendar1.get(Calendar.DAY_OF_MONTH) + 1);
				date = DateUtil.formatDate(calendar1.getTime());
				App.getDBcApplication().updateScheduleDateData(
						Integer.parseInt(mMap.get(ScheduleTable.schID)), date,
						mMap.get(ScheduleTable.schTime));
			}
		}
		if ("1".equals(isEnd)) {
			select_cb.setChecked(true);
			if ("1".equals(mMap.get(ScheduleTable.schIsImportant))) {
				timeall_rela_right.setBackground(context.getResources()
						.getDrawable(R.drawable.bg_sch_important_end));
			} else {
				timeall_rela_right.setBackground(context.getResources()
						.getDrawable(R.drawable.bg_sch_end));
			}
			comename_tv.setTextColor(context.getResources().getColor(
					R.color.gongkai_txt));
			if (!"0".equals(StringUtils.getIsStringEqulesNull(mMap
					.get(ScheduleTable.schcRecommendId)))) {
				if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(ScheduleTable.schcRecommendName)))) {
					comename_tv.setVisibility(View.VISIBLE);
					comename_tv.setText(" " + "来自" + " "
							+ mMap.get(ScheduleTable.schcRecommendName));
				} else {
					if (!"0".equals(mMap.get(ScheduleTable.schRepeatID))) {
						String type = App
								.getDBcApplication()
								.queryRepateType(
										mMap.get(ScheduleTable.schRepeatID))
								.get("type");
						if ("0".equals(mMap.get(ScheduleTable.schRepeatLink))) {
							comename_tv.setVisibility(View.GONE);
						} else {
							comename_tv.setVisibility(View.VISIBLE);
						}
						comename_tv.setTextColor(context.getResources()
								.getColor(R.color.gongkai_txt));
						if ("1".equals(type)) {
							comename_tv.setText(" 每天重复");
						} else if ("2".equals(type)) {
							comename_tv.setText(" 每周重复");
						} else if ("3".equals(type)) {
							comename_tv.setText(" 每月重复");
						} else if ("4".equals(type) || "6".equals(type)) {
							comename_tv.setText(" 每年重复");
						} else {
							comename_tv.setText(" 工作日重复");
						}
					} else {
						comename_tv.setVisibility(View.GONE);
					}
				}
			} else if (!"0".equals(mMap.get(ScheduleTable.schRepeatID))) {
				String type = App.getDBcApplication()
						.queryRepateType(mMap.get(ScheduleTable.schRepeatID))
						.get("type");
				if ("0".equals(mMap.get(ScheduleTable.schRepeatLink))) {
					comename_tv.setVisibility(View.GONE);
				} else {
					comename_tv.setVisibility(View.VISIBLE);
				}
				if ("1".equals(type)) {
					comename_tv.setText(" 每天重复");
				} else if ("2".equals(type)) {
					comename_tv.setText(" 每周重复");
				} else if ("3".equals(type)) {
					comename_tv.setText(" 每月重复");
				} else if ("4".equals(type) || "6".equals(type)) {
					comename_tv.setText(" 每年重复");
				} else {
					comename_tv.setText(" 工作日重复");
				}
			}
		} else {
			if ("1".equals(important)) {
				timeall_rela_right.setBackground(context.getResources()
						.getDrawable(R.drawable.bg_sch_important));
			} else {
				timeall_rela_right.setBackground(context.getResources()
						.getDrawable(R.drawable.bg_sch_normal));
			}
			select_cb.setChecked(false);

			if (!"0".equals(StringUtils.getIsStringEqulesNull(mMap
					.get(ScheduleTable.schcRecommendId)))) {
				if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(ScheduleTable.schcRecommendName)))) {
					comename_tv.setVisibility(View.VISIBLE);
					comename_tv.setText(" " + "来自" + " "
							+ mMap.get(ScheduleTable.schcRecommendName));
					if ("1".equals(isEnd)) {
						comename_tv.setTextColor(context.getResources().getColor(
								R.color.gongkai_txt));
					} else {
						comename_tv.setTextColor(context.getResources().getColor(
								R.color.mingtian_color));
					}
				} else {
					if (!"0".equals(mMap.get(ScheduleTable.schRepeatID))) {
						String type = App
								.getDBcApplication()
								.queryRepateType(
										mMap.get(ScheduleTable.schRepeatID))
								.get("type");
						if ("0".equals(mMap.get(ScheduleTable.schRepeatLink))) {
							comename_tv.setVisibility(View.GONE);
						} else {
							comename_tv.setVisibility(View.VISIBLE);
						}
						comename_tv.setTextColor(context.getResources()
								.getColor(R.color.gongkai_txt));
						if ("1".equals(type)) {
							comename_tv.setText(" 每天重复");
						} else if ("2".equals(type)) {
							comename_tv.setText(" 每周重复");
						} else if ("3".equals(type)) {
							comename_tv.setText(" 每月重复");
						} else if ("4".equals(type) || "6".equals(type)) {
							comename_tv.setText(" 每年重复");
						} else {
							comename_tv.setText(" 工作日重复");
						}
					} else {
						comename_tv.setVisibility(View.GONE);
					}
				}
			} else {
				if (!"0".equals(mMap.get(ScheduleTable.schRepeatID))) {
					String type = App
							.getDBcApplication()
							.queryRepateType(
									mMap.get(ScheduleTable.schRepeatID))
							.get("type");
					if ("0".equals(mMap.get(ScheduleTable.schRepeatLink))) {
						comename_tv.setVisibility(View.GONE);
					} else {
						comename_tv.setVisibility(View.VISIBLE);
					}
					comename_tv.setTextColor(context.getResources().getColor(
							R.color.gongkai_txt));
					if ("1".equals(type)) {
						comename_tv.setText(" 每天重复");
					} else if ("2".equals(type)) {
						comename_tv.setText(" 每周重复");
					} else if ("3".equals(type)) {
						comename_tv.setText(" 每月重复");
					} else if ("4".equals(type) || "6".equals(type)) {
						comename_tv.setText(" 每年重复");
					} else {
						comename_tv.setText(" 工作日重复");
					}
					if ("1".equals(important)) {
						timeall_rela_right.setBackground(context.getResources()
								.getDrawable(R.drawable.bg_sch_important));
					} else {
						timeall_rela_right.setBackground(context.getResources()
								.getDrawable(R.drawable.bg_sch_normal));
					}
				} else {
					comename_tv.setVisibility(View.GONE);
				}
			}
		}
	}
}
