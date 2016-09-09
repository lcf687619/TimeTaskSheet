package com.mission.schedule.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.mission.schedule.R;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.BackgroundBean;
import com.mission.schedule.bean.LunarCalendaTimeBackBean;
import com.mission.schedule.bean.LunarCalendaTimeBean;
import com.mission.schedule.bean.MyMessageBackBean;
import com.mission.schedule.bean.MyMessageBean;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.bean.TotalFriendsCountBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.clock.WriteAlarmClock;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.PostSendMainActivity;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.cutimage.Crop;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.fragment.NewFriendsFragment;
import com.mission.schedule.fragment.MyRepeatFragment;
import com.mission.schedule.fragment.MyScheduleFragment;
import com.mission.schedule.fragment.NewMyFoundFragment;
import com.mission.schedule.service.ClockService;
import com.mission.schedule.service.DownNongLiService;
import com.mission.schedule.service.DownQianDaoService;
import com.mission.schedule.service.MinAndMaxService;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.slidingmenu.lib.SlidingMenu;
import com.mission.schedule.slidingmenu.lib.app.SlidingFragmentActivity;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.PathFromUriUtils;
import com.mission.schedule.utils.PhotoActionHelper;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

public class MainActivity extends SlidingFragmentActivity implements
        OnClickListener {

    private LayoutInflater inflater = null;
    // 竖屏
    private RelativeLayout main_botton_rl_schedule;
    public TextView tv_schedule_count;
    public TextView tv_my_count;

    private RelativeLayout main_botton_rl_repeat;
    private RelativeLayout main_botton_rl_found;
    private RelativeLayout main_botton_rl_my;
    private Fragment[] mFragments = new Fragment[4];
    private RelativeLayout[] layouts;
    private Map<String, String> mainMap;
    /**
     * 左面菜单属性
     */
    CircularImage image_img;
    TextView tv_memberlogin, tixingname_tv, hidden_tv;// , notification_tv
    Button exit_btn, tixing_btn, jifenduihuan_btn, help_btn, set_btn,
            todayend_bt;
    ImageView member_relative;
    LinearLayout tixing_ll, todayend_ll;
    // RelativeLayout img_rl;
    RelativeLayout help_rl;
    String untaskend;
    Bitmap backgroundbitmap = null;

    private String paths;
    private String clipPath = "";// 裁剪过后的图片路径
    private int picW;
    // private ImageLoadingListener animateFirstListener = new
    // AnimateFirstDisplayListener();
    Context context;
    SharedPrefUtil sharedPrefUtil;
    private LinearLayout top_ll_left;
    int myIndex = 0;
    // 导航栏底部
    private TextView tv_schedule, tv_myrepeat, tv_found, tv_my;

    static String date = "";
    Bitmap bit = null;
    String path;
    int displaypixels;

    String userId;
    String backgroundname;
    int mScreenWidth;
    int mScreenHeight;

    private HomeKeyEventReceiver keyEventReceiver;
    String myfriendscount;

    String updatesettime;// 设置的更新时间
    private WakeLock wakeLock = null;
    String isYouKe = "1";
    List<Map<String, String>> nongliList = new ArrayList<Map<String, String>>();
    String downtagtime = "";// 新标签下载时间
    int heigh = 0;
    Bitmap headbitmap = null;
    private String mOutputPath;
    private String mDemoPath;
    ActivityManager1 activityManager = null;
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mymain1);
        activityManager = ActivityManager1.getInstance();
        activityManager.addActivities(this);
        EventBus.getDefault().register(this);
        context = this;
        inflater = LayoutInflater.from(this);
        picW = Utils.dipTopx(this, 80);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.img_null_smal)
                .showImageForEmptyUri(R.mipmap.img_null_smal)
                .showImageOnFail(R.mipmap.img_null_smal).cacheInMemory(true)
                .cacheOnDisc(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                .build();
        imageLoader = ImageLoader.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        userId = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "0");
        myfriendscount = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.COUNT, "0");
        isYouKe = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.ISYOUKE, "1");
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.QIANDAODATE, DateUtil.formatDate(new Date()));
        String background = sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.USERBACKGROUNDPATH, "");
        if ("".equals(StringUtils.getIsStringEqulesNull(background))) {
            path = URLConstants.默认背景图片;
        } else {
            path = URLConstants.默认背景图片 + "=" + background;
        }
        date = DateUtil.formatDateTimeSs(new Date());
        int year = Integer.parseInt(date.substring(0, 4).toString()) - 1;
        String beforeDownTime = String.valueOf(year)
                + date.substring(4, 19).toString();
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.OLDUPDATETIME, beforeDownTime);
        updatesettime = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.UPDATESETTIME, "");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        displaypixels = dm.widthPixels * dm.heightPixels;
        // mMenu.setMenuEnable(1);
        loadview();
        initview();
        initdata();
        // downFocusSch();
        guoqipostpone();
        CheckCreateRepeatSchData();
        StartMaxAndMin();
        registerHomeKeyReceiver();// 注册监听按下home键
        initDate();
        if (!"0".equals(sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.FIRSTLOGIN, "0"))) {
            downNongLiData();
        } else {
            IntenetData();
            Intent intent = new Intent(context, DownNongLiService.class);
            intent.setAction("com.mission.schedule.service.DownNongLiService");
            intent.setPackage(getPackageName());
            startService(intent);
        }
        // sharedPrefUtil.putString(context, ShareFile.USERFILE,
        // ShareFile.MAINONCREATE, "1");
        setFragmentIndicator(0);
        // everyDayExecute();
    }

    private void IntenetData() {
        heigh = Utils.dipTopx(context,
                (Utils.pxTodip(context, mScreenHeight) / 11) * 10);
        if (heigh == 0) {
            return;
        } else {
            Intent intent = new Intent(context, DownQianDaoService.class);
            intent.putExtra("heigh", heigh);
            intent.putExtra("firstLogin", "0");
            intent.setAction("updateData");
            intent.setPackage(getPackageName());
            startService(intent);
        }
    }

    private void StartMaxAndMin() {
        Intent intent = new Intent(context, MinAndMaxService.class);
        intent.setAction("com.mission.schedule.service.MinAndMaxService");
        intent.setPackage(getPackageName());
        intent.putExtra("myfrist", "0");// 0正常进入 1最小化
        startService(intent);
    }

    private void loadview() {
        // 设置左侧滑动菜单
        setBehindContentView(R.layout.layout_menu);
        // 实例化滑动菜单对象
        SlidingMenu menu = getSlidingMenu();// new SlidingMenu(this);
        // 设置为左滑菜单
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        // 设置滑动阴影的宽度
        menu.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动阴影的图像资源
        menu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单划出时主页面显示的剩余宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置下方视图的在滚动时的缩放比例
        // menu.setBehindScrollScale(0.0f);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        // 附加在Activity上
        // menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // 设置滑动菜单的布局
        // menu.setMenu(R.layout.layout_menu);
    }

    private void initview() {
        tv_schedule = (TextView) this.findViewById(R.id.tv_schedule);
        tv_myrepeat = (TextView) this.findViewById(R.id.tv_myrepeat);
        tv_found = (TextView) this.findViewById(R.id.tv_found);
        tv_my = (TextView) this.findViewById(R.id.tv_my);
        /******** 左边菜单 *************************************************/
        member_relative = (ImageView) this.findViewById(R.id.member_relative);
        member_relative.setOnClickListener(this);
        image_img = (CircularImage) this.findViewById(R.id.image_img);
        image_img.setOnClickListener(this);
        tv_memberlogin = (TextView) this.findViewById(R.id.tv_memberlogin);
        exit_btn = (Button) findViewById(R.id.exit_btn);
        exit_btn.setOnClickListener(this);
        tixing_btn = (Button) this.findViewById(R.id.tixing_btn);
        tixing_btn.setOnClickListener(this);
        help_btn = (Button) findViewById(R.id.help_btn);
        help_btn.setOnClickListener(this);
        jifenduihuan_btn = (Button) findViewById(R.id.jifenduihuan_btn);
        jifenduihuan_btn.setOnClickListener(this);
        set_btn = (Button) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
        todayend_ll = (LinearLayout) findViewById(R.id.todayend_ll);
        todayend_ll.setOnClickListener(this);
        tixing_ll = (LinearLayout) findViewById(R.id.tixing_ll);
        tixing_ll.setOnClickListener(this);
        tixingname_tv = (TextView) findViewById(R.id.tixingname_tv);
        hidden_tv = (TextView) findViewById(R.id.hidden_tv);
        hidden_tv.setOnClickListener(this);
        todayend_bt = (Button) findViewById(R.id.todayend_bt);
        todayend_bt.setOnClickListener(this);
        // notification_tv = (TextView) findViewById(R.id.notification_tv);
        // notification_tv.setOnClickListener(this);
        help_rl = (RelativeLayout) findViewById(R.id.help_rl);
        help_rl.setOnClickListener(this);
        // img_rl = (RelativeLayout) findViewById(R.id.img_rl);

        setHeadView();
    }

    private void setHeadView() {
        LayoutParams params = (LayoutParams) member_relative.getLayoutParams();
        params.height = mScreenWidth - Utils.dipTopx(context, 100);
        member_relative.setLayoutParams(params);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) image_img
                .getLayoutParams();
        params2.topMargin = mScreenWidth - Utils.dipTopx(context, 150);
        image_img.setLayoutParams(params2);
        RelativeLayout.LayoutParams params3 = (LayoutParams) tv_memberlogin
                .getLayoutParams();
        params3.topMargin = mScreenWidth - Utils.dipTopx(context, 130);
        tv_memberlogin.setLayoutParams(params3);

    }

    @SuppressLint("NewApi")
    private void initdata() {
        String image_head = sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.USERPHOTOPATH, "");
        String imageUrl = URLConstants.图片 + image_head + "&imageType=2&imageSizeType=3";
