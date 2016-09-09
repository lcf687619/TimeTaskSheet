package com.mission.schedule.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mission.schedule.R;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.CalendarChange;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.MyLinearLayout;
import com.mission.schedule.utils.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DateCalendarActivity extends BaseActivity {

	private TextView title;
	private RelativeLayout back;
	private TextView button_today;
	private MyLinearLayout dataLinear;
	private Handler handler;
	private LinearLayout.LayoutParams mLayoutParamss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
//	private SwitchButton date_cb;

	public int[] solartime = new int[25];
	private boolean isLeap = false;
	public boolean isRed = false;
	private ArrayList<HashMap<String, String>> data;
	private String today;
	private int temp_day;
	private int temp_month;
	private int temp_year;

	private int to_day;
	private int to_month;
	private int to_year;
	private int standard_day;
	private int standard_month;
	private int standard_year;
	private String holiday = "";
	private String curDate = "";

	private int sourse;// 0 EditNewFriendRiChengActivity 1 AddEverydayDetailTaskActivity 2 AddNewFocusShareRiChengActivity 3 EditNewFocusShareRiChengActivity
	private String dateTime = "";// 日期
//	private String myschedulefragment = "";
	ActivityManager1 activityManager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datecalendar);
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		bindClick();
//		createDialog("请稍后...");
		new LoadDataAsyncTask().execute();
	}

	/**
	 * 绑定事件
	 */
	private void bindClick() {
		sourse = getIntent().getIntExtra("sourse", 0);
		dateTime = getIntent().getStringExtra("dateTime");
		curDate = dateTime;
//		myschedulefragment = getIntent().getStringExtra("myschedulefragment");
		
//		date_cb = (SwitchButton) findViewById(R.id.date_cb);
//		String postpone = getIntent().getStringExtra("postpone");// 顺延
//		if (postpone.equals("1")) {
//			date_cb.setChecked(true);
//		}
		
		dataLinear = (MyLinearLayout) findViewById(R.id.datalinear);
		dataLinear.setBackgroundColor(Color.WHITE);
		title = (TextView) findViewById(R.id.title);
		back = (RelativeLayout) findViewById(R.id.back);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg==null){
					return;
				}
				switch (msg.what) {
				case 0:
					if (standard_month == 1) {
						standard_month = 12;
						standard_year--;
					} else {
						standard_month--;
					}
					GenData(false, curDate);
					GenView();
					title.setText(standard_year + "年" + standard_month + "月");
					dataLinear.startAnimation(AnimationUtils.loadAnimation(DateCalendarActivity.this, R.anim.right_in)); 
					
					break;
					
				case 1:
					if (standard_month == 12) {
						standard_month = 1;
						standard_year++;
					} else {
						standard_month++;
					}
					GenData(false, curDate);
					GenView();
					title.setText(standard_year + "年" + standard_month + "月");
					dataLinear.startAnimation(AnimationUtils.loadAnimation(DateCalendarActivity.this, R.anim.left_in));
					
					break;
					
				case 2:
					standard_year--;
					GenData(false, curDate);
					GenView();
					title.setText(standard_year + "年" + standard_month + "月");
					dataLinear.startAnimation(AnimationUtils.loadAnimation(DateCalendarActivity.this, R.anim.down_in));
					
					break;
					
				case 3:
					standard_year++;
					GenData(false, curDate);
					GenView();
					title.setText(standard_year + "年" + standard_month + "月");
					dataLinear.startAnimation(AnimationUtils.loadAnimation(DateCalendarActivity.this, R.anim.up_in));
					
					break;
					
				case 9:
					onCreateDialog(1);
					
					break;
				case 10:
					
					break;
					
				case 11:
					Toast.makeText(DateCalendarActivity.this, "网络中断", Toast.LENGTH_LONG).show();
					
					break;
					
				case 12:
					Toast.makeText(DateCalendarActivity.this, "服务器错误", Toast.LENGTH_LONG).show();
					
					break;
					
				case 13:
					
					break;
					
				}
			}
		};
		dataLinear.setHandler(handler);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = null;
				//2 AddNewFocusShareRiChengActivity 3 EditNewFocusShareRiChengActivity
				if (sourse == 0) {
					intent = new Intent(DateCalendarActivity.this, EditNewFriendRiChengActivity.class);
					intent.putExtra("dateTime", dateTime);
					setResult(Activity.RESULT_OK, intent);
				}else if(sourse == 1){
					intent = new Intent(DateCalendarActivity.this, AddEverydayDetailTaskActivity.class);
					intent.putExtra("dateTime", dateTime);
					setResult(Activity.RESULT_OK, intent);
				}else if(sourse == 2){
					intent = new Intent(DateCalendarActivity.this, AddNewFocusShareRiChengActivity.class);
					intent.putExtra("dateTime", dateTime);
					setResult(Activity.RESULT_OK, intent);
				}else if(sourse == 3){
					intent = new Intent(DateCalendarActivity.this, EditNewFocusShareRiChengActivity.class);
					intent.putExtra("dateTime", dateTime);
					setResult(Activity.RESULT_OK, intent);
				}
				DateCalendarActivity.this.finish();
			}
		});
		button_today = (TextView) findViewById(R.id.button_today);
		button_today.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				curDate = DateUtil.formatDate(new Date());// new Date()为获取当前系统时间
				loadData();
			}
		});

	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		GenData(true, curDate);
		GenView();
	}

	public ArrayList<HashMap<String, String>> GenData(boolean isfirst, String date) {
		if (!date.equals("") && isfirst) {
			data = new ArrayList<HashMap<String, String>>();
			to_year = Integer.parseInt(date.split("-")[0].toString());
			to_month = Integer.parseInt(date.split("-")[1].toString());
			to_day = Integer.parseInt(date.split("-")[2].toString());
			temp_day = to_day;
			temp_month = to_month;
			temp_year = to_year;
			standard_day = to_day;
			standard_month = to_month;
			standard_year = to_year;
			today = date;
			title.setText(standard_year + "年" + standard_month + "月");
		} else {
			if (isfirst) {
				data = new ArrayList<HashMap<String, String>>();
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				to_year = c.get(Calendar.YEAR);
				to_month = c.get(Calendar.MONTH);
				to_day = c.get(Calendar.DAY_OF_MONTH);

				to_month++;
				temp_day = to_day;
				temp_month = to_month;
				temp_year = to_year;
				standard_day = to_day;
				standard_month = to_month;
				standard_year = to_year;

				today = to_year + "-"
						+ (to_month > 9 ? "" + to_month : "0" + to_month) + "-"
						+ (to_day > 9 ? "" + to_day : "0" + to_day);
				title.setText(standard_year + "年" + standard_month + "月");
			} else {
				data = new ArrayList<HashMap<String, String>>();
				temp_day = standard_day;
				temp_month = standard_month;
				temp_year = standard_year;

			}
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("day", temp_day + "");

		map.put("isMonth", "1"); // 1是本月
		String tempStrT = temp_year + "-"
				+ (temp_month > 9 ? "" + temp_month : "0" + temp_month) + "-"
				+ (temp_day > 9 ? "" + temp_day : "0" + temp_day);
		if("".equals(StringUtils.getIsStringEqulesNull(tempStrT))){
			tempStrT = today;
		}
		if (today.equals(tempStrT)) {
			map.put("isToday", "1"); // 1是今天
			map.put("time", today);
		} else {
			map.put("isToday", "0"); // 1是今天
			map.put("time", tempStrT);
		}
		try {
			if (CalHoliday(Integer.valueOf(temp_month
					+ (temp_day > 9 ? "" + temp_day : "0" + temp_day)))) {
				map.put("lunar", holiday);
			} else {
				map.put("lunar", CalendarChange.CalendarChange(tempStrT));
			}
			if (isRed) {
				map.put("isHoliday", "1"); // 1节日
				isRed = false;
			} else {
				map.put("isHoliday", "0"); // 1节日
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		data.add(map);
		for (int i = temp_day - 1; i > 0; i--) {
			String tempStr = temp_year + "-"
					+ (temp_month > 9 ? "" + temp_month : "0" + temp_month)
					+ "-" + (i > 9 ? "" + i : "0" + i);
			map = new HashMap<String, String>();
			map.put("day", i + "");
			map.put("time", tempStr);
			map.put("isToday", "0"); // 1是今天
			map.put("isMonth", "1"); // 1是本月
			try {
				if (CalHoliday(Integer.valueOf("" + temp_month
						+ (i > 9 ? "" + i : "0" + i)))) {
					map.put("lunar", holiday);
				} else {
					map.put("lunar", CalendarChange.CalendarChange(tempStr));
				}
				if (isRed) {
					map.put("isHoliday", "1"); // 1节日
					isRed = false;
				} else {
					map.put("isHoliday", "0"); // 1节日
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			data.add(0, map);
		}
		int te_week = calWeek(temp_year, temp_month, 1);
		if (temp_month == 1) {
			temp_year--;
			temp_month = 12;
		} else {
			temp_month--;
		}
		calLeapYear(temp_year);
		if (temp_month == 4 || temp_month == 6 || temp_month == 9
				|| temp_month == 11) {
			for (int i = 30; i > 30 - te_week; i--) {
				String tempStr = temp_year + "-"
						+ (temp_month > 9 ? "" + temp_month : "0" + temp_month)
						+ "-" + (i > 9 ? "" + i : "0" + i);
				map = new HashMap<String, String>();
				map.put("day", i + "");
				map.put("time", tempStr);
				map.put("isToday", "0"); // 是今天
				map.put("isMonth", "0"); // 1是本月
				try {
					if (CalHoliday(Integer.valueOf("" + temp_month
							+ (i > 9 ? "" + i : "0" + i)))) {
						map.put("lunar", holiday);
					} else {
						map.put("lunar", CalendarChange.CalendarChange(tempStr));
					}
					if (isRed) {
						map.put("isHoliday", "1"); // 1节日
						isRed = false;
					} else {
						map.put("isHoliday", "0"); // 1节日
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				data.add(0, map);
			}
		} else if (temp_month == 2) {
			if (isLeap) {
				for (int i = 29; i > 29 - te_week; i--) {
					String tempStr = temp_year
							+ "-"
							+ (temp_month > 9 ? "" + temp_month : "0"
									+ temp_month) + "-"
							+ (i > 9 ? "" + i : "0" + i);
					map = new HashMap<String, String>();
					map.put("day", i + "");
					map.put("time", tempStr);
					map.put("isToday", "0"); // 1是今天
					map.put("isMonth", "0"); // 1是本月
					try {
						if (CalHoliday(Integer.valueOf("" + temp_month
								+ (i > 9 ? "" + i : "0" + i)))) {
							map.put("lunar", holiday);
						} else {
							map.put("lunar",
									CalendarChange.CalendarChange(tempStr));
						}
						if (isRed) {
							map.put("isHoliday", "1"); // 1节日
							isRed = false;
						} else {
							map.put("isHoliday", "0"); // 1节日
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					data.add(0, map);
				}
			} else {
				for (int i = 28; i > 28 - te_week; i--) {
					String tempStr = temp_year
							+ "-"
							+ (temp_month > 9 ? "" + temp_month : "0"
									+ temp_month) + "-"
							+ (i > 9 ? "" + i : "0" + i);
					map = new HashMap<String, String>();
					map.put("day", i + "");
					map.put("time", tempStr);
					map.put("isToday", "0"); // 1是今天
					map.put("isMonth", "0"); // 1是本月
					try {
						if (CalHoliday(Integer.valueOf("" + temp_month
								+ (i > 9 ? "" + i : "0" + i)))) {
							map.put("lunar", holiday);
						} else {
							map.put("lunar",
									CalendarChange.CalendarChange(tempStr));
						}
						if (isRed) {
							map.put("isHoliday", "1"); // 1节日
							isRed = false;
						} else {
							map.put("isHoliday", "0"); // 1节日
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					data.add(0, map);
				}
			}
		} else {
			for (int i = 31; i > 31 - te_week; i--) {
				String tempStr = temp_year + "-"
						+ (temp_month > 9 ? "" + temp_month : "0" + temp_month)
						+ "-" + (i > 9 ? "" + i : "0" + i);
				map = new HashMap<String, String>();
				map.put("day", i + "");
				map.put("time", tempStr);
				map.put("isToday", "0"); // 1是今天
				map.put("isMonth", "0"); // 1是本月
				try {
					if (CalHoliday(Integer.valueOf("" + temp_month
							+ (i > 9 ? "" + i : "0" + i)))) {
						map.put("lunar", holiday);
					} else {
						map.put("lunar", CalendarChange.CalendarChange(tempStr));
					}
					if (isRed) {
						map.put("isHoliday", "1"); // 1节日
						isRed = false;
					} else {
						map.put("isHoliday", "0"); // 1节日
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				data.add(0, map);
			}
		}
		if (temp_month == 12) {
			temp_year++;
			temp_month = 1;
		} else {
			temp_month = temp_month + 1;
		}
		calLeapYear(temp_year);
		if (temp_month == 4 || temp_month == 6 || temp_month == 9
				|| temp_month == 11) {
			for (int i = to_day + 1; i <= 30; i++) {
				String tempStr = temp_year + "-"
						+ (temp_month > 9 ? "" + temp_month : "0" + temp_month)
						+ "-" + (i > 9 ? "" + i : "0" + i);
				map = new HashMap<String, String>();
				map.put("day", i + "");
				map.put("time", tempStr);
				map.put("isToday", "0"); // 1是今天
				map.put("isMonth", "1"); // 1是本月
				try {
					if (CalHoliday(Integer.valueOf("" + temp_month
							+ (i > 9 ? "" + i : "0" + i)))) {
						map.put("lunar", holiday);
					} else {
						map.put("lunar", CalendarChange.CalendarChange(tempStr));
					}
					if (isRed) {
						map.put("isHoliday", "1"); // 1节日
						isRed = false;
					} else {
						map.put("isHoliday", "0"); // 1节日
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				data.add(map);
			}
		} else if (temp_month == 2) {
			if (isLeap) {
				for (int i = to_day + 1; i <= 29; i++) {
					String tempStr = temp_year
							+ "-"
							+ (temp_month > 9 ? "" + temp_month : "0"
									+ temp_month) + "-"
							+ (i > 9 ? "" + i : "0" + i);
					map = new HashMap<String, String>();
					map.put("day", i + "");
					map.put("time", tempStr);
					map.put("isToday", "0"); // 是今天
					map.put("isMonth", "1"); // 1是本月
					try {
						if (CalHoliday(Integer.valueOf("" + temp_month
								+ (i > 9 ? "" + i : "0" + i)))) {
							map.put("lunar", holiday);
						} else {
							map.put("lunar",
									CalendarChange.CalendarChange(tempStr));
						}
						if (isRed) {
							map.put("isHoliday", "1"); // 1节日
							isRed = false;
						} else {
							map.put("isHoliday", "0"); // 1节日
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					data.add(map);
				}
			} else {
				for (int i = to_day + 1; i <= 28; i++) {
					String tempStr = temp_year
							+ "-"
							+ (temp_month > 9 ? "" + temp_month : "0"
									+ temp_month) + "-"
							+ (i > 9 ? "" + i : "0" + to_day);
					map = new HashMap<String, String>();
					map.put("day", i + "");
					map.put("time", tempStr);
					map.put("isToday", "0"); // 是今天
					map.put("isMonth", "1"); // 1是本月
					try {
						if (CalHoliday(Integer.valueOf("" + temp_month
								+ (i > 9 ? "" + i : "0" + i)))) {
							map.put("lunar", holiday);
						} else {
							map.put("lunar",
									CalendarChange.CalendarChange(tempStr));
						}
						if (isRed) {
							map.put("isHoliday", "1"); // 1节日
							isRed = false;
						} else {
							map.put("isHoliday", "0"); // 1节日
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					data.add(map);
				}
			}
		} else {
			for (int i = to_day + 1; i <= 31; i++) {
				String tempStr = temp_year + "-"
						+ (temp_month > 9 ? "" + temp_month : "0" + temp_month)
						+ "-" + (i > 9 ? "" + i : "0" + i);
				map = new HashMap<String, String>();
				map.put("day", i + "");
				map.put("time", tempStr);
				map.put("isToday", "0"); // 是今天
				map.put("isMonth", "1"); // 1是本月
				try {
					if (CalHoliday(Integer.valueOf("" + temp_month
							+ (i > 9 ? "" + i : "0" + i)))) {
						map.put("lunar", holiday);
					} else {
						map.put("lunar", CalendarChange.CalendarChange(tempStr));
					}
					if (isRed) {
						map.put("isHoliday", "1"); // 1节日
						isRed = false;
					} else {
						map.put("isHoliday", "0"); // 1节日
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				data.add(map);
			}
		}
		if (temp_month >= 12) {
			temp_year++;
			temp_month = 1;
		} else {
			temp_month++;
		}
		int mm = data.size();
		for (int i = 1; i <= 42 - mm; i++) {
			String tempStr = temp_year + "-"
					+ (temp_month > 9 ? "" + temp_month : "0" + temp_month)
					+ "-" + (i > 9 ? "" + i : "0" + i);
			map = new HashMap<String, String>();
			map.put("day", i + "");
			map.put("time", tempStr);
			map.put("isToday", "0"); // 是今天
			map.put("isMonth", "0"); // 1是本月
			try {
				if (CalHoliday(Integer.valueOf("" + temp_month
						+ (i > 9 ? "" + i : "0" + i)))) {
					map.put("lunar", holiday);
				} else {
					map.put("lunar", CalendarChange.CalendarChange(tempStr));
				}
				if (isRed) {
					map.put("isHoliday", "1"); // 1节日
					isRed = false;
				} else {
					map.put("isHoliday", "0"); // 1节日
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			data.add(map);
		}
		return data;
	}

	public boolean CalHoliday(int t) {
		boolean is = false;
		switch (t / 100) {
		case 2:
			if (t == solartime[0]) {
				holiday = "立春";
				is = true;
			} else if (t == solartime[1]) {
				holiday = "雨水";
				is = true;
			}
			break;
		case 3:
			if (t == solartime[2]) {
				holiday = "惊蛰";
				is = true;
			} else if (t == solartime[3]) {
				holiday = "春分";
				is = true;
			}
			break;
		case 4:
			if (t == solartime[4]) {
				holiday = "清明";
				is = true;
			} else if (t == solartime[5]) {
				holiday = "谷雨";
				is = true;
			}
			break;
		case 5:
			if (t == solartime[6]) {
				holiday = "立夏";
				is = true;
			} else if (t == solartime[7]) {
				holiday = "小满";
				is = true;
			}
			break;
		case 6:
			if (t == solartime[8]) {
				holiday = "芒种";
				is = true;
			} else if (t == solartime[9]) {
				holiday = "夏至";
				is = true;
			}
			break;
		case 7:
			if (t == solartime[10]) {
				holiday = "小暑";
				is = true;
			} else if (t == solartime[11]) {
				holiday = "大暑";
				is = true;
			}
			break;
		case 8:
			if (t == solartime[12]) {
				holiday = "立秋";
				is = true;
			} else if (t == solartime[13]) {
				holiday = "处暑";
				is = true;
			}
			break;
		case 9:
			if (t == solartime[14]) {
				holiday = "白露";
				is = true;
			} else if (t == solartime[15]) {
				holiday = "秋分";
				is = true;
			}
			break;
		case 10:
			if (t == solartime[16]) {
				holiday = "寒露";
				is = true;
			} else if (t == solartime[17]) {
				holiday = "霜降";
				is = true;
			}
			break;
		case 11:
			if (t == solartime[18]) {
				holiday = "立冬";
				is = true;
			} else if (t == solartime[19]) {
				holiday = "小雪";
				is = true;
			}
			break;
		case 12:
			if (t == solartime[20]) {
				holiday = "大雪";
				is = true;
			} else if (t == solartime[21]) {
				holiday = "冬至";
				is = true;
			}
			break;
		case 1:
			if (t == solartime[22]) {
				holiday = "小寒";
				is = true;
			} else if (t == solartime[23]) {
				holiday = "大寒";
				is = true;
			}
			break;
		}
		switch (t) {
		case 101:
			holiday = "元旦";
			isRed = true;
			is = true;
			break;
		case 214:
			holiday = "情人节";
			is = true;
			break;
		case 308:
			holiday = "妇女节";
			is = true;
			break;
		case 312:
			holiday = "植树节";
			is = true;
			break;
		case 315:
			holiday = "消费日";
			is = true;
			break;
		case 401:
			holiday = "愚人节";
			is = true;
			break;
		// case 407:
		// holiday="卫生日";
		// is=true;
		// break;
		case 501:
			holiday = "劳动节";
			is = true;
			break;
		case 504:
			holiday = "青年节";
			is = true;
			break;
		// case 512:
		// holiday="护士节";
		// is=true;
		// break;
		// case 513:
		// holiday="助残日";
		// is=true;
		// break;

		case 601:
			holiday = "儿童节";
			is = true;
			break;
		case 605:
			holiday = "世界环境日";
			is = true;
			break;
		case 701:
			holiday = "建党节";
			is = true;
			break;
		case 801:
			holiday = "建军节";
			is = true;
			break;
		case 910:
			holiday = "教师节";
			is = true;
			break;
		case 1001:
			holiday = "国庆节";
			is = true;
			break;
		case 1225:
			holiday = "圣诞节";
			is = true;
			break;
		default:
			// is=false;
			break;
		}

		return is;
	}

	@SuppressWarnings("deprecation")
	public void GenView() {
		dataLinear.removeAllViews();
		mLayoutParamss.weight = 1;
		LayoutInflater lif = LayoutInflater.from(DateCalendarActivity.this);
		LinearLayout temp = new LinearLayout(DateCalendarActivity.this);
		int size = data.size();
		for (int i = 0; i < size; i++) {
			View convertView = lif.inflate(R.layout.gritem, null);
			TextView time = (TextView) convertView.findViewById(R.id.time);
			TextView lunar = (TextView) convertView.findViewById(R.id.lunar);
			TextView fraction = (TextView) convertView.findViewById(R.id.fraction);
			LinearLayout itemroot = (LinearLayout) convertView.findViewById(R.id.itemroot);
			HashMap<String, String> map = data.get(i);
			if ((i + 1) < size) {
				HashMap<String, String> map2 = data.get(i + 1);
				if (map2.get("lunar").equals("春节")) {
					map.put("lunar", "除夕");
				}
			}
			if (map.get("isMonth").equals("1"))// 1是本月
			{
				itemroot.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_riliactivity));
				if (map.get("isToday").equals("1"))// 1是今天
				{
					time.setTextColor(getResources().getColor(R.color.sunday_txt));
					lunar.setTextColor(getResources().getColor(R.color.sunday_txt));
					itemroot.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_riliactivity_jintian));
				} else {
					itemroot.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_riliactivity));
				}
				int m = (i + 1) % 7;
				if (m == 1 || m == 0) {
					time.setTextColor(Color.rgb(50, 79, 133));
				}
				if (map.get("isHoliday").equals("1")) // 节日
				{
					time.setTextColor(Color.RED);
				}
				itemroot.setTag(R.id.calendar_index,"1");//0其他,1是本月
			} else {
				time.setTextColor(getResources().getColor(R.color.othermonth_txt));
				lunar.setTextColor(getResources().getColor(R.color.othermonth_txt));
				itemroot.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_riliactivity));
				itemroot.setTag(R.id.calendar_index,"0");
			}
			String day = map.get("day");
			fraction.setText(map.get("fraction"));
			time.setText(day);
			lunar.setText(map.get("lunar"));
			itemroot.setTag(day);
			itemroot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String day = v.getTag().toString();
					// 0其他,1是本月
					String calendar_index = v.getTag(R.id.calendar_index)
							.toString();
					if ("1".equals(calendar_index)) {
						Intent intent = null;
						String dateTime = Integer.toString(standard_year)
								+ "-"
								+ (standard_month > 9 ? "" + standard_month
										: "0" + standard_month) + "-"
								+ (Integer.parseInt(day) > 9 ? day : "0" + day);
						if (sourse == 0) {
							intent = new Intent(DateCalendarActivity.this,
									EditNewFriendRiChengActivity.class);
							intent.putExtra("dateTime", dateTime);
							setResult(Activity.RESULT_OK, intent);
//							URLConstants.doEdit();
						} else if (sourse == 1) {
							intent = new Intent(DateCalendarActivity.this,
									AddEverydayDetailTaskActivity.class);
							intent.putExtra("dateTime", dateTime);
							setResult(Activity.RESULT_OK, intent);
						}else if(sourse == 2){
							intent = new Intent(DateCalendarActivity.this, AddNewFocusShareRiChengActivity.class);
							intent.putExtra("dateTime", dateTime);
							setResult(Activity.RESULT_OK, intent);
						}else if(sourse == 3){
							intent = new Intent(DateCalendarActivity.this, EditNewFocusShareRiChengActivity.class);
							intent.putExtra("dateTime", dateTime);
							setResult(Activity.RESULT_OK, intent);
						}
						DateCalendarActivity.this.finish();
					}
				}
			});
			if ((i + 1) % 7 != 0) {
				temp.addView(convertView, mLayoutParamss);
			} else {
				if (i != 0) {
					temp.addView(convertView, mLayoutParamss);
					dataLinear.addView(temp, mLayoutParamss);
					temp = new LinearLayout(DateCalendarActivity.this);
				}
			}
		}
	}

	/*
	 * 基姆拉尔森计算公式 QgW4jIbx W= (d+2*m+3*(m+1)/5+y+y/4-y/100+y/400) mod 7 /
	 * 
	 * d 天 m 月 y 年 1月2月换算为去年的13 14月计算
	 * 
	 * 基姆拉尔森计算公式的C#的实现 %NfbgJcL_ //y：年，m：月，d：日。在参数都只传入相应的整数
	 */
	private int calWeek(int y, int m, int d) {
		int a = 7; // 用来保存计算得到的星期几的整数 Dz>v;%$S-
		if ((m == 1) || (m == 2))// 如果是一月或二月进行换算 uWKc .
		{
			m += 12;
			y--;
		}
		a = (d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400) % 7; // 得到的星期几的整数
																				// 
																				// 7ehs+GI
		a++;
		if (a == 7) {
			a = 0;
		}
		return a;
	}

	public void calLeapYear(int i) {
		if (i % 100 == 0) {
			if (i % 400 == 0) {
				isLeap = true;
			} else {
				isLeap = false;
			}
		} else {
			if (i % 4 == 0) {
				isLeap = true;
			} else {
				isLeap = false;
			}
		}
	}

	@Override
	public boolean onKeyDown(int kCode, KeyEvent kEvent) {
		switch (kCode) {
		case KeyEvent.KEYCODE_BACK: {
			Intent intent = null;
			if (sourse == 0) {
				intent = new Intent(DateCalendarActivity.this, EditNewFriendRiChengActivity.class);
			}else if(sourse == 1){
				intent = new Intent(DateCalendarActivity.this, AddEverydayDetailTaskActivity.class);
//				intent = new Intent(DateCalendarActivity.this, ExportActivity.class);
			}else if(sourse == 2){
				intent = new Intent(DateCalendarActivity.this, AddNewFocusShareRiChengActivity.class);
			}else if(sourse == 3){
				intent = new Intent(DateCalendarActivity.this, EditNewFocusShareRiChengActivity.class);
			}
//			if (date_cb.isChecked()) {// 顺延
//				intent.putExtra("postpone", "开");
//			} else {
//				intent.putExtra("postpone", "关");
//			}
			intent.putExtra("dateTime", dateTime);
			setResult(Activity.RESULT_OK, intent);
			DateCalendarActivity.this.finish();
		}
			break;
		case KeyEvent.KEYCODE_MENU: {

		}
			break;
		}
		return false;
	}

	class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
//			dialog.dismiss();
			GenData(true, curDate);
			GenView();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
//		if (dialog != null)
//			dialog.dismiss();
	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void setContentView() {
	}

	@Override
	protected void init(Bundle savedInstanceState) {
	}

	@Override
	protected void setAdapter() {
	}

}
