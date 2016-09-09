package com.mission.schedule.activity;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OpinionBackActivity extends BaseActivity{

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.headtitle_tv)
	private TextView headtitle_tv;
	
	Context context;
	
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OpinionBackActivity.this.finish();
			}
		});
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_opinionback);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		headtitle_tv.setText("意见反馈");
	}

	@Override
	protected void setAdapter() {
		
	}

}
