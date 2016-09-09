package com.mission.schedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.AddTagBackBean;
import com.mission.schedule.bean.AddTagBean;
import com.mission.schedule.bean.TagCommandBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.SharedPrefUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

public class UpdataTagService extends Service {

	List<TagCommandBean> addList = new ArrayList<TagCommandBean>();
	List<TagCommandBean> updateList = new ArrayList<TagCommandBean>();
	List<TagCommandBean> deleteList = new ArrayList<TagCommandBean>();
	String json;
	String userid;
	SharedPrefUtil sharedPrefUtil = null;
	App app = App.getDBcApplication();
	public static List<AddTagBean> tagbeans = null;

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
		tagbeans = new ArrayList<AddTagBean>();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				sharedPrefUtil = new SharedPrefUtil(getApplication(),
						ShareFile.USERFILE);
				userid = sharedPrefUtil.getString(getApplication(), ShareFile.USERFILE,
						ShareFile.USERID, "0");
				updateLoad();
			}
		}).start();
	}

	private void updateLoad() {
		addList.clear();
		updateList.clear();
		deleteList.clear();
		JSONArray jsonarray1 = new JSONArray();
		JSONArray jsonarray2 = new JSONArray();
		JSONArray jsonarray3 = new JSONArray();
		JSONObject jsonobject1 = new JSONObject();
		try {
			addList = app.QueryTagData(1);
			updateList = app.QueryTagData(2);
			deleteList = app.QueryTagData(3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (addList != null && addList.size() > 0) {
				for (int i = 0; i < addList.size(); i++) {
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("id",
							addList.get(i).getCtgId());
					jsonobject.put("tagName",
							addList.get(i).getCtgText());
					jsonobject.put("uid", userid);
					jsonobject.put("stateTag",
							addList.get(i).getCtgType());
					jsonobject.put("remark", "");
					jsonobject.put("orderIndex",
							addList.get(i).getCtgOrder());
					jsonobject.put("color",
							addList.get(i).getCtgColor());
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
							updateList.get(i).getCtgId());
					jsonobject.put("tagName",
							updateList.get(i).getCtgText());
					jsonobject.put("uid", userid);
					jsonobject.put("stateTag",
							updateList.get(i).getCtgType());
					jsonobject.put("remark", "");
					jsonobject.put("orderIndex",
							updateList.get(i).getCtgOrder());
					jsonobject.put("color",
							updateList.get(i).getCtgColor());
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
							deleteList.get(i).getCtgId());
					jsonobject.put("tagName",
							deleteList.get(i).getCtgText());
					jsonobject.put("uid", userid);
					jsonobject.put("stateTag",
							deleteList.get(i).getCtgType());
					jsonobject.put("remark", "");
					jsonobject.put("orderIndex",
							deleteList.get(i).getCtgOrder());
					jsonobject.put("color",
							deleteList.get(i).getCtgColor());
					jsonarray3.put(jsonobject);
				}
				jsonobject1.put("deleData", jsonarray3);
			} else {
				jsonobject1.put("deleData", jsonarray3);
			}
			json = jsonobject1.toString();
			String path = URLConstants.标签同步;
			StringRequest request = new StringRequest(Method.POST, path, new Response.Listener<String>() {

				@Override
				public void onResponse(String s) {
					if(!TextUtils.isEmpty(s)){
						Gson gson = new Gson();
						try {
							AddTagBackBean backBean = gson.fromJson(s, AddTagBackBean.class);
							List<AddTagBean> beans = null;
							if(backBean.status==0){
								beans = backBean.list;
								tagbeans.clear();
								tagbeans = beans;
								if(beans!=null&&beans.size()>0){
									for(int i=0;i<beans.size();i++){
										if(beans.get(i).state==0){
											if(beans.get(i).dataState==0){
												app.updateTagID(beans.get(i).originalId, beans.get(i).id);
											}else if(beans.get(i).dataState==1){
												
											}else if(beans.get(i).dataState==2){
												app.deleteTagData(beans.get(i).id);
											}
										}
									}
								}
							}else{
								stopSelf();
							}
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					stopSelf();
					App.getHttpQueues().cancelAll("updatetag");
				}

			}){
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("data", json);
 					return map;
				}
			};
			request.setTag("updatetag");
			request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
			App.getHttpQueues().add(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
		App.getHttpQueues().cancelAll("updatetag");
	}
}
