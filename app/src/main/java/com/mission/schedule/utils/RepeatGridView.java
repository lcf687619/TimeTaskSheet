package com.mission.schedule.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class RepeatGridView extends GridView {   
    
    public RepeatGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
  
    public RepeatGridView(Context context) {   
        super(context);   
    }   
  
    public RepeatGridView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);   
    }   
  
//    @Override   
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
//  
//        int expandSpec = MeasureSpec.makeMeasureSpec(   
//                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
//        super.onMeasure(widthMeasureSpec, expandSpec);  
//    }   
}   
