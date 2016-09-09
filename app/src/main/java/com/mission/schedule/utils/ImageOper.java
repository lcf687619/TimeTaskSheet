package com.mission.schedule.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mission.schedule.constants.ShareFile;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.util.Log;

public class ImageOper {
	 private final static String ALBUM_PATH = ShareFile.TEMPORARYFILE;
	/**
	 * 图片圆角
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if (bitmap == null) {
			return null;
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffb69094;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 图片圆角
	 * 
	 * @param bigBackBitmap
	 * @param bigAvatarBitmap
	 * @return
	 */
	public static Bitmap getRoundBitmap(int R, Bitmap bigAvatarBitmap) {
		int x = R;
		int color = 0xff424242;
		Matrix matrix = new Matrix();
		int width = bigAvatarBitmap.getWidth();
		int height = bigAvatarBitmap.getHeight();
		float xScale = (float) x / (float) width;
		float yScale = (float) x / (float) height;
		matrix.postScale(xScale, yScale);
		Bitmap scaleBitmap = Bitmap.createBitmap(bigAvatarBitmap, 0, 0, width,
				height, matrix, true);
		Bitmap output = Bitmap.createBitmap(x, x, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		Rect rect = new Rect(2, 2, x, x);
		RectF rectf = new RectF(rect);
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawRoundRect(rectf, 20, 20, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaleBitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 保存图片
	 * 
	 * @param bmp
	 * @param name
	 */
	public static void saveBitmapToPic(Bitmap bmp, String name) {
		
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists())
			dirFile.mkdir();
		try {
			File myCaptureFile = new File(ALBUM_PATH + name);  
			if(!myCaptureFile.exists()){
				BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
				bmp.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
				bmp = null;
				Log.e("ImageOper", "saveBmp is here " + myCaptureFile.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存缩略图到SD卡（加文字水印）
	 * @param stringPath
	 * @param fileName
	 * @param text
	 */
	public static void saveBitmapToSD(String stringPath,int reqWidth,int reqHeight,String fileName,String text){
		Bitmap bitmap = decodeSampledBitmapFromPathName(stringPath, reqWidth, reqHeight);
		bitmap = doodle(bitmap, text);
		saveBitmapToPic(bitmap, fileName);
		bitmap.recycle();
		bitmap=null;
	}
	
	
	/**
	 * 保存缩略图到SD卡
	 * @param bitmap
	 * @param fileName
	 * @param text
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void saveBitmapToSD(String urlPath,int reqWidth,int reqHeight,String fileName) throws MalformedURLException, IOException{
		Bitmap bitmap = decodeSampledBitmapFromResourceByStream(urlPath, reqWidth, reqHeight);
			saveBitmapToPic(bitmap, fileName);
			bitmap.recycle();
		bitmap=null;
	}
	

	/**
	 * 保存缩略图到SD卡
	 * @param stringPath
	 * @param fileName
	 * @param text
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void saveBitmapToSD1(String stringPath,int reqWidth,int reqHeight,String fileName) throws MalformedURLException, IOException{
		Bitmap bitmap = decodeSampledBitmapFromPathName(stringPath, reqWidth, reqHeight);
			saveBitmapToPic(bitmap, fileName);
//			bitmap.recycle();
		bitmap=null;
	}
	/**
	 * 保存缩略图文件
	 * 
	 * @param bitmap
	 * @param widthe
	 * @param height
	 * @param quality
	 * @param fileName
	 */
	public static void saveThumPic(Bitmap bitmap, int width, int height,
			String fileName) {
		Bitmap newBitmap = createBitmapMatrix(bitmap, width, height);
		File file = new File(fileName);
		File dir = file.getParentFile();
		if (!dir.exists())
			dir.mkdirs();
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);
			newBitmap
					.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			newBitmap = null;
			Log.e("ImageOper", "saveBmp is here " + file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存缩略图文件
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @param quality
	 * @param fileName
	 */
	public static Bitmap saveThumPic(String paramString, int measuredWidth,
			int measuredHeight, String thumFileName) {
		try {
			Log.e("ImageOper", "param -- " + paramString + " thum -- "
					+ thumFileName);
			File file = new File(paramString);
			if (!file.exists() || !file.isFile()) {
				return null;
			}
			BitmapFactory.Options localOptions = new BitmapFactory.Options();
			localOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(paramString, localOptions);
			int i = localOptions.outWidth / measuredWidth;
			int j = localOptions.outHeight / measuredHeight;
			localOptions.inSampleSize = Math.min(i, j);
			localOptions.inJustDecodeBounds = false;
			Bitmap localBitmap2 = BitmapFactory.decodeFile(paramString,
					localOptions);
			int width = localBitmap2.getWidth();
			int height = localBitmap2.getHeight();
			// 创建操作图片用的matrix对象
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) measuredWidth) / width;
			float scaleHeight = ((float) measuredHeight) / height;
			// 缩放图片动作
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(localBitmap2, 0, 0,
					width, height, matrix, true);
			saveBitmapToPic(resizedBitmap, thumFileName);
			return resizedBitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	

	/**
	 * 创建缩略图 正方形（不失真）
	 * 
	 * @param paramString
	 * @param inSampleSize
	 * @return
	 */
	public static Bitmap createBitmapMatrix(String paramString,
			int measuredWidth, int measuredHeight) {
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(paramString, localOptions);

		int rotate = 0;
		if (localOptions.outWidth > localOptions.outHeight) {
			rotate = 90;
			int i = localOptions.outWidth / measuredHeight;
			int j = localOptions.outHeight / measuredWidth;
			localOptions.inSampleSize = Math.min(i, j);
		} else {
			int i = localOptions.outWidth / measuredWidth;
			int j = localOptions.outHeight / measuredHeight;
			localOptions.inSampleSize = Math.min(i, j);
		}
		localOptions.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(paramString, localOptions);
		if (bitmap != null) {
			Bitmap localBitmap2 = cutBmp(bitmap);
			bitmap.recycle();
			int width = localBitmap2.getWidth();
			int height = localBitmap2.getHeight();
			// 创建操作图片用的matrix对象
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) measuredWidth) / width;
			float scaleHeight = ((float) measuredHeight) / height;
			// 缩放图片动作
			// matrix.postScale(scaleWidth, scaleHeight);
			matrix.setRotate(rotate);
			matrix.setScale(scaleWidth, scaleHeight);
			// 创建新的图片
			Bitmap resizedBitmap = Bitmap.createBitmap(localBitmap2, 0, 0,
					width, height, matrix, true);
			localBitmap2 = null;
			return resizedBitmap;
		}
		return null;
	}

	/**
	 * 创建缩略图
	 * 
	 * @param bitmap
	 * @param measuredWidth
	 * @param measuredHeight
	 * @return
	 */
	public static Bitmap createBitmapMatrix(Bitmap bitmap, int measuredWidth,
			int measuredHeight) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) measuredWidth) / width;
		float scaleHeight = ((float) measuredHeight) / height;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作

		matrix.setScale(scaleWidth, scaleHeight);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap = null;
		return resizedBitmap;
	}

	/**
	 * 
	 * @param res
	 *            资源
	 * @param resId
	 *            图片ID
	 * @param reqWidth
	 *            设置的宽度缩放
	 * @param reqHeight
	 *            设置的高度缩放
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		// 给定的BitmapFactory设置解码的参数
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 取得缩放比例
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);

	}

	/**
	 * 
	 * @param res
	 *            资源
	 * @param resId
	 *            图片ID
	 * @param reqWidth
	 *            设置的宽度缩放
	 * @param reqHeight
	 *            设置的高度缩放
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromPathName(String pathName,
			int reqWidth, int reqHeight) {
		// 给定的BitmapFactory设置解码的参数
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(pathName, options);
		// 取得缩放比例
		options.inSampleSize = calculateInSampleSize1(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Log.d("options.inSampleSize:", "" + options.inSampleSize);
		return BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * 将图片修改成正方形
	 * 
	 * @param bmp
	 *            图片
	 * @return
	 */
	public static Bitmap cutBmp(Bitmap bitmap) {
		Bitmap result;
		int w = bitmap.getWidth();// 输入长方形宽
		int h = bitmap.getHeight();// 输入长方形高
		int nw;// 输出正方形宽
		if (w > h) {
			// 宽大于高
			nw = h;
			result = Bitmap.createBitmap(bitmap, (w - nw) / 2, 0, nw, nw);
		} else {
			// 高大于宽
			nw = w;
			result = Bitmap.createBitmap(bitmap, 0, (h - nw) / 2, nw, nw);
		}
		bitmap = null;
		return result;
	}

	/**
	 * 将图片修改成正方形
	 * 
	 * @param bitmap
	 *            图片
	 * @return
	 */
	public static Bitmap ImageCrop(Bitmap bitmap) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();
		int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
		int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
		int retY = w > h ? 0 : (h - w) / 2;

		// 下面这句是关键
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null,
				false);
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
		return bitmap2;
	}

