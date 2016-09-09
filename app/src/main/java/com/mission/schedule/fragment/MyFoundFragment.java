//package com.mission.schedule.fragment;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request.Method;
//import com.android.volley.Response;
//import com.android.volley.Response.Listener;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.john.library.util.NetUtil;
//import com.john.library.util.NetUtil.NetWorkState;
//import com.john.library.util.SharedPrefUtil;
//import com.mission.schedule.activity.AddFocusOnActivity;
//import com.mission.schedule.activity.FocusOnCRYActivity;
//import com.mission.schedule.activity.WebViewActivity;
//import com.mission.schedule.adapter.MyFoundFragmentAdapter;
//import com.mission.schedule.applcation.App;
//import com.mission.schedule.bean.AVBackBean;
//import com.mission.schedule.bean.AVBean;
//import com.mission.schedule.bean.FocusFriendsBackBean;
//import com.mission.schedule.bean.FocusFriendsBean;
//import com.mission.schedule.bean.FocusOtherBackBean;
//import com.mission.schedule.bean.FocusOtherBean;
//import com.mission.schedule.bean.SuccessOrFailBean;
//import com.mission.schedule.bean.TotalFriendsCountBean;
//import com.mission.schedule.constants.FristFragment;
//import com.mission.schedule.constants.PostSendMainActivity;
//import com.mission.schedule.constants.ShareFile;
//import com.mission.schedule.constants.URLConstants;
//import com.mission.schedule.entity.ScheduleTable;
//import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
//import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.IXListViewListener;
//import com.mission.schedule.utils.ImageSlideView;
//import com.mission.schedule.utils.ProgressUtil;
//import com.mission.schedule.utils.UriUtils;
//import com.mission.schedule.utils.ImageSlideView.ImageSlide;
//import com.mission.schedule.R;
//
//import de.greenrobot.event.EventBus;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.FragmentManager;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.GestureDetector;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//@SuppressLint("ValidFragment")
//public class MyFoundFragment extends BaseFragment implements OnClickListener,
//		IXListViewListener {
//
//	private boolean isShow;// 判断是否已经显示
//	public static LinearLayout top_ll_left;
//	RelativeLayout my_friend_ll_right; // 加关注
//	LinearLayout myfragment;
//	SwipeXListViewNoHead focus_lv;
//	// SwipeXListView unfocus_lv;
//	// TextView addfocus_rl;
//	TextView found_tv;
//	String path;
//	SharedPrefUtil sharedPrefUtil = null;
//	String userID;
//	MyFoundFragmentAdapter adapter = null;
//	// UnFocusOnAdapter focusOnAdapter = null;
//	Context context;
//	List<FocusFriendsBean> focusList = new ArrayList<FocusFriendsBean>();
//	List<FocusFriendsBean> unfocusList = new ArrayList<FocusFriendsBean>();
//	List<FocusFriendsBean> mList = new ArrayList<FocusFriendsBean>();
//
//	// private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部
//	// private boolean mRefreshFlag = false;// 判断是否刷新
//	// private PullToRefreshView mPullToRefreshView = null;
//	// private boolean isDel = true;
//	FragmentManager fm;
//
//	// TextView tv_schedule_count;
//	// TextView tv_my_count;
//	// 广告查询相关变量
//	private LinearLayout av_ll;
//	ImageView image;// 广告图片
//	List<AVBean> avList = new ArrayList<AVBean>();
//	List<ImageSlide> listViews = new ArrayList<ImageSlide>();
//	ImageSlideView imageSlideView;
//	View headview;
//	public static final int MYFOCUSSCH = 1;// 我关注的日程
//	public static final int MYFRIENDSITEM = 2;// 我关注的日程
//	public static final int ADDFOCUS = 3;// 加关注
//
//	// TextView unfound_tv;
//	ProgressUtil progressUtil = new ProgressUtil();
//	List<FocusOtherBean> list = new ArrayList<FocusOtherBean>();
//	int friendsId = 0;
//
//	GestureDetector gestureDetector;
//	int mScreenWidth;
//	int focuscount = 0;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.fragment_myfound, container, false);
//	}
//
//	@Override
//	public void onHiddenChanged(boolean hidden) {
//		super.onHiddenChanged(hidden);
//		if (!hidden && !isShow) {
//			isShow = true;
//			init();
//			AVData();
//			loadData();
//		}
//	}
//
//	// @Override
//	// public void onActivityCreated(Bundle savedInstanceState) {
//	// super.onActivityCreated(savedInstanceState);
//	// init();
//	// AVData();
//	// loadData();
//	// }
//
//	@SuppressLint("NewApi")
//	private void init() {
//		EventBus.getDefault().register(this);
//		View view = getView();
//		context = getActivity();
//		fm = getFragmentManager();
//		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
//		userID = sharedPrefUtil.getString(getActivity(), ShareFile.USERFILE,
//				ShareFile.USERID, "");
//		myfragment = (LinearLayout) view.findViewById(R.id.myfragment);
//		headview = LayoutInflater.from(getActivity()).inflate(
//				R.layout.fragment_myfound_headview, null);
//		top_ll_left = null;
//		top_ll_left = (LinearLayout) view.findViewById(R.id.top_ll_left);
//		top_ll_left.setOnClickListener(this);
//		focus_lv = (SwipeXListViewNoHead) view.findViewById(R.id.focus_lv);
//		focus_lv.setPullLoadEnable(true);
//		focus_lv.setXListViewListener(this);
//		focus_lv.setFocusable(true);
//
//		my_friend_ll_right = (RelativeLayout) view
//				.findViewById(R.id.my_friend_ll_right);
//		my_friend_ll_right.setOnClickListener(this);
//		// mPullToRefreshView = (PullToRefreshView) view
//		// .findViewById(R.id.myfriend_pull_refresh_view);
//		// mPullToRefreshView.setOnHeaderRefreshListener(this);
//		// mPullToRefreshView.setOnFooterRefreshListener(this);
//		// mPullToRefreshView.setFocusable(false);
//
//		// unfound_tv = (TextView) view.findViewById(R.id.unfound_tv);
//		// unfound_tv.setVisibility(View.GONE);
//
//		// unfocus_lv = (SwipeXListView) view.findViewById(R.id.unfocus_lv);
//		av_ll = (LinearLayout) headview.findViewById(R.id.av_ll);
//		DisplayMetrics metric = new DisplayMetrics();
//		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
//		mScreenWidth = metric.widthPixels;
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				mScreenWidth, mScreenWidth / 2);
//		av_ll.setLayoutParams(params);
//		av_ll.setBackground(context.getResources().getDrawable(
//				R.drawable.pic_ad));
//		// friendsfouce_ll = (LinearLayout) headview
//		// .findViewById(R.id.friendsfouce_ll);
//		// friendsfouce_ll.setOnClickListener(this);
//		found_tv = (TextView) headview.findViewById(R.id.found_tv);
//		// addfocus_rl = (TextView) headview.findViewById(R.id.addfocus_rl);
//		// addfocus_rl.setOnClickListener(this);
//		focus_lv.addHeaderView(headview);
//		View footView = LayoutInflater.from(context).inflate(
//				R.layout.activity_alarmfriends_footview, null);
//		focus_lv.addFooterView(footView);
//		// headview.setPadding(0, 0, 0, Utils.dipTopx(context, 10));
//
//		focus_lv.setFocusable(true);
//		// addfocus_rl.setVisibility(View.GONE);
//		adapter = new MyFoundFragmentAdapter(context, focusList, handler,
//				focus_lv, 0, 0);
//		focus_lv.setAdapter(adapter);
//		// otherview();
//		// unotherview();
//	}
//
//	private void loadData() {
//		path = URLConstants.关注的好友 + "?uid=" + Integer.parseInt(userID)
//				+ "&nowpage=" + 1 + "&pageNum=" + 40 + "&type=" + 3;
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			// Intent intent = new Intent(getActivity(), UpLoadService.class);
//			// intent.setAction(Const.SHUAXINDATA);
//			// context.startService(intent);
//			FocusFriendsAsync(path);
//		} else {
//			Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
//			found_tv.setVisibility(View.VISIBLE);
//			onLoad();
//			return;
//		}
//
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		// loadCount();
//	}
//
//	private void loadCount() {
//		// tv_schedule_count = MainActivity.tv_schedule_count;
////		int noEndCount = App.getDBcApplication().QueryNowGuoQiWeiJieShuCount();// Integer.parseInt(mainMap.get("noEndCount"));
////		EventBus.getDefault().post(new PostSendMainActivity(1, noEndCount));
//		// if (noEndCount == 0) {
//		// tv_schedule_count.setVisibility(View.GONE);
//		// } else {
//		// tv_schedule_count.setText(noEndCount + "");
//		// tv_schedule_count.setVisibility(View.VISIBLE);
//		// }
//		// 好友统计数量
//		String friendsCountPath = URLConstants.统计好友操作数量 + "?uId=" + userID;
//		FriendsTotalAsync(friendsCountPath);
//		// tv_my_count = MainActivity.tv_my_count;
//	}
//
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			Intent intent = null;
//			String path = "";
//			FocusFriendsBean friendsBean = (FocusFriendsBean) msg.obj;
//			friendsId = friendsBean.uid;
//			switch (msg.what) {
//			case 0:// 点击头像
//					// intent = new Intent(context, FriendsMainActivity.class);
//				intent = new Intent(context, FocusOnCRYActivity.class);
//				intent.putExtra("fid", friendsBean.uid);
//				intent.putExtra("name", friendsBean.uname);
//				intent.putExtra("friendsimage", friendsBean.titleImg);
//				intent.putExtra("friendsbackimage", friendsBean.backImage);
//				intent.putExtra("attentionState", friendsBean.attentionState);
//				intent.putExtra("type", "1");
//				startActivityForResult(intent, MYFRIENDSITEM);
//				break;
//			case 1:// 刷新
//				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//					String downPath = URLConstants.刷新下载关注数据 + userID
//							+ "&attentionId=" + friendsBean.uid;
//					DownFocusSchAsync(downPath);
//				} else {
//					return;
//				}
//				break;
//			case 2:// 关注
//				path = URLConstants.修改关注状态 + userID + "&fid=" + friendsBean.uid
//						+ "&state=" + 1;
//				cancleDialog(path, 2);
//				break;
//			case 3:// 删除
//				path = URLConstants.删除关注 + "?uid=" + userID + "&fid="
//						+ friendsBean.uid;
//				deleteDialog(path);
//				break;
//			case 4:// 取消关注 0待关注 1已关注
//				path = URLConstants.修改关注状态 + userID + "&fid=" + friendsBean.uid
//						+ "&state=" + 0;
//				cancleDialog(path, 4);
//				break;
//			case 5:
//				startActivityForResult(new Intent(context,
//						AddFocusOnActivity.class), ADDFOCUS);
//				break;
//			}
//		}
//
//	};
//
//	@Override
//	public void onClick(View v) {
//		// Animation translateIn = new TranslateAnimation(
//		// 0, mScreenWidth, 0, 0);
//		// translateIn.setDuration(400);
//		switch (v.getId()) {
//		case R.id.my_friend_ll_right:// 加关注
//			// myfragment.startAnimation(translateIn);
//			startActivityForResult(
//					new Intent(context, AddFocusOnActivity.class), ADDFOCUS);
//			break;
//		// case R.id.addfocus_rl:
//		// startActivityForResult(
//		// new Intent(context, AddFocusOnActivity.class), ADDFOCUS);
//		// break;
//		// case R.id.friendsfouce_ll:// 查看关注的日程
//		// startActivityForResult(new Intent(context,
//		// FocusOnRiChengActivity.class),MYFOCUSSCH);
//		// break;
//		default:
//			break;
//		}
//	}
//
//	private void item() {
//		focus_lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				FocusFriendsBean bean = (FocusFriendsBean) focus_lv
//						.getAdapter().getItem(position);
//				if (bean != null) {
//					if (focuscount == 0) {
//						if (position != 3) {
//							Intent intent = new Intent(context,
//									FocusOnCRYActivity.class);
//							intent.putExtra("fid", bean.uid);
//							intent.putExtra("name", bean.uname);
//							intent.putExtra("friendsimage", bean.titleImg);
//							intent.putExtra("friendsbackimage", bean.backImage);
//							intent.putExtra("attentionState", 0);
//							intent.putExtra("type", "1");
//							startActivityForResult(intent, MYFRIENDSITEM);
//						}
//					} else {
//						Intent intent = new Intent(context,
//								FocusOnCRYActivity.class);
//						intent.putExtra("fid", bean.uid);
//						intent.putExtra("name", bean.uname);
//						intent.putExtra("friendsimage", bean.titleImg);
//						intent.putExtra("friendsbackimage", bean.backImage);
//						intent.putExtra("attentionState", 0);
//						intent.putExtra("type", "1");
//						startActivityForResult(intent, MYFRIENDSITEM);
//					}
//				}
//			}
//		});
//		// unfocus_lv.setOnItemClickListener(new OnItemClickListener() {
//		//
//		// @Override
//		// public void onItemClick(AdapterView<?> parent, View view,
//		// int position, long id) {
//		// FocusFriendsBean bean = (FocusFriendsBean) unfocus_lv
//		// .getAdapter().getItem(position);
//		// Intent intent = new Intent(context, FocusOnCRYActivity.class);
//		// intent.putExtra("fid", bean.uid);
//		// intent.putExtra("name", bean.uname);
//		// intent.putExtra("friendsimage", bean.titleImg);
//		// intent.putExtra("friendsbackimage", bean.backImage);
//		// intent.putExtra("attentionState", 0);
//		// intent.putExtra("type", "1");
//		// startActivityForResult(intent, MYFRIENDSITEM);
//		// }
//		// });
//	}
//
//	private void FocusFriendsAsync(String path) {
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						onLoad();
//						// if (mRefreshFlag) {
//						// mPullToRefreshView.onHeaderRefreshComplete();
//						// // mPullToRefreshView.onFooterRefreshComplete();
//						// }
//						if (!TextUtils.isEmpty(result)) {
//							focusList.clear();
//							unfocusList.clear();
//							mList.clear();
//							// if (mRefreshHeadFlag) {
//							// adapter = null;
//							// }
//							Gson gson = new Gson();
//							FocusFriendsBackBean backBean = gson.fromJson(
//									result, FocusFriendsBackBean.class);
//							if (backBean.status == 0) {
//								int focuscount = 0;
//								if (backBean.list != null
//										&& backBean.list.size() > 0) {
//									for (int i = 0; i < backBean.list.size(); i++) {
//										if ("0".equals(backBean.list.get(i).attState)) {
//											focusList.add(backBean.list.get(i));
//										} else {
//											unfocusList.add(backBean.list
//													.get(i));
//										}
//									}
//									focuscount = focusList.size();
//									if (focusList.size() == 0) {
//										FocusFriendsBean bean = new FocusFriendsBean();
//										focuscount = 0;
//										focusList.add(bean);
//										// addfocus_rl.setVisibility(View.VISIBLE);
//										found_tv.setVisibility(View.GONE);
//									} else {
//										focuscount = focusList.size();
//										// addfocus_rl.setVisibility(View.GONE);
//										found_tv.setVisibility(View.GONE);
//									}
//									mList.addAll(focusList);
//									mList.addAll(unfocusList);
//									adapter = new MyFoundFragmentAdapter(
//											context, mList, handler, focus_lv,
//											focuscount, unfocusList.size());
//									focus_lv.setAdapter(adapter);
//									// setListViewHeightBasedOnChildren(focus_lv);
//								} else {
//									focuscount = focusList.size();
//									if (focusList.size() == 0) {
//										// addfocus_rl.setVisibility(View.VISIBLE);
//										FocusFriendsBean bean = new FocusFriendsBean();
//										focuscount = 0;
//										focusList.add(bean);
//										found_tv.setVisibility(View.GONE);
//									} else {
//										if (focusList.get(0).uid == 0) {
//											focuscount = 0;
//											found_tv.setVisibility(View.GONE);
//										} else {
//											focuscount = focusList.size();
//											found_tv.setVisibility(View.GONE);
//										}
//										// addfocus_rl.setVisibility(View.GONE);
//
//									}
//									adapter = new MyFoundFragmentAdapter(
//											context, focusList, handler,
//											focus_lv, focuscount, unfocusList
//													.size());
//									focus_lv.setAdapter(adapter);
//
//								}
//								item();
//							} else {
//								focuscount = 0;
//								FocusFriendsBean bean = new FocusFriendsBean();
//								focusList.add(bean);
//								found_tv.setVisibility(View.GONE);
//								adapter = new MyFoundFragmentAdapter(context,
//										focusList, handler, focus_lv, 0,
//										unfocusList.size());
//								focus_lv.setAdapter(adapter);
//							}
//						}
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError volleyError) {
//					}
//				});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//
//	/**
//	 * 删除好友信息
//	 */
//	private void DeleteFocusAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在删除中......");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							SuccessOrFailBean beans = gson.fromJson(result,
//									SuccessOrFailBean.class);
//							if (beans.status == 0) {
//								loadData();
//								adapter.notifyDataSetChanged();
//							} else {
//								return;
//							}
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
//	/**
//	 * 取消关注
//	 */
//	private void CancleFocusAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在取消中......");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							SuccessOrFailBean beans = gson.fromJson(result,
//									SuccessOrFailBean.class);
//							if (beans.status == 0) {
//								List<Map<String, String>> mList = App
//										.getDBcApplication().QueryFocusSch(
//												friendsId);
//								if (mList != null && mList.size() > 0) {
//									for (int i = 0; i < mList.size(); i++) {
//										if ("0".equals(App
//												.getDBcApplication()
//												.QueryFocusStateData(
//														Integer.parseInt(mList
//																.get(i)
//																.get(ScheduleTable.schAID)))
//												.get(ScheduleTable.schFocusState))) {
//											App.getDBcApplication()
//													.deleteFocusSch(
//															mList.get(i)
//																	.get(ScheduleTable.schAID),
//															friendsId);
//										}
//									}
//								}
//								loadData();
//								adapter.notifyDataSetChanged();
//							} else {
//								return;
//							}
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
//	/**
//	 * 添加关注
//	 */
//	private void AddFocusAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在添加中......");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							SuccessOrFailBean beans = gson.fromJson(result,
//									SuccessOrFailBean.class);
//							if (beans.status == 0) {
//								loadData();
//								if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//									String downPath = URLConstants.刷新下载关注数据
//											+ userID + "&attentionId="
//											+ friendsId;
//									DownFocusSchAsync(downPath);
//								}
//								adapter.notifyDataSetChanged();
//							} else {
//								return;
//							}
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
//	// public void setListViewHeightBasedOnChildren(SwipeMenuListView listView)
//	// {
//	// // 获取ListView对应的Adapter
//	// ListAdapter adapter = listView.getAdapter();
//	// if (adapter == null) {
//	// return;
//	// }
//	//
//	// int totalHeight = 0;
//	// int len = adapter.getCount();
//	// if (len <= 2) {
//	// for (int i = 0; i < len; i++) {
//	// // listAdapter.getCount()返回数据项的数目
//	// View listItem = adapter.getView(i, null, listView);
//	// // 计算子项View 的宽高
//	// listItem.measure(0, 0);
//	// // 统计所有子项的总高度
//	// totalHeight += (listItem.getMeasuredHeight() + 65);
//	// }
//	// } else {
//	// for (int i = 0; i < len; i++) {
//	// // listAdapter.getCount()返回数据项的数目
//	// View listItem = adapter.getView(i, null, listView);
//	// // 计算子项View 的宽高
//	// listItem.measure(0, 0);
//	// // 统计所有子项的总高度
//	// totalHeight += (listItem.getMeasuredHeight() + 5);
//	// }
//	// }
//	//
//	// ViewGroup.LayoutParams params = listView.getLayoutParams();
//	// params.height = totalHeight
//	// + (listView.getDividerHeight() * (adapter.getCount() - 1));
//	// // listView.getDividerHeight()获取子项间分隔符占用的高度
//	// // params.height最后得到整个ListView完整显示需要的高度
//	// listView.setLayoutParams(params);
//	// }
//
//	/**
//	 * 统计好友申请，被申请总数量
//	 */
//	private void FriendsTotalAsync(String path) {
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						if (!TextUtils.isEmpty(result)) {
//							try {
//								Gson gson = new Gson();
//								TotalFriendsCountBean countBean = gson
//										.fromJson(result,
//												TotalFriendsCountBean.class);
//								if (countBean.status == 0) {
//									EventBus.getDefault().post(
//											new PostSendMainActivity(2,
//													countBean.bsqCount));
//									// if (countBean.bsqCount == 0) {
//									// tv_my_count.setVisibility(View.GONE);
//									// } else {
//									// tv_my_count.setVisibility(View.VISIBLE);
//									// tv_my_count.setText(countBean.bsqCount
//									// + "");
//									// }
//								} else {
//									EventBus.getDefault().post(
//											new PostSendMainActivity(2, 0));
//									// tv_my_count.setVisibility(View.GONE);
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//							}
//						} else {
//							// tv_my_count.setVisibility(View.GONE);
//							EventBus.getDefault().post(
//									new PostSendMainActivity(2, 0));
//						}
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError volleyError) {
//					}
//				});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//
//	private void AVData() {
//		String path = URLConstants.查询广告;
//		QueryAVAsync(path);
//	}
//
//	private void QueryAVAsync(String path) {
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@SuppressLint("NewApi")
//					@Override
//					public void onResponse(String result) {
//						if (!TextUtils.isEmpty(result)) {
//							try {
//								Gson gson = new Gson();
//								avList.clear();
//								AVBackBean backBean = gson.fromJson(result,
//										AVBackBean.class);
//								if (backBean.status == 0) {
//									avList = backBean.list;
//									av_ll.setBackgroundColor(context
//											.getResources().getColor(
//													R.color.white));
//									Viewpager();
//								} else {
//									av_ll.setBackground(context.getResources()
//											.getDrawable(R.drawable.pic_ad));
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError volleyError) {
//					}
//				});
//		request.setTag("down");
//		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//		App.getHttpQueues().add(request);
//	}
//
//	public void Viewpager() {
//		listViews.clear();
//		if (avList == null && avList.size() == 0) {
//			return;
//		}
//		for (int i = 0; i < avList.size(); i++) {
//			String imageurl = URLConstants.展示广告图片 + avList.get(i).imgUrl;
//			listViews.add(new ImageSlide("", UriUtils.convertUrl2Uri(context,
//					imageurl), String.valueOf(i)));
//		}
//		imageSlideView = new ImageSlideView(context, listViews, av_ll);
//		imageSlideView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Intent intent = new Intent(context, WebViewActivity.class);
//				intent.putExtra("url", avList.get(position).url);
//				intent.putExtra("urlId", avList.get(position).imgUrl);
//				startActivity(intent);
//			}
//		});
//		imageSlideView.startScroll();
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == MYFRIENDSITEM) {
//			if (resultCode == Activity.RESULT_OK) {
//				loadData();
//			}
//		} else if (requestCode == MYFOCUSSCH) {
//			if (resultCode == Activity.RESULT_OK) {
//				loadData();
//			}
//		} else if (requestCode == ADDFOCUS) {
//			if (resultCode == Activity.RESULT_OK) {
//				loadData();
//			}
//		}
//	}
//
//	@Override
//	public void onRefresh() {
//		loadData();
//		adapter.notifyDataSetChanged();
//	}
//
//	@Override
//	public void onLoadMore() {
//		onLoad();
//	}
//
//	@Override
//	public void loadRefreshData() {
//
//	}
//
//	@Override
//	public void loadRefreshDataInother() {
//
//	}
//
//	@Override
//	public void homeRefreshData(boolean isRefresh) {
//
//	}
//
//	private void onLoad() {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
//		String date = format.format(new Date());
//		SwipeXListViewNoHead swipeXListView = null;
//		if (focus_lv.getVisibility() == View.VISIBLE) {
//			swipeXListView = focus_lv;
//		} else {
//			// swipeXListView = myImpotent_listview;
//		}
//		swipeXListView.stopRefresh();
//		swipeXListView.stopLoadMore();
//		swipeXListView.setRefreshTime("刚刚" + date);
//	}
//
//	private void deleteDialog(final String path) {
//		final AlertDialog builder = new AlertDialog.Builder(getActivity())
//				.create();
//		builder.show();
//		Window window = builder.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setAttributes(params);// 设置生效
//		window.setContentView(R.layout.dialog_alterdelete);
//		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
//		delete_ok.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				builder.cancel();
//				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//					DeleteFocusAsync(path);
//				} else {
//					return;
//				}
//			}
//		});
//		TextView delete_canel = (TextView) window
//				.findViewById(R.id.delete_canel);
//		delete_canel.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				builder.cancel();
//			}
//		});
//		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
//		delete_tv.setText("确认删除待关注?");
//
//	}
//
//	private void cancleDialog(final String path, final int type) {
//		final AlertDialog builder = new AlertDialog.Builder(getActivity())
//				.create();
//		builder.show();
//		Window window = builder.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setAttributes(params);// 设置生效
//		window.setContentView(R.layout.dialog_canclefocus);
//		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
//		delete_ok.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				builder.cancel();
//				if (type == 4) {
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						CancleFocusAsync(path);
//					} else {
//						return;
//					}
//				} else {
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						AddFocusAsync(path);
//					} else {
//						return;
//					}
//				}
//			}
//		});
//		TextView delete_canel = (TextView) window
//				.findViewById(R.id.delete_canel);
//		delete_canel.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				builder.cancel();
//			}
//		});
//		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
//		if (type == 4) {
//			delete_tv.setText("注意\n取消关注后，在我的日程中来自\n该频道的内容将被清除!");
//		} else {
//			delete_tv.setText("注意\n添加关注，该频道的内容将自动\n下载到我的日程列表中!");
//		}
//
//	}
//
//	private void DownFocusSchAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在刷新...");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							try {
//								Gson gson = new Gson();
//								FocusOtherBackBean backBean = gson.fromJson(
//										result, FocusOtherBackBean.class);
//								if (backBean.status == 0) {
//									list.clear();
//									list = backBean.list;
//									for (int i = 0; i < list.size(); i++) {
//										if (list.get(i).CState != 3) {
//											int count = App.getDBcApplication()
//													.CheckFocusIDData(
//															list.get(i).id);
//											if (count == 0) {
//												App.getDBcApplication()
//														.insertScheduleData(
//																list.get(i).CContent,
//																list.get(i).CDate,
//																list.get(i).CTime,
//																list.get(i).CIsAlarm,
//																list.get(i).CBefortime,
//																list.get(i).CDisplayAlarm,
//																0,
//																0,
//																0,
//																0,
//																list.get(i).CCreateTime,
//																"",
//																list.get(i).CType,
//																list.get(i).CTypeDesc,
//																list.get(i).CTypeSpare,
//																0,
//																"",
//																list.get(i).CUpdateTime,
//																0,
//																0,
//																0,
//																list.get(i).CAlarmsoundDesc,
//																list.get(i).CAlarmsound,
//																list.get(i).cuIckName,
//																1,
//																list.get(i).id,
//																list.get(i).aType,
//																list.get(i).webUrl,
//																list.get(i).imgPath,
//																0,
//																list.get(i).CUid,
//																0);
//											} else {
//												if ("0".equals(App
//														.getDBcApplication()
//														.QueryFocusStateData(
//																list.get(i).id)
//														.get(ScheduleTable.schFocusState))) {
//													App.getDBcApplication()
//															.updateFocusScheduleData(
//																	list.get(i).CContent,
//																	list.get(i).CDate,
//																	list.get(i).CTime,
//																	list.get(i).CIsAlarm,
//																	list.get(i).CBefortime,
//																	list.get(i).CDisplayAlarm,
//																	0,
//																	0,
//																	0,
//																	0,
//																	"",
//																	list.get(i).CType,
//																	list.get(i).CTypeDesc,
//																	list.get(i).CTypeSpare,
//																	0,
//																	"",
//																	list.get(i).CUpdateTime,
//																	0,
//																	0,
//																	0,
//																	list.get(i).CAlarmsoundDesc,
//																	list.get(i).CAlarmsound,
//																	list.get(i).cuIckName,
//																	1,
//																	list.get(i).id,
//																	list.get(i).aType,
//																	list.get(i).webUrl,
//																	list.get(i).imgPath,
//																	0,
//																	list.get(i).CUid,
//																	0);
//												}
//											}
//										} else if (list.get(i).CState == 3) {
//											App.getDBcApplication()
//													.deleteFocusScheduleData(
//															list.get(i).id);
//										}
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
//	@Override
//	public void onDestroy() {
//		EventBus.getDefault().unregister(this);
//		App.getHttpQueues().cancelAll("foundcount");
//		App.getHttpQueues().cancelAll("down");
//		handler.removeCallbacksAndMessages(null);
//		super.onDestroy();
//	}
//
//	public void onEventMainThread(FristFragment event) {
//
//		String msg = event.getMsg();
//		if ("2".equals(msg)) {
//			// loadData();
//			loadCount();
//			String foundcountPath = URLConstants.发现手机端添加统计数量 + userID;
//			StringRequest request = new StringRequest(Method.GET,
//					foundcountPath, new Listener<String>() {
//
//						@Override
//						public void onResponse(String result) {
//
//						}
//					}, new Response.ErrorListener() {
//
//						@Override
//						public void onErrorResponse(VolleyError arg0) {
//
//						}
//					});
//			request.setTag("foundcount");
//			request.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
//			App.getHttpQueues().add(request);
//			// adapter.notifyDataSetChanged();
//		}
//	}
//
//	@Override
//	protected void lazyLoad() {
//
//	}
//}
