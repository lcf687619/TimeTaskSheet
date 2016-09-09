package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mission.schedule.R;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.FriendsBean;
import com.mission.schedule.bean.NewFriendChongFuBean;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.service.NewFriendDataUpLoadService;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;

import de.greenrobot.event.EventBus;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class NewSendMessageToFriendActivity extends TabActivity implements
		OnClickListener {

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	private TabHost tabHost;
	private TabHost.TabSpec tabSpec;
	TextView chongfu_tv, richeng_tv, yiqian_tv;
	LinearLayout top_ll_back;
	TextView middle_tv;
	LinearLayout top_ll_right;

	public static FriendsBean friendsBean = null;

	List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
	String json = "";
	static App app = null;
	ActivityManager1 activityManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_newsendmessagetofriend);
		context = this;
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		initViews();
	}

	public void initViews() {
		findViewById(R.id.top_ll_back).setOnClickListener(this);
		middle_tv = (TextView) this.findViewById(R.id.middle_tv);
		findViewById(R.id.top_ll_right).setVisibility(View.GONE);
		tabHost = new TabHost(getBaseContext(), null);
		tabHost = extracted();
		chongfu_tv = (TextView) this.findViewById(R.id.chongfu_tv);
		richeng_tv = (TextView) this.findViewById(R.id.richeng_tv);
		yiqian_tv = (TextView) this.findViewById(R.id.yiqian_tv);

		chongfu_tv.setOnClickListener(this);
		richeng_tv.setOnClickListener(this);
		yiqian_tv.setOnClickListener(this);
		
		friendsBean = null;
		friendsBean = (FriendsBean) getIntent()
				.getSerializableExtra("myfriend");
		middle_tv.setText(friendsBean.uName);

		tabHost.addTab(tabHost.newTabSpec("first").setIndicator("first")
				.setContent(new Intent(context, NewChongFuActivity.class)));// 重复
		tabHost.addTab(tabHost.newTabSpec("second").setIndicator("second")
				.setContent(new Intent(context, NewRiChengActivity.class)));// 日程
		tabHost.addTab(tabHost.newTabSpec("three").setIndicator("three")
				.setContent(new Intent(context, NewYiQianActivity.class)));// 以前

		chongfu_tv.setTextColor(getResources().getColor(R.color.hintcolor));
		richeng_tv
				.setTextColor(getResources().getColor(R.color.mingtian_color));
		yiqian_tv.setTextColor(getResources().getColor(R.color.hintcolor));

		UpdateLoadData();
		updateShunyanData();
		CreateRepChildData();
		tabHost.setCurrentTabByTag("second");
	}

	private void CreateRepChildData() {
		try {
			List<Map<String, String>> list = app.queryAllLocalFriendsData(9,
					friendsBean.fId);
			Map<String, String> map = null;
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				NewFriendChongFuBean bean = new NewFriendChongFuBean();
				bean.uid = map.get(CLNFMessage.nfmSendId);
				bean.cpId = map.get(CLNFMessage.nfmGetId);
				bean.calendaId = map.get(CLNFMessage.nfmCalendarId);
				bean.openState = map.get(CLNFMessage.nfmOpenState);
				bean.schIsAlarm = map.get(CLNFMessage.nfmIsAlarm);
				bean.schCpostpone = map.get(CLNFMessage.nfmPostpone);
				bean.schDisplayTime = map.get(CLNFMessage.nfmDisplayTime);
				bean.schBeforeTime = map.get(CLNFMessage.nfmBeforeTime);
				bean.repType = map.get(CLNFMessage.nfmType);
				bean.schAType = map.get(CLNFMessage.nfmAType);
				bean.repInSTable = map.get(CLNFMessage.nfmInSTable);
				bean.isEnd = map.get(CLNFMessage.nfmIsEnd);
				bean.downstate = map.get(CLNFMessage.nfmDownState);
				bean.repTypeParameter = map.get(CLNFMessage.nfmParameter);
				bean.schContent = map.get(CLNFMessage.nfmContent);
				bean.schctime = map.get(CLNFMessage.nfmTime);
				bean.cTypeDesc = map.get(CLNFMessage.nfmSourceDesc);
				bean.cTypeSpare = map.get(CLNFMessage.nfmSourceDescSpare);
				bean.CAlarmsoundDesc = map.get(CLNFMessage.nfmRingDesc);
				bean.CAlarmsound = map.get(CLNFMessage.nfmRingCode);
				bean.repstartdate = map.get(CLNFMessage.nfmStartDate);
				bean.repinitialcreatedtime = map
						.get(CLNFMessage.nfmInitialCreatedTime);
				bean.schWebURL = map.get(CLNFMessage.nfmWebURL);
				bean.schImagePath = map.get(CLNFMessage.nfmImagePath);
				bean.uName = map.get(CLNFMessage.nfmSendName);
				bean.remark = map.get(CLNFMessage.nfmRemark);
				bean.id = map.get(CLNFMessage.nfmId);
				bean.repIsPuase = map.get(CLNFMessage.nfmIsPuase);
				bean.repstatetwo = map.get(CLNFMessage.nfmSubState);
				bean.repCalendaTime = map.get(CLNFMessage.nfmSubEndDate);
				app.deleteNewFriendsChildData(Integer.parseInt(bean.id));
				if ("0".equals(bean.repIsPuase)) {
					if ("0".equals(bean.repstatetwo)) {
						NewSendMessageToFriendActivity
								.CreateNextChildData(bean);
					} else {
						if (NewSendMessageToFriendActivity
								.getNextChildTime(bean).repNextCreatedTime
								.equals(bean.repdatetwo.replace("T", " "))) {
							if ("3".equals(bean.repstatetwo)) {
								NewSendMessageToFriendActivity
										.CreateNextChildEndData(bean);
							}
						} else {
							NewSendMessageToFriendActivity
									.CreateNextChildData(bean);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateShunyanData() {
		List<Map<String, String>> yiqianList = new ArrayList<Map<String, String>>();
		yiqianList.clear();
		try {
			yiqianList = app.queryAllLocalFriendsData(0, 0);
			if (yiqianList != null && yiqianList.size() > 0) {
				for (int i = 0; i < yiqianList.size(); i++) {
					app.updateNewFriendChildData1(
							Integer.parseInt(yiqianList.get(i).get(
									CLNFMessage.nfmId)),
									DateUtil.formatDate(new Date()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				if(!"".equals(json)){
					Intent intent = new Intent(context, NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				}
//				UpdateLoadAsync(path, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {

		}
	}

	private TabHost extracted() {
		return getTabHost();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;
		case R.id.chongfu_tv:
			EventBus.getDefault().post(new FristFragment("0"));
			tabHost.setCurrentTabByTag("first");
			chongfu_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			richeng_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			yiqian_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			break;
		case R.id.richeng_tv:
			EventBus.getDefault().post(new FristFragment("1"));
			tabHost.setCurrentTabByTag("second");
			chongfu_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			richeng_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			yiqian_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			break;
		case R.id.yiqian_tv:
			EventBus.getDefault().post(new FristFragment("2"));
			tabHost.setCurrentTabByTag("three");
			chongfu_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			richeng_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			yiqian_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			break;
		default:
			break;
		}
	}

//	private void UpdateLoadAsync(String path, final String json) {
//		StringRequest request = new StringRequest(Method.POST, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							List<UpdateNewFriendMessageBean> list = null;
//							try {
//								UpdateNewFriendMessageBackBean backBean = gson
//										.fromJson(
//												result,
//												UpdateNewFriendMessageBackBean.class);
//								if (backBean.status == 0) {
//									list = backBean.list;
//									if (list != null && list.size() > 0) {
//										for (int i = 0; i < list.size(); i++) {
//											if (list.get(i).state == 0) {
//												if (list.get(i).dataState == 1
//														|| list.get(i).dataState == 2) {
//													app.updateNewFriendsData(
//															list.get(i).oldId,
//															list.get(i).id,
//															Integer.parseInt(list
//																	.get(i).calendId),
//															0);
//													app.updateNewFriendsChildData(
//															list.get(i).oldId,
//															list.get(i).id,
//															Integer.parseInt(list
//																	.get(i).calendId));
//												} else if (list.get(i).dataState == 3) {
//													app.deleteNewFriendsData(list
//															.get(i).id);
//													app.deleteNewFriendsChildData(list
//															.get(i).id);
//												}
//											} else {
//												app.updateNewFriendsData(
//														list.get(i).oldId,
//														list.get(i).id,
//														Integer.parseInt(list
//																.get(i).calendId),
//														list.get(i).dataState);
//												app.updateNewFriendsChildData(
//														list.get(i).oldId,
//														list.get(i).id,
//														Integer.parseInt(list
//																.get(i).calendId));
//											}
//										}
//									}
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError volleyError) {
//					}
//				}) {
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				Map<String, String> map = new HashMap<String, String>();
//				map.put("data", json);
//				return map;
//			}
//		};
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}

	/**
	 * 生成子记事对应的时间
	 */
	public static RepeatBean getNextChildTime(NewFriendChongFuBean bean) {
		RepeatBean repeatBean = null;
		if ("1".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(bean.schctime, 1, "", "");
		} else if ("2".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					2,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if ("3".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					3,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if ("4".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					4,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "0");
		} else if ("6".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					4,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "1");
		} else {
			repeatBean = RepeatDateUtils.saveCalendar(bean.schctime, 5, "", "");
		}
		return repeatBean;
	}

	/**
	 * 生成子记事
	 */
	public static void CreateNextChildData(NewFriendChongFuBean bean) {
		RepeatBean repeatBean = null;
		if ("1".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(bean.schctime, 1, "", "");
		} else if ("2".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					2,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if ("3".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					3,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if ("4".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					4,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "0");
		} else if ("6".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					4,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "1");
		} else {
			repeatBean = RepeatDateUtils.saveCalendar(bean.schctime, 5, "", "");
		}
		if (repeatBean.repNextCreatedTime.equals(bean.repCalendaTime.replace(
				"T", " "))) {
			app.insertMessageSendData(Integer.parseInt(bean.uid),
					Integer.parseInt(bean.cpId),
					Integer.parseInt(bean.calendaId),
					Integer.parseInt(bean.openState), 1,
					Integer.parseInt(bean.schIsAlarm),
					Integer.parseInt(bean.schCpostpone), 0,
					Integer.parseInt(bean.schDisplayTime),
					Integer.parseInt(bean.schBeforeTime), 1,
					Integer.parseInt(bean.repType),
					Integer.parseInt(bean.schAType),
					Integer.parseInt(bean.repInSTable),
					Integer.parseInt(bean.isEnd),
					Integer.parseInt(bean.downstate), 0, bean.repTypeParameter,
					bean.schContent,
					repeatBean.repNextCreatedTime.substring(0, 10),
					bean.schctime, bean.cTypeDesc, bean.cTypeSpare, "",
					bean.CAlarmsoundDesc, bean.CAlarmsound,
					bean.repstartdate.replace("T", " "),
					bean.repinitialcreatedtime.replace("T", " "),
					repeatBean.repLastCreatedTime.replace("T", " "),
					repeatBean.repNextCreatedTime.replace("T", " "),
					bean.schWebURL, bean.schImagePath, bean.uName, bean.remark,
					0, Integer.parseInt(bean.id), 0, "", 0,
					Integer.parseInt(bean.repCalendaState),
					bean.repCalendaTime.replace("T", " "),
					Integer.parseInt(bean.repIsPuase), "",DateUtil.formatDateTimeSs(new Date()));
		} else {
			app.insertMessageSendData(Integer.parseInt(bean.uid),
					Integer.parseInt(bean.cpId),
					Integer.parseInt(bean.calendaId),
					Integer.parseInt(bean.openState), 1,
					Integer.parseInt(bean.schIsAlarm),
					Integer.parseInt(bean.schCpostpone), 0,
					Integer.parseInt(bean.schDisplayTime),
					Integer.parseInt(bean.schBeforeTime), 1,
					Integer.parseInt(bean.repType),
					Integer.parseInt(bean.schAType),
					Integer.parseInt(bean.repInSTable),
					Integer.parseInt(bean.isEnd),
					Integer.parseInt(bean.downstate), 0, bean.repTypeParameter,
					bean.schContent,
					repeatBean.repNextCreatedTime.substring(0, 10),
					bean.schctime, bean.cTypeDesc, bean.cTypeSpare, "",
					bean.CAlarmsoundDesc, bean.CAlarmsound,
					bean.repstartdate.replace("T", " "),
					bean.repinitialcreatedtime.replace("T", " "),
					repeatBean.repLastCreatedTime.replace("T", " "),
					repeatBean.repNextCreatedTime.replace("T", " "),
					bean.schWebURL, bean.schImagePath, bean.uName, bean.remark,
					0, Integer.parseInt(bean.id), 0, "", 0, 0, "",
					Integer.parseInt(bean.repIsPuase), "",DateUtil.formatDateTimeSs(new Date()));
		}
	}

	/**
	 * 生成结束子记事
	 */
	public static void CreateNextChildEndData(NewFriendChongFuBean bean) {
		RepeatBean repeatBean = null;
		if ("1".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(bean.schctime, 1, "", "");
		} else if ("2".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					2,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if ("3".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					3,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if ("4".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					4,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "0");
		} else if ("6".equals(bean.repType)) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.schctime,
					4,
					bean.repTypeParameter.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "1");
		} else {
			repeatBean = RepeatDateUtils.saveCalendar(bean.schctime, 5, "", "");
		}
		if (repeatBean.repNextCreatedTime.equals(bean.repCalendaTime.replace(
				"T", " "))) {
			app.insertMessageSendData(Integer.parseInt(bean.uid),
					Integer.parseInt(bean.cpId),
					Integer.parseInt(bean.calendaId),
					Integer.parseInt(bean.openState), 1,
					Integer.parseInt(bean.schIsAlarm),
					Integer.parseInt(bean.schCpostpone), 0,
					Integer.parseInt(bean.schDisplayTime),
					Integer.parseInt(bean.schBeforeTime), 1,
					Integer.parseInt(bean.repType),
					Integer.parseInt(bean.schAType),
					Integer.parseInt(bean.repInSTable),
					Integer.parseInt(bean.isEnd),
					Integer.parseInt(bean.downstate), 1, bean.repTypeParameter,
					bean.schContent,
					repeatBean.repNextCreatedTime.substring(0, 10),
					bean.schctime, bean.cTypeDesc, bean.cTypeSpare, "",
					bean.CAlarmsoundDesc, bean.CAlarmsound,
					bean.repstartdate.replace("T", " "),
					bean.repinitialcreatedtime.replace("T", " "),
					repeatBean.repLastCreatedTime.replace("T", " "),
					repeatBean.repNextCreatedTime.replace("T", " "),
					bean.schWebURL, bean.schImagePath, bean.uName, bean.remark,
					0, Integer.parseInt(bean.id), 0, "", 0,
					Integer.parseInt(bean.repCalendaState),
					bean.repCalendaTime.replace("T", " "),
					Integer.parseInt(bean.repIsPuase), "",DateUtil.formatDateTimeSs(new Date()));
		} else {
			app.insertMessageSendData(Integer.parseInt(bean.uid),
					Integer.parseInt(bean.cpId),
					Integer.parseInt(bean.calendaId),
					Integer.parseInt(bean.openState), 1,
					Integer.parseInt(bean.schIsAlarm),
					Integer.parseInt(bean.schCpostpone), 0,
					Integer.parseInt(bean.schDisplayTime),
					Integer.parseInt(bean.schBeforeTime), 1,
					Integer.parseInt(bean.repType),
					Integer.parseInt(bean.schAType),
					Integer.parseInt(bean.repInSTable),
					Integer.parseInt(bean.isEnd),
					Integer.parseInt(bean.downstate), 1, bean.repTypeParameter,
					bean.schContent,
					repeatBean.repNextCreatedTime.substring(0, 10),
					bean.schctime, bean.cTypeDesc, bean.cTypeSpare, "",
					bean.CAlarmsoundDesc, bean.CAlarmsound,
					bean.repstartdate.replace("T", " "),
					bean.repinitialcreatedtime.replace("T", " "),
					repeatBean.repLastCreatedTime.replace("T", " "),
					repeatBean.repNextCreatedTime.replace("T", " "),
					bean.schWebURL, bean.schImagePath, bean.uName, bean.remark,
					0, Integer.parseInt(bean.id), 0, "", 0, 0, "",
					Integer.parseInt(bean.repIsPuase), "",DateUtil.formatDateTimeSs(new Date()));
		}

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(NewRiChengActivity.beans!=null&&NewRiChengActivity.beans.size()>0){
			NewRiChengActivity.beans.clear();
		}
//		App.getHttpQueues().cancelAll("down");
	}
}