//        FileUtils.loadRoundHeadImg(this, ParameterUtil.userHeadImg, image_img,
//                imageUrl);
        imageLoader.displayImage(imageUrl, image_img, options,
                animateFirstListener);
        imageLoader.displayImage(path, member_relative, options, animateFirstListener);
        tv_memberlogin.setText(sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.USERNAME, ""));

//        try {
//            new Thread(new Runnable() {
//                public void run() {
//                    // bit=StreamTool.getHttpBitmap(path,displaypixels);
//                    bit = getBitmap(path);
//                    Message message = new Message();
//                    message.what = 0;
////                    message.obj = bit;
//                    handler.sendMessage(message);
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String ringstate = sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.RINGSTATE, "0");
        if (ringstate.equals("0")) {
            tixingname_tv.setText("语音");
            tixingname_tv.setTextColor(getResources().getColor(
                    R.color.mingtian_color));
        } else if (ringstate.equals("1")) {
            tixingname_tv.setText("静音");
            tixingname_tv.setTextColor(getResources().getColor(
                    R.color.sunday_txt));
        } else if (ringstate.equals("2")) {
            tixingname_tv.setText("短音");
            tixingname_tv.setTextColor(getResources().getColor(
                    R.color.mingtian_color));
        }
        untaskend = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.UNTASKEND, "0");
        if ("0".equals(untaskend)) {
            hidden_tv.setText("显示");
            hidden_tv.setTextColor(context.getResources().getColor(
                    R.color.mingtian_color));
        } else {
            hidden_tv.setText("隐藏");
            hidden_tv.setTextColor(context.getResources().getColor(
                    R.color.sunday_txt));
        }

        if ("5.0.0".equals(getVersion())) {
            app.deleteCLAdsTable();
        } else if ("5.1.0".equals(getVersion())) {
            app.deleteCLAdsTable();
        }
        init();
    }

    /**
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
     */
    public String getVersion() {
        String version = "5.5.5";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static void guoqipostpone() {
        List<Map<String, String>> guoqiList;
        try {
            guoqiList = app.queryAllSchData(19, 0, 0);
            if (guoqiList != null && guoqiList.size() > 0) {
                for (Map<String, String> map : guoqiList) {
                    if ("1".equals(map.get(ScheduleTable.schIsPostpone))) {
                        if (DateUtil.parseDate(map.get(ScheduleTable.schDate))
                                .before(DateUtil.parseDate(DateUtil
                                        .formatDate(new Date())))) {
                            date = DateUtil.formatDate(new Date());
                            app.updateScheduleDateData(Integer.parseInt(map
                                    .get(ScheduleTable.schID)), date, map
                                    .get(ScheduleTable.schTime));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public Bitmap getBitmap(String url) {
//        // 从内存缓存中获取图片
//        Bitmap result = memoryCache.getBitmapFromCache(url);
//        if (result == null) {
//            // 文件缓存中获取
//            result = fileCache.getImage(context,url);
//            if (result == null) {
//                // 从网络获取
//                result = ImageGetFromHttp.downloadBitmap(url, mScreenWidth
//                                - Utils.dipTopx(context, 100),
//                        mScreenWidth - Utils.dipTopx(context, 100));
//                if (result != null) {
//                    fileCache.saveBitmap(context,result, url, 1);
//                    memoryCache.addBitmapToCache(url, result);
//                }
//            } else {
//                // 添加到内存缓存
//                memoryCache.addBitmapToCache(url, result);
//            }
//        }
//        return result;
//    }

    private void init() {
        layouts = new RelativeLayout[4];
        main_botton_rl_schedule = (RelativeLayout) findViewById(R.id.main_botton_rl_schedule);
        main_botton_rl_schedule.setOnClickListener(this);
        layouts[0] = main_botton_rl_schedule;

        tv_schedule_count = (TextView) findViewById(R.id.tv_schedule_count);
        tv_my_count = (TextView) this.findViewById(R.id.tv_my_count);

        main_botton_rl_repeat = (RelativeLayout) findViewById(R.id.main_botton_rl_repeat);
        main_botton_rl_repeat.setOnClickListener(this);
        layouts[1] = main_botton_rl_repeat;

        main_botton_rl_found = (RelativeLayout) findViewById(R.id.main_botton_rl_found);
        main_botton_rl_found.setOnClickListener(this);
        layouts[2] = main_botton_rl_found;

        main_botton_rl_my = (RelativeLayout) findViewById(R.id.main_botton_rl_my);
        main_botton_rl_my.setOnClickListener(this);
        layouts[3] = main_botton_rl_my;
        // loadCount();
    }

    private void loadCount() {

        int noEndCount = App.getDBcApplication().QueryNowGuoQiWeiJieShuCount();// Integer.parseInt(mainMap.get("noEndCount"));
        if (noEndCount == 0) {
            tv_schedule_count.setVisibility(View.GONE);
        } else {
            tv_schedule_count.setText(noEndCount + "");
            tv_schedule_count.setVisibility(View.VISIBLE);
        }
        // 好友统计数量
        String friendsCountPath = URLConstants.统计好友操作数量 + "?uId=" + userId;
        FriendsTotalAsync(friendsCountPath);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // acquireWakeLock();
        top_ll_left = null;
        if (myIndex == 0) {
            top_ll_left = MyScheduleFragment.top_ll_left;
            if (top_ll_left != null) {
                top_ll_left.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        toggle();
                    }
                });
            }
        } else if (myIndex == 1) {
            top_ll_left = MyRepeatFragment.top_ll_left;
            if (top_ll_left != null) {
                top_ll_left.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        toggle();
                    }
                });
            }
        } else if (myIndex == 2) {
            top_ll_left = NewMyFoundFragment.top_ll_left;
            if (top_ll_left != null) {
                top_ll_left.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        toggle();
                    }
                });
            }
        } else {
            top_ll_left = NewFriendsFragment.top_ll_left;
            if (top_ll_left != null) {
                top_ll_left.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        toggle();
                    }
                });
            }
        }
    }

    private void isNetWork() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                    Intent intent = new Intent(context, UpLoadService.class);
                    intent.setAction(Const.SHUAXINDATA);
                    intent.setPackage(getPackageName());
                    startService(intent);
                } else {
                    return;
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String image_head = sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.USERPHOTOPATH, "");
        String imageUrl = URLConstants.图片 + image_head + "&imageType=2&imageSizeType=3";
        imageLoader.displayImage(imageUrl, image_img, options, animateFirstListener);
        tv_memberlogin.setText(sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.USERNAME, ""));
        final String path1;
        String backgroundname = sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.USERBACKGROUNDPATH, "");
        if ("".equals(StringUtils.getIsStringEqulesNull(backgroundname))) {
            path1 = URLConstants.默认背景图片;
        } else {
            path1 = URLConstants.默认背景图片 + "=" + backgroundname;
        }
        imageLoader.displayImage(path1, member_relative, options, animateFirstListener);
        String openstylestate = sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.OPENSTYLESTATE, "0");
        if ("0".equals(openstylestate) || "1".equals(openstylestate)) {
            String firstlogin = sharedPrefUtil.getString(context,
                    ShareFile.USERFILE, ShareFile.FIRSTLOGIN, "0");
            if (!"0".equals(firstlogin)) {
                isNetWork();
            }
        }
        if ("1".equals(openstylestate)) {
            guoqipostpone();
            CheckCreateRepeatSchData();
        }
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.OPENSTYLESTATE, "2");
        String refresh = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.REFRESHFRIEND, "0");
        if (!"0".equals(refresh)) {
            EventBus.getDefault().post(new FristFragment("3"));
            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                    ShareFile.REFRESHFRIEND, "0");
        }
        JPushInterface.onResume(context);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (keyEventReceiver == null) {
            registerHomeKeyReceiver();// 注册监听home键按下
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (keyEventReceiver != null) {
            unregisterReceiver(keyEventReceiver);
            keyEventReceiver = null;
        }
        JPushInterface.onPause(context);
    }

    /**
     * 页面适配器
     */
    // public class FragmentAdapter extends FragmentPagerAdapter {
    //
    // public FragmentAdapter(FragmentManager fm) {
    // super(fm);
    // }
    //
    // @Override
    // public Fragment getItem(int arg0) {
    // return fragments.get(arg0);
    // }
    //
    // @Override
    // public int getCount() {
    // return fragments.size();
    // }
    //
    // }
    @Override
    public void onClick(View v) {
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(context,
                ShareFile.USERFILE);
        switch (v.getId()) {
            case R.id.member_relative:
                // 更换左边菜单的背景图
                dialogShow();
                break;
            case R.id.image_img:
                isYouKe = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                        ShareFile.ISYOUKE, "1");
                // 点击头像进入到个人详细信息
                if ("1".equals(isYouKe)) {
                    startActivity(new Intent(context, PersonMessageActivity.class));
                } else {
                    startActivity(new Intent(context, NewResiginActivity.class));
                }
                break;
            case R.id.jifenduihuan_btn:// 积分兑换
                startActivity(new Intent(context, JiFenDuiHuanActivity.class));
                break;
            case R.id.exit_btn:
                try {
                    isYouKe = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                            ShareFile.ISYOUKE, "1");
                    if ("1".equals(isYouKe)) {
                        List<Map<String, String>> unSchUpdateList = app.queryAllSchData(-1, 0, 0);
                        List<Map<String, String>> unRepUpdateList = app.QueryAllChongFuData(2);
                        if ((unSchUpdateList != null && unSchUpdateList.size() > 0) || (unRepUpdateList != null && unRepUpdateList.size() > 0)) {
                            startActivity(new Intent(context,ExitAlterActivity.class));
                        } else {
                            WriteAlarmClock.clearAlarmClock(getApplicationContext());
                            if (!"".equals(sharedPrefUtil.getString(context,
                                    ShareFile.USERFILE, ShareFile.TELEPHONE, ""))) {
                                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                        ShareFile.TELEPHONE, "");
                            } else {
                                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                        ShareFile.USEREMAIL, "");
                            }
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.USERID, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.USERNAME, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.USERPHOTOPATH, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.USERSTATE, "0");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.FIRSTLOGIN, "0");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.U_ACC_NO, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.MAXFOCUSID, "0");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.OUTWEEKFAG, "0");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.UPDATESETTIME, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.DOWNSCHTIME, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.DOWNREPTIME, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.RINGSTATE, "0");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.ISYOUKE, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.DOWNTAGDATE, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.OPENSTYLESTATE, "2");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.FRIENDDOWNSCHTIME, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.FRIENDDOWNRepTIME, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.FRIENDDOWNOldTIME, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.PUSH_ALIAS, "0");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.REFRESHFRIEND, "0");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.FIRSTDOWNFOCUSSCH, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.SHOUCANGDATA, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.DOWNNEWFOCUSSHARESCHDATE, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.DOWNNEWFOCUSSHAREREPDATE, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.KuaiJieSouSuo, "");
                            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                    ShareFile.SHAREDATA, "");
                            ClearMAC();
                            // RuntimeCmdManager.clearAppUserData(getPackageName());
                            startActivity(new Intent(context, NewTelephoneLoginActivity.class));
                            activityManager.doAllActivityFinish();
                        }
                    } else {
                        startActivity(new Intent(context, LogoutActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tixing_ll: // 提醒模式
                alterRingDialog();
                break;
            case R.id.help_btn:
                startActivity(new Intent(context, HelpActivity.class));
                break;
            // case R.id.notification_tv:
            // startActivity(new Intent(context, HelpActivity.class));
            // break;
            case R.id.help_rl:
                startActivity(new Intent(context, HelpActivity.class));
                break;
            case R.id.tixing_btn: // 提醒模式
                alterRingDialog();
                break;
            case R.id.set_btn:
                startActivity(new Intent(context, SettingActivity.class));
                break;
            case R.id.todayend_ll:
                untaskend = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                        ShareFile.UNTASKEND, "0");
                if ("0".equals(untaskend)) {
                    hidden_tv.setText("显示");
                    hidden_tv.setTextColor(context.getResources().getColor(
                            R.color.mingtian_color));
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.UNTASKEND, "1");
                } else {
                    hidden_tv.setText("隐藏");
                    hidden_tv.setTextColor(context.getResources().getColor(
                            R.color.sunday_txt));
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.UNTASKEND, "0");
                }
                break;
            case R.id.hidden_tv:
                untaskend = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                        ShareFile.UNTASKEND, "0");
                if ("0".equals(untaskend)) {
                    hidden_tv.setText("显示");
                    hidden_tv.setTextColor(context.getResources().getColor(
                            R.color.mingtian_color));
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.UNTASKEND, "1");
                } else {
                    hidden_tv.setText("隐藏");
                    hidden_tv.setTextColor(context.getResources().getColor(
                            R.color.sunday_txt));
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.UNTASKEND, "0");
                }
                break;
            case R.id.todayend_bt:
                untaskend = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                        ShareFile.UNTASKEND, "0");
                if ("0".equals(untaskend)) {
                    hidden_tv.setText("显示");
                    hidden_tv.setTextColor(context.getResources().getColor(
                            R.color.mingtian_color));
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.UNTASKEND, "1");
                } else {
                    hidden_tv.setText("隐藏");
                    hidden_tv.setTextColor(context.getResources().getColor(
                            R.color.sunday_txt));
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.UNTASKEND, "0");
                }
                break;
            case R.id.main_botton_rl_schedule:
                checkBoxState(0);
                myIndex = 0;
                onStart();
                break;

            case R.id.main_botton_rl_repeat:
                checkBoxState(1);
                myIndex = 1;
                onStart();
                break;

            case R.id.main_botton_rl_found:
                checkBoxState(2);
                myIndex = 2;
                onStart();
                break;

            case R.id.main_botton_rl_my:
                checkBoxState(3);
                myIndex = 3;
                onStart();
                break;
            default:
                break;
        }
    }

    int[] bottom_none_choose = new int[]{R.mipmap.btn_richeng2,
            R.mipmap.btn_chongfu2, R.mipmap.btn_faxian2,
            R.mipmap.btn_haoyou2};
    int[] bottom_choose = new int[]{R.mipmap.btn_richeng1,
            R.mipmap.btn_chongfu1, R.mipmap.btn_faxian1,
            R.mipmap.btn_haoyou1};

    private void checkBoxState(int index) {
        setFragmentIndicator(index);
        for (int i = 0; i < layouts.length; i++) {
            ImageView imageView = (ImageView) ((RelativeLayout) layouts[i]
                    .getChildAt(0)).getChildAt(1);
            if (i == index) {
                imageView.setImageResource(bottom_choose[i]);
            } else {
                imageView.setImageResource(bottom_none_choose[i]);
            }
        }

    }

    private void setFragmentIndicator(int whichIsDefault) {
        if (whichIsDefault == 0) {
            tv_schedule.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang));
            tv_myrepeat.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_found.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_my.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            EventBus.getDefault().post(new FristFragment("0"));
        } else if (whichIsDefault == 1) {
            tv_schedule.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_myrepeat.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang));
            tv_found.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_my.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            EventBus.getDefault().post(new FristFragment("1"));
        } else if (whichIsDefault == 2) {
            tv_schedule.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_myrepeat.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_found.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang));
            tv_my.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            EventBus.getDefault().post(new FristFragment("2"));
        } else {
            tv_schedule.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_myrepeat.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_found.setTextColor(getResources().getColor(
                    R.color.text_dibudaohang_moren));
            tv_my.setTextColor(getResources()
                    .getColor(R.color.text_dibudaohang));
            EventBus.getDefault().post(new FristFragment("3"));
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragments[0] = fragmentManager
                .findFragmentById(R.id.fragment_MySchedule);
        mFragments[1] = fragmentManager
                .findFragmentById(R.id.fragment_MyRepeat);
        mFragments[2] = fragmentManager.findFragmentById(R.id.fragment_MyFound);
        mFragments[3] = fragmentManager.findFragmentById(R.id.fragment_my);
        fragmentManager.beginTransaction().hide(mFragments[0])
                .hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3])
                .show(mFragments[whichIsDefault]).commit();
    }

    public boolean setPrivateXml(String fileName, String key, String val) {
        try {
            SharedPreferences sp = getSharedPreferences(fileName, MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putString(key, val);
            e.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
////                    headbitmap = (Bitmap) msg.obj;
//                    headbitmap = bit;
//                    // bitmap = ImageUtils.zoomBitmap(bitmap,
//                    // mScreenWidth - Utils.dipTopx(context, 100),
//                    // mScreenWidth - Utils.dipTopx(context, 100));
//                    member_relative
//                            .setImageDrawable(new BitmapDrawable(headbitmap));
//                    // .setBackgroundDrawable(new BitmapDrawable(bitmap));
//                    // bitmap.recycle();
//                    break;
//
//                default:
//                    break;
//            }
        }

    };
    private boolean isClose;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
            if (!isClose) {
                isClose = true;
                Toast.makeText(this, "再按一次返回键关闭程序", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, 5000);
                return true;
            } else {
                handler.removeCallbacks(runnable);
                final Window win = getWindow();
                win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.OUTWEEKFAG, "0");
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.OPENSTYLESTATE, "1");
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.REFRESHFRIEND, "0");
                initDate();
                WriteAlarmClock.writeAlarm(getApplicationContext());
                activityManager.doAllActivityFinish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            isClose = false;
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if (mUploadPicHttpTask != null && !mUploadPicHttpTask.isCancelled())
        // {
        // mUploadPicHttpTask.cancel(true);
        // }
        if (headbitmap != null) {
            headbitmap.recycle();
        }
        if (bit != null) {
            bit.recycle();
        }
        bit = null;
        if (backgroundbitmap != null) {
            backgroundbitmap.recycle();
        }
        backgroundbitmap = null;
        MyScheduleFragment.top_ll_left = null;
        MyRepeatFragment.top_ll_left = null;
        NewMyFoundFragment.top_ll_left = null;
        NewFriendsFragment.top_ll_left = null;
        top_ll_left = null;
        App.getHttpQueues().cancelAll("down");
        App.getHttpQueues().cancelAll("downtag");
        EventBus.getDefault().unregister(this);
