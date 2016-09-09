package com.mission.schedule.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.SendLiaoTianMessageBackBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LiuYanActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_send)
	private LinearLayout top_ll_send;
	@ViewResId(id = R.id.friendsName_tv)
	private TextView friendsName_tv;
	@ViewResId(id = R.id.editmessage_et)
	private EditText editmessage_et;

	Context context;
	String userid;// 用户id
	int friendid;// 好友id
	String message;// 消息
	int messageState;// 消息状态（0普通消息，1日程消息）
	int alarmType;// 闹钟类型（0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均）
	String date;// 闹钟日期（格式2012-1-1）
	String time;// 闹钟时间（格式16：22：00）
	String niCheng;// 昵称
	String alamsound;// 铃声
	String alamsoundDesc;// 铃声描述
	String path;
	App app;
	SharedPrefUtil sharedPrefUtil;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_send.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_liuyan);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		app = App.getDBcApplication();
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userid = getIntent().getStringExtra("userId");
		friendid = getIntent().getIntExtra("friendId", 0);
		niCheng = getIntent().getStringExtra("friendName");
		messageState = getIntent().getIntExtra("state", 0);
		date = DateUtil.formatDate(new Date());
		time = DateUtil.formatDateTimeHm(new Date());
		alarmType = 1;
		alamsound = "aurora";
		alamsoundDesc = "默认";
		friendsName_tv.setText(niCheng);
	}

	@Override
	protected void setAdapter() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_send:
			message = editmessage_et.getText().toString().trim();
			if (!"".equals(message) && message != null) {
				path = URLConstants.添加聊天信息;
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("tbuserFrendsMessage.uid", userid);
				pairs.put("tbuserFrendsMessage.cpId", String.valueOf(friendid));
				pairs.put("tbuserFrendsMessage.messge", message);
				pairs.put("tbuserFrendsMessage.status",
						String.valueOf(messageState));
				pairs.put("tbuserFrendsMessage.cIsAlarm",
						String.valueOf(alarmType));

				pairs.put("tbuserFrendsMessage.cdate",
						DateUtil.formatDate(DateUtil.parseDate(date)));

				pairs.put("tbuserFrendsMessage.ctime", DateUtil
						.formatDateTimeHm(DateUtil.parseDateTimeHm(time)));
				pairs.put("tbuserFrendsMessage.cRecommendName", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.USERNAME, ""));
				pairs.put("tbuserFrendsMessage.cAlarmSound", alamsound);
				pairs.put("tbuserFrendsMessage.cAlarmSoundDesc", alamsoundDesc);
				pairs.put("tbuserFrendsMessage.repType", "0");// 1每天,2每周,3每月,4每年,5工作日
				/**
				 * 生成规则 1每天[] 2 每周[“1”] 3每月[“1”]每月1号 4每年[“01-01”]每年1月1号 5工作日[]
				 */
				pairs.put("tbuserFrendsMessage.repTypeParameter", "");
				pairs.put("tbuserFrendsMessage.cPostpone", String.valueOf(0));// 是否顺延(0否,1是)',
				pairs.put("tbuserFrendsMessage.cTags", "");// 分类标签'
				pairs.put("tbuserFrendsMessage.cType", String.valueOf(0));// 记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
				pairs.put("tbuserFrendsMessage.cTypeDesc", "");// 当记事类别为1时所带的url链接',
				pairs.put("tbuserFrendsMessage.cTypeSpare", "");// 当记事类别为1时所带的url链接描述',
				pairs.put("tbuserFrendsMessage.cOpenstate", String.valueOf(0));// 公开状态(0否,1是,2仅好友可见)',
				pairs.put("tbuserFrendsMessage.cLightAppId", "");// 轻应用与记事绑定的唯一ID'
				pairs.put("tbuserFrendsMessage.repcolortype", "0");// 记事颜色类别',
				pairs.put("tbuserFrendsMessage.repstartdate",
						DateUtil.formatDateTime(new Date()));// 下一次重复闹钟起始时间'
				pairs.put("tbuserFrendsMessage.repnextcreatedtime", "");// 下一次生成日期
																		// 如
																		// 2012-01-01
																		// 12：00
				pairs.put("tbuserFrendsMessage.replastcreatedtime", "");// 之前最后一次生成时间
																		// 如2012-01-01
																		// 12：00
				/**
				 * 是否显示时间 0否 1是 默认为1
				 */
				pairs.put("tbuserFrendsMessage.repdisplaytime", "1");
				pairs.put("tbuserFrendsMessage.repinitialcreatedtime",
						DateUtil.formatDateTime(new Date()));// 初始创建时间
				pairs.put("tbuserFrendsMessage.aType", "0");// 0无附加信息1附加连接2附加图片3连接和图片
				pairs.put("tbuserFrendsMessage.webUrl", "");
				pairs.put("tbuserFrendsMessage.imgPath", "");
				pairs.put("tbuserFrendsMessage.repInSTable", "0");
				app.insertMFMessageSendData(Integer.parseInt(userid), friendid,
						alarmType, 0, 0, 0, 1, 0, 0, 0, "", message, DateUtil
								.formatDateTimeSs(new Date()), DateUtil
								.formatDate(DateUtil.parseDate(date)), DateUtil
								.formatDateTimeHm(DateUtil
										.parseDateTimeHm(time)), "", "", "",
						alamsoundDesc, alamsound, DateUtil
								.formatDateTime(new Date()), "", "", "",
						messageState, 0, "", "", 0);

				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					MySendMessageAsync(path, pairs);
				} else {
					Toast.makeText(context, "请检查网络..", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			} else {
				Toast.makeText(context, "留言内容不能为空..", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			break;
		case R.id.top_ll_back:
			this.finish();
			break;
		default:
			break;
		}
	}

	private void MySendMessageAsync(String path, final Map<String, String> map) {
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在发送...");
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							try {
								SendLiaoTianMessageBackBean orFailBean = gson
										.fromJson(
												result,
												SendLiaoTianMessageBackBean.class);
								if (orFailBean.status == 0) {
									app.updateMFMessageSendData(orFailBean.id);
									alertDialog();
								} else {
									app.deleteMFMessageSendData();
									alertFailDialog();
									Toast.makeText(context, orFailBean.message,
											Toast.LENGTH_SHORT).show();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
								return;
							}
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
		delete_tv.setText("发送成功！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
				LiuYanActivity.this.finish();
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
		delete_tv.setText("发送失败！");
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
}
