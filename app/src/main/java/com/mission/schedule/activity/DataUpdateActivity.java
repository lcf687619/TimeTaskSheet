package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.LocateAllMemoTable;
import com.mission.schedule.entity.LocateOldAllNoticeTable;
import com.mission.schedule.entity.LocateRepeatNoticeTable;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.MyProgressBar;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.XmlUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class DataUpdateActivity extends BaseActivity {

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
	CalendarChangeValue changeValue = null;
	ActivityManager1 activityManager = null;

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
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		changeValue = new CalendarChangeValue();
		app = App.getDBcApplication();
		soundlist.clear();
		soundlist = XmlUtil.readBeforeBellXML(this);
		initdata();
		if (allList != null && allList.size() > 0) {
			progressBar1.setMax(allList.size());
		} else {
			progressBar1.setMax(0);
		}
	}

	@Override
	protected void setAdapter() {

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
										"", 0, "", "", 0, "", DateUtil.formatDateTimeSs(new Date()), 1, 0, 0,
										ringdesc, ringcode, "", 0, 0, 0, "",
										"", 1, 0, 0);
							} else {
								int before=0;
								if("5".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 5;
								}else if("10".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 15;
								}else if("15".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 15;
								}else if("30".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 30;
								}else if("45".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 60;
								}else if("60".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 60;
								}else if("120".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 120;
								}else if("240".equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 120;
								}else if((""+24*60).equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 24*60;
								}else if((""+48*60).equals(list.get(i).get(
										LocateOldAllNoticeTable.beforTime))){
									before = 48*60;
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
										"", 0, "", "", 0, "", DateUtil.formatDateTimeSs(new Date()), 1, 0, 0,
										ringdesc, ringcode, "", 0, 0, 0, "",
										"", 1, 0, 0);
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
							String parstr = "";
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
							} else if (type == 2) {
								typeparams = "["
										+ "\""
										+ replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpCurWeek)
										+ "\"" + "]";
							} else if (type == 3) {
								int day = Integer.parseInt(replist.get(i).get(
										LocateRepeatNoticeTable.key_tpDay));
								typeparams = "[" + "\""
										+ (day < 10 ? "0" + day : day + "")
										+ "\"" + "]";
							} else if (type == 4) {
								typeparams = "["
										+ "\""
										+ replist
												.get(i)
												.get(LocateRepeatNoticeTable.key_tpMonthDay)
										+ "\"" + "]";
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
								parstr = changeValue.changaSZ(monthdate);
								if(parstr.length()==2){
									parstr = DateUtil.formatDateTime(new Date()).substring(5, 7)+"-"+parstr;
								}
							}
							int alarmtype;
							if("0".equals(replist
											.get(i)
											.get(LocateRepeatNoticeTable.key_tpBeforTime))){
								alarmtype = 1;
							}else{
								alarmtype = 2;
							}
							int before=0;
							if("5".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 5;
							}else if("10".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 15;
							}else if("15".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 15;
							}else if("30".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 30;
							}else if("45".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 60;
							}else if("60".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 60;
							}else if("120".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 120;
							}else if("240".equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 120;
							}else if((""+24*60).equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 24*60;
							}else if((""+48*60).equals(replist.get(i).get(
									LocateRepeatNoticeTable.key_tpBeforTime))){
								before = 48*60;
							}else{
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
									DateUtil.formatDateTime(new Date()),
									"",
									replist.get(i)
											.get(LocateRepeatNoticeTable.key_tpContent),
									replist.get(i)
											.get(LocateRepeatNoticeTable.key_tpCreateTime),
									"",
									"",
									replist.get(i).get(
											LocateRepeatNoticeTable.key_tpTime),
									ringdesc, ringcode, DateUtil.formatDateTimeSs(new Date()), 0, "", 0, "",
									"", 0, 0, 0, "", "", 0,0,parstr,0,0);
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

	private Handler hander = new Handler() {
		public void handleMessage(android.os.Message msg) {

			progressBar1.setProgress(barCurrentValue);
			if (barCurrentValue == allList.size()) {
				startActivity(new Intent(context, OldLoginActivity.class));
				activityManager.doAllActivityFinish();
			}
		};
	};
}
