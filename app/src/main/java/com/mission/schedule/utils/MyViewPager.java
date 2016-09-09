package com.mission.schedule.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager{
	 public MyViewPager(Context context) {  
	        super(context);  
	    }  
	      
	    public MyViewPager(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	    }  
	  
	    @Override  
	    public boolean dispatchTouchEvent(MotionEvent ev) {  
	        getParent().requestDisallowInterceptTouchEvent(true);//这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。    
	        return super.dispatchTouchEvent(ev);  
	    }  

	
	 
}
