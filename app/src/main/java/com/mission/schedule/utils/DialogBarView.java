package com.mission.schedule.utils;

import com.mission.schedule.R;
import com.mission.schedule.activity.HelpActivity;

import android.app.Activity;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DialogBarView implements OnClickListener{
	private View view;
	private Activity activity;
	private TextView tiaozuan,quxiao;
	private DialogCallback callback;
	public DialogBarView(Activity activity,int layout,DialogCallback callback){
		this.activity=activity;
		this.callback = callback;
		view=LayoutInflater.from(activity).inflate(layout, null);
		initView();
	}
	private void initView() {
		quxiao=(TextView)view.findViewById(R.id.new_first_dialog_cakanover);
		tiaozuan=(TextView)view.findViewById(R.id.new_first_dialog_cakan_tiaozhuan);
		quxiao.setOnClickListener(this);
		tiaozuan.setOnClickListener(this);
	}
	public View getView() {
		// TODO Auto-generated method stub
		return view;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==quxiao){
			callback.callbackUpdateDialog(110);
		}else if(v==tiaozuan){
			callback.callbackUpdateDialog(120);		}
	}

}
