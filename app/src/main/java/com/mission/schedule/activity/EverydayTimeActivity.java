package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.view.CycleWheelView;
import com.mission.schedule.view.CycleWheelView.CycleWheelViewException;
import com.mission.schedule.view.CycleWheelView.WheelItemSelectedListener;
import com.mission.schedule.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EverydayTimeActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.right_tv)
	private TextView right_tv;
	@ViewResId(id = R.id.toggle_morning)
	private ToggleButton toggle_morning;
	@ViewResId(id = R.id.morning_time_tv)
	private TextView morning_time_tv;
	@ViewResId(id = R.id.toggle_night)
	private ToggleButton toggle_night;
	@ViewResId(id = R.id.night_time_tv)
	private TextView night_time_tv;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	String morningstate;
	String nightstate;
	String morningtime;
	String nighttime;
	boolean morningFlag = false;
	boolean nightFlag = false;

	ProgressUtil progressUtil = new ProgressUtil();

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
		morning_time_tv.setOnClickListener(this);
		night_time_tv.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_everydaytine);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		middle_tv.setText("每日问候");
		right_tv.setText("确定");
		morningstate = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MORNINGSTATE, "0");
		nightstate = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.NIGHTSTATE, "0");
		morningtime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MORNINGTIME, "07:58");
		nighttime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.NIGHTTIME, "20:58");
		morning_time_tv.setText(morningtime);
		night_time_tv.setText(nighttime);
		if ("0".equals(morningstate)) {
			morningFlag = true;
		} else {
			morningFlag = false;
		}
		if ("0".equals(nightstate)) {
			nightFlag = true;
		} else {
			nightFlag = false;
		}
		toggle_morning.setChecked(morningFlag);
		toggle_night.setChecked(nightFlag);
		toggle_morning
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 选中
							sharedPrefUtil.putString(context,
									ShareFile.USERFILE, ShareFile.MORNINGSTATE,
									"0");
						} else {
							// 未选中
							sharedPrefUtil.putString(context,
									ShareFile.USERFILE, ShareFile.MORNINGSTATE,
									"1");
						}
					}
				});
		toggle_night.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// 选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.NIGHTSTATE, "0");
				} else {
					// 未选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.NIGHTSTATE, "1");
				}
			}
		});
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
						ShareFile.MORNINGTIME, morning_time_tv.getText()
								.toString());
				sharedPrefUtil
						.putString(context, ShareFile.USERFILE,
								ShareFile.NIGHTTIME, night_time_tv.getText()
										.toString());
				String today, tomorrow;
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				today = DateUtil.formatDate(calendar.getTime());
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 1);
				tomorrow = DateUtil.formatDate(calendar.getTime());
				if ("0".equals(sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.MORNINGSTATE, "0"))) {
					int count = App.getDBcApplication().CheckClockIDData(-1);
					if (count == 0) {
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.MORNINGTIME, "07:58"))
								.before(DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					} else {
						App.getDBcApplication().deleteEveryClock(-1);
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.MORNINGTIME, "07:58"))
								.before(DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					}
				} else {
					App.getDBcApplication().deleteEveryClock(-1);
				}
				if ("0".equals(sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.NIGHTSTATE, "0"))) {

					int count = App.getDBcApplication().CheckClockIDData(-2);
					if (count == 0) {
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.NIGHTTIME, "20:58")).before(
								DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					} else {
						App.getDBcApplication().deleteEveryClock(-2);
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.NIGHTTIME, "18:58")).before(
								DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					}
				} else {
					App.getDBcApplication().deleteEveryClock(-2);
				}
				AlterSet();
				QueryAlarmData.writeAlarm(getApplicationContext());
			} else {
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.MORNINGTIME, morning_time_tv.getText()
								.toString());
				sharedPrefUtil
						.putString(context, ShareFile.USERFILE,
								ShareFile.NIGHTTIME, night_time_tv.getText()
										.toString());
				String today, tomorrow;
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				today = DateUtil.formatDate(calendar.getTime());
				calendar.set(Calendar.DAY_OF_MONTH,
						calendar.get(Calendar.DAY_OF_MONTH) + 1);
				tomorrow = DateUtil.formatDate(calendar.getTime());
				if ("0".equals(sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.MORNINGSTATE, "0"))) {
					int count = App.getDBcApplication().CheckClockIDData(-1);
					if (count == 0) {
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.MORNINGTIME, "07:58"))
								.before(DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					} else {
						App.getDBcApplication().deleteEveryClock(-1);
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.MORNINGTIME, "07:58"))
								.before(DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-1,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"早上问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.MORNINGTIME,
																	"07:58"))),
											"", "morninghello.wav", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					}
				} else {
					App.getDBcApplication().deleteEveryClock(-1);
				}
				if ("0".equals(sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.NIGHTSTATE, "0"))) {

					int count = App.getDBcApplication().CheckClockIDData(-2);
					if (count == 0) {
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.NIGHTTIME, "18:58")).before(
								DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					} else {
						App.getDBcApplication().deleteEveryClock(-2);
						if (DateUtil.parseDateTimeHm(
								sharedPrefUtil.getString(context,
										ShareFile.USERFILE,
										ShareFile.NIGHTTIME, "18:58")).before(
								DateUtil.parseDateTimeHm(DateUtil
										.formatDateTimeHm(new Date())))) {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(tomorrow
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						} else {
							App.getDBcApplication()
									.insertEveryClockData(
											-2,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"下午问候",
											0,
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(today
													+ " "
													+ sharedPrefUtil
															.getString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.NIGHTTIME,
																	"20:58"))),
											"", "nighthello.ogg", 1, 1, 7, 0,
											0, 1, 0, "", 0, 0, "", "");
						}
					}
				} else {
					App.getDBcApplication().deleteEveryClock(-2);
				}
				QueryAlarmData.writeAlarm(getApplicationContext());
				this.finish();
			}
			break;
		case R.id.morning_time_tv:
			dialogMorningTimeOnClick();
			break;
		case R.id.night_time_tv:
			dialogNightTimeOnClick();
			break;
		default:
			break;
		}
	}

	private void dialogMorningTimeOnClick() {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_everydaytime, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		dialog.show();

		new TimeOnClick(dialog, more_pop_menu);
	}

	class TimeOnClick {

		private View view;
		private Dialog dialog;
		// private PickerView hour_pv;
		// private PickerView minute_pv;
		CycleWheelView hour_cy;
		CycleWheelView min_cy;
		String hour;
		String minute;
		private String timeSet = "";

		@SuppressLint("NewApi")
		public TimeOnClick(Dialog dialog, View view) {
			this.dialog = dialog;
			this.view = view;
			initview();
			initdata();
		}

		private void initview() {
			hour_cy = (CycleWheelView) view.findViewById(R.id.hour_cy);
			min_cy = (CycleWheelView) view.findViewById(R.id.min_cy);
		}

		private void initdata() {
			timeSet = morning_time_tv.getText().toString();
			int curHours = Integer.parseInt(timeSet.split(":")[0]);
			int curMinutes = Integer.parseInt(timeSet.split(":")[1]);
			hour = curHours + "";
			minute = curMinutes + "";
			int currentHoursIndex = 0;
			int currentMinutesIndex = 0;
			List<String> hours = new ArrayList<String>();
			List<String> minutes = new ArrayList<String>();
			for (int i = 0; i < 24; i++) {
				hours.add(i < 10 ? "0" + i : "" + i);

			}
			for (int i = 0; i < 60; i++) {
				minutes.add(i < 10 ? "0" + i : "" + i);
			}
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
					morning_time_tv.setText(timeSet);
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
					morning_time_tv.setText(timeSet);
				}
			});
		}
	}

	private void dialogNightTimeOnClick() {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_everydaytime, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		dialog.show();

		new NightTimeOnClick(dialog, more_pop_menu);
	}

	class NightTimeOnClick {

		private View view;
		private Dialog dialog;
		CycleWheelView hour_cy;
		CycleWheelView min_cy;
		String hour;
		String minute;
		private String timeSet = "";

		@SuppressLint("NewApi")
		public NightTimeOnClick(Dialog dialog, View view) {
			this.dialog = dialog;
			this.view = view;
			initview();
			initdata();
		}

		private void initview() {
			hour_cy = (CycleWheelView) view.findViewById(R.id.hour_cy);
			min_cy = (CycleWheelView) view.findViewById(R.id.min_cy);
		}

		private void initdata() {
			timeSet = night_time_tv.getText().toString();
			int curHours = Integer.parseInt(timeSet.split(":")[0]);
			int curMinutes = Integer.parseInt(timeSet.split(":")[1]);
			hour = curHours + "";
			minute = curMinutes + "";
			int currentHoursIndex = 0;
			int currentMinutesIndex = 0;
			List<String> hours = new ArrayList<String>();
			List<String> minutes = new ArrayList<String>();
			for (int i = 0; i < 24; i++) {
				hours.add(i < 10 ? "0" + i : "" + i);

			}
			for (int i = 0; i < 60; i++) {
				minutes.add(i < 10 ? "0" + i : "" + i);
			}
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
					night_time_tv.setText(timeSet);
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
					night_time_tv.setText(timeSet);
				}
			});
		}
	}

	private void AlterSet() {
		String path = URLConstants.修改用户设置;
		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("tbUserMannge.uid", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.USERID, "0"));
		pairs.put("tbUserMannge.id", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.SETID, "0"));
		pairs.put("tbUserMannge.openState", "0");
		pairs.put("tbUserMannge.ringCode", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.MUSICCODE, "g_88"));
		pairs.put("tbUserMannge.ringDesc", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.MUSICDESC, "完成任务"));
		pairs.put("tbUserMannge.beforeTime", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.BEFORETIME, "0"));
		pairs.put("tbUserMannge.morningState", sharedPrefUtil.getString(
				context, ShareFile.USERFILE, ShareFile.MORNINGSTATE, "0"));
		pairs.put("tbUserMannge.morningTime", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.MORNINGTIME, "07:58"));
		pairs.put("tbUserMannge.nightState", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.NIGHTSTATE, "0"));
		pairs.put("tbUserMannge.nightTime", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.NIGHTTIME, "18:30"));
		pairs.put("tbUserMannge.dayTime", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.ALLTIME, "08:58"));
		pairs.put("tbUserMannge.dayState", sharedPrefUtil.getString(context,
				ShareFile.USERFILE, ShareFile.ALLSTATE, "0"));

		AlterSetAsync(path, pairs);
	}

	private void AlterSetAsync(String path, final Map<String, String> map) {
		progressUtil.ShowProgress(context, true, true, "正在保存...");

		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

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
							finish();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
