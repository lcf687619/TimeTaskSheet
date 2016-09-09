package com.mission.schedule.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.activity.AddNewFocusShareRiChengActivity;
import com.mission.schedule.activity.EditNewFocusShareRiChengActivity;
import com.mission.schedule.activity.NewFocusShareEditActivity;
import com.mission.schedule.activity.NewFocusShareRepeatActivity;
import com.mission.schedule.activity.NewFriendRiChengZhuanFaActivity;
import com.mission.schedule.adapter.NewFocusShareRiChengAdapter;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewFocusShareBean;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.entity.CLFindScheduleTable;
import com.mission.schedule.service.NewFocusShareService;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.IXListViewListener;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;

import de.greenrobot.event.EventBus;

public class NewFocusShareRiChengFragment extends BaseFragment implements
		OnClickListener, IXListViewListener {

	private SwipeXListViewNoHead richeng_listview;
	private RelativeLayout bottom_rl;

	private boolean isShow;// 判断是否已经显示
	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	App app = null;

	String alltime;
	String ringdesc;
	String ringcode;
	NewFocusShareRiChengAdapter adapter = null;
	List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> todaylist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> tomorrowlist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> outtomorrowlist = new ArrayList<Map<String, String>>();
	int pinDaoID = 0;

	private UpdateDataReceiver receiver = null;
	public static List<Map<String, String>> focusSchList = new ArrayList<Map<String, String>>();

	@Override
	protected void lazyLoad() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_newfocussharericheng,
				container, false);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && !isShow) {
			isShow = true;
			init();
			loadData();
			setAdapter();
		}
	}

	private void init() {
		context = getActivity();
		View view = getView();
		EventBus.getDefault().register(this);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		receiver = new UpdateDataReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NewFocusShareService.SCHUPDATADATA);
		getActivity().registerReceiver(receiver, filter);
		alltime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ALLTIME, "08:58");
		ringdesc = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICDESC, "完成任务");
		ringcode = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICCODE, "g_88");

		richeng_listview = (SwipeXListViewNoHead) view
				.findViewById(R.id.richeng_listview);
		bottom_rl = (RelativeLayout) view.findViewById(R.id.bottom_rl);
		bottom_rl.setOnClickListener(this);

		View footView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		richeng_listview.addFooterView(footView);

		richeng_listview.setPullRefreshEnable(true);
		richeng_listview.setPullLoadEnable(true);
		richeng_listview.setXListViewListener(this);
		richeng_listview.setFocusable(true);

		if (NewFocusShareEditActivity.been != null) {
			pinDaoID = NewFocusShareEditActivity.been.id;
		}
	}

	private void loadData() {
		try {
			todaylist.clear();
			tomorrowlist.clear();
			outtomorrowlist.clear();
			mList.clear();
			todaylist = app.QueryNewFocusData(1, pinDaoID);
			tomorrowlist = app.QueryNewFocusData(2, pinDaoID);
			outtomorrowlist = app.QueryNewFocusData(3, pinDaoID);

			mList.addAll(todaylist);
			mList.addAll(tomorrowlist);
			mList.addAll(outtomorrowlist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setAdapter() {
		adapter = new NewFocusShareRiChengAdapter(context, mList,
				R.layout.adapter_newfocussharericheng, handler,
				richeng_listview, mScreenWidth);
		richeng_listview.setAdapter(adapter);
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
			intent.setPackage(getActivity().getPackageName());
			context.startService(intent);
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
		if (richeng_listview.getVisibility() == View.VISIBLE) {
			swipeXListView = richeng_listview;
		} else {
			// swipeXListView = myImpotent_listview;
		}
		swipeXListView.stopRefresh();
		swipeXListView.stopLoadMore();
		// swipeXListView.setRefreshTime("刚刚" + date);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bottom_rl:
			Intent intent = new Intent(context,
					AddNewFocusShareRiChengActivity.class);
			startActivityForResult(intent, 100);
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Map<String, String> mMap = (Map<String, String>) msg.obj;
			if (Integer.parseInt(mMap.get(CLFindScheduleTable.fstSchID)) < 0) {
				if (!"0".equals(mMap.get(CLFindScheduleTable.fstRepeatId))
						&& "1".equals(mMap
								.get(CLFindScheduleTable.fstRepeatLink))) {
					if (Integer.parseInt(mMap
							.get(CLFindScheduleTable.fstRepeatId)) < 0) {
						if (NewFocusShareRepeatActivity.focusRepList != null
								&& NewFocusShareRepeatActivity.focusRepList
										.size() > 0) {
							for (Map<String, String> map : NewFocusShareRepeatActivity.focusRepList) {
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
				} else {
					if (focusSchList != null && focusSchList.size() > 0) {
						for (Map<String, String> map : focusSchList) {
							Object[] indexStr = map.keySet().toArray();
							String index = indexStr[0].toString();
							if (Integer.parseInt(mMap
									.get(CLFindScheduleTable.fstSchID)) == Integer
									.parseInt(index)) {
								mMap.put(CLFindScheduleTable.fstSchID,
										map.get(index));
								break;
							}
						}
					}
				}
			}
			// 0 点击详情 1删除
			switch (msg.what) {
			case 0:
				dialogOnClick(mMap);
				break;
			case 1:
				alertDeleteDialog(mMap);
				break;
			default:
				break;
			}
		}
	};

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
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
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
						EditNewFocusShareRiChengActivity.class);
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
				boolean fag = app.insertScheduleData(bean.fstContent,
						bean.fstDate, bean.fstTime, bean.fstIsAlarm,
						bean.fstBeforeTime, bean.fstDisplayTime,
						bean.fstIsPostpone, bean.fstIsImportant,
						bean.fstColorType, 0,
						DateUtil.formatDateTime(new Date()), bean.fstTags,
						bean.fstSourceType, bean.fstSourceDesc,
						bean.fstSourceDescSpare, 0, "",
						DateUtil.formatDateTime(new Date()), 1, 0, 0,
						bean.fstRingDesc, bean.fstRingCode, "", 0, 0,
						bean.fstAType, bean.fstWebUR, bean.fstImagePath, 0, 0,
						0);
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
						.get(CLFindScheduleTable.fstSchID));
				if ("0".equals(mMap.get(CLFindScheduleTable.fstRepeatId))) {
					if (deleteID <= 0) {
						app.deleteNewFocusShareData(0,
								NewFocusShareEditActivity.been.id, deleteID, 0,
								"");
					} else {
						app.deleteNewFocusShareData(4,
								NewFocusShareEditActivity.been.id, deleteID, 0,
								"");
					}
				} else if (!"0".equals(mMap
						.get(CLFindScheduleTable.fstRepeatId))
						&& "1".equals(mMap
								.get(CLFindScheduleTable.fstRepeatLink))) {
					app.deleteNewFocusShareData(5,
							NewFocusShareEditActivity.been.id, deleteID,
							Integer.parseInt(mMap
									.get(CLFindScheduleTable.fstRepeatId)), "");
					RepeatBean bean = NewFocusShareEditActivity
							.getNextChildTime(mMap);
					if (bean.repNextCreatedTime.equals(mMap
							.get(CLFindScheduleTable.fstRepeatDate))) {
						app.updateNewFocusShareRepeatData(
								NewFocusShareEditActivity.been.id,
								Integer.parseInt(mMap
										.get(CLFindScheduleTable.fstRepeatId)),
								mMap.get(CLFindScheduleTable.fstRepDateOne),
								mMap.get(CLFindScheduleTable.fstRepeatDate),
								Integer.parseInt(mMap
										.get(CLFindScheduleTable.fstRepStateOne)),
								2);
					} else {
						app.updateNewFocusShareRepeatData(
								NewFocusShareEditActivity.been.id,
								Integer.parseInt(mMap
										.get(CLFindScheduleTable.fstRepeatId)),
								mMap.get(CLFindScheduleTable.fstRepeatDate),
								mMap.get(CLFindScheduleTable.fstRepDateTwo),
								2,
								Integer.parseInt(mMap
										.get(CLFindScheduleTable.fstRepStateOne)));
					}

				} else {
					if (deleteID <= 0) {
						app.deleteNewFocusShareData(0,
								NewFocusShareEditActivity.been.id, deleteID, 0,
								"");
					} else {
						app.deleteNewFocusShareData(4,
								NewFocusShareEditActivity.been.id, deleteID, 0,
								"");
					}
				}
				richeng_listview.hiddenRight();
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

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		loadData();
		adapter.notifyDataSetChanged();
	}

	public void onEventMainThread(FristFragment event) {

		String msg = event.getMsg();
		if ("1".equals(msg)) {
			loadData();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
		EventBus.getDefault().unregister(this);
		focusSchList.clear();
	}
}
