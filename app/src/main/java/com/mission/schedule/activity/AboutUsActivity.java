package com.mission.schedule.activity;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutUsActivity extends BaseActivity{

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
				AboutUsActivity.this.finish();
			}
		});
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_aboutus);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		headtitle_tv.setText("关于我们");
	}

	@Override
	protected void setAdapter() {
		
	}

}
