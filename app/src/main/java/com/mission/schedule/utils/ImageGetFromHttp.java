package com.mission.schedule.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageGetFromHttp {
	private static final String LOG_TAG = "ImageGetFromHttp";

	public static Bitmap downloadBitmap(String url, int Width, int Height) {
		// final HttpClient client = new DefaultHttpClient();
		// final HttpGet getRequest = new HttpGet(url);
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			URL realUrl = new URL(url);
			// HttpResponse response = client.execute(getRequest);
			conn = (HttpURLConnection) realUrl.openConnection();// 2.设置请求信息（请求方式...
																// ...）
			// 设置请求方式和响应时间
			conn.setRequestMethod("GET");
			conn.setRequestProperty("encoding", "UTF-8"); // 可以指定编码
			conn.setConnectTimeout(5000);
			// 不使用缓存
			conn.setUseCaches(false);
			final int statusCode = conn.getResponseCode();
			if (statusCode != 200) {
				Log.w(LOG_TAG, "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			// final HttpEntity entity = response.getEntity();
			// if (entity != null) {
			// InputStream inputStream = null;
			try {
				// inputStream = entity.getContent();
				inputStream = conn.getInputStream();
				FilterInputStream fit = new FlushedInputStream(inputStream);
				int scale = downImage(url, Width, Height);
				if (scale == 0) {
					return BitmapFactory.decodeStream(fit);
				} else {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = scale;
					return BitmapFactory.decodeStream(fit, null, options);
				}
			} finally {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				// entity.consumeContent();
			}
			// }
		} catch (IOException e) {
			conn.disconnect();
			// getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			conn.disconnect();
			// getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			conn.disconnect();
			// getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			// client.getConnectionManager().shutdown();
			// 4.释放资源
			if (conn != null) {
				// 关闭连接 即设置 http.keepAlive = false;
				conn.disconnect();
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			conn.disconnect();
		}
		return null;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	public static int downImage(String url, int Width, int Height) {
		// final HttpClient client = new DefaultHttpClient();
		// final HttpGet getRequest = new HttpGet(url);
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();// 2.设置请求信息（请求方式...
																// ...）
			// 设置请求方式和响应时间
			conn.setRequestMethod("GET");
			conn.setRequestProperty("encoding", "UTF-8"); // 可以指定编码
			conn.setConnectTimeout(5000);
			// 不使用缓存
			conn.setUseCaches(false);
			// HttpResponse response = client.execute(getRequest);
			final int statusCode = conn.getResponseCode();
			if (statusCode != 200) {
				Log.w(LOG_TAG, "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return 0;
			}

			// final HttpEntity entity = response.getEntity();
			// if (entity != null) {
			// InputStream inputStream = null;
			try {
				// inputStream = entity.getContent();
				inputStream = conn.getInputStream();
				FilterInputStream fit = new FlushedInputStream(inputStream);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(fit, null, options);
				int imageWidth = options.outWidth;
				int imageHeight = options.outHeight;
				int widthscale = imageWidth / Width;
				int heightscale = imageHeight / Height;
				int scale = widthscale > heightscale ? widthscale : heightscale;

				return scale;
				// return BitmapFactory.decodeStream(fit);
			} finally {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				// entity.consumeContent();
			}
			// }
		} catch (IOException e) {
			conn.disconnect();
			// getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			conn.disconnect();
			// getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			conn.disconnect();
			// getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			// 4.释放资源
			if (conn != null) {
				// 关闭连接 即设置 http.keepAlive = false;
				conn.disconnect();
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			conn.disconnect();
			// client.getConnectionManager().shutdown();
		}
		return 0;
	}
}