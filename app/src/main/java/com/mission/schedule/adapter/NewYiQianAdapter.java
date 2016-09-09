package com.mission.schedule.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.applcation.App;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.onRightViewWidthListener;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewYiQianAdapter extends BaseAdapter implements
		onRightViewWidthListener {

	private Context context;
	private List<Map<String, String>> mList;
	private Handler handler;
	private SwipeXListViewNoHead swipeXlistview;
	int width;

	public NewYiQianAdapter(Context context, List<Map<String, String>> mList,
			Handler handler, SwipeXListViewNoHead swipeXlistview,int width) {
		this.context = context;
		this.mList = mList;
		this.handler = handler;
		this.swipeXlistview = swipeXlistview;
		this.width = width;
		swipeXlistview.setonRightViewWidthListener(this);
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

	private void setOnclick(int positions, Map<String, String> map, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = map;
		message.what = what;
		handler.sendMessage(message);
	}

	@SuppressLint({ "SimpleDateFormat", "NewApi" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewWapper viewWapper = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_newyiqian, null);
			viewWapper = new ViewWapper(view);
			view.setTag(viewWapper);
		} else {
			viewWapper = (ViewWapper) view.getTag();
		}

		LinearLayout top_ll = viewWapper.getTop_ll();
		LinearLayout timeall_ll = viewWapper.getTimeAllLL(); // 标题栏，包含今天，日期，农历等
		TextView timeall_tv_month = viewWapper.getTimeAllTvMonth(); // 日期
																	// 如：08-31
		TextView timeall_tv_week = viewWapper.getTimeAllTvWeek(); // 周几 如：周二
		TextView timeall_tv_daycount = viewWapper.getTimeAllTvDayCount(); // 今天，明天，几天后
		RelativeLayout timeall_rela_right = viewWapper.getTimeAllRightRela(); // 下面整体布局
		TextView timeall_tv_time = viewWapper.getTimeAllTvTime(); // 时间
																	// 如：全天或08：00
		TextView timeall_tv_shun = viewWapper.getTimeAllTvShun(); // 顺延 如：顺
		TextView timeall_tv_lastday = viewWapper.getTimeAllTvLastDay(); // 多少小时后，多少天后
		TextView timeall_tv_content = viewWapper.getTimeAllTvContent(); // 日程内容
		// TextView timeall_tv_has = viewWapper.getTimeall_tv_has(); // 标题栏天后
		TextView personstate_tv = viewWapper.getPersonStateTextView();// 公开状态
		TextView tv_delete = viewWapper.getTv_delete();// 删除
		TextView comename_tv = viewWapper.getComeName();// 来自谁的日程
		LinearLayout delete_ll = viewWapper.getDelete_ll();// 删除布局
		LinearLayout datebackground_ll = viewWapper.getDatebackground_ll();// 今天和日期的背景
		TextView timeall_tv_nongli = viewWapper.getNongLi_TV();// 农历日期显示
		TextView bottom_month = viewWapper.getBottom_month();
		TextView bottom_week = viewWapper.getBottom_week();
		TextView timeall_after = viewWapper.getAfter_tv();

		final Map<String, String> mMap = mList.get(position);

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
		String postpone = mMap.get(CLNFMessage.nfmPostpone);
		String alarmClockTime = mMap.get(CLNFMessage.nfmTime);
		String isEnd = mMap.get(CLNFMessage.nfmPostState);
		int beforTime = Integer.parseInt(mMap.get(CLNFMessage.nfmBeforeTime));

		String yestoday;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) - 1);
		yestoday = DateUtil.formatDate(calendar.getTime());

		String key = mMap.get(CLNFMessage.nfmDate);
		String timeKey = mMap.get(CLNFMessage.nfmTime);

		Date dateStr = DateUtil.parseDateTime(key + " " + timeKey);
		String[] ymd = key.split("-");

		String clockTime = "";
		String point = "";
		String sequence = "";
		int shunBackKuang = 0;
		String colorState =context.getResources().getColor(R.color.mingtian_color)+"";
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
			colorState = ""
					+ context.getResources().getColor(R.color.mingtian_color);
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

		timeall_tv_content.setText(mMap.get(CLNFMessage.nfmContent));
		if ("1".equals(isEnd)) {
			timeall_tv_content.setTextColor(Color.parseColor(colorState));
			timeall_tv_time.setTextColor(Color.parseColor("#7F7F7F"));
			bottom_month.setTextColor(Color.parseColor("#7F7F7F"));
			bottom_week.setTextColor(Color.parseColor("#7F7F7F"));
			timeall_tv_content.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		} else {
			timeall_tv_time.setTextColor(context.getResources().getColor(
					R.color.mingtian_color));
			timeall_tv_content.setTextColor(Color.BLACK);
			bottom_month.setTextColor(context.getResources().getColor(
					R.color.mingtian_color));
			bottom_week.setTextColor(context.getResources().getColor(
					R.color.mingtian_color));
			timeall_tv_content.getPaint().setFlags(0 | Paint.ANTI_ALIAS_FLAG);
		}

		String downstate = mMap.get(CLNFMessage.nfmDownState);
		String nfmisend = mMap.get(CLNFMessage.nfmIsEnd);
		if ("0".equals(mMap.get(CLNFMessage.nfmPId))) {
			personstate_tv.setVisibility(View.VISIBLE);
			if ("1".equals(nfmisend)) {
				personstate_tv.setVisibility(View.VISIBLE);
				personstate_tv.setText("已结束");
				personstate_tv.setTextColor(context.getResources().getColor(
						R.color.gongkai_txt));
			} else if (downstate.equals("0")) {
				personstate_tv.setVisibility(View.GONE);
				if ("1".equals(isEnd)) {
					// personstate_tv.setText("未下行");
					// personstate_tv.setTextColor(context.getResources()
					// .getColor(R.color.gongkai_txt));
				} else {
					// personstate_tv.setText("未下行");
					// personstate_tv.setTextColor(context.getResources()
					// .getColor(R.color.sunday_txt));
				}
			} else if (downstate.equals("1")) {
				personstate_tv.setVisibility(View.VISIBLE);
				if ("1".equals(isEnd)) {
					personstate_tv.setText("已下行");
					personstate_tv.setTextColor(context.getResources()
							.getColor(R.color.gongkai_txt));
				} else {
					personstate_tv.setText("已下行");
					personstate_tv.setTextColor(context.getResources()
							.getColor(R.color.sunday_txt));
				}
			}
		} else {
			personstate_tv.setVisibility(View.GONE);
		}

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(DateUtil.parseDate(mMap.get(CLNFMessage.nfmDate)));

		Date dateToday = DateUtil.parseDate(DateUtil.formatDate(new Date()));
		long betweem = (long) (dateToday.getTime() - DateUtil.parseDate(key)
				.getTime()) / 1000;
		long day = betweem / (24 * 3600);

		if (position == 0) {// 当position=0，判断今天中是否有待办日程，有就隐藏标题栏，隐藏顺和天后，如果今天没有日程，然后判断明天和之后的
			timeall_ll.setVisibility(View.VISIBLE);
			if (yestoday.equals(key)) {// &&
				timeall_after.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_yesterday));
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);

				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_nongli.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_tv_lastday.setText("1天前");
				if ("0".equals(mMap.get(CLNFMessage.nfmDisplayTime))) {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(postpone)) {
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
						timeall_tv_lastday.setVisibility(View.GONE);
					} else {
						timeall_tv_lastday.setVisibility(View.VISIBLE);
					}
				}
			} else {
				timeall_after.setVisibility(View.VISIBLE);
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_month.setVisibility(View.VISIBLE);
				timeall_tv_week.setVisibility(View.VISIBLE);
				timeall_tv_nongli.setVisibility(View.VISIBLE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_twodayago));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_after.setText(Math.abs(day) + "天前");
				if ("0".equals(mMap.get(CLNFMessage.nfmDisplayTime))) {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if ("1".equals(isEnd)) {
					bottom_week.setTextColor(Color.parseColor("#7F7F7F"));
				} else {
					bottom_week.setTextColor(context.getResources().getColor(
							R.color.mingtian_color));
				}
			}
		} else if (mList.get(position).get(CLNFMessage.nfmDate)
				.equals(mList.get(position - 1).get(CLNFMessage.nfmDate))) {
			if (yestoday.equals(mMap.get(CLNFMessage.nfmDate))) {
				timeall_tv_daycount.setVisibility(View.GONE);
				timeall_after.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.GONE);
				timeall_tv_lastday.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_tv_lastday.setText("1天前");
				if ("0".equals(mMap.get(CLNFMessage.nfmDisplayTime))) {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(postpone)) {
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
				if (mList
						.get(position)
						.get(CLNFMessage.nfmDate)
						.equals(mList.get(position - 1)
								.get(CLNFMessage.nfmDate))) {
					timeall_ll.setVisibility(View.GONE);
					timeall_tv_month.setVisibility(View.GONE);
					timeall_tv_week.setVisibility(View.GONE);
					timeall_tv_nongli.setVisibility(View.GONE);
					timeall_after.setVisibility(View.GONE);
				} else {
					timeall_ll.setVisibility(View.VISIBLE);
					timeall_tv_month.setVisibility(View.VISIBLE);
					timeall_tv_week.setVisibility(View.VISIBLE);
					timeall_tv_nongli.setVisibility(View.VISIBLE);
					timeall_after.setVisibility(View.VISIBLE);
				}
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_inweek));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_after.setText(Math.abs(day) + "天前");
				if ("0".equals(mMap.get(CLNFMessage.nfmDisplayTime))) {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if ("1".equals(isEnd)) {
					bottom_week.setTextColor(Color.parseColor("#7F7F7F"));
				} else {
					bottom_week.setTextColor(context.getResources().getColor(
							R.color.mingtian_color));
				}
			}
		} else {
			if (yestoday.equals(key)) {// 昨天
				timeall_tv_lastday.setVisibility(View.GONE);
				timeall_after.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_ll.setVisibility(View.VISIBLE);
				timeall_tv_daycount.setVisibility(View.VISIBLE);
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
				timeall_tv_lastday.setText("1天前");
				if ("0".equals(mMap.get(CLNFMessage.nfmDisplayTime))) {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(postpone)) {
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
					if (mList
							.get(position)
							.get(CLNFMessage.nfmDate)
							.equals(mList.get(position - 1).get(
									CLNFMessage.nfmDate))) {
						timeall_ll.setVisibility(View.GONE);
						timeall_tv_month.setVisibility(View.GONE);
						timeall_tv_week.setVisibility(View.GONE);
						timeall_tv_nongli.setVisibility(View.GONE);
						timeall_after.setVisibility(View.GONE);
					} else {
						timeall_ll.setVisibility(View.VISIBLE);
						timeall_tv_month.setVisibility(View.VISIBLE);
						timeall_tv_week.setVisibility(View.VISIBLE);
						timeall_tv_nongli.setVisibility(View.VISIBLE);
						timeall_after.setVisibility(View.VISIBLE);
					}
				timeall_tv_lastday.setVisibility(View.GONE);
				bottom_month.setVisibility(View.GONE);
				bottom_week.setVisibility(View.GONE);
				timeall_tv_daycount.setVisibility(View.GONE);

				timeall_tv_daycount.setText(context
						.getString(R.string.adapter_twodayago));
				timeall_tv_month.setText(ymd[1] + "-" + ymd[2]);
				timeall_tv_daycount.setTextColor(Color.parseColor("#ffffff"));
				datebackground_ll
						.setBackgroundResource(R.drawable.bg_tommorowtextview);
				timeall_tv_week.setText(CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key)));
				timeall_tv_week.setTextColor(Color.parseColor("#7F7F7F"));
				timeall_after.setText(Math.abs(day) + "天前");
				if ("0".equals(mMap.get(CLNFMessage.nfmDisplayTime))) {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(point));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(point));
					}
				} else {
					if ("1".equals(postpone)) {
						timeall_tv_time.setText(Html.fromHtml(clockTime));
						timeall_tv_shun.setVisibility(View.VISIBLE);
						timeall_tv_shun.setBackgroundResource(shunBackKuang);
						timeall_tv_shun.setText(Html.fromHtml(sequence));
					} else {
						timeall_tv_shun.setVisibility(View.GONE);
						timeall_tv_time.setText(Html.fromHtml(clockTime));
					}
				}
				if ("1".equals(isEnd)) {
					bottom_week.setTextColor(Color.parseColor("#7F7F7F"));
				} else {
					bottom_week.setTextColor(context.getResources().getColor(
							R.color.mingtian_color));
				}
			}
		}

		if (timeall_tv_nongli.getVisibility() == View.VISIBLE) {
			if (mList.size() > 0) {
				String nongli = App
						.getDBcApplication()
						.queryLunartoSolarList(mMap.get(CLNFMessage.nfmDate), 0)
						.get("lunarCalendar");
				timeall_tv_nongli.setText(nongli);
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
				measureTextViewHeight(mMap.get(CLNFMessage.nfmContent),
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
		timeall_rela_right.setBackground(context.getResources().getDrawable(
				R.drawable.bg_sch_normal));

		if ("1".equals(isEnd)) {
			if (!"0".equals(mMap.get(CLNFMessage.nfmPId))) {
				comename_tv.setVisibility(View.VISIBLE);
				comename_tv.setTextColor(context.getResources().getColor(
						R.color.gongkai_txt));
				comename_tv.setText("来自 重复");
			} else {
				comename_tv.setVisibility(View.GONE);
			}
			timeall_rela_right.setBackground(context.getResources()
					.getDrawable(R.drawable.bg_sch_end));
		} else {
			if (!"0".equals(mMap.get(CLNFMessage.nfmPId))) {
				comename_tv.setVisibility(View.VISIBLE);
				comename_tv.setTextColor(context.getResources().getColor(
						R.color.black));
				comename_tv.setText("来自 重复");
				timeall_rela_right.setBackground(context.getResources()
						.getDrawable(R.drawable.bg_sch_normal));
			} else {
				comename_tv.setVisibility(View.GONE);
				timeall_rela_right.setBackground(context.getResources()
						.getDrawable(R.drawable.bg_sch_normal));
			}
		}
		if ("0".equals(mMap.get(CLNFMessage.nfmDisplayTime))) {
			timeall_tv_lastday.setVisibility(View.GONE);
		} else {
			timeall_tv_lastday.setVisibility(View.VISIBLE);
		}
		return view;
	}

	class ViewWapper {
		private View view;
		private LinearLayout top_ll;
		private LinearLayout timeall_ll;// 前半部分view
		private TextView timeall_tv_week;// 星期几
		private TextView timeall_tv_month;// 日期，如：01-01
		private TextView timeall_tv_daycount;// 今天
		private RelativeLayout timeall_rela_right;// 提醒内容右边的整体布局
		private TextView timeall_tv_time;// 提醒的时间
		private TextView timeall_tv_shun;// 是否顺时
		private TextView timeall_tv_lastday;// 几小时后提醒
		private TextView timeall_tv_content;// 提醒的内容
		// private TextView timeall_tv_has;// 3天后
		private LinearLayout delete_ll;
		private TextView tv_delete;// 删除
		private TextView personstate_tv;// 公开状态
		private TextView comename_tv;// 来自谁的日程
		private LinearLayout datebackground_ll;
		private TextView timeall_tv_nongli;// 农历
		private TextView bottom_month;// 下面的月日，如：09:21
		private TextView bottom_week;// 下面的周几，如：周二
		private TextView timeall_after;

		private ViewWapper(View view) {
			this.view = view;
		}

		private LinearLayout getTop_ll() {
			if (top_ll == null) {
				top_ll = (LinearLayout) view.findViewById(R.id.top_ll);
			}
			return top_ll;
		}

		private LinearLayout getTimeAllLL() {
			if (timeall_ll == null) {
				timeall_ll = (LinearLayout) view.findViewById(R.id.timeall_ll);
			}
			return timeall_ll;
		}

		private TextView getPersonStateTextView() {
			if (personstate_tv == null) {
				personstate_tv = (TextView) view
						.findViewById(R.id.personstate_tv);
			}
			return personstate_tv;
		}

		private TextView getTimeAllTvWeek() {
			if (timeall_tv_week == null) {
				timeall_tv_week = (TextView) view
						.findViewById(R.id.timeall_tv_week);
			}
			return timeall_tv_week;
		}

		private TextView getTimeAllTvMonth() {
			if (timeall_tv_month == null) {
				timeall_tv_month = (TextView) view
						.findViewById(R.id.timeall_tv_month);
			}
			return timeall_tv_month;
		}

		private TextView getTimeAllTvDayCount() {
			if (timeall_tv_daycount == null) {
				timeall_tv_daycount = (TextView) view
						.findViewById(R.id.timeall_tv_daycount);
			}
			return timeall_tv_daycount;
		}

		// private TextView getTimeall_tv_has() {
		// if (timeall_tv_has == null) {
		// timeall_tv_has = (TextView) view
		// .findViewById(R.id.timeall_tv_has);
		// }
		// return timeall_tv_has;
		// }

		private RelativeLayout getTimeAllRightRela() {
			if (timeall_rela_right == null) {
				timeall_rela_right = (RelativeLayout) view
						.findViewById(R.id.timeall_rela_right);
			}
			return timeall_rela_right;
		}

		private TextView getTimeAllTvTime() {
			if (timeall_tv_time == null) {
				timeall_tv_time = (TextView) view
						.findViewById(R.id.timeall_tv_time);
			}
			return timeall_tv_time;
		}

		private TextView getTimeAllTvShun() {
			if (timeall_tv_shun == null) {
				timeall_tv_shun = (TextView) view
						.findViewById(R.id.timeall_tv_shun);
			}
			return timeall_tv_shun;
		}

		private TextView getTimeAllTvLastDay() {
			if (timeall_tv_lastday == null) {
				timeall_tv_lastday = (TextView) view
						.findViewById(R.id.timeall_tv_lastday);
			}
			return timeall_tv_lastday;
		}

		private TextView getTimeAllTvContent() {
			if (timeall_tv_content == null) {
				timeall_tv_content = (TextView) view
						.findViewById(R.id.timeall_tv_content);
			}
			return timeall_tv_content;
		}

		public TextView getTv_delete() {
			if (tv_delete == null) {
				tv_delete = (TextView) view.findViewById(R.id.tv_delete);
			}
			return tv_delete;
		}

		public TextView getComeName() {
			if (comename_tv == null) {
				comename_tv = (TextView) view.findViewById(R.id.comename_tv);
			}
			return comename_tv;
		}

		private LinearLayout getDelete_ll() {
			if (delete_ll == null) {
				delete_ll = (LinearLayout) view.findViewById(R.id.delete_ll);
			}
			return delete_ll;
		}

		private LinearLayout getDatebackground_ll() {
			if (datebackground_ll == null) {
				datebackground_ll = (LinearLayout) view
						.findViewById(R.id.datebackground_ll);
			}
			return datebackground_ll;
		}

		private TextView getNongLi_TV() {
			if (timeall_tv_nongli == null) {
				timeall_tv_nongli = (TextView) view
						.findViewById(R.id.timeall_tv_nongli);
			}
			return timeall_tv_nongli;
		}

		private TextView getBottom_month() {
			if (bottom_month == null) {
				bottom_month = (TextView) view.findViewById(R.id.bottom_month);
			}
			return bottom_month;
		}

		private TextView getBottom_week() {
			if (bottom_week == null) {
				bottom_week = (TextView) view.findViewById(R.id.bottom_week);
			}
			return bottom_week;
		}

		private TextView getAfter_tv() {
			if (timeall_after == null) {
				timeall_after = (TextView) view
						.findViewById(R.id.timeall_after);
			}
			return timeall_after;
		}
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
}