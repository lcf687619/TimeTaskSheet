package com.mission.schedule.fragment;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.activity.FriendsSouSuoActivity;
import com.mission.schedule.activity.MyFriendsAppActivity;
import com.mission.schedule.activity.NewSendMessageToFriendActivity;
import com.mission.schedule.adapter.MyFriendsFragmentAdapter;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.FriendsBackBean;
import com.mission.schedule.bean.FriendsBean;
import com.mission.schedule.bean.MyFriendsNewBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.bean.TotalFriendsCountBean;
import com.mission.schedule.bean.WeiDuFriendsBean;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.PostSendMainActivity;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.FriendsTable;
import com.mission.schedule.swipexlistview.MySwipeXListView;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.PullToRefreshViewNoFooter;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnFooterRefreshListener;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnHeaderRefreshListener;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class MyFragment extends BaseFragment implements OnClickListener,
		OnHeaderRefreshListener, OnFooterRefreshListener, OnItemClickListener {

	private boolean isShow = false;// 判断是否已经显示
	public static LinearLayout top_ll_left;
	RelativeLayout my_friend_ll_right;
	RelativeLayout friendapplication_ll;// 好友申请
	TextView friendapplicationcount_tv;// 还有申请个数
	// LinearLayout friendfanwei_ll;
	// TextView friendfanwei_tv;// 好友圈
	// View myview;
	SwipeMenuListView friends_lv;
	// RelativeLayout myfragment;
	String path;
	SharedPrefUtil sharedPrefUtil = null;
	String userID = "0";
	MyFriendsFragmentAdapter adapter = null;
	Context context;
	List<FriendsBean> friendsList = new ArrayList<FriendsBean>();
	List<WeiDuFriendsBean> weiduList = new ArrayList<WeiDuFriendsBean>();
	List<MyFriendsNewBean> mynewfriendsList = new ArrayList<MyFriendsNewBean>();

	private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部
	private boolean mRefreshFlag = false;// 判断是否刷新
	private PullToRefreshViewNoFooter mPullToRefreshView = null;
	private boolean isDel = true;

	// TextView tv_my_count;
	// TextView tv_schedule_count;
	View headview;

	public static final int BEAN = 1;
	public static final int ADDFRIEND = 2;
	public static final int FRIENDCIRCLE = 3;
	public static final int CHECKFRIEND = 4;

	int index;
	int deletecount;

	String myfriendscount;
	RelativeLayout add_rl;
	TextView add_tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_myfriend, container, false);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && !isShow) {
			isShow = true;
			init();
			loadData();
			setAdapter();
			loadCount();
		} else {
		}
	}

	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// super.onActivityCreated(savedInstanceState);
	// init();
	// loadData();
	// loadCount();
	// }

	private void init() {
		EventBus.getDefault().register(this);
		View view = getView();
		context = getActivity();
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userID = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "0");
		myfriendscount = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.COUNT, "0");
		// if(!"".equals(tv_my_count.getText().toString())){
		// tv_my_count.setVisibility(View.VISIBLE);
		// }else {
		// tv_my_count.setVisibility(View.GONE);
		// }

		headview = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_myfragment_headview, null);
		top_ll_left = null;
		top_ll_left = (LinearLayout) view.findViewById(R.id.top_ll_left);
		top_ll_left.setOnClickListener(this);
		friends_lv = (SwipeMenuListView) view.findViewById(R.id.friends_lv);
		my_friend_ll_right = (RelativeLayout) view
				.findViewById(R.id.my_friend_ll_right);
		my_friend_ll_right.setOnClickListener(this);
		friendapplication_ll = (RelativeLayout) headview
				.findViewById(R.id.friendapplication_ll);
		friendapplication_ll.setOnClickListener(this);
		friendapplication_ll.setVisibility(View.GONE);
		add_rl = (RelativeLayout) view.findViewById(R.id.add_rl);
		add_tv = (TextView) view.findViewById(R.id.add_tv);
		add_tv.setOnClickListener(this);
		// friendfanwei_tv = (TextView) headview
		// .findViewById(R.id.friendfanwei_tv);
		// friendfanwei_ll = (LinearLayout) headview
		// .findViewById(R.id.friendfanwei_ll);
		// friendfanwei_ll.setOnClickListener(this);
		// myfragment = (RelativeLayout) view.findViewById(R.id.myfragment);
		friendapplicationcount_tv = (TextView) headview
				.findViewById(R.id.friendapplicationcount_tv);
		// myview = headview.findViewById(R.id.view);
		friends_lv.addHeaderView(headview);
		if ("".equals(friendapplicationcount_tv.getText().toString().trim())) {
			friendapplicationcount_tv.setVisibility(View.GONE);
		} else {
		}
		// friendapplication_ll.setVisibility(View.GONE);
		// myview.setVisibility(View.GONE);
		View footView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarmfriends_footview, null);
		friends_lv.addFooterView(footView);

		mPullToRefreshView = (PullToRefreshViewNoFooter) view
				.findViewById(R.id.myfriend_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setFocusable(false);

		friends_lv.setOnItemClickListener(this);
		friends_lv.setFocusable(true);
		adapter = new MyFriendsFragmentAdapter(context, friendsList,
				R.layout.adapter_myfragmentadapter, handler);
		friends_lv.setAdapter(adapter);
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
//				SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
//				item1.setBackground(new ColorDrawable(Color
//						.parseColor("#55b192")));
//				item1.setWidth(dp2px(90));
//				// item1.setIcon(R.drawable.ic_action_discard);
//				item1.setTitle("日程");
//				// set item title fontsize
//				item1.setTitleSize(16);
//				// set item title font color
//				item1.setTitleColor(Color.WHITE);
//				menu.addMenuItem(item1);
				SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
				item2.setBackground(new ColorDrawable(Color
						.parseColor("#ff5959")));
				item2.setWidth(dp2px(90));
				// item2.setIcon(R.drawable.ic_action_good);
				item2.setTitle("删除");
				// set item title fontsize
				item2.setTitleSize(16);
				// set item title font color
				item2.setTitleColor(Color.WHITE);
				menu.addMenuItem(item2);
			}
		};
		friends_lv.setMenuCreator(creator);

		friends_lv.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				FriendsBean item = (FriendsBean) friendsList.get(position);
				Intent intent = null;
				switch (index) {
				case 0:
					String path = URLConstants.删除申请好友 + "?uId=" + item.fId
							+ "&fId=" + item.uId;
					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
						DeleteFriendsAppAsync(path);
					} else {
						return;
					}
					// open
					// intent = new Intent(context, FriendsCRYActivity.class);
					// intent.putExtra("fid", item.fId);
					// intent.putExtra("name", item.uName);
					// intent.putExtra("friendsimage", item.titleImg);
					// intent.putExtra("friendsbackimage", item.backImage);
					// intent.putExtra("attentionState", item.attentionState);
					// startActivity(intent);
					break;
				case 1:
					// delete
					// delete(item);
