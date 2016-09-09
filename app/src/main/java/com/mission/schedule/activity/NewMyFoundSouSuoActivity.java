package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.mission.schedule.bean.NewMyFoundSouSuoDataBeen;
import com.mission.schedule.bean.NewMyFoundSouSuoPingDaoAndRiChengItemBeen;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.ListViewForScrollView;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.widget.TagBaseAdapter;
import com.mission.schedule.widget.TagCloudLayout;
import com.tencent.mm.sdk.platformtools.Log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class NewMyFoundSouSuoActivity extends BaseActivity implements
		OnClickListener {
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;

	@ViewResId(id = R.id.new_activity_myfound_sousuo_edittext)
	private EditText sousuoEditText;

	@ViewResId(id = R.id.new_fragment_sousuo_kuaijie_sousuo_layout)
	private LinearLayout kuaijei_layout;
	@ViewResId(id = R.id.scrollview_new_fragment_sousuo_data_layout_all)
	private ScrollView sousuo_data_layout_all;
	@ViewResId(id = R.id.new_fragment_sousuo_nodata_layout)
	private LinearLayout nodata_layout;

	@ViewResId(id = R.id.new_fragment_sousuo_mylistviewview1_layout)
	private LinearLayout sousuo_pingdao_layout;
	@ViewResId(id = R.id.new_fragment_sousuo_mylistviewview2_layout)
	private LinearLayout sousuo_richeng_layout;
	@ViewResId(id = R.id.new_fragment_sousuo_mylistviewview1_title)
	private TextView sousuo_pingdao_title;
	@ViewResId(id = R.id.new_fragment_sousuo_mylistviewview2_title)
	private TextView sousuo_richeng_title;
	@ViewResId(id = R.id.new_fragment_sousuo_mylistviewview1)
	private ListViewForScrollView sousuo_pingdao_list;
	@ViewResId(id = R.id.new_fragment_sousuo_mylistviewview2)
	private ListViewForScrollView sousuo_richeng_list;

	@ViewResId(id = R.id.container1)
	private TagCloudLayout mContainer1;
	@ViewResId(id = R.id.container2)
	private TagCloudLayout mContainer2;
	@ViewResId(id = R.id.container1_layout)
	private LinearLayout container1_layout;
	@ViewResId(id = R.id.container2_layout)
	private LinearLayout container2_layout;
	@ViewResId(id = R.id.container1_delete_sousuo_jilu)
	private TextView mContainer1_DELETE;
	Context context;
	SharedPrefUtil prefUtil = null;
	String path;
	String UserID;
	List<String> tuijiangjz = new ArrayList<String>();
	List<String> bendigjz = new ArrayList<String>();

	private NewMyFoundSouSuoListAdapter pindaoAdapter = null,
			richengAdapter = null;
	private List<NewMyFoundSouSuoPingDaoAndRiChengItemBeen> pingdaolist = new ArrayList<NewMyFoundSouSuoPingDaoAndRiChengItemBeen>();
	private List<NewMyFoundSouSuoPingDaoAndRiChengItemBeen> richenglist = new ArrayList<NewMyFoundSouSuoPingDaoAndRiChengItemBeen>();

	private TagBaseAdapter mAdapter1, mAdapter2;

	private String kaijiesousuodata = "";
	private Boolean iskuaijielayout = true;
	List<String> sousuo = new ArrayList<String>();
	ProgressUtil progressUtil = new ProgressUtil();
	private String intentsousuoString = "";

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
		sousuo_pingdao_title.setOnClickListener(this);
		sousuo_richeng_title.setOnClickListener(this);
		mContainer1_DELETE.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newmyfound_sousuo);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		UserID = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		// Set<String> sousuo=new HashSet<String>();
		kaijiesousuodata = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.KuaiJieSouSuo, "");

		nodata_layout.setVisibility(View.GONE);
		sousuo_data_layout_all.setVisibility(View.GONE);
		kuaijei_layout.setVisibility(View.VISIBLE);

		loaddata();
		mAdapter1 = new TagBaseAdapter(context, bendigjz, R.layout.tagview);
		mContainer1.setAdapter(mAdapter1);
		if (kaijiesousuodata.equals("")) {
			container1_layout.setVisibility(View.GONE);
		} else {
			intdataBengdi();

		}
		sousuo_pingdao_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				NewMyFoundSouSuoPingDaoAndRiChengItemBeen bean1 = (NewMyFoundSouSuoPingDaoAndRiChengItemBeen) sousuo_pingdao_list
						.getAdapter().getItem(position);
				Intent intent1 = null;
				if (bean1 != null) {
					updataclockcount(bean1.uid);
					if ("1".equals(bean1.styleView)) {
						intent1 = new Intent(context,
								NewFocusMobleTwoActivity.class);
					} else if ("2".equals(bean1.styleView)) {
						intent1 = new Intent(context,
								NewFocusMobleThreeActivity.class);
					} else {
						intent1 = new Intent(context,
								NewFocusOnCRYActivity.class);
					}
					intent1.putExtra("fid", Integer.valueOf(bean1.uid));
					intent1.putExtra("name", bean1.name);
					intent1.putExtra("friendsimage", bean1.titleImg);
					intent1.putExtra("friendsbackimage", bean1.backgroundImg);
					intent1.putExtra("imagetype", bean1.startStateImg);
					intent1.putExtra("remark6", StringUtils.getIsStringEqulesNull(bean1.remark6));
					if ("".equals(bean1.remark5)
							|| "null".equals(bean1.remark5)
							|| null == bean1.remark5) {
						intent1.putExtra("othername", bean1.name);
					} else {
						intent1.putExtra("othername", bean1.remark5);
					}
					startActivityForResult(intent1, 3);
				}
			}
		});
		sousuo_richeng_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				NewMyFoundSouSuoPingDaoAndRiChengItemBeen bean2 = (NewMyFoundSouSuoPingDaoAndRiChengItemBeen) sousuo_richeng_list
						.getAdapter().getItem(position);
				Intent intent2 = null;
				if (bean2 != null) {
					updataclockcount(bean2.uid);
					if ("1".equals(bean2.styleView)) {
						intent2 = new Intent(context,
								NewFocusMobleTwoActivity.class);
					} else if ("2".equals(bean2.styleView)) {
						intent2 = new Intent(context,
								NewFocusMobleThreeActivity.class);
					} else {
						intent2 = new Intent(context,
								NewFocusOnCRYActivity.class);
					}
					intent2.putExtra("fid", Integer.valueOf(bean2.uid));
					intent2.putExtra("name", bean2.name);
					intent2.putExtra("friendsimage", bean2.titleImg);
					intent2.putExtra("friendsbackimage", bean2.backgroundImg);
					intent2.putExtra("imagetype", bean2.startStateImg);
					intent2.putExtra("remark6", StringUtils.getIsStringEqulesNull(bean2.remark6));
					if ("".equals(bean2.remark5)
							|| "null".equals(bean2.remark5)
							|| null == bean2.remark5) {
						intent2.putExtra("othername", bean2.name);
					} else {
						intent2.putExtra("othername", bean2.remark5);
					}
					startActivityForResult(intent2, 3);
				}
			}
		});
	}

	private void intdataBengdi() {
		container1_layout.setVisibility(View.VISIBLE);
		kaijiesousuodata.split(",");
		for (int i = 0; i < kaijiesousuodata.split(",").length; i++) {
			bendigjz.add(kaijiesousuodata.split(",")[i]);
		}
		mAdapter1.notifyDataSetChanged();
		// mAdapter1 = new TagBaseAdapter(context, bendigjz);
		// mContainer1.setAdapter(mAdapter1);
		mContainer1
				.setItemClickListener(new TagCloudLayout.TagItemClickListener() {
					@Override
					public void itemClick(int position) {
						iskuaijielayout = false;
						intentsousuoString = bendigjz.get(position);
						loadsousuodata(bendigjz.get(position));
						sousuoEditText.setText(bendigjz.get(position));
						sousuoEditText.setSelection(bendigjz.get(position).length());
						saveGZJtoBengdi(bendigjz.get(position));
					}

				});
	}

	private void intdataTuijian() {
		mContainer2
				.setItemClickListener(new TagCloudLayout.TagItemClickListener() {
					@Override
					public void itemClick(int position) {
						iskuaijielayout = false;
						intentsousuoString = tuijiangjz.get(position);
						loadsousuodata(tuijiangjz.get(position));
						sousuoEditText.setText(tuijiangjz.get(position));
						sousuoEditText.setSelection(tuijiangjz.get(position).length());
						saveGZJtoBengdi(tuijiangjz.get(position));
					}

				});
	}

	private void intdataBengdiNOTIFY() {
		if (kaijiesousuodata.equals("")) {
			container1_layout.setVisibility(View.GONE);
			bendigjz.clear();
		} else {
			container1_layout.setVisibility(View.VISIBLE);
			kaijiesousuodata.split(",");
			bendigjz.clear();
			for (int i = 0; i < kaijiesousuodata.split(",").length; i++) {
				bendigjz.add(kaijiesousuodata.split(",")[i]);
			}
			mAdapter1.notifyDataSetChanged();
		}
	}

	private void saveGZJtoBengdi(String text) {
		if (kaijiesousuodata.equals("")) {
			kaijiesousuodata = text;
		} else {
			String[] bendidata = kaijiesousuodata.split(",");
			List<String> bengdidatalist = new ArrayList<String>();
			for (int i = 0; i < bendidata.length; i++) {
				bengdidatalist.add(bendidata[i]);
			}
			if (bengdidatalist.contains(text)) {
				bengdidatalist.remove(text);
				sousuo.clear();
				sousuo.addAll(bengdidatalist);
				bengdidatalist.clear();
				bengdidatalist.add(text);
				bengdidatalist.addAll(sousuo);
			} else {
				if (bengdidatalist.size() == 10) {
					sousuo.clear();
					sousuo.addAll(bengdidatalist);
					bengdidatalist.clear();
					bengdidatalist.add(text);
					for (int i = 0; i < 9; i++) {
						bengdidatalist.add(sousuo.get(i));
					}
				} else {
					sousuo.clear();
					sousuo.addAll(bengdidatalist);
					bengdidatalist.clear();
					bengdidatalist.add(text);
					bengdidatalist.addAll(sousuo);
				}
			}
			kaijiesousuodata = "";
			for (int i = 0; i < bengdidatalist.size(); i++) {
				if (i == bengdidatalist.size() - 1) {
					kaijiesousuodata = kaijiesousuodata + bengdidatalist.get(i);
				} else {
					kaijiesousuodata = kaijiesousuodata + bengdidatalist.get(i)
							+ ",";
				}
			}
		}
		prefUtil.putString(context, ShareFile.USERFILE,
				ShareFile.KuaiJieSouSuo, kaijiesousuodata);
		intdataBengdiNOTIFY();
	}

	private void loadsousuodata(String string) {
		sousuo_data_layout_all.setVisibility(View.VISIBLE);
		kuaijei_layout.setVisibility(View.GONE);
		progressUtil.ShowProgress(context, true, true, "正在加载数据...");
		String path = URLConstants.新版发现搜索;
		final Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("data", string);
		pairs.put("nowPage", "1");
		pairs.put("pageNum", "4");
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@SuppressLint("NewApi")
					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							NewMyFoundSouSuoDataBeen backBean = gson.fromJson(
									result, NewMyFoundSouSuoDataBeen.class);
							pingdaolist.clear();
							richenglist.clear();
							if (backBean.status == 0) {
								pingdaolist = backBean.pageUser.items;
								richenglist = backBean.pageCalendar.items;
								pindaoAdapter = new NewMyFoundSouSuoListAdapter(
										mContext,
										pingdaolist,
										R.layout.adapter_newmyfound_sousuo_list_item);
								richengAdapter = new NewMyFoundSouSuoListAdapter(
										mContext,
										richenglist,
										R.layout.adapter_newmyfound_sousuo_list_item);
								sousuo_pingdao_list.setAdapter(pindaoAdapter);
								sousuo_richeng_list.setAdapter(richengAdapter);
								if (backBean.pageUser.totalCount == 0
										&& backBean.pageCalendar.totalCount == 0) {
									nodata_layout.setVisibility(View.VISIBLE);
									sousuo_data_layout_all
											.setVisibility(View.GONE);
								} else {
									nodata_layout.setVisibility(View.GONE);
									sousuo_data_layout_all
											.setVisibility(View.VISIBLE);
									if (backBean.pageUser.totalCount == 0) {
										sousuo_pingdao_layout
												.setVisibility(View.GONE);
									} else {
										String colorState1 = ""
												+ context
														.getResources()
														.getColor(
																R.color.gongkai_txt);
										String colorState2 = ""
												+ context.getResources()
														.getColor(
																R.color.bg_red);
										String sequence = "<font color='"
												+ colorState1 + "'>" + "标题搜索结果"
												+ "</font>" + "<font color='"
												+ colorState2 + "'>"
												+ backBean.pageUser.totalCount
												+ "</font>" + "<font color='"
												+ colorState1 + "'>" + "条"
												+ "</font>";
										sousuo_pingdao_title.setText(Html
												.fromHtml(sequence));
										sousuo_pingdao_layout
												.setVisibility(View.VISIBLE);
									}
									if (backBean.pageCalendar.totalCount == 0) {
										sousuo_richeng_layout
												.setVisibility(View.GONE);
									} else {
										String colorState1 = ""
												+ context
														.getResources()
														.getColor(
																R.color.gongkai_txt);
										String colorState2 = ""
												+ context.getResources()
														.getColor(
																R.color.bg_red);
										String sequence = "<font color='"
												+ colorState1
												+ "'>"
												+ "日程搜索结果"
												+ "</font>"
												+ "<font color='"
												+ colorState2
												+ "'>"
												+ backBean.pageCalendar.totalCount
												+ "</font>" + "<font color='"
												+ colorState1 + "'>" + "条"
												+ "</font>";
										sousuo_richeng_title.setText(Html
												.fromHtml(sequence));
										sousuo_richeng_layout
												.setVisibility(View.VISIBLE);
									}
								}

							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
						nodata_layout.setVisibility(View.VISIBLE);
						sousuo_data_layout_all.setVisibility(View.GONE);
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return pairs;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void loaddata() {
		String path = URLConstants.新版发现获取推荐关键字 + "?uid=" + UserID;
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@SuppressLint("NewApi")
					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject jb = new JSONObject(result);
								int backbeenstate = jb.getInt("status");
								if (backbeenstate == 0) {
									JSONArray ja = (JSONArray) jb.get("list");
									JSONObject usejb = (JSONObject) ja.get(0);
									String userString = usejb.getString("gjz");
									if (!"".equals(StringUtils.getIsStringEqulesNull(userString))) {
										tuijiangjz.clear();
										for (int i = 0; i < userString
												.split(",").length; i++) {
											tuijiangjz.add(userString
													.split(",")[i]);
										}
										if (tuijiangjz != null) {
											mAdapter2 = new TagBaseAdapter(
													context, tuijiangjz,
													R.layout.tagview);
											mContainer2.setAdapter(mAdapter2);
											intdataTuijian();
										} else {
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
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

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			if (!iskuaijielayout) {
				iskuaijielayout = true;
				intdataBengdiNOTIFY();
				nodata_layout.setVisibility(View.GONE);
				sousuo_data_layout_all.setVisibility(View.GONE);
				kuaijei_layout.setVisibility(View.VISIBLE);
				sousuo_pingdao_layout.setVisibility(View.GONE);
				sousuo_richeng_layout.setVisibility(View.GONE);
			} else {
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				this.finish();
			}
			break;
		case R.id.top_ll_right:
			if (sousuoEditText.getText().toString().trim() != null
					&& !sousuoEditText.getText().toString().trim().equals("")) {
				String tex = sousuoEditText.getText().toString().trim();
				intentsousuoString = sousuoEditText.getText().toString().trim();
				loadsousuodata(sousuoEditText.getText().toString().trim());
				saveGZJtoBengdi(tex);
			} else {
				Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.new_fragment_sousuo_mylistviewview1_title:// 跳频道详情
			Intent intent1 = new Intent(context,
					NewMyFoundSouSuoXiangqingActivity.class);
			intent1.putExtra("EditTextdata", intentsousuoString);
			intent1.putExtra("type", "1");
			startActivity(intent1);
			break;
		case R.id.new_fragment_sousuo_mylistviewview2_title:// 跳日程详情
			Intent intent2 = new Intent(context,
					NewMyFoundSouSuoXiangqingActivity.class);
			intent2.putExtra("EditTextdata", intentsousuoString);
			intent2.putExtra("type", "2");
			startActivity(intent2);
			break;
		case R.id.container1_delete_sousuo_jilu:
			kaijiesousuodata = "";
			prefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.KuaiJieSouSuo, kaijiesousuodata);
			intdataBengdiNOTIFY();
			break;
		default:
			break;
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
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("foundcount");
		App.getHttpQueues().cancelAll("down");
	}
}
