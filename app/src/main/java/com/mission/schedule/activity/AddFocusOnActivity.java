//package com.mission.schedule.activity;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
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
//import com.john.library.annotation.ViewResId;
//import com.john.library.util.NetUtil;
//import com.john.library.util.NetUtil.NetWorkState;
//import com.john.library.util.SharedPrefUtil;
//import com.mission.schedule.adapter.AddFocusOnAdapter;
//import com.mission.schedule.applcation.App;
//import com.mission.schedule.bean.FocusFriendsBackBean;
//import com.mission.schedule.bean.FocusFriendsBean;
//import com.mission.schedule.bean.FocusOtherBackBean;
//import com.mission.schedule.bean.FocusOtherBean;
//import com.mission.schedule.bean.SerachFocusBackBean;
//import com.mission.schedule.bean.SuccessOrFailBean;
//import com.mission.schedule.constants.ShareFile;
//import com.mission.schedule.constants.URLConstants;
//import com.mission.schedule.entity.ScheduleTable;
//import com.mission.schedule.fragment.MyFoundFragment;
//import com.mission.schedule.utils.ListViewForScrollView;
//import com.mission.schedule.utils.ProgressUtil;
//import com.mission.schedule.utils.PullToRefreshView;
//import com.mission.schedule.utils.Utils;
//import com.mission.schedule.utils.PullToRefreshView.OnFooterRefreshListener;
//import com.mission.schedule.utils.PullToRefreshView.OnHeaderRefreshListener;
//import com.mission.schedule.R;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class AddFocusOnActivity extends BaseActivity implements
//		OnClickListener, OnFooterRefreshListener, OnHeaderRefreshListener {
//
//	@ViewResId(id = R.id.top_ll_left)
//	private LinearLayout top_ll_left;
//	@ViewResId(id = R.id.myfriend_pull_refresh_view)
//	private PullToRefreshView mPullToRefreshView = null;
//	@ViewResId(id = R.id.sousuojieguo_tv)
//	private TextView sousuojieguo_tv;
//	@ViewResId(id = R.id.nosousuojieguo_tv)
//	private TextView nosousuojieguo_tv;
//	@ViewResId(id = R.id.likefocus_tv)
//	private TextView likefocus_tv;
//	@ViewResId(id = R.id.addfocus_lv)
//	private ListViewForScrollView addfocus_lv;
//	@ViewResId(id = R.id.addsousuofocus_lv)
//	private ListViewForScrollView addsousuofocus_lv;
//	@ViewResId(id = R.id.sousuocontent_et)
//	private EditText sousuocontent_et;
//	@ViewResId(id = R.id.sousuo_iv)
//	private ImageView sousuo_iv;
//	@ViewResId(id = R.id.view)
//	private View view;
//
//	Context context;
//	private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部
//	private boolean mRefreshFlag = false;// 判断是否刷新
//	private boolean isDel = true;
//	AddFocusOnAdapter addFriendsAdapter;
//	List<FocusFriendsBean> addFriendsList;
//
//	String path;
//	String SouSuoPath;
//
//	SharedPrefUtil prefUtil = null;
//	String userId;
//	String type;
//	int position;
//	FocusFriendsBean friendsbean = null;
//	List<FocusOtherBean> list = new ArrayList<FocusOtherBean>();
//	ProgressUtil progressUtil = new ProgressUtil();
//
//	int addType;
//	
//	App app = App.getDBcApplication();
//
//	@Override
//	protected void setListener() {
//		top_ll_left.setOnClickListener(this);
//		sousuo_iv.setOnClickListener(this);
//		mPullToRefreshView.setOnHeaderRefreshListener(this);
//		mPullToRefreshView.setOnFooterRefreshListener(this);
//	}
//
//	@Override
//	protected void setContentView() {
//		setContentView(R.layout.activity_addfocuson);
//	}
//
//	@Override
//	protected void init(Bundle savedInstanceState) {
//		context = this;
//		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
//		URLConstants.activityList.add(this);
//		userId = prefUtil.getString(context, ShareFile.USERFILE,
//				ShareFile.USERID, "");
//		sousuojieguo_tv.setVisibility(View.GONE);
//		addsousuofocus_lv.setVisibility(View.GONE);
//		View footView = LayoutInflater.from(context).inflate(
//				R.layout.activity_alarmfriends_footview, null);
//		addfocus_lv.addFooterView(footView);
//		addsousuofocus_lv.addFooterView(footView);
//		loadData();
//	}
//
//	@Override
//	protected void setAdapter() {
//
//	}
//
//	private void loadData() {
//		String path = URLConstants.查询推荐关注信息 + "?uid=" + userId + "&nowpage="
//				+ 1 + "&pageNum=" + 40;
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			TuiJianAsync(path);
//		} else {
//			Toast.makeText(context, "请检查您的网络...", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//	}
//
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			friendsbean = (FocusFriendsBean) msg.obj;
//			position = msg.arg1;
//			Intent intent = null;
//			String mytype = "";
//			int state;
//			switch (msg.what) {
//			case 0: // 添加关注
//				if ("0".equals(friendsbean.isV)) {
//					FocusDialog(1, friendsbean.uid);
//				} else {
//					FocusDialog(0, friendsbean.uid);
//				}
//				break;
//			case 1:// 进入日程
//				if (!"".equals(friendsbean.attentionState)
//						&& friendsbean.attentionState != null) {
//					state = Integer.parseInt(friendsbean.attentionState);
//				} else {
//					state = 0;
//				}
//				if ("0".equals(friendsbean.attentionState)) {
//					mytype = "1";
//				} else {
//					mytype = "2";
//				}
//				intent = new Intent(context, FriendsCRYActivity.class);
//				intent.putExtra("fid", friendsbean.uid);
//				intent.putExtra("name", friendsbean.uname);
//				intent.putExtra("friendsimage", friendsbean.titleImg);
//				intent.putExtra("friendsbackimage", friendsbean.backImage);
//				intent.putExtra("attentionState", state);
//				intent.putExtra("type", mytype);
//				startActivityForResult(intent, MyFoundFragment.MYFRIENDSITEM);
//				break;
//			case 2:
//				if (!"".equals(friendsbean.attentionState)
//						&& friendsbean.attentionState != null) {
//					state = Integer.parseInt(friendsbean.attentionState);
//				} else {
//					state = 0;
//				}
//				if ("0".equals(friendsbean.attentionState)) {
//					mytype = "1";
//				} else {
//					mytype = "2";
//				}
//				intent = new Intent(context, FriendsCRYActivity.class);
//				intent.putExtra("fid", friendsbean.uid);
//				intent.putExtra("name", friendsbean.uname);
//				intent.putExtra("friendsimage", friendsbean.titleImg);
//				intent.putExtra("friendsbackimage", friendsbean.backImage);
//				intent.putExtra("attentionState", state);
//				intent.putExtra("type", mytype);
//				startActivityForResult(intent, MyFoundFragment.MYFRIENDSITEM);
//				break;
//			}
//		}
//
//	};
//
//	private void initdata() {
//		if (Utils.checkMobilePhone(sousuocontent_et.getText().toString())) {
//			type = "2";
//		} else if (Utils.isEmail(sousuocontent_et.getText().toString())) {
//			type = "1";
//		} else {
//			type = "0";
//		}
//		SouSuoPath = URLConstants.搜索关注信息;
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			SouSuoAsync(SouSuoPath,"1",type);
//		} else {
//			Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
//			return;
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.top_ll_left:
//			Intent intent = new Intent();
//			setResult(Activity.RESULT_OK, intent);
//			this.finish();
//			break;
//		case R.id.sousuo_iv:
//			initdata();
//			break;
//		default:
//			break;
//		}
//	}
//
//	int i = 1;
//
//	@Override
//	public void onHeaderRefresh(PullToRefreshView view) {
//		mPullToRefreshView.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mRefreshHeadFlag = true;
//				mRefreshFlag = true;
//				i = 1;// 刷新头部时将页数初始化为1
//				initdata();
//				// mPullToRefreshView.onHeaderRefreshComplete();
//			}
//		}, 100);
//	}
//
//	@Override
//	public void onFooterRefresh(PullToRefreshView view) {
//		mPullToRefreshView.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mRefreshHeadFlag = false;
//				mRefreshFlag = true;
//				++i;
//				if (Utils.checkMobilePhone(sousuocontent_et.getText()
//						.toString())) {
//					type = "2";
//				} else if (Utils.isEmail(sousuocontent_et.getText().toString())) {
//					type = "1";
//				} else {
//					type = "0";
//				}
//				SouSuoPath = URLConstants.搜索关注信息;
//				SouSuoAsync(SouSuoPath,i + "",type);
//			}
//		}, 100);
//	}
//	private void SouSuoAsync(String path,final String indexpage,final String type){
//		if (!mRefreshFlag) {
//			progressUtil.ShowProgress(context, true, true, "正在努力加载......");
//		}
//		StringRequest request = new StringRequest(Method.POST, path, new Listener<String>() {
//
//			@Override
//			public void onResponse(String result) {
//				if (mRefreshFlag) {
//					mPullToRefreshView.onHeaderRefreshComplete();
//					mPullToRefreshView.onFooterRefreshComplete();
//				} else {
//					progressUtil.dismiss();
//				}
//				if (!TextUtils.isEmpty(result)) {
//					try {
//						if (mRefreshHeadFlag) {
//
//						}
//						Gson gson = new Gson();
//						SerachFocusBackBean backBean = gson.fromJson(result,
//								SerachFocusBackBean.class);
//						if (backBean.status == 0) {
//							addFriendsList.clear();
//							addFriendsList = backBean.page.items;
//							if (addFriendsList != null && addFriendsList.size() > 0) {
//								addsousuofocus_lv.setVisibility(View.VISIBLE);
//								addfocus_lv.setVisibility(View.GONE);
//								nosousuojieguo_tv.setVisibility(View.GONE);
//								sousuojieguo_tv.setVisibility(View.VISIBLE);
//								view.setVisibility(View.VISIBLE);
//								likefocus_tv.setVisibility(View.GONE);
//							} else {
//								addsousuofocus_lv.setVisibility(View.GONE);
//								addfocus_lv.setVisibility(View.VISIBLE);
//								nosousuojieguo_tv.setVisibility(View.VISIBLE);
//								sousuojieguo_tv.setVisibility(View.GONE);
//								view.setVisibility(View.GONE);
//								likefocus_tv.setVisibility(View.GONE);
//							}
//							addFriendsAdapter = new AddFocusOnAdapter(context,
//									addFriendsList, handler);
//							if (addFriendsList != null && addFriendsList.size() > 0) {
//								addsousuofocus_lv.setAdapter(addFriendsAdapter);
//							} else {
//								addfocus_lv.setAdapter(addFriendsAdapter);
//							}
//						} else {
//							return;
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				} else {
//					if (isDel) {
//						addFriendsList = new ArrayList<FocusFriendsBean>();
//						addFriendsAdapter = new AddFocusOnAdapter(context,
//								addFriendsList, handler);
//						addfocus_lv.setAdapter(addFriendsAdapter);
//					}
//					return;
//				}
//
//			}
//		}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError volleyError) {
//				progressUtil.dismiss();
//				isDel = false;
//			}
//		}){
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				Map<String, String> pairs = new HashMap<String, String>();
//				pairs.put("uid", userId);
//				pairs.put("uname", sousuocontent_et
//						.getEditableText().toString().trim());
//				pairs.put("nowpage", indexpage);
//				pairs.put("pageNum", "40");
//				pairs.put("type", type);
//				return pairs;
//			}
//		};
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//	private void AddFriendsAsync(String path){
//		StringRequest request = new StringRequest(Method.GET, path, new Listener<String>() {
//
//			@Override
//			public void onResponse(String result) {
//				if (!TextUtils.isEmpty(result)) {
//					Gson gson = new Gson();
//					SuccessOrFailBean bean = gson.fromJson(result,
//							SuccessOrFailBean.class);
//					if (bean.status == 0) {
//						if (addfocus_lv.getVisibility() == View.VISIBLE) {
//							loadData();
//							if (addType == 1) {
//								if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//									String downPath = URLConstants.刷新下载关注数据
//											+ userId + "&attentionId="
//											+ friendsbean.uid;
//									DownFocusSchAsync(downPath);
//								}
//							}
//						} else {
//							initdata();
//						}
//						addFriendsAdapter.notifyDataSetChanged();
//						alertDialog();
//					} else {
//						return;
//					}
//				} else {
//					return;
//				}
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				isDel = false;
//			}
//		});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//	private void alertDialog() {
//		final AlertDialog builder = new AlertDialog.Builder(context).create();
//		builder.show();
//		Window window = builder.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		params.gravity = Gravity.CENTER;
//		window.setAttributes(params);// 设置生效
//		window.setGravity(Gravity.CENTER);
//		window.setContentView(R.layout.dialog_alterfocus);
//		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
//		delete_ok.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				builder.cancel();
//			}
//		});
//
//	}
//	private void TuiJianAsync(String path){
//		progressUtil.ShowProgress(context, true, true, "正在加载...");
//		StringRequest request = new StringRequest(Method.GET, path, new Listener<String>() {
//
//			@Override
//			public void onResponse(String result) {
//				progressUtil.dismiss();
//				if (!TextUtils.isEmpty(result)) {
//					Gson gson = new Gson();
//					try {
//						FocusFriendsBackBean backBean = gson.fromJson(result,
//								FocusFriendsBackBean.class);
//						if (backBean.status == 0) {
//							addFriendsList = backBean.list;
//							if (addFriendsList != null && addFriendsList.size() > 0) {
//								addFriendsAdapter = new AddFocusOnAdapter(context,
//										addFriendsList, handler);
//								addfocus_lv.setAdapter(addFriendsAdapter);
//							} else {
//								return;
//							}
//						} else {
//							return;
//						}
//					} catch (JsonSyntaxException e) {
//						e.printStackTrace();
//					}
//				} else {
//					return;
//				}
//
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				progressUtil.dismiss();
//			}
//		});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
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
//
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == MyFoundFragment.MYFRIENDSITEM) {
//			if (resultCode == Activity.RESULT_OK) {
//
//			}
//		}
//	};
//
//	private void FocusDialog(int type, int fid) {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac.inflate(R.layout.dialog_serachfocus, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//
//		new FocusDialogOnClick(dialog, more_pop_menu, type, fid);
//	}
//
//	class FocusDialogOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private int fid;
//		private int type; // 0加入待关注列表 1直接加为关注和加入待关注列表
//		private TextView d_focus_tv;
//		private TextView delay_focus_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public FocusDialogOnClick(Dialog dialog, View view, int type, int fid) {
//			this.dialog = dialog;
//			this.view = view;
//			this.type = type;
//			this.fid = fid;
//			initview();
//		}
//
//		private void initview() {
//			d_focus_tv = (TextView) view.findViewById(R.id.d_focus_tv);
//			if (type == 0) {
//				d_focus_tv.setTextColor(context.getResources().getColor(
//						R.color.gongkai_txt));
//			} else {
//				d_focus_tv.setOnClickListener(this);
//				d_focus_tv.setTextColor(context.getResources().getColor(
//						R.color.black));
//			}
//			delay_focus_tv = (TextView) view.findViewById(R.id.delay_focus_tv);
//			delay_focus_tv.setOnClickListener(this);
//			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
//			canel_tv.setOnClickListener(this);
//		}
//
//		@Override
//		public void onClick(View v) {
//			String addPath = URLConstants.添加关注 + "?uid="
//					+ Integer.parseInt(userId) + "&fid=" + fid + "&type=";
//			switch (v.getId()) {
//			case R.id.d_focus_tv:
//				addType = 1;
//				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//					if ("0".equals(friendsbean.attentionState)) {
//						if ("1".equals(friendsbean.attState)) {
//							NewAddFocusAsync(URLConstants.修改关注状态
//									+ userId + "&fid=" + fid + "&state=" + 1);
//						} else {
//							AddFriendsAsync(addPath + 1);
//						}
//					} else {
//						AddFriendsAsync(addPath + 1);
//					}
//				} else {
//					Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT)
//							.show();
//					return;
//				}
//				dialog.dismiss();
//				break;
//			case R.id.delay_focus_tv:
//				addType = 0;
//				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//					AddFriendsAsync(addPath + 0);
//				} else {
//					Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT)
//							.show();
//					return;
//				}
//				dialog.dismiss();
//				break;
//			case R.id.canel_tv:
//				dialog.dismiss();
//				break;
//			}
//		}
//	}
//	private void DownFocusSchAsync(String path){
//		progressUtil.ShowProgress(context, true, true, "正在添加...");
//		StringRequest request = new StringRequest(Method.GET, path, new Listener<String>() {
//
//			@Override
//			public void onResponse(String result) {
//				progressUtil.dismiss();
//				progressUtil.dismiss();
//				if (!TextUtils.isEmpty(result)) {
//					try {
//						Gson gson = new Gson();
//						FocusOtherBackBean backBean = gson.fromJson(result,
//								FocusOtherBackBean.class);
//						if (backBean.status == 0) {
//							list.clear();
//							list = backBean.list;
//							for (int i = 0; i < list.size(); i++) {
//								if (list.get(i).CState != 3) {
//									int count = App.getDBcApplication()
//											.CheckFocusIDData(list.get(i).id);
//									if (count == 0) {
//										App.getDBcApplication().insertScheduleData(
//												list.get(i).CContent,
//												list.get(i).CDate,
//												list.get(i).CTime,
//												list.get(i).CIsAlarm,
//												list.get(i).CBefortime,
//												list.get(i).CDisplayAlarm, 0, 0, 0,
//												0, list.get(i).CCreateTime, "",
//												list.get(i).CType,
//												list.get(i).CTypeDesc,
//												list.get(i).CTypeSpare, 0, "",
//												list.get(i).CUpdateTime, 0, 0, 0,
//												list.get(i).CAlarmsoundDesc,
//												list.get(i).CAlarmsound,
//												list.get(i).cuIckName, 1,
//												list.get(i).id, list.get(i).aType,
//												list.get(i).webUrl,
//												list.get(i).imgPath, 0,
//												list.get(i).CUid, 0);
//									} else {
//										if ("0".equals(App
//												.getDBcApplication()
//												.QueryFocusStateData(list.get(i).id)
//												.get(ScheduleTable.schFocusState))) {
//											App.getDBcApplication()
//													.updateFocusScheduleData(
//															list.get(i).CContent,
//															list.get(i).CDate,
//															list.get(i).CTime,
//															list.get(i).CIsAlarm,
//															list.get(i).CBefortime,
//															list.get(i).CDisplayAlarm,
//															0,
//															0,
//															0,
//															0,
//															"",
//															list.get(i).CType,
//															list.get(i).CTypeDesc,
//															list.get(i).CTypeSpare,
//															0,
//															"",
//															list.get(i).CUpdateTime,
//															0,
//															0,
//															0,
//															list.get(i).CAlarmsoundDesc,
//															list.get(i).CAlarmsound,
//															list.get(i).cuIckName,
//															1, list.get(i).id,
//															list.get(i).aType,
//															list.get(i).webUrl,
//															list.get(i).imgPath, 0,
//															list.get(i).CUid, 0);
//										}
//									}
//								} else if (list.get(i).CState == 3) {
//									App.getDBcApplication()
//											.deleteFocusScheduleData(list.get(i).id);
//								}
//							}
//						}
//					} catch (JsonSyntaxException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				progressUtil.dismiss();
//			}
//		});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//	/**
//	 * 添加关注
//	 */
//	private void NewAddFocusAsync(String path){
//		progressUtil.ShowProgress(context, true, true, "正在添加...");
//		StringRequest request = new StringRequest(Method.GET, path, new Listener<String>() {
//
//			@Override
//			public void onResponse(String result) {
//				progressUtil.dismiss();
//				progressUtil.dismiss();
//				if (!TextUtils.isEmpty(result)) {
//					Gson gson = new Gson();
//					SuccessOrFailBean beans = gson.fromJson(result,
//							SuccessOrFailBean.class);
//					if (beans.status == 0) {
//						loadData();
//						if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//							String downPath = URLConstants.刷新下载关注数据 + userId
//									+ "&attentionId=" + friendsbean.uid;
//							DownFocusSchAsync(downPath);
//						}
//					} else {
//						return;
//					}
//				} else {
//					return;
//				}
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				progressUtil.dismiss();
//			}
//		});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		App.getHttpQueues().cancelAll("down");
//	}
//}
