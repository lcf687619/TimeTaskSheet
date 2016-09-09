package com.mission.schedule.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.mission.schedule.R;
import com.mission.schedule.activity.AddRepeatActivity;
import com.mission.schedule.activity.ComeRepeatTiXingActivity;
import com.mission.schedule.activity.EditBirthRepeatActivity;
import com.mission.schedule.activity.EditRepeatActivity;
import com.mission.schedule.activity.MainActivity;
import com.mission.schedule.activity.MyRepeatZhuanFaActivity;
import com.mission.schedule.activity.RepeatDaoQiDateActivity;
import com.mission.schedule.activity.RepeatFinishWebViewActivity;
import com.mission.schedule.activity.RepeatFriendsRiChengActivity;
import com.mission.schedule.activity.RepeatTuiJianMoreActivity;
import com.mission.schedule.adapter.MyRepeatAdapter;
import com.mission.schedule.adapter.RepeatTuiJianAdapter;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.bean.RepeatTuiJianBackBean;
import com.mission.schedule.bean.RepeatTuiJianItemBean;
import com.mission.schedule.bean.TotalFriendsCountBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.PostSendMainActivity;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLCategoryTable;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.LocateAllNoticeTable;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.swipexlistview.RepeatSwipeXListView;
import com.mission.schedule.utils.AfterPermissionGranted;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.DayComparator;
import com.mission.schedule.utils.EasyPermissions;
import com.mission.schedule.utils.JsonParser;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ReadTextContentXml.ReadWeiXinXml;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.RepeatGridView;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.Utils;
import com.mission.schedule.utils.YearDateComparator;
import com.mission.schedule.utils.YinLiYearDateComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class MyRepeatFragment extends BaseFragment implements
		View.OnClickListener,EasyPermissions.PermissionCallbacks {

	private RepeatSwipeXListView myrepeat_listview;
	public MyRepeatAdapter adapter = null;
	private boolean isShow = false;// 判断是否已经显示
	private View headView;
	private LinearLayout up_down;
	private TextView up_show;
	private TextView enddayweb_tv;
	public static LinearLayout top_ll_left;
	private RelativeLayout top_ll_right;

	// *****************************顶部布局*****************************************//
	 RepeatGridView tuijian_gv;
	int count;

	List<RepeatTuiJianItemBean> tuijianList = new ArrayList<RepeatTuiJianItemBean>();
	List<RepeatTuiJianItemBean> tuijians = new ArrayList<RepeatTuiJianItemBean>();
	// ===================================================================================//
	List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everydayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> workdayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyweekList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everymonthList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyyearList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> nongliList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> comelist = new ArrayList<Map<String, String>>();

	// TextView tv_schedule_count;
	// TextView tv_my_count;
	SharedPrefUtil sharedPrefUtil = null;
	String userid;
	Context context;
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private SharedPreferences mSharedPreferences;

	RelativeLayout tixing_rl;
	TextView count_tv;
	App application = null;
	/**
	 * 登录广播接收器
	 * */
	private UpdateDataReceiver receiver = null;
	boolean autoFag = false;
	private static final int RC_LOCATION_CONTACTS_PERM = 124;
	String permissionState = "0";

	// int scrolledX = 0;
	// int scrolledY = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_myrepeat, container, false);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && !isShow) {
			isShow = true;
			init();
			TuiJianData();
			loadData();
			setDataAdapter();
		}
	}

	private void init() {
		context = getActivity();
		EventBus.getDefault().register(this);
		receiver = new UpdateDataReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UpLoadService.RepUPDATADATA);
		getActivity().registerReceiver(receiver, filter);
		application = App.getDBcApplication();
		View view = getView();
		sharedPrefUtil = new SharedPrefUtil(getActivity(), ShareFile.USERFILE);
		userid = sharedPrefUtil.getString(getActivity(), ShareFile.USERFILE,
				ShareFile.USERID, "");
		headView = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_myrepeat_headview, null);
		up_down = (LinearLayout) headView.findViewById(R.id.up_down);
		up_show = (TextView) headView.findViewById(R.id.up_show);
		enddayweb_tv = (TextView) headView.findViewById(R.id.enddayweb_tv);
		enddayweb_tv.setOnClickListener(this);
		myrepeat_listview = (RepeatSwipeXListView) view
				.findViewById(R.id.myrepeat_listview);
		myrepeat_listview.setUpDown(up_down);
		myrepeat_listview.setUpshow(up_show);
		myrepeat_listview.setPullLoadEnable(true);
		myrepeat_listview.addHeaderView(headView);
		headView.setPadding(0, 0, 0, Utils.dipTopx(getActivity(), 10));
		top_ll_left = null;
		top_ll_left = (LinearLayout) view.findViewById(R.id.top_ll_left);
		top_ll_right = (RelativeLayout) view.findViewById(R.id.top_ll_right);
		top_ll_right.setOnClickListener(this);
		tixing_rl = (RelativeLayout) view.findViewById(R.id.tixing_rl);
		tixing_rl.setOnClickListener(this);
		count_tv = (TextView) view.findViewById(R.id.count_tv);
		/******************************* 顶部布局 ************************************************/
		// tuijian_gv = (RepeatGridView) view.findViewById(R.id.tuijian_gv);
		 tuijian_gv = RepeatSwipeXListView.gridView;

		// 初始化识别无UI识别对象
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);

		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
		// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
		mIatDialog = new RecognizerDialog(getActivity(), mInitListener);
		mSharedPreferences = getActivity().getSharedPreferences(
				"com.iflytek.setting", Activity.MODE_PRIVATE);
		permissionState = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "0");
		if("0".equals(permissionState)){
			checkPhonePermission();
		}
	}

	private void TuiJianData() {
		if (NetUtil.getConnectState(getActivity()) != NetWorkState.NONE) {
			String path = URLConstants.重复推荐;
			TuiJianAsync(path);
		} else {
			count = 0;
			RepeatTuiJianItemBean bean = new RepeatTuiJianItemBean();
			bean.uid = "0";
			bean.indexOrder = "0";
			bean.uNickName = "更多";
			bean.url = "";
			tuijians.add(bean);
			RepeatSwipeXListView.gridView.setAdapter(new RepeatTuiJianAdapter(
					getActivity(), tuijians, count, handler2));
		}
	}

	public void loadData() {
		everyyearList.clear();
		everymonthList.clear();
		everyweekList.clear();
		workdayList.clear();
		everydayList.clear();
		nongliList.clear();
		mList.clear();
		comelist.clear();
		try {
			comelist = application.QueryAllChongFuData(0);
			if (comelist != null && comelist.size() > 0) {
				count_tv.setText(comelist.size() + "");
				tixing_rl.setVisibility(View.VISIBLE);
			} else {
				tixing_rl.setVisibility(View.GONE);
			}
			List<Map<String, String>> list = application.QueryAllChongFuData(1);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setDataAdapter() {
		adapter = new MyRepeatAdapter(getActivity(), mList, R.layout.adapter_myrepeat,handler,
				myrepeat_listview);
		myrepeat_listview.setAdapter(adapter);
	}

	private void isNetWork() {
		if (NetUtil.getConnectState(getActivity()) != NetWorkState.NONE) {
			Intent intent = new Intent(getActivity(), UpLoadService.class);
			intent.setAction(Const.SHUAXINDATA);
			intent.setPackage("com.mission.schedule");
			getActivity().startService(intent);
		} else {
			return;
		}
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
	private Handler handler2 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			RepeatTuiJianItemBean itemBean = (RepeatTuiJianItemBean) msg.obj;
			int position = msg.arg1;
			Intent intent = new Intent();
			if (count >= 8) {
				if (position <= 6) {
					intent.setClass(getActivity(),
							RepeatFriendsRiChengActivity.class);
					intent.putExtra("uid", itemBean.uid);
					intent.putExtra("name", itemBean.uNickName);
					startActivityForResult(intent, 100);
				} else {
					intent.setClass(getActivity(),
							RepeatTuiJianMoreActivity.class);
					startActivityForResult(intent, 100);
				}
			} else if (count == 0) {
				intent.setClass(getActivity(), RepeatTuiJianMoreActivity.class);
				startActivityForResult(intent, 100);
			} else {
				if (position <= 6) {
					intent.setClass(getActivity(),
							RepeatFriendsRiChengActivity.class);
					intent.putExtra("uid", itemBean.uid);
					intent.putExtra("name", itemBean.uNickName);
					startActivityForResult(intent, 100);
				} else {
					intent.setClass(getActivity(),
							RepeatTuiJianMoreActivity.class);
					startActivityForResult(intent, 100);
				}
			}
		}

	};

	private void alertDeleteDialog(final Map<String, String> mMap,
			final int position) {
		final AlertDialog builder = new AlertDialog.Builder(getActivity())
				.create();
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
				// if (Integer.parseInt(mMap.get(CLRepeatTable.repID)) < 0) {
				// for (int i = 0; i < repIDList.size(); i++) {
				// Object[] indexStr = repIDList.get(i).keySet().toArray();
				// String index = indexStr[0].toString();
				// if (Integer.parseInt(mMap.get(CLRepeatTable.repID)) ==
				// Integer
				// .parseInt(index)) {
				// mMap.put(CLRepeatTable.repID,
				// repIDList.get(i).get(index));
				// }
				// break;
				// }
				// }
				String deleteId = mMap.get(CLRepeatTable.repID);
				App dbContextExtended = App.getDBcApplication();
				dbContextExtended.deleteCLRepeatTableLocalData(deleteId);
				dbContextExtended.deleteRep(Integer.parseInt(deleteId));
				myrepeat_listview.hiddenRight();
				dbContextExtended.deleteChildSch(mMap.get(CLRepeatTable.repID));
				 loadData();
				// isNetWork();
				// refreshHomeCountListener.RefreshHomeCount(0);// 刷新未结束个数
				adapter.notifyDataSetChanged();
				myrepeat_listview.invalidate();
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

	/**
	 * 未暂停的对话框
	 * 
	 * @param mMap
	 */
	private void dialogDetailOnClick(Map<String, String> mMap, int position) {
		Context context = getActivity();
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
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() - 30;
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
			// if (Integer.parseInt(mMap.get(CLRepeatTable.repID)) < 0) {
			// for (int i = 0; i < repIDList.size(); i++) {
			// Object[] indexStr = repIDList.get(i).keySet().toArray();
			// String index = indexStr[0].toString();
			// if (Integer.parseInt(mMap.get(CLRepeatTable.repID)) == Integer
			// .parseInt(index)) {
			// mMap.put(CLRepeatTable.repID,
			// repIDList.get(i).get(index));
			// }
			// break;
			// }
			// }
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
				intent = new Intent(getActivity(), EditRepeatActivity.class);
				intent.putExtra("repeatbean", repeatBean);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.pause_tv:
				updateCLRepeatTable(mMap, CLRepeatTable.repIsPuase,
						CLRepeatTable.repUpdateState);
				application.deleteChildSch(mMap.get(CLRepeatTable.repID));
				updateSchClock(mMap, LocateAllNoticeTable.isEnd);
				isNetWork();
				dialog.dismiss();
				break;
			case R.id.important_tv:
				updateCLRepeatTable(mMap, CLRepeatTable.repIsImportant,
						CLRepeatTable.repUpdateState);
				if ("1".equals(repeatBean.repIsImportant)) {
					application.updateChildSchState(
							Integer.parseInt(repeatBean.repID), 0);
				} else {
					application.updateChildSchState(
							Integer.parseInt(repeatBean.repID), 1);
				}
				isNetWork();
				dialog.dismiss();
				break;
			case R.id.copytext_tv://完成率
				intent = new Intent(getActivity(), RepeatFinishWebViewActivity.class);
				intent.putExtra("repeatID",Integer.parseInt(repeatBean.repID));
				startActivity(intent);
//				MainActivity.copy(repeatBean.repContent, getActivity());
				dialog.dismiss();
				break;
			case R.id.zhuanfafriends_tv:
				intent = new Intent(getActivity(),
						MyRepeatZhuanFaActivity.class);
				intent.putExtra("bean", repeatBean);
				startActivityForResult(intent, 100);
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
	 * 暂停的对话框
	 * 
	 * @param mMap
	 */
	private void dialogPauseDetailOnClick(Map<String, String> mMap, int position) {
		Context context = getActivity();
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
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() - 30;
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
			// if (Integer.parseInt(mMap.get(CLRepeatTable.repID)) < 0) {
			// for (int i = 0; i < repIDList.size(); i++) {
			// Object[] indexStr = repIDList.get(i).keySet().toArray();
			// String index = indexStr[0].toString();
			// if (Integer.parseInt(mMap.get(CLRepeatTable.repID)) == Integer
			// .parseInt(index)) {
			// mMap.put(CLRepeatTable.repID,
			// repIDList.get(i).get(index));
			// }
			// break;
			// }
			// }
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
				application.updateSchCLRepeatData(
						Integer.parseInt(mMap.get(CLRepeatTable.repID)), "",
						"", 0, 0);
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
				isNetWork();
				dialog.dismiss();
				break;
			case R.id.copytext_tv:
				MainActivity.copy(repeatBean.repContent, getActivity());
				dialog.dismiss();
				break;
			case R.id.zhuanfafriends_tv:
				Intent intent = new Intent(getActivity(),
						MyRepeatZhuanFaActivity.class);
				intent.putExtra("bean", repeatBean);
				startActivityForResult(intent, 100);
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
		myrepeat_listview.hiddenRight();
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

	/**
	 * 增加新的重复时间对话框
	 */
	private void dialogAddRepeatOnClick() {
		Context context = getActivity();
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(
				R.layout.dialog_myrepeatfragment_addrepeat, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() - 30;
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
		private TextView birth_tv;
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
			birth_tv = (TextView) view.findViewById(R.id.birth_tv);
			birth_tv.setOnClickListener(this);
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
				intent = new Intent(getActivity(), AddRepeatActivity.class);
				intent.putExtra("date", "每天");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.workday_tv:
				intent = new Intent(getActivity(), AddRepeatActivity.class);
				intent.putExtra("date", "工作日");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everyweek_tv:
				intent = new Intent(getActivity(), AddRepeatActivity.class);
				intent.putExtra("date", "每周");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everymonth_tv:
				intent = new Intent(getActivity(), AddRepeatActivity.class);
				intent.putExtra("date", "每月");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everyyear_tv:
				intent = new Intent(getActivity(), AddRepeatActivity.class);
				intent.putExtra("date", "每年");
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.birth_tv:
				checkPhonePermission();
				if(autoFag) {
					HuaTongDialog();
				}else{
					Toast.makeText(context,"权限已禁止访问!",Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_right:
			dialogAddRepeatOnClick();
			break;
		case R.id.enddayweb_tv:
			startActivityForResult(new Intent(getActivity(),
					RepeatDaoQiDateActivity.class), 100);
			break;
		case R.id.tixing_rl:
			startActivityForResult(new Intent(context,
					ComeRepeatTiXingActivity.class), 100);
			tixing_rl.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	private void TuiJianAsync(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							try {
								RepeatTuiJianBackBean backbean = gson.fromJson(
										result, RepeatTuiJianBackBean.class);
								if (backbean.status == 0) {
									tuijianList = backbean.page.items;
									if (tuijianList.size() >= 8) {
										for (int i = 0; i < 8; i++) {
											tuijians.add(tuijianList.get(i));
										}
									} else {
										for (int i = 0; i < tuijianList.size(); i++) {
											tuijians.add(tuijianList.get(i));
										}
									}
									count = tuijians.size();
									if(tuijian_gv!=null){
										tuijian_gv.setAdapter(new RepeatTuiJianAdapter(
												getActivity(), tuijians,
												count, handler2));
									}
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void item() {
		RepeatSwipeXListView.gridView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.v("TAG", "position的位置:" + position);
						Intent intent = new Intent();
						if (count >= 8) {
							if (position <= 6) {
								intent.setClass(getActivity(),
										RepeatFriendsRiChengActivity.class);
								intent.putExtra("uid",
										tuijianList.get(position).uid);
								intent.putExtra("name",
										tuijianList.get(position).uNickName);
								startActivity(intent);
							} else {
								intent.setClass(getActivity(),
										RepeatTuiJianMoreActivity.class);
								startActivity(intent);
							}
						} else if (count == 0) {
							intent.setClass(getActivity(),
									RepeatTuiJianMoreActivity.class);
							startActivity(intent);
						} else {
							if (position <= 6) {
								intent.setClass(getActivity(),
										RepeatFriendsRiChengActivity.class);
								intent.putExtra("uid",
										tuijianList.get(position).uid);
								intent.putExtra("name",
										tuijianList.get(position).uNickName);
								startActivity(intent);
							} else {
								intent.setClass(getActivity(),
										RepeatTuiJianMoreActivity.class);
								startActivity(intent);
							}
						}
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
		// isNetWork();
		// myrepeat_listview = (RepeatSwipeXListView) getView().findViewById(
		// R.id.myrepeat_listview);
		// loadData();
		// adapter = new MyRepeatAdapter(getActivity(), mList, handler,
		// myrepeat_listview);
		// myrepeat_listview.setAdapter(adapter);
		// adapter.notifyDataSetChanged();
		// sharedPrefUtil = new SharedPrefUtil(getActivity(),
		// ShareFile.USERFILE);
		// userid = sharedPrefUtil.getString(getActivity(), ShareFile.USERFILE,
		// ShareFile.USERID, "");
		// loadCount();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EasyPermissions.SETTINGS_REQ_CODE) {
			boolean hasReadSmsPermission = EasyPermissions.hasPermissions(getContext(),
					Manifest.permission.RECORD_AUDIO);
			autoFag = hasReadSmsPermission;
		}else {
			loadData();
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
			// myrepeat_listview.setSelectionFromTop(scrolledX, scrolledY);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loadCount() {
		// tv_schedule_count = MainActivity.tv_schedule_count;
//		int noEndCount = App.getDBcApplication().QueryNowGuoQiWeiJieShuCount();// Integer.parseInt(mainMap.get("noEndCount"));
		// if (noEndCount == 0) {
		// tv_schedule_count.setVisibility(View.GONE);
		// } else {
		// tv_schedule_count.setText(noEndCount + "");
		// tv_schedule_count.setVisibility(View.VISIBLE);
		// }
//		EventBus.getDefault().post(new PostSendMainActivity(1, noEndCount));
		// 好友统计数量
		String friendsCountPath = URLConstants.统计好友操作数量 + "?uId=" + userid;
		FriendsTotalAsync(friendsCountPath);
		// tv_my_count = MainActivity.tv_my_count;
	}

	/**
	 * 统计好友申请，被申请总数量
	 */
	private void FriendsTotalAsync(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								TotalFriendsCountBean countBean = gson
										.fromJson(result,
												TotalFriendsCountBean.class);
								if (countBean.status == 0) {
									EventBus.getDefault().post(
											new PostSendMainActivity(2,
													countBean.bsqCount));
									// if (countBean.bsqCount == 0) {
									// tv_my_count.setVisibility(View.GONE);
									// } else {
									// tv_my_count.setVisibility(View.VISIBLE);
									// tv_my_count.setText(countBean.bsqCount
									// + "");
									// }
								} else {
									// tv_my_count.setVisibility(View.GONE);
									EventBus.getDefault().post(
											new PostSendMainActivity(2, 0));
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
							// tv_my_count.setVisibility(View.GONE);
							EventBus.getDefault().post(
									new PostSendMainActivity(2, 0));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	public class UpdateDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 做一些修改界面之类的工作
			String result = intent.getStringExtra("data");
			if (result.equals("success")) {
				Message message = Message.obtain();
				message.what = -1;
				handler1.sendMessage(message);
			} else {
				Message message = Message.obtain();
				message.what = -2;
				handler1.sendMessage(message);
			}

		}
	}

	private Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg==null){
				return;
			}
			// int str = (Integer) msg.obj;
			switch (msg.what) {
			case -1:
				mList.clear();
				loadData();
				adapter.notifyDataSetChanged();
				break;
			case -2:
				// mList.clear();
				// loadData();
				// adapter.notifyDataSetChanged();
				break;
			}
		}

	};

	private void upLoad() {
		String path = URLConstants.好友关注重复生成
				+ sharedPrefUtil.getString(context, ShareFile.USERFILE,
						ShareFile.USERID, "0") + "&createRep=0";
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
		App.getHttpQueues().cancelAll("down");
		handler.removeCallbacksAndMessages(null);
		handler1.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	/**
	 * 话筒对话框
	 */
	Dialog huatongdialog = null;
	// private GestureDetector mGestureDetector;
	Button yuyin;

	private void HuaTongDialog() {
		huatongdialog = new Dialog(getActivity(), R.style.dialog_huatong);
		Window window = huatongdialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(getActivity());
		View more_pop_menu = fac.inflate(R.layout.dialog_yuyinbirth, null);
		yuyin = (Button) more_pop_menu.findViewById(R.id.yuyin);
		LinearLayout yuyin_ll = (LinearLayout) more_pop_menu
				.findViewById(R.id.yuyin_ll);
		huatongdialog.setCanceledOnTouchOutside(true);
		huatongdialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
		// yuyin.setOnLongClickListener(new PicOnLongClick());
		// mGestureDetector = new GestureDetector(getActivity(),
		// new MyOnGestureListener());
		yuyin_ll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					yuyin.setBackgroundDrawable(getActivity().getResources()
							.getDrawable(R.mipmap.btn_yuyina));
					xunfeiRecognizer();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					yuyin.setBackgroundDrawable(getActivity().getResources()
							.getDrawable(R.mipmap.btn_yuyinb));
					mIat.stopListening();
					huatongdialog.dismiss();
				}
				// mGestureDetector.onTouchEvent(event);
				return true;
			}

		});
		yuyin.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					yuyin.setBackgroundDrawable(getActivity().getResources()
							.getDrawable(R.mipmap.btn_yuyina));
					if (NetUtil.getConnectState(getActivity()) != NetWorkState.NONE) {
						xunfeiRecognizer();
					} else {
						alertFailDialog();
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					yuyin.setBackgroundDrawable(getActivity().getResources()
							.getDrawable(R.mipmap.btn_yuyinb));
					mIat.stopListening();
					huatongdialog.dismiss();
				}
				// mGestureDetector.onTouchEvent(event);
				return true;
			}

		});
		huatongdialog.show();
	}

	/**
	 * 讯飞语音
	 */
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d("TAG", "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				Toast.makeText(getActivity(), "初始化失败，错误码：" + code,
						Toast.LENGTH_LONG).show();
			}
		}
	};

	String mycontent = "";

	private void xunfeiRecognizer() {
		int ret = 0; // 函数调用返回值
		// 设置参数
		setParam();
		boolean isShowDialog = mSharedPreferences.getBoolean("iat_show", false);
		if (isShowDialog) {
			// 显示听写对话框
			mIatDialog.setListener(mRecognizerDialogListener);
			mIatDialog.show();
			Toast.makeText(getActivity(), "请开始说话…", Toast.LENGTH_SHORT).show();
		} else {
			// 不显示听写对话框
			ret = mIat.startListening(mRecognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				// showTip("听写失败,错误码：" + ret);
			} else {
				// showTip(getString(R.string.text_begin));
				// Toast.makeText(context, "请开始说话…", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			// showTip(error.getPlainDescription(true));
			// Toast.makeText(context, error.getPlainDescription(true),
			// Toast.LENGTH_SHORT).show();
		}

	};
	/**
	 * 听写监听器。
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			// Toast.makeText(context, "开始说话", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			// Toast.makeText(context, error.getPlainDescription(true),
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			// Toast.makeText(context, "结束说话", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d("TAG", results.getResultString());
			printResult(results);

			if (isLast) {
				StringBuffer resultBuffer = new StringBuffer();
				for (String key : mIatResults.keySet()) {
					resultBuffer.append(mIatResults.get(key));
				}
				mycontent = resultBuffer.toString();
				mIatResults.clear();
				System.out.println("=================>>" + mycontent);
				if (!"".equals(mycontent)) {
					sendMessageDialog(mycontent);
				}
				mycontent = "";
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			// Toast.makeText(context, "当前正在说话，音量大小：" + volume,
			// Toast.LENGTH_SHORT)
			// .show();
			Log.d("TAG", "返回音频数据：" + data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}

	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);
	}

	/**
	 * 参数设置
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "10000"));

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "2000"));

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "1"));

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/iat.wav");

		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA,
				mSharedPreferences.getString("iat_dwa_preference", "0"));
	}

	private void alertFailDialog() {
		final AlertDialog builder = new AlertDialog.Builder(getActivity())
				.create();
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
		delete_tv.setText("请检查您的网络！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	private void sendMessageDialog(String content) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		params.y = 150;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_birthtixing, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() - 30;
		dialog.show();
		new SendMessageDetailOnClick(dialog, more_pop_menu, content);
	}

	class SendMessageDetailOnClick implements View.OnClickListener {

		private View mainView;
		private Dialog dialog;
		private String content;
		private String alamsound;
		private String alamsoundDesc;
		private LinearLayout detail_edit;
		private LinearLayout detail_close;
		private Button suresend_bt;
		private TextView nongli_tv;
		private TextView tv_date;
		private TextView content_tv;
		Map<Object, Object> map;
		String yeartype;
		String date;
		int beforetime;
		boolean flag = true;
		String tagname;
		int coclor;

		public SendMessageDetailOnClick(Dialog dialog, View view, String content) {
			this.mainView = view;
			this.dialog = dialog;
			this.content = content;
			initview();
			initdata();
		}

		public void initview() {
			detail_edit = (LinearLayout) mainView
					.findViewById(R.id.detail_edit);
			detail_edit.setOnClickListener(this);
			detail_close = (LinearLayout) mainView
					.findViewById(R.id.detail_close);
			detail_close.setOnClickListener(this);
			suresend_bt = (Button) mainView.findViewById(R.id.suresend_bt);
			suresend_bt.setOnClickListener(this);
			tv_date = (TextView) mainView.findViewById(R.id.tv_date);
			content_tv = (TextView) mainView.findViewById(R.id.content_tv);
			nongli_tv = (TextView) mainView.findViewById(R.id.nongli_tv);
			map = ReadWeiXinXml.yuyinBirth(context, content.replace("，", "")
					.replace("。", ""));
		}

		public void initdata() {
			alamsoundDesc = "重要日";
			alamsound = "g_213";
			beforetime = 24 * 60;

			content = (String) map.get("content");
			yeartype = (String) map.get("yeartype");
			date = (String) map.get("date");

			if ("0".equals(yeartype)) {
				if ("".equals(StringUtils.getIsStringEqulesNull(date))) {
					date = DateUtil.formatDateMMDD(new Date());
					tv_date.setText(date);
				} else {
					tv_date.setText(DateUtil.formatDateMMDD(DateUtil
							.parseDate(date)));
				}
				nongli_tv.setVisibility(View.GONE);
			} else {
				if ("".equals(date) || "null".equals(date) || null == date) {
					date = DateUtil.formatDateMMDD(new Date());
					tv_date.setText(date);
				} else {
					tv_date.setText(date);
				}
				nongli_tv.setVisibility(View.VISIBLE);
			}
			content_tv.setText(StringUtils.getIsStringEqulesNull(content));
			tagname = "生日";
			coclor = Integer.parseInt(application.QueryTagIDData(tagname).get(
					CLCategoryTable.ctgId));
		}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.detail_edit:
				if (!"".equals(content_tv.getText().toString().trim())) {
					intent = new Intent(context, EditBirthRepeatActivity.class);
					intent.putExtra("content", content);
					intent.putExtra("yeartype", yeartype);
					intent.putExtra("date", tv_date.getText().toString());
					intent.putExtra("beforetime", beforetime + "");
					intent.putExtra("alamsound", alamsound);
					intent.putExtra("alamsoundDesc", alamsoundDesc);
					intent.putExtra("coclor", coclor);
					intent.putExtra("tagname", tagname);
					startActivityForResult(intent, 100);
					dialog.dismiss();
				} else {
					Toast.makeText(context, "提醒内容不能为空..", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				break;
			case R.id.detail_close:
				dialog.dismiss();
				break;
			case R.id.suresend_bt:
				try {
					if (!"".equals(content_tv.getText().toString().trim())) {
						RepeatBean bean = null;
						CalendarChangeValue changeValue = new CalendarChangeValue();
						App app = App.getDBcApplication();
						int befortime = 24 * 60;
						bean = RepeatDateUtils.saveCalendar("08:20", 4, tv_date
								.getText().toString().trim(), yeartype);
						if ("0".equals(yeartype)) {
							flag = App
									.getDBcApplication()
									.insertCLRepeatTableData(
											befortime,
											coclor,
											1,
											4,
											3,
											0,
											0,
											0,
											1,
											"["
													+ "\""
													+ tv_date.getText()
															.toString() + "\""
													+ "]",
											bean.repNextCreatedTime,
											bean.repLastCreatedTime,
											DateUtil.formatDateTime(new Date()),
											"",
											content_tv.getText().toString()
													.trim(),
											DateUtil.formatDateTime(new Date()),
											"",
											"",
											"08:20",
											alamsoundDesc,
											alamsound,
											DateUtil.formatDateTime(new Date()),
											0, "", 0, "", "", 0, 0, 0, "", "",
											0, 0, "", 0, 0);
						} else {
							flag = App
									.getDBcApplication()
									.insertCLRepeatTableData(
											befortime,
											coclor,
											1,
											6,
											3,
											0,
											0,
											0,
											1,
											"["
													+ "\""
													+ tv_date.getText()
															.toString() + "\""
													+ "]",
											bean.repNextCreatedTime,
											bean.repLastCreatedTime,
											DateUtil.formatDateTime(new Date()),
											"",
											content_tv.getText().toString()
													.trim(),
											DateUtil.formatDateTime(new Date()),
											"",
											"",
											"08:20",
											alamsoundDesc,
											alamsound,
											DateUtil.formatDateTime(new Date()),
											0,
											"",
											0,
											"",
											"",
											0,
											0,
											0,
											"",
											"",
											0,
											0,
											changeValue.changaSZ(tv_date
													.getText().toString()), 0,
											0);
						}
						app.insertScheduleData(content_tv.getText().toString()
								.trim(),
								bean.repNextCreatedTime.substring(0, 10),
								bean.repNextCreatedTime.substring(11, 16), 3,
								befortime, 1, 0, 0, coclor, 0,
								bean.repNextCreatedTime, "", 0, "", "",
								app.repschId, bean.repNextCreatedTime,
								DateUtil.formatDateTime(new Date()), 0, 0, 1,
								alamsoundDesc, alamsound, "", 0, 0, 0, "", "",
								0, 0, 0);
						if (flag) {
							isNetWork();
//							upLoad();
							QueryAlarmData.writeAlarm(getActivity()
									.getApplicationContext());
							adapter.notifyDataSetChanged();
						}
					} else {
						Toast.makeText(context, "提醒内容不能为空..",
								Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			default:
				break;
			}
		}

	}

	public void onEventMainThread(FristFragment event) {

		String msg = event.getMsg();
		if ("1".equals(msg)&&isShow) {
//			 loadData();
			loadCount();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void lazyLoad() {

	}
	@AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
	private void checkPhonePermission() {
		if(Build.VERSION.SDK_INT<23){
			autoFag = true;
		}else {
			String[] perms = {Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_PHONE_STATE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.RECORD_AUDIO};
			if (EasyPermissions.hasPermissions(getActivity(), perms)) {
				// Have permissions, do the thing!
				autoFag = true;
			} else {
				// Ask for both permissions
				EasyPermissions.requestPermissions(this, "该应用需要这些权限，为了保证应用正常运行!",
						RC_LOCATION_CONTACTS_PERM, perms);
			}
			sharedPrefUtil.putString(getActivity(),ShareFile.USERFILE,ShareFile.PERMISSIONSTATE,"1");
		}
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkstorgePhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.STORAGE);
//            if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
//            }
//            int checkcontactPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.CONTACTS);
//            if(checkcontactPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},10002);
//            }
//            int checkLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.LOCATION);
//            if(checkLocationPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10003);
//            }
//            int checkPhoneStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.LOCATION);
//            if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_PHONE_STATE},10004);
//            }
//            int checkautioStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.MICROPHONE);
//            if(checkautioStatePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},10005);
//            }else{
//                autoFag = true;
//            }
//        }else{
//            autoFag = true;
//        }
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		// EasyPermissions handles the request result.
		if(PackageManager.PERMISSION_GRANTED==grantResults[3]){
			autoFag = true;
		}else{
			autoFag = false;
		}
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}
	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		Log.d("TAG", "onPermissionsGranted:" + requestCode + ":" + perms.size());
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
// (Optional) Check whether the user denied permissions and checked NEVER ASK AGAIN.
		// This will display a dialog directing them to enable the permission in app settings.
		EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
				getString(R.string.rationale_ask_again),
				R.string.action_settings, R.string.cancel, null, perms);
	}
}