	/**
	 * 创建缩略图
	 * 
	 * @param is
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static Bitmap decodeSampledBitmapFromResourceByStream(String url,
			int reqWidth, int reqHeight) throws MalformedURLException,
			IOException {
		// 给定的BitmapFactory设置解码的参数
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Rect outPadding = new Rect(0, 0, 0, 0);
		BitmapFactory.decodeStream(new URL(url).openStream(), outPadding,
				options);
		// 取得缩放比例
		options.inSampleSize = calculateInSampleSize1(options, reqWidth,
				reqHeight);
		Log.d("options.inSampleSize==", options.inSampleSize + "");
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(new URL(url).openStream(),
				outPadding, options);

	}

	/**
	 * 设定缩放的比例 1
	 * 
	 * @param options
	 * @param reqWidth
	 *            设置的宽度缩放
	 * @param reqHeight
	 *            设置的高度缩放
	 * @return
	 */

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 获取原图的宽高
		final int height = options.outHeight;
		final int width = options.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	/**
	 * 设定缩放的比例 2
	 * 
	 * @param options
	 * @param reqWidth
	 *            设置的宽度缩放
	 * @param reqHeight
	 *            设置的高度缩放
	 * @return
	 */
	private static int calculateInSampleSize1(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 获取原图的宽高
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = Math.round(height) / reqHeight;
			final int halfWidth = Math.round(width) / reqWidth;
			inSampleSize = halfHeight < halfWidth ? halfHeight : halfWidth;
		}
		return inSampleSize;
	}

	/**
	 * 获取圆头像
	 * 
	 * @return
	 */
	public static Bitmap getCircleBitmap(int R, Bitmap mHeadBitmap) {
		int x = R;
		int color = 0xff424242;

		Matrix matrix = new Matrix();
		int width = mHeadBitmap.getWidth();
		int height = mHeadBitmap.getHeight();
		float xScale = (float) x / (float) width;
		float yScale = (float) x / (float) height;
		matrix.postScale(xScale, yScale);
		Bitmap scaleBitmap = Bitmap.createBitmap(mHeadBitmap, 0, 0, width,
				height, matrix, true);
		Bitmap output = Bitmap.createBitmap(x, x, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();

		Rect rect = new Rect(0, 0, x, x);
		paint.setAntiAlias(true);
		paint.setColor(color);

		canvas.drawCircle(x / 2, x / 2, x / 2 - 5, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaleBitmap, rect, rect, paint);
		mHeadBitmap = null;
		return output;
	}

	/**
	 * 图片旋转
	 * 
	 * @param bmp
	 * @param degree
	 * @return
	 */
	public static Bitmap postRotateBitamp(Bitmap bmp, float degree) {
		// 获得Bitmap的高和宽
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		// 产生resize后的Bitmap对象
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		bmp = null;
		return resizeBmp;
	}

	/**
	 * 获取圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap = null;
		return output;
	}

	/**
	 * 怀旧效果
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap oldRemeber(Bitmap bmp) {
		// 速度测试
		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR,
						newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.e("may", "used time=" + (end - start));
		return bitmap;
	}

	/**
	 * 模糊效果
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap blurImage(Bitmap bmp) {
		// 速度测试
		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int newColor = 0;

		int[][] colors = new int[9][3];
		for (int i = 1, length = width - 1; i < length; i++) {
			for (int k = 1, len = height - 1; k < len; k++) {
				for (int m = 0; m < 9; m++) {
					int s = 0;
					int p = 0;
					switch (m) {
					case 0:
						s = i - 1;
						p = k - 1;
						break;
					case 1:
						s = i;
						p = k - 1;
						break;
					case 2:
						s = i + 1;
						p = k - 1;
						break;
					case 3:
						s = i + 1;
						p = k;
						break;
					case 4:
						s = i + 1;
						p = k + 1;
						break;
					case 5:
						s = i;
						p = k + 1;
						break;
					case 6:
						s = i - 1;
						p = k + 1;
						break;
					case 7:
						s = i - 1;
						p = k;
						break;
					case 8:
						s = i;
						p = k;
					}
					pixColor = bmp.getPixel(s, p);
					colors[m][0] = Color.red(pixColor);
					colors[m][1] = Color.green(pixColor);
					colors[m][2] = Color.blue(pixColor);
				}

				for (int m = 0; m < 9; m++) {
					newR += colors[m][0];
					newG += colors[m][1];
					newB += colors[m][2];
				}

				newR = (int) (newR / 9F);
				newG = (int) (newG / 9F);
				newB = (int) (newB / 9F);

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				newColor = Color.argb(255, newR, newG, newB);
				bitmap.setPixel(i, k, newColor);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
		long end = System.currentTimeMillis();
		Log.e("blurImage()", "used time=" + (end - start));
		return bitmap;
	}

	/**
	 * 柔化效果(高斯模糊)(优化后比上面快三倍)
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap blurImageAmeliorate(Bitmap bmp) {
		long start = System.currentTimeMillis();
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int delta = 16; // 值越小图片会越亮，越大则越暗

		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * gauss[idx]);
						newG = newG + (int) (pixG * gauss[idx]);
						newB = newB + (int) (pixB * gauss[idx]);
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("blurImageAmeliorate", "used time=" + (end - start));
		return bitmap;
	}

	/**
	 * 浮雕
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap emboss(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				pixColor = pixels[pos + 1];
				newR = Color.red(pixColor) - pixR + 127;
				newG = Color.green(pixColor) - pixG + 127;
				newB = Color.blue(pixColor) - pixB + 127;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 图片锐化（拉普拉斯变换）
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap sharpenImageAmeliorate(Bitmap bmp) {
		long start = System.currentTimeMillis();
		// 拉普拉斯矩阵
		int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int idx = 0;
		float alpha = 0.3F;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + n) * width + k + m];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * laplacian[idx] * alpha);
						newG = newG + (int) (pixG * laplacian[idx] * alpha);
						newB = newB + (int) (pixB * laplacian[idx] * alpha);
						idx++;
					}
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.e("sharpenImageAmeliorate", "used time=" + (end - start));
		return bitmap;
	}

	/**
	 * 底片效果
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap film(Bitmap bmp) {
		// RGBA的最大值
		final int MAX_VALUE = 255;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = MAX_VALUE - pixR;
				newG = MAX_VALUE - pixG;
				newB = MAX_VALUE - pixB;

				newR = Math.min(MAX_VALUE, Math.max(0, newR));
				newG = Math.min(MAX_VALUE, Math.max(0, newG));
				newB = Math.min(MAX_VALUE, Math.max(0, newB));

				pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 光照效果
	 * 
	 * @param bmp
	 *            光照中心x坐标
	 * @param centerX
	 *            光照中心要坐标
	 * @param centerY
	 * @return
	 */
	public static Bitmap sunshine(Bitmap bmp, int centerX, int centerY) {
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;
		int radius = Math.min(centerX, centerY);

		final float strength = 150F; // 光照强度 100~150
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(
						centerX - k, 2));
				if (distance < radius * radius) {
					// 按照距离大小计算增加的光照值
					int result = (int) (strength * (1.0 - Math.sqrt(distance)
							/ radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 素描
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap sketch(Bitmap bmp) {
		long start = System.currentTimeMillis();
		int pos, row, col, clr;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] pixSrc = new int[width * height];
		int[] pixNvt = new int[width * height];
		// 先对图象的像素处理成灰度颜色后再取反
		bmp.getPixels(pixSrc, 0, width, 0, 0, width, height);

		for (row = 0; row < height; row++) {
			for (col = 0; col < width; col++) {
				pos = row * width + col;
				pixSrc[pos] = (Color.red(pixSrc[pos])
						+ Color.green(pixSrc[pos]) + Color.blue(pixSrc[pos])) / 3;
				pixNvt[pos] = 255 - pixSrc[pos];
			}
		}

		// 对取反的像素进行高斯模糊, 强度可以设置，暂定为5.0
		gaussGray(pixNvt, 5.0, 5.0, width, height);

		// 灰度颜色和模糊后像素进行差值运算
		for (row = 0; row < height; row++) {
			for (col = 0; col < width; col++) {
				pos = row * width + col;

				clr = pixSrc[pos] << 8;
				clr /= 256 - pixNvt[pos];
				clr = Math.min(clr, 255);

				pixSrc[pos] = Color.rgb(clr, clr, clr);
			}
		}
		bmp.setPixels(pixSrc, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("blurImageAmeliorate", "used time=" + (end - start));
		return bmp;
	}

	private static int gaussGray(int[] psrc, double horz, double vert,
			int width, int height) {
		int[] dst, src;
		double[] n_p, n_m, d_p, d_m, bd_p, bd_m;
		double[] val_p, val_m;
		int i, j, t, k, row, col, terms;
		int[] initial_p, initial_m;
		double std_dev;
		int row_stride = width;
		int max_len = Math.max(width, height);
		int sp_p_idx, sp_m_idx, vp_idx, vm_idx;

		val_p = new double[max_len];
		val_m = new double[max_len];

		n_p = new double[5];
		n_m = new double[5];
		d_p = new double[5];
		d_m = new double[5];
		bd_p = new double[5];
		bd_m = new double[5];

		src = new int[max_len];
		dst = new int[max_len];

		initial_p = new int[4];
		initial_m = new int[4];

		// 垂直方向
		if (vert > 0.0) {
			vert = Math.abs(vert) + 1.0;
			std_dev = Math.sqrt(-(vert * vert) / (2 * Math.log(1.0 / 255.0)));

			// 初试化常量
			findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);

			for (col = 0; col < width; col++) {
				for (k = 0; k < max_len; k++) {
					val_m[k] = val_p[k] = 0;
				}

				for (t = 0; t < height; t++) {
					src[t] = psrc[t * row_stride + col];
				}

				sp_p_idx = 0;
				sp_m_idx = height - 1;
				vp_idx = 0;
				vm_idx = height - 1;

				initial_p[0] = src[0];
				initial_m[0] = src[height - 1];

				for (row = 0; row < height; row++) {
					terms = (row < 4) ? row : 4;

					for (i = 0; i <= terms; i++) {
						val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
								* val_p[vp_idx - i];
						val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
								* val_m[vm_idx + i];
					}
					for (j = i; j <= 4; j++) {
						val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
						val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
					}

					sp_p_idx++;
					sp_m_idx--;
					vp_idx++;
					vm_idx--;
				}

				transferGaussPixels(val_p, val_m, dst, 1, height);

				for (t = 0; t < height; t++) {
					psrc[t * row_stride + col] = dst[t];
				}
			}
		}

		// 水平方向
		if (horz > 0.0) {
			horz = Math.abs(horz) + 1.0;

			if (horz != vert) {
				std_dev = Math.sqrt(-(horz * horz)
						/ (2 * Math.log(1.0 / 255.0)));

				// 初试化常量
				findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);
			}

			for (row = 0; row < height; row++) {
				for (k = 0; k < max_len; k++) {
					val_m[k] = val_p[k] = 0;
				}

				for (t = 0; t < width; t++) {
					src[t] = psrc[row * row_stride + t];
				}

				sp_p_idx = 0;
				sp_m_idx = width - 1;
				vp_idx = 0;
				vm_idx = width - 1;

				initial_p[0] = src[0];
				initial_m[0] = src[width - 1];

				for (col = 0; col < width; col++) {
					terms = (col < 4) ? col : 4;

					for (i = 0; i <= terms; i++) {
						val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
								* val_p[vp_idx - i];
						val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
								* val_m[vm_idx + i];
					}
					for (j = i; j <= 4; j++) {
						val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
						val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
					}

					sp_p_idx++;
					sp_m_idx--;
					vp_idx++;
					vm_idx--;
				}

				transferGaussPixels(val_p, val_m, dst, 1, width);

				for (t = 0; t < width; t++) {
					psrc[row * row_stride + t] = dst[t];
				}
			}
		}

		return 0;
	}

	private static void findConstants(double[] n_p, double[] n_m, double[] d_p,
			double[] d_m, double[] bd_p, double[] bd_m, double std_dev) {
		double div = Math.sqrt(2 * 3.141593) * std_dev;
		double x0 = -1.783 / std_dev;
		double x1 = -1.723 / std_dev;
		double x2 = 0.6318 / std_dev;
		double x3 = 1.997 / std_dev;
		double x4 = 1.6803 / div;
		double x5 = 3.735 / div;
		double x6 = -0.6803 / div;
		double x7 = -0.2598 / div;
		int i;

		n_p[0] = x4 + x6;
		n_p[1] = (Math.exp(x1)
				* (x7 * Math.sin(x3) - (x6 + 2 * x4) * Math.cos(x3)) + Math
				.exp(x0) * (x5 * Math.sin(x2) - (2 * x6 + x4) * Math.cos(x2)));
		n_p[2] = (2
				* Math.exp(x0 + x1)
				* ((x4 + x6) * Math.cos(x3) * Math.cos(x2) - x5 * Math.cos(x3)
						* Math.sin(x2) - x7 * Math.cos(x2) * Math.sin(x3)) + x6
				* Math.exp(2 * x0) + x4 * Math.exp(2 * x1));
		n_p[3] = (Math.exp(x1 + 2 * x0)
				* (x7 * Math.sin(x3) - x6 * Math.cos(x3)) + Math.exp(x0 + 2
				* x1)
				* (x5 * Math.sin(x2) - x4 * Math.cos(x2)));
		n_p[4] = 0.0;

		d_p[0] = 0.0;
		d_p[1] = -2 * Math.exp(x1) * Math.cos(x3) - 2 * Math.exp(x0)
				* Math.cos(x2);
		d_p[2] = 4 * Math.cos(x3) * Math.cos(x2) * Math.exp(x0 + x1)
				+ Math.exp(2 * x1) + Math.exp(2 * x0);
		d_p[3] = -2 * Math.cos(x2) * Math.exp(x0 + 2 * x1) - 2 * Math.cos(x3)
				* Math.exp(x1 + 2 * x0);
		d_p[4] = Math.exp(2 * x0 + 2 * x1);

		for (i = 0; i <= 4; i++) {
			d_m[i] = d_p[i];
		}

		n_m[0] = 0.0;
		for (i = 1; i <= 4; i++) {
			n_m[i] = n_p[i] - d_p[i] * n_p[0];
		}

		double sum_n_p, sum_n_m, sum_d;
		double a, b;

		sum_n_p = 0.0;
		sum_n_m = 0.0;
		sum_d = 0.0;

		for (i = 0; i <= 4; i++) {
			sum_n_p += n_p[i];
			sum_n_m += n_m[i];
			sum_d += d_p[i];
		}

		a = sum_n_p / (1.0 + sum_d);
		b = sum_n_m / (1.0 + sum_d);

		for (i = 0; i <= 4; i++) {
			bd_p[i] = d_p[i] * a;
			bd_m[i] = d_m[i] * b;
		}
	}

	private static void transferGaussPixels(double[] src1, double[] src2,
			int[] dest, int bytes, int width) {
		int i, j, k, b;
		int bend = bytes * width;
		double sum;

		i = j = k = 0;
		for (b = 0; b < bend; b++) {
			sum = src1[i++] + src2[j++];

			if (sum > 255)
				sum = 255;
			else if (sum < 0)
				sum = 0;

			dest[k++] = (int) sum;
		}
	}

	/**
	 * 图片去色,返回灰度图片
	 * 
	 * @param bmpOriginal
	 *            传入的图片
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 组合涂鸦图片和源图片 （水印）
	 * 
	 * @param src
	 *            源图片
	 * @param watermark
	 *            涂鸦图片
	 * @return
	 */

	public static Bitmap doodle(Bitmap src, String text) {
		// 另外创建一张图片
		Bitmap newb = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas canvas = new Canvas(newb);
		canvas.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入原图片src
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG  
                | Paint.DEV_KERN_TEXT_FLAG);
		paint.setTextSize(40.0f);
		paint.setColor(Color.RED);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText(text, src.getWidth()/1.5f, src.getWidth()/10.0f, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();

		return newb;
	}

	/**
	 * bitmap回收
	 * 
	 * @param picMap
	 */
	public static void recycle(Map<Integer, Bitmap> picMap) {
		if (picMap != null && picMap.size() > 0) {
			Set<Integer> set = picMap.keySet();
			Iterator<Integer> it = set.iterator();
			while (it.hasNext()) {
				Bitmap bitmap = picMap.get(it.next());
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		}
	}

}
