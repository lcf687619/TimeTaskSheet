//package com.mission.schedule.activity;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request.Method;
//import com.android.volley.AuthFailureError;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.john.library.annotation.ViewResId;
//import com.john.library.util.NetUtil;
//import com.john.library.util.SharedPrefUtil;
//import com.john.library.util.NetUtil.NetWorkState;
//import com.mission.schedule.R;
//import com.mission.schedule.adapter.FriendsChongFuAdapter;
//import com.mission.schedule.adapter.FriendsRiChengAdapter;
//import com.mission.schedule.adapter.FriendsYiQianAdapter;
//import com.mission.schedule.applcation.App;
//import com.mission.schedule.bean.FriendsChongFuBackBean;
//import com.mission.schedule.bean.FriendsChongFuBean;
//import com.mission.schedule.bean.FriendsRiChengBackBean;
//import com.mission.schedule.bean.FriendsRiChengBean;
//import com.mission.schedule.bean.FriendsYiQianBackBean;
//import com.mission.schedule.bean.FriendsYiQianBean;
//import com.mission.schedule.bean.SuccessOrFailBean;
//import com.mission.schedule.circleview.CircularImage;
//import com.mission.schedule.clock.QueryAlarmData;
//import com.mission.schedule.clock.WriteAlarmClock;
//import com.mission.schedule.constants.ShareFile;
//import com.mission.schedule.constants.URLConstants;
//import com.mission.schedule.utils.DateUtil;
//import com.mission.schedule.utils.ImageFileCache;
//import com.mission.schedule.utils.ImageGetFromIntenet;
//import com.mission.schedule.utils.ImageMemoryCache;
//import com.mission.schedule.utils.ListViewForScrollView;
//import com.mission.schedule.utils.ProgressUtil;
//import com.mission.schedule.utils.Utils;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
//
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class FriendsCRYActivity extends BaseActivity implements OnClickListener {
//
//	@ViewResId(id = R.id.friendbackground_rl)
//	private RelativeLayout friendbackground_rl;
//	@ViewResId(id = R.id.friendName_tv)
//	private TextView friendName_tv;
//	@ViewResId(id = R.id.friendstouxiang_iv)
//	private CircularImage friendstouxiang_iv;
//	@ViewResId(id = R.id.chongfu_tv)
//	private TextView chongfu_tv;
//	@ViewResId(id = R.id.richeng_tv)
//	private TextView richeng_tv;
//	@ViewResId(id = R.id.yiqian_tv)
//	private TextView yiqian_tv;
//	@ViewResId(id = R.id.mylistview_lv)
//	private ListViewForScrollView mylistview_lv;
//	@ViewResId(id = R.id.top_ll_back)
//	private LinearLayout top_ll_back;
//	@ViewResId(id = R.id.top_ll_right)
//	private RelativeLayout top_ll_right;
//
//	Context context;
//	String friendName;
//	int friendId;
//	String friendsimage;
//	String friendsbackimage;
//	int attentionState;
//	FriendsRiChengAdapter riChengAdapter = null;
//	FriendsChongFuAdapter chongFuAdapter = null;
//	FriendsYiQianAdapter yiQianAdapter = null;
//	String path;
//	String UserID;
//	SharedPrefUtil prefUtil = null;
//	List<FriendsRiChengBean> friendsList = new ArrayList<FriendsRiChengBean>();
//	List<FriendsChongFuBean> chongfuList = new ArrayList<FriendsChongFuBean>();
//	List<FriendsYiQianBean> yiqianList = new ArrayList<FriendsYiQianBean>();
//	Bitmap bit = null;
//	int displaypixels;
//	private ImageMemoryCache memoryCache;
//	private ImageFileCache fileCache;
//	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
//	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
//	private ImageLoader imageLoader;
//
//	FriendsRiChengBean riChengBean = null;
//	FriendsChongFuBean chongFuBean = null;
//	FriendsYiQianBean yiQianBean = null;
//
//	String imageUrl = "";
//	ProgressUtil progressUtil = new ProgressUtil();
//
//	@Override
//	protected void setListener() {
//		chongfu_tv.setOnClickListener(this);
//		richeng_tv.setOnClickListener(this);
//		yiqian_tv.setOnClickListener(this);
//		top_ll_back.setOnClickListener(this);
//		top_ll_right.setOnClickListener(this);
//		friendstouxiang_iv.setOnClickListener(this);
//	}
//
//	@Override
//	protected void setContentView() {
//		setContentView(R.layout.activity_friendscry);
//	}
//
//	@Override
//	protected void init(Bundle savedInstanceState) {
//		context = this;
//		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
//		UserID = prefUtil.getString(context, ShareFile.USERFILE,
//				ShareFile.USERID, "");
//		imageLoader = ImageLoader.getInstance();
//		memoryCache = new ImageMemoryCache(this);
//		fileCache = new ImageFileCache();
//		setHeadView();
//		displaypixels = mScreenWidth * mScreenHeight;
//
//		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
//		options = new DisplayImageOptions.Builder()
//				.showStubImage(R.drawable.img_null_smal) // 设置图片下载期间显示的图片
//				.showImageForEmptyUri(R.drawable.img_null_smal) // 设置图片Uri为空或是错误的时候显示的图片
//				.showImageOnFail(R.drawable.img_null_smal) // 设置图片加载或解码过程中发生错误显示的图片
//				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
//				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
//				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
//				.build(); // 创建配置过得DisplayImageOption对象
//
//		friendName = getIntent().getStringExtra("name");
//		friendId = getIntent().getIntExtra("fid", 0);
//		friendsimage = getIntent().getStringExtra("friendsimage");
//		friendsbackimage = getIntent().getStringExtra("friendsbackimage");
//		friendName_tv.setText(friendName);
//		attentionState = getIntent().getIntExtra("attentionState", 1);
//		if (!"".equals(friendsimage)) {
//			imageUrl = friendsimage.replace("\\", "");
//		}
//		String imageurl = URLConstants.图片 + imageUrl
//				+ "&imageType=2&imageSizeType=3";
//		// FileUtils.loadRoundHeadImg(context, ParameterUtil.userHeadImg,
//		// friendstouxiang_iv, imageurl);
//		imageLoader.displayImage(imageurl, friendstouxiang_iv, options,
//				animateFirstListener);
//		new Thread(new Runnable() {
//			public void run() {
//				String imageUrl;
//				if ("null".equals(friendsbackimage)
//						|| "".equals(friendsbackimage)
//						|| friendsbackimage == null) {
//					imageUrl = URLConstants.背景图片;
//				} else {
//					imageUrl = URLConstants.背景图片 + "=" + friendsbackimage;
//				}
//				// bit = StreamTool.getHttpBitmap(imageUrl,displaypixels);
//				bit = getBitmap(imageUrl);
//				Message message = new Message();
//				message.what = 0;
//				message.obj = bit;
//				handler.sendMessage(message);
//			}
//		}).start();
//
//	}
//
//	private void setHeadView() {
//		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) friendbackground_rl
//				.getLayoutParams();
//		linearParams.height = mScreenWidth;
//		friendbackground_rl.setLayoutParams(linearParams);
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) friendstouxiang_iv
//				.getLayoutParams();
//		params.topMargin = mScreenWidth - Utils.dipTopx(context, 50);
//		friendstouxiang_iv.setLayoutParams(params);
//	}
//
//	public Bitmap getBitmap(String url) {
//		// 从内存缓存中获取图片
//		Bitmap result = memoryCache.getBitmapFromCache(url);
//		if (result == null) {
//			// 文件缓存中获取
//			result = fileCache.getImage(url);
//			if (result == null) {
//				// 从网络获取
//				result = ImageGetFromIntenet.downloadBitmap(url);
//				if (result != null) {
//					fileCache.saveBitmap(result, url, 1);
//					memoryCache.addBitmapToCache(url, result);
//				}
//			} else {
//				// 添加到内存缓存
//				memoryCache.addBitmapToCache(url, result);
//			}
//		}
//		return result;
//	}
//
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case 0:
//				Bitmap bitmap = (Bitmap) msg.obj;
//				// bitmap = ImageUtils.zoomBitmap(bitmap, mScreenWidth,
//				// mScreenWidth);
//				friendbackground_rl.setBackgroundDrawable(new BitmapDrawable(
//						bitmap));
//				loadRiChengData();
//				break;
//
//			default:
//				break;
//			}
//		}
//
//	};
//
//	private void loadRiChengData() {
//		chongfu_tv.setTextColor(context.getResources().getColor(
//				R.color.gongkai_txt));
//		richeng_tv.setTextColor(context.getResources().getColor(R.color.black));
//		yiqian_tv.setTextColor(context.getResources().getColor(
//				R.color.gongkai_txt));
//		path = URLConstants.好友日程
//				+ "?cUid="
//				+ friendId
//				+ "&uId="
//				+ prefUtil.getString(context, ShareFile.USERFILE,
//						ShareFile.USERID, "") + "&type=" + 0;
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			FriendsRiChengAsync(path);
//		} else {
//			Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	private void loadYiQianData() {
//		chongfu_tv.setTextColor(context.getResources().getColor(
//				R.color.gongkai_txt));
//		richeng_tv.setTextColor(context.getResources().getColor(
//				R.color.gongkai_txt));
//		yiqian_tv.setTextColor(context.getResources().getColor(R.color.black));
//		path = URLConstants.好友以前日程
//				+ "?cUid="
//				+ friendId
//				+ "&uId="
//				+ prefUtil.getString(context, ShareFile.USERFILE,
//						ShareFile.USERID, "") + "&type=" + 0;
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			FriendsYiQianAsync(path);
//		} else {
//			Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	private void loadChongFuData() {
//		chongfu_tv.setTextColor(context.getResources().getColor(R.color.black));
//		richeng_tv.setTextColor(context.getResources().getColor(
//				R.color.gongkai_txt));
//		yiqian_tv.setTextColor(context.getResources().getColor(
//				R.color.gongkai_txt));
//		path = URLConstants.好友重复日程 + "?repUid=" + friendId;
//		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			FriendsChongFuAsync(path);
//		} else {
//			Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	private void richengitem() {
//		mylistview_lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				riChengBean = (FriendsRiChengBean) mylistview_lv.getAdapter()
//						.getItem(position);
//				dialogRiChengOnClick(riChengBean);
//			}
//		});
//	}
//
//	private void chongfuitem() {
//		mylistview_lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				chongFuBean = (FriendsChongFuBean) mylistview_lv.getAdapter()
//						.getItem(position);
//				dialogChongFuOnClick(chongFuBean);
//			}
//		});
//	}
//
//	private void yiqianitem() {
//		mylistview_lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				yiQianBean = (FriendsYiQianBean) mylistview_lv.getAdapter()
//						.getItem(position);
//				dialogYiQianOnClick(yiQianBean);
//			}
//		});
//	}
//
//	@Override
//	protected void setAdapter() {
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.chongfu_tv:
//			loadChongFuData();
//			break;
//		case R.id.richeng_tv:
//			loadRiChengData();
//			break;
//		case R.id.yiqian_tv:
//			loadYiQianData();
//			break;
//		case R.id.top_ll_back:
//			this.finish();
//			break;
//		case R.id.top_ll_right:
//			dialogRightOnClick();
//			break;
//		case R.id.friendstouxiang_iv:
//			String path;
//			if (attentionState == 0) {
//				path = URLConstants.删除关注 + "?uid=" + UserID + "&fid="
//						+ friendId;
//				alertDeleteDialog(path);
//			} else {
//				path = URLConstants.添加关注 + "?uid=" + UserID + "&fid="
//						+ friendId;
//				MyAddGuanZhuAsync(path);
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void alertDialog(int type) {
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
//
//	private void alertDeleteDialog(final String path) {
//		final AlertDialog builder = new AlertDialog.Builder(context).create();
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
//				MyDeleteGuanZhuAsync(path);
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
//		delete_tv.setText("确定要取消好友关注吗?");
//
//	}
//
//	private void FriendsRiChengAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在加载数据...");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							try {
//								Gson gson = new Gson();
//								FriendsRiChengBackBean backBean = gson
//										.fromJson(result,
//												FriendsRiChengBackBean.class);
//								friendsList.clear();
//								if (backBean.status == 0) {
//									friendsList = backBean.Calendars;
//									if (friendsList != null
//											&& friendsList.size() > 0) {
//										Collections
//												.sort(friendsList,
//														new Comparator<FriendsRiChengBean>() {
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
//																	FriendsRiChengBean o1,
//																	FriendsRiChengBean o2) {
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
//										riChengAdapter = new FriendsRiChengAdapter(
//												context, friendsList);
//										mylistview_lv
//												.setAdapter(riChengAdapter);
//										setListViewHeightBasedOnChildren(mylistview_lv);
//										richengitem();
//									} else {
//										riChengAdapter = new FriendsRiChengAdapter(
//												context, friendsList);
//										mylistview_lv
//												.setAdapter(riChengAdapter);
//										riChengAdapter.notifyDataSetChanged();
//										return;
//									}
//								} else {
//									friendsList.clear();
//									riChengAdapter = new FriendsRiChengAdapter(
//											context, friendsList);
//									mylistview_lv.setAdapter(riChengAdapter);
//									riChengAdapter.notifyDataSetChanged();
//									Toast.makeText(context, "没有日程...",
//											Toast.LENGTH_SHORT).show();
//									return;
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
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
//	private void FriendsChongFuAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在加载数据...");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							try {
//								Gson gson = new Gson();
//								FriendsChongFuBackBean backBean = gson
//										.fromJson(result,
//												FriendsChongFuBackBean.class);
//								if (backBean.status == 0) {
//									chongfuList = backBean.lists;
//									if (chongfuList != null
//											&& chongfuList.size() > 0) {
//										Collections
//												.sort(chongfuList,
//														new Comparator<FriendsChongFuBean>() {
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
//																	FriendsChongFuBean o1,
//																	FriendsChongFuBean o2) {
//
//																// 按照学生的年龄进行升序排列
//																if (o1.repType > o2.repType) {
//																	return 1;
//																}
//																if (o1.repType == o2.repType) {
//																	return 0;
//																}
//																return -1;
//															}
//														});
//										chongFuAdapter = new FriendsChongFuAdapter(
//												context, chongfuList);
//										mylistview_lv
//												.setAdapter(chongFuAdapter);
//										setListViewHeightBasedOnChildren(mylistview_lv);
//										chongfuitem();
//									} else {
//										chongFuAdapter = new FriendsChongFuAdapter(
//												context, chongfuList);
//										mylistview_lv
//												.setAdapter(chongFuAdapter);
//										chongFuAdapter.notifyDataSetChanged();
//										return;
//									}
//								} else {
//									chongfuList.clear();
//									chongFuAdapter = new FriendsChongFuAdapter(
//											context, chongfuList);
//									mylistview_lv.setAdapter(chongFuAdapter);
//									chongFuAdapter.notifyDataSetChanged();
//									Toast.makeText(context, "没有重复...",
//											Toast.LENGTH_SHORT).show();
//									return;
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
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
//	private void FriendsYiQianAsync(String path) {
//		progressUtil.ShowProgress(context, true, true, "正在加载数据...");
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(result)) {
//							try {
//								Gson gson = new Gson();
//								FriendsYiQianBackBean backBean = gson.fromJson(
//										result, FriendsYiQianBackBean.class);
//								yiqianList.clear();
//								if (backBean.status == 0) {
//									yiqianList = backBean.calendars;
//									if (yiqianList != null
//											&& yiqianList.size() > 0) {
//										Collections
//												.sort(yiqianList,
//														new Comparator<FriendsYiQianBean>() {
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
//																	FriendsYiQianBean o1,
//																	FriendsYiQianBean o2) {
//
//																// 按照学生的年龄进行升序排列
//																if (DateUtil
//																		.parseDate(
//																				o1.cDate)
//																		.getTime() < DateUtil
//																		.parseDate(
//																				o2.cDate)
//																		.getTime()) {
//																	return 1;
//																}
//
//																if (DateUtil
//																		.parseDate(
//																				o1.cDate)
//																		.getTime() == DateUtil
//																		.parseDate(
//																				o2.cDate)
//																		.getTime()) {
//																	return 0;
//																}
//																return -1;
//															}
//														});
//										yiQianAdapter = new FriendsYiQianAdapter(
//												context, yiqianList);
//										mylistview_lv.setAdapter(yiQianAdapter);
//										setListViewHeightYiQianBasedOnChildren(mylistview_lv);
//										yiqianitem();
//									} else {
//										yiQianAdapter = new FriendsYiQianAdapter(
//												context, yiqianList);
//										mylistview_lv.setAdapter(yiQianAdapter);
//										yiQianAdapter.notifyDataSetChanged();
//										return;
//									}
//								} else {
//									yiqianList.clear();
//									yiQianAdapter = new FriendsYiQianAdapter(
//											context, yiqianList);
//									mylistview_lv.setAdapter(yiQianAdapter);
//									yiQianAdapter.notifyDataSetChanged();
//									Toast.makeText(context, "没有以前日程...",
//											Toast.LENGTH_SHORT).show();
//									return;
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
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
//	public void setListViewHeightBasedOnChildren(ListView listView) {
//		// 获取ListView对应的Adapter
//		ListAdapter adapter = listView.getAdapter();
//		if (adapter == null) {
//			return;
//		}
//
//		int totalHeight = 0;
//		int len = adapter.getCount();
//		if (len <= 2) {
//			for (int i = 0; i < len; i++) {
//				// listAdapter.getCount()返回数据项的数目
//				View listItem = adapter.getView(i, null, listView);
//				// 计算子项View 的宽高
//				listItem.measure(0, 0);
//				// 统计所有子项的总高度
//				totalHeight += (listItem.getMeasuredHeight() + 65);
//			}
//		} else {
//			for (int i = 0; i < len; i++) {
//				// listAdapter.getCount()返回数据项的数目
//				View listItem = adapter.getView(i, null, listView);
//				// 计算子项View 的宽高
//				listItem.measure(0, 0);
//				// 统计所有子项的总高度
//				totalHeight += (listItem.getMeasuredHeight() + 2);
//			}
//		}
//
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight
//				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
//		// listView.getDividerHeight()获取子项间分隔符占用的高度
//		// params.height最后得到整个ListView完整显示需要的高度
//		listView.setLayoutParams(params);
//	}
//
//	public void setListViewHeightYiQianBasedOnChildren(ListView listView) {
//		// 获取ListView对应的Adapter
//		ListAdapter adapter = listView.getAdapter();
//		if (adapter == null) {
//			return;
//		}
//
//		int totalHeight = 0;
//		int len = adapter.getCount();
//		if (len <= 2) {
//			for (int i = 0; i < len; i++) {
//				// listAdapter.getCount()返回数据项的数目
//				View listItem = adapter.getView(i, null, listView);
//				// 计算子项View 的宽高
//				listItem.measure(0, 0);
//				// 统计所有子项的总高度
//				totalHeight += (listItem.getMeasuredHeight() + 65);
//			}
//		} else {
//			for (int i = 0; i < len; i++) {
//				// listAdapter.getCount()返回数据项的数目
//				View listItem = adapter.getView(i, null, listView);
//				// 计算子项View 的宽高
//				listItem.measure(0, 0);
//				// 统计所有子项的总高度
//				totalHeight += (listItem.getMeasuredHeight() + 5);
//			}
//		}
//
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight
//				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
//		// listView.getDividerHeight()获取子项间分隔符占用的高度
//		// params.height最后得到整个ListView完整显示需要的高度
//		listView.setLayoutParams(params);
//	}
//
//	private void MyAddGuanZhuAsync(String path) {
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							try {
//								SuccessOrFailBean bean = gson.fromJson(result,
//										SuccessOrFailBean.class);
//								if (bean.status == 0) {
//									attentionState = 0;
//									alertDialog(0);
//
//								} else {
//									Toast.makeText(context, "操作失败！",
//											Toast.LENGTH_SHORT).show();
//									return;
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//							}
//						} else {
//							return;
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
//	private void MyDeleteGuanZhuAsync(String path) {
//		StringRequest request = new StringRequest(Method.GET, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							try {
//								SuccessOrFailBean bean = gson.fromJson(result,
//										SuccessOrFailBean.class);
//								if (bean.status == 0) {
//									attentionState = 1;
//								} else {
//									Toast.makeText(context, "操作失败！",
//											Toast.LENGTH_SHORT).show();
//									return;
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//							}
//						} else {
//							return;
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
//	private void dialogRiChengOnClick(FriendsRiChengBean mMap) {
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
//		new RiChengOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class RiChengOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private FriendsRiChengBean mMap;
//		private TextView zhuanfafriends_tv;
//		private TextView addricheng_tv;
//		private TextView fenxiangwx_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public RiChengOnClick(Dialog dialog, FriendsRiChengBean mMap, View view) {
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
//				intent = new Intent(context,
//						FriendsRiChengZhuanFaActivity.class);
//				intent.putExtra("bean", mMap);
//				startActivity(intent);
//				dialog.dismiss();
//				break;
//			case R.id.addricheng_tv:
//				try {
//					boolean isInset = App.getDBcApplication()
//							.insertScheduleData(mMap.cContent, mMap.cDate,
//									mMap.cTime, mMap.cIsAlarm, mMap.cBeforTime,
//									mMap.cDisplayAlarm, mMap.cPostpone,
//									mMap.cImportant, mMap.cColorType,
//									mMap.cIsEnd, mMap.cCreateTime, mMap.cTags,
//									mMap.cType, mMap.cTypeDesc,
//									mMap.cTypeSpare, mMap.cRepeatId,
//									mMap.cRepeatDate, mMap.cUpdateTime, 0,
//									mMap.cOpenState, 0, mMap.cAlarmSoundDesc,
//									mMap.cAlarmSound, "", 0, 0, mMap.aType,
//									mMap.webUrl, mMap.imgPath, 0, 0, 0);
//					if (isInset) {
//						// if (mMap.cBeforTime == 0) {
//						// App.getDBcApplication().insertClockData(
//						// dateFormat.format(sdf.parse(mMap.cDate
//						// + " " + mMap.cTime)),
//						// mMap.cContent,
//						// mMap.cBeforTime,
//						// dateFormat.format(sdf.parse(mMap.cDate
//						// + " " + mMap.cTime)),
//						// mMap.cAlarmSoundDesc, mMap.cAlarmSound,
//						// mMap.cDisplayAlarm, mMap.cPostpone, 0,
//						// App.schID, 0, mMap.cIsAlarm, 0, "");
//						// } else {
//						// String resultTime = dateFormat.format(sdf.parse(
//						// mMap.cDate + " " + mMap.cTime).getTime()
//						// - mMap.cBeforTime * 60 * 1000);
//						// App.getDBcApplication().insertClockData(
//						// resultTime,
//						// mMap.cContent,
//						// mMap.cBeforTime,
//						// dateFormat.format(sdf.parse(mMap.cDate
//						// + " " + mMap.cTime)),
//						// mMap.cAlarmSoundDesc, mMap.cAlarmSound,
//						// mMap.cDisplayAlarm, mMap.cPostpone, 0,
//						// App.schID, 0, mMap.cIsAlarm, 0, "");
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
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.fenxiangwx_tv:
//				showShare(mMap.cDate + " " + mMap.cTime, mMap.cContent);
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
//	private void dialogChongFuOnClick(FriendsChongFuBean mMap) {
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
//		new ChongFuOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class ChongFuOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private FriendsChongFuBean mMap;
//		private TextView zhuanfafriends_tv;
//		private TextView addricheng_tv;
//		private TextView fenxiangwx_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public ChongFuOnClick(Dialog dialog, FriendsChongFuBean mMap, View view) {
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
//				intent = new Intent(context, FriendsRepeatZhuanFaActivity.class);
//				intent.putExtra("bean", mMap);
//				startActivity(intent);
//				dialog.dismiss();
//				break;
//			case R.id.addricheng_tv:
//				try {
//					boolean isInset = App.getDBcApplication()
//							.insertCLRepeatTableData(mMap.repBeforeTime,
//									mMap.repColorType, mMap.repDisplayTime,
//									mMap.repType, mMap.repIsAlarm,
//									mMap.repIsPuase, mMap.repIsImportant,
//									mMap.repSourceType, 1,
//									mMap.repTypeParameter,
//									mMap.repNextCreatedTime,
//									mMap.repLastCreatedTime,
//									mMap.repInitialCreatedTime,
//									mMap.repStartDate, mMap.repContent,
//									mMap.repCreateTime, mMap.repSourceDesc,
//									mMap.repSourceDescSpare, mMap.repTime,
//									mMap.repRingDesc, mMap.repRingCode,
//									mMap.repUpdateTime, mMap.repOpenState, "",
//									0, mMap.repdateone, mMap.repdatetwo,
//									mMap.repstateone, mMap.repstatetwo,
//									mMap.aType, mMap.webUrl, mMap.imgPath,
//									mMap.repInSTable, 0, mMap.parReamrk, 0, 0);
//					if (isInset) {
//						// RepeatBean bean = null;
//						// int index = 0;
//						// if (mMap.repType == 1) {
//						// index = 1;
//						// bean = RepeatDateUtils.saveCalendar(mMap.repTime,
//						// 1, "", "");
//						// } else if (mMap.repType == 2) {
//						// index = 2;
//						// bean = RepeatDateUtils.saveCalendar(
//						// mMap.repTime,
//						// 2,
//						// mMap.repTypeParameter.replace("[", "")
//						// .replace("]", "").replace("\"", "")
//						// .replace("\n\"", "")
//						// .replace("\n", ""), "");
//						// } else if (mMap.repType == 3) {
//						// index = 3;
//						// bean = RepeatDateUtils.saveCalendar(
//						// mMap.repTime,
//						// 3,
//						// mMap.repTypeParameter.replace("[", "")
//						// .replace("]", "").replace("\"", "")
//						// .replace("\n\"", "")
//						// .replace("\n", ""), "");
//						// } else if (mMap.repType == 4) {
//						// index = 4;
//						// String type = "";
//						// String str = mMap.repTypeParameter.replace("[", "")
//						// .replace("]", "").replace("\"", "")
//						// .replace("\n\"", "").replace("\n", "")
//						// .split("-")[0];
//						// boolean isNum = str.matches("[0-9]+");
//						// if (isNum) {
//						// type = "0";
//						// } else {
//						// type = "1";
//						// }
//						// bean = RepeatDateUtils.saveCalendar(
//						// mMap.repTime,
//						// 4,
//						// mMap.repTypeParameter.replace("[", "")
//						// .replace("]", "").replace("\"", "")
//						// .replace("\n\"", "")
//						// .replace("\n", ""), type);
//						// } else if (mMap.repType == 5) {
//						// index = 5;
//						// bean = RepeatDateUtils.saveCalendar(mMap.repTime,
//						// 5, "", "");
//						// }
//						//
//						// if (mMap.repBeforeTime == 0) {
//						// App.getDBcApplication().insertClockData(
//						// dateFormat.format(sdf
//						// .parse(bean.repNextCreatedTime)),
//						// mMap.repContent,
//						// mMap.repBeforeTime,
//						// dateFormat.format(sdf
//						// .parse(bean.repNextCreatedTime)),
//						// mMap.repRingDesc, mMap.repRingCode,
//						// mMap.repDisplayTime, 0, mMap.repType, 0,
//						// App.repschId, mMap.repIsAlarm, 0,
//						// mMap.repTypeParameter);
//						// } else {
//						// String resultTime = dateFormat.format(sdf.parse(
//						// bean.repNextCreatedTime).getTime()
//						// - mMap.repBeforeTime * 60 * 1000);
//						//
//						// App.getDBcApplication().insertClockData(
//						// resultTime,
//						// mMap.repContent,
//						// mMap.repBeforeTime,
//						// dateFormat.format(sdf
//						// .parse(bean.repNextCreatedTime)),
//						// mMap.repRingDesc, mMap.repRingCode,
//						// mMap.repDisplayTime, 0, mMap.repType, 0,
//						// App.repschId, mMap.repIsAlarm, 0,
//						// mMap.repTypeParameter);
//						//
//						// }
//						QueryAlarmData.writeAlarm(getApplicationContext());
//						WriteAlarmClock.writeAlarm(getApplicationContext());
//						dialog.dismiss();
//					} else {
//						Toast.makeText(context, "添加失败！", Toast.LENGTH_SHORT)
//								.show();
//						dialog.dismiss();
//						return;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.fenxiangwx_tv:
//				showShare(mMap.repTime, mMap.repContent);
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
//	private void dialogYiQianOnClick(FriendsYiQianBean mMap) {
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
//		new YiQianOnClick(dialog, mMap, more_pop_menu);
//	}
//
//	class YiQianOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private FriendsYiQianBean mMap;
//		private TextView zhuanfafriends_tv;
//		private TextView addricheng_tv;
//		private TextView fenxiangwx_tv;
//		private TextView canel_tv;
//
//		@SuppressLint("NewApi")
//		public YiQianOnClick(Dialog dialog, FriendsYiQianBean mMap, View view) {
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
//				intent = new Intent(context, FriendsYiQianZhuanFaActivity.class);
//				intent.putExtra("bean", mMap);
//				startActivity(intent);
//				dialog.dismiss();
//				break;
//			case R.id.addricheng_tv:
//				boolean isInset = App.getDBcApplication().insertScheduleData(
//						mMap.cContent, mMap.cDate, mMap.cTime, mMap.cIsAlarm,
//						mMap.cBeforTime, mMap.cDisplayAlarm, mMap.cPostpone,
//						mMap.cImportant, mMap.cColorType, mMap.cIsEnd,
//						mMap.cCreateTime, mMap.cTags, mMap.cType,
//						mMap.cTypeDesc, mMap.cTypeSpare, mMap.cRepeatId,
//						mMap.cRepeatDate, mMap.cUpdateTime, 0, mMap.cOpenState,
//						0, mMap.cAlarmSoundDesc, mMap.cAlarmSound, "", 0, 0,
//						mMap.aType, mMap.webUrl, mMap.imgPath, 0, 0, 0);
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
//				showShare(mMap.cDate + " " + mMap.cTime, mMap.cContent);
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
//	public void onBackPressed() {
//		AnimateFirstDisplayListener.displayedImages.clear();
//		super.onBackPressed();
//	}
//
//	/**
//	 * 图片加载第一次显示监听器
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	private static class AnimateFirstDisplayListener extends
//			SimpleImageLoadingListener {
//
//		static final List<String> displayedImages = Collections
//				.synchronizedList(new LinkedList<String>());
//
//		@Override
//		public void onLoadingComplete(String imageUri, View view,
//				Bitmap loadedImage) {
//			if (loadedImage != null) {
//				ImageView imageView = (ImageView) view;
//				// 是否第一次显示
//				boolean firstDisplay = !displayedImages.contains(imageUri);
//				if (firstDisplay) {
//					// 图片淡入效果
//					FadeInBitmapDisplayer.animate(imageView, 500);
//					displayedImages.add(imageUri);
//				}
//			}
//		}
//	}
//
//	private void dialogRightOnClick() {
//		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
//		Window window = dialog.getWindow();
//		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
//		params.alpha = 0.92f;
//		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		window.setAttributes(params);// 设置生效
//
//		LayoutInflater fac = LayoutInflater.from(context);
//		View more_pop_menu = fac
//				.inflate(R.layout.dialog_focusoncry_right, null);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setContentView(more_pop_menu);
//		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//		params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
//		dialog.show();
//
//		new RightOnClick(dialog, more_pop_menu);
//	}
//
//	class RightOnClick implements View.OnClickListener {
//
//		private View view;
//		private Dialog dialog;
//		private TextView refresh_tv;
//		private TextView share_wx_friends_tv;
//		private TextView share_wx_firendscircle_tv;
//		private TextView addfriends_tv;
//		private TextView canel_tv;
//		String title;
//		String content;
//		String path;
//		String username;
//
//		@SuppressLint("NewApi")
//		public RightOnClick(Dialog dialog, View view) {
//			this.dialog = dialog;
//			this.view = view;
//			initview();
//		}
//
//		public void initview() {
//			refresh_tv = (TextView) view.findViewById(R.id.refresh_tv);
//			refresh_tv.setOnClickListener(this);
//			share_wx_friends_tv = (TextView) view
//					.findViewById(R.id.share_wx_friends_tv);
//			share_wx_friends_tv.setOnClickListener(this);
//			share_wx_firendscircle_tv = (TextView) view
//					.findViewById(R.id.share_wx_firendscircle_tv);
//			share_wx_firendscircle_tv.setOnClickListener(this);
//			addfriends_tv = (TextView) view.findViewById(R.id.addfriends_tv);
//			addfriends_tv.setOnClickListener(this);
//			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
//			canel_tv.setOnClickListener(this);
//			path = URLConstants.分享日程 + friendId + "&source=1";
//			if (friendsList != null && friendsList.size() > 0) {
//				content = friendsList.get(0).cContent;
//			} else {
//				content = "";
//			}
//			username = prefUtil.getString(context, ShareFile.USERFILE,
//					ShareFile.USERNAME, "");
//			title = "@" + username + "@" + "给您分享了一个日程表";
//		}
//
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.refresh_tv:
//				loadRiChengData();
//				riChengAdapter.notifyDataSetChanged();
//				dialog.dismiss();
//				break;
//			case R.id.share_wx_friends_tv:
//				showShare(title, content, path);
//				dialog.dismiss();
//				break;
//			case R.id.share_wx_firendscircle_tv:
//				showShare(title, content, path);
//				dialog.dismiss();
//				break;
//			case R.id.addfriends_tv:
//				String addPath = URLConstants.添加好友申请;
//				Map<String, String> addPairs = new HashMap<String, String>();
//				addPairs.put("uId", UserID);
//				addPairs.put("fId", friendId + "");
//				addPairs.put("uName", username);
//				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//					AddFriendsAsync(addPath, addPairs);
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
//			default:
//				break;
//			}
//		}
//
//	}
//
//	private void AddFriendsAsync(String path, final Map<String, String> map) {
//		StringRequest request = new StringRequest(Method.POST, path,
//				new Response.Listener<String>() {
//
//					@Override
//					public void onResponse(String result) {
//						if (!TextUtils.isEmpty(result)) {
//							Gson gson = new Gson();
//							SuccessOrFailBean bean = gson.fromJson(result,
//									SuccessOrFailBean.class);
//							if (bean.status == 0) {
//								alertDialog(1);
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
//	private void showShare(String title, String content) {
//		ShareSDK.initSDK(this);
//		OnekeyShare oks = new OnekeyShare();
//		// 关闭sso授权
//		oks.disableSSOWhenAuthorize();
//		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
//		// oks.setNotification(R.drawable.ic_launcher,
//		// getString(R.string.app_name));
//		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle(title);
//		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		// oks.setTitleUrl(path);
//		// text是分享文本，所有平台都需要这个字段
//		oks.setText(content);
//		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		// oks.setImagePath(ParameterUtil.userHeadImg+imageUrl+"&imageType=2&imageSizeType=3");//
//		// 确保SDcard下面存在此张图片
//		// url仅在微信（包括好友和朋友圈）中使用
//		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		// oks.setComment("我是测试评论文本");
//		// site是分享此内容的网站名称，仅在QQ空间使用
//		// oks.setSite(getString(R.string.app_name));
//		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		// oks.setSiteUrl("http://sharesdk.cn");
//
//		// 启动分享GUI
//		oks.show(this);
//	}
//
//	private void showShare(String title, String content, String path) {
//		ShareSDK.initSDK(this);
//		OnekeyShare oks = new OnekeyShare();
//		// 关闭sso授权
//		oks.disableSSOWhenAuthorize();
//		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
//		// oks.setNotification(R.drawable.ic_launcher,
//		// getString(R.string.app_name));
//		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle(title);
//		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		// oks.setTitleUrl(path);
//		// text是分享文本，所有平台都需要这个字段
//		oks.setText(content);
//		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		// oks.setImagePath(ParameterUtil.userHeadImg+imageUrl+"&imageType=2&imageSizeType=3");//
//		// 确保SDcard下面存在此张图片
//		// url仅在微信（包括好友和朋友圈）中使用
//		oks.setUrl(path);
//		oks.setImageUrl(URLConstants.图片 + imageUrl
//				+ "&imageType=2&imageSizeType=3");
//		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		// oks.setComment("我是测试评论文本");
//		// site是分享此内容的网站名称，仅在QQ空间使用
//		// oks.setSite(getString(R.string.app_name));
//		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		// oks.setSiteUrl("http://sharesdk.cn");
//
//		// 启动分享GUI
//		oks.show(this);
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		App.getHttpQueues().cancelAll("down");
//		bit.recycle();
//	}
//}
