package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mission.schedule.R;
import com.mission.schedule.adapter.ComeRepeatTiXingAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.LocateAllNoticeTable;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.utils.DayComparator;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.YearDateComparator;
import com.mission.schedule.utils.YinLiYearDateComparator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ComeRepeatTiXingActivity extends BaseActivity {

	@ViewResId(id = R.id.chongfu_lv)
	private SwipeXListViewNoHead chongfu_lv;
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;

	Context context;
	App app = null;
	ComeRepeatTiXingAdapter adapter = null;

	List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everydayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> workdayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyweekList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everymonthList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyyearList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> nongliList = new ArrayList<Map<String, String>>();

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setReadState();
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	private void setReadState() {
		for (int i = 0; i < mList.size(); i++) {
			if("2".equals(mList.get(i).get(CLRepeatTable.repEndState))){
				app.deleteCLRepeatTableLocalData(mList.get(i).get(CLRepeatTable.repID));
				app.deleteChildSch(mList.get(i).get(CLRepeatTable.repID));
			}else{
				app.updateRepeateReadState(Integer.parseInt(mList.get(i).get(
					CLRepeatTable.repID)));
			}
		}
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_comerepeattixing);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		app = App.getDBcApplication();
		middle_tv.setText("重复提醒更新");
		top_ll_right.setVisibility(View.GONE);
		View footView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		chongfu_lv.addFooterView(footView);
		loadData();
	}

	public void loadData() {
		App application = App.getDBcApplication();
		everyyearList.clear();
		everymonthList.clear();
		everyweekList.clear();
		workdayList.clear();
		everydayList.clear();
		nongliList.clear();
		mList.clear();

		try {
			List<Map<String, String>> list = application
					.QueryAllChongFuData(0);
			if (list == null && list.size() == 0) {
				return;
			} else {
				for (int i = 0; i < list.size(); i++) {
					if ("1".equals(list.get(i).get(CLRepeatTable.repType))) {
						everydayList.add(list.get(i));
					} else if ("2".equals(list.get(i)
							.get(CLRepeatTable.repType))) {
						everyweekList.add(list.get(i));
						Collections.sort(everyweekList, new DayComparator());
					} else if ("3".equals(list.get(i)
							.get(CLRepeatTable.repType))) {
						everymonthList.add(list.get(i));
						Collections.sort(everymonthList, new DayComparator());
					} else if ("4".equals(list.get(i)
							.get(CLRepeatTable.repType))) {
						everyyearList.add(list.get(i));
						Collections.sort(everyyearList,
								new YearDateComparator());
					} else if ("6".equals(list.get(i)
							.get(CLRepeatTable.repType))) {
						nongliList.add(list.get(i));
						Collections.sort(nongliList,
								new YinLiYearDateComparator());
					} else {
						workdayList.add(list.get(i));
					}
				}
				mList.addAll(mList.size(), everydayList);
				mList.addAll(mList.size(), workdayList);
				mList.addAll(mList.size(), everyweekList);
				mList.addAll(mList.size(), everymonthList);
				mList.addAll(mList.size(), everyyearList);
				mList.addAll(mList.size(), nongliList);
				adapter = new ComeRepeatTiXingAdapter(context, mList,
						R.layout.adapter_comerepeatetixing, handler, chongfu_lv);
				chongfu_lv.setAdapter(adapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setAdapter() {

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Map<String, String> mMap = (Map<String, String>) msg.obj;
			int position = msg.arg1;
			switch (msg.what) {
			case 0:// 删除
				alertDeleteDialog(mMap, position);
				break;

			case 1:// 菜单详情
				if ("0".equals(mMap.get(CLRepeatTable.repIsPuase))) {
					dialogDetailOnClick(mMap, position);
					// isNetWork();
				} else {
					dialogPauseDetailOnClick(mMap, position);
					// isNetWork();
				}
				break;
			}
		}

	};

	private void alertDeleteDialog(final Map<String, String> mMap,
			final int position) {
		final AlertDialog builder = new AlertDialog.Builder(this).create();
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
				String deleteId = mMap.get(CLRepeatTable.repID);
				app.deleteCLRepeatTableLocalData(deleteId);
				app.deleteRep(Integer.parseInt(deleteId));
				chongfu_lv.hiddenRight();
				app.deleteChildSch(mMap.get(CLRepeatTable.repID));
				// loadData();
				// isNetWork();
				mList.remove(position);
				// refreshHomeCountListener.RefreshHomeCount(0);// 刷新未结束个数
				adapter.notifyDataSetChanged();
				chongfu_lv.invalidate();
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

	/**
	 * 未暂停的对话框
	 * 
	 * @param mMap
	 */
	private void dialogDetailOnClick(Map<String, String> mMap, int position) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_myrepeatdetail, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
		dialog.show();

		new MyGeneralDetailOnClick(dialog, mMap, more_pop_menu, position);
	}

	class MyGeneralDetailOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private Map<String, String> mMap;
		private TextView bianji_tv;
		private TextView pause_tv;
		private TextView important_tv;
		private TextView copytext_tv;
		private TextView zhuanfafriends_tv;
		private TextView delete_tv;
		private TextView cancle_tv;

		int position;
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();

		@SuppressLint("NewApi")
		public MyGeneralDetailOnClick(Dialog dialog, Map<String, String> mMap,
				View view, int position) {
			this.dialog = dialog;
			this.mMap = mMap;
			this.view = view;
			this.position = position;
			initview();
		}

		private void initview() {
			bianji_tv = (TextView) view.findViewById(R.id.bianji_tv);
			bianji_tv.setOnClickListener(this);
			pause_tv = (TextView) view.findViewById(R.id.pause_tv);
			pause_tv.setOnClickListener(this);
			important_tv = (TextView) view.findViewById(R.id.important_tv);
			important_tv.setOnClickListener(this);
			copytext_tv = (TextView) view.findViewById(R.id.copytext_tv);
			copytext_tv.setOnClickListener(this);
			zhuanfafriends_tv = (TextView) view
					.findViewById(R.id.zhuanfafriends_tv);
			zhuanfafriends_tv.setOnClickListener(this);
			delete_tv = (TextView) view.findViewById(R.id.delete_tv);
			delete_tv.setOnClickListener(this);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			cancle_tv.setOnClickListener(this);

			if ("0".equals(mMap.get(CLRepeatTable.repIsImportant))) {
				important_tv.setText("标记为重要");
			} else {
				important_tv.setText("取消重要");
			}

		}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			RepeatBean repeatBean = new RepeatBean();
			repeatBean.repID = mMap.get(CLRepeatTable.repID); // 重复记事ID
			repeatBean.repBeforeTime = mMap.get(CLRepeatTable.repBeforeTime); // 提前时间
			repeatBean.repColorType = mMap.get(CLRepeatTable.repColorType); // 分类：1工作
																			// |
																			// 2生活
																			// |
																			// 3其他
			repeatBean.repDisplayTime = mMap.get(CLRepeatTable.repDisplayTime); // 显示时间：0不显示
																				// |
																				// 1显示
			repeatBean.repType = mMap.get(CLRepeatTable.repType); // 重复类型 1.每天 |
																	// 2.每周 |
																	// 3.每月 |
																	// 4.每年 |
																	// 5.工作日
			repeatBean.repIsAlarm = mMap.get(CLRepeatTable.repIsAlarm); // 共4种：0
																		// 无闹钟 |
																		// 1
																		// 准时有闹钟
																		// 提前无闹钟
																		// | 2
																		// 准时无闹钟
																		// 提前有闹钟
																		// | 3
																		// 准时提前均有闹钟
			repeatBean.repIsPuase = mMap.get(CLRepeatTable.repIsPuase); // 暂停：0
																		// 未暂停 |
																		// 1 暂停
			repeatBean.repIsImportant = mMap.get(CLRepeatTable.repIsImportant); // 重要：0
																				// 未标记重要
																				// |
																				// 1
																				// 标记重要
			repeatBean.repSourceType = mMap.get(CLRepeatTable.repSourceType); // 0
																				// 普通
																				// |
																				// 1
																				// 全链接（发现）|
																				// 2
																				// ...
			repeatBean.repUpdateState = mMap.get(CLRepeatTable.repUpdateState); // 0
																				// 不需要上传
																				// |
																				// 1
																				// 新添加
																				// |
																				// 2
																				// 已更改
																				// |
																				// 3
																				// 已删除
			repeatBean.repOpenState = mMap.get(CLRepeatTable.repOpenState); // 公开状态
			repeatBean.repStateOne = mMap.get(CLRepeatTable.repStateOne); // 上一条子记事状态：0
																			// 普通
																			// |
																			// 1
																			// 脱钩
																			// |
																			// 2
																			// 删除
																			// |
																			// 3
																			// 结束
			repeatBean.repStateTwo = mMap.get(CLRepeatTable.repStateTwo); // 下一条子记事状态
			repeatBean.repcommendedUserId = mMap
					.get(CLRepeatTable.repcommendedUserId); // 来自某人：ID 默认为0

			/**
			 * 根据重复类型不同的参数 每天 每周 - 1、2、3...7 每月 - 1、2、3...31 每年 -
			 * 01-01、01-02、01-03...12-31
			 */
			repeatBean.repTypeParameter = mMap
					.get(CLRepeatTable.repTypeParameter);
			repeatBean.repStartDate = mMap.get(CLRepeatTable.repStartDate); // 重复起始日期
			repeatBean.repNextCreatedTime = mMap
					.get(CLRepeatTable.repNextCreatedTime); // 下一次已经生成子记事的时间 格式
															// - yyyy-mm-dd
															// hh:mm
			repeatBean.repLastCreatedTime = mMap
					.get(CLRepeatTable.repLastCreatedTime); // 上一次已经生成子记事的时间 格式
															// - yyyy-mm-dd
															// hh:mm 无则为 @“”
			repeatBean.repInitialCreatedTime = mMap
					.get(CLRepeatTable.repInitialCreatedTime); // 母记事创建的时间 格式 -
																// yyyy-mm-dd
																// hh:mm
			repeatBean.repContent = mMap.get(CLRepeatTable.repContent); // 内容
			repeatBean.repCreateTime = mMap.get(CLRepeatTable.repCreateTime); // 创建时间
			repeatBean.repSourceDesc = mMap.get(CLRepeatTable.repSourceDesc); // 链接
			repeatBean.repSourceDescSpare = mMap
					.get(CLRepeatTable.repSourceDescSpare); // 链接描述
			repeatBean.repTime = mMap.get(CLRepeatTable.repTime); // 时间
			repeatBean.repRingDesc = mMap.get(CLRepeatTable.repRingDesc); // 铃声文字
			repeatBean.repRingCode = mMap.get(CLRepeatTable.repRingCode); // 铃声文件名
			repeatBean.repUpdateTime = mMap.get(CLRepeatTable.repUpdateTime); // 更新时间
			repeatBean.repcommendedUserName = mMap
					.get(CLRepeatTable.repcommendedUserName); // 来自
			repeatBean.repDateOne = mMap.get(CLRepeatTable.repDateOne); // 上一条子记事标记时间
																		// 格式 -
																		// yyyy-MM-dd
																		// HH:mm
			repeatBean.repDateTwo = mMap.get(CLRepeatTable.repDateTwo);
			repeatBean.repInSTable = Integer.parseInt(mMap
					.get(CLRepeatTable.repInSTable));
			repeatBean.repcommendedUserId = mMap
					.get(CLRepeatTable.repcommendedUserId);
			repeatBean.repEndState = Integer.parseInt(mMap
					.get(CLRepeatTable.repEndState));
			repeatBean.parReamrk = mMap.get(CLRepeatTable.parReamrk);
			switch (v.getId()) {
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			case R.id.bianji_tv:
				intent = new Intent(context, EditRepeatActivity.class);
				intent.putExtra("repeatbean", repeatBean);
				startActivity(intent);
				dialog.dismiss();
				break;
			case R.id.pause_tv:
				updateCLRepeatTable(mMap, CLRepeatTable.repIsPuase,
						CLRepeatTable.repUpdateState);
				App.getDBcApplication().deleteChildSch(
						mMap.get(CLRepeatTable.repID));
				updateSchClock(mMap, LocateAllNoticeTable.isEnd);
				dialog.dismiss();
				break;
			case R.id.important_tv:
				updateCLRepeatTable(mMap, CLRepeatTable.repIsImportant,
						CLRepeatTable.repUpdateState);
				dialog.dismiss();
				break;
			case R.id.copytext_tv:
				copy(repeatBean.repContent, context);
				dialog.dismiss();
				break;
			case R.id.zhuanfafriends_tv:
				intent = new Intent(context, MyRepeatZhuanFaActivity.class);
				intent.putExtra("bean", repeatBean);
				startActivity(intent);
				dialog.dismiss();
				break;
			case R.id.delete_tv:
				alertDeleteDialog(mMap, position);
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 实现文本复制功能
	 * 
	 * @author lenovo
	 * @param content
	 */
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	/**
	 * 实现粘贴功能
	 * 
	 * @author lenovo
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

	/**
	 * 暂停的对话框
	 * 
	 * @param mMap
	 */
	private void dialogPauseDetailOnClick(Map<String, String> mMap, int position) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_myrepeat_pause, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
		dialog.show();

		new MyGeneralPauseDetailOnClick(dialog, mMap, more_pop_menu, position);
	}

	class MyGeneralPauseDetailOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private Map<String, String> mMap;
		private TextView start_tv;
		private TextView copytext_tv;
		private TextView zhuanfafriends_tv;
		private TextView cancle_tv;

		int position;
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();

		@SuppressLint("NewApi")
		public MyGeneralPauseDetailOnClick(Dialog dialog,
				Map<String, String> mMap, View view, int position) {
			this.dialog = dialog;
			this.mMap = mMap;
			this.view = view;
			this.position = position;
			initview();
		}

		private void initview() {
			start_tv = (TextView) view.findViewById(R.id.start_tv);
			start_tv.setOnClickListener(this);
			copytext_tv = (TextView) view.findViewById(R.id.copytext_tv);
			copytext_tv.setOnClickListener(this);
			zhuanfafriends_tv = (TextView) view
					.findViewById(R.id.zhuanfafriends_tv);
			zhuanfafriends_tv.setOnClickListener(this);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			cancle_tv.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			RepeatBean repeatBean = new RepeatBean();
			repeatBean.repID = mMap.get(CLRepeatTable.repID); // 重复记事ID
			repeatBean.repBeforeTime = mMap.get(CLRepeatTable.repBeforeTime); // 提前时间
			repeatBean.repColorType = mMap.get(CLRepeatTable.repColorType); // 分类：1工作
																			// |
																			// 2生活
																			// |
																			// 3其他
			repeatBean.repDisplayTime = mMap.get(CLRepeatTable.repDisplayTime); // 显示时间：0不显示
																				// |
																				// 1显示
			repeatBean.repType = mMap.get(CLRepeatTable.repType); // 重复类型 1.每天 |
																	// 2.每周 |
																	// 3.每月 |
																	// 4.每年 |
																	// 5.工作日
			repeatBean.repIsAlarm = mMap.get(CLRepeatTable.repIsAlarm); // 共4种：0
																		// 无闹钟 |
																		// 1
																		// 准时有闹钟
																		// 提前无闹钟
																		// | 2
																		// 准时无闹钟
																		// 提前有闹钟
																		// | 3
																		// 准时提前均有闹钟
			repeatBean.repIsPuase = mMap.get(CLRepeatTable.repIsPuase); // 暂停：0
																		// 未暂停 |
																		// 1 暂停
			repeatBean.repIsImportant = mMap.get(CLRepeatTable.repIsImportant); // 重要：0
																				// 未标记重要
																				// |
																				// 1
																				// 标记重要
			repeatBean.repSourceType = mMap.get(CLRepeatTable.repSourceType); // 0
																				// 普通
																				// |
																				// 1
																				// 全链接（发现）|
																				// 2
																				// ...
			repeatBean.repUpdateState = mMap.get(CLRepeatTable.repUpdateState); // 0
																				// 不需要上传
																				// |
																				// 1
																				// 新添加
																				// |
																				// 2
																				// 已更改
																				// |
																				// 3
																				// 已删除
			repeatBean.repOpenState = mMap.get(CLRepeatTable.repOpenState); // 公开状态
			repeatBean.repStateOne = mMap.get(CLRepeatTable.repStateOne); // 上一条子记事状态：0
																			// 普通
																			// |
																			// 1
																			// 脱钩
																			// |
																			// 2
																			// 删除
																			// |
																			// 3
																			// 结束
			repeatBean.repStateTwo = mMap.get(CLRepeatTable.repStateTwo); // 下一条子记事状态
			repeatBean.repcommendedUserId = mMap
					.get(CLRepeatTable.repcommendedUserId); // 来自某人：ID 默认为0

			/**
			 * 根据重复类型不同的参数 每天 每周 - 1、2、3...7 每月 - 1、2、3...31 每年 -
			 * 01-01、01-02、01-03...12-31
			 */
			repeatBean.repTypeParameter = mMap
					.get(CLRepeatTable.repTypeParameter);
			repeatBean.repStartDate = mMap.get(CLRepeatTable.repStartDate); // 重复起始日期
			repeatBean.repNextCreatedTime = mMap
					.get(CLRepeatTable.repNextCreatedTime); // 下一次已经生成子记事的时间 格式
															// - yyyy-mm-dd
															// hh:mm
			repeatBean.repLastCreatedTime = mMap
					.get(CLRepeatTable.repLastCreatedTime); // 上一次已经生成子记事的时间 格式
															// - yyyy-mm-dd
															// hh:mm 无则为 @“”
			repeatBean.repInitialCreatedTime = mMap
					.get(CLRepeatTable.repInitialCreatedTime); // 母记事创建的时间 格式 -
																// yyyy-mm-dd
																// hh:mm
			repeatBean.repContent = mMap.get(CLRepeatTable.repContent); // 内容
			repeatBean.repCreateTime = mMap.get(CLRepeatTable.repCreateTime); // 创建时间
			repeatBean.repSourceDesc = mMap.get(CLRepeatTable.repSourceDesc); // 链接
			repeatBean.repSourceDescSpare = mMap
					.get(CLRepeatTable.repSourceDescSpare); // 链接描述
			repeatBean.repTime = mMap.get(CLRepeatTable.repTime); // 时间
			repeatBean.repRingDesc = mMap.get(CLRepeatTable.repRingDesc); // 铃声文字
			repeatBean.repRingCode = mMap.get(CLRepeatTable.repRingCode); // 铃声文件名
			repeatBean.repUpdateTime = mMap.get(CLRepeatTable.repUpdateTime); // 更新时间
			repeatBean.repcommendedUserName = mMap
					.get(CLRepeatTable.repcommendedUserName); // 来自
			repeatBean.repDateOne = mMap.get(CLRepeatTable.repDateOne); // 上一条子记事标记时间
																		// 格式 -
																		// yyyy-MM-dd
																		// HH:mm
			repeatBean.repDateTwo = mMap.get(CLRepeatTable.repDateTwo);
			repeatBean.repEndState = Integer.parseInt(mMap
					.get(CLRepeatTable.repEndState));
			repeatBean.parReamrk = mMap.get(CLRepeatTable.parReamrk);
			switch (v.getId()) {
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			case R.id.start_tv:
				updateCLRepeatTable(mMap, CLRepeatTable.repIsPuase,
						CLRepeatTable.repUpdateState);
				RepeatBean bean;
				int recommendID = 0;
				if ("".equals(mMap.get(CLRepeatTable.repcommendedUserId))
						|| "null".equals(mMap
								.get(CLRepeatTable.repcommendedUserId))
						|| mMap.get(CLRepeatTable.repcommendedUserId) == null) {
					recommendID = 0;
				} else {
					recommendID = Integer.parseInt(mMap
							.get(CLRepeatTable.repcommendedUserId));
				}
				String[] dateselectData;
				if ("1".equals(mMap.get(CLRepeatTable.repType))) {
					bean = RepeatDateUtils.saveCalendar(
							mMap.get(CLRepeatTable.repTime), 1, "", "");
					if ("0".equals(mMap.get(CLRepeatTable.repInSTable))) {
						App.getDBcApplication()
								.insertScheduleData(
										repeatBean.repContent,
										bean.repNextCreatedTime
												.substring(0, 10),
										bean.repNextCreatedTime.substring(11,
												16),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsAlarm)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repBeforeTime)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repDisplayTime)),
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsImportant)),
										0,
										0,
										bean.repNextCreatedTime,
										"",
										0,
										"",
										"",
										Integer.parseInt(mMap
												.get(CLRepeatTable.repID)),
										bean.repNextCreatedTime,
										bean.repNextCreatedTime,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repOpenState)),
										1,
										mMap.get(CLRepeatTable.repRingDesc),
										mMap.get(CLRepeatTable.repRingCode),
										mMap.get(CLRepeatTable.repcommendedUserName),
										0,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repAType)),
										mMap.get(CLRepeatTable.repWebURL),
										mMap.get(CLRepeatTable.repImagePath),
										0, 0, recommendID);
					}
				} else if ("5".equals(mMap.get(CLRepeatTable.repType))) {
					bean = RepeatDateUtils.saveCalendar(
							mMap.get(CLRepeatTable.repTime), 5, "", "");
					if ("0".equals(mMap.get(CLRepeatTable.repInSTable))) {
						App.getDBcApplication()
								.insertScheduleData(
										repeatBean.repContent,
										bean.repNextCreatedTime
												.substring(0, 10),
										bean.repNextCreatedTime.substring(11,
												16),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsAlarm)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repBeforeTime)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repDisplayTime)),
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsImportant)),
										0,
										0,
										bean.repNextCreatedTime,
										"",
										0,
										"",
										"",
										Integer.parseInt(mMap
												.get(CLRepeatTable.repID)),
										bean.repNextCreatedTime,
										bean.repNextCreatedTime,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repOpenState)),
										1,
										mMap.get(CLRepeatTable.repRingDesc),
										mMap.get(CLRepeatTable.repRingCode),
										mMap.get(CLRepeatTable.repcommendedUserName),
										0,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repAType)),
										mMap.get(CLRepeatTable.repWebURL),
										mMap.get(CLRepeatTable.repImagePath),
										0, 0, recommendID);
					}
				} else if ("2".equals(mMap.get(CLRepeatTable.repType))) {
					bean = RepeatDateUtils.saveCalendar(
							mMap.get(CLRepeatTable.repTime),
							2,
							mMap.get(CLRepeatTable.repTypeParameter)
									.replace("[", "").replace("]", "")
									.replace("\"", ""), "");
					if ("0".equals(mMap.get(CLRepeatTable.repInSTable))) {
						App.getDBcApplication()
								.insertScheduleData(
										repeatBean.repContent,
										bean.repNextCreatedTime
												.substring(0, 10),
										bean.repNextCreatedTime.substring(11,
												16),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsAlarm)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repBeforeTime)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repDisplayTime)),
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsImportant)),
										0,
										0,
										bean.repNextCreatedTime,
										"",
										0,
										"",
										"",
										Integer.parseInt(mMap
												.get(CLRepeatTable.repID)),
										bean.repNextCreatedTime,
										bean.repNextCreatedTime,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repOpenState)),
										1,
										mMap.get(CLRepeatTable.repRingDesc),
										mMap.get(CLRepeatTable.repRingCode),
										mMap.get(CLRepeatTable.repcommendedUserName),
										0,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repAType)),
										mMap.get(CLRepeatTable.repWebURL),
										mMap.get(CLRepeatTable.repImagePath),
										0, 0, recommendID);
					}
				} else if ("3".equals(mMap.get(CLRepeatTable.repType))) {
					bean = RepeatDateUtils.saveCalendar(
							mMap.get(CLRepeatTable.repTime),
							3,
							mMap.get(CLRepeatTable.repTypeParameter)
									.replace("[", "").replace("]", "")
									.replace("\"", ""), "");
					if ("0".equals(mMap.get(CLRepeatTable.repInSTable))) {
						App.getDBcApplication()
								.insertScheduleData(
										repeatBean.repContent,
										bean.repNextCreatedTime
												.substring(0, 10),
										bean.repNextCreatedTime.substring(11,
												16),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsAlarm)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repBeforeTime)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repDisplayTime)),
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsImportant)),
										0,
										0,
										bean.repNextCreatedTime,
										"",
										0,
										"",
										"",
										Integer.parseInt(mMap
												.get(CLRepeatTable.repID)),
										bean.repNextCreatedTime,
										bean.repNextCreatedTime,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repOpenState)),
										1,
										mMap.get(CLRepeatTable.repRingDesc),
										mMap.get(CLRepeatTable.repRingCode),
										mMap.get(CLRepeatTable.repcommendedUserName),
										0,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repAType)),
										mMap.get(CLRepeatTable.repWebURL),
										mMap.get(CLRepeatTable.repImagePath),
										0, 0, recommendID);
					}
				} else if ("4".equals(mMap.get(CLRepeatTable.repType))) {
					bean = RepeatDateUtils.saveCalendar(
							mMap.get(CLRepeatTable.repTime),
							4,
							mMap.get(CLRepeatTable.repTypeParameter)
									.replace("[", "").replace("]", "")
									.replace("\"", ""), "0");
					if ("0".equals(mMap.get(CLRepeatTable.repInSTable))) {
						App.getDBcApplication()
								.insertScheduleData(
										repeatBean.repContent,
										bean.repNextCreatedTime
												.substring(0, 10),
										bean.repNextCreatedTime.substring(11,
												16),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsAlarm)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repBeforeTime)),
										Integer.parseInt(mMap
												.get(CLRepeatTable.repDisplayTime)),
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repIsImportant)),
										0,
										0,
										bean.repNextCreatedTime,
										"",
										0,
										"",
										"",
										Integer.parseInt(mMap
												.get(CLRepeatTable.repID)),
										bean.repNextCreatedTime,
										bean.repNextCreatedTime,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repOpenState)),
										1,
										mMap.get(CLRepeatTable.repRingDesc),
										mMap.get(CLRepeatTable.repRingCode),
										mMap.get(CLRepeatTable.repcommendedUserName),
										0,
										0,
										Integer.parseInt(mMap
												.get(CLRepeatTable.repAType)),
										mMap.get(CLRepeatTable.repWebURL),
										mMap.get(CLRepeatTable.repImagePath),
										0, 0, recommendID);
					} else {
						bean = RepeatDateUtils.saveCalendar(
								mMap.get(CLRepeatTable.repTime), 4,
								mMap.get(CLRepeatTable.repTypeParameter)
										.replace("[", "").replace("]", "")
										.replace("\"", ""), "1");
						if ("0".equals(mMap.get(CLRepeatTable.repInSTable))) {
							App.getDBcApplication()
									.insertScheduleData(
											repeatBean.repContent,
											bean.repNextCreatedTime.substring(
													0, 10),
											bean.repNextCreatedTime.substring(
													11, 16),
											Integer.parseInt(mMap
													.get(CLRepeatTable.repIsAlarm)),
											Integer.parseInt(mMap
													.get(CLRepeatTable.repBeforeTime)),
											Integer.parseInt(mMap
													.get(CLRepeatTable.repDisplayTime)),
											0,
											Integer.parseInt(mMap
													.get(CLRepeatTable.repIsImportant)),
											0,
											0,
											bean.repNextCreatedTime,
											"",
											0,
											"",
											"",
											Integer.parseInt(mMap
													.get(CLRepeatTable.repID)),
											bean.repNextCreatedTime,
											bean.repNextCreatedTime,
											0,
											Integer.parseInt(mMap
													.get(CLRepeatTable.repOpenState)),
											1,
											mMap.get(CLRepeatTable.repRingDesc),
											mMap.get(CLRepeatTable.repRingCode),
											mMap.get(CLRepeatTable.repcommendedUserName),
											0,
											0,
											Integer.parseInt(mMap
													.get(CLRepeatTable.repAType)),
											mMap.get(CLRepeatTable.repWebURL),
											mMap.get(CLRepeatTable.repImagePath),
											0, 0, recommendID);
						}
					}
				}
				updateSchClock(mMap, LocateAllNoticeTable.isEnd);
				dialog.dismiss();
				break;
			case R.id.copytext_tv:
				copy(repeatBean.repContent, context);
				dialog.dismiss();
				break;
			case R.id.zhuanfafriends_tv:
				Intent intent = new Intent(context,
						MyRepeatZhuanFaActivity.class);
				intent.putExtra("bean", repeatBean);
				startActivity(intent);
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	// 设置为暂停
	private void updateCLRepeatTable(Map<String, String> mMap, String key,
			String key1) {
		String value = "0";
		Map<String, String> upMap = new HashMap<String, String>();
		chongfu_lv.hiddenRight();
		if ("0".equals(mMap.get(key)))
			value = "1";
		else
			value = "0";
		upMap.put(key, value);
		upMap.put(key1, "2");
		App.getDBcApplication().updateCLRepeatTableData(upMap,
				"where repID=" + mMap.get(CLRepeatTable.repID));
		mMap.put(key, value);
		// everyyearList.clear();
		// everymonthList.clear();
		// everyweekList.clear();
		// workdayList.clear();
		// everydayList.clear();
		// mList.clear();
		// loadData();
		// isNetWork();
		adapter.notifyDataSetChanged();
	}

	private void updateSchClock(Map<String, String> mMap, String key) {
		try {
			String value = "0";
			String key1 = "";
			Map<String, String> upMap = new HashMap<String, String>();
			if (key.equals("isEnd")) {
				key1 = "repIsPuase";
			}
			if ("1".equals(mMap.get(key1)))
				value = "1";
			else
				value = "0";
			upMap.put(key, value);
			App.getDBcApplication().updateSchIsEnd(upMap,
					"where repID=" + mMap.get("repID"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 竖屏
			setReadState();
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}
