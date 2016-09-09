package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.service.NewFriendDataUpLoadService;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;
import com.mission.schedule.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class AddNewFriendRepeatActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_send)
	private LinearLayout top_ll_send;
	@ViewResId(id = R.id.date_tv)
	private TextView date_tv;
	@ViewResId(id = R.id.time_tv)
	private TextView time_tv;
	@ViewResId(id = R.id.timeanniu_imag)
	private ImageView timeanniu_imag;
	@ViewResId(id = R.id.tixing_tv)
	private TextView tixing_tv;
	@ViewResId(id = R.id.lingsheng_ll)
	private LinearLayout lingsheng_ll;
	@ViewResId(id = R.id.morenlingshen_tv)
	private TextView morenlingshen_tv;
	@ViewResId(id = R.id.addrepeatcontent_et)
	private EditText addrepeatcontent_et;
	@ViewResId(id = R.id.choosecontent_tv)
	private TextView choosecontent_tv;
	@ViewResId(id = R.id.dateselect_tv)
	private TextView dateselect_tv;
	@ViewResId(id = R.id.time_rl)
	private RelativeLayout time_rl;
	@ViewResId(id = R.id.line_view)
	private View line_view;
	@ViewResId(id = R.id.friendsName_tv)
	private TextView friendsName_tv;

	SharedPrefUtil prefUtil = null;

	Context context;
	private static final int DATE_CHOOSE = 1;
	private static final int TIME_CHOOSE = 2;// 选择时间
	private static final int CHOOSE_MUSIC = 3;// 选择铃声
	private static final int REQUESTCODE_WEEK = 4;// 周
	private static final int REQUESTCODE_DAY = 5;// 每月的天
	private static final int REQUESTCODE_DATE = 6;// 日期(每年)
	private static final int URL_SELECT = 7;// 附加信息

	App app = null;

	private String datetype;
	private int beforeTime;
	int timeInt = 0;
	int lastIndex;
	int timestr;
	int timeState;
	RepeatBean repeatbean = null;
	String dateStr;
	String timeStr = "准时提醒";
	String dateselect;
	private String weeks = "0";
	private String day;
	private String yearType = "0";// 0 公立 1 农历
	private int month;
	private String key_tpLcDate = "";
	private String key_tpDate = "";// 日期

	String url = "";
	String ringcode;
	int setBefore;
	String ringdesc;

	int isAlarm = 3; // 共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟

	String friendname;
	int userid;
	int friendid;
	String username;
	CalendarChangeValue changeValue = null;
	String parstr = "";
	String alltime = "";

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_send.setOnClickListener(this);
		date_tv.setOnClickListener(this);
		time_tv.setOnClickListener(this);
		timeanniu_imag.setOnClickListener(this);
		tixing_tv.setOnClickListener(this);
		lingsheng_ll.setOnClickListener(this);
		choosecontent_tv.setOnClickListener(this);
		dateselect_tv.setOnClickListener(this);
		time_rl.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_addnewfriendrepeat);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		app = App.getDBcApplication();
		changeValue = new CalendarChangeValue();
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		datetype = getIntent().getStringExtra("date");
		friendname = getIntent().getStringExtra("friendname");
		friendid = getIntent().getIntExtra("friendid", 0);
		setBefore = Integer.parseInt(prefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.BEFORETIME, "0"));
		ringdesc = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICDESC, "完成任务");
		ringcode = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICCODE, "g_88");
		userid = Integer.parseInt(prefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.USERID, "0"));
		username = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		alltime = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ALLTIME, "08:58");
		friendsName_tv.setText(friendname);
		Date date = new Date();
		String[] ymdHm = DateUtil.formatDateTime(date).split(" ");
		if ("每天".equals(datetype)) {
			dateselect_tv.setVisibility(View.GONE);
			line_view.setVisibility(View.GONE);
		} else if ("每周".equals(datetype)) {
			dateselect_tv.setVisibility(View.VISIBLE);
			line_view.setVisibility(View.VISIBLE);
			dateselect_tv.setText(getWeekOfDate(date));
			// 当前日期
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int curWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (curWeek == 0) {// 星期天改为7
				curWeek = 7;
			}
			weeks += "," + curWeek;
		} else if ("每月".equals(datetype)) {
			dateselect_tv.setVisibility(View.VISIBLE);
			line_view.setVisibility(View.VISIBLE);
			day = ymdHm[0].split("-")[2];
			dateselect_tv.setText(day + "日");
		} else if ("每年".equals(datetype)) {
			dateselect_tv.setVisibility(View.VISIBLE);
			line_view.setVisibility(View.VISIBLE);
			dateselect_tv.setText(ymdHm[0].split("-")[1] + "-"
					+ ymdHm[0].split("-")[2]);
			month = Integer.parseInt(ymdHm[0].split("-")[1]);
			day = ymdHm[0].split("-")[2];
		} else {
			dateselect_tv.setVisibility(View.GONE);
			line_view.setVisibility(View.GONE);
		}
		date_tv.setText(datetype);
		time_tv.setText(DateUtil.formatDateTimeHm(new Date()));
		// }

		if ("全天".equals(time_tv.getText().toString())) {
			timeState = 0;
		} else {
			timeState = 1;
		}
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
		} else if (setBefore == 1440) {
			beforeStr = "1天";
		} else if (setBefore == 2 * 1440) {
			beforeStr = "2天";
		} else if (setBefore == 7 * 1440) {
			beforeStr = "1周";
		}
		if (setBefore == 0) {
			tixing_tv.setText("准时提醒");
			isAlarm = 1;
		} else {
			isAlarm = 3;
			tixing_tv.setText("提前" + beforeStr);
		}
		morenlingshen_tv.setText(ringdesc);
		initValues();
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
		case R.id.choosecontent_tv:
			intent = new Intent(context, AddRepeateWebUrlActivity.class);
			intent.putExtra("url", url);
			startActivityForResult(intent, URL_SELECT);
			break;
		case R.id.top_ll_send:
			String berforeStr = tixing_tv.getText().toString();
			int urlstate = 0;
			if ("".equals(url)) {
				urlstate = 0;
			} else {
				urlstate = 1;
			}
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
			String datetypeStr = date_tv.getText().toString();
			int dateIndex = 1;
			if ("每天".equals(datetypeStr)) {
				dateIndex = 1;
			} else if ("每周".equals(datetypeStr)) {
				dateIndex = 2;
			} else if ("每月".equals(datetypeStr)) {
				dateIndex = 3;
			} else if ("每年".equals(datetypeStr)) {
				dateIndex = 4;
			} else if ("工作日".equals(datetypeStr)) {
				dateIndex = 5;
			}
			int state;
			String alltime = "";
			if ("全天".equals(time_tv.getText().toString())) {
				timeState = 0;
				alltime = prefUtil.getString(context, ShareFile.USERFILE,
						ShareFile.ALLTIME, "08:58");
			} else {
				timeState = 1;
				alltime = time_tv.getText().toString();
			}
			int atype = 0;
			if ("".equals(url)) {
				atype = 0;
			} else {
				atype = 1;
			}
			if (addrepeatcontent_et.getText().toString().trim() != null
					&& !addrepeatcontent_et.getText().toString().trim()
							.equals("")) {
				try {
					boolean flag = false;
					RepeatBean bean = null;
					if ("每天".equals(date_tv.getText().toString())) {
						bean = RepeatDateUtils.saveCalendar(alltime, 1, "", "");
						flag = app.insertMessageSendData(userid, friendid, 0,
								0, 2, isAlarm, 0, 0, timeState, before, 0, 1,
								atype, 0, 0, 0, 0, "[]", addrepeatcontent_et
										.getText().toString().trim(),
								DateUtil.formatDate(new Date()), alltime, "",
								"", "", morenlingshen_tv.getText().toString()
										.trim(), ringcode,
								bean.repNextCreatedTime,
								DateUtil.formatDateTime(new Date()),
								bean.repLastCreatedTime,
								bean.repNextCreatedTime, url, "", username, "",
								1, 0, 0, "", 0, 0, "", 0, parstr,
								DateUtil.formatDateTimeSs(new Date()));
						if (flag) {
							app.insertMessageSendData(userid, friendid, 0, 0,
									1, isAlarm, 0, 0, timeState, before, 0, 0,
									atype, 0, 0, 0, 0, "[]",
									addrepeatcontent_et.getText().toString()
											.trim(),
									bean.repNextCreatedTime.substring(0, 10),
									alltime, "", "", "", morenlingshen_tv
											.getText().toString().trim(),
									ringcode, bean.repNextCreatedTime,
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, url, "", username,
									"", 0, app.nfmId, 0, "", 0, 0, "", 0,
									parstr,
									DateUtil.formatDateTimeSs(new Date()));
						}
					} else if ("工作日".equals(date_tv.getText().toString())) {
						bean = RepeatDateUtils.saveCalendar(alltime, 5, "", "");
						flag = app.insertMessageSendData(userid, friendid, 0,
								0, 2, isAlarm, 0, 0, timeState, before, 0, 5,
								atype, 0, 0, 0, 0, "[]", addrepeatcontent_et
										.getText().toString().trim(),
								DateUtil.formatDate(new Date()), alltime, "",
								"", "", morenlingshen_tv.getText().toString()
										.trim(), ringcode,
								bean.repNextCreatedTime,
								DateUtil.formatDateTime(new Date()),
								bean.repLastCreatedTime,
								bean.repNextCreatedTime, url, "", username, "",
								1, 0, 0, "", 0, 0, "", 0, parstr,
								DateUtil.formatDateTimeSs(new Date()));
						if (flag) {
							app.insertMessageSendData(userid, friendid, 0, 0,
									1, isAlarm, 0, 0, timeState, before, 0, 0,
									atype, 0, 0, 0, 0, "[]",
									addrepeatcontent_et.getText().toString()
											.trim(),
									bean.repNextCreatedTime.substring(0, 10),
									alltime, "", "", "", morenlingshen_tv
											.getText().toString().trim(),
									ringcode, bean.repNextCreatedTime,
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, url, "", username,
									"", 0, app.nfmId, 0, "", 0, 0, "", 0,
									parstr,
									DateUtil.formatDateTimeSs(new Date()));
						}
					} else if ("每周".equals(date_tv.getText().toString())) {
						bean = RepeatDateUtils.saveCalendar(alltime, 2,
								dateselect_tv.getText().toString(), "");
						String str = "";
						if (dateselect_tv.getText().toString().contains("星期")) {
							if ("一".equals(dateselect_tv
									.getText()
									.toString()
									.substring(
											2,
											dateselect_tv.getText().toString()
													.length()))) {
								str = "["+ "\"" + 1+ "\"" + "]";
							} else if ("二".equals(dateselect_tv
									.getText()
									.toString()
									.substring(
											2,
											dateselect_tv.getText().toString()
													.length()))) {
								str = "[" + "\"" + 2 + "\"" + "]";
							} else if ("三".equals(dateselect_tv
									.getText()
									.toString()
									.substring(
											2,
											dateselect_tv.getText().toString()
													.length()))) {
								str = "[" + "\"" + 3 + "\"" + "]";
							} else if ("四".equals(dateselect_tv
									.getText()
									.toString()
									.substring(
											2,
											dateselect_tv.getText().toString()
													.length()))) {
								str = "[" + "\"" + 4 + "\"" + "]";
							} else if ("五".equals(dateselect_tv
									.getText()
									.toString()
									.substring(
											2,
											dateselect_tv.getText().toString()
													.length()))) {
								str = "[" + "\"" + 5 + "\"" + "]";
							} else if ("六".equals(dateselect_tv
									.getText()
									.toString()
									.substring(
											2,
											dateselect_tv.getText().toString()
													.length()))) {
								str = "[" + "\"" + 6 + "\"" + "]";
							} else {
								str = "[" + "\"" + 7 + "\"" + "]";
							}

						}
						flag = app.insertMessageSendData(userid, friendid, 0,
								0, 2, isAlarm, 0, 0, timeState, before, 0, 2,
								atype, 0, 0, 0, 0, str, addrepeatcontent_et
										.getText().toString().trim(),
								DateUtil.formatDate(new Date()), alltime, "",
								"", "", morenlingshen_tv.getText().toString()
										.trim(), ringcode,
								bean.repNextCreatedTime,
								DateUtil.formatDateTime(new Date()),
								bean.repLastCreatedTime,
								bean.repNextCreatedTime, url, "", username, "",
								1, 0, 0, "", 0, 0, "", 0, parstr,
								DateUtil.formatDateTimeSs(new Date()));
						if (flag) {
							app.insertMessageSendData(userid, friendid, 0, 0,
									1, isAlarm, 0, 0, timeState, before, 0, 0,
									atype, 0, 0, 0, 0, str, addrepeatcontent_et
											.getText().toString().trim(),
									bean.repNextCreatedTime.substring(0, 10),
									alltime, "", "", "", morenlingshen_tv
											.getText().toString().trim(),
									ringcode, bean.repNextCreatedTime,
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, url, "", username,
									"", 0, app.nfmId, 0, "", 0, 0, "", 0,
									parstr,
									DateUtil.formatDateTimeSs(new Date()));
						}
					} else if ("每月".equals(date_tv.getText().toString())) {
						bean = RepeatDateUtils.saveCalendar(alltime, 3,
								dateselect_tv.getText().toString(), "");
						String str = "";
						if (dateselect_tv.getText().toString().contains("日")) {
							str = "[" + "\""
									+ dateselect_tv
											.getText()
											.toString()
											.substring(
													0,
													dateselect_tv.getText()
															.toString()
															.length() - 1) + "\""
									+ "]";
						}
						flag = app.insertMessageSendData(userid, friendid, 0,
								0, 2, isAlarm, 0, 0, timeState, before, 0, 3,
								atype, 0, 0, 0, 0, str, addrepeatcontent_et
										.getText().toString().trim(),
								DateUtil.formatDate(new Date()), alltime, "",
								"", "", morenlingshen_tv.getText().toString()
										.trim(), ringcode,
								bean.repNextCreatedTime,
								DateUtil.formatDateTime(new Date()),
								bean.repLastCreatedTime,
								bean.repNextCreatedTime, url, "", username, "",
								1, 0, 0, "", 0, 0, "", 0, parstr,
								DateUtil.formatDateTimeSs(new Date()));
						if (flag) {
							app.insertMessageSendData(userid, friendid, 0, 0,
									1, isAlarm, 0, 0, timeState, before, 0, 0,
									atype, 0, 0, 0, 0, str, addrepeatcontent_et
											.getText().toString().trim(),
									bean.repNextCreatedTime.substring(0, 10),
									alltime, "", "", "", morenlingshen_tv
											.getText().toString().trim(),
									ringcode, bean.repNextCreatedTime,
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, url, "", username,
									"", 0, app.nfmId, 0, "", 0, 0, "", 0,
									parstr,
									DateUtil.formatDateTimeSs(new Date()));
						}
					} else {
						String str = "";
						str = "[" + "\"" + dateselect_tv.getText().toString() + "\"" + "]";
						bean = RepeatDateUtils.saveCalendar(alltime, 4,
								dateselect_tv.getText().toString(), yearType);
						if ("0".equals(yearType)) {
							flag = app.insertMessageSendData(userid, friendid,
									0, 0, 2, isAlarm, 0, 0, timeState, before,
									0, 4, atype, 0, 0, 0, 0, str,
									addrepeatcontent_et.getText().toString()
											.trim(),
									DateUtil.formatDate(new Date()), alltime,
									"", "", "", morenlingshen_tv.getText()
											.toString().trim(), ringcode,
									bean.repNextCreatedTime,
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, url, "", username,
									"", 1, 0, 0, "", 0, 0, "", 0, parstr,
									DateUtil.formatDateTimeSs(new Date()));
						} else {
							parstr = changeValue.changaSZ(dateselect_tv
									.getText().toString());
							flag = app.insertMessageSendData(userid, friendid,
									0, 0, 2, isAlarm, 0, 0, timeState, before,
									0, 6, atype, 0, 0, 0, 0, str,
									addrepeatcontent_et.getText().toString()
											.trim(),
									DateUtil.formatDate(new Date()), alltime,
									"", "", "", morenlingshen_tv.getText()
											.toString().trim(), ringcode,
									bean.repNextCreatedTime,
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, url, "", username,
									"", 1, 0, 0, "", 0, 0, "", 0, parstr,
									DateUtil.formatDateTimeSs(new Date()));
						}
						if (flag) {
							app.insertMessageSendData(userid, friendid, 0, 0,
									1, isAlarm, 0, 0, timeState, before, 0, 0,
									atype, 0, 0, 0, 0, str, addrepeatcontent_et
											.getText().toString().trim(),
									bean.repNextCreatedTime.substring(0, 10),
									alltime, "", "", "", morenlingshen_tv
											.getText().toString().trim(),
									ringcode, bean.repNextCreatedTime,
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, url, "", username,
									"", 0, app.nfmId, 0, "", 0, 0, "", 0, "",
									DateUtil.formatDateTimeSs(new Date()));
						}

					}
					if (flag) {
						intent = new Intent(context,
								NewFriendDataUpLoadService.class);
						intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
						intent.setPackage(getPackageName());
						startService(intent);
						this.finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
				this.finish();
			} else {
				Toast.makeText(context, "重复内容不可以为空!", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			break;
		case R.id.date_tv:
			intent = new Intent(context, ChooseDateTypeActivity.class);
			intent.putExtra("datetype", date_tv.getText().toString());
			startActivityForResult(intent, DATE_CHOOSE);
			break;
		case R.id.dateselect_tv:
			if ("每周".equals(date_tv.getText().toString())) {
				intent = new Intent(this, WeekSelectActivity.class);
				intent.putExtra("weeks", weeks);
				startActivityForResult(intent, REQUESTCODE_WEEK);
			} else if ("每月".equals(date_tv.getText().toString())) {
				intent = new Intent(this, DaySelectActivity.class);
				intent.putExtra("type", 2);
				intent.putExtra("dayS", day);
				startActivityForResult(intent, REQUESTCODE_DAY);
			} else {
				intent = new Intent(this, NewDateSetActivity.class);
				intent.putExtra("month", month);
				intent.putExtra("day", day);
				startActivityForResult(intent, REQUESTCODE_DATE);
			}
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
		case R.id.tixing_tv:
			initBeforeDiaLog();
			break;
		case R.id.lingsheng_ll:
			intent = new Intent(context, LingShengActivity.class);
			startActivityForResult(intent, CHOOSE_MUSIC);
			break;
		default:
			break;
		}
	}

	private void selectTimeView() {
		String[] ymdHm = DateUtil.formatDateTime(new Date()).split(" ");
		int result = Integer.parseInt(ymdHm[1].split(":")[1]) % 5;
		if ("全天".equals(time_tv.getText().toString())) {
			time_tv.setText(DateUtil.formatDateTimeHm(new Date()));
		}
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
						+ (timeChoose < 10 ? "0" + timeChoose : timeChoose));
			}
		}
		initDiaLog();
		
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
		} else if (setBefore == 1440) {
			lastIndex = 6;
		} else if (setBefore == 2 * 1440) {
			lastIndex = 7;
		} else if (setBefore == 7 * 1440) {
			lastIndex = 8;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (DATE_CHOOSE == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				String datetype = data.getStringExtra("datetype");
				date_tv.setText(datetype);
				DateTypeSelect();
			}
		}
		if (requestCode == TIME_CHOOSE) {
			if (resultCode == Activity.RESULT_OK) {
				String timeSet = data.getStringExtra("timeSet");
				beforeTime = data.getIntExtra("beforeTime", 5);
				time_tv.setText(timeSet);
				if ("全天".equals(time_tv.getText().toString())) {
					timeState = 0;
				} else {
					timeState = 1;
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
		} else if (CHOOSE_MUSIC == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				String lingshengname = data.getStringExtra("lingshengname");
				ringcode = data.getStringExtra("code");
				morenlingshen_tv.setText(lingshengname);
			}
		} else if (requestCode == REQUESTCODE_WEEK) {// 每周
			if (resultCode == Activity.RESULT_OK) {
				weeks = data.getStringExtra("weeks");
				dateselect_tv.setText(weeks);
			}
		} else if (requestCode == REQUESTCODE_DAY) {
			if (resultCode == Activity.RESULT_OK) {
				String strDay = data.getStringExtra("dayS");
				day = strDay;
				dateselect_tv.setText(day + "日");
			}
		} else if (requestCode == REQUESTCODE_DATE) {// 每年
			if (resultCode == Activity.RESULT_OK) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				String str = "";
				String[] monthStr = getResources().getStringArray(
						R.array.monthStr);
				String[] lunarStr = getResources().getStringArray(
						R.array.lunarstr);
				List<String> dateList = data.getStringArrayListExtra("list");
				yearType = data.getStringExtra("type");
				String monthLunar = "";
				String dayLunar = "";
				List<String> mList = new ArrayList<String>();
				if (dateList != null && dateList.size() > 0) {
					if ("0".equals(yearType)) {
						for (int i = 0; i < dateList.size(); i++) {
							if (dateList.size() == 1) {
								str = dateList.get(i);
							} else if (dateList.size() == 2) {
								str = dateList.get(0) + "、" + dateList.get(i);
							} else if (dateList.size() == 3) {
								str = dateList.get(0) + "、" + dateList.get(1)
										+ "、" + dateList.get(i);
							} else if (dateList.size() == 4) {
								str = dateList.get(0) + "、" + dateList.get(1)
										+ "、" + dateList.get(2) + "、"
										+ dateList.get(i);
							} else if (dateList.size() == 5) {
								str = dateList.get(0) + "、" + dateList.get(1)
										+ "、" + dateList.get(2) + "、"
										+ dateList.get(3) + "、"
										+ dateList.get(i);
							}
						}
					} else {
						for (int i = 0; i < dateList.size(); i++) {
							for (int j = 1; j <= monthStr.length; j++) {
								if (Integer
										.parseInt(dateList.get(i).split("-")[0]) == j) {
									monthLunar = monthStr[j - 1];
								}
							}
							for (int j = 1; j <= lunarStr.length; j++) {
								if (Integer
										.parseInt(dateList.get(i).split("-")[1]) == j) {
									dayLunar = lunarStr[j - 1];
								}
							}
							mList.add(monthLunar + dayLunar);
						}
						for (int i = 0; i < mList.size(); i++) {
							if (mList.size() == 1) {
								str = mList.get(i);
							} else if (mList.size() == 2) {
								str = mList.get(0) + "、" + mList.get(i);
							} else if (mList.size() == 3) {
								str = mList.get(0) + "、" + mList.get(1) + "、"
										+ mList.get(i);
							} else if (mList.size() == 4) {
								str = mList.get(0) + "、" + mList.get(1) + "、"
										+ mList.get(2) + "、" + mList.get(i);
							} else if (mList.size() == 5) {
								str = mList.get(0) + "、" + mList.get(1) + "、"
										+ mList.get(2) + "、" + mList.get(3)
										+ "、" + dateList.get(i);
							}
						}
					}

					if ("0".equals(yearType)) {
						dateselect_tv.setText(str);
					} else {
						dateselect_tv.setText(str);
					}
				}
			}
		} else if (URL_SELECT == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				url = data.getStringExtra("url");
				if ("".equals(url)) {
					choosecontent_tv.setText("附加信息");
				} else {
					choosecontent_tv.setText("附加信息      URL");
				}
			}
		}
	}

	private void DateTypeSelect() {
		Date date = new Date();
		String[] ymdHm = DateUtil.formatDateTime(date).split(" ");
		if ("每天".equals(date_tv.getText().toString())) {
			dateselect_tv.setVisibility(View.GONE);
			line_view.setVisibility(View.GONE);
		} else if ("每周".equals(date_tv.getText().toString())) {
			dateselect_tv.setVisibility(View.VISIBLE);
			line_view.setVisibility(View.VISIBLE);
			dateselect_tv.setText(getWeekOfDate(date));
			// 当前日期
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int curWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (curWeek == 0) {// 星期天改为7
				curWeek = 7;
			}
			weeks += "," + curWeek;
		} else if ("每月".equals(date_tv.getText().toString())) {
			dateselect_tv.setVisibility(View.VISIBLE);
			dateselect_tv.setVisibility(View.VISIBLE);
			line_view.setVisibility(View.VISIBLE);
			day = ymdHm[0].split("-")[2];
			dateselect_tv.setText(day + "日");
		} else if ("每年".equals(date_tv.getText().toString())) {
			dateselect_tv.setVisibility(View.VISIBLE);
			line_view.setVisibility(View.VISIBLE);
			dateselect_tv.setText(ymdHm[0].split("-")[1] + "-"
					+ ymdHm[0].split("-")[2]);
			month = Integer.parseInt(ymdHm[0].split("-")[1]);
			day = ymdHm[0].split("-")[2];
		} else {
			dateselect_tv.setVisibility(View.GONE);
			line_view.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Date date = new Date();
		if ("每天".equals(date_tv.getText().toString())) {
			line_view.setVisibility(View.GONE);
			dateselect_tv.setVisibility(View.GONE);
		} else if ("每周".equals(date_tv.getText().toString())) {
			line_view.setVisibility(View.VISIBLE);
			dateselect_tv.setVisibility(View.VISIBLE);
		} else if ("每月".equals(date_tv.getText().toString())) {
			line_view.setVisibility(View.VISIBLE);
			dateselect_tv.setVisibility(View.VISIBLE);
		} else if ("每年".equals(date_tv.getText().toString())) {
			line_view.setVisibility(View.VISIBLE);
			dateselect_tv.setVisibility(View.VISIBLE);
		} else {
			line_view.setVisibility(View.GONE);
			dateselect_tv.setVisibility(View.GONE);
		}

	}

	/**
	 * 根据日期获得星期
	 * 
	 * @param date
	 * @return
	 */
	private String getWeekOfDate(Date date) {
		String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
				"星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}

	// ========================================================================================//
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

		private MyClick(Dialog dialog, View more_pop_menu) {
			this.dialog = dialog;
			calendar = Calendar.getInstance();
			// if (isNeedPush) {
			String[] times;
			if ("全天".equals(time_tv.getText().toString())) {
				times = prefUtil.getString(context, ShareFile.USERFILE,
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
			tv_clock_state.setVisibility(View.GONE);
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
			if (map.get(position)) {
				before_ll.setBackgroundColor(Color.parseColor("#F4E8C2"));
				before_tv.setTextColor(Color.parseColor("#F24040"));
			} else {
				before_ll.setBackgroundColor(Color.parseColor("#fef8f0"));
				before_tv.setTextColor(Color.parseColor("#938761"));
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
					String retStrFormatNowDate = DateUtil
							.formatDate(new Date());
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
						Date nowTime = new Date(DateUtil.parseDateTime(
								DateUtil.formatDate(new Date()) + " "
										+ selecttime)
								.getTime()
								- before * 60 * 1000);
						String retStrFormatNowDate = DateUtil
								.formatDateTime(nowTime);
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
						String beforenowtime = DateUtil
								.formatDateTime(new Date());
						Date nowtime = new Date(DateUtil.parseDateTime(
								DateUtil.formatDate(new Date()) + " "
										+ selecttime)
								.getTime()
								- number * 60 * 1000);
						String retStrFormatNowDate = DateUtil
								.formatDateTime(nowtime);
						year_tv.setText(retStrFormatNowDate.substring(0, 10));
						HHmmtime_tv.setText(retStrFormatNowDate.substring(11,
								16));
						year_tv1.setText(beforenowtime.substring(0, 10));
						time_tv1.setText(selecttime);
					}
				}
			} else {
				ll.setVisibility(View.GONE);
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

}
