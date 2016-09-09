package com.mission.schedule.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.mission.schedule.R;
import com.mission.schedule.activity.AddEverydayDetailTaskActivity;
import com.mission.schedule.activity.ComeFriendSchActivity;
import com.mission.schedule.activity.DateCalendarActivity;
import com.mission.schedule.activity.EditSchActivity;
import com.mission.schedule.activity.GuoQiWeiJieShuActivity;
import com.mission.schedule.activity.HelpActivity;
import com.mission.schedule.activity.ImportantSchActivity;
import com.mission.schedule.activity.MainActivity;
import com.mission.schedule.activity.MyStateActivity;
import com.mission.schedule.activity.SchZhuanFaActivity;
import com.mission.schedule.activity.TagSerachActivity;
import com.mission.schedule.activity.UpdateSchTiXingActivity;
import com.mission.schedule.activity.YiQianActivity;
import com.mission.schedule.adapter.ChangYongYuAdapter;
import com.mission.schedule.adapter.DaoJiShiTiXingAdapter;
import com.mission.schedule.adapter.MyScheduleAdapter;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.ChangYongBean;
import com.mission.schedule.bean.MySchBean;
import com.mission.schedule.bean.TotalFriendsCountBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.PostSendMainActivity;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.data.MyData;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.LocateAllNoticeTable;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.swipexlistview.SwipeXListView;
import com.mission.schedule.swipexlistview.SwipeXListView.IXListViewListener;
import com.mission.schedule.utils.AfterPermissionGranted;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.DialogBarView;
import com.mission.schedule.utils.DialogCallback;
import com.mission.schedule.utils.DialogWidget;
import com.mission.schedule.utils.DisplayUtils;
import com.mission.schedule.utils.EasyPermissions;
import com.mission.schedule.utils.GuideHelper;
import com.mission.schedule.utils.GuideHelper.TipData;
import com.mission.schedule.utils.InWeekUtils;
import com.mission.schedule.utils.JsonParser;
import com.mission.schedule.utils.LineGridView;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.ReadTextContentXml.ReadWeiXinXml;
import com.mission.schedule.utils.RepeatSetChildEndUtils;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.greenrobot.event.EventBus;

