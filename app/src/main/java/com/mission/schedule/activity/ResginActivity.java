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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.ResginBackBean;
import com.mission.schedule.bean.ResginBean;
import com.mission.schedule.bean.SuccessOrFailBean;
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
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class ResginActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.edt_user)
	private EditText edt_user;
	@ViewResId(id = R.id.edt_pwd)
	private EditText edt_pwd;
	@ViewResId(id = R.id.btn_login)
	private Button btn_login;
	Context context;
	SharedPrefUtil sharedPrefUtil;
	private static ActivityManager1 instance;
	String userid;
	ProgressUtil progressUtil = new ProgressUtil();

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		btn_login.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_resginactivity);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		instance = ActivityManager1.getInstance();
		instance.addActivities(this);
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
	}

	@Override
	protected void setAdapter() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("resgin");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;
		case R.id.btn_login:
			if ("".equals(userid)) {
				new WriteDataBase().execute();
			} else {
				if (Utils.isEmail(edt_user.getText().toString())) {
					if (edt_pwd.getText().toString().trim().length() >= 6) {
						String path = URLConstants.修改个人信息 + "?uid=" + userid
								+ "&uNickName=" + "" + "&uEmail="
								+ edt_user.getText().toString() + "&uMobile="
								+ "" + "&pwd=123456" + "&newPwd="
								+ edt_pwd.getText().toString() + "&sex=" + "";
						YouKeResginAsync(path);
					} else {
						Toast.makeText(context, "密码至少六位!!!", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(context, "账号输入有误!!!", Toast.LENGTH_SHORT)
							.show();
				}
			}
			// }
			break;
		default:
			break;
		}
	}

	private void ResginAsync(String path) {
		progressUtil.ShowProgress(context, true, true, "正在努力加载......");
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						Log.d("TAG", result);
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								ResginBackBean backBean = gson.fromJson(result,
										ResginBackBean.class);
								if (backBean.status == 0) {
									sharedPrefUtil.putString(context,
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
											ShareFile.USERID, list.get(0).uId
													+ "");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERNAME,
											list.get(0).uNickName);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERBACKGROUNDPATH,
											list.get(0).uBackgroundImage);
									if (!"".equals(list.get(0).uPortrait)) {
										String str = list.get(0).uPortrait
												.toString();
										str = str.replace("\\", "");
										Log.d("TAG", str);
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
											list.get(0).uAccNo);
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.ISYOUKE, "1");
									startActivity(new Intent(context,
											MainActivity.class));
									for (int i = 0; i < instance
											.getActivities().size(); i++) {
										instance.getActivities().get(i)
												.finish();
									}
								} else if (backBean.status == 1) {
									Toast.makeText(context, backBean.message,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "登录失败！",
											Toast.LENGTH_SHORT).show();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
						alertFailIntenetDialog();
					}
				});
		request.setTag("resgin");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	private void YouKeResginAsync(String path) {
		progressUtil.ShowProgress(context, true, true, "正在努力加载......");
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {

					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						Log.d("TAG", result);
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								SuccessOrFailBean backBean = gson.fromJson(
										result, SuccessOrFailBean.class);
								if (backBean.status == 0) {
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.NewMyFoundFenXiangFirst,
											"1");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USEREMAIL, edt_user
													.getText().toString());
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERSTATE, "1");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.ISYOUKE, "1");
									sharedPrefUtil.putString(context,
											ShareFile.USERFILE,
											ShareFile.USERNAME, edt_user
													.getText().toString());
									for (int i = 0; i < instance
											.getActivities().size(); i++) {
										instance.getActivities().get(i)
												.finish();
									}
									startActivity(new Intent(context,
											MainActivity.class));
								} else if (backBean.status == 1) {
									Toast.makeText(context, backBean.message,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "登录失败！",
											Toast.LENGTH_SHORT).show();
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
						alertFailIntenetDialog();
					}
				});
		request.setTag("resgin");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
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
				ResginActivity.this.finish();

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
			IntenetData();
			if (Utils.isEmail(edt_user.getText().toString())) {
				if (edt_pwd.getText().toString().trim().length() >= 6) {
					String path = URLConstants.注册 + "?uname="
							+ edt_user.getText().toString() + "&pwd="
							+ edt_pwd.getText().toString() + "&type=" + 1
							+ "&uClintAddr=" + Utils.getSN() + "&uTocode="
							+ "android" + "&uSourceType=" + 1 + "&pushMac="
							+ JPushInterface.getUdid(getApplicationContext());
					ResginAsync(path);
				} else {
					Toast.makeText(context, "密码至少六位!!!", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(context, "账号输入有误!!!", Toast.LENGTH_SHORT).show();
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
}
