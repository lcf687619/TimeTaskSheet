package com.mission.schedule.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.applcation.App;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.LocateAllNoticeTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.utils.DateTimeHelper;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AlarmDialog extends BaseActivity implements OnClickListener,
		OnCompletionListener {
	private int id = 0;
	private String content = "";
	private String userid = "0";
	String time = "";
	// private ImageView iv_showdialog_set;
	private TextView tv_content, tv_finish, tv_after, tv_time;
	private Map<String, String> mMap = null;

	SharedPrefUtil sharedPrefUtil = null;

	Context context;
	App app = null;
	private WakeLock mWakelock;
	private KeyguardManager km;
	private KeyguardLock kl;
	String alarmType;
	String ringcode;
	String alarmSound;
	String morningstate;
	String nightstate;
	String before;
	String alltimestate;
	String ringstate;
	String displaytime;
	String postpone;
	String alarmSoundDesc;
	String isalarmtype;
	MediaPlayer mediaPlayer = null;
	String stateone = "";
	String statetwo = "";
	String dateone = "";
	String datetwo = "";
	private RelativeLayout mRelativeLayoutbackground;
	private String mWay;
	private int imgid[] = { R.mipmap.alarm_bg, R.mipmap.alarm_bg1,
			R.mipmap.alarm_bg2, R.mipmap.alarm_bg3, R.mipmap.alarm_bg4,
			R.mipmap.alarm_bg5, R.mipmap.alarm_bg6 };

	boolean fag = true;

	@Override
	protected void setListener() {

	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.alarm_dialog);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// super.onNewIntent(intent);
		setIntent(intent);
		fag = true;
		init();
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		// final Window win = getWindow();
		// win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		// | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		// win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);// |
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		// context
		// =
		// this;
		// sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		// app = App.getDBcApplication();
		// // //获取锁，保持屏幕亮度
		// // acquireWakeLock();
		// // handler.postDelayed(runnable, 10000);
		//
		// userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
		// ShareFile.USERID, "0");
		// id = getIntent().getStringExtra("id");
		// content = getIntent().getStringExtra("content");
		// time = getIntent().getStringExtra("time");
		//
		// App dbContextApplication = App.getDBcApplication();
		// mMap = dbContextApplication.getqueryschrepID(Integer.parseInt(id));
		// // mMap=dbContextApplication.getNoticeById(id);
		init();
		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// AlarmDialog.this.finish();
		// }
		// }, 15000);
	}

	@Override
	protected void setAdapter() {

	}

	private void init() {
		// iv_showdialog_set=(ImageView)findViewById(R.id.iv_showdialog_set);
		// iv_showdialog_set.setOnClickListener(this);
		// //获取锁，保持屏幕亮度
		// acquiremWakeLock();
		// handler.postDelayed(runnable, 10000);
		context = this;
		acquiremWakeLock();
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();

		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "0");
		// content = getIntent().getStringExtra("content");
		// time = getIntent().getStringExtra("time");
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		alarmType = getIntent().getStringExtra("alarmType");
		isalarmtype = getIntent().getStringExtra("isalarmtype");
		ringcode = getIntent().getStringExtra("ringcode");
		alarmSound = getIntent().getStringExtra("alarmSound");
		alarmSoundDesc = getIntent().getStringExtra("alarmSoundDesc");
		id = getIntent().getIntExtra("cdId", 0);
		morningstate = getIntent().getStringExtra("morningstate");
		nightstate = getIntent().getStringExtra("nightstate");
		time = getIntent().getStringExtra("alarmclocktime");
		before = getIntent().getStringExtra("before");
		content = getIntent().getStringExtra("content");
		alltimestate = getIntent().getStringExtra("alltimestate");
		// ringstate = getIntent().getStringExtra("ringstate");
		displaytime = getIntent().getStringExtra("displaytime");
		postpone = getIntent().getStringExtra("postpone");
		stateone = getIntent().getStringExtra("stateone");
		statetwo = getIntent().getStringExtra("statetwo");
		dateone = getIntent().getStringExtra("dateone");
		datetwo = getIntent().getStringExtra("datetwo");
		ringstate = sharedPrefUtil.getString(this, ShareFile.USERFILE,
				ShareFile.RINGSTATE, "0");
		mMap = app.getqueryschrepID(id);
		mRelativeLayoutbackground = (RelativeLayout) findViewById(R.id.alarm_dialog_background_img);
		Calendar cd1 = Calendar.getInstance();
		cd1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cd1.setTime(DateUtil.parseDateTime(time));
		cd1.add(Calendar.MINUTE, Integer.parseInt(before));
		// SimpleDateFormat format1 = new SimpleDateFormat(
		// "yyyy-MM-dd");
		mWay = String.valueOf(cd1.get(Calendar.DAY_OF_WEEK));
		int idurl = Integer.valueOf(mWay);
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = getResources().openRawResource(imgid[idurl - 1]);
		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
		BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
		mRelativeLayoutbackground.setBackgroundDrawable(bd);
		String weekstring = "";
		if (mWay.equals("1")) {
			weekstring = "星期天";
		} else if (mWay.equals("2")) {
			weekstring = "星期一";
		} else if (mWay.equals("3")) {
			weekstring = "星期二";
		} else if (mWay.equals("4")) {
			weekstring = "星期三";
		} else if (mWay.equals("5")) {
			weekstring = "星期四";
		} else if (mWay.equals("6")) {
			weekstring = "星期五";
		} else if (mWay.equals("7")) {
			weekstring = "星期六";
		}
		String timenow = Stringadd(cd1.get(Calendar.MONTH) + 1) + "-"
				+ Stringadd(cd1.get(Calendar.DATE)) + "  "
				+ Stringadd(cd1.get(Calendar.HOUR_OF_DAY)) + ":"
				+ Stringadd(cd1.get(Calendar.MINUTE)) + "  " + weekstring;
		// SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		// String weekday="周一";
		// try {
		// weekday=CharacterUtil.getWeekOfDate(context, sdf1.parse(time));
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		// Log.i("weekday", "===="+weekday);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setText(content);
		tv_finish = (TextView) findViewById(R.id.tv_finish);
		tv_finish.setOnClickListener(this);
		tv_after = (TextView) findViewById(R.id.tv_after);
		tv_after.setOnClickListener(this);
		tv_time = (TextView) findViewById(R.id.tv_time);
		if (ringstate.equals("0")) {
			Alarm(2);
		} else if (ringstate.equals("1")) {
			Alarm(1);
		} else if (ringstate.equals("2")) {
			ringcode = "g_220";
			alarmSound = "g_220";
			Alarm(2);
		}
		tv_time.setText(timenow);
		// if (null != mMap) {
		// tv_time.setText(time);
		// } else {
		// tv_time.setText(DateTimeHelper.formatDateTimetoString(new Date(),
		// DateTimeHelper.FMT_HHmm));
		// }
	}

	private String Stringadd(int num) {
		String stringnum = num + "";
		if (stringnum.length() == 1) {
			stringnum = "0" + stringnum;
		}
		return stringnum;
	}

	private void Alarm(int type) {
		try {
			Calendar cd = Calendar.getInstance();
			cd.setTime(DateUtil.parseDateTime(time));
			cd.add(Calendar.MINUTE, -Integer.parseInt(before));
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
				if (id < 0) {
					if (id == -1) {
						if ("0".equals(morningstate)) {
							fileDescriptor = getApplication().getAssets()
									.openFd(alarmSound);
						}
					} else if (id == -2) {
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

			// final AudioManager audioManager = (AudioManager) getApplication()
			// .getSystemService(Context.AUDIO_SERVICE);
			// Log.e("闹钟声音大小",
			// audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)+"===="+audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//STREAM_ALARM
			mediaPlayer.prepare();
			if (type == 1) {
				mediaPlayer.setVolume(0.0f, 0.0f);
			}
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fag) {
			WriteAlarmClock.writeAlarm(getApplicationContext());// 写入闹钟MainActivity.allContext
			fag = false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_finish:
			if (null != mMap&&mMap.size()>0) {
				int repid = 0;
				if("".equals(StringUtils.getIsStringEqulesNull(mMap
						.get(LocateAllNoticeTable.repID)))){
					repid = 0;
				}else{
					repid = Integer.parseInt(mMap
							.get(LocateAllNoticeTable.repID));
				}
				if (repid == 0) {// 普通记事
					writeCloseBase(mMap);
				} else {// 根据repid获取母记事对应的上一条和下一条
					Map<String, String> repMap = new HashMap<String, String>();
					repMap = app.QueryStateData(repid);
					if (repMap != null && repMap.size() > 0) {
						// RepeatBean repeatBean = MainActivity
						// .CreateRepeatSchDateData(repMap);
						String lastdate = StringUtils
								.getIsStringEqulesNull(repMap
										.get(CLRepeatTable.repDateOne));
						String nextdate = StringUtils
								.getIsStringEqulesNull(repMap
										.get(CLRepeatTable.repDateTwo));
						String repdate = DateUtil.formatDate(new Date()) + " "
								+ repMap.get(CLRepeatTable.repTime);
						if (repdate.equals(lastdate)
								|| repdate.equals(nextdate)) {
							if (!"".equals(lastdate)
									&& repdate.equals(lastdate)) {
								App.getDBcApplication()
										.updateSchCLRepeatData(
												Integer.parseInt(repMap
														.get(CLRepeatTable.repID)),
												repdate,
												repMap.get(CLRepeatTable.repDateTwo),
												3,
												Integer.parseInt(repMap
														.get(CLRepeatTable.repStateTwo)));
							} else if (!"".equals(nextdate)) {
								App.getDBcApplication()
										.updateSchCLRepeatData(
												Integer.parseInt(repMap
														.get(CLRepeatTable.repID)),
												repMap.get(CLRepeatTable.repDateOne),
												repdate,
												Integer.parseInt(repMap
														.get(CLRepeatTable.repStateOne)),
												3);
							}
						} else {
							if ("".equals(lastdate) && "".equals(nextdate)) {
								App.getDBcApplication()
										.updateSchCLRepeatData(
												Integer.parseInt(repMap
														.get(CLRepeatTable.repID)),
												repdate,
												repMap.get(CLRepeatTable.repDateTwo),
												3,
												Integer.parseInt(repMap
														.get(CLRepeatTable.repStateTwo)));
							} else if ("".equals(lastdate)
									&& !"".equals(nextdate)) {
								App.getDBcApplication()
										.updateSchCLRepeatData(
												Integer.parseInt(repMap
														.get(CLRepeatTable.repID)),
												repdate,
												repMap.get(CLRepeatTable.repDateTwo),
												3,
												Integer.parseInt(repMap
														.get(CLRepeatTable.repStateTwo)));
							} else if (!"".equals(lastdate)
									&& "".equals(nextdate)) {
								App.getDBcApplication()
										.updateSchCLRepeatData(
												Integer.parseInt(repMap
														.get(CLRepeatTable.repID)),
												repMap.get(CLRepeatTable.repDateOne),
												repdate,
												Integer.parseInt(repMap
														.get(CLRepeatTable.repStateOne)),
												3);
							} else {
								if (DateUtil.parseDateTime(lastdate).getTime() > DateUtil
										.parseDateTime(nextdate).getTime()) {
									App.getDBcApplication()
											.updateSchCLRepeatData(
													Integer.parseInt(repMap
															.get(CLRepeatTable.repID)),
													repMap.get(CLRepeatTable.repDateOne),
													repdate,
													Integer.parseInt(repMap
															.get(CLRepeatTable.repStateOne)),
													3);
								} else {
									App.getDBcApplication()
											.updateSchCLRepeatData(
													Integer.parseInt(repMap
															.get(CLRepeatTable.repID)),
													repdate,
													repMap.get(CLRepeatTable.repDateTwo),
													3,
													Integer.parseInt(repMap
															.get(CLRepeatTable.repStateTwo)));
								}
							}
						}
						// if (DateUtil.formatDate(new Date()).equals(
						// repeatBean.repLastCreatedTime.substring(0, 10))) {
						// app.updateSchCLRepeatData(repid,
						// repeatBean.repLastCreatedTime, repMap
						// .get(CLRepeatTable.repDateTwo), 3,
						// Integer.parseInt(repMap
						// .get(CLRepeatTable.repStateTwo)));
						// } else if (DateUtil.formatDate(new Date()).equals(
						// repeatBean.repNextCreatedTime.substring(0, 10))) {
						// app.updateSchCLRepeatData(repid, repMap
						// .get(CLRepeatTable.repDateOne),
						// repeatBean.repNextCreatedTime,
						// Integer.parseInt(repMap
						// .get(CLRepeatTable.repStateOne)), 3);
						// }
					}
					// UpLoadData();
				}
			}
			Intent intentac = new Intent(this, MainActivity.class);
			startActivity(intentac);
			this.finish();
			break;
		// case R.id.iv_showdialog_set:
		// Intent intent = new Intent(this, ShowSetActivity.class);
		// startActivity(intent);
		// this.finish();
		// break;
		case R.id.tv_after:
			this.finish();
			break;
		default:
			break;
		}
	}

	private void UpLoadData() {
		if (NetUtil.getConnectState(this) != NetWorkState.NONE) {
			Intent intent = new Intent(this, UpLoadService.class);
			intent.setAction(Const.UPLOADDATA);
			intent.setPackage(getPackageName());
			startService(intent);
		} else {
			return;
		}
	}

	private void writeCloseBase(Map<String, String> mMap) {
		App dbContextClosed = App.getDBcApplication();
		Map<String, String> mapClosed = new HashMap<String, String>();
		String repID = mMap.get(LocateAllNoticeTable.repID);
		String schID = mMap.get(LocateAllNoticeTable.schID);
		if (!"0".equals(repID)) {// 重复记事
			// mapClosed.put(LocateAllNoticeTable.repID, "0");
		} else if (id >= 0) {
			updateSchedule(mMap, ScheduleTable.schIsEnd,
					ScheduleTable.schUpdateState);
			updateSchClock(mMap, LocateAllNoticeTable.isEnd);
		}
		// if (dbContextClosed.updateLocalNoticeData(mapClosed,
		// " where orderId="
		// + mapClosed.get(LocateAllNoticeTable.orderId))) {
		// 更新桌面插件
		// Intent intent = new Intent(AlarmDialog.this, WidgetService.class);
		// startService(intent);

		// WriteAlarmClock.writeAlarm(AlarmDialog.this);// 写入闹钟
		// String ymdTime = mMap.get(LocateAllNoticeTable.noticeDate) + " " +
		// mMap.get(LocateAllNoticeTable.alarmClockTime);
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// try {
		// Date dateStr = sdf.parse(ymdTime);
		// Date dateToday = sdf.parse(sdf.format(new Date()));
		// if (!"0".equals(tpId) && dateStr.after(dateToday)) {
		// if (!dbContextClosed.updateLocalRepeatAlarmFromTime(tpId)) {
		// Toast.makeText(AlarmDialog.this,
		// "操作失败，请联系时间表小秘书...",Toast.LENGTH_SHORT).show();
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// } else {
		// Toast.makeText(AlarmDialog.this, "操作失败，请联系时间表小秘书...",
		// Toast.LENGTH_SHORT).show();
		// }
		this.finish();
	}

	private void writeCloseBase(Map<String, String> mMap,
			String d_aFinishCount, String d_allFinishCount) {
		Map<String, String> mapClosed = new HashMap<String, String>();
		mapClosed.put(LocateAllNoticeTable.alarmId,
				mMap.get(LocateAllNoticeTable.alarmId));
		App dbContextClosed = App.getDBcApplication();
		// if (dbContextClosed.updateLocalNoticeData(mapClosed,
		// " where alarmId="
		// + mapClosed.get(LocateAllNoticeTable.alarmId))) {
		// } else {
		// Toast.makeText(AlarmDialog.this,
		// "操作失败，请联系时间表小秘书...",Toast.LENGTH_SHORT).show();
		// }
	}

	@Override
	public boolean onKeyDown(int kCode, KeyEvent kEvent) {
		if (kCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(kCode, kEvent);
	}

	@SuppressLint("NewApi") @Override
	protected void onDestroy() {
		releasemWakeLock();
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mRelativeLayoutbackground.setBackgroundDrawable(null);;
		if (fag) {
			WriteAlarmClock.writeAlarm(getApplicationContext());// 写入闹钟MainActivity.allContext
		}
	}

	@Override
	public void onResume() {
		// acquiremWakeLock();
		super.onResume();
	}

	private void acquiremWakeLock() {
		// Log.e("tagtagtag1111", "====acquiremWakeLock亮屏");
		if (mWakelock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_DIM_WAKE_LOCK, "WAKE");// SCREEN_DIM_WAKE_LOCK
			mWakelock.acquire();
		}
		km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		kl = km.newKeyguardLock("unLock");
		// 解锁
		kl.disableKeyguard();
	}

	private void releasemWakeLock() {
		Log.e("tagtagtag2222", "====releasemWakeLock灭屏");
		kl.reenableKeyguard();
		if (mWakelock != null && mWakelock.isHeld()) {
			mWakelock.release();
			mWakelock = null;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// private Runnable runnable = new Runnable() {
	// @Override
	// public void run() {
	// // WindowManager.LayoutParams params = getWindow().getAttributes();
	// // params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
	// // params.screenBrightness = 0;
	// // getWindow().setAttributes(params);
	// releaseWakeLock();
	// }
	// };

	// private void acquireWakeLock() {
	// if (wakeLock ==null) {
	// System.out.println("====acquireWakeLock亮屏");
	//
	// PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	// wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
	// PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
	// wakeLock.acquire();
	//
	// }
	// }
	// private void releaseWakeLock() {
	// // if (wakeLock !=null&& wakeLock.isHeld()) {
	// // wakeLock.release();
	// // wakeLock =null;
	// // System.out.println("====releaseWakeLock释放");
	// // }
	// }

	// final Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case 1:
	// releaseWakeLock();
	// break;
	// case 2:
	//
	// break;
	// }
	// super.handleMessage(msg);
	// }
	// };

	private void updateSchedule(Map<String, String> mMap, String key,
			String key1) {
		String value = "0";
		String key2 = "";
		Map<String, String> upMap = new HashMap<String, String>();
		if (key.equals("schIsEnd")) {
			key2 = "isEnd";
		}
		if ("0".equals(mMap.get(key2)))
			value = "1";
		else
			value = "0";
		upMap.put(key, value);
		upMap.put(key1, "2");
		App.getDBcApplication().updateScheduleData(upMap,
				"where schID=" + mMap.get(LocateAllNoticeTable.schID));
		mMap.put(key, value);
	}

	private void updateSchClock(Map<String, String> mMap, String key) {
		try {
			String value = "0";
			Map<String, String> upMap = new HashMap<String, String>();
			if ("0".equals(mMap.get(key)))
				value = "1";
			else
				value = "0";
			upMap.put(key, value);
			App.getDBcApplication().updateSchIsEnd(upMap,
					"where schID=" + mMap.get("schID"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {// 监听播放器放完
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		releasemWakeLock();
		Log.e("tagtagtag1111", "====acquiremWakeLock亮屏over");
	}
}
