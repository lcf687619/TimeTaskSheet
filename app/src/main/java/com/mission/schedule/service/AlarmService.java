package com.mission.schedule.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.mission.schedule.activity.AlarmDialog;
import com.mission.schedule.activity.MainActivity;
import com.mission.schedule.applcation.App;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.utils.DateTimeHelper;
import com.mission.schedule.utils.DateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmService extends Service {

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
	MediaPlayer mediaPlayer = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent == null) {
			return;
		}
		mediaPlayer = new MediaPlayer();
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
		ringstate = intent.getStringExtra("ringstate");
		displaytime = intent.getStringExtra("displaytime");
		postpone = intent.getStringExtra("postpone");
		Log.i("TAG", "alarmSound======>>>" + alarmSound + "");
		Log.i("TAG", ringcode + ".........");
		Log.e("tag_cxalarmType1", alarmType);
//		if ("周六".equals(CharacterUtil.getWeekOfDate(getApplication(),
//				new Date()))
//				|| "周日".equals(CharacterUtil.getWeekOfDate(getApplication(),
//						new Date()))) {
//			if ("5".equals(alarmType)) {
//				return;
//			}
//		}
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
				content = "请打开您的时间表，每五分钟测试一次！";
			} else {
				content = "请打开您的时间表，看看有没有未完成的事情!";
			}
		} else if (cdId > 0 && "0".equals(displaytime)) {
			content = "时间表提醒您，您今天有待办的日程需要完成，请尽快办理！";
		}

		Log.i("TAG", content + "=========..");
		NotificationManager nm = (NotificationManager) getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(getApplication(),
				MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplication(), 0, notificationIntent, 0);

		// 创建Notifcation
