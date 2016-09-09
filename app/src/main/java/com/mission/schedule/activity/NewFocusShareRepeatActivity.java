package com.mission.schedule.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.NewFocusShareRepeatAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewFocusShareBean;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.entity.CLFindScheduleTable;
import com.mission.schedule.service.NewFocusShareService;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.IXListViewListener;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.NewFocusShareDayComparator;
import com.mission.schedule.utils.NewFocusShareYangLiYearComparator;
import com.mission.schedule.utils.NewFocusShareYinLiComparator;
import com.mission.schedule.utils.SharedPrefUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class NewFocusShareRepeatActivity extends BaseActivity implements
		OnClickListener, IXListViewListener {

	@ViewResId(id = R.id.bottom_rl)
	private RelativeLayout bottom_rl;
	@ViewResId(id = R.id.repeat_listview)
	private SwipeXListViewNoHead repeat_listview;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	App app = null;
	NewFocusShareRepeatAdapter adapter = null;

	List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everydayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> workdayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyweekList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everymonthList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyyearList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> nongliList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> allList = new ArrayList<Map<String,String>>();

	CalendarChangeValue changeValue = new CalendarChangeValue();
	private UpdateDataReceiver receiver = null;
	public static List<Map<String, String>> focusRepList = new ArrayList<Map<String, String>>();

	@Override
	protected void setListener() {
		bottom_rl.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newfocussharerepeat);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		EventBus.getDefault().register(this);
		receiver = new UpdateDataReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NewFocusShareService.REPUPDATADATA);
		registerReceiver(receiver, filter);
		repeat_listview.setPullLoadEnable(true);
		repeat_listview.setXListViewListener(this);
		repeat_listview.setFocusable(true);

		View footView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		repeat_listview.addFooterView(footView);

		loadData();
	}

	@SuppressWarnings("null")
	private void loadData() {
		everyyearList.clear();
		everymonthList.clear();
		everyweekList.clear();
		workdayList.clear();
		everydayList.clear();
		nongliList.clear();
		mList.clear();
		allList.clear();
		allList = app.QueryNewFocusData(6,
					NewFocusShareEditActivity.been.id);
		try {
			if (allList == null && allList.size() == 0) {
				return;
			} else {
				for (Map<String, String> map : allList) {
					if ("1".equals(map.get(CLFindScheduleTable.fstRepType))) {
						everydayList.add(map);
					} else if ("2".equals(map
							.get(CLFindScheduleTable.fstRepType))) {
						everyweekList.add(map);
						Collections.sort(everyweekList,
								new NewFocusShareDayComparator());
					} else if ("3".equals(map
							.get(CLFindScheduleTable.fstRepType))) {
						everymonthList.add(map);
						Collections.sort(everymonthList,
								new NewFocusShareDayComparator());
					} else if ("4".equals(map
							.get(CLFindScheduleTable.fstRepType))) {
						everyyearList.add(map);
						Collections.sort(everyyearList,
								new NewFocusShareYangLiYearComparator());
					} else if ("6".equals(map
							.get(CLFindScheduleTable.fstRepType))) {
						nongliList.add(map);
						Collections.sort(nongliList,
								new NewFocusShareYinLiComparator());
					} else {
						workdayList.add(map);
					}
				}
				mList.addAll(mList.size(), everydayList);
				mList.addAll(mList.size(), workdayList);
				mList.addAll(mList.size(), everyweekList);
				mList.addAll(mList.size(), everymonthList);
				mList.addAll(mList.size(), everyyearList);
				mList.addAll(mList.size(), nongliList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setAdapter() {
		adapter = new NewFocusShareRepeatAdapter(context, mList,
				R.layout.adapter_newfocussharerepeat, handler);
		repeat_listview.setAdapter(adapter);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Map<String, String> mMap = (Map<String, String>) msg.obj;
			if (Integer.parseInt(mMap.get(CLFindScheduleTable.fstRepeatId)) < 0) {
				if (focusRepList != null && focusRepList.size() > 0) {
					for (Map<String, String> map : focusRepList) {
						Object[] indexStr = map.keySet().toArray();
						String index = indexStr[0].toString();
						if (Integer.parseInt(mMap
								.get(CLFindScheduleTable.fstRepeatId)) == Integer
								.parseInt(index)) {
							mMap.put(CLFindScheduleTable.fstRepeatId,
									map.get(index));
							break;
						}
					}
				}
			}
			// 0删除 1点击内容
			switch (msg.what) {
			case 0:
				alertDeleteDialog(mMap);
				break;
			case 1:
				dialogOnClick(mMap);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bottom_rl:
			dialogAddRepeatOnClick();
			break;
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			String updatetime = sharedPrefUtil.getString(context,
					ShareFile.USERFILE, ShareFile.FOCUSUPDATETIME,
					DateUtil.formatDateTimeSs(new Date()));
			if (DateUtil.parseDateTimeSs(DateUtil.formatDateTimeSs(new Date()))
					.getTime() - DateUtil.parseDateTimeSs(updatetime).getTime() > 10000
					|| DateUtil.parseDateTimeSs(
							DateUtil.formatDateTimeSs(new Date())).getTime()
							- DateUtil.parseDateTimeSs(updatetime).getTime() == 0) {
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.FOCUSUPDATETIME,
						DateUtil.formatDateTimeSs(new Date()));
				loadData();
				adapter.notifyDataSetChanged();
				isNetWork();
			} else {
				loadData();
				adapter.notifyDataSetChanged();
				onLoad();
			}
		} else {
			loadData();
			adapter.notifyDataSetChanged();
			onLoad();
		}
	}

	private void isNetWork() {
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			Intent intent = new Intent(context, NewFocusShareService.class);
			intent.setAction("upanddown");
			intent.setPackage(getPackageName());
			startService(intent);
		} else {
			return;
		}
	}

	@Override
	public void onLoadMore() {
		onLoad();
	}

	private void onLoad() {
		SwipeXListViewNoHead swipeXListView = null;
		if (repeat_listview.getVisibility() == View.VISIBLE) {
			swipeXListView = repeat_listview;
		} else {
			// swipeXListView = myImpotent_listview;
		}
		swipeXListView.stopRefresh();
		swipeXListView.stopLoadMore();
		// swipeXListView.setRefreshTime("刚刚" + date);
	}

	/*********************************** 点击详情弹出对话框 **********************************************/
	private void dialogOnClick(Map<String, String> map) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_newfocussharericheng,
				null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		dialog.show();

		new MyGeneralOnClick(dialog, more_pop_menu, map);
	}

	class MyGeneralOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private TextView edit_tv;
		private TextView zhuanfa_tv;
		private TextView addmysch_tv;
		private TextView delete_tv;
		private TextView cancle_tv;

		NewFocusShareBean bean = null;
		Map<String, String> mMap = null;

		@SuppressLint("NewApi")
		public MyGeneralOnClick(Dialog dialog, View view,
				Map<String, String> map) {
			this.dialog = dialog;
			this.view = view;
			this.mMap = map;
			initview();
			initdata(map);
		}

		private void initdata(Map<String, String> map) {
			bean = new NewFocusShareBean();
			bean.fstID = Integer.parseInt(map.get(CLFindScheduleTable.fstID));
			bean.fstFID = Integer.parseInt(map.get(CLFindScheduleTable.fstFID));
			bean.fstSchID = Integer.parseInt(map
					.get(CLFindScheduleTable.fstSchID));
			bean.fstType = Integer.parseInt(map
					.get(CLFindScheduleTable.fstType));
			bean.fstBeforeTime = Integer.parseInt(map
					.get(CLFindScheduleTable.fstBeforeTime));
			bean.fstIsAlarm = Integer.parseInt(map
					.get(CLFindScheduleTable.fstIsAlarm));
			bean.fstDisplayTime = Integer.parseInt(map
					.get(CLFindScheduleTable.fstDisplayTime));
			bean.fstColorType = Integer.parseInt(map
					.get(CLFindScheduleTable.fstColorType));
			bean.fstIsPostpone = Integer.parseInt(map
					.get(CLFindScheduleTable.fstIsPostpone));
			bean.fstIsImportant = Integer.parseInt(map
					.get(CLFindScheduleTable.fstIsImportant));
			bean.fstIsEnd = Integer.parseInt(map
					.get(CLFindScheduleTable.fstIsEnd));
			bean.fstSourceType = Integer.parseInt(map
					.get(CLFindScheduleTable.fstSourceType));
			bean.fstRepeatId = Integer.parseInt(map
					.get(CLFindScheduleTable.fstRepeatId));
			bean.fstOpenState = Integer.parseInt(map
					.get(CLFindScheduleTable.fstOpenState));
			bean.fstRepeatLink = Integer.parseInt(map
					.get(CLFindScheduleTable.fstRepeatLink));
			bean.fstRecommendId = Integer.parseInt(map
					.get(CLFindScheduleTable.fstRecommendId));
			bean.fstIsRead = Integer.parseInt(map
					.get(CLFindScheduleTable.fstIsRead));
			bean.fstAID = Integer.parseInt(map.get(CLFindScheduleTable.fstAID));
			bean.fstIsPuase = Integer.parseInt(map
					.get(CLFindScheduleTable.fstIsPuase));
			bean.fstRepStateOne = Integer.parseInt(map
					.get(CLFindScheduleTable.fstRepStateOne));
			bean.fstRepStateTwo = Integer.parseInt(map
					.get(CLFindScheduleTable.fstRepStateTwo));
			bean.fstRepInStable = Integer.parseInt(map
					.get(CLFindScheduleTable.fstRepInStable));
			bean.fstPostState = Integer.parseInt(map
					.get(CLFindScheduleTable.fstPostState));
			bean.fstRepType = Integer.parseInt(map
					.get(CLFindScheduleTable.fstRepType));
			bean.fstAType = Integer.parseInt(map
					.get(CLFindScheduleTable.fstAType));
			bean.fstUpdateState = Integer.parseInt(map
					.get(CLFindScheduleTable.fstUpdateState));
			bean.fstParameter = map.get(CLFindScheduleTable.fstParameter);
			bean.fstContent = map.get(CLFindScheduleTable.fstContent);
			bean.fstDate = map.get(CLFindScheduleTable.fstDate);
			bean.fstTime = map.get(CLFindScheduleTable.fstTime);
			bean.fstRingCode = map.get(CLFindScheduleTable.fstRingCode);
			bean.fstRingDesc = map.get(CLFindScheduleTable.fstRingDesc);
			bean.fstTags = map.get(CLFindScheduleTable.fstTags);
			bean.fstSourceDesc = map.get(CLFindScheduleTable.fstSourceDesc);
			bean.fstSourceDescSpare = map
					.get(CLFindScheduleTable.fstSourceDescSpare);
			bean.fstRepeatDate = map.get(CLFindScheduleTable.fstRepeatDate);
			bean.fstRepStartDate = map.get(CLFindScheduleTable.fstRepStartDate);
			bean.fstRpNextCreatedTime = map
					.get(CLFindScheduleTable.fstRpNextCreatedTime);
			bean.fstRepLastCreatedTime = map
					.get(CLFindScheduleTable.fstRepLastCreatedTime);
			bean.fstRepInitialCreatedTime = map
					.get(CLFindScheduleTable.fstRepInitialCreatedTime);
			bean.fstRepDateOne = map.get(CLFindScheduleTable.fstRepDateOne);
			bean.fstRepDateTwo = map.get(CLFindScheduleTable.fstRepDateTwo);
			bean.fstRecommendName = map
					.get(CLFindScheduleTable.fstRecommendName);
			bean.fstWebUR = map.get(CLFindScheduleTable.fstWebURL);
			bean.fstImagePath = map.get(CLFindScheduleTable.fstImagePath);
			bean.fstParReamrk = map.get(CLFindScheduleTable.fstParReamrk);
			bean.fstCreateTime = map.get(CLFindScheduleTable.fstCreateTime);
			bean.fstUpdateTime = map.get(CLFindScheduleTable.fstUpdateTime);
			bean.fstReamrk1 = map.get(CLFindScheduleTable.fstReamrk1);
			bean.fstReamrk2 = map.get(CLFindScheduleTable.fstReamrk2);
			bean.fstReamrk3 = map.get(CLFindScheduleTable.fstReamrk3);
			bean.fstReamrk4 = map.get(CLFindScheduleTable.fstReamrk4);
			bean.fstReamrk5 = map.get(CLFindScheduleTable.fstReamrk5);
		}

		private void initview() {
			edit_tv = (TextView) view.findViewById(R.id.edit_tv);
			edit_tv.setOnClickListener(this);
			zhuanfa_tv = (TextView) view.findViewById(R.id.zhuanfa_tv);
			zhuanfa_tv.setOnClickListener(this);
			addmysch_tv = (TextView) view.findViewById(R.id.addmysch_tv);
			addmysch_tv.setOnClickListener(this);
			delete_tv = (TextView) view.findViewById(R.id.delete_tv);
			delete_tv.setOnClickListener(this);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			cancle_tv.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.edit_tv:
				intent = new Intent(context,
						EditNewFocusShareRepeatActivity.class);
				intent.putExtra("bean", bean);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.zhuanfa_tv:
				intent = new Intent(context,
						NewFriendRiChengZhuanFaActivity.NewFocusShareRiChengZhuanFaActivity.class);
				intent.putExtra("bean", bean);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.addmysch_tv:
				boolean fag = false;
				if (fag) {
					alertDialog(0);
				} else {
					alertDialog(1);
				}
				dialog.dismiss();
				break;
			case R.id.delete_tv:
				alertDeleteDialog(mMap);
				dialog.dismiss();
				break;
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	public class UpdateDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("data");
			System.out.println("重复的:"+result);
			onLoad();
			if (result.equals("success")) {
				Message message = Message.obtain();
				message.what = 1;
				handler1.sendMessage(message);
			} else {
				Message message = Message.obtain();
				message.what = 2;
				handler1.sendMessage(message);
			}

		}
	}

	private Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// int str = (Integer) msg.obj;
			switch (msg.what) {
			case 1:// 成功
				loadData();
				adapter.notifyDataSetChanged();
				break;
			case 2:// 失败
				loadData();
				adapter.notifyDataSetChanged();
				break;
			}
		}

	};

	/**
	 * 删除操作
	 */
	private void alertDeleteDialog(final Map<String, String> mMap) {
		final AlertDialog builder = new AlertDialog.Builder(context).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setAttributes(params);// 设置生效
		window.setContentView(R.layout.dialog_alterdelete);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
				int deleteID = Integer.parseInt(mMap
						.get(CLFindScheduleTable.fstRepeatId));
				if (deleteID <= 0) {
					app.deleteNewFocusShareData(1,
							NewFocusShareEditActivity.been.id, 0, deleteID, "");
				} else {
					app.deleteNewFocusShareData(9, NewFocusShareEditActivity.been.id, 0, deleteID, "");
				}
				app.deleteNewFocusShareData(2, NewFocusShareEditActivity.been.id, 0, deleteID, "");
				repeat_listview.hiddenRight();
				loadData();
				adapter.notifyDataSetChanged();
				isNetWork();
			}
		});
		TextView delete_canel = (TextView) window
				.findViewById(R.id.delete_canel);
		delete_canel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		delete_tv.setText("确定要删除此记事吗?");

	}

	/*
	 * 提示dialog
	 */
	private void alertDialog(int type) {
		final AlertDialog builder = new AlertDialog.Builder(context).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);// 设置生效
		window.setGravity(Gravity.CENTER);
		window.setContentView(R.layout.dialog_alert_ok);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		if (type == 0) {
			delete_tv.setText("加入成功！");
		} else if (type == 1) {
			delete_tv.setText("加入失败！");
		}
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	/**
	 * 增加新的重复时间对话框
	 */
	private void dialogAddRepeatOnClick() {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_newfocussharerepeat,
				null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		dialog.show();

		new MyGeneralAddRepeatOnClick(dialog, more_pop_menu);
	}

	class MyGeneralAddRepeatOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private TextView everyday_tv;
		private TextView workday_tv;
		private TextView everyweek_tv;
		private TextView everymonth_tv;
		private TextView everyyear_tv;
		private TextView cancle_tv;

		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();

		@SuppressLint("NewApi")
		public MyGeneralAddRepeatOnClick(Dialog dialog, View view) {
			this.dialog = dialog;
			this.view = view;
			initview();
		}

		private void initview() {
			everyday_tv = (TextView) view.findViewById(R.id.everyday_tv);
			everyday_tv.setOnClickListener(this);
			workday_tv = (TextView) view.findViewById(R.id.workday_tv);
			workday_tv.setOnClickListener(this);
			everyweek_tv = (TextView) view.findViewById(R.id.everyweek_tv);
			everyweek_tv.setOnClickListener(this);
			everymonth_tv = (TextView) view.findViewById(R.id.everymonth_tv);
			everymonth_tv.setOnClickListener(this);
			everyyear_tv = (TextView) view.findViewById(R.id.everyyear_tv);
			everyyear_tv.setOnClickListener(this);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			cancle_tv.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			case R.id.everyday_tv:
				intent = new Intent(context,
						AddNewFocusShareRepeatActivity.class);
				intent.putExtra("date", "每天");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.workday_tv:
				intent = new Intent(context,
						AddNewFocusShareRepeatActivity.class);
				intent.putExtra("date", "工作日");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everyweek_tv:
				intent = new Intent(context,
						AddNewFocusShareRepeatActivity.class);
				intent.putExtra("date", "每周");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everymonth_tv:
				intent = new Intent(context,
						AddNewFocusShareRepeatActivity.class);
				intent.putExtra("date", "每月");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everyyear_tv:
				intent = new Intent(context,
						AddNewFocusShareRepeatActivity.class);
				intent.putExtra("date", "每年");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		loadData();
		adapter.notifyDataSetChanged();
	}

	public void onEventMainThread(FristFragment event) {

		String msg = event.getMsg();
		if ("0".equals(msg)) {
			loadData();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
