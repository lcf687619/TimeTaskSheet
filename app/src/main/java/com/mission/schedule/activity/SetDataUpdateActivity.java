package com.mission.schedule.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.entity.LocateAllMemoTable;
import com.mission.schedule.entity.LocateOldAllNoticeTable;
import com.mission.schedule.entity.LocateRepeatNoticeTable;
import com.mission.schedule.service.SetDataUpdateService;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.MyProgressBar;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.XmlUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

@SuppressLint("SimpleDateFormat")
public class SetDataUpdateActivity extends BaseActivity {

	@ViewResId(id = R.id.progressBar1)
	private MyProgressBar progressBar1;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;

	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	List<Map<String, String>> soundlist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> replist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> beiwanglist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> allList = new ArrayList<Map<String, String>>();

	App app = null;
	private int barCurrentValue = 0;
	String datatype;

	// List<Map<String, String>> schList = new ArrayList<Map<String, String>>();
	// List<Map<String, String>> repList = new ArrayList<Map<String, String>>();

	String json;
	List<Map<String, String>> upList;
	List<Map<String, String>> upRepeatList;

	// Context context;
	// App app;
	// String type;
	String userid;
	// SharedPrefUtil sharedPrefUtil = null;
	ProgressUtil progressUtil = new ProgressUtil();
	CalendarChangeValue changeValue = null;
	String perstr = "";

