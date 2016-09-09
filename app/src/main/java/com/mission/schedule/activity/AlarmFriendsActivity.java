//package com.mission.schedule.activity;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.Request.Method;
//import com.android.volley.Response.Listener;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.InitListener;
//import com.iflytek.cloud.RecognizerListener;
//import com.iflytek.cloud.RecognizerResult;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechRecognizer;
//import com.iflytek.cloud.ui.RecognizerDialog;
//import com.iflytek.cloud.ui.RecognizerDialogListener;
//import com.mission.schedule.adapter.AlarmFriendsAdapter;
//import com.mission.schedule.annotation.ViewResId;
//import com.mission.schedule.applcation.App;
//import com.mission.schedule.bean.FriendsBean;
//import com.mission.schedule.bean.LiaoTianHistoryBackBean;
//import com.mission.schedule.bean.LiaoTianHistoryBean;
//import com.mission.schedule.bean.SendLiaoTianMessageBackBean;
//import com.mission.schedule.constants.ShareFile;
//import com.mission.schedule.constants.URLConstants;
//import com.mission.schedule.entity.FMessages;
//import com.mission.schedule.utils.CharacterUtil;
//import com.mission.schedule.utils.DateUtil;
//import com.mission.schedule.utils.JsonParser;
//import com.mission.schedule.utils.MyPullToRefreshView;
//import com.mission.schedule.utils.NetUtil;
//import com.mission.schedule.utils.NetUtil.NetWorkState;
//import com.mission.schedule.utils.ProgressUtil;
//import com.mission.schedule.utils.RepeatCreateTimeComparaor;
//import com.mission.schedule.utils.MyPullToRefreshView.OnFooterRefreshListener;
//import com.mission.schedule.utils.MyPullToRefreshView.OnHeaderRefreshListener;
//import com.mission.schedule.utils.ReadTextContentXml.ReadWeiXinXml;
//import com.mission.schedule.utils.SharedPrefUtil;
//import com.mission.schedule.view.SonicSensorView;
//import com.mission.schedule.R;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Html;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class AlarmFriendsActivity extends BaseActivity implements
//		OnClickListener, OnFooterRefreshListener, OnHeaderRefreshListener {
//
//	@ViewResId(id = R.id.top_ll_back)
//	private LinearLayout top_ll_back;
//	@ViewResId(id = R.id.friendsName_tv)
//	private TextView friendsName_tv;
//	@ViewResId(id = R.id.listview)
//	private ListView listview;
//	@ViewResId(id = R.id.yuyin_ib)
//	private ImageButton yuyin_ib;
//	@ViewResId(id = R.id.et_sendmessage)
//	private EditText et_sendmessage;
//	@ViewResId(id = R.id.add_ib)
//	private ImageButton add_ib;
//	@ViewResId(id = R.id.myfriend_pull_refresh_view)
//	private MyPullToRefreshView mPullToRefreshView = null;
//	@ViewResId(id = R.id.ll)
//	private LinearLayout ll;
//	@ViewResId(id = R.id.zidingyi_ll)
//	private LinearLayout zidingyi_ll;
//	@ViewResId(id = R.id.liuyan_ll)
//	private LinearLayout liuyan_ll;
//	@ViewResId(id = R.id.shoudao_ll)
//	private LinearLayout shoudao_ll;
//	@ViewResId(id = R.id.chongfu_ll)
//	private LinearLayout chongfu_ll;
//	@ViewResId(id = R.id.shuohua_bt)
//	private Button shuohua_bt;
//	@ViewResId(id = R.id.wenzi_ib)
//	private ImageButton wenzi_ib;
//
//	private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部
//	private boolean mRefreshFlag = false;// 判断是否刷新
//
//	SharedPrefUtil prefUtil = null;
//	Context context;
//	String friendsName;
//	FriendsBean myFriendsBean = null;
//	String path;
//	String userId;
//	AlarmFriendsAdapter adapter = null;
//	List<LiaoTianHistoryBean> mList;
//	List<Map<String, String>> mylist = new ArrayList<Map<String, String>>();
//	String content;
//	int fid;
//	int state;
//	String alamsound;
//	String alamsoundDesc;
//	int index = 0;
//	String friendsheadimage;
//	String myimage;
//	String myName;
//	int pageIndex = 1;
//
//	App app = null;
//
//	// 语音听写对象
//	private SpeechRecognizer mIat;
//	// 语音听写UI
//	private RecognizerDialog mIatDialog;
//	// 用HashMap存储听写结果
//	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
//	// 引擎类型
//	private String mEngineType = SpeechConstant.TYPE_CLOUD;
//	private SharedPreferences mSharedPreferences;
//	private Toast mToast;
//	ProgressUtil progressUtil = new ProgressUtil();
//
//	@Override
//	protected void setListener() {
//		top_ll_back.setOnClickListener(this);
//		yuyin_ib.setOnClickListener(this);
//		add_ib.setOnClickListener(this);
//		zidingyi_ll.setOnClickListener(this);
//		liuyan_ll.setOnClickListener(this);
//		shoudao_ll.setOnClickListener(this);
//		chongfu_ll.setOnClickListener(this);
//		mPullToRefreshView.setOnHeaderRefreshListener(this);
//		mPullToRefreshView.setOnFooterRefreshListener(this);
//		wenzi_ib.setOnClickListener(this);
//	}
//
//	@Override
//	protected void setContentView() {
//		setContentView(R.layout.activity_alarmfriends);
//	}
//
//	@Override
//	protected void init(Bundle savedInstanceState) {
//		context = this;
//		URLConstants.activityList.add(this);
//		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
//		userId = prefUtil.getString(context, ShareFile.USERFILE,
//				ShareFile.USERID, "");
//		app = App.getDBcApplication();
//		View footView = LayoutInflater.from(context).inflate(
//				R.layout.activity_alarmfriends_footview, null);
//		listview.addFooterView(footView);
//		myFriendsBean = (FriendsBean) getIntent().getSerializableExtra(
//				"myfriend");
//		friendsheadimage = myFriendsBean.titleImg;
//		myimage = prefUtil.getString(context, ShareFile.USERFILE,
//				ShareFile.USERPHOTOPATH, "");
//		myName = prefUtil.getString(context, ShareFile.USERFILE,
//				ShareFile.USERNAME, "");
//		friendsName_tv.setText(myFriendsBean.uName);
//		fid = myFriendsBean.fId;
//		ll.setVisibility(View.GONE);
//		shuohua_bt.setVisibility(View.GONE);
//		et_sendmessage.setVisibility(View.VISIBLE);
//		wenzi_ib.setVisibility(View.GONE);
//		yuyin_ib.setVisibility(View.VISIBLE);
//		// 初始化识别无UI识别对象
//		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
//		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
//
//		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
//		// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
//		mIatDialog = new RecognizerDialog(this, mInitListener);
//		mSharedPreferences = getSharedPreferences("com.iflytek.setting",
//				Activity.MODE_PRIVATE);
//		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//
//		loadData();
//		item();
//	}
//
//	private void loadData() {
//		path = URLConstants.查询聊天记录 + "?uId=" + Integer.parseInt(userId)
//				+ "&fId=" + myFriendsBean.fId + "&nowPage=" + 1 + "&pageNum="
//				+ 40;
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			ChaXunHistoryAsync(path);
//		} else {
//			Toast.makeText(context, "请检查你的网络..", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	private void checkData() {
//		mylist.clear();
//		mylist = app.QueryAllLiaoTianData(fid);
//		Collections.sort(mylist, new RepeatCreateTimeComparaor());
//		adapter = new AlarmFriendsAdapter(context, mylist, userId,
//				friendsheadimage, myimage, mScreenWidth);
//		listview.setAdapter(adapter);
//		listview.setSelection(adapter.getCount() - 1);
//	}
//
//	private void item() {
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Map<String, String> bean = (Map<String, String>) listview
//						.getAdapter().getItem(position);
//				if (bean != null) {
//					if (Integer.parseInt(bean.get(FMessages.fmStatus)) == 1) {
//						if (Integer.parseInt(bean.get(FMessages.fmSendID)) == Integer
//								.parseInt(userId)) {
//							dialogFenXiangOnClick(bean);
//						} else {
//							dialogFriendsFenXiangOnClick(bean);
//						}
//					} else if (Integer.parseInt(bean.get(FMessages.fmStatus)) == 2) {
//						if (Integer.parseInt(bean.get(FMessages.fmSendID)) == Integer
//								.parseInt(userId)) {
//							dialogFenXiangRepeatOnClick(bean);
//						} else {
//							dialogFriendsFenXiangOnClick(bean);
//						}
//					} else {
//						dialogDeleteOKOnClick(bean);
//					}
//				}
//			}
//		});
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		ll.setVisibility(View.GONE);
//		checkData();
//		et_sendmessage.setText("");
//	}
//
//	@Override
//	protected void setAdapter() {
//		et_sendmessage
//				.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//
//					@Override
//					public boolean onEditorAction(TextView v, int actionId,
//							KeyEvent event) {
//						ll.setVisibility(View.GONE);
//						if (actionId == EditorInfo.IME_ACTION_SEND) {
//							InputMethodManager imm = (InputMethodManager) v
//									.getContext().getSystemService(
//											Context.INPUT_METHOD_SERVICE);
//							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//							Map<Object, Object> map = ReadWeiXinXml.yuyinSb(
//									context, et_sendmessage.getText()
//											.toString().trim());
//							content = (String) map.get("value");
//							state = 1;
//							alamsound = (String) map.get("ringDesc");
//							alamsoundDesc = (String) map.get("ringVal");
//							sendMessageDialog(content, fid, state,
//									friendsName_tv.getText().toString().trim(),
//									alamsound, alamsoundDesc, map);
//
//							return true;
//						}
//						return false;
//					}
//				});
//
//	}
//
//	private void sendMessageDialog(String content, int fid, int state,
//			String friendsName, String alamsound, String alamsoundDesc,
//			Map<Object, Object> map) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		params.y = 150;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_sendmessagedialog,
//				null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//		new SendMessageDetailOnClick(dialog, more_pop_menu, content, fid,
//				state, friendsName, alamsound, alamsoundDesc, map);
//	}
//
//	class SendMessageDetailOnClick implements View.OnClickListener {
//
//		private View mainView;
//		private Dialog dialog;
//		private String content;
//		private int fid;
//		private int state;
//		private String friendsName;
//		private String alamsound;
//		private String alamsoundDesc;
//		private LinearLayout detail_edit;
//		private LinearLayout detail_close;
//		private Button suresend_bt;
//		private TextView time_date;
//		private TextView year_date;
//		private TextView shunyan_tv;
//		private TextView content_tv;
//		private TextView timebefore_tv;
//		private TextView week_date;
//		Map<Object, Object> map;
//		String today, tomorrow;
//		Calendar calendar = Calendar.getInstance();
//		int displaytime, postpone;
//		String date;
//		String time;
//
//		public SendMessageDetailOnClick(Dialog dialog, View view,
//				String content, int fid, int state, String friendsName,
//				String alamsound, String alamsoundDesc, Map<Object, Object> map) {
//			this.mainView = view;
//			this.dialog = dialog;
//			this.content = content;
//			this.fid = fid;
//			this.state = state;
//			this.friendsName = friendsName;
//			this.alamsound = alamsound;
//			this.alamsoundDesc = alamsoundDesc;
//			this.map = map;
//			initview();
//			initdata();
//		}
//
//		public void initview() {
//			detail_edit = (LinearLayout) mainView
//					.findViewById(R.id.detail_edit);
//			detail_edit.setOnClickListener(this);
//			detail_close = (LinearLayout) mainView
//					.findViewById(R.id.detail_close);
//			detail_close.setOnClickListener(this);
//			suresend_bt = (Button) mainView.findViewById(R.id.suresend_bt);
//			suresend_bt.setOnClickListener(this);
//			time_date = (TextView) mainView.findViewById(R.id.time_date);
//			year_date = (TextView) mainView.findViewById(R.id.year_date);
//			shunyan_tv = (TextView) mainView.findViewById(R.id.shunyan_tv);
//			content_tv = (TextView) mainView.findViewById(R.id.content_tv);
//			timebefore_tv = (TextView) mainView
//					.findViewById(R.id.timebefore_tv);
//			week_date = (TextView) mainView.findViewById(R.id.week_date);
//		}
//
//		public void initdata() {
//			if ("0".equals(map.get("di")) && "0".equals(map.get("ti"))) {
//				displaytime = 0;
//				postpone = 1;
//				// t.setcDisplayAlarm(0);
//				// t.setcPostpone(1);
//			} else {
//				displaytime = 1;
//				postpone = 0;
//				// t.setcDisplayAlarm(1);
//				// t.setcPostpone(0);
//			}
//			calendar.setTime(new Date());
//			today = DateUtil.formatDate(calendar.getTime());
//			calendar.set(Calendar.DAY_OF_MONTH,
//					calendar.get(Calendar.DAY_OF_MONTH) + 1);
//			tomorrow = DateUtil.formatDate(calendar.getTime());
//
//			content_tv.setText(content);
//			date = (String) map.get("date");
//			if (today.equals(date)) {
//				year_date.setText("今天");
//			} else if (tomorrow.equals(date)) {
//				year_date.setText("明天");
//			} else {
//				year_date.setText(date);
//			}
//			if (displaytime == 0) {
//				time = DateUtil.formatDateTimeHm(new Date());
//				time_date.setText("全天");
//			} else {
//				time = (String) map.get("time");
//				time_date.setText((String) map.get("time"));
//			}
//			week_date.setText(CharacterUtil.getWeekOfDate(context,
//					DateUtil.parseDate(date)));
//			String colorState = ""
//					+ context.getResources().getColor(R.color.mingtian_color);
//			String sequence = "<font color='" + colorState + "'>"
//					+ context.getString(R.string.adapter_shun) + "</font>";
//
//			shunyan_tv.setBackgroundResource(R.drawable.tv_kuang_aftertime);
//			shunyan_tv.setText(Html.fromHtml(sequence));
//			if (0 == postpone) {
//				shunyan_tv.setVisibility(View.GONE);
//			} else {
//				shunyan_tv.setVisibility(View.VISIBLE);
//			}
//			Date dateStr = DateUtil.parseDate(date);
//			Date dateToday = DateUtil
//					.parseDate(DateUtil.formatDate(new Date()));
//			long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
//			long day = betweem / (24 * 3600);
//			long hour = betweem % (24 * 3600) / 3600;
//			long min = betweem % 3600 / 60;
//
//			if (today.equals(date)) {// 今天
//				if (displaytime == 0 && postpone == 1) {
//					timebefore_tv.setText("今天");
//				} else {
//					if (DateUtil.parseDate(DateUtil.formatDate(new Date()))
//							.after(DateUtil.parseDate(DateUtil
//									.formatDate(dateStr)))) {
//						if (Math.abs(hour) >= 1) {
//							timebefore_tv.setText(Math.abs(hour) + "小时前");
//						} else {
//							timebefore_tv.setText(Math.abs(min) + "分钟前");
//						}
//					} else {
//						if (Math.abs(hour) >= 1) {
//							timebefore_tv.setText(Math.abs(hour) + "小时后");
//						} else {
//							timebefore_tv.setText(Math.abs(min) + "分钟后");
//						}
//					}
//				}
//			} else if (tomorrow.equals(date)) {// 明天
//				if (Math.abs(day) >= 1) {
//					timebefore_tv.setText(Math.abs(day) + "天后");
//				} else {
//					timebefore_tv.setText(Math.abs(hour) + "小时后");
//
//				}
//			} else {
//				timebefore_tv.setText(Math.abs(day) + 1 + "天后");
//			}
//		}
//
//		@Override
//		public void onClick(View v) {
//			Intent intent = null;
//			switch (v.getId()) {
//			case R.id.detail_edit:
//				if (!"".equals(content_tv.getText().toString().trim())) {
//					intent = new Intent(context, EditSendMessageActivity.class);
//					intent.putExtra("userId", userId);
//					intent.putExtra("friendId", fid);
//					intent.putExtra("content", content);
//					intent.putExtra("state", state);
//					intent.putExtra("friendName", friendsName_tv.getText()
//							.toString().trim());
//					intent.putExtra("displaytime", displaytime + "");
//					intent.putExtra("postpone", postpone + "");
//					intent.putExtra("ringdesc", alamsoundDesc);
//					intent.putExtra("ringcode", alamsound);
//					intent.putExtra("date", date);
//					intent.putExtra("time", (String) map.get("time"));
//					startActivity(intent);
//				} else {
//					Toast.makeText(context, "提醒内容不能为空..", Toast.LENGTH_SHORT)
//							.show();
//					return;
//				}
//				break;
//			case R.id.detail_close:
//				dialog.dismiss();
//				break;
//			case R.id.suresend_bt:
//				if ("".equals(content) && content != null) {
//					Toast.makeText(context, "发送内容不能为空！", Toast.LENGTH_SHORT)
//							.show();
//					return;
//				} else {
//					String path = URLConstants.添加聊天信息;
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("tbuserFrendsMessage.uid", userId);
//					map.put("tbuserFrendsMessage.cpId", String.valueOf(fid));
//					map.put("tbuserFrendsMessage.messge", content);
//					map.put("tbuserFrendsMessage.status", String.valueOf(state));
//					map.put("tbuserFrendsMessage.cIsAlarm", String.valueOf(1));
//					map.put("tbuserFrendsMessage.cdate", date);
//					map.put("tbuserFrendsMessage.ctime", time);
//					map.put("tbuserFrendsMessage.cRecommendName", myName);
//					map.put("tbuserFrendsMessage.cAlarmSound", alamsound);
//					map.put("tbuserFrendsMessage.cAlarmSoundDesc",
//							alamsoundDesc);
//					map.put("tbuserFrendsMessage.repType", "0");// 1每天,2每周,3每月,4每年,5工作日
//					/**
//					 * 生成规则 1每天[] 2 每周[“1”] 3每月[“1”]每月1号 4每年[“01-01”]每年1月1号
//					 * 5工作日[]
//					 */
//					map.put("tbuserFrendsMessage.repTypeParameter", "");
//					map.put("tbuserFrendsMessage.cPostpone", String.valueOf(0));// 是否顺延(0否,1是)',
//					map.put("tbuserFrendsMessage.cTags", "");// 分类标签'
//					map.put("tbuserFrendsMessage.cType", String.valueOf(0));// 记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
//					map.put("tbuserFrendsMessage.cTypeDesc", String.valueOf(1));// 当记事类别为1时所带的url链接',
//					map.put("tbuserFrendsMessage.cTypeSpare", "");// 当记事类别为1时所带的url链接描述',
//					map.put("tbuserFrendsMessage.cOpenstate", String.valueOf(0));// 公开状态(0否,1是,2仅好友可见)',
//					map.put("tbuserFrendsMessage.cLightAppId", "");// 轻应用与记事绑定的唯一ID'
//					map.put("tbuserFrendsMessage.repcolortype",
//							String.valueOf(0));// 记事颜色类别',
//					map.put("tbuserFrendsMessage.repstartdate", "");// 下一次重复闹钟起始时间'
//					map.put("tbuserFrendsMessage.repnextcreatedtime", "");// 下一次生成日期
//																			// 如
//																			// 2012-01-01
//																			// 12：00
//					map.put("tbuserFrendsMessage.replastcreatedtime", "");// 之前最后一次生成时间
//																			// 如2012-01-01
//																			// 12：00
//					/**
//					 * 是否显示时间 0否 1是 默认为1
//					 */
//					map.put("tbuserFrendsMessage.repdisplaytime", displaytime
//							+ "");
//					map.put("tbuserFrendsMessage.repinitialcreatedtime",
//							DateUtil.formatDateTime(new Date()));// 初始创建时间
//					map.put("tbuserFrendsMessage.aType", "0");// 0无附加信息1附加连接2附加图片3连接和图片
//					map.put("tbuserFrendsMessage.webUrl", "");
//					map.put("tbuserFrendsMessage.imgPath", "");
//					map.put("tbuserFrendsMessage.repInSTable", "0");
//					app.insertMFMessageSendData(Integer.parseInt(userId), fid,
//							1, 0, postpone, 0, displaytime, 0, 0, 0, "",
//							content, DateUtil.formatDateTimeSs(new Date()),
//							date, time_date.getText().toString().trim(), "",
//							"", "", alamsoundDesc, alamsound,
//							DateUtil.formatDateTime(new Date()),
//							DateUtil.formatDateTime(new Date()), "", "", state,
//							0, "", "", 0);
//
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						MySendMessageAsync(path, map);
//						dialog.dismiss();
//					} else {
//						Toast.makeText(context, "请检查网络..", Toast.LENGTH_SHORT)
//								.show();
//						return;
//					}
//				}
//				break;
//			default:
//				break;
//			}
//		}
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		Intent intent = null;
//		switch (v.getId()) {
//		case R.id.top_ll_back:
//			// FriendsBean bean = new FriendsBean();
//			// bean.fId = myFriendsBean.fId;
//			// bean.state = myFriendsBean.state;
//			// bean.uId = myFriendsBean.uId;
//			// bean.uName = myFriendsBean.uName;
//			// bean.titleImg = myFriendsBean.titleImg;
//			// bean.type = myFriendsBean.type;
//			// bean.backImage = myFriendsBean.backImage;
//			// bean.attentionState = myFriendsBean.attentionState;
//			// bean.redCount = myFriendsBean.redCount;
//			intent = new Intent();
//			intent.putExtra("bean", myFriendsBean);
//			setResult(Activity.RESULT_OK, intent);
//			this.finish();
//			break;
//		case R.id.add_ib:
//			if (index % 2 == 0) {
//				ll.setVisibility(View.VISIBLE);
//				index++;
//			} else {
//				ll.setVisibility(View.GONE);
//				index = 0;
//			}
//			break;
//		case R.id.zidingyi_ll:
//			state = 1;
//			intent = new Intent(context, EditFriendsMessageActivity.class);
//			intent.putExtra("userId", userId);
//			intent.putExtra("friendId", fid);
//			intent.putExtra("state", state);
//			intent.putExtra("content", "");
//			intent.putExtra("friendName", friendsName_tv.getText().toString()
//					.trim());
//			startActivity(intent);
//			break;
//		case R.id.chongfu_ll:
//			state = 2;
//			intent = new Intent(context, EditFriendsRepeatActivity.class);
//			intent.putExtra("userId", userId);
//			intent.putExtra("friendId", fid);
//			intent.putExtra("state", state);
//			intent.putExtra("friendName", friendsName_tv.getText().toString()
//					.trim());
//			startActivity(intent);
//			break;
//		case R.id.liuyan_ll:
//			state = 0;
//			intent = new Intent(context, LiuYanActivity.class);
//			intent.putExtra("userId", userId);
//			intent.putExtra("friendId", fid);
//			intent.putExtra("state", state);
//			intent.putExtra("friendName", friendsName_tv.getText().toString()
//					.trim());
//			startActivity(intent);
//			break;
//		case R.id.shoudao_ll:
//			state = 0;
//			ll.setVisibility(View.GONE);
//			String sendPath = URLConstants.添加聊天信息;
//			Map<String, String> pairs = new HashMap<String, String>();
//			pairs.put("tbuserFrendsMessage.uid", userId);
//			pairs.put("tbuserFrendsMessage.cpId", String.valueOf(fid));
//			pairs.put("tbuserFrendsMessage.messge", "OK");
//			pairs.put("tbuserFrendsMessage.status", String.valueOf(state));
//			pairs.put("tbuserFrendsMessage.cIsAlarm", String.valueOf(1));
//			pairs.put("tbuserFrendsMessage.cdate",
//					DateUtil.formatDateTime(new Date()).substring(0, 10)
//							.toString());
//			pairs.put("tbuserFrendsMessage.ctime",
//					DateUtil.formatDateTime(new Date()).substring(11, 16)
//							.toString());
//			pairs.put("tbuserFrendsMessage.cRecommendName", myName);
//			pairs.put("tbuserFrendsMessage.cAlarmSound", alamsound);
//			pairs.put("tbuserFrendsMessage.cAlarmSoundDesc", alamsoundDesc);
//			pairs.put("tbuserFrendsMessage.repType", "0");// 1每天,2每周,3每月,4每年,5工作日
//			/**
//			 * 生成规则 1每天[] 2 每周[“1”] 3每月[“1”]每月1号 4每年[“01-01”]每年1月1号 5工作日[]
//			 */
//			pairs.put("tbuserFrendsMessage.repTypeParameter", "");
//			pairs.put("tbuserFrendsMessage.cPostpone", String.valueOf(0));// 是否顺延(0否,1是)',
//			pairs.put("tbuserFrendsMessage.cTags", "");// 分类标签'
//			pairs.put("tbuserFrendsMessage.cType", String.valueOf(0));// 记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
//			pairs.put("tbuserFrendsMessage.cTypeDesc", "");// 当记事类别为1时所带的url链接',
//			pairs.put("tbuserFrendsMessage.cTypeSpare", "");// 当记事类别为1时所带的url链接描述',
//			pairs.put("tbuserFrendsMessage.cOpenstate", String.valueOf(0));// 公开状态(0否,1是,2仅好友可见)',
//			pairs.put("tbuserFrendsMessage.cLightAppId", "");// 轻应用与记事绑定的唯一ID'
//			pairs.put("tbuserFrendsMessage.repcolortype", "0");// 记事颜色类别',
//			pairs.put("tbuserFrendsMessage.repstartdate",
//					DateUtil.formatDateTime(new Date()));// 下一次重复闹钟起始时间'
//			pairs.put("tbuserFrendsMessage.repnextcreatedtime", "");// 下一次生成日期
//																	// 如
//																	// 2012-01-01
//																	// 12：00
//			pairs.put("tbuserFrendsMessage.replastcreatedtime", "");// 之前最后一次生成时间
//																	// 如2012-01-01
//																	// 12：00
//			/**
//			 * 是否显示时间 0否 1是 默认为1
//			 */
//			pairs.put("tbuserFrendsMessage.repdisplaytime", "1");
//			pairs.put("tbuserFrendsMessage.repinitialcreatedtime",
//					DateUtil.formatDateTime(new Date()));// 初始创建时间
//			pairs.put("tbuserFrendsMessage.aType", "0");// 0无附加信息1附加连接2附加图片3连接和图片
//			pairs.put("tbuserFrendsMessage.webUrl", "");
//			pairs.put("tbuserFrendsMessage.imgPath", "");
//			pairs.put("tbuserFrendsMessage.repInSTable", "0");
//
//			app.insertMFMessageSendData(Integer.parseInt(userId), fid, 1, 0, 0,
//					0, 1, 0, 0, 0, "", "OK",
//					DateUtil.formatDateTimeSs(new Date()), DateUtil
//							.formatDateTime(new Date()).substring(0, 10)
//							.toString(), DateUtil.formatDateTime(new Date())
//							.substring(11, 16).toString(), "", "", "",
//					alamsoundDesc, alamsound,
//					DateUtil.formatDateTime(new Date()),
//					DateUtil.formatDateTime(new Date()), "", "", state, 0, "",
//					"", 0);
//			if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//				MySendMessageAsync(sendPath, pairs);
//			} else {
//				Toast.makeText(context, "请检查你的网络..", Toast.LENGTH_SHORT).show();
//			}
//			break;
//		case R.id.yuyin_ib:
//			shuohua_bt.setVisibility(View.VISIBLE);
//			et_sendmessage.setVisibility(View.GONE);
//			wenzi_ib.setVisibility(View.VISIBLE);
//			yuyin_ib.setVisibility(View.GONE);
//			shuohua_bt.setOnTouchListener(new OnTouchListener() {
//
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					if (event.getAction() == MotionEvent.ACTION_DOWN) {
//						YuYinDialog();
//					} else if (event.getAction() == MotionEvent.ACTION_UP) {
//						mIat.stopListening();
//						// stopRecording();
//						yuyindialog.dismiss();
//					}
//					return true;
//				}
//			});
//			break;
//		case R.id.wenzi_ib:
//			shuohua_bt.setVisibility(View.GONE);
//			et_sendmessage.setVisibility(View.VISIBLE);
//			wenzi_ib.setVisibility(View.GONE);
//			yuyin_ib.setVisibility(View.VISIBLE);
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void ChaXunHistoryAsync(String path) {
//		if (!mRefreshFlag) {
//			progressUtil.ShowProgress(context, true, true, "正在努力加载......");
//		}
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						if (mRefreshFlag) {
//							mPullToRefreshView.onHeaderRefreshComplete();
//							mPullToRefreshView.onFooterRefreshComplete();
//						} else {
//							progressUtil.dismiss();
//						}
//						if (!TextUtils.isEmpty(result)) {
//							try {
//								if (mRefreshHeadFlag) {
//									adapter = null;
//								}
//								Log.d("TAG", result);
//								Gson gson = new Gson();
//								LiaoTianHistoryBackBean backBean;
//								try {
//									backBean = gson.fromJson(result,
//											LiaoTianHistoryBackBean.class);
//									if (backBean.status == 0) {
//										myFriendsBean.redCount = 0;
//										// App.getDBcApplication().updateFriendsReadStateData(fid);
//										if (adapter == null) {
//											mList = backBean.list;
//											boolean fag = false;
//											for (int i = 0; i < mList.size(); i++) {
//												if (!"".equals(mList.get(i).repcolortype)
//														&& mList.get(i).repcolortype != null) {
//													app.insertMFMessageData(
//															mList.get(i).id,
//															mList.get(i).uid,
//															mList.get(i).cpId,
//															mList.get(i).cIsAlarm,
//															mList.get(i).cOpenstate,
//															mList.get(i).cPostpone,
//															Integer.parseInt(mList
//																	.get(i).repcolortype),
//															mList.get(i).repdisplaytime,
//															mList.get(i).cBeforTime,
//															0,
//															mList.get(i).repType,
//															mList.get(i).repTypeParameter,
//															mList.get(i).messge,
//															mList.get(i).cretetime
//																	.replace(
//																			"T",
//																			" "),
//															mList.get(i).cdate,
//															mList.get(i).ctime,
//															mList.get(i).cTypeDesc,
//															mList.get(i).cTypeSpare,
//															mList.get(i).cTags,
//															mList.get(i).cAlarmSoundDesc,
//															mList.get(i).cAlarmSound,
//															mList.get(i).repstartdate,
//															mList.get(i).repinitialcreatedtime,
//															mList.get(i).replastcreatedtime,
//															mList.get(i).repnextcreatedtime,
//															mList.get(i).status,
//															mList.get(i).aType,
//															mList.get(i).webUrl,
//															mList.get(i).imgPath,
//															mList.get(i).repInSTable);
//												} else {
//													fag = app
//															.insertMFMessageData(
//																	mList.get(i).id,
//																	mList.get(i).uid,
//																	mList.get(i).cpId,
//																	mList.get(i).cIsAlarm,
//																	mList.get(i).cOpenstate,
//																	mList.get(i).cPostpone,
//																	0,
//																	mList.get(i).repdisplaytime,
//																	mList.get(i).cBeforTime,
//																	0,
//																	mList.get(i).repType,
//																	mList.get(i).repTypeParameter,
//																	mList.get(i).messge,
//																	mList.get(i).cretetime
//																			.replace(
//																					"T",
//																					" "),
//																	mList.get(i).cdate,
//																	mList.get(i).ctime,
//																	mList.get(i).cTypeDesc,
//																	mList.get(i).cTypeSpare,
//																	mList.get(i).cTags,
//																	mList.get(i).cAlarmSoundDesc,
//																	mList.get(i).cAlarmSound,
//																	mList.get(i).repstartdate,
//																	mList.get(i).repinitialcreatedtime,
//																	mList.get(i).replastcreatedtime,
//																	mList.get(i).repnextcreatedtime,
//																	mList.get(i).status,
//																	mList.get(i).aType,
//																	mList.get(i).webUrl,
//																	mList.get(i).imgPath,
//																	mList.get(i).repInSTable);
//												}
//
//											}
//											if (fag) {
//												mylist = app
//														.QueryAllLiaoTianData(fid);
//												Collections
//														.sort(mylist,
//																new RepeatCreateTimeComparaor());
//												adapter = new AlarmFriendsAdapter(
//														context, mylist,
//														userId,
//														friendsheadimage,
//														myimage, mScreenWidth);
//												listview.setAdapter(adapter);
//												listview.setSelection(adapter
//														.getCount() - 1);
//											}
//
//										} else {
//											mList.addAll(backBean.list);
//											boolean fag = true;
//											for (int i = 0; i < mList.size(); i++) {
//												if (!"".equals(mList.get(i).repcolortype)
//														&& mList.get(i).repcolortype != null) {
//													fag = app
//															.insertMFMessageData(
//																	mList.get(i).id,
//																	mList.get(i).uid,
//																	mList.get(i).cpId,
//																	mList.get(i).cIsAlarm,
//																	mList.get(i).cOpenstate,
//																	mList.get(i).cPostpone,
//																	Integer.parseInt(mList
//																			.get(i).repcolortype),
//																	mList.get(i).repdisplaytime,
//																	mList.get(i).cBeforTime,
//																	0,
//																	mList.get(i).repType,
//																	mList.get(i).repTypeParameter,
//																	mList.get(i).messge,
//																	mList.get(i).cretetime
//																			.replace(
//																					"T",
//																					" "),
//																	mList.get(i).cdate,
//																	mList.get(i).ctime,
//																	mList.get(i).cTypeDesc,
//																	mList.get(i).cTypeSpare,
//																	mList.get(i).cTags,
//																	mList.get(i).cAlarmSoundDesc,
//																	mList.get(i).cAlarmSound,
//																	mList.get(i).repstartdate,
//																	mList.get(i).repinitialcreatedtime,
//																	mList.get(i).replastcreatedtime,
//																	mList.get(i).repnextcreatedtime,
//																	mList.get(i).status,
//																	mList.get(i).aType,
//																	mList.get(i).webUrl,
//																	mList.get(i).imgPath,
//																	mList.get(i).repInSTable);
//												} else {
//													fag = app
//															.insertMFMessageData(
//																	mList.get(i).id,
//																	mList.get(i).uid,
//																	mList.get(i).cpId,
//																	mList.get(i).cIsAlarm,
//																	mList.get(i).cOpenstate,
//																	mList.get(i).cPostpone,
//																	0,
//																	mList.get(i).repdisplaytime,
//																	mList.get(i).cBeforTime,
//																	0,
//																	mList.get(i).repType,
//																	mList.get(i).repTypeParameter,
//																	mList.get(i).messge,
//																	mList.get(i).cretetime
//																			.replace(
//																					"T",
//																					" "),
//																	mList.get(i).cdate,
//																	mList.get(i).ctime,
//																	mList.get(i).cTypeDesc,
//																	mList.get(i).cTypeSpare,
//																	mList.get(i).cTags,
//																	mList.get(i).cAlarmSoundDesc,
//																	mList.get(i).cAlarmSound,
//																	mList.get(i).repstartdate,
//																	mList.get(i).repinitialcreatedtime,
//																	mList.get(i).replastcreatedtime,
//																	mList.get(i).repnextcreatedtime,
//																	mList.get(i).status,
//																	mList.get(i).aType,
//																	mList.get(i).webUrl,
//																	mList.get(i).imgPath,
//																	mList.get(i).repInSTable);
//												}
//
//											}
//											// mylist =
//											// app.QueryAllLiaoTianData(fid,
//											// pageIndex);
//											// Collections.sort(mylist,
//											// new RepeatCreateTimeComparaor());
//											// adapter = new
//											// AlarmFriendsAdapter(context,
//											// mylist, userId, friendsheadimage,
//											// myimage, mScreenWidth);
//											// listview.setAdapter(adapter);
//										}
//										checkData();
//									} else {
//										checkData();
//									}
//								} catch (JsonSyntaxException e) {
//									e.printStackTrace();
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//
//						} else {
//							return;
//						}
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError volleyError) {
//						progressUtil.dismiss();
//					}
//				});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//
//	private void MySendMessageAsync(String path, final Map<String, String> map) {
//		progressUtil.ShowProgress(context, true, true, "正在发送...");
//		StringRequest request = new StringRequest(Method.POST, path,
//				new Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							try {
//								SendLiaoTianMessageBackBean orFailBean = gson
//										.fromJson(
//												result,
//												SendLiaoTianMessageBackBean.class);
//								if (orFailBean.status == 0) {
//									app.updateMFMessageSendData(orFailBean.id);
//									checkData();
//									// adapter.notifyDataSetChanged();
//									et_sendmessage.setText("");
//								} else {
//									app.deleteMFMessageSendData();
//									Toast.makeText(context, orFailBean.message,
//											Toast.LENGTH_SHORT).show();
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//								return;
//							}
//						}
//
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError volleyError) {
//						progressUtil.dismiss();
//					}
//				}) {
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				return map;
//			}
//		};
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//
//	/**
//	 * 点击转发给好友
//	 * 
//	 * @param mMap
//	 */
//	private void dialogFenXiangOnClick(Map<String, String> mMap) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_friends, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//
//		new MyFenXiangOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class MyFenXiangOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private Map<String, String> mMap;
//		private TextView zhuanfafriends_tv;
//		private TextView addricheng_tv;
//		private TextView fenxiangwx_tv;
//		private TextView delete_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public MyFenXiangOnClick(Dialog dialog, Map<String, String> mMap,
//				View view) {
//			this.dialog = dialog;
//			this.mMap = mMap;
//			this.view = view;
//			initview();
//		}
//
//		public void initview() {
//			zhuanfafriends_tv = (TextView) view
//					.findViewById(R.id.zhuanfafriends_tv);
//			zhuanfafriends_tv.setOnClickListener(this);
//			addricheng_tv = (TextView) view.findViewById(R.id.addricheng_tv);
//			addricheng_tv.setOnClickListener(this);
//			fenxiangwx_tv = (TextView) view.findViewById(R.id.fenxiangwx_tv);
//			fenxiangwx_tv.setOnClickListener(this);
//			delete_tv = (TextView) view.findViewById(R.id.delete_tv);
//			delete_tv.setOnClickListener(this);
//			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
//			canel_tv.setOnClickListener(this);
//		}
//
//		@Override
//		public void onClick(View v) {
//			Intent intent = null;
//			LiaoTianHistoryBean historyBean = new LiaoTianHistoryBean();
//			historyBean.id = Integer.parseInt(mMap.get(FMessages.fmID));
//			historyBean.uid = Integer.parseInt(userId);
//			if (userId.equals(mMap.get(FMessages.fmSendID))) {
//				historyBean.cpId = Integer
//						.parseInt(mMap.get(FMessages.fmGetID));
//			} else {
//				historyBean.cpId = Integer.parseInt(mMap
//						.get(FMessages.fmSendID));
//			}
//			historyBean.cIsAlarm = Integer.parseInt(mMap
//					.get(FMessages.fmIsAlarm));
//			historyBean.cOpenstate = Integer.parseInt(mMap
//					.get(FMessages.fmOpenState));
//			historyBean.cPostpone = Integer.parseInt(mMap
//					.get(FMessages.fmPostpone));
//			historyBean.repcolortype = mMap.get(FMessages.fmColorType);
//			historyBean.repdisplaytime = Integer.parseInt(mMap
//					.get(FMessages.fmDisplayTime));
//			historyBean.cBeforTime = Integer.parseInt(mMap
//					.get(FMessages.fmBeforeTime));
//			historyBean.cType = Integer.parseInt(mMap
//					.get(FMessages.fmSourceType));
//			historyBean.repType = Integer.parseInt(mMap.get(FMessages.fmType));
//			historyBean.repTypeParameter = mMap.get(FMessages.fmParameter);
//			historyBean.messge = mMap.get(FMessages.fmContent);
//			historyBean.cretetime = mMap.get(FMessages.fmCreateTime);
//			historyBean.cdate = mMap.get(FMessages.fmDate);
//			historyBean.ctime = mMap.get(FMessages.fmTime);
//			historyBean.cTypeDesc = mMap.get(FMessages.fmSourceDesc);
//			historyBean.cTypeSpare = mMap.get(FMessages.fmSourceDescSpare);
//			historyBean.cTags = mMap.get(FMessages.fmTags);
//			historyBean.cAlarmSoundDesc = mMap.get(FMessages.fmRingDesc);
//			historyBean.cAlarmSound = mMap.get(FMessages.fmRingCode);
//			historyBean.repstartdate = mMap.get(FMessages.fmStartDate);
//			historyBean.repinitialcreatedtime = mMap
//					.get(FMessages.fmInitialCreatedTime);
//			historyBean.replastcreatedtime = mMap
//					.get(FMessages.fmLastCreatedTime);
//			historyBean.repnextcreatedtime = mMap
//					.get(FMessages.fmNextCreatedTime);
//			historyBean.status = Integer.parseInt(mMap.get(FMessages.fmStatus));
//
//			switch (v.getId()) {
//			case R.id.zhuanfafriends_tv:
//				intent = new Intent(context, FriendsSelectActivity.class);
//				intent.putExtra("bean", historyBean);
//				startActivity(intent);
//				dialog.dismiss();
//				break;
//			case R.id.addricheng_tv:
//				boolean isInset = App.getDBcApplication().insertScheduleData(
//						historyBean.messge, historyBean.cdate,
//						historyBean.ctime, historyBean.cIsAlarm,
//						historyBean.cBeforTime, historyBean.repdisplaytime,
//						historyBean.cPostpone, 0,
//						Integer.parseInt(historyBean.repcolortype), 0,
//						historyBean.cretetime.replace("T", " "),
//						historyBean.cTags, 0, historyBean.cTypeSpare, "", 0,
//						"", DateUtil.formatDateTimeSs(new Date()), 1,
//						historyBean.cOpenstate, 1, historyBean.cAlarmSoundDesc,
//						historyBean.cAlarmSound,
//
//						historyBean.cRecommendName, 0, 0, historyBean.aType,
//						historyBean.webUrl, historyBean.imgPath, 0, 0, 0);
//				if (isInset) {
//					Toast.makeText(context, "添加成功！", Toast.LENGTH_SHORT).show();
//					dialog.dismiss();
//				} else {
//					Toast.makeText(context, "添加失败！", Toast.LENGTH_SHORT).show();
//					dialog.dismiss();
//					return;
//				}
//				break;
//			case R.id.fenxiangwx_tv:
//
//				break;
//			case R.id.delete_tv:
//				app.deleteLiaoTianData(Integer.parseInt(mMap
//						.get(FMessages.fmID)));
//				checkData();
//				adapter.notifyDataSetChanged();
//				dialog.dismiss();
//				break;
//			case R.id.canel_tv:
//				dialog.dismiss();
//				break;
//			default:
//				break;
//			}
//		}
//
//	}
//
//	/**
//	 * 点击转发给好友
//	 * 
//	 * @param mMap
//	 */
//	private void dialogFriendsFenXiangOnClick(Map<String, String> mMap) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_friendsfenxiang, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//
//		new MyFriendsFenXiangOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class MyFriendsFenXiangOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private Map<String, String> mMap;
//		private TextView zhuanfafriends_tv;
//		private TextView fenxiangwx_tv;
//		private TextView delete_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public MyFriendsFenXiangOnClick(Dialog dialog,
//				Map<String, String> mMap, View view) {
//			this.dialog = dialog;
//			this.mMap = mMap;
//			this.view = view;
//			initview();
//		}
//
//		public void initview() {
//			zhuanfafriends_tv = (TextView) view
//					.findViewById(R.id.zhuanfafriends_tv);
//			zhuanfafriends_tv.setOnClickListener(this);
//			fenxiangwx_tv = (TextView) view.findViewById(R.id.fenxiangwx_tv);
//			fenxiangwx_tv.setOnClickListener(this);
//			delete_tv = (TextView) view.findViewById(R.id.delete_tv);
//			delete_tv.setOnClickListener(this);
//			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
//			canel_tv.setOnClickListener(this);
//		}
//
//		@Override
//		public void onClick(View v) {
//			Intent intent = null;
//			LiaoTianHistoryBean historyBean = new LiaoTianHistoryBean();
//			historyBean.id = Integer.parseInt(mMap.get(FMessages.fmID));
//			historyBean.uid = Integer.parseInt(userId);
//			if (userId.equals(mMap.get(FMessages.fmSendID))) {
//				historyBean.cpId = Integer
//						.parseInt(mMap.get(FMessages.fmGetID));
//			} else {
//				historyBean.cpId = Integer.parseInt(mMap
//						.get(FMessages.fmSendID));
//			}
//			historyBean.cIsAlarm = Integer.parseInt(mMap
//					.get(FMessages.fmIsAlarm));
//			historyBean.cOpenstate = Integer.parseInt(mMap
//					.get(FMessages.fmOpenState));
//			historyBean.cPostpone = Integer.parseInt(mMap
//					.get(FMessages.fmPostpone));
//			historyBean.repcolortype = mMap.get(FMessages.fmColorType);
//			historyBean.repdisplaytime = Integer.parseInt(mMap
//					.get(FMessages.fmDisplayTime));
//			historyBean.cBeforTime = Integer.parseInt(mMap
//					.get(FMessages.fmBeforeTime));
//			historyBean.cType = Integer.parseInt(mMap
//					.get(FMessages.fmSourceType));
//			historyBean.repType = Integer.parseInt(mMap.get(FMessages.fmType));
//			historyBean.repTypeParameter = mMap.get(FMessages.fmParameter);
//			historyBean.messge = mMap.get(FMessages.fmContent);
//			historyBean.cretetime = mMap.get(FMessages.fmCreateTime);
//			historyBean.cdate = mMap.get(FMessages.fmDate);
//			historyBean.ctime = mMap.get(FMessages.fmTime);
//			historyBean.cTypeDesc = mMap.get(FMessages.fmSourceDesc);
//			historyBean.cTypeSpare = mMap.get(FMessages.fmSourceDescSpare);
//			historyBean.cTags = mMap.get(FMessages.fmTags);
//			historyBean.cAlarmSoundDesc = mMap.get(FMessages.fmRingDesc);
//			historyBean.cAlarmSound = mMap.get(FMessages.fmRingCode);
//			historyBean.repstartdate = mMap.get(FMessages.fmStartDate);
//			historyBean.repinitialcreatedtime = mMap
//					.get(FMessages.fmInitialCreatedTime);
//			historyBean.replastcreatedtime = mMap
//					.get(FMessages.fmLastCreatedTime);
//			historyBean.repnextcreatedtime = mMap
//					.get(FMessages.fmNextCreatedTime);
//			historyBean.status = Integer.parseInt(mMap.get(FMessages.fmStatus));
//			switch (v.getId()) {
//			case R.id.zhuanfafriends_tv:
//				intent = new Intent(context, FriendsSelectActivity.class);
//				intent.putExtra("bean", historyBean);
//				startActivity(intent);
//				dialog.dismiss();
//				break;
//			case R.id.fenxiangwx_tv:
//
//				break;
//			case R.id.delete_tv:
//				app.deleteLiaoTianData(Integer.parseInt(mMap
//						.get(FMessages.fmID)));
//				checkData();
//				adapter.notifyDataSetChanged();
//				dialog.dismiss();
//				break;
//			case R.id.canel_tv:
//				dialog.dismiss();
//				break;
//			default:
//				break;
//			}
//		}
//
//	}
//
//	/**
//	 * 点击重复分享
//	 */
//	/**
//	 * 点击转发给好友
//	 * 
//	 * @param mMap
//	 */
//	private void dialogFenXiangRepeatOnClick(Map<String, String> mMap) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_friends, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//
//		new MyFenXiangRepeatOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class MyFenXiangRepeatOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private Map<String, String> mMap;
//		private TextView zhuanfafriends_tv;
//		private TextView fenxiangwx_tv;
//		private TextView addricheng_tv;
//		private TextView delete_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public MyFenXiangRepeatOnClick(Dialog dialog, Map<String, String> mMap,
//				View view) {
//			this.dialog = dialog;
//			this.mMap = mMap;
//			this.view = view;
//			initview();
//		}
//
//		public void initview() {
//			zhuanfafriends_tv = (TextView) view
//					.findViewById(R.id.zhuanfafriends_tv);
//			zhuanfafriends_tv.setOnClickListener(this);
//			addricheng_tv = (TextView) view.findViewById(R.id.addricheng_tv);
//			addricheng_tv.setOnClickListener(this);
//			fenxiangwx_tv = (TextView) view.findViewById(R.id.fenxiangwx_tv);
//			fenxiangwx_tv.setOnClickListener(this);
//			delete_tv = (TextView) view.findViewById(R.id.delete_tv);
//			delete_tv.setOnClickListener(this);
//			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
//			canel_tv.setOnClickListener(this);
//		}
//
//		@Override
//		public void onClick(View v) {
//			Intent intent = null;
//			LiaoTianHistoryBean historyBean = new LiaoTianHistoryBean();
//			historyBean.id = Integer.parseInt(mMap.get(FMessages.fmID));
//			historyBean.uid = Integer.parseInt(userId);
//			if (userId.equals(mMap.get(FMessages.fmSendID))) {
//				historyBean.cpId = Integer
//						.parseInt(mMap.get(FMessages.fmGetID));
//			} else {
//				historyBean.cpId = Integer.parseInt(mMap
//						.get(FMessages.fmSendID));
//			}
//			historyBean.cIsAlarm = Integer.parseInt(mMap
//					.get(FMessages.fmIsAlarm));
//			historyBean.cOpenstate = Integer.parseInt(mMap
//					.get(FMessages.fmOpenState));
//			historyBean.cPostpone = Integer.parseInt(mMap
//					.get(FMessages.fmPostpone));
//			historyBean.repcolortype = mMap.get(FMessages.fmColorType);
//			historyBean.repdisplaytime = Integer.parseInt(mMap
//					.get(FMessages.fmDisplayTime));
//			historyBean.cBeforTime = Integer.parseInt(mMap
//					.get(FMessages.fmBeforeTime));
//			historyBean.cType = Integer.parseInt(mMap
//					.get(FMessages.fmSourceType));
//			historyBean.repType = Integer.parseInt(mMap.get(FMessages.fmType));
//			historyBean.repTypeParameter = mMap.get(FMessages.fmParameter);
//			historyBean.messge = mMap.get(FMessages.fmContent);
//			historyBean.cretetime = mMap.get(FMessages.fmCreateTime);
//			historyBean.cdate = mMap.get(FMessages.fmDate);
//			historyBean.ctime = mMap.get(FMessages.fmTime);
//			historyBean.cTypeDesc = mMap.get(FMessages.fmSourceDesc);
//			historyBean.cTypeSpare = mMap.get(FMessages.fmSourceDescSpare);
//			historyBean.cTags = mMap.get(FMessages.fmTags);
//			historyBean.cAlarmSoundDesc = mMap.get(FMessages.fmRingDesc);
//			historyBean.cAlarmSound = mMap.get(FMessages.fmRingCode);
//			historyBean.repstartdate = mMap.get(FMessages.fmStartDate);
//			historyBean.repinitialcreatedtime = mMap
//					.get(FMessages.fmInitialCreatedTime);
//			historyBean.replastcreatedtime = mMap
//					.get(FMessages.fmLastCreatedTime);
//			historyBean.repnextcreatedtime = mMap
//					.get(FMessages.fmNextCreatedTime);
//			historyBean.status = Integer.parseInt(mMap.get(FMessages.fmStatus));
//
//			switch (v.getId()) {
//			case R.id.zhuanfafriends_tv:
//				intent = new Intent(context, FriendsSelectActivity.class);
//				intent.putExtra("bean", historyBean);
//				startActivity(intent);
//				dialog.dismiss();
//				break;
//			case R.id.addricheng_tv:
//				boolean isInset = app.insertCLRepeatTableData(
//						historyBean.cBeforTime, 0, historyBean.repdisplaytime,
//						historyBean.repType, historyBean.cIsAlarm, 0, 0,
//						historyBean.cType, 1, historyBean.repTypeParameter,
//						historyBean.repnextcreatedtime,
//						historyBean.replastcreatedtime,
//						historyBean.repinitialcreatedtime,
//						historyBean.repstartdate, historyBean.messge,
//						historyBean.cretetime, historyBean.cTypeDesc,
//						historyBean.cTypeSpare, historyBean.ctime,
//						historyBean.cAlarmSoundDesc, historyBean.cAlarmSound,
//						"", historyBean.cOpenstate, friendsName,
//						historyBean.cpId, "", "", 0, 0, historyBean.aType,
//						historyBean.webUrl, historyBean.imgPath,
//						historyBean.repInSTable, 0, "", 0, 0);
//				if (isInset) {
//					Toast.makeText(context, "添加成功！", Toast.LENGTH_SHORT).show();
//					dialog.dismiss();
//				} else {
//					Toast.makeText(context, "添加失败！", Toast.LENGTH_SHORT).show();
//					dialog.dismiss();
//					return;
//				}
//				break;
//			case R.id.fenxiangwx_tv:
//
//				break;
//			case R.id.delete_tv:
//				app.deleteLiaoTianData(Integer.parseInt(mMap
//						.get(FMessages.fmID)));
//				checkData();
//				adapter.notifyDataSetChanged();
//				dialog.dismiss();
//				break;
//			case R.id.canel_tv:
//				dialog.dismiss();
//				break;
//			default:
//				break;
//			}
//		}
//
//	}
//
//	/**
//	 * 删除ok选项
//	 * 
//	 * @param mMap
//	 */
//	private void dialogDeleteOKOnClick(Map<String, String> mMap) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_deleteok, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//
//		new DeleteOKOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class DeleteOKOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private Map<String, String> mMap;
//		private TextView delete_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public DeleteOKOnClick(Dialog dialog, Map<String, String> mMap,
//				View view) {
//			this.dialog = dialog;
//			this.mMap = mMap;
//			this.view = view;
//			initview();
//		}
//
//		public void initview() {
//			delete_tv = (TextView) view.findViewById(R.id.delete_tv);
//			delete_tv.setOnClickListener(this);
//			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
//			canel_tv.setOnClickListener(this);
//		}
//
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.delete_tv:
//				app.deleteLiaoTianData(Integer.parseInt(mMap
//						.get(FMessages.fmID)));
//				checkData();
//				adapter.notifyDataSetChanged();
//				dialog.dismiss();
//				break;
//			case R.id.canel_tv:
//				dialog.dismiss();
//				break;
//			default:
//				break;
//			}
//		}
//
//	}
//
//	@Override
//	public void onHeaderRefresh(MyPullToRefreshView view) {
//		mPullToRefreshView.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mRefreshHeadFlag = false;
//				mRefreshFlag = true;
//				loadData();
//				// ++pageIndex;
//				adapter.notifyDataSetChanged();
//				// mylist = app.QueryAllLiaoTianData(fid, pageIndex);
//				// adapter = new AlarmFriendsAdapter(context, mylist, userId,
//				// friendsheadimage, myimage, mScreenWidth);
//				// listview.setAdapter(adapter);
//				// adapter.notifyDataSetChanged();
//			}
//		}, 100);
//	}
//
//	@Override
//	public void onFooterRefresh(MyPullToRefreshView view) {
//		mPullToRefreshView.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mRefreshHeadFlag = true;
//				mRefreshFlag = true;
//				pageIndex = 1;// 刷新头部时将页数初始化为1
//				// mylist = app.QueryAllLiaoTianData(fid, pageIndex);
//				// adapter = new AlarmFriendsAdapter(context, mylist, userId,
//				// friendsheadimage, myimage, mScreenWidth);
//				// listview.setAdapter(adapter);
//				loadData();
//				adapter.notifyDataSetChanged();
//			}
//		}, 100);
//	}
//
//	private boolean isClose;
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK
//				&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
//			if (!isClose) {
//				isClose = true;
//				Toast.makeText(this, "再按一次返回键关闭程序", Toast.LENGTH_SHORT).show();
//				handler.postDelayed(runnable, 5000);
//				return true;
//			} else {
//				handler.removeCallbacks(runnable);
//				URLConstants.doEdit();
//				// this.finish();
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	private Runnable runnable = new Runnable() {
//
//		@Override
//		public void run() {
//			isClose = false;
//		}
//
//	};
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			int what = msg.what;
//			if (what >= 0 && what <= 20) {
//				mSonicSensorView.setVoiceLevel(voiceList[what]);
//			}
//		}
//	};
//	/**
//	 * 讯飞语音
//	 */
//	/**
//	 * 初始化监听器。
//	 */
//	private InitListener mInitListener = new InitListener() {
//
//		@Override
//		public void onInit(int code) {
//			Log.d("TAG", "SpeechRecognizer init() code = " + code);
//			if (code != ErrorCode.SUCCESS) {
//				Toast.makeText(context, "初始化失败，错误码：" + code, Toast.LENGTH_LONG)
//						.show();
//			}
//		}
//	};
//	String mcontent = "";
//
//	private void xunfeiRecognizer() {
//		int ret = 0; // 函数调用返回值
//		// 设置参数
//		setParam();
//		boolean isShowDialog = mSharedPreferences.getBoolean("iat_show", false);
//		if (isShowDialog) {
//			// 显示听写对话框
//			mIatDialog.setListener(mRecognizerDialogListener);
//			mIatDialog.show();
//			Toast.makeText(context, "请开始说话…", Toast.LENGTH_SHORT).show();
//		} else {
//			// 不显示听写对话框
//			ret = mIat.startListening(mRecognizerListener);
//			if (ret != ErrorCode.SUCCESS) {
//				// showTip("听写失败,错误码：" + ret);
//			} else {
//				// showTip(getString(R.string.text_begin));
//				// Toast.makeText(context, "请开始说话…", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//
//	/**
//	 * 听写UI监听器
//	 */
//	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
//		public void onResult(RecognizerResult results, boolean isLast) {
//			printResult(results);
//		}
//
//		/**
//		 * 识别回调错误.
//		 */
//		public void onError(SpeechError error) {
//			// showTip(error.getPlainDescription(true));
//			// Toast.makeText(context, error.getPlainDescription(true),
//			// Toast.LENGTH_SHORT).show();
//		}
//
//	};
//
//	private void showTip(final String str) {
//		mToast.setText(str);
//		mToast.show();
//	}
//
//	/**
//	 * 听写监听器。
//	 */
//	private RecognizerListener mRecognizerListener = new RecognizerListener() {
//
//		@Override
//		public void onBeginOfSpeech() {
//			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//			// showTip("开始说话");
//		}
//
//		@Override
//		public void onError(SpeechError error) {
//			// Tips：
//			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
//			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
//			// showTip(error.getPlainDescription(true));
//		}
//
//		@Override
//		public void onEndOfSpeech() {
//			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//		}
//
//		@Override
//		public void onResult(RecognizerResult results, boolean isLast) {
//			printResult(results);
//
//			if (isLast) {
//				if ("".equals(content)) {
//
//				} else {
//					sendYuYinMessageDialog(mcontent);
//				}
//			}
//		}
//
//		@Override
//		public void onVolumeChanged(int volume, byte[] data) {
//			// showTip("当前正在说话，音量大小：" + volume);
//			// Log.d("TAG", "返回音频数据："+data.length);
//			startRecording(volume);
//		}
//
//		@Override
//		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
//			// 若使用本地能力，会话id为null
//			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//			// Log.d(TAG, "session id =" + sid);
//			// }
//		}
//	};
//	int jianting = 0;
//
//	private void printResult(RecognizerResult results) {
//		String text = JsonParser.parseIatResult(results.getResultString());
//
//		String sn = null;
//		// 读取json结果中的sn字段
//		try {
//			JSONObject resultJson = new JSONObject(results.getResultString());
//			sn = resultJson.optString("sn");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		mIatResults.put(sn, text);
//
//		StringBuffer resultBuffer = new StringBuffer();
//		for (String key : mIatResults.keySet()) {
//			resultBuffer.append(mIatResults.get(key));
//		}
//		mcontent = resultBuffer.toString();
//		// jianting++;
//		// if (jianting != 1) {
//		// jianting = 0;
//		// }
//	}
//
//	/**
//	 * 参数设置
//	 * 
//	 * @param param
//	 * @return
//	 */
//	public void setParam() {
//		// 清空参数
//		mIat.setParameter(SpeechConstant.PARAMS, null);
//
//		// 设置听写引擎
//		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
//		// 设置返回结果格式
//		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//
//		String lag = mSharedPreferences.getString("iat_language_preference",
//				"mandarin");
//		if (lag.equals("en_us")) {
//			// 设置语言
//			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
//		} else {
//			// 设置语言
//			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//			// 设置语言区域
//			mIat.setParameter(SpeechConstant.ACCENT, lag);
//		}
//
//		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//		mIat.setParameter(SpeechConstant.VAD_BOS,
//				mSharedPreferences.getString("iat_vadbos_preference", "5000"));
//
//		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//		mIat.setParameter(SpeechConstant.VAD_EOS,
//				mSharedPreferences.getString("iat_vadeos_preference", "2000"));
//
//		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//		mIat.setParameter(SpeechConstant.ASR_PTT,
//				mSharedPreferences.getString("iat_punc_preference", "1"));
//
//		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
//				Environment.getExternalStorageDirectory() + "/msc/iat.wav");
//
//		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
//		// 注：该参数暂时只对在线听写有效
//		mIat.setParameter(SpeechConstant.ASR_DWA,
//				mSharedPreferences.getString("iat_dwa_preference", "0"));
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		App.getHttpQueues().cancelAll("down");
//		// 退出时释放连接
//		mIat.cancel();
//		mIat.destroy();
//	}
//
//	private void sendYuYinMessageDialog(String content) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		params.y = 150;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_sendmessagedialog,
//				null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//		new SendYuYinMessageDetailOnClick(dialog, more_pop_menu, content);
//	}
//
//	class SendYuYinMessageDetailOnClick implements View.OnClickListener {
//
//		private View mainView;
//		private Dialog dialog;
//		private String content;
//		private String alamsound;
//		private String alamsoundDesc;
//		private LinearLayout detail_edit;
//		private LinearLayout detail_close;
//		private Button suresend_bt;
//		private TextView time_date;
//		private TextView year_date;
//		private TextView shunyan_tv;
//		private TextView content_tv;
//		private TextView timebefore_tv;
//		private TextView week_date;
//		Map<Object, Object> map;
//		String today, tomorrow;
//		Calendar calendar = Calendar.getInstance();
//		int displaytime, postpone;
//		String date;
//
//		public SendYuYinMessageDetailOnClick(Dialog dialog, View view,
//				String content) {
//			this.mainView = view;
//			this.dialog = dialog;
//			this.content = content;
//			initview();
//			initdata();
//		}
//
//		public void initview() {
//			detail_edit = (LinearLayout) mainView
//					.findViewById(R.id.detail_edit);
//			detail_edit.setOnClickListener(this);
//			detail_close = (LinearLayout) mainView
//					.findViewById(R.id.detail_close);
//			detail_close.setOnClickListener(this);
//			suresend_bt = (Button) mainView.findViewById(R.id.suresend_bt);
//			suresend_bt.setOnClickListener(this);
//			time_date = (TextView) mainView.findViewById(R.id.time_date);
//			year_date = (TextView) mainView.findViewById(R.id.year_date);
//			shunyan_tv = (TextView) mainView.findViewById(R.id.shunyan_tv);
//			content_tv = (TextView) mainView.findViewById(R.id.content_tv);
//			timebefore_tv = (TextView) mainView
//					.findViewById(R.id.timebefore_tv);
//			week_date = (TextView) mainView.findViewById(R.id.week_date);
//			map = ReadWeiXinXml.yuyinSb(context, content);
//		}
//
//		public void initdata() {
//			if ("0".equals(map.get("di")) && "0".equals(map.get("ti"))) {
//				displaytime = 0;
//				postpone = 1;
//			} else {
//				displaytime = 1;
//				postpone = 0;
//			}
//			alamsound = (String) map.get("ringDesc");
//			alamsoundDesc = (String) map.get("ringVal");
//			content = (String) map.get("value");
//
//			calendar.setTime(new Date());
//			today = DateUtil.formatDate(calendar.getTime());
//			calendar.set(Calendar.DAY_OF_MONTH,
//					calendar.get(Calendar.DAY_OF_MONTH) + 1);
//			tomorrow = DateUtil.formatDate(calendar.getTime());
//
//			content_tv.setText(content);
//			date = DateUtil.formatDate(DateUtil.parseDate((String) map
//					.get("date")));
//			if (today.equals(date)) {
//				year_date.setText(date);
//			} else if (tomorrow.equals(date)) {
//				year_date.setText("明天");
//			} else {
//				year_date.setText(date);
//			}
//			if (displaytime == 0) {
//				time_date.setText("全天");
//			} else {
//				time_date.setText((String) map.get("time"));
//			}
//			week_date.setText(CharacterUtil.getWeekOfDate(context,
//					DateUtil.parseDate(date)));
//			String colorState = ""
//					+ context.getResources().getColor(R.color.mingtian_color);
//			String sequence = "<font color='" + colorState + "'>"
//					+ context.getString(R.string.adapter_shun) + "</font>";
//
//			shunyan_tv.setBackgroundResource(R.drawable.tv_kuang_aftertime);
//			shunyan_tv.setText(Html.fromHtml(sequence));
//			if (0 == postpone) {
//				shunyan_tv.setVisibility(View.GONE);
//			} else {
//				shunyan_tv.setVisibility(View.VISIBLE);
//			}
//			Date dateStr = DateUtil.parseDate(date);
//			Date dateToday = DateUtil
//					.parseDate(DateUtil.formatDate(new Date()));
//			long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
//			long day = betweem / (24 * 3600);
//			long hour = betweem % (24 * 3600) / 3600;
//			long min = betweem % 3600 / 60;
//
//			if (today.equals(date)) {// 今天
//				if (displaytime == 0 && postpone == 1) {
//					timebefore_tv.setText("今天");
//				} else {
//					if (DateUtil.parseDate(DateUtil.formatDate(new Date()))
//							.after(DateUtil.parseDate(DateUtil
//									.formatDate(dateStr)))) {
//						if (Math.abs(hour) >= 1) {
//							timebefore_tv.setText(Math.abs(hour) + "小时前");
//						} else {
//							timebefore_tv.setText(Math.abs(min) + "分钟前");
//						}
//					} else {
//						if (Math.abs(hour) >= 1) {
//							timebefore_tv.setText(Math.abs(hour) + "小时后");
//						} else {
//							timebefore_tv.setText(Math.abs(min) + "分钟后");
//						}
//					}
//				}
//			} else if (tomorrow.equals(date)) {// 明天
//				if (Math.abs(day) >= 1) {
//					timebefore_tv.setText(Math.abs(day) + "天后");
//				} else {
//					timebefore_tv.setText(Math.abs(hour) + "小时后");
//
//				}
//			} else {
//				timebefore_tv.setText(Math.abs(day) + 1 + "天后");
//			}
//
//		}
//
//		@Override
//		public void onClick(View v) {
//			Intent intent = null;
//			switch (v.getId()) {
//			case R.id.detail_edit:
//				if (!"".equals(content_tv.getText().toString().trim())) {
//					intent = new Intent(context, EditSendMessageActivity.class);
//					intent.putExtra("userId", userId);
//					intent.putExtra("friendId", fid);
//					intent.putExtra("state", "1");
//					intent.putExtra("content", mcontent);
//					intent.putExtra("friendName", friendsName_tv.getText()
//							.toString().trim());
//					intent.putExtra("displaytime", displaytime + "");
//					intent.putExtra("postpone", postpone + "");
//					intent.putExtra("ringdesc", alamsoundDesc);
//					intent.putExtra("ringcode", alamsound);
//					intent.putExtra("date", date);
//					intent.putExtra("time", (String) map.get("time"));
//
//					startActivity(intent);
//					dialog.dismiss();
//				} else {
//					Toast.makeText(context, "提醒内容不能为空..", Toast.LENGTH_SHORT)
//							.show();
//					return;
//				}
//				break;
//			case R.id.detail_close:
//				dialog.dismiss();
//				break;
//			case R.id.suresend_bt:
//				try {
//					String before = prefUtil.getString(context,
//							ShareFile.USERFILE, ShareFile.BEFORETIME, "0");
//					String sendPath = URLConstants.添加聊天信息;
//					Map<String, String> pairs = new HashMap<String, String>();
//					pairs.put("tbuserFrendsMessage.uid", userId);
//					pairs.put("tbuserFrendsMessage.cpId", String.valueOf(fid));
//					pairs.put("tbuserFrendsMessage.messge", mcontent);
//					pairs.put("tbuserFrendsMessage.status", "1");
//					pairs.put("tbuserFrendsMessage.cIsAlarm", String.valueOf(1));
//					pairs.put("tbuserFrendsMessage.cdate", date);
//					pairs.put("tbuserFrendsMessage.ctime",
//							(String) map.get("time"));
//					pairs.put("tbuserFrendsMessage.cRecommendName", myName);
//					pairs.put("tbuserFrendsMessage.cAlarmSound", alamsound);
//					pairs.put("tbuserFrendsMessage.cAlarmSoundDesc",
//							alamsoundDesc);
//					pairs.put("tbuserFrendsMessage.repType", "0");// 1每天,2每周,3每月,4每年,5工作日
//					/**
//					 * 生成规则 1每天[] 2 每周[“1”] 3每月[“1”]每月1号 4每年[“01-01”]每年1月1号
//					 * 5工作日[]
//					 */
//					pairs.put("tbuserFrendsMessage.repTypeParameter", "");
//					pairs.put("tbuserFrendsMessage.cPostpone", postpone + "");// 是否顺延(0否,1是)',
//					pairs.put("tbuserFrendsMessage.cTags", "");// 分类标签'
//					pairs.put("tbuserFrendsMessage.cType", String.valueOf(0));// 记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
//					pairs.put("tbuserFrendsMessage.cTypeDesc", "");// 当记事类别为1时所带的url链接',
//					pairs.put("tbuserFrendsMessage.cTypeSpare", "");// 当记事类别为1时所带的url链接描述',
//					pairs.put("tbuserFrendsMessage.cOpenstate", 0 + "");// 公开状态(0否,1是,2仅好友可见)',
//					pairs.put("tbuserFrendsMessage.cLightAppId", "");// 轻应用与记事绑定的唯一ID'
//					pairs.put("tbuserFrendsMessage.repcolortype", "0");// 记事颜色类别',
//					pairs.put("tbuserFrendsMessage.repstartdate",
//							DateUtil.formatDateTime(new Date()));// 下一次重复闹钟起始时间'
//					pairs.put("tbuserFrendsMessage.repnextcreatedtime", "");// 下一次生成日期
//																			// 如
//																			// 2012-01-01
//																			// 12：00
//					pairs.put("tbuserFrendsMessage.replastcreatedtime", "");// 之前最后一次生成时间
//																			// 如2012-01-01
//																			// 12：00
//					/**
//					 * 是否显示时间 0否 1是 默认为1
//					 */
//					pairs.put("tbuserFrendsMessage.repdisplaytime", displaytime
//							+ "");
//					pairs.put("tbuserFrendsMessage.repinitialcreatedtime",
//							DateUtil.formatDateTime(new Date()));// 初始创建时间
//					pairs.put("tbuserFrendsMessage.aType", "0");// 0无附加信息1附加连接2附加图片3连接和图片
//					pairs.put("tbuserFrendsMessage.webUrl", "");
//					pairs.put("tbuserFrendsMessage.imgPath", "");
//					pairs.put("tbuserFrendsMessage.repInSTable", "0");
//
//					App.getDBcApplication().insertMFMessageSendData(
//							Integer.parseInt(userId), fid, 1, 0, postpone, 0,
//							displaytime, Integer.parseInt(before), 0, 0, "",
//							mcontent, DateUtil.formatDateTimeSs(new Date()),
//							date, (String) map.get("time"), "", "", "",
//							alamsoundDesc, alamsound, "",
//							DateUtil.formatDateTimeSs(new Date()), "", "", 1,
//							0, "", "", 0);
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						MySendMessageAsync(sendPath, pairs);
//					} else {
//						Toast.makeText(context, "请检查你的网络..", Toast.LENGTH_SHORT)
//								.show();
//					}
//					loadData();
//					adapter.notifyDataSetChanged();
//				} catch (NumberFormatException e) {
//					e.printStackTrace();
//				}
//				dialog.dismiss();
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	SonicSensorView mSonicSensorView;
//	private int[] voiceList = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
//			14, 15, 16, 17, 18, 19, 20 };
//	int m;
//
//	private void startRecording(int recBufSize) {
//		m = recBufSize;
//		new MyThread().start();
//	}
//
//	class MyThread extends Thread {
//		@Override
//		public void run() {
//			super.run();
//			// for(m=0;m<voiceList.length;m++){
//			try {
//				Thread.sleep(100);
//				handler.sendEmptyMessage(m);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			// }
//		}
//	}
//
//	Dialog yuyindialog = null;
//
//	private void YuYinDialog() {
//		yuyindialog = new Dialog(context, R.style.dialog_huatong);
//		Window window = yuyindialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_friendsyuyin, null);
//		mSonicSensorView = (SonicSensorView) more_pop_menu
//				.findViewById(R.id.mSonicSensorView);
//		yuyindialog.setCanceledOnTouchOutside(true);
//		yuyindialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//		params.width = getWindowManager().getDefaultDisplay().getWidth();
//		yuyindialog.show();
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			xunfeiRecognizer();
//		} else {
//			alertFailDialog();
//		}
//	}
//
//	private void alertFailDialog() {
//		final AlertDialog builder = new AlertDialog.Builder(context).create();
//		builder.show();
//		Window window = builder.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		params.gravity = Gravity.CENTER;
//		window.setAttributes(params);// 设置生效
//		window.setGravity(Gravity.CENTER);
//		window.setContentView(R.layout.dialog_alert_ok);
//		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
//		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
//		delete_tv.setText("请检查您的网络！");
//		delete_ok.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				builder.cancel();
//			}
//		});
//
//	}
//}
