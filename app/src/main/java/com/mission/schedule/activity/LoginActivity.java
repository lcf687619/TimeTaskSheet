package com.mission.schedule.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.ResginBackBean;
import com.mission.schedule.bean.ResginBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.db.DBSourse;
import com.mission.schedule.service.DownQianDaoService;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.edt_user)
	private EditText edt_user;
	@ViewResId(id = R.id.edt_pwd)
	private EditText edt_pwd;
	@ViewResId(id = R.id.btn_login)
	private Button btn_login;
	@ViewResId(id = R.id.tv_zhuce)
	private TextView tv_zhuce;
	@ViewResId(id = R.id.tv_wangji)
	private TextView tv_wangji;
	@ViewResId(id = R.id.lijishiyong_bt)
	private Button lijishiyong_bt;

	Context context;
	SharedPrefUtil sharedPrefUtil;
	public List<ResginBean> list = new ArrayList<ResginBean>();
	private String userid;
	ProgressUtil progressUtil = new ProgressUtil();
	int requestType = 0;//0正常登录，1立即使用
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		btn_login.setOnClickListener(this);
		tv_zhuce.setOnClickListener(this);
		tv_wangji.setOnClickListener(this);
		lijishiyong_bt.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_loginactivity);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		activityManager =ActivityManager1.getInstance();
		activityManager.addActivities(this);
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
	}

	@Override
	protected void setAdapter() {
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_login:
				if ("".equals(userid)) {//用户未登录
					requestType = 0;
					new WriteDataBase().execute();
				} else {
					if (Utils.isEmail(edt_user.getText().toString())) {
						if (edt_pwd.getText().toString().trim().length() >= 6) {
							String path = URLConstants.登录
									+ "?uname="
									+ edt_user.getText().toString().trim()
									+ "&pwd="
									+ edt_pwd.getText().toString().trim()
									+ "&type="
									+ 4
									+ "&uClintAddr="
									+ JPushInterface
									.getUdid(getApplicationContext());
							// new LoginAsync().execute(path);
							progressUtil.ShowProgress(context, true, true,
									"正在登录......");
							StringRequest request = new StringRequest(
									Request.Method.GET, path,
									new Response.Listener<String>() {
										@Override
										public void onResponse(String s) {
											progressUtil.dismiss();
											if (!TextUtils.isEmpty(s)) {
												try {
													Gson gson = new Gson();
													ResginBackBean backBean = gson
															.fromJson(
																	s,
																	ResginBackBean.class);
													if (backBean.status == 0) {
														sharedPrefUtil
														.putString(
																context,
																ShareFile.USERFILE,
																ShareFile.NewMyFoundFenXiangFirst,
																"1");
														list = backBean.list;
														sharedPrefUtil.putString(context, ShareFile.USERFILE,
																ShareFile.ISYOUKE, "1");
														sharedPrefUtil
																.putString(
																		context,
																		ShareFile.USERFILE,
																		ShareFile.USEREMAIL,
																		list.get(0).uEmail);
														sharedPrefUtil
																.putString(
																		context,
																		ShareFile.USERFILE,
																		ShareFile.TELEPHONE,
																		list.get(0).uMobile);
														sharedPrefUtil.putString(
																context,
																ShareFile.USERFILE,
																ShareFile.USERID,
																list.get(0).uId
																		+ "");
														sharedPrefUtil
																.putString(
																		context,
																		ShareFile.USERFILE,
																		ShareFile.USERNAME,
																		list.get(0).uNickName);
														sharedPrefUtil
																.putString(
																		context,
																		ShareFile.USERFILE,
																		ShareFile.USERSTATE,
																		"1");
														sharedPrefUtil
																.putString(
																		context,
																		ShareFile.USERFILE,
																		ShareFile.USERBACKGROUNDPATH,
																		list.get(0).uBackgroundImage);
														if (!"".equals(list.get(0).uPortrait)) {
															String str = list
																	.get(0).uPortrait
																	.toString();
															str = str.replace("\\",
																	"");
															sharedPrefUtil
																	.putString(
																			context,
																			ShareFile.USERFILE,
																			ShareFile.USERPHOTOPATH,
																			str);
														}
														sharedPrefUtil.putString(
																context,
																ShareFile.USERFILE,
																ShareFile.U_ACC_NO,
																list.get(0).uAccNo);
														sharedPrefUtil
																.putString(
																		context,
																		ShareFile.USERFILE,
																		ShareFile.ISYOUKE,
																		list.get(0).uIsActive);
														startActivity(new Intent(
																context,
																MainActivity.class));
														LoginActivity.this.finish();
													} else if (backBean.status == 1) {
														alertFailZHDialog();
													} else if (backBean.status == 4) {
														alertFailPWDDialog();
													} else {
														alertFailIntenetDialog();
													}
												} catch (JsonSyntaxException e) {
													e.printStackTrace();
												}
											} else {
												alertFailIntenetDialog();
											}
										}
									}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(
										VolleyError volleyError) {
									progressUtil.dismiss();
									alertFailIntenetDialog();
								}
							});
							request.setTag("login");
							request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
							App.getHttpQueues().add(request);
						} else {
							Toast.makeText(context, "密码至少六位!!!", Toast.LENGTH_LONG)
									.show();
						}
					} else {
						Toast.makeText(context, "账号输入有误!!!", Toast.LENGTH_LONG)
								.show();
					}
				}
				break;
			case R.id.tv_zhuce:
				startActivity(new Intent(context, NewResiginActivity.class));
				break;
			case R.id.lijishiyong_bt:
				if ("".equals(userid)) { // 0初次安装
					requestType = 1;
					new WriteDataBase().execute();
				} else {
					Toast.makeText(context, "请检查网络连接!", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.tv_wangji:
				startActivity(new Intent(context, FindMyPassword.class));
				break;
			default:
				break;
		}
	}


	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					Toast.makeText(context, "出错了...", Toast.LENGTH_SHORT).show();

					break;
				case 1:
					Toast.makeText(context, "存储卡不可用，请确认已插入卡后再使用本程序",
							Toast.LENGTH_SHORT).show();
					LoginActivity.this.finish();

					break;
			}
		}

	};

	class WriteDataBase extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			toWriteData(DBSourse.dataBaseName, R.raw.data);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(requestType==0){
				IntenetData();
				if (Utils.isEmail(edt_user.getText().toString())) {
					if (edt_pwd.getText().toString().trim().length() >= 6) {
						String path = URLConstants.登录
								+ "?uname="
								+ edt_user.getText().toString().trim()
								+ "&pwd="
								+ edt_pwd.getText().toString().trim()
								+ "&type="
								+ 4
								+ "&uClintAddr="
								+ JPushInterface
								.getUdid(getApplicationContext())
								+ "&uTocode=android";
						// new LoginAsync().execute(path);
						progressUtil.ShowProgress(context, true, true,
								"正在登录......");
						StringRequest request = new StringRequest(
								Request.Method.GET, path,
								new Response.Listener<String>() {
									@Override
									public void onResponse(String s) {
										progressUtil.dismiss();
										if (!TextUtils.isEmpty(s)) {
											try {
												Gson gson = new Gson();
												ResginBackBean backBean = gson
														.fromJson(
																s,
																ResginBackBean.class);
												if (backBean.status == 0) {
													sharedPrefUtil
													.putString(
															context,
															ShareFile.USERFILE,
															ShareFile.NewMyFoundFenXiangFirst,
															"1");
													list = backBean.list;
													sharedPrefUtil.putString(context, ShareFile.USERFILE,
															ShareFile.ISYOUKE, "1");
													sharedPrefUtil
															.putString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.USEREMAIL,
																	list.get(0).uEmail);
													sharedPrefUtil
															.putString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.TELEPHONE,
																	list.get(0).uMobile);
													sharedPrefUtil.putString(
															context,
															ShareFile.USERFILE,
															ShareFile.USERID,
															list.get(0).uId
																	+ "");
													sharedPrefUtil
															.putString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.USERNAME,
																	list.get(0).uNickName);
													sharedPrefUtil
															.putString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.USERSTATE,
																	"1");
													sharedPrefUtil
															.putString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.USERBACKGROUNDPATH,
																	list.get(0).uBackgroundImage);
													if (!"".equals(list.get(0).uPortrait)) {
														String str = list
																.get(0).uPortrait
																.toString();
														str = str.replace("\\",
																"");
														sharedPrefUtil
																.putString(
																		context,
																		ShareFile.USERFILE,
																		ShareFile.USERPHOTOPATH,
																		str);
													}
													sharedPrefUtil.putString(
															context,
															ShareFile.USERFILE,
															ShareFile.U_ACC_NO,
															list.get(0).uAccNo);
													sharedPrefUtil
															.putString(
																	context,
																	ShareFile.USERFILE,
																	ShareFile.ISYOUKE,
																	list.get(0).uIsActive);
													startActivity(new Intent(
															context,
															MainActivity.class));
													LoginActivity.this.finish();
												} else if (backBean.status == 1) {
													alertFailZHDialog();
												} else if (backBean.status == 4) {
													alertFailPWDDialog();
												} else {
													alertFailIntenetDialog();
												}
											} catch (JsonSyntaxException e) {
												e.printStackTrace();
											}
										} else {
											alertFailIntenetDialog();
										}
									}
								}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(
									VolleyError volleyError) {
								progressUtil.dismiss();
							}
						});
						request.setTag("login");
						App.getHttpQueues().add(request);
					} else {
						Toast.makeText(context, "密码至少六位!!!", Toast.LENGTH_LONG)
								.show();
					}
				} else {
					Toast.makeText(context, "账号输入有误!!!", Toast.LENGTH_LONG)
							.show();
				}
			}else if(requestType==1){
				IntenetData();
				String path = URLConstants.注册;
				progressUtil.ShowProgress(context, true, true, "正在登录......");
				StringRequest request = new StringRequest(Request.Method.POST,
						path, new Response.Listener<String>() {
					@Override
					public void onResponse(String s) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(s)) {
							try {
								Gson gson = new Gson();
								ResginBackBean backBean = gson
										.fromJson(s,
												ResginBackBean.class);
								if (backBean.status == 0) {
									sharedPrefUtil
									.putString(
											context,
											ShareFile.USERFILE,
											ShareFile.NewMyFoundFenXiangFirst,
											"1");
									List<ResginBean> list = backBean.list;
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USEREMAIL,
											list.get(0).uEmail);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.TELEPHONE,
											list.get(0).uMobile);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERID,
											list.get(0).uId + "");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERNAME,
											list.get(0).uNickName);
									sharedPrefUtil
											.putString(
													context,
													ShareFile.USERFILE,
													ShareFile.USERBACKGROUNDPATH,
													list.get(0).uBackgroundImage);
									if (!"".equals(list.get(0).uPortrait)) {
										String str = list.get(0).uPortrait
												.toString();
										str = str.replace("\\", "");
										Log.d("TAG", str);
										sharedPrefUtil
												.putString(
														context,
														ShareFile.USERFILE,
														ShareFile.USERPHOTOPATH,
														str);
									}
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERSTATE, "1");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.U_ACC_NO,
											list.get(0).uAccNo);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.ISYOUKE,
											list.get(0).uIsActive);
									startActivity(new Intent(context,
											MainActivity.class));
									finish();
								} else if (backBean.status == 1) {
									Toast.makeText(context,
											backBean.message,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "登录失败！",
											Toast.LENGTH_SHORT).show();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(context, "登录失败,请重试!",
									Toast.LENGTH_LONG).show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
						alertFailIntenetDialog();
					}
				}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("uname", "游客");
						map.put("pwd", "123456");
						map.put("type", "2");
						map.put("uClintAddr", Utils.getSN());
						map.put("uTocode", "android");
						map.put("uSourceType", "1");
						map.put("pushMac",
								JPushInterface.getUdid(getApplicationContext()));
						return map;
					}
				};
				request.setTag("login");
				request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
				App.getHttpQueues().add(request);
			}
		}

	}

	/**
	 * 初始化数据库
	 *
	 * @param dataBaseName
	 * @param resourse
	 */
	private void toWriteData(String dataBaseName, int resourse) {
		InputStream is = getResources().openRawResource(resourse); // 欲导入的数据库
		OutputStream fos;
		try {
			String dbpath = "/data/data/com.mission.schedule/databases/"
					+ dataBaseName;
			if (DBSourse.createFile(dbpath)) {
				fos = new FileOutputStream(dbpath);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) != -1) {
					fos.write(buffer, 0, count);
				}
				fos.flush();
				fos.close();
				is.close();

				SharedPreferences sp = getSharedPreferences("localchedule",
						Context.MODE_PRIVATE);
				SharedPreferences.Editor e = sp.edit();
				e.putString("isDataWrite", "1");// 0为写入数据库文件
				e.commit();
				Log.d("TAG", "数据库导入成功");
			} else {
				handler.sendEmptyMessage(0);
				Log.d("TAG", "数据库导入失败");
			}
		} catch (Exception e) {
			handler.sendEmptyMessage(0);
			Log.d("TAG", "数据库导入失败");
		}
	}

	private void alertFailZHDialog() {
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
		delete_tv.setText("账号不存在！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	private void alertFailPWDDialog() {
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
		delete_tv.setText("密码输入有误！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	private void alertFailIntenetDialog() {
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
		delete_tv.setText("网络请求超时！");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	private void IntenetData() {
		if (heigh == 0) {
			return;
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(context,
							DownQianDaoService.class);
					intent.putExtra("heigh", heigh);
					intent.setAction("updateData");
					intent.setPackage(getPackageName());
					startService(intent);
				}
			}).start();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("login");
	}
}
