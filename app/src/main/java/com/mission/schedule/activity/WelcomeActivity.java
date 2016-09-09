package com.mission.schedule.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.db.DBSourse;
import com.mission.schedule.entity.CLAdsTable;
import com.mission.schedule.service.DownQianDaoService;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.ImageFileCache;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({"SdCardPath", "HandlerLeak"})
public class WelcomeActivity extends BaseActivity implements OnClickListener {

    @ViewResId(id = R.id.background_rl)
    private RelativeLayout background_rl;// 大背景
    @ViewResId(id = R.id.date_rl)
    private RelativeLayout date_rl;// 日期大背景
    @ViewResId(id = R.id.month_tv)
    private TextView month_tv;// 今天3月
    @ViewResId(id = R.id.day_tv)
    private TextView day_tv;// 28号
    @ViewResId(id = R.id.week_tv)
    private TextView week_tv;// 星期三
    @ViewResId(id = R.id.qiandao_ll)
    private LinearLayout qiandao_ll;// 签到背景
    @ViewResId(id = R.id.tiaoguo_tv)
    private TextView tiaoguo_tv;// 跳过
    @ViewResId(id = R.id.nextfenshu_tv)
    private TextView nextfenshu_tv;// 下次签到+1
    @ViewResId(id = R.id.jieri_ll)
    private LinearLayout jieri_ll;// 节日线性布局
    @ViewResId(id = R.id.year_tv)
    private TextView year_tv;// 如 2016-02-07
    @ViewResId(id = R.id.jieri_week_tv)
    private TextView jieri_week_tv;// 周日
    @ViewResId(id = R.id.jieri_nongli_tv)
    private TextView jieri_nongli_tv;
    @ViewResId(id = R.id.jieri_tv)
    private TextView jieri_tv;
    @ViewResId(id = R.id.qiandaoadd_tv)
    private TextView qiandaoadd_tv;
    @ViewResId(id = R.id.text)
    private TextView text;
    @ViewResId(id = R.id.text1)
    private TextView text1;
    @ViewResId(id = R.id.outbackground_ll)
    private LinearLayout outbackground_ll;

    private String userId = "";// 用户id
    SharedPrefUtil sharePrefUtil;
    private static final String CACHDIR = "ImgCach";

    Context context;

    List<Map<String, String>> nongliList = new ArrayList<Map<String, String>>();

