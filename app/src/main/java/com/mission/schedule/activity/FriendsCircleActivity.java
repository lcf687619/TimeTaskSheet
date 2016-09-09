//package com.mission.schedule.activity;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request.Method;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.john.library.annotation.ViewResId;
//import com.john.library.util.NetUtil;
//import com.john.library.util.NetUtil.NetWorkState;
//import com.john.library.util.SharedPrefUtil;
//import com.mission.schedule.adapter.FriendsCircleAdapter;
//import com.mission.schedule.applcation.App;
//import com.mission.schedule.bean.FriendsCircleBackBean;
//import com.mission.schedule.bean.FriendsCircleBean;
//import com.mission.schedule.clock.QueryAlarmData;
//import com.mission.schedule.clock.WriteAlarmClock;
//import com.mission.schedule.constants.ShareFile;
//import com.mission.schedule.constants.URLConstants;
//import com.mission.schedule.utils.DateUtil;
//import com.mission.schedule.utils.ProgressUtil;
//import com.mission.schedule.R;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class FriendsCircleActivity extends BaseActivity implements
//		OnClickListener {
//
//	@ViewResId(id = R.id.top_ll_back)
//	private LinearLayout top_ll_back;
//	@ViewResId(id = R.id.top_ll_right)
//	private RelativeLayout top_ll_right;
//	@ViewResId(id = R.id.friendscircle_lv)
//	private ListView friendscircle_lv;
//
//	Context context;
//	String path;
//	String userId;
//	SharedPrefUtil sharedPrefUtil = null;
//	List<FriendsCircleBean> circleBeansList = new ArrayList<FriendsCircleBean>();
//	FriendsCircleAdapter adapter = null;
//
//	ProgressUtil progressUtil = new ProgressUtil();
//
//	@Override
//	protected void setListener() {
//		top_ll_back.setOnClickListener(this);
//		top_ll_right.setOnClickListener(this);
//	}
//
//	@Override
//	protected void setContentView() {
//		setContentView(R.layout.activity_friendscircle);
//	}
//
//	@Override
//	protected void init(Bundle savedInstanceState) {
//		context = this;
//		URLConstants.activityList.add(this);
//		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
//		userId = sharedPrefUtil.getString(context, ShareFile.USERFILE,
//				ShareFile.USERID, "");
//
//		loadData();
//	}
//
//	private void loadData() {
//		path = URLConstants.全部好友日程 + "?uId=" + userId;
//		Log.d("TAG", path);
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			FriendsCircleAsync(path);
//		} else {
//			Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
//			return;
//		}
//	}
//
//	private void item() {
//		friendscircle_lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				FriendsCircleBean circleBean = (FriendsCircleBean) friendscircle_lv
//						.getAdapter().getItem(position);
//				dialogOnClick(circleBean);
//			}
//		});
//
//	}
//
//	@Override
//	protected void setAdapter() {
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.top_ll_back:
//			Intent intent = new Intent();
//			setResult(Activity.RESULT_OK, intent);
//			this.finish();
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	private void FriendsCircleAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在努力加载......");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							try {
//								FriendsCircleBackBean backBean = gson.fromJson(
//										result, FriendsCircleBackBean.class);
//								if (backBean.status == 0) {
//									circleBeansList.clear();
//									circleBeansList = backBean.maps;
//									if (circleBeansList != null
//											&& circleBeansList.size() > 0) {
//										Collections
//												.sort(circleBeansList,
//														new Comparator<FriendsCircleBean>() {
//
//															/*
//															 * int
//															 * compare(Student
//															 * o1, Student o2)
//															 * 返回一个基本类型的整型，
//															 * 返回负数表示：o1 小于o2，
//															 * 返回0 表示：o1和o2相等，
//															 * 返回正数表示：o1大于o2。
//															 */
//															public int compare(
//																	FriendsCircleBean o1,
//																	FriendsCircleBean o2) {
//
//																// 按照学生的年龄进行升序排列
//																if (DateUtil
//																		.parseDate(
//																				DateUtil.formatDate(DateUtil
//																						.parseDate(o1.cDate)))
//																		.getTime() > DateUtil
//																		.parseDate(
//																				DateUtil.formatDate(DateUtil
//																						.parseDate(o2.cDate)))
//																		.getTime()) {
//																	return 1;
//																}
//
//																if (DateUtil
//																		.parseDate(
//																				DateUtil.formatDate(DateUtil
//																						.parseDate(o1.cDate)))
//																		.getTime() == DateUtil
//																		.parseDate(
//																				DateUtil.formatDate(DateUtil
//																						.parseDate(o2.cDate)))
//																		.getTime()) {
//																	return 0;
//																}
//																return -1;
//															}
//														});
//										adapter = new FriendsCircleAdapter(
//												context, circleBeansList);
//										friendscircle_lv.setAdapter(adapter);
//										item();
//									}
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//							}
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
//	private void dialogOnClick(FriendsCircleBean mMap) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_friendscircle, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//
//		new FriendsCircleOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class FriendsCircleOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private FriendsCircleBean mMap;
//		private TextView zhuanfafriends_tv;
//		private TextView addricheng_tv;
//		private TextView fenxiangwx_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public FriendsCircleOnClick(Dialog dialog, FriendsCircleBean mMap,
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
//			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
//			canel_tv.setOnClickListener(this);
//		}
//
//		@Override
//		public void onClick(View v) {
//			Intent intent = null;
//			switch (v.getId()) {
//			case R.id.zhuanfafriends_tv:
//				intent = new Intent(context, FriendsCircleZhuanFaActivity.class);
//				intent.putExtra("bean", mMap);
//				startActivity(intent);
//				dialog.dismiss();
//				break;
//			case R.id.addricheng_tv:
//				try {
//					boolean isInset = App.getDBcApplication()
//							.insertScheduleData(mMap.cContent, mMap.cDate,
//									mMap.cTime,
//									Integer.parseInt(mMap.cIsAlarm),
//									Integer.parseInt(mMap.cBeforTime),
//									Integer.parseInt(mMap.cDisplayAlarm), 0, 0,
//									0, 0,
//									DateUtil.formatDateTimeSs(new Date()), "",
//									0, "", "", 0, "", "", 0, 0, 0,
//									mMap.cAlarmSoundDesc, mMap.cAlarmSound,
//									mMap.cuIckName, 0, 0, mMap.aType,
//									mMap.webUrl, mMap.imgPath, 0, 0, 0);
//					if (isInset) {
//						// if (Integer.parseInt(mMap.cBeforTime) == 0) {
//						//
//						// App.getDBcApplication().insertClockData(
//						// dateFormat.format(sdf.parse(mMap.cDate
//						// + " " + mMap.cTime)),
//						// mMap.cContent,
//						// Integer.parseInt(mMap.cBeforTime),
//						// dateFormat.format(sdf.parse(mMap.cDate
//						// + " " + mMap.cTime)),
//						// mMap.cAlarmSoundDesc, mMap.cAlarmSound,
//						// Integer.parseInt(mMap.cDisplayAlarm), 0, 0,
//						// App.schID, 0,
//						// Integer.parseInt(mMap.cIsAlarm), 0, "");
//						// } else {
//						// String resultTime = dateFormat.format(sdf.parse(
//						// mMap.cDate + " " + mMap.cTime).getTime()
//						// - Integer.parseInt(mMap.cBeforTime)
//						// * 60
//						// * 1000);
//						// App.getDBcApplication().insertClockData(
//						// resultTime,
//						// mMap.cContent,
//						// Integer.parseInt(mMap.cBeforTime),
//						// dateFormat.format(sdf.parse(mMap.cDate
//						// + " " + mMap.cTime)),
//						// mMap.cAlarmSoundDesc, mMap.cAlarmSound,
//						// Integer.parseInt(mMap.cDisplayAlarm), 0, 0,
//						// App.schID, 0,
//						// Integer.parseInt(mMap.cIsAlarm), 0, "");
//						// }
//						QueryAlarmData.writeAlarm(getApplicationContext());
//						WriteAlarmClock.writeAlarm(getApplicationContext());
//						Toast.makeText(context, "添加成功！", Toast.LENGTH_SHORT)
//								.show();
//						dialog.dismiss();
//					} else {
//						Toast.makeText(context, "添加失败！", Toast.LENGTH_SHORT)
//								.show();
//						dialog.dismiss();
//						return;
//					}
//				} catch (NumberFormatException e) {
//					e.printStackTrace();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.fenxiangwx_tv:
//
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
//	private static boolean isExit = false;
//	Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			isExit = false;
//		}
//	};
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			exit();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	private void exit() {
//		if (!isExit) {
//			isExit = true;
//			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//			// 利用handler延迟发送更改状态信息
//			mHandler.sendEmptyMessageDelayed(0, 3000);
//		} else {
//			URLConstants.doEdit();
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		App.getHttpQueues().cancelAll("down");
//	}
//}
