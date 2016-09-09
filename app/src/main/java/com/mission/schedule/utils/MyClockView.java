package com.mission.schedule.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mission.schedule.cutimage.FileUtils;

public class MyClockView extends View {

	private Paint paint;
	private Context context;

	public MyClockView(Context context) {
		super(context);
		init(context);
	}

	public MyClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		paint = new Paint();
		paint.setAntiAlias(true); //去掉锯齿
		paint.setStyle(Paint.Style.STROKE); // 加粗
		paint.setColor(Color.parseColor("#51504e"));
		paint.setStrokeWidth(0.5f);  

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int haf = Utils.dipTopx(context, 40) / 2;
		int centerX = getWidth() / 2;
		int centerY = getWidth() / 2 - haf;

		int hourRadius = Utils.dipTopx(context, 90); // 小时的半径
		int minutesRadius = Utils.dipTopx(context, 140); // 分钟的半径

		// 画小时圆
		canvas.drawCircle(centerX, centerY, hourRadius, paint);

		// 画分钟圆
		canvas.drawCircle(centerX, centerY, minutesRadius, paint);

	}

}