    String updateState = "1";
    App app = App.getDBcApplication();
    String downtime = "";
    String downImgTime = "";
    String today;
    String tommrow;
    String week;// 周二
    String myweek;// 星期2
    Map<String, String> mMap = null;
    String imagePath = "";
    private ImageFileCache fileCache;
    String qiandaoDate = "";
    int month;
    int day;
    int type;
    boolean qiandaoFag = false;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(WelcomeActivity.this, "出错了...",
                            Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(WelcomeActivity.this, "存储卡不可用，请确认已插入卡后再使用本程序",
                            Toast.LENGTH_SHORT).show();
                    WelcomeActivity.this.finish();

                    break;
            }
        }

    };

    @Override
    protected void setListener() {
        qiandao_ll.setOnClickListener(this);
        tiaoguo_tv.setOnClickListener(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_welcomeactivity);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void init(Bundle savedInstanceState) {
        context = this;
        sharePrefUtil = new SharedPrefUtil(this, ShareFile.USERFILE);
        qiandaoFag = false;
        fileCache = new ImageFileCache();
        userId = sharePrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "0");
        updateState = sharePrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.UPDATESTATE, "0");
        qiandaoDate = sharePrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.QIANDAODATE, DateUtil.formatDate(new Date()));
        Typeface typeFace = Typeface.createFromAsset(getAssets(),
                "fonts/pop.ttf");
        Calendar calendar = Calendar.getInstance();
        today = DateUtil.formatDate(calendar.getTime());
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 1);
        tommrow = DateUtil.formatDate(calendar.getTime());
        week = CharacterUtil.getWeekOfDate(context, DateUtil.parseDate(today));
        myweek = CharacterUtil.getWeekOfDate1(context,
                DateUtil.parseDate(today));
        month_tv.setTypeface(typeFace);
        day_tv.setTypeface(typeFace);
        week_tv.setTypeface(typeFace);
        year_tv.setTypeface(typeFace);
        jieri_week_tv.setTypeface(typeFace);
        jieri_nongli_tv.setTypeface(typeFace);
        jieri_tv.setTypeface(typeFace);
        qiandaoadd_tv.setTypeface(typeFace);
        nextfenshu_tv.setTypeface(typeFace);
        tiaoguo_tv.setTypeface(typeFace);
        text.setTypeface(typeFace);
        text1.setTypeface(typeFace);

    }

    private void LoadData() {
        if ("0".equals(userId) || "".equals(userId)) {
            new WriteDataBase().execute();
            type = 0;
            background_rl.setVisibility(View.GONE);
            handler.postDelayed(runnable, 500);
        } else {
            if (DateUtil.formatDate(new Date()).equals(qiandaoDate)) {
                background_rl.setVisibility(View.GONE);
                type = 0;
                handler.postDelayed(runnable, 500);
            } else {
                background_rl.setVisibility(View.VISIBLE);
                type = 1;
                // app.updateQianDaoImgData("20160329111518",
                // "20160320/788373.jpg", "");
                mMap = app.QueryQianDaoImgData(today, 0);
                if (mMap != null && mMap.size() > 0) {
                    imagePath = mMap.get(CLAdsTable.adsImagePath);
                    if (!"".equals(StringUtils.getIsStringEqulesNull(imagePath))) {
                        String dir = getDirectory();
                        File file = getExternalFilesDir(dir + "/"
                                + imagePath.split("/")[1]
                                + "&imageType=10&imageSizeType=1.cach");
                        if (file.exists()) {
                            Bitmap bitmap = fileCache
                                    .getImage(context, URLConstants.图片
                                            + imagePath
                                            + "&imageType=10&imageSizeType=1");
                            if (bitmap != null) {
                                background_rl
                                        .setBackgroundDrawable(new BitmapDrawable(
                                                bitmap));
                                jieri_ll.setVisibility(View.VISIBLE);
                                date_rl.setVisibility(View.GONE);
                                year_tv.setText(today);
                                jieri_week_tv.setText(week);
                                jieri_nongli_tv.setText(mMap.get(CLAdsTable.adsLDate));
                                if (!"".equals(StringUtils.getIsStringEqulesNull(mMap.get(CLAdsTable.adsHoliday)))) {
                                    jieri_tv.setText(mMap.get(CLAdsTable.adsHoliday));
                                } else {
                                    jieri_tv.setText(mMap.get(CLAdsTable.adsLHoliday));
                                }
                            } else {
                                jieri_ll.setVisibility(View.GONE);
                                date_rl.setVisibility(View.VISIBLE);
                                month_tv.setText("今天" + month + "月");
                                day_tv.setText(day + "");
                                week_tv.setText(myweek);
                                if (week.equals("周六")) {
                                    background_rl.setBackgroundColor(getResources()
                                            .getColor(R.color.Saturdaybackcolor));
                                    date_rl.setBackgroundDrawable(getResources()
                                            .getDrawable(R.drawable.bg_welcomesaturday));
                                } else if (week.equals("周日")) {
                                    date_rl.setBackgroundDrawable(getResources()
                                            .getDrawable(R.drawable.bg_welcomesunday));
                                    background_rl.setBackgroundColor(getResources()
                                            .getColor(R.color.Sundaybackcolor));
                                } else {
                                    date_rl.setBackgroundDrawable(getResources()
                                            .getDrawable(R.drawable.bg_welcomeworkday));
                                    background_rl.setBackgroundColor(getResources()
                                            .getColor(R.color.mingtian_color));
                                }
                            }
                        } else {
                            jieri_ll.setVisibility(View.GONE);
                            date_rl.setVisibility(View.VISIBLE);
                            month_tv.setText("今天" + month + "月");
                            day_tv.setText(day + "");
                            week_tv.setText(myweek);
                            if (week.equals("周六")) {
                                background_rl.setBackgroundColor(getResources()
                                        .getColor(R.color.Saturdaybackcolor));
                                date_rl.setBackgroundDrawable(getResources()
                                        .getDrawable(R.drawable.bg_welcomesaturday));
                            } else if (week.equals("周日")) {
                                date_rl.setBackgroundDrawable(getResources()
                                        .getDrawable(R.drawable.bg_welcomesunday));
                                background_rl.setBackgroundColor(getResources()
                                        .getColor(R.color.Sundaybackcolor));
                            } else {
                                date_rl.setBackgroundDrawable(getResources()
                                        .getDrawable(R.drawable.bg_welcomeworkday));
                                background_rl.setBackgroundColor(getResources()
                                        .getColor(R.color.mingtian_color));
                            }
                        }
                    } else {
                        jieri_ll.setVisibility(View.GONE);
                        date_rl.setVisibility(View.VISIBLE);
                        month_tv.setText("今天" + month + "月");
                        day_tv.setText(day + "");
                        week_tv.setText(myweek);
                        if (week.equals("周六")) {
                            background_rl.setBackgroundColor(getResources()
                                    .getColor(R.color.Saturdaybackcolor));
                            date_rl.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bg_welcomesaturday));
                        } else if (week.equals("周日")) {
                            date_rl.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bg_welcomesunday));
                            background_rl.setBackgroundColor(getResources()
                                    .getColor(R.color.Sundaybackcolor));
                        } else {
                            date_rl.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.bg_welcomeworkday));
                            background_rl.setBackgroundColor(getResources()
                                    .getColor(R.color.mingtian_color));
                        }
                    }
                    qiandaoadd_tv.setText("签到+ "
                            + mMap.get(CLAdsTable.adsScore));
                    Map<String, String> map = app.QueryQianDaoImgData(tommrow,
                            0);
                    if (map != null && map.size() > 0) {
                        nextfenshu_tv.setText(map.get(CLAdsTable.adsScore));
                    } else {
                        nextfenshu_tv.setText(1 + "");
                    }
                } else {
                    qiandaoadd_tv.setText("签到+ " + 1);
                }
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    if (getUserState().equals("1"))
                        userId = sharePrefUtil.getString(this,
                                ShareFile.USERFILE, ShareFile.USERID, "");

                    // handler.postDelayed(runnable, 3000);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
            IntenetData();
        }
    }