public class MyScheduleFragment extends BaseFragment implements
        OnClickListener, IXListViewListener, DialogCallback, EasyPermissions.PermissionCallbacks {

    private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    List<Map<String, String>> unlist = new ArrayList<Map<String, String>>();
    List<Map<String, String>> todaylist = new ArrayList<Map<String, String>>();
    List<Map<String, String>> tomorrowlist = new ArrayList<Map<String, String>>();
    List<Map<String, String>> inweeklist = new ArrayList<Map<String, String>>();
    List<Map<String, String>> outweeklist = new ArrayList<Map<String, String>>();
    List<Map<String, String>> comelist = new ArrayList<Map<String, String>>();
    private SwipeXListView myschedule_listview;
    private MyScheduleAdapter adapter = null;
    private boolean isShow = false;// 判断是否已经显示
    public static LinearLayout top_ll_left;
    private TextView guoqiweijieshu_tv;// 过期为结束个数
    // private TextView count_tv;// 今天一结束数量
    ImageView iv_more_down;// 设置铃声是否静音
    // private TextView tv_piliang;
    private LinearLayout mysch_ll;

    private View headView;
    private RelativeLayout up_down;
    private RelativeLayout top_ll_right;
    private ImageView top_right_iv;
    private int pageIndex = 1;
    private int timeType = 9;// 8以前,9今后,10本周17已发出,5刷新隐藏
    int index = 0;
    private String postpone = "0";// 顺延
    View view;

    SharedPrefUtil sharedPrefUtil = null;
    // TextView tv_schedule_count;
    // TextView tv_my_count;
    String userid;

    String myfriendscount;
    static Context context;
    String localpath;
    int width;
    int heigth;
    String untaskend;

    int uncount = 0;// 今日待办个数
    int todaycount = 0;// 今日日程个数
    int tomorrowcount = 0;// 明天日程个数
    int inweekcount = 0;// 一周以内日程个数
    int outweekcount = 0;// 一周以外日程个数
    String outweekfag = "0";
    String firstlogin = "0";
    String time;
    String ringdesc;
    String ringcode;
    String delaytime = "";

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SharedPreferences mSharedPreferences;

    // 声明记录停止滚动时候，可见的位置
    int stop_position;
    boolean isLastRow = true;

    Button chakan;
    String soundstate;
    String wakestate;
    private Vibrator vibrator;

    RelativeLayout tixing_rl;
    TextView count_tv;
    final ProgressUtil progressUtil = new ProgressUtil();
    App application = App.getDBcApplication();
    CalendarChangeValue changeValue = new CalendarChangeValue();
    String mainoncreate = "";
    // int scrolledX = 0;
    // int scrolledY = 0;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    InWeekUtils inWeekUtils = new InWeekUtils();
    private DialogWidget dialog;
    boolean autoFag = false;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    RepeatSetChildEndUtils repeatSetChildEndUtils = new RepeatSetChildEndUtils();

    public static List<Map<String, String>> schIDList = new ArrayList<Map<String, String>>();
    String  permissionState = "0";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myschedule, container, false);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !isShow) {
            isShow = true;
            init();
            loadData();
            setDataToAdapter();
        }
    }

    @SuppressLint("NewApi")
    private void init() {
        context = getActivity();
        EventBus.getDefault().register(this);
        receiver = new UpdateDataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.SHUAXINDATA);
        filter.addAction("dismiss");
        getActivity().registerReceiver(receiver, filter);
        vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        // isNetWork();
        sharedPrefUtil = new SharedPrefUtil(getActivity(), ShareFile.USERFILE);
        userid = sharedPrefUtil.getString(getActivity(), ShareFile.USERFILE,
                ShareFile.USERID, "");
        localpath = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.LOCALPATH, "");
        myfriendscount = sharedPrefUtil.getString(getActivity(),
                ShareFile.USERFILE, ShareFile.COUNT, "0");
        time = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.ALLTIME, "08:58");
        ringdesc = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.MUSICDESC, "完成任务");
        ringcode = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.MUSICCODE, "g_88");
        headView = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_myschedule_head, null);
        // count_tv = (TextView) headView.findViewById(R.id.count_tv);
        guoqiweijieshu_tv = (TextView) headView
                .findViewById(R.id.guoqiweijieshu_tv);
        up_down = (RelativeLayout) headView.findViewById(R.id.up_down);
        // tv_piliang = (TextView) headView.findViewById(R.id.tv_piliang);
        // tv_piliang.setOnClickListener(this);
        // headView.setPadding(0, 0, 0, Utils.dipTopx(context, 40));
        up_down.setOnClickListener(this);
        // up_show = (TextView) headView.findViewById(R.id.up_show);
        myschedule_listview = (SwipeXListView) view
                .findViewById(R.id.myschedule_listview);
        myschedule_listview.setUpDown(up_down);
        // myschedule_listview.setUpshow(up_show);
        myschedule_listview.setPullLoadEnable(true);
        myschedule_listview.setXListViewListener(this);
        myschedule_listview.addHeaderView(headView);
        myschedule_listview.setFocusable(true);
        top_ll_left = null;
        top_ll_left = (LinearLayout) view.findViewById(R.id.top_ll_left);
        top_ll_left.setOnClickListener(this);
        top_ll_right = (RelativeLayout) view.findViewById(R.id.top_ll_right);
        top_ll_right.setOnClickListener(this);
        top_right_iv = (ImageView) view.findViewById(R.id.top_right_iv);
        iv_more_down = (ImageView) view.findViewById(R.id.iv_more_down);
        iv_more_down.setOnClickListener(this);
        LinearLayout myschedule_ll = (LinearLayout) view
                .findViewById(R.id.myschedule_ll);

        tixing_rl = (RelativeLayout) view.findViewById(R.id.tixing_rl);
        tixing_rl.setOnClickListener(this);
        tixing_rl.setVisibility(View.GONE);
        count_tv = (TextView) view.findViewById(R.id.count_tv);

        mysch_ll = (LinearLayout) view.findViewById(R.id.mysch_ll);

        myschedule_ll.setOnClickListener(this);

        // tv_schedule_count = MainActivity.tv_schedule_count;

        /**
         * 获取屏幕的高度和宽度
         */
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        heigth = metric.heightPixels;

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(getActivity(), mInitListener);
        mSharedPreferences = context.getSharedPreferences(
                "com.iflytek.setting", Activity.MODE_PRIVATE);
        firstlogin = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.FIRSTLOGIN, "0");
        if ("0".equals(sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.ISSHOWDIALOG, "0"))) {
            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                    ShareFile.ISSHOWDIALOG, "1");
            dialog = new DialogWidget(this.getActivity(),
                    new DialogBarView(this.getActivity(),
                            R.layout.new_first_dialog_tishi, this).getView(),
                    R.style.MyDialogs);
            dialog.show();
        }
        if ("0".equals(firstlogin)) {
            if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                if ("0".equals(sharedPrefUtil.getString(context,
                        ShareFile.USERFILE, ShareFile.ISSHOWPROGRESS, "0"))) {
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.ISSHOWPROGRESS, "1");
                } else {
                    progressUtil.ShowProgress(context, true, true, "正在同步数据...");
                }
                isNetWork();
            }
        } else {
            // isNetWork();
        }
        isPrepared = true;
        permissionState = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "0");
        if("0".equals(permissionState)){
            checkPhonePermission();
        }
        lazyLoad();
    }

    private void loadData() {
        try {
            untaskend = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                    ShareFile.UNTASKEND, "1");// 0不显示 1显示
            outweekfag = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                    ShareFile.OUTWEEKFAG, "0");

            String date = DateUtil.formatDateTimeSs(new Date());
            int year = Integer.parseInt(date.substring(0, 4).toString()) - 1;
            String beforeDownTime = String.valueOf(year)
                    + date.substring(4, 19).toString();
            sharedPrefUtil.putString(getActivity(), ShareFile.USERFILE,
                    ShareFile.OLDUPDATETIME, beforeDownTime);
            int count = application.QueryGuoQiWeiJieShuCount();
            if (count == 0) {
                headView.setVisibility(View.GONE);
            } else {
                headView.setVisibility(View.VISIBLE);
                // tv_piliang.setVisibility(View.VISIBLE);
                guoqiweijieshu_tv.setText(count + "");
            }

            try {
                unlist.clear();
                todaylist.clear();
                tomorrowlist.clear();
                inweeklist.clear();
                outweeklist.clear();
                mList.clear();
                comelist.clear();

                comelist = application.queryAllSchData(16, 0, 0);
                if (comelist != null && comelist.size() > 0) {
                    count_tv.setText(comelist.size() + "");
                    tixing_rl.setVisibility(View.VISIBLE);
                } else {
                    tixing_rl.setVisibility(View.GONE);
                }

                if ("0".equals(untaskend)) {
                    unlist = application.queryAllSchData(0, 0, 0);
                } else {
                    unlist = application.queryAllSchData(1, 0, 0);
                }
                if ("0".equals(untaskend)) {
                    todaylist = application.queryAllSchData(2, 0, 0);
                } else {
                    todaylist = application.queryAllSchData(7, 0, 0);
                }
                tomorrowlist = application.queryAllSchData(3, 0, 0);
                inweeklist = application.queryAllSchData(4, 0, 0);
                outweeklist = application.queryAllSchData(5, 0, 0);

                uncount = unlist.size();
                todaycount = todaylist.size();
                tomorrowcount = tomorrowlist.size();
                inweekcount = inweeklist.size();
                outweekcount = outweeklist.size();
                // aftercount = list.size();
                mList.addAll(unlist);
                mList.addAll(todaylist);
                mList.addAll(tomorrowlist);
                mList.addAll(inweeklist);

                if (uncount == 0 && todaycount == 0 && tomorrowcount == 0
                        && inweekcount == 0) {
                    mList.addAll(outweeklist);
                } else {
                    if ("1".equals(outweekfag)) {
                        mList.addAll(outweeklist);
                    }
                }
                // mList.addAll(list);
                localpath = sharedPrefUtil.getString(context,
                        ShareFile.USERFILE, ShareFile.LOCALPATH, "");
                // state = myschedule_listview.onSaveInstanceState();

                if (headView.getVisibility() == View.GONE) {
                    if (uncount == 0) {
                        if (comelist != null && comelist.size() > 0) {
                            headView.setPadding(0, 0, 0,
                                    Utils.dipTopx(context, 20));
                        } else {
                            headView.setPadding(0, 0, 0,
                                    Utils.dipTopx(context, -30));
                        }
                    } else {
                        if (comelist != null && comelist.size() > 0) {
                            headView.setPadding(0, 0, 0,
                                    Utils.dipTopx(context, 30));
                        } else {
                            headView.setPadding(0, 0, 0,
                                    Utils.dipTopx(context, -20));
                        }
                    }
                } else {
                    if (uncount == 0) {
                        if (comelist != null && comelist.size() > 0) {
                            headView.setPadding(0, 0, 0,
                                    Utils.dipTopx(context, 20));
                        } else {
                            headView.setPadding(0, 0, 0,
                                    Utils.dipTopx(context, 20));
                        }
                    } else {
                        headView.setPadding(0, 0, 0, Utils.dipTopx(context, 20));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setDataToAdapter() {
        adapter = new MyScheduleAdapter(getActivity(), mList,
                R.layout.adapter_timeall_item, handler, myschedule_listview,
                uncount, todaycount, tomorrowcount, inweekcount, outweekcount,
                localpath, outweekfag, width);
        myschedule_listview.setAdapter(adapter);
    }

    private void isNetWork() {
        if (NetUtil.getConnectState(getActivity()) != NetWorkState.NONE) {
            Intent intent = new Intent(getActivity(), UpLoadService.class);
            intent.setAction(Const.SHUAXINDATA);
            intent.setPackage(getActivity().getPackageName());
            getActivity().startService(intent);
        } else {
            return;
        }
    }

    private void UpLoadData() {
        if (NetUtil.getConnectState(getActivity()) != NetWorkState.NONE) {
            Intent intent = new Intent(getActivity(), UpLoadService.class);
            intent.setAction(Const.UPLOADDATA);
            intent.setPackage(getActivity().getPackageName());
            getActivity().startService(intent);
        } else {
            return;
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = null;
            Map<String, String> mMap = (Map<String, String>) msg.obj;
            Map upMap = null;
            String value = "0";
            int position = msg.arg1;
            if (Integer.parseInt(mMap.get(ScheduleTable.schID)) < 0) {
                if (schIDList != null && schIDList.size() > 0) {
                    for (int i = 0; i < schIDList.size(); i++) {
                        Object[] indexStr = schIDList.get(i).keySet().toArray();
                        String index = indexStr[0].toString();
                        if (Integer.parseInt(mMap.get(ScheduleTable.schID)) == Integer
                                .parseInt(index)) {
                            mMap.put(ScheduleTable.schID,
                                    schIDList.get(i).get(index));
                            break;
                        }
                    }
                }
            }
            switch (msg.what) {
                case 0:// 点击菜单(详情菜单)
                    dialogDetailOnClick(mMap, 0, position);
                    break;

                // case 1:// 设置
                // dialogDetailOnClick(mMap, 1);
                // break;

                case 2:// 设为结束
                    try {
                        if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                            soundstate = sharedPrefUtil.getString(getActivity(),
                                    ShareFile.USERFILE, ShareFile.ENDSOUNDSTATE,
                                    "0");
                            wakestate = sharedPrefUtil
                                    .getString(getActivity(), ShareFile.USERFILE,
                                            ShareFile.ENDWAKESTATE, "0");
                            if ("0".equals(soundstate)) {
                                MediaPlayer mediaPlayer = mediaPlayer = new MediaPlayer();
                                AssetFileDescriptor fileDescriptor = getActivity()
                                        .getAssets().openFd("complete.mp3");
                                mediaPlayer.setDataSource(
                                        fileDescriptor.getFileDescriptor(),
                                        fileDescriptor.getStartOffset(),
                                        fileDescriptor.getLength());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            }
                            if ("0".equals(wakestate)) {
                                long[] pattern = {100, 400}; // 停止 开启 停止 开启
                                vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
                                // 如果只想震动一次，index设为-1
                            }
                        }
                        if ("0".equals(mMap.get(ScheduleTable.schRepeatID))) {
                            updateScheduleRead1(mMap, ScheduleTable.schRead);
                        } else {
                            updateScheduleRead2(mMap, ScheduleTable.schRead,
                                    ScheduleTable.schRepeatLink);
                        }
                        updateScheduleIsEnd(mMap, ScheduleTable.schIsEnd,
                                ScheduleTable.schUpdateState);
                        updateSchClock(mMap, LocateAllNoticeTable.isEnd);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                ShareFile.ENDUPDATETIME,
                                DateUtil.formatDateTimeSs(new Date()));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String updatetime = sharedPrefUtil.getString(
                                        context, ShareFile.USERFILE,
                                        ShareFile.ENDUPDATETIME,
                                        DateUtil.formatDateTimeSs(new Date()));
                                delaytime = DateUtil.formatDateTimeSs(new Date());
                                if (DateUtil.parseDateTimeSs(delaytime).getTime()
                                        - DateUtil.parseDateTimeSs(updatetime)
                                        .getTime() >= 3000) {
                                    UpLoadData();
                                }
                            }
                        }, 3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case 3:// 删除
                    try {
                        alertDeleteDialog(mMap, 0, position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:// 跳到日历页面
                    intent = new Intent(getActivity(), DateCalendarActivity.class);
                    intent.putExtra("myschedulefragment", "0");
                    intent.putExtra("sourse", 0);
                    intent.putExtra("dateTime", mMap.get(ScheduleTable.schDate));
                    intent.putExtra("postpone", postpone);
                    intent.putExtra("openState",
                            mMap.get(ScheduleTable.schOpenState));
                    intent.putExtra("ringcode", mMap.get(ScheduleTable.schRingCode));
                    intent.putExtra("schIsAlarm",
                            mMap.get(ScheduleTable.schIsAlarm));
                    startActivity(intent);
                    break;
                case 5:// 点击展开一周后
                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                            ShareFile.OUTWEEKFAG, "1");
                    mList.addAll(outweeklist);
                    adapter.notifyDataSetChanged();
                    // loadData();
                    break;
            }
        }

    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_ll_right:
                dialogOnClick();
                break;
            case R.id.top_ll_left:
                // toggle();
                break;
            case R.id.iv_more_down:
                alterImportantDialog();
                break;
            case R.id.myschedule_ll:
                alterImportantDialog();
                break;
            case R.id.up_down:
                startActivityForResult(new Intent(getActivity(),
                        GuoQiWeiJieShuActivity.class), 100);
                break;
            // case R.id.tv_piliang:
            // List<Map<String, String>> myList = new ArrayList<Map<String,
            // String>>();
            // myList.clear();
            // myList.addAll(App.getDBcApplication().QueryGuoQiWeiJieShu(1));
            // myList.addAll(App.getDBcApplication().QueryGuoQiWeiJieShu(2));
            // if (myList.size() > 0) {
            // setIsEndDialog(myList);
            // } else {
            // Toast.makeText(getActivity(), "没有需要结束的记事!", Toast.LENGTH_LONG)
            // .show();
            // return;
            // }
            // break;
            case R.id.tixing_rl:
                startActivityForResult(new Intent(context,
                        UpdateSchTiXingActivity.class), 100);
                tixing_rl.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void alertDeleteDialog(final Map<String, String> mMap,
                                   final int type, final int position) {
        final AlertDialog builder = new AlertDialog.Builder(getActivity())
                .create();
        builder.show();
        Window window = builder.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setAttributes(params);// 设置生效
        window.setContentView(R.layout.dialog_alterdelete);
        TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
                if (type == 0) {
                    if (Integer.parseInt(mMap.get(ScheduleTable.schID)) < 0) {
                        if (schIDList != null && schIDList.size() > 0) {
                            for (int i = 0; i < schIDList.size(); i++) {
                                Object[] indexStr = schIDList.get(i).keySet()
                                        .toArray();
                                String index = indexStr[0].toString();
                                if (Integer.parseInt(mMap
                                        .get(ScheduleTable.schID)) == Integer
                                        .parseInt(index)) {
                                    mMap.put(ScheduleTable.schID, schIDList
                                            .get(i).get(index));
                                    break;
                                }
                            }
                        }
                    }
                    try {
                        String deleteId = mMap.get(ScheduleTable.schID);
                        App dbContextExtended = App.getDBcApplication();
                        dbContextExtended.deleteScheduleLocalData(deleteId);
                        dbContextExtended.deleteSch(Integer.parseInt(deleteId));
                        myschedule_listview.hiddenRight();
                        if ("1".equals(mMap.get(ScheduleTable.schRepeatLink))
                                || "3".equals(mMap
                                .get(ScheduleTable.schRepeatLink))) {
                            Map<String, String> map = App
                                    .getDBcApplication()
                                    .QueryStateData(
                                            Integer.parseInt(mMap
                                                    .get(ScheduleTable.schRepeatID)));
                            if (map != null) {
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                if (repdate.equals(lastdate) || repdate.equals(nextdate)) {
                                    if (!"".equals(lastdate) && lastdate.equals(repdate)) {
                                        application
                                                .updateSchCLRepeatData(
                                                        Integer.parseInt(mMap
                                                                .get(ScheduleTable.schRepeatID)),
                                                        mMap.get(ScheduleTable.schRepeatDate),
                                                        map.get(CLRepeatTable.repDateTwo),
                                                        2,
                                                        Integer.parseInt(map
                                                                .get(CLRepeatTable.repStateTwo)));
                                    } else if (!"".equals(nextdate) && nextdate.equals(repdate)) {
                                        application
                                                .updateSchCLRepeatData(
                                                        Integer.parseInt(mMap
                                                                .get(ScheduleTable.schRepeatID)),
                                                        map.get(CLRepeatTable.repDateOne),
                                                        mMap.get(ScheduleTable.schRepeatDate),
                                                        Integer.parseInt(map
                                                                .get(CLRepeatTable.repStateOne)),
                                                        2);
                                    }
                                } else {
                                    if ("".equals(lastdate) && "".equals(nextdate)) {
                                        application
                                                .updateSchCLRepeatData(
                                                        Integer.parseInt(mMap
                                                                .get(ScheduleTable.schRepeatID)),
                                                        mMap.get(ScheduleTable.schRepeatDate),
                                                        map.get(CLRepeatTable.repDateTwo),
                                                        2,
                                                        Integer.parseInt(map
                                                                .get(CLRepeatTable.repStateTwo)));
                                    } else if ("".equals(lastdate) && !"".equals(nextdate)) {
                                        application
                                                .updateSchCLRepeatData(
                                                        Integer.parseInt(mMap
                                                                .get(ScheduleTable.schRepeatID)),
                                                        map.get(CLRepeatTable.repDateOne),
                                                        mMap.get(ScheduleTable.schRepeatDate),
                                                        Integer.parseInt(map
                                                                .get(CLRepeatTable.repStateOne)),
                                                        2);
                                    } else if (!"".equals(lastdate) && "".equals(nextdate)) {
                                        application
                                                .updateSchCLRepeatData(
                                                        Integer.parseInt(mMap
                                                                .get(ScheduleTable.schRepeatID)),
                                                        mMap.get(ScheduleTable.schRepeatDate),
                                                        map.get(CLRepeatTable.repDateTwo),
                                                        2,
                                                        Integer.parseInt(map
                                                                .get(CLRepeatTable.repStateTwo)));
                                    } else {
                                        if (DateUtil.parseDateTime(lastdate).getTime() > DateUtil
                                                .parseDateTime(nextdate).getTime()) {
                                            application
                                                    .updateSchCLRepeatData(
                                                            Integer.parseInt(mMap
                                                                    .get(ScheduleTable.schRepeatID)),
                                                            map.get(CLRepeatTable.repDateOne),
                                                            mMap.get(ScheduleTable.schRepeatDate),
                                                            Integer.parseInt(map
                                                                    .get(CLRepeatTable.repStateOne)),
                                                            2);
                                        } else {
                                            application
                                                    .updateSchCLRepeatData(
                                                            Integer.parseInt(mMap
                                                                    .get(ScheduleTable.schRepeatID)),
                                                            mMap.get(ScheduleTable.schRepeatDate),
                                                            map.get(CLRepeatTable.repDateTwo),
                                                            2,
                                                            Integer.parseInt(map
                                                                    .get(CLRepeatTable.repStateTwo)));
                                        }
                                    }
                                }
                            }
                        }
                        // loadData();
                        mList.remove(position);
                        updateDeleteScheduleRead(mMap, ScheduleTable.schRead,
                                ScheduleTable.schRepeatLink);
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        // isNetWork();
                        loadData();
                        loadCount();
                        // adapter.updateCount(uncount, todaycount,
                        // tomorrowcount,
                        // inweekcount, outweekcount);
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.invalidate();
                        isNetWork();
                        if (mList != null && mList.size() == 0) {
                            mList = application.queryAllSchData(5, 0, 0);
                            adapter = new MyScheduleAdapter(context, mList,
                                    R.layout.adapter_timeall_item, handler,
                                    myschedule_listview, 0, 0, 0, 0, mList
                                    .size(), localpath, "1", width);
                            myschedule_listview.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        TextView delete_canel = (TextView) window
                .findViewById(R.id.delete_canel);
        delete_canel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });
        TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
        if (type == 0) {
            delete_tv.setText("确定要删除此记事吗?");
        } else {
            delete_tv.setText("结束今天之前所有未结束的记事?");
        }

    }

    /**
     * 普通记事点击弹出详情菜单 setType 0,菜单详情 1,设置
     *
     * @param mMap
     */
    private void dialogDetailOnClick(Map<String, String> mMap, int setType,
                                     int position) {
        Context context = getActivity();
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_cls_detail, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth() - 30;
        dialog.show();

        new MyGeneralDetailOnClick(dialog, mMap, more_pop_menu, setType,
                position);
    }

    class MyGeneralDetailOnClick implements View.OnClickListener {

        private View mianView;
        private Dialog dialog;
        private int setType = 0;
        private LinearLayout detail_close;
        private LinearLayout detail;
        private LinearLayout after;
        private LinearLayout setting;
        private Map<String, String> mMap;
        private int closeType = 0;
        private TextView detail_after;
        private TextView detail_more;
        private LinearLayout detail_zhuanfa;
        private TextView timebefore_tv;
        // 更多
        private LinearLayout more_openstate;
        private TextView more_zhuanfasjb;
        private TextView more_zhuanfawx;
        private TextView more_setdaiban;
        private TextView more_shitingmusic;
        private TextView more_delete;
        private TextView more_openstate_tv;
        // 详情
        private TextView detail_date;
        private TextView detail_content;
        private TextView detail_year_date;
        private TextView detail_time_date;
        private TextView detail_tv_shun;
        private LinearLayout detail_edit_ll;
        // 推后
        private TextView after_autopostone;// 自动顺延
        private TextView after_onehour;// 推后一小时
        private TextView after_oneday;// 推后一天
        private TextView after_oneweek;// 推后一周
        private TextView after_onemonth;// 推后一个月
        private TextView after_nextweekfirstday;// 推到下周一
        private TextView after_today;// 推到今天
        private TextView after_tommrow;// 推到明天
        private View after_today_view, after_tommrow_view, after_onehour_view,
                after_oneday_view, after_oneweek_view, after_onemonth_view,
                after_nextweekfirstday_view;

        private TextView detail_important;
        String today, tomorrow;
        int position;
        Calendar calendar = Calendar.getInstance();

        @SuppressLint("NewApi")
        public MyGeneralDetailOnClick(Dialog dialog, Map<String, String> mMap,
                                      View view, int setType, int position) {
            this.setType = setType;
            this.dialog = dialog;
            this.mMap = mMap;
            this.mianView = view;
            this.position = position;
            calendar.setTime(new Date());
            today = DateUtil.formatDate(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.get(Calendar.DAY_OF_MONTH) + 1);
            tomorrow = DateUtil.formatDate(calendar.getTime());
            String key = mMap.get(ScheduleTable.schDate);
            String timeKey = mMap.get(ScheduleTable.schTime);
            detail_close = (LinearLayout) view.findViewById(R.id.detail_close);
            detail_close.setOnClickListener(this);
            timebefore_tv = (TextView) view.findViewById(R.id.timebefore_tv);
            detail = (LinearLayout) view.findViewById(R.id.detail);
            after = (LinearLayout) view.findViewById(R.id.after);
            setting = (LinearLayout) view.findViewById(R.id.setting);
            detail_after = (TextView) view.findViewById(R.id.detail_after);
            detail_after.setOnClickListener(this);
            detail_more = (TextView) view.findViewById(R.id.detail_more);
            detail_more.setOnClickListener(this);
            // detail_edit = (LinearLayout) view.findViewById(R.id.detail_edit);
            // detail_edit.setOnClickListener(this);
            // 更多
            more_openstate = (LinearLayout) view
                    .findViewById(R.id.more_openstate);
            more_openstate_tv = (TextView) view
                    .findViewById(R.id.more_openstate_tv);
            more_zhuanfasjb = (TextView) view
                    .findViewById(R.id.more_zhuanfasjb);
            more_zhuanfawx = (TextView) view.findViewById(R.id.more_zhuanfawx);
            more_setdaiban = (TextView) view.findViewById(R.id.more_setdaiban);
            more_shitingmusic = (TextView) view
                    .findViewById(R.id.more_shitingmusic);
            more_delete = (TextView) view.findViewById(R.id.more_delete);
            more_openstate.setOnClickListener(this);
            more_zhuanfasjb.setOnClickListener(this);
            more_zhuanfawx.setOnClickListener(this);
            more_setdaiban.setOnClickListener(this);
            more_shitingmusic.setOnClickListener(this);
            more_delete.setOnClickListener(this);
            // 详情
            detail_zhuanfa = (LinearLayout) view
                    .findViewById(R.id.detail_zhuanfa);
            detail_zhuanfa.setOnClickListener(this);
            detail_edit_ll = (LinearLayout) view
                    .findViewById(R.id.detail_edit_ll);
            detail_edit_ll.setOnClickListener(this);
            detail_date = (TextView) view.findViewById(R.id.detail_date);
            detail_year_date = (TextView) view
                    .findViewById(R.id.detail_year_date);
            detail_tv_shun = (TextView) view.findViewById(R.id.detail_tv_shun);
            String colorState = ""
                    + context.getResources().getColor(R.color.mingtian_color);
            // 顺延
            String sequence = "<font color='" + colorState + "'>"
                    + context.getString(R.string.adapter_shun) + "</font>";
            int shunBackKuang = R.drawable.tv_kuang_aftertime;
            detail_tv_shun.setText(Html.fromHtml(sequence));
            detail_tv_shun.setBackgroundResource(shunBackKuang);
            if (today.equals(key)) {
                detail_year_date.setText("今天");
            } else if (tomorrow.equals(key)) {
                detail_year_date.setText("明天");
            } else {
                detail_year_date.setText(key);
            }
            detail_time_date = (TextView) view
                    .findViewById(R.id.detail_time_date);
            detail_time_date.setText(mMap.get(ScheduleTable.schTime));
            timebefore_tv = (TextView) view.findViewById(R.id.timebefore_tv);
            detail_content = (TextView) view.findViewById(R.id.detail_content);
            detail_content.setText(mMap.get(ScheduleTable.schContent));
            if ("1".equals(mMap.get(ScheduleTable.schIsPostpone))) {
                detail_tv_shun.setVisibility(View.VISIBLE);
            } else {
                detail_tv_shun.setVisibility(View.GONE);
            }
            // 推后
            after_autopostone = (TextView) view
                    .findViewById(R.id.after_autopostone);
            after_autopostone.setOnClickListener(this);
            after_onehour = (TextView) view.findViewById(R.id.after_onehour);
            after_onehour.setOnClickListener(this);
            after_oneday = (TextView) view.findViewById(R.id.after_oneday);
            after_oneday.setOnClickListener(this);
            after_oneweek = (TextView) view.findViewById(R.id.after_oneweek);
            after_oneweek.setOnClickListener(this);
            after_onemonth = (TextView) view.findViewById(R.id.after_onemonth);
            after_onemonth.setOnClickListener(this);
            after_nextweekfirstday = (TextView) view
                    .findViewById(R.id.after_nextweekfirstday);
            after_nextweekfirstday.setOnClickListener(this);
            after_today = (TextView) view.findViewById(R.id.after_today);
            after_today.setOnClickListener(this);
            after_tommrow = (TextView) view.findViewById(R.id.after_tommrow);
            after_tommrow.setOnClickListener(this);
            after_today_view = view.findViewById(R.id.after_today_view);
            after_tommrow_view = view.findViewById(R.id.after_tommrow_view);
            after_onehour_view = view.findViewById(R.id.after_onehour_view);
            after_oneday_view = view.findViewById(R.id.after_oneday_view);
            after_oneweek_view = view.findViewById(R.id.after_oneweek_view);
            after_onemonth_view = view.findViewById(R.id.after_onemonth_view);
            after_nextweekfirstday_view = view
                    .findViewById(R.id.after_nextweekfirstday_view);

            detail_date.setText(CharacterUtil.getWeekOfDate(getActivity(),
                    DateUtil.parseDate(key)));
            Date dateStr = DateUtil.parseDateTime(key + " " + timeKey);
            Date dateToday = DateUtil.parseDateTime(DateUtil
                    .formatDateTime(new Date()));
            long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
            long day = betweem / (24 * 3600);
            long hour = betweem % (24 * 3600) / 3600;
            long min = betweem % 3600 / 60;

            if (today.equals(key)) {// 今天
                if (DateUtil.parseDateTime(DateUtil.formatDateTime(new Date()))
                        .after(DateUtil.parseDateTime(DateUtil
                                .formatDateTime(dateStr)))) {
                    if (Math.abs(hour) >= 1) {
                        timebefore_tv.setText(Math.abs(hour) + "小时前");
                    } else {
                        timebefore_tv.setText(Math.abs(min) + "分钟前");
                    }
                } else {
                    if (Math.abs(hour) >= 1) {
                        timebefore_tv.setText(Math.abs(hour) + "小时后");
                    } else {
                        timebefore_tv.setText(Math.abs(min) + "分钟后");
                    }
                }
            } else if (tomorrow.equals(key)) {// 明天
                if (Math.abs(day) >= 1) {
                    timebefore_tv.setText(Math.abs(day) + "天后");
                } else {
                    timebefore_tv.setText(Math.abs(hour) + "小时后");

                }
            } else {
                timebefore_tv.setText(Math.abs(day) + "天后");
            }
            detail_important = (TextView) view
                    .findViewById(R.id.detail_important);
            detail_important.setOnClickListener(this);
            // detail_edit.setVisibility(View.VISIBLE);
            if ("0".equals(mMap.get(ScheduleTable.schIsPostpone)))
                after_autopostone.setText("自动顺延");
            else
                after_autopostone.setText("取消顺延");
            Drawable jieshu = getResources().getDrawable(
                    R.mipmap.btn_quxiaozhongyao);
            Drawable weijieshu = getResources().getDrawable(
                    R.mipmap.btn_zhongyao);
            // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
            jieshu.setBounds(0, 0, jieshu.getMinimumWidth(),
                    jieshu.getMinimumHeight());
            weijieshu.setBounds(0, 0, jieshu.getMinimumWidth(),
                    jieshu.getMinimumHeight());
            if ("0".equals(mMap.get(ScheduleTable.schIsImportant))) {
                detail_important.setText("设为重要");
                detail_important.setCompoundDrawables(null, jieshu, null, null);
            } else {
                detail_important.setText("取消重要");
                detail_important.setCompoundDrawables(null, weijieshu, null,
                        null);
            }
            int colortype = Integer.parseInt(mMap
                    .get(ScheduleTable.schColorType));
            String colorname = application.QueryTagNameData(colortype).get(
                    "ctgText");
            more_openstate_tv.setText("(" + colorname + ")");
            boolean fag = inWeekUtils.getNextWeek(context, key);
            if (fag) {
                after_autopostone.setVisibility(View.VISIBLE);
                after_onehour.setVisibility(View.VISIBLE);
                after_oneday.setVisibility(View.VISIBLE);
                after_oneweek.setVisibility(View.VISIBLE);
                after_onemonth.setVisibility(View.VISIBLE);
                after_nextweekfirstday.setVisibility(View.VISIBLE);
                after_today.setVisibility(View.GONE);
                after_tommrow.setVisibility(View.GONE);
                after_today_view.setVisibility(View.GONE);
                after_tommrow_view.setVisibility(View.GONE);
                after_onehour_view.setVisibility(View.VISIBLE);
                after_oneday_view.setVisibility(View.VISIBLE);
                after_oneweek_view.setVisibility(View.VISIBLE);
                after_onemonth_view.setVisibility(View.VISIBLE);
                after_nextweekfirstday_view.setVisibility(View.VISIBLE);
            } else {
                after_autopostone.setVisibility(View.VISIBLE);
                after_onehour.setVisibility(View.VISIBLE);
                after_oneday.setVisibility(View.VISIBLE);
                after_oneweek.setVisibility(View.VISIBLE);
                after_onemonth.setVisibility(View.VISIBLE);
                after_nextweekfirstday.setVisibility(View.GONE);
                after_today.setVisibility(View.GONE);
                after_tommrow.setVisibility(View.GONE);
                after_today_view.setVisibility(View.GONE);
                after_tommrow_view.setVisibility(View.GONE);
                after_onehour_view.setVisibility(View.VISIBLE);
                after_oneday_view.setVisibility(View.VISIBLE);
                after_oneweek_view.setVisibility(View.VISIBLE);
                after_onemonth_view.setVisibility(View.VISIBLE);
                after_nextweekfirstday_view.setVisibility(View.GONE);
            }
            // 设置菜单判断
            if (setType == 1) {
                after.setVisibility(View.GONE);
                detail.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Animation translateIn0 = new TranslateAnimation(
                    -mianView.getWidth(), 0, 0, 0);
            Animation translateIn1 = new TranslateAnimation(
                    mianView.getWidth(), 0, 0, 0);
            translateIn0.setDuration(400);
            translateIn1.setDuration(400);
            App app = App.getDBcApplication();
            String key = mMap.get(ScheduleTable.schDate);
            String timeKey = mMap.get(ScheduleTable.schTime);
            Date dateStr;
            long datetime = 0;
            int id = Integer.parseInt(mMap.get(ScheduleTable.schID));
            String date;
            dateStr = DateUtil.parseDateTime(key + " " + timeKey);
            datetime = dateStr.getTime();
            Intent intent = new Intent();
            MySchBean mySchBean = new MySchBean();
            mySchBean.schID = mMap.get(ScheduleTable.schID);
            mySchBean.schContent = mMap.get(ScheduleTable.schContent);
            mySchBean.schDate = mMap.get(ScheduleTable.schDate);
            mySchBean.schTime = mMap.get(ScheduleTable.schTime);
            mySchBean.schIsAlarm = mMap.get(ScheduleTable.schIsAlarm);
            mySchBean.schBeforeTime = mMap.get(ScheduleTable.schBeforeTime);
            mySchBean.schDisplayTime = mMap.get(ScheduleTable.schDisplayTime);
            mySchBean.schIsPostpone = mMap.get(ScheduleTable.schIsPostpone);
            mySchBean.schIsImportant = mMap.get(ScheduleTable.schIsImportant);
            mySchBean.schColorType = mMap.get(ScheduleTable.schColorType);
            mySchBean.schIsEnd = mMap.get(ScheduleTable.schIsEnd);
            mySchBean.schCreateTime = mMap.get(ScheduleTable.schCreateTime);
            mySchBean.schTags = mMap.get(ScheduleTable.schTags);
            mySchBean.schSourceType = mMap.get(ScheduleTable.schSourceType);
            mySchBean.schSourceDesc = mMap.get(ScheduleTable.schSourceDesc);
            mySchBean.schSourceDescSpare = mMap
                    .get(ScheduleTable.schSourceDescSpare);
            mySchBean.schRepeatID = mMap.get(ScheduleTable.schRepeatID);
            mySchBean.schRepeatDate = mMap.get(ScheduleTable.schRepeatDate);
            mySchBean.schUpdateTime = mMap.get(ScheduleTable.schUpdateTime);
            mySchBean.schUpdateState = mMap.get(ScheduleTable.schUpdateState);
            mySchBean.schOpenState = mMap.get(ScheduleTable.schOpenState);
            mySchBean.schRepeatLink = mMap.get(ScheduleTable.schRepeatLink);
            mySchBean.schRingDesc = mMap.get(ScheduleTable.schRingDesc);
            mySchBean.schRingCode = mMap.get(ScheduleTable.schRingCode);
            mySchBean.schcRecommendName = mMap
                    .get(ScheduleTable.schcRecommendName);
            mySchBean.schRead = mMap.get(ScheduleTable.schRead);
            Map<String, String> map = App.getDBcApplication().QueryStateData(
                    Integer.parseInt(mMap.get(ScheduleTable.schRepeatID)));
            switch (v.getId()) {
                case R.id.detail_close:
                    if (closeType != 0) {
                        hint();
                        // detail_edit.setVisibility(View.VISIBLE);
                        detail.setVisibility(View.VISIBLE);
                        detail.startAnimation(translateIn0);
                        closeType = 0;
                    } else {
                        dialog.dismiss();
                    }
                    break;
                case R.id.detail_edit_ll:
                    String i = mMap.get(ScheduleTable.schID);
                    intent.putExtra("id", i);
                    intent.putExtra("content", detail_content.getText().toString());
                    intent.putExtra("year", key);
                    intent.putExtra("time", detail_time_date.getText().toString());
                    intent.putExtra("week", detail_date.getText().toString());
                    intent.putExtra("tixing", timebefore_tv.getText().toString());
                    intent.putExtra("beforetime",
                            mMap.get(ScheduleTable.schBeforeTime));
                    intent.putExtra("openState",
                            mMap.get(ScheduleTable.schOpenState));
                    intent.putExtra("lingshengname",
                            mMap.get(ScheduleTable.schRingDesc));
                    intent.putExtra("ringcode", mMap.get(ScheduleTable.schRingCode));
                    intent.putExtra("recommendID",
                            mMap.get(ScheduleTable.schcRecommendId));
                    intent.putExtra("recommendname",
                            mMap.get(ScheduleTable.schcRecommendName));
                    intent.putExtra("repid", mMap.get(ScheduleTable.schRepeatID));
                    intent.putExtra("repdate",
                            mMap.get(ScheduleTable.schRepeatDate));
                    intent.putExtra("replink",
                            mMap.get(ScheduleTable.schRepeatLink));
                    intent.putExtra("aid", mMap.get(ScheduleTable.schAID));
                    intent.putExtra("friendID", mMap.get(ScheduleTable.schFriendID));
                    intent.putExtra("schIsAlarm",
                            mMap.get(ScheduleTable.schIsAlarm));
                    intent.putExtra("postpone",
                            mMap.get(ScheduleTable.schIsPostpone));
                    intent.putExtra("important",
                            mMap.get(ScheduleTable.schIsImportant));
                    intent.putExtra("coclor", mMap.get(ScheduleTable.schColorType));
                    intent.putExtra("isEnd", mMap.get(ScheduleTable.schIsEnd));
                    intent.putExtra("displaytime",
                            mMap.get(ScheduleTable.schDisplayTime));
                    startActivityForResult(intent.setClass(getActivity(),
                            AddEverydayDetailTaskActivity.class), 100);
                    dialog.dismiss();
                    break;
                case R.id.detail_after:
                    hint();
                    after.setVisibility(View.VISIBLE);
                    detail_zhuanfa.setVisibility(View.GONE);
                    after.startAnimation(translateIn1);
                    break;
                case R.id.detail_more:
                    hint();
                    detail_zhuanfa.setVisibility(View.GONE);
                    setting.setVisibility(View.VISIBLE);
                    setting.startAnimation(translateIn1);
                    break;

                // ---------------更多子项事件--------------------
                case R.id.more_openstate:// 谁可以看
                    try {
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        App.getDBcApplication().updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                                || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                        } else {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                        }
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        // loadData();
                        adapter.notifyDataSetChanged();
                        intent.putExtra("statename", more_openstate_tv.getText()
                                .toString());
                        intent.putExtra("id", mMap.get(ScheduleTable.schID));
                        startActivityForResult(
                                intent.setClass(context, MyStateActivity.class),
                                100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.detail_zhuanfa:
                    intent.putExtra("bean", mySchBean);
                    startActivity(intent
                            .setClass(context, SchZhuanFaActivity.class));
                    dialog.dismiss();
                    break;
                case R.id.more_zhuanfasjb:// 转发时间表好友
                    intent.putExtra("bean", mySchBean);
                    startActivity(intent
                            .setClass(context, SchZhuanFaActivity.class));
                    dialog.dismiss();
                    break;
                case R.id.more_zhuanfawx:// 转发微信好友
                    ShareSDK.initSDK(getActivity());
                    OnekeyShare oks = new OnekeyShare();
                    // 关闭sso授权
                    oks.disableSSOWhenAuthorize();
                    // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
                    // oks.setNotification(R.drawable.ic_launcher,
                    // getString(R.string.app_name));
                    // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                    // oks.setTitle(title);
                    // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                    // oks.setTitleUrl(path);
                    // text是分享文本，所有平台都需要这个字段
                    oks.setText(mySchBean.schDate + "  " + mySchBean.schTime + "  "
                            + mySchBean.schContent);
                    // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                    // oks.setImagePath(ParameterUtil.userHeadImg+imageUrl+"&imageType=2&imageSizeType=3");//
                    // 确保SDcard下面存在此张图片
                    // url仅在微信（包括好友和朋友圈）中使用
                    // oks.setUrl(path);
                    // oks.setImageUrl(URLConstants.图片+imageUrl+"&imageType=2&imageSizeType=3");
                    // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                    // oks.setComment("我是测试评论文本");
                    // site是分享此内容的网站名称，仅在QQ空间使用
                    // oks.setSite(getString(R.string.app_name));
                    // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                    // oks.setSiteUrl("http://sharesdk.cn");

                    // 启动分享GUI
                    oks.show(getActivity());
                    dialog.dismiss();
                    break;
                case R.id.more_setdaiban:// 设为待办 今天+全天+顺延
                    try {
                        app.updateScheduleUnTaskData(mySchBean.schID);
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        App.getDBcApplication().updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        app.updateUnTaskClockDate(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)),
                                ringdesc, ringcode);
                        if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                                || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                        } else {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                        }
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        loadData();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.more_shitingmusic:// 试听铃声
                    final MediaPlayer mediaPlayer = new MediaPlayer();

                    try {
                        AssetFileDescriptor fileDescriptor = getActivity()
                                .getAssets().openFd(
                                        mMap.get(ScheduleTable.schRingCode)
                                                + ".mp3");
                        mediaPlayer.setDataSource(
                                fileDescriptor.getFileDescriptor(),
                                fileDescriptor.getStartOffset(),
                                fileDescriptor.getLength());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.more_delete:// 删除
                    try {
                        alertDeleteDialog(mMap, 0, position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                // --------------推后子项--------------------
                case R.id.after_autopostone:
                    updateSchedule(mMap, ScheduleTable.schIsPostpone,
                            ScheduleTable.schUpdateState);
                    updateSchClock(mMap, LocateAllNoticeTable.postpone);
                    updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                    calendar.setTime(DateUtil.parseDate(mMap
                            .get(ScheduleTable.schDate)));
                    if (DateUtil.parseDate(mMap.get(ScheduleTable.schDate)).before(
                            DateUtil.parseDate(DateUtil.formatDate(new Date())))) {
                        calendar.set(Calendar.DAY_OF_MONTH,
                                calendar.get(Calendar.DAY_OF_MONTH) + 1);
                        date = DateUtil.formatDate(calendar.getTime());
                        app.updateScheduleDateData(id, date,
                                mMap.get(ScheduleTable.schTime));
                    }

                    app.updateSchReadData(
                            Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                    App.getDBcApplication().updateSchRepeatLinkData(
                            Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                    if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                            || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                    } else {
                        if (map != null) {
                            String repdate = mMap
                                    .get(ScheduleTable.schRepeatDate);
                            String lastdate = StringUtils.getIsStringEqulesNull(map
                                    .get(CLRepeatTable.repDateOne));
                            String nextdate = StringUtils.getIsStringEqulesNull(map
                                    .get(CLRepeatTable.repDateTwo));
                            repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                    .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                        }
                    }
                    loadData();
                    QueryAlarmData
                            .writeAlarm(getActivity().getApplicationContext());
                    adapter.notifyDataSetChanged();
                    isNetWork();
                    dialog.dismiss();
                    break;
                case R.id.after_onehour:
                    try {
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        date = inWeekUtils.AfterOneHours(key, timeKey);
                        app.updateScheduleDateData(id, date.substring(0, 10)
                                .toString(), date.substring(11, 16).toString());
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        application.updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                                || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                        } else {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                        }
                        loadCount();
                        loadData();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.after_oneday:
                    try {
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        date = inWeekUtils.AfterOneDay(key);
                        app.updateScheduleDateData(id, date,
                                mMap.get(ScheduleTable.schTime));
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        App.getDBcApplication().updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                                || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                        } else {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                        }
                        loadCount();
                        loadData();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.after_oneweek:
                    try {
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        date = inWeekUtils.AfterOneWeek(key);
                        app.updateScheduleDateData(id, date,
                                mMap.get(ScheduleTable.schTime));
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        App.getDBcApplication().updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                                || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                        } else {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                        }
                        loadCount();
                        loadData();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.after_onemonth:// 推后一个月
                    try {
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        date = inWeekUtils.AfterOneMonth(key);
                        app.updateScheduleDateData(id, date,
                                mMap.get(ScheduleTable.schTime));
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        App.getDBcApplication().updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                                || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                        } else {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                        }
                        loadData();
                        loadCount();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.after_nextweekfirstday:// 推后的下周一
                    try {
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        date = inWeekUtils.AfterNextWeekFirstDay(context, key);
                        app.updateScheduleDateData(id, date,
                                mMap.get(ScheduleTable.schTime));
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        App.getDBcApplication().updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        if ("".equals(mMap.get(ScheduleTable.schRepeatDate))
                                || "0".equals(mMap.get(ScheduleTable.schRepeatLink))) {

                        } else {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                        }
                        loadData();
                        loadCount();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                case R.id.after_today:
                    break;
                case R.id.after_tommrow:
                    break;
                // --------------详情子项事件----------------

                case R.id.detail_important:
                    try {
                        updateFocusStateSch(mMap, ScheduleTable.schFocusState);
                        app.updateSchReadData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        app.updateSchRepeatLinkData(
                                Integer.parseInt(mMap.get(ScheduleTable.schID)), 0);
                        if (!"".equals(mMap.get(ScheduleTable.schRepeatDate))
                                && !"0".equals(mMap
                                .get(ScheduleTable.schRepeatLink))) {
                            if (map != null) {
                                String repdate = mMap
                                        .get(ScheduleTable.schRepeatDate);
                                String lastdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateOne));
                                String nextdate = StringUtils.getIsStringEqulesNull(map
                                        .get(CLRepeatTable.repDateTwo));
                                repeatSetChildEndUtils.setParentState(Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)), repdate, nextdate, lastdate, map);
                            }
                            updateRepSchUpdate(mMap, ScheduleTable.schIsImportant,
                                    ScheduleTable.schUpdateState);
                        } else {
                            updateSchedule(mMap, ScheduleTable.schIsImportant,
                                    ScheduleTable.schUpdateState);
                        }
//                        mList.remove(position);
//                        mMap.put(ScheduleTable.schRepeatLink, "0");
//                        mList.add(position, mMap);
                        loadData();
                        adapter.notifyDataSetChanged();
//                        myschedule_listview.invalidate();
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
            }
        }

        private void hint() {
            closeType = 1;
            after.setVisibility(View.GONE);
            setting.setVisibility(View.GONE);
            detail.setVisibility(View.GONE);
        }
    }

    private void updateDeleteScheduleRead(Map<String, String> mMap, String key,
                                          String key1) {
        try {
            String value = "0";
            Map<String, String> upMap = new HashMap<String, String>();
            myschedule_listview.hiddenRight();
            if ("0".equals(mMap.get(key)))
                value = "0";
            else
                value = "0";
            upMap.put(key, value);
            upMap.put(key1, "2");
            App.getDBcApplication().updateScheduleData(upMap,
                    "where schID=" + mMap.get("schID"));
            mMap.put(key, value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateScheduleRead(Map<String, String> mMap, String key,
                                    String key1) {
        try {
            String value = "0";
            Map<String, String> upMap = new HashMap<String, String>();
            myschedule_listview.hiddenRight();
            if ("0".equals(mMap.get(key)))
                value = "0";
            else
                value = "0";
            upMap.put(key, value);
            upMap.put(key1, "3");
            App.getDBcApplication().updateScheduleData(upMap,
                    "where schID=" + mMap.get("schID"));
            mMap.put(key, value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateScheduleRead1(Map<String, String> mMap, String key) {
        try {
            String value = "0";
            Map<String, String> upMap = new HashMap<String, String>();
            myschedule_listview.hiddenRight();
            // if ("0".equals(mMap.get(key)))
            value = mMap.get(ScheduleTable.schRead);
            // else
            // value = "1";
            upMap.put(key, value);
            App.getDBcApplication().updateScheduleData1(upMap,
                    "where schID=" + mMap.get("schID"));
            mMap.put(key, value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateScheduleRead2(Map<String, String> mMap, String key,
                                     String key1) {
        try {
            String value = "0";
            Map<String, String> upMap = new HashMap<String, String>();
            myschedule_listview.hiddenRight();
            // if ("0".equals(mMap.get(key)))
            value = mMap.get(key);
            // else
            // value = "1";
            upMap.put(key, value);
            upMap.put(key1, "3");
            App.getDBcApplication().updateScheduleData(upMap,
                    "where schID=" + mMap.get("schID"));
            loadData();
            adapter.notifyDataSetChanged();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateSchedule(Map<String, String> mMap, String key,
                                String key1) {
        try {
            String value = "0";
            Map<String, String> upMap = new HashMap<String, String>();
            myschedule_listview.hiddenRight();
            if ("0".equals(mMap.get(key)))
                value = "1";
            else
                value = "0";
            upMap.put(key, value);
            if (!"0".equals(upMap.get(ScheduleTable.schAID))) {
                upMap.put(key1, "2");
            } else {
                upMap.put(key1, "2");
            }
            App.getDBcApplication().updateScheduleData(upMap,
                    "where schID=" + mMap.get("schID"));
            mMap.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateScheduleIsEnd(Map<String, String> mMap, String key,
                                     String key1) {
        try {
            if ("1".equals(mMap.get(ScheduleTable.schRepeatLink))
                    || "3".equals(mMap.get(ScheduleTable.schRepeatLink))) {
                repeatSetChildEndUtils.setParentStateIsEnd(mMap);
            }
            String value = "0";
            Map<String, String> upMap = new HashMap<String, String>();
            myschedule_listview.hiddenRight();
            if ("0".equals(mMap.get(key)))
                value = "1";
            else
                value = "0";
            upMap.put(key, value);
            if ("1".equals(mMap.get(ScheduleTable.schRepeatLink))
                    || "3".equals(mMap.get(ScheduleTable.schRepeatLink))) {
                upMap.put(key1, "0");
            } else {
                upMap.put(key1, "2");
            }
            App.getDBcApplication().updateScheduleData(upMap,
                    "where schID=" + mMap.get("schID"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadData();
        loadCount();
        adapter.setData(mList);
    }

    private void updateRepSchUpdate(Map<String, String> mMap, String key,
                                    String key1) {
        try {
            String value = "0";
            Map<String, String> upMap = new HashMap<String, String>();
            myschedule_listview.hiddenRight();
            if ("0".equals(mMap.get(key)))
                value = "1";
            else
                value = "0";
            upMap.put(key, value);
            if (!"0".equals(upMap.get(ScheduleTable.schAID))) {
                upMap.put(key1, "1");
            } else {
                upMap.put(key1, "1");
            }
            App.getDBcApplication().updateScheduleData(upMap,
                    "where schID=" + mMap.get("schID"));
            mMap.put(key, value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateFocusStateSch(Map<String, String> mMap, String key) {
        try {
            Map<String, String> upMap = new HashMap<String, String>();
            upMap.put(key, "1");
            App.getDBcApplication().updateSchFocusState(upMap,
                    "where schID=" + mMap.get("schID"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateSchClock(Map<String, String> mMap, String key) {
        try {
            String value = "0";
            String key1 = "";
            Map<String, String> upMap = new HashMap<String, String>();
            if (key.equals("isEnd")) {
                key1 = "schIsEnd";
            } else if (key.equals("postpone")) {
                key1 = "schIsPostpone";
            }
            if ("1".equals(mMap.get(key1)))
                value = "1";
            else
                value = "0";
            upMap.put(key, value);
            App.getDBcApplication().updateSchIsEnd(upMap,
                    "where schID=" + mMap.get("schID"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击添加时的对话框
     */
    private void dialogOnClick() {
        Context context = getActivity();
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_addmyschselect, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth();
        dialog.show();

        new MyGeneralOnClick(dialog, more_pop_menu);
    }

    class MyGeneralOnClick implements View.OnClickListener {

        private View view;
        private Dialog dialog;
        private RelativeLayout yuyin_rl;
        private TextView zidingyi_tv;
        private TextView newdaiban_tv;
        private TextView changyong_tv;
        private TextView canel_tv;

        @SuppressLint("NewApi")
        public MyGeneralOnClick(Dialog dialog, View view) {
            this.dialog = dialog;
            this.view = view;
            initview();
        }

        private void initview() {
            // kuaijie_tv = (TextView) view.findViewById(R.id.kuaijie_tv);
            // kuaijie_tv.setOnClickListener(this);
            yuyin_rl = (RelativeLayout) view.findViewById(R.id.yuyin_rl);
            yuyin_rl.setOnClickListener(this);
            zidingyi_tv = (TextView) view.findViewById(R.id.zidingyi_tv);
            zidingyi_tv.setOnClickListener(this);
            newdaiban_tv = (TextView) view.findViewById(R.id.newdaiban_tv);
            newdaiban_tv.setOnClickListener(this);
            changyong_tv = (TextView) view.findViewById(R.id.changyong_tv);
            changyong_tv.setOnClickListener(this);
            canel_tv = (TextView) view.findViewById(R.id.canel_tv);
            canel_tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // case R.id.kuaijie_tv:
                // dialogKuaiJie();
                // dialog.dismiss();
                // break;
                case R.id.yuyin_rl:
                    checkPhonePermission();
                    if(autoFag) {
                        HuaTongDialog();
                    }else{
                        Toast.makeText(context,"权限已禁止访问!",Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                    break;
                case R.id.zidingyi_tv:
                    startActivityForResult(new Intent(context,
                            AddEverydayDetailTaskActivity.class), 100);
                    dialog.dismiss();
                    break;
                case R.id.newdaiban_tv:
                    dialogDaiBan();
                    dialog.dismiss();
                    break;
                case R.id.changyong_tv:
                    dialogChangYong();
                    dialog.dismiss();
                    break;
                case R.id.canel_tv:
                    dialog.dismiss();
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EasyPermissions.SETTINGS_REQ_CODE) {
            boolean hasReadSmsPermission = EasyPermissions.hasPermissions(getContext(),
                    Manifest.permission.RECORD_AUDIO);
            autoFag = hasReadSmsPermission;
        }else{
            loadCount();
            loadData();
            adapter.notifyDataSetChanged();
        }
        // myschedule_listview.setSelectionFromTop(scrolledX, scrolledY);
    }

    /**
     * 新建待办
     */
    private void dialogDaiBan() {
        Context context = getActivity();
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_myschedit, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth() - 20;
        dialog.show();

        new DaiBanOnClick(dialog, more_pop_menu);
    }

    class DaiBanOnClick {

        private View view;
        private Dialog dialog;
        private EditText schcontent_et;
        private TextView head_tv;

        @SuppressLint("NewApi")
        public DaiBanOnClick(Dialog dialog, View view) {
            this.dialog = dialog;
            this.view = view;
            initview();
        }

        private void initview() {
            schcontent_et = (EditText) view.findViewById(R.id.schcontent_et);
            schcontent_et.requestFocus();
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            view.findViewById(R.id.paste_tv).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            schcontent_et.setText(schcontent_et.getText()
                                    .toString() + MainActivity.paste(getActivity()));
                            if (!"".equals(schcontent_et.getText().toString())) {
                                schcontent_et.setSelection(schcontent_et
                                        .getText().toString().length());
                            }
                        }
                    });
            head_tv = (TextView) view.findViewById(R.id.head_tv);
            head_tv.setText("新建待办");
            view.findViewById(R.id.close_ll).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            view.findViewById(R.id.save_ll).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                if (!"".equals(schcontent_et.getText()
                                        .toString().trim())) {
                                    App.getDBcApplication()
                                            .insertScheduleData(
                                                    schcontent_et
                                                            .getEditableText()
                                                            .toString(),
                                                    DateUtil.formatDate(new Date()),
                                                    time,
                                                    1,
                                                    0,
                                                    0,
                                                    1,
                                                    0,
                                                    0,
                                                    0,
                                                    DateUtil.formatDateTimeSs(new Date()),
                                                    "", 0, "", "", 0, "", "",
                                                    1, 0, 0, ringdesc,
                                                    ringcode, "", 0, 0, 0, "",
                                                    "", 0, 0, 0);
                                    loadData();
                                    adapter.notifyDataSetChanged();
                                    // myschedule_listview.setSelection(index);
                                    // myschedule_listview.setSelectionFromTop(
                                    // scrolledX, scrolledY);
                                    // myschedule_listview.set
                                    isNetWork();
                                    QueryAlarmData.writeAlarm(getActivity()
                                            .getApplicationContext());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });

        }
    }

    /**
     * 常用提醒
     */
    private void dialogChangYong() {
        Context context = getActivity();
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_changyong, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth() - 20;
        dialog.show();

        new ChangYongOnClick(dialog, more_pop_menu);
    }

    class ChangYongOnClick {

        private View view;
        private Dialog dialog;
        private LineGridView daojishi_gv;
        private LineGridView selectchangyong_gv;

        @SuppressLint("NewApi")
        public ChangYongOnClick(Dialog dialog, View view) {
            this.dialog = dialog;
            this.view = view;
            initview();
            initdata();
            item();
        }

        private void initview() {
            daojishi_gv = (LineGridView) view.findViewById(R.id.daojishi_gv);
            selectchangyong_gv = (LineGridView) view
                    .findViewById(R.id.selectchangyong_gv);
        }

        private void initdata() {
            List<ChangYongBean> daojiList = new ArrayList<ChangYongBean>();
            List<ChangYongBean> changyongList = new ArrayList<ChangYongBean>();
            daojiList = MyData.getDaoJiShiList();
            changyongList = MyData.getChangYongWakeUpList();
            daojishi_gv
                    .setAdapter(new DaoJiShiTiXingAdapter(context, daojiList, R.layout.adapter_daojishi));
            selectchangyong_gv.setAdapter(new ChangYongYuAdapter(context,
                    changyongList, R.layout.adapter_changyongyu, width));
        }

        private void item() {
            daojishi_gv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    try {
                        ChangYongBean bean = (ChangYongBean) daojishi_gv
                                .getAdapter().getItem(position);
                        String date = "";
                        if (position == 0) {
                            date = DateUtil.formatDateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(DateUtil
                                    .parseDateTime(
                                            DateUtil.formatDateTime(new Date()))
                                    .getTime() + 15 * 60 * 1000)));
                        } else if (position == 1) {
                            date = DateUtil.formatDateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(DateUtil
                                    .parseDateTime(
                                            DateUtil.formatDateTime(new Date()))
                                    .getTime() + 30 * 60 * 1000)));
                        } else if (position == 2) {
                            date = DateUtil.formatDateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(DateUtil
                                    .parseDateTime(
                                            DateUtil.formatDateTime(new Date()))
                                    .getTime() + 45 * 60 * 1000)));
                        } else if (position == 3) {
                            date = DateUtil.formatDateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(DateUtil
                                    .parseDateTime(
                                            DateUtil.formatDateTime(new Date()))
                                    .getTime() + 60 * 60 * 1000)));
                        } else if (position == 4) {
                            date = DateUtil.formatDateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(DateUtil
                                    .parseDateTime(
                                            DateUtil.formatDateTime(new Date()))
                                    .getTime() + 90 * 60 * 1000)));
                        } else if (position == 5) {
                            date = DateUtil.formatDateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(DateUtil
                                    .parseDateTime(
                                            DateUtil.formatDateTime(new Date()))
                                    .getTime() + 120 * 60 * 1000)));
                        }
                        String createtime = DateUtil
                                .formatDateTimeSs(new Date());
                        App.getDBcApplication().insertScheduleData(
                                bean.title + bean.content + "倒计时提醒",
                                DateUtil.formatDate(DateUtil.parseDate(date)),
                                date.substring(11, 16), 1, 0, 1, 0, 0, 0, 0,
                                createtime, "", 0, "", "", 0, "", "", 1, 0, 0,
                                ringdesc, ringcode, "", 0, 0, 0, "", "", 0, 0,
                                0);
                        // Map<String, String> map = application.querySchData(
                        // sdf1.format(sdf1.parse(date)),
                        // date.substring(11, 16), bean.title
                        // + bean.content + "倒计时提醒",createtime);
                        loadData();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelection(index);
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                        // myschedule_listview.set
                        isNetWork();
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            selectchangyong_gv
                    .setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            try {
                                ChangYongBean bean = (ChangYongBean) selectchangyong_gv
                                        .getAdapter().getItem(position);
                                String today = "";
                                String tomorrow = "";
                                String schtime = "";
                                String schdate = "";
                                Calendar calendar = Calendar.getInstance();
                                today = DateUtil.formatDate(calendar.getTime());
                                calendar.set(Calendar.DAY_OF_MONTH,
                                        calendar.get(Calendar.DAY_OF_MONTH) + 1);
                                tomorrow = DateUtil.formatDate(calendar
                                        .getTime());
                                schtime = DateUtil.formatDateTimeHm(DateUtil
                                        .parseDateTimeHm(bean.content));
                                if (DateUtil.parseDateTimeHm(schtime).getTime() > DateUtil
                                        .parseDateTimeHm(
                                                DateUtil.formatDateTimeHm(new Date()))
                                        .getTime()) {
                                    schdate = today;
                                } else {
                                    schdate = tomorrow;
                                }
                                App.getDBcApplication().insertScheduleData(
                                        "起床", schdate, schtime, 1, 0, 1, 0, 0,
                                        0, 0,
                                        DateUtil.formatDateTimeSs(new Date()),
                                        "", 0, "", "", 0, "", "", 1, 0, 0,
                                        "起床", "g_202", "", 0, 0, 0, "", "", 0,
                                        0, 0);
                                loadData();
                                adapter.notifyDataSetChanged();
                                // myschedule_listview.setSelection(index);
                                // myschedule_listview.setSelectionFromTop(
                                // scrolledX, scrolledY);
                                // myschedule_listview.set
                                isNetWork();
                                QueryAlarmData.writeAlarm(getActivity()
                                        .getApplicationContext());
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
        }
    }

    @Override
    public void onRefresh() {
        sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.ENDUPDATETIME, DateUtil.formatDateTimeSs(new Date()));
        pageIndex = 1;
        int noEndCount = App.getDBcApplication().QueryNowGuoQiWeiJieShuCount();// Integer.parseInt(mainMap.get("noEndCount"));
        EventBus.getDefault().post(new PostSendMainActivity(1, noEndCount));
        // if (noEndCount == 0) {
        // tv_schedule_count.setVisibility(View.GONE);
        // } else {
        // tv_schedule_count.setText(noEndCount + "");
        // tv_schedule_count.setVisibility(View.VISIBLE);
        // }
        if (schIDList != null && schIDList.size() > 0) {
            schIDList.clear();
        }
        if (NetUtil.getConnectState(context) == NetWorkState.NONE) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    MainActivity.guoqipostpone();
                    MainActivity.CheckCreateRepeatSchData();
                }
            }).start();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    loadData();
                    onLoad();
                    adapter.notifyDataSetChanged();
                }
            }, 3000);
        } else {
            // isNetWork();
            String updatetime = sharedPrefUtil.getString(context,
                    ShareFile.USERFILE, ShareFile.UPDATETIME,
                    DateUtil.formatDateTimeSs(new Date()));
            if (DateUtil.parseDateTimeSs(DateUtil.formatDateTimeSs(new Date()))
                    .getTime() - DateUtil.parseDateTimeSs(updatetime).getTime() > 5000
                    || DateUtil.parseDateTimeSs(
                    DateUtil.formatDateTimeSs(new Date())).getTime()
                    - DateUtil.parseDateTimeSs(updatetime).getTime() == 0) {
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.UPDATETIME,
                        DateUtil.formatDateTimeSs(new Date()));
                // new Thread(new Runnable() {
                //
                // @Override
                // public void run() {
                // MainActivity.CheckCreateRepeatSchData();
                // }
                // }).start();
                // handler.postDelayed(new Runnable() {
                //
                // @Override
                // public void run() {
                loadData();
                loadCount();
                adapter.notifyDataSetChanged();
                isNetWork();
                // }
                // }, 3000);
            } else {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        MainActivity.guoqipostpone();
                        MainActivity.CheckCreateRepeatSchData();
                    }
                }).start();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadData();
                        loadCount();
                        adapter.notifyDataSetChanged();
                        onLoad();
                    }
                }, 4000);
            }
        }
    }

    @Override
    public void onLoadMore() {
        Log.v("TAG", "onLoadMore()=================");
        pageIndex++;
        // isNetWork();
        // loadData();

        adapter.notifyDataSetChanged();
        onLoad();
    }

    private void onLoad() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        String date = format.format(new Date());
        SwipeXListView swipeXListView = null;
        if (myschedule_listview.getVisibility() == View.VISIBLE) {
            swipeXListView = myschedule_listview;
        } else {
            // swipeXListView = myImpotent_listview;
        }
        swipeXListView.stopRefresh();
        swipeXListView.stopLoadMore();
        swipeXListView.setRefreshTime("刚刚" + date);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        // isNetWork();
        // refreshdata();
        // MainActivity.CheckCreateRepeatSchData();
        if ("0".equals(sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.ISSHOWFIRSTDIALOG, "1"))) {
            sharedPrefUtil.putString(context, ShareFile.USERFILE,
                    ShareFile.ISSHOWFIRSTDIALOG, "1");
            top_right_iv.setBackground(getResources().getDrawable(
                    R.drawable.bg_schrightrl));
            GuideHelper guideHelper = new GuideHelper(getActivity());
            TipData tipData = new TipData(R.mipmap.pic_zi, top_ll_right);
            tipData.setLocation(-DisplayUtils.dipToPix(context, 85),
                    -DisplayUtils.dipToPix(context, 30));
            guideHelper.addPage(tipData);
            guideHelper.addPage(tipData);
            guideHelper.show();
        }
        loadCount();
        // mainoncreate = sharedPrefUtil.getString(context, ShareFile.USERFILE,
        // ShareFile.MAINONCREATE, "0");
        // if ("1".equals(mainoncreate)) {
        // loadData();
        // setDataToAdapter();
        // adapter.notifyDataSetChanged();
        // }
        localpath = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.LOCALPATH, "");
        if (!"".equals(localpath)) {
            if ("0".equals(localpath)) {
                mysch_ll.setBackgroundColor(context.getResources().getColor(
                        R.color.bg_color));
            } else if ("1".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.a), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
            } else if ("2".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.b), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
            } else if ("3".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.c), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
                // mysch_ll.setBackgroundDrawable(context
                // .getResources().getDrawable(R.drawable.c));
            } else if ("4".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.d), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
            } else if ("5".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.e), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
            } else if ("6".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.f), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
            } else if ("7".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.g), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
            } else if ("8".equals(localpath)) {
                mysch_ll.setBackgroundDrawable(zoomDrawable(context
                                .getResources().getDrawable(R.mipmap.h), width,
                        heigth - Utils.dipTopx(context, heigth / 11)));
            } else {
                mysch_ll.setBackgroundDrawable(resizeImage2(localpath, width,
                        heigth));
            }
        } else {
            mysch_ll.setBackgroundDrawable(zoomDrawable(context.getResources()
                            .getDrawable(R.mipmap.a), width,
                    heigth - Utils.dipTopx(context, heigth / 11)));
        }
        // myschedule_listview.onRestoreInstanceState(state);
    }

    // 使用BitmapFactory.Options的inSampleSize参数来缩放
    public static Drawable resizeImage2(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 不加载bitmap到内存中
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight
                    / (height - Utils.dipTopx(context, 53) - (height / 11) - Utils
                    .dipTopx(context, 50))) / 2;
            options.inSampleSize = sampleSize;
        }

        options.inJustDecodeBounds = false;
        return new BitmapDrawable(BitmapFactory.decodeFile(path, options));
    }

    static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成 bitmap
    {
        int width = drawable.getIntrinsicWidth(); // 取 drawable 的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565; // 取 drawable 的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应
        // bitmap
        Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap 的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas); // 把 drawable 内容画到画布中
        return bitmap;
    }

    static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap
            Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
            float scaleWidth = ((float) w / width); // 计算缩放比例
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
            Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                    matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
            return new BitmapDrawable(newbmp); // 把 bitmap 转换成 drawable 并返回
        } else {
            return null;
        }
    }

    /**
     * 登录广播接收器
     */
    private UpdateDataReceiver receiver = null;

    @Override
    public void onDestroy() {
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
        EventBus.getDefault().unregister(this);
        App.getHttpQueues().cancelAll("down");
        App.getHttpQueues().cancelAll("upload");
        App.getHttpQueues().cancelAll("download");
        handler.removeCallbacksAndMessages(null);
        handler1.removeCallbacksAndMessages(null);
        if (schIDList != null && schIDList.size() > 0) {
            schIDList.clear();
        }
        super.onDestroy();
    }

    public class UpdateDataReceiver extends BroadcastReceiver {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("dismiss".equals(intent.getAction())) {
                top_right_iv.setBackgroundColor(Color.parseColor("#00ffffff"));
            } else {
                progressUtil.dismiss();
                String firststate = "0";
                firststate = sharedPrefUtil.getString(context,
                        ShareFile.USERFILE, ShareFile.FIRSTLOGIN, "0");
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.FIRSTLOGIN, "1");
                // 做一些修改界面之类的工作
                String result = intent.getStringExtra("data");
                int index = intent.getIntExtra("index", 0);
                int what = intent.getIntExtra("what", 0);
                if ("0".equals(firststate)) {
                    what = 1;
                }
                System.err.println("what的数字：" + what);
                onLoad();
                if (result.equals("success")) {
                    Message message = Message.obtain();
                    if (index == -10) {
                        message.obj = index;
                        message.what = what;
                        handler1.sendMessage(message);
                    } else {
                        message.obj = index;
                        message.what = what;
                        handler1.sendMessage(message);
                    }
                } else {
                    Message message = Message.obtain();
                    message.obj = "fail";
                    message.what = -2;
                    handler1.sendMessage(message);
                }

            }
        }
    }

    private Handler handler1 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg==null){
                return;
            }
            // int str = (Integer) msg.obj;
            switch (msg.what) {
                case -2:
                    // MainActivity.CheckCreateRepeatSchData();
//				MainActivity.guoqipostpone();
                    loadCount();
                    // loadData();
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
//				MainActivity.guoqipostpone();
                    MainActivity.CheckCreateRepeatSchData();
                    loadData();
                    loadCount();
                    adapter.notifyDataSetChanged();
                    // }
                    break;
                case 0:
                    // MainActivity.CheckCreateRepeatSchData();
                    // loadData();
                    loadCount();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }

    };

    /**
     * 点击头部，弹出以前，重要，重要未结束，取消布局
     */
    /**
     * 普通记事点击弹出详情菜单 setType 0,菜单详情 1,设置
     */
    private void alterImportantDialog() {
        Context context = getActivity();
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_importantandold, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth() - 30;
        dialog.show();

        new alterImportantDialogOnClick(dialog, more_pop_menu);
    }

    class alterImportantDialogOnClick implements View.OnClickListener {

        private View view;
        private Dialog dialog;
        private TextView old_tv;
        private TextView important_tv;
        private TextView come_friends_tv;
        // private TextView fenxiangsch_tv;// 分享日程
        private TextView sousuo_tv;
        private TextView canel_tv;

        @SuppressLint("NewApi")
        public alterImportantDialogOnClick(Dialog dialog, View view) {
            this.dialog = dialog;
            this.view = view;
            initview();
        }

        private void initview() {
            old_tv = (TextView) view.findViewById(R.id.old_tv);
            old_tv.setOnClickListener(this);
            important_tv = (TextView) view.findViewById(R.id.important_tv);
            important_tv.setOnClickListener(this);
            come_friends_tv = (TextView) view
                    .findViewById(R.id.come_friends_tv);
            come_friends_tv.setOnClickListener(this);
            // fenxiangsch_tv = (TextView)
            // view.findViewById(R.id.fenxiangsch_tv);
            // fenxiangsch_tv.setOnClickListener(this);
            sousuo_tv = (TextView) view.findViewById(R.id.sousuo_tv);
            sousuo_tv.setOnClickListener(this);
            canel_tv = (TextView) view.findViewById(R.id.canel_tv);
            canel_tv.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.old_tv:
                    startActivityForResult(new Intent(getActivity(),
                            YiQianActivity.class), 100);
                    dialog.dismiss();
                    break;
                case R.id.important_tv:
                    startActivityForResult(new Intent(getActivity(),
                            ImportantSchActivity.class), 100);
                    dialog.dismiss();
                    break;
                case R.id.come_friends_tv:
                    startActivityForResult(new Intent(getActivity(),
                            ComeFriendSchActivity.class), 100);
                    dialog.dismiss();
                    break;
                // case R.id.fenxiangsch_tv:
                // startActivity(new Intent(context,
                // ShareSchWebViewActivity.class));
                // dialog.dismiss();
                // break;
                case R.id.sousuo_tv:
                    startActivityForResult(new Intent(getActivity(),
                            TagSerachActivity.class), 100);
                    dialog.dismiss();
                    break;
                case R.id.canel_tv:
                    dialog.dismiss();
                    break;
            }
        }
    }

    private void loadCount() {
        int noEndCount = App.getDBcApplication().QueryNowGuoQiWeiJieShuCount();// Integer.parseInt(mainMap.get("noEndCount"));
        // if (noEndCount == 0) {
        // tv_schedule_count.setVisibility(View.GONE);
        // } else {
        // tv_schedule_count.setText(noEndCount + "");
        // tv_schedule_count.setVisibility(View.VISIBLE);
        // }
        EventBus.getDefault().post(new PostSendMainActivity(1, noEndCount));
        // 好友统计数量
        String friendsCountPath = URLConstants.统计好友操作数量 + "?uId=" + userid;
        FriendsTotalAsync(friendsCountPath);
        // tv_my_count = MainActivity.tv_my_count;
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
                                if (countBean.bsqCount == Integer
                                        .parseInt(myfriendscount)) {
                                    EventBus.getDefault().post(
                                            new PostSendMainActivity(2,
                                                    countBean.bsqCount));
                                    // if (countBean.bsqCount == 0) {
                                    // tv_my_count.setVisibility(View.GONE);
                                    // } else {
                                    // tv_my_count.setVisibility(View.VISIBLE);
                                    // tv_my_count.setText(myfriendscount);
                                    // }
                                } else {
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.COUNT, countBean.bsqCount
                                                    + "");
                                    // if (countBean.count == 0) {
                                    // tv_my_count.setVisibility(View.GONE);
                                    // } else {
                                    // tv_my_count.setVisibility(View.VISIBLE);
                                    // tv_my_count.setText(countBean.bsqCount
                                    // + "");
                                    // }
                                    EventBus.getDefault().post(
                                            new PostSendMainActivity(2,
                                                    countBean.bsqCount));
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // tv_my_count.setVisibility(View.GONE);
                            EventBus.getDefault().post(
                                    new PostSendMainActivity(2, 0));
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
     * 讯飞语音
     */
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("TAG", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(context, "初始化失败，错误码：" + code, Toast.LENGTH_LONG)
                        .show();
            }
        }
    };
    String mycontent = "";

    private void xunfeiRecognizer() {
        int ret = 0; // 函数调用返回值
        // 设置参数
        setParam();
        boolean isShowDialog = mSharedPreferences.getBoolean("iat_show", false);
        if (isShowDialog) {
            // 显示听写对话框
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            Toast.makeText(context, "请开始说话…", Toast.LENGTH_SHORT).show();
        } else {
            // 不显示听写对话框
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                // showTip("听写失败,错误码：" + ret);
            } else {
                // showTip(getString(R.string.text_begin));
                // Toast.makeText(context, "请开始说话…", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            // showTip(error.getPlainDescription(true));
            // Toast.makeText(context, error.getPlainDescription(true),
            // Toast.LENGTH_SHORT).show();
        }

    };
    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            // Toast.makeText(context, "开始说话", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            // Toast.makeText(context, error.getPlainDescription(true),
            // Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            // Toast.makeText(context, "结束说话", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("TAG", results.getResultString());
            printResult(results);

            if (isLast) {
                StringBuffer resultBuffer = new StringBuffer();
                for (String key : mIatResults.keySet()) {
                    resultBuffer.append(mIatResults.get(key));
                }
                mycontent = resultBuffer.toString();
                mIatResults.clear();
                System.out.println("=================>>" + mycontent);
                if (!"".equals(mycontent)) {
                    sendMessageDialog(mycontent);
                }
                mycontent = "";
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            // Toast.makeText(context, "当前正在说话，音量大小：" + volume,
            // Toast.LENGTH_SHORT)
            // .show();
            Log.d("TAG", "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }

    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,
                mSharedPreferences.getString("iat_vadbos_preference", "10000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS,
                mSharedPreferences.getString("iat_vadeos_preference", "2000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,
                mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory() + "/msc/iat.wav");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA,
                mSharedPreferences.getString("iat_dwa_preference", "0"));
    }

    private void sendMessageDialog(String content) {
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        params.y = 150;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_sendmessagedialog,
                null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth() - 30;
        dialog.show();
        new SendMessageDetailOnClick(dialog, more_pop_menu, content);
    }

    class SendMessageDetailOnClick implements View.OnClickListener {

        private View mainView;
        private Dialog dialog;
        private String content;
        private String alamsound;
        private String alamsoundDesc;
        private LinearLayout detail_edit;
        private LinearLayout detail_close;
        private Button suresend_bt;
        private TextView time_date;
        private TextView year_date;
        private TextView shunyan_tv;
        private TextView content_tv;
        private TextView timebefore_tv;
        private TextView week_date;
        Map<Object, Object> map;
        String today, tomorrow;
        Calendar calendar = Calendar.getInstance();
        int displaytime, postpone;
        String date;

        public SendMessageDetailOnClick(Dialog dialog, View view, String content) {
            this.mainView = view;
            this.dialog = dialog;
            this.content = content;
            initview();
            initdata();
        }

        public void initview() {
            detail_edit = (LinearLayout) mainView
                    .findViewById(R.id.detail_edit);
            detail_edit.setOnClickListener(this);
            detail_close = (LinearLayout) mainView
                    .findViewById(R.id.detail_close);
            detail_close.setOnClickListener(this);
            suresend_bt = (Button) mainView.findViewById(R.id.suresend_bt);
            suresend_bt.setOnClickListener(this);
            time_date = (TextView) mainView.findViewById(R.id.time_date);
            year_date = (TextView) mainView.findViewById(R.id.year_date);
            shunyan_tv = (TextView) mainView.findViewById(R.id.shunyan_tv);
            content_tv = (TextView) mainView.findViewById(R.id.content_tv);
            timebefore_tv = (TextView) mainView
                    .findViewById(R.id.timebefore_tv);
            week_date = (TextView) mainView.findViewById(R.id.week_date);
            map = ReadWeiXinXml.yuyinSb(context, content);
        }

        public void initdata() {
            if (map != null) {
                if ("0".equals(map.get("di")) && "0".equals(map.get("ti"))) {
                    displaytime = 0;
                    postpone = 1;
                } else {
                    displaytime = 1;
                    postpone = 0;
                }
                alamsound = (String) map.get("ringDesc");
                alamsoundDesc = (String) map.get("ringVal");
                content = (String) map.get("value");

                calendar.setTime(new Date());
                today = DateUtil.formatDate(calendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH,
                        calendar.get(Calendar.DAY_OF_MONTH) + 1);
                tomorrow = DateUtil.formatDate(calendar.getTime());

                content_tv.setText(content);
                date = DateUtil.formatDate(DateUtil.parseDate((String) map
                        .get("date")));
                if (today.equals(date)) {
                    year_date.setText(date);
                } else if (tomorrow.equals(date)) {
                    year_date.setText("明天");
                } else {
                    year_date.setText(date);
                }
                String timestr = "";
                if (displaytime == 0) {
                    time_date.setText("全天");
                    timestr = sharedPrefUtil.getString(context,
                            ShareFile.USERFILE, ShareFile.ALLTIME, "08:30");
                } else {
                    time_date.setText((String) map.get("time"));
                    timestr = time_date.getText().toString();
                }
                week_date.setText(CharacterUtil.getWeekOfDate(context,
                        DateUtil.parseDate(date)));
                String colorState = ""
                        + context.getResources().getColor(
                        R.color.mingtian_color);
                String sequence = "<font color='" + colorState + "'>"
                        + context.getString(R.string.adapter_shun) + "</font>";

                shunyan_tv.setBackgroundResource(R.drawable.tv_kuang_aftertime);
                shunyan_tv.setText(Html.fromHtml(sequence));
                if (0 == postpone) {
                    shunyan_tv.setVisibility(View.GONE);
                } else {
                    shunyan_tv.setVisibility(View.VISIBLE);
                }
                Date dateStr = DateUtil.parseDateTime(date + " " + timestr);
                Date dateToday = DateUtil.parseDateTime(DateUtil
                        .formatDateTime(new Date()));
                long betweem = (long) (dateStr.getTime() - dateToday.getTime()) / 1000;
                long day = betweem / (24 * 3600);
                long hour = betweem % (24 * 3600) / 3600;
                long min = betweem % 3600 / 60;

                if (today.equals(date)) {// 今天
                    if (displaytime == 0 && postpone == 1) {
                        timebefore_tv.setText("今天");
                    } else {
                        if (DateUtil.parseDate(DateUtil.formatDate(new Date()))
                                .after(DateUtil.parseDate(DateUtil
                                        .formatDate(dateStr)))) {
                            if (Math.abs(hour) >= 1) {
                                timebefore_tv.setText(Math.abs(hour) + "小时前");
                            } else {
                                timebefore_tv.setText(Math.abs(min) + "分钟前");
                            }
                        } else {
                            if (Math.abs(hour) >= 1) {
                                timebefore_tv.setText(Math.abs(hour) + "小时后");
                            } else {
                                timebefore_tv.setText(Math.abs(min) + "分钟后");
                            }
                        }
                    }
                } else if (tomorrow.equals(date)) {// 明天
                    if (Math.abs(day) >= 1) {
                        timebefore_tv.setText(Math.abs(day) + "天后");
                    } else {
                        timebefore_tv.setText(Math.abs(hour) + "小时后");
                    }
                } else {
                    timebefore_tv.setText(Math.abs(day) + 1 + "天后");
                }
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.detail_edit:
                    if (!"".equals(content_tv.getText().toString().trim())) {
                        intent = new Intent(context, EditSchActivity.class);
                        intent.putExtra("content", content);
                        intent.putExtra("displaytime", displaytime + "");
                        intent.putExtra("postpone", postpone + "");
                        intent.putExtra("ringdesc", alamsoundDesc);
                        intent.putExtra("ringcode", alamsound);
                        intent.putExtra("date", date);
                        intent.putExtra("time", (String) map.get("time"));
                        startActivityForResult(intent, 100);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "提醒内容不能为空..", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    break;
                case R.id.detail_close:
                    dialog.dismiss();
                    break;
                case R.id.suresend_bt:
                    try {
                        String before = sharedPrefUtil.getString(context,
                                ShareFile.USERFILE, ShareFile.BEFORETIME, "0");
                        App.getDBcApplication().insertScheduleData(content, date,
                                (String) map.get("time"), 1,
                                Integer.parseInt(before), displaytime, postpone, 0,
                                0, 0, DateUtil.formatDateTime(new Date()), "", 0,
                                "", "", 0, "", DateUtil.formatDateTime(new Date()),
                                1, 0, 0, alamsoundDesc, alamsound, "", 0, 0, 0, "",
                                "", 0, 0, 0);
                        loadData();
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        isNetWork();
                        adapter.notifyDataSetChanged();
                        // myschedule_listview.setSelectionFromTop(scrolledX,
                        // scrolledY);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 话筒对话框
     */
    Dialog huatongdialog = null;
    // private GestureDetector mGestureDetector;
    Button yuyin;

    private void HuaTongDialog() {
        // final AlertDialog builder = new AlertDialog.Builder(getActivity())
        // .create();
        // builder.show();
        // Window window = builder.getWindow();
        // android.view.WindowManager.LayoutParams params =
        // window.getAttributes();// 获取LayoutParams
        // params.alpha = 0.92f;
        // window.setAttributes(params);// 设置生效
        // window.setContentView(R.layout.dialog_huatong);
        // Button yuyin = (Button) window.findViewById(R.id.yuyin);
        // yuyin.setOnLongClickListener(new PicOnLongClick());

        huatongdialog = new Dialog(context, R.style.dialog_huatong);
        Window window = huatongdialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_huatong, null);
        yuyin = (Button) more_pop_menu.findViewById(R.id.yuyin);
        LinearLayout yuyin_ll = (LinearLayout) more_pop_menu
                .findViewById(R.id.yuyin_ll);
        huatongdialog.setCanceledOnTouchOutside(true);
        huatongdialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth();
        // yuyin.setOnLongClickListener(new PicOnLongClick());
        // mGestureDetector = new GestureDetector(getActivity(),
        // new MyOnGestureListener());
        yuyin_ll.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    yuyin.setBackgroundDrawable(getActivity().getResources()
                            .getDrawable(R.mipmap.btn_yuyina));
                    xunfeiRecognizer();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    yuyin.setBackgroundDrawable(getActivity().getResources()
                            .getDrawable(R.mipmap.btn_yuyinb));
                    mIat.stopListening();
                    huatongdialog.dismiss();
                }
                // mGestureDetector.onTouchEvent(event);
                return true;
            }

        });
        yuyin.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    yuyin.setBackgroundDrawable(getActivity().getResources()
                            .getDrawable(R.mipmap.btn_yuyina));
                    if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                        xunfeiRecognizer();
                    } else {
                        alertFailDialog();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    yuyin.setBackgroundDrawable(getActivity().getResources()
                            .getDrawable(R.mipmap.btn_yuyinb));
                    mIat.stopListening();
                    huatongdialog.dismiss();
                }
                // mGestureDetector.onTouchEvent(event);
                return true;
            }

        });
        huatongdialog.show();
    }

    private void alertFailDialog() {
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
        delete_tv.setText("请检查您的网络！");
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });

    }

    public void onEventMainThread(FristFragment event) {

        String msg = event.getMsg();
        if ("0".equals(msg)&&isShow) {
            // MainActivity.CheckCreateRepeatSchData();
            loadCount();
            loadData();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        vibrator.cancel();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        // 填充各控件的数据
    }

    @SuppressLint("NewApi")
    @Override
    public void callbackUpdateDialog(int type) {
        if (type == 110) {
            dialog.dismiss();
            if ("0".equals(sharedPrefUtil.getString(context,
                    ShareFile.USERFILE, ShareFile.ISSHOWFIRSTDIALOG, "0"))) {
                sharedPrefUtil.putString(context, ShareFile.USERFILE,
                        ShareFile.ISSHOWFIRSTDIALOG, "1");
                top_right_iv.setBackground(getResources().getDrawable(
                        R.drawable.bg_schrightrl));
                GuideHelper guideHelper = new GuideHelper(getActivity());
                TipData tipData = new TipData(R.mipmap.pic_zi, top_ll_right);
                tipData.setLocation(-DisplayUtils.dipToPix(context, 85),
                        -DisplayUtils.dipToPix(context, 30));
                guideHelper.addPage(tipData);
                guideHelper.addPage(tipData);
                guideHelper.show();
                // RelativeLayout.LayoutParams layoutParams = (LayoutParams)
                // top_right_iv.getLayoutParams();
                // layoutParams.width = Utils.dipTopx(getActivity(), 40);
                // layoutParams.height = Utils.dipTopx(getActivity(), 40);
                // top_right_iv.setLayoutParams(layoutParams);

            }
        } else if (type == 120) {
            Intent tohelp = new Intent(getActivity(), HelpActivity.class);
            startActivity(tohelp);
            dialog.dismiss();
        }
    }
    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void checkPhonePermission() {
        if(Build.VERSION.SDK_INT<23){
            autoFag = true;
        }else {
            String[] perms = {Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO};
            if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                // Have permissions, do the thing!
                autoFag = true;
            } else {
                // Ask for both permissions
                EasyPermissions.requestPermissions(this, "该应用需要这些权限，为了保证应用正常运行!",
                        RC_LOCATION_CONTACTS_PERM, perms);
            }
            sharedPrefUtil.putString(getActivity(),ShareFile.USERFILE,ShareFile.PERMISSIONSTATE,"1");
        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkstorgePhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.STORAGE);
//            if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
//            }
//            int checkcontactPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.CONTACTS);
//            if(checkcontactPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},10002);
//            }
//            int checkLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.LOCATION);
//            if(checkLocationPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10003);
//            }
//            int checkPhoneStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.LOCATION);
//            if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_PHONE_STATE},10004);
//            }
//            int checkautioStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.MICROPHONE);
//            if(checkautioStatePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},10005);
//            }else{
//                autoFag = true;
//            }
//        }else{
//            autoFag = true;
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        if(PackageManager.PERMISSION_GRANTED==grantResults[3]){
            autoFag = true;
        }else{
            autoFag = false;
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("TAG", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
// (Optional) Check whether the user denied permissions and checked NEVER ASK AGAIN.
        // This will display a dialog directing them to enable the permission in app settings.
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
                getString(R.string.rationale_ask_again),
                R.string.action_settings, R.string.cancel, null, perms);
    }
}
