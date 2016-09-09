package com.mission.schedule.activity;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.OldLoginBackBean;
import com.mission.schedule.bean.ResginBackBean;
import com.mission.schedule.bean.ResginBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OldLoginActivity extends BaseActivity {

	@ViewResId(id = R.id.phone_et)
	private EditText phone_et;
	@ViewResId(id = R.id.yanzheng_et)
	private EditText yanzheng_et;
	@ViewResId(id = R.id.yanzheng_bt)
	private Button yanzheng_bt;
	@ViewResId(id = R.id.pwd_et)
	private EditText pwd_et;
	@ViewResId(id = R.id.btn_login)
	private Button button;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;

	String uid;
	ProgressUtil progressUtil = new ProgressUtil();
	ActivityManager1 activityManager = null;
	private Timer timer;
	private int second;

	@Override
	protected void setListener() {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginUser();
			}
		});
		yanzheng_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (phone_et.getText().toString().trim().length()==11) {
					senPhoneMessage();
					second = 90;
					yanzheng_bt.setEnabled(false);
					if (timer == null) {
						timer = new Timer();
					}
					timer.schedule(new CodeTimerTash(), 500, 1000);
				}else {
					alertFailIntenetDialog(-1,"手机号输入有误!");
				}
			}
		});
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_oldlogin);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		SharedPreferences sp = getSharedPreferences("userinfoxml",
				Context.MODE_PRIVATE);
		uid = sp.getString("userid", "0");
		timer = new Timer();
		phone_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		initdata();
		checkPhonePermission();
	}

	@Override
	protected void setAdapter() {

	}

	private void initdata() {
		phone_et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().trim().length() == 11) {
					yanzheng_bt.setBackground(getResources().getDrawable(R.drawable.bg_newresgin_send));
					yanzheng_bt.setTextColor(getResources().getColor(R.color.white));
					yanzheng_bt.setEnabled(true);
				} else {
					yanzheng_bt.setBackground(getResources().getDrawable(R.drawable.bg_newresgin_unsend));
					yanzheng_bt.setTextColor(getResources().getColor(R.color.newresgin_hintcolor));
					yanzheng_bt.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
//		String path = URLConstants.老用户数据查询 + uid + "&type=0";
//		progressUtil.ShowProgress(context, true, true, "正在加载......");
//		StringRequest request = new StringRequest(Request.Method.GET, path,
//				new Response.Listener<String>() {
//					@Override
//					public void onResponse(String s) {
//						progressUtil.dismiss();
//						if (!TextUtils.isEmpty(s)) {
//							try {
//								Gson gson = new Gson();
//								OldUserMessageBackBean backBean = gson
//										.fromJson(s,
//												OldUserMessageBackBean.class);
//								if (backBean.status == 0) {
//									List<OldUserMessageBean> list = backBean.list;
//									sharedPrefUtil.putString(context,
//											ShareFile.USERFILE,
//											ShareFile.ISYOUKE, "1");
//									// sharedPrefUtil.putString(context,
//									// ShareFile.USERFILE,
//									// ShareFile.USEREMAIL,
//									// list.get(0).uEmail);
//									// sharedPrefUtil.putString(context,
//									// ShareFile.USERFILE,
//									// ShareFile.TELEPHONE,
//									// list.get(0).uMobile);
//									// sharedPrefUtil.putString(context,
//									// ShareFile.USERFILE,
//									// ShareFile.USERID,
//									// list.get(0).userId);
//									// sharedPrefUtil.putString(context,
//									// ShareFile.USERFILE,
//									// ShareFile.USERNAME,
//									// list.get(0).uName);
//									// if (!"".equals(list.get(0).uPortrait)) {
//									// String str = list.get(0).uPortrait
//									// .toString();
//									// str = str.replace("\\", "");
//									// sharedPrefUtil.putString(context,
//									// ShareFile.USERFILE,
//									// ShareFile.USERPHOTOPATH, str);
//									// }
//									// if (!"".equals(list.get(0).uEmail)
//									// && !"null".equals(list.get(0).uEmail)
//									// && list.get(0).uEmail != null) {
//									// edt_user.setText(list.get(0).uEmail);
//									// edt_user.setSelection(list.get(0).uEmail
//									// .length());
//									// }
//								}
//							} catch (JsonSyntaxException e) {
//								e.printStackTrace();
//							}
//						} else {
//							alertFailIntenetDialog();
//						}
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError volleyError) {
//						progressUtil.dismiss();
//					}
//				});
//		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
//		request.setTag("login");
//		App.getHttpQueues().add(request);
		sharedPrefUtil.putString(context, ShareFile.USERFILE,
				ShareFile.UPDATESTATE, "1");
	}
	private class CodeTimerTash extends TimerTask {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {      // UI thread
				@Override
				public void run() {
					second--;
					if (second == 0) {
						yanzheng_bt.setText("重新发送验证码");
						yanzheng_bt.setEnabled(true);
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
					} else {
						yanzheng_bt.setText(second + "秒后重试");
					}
				}
			});
		}
	}

	private void senPhoneMessage() {
		String path = URLConstants.短信登录获取验证码 + phone_et.getText().toString().trim()+"&type=0";
		Log.d("验证码:",path);
		StringRequest request = new StringRequest(Request.Method.GET, path, new Response.Listener<String>() {
			@Override
			public void onResponse(String result) {
				if(!TextUtils.isEmpty(result)){
					Gson gson = new Gson();
					ResginBackBean backBean = gson.fromJson(result,
							ResginBackBean.class);
					if (backBean.status == 0) {

					}else if(backBean.status==1){
//                        alertFailDialog(1,"");
					}else if(backBean.status==2){
//                        alertFailDialog(2,"");
					}else{
						alertFailDialog(backBean.status,backBean.message);
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				alertFailIntenetDialog(0,"");
			}
		});
		request.setTag("send");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void loginUser() {
		String path = URLConstants.老用户信息迁移
				+ "" + "&val=" + uid
				+ "&pwd="+ pwd_et.getText().toString().trim() + "&mac="
				+ JPushInterface.getUdid(getApplicationContext())
				+ "&mobileType=android&type=1&mobile="+phone_et.getText().toString().trim()
				+"&yzm="+yanzheng_et.getText().toString().trim();
		if (phone_et.getText().toString().trim().length() == 11
				&& Utils.checkMobilePhone(phone_et.getText().toString().trim())) {
			if (yanzheng_et.getText().toString().trim().length() <= 6 &&
					yanzheng_et.getText().toString().trim().length() >= 4) {
				if (pwd_et.getText().toString().trim().length() >= 6) {
					LoginAsync(path);
				} else {
					alertFailIntenetDialog(-1,"密码输入有误!");
				}
			} else {
				alertFailIntenetDialog(-1,"验证码输入有误!");
			}
		} else {
			alertFailIntenetDialog(-1,"手机号输入有误!");
		}
	}

	private void LoginAsync(String path) {
		progressUtil.ShowProgress(context, true, true, "正在登录......");
		StringRequest request = new StringRequest(Method.GET, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								OldLoginBackBean backBean = gson.fromJson(
										result, OldLoginBackBean.class);
								if (backBean.status == 0) {
									ResginBean bean = backBean.tbUser;
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USEREMAIL,
											bean.uEmail);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.TELEPHONE,
											bean.uMobile);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERID, bean.uId
													+ "");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERNAME,
											bean.uNickName);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERBACKGROUNDPATH,
											bean.uBackgroundImage);
									if (!"".equals(bean.uPortrait)) {
										String str = bean.uPortrait
												.toString();
										str = str.replace("\\", "");
										sharedPrefUtil.putString(context,
												ShareFile.USERFILE,
												ShareFile.USERPHOTOPATH, str);
									}
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERSTATE, "1");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.U_ACC_NO,
											bean.uAccNo);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.ISYOUKE, "1");
									startActivity(new Intent(context,
											MainActivity.class));
									activityManager.doAllActivityFinish();
								} else {
									alertFailIntenetDialog(backBean.status,backBean.message);
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
							alertFailIntenetDialog(0,"");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
						alertFailIntenetDialog(0,"");
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(60000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}
	private void alertFailDialog(int type,String message) {
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
		if(type==1){
			delete_tv.setText("验证码超时,请重新获取验证码!");
		}else if(type==2){
			delete_tv.setText("请联系客服!");
		}else {
			if(type==4){
				yanzheng_bt.setText("获取验证码");
				yanzheng_bt.setEnabled(true);
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
			}
			delete_tv.setText(message+"!");
		}
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	private void alertFailIntenetDialog(int type,String msg) {
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
		if(type==-1){
			delete_tv.setText(msg);
		}else if(type==0){
			delete_tv.setText("网络请求超时！");
		}else{
			delete_tv.setText(msg);
		}

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
		App.getHttpQueues().cancelAll("login");
		App.getHttpQueues().cancelAll("log");
		App.getHttpQueues().cancelAll("down");
	}
	private void checkPhonePermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			int checkstorgePhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.STORAGE);
			if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(OldLoginActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
			}
			int checkcontactPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.CONTACTS);
			if(checkcontactPermission != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(OldLoginActivity.this,new String[]{Manifest.permission.READ_CONTACTS},10001);
			}
			int checkLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.LOCATION);
			if(checkLocationPermission != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(OldLoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10001);
			}
			int checkPhoneStatePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.LOCATION);
			if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(OldLoginActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},10001);
			}

		}
	}
}
