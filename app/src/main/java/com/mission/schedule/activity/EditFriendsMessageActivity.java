package com.mission.schedule.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.SendLiaoTianMessageBackBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditFriendsMessageActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_send)
	private LinearLayout top_ll_send;
	@ViewResId(id = R.id.friendsName_tv)
	private TextView friendsName_tv;
	@ViewResId(id = R.id.editmessage_et)
	private EditText editmessage_et;
	@ViewResId(id = R.id.changyongyu_tv)
	private TextView changyongyu_tv;// 常用语
	@ViewResId(id = R.id.rili_tv)
	private TextView rili_tv;// 日历字体
	@ViewResId(id = R.id.rilibianhua_tv)
	private TextView rilibianhua_tv;// 日历显示，如2014-12-25
	@ViewResId(id = R.id.newbuild_ymd_state)
	private TextView newbuild_ymd_state;// 日历显示的汉字 如 今天周二
	@ViewResId(id = R.id.date_ll)
	private LinearLayout date_ll;// 选择日历按钮加号
	@ViewResId(id = R.id.time_tv)
	private TextView time_tv;// 时间显示
	@ViewResId(id = R.id.timeanniu_imag)
	private ImageView timeanniu_imag;// 时间选择按钮
	@ViewResId(id = R.id.tixing_tv)
	private TextView tixing_tv;// 提醒时间设置
	@ViewResId(id = R.id.lingsheng_ll)
	private LinearLayout lingsheng_ll;// 设置铃声布局
	@ViewResId(id = R.id.morenlingshen_tv)
	private TextView morenlingshen_tv;// 铃声描述显示
	@ViewResId(id = R.id.rili_ll)
	private LinearLayout rili_ll;
	@ViewResId(id = R.id.time_rl)
	private RelativeLayout time_rl;

	private static final int TIME_CHOOSE = 0;// 选择时间
	private static final int REQUEST_YMD = 1;// 年
	private static final int CHOOSE_MUSIC = 2;// 选择铃声
	private static final int URL_SELECT = 3;// 附加信息

	Context context;
	String path;
	String editmessage;
	String userid;// 用户id
	int friendid;// 好友id
	String message;// 消息
	int messageState;// 消息状态（0普通消息，1日程消息）
	String date;// 闹钟日期（格式2012-1-1）
	String time;// 闹钟时间（格式16：22：00）
	String niCheng;// 昵称
	String alamsound;// 铃声
	String alamsoundDesc;// 铃声描述
	SharedPrefUtil sharedPrefUtil = null;
	App app;
	int timestate;
	int timeInt = 0;
	String ymd = "";
	String ymdState = "";
	private int beforeTime = 0;
	private int lastIndex;
	String url = "";
	int setBefore;

	int isAlarm = 3; // 共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟

	boolean checkstate = false;
	String today, tomorrow;
	String alltime = "";
	

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_send.setOnClickListener(this);
		rili_tv.setOnClickListener(this);
		rilibianhua_tv.setOnClickListener(this);
		date_ll.setOnClickListener(this);
		time_tv.setOnClickListener(this);
		lingsheng_ll.setOnClickListener(this);
		tixing_tv.setOnClickListener(this);
		rili_ll.setOnClickListener(this);
		changyongyu_tv.setOnClickListener(this);
		time_rl.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_editsendmessage);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		loadData();
	}

	private void loadData() {
		alltime = sharedPrefUtil.getString(context, ShareFile.USERFILE, ShareFile.ALLTIME, "08:58");
		userid = getIntent().getStringExtra("userId");
		friendid = getIntent().getIntExtra("friendId", 0);
		niCheng = getIntent().getStringExtra("friendName");
		message = getIntent().getStringExtra("content");
		editmessage_et.setText(message);
		friendsName_tv.setText(niCheng);
		messageState = getIntent().getIntExtra("state", 0);
		editmessage = editmessage_et.getText().toString().trim();
		editmessage_et.setSelection(editmessage.length());

		alamsoundDesc = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICDESC, "完成任务");
		alamsound = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICCODE, "g_88");

		setBefore = Integer.parseInt(sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.BEFORETIME, "0"));
		time_tv.setText(DateUtil.formatDateTimeHm(new Date()));
		rilibianhua_tv.setText(DateUtil.formatDate(new Date()));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());
		newbuild_ymd_state.setText("今天"
				+ CharacterUtil.getWeekOfDate(this, new Date()));
		initValues();
		morenlingshen_tv.setText(alamsoundDesc);
		String beforeStr = "";
		if (setBefore == 0) {
			beforeStr = "准时提醒";
		} else if (setBefore == 5) {
			beforeStr = "5分钟";
		} else if (setBefore == 15) {
			beforeStr = "15分钟";
		} else if (setBefore == 30) {
			beforeStr = "30分钟";
		} else if (setBefore == 60) {
			beforeStr = "1小时";
		} else if (setBefore == 120) {
			beforeStr = "2小时";
		} else if (setBefore == 24 * 60) {
			beforeStr = "1天";
		} else if (setBefore == 48 * 60) {
			beforeStr = "2天";
		} else if (setBefore == 7 * 24 * 60) {
			beforeStr = "1周";
		}
		if (setBefore == 0) {
			isAlarm = 1;
		} else {
			isAlarm = 3;
		}
		if (setBefore == 0) {
			tixing_tv.setText(beforeStr);
		} else {
			tixing_tv.setText("提前" + beforeStr);
		}
	}

	private void initValues() {
		if (beforeTime == timeInt) {
			lastIndex = 0;
		} else if (beforeTime == 5) {
			lastIndex = 1;
		} else if (beforeTime == 15) {
			lastIndex = 2;
		} else if (beforeTime == 30) {
			lastIndex = 3;
		} else if (beforeTime == 60) {
			lastIndex = 4;
		} else if (beforeTime == 120) {
			lastIndex = 5;
		} else if (beforeTime == 1440) {
			lastIndex = 6;
		} else if (beforeTime == 2 * 1440) {
			lastIndex = 7;
		} else if (beforeTime == 7 * 1440) {
			lastIndex = 8;
		}
	}

	@Override
	protected void setAdapter() {
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;
		case R.id.changyongyu_tv:
			intent = new Intent(context, AddSchWebUrlActivity.class);
			intent.putExtra("url", url);
			startActivityForResult(intent, URL_SELECT);
			break;
		case R.id.tixing_tv:
			initBeforeDiaLog();
			break;
		case R.id.lingsheng_ll:
			intent = new Intent(context, LingShengActivity.class);
			startActivityForResult(intent, CHOOSE_MUSIC);
			break;
		case R.id.top_ll_send:
			try {
				int urlstate = 0;
				if ("".equals(url)) {
					urlstate = 0;
				} else {
					urlstate = 1;
				}

				if ("全天".equals(time_tv.getText().toString())) {
					timestate = 0;
					time = DateUtil.formatDateTimeHm(new Date());
				} else {
					timestate = 1;
					time = time_tv.getText().toString();
				}
				String berforeStr = tixing_tv.getText().toString();
				int before = 0;
				if ("准时提醒".equals(berforeStr)) {
					before = 0;
				} else if ("提前5分钟".equals(berforeStr)) {
					before = 5;
				} else if ("提前15分钟".equals(berforeStr)) {
					before = 15;
				} else if ("提前30分钟".equals(berforeStr)) {
					before = 30;
				} else if ("提前1小时".equals(berforeStr)) {
					before = 60;
				} else if ("提前2小时".equals(berforeStr)) {
					before = 120;
				} else if ("提前1天".equals(berforeStr)) {
					before = 1440;
				} else if ("提前2天".equals(berforeStr)) {
					before = 2 * 1440;
				} else if ("提前1周".equals(berforeStr)) {
					before = 7 * 1440;
				}

				date = rilibianhua_tv.getText().toString();
				message = editmessage_et.getText().toString();

				if (checkstate) {
					app.insertScheduleData(message, date, time, isAlarm,
							before, timestate, 0, 0, 0, 0,
							DateUtil.formatDateTimeSs(new Date()), "", 0, "",
							"", 0, "", DateUtil.formatDateTime(new Date()), 1,
							0, 0, alamsoundDesc, alamsound, "", 0, 0, 0, "",
							"", 0, 0, 0);
					// if (before == 0) {
					// app.insertClockData(
					// format1.format(format.parse(date + " " + time)),
					// message,
					// before,
					// format1.format(format.parse(date + " " + time)),
					// alamsoundDesc, alamsound, timestate, 0, 0,
					// App.schID, 0, isAlarm, 0, "");
					// } else {
					// String alarmResultTime = format1.format(format.parse(
					// date + " " + time).getTime()
					// - before * 60 * 1000);
					// app.insertClockData(
					// alarmResultTime,
					// message,
					// before,
					// format1.format(format.parse(date + " " + time)),
					// alamsoundDesc, alamsound, timestate, 0, 0,
					// App.schID, 0, isAlarm, 0, "");
					// }
					QueryAlarmData.writeAlarm(getApplicationContext());
				} else {

				}

				path = URLConstants.添加聊天信息;
				Map<String, String> parList = new HashMap<String, String>();
				parList.put("tbuserFrendsMessage.uid", userid);
				parList.put("tbuserFrendsMessage.cpId",
						String.valueOf(friendid));
				parList.put("tbuserFrendsMessage.messge", message);
				parList.put("tbuserFrendsMessage.status",
						String.valueOf(messageState));
				parList.put("tbuserFrendsMessage.cIsAlarm",
						String.valueOf(isAlarm));
				parList.put("tbuserFrendsMessage.cdate", date);
				parList.put("tbuserFrendsMessage.ctime", DateUtil
						.formatDateTimeHm(DateUtil.parseDateTimeHm(time)));
				parList.put("tbuserFrendsMessage.cRecommendName",
						sharedPrefUtil.getString(context, ShareFile.USERFILE,
								ShareFile.USERNAME, ""));
				parList.put("tbuserFrendsMessage.cAlarmSound", alamsound);
				parList.put("tbuserFrendsMessage.cAlarmSoundDesc",
						alamsoundDesc);
				parList.put("tbuserFrendsMessage.repType", "0");// 1每天,2每周,3每月,4每年,5工作日
				/**
				 * 生成规则 1每天[] 2 每周[“1”] 3每月[“1”]每月1号 4每年[“01-01”]每年1月1号 5工作日[]
				 */
				parList.put("tbuserFrendsMessage.repTypeParameter", "");
				parList.put("tbuserFrendsMessage.cPostpone", String.valueOf(0));// 是否顺延(0否,1是)',
				parList.put("tbuserFrendsMessage.cTags", "");// 分类标签'
				parList.put("tbuserFrendsMessage.cType", String.valueOf(0));// 记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
				parList.put("tbuserFrendsMessage.cTypeDesc", "");// 当记事类别为1时所带的url链接',
				parList.put("tbuserFrendsMessage.cTypeSpare", "");// 当记事类别为1时所带的url链接描述',
				parList.put("tbuserFrendsMessage.cOpenstate", 0 + "");// 公开状态(0否,1是,2仅好友可见)',
				parList.put("tbuserFrendsMessage.cLightAppId", "");// 轻应用与记事绑定的唯一ID'
				parList.put("tbuserFrendsMessage.repcolortype", "0");// 记事颜色类别',
				parList.put("tbuserFrendsMessage.repstartdate", "");// 下一次重复闹钟起始时间'
				parList.put("tbuserFrendsMessage.repnextcreatedtime", "");// 下一次生成日期
																			// 如
																			// 2012-01-01
																			// 12：00
				parList.put("tbuserFrendsMessage.replastcreatedtime", "");// 之前最后一次生成时间
																			// 如2012-01-01
																			// 12：00
				/**
				 * 是否显示时间 0否 1是 默认为1
				 */
				parList.put("tbuserFrendsMessage.repdisplaytime",
						String.valueOf(timestate));
				parList.put("tbuserFrendsMessage.repinitialcreatedtime", "");// 初始创建时间
				parList.put("tbuserFrendsMessage.aType", urlstate + "");// 0无附加信息1附加连接2附加图片3连接和图片
				parList.put("tbuserFrendsMessage.webUrl", url);
				parList.put("tbuserFrendsMessage.imgPath", "");
				parList.put("tbuserFrendsMessage.repInSTable", "0");
				app.insertMFMessageSendData(Integer.parseInt(userid), friendid,
						isAlarm, 0, 0, 0, timestate, before, 0, 0, "", message,
						DateUtil.formatDateTimeSs(new Date()), date, DateUtil
								.formatDateTimeHm(DateUtil
										.parseDateTimeHm(time)), "", "", "",
						alamsoundDesc, alamsound, "", "", "", "", messageState,
						urlstate, url, "", 0);
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					MySendMessageAsync(path, parList);
				} else {
					Toast.makeText(context, "请检查网络..", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.rili_tv:
			intent = new Intent(this, DateCalendarActivity.class);
			intent.putExtra("sourse", 0);
			intent.putExtra("dateTime", rilibianhua_tv.getText().toString());
			startActivityForResult(intent, REQUEST_YMD);
			break;
		case R.id.rili_ll:
			selectDateView();
			break;
		case R.id.rilibianhua_tv:
			selectDateView();
			break;
		case R.id.date_ll:
			selectDateView();
			break;
		case R.id.time_rl:
			selectTimeView();
			break;
		case R.id.time_tv:
			selectTimeView();
			break;
		default:
			break;
		}
	}

	private void selectTimeView() {
		if ("全天".equals(time_tv.getText().toString())) {
			time_tv.setText(DateUtil.formatDateTimeHm(new Date()));
		}
		String[] ymdHm1 = DateUtil.formatDateTime(new Date()).split(" ");
		int result1 = Integer.parseInt(ymdHm1[1].split(":")[1]) % 5;
		if (result1 == 0) {
			time_tv.setText(ymdHm1[1]);
		} else {
			int division = Integer.parseInt(ymdHm1[1].split(":")[1]) / 5;
			int timeChoose = division * 5;
			if (timeChoose == 60) {
				if (Integer.parseInt(time_tv.getText().toString()
						.split(":")[0]) == 23) {
					time_tv.setText("00" + ":" + "00");
				} else {
					time_tv.setText(Integer.parseInt(time_tv.getText()
							.toString().split(":")[0])
							+ 1 + ":" + "00");
				}
			} else {
				time_tv.setText(time_tv.getText().toString().split(":")[0]
						+ ":"
						+ (timeChoose < 10 ? "0" + timeChoose : timeChoose));
			}
		}
		// tv_beforetime.setText("(" + beforTimetp + ")");
		initDiaLog();		
	}

	private void selectDateView() {
		String[] ymdStr;
		Calendar calendar = Calendar.getInstance();
		int month;
		String monthStr;
		int day;
		String dayStr;
		Date dateStr;
		Date dateToday;
		int count;
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.fade_top);
		ymdStr = rilibianhua_tv.getText().toString().split("-");
		calendar.set(Integer.parseInt(ymdStr[0]),
				Integer.parseInt(ymdStr[1]) - 1,
				Integer.parseInt(ymdStr[2]) + 1);
		month = calendar.get(Calendar.MONTH) + 1;
		monthStr = month < 10 ? "0" + month : "" + month;
		day = calendar.get(Calendar.DATE);
		dayStr = day < 10 ? "0" + day : "" + day;
		ymd = calendar.get(Calendar.YEAR) + "-" + monthStr + "-" + dayStr;
		dateStr = DateUtil.parseDate(ymd);
		dateToday = DateUtil.parseDate(DateUtil.formatDate(new Date()));
		count = (int) ((dateToday.getTime() - dateStr.getTime()) / (1000 * 60 * 60 * 24));
		if (count > 0) {
			if (count == 1) {
				ymdState = "昨天";
			} else {
				ymdState = count + "天前";
			}
		} else if (count == 0) {
			ymdState = "今天";
		} else {
			if (Math.abs(count) == 1) {
				ymdState = "明天";
			} else if (Math.abs(count) == 2) {
				ymdState = "后天";
			} else {
				ymdState = Math.abs(count) + "天后";
			}
		}

		ymdState += CharacterUtil.getWeekOfDate(this, calendar.getTime());
		newbuild_ymd_state.setText(ymdState);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				rilibianhua_tv.setText(ymd);
				newbuild_ymd_state.setText(ymdState);
				rilibianhua_tv.startAnimation(AnimationUtils.loadAnimation(
						context, R.anim.fade_in));
			}
		});
		rilibianhua_tv.startAnimation(animation);	
		newbuild_ymd_state.startAnimation(animation);
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
									Toast.makeText(context, "发送成功!",
											Toast.LENGTH_SHORT).show();
									app.updateMFMessageSendData(orFailBean.id);
									finish();
								} else {
									app.deleteMFMessageSendData();
									Toast.makeText(context, orFailBean.message,
											Toast.LENGTH_SHORT).show();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
								return;
							}
						} else {
							app.deleteMFMessageSendData();
							Toast.makeText(context, "发送失败!", Toast.LENGTH_SHORT)
									.show();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}

	@SuppressWarnings("unused")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TIME_CHOOSE) {
			if (resultCode == Activity.RESULT_OK) {
				String timeSet = data.getStringExtra("timeSet");
				beforeTime = data.getIntExtra("beforeTime", 5);
				time_tv.setText(timeSet);
				if ("全天".equals(time_tv.getText().toString())) {
					timestate = 0;
				} else {
					timestate = 1;
				}
				if (beforeTime == -1) {
					// tv_beforetime.setVisibility(View.GONE);
					// isNeedPush = false;
				} else {
					// isNeedPush = true;
				}
				String beforTimetp = "";
				if (beforeTime == timeInt) {
					beforTimetp = "-0分钟";
				} else if (beforeTime == 5) {
					beforTimetp = "-5分钟";
				} else if (beforeTime == 10) {
					beforTimetp = "-10分钟";
				} else if (beforeTime == 15) {
					beforTimetp = "-15分钟";
				} else if (beforeTime == 30) {
					beforTimetp = "-30分钟";
				} else if (beforeTime == 45) {
					beforTimetp = "-45分钟";
				} else if (beforeTime == 60) {
					beforTimetp = "-60分钟";
				} else if (beforeTime == 120) {
					beforTimetp = "-120分钟";
				} else if (beforeTime == 1440) {
					beforTimetp = "-1d";
				} else if (beforeTime == 2 * 1440) {
					beforTimetp = "-2d";
				}
				// tv_beforetime.setText("(" + beforTimetp + ")");
			}
		} else if (requestCode == REQUEST_YMD) {
			if (resultCode == Activity.RESULT_OK) {
				String key_tpDate = data.getStringExtra("dateTime");
				rilibianhua_tv.setText(key_tpDate);
				String ymdState = "";
				Date dateStr = DateUtil.parseDate(key_tpDate);
				Date dateToday = DateUtil.parseDate(DateUtil
						.formatDate(new Date()));
				int count = (int) ((dateToday.getTime() - dateStr.getTime()) / (1000 * 60 * 60 * 24));
				if (count > 0) {
					if (count == 1) {
						ymdState = "昨天";
					} else {
						ymdState = count + "天前";
					}
				} else if (count == 0) {
					ymdState = "今天";
				} else {
					if (Math.abs(count) == 1) {
						ymdState = "明天";
					} else if (Math.abs(count) == 2) {
						ymdState = "后天";
					} else {
						ymdState = Math.abs(count) + "天后";
					}
				}
				ymdState += CharacterUtil.getWeekOfDate(context,
						DateUtil.parseDate(key_tpDate));
				newbuild_ymd_state.setText(ymdState);
			}
		} else if (CHOOSE_MUSIC == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				alamsoundDesc = data.getStringExtra("lingshengname");
				alamsound = data.getStringExtra("code");
				morenlingshen_tv.setText(alamsoundDesc);
			}
		} else if (URL_SELECT == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				url = data.getStringExtra("url");
				if ("".equals(url)) {
					changyongyu_tv.setText("附加信息");
				} else {
					changyongyu_tv.setText("附加信息     URL");
				}
			}
		}
	}

	// ===================================================================================//

	Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	int index = 0;
	GridBeforeAdapter adapter = null;

	private LinearLayout ll;
	private TextView tixingcishu_tv;
	private TextView year_tv;
	private TextView HHmmtime_tv;

	private LinearLayout tixing_ll;
	private TextView tixingcishu_tv1;
	private TextView year_tv1;
	private TextView time_tv1;

	private void initBeforeDiaLog() {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_naozhongtiqian, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
		dialog.show();
		new MyBeforeOnClick(dialog, more_pop_menu);
	}

	class MyBeforeOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private GridView before_gridview;
		private LinearLayout detail_close;

		private String[] befStrings;

		int height;

		boolean beforeFag;

		public MyBeforeOnClick(Dialog dialog, View view) {
			this.dialog = dialog;
			this.view = view;
			initview();
			initdata();
		}

		private void initview() {
			detail_close = (LinearLayout) view.findViewById(R.id.detail_close);
			ll = (LinearLayout) view.findViewById(R.id.ll);
			tixingcishu_tv = (TextView) view.findViewById(R.id.tixingcishu_tv);
			year_tv = (TextView) view.findViewById(R.id.year_tv);
			HHmmtime_tv = (TextView) view.findViewById(R.id.time_tv);
			tixing_ll = (LinearLayout) view.findViewById(R.id.tixing_ll);
			tixingcishu_tv1 = (TextView) view
					.findViewById(R.id.tixingcishu_tv1);
			year_tv1 = (TextView) view.findViewById(R.id.year_tv1);
			time_tv1 = (TextView) view.findViewById(R.id.time_tv1);
			before_gridview = (GridView) view
					.findViewById(R.id.before_gridview);
			height = Utils.dipTopx(context, 100);
			befStrings = context.getResources().getStringArray(
					R.array.before_time);
			tixingcishu_tv.setText("1");

			detail_close.setOnClickListener(this);
		}

		private void initdata() {
			map.put(0, true);
			String beforeStr = "";
			if (tixing_tv.getText().toString().equals("准时提醒")) {
				index = 0;
			} else if (tixing_tv.getText().toString().equals("提前5分钟")) {
				beforeStr = "5分钟";
				index = 1;
			} else if (tixing_tv.getText().toString().equals("提前15分钟")) {
				beforeStr = "15分钟";
				index = 2;
			} else if (tixing_tv.getText().toString().equals("提前30分钟")) {
				beforeStr = "30分钟";
				index = 3;
			} else if (tixing_tv.getText().toString().equals("提前1小时")) {
				beforeStr = "1小时";
				index = 4;
			} else if (tixing_tv.getText().toString().equals("提前2小时")) {
				beforeStr = "2小时";
				index = 5;
			} else if (tixing_tv.getText().toString().equals("提前1天")) {
				beforeStr = "1天";
				index = 6;
			} else if (tixing_tv.getText().toString().equals("提前2天")) {
				beforeStr = "2天";
				index = 7;
			} else if (tixing_tv.getText().toString().equals("提前1周")) {
				beforeStr = "1周";
				index = 8;
			}
			map.put(index, true);
			adapter = new GridBeforeAdapter(context, befStrings, height, index);
			before_gridview.setAdapter(adapter);
		}

		@Override
		public void onClick(View v) {
			Animation translateIn0 = new TranslateAnimation(-view.getWidth(),
					0, 0, 0);
			Animation translateIn1 = new TranslateAnimation(view.getWidth(), 0,
					0, 0);
			translateIn0.setDuration(400);
			translateIn1.setDuration(400);
			switch (v.getId()) {
			case R.id.detail_close:
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	private void initMap() {
		for (int i = 1; i < 9; i++) {
			if (i == index) {
				map.put(i, true);
			} else {
				map.put(i, false);
			}
		}
	}

	private void initMap1() {
		for (int i = 1; i < 9; i++) {
			map.put(i, false);
		}
	}

	public class GridBeforeAdapter extends BaseAdapter {

		private Context context;
		private String[] beforeTime;
		private int height;
		private int lastIndex;
		String str = "准时提醒";

		public GridBeforeAdapter(Context context, String[] beforeTime,
				int height, int lastIndex) {
			this.context = context;
			this.beforeTime = beforeTime;
			this.height = height;
			this.lastIndex = lastIndex;
			initMap();
		}

		@Override
		public int getCount() {
			return beforeTime.length;
		}

		@Override
		public Object getItem(int position) {
			return beforeTime[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public String[] getBeforeTime() {
			return beforeTime;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			ViewWapper viewWapper = null;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.adapter_grid_before_item, null);
				viewWapper = new ViewWapper(view);
				view.setTag(viewWapper);
			} else {
				viewWapper = (ViewWapper) view.getTag();
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, height);
			LinearLayout before_ll = viewWapper.getBeforLL();
			before_ll.setLayoutParams(params);

			TextView before_tv = viewWapper.getBeforTime();
			TextView before_tv_state = viewWapper.getBeforState();
			String beforTime = beforeTime[position];
			if (beforTime.equals(str)) {
				before_tv.setText(beforTime);
				before_tv.setTextSize(15);
			} else {
				before_tv.setText(beforTime.split("-")[0]);
				before_tv_state.setText(beforTime.split("-")[1]);
			}
			before_ll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (position == 0) {
						if (map.get(0)) {
							map.put(position, false);
						} else {
							map.put(position, true);
						}
					} else {
						if (position == lastIndex) {
							if (map.get(position)) {
								map.put(position, false);
							} else {
								map.put(position, true);
							}
						} else {
							initMap1();
							map.put(position, true);
						}

						lastIndex = position;
					}
					adapter.notifyDataSetChanged();
				}
			});
			if (position == 0) {
				if (map.get(position)) {
					before_ll.setBackgroundColor(getResources().getColor(
							R.color.sunday_txt));
					before_tv.setTextColor(getResources().getColor(
							R.color.white));
				} else {
					before_ll.setBackgroundColor(getResources().getColor(
							R.color.choosedate));
					before_tv.setTextColor(getResources().getColor(
							R.color.gongkai_txt));
				}
			} else {
				if (map.get(position)) {
					before_ll.setBackgroundColor(getResources().getColor(
							R.color.mingtian_color));
					before_tv.setTextColor(getResources().getColor(
							R.color.white));
					before_tv_state.setTextColor(getResources().getColor(
							R.color.white));
				} else {
					before_ll.setBackgroundColor(getResources().getColor(
							R.color.choosedate));
					before_tv.setTextColor(getResources().getColor(
							R.color.gongkai_txt));
					before_tv_state.setTextColor(getResources().getColor(
							R.color.gongkai_txt));
				}
			}
			int select = 0;
			for (int i = 0; i < beforeTime.length; i++) {
				if (map.get(i)) {
					select++;
				} else {
					tixing_ll.setVisibility(View.GONE);
				}
			}
			int before = 0;
			if (str.equals(beforTime)) {
				before = 0;
			} else if ("5-分钟".equals(beforTime)) {
				before = 5;
			} else if ("15-分钟".equals(beforTime)) {
				before = 15;
			} else if ("30-分钟".equals(beforTime)) {
				before = 30;
			} else if ("1-小时".equals(beforTime)) {
				before = 60;
			} else if ("2-小时".equals(beforTime)) {
				before = 120;
			} else if ("1-天".equals(beforTime)) {
				before = 24 * 60;
			} else if ("2-天".equals(beforTime)) {
				before = 48 * 60;
			} else if ("1-周".equals(beforTime)) {
				before = 7 * 24 * 60;
			}
			String selecttime = "";
			if("全天".equals(time_tv.getText().toString())){
				selecttime = alltime;
			}else{
				selecttime = time_tv.getText().toString();
			}
			if (select == 1) {
				ll.setVisibility(View.VISIBLE);
				tixing_ll.setVisibility(View.GONE);
				if (map.get(0)) {
					tixing_tv.setText(str);
					isAlarm = 1;
					String retStrFormatNowDate = DateUtil.formatDate(DateUtil
							.parseDate(rilibianhua_tv.getText().toString()));
					year_tv.setText(retStrFormatNowDate);
					HHmmtime_tv.setText(selecttime);
				} else {
					if (map.get(position)) {
						isAlarm = 2;
						if ("5-分钟".equals(beforTime)) {
							tixing_tv.setText("提前5分钟");
						} else if ("15-分钟".equals(beforTime)) {
							tixing_tv.setText("提前15分钟");
						} else if ("30-分钟".equals(beforTime)) {
							tixing_tv.setText("提前30分钟");
						} else if ("1-小时".equals(beforTime)) {
							tixing_tv.setText("提前1小时");
						} else if ("2-小时".equals(beforTime)) {
							tixing_tv.setText("提前2小时");
						} else if ("1-天".equals(beforTime)) {
							tixing_tv.setText("提前1天");
						} else if ("2-天".equals(beforTime)) {
							tixing_tv.setText("提前2天");
						} else if ("1-周".equals(beforTime)) {
							tixing_tv.setText("提前1周");
						}
						// Date nowTime = new
						// Date(System.currentTimeMillis()
						// - before * 60 * 1000);
						String retStrFormatNowDate = DateUtil
								.formatDateTime(DateUtil
										.parseDateTime(rilibianhua_tv.getText()
												.toString()+" "+selecttime));
						year_tv.setText(retStrFormatNowDate.substring(0, 10));
						HHmmtime_tv.setText(retStrFormatNowDate.substring(11,
								16));
					}
				}
			} else if (select == 2) {
				ll.setVisibility(View.VISIBLE);
				tixing_ll.setVisibility(View.VISIBLE);
				isAlarm = 3;
				if (map.get(position)) {
					if ("5-分钟".equals(beforTime)) {
						tixing_tv.setText("提前5分钟");
					} else if ("15-分钟".equals(beforTime)) {
						tixing_tv.setText("提前15分钟");
					} else if ("30-分钟".equals(beforTime)) {
						tixing_tv.setText("提前30分钟");
					} else if ("1-小时".equals(beforTime)) {
						tixing_tv.setText("提前1小时");
					} else if ("2-小时".equals(beforTime)) {
						tixing_tv.setText("提前2小时");
					} else if ("1-天".equals(beforTime)) {
						tixing_tv.setText("提前1天");
					} else if ("2-天".equals(beforTime)) {
						tixing_tv.setText("提前2天");
					} else if ("1-周".equals(beforTime)) {
						tixing_tv.setText("提前1周");
					}
					int number = 0;
					if (str.equals(beforTime)) {
						number = 0;
					} else if ("5-分钟".equals(beforTime)) {
						number = 5;
					} else if ("15-分钟".equals(beforTime)) {
						number = 15;
					} else if ("30-分钟".equals(beforTime)) {
						number = 30;
					} else if ("1-小时".equals(beforTime)) {
						number = 60;
					} else if ("2-小时".equals(beforTime)) {
						number = 120;
					} else if ("1-天".equals(beforTime)) {
						number = 24 * 60;
					} else if ("2-天".equals(beforTime)) {
						number = 48 * 60;
					} else if ("1-周".equals(beforTime)) {
						number = 7 * 24 * 60;
					}
					if (number != 0) {
						String beforenowtime = DateUtil.formatDateTime(DateUtil
								.parseDateTime(rilibianhua_tv.getText()
										.toString()
										+ " "
										+ selecttime));
						Date nowtime = new Date(DateUtil.parseDateTime(
								beforenowtime).getTime()
								- number * 60 * 1000);
						String retStrFormatNowDate = DateUtil
								.formatDateTime(nowtime);
						year_tv.setText(retStrFormatNowDate.substring(0, 10));
						HHmmtime_tv.setText(retStrFormatNowDate.substring(11,
								16));
						year_tv1.setText(beforenowtime.substring(0, 10));
						time_tv1.setText(beforenowtime.substring(11, 16));
					}
				}
			} else {
				map.put(0, true);
				ll.setVisibility(View.VISIBLE);
				tixing_ll.setVisibility(View.GONE);
				tixing_tv.setText(str);
			}
			return view;
		}

		class ViewWapper {

			private View view;
			private LinearLayout before_ll;
			private TextView before_tv;
			private TextView before_state;

			private ViewWapper(View view) {
				this.view = view;
			}

			private LinearLayout getBeforLL() {
				if (before_ll == null) {
					before_ll = (LinearLayout) view
							.findViewById(R.id.before_ll);
				}
				return before_ll;
			}

			private TextView getBeforTime() {
				if (before_tv == null) {
					before_tv = (TextView) view.findViewById(R.id.before_tv);
				}
				return before_tv;
			}

			private TextView getBeforState() {
				if (before_state == null) {
					before_state = (TextView) view
							.findViewById(R.id.before_state);
				}
				return before_state;
			}
		}
	}

	private void initDiaLog() {
		Dialog dialog = new Dialog(context, R.style.dialog_clock_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.8f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(this);
		View more_pop_menu = fac.inflate(R.layout.dialog_view, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = getWindowManager().getDefaultDisplay().getWidth();
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		dialog.show();

		new MyClick(dialog, more_pop_menu);
	}

	class MyClick implements View.OnClickListener {

		private Dialog dialog;
		private RelativeLayout rela_hour;
		private RelativeLayout rela_minutes;
		private int hours = 12;
		private int minutes = 0;
		private int width;
		private int centerX;
		private int centerY;
		private Calendar calendar;
		private int lastMinutes;
		private TextView dialog_tv_state;
		private int grState;// 结果为0是上午 结果为1是下午
		private String chooseHour;
		private String chooseMinute;

		private TextView tv_clock_state;
		private LinearLayout ll_clock;
		private TextView tv_clock_time;
		private TextView tv_clock_beforetime;

		@SuppressWarnings("deprecation")
		private MyClick(Dialog dialog, View more_pop_menu) {
			this.dialog = dialog;
			calendar = Calendar.getInstance();
			// if (isNeedPush) {
			String[] times;
			if ("全天".equals(time_tv.getText().toString())) {
				times = sharedPrefUtil.getString(context, ShareFile.USERFILE,
						ShareFile.ALLTIME, "08:58").split(":");
			} else {
				times = time_tv.getText().toString().split(":");
			}
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
			calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));
			// }
			rela_hour = (RelativeLayout) more_pop_menu
					.findViewById(R.id.rela_hour);
			rela_minutes = (RelativeLayout) more_pop_menu
					.findViewById(R.id.rela_minutes);

			dialog_tv_state = (TextView) more_pop_menu
					.findViewById(R.id.dialog_tv_state);
			dialog_tv_state.setOnClickListener(this);
			tv_clock_state = (TextView) more_pop_menu
					.findViewById(R.id.tv_clock_state);
			tv_clock_state.setOnClickListener(this);
			ll_clock = (LinearLayout) more_pop_menu.findViewById(R.id.ll_clock);
			ll_clock.setOnClickListener(this);

			tv_clock_time = (TextView) more_pop_menu
					.findViewById(R.id.tv_clock_time);
			tv_clock_beforetime = (TextView) more_pop_menu
					.findViewById(R.id.tv_clock_beforetime);

			width = getWindowManager().getDefaultDisplay().getWidth();
			int haf = Utils.dipTopx(context, 40) / 2;
			centerX = width / 2 - Utils.dipTopx(context, 16);
			centerY = width / 2 - Utils.dipTopx(context, 16) - haf;

			initclockValue();
			initHour();
			initMinutes();

			grState = calendar.get(Calendar.AM_PM);
			if (grState == 0) {// 结果为0是上午 结果为1是下午
				dialog_tv_state.setText("上午");
			} else {
				dialog_tv_state.setText("下午");
			}
		}

		private void initclockValue() {
			tv_clock_time.setText(time_tv.getText().toString());
			// String beforeStr = tv_beforetime.getText().toString();
			// if ("".equals(beforeStr)) {
			// tv_clock_beforetime.setVisibility(View.GONE);
			// } else {
			// tv_clock_beforetime.setText(beforeStr);
			// }
		}

		@SuppressWarnings("deprecation")
		private void initHour() {
			int hour = calendar.get(Calendar.HOUR);
			if (hour == 0) {
				hour = 12;
			}
			int hourRadius = Utils.dipTopx(context, 90); // 设置圆半径
			for (int i = 12; i >= 1; i--) {
				int dy = (int) (hourRadius * Math.cos((Math.PI / 6.0) * i));
				int dx = (int) (hourRadius * Math.sin((Math.PI / 6.0) * i));

				int x1 = centerX + dx;
				int y1 = centerY - dy;

				String text = hours < 10 ? "0" + hours : "" + hours;

				TextView textView = new TextView(context);
				textView.setText(text);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18.0f);
				textView.setGravity(Gravity.CENTER);
				textView.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT));
				textView.setTag(i);
				textView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						chooseHour = v.getTag().toString();
						chageHour(Integer.parseInt(chooseHour));
						calendar.set(Calendar.HOUR,
								Integer.parseInt(chooseHour));
						calendar.set(Calendar.MINUTE,
								Integer.parseInt(chooseMinute));
						if (grState == 0) {// 上午
							calendar.set(Calendar.AM_PM, 0);
						} else if (grState == 1) {// 下午
							calendar.set(Calendar.AM_PM, 1);
						}
						String calendarHour = calendar
								.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
								+ calendar.get(Calendar.HOUR_OF_DAY) : ""
								+ calendar.get(Calendar.HOUR_OF_DAY);
						String calendarMinute = calendar.get(Calendar.MINUTE) < 10 ? "0"
								+ calendar.get(Calendar.MINUTE)
								: "" + calendar.get(Calendar.MINUTE);
						time_tv.setText(calendarHour + ":" + calendarMinute);
						tv_clock_time.setText(calendarHour + ":"
								+ calendarMinute);
					}
				});

				if (hour == i) {
					chooseHour = text;
					textView.setTextColor(Color.WHITE);
					textView.setBackgroundResource(R.mipmap.icon_shuzi1);
				} else {
					textView.setBackgroundDrawable(null);
				}

				rela_hour.addView(textView);

				textView.setAnimation(animTranslate(centerX, centerY, x1, y1,
						textView, 200));

				hours--;
			}
		}

		@SuppressWarnings("deprecation")
		private void initMinutes() {
			int minute = calendar.get(Calendar.MINUTE);
			int result = minute % 5;
			if (result != 0) {
				int division = minute / 5;
				minute = division * 5;
			}
			int minutesRadius = Utils.dipTopx(context, 140); // 设置圆半径
			for (int i = 0; i < 12; i++) {
				int dy = (int) (minutesRadius * Math.cos((Math.PI / 6.0) * i));
				int dx = (int) (minutesRadius * Math.sin((Math.PI / 6.0) * i));

				int x1 = centerX + dx;
				int y1 = centerY - dy;

				String text = minutes * 5 < 10 ? "0" + minutes * 5 : ""
						+ minutes * 5;

				TextView textView = new TextView(context);
				textView.setText(text);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18.0f);
				textView.setGravity(Gravity.CENTER);
				textView.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT));
				textView.setTag(text);
				textView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						chooseMinute = v.getTag().toString();
						chageMinutes(Integer.parseInt(chooseMinute) / 5);
						lastMinutes = Integer.parseInt(chooseMinute) / 5;
						calendar.set(Calendar.HOUR,
								Integer.parseInt(chooseHour));
						calendar.set(Calendar.MINUTE,
								Integer.parseInt(chooseMinute));
						if (grState == 0) {// 上午
							calendar.set(Calendar.AM_PM, 0);
						} else if (grState == 1) {// 下午
							calendar.set(Calendar.AM_PM, 1);
						}
						String calendarHour = calendar
								.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
								+ calendar.get(Calendar.HOUR_OF_DAY) : ""
								+ calendar.get(Calendar.HOUR_OF_DAY);
						String calendarMinute = calendar.get(Calendar.MINUTE) < 10 ? "0"
								+ calendar.get(Calendar.MINUTE)
								: "" + calendar.get(Calendar.MINUTE);
						time_tv.setText(calendarHour + ":" + calendarMinute);
						tv_clock_time.setText(calendarHour + ":"
								+ calendarMinute);
					}
				});
				if (minute == Integer.parseInt(text)) {
					lastMinutes = i;
					chooseMinute = text;
					textView.setTextColor(Color.WHITE);
					textView.setBackgroundResource(R.mipmap.icon_shuzi2);
				} else {
					textView.setBackgroundDrawable(null);
				}
				rela_minutes.addView(textView);
				textView.setAnimation(animTranslate(centerX, centerY, x1, y1,
						textView, 200));

				minutes++;
			}
		}

		@SuppressWarnings("deprecation")
		private void chageHour(int hours) {
			for (int i = rela_hour.getChildCount() - 1; i >= 0; i--) {
				TextView tv_hour = (TextView) rela_hour.getChildAt(i);
				if (hours == Integer.parseInt(tv_hour.getTag().toString())) {
					tv_hour.setBackgroundResource(R.mipmap.icon_shuzi1);
				} else {
					tv_hour.setBackgroundDrawable(null);
				}
			}
		}

		@SuppressWarnings("deprecation")
		private void chageMinutes(int minutes) {
			TextView textView = (TextView) rela_minutes.getChildAt(lastMinutes);
			textView.setBackgroundDrawable(null);
			TextView tv_minutes = (TextView) rela_minutes.getChildAt(minutes);
			tv_minutes.setBackgroundResource(R.mipmap.icon_shuzi2);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_tv_state:
				calendar.set(Calendar.HOUR, Integer.parseInt(chooseHour));
				calendar.set(Calendar.MINUTE, Integer.parseInt(chooseMinute));
				if (grState == 0) {// 上午
					dialog_tv_state.setText("下午");
					grState = 1;
					calendar.set(Calendar.AM_PM, grState);
				} else if (grState == 1) {// 下午
					dialog_tv_state.setText("上午");
					grState = 0;
					calendar.set(Calendar.AM_PM, grState);
				}
				dialog_tv_state.startAnimation(AnimationUtils.loadAnimation(
						context, R.anim.scale_in));
				String calendarHour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
						+ calendar.get(Calendar.HOUR_OF_DAY)
						: "" + calendar.get(Calendar.HOUR_OF_DAY);
				String calendarMinute = calendar.get(Calendar.MINUTE) < 10 ? "0"
						+ calendar.get(Calendar.MINUTE)
						: "" + calendar.get(Calendar.MINUTE);
				time_tv.setText(calendarHour + ":" + calendarMinute);
				tv_clock_time.setText(calendarHour + ":" + calendarMinute);

				break;

			case R.id.tv_clock_state:
				// isNeedPush = false;
				time_tv.setText("全天");
				// tv_beforetime.setText("");
				dialog.dismiss();

				break;

			case R.id.ll_clock:
				Intent intent = new Intent(context, ChooseTimeActivity.class);
				intent.putExtra("source", 0);
				intent.putExtra("beforeTime", beforeTime);
				intent.putExtra("timeSet", time_tv.getText().toString());
				startActivityForResult(intent, TIME_CHOOSE);
				dialog.dismiss();

				break;

			default:
				dialog.dismiss();

				break;
			}
		}
	}

	private Animation animationTranslate;
	private LayoutParams params = new LayoutParams(0, 0);

	// 移动的动画效果
	/*
	 * TranslateAnimation(float fromXDelta, float toXDelta, float
	 * fromYDelta,float toYDelta)
	 * 
	 * float fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
	 * 
	 * float toXDelta, 这个参数表示动画结束的点离当前View X坐标上的差值；
	 * 
	 * float fromYDelta, 这个参数表示动画开始的点离当前View Y坐标上的差值；
	 * 
	 * float toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
	 */
	protected Animation animTranslate(float toX, float toY, final int x1,
			final int y1, final TextView textView, long durationMillis) {
		animationTranslate = new TranslateAnimation(toX, toX, toY, toY);
		animationTranslate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				params = new LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(x1, y1, 0, 0);
				textView.setLayoutParams(params);
				textView.setPadding(12, 7, 12, 7);
				textView.clearAnimation();
			}
		});
		animationTranslate.setDuration(durationMillis);
		return animationTranslate;
	}

}
