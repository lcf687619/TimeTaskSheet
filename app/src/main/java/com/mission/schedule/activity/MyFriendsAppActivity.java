package com.mission.schedule.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mission.schedule.adapter.MyFriendsAppAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.FriendsBean;
import com.mission.schedule.bean.MyFriendsBackBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.IXListViewListener;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyFriendsAppActivity extends BaseActivity implements
		OnClickListener, IXListViewListener {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.myfriends_lv)
	private SwipeXListViewNoHead myfriends_lv;

	Context context;
	SharedPrefUtil prefUtil = null;
	String userID;
	List<FriendsBean> bsqFriendList = null;
	List<FriendsBean> sqFrendList = null;
	List<FriendsBean> mList = new ArrayList<FriendsBean>();;
	MyFriendsAppAdapter adapter = null;
	ProgressUtil progressUtil = new ProgressUtil();
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		top_ll_left.setOnClickListener(this);
		myfriends_lv.setPullLoadEnable(false);
		myfriends_lv.setXListViewListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_myfriendsapp);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userID = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		initdata();
	}

	@Override
	protected void setAdapter() {
	}

	private void initdata() {
		String path = URLConstants.好友申请和被申请 + "?uId="
				+ Integer.parseInt(userID);// "255154"
		MyFriendsAppAsync(path);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			FriendsBean friendsBean = (FriendsBean) msg.obj;
			int fId = friendsBean.fId;// 当前用户ID
			int uId = friendsBean.uId;// 好友id
			String path;
			switch (msg.what) {
			case 0:// 接受好友
				path = URLConstants.确认好友申请; // + "?uId=" + uId + "&fId=" +
											// fId+"&uName="+prefUtil.getString(context,
											// ShareFile.USERFILE,
											// ShareFile.USERNAME, "");
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("uId", uId + "");
				pairs.put("fId", fId + "");
				pairs.put("uName", prefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.USERNAME, ""));
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					AgreeFriendsAppAsync(path, pairs);
				} else {
					Toast.makeText(context, "请检查你的网络...", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				break;
			case 1:// 右滑忽略
				path = URLConstants.忽略好友申请;// + "?uId=" + uId + "&fId=" +
											// fId+"&uName="+prefUtil.getString(context,
											// ShareFile.USERFILE,
											// ShareFile.USERNAME, "");
				Map<String, String> map = new HashMap<String, String>();
				map.put("uId", uId + "");
				map.put("fId", fId + "");
				map.put("uName", prefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.USERNAME, ""));
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					PassFriendsAppAsync(path, map);
				} else {
					Toast.makeText(context, "请检查你的网络...", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				break;
			case 2:// 右滑删除
				path = URLConstants.删除申请好友;// + "?uId=" + uId + "&fId=" +
											// fId+"&uName="+prefUtil.getString(context,
											// ShareFile.USERFILE,
											// ShareFile.USERNAME, "");
				Map<String, String> deleteMap = new HashMap<String, String>();
				deleteMap.put("uId", uId + "");
				deleteMap.put("fId", fId + "");
				deleteMap.put("uName", prefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.USERNAME, ""));
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					DeleteFriendsAppAsync(path, deleteMap);
				} else {
					Toast.makeText(context, "请检查你的网络...", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_left:
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		// mList.clear();
		// initdata();
		onLoad();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoadMore() {
		// mList.clear();
		// initdata();
		// adapter.notifyDataSetChanged();
	}

	private void onLoad() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date = format.format(new Date());
		SwipeXListViewNoHead swipeXListView = null;
		if (myfriends_lv.getVisibility() == View.VISIBLE) {
			swipeXListView = myfriends_lv;
		} else {
			// swipeXListView = myImpotent_listview;
		}
		swipeXListView.stopRefresh();
		swipeXListView.stopLoadMore();
		swipeXListView.setRefreshTime("刚刚" + date);
	}

	/**
	 * 查询好友申请信息
	 * 
	 * @author li
	 * 
	 */
	private void MyFriendsAppAsync(String path) {
		progressUtil.ShowProgress(context, true, true, "正在努力加载......");
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							MyFriendsBackBean backBean = gson.fromJson(result,
									MyFriendsBackBean.class);
							if (backBean.status == 0) {
								bsqFriendList = backBean.bsqFriend;
								sqFrendList = backBean.sqFrend;
								if (bsqFriendList != null
										&& bsqFriendList.size() > 0
										&& sqFrendList != null
										&& sqFrendList.size() > 0) {
									mList.addAll(bsqFriendList);
									mList.addAll(sqFrendList);
								} else if (bsqFriendList != null
										&& bsqFriendList.size() > 0) {
									mList.clear();
									mList.addAll(bsqFriendList);
								} else if (sqFrendList != null
										&& sqFrendList.size() > 0) {
									mList.clear();
									mList.addAll(sqFrendList);
								} else {
									mList.clear();
									mList = new ArrayList<FriendsBean>();
								}
								adapter = new MyFriendsAppAdapter(context,
										mList,R.layout.adapter_myfriends, handler, myfriends_lv);
								myfriends_lv.setAdapter(adapter);
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	/**
	 * 确认好友申请信息
	 */
	private void AgreeFriendsAppAsync(String path, final Map<String, String> map) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							SuccessOrFailBean beans = gson.fromJson(result,
									SuccessOrFailBean.class);
							if (beans.status == 0) {
								mList.clear();
								alertDialog1();
								initdata();
								adapter.notifyDataSetChanged();
								Toast.makeText(context, "添加好友成功...",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(context, "添加好友失败...",
										Toast.LENGTH_SHORT).show();
								return;
							}
						} else {
							Toast.makeText(context, "添加好友失败...",
									Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return map;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	/**
	 * 忽略好友申请信息
	 */
	private void PassFriendsAppAsync(String path, final Map<String, String> map) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							SuccessOrFailBean beans = gson.fromJson(result,
									SuccessOrFailBean.class);
							if (beans.status == 0) {
								mList.clear();
								alertDialog();
								initdata();
								adapter.notifyDataSetChanged();
							} else {
								return;
							}
						} else {
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return map;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	/**
	 * 删除申请好友信息
	 */
	private void DeleteFriendsAppAsync(String path,
			final Map<String, String> map) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							SuccessOrFailBean beans = gson.fromJson(result,
									SuccessOrFailBean.class);
							if (beans.status == 0) {
								mList.clear();
								alertDialog();
								initdata();
								adapter.notifyDataSetChanged();
							} else {
								return;
							}
						} else {
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return map;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void alertDialog() {
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
		delete_tv.setText("忽略成功！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	private void alertDialog1() {
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
		delete_tv.setText("添加成功！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	private static boolean isExit = false;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 3000);
		} else {
			activityManager.doAllActivityFinish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