//		Notification notification = new Notification(R.mipmap.logo128,
//				content, System.currentTimeMillis() + 2000);
//		// notification.defaults |= Notification.DEFAULT_SOUND;// 添加声音
//		notification.defaults |= Notification.DEFAULT_VIBRATE;
//		notification.defaults |= Notification.DEFAULT_LIGHTS;
//		notification.ledOnMS = 300;
//		notification.ledOffMS = 500;
//		notification.flags = Notification.FLAG_SHOW_LIGHTS;
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;// 在通知栏上点击此通知后自动清除此通知
//		notification.setLatestEventInfo(getApplication(), "时间表", content,
//				contentIntent);
//
//		nm.notify(cdId, notification);

		if ((cdId >= 0 && "1".equals(displaytime)) || cdId == -10) {
			// 桌面显示提示
			Intent i = new Intent(getApplication(), AlarmDialog.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("id", String.valueOf(cdId));
			i.putExtra("content", content);
			i.putExtra("time", alarmclocktime);
			getApplication().startActivity(i);
		}

		if (alarmSound == null)
			return;

		if ("1".equals(ringstate)) {
			return;
		} else if ("2".equals(ringstate)) {
			ringcode = "g_220";
			alarmSound = "g_220";
		}
		if (cdId >= 0 && "0".equals(displaytime)) {
			alarmType = "1";
			ringcode = "g_220";
			alarmSound = "g_220";
		}

		Calendar cd = Calendar.getInstance();
		cd.setTime(DateUtil.parseDateTime(alarmclocktime));
		cd.add(Calendar.MINUTE, -Integer.parseInt(before));
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			AssetFileDescriptor fileDescriptor = null;
			if ("3".equals(isalarmtype)) {// >=
											// System.currentTimeMillis()
				if (DateTimeHelper.formatDateTimetoString(cd.getTime(),
						DateTimeHelper.FMT_yyyyMMddHHmm).equals(
						DateTimeHelper.formatDateTimetoString(new Date(),
								DateTimeHelper.FMT_yyyyMMddHHmm))) {
					fileDescriptor = getApplication().getAssets().openFd(
							ringcode + ".mp3");
				} else {
					fileDescriptor = getApplication().getAssets().openFd(
							alarmSound + ".mp3");
				}
			} else if ("2".equals(isalarmtype)) {
				if (DateTimeHelper.formatDateTimetoString(cd.getTime(),
						DateTimeHelper.FMT_yyyyMMddHHmm).equals(
						DateTimeHelper.formatDateTimetoString(new Date(),
								DateTimeHelper.FMT_yyyyMMddHHmm))) {
					fileDescriptor = getApplication().getAssets().openFd(
							ringcode + ".mp3");
				} else {
					fileDescriptor = null;
				}
			} else if ("0".equals(isalarmtype)) {
				fileDescriptor = null;
			} else {
				if (cdId < 0) {
					if (cdId == -1) {
						if ("0".equals(morningstate)) {
							fileDescriptor = getApplication().getAssets()
									.openFd(alarmSound);
						}
					} else if (cdId == -2) {
						if ("0".equals(nightstate)) {
							fileDescriptor = getApplication().getAssets()
									.openFd(alarmSound);
						}
					} else {
						fileDescriptor = getApplication().getAssets().openFd(
								alarmSound + ".mp3");
					}
				} else {
					fileDescriptor = getApplication().getAssets().openFd(
							alarmSound + ".mp3");
				}
			}
			if (fileDescriptor == null)
				return;
			mediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
			// mediaPlayer.prepare();
			// mediaPlayer.start();

			final AudioManager audioManager = (AudioManager) getApplication()
					.getSystemService(Context.AUDIO_SERVICE);
			//if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.prepare();
				mediaPlayer.start();
			//}
			try {
				File temp = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/YourAppFolder/");// 自已缓存文件夹
				if (!temp.exists()) {
					temp.mkdir();
				}
				FileWriter fw = new FileWriter(temp.getAbsolutePath()
						+ "/bb.txt", true);
				if (fw != null) {
					fw.flush();
					fw.write(format1.format(new Date()));
					fw.write("  ," + content);
					fw.write("  ," + alarmSound + "\n");
					fw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (cdId == -10) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtil.parseDateTimeSs(alarmclocktime));
			calendar.add(Calendar.MINUTE, 5);
			App.getDBcApplication().updateClockDate(cdId,
					DateUtil.formatDateTimeSs(calendar.getTime()),
					DateUtil.formatDateTimeSs(calendar.getTime()));
		} else if (cdId == -1 || cdId == -2) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtil.parseDateTimeSs(alarmclocktime));
			calendar.add(Calendar.DATE, 1);
			App.getDBcApplication().updateClockDate(cdId,
					DateUtil.formatDateTimeSs(calendar.getTime()),
					DateUtil.formatDateTimeSs(calendar.getTime()));
		} else if (cdId >= 0 && "1".equals(postpone) && "1".equals(displaytime)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtil.parseDateTimeSs(alarmclocktime));
			calendar.add(Calendar.DATE, 1);
			App.getDBcApplication().updateClockDate(cdId,
					DateUtil.formatDateTimeSs(calendar.getTime()),
					DateUtil.formatDateTimeSs(calendar.getTime()));
		} else if (cdId >= 0 && "0".equals(displaytime)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtil.parseDateTimeSs(alarmclocktime));
			calendar.add(Calendar.DATE, 1);
			App.getDBcApplication().updateClockDate(cdId,
					DateUtil.formatDateTimeSs(calendar.getTime()),
					DateUtil.formatDateTimeSs(calendar.getTime()));
		}
		// Log.e("tag_cxalarmType2", alarmType);
//		if (!alarmType.equals("0")) {
		WriteAlarmClock.writeAlarm(getApplicationContext());// 写入闹钟
//		Intent intent1 = new Intent(this, WidgetService.class);
//		intent1.setAction("com.mission.schedule.widget.WidgetService");
//		intent1.setFlags(Service.START_REDELIVER_INTENT);
//		intent1.setPackage(getPackageName());
//		intent.putExtra("autoStart", "0");
//		startService(intent1);
//		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayer.stop();

		mediaPlayer.release();

		mediaPlayer = null;
	}
}

