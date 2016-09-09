package com.mission.schedule.activity;

import java.util.ArrayList;
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
import com.mission.schedule.adapter.AddFriendsAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.AddFriendsBackBean;
import com.mission.schedule.bean.AddFriendsBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.ListViewForScrollView;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.PullToRefreshView;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;
import com.mission.schedule.utils.PullToRefreshView.OnFooterRefreshListener;
import com.mission.schedule.utils.PullToRefreshView.OnHeaderRefreshListener;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsSouSuoActivity extends BaseActivity implements
		OnClickListener, OnFooterRefreshListener, OnHeaderRefreshListener {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.myfriend_pull_refresh_view)
	private PullToRefreshView mPullToRefreshView = null;
	@ViewResId(id = R.id.sousuojieguo_tv)
	private TextView sousuojieguo_tv;
	@ViewResId(id = R.id.nosousuojieguo_tv)
	private TextView nosousuojieguo_tv;
	@ViewResId(id = R.id.addfriends_lv)
	private ListViewForScrollView addfriends_lv;
	@ViewResId(id = R.id.sousuocontent_et)
	private EditText sousuocontent_et;
	@ViewResId(id = R.id.sousuo_iv)
	private ImageView sousuo_iv;
	@ViewResId(id = R.id.view)
	private View view;

	Context context;
	private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部
	private boolean mRefreshFlag = false;// 判断是否刷新
	private boolean isDel = true;
	AddFriendsAdapter addFriendsAdapter;
	List<AddFriendsBean> addFriendsList;

	String path;
	String SouSuoPath;

	SharedPrefUtil prefUtil = null;
	String userId;
	String type;
	int position;
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		top_ll_left.setOnClickListener(this);
		sousuo_iv.setOnClickListener(this);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_friendssousuo);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userId = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		view.setVisibility(View.GONE);
		sousuojieguo_tv.setVisibility(View.GONE);
	}

	@Override
	protected void setAdapter() {

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AddFriendsBean friendsbean = (AddFriendsBean) msg.obj;
			position = msg.arg1;
			switch (msg.what) {
			case 0:
				String addPath = URLConstants.添加好友申请;
				Map<String, String> addPairs = new HashMap<String, String>();
				addPairs.put("uId", userId);
				addPairs.put("fId", friendsbean.uid + "");
				addPairs.put("uName", prefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.USERNAME, ""));
				// + "?uId="
				// + Integer.parseInt(userId)
				// + "&fId="
				// + friendsbean.uid
				// + "&uName="
				// + prefUtil.getString(context, ShareFile.USERFILE,
				// ShareFile.USERNAME, "");
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					AddFriendsAsync(addPath, addPairs);
				} else {
					Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				break;
			case 1:
				// int state;
				// if (!"".equals(friendsbean.attentionState)
				// && friendsbean.attentionState != null) {
				// state = Integer.parseInt(friendsbean.attentionState);
				// } else {
				// state = 0;
				// }
				// Intent intent = new Intent(context,
				// FriendsCRYActivity.class);
				// intent.putExtra("fid", friendsbean.uid);
				// intent.putExtra("name", friendsbean.uname);
				// intent.putExtra("friendsimage", friendsbean.titleImg);
				// intent.putExtra("friendsbackimage", friendsbean.backImage);
				// intent.putExtra("attentionState", state);
				// startActivity(intent);
				break;

			}
		}

	};

	private void initdata() {
		if (Utils.checkMobilePhone(sousuocontent_et.getText().toString())) {
			type = "1";
		} else if (Utils.isEmail(sousuocontent_et.getText().toString())) {
			type = "2";
		} else {
			type = "0";
		}
		String uname = sousuocontent_et.getText().toString().trim();
		SouSuoPath = URLConstants.搜索添加好友;
		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("uId", userId);
		pairs.put("uName", uname);
		pairs.put("nowPage", "1");
		pairs.put("pageNum", "40");
		pairs.put("type", type);
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			SouSuoAsync(SouSuoPath, pairs);
		} else {
			Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_left:
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;
		case R.id.sousuo_iv:
			initdata();
			break;
		default:
			break;
		}
	}

	int i = 1;

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshHeadFlag = true;
				mRefreshFlag = true;
				i = 1;// 刷新头部时将页数初始化为1
				initdata();
				// mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 100);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshHeadFlag = false;
				mRefreshFlag = true;
				++i;
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("uId", userId);
				pairs.put("uName", sousuocontent_et.getText().toString().trim());
				pairs.put("nowPage", i + "");
				pairs.put("pageNum", "40");
				pairs.put("type", type);
				String URLpath = URLConstants.搜索添加好友;
				SouSuoAsync(URLpath, pairs);
			}
		}, 100);
	}

	private void SouSuoAsync(String path, final Map<String, String> map) {
		final ProgressUtil progressUtil = new ProgressUtil();
		if (!mRefreshFlag) {
			progressUtil.ShowProgress(context, true, true, "正在努力加载......");
		}
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (mRefreshFlag) {
							mPullToRefreshView.onHeaderRefreshComplete();
							mPullToRefreshView.onFooterRefreshComplete();
						} else {
							progressUtil.dismiss();
						}
						if (!TextUtils.isEmpty(result)) {
							try {
								if (mRefreshHeadFlag) {
									addFriendsAdapter = null;
								}
								Gson gson = new Gson();
								AddFriendsBackBean backBean = gson.fromJson(
										result, AddFriendsBackBean.class);
								if (backBean.status == 0) {
									if (addFriendsAdapter == null) {
										addFriendsList = backBean.list;
										if (addFriendsList != null
												&& addFriendsList.size() > 0) {
											nosousuojieguo_tv
													.setVisibility(View.GONE);
											sousuojieguo_tv
													.setVisibility(View.VISIBLE);
											view.setVisibility(View.VISIBLE);
										} else {
											nosousuojieguo_tv
													.setVisibility(View.VISIBLE);
											sousuojieguo_tv
													.setVisibility(View.GONE);
											view.setVisibility(View.GONE);
										}
										addFriendsAdapter = new AddFriendsAdapter(
												context, addFriendsList,
												R.layout.adapter_addfriends,
												handler);
										addfriends_lv
												.setAdapter(addFriendsAdapter);
									} else {
										addFriendsList.addAll(backBean.list);
										if (addFriendsList != null
												&& addFriendsList.size() > 0) {
											nosousuojieguo_tv
													.setVisibility(View.GONE);
											sousuojieguo_tv
													.setVisibility(View.VISIBLE);
											view.setVisibility(View.VISIBLE);
										} else {
											nosousuojieguo_tv
													.setVisibility(View.VISIBLE);
											sousuojieguo_tv
													.setVisibility(View.GONE);
											view.setVisibility(View.GONE);
										}
										addFriendsAdapter
												.notifyDataSetChanged();
									}
								} else {
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							if (isDel) {
								addFriendsList = new ArrayList<AddFriendsBean>();
								addFriendsAdapter = new AddFriendsAdapter(
										context, addFriendsList,
										R.layout.adapter_addfriends, handler);
								addfriends_lv.setAdapter(addFriendsAdapter);
							}
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						isDel = false;
						progressUtil.dismiss();
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

	private void AddFriendsAsync(String path, final Map<String, String> map) {
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在添加...");
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							SuccessOrFailBean bean = gson.fromJson(result,
									SuccessOrFailBean.class);
							if (bean.status == 0) {
								AddFriendsBean friendsBean = (AddFriendsBean) addfriends_lv
										.getAdapter().getItem(position);
								friendsBean.flag = false;
								addFriendsAdapter.notifyDataSetChanged();
								alertDialog();
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
						progressUtil.dismiss();
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
