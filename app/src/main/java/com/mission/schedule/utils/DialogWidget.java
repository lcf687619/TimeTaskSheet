package com.mission.schedule.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public class DialogWidget extends Dialog  {
	Activity activity;
	private View view;
	private boolean isOutSideTouch=false;

	public DialogWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DialogWidget(Activity activity, View view) {
		super(activity);
		this.activity = activity;
		this.view=view;
	}
	public DialogWidget(Activity activity, View view,int theme) {
		super(activity,theme);
		this.activity = activity;
		this.view=view;
	}
	public DialogWidget(Activity activity, View view,int theme,boolean isOutSide) {
		super(activity,theme);
		this.activity = activity;
		this.view=view;
		this.isOutSideTouch=isOutSide;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(view);
		this.setCanceledOnTouchOutside(isOutSideTouch);

		DisplayMetrics dm = new DisplayMetrics();
		// ȡ�ô�������
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		// ���ڵĿ��
		int screenWidth = dm.widthPixels;
		int withd=screenWidth/10*9;
		WindowManager.LayoutParams layoutParams = this.getWindow()
				.getAttributes();
		layoutParams.width = withd;
		layoutParams.height = LayoutParams.WRAP_CONTENT;
		this.getWindow().setAttributes(layoutParams);
		

	}

}
