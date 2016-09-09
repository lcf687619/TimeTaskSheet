package com.mission.schedule.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AlterEMailActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_save)
	private LinearLayout top_ll_save;
	@ViewResId(id = R.id.email_et)
	private EditText email_et;

	Context context;
	String email;
	String path;
	SharedPrefUtil sharedPrefUtil = null;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_save.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_alteremail);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		email =sharedPrefUtil.getString(context, ShareFile.USERFILE, ShareFile.USEREMAIL, "");
		email_et.setText(email);
		email_et.setSelection(email_et.getText().length());
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
		case R.id.top_ll_save:
			if (email.equals(email_et.getText().toString().trim())) {
				Toast.makeText(context, "邮箱没有发生改变！", Toast.LENGTH_SHORT).show();
				return;
			} else {
				path = URLConstants.修改个人信息;
				if(NetUtil.getConnectState(context)!=NetWorkState.NONE){
					SaveMessageAsync(path);	
				}else {
					Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
					return;
				}
				
			}
			break;
		default:
			break;
		}
	}
	private void SaveMessageAsync(String path){
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context,true,true,"正在保存");
		StringRequest request = new StringRequest(Method.POST, path, new Listener<String>() {

			@Override
			public void onResponse(String result) {
				progressUtil.dismiss();
				if(!TextUtils.isEmpty(result)){
					Gson gson = new Gson();
					try {
						SuccessOrFailBean bean = gson.fromJson(result, SuccessOrFailBean.class);
						if(bean.status==0){
							sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.USEREMAIL, email_et.getText()
									.toString());
							Intent intent = new Intent();
							intent.putExtra("email", email_et.getText()
									.toString());
					        setResult(Activity.RESULT_OK, intent);
					        finish();
						}else {
							Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}else {
					return;
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
				Map<String, String> pairs = new HashMap<String, String>();
				pairs.put("uid", sharedPrefUtil
						.getString(context, ShareFile.USERFILE,
								ShareFile.USERID, ""));
//				pairs.put("uNickName", sharedPrefUtil
//						.getString(context, ShareFile.USERFILE,
//								ShareFile.USERNAME, ""));
				pairs.put("uEmail",email_et.getText().toString().trim());
//				pairs.put("uMobile", sharedPrefUtil
//						.getString(context, ShareFile.USERFILE,
//								ShareFile.TELEPHONE, ""));
//				pairs.put("pwd", sharedPrefUtil
//						.getString(context, ShareFile.USERFILE,
//								ShareFile.USERPWD, ""));
//				pairs.put("newPwd", sharedPrefUtil
//						.getString(context, ShareFile.USERFILE,
//								ShareFile.USERPWD, ""));
				return pairs;
			}
		};
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
