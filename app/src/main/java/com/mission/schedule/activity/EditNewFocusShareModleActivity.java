package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EditNewFocusShareModleActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.modle_lv)
	private ListView modle_lv;
	@ViewResId(id = R.id.right_tv)
	private TextView right_tv;
	
	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	String title = "";
	int uid = 0;
	String photopath;
	String backgroundpath;
	String sharetitle;
	String modlestr;
	String contentstr;
	String userid;
	
	String modledesc = "";
	
	ChooseStateAdapter adapter = null;
	List<String> list = new ArrayList<String>();
	private Map<Integer, Boolean> isSelected;
	private int lastIndex;
	
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_editnewfocussharemodle);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE, ShareFile.USERID, "0");
		uid = getIntent().getIntExtra("uid", 0);
		title = getIntent().getStringExtra("title");
		photopath = getIntent().getStringExtra("photopath");
		backgroundpath = getIntent().getStringExtra("backgroundpath");
		sharetitle = getIntent().getStringExtra("sharetitle");
		modlestr = getIntent().getStringExtra("modlestr");
		contentstr = getIntent().getStringExtra("contentstr");
		middle_tv.setText("模式选择");
		right_tv.setText("确认");
		list.add("纯文本模式");
		list.add("图片日程模式");
		list.add("图片倒数日模式");
		if (isSelected != null)
			isSelected = null;
		isSelected = new HashMap<Integer, Boolean>();
		initdata();
	}

	private void initdata() {
		for (int i = 0; i < list.size(); i++) {
			isSelected.put(i, false);
		}
		if ("0".equals(modlestr)) {
			lastIndex = 0;
		} else if ("1".equals(modlestr)) {
			lastIndex = 1;
		} else {
			lastIndex = 2;
		}
	}
	@Override
	protected void setAdapter() {
		adapter = new ChooseStateAdapter(context, list, R.layout.adapter_choosestate, lastIndex);
		modle_lv.setAdapter(adapter);
		modle_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter.notifyDataSetChanged();
		modle_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				modledesc = modle_lv.getAdapter().getItem(position)
						.toString();
				if ("".equals(modledesc)) {
					return;
				} else {
					if ("纯文本模式".equals(modledesc)) {
						modlestr = "0";
					} else if ("图片日程模式".equals(modledesc)) {
						modlestr = "1";
					} else if ("图片倒数日模式".equals(modledesc)) {
						modlestr = "2";
					} 
					initdata();
//					// 当前点击的CB
//					boolean cu = !isSelected.get(position);
//					// 先将所有的置为FALSE
//					for (Integer p : isSelected.keySet()) {
//						isSelected.put(p, false);
//					}
//					// 再将当前选择CB的实际状态
//					isSelected.put(position, cu);
					adapter = new ChooseStateAdapter(context, list, R.layout.adapter_choosestate, lastIndex);
					modle_lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;
		case R.id.top_ll_right:
			if(NetUtil.getConnectState(context)!=NetWorkState.NONE){
				if ("纯文本模式".equals(modledesc)) {
					modlestr = "0";
				} else if ("图片日程模式".equals(modledesc)) {
					modlestr = "1";
				} else if ("图片倒数日模式".equals(modledesc)) {
					modlestr = "2";
				} 
				AlterMessageData();
			}else{
				Toast.makeText(context, "请检查您的网络!", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	public class ChooseStateAdapter extends CommonAdapter<String> {
		
		private int lastIndex;

		public ChooseStateAdapter(Context context, List<String> lDatas,
				int layoutItemID, int lastIndex) {
			super(context, lDatas, layoutItemID);
			this.lastIndex = lastIndex;
		}

		@Override
		public void getViewItem(ViewHolder holder, final String item, final int position) {
			TextView state_item_title = holder.getView(R.id.state_item_title);
			CheckBox state_item_cb = holder.getView(R.id.state_item_cb);
			state_item_title.setText(item);
			state_item_cb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					for (int i = 0; i < list.size(); i++) {
						isSelected.put(i, false);
					}
					lastIndex = position;
					notifyDataSetChanged();
					modledesc = item;
					if ("纯文本模式".equals(item)) {
						modlestr = "0";
					} else if ("图片日程模式".equals(item)) {
						modlestr = "1";
					} else if ("图片倒数日模式".equals(item)) {
						modlestr = "2";
					} 
				}
			});
			if (lastIndex == position) {
				state_item_cb.setChecked(true);
			}else{
				state_item_cb.setChecked(false);
			}
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
					listBeen.name = title;
					listBeen.titleImg = photopath;
					listBeen.backgroundImg = backgroundpath;
					listBeen.styleView = modlestr;
					listBeen.remark5 = sharetitle;
					listBeen.remark6 = contentstr;
					Intent intent = new Intent();
					intent.putExtra("bean", listBeen);
					setResult(Activity.RESULT_OK, intent);
					EditNewFocusShareModleActivity.this.finish();
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
				map.put("tbAttentionUserSpace.name", title);
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
