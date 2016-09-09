package com.mission.schedule.activity;

import com.mission.schedule.wheel.OnWheelChangedListener;
import com.mission.schedule.wheel.OnWheelClickedListener;
import com.mission.schedule.wheel.OnWheelScrollListener;
import com.mission.schedule.wheel.WheelView;
import com.mission.schedule.wheel.adapter.NumericWheelAdapter;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetTimeActivity extends BaseActivity implements View.OnClickListener {

	private WheelView hours;
	private WheelView mins;
	private LinearLayout timeset_back;
	private TextView timeset_upload;
	
	private String timeSet = "";
	// Time changed flag
	private boolean timeChanged = false;
	// Time scrolled flag
	private boolean timeScrolled = false;
	
	private int lastIndex;
	private int beforeTime;
	
	private int source; // 0 NewBuildActivity 1 NewRepeatBuildActivity
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settimeactivity);
		source = getIntent().getIntExtra("source", 0);
		timeSet = getIntent().getStringExtra("timeSet");
		beforeTime = getIntent().getIntExtra("beforeTime", 5);
		init();
		
		int curHours = Integer.parseInt(timeSet.split(":")[0]);
		int curMinutes = Integer.parseInt(timeSet.split(":")[1]);

		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);

		// add listeners
		addChangingListener(mins, "min");
		addChangingListener(hours, "hour");

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);

		hours.addClickingListener(click);
		mins.addClickingListener(click);

		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
	}
	
	private void init() {
		hours = (WheelView) findViewById(R.id.hour);
		NumericWheelAdapter hoursAdapter = new NumericWheelAdapter(this, hours, 0, 23);
		hoursAdapter.setTextColor(Color.parseColor("#000000"));
		hours.setViewAdapter(hoursAdapter);
		hours.setCyclic(true);
		
		mins = (WheelView) findViewById(R.id.mins);
		NumericWheelAdapter minsAdapter = new NumericWheelAdapter(this, mins, 0, 59, "%02d", 0);
		minsAdapter.setTextColor(Color.parseColor("#000000"));
		mins.setViewAdapter(minsAdapter);
		mins.setCyclic(true);

		timeset_back = (LinearLayout) findViewById(R.id.timeset_back);
		timeset_back.setOnClickListener(this);
		timeset_upload = (TextView) findViewById(R.id.timeset_upload);
		timeset_upload.setOnClickListener(this);
	}
	
	private OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (!timeScrolled) {
				timeChanged = true;
				timeChanged = false;
				timeSet = (hours.getCurrentItem() >= 10 ? ""
						+ hours.getCurrentItem() : "0" + hours.getCurrentItem())
						+ ":"
						+ (mins.getCurrentItem() >= 10 ? ""
								+ mins.getCurrentItem() : "0"
								+ mins.getCurrentItem());
			}
		}
	};

	private OnWheelClickedListener click = new OnWheelClickedListener() {
		public void onItemClicked(WheelView wheel, int itemIndex) {
			wheel.setCurrentItem(itemIndex, true);
		}
	};

	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			timeScrolled = true;
		}

		public void onScrollingFinished(WheelView wheel) {
			timeScrolled = false;
			timeChanged = true;
			timeSet = (hours.getCurrentItem() >= 10 ? ""
					+ hours.getCurrentItem() : "0" + hours.getCurrentItem())
					+ ":"
					+ (mins.getCurrentItem() >= 10 ? ""
							+ mins.getCurrentItem() : "0"
							+ mins.getCurrentItem());
			timeChanged = false;
		}
	};

	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.timeset_back:
			this.finish();
			
			break;

		case R.id.timeset_upload:
			Intent intent = null;
			if(source == 0){// AddEverydayDetailTaskActivity
				intent = new Intent(this, AddEverydayDetailTaskActivity.class);
			}else if(source == 1){// NewRepeatBuildActivity
//				intent = new Intent(this, NewRepeatBuildActivity.class);
			}
			intent.putExtra("timeSet", timeSet);
			intent.putExtra("beforeTime", beforeTime);
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			
			break;
		}
	}


	@Override
	protected void setListener() {
	}

	@Override
	protected void setContentView() {
	}

	@Override
	protected void init(Bundle savedInstanceState) {
	}

	@Override
	protected void setAdapter() {
	}

}
