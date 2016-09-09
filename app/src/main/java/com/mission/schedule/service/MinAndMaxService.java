package com.mission.schedule.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.activity.MainActivity;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewFocusDeleteRepDataBean;
import com.mission.schedule.bean.NewFocusDeleteSchDataBean;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueBeen;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueListBeen;
import com.mission.schedule.bean.SetBackBean;
import com.mission.schedule.bean.SetBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.bean.TagBackBean;
import com.mission.schedule.bean.TagBean;
import com.mission.schedule.bean.TagDelBean;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.SharedPrefUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

public class MinAndMaxService extends Service {

	SharedPrefUtil sharedPrefUtil = null;
	App app = null;
	String userId;
	String downtagtime = "";// 新标签下载时间
	String updatesettime;// 设置的更新时间
	String myfrist = "0";
	String focusname = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (intent == null) {
					myfrist = "0";
				} else {
					myfrist = intent.getStringExtra("myfrist");
				}
				app = App.getDBcApplication();
				sharedPrefUtil = new SharedPrefUtil(getApplication(),
						ShareFile.USERFILE);
				userId = sharedPrefUtil.getString(getApplication(),
						ShareFile.USERFILE, ShareFile.USERID, "0");
				updatesettime = sharedPrefUtil.getString(getApplication(),
						ShareFile.USERFILE, ShareFile.UPDATESETTIME, "");
				if ("1".equals(myfrist)) {
					MainActivity.CheckCreateRepeatSchData();
					WriteAlarmClock.writeAlarm(getApplicationContext());
				}
				JiFen();
				AlterMAC();
				queryset();
				updateLoginDate();
				downTag();
				DownShcData();
				// setFiveMinuteRepeat();
			}
		}).start();
	}

	private void updateLoginDate() {
		System.out.println("MinAndMaxService的线程threadID:"
				+ Thread.currentThread().getId());
		String date = sharedPrefUtil.getString(getApplication(),
				ShareFile.USERFILE, ShareFile.ENDLOGINDATE, "");
		// if ("".equals(date)) {
		// String path = URLConstants.更新用户最后登录时间 + userId + "&userOpenId=1";
		// new updateLoginDateAsync().execute(path);
		// } else if (!dateFormat.format(new Date()).equals(date)) {
		String path = URLConstants.更新用户最后登录时间 + userId
				+ "&userOpenId=1&version=5.5.9&phoneType="
				+ android.os.Build.MANUFACTURER + "-"
				+ android.os.Build.MODEL.replace(" ", "");// android.os.Build.MODEL
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {

					@Override
					public void onResponse(String s) {
						if (!TextUtils.isEmpty(s)) {
							try {
								Gson gson = new Gson();
								SuccessOrFailBean bean = gson.fromJson(s,
										SuccessOrFailBean.class);
								if (bean.status == 0) {
									sharedPrefUtil.putString(getApplication(),
											ShareFile.USERFILE,
											ShareFile.ENDLOGINDATE,
											DateUtil.formatDate(new Date()));
								}
							}catch (Exception e){
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
		App.getHttpQueues().add(request);
		// new updateLoginDateAsync().execute(path.trim());
		// } else {
		//
		// }

	}

	/**
	 * 修改推送mac地址
	 */
	private void AlterMAC() {
		if (NetUtil.getConnectState(getApplication()) != NetWorkState.NONE) {
			final String path = URLConstants.修改MAC地址 + userId + "&uClintAddr="
					+ JPushInterface.getUdid(getApplication())
					+ "&uTocode=android";
			StringRequest request = new StringRequest(Request.Method.GET, path,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String s) {

						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError volleyError) {
						}
					});
			request.setTag("down");
			request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
			App.getHttpQueues().add(request);
			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// try {
			// HttpUtil.HttpClientDoGet(path);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			// }).start();
		} else {
			return;
		}
	}

	/**
	 * 积分计算
	 */
	private void JiFen() {
		if (NetUtil.getConnectState(getApplication()) != NetWorkState.NONE) {
			String path = URLConstants.积分计算 + userId + "&score=0&ryType=0";
			StringRequest request = new StringRequest(Request.Method.GET, path,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String s) {
							if (!TextUtils.isEmpty(s)) {
								try {
									Gson gson = new Gson();
									SuccessOrFailBean bean = gson.fromJson(s,
											SuccessOrFailBean.class);
									if (bean.status == 0) {

									}
								} catch (JsonSyntaxException e) {
									e.printStackTrace();
								}
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError volleyError) {
						}
					});
			request.setTag("down");
			request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
			App.getHttpQueues().add(request);
			// new JiFenAsync().execute(path);
		} else {
			return;
		}
	}

	/*
	 * 下载新标签
	 */
	private void downTag() {
		downtagtime = sharedPrefUtil.getString(getApplication(),
				ShareFile.USERFILE, ShareFile.DOWNTAGDATE,
				"2016-01-01%2B00:00:00");
		if ("".equals(downtagtime)) {
			downtagtime = "2016-01-01%2B00:00:00";
		}
		String downtagpath = URLConstants.标签下载 + userId + "&changeTime="
				+ downtagtime;
		StringRequest request = new StringRequest(Request.Method.GET,
				downtagpath, new Response.Listener<String>() {
					@Override
					public void onResponse(String s) {
						if (!TextUtils.isEmpty(s)) {
							try {
								List<TagBean> tagBeans = null;
								List<TagDelBean> delBeans = null;
								Gson gson = new Gson();
								TagBackBean backBean = gson.fromJson(s,
										TagBackBean.class);
								if (backBean.status == 0) {
									sharedPrefUtil.putString(getApplication(),
											ShareFile.USERFILE,
											ShareFile.DOWNTAGDATE,
											backBean.downTime.replace("T",
													"%2B"));
									tagBeans = backBean.list;
									delBeans = backBean.delList;
									if (tagBeans != null && tagBeans.size() > 0) {
										for (TagBean tagBean : tagBeans) {
											String tagtext = "";
											String ctgColor = "";
											if (!"".equals(StringUtils
													.getIsStringEqulesNull(tagBean.tagName))) {
												tagtext = tagBean.tagName;
											} else {
												tagtext = "";
											}
											if (!"".equals(StringUtils
													.getIsStringEqulesNull(tagBean.color))) {
												ctgColor = tagBean.color;
											} else {
												ctgColor = "0";
											}
											if (tagBean.uid != 0) {
												int resultcount = app
														.CheckCountTagData(tagBean.id);
												if (resultcount == 0) {
													app.insertTagIntenetData(
															tagBean.id,
															tagtext,
															tagBean.orderIndex,
															ctgColor,
															tagBean.stateTag,
															0, "", "");
												} else {
													app.updateTagData(
															tagBean.id,
															tagtext,
															tagBean.orderIndex,
															ctgColor,
															tagBean.stateTag,
															0, "", "");
												}
											}
										}
										int count = app.CheckCountTagData();
										if (count == 0) {
											for (TagBean tagBean : tagBeans) {
												String tagtext = "";
												String ctgColor = "";
												if (!"".equals(StringUtils
														.getIsStringEqulesNull(tagBean.tagName))) {
													tagtext = tagBean.tagName;
												} else {
													tagtext = "";
												}
												if (!"".equals(StringUtils
														.getIsStringEqulesNull(tagBean.color))) {
													ctgColor = tagBean.color;
												} else {
													ctgColor = "0";
												}
												if (tagBean.uid == 0
														&& tagBean.stateTag == 0) {
													int resultcount = app
															.CheckCountTagData(tagBean.id);
													if (resultcount == 0) {
														app.insertTagIntenetData(
																tagBean.id,
																tagtext,
																tagBean.orderIndex,
																ctgColor,
																tagBean.stateTag,
																0, "", "");
													}
												} else {
													app.insertTagData(tagtext,
															tagBean.orderIndex,
															ctgColor,
															tagBean.stateTag,
															1, "", "");
												}
											}
										} else {
											for (TagBean tagBean : tagBeans) {
												String tagtext = "";
												String ctgColor = "";
												if (!"".equals(StringUtils
														.getIsStringEqulesNull(tagBean.tagName))) {
													tagtext = tagBean.tagName;
												} else {
													tagtext = "";
												}
												if (!"".equals(StringUtils
														.getIsStringEqulesNull(tagBean.color))) {
													ctgColor = tagBean.color;
												} else {
													ctgColor = "0";
												}
												if (tagBean.uid == 0
														&& tagBean.stateTag == 0) {
													int resultcount = app
															.CheckCountTagData(tagBean.id);
													if (resultcount == 0) {
														app.insertTagIntenetData(
																tagBean.id,
																tagtext,
																tagBean.orderIndex,
																ctgColor,
																tagBean.stateTag,
																0, "", "");
													}
												}
											}
										}

									}
									if (delBeans != null && delBeans.size() > 0) {
										for (int i = 0; i < delBeans.size(); i++) {
											try {
												int count = app
														.CheckCountTagData(Integer
																.parseInt(delBeans
																		.get(i).dataId));
												if (count != 0) {
													app.deleteTagData(Integer.parseInt(delBeans
															.get(i).dataId));
												}
											} catch (NumberFormatException e) {
												e.printStackTrace();
											}
										}
									}
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						Intent intent = new Intent(getApplication(),
								UpdataTagService.class);
						intent.setAction("updateData");
						intent.setPackage(getPackageName());
						startService(intent);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Intent intent = new Intent(getApplication(),
								UpdataTagService.class);
						intent.setAction("updateData");
						intent.setPackage(getPackageName());
						startService(intent);
					}
				});
		request.setTag("downtag");
		request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	/**
	 * 查询个人设置
	 */
	private void queryset() {
		if (NetUtil.getConnectState(getApplication()) != NetWorkState.NONE) {
			String querysetPath = URLConstants.查询设置信息 + userId;
			StringRequest request = new StringRequest(Request.Method.GET,
					querysetPath, new Response.Listener<String>() {
						@Override
						public void onResponse(String s) {
							if (!TextUtils.isEmpty(s)) {
								try {
									Gson gson = new Gson();
									SetBackBean backBean = gson.fromJson(s,
											SetBackBean.class);
									if (backBean.status == 0) {
										List<SetBean> list = backBean.list;
										if ("".equals(updatesettime)) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.MUSICDESC,
													list.get(0).ringDesc + "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.MUSICCODE,
													list.get(0).ringCode + "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.BEFORETIME,
													list.get(0).beforeTime + "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.MORNINGSTATE,
													list.get(0).morningState
															+ "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.MORNINGTIME,
													list.get(0).morningTime
															+ "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.NIGHTSTATE,
													list.get(0).nightState + "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.NIGHTTIME,
													list.get(0).nightTime + "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.ALLSTATE,
													list.get(0).dayState + "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.ALLTIME,
													list.get(0).dayTime + "");
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.UPDATESETTIME,
													list.get(0).updateTime
															.replace("T", " "));
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.SETID,
													list.get(0).id + "");
											String today, tomorrow;
											Calendar calendar = Calendar
													.getInstance();
											calendar.setTime(new Date());
											today = DateUtil
													.formatDate(calendar
															.getTime());
											calendar.set(
													Calendar.DAY_OF_MONTH,
													calendar.get(Calendar.DAY_OF_MONTH) + 1);
											tomorrow = DateUtil
													.formatDate(calendar
															.getTime());
											if ("0".equals(sharedPrefUtil
													.getString(
															getApplication(),
															ShareFile.USERFILE,
															ShareFile.MORNINGSTATE,
															"0"))) {
												int count = App
														.getDBcApplication()
														.CheckClockIDData(-1);
												if (count == 0) {
													if (DateUtil
															.parseDateTimeHm(
																	sharedPrefUtil
																			.getString(
																					getApplication(),
																					ShareFile.USERFILE,
																					ShareFile.MORNINGTIME,
																					"07:58"))
															.before(DateUtil
																	.parseDateTimeHm(DateUtil
																			.formatDateTimeHm(new Date())))) {
														App.getDBcApplication()
																.insertEveryClockData(
																		-1,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"早上问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"",
																		"morninghello.wav",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													} else {
														App.getDBcApplication()
																.insertEveryClockData(
																		-1,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"早上问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"",
																		"morninghello.wav",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													}
												} else {
													App.getDBcApplication()
															.deleteEveryClock(
																	-1);
													if (DateUtil
															.parseDateTimeHm(
																	sharedPrefUtil
																			.getString(
																					getApplication(),
																					ShareFile.USERFILE,
																					ShareFile.MORNINGTIME,
																					"07:58"))
															.before(DateUtil
																	.parseDateTimeHm(DateUtil
																			.formatDateTimeHm(new Date())))) {
														App.getDBcApplication()
																.insertEveryClockData(
																		-1,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"早上问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"",
																		"morninghello.wav",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													} else {
														App.getDBcApplication()
																.insertEveryClockData(
																		-1,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"早上问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.MORNINGTIME,
																										"07:58"))),
																		"",
																		"morninghello.wav",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													}
												}
											} else {
												App.getDBcApplication()
														.deleteEveryClock(-1);
											}
											if ("0".equals(sharedPrefUtil
													.getString(
															getApplication(),
															ShareFile.USERFILE,
															ShareFile.NIGHTSTATE,
															"0"))) {

												int count = App
														.getDBcApplication()
														.CheckClockIDData(-2);
												if (count == 0) {
													if (DateUtil
															.parseDateTimeHm(
																	sharedPrefUtil
																			.getString(
																					getApplication(),
																					ShareFile.USERFILE,
																					ShareFile.NIGHTTIME,
																					"20:58"))
															.before(DateUtil
																	.parseDateTimeHm(DateUtil
																			.formatDateTimeHm(new Date())))) {
														App.getDBcApplication()
																.insertEveryClockData(
																		-2,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"下午问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"",
																		"nighthello.ogg",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													} else {
														App.getDBcApplication()
																.insertEveryClockData(
																		-2,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"下午问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"",
																		"nighthello.ogg",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													}
												} else {
													App.getDBcApplication()
															.deleteEveryClock(
																	-2);
													if (DateUtil
															.parseDateTimeHm(
																	sharedPrefUtil
																			.getString(
																					getApplication(),
																					ShareFile.USERFILE,
																					ShareFile.NIGHTTIME,
																					"20:58"))
															.before(DateUtil
																	.parseDateTimeHm(DateUtil
																			.formatDateTimeHm(new Date())))) {
														App.getDBcApplication()
																.insertEveryClockData(
																		-2,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"下午问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(tomorrow
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"",
																		"nighthello.ogg",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													} else {
														App.getDBcApplication()
																.insertEveryClockData(
																		-2,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"下午问候",
																		0,
																		DateUtil.formatDateTimeSs(DateUtil
																				.parseDateTime(today
																						+ " "
																						+ sharedPrefUtil
																								.getString(
																										getApplication(),
																										ShareFile.USERFILE,
																										ShareFile.NIGHTTIME,
																										"20:58"))),
																		"",
																		"nighthello.ogg",
																		1, 1,
																		7, 0,
																		0, 1,
																		0, "",
																		0, 0,
																		"", "");
													}
												}
											} else {
												App.getDBcApplication()
														.deleteEveryClock(-2);
											}
										} else {
											if (DateUtil
													.parseDateTimeSs(
															list.get(0).updateTime
																	.replace(
																			"T",
																			" "))
													.after(DateUtil
															.parseDateTimeSs(updatesettime))) {
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.MUSICDESC,
														list.get(0).ringDesc
																+ "");
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.MUSICCODE,
														list.get(0).ringCode
																+ "");
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.BEFORETIME,
														list.get(0).beforeTime
																+ "");
												sharedPrefUtil
														.putString(
																getApplication(),
																ShareFile.USERFILE,
																ShareFile.MORNINGSTATE,
																list.get(0).morningState
																		+ "");
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.MORNINGTIME,
														list.get(0).morningTime
																+ "");
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.NIGHTSTATE,
														list.get(0).nightState
																+ "");
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.NIGHTTIME,
														list.get(0).nightTime
																+ "");
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.ALLSTATE,
														list.get(0).dayState
																+ "");
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.ALLTIME,
														list.get(0).dayTime
																+ "");
												sharedPrefUtil
														.putString(
																getApplication(),
																ShareFile.USERFILE,
																ShareFile.UPDATESETTIME,
																list.get(0).updateTime
																		.replace(
																				"T",
																				" "));
												sharedPrefUtil.putString(
														getApplication(),
														ShareFile.USERFILE,
														ShareFile.SETID,
														list.get(0).id + "");
												String today, tomorrow;
												Calendar calendar = Calendar
														.getInstance();
												calendar.setTime(new Date());
												today = DateUtil
														.formatDate(calendar
																.getTime());
												calendar.set(
														Calendar.DAY_OF_MONTH,
														calendar.get(Calendar.DAY_OF_MONTH) + 1);
												tomorrow = DateUtil
														.formatDate(calendar
																.getTime());
												if ("0".equals(sharedPrefUtil
														.getString(
																getApplication(),
																ShareFile.USERFILE,
																ShareFile.MORNINGSTATE,
																"0"))) {
													int count = App
															.getDBcApplication()
															.CheckClockIDData(
																	-1);
													if (count == 0) {
														if (DateUtil
																.parseDateTimeHm(
																		sharedPrefUtil
																				.getString(
																						getApplication(),
																						ShareFile.USERFILE,
																						ShareFile.MORNINGTIME,
																						"07:58"))
																.before(DateUtil
																		.parseDateTimeHm(DateUtil
																				.formatDateTimeHm(new Date())))) {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-1,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"早上问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"",
																			"morninghello.wav",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														} else {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-1,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"早上问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"",
																			"morninghello.wav",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														}
													} else {
														App.getDBcApplication()
																.deleteEveryClock(
																		-1);
														if (DateUtil
																.parseDateTimeHm(
																		sharedPrefUtil
																				.getString(
																						getApplication(),
																						ShareFile.USERFILE,
																						ShareFile.MORNINGTIME,
																						"07:58"))
																.before(DateUtil
																		.parseDateTimeHm(DateUtil
																				.formatDateTimeHm(new Date())))) {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-1,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"早上问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"",
																			"morninghello.wav",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														} else {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-1,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"早上问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.MORNINGTIME,
																											"07:58"))),
																			"",
																			"morninghello.wav",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														}
													}
												} else {
													App.getDBcApplication()
															.deleteEveryClock(
																	-1);
												}
												if ("0".equals(sharedPrefUtil
														.getString(
																getApplication(),
																ShareFile.USERFILE,
																ShareFile.NIGHTSTATE,
																"0"))) {

													int count = App
															.getDBcApplication()
															.CheckClockIDData(
																	-2);
													if (count == 0) {
														if (DateUtil
																.parseDateTimeHm(
																		sharedPrefUtil
																				.getString(
																						getApplication(),
																						ShareFile.USERFILE,
																						ShareFile.NIGHTTIME,
																						"20:58"))
																.before(DateUtil
																		.parseDateTimeHm(DateUtil
																				.formatDateTimeHm(new Date())))) {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-2,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"下午问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"",
																			"nighthello.ogg",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														} else {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-2,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"下午问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"",
																			"nighthello.ogg",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														}
													} else {
														App.getDBcApplication()
																.deleteEveryClock(
																		-2);
														if (DateUtil
																.parseDateTimeHm(
																		sharedPrefUtil
																				.getString(
																						getApplication(),
																						ShareFile.USERFILE,
																						ShareFile.NIGHTTIME,
																						"20:58"))
																.before(DateUtil
																		.parseDateTimeHm(DateUtil
																				.formatDateTimeHm(new Date())))) {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-2,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"下午问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(tomorrow
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"",
																			"nighthello.ogg",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														} else {
															App.getDBcApplication()
																	.insertEveryClockData(
																			-2,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"下午问候",
																			0,
																			DateUtil.formatDateTimeSs(DateUtil
																					.parseDateTime(today
																							+ " "
																							+ sharedPrefUtil
																									.getString(
																											getApplication(),
																											ShareFile.USERFILE,
																											ShareFile.NIGHTTIME,
																											"20:58"))),
																			"",
																			"nighthello.ogg",
																			1,
																			1,
																			7,
																			0,
																			0,
																			1,
																			0,
																			"",
																			0,
																			0,
																			"",
																			"");
														}
													}
												} else {
													App.getDBcApplication()
															.deleteEveryClock(
																	-2);
												}
											} else {

											}
										}
									} else if (backBean.status == 1) {
										String today, tomorrow;
										Calendar calendar = Calendar
												.getInstance();
										calendar.setTime(new Date());
										today = DateUtil.formatDate(calendar
												.getTime());
										calendar.set(
												Calendar.DAY_OF_MONTH,
												calendar.get(Calendar.DAY_OF_MONTH) + 1);
										tomorrow = DateUtil.formatDate(calendar
												.getTime());
										if ("0".equals(sharedPrefUtil
												.getString(getApplication(),
														ShareFile.USERFILE,
														ShareFile.MORNINGSTATE,
														"0"))) {
											int count = App.getDBcApplication()
													.CheckClockIDData(-1);
											if (count == 0) {
												if (DateUtil
														.parseDateTimeHm(
																sharedPrefUtil
																		.getString(
																				getApplication(),
																				ShareFile.USERFILE,
																				ShareFile.MORNINGTIME,
																				"07:58"))
														.before(DateUtil
																.parseDateTimeHm(DateUtil
																		.formatDateTimeHm(new Date())))) {
													App.getDBcApplication()
															.insertEveryClockData(
																	-1,
																	tomorrow
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"早上问候",
																	0,
																	DateUtil.formatDateTime(new Date())
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"",
																	"morninghello.wav",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												} else {
													App.getDBcApplication()
															.insertEveryClockData(
																	-1,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"早上问候",
																	0,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"",
																	"morninghello.wav",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												}

											} else {
												App.getDBcApplication()
														.deleteEveryClock(-1);
												if (DateUtil
														.parseDateTimeHm(
																sharedPrefUtil
																		.getString(
																				getApplication(),
																				ShareFile.USERFILE,
																				ShareFile.MORNINGTIME,
																				"07:58"))
														.before(DateUtil
																.parseDateTimeHm(DateUtil
																		.formatDateTimeHm(new Date())))) {
													App.getDBcApplication()
															.insertEveryClockData(
																	-1,
																	tomorrow
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"早上问候",
																	0,
																	DateUtil.formatDateTime(new Date())
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"",
																	"morninghello.wav",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												} else {
													App.getDBcApplication()
															.insertEveryClockData(
																	-1,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"早上问候",
																	0,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.MORNINGTIME,
																							"07:58"),
																	"",
																	"morninghello.wav",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												}
											}
										} else {
											App.getDBcApplication()
													.deleteEveryClock(-1);
										}
										if ("0".equals(sharedPrefUtil
												.getString(getApplication(),
														ShareFile.USERFILE,
														ShareFile.NIGHTSTATE,
														"0"))) {

											int count = App.getDBcApplication()
													.CheckClockIDData(-2);
											if (count == 0) {
												if (DateUtil
														.parseDateTimeHm(
																sharedPrefUtil
																		.getString(
																				getApplication(),
																				ShareFile.USERFILE,
																				ShareFile.NIGHTTIME,
																				"20:58"))
														.before(DateUtil
																.parseDateTimeHm(DateUtil
																		.formatDateTimeHm(new Date())))) {
													App.getDBcApplication()
															.insertEveryClockData(
																	-2,
																	tomorrow
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"下午问候",
																	0,
																	tomorrow
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"",
																	"nighthello.ogg",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												} else {
													App.getDBcApplication()
															.insertEveryClockData(
																	-2,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"下午问候",
																	0,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"",
																	"nighthello.ogg",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												}
											} else {
												App.getDBcApplication()
														.deleteEveryClock(-2);
												if (DateUtil
														.parseDateTimeHm(
																sharedPrefUtil
																		.getString(
																				getApplication(),
																				ShareFile.USERFILE,
																				ShareFile.NIGHTTIME,
																				"20:58"))
														.before(DateUtil
																.parseDateTimeHm(DateUtil
																		.formatDateTimeHm(new Date())))) {
													App.getDBcApplication()
															.insertEveryClockData(
																	-2,
																	tomorrow
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"下午问候",
																	0,
																	tomorrow
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"",
																	"nighthello.ogg",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												} else {
													App.getDBcApplication()
															.insertEveryClockData(
																	-2,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"下午问候",
																	0,
																	today
																			+ " "
																			+ sharedPrefUtil
																					.getString(
																							getApplication(),
																							ShareFile.USERFILE,
																							ShareFile.NIGHTTIME,
																							"20:58"),
																	"",
																	"nighthello.ogg",
																	1, 1, 7, 0,
																	0, 1, 0,
																	"", 0, 0,
																	"", "");
												}
											}
										} else {
											App.getDBcApplication()
													.deleteEveryClock(-2);
										}
										String addPath = URLConstants.添加用户设置;
										AddSetAsync(addPath);
									}
								} catch (JsonSyntaxException e) {
									e.printStackTrace();
								}
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError volleyError) {
						}
					});
			request.setTag("down");
			request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
			App.getHttpQueues().add(request);
			// new QuerySetAsync().execute(querysetPath);
		} else {
			SetEveryDayTime();
		}
	}

	/**
	 * 添加个人设置
	 */
	private void AddSetAsync(String path) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								SuccessOrFailBean bean = gson.fromJson(result,
										SuccessOrFailBean.class);
								if (bean.status == 0) {
									SetEveryDayTime();
								} else {

								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}

						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("tbUserMannge.uid", userId);
				pairs.put("tbUserMannge.openState", 0 + "");
				pairs.put("tbUserMannge.ringCode", "g_88");
				pairs.put("tbUserMannge.ringDesc", "完成任务");
				pairs.put("tbUserMannge.beforeTime", 0 + "");
				pairs.put("tbUserMannge.morningState", 0 + "");
				pairs.put("tbUserMannge.morningTime", "07:58");
				pairs.put("tbUserMannge.nightState", 0 + "");
				pairs.put("tbUserMannge.nightTime", "20:58");
				pairs.put("tbUserMannge.dayTime", "12:00");
				pairs.put("tbUserMannge.dayState", 0 + "");
				return pairs;
			}
		};
		request.setTag("addset");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	/**
	 * 每天闹钟写入
	 */
	private void SetEveryDayTime() {
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());
		if ("0".equals(sharedPrefUtil.getString(getApplication(),
				ShareFile.USERFILE, ShareFile.MORNINGSTATE, "0"))) {
			int count = App.getDBcApplication().CheckClockIDData(-1);
			if (count == 0) {
				if (DateUtil.parseDateTimeHm(
						sharedPrefUtil.getString(getApplication(),
								ShareFile.USERFILE, ShareFile.MORNINGTIME,
								"07:58")).before(
						DateUtil.parseDateTimeHm(DateUtil
								.formatDateTimeHm(new Date())))) {
					App.getDBcApplication().insertEveryClockData(
							-1,
							tomorrow
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"早上问候",
							0,
							DateUtil.formatDateTime(new Date())
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"", "morninghello.wav", 1, 1, 7, 0, 0, 1, 0, "", 0,
							0, "", "");
				} else {
					App.getDBcApplication().insertEveryClockData(
							-1,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"早上问候",
							0,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"", "morninghello.wav", 1, 1, 7, 0, 0, 1, 0, "", 0,
							0, "", "");
				}

			} else {
				App.getDBcApplication().deleteEveryClock(-1);
				if (DateUtil.parseDateTimeHm(
						sharedPrefUtil.getString(getApplication(),
								ShareFile.USERFILE, ShareFile.MORNINGTIME,
								"07:58")).before(
						DateUtil.parseDateTimeHm(DateUtil
								.formatDateTimeHm(new Date())))) {
					App.getDBcApplication().insertEveryClockData(
							-1,
							tomorrow
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"早上问候",
							0,
							DateUtil.formatDateTime(new Date())
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"", "morninghello.wav", 1, 1, 7, 0, 0, 1, 0, "", 0,
							0, "", "");
				} else {
					App.getDBcApplication().insertEveryClockData(
							-1,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"早上问候",
							0,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.MORNINGTIME, "07:58"),
							"", "morninghello.wav", 1, 1, 7, 0, 0, 1, 0, "", 0,
							0, "", "");
				}
			}
		} else {
			App.getDBcApplication().deleteEveryClock(-1);
		}
		if ("0".equals(sharedPrefUtil.getString(getApplication(),
				ShareFile.USERFILE, ShareFile.NIGHTSTATE, "0"))) {

			int count = App.getDBcApplication().CheckClockIDData(-2);
			if (count == 0) {
				if (DateUtil.parseDateTimeHm(
						sharedPrefUtil.getString(getApplication(),
								ShareFile.USERFILE, ShareFile.NIGHTTIME,
								"20:58")).before(
						DateUtil.parseDateTimeHm(DateUtil
								.formatDateTimeHm(new Date())))) {
					App.getDBcApplication().insertEveryClockData(
							-2,
							tomorrow
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "20:58"),
							"下午问候",
							0,
							tomorrow
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "20:58"), "",
							"nighthello.ogg", 1, 1, 7, 0, 0, 1, 0, "", 0, 0,
							"", "");
				} else {
					App.getDBcApplication().insertEveryClockData(
							-2,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "20:58"),
							"下午问候",
							0,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "20:58"), "",
							"nighthello.ogg", 1, 1, 7, 0, 0, 1, 0, "", 0, 0,
							"", "");
				}
			} else {
				App.getDBcApplication().deleteEveryClock(-2);
				if (DateUtil.parseDateTimeHm(
						sharedPrefUtil.getString(getApplication(),
								ShareFile.USERFILE, ShareFile.NIGHTTIME,
								"20:58")).before(
						DateUtil.parseDateTimeHm(DateUtil
								.formatDateTimeHm(new Date())))) {
					App.getDBcApplication().insertEveryClockData(
							-2,
							tomorrow
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "20:58"),
							"下午问候",
							0,
							tomorrow
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "20:58"), "",
							"nighthello.ogg", 1, 1, 7, 0, 0, 1, 0, "", 0, 0,
							"", "");
				} else {
					App.getDBcApplication().insertEveryClockData(
							-2,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "18:58"),
							"下午问候",
							0,
							today
									+ " "
									+ sharedPrefUtil.getString(
											getApplication(),
											ShareFile.USERFILE,
											ShareFile.NIGHTTIME, "07:58"), "",
							"nighthello.ogg", 1, 1, 7, 0, 0, 1, 0, "", 0, 0,
							"", "");
				}
			}
		} else {
			App.getDBcApplication().deleteEveryClock(-2);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
		App.getHttpQueues().cancelAll("downtag");
		App.getHttpQueues().cancelAll("addset");
		App.getHttpQueues().cancelAll("downsch");
		stopSelf();
	}

	/**
	 * 设置每5分钟响一次铃声
	 * 
	 * @throws
	 */
	private void setFiveMinuteRepeat() {
		int count = App.getDBcApplication().CheckClockIDData(-10);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		if (count == 0) {
			App.getDBcApplication().insertEveryClockData(
					-10,
					DateUtil.formatDateTimeSs(DateUtil.parseDateTime(DateUtil
							.formatDateTime(calendar.getTime()))), "睡觉", 0,
					DateUtil.formatDateTimeSs(calendar.getTime()), "", "g_203",
					1, 1, 10, 0, 0, 1, 0, "", 0, 0, "", "");
		} else {
			App.getDBcApplication().deleteEveryClock(-10);
			App.getDBcApplication().insertEveryClockData(
					-10,
					DateUtil.formatDateTimeSs(DateUtil.parseDateTime(DateUtil
							.formatDateTime(calendar.getTime()))), "睡觉", 0,
					DateUtil.formatDateTimeSs(calendar.getTime()), "", "g_203",
					1, 1, 10, 0, 0, 1, 0, "", 0, 0, "", "");
		}
	}

	private void DownShcData() {
		String datetime = "";
		final String jsonArrayStr = sharedPrefUtil.getString(getApplication(),
				ShareFile.USERFILE, ShareFile.SHOUCANGDATA, "");
		datetime = sharedPrefUtil.getString(getApplication(),
				ShareFile.USERFILE, ShareFile.FIRSTDOWNFOCUSSCH,
				"2016-01-01 00:00:00");
		if ("".equals(datetime)) {
			datetime = "2016-01-01 00:00:00";
		}
		datetime = datetime.replace(" ", "%2B");
		String downschpath = URLConstants.新版发现收藏下行数据到日程 + "?uid=" + userId
				+ "&dateTime=" + datetime + "&type=1";
		StringRequest stringRequest = new StringRequest(Method.GET,
				downschpath, new Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							List<NewMyFoundShouChangDingYueListBeen> addList = new ArrayList<NewMyFoundShouChangDingYueListBeen>();
							try {
								Gson gson = new Gson();
								NewMyFoundShouChangDingYueBeen backbean = gson
										.fromJson(
												result,
												NewMyFoundShouChangDingYueBeen.class);
								List<NewFocusDeleteRepDataBean> deleterepList = null;
								List<NewFocusDeleteSchDataBean> deleteschList = null;
								if (backbean.status == 0) {
									addList.clear();
									addList = backbean.list;
									if (addList != null && addList.size() > 0) {
										for (NewMyFoundShouChangDingYueListBeen been : addList) {
											if ("".equals(jsonArrayStr)) {
												focusname = "发现频道";
											} else {
												try {
													JSONArray jsonArray = new JSONArray(
															jsonArrayStr);
													for (int i = 0; i < jsonArray
															.length(); i++) {
														JSONObject jsonObject = jsonArray
																.getJSONObject(i);
														int id = jsonObject
																.getInt("id");
														if (been.CUid == id) {
															focusname = jsonObject
																	.getString("name");
															break;
														} else {
															focusname = "发现频道";
														}
													}
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
											String repeatDate = "";
											if ("".equals(StringUtils
													.getIsStringEqulesNull(been.CRepeatDate))) {
												repeatDate = "";
											} else {
												repeatDate = been.CRepeatDate
														.replace("T", " ");
											}
											String alarmdesc = "";
											String alarmcode = "";
											if ("".equals(StringUtils
													.getIsStringEqulesNull(been.CAlarmsoundDesc))) {
												alarmdesc = "完成任务";
											} else {
												alarmdesc = been.CAlarmsoundDesc;
											}
											if ("".equals(StringUtils
													.getIsStringEqulesNull(been.CAlarmsound))) {
												alarmcode = "g_88";
											} else {
												alarmcode = been.CAlarmsound;
											}
											if (been.CRepeatId == 0) {
												int schcount = app
														.CheckCountSchFromFocusData(been.CId);
												if (schcount == 0) {
													app.insertScheduleData(
															StringUtils
																	.getIsStringEqulesNull(been.CContent),
															DateUtil.formatDate(DateUtil
																	.parseDate(been.CDate)),
															DateUtil.formatDateTimeHm(DateUtil
																	.parseDateTimeHm(been.CTime)),
															been.CIsAlarm,
															been.CBefortime,
															been.CDisplayAlarm,
															been.CPostpone,
															been.CImportant,
															been.CColorType,
															been.CIsEnd,
															been.CCreateTime
																	.replace(
																			"T",
																			" "),
															"",
															been.CType,
															StringUtils
																	.getIsStringEqulesNull(been.CTypeDesc),
															StringUtils
																	.getIsStringEqulesNull(been.CTypeSpare),
															been.CRepeatId,
															repeatDate,
															been.CUpdateTime
																	.replace(
																			"T",
																			" "),
															0,
															0,
															been.CSchRepeatLink,
															alarmdesc,
															alarmcode,
															focusname,
															been.schRead,
															been.CId,
															been.atype,
															StringUtils
																	.getIsStringEqulesNull(been.webUrl),
															StringUtils
																	.getIsStringEqulesNull(been.imgPath),
															0, 0, been.CUid);
												} else {
													List<Map<String, String>> mList;
													mList = app
															.queryAllSchData(
																	20,
																	been.CId, 0);
													int isEnd = 0;
													if (mList != null
															&& mList.size() > 0) {
														isEnd = Integer
																.parseInt(mList
																		.get(0)
																		.get(ScheduleTable.schIsEnd));
													} else {
														isEnd = 0;
													}
													app.updateScheduleNoIDForSchData(
															StringUtils
																	.getIsStringEqulesNull(been.CContent),
															DateUtil.formatDate(DateUtil
																	.parseDate(been.CDate)),
															DateUtil.formatDateTimeHm(DateUtil
																	.parseDateTimeHm(been.CTime)),
															been.CIsAlarm,
															been.CBefortime,
															been.CDisplayAlarm,
															been.CPostpone,
															been.CImportant,
															been.CColorType,
															isEnd,
															been.CCreateTime
																	.replace(
																			"T",
																			" "),
															"",
															been.CType,
															StringUtils
																	.getIsStringEqulesNull(been.CTypeDesc),
															StringUtils
																	.getIsStringEqulesNull(been.CTypeSpare),
															been.CRepeatId,
															repeatDate,
															been.CUpdateTime
																	.replace(
																			"T",
																			" "),
															0,
															0,
															been.CSchRepeatLink,
															alarmdesc,
															alarmcode,
															focusname,
															been.schRead,
															been.CId,
															been.atype,
															StringUtils
																	.getIsStringEqulesNull(been.webUrl),
															StringUtils
																	.getIsStringEqulesNull(been.imgPath),
															0, 0, been.CUid);
												}

											} else {
												int repcount = app
														.CheckCountRepFromFocusData(been.CRepeatId);
												if (repcount == 0) {
													app.insertScheduleData(
															StringUtils
																	.getIsStringEqulesNull(been.CContent),
															DateUtil.formatDate(DateUtil
																	.parseDate(been.CDate)),
															DateUtil.formatDateTimeHm(DateUtil
																	.parseDateTimeHm(been.CTime)),
															been.CIsAlarm,
															been.CBefortime,
															been.CDisplayAlarm,
															been.CPostpone,
															been.CImportant,
															been.CColorType,
															been.CIsEnd,
															been.CCreateTime
																	.replace(
																			"T",
																			" "),
															"",
															been.CType,
															StringUtils
																	.getIsStringEqulesNull(been.CTypeDesc),
															StringUtils
																	.getIsStringEqulesNull(been.CTypeSpare),
															been.CRepeatId,
															repeatDate,
															been.CUpdateTime
																	.replace(
																			"T",
																			" "),
															0,
															0,
															been.CSchRepeatLink,
															alarmdesc,
															alarmcode,
															focusname,
															been.schRead,
															been.CId,
															been.atype,
															StringUtils
																	.getIsStringEqulesNull(been.webUrl),
															StringUtils
																	.getIsStringEqulesNull(been.imgPath),
															0, 0, been.CUid);
												} else {
													app.updateScheduleNoIDForRepData(
															StringUtils
																	.getIsStringEqulesNull(been.CContent),
															DateUtil.formatDate(DateUtil
																	.parseDate(been.CDate)),
															DateUtil.formatDateTimeHm(DateUtil
																	.parseDateTimeHm(been.CTime)),
															been.CIsAlarm,
															been.CBefortime,
															been.CDisplayAlarm,
															been.CPostpone,
															been.CImportant,
															been.CColorType,
															been.CIsEnd,
															been.CCreateTime
																	.replace(
																			"T",
																			" "),
															"",
															been.CType,
															StringUtils
																	.getIsStringEqulesNull(been.CTypeDesc),
															StringUtils
																	.getIsStringEqulesNull(been.CTypeSpare),
															been.CRepeatId,
															repeatDate,
															been.CUpdateTime
																	.replace(
																			"T",
																			" "),
															0,
															0,
															been.CSchRepeatLink,
															alarmdesc,
															alarmcode,
															focusname,
															been.schRead,
															been.CId,
															been.atype,
															StringUtils
																	.getIsStringEqulesNull(been.webUrl),
															StringUtils
																	.getIsStringEqulesNull(been.imgPath),
															0, 0, been.CUid);
												}
											}

										}

									}

									deleteschList = backbean.delList;
									deleterepList = backbean.tDelList;
									if (deleteschList != null
											&& deleteschList.size() > 0) {
										for (NewFocusDeleteSchDataBean schDataBean : deleteschList) {
											if (schDataBean.state == 1) {
												app.deleteRepFocusParentData(
														schDataBean.dataId,
														schDataBean.uid);
											} else {
												app.deleteSchFocusData(
														schDataBean.dataId,
														schDataBean.uid);
											}
										}
									}
									if (deleterepList != null
											&& deleterepList.size() > 0) {
										for (NewFocusDeleteRepDataBean deleteRepDataBean : deleterepList) {
											String repdate = StringUtils
													.getIsStringEqulesNull(deleteRepDataBean.repdatetwo);
											if (!"".equals(repdate)) {
												repdate = repdate.replace("T",
														" ");
												app.deleteRepFocusData(
														deleteRepDataBean.repId,
														repdate);
											}
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
					}
				});
		stringRequest.setTag("downsch");
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(stringRequest);
	}
}