//	private void checkPhonePermission() {
//		if (Build.VERSION.SDK_INT >= 23) {
//			int checkstorgePhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.STORAGE);
//			if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
//				ActivityCompat.requestPermissions(WelcomeActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
//			}
//			int checkcontactPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.CONTACTS);
//			if(checkcontactPermission != PackageManager.PERMISSION_GRANTED){
//				ActivityCompat.requestPermissions(WelcomeActivity.this,new String[]{Manifest.permission.READ_CONTACTS},10001);
//			}
//			int checkLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.LOCATION);
//			if(checkLocationPermission != PackageManager.PERMISSION_GRANTED){
//				ActivityCompat.requestPermissions(WelcomeActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10001);
//			}
//			int checkPhoneStatePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.PHONE);
//			if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
//				ActivityCompat.requestPermissions(WelcomeActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},10001);
//			}
//
//		}
//	}

    private void IntenetData() {
        if (heigh == 0) {
            return;
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(context,
                            DownQianDaoService.class);
                    intent.setPackage(getPackageName());
                    intent.setAction("updateData");
                    intent.putExtra("heigh", heigh);
                    intent.putExtra("firstLogin", "1");
                    startService(intent);
                }
            }).start();
        }
    }

    @Override
    protected void setAdapter() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tiaoguo_tv:
                LoaddingData(1);
                break;
            case R.id.qiandao_ll:
                if (!qiandaoFag) {
                    qiandaoFag = true;
                    String path = URLConstants.签到 + userId + "&tp.signDate=" + today
                            + "&tp.signIntegral="
                            + qiandaoadd_tv.getText().toString().replace("签到+ ", "")
                            + "&tp.remark=android";
                    StringRequest request = new StringRequest(Method.GET, path,
                            new Listener<String>() {

                                @Override
                                public void onResponse(final String result) {
                                    qiandaoFag = false;
                                    if (!TextUtils.isEmpty(result)) {
                                        Gson gson = new Gson();
                                        try {
                                            SuccessOrFailBean bean = gson.fromJson(
                                                    result, SuccessOrFailBean.class);
                                            if (bean.status == 0) {
                                                qiandaoadd_tv.setText("签到成功");
                                            } else if (bean.status == 4) {
                                                qiandaoadd_tv.setText("已签到");
                                            } else {
                                                qiandaoadd_tv.setText("签到失败");
                                            }
                                        } catch (JsonSyntaxException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    LoaddingData(0);
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            qiandaoFag = false;
                            qiandaoadd_tv.setText("网络开小差");
                            LoaddingData(0);
                        }
                    });
                    request.setTag("qiandao");
                    request.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1.0f));
                    App.getHttpQueues().add(request);
                } else {
                    LoaddingData(0);
                }
                break;
            default:
                break;
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            LoaddingData(type);
        }
    };

    private void LoaddingData(int type) {
        Intent intent = null;
        File file = new File("/data/data/com.mission.schedule/databases/plan");
        if (file.exists() && "0".equals(updateState)) {// &&"0".equals(updateState)
            makeFilePath(Environment.getExternalStorageDirectory().getPath()
                    + "/YourAppDataFolder/", "plan");
            copyFile("/data/data/com.mission.schedule/databases/plan",
                    Environment.getExternalStorageDirectory().getPath()
                            + "/YourAppDataFolder/" + "/plan");
            new WriteDataBase().execute();
            intent = new Intent(WelcomeActivity.this, DataUpdateActivity.class);
        } else if (file.exists()) {
            makeFilePath(Environment.getExternalStorageDirectory().getPath()
                    + "/YourAppDataFolder/", "plan");
            copyFile("/data/data/com.mission.schedule/databases/plan",
                    Environment.getExternalStorageDirectory().getPath()
                            + "/YourAppDataFolder/" + "/plan");
            if ("".equals(userId) || "0".equals(userId)) {
                intent = new Intent(WelcomeActivity.this, NewTelephoneLoginActivity.class);
            } else {
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            }
        } else {
            if ("".equals(userId) || "0".equals(userId)) {
                intent = new Intent(WelcomeActivity.this, NewTelephoneLoginActivity.class);
            } else {
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            }
        }
        startActivity(intent);

        // 设置切换动画，从右边进入，左边退出
        if (type == 1) {
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
        WelcomeActivity.this.finish();
    }

    /**
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
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
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(context, filePath);
        try {
            file = getExternalFilesDir(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(Context context, String filePath) {
        File file = null;
        try {
            file = context.getExternalFilesDir(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
        }
    }

    class WriteDataBase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            toWriteData(DBSourse.dataBaseName, R.raw.data);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    /**
     * 初始化数据库
     *
     * @param dataBaseName
     * @param resourse
     */
    private void toWriteData(String dataBaseName, int resourse) {
        InputStream is = getResources().openRawResource(resourse); // 欲导入的数据库
        OutputStream fos;
        try {
            String dbpath = "/data/data/com.mission.schedule/databases/"
                    + dataBaseName;
            if (DBSourse.createFile(dbpath)) {
                fos = new FileOutputStream(dbpath);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
                SharedPreferences sp = getSharedPreferences("localchedule",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                e.putString("isDataWrite", "1");// 0为写入数据库文件
                e.commit();
                Log.d("TAG", "数据库导入成功");
            } else {
                handler.sendEmptyMessage(0);
                Log.d("TAG", "数据库导入失败");
            }
        } catch (Exception e) {
            handler.sendEmptyMessage(0);
            Log.d("TAG", "数据库导入失败");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(context);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getHttpQueues().cancelAll("qiandao");
    }

    /**
     * 取SD卡路径
     **/
    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    /**
     * 获得缓存目录
     **/
    private String getDirectory() {
        String dir = getSDPath() + "/" + CACHDIR;
        return dir;
    }
}
