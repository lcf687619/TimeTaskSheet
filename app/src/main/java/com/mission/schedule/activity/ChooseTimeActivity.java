package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.view.CycleWheelView;
import com.mission.schedule.view.CycleWheelView.CycleWheelViewException;
import com.mission.schedule.view.CycleWheelView.WheelItemSelectedListener;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChooseTimeActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.timeset_back)
	private LinearLayout timeset_back;
	@ViewResId(id = R.id.timeset_upload)
	private TextView timeset_upload;
	// @ViewResId(id = R.id.hour_pv)
	// PickerView hour_pv;
	// @ViewResId(id = R.id.minute_pv)
	// PickerView minute_pv;
	@ViewResId(id = R.id.hour_cy)
	private CycleWheelView hour_cy;
	@ViewResId(id = R.id.min_cy)
	private CycleWheelView min_cy;

	private int source; // 0 NewBuildActivity 1 NewRepeatBuildActivity
	private String timeSet = "";
	private int beforeTime;

	Context context;
	String hour;
	String minute;

	@Override
	protected void setListener() {
		timeset_back.setOnClickListener(this);
		timeset_upload.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_choosetime);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		source = getIntent().getIntExtra("source", 0);
		timeSet = getIntent().getStringExtra("timeSet");
		beforeTime = getIntent().getIntExtra("beforeTime", 5);
		initnewdata();
	}

	private void initnewdata() {
		int curHours = Integer.parseInt(timeSet.split(":")[0]);
		int curMinutes = Integer.parseInt(timeSet.split(":")[1]);
		int currentHoursIndex = 0;
		int currentMinutesIndex = 0;
		hour = curHours + "";
		minute = curMinutes + "";
		List<String> hours = new ArrayList<String>();
		List<String> minutes = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			hours.add(i < 10 ? "0" + i : "" + i);

		}
		for (int i = 0; i < 60; i++) {
			minutes.add(i < 10 ? "0" + i : "" + i);
		}
		for (int i = 0; i < hours.size(); i++) {
			if (curHours == Integer.parseInt(hours.get(i))) {
				currentHoursIndex = i;
			}
		}
		for (int i = 0; i < minutes.size(); i++) {
			if (curMinutes == Integer.parseInt(minutes.get(i))) {
				currentMinutesIndex = i;
			}
		}
		hour_cy.setLabels(hours);
		try {
			hour_cy.setWheelSize(3);
		} catch (CycleWheelViewException e) {
			e.printStackTrace();
		}
		hour_cy.setCycleEnable(true);
		hour_cy.setSelection(currentHoursIndex);
		hour_cy.setAlphaGradual(0.6f);
		hour_cy.setDivider(getResources().getColor(R.color.gongkai_txt), 1);
		hour_cy.setSolid(Color.WHITE, Color.WHITE);
		hour_cy.setLabelColor(getResources().getColor(R.color.gongkai_txt));
		hour_cy.setLabelSelectColor(Color.BLACK);
		hour_cy.setOnWheelItemSelectedListener(new WheelItemSelectedListener() {
			@Override
			public void onItemSelected(int position, String label) {
				hour = label;
			}
		});
		
		min_cy.setLabels(minutes);
		try {
			min_cy.setWheelSize(3);
		} catch (CycleWheelViewException e) {
			e.printStackTrace();
		}
		min_cy.setCycleEnable(true);
		min_cy.setSelection(currentMinutesIndex);
		min_cy.setAlphaGradual(0.6f);
		min_cy.setDivider(getResources().getColor(R.color.gongkai_txt), 1);
		min_cy.setSolid(Color.WHITE, Color.WHITE);
		min_cy.setLabelColor(getResources().getColor(R.color.gongkai_txt));
		min_cy.setLabelSelectColor(Color.BLACK);
		min_cy.setOnWheelItemSelectedListener(new WheelItemSelectedListener() {
			@Override
			public void onItemSelected(int position, String label) {
				minute = label;
			}
		});
	}

	// private void initdata(){
	// List<String> hours = new ArrayList<String>();
	// List<String> minutes = new ArrayList<String>();
	// for (int i = 0; i < 24; i++)
	// {
	// hours.add(i < 10 ? "0" + i : "" + i);
	//
	// }
	// for (int i = 0; i < 60; i++)
	// {
	// minutes.add(i < 10 ? "0" + i : "" + i);
	// }
	// hour_pv.setData(hours);
	// hour_pv.setOnSelectListener(new onSelectListener()
	// {
	//
	// @Override
	// public void onSelect(String text)
	// {
	// hour = text;
	// }
	// });
	// minute_pv.setData(minutes);
	// minute_pv.setOnSelectListener(new onSelectListener()
	// {
	//
	// @Override
	// public void onSelect(String text)
	// {
	// minute = text;
	// }
	// });
	// int curHours = Integer.parseInt(timeSet.split(":")[0]);
	// int curMinutes = Integer.parseInt(timeSet.split(":")[1]);
	// hour = curHours+"";
	// minute = curMinutes+"";
	// for(int i=0;i<hours.size();i++){
	// if(curHours==Integer.parseInt(hours.get(i))){
	// hour_pv.setSelected(i);
	// }
	// }
	// for(int i=0;i<minutes.size();i++){
	// if(curMinutes==Integer.parseInt(minutes.get(i))){
	// minute_pv.setSelected(i);
	// }
	// }
	//
	// }
	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.timeset_back:
			this.finish();

			break;

		case R.id.timeset_upload:
			timeSet = hour + ":" + minute;
			Intent intent = null;
			if (source == 0) {// AddEverydayDetailTaskActivity
				intent = new Intent(this, AddEverydayDetailTaskActivity.class);
			} else if (source == 1) {// NewRepeatBuildActivity
				// intent = new Intent(this, NewRepeatBuildActivity.class);
			}
			intent.putExtra("timeSet", timeSet);
			intent.putExtra("beforeTime", beforeTime);
			setResult(Activity.RESULT_OK, intent);
			this.finish();

			break;
		}
	}
}
