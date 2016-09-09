package com.mission.schedule.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.SharedPrefUtil;

public class LogoutActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.btn_resgin)
	private Button btn_resgin;
	@ViewResId(id = R.id.exit)
	private TextView exit;
	@ViewResId(id = R.id.cancle)
	private TextView cancle;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		btn_resgin.setOnClickListener(this);
		exit.setOnClickListener(this);
		cancle.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_logout);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_resgin:
				startActivity(new Intent(context, NewResiginActivity.class));
				break;
			case R.id.exit:
				WriteAlarmClock.clearAlarmClock(getApplicationContext());
				if (!"".equals(sharedPrefUtil.getString(context,
						ShareFile.USERFILE, ShareFile.TELEPHONE, ""))) {
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.TELEPHONE, "");
				} else {
					sharedPrefUtil.putString(context, ShareFile.USERFILE,
							ShareFile.USEREMAIL, "");
				}
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.USERID, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.USERNAME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.USERPHOTOPATH, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.USERSTATE, "0");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.FIRSTLOGIN, "0");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.U_ACC_NO, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.MAXFOCUSID, "0");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.OUTWEEKFAG, "0");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.UPDATESETTIME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.DOWNSCHTIME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.DOWNREPTIME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.RINGSTATE, "0");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.ISYOUKE, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.DOWNTAGDATE, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.OPENSTYLESTATE, "2");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.FRIENDDOWNSCHTIME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.FRIENDDOWNRepTIME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.FRIENDDOWNOldTIME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.PUSH_ALIAS, "0");
				sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.REFRESHFRIEND, "0");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.FRIENDUPDATETIME, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.FIRSTDOWNFOCUSSCH, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.SHOUCANGDATA, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.DOWNNEWFOCUSSHARESCHDATE, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.DOWNNEWFOCUSSHAREREPDATE, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE,ShareFile.KuaiJieSouSuo, "");
				sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.SHAREDATA, "");
				startActivity(new Intent(context, NewTelephoneLoginActivity.class));
				activityManager.doAllActivityFinish();
				break;
			case R.id.cancle:
				this.finish();
				break;
			default:
				break;
		}
	}

}
