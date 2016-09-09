package com.mission.schedule.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.service.NewFocusShareService;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewFocusShareRiChengActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;// 返回键
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;// 保存
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;// 名称
	@ViewResId(id = R.id.addeverytask_tv)
	private EditText addeverytask_tv;// 提醒内容
	@ViewResId(id = R.id.date_ll)
	private LinearLayout date_ll;// 日历增加按钮
	@ViewResId(id = R.id.rili_tv)
	private TextView rili_tv;// 日历
	@ViewResId(id = R.id.rilibianhua_tv)
	private TextView rilibianhua_tv;// 日历显示的变化
	@ViewResId(id = R.id.time_tv)
	private TextView time_tv;// 时间
	@ViewResId(id = R.id.timeanniu_imag)
	private ImageView timeanniu_imag;// 时间选择按钮
	@ViewResId(id = R.id.tixing_tv)
	private TextView tixing_tv;// 准时提醒
	@ViewResId(id = R.id.morenlingshen_tv)
	private TextView morenlingshen_tv;// 铃声形式:默认
	@ViewResId(id = R.id.newbuild_ymd_state)
	private TextView newbuild_ymd_state;
	@ViewResId(id = R.id.lingsheng_ll)
	private LinearLayout lingsheng_ll;
	@ViewResId(id = R.id.rili_ll)
	private LinearLayout rili_ll;
	@ViewResId(id = R.id.qingyingyong_tv)
	private TextView qingyingyong_tv;
	@ViewResId(id = R.id.head_title)
	private TextView head_title;
	@ViewResId(id = R.id.time_rl)
	private RelativeLayout time_rl;

	Context mcontext;
	private int lastIndex;

	private static final int TIME_CHOOSE = 100;// 选择时间
	private static final int REQUEST_YMD = 101;// 年
	private static final int CHOOSE_MUSIC = 102;// 选择铃声
	private static final int TIXING_NAME = 103;// 选择铃声
	private static final int URL_SELECT = 104;// 附加信息

	public String UserId;
	String ymd = "";
	String ymdState = "";
	private int beforeTime = 0;
	String timeStr = "准时提醒";
	int timeInt = 0;
	String id;
	SharedPrefUtil sharedPrefUtil = null;
	int timestr;

	int beforetime;

	String lingshengname;
	String url = "";
	String imagepath = "";
	String ringcode;// 铃声对应的编码

	int setBefore;
	String ringdesc = "完成任务";
	String alltime = "";
	int isAlarm = 3; // 共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
	App app = App.getDBcApplication();
	int ctgId;
	String tagname;
	int coclor = 0;

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_addnewfocussharericheng);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		mcontext = this;
		sharedPrefUtil = new SharedPrefUtil(mcontext, ShareFile.USERFILE);
		if ("全天".equals(time_tv.getText().toString())) {
			timestr = 0;
		} else {
			timestr = 1;
		}
		middle_tv.setText(NewFocusShareEditActivity.been.name);
		UserId = sharedPrefUtil.getString(mcontext, ShareFile.USERFILE,
				ShareFile.USERID, "");
		setBefore = Integer.parseInt(sharedPrefUtil.getString(mcontext,
				ShareFile.USERFILE, ShareFile.BEFORETIME, "0"));
		ringdesc = sharedPrefUtil.getString(mcontext, ShareFile.USERFILE,
				ShareFile.MUSICDESC, "完成任务");
		alltime = sharedPrefUtil.getString(mcontext, ShareFile.USERFILE,
				ShareFile.ALLTIME, "08:58");
		Date dateToday = new Date();

		ringcode = sharedPrefUtil.getString(mcontext, ShareFile.USERFILE,
				ShareFile.MUSICCODE, "g_88");
		rilibianhua_tv.setText(DateUtil.formatDate(dateToday));
		newbuild_ymd_state.setText("今天  "
				+ CharacterUtil.getWeekOfDate(this, dateToday));
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
		initValues();
		if (setBefore == 0) {
			tixing_tv.setText(beforeStr);
			isAlarm = 1;
		} else {
			isAlarm = 3;
			tixing_tv.setText("提前" + beforeStr);
		}
		time_tv.setText(DateUtil.formatDateTime(dateToday).substring(11, 16)
				.toString());
		morenlingshen_tv.setText(ringdesc);
	}

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
		rili_tv.setOnClickListener(this);
		rilibianhua_tv.setOnClickListener(this);
		date_ll.setOnClickListener(this);
		time_tv.setOnClickListener(this);
		timeanniu_imag.setOnClickListener(this);
		lingsheng_ll.setOnClickListener(this);
		tixing_tv.setOnClickListener(this);
		rili_ll.setOnClickListener(this);
		qingyingyong_tv.setOnClickListener(this);
		time_rl.setOnClickListener(this);
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
		case R.id.top_ll_right:
			int urlstate = 0;
			if (!"".equals(url) && !"".equals(imagepath)) {
				urlstate = 3;
			} else if ("".equals(url) && !"".equals(imagepath)) {
				urlstate = 2;
			} else if (!"".equals(url) && "".equals(imagepath)) {
				urlstate = 1;
			} else {
				urlstate = 0;
			}

			String berforeStr = tixing_tv.getText().toString();
			int before = 0;
			if (timeStr.equals(berforeStr)) {
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
			int displaytime = 1;
			String time = "";
			if ("全天".equals(time_tv.getText().toString())) {
				displaytime = 0;
				time = DateUtil.formatDateTimeHm(new Date());
			} else {
				displaytime = 1;
				time = time_tv.getText().toString();
			}
			Date date = new Date();
			String schUpdateTime = DateUtil.formatDateTimeSs(date);
			String createTime = DateUtil.formatDateTimeSs(date);
			app.insertNewFocusData(NewFocusShareEditActivity.been.id, 0,
					before, isAlarm, displaytime, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, urlstate, 1, "", addeverytask_tv
							.getText().toString(), rilibianhua_tv.getText()
							.toString(), time, ringcode, ringdesc, "", "", "",
					"", "", "", "", "", "", "", "", url, imagepath, "",
					createTime, schUpdateTime, "", "", "", "", "");
			if (NetUtil.getConnectState(mcontext) != NetWorkState.NONE) {
				upload();
			}
		    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
			this.finish();
			break;
		case R.id.qingyingyong_tv:
			intent = new Intent(mcontext, AddSchWebUrlAndImageActivity.class);
			intent.putExtra("weburl", url);
			intent.putExtra("imagepath", imagepath);
			intent.putExtra("isedit", 1);
			startActivityForResult(intent, URL_SELECT);
			break;
		case R.id.tixing_tv:
			initBeforeDiaLog();
			break;
		case R.id.lingsheng_ll:
			intent = new Intent(mcontext, LingShengActivity.class);
			startActivityForResult(intent, CHOOSE_MUSIC);
			break;
		case R.id.rili_tv:
			intent = new Intent(this, DateCalendarActivity.class);
			intent.putExtra("sourse", 2);
			intent.putExtra("dateTime", rilibianhua_tv.getText().toString());
			// intent.putExtra("postpone", postpone);
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
		case R.id.timeanniu_imag:
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
		if (!"".equals(id)) {
			int result = Integer.parseInt(time_tv.getText().toString()
					.split(":")[1]) % 5;
			if (result == 0) {
			} else {
				int division = Integer.parseInt(time_tv.getText()
						.toString().split(":")[1]) / 5;
				int timeChoose = (division + 1) * 5;
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
							+ (timeChoose < 10 ? "0" + timeChoose
									: timeChoose));
				}
			}
		} else {
			String[] ymdHm = DateUtil.formatDateTime(new Date()).split(" ");
			int result = Integer.parseInt(ymdHm[1].split(":")[1]) % 5;
			if (result == 0) {
				time_tv.setText(ymdHm[1]);
			} else {
				int division = Integer.parseInt(ymdHm[1].split(":")[1]) / 5;
				int timeChoose = (division + 1) * 5;
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
							+ (timeChoose < 10 ? "0" + timeChoose
									: timeChoose));
				}
			}
		}
		// tv_beforetime.setText("(" + beforTimetp + ")");
		initDiaLog();
	}

	private void selectDateView() {
		Calendar calendar = Calendar.getInstance();
		String[] ymdStr;
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
				ymdState = "昨天  ";
			} else {
				ymdState = count + "天前  ";
			}
		} else if (count == 0) {
			ymdState = "今天  ";
		} else {
			if (Math.abs(count) == 1) {
				ymdState = "明天  ";
			} else {
				ymdState = Math.abs(count) + "天后  ";
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
						mcontext, R.anim.fade_in));
				newbuild_ymd_state.startAnimation(AnimationUtils
						.loadAnimation(mcontext, R.anim.fade_in));
			}
		});
		rilibianhua_tv.startAnimation(animation);
		newbuild_ymd_state.startAnimation(animation);
	}

	private void initValues() {
		if (setBefore == timeInt) {
			lastIndex = 0;
		} else if (setBefore == 5) {
			lastIndex = 1;
		} else if (setBefore == 15) {
			lastIndex = 2;
		} else if (setBefore == 30) {
			lastIndex = 3;
		} else if (setBefore == 60) {
			lastIndex = 4;
		} else if (setBefore == 120) {
			lastIndex = 5;
		} else if (setBefore == 24 * 60) {
			lastIndex = 6;
		} else if (setBefore == 48 * 60) {
			lastIndex = 7;
		} else if (setBefore == 7 * 24 * 60) {
			lastIndex = 8;
		}
	}

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
		Dialog dialog = new Dialog(mcontext, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(mcontext);
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
			height = Utils.dipTopx(mcontext, 100);
			befStrings = mcontext.getResources().getStringArray(
					R.array.before_time);
			tixingcishu_tv.setText("1");

			detail_close.setOnClickListener(this);
		}

		private void initdata() {
			if (!"".equals(id) && id != null) {
				if (1 == isAlarm || 3 == isAlarm) {
					map.put(0, true);
				} else {
					map.put(0, false);
				}
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
				adapter = new GridBeforeAdapter(mcontext, befStrings, height,
						index);
				before_gridview.setAdapter(adapter);

			} else {
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
				adapter = new GridBeforeAdapter(mcontext, befStrings, height,
						index);
				before_gridview.setAdapter(adapter);
			}
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
			if ("全天".equals(time_tv.getText().toString())) {
				selecttime = alltime;
			} else {
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
												.toString() + " " + selecttime));
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
										.toString() + " " + selecttime));
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
		Dialog dialog = new Dialog(mcontext, R.style.dialog_clock_translucent);
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

		private MyClick(Dialog dialog, View more_pop_menu) {
			this.dialog = dialog;
			calendar = Calendar.getInstance();
			// if (isNeedPush) {
			String[] times;
			if ("全天".equals(time_tv.getText().toString())) {
				times = sharedPrefUtil.getString(mcontext, ShareFile.USERFILE,
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
			int haf =Utils.dipTopx(mcontext, 40) / 2;
			centerX = width / 2 - Utils.dipTopx(mcontext, 16);
			centerY = width / 2 - Utils.dipTopx(mcontext, 16) - haf;

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

		private void initHour() {
			int hour = calendar.get(Calendar.HOUR);
			if (hour == 0) {
				hour = 12;
			}
			int hourRadius = Utils.dipTopx(mcontext, 90); // 设置圆半径
			for (int i = 12; i >= 1; i--) {
				int dy = (int) (hourRadius * Math.cos((Math.PI / 6.0) * i));
				int dx = (int) (hourRadius * Math.sin((Math.PI / 6.0) * i));

				int x1 = centerX + dx;
				int y1 = centerY - dy;

				String text = hours < 10 ? "0" + hours : "" + hours;

				TextView textView = new TextView(mcontext);
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

		private void initMinutes() {
			int minute = calendar.get(Calendar.MINUTE);
			int result = minute % 5;
			if (result != 0) {
				int division = minute / 5;
				minute = division * 5;
			}
			int minutesRadius = Utils.dipTopx(mcontext, 140); // 设置圆半径
			for (int i = 0; i < 12; i++) {
				int dy = (int) (minutesRadius * Math.cos((Math.PI / 6.0) * i));
				int dx = (int) (minutesRadius * Math.sin((Math.PI / 6.0) * i));

				int x1 = centerX + dx;
				int y1 = centerY - dy;

				String text = minutes * 5 < 10 ? "0" + minutes * 5 : ""
						+ minutes * 5;

				TextView textView = new TextView(mcontext);
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
						mcontext, R.anim.scale_in));
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
				Intent intent = new Intent(mcontext, ChooseTimeActivity.class);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TIME_CHOOSE) {
			if (resultCode == Activity.RESULT_OK) {
				String timeSet = data.getStringExtra("timeSet");
				beforeTime = data.getIntExtra("beforeTime", 5);
				time_tv.setText(timeSet);
				if ("全天".equals(time_tv.getText().toString())) {
					timestr = 0;
				} else {
					timestr = 1;
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

				// String post_tv = data.getStringExtra("postpone");// 顺延
				// if (post_tv.equals("开")) {
				// postpone = "1";
				// } else {
				// postpone = "0";
				// }
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
				ymdState += CharacterUtil.getWeekOfDate(mcontext,
						DateUtil.parseDate(key_tpDate));
				newbuild_ymd_state.setText(ymdState);
			}
		} else if (CHOOSE_MUSIC == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				String lingshengname = data.getStringExtra("lingshengname");
				ringdesc = lingshengname;
				ringcode = data.getStringExtra("code");
				morenlingshen_tv.setText(lingshengname);
			}
		} else if (TIXING_NAME == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				String tixingname = data.getStringExtra("name");
				addeverytask_tv.setText(tixingname);
				addeverytask_tv.setSelection(tixingname.length());
			}
		} else if (URL_SELECT == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				url = data.getStringExtra("url");
				imagepath = data.getStringExtra("imagepath");
				if (!"".equals(url) && !"".equals(imagepath)) {
					qingyingyong_tv.setText("附加信息     URL+图片");
				} else if(!"".equals(url) && "".equals(imagepath)){
					qingyingyong_tv.setText("附加信息     URL");
				} else if("".equals(url) && !"".equals(imagepath)){
					qingyingyong_tv.setText("附加信息     图片");
				} else{
					qingyingyong_tv.setText("附加信息");
				}
			}
		}
	}

	private void upload() {
		sharedPrefUtil.putString(mcontext, ShareFile.USERFILE,
				ShareFile.FOCUSUPDATETIME,
				DateUtil.formatDateTimeSs(new Date()));
		Intent intent = new Intent(mcontext, NewFocusShareService.class);
		intent.setPackage(getPackageName());
		intent.setAction("up");
		startService(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
