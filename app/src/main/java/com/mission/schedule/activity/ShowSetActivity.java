package com.mission.schedule.activity;

import com.mission.schedule.switchbutton.SwitchButton;
import com.mission.schedule.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

public class ShowSetActivity extends BaseActivity implements OnClickListener{
	private LinearLayout showset_linear_back;
	private SwitchButton date_cb,alarm_cb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showsetactivity);
		init();
		initData();
	}
	private void init() {
		showset_linear_back=(LinearLayout)findViewById(R.id.showset_linear_back);
		showset_linear_back.setOnClickListener(this);
		date_cb = (SwitchButton) findViewById(R.id.date_cb);
		date_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked){
//					setPrivateXml("yaoyiyao_xml", "checkState", "1");
//				}else
//					setPrivateXml("yaoyiyao_xml", "checkState", "0");
			}
		});
		alarm_cb = (SwitchButton) findViewById(R.id.alarm_cb);
		alarm_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked){
//					setPrivateXml("alarm_xml", "checkState", "1");
//				}else
//					setPrivateXml("alarm_xml", "checkState", "0");
			}
		});
	}
	private void initData() {
//		String checkState= getPrivateXml("yaoyiyao_xml", "checkState", "1");
//		if("1".equals(checkState)){
//			date_cb.setChecked(true);
//		}else
//			date_cb.setChecked(false);
//		String alarm_checkState= getPrivateXml("alarm_xml", "checkState", "1");
//		if("1".equals(alarm_checkState)){
//			alarm_cb.setChecked(true);
//		}else
//			alarm_cb.setChecked(false);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.showset_linear_back:
				this.finish();
				break;
			default:
				break;
		}
	}
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void init(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setAdapter() {
		// TODO Auto-generated method stub
		
	}

}
