package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.utils.DateWidgetDayCell;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DayActivity extends BaseActivity implements OnClickListener{
	@ViewResId(id = R.id.day_back_linear)
	private LinearLayout day_back_linear;
	@ViewResId(id = R.id.rl_save)
	private RelativeLayout rl_save;
	@ViewResId(id = R.id.day_linear_days)
	private LinearLayout day_linear_days;
	
	private String dayS;
	private List<String> mList;
	int type=0;//1AddRepeatActivity  2 AddNewFocusShareRepeatActivity

	@Override
	protected void setListener() {
		day_back_linear.setOnClickListener(this);
		rl_save.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_day);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		dayS = getIntent().getStringExtra("dayS");
		type = getIntent().getIntExtra("type", 0);
		mList = new ArrayList<String>();
		initDay();
		initData();
	}

	@Override
	protected void setAdapter() {
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.day_back_linear:
			this.finish();

			break;
			
		case R.id.rl_save:
			close();
			
			break;
		}
	}
	private void initDay() {
		if(dayS.indexOf(",") != -1){
			String[] days = dayS.split(",");
			for(int i = 0; i < days.length; i++){
				int daySplit = Integer.parseInt(days[i]);
				mList.add(String.valueOf(daySplit));
			}
		}else{
			int daySplit = Integer.parseInt(dayS);
			mList.add(String.valueOf(daySplit));
		}
	}

	private void initData() {
		int Cell_Width = mScreenWidth / 7;
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		for (int i = 1; i <= 5; i++) {
			LinearLayout linearLayout = new LinearLayout(this);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setLayoutParams(params2);
			for (int j = 1 + (i - 1) * 7; j <= 7 + (i - 1) * 7; j++) {
				DateWidgetDayCell dayCell = new DateWidgetDayCell(this, Cell_Width, Cell_Width);
				if(j <= 31){
					if (mList.contains("" + j)) {
						dayCell.setSelected(true);
					} 
					dayCell.setItemClick(mOnDayCellClick);
					dayCell.setData(j, false);
				}
				linearLayout.addView(dayCell);
			}
			day_linear_days.addView(linearLayout, i - 1);
		}
	}

	private void close() {
		if(mList.size() > 0){
			Collections.sort(mList, new Comparator<String>(){

				@Override
				public int compare(String lhs, String rhs) {
					return new Integer(lhs).compareTo(new Integer(rhs));
				}

			});
			String days = "";
			for(String sday : mList){
				days += sday + ",";
			}
			days = days.substring(0, days.lastIndexOf(","));
			if(type==1){
				Intent intent = new Intent(DayActivity.this, AddRepeatActivity.class);
				intent.putExtra("dayS", days);
				setResult(Activity.RESULT_OK, intent);
			}else if(type==2){
				Intent intent = new Intent(DayActivity.this, AddNewFocusShareRepeatActivity.class);
				intent.putExtra("dayS", days);
				setResult(Activity.RESULT_OK, intent);
			}else{
				Intent intent = new Intent();
				intent.putExtra("dayS", days);
				setResult(Activity.RESULT_OK, intent);
			}
			DayActivity.this.finish();
		}else{
			Toast.makeText(this, "请至少选择一天...", Toast.LENGTH_SHORT).show();
		}
	}
	// 点击日历，触发事件
		private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
			public void OnClick(DateWidgetDayCell item) {
					String sDate = item.getsDate();
					if(item.getSelected()){
						mList.remove(sDate);
						item.setSelected(false);
					}else{
						if(mList.size() < 5){
							mList.add(sDate);
							item.setSelected(true);	
						}else{
							Toast.makeText(DayActivity.this, "最多能选择五天...", Toast.LENGTH_SHORT).show();
						}
					}
				}
			};
}
