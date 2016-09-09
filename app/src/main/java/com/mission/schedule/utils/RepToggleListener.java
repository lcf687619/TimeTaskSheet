package com.mission.schedule.utils;

import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.R;

import android.content.Context;
import android.view.Gravity;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

/**
 * 状态按钮的监听事件
 * 
 * @author lcf
 * 
 */
public class RepToggleListener implements OnCheckedChangeListener {
	private Context context;
	private ToggleButton toggle;
	private ImageButton toggle_Button;
	private SharedPrefUtil sharedPrefUtil;
	String state;

	public RepToggleListener(Context context, ToggleButton toggle,
			ImageButton toggle_Button) {
		this.context = context;
		this.toggle = toggle;
		this.toggle_Button = toggle_Button;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			state = "0";
		} else {
			state = "1";
		}
		sharedPrefUtil.putString(context, ShareFile.USERFILE,
				ShareFile.REPSELECTSTATE, state);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toggle_Button
				.getLayoutParams();
		if (isChecked) {
			// 调整位置
			params.addRule(RelativeLayout.ALIGN_RIGHT, -1);
			params.addRule(RelativeLayout.ALIGN_LEFT, R.id.toggle_sch);
			toggle_Button.setLayoutParams(params);
			toggle_Button.setImageResource(R.drawable.progress_thumb_selector);
			toggle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			// 播放动画
			TranslateAnimation animation = new TranslateAnimation(
					DisplayUtils.dip2px(context, 40), 0, 0, 0);
			animation.setDuration(200);
			toggle_Button.startAnimation(animation);
		} else {
			// 调整位置
			params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_sch);
			params.addRule(RelativeLayout.ALIGN_LEFT, -1);
			toggle_Button.setLayoutParams(params);
			toggle_Button
					.setImageResource(R.drawable.progress_thumb_off_selector);

			toggle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			// 播放动画
			TranslateAnimation animation = new TranslateAnimation(
					DisplayUtils.dip2px(context, -40), 0, 0, 0);
			animation.setDuration(200);
			toggle_Button.startAnimation(animation);
		}
	}

}