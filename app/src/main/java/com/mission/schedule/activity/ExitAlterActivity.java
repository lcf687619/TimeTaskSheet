package com.mission.schedule.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.iflytek.thirdparty.P;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatUpLoadBackBean;
import com.mission.schedule.bean.UpLoadBackBean;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/22.
 */
public class ExitAlterActivity extends BaseActivity implements View.OnClickListener {

    @ViewResId(id = R.id.content_tv)
    private TextView content_tv;
    @ViewResId(id = R.id.update_bt)
    private Button update_bt;
    @ViewResId(id = R.id.exit_tv)
    private TextView exit_tv;
    @ViewResId(id = R.id.cancle_tv)
    private TextView cancle_tv;

    Context context;
    ActivityManager1 activityManager = null;
    SharedPrefUtil sharedPrefUtil = null;
    ProgressUtil progressUtil = null;
    String startContentStr = "";
    String redContentStr = "";
    String endContentStr = "";

    App application = null;
    List<Map<String, String>> upList;
    List<Map<String, String>> upRepeatList;
    String schuppath = "";
    String schjson = "";
    String repeatUpPath = "";
    String repJson = "";
    String UserId = "0";

    @Override
    protected void setListener() {
        update_bt.setOnClickListener(this);
        exit_tv.setOnClickListener(this);
        cancle_tv.setOnClickListener(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_exitalter);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        context = this;
        activityManager = ActivityManager1.getInstance();
        activityManager.addActivities(this);
        application = App.getDBcApplication();
        sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        UserId = sharedPrefUtil.getString(context, ShareFile.USERFILE, ShareFile.USERID, "0");
        startContentStr = "<font color='" + context.getResources().getColor(R.color.black) + "'>"
                + "本地还有" + "</font>";
        redContentStr = "<font color='" + context.getResources().getColor(R.color.sunday_txt) + "'>"
                + "未同步的数据" + "</font>";
        endContentStr = "<font color='" + context.getResources().getColor(R.color.black) + "'>"
                + "，如果继续退出，数据将会丢失。请您在网络畅通的环境，先上传数据，再退出登录。" + "</font>";
        content_tv.setText(Html.fromHtml(startContentStr + redContentStr + endContentStr));
    }

    @Override
    protected void setAdapter() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_bt:
                if(NetUtil.getConnectState(context)!= NetUtil.NetWorkState.NONE){
                    progressUtil = new ProgressUtil();
                    progressUtil.ShowProgress(context, true, true, "正在同步");
                    updateLoadData();
                }else {
                    Toast.makeText(context,"请检查您的网络!",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.exit_tv:
                clearMessage();
                break;
            case R.id.cancle_tv:
                this.finish();
                break;
        }
    }
    private void clearMessage(){
        WriteAlarmClock.clearAlarmClock(getApplicationContext());
        if (!"".equals(sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.TELEPHONE, ""))) {
            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                    ShareFile.TELEPHONE, "");
        } else {
            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                    ShareFile.USEREMAIL, "");
        }
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.USERID, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.USERNAME, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.USERPHOTOPATH, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.USERSTATE, "0");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.FIRSTLOGIN, "0");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.U_ACC_NO, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.MAXFOCUSID, "0");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.OUTWEEKFAG, "0");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.UPDATESETTIME, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.DOWNSCHTIME, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.DOWNREPTIME, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.RINGSTATE, "0");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.ISYOUKE, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.DOWNTAGDATE, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.OPENSTYLESTATE, "2");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.FRIENDDOWNSCHTIME, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.FRIENDDOWNRepTIME, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.FRIENDDOWNOldTIME, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.PUSH_ALIAS, "0");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.REFRESHFRIEND, "0");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.FIRSTDOWNFOCUSSCH, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.SHOUCANGDATA, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.DOWNNEWFOCUSSHARESCHDATE, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.DOWNNEWFOCUSSHAREREPDATE, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.KuaiJieSouSuo, "");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.SHAREDATA, "");
        ClearMAC();
        startActivity(new Intent(context, NewTelephoneLoginActivity.class));
        activityManager.doAllActivityFinish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressUtil != null) {
            progressUtil.dismiss();
        }
        App.getHttpQueues().cancelAll("upload");
        App.getHttpQueues().cancelAll("down");
    }

    private void updateLoadData() {
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
            repeatUpPath = URLConstants.重复数据上传;
            // if (upRepeatList != null &&
            // upRepeatList.size() >
            // 0) {
            // NewRepeatAsync(repeatUpPath,
            // jsonobject2.toString());
            // }
            upList = application.queryAllSchData(-1, 0, 0);
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
    }

    private void NewRepeatAsync(final String path, final String json) {
        StringRequest request = new StringRequest(Request.Method.POST, path,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(final String result) {
                        if (!TextUtils.isEmpty(result)) {
                            Gson gson = new Gson();
                            RepeatUpLoadBackBean backBean = gson
                                    .fromJson(
                                            result,
                                            RepeatUpLoadBackBean.class);
                            if (backBean.status == 0) {
                                if (upList != null && upList.size() > 0) {
                                    UpLoadSch(schuppath, schjson);
                                } else {
                                    progressUtil.dismiss();
                                    clearMessage();
                                }
                            } else {
                                Toast.makeText(context,"同步失败,请重试!",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            progressUtil.dismiss();
                            Toast.makeText(context,"同步失败,请重试!",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressUtil.dismiss();
                Toast.makeText(context,"同步失败,请重试!",Toast.LENGTH_LONG).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(60000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void UpLoadSch(String path, final String json) {
        StringRequest request = new StringRequest(Request.Method.POST, path,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(final String result) {
                        if (!TextUtils.isEmpty(result)) {
                            Gson gson = new Gson();
                            UpLoadBackBean backBean = gson
                                    .fromJson(result,
                                            UpLoadBackBean.class);
                            if (backBean.status == 0) {
                                progressUtil.dismiss();
                                clearMessage();
                            } else {
                                progressUtil.dismiss();
                                Toast.makeText(context,"同步失败,请重试!",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            progressUtil.dismiss();
                            Toast.makeText(context,"同步失败,请重试!",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressUtil.dismiss();
                Toast.makeText(context,"同步失败,请重试!",Toast.LENGTH_LONG).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(60000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }
    /**
     * 修改推送mac地址置为空
     */
    private void ClearMAC() {
        if (NetUtil.getConnectState(context) != NetUtil.NetWorkState.NONE) {
            final String path = URLConstants.修改MAC地址 + UserId + "&uClintAddr="
                    + "" + "&uTocode=android";
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
}
