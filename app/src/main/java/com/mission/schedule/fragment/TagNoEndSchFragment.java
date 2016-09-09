package com.mission.schedule.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.activity.AddEverydayDetailTaskActivity;
import com.mission.schedule.activity.MainActivity;
import com.mission.schedule.activity.MyStateActivity;
import com.mission.schedule.activity.SchZhuanFaActivity;
import com.mission.schedule.activity.TagSchSerachActivity;
import com.mission.schedule.adapter.TagAllSchAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.MySchBean;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.LocateAllNoticeTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.InWeekUtils;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.RepeatSetChildEndUtils;
import com.mission.schedule.utils.SchDateComparator;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class TagNoEndSchFragment extends Fragment {

	@ViewResId(id = R.id.important_listview)
	private SwipeXListViewNoHead important_listview;

	List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> otherlist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> yestodaylist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> todaylist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> tomorrowlist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> inweeklist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> outweeklist = new ArrayList<Map<String, String>>();

	TagAllSchAdapter adapter = null;
	Context context;
	App application = App.getDBcApplication();

	String time;
	String ringdesc;
	String ringcode;

	SharedPrefUtil sharedPrefUtil = null;
	private boolean isShow;// 判断是否已经显示
	int width, heigth;
	// int scrolledX = 0;
	// int scrolledY = 0;
	InWeekUtils inWeekUtils = new InWeekUtils();
	RepeatSetChildEndUtils repeatSetChildEndUtils = new RepeatSetChildEndUtils();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_importantsch, container,
				false);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && !isShow) {
			isShow = true;
			init();
			loadData();
			setAdapter();
		}
	}

	private void init() {
		View view = getView();
		context = getActivity();
		important_listview = (SwipeXListViewNoHead) view
				.findViewById(R.id.important_listview);
		important_listview.setPullLoadEnable(false);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		time = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ALLTIME, "08:58");
		ringdesc = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICDESC, "完成任务");
		ringcode = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICCODE, "g_88");
		View footView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		important_listview.addFooterView(footView);
		/**
		 * 获取屏幕的高度和宽度
		 */
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		heigth = metric.heightPixels;
		// important_listview.setOnScrollListener(new OnScrollListener() {
		//
		// /**
		// * 滚动状态改变时调用
		// */
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// // 不滚动时保存当前滚动到的位置
		// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		// // if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		// // scrollPos记录当前可见的List顶端的一行的位置
		// scrolledX = important_listview.getFirstVisiblePosition();
		// }
		// if (mList != null) {
		// View v = important_listview.getChildAt(0);
		// scrolledY = (v == null) ? 0 : v.getTop();
		// }
		// System.out.println("scrolledX:" + scrolledX + ",scrolledY:"
		// + scrolledY);
		// }
		//
		// /**
		// * 滚动时调用
		// */
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// }
		// });

	}

	private void loadData() {
		// 1 两天以前 2 昨天 3今天 4明天 5一周以内 6一周以后
		try {
			otherlist.clear();
			yestodaylist.clear();
			todaylist.clear();
			tomorrowlist.clear();
			inweeklist.clear();
			outweeklist.clear();
			mList.clear();
			if (!"0".equals(TagSchSerachActivity.tagId)) {
				int type = Integer.parseInt(TagSchSerachActivity.tagId);
				otherlist = application.queryAllSchData(56, 0, type);
				yestodaylist = application.queryAllSchData(57, 0, type);
				todaylist = application.queryAllSchData(58, 0, type);
				tomorrowlist = application.queryAllSchData(59, 0, type);
				inweeklist = application.queryAllSchData(60, 0, type);
				outweeklist = application.queryAllSchData(61, 0, type);
				Collections.sort(otherlist, new SchDateComparator());

				mList.addAll(otherlist);
				mList.addAll(yestodaylist);
				mList.addAll(todaylist);
				mList.addAll(tomorrowlist);
				mList.addAll(inweeklist);
				mList.addAll(outweeklist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setAdapter() {
		adapter = new TagAllSchAdapter(context, mList, R.layout.adapter_tagall,
				handler, important_listview, width,
				TagSchSerachActivity.tagname);
		important_listview.setPullLoadEnable(false);
		important_listview.setPullRefreshEnable(false);
		important_listview.setAdapter(adapter);
	}

	private void RefreshMyData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (NetUtil.getConnectState(getActivity()) != NetWorkState.NONE) {
					Intent intent = new Intent(getActivity(),
							UpLoadService.class);
					intent.setAction(Const.SHUAXINDATA);
					intent.setPackage(getActivity().getPackageName());
					getActivity().startService(intent);
				} else {
					return;
				}
			}
		}).start();
	}

	private void UpLoadData() {
		if (NetUtil.getConnectState(getActivity()) != NetWorkState.NONE) {
			Intent intent = new Intent(getActivity(), UpLoadService.class);
			intent.setAction(Const.UPLOADDATA);
			intent.setPackage(getActivity().getPackageName());
			getActivity().startService(intent);
		} else {
			return;
		}
	}

	private Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Map<String, String> mMap = (Map<String, String>) msg.obj;
			// Map upMap = null;
			// String value = "0";
			int position = msg.arg1;
			switch (msg.what) {
			case 0:// 点击菜单(详情菜单)
				dialogDetailOnClick(mMap, position);
				break;

			case 2:// 设为结束
					// updateSchedule(mMap, ScheduleTable.schIsEnd,
					// ScheduleTable.schUpdateState);
					// App.getDBcApplication().updateSchReadData(
					// Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					// App.getDBcApplication().updateSchRepeatLinkData(
					// Integer.parseInt(mMap.get(ScheduleTable.schID)), 3);
					// updateSchClock(mMap, LocateAllNoticeTable.isEnd);
				if ("0".equals(mMap.get(ScheduleTable.schRepeatID))) {
					// updateScheduleRead1(mMap, ScheduleTable.schRead);
				} else {
					updateScheduleRead2(mMap, ScheduleTable.schRead,
							ScheduleTable.schRepeatLink);
				}
				updateFocusStateSch(mMap, ScheduleTable.schFocusState);
				updateScheduleIsEnd(mMap, ScheduleTable.schIsEnd,
						ScheduleTable.schUpdateState);
				updateSchClock(mMap, LocateAllNoticeTable.isEnd);
				adapter.notifyDataSetChanged();
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.ENDUPDATETIME,
						DateUtil.formatDateTimeSs(new Date()));
				final String updatetime = sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.ENDUPDATETIME,
						DateUtil.formatDateTimeSs(new Date()));
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						String delaytime = DateUtil
								.formatDateTimeSs(new Date());
						if (DateUtil.parseDateTimeSs(delaytime).getTime()
								- DateUtil.parseDateTimeSs(updatetime)
										.getTime() >= 3000) {
							UpLoadData();
						}
					}
				}, 3000);
				if (DateUtil.parseDateTime(DateUtil.formatDateTime(new Date()))
						.getTime() >= DateUtil.parseDateTime(
						mMap.get(ScheduleTable.schDate) + " "
								+ mMap.get(ScheduleTable.schTime)).getTime()) {
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

				}
				break;

			case 3:// 删除
				alertDeleteDialog(mMap, 0, position);
				break;
			}
		}

	};

	private void updateScheduleRead2(Map<String, String> mMap, String key,
			String key1) {
		try {
			String value = "0";
			Map<String, String> upMap = new HashMap<String, String>();
			important_listview.hiddenRight();
			// if ("0".equals(mMap.get(key)))
			// value = "0";
			// else
			value = mMap.get(key);
			upMap.put(key, value);
			upMap.put(key1, "3");
			App.getDBcApplication().updateScheduleData(upMap,
					"where schID=" + mMap.get("schID"));
			mMap.put(key, value);
			mMap.put(key1, "3");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void alertDeleteDialog(final Map<String, String> mMap,
			final int type, final int position) {
		final AlertDialog builder = new AlertDialog.Builder(context).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setAttributes(params);// 设置生效
		window.setContentView(R.layout.dialog_alterdelete);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
				if (type == 0) {
					App.getDBcApplication().updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 2);
					String deleteId = mMap.get(ScheduleTable.schID);
					App dbContextExtended = App.getDBcApplication();
					dbContextExtended.deleteScheduleLocalData(deleteId);
					dbContextExtended.deleteSch(Integer.parseInt(deleteId));
					important_listview.hiddenRight();
					App.getDBcApplication().updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("1".equals(mMap.get(ScheduleTable.schRepeatLink))
							|| "3".equals(mMap.get(ScheduleTable.schRepeatLink))) {
						Map<String, String> map = App
								.getDBcApplication()
								.QueryStateData(
										Integer.parseInt(mMap
												.get(ScheduleTable.schRepeatID)));
						if (map != null) {
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							if (repdate.equals(lastdate) || repdate.equals(nextdate)) {
								if (!"".equals(lastdate)&& lastdate.equals(repdate)) {
									application
											.updateSchCLRepeatData(
													Integer.parseInt(mMap
															.get(ScheduleTable.schRepeatID)),
													mMap.get(ScheduleTable.schRepeatDate),
													map.get(CLRepeatTable.repDateTwo),
													2,
													Integer.parseInt(map
															.get(CLRepeatTable.repStateTwo)));
								} else if (!"".equals(nextdate)&& nextdate.equals(repdate)) {
									application
											.updateSchCLRepeatData(
													Integer.parseInt(mMap
															.get(ScheduleTable.schRepeatID)),
													map.get(CLRepeatTable.repDateOne),
													mMap.get(ScheduleTable.schRepeatDate),
													Integer.parseInt(map
															.get(CLRepeatTable.repStateOne)),
													2);
								}
							} else {
								if ("".equals(lastdate) && "".equals(nextdate)) {
									application
											.updateSchCLRepeatData(
													Integer.parseInt(mMap
															.get(ScheduleTable.schRepeatID)),
													mMap.get(ScheduleTable.schRepeatDate),
													map.get(CLRepeatTable.repDateTwo),
													2,
													Integer.parseInt(map
															.get(CLRepeatTable.repStateTwo)));
								} else if ("".equals(lastdate) && !"".equals(nextdate)) {
									application
											.updateSchCLRepeatData(
													Integer.parseInt(mMap
															.get(ScheduleTable.schRepeatID)),
													map.get(CLRepeatTable.repDateOne),
													mMap.get(ScheduleTable.schRepeatDate),
													Integer.parseInt(map
															.get(CLRepeatTable.repStateOne)),
													2);
								} else if (!"".equals(lastdate) && "".equals(nextdate)) {
									application
											.updateSchCLRepeatData(
													Integer.parseInt(mMap
															.get(ScheduleTable.schRepeatID)),
													mMap.get(ScheduleTable.schRepeatDate),
													map.get(CLRepeatTable.repDateTwo),
													2,
													Integer.parseInt(map
															.get(CLRepeatTable.repStateTwo)));
								} else {
									if (DateUtil.parseDateTime(lastdate).getTime() > DateUtil
											.parseDateTime(nextdate).getTime()) {
										application
												.updateSchCLRepeatData(
														Integer.parseInt(mMap
																.get(ScheduleTable.schRepeatID)),
														map.get(CLRepeatTable.repDateOne),
														mMap.get(ScheduleTable.schRepeatDate),
														Integer.parseInt(map
																.get(CLRepeatTable.repStateOne)),
														2);
									} else {
										application
												.updateSchCLRepeatData(
														Integer.parseInt(mMap
																.get(ScheduleTable.schRepeatID)),
														mMap.get(ScheduleTable.schRepeatDate),
														map.get(CLRepeatTable.repDateTwo),
														2,
														Integer.parseInt(map
																.get(CLRepeatTable.repStateTwo)));
									}
								}
							}
						}
					}
					// mList.remove(position);
					loadData();
					adapter.notifyDataSetChanged();
					important_listview.invalidate();
					RefreshMyData();
					if (DateUtil.parseDateTime(
							DateUtil.formatDateTime(new Date())).getTime() >= DateUtil
							.parseDateTime(
									mMap.get(ScheduleTable.schDate) + " "
											+ mMap.get(ScheduleTable.schTime))
							.getTime()) {
						QueryAlarmData.writeAlarm(getActivity()
								.getApplicationContext());

					}
				}
			}
		});
		TextView delete_canel = (TextView) window
				.findViewById(R.id.delete_canel);
		delete_canel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		if (type == 0) {
			delete_tv.setText("确定要删除此记事吗?");
		} else {
			delete_tv.setText("结束今天之前所有未结束的记事?");
		}

	}

	/**
	 * 普通记事点击弹出详情菜单 setType 0,菜单详情 1,设置
	 * 
	 * @param mMap
	 */
	@SuppressWarnings("deprecation")
	private void dialogDetailOnClick(Map<String, String> mMap, int position) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_cls_detail, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() - 30;
		dialog.show();

		new MyGeneralDetailOnClick(dialog, mMap, more_pop_menu, position);
	}

	class MyGeneralDetailOnClick implements View.OnClickListener {

		private View mianView;
		private Dialog dialog;
		private int setType = 0;
		private LinearLayout detail_close;
		private LinearLayout detail;
		private LinearLayout after;
		private LinearLayout setting;
		private Map<String, String> mMap;
		private int closeType = 0;
		private TextView detail_after;
		private TextView detail_more;
		private LinearLayout detail_zhuanfa;
		private TextView timebefore_tv;
		// 更多
		private LinearLayout more_openstate;
		private TextView more_zhuanfasjb;
		private TextView more_zhuanfawx;
		private TextView more_setdaiban;
		private TextView more_shitingmusic;
		private TextView more_delete;
		private TextView more_openstate_tv;
		// 详情
		private TextView detail_date;
		private TextView detail_content;
		private TextView detail_year_date;
		private TextView detail_time_date;
		private TextView detail_tv_shun;
		private LinearLayout detail_edit_ll;
		// 推后
		private TextView after_autopostone;// 自动顺延
		private TextView after_onehour;// 推后一小时
		private TextView after_oneday;// 推后一天
		private TextView after_oneweek;// 推后一周
		private TextView after_onemonth;// 推后一个月
		private TextView after_nextweekfirstday;// 推到下周一
		private TextView after_today;// 推到今天
		private TextView after_tommrow;// 推到明天
		private View after_today_view, after_tommrow_view, after_onehour_view,
				after_oneday_view, after_oneweek_view, after_onemonth_view,
				after_nextweekfirstday_view;

		private TextView detail_important;
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();
		int position;

		@SuppressLint("NewApi")
		public MyGeneralDetailOnClick(Dialog dialog, Map<String, String> mMap,
				View view, int position) {
			this.position = position;
			this.dialog = dialog;
			this.mMap = mMap;
			this.mianView = view;
			calendar.setTime(new Date());
			today = DateUtil.formatDate(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 1);
			tomorrow = DateUtil.formatDate(calendar.getTime());
			String key = mMap.get(ScheduleTable.schDate);
			String timeKey = mMap.get(ScheduleTable.schTime);
			detail_close = (LinearLayout) view.findViewById(R.id.detail_close);
			detail_close.setOnClickListener(this);
			timebefore_tv = (TextView) view.findViewById(R.id.timebefore_tv);
			detail = (LinearLayout) view.findViewById(R.id.detail);
			after = (LinearLayout) view.findViewById(R.id.after);
			setting = (LinearLayout) view.findViewById(R.id.setting);
			detail_after = (TextView) view.findViewById(R.id.detail_after);
			detail_after.setOnClickListener(this);
			detail_more = (TextView) view.findViewById(R.id.detail_more);
			detail_more.setOnClickListener(this);
			// detail_edit = (LinearLayout) view.findViewById(R.id.detail_edit);
			// detail_edit.setOnClickListener(this);
			// 更多
			more_openstate = (LinearLayout) view
					.findViewById(R.id.more_openstate);
			more_openstate_tv = (TextView) view
					.findViewById(R.id.more_openstate_tv);
			more_zhuanfasjb = (TextView) view
					.findViewById(R.id.more_zhuanfasjb);
			more_zhuanfawx = (TextView) view.findViewById(R.id.more_zhuanfawx);
			more_setdaiban = (TextView) view.findViewById(R.id.more_setdaiban);
			more_shitingmusic = (TextView) view
					.findViewById(R.id.more_shitingmusic);
			more_delete = (TextView) view.findViewById(R.id.more_delete);
			more_openstate.setOnClickListener(this);
			more_zhuanfasjb.setOnClickListener(this);
			more_zhuanfawx.setOnClickListener(this);
			more_setdaiban.setOnClickListener(this);
			more_shitingmusic.setOnClickListener(this);
			more_delete.setOnClickListener(this);
			// 详情
			detail_zhuanfa = (LinearLayout) view
					.findViewById(R.id.detail_zhuanfa);
			detail_zhuanfa.setOnClickListener(this);
			detail_edit_ll = (LinearLayout) view
					.findViewById(R.id.detail_edit_ll);
			detail_edit_ll.setOnClickListener(this);
			detail_date = (TextView) view.findViewById(R.id.detail_date);
			detail_year_date = (TextView) view
					.findViewById(R.id.detail_year_date);
			detail_tv_shun = (TextView) view.findViewById(R.id.detail_tv_shun);
			String colorState = ""
					+ context.getResources().getColor(R.color.mingtian_color);
			// 顺延
			String sequence = "<font color='" + colorState + "'>"
					+ context.getString(R.string.adapter_shun) + "</font>";
			int shunBackKuang = R.drawable.tv_kuang_aftertime;
			detail_tv_shun.setText(Html.fromHtml(sequence));
			detail_tv_shun.setBackgroundResource(shunBackKuang);
			if (today.equals(key)) {
				detail_year_date.setText("今天");
			} else if (tomorrow.equals(key)) {
				detail_year_date.setText("明天");
			} else {
				detail_year_date.setText(key);
			}
			detail_time_date = (TextView) view
					.findViewById(R.id.detail_time_date);
			detail_time_date.setText(mMap.get(ScheduleTable.schTime));
			timebefore_tv = (TextView) view.findViewById(R.id.timebefore_tv);
			detail_content = (TextView) view.findViewById(R.id.detail_content);
			detail_content.setText(mMap.get(ScheduleTable.schContent));
			if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
				detail_tv_shun.setVisibility(View.VISIBLE);
			} else {
				detail_tv_shun.setVisibility(View.GONE);
			}

			// 推后
			after_autopostone = (TextView) view
					.findViewById(R.id.after_autopostone);
			after_autopostone.setOnClickListener(this);
			after_onehour = (TextView) view.findViewById(R.id.after_onehour);
			after_onehour.setOnClickListener(this);
			after_oneday = (TextView) view.findViewById(R.id.after_oneday);
			after_oneday.setOnClickListener(this);
			after_oneweek = (TextView) view.findViewById(R.id.after_oneweek);
			after_oneweek.setOnClickListener(this);
			after_onemonth = (TextView) view.findViewById(R.id.after_onemonth);
			after_onemonth.setOnClickListener(this);
			after_nextweekfirstday = (TextView) view
					.findViewById(R.id.after_nextweekfirstday);
			after_nextweekfirstday.setOnClickListener(this);
			after_today = (TextView) view.findViewById(R.id.after_today);
			after_today.setOnClickListener(this);
			after_tommrow = (TextView) view.findViewById(R.id.after_tommrow);
			after_tommrow.setOnClickListener(this);
			after_today_view = view.findViewById(R.id.after_today_view);
			after_tommrow_view = view.findViewById(R.id.after_tommrow_view);
			after_onehour_view = view.findViewById(R.id.after_onehour_view);
			after_oneday_view = view.findViewById(R.id.after_oneday_view);
			after_oneweek_view = view.findViewById(R.id.after_oneweek_view);
			after_onemonth_view = view.findViewById(R.id.after_onemonth_view);
			after_nextweekfirstday_view = view
					.findViewById(R.id.after_nextweekfirstday_view);
			detail_date.setText(CharacterUtil.getWeekOfDate(getActivity(),
					DateUtil.parseDate(key)));
			Date dateStr = DateUtil.parseDateTime(key + " " + timeKey);
			Date dateToday = DateUtil.parseDateTime(DateUtil
					.formatDateTime(new Date()));
			long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
			long day = betweem / (24 * 3600);
			long hour = betweem % (24 * 3600) / 3600;
			long min = betweem % 3600 / 60;

			if (today.equals(key)) {// 今天
				if (DateUtil.parseDateTime(DateUtil.formatDateTime(new Date()))
						.after(DateUtil.parseDateTime(DateUtil
								.formatDateTime(dateStr)))) {
					if (Math.abs(hour) >= 1) {
						timebefore_tv.setText(Math.abs(hour) + "小时前");
					} else {
						timebefore_tv.setText(Math.abs(min) + "分钟前");
					}
				} else {
					if (Math.abs(hour) >= 1) {
						timebefore_tv.setText(Math.abs(hour) + "小时后");
					} else {
						timebefore_tv.setText(Math.abs(min) + "分钟后");
					}
				}
			} else if (tomorrow.equals(key)) {// 明天
				if (Math.abs(day) >= 1) {
					timebefore_tv.setText(Math.abs(day) + "天后");
				} else {
					timebefore_tv.setText(Math.abs(hour) + "小时后");

				}
			} else {
				timebefore_tv.setText(Math.abs(day) + 1 + "天后");
			}
			detail_important = (TextView) view
					.findViewById(R.id.detail_important);
			detail_important.setOnClickListener(this);
			// detail_edit.setVisibility(View.VISIBLE);
			if ("0".equals(mMap.get(ScheduleTable.schIsPostpone)))
				after_autopostone.setText("自动顺延");
			else
				after_autopostone.setText("取消顺延");
			Drawable jieshu = getResources().getDrawable(
					R.mipmap.btn_quxiaozhongyao);
			Drawable weijieshu = getResources().getDrawable(
					R.mipmap.btn_zhongyao);
			// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
			jieshu.setBounds(0, 0, jieshu.getMinimumWidth(),
					jieshu.getMinimumHeight());
			weijieshu.setBounds(0, 0, jieshu.getMinimumWidth(),
					jieshu.getMinimumHeight());
			if ("0".equals(mMap.get(ScheduleTable.schIsImportant))) {
				detail_important.setText("设为重要");
				detail_important.setCompoundDrawables(null, jieshu, null, null);
			} else {
				detail_important.setText("取消重要");
				detail_important.setCompoundDrawables(null, weijieshu, null,
						null);
			}
			int colortype = Integer.parseInt(mMap
					.get(ScheduleTable.schColorType));
			String colorname = application.QueryTagNameData(colortype).get(
					"ctgText");
			more_openstate_tv.setText("(" + colorname + ")");
			if (DateUtil.parseDate(key).getTime() <= DateUtil.parseDate(
					DateUtil.formatDate(new Date())).getTime()) {
				if (inWeekUtils.isInWeek(key)) {
					after_autopostone.setVisibility(View.VISIBLE);
					after_onehour.setVisibility(View.GONE);
					after_oneday.setVisibility(View.GONE);
					after_oneweek.setVisibility(View.VISIBLE);
					after_onemonth.setVisibility(View.VISIBLE);
					after_nextweekfirstday.setVisibility(View.VISIBLE);
					after_today.setVisibility(View.VISIBLE);
					after_tommrow.setVisibility(View.VISIBLE);
					after_today_view.setVisibility(View.VISIBLE);
					after_tommrow_view.setVisibility(View.VISIBLE);
					after_onehour_view.setVisibility(View.GONE);
					after_oneday_view.setVisibility(View.GONE);
					after_oneweek_view.setVisibility(View.VISIBLE);
					after_onemonth_view.setVisibility(View.VISIBLE);
					after_nextweekfirstday_view.setVisibility(View.VISIBLE);
				} else {
					if (inWeekUtils.isInMonth(key)) {
						after_autopostone.setVisibility(View.VISIBLE);
						after_onehour.setVisibility(View.GONE);
						after_oneday.setVisibility(View.GONE);
						after_oneweek.setVisibility(View.GONE);
						after_onemonth.setVisibility(View.VISIBLE);
						after_nextweekfirstday.setVisibility(View.VISIBLE);
						after_today.setVisibility(View.VISIBLE);
						after_tommrow.setVisibility(View.VISIBLE);
						after_today_view.setVisibility(View.VISIBLE);
						after_tommrow_view.setVisibility(View.VISIBLE);
						after_onehour_view.setVisibility(View.GONE);
						after_oneday_view.setVisibility(View.GONE);
						after_oneweek_view.setVisibility(View.GONE);
						after_onemonth_view.setVisibility(View.VISIBLE);
						after_nextweekfirstday_view.setVisibility(View.VISIBLE);
					} else {
						after_autopostone.setVisibility(View.VISIBLE);
						after_onehour.setVisibility(View.GONE);
						after_oneday.setVisibility(View.GONE);
						after_oneweek.setVisibility(View.GONE);
						after_onemonth.setVisibility(View.GONE);
						after_nextweekfirstday.setVisibility(View.VISIBLE);
						after_today.setVisibility(View.VISIBLE);
						after_tommrow.setVisibility(View.VISIBLE);
						after_today_view.setVisibility(View.VISIBLE);
						after_tommrow_view.setVisibility(View.VISIBLE);
						after_onehour_view.setVisibility(View.GONE);
						after_oneday_view.setVisibility(View.GONE);
						after_oneweek_view.setVisibility(View.GONE);
						after_onemonth_view.setVisibility(View.GONE);
						after_nextweekfirstday_view.setVisibility(View.VISIBLE);
					}
				}
			} else {
				boolean fag = inWeekUtils.getNextWeek(context, key);
				if (fag) {
					after_autopostone.setVisibility(View.VISIBLE);
					after_onehour.setVisibility(View.VISIBLE);
					after_oneday.setVisibility(View.VISIBLE);
					after_oneweek.setVisibility(View.VISIBLE);
					after_onemonth.setVisibility(View.VISIBLE);
					after_nextweekfirstday.setVisibility(View.VISIBLE);
					after_today.setVisibility(View.GONE);
					after_tommrow.setVisibility(View.GONE);
					after_today_view.setVisibility(View.GONE);
					after_tommrow_view.setVisibility(View.GONE);
					after_onehour_view.setVisibility(View.VISIBLE);
					after_oneday_view.setVisibility(View.VISIBLE);
					after_oneweek_view.setVisibility(View.VISIBLE);
					after_onemonth_view.setVisibility(View.VISIBLE);
					after_nextweekfirstday_view.setVisibility(View.VISIBLE);
				} else {
					after_autopostone.setVisibility(View.VISIBLE);
					after_onehour.setVisibility(View.VISIBLE);
					after_oneday.setVisibility(View.VISIBLE);
					after_oneweek.setVisibility(View.VISIBLE);
					after_onemonth.setVisibility(View.VISIBLE);
					after_nextweekfirstday.setVisibility(View.GONE);
					after_today.setVisibility(View.GONE);
					after_tommrow.setVisibility(View.GONE);
					after_today_view.setVisibility(View.GONE);
					after_tommrow_view.setVisibility(View.GONE);
					after_onehour_view.setVisibility(View.VISIBLE);
					after_oneday_view.setVisibility(View.VISIBLE);
					after_oneweek_view.setVisibility(View.VISIBLE);
					after_onemonth_view.setVisibility(View.VISIBLE);
					after_nextweekfirstday_view.setVisibility(View.GONE);
				}
			}
			// 设置菜单判断
			if (setType == 1) {
				after.setVisibility(View.GONE);
				detail.setVisibility(View.GONE);
				setting.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onClick(View v) {
			Animation translateIn0 = new TranslateAnimation(
					-mianView.getWidth(), 0, 0, 0);
			Animation translateIn1 = new TranslateAnimation(
					mianView.getWidth(), 0, 0, 0);
			translateIn0.setDuration(400);
			translateIn1.setDuration(400);
			App app = App.getDBcApplication();
			String key = mMap.get(ScheduleTable.schDate);
			String timeKey = mMap.get(ScheduleTable.schTime);
			Date dateStr;
			long datetime = 0;
			int id = Integer.parseInt(mMap.get(ScheduleTable.schID));
			String date;
			dateStr = DateUtil.parseDateTime(key + " " + timeKey);
			datetime = dateStr.getTime();
			Intent intent = new Intent();
			MySchBean mySchBean = new MySchBean();
			mySchBean.schID = mMap.get(ScheduleTable.schID);
			mySchBean.schContent = mMap.get(ScheduleTable.schContent);
			mySchBean.schDate = mMap.get(ScheduleTable.schDate);
			mySchBean.schTime = mMap.get(ScheduleTable.schTime);
			mySchBean.schIsAlarm = mMap.get(ScheduleTable.schIsAlarm);
			mySchBean.schBeforeTime = mMap.get(ScheduleTable.schBeforeTime);
			mySchBean.schDisplayTime = mMap.get(ScheduleTable.schDisplayTime);
			mySchBean.schIsPostpone = mMap.get(ScheduleTable.schIsPostpone);
			mySchBean.schIsImportant = mMap.get(ScheduleTable.schIsImportant);
			mySchBean.schColorType = mMap.get(ScheduleTable.schColorType);
			mySchBean.schIsEnd = mMap.get(ScheduleTable.schIsEnd);
			mySchBean.schCreateTime = mMap.get(ScheduleTable.schCreateTime);
			mySchBean.schTags = mMap.get(ScheduleTable.schTags);
			mySchBean.schSourceType = mMap.get(ScheduleTable.schSourceType);
			mySchBean.schSourceDesc = mMap.get(ScheduleTable.schSourceDesc);
			mySchBean.schSourceDescSpare = mMap
					.get(ScheduleTable.schSourceDescSpare);
			mySchBean.schRepeatID = mMap.get(ScheduleTable.schRepeatID);
			mySchBean.schRepeatDate = mMap.get(ScheduleTable.schRepeatDate);
			mySchBean.schUpdateTime = mMap.get(ScheduleTable.schUpdateTime);
			mySchBean.schUpdateState = mMap.get(ScheduleTable.schUpdateState);
			mySchBean.schOpenState = mMap.get(ScheduleTable.schOpenState);
			mySchBean.schRepeatLink = mMap.get(ScheduleTable.schRepeatLink);
			mySchBean.schRingDesc = mMap.get(ScheduleTable.schRingDesc);
			mySchBean.schRingCode = mMap.get(ScheduleTable.schRingCode);
			mySchBean.schcRecommendName = mMap
					.get(ScheduleTable.schcRecommendName);
			mySchBean.schRead = mMap.get(ScheduleTable.schRead);
			Map<String, String> map = App.getDBcApplication().QueryStateData(
					Integer.parseInt(mMap.get(ScheduleTable.schRepeatID)));
			switch (v.getId()) {
			case R.id.detail_close:
				if (closeType != 0) {
					hint();
					// detail_edit.setVisibility(View.VISIBLE);
					detail.setVisibility(View.VISIBLE);
					detail.startAnimation(translateIn0);
					closeType = 0;
				} else {
					dialog.dismiss();
				}
				break;
			case R.id.detail_edit_ll:
				String i = mMap.get(ScheduleTable.schID);
				intent.putExtra("id", i);
				intent.putExtra("content", detail_content.getText().toString());
				intent.putExtra("year", key);
				intent.putExtra("time", detail_time_date.getText().toString());
				intent.putExtra("week", detail_date.getText().toString());
				intent.putExtra("tixing", timebefore_tv.getText().toString());
				intent.putExtra("beforetime",
						mMap.get(ScheduleTable.schBeforeTime));
				intent.putExtra("openState",
						mMap.get(ScheduleTable.schOpenState));
				intent.putExtra("lingshengname",
						mMap.get(ScheduleTable.schRingDesc));
				intent.putExtra("ringcode", mMap.get(ScheduleTable.schRingCode));
				intent.putExtra("recommendID",
						mMap.get(ScheduleTable.schcRecommendId));
				intent.putExtra("recommendname",
						mMap.get(ScheduleTable.schcRecommendName));
				intent.putExtra("repid", mMap.get(ScheduleTable.schRepeatID));
				intent.putExtra("repdate",
						mMap.get(ScheduleTable.schRepeatDate));
				intent.putExtra("replink",
						mMap.get(ScheduleTable.schRepeatLink));
				intent.putExtra("aid", mMap.get(ScheduleTable.schAID));
				intent.putExtra("friendID", mMap.get(ScheduleTable.schFriendID));
				intent.putExtra("schIsAlarm",
						mMap.get(ScheduleTable.schIsAlarm));
				intent.putExtra("postpone",
						mMap.get(ScheduleTable.schIsPostpone));
				intent.putExtra("important",
						mMap.get(ScheduleTable.schIsImportant));
				intent.putExtra("coclor", mMap.get(ScheduleTable.schColorType));
				intent.putExtra("isEnd", mMap.get(ScheduleTable.schIsEnd));
				intent.putExtra("displaytime",
						mMap.get(ScheduleTable.schDisplayTime));
				startActivity(intent.setClass(getActivity(),
						AddEverydayDetailTaskActivity.class));
				dialog.dismiss();
				break;
			case R.id.detail_after:
				hint();
				after.setVisibility(View.VISIBLE);
				detail_zhuanfa.setVisibility(View.GONE);
				after.startAnimation(translateIn1);
				break;
			case R.id.detail_more:
				hint();
				detail_zhuanfa.setVisibility(View.GONE);
				setting.setVisibility(View.VISIBLE);
				setting.startAnimation(translateIn1);
				break;

			// ---------------更多子项事件--------------------
			case R.id.more_openstate:// 谁可以看
				try {
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					// loadData();
					adapter.notifyDataSetChanged();
					intent.putExtra("statename", more_openstate_tv.getText()
							.toString());
					intent.putExtra("id", mMap.get(ScheduleTable.schID));
					startActivity(intent.setClass(context,
							MyStateActivity.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.detail_zhuanfa:
				intent.putExtra("bean", mySchBean);
				startActivity(intent
						.setClass(context, SchZhuanFaActivity.class));
				dialog.dismiss();
				break;
			case R.id.more_zhuanfasjb:// 转发时间表好友
				intent.putExtra("bean", mySchBean);
				startActivity(intent
						.setClass(context, SchZhuanFaActivity.class));
				dialog.dismiss();
				break;
			case R.id.more_zhuanfawx:// 转发微信好友
				ShareSDK.initSDK(getActivity());
				OnekeyShare oks = new OnekeyShare();
				// 关闭sso授权
				oks.disableSSOWhenAuthorize();
				// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
				// oks.setNotification(R.drawable.ic_launcher,
				// getString(R.string.app_name));
				// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
				// oks.setTitle(title);
				// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
				// oks.setTitleUrl(path);
				// text是分享文本，所有平台都需要这个字段
				oks.setText(mySchBean.schDate + "  " + mySchBean.schTime + "  "
						+ mySchBean.schContent);
				// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
				// oks.setImagePath(ParameterUtil.userHeadImg+imageUrl+"&imageType=2&imageSizeType=3");//
				// 确保SDcard下面存在此张图片
				// url仅在微信（包括好友和朋友圈）中使用
				// oks.setUrl(path);
				// oks.setImageUrl(URLConstants.图片+imageUrl+"&imageType=2&imageSizeType=3");
				// comment是我对这条分享的评论，仅在人人网和QQ空间使用
				// oks.setComment("我是测试评论文本");
				// site是分享此内容的网站名称，仅在QQ空间使用
				// oks.setSite(getString(R.string.app_name));
				// siteUrl是分享此内容的网站地址，仅在QQ空间使用
				// oks.setSiteUrl("http://sharesdk.cn");

				// 启动分享GUI
				oks.show(getActivity());
				dialog.dismiss();
				break;
			case R.id.more_setdaiban:// 设为待办 今天+全天+顺延
				try {
					app.updateScheduleUnTaskData(mySchBean.schID);
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					app.updateUnTaskClockDate(
							Integer.parseInt(mMap.get(ScheduleTable.schID)),
							ringdesc, ringcode);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					loadData();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					adapter.notifyDataSetChanged();
					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.more_shitingmusic:// 试听铃声
				final MediaPlayer mediaPlayer = new MediaPlayer();

				try {
					AssetFileDescriptor fileDescriptor = getActivity()
							.getAssets().openFd(
									mMap.get(ScheduleTable.schRingCode)
											+ ".mp3");
					mediaPlayer.setDataSource(
							fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.more_delete:// 删除
				try {
					alertDeleteDialog(mMap, 0, position);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			// --------------推后子项--------------------
			case R.id.after_autopostone:
				updateSchedule(mMap, ScheduleTable.schIsPostpone,
						ScheduleTable.schUpdateState);
				updateSchClock(mMap, LocateAllNoticeTable.postpone);
				updateFocusStateSch(mMap, ScheduleTable.schFocusState);
				calendar.setTime(DateUtil.parseDate(mMap
						.get(ScheduleTable.schDate)));
				if (DateUtil.parseDate(mMap.get(ScheduleTable.schDate)).before(
						DateUtil.parseDate(DateUtil.formatDate(new Date())))) {
					calendar.set(Calendar.DAY_OF_MONTH,
							calendar.get(Calendar.DAY_OF_MONTH) + 1);
					date = DateUtil.formatDate(calendar.getTime());
					app.updateScheduleDateData(id, date,
							mMap.get(ScheduleTable.schTime));
				}

				app.updateSchReadData(
						Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
				App.getDBcApplication().updateSchRepeatLinkData(
						Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
				if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
						|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

				} else {
					if (map != null) {
						String repdate = mMap
								.get(ScheduleTable.schRepeatDate);
						String lastdate = StringUtils.getIsStringEqulesNull(map
								.get(CLRepeatTable.repDateOne));
						String nextdate = StringUtils.getIsStringEqulesNull(map
								.get(CLRepeatTable.repDateTwo));
						repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
								.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
					}
				}
				loadData();
				QueryAlarmData
						.writeAlarm(getActivity().getApplicationContext());

				adapter.notifyDataSetChanged();
				RefreshMyData();
				dialog.dismiss();
				break;
			case R.id.after_onehour:
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					date = inWeekUtils.AfterOneHours(key,
							mMap.get(ScheduleTable.schTime));
					app.updateScheduleDateData(id, date.substring(0, 10)
							.toString(), date.substring(11, 16).toString());
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_oneday:
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					date = inWeekUtils.AfterOneDay(key);
					app.updateScheduleDateData(id, date,
							mMap.get(ScheduleTable.schTime));
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_oneweek:
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					date = inWeekUtils.AfterOneWeek(key);
					app.updateScheduleDateData(id, date,
							mMap.get(ScheduleTable.schTime));
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_onemonth:// 推后一个月
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					date = inWeekUtils.AfterOneMonth(key);
					app.updateScheduleDateData(id, date,
							mMap.get(ScheduleTable.schTime));
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_nextweekfirstday:// 推后的下周一
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					date = inWeekUtils.AfterNextWeekFirstDay(context, key);
					app.updateScheduleDateData(id, date,
							mMap.get(ScheduleTable.schTime));
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_today:
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					app.updateScheduleDateData(id, today,
							mMap.get(ScheduleTable.schTime));
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_tommrow:
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					app.updateScheduleDateData(id, tomorrow,
							mMap.get(ScheduleTable.schTime));
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					App.getDBcApplication().updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
							|| "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

					} else {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					QueryAlarmData.writeAlarm(getActivity()
							.getApplicationContext());

					RefreshMyData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			// --------------详情子项事件----------------

			case R.id.detail_important:
				try {
					updateFocusStateSch(mMap, ScheduleTable.schFocusState);
					app.updateSchReadData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					app.updateSchRepeatLinkData(
							Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
					if (!"".equals(mMap.get(ScheduleTable.schRepeatDate))
							&& !"0".equals(mMap
									.get(ScheduleTable.schRepeatLink))) {
						if (map != null) {
							String repdate = mMap
									.get(ScheduleTable.schRepeatDate);
							String lastdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateOne));
							String nextdate = StringUtils.getIsStringEqulesNull(map
									.get(CLRepeatTable.repDateTwo));
							repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
									.get(ScheduleTable.schRepeatID)),repdate,nextdate,lastdate,map);
						}
						updateRepSchUpdate(mMap, ScheduleTable.schIsImportant,
								ScheduleTable.schUpdateState);
					} else {
						updateSchedule(mMap, ScheduleTable.schIsImportant,
								ScheduleTable.schUpdateState);
					}
					mList.remove(position);
					mMap.put(ScheduleTable.schRepeatLink, "0");
					mList.add(position, mMap);
					RefreshMyData();
					adapter.notifyDataSetChanged();
					important_listview.invalidate();
					if (DateUtil.parseDateTime(
							DateUtil.formatDateTime(new Date())).getTime() >= DateUtil
							.parseDateTime(
									mMap.get(ScheduleTable.schDate) + " "
											+ mMap.get(ScheduleTable.schTime))
							.getTime()) {
						QueryAlarmData.writeAlarm(getActivity()
								.getApplicationContext());

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			}
		}

		private void hint() {
			closeType = 1;
			after.setVisibility(View.GONE);
			setting.setVisibility(View.GONE);
			detail.setVisibility(View.GONE);
		}
	}

	// private void deleteDialog(final String id, final Map<String, String>
	// mMap,
	// final int type) {
	// final AlertDialog builder = new AlertDialog.Builder(context).create();
	// builder.show();
	// Window window = builder.getWindow();
	// android.view.WindowManager.LayoutParams params =
	// window.getAttributes();// 获取LayoutParams
	// params.alpha = 0.92f;
	// window.setAttributes(params);// 设置生效
	// window.setContentView(R.layout.dialog_alterdelete);
	// TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
	// delete_ok.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// builder.cancel();
	// if (type == 0) {
	// String deleteId = id;
	// App dbContextExtended = App.getDBcApplication();
	// dbContextExtended.deleteScheduleData(deleteId);
	// mList.clear();
	// loadData();
	// adapter.notifyDataSetChanged();
	// }
	// }
	// });
	// TextView delete_canel = (TextView) window
	// .findViewById(R.id.delete_canel);
	// delete_canel.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// builder.cancel();
	// }
	// });
	// TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
	// if (type == 0) {
	// delete_tv.setText("确定要删除此记事吗?");
	// } else {
	// delete_tv.setText("结束今天之前所有未结束的记事?");
	// }
	//
	// }
	private void updateRepSchUpdate(Map<String, String> mMap, String key,
			String key1) {
		try {
			String value = "0";
			Map<String, String> upMap = new HashMap<String, String>();
			important_listview.hiddenRight();
			if ("0".equals(mMap.get(key)))
				value = "1";
			else
				value = "0";
			upMap.put(key, value);
			if (!"0".equals(upMap.get(ScheduleTable.schAID))) {
				upMap.put(key1, "1");
			} else {
				upMap.put(key1, "1");
			}
			App.getDBcApplication().updateScheduleData(upMap,
					"where schID=" + mMap.get("schID"));
			mMap.put(key, value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void updateFocusStateSch(Map<String, String> mMap, String key) {
		try {
			Map<String, String> upMap = new HashMap<String, String>();
			upMap.put(key, "1");
			App.getDBcApplication().updateSchFocusState(upMap,
					"where schID=" + mMap.get("schID"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void updateSchedule(Map<String, String> mMap, String key,
			String key1) {
		String value = "0";
		Map<String, String> upMap = new HashMap<String, String>();
		important_listview.hiddenRight();
		if ("0".equals(mMap.get(key)))
			value = "1";
		else
			value = "0";
		upMap.put(key, value);
		upMap.put(key1, "2");
		App.getDBcApplication().updateScheduleData(upMap,
				"where schID=" + mMap.get("schID"));
		mMap.put(key, value);
	}

	private void updateScheduleIsEnd(Map<String, String> mMap, String key,
			String key1) {
		try {
			if ("1".equals(mMap.get(ScheduleTable.schRepeatLink))
					|| "3".equals(mMap.get(ScheduleTable.schRepeatLink))) {
				repeatSetChildEndUtils.setParentStateIsEnd(mMap);
			}
			String value = "0";
			Map<String, String> upMap = new HashMap<String, String>();
			important_listview.hiddenRight();
			if ("0".equals(mMap.get(key)))
				value = "1";
			else
				value = "0";
			upMap.put(key, value);
			if ("1".equals(mMap.get(ScheduleTable.schRepeatLink))
					|| "3".equals(mMap.get(ScheduleTable.schRepeatLink))) {
				upMap.put(key1, "0");
			} else {
				upMap.put(key1, "2");
			}
			App.getDBcApplication().updateScheduleData(upMap,
					"where schID=" + mMap.get("schID"));
			mMap.put(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateSchClock(Map<String, String> mMap, String key) {
		try {
			String value = "0";
			String key1 = "";
			Map<String, String> upMap = new HashMap<String, String>();
			if (key.equals("isEnd")) {
				key1 = "schIsEnd";
			} else if (key.equals("postpone")) {
				key1 = "schIsPostpone";
			}
			if ("1".equals(mMap.get(key1)))
				value = "1";
			else
				value = "0";
			upMap.put(key, value);
			App.getDBcApplication().updateSchIsEnd(upMap,
					"where schID=" + mMap.get("schID"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
