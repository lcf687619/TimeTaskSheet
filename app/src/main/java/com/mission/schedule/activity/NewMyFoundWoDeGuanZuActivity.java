package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.adapter.NewMyFoundWoDeGuanZuAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewMyFoundShouChangBeen;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueBeen;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueListBeen;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.PullToRefreshViewNoFooter;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnFooterRefreshListener;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnHeaderRefreshListener;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewMyFoundWoDeGuanZuActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener,OnHeaderRefreshListener, OnFooterRefreshListener {
	@ViewResId(id = R.id.focusName_tv)
	private TextView focusName_tv;// 大标题
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.pull_refresh_view)
	PullToRefreshViewNoFooter pull_refresh_view;

	App app = null;
	SwipeMenuListView friends_lv;
	Context context;
	SharedPrefUtil prefUtil = null;
	String path;
	String UserID;
	ProgressUtil progressUtil = new ProgressUtil();
	List<NewMyFoundShouChangListBeen> shouchangList = new ArrayList<NewMyFoundShouChangListBeen>();
	NewMyFoundWoDeGuanZuAdapter shouchangAdapter = null;
	String datetime = "";
	String jsonArrayStr = "";
	List<NewMyFoundShouChangDingYueListBeen> list = new ArrayList<NewMyFoundShouChangDingYueListBeen>();
	View headView;
	View bottomView;
	private boolean mRefreshFlag = false;// 判断是否刷新
	private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newmyfound_wodeguanzhu);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		UserID = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		jsonArrayStr = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.SHOUCANGDATA, "");
		top_ll_right.setVisibility(View.INVISIBLE);
		focusName_tv.setText("我的收藏");
		friends_lv = (SwipeMenuListView) findViewById(R.id.friends_lv);
		headView = LayoutInflater.from(context).inflate(
				R.layout.activity_basecommond_headview, null);
		bottomView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		friends_lv.addHeaderView(headView);
		friends_lv.addFooterView(bottomView);
		pull_refresh_view.setOnHeaderRefreshListener(this);
		pull_refresh_view.setOnFooterRefreshListener(this);
		pull_refresh_view.setFocusable(false);
		loadData();
		friends_lv.setOnItemClickListener(this);
		friends_lv.setFocusable(true);
		otherview();
	}

	private void otherview() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// Create different menus depending on the view type
				switch (menu.getViewType()) {
				case 0:
					createMenu1(menu);
					break;
				// case 1:
				// createMenu2(menu);
				// break;
				// case 2:
				// createMenu3(menu);
				// break;
				}
			}

			private void createMenu1(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(context);
				item1.setBackground(new ColorDrawable(Color
						.parseColor("#55b192")));
				item1.setWidth(dp2px(90));
				// item1.setIcon(R.drawable.ic_action_discard);
				item1.setTitle("取消收藏");
				// set item title fontsize
				item1.setTitleSize(16);
				// set item title font color
				item1.setTitleColor(Color.WHITE);
				menu.addMenuItem(item1);
			}
		};
		friends_lv.setMenuCreator(creator);

		friends_lv.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				NewMyFoundShouChangListBeen item = (NewMyFoundShouChangListBeen) shouchangList
						.get(position);
				switch (index) {
				case 0:
					// delete
					// delete(item);
					String path = URLConstants.新版发现取消收藏 + "?userId=" + UserID
							+ "&attentionId=" + item.id;
					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
						int newdeletenum = item.id;
						DeleteFriendsAppAsync(path, newdeletenum);
					} else {
						return;
					}
					break;
				}
			}
		});

	}

	/**
	 * 取消收藏
	 */
	private void DeleteFriendsAppAsync(String path, final int deleteid) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							SuccessOrFailBean beans = gson.fromJson(result,
									SuccessOrFailBean.class);
							if (beans.status == 0) {
								loadData();
								shouchangAdapter.notifyDataSetChanged();
								app.deleteSchAIDData(deleteid);
								QueryAlarmData
										.writeAlarm(getApplicationContext());
							} else {
								Toast.makeText(context, "取消收藏失败...",
										Toast.LENGTH_SHORT).show();
								return;
							}
						} else {
							Toast.makeText(context, "取消收藏失败...",
									Toast.LENGTH_SHORT).show();
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

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	private void loadData() {
		path = URLConstants.新版发现我的收藏 + "?userId=" + UserID;
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			LoadDataAsync(path);
		} else {
			if (mRefreshFlag) {
				pull_refresh_view.onHeaderRefreshComplete();
				// mPullToRefreshView.onFooterRefreshComplete();
			}
			if (!"".equals(jsonArrayStr)) {
				try {
					JSONArray jsonArray = new JSONArray(jsonArrayStr);
					for (int i = 0; i < jsonArray.length(); i++) {
						NewMyFoundShouChangListBeen been = new NewMyFoundShouChangListBeen();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						been.id = jsonObject.getInt("id");
						been.clickCount = jsonObject.getInt("clickCount");
						been.attentionState = jsonObject
								.getInt("attentionState");
						been.date = jsonObject.getString("date");
						been.content = jsonObject.getString("content");
						been.time = jsonObject.getString("time");
						been.name = jsonObject.getString("name");
						been.titleImg = jsonObject.getString("titleImg");
						been.startStateImg = jsonObject
								.getString("startStateImg");
						shouchangList.add(been);
					}
					shouchangAdapter = new NewMyFoundWoDeGuanZuAdapter(
							context,
							shouchangList,
							R.layout.adapter_newmyfound_wodeshouchang_list_item,
							handler);
					friends_lv.setAdapter(shouchangAdapter);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressUtil.dismiss();
			NewMyFoundShouChangListBeen bean = null;
			if (msg.what != 0) {
				bean = (NewMyFoundShouChangListBeen) msg.obj;
			}
			switch (msg.what) {
			case 0:
				loadData();
				break;
			case 3:
				if (bean != null) {
					dialogDingYueOnClick(bean);
				}
				break;
			default:
				break;
			}
		}

	};

	private void dialogDingYueOnClick(NewMyFoundShouChangListBeen mMap) {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_friendscircle, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		dialog.show();

		new DingYueOnClick(dialog, mMap, more_pop_menu);
	}

	class DingYueOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private NewMyFoundShouChangListBeen mMap;
		private TextView zhuanfafriends_tv;
		private TextView addricheng_tv;
		private TextView canel_tv;

		@SuppressLint("NewApi")
		public DingYueOnClick(Dialog dialog, NewMyFoundShouChangListBeen mMap,
				View view) {
			this.dialog = dialog;
			this.mMap = mMap;
			this.view = view;
			initview();
		}

		public void initview() {
			zhuanfafriends_tv = (TextView) view
					.findViewById(R.id.zhuanfafriends_tv);
			zhuanfafriends_tv.setVisibility(View.GONE);
			// zhuanfafriends_tv.setOnClickListener(this);
			addricheng_tv = (TextView) view.findViewById(R.id.addricheng_tv);
			addricheng_tv.setOnClickListener(this);
			if (0 == mMap.attentionState) {
				addricheng_tv.setText("取消订阅");
			} else {
				addricheng_tv.setText("订阅");
			}
			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
			canel_tv.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.addricheng_tv:
				if (0 == mMap.attentionState) {// 已订阅
					String Path = URLConstants.新版发现收藏修改订阅 + "?userId=" + UserID
							+ "&id=" + mMap.id + "&type=" + 1;
					NewDeleteFocusAsync(Path);
				} else {
					String Path = URLConstants.新版发现收藏修改订阅 + "?userId=" + UserID
							+ "&id=" + mMap.id + "&type=" + 0;
					NewAddFocusAsync(Path);
				}
				dialog.dismiss();
				break;
			case R.id.canel_tv:
				dialog.dismiss();
				break;
			default:
				break;
			}
		}

		/*
		 * 取消订阅
		 */
		private void NewDeleteFocusAsync(String path) {
			progressUtil.ShowProgress(context, true, true, "正在更改...");
			StringRequest request = new StringRequest(Method.GET, path,
					new Listener<String>() {

						@Override
						public void onResponse(String result) {
							progressUtil.dismiss();
							if (!TextUtils.isEmpty(result)) {
								Gson gson = new Gson();
								SuccessOrFailBean beans = gson.fromJson(result,
										SuccessOrFailBean.class);
								if (beans.status == 0) {
									loadData();
									int newdeletenum = mMap.id;
									app.deleteSchAIDData(newdeletenum);
									QueryAlarmData
											.writeAlarm(getApplicationContext());
								} else {
									return;
								}
							} else {
								return;
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							progressUtil.dismiss();
						}
					});
			request.setTag("down");
			request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
			App.getHttpQueues().add(request);
		}

		/*
		 * 订阅
		 */
		private void NewAddFocusAsync(String path) {
			datetime = prefUtil.getString(getApplication(), ShareFile.USERFILE,
					ShareFile.FIRSTDOWNFOCUSSCH, "2016-01-01 00:00:00");
			if ("".equals(datetime)) {
				datetime = "2016-01-01 00:00:00";
			}
			datetime = datetime.replace(" ", "%2B");
			progressUtil.ShowProgress(context, true, true, "正在添加...");
			StringRequest request = new StringRequest(Method.GET, path,
					new Listener<String>() {

						@Override
						public void onResponse(String result) {
							progressUtil.dismiss();
							if (!TextUtils.isEmpty(result)) {
								Gson gson = new Gson();
								SuccessOrFailBean beans = gson.fromJson(result,
										SuccessOrFailBean.class);
								if (beans.status == 0) {
									loadData();
									if (NetUtil.getConnectState(context) != NetWorkState.NONE) {

										String downPath = URLConstants.新版发现收藏下行数据到日程
												+ "?uid="
												+ mMap.id
												+ "&dateTime="
												+ datetime
												+ "&type=0";
										DownFocusSchAsync(downPath);
									}
								} else {
									return;
								}
							} else {
								return;
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							progressUtil.dismiss();
						}
					});
			request.setTag("down");
			request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
			App.getHttpQueues().add(request);
		}

		private void DownFocusSchAsync(String path) {
			StringRequest request = new StringRequest(Method.GET, path,
					new Listener<String>() {

						@Override
						public void onResponse(String result) {
							if (!TextUtils.isEmpty(result)) {
								try {
									Gson gson = new Gson();
									NewMyFoundShouChangDingYueBeen backBean = gson
											.fromJson(
													result,
													NewMyFoundShouChangDingYueBeen.class);
									if (backBean.status == 0) {
										list.clear();
										list = backBean.list;
										for (int i = 0; i < list.size(); i++) {
											// if (list.get(i).CState != 3) {
											int count = App.getDBcApplication()
													.CheckFocusIDData(
															list.get(i).CId);
											if (count == 0) {
												App.getDBcApplication()
														.insertScheduleData(
																list.get(i).CContent,
																DateUtil.formatDate(DateUtil
																		.parseDate(list
																				.get(i).CDate)),
																DateUtil.formatDateTimeHm(DateUtil
																		.parseDateTimeHm(list
																				.get(i).CTime)),
																list.get(i).CIsAlarm,
																list.get(i).CBefortime,
																list.get(i).CDisplayAlarm,
																0,
																0,
																0,
																0,
																list.get(i).CCreateTime
																		.replace(
																				"T",
																				" "),
																"",
																list.get(i).CType,
																list.get(i).CTypeDesc,
																list.get(i).CTypeSpare,
																0,
																"",
																list.get(i).CUpdateTime,
																0,
																0,
																0,
																list.get(i).CAlarmsoundDesc,
																list.get(i).CAlarmsound,
																mMap.name,
																1,
																list.get(i).CId,
																list.get(i).atype,
																list.get(i).webUrl,
																list.get(i).imgPath,
																0,
																0,
																list.get(i).CUid);
											} else {
												if ("0".equals(App
														.getDBcApplication()
														.QueryFocusStateData(
																list.get(i).CId)
														.get(ScheduleTable.schFocusState))) {
													App.getDBcApplication()
															.updateFocusScheduleData(
																	list.get(i).CContent,
																	DateUtil.formatDate(DateUtil
																			.parseDate(list
																					.get(i).CDate)),
																	DateUtil.formatDateTimeHm(DateUtil
																			.parseDateTimeHm(list
																					.get(i).CTime)),
																	list.get(i).CIsAlarm,
																	list.get(i).CBefortime,
																	list.get(i).CDisplayAlarm,
																	0,
																	0,
																	0,
																	0,
																	"",
																	list.get(i).CType,
																	list.get(i).CTypeDesc,
																	list.get(i).CTypeSpare,
																	0,
																	"",
																	list.get(i).CUpdateTime
																			.replace(
																					"T",
																					" "),
																	0,
																	0,
																	0,
																	list.get(i).CAlarmsoundDesc,
																	list.get(i).CAlarmsound,
																	mMap.name,
																	1,
																	list.get(i).CId,
																	list.get(i).atype,
																	list.get(i).webUrl,
																	list.get(i).imgPath,
																	0,
																	0,
																	list.get(i).CUid);
												}
											}
											// } else if (list.get(i).CState ==
											// 3) {
											// App.getDBcApplication()
											// .deleteFocusScheduleData(list.get(i).id);
											// }
										}
										QueryAlarmData
												.writeAlarm(getApplicationContext());
									}
								} catch (JsonSyntaxException e) {
									e.printStackTrace();
								}
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
						}
					});
			request.setTag("down");
			request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
			App.getHttpQueues().add(request);
		}
	}

	private void LoadDataAsync(String path) {
		if ("".equals(jsonArrayStr)) {
			if(!mRefreshFlag){
				progressUtil.ShowProgress(context, true, true, "正在加载数据...");
			}
		}
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (mRefreshFlag) {
							pull_refresh_view.onHeaderRefreshComplete();
							// mPullToRefreshView.onFooterRefreshComplete();
						}
						if (!TextUtils.isEmpty(result)) {
							try {
								if (mRefreshHeadFlag) {
									shouchangAdapter = null;
								}
								Gson gson = new Gson();
								NewMyFoundShouChangBeen backBean = gson
										.fromJson(result,
												NewMyFoundShouChangBeen.class);
								if (backBean.status == 0) {
									if (shouchangList != null
											&& shouchangList.size() > 0) {
										shouchangList.clear();
									}
									clearSCData(context);
									if (shouchangAdapter == null) {
										shouchangList = backBean.list;
										if (shouchangList != null
												&& shouchangList.size() > 0) {
											JSONArray jsonArray = new JSONArray();
											for (NewMyFoundShouChangListBeen been : shouchangList) {
												JSONObject jsonObject = new JSONObject();
												try {
													jsonObject.put(
															"clickCount",
															been.clickCount);
													jsonObject
															.put("attentionState",
																	been.attentionState);
													jsonObject.put("date",
															been.date);
													jsonObject.put("content",
															been.content);
													jsonObject.put("id",
															been.id);
													jsonObject.put("time",
															been.time);
													jsonObject.put("name",
															been.name);
													jsonObject
															.put("titleImg",
																	been.titleImg
																			.replace(
																					"\\/",
																					""));
													jsonObject.put(
															"startStateImg",
															been.startStateImg);
													jsonArray.put(jsonObject);
												} catch (JSONException e) {
													e.printStackTrace();
												}

											}
											save(context, jsonArray.toString());
											shouchangAdapter = new NewMyFoundWoDeGuanZuAdapter(
													context,
													shouchangList,
													R.layout.adapter_newmyfound_wodeshouchang_list_item,
													handler);
											friends_lv
													.setAdapter(shouchangAdapter);
										}
									} else {
											shouchangList.addAll(backBean.list);
											// setListViewHeightBasedOnChildren(friends_lv);
											shouchangAdapter
													.notifyDataSetChanged();
									}
								}else if(backBean.status==1){
									clearSCData(context);
									if (shouchangList != null
											&& shouchangList.size() > 0) {
										shouchangList.clear();
									}
									shouchangAdapter = new NewMyFoundWoDeGuanZuAdapter(
											context,
											shouchangList,
											R.layout.adapter_newmyfound_wodeshouchang_list_item,
											handler);
									friends_lv
											.setAdapter(shouchangAdapter);
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
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
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		NewMyFoundShouChangListBeen bean = (NewMyFoundShouChangListBeen) friends_lv
				.getAdapter().getItem(position);
		Intent intent = null;
		if (bean != null) {
			updataclockcount(bean.id);
			if ("1".equals(bean.styleView)) {
				intent = new Intent(context, NewFocusMobleTwoActivity.class);
			} else if ("2".equals(bean.styleView)) {
				intent = new Intent(context, NewFocusMobleThreeActivity.class);
			} else {
				intent = new Intent(context, NewFocusOnCRYActivity.class);
			}
			intent.putExtra("fid", Integer.valueOf(bean.id));
			intent.putExtra("name", bean.name);
			intent.putExtra("friendsimage", bean.titleImg);
			intent.putExtra("friendsbackimage", bean.backgroundImg);
			intent.putExtra("imagetype", bean.startStateImg);
			intent.putExtra("remark6", StringUtils.getIsStringEqulesNull(bean.remark6));
			if ("".equals(bean.remark5) || "null".equals(bean.remark5)
					|| null == bean.remark5) {
				intent.putExtra("othername", bean.name);
			} else {
				intent.putExtra("othername", bean.remark5);
			}
			startActivityForResult(intent, 2);
		}
	}

	private void save(Context context, String jsonarray) {// 写入SharedPreferences
		prefUtil.putString(context, ShareFile.USERFILE, ShareFile.SHOUCANGDATA,
				jsonarray);
	}

	public void clearSCData(Context context) {// 清空闹钟
		prefUtil.putString(context, ShareFile.USERFILE, ShareFile.SHOUCANGDATA,
				"");
	}

	private void updataclockcount(int id) {
		String path = URLConstants.新版发现热门推荐点击增加点击数量 + "?userId=" + id;
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				});
		request.setTag("foundcount");
		request.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("foundcount");
		App.getHttpQueues().cancelAll("down");
	}

	@Override
	public void onFooterRefresh(PullToRefreshViewNoFooter view) {
		pull_refresh_view.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshHeadFlag = false;
				mRefreshFlag = true;
				pull_refresh_view.onFooterRefreshComplete();
				// path = URLConstants.好友列表查询 + "?uId=" +
				// Integer.parseInt(userID);
				// new FriendsAppAsync().execute(path);
				// loadCount();
			}
		}, 100);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshViewNoFooter view) {
		pull_refresh_view.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshHeadFlag = true;
				mRefreshFlag = true;
				loadData();
				// mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 100);
	}
}
