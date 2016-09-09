package com.mission.schedule.activity;

import java.io.File;
import java.util.HashMap;
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
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.service.ClockService;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;// 返回
	@ViewResId(id = R.id.personmessage_tv)
	private TextView personmessage_tv;// 个人信息
	@ViewResId(id = R.id.setbackground_tv)
	private TextView setbackground_tv;// 设置背景
	// @ViewResId(id = R.id.rl)
	// private RelativeLayout rl;// 谁可以见
	// @ViewResId(id = R.id.shuikejian_state_tv)
	// private TextView shuikejian_state_tv;// 谁可以见
	@ViewResId(id = R.id.rl1)
	private RelativeLayout rl1;// 默认铃声
	@ViewResId(id = R.id.ringname_tv)
	private TextView ringname_tv;
	@ViewResId(id = R.id.morenbeforetime_tv)
	private TextView morenbeforetime_tv;// 默认提前提醒时间
	@ViewResId(id = R.id.everydaywenhou_tv)
	private TextView everydaywenhou_tv;// 每天问候
	@ViewResId(id = R.id.alldaytixing_tv)
	private TextView alldaytixing_tv;// 全天的提醒时间
	// @ViewResId(id = R.id.addfocus_tv)
	// private TextView addfocus_tv;// 加入发现推荐榜
	@ViewResId(id = R.id.yijianfankui_tv)
	private TextView yijianfankui_tv;// 意见反馈
	@ViewResId(id = R.id.haoping_tv)
	private TextView haoping_tv;// 好评
	@ViewResId(id = R.id.aboutus_tv)
	private TextView aboutus_tv;// 关于我们
	@ViewResId(id = R.id.setendsound_tv)
	private TextView setendsound_tv;
	@ViewResId(id = R.id.updatedata_tv)
	private TextView updatedata_tv;
	@ViewResId(id = R.id.toggle_notifation)
	private ToggleButton toggle_notifation;

	Context context;
	// private final static int STATE_CHOOSE = 1;// 状态选择
	private static final int CHOOSE_MUSIC = 2;// 选择铃声
	private static final int BEFORETIME = 3;// 提前时间设置

	String ringcode;// 铃声对应的编码

	SharedPrefUtil sharedPrefUtil = null;
	String ringname;
	String beforetime;
	String isYouKe = "1";

	boolean isnotification = true;
	String notificationStr = "";
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		personmessage_tv.setOnClickListener(this);
		setbackground_tv.setOnClickListener(this);
		// rl.setOnClickListener(this);
		// shuikejian_state_tv.setOnClickListener(this);
		rl1.setOnClickListener(this);
		ringname_tv.setOnClickListener(this);
		morenbeforetime_tv.setOnClickListener(this);
		everydaywenhou_tv.setOnClickListener(this);
		alldaytixing_tv.setOnClickListener(this);
		// addfocus_tv.setOnClickListener(this);
		yijianfankui_tv.setOnClickListener(this);
		haoping_tv.setOnClickListener(this);
		aboutus_tv.setOnClickListener(this);
		setendsound_tv.setOnClickListener(this);
		updatedata_tv.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_setting);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		ringname = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICDESC, "完成任务");
		ringname_tv.setText(ringname);
		ringcode = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICCODE, "g_88");
		isYouKe = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ISYOUKE, "1");
		beforetime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.BEFORETIME, "0");
		notificationStr = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ISNOTIFICATION, "0");
		if ("0".equals(notificationStr)) {
			isnotification = true;
		} else {
			isnotification = false;
		}
		toggle_notifation.setChecked(isnotification);
		toggle_notifation
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 选中
							sharedPrefUtil.putString(context,
									ShareFile.USERFILE,
									ShareFile.ISNOTIFICATION, "0");
							Intent intent = new Intent(getApplicationContext(), ClockService.class);
							intent.putExtra("WriteAlarmClockwrite", "0");
							stopService(intent);
							startService(intent);
						} else {
							// 未选中
							sharedPrefUtil.putString(context,
									ShareFile.USERFILE,
									ShareFile.ISNOTIFICATION, "1");
							Intent intent = new Intent(getApplicationContext(), ClockService.class);
							intent.putExtra("WriteAlarmClockwrite", "0");
							stopService(intent);
							startService(intent);
						}
					}
				});
	}

	@Override
	protected void setAdapter() {
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.updatedata_tv:
			cancleDialog();
			break;
		case R.id.top_ll_back:
			this.finish();
			break;
		case R.id.personmessage_tv:// 个人信息
			if ("1".equals(isYouKe)) {
				startActivity(new Intent(context, PersonMessageActivity.class));
			} else {
				startActivity(new Intent(context, ResginActivity.class));
			}
			break;
		case R.id.setbackground_tv:// 设置背景
			startActivity(new Intent(context, SetBackgroundActivity.class));
			break;
		// case R.id.rl:// 谁可以看见
		// intent = new Intent(context, StateActivity.class);
		// intent.putExtra("statename", shuikejian_state_tv.getText()
		// .toString());
		// startActivityForResult(intent, STATE_CHOOSE);
		// break;
		// case R.id.shuikejian_state_tv:// 谁可见
		// intent = new Intent(context, StateActivity.class);
		// intent.putExtra("statename", shuikejian_state_tv.getText()
		// .toString());
		// startActivityForResult(intent, STATE_CHOOSE);
		// break;
		case R.id.rl1:// 默认铃声
			intent = new Intent(context, LingShengActivity.class);
			startActivityForResult(intent, CHOOSE_MUSIC);
			break;
		case R.id.ringname_tv:// 默认铃声
			intent = new Intent(context, LingShengActivity.class);
			startActivityForResult(intent, CHOOSE_MUSIC);
			break;
		case R.id.morenbeforetime_tv:// 默认提前提醒时间
			intent = new Intent(context, SetBeforeTimeActivity.class);
			intent.putExtra("time", beforetime);
			startActivityForResult(intent, BEFORETIME);
			break;
		case R.id.everydaywenhou_tv:// 每天问候时间
			startActivity(new Intent(context, EverydayTimeActivity.class));
			break;
		case R.id.alldaytixing_tv:// 全天的提醒时间
			startActivity(new Intent(context, AllDayTimeActivity.class));
			break;
		// case R.id.addfocus_tv:// 加入发现推荐榜
		// startActivity(new Intent(context, JoinFoundBangActivity.class));
		// break;
		case R.id.yijianfankui_tv:// 意见反馈
			startActivity(new Intent(context, OpinionBackActivity.class));
			break;
		case R.id.haoping_tv:// 给个好评
			try {
				Intent intent1 = new Intent(Intent.ACTION_VIEW);
				intent1.setData(Uri
						.parse("market://details?id=" + getPackageName()));
				startActivity(intent1);
			}catch (Exception e){

			}
			break;
		case R.id.aboutus_tv:// 关于我们
			startActivity(new Intent(context, AboutUsActivity.class));
			break;
		case R.id.setendsound_tv:// 设置结束的音效
			startActivity(new Intent(context, EndSoundActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (STATE_CHOOSE == requestCode) {
		// if (resultCode == Activity.RESULT_OK) {
		// String state = data.getStringExtra("state");
		// shuikejian_state_tv.setText(state);
		// if("私密".equals(state)){
		// sharedPrefUtil.putString(context, ShareFile.USERFILE,
		// ShareFile.OPENSTATE, "0");
		// }else if("公开".equals(state)){
		// sharedPrefUtil.putString(context, ShareFile.USERFILE,
		// ShareFile.OPENSTATE, "1");
		// }else{
		// sharedPrefUtil.putString(context, ShareFile.USERFILE,
		// ShareFile.OPENSTATE, "2");
		// }
		//
		// if(NetUtil.getConnectState(context)!=NetWorkState.NONE){
		// AlterSet();
		// }else {
		// return;
		// }
		// }
		// } else
		if (CHOOSE_MUSIC == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				String lingshengname = data.getStringExtra("lingshengname");
				ringcode = data.getStringExtra("code");
				ringname_tv.setText(lingshengname);
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.MUSICCODE, ringcode);
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.MUSICDESC, ringname_tv.getText().toString());
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					AlterSet();
				} else {
					return;
				}
			}
		} else if (BEFORETIME == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				beforetime = data.getStringExtra("beforetime");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.BEFORETIME, beforetime);
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					AlterSet();
				} else {
					return;
				}
			}
		}
	}

	private void AlterSet() {
		String path = URLConstants.修改用户设置;
		AlterSetAsync(path);
	}

	private void AlterSetAsync(String path) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								SuccessOrFailBean bean = gson.fromJson(result,
										SuccessOrFailBean.class);
								if (bean.status == 0) {
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("tbUserMannge.uid", sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.USERID, "0"));
				pairs.put("tbUserMannge.id", sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.SETID, "0"));
				pairs.put("tbUserMannge.openState", "0");
				pairs.put("tbUserMannge.ringCode", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.MUSICCODE,
						"g_88"));
				pairs.put("tbUserMannge.ringDesc", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.MUSICDESC,
						"完成任务"));
				pairs.put("tbUserMannge.beforeTime", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.BEFORETIME, "0"));
				pairs.put("tbUserMannge.morningState", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.MORNINGSTATE, "0"));
				pairs.put("tbUserMannge.morningTime", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.MORNINGTIME,
						"07:58"));
				pairs.put("tbUserMannge.nightState", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.NIGHTSTATE, "0"));
				pairs.put("tbUserMannge.nightTime", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.NIGHTTIME,
						"18:30"));
				pairs.put("tbUserMannge.dayTime", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.ALLTIME, "08:58"));
				pairs.put("tbUserMannge.dayState", sharedPrefUtil.getString(
						context, ShareFile.USERFILE, ShareFile.ALLSTATE, "0"));
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

	private void cancleDialog() {
		final AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setAttributes(params);// 设置生效
		window.setContentView(R.layout.dialog_canclefocus);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
				// App app = App.getDBcApplication();
				Intent intent = new Intent(context, SetDataUpdateActivity.class);
				File file = new File(
						"/data/data/com.mission.schedule/databases/plan");
				File file1 = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/YourAppDataFolder/plan");
				// List<Map<String, String>> schList = new
				// ArrayList<Map<String,String>>();
				// List<Map<String, String>> repList = new
				// ArrayList<Map<String,String>>();
				// schList = app.queryAllSchData();
				// repList = app.QueryAllRepData();
				if (file.exists()) {
					// if(schList!=null&&schList.size()>0){
					// for(int i=0;i<schList.size();i++){
					// app.deleteSchData(schList.get(i).get(ScheduleTable.schID));
					// }
					// }
					// if(repList!=null&&repList.size()>0){
					// for(int i=0;i<repList.size();i++){
					// app.deleteRepData(repList.get(i).get(CLRepeatTable.repID));
					// }
					// }
					intent.putExtra("type", "0");
					startActivity(intent);
				} else if (file1.exists() && !file.exists()) {
					// if(schList!=null&&schList.size()>0){
					// for(int i=0;i<schList.size();i++){
					// app.deleteSchData(schList.get(i).get(ScheduleTable.schID));
					// }
					// }
					// if(repList!=null&&repList.size()>0){
					// for(int i=0;i<repList.size();i++){
					// app.deleteRepData(repList.get(i).get(CLRepeatTable.repID));
					// }
					// }
					intent.putExtra("type", "1");
					startActivity(intent);
				} else {
					alertFailDialog();
				}
			}
		});
		TextView delete_canel = (TextView) window
				.findViewById(R.id.delete_canel);
		delete_canel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		delete_tv.setText("注意\n迁移数据时，所有本地数据将全部清空!");

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
		delete_tv.setText("所需迁移的数据文件不存在！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

}
