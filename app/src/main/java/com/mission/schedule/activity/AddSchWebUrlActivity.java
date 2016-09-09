package com.mission.schedule.activity;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddSchWebUrlActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.weburl_et)
	private EditText weburl_et;
	
	Context context;
	App app;
	String url = "";
	
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_addweburl);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		app = App.getDBcApplication();
		middle_tv.setText("附加信息");
		url = getIntent().getStringExtra("url");
		weburl_et.setText(url);
		weburl_et.setSelection(url.length());
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
			if(!"".equals(weburl_et.getEditableText().toString().trim())){
				Intent intent = new Intent();
				intent.putExtra("url", "http://"+weburl_et.getEditableText().toString());
				setResult(Activity.RESULT_OK, intent);
				this.finish();
			}else {
				Toast.makeText(context, "附加信息不能为空！", Toast.LENGTH_SHORT).show();
				return; 
			}
			break;
		default:
			break;
		}
	}

}
