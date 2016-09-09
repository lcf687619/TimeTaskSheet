package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.adapter.RepeatTuiJianMoreAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatTuiJianBackBean;
import com.mission.schedule.bean.RepeatTuiJianItemBean;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.R;

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

public class RepeatTuiJianMoreActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.more_lv)
	private ListView more_lv;
	
	List<RepeatTuiJianItemBean> list = new ArrayList<RepeatTuiJianItemBean>();
	RepeatTuiJianMoreAdapter adapter = null;
	
	Context context;
	
	ProgressUtil progressUtil = new ProgressUtil();
	
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_repeattuijianmore);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		if(NetUtil.getConnectState(context)!=NetWorkState.NONE){
			String path = URLConstants.重复推荐;
			TuiJianAsync(path);
		}else {
			return;
		}
		
	}

	@Override
	protected void setAdapter() {
		
	}
	private void TuiJianAsync(String path){
		progressUtil.ShowProgress(context, true, true, "正在加载...");
		StringRequest request = new StringRequest(Method.GET, path, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				progressUtil.dismiss();
				if (!TextUtils.isEmpty(result)) {
					Gson gson = new Gson();
					try {
						RepeatTuiJianBackBean backbean = gson.fromJson(result,
								RepeatTuiJianBackBean.class);
						if (backbean.status == 0) {
							list = backbean.page.items;
							if (list != null && list.size() > 0) {
								adapter = new RepeatTuiJianMoreAdapter(context, list);
								more_lv.setAdapter(adapter);
								item();
							} else {
								return;
							}
						} else {
							return;
						}
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				} else {
					return;
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
	private void item(){
		more_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RepeatTuiJianItemBean bean = (RepeatTuiJianItemBean) more_lv.getAdapter().getItem(position);
				Intent intent = new Intent(context, RepeatFriendsRiChengActivity.class);
				intent.putExtra("uid", bean.uid);
				intent.putExtra("name", bean.uNickName);
				startActivity(intent);
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
}
