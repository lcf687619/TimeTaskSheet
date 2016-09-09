package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Date;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.fragment.YangLiCalendarFragment;
import com.mission.schedule.fragment.YinLiCalendarFragment;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.R;

import de.greenrobot.event.EventBus;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DateSetActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.dateset_back_linear)
	private LinearLayout dateset_back_linear;
	@ViewResId(id = R.id.dateset_cb_solar)
	private CheckBox dateset_cb_solar;
	@ViewResId(id = R.id.dateset_cb_lunar)
	private CheckBox dateset_cb_lunar;
	@ViewResId(id = R.id.sure_ll)
	private LinearLayout sure_ll;
	@ViewResId(id = R.id.left_rl)
	private RelativeLayout left_rl;
	@ViewResId(id = R.id.yangli_tv)
	private TextView yangli_tv;
	@ViewResId(id = R.id.right_rl)
	private RelativeLayout right_rl;
	@ViewResId(id = R.id.nongli_tv)
	private TextView nongli_tv;

	private Fragment[] mfragments;

	private int which;
	private int monthStrT;
	private String dayStr;

	@Override
	protected void setListener() {
		dateset_back_linear.setOnClickListener(this);
		dateset_cb_solar.setOnClickListener(this);
		dateset_cb_lunar.setOnClickListener(this);
		sure_ll.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_dateset);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		monthStrT = getIntent().getIntExtra("month", 0);
		dayStr = getIntent().getStringExtra("day");
		init();
		setFragmentIndicator(0);
	}

	@SuppressLint("NewApi")
	private void init() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		mfragments = new Fragment[2];
		mfragments[0] = fragmentManager.findFragmentById(R.id.fragment_solar);
		mfragments[1] = fragmentManager.findFragmentById(R.id.fragment_lunar);

		if (which == 0) {
			left_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_left));
			right_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_right));
			yangli_tv.setTextColor(getResources().getColor(R.color.white));
			nongli_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			YangLiCalendarFragment solarCalendarFragment = (YangLiCalendarFragment) fragmentManager
					.findFragmentById(R.id.fragment_solar);
			// solarCalendarFragment.setMonthAndDay(monthStrT,
			// Integer.parseInt(dayStr));
		} else if (which == 1) {
			left_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_left1));
			right_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_right1));
			yangli_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			nongli_tv.setTextColor(getResources().getColor(R.color.white));
			YinLiCalendarFragment lunarFragment = (YinLiCalendarFragment) fragmentManager
					.findFragmentById(R.id.fragment_lunar);
			// lunarFragment.setMonthAndDay(1, 1);
		}
	}

	@SuppressLint("NewApi")
	private void setFragmentIndicator(int whichIsDefault) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		which = whichIsDefault;
		if (whichIsDefault == 0) {
			left_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_left));
			right_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_right));
			yangli_tv.setTextColor(getResources().getColor(R.color.white));
			nongli_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			YangLiCalendarFragment solarCalendarFragment = (YangLiCalendarFragment) fragmentManager
					.findFragmentById(R.id.fragment_solar);
			// solarCalendarFragment.setMonthAndDay(monthStrT,
			// Integer.parseInt(dayStr));
		} else if (whichIsDefault == 1) {
			left_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_left1));
			right_rl.setBackground(getResources().getDrawable(
					R.drawable.bg_btn_right1));
			yangli_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			nongli_tv.setTextColor(getResources().getColor(R.color.white));
			YinLiCalendarFragment lunarFragment = (YinLiCalendarFragment) fragmentManager
					.findFragmentById(R.id.fragment_lunar);
			// lunarFragment.setMonthAndDay(1, 1);
		}
		fragmentManager.beginTransaction().hide(mfragments[0])
				.hide(mfragments[1]).show(mfragments[whichIsDefault]).commit();

	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dateset_back_linear:
			String ymd_date = DateUtil.formatDate(new Date());
			Intent intent = new Intent();
			if (dateset_cb_solar.isChecked()) {
				intent.putExtra("type", "0");// 公立
			} else {
				intent.putExtra("type", "1");// 农历
			}
			intent.putExtra("month", ymd_date.split("-")[1]);
			intent.putExtra("day", ymd_date.split("-")[2]);

			setResult(Activity.RESULT_OK, intent);
			DateSetActivity.this.finish();

			break;

		case R.id.dateset_cb_solar:// 阳历
			dateset_cb_solar.setChecked(true);
			dateset_cb_lunar.setChecked(false);
			if (YinLiCalendarFragment.strlist.size() > 0) {
				alertDeleteDialog(0);
			} else {
				EventBus.getDefault().post(new FristFragment("5"));
				setFragmentIndicator(0);
			}
			break;

		case R.id.dateset_cb_lunar:// 阴历
			dateset_cb_solar.setChecked(false);
			dateset_cb_lunar.setChecked(true);
			if (YangLiCalendarFragment.strlist.size() > 0) {
				alertDeleteDialog(1);
			} else {
				setFragmentIndicator(1);
				EventBus.getDefault().post(new FristFragment("6"));
			}
			break;
		case R.id.sure_ll:
			// Intent intent1 = new Intent(this, AddRepeatActivity.class);
			Intent intent1 = new Intent();
			if (which == 0) {
				intent1.putExtra("type", "0");// 公立
				intent1.putStringArrayListExtra("list",
						(ArrayList<String>) YangLiCalendarFragment.strlist);
				setResult(Activity.RESULT_OK, intent1);
			} else {
				intent1.putExtra("type", "1");// 阴历
				intent1.putStringArrayListExtra("list",
						(ArrayList<String>) YinLiCalendarFragment.strlist);
				setResult(Activity.RESULT_OK, intent1);
			}
			finish();
			break;
		}
	}

	/**
	 * 切换对话
	 */
	private void alertDeleteDialog(final int type) {
		final AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setAttributes(params);// 设置生效
		window.setContentView(R.layout.dialog_alterdelete);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		delete_ok.setText("继续");
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type == 0) {
					YinLiCalendarFragment.strlist.clear();
					setFragmentIndicator(0);
					EventBus.getDefault().post(new FristFragment("5"));
				} else {
					YangLiCalendarFragment.strlist.clear();
					setFragmentIndicator(1);
					EventBus.getDefault().post(new FristFragment("6"));
				}
				builder.cancel();
			}
		});
		TextView delete_canel = (TextView) window
				.findViewById(R.id.delete_canel);
		delete_canel.setText("取消");
		delete_canel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		delete_tv.setText("切换需清空所选日期");

	}
}
