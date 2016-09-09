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
import com.mission.schedule.bean.DownLoadBackBean;
import com.mission.schedule.bean.DownLoadBean;
import com.mission.schedule.bean.DownLoadRepeatBackBean;
import com.mission.schedule.bean.DownLoadRepeatBean;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.bean.RepeatUpAndDownBean;
import com.mission.schedule.bean.RepeatUpLoadBackBean;
import com.mission.schedule.bean.RepeatUpLoadBean;
import com.mission.schedule.bean.ScheduBean;
import com.mission.schedule.bean.UpLoadBackBean;
import com.mission.schedule.bean.UpLoadBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.fragment.MyScheduleFragment;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.XmlUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpLoadService extends Service {

	public static final String RepUPDATADATA = "repUpdateData";

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
	int index;
	private MyBinder mBinder = new MyBinder();
	CalendarChangeValue changeValue = new CalendarChangeValue();
	List<Map<String, String>> soundlist = new ArrayList<Map<String, String>>();
	String downreppath = "";
	String downschpath = "";
	String schuppath = "";
	String schjson = "";
	String repeatUpPath = "";
	String repJson = "";
	private int IsRepeat = 0;

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
		index = Integer.parseInt(sharedPrefUtil.getString(getApplication(),
				ShareFile.USERFILE, ShareFile.INDEX, "0"));
		if (index == 3) {
			sharedPrefUtil.putString(getApplication(), ShareFile.USERFILE,
					ShareFile.INDEX, 1 + "");
		}
		soundlist.clear();
		soundlist = XmlUtil.readBeforeBellXML(this);
		UserId = sharedPrefUtil.getString(getApplicationContext(),
				ShareFile.USERFILE, ShareFile.USERID, "");
		if ("".equals(UserId)) {

		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					IsRepeat = 0;
					String date = DateUtil.formatDateTimeSs(new Date());
					int year1 = Integer.parseInt(date.substring(0, 4)
							.toString()) - 1;
					if ("0".equals(sharedPrefUtil.getString(getApplication(),
							ShareFile.USERFILE, ShareFile.FIRSTLOGIN, "0"))) {
						schyear = String.valueOf(year1)
								+ date.substring(4, 10).toString();
						schtime = date.substring(11, 19).toString();
						repyear = String.valueOf(year1)
								+ date.substring(4, 10).toString();
						reptime = date.substring(11, 19).toString();
					} else {
						String schdate = sharedPrefUtil.getString(
								getApplication(), ShareFile.USERFILE,
								ShareFile.DOWNSCHTIME, "");
						if (!"".equals(schdate)) {
							try {
								schyear = schdate.substring(0, 10);
								schtime = schdate.substring(11);
							}catch (Exception e){
								schyear = DateUtil.formatDate(new Date());
								schtime = DateUtil.formatDateTimeSs(new Date()).substring(11);
							}
						} else {
							schyear = String.valueOf(year1)
									+ date.substring(4, 10).toString();
							schtime = date.substring(11, 19).toString();
						}
						String repdate = sharedPrefUtil.getString(
								getApplication(), ShareFile.USERFILE,
								ShareFile.DOWNREPTIME, "");
						if (!"".equals(repdate)) {
							try {
								repyear = repdate.substring(0, 10);
								reptime = repdate.substring(11);
							}catch (Exception e){
								repyear = DateUtil.formatDate(new Date());
								reptime = DateUtil.formatDateTimeSs(new Date()).substring(11);
							}
						} else {
							repyear = String.valueOf(year1)
									+ date.substring(4, 10).toString();
							reptime = date.substring(11, 19).toString();
						}
					}
					if (intent != null) {
						if (Const.SHUAXINDATA.equals(intent.getAction())) {
							try {
								index++;
								upRepeatList = application
										.QueryAllChongFuData(2);
								JSONArray jsonarray1 = new JSONArray();
								JSONObject jsonobject2 = new JSONObject();
								if (upRepeatList != null
										&& upRepeatList.size() > 0) {
									repJson = "";
									for (int i = 0; i < upRepeatList.size(); i++) {
										if ((Integer.parseInt(upRepeatList.get(
												i).get(CLRepeatTable.repID)) < 0 && Integer
												.parseInt(upRepeatList
														.get(i)
														.get(CLRepeatTable.repUpdateState)) == 1)
												|| (Integer
														.parseInt(upRepeatList
																.get(i)
																.get(CLRepeatTable.repID)) > 0 && Integer
														.parseInt(upRepeatList
																.get(i)
																.get(CLRepeatTable.repUpdateState)) != 1)) {
											JSONObject jsonobject3 = new JSONObject();
											if (Integer.parseInt(upRepeatList
													.get(i)
													.get(CLRepeatTable.repID)) < 0) {
												jsonobject3
														.put("tempId",
																upRepeatList
																		.get(i)
																		.get(CLRepeatTable.repID));
											} else {
												jsonobject3
														.put("repId",
																upRepeatList
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
											jsonobject3
													.put("repType",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repType));
											jsonobject3
													.put("repIsAlarm",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repIsAlarm));
											jsonobject3
													.put("repIsPuase",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repIsPuase));
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
											jsonobject3
													.put("repstateone",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repStateOne));
											jsonobject3
													.put("repstatetwo",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repStateTwo));
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
											jsonobject3
													.put("repContent",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repContent));
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
											jsonobject3
													.put("repTime",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repTime));
											jsonobject3
													.put("repRingDesc",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repRingDesc));
											jsonobject3
													.put("repRingCode",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repRingCode));
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
											jsonobject3
													.put("repdateone",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repDateOne));
											jsonobject3
													.put("repdatetwo",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repDateTwo));
											jsonobject3
													.put("aType",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repAType));
											jsonobject3
													.put("webUrl",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repWebURL));
											jsonobject3
													.put("imgPath",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repImagePath));
											jsonobject3
													.put("repInSTable",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repInSTable));
											jsonobject3
													.put("repEndState",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repEndState));
											jsonobject3
													.put("parReamrk",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.parReamrk));
											jsonobject3
													.put("repRead",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repRead));
											jsonobject3
													.put("recommendedUserId",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repcommendedUserId));
											jsonarray1.put(jsonobject3);
										}
									}
								}
								jsonobject2.put("data", jsonarray1);
								repJson = jsonobject2.toString();
								repeatUpPath = URLConstants.重复数据上传;
								// if (upRepeatList != null &&
								// upRepeatList.size() >
								// 0) {
								// NewRepeatAsync(repeatUpPath,
								// jsonobject2.toString());
								// }
								upList = application.queryAllSchData(-1,0,0);
								JSONArray jsonarray = new JSONArray();
								JSONObject jsonobject = new JSONObject();
								if (upList != null && upList.size() > 0) {
									for (int i = 0; i < upList.size(); i++) {
										if ((Integer.parseInt(upList.get(i)
												.get(ScheduleTable.schID)) < 0 && Integer
												.parseInt(upList
														.get(i)
														.get(ScheduleTable.schUpdateState)) == 1)
												|| (Integer
														.parseInt(upList
																.get(i)
																.get(ScheduleTable.schID)) > 0 && Integer
														.parseInt(upList
																.get(i)
																.get(ScheduleTable.schUpdateState)) != 1)) {
											JSONObject jsonobject1 = new JSONObject();
											if (Integer.parseInt(upList.get(i)
													.get(ScheduleTable.schID)) < 0) {
												jsonobject1
														.put("tempId",
																upList.get(i)
																		.get(ScheduleTable.schID));
											} else {
												jsonobject1
														.put("cId",
																upList.get(i)
																		.get(ScheduleTable.schID));
											}

											jsonobject1.put("cUid", UserId);
											jsonobject1
													.put("cContent",
															upList.get(i)
																	.get(ScheduleTable.schContent));
											jsonobject1
													.put("cDate",
															upList.get(i)
																	.get(ScheduleTable.schDate));
											jsonobject1
													.put("cTime",
															upList.get(i)
																	.get(ScheduleTable.schTime));
											jsonobject1
													.put("cIsAlarm",
															upList.get(i)
																	.get(ScheduleTable.schIsAlarm));
											jsonobject1
													.put("cBeforTime",
															upList.get(i)
																	.get(ScheduleTable.schBeforeTime));
											jsonobject1
													.put("cDisplayAlarm",
															upList.get(i)
																	.get(ScheduleTable.schDisplayTime));
											jsonobject1
													.put("cPostpone",
															upList.get(i)
																	.get(ScheduleTable.schIsPostpone));
											jsonobject1
													.put("cImportant",
															upList.get(i)
																	.get(ScheduleTable.schIsImportant));
											jsonobject1
													.put("cColorType",
															upList.get(i)
																	.get(ScheduleTable.schColorType));
											jsonobject1
													.put("cIsEnd",
															upList.get(i)
																	.get(ScheduleTable.schIsEnd));
											jsonobject1
													.put("cCreateTime",
															upList.get(i)
																	.get(ScheduleTable.schCreateTime));
											jsonobject1
													.put("cTags",
															upList.get(i)
																	.get(ScheduleTable.schTags));
											jsonobject1
													.put("cType",
															upList.get(i)
																	.get(ScheduleTable.schSourceType));
											jsonobject1
													.put("cTypeDesc",
															upList.get(i)
																	.get(ScheduleTable.schSourceDesc));
											jsonobject1
													.put("cTypeSpare",
															upList.get(i)
																	.get(ScheduleTable.schSourceDescSpare));
											jsonobject1
													.put("cRepeatId",
															upList.get(i)
																	.get(ScheduleTable.schRepeatID));
											jsonobject1
													.put("cRepeatDate",
															upList.get(i)
																	.get(ScheduleTable.schRepeatDate));
											jsonobject1
													.put("cUpdateTime",
															upList.get(i)
																	.get(ScheduleTable.schUpdateTime));
											jsonobject1
													.put("cOpenState",
															upList.get(i)
																	.get(ScheduleTable.schOpenState));
											jsonobject1
													.put("cRecommendName",
															upList.get(i)
																	.get(ScheduleTable.schcRecommendName));
											jsonobject1
													.put("updateState",
															upList.get(i)
																	.get(ScheduleTable.schUpdateState));
											jsonobject1
													.put("cAlarmSoundDesc",
															upList.get(i)
																	.get(ScheduleTable.schRingDesc));
											jsonobject1
													.put("cAlarmSound",
															upList.get(i)
																	.get(ScheduleTable.schRingCode));
											jsonobject1
													.put("schRead",
															upList.get(i)
																	.get(ScheduleTable.schRead));
											jsonobject1
													.put("attentionid",
															upList.get(i)
																	.get(ScheduleTable.schAID));
											jsonobject1
													.put("aType",
															upList.get(i)
																	.get(ScheduleTable.schAType));
											jsonobject1
													.put("webUrl",
															upList.get(i)
																	.get(ScheduleTable.schWebURL));
											jsonobject1
													.put("imgPath",
															upList.get(i)
																	.get(ScheduleTable.schImagePath));
											jsonobject1
													.put("cSchRepeatLink",
															upList.get(i)
																	.get(ScheduleTable.schRepeatLink));
											jsonobject1
													.put("cRecommendId",
															upList.get(i)
																	.get(ScheduleTable.schcRecommendId));
											jsonarray.put(jsonobject1);
										}
									}
								}
								jsonobject.put("data", jsonarray);
								schjson = jsonobject.toString();
								schuppath = URLConstants.同步数据接口;
								// if(upList!=null&&upList.size()>0){
								// UpLoadSch(schuppath, json);
								// }
								downreppath = URLConstants.重复数据下载 + "?cUid="
										+ UserId + "&beforDownTime=" + repyear
										+ "&time=" + reptime;
								downschpath = URLConstants.数据下载同步 + "?cUid="
										+ UserId + "&beforDownTime=" + schyear
										+ "&time=" + schtime;
								// if ((upList != null && upList.size() > 0)
								// && (upRepeatList != null && upRepeatList
								// .size() > 0)) {
								// NewRepeatAsync(repeatUpPath,
								// jsonobject2.toString());
								// } else {
								// if (upRepeatList != null
								// && upRepeatList.size() > 0) {
								// NewRepeatAsync(repeatUpPath,
								// jsonobject2.toString());
								// } else if (upList != null && upList.size() >
								// 0) {
								// UpLoadSch(schuppath, json);
								// } else {
								// DownLoadRepeatDataAsync(downreppath);
								// }
								// }
								DownLoadRepeatDataAsync(downreppath);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (Const.UPLOADDATA.equals(intent.getAction())) {
							try {
								upRepeatList = application
										.QueryAllChongFuData(2);
								JSONArray jsonarray1 = new JSONArray();
								JSONObject jsonobject2 = new JSONObject();
								if (upRepeatList != null
										&& upRepeatList.size() > 0) {
									repJson = "";
									for (int i = 0; i < upRepeatList.size(); i++) {
										if ((Integer.parseInt(upRepeatList.get(
												i).get(CLRepeatTable.repID)) < 0 && Integer
												.parseInt(upRepeatList
														.get(i)
														.get(CLRepeatTable.repUpdateState)) == 1)
												|| (Integer
														.parseInt(upRepeatList
																.get(i)
																.get(CLRepeatTable.repID)) > 0 && Integer
														.parseInt(upRepeatList
																.get(i)
																.get(CLRepeatTable.repUpdateState)) != 1)) {
											JSONObject jsonobject3 = new JSONObject();
											if (Integer.parseInt(upRepeatList
													.get(i)
													.get(CLRepeatTable.repID)) < 0) {
												jsonobject3
														.put("tempId",
																upRepeatList
																		.get(i)
																		.get(CLRepeatTable.repID));
											} else {
												jsonobject3
														.put("repId",
																upRepeatList
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
											jsonobject3
													.put("repType",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repType));
											jsonobject3
													.put("repIsAlarm",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repIsAlarm));
											jsonobject3
													.put("repIsPuase",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repIsPuase));
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
											jsonobject3
													.put("repstateone",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repStateOne));
											jsonobject3
													.put("repstatetwo",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repStateTwo));
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
											jsonobject3
													.put("repContent",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repContent));
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
											jsonobject3
													.put("repTime",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repTime));
											jsonobject3
													.put("repRingDesc",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repRingDesc));
											jsonobject3
													.put("repRingCode",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repRingCode));
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
											jsonobject3
													.put("repdateone",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repDateOne));
											jsonobject3
													.put("repdatetwo",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repDateTwo));
											jsonobject3
													.put("aType",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repAType));
											jsonobject3
													.put("webUrl",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repWebURL));
											jsonobject3
													.put("imgPath",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repImagePath));
											jsonobject3
													.put("repInSTable",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repInSTable));
											jsonobject3
													.put("repEndState",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repEndState));
											jsonobject3
													.put("parReamrk",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.parReamrk));
											jsonobject3
													.put("repRead",
															upRepeatList
																	.get(i)
																	.get(CLRepeatTable.repRead));
											jsonobject3
													.put("recommendedUserId",
															upRepeatList
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
										if ((Integer.parseInt(upList.get(i)
												.get(ScheduleTable.schID)) < 0 && Integer
												.parseInt(upList
														.get(i)
														.get(ScheduleTable.schUpdateState)) == 1)
												|| (Integer
														.parseInt(upList
																.get(i)
																.get(ScheduleTable.schID)) > 0 && Integer
														.parseInt(upList
																.get(i)
																.get(ScheduleTable.schUpdateState)) != 1)) {
											JSONObject jsonobject1 = new JSONObject();
											if (Integer.parseInt(upList.get(i)
													.get(ScheduleTable.schID)) < 0) {
												jsonobject1
														.put("tempId",
																upList.get(i)
																		.get(ScheduleTable.schID));
											} else {
												jsonobject1
														.put("cId",
																upList.get(i)
																		.get(ScheduleTable.schID));
											}
											jsonobject1
													.put("cRecommendId",
															upList.get(i)
																	.get(ScheduleTable.schcRecommendId));
											jsonobject1.put("cUid", UserId);
											jsonobject1
													.put("cContent",
															upList.get(i)
																	.get(ScheduleTable.schContent));
											jsonobject1
													.put("cDate",
															upList.get(i)
																	.get(ScheduleTable.schDate));
											jsonobject1
													.put("cTime",
															upList.get(i)
																	.get(ScheduleTable.schTime));
											jsonobject1
													.put("cIsAlarm",
															upList.get(i)
																	.get(ScheduleTable.schIsAlarm));
											jsonobject1
													.put("cBeforTime",
															upList.get(i)
																	.get(ScheduleTable.schBeforeTime));
											jsonobject1
													.put("cDisplayAlarm",
															upList.get(i)
																	.get(ScheduleTable.schDisplayTime));
											jsonobject1
													.put("cPostpone",
															upList.get(i)
																	.get(ScheduleTable.schIsPostpone));
											jsonobject1
													.put("cImportant",
															upList.get(i)
																	.get(ScheduleTable.schIsImportant));
											jsonobject1
													.put("cColorType",
															upList.get(i)
																	.get(ScheduleTable.schColorType));
											jsonobject1
													.put("cIsEnd",
															upList.get(i)
																	.get(ScheduleTable.schIsEnd));
											jsonobject1
													.put("cCreateTime",
															upList.get(i)
																	.get(ScheduleTable.schCreateTime));
											jsonobject1
													.put("cTags",
															upList.get(i)
																	.get(ScheduleTable.schTags));
											jsonobject1
													.put("cType",
															upList.get(i)
																	.get(ScheduleTable.schSourceType));
											jsonobject1
													.put("cTypeDesc",
															upList.get(i)
																	.get(ScheduleTable.schSourceDesc));
											jsonobject1
													.put("cTypeSpare",
															upList.get(i)
																	.get(ScheduleTable.schSourceDescSpare));
											jsonobject1
													.put("cRepeatId",
															upList.get(i)
																	.get(ScheduleTable.schRepeatID));
											jsonobject1
													.put("cRepeatDate",
															upList.get(i)
																	.get(ScheduleTable.schRepeatDate));
											jsonobject1
													.put("cUpdateTime",
															upList.get(i)
																	.get(ScheduleTable.schUpdateTime));
											jsonobject1
													.put("cOpenState",
															upList.get(i)
																	.get(ScheduleTable.schOpenState));
											jsonobject1
													.put("cRecommendName",
															upList.get(i)
																	.get(ScheduleTable.schcRecommendName));
											jsonobject1
													.put("updateState",
															upList.get(i)
																	.get(ScheduleTable.schUpdateState));
											jsonobject1
													.put("cAlarmSoundDesc",
															upList.get(i)
																	.get(ScheduleTable.schRingDesc));
											jsonobject1
													.put("cAlarmSound",
															upList.get(i)
																	.get(ScheduleTable.schRingCode));
											jsonobject1
													.put("schRead",
															upList.get(i)
																	.get(ScheduleTable.schRead));
											jsonobject1
													.put("attentionid",
															upList.get(i)
																	.get(ScheduleTable.schAID));
											jsonobject1
													.put("aType",
															upList.get(i)
																	.get(ScheduleTable.schAType));
											jsonobject1
													.put("webUrl",
															upList.get(i)
																	.get(ScheduleTable.schWebURL));
											jsonobject1
													.put("imgPath",
															upList.get(i)
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
										&& (upRepeatList != null && upRepeatList
												.size() > 0)) {
									NewRepeatAsync(repeatUpPath, repJson);
								} else {
									if (upRepeatList != null
											&& upRepeatList.size() > 0) {
										NewRepeatAsync(repeatUpPath, repJson);
									} else if (upList != null
											&& upList.size() > 0) {
										UpLoadSch(schuppath, schjson);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							stopSelf();
						}
					} else {
						Intent intent1 = new Intent(Const.SHUAXINDATA);
						intent1.putExtra("data", "fail");
						sendBroadcast(intent1);
						stopSelf();
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
		App.getHttpQueues().cancelAll("download");
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
													System.out
															.println(uppdate.get(i).toString());
													String str = uppdate.get(i).cUpdateTime
															.toString();
													int aid = uppdate.get(i).attentionid;
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
										if ((add != null && add.size() > 0)
												|| IsRepeat == 1) {
											if (MyScheduleFragment.schIDList != null
													&& MyScheduleFragment.schIDList
															.size() > 0) {
												MyScheduleFragment.schIDList
														.clear();
											}
											if (add != null && add.size() > 0) {
												MyScheduleFragment.schIDList = add;
											} else if (IsRepeat == 1) {
												Intent intent = new Intent();
												intent.setAction(Const.SHUAXINDATA);
												intent.putExtra("data",
														"success");
												intent.putExtra("index", index);
												intent.putExtra("what", 1);
												sendBroadcast(intent);
											}
										}
									} catch (JsonSyntaxException e) {
										e.printStackTrace();
									}
									// DownLoadRepeatDataAsync(downreppath);
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

	private void DownLoadSchAsync(String path) {
		System.out.println("日程下载:" + path);
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {

					@Override
					public void onResponse(final String result) {
						if (!TextUtils.isEmpty(result)) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Gson gson = new Gson();
										DownLoadBackBean downLoadBean = gson
												.fromJson(result,
														DownLoadBackBean.class);

										List<DownLoadBean> beans = null;
										List<ScheduBean> list = null;
										List<Integer> deleteID = null;
										if (downLoadBean.status == 0) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNSCHTIME,
													downLoadBean.message
															.replace("T", " "));
											beans = downLoadBean.maps;
											for (int k = 0; k < beans.size(); k++) {
												deleteID = beans.get(k).delete;
												list = beans.get(k).tbCalendar;
											}
											// if (mapList != null &&
											// mapList.size() > 0) {
											if (list != null && list.size() > 0) {
												// for (int j = 0; j <
												// mapList.size(); j++)
												// {
												for (int i = 0; i < list.size(); i++) {
													String ringcode = "";
													String ringdesc = "";
													for (int j = 0; j < soundlist
															.size(); j++) {
														String str = list
																.get(i).cAlarmSoundDesc
																.replace(
																		".ogg",
																		"");
														if (str.equals(soundlist
																.get(j)
																.get("value"))) {
															ringcode = soundlist
																	.get(j)
																	.get("value");
															ringdesc = soundlist
																	.get(j)
																	.get("name");
															break;
														} else {
															ringcode = "g_88";
															ringdesc = "完成任务";
														}
													}
													String str = list.get(i).cUpdateTime
															.toString()
															.replace("T", " ");
													int m = application
															.CheckCountSchData(list
																	.get(i).cId);
													int aid = list.get(i).attentionid;
													int atype = list.get(i).aType;
													if (m == 0) {
														application
																.insertIntenetScheduleData(
																		list.get(i).cId,
																		list.get(i).cContent,
																		list.get(i).cDate,
																		list.get(i).cTime,
																		list.get(i).cIsAlarm,
																		list.get(i).cBeforTime,
																		list.get(i).cDisplayAlarm,
																		list.get(i).cPostpone,
																		list.get(i).cImportant,
																		list.get(i).cColorType,
																		list.get(i).cIsEnd,
																		str,
																		list.get(i).cTags,
																		list.get(i).cType,
																		list.get(i).cTypeDesc,
																		list.get(i).cTypeSpare,
																		list.get(i).cRepeatId,
																		list.get(i).cRepeatDate,
																		str,
																		0,
																		list.get(i).cOpenState,
																		list.get(i).cSchRepeatLink,
																		ringdesc,
																		ringcode,
																		list.get(i).cRecommendName,
																		list.get(i).schRead,
																		aid,
																		atype,
																		list.get(i).webUrl,
																		list.get(i).imgPath,
																		1,
																		0,
																		list.get(i).cRecommendId);
													} else {
														application
																.updateScheduleData(
																		list.get(i).cId,
																		list.get(i).cContent,
																		list.get(i).cDate,
																		list.get(i).cTime,
																		list.get(i).cIsAlarm,
																		list.get(i).cBeforTime,
																		list.get(i).cDisplayAlarm,
																		list.get(i).cPostpone,
																		list.get(i).cImportant,
																		list.get(i).cColorType,
																		list.get(i).cIsEnd,
																		list.get(i).cTags,
																		list.get(i).cType,
																		list.get(i).cTypeDesc,
																		list.get(i).cTypeSpare,
																		list.get(i).cRepeatId,
																		list.get(i).cRepeatDate,
																		str,
																		0,
																		list.get(i).cOpenState,
																		list.get(i).cSchRepeatLink,
																		ringdesc,
																		ringcode,
																		list.get(i).cRecommendName,
																		list.get(i).schRead,
																		aid,
																		list.get(i).aType,
																		list.get(i).webUrl,
																		list.get(i).imgPath,
																		0,
																		0,
																		list.get(i).cRecommendId);
													}
												}
											}

											if (deleteID != null
													&& deleteID.size() > 0) {
												for (int i = 0; i < deleteID
														.size(); i++) {
													application
															.deleteScheduleData(deleteID
																	.get(i));
													application
															.deleteSch(deleteID
																	.get(i));
												}
											} else {
											}
											QueryAlarmData
													.writeAlarm(getApplicationContext());
											sharedPrefUtil
													.putString(
															getApplication(),
															ShareFile.USERFILE,
															ShareFile.INDEX,
															index + "");
											if (list != null && list.size() > 0) {
												Intent intent = new Intent();
												intent.setAction(Const.SHUAXINDATA);
												intent.putExtra("data",
														"success");
												intent.putExtra("index", index);
												intent.putExtra("what", 1);
												sendBroadcast(intent);
											} else {
												if (IsRepeat == 1) {
													Intent intent = new Intent();
													intent.setAction(Const.SHUAXINDATA);
													intent.putExtra("data",
															"success");
													intent.putExtra("index",
															index);
													intent.putExtra("what", 1);
													sendBroadcast(intent);
												} else {
													Intent intent = new Intent();
													intent.setAction(Const.SHUAXINDATA);
													intent.putExtra("data",
															"success");
													intent.putExtra("index",
															-10);
													intent.putExtra("what", 0);
													sendBroadcast(intent);
												}
											}
											// stopSelf();
										} else if (downLoadBean.status == 1) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNSCHTIME,
													downLoadBean.message
															.replace("T", " "));
											sharedPrefUtil
													.putString(
															getApplication(),
															ShareFile.USERFILE,
															ShareFile.INDEX,
															index + "");
										} else {
											sharedPrefUtil
													.putString(
															getApplication(),
															ShareFile.USERFILE,
															ShareFile.INDEX,
															index + "");
											// stopSelf();
										}
									} catch (JsonSyntaxException e) {
										e.printStackTrace();
										sharedPrefUtil.putString(
												getApplication(),
												ShareFile.USERFILE,
												ShareFile.INDEX, index + "");
										// stopSelf();
									}
									if ((upList != null && upList.size() > 0)
											&& (upRepeatList != null && upRepeatList
													.size() > 0)) {
										NewRepeatAsync(repeatUpPath, repJson);
									} else {
										if (upRepeatList != null
												&& upRepeatList.size() > 0) {
											NewRepeatAsync(repeatUpPath,
													repJson);
										} else if (upList != null
												&& upList.size() > 0) {
											UpLoadSch(schuppath, schjson);
										}
									}
								}
							}).start();

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);
						Intent intent = new Intent(Const.SHUAXINDATA);
						intent.putExtra("data", "success");
						intent.putExtra("index", -10);
						intent.putExtra("what", 0);
						sendBroadcast(intent);
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
					}
				});
		request.setTag("download");
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
													application

															.updateRepUpdateState(Integer

															.parseInt(str));
													application
															.UpdateClockRepID

															(index, str);
													application.UpdateSchrepID(
															Integer.parseInt

															(index),
															Integer.parseInt

															(addRepeat.get(

															i).get(index)));
												}
												Intent intent = new Intent();
												intent.setAction(RepUPDATADATA);
												intent.putExtra("data",
														"success");
												sendBroadcast(intent);
												if (upList != null
														&& upList.size() > 0) {
													IsRepeat = 1;
												} else {
													Intent intent1 = new Intent();
													intent1.setAction(Const.SHUAXINDATA);
													intent1.putExtra("data",
															"success");
													intent1.putExtra("index", 1);
													intent1.putExtra("what", 1);
													sendBroadcast(intent1);
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
											Intent intent = new Intent(
													RepUPDATADATA);
											intent.putExtra("data", "success");
											sendBroadcast(intent);
										} else if (backBean.status == 1) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNREPTIME,
													backBean.message.replace(
															"T", " "));
										} else {
											Intent intent = new Intent(
													RepUPDATADATA);
											intent.putExtra("data", "success");
											sendBroadcast(intent);
										}

									} catch (JsonSyntaxException e) {
										e.printStackTrace();
										return;
									} catch (NumberFormatException e) {
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

	private void DownLoadRepeatDataAsync(final String path) {
		System.out.println("重复下载:" + path);
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {

					@Override
					public void onResponse(final String result) {
						if (!TextUtils.isEmpty(result)) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Gson gson = new Gson();
										DownLoadRepeatBackBean downLoadBean;

										downLoadBean = gson.fromJson(result,
												DownLoadRepeatBackBean.class);

										List<DownLoadRepeatBean> beans = null;
										List<RepeatUpAndDownBean> list = null;
										List<Integer> deleteID = null;
										if (downLoadBean.status == 0) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNREPTIME,
													downLoadBean.message
															.replace("T", " "));
											beans = downLoadBean.maps;
											for (int k = 0; k < beans.size(); k++) {
												deleteID = beans.get(k).delete;
												list = beans.get(k).TbTtimepreinstall;
											}
											if (list != null && list.size() > 0) {
												for (int i = 0; i < list.size(); i++) {
													String ringcode = "";
													String ringdesc = "";
													for (int j = 0; j < soundlist
															.size(); j++) {
														String str = list
																.get(i).repRingCode
																.replace(
																		".ogg",
																		"");
														if (str.equals(soundlist
																.get(j)
																.get("value"))) {
															ringcode = soundlist
																	.get(j)
																	.get("value");
															ringdesc = soundlist
																	.get(j)
																	.get("name");
															break;
														} else {
															ringcode = "g_88";
															ringdesc = "完成任务";
														}
													}
													int index = 0;
													if (list.get(i).recommendedUserId != null
															&& !"".equals(list
																	.get(i).recommendedUserId)) {
														index = Integer.parseInt(list
																.get(i).recommendedUserId);
													}
													// 根据repid查询重复表，看下是否有repid相同的母记事
													int count = application
															.CheckCountRepData(Integer
																	.parseInt(list
																			.get(i).repId));
													if (count == 0) {
														if (("".equals(list
																.get(i).repTypeParameter) || "null".equals(list
																.get(i).repTypeParameter))
																&& (!"1".equals(list
																		.get(i).repType) || !"5"
																		.equals(list
																				.get(i).repType))) {
															String typestr = "";
															int type = 5;
															String parstr = "";
															if ("1".equals(list
																	.get(i).repType)) {
																typestr = "["
																		+ ""
																		+ "]";
																type = 1;
															} else if ("2"
																	.equals(list
																			.get(i).repType)) {
																typestr = "["
																		+ "1"
																		+ "]";
																type = 2;
															} else if ("3"
																	.equals(list
																			.get(i).repType)) {
																typestr = "["
																		+ "\""
																		+ 1
																		+ "\""
																		+ "]";
																type = 3;
															} else if ("4"
																	.equals(list
																			.get(i).repType)) {
																typestr = "["
																		+ "\""
																		+ "01-01"
																		+ "\""
																		+ "]";
																type = 4;
															} else if ("6"
																	.equals(list
																			.get(i).repType)) {
																typestr = "["
																		+ "\""
																		+ "正月初一"
																		+ "\""
																		+ "]";
																type = 6;
																parstr = "01-01";
															} else {
																typestr = "["
																		+ ""
																		+ "]";
																type = 5;
															}
															application
																	.insertDownCLRepeatTableData(
																			Integer.parseInt(list
																					.get(i).repId),
																			Integer.parseInt(list
																					.get(i).repBeforeTime),
																			Integer.parseInt(list
																					.get(i).repColorType),
																			Integer.parseInt(list
																					.get(i).repDisplayTime),
																			type,
																			Integer.parseInt(list
																					.get(i).repIsAlarm),
																			Integer.parseInt(list
																					.get(i).repIsPuase),
																			Integer.parseInt(list
																					.get(i).repIsImportant),
																			Integer.parseInt(list
																					.get(i).repSourceType),
																			0,
																			typestr,
																			list.get(i).repNextCreatedTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repLastCreatedTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repInitialCreatedTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repStartDate
																					.replace(
																							"T",
																							" "),
																			list.get(i).repContent,
																			list.get(i).repCreateTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repSourceDesc,
																			list.get(i).repSourceDescSpare,
																			list.get(i).repTime,
																			ringdesc,
																			ringcode,
																			list.get(i).repUpdateTime
																					.replace(
																							"T",
																							" "),
																			Integer.parseInt(list
																					.get(i).repOpenState),
																			list.get(i).recommendedUserName,
																			index,
																			list.get(i).repdateone,
																			list.get(i).repdatetwo,
																			Integer.parseInt(list
																					.get(i).repstateone),
																			Integer.parseInt(list
																					.get(i).repstatetwo),
																			list.get(i).aType,
																			list.get(i).webUrl,
																			list.get(i).imgPath,
																			list.get(i).repInSTable,
																			list.get(i).repEndState,
																			parstr,
																			list.get(i).repRead,
																			0);
														} else {
															String parstr = "";
															if ("6".equals(list
																	.get(i).repType)) {
																if (changeValue
																		.changaSZ(
																				list.get(i).repTypeParameter
																						.replace(
																								"[",
																								"")
																						.replace(
																								"]",
																								"")
																						.replace(
																								"\n\"",
																								"")
																						.replace(
																								"\n",
																								"")
																						.replace(
																								"\"",
																								""))
																		.length() == 2) {
																	parstr = DateUtil
																			.formatDate(
																					new Date())
																			.substring(
																					5,
																					7)
																			+ "-"
																			+ changeValue
																					.changaSZ(list
																							.get(i).repTypeParameter
																							.replace(
																									"[",
																									"")
																							.replace(
																									"]",
																									"")
																							.replace(
																									"\n\"",
																									"")
																							.replace(
																									"\n",
																									"")
																							.replace(
																									"\"",
																									""));
																} else {
																	parstr = changeValue
																			.changaSZ(list
																					.get(i).repTypeParameter
																					.replace(
																							"[",
																							"")
																					.replace(
																							"]",
																							"")
																					.replace(
																							"\n\"",
																							"")
																					.replace(
																							"\n",
																							"")
																					.replace(
																							"\"",
																							""));
																}
															}
															application
																	.insertDownCLRepeatTableData(
																			Integer.parseInt(list
																					.get(i).repId),
																			Integer.parseInt(list
																					.get(i).repBeforeTime),
																			Integer.parseInt(list
																					.get(i).repColorType),
																			Integer.parseInt(list
																					.get(i).repDisplayTime),
																			Integer.parseInt(list
																					.get(i).repType),
																			Integer.parseInt(list
																					.get(i).repIsAlarm),
																			Integer.parseInt(list
																					.get(i).repIsPuase),
																			Integer.parseInt(list
																					.get(i).repIsImportant),
																			Integer.parseInt(list
																					.get(i).repSourceType),
																			0,
																			list.get(i).repTypeParameter,
																			list.get(i).repNextCreatedTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repLastCreatedTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repInitialCreatedTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repStartDate
																					.replace(
																							"T",
																							" "),
																			list.get(i).repContent,
																			list.get(i).repCreateTime
																					.replace(
																							"T",
																							" "),
																			list.get(i).repSourceDesc,
																			list.get(i).repSourceDescSpare,
																			list.get(i).repTime,
																			ringdesc,
																			ringcode,
																			list.get(i).repUpdateTime
																					.replace(
																							"T",
																							" "),
																			Integer.parseInt(list
																					.get(i).repOpenState),
																			list.get(i).recommendedUserName,
																			index,
																			list.get(i).repdateone,
																			list.get(i).repdatetwo,
																			Integer.parseInt(list
																					.get(i).repstateone),
																			Integer.parseInt(list
																					.get(i).repstatetwo),
																			list.get(i).aType,
																			list.get(i).webUrl,
																			list.get(i).imgPath,
																			list.get(i).repInSTable,
																			list.get(i).repEndState,
																			parstr,
																			list.get(i).repRead,
																			0);
														}
														if (list.get(i).repInSTable == 0) {
															application
																	.deleteChildSch(list
																			.get(i).repId);
															if ("0".equals(list
																	.get(i).repIsPuase)) {
																if (Integer
																		.parseInt(list
																				.get(i).repstateone) == 0
																		&& Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 0) {
																	if (DateUtil
																			.formatDate(
																					DateUtil.parseDateTime(list
																							.get(i).repInitialCreatedTime
																							.replace(
																									"T",
																									" ")))
																			.equals(DateUtil
																					.formatDate(new Date()))) {
																		if (DateUtil
																				.parseDateTime(
																						list.get(i).repNextCreatedTime
																								.replace(
																										"T",
																										" "))
																				.after(DateUtil
																						.parseDateTime(DateUtil
																								.formatDateTime(new Date())))) {
																			CreateRepeatSchNextData(list
																					.get(i));
																		} else if (DateUtil
																				.parseDateTime(
																						list.get(i).repNextCreatedTime
																								.replace(
																										"T",
																										" "))
																				.equals(DateUtil
																						.parseDateTime(DateUtil
																								.formatDateTime(new Date())))) {
																			CreateRepeatSchNextData(list
																					.get(i));
																		} else {
																			CreateRepeatSchData(list
																					.get(i));
																		}
																	} else {
																		CreateRepeatSchData(list
																				.get(i));
																	}
																} else {
																	if (!CreateRepeatSchDateData(list
																			.get(i)).repLastCreatedTime
																			.equals(list
																					.get(i).repdateone
																					.replace(
																							"T",
																							""))
																			&& !CreateRepeatSchDateData(list
																					.get(i)).repLastCreatedTime
																					.equals(list
																							.get(i).repdatetwo
																							.replace(
																									"T",
																									""))) {
																		if (DateUtil
																				.parseDateTime(
																						CreateRepeatSchDateData(list
																								.get(i)).repLastCreatedTime)
																				.before(DateUtil
																						.parseDateTime(list
																								.get(i).repInitialCreatedTime
																								.replace(
																										"T",
																										" ")))) {
																		} else {
																			if ("0".equals(list
																					.get(i).repstateone)) {
																				CreateRepeatSchLastData(list
																						.get(i));
																			} else {
																				CreateRepeatSchLastData(list
																						.get(i));
																			}
																		}
																	} else if (CreateRepeatSchDateData(list
																			.get(i)).repLastCreatedTime
																			.equals(list
																					.get(i).repdateone
																					.replace(
																							"T",
																							""))) {
																		if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 1) {

																		} else if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 2) {

																		}
																		if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 3) {
																			CreateRepeatSchEndLastData(list
																					.get(i)); // 脱钩时，生成下一条
																		} else if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 0) {
																			CreateRepeatSchLastData(list
																					.get(i)); // 脱钩时，生成下一条
																		}
																	} else if (CreateRepeatSchDateData(list
																			.get(i)).repLastCreatedTime
																			.equals(list
																					.get(i).repdatetwo
																					.replace(
																							"T",
																							""))) {// 时间与计算的时间进行比较
																		if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 1) {
																		} else if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 2) {
																		}
																		if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 3) {
																			CreateRepeatSchEndLastData(list
																					.get(i)); // 脱钩时，生成下一条
																		} else if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 0) {
																			CreateRepeatSchLastData(list
																					.get(i)); // 脱钩时，生成下一条
																		}
																	}
																	if (!CreateRepeatSchDateData(list
																			.get(i)).repNextCreatedTime
																			.equals(list
																					.get(i).repdateone
																					.replace(
																							"T",
																							""))
																			&& !CreateRepeatSchDateData(list
																					.get(i)).repNextCreatedTime
																					.equals(list
																							.get(i).repdatetwo
																							.replace(
																									"T",
																									""))) {
																		if (DateUtil
																				.parseDateTime(
																						CreateRepeatSchDateData(list
																								.get(i)).repNextCreatedTime)
																				.before(DateUtil
																						.parseDateTime(list
																								.get(i).repInitialCreatedTime
																								.replace(
																										"T",
																										" ")))) {
																		} else {
																			if ("0".equals(list
																					.get(i).repstatetwo)) {
																				CreateRepeatSchNextData(list
																						.get(i));
																			} else {
																				CreateRepeatSchNextData(list
																						.get(i));
																			}
																		}
																	} else if (CreateRepeatSchDateData(list
																			.get(i)).repNextCreatedTime
																			.equals(list
																					.get(i).repdateone
																					.replace(
																							"T",
																							""))) {
																		if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 1) {
																		} else if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 2) {
																		}
																		if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 3) {
																			CreateRepeatSchEndNextData(list
																					.get(i));

																		} else if (Integer
																				.parseInt(list
																						.get(i).repstateone) == 0) {
																			CreateRepeatSchNextData(list
																					.get(i));

																		}
																	} else if (CreateRepeatSchDateData(list
																			.get(i)).repNextCreatedTime
																			.equals(list
																					.get(i).repdatetwo
																					.replace(
																							"T",
																							""))) {// 时间与计算的时间进行比较
																		if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 1) {
																		} else if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 2) {
																		}
																		if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 3) {
																			CreateRepeatSchEndNextData(list
																					.get(i)); // 脱钩时，生成下一条
																		} else if (Integer
																				.parseInt(list
																						.get(i).repstatetwo) == 0) {
																			CreateRepeatSchNextData(list
																					.get(i)); // 脱钩时，生成下一条
																		}
																	}
																}
															}
														}
													} else {
														if (2 == list.get(i).repEndState) {
															application
																	.deleteChildSch(list
																			.get(i).repId);
														} else {
															// 有相同母记事就对本地的母记事进行修改，和网上的母记事进行同步
															if (("".equals(list
																	.get(i).repTypeParameter) || "null"
																	.equals(list
																			.get(i).repTypeParameter))
																	&& (!"1".equals(list
																			.get(i).repType) || !"5"
																			.equals(list
																					.get(i).repType))) {
																String typestr = "";
																String parString = "";
																int type = 5;
																if ("1".equals(list
																		.get(i).repType)) {
																	typestr = "["
																			+ ""
																			+ "]";
																	type = 1;
																} else if ("2"
																		.equals(list
																				.get(i).repType)) {
																	typestr = "["
																			+ "1"
																			+ "]";
																	type = 2;
																} else if ("3"
																		.equals(list
																				.get(i).repType)) {
																	typestr = "["
																			+ "\""
																			+ 1
																			+ "\""
																			+ "]";
																	type = 3;
																} else if ("4"
																		.equals(list
																				.get(i).repType)) {
																	typestr = "["
																			+ "\""
																			+ "01-01"
																			+ "\""
																			+ "]";
																	type = 4;
																} else if ("6"
																		.equals(list
																				.get(i).repType)) {
																	typestr = "["
																			+ "\""
																			+ "正月初一"
																			+ "\""
																			+ "]";
																	type = 6;
																	parString = "01-01";
																} else {
																	typestr = "["
																			+ ""
																			+ "]";
																	type = 5;
																}
																application
																		.updateCLRepeatTableData(
																				Integer.parseInt(list
																						.get(i).repId),
																				Integer.parseInt(list
																						.get(i).repBeforeTime),
																				Integer.parseInt(list
																						.get(i).repColorType),
																				Integer.parseInt(list
																						.get(i).repDisplayTime),
																				type,
																				Integer.parseInt(list
																						.get(i).repIsAlarm),
																				Integer.parseInt(list
																						.get(i).repIsPuase),
																				Integer.parseInt(list
																						.get(i).repIsImportant),
																				Integer.parseInt(list
																						.get(i).repSourceType),
																				0,
																				typestr,
																				list.get(i).repNextCreatedTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repLastCreatedTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repInitialCreatedTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repStartDate
																						.replace(
																								"T",
																								" "),
																				list.get(i).repContent,
																				list.get(i).repCreateTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repSourceDesc,
																				list.get(i).repSourceDescSpare,
																				list.get(i).repTime,
																				ringdesc,
																				ringcode,
																				list.get(i).repUpdateTime
																						.replace(
																								"T",
																								" "),
																				Integer.parseInt(list
																						.get(i).repOpenState),
																				list.get(i).recommendedUserName,
																				index,
																				list.get(i).repdateone,
																				list.get(i).repdatetwo,
																				Integer.parseInt(list
																						.get(i).repstateone),
																				Integer.parseInt(list
																						.get(i).repstatetwo),
																				list.get(i).aType,
																				list.get(i).webUrl,
																				list.get(i).imgPath,
																				list.get(i).repInSTable,
																				list.get(i).repEndState,
																				parString,
																				list.get(i).repRead,
																				0);
															} else {
																String parstr = "";
																if ("6".equals(list
																		.get(i).repType)) {
																	if (changeValue
																			.changaSZ(
																					list.get(i).repTypeParameter
																							.replace(
																									"[",
																									"")
																							.replace(
																									"]",
																									"")
																							.replace(
																									"\n\"",
																									"")
																							.replace(
																									"\n",
																									"")
																							.replace(
																									"\"",
																									""))
																			.length() == 2) {
																		parstr = DateUtil
																				.formatDate(
																						new Date())
																				.substring(
																						5,
																						7)
																				+ "-"
																				+ changeValue
																						.changaSZ(list
																								.get(i).repTypeParameter
																								.replace(
																										"[",
																										"")
																								.replace(
																										"]",
																										"")
																								.replace(
																										"\n\"",
																										"")
																								.replace(
																										"\n",
																										"")
																								.replace(
																										"\"",
																										""));
																	} else {
																		parstr = changeValue
																				.changaSZ(list
																						.get(i).repTypeParameter
																						.replace(
																								"[",
																								"")
																						.replace(
																								"]",
																								"")
																						.replace(
																								"\n\"",
																								"")
																						.replace(
																								"\n",
																								"")
																						.replace(
																								"\"",
																								""));
																	}
																}
																application
																		.updateCLRepeatTableData(
																				Integer.parseInt(list
																						.get(i).repId),
																				Integer.parseInt(list
																						.get(i).repBeforeTime),
																				Integer.parseInt(list
																						.get(i).repColorType),
																				Integer.parseInt(list
																						.get(i).repDisplayTime),
																				Integer.parseInt(list
																						.get(i).repType),
																				Integer.parseInt(list
																						.get(i).repIsAlarm),
																				Integer.parseInt(list
																						.get(i).repIsPuase),
																				Integer.parseInt(list
																						.get(i).repIsImportant),
																				Integer.parseInt(list
																						.get(i).repSourceType),
																				0,
																				list.get(i).repTypeParameter,
																				list.get(i).repNextCreatedTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repLastCreatedTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repInitialCreatedTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repStartDate
																						.replace(
																								"T",
																								" "),
																				list.get(i).repContent,
																				list.get(i).repCreateTime
																						.replace(
																								"T",
																								" "),
																				list.get(i).repSourceDesc,
																				list.get(i).repSourceDescSpare,
																				list.get(i).repTime,
																				ringdesc,
																				ringcode,
																				list.get(i).repUpdateTime
																						.replace(
																								"T",
																								" "),
																				Integer.parseInt(list
																						.get(i).repOpenState),
																				list.get(i).recommendedUserName,
																				index,
																				list.get(i).repdateone,
																				list.get(i).repdatetwo,
																				Integer.parseInt(list
																						.get(i).repstateone),
																				Integer.parseInt(list
																						.get(i).repstatetwo),
																				list.get(i).aType,
																				list.get(i).webUrl,
																				list.get(i).imgPath,
																				list.get(i).repInSTable,
																				list.get(i).repEndState,
																				parstr,
																				list.get(i).repRead,
																				0);
															}
															if (list.get(i).repInSTable == 0) {
																// 先删除本地repid相同的子记事
																application
																		.deleteChildSch(list
																				.get(i).repId);
																if ("0".equals(list
																		.get(i).repIsPuase)) {
																	if (Integer
																			.parseInt(list
																					.get(i).repstateone) == 0
																			&& Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 0) {
																		if (DateUtil
																				.formatDate(
																						DateUtil.parseDateTime(list
																								.get(i).repInitialCreatedTime
																								.replace(
																										"T",
																										" ")))
																				.equals(DateUtil
																						.formatDate(new Date()))) {
																			if (DateUtil
																					.parseDateTime(
																							list.get(i).repNextCreatedTime
																									.replace(
																											"T",
																											" "))
																					.after(DateUtil
																							.parseDateTime(DateUtil
																									.formatDateTime(new Date())))) {
																				CreateRepeatSchNextData(list
																						.get(i));
																			} else if (DateUtil
																					.parseDateTime(
																							list.get(i).repNextCreatedTime
																									.replace(
																											"T",
																											" "))
																					.equals(DateUtil
																							.parseDateTime(DateUtil
																									.formatDateTime(new Date())))) {
																				CreateRepeatSchNextData(list
																						.get(i));
																			} else {
																				CreateRepeatSchData(list
																						.get(i));
																			}
																		} else {
																			CreateRepeatSchData(list
																					.get(i));
																		}
																	} else {
																		if (!CreateRepeatSchDateData(list
																				.get(i)).repLastCreatedTime
																				.equals(list
																						.get(i).repdateone
																						.replace(
																								"T",
																								""))
																				&& !CreateRepeatSchDateData(list
																						.get(i)).repLastCreatedTime
																						.equals(list
																								.get(i).repdatetwo
																								.replace(
																										"T",
																										""))) {
																			if (DateUtil
																					.parseDateTime(
																							CreateRepeatSchDateData(list
																									.get(i)).repLastCreatedTime)
																					.before(DateUtil
																							.parseDateTime(list
																									.get(i).repInitialCreatedTime
																									.replace(
																											"T",
																											" ")))) {
																			} else {
																				CreateRepeatSchLastData(list
																						.get(i));
																			}
																		} else if (CreateRepeatSchDateData(list
																				.get(i)).repLastCreatedTime
																				.equals(list
																						.get(i).repdateone
																						.replace(
																								"T",
																								""))) {
																			if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 1) {

																			} else if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 2) {

																			}
																			if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 3) {
																				CreateRepeatSchEndLastData(list
																						.get(i)); // 脱钩时，生成下一条
																			} else if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 0) {
																				CreateRepeatSchLastData(list
																						.get(i)); // 脱钩时，生成下一条
																			}
																		} else if (CreateRepeatSchDateData(list
																				.get(i)).repLastCreatedTime
																				.equals(list
																						.get(i).repdatetwo
																						.replace(
																								"T",
																								""))) {// 时间与计算的时间进行比较
																			if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 1) {
																			} else if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 2) {
																			}
																			if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 3) {
																				CreateRepeatSchEndLastData(list
																						.get(i)); // 脱钩时，生成下一条
																			} else if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 0) {
																				CreateRepeatSchLastData(list
																						.get(i)); // 脱钩时，生成下一条
																			}
																		}
																		if (!CreateRepeatSchDateData(list
																				.get(i)).repNextCreatedTime
																				.equals(list
																						.get(i).repdateone
																						.replace(
																								"T",
																								""))
																				&& !CreateRepeatSchDateData(list
																						.get(i)).repNextCreatedTime
																						.equals(list
																								.get(i).repdatetwo
																								.replace(
																										"T",
																										""))) {
																			if (DateUtil
																					.parseDateTime(
																							CreateRepeatSchDateData(list
																									.get(i)).repNextCreatedTime)
																					.before(DateUtil
																							.parseDateTime(list
																									.get(i).repInitialCreatedTime
																									.replace(
																											"T",
																											" ")))) {
																			} else {

																				CreateRepeatSchNextData(list
																						.get(i));
																			}
																		} else if (CreateRepeatSchDateData(list
																				.get(i)).repNextCreatedTime
																				.equals(list
																						.get(i).repdateone
																						.replace(
																								"T",
																								""))) {
																			if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 1) {
																			} else if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 2) {
																			}
																			if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 3) {
																				CreateRepeatSchEndNextData(list
																						.get(i));

																			} else if (Integer
																					.parseInt(list
																							.get(i).repstateone) == 0) {
																				CreateRepeatSchNextData(list
																						.get(i));

																			}
																		} else if (CreateRepeatSchDateData(list
																				.get(i)).repNextCreatedTime
																				.equals(list
																						.get(i).repdatetwo
																						.replace(
																								"T",
																								""))) {// 时间与计算的时间进行比较
																			if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 1) {
																			} else if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 2) {
																			}
																			if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 3) {
																				CreateRepeatSchEndNextData(list
																						.get(i)); // 脱钩时，生成下一条
																			} else if (Integer
																					.parseInt(list
																							.get(i).repstatetwo) == 0) {
																				CreateRepeatSchNextData(list
																						.get(i)); // 脱钩时，生成下一条
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
											if (deleteID != null
													&& deleteID.size() > 0) {
												for (int i = 0; i < deleteID
														.size(); i++) {
													application
															.deleteChildSch(deleteID
																	.get(i)
																	+ "");
													application
															.deleteRepeatData(deleteID
																	.get(i)
																	.toString());
													application
															.deleteSch(deleteID
																	.get(i));
												}
											} else {

											}
											if ((list != null && list.size() > 0)
													|| (deleteID != null && deleteID
															.size() > 0)) {
												IsRepeat = 1;
												Intent intent = new Intent();
												intent.setAction(RepUPDATADATA);
												intent.putExtra("data",
														"success");
												sendBroadcast(intent);
											} else {
												Intent intent = new Intent();
												intent.setAction(RepUPDATADATA);
												intent.putExtra("data", "fail");
												sendBroadcast(intent);
											}
										} else if (downLoadBean.status == 1) {
											sharedPrefUtil.putString(
													getApplication(),
													ShareFile.USERFILE,
													ShareFile.DOWNREPTIME,
													downLoadBean.message
															.replace("T", " "));
											Intent intent = new Intent();
											intent.setAction(RepUPDATADATA);
											intent.putExtra("data", "fail");
											sendBroadcast(intent);
										} else {
											Intent intent = new Intent();
											intent.setAction(RepUPDATADATA);
											intent.putExtra("data", "fail");
											sendBroadcast(intent);
										}
									} catch (Exception e) {
										e.printStackTrace();
										Intent intent = new Intent();
										intent.setAction(RepUPDATADATA);
										intent.putExtra("data", "fail");
										sendBroadcast(intent);
										DownLoadSchAsync(downschpath);
										// stopSelf();
									}
									DownLoadSchAsync(downschpath);
								}
							}).start();

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						DownLoadSchAsync(downschpath);
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);
					}
				});
		request.setTag("download");
		request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	public static void CreateRepeatSchData(RepeatUpAndDownBean list) {
		App app = App.getDBcApplication();
		RepeatBean bean;
		// app.deleteChildSch(list.repId);
		int recommendId = 0;
		if (!"".equals(StringUtils
				.getIsStringEqulesNull(list.recommendedUserId))) {
			recommendId = Integer.parseInt(list.recommendedUserId);
		} else {
			recommendId = 0;
		}
		int coclor = 0;
		if ("".equals(StringUtils.getIsStringEqulesNull(list.repColorType))) {
			coclor = 0;
		} else {
			coclor = Integer.parseInt(list.repColorType);
		}
		if ("1".equals(list.repType)) {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 1, "", "");
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("2".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 2, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						2,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}

			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("3".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 3, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						3,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("4".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "01-01",
						"0");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "0");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("6".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "正月初一",
						"1");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "1");
			}

			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 5, "", "");
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		}

	}

	public static void CreateRepeatSchNextData(RepeatUpAndDownBean list) {
		App app = App.getDBcApplication();
		RepeatBean bean;
		int recommendId = 0;
		if (!"".equals(StringUtils
				.getIsStringEqulesNull(list.recommendedUserId))) {
			recommendId = Integer.parseInt(list.recommendedUserId);
		} else {
			recommendId = 0;
		}
		int coclor = 0;
		if ("".equals(StringUtils.getIsStringEqulesNull(list.repColorType))) {
			coclor = 0;
		} else {
			coclor = Integer.parseInt(list.repColorType);
		}
		// app.deleteChildSch(list.repId);
		if ("1".equals(list.repType)) {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 1, "", "");
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("2".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 2, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						2,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}

			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("3".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 3, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						3,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("4".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "01-01",
						"0");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "0");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("6".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "正月初一",
						"1");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "1");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 5, "", "");
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		}

	}

	public static void CreateRepeatSchLastData(RepeatUpAndDownBean list) {
		App app = App.getDBcApplication();
		RepeatBean bean;
		int recommendId = 0;
		if (!"".equals(StringUtils
				.getIsStringEqulesNull(list.recommendedUserId))) {
			recommendId = Integer.parseInt(list.recommendedUserId);
		} else {
			recommendId = 0;
		}
		int coclor = 0;
		if ("".equals(StringUtils.getIsStringEqulesNull(list.repColorType))) {
			coclor = 0;
		} else {
			coclor = Integer.parseInt(list.repColorType);
		}
		// app.deleteChildSch(list.repId);
		if ("1".equals(list.repType)) {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 1, "", "");
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("2".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 2, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						2,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("3".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 3, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						3,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}

			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("4".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "01-01",
						"0");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "0");
			}
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("6".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "正月初一",
						"1");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "1");
			}

			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 5, "", "");
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 0,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		}

	}

	/**
	 * 标记为结束的上一条
	 */
	public static void CreateRepeatSchEndLastData(RepeatUpAndDownBean list) {
		System.out.println("标记上一条记事:" + list);
		App app = App.getDBcApplication();
		RepeatBean bean;
		int recommendId = 0;
		if (!"".equals(StringUtils
				.getIsStringEqulesNull(list.recommendedUserId))) {
			recommendId = Integer.parseInt(list.recommendedUserId);
		} else {
			recommendId = 0;
		}
		int coclor = 0;
		if ("".equals(StringUtils.getIsStringEqulesNull(list.repColorType))) {
			coclor = 0;
		} else {
			coclor = Integer.parseInt(list.repColorType);
		}
		// app.deleteChildSch(list.repId);
		if ("1".equals(list.repType)) {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 1, "", "");
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("2".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 2, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						2,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}

			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("3".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 3, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						3,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("4".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "01-01",
						"0");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "0");
			}
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("6".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "正月初一",
						"1");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "1");
			}
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 5, "", "");
			app.insertScheduleData(list.repContent,
					bean.repLastCreatedTime.substring(0, 10),
					bean.repLastCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repLastCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repLastCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		}

	}

	/**
	 * 状态为3结束生成下一条
	 * 
	 * @param list
	 */
	public static void CreateRepeatSchEndNextData(RepeatUpAndDownBean list) {
		App app = App.getDBcApplication();
		RepeatBean bean;
		int recommendId = 0;
		if (!"".equals(StringUtils
				.getIsStringEqulesNull(list.recommendedUserId))) {
			recommendId = Integer.parseInt(list.recommendedUserId);
		} else {
			recommendId = 0;
		}
		int coclor = 0;
		if ("".equals(StringUtils.getIsStringEqulesNull(list.repColorType))) {
			coclor = 0;
		} else {
			coclor = Integer.parseInt(list.repColorType);
		}
		// app.deleteChildSch(list.repId);
		if ("1".equals(list.repType)) {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 1, "", "");
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("2".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 2, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						2,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("3".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 3, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						3,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}

			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("4".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "01-01",
						"0");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "0");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else if ("6".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "正月初一",
						"1");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "1");
			}
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		} else {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 5, "", "");
			app.insertScheduleData(list.repContent,
					bean.repNextCreatedTime.substring(0, 10),
					bean.repNextCreatedTime.substring(11, 16),
					Integer.parseInt(list.repIsAlarm),
					Integer.parseInt(list.repBeforeTime),
					Integer.parseInt(list.repDisplayTime), 0,
					Integer.parseInt(list.repIsImportant), coclor, 1,
					bean.repNextCreatedTime, "", 0, "", "",
					Integer.parseInt(list.repId), bean.repNextCreatedTime,
					DateUtil.formatDateTimeSs(new Date()), 0,
					Integer.parseInt(list.repOpenState), 1, list.repRingDesc,
					list.repRingCode, "", 0, 0, list.aType, list.webUrl,
					list.imgPath, 0, 0, recommendId);
		}

	}

	/**
	 * 计算生成子记事上一条和下一条的时间
	 */
	public static RepeatBean CreateRepeatSchDateData(RepeatUpAndDownBean list) {
		// App app = App.getDBcApplication();
		RepeatBean bean;
		// app.deleteChildSch(list.repId);
		if ("1".equals(list.repType)) {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 1, "", "");
		} else if ("2".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 2, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						2,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}
		} else if ("3".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 3, "1", "");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						3,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "");
			}
		} else if ("4".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "01-01",
						"0");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "0");
			}
		} else if ("6".equals(list.repType)) {
			if ("".equals(StringUtils
					.getIsStringEqulesNull(list.repTypeParameter))) {
				bean = RepeatDateUtils.saveCalendar(list.repTime, 4, "正月初一",
						"1");
			} else {
				bean = RepeatDateUtils.saveCalendar(
						list.repTime,
						4,
						list.repTypeParameter.replace("[", "").replace("]", "")
								.replace("\n\"", "").replace("\n", "")
								.replace("\"", ""), "1");
			}
		} else {
			bean = RepeatDateUtils.saveCalendar(list.repTime, 5, "", "");
		}
		return bean;
	}
}
