package com.mission.schedule.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.LunarCalendaTimeBackBean;
import com.mission.schedule.bean.LunarCalendaTimeBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.URLConstants;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

public class DownNongLiService extends Service{
	
	List<Map<String, String>> nongliList = new ArrayList<Map<String, String>>();
	@Override
	public void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				downNongLiData();
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}
	public void downNongLiData() {
		System.out.println("DownNongLiService的线程threadID:"+Thread.currentThread().getId());
		nongliList = App.getDBcApplication().queryMaxDate();
		if (nongliList != null && nongliList.size() > 0) {
			String path = URLConstants.更新农历 + nongliList.get(0).get("calendar");
			StringRequest request = new StringRequest(Method.GET, path, new Listener<String>() {

				@Override
				public void onResponse(String s) {
					if (!TextUtils.isEmpty(s)) {
						try {
							Gson gson = new Gson();
							SuccessOrFailBean backBean = gson.fromJson(s,
									SuccessOrFailBean.class);
							if (backBean.status == 1) {
								App.getDBcApplication().deletenongliData();
								String nonglipath = URLConstants.下载农历;
//								new DownNongLiAsync().execute(nonglipath);
								StringRequest stringRequest = new StringRequest(Method.GET, nonglipath, new Listener<String>() {

									@Override
									public void onResponse(String s) {
										if (!TextUtils.isEmpty(s)) {
											try {
												final List<LunarCalendaTimeBean> list;
												Gson gson = new Gson();
												LunarCalendaTimeBackBean backBean = gson.fromJson(s,
														LunarCalendaTimeBackBean.class);
												if (backBean.status == 0) {
													list = backBean.list;
													new Thread(new Runnable() {
														
														@Override
														public void run() {
															for (int i = 0; i < list.size(); i++) {
																App.getDBcApplication().insertNongLiData(list.get(i).calendar,
																		list.get(i).solarTerms, list.get(i).week,
																		list.get(i).lunarCalendar,
																		list.get(i).holiday,
																		list.get(i).lunarHoliday,
																		list.get(i).createTime,
																		list.get(i).isNotHoliday);
															}
														}
													}).start();
												}
											} catch (JsonSyntaxException e) {
												e.printStackTrace();
											}
											stopSelf();
										}
									}
								}, new Response.ErrorListener() {

									@Override
									public void onErrorResponse(VolleyError volleyError) {
										stopSelf();
									}
								});
								stringRequest.setTag("down");
								stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
								App.getHttpQueues().add(stringRequest);
							}
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					stopSelf();
				}
			});
			request.setTag("down");
			request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
			App.getHttpQueues().add(request);
//			new UpdateNongLiAsync().execute(path);
		} else {
			String nonglipath = URLConstants.下载农历;
			StringRequest stringRequest = new StringRequest(Method.GET, nonglipath, new Listener<String>() {

				@Override
				public void onResponse(String s) {
					if (!TextUtils.isEmpty(s)) {
						try {
							final List<LunarCalendaTimeBean> list;
							Gson gson = new Gson();
							LunarCalendaTimeBackBean backBean = gson.fromJson(s,
									LunarCalendaTimeBackBean.class);
							if (backBean.status == 0) {
								list = backBean.list;
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										for (int i = 0; i < list.size(); i++) {
											App.getDBcApplication().insertNongLiData(list.get(i).calendar,
													list.get(i).solarTerms, list.get(i).week,
													list.get(i).lunarCalendar,
													list.get(i).holiday,
													list.get(i).lunarHoliday,
													list.get(i).createTime,
													list.get(i).isNotHoliday);
										}
										stopSelf();
									}
								}).start();
								
							}
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
						}
					}
					stopSelf();
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError volleyError) {
					stopSelf();
				}
			});
			stringRequest.setTag("down");
			stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
			App.getHttpQueues().add(stringRequest);
//			new DownNongLiAsync().execute(nonglipath);
		}
	}
}
