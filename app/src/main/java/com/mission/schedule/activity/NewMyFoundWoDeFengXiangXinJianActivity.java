package com.mission.schedule.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
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

public class NewMyFoundWoDeFengXiangXinJianActivity extends BaseActivity
		implements OnClickListener {
	@ViewResId(id = R.id.focusName_tv)
	private TextView focusName_tv;// 大标题
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.top_ll_right_textview_text)
	private TextView top_ll_right_textview_text;

	@ViewResId(id = R.id.new_activity_myfound_wodefengxiang_xinjianpingdao_edittext)
	private EditText mEditText;
	Context context;
	SharedPrefUtil prefUtil = null;
	String path;
	String name;
	String UserID;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;
		case R.id.top_ll_right:// 新建保存
			if (mEditText.getText().toString().trim() != null
					&& !mEditText.getText().toString().trim().equals("")) {
				int maxStringlength = "时间表标题的大小".getBytes().length;
				String mEditTextTitle = mEditText.getText().toString().trim();
				// String mEditTextTitleend="";
				// if(mEditTextTitle.contains(" ")){
				// mEditTextTitleend=mEditTextTitle.replace(" ", "");
				// }else{
				// mEditTextTitleend=mEditTextTitle;
				// }
				int num = mEditTextTitle.getBytes().length;
				if (num <= maxStringlength) {
					name = mEditTextTitle;
					sendpath(name);
				} else {
					Toast.makeText(context, "频道标题最多8个汉字长度", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(context, "请输入您的频道标题", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	private void sendpath(String name) {
		path = URLConstants.新版发现新建分享;
		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("tbAttentionUserSpace.userId", UserID);
		pairs.put("tbAttentionUserSpace.name", name);
		pairs.put("tbAttentionUserSpace.titleImg", "");
		pairs.put("tbAttentionUserSpace.backgroundImg", "");
		pairs.put("tbAttentionUserSpace.startStateImg", "0,0");
		pairs.put("tbAttentionUserSpace.clickCount", "0");
		pairs.put("tbAttentionUserSpace.remark5", "");
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			senddata(path, pairs);
		} else {
			Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
		}
	}

	private void senddata(String path, final Map<String, String> map) {
		StringRequest request = new StringRequest(Method.POST, path,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						if (!TextUtils.isEmpty(result)) {
							Gson gson = new Gson();
							SuccessOrFailBean beans = gson.fromJson(result,
									SuccessOrFailBean.class);
							if (beans.status == 0) {
								Toast.makeText(context, "新建分享成功...",
										Toast.LENGTH_SHORT).show();
								backandfish();
							} else {
								Toast.makeText(context, "新建分享失败...",
										Toast.LENGTH_SHORT).show();
								return;
							}
						} else {
							Toast.makeText(context, "新建分享失败...",
									Toast.LENGTH_SHORT).show();
							return;
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Toast.makeText(context, "新建分享失败...", Toast.LENGTH_SHORT)
								.show();
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

	private void backandfish() {
		Intent intent1 = new Intent();
		setResult(Activity.RESULT_OK, intent1);
		this.finish();
	}

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newmyfound_wodefengxiang_xinjianpingdao);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		UserID = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		focusName_tv.setText("设置");
		top_ll_right_textview_text.setText("保存");
		top_ll_right_textview_text.setTextColor(getResources().getColor(
				R.color.sunday_txt));
	}

	@Override
	protected void setAdapter() {

	}

}
