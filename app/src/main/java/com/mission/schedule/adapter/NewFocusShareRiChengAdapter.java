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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.applcation.App;
import com.mission.schedule.entity.CLFindScheduleTable;
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

public class NewFocusShareRiChengAdapter extends
		CommonAdapter<Map<String, String>> implements onRightViewWidthListener {

	private Context context;
	private List<Map<String, String>> mList;
	private Handler handler;
	private SwipeXListViewNoHead swipeXlistview;
	int width = 0;

	public NewFocusShareRiChengAdapter(Context context,
			List<Map<String, String>> lDatas, int layoutItemID,
			Handler handler, SwipeXListViewNoHead swipeXlistview, int width) {
		super(context, lDatas, layoutItemID);
		this.context = context;
		this.mList = lDatas;
		this.handler = handler;
		this.swipeXlistview = swipeXlistview;
		this.width = width;
		swipeXlistview.setonRightViewWidthListener(this);
	}

	private void setOnclick(int positions, Map<String, String> map, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = map;
		message.what = what;
		handler.sendMessage(message);
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
		// TextView timeall_tv_has = holder.getView(R.id.timeall_tv_has); //
		// 标题栏天后
		TextView tv_delete = holder.getView(R.id.tv_delete);// 删除
		TextView comename_tv = holder.getView(R.id.comename_tv);// 来自谁的日程
		LinearLayout delete_ll = holder.getView(R.id.delete_ll);// 删除布局
		LinearLayout datebackground_ll = holder.getView(R.id.datebackground_ll);// 今天和日期的背景
		TextView timeall_tv_nongli = holder.getView(R.id.timeall_tv_nongli);// 农历日期显示
		TextView timeall_after = holder.getView(R.id.timeall_after);// 标题栏几天后

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
				setOnclick(position, mMap, 1);
			}
		});
		String postpone = mMap.get(CLFindScheduleTable.fstIsPostpone);
		String alarmClockTime = DateUtil.formatDateTimeHm(DateUtil
				.parseDateTimeHm(mMap.get(CLFindScheduleTable.fstTime)));
		String isEnd = mMap.get(CLFindScheduleTable.fstPostState);
		int beforTime = Integer.parseInt(mMap
				.get(CLFindScheduleTable.fstBeforeTime));

		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());

		String key = mMap.get(CLFindScheduleTable.fstDate);
		String timeKey = DateUtil.formatDateTimeHm(DateUtil
				.parseDateTimeHm(mMap.get(CLFindScheduleTable.fstTime)));

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
		if (beforeStr.equals("0")) {
			clockTime = "<font color='" + colorState + "' size='5px'>"
					+ alarmClockTime + "</font>";
		} else {
			clockTime = "<font color='" + colorState + "' size='5px'>"
					+ alarmClockTime + "(" + beforeStr + ")" + "</font>";
		}
		if ("1".equals(isEnd)) {
			colorState = "#7F7F7F";// "#9f9f9f";
			shunBackKuang = R.drawable.tv_kuang_before;
		} else {
			if (today.equals(key)) {
				colorState = ""
						+ context.getResources().getColor(
								R.color.mingtian_color);
			} else if (tomorrow.equals(key)) {
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

		// clockTime = "<font color='" + colorState + "' size='5px'>"
		// + alarmClockTime + "(" + beforeStr + ")" + "</font>";
		// 全天
		point = "<font color='" + colorState + "' size='5px'>"
				+ context.getString(R.string.adapter_allday) + "</font>";
		// 顺延
		sequence = "<font color='" + colorState + "'>"
				+ context.getString(R.string.adapter_shun) + "</font>";

		timeall_tv_content.setText(mMap.get(CLFindScheduleTable.fstContent));
		if ("1".equals(mMap.get(CLFindScheduleTable.fstPostState))) {
			timeall_tv_content.setTextColor(Color.parseColor(colorState));
			timeall_tv_time.setTextColor(Color.parseColor(colorState));
			// timeall_tv_content.getPaint().setFlags(
			// Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			timeall_tv_content.setTextColor(context.getResources().getColor(
					R.color.endtextcolor));
			timeall_tv_content.getPaint().setFlags(0 | Paint.ANTI_ALIAS_FLAG);
		} else {
			timeall_tv_time.setTextColor(context.getResources().getColor(
					R.color.mingtian_color));
			timeall_tv_content.setTextColor(Color.BLACK);
			timeall_tv_content.getPaint().setFlags(0 | Paint.ANTI_ALIAS_FLAG);
		}
		String nongli = "";
		if (mList.size() > 0) {
			if (DateUtil.parseDate(mMap.get(CLFindScheduleTable.fstDate))
					.before(DateUtil.parseDate("2017-12-31"))) {
				nongli = App
						.getDBcApplication()
						.queryLunartoSolarList(
								mMap.get(CLFindScheduleTable.fstDate), 0)
						.get("lunarCalendar");
			}
		}
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(DateUtil.parseDate(mMap
				.get(CLFindScheduleTable.fstDate)));
		Date dateToday = DateUtil.parseDateTime(DateUtil
				.formatDateTime(new Date()));
		long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
		long day = betweem / (24 * 3600);
		long hour = betweem % (24 * 3600) / 3600;
		long min = betweem % 3600 / 60;
		long afterday = ((long) (DateUtil.parseDate(
				DateUtil.formatDate(new Date())).getTime() - DateUtil
				.parseDate(key).getTime()) / 1000) / (24 * 3600);
		if (position == 0) {
			timeall_ll.setVisibility(View.VISIBLE);
			if (today.equals(key)) {// &&
				timeall_after.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setText(nongli);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_today));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_todaytextview);

				timeall_tv_month.setText(today.substring(5));
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (Math.abs(min) == 0) {
					timeall_tv_lastday.setText("");
				} else {
					timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
				}
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
					if (dateStr.getTime() < dateToday.getTime()) {
						if ("0".equals(mMap
								.get(CLFindScheduleTable.fstDisplayTime))) {
							timeall_tv_lastday.setVisibility(View.GONE);
						} else {
							timeall_tv_lastday.setVisibility(View.GONE);
						}
					} else {
						if ("0".equals(mMap
								.get(CLFindScheduleTable.fstDisplayTime))) {
							timeall_tv_lastday.setVisibility(View.GONE);
						} else {
							timeall_tv_lastday.setVisibility(View.VISIBLE);
						}
					}
				}
			} else if (tomorrow.equals(key)) {
				timeall_after.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setText(nongli);
				timeall_ll.setVisibility(View.VISIBLE);
				// timeall_tv_has.setVisibility(View.GONE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_tomorrow));
				timeall_tv_month.setText(tomorrow.substring(5));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					timeall_tv_lastday.setVisibility(View.GONE);
				} else {
					timeall_tv_lastday.setVisibility(View.GONE);
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(Math.abs(day) + "天后");
				} else {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");

				}
			} else {
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_after.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setText(nongli);

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_outweek));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));

				timeall_after.setText(Math.abs(afterday) + "天后");
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
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
		} else if (mList
				.get(position)
				.get(CLFindScheduleTable.fstDate)
				.equals(mList.get(position - 1)
						.get(CLFindScheduleTable.fstDate))) {
			if (today.equals(mList.get(position).get(
					CLFindScheduleTable.fstDate))) {
				timeall_after.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_ll.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.GONE);
				timeall_tv_nongli.setText(nongli);
				timeall_tv_daycount.setText(context.getResources().getString(
						R.string.adapter_today));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_todaytextview);
				timeall_tv_month.setText(today.substring(5));
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
					timeall_tv_lastday.setVisibility(View.VISIBLE);
				} else if (Math.abs(min) == 0) {
					timeall_tv_lastday.setText("");
				} else {
					timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
				}
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
					if (dateStr.getTime() < dateToday.getTime()) {
						if ("0".equals(mMap
								.get(CLFindScheduleTable.fstDisplayTime))) {
							timeall_tv_lastday.setVisibility(View.GONE);
						} else {
							timeall_tv_lastday.setVisibility(View.GONE);
						}
					} else {
						if ("0".equals(mMap
								.get(CLFindScheduleTable.fstDisplayTime))) {
							timeall_tv_lastday.setVisibility(View.GONE);
						} else {
							timeall_tv_lastday.setVisibility(View.VISIBLE);
						}
					}
				}
			} else if (tomorrow.equals(mMap.get(CLFindScheduleTable.fstDate))) {
				timeall_after.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_ll.setVisibility(View.GONE);
				timeall_tv_nongli.setVisibility(View.GONE);
				timeall_tv_nongli.setText(nongli);
				if (dateStr.getTime() < dateToday.getTime()) {
					if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						timeall_tv_lastday.setVisibility(View.GONE);
					}
				} else {
					if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						timeall_tv_lastday.setVisibility(View.VISIBLE);
					}
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(Math.abs(day) + "天后");
				} else {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");

				}
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else {
				timeall_after.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				if (mList
						.get(position)
						.get(CLFindScheduleTable.fstDate)
						.equals(mList.get(position - 1).get(
								CLFindScheduleTable.fstDate))) {
					timeall_ll.setVisibility(View.GONE);
					timeall_tv_month.setVisibility(View.GONE);
					timeall_tv_week.setVisibility(View.GONE);
					timeall_tv_nongli.setVisibility(View.GONE);
				} else {
					timeall_ll.setVisibility(View.VISIBLE);
					timeall_tv_month.setVisibility(View.VISIBLE);
					timeall_tv_week.setVisibility(View.VISIBLE);
					timeall_tv_nongli.setVisibility(View.VISIBLE);
				}
				timeall_tv_nongli.setText(nongli);
				timeall_tv_daycount.setVisibility(View.GONE);

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_outweek));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_after.setText(Math.abs(afterday) + "天后");
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
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
			if (today.equals(key)) {// 今天
				timeall_after.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setText(nongli);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_today));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_todaytextview);
				timeall_tv_month.setText(today.substring(5));
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				if (Math.abs(hour) >= 1) {
					timeall_tv_lastday.setVisibility(View.VISIBLE);
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");
				} else if (Math.abs(min) == 0) {
					timeall_tv_lastday.setText("");
				} else {
					timeall_tv_lastday.setText(Math.abs(min) + "分钟后");
				}
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if (dateStr.getTime() < dateToday.getTime()) {
					if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						timeall_tv_lastday.setVisibility(View.GONE);
					}
				} else {
					if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						timeall_tv_lastday.setVisibility(View.VISIBLE);
					}
				}
			} else if (tomorrow.equals(key)) {// 明天
				timeall_after.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setText(nongli);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_tomorrow));
				timeall_tv_month.setText(tomorrow.substring(5));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				if (dateStr.getTime() < dateToday.getTime()) {
					if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						timeall_tv_lastday.setVisibility(View.GONE);
					}
				} else {
					if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						timeall_tv_lastday.setVisibility(View.VISIBLE);
					}
				}
				if (Math.abs(day) >= 1) {
					timeall_tv_lastday.setText(Math.abs(day) + "天后");
				} else {
					timeall_tv_lastday.setText(Math.abs(hour) + "小时后");

				}
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
			} else {
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				if (mList
						.get(position)
						.get(CLFindScheduleTable.fstDate)
						.equals(mList.get(position - 1).get(
								CLFindScheduleTable.fstDate))) {
					timeall_ll.setVisibility(View.GONE);
					timeall_after.setVisibility(View.GONE);
					timeall_tv_month.setVisibility(View.GONE);
					timeall_tv_week.setVisibility(View.GONE);
					timeall_tv_nongli.setVisibility(View.GONE);
				} else {
					timeall_ll.setVisibility(View.VISIBLE);
					timeall_after.setVisibility(View.VISIBLE);
					timeall_tv_month.setVisibility(View.VISIBLE);
					timeall_tv_week.setVisibility(View.VISIBLE);
					timeall_tv_nongli.setVisibility(View.VISIBLE);
				}
				timeall_after.setText(Math.abs(afterday) + "天后");
				timeall_tv_nongli.setText(nongli);
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_outweek));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				if ("0".equals(mMap.get(CLFindScheduleTable.fstDisplayTime))) {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(mMap.get(CLFindScheduleTable.fstIsPostpone))) {
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
				measureTextViewHeight(mMap.get(CLFindScheduleTable.fstContent),
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
		String date;
		if ("0".equals(isEnd) && "1".equals(postpone)) {
			if (DateUtil
					.parseDate(mMap.get(CLFindScheduleTable.fstDate))
					.before(DateUtil.parseDate(DateUtil.formatDate(new Date())))) {
				calendar1.set(Calendar.DAY_OF_MONTH,
						calendar1.get(Calendar.DAY_OF_MONTH) + 1);
				date = DateUtil.formatDate(calendar1.getTime());
				App.getDBcApplication()
						.updateScheduleDateData(
								Integer.parseInt(mMap
										.get(CLFindScheduleTable.fstSchID)),
								date, mMap.get(CLFindScheduleTable.fstTime));
			}
		}
		if ("1".equals(mMap.get(CLFindScheduleTable.fstIsEnd))) {
			timeall_rela_right.setBackground(context.getResources()
					.getDrawable(R.drawable.bg_sch_end));
			if (!"0".equals(mMap.get(CLFindScheduleTable.fstRepeatId))
					&& "1".equals(mMap.get(CLFindScheduleTable.fstRepeatLink))) {
				comename_tv.setVisibility(View.VISIBLE);
				if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片+链接");
				} else if ("".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片");
				} else if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& "".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "链接");
				} else {
					comename_tv.setText("(来自 重复)");
				}
			} else {
				comename_tv.setVisibility(View.VISIBLE);
				if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片+链接");
				} else if ("".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片");
				} else if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& "".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "链接");
				} else {
					comename_tv.setVisibility(View.GONE);
				}
			}
		} else {
			timeall_rela_right.setBackground(context.getResources()
					.getDrawable(R.drawable.bg_sch_normal));
			if (!"0".equals(mMap.get(CLFindScheduleTable.fstRepeatId))
					&& "1".equals(mMap.get(CLFindScheduleTable.fstRepeatLink))) {
				comename_tv.setVisibility(View.VISIBLE);
				if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片+链接");
				} else if ("".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片");
				} else if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& "".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "链接");
				} else {
					comename_tv.setText("(来自 重复)");
				}
			} else {
				comename_tv.setVisibility(View.VISIBLE);
				if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片+链接");
				} else if ("".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& !"".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "图片");
				} else if (!"".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(CLFindScheduleTable.fstWebURL)))
						&& "".equals(StringUtils.getIsStringEqulesNull(mMap
								.get(CLFindScheduleTable.fstImagePath)))) {
					comename_tv.setText("  " + "链接");
				} else {
					comename_tv.setVisibility(View.GONE);
				}
			}
		}
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

	@Override
	public void onRightViewWidth(int position) {
		swipeXlistview.setRightViewWidth(context.getResources()
				.getDimensionPixelSize(R.dimen.friends_item_80));
	}

}