// package com.mission.schedule.service;
//
// import java.io.IOException;
// import java.util.Calendar;
// import java.util.Date;
//
// import com.mission.schedule.activity.AlarmDialog;
// import com.mission.schedule.utils.DateTimeHelper;
// import com.mission.schedule.utils.DateUtil;
//
// import android.app.Service;
// import android.content.Context;
// import android.content.Intent;
// import android.content.res.AssetFileDescriptor;
// import android.media.AudioManager;
// import android.media.MediaPlayer;
// import android.os.Handler;
// import android.os.IBinder;
// import android.util.Log;
//
// public class AlarmService extends Service {
//
// String alarmtype;
// String ringcode;
// String alarmSound;
// int cdId;
// String morningstate;
// String nightstate;
// String alarmclocktime;
// String before;
// // String content = "";
// // String alltimestate;
// // String ringstate;
// // String displaytime;
// // String postpone;
// MediaPlayer mediaPlayer = null;
//
// @Override
// public IBinder onBind(Intent intent) {
// return null;
// }
// @Override
// public void onCreate() {
// super.onCreate();
// }
// @Override
// public void onStart(Intent intent, int startId) {
// super.onStart(intent, startId);
// if(intent==null){
// return;
// }
// mediaPlayer = new MediaPlayer();
// alarmtype = intent.getStringExtra("alarmtype");
// ringcode = intent.getStringExtra("ringcode");
// alarmSound = intent.getStringExtra("alarmSound");//Desc
// cdId = intent.getIntExtra("cdId", 0);
// morningstate = intent.getStringExtra("morningstate");
// nightstate = intent.getStringExtra("nightstate");
// alarmclocktime = intent.getStringExtra("alarmclocktime");
// before = intent.getStringExtra("before");
// // content = intent.getStringExtra("content");
// // alltimestate = intent.getStringExtra("alltimestate");
// // ringstate = intent.getStringExtra("ringstate");
// // displaytime = intent.getStringExtra("displaytime");
// // postpone = intent.getStringExtra("postpone");
//
// Log.i("TAG-a1", "alarmSound======>>>" + alarmSound + "");
// Log.i("TAG-a2", ringcode+".........");
//
// // if (cdId < 0) {
// // if (cdId == -1) {
// // content = "请打开您的时间表，看看有没有新的安排！";
// // } else {
// // content = "请打开您的时间表，看看有没有未完成的事情!";
// // }
// // } else if (cdId > 0 && "0".equals(displaytime)) {
// // content = "时间表提醒您，您今天有待办的日程需要完成，请尽快办理！";
// // }
// //
// // Log.i("TAG", content + "=========..");
// // NotificationManager nm = (NotificationManager) getApplication()
// // .getSystemService(Context.NOTIFICATION_SERVICE);
// // Intent notificationIntent = new Intent(getApplication(),
// MainActivity.class);
// // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
// // | Intent.FLAG_ACTIVITY_NEW_TASK);
// // PendingIntent contentIntent = PendingIntent.getActivity(getApplication(),
// 0,
// // notificationIntent, 0);
// //
// // // 创建Notifcation
// // Notification notification = new Notification(R.drawable.logo128,
// // content, System.currentTimeMillis() + 2000);
// // // notification.defaults |= Notification.DEFAULT_SOUND;// 添加声音
// // notification.defaults |= Notification.DEFAULT_VIBRATE;
// // notification.defaults |= Notification.DEFAULT_LIGHTS;
// // notification.ledOnMS = 300;
// // notification.ledOffMS = 500;
// // notification.flags = Notification.FLAG_SHOW_LIGHTS;
// // notification.flags |= Notification.FLAG_AUTO_CANCEL;// 在通知栏上点击此通知后自动清除此通知
// // notification.setLatestEventInfo(getApplication(), "时间表", content,
// contentIntent);
// //
// // nm.notify(cdId, notification);
// //
// // if (cdId >= 0 && "1".equals(displaytime)) {
// // // 桌面显示提示
// // Intent i = new Intent(getApplication(), AlarmDialog.class);
// // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
// // i.putExtra("id", String.valueOf(cdId));
// // i.putExtra("content", content);
// // i.putExtra("time", alarmclocktime);
// // getApplication().startActivity(i);
// // }
// //
// // if (alarmSound == null)
// // return;
// //
// // if ("1".equals(ringstate)) {
// // return;
// // } else if ("2".equals(ringstate)) {
// // ringcode = "g_220";
// // alarmSound = "g_220";
// // }
// // if (cdId >= 0 && "0".equals(displaytime)) {
// // alarmtype = "1";
// // ringcode = "g_220";
// // alarmSound = "g_220";
// // }
// Log.e("TAG-a2", "....1.....");
// Calendar cd = Calendar.getInstance();
// cd.setTime(DateUtil.parseDateTime(alarmclocktime));
// cd.add(Calendar.MINUTE, -Integer.parseInt(before));
// // SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// // try {
// // File temp = new File(Environment.getExternalStorageDirectory()
// // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
// // if (!temp.exists()) {
// // temp.mkdir();
// // }
// // FileWriter fw = new FileWriter(temp.getAbsolutePath() + "/bb.txt",
// // true);
// // if (fw != null) {
// // fw.flush();
// // fw.write(format1.format(new Date()));
// // fw.write("  ," + content);
// // fw.write("  ," + alarmSound);
// // fw.close();
// // }
// // } catch (Exception e) {
// // e.printStackTrace();
// // }
// Log.e("TAG-a2", "....2.....");
// try {
// AssetFileDescriptor fileDescriptor = null;
// if ("3".equals(alarmtype)) {// >=
// // System.currentTimeMillis()
// if (DateTimeHelper.formatDateTimetoString(cd.getTime(),
// DateTimeHelper.FMT_yyyyMMddHHmm).equals(
// DateTimeHelper.formatDateTimetoString(new Date(),
// DateTimeHelper.FMT_yyyyMMddHHmm))) {
// fileDescriptor = getApplication().getAssets().openFd(ringcode + ".mp3");
// } else {
// fileDescriptor = getApplication().getAssets().openFd(
// alarmSound + ".mp3");
// }
// } else if ("2".equals(alarmtype)) {
// if (DateTimeHelper.formatDateTimetoString(cd.getTime(),
// DateTimeHelper.FMT_yyyyMMddHHmm).equals(
// DateTimeHelper.formatDateTimetoString(new Date(),
// DateTimeHelper.FMT_yyyyMMddHHmm))) {
// fileDescriptor = getApplication().getAssets().openFd(ringcode + ".mp3");
// } else {
// fileDescriptor = null;
// }
// } else if ("0".equals(alarmtype)) {
// fileDescriptor = null;
// } else {
// if (cdId < 0) {
// if(cdId==-1){
// if("0".equals(morningstate)){
// fileDescriptor = getApplication().getAssets().openFd(alarmSound);
// }
// }else if(cdId==-2){
// if("0".equals(nightstate)){
// fileDescriptor = getApplication().getAssets().openFd(alarmSound);
// }
// }
// } else {
// fileDescriptor = getApplication().getAssets().openFd(
// alarmSound + ".mp3");
// }
// }
// if (fileDescriptor == null)
// return;
// Log.e("TAG-a2", "....3.....");
// mediaPlayer
// .setDataSource(fileDescriptor.getFileDescriptor(),
// fileDescriptor.getStartOffset(),
// fileDescriptor.getLength());
// // mediaPlayer.prepare();
// // mediaPlayer.start();
//
// final AudioManager audioManager = (AudioManager) getApplication()
// .getSystemService(Context.AUDIO_SERVICE);
// if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
// Log.e("TAG-a2", "....4.....");
// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
// mediaPlayer.prepare();
// mediaPlayer.start();
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
// // WriteAlarmClock.writeAlarm(getApplication());// 写入闹钟
//
// // new Handler().postDelayed(new Runnable() {
// //
// // @Override
// // public void run() {
// // AlarmService.this.stopSelf();
// // }
// // }, 15000);
// }
//
// @Override
// public void onDestroy() {
// super.onDestroy();
// mediaPlayer.stop();
//
// mediaPlayer.release();
//
// mediaPlayer = null;
// }
// }