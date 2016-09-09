package com.mission.schedule.activity;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EndSoundActivity extends BaseActivity {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.toggle_sound)
	private ToggleButton toggle_sound;
	@ViewResId(id = R.id.toggle_wake)
	private ToggleButton toggle_wake;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	String soundstate;
	String wakestate;
	boolean soundFlag = false;
	boolean wakeFlag = false;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_endsound);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		top_ll_right.setVisibility(View.GONE);
		middle_tv.setText("结束音效");
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		soundstate = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ENDSOUNDSTATE, "0");
		wakestate = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.ENDWAKESTATE, "0");
		if ("0".equals(soundstate)) {
			soundFlag = true;
		} else {
			soundFlag = false;
		}
		if ("0".equals(wakestate)) {
			wakeFlag = true;
		} else {
			wakeFlag = false;
		}

		toggle_sound.setChecked(soundFlag);
		toggle_wake.setChecked(wakeFlag);
		toggle_sound.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					//选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.ENDSOUNDSTATE, "0");
				}else{
					//未选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.ENDSOUNDSTATE, "1");
				}
			}
		});
		toggle_wake.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					//选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.ENDWAKESTATE, "0");
				}else{
					//未选中
					sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.ENDWAKESTATE, "1");
				}
			}
		});
	}

	@Override
	protected void setAdapter() {

	}

}
