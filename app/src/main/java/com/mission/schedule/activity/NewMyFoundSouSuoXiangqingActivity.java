package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mission.schedule.R;
import com.mission.schedule.adapter.NewMyFoundSouSuoListAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewMyFoundSouSuoPingDaoAndRiChengItemBeen;
import com.mission.schedule.bean.NewMyFoundSouSuoXiangqingBeen;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.PullToRefreshViewNoFooter;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnFooterRefreshListener;
import com.mission.schedule.utils.PullToRefreshViewNoFooter.OnHeaderRefreshListener;
import com.mission.schedule.utils.SharedPrefUtil;
import com.tencent.mm.sdk.platformtools.Log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewMyFoundSouSuoXiangqingActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnHeaderRefreshListener,
		OnFooterRefreshListener {
	@ViewResId(id = R.id.focusName_tv)
	private TextView focusName_tv;// 大标题
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;

	Context context;
	SharedPrefUtil prefUtil = null;
	String path;
	String UserID;
	String sousuodataString;
	String sousuodatatype;
	@SuppressWarnings("unused")
	private boolean mRefreshHeadFlag = true;// 判断是否刷新的是头部
	private boolean mRefreshFlag = false;// 判断是否刷新
	private PullToRefreshViewNoFooter mPullToRefreshView = null;

	private ListView myListView;
	private NewMyFoundSouSuoListAdapter ListviewAdapter = null;
	private List<NewMyFoundSouSuoPingDaoAndRiChengItemBeen> list = new ArrayList<NewMyFoundSouSuoPingDaoAndRiChengItemBeen>();
	private int pagenum = 1;
	View headview, footView;
	RelativeLayout friendapplication_ll;
	ProgressUtil progressUtil = new ProgressUtil();
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newmyfound_sousuo_xiangqing);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		UserID = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		focusName_tv.setText("搜索结果");
		top_ll_right.setVisibility(View.INVISIBLE);
		sousuodataString = getIntent().getStringExtra("EditTextdata");
		sousuodatatype = getIntent().getStringExtra("type");
		myListView = (ListView) findViewById(R.id.new_fragment_sousuo_xiangqing_mylistviewview1);
		
		mPullToRefreshView = (PullToRefreshViewNoFooter) findViewById(R.id.myfriend_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setFocusable(false);

		myListView.setOnItemClickListener(this);
		myListView.setFocusable(true);
		if (sousuodatatype.equals("1")) {
			path = URLConstants.新版发现搜索用户;
		} else if (sousuodatatype.equals("2")) {
			path = URLConstants.新版发现搜索日程;
		}
		loadCountdata(path);
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		NewMyFoundSouSuoPingDaoAndRiChengItemBeen bean1 = (NewMyFoundSouSuoPingDaoAndRiChengItemBeen) myListView
				.getAdapter().getItem(position);
		Intent intent1 = null;
		if (bean1 != null) {
			updataclockcount(bean1.uid);
			if ("1".equals(bean1.styleView)) {
				intent1 = new Intent(context, NewFocusMobleTwoActivity.class);
			} else if ("2".equals(bean1.styleView)) {
				intent1 = new Intent(context, NewFocusMobleThreeActivity.class);
			} else {
				intent1 = new Intent(context, NewFocusOnCRYActivity.class);
			}
			intent1.putExtra("fid", Integer.valueOf(bean1.uid));
			intent1.putExtra("name", bean1.name);
			intent1.putExtra("friendsimage", bean1.titleImg);
			intent1.putExtra("friendsbackimage", bean1.backgroundImg);
			intent1.putExtra("imagetype", bean1.startStateImg);
			intent1.putExtra("remark6", StringUtils.getIsStringEqulesNull(bean1.remark6));
			if ("".equals(bean1.remark5) || "null".equals(bean1.remark5)
					|| null == bean1.remark5) {
				intent1.putExtra("othername", bean1.name);
			} else {
				intent1.putExtra("othername", bean1.remark5);
			}
			startActivityForResult(intent1, 3);
		}
	}
	private void updataclockcount(String id) {
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
	public void onFooterRefresh(PullToRefreshViewNoFooter view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshHeadFlag = false;
				mRefreshFlag = true;				
				if (sousuodatatype.equals("1")) {
					path = URLConstants.新版发现搜索用户;
				} else if (sousuodatatype.equals("2")) {
					path = URLConstants.新版发现搜索日程 ;
				}
				loadCountmore(path);
			}

		}, 100);
	}

	private void loadCountmore(String path) {
		final Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("data", sousuodataString);
		pairs.put("nowPage", ""+pagenum);
		pairs.put("pageNum", "40");
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@SuppressLint("NewApi")
					@Override
					public void onResponse(String result) {
						Log.e("sousuoxingqing", result);
						mPullToRefreshView.onFooterRefreshComplete();
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							NewMyFoundSouSuoXiangqingBeen backBean = gson
									.fromJson(result,
											NewMyFoundSouSuoXiangqingBeen.class);
							if (backBean.status == 0) {
//								ListviewAdapter = new NewMyFoundSouSuoListAdapter(
//										mContext,
//										list,
//										R.layout.new_adapter_myfound_sousuo_list_item);
								if(backBean.page.items!=null){
									pagenum++;
									list.addAll(backBean.page.items);
									ListviewAdapter.notifyDataSetChanged();									
								}
//								myListView.setAdapter(ListviewAdapter);
//								mPullToRefreshView.setVisibility(View.VISIBLE);
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return pairs;
			}
		};
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
				if (sousuodatatype.equals("1")) {
					path = URLConstants.新版发现搜索用户;
				} else if (sousuodatatype.equals("2")) {
					path = URLConstants.新版发现搜索日程;
				}
				loadCount(path);
				// mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 100);
	}

	private void loadCount(String path) {
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			loadCountdata(path);
		} else {
			return;
		}

	}

	private void loadCountdata(String path) {
		progressUtil.ShowProgress(context, true, true, "正在加载数据...");
		final Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("data", sousuodataString);
		pairs.put("nowPage", "1");
		pairs.put("pageNum", "40");
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@SuppressLint("NewApi")
					@Override
					public void onResponse(String result) {
						Log.e("sousuoxingqing", result);
						progressUtil.dismiss();
						if (mRefreshFlag) {
							mPullToRefreshView.onHeaderRefreshComplete();
							// mPullToRefreshView.onFooterRefreshComplete();
						} else {
						}
						pagenum = 2;
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							NewMyFoundSouSuoXiangqingBeen backBean = gson
									.fromJson(result,
											NewMyFoundSouSuoXiangqingBeen.class);
							list.clear();
							if (backBean.status == 0) {
								list = backBean.page.items;
								ListviewAdapter = new NewMyFoundSouSuoListAdapter(
										mContext,
										list,
										R.layout.adapter_newmyfound_sousuo_list_item);
								myListView.setAdapter(ListviewAdapter);
								mPullToRefreshView.setVisibility(View.VISIBLE);
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
					}
				}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return pairs;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
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
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("foundcount");
		App.getHttpQueues().cancelAll("down");
	}
}
