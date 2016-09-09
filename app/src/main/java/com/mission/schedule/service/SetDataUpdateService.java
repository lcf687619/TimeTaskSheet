package com.mission.schedule.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatUpAndDownBean;
import com.mission.schedule.bean.RepeatUpLoadBackBean;
import com.mission.schedule.bean.RepeatUpLoadBean;
import com.mission.schedule.bean.ScheduBean;
import com.mission.schedule.bean.UpLoadBackBean;
import com.mission.schedule.bean.UpLoadBean;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.XmlUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetDataUpdateService extends Service {

	public static final String UPDATADATA = "updateData";

	String schjson = "";
	App application = App.getDBcApplication();
	String downPath;
	SharedPrefUtil sharedPrefUtil = null;
	String befortime;
	String UserId = "";
	String schyear;
	String schtime;
	String repyear;
	String reptime;
	List<Map<String, String>> upList;
	List<Map<String, String>> upRepeatList;
	private MyBinder mBinder = new MyBinder();
	CalendarChangeValue changeValue = new CalendarChangeValue();
	List<Map<String, String>> soundlist = new ArrayList<Map<String, String>>();
	String schuppath = "";
	String downreppath = "";
	String downschpath = "";
	String repeatUpPath = "";
	String repJson = "";

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		sharedPrefUtil = new SharedPrefUtil(getApplication(),
				ShareFile.USERFILE);
		soundlist.clear();
		soundlist = XmlUtil.readBeforeBellXML(this);
		UserId = sharedPrefUtil.getString(getApplicationContext(),
				ShareFile.USERFILE, ShareFile.USERID, "");
		if ("".equals(UserId)) {

		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						upRepeatList = application.QueryAllChongFuData(2);
						JSONArray jsonarray1 = new JSONArray();
						JSONObject jsonobject2 = new JSONObject();
						if (upRepeatList != null && upRepeatList.size() > 0) {
							repJson = "";
							for (int i = 0; i < upRepeatList.size(); i++) {
								if ((Integer.parseInt(upRepeatList.get(i).get(
										CLRepeatTable.repID)) < 0 && Integer
										.parseInt(upRepeatList.get(i).get(
												CLRepeatTable.repUpdateState)) == 1)
										|| (Integer.parseInt(upRepeatList
												.get(i)
												.get(CLRepeatTable.repID)) > 0 && Integer
												.parseInt(upRepeatList
														.get(i)
														.get(CLRepeatTable.repUpdateState)) != 1)) {
									JSONObject jsonobject3 = new JSONObject();
									if (Integer.parseInt(upRepeatList.get(i)
											.get(CLRepeatTable.repID)) < 0) {
										jsonobject3.put("tempId", upRepeatList
												.get(i)
												.get(CLRepeatTable.repID));
									} else {
										jsonobject3.put("repId", upRepeatList
												.get(i)
												.get(CLRepeatTable.repID));
									}
									jsonobject3.put("repUid", UserId);
									jsonobject3
											.put("repBeforeTime",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repBeforeTime));
									jsonobject3
											.put("repColorType",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repColorType));
									jsonobject3
											.put("repDisplayTime",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repDisplayTime));
									jsonobject3.put("repType", upRepeatList
											.get(i).get(CLRepeatTable.repType));
									jsonobject3.put(
											"repIsAlarm",
											upRepeatList.get(i).get(
													CLRepeatTable.repIsAlarm));
									jsonobject3.put(
											"repIsPuase",
											upRepeatList.get(i).get(
													CLRepeatTable.repIsPuase));
									jsonobject3
											.put("repIsImportant",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repIsImportant));
									jsonobject3
											.put("repSourceType",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repSourceType));
									jsonobject3
											.put("repOpenState",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repOpenState));
									jsonobject3.put(
											"repstateone",
											upRepeatList.get(i).get(
													CLRepeatTable.repStateOne));
									jsonobject3.put(
											"repstatetwo",
											upRepeatList.get(i).get(
													CLRepeatTable.repStateTwo));
									jsonobject3
											.put("recommendedUserId",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repcommendedUserId));
									jsonobject3
											.put("repTypeParameter",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repTypeParameter));
									jsonobject3
											.put("repStartDate",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repStartDate));
									jsonobject3
											.put("repNextCreatedTime",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repNextCreatedTime));
									jsonobject3
											.put("repLastCreatedTime",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repLastCreatedTime));
									jsonobject3
											.put("repInitialCreatedTime",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repInitialCreatedTime));
									jsonobject3.put(
											"repContent",
											upRepeatList.get(i).get(
													CLRepeatTable.repContent));
									jsonobject3
											.put("repCreateTime",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repCreateTime));
									jsonobject3
											.put("repChangeState",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repUpdateState));
									jsonobject3
											.put("repSourceDesc",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repSourceDesc));
									jsonobject3
											.put("repSourceDescSpare",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repSourceDescSpare));
									jsonobject3.put("repTime", upRepeatList
											.get(i).get(CLRepeatTable.repTime));
									jsonobject3.put(
											"repRingDesc",
											upRepeatList.get(i).get(
													CLRepeatTable.repRingDesc));
									jsonobject3.put(
											"repRingCode",
											upRepeatList.get(i).get(
													CLRepeatTable.repRingCode));
									jsonobject3
											.put("repUpdateTime",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repUpdateTime));
									jsonobject3
											.put("recommendedUserName",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repcommendedUserName));
									jsonobject3.put(
											"repdateone",
											upRepeatList.get(i).get(
													CLRepeatTable.repDateOne));
									jsonobject3.put(
											"repdatetwo",
											upRepeatList.get(i).get(
													CLRepeatTable.repDateTwo));
									jsonobject3
											.put("aType",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repAType));
									jsonobject3.put(
											"webUrl",
											upRepeatList.get(i).get(
													CLRepeatTable.repWebURL));
									jsonobject3
											.put("imgPath",
													upRepeatList
															.get(i)
															.get(CLRepeatTable.repImagePath));
									jsonobject3.put(
											"repInSTable",
											upRepeatList.get(i).get(
													CLRepeatTable.repInSTable));
									jsonobject3.put(
											"repEndState",
											upRepeatList.get(i).get(
													CLRepeatTable.repEndState));
									jsonobject3.put("parReamrk", upRepeatList
											.get(i)
											.get(CLRepeatTable.parReamrk));
									jsonobject3.put("repRead", upRepeatList
											.get(i).get(CLRepeatTable.repRead));
									jsonobject3.put("recommendedUserId", upRepeatList
											.get(i)
											.get(CLRepeatTable.repcommendedUserId));
									jsonarray1.put(jsonobject3);
								}
							}
						}
						jsonobject2.put("data", jsonarray1);
						repJson = jsonobject2.toString();
						upList = application.queryAllSchData(-1,0,0);
						JSONArray jsonarray = new JSONArray();
						JSONObject jsonobject = new JSONObject();
						if (upList != null && upList.size() > 0) {
							for (int i = 0; i < upList.size(); i++) {
								if ((Integer.parseInt(upList.get(i).get(
										ScheduleTable.schID)) < 0 && Integer
										.parseInt(upList.get(i).get(
												ScheduleTable.schUpdateState)) == 1)
										|| (Integer.parseInt(upList.get(i).get(
												ScheduleTable.schID)) > 0 && Integer
												.parseInt(upList
														.get(i)
														.get(ScheduleTable.schUpdateState)) != 1)) {
									JSONObject jsonobject1 = new JSONObject();
									if (Integer.parseInt(upList.get(i).get(
											ScheduleTable.schID)) < 0) {
										jsonobject1.put("tempId", upList.get(i)
												.get(ScheduleTable.schID));
									} else {
										jsonobject1.put("cId", upList.get(i)
												.get(ScheduleTable.schID));
									}
									jsonobject1.put("cRecommendId", upList.get(i)
											.get(ScheduleTable.schcRecommendId));
									jsonobject1.put("cUid", UserId);
									jsonobject1.put("cContent", upList.get(i)
											.get(ScheduleTable.schContent));
									jsonobject1.put(
											"cDate",
											upList.get(i).get(
													ScheduleTable.schDate));
									jsonobject1.put(
											"cTime",
											upList.get(i).get(
													ScheduleTable.schTime));
									jsonobject1.put("cIsAlarm", upList.get(i)
											.get(ScheduleTable.schIsAlarm));
									jsonobject1.put("cBeforTime", upList.get(i)
											.get(ScheduleTable.schBeforeTime));
									jsonobject1
											.put("cDisplayAlarm",
													upList.get(i)
															.get(ScheduleTable.schDisplayTime));
									jsonobject1.put("cPostpone", upList.get(i)
											.get(ScheduleTable.schIsPostpone));
									jsonobject1.put("cImportant", upList.get(i)
											.get(ScheduleTable.schIsImportant));
									jsonobject1.put("cColorType", upList.get(i)
											.get(ScheduleTable.schColorType));
									jsonobject1.put("cIsEnd", upList.get(i)
											.get(ScheduleTable.schIsEnd));
									jsonobject1
											.put("cCreateTime",
													upList.get(i)
															.get(ScheduleTable.schCreateTime));
									jsonobject1.put(
											"cTags",
											upList.get(i).get(
													ScheduleTable.schTags));
									jsonobject1
											.put("cType",
													upList.get(i)
															.get(ScheduleTable.schSourceType));
									jsonobject1.put("cTypeDesc", upList.get(i)
											.get(ScheduleTable.schSourceDesc));
									jsonobject1
											.put("cTypeSpare",
													upList.get(i)
															.get(ScheduleTable.schSourceDescSpare));
									jsonobject1.put("cRepeatId", upList.get(i)
											.get(ScheduleTable.schRepeatID));
									jsonobject1
											.put("cRepeatDate",
													upList.get(i)
															.get(ScheduleTable.schRepeatDate));
									jsonobject1
											.put("cUpdateTime",
													upList.get(i)
															.get(ScheduleTable.schUpdateTime));
									jsonobject1.put("cOpenState", upList.get(i)
											.get(ScheduleTable.schOpenState));
									jsonobject1
											.put("cRecommendName",
													upList.get(i)
															.get(ScheduleTable.schcRecommendName));
									jsonobject1
											.put("updateState",
													upList.get(i)
															.get(ScheduleTable.schUpdateState));
									jsonobject1.put(
											"cAlarmSoundDesc",
											upList.get(i).get(
													ScheduleTable.schRingDesc));
									jsonobject1.put(
											"cAlarmSound",
											upList.get(i).get(
													ScheduleTable.schRingCode));
									jsonobject1.put("schRead", upList.get(i)
											.get(ScheduleTable.schRead));
									jsonobject1.put("attentionid", upList
											.get(i).get(ScheduleTable.schAID));
									jsonobject1.put(
											"aType",
											upList.get(i).get(
													ScheduleTable.schAType));
									jsonobject1.put("webUrl", upList.get(i)
											.get(ScheduleTable.schWebURL));
									jsonobject1.put("imgPath", upList.get(i)
											.get(ScheduleTable.schImagePath));
									jsonobject1
											.put("cSchRepeatLink",
													upList.get(i)
															.get(ScheduleTable.schRepeatLink));
									jsonarray.put(jsonobject1);
								}
							}
						}
						jsonobject.put("data", jsonarray);
						schjson = jsonobject.toString();
						repeatUpPath = URLConstants.重复数据上传;
						schuppath = URLConstants.同步数据接口;
						if ((upList != null && upList.size() > 0)
								&& (upRepeatList != null && upRepeatList.size() > 0)) {
							NewRepeatAsync(repeatUpPath, repJson);
						} else {
							if (upRepeatList != null && upRepeatList.size() > 0) {
								NewRepeatAsync(repeatUpPath, repJson);
							} else if (upList != null && upList.size() > 0) {
								UpLoadSch(schuppath, schjson);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);
	}

	class MyBinder extends Binder {

		public void startDownload() {
			Log.d("TAG", "startDownload() executed");
			// 执行具体的下载任务
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("upload");
		stopSelf();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				stopSelf();
				break;
			case 1:
				stopSelf();
				break;
			case 2:
				stopSelf();
				break;
			case 3:

				break;
			}
		}
	};

	private void UpLoadSch(String path, final String json) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Listener<String>() {

					@Override
					public void onResponse(final String result) {
						if (!TextUtils.isEmpty(result)) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Gson gson = new Gson();
										List<UpLoadBean> list = null;
										List<Map<String, String>> add = null;
										List<ScheduBean> uppdate = null;
										List<Integer> delete = null;

										UpLoadBackBean backBean = gson
												.fromJson(result,
														UpLoadBackBean.class);
										if (backBean.status == 0) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNSCHTIME,
													backBean.message.replace(
															"T", " "));
											list = backBean.maps;
											for (int i = 0; i < list.size(); i++) {
												add = list.get(i).add;
												uppdate = list.get(i).uppdate;
												delete = list.get(i).delete;
											}
											if (add != null && add.size() > 0) {
												for (int i = 0; i < add.size(); i++) {
													Object[] indexStr = add
															.get(i).keySet()
															.toArray();
													String index = indexStr[0]
															.toString();
													application.UpdateSchID(
															Integer.parseInt(index),
															Integer.parseInt(add
																	.get(i)
																	.get(index)));
													application
															.UpdateClockSchID(
																	index,
																	add.get(i)
																			.get(index));
													application
															.updateUpdateState(Integer
																	.parseInt(add
																			.get(i)
																			.get(index)));
												}
											} else if (delete != null
													&& delete.size() > 0) {
												for (int i = 0; i < delete
														.size(); i++) {
													application
															.deleteScheduleData(delete
																	.get(i));
													application
															.deleteSch(delete
																	.get(i));
												}
											} else if (uppdate != null
													&& uppdate.size() > 0) {
												for (int i = 0; i < uppdate
														.size(); i++) {
													String str = uppdate.get(i).cUpdateTime
															.toString();
													int aid = uppdate.get(i).attentionid;
													if (aid == 0) {
														application
																.updateScheduleData(
																		uppdate.get(i).cId,
																		uppdate.get(i).cContent,
																		uppdate.get(i).cDate,
																		uppdate.get(i).cTime,
																		uppdate.get(i).cIsAlarm,
																		uppdate.get(i).cBeforTime,
																		uppdate.get(i).cDisplayAlarm,
																		uppdate.get(i).cPostpone,
																		uppdate.get(i).cImportant,
																		uppdate.get(i).cColorType,
																		uppdate.get(i).cIsEnd,
																		uppdate.get(i).cTags,
																		uppdate.get(i).cType,
																		uppdate.get(i).cTypeDesc,
																		uppdate.get(i).cTypeSpare,
																		uppdate.get(i).cRepeatId,
																		uppdate.get(i).cRepeatDate,
																		str,
																		0,
																		uppdate.get(i).cOpenState,
																		uppdate.get(i).cSchRepeatLink,
																		uppdate.get(i).cAlarmSoundDesc,
																		uppdate.get(i).cAlarmSound,
																		uppdate.get(i).cRecommendName,
																		uppdate.get(i).schRead,
																		aid,
																		uppdate.get(i).aType,
																		uppdate.get(i).webUrl,
																		uppdate.get(i).imgPath,
																		0,
																		0,
																		uppdate.get(i).cRecommendId);

													} else {
														application
																.updateScheduleData(
																		uppdate.get(i).cId,
																		uppdate.get(i).cContent,
																		uppdate.get(i).cDate,
																		uppdate.get(i).cTime,
																		uppdate.get(i).cIsAlarm,
																		uppdate.get(i).cBeforTime,
																		uppdate.get(i).cDisplayAlarm,
																		uppdate.get(i).cPostpone,
																		uppdate.get(i).cImportant,
																		uppdate.get(i).cColorType,
																		uppdate.get(i).cIsEnd,
																		uppdate.get(i).cTags,
																		uppdate.get(i).cType,
																		uppdate.get(i).cTypeDesc,
																		uppdate.get(i).cTypeSpare,
																		uppdate.get(i).cRepeatId,
																		uppdate.get(i).cRepeatDate,
																		str,
																		0,
																		uppdate.get(i).cOpenState,
																		uppdate.get(i).cSchRepeatLink,
																		uppdate.get(i).cAlarmSoundDesc,
																		uppdate.get(i).cAlarmSound,
																		uppdate.get(i).cRecommendName,
																		uppdate.get(i).schRead,
																		aid,
																		uppdate.get(i).aType,
																		uppdate.get(i).webUrl,
																		uppdate.get(i).imgPath,
																		0,
																		0,
																		uppdate.get(i).cRecommendId);

													}
													application
															.updateUpdateState(uppdate
																	.get(i).cId);

												}
											}
										} else if (backBean.status == 1) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNSCHTIME,
													backBean.message.replace(
															"T", " "));
										}
										Intent intent = new Intent();
										intent.setAction(UPDATADATA);
										intent.putExtra("data", "success");
										sendBroadcast(intent);
									} catch (JsonSyntaxException e) {
										e.printStackTrace();
										Intent intent = new Intent();
										intent.setAction(Const.SHUAXINDATA);
										intent.putExtra("data", "fail");
										sendBroadcast(intent);
									}
								}
							}).start();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("json", json);
				System.out.println("上传日志:" + json);
				return map;
			}
		};
		request.setTag("upload");
		request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void NewRepeatAsync(final String path, final String json) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Listener<String>() {

					@Override
					public void onResponse(final String result) {
						if (!TextUtils.isEmpty(result)) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Gson gson = new Gson();
										List<RepeatUpLoadBean> listRepeat = null;
										List<Map<String, String>> addRepeat = null;
										List<RepeatUpAndDownBean> uppdateRepeat = null;
										List<Integer> deleteRepeat = null;

										RepeatUpLoadBackBean backBean = gson
												.fromJson(
														result,
														RepeatUpLoadBackBean.class);
										if (backBean.status == 0) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNREPTIME,
													backBean.message.replace(
															"T", " "));
											listRepeat = backBean.maps;
											for (int i = 0; i < listRepeat
													.size(); i++) {
												addRepeat = listRepeat.get(i).add;
												uppdateRepeat = listRepeat
														.get(i).update;
												deleteRepeat = listRepeat
														.get(i).delete;
											}
											if (addRepeat != null
													&& addRepeat.size() > 0) {
												for (int i = 0; i < addRepeat
														.size(); i++)

												{
													Object[] indexStr = addRepeat
															.get

															(i).keySet()
															.toArray

															();
													String index = indexStr

													[0].toString();
													application.UpdateRepeatID(
															Integer.parseInt

															(index),
															Integer.parseInt

															(addRepeat.get(

															i).get(index)));
													String str = addRepeat
															.get(i)
															.get(upRepeatList
																	.get

																	(i)
																	.get(

																	CLRepeatTable.repID));
													if ("".equals(str)
															|| "null"
																	.equals(str)
															|| null == str) {

													} else {
														application

																.updateRepUpdateState(Integer

																.parseInt(str));
														application
																.UpdateClockRepID

																(index, str);
														application
																.UpdateSchrepID(
																		Integer.parseInt

																		(index),
																		Integer.parseInt

																		(addRepeat
																				.get(

																				i)
																				.get(index)));
													}
												}
											} else if (deleteRepeat != null
													&& deleteRepeat.size() > 0) {
												for (int i = 0; i < deleteRepeat
														.size(); i

												++) {
													application
															.deleteRepeatData

															(deleteRepeat

															.get(i).toString());
													application.deleteChildSch

													(deleteRepeat.get(i) + "");
													application
															.deleteRep(deleteRepeat
																	.get(i));
												}
											} else if (uppdateRepeat != null
													&& uppdateRepeat.size() > 0) {
												int recommendId;
												for (int i = 0; i < uppdateRepeat
														.size();

												i++) {
													if (uppdateRepeat.get

													(i).recommendedUserId != null
															&& !"".equals

															(uppdateRepeat

															.get(i).recommendedUserId)) {
														recommendId =

														Integer.parseInt(uppdateRepeat
																.get

																(i).recommendedUserId);
													} else {
														recommendId = 0;
													}

													application
															.updateCLRepeatTableData(
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repId),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repBeforeTime),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repColorType),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repDisplayTime),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repType),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repIsAlarm),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repIsPuase),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repIsImportant),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repSourceType),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repChangeState),
																	uppdateRepeat
																			.get

																			(i).repTypeParameter,
																	uppdateRepeat
																			.get

																			(i).repNextCreatedTime

																			.replace(
																					"T",
																					" "),
																	uppdateRepeat
																			.get

																			(i).repLastCreatedTime

																			.replace(
																					"T",
																					" "),
																	uppdateRepeat
																			.get

																			(i).repInitialCreatedTime

																			.replace(
																					"T",
																					" "),
																	uppdateRepeat
																			.get

																			(i).repStartDate

																			.replace(
																					"T",
																					" "),
																	uppdateRepeat
																			.get

																			(i).repContent,
																	uppdateRepeat
																			.get

																			(i).repCreateTime

																			.replace(
																					"T",
																					" "),
																	uppdateRepeat
																			.get

																			(i).repSourceDesc,
																	uppdateRepeat
																			.get

																			(i).repSourceDescSpare,
																	uppdateRepeat
																			.get

																			(i).repTime,
																	uppdateRepeat
																			.get

																			(i).repRingDesc,
																	uppdateRepeat
																			.get

																			(i).repRingCode,
																	uppdateRepeat
																			.get

																			(i).repUpdateTime

																			.replace(
																					"T",
																					" "),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repOpenState),
																	uppdateRepeat
																			.get

																			(i).recommendedUserName,
																	recommendId,
																	uppdateRepeat
																			.get

																			(i).repdateone

																			.replace(
																					"T",
																					" "),
																	uppdateRepeat
																			.get

																			(i).repdatetwo

																			.replace(
																					"T",
																					" "),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repstateone),
																	Integer.parseInt

																	(uppdateRepeat

																	.get(i).repstatetwo),
																	uppdateRepeat
																			.get

																			(i).aType,
																	uppdateRepeat
																			.get

																			(i).webUrl,
																	uppdateRepeat
																			.get

																			(i).imgPath,
																	uppdateRepeat
																			.get

																			(i).repInSTable,
																	uppdateRepeat
																			.get

																			(i).repEndState,
																	uppdateRepeat
																			.get

																			(i).parReamrk,
																	uppdateRepeat
																			.get

																			(i).repRead,
																	0);

													application
															.updateRepUpdateState(Integer

																	.parseInt(uppdateRepeat

																	.get(i).repId));
												}
											}
										} else if (backBean.status == 1) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNREPTIME,
													backBean.message.replace(
															"T", " "));
										} else {
										}

									} catch (Exception e) {
										e.printStackTrace();
									} 
									if (upList != null && upList.size() > 0) {
										UpLoadSch(schuppath, schjson);
									}
									// else {
									// DownLoadRepeatDataAsync(downreppath);
									// }
								}
							}).start();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						if (upList != null && upList.size() > 0) {
							UpLoadSch(schuppath, schjson);
						}
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("json", json);
				return map;
			}
		};
		request.setTag("upload");
		request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

}
