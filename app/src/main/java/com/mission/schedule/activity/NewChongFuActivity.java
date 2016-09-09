package com.mission.schedule.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.mission.schedule.adapter.NewChongFuAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.DelFriendRepBean;
import com.mission.schedule.bean.NewFriendBean;
import com.mission.schedule.bean.NewFriendChongFuBackBean;
import com.mission.schedule.bean.NewFriendChongFuBean;
import com.mission.schedule.bean.NewFriendChongFuBean1;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBackBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBean;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLCategoryTable;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.service.NewFriendDataUpLoadService;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.IXListViewListener;
import com.mission.schedule.utils.AfterPermissionGranted;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.EasyPermissions;
import com.mission.schedule.utils.JsonParser;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.NewDayComparator;
import com.mission.schedule.utils.NewFriendYinLiComparator;
import com.mission.schedule.utils.NewYearDateComparator;
import com.mission.schedule.utils.ReadTextContentXml.ReadWeiXinXml;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class NewChongFuActivity extends BaseActivity implements
		OnClickListener, IXListViewListener,EasyPermissions.PermissionCallbacks {

	@ViewResId(id = R.id.chongfu_lv)
	private SwipeXListViewNoHead chongfu_lv;
	@ViewResId(id = R.id.newrepeat_tv)
	private TextView newrepeat_tv;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	int fid;
	int state;
	String userId;
	String friendName;
	String userName;
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private SharedPreferences mSharedPreferences;

	String path;
	App app = null;

	List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
	String json = "";
	NewChongFuAdapter adapter = null;

	List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everydayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> workdayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyweekList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everymonthList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> everyyearList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> nongliList = new ArrayList<Map<String, String>>();

	CalendarChangeValue changeValue = new CalendarChangeValue();
	String downtime = "";
	private static final int RC_LOCATION_CONTACTS_PERM = 124;
	String  permissionState = "0";
	boolean autoFag = false;

	@Override
	protected void setListener() {
		newrepeat_tv.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newchongfu);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		EventBus.getDefault().register(this);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		userId = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		userName = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		downtime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.FRIENDDOWNRepTIME, "");
		fid = NewSendMessageToFriendActivity.friendsBean.fId;
		friendName = NewSendMessageToFriendActivity.friendsBean.uName;

		// 初始化识别无UI识别对象
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mIat = SpeechRecognizer.createRecognizer(context, mInitListener);

		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
		// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
		mIatDialog = new RecognizerDialog(context, mInitListener);
		mSharedPreferences = context.getSharedPreferences(
				"com.iflytek.setting", Activity.MODE_PRIVATE);
		fid = NewSendMessageToFriendActivity.friendsBean.fId;
		friendName = NewSendMessageToFriendActivity.friendsBean.uName;

		chongfu_lv.setPullLoadEnable(true);
		chongfu_lv.setXListViewListener(this);
		chongfu_lv.setFocusable(true);

		View footView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		chongfu_lv.addFooterView(footView);
		DownLoadMessage();
		permissionState = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "0");
		if("0".equals(permissionState)){
			checkPhonePermission();
		}
	}

	private void DownLoadMessage() {
		if ("".equals(downtime)) {
			downtime = "2016-01-01%2B00:00:00";
		} else {
			downtime = downtime.replace(" ", "%2B");
		}
		path = URLConstants.新版好友查询重复信息 + userId + "&cpId=" + fid
				+ "&pageNum=1000&nowPage=1&downTime=" + downtime;
		NewSendMessageToFriendActivity.updateShunyanData();
		loadData();
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			DownLoadDataAsync(path);
		}
		// }
	}

	private void loadData() {
		everyyearList.clear();
		everymonthList.clear();
		everyweekList.clear();
		workdayList.clear();
		everydayList.clear();
		nongliList.clear();
		mList.clear();
		try {
			List<Map<String, String>> list = app.queryAllLocalFriendsData(9,
					fid);
			if (list == null && list.size() == 0) {
				return;
			} else {
				for (int i = 0; i < list.size(); i++) {
					if ("1".equals(list.get(i).get(CLNFMessage.nfmType))) {
						everydayList.add(list.get(i));
					} else if ("2".equals(list.get(i).get(CLNFMessage.nfmType))) {
						everyweekList.add(list.get(i));
						Collections.sort(everyweekList, new NewDayComparator());
					} else if ("3".equals(list.get(i).get(CLNFMessage.nfmType))) {
						everymonthList.add(list.get(i));
						Collections
								.sort(everymonthList, new NewDayComparator());
					} else if ("4".equals(list.get(i).get(CLNFMessage.nfmType))) {
						everyyearList.add(list.get(i));
						Collections.sort(everyyearList,
								new NewYearDateComparator());
					} else if ("6".equals(list.get(i).get(CLNFMessage.nfmType))) {
						nongliList.add(list.get(i));
						Collections.sort(nongliList,
								new NewFriendYinLiComparator());
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

	@Override
	protected void setAdapter() {
		adapter = new NewChongFuAdapter(context, mList,R.layout.adapter_newchongfu, handler, chongfu_lv);
		chongfu_lv.setAdapter(adapter);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = null;
			Map<String, String> mMap = (Map<String, String>) msg.obj;
			int position = msg.arg1;
			switch (msg.what) {
			case 0:// 删除
				if (NewRiChengActivity.beans != null
						&& NewRiChengActivity.beans.size() > 0) {
					for (int i = 0; i < NewRiChengActivity.beans.size(); i++) {
						if (NewRiChengActivity.beans.get(i).state == 0) {
							if (NewRiChengActivity.beans.get(i).dataState == 1
									|| NewRiChengActivity.beans.get(i).dataState == 2) {
								if (Integer.parseInt(mMap
										.get(CLNFMessage.nfmId)) == NewRiChengActivity.beans
										.get(i).oldId) {
									mMap.put(CLNFMessage.nfmId,
											NewRiChengActivity.beans.get(i).id
													+ "");
									break;
								}
							}
						} else {
							if (Integer.parseInt(mMap.get(CLNFMessage.nfmId)) == NewRiChengActivity.beans
									.get(i).oldId) {
								mMap.put(CLNFMessage.nfmId,
										NewRiChengActivity.beans.get(i).id + "");
								break;
							}
						}
					}
				}
				String deleteId = mMap.get(CLNFMessage.nfmId);
				app.deleteNewFriendLocalData(Integer.parseInt(deleteId));
				app.deleteNewFriendsChildData(Integer.parseInt(deleteId));
				chongfu_lv.hiddenRight();
				mList.remove(position);
				adapter.notifyDataSetChanged();
				chongfu_lv.invalidate();
				UpdateLoadData();
				break;
			case 1:// 点击详情
				if (NewRiChengActivity.beans != null
						&& NewRiChengActivity.beans.size() > 0) {
					for (int i = 0; i < NewRiChengActivity.beans.size(); i++) {
						if (NewRiChengActivity.beans.get(i).state == 0) {
							if (NewRiChengActivity.beans.get(i).dataState == 1
									|| NewRiChengActivity.beans.get(i).dataState == 2) {
								if (Integer.parseInt(mMap
										.get(CLNFMessage.nfmId)) == NewRiChengActivity.beans
										.get(i).oldId) {
									mMap.put(CLNFMessage.nfmId,
											NewRiChengActivity.beans.get(i).id
													+ "");
									break;
								}
							}
						} else {
							if (Integer.parseInt(mMap.get(CLNFMessage.nfmId)) == NewRiChengActivity.beans
									.get(i).oldId) {
								mMap.put(CLNFMessage.nfmId,
										NewRiChengActivity.beans.get(i).id + "");
								break;
							}
						}
					}
				}
				dialogDetailOnClick(mMap);
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newrepeat_tv:
			dialogAddRepeatOnClick();
			break;

		default:
			break;
		}
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
		View more_pop_menu = fac.inflate(
				R.layout.dialog_myrepeatfragment_addrepeat, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
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
				intent = new Intent(context, AddNewFriendRepeatActivity.class);
				intent.putExtra("date", "每天");
				intent.putExtra("friendname", friendName);
				intent.putExtra("friendid", fid);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.workday_tv:
				intent = new Intent(context, AddNewFriendRepeatActivity.class);
				intent.putExtra("date", "工作日");
				intent.putExtra("friendname", friendName);
				intent.putExtra("friendid", fid);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everyweek_tv:
				intent = new Intent(context, AddNewFriendRepeatActivity.class);
				intent.putExtra("date", "每周");
				intent.putExtra("friendname", friendName);
				intent.putExtra("friendid", fid);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everymonth_tv:
				intent = new Intent(context, AddNewFriendRepeatActivity.class);
				intent.putExtra("date", "每月");
				intent.putExtra("friendname", friendName);
				intent.putExtra("friendid", fid);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.everyyear_tv:
				intent = new Intent(context, AddNewFriendRepeatActivity.class);
				intent.putExtra("date", "每年");
				intent.putExtra("friendname", friendName);
				intent.putExtra("friendid", fid);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.birth_tv:
				if(autoFag){
					HuaTongDialog();
				}else{
					Toast.makeText(context,"确实必要的权限!",Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 话筒对话框
	 */
	Dialog huatongdialog = null;
	// private GestureDetector mGestureDetector;
	Button yuyin;

	private void HuaTongDialog() {
		huatongdialog = new Dialog(context, R.style.dialog_huatong);
		Window window = huatongdialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_yuyinbirth, null);
		yuyin = (Button) more_pop_menu.findViewById(R.id.yuyin);
		LinearLayout yuyin_ll = (LinearLayout) more_pop_menu
				.findViewById(R.id.yuyin_ll);
		huatongdialog.setCanceledOnTouchOutside(true);
		huatongdialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		// yuyin.setOnLongClickListener(new PicOnLongClick());
		// mGestureDetector = new GestureDetector(context,
		// new MyOnGestureListener());
		yuyin_ll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					yuyin.setBackgroundDrawable(context.getResources()
							.getDrawable(R.mipmap.btn_yuyina));
					xunfeiRecognizer();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					yuyin.setBackgroundDrawable(context.getResources()
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
					yuyin.setBackgroundDrawable(context.getResources()
							.getDrawable(R.mipmap.btn_yuyina));
					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
						xunfeiRecognizer();
					} else {
						alertFailDialog();
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					yuyin.setBackgroundDrawable(context.getResources()
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
				Toast.makeText(context, "初始化失败，错误码：" + code, Toast.LENGTH_LONG)
						.show();
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
			Toast.makeText(context, "请开始说话…", Toast.LENGTH_SHORT).show();
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
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
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
				if ("".equals(date) || "null".equals(date) || null == date) {
					date = DateUtil.formatDateTimeHm(new Date());
					tv_date.setText(date);
				} else {
					tv_date.setText(DateUtil.formatDateTimeHm(DateUtil
							.parseDate(date)));
				}
				nongli_tv.setVisibility(View.GONE);
			} else {
				if ("".equals(date) || "null".equals(date) || null == date) {
					date = DateUtil.formatDateTimeHm(new Date());
					tv_date.setText(date);
				} else {
					tv_date.setText(date);
				}
				nongli_tv.setVisibility(View.VISIBLE);
			}
			content_tv.setText(content);
			tagname = "生日";
			coclor = Integer.parseInt(app.QueryTagIDData(tagname).get(
					CLCategoryTable.ctgId));
		}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.detail_edit:
				if (!"".equals(content_tv.getText().toString().trim())) {
					intent = new Intent(context,
							EditNewFriendBirthdayActivity.class);
					intent.putExtra("content", content);
					intent.putExtra("yeartype", yeartype);
					intent.putExtra("date", tv_date.getText().toString());
					intent.putExtra("beforetime", beforetime + "");
					intent.putExtra("alamsound", alamsound);
					intent.putExtra("alamsoundDesc", alamsoundDesc);
					intent.putExtra("friendid", fid);
					intent.putExtra("friendname", friendName);
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
						App app = App.getDBcApplication();
						int befortime = 24 * 60;
						bean = RepeatDateUtils.saveCalendar("08:20", 4, tv_date
								.getText().toString().trim(), yeartype);
						if ("0".equals(yeartype)) {
							flag = app.insertMessageSendData(
									Integer.parseInt(userId), fid, 0, 0, 2, 3,
									0, coclor, 1, befortime, 0, 4, 0, 0, 0, 0,
									0, "[" + "\""
											+ tv_date.getText().toString()
											+ "\"" + "]", content_tv.getText()
											.toString().trim(),
									DateUtil.formatDate(new Date()), "08:20",
									"", "", "", alamsoundDesc, alamsound,
									DateUtil.formatDateTime(new Date()),
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, "", "", userName,
									"", 1, 0, 0, "", 0, 0, "", 0, "",
									DateUtil.formatDateTimeSs(new Date()));
						} else {
							flag = app.insertMessageSendData(Integer
									.parseInt(userId), fid, 0, 0, 2, 3, 0,
									coclor, 1, befortime, 0, 6, 0, 0, 0, 0, 0,
									"[" + "\"" + tv_date.getText().toString()
											+ "\"" + "]", content_tv.getText()
											.toString().trim(), DateUtil
											.formatDate(new Date()), "08:20",
									"", "", "", alamsoundDesc, alamsound,
									DateUtil.formatDateTime(new Date()),
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, "", "", userName,
									"", 1, 0, 0, "", 0, 0, "", 0, changeValue
											.changaSZ(tv_date.getText()
													.toString()), DateUtil
											.formatDateTimeSs(new Date()));
						}

						if (flag) {
							app.insertMessageSendData(Integer.parseInt(userId),
									fid, 0, 0, 1, 3, 0, coclor, 1, befortime,
									0, 0, 0, 0, 0, 0, 0, "["
											+ tv_date.getText().toString()
											+ "]", content_tv.getText()
											.toString().trim(),
									bean.repNextCreatedTime.substring(0, 10),
									"08:20", "", "", "", alamsoundDesc,
									alamsound,
									DateUtil.formatDateTime(new Date()),
									DateUtil.formatDateTime(new Date()),
									bean.repLastCreatedTime,
									bean.repNextCreatedTime, "", "", userName,
									"", 0, app.nfmId, 0, "", 0, 0, "", 0, "",
									DateUtil.formatDateTimeSs(new Date()));
							UpdateLoadData();
							loadData();
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

	/**************************************** 下载日程列表数据 ******************************************/
	private void DownLoadDataAsync(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								NewFriendChongFuBean1 bean1 = null;
								List<NewFriendChongFuBean> beans = null;
								List<DelFriendRepBean> delBeans = null;
								Gson gson = new Gson();
								NewFriendChongFuBackBean backBean = gson
										.fromJson(result,
												NewFriendChongFuBackBean.class);
								sharedPrefUtil.putString(context,
										ShareFile.USERFILE,
										ShareFile.FRIENDDOWNRepTIME,
										backBean.downTime.replace("T", " "));
								if (backBean.status == 0) {
									bean1 = backBean.page;
									delBeans = backBean.delList;
									if (delBeans != null && delBeans.size() > 0) {
										for (DelFriendRepBean bean : delBeans) {
											app.deleteFriendsData(bean.uid,
													bean.dataId);
										}
									}
									if (bean1 != null) {
										beans = bean1.items;
										for (int i = 0; i < beans.size(); i++) {
											int repispause = 0;
											if ("".equals(beans.get(i).repIsPuase)
													|| "null".equals(beans
															.get(i).repIsPuase)) {
												repispause = 0;
											} else {
												repispause = Integer
														.parseInt(beans.get(i).repIsPuase);
											}
											if (app.CheckCountNewFriendData(Integer
													.parseInt(beans.get(i).id)) != 0) {
												app.alterNewFriendsData(
														Integer.parseInt(beans
																.get(i).id),
														Integer.parseInt(beans
																.get(i).uid),
														Integer.parseInt(beans
																.get(i).cpId),
														Integer.parseInt(beans
																.get(i).calendaId),
														Integer.parseInt(beans
																.get(i).openState),
														2,
														Integer.parseInt(beans
																.get(i).schIsAlarm),
														Integer.parseInt(beans
																.get(i).schCpostpone),
														0,
														Integer.parseInt(beans
																.get(i).schDisplayTime),
														Integer.parseInt(beans
																.get(i).schBeforeTime),
														Integer.parseInt(beans
																.get(i).cType),
														Integer.parseInt(beans
																.get(i).repType),
														Integer.parseInt(beans
																.get(i).schAType),
														Integer.parseInt(beans
																.get(i).repInSTable),
														Integer.parseInt(beans
																.get(i).isEnd),
														Integer.parseInt(beans
																.get(i).downstate),
														Integer.parseInt(beans
																.get(i).poststate),
														beans.get(i).repTypeParameter,
														beans.get(i).schContent,
														beans.get(i).schDate,
														beans.get(i).schctime,
														beans.get(i).cTypeDesc,
														beans.get(i).cTypeSpare,
														"",
														beans.get(i).CAlarmsoundDesc,
														beans.get(i).CAlarmsound,
														beans.get(i).repstartdate
																.replace("T",
																		" "),
														beans.get(i).repinitialcreatedtime
																.replace("T",
																		" "),
														beans.get(i).replastcreatedtime
																.replace("T",
																		" "),
														beans.get(i).repnextcreatedtime
																.replace("T",
																		" "),
														beans.get(i).schWebURL,
														beans.get(i).schImagePath,
														beans.get(i).uName,
														beans.get(i).remark,
														0,
														Integer.parseInt(beans
																.get(i).pId),
														0,
														"",
														0,
														0,
														"",
														repispause,
														beans.get(i).parReamrk,
														beans.get(i).changTime
																.replace("T",
																		" ")
																.substring(0,
																		19));
												app.deleteNewFriendsChildData(Integer
														.parseInt(beans.get(i).id));
												if ("0".equals(beans.get(i).repIsPuase)) {
													if ("0".equals(beans.get(i).repstatetwo)) {
														NewSendMessageToFriendActivity
																.CreateNextChildData(beans
																		.get(i));
													} else {
														if (NewSendMessageToFriendActivity
																.getNextChildTime(beans
																		.get(i)).repNextCreatedTime.equals(beans
																.get(i).repdatetwo
																.replace("T",
																		" "))) {
															if ("3".equals(beans
																	.get(i).repstatetwo)) {
																NewSendMessageToFriendActivity
																		.CreateNextChildEndData(beans
																				.get(i));
															}
														} else {
															NewSendMessageToFriendActivity
																	.CreateNextChildData(beans
																			.get(i));
														}
													}
												}
											} else {
												boolean fag = app
														.insertIntnetMessageSendData(
																Integer.parseInt(beans
																		.get(i).id),
																Integer.parseInt(beans
																		.get(i).uid),
																Integer.parseInt(beans
																		.get(i).cpId),
																Integer.parseInt(beans
																		.get(i).calendaId),
																Integer.parseInt(beans
																		.get(i).openState),
																2,
																Integer.parseInt(beans
																		.get(i).schIsAlarm),
																Integer.parseInt(beans
																		.get(i).schCpostpone),
																0,
																Integer.parseInt(beans
																		.get(i).schDisplayTime),
																Integer.parseInt(beans
																		.get(i).schBeforeTime),
																Integer.parseInt(beans
																		.get(i).cType),
																Integer.parseInt(beans
																		.get(i).repType),
																Integer.parseInt(beans
																		.get(i).schAType),
																Integer.parseInt(beans
																		.get(i).repInSTable),
																Integer.parseInt(beans
																		.get(i).isEnd),
																Integer.parseInt(beans
																		.get(i).downstate),
																Integer.parseInt(beans
																		.get(i).poststate),
																beans.get(i).repTypeParameter,
																beans.get(i).schContent,
																beans.get(i).repnextcreatedtime
																		.replace(
																				"T",
																				" ")
																		.substring(
																				0,
																				10),
																beans.get(i).schctime,
																beans.get(i).cTypeDesc,
																beans.get(i).cTypeSpare,
																"",
																beans.get(i).CAlarmsoundDesc,
																beans.get(i).CAlarmsound,
																beans.get(i).repstartdate
																		.replace(
																				"T",
																				" "),
																beans.get(i).repinitialcreatedtime
																		.replace(
																				"T",
																				" "),
																beans.get(i).replastcreatedtime
																		.replace(
																				"T",
																				" "),
																beans.get(i).repnextcreatedtime
																		.replace(
																				"T",
																				" "),
																beans.get(i).schWebURL,
																beans.get(i).schImagePath,
																beans.get(i).uName,
																beans.get(i).remark,
																0,
																Integer.parseInt(beans
																		.get(i).pId),
																0,
																"",
																0,
																0,
																"",
																repispause,
																beans.get(i).parReamrk,
																beans.get(i).changTime
																		.replace(
																				"T",
																				" ")
																		.substring(
																				0,
																				19));
												if (fag) {
													if ("0".equals(beans.get(i).repIsPuase)) {
														if ("0".equals(beans
																.get(i).repstatetwo)) {
															NewSendMessageToFriendActivity
																	.CreateNextChildData(beans
																			.get(i));
														} else {
															if (NewSendMessageToFriendActivity
																	.getNextChildTime(beans
																			.get(i)).repNextCreatedTime
																	.equals(beans
																			.get(i).repdatetwo
																			.replace(
																					"T",
																					" "))) {
																if ("3".equals(beans
																		.get(i).repstatetwo)) {
																	NewSendMessageToFriendActivity
																			.CreateNextChildEndData(beans
																					.get(i));
																}
															} else {
																NewSendMessageToFriendActivity
																		.CreateNextChildData(beans
																				.get(i));
															}
														}
													}
												}
											}
										}
									}
									NewSendMessageToFriendActivity
											.updateShunyanData();
									// loadData();
									// adapter.notifyDataSetChanged();
								} else {
									NewSendMessageToFriendActivity
											.updateShunyanData();
									// loadData();
									// adapter.notifyDataSetChanged();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						onLoad();
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

	/******************************** 发送消息进行同步 ***************************************************/
	private void UpdateLoadData() {
		addList.clear();
		updateList.clear();
		deleteList.clear();
		JSONArray jsonarray1 = new JSONArray();
		JSONArray jsonarray2 = new JSONArray();
		JSONArray jsonarray3 = new JSONArray();
		JSONObject jsonobject1 = new JSONObject();
		try {
			addList = app.queryAllLocalFriendsData(-1, 0);
			updateList = app.queryAllLocalFriendsData(-2, 0);
			deleteList = app.queryAllLocalFriendsData(-3, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			try {
				if (addList != null && addList.size() > 0) {
					for (int i = 0; i < addList.size(); i++) {
						JSONObject jsonobject = new JSONObject();
						jsonobject.put("id",
								addList.get(i).get(CLNFMessage.nfmId));
						jsonobject.put("uid",
								addList.get(i).get(CLNFMessage.nfmSendId));
						jsonobject.put("cpId",
								addList.get(i).get(CLNFMessage.nfmGetId));
						jsonobject.put("message",
								addList.get(i).get(CLNFMessage.nfmContent));
						jsonobject.put("status",
								addList.get(i).get(CLNFMessage.nfmStatus));
						jsonobject.put("cisAlarm",
								addList.get(i).get(CLNFMessage.nfmIsAlarm));
						jsonobject.put("cdate",
								addList.get(i).get(CLNFMessage.nfmDate));
						jsonobject.put("ctime",
								addList.get(i).get(CLNFMessage.nfmTime));
						jsonobject.put("cbeforTime",
								addList.get(i).get(CLNFMessage.nfmBeforeTime));
						jsonobject.put("CAlarmsound",
								addList.get(i).get(CLNFMessage.nfmRingCode));
						jsonobject.put("CAlarmsoundDesc",
								addList.get(i).get(CLNFMessage.nfmRingDesc));
						jsonobject.put("repType",
								addList.get(i).get(CLNFMessage.nfmType));
						jsonobject.put("repTypeParameter",
								addList.get(i).get(CLNFMessage.nfmParameter)
										.replace("\n\"", "").replace("\n", "")
										.replace("\"", "").toString());
						jsonobject.put("CPostpone",
								addList.get(i).get(CLNFMessage.nfmPostpone));
						jsonobject.put("CTags",
								addList.get(i).get(CLNFMessage.nfmTags));
						jsonobject.put("COpenstate",
								addList.get(i).get(CLNFMessage.nfmOpenState));
						jsonobject.put("CLightAppId", "0");
						jsonobject.put("CRecommendName",
								addList.get(i).get(CLNFMessage.nfmSendName));
						jsonobject.put("repColorType",
								addList.get(i).get(CLNFMessage.nfmColorType));
						jsonobject.put("repDisplayTime",
								addList.get(i).get(CLNFMessage.nfmDisplayTime));
						jsonobject.put("endState",
								addList.get(i).get(CLNFMessage.nfmIsEnd));
						jsonobject.put("schIsImportant", "0");
						jsonobject.put("calendaId",
								addList.get(i).get(CLNFMessage.nfmCalendarId));
						jsonobject.put("atype",
								addList.get(i).get(CLNFMessage.nfmAType));
						jsonobject.put("webUrl",
								addList.get(i).get(CLNFMessage.nfmWebURL));
						jsonobject.put("imgPath",
								addList.get(i).get(CLNFMessage.nfmImagePath));
						jsonobject.put("repInStable",
								addList.get(i).get(CLNFMessage.nfmInSTable));
						jsonobject.put("downstate",
								addList.get(i).get(CLNFMessage.nfmDownState));
						jsonobject.put("remark",
								addList.get(i).get(CLNFMessage.nfmRemark));
						jsonobject.put("poststate",
								addList.get(i).get(CLNFMessage.nfmPostState));
						jsonobject.put("repnextcreatedtime", addList.get(i)
								.get(CLNFMessage.nfmNextCreatedTime));
						jsonobject.put("replastcreatedtime", addList.get(i)
								.get(CLNFMessage.nfmLastCreatedTime));
						jsonobject.put("repstartdate",
								addList.get(i).get(CLNFMessage.nfmStartDate));
						jsonobject.put("repinitialcreatedtime", addList.get(i)
								.get(CLNFMessage.nfmInitialCreatedTime));
						jsonobject.put("cType",
								addList.get(i).get(CLNFMessage.nfmSourceType));
						jsonobject.put("cTypeDesc",
								addList.get(i).get(CLNFMessage.nfmSourceDesc));
						jsonobject.put(
								"cTypeSpare",
								addList.get(i).get(
										CLNFMessage.nfmSourceDescSpare));
						jsonobject.put("pId",
								addList.get(i).get(CLNFMessage.nfmPId));
						jsonobject.put("repstatetwo",
								addList.get(i).get(CLNFMessage.nfmSubState));
						jsonobject.put("repdatetwo",
								addList.get(i).get(CLNFMessage.nfmSubDate));
						jsonobject.put("repState",
								addList.get(i).get(CLNFMessage.nfmCState));
						jsonobject.put("repCalendaState",
								addList.get(i).get(CLNFMessage.nfmSubEnd));
						jsonobject.put("repCalendaTime",
								addList.get(i).get(CLNFMessage.nfmSubEndDate));
						jsonobject.put("repIsPuase",
								addList.get(i).get(CLNFMessage.nfmIsPuase));
						jsonobject.put("parReamrk", addList.get(i).get(CLNFMessage.parReamrk));
						jsonarray1.put(jsonobject);
					}
					jsonobject1.put("addData", jsonarray1);
				} else {
					jsonobject1.put("addData", jsonarray1);
				}
				if (updateList != null && updateList.size() > 0) {
					for (int i = 0; i < updateList.size(); i++) {
						JSONObject jsonobject = new JSONObject();
						jsonobject.put("id",
								updateList.get(i).get(CLNFMessage.nfmId));
						jsonobject.put("uid",
								updateList.get(i).get(CLNFMessage.nfmSendId));
						jsonobject.put("cpId",
								updateList.get(i).get(CLNFMessage.nfmGetId));
						jsonobject.put("message",
								updateList.get(i).get(CLNFMessage.nfmContent));
						jsonobject.put("status",
								updateList.get(i).get(CLNFMessage.nfmStatus));
						jsonobject.put("cisAlarm",
								updateList.get(i).get(CLNFMessage.nfmIsAlarm));
						jsonobject.put("cdate",
								updateList.get(i).get(CLNFMessage.nfmDate));
						jsonobject.put("ctime",
								updateList.get(i).get(CLNFMessage.nfmTime));
						jsonobject.put("cbeforTime",
								updateList.get(i)
										.get(CLNFMessage.nfmBeforeTime));
						jsonobject.put("CAlarmsound",
								updateList.get(i).get(CLNFMessage.nfmRingCode));
						jsonobject.put("CAlarmsoundDesc", updateList.get(i)
								.get(CLNFMessage.nfmRingDesc));
						jsonobject.put("repType",
								updateList.get(i).get(CLNFMessage.nfmType));
						jsonobject.put("repTypeParameter",
								updateList.get(i).get(CLNFMessage.nfmParameter)
										.replace("\n\"", "").replace("\n", "")
										.replace("\"", "").toString());
						jsonobject.put("CPostpone",
								updateList.get(i).get(CLNFMessage.nfmPostpone));
						jsonobject.put("CTags",
								updateList.get(i).get(CLNFMessage.nfmTags));
						jsonobject
								.put("COpenstate",
										updateList.get(i).get(
												CLNFMessage.nfmOpenState));
						jsonobject.put("CLightAppId", "0");
						jsonobject.put("CRecommendName",
								updateList.get(i).get(CLNFMessage.nfmSendName));
						jsonobject
								.put("repColorType",
										updateList.get(i).get(
												CLNFMessage.nfmColorType));
						jsonobject.put(
								"repDisplayTime",
								updateList.get(i).get(
										CLNFMessage.nfmDisplayTime));
						jsonobject.put("endState",
								updateList.get(i).get(CLNFMessage.nfmIsEnd));
						jsonobject.put("schIsImportant", "0");
						jsonobject.put("calendaId",
								updateList.get(i)
										.get(CLNFMessage.nfmCalendarId));
						jsonobject.put("atype",
								updateList.get(i).get(CLNFMessage.nfmAType));
						jsonobject.put("webUrl",
								updateList.get(i).get(CLNFMessage.nfmWebURL));
						jsonobject
								.put("imgPath",
										updateList.get(i).get(
												CLNFMessage.nfmImagePath));
						jsonobject.put("repInStable",
								updateList.get(i).get(CLNFMessage.nfmInSTable));
						jsonobject
								.put("downstate",
										updateList.get(i).get(
												CLNFMessage.nfmDownState));
						jsonobject.put("remark",
								updateList.get(i).get(CLNFMessage.nfmRemark));
						jsonobject
								.put("poststate",
										updateList.get(i).get(
												CLNFMessage.nfmPostState));
						jsonobject.put("repnextcreatedtime", updateList.get(i)
								.get(CLNFMessage.nfmNextCreatedTime));
						jsonobject.put("replastcreatedtime", updateList.get(i)
								.get(CLNFMessage.nfmLastCreatedTime));
						jsonobject
								.put("repstartdate",
										updateList.get(i).get(
												CLNFMessage.nfmStartDate));
						jsonobject.put(
								"repinitialcreatedtime",
								updateList.get(i).get(
										CLNFMessage.nfmInitialCreatedTime));
						jsonobject.put("cType",
								updateList.get(i)
										.get(CLNFMessage.nfmSourceType));
						jsonobject.put("cTypeDesc",
								updateList.get(i)
										.get(CLNFMessage.nfmSourceDesc));
						jsonobject.put(
								"cTypeSpare",
								updateList.get(i).get(
										CLNFMessage.nfmSourceDescSpare));
						jsonobject.put("pId",
								updateList.get(i).get(CLNFMessage.nfmPId));
						jsonobject.put("repstatetwo",
								updateList.get(i).get(CLNFMessage.nfmSubState));
						jsonobject.put("repdatetwo",
								updateList.get(i).get(CLNFMessage.nfmSubDate));
						jsonobject.put("repState",
								updateList.get(i).get(CLNFMessage.nfmCState));
						jsonobject.put("repCalendaState", updateList.get(i)
								.get(CLNFMessage.nfmSubEnd));
						jsonobject.put("repCalendaTime",
								updateList.get(i)
										.get(CLNFMessage.nfmSubEndDate));
						jsonobject.put("repIsPuase",
								updateList.get(i).get(CLNFMessage.nfmIsPuase));
						jsonobject.put("parReamrk", updateList.get(i).get(CLNFMessage.parReamrk));
						jsonarray2.put(jsonobject);
					}
					jsonobject1.put("updateData", jsonarray2);
				} else {
					jsonobject1.put("updateData", jsonarray2);
				}
				if (deleteList != null && deleteList.size() > 0) {
					for (int i = 0; i < deleteList.size(); i++) {
						JSONObject jsonobject = new JSONObject();
						jsonobject.put("id",
								deleteList.get(i).get(CLNFMessage.nfmId));
						jsonobject.put("uid",
								deleteList.get(i).get(CLNFMessage.nfmSendId));
						jsonobject.put("cpId",
								deleteList.get(i).get(CLNFMessage.nfmGetId));
						jsonobject.put("message",
								deleteList.get(i).get(CLNFMessage.nfmContent));
						jsonobject.put("status",
								deleteList.get(i).get(CLNFMessage.nfmStatus));
						jsonobject.put("cisAlarm",
								deleteList.get(i).get(CLNFMessage.nfmIsAlarm));
						jsonobject.put("cdate",
								deleteList.get(i).get(CLNFMessage.nfmDate));
						jsonobject.put("ctime",
								deleteList.get(i).get(CLNFMessage.nfmTime));
						jsonobject.put("cbeforTime",
								deleteList.get(i)
										.get(CLNFMessage.nfmBeforeTime));
						jsonobject.put("CAlarmsound",
								deleteList.get(i).get(CLNFMessage.nfmRingCode));
						jsonobject.put("CAlarmsoundDesc", deleteList.get(i)
								.get(CLNFMessage.nfmRingDesc));
						jsonobject.put("repType",
								deleteList.get(i).get(CLNFMessage.nfmType));
						jsonobject.put("repTypeParameter",
								deleteList.get(i).get(CLNFMessage.nfmParameter)
										.replace("\n\"", "").replace("\n", "")
										.replace("\"", "").toString());
						jsonobject.put("CPostpone",
								deleteList.get(i).get(CLNFMessage.nfmPostpone));
						jsonobject.put("CTags",
								deleteList.get(i).get(CLNFMessage.nfmTags));
						jsonobject
								.put("COpenstate",
										deleteList.get(i).get(
												CLNFMessage.nfmOpenState));
						jsonobject.put("CLightAppId", "0");
						jsonobject.put("CRecommendName",
								deleteList.get(i).get(CLNFMessage.nfmSendName));
						jsonobject
								.put("repColorType",
										deleteList.get(i).get(
												CLNFMessage.nfmColorType));
						jsonobject.put(
								"repDisplayTime",
								deleteList.get(i).get(
										CLNFMessage.nfmDisplayTime));
						jsonobject.put("endState",
								deleteList.get(i).get(CLNFMessage.nfmIsEnd));
						jsonobject.put("schIsImportant", "0");
						jsonobject.put("calendaId",
								deleteList.get(i)
										.get(CLNFMessage.nfmCalendarId));
						jsonobject.put("atype",
								deleteList.get(i).get(CLNFMessage.nfmAType));
						jsonobject.put("webUrl",
								deleteList.get(i).get(CLNFMessage.nfmWebURL));
						jsonobject
								.put("imgPath",
										deleteList.get(i).get(
												CLNFMessage.nfmImagePath));
						jsonobject.put("repInStable",
								deleteList.get(i).get(CLNFMessage.nfmInSTable));
						jsonobject
								.put("downstate",
										deleteList.get(i).get(
												CLNFMessage.nfmDownState));
						jsonobject.put("remark",
								deleteList.get(i).get(CLNFMessage.nfmRemark));
						jsonobject
								.put("poststate",
										deleteList.get(i).get(
												CLNFMessage.nfmPostState));
						jsonobject.put("repnextcreatedtime", deleteList.get(i)
								.get(CLNFMessage.nfmNextCreatedTime));
						jsonobject.put("replastcreatedtime", deleteList.get(i)
								.get(CLNFMessage.nfmLastCreatedTime));
						jsonobject
								.put("repstartdate",
										deleteList.get(i).get(
												CLNFMessage.nfmStartDate));
						jsonobject.put(
								"repinitialcreatedtime",
								deleteList.get(i).get(
										CLNFMessage.nfmInitialCreatedTime));
						jsonobject.put("cType",
								deleteList.get(i)
										.get(CLNFMessage.nfmSourceType));
						jsonobject.put("cTypeDesc",
								deleteList.get(i)
										.get(CLNFMessage.nfmSourceDesc));
						jsonobject.put(
								"cTypeSpare",
								deleteList.get(i).get(
										CLNFMessage.nfmSourceDescSpare));
						jsonobject.put("pId",
								deleteList.get(i).get(CLNFMessage.nfmPId));
						jsonobject.put("repstatetwo",
								deleteList.get(i).get(CLNFMessage.nfmSubState));
						jsonobject.put("repdatetwo",
								deleteList.get(i).get(CLNFMessage.nfmSubDate));
						jsonobject.put("repState",
								deleteList.get(i).get(CLNFMessage.nfmCState));
						jsonobject.put("repCalendaState", deleteList.get(i)
								.get(CLNFMessage.nfmSubEnd));
						jsonobject.put("repCalendaTime",
								deleteList.get(i)
										.get(CLNFMessage.nfmSubEndDate));
						jsonobject.put("repIsPuase",
								deleteList.get(i).get(CLNFMessage.nfmIsPuase));
						jsonobject.put("parReamrk", deleteList.get(i).get(CLNFMessage.parReamrk));
						jsonarray3.put(jsonobject);
					}
					jsonobject1.put("deleData", jsonarray3);
				} else {
					jsonobject1.put("deleData", jsonarray3);
				}
				json = jsonobject1.toString();
				if (!"".equals(json)) {
					Intent intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				}
				// String path = URLConstants.新版好友同步;
				// UpdateLoadAsync(path, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void UpdateLoadAsync(String path, final String json) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							List<UpdateNewFriendMessageBean> list = null;
							try {
								UpdateNewFriendMessageBackBean backBean = gson
										.fromJson(
												result,
												UpdateNewFriendMessageBackBean.class);
								if (backBean.status == 0) {
									list = backBean.list;
									if (list != null && list.size() > 0) {
										for (int i = 0; i < list.size(); i++) {
											if (list.get(i).state == 0) {
												if (list.get(i).dataState == 1
														|| list.get(i).dataState == 2) {
													app.updateNewFriendsData(
															list.get(i).oldId,
															list.get(i).id,
															Integer.parseInt(list
																	.get(i).calendId),
															0);
													app.updateNewFriendsChildData(
															list.get(i).oldId,
															list.get(i).id,
															Integer.parseInt(list
																	.get(i).calendId));
												} else if (list.get(i).dataState == 3) {
													app.deleteNewFriendsData(list
															.get(i).id);
													app.deleteNewFriendsChildData(list
															.get(i).id);
												}
											} else {
												app.updateNewFriendsData(
														list.get(i).oldId,
														list.get(i).id,
														Integer.parseInt(list
																.get(i).calendId),
														list.get(i).dataState);
												app.updateNewFriendsChildData(
														list.get(i).oldId,
														list.get(i).id,
														Integer.parseInt(list
																.get(i).calendId));
											}
										}
									}
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						DownLoadMessage();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("data", json);
				return map;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	public void onRefresh() {
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			String updatetime = sharedPrefUtil.getString(context,
					ShareFile.USERFILE, ShareFile.FRIENDUPDATETIME,
					DateUtil.formatDateTimeSs(new Date()));
			if (DateUtil.parseDateTimeSs(DateUtil.formatDateTimeSs(new Date()))
					.getTime() - DateUtil.parseDateTimeSs(updatetime).getTime() > 10000
					|| DateUtil.parseDateTimeSs(
							DateUtil.formatDateTimeSs(new Date())).getTime()
							- DateUtil.parseDateTimeSs(updatetime).getTime() == 0) {
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.FRIENDUPDATETIME,
						DateUtil.formatDateTimeSs(new Date()));
				UpdateLoadData();
				DownLoadMessage();
			}
		} else {
			loadData();
			adapter.notifyDataSetChanged();
			onLoad();
		}
	}

	@Override
	public void onLoadMore() {

	}

	private void onLoad() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date = format.format(new Date());
		SwipeXListViewNoHead swipeXListView = null;
		if (chongfu_lv.getVisibility() == View.VISIBLE) {
			swipeXListView = chongfu_lv;
		} else {
			// swipeXListView = myImpotent_listview;
		}
		swipeXListView.stopRefresh();
		swipeXListView.stopLoadMore();
		// swipeXListView.setRefreshTime("刚刚" + date);
	}

	/********************************* 点击详情对话框 *********************************************/
	/**
	 * @param mMap
	 */
	private void dialogDetailOnClick(Map<String, String> mMap) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_newchongfu, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
		dialog.show();

		new MyGeneralDetailOnClick(dialog, mMap, more_pop_menu);
	}

	class MyGeneralDetailOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private Map<String, String> map;
		private TextView bianji_tv;
		private TextView pause_tv;
		private TextView zhuanfafriends_tv;
		private TextView addmyrepeat_tv;
		private TextView cancle_tv;

		int position;
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();

		@SuppressLint("NewApi")
		public MyGeneralDetailOnClick(Dialog dialog, Map<String, String> mMap,
				View view) {
			this.dialog = dialog;
			this.map = mMap;
			this.view = view;
			initview();
		}

		private void initview() {
			bianji_tv = (TextView) view.findViewById(R.id.bianji_tv);
			bianji_tv.setOnClickListener(this);
			pause_tv = (TextView) view.findViewById(R.id.pause_tv);
			pause_tv.setOnClickListener(this);
			zhuanfafriends_tv = (TextView) view
					.findViewById(R.id.zhuanfafriends_tv);
			zhuanfafriends_tv.setOnClickListener(this);
			addmyrepeat_tv = (TextView) view.findViewById(R.id.addmyrepeat_tv);
			addmyrepeat_tv.setOnClickListener(this);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			cancle_tv.setOnClickListener(this);

			if ("0".equals(map.get(CLNFMessage.nfmIsPuase))) {
				pause_tv.setText("暂停");
			} else {
				pause_tv.setText("启动");
			}

		}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			NewFriendBean bean = new NewFriendBean();
			bean.nfmId = Integer.parseInt(map.get(CLNFMessage.nfmId));// =
																		// "nfmId";
																		// //Id
																		// of
																		// message.
			bean.nfmSendId = Integer.parseInt(map.get(CLNFMessage.nfmSendId));// =
																				// "nfmSendId";
																				// //Id
																				// of
																				// sender.
			bean.nfmGetId = Integer.parseInt(map.get(CLNFMessage.nfmGetId));// =
																			// "nfmGetId";
			bean.nfmCalendarId = Integer.parseInt(map
					.get(CLNFMessage.nfmCalendarId));// = "nfmCalendarId";
														// //Calendar's Id that
														// relation message
			bean.nfmOpenState = Integer.parseInt(map
					.get(CLNFMessage.nfmOpenState));// = "nfmOpenState"; //Open
													// state, 0 static | 1
													// public | 2 firend can
													// read.
			bean.nfmStatus = Integer.parseInt(map.get(CLNFMessage.nfmStatus));// =
																				// "nfmStatus";
																				// //Status
																				// of
																				// message,
																				// 0
																				// common
																				// |
																				// 1
																				// schedule
																				// |
																				// 2
																				// repeat
			bean.nfmIsAlarm = Integer.parseInt(map.get(CLNFMessage.nfmIsAlarm));// =
																				// "nfmIsAlarm";
																				// //是否响铃：0
																				// 无闹钟
																				// |
																				// 1
																				// 准时有闹钟
																				// 提前无闹钟
																				// |
																				// 2
																				// 准时无闹钟
																				// 提前有闹钟
																				// |
																				// 3
																				// 准时提前均有闹钟
			bean.nfmPostpone = Integer.parseInt(map
					.get(CLNFMessage.nfmPostpone));// = "nfmPostpone"; //自动顺延：0
													// 无顺延 | 1 顺延
			bean.nfmColorType = Integer.parseInt(map
					.get(CLNFMessage.nfmColorType));// = "nfmColorType"; //分类： 1
													// 工作 | 2生活 | 3其他
			bean.nfmDisplayTime = Integer.parseInt(map
					.get(CLNFMessage.nfmDisplayTime));// =
														// "nfmDisplayTime";//显示时间：0
														// 不显示 | 1显示
			bean.nfmBeforeTime = Integer.parseInt(map
					.get(CLNFMessage.nfmBeforeTime));// = "nfmBeforeTime";
														// //提前时间
			bean.nfmSourceType = Integer.parseInt(map
					.get(CLNFMessage.nfmSourceType));// = "nfmSourceType";
														// //链接类型：0 普通 | 1
														// 全链接（发现）| 2 ...
			bean.nfmType = Integer.parseInt(map.get(CLNFMessage.nfmType));// =
																			// "nfmType";
																			// //重复类型：1.每天
																			// |
																			// 2.每周
																			// |
																			// 3.每月
																			// |
																			// 4.每年
																			// |
																			// 5.工作日
			bean.nfmAType = Integer.parseInt(map.get(CLNFMessage.nfmAType));// =
																			// "nfmAType";
																			// //附加信息类型
																			// 0
																			// 没有附加信息
																			// |
																			// 1
																			// 附加链接|
																			// 2
																			// 附加图片
																			// |
																			// 3
																			// 附加链接和图片
			bean.nfmInSTable = Integer.parseInt(map
					.get(CLNFMessage.nfmInSTable));// = "nfmInSTable"; //是否生成子日程
													// 0 生成 | 1 不生成,default 0
			bean.nfmIsEnd = Integer.parseInt(map.get(CLNFMessage.nfmIsEnd));// =
																			// "nfmIsEnd";
																			// //是否完成：0.未完成
																			// |
																			// 1.完成
			bean.nfmDownState = Integer.parseInt(map
					.get(CLNFMessage.nfmDownState));// = "nfmDownState";
													// //是否下行：0.未下行 | 1.下行
			bean.nfmPostState = Integer.parseInt(map
					.get(CLNFMessage.nfmPostState));// = "nfmPostState";
													// //修改类型：0.普通 | 1.撤销 | 2.完成
													// | 3.修改 0 未结束 1 已结束
			bean.nfmUpdateState = Integer.parseInt(map
					.get(CLNFMessage.nfmUpdateState));// =
														// "nfmUpdateState";//上传状态：0.普通
														// | 1.新建 | 2.修改 | 3.删除
			bean.nfmPId = Integer.parseInt(map.get(CLNFMessage.nfmPId));// =
																		// "nfmPId";
																		// //父ID
			bean.nfmSubState = Integer.parseInt(map
					.get(CLNFMessage.nfmSubState));// = "nfmSubState";
													// //子记事状态：0.普通 | 1 脱钩 | 2
													// 删除 | 3 结束
			bean.nfmSubEnd = Integer.parseInt(map.get(CLNFMessage.nfmSubEnd));// =
																				// "nfmSubEnd";
																				// //对方子记事结束状态：0未结束
																				// |
																				// 1已结束
			bean.nfmCState = Integer.parseInt(map.get(CLNFMessage.nfmCState));// =
																				// "nfmCState";
																				// //修改对象：0
																				// 本身
																				// |
																				// 1
																				// 子记事

			/**
			 * 根据重复类型不同的参数 每天 每周 - 1、2、3...7 每月 - 1、2、3...31 每年 -
			 * 01-01、01-02、01-03...12-31
			 */
			bean.nfmParameter = map.get(CLNFMessage.nfmParameter);// =
																	// "nfmParameter";
			bean.nfmContent = map.get(CLNFMessage.nfmContent);// = "nfmContent";
																// //消息内容
			bean.nfmDate = map.get(CLNFMessage.nfmDate);// = "nfmDate"; //记事日期
			bean.nfmTime = map.get(CLNFMessage.nfmTime);// = "nfmTime"; //记事时间
			bean.nfmSourceDesc = map.get(CLNFMessage.nfmSourceDesc);// =
																	// "nfmSourceDesc";
																	// //链接
			bean.nfmSourceDescSpare = map.get(CLNFMessage.nfmSourceDescSpare);// =
																				// "nfmSourceDescSpare";
																				// //链接描述
			bean.nfmTags = map.get(CLNFMessage.nfmTags);// = "nfmTags"; //分类标记
			bean.nfmRingDesc = map.get(CLNFMessage.nfmRingDesc);// =
																// "nfmRingDesc";
																// //铃声中文名
			bean.nfmRingCode = map.get(CLNFMessage.nfmRingCode);// =
																// "nfmRingCode";
																// //铃声文件名
			bean.nfmStartDate = map.get(CLNFMessage.nfmStartDate);// =
																	// "nfmStartDate";
																	// //重复起始日期
			bean.nfmInitialCreatedTime = map
					.get(CLNFMessage.nfmInitialCreatedTime);// =
															// "nfmInitialCreatedTime";//母记事创建的时间
															// 格式 - yyyy-mm-dd
															// hh:mm
			bean.nfmLastCreatedTime = map.get(CLNFMessage.nfmLastCreatedTime);// =
																				// "nfmLastCreatedTime";
																				// //上一次已经生成子记事的时间
																				// 格式
																				// -
																				// yyyy-mm-dd
																				// hh:mm
																				// 无则为
																				// @“”
			bean.nfmNextCreatedTime = map.get(CLNFMessage.nfmNextCreatedTime);// =
																				// "nfmNextCreatedTime";
																				// //下一次已经生成子记事的时间
																				// 格式
																				// -
																				// yyyy-mm-dd
																				// hh:mm
			bean.nfmWebURL = map.get(CLNFMessage.nfmWebURL);// = "nfmWebURL";
															// //链接地址
			bean.nfmImagePath = map.get(CLNFMessage.nfmImagePath);// =
																	// "nfmImagePath";
																	// //图片地址
			bean.nfmSendName = map.get(CLNFMessage.nfmSendName);// =
																// "nfmSendName";
																// //发送者昵称
			bean.nfmSubDate = map.get(CLNFMessage.nfmSubDate);// = "nfmSubDate";
																// //子记事修改时间 格式
																// - yyyy-mm-dd
																// hh:mm
			bean.nfmRemark = map.get(CLNFMessage.nfmRemark);// = "nfmRemark";
															// //备注
			bean.nfmSubEndDate = map.get(CLNFMessage.nfmSubEndDate);// =
																	// "nfmSubEndDate";
																	// //子记事结束时间
			bean.nfmIsPuase = Integer.parseInt(map.get(CLNFMessage.nfmIsPuase));// =
			bean.parReamrk = map.get(CLNFMessage.parReamrk); // "nfmIsPuase";
			switch (v.getId()) {
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			case R.id.bianji_tv:
				intent = new Intent(context, EditNewFriendRepeatActivity.class);
				intent.putExtra("bean", bean);
				intent.putExtra("friendname", friendName);
				intent.putExtra("friendid", fid);
				startActivityForResult(intent, 100);
				dialog.dismiss();
				break;
			case R.id.pause_tv:
				setEndData(map, CLNFMessage.nfmIsPuase,
						CLNFMessage.nfmUpdateState);
				intent = new Intent(context, NewFriendDataUpLoadService.class);
				intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
				intent.setPackage(getPackageName());
				startService(intent);
				dialog.dismiss();
				break;
			case R.id.zhuanfafriends_tv:
				intent = new Intent(context, NewChongFuZhuanFaActivity.class);
				intent.putExtra("bean", bean);
				startActivity(intent);
				dialog.dismiss();
				break;
			case R.id.addmyrepeat_tv:
				boolean fag = app.insertCLRepeatTableData(bean.nfmBeforeTime,
						bean.nfmColorType, bean.nfmDisplayTime, bean.nfmType,
						bean.nfmIsAlarm, 0, 0, bean.nfmSourceType, 1,
						bean.nfmParameter, bean.nfmNextCreatedTime,
						bean.nfmLastCreatedTime,
						DateUtil.formatDateTime(new Date()), bean.nfmStartDate,
						bean.nfmContent, DateUtil.formatDateTime(new Date()),
						bean.nfmSourceDesc, bean.nfmSourceDescSpare,
						bean.nfmTime, bean.nfmRingDesc, bean.nfmRingCode, "",
						bean.nfmOpenState, "", 0, "", "", 0, 0, bean.nfmAType,
						bean.nfmWebURL, bean.nfmImagePath, 0, 0,
						bean.parReamrk, 0, 0);
				if (fag) {
					RepeatBean repeatBean = null;
					if ("1".equals(bean.nfmType)) {
						repeatBean = RepeatDateUtils.saveCalendar(bean.nfmTime,
								1, "", "");
					} else if ("2".equals(bean.nfmType)) {
						repeatBean = RepeatDateUtils.saveCalendar(
								bean.nfmTime,
								2,
								bean.nfmParameter.replace("[", "")
										.replace("]", "").replace("\n\"", "")
										.replace("\n", "").replace("\"", ""),
								"");
					} else if ("3".equals(bean.nfmType)) {
						repeatBean = RepeatDateUtils.saveCalendar(
								bean.nfmTime,
								3,
								bean.nfmParameter.replace("[", "")
										.replace("]", "").replace("\n\"", "")
										.replace("\n", "").replace("\"", ""),
								"");
					} else if ("4".equals(bean.nfmType)) {
						repeatBean = RepeatDateUtils.saveCalendar(
								bean.nfmTime,
								4,
								bean.nfmParameter.replace("[", "")
										.replace("]", "").replace("\n\"", "")
										.replace("\n", "").replace("\"", ""),
								"0");
					} else if ("6".equals(bean.nfmType)) {
						repeatBean = RepeatDateUtils.saveCalendar(
								bean.nfmTime,
								4,
								bean.nfmParameter.replace("[", "")
										.replace("]", "").replace("\n\"", "")
										.replace("\n", "").replace("\"", ""),
								"1");
					} else {
						repeatBean = RepeatDateUtils.saveCalendar(bean.nfmTime,
								5, "", "");
					}
					app.insertScheduleData(bean.nfmContent,
							repeatBean.repNextCreatedTime.substring(0, 10),
							bean.nfmTime, bean.nfmIsAlarm, bean.nfmBeforeTime,
							bean.nfmDisplayTime, bean.nfmPostpone, 0,
							bean.nfmColorType, 0,
							DateUtil.formatDateTime(new Date()), bean.nfmTags,
							bean.nfmSourceType, bean.nfmSourceDesc,
							bean.nfmSourceDescSpare, app.repschId,
							repeatBean.repNextCreatedTime.substring(0, 10), "",
							0, bean.nfmOpenState, 1, bean.nfmRingDesc,
							bean.nfmRingCode, "", 0, 0, bean.nfmAType,
							bean.nfmWebURL, bean.nfmImagePath, 0, 0, 0);
					alertDialog(0);
				} else {
					alertDialog(1);
				}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		App.getHttpQueues().cancelAll("down");
	}

	public void onEventMainThread(FristFragment event) {

		String msg = event.getMsg();
		if ("0".equals(msg)) {
			// loadData();
			// adapter.notifyDataSetChanged();
		}
	}

	private void setEndData(Map<String, String> mMap, String key, String key1) {
		try {
			String value = "0";
			Map<String, String> upMap = new HashMap<String, String>();
			if ("0".equals(mMap.get(key))) {
				value = "1";
				app.deleteNewFriendsChildData(Integer.parseInt(mMap
						.get("nfmId")));
			} else {
				value = "0";
				RepeatBean repeatBean = null;
				if ("1".equals(mMap.get(CLNFMessage.nfmType))) {
					repeatBean = RepeatDateUtils.saveCalendar(
							mMap.get(CLNFMessage.nfmTime), 1, "", "");
				} else if ("2".equals(mMap.get(CLNFMessage.nfmType))) {
					repeatBean = RepeatDateUtils.saveCalendar(
							mMap.get(CLNFMessage.nfmTime), 2,
							mMap.get(CLNFMessage.nfmParameter).replace("[", "")
									.replace("]", "").replace("\n\"", "")
									.replace("\n", "").replace("\"", ""), "");
				} else if ("3".equals(mMap.get(CLNFMessage.nfmType))) {
					repeatBean = RepeatDateUtils.saveCalendar(
							mMap.get(CLNFMessage.nfmTime), 3,
							mMap.get(CLNFMessage.nfmParameter).replace("[", "")
									.replace("]", "").replace("\n\"", "")
									.replace("\n", "").replace("\"", ""), "");
				} else if ("4".equals(mMap.get(CLNFMessage.nfmType))) {
					repeatBean = RepeatDateUtils.saveCalendar(
							mMap.get(CLNFMessage.nfmTime), 4,
							mMap.get(CLNFMessage.nfmParameter).replace("[", "")
									.replace("]", "").replace("\n\"", "")
									.replace("\n", "").replace("\"", ""), "0");
				} else if ("6".equals(mMap.get(CLNFMessage.nfmType))) {
					repeatBean = RepeatDateUtils.saveCalendar(
							mMap.get(CLNFMessage.nfmTime), 4,
							mMap.get(CLNFMessage.nfmParameter).replace("[", "")
									.replace("]", "").replace("\n\"", "")
									.replace("\n", "").replace("\"", ""), "1");
				} else {
					repeatBean = RepeatDateUtils.saveCalendar(
							mMap.get(CLNFMessage.nfmTime), 5, "", "");
				}
				app.insertMessageSendData(
						Integer.parseInt(mMap.get(CLNFMessage.nfmSendId)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmGetId)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmCalendarId)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmOpenState)),
						1, Integer.parseInt(mMap.get(CLNFMessage.nfmIsAlarm)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmPostpone)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmColorType)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmDisplayTime)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmBeforeTime)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmSourceType)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmType)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmAType)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmInSTable)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmIsEnd)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmDownState)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmPostState)),
						mMap.get(CLNFMessage.nfmParameter),
						mMap.get(CLNFMessage.nfmContent),
						repeatBean.repNextCreatedTime.substring(0, 10),
						mMap.get(CLNFMessage.nfmTime),
						mMap.get(CLNFMessage.nfmSourceDesc),
						mMap.get(CLNFMessage.nfmSourceDescSpare),
						mMap.get(CLNFMessage.nfmTags),
						mMap.get(CLNFMessage.nfmRingDesc),
						mMap.get(CLNFMessage.nfmRingCode),
						mMap.get(CLNFMessage.nfmStartDate),
						mMap.get(CLNFMessage.nfmInitialCreatedTime),
						repeatBean.repLastCreatedTime,
						repeatBean.repNextCreatedTime,
						mMap.get(CLNFMessage.nfmWebURL),
						mMap.get(CLNFMessage.nfmImagePath),
						mMap.get(CLNFMessage.nfmSendName),
						mMap.get(CLNFMessage.nfmRemark), 0,
						Integer.parseInt(mMap.get(CLNFMessage.nfmId)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmSubState)),
						mMap.get(CLNFMessage.nfmSubDate),
						Integer.parseInt(mMap.get(CLNFMessage.nfmCState)),
						Integer.parseInt(mMap.get(CLNFMessage.nfmSubEnd)),
						mMap.get(CLNFMessage.nfmSubEndDate), 0, mMap.get(CLNFMessage.parReamrk),
						DateUtil.formatDateTimeSs(new Date()));
			}
			upMap.put(key, value);
			upMap.put(key1, "2");
			app.updateNewFriendEndData(upMap,
					"where nfmId=" + mMap.get("nfmId"));
			loadData();
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
			delete_tv.setText("添加成功！");
			isNetWork();
		} else {
			delete_tv.setText("添加失败！");
		}
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});
	}

	private void isNetWork() {
		if (NetUtil.getConnectState(this) != NetWorkState.NONE) {
			Intent intent = new Intent(this, UpLoadService.class);
			intent.setAction(Const.SHUAXINDATA);
			intent.setPackage(getPackageName());
			startService(intent);
		} else {
			return;
		}
	}
	@AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
	private void checkPhonePermission() {
		String[] perms = { Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.ACCESS_FINE_LOCATION};
		autoFag = EasyPermissions.hasPermissions(this, perms);
		if (EasyPermissions.hasPermissions(this, perms)) {
			// Have permissions, do the thing!
			autoFag = true;
		} else {
			// Ask for both permissions
			EasyPermissions.requestPermissions(this, "该应用需要这些权限，为了保证应用正常运行!",
					RC_LOCATION_CONTACTS_PERM, perms);
		}
		sharedPrefUtil.putString(context,ShareFile.USERFILE,ShareFile.PERMISSIONSTATE,"1");
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkstorgePhonePermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.STORAGE);
//            if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
//            }
//            int checkcontactPermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.CONTACTS);
//            if(checkcontactPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.READ_CONTACTS},10001);
//            }
//            int checkLocationPermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.LOCATION);
//            if(checkLocationPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10001);
//            }
//            int checkPhoneStatePermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.LOCATION);
//            if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},10001);
//            }
//
//        }
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(PackageManager.PERMISSION_GRANTED==grantResults[3]){
			autoFag = true;
		}else{
			autoFag = false;
		}
		// EasyPermissions handles the request result.
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
