package com.mission.schedule.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ParentScrollView extends ScrollView{

	public ParentScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParentScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParentScrollView(Context context) {
		super(context);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return super.onInterceptTouchEvent(ev);
		return false;
	}
}
