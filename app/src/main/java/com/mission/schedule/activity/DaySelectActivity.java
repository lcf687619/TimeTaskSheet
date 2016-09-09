package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.adapter.DaySelectAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.utils.LineGridView;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class DaySelectActivity extends BaseActivity implements OnClickListener,OnItemClickListener{
	@ViewResId(id = R.id.day_gv)
	private LineGridView day_gv;
	@ViewResId(id = R.id.day_back_linear)
	private LinearLayout day_back_linear;
	
	private String dayS;
	private List<String> mList;
	int index=0;
	DaySelectAdapter adapter = null;
	Context context;
	int type = 0;//
	
	@Override
	protected void setListener() {
		day_back_linear.setOnClickListener(this);
		day_gv.setOnItemClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_dayselsect);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		dayS = getIntent().getStringExtra("dayS");
		type = getIntent().getIntExtra("type", 0);
		mList = new ArrayList<String>();
		
		for(int i=0;i<=30;i++){
			index++;
			mList.add(index+"");
		}
	}

	@Override
	protected void setAdapter() {
		adapter = new DaySelectAdapter(context, mList,R.layout.adapter_dayselect,mScreenWidth);
		day_gv.setAdapter(adapter);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.day_back_linear:
			this.finish();

			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		dayS = (String) day_gv.getAdapter().getItem(position);
		if(type==2){
			Intent intent = new Intent();
			intent.putExtra("dayS", dayS);
			setResult(Activity.RESULT_OK, intent);
		}else{
			Intent intent = new Intent(DaySelectActivity.this, EditRepeatActivity.class);
			intent.putExtra("dayS", dayS);
			setResult(Activity.RESULT_OK, intent);
		}
		this.finish();
	}
			
}
