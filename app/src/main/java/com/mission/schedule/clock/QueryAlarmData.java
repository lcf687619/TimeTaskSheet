package com.mission.schedule.clock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;

import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.RepeatDateUtils;

public class QueryAlarmData {
	public static void writeAlarm(Context context) {
		new QueryClockAsyncTask(context).execute();
	}

	static private class QueryClockAsyncTask extends
			AsyncTask<Void, Void, List<Map<String, String>>> {

		private Context context;

		public QueryClockAsyncTask(Context context) {
			this.context = context;
		}

		@Override
		protected List<Map<String, String>> doInBackground(Void... params) {
			App dbContextApplication = App.getDBcApplication();
			dbContextApplication.deleteclockData();
			try {
				List<Map<String, String>> schList = new ArrayList<Map<String, String>>();
				List<Map<String, String>> repList = new ArrayList<Map<String, String>>();
				List<Map<String, String>> untask = new ArrayList<Map<String, String>>();
				schList.clear();
				repList.clear();
				// App dbContextApplication = App.getDBcApplication();
				schList = dbContextApplication.queryAllSchData(-2,0,0);
				untask = dbContextApplication.queryAllSchData(-3,0,0);
				repList = dbContextApplication.QueryAllChongFuData(4);
				if (untask != null && untask.size() > 0) {
					dbContextApplication
							.insertClockData(
									DateUtil.formatDateTimeSs(DateUtil.parseDateTime(untask
											.get(0).get(ScheduleTable.schDate)
											+ " "
											+ untask.get(0).get(
													ScheduleTable.schTime))),
									untask.get(0).get(ScheduleTable.schContent),
									Integer.parseInt(untask.get(0).get(
											ScheduleTable.schBeforeTime)),
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(untask
											.get(0).get(ScheduleTable.schDate)
											+ " "
											+ untask.get(0).get(
													ScheduleTable.schTime))),
									untask.get(0)
											.get(ScheduleTable.schRingDesc),
									untask.get(0)
											.get(ScheduleTable.schRingCode),
									Integer.parseInt(untask.get(0).get(
											ScheduleTable.schDisplayTime)),
									Integer.parseInt(untask.get(0).get(
											ScheduleTable.schIsPostpone)), 0,
									Integer.parseInt(untask.get(0).get(
											ScheduleTable.schID)), 0, Integer
											.parseInt(untask.get(0).get(
													ScheduleTable.schIsAlarm)),
									0, "", 0, 0, "", "");
				}
				if (schList != null && schList.size() > 0) {
					for (int i = 0; i < schList.size(); i++) {
						if ("1".equals(schList.get(i).get(
								ScheduleTable.schIsAlarm))) {
							dbContextApplication.insertClockData(
									DateUtil.formatDateTimeSs(DateUtil.parseDateTime(schList.get(
											i).get(ScheduleTable.schDate)
											+ " "
											+ schList.get(i).get(
													ScheduleTable.schTime))),
									schList.get(i)
											.get(ScheduleTable.schContent),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schBeforeTime)),
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(schList.get(
											i).get(ScheduleTable.schDate)
											+ " "
											+ schList.get(i).get(
													ScheduleTable.schTime))),
									schList.get(i).get(
											ScheduleTable.schRingDesc),
									schList.get(i).get(
											ScheduleTable.schRingCode),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schDisplayTime)),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schIsPostpone)), 0,
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schID)), 0, Integer
											.parseInt(schList.get(i).get(
													ScheduleTable.schIsAlarm)),
									0, "", 0, 0, "", "");
						} else if ("2".equals(schList.get(i).get(
								ScheduleTable.schIsAlarm))) {
							String alarmResultTime = DateUtil.formatDateTimeSsLong(DateUtil.parseDateTime(schList.get(i).get(
											ScheduleTable.schDate)
											+ " "
											+ schList.get(i).get(
													ScheduleTable.schTime))
									.getTime()
									- Integer.parseInt(schList.get(i).get(
											ScheduleTable.schBeforeTime))
									* 60
									* 1000);
							// dateFormat.format(format.parse(schList.get(i)
							// .get(ScheduleTable.schDate)
							// + " "
							// + schList.get(i).get(
							// ScheduleTable.schTime)))
							dbContextApplication.insertClockData(
									alarmResultTime,
									InitBefore(schList.get(i).get(
											ScheduleTable.schBeforeTime))
											+ schList.get(i).get(
													ScheduleTable.schContent),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schBeforeTime)),
									alarmResultTime,
									schList.get(i).get(
											ScheduleTable.schRingDesc),
									schList.get(i).get(
											ScheduleTable.schRingCode),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schDisplayTime)),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schIsPostpone)), 0,
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schID)), 0, Integer
											.parseInt(schList.get(i).get(
													ScheduleTable.schIsAlarm)),
									0, "", 0, 0, "", "");
						} else if ("3".equals(schList.get(i).get(
								ScheduleTable.schIsAlarm))) {
							dbContextApplication.insertClockData(
									DateUtil.formatDateTimeSs(DateUtil.parseDateTime(schList.get(
											i).get(ScheduleTable.schDate)
											+ " "
											+ schList.get(i).get(
													ScheduleTable.schTime))),
									schList.get(i)
											.get(ScheduleTable.schContent),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schBeforeTime)),
											DateUtil.formatDateTimeSs(DateUtil.parseDateTime(schList.get(
											i).get(ScheduleTable.schDate)
											+ " "
											+ schList.get(i).get(
													ScheduleTable.schTime))),
									schList.get(i).get(
											ScheduleTable.schRingDesc),
									schList.get(i).get(
											ScheduleTable.schRingCode),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schDisplayTime)),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schIsPostpone)), 0,
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schID)), 0, Integer
											.parseInt(schList.get(i).get(
													ScheduleTable.schIsAlarm)),
									0, "", 0, 0, "", "");
							String alarmResultTime = DateUtil.formatDateTimeSsLong(DateUtil.parseDateTime(schList.get(i).get(
											ScheduleTable.schDate)
											+ " "
											+ schList.get(i).get(
													ScheduleTable.schTime))
									.getTime()
									- Integer.parseInt(schList.get(i).get(
											ScheduleTable.schBeforeTime))
									* 60
									* 1000);
							dbContextApplication.insertClockData(
									alarmResultTime,
									InitBefore(schList.get(i).get(
											ScheduleTable.schBeforeTime))
											+ schList.get(i).get(
													ScheduleTable.schContent),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schBeforeTime)),
									alarmResultTime,
									schList.get(i).get(
											ScheduleTable.schRingDesc),
									schList.get(i).get(
											ScheduleTable.schRingCode),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schDisplayTime)),
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schIsPostpone)), 0,
									Integer.parseInt(schList.get(i).get(
											ScheduleTable.schID)), 0, Integer
											.parseInt(schList.get(i).get(
													ScheduleTable.schIsAlarm)),
									0, "", 0, 0, "", "");
						}

					}
				}
				if (repList != null && repList.size() > 0) {
					for (int i = 0; i < repList.size(); i++) {
						RepeatBean bean = null;
						int index = 0;
						if ("1".equals(repList.get(i)
								.get(CLRepeatTable.repType))) {
							index = 1;
							bean = RepeatDateUtils.saveCalendar(repList.get(i)
									.get(CLRepeatTable.repTime), 1, "", "");
						} else if ("2".equals(repList.get(i).get(
								CLRepeatTable.repType))) {
							index = 2;
							bean = RepeatDateUtils
									.saveCalendar(
											repList.get(i).get(
													CLRepeatTable.repTime),
											2,
											repList.get(i)
													.get(CLRepeatTable.repTypeParameter)
													.replace("[", "")
													.replace("]", "")
													.replace("\"", "")
													.replace("\n\"", "")
													.replace("\n", ""), "");
						} else if ("3".equals(repList.get(i).get(
								CLRepeatTable.repType))) {
							index = 3;
							bean = RepeatDateUtils
									.saveCalendar(
											repList.get(i).get(
													CLRepeatTable.repTime),
											3,
											repList.get(i)
													.get(CLRepeatTable.repTypeParameter)
													.replace("[", "")
													.replace("]", "")
													.replace("\"", "")
													.replace("\n\"", "")
													.replace("\n", ""), "");
						} else if ("4".equals(repList.get(i).get(
								CLRepeatTable.repType))) {
							index = 4;
							bean = RepeatDateUtils
									.saveCalendar(
											repList.get(i).get(
													CLRepeatTable.repTime),
											4,
											repList.get(i)
													.get(CLRepeatTable.repTypeParameter)
													.replace("[", "")
													.replace("]", "")
													.replace("\"", "")
													.replace("\n\"", "")
													.replace("\n", ""), "0");
						} else if ("5".equals(repList.get(i).get(
								CLRepeatTable.repType))) {
							index = 5;
							bean = RepeatDateUtils.saveCalendar(repList.get(i)
									.get(CLRepeatTable.repTime), 5, "", "");
						} else if ("6".equals(repList.get(i).get(
								CLRepeatTable.repType))) {
							index = 6;
							bean = RepeatDateUtils
									.saveCalendar(
											repList.get(i).get(
													CLRepeatTable.repTime),
											4,
											repList.get(i)
													.get(CLRepeatTable.repTypeParameter)
													.replace("[", "")
													.replace("]", "")
													.replace("\"", "")
													.replace("\n\"", "")
													.replace("\n", ""), "1");
						}
						if ("1".equals(repList.get(i).get(
								CLRepeatTable.repIsAlarm))) {
							App.getDBcApplication().insertClockData(
									DateUtil.formatDateTimeSs(DateUtil.parseDateTime(bean.repNextCreatedTime)),
									repList.get(i)
											.get(CLRepeatTable.repContent),
									0,
									DateUtil.formatDateTimeSs(DateUtil.parseDateTime(bean.repNextCreatedTime)),
									repList.get(i).get(
											CLRepeatTable.repRingDesc),
									repList.get(i).get(
											CLRepeatTable.repRingCode),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repDisplayTime)),
									0,
									index,
									0,
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repID)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repIsAlarm)),
									0,
									repList.get(i).get(
											CLRepeatTable.repTypeParameter),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateOne)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateTwo)),
									repList.get(i)
											.get(CLRepeatTable.repDateOne),
									repList.get(i)
											.get(CLRepeatTable.repDateTwo));
						} else if ("2".equals(repList.get(i).get(
								CLRepeatTable.repIsAlarm))) {
							String alarmResultTime = DateUtil.formatDateTimeSsLong(DateUtil.parseDateTime(bean.repNextCreatedTime).getTime()
									- Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repBeforeTime))
									* 60
									* 1000);
							// dateFormat.format(format.parse(bean.repNextCreatedTime))
							App.getDBcApplication().insertClockData(
									alarmResultTime,
									InitBefore(repList.get(i).get(
											CLRepeatTable.repBeforeTime))
											+ repList.get(i).get(
													CLRepeatTable.repContent),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repBeforeTime)),
									alarmResultTime,
									repList.get(i).get(
											CLRepeatTable.repRingDesc),
									repList.get(i).get(
											CLRepeatTable.repRingCode),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repDisplayTime)),
									0,
									index,
									0,
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repID)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repIsAlarm)),
									0,
									repList.get(i).get(
											CLRepeatTable.repTypeParameter),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateOne)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateTwo)),
									repList.get(i)
											.get(CLRepeatTable.repDateOne),
									repList.get(i)
											.get(CLRepeatTable.repDateTwo));

						} else if ("3".equals(repList.get(i).get(
								CLRepeatTable.repIsAlarm))) {
							App.getDBcApplication().insertClockData(
									DateUtil.formatDateTimeSs(DateUtil.parseDateTime(bean.repNextCreatedTime)),
									repList.get(i)
											.get(CLRepeatTable.repContent),
									0,
									DateUtil.formatDateTimeSs(DateUtil.parseDateTime(bean.repNextCreatedTime)),
									repList.get(i).get(
											CLRepeatTable.repRingDesc),
									repList.get(i).get(
											CLRepeatTable.repRingCode),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repDisplayTime)),
									0,
									index,
									0,
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repID)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repIsAlarm)),
									0,
									repList.get(i).get(
											CLRepeatTable.repTypeParameter),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateOne)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateTwo)),
									repList.get(i)
											.get(CLRepeatTable.repDateOne),
									repList.get(i)
											.get(CLRepeatTable.repDateTwo));
							String alarmResultTime = DateUtil.formatDateTimeSsLong(DateUtil.parseDateTime(bean.repNextCreatedTime).getTime()
									- Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repBeforeTime))
									* 60
									* 1000);
							App.getDBcApplication().insertClockData(
									alarmResultTime,
									InitBefore(repList.get(i).get(
											CLRepeatTable.repBeforeTime))
											+ repList.get(i).get(
													CLRepeatTable.repContent),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repBeforeTime)),
									alarmResultTime,
									repList.get(i).get(
											CLRepeatTable.repRingDesc),
									repList.get(i).get(
											CLRepeatTable.repRingCode),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repDisplayTime)),
									0,
									index,
									0,
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repID)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repIsAlarm)),
									0,
									repList.get(i).get(
											CLRepeatTable.repTypeParameter),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateOne)),
									Integer.parseInt(repList.get(i).get(
											CLRepeatTable.repStateTwo)),
									repList.get(i)
											.get(CLRepeatTable.repDateOne),
									repList.get(i)
											.get(CLRepeatTable.repDateTwo));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}

		@Override
		protected void onPostExecute(List<Map<String, String>> result) {
			super.onPostExecute(result);
			WriteAlarmClock.writeAlarm(context);
		}
	}

	private static String InitBefore(String before) {
		String beforeStr = "";
		if ("5".equals(before)) {
			beforeStr = "5分钟后,";
		} else if ("15".equals(before)) {
			beforeStr = "15分钟后,";
		} else if ("30".equals(before)) {
			beforeStr = "30分钟后,";
		} else if ("60".equals(before)) {
			beforeStr = "1小时后,";
		} else if ("120".equals(before)) {
			beforeStr = "2小时后,";
		} else if ((24 * 60 + "").equals(before)) {
			beforeStr = "1天后,";
		} else if ((48 * 60 + "").equals(before)) {
			beforeStr = "2天后,";
		} else if ((7 * 24 * 60 + "").equals(before)) {
			beforeStr = "1周后,";
		}
		return beforeStr;
	}
}
