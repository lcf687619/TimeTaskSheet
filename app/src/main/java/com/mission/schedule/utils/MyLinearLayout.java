package com.mission.schedule.utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	private boolean isMove=false;
	private boolean isCutOff=true;
	private float sx,sy;
	private Handler handler;
	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setHandler(Handler h){
		handler=h;
	}
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	if(isCutOff){
			if(ev.getAction()==MotionEvent.ACTION_DOWN){
				sx=ev.getX();
				sy=ev.getY();
				return true;
			}
			return true;
    	}else{
			if(MotionEvent.ACTION_UP==ev.getAction()){
			   isCutOff=true;
			}
			return false;
		}
    }
   @Override
   public boolean onTouchEvent(MotionEvent ev) {
    	switch(ev.getAction()){
    	    /*case MotionEvent.ACTION_MOVE:
    		   isMove=true;
    	     break;*/
    	   case MotionEvent.ACTION_UP:
    		   float tx=ev.getX();
			   float ty=ev.getY();
			   float rx=tx-sx;
			   float ry=ty-sy;
			   if(Math.abs(rx)>Math.abs(ry)){
				   if(Math.abs(rx)>20){
					   isMove=true;
				   }
			   }else{
				   if(Math.abs(ry)>20){
					   isMove=true;
				   }
			   }
    		   if(isMove){
    			   isMove=false;
    			   if(Math.abs(rx)>Math.abs(ry)){
    				      if(rx>0){
    	    				  handler.sendEmptyMessage(0);
    	    			  }else{
    	    				  handler.sendEmptyMessage(1);
    	    			  }
    			   }else{
    				     if(ry>0){
    	    				  handler.sendEmptyMessage(2);
    	    			  }else{
    	    				  handler.sendEmptyMessage(3);
    	    			  }
    			   }
    	       }else{
    			   isCutOff=false;
    			   ev.setAction(MotionEvent.ACTION_DOWN);
    			   dispatchTouchEvent(ev);
    			   ev.setAction(MotionEvent.ACTION_UP);
     			   dispatchTouchEvent(ev);
    			   return true;
    		   }
    	   break;
    	   default:
    		   break;
    	}
    	 return true;
    }
}
