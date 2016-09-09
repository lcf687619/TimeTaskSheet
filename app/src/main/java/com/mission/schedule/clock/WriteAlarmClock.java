package com.mission.schedule.clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mission.schedule.applcation.App;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.entity.LocateAllNoticeTable;
import com.mission.schedule.service.ClockService;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.XmlUtil;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class WriteAlarmClock {

	public static void writeAlarm(Context context) {
		new AlarmClockAsyncTask(context).execute();
	}

	static private class AlarmClockAsyncTask extends
			AsyncTask<Void, Void, List<Map<String, String>>> {

		private Context context;
		SharedPrefUtil sharedPrefUtil = null;

		public AlarmClockAsyncTask(Context context) {
			this.context = context;
//			try {
//				File temp = new File(Environment.getExternalStorageDirectory()
//						.getPath() + "/YourAppFolder/");// 自已缓存文件夹
//				if (!temp.exists()) {
//					temp.mkdir();
//				}
//				FileWriter fw = new FileWriter(temp.getAbsolutePath()
//						+ "/writealarm.txt", true);
//				if (fw != null) {
//					fw.flush();
//					fw.write(DateUtil.formatDateTimeSs(new Date()));
//					fw.write("  ," + context);
//					fw.close();
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
		}

		@Override
		protected List<Map<String, String>> doInBackground(Void... params) {
			List<Map<String, String>> mList = null;
			App dbContextApplication = App.getDBcApplication();
			mList = dbContextApplication.getAlarmData();
//			try {
//				File temp = new File(Environment.getExternalStorageDirectory()
//						.getPath() + "/YourAppFolder/");// 自已缓存文件夹
//				if (!temp.exists()) {
//					temp.mkdir();
//				}
//				FileWriter fw = new FileWriter(temp.getAbsolutePath()
//						+ "/writealarmSize.txt", true);
//				if (fw != null) {
//					fw.flush();
//					fw.write(DateUtil.formatDateTimeSs(new Date()));
//					fw.write("  ," + mList.size());
//					fw.close();
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
			return mList;
		}

		@Override
		protected void onPostExecute(List<Map<String, String>> result) {
			super.onPostExecute(result);
			if (result != null && context != null) {
				sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
				String ringstate = sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.RINGSTATE, "0");
				String morningstate = sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.MORNINGSTATE, "0");
				String nightstate = sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.NIGHTSTATE, "0");
				String alltimestate = sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.ALLSTATE, "0");
				String alltimering = sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.MUSICCODE, "0");
				String alltime = sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.ALLTIME, "08:58");
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				list.clear();
				list = XmlUtil.readBeforeBellXML(context);
				clearAlarmClock(context);// 清空闹钟
				if (result.size() > 0) {
					JSONArray jsonArray = new JSONArray();
					int length = result.size();
					if (length > 5)
						length = 5;
					for (int i = 0; i < length; i++) {
//						try {
//							File temp = new File(Environment.getExternalStorageDirectory()
//									.getPath() + "/YourAppFolder/");// 自已缓存文件夹
//							if (!temp.exists()) {
//								temp.mkdir();
//							}
//							FileWriter fw = new FileWriter(temp.getAbsolutePath()
//									+ "/writealarmDate.txt", true);
//							if (fw != null) {
//								fw.flush();
//								fw.write("  "+ DateUtil.formatDateTimeSs(new Date()));
//								fw.write("," + result.get(i).get(LocateAllNoticeTable.noticeContent));
//								fw.write("," + result.get(i).get(LocateAllNoticeTable.alarmResultTime));
//								fw.close();
//							}
//						} catch (Exception e1) {
//							e1.printStackTrace();
//						}
						Map<String, String> mMap = result.get(i);
						String alarmResultTime = mMap
								.get(LocateAllNoticeTable.alarmResultTime);
						JSONObject jsonObject = new JSONObject();
						// int
						// id=Integer.parseInt(mMap.get(LocateAllNoticeTable.ID));
						int alarmId = Integer.parseInt(mMap
								.get(LocateAllNoticeTable.alarmId));

						String content = mMap
								.get(LocateAllNoticeTable.noticeContent);
						String alarmType = mMap
								.get(LocateAllNoticeTable.alarmType);
						String alarmSound = mMap
								.get(LocateAllNoticeTable.alarmSound);
						String alarmSoundDesc = mMap
								.get(LocateAllNoticeTable.alarmSoundDesc);
						String before = mMap
								.get(LocateAllNoticeTable.beforTime);
						String isalarmtype = mMap
								.get(LocateAllNoticeTable.isAlarmClock);
						String alarmclocktime = mMap
								.get(LocateAllNoticeTable.alarmClockTime);
						String displaytime = mMap
								.get(LocateAllNoticeTable.displayAlarm);
						String postpone = mMap
								.get(LocateAllNoticeTable.postpone);
						String stateone = mMap
								.get(LocateAllNoticeTable.stateone);
						String statetwo = mMap
								.get(LocateAllNoticeTable.statetwo);
						String dateone = mMap.get(LocateAllNoticeTable.dateone);
						String datetwo = mMap.get(LocateAllNoticeTable.datetwo);
						try {
							jsonObject.put("cdId", alarmId);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						jsonArray.put(jsonObject);

						setAlarm(alarmType, context, alarmId, content,
								alarmSound, alarmResultTime, alarmSoundDesc,
								before, isalarmtype, list, alarmclocktime,
								displaytime, postpone, ringstate, morningstate,
								nightstate, alltimestate, stateone, statetwo,
								dateone, datetwo);
					}
					save(context, jsonArray.toString());
				}
			}
		}
	}

	private static void save(Context context, String content) {// 写入SharedPreferences
		SharedPreferences sp = context.getSharedPreferences("AlarmClock",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("AlarmArray", content);
		editor.commit();

	}

	private static String read(Context context) {// 读取SharedPreferences
		SharedPreferences sp = context.getSharedPreferences("AlarmClock",
				Context.MODE_PRIVATE);
		return sp.getString("AlarmArray", "");
	}

	public static void clearAlarmClock(Context context) {// 清空闹钟
		String jsonArrayStr = read(context);
		try {
			if (!"".equals(jsonArrayStr)) {
				JSONArray jsonArray = new JSONArray(jsonArrayStr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					int cdId = jsonObject.getInt("cdId");
					Intent intent = new Intent(context, ClockService.class);// "com.mission.schedule.receiver.AlarmBootReciver"
					intent.putExtra("cdId", cdId);
					// PendingIntent sender =
					// PendingIntent.getBroadcast(context,
					// cdId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
					PendingIntent sender = PendingIntent.getService(context,
							cdId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					AlarmManager am = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					// 删除所有闹钟
					am.cancel(sender);
					sender.cancel();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置闹钟
	 * 
	 * @param con
	 *            当前上下文
	 * @param code
	 *            标记号(数据ID标记闹钟唯一)
	 * @param name
	 *            显示名称
	 * @param date
	 *            日期(yyyy-MM-dd HH:mm:ss)
	 */
	@SuppressLint("NewApi")
	private static void setAlarm(String alarmType, Context con, int code,
			String name, String alarmSound, String date, String alarmSoundDesc,
			String before, String isalarmtype, List<Map<String, String>> list,
			String alarmclocktime, String displaytime, String postpone,
			String ringstate, String morningstate, String nightstate,
			String alltimestate, String stateone, String statetwo,
			String dateone, String datetwo) {
		try {

			Calendar cc = Calendar.getInstance();

			String ymd = date.split(" ")[0];
			String hms = date.split(" ")[1];

			int year = Integer.parseInt(ymd.split("-")[0]);
			int month = Integer.parseInt(ymd.split("-")[1]) - 1;
			int day = Integer.parseInt(ymd.split("-")[2]);
			int hourOfDay = Integer.parseInt(hms.split(":")[0]);
			int minute = Integer.parseInt(hms.split(":")[1]);
			int ss = Integer.parseInt(hms.split(":")[2]);
			cc.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			cc.set(year, month, day, hourOfDay, minute);

			if (DateUtil.parseDateTime(
					DateUtil.formatDateTime(cc.getTime())).getTime() > DateUtil
					.parseDateTime(DateUtil.formatDateTime(new Date()))
					.getTime()) {
				Intent intents = new Intent(con, ClockService.class);
				intents.putExtra("cdId", code);
				intents.putExtra("WriteAlarmClockwrite", "1");
				intents.putExtra("content", name);
				intents.putExtra("alarmType", alarmType);//
				intents.putExtra("time", hourOfDay + ":"
						+ (minute < 10 ? "0" + minute : minute));
				intents.putExtra("alarmSoundDesc", alarmSoundDesc);
				intents.putExtra("before", before);
				intents.putExtra("isalarmtype", isalarmtype);
				intents.putExtra("alarmclocktime", alarmclocktime);
				intents.putExtra("displaytime", displaytime);
				intents.putExtra("postpone", postpone);
				intents.putExtra("ringstate", ringstate);
				intents.putExtra("morningstate", morningstate);
				intents.putExtra("nightstate", nightstate);
				intents.putExtra("alltimestate", alltimestate);
				intents.putExtra("stateone", stateone);
				intents.putExtra("statetwo", statetwo);
				intents.putExtra("dateone", dateone);
				intents.putExtra("datetwo", datetwo);
				if (!"0".equals(isalarmtype)) {
					if ("0".equals(displaytime)) {
						intents.putExtra("ringcode", "g_207");
						intents.putExtra("alarmSound", "g_207");
					} else {
						intents.putExtra("alarmSound", alarmSound);
						if (!"".equals(alarmSoundDesc)) {
							for (int i = 0; i < list.size(); i++) {
								if (alarmSoundDesc.equals(list.get(i).get(
										"name"))) {
									intents.putExtra("ringcode", list.get(i)
											.get("value"));
									break;
								}
							}
						} else {
							intents.putExtra("ringcode", alarmSound);
						}
					}
				} else {
					intents.putExtra("alarmSound", alarmSound);
					intents.putExtra("ringcode", "");
				}
				// PendingIntent senders = PendingIntent.getBroadcast(con, code,
				// intents, PendingIntent.FLAG_UPDATE_CURRENT);//
				// AlarmManager ams = (AlarmManager) con
				// .getSystemService(Context.ALARM_SERVICE);//
				// Context.ALARM_SERVICE
				PendingIntent senders = PendingIntent.getService(con, code,
						intents, PendingIntent.FLAG_UPDATE_CURRENT);//
				AlarmManager ams = (AlarmManager) con
						.getSystemService(Context.ALARM_SERVICE);// Context.ALARM_SERVICE
				int version = android.os.Build.VERSION.SDK_INT;
				// 将闹铃时间记录到系统中
				if ("0".equals(alarmType)) {// 普通
					long c = cc.getTimeInMillis();
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
				} else if ("7".equals(alarmType)) {// 每天早晚
					// long interval = 24 * 60 * 60 * 1000;
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
				} else if ("1".equals(alarmType)) {// 每天
					// long interval = 24 * 60 * 60 * 1000;
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
				} else if ("2".equals(alarmType)) {// 每周
					// long interval = 7 * 24 * 60 * 60 * 1000;
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
				} else if ("3".equals(alarmType)) {// 每月
					// int days = cc.getActualMaximum(Calendar.DAY_OF_MONTH);
					// long interval = days * 24 * 60 * 60 * 1000;
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
				} else if ("4".equals(alarmType)) {// 每年
					// int yearDay = cc.getActualMaximum(Calendar.DAY_OF_YEAR);
					// long interval = yearDay * 24 * 60 * 60 * 1000;
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
				} else if ("5".equals(alarmType)) {
					// long interval = 24 * 60 * 60 * 1000;
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
					// long interval;
					// if ("周五".equals(CharacterUtil.getWeekOfDate(con,
					// format.parse(format.format(new Date()))))) {
					// interval = 24 * 60 * 60 * 1000;
					// } else if ("周六".equals(CharacterUtil.getWeekOfDate(con,
					// format.parse(format.format(new Date()))))) {
					// interval = 2 * 24 * 60 * 60 * 1000;
					// } else {
					// interval = 24 * 60 * 60 * 1000;
					// }
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
				} else if ("6".equals(alarmType)) {// 每年
					// int yearDay = cc.getActualMaximum(Calendar.DAY_OF_YEAR);
					// long interval = yearDay * 24 * 60 * 60 * 1000;
					// if (version >= 19) {
					// ams.setWindow(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// } else {
					// ams.setRepeating(AlarmManager.RTC_WAKEUP,
					// cc.getTimeInMillis(), interval, senders);
					// }
					if (version >= 19) {
						ams.setExact(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), senders);
					} else {
						ams.set(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
								senders);
					}
				} else if ("10".equals(alarmType)) {// 每5分钟
					long interval = 5 * 60 * 1000;
					if (version >= 19) {
						ams.setWindow(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), interval, senders);
					} else {
						ams.setRepeating(AlarmManager.RTC_WAKEUP,
								cc.getTimeInMillis(), interval, senders);
					}
				}
//				try {
//					File temp = new File(Environment
//							.getExternalStorageDirectory().getPath()
//							+ "/YourAppFolder/");// 自已缓存文件夹
//					if (!temp.exists()) {
//						temp.mkdir();
//					}
//					FileWriter fw = new FileWriter(temp.getAbsolutePath()
//							+ "/as.txt", true);
//					if (fw != null) {
//						fw.flush();
//						fw.write(DateUtil.formatDateTimeSs(new Date()));
//						fw.write("  ," + code);
//						fw.write("  ,传入时间--" + date);
//						fw.write("  ,日历时间--"
//								+ DateUtil
//										.parseDateTimeSs(
//												DateUtil.formatDateTimeSs(cc
//														.getTime())).getTime());
//						fw.write("  ,日历时间戳--" + cc.getTime());
//						fw.write("  ,系统时间戳--" + System.currentTimeMillis());
//						fw.write(" ," + isalarmtype);
//						fw.write("  ," + name + "。" + "\n");
//						fw.close();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//			try {
//				File temp = new File(Environment.getExternalStorageDirectory()
//						.getPath() + "/YourAppFolder/");// 自已缓存文件夹
//				if (!temp.exists()) {
//					temp.mkdir();
//				}
//				FileWriter fw = new FileWriter(temp.getAbsolutePath()
//						+ "/error123.txt", true);
//				if (fw != null) {
//					fw.flush();
//					fw.write(e + "\n");
//					fw.close();
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
		} finally {
		}
	}
}
