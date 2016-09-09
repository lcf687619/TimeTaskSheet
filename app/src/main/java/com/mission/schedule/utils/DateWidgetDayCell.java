package com.mission.schedule.utils;

import com.mission.schedule.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;

/**
 * 日历控件单元格绘制类
 * @Description: 日历控件单元格绘制类
 */

public class DateWidgetDayCell extends View {
	// 字体大小
	private static final int fTextSize = 50;
	
	// 基本元素
	private OnItemClick itemClick = null;
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private String sDate = "";

	private int iDateDay = 0;

	// 布尔变量
	private boolean bSelected = false;
	private boolean bIsActiveMonth = false;
	private boolean bToday = false;
	private boolean bTouchedDown = false;
	private boolean bHoliday = false;
	private boolean hasRecord = false;

	public static int ANIM_ALPHA_DURATION = 100;
	
	private int Calendar_DayBgColor = 0;
	private int unPresentMonth_FontColor = 0;
	private int isPresentMonth_FontColor = 0;
	private int lineColor = 0;
	private int isToday_BgColor = 0;
	private int special_Reminder = 0;

	public interface OnItemClick {
		public void OnClick(DateWidgetDayCell item);
	}

	// 构造函数
	public DateWidgetDayCell(Context context, int iWidth, int iHeight) {
		super(context);
		setFocusable(true);
		setLayoutParams(new LayoutParams(iWidth, iHeight));
		init();
	}
	
	public DateWidgetDayCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DateWidgetDayCell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		Calendar_DayBgColor = this.getResources().getColor(R.color.Calendar_DayBgColor);
		unPresentMonth_FontColor = this.getResources().getColor(R.color.unPresentMonth_FontColor);
		isPresentMonth_FontColor = this.getResources().getColor(R.color.isPresentMonth_FontColor);
		isToday_BgColor = this.getResources().getColor(R.color.isToday_BgColor);
		special_Reminder = this.getResources().getColor(R.color.specialReminder);
		lineColor = this.getResources().getColor(R.color.lineColor);
	}
	
	// 设置变量值
	public void setData(int iDay, boolean hasRecord) {
		iDateDay = iDay;
		this.sDate = Integer.toString(iDateDay);
		this.hasRecord = hasRecord;
	}

	// 重载绘制方法
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(lineColor);
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(1, 1);

		final boolean bFocused = IsViewFocused();

		drawDayView(canvas, bFocused);
		drawDayNumber(canvas);
	}

	public boolean IsViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}

	// 绘制日历方格
	private void drawDayView(Canvas canvas, boolean bFocused) {

		if (bSelected || bFocused) {
			LinearGradient lGradBkg = null;

			if (bFocused) {
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						Color.parseColor("#f0e6c8"), Color.parseColor("#f0e6c8"), Shader.TileMode.CLAMP);
			}

			if (bSelected) {
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						Color.parseColor("#eddeb3"), Color.parseColor("#eddeb3"), Shader.TileMode.CLAMP);
			}

			if (lGradBkg != null) {
				pt.setShader(lGradBkg);
				canvas.drawRect(rect, pt);
			}

			pt.setShader(null);

		} else {
			pt.setColor(getColorBkg(bHoliday, bToday));
			canvas.drawRect(rect, pt);
		}

		if (hasRecord) {
			CreateReminder(canvas, special_Reminder);
		}
		// else if (!hasRecord && !bToday && !bSelected) {
		// CreateReminder(canvas, Calendar_TestActivity.Calendar_DayBgColor);
		// }
	}

	// 绘制日历中的数字
	public void drawDayNumber(Canvas canvas) {
		// draw day number
		pt.setTypeface(null);
		pt.setAntiAlias(true);
		pt.setShader(null);
		//pt.setFakeBoldText(true);
		pt.setTextSize(fTextSize);
		pt.setColor(isPresentMonth_FontColor);
		pt.setUnderlineText(false);
		
		if (!bIsActiveMonth)
			pt.setColor(unPresentMonth_FontColor);

		if (bToday)
			pt.setUnderlineText(true);
		final int iPosX = (int) rect.left + ((int) rect.width() >> 1) - ((int) pt.measureText(sDate) >> 1);
		final int iPosY = (int) (this.getHeight() - (this.getHeight() - getTextHeight()) / 2 - pt.getFontMetrics().bottom);
		canvas.drawText(sDate, iPosX, iPosY, pt);
		pt.setUnderlineText(false);
	}

	// 得到字体高度
	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}

	// 根据条件返回不同颜色值
	public int getColorBkg(boolean bHoliday, boolean bToday) {
		if (bToday)
			return isToday_BgColor;
		// if (bHoliday) //如需周末有特殊背景色，可去掉注释
		// return Calendar_TestActivity.isHoliday_BgColor;
		return Calendar_DayBgColor;
	}

	// 设置是否被选中
	@Override
	public void setSelected(boolean bEnable) {
		if (this.bSelected != bEnable) {
			this.bSelected = bEnable;
			this.invalidate();
		}
	}
	
	public boolean getSelected(){
		return bSelected;
	}

	public String getsDate() {
		return sDate;
	}
	
	public void setItemClick(OnItemClick itemClick) {
		this.itemClick = itemClick;
	}

	public void doItemClick() {
		if (itemClick != null)
			itemClick.OnClick(this);
	}

	// 点击事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean bHandled = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bHandled = true;
			bTouchedDown = true;
			invalidate();
			startAlphaAnimIn(DateWidgetDayCell.this);
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
			doItemClick();
		}
		
		return bHandled;
	}

	// 点击事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			doItemClick();
		}
		return bResult;
	}

	// 不透明度渐变
	public static void startAlphaAnimIn(View view) {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
	}

	public void CreateReminder(Canvas canvas, int Color) {
		pt.setStyle(Paint.Style.FILL_AND_STROKE);
		pt.setColor(Color);
		Path path = new Path();
		path.moveTo(rect.right - rect.width() / 4, rect.top);
		path.lineTo(rect.right, rect.top);
		path.lineTo(rect.right, rect.top + rect.width() / 4);
		path.lineTo(rect.right - rect.width() / 4, rect.top);
		path.close();
		canvas.drawPath(path, pt);
	}
}