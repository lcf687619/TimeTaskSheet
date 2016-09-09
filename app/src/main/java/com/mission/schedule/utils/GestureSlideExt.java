package com.mission.schedule.utils;

import com.mission.schedule.utils.ScreenUtil.Screen;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
 
public class GestureSlideExt {
  
 public static final int GESTURE_UP = 1;
  
 public static final int GESTURE_RIGHT = 2;
  
 public static final int GESTURE_DOWN = 3;
  
 public static final int GESTURE_LEFT = 4;
 private Context mContext;
 private Screen screen;
 private OnGestureResult onGestureResult;
 
 public GestureDetector Buile() {
  return new GestureDetector(mContext, onGestureListener);
 }
 
 public GestureSlideExt(Context context, OnGestureResult onGestureResult) {
  this.mContext = context;
  this.onGestureResult = onGestureResult;
  screen = ScreenUtil.getScreenPix(context);
 }
 
 public void doResult(int result) {
  if (onGestureResult != null) {
   onGestureResult.onGestureResult(result);
  }
 }
 
 public interface OnGestureResult {
  public void onGestureResult(int direction);
 }
 
 private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
    float velocityY) {
   float x = e2.getX() - e1.getX();
   float y = e2.getY() - e1.getY();
   // 限制必须得划过屏幕的1/4才能算划过
   float x_limit = screen.widthPixels / 4;
   float y_limit = screen.heightPixels / 4;
   float x_abs = Math.abs(x);
   float y_abs = Math.abs(y);
   if (x_abs >= y_abs) {
    // gesture left or right
    if (x > x_limit || x < -x_limit) {
     if (x > 0) {
      // right
      doResult(GESTURE_RIGHT);
     } else if (x <= 0) {
      // left
      doResult(GESTURE_LEFT);
     }
    }
   } else {
    // gesture down or up
    if (y > y_limit || y < -y_limit) {
     if (y > 0) {
      // down
      doResult(GESTURE_DOWN);
     } else if (y <= 0) {
      // up
      doResult(GESTURE_UP);
     }
    }
   }
   Log.e("Tag", "判断结束");
   return true;
  }
 };
}
