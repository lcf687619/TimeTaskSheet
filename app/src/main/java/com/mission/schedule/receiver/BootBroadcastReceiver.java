package com.mission.schedule.receiver;

import com.mission.schedule.service.ClockService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	// 重写onReceive方法
	@Override
	public void onReceive(Context context, Intent intent) {
		  if (intent.getAction().equals(ACTION)){
			// 后边的XXX.class就是要启动的服务
				Intent service = new Intent(context, ClockService.class);
//				service.setAction("com.mission.schedule.widget.WidgetService");
				service.setAction("com.mission.schedule.service.ClockService");
				service.setPackage("com.mission.schedule");
//				service.putExtra("autoStart", "1");
				service.putExtra("WriteAlarmClockwrite", "2");
				context.startService(service);
				Log.e("TAG", "开机自动服务自动启动.....");
				System.err.println("开机自动服务自动启动.....:"+"com.mission.schedule");
        }
		// 启动应用，参数为需要自动启动的应用的包名
//		Intent intent1 = context.getPackageManager().getLaunchIntentForPackage(
//				"com.mission.schedule");
//		context.startActivity(intent1);

//		Intent i = new Intent(context, WidgetService.class);
//		PendingIntent refreshIntent = PendingIntent
//				.getService(context, 0, i, 0);
//		AlarmManager alarm = (AlarmManager) context
//				.getSystemService("alarm");
//		alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), refreshIntent);
//		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//				60000, refreshIntent);
	}

}
