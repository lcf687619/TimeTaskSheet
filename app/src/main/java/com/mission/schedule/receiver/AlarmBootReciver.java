package com.mission.schedule.receiver;


import com.mission.schedule.service.AlarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBootReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		String content = "";
		String isalarmtype = "";
		int cdId = arg1.getIntExtra("cdId", 0);
		content = arg1.getStringExtra("content");
		String alarmSoundDesc = arg1.getStringExtra("alarmSoundDesc");
		String before = arg1.getStringExtra("before");
		isalarmtype = arg1.getStringExtra("isalarmtype");
		String ringcode = arg1.getStringExtra("ringcode");
		String alarmclocktime = arg1.getStringExtra("alarmclocktime");
		String displaytime = arg1.getStringExtra("displaytime");
		String postpone = arg1.getStringExtra("postpone");
		String ringstate = arg1.getStringExtra("ringstate");
		String morningstate = arg1.getStringExtra("morningstate");
		String nightstate = arg1.getStringExtra("nightstate");
		String alltimestate = arg1.getStringExtra("alltimestate");
		String alarmSound = arg1.getStringExtra("alarmSound");
		String alarmType = arg1.getStringExtra("alarmType");
		
		 Intent intent2 = new Intent(arg0, AlarmService.class);
		 intent2.setAction("com.mission.schedule.service.AlarmService");
		 intent2.setPackage("com.mission.schedule");
		 intent2.putExtra("cdId", cdId);
		 intent2.putExtra("alarmType", alarmType);
		 intent2.putExtra("alarmSound", alarmSound);
		 intent2.putExtra("content", content);
		 intent2.putExtra("alarmSoundDesc", alarmSoundDesc);
		 intent2.putExtra("before", before);
		 intent2.putExtra("isalarmtype", isalarmtype);
		 intent2.putExtra("ringcode", ringcode);
		 intent2.putExtra("alarmclocktime", alarmclocktime);
		 intent2.putExtra("displaytime", displaytime);
		 intent2.putExtra("postpone", postpone);
		 intent2.putExtra("ringstate", ringstate);
		 intent2.putExtra("morningstate", morningstate);
		 intent2.putExtra("nightstate", nightstate);
		 intent2.putExtra("alltimestate", alltimestate);
		 arg0.startService(intent2);
	}
}
