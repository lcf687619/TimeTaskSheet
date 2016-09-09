package com.mission.schedule.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditSettingNewFocusShareTitleActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.title_et)
	private EditText title_et;
	
	String content = "";
	int uid = 0;
	String photopath;
	String backgroundpath;
	String sharetitle;
	String modlestr;
	String contentstr;
	String userid;
	
	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_editsettingnewfocussharetitle);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE, ShareFile.USERID, "0");
		uid = getIntent().getIntExtra("uid", 0);
		content = getIntent().getStringExtra("title");
		photopath = getIntent().getStringExtra("photopath");
		backgroundpath = getIntent().getStringExtra("backgroundpath");
		sharetitle = getIntent().getStringExtra("sharetitle");
		modlestr = getIntent().getStringExtra("modlestr");
		contentstr = getIntent().getStringExtra("contentstr");
		
		middle_tv.setText("修改昵称");
		title_et.setText(content);
		title_et.setSelection(content.length());
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
		case R.id.top_ll_right:
			if(NetUtil.getConnectState(context)!=NetWorkState.NONE){
				AlterMessageData();
			}else{
				Toast.makeText(context, "请检查您的网络!", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	private void AlterMessageData(){
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在修改...");
		String path = URLConstants.新版发现修改分享;
		StringRequest request = new StringRequest(Method.POST, path, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				progressUtil.dismiss();
				if(!TextUtils.isEmpty(result)){
					NewMyFoundShouChangListBeen listBeen = new NewMyFoundShouChangListBeen();
					listBeen.id = uid;
					listBeen.name = title_et.getText().toString();
					listBeen.titleImg = photopath;
					listBeen.backgroundImg = backgroundpath;
					listBeen.styleView = modlestr;
					listBeen.remark5 = sharetitle;
					listBeen.remark6 = contentstr;
					Intent intent = new Intent();
					intent.putExtra("bean", listBeen);
					setResult(Activity.RESULT_OK, intent);
					EditSettingNewFocusShareTitleActivity.this.finish();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				progressUtil.dismiss();
				Toast.makeText(context, "保存失败!", Toast.LENGTH_SHORT).show();
			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("tbAttentionUserSpace.id", String.valueOf(uid));
				map.put("tbAttentionUserSpace.name", title_et.getText().toString().trim());
				map.put("tbAttentionUserSpace.userId", userid);
				map.put("tbAttentionUserSpace.titleImg", photopath);
				map.put("tbAttentionUserSpace.backgroundImg", backgroundpath);
				map.put("tbAttentionUserSpace.remark6", contentstr);
				map.put("tbAttentionUserSpace.styleView", modlestr);
				map.put("tbAttentionUserSpace.remark5", sharetitle);
				return map;
			}
		};
		request.setTag("up");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("up");
	}
}
