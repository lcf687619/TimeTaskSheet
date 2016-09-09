package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.adapter.FriendsSelectAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.FriendsBackBean;
import com.mission.schedule.bean.FriendsBean;
import com.mission.schedule.bean.NewFocusShareBean;
import com.mission.schedule.bean.NewFriendBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBackBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;
import com.mission.schedule.utils.StringUtils;

public class NewFriendRiChengZhuanFaActivity extends BaseActivity implements
		OnClickListener {
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.friends_lv)
	private ListView friends_lv;

	Context context;
	SharedPrefUtil prefUtil = null;
	String userId;
	String path;
	FriendsSelectAdapter adapter = null;
	List<FriendsBean> beansList = new ArrayList<FriendsBean>();
	FriendsBean friendsBean = null;
	NewFriendBean bean = null;

	List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
	String json;

	App app;
	String myName;
	int ID;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_friendsselect);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		userId = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		myName = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		bean = (NewFriendBean) getIntent().getSerializableExtra("bean");
		LoadData();
	}

	private void LoadData() {
		path = URLConstants.好友列表查询 + "?uId=" + Integer.parseInt(userId);
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			FriendsQueryAsync(path);
		} else {
			Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void item() {
		friends_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				friendsBean = (FriendsBean) friends_lv.getAdapter().getItem(
						position);
				app.insertMessageSendData(Integer.parseInt(userId),
						friendsBean.fId, bean.nfmCalendarId, bean.nfmOpenState,
						1, bean.nfmIsAlarm, bean.nfmPostpone,
						bean.nfmColorType, bean.nfmDisplayTime,
						bean.nfmBeforeTime, bean.nfmSourceType, bean.nfmType,
						bean.nfmAType, bean.nfmInSTable, 0, 0, 0,
						bean.nfmParameter, bean.nfmContent, bean.nfmDate,
						bean.nfmTime, bean.nfmSourceDesc,
						bean.nfmSourceDescSpare, bean.nfmTags,
						bean.nfmRingDesc, bean.nfmRingCode, bean.nfmStartDate,
						bean.nfmInitialCreatedTime, bean.nfmLastCreatedTime,
						bean.nfmNextCreatedTime, bean.nfmWebURL,
						bean.nfmImagePath, myName, bean.nfmRemark, 1,
						bean.nfmPId, bean.nfmSubState, bean.nfmSubDate,
						bean.nfmCState, bean.nfmSubEnd, bean.nfmSubEndDate,
						bean.nfmIsPuase, "",
						DateUtil.formatDateTimeSs(new Date()));
				ID = app.nfmId;
				UpdateLoadData();
			}
		});

	}

	@Override
	protected void setAdapter() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	private void FriendsQueryAsync(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							FriendsBackBean backBean = gson.fromJson(result,
									FriendsBackBean.class);
							if (backBean.status == 0) {
								if (adapter == null) {
									beansList = backBean.tbUserFriendsApp;
									if (beansList != null
											&& beansList.size() > 0) {
										adapter = new FriendsSelectAdapter(
												context, beansList,R.layout.adapter_friendsselect);
										friends_lv.setAdapter(adapter);
										item();
									} else {
										Toast.makeText(context,
												"没有好友，赶紧添加几个吧！",
												Toast.LENGTH_SHORT).show();
									}
								} else {
									beansList.clear();
									beansList.addAll(backBean.tbUserFriendsApp);
									adapter.notifyDataSetChanged();
									item();
								}
							} else {
								return;
							}
						} else {
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	/******************************** 发送消息进行同步 ***************************************************/
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
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
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
		} else {
			app.deleteNewFriendLocalData(ID);
			alertFailDialog();
		}
	}

	private void UpdateLoadAsync(String path, final String json) {
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在发送...");
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							List<UpdateNewFriendMessageBean> list = null;
							try {
								UpdateNewFriendMessageBackBean backBean = gson
										.fromJson(
												result,
												UpdateNewFriendMessageBackBean.class);
								if (backBean.status == 0) {
									list = backBean.list;
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
												} else if (list.get(i).dataState == 3) {
													app.deleteNewFriendsData(list
															.get(i).id);
												}
											} else {
												app.updateNewFriendsData(
														list.get(i).oldId,
														list.get(i).id,
														Integer.parseInt(list
																.get(i).calendId),
														list.get(i).dataState);
											}
										}
									}
									alertDialog();
								} else {
									app.deleteNewFriendLocalData(ID);
									alertFailDialog();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
							app.deleteNewFriendLocalData(ID);
							alertFailDialog();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
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

	private void alertDialog() {
		final AlertDialog builder = new AlertDialog.Builder(context).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);// 设置生效
		window.setGravity(Gravity.CENTER);
		window.setContentView(R.layout.dialog_alert_ok);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		delete_tv.setText("转发成功！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
				NewFriendRiChengZhuanFaActivity.this.finish();
			}
		});

	}

	private void alertFailDialog() {
		final AlertDialog builder = new AlertDialog.Builder(context).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);// 设置生效
		window.setGravity(Gravity.CENTER);
		window.setContentView(R.layout.dialog_alert_ok);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		delete_tv.setText("转发失败！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}

	public static class NewFocusShareRiChengZhuanFaActivity extends BaseActivity implements
            OnClickListener {

        @ViewResId(id = R.id.top_ll_back)
        private LinearLayout top_ll_back;
        @ViewResId(id = R.id.friends_lv)
        private ListView friends_lv;

        Context context;
        SharedPrefUtil prefUtil = null;
        String userId;
        String path;
        FriendsSelectAdapter adapter = null;
        List<FriendsBean> beansList = new ArrayList<FriendsBean>();
        FriendsBean friendsBean = null;
        NewFocusShareBean bean = null;
        String cAlarmSound;
        String cAlarmSoundDesc;
        String userName;

        App app;
        List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
        List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
        List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
        String json;
        int ID;
        boolean fag = false;

        @Override
        protected void setListener() {
            top_ll_back.setOnClickListener(this);
        }

        @Override
        protected void setContentView() {
            setContentView(R.layout.activity_friendsselect);
        }

        @Override
        protected void init(Bundle savedInstanceState) {
            context = this;
            prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
            app = App.getDBcApplication();
            userId = prefUtil.getString(context, ShareFile.USERFILE,
                    ShareFile.USERID, "");
            userName = prefUtil.getString(context, ShareFile.USERFILE,
                    ShareFile.USERNAME, "");
            cAlarmSoundDesc = prefUtil.getString(context, ShareFile.USERFILE,
                    ShareFile.MUSICDESC, "完成任务");
            cAlarmSound = prefUtil.getString(context, ShareFile.USERFILE,
                    ShareFile.MUSICCODE, "g_88");
            bean = (NewFocusShareBean) getIntent().getSerializableExtra("bean");
            if (!"".equals(StringUtils.getIsStringEqulesNull(bean.fstRingDesc))) {
                cAlarmSoundDesc = bean.fstRingDesc;
            }
            if (!"".equals(StringUtils.getIsStringEqulesNull(bean.fstRingCode))) {
                cAlarmSound = bean.fstRingCode;
            }
            LoadData();
        }

        private void LoadData() {
            path = URLConstants.好友列表查询 + "?uId=" + Integer.parseInt(userId);
            if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                FriendsQueryAsync(path);
            } else {
                Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        private void item() {
            friends_lv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    friendsBean = (FriendsBean) friends_lv.getAdapter().getItem(
                            position);
                    fag = app.insertMessageSendData(Integer.parseInt(userId),
                            friendsBean.fId, 0, bean.fstOpenState, bean.fstType,
                            bean.fstIsAlarm, bean.fstIsPostpone, bean.fstColorType,
                            bean.fstDisplayTime, bean.fstBeforeTime,
                            bean.fstSourceType, bean.fstRepType, bean.fstAType,
                            bean.fstRepInStable, bean.fstIsEnd, 0, 0,
                            bean.fstParameter, bean.fstContent, bean.fstDate,
                            bean.fstTime, bean.fstSourceDesc,
                            bean.fstSourceDescSpare, bean.fstTags, cAlarmSoundDesc,
                            cAlarmSound, bean.fstRepStartDate,
                            bean.fstRepInitialCreatedTime,
                            bean.fstRepLastCreatedTime, bean.fstRpNextCreatedTime,
                            bean.fstWebUR, bean.fstImagePath, userName, "", 1, 0,
                            0, "", 0, 0, "", bean.fstIsPuase, bean.fstParReamrk,
                            DateUtil.formatDateTimeSs(new Date()));
                    ID = app.nfmId;
                    if(bean.fstType==1&&fag){
                        fag = app.insertMessageSendData(Integer.parseInt(userId),
                                friendsBean.fId, 0, bean.fstOpenState, 0,
                                bean.fstIsAlarm, bean.fstIsPostpone, bean.fstColorType,
                                bean.fstDisplayTime, bean.fstBeforeTime,
                                bean.fstSourceType, bean.fstRepType, bean.fstAType,
                                bean.fstRepInStable, bean.fstIsEnd, 0, 0,
                                bean.fstParameter, bean.fstContent, bean.fstDate,
                                bean.fstTime, bean.fstSourceDesc,
                                bean.fstSourceDescSpare, bean.fstTags, cAlarmSoundDesc,
                                cAlarmSound, bean.fstRepStartDate,
                                bean.fstRepInitialCreatedTime,
                                bean.fstRepLastCreatedTime, bean.fstRpNextCreatedTime,
                                bean.fstWebUR, bean.fstImagePath, userName, "", 0, ID,
                                0, "", 0, 0, "", bean.fstIsPuase, bean.fstParReamrk,
                                DateUtil.formatDateTimeSs(new Date()));
                    }
                    UpdateLoadData();
                }
            });

        }

        @Override
        protected void setAdapter() {
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.top_ll_back:
                this.finish();
                break;

            default:
                break;
            }
        }

        private void FriendsQueryAsync(String path) {
            StringRequest request = new StringRequest(Method.GET, path,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String result) {
                            if (!TextUtils.isEmpty(result)) {
                                Gson gson = new Gson();
                                FriendsBackBean backBean = gson.fromJson(result,
                                        FriendsBackBean.class);
                                if (backBean.status == 0) {
                                    if (adapter == null) {
                                        beansList = backBean.tbUserFriendsApp;
                                        if (beansList != null
                                                && beansList.size() > 0) {
                                            adapter = new FriendsSelectAdapter(
                                                    context, beansList,
                                                    R.layout.adapter_friendsselect);
                                            friends_lv.setAdapter(adapter);
                                            item();
                                        } else {
                                            Toast.makeText(context,
                                                    "没有好友，赶紧添加几个吧！",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        beansList.clear();
                                        beansList.addAll(backBean.tbUserFriendsApp);
                                        adapter.notifyDataSetChanged();
                                        item();
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                        }
                    });
            request.setTag("down");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
            App.getHttpQueues().add(request);
        }

        /******************************** 发送消息进行同步 ***************************************************/
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
            if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
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
                            jsonobject.put("parReamrk",
                                    addList.get(i).get(CLNFMessage.parReamrk));
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
                            jsonobject.put("parReamrk",
                                    updateList.get(i).get(CLNFMessage.parReamrk));
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
                            jsonobject.put("parReamrk",
                                    deleteList.get(i).get(CLNFMessage.parReamrk));
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
            } else {
                app.deleteNewFriendLocalData(ID);
                alertFailDialog();
            }
        }

        private void UpdateLoadAsync(String path, final String json) {
            final ProgressUtil progressUtil = new ProgressUtil();
            progressUtil.ShowProgress(context, true, true, "正在发送...");
            StringRequest request = new StringRequest(Method.POST, path,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String result) {
                            progressUtil.dismiss();
                            if (!TextUtils.isEmpty(result)) {
                                Gson gson = new Gson();
                                List<UpdateNewFriendMessageBean> list = null;
                                try {
                                    UpdateNewFriendMessageBackBean backBean = gson
                                            .fromJson(
                                                    result,
                                                    UpdateNewFriendMessageBackBean.class);
                                    if (backBean.status == 0) {
                                        list = backBean.list;
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
                                                    } else if (list.get(i).dataState == 3) {
                                                        app.deleteNewFriendsData(list
                                                                .get(i).id);
                                                    }
                                                } else {
                                                    app.updateNewFriendsData(
                                                            list.get(i).oldId,
                                                            list.get(i).id,
                                                            Integer.parseInt(list
                                                                    .get(i).calendId),
                                                            list.get(i).dataState);
                                                }
                                            }
                                        }
                                        alertDialog();
                                    } else {
                                        app.deleteNewFriendLocalData(ID);
                                        alertFailDialog();
                                    }
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                app.deleteNewFriendLocalData(ID);
                                alertFailDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressUtil.dismiss();
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

        private void alertDialog() {
            final AlertDialog builder = new AlertDialog.Builder(context).create();
            builder.show();
            Window window = builder.getWindow();
            android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
            params.alpha = 0.92f;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);// 设置生效
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_alert_ok);
            TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
            TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
            delete_tv.setText("转发成功！");
            delete_ok.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    builder.cancel();
                    finish();
                }
            });

        }

        private void alertFailDialog() {
            final AlertDialog builder = new AlertDialog.Builder(context).create();
            builder.show();
            Window window = builder.getWindow();
            android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
            params.alpha = 0.92f;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);// 设置生效
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_alert_ok);
            TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
            TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
            delete_tv.setText("转发失败！");
            delete_ok.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    builder.cancel();
                }
            });
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            App.getHttpQueues().cancelAll("down");
        }
    }
}
