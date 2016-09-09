package com.mission.schedule.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.activity.NewRiChengActivity;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.UpdateNewFriendMessageBackBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.SharedPrefUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

public class NewFriendDataUpLoadService extends Service {

	public static final String FRIENDDATA = "com.mission.schedule.service.NewFriendDataUpLoadService";
	List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
	String json = "";
	App app = App.getDBcApplication();
	SharedPrefUtil sharedPrefUtil = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		sharedPrefUtil = new SharedPrefUtil(getApplication(),
				ShareFile.USERFILE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				UpdateLoadData();
			}
		}).start();
	}

	private void UpdateLoadData() {
		addList.clear();
		updateList.clear();
		deleteList.clear();
		JSONArray jsonarray1 = new JSONArray();
		JSONArray jsonarray2 = new JSONArray();
		JSONArray jsonarray3 = new JSONArray();
		JSONObject jsonobject1 = new JSONObject();
		try {
			addList = app.queryAllLocalFriendsData(-1, 0);
			updateList = app.queryAllLocalFriendsData(-2, 0);
			deleteList = app.queryAllLocalFriendsData(-3, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (NetUtil.getConnectState(getApplication()) != NetWorkState.NONE) {
			try {
				if (addList != null && addList.size() > 0) {
					for (int i = 0; i < addList.size(); i++) {
						JSONObject jsonobject = new JSONObject();
						jsonobject.put("id",
								addList.get(i).get(CLNFMessage.nfmId));
						jsonobject.put("uid",
								addList.get(i).get(CLNFMessage.nfmSendId));
						jsonobject.put("cpId",
								addList.get(i).get(CLNFMessage.nfmGetId));
						jsonobject.put("message",
								addList.get(i).get(CLNFMessage.nfmContent));
						jsonobject.put("status",
								addList.get(i).get(CLNFMessage.nfmStatus));
						jsonobject.put("cisAlarm",
								addList.get(i).get(CLNFMessage.nfmIsAlarm));
						jsonobject.put("cdate",
								addList.get(i).get(CLNFMessage.nfmDate));
						jsonobject.put("ctime",
								addList.get(i).get(CLNFMessage.nfmTime));
						jsonobject.put("cbeforTime",
								addList.get(i).get(CLNFMessage.nfmBeforeTime));
						jsonobject.put("CAlarmsound",
								addList.get(i).get(CLNFMessage.nfmRingCode));
						jsonobject.put("CAlarmsoundDesc",
								addList.get(i).get(CLNFMessage.nfmRingDesc));
						jsonobject.put("repType",
								addList.get(i).get(CLNFMessage.nfmType));
						jsonobject.put("repTypeParameter",
								addList.get(i).get(CLNFMessage.nfmParameter)
										.replace("\n\"", "").replace("\n", "")
										.replace("\"", "").toString());
						jsonobject.put("CPostpone",
								addList.get(i).get(CLNFMessage.nfmPostpone));
						jsonobject.put("CTags",
								addList.get(i).get(CLNFMessage.nfmTags));
						jsonobject.put("COpenstate",
								addList.get(i).get(CLNFMessage.nfmOpenState));
						jsonobject.put("CLightAppId", "0");
						jsonobject.put("CRecommendName",
								addList.get(i).get(CLNFMessage.nfmSendName));
						jsonobject.put("repColorType",
								addList.get(i).get(CLNFMessage.nfmColorType));
						jsonobject.put("repDisplayTime",
								addList.get(i).get(CLNFMessage.nfmDisplayTime));
						jsonobject.put("endState",
								addList.get(i).get(CLNFMessage.nfmIsEnd));
						jsonobject.put("schIsImportant", "0");
						jsonobject.put("calendaId",
								addList.get(i).get(CLNFMessage.nfmCalendarId));
						jsonobject.put("atype",
								addList.get(i).get(CLNFMessage.nfmAType));
						jsonobject.put("webUrl",
								addList.get(i).get(CLNFMessage.nfmWebURL));
						jsonobject.put("imgPath",
								addList.get(i).get(CLNFMessage.nfmImagePath));
						jsonobject.put("repInStable",
								addList.get(i).get(CLNFMessage.nfmInSTable));
						jsonobject.put("downstate",
								addList.get(i).get(CLNFMessage.nfmDownState));
						jsonobject.put("remark",
								addList.get(i).get(CLNFMessage.nfmRemark));
						jsonobject.put("poststate",
								addList.get(i).get(CLNFMessage.nfmPostState));
						jsonobject.put("repnextcreatedtime", addList.get(i)
								.get(CLNFMessage.nfmNextCreatedTime));
						jsonobject.put("replastcreatedtime", addList.get(i)
								.get(CLNFMessage.nfmLastCreatedTime));
						jsonobject.put("repstartdate",
								addList.get(i).get(CLNFMessage.nfmStartDate));
						jsonobject.put("repinitialcreatedtime", addList.get(i)
								.get(CLNFMessage.nfmInitialCreatedTime));
						jsonobject.put("cType",
								addList.get(i).get(CLNFMessage.nfmSourceType));
						jsonobject.put("cTypeDesc",
								addList.get(i).get(CLNFMessage.nfmSourceDesc));
						jsonobject.put(
								"cTypeSpare",
								addList.get(i).get(
										CLNFMessage.nfmSourceDescSpare));
						jsonobject.put("pId",
								addList.get(i).get(CLNFMessage.nfmPId));
						jsonobject.put("repstatetwo",
								addList.get(i).get(CLNFMessage.nfmSubState));
						jsonobject.put("repdatetwo",
								addList.get(i).get(CLNFMessage.nfmSubDate));
						jsonobject.put("repState",
								addList.get(i).get(CLNFMessage.nfmCState));
						jsonobject.put("repCalendaState",
								addList.get(i).get(CLNFMessage.nfmSubEnd));
						jsonobject.put("repCalendaTime",
								addList.get(i).get(CLNFMessage.nfmSubEndDate));
						jsonobject.put("repIsPuase",
								addList.get(i).get(CLNFMessage.nfmIsPuase));
						jsonobject.put("parReamrk", addList.get(i).get(CLNFMessage.parReamrk));
						jsonarray1.put(jsonobject);
					}
					jsonobject1.put("addData", jsonarray1);
				} else {
					jsonobject1.put("addData", jsonarray1);
				}
				if (updateList != null && updateList.size() > 0) {
					for (int i = 0; i < updateList.size(); i++) {
						JSONObject jsonobject = new JSONObject();
						jsonobject.put("id",
								updateList.get(i).get(CLNFMessage.nfmId));
						jsonobject.put("uid",
								updateList.get(i).get(CLNFMessage.nfmSendId));
						jsonobject.put("cpId",
								updateList.get(i).get(CLNFMessage.nfmGetId));
						jsonobject.put("message",
								updateList.get(i).get(CLNFMessage.nfmContent));
						jsonobject.put("status",
								updateList.get(i).get(CLNFMessage.nfmStatus));
						jsonobject.put("cisAlarm",
								updateList.get(i).get(CLNFMessage.nfmIsAlarm));
						jsonobject.put("cdate",
								updateList.get(i).get(CLNFMessage.nfmDate));
						jsonobject.put("ctime",
								updateList.get(i).get(CLNFMessage.nfmTime));
						jsonobject.put("cbeforTime",
								updateList.get(i)
										.get(CLNFMessage.nfmBeforeTime));
						jsonobject.put("CAlarmsound",
								updateList.get(i).get(CLNFMessage.nfmRingCode));
						jsonobject.put("CAlarmsoundDesc", updateList.get(i)
								.get(CLNFMessage.nfmRingDesc));
						jsonobject.put("repType",
								updateList.get(i).get(CLNFMessage.nfmType));
						jsonobject.put("repTypeParameter",
								updateList.get(i).get(CLNFMessage.nfmParameter)
										.replace("\n\"", "").replace("\n", "")
										.replace("\"", "").toString());
						jsonobject.put("CPostpone",
								updateList.get(i).get(CLNFMessage.nfmPostpone));
						jsonobject.put("CTags",
								updateList.get(i).get(CLNFMessage.nfmTags));
						jsonobject
								.put("COpenstate",
										updateList.get(i).get(
												CLNFMessage.nfmOpenState));
						jsonobject.put("CLightAppId", "0");
						jsonobject.put("CRecommendName",
								updateList.get(i).get(CLNFMessage.nfmSendName));
						jsonobject
								.put("repColorType",
										updateList.get(i).get(
												CLNFMessage.nfmColorType));
						jsonobject.put(
								"repDisplayTime",
								updateList.get(i).get(
										CLNFMessage.nfmDisplayTime));
						jsonobject.put("endState",
								updateList.get(i).get(CLNFMessage.nfmIsEnd));
						jsonobject.put("schIsImportant", "0");
						jsonobject.put("calendaId",
								updateList.get(i)
										.get(CLNFMessage.nfmCalendarId));
						jsonobject.put("atype",
								updateList.get(i).get(CLNFMessage.nfmAType));
						jsonobject.put("webUrl",
								updateList.get(i).get(CLNFMessage.nfmWebURL));
						jsonobject
								.put("imgPath",
										updateList.get(i).get(
												CLNFMessage.nfmImagePath));
						jsonobject.put("repInStable",
								updateList.get(i).get(CLNFMessage.nfmInSTable));
						jsonobject
								.put("downstate",
										updateList.get(i).get(
												CLNFMessage.nfmDownState));
						jsonobject.put("remark",
								updateList.get(i).get(CLNFMessage.nfmRemark));
						jsonobject
								.put("poststate",
										updateList.get(i).get(
												CLNFMessage.nfmPostState));
						jsonobject.put("repnextcreatedtime", updateList.get(i)
								.get(CLNFMessage.nfmNextCreatedTime));
						jsonobject.put("replastcreatedtime", updateList.get(i)
								.get(CLNFMessage.nfmLastCreatedTime));
						jsonobject
								.put("repstartdate",
										updateList.get(i).get(
												CLNFMessage.nfmStartDate));
						jsonobject.put(
								"repinitialcreatedtime",
								updateList.get(i).get(
										CLNFMessage.nfmInitialCreatedTime));
						jsonobject.put("cType",
								updateList.get(i)
										.get(CLNFMessage.nfmSourceType));
						jsonobject.put("cTypeDesc",
								updateList.get(i)
										.get(CLNFMessage.nfmSourceDesc));
						jsonobject.put(
								"cTypeSpare",
								updateList.get(i).get(
										CLNFMessage.nfmSourceDescSpare));
						jsonobject.put("pId",
								updateList.get(i).get(CLNFMessage.nfmPId));
						jsonobject.put("repstatetwo",
								updateList.get(i).get(CLNFMessage.nfmSubState));
						jsonobject.put("repdatetwo",
								updateList.get(i).get(CLNFMessage.nfmSubDate));
						jsonobject.put("repState",
								updateList.get(i).get(CLNFMessage.nfmCState));
						jsonobject.put("repCalendaState", updateList.get(i)
								.get(CLNFMessage.nfmSubEnd));
						jsonobject.put("repCalendaTime",
								updateList.get(i)
										.get(CLNFMessage.nfmSubEndDate));
						jsonobject.put("repIsPuase",
								updateList.get(i).get(CLNFMessage.nfmIsPuase));
						jsonobject.put("parReamrk", updateList.get(i).get(CLNFMessage.parReamrk));
						jsonarray2.put(jsonobject);
					}
					jsonobject1.put("updateData", jsonarray2);
				} else {
					jsonobject1.put("updateData", jsonarray2);
				}
				if (deleteList != null && deleteList.size() > 0) {
					for (int i = 0; i < deleteList.size(); i++) {
						JSONObject jsonobject = new JSONObject();
						jsonobject.put("id",
								deleteList.get(i).get(CLNFMessage.nfmId));
						jsonobject.put("uid",
								deleteList.get(i).get(CLNFMessage.nfmSendId));
						jsonobject.put("cpId",
								deleteList.get(i).get(CLNFMessage.nfmGetId));
						jsonobject.put("message",
								deleteList.get(i).get(CLNFMessage.nfmContent));
						jsonobject.put("status",
								deleteList.get(i).get(CLNFMessage.nfmStatus));
						jsonobject.put("cisAlarm",
								deleteList.get(i).get(CLNFMessage.nfmIsAlarm));
						jsonobject.put("cdate",
								deleteList.get(i).get(CLNFMessage.nfmDate));
						jsonobject.put("ctime",
								deleteList.get(i).get(CLNFMessage.nfmTime));
						jsonobject.put("cbeforTime",
								deleteList.get(i)
										.get(CLNFMessage.nfmBeforeTime));
						jsonobject.put("CAlarmsound",
								deleteList.get(i).get(CLNFMessage.nfmRingCode));
						jsonobject.put("CAlarmsoundDesc", deleteList.get(i)
								.get(CLNFMessage.nfmRingDesc));
						jsonobject.put("repType",
								deleteList.get(i).get(CLNFMessage.nfmType));
						jsonobject.put("repTypeParameter",
								deleteList.get(i).get(CLNFMessage.nfmParameter)
										.replace("\n\"", "").replace("\n", "")
										.replace("\"", "").toString());
						jsonobject.put("CPostpone",
								deleteList.get(i).get(CLNFMessage.nfmPostpone));
						jsonobject.put("CTags",
								deleteList.get(i).get(CLNFMessage.nfmTags));
						jsonobject
								.put("COpenstate",
										deleteList.get(i).get(
												CLNFMessage.nfmOpenState));
						jsonobject.put("CLightAppId", "0");
						jsonobject.put("CRecommendName",
								deleteList.get(i).get(CLNFMessage.nfmSendName));
						jsonobject
								.put("repColorType",
										deleteList.get(i).get(
												CLNFMessage.nfmColorType));
						jsonobject.put(
								"repDisplayTime",
								deleteList.get(i).get(
										CLNFMessage.nfmDisplayTime));
						jsonobject.put("endState",
								deleteList.get(i).get(CLNFMessage.nfmIsEnd));
						jsonobject.put("schIsImportant", "0");
						jsonobject.put("calendaId",
								deleteList.get(i)
										.get(CLNFMessage.nfmCalendarId));
						jsonobject.put("atype",
								deleteList.get(i).get(CLNFMessage.nfmAType));
						jsonobject.put("webUrl",
								deleteList.get(i).get(CLNFMessage.nfmWebURL));
						jsonobject
								.put("imgPath",
										deleteList.get(i).get(
												CLNFMessage.nfmImagePath));
						jsonobject.put("repInStable",
								deleteList.get(i).get(CLNFMessage.nfmInSTable));
						jsonobject
								.put("downstate",
										deleteList.get(i).get(
												CLNFMessage.nfmDownState));
						jsonobject.put("remark",
								deleteList.get(i).get(CLNFMessage.nfmRemark));
						jsonobject
								.put("poststate",
										deleteList.get(i).get(
												CLNFMessage.nfmPostState));
						jsonobject.put("repnextcreatedtime", deleteList.get(i)
								.get(CLNFMessage.nfmNextCreatedTime));
						jsonobject.put("replastcreatedtime", deleteList.get(i)
								.get(CLNFMessage.nfmLastCreatedTime));
						jsonobject
								.put("repstartdate",
										deleteList.get(i).get(
												CLNFMessage.nfmStartDate));
						jsonobject.put(
								"repinitialcreatedtime",
								deleteList.get(i).get(
										CLNFMessage.nfmInitialCreatedTime));
						jsonobject.put("cType",
								deleteList.get(i)
										.get(CLNFMessage.nfmSourceType));
						jsonobject.put("cTypeDesc",
								deleteList.get(i)
										.get(CLNFMessage.nfmSourceDesc));
						jsonobject.put(
								"cTypeSpare",
								deleteList.get(i).get(
										CLNFMessage.nfmSourceDescSpare));
						jsonobject.put("pId",
								deleteList.get(i).get(CLNFMessage.nfmPId));
						jsonobject.put("repstatetwo",
								deleteList.get(i).get(CLNFMessage.nfmSubState));
						jsonobject.put("repdatetwo",
								deleteList.get(i).get(CLNFMessage.nfmSubDate));
						jsonobject.put("repState",
								deleteList.get(i).get(CLNFMessage.nfmCState));
						jsonobject.put("repCalendaState", deleteList.get(i)
								.get(CLNFMessage.nfmSubEnd));
						jsonobject.put("repCalendaTime",
								deleteList.get(i)
										.get(CLNFMessage.nfmSubEndDate));
						jsonobject.put("repIsPuase",
								deleteList.get(i).get(CLNFMessage.nfmIsPuase));
						jsonobject.put("parReamrk", deleteList.get(i).get(CLNFMessage.parReamrk));
						jsonarray3.put(jsonobject);
					}
					jsonobject1.put("deleData", jsonarray3);
				} else {
					jsonobject1.put("deleData", jsonarray3);
				}
				json = jsonobject1.toString();
				String path = URLConstants.新版好友同步;
				UpdateLoadAsync(path, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void UpdateLoadAsync(String path, final String json) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							List<UpdateNewFriendMessageBean> list = null;
							try {
								UpdateNewFriendMessageBackBean backBean = gson
										.fromJson(
												result,
												UpdateNewFriendMessageBackBean.class);
								sharedPrefUtil.putString(getApplication(),
										ShareFile.USERFILE,
										ShareFile.FRIENDUPDATETIME,
										DateUtil.formatDateTimeSs(new Date()));
								if (backBean.status == 0) {
									list = backBean.list;
									if(NewRiChengActivity.beans!=null&&NewRiChengActivity.beans.size()>0){
										NewRiChengActivity.beans.clear();
									}
									NewRiChengActivity.beans = backBean.list;
									if (list != null && list.size() > 0) {
										for (int i = 0; i < list.size(); i++) {
											if (list.get(i).state == 0) {
												if (list.get(i).dataState == 1
														|| list.get(i).dataState == 2) {
													app.updateNewFriendsData(
															list.get(i).oldId,
															list.get(i).id,
															Integer.parseInt(list
																	.get(i).calendId),
															0);
													app.updateNewFriendsChildData(
															list.get(i).oldId,
															list.get(i).id,
															Integer.parseInt(list
																	.get(i).calendId));
												} else if (list.get(i).dataState == 3) {
													app.deleteNewFriendsData(list
															.get(i).id);
													app.deleteNewFriendsChildData(list
															.get(i).id);
												}
											} else {
												app.updateNewFriendsData(
														list.get(i).oldId,
														list.get(i).id,
														Integer.parseInt(list
																.get(i).calendId),
														list.get(i).dataState);
												app.updateNewFriendsChildData(
														list.get(i).oldId,
														list.get(i).id,
														Integer.parseInt(list
																.get(i).calendId));
											}
										}
									}
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
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("data", json);
				return map;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
		stopSelf();
	}
}