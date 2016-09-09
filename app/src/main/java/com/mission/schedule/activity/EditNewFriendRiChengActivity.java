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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewFriendBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBackBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.service.NewFriendDataUpLoadService;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditNewFriendRiChengActivity extends BaseActivity implements
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
	String editmessage;
	String userid;// 用户id
	String userName;
	int friendid;// 好友id
	String message;// 消息
	String date;// 闹钟日期（格式2012-1-1）
	String time;// 闹钟时间（格式16：22：00）
	String niCheng;// 昵称
	String alamsound;// 铃声
	String alamsoundDesc;// 铃声描述
	SharedPrefUtil sharedPrefUtil = null;
	List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
	String json;
	App app;
	int timestate;
	String tixingStr = "准时提醒";
	int timeInt = 0;
	String ymd = "";
	String ymdState = "";
	private int beforeTime = 0;
	private int lastIndex;
	String url = "";
	String openstate;
	String displaytime;
	String postpone;
	String yeardate;
	String timedate;
	int nfmID;
	int beforetime;
	NewFriendBean bean = null;

	int isAlarm; // 共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟

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
		bean = (NewFriendBean) getIntent().getSerializableExtra("bean");
		nfmID = bean.nfmId;
		userid = getIntent().getStringExtra("userId");
		friendid = getIntent().getIntExtra("friendId", 0);
		niCheng = getIntent().getStringExtra("friendName");
		message = bean.nfmContent;
		displaytime = bean.nfmDisplayTime + "";
		postpone = bean.nfmPostpone + "";
		yeardate = bean.nfmDate;
		timedate = bean.nfmTime;
		editmessage_et.setText(message);
		editmessage_et.setSelection(message.length());
		friendsName_tv.setText(niCheng);
		alamsound = bean.nfmRingCode;
		alamsoundDesc = bean.nfmRingDesc;
		isAlarm = bean.nfmIsAlarm;
		beforetime = bean.nfmBeforeTime;
		editmessage = editmessage_et.getText().toString().trim();

		openstate = bean.nfmOpenState + "";
		userName = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		if ("0".equals(displaytime)) {
			time_tv.setText("全天");
		} else {
			time_tv.setText(timedate);
		}
		rilibianhua_tv.setText(yeardate);
		if ("全天".equals(time_tv.getText().toString())) {
			timestate = 0;
		} else {
			timestate = 1;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());
		if (today.equals(yeardate)) {
			newbuild_ymd_state.setText("今天"
					+ CharacterUtil.getWeekOfDate(this, new Date()));
		} else if (tomorrow.equals(yeardate)) {
			newbuild_ymd_state.setText("明天"
					+ CharacterUtil.getWeekOfDate(this,
							DateUtil.parseDate(tomorrow)));
		} else {
			Date dateStr = DateUtil.parseDate(yeardate);
			Date dateToday = DateUtil
					.parseDate(DateUtil.formatDate(new Date()));
			long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
			long day = betweem / (24 * 3600);
			newbuild_ymd_state.setText(Math.abs(day)
					+ 1
					+ "天后"
					+ CharacterUtil.getWeekOfDate(this,
							DateUtil.parseDate(yeardate)));
		}
		String beforestr;
		if (beforetime == 0) {
			beforestr = tixingStr;
		} else if (beforetime == 5) {
			beforestr = "提前5分钟";
		} else if (beforetime == 15) {
			beforestr = "提前15分钟";
		} else if (beforetime == 30) {
			beforestr = "提前30分钟";
		} else if (beforetime == 60) {
			beforestr = "提前1小时";
		} else if (beforetime == 120) {
			beforestr = "提前2小时";
		} else if (beforetime == 24 * 60) {
			beforestr = "提前1天";
		} else if (beforetime == 48 * 60) {
			beforestr = "提前2天";
		} else if (beforetime == 7 * 24 * 60) {
			beforestr = "提前1周";
		} else {
			beforestr = tixingStr;
		}
		initValues();
		morenlingshen_tv.setText(alamsoundDesc);
		tixing_tv.setText(beforestr);
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
				if (tixingStr.equals(berforeStr)) {
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
				int atype = 0;
				if ("".equals(url)) {
					atype = 0;
				} else {
					atype = 1;
				}
				int updatestate = 0;
				if (nfmID <= 0) {
					updatestate = 1;
				} else {
					updatestate = 2;
				}
				if (0 != bean.nfmPId
						&& (0 == bean.nfmSubState || 3 == bean.nfmSubState)) {
					app.alterNewFriendParentData(bean.nfmPId, 1, bean.nfmDate
							+ " " + bean.nfmTime);
				}
				app.alterNewFriendsData(nfmID, Integer.parseInt(userid),
						friendid, bean.nfmCalendarId, Integer
								.parseInt(openstate), 1, isAlarm,
						bean.nfmPostpone, bean.nfmColorType, timestate, before,
						bean.nfmSourceType, 0, atype, 0, bean.nfmIsEnd, 0,
						bean.nfmPostState, "", message, date, DateUtil
								.formatDateTimeHm(DateUtil
										.parseDateTimeHm(time)),
						bean.nfmSourceDesc, bean.nfmSourceDescSpare,
						bean.nfmTags, alamsoundDesc, alamsound,
						bean.nfmStartDate, bean.nfmInitialCreatedTime,
						bean.nfmLastCreatedTime, bean.nfmNextCreatedTime, url,
						"", userName, bean.nfmRemark, updatestate, 0, 0, "",
						bean.nfmCState, bean.nfmSubEnd, bean.nfmSubEndDate,
						bean.nfmIsPuase, "", DateUtil
								.formatDateTimeSs(new Date()));
				intent = new Intent(context, NewFriendDataUpLoadService.class);
				intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
				intent.setPackage(getPackageName());
				startService(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
			this.finish();
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
		String[] ymdHm1;
		if ("全天".equals(time_tv.getText().toString())) {
			ymdHm1 = DateUtil.formatDateTime(new Date()).split(" ");
		} else {
			ymdHm1 = (DateUtil.formatDate(new Date()) + " " + time_tv
					.getText().toString()).split(" ");
		}
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
					time_tv.setText(Integer.parseInt(ymdHm1[1].split(":")[0])
							+ 1 + ":" + "00");
				}
			} else {
				time_tv.setText(ymdHm1[1].split(":")[0] + ":"
						+ (timeChoose < 10 ? "0" + timeChoose : timeChoose));
			}
		}
		// tv_beforetime.setText("(" + beforTimetp + ")");
//		String timestr = time_tv.getText().toString();

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
				newbuild_ymd_state.startAnimation(AnimationUtils
						.loadAnimation(context, R.anim.fade_in));
			}
		});
		rilibianhua_tv.startAnimation(animation);
		newbuild_ymd_state.startAnimation(animation);		
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
			String timestr = time_tv.getText().toString();
			System.out.println("1555===》》" + timestr);
			// if (isNeedPush) {
			String[] times;
			if ("全天".equals(timestr)) {
				times = sharedPrefUtil.getString(context, ShareFile.USERFILE,
						ShareFile.ALLTIME, "08:30").split(":");
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
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			this.finish();
		}
	}

	private void UpdateLoadAsync(String path, final String json) {
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在发送...");
		StringRequest request = new StringRequest(Method.POST, path,
				new Listener<String>() {

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
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						Intent intent = new Intent();
						setResult(RESULT_OK, intent);
						finish();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
