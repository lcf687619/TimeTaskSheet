package com.mission.schedule.activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindMyPassword extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_save)
	private LinearLayout top_ll_save;
	@ViewResId(id = R.id.email_et)
	private EditText email_et;
//	@ViewResId(id = R.id.zhanghao_et)
//	private EditText zhanghao_et;
	@ViewResId(id = R.id.btn_summit)
	private Button btn_summit;

	Context context;
	String type;
	
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_save.setOnClickListener(this);
		btn_summit.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_findmypassword);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
	}

	private void LoadData() {
//		if (Utils.checkMobilePhone(zhanghao_et.getEditableText().toString()
//				.trim())) {
//			type = "0";
//		} else if (Utils.isEmail(zhanghao_et.getEditableText().toString()
//				.trim())) {
//			type = "1";
//		} else {
//			type = "";
//			Toast.makeText(context, "账号或邮箱输入有误!", Toast.LENGTH_SHORT).show();
//			return;
//		}
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//			String path = URLConstants.找回密码;
			String path = URLConstants.新找回密码+email_et.getEditableText().toString().trim();
//			list.add(new BasicNameValuePair("uNickName", zhanghao_et.getEditableText().toString().trim()));
//			list.add(new BasicNameValuePair("uEmail", email_et.getEditableText().toString().trim()));
//					+ email_et.getEditableText().toString().trim()
//					+ "&emailOrPhone="
//					+ zhanghao_et.getEditableText().toString().trim()
//					+ "&type=" + type;
			if("".equals(email_et.getEditableText().toString().trim()))
				return;
			FindMyPWAsync(path);
		} else {
			Toast.makeText(context, "请检查您的网络!", Toast.LENGTH_SHORT).show();
			return;
		}
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
		case R.id.btn_summit:
			LoadData();
			break;
		default:
			break;
		}
	}
	private void FindMyPWAsync(String path){
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在提交中......");
		StringRequest request = new StringRequest(Method.GET, path, new Listener<String>() {

			@Override
			public void onResponse(String result) {
				progressUtil.dismiss();
				if (!TextUtils.isEmpty(result)) {
					try {
						Gson gson = new Gson();
						SuccessOrFailBean bean = gson.fromJson(result,
								SuccessOrFailBean.class);
						if (bean.status == 0) {
							Toast.makeText(context, "已提交成功！", Toast.LENGTH_SHORT)
									.show();
							finish();
						} else if(bean.status == 1){
							alertFailPWDDialog();
							return;
						}else {
							alertFailIntenetDialog();
							return;
						}
//						else if(bean.status == 3){
//							alertFailDialog();
//							return;
//						}
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
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
		delete_tv.setText("昵称和邮箱不匹配！");
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
		delete_tv.setText("邮箱发送失败！");
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
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
