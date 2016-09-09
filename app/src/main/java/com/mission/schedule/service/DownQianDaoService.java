package com.mission.schedule.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.QianDaoBackBean;
import com.mission.schedule.bean.QianDaoBean;
import com.mission.schedule.bean.QianDaoImgBackBean;
import com.mission.schedule.bean.QianDaoImgBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.ImageFileCache;
import com.mission.schedule.utils.ImageGetFromIntenet;
import com.mission.schedule.utils.ImageMemoryCache;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownQianDaoService extends Service {

	SharedPrefUtil sharedPrefUtil = null;
	App app = null;
	String downtime = "";
	String downImgTime = "";
	// ImageLoader imageLoader = null;
	private ImageMemoryCache memoryCache;
	private ImageFileCache fileCache;
	String imageType = "";
	int heigh = 0;
	String firstLogin = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.init("TAG");
		// File cacheDir =StorageUtils.getOwnCacheDirectory(this,
		// "imageloader/Cache");
		// ImageLoaderConfiguration config = new ImageLoaderConfiguration
		// .Builder(this)
		// .memoryCacheExtraOptions(480, 800) // maxwidth, max
		// height，即保存的每个缓存文件的最大长宽
		// .threadPoolSize(3)//线程池内加载的数量
		// .threadPriority(Thread.NORM_PRIORITY -2)
		// .denyCacheImageMultipleSizesInMemory()
		// // .memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024)) //
		// You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
		// // .memoryCacheSize(2 * 1024 * 1024)
		// // .discCacheSize(50 * 1024 * 1024)
		// //
		// .discCacheFileNameGenerator(newMd5FileNameGenerator())//将保存的时候的URI名称用MD5
		// 加密
		// // .tasksProcessingOrder(QueueProcessingType.LIFO)
		// // .discCacheFileCount(100) //缓存的文件数量
		// .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
		// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
		// .imageDownloader(new BaseImageDownloader(this,10 * 1000, 30 * 1000))
		// // connectTimeout (5 s), readTimeout (30 s)超时时间
		// .writeDebugLogs() // Remove for releaseapp
		// .build();//开始构建
		// ImageLoader.getInstance().init(config);
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		app = App.getDBcApplication();
		new Thread(new Runnable() {

			@Override
			public void run() {
				memoryCache = new ImageMemoryCache(getApplication());
				fileCache = new ImageFileCache();
				sharedPrefUtil = new SharedPrefUtil(getApplication(),
						ShareFile.USERFILE);
				downtime = sharedPrefUtil.getString(getApplication(),
						ShareFile.USERFILE, ShareFile.QIANDAODOWNDATE,
						"2016-01-01%2B00:00:00");
				downImgTime = sharedPrefUtil.getString(getApplication(),
						ShareFile.USERFILE, ShareFile.QIANDAODOWNIMGDATE,
						"2016-01-01 00:00:00");
				if (intent == null) {
					return;
				}
				heigh = intent.getIntExtra("heigh", 0);
				firstLogin = intent.getStringExtra("firstLogin");
				if (heigh == 0) {
					return;
				} else {
					if (heigh > 0 && heigh <= 1200) {
						imageType = "2";
					} else if (heigh > 1200 && heigh <= 1800) {
						imageType = "3";
					} else if (heigh > 1800 && heigh < 2560) {
						imageType = "4";
					}
					if ("0".equals(firstLogin)) {
						downtime = "2016-01-01%2B00:00:00";
						downImgTime = "2016-01-01 00:00:00";
					}
					LoadData();
				}
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}

	private void LoadData() {
		String downqiandaoPath = URLConstants.签到下行 + downtime;
		StringRequest request = new StringRequest(Method.GET, downqiandaoPath,
				new Listener<String>() {

					@Override
					public void onResponse(final String result) {
						new Thread(new Runnable() {
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									Gson gson = new Gson();
									try {
										QianDaoBackBean backBean = gson
												.fromJson(result,
														QianDaoBackBean.class);
										sharedPrefUtil.putString(
												getApplication(),
												ShareFile.USERFILE,
												ShareFile.QIANDAODOWNDATE,
												backBean.downTime.replace("T",
														"%2B"));
										List<QianDaoBean> beans = null;
										if (backBean.status == 0) {
											beans = backBean.list;
											if (beans != null
													&& beans.size() > 0) {
												for (QianDaoBean bean : beans) {
													int count = app
															.CheckCountQianDaoData(bean.calendar);
													if (count == 0) {
														app.insertQianDaoData(
																bean.id,
																bean.integral,
																bean.calendar,
																bean.lunarCalendar,
																bean.holiday,
																bean.lunarHoliday,
																bean.solarTerms,
																bean.imgNum,
																"", "");
													} else {
														app.updateQianDaoData(
																bean.id,
																bean.integral,
																bean.calendar,
																bean.lunarCalendar,
																bean.holiday,
																bean.lunarHoliday,
																bean.solarTerms,
																bean.imgNum,
																"", "");
													}
												}
											}
										}
									} catch (JsonSyntaxException e) {
										e.printStackTrace();
										downLoadImage();
									}
								}
								downLoadImage();
							}
						}).start();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						downLoadImage();
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
		App.getHttpQueues().add(request);
		// + downImgTime.replace(" ", " ")
		// + "&imgType="+1;//imageType
	}

	private void downLoadImage() {
		String downqiandaoImgPath = URLConstants.签到图片下载 ;
//				+ "?downTime="
//				+ downImgTime + "&imgType=1";
		StringRequest request1 = new StringRequest(Method.POST,
				downqiandaoImgPath, new Listener<String>() {

					@Override
					public void onResponse(final String result) {
						new Thread(new Runnable() {
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									Gson gson = new Gson();
									try {
										QianDaoImgBackBean backBean = gson
												.fromJson(
														result,
														QianDaoImgBackBean.class);
										sharedPrefUtil.putString(
												getApplication(),
												ShareFile.USERFILE,
												ShareFile.QIANDAODOWNIMGDATE,
												backBean.downTime.replace("T",
														" "));
										List<QianDaoImgBean> beans = null;
										if (backBean.status == 0) {
											beans = backBean.list;
											if (beans != null
													&& beans.size() > 0) {
												for (QianDaoImgBean bean : beans) {
													int count = app
															.CheckCountQianDaoImgData(bean.signNum);
													if (count != 0) {
														app.updateQianDaoImgData(
																bean.signNum,
																bean.imgPath
																		.replace(
																				"\\",
																				""),
																"");
													}
													if (!"".equals(StringUtils
															.getIsStringEqulesNull(bean.imgPath))) {
														String path = URLConstants.图片
																+ bean.imgPath
																		.replace(
																				"\\",
																				"")
																+ "&imageType=10&imageSizeType=1";
														getBitmap(path);
													}
												}
												// File file = new
												// File(Environment.getExternalStorageDirectory()
												// .getPath() +
												// "/YourAppFolder/data");
												// if (file.exists()) {
												// file.delete();
												// }
												// copyFile("/data/data/com.mission.schedule/databases/data",
												// Environment.getExternalStorageDirectory().getPath()
												// + "/YourAppFolder/data");
											}
										}
									} catch (JsonSyntaxException e) {
										e.printStackTrace();
									}
								}
							}
						}).start();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
					}
				}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> map = new HashMap<String,String>();
				map.put("downTime",downImgTime);
				map.put("imgType","1");
				return map;
			}
		};
		request1.setTag("down");
		request1.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
		App.getHttpQueues().add(request1);
	}

	public Bitmap getBitmap(String url) {
		// 从内存缓存中获取图片
		Bitmap result = memoryCache.getBitmapFromCache(url);
		if (result == null) {
			// 文件缓存中获取
			result = fileCache.getImage(getApplication(),url);
			if (result == null) {
				// 从网络获取
				result = ImageGetFromIntenet.downloadBitmap(url);
				if (result != null) {
					fileCache.saveBitmap(result, url, 0);
					memoryCache.addBitmapToCache(url, result);
				}
			} else {
				// 添加到内存缓存
				memoryCache.addBitmapToCache(url, result);
			}
		}
		return result;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}

	/**
	 * 
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}
}
