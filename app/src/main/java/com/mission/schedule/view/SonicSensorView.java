package com.mission.schedule.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class SonicSensorView extends View{

	/**画一条直线*/
	private Paint mLinePaint;
	/**直线高度*/
	private int mLineHeight=1;
	/**绘直线的颜色*/
	private int mLineColor=0xFFD3D4D7;

	/**贝塞尔曲线的起点*/
	private PointF mStartPointF;
	/**贝塞尔曲线的终点*/
	private PointF mStopPointF;
	/**贝塞尔曲线的控制点*/
	private PointF mControlPointF;

	/**画一条贝塞尔曲线*/
	private Paint mBezierPaint;
	/**贝塞尔曲高度*/
	private int mBezierHeight=1;
	/**贝塞尔曲的颜色*/
	private int mBezierColor=0xFFD3D4D7;

	private Path mBeizerPath;
	
	private int voiceLevel=20;
	private int maxLevel=20;

	public SonicSensorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

//		setBackgroundColor(0x88173FF4);

		mLinePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(mLineColor);
		mLinePaint.setStrokeWidth(mLineHeight);
		mLinePaint.setStyle(Paint.Style.STROKE);

		mBezierPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		mBezierPaint.setAntiAlias(true);
		mBezierPaint.setColor(mBezierColor);
		mBezierPaint.setStrokeWidth(mBezierHeight);
		mBezierPaint.setStyle(Paint.Style.STROKE);

		mBeizerPath=new Path();
	}

	public SonicSensorView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SonicSensorView(Context context) {
		this(context,null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		onDrawLine(canvas);
		if(getVoiceLevel()>=1){
			onDrawBezier(canvas);
		}

	}

	private void onDrawBezier(Canvas canvas) {
		canvas.drawPath(getBezierPath(), mBezierPaint);
		mBeizerPath.reset(); 
	}

	private Path getBezierPath() {
		
		mBeizerPath.moveTo(getPaddingLeft(), getMeasuredHeight()/2);

		int unitWith=(getMeasuredWidth()-getPaddingLeft()-getPaddingRight())/3;
		mBeizerPath.cubicTo(unitWith+getPaddingLeft(),(getMeasuredHeight()-getPaddingBottom()-getPaddingTop())*voiceLevel/maxLevel+getPaddingTop(),unitWith*2+getPaddingLeft(),getPaddingTop()+(getMeasuredHeight()-getPaddingBottom()-getPaddingTop())*(maxLevel-voiceLevel)/maxLevel,getMeasuredWidth()-getPaddingRight(), getMeasuredHeight()/2);
		
		mBeizerPath.lineTo(getMeasuredWidth()-getPaddingRight(), getMeasuredHeight()/2);  
		return mBeizerPath;
	}

	private void onDrawLine(Canvas canvas) {

		canvas.drawLine(getPaddingLeft(), getMeasuredHeight()/2, getMeasuredWidth()-getPaddingRight(), getMeasuredHeight()/2, mLinePaint);
	}

	public Paint getLinePaint() {
		return mLinePaint;
	}

	public void setLinePaint(Paint mLinePaint) {
		this.mLinePaint = mLinePaint;
	}

	public int getLineHeight() {
		return mLineHeight;
	}

	public void setLineHeight(int mLineHeight) {
		this.mLineHeight = mLineHeight;
	}

	public int getLineColor() {
		return mLineColor;
	}

	public void setLineColor(int mLineColor) {
		this.mLineColor = mLineColor;
	}

	public PointF getStartPointF() {
		return mStartPointF;
	}

	public void setStartPointF(PointF mStartPointF) {
		this.mStartPointF = mStartPointF;
	}

	public PointF getStopPointF() {
		return mStopPointF;
	}

	public void setStopPointF(PointF mStopPointF) {
		this.mStopPointF = mStopPointF;
	}

	public PointF getControlPointF() {
		return mControlPointF;
	}

	public void setControlPointF(PointF mControlPointF) {
		this.mControlPointF = mControlPointF;
	}

	public Paint getBezierPaint() {
		return mBezierPaint;
	}

	public void setBezierPaint(Paint mBezierPaint) {
		this.mBezierPaint = mBezierPaint;
	}

	public int getBezierHeight() {
		return mBezierHeight;
	}

	public void setBezierHeight(int mBezierHeight) {
		this.mBezierHeight = mBezierHeight;
	}

	public int getBezierColor() {
		return mBezierColor;
	}

	public void setBezierColor(int mBezierColor) {
		this.mBezierColor = mBezierColor;
	}

	public int getVoiceLevel() {
		return voiceLevel>maxLevel?maxLevel:voiceLevel;
	}

	public void setVoiceLevel(int voiceLevel) {
		this.voiceLevel = voiceLevel<0?0:voiceLevel;
		postInvalidate();
	}
	
	/***
	 * 解析分贝大小转换为等级（0-20）
	 * @param voiceSize
	 * @return
	 */
	public int formatVoiceSize(int voiceSize){
		
		int level=0;
		if(voiceSize<=0){
			level=0;
		}else if(voiceSize<=10){
			level=1;
		}else if(voiceSize<=20){
			level=2;
		}else if(voiceSize<=30){
			level=3;
		}else if(voiceSize<=40){
			level=4;
		}else if(voiceSize<=50){
			level=5;
		}else if(voiceSize<=60){
			level=6;
		}else if(voiceSize<=70){
			level=7;
		}else if(voiceSize<=80){
			level=8;
		}else if(voiceSize<=90){
			level=9;
		}else if(voiceSize<=100){
			level=10;
		}else if(voiceSize<=110){
			level=11;
		}else if(voiceSize<=120){
			level=12;
		}else if(voiceSize<=130){
			level=13;
		}else if(voiceSize<=140){
			level=14;
		}else if(voiceSize<=150){
			level=15;
		}else if(voiceSize<=160){
			level=16;
		}else if(voiceSize<=170){
			level=17;
		}else if(voiceSize<=180){
			level=18;
		}else if(voiceSize<=190){
			level=19;
		}else if(voiceSize>190){
			level=20;
		}
		
		return level;
	}


}