	@Override
	protected void setListener() {

	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_dataupdate);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		receiver = new UpdateDataReceiver();
		changeValue = new CalendarChangeValue();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SetDataUpdateService.UPDATADATA);
		this.registerReceiver(receiver, filter);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		soundlist.clear();
		soundlist = XmlUtil.readBeforeBellXML(this);
		datatype = getIntent().getStringExtra("type");

		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "0");
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// schList = app.queryAllSchData();
		// repList = app.QueryAllRepData();
		// }
		// }).start();
		// uploaddata();
		progressUtil.ShowProgress(context, true, false, "数据正在同步，请稍等...");
		new MyAsync().execute();
	}

	class MyAsync extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressUtil.ShowProgress(context, true, false, "数据正在同步，请稍等...");
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				if (app.queryAllSchData(-4, 0, 0) != null
						&& app.queryAllSchData(-4, 0, 0).size() > 0) {
					app.deleteSchData();
					app.deleteSchData1();
				}
				if (app.QueryAllChongFuData(5) != null
						&& app.QueryAllChongFuData(5).size() > 0) {
					app.deleteRepData();
					app.deleteRepData1();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			uploaddata();
			// }
			// }).start();
		}
	}

	@Override
	protected void setAdapter() {

	}

	private void uploaddata() {
		// if(schList != null && schList.size() > 0 || repList != null &&
		// repList.size() > 0){
		// progressUtil.ShowProgress(context, true, false, "数据正在同步，请稍等...");
		// }
		// if (schList != null && schList.size() > 0) {
		// for (int i = 0; i < schList.size(); i++) {
		// app.deleteSchData(schList.get(i).get(ScheduleTable.schID));
		// }
		// }
		// if (repList != null && repList.size() > 0) {
		// for (int i = 0; i < repList.size(); i++) {
		// app.deleteRepData(repList.get(i).get(CLRepeatTable.repID));
		// }
		// }
		// if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
		// uploadData();
		// } else {
		progressUtil.dismiss();
		if ("0".equals(datatype)) {
			initdata();
		} else if ("1".equals(datatype)) {
			initdata1();
		} else {
			progressBar1.setMax(100);
			finish();
		}
		if (allList != null && allList.size() > 0) {
			progressBar1.setMax(allList.size());
		} else {
			progressBar1.setMax(0);
		}
		// }
	}

	private void initdata() {
		list = app.QueryOldSchUpdate();
		replist = app.QueryRepeatData();
		beiwanglist = app.QueryYestodayData();
		allList.addAll(list);
		allList.addAll(replist);
		allList.addAll(beiwanglist);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							int isend = 0;
							if ("14".equals(list.get(i).get(
									LocateOldAllNoticeTable.colorType))) {
								isend = 1;
							} else {
								isend = 0;
							}
							String ringcode = "";
							String ringdesc = "";
							for (int j = 0; j < soundlist.size(); j++) {
								String str = list
										.get(i)
										.get(LocateOldAllNoticeTable.alarmSound)
										.replace(".ogg", "");
								if (str.equals(soundlist.get(j).get("value"))) {
									ringcode = soundlist.get(j).get("value");
									ringdesc = soundlist.get(j).get("name");
									break;
								} else {
									ringcode = "g_88";
									ringdesc = "完成任务";
								}
							}
							if ("0".equals(list.get(i).get(
									LocateOldAllNoticeTable.beforTime))) {
								app.insertScheduleData(
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeContent),
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeDate),
										list.get(i)
												.get(LocateOldAllNoticeTable.alarmClockTime),
										1,
										0,
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.displayAlarm)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.postpone)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.noticeIsStarred)),
										0,
										isend,
										list.get(i)
												.get(LocateOldAllNoticeTable.createTime),
										"", 0, "", "", 0, "", DateUtil
												.formatDateTimeSs(new Date()),
										1, 0, 0, ringdesc, ringcode, "", 0, 0,
										0, "", "", 1, 0, 0);
							} else {
								int before = 0;
								if ("5".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 5;
								} else if ("10".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 15;
								} else if ("15".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 15;
								} else if ("30".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 30;
								} else if ("45".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 60;
								} else if ("60".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 60;
								} else if ("120".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 120;
								} else if ("240".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 120;
								} else if (("" + 24 * 60)
										.equals(list
												.get(i)
												.get(LocateOldAllNoticeTable.beforTime))) {
									before = 24 * 60;
								} else if (("" + 48 * 60)
										.equals(list
												.get(i)
												.get(LocateOldAllNoticeTable.beforTime))) {
									before = 48 * 60;
								}
								app.insertScheduleData(
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeContent),
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeDate),
										list.get(i)
												.get(LocateOldAllNoticeTable.alarmClockTime),
										2,
										before,
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.displayAlarm)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.postpone)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.noticeIsStarred)),
										0,
										isend,
										list.get(i)
												.get(LocateOldAllNoticeTable.createTime),
										"", 0, "", "", 0, "", DateUtil
												.formatDateTimeSs(new Date()),
										1, 0, 0, ringdesc, ringcode, "", 0, 0,
										0, "", "", 1, 0, 0);
							}
							barCurrentValue++;
							hander.sendEmptyMessage(barCurrentValue);
						}
					}
					Thread.sleep(1000);
					if (replist != null && replist.size() > 0) {
						for (int i = 0; i < replist.size(); i++) {
							String ringcode = "";
							String ringdesc = "";
							for (int j = 0; j < soundlist.size(); j++) {
								String str = replist
										.get(i)
										.get(LocateRepeatNoticeTable.key_tpAlarmSound)
										.replace(".ogg", "");
								if (str.equals(soundlist.get(j).get("value"))) {
									ringcode = soundlist.get(j).get("value");
									ringdesc = soundlist.get(j).get("name");
									break;
								} else {
									ringcode = "g_88";
									ringdesc = "完成任务";
								}
							}
							int type;
							if ("10".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpDataType))) {
								if ("".equals(replist.get(i).get(
										LocateRepeatNoticeTable.key_tpLcDate))) {
									type = 4;
								} else {
									type = 6;
								}
							} else if ("4".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpDataType))) {
								if ("".equals(replist.get(i).get(
										LocateRepeatNoticeTable.key_tpLcDate))) {
									type = 4;
								} else {
									type = 6;
								}
							} else {
								type = Integer
										.parseInt(replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpDataType));
							}
							String typeparams = "";
							if (type == 1) {
								typeparams = "[]";
								perstr = "";
							} else if (type == 2) {
								typeparams = "["
										+ "\""
										+ replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpCurWeek)
										+ "\"" + "]";
								perstr = "";
							} else if (type == 3) {
								int day = Integer.parseInt(replist.get(i).get(
										LocateRepeatNoticeTable.key_tpDay));
								typeparams = "[" + "\""
										+ (day < 10 ? "0" + day : day + "")
										+ "\"" + "]";
								perstr = "";
							} else if (type == 4) {
								typeparams = "["
										+ "\""
										+ replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpMonthDay)
										+ "\"" + "]";
								perstr = "";
							} else if (type == 6) {
								String[] monthStr = getResources()
										.getStringArray(R.array.monthStr);
								String[] dayStr = getResources()
										.getStringArray(R.array.lunarstr);
								String str = replist.get(i).get(
										LocateRepeatNoticeTable.key_tpLcDate);
								int month = Integer.parseInt(str.split("-")[0]);
								int day = Integer.parseInt(str.split("-")[1]);
								String month1 = "";
								String day1 = "";
								String monthdate = "";
								for (int j = 1; j <= monthStr.length; j++) {
									if (month == j) {
										month1 = monthStr[j - 1];
										break;
									}
								}
								for (int j = 1; j <= dayStr.length; j++) {
									if (day == j) {
										day1 = dayStr[j - 1];
										break;
									}
								}
								monthdate = month1 + day1;
								typeparams = "[" + "\"" + monthdate + "\""
										+ "]";
								perstr = changeValue.changaSZ(monthdate);
								if (perstr.length() == 2) {
									perstr = DateUtil.formatDateTimeSs(
											new Date()).substring(5, 7)
											+ "-" + perstr;
								}

							}
							int alarmtype;
							if ("0".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								alarmtype = 1;
							} else {
								alarmtype = 2;
							}
							int before = 0;
							if ("5".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 5;
							} else if ("10".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 15;
							} else if ("15".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 15;
							} else if ("30".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 30;
							} else if ("45".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 60;
							} else if ("60".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 60;
							} else if ("120".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 120;
							} else if ("240".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 120;
							} else if (("" + 24 * 60).equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 24 * 60;
							} else if (("" + 48 * 60).equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 48 * 60;
							} else {
								before = 0;
							}
							app.insertCLRepeatTableData(
									before,
									0,
									Integer.parseInt(replist
											.get(i)
											.get(LocateRepeatNoticeTable.key_tpDisplayAlarm)),
									type,
									alarmtype,
									0,
									0,
									0,
									1,
									typeparams,
									replist.get(i)
											.get(LocateRepeatNoticeTable.locateNextCreatTime),
									"",
									DateUtil.formatDateTimeSs(new Date()),
									"",
									replist.get(i)
											.get(LocateRepeatNoticeTable.key_tpContent),
									replist.get(i)
											.get(LocateRepeatNoticeTable.key_tpCreateTime),
									"",
									"",
									replist.get(i).get(
											LocateRepeatNoticeTable.key_tpTime),
									ringdesc, ringcode, DateUtil
											.formatDateTimeSs(new Date()), 0,
									"", 0, "", "", 0, 0, 0, "", "", 0, 0,
									perstr, 0, 0);
							barCurrentValue++;
							hander.sendEmptyMessage(barCurrentValue);
						}
					}

					Thread.sleep(1000);
					if (beiwanglist != null && beiwanglist.size() > 0) {
						for (int i = 0; i < beiwanglist.size(); i++) {
							app.insertYestodayData(
									beiwanglist.get(i).get(
											LocateAllMemoTable.noticeContent),
									beiwanglist.get(i)
											.get(LocateAllMemoTable.createTime)
											.substring(11, 16),
									beiwanglist.get(i).get(
											LocateAllMemoTable.createTime));
							barCurrentValue++;
							hander.sendEmptyMessage(barCurrentValue);
						}
					}
				} catch (InterruptedException e) {
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.UPDATESTATE, "0");
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void initdata1() {
		list = app.QueryOldSchUpdate1();
		replist = app.QueryRepeatData1();
		beiwanglist = app.QueryYestodayData1();
		allList.addAll(list);
		allList.addAll(replist);
		allList.addAll(beiwanglist);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							int isend = 0;
							if ("14".equals(list.get(i).get(
									LocateOldAllNoticeTable.colorType))) {
								isend = 1;
							} else {
								isend = 0;
							}
							String ringcode = "";
							String ringdesc = "";
							for (int j = 0; j < soundlist.size(); j++) {
								String str = list
										.get(i)
										.get(LocateOldAllNoticeTable.alarmSound)
										.replace(".ogg", "");
								if (str.equals(soundlist.get(j).get("value"))) {
									ringcode = soundlist.get(j).get("value");
									ringdesc = soundlist.get(j).get("name");
									break;
								} else {
									ringcode = "g_88";
									ringdesc = "完成任务";
								}
							}
							if ("0".equals(list.get(i).get(
									LocateOldAllNoticeTable.beforTime))) {
								app.insertScheduleData(
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeContent),
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeDate),
										list.get(i)
												.get(LocateOldAllNoticeTable.alarmClockTime),
										1,
										0,
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.displayAlarm)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.postpone)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.noticeIsStarred)),
										0,
										isend,
										list.get(i)
												.get(LocateOldAllNoticeTable.createTime),
										"", 0, "", "", 0, "", DateUtil
												.formatDateTimeSs(new Date()),
										1, 0, 0, ringdesc, ringcode, "", 0, 0,
										0, "", "", 1, 0, 0);
							} else {
								int before = 0;
								if ("5".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 5;
								} else if ("10".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 15;
								} else if ("15".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 15;
								} else if ("30".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 30;
								} else if ("45".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 60;
								} else if ("60".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 60;
								} else if ("120".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 120;
								} else if ("240".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))) {
									before = 120;
								} else if (("" + 24 * 60)
										.equals(list
												.get(i)
												.get(LocateOldAllNoticeTable.beforTime))) {
									before = 24 * 60;
								} else if (("" + 48 * 60)
										.equals(list
												.get(i)
												.get(LocateOldAllNoticeTable.beforTime))) {
									before = 48 * 60;
								}
								app.insertScheduleData(
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeContent),
										list.get(i)
												.get(LocateOldAllNoticeTable.noticeDate),
										list.get(i)
												.get(LocateOldAllNoticeTable.alarmClockTime),
										2,
										before,
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.displayAlarm)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.postpone)),
										Integer.parseInt(list
												.get(i)
												.get(LocateOldAllNoticeTable.noticeIsStarred)),
										0,
										isend,
										list.get(i)
												.get(LocateOldAllNoticeTable.createTime),
										"", 0, "", "", 0, "", DateUtil
												.formatDateTimeSs(new Date()),
										1, 0, 0, ringdesc, ringcode, "", 0, 0,
										0, "", "", 1, 0, 0);
							}
							barCurrentValue++;
							hander.sendEmptyMessage(barCurrentValue);
						}
					}
					Thread.sleep(1000);
					if (replist != null && replist.size() > 0) {
						for (int i = 0; i < replist.size(); i++) {
							String ringcode = "";
							String ringdesc = "";
							for (int j = 0; j < soundlist.size(); j++) {
								String str = replist
										.get(i)
										.get(LocateRepeatNoticeTable.key_tpAlarmSound)
										.replace(".ogg", "");
								if (str.equals(soundlist.get(j).get("value"))) {
									ringcode = soundlist.get(j).get("value");
									ringdesc = soundlist.get(j).get("name");
									break;
								} else {
									ringcode = "g_88";
									ringdesc = "完成任务";
								}
							}
							int type;
							if ("10".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpDataType))) {
								if ("".equals(replist.get(i).get(
										LocateRepeatNoticeTable.key_tpLcDate))) {
									type = 4;
								} else {
									type = 6;
								}
							} else if ("4".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpDataType))) {
								if ("".equals(replist.get(i).get(
										LocateRepeatNoticeTable.key_tpLcDate))) {
									type = 4;
								} else {
									type = 6;
								}
							} else {
								type = Integer
										.parseInt(replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpDataType));
							}
							String typeparams = "";
							if (type == 1) {
								typeparams = "[]";
								perstr = "";
							} else if (type == 2) {
								typeparams = "["
										+ "\""
										+ replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpCurWeek)
										+ "\"" + "]";
								perstr = "";
							} else if (type == 3) {
								int day = Integer.parseInt(replist.get(i).get(
										LocateRepeatNoticeTable.key_tpDay));
								typeparams = "[" + "\""
										+ (day < 10 ? "0" + day : day + "")
										+ "\"" + "]";
								perstr = "";
							} else if (type == 4) {
								typeparams = "["
										+ "\""
										+ replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpMonthDay)
										+ "\"" + "]";
								perstr = "";
							} else if (type == 6) {
								String[] monthStr = getResources()
										.getStringArray(R.array.monthStr);
								String[] dayStr = getResources()
										.getStringArray(R.array.lunarstr);
								String str = replist.get(i).get(
										LocateRepeatNoticeTable.key_tpLcDate);
								int month = Integer.parseInt(str.split("-")[0]);
								int day = Integer.parseInt(str.split("-")[1]);
								String month1 = "";
								String day1 = "";
								String monthdate = "";
								for (int j = 1; j <= monthStr.length; j++) {
									if (month == j) {
										month1 = monthStr[j - 1];
										break;
									}
								}
								for (int j = 1; j <= dayStr.length; j++) {
									if (day == j) {
										day1 = dayStr[j - 1];
										break;
									}
								}
								monthdate = month1 + day1;
								typeparams = "[" + "\"" + monthdate + "\""
										+ "]";
								perstr = changeValue.changaSZ(monthdate);
							}
							int alarmtype;
							if ("0".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								alarmtype = 1;
							} else {
								alarmtype = 2;
							}
							int before = 0;
							if ("5".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 5;
							} else if ("10".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 15;
							} else if ("15".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 15;
							} else if ("30".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 30;
							} else if ("45".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 60;
							} else if ("60".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 60;
							} else if ("120".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 120;
							} else if ("240".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 120;
							} else if (("" + 24 * 60).equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 24 * 60;
							} else if (("" + 48 * 60).equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))) {
								before = 48 * 60;
							} else {
								before = 0;
							}
							app.insertCLRepeatTableData(
									before,
									0,
									Integer.parseInt(replist
											.get(i)
											.get(LocateRepeatNoticeTable.key_tpDisplayAlarm)),
									type,
									alarmtype,
									0,
									0,
									0,
									1,
									typeparams,
									replist.get(i)
											.get(LocateRepeatNoticeTable.locateNextCreatTime),
									"",
									replist.get(i)
											.get(LocateRepeatNoticeTable.key_tpCreateTime),
									"",
									replist.get(i)
											.get(LocateRepeatNoticeTable.key_tpContent),
									replist.get(i)
											.get(LocateRepeatNoticeTable.key_tpCreateTime),
									"",
									"",
									replist.get(i).get(
											LocateRepeatNoticeTable.key_tpTime),
									ringdesc, ringcode, DateUtil
											.formatDateTimeSs(new Date()), 0,
									"", 0, "", "", 0, 0, 0, "", "", 0, 0,
									perstr, 0, 0);
							barCurrentValue++;
							hander.sendEmptyMessage(barCurrentValue);
						}
					}

					Thread.sleep(1000);
					if (beiwanglist != null && beiwanglist.size() > 0) {
						for (int i = 0; i < beiwanglist.size(); i++) {
							app.insertYestodayData(
									beiwanglist.get(i).get(
											LocateAllMemoTable.noticeContent),
									beiwanglist.get(i)
											.get(LocateAllMemoTable.createTime)
											.substring(11, 16),
									beiwanglist.get(i).get(
											LocateAllMemoTable.createTime));
							barCurrentValue++;
							hander.sendEmptyMessage(barCurrentValue);
						}
					}
				} catch (InterruptedException e) {
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.UPDATESTATE, "0");
					e.printStackTrace();
				}
			}
		}).start();
	}

	ProgressUtil progressUtil1 = new ProgressUtil();
	private Handler hander = new Handler() {
		public void handleMessage(android.os.Message msg) {

			progressBar1.setProgress(barCurrentValue);
			if (barCurrentValue == allList.size()) {
				if (progressBar1.getProgress() == allList.size()) {
					progressUtil1.ShowProgress(context, true, false,
							"数据正在同步，请稍等...");
					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
						Intent intent = new Intent(context,
								SetDataUpdateService.class);
						intent.setAction("updateData");
						intent.setPackage(getPackageName());
						startService(intent);
					} else {
						return;
					}
				}
				// copyFile("/data/data/com.mission.schedule/databases/data",
				// Environment.getExternalStorageDirectory().getPath()
				// + "/YourAppFolder/data1");
			}
		};
	};

	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}
	}

	/**
	 * 登录广播接收器
	 * */
	private UpdateDataReceiver receiver = null;

	@Override
	public void onDestroy() {
		if (receiver != null) {
			this.unregisterReceiver(receiver);
		}
		App.getHttpQueues().cancelAll("upload");
		super.onDestroy();
	}

	public class UpdateDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			progressUtil1.dismiss();
			// 做一些修改界面之类的工作
			String result = intent.getStringExtra("data");
			if (result.equals("success")) {
				Message message = Message.obtain();
				message.what = -1;
				handler1.sendMessage(message);
			} else {
				Message message = Message.obtain();
				message.what = -2;
				handler1.sendMessage(message);
			}

		}
	}

	private Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// int str = (Integer) msg.obj;
			switch (msg.what) {
			case -1:
				finish();
				break;
			case -2:
				finish();
				break;
			}
		}

	};

}
