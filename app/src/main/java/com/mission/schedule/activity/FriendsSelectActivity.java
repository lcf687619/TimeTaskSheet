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
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.adapter.FriendsSelectAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.FriendsBackBean;
import com.mission.schedule.bean.FriendsBean;
import com.mission.schedule.bean.LiaoTianHistoryBean;
import com.mission.schedule.bean.SendLiaoTianMessageBackBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsSelectActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.friends_lv)
	private ListView friends_lv;

	Context context;
	SharedPrefUtil prefUtil = null;
	String userId;
	String userName;
	String path;
	FriendsSelectAdapter adapter = null;
	List<FriendsBean> beansList = new ArrayList<FriendsBean>();
	FriendsBean friendsBean = null;
	LiaoTianHistoryBean myFriendsBean = null;

	App app;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_friendsselect);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		app = App.getDBcApplication();
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userId = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		userName = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		myFriendsBean = (LiaoTianHistoryBean) getIntent().getSerializableExtra(
				"bean");
		LoadData();
	}

	private void LoadData() {
		path = URLConstants.好友列表查询 + "?uId=" + Integer.parseInt(userId);
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			FriendsQueryAsync(path);
		} else {
			Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void item() {
		friends_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				friendsBean = (FriendsBean) friends_lv.getAdapter().getItem(
						position);
				String sendPath = URLConstants.添加聊天信息;
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("tbuserFrendsMessage.uid", userId);
				pairs.put("tbuserFrendsMessage.cpId",
						String.valueOf(friendsBean.fId));
				pairs.put("tbuserFrendsMessage.messge", myFriendsBean.messge);
				pairs.put("tbuserFrendsMessage.status",
						String.valueOf(myFriendsBean.status));
				pairs.put("tbuserFrendsMessage.cIsAlarm",
						String.valueOf(myFriendsBean.cIsAlarm));
				pairs.put("tbuserFrendsMessage.cdate", myFriendsBean.cdate);
				pairs.put("tbuserFrendsMessage.ctime", myFriendsBean.ctime);
				pairs.put("tbuserFrendsMessage.cRecommendName", userName);
				pairs.put("tbuserFrendsMessage.cAlarmSound",
						myFriendsBean.cAlarmSound);
				pairs.put("tbuserFrendsMessage.cAlarmSoundDesc",
						myFriendsBean.cAlarmSoundDesc);
				pairs.put("tbuserFrendsMessage.repType",
						String.valueOf(myFriendsBean.repType));// 1每天,2每周,3每月,4每年,5工作日
				/**
				 * 生成规则 1每天[] 2 每周[“1”] 3每月[“1”]每月1号 4每年[“01-01”]每年1月1号 5工作日[]
				 */
				pairs.put("tbuserFrendsMessage.repTypeParameter",
						myFriendsBean.repTypeParameter);
				pairs.put("tbuserFrendsMessage.cPostpone",
						String.valueOf(myFriendsBean.cPostpone));// 是否顺延(0否,1是)',
				pairs.put("tbuserFrendsMessage.cTags", myFriendsBean.cTags);// 分类标签'
				pairs.put("tbuserFrendsMessage.cType",
						String.valueOf(myFriendsBean.cType));// 记事类别(0普通的,1带url的,2备忘录以上的都需要带公用参数)',
				pairs.put("tbuserFrendsMessage.cTypeDesc",
						String.valueOf(myFriendsBean.cTypeDesc));// 当记事类别为1时所带的url链接',
				pairs.put("tbuserFrendsMessage.cTypeSpare",
						myFriendsBean.cTypeSpare);// 当记事类别为1时所带的url链接描述',
				pairs.put("tbuserFrendsMessage.cOpenstate",
						String.valueOf(myFriendsBean.cOpenstate));// 公开状态(0否,1是,2仅好友可见)',
				pairs.put("tbuserFrendsMessage.cLightAppId",
						String.valueOf(myFriendsBean.cLightAppId));// 轻应用与记事绑定的唯一ID'
				pairs.put("tbuserFrendsMessage.repcolortype",
						String.valueOf(myFriendsBean.repcolortype));// 记事颜色类别',
				pairs.put("tbuserFrendsMessage.repstartdate",
						myFriendsBean.repstartdate);// 下一次重复闹钟起始时间'
				pairs.put("tbuserFrendsMessage.repnextcreatedtime",
						myFriendsBean.repnextcreatedtime);// 下一次生成日期 如
															// 2012-01-01 12：00
				pairs.put("tbuserFrendsMessage.replastcreatedtime",
						myFriendsBean.replastcreatedtime);// 之前最后一次生成时间
															// 如2012-01-01 12：00
				/**
				 * 是否显示时间 0否 1是 默认为1
				 */
				pairs.put("tbuserFrendsMessage.repdisplaytime",
						String.valueOf(myFriendsBean.repdisplaytime));
				pairs.put("tbuserFrendsMessage.repinitialcreatedtime",
						myFriendsBean.repinitialcreatedtime);// 初始创建时间
				pairs.put("tbuserFrendsMessage.aType", myFriendsBean.aType + "");// 0无附加信息1附加连接2附加图片3连接和图片
				pairs.put("tbuserFrendsMessage.webUrl", myFriendsBean.webUrl);
				pairs.put("tbuserFrendsMessage.imgPath", myFriendsBean.imgPath);
				pairs.put("tbuserFrendsMessage.repInSTable",
						myFriendsBean.repInSTable + "");

				app.insertMFMessageSendData(Integer.parseInt(userId),
						friendsBean.fId, myFriendsBean.cIsAlarm,
						myFriendsBean.cOpenstate, myFriendsBean.cPostpone, 0,
						myFriendsBean.repdisplaytime, myFriendsBean.cBeforTime,
						myFriendsBean.cType, myFriendsBean.repType,
						myFriendsBean.repTypeParameter, myFriendsBean.messge,
						myFriendsBean.cretetime, myFriendsBean.cdate,
						myFriendsBean.ctime, myFriendsBean.cTypeDesc,
						myFriendsBean.cTypeSpare, myFriendsBean.cTags,
						myFriendsBean.cAlarmSoundDesc,
						myFriendsBean.cAlarmSound, myFriendsBean.repstartdate,
						myFriendsBean.repinitialcreatedtime,
						myFriendsBean.replastcreatedtime,
						myFriendsBean.repnextcreatedtime, myFriendsBean.status,
						myFriendsBean.aType, myFriendsBean.webUrl,
						myFriendsBean.imgPath, myFriendsBean.repInSTable);

				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					MySendMessageAsync(sendPath, pairs);
				} else {
					Toast.makeText(context, "请检查网络..", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
		});

	}

	@Override
	protected void setAdapter() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	private void FriendsQueryAsync(String path) {
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							FriendsBackBean backBean = gson.fromJson(result,
									FriendsBackBean.class);
							if (backBean.status == 0) {
								if (adapter == null) {
									beansList = backBean.tbUserFriendsApp;
									if (beansList != null
											&& beansList.size() > 0) {
										adapter = new FriendsSelectAdapter(
												context, beansList,R.layout.adapter_friendsselect);
										friends_lv.setAdapter(adapter);
										item();
									} else {
										Toast.makeText(context,
												"没有好友，赶紧添加几个吧！",
												Toast.LENGTH_SHORT).show();
									}
								} else {
									beansList.clear();
									beansList.addAll(backBean.tbUserFriendsApp);
									adapter.notifyDataSetChanged();
									item();
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

	private void MySendMessageAsync(String path, final Map<String, String> map) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							try {
								SendLiaoTianMessageBackBean orFailBean = gson
										.fromJson(
												result,
												SendLiaoTianMessageBackBean.class);
								if (orFailBean.status == 0) {
									app.updateMFMessageSendData(orFailBean.id);
									alertDialog();
								} else {
									app.deleteMFMessageSendData();
									alertFailDialog();
									Toast.makeText(context, orFailBean.message,
											Toast.LENGTH_SHORT).show();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
								return;
							}
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
		delete_tv.setText("转发成功！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
				FriendsSelectActivity.this.finish();
			}
		});

	}

	private void alertFailDialog() {
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
		delete_tv.setText("转发失败！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
