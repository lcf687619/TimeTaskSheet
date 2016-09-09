package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.mission.schedule.adapter.NewMyFoundWoDeFengXiangAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewMyFoundShouChangBeen;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLFindScheduleTable;
import com.mission.schedule.service.NewFocusShareService;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnFooterRefreshListener;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnHeaderRefreshListener;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.PullToRefreshViewNoFooter;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewMyFoundWoDeFengXiangActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener,OnHeaderRefreshListener, OnFooterRefreshListener {
	@ViewResId(id = R.id.focusName_tv)
	private TextView focusName_tv;// 大标题
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	SwipeMenuListView friends_lv;
	@ViewResId(id = R.id.pull_refresh_view)
	PullToRefreshViewNoFooter pull_refresh_view;

	Context context;
	SharedPrefUtil prefUtil = null;
	String path;
	String UserID;
	ProgressUtil progressUtil = new ProgressUtil();
	List<NewMyFoundShouChangListBeen> fengxingList = new ArrayList<NewMyFoundShouChangListBeen>();
	NewMyFoundWoDeFengXiangAdapter fengxiangAdapter = null;
	public static final int MYFRIENDSITEM = 6;// 新建分享
	View headView;
	View bottomView;
	App app = null;
	List<Map<String, String>> aftertodayList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> oldtodayList = new ArrayList<Map<String, String>>();
	String jsonArrayStr = "";
	private boolean mRefreshFlag = false;// 判断是否刷新
	private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MYFRIENDSITEM) {
			if (resultCode == Activity.RESULT_OK) {
				loadData();
				if(fengxiangAdapter!=null){
					fengxiangAdapter.notifyDataSetChanged();
				}
			}
		} else if (requestCode == 100) {
			if (resultCode == Activity.RESULT_OK) {
				loadData();
				if(fengxiangAdapter!=null){
					fengxiangAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newmyfound_wodefengxiang);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		app = App.getDBcApplication();
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		UserID = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		focusName_tv.setText("我的分享");
		friends_lv = (SwipeMenuListView) findViewById(R.id.friends_lv);
		headView = LayoutInflater.from(context).inflate(
				R.layout.activity_basecommond_headview, null);
		bottomView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		friends_lv.addHeaderView(headView);
		friends_lv.addFooterView(bottomView);
		loadData();
		IsIntenetForService();
		friends_lv.setOnItemClickListener(this);
		friends_lv.setFocusable(true);
		pull_refresh_view.setOnHeaderRefreshListener(this);
		pull_refresh_view.setOnFooterRefreshListener(this);
		pull_refresh_view.setFocusable(false);
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
					createMenu0(menu);
					break;
				case 1:
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
				item1.setTitle("删除分享");
				// set item title fontsize
				item1.setTitleSize(16);
				// set item title font color
				item1.setTitleColor(Color.WHITE);
				menu.addMenuItem(item1);
			}

			private void createMenu0(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(context);
				item1.setWidth(0);
				menu.addMenuItem(item1);
			}
		};
		friends_lv.setMenuCreator(creator);

		friends_lv.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				if (menu.getViewType() == 1) {
					NewMyFoundShouChangListBeen item = (NewMyFoundShouChangListBeen) fengxingList
							.get(position);
					switch (index) {
					case 0:
						// delete
						// delete(item);
						String path = URLConstants.新版发现删除分享 + "?id=" + item.id;
						if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
							int newdeletenum = item.id;
							DeleteFriendsAppAsync(path, newdeletenum);
						} else {
							return;
						}
						break;
					}
				} else {

				}
			}
		});

	}

	private void IsIntenetForService() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(context, NewFocusShareService.class);
				intent.setAction("upanddown");
				intent.setPackage(getPackageName());
				startService(intent);
			}
		}).start();
	}

	private void loadData() {
		path = URLConstants.新版发现我的分享 + "?userId=" + UserID + "&type=0";
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
						fengxingList.add(been);
					}
					for (NewMyFoundShouChangListBeen listBeen : fengxingList) {
						aftertodayList = app.QueryNewFocusData(8, listBeen.id);
						if (aftertodayList != null && aftertodayList.size() > 0) {
							listBeen.content = aftertodayList.get(0).get(
									CLFindScheduleTable.fstContent);
							listBeen.displaytime = Integer
									.parseInt(aftertodayList.get(0).get(
											CLFindScheduleTable.fstDisplayTime));
							listBeen.date = aftertodayList.get(0).get(
									CLFindScheduleTable.fstDate);
							listBeen.time = aftertodayList.get(0).get(
									CLFindScheduleTable.fstTime);
							// fengxingList.add(listBeen);
						} else {
							oldtodayList = app
									.QueryNewFocusData(9, listBeen.id);
							if (oldtodayList != null && oldtodayList.size() > 0) {
								listBeen.content = oldtodayList.get(
										oldtodayList.size() - 1).get(
										CLFindScheduleTable.fstContent);
								listBeen.displaytime = Integer
										.parseInt(oldtodayList
												.get(oldtodayList.size() - 1)
												.get(CLFindScheduleTable.fstDisplayTime));
								listBeen.date = oldtodayList.get(
										oldtodayList.size() - 1).get(
										CLFindScheduleTable.fstDate);
								listBeen.time = oldtodayList.get(
										oldtodayList.size() - 1).get(
										CLFindScheduleTable.fstTime);
								// fengxingList.add(listBeen);
							} else {
								listBeen.content = "";
							}
						}
					}
					fengxiangAdapter = new NewMyFoundWoDeFengXiangAdapter(
							context,
							fengxingList,
							R.layout.adapter_newmyfound_wodefengxiang_list_item,
							handler);
					friends_lv.setAdapter(fengxiangAdapter);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			NewMyFoundShouChangListBeen bean = (NewMyFoundShouChangListBeen) msg.obj;
			switch (msg.what) {
			case 0: // 编辑
				if (bean != null) {
					Intent intent = new Intent(context,
							NewFocusShareEditActivity.class);
					intent.putExtra("bean", bean);
					startActivityForResult(intent, 100);
				}
				break;
			default:
				break;
			}
		}

	};

	private void LoadDataAsync(String path) {
		if (!mRefreshFlag) {
			progressUtil.ShowProgress(context, true, true, "正在加载数据...");
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
									fengxiangAdapter = null;
								}
								Gson gson = new Gson();
								NewMyFoundShouChangBeen backBean = gson
										.fromJson(result,
												NewMyFoundShouChangBeen.class);
								if (backBean.status != 2) {
									if(fengxingList!=null&&fengxingList.size()>0){
										fengxingList.clear();
									}
									if (fengxiangAdapter == null) {
										fengxingList = backBean.list;
										if (fengxingList != null
												&& fengxingList.size() > 0) {
											aftertodayList.clear();
											oldtodayList.clear();
											clearSCData(context);
											JSONArray jsonArray = new JSONArray();
											for (NewMyFoundShouChangListBeen listBeen : fengxingList) {
												aftertodayList = app
														.QueryNewFocusData(8,
																listBeen.id);
												if (aftertodayList != null
														&& aftertodayList
																.size() > 0) {
													listBeen.content = aftertodayList
															.get(0)
															.get(CLFindScheduleTable.fstContent);
													listBeen.displaytime = Integer
															.parseInt(aftertodayList
																	.get(0)
																	.get(CLFindScheduleTable.fstDisplayTime));
													listBeen.date = aftertodayList
															.get(0)
															.get(CLFindScheduleTable.fstDate);
													listBeen.time = aftertodayList
															.get(0)
															.get(CLFindScheduleTable.fstTime);
													// fengxingList.add(listBeen);
												} else {
													oldtodayList = app
															.QueryNewFocusData(
																	9,
																	listBeen.id);
													if (oldtodayList != null
															&& oldtodayList
																	.size() > 0) {
														listBeen.content = oldtodayList
																.get(oldtodayList
																		.size() - 1)
																.get(CLFindScheduleTable.fstContent);
														listBeen.displaytime = Integer
																.parseInt(oldtodayList
																		.get(oldtodayList
																				.size() - 1)
																		.get(CLFindScheduleTable.fstDisplayTime));
														listBeen.date = oldtodayList
																.get(oldtodayList
																		.size() - 1)
																.get(CLFindScheduleTable.fstDate);
														listBeen.time = oldtodayList
																.get(oldtodayList
																		.size() - 1)
																.get(CLFindScheduleTable.fstTime);
														// fengxingList.add(listBeen);
													} else {
														listBeen.content = "";
													}
												}
												JSONObject jsonObject = new JSONObject();
												try {
													jsonObject
															.put("clickCount",
																	listBeen.clickCount);
													jsonObject
															.put("attentionState",
																	listBeen.attentionState);
													jsonObject.put("date",
															listBeen.date);
													jsonObject.put("content",
															listBeen.content);
													jsonObject.put("id",
															listBeen.id);
													jsonObject.put("time",
															listBeen.time);
													jsonObject.put("name",
															listBeen.name);
													jsonObject
															.put("titleImg",
																	listBeen.titleImg
																			.replace(
																					"\\/",
																					""));
													jsonObject
															.put("startStateImg",
																	listBeen.startStateImg);
													jsonArray.put(jsonObject);
												} catch (JSONException e) {
													e.printStackTrace();
												}

											}
											save(context, jsonArray.toString());
											fengxiangAdapter = new NewMyFoundWoDeFengXiangAdapter(
													context,
													fengxingList,
													R.layout.adapter_newmyfound_wodefengxiang_list_item,
													handler);
											friends_lv
													.setAdapter(fengxiangAdapter);
										}
									} else {
										fengxingList.addAll(backBean.list);
										aftertodayList.clear();
										oldtodayList.clear();
										clearSCData(context);
										JSONArray jsonArray = new JSONArray();
										for (NewMyFoundShouChangListBeen listBeen : fengxingList) {
											aftertodayList = app
													.QueryNewFocusData(8,
															listBeen.id);
											if (aftertodayList != null
													&& aftertodayList.size() > 0) {
												listBeen.content = aftertodayList
														.get(0)
														.get(CLFindScheduleTable.fstContent);
												listBeen.displaytime = Integer
														.parseInt(aftertodayList
																.get(0)
																.get(CLFindScheduleTable.fstDisplayTime));
												listBeen.date = aftertodayList
														.get(0)
														.get(CLFindScheduleTable.fstDate);
												listBeen.time = aftertodayList
														.get(0)
														.get(CLFindScheduleTable.fstTime);
												// fengxingList.add(listBeen);
											} else {
												oldtodayList = app
														.QueryNewFocusData(9,
																listBeen.id);
												if (oldtodayList != null
														&& oldtodayList.size() > 0) {
													listBeen.content = oldtodayList
															.get(oldtodayList
																	.size() - 1)
															.get(CLFindScheduleTable.fstContent);
													listBeen.displaytime = Integer
															.parseInt(oldtodayList
																	.get(oldtodayList
																			.size() - 1)
																	.get(CLFindScheduleTable.fstDisplayTime));
													listBeen.date = oldtodayList
															.get(oldtodayList
																	.size() - 1)
															.get(CLFindScheduleTable.fstDate);
													listBeen.time = oldtodayList
															.get(oldtodayList
																	.size() - 1)
															.get(CLFindScheduleTable.fstTime);
													// fengxingList.add(listBeen);
												} else {
													listBeen.content = "";
												}
											}
											JSONObject jsonObject = new JSONObject();
											try {
												jsonObject.put("clickCount",
														listBeen.clickCount);
												jsonObject
														.put("attentionState",
																listBeen.attentionState);
												jsonObject.put("date",
														listBeen.date);
												jsonObject.put("content",
														listBeen.content);
												jsonObject.put("id",
														listBeen.id);
												jsonObject.put("time",
														listBeen.time);
												jsonObject.put("name",
														listBeen.name);
												jsonObject.put("titleImg",
														listBeen.titleImg
																.replace("\\/",
																		""));
												jsonObject.put("startStateImg",
														listBeen.startStateImg);
												jsonArray.put(jsonObject);
											} catch (JSONException e) {
												e.printStackTrace();
											}

										}
										save(context, jsonArray.toString());
										fengxiangAdapter.notifyDataSetChanged();
									}
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
		case R.id.top_ll_right:// 新建分享
			Intent intentnew = new Intent(context,
					NewMyFoundWoDeFengXiangXinJianActivity.class);
			startActivityForResult(intentnew, MYFRIENDSITEM);
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

	/**
	 * 删除分享
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
								fengxiangAdapter.notifyDataSetChanged();
								// App.getDBcApplication()
								// .newdeleteFocusScheduleData(deleteid);
								// QueryAlarmData.writeAlarm(getApplicationContext());
							} else {
								Toast.makeText(context, "删除分享失败...",
										Toast.LENGTH_SHORT).show();
								return;
							}
						} else {
							Toast.makeText(context, "删除分享失败...",
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

	private void save(Context context, String jsonarray) {// 写入SharedPreferences
		prefUtil.putString(context, ShareFile.USERFILE, ShareFile.SHAREDATA,
				jsonarray);
	}

	public void clearSCData(Context context) {// 清空闹钟
		prefUtil.putString(context, ShareFile.USERFILE, ShareFile.SHAREDATA, "");
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
