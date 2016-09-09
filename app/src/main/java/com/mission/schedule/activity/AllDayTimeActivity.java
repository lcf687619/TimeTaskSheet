package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.view.CycleWheelView;
import com.mission.schedule.view.CycleWheelView.CycleWheelViewException;
import com.mission.schedule.view.CycleWheelView.WheelItemSelectedListener;
import com.mission.schedule.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AllDayTimeActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.toggle_time)
	private ToggleButton toggle_time;
	@ViewResId(id = R.id.time_tv)
	private TextView time_tv;
	@ViewResId(id = R.id.time_rl)
	private RelativeLayout time_rl;
	@ViewResId(id = R.id.hour_cy)
	private CycleWheelView hour_cy;
	@ViewResId(id = R.id.min_cy)
	private CycleWheelView min_cy;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	String alltime;
	String allstate;
	boolean allFlag = false;

	private String timeSet = "";
	String hour;
	String minute;
	String state = "0";

	ProgressUtil progressUtil = new ProgressUtil();

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_alldaytime);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		middle_tv.setText("全天响铃时间设置");
		alltime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ALLTIME, "08:58");
		allstate = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ALLSTATE, "0");
		time_tv.setText(alltime);

		if ("0".equals(allstate)) {
			allFlag = true;
		} else {
			allFlag = false;
		}
		toggle_time.setChecked(allFlag);
		toggle_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					state = "0";
				} else {
					state = "1";
				}
				if (isChecked) {
					// 选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.ALLSTATE, state);
				} else {
					// 未选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.ALLSTATE, state);
				}
			}
		});

		initdata();
	}

	private void initdata() {
		timeSet = time_tv.getText().toString();
		List<String> hours = new ArrayList<String>();
		List<String> minutes = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			hours.add(i < 10 ? "0" + i : "" + i);

		}
		for (int i = 0; i < 60; i++) {
			minutes.add(i < 10 ? "0" + i : "" + i);
		}
		// hour_pv.setData(hours);
		// hour_pv.setOnSelectListener(new onSelectListener()
		// {
		//
		// @Override
		// public void onSelect(String text)
		// {
		// hour = Integer.parseInt(text)<10?"0"+Integer.parseInt(text):text;
		// minute =
		// Integer.parseInt(minute)<10?"0"+Integer.parseInt(minute):minute;
		// timeSet = hour+":"+minute;
		// time_tv.setText(timeSet);
		// }
		// });
		// minute_pv.setData(minutes);
		// minute_pv.setOnSelectListener(new onSelectListener()
		// {
		//
		// @Override
		// public void onSelect(String text)
		// {
		// hour = Integer.parseInt(hour)<10?"0"+Integer.parseInt(hour):hour;
		// minute = Integer.parseInt(text)<10?"0"+Integer.parseInt(text):text;;
		// timeSet = hour+":"+minute;
		// time_tv.setText(timeSet);
		// }
		// });
		int curHours = Integer.parseInt(timeSet.split(":")[0]);
		int curMinutes = Integer.parseInt(timeSet.split(":")[1]);
		hour = curHours + "";
		minute = curMinutes + "";
		int currentHoursIndex = 0;
		int currentMinutesIndex = 0;
		for (int i = 0; i < hours.size(); i++) {
			if (curHours == Integer.parseInt(hours.get(i))) {
				currentHoursIndex = i;
			}
		}
		for (int i = 0; i < minutes.size(); i++) {
			if (curMinutes == Integer.parseInt(minutes.get(i))) {
				currentMinutesIndex = i;
			}
		}
		hour_cy.setLabels(hours);
		try {
			hour_cy.setWheelSize(5);
		} catch (CycleWheelViewException e) {
			e.printStackTrace();
		}
		hour_cy.setCycleEnable(true);
		hour_cy.setSelection(currentHoursIndex);
		hour_cy.setAlphaGradual(0.6f);
		hour_cy.setDivider(getResources().getColor(R.color.gongkai_txt), 1);
		hour_cy.setSolid(Color.WHITE, Color.WHITE);
		hour_cy.setLabelColor(getResources().getColor(R.color.gongkai_txt));
		hour_cy.setLabelSelectColor(Color.BLACK);
		hour_cy.setOnWheelItemSelectedListener(new WheelItemSelectedListener() {
			@Override
			public void onItemSelected(int position, String label) {
				hour = Integer.parseInt(label) < 10 ? "0"
						+ Integer.parseInt(label) : label;
				minute = Integer.parseInt(minute) < 10 ? "0"
						+ Integer.parseInt(minute) : minute;
				timeSet = hour + ":" + minute;
				time_tv.setText(timeSet);
			}
		});

		min_cy.setLabels(minutes);
		try {
			min_cy.setWheelSize(5);
		} catch (CycleWheelViewException e) {
			e.printStackTrace();
		}
		min_cy.setCycleEnable(true);
		min_cy.setSelection(currentMinutesIndex);
		min_cy.setAlphaGradual(0.6f);
		min_cy.setDivider(getResources().getColor(R.color.gongkai_txt), 1);
		min_cy.setSolid(Color.WHITE, Color.WHITE);
		min_cy.setLabelColor(getResources().getColor(R.color.gongkai_txt));
		min_cy.setLabelSelectColor(Color.BLACK);
		min_cy.setOnWheelItemSelectedListener(new WheelItemSelectedListener() {
			@Override
			public void onItemSelected(int position, String label) {
				hour = Integer.parseInt(hour) < 10 ? "0"
						+ Integer.parseInt(hour) : hour;
				minute = Integer.parseInt(label) < 10 ? "0"
						+ Integer.parseInt(label) : label;
				timeSet = hour + ":" + minute;
				time_tv.setText(timeSet);
			}
		});
		// for(int i=0;i<hours.size();i++){
		// if(curHours==Integer.parseInt(hours.get(i))){
		// hour_pv.setSelected(i);
		// }
		// }
		// for(int i=0;i<minutes.size();i++){
		// if(curMinutes==Integer.parseInt(minutes.get(i))){
		// minute_pv.setSelected(i);
		// }
		// }
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
		case R.id.top_ll_right:
			if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.ALLTIME, time_tv.getText().toString());
				AlterSet();
			} else {
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.ALLTIME, time_tv.getText().toString());
			}
			QueryAlarmData.writeAlarm(getApplicationContext());
			this.finish();
			break;
		default:
			break;
		}
	}

	private void AlterSet() {
		String path = URLConstants.修改用户设置;
		AlterSetAsync(path);
	}

	private void AlterSetAsync(String path) {
		progressUtil.ShowProgress(context, true, true, "正在保存...");
		StringRequest request = new StringRequest(Method.POST, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								SuccessOrFailBean bean = gson.fromJson(result,
										SuccessOrFailBean.class);
								if (bean.status == 0) {
									finish();
								} else {
									finish();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}

						} else {
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
					}
				}){
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("tbUserMannge.uid", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.USERID,
						"0"));
				pairs.put("tbUserMannge.id", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.SETID,
						"0"));
				pairs.put("tbUserMannge.openState", "0");
				pairs.put("tbUserMannge.ringCode", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.MUSICCODE, "g_88"));
				pairs.put("tbUserMannge.ringDesc", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.MUSICDESC, "完成任务"));
				pairs.put("tbUserMannge.beforeTime", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.BEFORETIME, "0"));
				pairs.put("tbUserMannge.morningState", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.MORNINGSTATE, "0"));
				pairs.put("tbUserMannge.morningTime", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.MORNINGTIME, "07:58"));
				pairs.put("tbUserMannge.nightState", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.NIGHTSTATE, "0"));
				pairs.put("tbUserMannge.nightTime", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.NIGHTTIME, "20:58"));
				pairs.put("tbUserMannge.dayTime", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.ALLTIME, "08:58"));
				pairs.put("tbUserMannge.dayState", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.ALLSTATE, "0"));
				return pairs;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
