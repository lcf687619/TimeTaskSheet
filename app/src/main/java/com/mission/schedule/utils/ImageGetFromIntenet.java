package com.mission.schedule.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageGetFromIntenet {
    private static final String LOG_TAG = "ImageGetFromHttp";

    public static Bitmap downloadBitmap(String url) {
        // final HttpClient client = new DefaultHttpClient();
        // final HttpGet getRequest = new HttpGet(url);
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        FilterInputStream fit = null;
        try {
            URL connectionurl = new URL(url);
            httpURLConnection = (HttpURLConnection) connectionurl
                    .openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("encoding", "UTF-8"); // 可以指定编码
            httpURLConnection.setConnectTimeout(20000);
            // 不使用缓存
            httpURLConnection.setUseCaches(false);
            // HttpResponse response = client.execute(getRequest);
            final int statusCode = httpURLConnection.getResponseCode();
            if (statusCode != 200) {
                Log.w(LOG_TAG, "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                return null;
            }
            try {
                inputStream = httpURLConnection.getInputStream();
                fit = new FlushedInputStream(inputStream);
                return BitmapFactory.decodeStream(fit);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                if (fit != null) {
                    fit.close();
                    fit = null;
                }
            }
        } catch (IOException e) {
            httpURLConnection.disconnect();
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            httpURLConnection.disconnect();
            Log.w(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            httpURLConnection.disconnect();
            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if (httpURLConnection != null) {
                // 关闭连接 即设置 http.keepAlive = false;
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fit != null) {
                try {
                    fit.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fit = null;
            }
            httpURLConnection.disconnect();
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
}