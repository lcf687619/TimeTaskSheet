//package com.mission.schedule.widget;
//
//import com.mission.schedule.R;
//import com.mission.schedule.activity.MainActivity;
//import com.mission.schedule.clock.QueryAlarmData;
//import com.mission.schedule.clock.WriteAlarmClock;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//public class WidgetService extends Service {
//
//	@Override
//	public IBinder onBind(Intent intent) {
//
//		return null;
//	}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		Notification notification = new Notification(R.drawable.logo128, "时间表",
//				System.currentTimeMillis());
//		PendingIntent p_intent = PendingIntent.getActivity(this, 0, new Intent(
//				this, MainActivity.class), 0);
//		notification.setLatestEventInfo(this, "时间表", "时间表正在运行中!", p_intent);// date+" "+time+" "+week
//		notification.flags |= Notification.FLAG_ONGOING_EVENT;// 通知放前台
//		notification.flags |= Notification.FLAG_NO_CLEAR;// 通知不能清理
//		startForeground(0x1982, notification); // notification ID: 0x1982, you
//		// // can name it as you will.
//
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//
//		return super.onStartCommand(intent, flags, startId);
//	}
//
//	@Override
//	public void onStart(Intent intent, int startId) {
//		super.onStart(intent, startId);
//		if(intent==null){
//		}else{
//			String autoStart = intent.getStringExtra("autoStart");
//			if("1".equals(autoStart)){
//				QueryAlarmData.writeAlarm(getApplicationContext());
//				System.err.println("写入闹钟执行...");
//			}
//		}
//		// sharedPrefUtil = new SharedPrefUtil(getApplication(),
//		// ShareFile.USERFILE);
//		// SharedPreferences sp = null;
//		// 相当于每天第一次打开时间表begin
//		// String ymd = sharedPrefUtil.getString(getApplication(),
//		// ShareFile.USERFILE, ShareFile.EVERYDAY, "0");
//		// String nowDate = DateUtil.formatDate(new Date());
//		// if (!ymd.equals(nowDate)) {
//		// App.getDBcApplication().addRepeatToNoticeData();//自动生成重复，顺延
//		// sharedPrefUtil.putString(getApplication(), ShareFile.USERFILE,
//		// ShareFile.EVERYDAY, nowDate);
//		// WriteAlarmClock.writeAlarm(this);// 写入闹钟
//		// QueryAlarmData.writeAlarm(this);
//		// }
//		// 相当于每天第一次打开时间表end
//		// sp = getSharedPreferences("widgetproviderxml", Context.MODE_PRIVATE);
//		// String ids = sp.getString("ids", "");
//		// if (!"".equals(ids)) {
//		// String deskplugColor = "#00000000";
//		// sp = getSharedPreferences("deskplug", Context.MODE_PRIVATE);
//		// deskplugColor = sp.getString("color", "#40000000");
//
//		// if (null == dbContextApplication) {
//		// dbContextApplication = App.getDBcApplication();
//		// }
//		// if ("".equals(userid)) {
//		// userid = sharedPrefUtil.getString(getApplication(),
//		// ShareFile.USERFILE, ShareFile.USERID, "0");
//		// }
//		// String nowTime = DateTimeHelper.formatDateTimetoString(new Date(),
//		// DateTimeHelper.FMT_HHmm);
//		// 如果是凌晨则初始为空值
//		// if ("00:00".equals(nowTime) || "00:01".equals(nowTime)) {
//		// sp = getSharedPreferences("widgetxml", Context.MODE_PRIVATE);
//		// SharedPreferences.Editor e = sp.edit();
//		// e.putString("widgetTime", "");
//		// e.commit();
//		// }
//
//		// sp = getSharedPreferences("widgetxml", Context.MODE_PRIVATE);
//		// String widgetTime = sp.getString("widgetTime", "");
//		// rv = new RemoteViews(getPackageName(), R.layout.widget_layout);
//		// rv.setInt(R.id.ll_widget, "setBackgroundColor",
//		// Color.parseColor(deskplugColor));
//		// if (!"".equals(userid)) {
//		// try {
//		// // 界面最多显示8条 为判断是否大于8条 所以一次查询9条
//		// List<Map<String, String>> list = dbContextApplication
//		// .QuerySchDeskData();
//		// int noEndCount = dbContextApplication.getAllCountNoticeNoEnd();
//		// Date d = new Date();
//		// rv.setTextViewText(R.id.item_date, DateTimeHelper
//		// .formatDateTimetoString(d, DateTimeHelper.FMT_yyyyMMdd));
//		// rv.setTextViewText(R.id.item_noEndCount, "过期未结束(" + noEndCount
//		// + ")");
//		// rv.setTextViewText(R.id.item_week,
//		// CharacterUtil.getWeekOfDate(this, d));
//		// int size = list.size();
//		// if (size > ROW) {
//		// size = ROW;
//		// rv.setViewVisibility(R.id.tv_more, View.VISIBLE);
//		// } else {
//		// rv.setViewVisibility(R.id.tv_more, View.GONE);
//		// }
//		// Map<String, String> m = null;
//		// String time, content;
//		// int nextOne = 0;// 即将要到的第一条
//		// String nowDateTime = DateTimeHelper.formatDateTimetoString(d,
//		// DateTimeHelper.FMT_yyyyMMddHHmm);
//		// for (int i = 0; i < ROW; i++) {
//		// if (i < size) {
//		// m = list.get(i);
//		// time = m.get(ScheduleTable.schDate) + " "
//		// + m.get(ScheduleTable.schTime);
//		// content = m.get(ScheduleTable.schContent);
//		// if ("1".equals(m.get(ScheduleTable.schDisplayTime))) {
//		// if (nowDateTime.compareTo(m
//		// .get(ScheduleTable.schDate)
//		// + " "
//		// + m.get(ScheduleTable.schTime)) >= 0) {
//		// if (i == size - 1) {// 最后一条
//		// // sp = getSharedPreferences("widgetxml",
//		// // Context.MODE_PRIVATE);
//		// // SharedPreferences.Editor e = sp.edit();
//		// // e.putString(
//		// // "widgetTime",
//		// // m.get(LocateAllNoticeTable.alarmClockTime));
//		// // e.commit();
//		// }
//		// } else {
//		// // color = 1;// 时间未到
//		// // 如果是时间未到的第一条
//		// if (nextOne == 0) {
//		// nextOne = 1;
//		// // color = 2;
//		// if (list.size() > ROW) {// 如果有更多则保存即将发生这一条的时间
//		// Map<String, String> tm = null;
//		// int afterCount = dbContextApplication
//		// .getAfterAllCountNotice(
//		// nowDate, nowTime);
//		// if (afterCount < ROW) {
//		// int ti = list.size()
//		// - (ROW - afterCount);
//		// if (ti >= 0) {
//		// tm = list.get(ti);
//		// } else
//		// tm = list.get(i);
//		// } else
//		// tm = list.get(i);
//		// // sp =
//		// // getSharedPreferences("widgetxml",
//		// // Context.MODE_PRIVATE);
//		// // SharedPreferences.Editor e =
//		// // sp.edit();
//		// // e.putString(
//		// // "widgetTime",
//		// // tm.get(LocateAllNoticeTable.alarmClockTime));
//		// // e.commit();
//		// }
//		// }
//		// }
//		// } else {
//		// // color = 0;
//		// time = "全天";
//		// if (i == size - 1) {// 最后一条
//		// // sp = getSharedPreferences("widgetxml",
//		// // Context.MODE_PRIVATE);
//		// // SharedPreferences.Editor e = sp.edit();
//		// // e.putString(
//		// // "widgetTime",
//		// // m.get(LocateAllNoticeTable.alarmClockTime));
//		// // e.commit();
//		// }
//		// }
//		// } else {
//		// time = "";
//		// content = "";
//		// }
//		// // if (color == 0) {
//		// // showColor = "#bac2c1";
//		// // } else if (color == 2) {
//		// // showColor = "#f8710e";
//		// // } else {
//		// // showColor = "#ffffff";
//		// // }
//		// switch (i) {
//		// case 0:
//		// rv.setTextViewText(R.id.tv_time1, time);
//		// rv.setTextViewText(R.id.tv_content1, content);
//		// // rv.setTextColor(R.id.tv_time1,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content1,
//		// // Color.parseColor(showColor));
//		// break;
//		// case 1:
//		// rv.setTextViewText(R.id.tv_time2, time);
//		// rv.setTextViewText(R.id.tv_content2, content);
//		// // rv.setTextColor(R.id.tv_time2,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content2,
//		// // Color.parseColor(showColor));
//		// break;
//		// case 2:
//		// rv.setTextViewText(R.id.tv_time3, time);
//		// rv.setTextViewText(R.id.tv_content3, content);
//		// // rv.setTextColor(R.id.tv_time3,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content3,
//		// // Color.parseColor(showColor));
//		// break;
//		// case 3:
//		// rv.setTextViewText(R.id.tv_time4, time);
//		// rv.setTextViewText(R.id.tv_content4, content);
//		// // rv.setTextColor(R.id.tv_time4,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content4,
//		// // Color.parseColor(showColor));
//		// break;
//		// case 4:
//		// rv.setTextViewText(R.id.tv_time5, time);
//		// rv.setTextViewText(R.id.tv_content5, content);
//		// // rv.setTextColor(R.id.tv_time5,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content5,
//		// // Color.parseColor(showColor));
//		// break;
//		// case 5:
//		// rv.setTextViewText(R.id.tv_time6, time);
//		// rv.setTextViewText(R.id.tv_content6, content);
//		// // rv.setTextColor(R.id.tv_time6,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content6,
//		// // Color.parseColor(showColor));
//		// break;
//		// case 6:
//		// rv.setTextViewText(R.id.tv_time7, time);
//		// rv.setTextViewText(R.id.tv_content7, content);
//		// // rv.setTextColor(R.id.tv_time7,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content7,
//		// // Color.parseColor(showColor));
//		// break;
//		// case 7:
//		// rv.setTextViewText(R.id.tv_time8, time);
//		// rv.setTextViewText(R.id.tv_content8, content);
//		// // rv.setTextColor(R.id.tv_time8,
//		// // Color.parseColor(showColor));
//		// // rv.setTextColor(R.id.tv_content8,
//		// // Color.parseColor(showColor));
//		// break;
//		//
//		// default:
//		// break;
//		// }
//		// }
//		// } catch (Exception e) {
//		// e.printStackTrace();
//		// }
//		// }
//		// // "窗口小部件"点击事件发送的Intent广播
//		// // 进入时间表
//		// Intent intentClick = new Intent();
//		// intentClick.setAction(TsWidgetProvider.broadCastWIGET_CLICKString);
//		// // 进入日程界面
//		// Intent intentNoticeClick = new Intent();
//		// intentNoticeClick
//		// .setAction(TsWidgetProvider.broadCastNOTICE_WIGET_CLICKString);
//		// // 进入日程添加界面
//		// Intent intentAddClick = new Intent();
//		// intentAddClick
//		// .setAction(TsWidgetProvider.broadCastADD_WIGET_CLICKString);
//		//
//		// // 进入时间表
//		// PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
//		// intentClick, 0);
//		// rv.setOnClickPendingIntent(R.id.ll_widget, pendingIntent);
//		// // 进入日程界面
//		// PendingIntent pendingNoticeIntent = PendingIntent.getBroadcast(this,
//		// 0,
//		// intentNoticeClick, 0);
//		// rv.setOnClickPendingIntent(R.id.ll_notice, pendingNoticeIntent);
//		// // 进入日程添加界面
//		// PendingIntent pendingAddIntent = PendingIntent.getBroadcast(this, 0,
//		// intentAddClick, 0);
//		// rv.setOnClickPendingIntent(R.id.ll_add, pendingAddIntent);
//		//
//		// ComponentName cn = new ComponentName(this, TsWidgetProvider.class);
//		// AppWidgetManager am = AppWidgetManager.getInstance(this);
//		// am.updateAppWidget(cn, rv);
//		// // }
//	}
//
//}
