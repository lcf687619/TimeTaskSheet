package com.mission.schedule.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;

import com.mission.schedule.R;
import com.mission.schedule.activity.AlarmDialog;
import com.mission.schedule.activity.MainActivity;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.utils.SharedPrefUtil;

//先废弃
public class ClockService extends Service {// implements OnCompletionListener
    String alarmType;
    String ringcode;
    String alarmSound;
    int cdId;
    String morningstate;
    String nightstate;
    String alarmclocktime;
    String before;
    String content = "";
    String alltimestate;
    String ringstate;
    String displaytime;
    String postpone;
    String alarmSoundDesc;
    String isalarmtype;
    String stateone = "";
    String statetwo = "";
    String dateone = "";
    String datetwo = "";
    SharedPrefUtil sharedPrefUtil = null;
    private String WriteAlarmClockwrite = "0";
    private String notificationStr = "";
    NotificationManager notificationManager = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NewApi") @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefUtil = new SharedPrefUtil(this, ShareFile.USERFILE);
        notificationStr = sharedPrefUtil.getString(getApplicationContext(), ShareFile.USERFILE,
                ShareFile.ISNOTIFICATION, "0");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo48)
        .setContentTitle("时间表")
        .setContentText("时间表正在运行中!");
//        .setTicker(System.currentTimeMillis()+"")
//        .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        Notification notification = builder.build();
		notification.flags |= Notification.FLAG_ONGOING_EVENT;// 通知放前台
		notification.flags |= Notification.FLAG_NO_CLEAR;// 通知不能清理
		if("0".equals(notificationStr)){
			startForeground(0x1982, notification); // notification ID: 0x1982, you
		}else{
			startForeground(0, notification); // notification ID: 0x1982, you
		}
    }

    @SuppressLint("NewApi")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        // acquiremWakeLock();
        if (intent == null) {
            return;
        }
        WriteAlarmClockwrite = intent.getStringExtra("WriteAlarmClockwrite");
        if (WriteAlarmClockwrite.equals("1")) {
            // if (mWakelock == null) {
            // PowerManager pm = (PowerManager)
            // getSystemService(Context.POWER_SERVICE);
            // mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
            // | PowerManager.SCREEN_DIM_WAKE_LOCK, "WAKE");
            // mWakelock.acquire();
            // }
            // km= (KeyguardManager)
            // this.getSystemService(Context.KEYGUARD_SERVICE);
            // kl = km.newKeyguardLock("unLock");
            // //解锁
            // kl.disableKeyguard();
            alarmType = intent.getStringExtra("alarmType");
            isalarmtype = intent.getStringExtra("isalarmtype");
            ringcode = intent.getStringExtra("ringcode");
            alarmSound = intent.getStringExtra("alarmSound");
            alarmSoundDesc = intent.getStringExtra("alarmSoundDesc");
            cdId = intent.getIntExtra("cdId", 0);
            morningstate = intent.getStringExtra("morningstate");
            nightstate = intent.getStringExtra("nightstate");
            alarmclocktime = intent.getStringExtra("alarmclocktime");
            before = intent.getStringExtra("before");
            content = intent.getStringExtra("content");
            alltimestate = intent.getStringExtra("alltimestate");
            displaytime = intent.getStringExtra("displaytime");
            postpone = intent.getStringExtra("postpone");
            stateone = intent.getStringExtra("stateone");
            statetwo = intent.getStringExtra("statetwo");
            dateone = intent.getStringExtra("dateone");
            datetwo = intent.getStringExtra("datetwo");
            ringstate = sharedPrefUtil.getString(this, ShareFile.USERFILE,
                    ShareFile.RINGSTATE, "0");
            // Log.i("TAG", "alarmSound======>>>" + alarmSound + "");
            // Log.i("TAG", ringcode + ".........");
            // Log.e("tag_cxalarmType1", alarmType);

            // if ("周六".equals(CharacterUtil.getWeekOfDate(getApplication(),
            // new Date()))
            // || "周日".equals(CharacterUtil.getWeekOfDate(getApplication(),
            // new Date()))) {
            // if ("5".equals(alarmType)) {
            // return;
            // }
            // }
            if ("默认".equals(alarmSoundDesc)) {
                alarmSoundDesc = "完成任务";
                ringcode = "g_88";
            }
            if ("".equals(ringcode)) {
                ringcode = "g_88";
            }
            if (cdId < 0) {
                if (cdId == -1) {
                    content = "请打开您的时间表，看看有没有新的安排！";
                } else if (cdId == -10) {
                    content = "请打开您的时间表，每五分钟测试!！";
                } else {
                    content = "请打开您的时间表，看看有没有未完成的事情!";
                }
            } else if (cdId >= 0 && "0".equals(displaytime)) {
                content = "时间表提醒您，您今天有待办的日程需要完成，请尽快办理！";
            }

            // Log.i("TAG", content + "=========..");
            // if (!"".equals(dateone) || !"".equals(datetwo)) {
            // if (!"".equals(dateone)) {
            // if (dateone.substring(0, 10).equals(
            // DateUtil.formatDate(new Date()))) {
            // return;
            // }
            // }
            // if (!"".equals(datetwo)) {
            // if (datetwo.substring(0, 10).equals(
            // DateUtil.formatDate(new Date()))) {
            // return;
            // }
            // }
            // } else {
            if(notificationManager==null){
            	 notificationManager = (NotificationManager) getApplication()
                         .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            NotificationManager nm = (NotificationManager) getApplication()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(getApplication(),
					MainActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplication(), 0, notificationIntent, 0);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
	        builder.setSmallIcon(R.mipmap.logo48)
	        .setContentTitle("时间表")
	        .setContentText(content)
//	        .setTicker(System.currentTimeMillis()+"")
	        .setContentIntent(contentIntent);
	        Notification notification = builder.build();
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.ledOnMS = 300;
			notification.ledOffMS = 500;
			notification.flags = Notification.FLAG_SHOW_LIGHTS;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;// 在通知栏上点击此通知后自动清除此通知

			nm.notify(cdId, notification);

            // if ((cdId >= 0 && "1".equals(displaytime)) ) {
            // 桌面显示提示
            Intent i = new Intent(getApplication(), AlarmDialog.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("alarmType", alarmType);
            i.putExtra("isalarmtype", isalarmtype);
            i.putExtra("ringcode", ringcode);
            i.putExtra("alarmSound", alarmSound);
            i.putExtra("alarmSoundDesc", alarmSoundDesc);
            i.putExtra("cdId", cdId);
            i.putExtra("morningstate", morningstate);
            i.putExtra("nightstate", nightstate);
            i.putExtra("alarmclocktime", alarmclocktime);
            i.putExtra("before", before);
            i.putExtra("content", content);
            i.putExtra("alltimestate", alltimestate);
            // i.putExtra("ringstate",ringstate);
            i.putExtra("displaytime", displaytime);
            i.putExtra("postpone", postpone);
            i.putExtra("stateone", stateone);
            i.putExtra("statetwo", statetwo);
            i.putExtra("dateone", dateone);
            i.putExtra("datetwo", datetwo);
            getApplication().startActivity(i);
            // }

            // if (cdId == -10) {
            // Calendar calendar = Calendar.getInstance();
            // calendar.setTime(DateUtil.parseDateTime(DateUtil
            // .formatDateTime(DateUtil
            // .parseDateTimeSs(alarmclocktime))));
            // calendar.add(Calendar.MINUTE, 5);
            // App.getDBcApplication().updateClockDate(cdId,
            // DateUtil.formatDateTimeSs(calendar.getTime()),
            // DateUtil.formatDateTimeSs(calendar.getTime()));
            // } else if (cdId == -1 || cdId == -2) {
            // Calendar calendar = Calendar.getInstance();
            // calendar.setTime(DateUtil.parseDateTime(DateUtil
            // .formatDateTime(DateUtil
            // .parseDateTimeSs(alarmclocktime))));
            // calendar.add(Calendar.DATE, 1);
            // App.getDBcApplication().updateClockDate(cdId,
            // DateUtil.formatDateTimeSs(calendar.getTime()),
            // DateUtil.formatDateTimeSs(calendar.getTime()));
            // } else if (cdId >= 0 && "1".equals(postpone)
            // && "1".equals(displaytime)) {
            // Calendar calendar = Calendar.getInstance();
            // calendar.setTime(DateUtil.parseDateTime(DateUtil
            // .formatDateTime(DateUtil
            // .parseDateTimeSs(alarmclocktime))));
            // calendar.add(Calendar.DATE, 1);
            // App.getDBcApplication().updateClockDate(cdId,
            // DateUtil.formatDateTimeSs(calendar.getTime()),
            // DateUtil.formatDateTimeSs(calendar.getTime()));
            // } else if (cdId >= 0 && "0".equals(displaytime)
            // && "1".equals(postpone)) {
            // Calendar calendar = Calendar.getInstance();
            // calendar.setTime(DateUtil.parseDateTime(DateUtil
            // .formatDateTime(DateUtil
            // .parseDateTimeSs(alarmclocktime))));
            // calendar.add(Calendar.DATE, 1);
            // App.getDBcApplication().updateClockDate(cdId,
            // DateUtil.formatDateTimeSs(calendar.getTime()),
            // DateUtil.formatDateTimeSs(calendar.getTime()));
            // }
//			try {
//				File temp = new File(Environment.getExternalStorageDirectory()
//						.getPath() + "/YourAppFolder/");// 自已缓存文件夹
//				if (!temp.exists()) {
//					temp.mkdir();
//				}
//				FileWriter fw = new FileWriter(temp.getAbsolutePath()
//						+ "/bb.txt", true);
//				if (fw != null) {
//					fw.flush();
//					fw.write(DateUtil.formatDateTimeSs(new Date()));
//					fw.write("  ," + content);
//					fw.write("  ," + alarmSound + "\n");
//					fw.close();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
            // if (mWakelock != null && mWakelock.isHeld()) {
            // mWakelock.release();
            // mWakelock = null;
            // }
        } else if ("2".equals(WriteAlarmClockwrite)) {
            QueryAlarmData.writeAlarm(getApplicationContext());
        }
        // if (alarmSound == null)
        // return;
        // if ("1".equals(ringstate)) {
        // return;
        // } else if ("2".equals(ringstate)) {
        // ringcode = "g_220";
        // alarmSound = "g_220";
        // }
        // if (cdId >= 0 && "0".equals(displaytime)) {
        // alarmType = "1";
        // ringcode = "g_207";
        // alarmSound = "g_207";
        // }
        // if ("2".equals(ringstate)) {
        // ringcode = "g_220";
        // alarmSound = "g_220";
        // }
        //
        // Calendar cd = Calendar.getInstance();
        // cd.setTime(DateUtil.parseDateTime(alarmclocktime));
        // cd.add(Calendar.MINUTE, -Integer.parseInt(before));
        // SimpleDateFormat format1 = new
        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // try {
        // if (cdId == -1 || cdId == -2 || cdId == -10
        // || (cdId >= 0 && "0".equals(displaytime))) {
        // AssetFileDescriptor fileDescriptor = null;
        // if ("3".equals(isalarmtype)) {// >=
        // // System.currentTimeMillis()
        // if (DateTimeHelper.formatDateTimetoString(cd.getTime(),
        // DateTimeHelper.FMT_yyyyMMddHHmm).equals(
        // DateTimeHelper.formatDateTimetoString(new Date(),
        // DateTimeHelper.FMT_yyyyMMddHHmm))) {
        // fileDescriptor = getApplication().getAssets().openFd(
        // ringcode + ".mp3");
        // } else {
        // fileDescriptor = getApplication().getAssets().openFd(
        // alarmSound + ".mp3");
        // }
        // } else if ("2".equals(isalarmtype)) {
        // if (DateTimeHelper.formatDateTimetoString(cd.getTime(),
        // DateTimeHelper.FMT_yyyyMMddHHmm).equals(
        // DateTimeHelper.formatDateTimetoString(new Date(),
        // DateTimeHelper.FMT_yyyyMMddHHmm))) {
        // fileDescriptor = getApplication().getAssets().openFd(
        // ringcode + ".mp3");
        // } else {
        // fileDescriptor = null;
        // }
        // } else if ("0".equals(isalarmtype)) {
        // fileDescriptor = null;
        // } else {
        // if (cdId < 0) {
        // if (cdId == -1) {
        // if ("0".equals(morningstate)) {
        // fileDescriptor = getApplication().getAssets()
        // .openFd(alarmSound);
        // }
        // } else if (cdId == -2) {
        // if ("0".equals(nightstate)) {
        // fileDescriptor = getApplication().getAssets()
        // .openFd(alarmSound);
        // }
        // } else {
        // fileDescriptor = getApplication().getAssets()
        // .openFd(alarmSound + ".mp3");
        // }
        // } else {
        // fileDescriptor = getApplication().getAssets().openFd(
        // alarmSound + ".mp3");
        // }
        // }
        // if (fileDescriptor == null)
        // return;
        // mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
        // fileDescriptor.getStartOffset(),
        // fileDescriptor.getLength());
        // // mediaPlayer.prepare();
        // // mediaPlayer.start();
        // mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        // mediaPlayer.prepare();
        // if("1".equals(ringstate)){
        // mediaPlayer.setVolume(0.0f, 0.0f);
        // }
        // mediaPlayer.start();
        // }
        // // }
        // try {
        // File temp = new File(Environment.getExternalStorageDirectory()
        // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
        // if (!temp.exists()) {
        // temp.mkdir();
        // }
        // FileWriter fw = new FileWriter(temp.getAbsolutePath()
        // + "/bb.txt", true);
        // if (fw != null) {
        // fw.flush();
        // fw.write(format1.format(new Date()));
        // fw.write("  ," + content);
        // fw.write("  ," + alarmSound + "\n");
        // fw.close();
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // } catch (IllegalArgumentException e) {
        // e.printStackTrace();
        // } catch (SecurityException e) {
        // e.printStackTrace();
        // } catch (IllegalStateException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // Intent intent1 = new Intent(this, WidgetService.class);
        // intent1.setAction("com.mission.schedule.widget.WidgetService");
        // intent1.setPackage(getPackageName());
        // intent1.setFlags(Service.START_REDELIVER_INTENT);
        // intent.putExtra("autoStart", "0");
        // startService(intent1);
        // }
        // }
    }

    @Override
    public void onDestroy() {
        writkilldata();
        super.onDestroy();
        // if(mediaPlayer!=null){
        // mediaPlayer.stop();
        // mediaPlayer.release();
        // mediaPlayer = null;
        // }
    }

    //
    // @Override
    // public void onCompletion(MediaPlayer mp) {
    // // TODO Auto-generated method stub
    // if(mediaPlayer!=null){
    // mediaPlayer.stop();
    // mediaPlayer.release();
    // mediaPlayer = null;
    // }
    // }
    private void writkilldata() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        String phonememory = Formatter.formatFileSize(getBaseContext(),
                mi.availMem);
//		try {
//			File temp = new File(Environment.getExternalStorageDirectory()
//					.getPath() + "/YourAppFolder/");// 自已缓存文件夹
//			if (!temp.exists()) {
//				temp.mkdir();
//			}
//			SimpleDateFormat format1 = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss");
//			FileWriter fw = new FileWriter(temp.getAbsolutePath()
//					+ "/writeserviceKill.txt", true);
//			if (fw != null) {
//				fw.flush();
//				fw.write(format1.format(new Date()));
//				fw.write("  ,clockservice杀死手机内存大小" + phonememory + "!!!" + "\n");
//				fw.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

}