//					String path = URLConstants.删除申请好友 + "?uId=" + item.fId
//							+ "&fId=" + item.uId;
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						DeleteFriendsAppAsync(path);
//					} else {
//						return;
//					}
					break;
				}
			}
		});

	}

	private void loadData() {
		path = URLConstants.好友列表查询 + "?uId=" + Integer.parseInt(userID);
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			FriendsAppAsync(path);
		} else {
			List<Map<String, String>> friend = App.getDBcApplication()
					.QueryFriendsData(Integer.parseInt(userID));
			if (friend != null && friend.size() > 0) {
				friendapplication_ll.setVisibility(View.GONE);
			} else {
				friendapplication_ll.setVisibility(View.VISIBLE);
			}
			friendsList.clear();
			for (int i = 0; i < friend.size(); i++) {
				FriendsBean bean = new FriendsBean();
				bean.fId = Integer
						.parseInt(friend.get(i).get(FriendsTable.fId));
				bean.uId = Integer
						.parseInt(friend.get(i).get(FriendsTable.uId));
				bean.state = Integer.parseInt(friend.get(i).get(
						FriendsTable.state));
				bean.attentionState = Integer.parseInt(friend.get(i).get(
						FriendsTable.attentionState));
				bean.type = Integer.parseInt(friend.get(i).get(
						FriendsTable.type));
				bean.backImage = friend.get(i).get(FriendsTable.backImage);
				bean.titleImg = friend.get(i).get(FriendsTable.titleImg);
				bean.uName = friend.get(i).get(FriendsTable.uName);
				friendsList.add(bean);
			}
			Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (friendapplication_ll.getVisibility() == View.VISIBLE) {
			headview.setPadding(0, Utils.dipTopx(context, 30), 0,
					Utils.dipTopx(context, 30));
		} else {
			headview.setPadding(0, Utils.dipTopx(context, 30), 0, 0);
		}
	}
	private void setAdapter(){
		adapter = new MyFriendsFragmentAdapter(context, friendsList,
				R.layout.adapter_myfragmentadapter, handler);
		friends_lv.setAdapter(adapter);
	}
	// int i = 0;

	@Override
	public void onResume() {
		super.onResume();
		// i++;
		// if (i % 2 == 0) {
		// friends_lv.removeHeaderView(headview);
		// }
		// if (i == 2) {
		// i = 1;
		// }
		// init();
		// path = URLConstants.好友列表查询 + "?uId=" + Integer.parseInt(userID);
		// loadData();
		// adapter.notifyDataSetChanged();
		// loadCount();
	}

	private void loadCount() {
		// tv_schedule_count = MainActivity.tv_schedule_count;
		// int noEndCount =
		// App.getDBcApplication().QueryNowGuoQiWeiJieShuCount();//
		// Integer.parseInt(mainMap.get("noEndCount"));
		// if (noEndCount == 0) {
		// tv_schedule_count.setVisibility(View.GONE);
		// } else {
		// tv_schedule_count.setText(noEndCount + "");
		// tv_schedule_count.setVisibility(View.VISIBLE);
		// }
		// EventBus.getDefault().post(new PostSendMainActivity(1, noEndCount));
		// 好友统计数量
		// if(!"".equals(tv_my_count.getText().toString())){
		// tv_my_count.setVisibility(View.VISIBLE);
		// }else {
		// tv_my_count.setVisibility(View.GONE);
		// }
		// tv_my_count = MainActivity.tv_my_count;
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			String friendsCountPath = URLConstants.统计好友操作数量 + "?uId=" + userID;
			FriendsTotalAsync(friendsCountPath);
		} else {
			return;
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = null;
			FriendsBean friendsBean = (FriendsBean) msg.obj;
			int position = msg.arg1;
			switch (msg.what) {
			case 0:// 点击头像
					// intent = new Intent(context, FriendsMainActivity.class);
					// intent = new Intent(context, FriendsCRYActivity.class);
				// intent.putExtra("fid", friendsBean.fId);
				// intent.putExtra("name", friendsBean.uName);
				// intent.putExtra("friendsimage", friendsBean.titleImg);
				// intent.putExtra("friendsbackimage", friendsBean.backImage);
				// intent.putExtra("attentionState",
				// friendsBean.attentionState);
				// startActivity(intent);
				break;
			case 1:// 日程
					// intent = new Intent(context, FriendsCRYActivity.class);
					// intent.putExtra("fid", friendsBean.fId);
					// intent.putExtra("name", friendsBean.uName);
					// intent.putExtra("friendsimage", friendsBean.titleImg);
					// intent.putExtra("friendsbackimage",
					// friendsBean.backImage);
					// intent.putExtra("attentionState",
					// friendsBean.attentionState);
					// startActivity(intent);
				break;
			case 2: // 删除
				// String path = URLConstants.删除申请好友 + "?uId=" + friendsBean.fId
				// + "&fId=" + friendsBean.uId;
				// if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
				// new DeleteFriendsAppAsync().execute(path);
				// } else {
				// return;
				// }
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_friend_ll_right:// 添加好友
			startActivityForResult(new Intent(context,
					FriendsSouSuoActivity.class), ADDFRIEND);
			break;
		case R.id.friendapplication_ll:// 查看申请的好友
			startActivityForResult(new Intent(context,
					MyFriendsAppActivity.class), CHECKFRIEND);
			// FragmentTransaction ft = fm.beginTransaction();//注意。一个transaction
			// 只能commit一次，所以不要定义成全局变量
			// MyFriendsAppActivity df = new MyFriendsAppActivity();
			// ft.replace(R.id.myfragment, df);
			// ft.addToBackStack(null);
			// ft.commit();
			break;
		case R.id.add_tv:
			startActivityForResult(new Intent(context,
					FriendsSouSuoActivity.class), ADDFRIEND);
			break;
		// case R.id.friendfanwei_ll:// 好友圈
		// startActivityForResult(new Intent(context,
		// FriendsCircleActivity.class), FRIENDCIRCLE);
		// break;
		default:
			break;
		}
	}

	private void FriendsAppAsync(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (mRefreshFlag) {
							mPullToRefreshView.onHeaderRefreshComplete();
							// mPullToRefreshView.onFooterRefreshComplete();
						} else {
						}
						if (!TextUtils.isEmpty(result)) {
							if (mRefreshHeadFlag) {
								adapter = null;
							}
							Gson gson = new Gson();
							FriendsBackBean backBean = gson.fromJson(result,
									FriendsBackBean.class);
							if (backBean.status == 0) {
								friendsList.clear();
								if (backBean.count == 0) {
									if (backBean.zdsqCount == 0) {
										friendapplication_ll
												.setVisibility(View.GONE);
									} else {
										friendapplication_ll
												.setVisibility(View.VISIBLE);
									}
									// myview.setVisibility(View.GONE);
								} else {
									friendapplication_ll
											.setVisibility(View.VISIBLE);
									// myview.setVisibility(View.VISIBLE);
									friendapplicationcount_tv
											.setVisibility(View.VISIBLE);
									friendapplicationcount_tv
											.setText(backBean.count + "");
								}
								if (adapter == null) {
									friendsList = backBean.tbUserFriendsApp;
									if (backBean.count == 0
											&& backBean.zdsqCount == 0
											&& friendsList.size() == 0) {
										add_rl.setVisibility(View.VISIBLE);
										mPullToRefreshView
												.setVisibility(View.GONE);
										friends_lv.setVisibility(View.GONE);
									} else {
										add_rl.setVisibility(View.GONE);
										mPullToRefreshView
												.setVisibility(View.VISIBLE);
										friends_lv.setVisibility(View.VISIBLE);
									}
									weiduList = backBean.list;
									if (friendsList != null
											&& friendsList.size() > 0) {
										if (weiduList != null
												&& weiduList.size() > 0) {
											for (int j = 0; j < friendsList
													.size(); j++) {
												for (int i = 0; i < weiduList
														.size(); i++) {
													if (friendsList.get(j).fId == Integer.parseInt(weiduList
															.get(i).fid)) {
														friendsList.get(j).redCount = Integer
																.parseInt(weiduList
																		.get(i).redCount);
														friendsList.get(j).fId = friendsList
																.get(j).fId;
														friendsList.get(j).state = friendsList
																.get(j).state;
														friendsList.get(j).uId = friendsList
																.get(j).uId;
														friendsList.get(j).uName = friendsList
																.get(j).uName;
														friendsList.get(j).titleImg = friendsList
																.get(j).titleImg;
														friendsList.get(j).type = friendsList
																.get(j).type;
														friendsList.get(j).backImage = friendsList
																.get(j).backImage;
														friendsList.get(j).attentionState = friendsList
																.get(j).attentionState;
														friendsList.get(j).attState = friendsList
																.get(j).attState;
														friendsList.get(j).isV = friendsList
																.get(j).isV;
														friendsList.get(j).isFrends = friendsList
																.get(j).isFrends;
													}
												}
												int count = App
														.getDBcApplication()
														.CheckFriendsIDData(
																friendsList
																		.get(j).fId);
												if (count == 0) {
													App.getDBcApplication()
															.insertFriendsData(
																	friendsList
																			.get(j).fId,
																	friendsList
																			.get(j).uId,
																	friendsList
																			.get(j).state,
																	friendsList
																			.get(j).attentionState,
																	friendsList
																			.get(j).type,
																	friendsList
																			.get(j).backImage,
																	friendsList
																			.get(j).titleImg,
																	friendsList
																			.get(j).uName,
																	friendsList
																			.get(j).attState,
																	friendsList
																			.get(j).isFrends,
																	friendsList
																			.get(j).isV);
												} else {
													App.getDBcApplication()
															.updateFriendsData(
																	friendsList
																			.get(j).fId,
																	friendsList
																			.get(j).uId,
																	friendsList
																			.get(j).state,
																	friendsList
																			.get(j).attentionState,
																	friendsList
																			.get(j).type,
																	friendsList
																			.get(j).backImage,
																	friendsList
																			.get(j).titleImg,
																	friendsList
																			.get(j).uName,
																	friendsList
																			.get(j).attState,
																	friendsList
																			.get(j).isFrends,
																	friendsList
																			.get(j).isV);
												}
											}
											adapter = new MyFriendsFragmentAdapter(
													context,
													friendsList,
													R.layout.adapter_myfragmentadapter,
													handler);
											friends_lv.setAdapter(adapter);
											if (friendapplication_ll
													.getVisibility() == View.VISIBLE) {
												headview.setPadding(0, Utils
														.dipTopx(context, 30),
														0, Utils.dipTopx(
																context, 30));
											} else {
												headview.setPadding(0, Utils
														.dipTopx(context, 30),
														0, 0);
											}
											// setListViewHeightBasedOnChildren(friends_lv);
										} else {
											if (backBean.count == 0
													&& backBean.zdsqCount == 0
													&& friendsList.size() == 0) {
												add_rl.setVisibility(View.VISIBLE);
												mPullToRefreshView
														.setVisibility(View.GONE);
												friends_lv
														.setVisibility(View.GONE);
											} else {
												add_rl.setVisibility(View.GONE);
												mPullToRefreshView
														.setVisibility(View.VISIBLE);
												friends_lv
														.setVisibility(View.VISIBLE);
											}
											adapter = new MyFriendsFragmentAdapter(
													context,
													friendsList,
													R.layout.adapter_myfragmentadapter,
													handler);
											friends_lv.setAdapter(adapter);
											for (int j = 0; j < friendsList
													.size(); j++) {
												int count = App
														.getDBcApplication()
														.CheckFriendsIDData(
																friendsList
																		.get(j).fId);
												if (count == 0) {
													App.getDBcApplication()
															.insertFriendsData(
																	friendsList
																			.get(j).fId,
																	friendsList
																			.get(j).uId,
																	friendsList
																			.get(j).state,
																	friendsList
																			.get(j).attentionState,
																	friendsList
																			.get(j).type,
																	friendsList
																			.get(j).backImage,
																	friendsList
																			.get(j).titleImg,
																	friendsList
																			.get(j).uName,
																	friendsList
																			.get(j).attState,
																	friendsList
																			.get(j).isFrends,
																	friendsList
																			.get(j).isV);
												} else {
													App.getDBcApplication()
															.updateFriendsData(
																	friendsList
																			.get(j).fId,
																	friendsList
																			.get(j).uId,
																	friendsList
																			.get(j).state,
																	friendsList
																			.get(j).attentionState,
																	friendsList
																			.get(j).type,
																	friendsList
																			.get(j).backImage,
																	friendsList
																			.get(j).titleImg,
																	friendsList
																			.get(j).uName,
																	friendsList
																			.get(j).attState,
																	friendsList
																			.get(j).isFrends,
																	friendsList
																			.get(j).isV);
												}
											}
											if (friendapplication_ll
													.getVisibility() == View.VISIBLE) {
												headview.setPadding(0, Utils
														.dipTopx(context, 30),
														0, Utils.dipTopx(
																context, 30));
											} else {
												headview.setPadding(0, Utils
														.dipTopx(context, 30),
														0, 0);
											}
											// setListViewHeightBasedOnChildren(friends_lv);
										}
									} else {
										Toast.makeText(getActivity(),
												"没有好友，赶紧添加几个吧！",
												Toast.LENGTH_SHORT).show();
									}
								} else {
									friendsList.clear();
									friendsList
											.addAll(backBean.tbUserFriendsApp);
									// setListViewHeightBasedOnChildren(friends_lv);
									adapter.notifyDataSetChanged();
								}
							} else {
								friendsList.clear();
							if (backBean.count == 0
									&& backBean.zdsqCount == 0
									&& friendsList.size() == 0) {
								add_rl.setVisibility(View.VISIBLE);
								mPullToRefreshView.setVisibility(View.GONE);
								friends_lv.setVisibility(View.GONE);
							} else {
								add_rl.setVisibility(View.GONE);
								mPullToRefreshView
										.setVisibility(View.VISIBLE);
								friends_lv.setVisibility(View.VISIBLE);
							}
							if (backBean.count == 0) {
								if (backBean.zdsqCount == 0) {
									friendapplication_ll
											.setVisibility(View.GONE);
								} else {
									friendapplication_ll
											.setVisibility(View.VISIBLE);
								}
							} else {
								friendapplication_ll
										.setVisibility(View.VISIBLE);
								friendapplicationcount_tv
										.setVisibility(View.VISIBLE);
								friendapplicationcount_tv
										.setText(backBean.count + "");
							}
						}
							if(adapter!=null){
								adapter.notifyDataSetChanged();
							}else{
								adapter = new MyFriendsFragmentAdapter(context,
									friendsList,
									R.layout.adapter_myfragmentadapter, handler);
								friends_lv.setAdapter(adapter);
							}
							if (friendapplication_ll.getVisibility() == View.VISIBLE) {
								headview.setPadding(0,
										Utils.dipTopx(context, 30), 0,
										Utils.dipTopx(context, 30));
							} else {
								headview.setPadding(0,
										Utils.dipTopx(context, 30), 0, 0);
							}
						} else {
							if (isDel) {
								add_rl.setVisibility(View.GONE);
								mPullToRefreshView.setVisibility(View.VISIBLE);
								friends_lv.setVisibility(View.VISIBLE);
								friendsList = new ArrayList<FriendsBean>();
								adapter = new MyFriendsFragmentAdapter(context,
										friendsList,
										R.layout.adapter_myfragmentadapter,
										handler);
								friends_lv.setAdapter(adapter);
								if (friendapplication_ll.getVisibility() == View.VISIBLE) {
									headview.setPadding(0,
											Utils.dipTopx(context, 30), 0,
											Utils.dipTopx(context, 30));
								} else {
									headview.setPadding(0,
											Utils.dipTopx(context, 30), 0, 0);
								}
								// ListViewUtils.setListViewHeightBasedOnChildren(friends_lv);
							}
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						isDel = false;
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshViewNoFooter view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshHeadFlag = true;
				mRefreshFlag = true;
				path = URLConstants.好友列表查询 + "?uId=" + Integer.parseInt(userID);
				FriendsAppAsync(path);
				loadCount();
				// mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 100);
	}

	@Override
	public void onFooterRefresh(PullToRefreshViewNoFooter view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshHeadFlag = false;
				mRefreshFlag = true;
				mPullToRefreshView.onFooterRefreshComplete();
				// path = URLConstants.好友列表查询 + "?uId=" +
				// Integer.parseInt(userID);
				// new FriendsAppAsync().execute(path);
				// loadCount();
			}
		}, 100);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FriendsBean bean = (FriendsBean) friends_lv.getAdapter().getItem(
				position);
		// String name = bean.uName;
		index = position;
		if(bean!=null){
			deletecount = bean.redCount;
			Intent intent = new Intent(context,
					NewSendMessageToFriendActivity.class);
			intent.putExtra("myfriend", bean);
			startActivityForResult(intent, BEAN);
		}
	}

	/**
	 * 删除好友信息
	 */
	private void DeleteFriendsAppAsync(String path) {
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
								adapter.notifyDataSetChanged();
								Toast.makeText(context, "删除好友成功...",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(context, "删除好友失败...",
										Toast.LENGTH_SHORT).show();
								return;
							}
						} else {
							Toast.makeText(context, "删除好友失败...",
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

	private void FriendsAsync() {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							FriendsBackBean backBean = gson.fromJson(result,
									FriendsBackBean.class);
							if (backBean.status == 0) {
								if (backBean.count == 0) {
									friendapplication_ll
											.setVisibility(View.GONE);
									// myview.setVisibility(View.GONE);
								} else {
									friendapplication_ll
											.setVisibility(View.VISIBLE);
									// myview.setVisibility(View.VISIBLE);
									friendapplicationcount_tv
											.setVisibility(View.VISIBLE);
									friendapplicationcount_tv
											.setText(backBean.count + "");
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
					public void onErrorResponse(VolleyError volleyError) {
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	public void setListViewHeightBasedOnChildren(MySwipeXListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter adapter = listView.getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		int len = adapter.getCount();
		if (len <= 2) {
			for (int i = 0; i < len; i++) {
				// listAdapter.getCount()返回数据项的数目
				View listItem = adapter.getView(i, null, listView);
				// 计算子项View 的宽高
				listItem.measure(0, 0);
				// 统计所有子项的总高度
				totalHeight += (listItem.getMeasuredHeight() + 65);
			}
		} else {
			for (int i = 0; i < len; i++) {
				// listAdapter.getCount()返回数据项的数目
				View listItem = adapter.getView(i, null, listView);
				// 计算子项View 的宽高
				listItem.measure(0, 0);
				// 统计所有子项的总高度
				totalHeight += (listItem.getMeasuredHeight() + 5);
			}
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
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
									if (countBean.bsqCount == Integer
											.parseInt(myfriendscount)) {
										// if (countBean.bsqCount == 0) {
										// tv_my_count
										// .setVisibility(View.GONE);
										// } else {
										// tv_my_count
										// .setVisibility(View.VISIBLE);
										// tv_my_count.setText(myfriendscount);
										// }
										EventBus.getDefault().post(
												new PostSendMainActivity(2,
														countBean.bsqCount));
									} else {
										sharedPrefUtil.putString(context,
												ShareFile.USERFILE,
												ShareFile.COUNT,
												countBean.bsqCount + "");
										// if (countBean.bsqCount == 0) {
										// tv_my_count
										// .setVisibility(View.GONE);
										// } else {
										// tv_my_count
										// .setVisibility(View.VISIBLE);
										// tv_my_count
										// .setText(countBean.bsqCount
										// + "");
										// }
										EventBus.getDefault().post(
												new PostSendMainActivity(2,
														countBean.bsqCount));
									}
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

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BEAN) {
			if (resultCode == Activity.RESULT_OK) {
				FriendsBean bean = (FriendsBean) data
						.getSerializableExtra("bean");
				friendsList.remove(index - 1);
				friendsList.add(index - 1, bean);
				adapter = new MyFriendsFragmentAdapter(context, friendsList,
						R.layout.adapter_myfragmentadapter, handler);
				friends_lv.setAdapter(adapter);
				// tv_my_count = MainActivity.tv_my_count;
				if (Integer.parseInt(myfriendscount) - deletecount == 0) {
					// tv_my_count.setVisibility(View.GONE);
					EventBus.getDefault().post(new PostSendMainActivity(2, 0));
				} else {
					int i = Integer.parseInt(myfriendscount) - deletecount;
					// tv_my_count.setVisibility(View.VISIBLE);
					// tv_my_count.setText(i + "");
					EventBus.getDefault().post(new PostSendMainActivity(2, i));
				}
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					loadData();
					loadCount();
				} else {
					adapter.notifyDataSetChanged();
					return;
				}
			}
		} else if (requestCode == FRIENDCIRCLE) {
			if (resultCode == Activity.RESULT_OK) {
				loadData();
				// tv_my_count.setVisibility(View.GONE);
				loadCount();
			}
		} else if (requestCode == ADDFRIEND) {
			if (resultCode == Activity.RESULT_OK) {
				loadData();
				// tv_my_count.setVisibility(View.GONE);
				loadCount();
			}
		} else if (requestCode == CHECKFRIEND) {
			if (resultCode == Activity.RESULT_OK) {
				loadData();
				// tv_my_count.setVisibility(View.GONE);
				loadCount();
			}
		}
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		App.getHttpQueues().cancelAll("down");
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	public void onEventMainThread(FristFragment event) {

		String msg = event.getMsg();
		if ("3".equals(msg)&&isShow) {
			loadData();
			loadCount();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void lazyLoad() {

	}
}