//        handler.removeCallbacksAndMessages(null);
        // android.os.Process.killProcess(android.os.Process.myPid());
        // releaseWakeLock();
    }

    private void dialogShow() {
        Dialog dialog = new Dialog(this, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(this);
        View more_pop_menu = fac
                .inflate(R.layout.dialog_selecttypecamera, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
        dialog.show();

        new MyClickClass(more_pop_menu, dialog);
    }

    class MyClickClass implements View.OnClickListener {

        private Dialog dialog;
        private Button dialog_button_photo;
        // private Button dialog_button_moren;
        private Button dialog_button_cancel;

        public MyClickClass(View view, Dialog dialog) {
            this.dialog = dialog;
            dialog_button_photo = (Button) view
                    .findViewById(R.id.dialog_button_photo);
            dialog_button_photo.setOnClickListener(this);
            // dialog_button_moren = (Button) view
            // .findViewById(R.id.dialog_button_moren);
            // dialog_button_moren.setOnClickListener(this);
            dialog_button_cancel = (Button) view
                    .findViewById(R.id.dialog_button_cancel);
            dialog_button_cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Intent intent = null;
            switch (v.getId()) {
                case R.id.dialog_button_photo:
                    // ImageCutUtils.openLocalImage(MainActivity.this);
//				ImageCutTools.getInstance().selectPicture();
                    Crop.pickImage(MainActivity.this);
                    break;
                case R.id.dialog_button_cancel:
                    break;
            }
            dialog.dismiss();
        }
    }

    /* 根据uri返回文件路径 */
    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        return cursor.getString(column_index);
    }

    public final static Bitmap lessenUriImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回 bm 为空
        options.inJustDecodeBounds = false; // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = (int) (options.outHeight / (float) 320);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; // 重新读入图片，注意此时已经把 options.inJustDecodeBounds
        // 设回 false 了
        bitmap = BitmapFactory.decodeFile(path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + " " + h); // after zoom
        return bitmap;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (requestCode == ImageCutUtils.GET_IMAGE_FROM_PHONE) {
        // if (resultCode == Activity.RESULT_OK) {
        // if (data != null && data.getData() != null) {
        // // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
        // // iv.setImageURI(data.getData());
        //
        // // 对图片进行裁剪
        // ImageCutUtils.cropImage(this, data.getData());
        // }
        // }
        // } else if (requestCode == ImageCutUtils.CROP_IMAGE) {
        // if (resultCode == Activity.RESULT_OK) {
        // if (ImageCutUtils.cropImageUri != null) {
        // Bitmap photo = null;
        // Uri photoUri = data.getData();
        // // 可以直接显示图片,或者进行其他处理(如压缩等)
        // if (photoUri != null) {
        // photo = BitmapFactory.decodeFile(photoUri.getPath());
        // }
        // if (photo == null) {
        // String filepath = getPath(ImageCutUtils.cropImageUri);
        // photo = lessenUriImage(filepath);
        // // Bundle extra = data.getExtras();
        // // if (extra != null) {
        // // photo = (Bitmap) extra.get("data");
        // ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        // // 将选择的图片放在临时文件夹中
        // File f = new File(ParameterUtil.saveImgTempPath);
        // if (!f.exists()) {
        // f.mkdirs();
        // }
        //
        // String imageName = System.currentTimeMillis()
        // + (int) (Math.random() * 10000) + ".jpg";
        //
        // byte[] array = stream.toByteArray();
        // File ff = new File(ParameterUtil.saveImgTempPath
        // + imageName);
        //
        // FileOutputStream outStream;
        // try {
        // outStream = new FileOutputStream(ff);
        // outStream.write(array);
        // outStream.flush();
        // outStream.close();
        // } catch (FileNotFoundException ee) {
        // ee.printStackTrace();
        // } catch (IOException ee) {
        // ee.printStackTrace();
        // }
        // String imageNameJpeg = System.currentTimeMillis()
        // + (int) (Math.random() * 10000) + ".jpg";
        //
        // Bitmap bitmapJpeg = BitmapFactory
        // .decodeFile(ParameterUtil.saveImgTempPath
        // + imageName);
        // ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        // bitmapJpeg.compress(Bitmap.CompressFormat.JPEG, 95,
        // byteOut);
        // byte[] btyeArray = byteOut.toByteArray();
        // File fJpeg = new File(ParameterUtil.saveImgTempPath
        // + imageNameJpeg);
        //
        // clipPath = fJpeg.getAbsolutePath();// 获得裁剪后压缩的图片路径
        //
        // FileOutputStream outStreamJpeg;
        // try {
        // outStreamJpeg = new FileOutputStream(fJpeg);
        // outStreamJpeg.write(btyeArray);
        // outStreamJpeg.flush();
        // outStreamJpeg.close();
        // } catch (FileNotFoundException ee) {
        // ee.printStackTrace();
        // } catch (IOException ee) {
        // ee.printStackTrace();
        // }
        //
        // File file = new File(ParameterUtil.saveImgTempPath
        // + imageName);
        // if (file.exists()) {
        // file.delete();// 删除中间图片
        // }
        // paths = clipPath;
        // image_img.setImageBitmap(photo);
        //
        // uploadJvBaoContent();
        // }
        // }
        // }
        // }
        Uri uri = null;
//		if (requestCode == AppConstant.KITKAT_LESS) {
//			if (resultCode == Activity.RESULT_OK) {
//				uri = data.getData();
//				Log.d("tag", "uri=" + uri);
//				// 调用裁剪方法
//				ImageCutTools.getInstance().cropPicture(this, uri);
//			}
//		} else if (requestCode == AppConstant.KITKAT_ABOVE) {
//			if (resultCode == Activity.RESULT_OK) {
//				uri = data.getData();
//				Log.d("tag", "uri=" + uri);
//				// 先将这个uri转换为path，然后再转换为uri
//				String thePath = ImageCutTools.getInstance().getPath(this, uri);
//				ImageCutTools.getInstance().cropPicture(this,
//						Uri.fromFile(new File(thePath)));
//			}
//		} else if (requestCode == AppConstant.INTENT_CROP) {
//			if (resultCode == Activity.RESULT_OK) {
//				backgroundbitmap = data.getParcelableExtra("data");
//				File temp = new File(Environment.getExternalStorageDirectory()
//						.getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
//				if (!temp.exists()) {
//					temp.mkdir();
//				}
//				File tempFile = new File(temp.getAbsolutePath() + "/"
//						+ Calendar.getInstance().getTimeInMillis() + ".jpg"); // 以时间秒为文件名
//				// 图像保存到文件中
//				FileOutputStream foutput = null;
//				try {
//					foutput = new FileOutputStream(tempFile);
//					if (backgroundbitmap.compress(Bitmap.CompressFormat.JPEG,
//							100, foutput)) {
//						paths = tempFile.getAbsolutePath();
//						// Toast.makeText(
//						// context,
//						// "已生成缓存文件，等待上传！文件位置："
//						// + tempFile.getAbsolutePath(),
//						// Toast.LENGTH_LONG).show();
//					}
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						uploadJvBaoContent();
//					} else {
//						alertFailDialog(0);
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//		}
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                File temp = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
                if (!temp.exists()) {
                    temp.mkdir();
                }
                String fileName = "Temp_" + String.valueOf(System.currentTimeMillis()) + ".png";
                File cropFile = new File(temp, fileName);
                Uri outputUri = Uri.fromFile(cropFile);
                mOutputPath = outputUri.getPath();
                mDemoPath = PathFromUriUtils.getPath(context, data.getData());
                PhotoActionHelper.clipImage(this, PersonPhotoClipActivity.class).input(mDemoPath).output(mOutputPath)
                        .requestCode(Const.REQUEST_CLIP_IMAGE).start();
//                beginCrop( data.getData());
            }
            if (data != null
                    && (requestCode == Const.REQUEST_CLIP_IMAGE || requestCode == Const.REQUEST_TAKE_PHOTO)) {
                String path = PhotoActionHelper.getOutputPath(data);
                if (path != null) {
                    paths = path;
                    backgroundbitmap = BitmapFactory.decodeFile(path);
                    if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                        uploadJvBaoContent();
                    } else {
                        alertFailDialog(0);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadJvBaoContent() {
        if (NetUtil.getConnectState(this) == NetWorkState.NONE) {
            Toast.makeText(context, "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
            ;
        } else {
            final String imagepath = paths;
            backgroundname = paths.substring(paths.lastIndexOf("/") + 1,
                    paths.length());
            HttpUtils httpUtils = new HttpUtils(10000);
            final ProgressUtil progressUtil = new ProgressUtil();
            progressUtil.ShowProgress(context, true, true, "正在上传......");
            String upImagePath = URLConstants.修改背景图片;
            RequestParams params = new RequestParams();
            params.addBodyParameter("doc", new File(imagepath));
            params.addBodyParameter("id", userId);
            params.addBodyParameter("docFilename", backgroundname);
            httpUtils.send(HttpMethod.POST, upImagePath, params,
                    new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException e, String msg) {
                            progressUtil.dismiss();
                            Toast.makeText(context, "上传失败！", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            progressUtil.dismiss();
                            if (!TextUtils.isEmpty(responseInfo.result)) {
                                Gson gson = new Gson();
                                BackgroundBean bean;
                                try {
                                    bean = gson.fromJson(responseInfo.result,
                                            BackgroundBean.class);
                                    if (bean.state.equals("0")) {
                                        member_relative
                                                .setImageBitmap(backgroundbitmap);
                                        setResult(1);
                                        UpdateMyMessage();
                                    } else if (bean.state.equals("1")) {
                                        alertFailDialog(1);
                                    } else {
                                        Toast.makeText(context,
                                                responseInfo.result,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(context, "上传失败！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void alertFailDialog(int type) {
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.show();
        Window window = builder.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);// 设置生效
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_alert_ok);
        TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
        TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
        if (type == 0) {
            delete_tv.setText("请检查您的网络！");
        } else {
            delete_tv.setText("头像上传失败！");
        }
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });

    }

    /**
     * 执行请求
     *
     * @param requestMethod
     * @param taskParams
     * @param taskHandler
     * @param requestCode
     * @return
     */
    // public HttpAsyncTask request2Server(int requestMethod,
    // final HttpTaskParams taskParams, HttpTaskHandler taskHandler,
    // int requestCode) {
    // HttpAsyncTask task = new HttpAsyncTask(requestMethod, this,
    // taskHandler, requestCode);
    // task.execute(taskParams);
    // return task;
    // }

    /**
     * 按home键进行个人信息同步
     *
     * @author lenovo
     */
    private void UpdateMyMessage() {
        // new MyMessageAsync().execute(URLConstants.查询个人信息 + "?uid=" + userId);
        String path = URLConstants.查询个人信息 + "?uid=" + userId;
        StringRequest request = new StringRequest(Request.Method.GET, path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            Gson gson = new Gson();
                            try {
                                MyMessageBackBean backBean = gson.fromJson(s,
                                        MyMessageBackBean.class);
                                if (backBean.status == 0) {
                                    MyMessageBean bean = null;
                                    if (backBean.list != null
                                            && backBean.list.size() > 0) {
                                        for (int i = 0; i < backBean.list
                                                .size(); i++) {
                                            bean = backBean.list.get(i);
                                        }
                                        sharedPrefUtil.putString(
                                                getApplication(),
                                                ShareFile.USERFILE,
                                                ShareFile.USERBACKGROUNDPATH,
                                                bean.uBackgroundImage);
                                        sharedPrefUtil.putString(
                                                getApplication(),
                                                ShareFile.USERFILE,
                                                ShareFile.USEREMAIL,
                                                bean.uEmail);
                                        sharedPrefUtil.putString(
                                                getApplication(),
                                                ShareFile.USERFILE,
                                                ShareFile.TELEPHONE,
                                                bean.uMobile);
                                        sharedPrefUtil.putString(
                                                getApplication(),
                                                ShareFile.USERFILE,
                                                ShareFile.USERID, bean.uId);
                                        sharedPrefUtil.putString(
                                                getApplication(),
                                                ShareFile.USERFILE,
                                                ShareFile.USERNAME,
                                                bean.uNickName);
                                        sharedPrefUtil
                                                .putString(getApplication(),
                                                        ShareFile.USERFILE,
                                                        ShareFile.U_ACC_NO,
                                                        bean.uAccNo);
                                        sharedPrefUtil.putString(getApplication(), ShareFile.USERFILE,
                                                ShareFile.PERSONREMARK,
                                                bean.uPersontag);
                                        if (!"".equals(bean.uPortrait)) {
                                            String str = bean.uPortrait
                                                    .toString();
                                            str = str.replace("\\", "");
                                            sharedPrefUtil.putString(
                                                    getApplication(),
                                                    ShareFile.USERFILE,
                                                    ShareFile.USERPHOTOPATH,
                                                    str);
                                        }
                                        String background = sharedPrefUtil
                                                .getString(
                                                        getApplication(),
                                                        ShareFile.USERFILE,
                                                        ShareFile.USERBACKGROUNDPATH,
                                                        "");
                                        final String mypath;
                                        if ("".equals(StringUtils.getIsStringEqulesNull(background))) {
                                            mypath = URLConstants.默认背景图片;
                                        } else {
                                            mypath = URLConstants.默认背景图片 + "="
                                                    + background;
                                        }
                                        imageLoader.displayImage(mypath, member_relative, options, animateFirstListener);
//                                        try {
//                                            new Thread(new Runnable() {
//                                                public void run() {
//                                                    // bit=StreamTool.getHttpBitmap(path,displaypixels);
//                                                    bit = getBitmap(mypath);
//                                                    Message message = new Message();
//                                                    message.what = 0;
//                                                    message.obj = bit;
//                                                    handler.sendMessage(message);
//                                                }
//                                            }).start();
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
                                    } else {
                                        return;
                                    }
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    // ============================每次打开程序生成子记事============================================//
    static List<Map<String, String>> repeatlist = new ArrayList<Map<String, String>>();
    static Map<String, String> map;
    Map<String, String> schMap;
    // List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
    List<Map<String, String>> chongfuList = new ArrayList<Map<String, String>>();
    static App app = App.getDBcApplication();

    public static void CheckCreateRepeatSchData() {
        repeatlist = app.QueryAllChongFuData(3);
        // mapList = app.QueryUpdate();
        RepeatBean bean;
        for (int i = 0; i < repeatlist.size(); i++) {
            map = repeatlist.get(i);
            app.deleteChildSch(repeatlist.get(i).get(CLRepeatTable.repID));
            if (map != null && map.size() > 0) {
                if (Integer.parseInt(map.get(CLRepeatTable.repStateOne)) == 0
                        && Integer.parseInt(map.get(CLRepeatTable.repStateTwo)) == 0) {
                    if (DateUtil.formatDate(
                            DateUtil.parseDateTime(map.get(
                                    CLRepeatTable.repInitialCreatedTime).replace(
                                    "T", " "))).equals(
                            DateUtil.formatDate(new Date()))) {

                        if (DateUtil.parseDateTime(
                                map.get(CLRepeatTable.repNextCreatedTime).replace(
                                        "T", " ")).after(
                                DateUtil.parseDateTime(DateUtil
                                        .formatDateTime(new Date())))) {
                            if ("0".equals(map.get(CLRepeatTable.repInSTable))) {
                                CreateRepeatSchNextData(map);
                            }
                        } else if (DateUtil.parseDateTime(
                                map.get(CLRepeatTable.repNextCreatedTime).replace(
                                        "T", " ")).equals(
                                DateUtil.parseDateTime(DateUtil
                                        .formatDateTime(new Date())))) {
                            if ("0".equals(map.get(CLRepeatTable.repInSTable))) {
                                CreateRepeatSchNextData(map);
                            }
                        } else {
                            if ("0".equals(map.get(CLRepeatTable.repInSTable))) {
                                CreateRepeatSchData(map);
                            }
                        }
                    } else {
                        if (DateUtil.parseDateTime(
                                map.get(CLRepeatTable.repNextCreatedTime).replace(
                                        "T", " ")).after(
                                DateUtil.parseDateTime(DateUtil
                                        .formatDateTime(new Date())))) {
                            if ("0".equals(map.get(CLRepeatTable.repInSTable))) {
                                CreateRepeatSchNextData(map);
                            }
                        } else if (DateUtil.parseDateTime(
                                map.get(CLRepeatTable.repNextCreatedTime).replace(
                                        "T", " ")).equals(
                                DateUtil.parseDateTime(DateUtil
                                        .formatDateTime(new Date())))) {
                            if ("0".equals(map.get(CLRepeatTable.repInSTable))) {
                                CreateRepeatSchNextData(map);
                            }
                        } else {
                            if ("0".equals(map.get(CLRepeatTable.repInSTable))) {
                                CreateRepeatSchData(map);
                            }
                        }
                    }
                } else {
                    if (!CreateRepeatSchDateData(map).repLastCreatedTime.equals(map
                            .get(CLRepeatTable.repDateOne))
                            && !CreateRepeatSchDateData(map).repLastCreatedTime
                            .equals(map.get(CLRepeatTable.repDateTwo))) {
                        if (DateUtil.parseDateTime(
                                CreateRepeatSchDateData(map).repLastCreatedTime)
                                .before(DateUtil.parseDateTime(map
                                        .get(CLRepeatTable.repInitialCreatedTime)))) {
                        } else {
                            if ("0".equals(map.get(CLRepeatTable.repStateOne))) {
                                CreateRepeatSchLastData(map);
                            } else {
                                CreateRepeatSchLastData(map);
                            }
                        }
                    } else if (CreateRepeatSchDateData(map).repLastCreatedTime
                            .equals(map.get(CLRepeatTable.repDateOne))) {
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateOne)) == 1) {
                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateOne)) == 2) {
                        }
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateOne)) == 3) {
                            CreateRepeatSchEndLastData(map); // 脱钩时，生成下一条
                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateOne)) == 0) {
                            CreateRepeatSchLastData(map);
                        }
                    } else if (CreateRepeatSchDateData(map).repLastCreatedTime
                            .equals(map.get(CLRepeatTable.repDateTwo))) {
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateTwo)) == 1) {
                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateTwo)) == 2) {
                        }
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateTwo)) == 3) {
                            CreateRepeatSchEndLastData(map);
                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateTwo)) == 0) {
                            CreateRepeatSchLastData(map);
                        }
                    }
                    if (!CreateRepeatSchDateData(map).repNextCreatedTime.equals(map
                            .get(CLRepeatTable.repDateOne))
                            && !CreateRepeatSchDateData(map).repNextCreatedTime
                            .equals(map.get(CLRepeatTable.repDateTwo))) {
                        if (DateUtil.parseDateTime(
                                CreateRepeatSchDateData(map).repNextCreatedTime)
                                .before(DateUtil.parseDateTime(map
                                        .get(CLRepeatTable.repInitialCreatedTime)))) {
                        } else {
                            if ("0".equals(map.get(CLRepeatTable.repStateTwo))) {
                                CreateRepeatSchNextData(map);
                            } else {
                                CreateRepeatSchNextData(map);
                            }
                        }
                    } else if (CreateRepeatSchDateData(map).repNextCreatedTime
                            .equals(map.get(CLRepeatTable.repDateOne))) {
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateOne)) == 1) {
                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateOne)) == 2) {
                        }
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateOne)) == 3) {
                            CreateRepeatSchEndNextData(map);

                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateOne)) == 0) {
                            CreateRepeatSchNextData(map);
                        }
                    } else if (CreateRepeatSchDateData(map).repNextCreatedTime
                            .equals(map.get(CLRepeatTable.repDateTwo))) {
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateTwo)) == 1) {
                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateTwo)) == 2) {
                        }
                        if (Integer.parseInt(map.get(CLRepeatTable.repStateTwo)) == 3) {
                            CreateRepeatSchEndNextData(map);
                        } else if (Integer.parseInt(map
                                .get(CLRepeatTable.repStateTwo)) == 0) {
                            CreateRepeatSchNextData(map);
                        }
                    }
                }
            }
        }
    }

    private static void CreateRepeatSchData(Map<String, String> map) {
        RepeatBean bean;
        if ("1".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    1, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("2".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    2,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("3".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    3,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("4".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "0");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("6".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\\", "")
                            .replace("\n", "").replace("\"", ""), "1");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    5, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    0, Integer.parseInt(map.get(CLRepeatTable.repColorType)),
                    0, bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        }

    }

    private static void CreateRepeatSchNextData(Map<String, String> map) {
        RepeatBean bean;
        if ("1".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    1, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("2".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    2,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("3".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    3,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("4".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "0");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("6".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\\", "")
                            .replace("\\", "").replace("\"", ""), "1");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    5, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        }
    }

    private static void CreateRepeatSchLastData(Map<String, String> map) {
        RepeatBean bean;
        if ("1".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    1, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("2".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    2,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("3".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    3,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("4".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "0");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("6".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\\", "")
                            .replace("\\", "").replace("\"", ""), "1");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    5, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 0,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        }
    }

    class HomeKeyEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                final Window win = getWindow();
                win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                loadCount();
                UpdateMyMessage();
                // RefreshHomeCount(0);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(context,
                                MinAndMaxService.class);
                        intent.setAction("com.mission.schedule.service.MinAndMaxService");
                        intent.setPackage(getPackageName());
                        intent.putExtra("myfrist", "1");// 0正常进入 1最小化
                        startService(intent);
                    }
                }).start();
                // sharedPrefUtil.putString(context, ShareFile.USERFILE,
                // ShareFile.MAINONCREATE, "0");
                // Intent i = new Intent(Intent.ACTION_MAIN);
                // i.addCategory(Intent.CATEGORY_HOME);
                // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // startActivity(i);
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.OUTWEEKFAG, "0");
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.OPENSTYLESTATE, "1");
                if (myIndex == 3) {
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.REFRESHFRIEND, "1");
                }
            }
        }
    }

    private void registerHomeKeyReceiver() {
        keyEventReceiver = new HomeKeyEventReceiver();
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        myIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        // 注册广播
        registerReceiver(keyEventReceiver, myIntentFilter);
    }

    /**
     * 统计好友申请，被申请总数量
     */
    private void FriendsTotalAsync(String path) {
        StringRequest request = new StringRequest(Method.GET, path,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                TotalFriendsCountBean countBean = gson
                                        .fromJson(result,
                                                TotalFriendsCountBean.class);
                                if (countBean.status == 0) {
                                    if (countBean.bsqCount == Integer
                                            .parseInt(myfriendscount)) {
                                        if (countBean.bsqCount == 0) {
                                            tv_my_count
                                                    .setVisibility(View.GONE);
                                        } else {
                                            tv_my_count
                                                    .setVisibility(View.VISIBLE);
                                            tv_my_count.setText(myfriendscount);
                                        }
                                    } else {
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.COUNT,
                                                countBean.bsqCount + "");
                                        if (countBean.count == 0) {
                                            tv_my_count
                                                    .setVisibility(View.GONE);
                                        } else {
                                            tv_my_count
                                                    .setVisibility(View.VISIBLE);
                                            tv_my_count
                                                    .setText(countBean.bsqCount
                                                            + "");
                                        }
                                    }
                                } else {
                                    tv_my_count.setVisibility(View.GONE);
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        } else {
                            tv_my_count.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    /**
     * 状态为3结束生成下一条
     */
    private static void CreateRepeatSchEndNextData(Map<String, String> map) {
        App app = App.getDBcApplication();
        RepeatBean bean;
        // app.deleteChildSch(list.repId);
        if ("1".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    1, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("2".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    2,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("3".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    3,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("4".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "0");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("6".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "1");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    5, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repNextCreatedTime.substring(0, 10),
                    bean.repNextCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repNextCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repNextCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    map.get(CLRepeatTable.repRingDesc),
                    map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        }

    }

    /**
     * 标记为结束的上一条
     */
    private static void CreateRepeatSchEndLastData(Map<String, String> map) {
        App app = App.getDBcApplication();
        RepeatBean bean;
        // app.deleteChildSch(list.repId);
        if ("1".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    1, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 1,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    "", map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("2".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    2,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 1,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    "", map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("3".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    3,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 1,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    "", map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("4".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "0");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 1,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    "", map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else if ("6".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "1");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 1,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    "", map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        } else {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    5, "", "");
            app.insertScheduleData(map.get(CLRepeatTable.repContent),
                    bean.repLastCreatedTime.substring(0, 10),
                    bean.repLastCreatedTime.substring(11, 16),
                    Integer.parseInt(map.get(CLRepeatTable.repIsAlarm)),
                    Integer.parseInt(map.get(CLRepeatTable.repBeforeTime)),
                    Integer.parseInt(map.get(CLRepeatTable.repDisplayTime)), 0,
                    Integer.parseInt(map.get(CLRepeatTable.repIsImportant)),
                    Integer.parseInt(map.get(CLRepeatTable.repColorType)), 1,
                    bean.repLastCreatedTime, "", 0, "", "",
                    Integer.parseInt(map.get(CLRepeatTable.repID)),
                    bean.repLastCreatedTime,
                    DateUtil.formatDateTimeSs(new Date()), 1,
                    Integer.parseInt(map.get(CLRepeatTable.repOpenState)), 1,
                    "", map.get(CLRepeatTable.repRingCode), "", 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repAType)),
                    map.get(CLRepeatTable.repWebURL),
                    map.get(CLRepeatTable.repImagePath), 0, 0,
                    Integer.parseInt(map.get(CLRepeatTable.repcommendedUserId)));
        }

    }

    /**
     * 计算生成子记事上一条和下一条的时间
     */
    public static RepeatBean CreateRepeatSchDateData(Map<String, String> map) {
        // App app = App.getDBcApplication();
        RepeatBean bean;
        // app.deleteChildSch(list.repId);
        if ("1".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    1, "", "");
        } else if ("2".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    2,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
        } else if ("3".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    3,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "");
        } else if ("4".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "0");
        } else if ("6".equals(map.get(CLRepeatTable.repType))) {
            bean = RepeatDateUtils.saveCalendar(
                    map.get(CLRepeatTable.repTime),
                    4,
                    map.get(CLRepeatTable.repTypeParameter).replace("[", "")
                            .replace("]", "").replace("\n\"", "")
                            .replace("\n", "").replace("\"", ""), "1");
        } else {
            bean = RepeatDateUtils.saveCalendar(map.get(CLRepeatTable.repTime),
                    5, "", "");
        }
        return bean;
    }

    /**
     * 修改极光推送的推送码
     *
     * @author lenovo
     *
     */
    /**
     * 修改推送mac地址置为空
     */
    private void ClearMAC() {
        if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
            final String path = URLConstants.修改MAC地址 + userId + "&uClintAddr="
                    + "" + "&uTocode=android";
            StringRequest request = new StringRequest(Request.Method.GET, path,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                }
            });
            request.setTag("down");
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
            App.getHttpQueues().add(request);
            // new Thread(new Runnable() {
            //
            // @Override
            // public void run() {
            // try {
            // HttpUtil.HttpClientDoGet(path);
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            // }
            // }).start();
        } else {
            return;
        }
    }

    /**
     * 重要通知
     */
    private void NotificationAsync(String path) {
        StringRequest request = new StringRequest(Method.GET, path,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    /**
     * 设置提醒模式
     */
    private void alterRingDialog() {
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_setyuyin, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getWindowManager().getDefaultDisplay().getWidth();
        dialog.show();

        new alterRingDialogOnClick(dialog, more_pop_menu);
    }

    class alterRingDialogOnClick implements View.OnClickListener {

        private View view;
        private Dialog dialog;
        private TextView yuyin_tv;
        private TextView jingyin_tv;
        private TextView duanyin_tv;
        private TextView canel_tv;

        @SuppressLint("NewApi")
        public alterRingDialogOnClick(Dialog dialog, View view) {
            this.dialog = dialog;
            this.view = view;
            initview();
        }

        private void initview() {
            yuyin_tv = (TextView) view.findViewById(R.id.yuyin_tv);
            yuyin_tv.setOnClickListener(this);
            jingyin_tv = (TextView) view.findViewById(R.id.jingyin_tv);
            jingyin_tv.setOnClickListener(this);
            duanyin_tv = (TextView) view.findViewById(R.id.duanyin_tv);
            duanyin_tv.setOnClickListener(this);
            canel_tv = (TextView) view.findViewById(R.id.canel_tv);
            canel_tv.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.yuyin_tv:
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.RINGSTATE, "0");
                    tixingname_tv.setText("语音");
                    tixingname_tv.setTextColor(getResources().getColor(
                            R.color.mingtian_color));
                    dialog.dismiss();
                    break;
                case R.id.jingyin_tv:
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.RINGSTATE, "1");
                    tixingname_tv.setText("静音");
                    tixingname_tv.setTextColor(getResources().getColor(
                            R.color.sunday_txt));
                    dialog.dismiss();
                    break;
                case R.id.duanyin_tv:
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.RINGSTATE, "2");
                    tixingname_tv.setText("短音");
                    tixingname_tv.setTextColor(getResources().getColor(
                            R.color.mingtian_color));
                    dialog.dismiss();
                    break;
                case R.id.canel_tv:
                    dialog.dismiss();
                    break;
            }
        }
    }

    // ==========================闹钟=======================================//
    private void initDate() {
        if (!"0".equals(sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.FIRSTLOGIN, "0"))) {
            QueryAlarmData.writeAlarm(getApplicationContext());
            Intent intent = new Intent(this, ClockService.class);
            // intent.setAction("com.mission.schedule.widget.WidgetService");
            intent.setAction("com.mission.schedule.service.ClockService");
            intent.setPackage(getPackageName());
            // intent.setFlags(Service.START_REDELIVER_INTENT);
            // intent.putExtra("autoStart", "0");
            intent.putExtra("WriteAlarmClockwrite", "0");
            startService(intent);
        } else {
            // QueryAlarmData.writeAlarm(getApplicationContext());
            Intent intent = new Intent(this, ClockService.class);
            // intent.setAction("com.mission.schedule.widget.WidgetService");
            intent.setAction("com.mission.schedule.service.ClockService");
            intent.setPackage(getPackageName());
            // intent.setFlags(Service.START_REDELIVER_INTENT);
            // intent.putExtra("autoStart", "0");
            intent.putExtra("WriteAlarmClockwrite", "0");
            startService(intent);
        }
    }

    private void downNongLiData() {
        nongliList = app.queryMaxDate();
        if (nongliList != null && nongliList.size() > 0) {
            String path = URLConstants.更新农历 + nongliList.get(0).get("calendar");
            StringRequest request = new StringRequest(Method.GET, path,
                    new Listener<String>() {

                        @Override
                        public void onResponse(String s) {
                            if (!TextUtils.isEmpty(s)) {
                                try {
                                    Gson gson = new Gson();
                                    SuccessOrFailBean backBean = gson.fromJson(
                                            s, SuccessOrFailBean.class);
                                    if (backBean.status == 1) {
                                        app.deletenongliData();
                                        String nonglipath = URLConstants.下载农历;
                                        // new
                                        // DownNongLiAsync().execute(nonglipath);
                                        StringRequest stringRequest = new StringRequest(
                                                Method.GET,
                                                nonglipath,
                                                new Listener<String>() {

                                                    @Override
                                                    public void onResponse(
                                                            String s) {
                                                        if (!TextUtils
                                                                .isEmpty(s)) {
                                                            try {
                                                                List<LunarCalendaTimeBean> list = new ArrayList<LunarCalendaTimeBean>();
                                                                Gson gson = new Gson();
                                                                LunarCalendaTimeBackBean backBean = gson
                                                                        .fromJson(
                                                                                s,
                                                                                LunarCalendaTimeBackBean.class);
                                                                if (backBean.status == 0) {
                                                                    list = backBean.list;
                                                                    for (int i = 0; i < list
                                                                            .size(); i++) {
                                                                        app.insertNongLiData(
                                                                                list.get(i).calendar,
                                                                                list.get(i).solarTerms,
                                                                                list.get(i).week,
                                                                                list.get(i).lunarCalendar,
                                                                                list.get(i).holiday,
                                                                                list.get(i).lunarHoliday,
                                                                                list.get(i).createTime,
                                                                                list.get(i).isNotHoliday);
                                                                    }
                                                                }
                                                            } catch (JsonSyntaxException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {

                                                    @Override
                                                    public void onErrorResponse(
                                                            VolleyError volleyError) {

                                                    }
                                                });
                                        stringRequest.setTag("down");
                                        stringRequest
                                                .setRetryPolicy(new DefaultRetryPolicy(
                                                        20000, 1, 1.0f));
                                        App.getHttpQueues().add(stringRequest);
                                    }
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            request.setTag("down");
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
            App.getHttpQueues().add(request);
        } else {
            String nonglipath = URLConstants.下载农历;
            StringRequest stringRequest = new StringRequest(Method.GET,
                    nonglipath, new Listener<String>() {

                @Override
                public void onResponse(String s) {
                    if (!TextUtils.isEmpty(s)) {
                        try {
                            List<LunarCalendaTimeBean> list = new ArrayList<LunarCalendaTimeBean>();
                            Gson gson = new Gson();
                            LunarCalendaTimeBackBean backBean = gson
                                    .fromJson(
                                            s,
                                            LunarCalendaTimeBackBean.class);
                            if (backBean.status == 0) {
                                list = backBean.list;
                                for (int i = 0; i < list.size(); i++) {
                                    app.insertNongLiData(
                                            list.get(i).calendar,
                                            list.get(i).solarTerms,
                                            list.get(i).week,
                                            list.get(i).lunarCalendar,
                                            list.get(i).holiday,
                                            list.get(i).lunarHoliday,
                                            list.get(i).createTime,
                                            list.get(i).isNotHoliday);
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            stringRequest.setTag("down");
            stringRequest
                    .setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
            App.getHttpQueues().add(stringRequest);
            // new DownNongLiAsync().execute(nonglipath);
        }
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     * @author lenovo
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (!"".equals(StringUtils.getIsStringEqulesNull(content))) {
            cmb.setText(content.trim());
        }
    }

    /**
     * 实现粘贴功能
     *
     * @param context
     * @return
     * @author lenovo
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    public void onEventMainThread(PostSendMainActivity event) {
        int index = event.getIndex();
        if (index == 1) {
            int noEndCount = event.getMsg();
            if (noEndCount == 0) {
                tv_schedule_count.setVisibility(View.GONE);
            } else {
                tv_schedule_count.setText(noEndCount + "");
                tv_schedule_count.setVisibility(View.VISIBLE);
            }
        } else if (index == 2) {
            int count = event.getMsg();
            if (count == 0) {
                tv_my_count.setVisibility(View.GONE);
            } else {
                tv_my_count.setVisibility(View.VISIBLE);
                tv_my_count.setText(count + "");
            }
        }
    }

    /**
     * 图片加载第一次显示监听器
     *
     * @author Administrator
     */
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}
