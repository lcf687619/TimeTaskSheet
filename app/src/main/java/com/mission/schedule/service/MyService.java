package com.mission.schedule.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


public class MyService extends Service {
	private boolean isServiceRunning = false; 
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override  
	public void onCreate() {  
		IntentFilter filter=new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver,filter);
		 
	}
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_TIME_TICK)) {
			  //检查Service状态 
				ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
				for (RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
					if("com.mission.schedule.service.MyService".equals(service.service.getClassName())) 
					 //Service的类名 
					{ 
						isServiceRunning = true; 
//						Intent startactivity = new Intent(context, MainActivity.class);
//						context.startActivity(startactivity);
//						QueryAlarmData.writeAlarm(context);
//						WriteAlarmClock.writeAlarm(context);
					} 
				} 
				if (!isServiceRunning) { 
					//=========处理闹钟
					Intent i = new Intent(context, MyService.class); 
					i.setAction("com.mission.schedule.service.MyService");
					i.setPackage("com.mission.schedule");
				    context.startService(i); 
				}
			}
		}
	};
	@Override  
    public void onDestroy() {
	}
	@Override  
	public void onStart(Intent intent, int startid) {
	}
}
