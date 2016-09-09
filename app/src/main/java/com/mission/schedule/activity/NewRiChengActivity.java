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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
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
import com.mission.schedule.adapter.NewRiChengAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewFriendBean;
import com.mission.schedule.bean.NewFriendRiChengBackBean;
import com.mission.schedule.bean.NewFriendRiChengBean;
import com.mission.schedule.bean.NewFriendRiChengBean1;
import com.mission.schedule.bean.UpdateNewFriendMessageBackBean;
import com.mission.schedule.bean.UpdateNewFriendMessageBean;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.service.NewFriendDataUpLoadService;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.IXListViewListener;
import com.mission.schedule.utils.AfterPermissionGranted;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.EasyPermissions;
import com.mission.schedule.utils.JsonParser;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.ReadTextContentXml.ReadWeiXinXml;
import com.mission.schedule.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class NewRiChengActivity extends BaseActivity implements
		OnClickListener, IXListViewListener,EasyPermissions.PermissionCallbacks {

	@ViewResId(id = R.id.richeng_listview)
	private SwipeXListViewNoHead richeng_listview;
	@ViewResId(id = R.id.yuyin_tv)
	private TextView yuyin_tv;
	@ViewResId(id = R.id.zidingyi_tv)
	private TextView zidingyi_tv;
//	@ViewResId(id = R.id.kuaijie_tv)
//	private TextView kuaijie_tv;
	@ViewResId(id = R.id.daiban_tv)
	private TextView daiban_tv;

	public static final int ALTER_RICHENG = 0;
	public static final int ZIDINGYI_RICHENG = 1;
	public static final int YUYIN_RICHENG = 2;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	int fid;
	int state;
	String userId;
	String userName;
	String friendName;
	String alltime;
	String ringdesc;
	String ringcode;

	String path;
	App app = null;
	List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
	String json = "";

	private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> unlist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> todaylist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> tomorrowlist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> inweeklist = new ArrayList<Map<String, String>>();
	List<Map<String, String>> outweeklist = new ArrayList<Map<String, String>>();
	int uncount = 0;// 今日待办个数
	int todaycount = 0;// 今日日程个数
	int tomorrowcount = 0;// 明天日程个数
	int inweekcount = 0;// 一周以内日程个数
	int outweekcount = 0;// 一周以外日程个数

	NewRiChengAdapter adapter = null;

	/**
	 * 语音相关
	 */
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private SharedPreferences mSharedPreferences;

	ProgressUtil progressUtil = new ProgressUtil();
	String downtime = "";

	public static List<UpdateNewFriendMessageBean> beans;

	private static final int RC_LOCATION_CONTACTS_PERM = 124;
	String  permissionState = "0";
	boolean autoFag = false;

	@Override
	protected void setListener() {
		yuyin_tv.setOnClickListener(this);
		zidingyi_tv.setOnClickListener(this);
//		kuaijie_tv.setOnClickListener(this);
		daiban_tv.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newricheng);
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
		alltime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ALLTIME, "08:58");
		ringdesc = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICDESC, "完成任务");
		ringcode = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.MUSICCODE, "g_88");
		downtime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.FRIENDDOWNSCHTIME, "");
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

		View footView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		richeng_listview.addFooterView(footView);

		richeng_listview.setPullRefreshEnable(true);
		richeng_listview.setPullLoadEnable(true);
		richeng_listview.setXListViewListener(this);
		richeng_listview.setFocusable(true);
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
		path = URLConstants.新版好友查询日程和以前信息 + userId + "&type=1&cpId=" + fid
				+ "&pageNum=1000&nowPage=1&downTime=" + downtime;
		NewSendMessageToFriendActivity.updateShunyanData();
		loadData();
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			DownLoadDataAsync(path);
		}
		// }
	}

	private void loadData() {
		try {
			unlist.clear();
			todaylist.clear();
			tomorrowlist.clear();
			inweeklist.clear();
			outweeklist.clear();
			mList.clear();
			unlist = app.queryAllLocalFriendsData(1, fid);
			todaylist = app.queryAllLocalFriendsData(2, fid);
			tomorrowlist = app.queryAllLocalFriendsData(3, fid);
			inweeklist = app.queryAllLocalFriendsData(4, fid);
			outweeklist = app.queryAllLocalFriendsData(5, fid);
			uncount = unlist.size();
			todaycount = todaylist.size();
			tomorrowcount = tomorrowlist.size();
			inweekcount = inweeklist.size();
			outweekcount = outweeklist.size();

			mList.addAll(unlist);
			mList.addAll(todaylist);
			mList.addAll(tomorrowlist);
			mList.addAll(inweeklist);
			mList.addAll(outweeklist);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setAdapter() {
		adapter = new NewRiChengAdapter(context, mList, handler,
				richeng_listview, uncount, todaycount, tomorrowcount,
				inweekcount, mScreenWidth);
		richeng_listview.setAdapter(adapter);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = null;
			Map<String, String> mMap = (Map<String, String>) msg.obj;
			int position = msg.arg1;
			switch (msg.what) {
			case 0:
				if (beans != null && beans.size() > 0) {
					for (int i = 0; i < beans.size(); i++) {
						if (beans.get(i).state == 0) {
							if (beans.get(i).dataState == 1
									|| beans.get(i).dataState == 2) {
								if (Integer.parseInt(mMap
										.get(CLNFMessage.nfmId)) == beans
										.get(i).oldId) {
									mMap.put(CLNFMessage.nfmId, beans.get(i).id
											+ "");
									break;
								}
							}
						} else {
							if (Integer.parseInt(mMap.get(CLNFMessage.nfmId)) == beans
									.get(i).oldId) {
								mMap.put(CLNFMessage.nfmId, beans.get(i).id
										+ "");
								break;
							}
						}
					}
				}
				dialogOnClick(mMap);
				break;
			case 3:
				try {
					if (beans != null && beans.size() > 0) {
						for (int i = 0; i < beans.size(); i++) {
							if (beans.get(i).state == 0) {
								if (beans.get(i).dataState == 1
										|| beans.get(i).dataState == 2) {
									if (Integer.parseInt(mMap
											.get(CLNFMessage.nfmId)) == beans
											.get(i).oldId) {
										mMap.put(CLNFMessage.nfmId,
												beans.get(i).id + "");
										break;
									}
								}
							} else {
								if (Integer.parseInt(mMap
										.get(CLNFMessage.nfmId)) == beans
										.get(i).oldId) {
									mMap.put(CLNFMessage.nfmId, beans.get(i).id
											+ "");
									break;
								}
							}
						}
					}
					String deleteId = mMap.get(CLNFMessage.nfmId);
					app.deleteNewFriendLocalData(Integer.parseInt(deleteId));
					richeng_listview.hiddenRight();
					if (("0".equals(mMap.get(CLNFMessage.nfmSubState)) || ("3"
							.equals(mMap.get(CLNFMessage.nfmSubState))))
							&& !"0".equals(mMap.get(CLNFMessage.nfmPId))) {
						// app.updateNewFriendChildData(
						// Integer.parseInt(deleteId),
						// 2,
						// mMap.get(CLNFMessage.nfmDate) + " "
						// + mMap.get(CLNFMessage.nfmTime), 3);
						app.alterNewFriendParentData1(
								Integer.parseInt(mMap.get(CLNFMessage.nfmPId)),
								2,
								mMap.get(CLNFMessage.nfmDate) + " "
										+ mMap.get(CLNFMessage.nfmTime));
					}
					mList.remove(position);
					adapter.notifyDataSetChanged();
					richeng_listview.invalidate();
					intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yuyin_tv:
			if(autoFag){
				HuaTongDialog();
			}else{
				Toast.makeText(context,"确实必要的权限!",Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.zidingyi_tv:
			state = 1;
			Intent intent = new Intent(context,
					NewEditFriendsMessageActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("friendId", fid);
			intent.putExtra("state", state);
			intent.putExtra("content", "");
			intent.putExtra("friendName", friendName);
			startActivityForResult(intent, ZIDINGYI_RICHENG);
			break;
//		case R.id.kuaijie_tv:
//			dialogKuaiJie();
//			break;
		case R.id.daiban_tv:
			dialogDaiBan();
			break;
		default:
			break;
		}
	}

	/**
	 * 快捷输入
	 */
//	private void dialogKuaiJie() {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_myschedit, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = getWindowManager().getDefaultDisplay().getWidth() - 20;
//		dialog.show();
//
//		new KuaiJieOnClick(dialog, more_pop_menu);
//	}
//
//	class KuaiJieOnClick {
//
//		private View view;
//		private Dialog dialog;
//		private EditText schcontent_et;
//		private TextView head_tv;
//
//		@SuppressLint("NewApi")
//		public KuaiJieOnClick(Dialog dialog, View view) {
//			this.dialog = dialog;
//			this.view = view;
//			initview();
//		}
//
//		private void initview() {
//			schcontent_et = (EditText) view.findViewById(R.id.schcontent_et);
//			schcontent_et.requestFocus();
//			dialog.getWindow().setSoftInputMode(
//					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//			view.findViewById(R.id.paste_tv).setOnClickListener(
//					new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							schcontent_et.setText(schcontent_et.getText()
//									.toString() + MainActivity.paste(context));
//							if (!"".equals(schcontent_et.getText().toString())) {
//								schcontent_et.setSelection(schcontent_et
//										.getText().toString().length());
//							}
//						}
//					});
//			head_tv = (TextView) view.findViewById(R.id.head_tv);
//			head_tv.setText("新建");
//			view.findViewById(R.id.close_ll).setOnClickListener(
//					new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							dialog.dismiss();
//						}
//					});
//			view.findViewById(R.id.save_ll).setOnClickListener(
//					new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							try {
//								if (!"".equals(schcontent_et.getText()
//										.toString().trim())) {
//									Map<Object, Object> map = ReadWeiXinXml
//											.yuyinSb(context, schcontent_et
//													.getText().toString());
//
//									if (map != null) {
//										String date = (String) map.get("date");
//										String time = (String) map.get("time");
//										if ("0".equals(map.get("di"))
//												&& "0".equals(map.get("ti"))) {
//											// t.setcDisplayAlarm(0);
//											// t.setcPostpone(1);
//											app.insertMessageSendData(
//													Integer.parseInt(userId),
//													fid,
//													0,
//													0,
//													1,
//													1,
//													1,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													"",
//													(String) map.get("value"),
//													date,
//													time,
//													"",
//													"",
//													"",
//													(String) map.get("ringVal"),
//													(String) map
//															.get("ringDesc"),
//													"",
//													"",
//													"",
//													"",
//													"",
//													"",
//													userName,
//													"",
//													1,
//													0,
//													0,
//													"",
//													0,
//													0,
//													"",
//													0,
//													"",
//													DateUtil.formatDateTimeSs(new Date()));
//										} else {
//											app.insertMessageSendData(
//													Integer.parseInt(userId),
//													fid,
//													0,
//													0,
//													1,
//													1,
//													0,
//													0,
//													1,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													0,
//													"",
//													(String) map.get("value"),
//													date,
//													time,
//													"",
//													"",
//													"",
//													(String) map.get("ringVal"),
//													(String) map
//															.get("ringDesc"),
//													"",
//													"",
//													"",
//													"",
//													"",
//													"",
//													userName,
//													"",
//													1,
//													0,
//													0,
//													"",
//													0,
//													0,
//													"",
//													0,
//													"",
//													DateUtil.formatDateTimeSs(new Date()));
//										}
//										UpdateLoadData();
//										loadData();
//										adapter.notifyDataSetChanged();
//									} else {
//									}
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							dialog.dismiss();
//						}
//
//					});
//			// 显示软键盘
//			schcontent_et.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI
//					| EditorInfo.IME_ACTION_DONE);
//		}
//	}

	/**
	 * 新建待办
	 */
	private void dialogDaiBan() {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_myschedit, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 20;
		dialog.show();

		new DaiBanOnClick(dialog, more_pop_menu);
	}

	class DaiBanOnClick {

		private View view;
		private Dialog dialog;
		private EditText schcontent_et;
		private TextView head_tv;

		@SuppressLint("NewApi")
		public DaiBanOnClick(Dialog dialog, View view) {
			this.dialog = dialog;
			this.view = view;
			initview();
		}

		private void initview() {
			schcontent_et = (EditText) view.findViewById(R.id.schcontent_et);
			schcontent_et.requestFocus();
			dialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			view.findViewById(R.id.paste_tv).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							schcontent_et.setText(schcontent_et.getText()
									.toString() + MainActivity.paste(context));
							if (!"".equals(schcontent_et.getText().toString())) {
								schcontent_et.setSelection(schcontent_et
										.getText().toString().length());
							}
						}
					});
			head_tv = (TextView) view.findViewById(R.id.head_tv);
			head_tv.setText("新建待办");
			view.findViewById(R.id.close_ll).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
			view.findViewById(R.id.save_ll).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							try {
								if (!"".equals(schcontent_et.getText()
										.toString().trim())) {
									app.insertMessageSendData(
											Integer.parseInt(userId),
											fid,
											0,
											0,
											1,
											1,
											1,
											0,
											0,
											0,
											0,
											0,
											0,
											0,
											0,
											0,
											0,
											"",
											schcontent_et.getEditableText()
													.toString(),
											DateUtil.formatDate(new Date()),
											alltime,
											"",
											"",
											"",
											ringdesc,
											ringcode,
											"",
											"",
											"",
											"",
											"",
											"",
											userName,
											"",
											1,
											0,
											0,
											"",
											0,
											0,
											"",
											0,
											"",
											DateUtil.formatDateTimeSs(new Date()));
									UpdateLoadData();
									loadData();
									adapter.notifyDataSetChanged();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							dialog.dismiss();
						}
					});
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
								NewFriendRiChengBean1 bean1 = null;
								List<NewFriendRiChengBean> beans = null;
								Gson gson = new Gson();
								NewFriendRiChengBackBean backBean = gson
										.fromJson(result,
												NewFriendRiChengBackBean.class);
								sharedPrefUtil.putString(context,
										ShareFile.USERFILE,
										ShareFile.FRIENDDOWNSCHTIME,
										backBean.downTime.replace("T", " "));
								if (backBean.status == 0) {
									bean1 = backBean.page;
									if (bean1 != null) {
										beans = bean1.items;
										for (int i = 0; i < beans.size(); i++) {
											if (app.CheckCountNewFriendData(Integer
													.parseInt(beans.get(i).id)) != 0) {
												app.alterNewFriendsData1(
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
														1,
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
														0,
														Integer.parseInt(beans
																.get(i).schAType),
														Integer.parseInt(beans
																.get(i).repInSTable),
														beans.get(i).isEnd,
														Integer.parseInt(beans
																.get(i).downstate),
														Integer.parseInt(beans
																.get(i).poststate),
														"",
														beans.get(i).schContent,
														beans.get(i).schDate,
														beans.get(i).schctime,
														beans.get(i).cTypeDesc,
														beans.get(i).cTypeSpare,
														"",
														beans.get(i).CAlarmsoundDesc,
														beans.get(i).CAlarmsound,
														beans.get(i).repstartdate,
														beans.get(i).repinitialcreatedtime,
														beans.get(i).replastcreatedtime,
														beans.get(i).repnextcreatedtime,
														beans.get(i).schWebURL,
														beans.get(i).schImagePath,
														beans.get(i).uName,
														beans.get(i).remark, 0,
														Integer.parseInt(beans
																.get(i).pId),
														0, "", 0, 0, "", 0, "");
											} else {
												app.insertIntnetMessageSendData(
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
														1,
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
														0,
														Integer.parseInt(beans
																.get(i).schAType),
														Integer.parseInt(beans
																.get(i).repInSTable),
														beans.get(i).isEnd,
														Integer.parseInt(beans
																.get(i).downstate),
														Integer.parseInt(beans
																.get(i).poststate),
														"",
														beans.get(i).schContent,
														beans.get(i).schDate,
														beans.get(i).schctime,
														beans.get(i).cTypeDesc,
														beans.get(i).cTypeSpare,
														"",
														beans.get(i).CAlarmsoundDesc,
														beans.get(i).CAlarmsound,
														beans.get(i).repstartdate,
														beans.get(i).repinitialcreatedtime,
														beans.get(i).replastcreatedtime,
														beans.get(i).repnextcreatedtime,
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
														0,
														"",
														DateUtil.formatDateTimeSs(new Date()));
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
				// DownLoadMessage1();
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
												} else if (list.get(i).dataState == 3) {
													app.deleteNewFriendsData(list
															.get(i).id);
												}
											} else {
												app.updateNewFriendsData(
														list.get(i).oldId,
														list.get(i).id,
														Integer.parseInt(list
																.get(i).calendId),
														list.get(i).dataState);
											}
										}
										loadData();
										adapter.notifyDataSetChanged();
									}
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						DownLoadMessage1();
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
				DownLoadMessage1();
			}else{
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

	@Override
	public void onLoadMore() {

	}

	private void onLoad() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date = format.format(new Date());
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

	/**
	 * 讯飞语音
	 */
	/**
	 * 话筒对话框
	 */
	Dialog huatongdialog = null;
	// private GestureDetector mGestureDetector;
	Button yuyin;

	private void HuaTongDialog() {
		// final AlertDialog builder = new AlertDialog.Builder(getActivity())
		// .create();
		// builder.show();
		// Window window = builder.getWindow();
		// android.view.WindowManager.LayoutParams params =
		// window.getAttributes();// 获取LayoutParams
		// params.alpha = 0.92f;
		// window.setAttributes(params);// 设置生效
		// window.setContentView(R.layout.dialog_huatong);
		// Button yuyin = (Button) window.findViewById(R.id.yuyin);
		// yuyin.setOnLongClickListener(new PicOnLongClick());

		huatongdialog = new Dialog(context, R.style.dialog_huatong);
		Window window = huatongdialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_huatong, null);
		yuyin = (Button) more_pop_menu.findViewById(R.id.yuyin);
		LinearLayout yuyin_ll = (LinearLayout) more_pop_menu
				.findViewById(R.id.yuyin_ll);
		huatongdialog.setCanceledOnTouchOutside(true);
		huatongdialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		// yuyin.setOnLongClickListener(new PicOnLongClick());
		// mGestureDetector = new GestureDetector(getActivity(),
		// new MyOnGestureListener());
		yuyin_ll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					yuyin.setBackgroundDrawable(getResources().getDrawable(
							R.mipmap.btn_yuyina));
					xunfeiRecognizer();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					yuyin.setBackgroundDrawable(getResources().getDrawable(
							R.mipmap.btn_yuyinb));
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
					yuyin.setBackgroundDrawable(getResources().getDrawable(
							R.mipmap.btn_yuyina));
					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
						xunfeiRecognizer();
					} else {
						alertFailDialog();
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					yuyin.setBackgroundDrawable(getResources().getDrawable(
							R.mipmap.btn_yuyinb));
					mIat.stopListening();
					huatongdialog.dismiss();
				}
				// mGestureDetector.onTouchEvent(event);
				return true;
			}

		});
		huatongdialog.show();
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
	String mycontent = "";
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
		System.out.println("******************++>>" + text);
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

	private void sendMessageDialog(String content) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		params.y = 150;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_sendmessagedialog,
				null);
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
		private String content1;
		private String alamsound;
		private String alamsoundDesc;
		private LinearLayout detail_edit;
		private LinearLayout detail_close;
		private Button suresend_bt;
		private TextView time_date;
		private TextView year_date;
		private TextView shunyan_tv;
		private TextView content_tv;
		private TextView timebefore_tv;
		private TextView week_date;
		Map<Object, Object> map;
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();
		int displaytime, postpone;
		String date;

		public SendMessageDetailOnClick(Dialog dialog, View view, String content) {
			this.mainView = view;
			this.dialog = dialog;
			this.content1 = content;
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
			time_date = (TextView) mainView.findViewById(R.id.time_date);
			year_date = (TextView) mainView.findViewById(R.id.year_date);
			shunyan_tv = (TextView) mainView.findViewById(R.id.shunyan_tv);
			content_tv = (TextView) mainView.findViewById(R.id.content_tv);
			timebefore_tv = (TextView) mainView
					.findViewById(R.id.timebefore_tv);
			week_date = (TextView) mainView.findViewById(R.id.week_date);
			map = ReadWeiXinXml.yuyinSb(context, content1);
		}

		public void initdata() {
			if ("0".equals(map.get("di")) && "0".equals(map.get("ti"))) {
				displaytime = 0;
				postpone = 1;
			} else {
				displaytime = 1;
				postpone = 0;
			}
			alamsound = (String) map.get("ringDesc");
			alamsoundDesc = (String) map.get("ringVal");
			content1 = (String) map.get("value");

			calendar.setTime(new Date());
			today = DateUtil.formatDate(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 1);
			tomorrow = DateUtil.formatDate(calendar.getTime());

			content_tv.setText(content1);
			date = DateUtil.formatDate(DateUtil.parseDate((String) map
					.get("date")));
			if (today.equals(date)) {
				year_date.setText(date);
			} else if (tomorrow.equals(date)) {
				year_date.setText("明天");
			} else {
				year_date.setText(date);
			}
			String timestr = "";
			if (displaytime == 0) {
				time_date.setText("全天");
				timestr = sharedPrefUtil.getString(context, ShareFile.USERFILE,
						ShareFile.ALLTIME, "08:58");
			} else {
				time_date.setText((String) map.get("time"));
				timestr = time_date.getText().toString();
			}
			week_date.setText(CharacterUtil.getWeekOfDate(context,
					DateUtil.parseDate(date)));
			String colorState = ""
					+ context.getResources().getColor(R.color.mingtian_color);
			String sequence = "<font color='" + colorState + "'>"
					+ context.getString(R.string.adapter_shun) + "</font>";

			shunyan_tv.setBackgroundResource(R.drawable.tv_kuang_aftertime);
			shunyan_tv.setText(Html.fromHtml(sequence));
			if (0 == postpone) {
				shunyan_tv.setVisibility(View.GONE);
			} else {
				shunyan_tv.setVisibility(View.VISIBLE);
			}
			Date dateStr = DateUtil.parseDateTime(date + " " + timestr);
			Date dateToday = DateUtil.parseDateTime(DateUtil
					.formatDateTime(new Date()));
			long betweem = (long) (dateStr.getTime() - dateToday.getTime()) / 1000;
			long day = betweem / (24 * 3600);
			long hour = betweem % (24 * 3600) / 3600;
			long min = betweem % 3600 / 60;

			if (today.equals(date)) {// 今天
				if (displaytime == 0 && postpone == 1) {
					timebefore_tv.setText("今天");
				} else {
					if (DateUtil.parseDate(DateUtil.formatDate(new Date()))
							.after(DateUtil.parseDate(DateUtil
									.formatDate(dateStr)))) {
						if (Math.abs(hour) >= 1) {
							timebefore_tv.setText(Math.abs(hour) + "小时前");
						} else {
							timebefore_tv.setText(Math.abs(min) + "分钟前");
						}
					} else {
						if (Math.abs(hour) >= 1) {
							timebefore_tv.setText(Math.abs(hour) + "小时后");
						} else {
							timebefore_tv.setText(Math.abs(min) + "分钟后");
						}
					}
				}
			} else if (tomorrow.equals(date)) {// 明天
				if (Math.abs(day) >= 1) {
					timebefore_tv.setText(Math.abs(day) + "天后");
				} else {
					timebefore_tv.setText(Math.abs(hour) + "小时后");
				}
			} else {
				timebefore_tv.setText(Math.abs(day) + 1 + "天后");
			}

		}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.detail_edit:
				if (!"".equals(content_tv.getText().toString().trim())) {
					intent = new Intent(context, EditSendMessageActivity.class);
					intent.putExtra("userId", userId);
					intent.putExtra("friendId", fid);
					intent.putExtra("content", content1);
					intent.putExtra("state", 1);
					intent.putExtra("friendName",
							NewSendMessageToFriendActivity.friendsBean.uName);
					intent.putExtra("displaytime", displaytime + "");
					intent.putExtra("postpone", postpone + "");
					intent.putExtra("ringdesc", alamsoundDesc);
					intent.putExtra("ringcode", alamsound);
					intent.putExtra("date", date);
					intent.putExtra("time", (String) map.get("time"));
					startActivityForResult(intent, YUYIN_RICHENG);
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
					String before = sharedPrefUtil.getString(context,
							ShareFile.USERFILE, ShareFile.BEFORETIME, "0");
					app.insertMessageSendData(Integer.parseInt(userId), fid, 0,
							0, 1, 1, postpone, 0, displaytime,
							Integer.parseInt(before), 0, 0, 0, 0, 0, 0, 0, "",
							content1, date, (String) map.get("time"), "", "",
							"", alamsoundDesc, alamsound, "", "", "", "", "",
							"", userName, "", 1, 0, 0, "", 0, 0, "", 0, "",
							DateUtil.formatDateTimeSs(new Date()));
					UpdateLoadData();
					loadData();
					adapter.notifyDataSetChanged();
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

	/*********************************** 点击详情弹出对话框 **********************************************/
	private void dialogOnClick(Map<String, String> map) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_newricheng, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
		dialog.show();

		new MyGeneralOnClick(dialog, more_pop_menu, map);
	}

	class MyGeneralOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private LinearLayout detail;
		private TextView edit_tv;
		private TextView tuihou_tv;
		private TextView end_tv;
		private TextView zhuanfa_tv;
		private TextView addmysch_tv;
		private TextView cancle_tv;
		private LinearLayout after;// 推后整体
		private TextView after_1;// 自动顺延
		private TextView after_2;// 推后一小时
		private TextView after_3;// 推后一天
		private TextView after_4;// 推后一周
		private TextView after_5;// 推后一个月
		private TextView after_6;// 推到下周一
		private TextView cancle1_tv;// 取消

		Map<String, String> map = null;
		String today, tomorrow;
		Calendar calendar = Calendar.getInstance();

		@SuppressLint("NewApi")
		public MyGeneralOnClick(Dialog dialog, View view,
				Map<String, String> map) {
			this.dialog = dialog;
			this.view = view;
			this.map = map;
			initview();
			initdata();
		}

		private void initview() {
			detail = (LinearLayout) view.findViewById(R.id.detail);
			detail.setOnClickListener(this);
			edit_tv = (TextView) view.findViewById(R.id.edit_tv);
			edit_tv.setOnClickListener(this);
			tuihou_tv = (TextView) view.findViewById(R.id.tuihou_tv);
			tuihou_tv.setOnClickListener(this);
			end_tv = (TextView) view.findViewById(R.id.end_tv);
			end_tv.setOnClickListener(this);
			zhuanfa_tv = (TextView) view.findViewById(R.id.zhuanfa_tv);
			zhuanfa_tv.setOnClickListener(this);
			addmysch_tv = (TextView) view.findViewById(R.id.addmysch_tv);
			addmysch_tv.setOnClickListener(this);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			cancle_tv.setOnClickListener(this);
			after = (LinearLayout) view.findViewById(R.id.after);// 推后整体
			after.setOnClickListener(this);
			after_1 = (TextView) view.findViewById(R.id.after_1);// 自动顺延
			after_1.setOnClickListener(this);
			after_2 = (TextView) view.findViewById(R.id.after_2);// 推后一小时
			after_2.setOnClickListener(this);
			after_3 = (TextView) view.findViewById(R.id.after_3);// 推后一天
			after_3.setOnClickListener(this);
			after_4 = (TextView) view.findViewById(R.id.after_4);// 推后一周
			after_4.setOnClickListener(this);
			after_5 = (TextView) view.findViewById(R.id.after_5);// 推后一个月
			after_5.setOnClickListener(this);
			after_6 = (TextView) view.findViewById(R.id.after_6);// 推到下周一
			after_6.setOnClickListener(this);
			cancle1_tv = (TextView) view.findViewById(R.id.cancle1_tv);// 取消
			cancle1_tv.setOnClickListener(this);
		}

		private void initdata() {
			calendar.setTime(new Date());
			today = DateUtil.formatDate(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 1);
			tomorrow = DateUtil.formatDate(calendar.getTime());

			if ("0".equals(map.get(CLNFMessage.nfmPostState))) {
				end_tv.setText("设为结束");
			} else {
				end_tv.setText("取消结束");
			}
			if ("0".equals(map.get(CLNFMessage.nfmPostpone))) {
				after_1.setText("自动顺延");
			} else {
				after_1.setText("取消顺延");
			}
		}

		@Override
		public void onClick(View v) {
			Animation translateIn0 = new TranslateAnimation(-view.getWidth(),
					0, 0, 0);
			Animation translateIn1 = new TranslateAnimation(view.getWidth(), 0,
					0, 0);
			translateIn0.setDuration(400);
			translateIn1.setDuration(400);
			String date;
			int id = Integer.parseInt(map.get(CLNFMessage.nfmId));
			long datetime = 0;
			datetime = DateUtil.parseDateTime(
					map.get(CLNFMessage.nfmDate) + " "
							+ map.get(CLNFMessage.nfmTime)).getTime();
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
																				// "nfmIsPuase";
			Map<String, String> upMap = new HashMap<String, String>();
			Map<String, String> upMap1 = new HashMap<String, String>();
			Map<String, String> upMap2 = new HashMap<String, String>();
			switch (v.getId()) {
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			case R.id.edit_tv:
				intent = new Intent(context, EditNewFriendRiChengActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("friendId", fid);
				intent.putExtra("friendName",
						NewSendMessageToFriendActivity.friendsBean.uName);
				intent.putExtra("bean", bean);
				startActivityForResult(intent, ALTER_RICHENG);
				dialog.dismiss();
				break;
			case R.id.tuihou_tv:
				hint();
				after.setVisibility(View.VISIBLE);
				after.startAnimation(translateIn1);
				break;
			case R.id.end_tv:
				setEndData(map, CLNFMessage.nfmPostState,
						CLNFMessage.nfmUpdateState);
				dialog.dismiss();
				break;
			case R.id.zhuanfa_tv:
				intent = new Intent(context,
						NewFriendRiChengZhuanFaActivity.class);
				intent.putExtra("bean", bean);
				startActivity(intent);
				dialog.dismiss();
				break;
			case R.id.addmysch_tv:
				boolean fag = app.insertScheduleData(
						map.get(CLNFMessage.nfmContent),
						map.get(CLNFMessage.nfmDate),
						map.get(CLNFMessage.nfmTime),
						Integer.parseInt(map.get(CLNFMessage.nfmIsAlarm)),
						Integer.parseInt(map.get(CLNFMessage.nfmBeforeTime)),
						Integer.parseInt(map.get(CLNFMessage.nfmDisplayTime)),
						Integer.parseInt(map.get(CLNFMessage.nfmPostpone)), 0,
						0, Integer.parseInt(map.get(CLNFMessage.nfmPostState)),
						DateUtil.formatDateTime(new Date()), "",
						Integer.parseInt(map.get(CLNFMessage.nfmSourceType)),
						map.get(CLNFMessage.nfmSourceDesc),
						map.get(CLNFMessage.nfmSourceDescSpare), 0, "",
						DateUtil.formatDateTime(new Date()), 1,
						Integer.parseInt(map.get(CLNFMessage.nfmOpenState)), 0,
						map.get(CLNFMessage.nfmRingDesc),
						map.get(CLNFMessage.nfmRingCode), "", 0, 0,
						Integer.parseInt(map.get(CLNFMessage.nfmAType)),
						map.get(CLNFMessage.nfmWebURL),
						map.get(CLNFMessage.nfmImagePath), 0, 0, 0);
				if (fag) {
					alertDialog(0);
				} else {
					alertDialog(1);
				}
				dialog.dismiss();
				break;
			case R.id.after_1:
				try {
					setpostponeData(map, CLNFMessage.nfmPostpone,
							CLNFMessage.nfmUpdateState);
					calendar.setTime(DateUtil.parseDate(map
							.get(CLNFMessage.nfmDate)));
					if (DateUtil.parseDate(map.get(CLNFMessage.nfmDate))
							.before(DateUtil.parseDate(DateUtil
									.formatDate(new Date())))) {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 1);
						date = DateUtil.formatDate(calendar.getTime());
						if (!"0".equals(map.get(CLNFMessage.nfmPId))
								&& ("0".equals(map.get(CLNFMessage.nfmSubState)) || "3"
										.equals(map
												.get(CLNFMessage.nfmSubState)))) {
							app.updateNewFriendDateData(id, date,
									map.get(CLNFMessage.nfmTime), 1);
						} else {
							app.updateNewFriendDateData(id, date,
									map.get(CLNFMessage.nfmTime), 2);
						}
					} else {
						if (!"0".equals(map.get(CLNFMessage.nfmPId))
								&& ("0".equals(map.get(CLNFMessage.nfmSubState)) || "3"
										.equals(map
												.get(CLNFMessage.nfmSubState)))) {
							app.updateNewFriendDateData(id,
									map.get(CLNFMessage.nfmDate),
									map.get(CLNFMessage.nfmTime), 1);
						} else {
							app.updateNewFriendDateData(id,
									map.get(CLNFMessage.nfmDate),
									map.get(CLNFMessage.nfmTime), 2);
						}
					}
					loadData();
					adapter.notifyDataSetChanged();
					intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_2://
				try {
					date = DateUtil.formatDateTime(datetime + 60 * 60 * 1000);

					if (!"0".equals(map.get(CLNFMessage.nfmPId))
							&& ("0".equals(map.get(CLNFMessage.nfmSubState)) || "3"
									.equals(map.get(CLNFMessage.nfmSubState)))) {
						app.updateNewFriendDateData(id, date.substring(0, 10)
								.toString(), date.substring(11, 16).toString(),
								1);
						upMap.put("nfmSubState", "1");
						upMap.put("nfmSubDate", map.get(CLNFMessage.nfmDate)
								+ " " + map.get(CLNFMessage.nfmTime));
						app.updateNewFriendEndData(upMap,
								"where nfmId=" + map.get(CLNFMessage.nfmPId));
						upMap2.put(CLNFMessage.nfmUpdateState, "2");
						app.updateNewFriendEndData1(upMap2, "where nfmId="
								+ map.get(CLNFMessage.nfmPId));
						upMap1.put(CLNFMessage.nfmPId, "0");
						app.updateNewFriendEndData1(upMap1, "where nfmId="
								+ map.get("nfmId"));
					} else {
						app.updateNewFriendDateData(id, date.substring(0, 10)
								.toString(), date.substring(11, 16).toString(),
								2);
					}
					loadData();
					adapter.notifyDataSetChanged();
					intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_3:
				try {
					calendar.setTime(DateUtil.parseDate(map
							.get(CLNFMessage.nfmDate)));
					calendar.set(Calendar.DAY_OF_MONTH,
							calendar.get(Calendar.DAY_OF_MONTH) + 1);
					if (!"0".equals(map.get(CLNFMessage.nfmPId))
							&& ("0".equals(map.get(CLNFMessage.nfmSubState)) || "3"
									.equals(map.get(CLNFMessage.nfmSubState)))) {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 1);
						upMap.put("nfmSubState", "1");
						upMap.put("nfmSubDate", map.get(CLNFMessage.nfmDate)
								+ " " + map.get(CLNFMessage.nfmTime));
						app.updateNewFriendEndData(upMap,
								"where nfmId=" + map.get(CLNFMessage.nfmPId));
						upMap2.put(CLNFMessage.nfmUpdateState, "2");
						app.updateNewFriendEndData1(upMap2, "where nfmId="
								+ map.get(CLNFMessage.nfmPId));
						upMap1.put(CLNFMessage.nfmPId, "0");
						app.updateNewFriendEndData1(upMap1, "where nfmId="
								+ map.get("nfmId"));
					} else {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 2);
					}
					loadData();
					adapter.notifyDataSetChanged();
					intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_4:
				try {
					calendar.setTime(DateUtil.parseDate(map
							.get(CLNFMessage.nfmDate)));
					calendar.set(Calendar.DAY_OF_MONTH,
							calendar.get(Calendar.DAY_OF_MONTH) + 7);
					if (!"0".equals(map.get(CLNFMessage.nfmPId))
							&& ("0".equals(map.get(CLNFMessage.nfmSubState)) || "3"
									.equals(map.get(CLNFMessage.nfmSubState)))) {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 1);
						upMap.put("nfmSubState", "1");
						upMap.put("nfmSubDate", map.get(CLNFMessage.nfmDate)
								+ " " + map.get(CLNFMessage.nfmTime));
						app.updateNewFriendEndData(upMap,
								"where nfmId=" + map.get(CLNFMessage.nfmPId));
						upMap2.put(CLNFMessage.nfmUpdateState, "2");
						app.updateNewFriendEndData1(upMap2, "where nfmId="
								+ map.get(CLNFMessage.nfmPId));
						upMap1.put(CLNFMessage.nfmPId, "0");
						app.updateNewFriendEndData1(upMap1, "where nfmId="
								+ map.get("nfmId"));
					} else {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 2);
					}
					loadData();
					adapter.notifyDataSetChanged();
					intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_5:
				try {
					calendar.setTime(DateUtil.parseDate(map
							.get(CLNFMessage.nfmDate)));
					calendar.set(Calendar.DAY_OF_MONTH,
							calendar.get(Calendar.DAY_OF_MONTH) + 30);
					if (!"0".equals(map.get(CLNFMessage.nfmPId))
							&& ("0".equals(map.get(CLNFMessage.nfmSubState)) || "3"
									.equals(map.get(CLNFMessage.nfmSubState)))) {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 1);
						upMap.put("nfmSubState", "1");
						upMap.put("nfmSubDate", map.get(CLNFMessage.nfmDate)
								+ " " + map.get(CLNFMessage.nfmTime));
						app.updateNewFriendEndData(upMap,
								"where nfmId=" + map.get(CLNFMessage.nfmPId));
						upMap2.put(CLNFMessage.nfmUpdateState, "2");
						app.updateNewFriendEndData1(upMap2, "where nfmId="
								+ map.get(CLNFMessage.nfmPId));
						upMap1.put(CLNFMessage.nfmPId, "0");
						app.updateNewFriendEndData1(upMap1, "where nfmId="
								+ map.get("nfmId"));
					} else {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 2);
					}
					loadData();
					adapter.notifyDataSetChanged();
					intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.after_6:
				try {
					String week = CharacterUtil.getWeekOfDate(context,
							DateUtil.parseDate(map.get(CLNFMessage.nfmDate)));
					calendar.setTime(DateUtil.parseDate(map
							.get(CLNFMessage.nfmDate)));
					if ("周一".equals(week)) {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 7);
					} else if ("周二".equals(week)) {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 6);
					} else if ("周三".equals(week)) {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 5);
					} else if ("周四".equals(week)) {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 4);
					} else if ("周五".equals(week)) {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 3);
					} else if ("周六".equals(week)) {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 2);
					} else {
						calendar.set(Calendar.DAY_OF_MONTH,
								calendar.get(Calendar.DAY_OF_MONTH) + 1);
					}
					if (!"0".equals(map.get(CLNFMessage.nfmPId))
							&& ("0".equals(map.get(CLNFMessage.nfmSubState)) || "3"
									.equals(map.get(CLNFMessage.nfmSubState)))) {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 1);
						upMap.put("nfmSubState", "1");
						upMap.put("nfmSubDate", map.get(CLNFMessage.nfmDate)
								+ " " + map.get(CLNFMessage.nfmTime));
						app.updateNewFriendEndData(upMap,
								"where nfmId=" + map.get(CLNFMessage.nfmPId));
						upMap2.put(CLNFMessage.nfmUpdateState, "2");
						app.updateNewFriendEndData1(upMap2, "where nfmId="
								+ map.get(CLNFMessage.nfmPId));
						upMap1.put(CLNFMessage.nfmPId, "0");
						app.updateNewFriendEndData1(upMap1, "where nfmId="
								+ map.get("nfmId"));
					} else {
						app.updateNewFriendDateData(id,
								DateUtil.formatDate(calendar.getTime()),
								map.get(CLNFMessage.nfmTime), 2);
					}
					loadData();
					adapter.notifyDataSetChanged();
					intent = new Intent(context,
							NewFriendDataUpLoadService.class);
					intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
					intent.setPackage(getPackageName());
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				break;
			case R.id.cancle1_tv:
				hint();
				detail.setVisibility(View.VISIBLE);
				detail.startAnimation(translateIn0);
				break;
			default:
				break;
			}
		}

		private void hint() {
			after.setVisibility(View.GONE);
			detail.setVisibility(View.GONE);
		}
	}

	private void setEndData(Map<String, String> mMap, String key, String key1) {
		try {
			String value = "0";
			Map<String, String> upMap = new HashMap<String, String>();
			Map<String, String> upMap1 = new HashMap<String, String>();

			String value1 = "0";
			if (!"0".equals(mMap.get(CLNFMessage.nfmPId))
					&& ("0".equals(mMap.get(CLNFMessage.nfmSubState)) || "3"
							.equals(mMap.get(CLNFMessage.nfmSubState)))) {
				if ("0".equals(mMap.get(key)))
					value = "1";
				else
					value = "0";
				upMap.put(key, value);
				app.updateNewFriendEndData1(upMap,
						"where nfmId=" + mMap.get("nfmId"));
				if ("0".equals(mMap.get(CLNFMessage.nfmSubState))) {
					value1 = "3";
					app.alterNewFriendParentData1(
							Integer.parseInt(mMap.get(CLNFMessage.nfmPId)),
							3,
							mMap.get(CLNFMessage.nfmDate) + " "
									+ mMap.get(CLNFMessage.nfmTime));
					upMap1.put("nfmSubDate", mMap.get(CLNFMessage.nfmDate)
							+ " " + mMap.get(CLNFMessage.nfmTime));
				} else {
					value1 = "0";
					app.alterNewFriendParentData1(
							Integer.parseInt(mMap.get(CLNFMessage.nfmPId)), 0,
							"");
					upMap1.put("nfmSubDate", "");
				}
				upMap1.put("nfmSubState", value1);
				app.updateNewFriendEndData(upMap1,
						"where nfmId=" + mMap.get("nfmId"));
			} else {
				if ("0".equals(mMap.get(key)))
					value = "1";
				else
					value = "0";
				upMap.put(key, value);
				upMap.put(key1, "2");
				app.updateNewFriendEndData(upMap,
						"where nfmId=" + mMap.get("nfmId"));
			}
			loadData();
			adapter.notifyDataSetChanged();
			Intent intent = new Intent(context,
					NewFriendDataUpLoadService.class);
			intent.setAction(NewFriendDataUpLoadService.FRIENDDATA);
			intent.setPackage(getPackageName());
			startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setpostponeData(Map<String, String> mMap, String key,
			String key1) {
		try {
			String value = "0";
			Map<String, String> upMap = new HashMap<String, String>();
			Map<String, String> upMap1 = new HashMap<String, String>();
			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> map2 = new HashMap<String, String>();

			if (!"0".equals(mMap.get(CLNFMessage.nfmPId))
					&& ("0".equals(mMap.get(CLNFMessage.nfmSubState)) || "3"
							.equals(mMap.get(CLNFMessage.nfmSubState)))) {
				if ("0".equals(mMap.get(key))) {
					value = "1";

				} else {
					value = "0";
				}
				upMap.put(key, value);
				upMap.put(key1, "2");
				app.updateNewFriendEndData(upMap,
						"where nfmId=" + mMap.get("nfmId"));
				upMap1.put("nfmSubState", "1");
				upMap1.put("nfmSubDate", mMap.get(CLNFMessage.nfmDate) + " "
						+ mMap.get(CLNFMessage.nfmTime));
				app.updateNewFriendEndData(upMap1,
						"where nfmId=" + mMap.get(CLNFMessage.nfmPId));
				map2.put(CLNFMessage.nfmUpdateState, "2");
				app.updateNewFriendEndData1(map2,
						"where nfmId=" + mMap.get(CLNFMessage.nfmPId));
				map.put(CLNFMessage.nfmPId, "0");
				app.updateNewFriendEndData1(map,
						"where nfmId=" + mMap.get("nfmId"));
			} else {
				if ("0".equals(mMap.get(key)))
					value = "1";
				else
					value = "0";
				upMap.put(key, value);
				upMap.put(key1, "2");
				app.updateNewFriendEndData(upMap,
						"where nfmId=" + mMap.get("nfmId"));
			}
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
		if (type == 0) {// 添加成功
			delete_tv.setText("添加成功！");
			isNetWork();
		} else if (type == 1) {
			delete_tv.setText("添加失败！");
		}
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
		if ("1".equals(msg)) {
			loadData();
			adapter.notifyDataSetChanged();
		}
	}

	private void DownLoadMessage1() {
		if ("".equals(downtime)) {
			downtime = "2016-01-01%2B00:00:00";
		} else {
			downtime = downtime.replace(" ", "%2B");
		}
		path = URLConstants.新版好友查询日程和以前信息 + userId + "&type=1&cpId=" + fid
				+ "&pageNum=1000&nowPage=1&downTime=" + downtime;
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			DownLoadDataAsync1(path);
		} else {
			NewSendMessageToFriendActivity.updateShunyanData();
			loadData();
		}
	}

	private void DownLoadDataAsync1(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								NewFriendRiChengBean1 bean1 = null;
								List<NewFriendRiChengBean> beans = null;
								Gson gson = new Gson();
								NewFriendRiChengBackBean backBean = gson
										.fromJson(result,
												NewFriendRiChengBackBean.class);
								sharedPrefUtil.putString(context,
										ShareFile.USERFILE,
										ShareFile.FRIENDDOWNSCHTIME,
										backBean.downTime.replace("T", " "));
								if (backBean.status == 0) {
									bean1 = backBean.page;
									if (bean1 != null) {
										beans = bean1.items;
										for (int i = 0; i < beans.size(); i++) {
											if (app.CheckCountNewFriendData(Integer
													.parseInt(beans.get(i).id)) != 0) {
												app.alterNewFriendsData1(
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
														1,
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
														0,
														Integer.parseInt(beans
																.get(i).schAType),
														Integer.parseInt(beans
																.get(i).repInSTable),
														beans.get(i).isEnd,
														Integer.parseInt(beans
																.get(i).downstate),
														Integer.parseInt(beans
																.get(i).poststate),
														"",
														beans.get(i).schContent,
														beans.get(i).schDate,
														beans.get(i).schctime,
														beans.get(i).cTypeDesc,
														beans.get(i).cTypeSpare,
														"",
														beans.get(i).CAlarmsoundDesc,
														beans.get(i).CAlarmsound,
														beans.get(i).repstartdate,
														beans.get(i).repinitialcreatedtime,
														beans.get(i).replastcreatedtime,
														beans.get(i).repnextcreatedtime,
														beans.get(i).schWebURL,
														beans.get(i).schImagePath,
														beans.get(i).uName,
														beans.get(i).remark, 0,
														Integer.parseInt(beans
																.get(i).pId),
														0, "", 0, 0, "", 0, "");
											} else {
												app.insertIntnetMessageSendData(
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
														1,
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
														0,
														Integer.parseInt(beans
																.get(i).schAType),
														Integer.parseInt(beans
																.get(i).repInSTable),
														beans.get(i).isEnd,
														Integer.parseInt(beans
																.get(i).downstate),
														Integer.parseInt(beans
																.get(i).poststate),
														"",
														beans.get(i).schContent,
														beans.get(i).schDate,
														beans.get(i).schctime,
														beans.get(i).cTypeDesc,
														beans.get(i).cTypeSpare,
														"",
														beans.get(i).CAlarmsoundDesc,
														beans.get(i).CAlarmsound,
														beans.get(i).repstartdate,
														beans.get(i).repinitialcreatedtime,
														beans.get(i).replastcreatedtime,
														beans.get(i).repnextcreatedtime,
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
														0,
														"",
														DateUtil.formatDateTimeSs(new Date()));
											}
										}
									}
									NewSendMessageToFriendActivity
											.updateShunyanData();
									loadData();
									adapter.notifyDataSetChanged();
								} else {
									NewSendMessageToFriendActivity
											.updateShunyanData();
									loadData();
									adapter.notifyDataSetChanged();
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
						loadData();
						adapter.notifyDataSetChanged();
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
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
