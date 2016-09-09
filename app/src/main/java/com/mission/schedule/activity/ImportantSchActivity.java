package com.mission.schedule.activity;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImportantSchActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.headtitle_tv)
	private TextView headtitle_tv;
	@ViewResId(id = R.id.all_cb)
	private CheckBox all_cb;
	@ViewResId(id = R.id.noend_cb)
	private CheckBox noend_cb;
	@ViewResId(id = R.id.left_rl)
	private RelativeLayout left_rl;
	@ViewResId(id = R.id.all_tv)
	private TextView all_tv;
	@ViewResId(id = R.id.right_rl)
	private RelativeLayout right_rl;
	@ViewResId(id = R.id.noend_tv)
	private TextView noend_tv;
	
	
	private Fragment[] mfragments;

	private int which;
	
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		all_cb.setOnClickListener(this);
		noend_cb.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_importantsch);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		headtitle_tv.setText("重要");
		init();
		setFragmentIndicator(1);
	}
	@SuppressLint("NewApi") private void init() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		mfragments = new Fragment[2];
		mfragments[0] = fragmentManager.findFragmentById(R.id.all_fragment);
		mfragments[1] = fragmentManager.findFragmentById(R.id.noend_fragment);

		if (which == 0) {
//			left_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_left));
//			right_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_right));
			all_tv.setTextColor(getResources().getColor(R.color.endbackground));
			noend_tv.setTextColor(getResources().getColor(R.color.important_text_color));
		} else if (which == 1) {
//			left_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_left1));
//			right_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_right1));
			all_tv.setTextColor(getResources().getColor(R.color.important_text_color));
			noend_tv.setTextColor(getResources().getColor(R.color.endbackground));
		}
	}
	@SuppressLint("NewApi") private void setFragmentIndicator(int whichIsDefault) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		which = whichIsDefault;
		if (whichIsDefault == 0) {
//			left_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_left));
//			right_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_right));
			all_tv.setTextColor(getResources().getColor(R.color.endbackground));
			noend_tv.setTextColor(getResources().getColor(R.color.important_text_color));
		} else if (whichIsDefault == 1) {
//			left_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_left1));
//			right_rl.setBackground(getResources().getDrawable(R.drawable.bg_btn_right1));
			all_tv.setTextColor(getResources().getColor(R.color.important_text_color));
			noend_tv.setTextColor(getResources().getColor(R.color.endbackground));
		}
		fragmentManager.beginTransaction().hide(mfragments[0]).hide(mfragments[1]).show(mfragments[whichIsDefault]).commit();

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

		case R.id.all_cb://阳历
			all_cb.setChecked(true);
			noend_cb.setChecked(false);
			setFragmentIndicator(0);
			break;

		case R.id.noend_cb://阴历
			all_cb.setChecked(false);
			noend_cb.setChecked(true);
			setFragmentIndicator(1);
			break;
		}
	}

}
