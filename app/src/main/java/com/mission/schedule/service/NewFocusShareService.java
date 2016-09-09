package com.mission.schedule.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.activity.NewFocusShareRepeatActivity;
import com.mission.schedule.activity.NewFocusShareRiChengActivity;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewFocusDeleteRepDataBean;
import com.mission.schedule.bean.NewFocusDeleteSchDataBean;
import com.mission.schedule.bean.NewFocusShareRepeatBackBean;
import com.mission.schedule.bean.NewFocusShareRepeatBean;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueBeen;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueListBeen;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.bean.UpNewFocusShareBackBean;
import com.mission.schedule.bean.UpNewFocusShareBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLFindScheduleTable;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

public class NewFocusShareService extends Service {

	public static final String SCHUPDATADATA = "schUpdateData";
	public static final String REPUPDATADATA = "repUpdateData";

	private String userID = "";
	App app = null;
	SharedPrefUtil sharedPrefUtil = null;
	String downSchDate = "";
	String downRepDate = "";
	String downSchPath = "";
	String downRepPath = "";
	String upSchPath = "";
	String upRepPath = "";
	String upSchString = "";
	String upRepString = "";
	String ringdesc = "完成任务";
	String ringcode = "g_88";
	int IsRepeat = 0;
	List<Map<String, String>> upAddFocusSchList;
	List<Map<String, String>> upUpdateFocusSchList;
	List<Map<String, String>> upDeleteFocusSchList;
	List<Map<String, String>> upAddFocusRepList;
	List<Map<String, String>> upUpdateFocusRepList;
	List<Map<String, String>> upDeleteFocusRepList;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private void UpLoadData() {
		try {
			upAddFocusRepList = app.QueryNewFocusData(-1, 0);
			upUpdateFocusRepList = app.QueryNewFocusData(-2, 0);
			upDeleteFocusRepList = app.QueryNewFocusData(-3, 0);
			JSONArray jsonarray4 = new JSONArray();
			JSONArray jsonarray5 = new JSONArray();
			JSONArray jsonarray6 = new JSONArray();
			JSONObject jsonobject2 = new JSONObject();
			if (upAddFocusRepList != null && upAddFocusRepList.size() > 0) {
				for (Map<String, String> repMap : upAddFocusRepList) {
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("repId",
							repMap.get(CLFindScheduleTable.fstRepeatId));
					jsonobject.put("repUid",
							repMap.get(CLFindScheduleTable.fstFID));
					jsonobject.put("repBeforeTime",
							repMap.get(CLFindScheduleTable.fstBeforeTime));
					jsonobject.put("ctags",
							repMap.get(CLFindScheduleTable.fstTags));
					jsonobject.put("repColorType",
							repMap.get(CLFindScheduleTable.fstColorType));
					jsonobject.put("repDisplayTime",
							repMap.get(CLFindScheduleTable.fstDisplayTime));
					jsonobject.put("repType",
							repMap.get(CLFindScheduleTable.fstRepType));
					jsonobject.put("repTypeParameter",
							repMap.get(CLFindScheduleTable.fstParameter)
									.replace("\n\"", "").replace("\n", "")
									.replace("\"", "").toString());
					jsonobject.put("repIsAlarm",
							repMap.get(CLFindScheduleTable.fstIsAlarm));
					jsonobject.put("repIsPuase",
							repMap.get(CLFindScheduleTable.fstIsPuase));
					jsonobject.put("repIsImportant",
							repMap.get(CLFindScheduleTable.fstIsImportant));
					jsonobject.put("repSourceType",
							repMap.get(CLFindScheduleTable.fstSourceType));
					jsonobject.put("repSourceDesc",
							repMap.get(CLFindScheduleTable.fstSourceDesc));
					jsonobject.put("repSourceDescSpare",
							repMap.get(CLFindScheduleTable.fstSourceDescSpare));
					jsonobject.put("repNextCreatedTime", repMap
							.get(CLFindScheduleTable.fstRpNextCreatedTime));
					jsonobject.put("repLastCreatedTime", repMap
							.get(CLFindScheduleTable.fstRepLastCreatedTime));
					jsonobject.put("repStartDate",
							repMap.get(CLFindScheduleTable.fstRepStartDate));
					jsonobject.put("repContent",
							repMap.get(CLFindScheduleTable.fstContent));
					jsonobject.put("repCreateTime",
							repMap.get(CLFindScheduleTable.fstCreateTime));
					jsonobject.put("repTime",
							repMap.get(CLFindScheduleTable.fstTime));
					jsonobject.put("repRingDesc",
							repMap.get(CLFindScheduleTable.fstRingDesc));
					jsonobject.put("repRingCode",
							repMap.get(CLFindScheduleTable.fstRingCode));
					jsonobject.put("repUpdateTime",
							repMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("repOpenState",
							repMap.get(CLFindScheduleTable.fstOpenState));
					jsonobject.put("repDecoupledState", 0 + "");
					jsonobject.put("repEndState",
							repMap.get(CLFindScheduleTable.fstIsEnd));
					jsonobject.put("repDeleteState", "" + 0);
					jsonobject.put("repChangeState",
							repMap.get(CLFindScheduleTable.fstUpdateState));
					jsonobject.put("repChangeTime",
							repMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("repInitialCreatedTime", repMap
							.get(CLFindScheduleTable.fstRepInitialCreatedTime));
					jsonobject.put("repDecoupledStateTwo", 0 + "");
					jsonobject.put("repEndStateTwo", 0 + "");
					jsonobject.put("repDeleteStateTwo", 0 + "");
					jsonobject.put("recommendedUserId",
							repMap.get(CLFindScheduleTable.fstRecommendId));
					jsonobject.put("repChangeTimeTwo", "");
					jsonobject.put("recommendedUserName",
							repMap.get(CLFindScheduleTable.fstRecommendName));
					jsonobject.put("repdateone",
							repMap.get(CLFindScheduleTable.fstRepDateOne));
					jsonobject.put("repdatetwo",
							repMap.get(CLFindScheduleTable.fstRepDateTwo));
					jsonobject.put("repstateone",
							repMap.get(CLFindScheduleTable.fstRepStateOne));
					jsonobject.put("repstatetwo",
							repMap.get(CLFindScheduleTable.fstRepStateTwo));
					jsonobject.put("repRead",
							repMap.get(CLFindScheduleTable.fstIsRead));
					jsonobject.put("atype",
							repMap.get(CLFindScheduleTable.fstAType));
					jsonobject.put("webUrl",
							repMap.get(CLFindScheduleTable.fstWebURL));
					jsonobject.put("imgPath",
							repMap.get(CLFindScheduleTable.fstImagePath));
					jsonobject.put("repInStable",
							repMap.get(CLFindScheduleTable.fstRepInStable));
					jsonobject.put("remark1",
							repMap.get(CLFindScheduleTable.fstReamrk1));
					jsonobject.put("remark2",
							repMap.get(CLFindScheduleTable.fstReamrk2));
					jsonobject.put("remark3",
							repMap.get(CLFindScheduleTable.fstReamrk3));
					jsonobject.put("remark4",
							repMap.get(CLFindScheduleTable.fstReamrk4));
					jsonobject.put("remark5",
							repMap.get(CLFindScheduleTable.fstReamrk5));
					jsonobject.put("parReamrk",
							repMap.get(CLFindScheduleTable.fstParReamrk));
					jsonarray4.put(jsonobject);
				}
				jsonobject2.put("addData", jsonarray4);
			} else {
				jsonobject2.put("addData", jsonarray4);
			}
			if (upUpdateFocusRepList != null && upUpdateFocusRepList.size() > 0) {
				for (Map<String, String> repMap : upUpdateFocusRepList) {
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("repId",
							repMap.get(CLFindScheduleTable.fstRepeatId));
					jsonobject.put("repUid",
							repMap.get(CLFindScheduleTable.fstFID));
					jsonobject.put("repBeforeTime",
							repMap.get(CLFindScheduleTable.fstBeforeTime));
					jsonobject.put("ctags",
							repMap.get(CLFindScheduleTable.fstTags));
					jsonobject.put("repColorType",
							repMap.get(CLFindScheduleTable.fstColorType));
					jsonobject.put("repDisplayTime",
							repMap.get(CLFindScheduleTable.fstDisplayTime));
					jsonobject.put("repType",
							repMap.get(CLFindScheduleTable.fstRepType));
					jsonobject.put("repTypeParameter",
							repMap.get(CLFindScheduleTable.fstParameter)
									.replace("\n\"", "").replace("\n", "")
									.replace("\"", "").toString());
					jsonobject.put("repIsAlarm",
							repMap.get(CLFindScheduleTable.fstIsAlarm));
					jsonobject.put("repIsPuase",
							repMap.get(CLFindScheduleTable.fstIsPuase));
					jsonobject.put("repIsImportant",
							repMap.get(CLFindScheduleTable.fstIsImportant));
					jsonobject.put("repSourceType",
							repMap.get(CLFindScheduleTable.fstSourceType));
					jsonobject.put("repSourceDesc",
							repMap.get(CLFindScheduleTable.fstSourceDesc));
					jsonobject.put("repSourceDescSpare",
							repMap.get(CLFindScheduleTable.fstSourceDescSpare));
					jsonobject.put("repNextCreatedTime", repMap
							.get(CLFindScheduleTable.fstRpNextCreatedTime));
					jsonobject.put("repLastCreatedTime", repMap
							.get(CLFindScheduleTable.fstRepLastCreatedTime));
					jsonobject.put("repStartDate",
							repMap.get(CLFindScheduleTable.fstRepStartDate));
					jsonobject.put("repContent",
							repMap.get(CLFindScheduleTable.fstContent));
					jsonobject.put("repCreateTime",
							repMap.get(CLFindScheduleTable.fstCreateTime));
					jsonobject.put("repTime",
							repMap.get(CLFindScheduleTable.fstTime));
					jsonobject.put("repRingDesc",
							repMap.get(CLFindScheduleTable.fstRingDesc));
					jsonobject.put("repRingCode",
							repMap.get(CLFindScheduleTable.fstRingCode));
					jsonobject.put("repUpdateTime",
							repMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("repOpenState",
							repMap.get(CLFindScheduleTable.fstOpenState));
					jsonobject.put("repDecoupledState", 0 + "");
					jsonobject.put("repEndState",
							repMap.get(CLFindScheduleTable.fstIsEnd));
					jsonobject.put("repDeleteState", "" + 0);
					jsonobject.put("repChangeState",
							repMap.get(CLFindScheduleTable.fstUpdateState));
					jsonobject.put("repChangeTime",
							repMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("repInitialCreatedTime", repMap
							.get(CLFindScheduleTable.fstRepInitialCreatedTime));
					jsonobject.put("repDecoupledStateTwo", 0 + "");
					jsonobject.put("repEndStateTwo", 0 + "");
					jsonobject.put("repDeleteStateTwo", 0 + "");
					jsonobject.put("recommendedUserId",
							repMap.get(CLFindScheduleTable.fstRecommendId));
					jsonobject.put("repChangeTimeTwo", "");
					jsonobject.put("recommendedUserName",
							repMap.get(CLFindScheduleTable.fstRecommendName));
					jsonobject.put("repdateone",
							repMap.get(CLFindScheduleTable.fstRepDateOne));
					jsonobject.put("repdatetwo",
							repMap.get(CLFindScheduleTable.fstRepDateTwo));
					jsonobject.put("repstateone",
							repMap.get(CLFindScheduleTable.fstRepStateOne));
					jsonobject.put("repstatetwo",
							repMap.get(CLFindScheduleTable.fstRepStateTwo));
					jsonobject.put("repRead",
							repMap.get(CLFindScheduleTable.fstIsRead));
					jsonobject.put("atype",
							repMap.get(CLFindScheduleTable.fstAType));
					jsonobject.put("webUrl",
							repMap.get(CLFindScheduleTable.fstWebURL));
					jsonobject.put("imgPath",
							repMap.get(CLFindScheduleTable.fstImagePath));
					jsonobject.put("repInStable",
							repMap.get(CLFindScheduleTable.fstRepInStable));
					jsonobject.put("remark1",
							repMap.get(CLFindScheduleTable.fstReamrk1));
					jsonobject.put("remark2",
							repMap.get(CLFindScheduleTable.fstReamrk2));
					jsonobject.put("remark3",
							repMap.get(CLFindScheduleTable.fstReamrk3));
					jsonobject.put("remark4",
							repMap.get(CLFindScheduleTable.fstReamrk4));
					jsonobject.put("remark5",
							repMap.get(CLFindScheduleTable.fstReamrk5));
					jsonobject.put("parReamrk",
							repMap.get(CLFindScheduleTable.fstParReamrk));
					jsonarray5.put(jsonobject);
				}
				jsonobject2.put("updateData", jsonarray5);
			} else {
				jsonobject2.put("updateData", jsonarray5);
			}
			if (upDeleteFocusRepList != null && upDeleteFocusRepList.size() > 0) {
				for (Map<String, String> repMap : upDeleteFocusRepList) {
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("repId",
							repMap.get(CLFindScheduleTable.fstRepeatId));
					jsonobject.put("repUid",
							repMap.get(CLFindScheduleTable.fstFID));
					jsonobject.put("repBeforeTime",
							repMap.get(CLFindScheduleTable.fstBeforeTime));
					jsonobject.put("ctags",
							repMap.get(CLFindScheduleTable.fstTags));
					jsonobject.put("repColorType",
							repMap.get(CLFindScheduleTable.fstColorType));
					jsonobject.put("repDisplayTime",
							repMap.get(CLFindScheduleTable.fstDisplayTime));
					jsonobject.put("repType",
							repMap.get(CLFindScheduleTable.fstRepType));
					jsonobject.put("repTypeParameter",
							repMap.get(CLFindScheduleTable.fstParameter)
									.replace("\n\"", "").replace("\n", "")
									.replace("\"", "").toString());
					jsonobject.put("repIsAlarm",
							repMap.get(CLFindScheduleTable.fstIsAlarm));
					jsonobject.put("repIsPuase",
							repMap.get(CLFindScheduleTable.fstIsPuase));
					jsonobject.put("repIsImportant",
							repMap.get(CLFindScheduleTable.fstIsImportant));
					jsonobject.put("repSourceType",
							repMap.get(CLFindScheduleTable.fstSourceType));
					jsonobject.put("repSourceDesc",
							repMap.get(CLFindScheduleTable.fstSourceDesc));
					jsonobject.put("repSourceDescSpare",
							repMap.get(CLFindScheduleTable.fstSourceDescSpare));
					jsonobject.put("repNextCreatedTime", repMap
							.get(CLFindScheduleTable.fstRpNextCreatedTime));
					jsonobject.put("repLastCreatedTime", repMap
							.get(CLFindScheduleTable.fstRepLastCreatedTime));
					jsonobject.put("repStartDate",
							repMap.get(CLFindScheduleTable.fstRepStartDate));
					jsonobject.put("repContent",
							repMap.get(CLFindScheduleTable.fstContent));
					jsonobject.put("repCreateTime",
							repMap.get(CLFindScheduleTable.fstCreateTime));
					jsonobject.put("repTime",
							repMap.get(CLFindScheduleTable.fstTime));
					jsonobject.put("repRingDesc",
							repMap.get(CLFindScheduleTable.fstRingDesc));
					jsonobject.put("repRingCode",
							repMap.get(CLFindScheduleTable.fstRingCode));
					jsonobject.put("repUpdateTime",
							repMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("repOpenState",
							repMap.get(CLFindScheduleTable.fstOpenState));
					jsonobject.put("repDecoupledState", 0 + "");
					jsonobject.put("repEndState",
							repMap.get(CLFindScheduleTable.fstIsEnd));
					jsonobject.put("repDeleteState", "" + 0);
					jsonobject.put("repChangeState",
							repMap.get(CLFindScheduleTable.fstUpdateState));
					jsonobject.put("repChangeTime",
							repMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("repInitialCreatedTime", repMap
							.get(CLFindScheduleTable.fstRepInitialCreatedTime));
					jsonobject.put("repDecoupledStateTwo", 0 + "");
					jsonobject.put("repEndStateTwo", 0 + "");
					jsonobject.put("repDeleteStateTwo", 0 + "");
					jsonobject.put("recommendedUserId",
							repMap.get(CLFindScheduleTable.fstRecommendId));
					jsonobject.put("repChangeTimeTwo", "");
					jsonobject.put("recommendedUserName",
							repMap.get(CLFindScheduleTable.fstRecommendName));
					jsonobject.put("repdateone",
							repMap.get(CLFindScheduleTable.fstRepDateOne));
					jsonobject.put("repdatetwo",
							repMap.get(CLFindScheduleTable.fstRepDateTwo));
					jsonobject.put("repstateone",
							repMap.get(CLFindScheduleTable.fstRepStateOne));
					jsonobject.put("repstatetwo",
							repMap.get(CLFindScheduleTable.fstRepStateTwo));
					jsonobject.put("repRead",
							repMap.get(CLFindScheduleTable.fstIsRead));
					jsonobject.put("atype",
							repMap.get(CLFindScheduleTable.fstAType));
					jsonobject.put("webUrl",
							repMap.get(CLFindScheduleTable.fstWebURL));
					jsonobject.put("imgPath",
							repMap.get(CLFindScheduleTable.fstImagePath));
					jsonobject.put("repInStable",
							repMap.get(CLFindScheduleTable.fstRepInStable));
					jsonobject.put("remark1",
							repMap.get(CLFindScheduleTable.fstReamrk1));
					jsonobject.put("remark2",
							repMap.get(CLFindScheduleTable.fstReamrk2));
					jsonobject.put("remark3",
							repMap.get(CLFindScheduleTable.fstReamrk3));
					jsonobject.put("remark4",
							repMap.get(CLFindScheduleTable.fstReamrk4));
					jsonobject.put("remark5",
							repMap.get(CLFindScheduleTable.fstReamrk5));
					jsonobject.put("parReamrk",
							repMap.get(CLFindScheduleTable.fstParReamrk));
					jsonarray6.put(jsonobject);
				}
				jsonobject2.put("deleData", jsonarray6);
			} else {
				jsonobject2.put("deleData", jsonarray6);
			}
			upRepString = jsonobject2.toString();

			JSONArray jsonarray1 = new JSONArray();
			JSONArray jsonarray2 = new JSONArray();
			JSONArray jsonarray3 = new JSONArray();
			JSONObject jsonobject1 = new JSONObject();
			upAddFocusSchList = app.QueryNewFocusData(-4, 0);
			upUpdateFocusSchList = app.QueryNewFocusData(-5, 0);
			upDeleteFocusSchList = app.QueryNewFocusData(-6, 0);
			if (upAddFocusSchList != null && upAddFocusSchList.size() > 0) {
				for (Map<String, String> schMap : upAddFocusSchList) {
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("CId",
							schMap.get(CLFindScheduleTable.fstSchID));
					jsonobject.put("CUid",
							schMap.get(CLFindScheduleTable.fstFID));
					jsonobject.put("CContent",
							schMap.get(CLFindScheduleTable.fstContent));
					jsonobject.put("CDate",
							schMap.get(CLFindScheduleTable.fstDate));
					jsonobject.put("CTime",
							schMap.get(CLFindScheduleTable.fstTime));
					jsonobject.put("CIsAlarm",
							schMap.get(CLFindScheduleTable.fstIsAlarm));
					jsonobject.put("CBefortime",
							schMap.get(CLFindScheduleTable.fstBeforeTime));
					jsonobject.put("CAlarmsound",
							schMap.get(CLFindScheduleTable.fstRingCode));
					jsonobject.put("CAlarmsoundDesc",
							schMap.get(CLFindScheduleTable.fstRingDesc));
					jsonobject.put("CDisplayAlarm",
							schMap.get(CLFindScheduleTable.fstDisplayTime));
					jsonobject.put("CPostpone",
							schMap.get(CLFindScheduleTable.fstIsPostpone));
					jsonobject.put("CImportant",
							schMap.get(CLFindScheduleTable.fstIsImportant));
					jsonobject.put("CColorType",
							schMap.get(CLFindScheduleTable.fstColorType));
					jsonobject.put("CIsEnd",
							schMap.get(CLFindScheduleTable.fstIsEnd));
					jsonobject.put("CCreateTime",
							schMap.get(CLFindScheduleTable.fstCreateTime));
					jsonobject.put("CTags",
							schMap.get(CLFindScheduleTable.fstTags));
					jsonobject.put("CType",
							schMap.get(CLFindScheduleTable.fstSourceType));
					jsonobject.put("CTypeDesc",
							schMap.get(CLFindScheduleTable.fstSourceDesc));
					jsonobject.put("CTypeSpare",
							schMap.get(CLFindScheduleTable.fstSourceDescSpare));
					jsonobject.put("CRepeatId",
							schMap.get(CLFindScheduleTable.fstRepeatId));
					jsonobject.put("CRepeatDate",
							schMap.get(CLFindScheduleTable.fstRepeatDate));
					jsonobject.put("CUpdateTime",
							schMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("COpenstate",
							schMap.get(CLFindScheduleTable.fstOpenState));
					jsonobject.put("CLightAppId", 0 + "");
					jsonobject.put("CStoreParentId", 0 + "");
					jsonobject.put("CSchRepeatLink",
							schMap.get(CLFindScheduleTable.fstRepeatLink));
					jsonobject.put("CRecommendName",
							schMap.get(CLFindScheduleTable.fstRecommendName));
					jsonobject.put("CRecommendId",
							schMap.get(CLFindScheduleTable.fstRecommendId));
					jsonobject.put("schRead",
							schMap.get(CLFindScheduleTable.fstIsRead));
					jsonobject.put("calendaReamrk", "");
					jsonobject.put("atype",
							schMap.get(CLFindScheduleTable.fstAType));
					jsonobject.put("webUrl",
							schMap.get(CLFindScheduleTable.fstWebURL));
					jsonobject.put("imgPath",
							schMap.get(CLFindScheduleTable.fstImagePath));
					jsonobject.put("attentionid",
							schMap.get(CLFindScheduleTable.fstAID));
					jsonobject.put("remark1",
							schMap.get(CLFindScheduleTable.fstReamrk1));
					jsonobject.put("remark2",
							schMap.get(CLFindScheduleTable.fstReamrk2));
					jsonobject.put("remark3",
							schMap.get(CLFindScheduleTable.fstReamrk3));
					jsonobject.put("remark4",
							schMap.get(CLFindScheduleTable.fstReamrk4));
					jsonobject.put("remark5",
							schMap.get(CLFindScheduleTable.fstReamrk5));
					jsonarray1.put(jsonobject);
				}
				jsonobject1.put("addData", jsonarray1);
			} else {
				jsonobject1.put("addData", jsonarray1);
			}
			if (upUpdateFocusSchList != null && upUpdateFocusSchList.size() > 0) {
				for (Map<String, String> schMap : upUpdateFocusSchList) {
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("CId",
							schMap.get(CLFindScheduleTable.fstSchID));
					jsonobject.put("CUid",
							schMap.get(CLFindScheduleTable.fstFID));
					jsonobject.put("CContent",
							schMap.get(CLFindScheduleTable.fstContent));
					jsonobject.put("CDate",
							schMap.get(CLFindScheduleTable.fstDate));
					jsonobject.put("CTime",
							schMap.get(CLFindScheduleTable.fstTime));
					jsonobject.put("CIsAlarm",
							schMap.get(CLFindScheduleTable.fstIsAlarm));
					jsonobject.put("CBefortime",
							schMap.get(CLFindScheduleTable.fstBeforeTime));
					jsonobject.put("CAlarmsound",
							schMap.get(CLFindScheduleTable.fstRingCode));
					jsonobject.put("CAlarmsoundDesc",
							schMap.get(CLFindScheduleTable.fstRingDesc));
					jsonobject.put("CDisplayAlarm",
							schMap.get(CLFindScheduleTable.fstDisplayTime));
					jsonobject.put("CPostpone",
							schMap.get(CLFindScheduleTable.fstIsPostpone));
					jsonobject.put("CImportant",
							schMap.get(CLFindScheduleTable.fstIsImportant));
					jsonobject.put("CColorType",
							schMap.get(CLFindScheduleTable.fstColorType));
					jsonobject.put("CIsEnd",
							schMap.get(CLFindScheduleTable.fstIsEnd));
					jsonobject.put("CCreateTime",
							schMap.get(CLFindScheduleTable.fstCreateTime));
					jsonobject.put("CTags",
							schMap.get(CLFindScheduleTable.fstTags));
					jsonobject.put("CType",
							schMap.get(CLFindScheduleTable.fstSourceType));
					jsonobject.put("CTypeDesc",
							schMap.get(CLFindScheduleTable.fstSourceDesc));
					jsonobject.put("CTypeSpare",
							schMap.get(CLFindScheduleTable.fstSourceDescSpare));
					jsonobject.put("CRepeatId",
							schMap.get(CLFindScheduleTable.fstRepeatId));
					jsonobject.put("CRepeatDate",
							schMap.get(CLFindScheduleTable.fstRepeatDate));
					jsonobject.put("CUpdateTime",
							schMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("COpenstate",
							schMap.get(CLFindScheduleTable.fstOpenState));
					jsonobject.put("CLightAppId", 0 + "");
					jsonobject.put("CStoreParentId", 0 + "");
					jsonobject.put("CSchRepeatLink",
							schMap.get(CLFindScheduleTable.fstRepeatLink));
					jsonobject.put("CRecommendName",
							schMap.get(CLFindScheduleTable.fstRecommendName));
					jsonobject.put("CRecommendId",
							schMap.get(CLFindScheduleTable.fstRecommendId));
					jsonobject.put("schRead",
							schMap.get(CLFindScheduleTable.fstIsRead));
					jsonobject.put("calendaReamrk", "");
					jsonobject.put("atype",
							schMap.get(CLFindScheduleTable.fstAType));
					jsonobject.put("webUrl",
							schMap.get(CLFindScheduleTable.fstWebURL));
					jsonobject.put("imgPath",
							schMap.get(CLFindScheduleTable.fstImagePath));
					jsonobject.put("attentionid",
							schMap.get(CLFindScheduleTable.fstAID));
					jsonobject.put("remark1",
							schMap.get(CLFindScheduleTable.fstReamrk1));
					jsonobject.put("remark2",
							schMap.get(CLFindScheduleTable.fstReamrk2));
					jsonobject.put("remark3",
							schMap.get(CLFindScheduleTable.fstReamrk3));
					jsonobject.put("remark4",
							schMap.get(CLFindScheduleTable.fstReamrk4));
					jsonobject.put("remark5",
							schMap.get(CLFindScheduleTable.fstReamrk5));
					jsonarray2.put(jsonobject);
				}
				jsonobject1.put("updateData", jsonarray2);
			} else {
				jsonobject1.put("updateData", jsonarray2);
			}
			if (upDeleteFocusSchList != null && upDeleteFocusSchList.size() > 0) {
				for (Map<String, String> schMap : upDeleteFocusSchList) {
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("CId",
							schMap.get(CLFindScheduleTable.fstSchID));
					jsonobject.put("CUid",
							schMap.get(CLFindScheduleTable.fstFID));
					jsonobject.put("CContent",
							schMap.get(CLFindScheduleTable.fstContent));
					jsonobject.put("CDate",
							schMap.get(CLFindScheduleTable.fstDate));
					jsonobject.put("CTime",
							schMap.get(CLFindScheduleTable.fstTime));
					jsonobject.put("CIsAlarm",
							schMap.get(CLFindScheduleTable.fstIsAlarm));
					jsonobject.put("CBefortime",
							schMap.get(CLFindScheduleTable.fstBeforeTime));
					jsonobject.put("CAlarmsound",
							schMap.get(CLFindScheduleTable.fstRingCode));
					jsonobject.put("CAlarmsoundDesc",
							schMap.get(CLFindScheduleTable.fstRingDesc));
					jsonobject.put("CDisplayAlarm",
							schMap.get(CLFindScheduleTable.fstDisplayTime));
					jsonobject.put("CPostpone",
							schMap.get(CLFindScheduleTable.fstIsPostpone));
					jsonobject.put("CImportant",
							schMap.get(CLFindScheduleTable.fstIsImportant));
					jsonobject.put("CColorType",
							schMap.get(CLFindScheduleTable.fstColorType));
					jsonobject.put("CIsEnd",
							schMap.get(CLFindScheduleTable.fstIsEnd));
					jsonobject.put("CCreateTime",
							schMap.get(CLFindScheduleTable.fstCreateTime));
					jsonobject.put("CTags",
							schMap.get(CLFindScheduleTable.fstTags));
					jsonobject.put("CType",
							schMap.get(CLFindScheduleTable.fstSourceType));
					jsonobject.put("CTypeDesc",
							schMap.get(CLFindScheduleTable.fstSourceDesc));
					jsonobject.put("CTypeSpare",
							schMap.get(CLFindScheduleTable.fstSourceDescSpare));
					jsonobject.put("CRepeatId",
							schMap.get(CLFindScheduleTable.fstRepeatId));
					jsonobject.put("CRepeatDate",
							schMap.get(CLFindScheduleTable.fstRepeatDate));
					jsonobject.put("CUpdateTime",
							schMap.get(CLFindScheduleTable.fstUpdateTime));
					jsonobject.put("COpenstate",
							schMap.get(CLFindScheduleTable.fstOpenState));
					jsonobject.put("CLightAppId", 0 + "");
					jsonobject.put("CStoreParentId", 0 + "");
					jsonobject.put("CSchRepeatLink",
							schMap.get(CLFindScheduleTable.fstRepeatLink));
					jsonobject.put("CRecommendName",
							schMap.get(CLFindScheduleTable.fstRecommendName));
					jsonobject.put("CRecommendId",
							schMap.get(CLFindScheduleTable.fstRecommendId));
					jsonobject.put("schRead",
							schMap.get(CLFindScheduleTable.fstIsRead));
					jsonobject.put("calendaReamrk", "");
					jsonobject.put("atype",
							schMap.get(CLFindScheduleTable.fstAType));
					jsonobject.put("webUrl",
							schMap.get(CLFindScheduleTable.fstWebURL));
					jsonobject.put("imgPath",
							schMap.get(CLFindScheduleTable.fstImagePath));
					jsonobject.put("attentionid",
							schMap.get(CLFindScheduleTable.fstAID));
					jsonobject.put("remark1",
							schMap.get(CLFindScheduleTable.fstReamrk1));
					jsonobject.put("remark2",
							schMap.get(CLFindScheduleTable.fstReamrk2));
					jsonobject.put("remark3",
							schMap.get(CLFindScheduleTable.fstReamrk3));
					jsonobject.put("remark4",
							schMap.get(CLFindScheduleTable.fstReamrk4));
					jsonobject.put("remark5",
							schMap.get(CLFindScheduleTable.fstReamrk5));
					jsonarray3.put(jsonobject);
				}
				jsonobject1.put("deleData", jsonarray3);
			} else {
				jsonobject1.put("deleData", jsonarray3);
			}
			upSchString = jsonobject1.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (intent == null) {
					return;
				}
				sharedPrefUtil = new SharedPrefUtil(getApplication(),
						ShareFile.USERFILE);
				app = App.getDBcApplication();
				userID = sharedPrefUtil.getString(getApplication(),
						ShareFile.USERFILE, ShareFile.USERID, "");
				downSchDate = sharedPrefUtil.getString(getApplication(),
						ShareFile.USERFILE, ShareFile.DOWNNEWFOCUSSHARESCHDATE,
						"");
				downRepDate = sharedPrefUtil.getString(getApplication(),
						ShareFile.USERFILE, ShareFile.DOWNNEWFOCUSSHAREREPDATE,
						"");
				if (!"".equals(userID)) {
					if ("".equals(downSchDate)) {
						downSchDate = "2016-01-01 00:00:00";
					}
					if ("".equals(downRepDate)) {
						downRepDate = "2016-01-01 00:00:00";
					}
					downSchDate = downSchDate.replace(" ", "%2B");
					downRepDate = downRepDate.replace(" ", "%2B");
					downSchPath = URLConstants.新版发现下行普通 + "?attentionId="
							+ userID + "&downTime=" + downSchDate;
					downRepPath = URLConstants.新版发现下行重复 + "?attentionId="
							+ userID + "&downTime=" + downRepDate;
					upRepPath = URLConstants.新版发现重复上传;
					upSchPath = URLConstants.新版发现普通上传;
				} else {
					stopSelf();
				}
				if ("up".equals(intent.getAction())) {
					UpLoadData();
					if (!"".equals(upRepString)) {
						UpRepData(upRepPath, upRepString);
					} else if (!"".equals(upSchString)) {
						UpSchData(upSchPath, upSchString);
					}
				} else if ("down".equals(intent.getAction())) {
					DownRepData(downRepPath);
				} else {
					UpLoadData();
					if (!"".equals(upRepString)) {
						UpRepData(upRepPath, upRepString);
					} else if (!"".equals(upSchString)) {
						UpSchData(upSchPath, upSchString);
					}
					DownRepData(downRepPath);
				}
			}
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	private void UpSchData(final String path, final String json) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								UpNewFocusShareBackBean backBean = gson
										.fromJson(result,
												UpNewFocusShareBackBean.class);
								if (backBean.status == 0) {
									List<UpNewFocusShareBean> list = backBean.list;
									if (list != null && list.size() > 0) {
										for (UpNewFocusShareBean bean : list) {
											if (bean.state == 0) {
												if (bean.dataState == 0) {
													if (NewFocusShareRiChengActivity.focusSchList != null
															&& NewFocusShareRiChengActivity.focusSchList
																	.size() > 0) {
														NewFocusShareRiChengActivity.focusSchList
																.clear();
													}
													Map<String, String> map = new HashMap<String, String>();
													map.put(String
															.valueOf(bean.originalId),
															String.valueOf(bean.id));
													NewFocusShareRiChengActivity.focusSchList
															.add(map);
													app.updateNewFocusShareStateData(
															0, bean.originalId,
															bean.id, 0, 0);
												} else if (bean.dataState == 1) {
													app.updateNewFocusShareStateData(
															0, bean.originalId,
															bean.id, 0, 0);
												} else if (bean.dataState == 2) {
													app.deleteNewFocusShareData(
															6, 0, bean.id, 0,
															"");
												} else {

												}
											}
										}
									}
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
				Map<String, String> map = new HashMap<String, String>();
				map.put("data", json);
				return map;
			}
		};
		request.setTag("up");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void UpRepData(String path, final String json) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								UpNewFocusShareBackBean backBean = gson
										.fromJson(result,
												UpNewFocusShareBackBean.class);
								if (backBean.status == 0) {
									List<UpNewFocusShareBean> list = backBean.list;
									if (list != null && list.size() > 0) {
										for (UpNewFocusShareBean bean : list) {
											if (bean.state == 0) {
												if (bean.dataState == 0) {
													if (NewFocusShareRepeatActivity.focusRepList != null
															&& NewFocusShareRepeatActivity.focusRepList
																	.size() > 0) {
														NewFocusShareRepeatActivity.focusRepList
																.clear();
													}
													Map<String, String> map = new HashMap<String, String>();
													map.put(String
															.valueOf(bean.originalId),
															String.valueOf(bean.id));
													NewFocusShareRepeatActivity.focusRepList
															.add(map);
													app.updateNewFocusShareStateData(
															1, 0, 0,
															bean.originalId,
															bean.id);
													app.updateNewFocusShareStateData(
															3, 0, 0,
															bean.originalId,
															bean.id);
												} else if (bean.dataState == 1) {
													app.updateNewFocusShareStateData(
															1, 0, 0,
															bean.originalId,
															bean.id);
													app.updateNewFocusShareStateData(
															3, 0, 0,
															bean.originalId,
															bean.id);
												} else if (bean.dataState == 2) {
													app.deleteNewFocusShareData(
															7, 0, 0, bean.id,
															"");
													app.deleteNewFocusShareData(
															8, 0, 0, bean.id,
															"");
												} else {

												}
											}
										}
									}
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						if (!"".equals(upSchString)) {
							UpSchData(upSchPath, upSchString);
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						if (!"".equals(upSchString)) {
							UpSchData(upSchPath, upSchString);
						}
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("data", json);
				return map;
			}
		};
		request.setTag("up");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void DownSchData(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						List<NewMyFoundShouChangDingYueListBeen> addList = new ArrayList<NewMyFoundShouChangDingYueListBeen>();
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								NewMyFoundShouChangDingYueBeen backbean = gson
										.fromJson(
												result,
												NewMyFoundShouChangDingYueBeen.class);
								sharedPrefUtil.putString(getApplication(),
										ShareFile.USERFILE,
										ShareFile.DOWNNEWFOCUSSHARESCHDATE,
										backbean.downTime.replace("T", " "));
								List<NewFocusDeleteRepDataBean> deleterepList = null;
								List<NewFocusDeleteSchDataBean> deleteschList = null;
								if (backbean.status == 0) {
									addList.clear();
									addList = backbean.list;
									deleteschList = backbean.delList;
									deleterepList = backbean.tDelList;
									if (deleteschList != null
											&& deleteschList.size() > 0) {
										for (NewFocusDeleteSchDataBean schDataBean : deleteschList) {
											if (schDataBean.type == 7) {// 日程
												app.deleteNewFocusShareData(0,
														schDataBean.uId,
														schDataBean.dataId, 0,
														"");
											} else if (schDataBean.type == 8) {// 重复

											}
										}
									}
									if (addList != null && addList.size() > 0) {
										for (NewMyFoundShouChangDingYueListBeen been : addList) {
											if (!"".equals(StringUtils
													.getIsStringEqulesNull(been.CAlarmsoundDesc))) {
												ringdesc = been.CAlarmsoundDesc;
											}
											if (!"".equals(StringUtils
													.getIsStringEqulesNull(been.CAlarmsound))) {
												ringcode = been.CAlarmsound;
											}
											int count = app
													.CheckCountFromFocusShareData(
															0, been.CId,
															been.CUid, 0);
											if (count == 0) {
												app.insertNewFocusFromIntenetData(
														been.CUid,
														been.CId,
														0,
														been.CBefortime,
														been.CIsAlarm,
														been.CDisplayAlarm,
														been.CColorType,
														been.CPostpone,
														been.CImportant,
														been.CIsEnd,
														been.CType,
														0,
														been.COpenstate,
														0,
														been.CRecommendId,
														been.schRead,
														been.attentionid,
														0,
														0,
														0,
														0,
														0,
														0,
														been.atype,
														0,
														"",
														been.CContent,
														DateUtil.formatDate(DateUtil
																.parseDate(been.CDate)),
														DateUtil.formatDateTimeHm(DateUtil
																.parseDateTimeHm(been.CTime)),
														ringcode,
														ringdesc,
														StringUtils
																.getIsStringEqulesNull(been.CTags),
														StringUtils
																.getIsStringEqulesNull(been.CTypeDesc),
														StringUtils
																.getIsStringEqulesNull(been.CTypeSpare),
														"",
														"",
														"",
														"",
														"",
														"",
														"",
														StringUtils
																.getIsStringEqulesNull(been.CRecommendName),
														StringUtils
																.getIsStringEqulesNull(been.webUrl),
														StringUtils
																.getIsStringEqulesNull(been.imgPath),
														"",
														been.CCreateTime
																.replace("T",
																		" "),
														been.CUpdateTime
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(been.remark1),
														StringUtils
																.getIsStringEqulesNull(been.remark2),
														StringUtils
																.getIsStringEqulesNull(been.remark3),
														StringUtils
																.getIsStringEqulesNull(been.remark4),
														StringUtils
																.getIsStringEqulesNull(been.remark5));
											} else {
												app.updateNewFocusFromIntenetData(
														been.CUid,
														been.CId,
														0,
														been.CBefortime,
														been.CIsAlarm,
														been.CDisplayAlarm,
														been.CColorType,
														been.CPostpone,
														been.CImportant,
														been.CIsEnd,
														been.CType,
														0,
														been.COpenstate,
														0,
														been.CRecommendId,
														been.schRead,
														been.attentionid,
														0,
														0,
														0,
														0,
														0,
														0,
														been.atype,
														0,
														"",
														been.CContent,
														DateUtil.formatDate(DateUtil
																.parseDate(been.CDate)),
														DateUtil.formatDateTimeHm(DateUtil
																.parseDateTimeHm(been.CTime)),
														ringcode,
														ringdesc,
														StringUtils
																.getIsStringEqulesNull(been.CTags),
														StringUtils
																.getIsStringEqulesNull(been.CTypeDesc),
														StringUtils
																.getIsStringEqulesNull(been.CTypeSpare),
														"",
														"",
														"",
														"",
														"",
														"",
														"",
														StringUtils
																.getIsStringEqulesNull(been.CRecommendName),
														StringUtils
																.getIsStringEqulesNull(been.webUrl),
														StringUtils
																.getIsStringEqulesNull(been.imgPath),
														"",
														been.CCreateTime
																.replace("T",
																		" "),
														been.CUpdateTime
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(been.remark1),
														StringUtils
																.getIsStringEqulesNull(been.remark2),
														StringUtils
																.getIsStringEqulesNull(been.remark3),
														StringUtils
																.getIsStringEqulesNull(been.remark4),
														StringUtils
																.getIsStringEqulesNull(been.remark5));
											}
										}
									}
								}
							} catch (JsonSyntaxException e) {
								Intent intent = new Intent();
								intent.setAction(NewFocusShareService.SCHUPDATADATA);
								intent.putExtra("data", "fail");
								sendBroadcast(intent);
								e.printStackTrace();
							}
						}
						if ((addList != null && addList.size() > 0)
								|| IsRepeat == 1) {
							Intent intent = new Intent();
							intent.setAction(NewFocusShareService.SCHUPDATADATA);
							intent.putExtra("data", "success");
							sendBroadcast(intent);
						} else {
							Intent intent = new Intent();
							intent.setAction(NewFocusShareService.SCHUPDATADATA);
							intent.putExtra("data", "fail");
							sendBroadcast(intent);
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Intent intent = new Intent();
						intent.setAction(NewFocusShareService.SCHUPDATADATA);
						intent.putExtra("data", "fail");
						sendBroadcast(intent);
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void DownRepData(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							List<NewFocusShareRepeatBean> addList = new ArrayList<NewFocusShareRepeatBean>();
							try {
								Gson gson = new Gson();
								NewFocusShareRepeatBackBean backBean = gson
										.fromJson(
												result,
												NewFocusShareRepeatBackBean.class);
								sharedPrefUtil.putString(getApplication(),
										ShareFile.USERFILE,
										ShareFile.DOWNNEWFOCUSSHAREREPDATE,
										backBean.downTime.replace("T", " "));
								if (backBean.status == 0) {
									addList.clear();
									List<NewFocusDeleteRepDataBean> deleterepList = null;
									List<NewFocusDeleteSchDataBean> deleteschList = null;
									addList = backBean.list;
									deleteschList = backBean.delList;
									deleterepList = backBean.tDelList;
									if (deleteschList != null
											&& deleteschList.size() > 0) {
										for (NewFocusDeleteSchDataBean schDataBean : deleteschList) {
											if (schDataBean.type == 7) {// 日程
											} else if (schDataBean.type == 8) {// 重复
												app.deleteNewFocusShareData(1,
														schDataBean.uId, 0,
														schDataBean.dataId, "");
												app.deleteNewFocusShareData(2,
														schDataBean.uId, 0,
														schDataBean.dataId, "");
											}
										}
									}
									if (addList != null && addList.size() > 0) {
										IsRepeat = 1;
										for (NewFocusShareRepeatBean bean : addList) {
											app.deleteNewFocusShareData(2,
													bean.repUid, 0, bean.repId,
													"");
											if (!"".equals(StringUtils
													.getIsStringEqulesNull(bean.repRingDesc))) {
												ringdesc = bean.repRingDesc;
											}
											if (!"".equals(StringUtils
													.getIsStringEqulesNull(bean.repRingCode))) {
												ringcode = bean.repRingCode;
											}
											int count = app
													.CheckCountFromFocusShareData(
															1, bean.repId,
															bean.repUid,
															bean.repId);
											if (count == 0) {
												app.insertNewFocusFromIntenetRepeatData(
														bean.repUid,
														1,
														bean.repBeforeTime,
														bean.repIsAlarm,
														bean.repDisplayTime,
														bean.repColorType,
														0,
														bean.repIsImportant,
														0,
														bean.repSourceType,
														bean.repId,
														bean.repOpenState,
														0,
														bean.recommendedUserId,
														bean.repRead,
														0,
														bean.repIsPuase,
														bean.repstateone,
														bean.repstatetwo,
														bean.repInStable,
														0,
														bean.repType,
														bean.atype,
														0,
														bean.repTypeParameter,
														bean.repContent,
														DateUtil.formatDate(new Date()),
														DateUtil.formatDateTimeHm(DateUtil
																.parseDateTimeHm(bean.repTime)),
														ringcode,
														ringdesc,
														StringUtils
																.getIsStringEqulesNull(bean.ctags),
														StringUtils
																.getIsStringEqulesNull(bean.repSourceDesc),
														StringUtils
																.getIsStringEqulesNull(bean.repSourceDescSpare),
														"",
														StringUtils
																.getIsStringEqulesNull(
																		bean.repStartDate)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repNextCreatedTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repLastCreatedTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repInitialCreatedTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repdateone)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repdatetwo)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(bean.recommendedUserName),
														StringUtils
																.getIsStringEqulesNull(bean.webUrl),
														StringUtils
																.getIsStringEqulesNull(bean.imgPath),
														StringUtils
																.getIsStringEqulesNull(bean.parReamrk),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repCreateTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repUpdateTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(bean.remark1),
														StringUtils
																.getIsStringEqulesNull(bean.remark2),
														StringUtils
																.getIsStringEqulesNull(bean.remark3),
														StringUtils
																.getIsStringEqulesNull(bean.remark4),
														StringUtils
																.getIsStringEqulesNull(bean.remark5));
											} else {
												app.updateNewFocusFromIntenetData(
														bean.repUid,
														bean.repId,
														2,
														bean.repBeforeTime,
														bean.repIsAlarm,
														bean.repDisplayTime,
														bean.repColorType,
														0,
														bean.repIsImportant,
														0,
														bean.repSourceType,
														bean.repId,
														bean.repOpenState,
														0,
														bean.recommendedUserId,
														bean.repRead,
														0,
														bean.repIsPuase,
														bean.repstateone,
														bean.repstatetwo,
														bean.repInStable,
														0,
														bean.repType,
														bean.atype,
														0,
														bean.repTypeParameter,
														bean.repContent,
														DateUtil.formatDate(new Date()),
														DateUtil.formatDateTimeHm(DateUtil
																.parseDateTimeHm(bean.repTime)),
														ringcode,
														ringdesc,
														StringUtils
																.getIsStringEqulesNull(bean.ctags),
														StringUtils
																.getIsStringEqulesNull(bean.repSourceDesc),
														StringUtils
																.getIsStringEqulesNull(bean.repSourceDescSpare),
														"",
														StringUtils
																.getIsStringEqulesNull(
																		bean.repStartDate)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repNextCreatedTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repLastCreatedTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repInitialCreatedTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repdateone)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repdatetwo)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(bean.recommendedUserName),
														StringUtils
																.getIsStringEqulesNull(bean.webUrl),
														StringUtils
																.getIsStringEqulesNull(bean.imgPath),
														StringUtils
																.getIsStringEqulesNull(bean.parReamrk),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repCreateTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(
																		bean.repUpdateTime)
																.replace("T",
																		" "),
														StringUtils
																.getIsStringEqulesNull(bean.remark1),
														StringUtils
																.getIsStringEqulesNull(bean.remark2),
														StringUtils
																.getIsStringEqulesNull(bean.remark3),
														StringUtils
																.getIsStringEqulesNull(bean.remark4),
														StringUtils
																.getIsStringEqulesNull(bean.remark5));
											}
											CheckCreateRepeatSchData(bean);
										}
										Intent intent = new Intent();
										intent.setAction(NewFocusShareService.REPUPDATADATA);
										intent.putExtra("data", "success");
										sendBroadcast(intent);
									}
								} else {
									Intent intent = new Intent();
									intent.setAction(NewFocusShareService.REPUPDATADATA);
									intent.putExtra("data", "fail");
									sendBroadcast(intent);
								}
							} catch (JsonSyntaxException e) {
								Intent intent = new Intent();
								intent.setAction(NewFocusShareService.REPUPDATADATA);
								intent.putExtra("data", "fail");
								sendBroadcast(intent);
								DownSchData(downSchPath);
								e.printStackTrace();
							}
						}
						DownSchData(downSchPath);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Intent intent = new Intent();
						intent.setAction(NewFocusShareService.REPUPDATADATA);
						intent.putExtra("data", "fail");
						sendBroadcast(intent);
						DownSchData(downSchPath);
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
		App.getHttpQueues().cancelAll("up");
	}

	/**
	 * 生成子记事对应的时间
	 */
	public static RepeatBean getNextChildTime(NewFocusShareRepeatBean bean) {
		RepeatBean repeatBean = null;
		if (1 == bean.repType) {
			repeatBean = RepeatDateUtils.saveCalendar(DateUtil
					.formatDateTimeHm(DateUtil.parseDateTimeHm(bean.repTime)),
					1, "", "");
		} else if (2 == bean.repType) {
			repeatBean = RepeatDateUtils.saveCalendar(
					DateUtil.formatDateTimeHm(DateUtil
							.parseDateTimeHm(bean.repTime)),
					2,
					StringUtils.getIsStringEqulesNull(bean.repTypeParameter)
							.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if (3 == bean.repType) {
			repeatBean = RepeatDateUtils.saveCalendar(
					DateUtil.formatDateTimeHm(DateUtil
							.parseDateTimeHm(bean.repTime)),
					3,
					StringUtils.getIsStringEqulesNull(bean.repTypeParameter)
							.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "");
		} else if (4 == bean.repType) {
			repeatBean = RepeatDateUtils.saveCalendar(
					DateUtil.formatDateTimeHm(DateUtil
							.parseDateTimeHm(bean.repTime)),
					4,
					StringUtils.getIsStringEqulesNull(bean.repTypeParameter)
							.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "0");
		} else if (6 == bean.repType) {
			repeatBean = RepeatDateUtils.saveCalendar(
					DateUtil.formatDateTimeHm(DateUtil
							.parseDateTimeHm(bean.repTime)),
					4,
					StringUtils.getIsStringEqulesNull(bean.repTypeParameter)
							.replace("[", "").replace("]", "")
							.replace("\n\"", "").replace("\n", "")
							.replace("\"", ""), "1");
		} else {
			repeatBean = RepeatDateUtils.saveCalendar(DateUtil
					.formatDateTimeHm(DateUtil.parseDateTimeHm(bean.repTime)),
					5, "", "");
		}
		return repeatBean;
	}

	/**
	 * 查询表中重复母记事生成对应的子记事
	 */
	public void CheckCreateRepeatSchData(NewFocusShareRepeatBean been) {
		if (been != null) {
			if (been.repstateone == 0 && been.repstatetwo == 0) {
				if (DateUtil.formatDate(
						DateUtil.parseDateTime(been.repInitialCreatedTime
								.replace("T", " "))).equals(
						DateUtil.formatDate(new Date()))) {

					if (DateUtil.parseDateTime(
							been.repInitialCreatedTime.replace("T", " "))
							.after(DateUtil.parseDateTime(DateUtil
									.formatDateTime(new Date())))) {
						if (0 == been.repInStable) {
							CreateNextChildData(been);
						}
					} else if (DateUtil.parseDateTime(
							been.repNextCreatedTime.replace("T", " ")).equals(
							DateUtil.parseDateTime(DateUtil
									.formatDateTime(new Date())))) {
						if (0 == been.repInStable) {
							CreateNextChildData(been);
						}
					} else {
						if (0 == been.repInStable) {
							CreateNextChildData(been);
						}
					}
				} else {
					if (DateUtil.parseDateTime(
							been.repNextCreatedTime.replace("T", " ")).after(
							DateUtil.parseDateTime(DateUtil
									.formatDateTime(new Date())))) {
						if (0 == been.repInStable) {
							CreateNextChildData(been);
						}
					} else if (DateUtil.parseDateTime(
							been.repNextCreatedTime.replace("T", " ")).equals(
							DateUtil.parseDateTime(DateUtil
									.formatDateTime(new Date())))) {
						if (0 == been.repInStable) {
							CreateNextChildData(been);
						}
					} else {
						if (0 == been.repInStable) {
							CreateNextChildData(been);
						}
					}
				}
			} else {
				if (!getNextChildTime(been).repLastCreatedTime
						.equals(been.repdateone.replace("T", " "))
						&& !getNextChildTime(been).repLastCreatedTime
								.equals(been.repdatetwo.replace("T", " "))) {
					if (DateUtil.parseDateTime(
							getNextChildTime(been).repLastCreatedTime).before(
							DateUtil.parseDateTime(been.repInitialCreatedTime
									.replace("T", " ")))) {
					} else {
					}
				} else if (getNextChildTime(been).repLastCreatedTime
						.equals(been.repdateone.replace("T", " "))) {
					if (been.repstateone == 1) {
					} else if (been.repstateone == 2) {
					}
					if (been.repstateone == 3) {
					} else if (been.repInStable == 0) {
					}
				} else if (getNextChildTime(been).repLastCreatedTime
						.equals(been.repdatetwo.replace("T", " "))) {
					if (been.repstatetwo == 1) {
					} else if (been.repstatetwo == 2) {
					}
					if (been.repstatetwo == 3) {
					} else if (been.repstatetwo == 0) {
					}
				}
				if (!getNextChildTime(been).repNextCreatedTime
						.equals(been.repdateone.replace("T", " "))
						&& !getNextChildTime(been).repNextCreatedTime
								.equals(been.repdatetwo.replace("T", " "))) {
					if (DateUtil.parseDateTime(
							getNextChildTime(been).repNextCreatedTime).before(
							DateUtil.parseDateTime(been.repInitialCreatedTime
									.replace("T", " ")))) {
					} else {
						if (0 == been.repstatetwo) {
							CreateNextChildData(been);
						} else {
							CreateNextChildData(been);
						}
					}
				} else if (getNextChildTime(been).repNextCreatedTime
						.equals(been.repdateone.replace("T", " "))) {
					if (been.repstateone == 1) {
					} else if (been.repstateone == 2) {
					}
					if (been.repstateone == 3) {
						CreateNextChildEndData(been);

					} else if (been.repstateone == 0) {
						CreateNextChildData(been);
					}
				} else if (getNextChildTime(been).repNextCreatedTime
						.equals(been.repdatetwo.replace("T", " "))) {
					if (been.repstatetwo == 1) {
					} else if (been.repstatetwo == 2) {
					}
					if (been.repstatetwo == 3) {
						CreateNextChildEndData(been);
					} else if (been.repstatetwo == 0) {
						CreateNextChildData(been);
					}
				}
			}
		}
	}

	/**
	 * 生成子记事
	 */
	public void CreateNextChildData(NewFocusShareRepeatBean bean) {
		app.insertNewFocusData(
				bean.repUid,
				0,
				bean.repBeforeTime,
				bean.repIsAlarm,
				bean.repDisplayTime,
				bean.repColorType,
				0,
				bean.repIsImportant,
				0,
				bean.repSourceType,
				bean.repId,
				bean.repOpenState,
				1,
				bean.recommendedUserId,
				bean.repRead,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				bean.atype,
				0,
				"",
				bean.repContent,
				getNextChildTime(bean).repNextCreatedTime.substring(0, 10),
				StringUtils.getIsStringEqulesNull(bean.repTime),
				StringUtils.getIsStringEqulesNull(bean.repRingCode),
				StringUtils.getIsStringEqulesNull(bean.repRingDesc),
				StringUtils.getIsStringEqulesNull(bean.ctags),
				StringUtils.getIsStringEqulesNull(bean.repSourceDesc),
				StringUtils.getIsStringEqulesNull(bean.repSourceDescSpare),
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repLastCreatedTime,
				"",
				"",
				"",
				StringUtils.getIsStringEqulesNull(bean.recommendedUserName),
				StringUtils.getIsStringEqulesNull(bean.webUrl),
				StringUtils.getIsStringEqulesNull(bean.imgPath),
				StringUtils.getIsStringEqulesNull(bean.parReamrk),
				StringUtils.getIsStringEqulesNull(bean.repCreateTime).replace(
						"T", " "),
				StringUtils.getIsStringEqulesNull(bean.repUpdateTime).replace(
						"T", " "), StringUtils
						.getIsStringEqulesNull(bean.remark1), StringUtils
						.getIsStringEqulesNull(bean.remark2), StringUtils
						.getIsStringEqulesNull(bean.remark3), StringUtils
						.getIsStringEqulesNull(bean.remark4), StringUtils
						.getIsStringEqulesNull(bean.remark5));
	}

	/**
	 * 生成结束子记事
	 */
	public void CreateNextChildEndData(NewFocusShareRepeatBean bean) {
		app.insertNewFocusData(
				bean.repUid,
				0,
				bean.repBeforeTime,
				bean.repIsAlarm,
				bean.repDisplayTime,
				bean.repColorType,
				0,
				bean.repIsImportant,
				1,
				bean.repSourceType,
				bean.repId,
				bean.repOpenState,
				1,
				bean.recommendedUserId,
				bean.repRead,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				bean.atype,
				0,
				"",
				bean.repContent,
				getNextChildTime(bean).repNextCreatedTime.substring(0, 10),
				StringUtils.getIsStringEqulesNull(bean.repTime),
				StringUtils.getIsStringEqulesNull(bean.repRingCode),
				StringUtils.getIsStringEqulesNull(bean.repRingDesc),
				StringUtils.getIsStringEqulesNull(bean.ctags),
				StringUtils.getIsStringEqulesNull(bean.repSourceDesc),
				StringUtils.getIsStringEqulesNull(bean.repSourceDescSpare),
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repLastCreatedTime,
				"",
				"",
				"",
				StringUtils.getIsStringEqulesNull(bean.recommendedUserName),
				StringUtils.getIsStringEqulesNull(bean.webUrl),
				StringUtils.getIsStringEqulesNull(bean.imgPath),
				StringUtils.getIsStringEqulesNull(bean.parReamrk),
				StringUtils.getIsStringEqulesNull(bean.repCreateTime).replace(
						"T", " "),
				StringUtils.getIsStringEqulesNull(bean.repUpdateTime).replace(
						"T", " "), StringUtils
						.getIsStringEqulesNull(bean.remark1), StringUtils
						.getIsStringEqulesNull(bean.remark2), StringUtils
						.getIsStringEqulesNull(bean.remark3), StringUtils
						.getIsStringEqulesNull(bean.remark4), StringUtils
						.getIsStringEqulesNull(bean.remark5));
	}
}
