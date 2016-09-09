//package com.mission.schedule.utils;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class FileUtils {
//	private String SDPATH;
//
//	private int FILESIZE = 4 * 1024;
//
//	public String getSDPATH() {
//		return SDPATH;
//	}
//
//	public FileUtils() {
//		// 得到当前外部存储设备的目录( /SDCARD )
//		SDPATH = Environment.getExternalStorageDirectory() + "/";
//	}
//
//	/**
//	 * 在SD卡上创建文件
//	 *
//	 * @param fileName
//	 * @return
//	 * @throws IOException
//	 */
//	public File createSDFile(String fileName) throws IOException {
//		File file = new File(SDPATH + fileName);
//		file.createNewFile();
//		return file;
//	}
//
//	/**
//	 * 在SD卡上创建目录
//	 *
//	 * @param dirName
//	 * @return
//	 */
//	public File createSDDir(String dirName) {
//		File dir = new File(SDPATH + dirName);
//		dir.mkdir();
//		return dir;
//	}
//
//	/**
//	 * 判断SD卡上的文件夹是否存在
//	 *
//	 * @param fileName
//	 * @return
//	 */
//	public boolean isFileExist(String fileName) {
//		File file = new File(SDPATH + fileName);
//		return file.exists();
//	}
//
//	/**
//	 * 将一个InputStream里面的数据写入到SD卡中
//	 *
//	 * @param path
//	 * @param fileName
//	 * @param input
//	 * @return
//	 */
//	public File write2SDFromInput(String path, String fileName,
//			InputStream input) {
//		File file = null;
//		OutputStream output = null;
//		try {
//			createSDDir(path);
//			file = createSDFile(path + fileName);
//			output = new FileOutputStream(file);
//			byte[] buffer = new byte[FILESIZE];
//			while ((input.read(buffer)) != -1) {
//				output.write(buffer);
//			}
//			output.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				output.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return file;
//	}
//
//	// 复制文件夹
//	public static void copyDirectiory(String sourceDir, String targetDir)
//			throws IOException {
//		// 新建目标目录
//		(new File(targetDir)).mkdirs();
//		// 获取源文件夹当前下的文件或目录
//		File[] file = (new File(sourceDir)).listFiles();
//		for (int i = 0; i < file.length; i++) {
//			if (file[i].isFile()) {
//				// 源文件
//				File sourceFile = file[i];
//				// 目标文件
//				File targetFile = new File(
//						new File(targetDir).getAbsolutePath() + File.separator
//								+ file[i].getName());
//				copyFile(sourceFile, targetFile);
//			}
//			if (file[i].isDirectory()) {
//				// 准备复制的源文件夹
//				String dir1 = sourceDir + "/" + file[i].getName();
//				// 准备复制的目标文件夹
//				String dir2 = targetDir + "/" + file[i].getName();
//				copyDirectiory(dir1, dir2);
//			}
//		}
//	}
//
//	// 复制文件
//	public static boolean copyFile(File sourceFile, File targetFile)
//			throws IOException {
//		boolean bo = true;
//		BufferedInputStream inBuff = null;
//		BufferedOutputStream outBuff = null;
//		try {
//			// 新建文件输入流并对它进行缓冲
//			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
//
//			// 新建文件输出流并对它进行缓冲
//			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
//
//			// 缓冲数组
//			byte[] b = new byte[1024 * 5];
//			int len;
//			while ((len = inBuff.read(b)) != -1) {
//				outBuff.write(b, 0, len);
//			}
//			// 刷新此缓冲的输出流
//			outBuff.flush();
//		} catch (Exception e) {
//			bo = false;
//		} finally {
//			// 关闭流
//			if (inBuff != null)
//				inBuff.close();
//			if (outBuff != null)
//				outBuff.close();
//		}
//		return bo;
//	}
//
//	/**
//	 * 删除文件及文件夹
//	 *
//	 * @param filepath
//	 */
//	public static boolean del(String filepath) {
//		try {
//			File f = new File(filepath);// 定义文件路径
//			if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
//				if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
//					f.delete();
//				} else {// 若有则把文件放进数组，并判断是否有下级目录
//					File delFile[] = f.listFiles();
//					int i = f.listFiles().length;
//					for (int j = 0; j < i; j++) {
//						if (delFile[j].isDirectory()) {
//							del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
//						}
//						delFile[j].delete();// 删除文件
//					}
//					f.delete();
//				}
//			} else if (f.exists()) {
//				f.delete();
//			}
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	// -----------------------------------------------------------------------
//	/**
//	 * 删除文件--恢复出厂设置
//	 *
//	 * @param filepath
//	 */
//	public static boolean delfiles(String filepath) {
//		try {
//			File f = new File(filepath);// 定义文件路径
//			if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
//				File delFile[] = f.listFiles();
//				int i = f.listFiles().length;
//				for (int j = 0; j < i; j++) {
//					if (delFile[j].isDirectory()) {
//						delfiles(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
//					}
//					delFile[j].delete();// 删除文件
//				}
//			}
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//
//	// --------------------------------------文件上传下载---------------------------------
//	private int upByte = 1024 * 100;
//	private final int TIME_OUT = 10 * 1000; // 超时时间
//	private final String CHARSET = "utf-8"; // 设置编码
//
//	/**
//	 * android上传文件到服务器
//	 *
//	 * @param file
//	 *            需要上传的文件
//	 * @param RequestURL
//	 *            请求的rul
//	 * @return 返回响应的内容
//	 */
//	public String uploadFile(File file, String RequestURL) {
//		String result = null;
//		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
//		String PREFIX = "--", LINE_END = "\r\n";
//		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
//		try {
//			URL url = new URL(RequestURL);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setReadTimeout(TIME_OUT);
//			conn.setConnectTimeout(TIME_OUT);
//			conn.setDoInput(true); // 允许输入流
//			conn.setDoOutput(true); // 允许输出流
//			conn.setUseCaches(false); // 不允许使用缓存
//			conn.setRequestMethod("POST"); // 请求方式
//			conn.setRequestProperty("Charset", CHARSET); // 设置编码
//			conn.setRequestProperty("connection", "keep-alive");
//			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
//					+ BOUNDARY);
//			if (file != null) {
//				/**
//				 * 当文件不为空，把文件包装并且上传
//				 */
//				DataOutputStream dos = new DataOutputStream(
//						conn.getOutputStream());
//				StringBuffer sb = new StringBuffer();
//				sb.append(PREFIX);
//				sb.append(BOUNDARY);
//				sb.append(LINE_END);
//				/**
//				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
//				 * filename是文件的名字，包含后缀名的 比如:abc.png
//				 */
//				sb.append("Content-Disposition: form-data; name=\"doc\"; filename=\""
//						+ file.getName() + "\"" + LINE_END);
//				sb.append("Content-Type: application/octet-stream; charset="
//						+ CHARSET + LINE_END);
//				sb.append(LINE_END);
//				dos.write(sb.toString().getBytes());
//				// System.out.println("文件路径:"+file.getAbsolutePath());
//				InputStream is = new FileInputStream(file);
//				byte[] bytes = new byte[upByte];
//				int len = 0;
//				while ((len = is.read(bytes)) != -1) {
//					dos.write(bytes, 0, len);
//				}
//				is.close();
//				dos.write(LINE_END.getBytes());
//				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
//						.getBytes();
//				dos.write(end_data);
//				dos.flush();
//				/**
//				 * 获取响应码 200=成功 当响应成功，获取响应的流
//				 */
//				int res = conn.getResponseCode();
//				// System.out.println("response code:"+res);
//				if (res == 200) {
//					// System.out.println("request success");
//					InputStream input = conn.getInputStream();
//					StringBuffer sb1 = new StringBuffer();
//					int ss;
//					while ((ss = input.read()) != -1) {
//						sb1.append((char) ss);
//					}
//					result = sb1.toString();
//				} else {
//					// System.out.println("request error");
//				}
//			}
//		} catch (MalformedURLException e) {
//			// System.out.println("===MalformedURLException:"+ e.getMessage());
//			e.printStackTrace();
//		} catch (IOException e) {
//			// System.out.println("===IOException:"+ e.getMessage());
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//	private int downByte = 1024 * 100;
//
//	/**
//	 * 根据URL下载文件
//	 *
//	 * @param urlStrmyuserdownload
//	 *            .do?uid="+uid+"&type=0&m=0&webVali="+thisWebVali+"&filename=mc
//	 *            "+uid+"data.sqlite3"
//	 * @param str
//	 *            本地文件地址
//	 *            "/data/data/android.mission.CalendarCatS/databases/plan"
//	 * @return
//	 */
//	public boolean downloadIs(String urlStr, String str) {
//		InputStream is = null;
//		FileOutputStream fos;
//		HttpURLConnection urlConn = null;
//		try {
//			URL url = new URL(urlStr);
//			urlConn = (HttpURLConnection) url.openConnection();
//			is = urlConn.getInputStream();
//			fos = new FileOutputStream(str);
//			byte[] buffer = new byte[downByte];
//			int count = 0;
//			while ((count = is.read(buffer)) != -1) {
//				fos.write(buffer, 0, count);
//			}
//			fos.flush();
//			fos.close();
//			is.close();
//			if(count==-1){
//				return false;
//			}else {
//				return true;
//			}
//
//		} catch (Exception e) {
//			// SystemOut.println("----"+e.getMessage());
//			return false;
//		}
//	}
//
//	/**
//	 * 下载文件
//	 *
//	 * @author MR-WU
//	 *
//	 */
//	public static class LoadFileAsyncTask extends
//			AsyncTask<Object, Object, Void> {
//		private boolean finished = false;
//		private Context context;
//		private ProgressBar progressBar;
//		private TextView downState;
//		private String pathSdcard;
//
//		@Override
//		protected Void doInBackground(Object... params) {
//			context = (Context) params[0];// 上下文对象
//			String path = (String) params[1];// 存放地址
//			progressBar = (ProgressBar) params[2];// 控件
//			String urlStr = (String) params[3];// url地址
//			downState = (TextView) params[4];// 状态
//
//			File file = new File(path);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//			pathSdcard = path
//					+ urlStr.substring(urlStr.lastIndexOf("/") + 1,
//							urlStr.length());
//			file = new File(pathSdcard);
//
//			if (file.exists()) {
//				publishProgress(new Object[] { 1, 1 });
//			} else {
//				InputStream is = null;
//				FileOutputStream fos;
//				HttpURLConnection urlConn = null;
//				try {
//					System.out.println("下载啊..");
//					URL url = new URL(urlStr);
//					urlConn = (HttpURLConnection) url.openConnection();
//					is = urlConn.getInputStream();
//					fos = new FileOutputStream(pathSdcard);
//					byte[] buffer = new byte[1024 * 100];
//					int count = 0;
//					int aCount = 0;
//					while ((count = is.read(buffer)) != -1) {
//						aCount += count;
//						fos.write(buffer, 0, count);
//						publishProgress(new Object[] { aCount,
//								urlConn.getContentLength() });
//						if (finished) {
//							break;
//						}
//					}
//					fos.flush();
//					fos.close();
//					is.close();
//				} catch (Exception e) {
//				}
//			}
//			return null;
//		}
//
//		@Override
//		protected void onProgressUpdate(Object... values) {
//			super.onProgressUpdate(values);
//			// System.out.println(values[0].toString()+"=="+values[1].toString());
//			int p = Integer.parseInt(values[0].toString());
//			int m = Integer.parseInt(values[1].toString());
//			progressBar.setMax(m);
//			progressBar.setProgress(p);
//			if (p >= m) {
//				downState.setText("打开");
//				downState.setTag(pathSdcard);
//				downState.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						try {
//							String pathSdcard = v.getTag().toString();
//							Intent intent = openFile(pathSdcard);
//							if (intent != null)
//								context.startActivity(intent);
//						} catch (Exception e) {
//							Toast.makeText(context, "文件不存在呢.", 1500).show();
//						}
//					}
//				});
//			}
//		}
//
//		public void cancleDown() {
//			finished = true;
//		}
//
//	}
//
//	public static Intent openFile(String filePath) {
//		File file = new File(filePath);
//
//		if ((file == null) || !file.exists() || file.isDirectory())
//			return null;
//
//		/* 取得扩展名 */
//		String end = file
//				.getName()
//				.substring(file.getName().lastIndexOf(".") + 1,
//						file.getName().length()).toLowerCase();
//		/* 依扩展名的类型决定MimeType */
//		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
//				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
//			return getAudioFileIntent(filePath);
//		} else if (end.equals("3gp") || end.equals("mp4")) {
//			return getAudioFileIntent(filePath);
//		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
//				|| end.equals("jpeg") || end.equals("bmp")) {
//			return getImageFileIntent(filePath);
//		} else if (end.equals("apk")) {
//			return getApkFileIntent(filePath);
//		} else if (end.equals("ppt")) {
//			return getPptFileIntent(filePath);
//		} else if (end.equals("xls")) {
//			return getExcelFileIntent(filePath);
//		} else if (end.equals("doc")) {
//			return getWordFileIntent(filePath);
//		} else if (end.equals("pdf")) {
//			return getPdfFileIntent(filePath);
//		} else if (end.equals("chm")) {
//			return getChmFileIntent(filePath);
//		} else if (end.equals("txt")) {
//			return getTextFileIntent(filePath, false);
//		} else {
//			return getAllIntent(filePath);
//		}
//	}
//
//	// Android获取一个用于打开APK文件的intent
//	public static Intent getAllIntent(String param) {
//
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "*/*");
//		return intent;
//	}
//
//	// Android获取一个用于打开APK文件的intent
//	public static Intent getApkFileIntent(String param) {
//
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "application/vnd.android.package-archive");
//		return intent;
//	}
//
//	// Android获取一个用于打开VIDEO文件的intent
//	public static Intent getVideoFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra("oneshot", 0);
//		intent.putExtra("configchange", 0);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "video/*");
//		return intent;
//	}
//
//	// Android获取一个用于打开AUDIO文件的intent
//	public static Intent getAudioFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra("oneshot", 0);
//		intent.putExtra("configchange", 0);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "audio/*");
//		return intent;
//	}
//
//	// Android获取一个用于打开Html文件的intent
//	public static Intent getHtmlFileIntent(String param) {
//
//		Uri uri = Uri.parse(param).buildUpon()
//				.encodedAuthority("com.android.htmlfileprovider")
//				.scheme("content").encodedPath(param).build();
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.setDataAndType(uri, "text/html");
//		return intent;
//	}
//
//	// Android获取一个用于打开图片文件的intent
//	public static Intent getImageFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addCategory("android.intent.category.DEFAULT");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "image/*");
//		return intent;
//	}
//
//	// Android获取一个用于打开PPT文件的intent
//	public static Intent getPptFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addCategory("android.intent.category.DEFAULT");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//		return intent;
//	}
//
//	// Android获取一个用于打开Excel文件的intent
//	public static Intent getExcelFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addCategory("android.intent.category.DEFAULT");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "application/vnd.ms-excel");
//		return intent;
//	}
//
//	// Android获取一个用于打开Word文件的intent
//	public static Intent getWordFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addCategory("android.intent.category.DEFAULT");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "application/msword");
//		return intent;
//	}
//
//	// Android获取一个用于打开CHM文件的intent
//	public static Intent getChmFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addCategory("android.intent.category.DEFAULT");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "application/x-chm");
//		return intent;
//	}
//
//	// Android获取一个用于打开文本文件的intent
//	public static Intent getTextFileIntent(String param, boolean paramBoolean) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addCategory("android.intent.category.DEFAULT");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		if (paramBoolean) {
//			Uri uri1 = Uri.parse(param);
//			intent.setDataAndType(uri1, "text/plain");
//		} else {
//			Uri uri2 = Uri.fromFile(new File(param));
//			intent.setDataAndType(uri2, "text/plain");
//		}
//		return intent;
//	}
//
//	// Android获取一个用于打开PDF文件的intent
//	public static Intent getPdfFileIntent(String param) {
//
//		Intent intent = new Intent("android.intent.action.VIEW");
//		intent.addCategory("android.intent.category.DEFAULT");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(new File(param));
//		intent.setDataAndType(uri, "application/pdf");
//		return intent;
//	}
//
//	/**
//	 * 加载头像
//	 *
//	 * @param sdcardPath
//	 *            sdcard路径
//	 * @param imageView
//	 * @param url
//	 *            url 地址
//	 */
//	public static void loadHeadImg(Context context, String sdcardPath, ImageView imageView, String url) {
//		String headImgPath = sdcardPath + url.substring(url.lastIndexOf("/") + 1);
//		Bitmap bitmap = BitmapFactory.decodeFile(headImgPath);
//		if (bitmap != null) {
//			imageView.setImageBitmap(PicturePro.getRoundedCornerBitmap(bitmap,30.0f));
//		} else {
//			new DownHeadImgAsyncTask(context, 0).execute(new Object[] {sdcardPath, imageView, url });
//		}
//	}
//
//	/**
//	 * 加载圆形头像
//	 *
//	 * @param sdcardPath
//	 *            sdcard路径
//	 * @param imageView
//	 * @param url
//	 *            url 地址
//	 */
//	public static void loadRoundHeadImg(Context context, String sdcardPath, ImageView imageView, String url) {
//		String headImgPath = sdcardPath + url.substring(url.lastIndexOf("/") + 1);
//		Bitmap bitmap = BitmapFactory.decodeFile(headImgPath);
//		if (bitmap != null) {
//			Bitmap bitmap2 = decodeFile(bitmap, dip2px(context, 70), dip2px(context, 70));
//			imageView.setImageBitmap(ImageUtils.getRoundBitmap(context, bitmap2));
//		} else {
//			new DownHeadImgAsyncTask(context, 1).execute(new Object[] {sdcardPath, imageView, url });
//		}
//	}
//
//	/**
//	 * 加载个人页面图片
//	 *
//	 * @param sdcardPath
//	 *            sdcard路径
//	 * @param imageView
//	 * @param url
//	 *            url 地址
//	 */
//	public static void loadMyFragmentImg(Context context, String sdcardPath, ImageView imageView, String url) {
//		String headImgPath = sdcardPath + url.substring(url.lastIndexOf("/") + 1);
//		Bitmap bitmap = BitmapFactory.decodeFile(headImgPath);
//		if (bitmap != null) {
//			imageView.setImageBitmap(bitmap);
//		} else {
//			new DownHeadImgAsyncTask(context, 2).execute(new Object[] {sdcardPath, imageView, url });
//		}
//	}
//
//	/**
//	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//	 */
//	public static int dip2px(Context context, float dpValue) {
//		final float scale = context.getResources().getDisplayMetrics().density;
//		return (int) (dpValue * scale + 0.5f);
//	}
//
//	/**
//	 * 从文件解析出Bitmap格式的图片
//	 *
//	 * @param maxWidth
//	 * @param maxHeight
//	 * @return
//	 */
//	public static Bitmap decodeFile(Bitmap bitmap, int maxWidth, int maxHeight) {
//		int width = bitmap.getWidth();
//		int height = bitmap.getHeight();
//		Matrix matrix = new Matrix();
//		float scaleWidth = ((float) maxWidth / width);
//		float scaleHeight = ((float) maxHeight / height);
//		matrix.postScale(scaleWidth, scaleHeight);
//		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//		return newbmp;
//	}
//
//	static class DownHeadImgAsyncTask extends AsyncTask<Object, Object, Void> {
//
//		private Context context;
//		private int type;
//
//		public DownHeadImgAsyncTask(Context context, int type) {
//			this.context = context;
//			this.type = type;
//		}
//
//		@Override
//		protected Void doInBackground(Object... params) {
//			String ImgPath = (String) params[0];
//			ImageView imageView = (ImageView) params[1];
//			String url = (String) params[2];
//
//			StringBuffer strb = new StringBuffer(ParameterUtil.webServiceUrl);
//			strb.append(url);
//			File file = new File(ImgPath);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//			String pathSdcard = ImgPath
//					+ url.substring(url.lastIndexOf("/") + 1);
//
//			FileUtils fu = new FileUtils();
//			fu.downloadIs(strb.toString(), pathSdcard);
//			Bitmap bitmap = BitmapFactory.decodeFile(pathSdcard);
//			publishProgress(new Object[] { bitmap, imageView });
//
//			return null;
//		}
//
//		@Override
//		protected void onProgressUpdate(Object... values) {
//			super.onProgressUpdate(values);
//			Bitmap bitmap = (Bitmap) values[0];
//			ImageView imageView = (ImageView) values[1];
//			if (bitmap != null) {
//				if(type == 0){
//					imageView.setImageBitmap(PicturePro.getRoundedCornerBitmap(bitmap,30.0f));
//				}else if(type == 1){
//					Bitmap bitmap2 = decodeFile(bitmap, dip2px(context, 70), dip2px(context, 70));
//					imageView.setImageBitmap(ImageUtils.getRoundBitmap(context, bitmap2));
//				}else if(type == 2){
//					imageView.setImageBitmap(bitmap);
//				}
//			}
//		}
//
//	}
//
//	/**
//	 * 下载图片显示进度条
//	 *
//	 * @param context
//	 * @param progressBar
//	 * @param imageView
//	 * @param sdcardPath
//	 * @param savePath
//	 */
//	public static void loadImgInProgress(Context context,
//			ProgressBar progressBar, ImageView imageView, String imageName,
//			String savePath) {
//		String imagePath = savePath
//				+ imageName.substring(imageName.lastIndexOf("/") + 1);
//		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//		if (bitmap != null) {
//			imageView.setImageBitmap(bitmap);
//			imageView.setVisibility(View.VISIBLE);
//			progressBar.setVisibility(View.GONE);
//		} else {
//			new DownImgAsyncTask(context, progressBar, imageView)
//					.execute(new Object[] { imageName, savePath });
//		}
//	}
//
//	static class DownImgAsyncTask extends AsyncTask<Object, Integer, String> {
//
//		private Context context;
//		private ProgressBar progressBar;
//		private ImageView imageView;
//
//		private DownImgAsyncTask(Context context, ProgressBar progressBar,
//				ImageView imageView) {
//			this.context = context;
//			this.progressBar = progressBar;
//			this.imageView = imageView;
//		}
//
//		@Override
//		protected String doInBackground(Object... params) {
//			String imgName = (String) params[0];
//			String savePath = (String) params[1];
//			StringBuffer strb = new StringBuffer(ParameterUtil.webServiceUrl);
//			strb.append("pdMobile_calendar!showImage.do?imageName=");
//			strb.append(imgName);
//			strb.append("&imgType=3");
//			strb.append("&imgSizeType=5");
//			strb.append("&webVali=" + ParameterUtil.webVali);
//			int progress = 0;
//			String imgPath = "";
//			try {
//				URL url = new URL(strb.toString());
//				HttpURLConnection conn = (HttpURLConnection) url
//						.openConnection();
//				conn.connect();
//				int length = conn.getContentLength();
//				InputStream is = conn.getInputStream();
//
//				File file = new File(savePath);
//				if (!file.exists()) {
//					file.mkdirs();
//				}
//				imgPath = savePath
//						+ imgName.substring(imgName.lastIndexOf("/") + 1);
//				;// 存放地址
//				File imgFile = new File(imgPath);
//				FileOutputStream fos = new FileOutputStream(imgFile);
//
//				int count = 0;
//				byte buf[] = new byte[1024];
//
//				do {
//					int numread = is.read(buf);
//					count += numread;
//					progress = (int) (((float) count / length) * 100);
//					if (numread <= 0) {
//						break;
//					}
//					fos.write(buf, 0, numread);
//					publishProgress(progress);
//				} while (true);// 点击取消就停止下载.
//				fos.close();
//				is.close();
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return imgPath;
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			super.onProgressUpdate(values);
//			progressBar.setProgress(values[0]);
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			progressBar.setVisibility(View.GONE);
//			File file = new File(result);
//			if (file.exists()) {
//				imageView.setVisibility(View.VISIBLE);
//				imageView.setImageBitmap(BitmapFactory.decodeFile(result));
//			} else {
//				Toast.makeText(context, "下载图片失败...", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//
////	public static void downTeamNotice(Context context, Handler handler, String userid) {
////		new downTeamNoticeAsyncTask().execute(new Object[] { context, handler, userid });
////	}
//	/**
//	 * 增加积分公共接口
//	 * @param context
//	 * @param userid
//	 * @param type 1打开时间表
//	 */
////	public static void addTimeTableScore(Context context,String userid,int type){
////		new addTimeTableScoreAsyncTask().execute(new Object[] { context, userid, type});
////	}
//
//	/**
//	 * 下载TeamNotice的新消息
//	 *
//	 * @author MR-DENG
//	 *
//	 */
////	static class downTeamNoticeAsyncTask extends AsyncTask<Object, Void, String> {
////
////		private Context context;
////		private Handler handler;
////
////		@Override
////		protected String doInBackground(Object... params) {
////
////			context = (Context) params[0];
////			handler = (Handler) params[1];
////			String userid = (String) params[2];
////			// 保存查询beforTime
////			SharedPreferences sp = null;
////			String beforTimeTeam = "0";
////			try {
////				sp = context.getSharedPreferences("befortimexml", Context.MODE_PRIVATE);
////				SharedPreferences.Editor e = sp.edit();
////				beforTimeTeam = sp.getString("beforTimeTeam", "0");
////				if ("0".equals(beforTimeTeam)) {
////					beforTimeTeam = "1970-01-01 00:00:01";
////					e.putString("beforTimeTeam", beforTimeTeam);
////					e.commit();
////				}
////			} catch (Exception e) {
////				return null;
////			}
////
////			String result = null;
////			List<String> paramList = new ArrayList<String>();
////			try {
////				JSONObject jsonObject = new JSONObject();
////				jsonObject.put("webVali", ParameterUtil.webVali);
////				jsonObject.put("uid", userid);
////				jsonObject.put("beforTime", beforTimeTeam);
////				paramList.add(jsonObject.toString());
////				String strResult = ServiceUtil.getServiceXML(
////						ParameterUtil.webServiceUrl,
////						ParameterUtil.webWs_syTags, "synTeamCalendars",
////						paramList, context);
////				result = JsonFormater.divisionStr(strResult);
////				if (result != null && !"{}".equals(result)) {
////					try {
////						JSONObject jsonResult = new JSONObject(result);
////						if (jsonResult.getInt(PubKey.STATE) == StatusCode.SUCCEED) {
////							JSONArray jsonArray = jsonResult.getJSONArray("pageData");
////							int number = 0;
////							for (int i = 0; i < jsonArray.length(); i++) {
////								JSONObject objects = jsonArray.getJSONObject(i);
////								JSONObject object = objects
////										.getJSONObject("currentData");
////								Map<String, String> localMap = null;
////								// 普通下载
////
////								localMap = DBContextApplication
////										.getDBcApplication()
////										.synLocalNoticeForID(
////												" parentId="
////														+ object.getString("parentId")
////														+ " and tType="
////														+ object.getString("tType"));
////								if (localMap == null) {
////
////									DBContextApplication.getDBcApplication()
////											.synAddLocalNoticeData(object);
////									number = 1;
////								} else {
////									// 循环本地数据
////
////									if (!"".equals(object
////											.getString("calendarDetailLastUpdateTime"))
////											&& !"".equals(localMap
////													.get("calendarDetailLastUpdateTime"))) {
////										// 网上时间大于本地时间
////										if (DateUtil
////												.parseDateTimeSs(
////														object.getString("calendarDetailLastUpdateTime"))
////												.getTime() > DateUtil
////												.parseDateTimeSs(
////														localMap.get("calendarDetailLastUpdateTime"))
////												.getTime()) {
////											// update
////											Map<String, String> upMap = new HashMap<String, String>();
////
////											upMap.put(
////													LocateAllNoticeTable.alarmClockTime,
////													object.getString("alarmClockTime"));
////											upMap.put(
////													LocateAllNoticeTable.alarmSound,
////													object.getString("alarmSound")
////															+ ".ogg");
////											upMap.put(
////													LocateAllNoticeTable.alarmSoundDesc,
////													object.getString("alarmSoundDesc"));
////											upMap.put(
////													LocateAllNoticeTable.beforTime,
////													object.getString("beforTime"));
////											upMap.put(
////													LocateAllNoticeTable.calendarDetailLastUpdateTime,
////													object.getString("calendarDetailLastUpdateTime"));
////											upMap.put(
////													LocateAllNoticeTable.cdD_value,
////													object.getString("cdD_value"));
////											upMap.put(
////													LocateAllNoticeTable.cdLastDate,
////													object.getString("cdLastDate"));
////											upMap.put(
////													LocateAllNoticeTable.colorType,
////													object.getString("colorType"));
////											upMap.put(
////													LocateAllNoticeTable.createTime,
////													object.getString("createTime"));
////											upMap.put(
////													LocateAllNoticeTable.displayAlarm,
////													object.getString("displayAlarm"));
////											upMap.put(
////													LocateAllNoticeTable.displayDate,
////													object.getString("displayDate"));
////											upMap.put(
////													LocateAllNoticeTable.fromUserImg,
////													object.getString("fromUserImg"));
////											upMap.put(
////													LocateAllNoticeTable.fromUserNickName,
////													object.getString("fromUserNickName"));
////											upMap.put(
////													LocateAllNoticeTable.imageName,
////													object.getString("imageName"));
////											upMap.put(
////													LocateAllNoticeTable.isNeedPush,
////													object.getString("isNeedPush"));
////											upMap.put(
////													LocateAllNoticeTable.noticeContent,
////													object.getString("noticeContent"));
////											upMap.put(
////													LocateAllNoticeTable.noticeDate,
////													object.getString("noticeDate"));
////											upMap.put(
////													LocateAllNoticeTable.postpone,
////													object.getString("postpone"));
////											upMap.put(
////													LocateAllNoticeTable.readTimes,
////													object.getString("readTimes"));
////											upMap.put(
////													LocateAllNoticeTable.senduid,
////													object.getString("senduid"));
////											upMap.put(
////													LocateAllNoticeTable.tags,
////													object.getString("tags"));
////											upMap.put(
////													LocateAllNoticeTable.totalGrade,
////													object.getString("totalGrade"));
////											upMap.put(
////													LocateAllNoticeTable.tType,
////													object.getString("tType"));
////											upMap.put(
////													LocateAllNoticeTable.parentId,
////													object.getString("parentId"));
////											upMap.put(
////													LocateAllNoticeTable.toUserName,
////													object.getString("toUserName"));
////											upMap.put(
////													LocateAllNoticeTable.locateNoId,
////													object.getString("parentColorType"));
////											upMap.put(
////													LocateAllNoticeTable.aFinishCount,
////													object.getString("aFinishCount"));
////											upMap.put(
////													LocateAllNoticeTable.allFinishCount,
////													object.getString("allFinishCount"));
////											upMap.put(
////													LocateAllNoticeTable.tpId,
////													object.getString("tpId"));
////											upMap.put(
////													LocateAllNoticeTable.orderVauue,
////													object.getString("orderVauue"));
////											upMap.put(
////													LocateAllNoticeTable.source,
////													object.getString("source"));
////											upMap.put(
////													LocateAllNoticeTable.noticeOriginalDate,
////													object.getString("noticeOriginalDate"));
////											upMap.put(
////													LocateAllNoticeTable.d_postponeCount,
////													object.getString("d_postponeCount"));
////											upMap.put(
////													LocateAllNoticeTable.d_endDate,
////													object.getString("d_endDate"));
////											upMap.put(
////													LocateAllNoticeTable.locateUpdateState,
////													"0");
////											upMap.put(
////													LocateAllNoticeTable.noticeIsStarred,
////													object.getString("noticeIsStarred"));
////											upMap.put(
////													LocateAllNoticeTable.teamNoticeReadState,
////													object.getString("teamNoticeReadState"));
////											upMap.put(
////													LocateAllNoticeTable.teamNoticeLocateSign,
////													object.getString("teamNoticeLocateSign"));
////											upMap.put(
////													LocateAllNoticeTable.teamIsParentFinish,
////													object.getString("teamIsParentFinish"));
////											DBContextApplication
////													.getDBcApplication()
////													.updateLocalNoticeData(
////															upMap,
////															" where parentId="
////																	+ upMap.get(LocateAllNoticeTable.parentId));
////											number = 1;
////										}
////										// 网上不等于本地并且本地为空
////									} else if (!localMap
////											.get("calendarDetailLastUpdateTime")
////											.equals(object
////													.getString("calendarDetailLastUpdateTime"))
////											&& "".equals(localMap
////													.get("calendarDetailLastUpdateTime"))) {
////
////										// update
////										Map<String, String> upMap = new HashMap<String, String>();
////
////										upMap.put(
////												LocateAllNoticeTable.alarmClockTime,
////												object.getString("alarmClockTime"));
////										upMap.put(
////												LocateAllNoticeTable.alarmSound,
////												object.getString("alarmSound")
////														+ ".ogg");
////										upMap.put(
////												LocateAllNoticeTable.alarmSoundDesc,
////												object.getString("alarmSoundDesc"));
////										upMap.put(
////												LocateAllNoticeTable.beforTime,
////												object.getString("beforTime"));
////										upMap.put(
////												LocateAllNoticeTable.calendarDetailLastUpdateTime,
////												object.getString("calendarDetailLastUpdateTime"));
////										upMap.put(
////												LocateAllNoticeTable.cdD_value,
////												object.getString("cdD_value"));
////										upMap.put(
////												LocateAllNoticeTable.cdLastDate,
////												object.getString("cdLastDate"));
////										upMap.put(
////												LocateAllNoticeTable.colorType,
////												object.getString("colorType"));
////										upMap.put(
////												LocateAllNoticeTable.createTime,
////												object.getString("createTime"));
////										upMap.put(
////												LocateAllNoticeTable.displayAlarm,
////												object.getString("displayAlarm"));
////										upMap.put(
////												LocateAllNoticeTable.displayDate,
////												object.getString("displayDate"));
////										upMap.put(
////												LocateAllNoticeTable.fromUserImg,
////												object.getString("fromUserImg"));
////										upMap.put(
////												LocateAllNoticeTable.fromUserNickName,
////												object.getString("fromUserNickName"));
////										upMap.put(
////												LocateAllNoticeTable.imageName,
////												object.getString("imageName"));
////										upMap.put(
////												LocateAllNoticeTable.isNeedPush,
////												object.getString("isNeedPush"));
////										upMap.put(
////												LocateAllNoticeTable.noticeContent,
////												object.getString("noticeContent"));
////										upMap.put(
////												LocateAllNoticeTable.noticeDate,
////												object.getString("noticeDate"));
////										upMap.put(
////												LocateAllNoticeTable.postpone,
////												object.getString("postpone"));
////										upMap.put(
////												LocateAllNoticeTable.readTimes,
////												object.getString("readTimes"));
////										upMap.put(LocateAllNoticeTable.senduid,
////												object.getString("senduid"));
////										upMap.put(LocateAllNoticeTable.tags,
////												object.getString("tags"));
////										upMap.put(
////												LocateAllNoticeTable.totalGrade,
////												object.getString("totalGrade"));
////										upMap.put(LocateAllNoticeTable.tType,
////												object.getString("tType"));
////										upMap.put(
////												LocateAllNoticeTable.parentId,
////												object.getString("parentId"));
////										upMap.put(
////												LocateAllNoticeTable.toUserName,
////												object.getString("toUserName"));
////										upMap.put(
////												LocateAllNoticeTable.locateNoId,
////												object.getString("parentColorType"));
////										upMap.put(
////												LocateAllNoticeTable.aFinishCount,
////												object.getString("aFinishCount"));
////										upMap.put(
////												LocateAllNoticeTable.allFinishCount,
////												object.getString("allFinishCount"));
////										upMap.put(LocateAllNoticeTable.tpId,
////												object.getString("tpId"));
////										upMap.put(
////												LocateAllNoticeTable.orderVauue,
////												object.getString("orderVauue"));
////										upMap.put(LocateAllNoticeTable.source,
////												object.getString("source"));
////										upMap.put(
////												LocateAllNoticeTable.noticeOriginalDate,
////												object.getString("noticeOriginalDate"));
////										upMap.put(
////												LocateAllNoticeTable.d_postponeCount,
////												object.getString("d_postponeCount"));
////										upMap.put(
////												LocateAllNoticeTable.d_endDate,
////												object.getString("d_endDate"));
////										upMap.put(
////												LocateAllNoticeTable.locateUpdateState,
////												"0");
////										upMap.put(
////												LocateAllNoticeTable.noticeIsStarred,
////												object.getString("noticeIsStarred"));
////										upMap.put(
////												LocateAllNoticeTable.teamNoticeReadState,
////												object.getString("teamNoticeReadState"));
////										upMap.put(
////												LocateAllNoticeTable.teamNoticeLocateSign,
////												object.getString("teamNoticeLocateSign"));
////										upMap.put(
////												LocateAllNoticeTable.teamIsParentFinish,
////												object.getString("teamIsParentFinish"));
////										DBContextApplication
////												.getDBcApplication()
////												.updateLocalNoticeData(
////														upMap,
////														" where parentId="
////																+ upMap.get(LocateAllNoticeTable.parentId));
////										number = 1;
////									}
////
////								}
////
////							}
////
////							// 网上删除本地
////							JSONArray array = jsonResult
////									.getJSONArray("delData");
////							if (array.length() > 0) {
////								number = 1;
////								for (int i = 0; i < array.length(); i++) {
////									DBContextApplication
////											.getDBcApplication()
////											.deleteLocalNoticeData(
////													" parentId="
////															+ array.getString(i));
////								}
////							}
////
////							if (number == 1) {
////								try {
////									SharedPreferences.Editor e = sp.edit();
////									e.putString("beforTimeTeam",
////											jsonResult.getString("beforTime"));
////									e.commit();
////
////									DBContextApplication dbContextApplication = DBContextApplication
////											.getDBcApplication();
////									dbContextApplication
////											.addRepeatToNoticeData();
////
////								} catch (Exception e) {
////									return null;
////								}
////							}
////
////						}
////					} catch (JSONException e1) {
////						e1.printStackTrace();
////					}
////				}
////			} catch (Exception e) {
////			} finally {
////				SystemOut.println("result " + result);
////			}
////			return null;
////		}
//
////		@Override
////		protected void onPostExecute(String result) {
////			super.onPostExecute(result);
////			if (handler == null) {
////				((MainActivity) context).setPrivateXml("Refresh", "RefreshCode", "1");// 刷新列表
////				((MainActivity) context).refreshData(0);// 刷新日程
////			} else {
////				Message message = new Message();
////				message.what = 0;
////				handler.sendMessage(message);
////			}
////		}
//
////	}
//	/**
//	 * 增加积分
//	 * @author pc-2014-2
//	 *
//	 */
////	static class addTimeTableScoreAsyncTask extends AsyncTask<Object, Void, String> {
////		private Context context;
////		@Override
////		protected String doInBackground(Object... params) {
////			context = (Context) params[0];
////			String userid = (String) params[1];
////			int type = Integer.parseInt(params[2].toString()) ;
////
////			String result = null;
////			List<String> paramList = new ArrayList<String>();
////			try {
////				JSONObject jsonObject = new JSONObject();
////				jsonObject.put("webVali", ParameterUtil.webVali);
////				jsonObject.put("uid", userid);
////				jsonObject.put("type", type);
////				paramList.add(jsonObject.toString());
////				ServiceUtil.getServiceXML(
////						ParameterUtil.webServiceUrl,
////						ParameterUtil.webWs_userInfo, "addScoreByType",
////						paramList, context);
////			} catch (Exception e) {
////			} finally {
////				SystemOut.println("result " + result);
////			}
////			return null;
////		}
////		@Override
////		protected void onPostExecute(String result) {
////			super.onPostExecute(result);
////		}
////	}
//
////	public static void downDynamic(Context context, Handler handler, TextView textView, String userid) {
////		new downDynamicAsyncTask().execute(new Object[] { context, handler, textView, userid });
////	}
//
//	/**
//	 * 下载TeamNotice的新消息
//	 *
//	 * @author MR-DENG
//	 *
//	 */
////	static class downDynamicAsyncTask extends AsyncTask<Object, Void, String> {
////
////		private TextView textView;
////		private Context context;
////		private Handler handler;
////
////		@Override
////		protected String doInBackground(Object... params) {
////			context = (Context) params[0];
////			handler = (Handler) params[1];
////			textView = (TextView) params[2];
////			String userid = (String) params[3];
////
////			String result = null;
////			List<String> paramList = new ArrayList<String>();
////			try {
////				JSONObject jsonObject = new JSONObject();
////				jsonObject.put("webVali", ParameterUtil.webVali);
////				jsonObject.put("uid", userid);
////				jsonObject.put("isTeam", "1");
////				paramList.add(jsonObject.toString());
////				String strResult = ServiceUtil.getServiceXML(
////						ParameterUtil.webServiceUrl,
////						ParameterUtil.webWs_timeInfo, "getInfoTrendsCount",
////						paramList, context);
////				result = JsonFormater.divisionStr(strResult);
////			} catch (Exception ex) {
////			} finally {
////				SystemOut.println("result11 " + result);
////			}
////
////			return result;
////		}
////
////		@Override
////		protected void onPostExecute(String result) {
////			super.onPostExecute(result);
////			if (result != null && !"{}".equals(result)) {
////				try {
////					JSONObject jsonObject = new JSONObject(result);
////					if (jsonObject.getInt(PubKey.STATE) == StatusCode.SUCCEED) {
////						int notReadItsCount = jsonObject.getInt("notReadItsCount");
////						if (textView != null) {
////							((MainActivity)context).setPrivateXml("message", "count", notReadItsCount + "");
////							if(notReadItsCount > 0){
////								textView.setVisibility(View.VISIBLE);
////								textView.setText(notReadItsCount + "");
////							}else{
////								textView.setVisibility(View.GONE);
////							}
////						}else{
////							textView.setVisibility(View.GONE);
////						}
////						if(handler != null){
////							handler.sendEmptyMessage(0);
////						}
////					}
////				} catch (JSONException e) {
////					e.printStackTrace();
////				}
////			}
////		}
////	}
////
////	public static void downloadRepeatData(Context con) {
////		new UserTeamRepeatAsyncTask(con).execute();
////	}
////
////	// ===============================协作重复记事下载======================================
////	static class UserTeamRepeatAsyncTask extends AsyncTask<Void, Void, Void> {
////		SharedPreferences sp = null;
////		String userid = "";
////		String beforTimeRepeat = "";
////		Context context = null;
////
////		public UserTeamRepeatAsyncTask(Context context) {
////			this.context = context;
////			sp = context.getSharedPreferences("userinfoxml", Context.MODE_PRIVATE);
////			userid = sp.getString("userid", "0");
////			sp = context.getSharedPreferences("teambefortimexml",Context.MODE_PRIVATE);
////			beforTimeRepeat = sp.getString("beforTimeRepeat", "0");
////			if("0".equals(beforTimeRepeat)){
////				beforTimeRepeat = "1970-01-01 00:00:01";
////			}
////		}
////
////		@Override
////		protected Void doInBackground(Void... params) {
////			String result = null;
////			List<String> paramList = new ArrayList<String>();
////			try {
////				JSONObject jsonObject = new JSONObject();
////				jsonObject.put("webVali", ParameterUtil.webVali);
////				jsonObject.put("uid", userid);
////				jsonObject.put("beforTime", beforTimeRepeat);
////				paramList.add(jsonObject.toString());
////				System.out.println("sdfsd" + paramList);
////				String strResult = ServiceUtil.getServiceXML(
////						ParameterUtil.webServiceUrl,
////						ParameterUtil.webWs_syTags, "synRepeatTeamTimePS",
////						paramList, context);
////				System.out.println("repeat..." + result);
////				result = JsonFormater.divisionStr(strResult);
////
////				if (result != null && !"{}".equals(result)) {
////					try {
////						JSONObject jsonResult = new JSONObject(result);
////						if (jsonResult.getInt(PubKey.STATE) == StatusCode.SUCCEED) {
////							JSONArray jsonArray = jsonResult
////									.getJSONArray("data");
////							int number = 0;
////							for (int i = 0; i < jsonArray.length(); i++) {
////								JSONObject object = jsonArray.getJSONObject(i);
////								Map<String, String> localMap = DBContextApplication
////										.getDBcApplication()
////										.synLocalRepeatForID(
////												object.getString("key_id"));
////								if (localMap == null) {
////									// 加入本地
////									DBContextApplication.getDBcApplication()
////											.sysAddLocalRepeatANoticeData(
////													object);
////									number = 1;
////								} else {
////
////									if (!"".equals(object
////											.getString("key_tpUpdateTime"))
////											&& !"".equals(localMap
////													.get("key_tpUpdateTime"))) {
////										// 网上时间大于本地时间
////										if (DateUtil
////												.parseDateTimeSs(
////														object.getString("key_tpUpdateTime"))
////												.getTime() > DateUtil
////												.parseDateTimeSs(
////														localMap.get("key_tpUpdateTime"))
////												.getTime()) {
////
////											// update
////											Map<String, String> upMap = new HashMap<String, String>();
////											upMap.put(
////													LocateRepeatNoticeTable.key_id,
////													object.getString("key_id"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpAlarmSound,
////													object.getString("key_tpAlarmSound")
////															+ ".ogg");
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpBeforTime,
////													object.getString("key_tpBeforTime"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpAlarmSoundDesc,
////													object.getString("key_tpAlarmSoundDesc"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpContent,
////													object.getString("key_tpContent"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpCreateTime,
////													object.getString("key_tpCreateTime"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpDataType,
////													object.getString("key_tpDataType"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpDate,
////													object.getString("key_tpDate"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpDisplayAlarm,
////													object.getString("key_tpDisplayAlarm"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpIsRemind,
////													object.getString("key_tpIsRemind"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpLastDate,
////													object.getString("key_tpLastDate"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpOpenState,
////													object.getString("key_tpOpenState"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpPostpone,
////													object.getString("key_tpPostpone"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpTime,
////													object.getString("key_tpTime"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpType,
////													object.getString("key_tpType"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpUpdateTime,
////													object.getString("key_tpUpdateTime"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpUserId,
////													object.getString("key_tpUserId"));
////											upMap.put(
////													LocateRepeatNoticeTable.locateUpdateState,
////													"0");
////											upMap.put(
////													LocateRepeatNoticeTable.locateNextCreatTime,
////													object.getString("locateNextCreatTime"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpCurWeek,
////													object.getString("key_tpCurWeek"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpDay,
////													object.getString("key_tpDay"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpMonthDay,
////													object.getString("key_tpMonthDay"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpLcDate,
////													object.getString("key_tpLcDate"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpSeparateFlag,
////													object.getString("key_tpSeparateFlag"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpColorType,
////													object.getString("key_tpColorType"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpLocateAlarmFromTime,
////													object.getString("key_tpLocateAlarmFromTime"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpHolidayRepeatType,
////													object.getString("key_tpHolidayRepeatType"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpHolidayName,
////													object.getString("key_tpHolidayName"));
////
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpParentId,
////													object.getString("key_tpParentId"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpTType,
////													object.getString("key_tpTType"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpMemberCount,
////													object.getString("key_tpMemberCount"));
////											upMap.put(
////													LocateRepeatNoticeTable.key_tpToUserName,
////													object.getString("key_tpToUserName"));
////
////											DBContextApplication
////													.getDBcApplication()
////													.updateSynRepeatANoticeData(
////															upMap,
////															" where key_id ="
////																	+ object.getString("key_id"));
////											number = 1;
////										}
////										// 网上不等于本地并且本地为空 或者 网上不等于本地并且
////									} else if (!localMap
////											.get("key_tpUpdateTime")
////											.equals(object
////													.getString("key_tpUpdateTime"))
////											&& "".equals(localMap
////													.get("key_tpUpdateTime"))) {
////
////										// update
////										Map<String, String> upMap = new HashMap<String, String>();
////										upMap.put(
////												LocateRepeatNoticeTable.key_id,
////												object.getString("key_id"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpAlarmSound,
////												object.getString("key_tpAlarmSound")
////														+ ".ogg");
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpBeforTime,
////												object.getString("key_tpBeforTime"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpAlarmSoundDesc,
////												object.getString("key_tpAlarmSoundDesc"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpContent,
////												object.getString("key_tpContent"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpCreateTime,
////												object.getString("key_tpCreateTime"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpDataType,
////												object.getString("key_tpDataType"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpDate,
////												object.getString("key_tpDate"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpDisplayAlarm,
////												object.getString("key_tpDisplayAlarm"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpIsRemind,
////												object.getString("key_tpIsRemind"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpLastDate,
////												object.getString("key_tpLastDate"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpOpenState,
////												object.getString("key_tpOpenState"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpPostpone,
////												object.getString("key_tpPostpone"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpTime,
////												object.getString("key_tpTime"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpType,
////												object.getString("key_tpType"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpUpdateTime,
////												object.getString("key_tpUpdateTime"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpUserId,
////												object.getString("key_tpUserId"));
////										upMap.put(
////												LocateRepeatNoticeTable.locateUpdateState,
////												"0");
////										upMap.put(
////												LocateRepeatNoticeTable.locateNextCreatTime,
////												object.getString("locateNextCreatTime"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpCurWeek,
////												object.getString("key_tpCurWeek"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpDay,
////												object.getString("key_tpDay"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpMonthDay,
////												object.getString("key_tpMonthDay"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpLcDate,
////												object.getString("key_tpLcDate"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpSeparateFlag,
////												object.getString("key_tpSeparateFlag"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpColorType,
////												object.getString("key_tpColorType"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpLocateAlarmFromTime,
////												object.getString("key_tpLocateAlarmFromTime"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpHolidayRepeatType,
////												object.getString("key_tpHolidayRepeatType"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpHolidayName,
////												object.getString("key_tpHolidayName"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpParentId,
////												object.getString("key_tpParentId"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpTType,
////												object.getString("key_tpTType"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpMemberCount,
////												object.getString("key_tpMemberCount"));
////										upMap.put(
////												LocateRepeatNoticeTable.key_tpToUserName,
////												object.getString("key_tpToUserName"));
////
////										DBContextApplication.getDBcApplication().updateSynRepeatANoticeData(upMap," where key_id =" + object.getString("key_id"));
////										number = 1;
////									}
////
////								}
////
////							}
////
////							// 网上删除本地
////							JSONArray array = jsonResult.getJSONArray("delData");
////							if (array.length() > 0) {
////								number = 1;
////								for (int i = 0; i < array.length(); i++) {
////									JSONObject obj = array.getJSONObject(i);
////									String dataid = obj.getString("dataid");
////									String ttype = obj.getString("ttype");
////									DBContextApplication.getDBcApplication().updateTeamLocalRepeatANoticeData(dataid, ttype);
////								}
////							}
////
////							if (number == 1) {
////								sp = context.getSharedPreferences("teambefortimexml", Context.MODE_PRIVATE);
////								SharedPreferences.Editor e = sp.edit();
////								e.putString("beforTimeRepeat", DateUtil.formatDateTimeSs(new Date()));
////								e.commit();
////							}
////
////						}
////					} catch (JSONException e1) {
////						e1.printStackTrace();
////					}
////				}
////			} catch (Exception e) {
////			} finally {
////				SystemOut.println("result " + result);
////			}
////			return null;
////		}
////
////		@Override
////		protected void onPostExecute(Void result) {
////			super.onPostExecute(result);
////			DBContextApplication dbContextApplication = DBContextApplication.getDBcApplication();
////			dbContextApplication.addRepeatToNoticeData();// 自动生成重复，顺延
////
////			((MainActivity) context).setPrivateXml("Refresh", "RefreshCode", "1");// 刷新列表
////			((MainActivity) context).refreshData(0);// 刷新日程
////		}
////	}
//}
