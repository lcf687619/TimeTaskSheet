package com.mission.schedule.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.mission.schedule.R;

public class ImageUtils {

	/**
	 * 从SDCard上读取图片
	 * 
	 * @param pathName
	 * @return
	 */
	public static Bitmap getBitmapFromSDCard(String pathName) {
		return BitmapFactory.decodeFile(pathName);
	}

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / w, (float) height / h);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}
	/**
	 * 
	 * @param sourceImg
	 * @param number 设置透明度 1-100
	 * @return
	 */
	public static Bitmap setAlaph(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
		}
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}

	/**
	 * 获得圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 获得圆形加边框图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundBitmap(Context context, Bitmap bitmap) {
		if (bitmap == null)
			return null;
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_4444);
		Canvas canvas = new Canvas(outBitmap);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPX = bitmap.getWidth() / 2;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		bitmap.recycle();

		Bitmap bitBack = BitmapFactory.decodeResource(context.getResources(),
				R.mipmap.bg_head);

		Bitmap outBitGroup = Bitmap.createBitmap(bitBack.getWidth(),
				bitBack.getHeight(), Config.RGB_565);
		Canvas canvaBack = new Canvas(outBitGroup);
		canvaBack.setDrawFilter(new PaintFlagsDrawFilter(0,
				Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		// canvaBack.drawBitmap(bitBack, 0, 0, null);
		int left = (bitBack.getWidth() - outBitmap.getWidth()) / 2;
		int top = (bitBack.getHeight() - outBitmap.getHeight()) / 2;
		canvaBack.drawBitmap(outBitmap, left, top, null);
		canvaBack.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		canvaBack.restore();// 存储

		outBitmap.recycle();
		bitBack.recycle();

		return outBitGroup;
	}

	/**
	 * 获得带倒影的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getReflectionImageWithOrigin(Bitmap bitmap) {

		// 原始图片和反射图片中间的间距
		final int reflectionGap = 0;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// 反转
		Matrix matrix = new Matrix();

		// 第一个参数为1表示x方向上以原比例为准保持不变，正数表示方向不变。
		// 第二个参数为-1表示y方向上以原比例为准保持不变，负数表示方向取反。
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}

	/**
	 * 添加图片显示渐现动画
	 * 
	 */
	public static void setFadeInBitmap(ImageView imageView, Bitmap bitmap) {
		TransitionDrawable td = new TransitionDrawable(new Drawable[] {
				new ColorDrawable(Color.TRANSPARENT),
				new BitmapDrawable(bitmap) });
		td.setCrossFadeEnabled(true);
		imageView.setImageDrawable(td);
		td.startTransition(500);
	}

	/**
	 * 获得倒影圆角图片
	 */
	public static Bitmap getReflectionWithCorner(Bitmap bitmap, int roundPx) {
		Bitmap bitCorner = getRoundedCornerBitmap(bitmap, roundPx);
		bitmap.recycle();
		Bitmap bitFlectionWithCorner = getReflectionImageWithOrigin(bitCorner);
		bitCorner.recycle();

		return bitFlectionWithCorner;
	}
}
