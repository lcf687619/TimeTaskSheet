package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.adapter.RepeatFriendsRiChengAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.FriendsChongFuBackBean;
import com.mission.schedule.bean.FriendsChongFuBean;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.comparator.RepeatDayComparator;
import com.mission.schedule.comparator.RepeatYearComparator;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RepeatFriendsRiChengActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.myrepeatfriends_title)
	private TextView myrepeatfriends_title;
	@ViewResId(id = R.id.repeatfriends_lv)
	private ListView repeatfriends_lv;
	@ViewResId(id = R.id.addrepeat_bt)
	private Button addrepeat_bt;

	String name;
	String uid;
	RepeatFriendsRiChengAdapter adapter = null;
	Context context;
	List<Integer> listItemID = new ArrayList<Integer>();
	List<FriendsChongFuBean> addRepeatList = new ArrayList<FriendsChongFuBean>();

	List<FriendsChongFuBean> AllList = new ArrayList<FriendsChongFuBean>();
	List<FriendsChongFuBean> everydayList = new ArrayList<FriendsChongFuBean>();
	List<FriendsChongFuBean> workdayList = new ArrayList<FriendsChongFuBean>();
	List<FriendsChongFuBean> everyweekList = new ArrayList<FriendsChongFuBean>();
	List<FriendsChongFuBean> everymonthList = new ArrayList<FriendsChongFuBean>();
	List<FriendsChongFuBean> everyyearList = new ArrayList<FriendsChongFuBean>();
	List<FriendsChongFuBean> backList = new ArrayList<FriendsChongFuBean>();

	ProgressUtil progressUtil = new ProgressUtil();

	@Override
	protected void setListener() {
		top_ll_left.setOnClickListener(this);
		addrepeat_bt.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_repeatfriendsricheng);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		uid = getIntent().getStringExtra("uid");
		name = getIntent().getStringExtra("name");
		myrepeatfriends_title.setText(name);
		loadData();
	}

	private void loadData() {
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			String path = URLConstants.好友重复日程 + "?repUid=" + uid;
			RepeatFriendsRiChengAsync(path);
		} else {
			Toast.makeText(context, "请检查您的网络!", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_left:
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;
		case R.id.addrepeat_bt:
			try {
				listItemID.clear();
				for (int i = 0; i < adapter.mChecked.size(); i++) {
					if (adapter.mChecked.get(i)) {
						listItemID.add(i);
					}
				}
				if (listItemID.size() == 0) {
					Toast.makeText(context, "没有选中任何选项!", Toast.LENGTH_SHORT)
							.show();
					return;
				} else {
					App app = App.getDBcApplication();
					for (int i = 0; i < listItemID.size(); i++) {
						addRepeatList.add((FriendsChongFuBean) repeatfriends_lv
								.getAdapter().getItem(listItemID.get(i)));
						RepeatBean bean = null;
						if (addRepeatList.get(i).recommendedUserId != null
								&& !"".equals(addRepeatList.get(i).recommendedUserId)) {
							if (addRepeatList.get(i).repType == 1) {
								bean = RepeatDateUtils
										.saveCalendar(
												addRepeatList.get(i).repTime,
												1, "", "");
							} else if (addRepeatList.get(i).repType == 2) {
								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										2,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "");
							} else if (addRepeatList.get(i).repType == 3) {
								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										3,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "");
							} else if (addRepeatList.get(i).repType == 4) {
								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										4,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "0");
							} else if (addRepeatList.get(i).repType == 6) {

								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										4,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "1");
							} else {
								bean = RepeatDateUtils
										.saveCalendar(
												addRepeatList.get(i).repTime,
												5, "", "");
							}
							app.insertCLRepeatTableData(
									addRepeatList.get(i).repBeforeTime,
									addRepeatList.get(i).repColorType,
									addRepeatList.get(i).repDisplayTime,
									addRepeatList.get(i).repType,
									addRepeatList.get(i).repIsAlarm,
									addRepeatList.get(i).repIsPuase,
									addRepeatList.get(i).repIsImportant,
									addRepeatList.get(i).repSourceType,
									1,
									addRepeatList.get(i).repTypeParameter
											.replace("\\", ""),
									bean.repNextCreatedTime,
									bean.repLastCreatedTime,
									DateUtil.formatDateTime(new Date()),
									addRepeatList.get(i).repStartDate,
									addRepeatList.get(i).repContent,
									DateUtil.formatDateTime(new Date()),
									addRepeatList.get(i).repSourceDesc,
									addRepeatList.get(i).repSourceDescSpare,
									addRepeatList.get(i).repTime,
									addRepeatList.get(i).repRingDesc,
									addRepeatList.get(i).repRingCode,
									addRepeatList.get(i).repUpdateTime,
									addRepeatList.get(i).repOpenState,
									addRepeatList.get(i).recommendedUserName,
									Integer.parseInt(addRepeatList.get(i).recommendedUserId),
									"", "", 0, 0, addRepeatList.get(i).aType,
									addRepeatList.get(i).webUrl, addRepeatList
											.get(i).imgPath, addRepeatList
											.get(i).repInSTable, 0,
									addRepeatList.get(i).parReamrk, 0, 0);
							if (addRepeatList.get(i).repInSTable == 0) {
								app.insertScheduleData(
										addRepeatList.get(i).repContent,
										bean.repNextCreatedTime
												.substring(0, 10),
										addRepeatList.get(i).repTime,
										addRepeatList.get(i).repIsAlarm,
										addRepeatList.get(i).repBeforeTime,
										addRepeatList.get(i).repDisplayTime,
										0,
										addRepeatList.get(i).repIsImportant,
										addRepeatList.get(i).repColorType,
										addRepeatList.get(i).repIsPuase,
										DateUtil.formatDateTime(new Date()),
										"",
										addRepeatList.get(i).repSourceType,
										addRepeatList.get(i).repSourceDesc,
										addRepeatList.get(i).repSourceDescSpare,
										App.repschId,
										bean.repNextCreatedTime,
										addRepeatList.get(i).repUpdateTime,
										0,
										addRepeatList.get(i).repOpenState,
										1,
										addRepeatList.get(i).repRingDesc,
										addRepeatList.get(i).repRingCode,
										addRepeatList.get(i).recommendedUserName,
										0, 0, addRepeatList.get(i).aType,
										addRepeatList.get(i).webUrl,
										addRepeatList.get(i).imgPath, 0, 0, 0);
							}
						} else {
							if (addRepeatList.get(i).repType == 1) {
								bean = RepeatDateUtils
										.saveCalendar(
												addRepeatList.get(i).repTime,
												1, "", "");
							} else if (addRepeatList.get(i).repType == 2) {
								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										2,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "");
							} else if (addRepeatList.get(i).repType == 3) {
								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										3,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "");
							} else if (addRepeatList.get(i).repType == 4) {
								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										4,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "0");
							} else if (addRepeatList.get(i).repType == 6) {
								bean = RepeatDateUtils.saveCalendar(
										addRepeatList.get(i).repTime,
										4,
										addRepeatList.get(i).repTypeParameter
												.replace("\"", "")
												.replace("\\", "")
												.replace("[", "")
												.replace("]", ""), "1");
							} else {
								bean = RepeatDateUtils
										.saveCalendar(
												addRepeatList.get(i).repTime,
												5, "", "");
							}
							app.insertCLRepeatTableData(
									addRepeatList.get(i).repBeforeTime,
									addRepeatList.get(i).repColorType,
									addRepeatList.get(i).repDisplayTime,
									addRepeatList.get(i).repType, addRepeatList
											.get(i).repIsAlarm, addRepeatList
											.get(i).repIsPuase, addRepeatList
											.get(i).repIsImportant,
									addRepeatList.get(i).repSourceType, 1,
									addRepeatList.get(i).repTypeParameter
											.replace("\\", ""),
									bean.repNextCreatedTime,
									bean.repLastCreatedTime, DateUtil
											.formatDateTime(new Date()),
									addRepeatList.get(i).repStartDate,
									addRepeatList.get(i).repContent, DateUtil
											.formatDateTime(new Date()),
									addRepeatList.get(i).repSourceDesc,
									addRepeatList.get(i).repSourceDescSpare,
									addRepeatList.get(i).repTime, addRepeatList
											.get(i).repRingDesc, addRepeatList
											.get(i).repRingCode, addRepeatList
											.get(i).repUpdateTime,
									addRepeatList.get(i).repOpenState,
									addRepeatList.get(i).recommendedUserName,
									0, "", "", 0, 0,
									addRepeatList.get(i).aType, addRepeatList
											.get(i).webUrl, addRepeatList
											.get(i).imgPath, addRepeatList
											.get(i).repInSTable, 0,
									addRepeatList.get(i).parReamrk, 0, 0);
							if (addRepeatList.get(i).repInSTable == 0) {
								app.insertScheduleData(
										addRepeatList.get(i).repContent,
										bean.repNextCreatedTime
												.substring(0, 10),
										addRepeatList.get(i).repTime,
										addRepeatList.get(i).repIsAlarm,
										addRepeatList.get(i).repBeforeTime,
										addRepeatList.get(i).repDisplayTime,
										0,
										addRepeatList.get(i).repIsImportant,
										addRepeatList.get(i).repColorType,
										addRepeatList.get(i).repIsPuase,
										DateUtil.formatDateTime(new Date()),
										"",
										addRepeatList.get(i).repSourceType,
										addRepeatList.get(i).repSourceDesc,
										addRepeatList.get(i).repSourceDescSpare,
										App.repschId,
										bean.repNextCreatedTime,
										addRepeatList.get(i).repUpdateTime,
										0,
										addRepeatList.get(i).repOpenState,
										1,
										addRepeatList.get(i).repRingDesc,
										addRepeatList.get(i).repRingCode,
										addRepeatList.get(i).recommendedUserName,
										0, 0, addRepeatList.get(i).aType,
										addRepeatList.get(i).webUrl,
										addRepeatList.get(i).imgPath, 0, 0, 0);
							}
						}
						// if (addRepeatList.get(i).repBeforeTime == 0) {
						//
						// app.insertClockData(dateFormat.format(yyyyMMddHHmm.parse(bean.repNextCreatedTime)),
						// addRepeatList.get(i).repContent,
						// addRepeatList.get(i).repBeforeTime,
						// dateFormat.format(yyyyMMddHHmm.parse(bean.repNextCreatedTime)),
						// addRepeatList
						// .get(i).repRingDesc, addRepeatList
						// .get(i).repRingCode, addRepeatList
						// .get(i).repDisplayTime, 0,
						// addRepeatList.get(i).repType, 0,
						// App.repschId,
						// addRepeatList.get(i).repIsAlarm, 0,
						// addRepeatList.get(i).repTypeParameter
						// .replace("\\", ""));
						// } else {
						// String alarmResultTime = dateFormat
						// .format(yyyyMMddHHmm.parse(
						// bean.repNextCreatedTime).getTime()
						// - addRepeatList.get(i).repBeforeTime
						// * 60 * 1000);
						//
						// app.insertClockData(alarmResultTime, addRepeatList
						// .get(i).repContent,
						// addRepeatList.get(i).repBeforeTime,
						// dateFormat.format(yyyyMMddHHmm.parse(bean.repNextCreatedTime)),
						// addRepeatList
						// .get(i).repRingDesc, addRepeatList
						// .get(i).repRingCode, addRepeatList
						// .get(i).repDisplayTime, 0,
						// addRepeatList.get(i).repType, 0,
						// App.repschId,
						// addRepeatList.get(i).repIsAlarm, 0,
						// addRepeatList.get(i).repTypeParameter
						// .replace("\\", ""));
						// }
						QueryAlarmData.writeAlarm(getApplicationContext());
					}
					Intent intent2 = new Intent();
					setResult(Activity.RESULT_OK, intent2);
					this.finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private void RepeatFriendsRiChengAsync(String path) {
		progressUtil.ShowProgress(context, true, true, "正在加载...");
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							try {
								FriendsChongFuBackBean backBean = gson
										.fromJson(result,
												FriendsChongFuBackBean.class);
								if (backBean.status == 0) {
									backList = backBean.lists;
									if (backList != null && backList.size() > 0) {
										for (int i = 0; i < backList.size(); i++) {
											if (backList.get(i).repType == 1) {
												everydayList.add(backList
														.get(i));
											} else if (backList.get(i).repType == 2) {
												everyweekList.add(backList
														.get(i));
												Collections
														.sort(everyweekList,
																new RepeatDayComparator());
											} else if (backList.get(i).repType == 3) {
												everymonthList.add(backList
														.get(i));
												Collections
														.sort(everymonthList,
																new RepeatDayComparator());
											} else if (backList.get(i).repType == 4) {
												everyyearList.add(backList
														.get(i));
												Collections
														.sort(everyyearList,
																new RepeatYearComparator());
											} else {
												workdayList.add(backList.get(i));
											}
										}
										AllList.addAll(everydayList);
										AllList.addAll(workdayList);
										AllList.addAll(everyweekList);
										AllList.addAll(everymonthList);
										AllList.addAll(everyyearList);
										adapter = new RepeatFriendsRiChengAdapter(
												context, AllList, mScreenWidth);
										repeatfriends_lv.setAdapter(adapter);
									} else {
										return;
									}
								} else {
									return;
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
