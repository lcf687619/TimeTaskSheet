package com.mission.schedule.applcation;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iflytek.cloud.SpeechUtility;
import com.mission.schedule.R;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.bean.TagCommandBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.db.DBHelper;
import com.mission.schedule.entity.CLAdsTable;
import com.mission.schedule.entity.CLCategoryTable;
import com.mission.schedule.entity.CLFindScheduleTable;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.FMessages;
import com.mission.schedule.entity.FriendsTable;
import com.mission.schedule.entity.LocateAllMemoTable;
import com.mission.schedule.entity.LocateAllNoticeTable;
import com.mission.schedule.entity.LocateOldAllNoticeTable;
import com.mission.schedule.entity.LocateRepeatNoticeTable;
import com.mission.schedule.entity.LocateSolarToLunar;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.utils.CalendarChangeValue;
import com.mission.schedule.utils.CharacterUtil;
import com.mission.schedule.utils.DateTimeHelper;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class App extends Application implements Thread.UncaughtExceptionHandler {//
    private static App mContextApplication = null;
    private DBHelper helper = null;
    private SharedPrefUtil sp;
    public static int repschId;
    public static int schID;
    public static int nfmId;
    public static int fstRepeatId;
    public static RequestQueue queues;

    @Override
    public void onCreate() {
        super.onCreate();
        mContextApplication = this;
        queues = Volley.newRequestQueue(getApplicationContext());
        sp = new SharedPrefUtil(mContextApplication, ShareFile.USERFILE);
        initDBHelper(this);
        initImageLoader(getApplicationContext());
//		CrashReport.initCrashReport(getApplicationContext(), "900033407", false);
        String pushAlias = sp.getString(mContextApplication,
                ShareFile.USERFILE, ShareFile.PUSH_ALIAS, "0");// 第一次
        if ("0".equals(pushAlias)) {
            // 调用JPush API设置Alias
            JPushInterface
                    .setAliasAndTags(getApplicationContext(),
                            JPushInterface.getUdid(getApplicationContext()),
                            null, null);
            sp.putString(mContextApplication, ShareFile.USERFILE,
                    ShareFile.PUSH_ALIAS, "1");
        }
        // JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this); // 初始化 JPush
        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用半角“,”分隔。
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误

        SpeechUtility.createUtility(getApplicationContext(), "appid="
                + getString(R.string.app_id));

        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        // Setting.setShowLog(false);
    }

    public static RequestQueue getHttpQueues() {
        return queues;
    }

    private void initDBHelper(Context context) {
        if (helper == null) {
            helper = new DBHelper(context);
        }
    }

    public static App getDBcApplication() {
        return mContextApplication;
    }

    private List<Map<String, String>> mChooseList;

    public List<Map<String, String>> getmChooseList() {
        return mChooseList;
    }

    public void setmChooseList(List<Map<String, String>> mChooseList) {
        this.mChooseList = mChooseList;
    }

    /**
     * 查询我的时间表列表集合
     *
     * @param timeType -1上传新建日程，updatestate不为0的都上传
     *                 0 待办 今天以前+全天+未结束+自动顺延 隐藏未结束的待办 1待办 今天以前+全天+未结束+自动顺延 显示未结束的待办
     *                 显示未结束的待办 2今天 + 不顺延 + 显示时间+未结束3 明天 4一周以内 5一周以外 7 今天 + 不顺延 + 显示时间+所有
     *                 8以前,9今后  10  两天以前+未结束+未读 11 昨天+未结束+未读12今天+未结束+未读 13明天+未结束+未读 14一周以内+未结束+未读
     *                 15一周以后+未结束+未读 16全部+未结束+未读 17 昨天+未结束 18 昨天以前+未结束 19 查询以前顺延状态，并且未结束
     *                 20  查询SChAID相同的日程 21 日程分享webview 22 昨天+全部  23 昨天以前+全部
     *                 24 全部+重要 25 两天以前+重要 26 昨天+重要 27今天+重要 28 明天+重要 29一周以内+重要 30一周以后+重要
     *                 31 两天以前+重要+未结束 32 昨天+重要+未结束 33今天+重要+未结束  34明天+重要+未结束  35一周以内+重要+未结束 36一周以后+重要+未结束
     *                 37 全部+来自好友 38 两天以前+来自好友 39 昨天+来自好友 40今天+来自好友 41明天+来自好友 42一周以内+来自好友 43一周以后+来自好友
     *                 44两天以前+来自好友+未结束 45 昨天+来自好友+未结束 46今天+来自好友+未结束 47明天+来自好友+未结束 48一周以内+来自好友+未结束49一周以后+来自好友+未结束
     *                 50 两天以前+分类 51 昨天+分类 52今天+分类 53明天+分类 54一周以内+分类 55一周以后+分类
     *                 56 两天以前+分类+未结束 57 昨天+分类+未结束 58今天+分类+未结束 59明天+分类+未结束 60一周以内+分类+未结束 61一周以后+分类+未结束
     */

    public List<Map<String, String>> queryAllSchData(int timeType, int schAID, int colortype)
            throws Exception {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "";
        String yestoday;// 昨天
        String today;// 今天
        String tomorrow;// 明天
        String inweek;// 一周以内
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) - 1);
        yestoday = DateUtil.formatDate(calendar.getTime());
        calendar.setTime(new Date());
        today = DateUtil.formatDate(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow = DateUtil.formatDate(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 7);
        inweek = DateUtil.formatDate(calendar.getTime());

        switch (timeType) {
            case -5://上传日程中删除的数据
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schUpdateState + " = " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case -4://查询数据库中所有日程数据
                sql = "select * from " + ScheduleTable.ScheduleTable;
                break;
            case -3:// 待办 今天+不显示时间+顺延   闹钟相关
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today + "' and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schRepeatID + " = " + 0 + " and "
                        + ScheduleTable.schDisplayTime + " = " + 0 + " and "
                        + ScheduleTable.schIsPostpone + " = " + 1
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case -2://闹钟相关
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + today + "' and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schRepeatID + " = " + 0 + " and (("
                        + ScheduleTable.schDisplayTime + " = " + 1 + " and "
                        + ScheduleTable.schIsPostpone + " = " + 0 + ") or ("
                        + ScheduleTable.schDisplayTime + " = " + 1 + " and "
                        + ScheduleTable.schIsPostpone + " = " + 1
                        + ")) order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case -1://上传新建日程，updatestate不为0的都上传
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schUpdateState + " != " + 0 + " and "
                        + ScheduleTable.schRepeatLink + " != " + 1 + " and "
                        + ScheduleTable.schRepeatLink + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 0:// 待办 今天以前+全天+未结束+自动顺延 隐藏未结束的待办
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate=='"
                        + today
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " and "
                        + ScheduleTable.schIsEnd
                        + " != "
                        + 1
                        + " and "
                        + ScheduleTable.schDisplayTime
                        + " != "
                        + 1
                        + " and "
                        + ScheduleTable.schIsPostpone
                        + " != "
                        + 0
                        + " order by schDate asc,schDisplayTime asc,schCreateTime desc ";
                break;
            case 1:// 待办 今天以前+全天+未结束+自动顺延 显示未结束的待办
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate=='"
                        + today
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " and "
                        + ScheduleTable.schDisplayTime
                        + " != "
                        + 1
                        + " and "
                        + ScheduleTable.schIsPostpone
                        + " != "
                        + 0
                        + " order by schDate asc,schDisplayTime asc,schCreateTime desc";
                break;
            case 2:// 今天 + 不顺延 + 显示时间 + 未结束
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate =='"
                        + today
                        + "' and "
                        + ScheduleTable.schIsEnd
                        + " != "
                        + 1
                        + " and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " and ("
                        + ScheduleTable.schDisplayTime
                        + " != "
                        + 0
                        + " or "
                        + ScheduleTable.schIsPostpone
                        + " != "
                        + 1
                        + ") order by schDate asc,schDisplayTime asc,schTime asc,schCreateTime asc  ";
                break;
            case 3:// 明天
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate ='"
                        + tomorrow
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " order by schDate asc,schDisplayTime asc,schTime asc,schCreateTime asc  ";
                break;
            case 4:// 一周以内
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate <'"
                        + inweek
                        + "' and "
                        + " schDate >'"
                        + tomorrow
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " order by schDate asc,schDisplayTime asc,schTime asc,schCreateTime asc  ";
                break;
            case 5:// 一周以外
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate >='"
                        + inweek
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " order by schDate asc,schDisplayTime asc,schTime asc,schCreateTime asc  ";
                break;
            case 6:
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + "schDate =='" + DateUtil.formatDate(new Date())
                        + "' and " + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate asc,schDisplayTime asc,schTime asc ";
                break;
            case 7:// 今天 + 不顺延 + 显示时间 + 所有
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate =='"
                        + today
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " and ("
                        + ScheduleTable.schDisplayTime
                        + " != "
                        + 0
                        + " or "
                        + ScheduleTable.schIsPostpone
                        + " != "
                        + 1
                        + ") order by schDate asc,schDisplayTime asc,schTime asc,schCreateTime asc  ";
                break;
            case 8:// 8以前
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + "schDate<'" + DateUtil.formatDate(new Date()) + "'and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc ";
                break;

            case 9:// 今后DateUtil.formatDate(new Date())
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + "schDate>'"
                        + today
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " order by schDate asc,schDisplayTime asc,schTime asc,schCreateTime asc ";
                break;
            case 10:// 两天以前+未结束+未读
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schRead + " = " + 1 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 11:// 昨天+未结束+未读
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schRead + " = " + 1 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 12:// 今天+未结束+未读
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schRead + " = " + 1 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 13:// 明天+未结束+未读
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schRead + " = " + 1 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 14:// 一周以内+未结束+未读
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schRead + " = " + 1 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " > '" + tomorrow + "' and "
                        + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 15:// 一周以后+未结束+未读
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schRead + " = " + 1 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 16://d大于今天的全部+未读
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schRead + " = " + 1 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 17:// 昨天+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday + "' and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 18:// 昨天以前+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday + "' and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 19://查询以前顺延状态，并且未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schDate + " < '" + today + "' and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and ("
                        + ScheduleTable.schRepeatID + " = " + 0 + " or ("
                        + ScheduleTable.schRepeatID + " != " + 0 + " and "
                        + ScheduleTable.schRepeatLink + " != " + 1
                        + ")) order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 20:// 查询SChAID相同的日程
                sql = "select * from " + ScheduleTable.ScheduleTable
                        + " where " + ScheduleTable.schAID + " = " + schAID
                        + " and " + ScheduleTable.schAID + " > " + 0;
                break;
            case 21:// 日程分享webview
                sql = "select * from "
                        + ScheduleTable.ScheduleTable
                        + " where "
                        + " schDate >='"
                        + today
                        + "' and "
                        + ScheduleTable.schUpdateState
                        + " != "
                        + 3
                        + " and "
                        + ScheduleTable.schOpenState
                        + " = "
                        + 1
                        + " and "
                        + ScheduleTable.schIsEnd
                        + " = "
                        + 0
                        + " order by schDate asc,schDisplayTime asc,schTime asc,schCreateTime asc";
                break;
            case 22:// 昨天+全部
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schDate + " = '" + yestoday + "' and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 23:// 昨天以前+全部
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schDate + " < '" + yestoday + "' and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 24://重要全部
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 25:// 两天以前+重要
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 26:// 昨天+重要
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 27:// 今天+重要
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 28:// 明天+重要
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 29:// 一周以内+重要
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " > '" + tomorrow + "' and "
                        + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 30:// 一周以后+重要
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 31:// 两天以前+重要+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday + "' and "
                        + ScheduleTable.schIsEnd + " = " + 0
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 32:// 昨天+重要+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday + "' and "
                        + ScheduleTable.schIsEnd + " = " + 0
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 33:// 今天+重要+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 34:// 明天+重要+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 35:// 一周以内+重要+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " > '" + tomorrow + "' and "
                        + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 36:// 一周以后+重要+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schIsImportant + " = " + 1 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 37://全部+来自好友
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 38:// 两天以前+来自好友
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 39:// 昨天+来自好友
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 40:// 今天+来自好友
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 41:// 明天+来自好友
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 42:// 一周以内+来自好友
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " > '" + tomorrow + "' and "
                        + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 43:// 一周以后+来自好友
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 44:// 两天以前+来自好友+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday + "' and "
                        + ScheduleTable.schIsEnd + " = " + 0
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 45:// 昨天+来自好友+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday + "' and "
                        + ScheduleTable.schIsEnd + " = " + 0
                        + " order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 46:// 今天+来自好友+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 47:// 明天+来自好友+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 48:// 一周以内+来自好友+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " > '" + tomorrow + "' and "
                        + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 49:// 一周以后+来自好友+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schcRecommendId + " > " + 0 + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 50:// 两天以前+分类
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 51:// 昨天+分类
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 52:// 今天+分类
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 53:// 明天+分类
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 54:// 一周以内+分类
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " > '" + tomorrow + "' and "
                        + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 55:// 一周以后+分类
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 56:// 两天以前+分类+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schDate + " < '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 57:// 昨天+分类+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schIsEnd + " = " + 0 + " and "
                        + ScheduleTable.schDate + " = '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 58:// 今天+分类+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 59:// 明天+分类+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 60:// 一周以内+分类+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " > '" + tomorrow + "' and "
                        + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 61:// 一周以后+分类+未结束
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schColorType + " = " + colortype + " and "
                        + ScheduleTable.schUpdateState + " != " + 3 + " and "
                        + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;

        }
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(ScheduleTable.schID, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schID)));
                noticeMap.put(ScheduleTable.schContent, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schContent)));
                noticeMap.put(ScheduleTable.schDate, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schDate)));
                noticeMap.put(ScheduleTable.schTime, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schTime)));
                noticeMap.put(ScheduleTable.schIsAlarm, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schIsAlarm)));
                noticeMap.put(ScheduleTable.schBeforeTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schBeforeTime)));
                noticeMap.put(ScheduleTable.schDisplayTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schDisplayTime)));
                noticeMap.put(ScheduleTable.schIsPostpone, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schIsPostpone)));
                noticeMap.put(ScheduleTable.schIsImportant, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schIsImportant)));
                noticeMap.put(ScheduleTable.schColorType, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schColorType)));
                noticeMap.put(ScheduleTable.schIsEnd, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schIsEnd)));
                noticeMap.put(ScheduleTable.schCreateTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schCreateTime)));
                noticeMap.put(ScheduleTable.schTags, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schTags)));
                noticeMap.put(ScheduleTable.schSourceType, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schSourceType)));
                noticeMap.put(ScheduleTable.schSourceDesc, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schSourceDesc)));
                noticeMap
                        .put(ScheduleTable.schSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schSourceDescSpare)));
                noticeMap.put(ScheduleTable.schRepeatID, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatID)));
                noticeMap.put(ScheduleTable.schRepeatDate, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatDate)));
                noticeMap.put(ScheduleTable.schUpdateTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schUpdateTime)));
                noticeMap.put(ScheduleTable.schUpdateState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schUpdateState)));
                noticeMap.put(ScheduleTable.schOpenState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schOpenState)));
                noticeMap.put(ScheduleTable.schRepeatLink, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatLink)));
                noticeMap.put(ScheduleTable.schRingDesc, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRingDesc)));
                noticeMap.put(ScheduleTable.schRingCode, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRingCode)));
                noticeMap
                        .put(ScheduleTable.schcRecommendName,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schcRecommendName)));
                noticeMap.put(ScheduleTable.schRead, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schRead)));
                noticeMap.put(ScheduleTable.schAID, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schAID)));
                noticeMap.put(ScheduleTable.schAType, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schAType)));
                noticeMap.put(ScheduleTable.schWebURL, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schWebURL)));
                noticeMap.put(ScheduleTable.schImagePath, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schImagePath)));
                noticeMap.put(ScheduleTable.schFocusState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schFocusState)));
                noticeMap.put(ScheduleTable.schFriendID, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schFriendID)));
                noticeMap
                        .put(ScheduleTable.schcRecommendId,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schcRecommendId)));
                dataList.add(noticeMap);

            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 设置更新提醒日程的读取状态 0已读 1未读
     */
    public void updateReadState(int schID, int update) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schRead + " = " + 0 + " , "
                + ScheduleTable.schUpdateState + " = " + update + " where "
                + ScheduleTable.schID + " = " + schID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置更新提醒日程的读取状态 0已读 1未读
     */
    public void updateReadState1(int schID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schRead + " = " + 0 + " where "
                + ScheduleTable.schID + " = " + schID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置更新提醒日程的读取状态 0已读 1未读
     */
    public void updateRepeateReadState(int repID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                + CLRepeatTable.repRead + " = " + 0 + " where "
                + CLRepeatTable.repID + " = " + repID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 今天已结束数量
     */
    public int QueryTodayJieShuCount() {
        int result = 0;
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "select count(*) from " + ScheduleTable.ScheduleTable
                + " where " + ScheduleTable.schIsEnd + " = " + 1 + " and "
                + ScheduleTable.schDate + " = '"
                + DateUtil.formatDate(new Date()) + "'and "
                + ScheduleTable.schUpdateState + " != " + 3;
        try {
            Cursor cursor1 = sqldb.rawQuery(sql, null);
            if (null != cursor1 && cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                result = cursor1.getInt(0);
                cursor1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    /**
     * 过期未结束数量
     *
     * @return
     */
    public int QueryGuoQiWeiJieShuCount() {
        int result = 0;
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "select count(*) from " + ScheduleTable.ScheduleTable
                + " where " + ScheduleTable.schIsEnd + " = " + 0 + " and "
                + ScheduleTable.schDate + " < '"
                + DateUtil.formatDate(new Date()) + "'and "
                + ScheduleTable.schUpdateState + " != " + 3;
        try {
            Cursor cursor1 = sqldb.rawQuery(sql, null);
            if (null != cursor1 && cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                result = cursor1.getInt(0);
                cursor1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    /**
     *
     */
    /**
     * 查询过期未结束数量 和当前时间进行比较
     *
     * @return
     */
    public int QueryNowGuoQiWeiJieShuCount() {
        int result = 0;
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "select count(*) from " + ScheduleTable.ScheduleTable
                + " where " + ScheduleTable.schIsEnd + " = " + 0 + " and (("
                + ScheduleTable.schDate + " < '"
                + DateUtil.formatDate(new Date()) + "') or ("
                + ScheduleTable.schDate + " <= '"
                + DateUtil.formatDate(new Date()) + "' and "
                + ScheduleTable.schTime + " < '"
                + DateUtil.formatDateTimeHm(new Date()) + "' and (("
                + ScheduleTable.schDisplayTime + " = " + 1 + " and "
                + ScheduleTable.schIsPostpone + " = " + 1 + ") or ("
                + ScheduleTable.schDisplayTime + " = " + 0 + " and "
                + ScheduleTable.schIsPostpone + " = " + 0 + ") or ("
                + ScheduleTable.schDisplayTime + " = " + 1 + " and "
                + ScheduleTable.schIsPostpone + " = " + 0 + ")))) and "
                + ScheduleTable.schUpdateState + " != " + 3;
        // + " and (" + ScheduleTable.schDisplayTime + " = " + 1
        // + " and " + ScheduleTable.schIsPostpone + " = " + 0
        // + " and " + ScheduleTable.schDate + " = '" + DateUtil.formatDate(new
        // Date())+"')";// + " and "+
        // ScheduleTable.schDisplayTime
        // + " != " + 0
        try {
            Cursor cursor1 = sqldb.rawQuery(sql, null);
            if (null != cursor1 && cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                result = cursor1.getInt(0);
                cursor1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    /**
     * 修改过期未结束
     */
    public void updateGuoQiWeiJieShu() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schIsEnd + " = " + 1 + " where "
                + ScheduleTable.schIsEnd + " = " + 0 + " and "
                + ScheduleTable.schUpdateState + " != " + 3 + " and "
                + ScheduleTable.schDate + " < '"
                + DateUtil.formatDate(new Date()) + "'";
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author li 新建日程 ScheduleTable 表名 schID 日程ID schContent 内容 schDate 日期
     * schTime 时间 schIsAlarm 是否有提醒 默认0无1有 schBeforeTime 提前时间 0
     * schDisplayTime 是否显示时间 0 schIsPostpone 是否顺延 0 schIsImportant 是否重要0
     * schColorType 颜色分类 0 schIsEnd 是否结束 0 schCreateTime 创建时间 schTags
     * 所在分类 "" schSourceType 来源分类(记录是否是链接)0 普通记事 schSourceDesc 来源描述(链接)
     * schSourceDescSpare 来源备用(链接描述) schRepeatID 重复ID schRepeatDate 重复时间
     * schUpdateTime 同步更新时间 schUpdateState 同步更新状态 schOpenState 打开状态
     * schRepeatLink 是否与母计时脱钩 0 schRingDesc 铃声描述 schRingCode 铃声编码
     * schcRecommendName 昵称
     */

    public boolean insertScheduleData(String schContent, String schDate,
                                      String schTime, int schIsAlarm, int schBeforeTime,
                                      int schDisplayTime, int schIsPostpone, int schIsImportant,
                                      int schColorType, int schIsEnd, String schCreateTime,
                                      String schTags, int schSourceType, String schSourceDesc,
                                      String schSourceDescSpare, int schRepeatID, String schRepeatDate,
                                      String schUpdateTime, int schUpdateState, int schOpenState,
                                      int schRepeatLink, String schRingDesc, String schRingCode,
                                      String schcRecommendName, int schRead, int schAID, int schAType,
                                      String schWebURL, String schImagePath, int schFocusState,
                                      int schFriendID, int schcRecommendId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        schID = getLocalId(-1, "ScheduleTable", ScheduleTable.schID);
        String content = schContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        int ID = getLocalId(1, "ScheduleTable", ScheduleTable.ID);
        String sql = "insert into ScheduleTable(ID, schID,schContent,schDate,schTime,"
                + "schIsAlarm, schBeforeTime,schDisplayTime, schIsPostpone,schIsImportant,"
                + "schColorType, schIsEnd, schCreateTime,schTags, schSourceType,schSourceDesc,"
                + "schSourceDescSpare, schRepeatID, schRepeatDate,schUpdateTime, schUpdateState,"
                + " schOpenState,schRepeatLink, schRingDesc, schRingCode,schcRecommendName, "
                + "schRead, schAID, schAType,schWebURL, schImagePath, schFocusState,schFriendID,"
                + " schcRecommendId) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{ID, schID,
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(schDate),
                StringUtils.getIsStringEqulesNull(schTime), schIsAlarm,
                schBeforeTime, schDisplayTime, schIsPostpone, schIsImportant,
                schColorType, schIsEnd,
                StringUtils.getIsStringEqulesNull(schCreateTime),
                StringUtils.getIsStringEqulesNull(schTags), schSourceType,
                StringUtils.getIsStringEqulesNull(schSourceDesc),
                StringUtils.getIsStringEqulesNull(schSourceDescSpare),
                schRepeatID, StringUtils.getIsStringEqulesNull(schRepeatDate),
                StringUtils.getIsStringEqulesNull(schUpdateTime),
                schUpdateState, schOpenState, schRepeatLink,
                StringUtils.getIsStringEqulesNull(schRingDesc),
                StringUtils.getIsStringEqulesNull(schRingCode),
                StringUtils.getIsStringEqulesNull(schcRecommendName), schRead,
                schAID, schAType, StringUtils.getIsStringEqulesNull(schWebURL),
                StringUtils.getIsStringEqulesNull(schImagePath), schFocusState,
                schFriendID, schcRecommendId};
        // String sql = "insert into ScheduleTable values(" + id + "," + schID
        // + ",'" + content + "','" + schDate + "','" + schTime + "',"
        // + schIsAlarm + "," + schBeforeTime + "," + schDisplayTime + ","
        // + schIsPostpone + "," + schIsImportant + "," + schColorType
        // + "," + schIsEnd + ",'" + schCreateTime + "','" + schTags
        // + "'," + schSourceType + ",'" + schSourceDesc + "','"
        // + schSourceDescSpare + "'," + schRepeatID + ",'"
        // + schRepeatDate + "','" + schUpdateTime + "'," + schUpdateState
        // + "," + schOpenState + "," + schRepeatLink + ",'" + schRingDesc
        // + "','" + schRingCode + "','" + schcRecommendName + "',"
        // + schRead + " , " + schAID + " , " + schAType + " , '"
        // + schWebURL + "', '" + schImagePath + "'," + schFocusState
        // + "," + schFriendID + "," + schcRecommendId + ")";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 网上下载，没有的下载插入
     */
    public boolean insertIntenetScheduleData(Integer id, String schContent,
                                             String schDate, String schTime, Integer schIsAlarm,
                                             Integer schBeforeTime, Integer schDisplayTime,
                                             Integer schIsPostpone, Integer schIsImportant,
                                             Integer schColorType, Integer schIsEnd, String schCreateTime,
                                             String schTags, Integer schSourceType, String schSourceDesc,
                                             String schSourceDescSpare, Integer schRepeatID,
                                             String schRepeatDate, String schUpdateTime, Integer schUpdateState,
                                             Integer schOpenState, Integer schRepeatLink, String schRingDesc,
                                             String schRingCode, String schcRecommendName, int schRead,
                                             int schAID, int schAType, String schWebURL, String schImagePath,
                                             int schFocusState, int schFriendID, int schcRecommendId) {
        String content = schContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int ID = getLocalId(1, "ScheduleTable", ScheduleTable.ID);
        String sql = "insert into ScheduleTable(ID,schID,schContent,schDate,schTime,"
                + "schIsAlarm, schBeforeTime,schDisplayTime, schIsPostpone,schIsImportant,"
                + "schColorType, schIsEnd, schCreateTime,schTags, schSourceType,schSourceDesc,"
                + "schSourceDescSpare, schRepeatID, schRepeatDate,schUpdateTime, schUpdateState,"
                + " schOpenState,schRepeatLink, schRingDesc, schRingCode,schcRecommendName, "
                + "schRead, schAID, schAType,schWebURL, schImagePath, schFocusState,schFriendID,"
                + " schcRecommendId) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{ID, id,
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(schDate),
                StringUtils.getIsStringEqulesNull(schTime), schIsAlarm,
                schBeforeTime, schDisplayTime, schIsPostpone, schIsImportant,
                schColorType, schIsEnd,
                StringUtils.getIsStringEqulesNull(schCreateTime),
                StringUtils.getIsStringEqulesNull(schTags), schSourceType,
                StringUtils.getIsStringEqulesNull(schSourceDesc),
                StringUtils.getIsStringEqulesNull(schSourceDescSpare),
                schRepeatID, StringUtils.getIsStringEqulesNull(schRepeatDate),
                StringUtils.getIsStringEqulesNull(schUpdateTime),
                schUpdateState, schOpenState, schRepeatLink,
                StringUtils.getIsStringEqulesNull(schRingDesc),
                StringUtils.getIsStringEqulesNull(schRingCode),
                StringUtils.getIsStringEqulesNull(schcRecommendName), schRead,
                schAID, schAType, StringUtils.getIsStringEqulesNull(schWebURL),
                StringUtils.getIsStringEqulesNull(schImagePath), schFocusState,
                schFriendID, schcRecommendId};
        // String sql = "insert into ScheduleTable values( " + ID + "," + id
        // + ",'" + content + "','" + schDate + "','" + schTime + "',"
        // + schIsAlarm + "," + schBeforeTime + "," + schDisplayTime + ","
        // + schIsPostpone + "," + schIsImportant + "," + schColorType
        // + "," + schIsEnd + ",'" + schCreateTime + "','" + schTags
        // + "'," + schSourceType + ",'" + schSourceDesc + "','"
        // + schSourceDescSpare + "'," + schRepeatID + ",'"
        // + schRepeatDate + "','" + schUpdateTime + "'," + schUpdateState
        // + "," + schOpenState + "," + schRepeatLink + ",'" + schRingDesc
        // + "','" + schRingCode + "','" + schcRecommendName + "',"
        // + schRead + " , " + schAID + " , " + schAType + " , '"
        // + schWebURL + "', '" + schImagePath + "'," + schFocusState
        // + "," + schFriendID + "," + schcRecommendId + ")";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取某表,某字段的最大值
     *
     * @param type          1需要正值,-1需要负值
     * @param tableName     表名
     * @param maxColumnName 要查询最大值的字段名
     * @return
     */
    public int getLocalId(int type, String tableName, String maxColumnName) {
        int result = 0;
        if (type == 1) {
            result = 1;
            SQLiteDatabase sqldb = helper.getReadableDatabase();
            try {
                String sql = "select max(" + maxColumnName + ") from "
                        + tableName;
                Cursor cursor = sqldb.rawQuery(sql, null);
                if (null != cursor && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    result = cursor.getInt(0) + 1;
                    if (result < 0) {
                        result = 1;
                    }
                    cursor.close();
                } else {
                    result = 1;
                }
            } catch (Exception e) {
                result = 1;
            }
        } else if (type == -1) {
            result = -1;
            SQLiteDatabase sqldb = helper.getReadableDatabase();
            try {
                String sql = "select min(" + maxColumnName + ") from "
                        + tableName;
                Cursor cursor = sqldb.rawQuery(sql, null);
                if (null != cursor && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    result = cursor.getInt(0);
                    if (result >= 0) {
                        result = -1;
                    } else {
                        result = result - 1;
                    }
                    cursor.close();
                } else {
                    result = -1;
                }
            } catch (Exception e) {
                result = -1;
            }
        }
        return result;
    }

    /**
     * 本地删除，就是修改updateState的状态值
     *
     * @param schId
     */
    public void deleteScheduleLocalData(String schId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int id = Integer.parseInt(schId);
        String sql;
        if (id < 0) {
            sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                    + ScheduleTable.schID + " = " + schId;
        } else {
            sql = "update " + ScheduleTable.ScheduleTable + " set "
                    + ScheduleTable.schUpdateState + " = " + 3 + " where "
                    + ScheduleTable.schID + " = " + schId + " and "
                    + ScheduleTable.schUpdateState + " != " + 3;
        }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 和网上同步删除
     *
     * @param schId
     */
    public void deleteScheduleData(Integer schId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schID + " = " + schId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改普通日程
     *
     * @return
     */
    public void updateScheduleData(int schId, String schContent,
                                   String schDate, String schTime, int schIsAlarm, int schBeforeTime,
                                   int schDisplayTime, int schIsPostpone, int schIsImportant,
                                   int schColorType, int schIsEnd, String schTags, int schSourceType,
                                   String schSourceDesc, String schSourceDescSpare, int schRepeatID,
                                   String schRepeatDate, String schUpdateTime, int schUpdateState,
                                   int schOpenState, int schRepeatLink, String schRingDesc,
                                   String schRingCode, String schcRecommendName, int schRead,
                                   int schAID, int schAType, String schWebURL, String schImagePath,
                                   int schFocusState, int schFriendID, int schcRecommendId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String content = schContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String sql = "update ScheduleTable set " + ScheduleTable.schContent
                + " = ? " + "," + ScheduleTable.schDate + " = ? " + ", "
                + ScheduleTable.schTime + " = ?" + ", "
                + ScheduleTable.schIsAlarm + " = ?" + ", "
                + ScheduleTable.schBeforeTime + " = ?" + ", "
                + ScheduleTable.schDisplayTime + " = ?" + ", "
                + ScheduleTable.schIsPostpone + " = ?" + ", "
                + ScheduleTable.schIsImportant + " = ?" + ", "
                + ScheduleTable.schColorType + " = ?" + ", "
                + ScheduleTable.schIsEnd + " = ?" + ", "
                + ScheduleTable.schTags + " = ?" + ", "
                + ScheduleTable.schSourceType + " = ?" + ", "
                + ScheduleTable.schSourceDesc + " = ?" + ", "
                + ScheduleTable.schSourceDescSpare + " = ?" + ", "
                + ScheduleTable.schRepeatID + " = ?" + ", "
                + ScheduleTable.schRepeatDate + " = ?" + ", "
                + ScheduleTable.schUpdateTime + " = ?" + ", "
                + ScheduleTable.schUpdateState + " = ?" + ", "
                + ScheduleTable.schOpenState + " = ?" + ", "
                + ScheduleTable.schRepeatLink + " = ?" + ", "
                + ScheduleTable.schRingDesc + " = ?" + ", "
                + ScheduleTable.schRingCode + " = ?" + ", "
                + ScheduleTable.schcRecommendName + " = ?" + ", "
                + ScheduleTable.schRead + " = ?" + ", " + ScheduleTable.schAID
                + " = ?" + ", " + ScheduleTable.schAType + " = ?" + ", "
                + ScheduleTable.schWebURL + " = ?" + ", "
                + ScheduleTable.schImagePath + " = ?" + ", "
                + ScheduleTable.schFocusState + " = ?" + ", "
                + ScheduleTable.schFriendID + " = ?" + ", "
                + ScheduleTable.schcRecommendId + " = ? " + " where "
                + ScheduleTable.schID + " = " + schId;
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(schDate),
                StringUtils.getIsStringEqulesNull(schTime), schIsAlarm,
                schBeforeTime, schDisplayTime, schIsPostpone, schIsImportant,
                schColorType, schIsEnd,
                StringUtils.getIsStringEqulesNull(schTags), schSourceType,
                StringUtils.getIsStringEqulesNull(schSourceDesc),
                StringUtils.getIsStringEqulesNull(schSourceDescSpare),
                schRepeatID, StringUtils.getIsStringEqulesNull(schRepeatDate),
                StringUtils.getIsStringEqulesNull(schUpdateTime),
                schUpdateState, schOpenState, schRepeatLink,
                StringUtils.getIsStringEqulesNull(schRingDesc),
                StringUtils.getIsStringEqulesNull(schRingCode),
                StringUtils.getIsStringEqulesNull(schcRecommendName), schRead,
                schAID, schAType, StringUtils.getIsStringEqulesNull(schWebURL),
                StringUtils.getIsStringEqulesNull(schImagePath), schFocusState,
                schFriendID, schcRecommendId};
        // String sql = "update " + ScheduleTable.ScheduleTable + " set "
        // + ScheduleTable.schContent + " = '" + content + "', "
        // + ScheduleTable.schDate + " = '" + schDate + "', "
        // + ScheduleTable.schTime + " = '" + schTime + "', "
        // + ScheduleTable.schIsAlarm + " = " + schIsAlarm + ", "
        // + ScheduleTable.schBeforeTime + " = " + schBeforeTime + ", "
        // + ScheduleTable.schDisplayTime + " = " + schDisplayTime + ", "
        // + ScheduleTable.schIsPostpone + " = " + schIsPostpone + ", "
        // + ScheduleTable.schIsImportant + " = " + schIsImportant + ", "
        // + ScheduleTable.schColorType + " = " + schColorType + ", "
        // + ScheduleTable.schIsEnd + " = " + schIsEnd + ", "
        // + ScheduleTable.schTags + " = '" + schTags + "', "
        // + ScheduleTable.schSourceType + " = '" + schSourceType + "', "
        // + ScheduleTable.schSourceDesc + " = '" + schSourceDesc + "', "
        // + ScheduleTable.schSourceDescSpare + " = '"
        // + schSourceDescSpare + "', " + ScheduleTable.schRepeatID
        // + " = " + schRepeatID + ", " + ScheduleTable.schRepeatDate
        // + " = '" + schRepeatDate + "', " + ScheduleTable.schUpdateTime
        // + " = '" + schUpdateTime + "', " + ScheduleTable.schUpdateState
        // + " = " + schUpdateState + ", " + ScheduleTable.schOpenState
        // + " = " + schOpenState + ", " + ScheduleTable.schRepeatLink
        // + " = " + schRepeatLink + ", " + ScheduleTable.schRingDesc
        // + " = '" + schRingDesc + "', " + ScheduleTable.schRingCode
        // + " = '" + schRingCode + "', "
        // + ScheduleTable.schcRecommendName + " = '" + schcRecommendName
        // + "', " + ScheduleTable.schRead + " = " + schRead + ", "
        // + ScheduleTable.schAID + " = " + schAID + ", "
        // + ScheduleTable.schAType + " = " + schAType + ", "
        // + ScheduleTable.schWebURL + " = '" + schWebURL + "', "
        // + ScheduleTable.schImagePath + " = '" + schImagePath + "', "
        // + ScheduleTable.schFocusState + " = " + schFocusState + ", "
        // + ScheduleTable.schFriendID + " = " + schFriendID + ", "
        // + ScheduleTable.schcRecommendId + " = " + schcRecommendId
        // + " where " + ScheduleTable.schID + " = " + schId;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 和网上同步成功后修改状态值为0
     */
    public void updateUpdateState(Integer schId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schUpdateState + " = " + 0 + " where "
                + ScheduleTable.schID + " = " + schId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改日程推迟时间
     */
    public void updateScheduleDateData(int schId, String schDate, String schTime) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schDate + " = '" + schDate + "', "
                + ScheduleTable.schTime + " = '" + schTime + "', "
                + ScheduleTable.schUpdateState + " = " + 2 + " where "
                + ScheduleTable.schID + " = " + schId + " and "
                + ScheduleTable.schUpdateState + " != " + 3;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改openstate状态
     */
    /**
     * 更改日程推迟时间
     */
    public void updateScheduleOpenStateData(String id, int state) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schOpenState + " = " + state + ", "
                + ScheduleTable.schUpdateState + " = " + 2 + " where "
                + ScheduleTable.schID + " = " + id + " and "
                + ScheduleTable.schUpdateState + " != " + 3;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改为待办 今天+全天+顺延
     */
    public void updateScheduleUnTaskData(String schId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schDate + " = '"
                + DateUtil.formatDate(new Date()) + "', "
                + ScheduleTable.schDisplayTime + " = " + 0 + ", "
                + ScheduleTable.schIsPostpone + " = " + 1 + ", "
                + ScheduleTable.schUpdateState + " = " + 2 + " where "
                + ScheduleTable.schID + " = " + schId + " and "
                + ScheduleTable.schUpdateState + " != " + 3;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改是否结束
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public void updateScheduleData(Map<String, String> upMap, String sqlWhere) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String updateMap = "";

        // 取出需修改的字段拼接
        Set set = upMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();

            if (isNum(mapentry.getValue().toString())) {
                updateMap = updateMap + mapentry.getKey() + "="
                        + mapentry.getValue() + ",";
            } else {
                updateMap = updateMap + mapentry.getKey() + "='"
                        + mapentry.getValue() + "',";
            }

        }
        String updateStr = updateMap.substring(0, updateMap.lastIndexOf(","));
        String[] str = updateStr.split(",");
        String updateState = str[0].toString();
        String schIsEnd = str[1].toString();
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + updateState + " , " + schIsEnd + " " + sqlWhere;

        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public void updateScheduleData1(Map<String, String> upMap, String sqlWhere) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String updateMap = "";

        // 取出需修改的字段拼接
        Set set = upMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();

            if (isNum(mapentry.getValue().toString())) {
                updateMap = updateMap + mapentry.getKey() + "="
                        + mapentry.getValue() + ",";
            } else {
                updateMap = updateMap + mapentry.getKey() + "='"
                        + mapentry.getValue() + "',";
            }

        }
        String updateStr = updateMap.substring(0, updateMap.lastIndexOf(","));
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + updateStr + " " + sqlWhere;

        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改schread状态
     */
    public void updateSchReadData(int schId, int schread) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schRead + " = " + schread + " where "
                + ScheduleTable.schID + " = " + schId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改schRepeatLink 的值
     */
    public void updateSchRepeatLinkData(int schId, int link) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schRepeatLink + " = " + link + " where "
                + ScheduleTable.schID + " = " + schId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 验证一个字符串是不是数字
     *
     * @param str
     * @return
     */
    public static boolean isNum(String str) {
        if ("".equals(str))
            return false;
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 修改日程ID
     */
    public void UpdateSchID(Integer SchID, Integer newid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update ScheduleTable set schID = " + newid
                + " where schID = " + SchID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询数据库是否有相同id的数据
     */
    public int CheckCountSchData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + ScheduleTable.ScheduleTable
                + " where " + ScheduleTable.schID + " = " + id;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    /**
     * 根据重复id查询重复类型
     *
     * @return
     */
    public Map<String, String> queryRepateType(String repid) {

        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> sMap = new HashMap<String, String>();
        String sql = "select * from CLRepeatTable where repID ='" + repid + "'";

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    sMap.put("type", cursor.getString(cursor
                            .getColumnIndex(CLRepeatTable.repType)));
                    cursor.close();
                    return sMap;
                }
            } else {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sMap;

    }

    /**
     * 修改闹钟日程是否结束
     */
    @SuppressWarnings("unchecked")
    public void updateSchFocusState(Map<String, String> upMap, String sqlWhere) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String updateMap = "";

        // 取出需修改的字段拼接
        Set set = upMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();

            if (isNum(mapentry.getValue().toString())) {
                updateMap = updateMap + mapentry.getKey() + "="
                        + mapentry.getValue() + ",";
            } else {
                updateMap = updateMap + mapentry.getKey() + "='"
                        + mapentry.getValue() + "',";
            }

        }
        String updateStr = updateMap.substring(0, updateMap.lastIndexOf(","));
        String[] str = updateStr.split(",");
        String isEnd = str[0].toString();
        String sql = "update " + ScheduleTable.ScheduleTable + " set " + isEnd
                + " " + sqlWhere;

        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询搜索到的日程数据 1 两天以前 2 昨天 3今天 4明天 5一周以内 6一周以后
     */
    public List<Map<String, String>> QuerySchSerachData(int index,
                                                        String content) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "";
        String yestoday;// 昨天
        String today;// 今天
        String tomorrow;// 明天
        String inweek;// 一周以内
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) - 1);
        yestoday = DateUtil.formatDate(calendar.getTime());
        calendar.setTime(new Date());
        today = DateUtil.formatDate(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow = DateUtil.formatDate(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 7);
        inweek = DateUtil.formatDate(calendar.getTime());
        switch (index) {
            case 1:// 两天以前
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schContent + " like '%" + content
                        + "%' and " + ScheduleTable.schUpdateState + " != " + 3
                        + " and " + ScheduleTable.schDate + " < '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 2:// 昨天
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schContent + " like '%" + content
                        + "%' and " + ScheduleTable.schUpdateState + " != " + 3
                        + " and " + ScheduleTable.schDate + " = '" + yestoday
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 3:// 今天
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schContent + " like '%" + content
                        + "%' and " + ScheduleTable.schUpdateState + " != " + 3
                        + " and " + ScheduleTable.schDate + " = '" + today
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 4:// 明天
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schContent + " like '%" + content
                        + "%' and " + ScheduleTable.schUpdateState + " != " + 3
                        + " and " + ScheduleTable.schDate + " = '" + tomorrow
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 5:// 一周以内
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schContent + " like '%" + content
                        + "%' and " + ScheduleTable.schUpdateState + " != " + 3
                        + " and " + ScheduleTable.schDate + " > '" + tomorrow
                        + "' and " + ScheduleTable.schDate + " < '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            case 6:// 一周以后
                sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                        + ScheduleTable.schContent + " like '%" + content
                        + "%' and " + ScheduleTable.schUpdateState + " != " + 3
                        + " and " + ScheduleTable.schDate + " >= '" + inweek
                        + "' order by schDate desc,schDisplayTime asc,schTime asc";
                break;
            default:
                break;
        }
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(ScheduleTable.schID, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schID)));
                noticeMap.put(ScheduleTable.schContent, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schContent)));
                noticeMap.put(ScheduleTable.schDate, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schDate)));
                noticeMap.put(ScheduleTable.schTime, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schTime)));
                noticeMap.put(ScheduleTable.schIsAlarm, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schIsAlarm)));
                noticeMap.put(ScheduleTable.schBeforeTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schBeforeTime)));
                noticeMap.put(ScheduleTable.schDisplayTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schDisplayTime)));
                noticeMap.put(ScheduleTable.schIsPostpone, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schIsPostpone)));
                noticeMap.put(ScheduleTable.schIsImportant, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schIsImportant)));
                noticeMap.put(ScheduleTable.schColorType, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schColorType)));
                noticeMap.put(ScheduleTable.schIsEnd, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schIsEnd)));
                noticeMap.put(ScheduleTable.schCreateTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schCreateTime)));
                noticeMap.put(ScheduleTable.schTags, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schTags)));
                noticeMap.put(ScheduleTable.schSourceType, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schSourceType)));
                noticeMap.put(ScheduleTable.schSourceDesc, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schSourceDesc)));
                noticeMap
                        .put(ScheduleTable.schSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schSourceDescSpare)));
                noticeMap.put(ScheduleTable.schRepeatID, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatID)));
                noticeMap.put(ScheduleTable.schRepeatDate, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatDate)));
                noticeMap.put(ScheduleTable.schUpdateTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schUpdateTime)));
                noticeMap.put(ScheduleTable.schUpdateState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schUpdateState)));
                noticeMap.put(ScheduleTable.schOpenState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schOpenState)));
                noticeMap.put(ScheduleTable.schRepeatLink, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatLink)));
                noticeMap.put(ScheduleTable.schRingDesc, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRingDesc)));
                noticeMap.put(ScheduleTable.schRingCode, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRingCode)));
                noticeMap
                        .put(ScheduleTable.schcRecommendName,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schcRecommendName)));
                noticeMap.put(ScheduleTable.schRead, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schRead)));
                noticeMap.put(ScheduleTable.schAID, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schAID)));
                noticeMap.put(ScheduleTable.schAType, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schAType)));
                noticeMap.put(ScheduleTable.schWebURL, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schWebURL)));
                noticeMap.put(ScheduleTable.schImagePath, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schImagePath)));
                noticeMap.put(ScheduleTable.schFocusState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schFocusState)));
                noticeMap.put(ScheduleTable.schFriendID, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schFriendID)));
                noticeMap
                        .put(ScheduleTable.schcRecommendId,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schcRecommendId)));
                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /*********************************
     * 重复数据库操作
     ************************************************/
    public boolean insertCLRepeatTableData(int repBeforeTime, int repColorType,
                                           int repDisplayTime, int repType, int repIsAlarm, int repIsPuase,
                                           int repIsImportant, int repSourceType, int repUpdateState,
                                           String repTypeParameter, String repNextCreatedTime,
                                           String repLastCreatedTime, String repInitialCreatedTime,
                                           String repStartDate, String repContent, String repCreateTime,
                                           String repSourceDesc, String repSourceDescSpare, String repTime,
                                           String repRingDesc, String repRingCode, String repUpdateTime,
                                           int repOpenState, String repCommendedUserName,
                                           int repCommendedUserId, String repDateOne, String repDateTwo,
                                           int repStateOne, int repStateTwo, int repAType, String repWebURL,
                                           String repImagePath, int repInSTable, int repEndState,
                                           String parReamrk, int repRead, int repPostState) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String content = repContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        int repID = getLocalId(-1, "CLRepeatTable", CLRepeatTable.repID);
        repschId = repID;
        String sql = "insert into CLRepeatTable(repID,repBeforeTime, repColorType,repDisplayTime,"
                + "repType,repIsAlarm,repIsPuase,repIsImportant,repSourceType,repUpdateState,"
                + "repTypeParameter, repNextCreatedTime,repLastCreatedTime,repInitialCreatedTime,"
                + "repStartDate,repContent, repCreateTime,repSourceDesc, repSourceDescSpare, "
                + "repTime,repRingDesc, repRingCode,repUpdateTime,repOpenState, "
                + "repCommendedUserName,repCommendedUserId, repDateOne, repDateTwo,"
                + "repStateOne,repStateTwo,repAType, repWebURL,repImagePath,repInSTable,"
                + "repEndState,parReamrk,repRead,repPostState) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{repID, repBeforeTime, repColorType,
                repDisplayTime, repType, repIsAlarm, repIsPuase,
                repIsImportant, repSourceType, repUpdateState,
                StringUtils.getIsStringEqulesNull(repTypeParameter),
                StringUtils.getIsStringEqulesNull(repNextCreatedTime),
                StringUtils.getIsStringEqulesNull(repLastCreatedTime),
                StringUtils.getIsStringEqulesNull(repInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(repStartDate),
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(repCreateTime),
                StringUtils.getIsStringEqulesNull(repSourceDesc),
                StringUtils.getIsStringEqulesNull(repSourceDescSpare),
                StringUtils.getIsStringEqulesNull(repTime),
                StringUtils.getIsStringEqulesNull(repRingDesc),
                StringUtils.getIsStringEqulesNull(repRingCode),
                StringUtils.getIsStringEqulesNull(repUpdateTime), repOpenState,
                StringUtils.getIsStringEqulesNull(repCommendedUserName),
                repCommendedUserId,
                StringUtils.getIsStringEqulesNull(repDateOne),
                StringUtils.getIsStringEqulesNull(repDateTwo), repStateOne,
                repStateTwo, repAType,
                StringUtils.getIsStringEqulesNull(repWebURL),
                StringUtils.getIsStringEqulesNull(repImagePath), repInSTable,
                repEndState, StringUtils.getIsStringEqulesNull(parReamrk),
                repRead, repPostState};
        // String sql = "insert into CLRepeatTable values(" + repID + ","
        // + repBeforeTime + "," + repColorType + "," + repDisplayTime
        // + "," + repType + "," + repIsAlarm + "," + repIsPuase + ","
        // + repIsImportant + "," + repSourceType + "," + repUpdateState
        // + ",'" + repTypeParameter + "','" + repNextCreatedTime + "','"
        // + repLastCreatedTime + "','" + repInitialCreatedTime + "','"
        // + repStartDate + "','" + content + "','" + repCreateTime
        // + "','" + repSourceDesc + "','" + repSourceDescSpare + "','"
        // + repTime + "','" + repRingDesc + "','" + repRingCode + "','"
        // + repUpdateTime + "'," + repOpenState + ",'"
        // + repCommendedUserName + "'," + repCommendedUserId + ",'"
        // + repDateOne + "','" + repDateTwo + "'," + repStateOne + ","
        // + repStateTwo + "," + repAType + ",'" + repWebURL + "','"
        // + repImagePath + "'," + repInSTable + "," + repEndState
        // + " , '" + parReamrk + "'," + repRead + " , " + repPostState
        // + ")";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询重复母记事
     */
    public List<Map<String, String>> QueryAllChongFuData(int type) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "";
        switch (type) {
            case 0:
                sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                        + CLRepeatTable.repRead + " = " + 1 + " and "
                        + CLRepeatTable.repUpdateState + " != " + 3
                        + " order by repTime asc";
                break;
            case 1:
                sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                        + CLRepeatTable.repUpdateState + " != " + 3
                        + " order by repTime asc";
                break;
            case 2://updatestate不为0的都上传
                sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                        + CLRepeatTable.repUpdateState + " != " + 0
                        + " order by repInitialCreatedTime desc,repTime asc";
                break;
            case 3:// 刚进入程序查询母记事，未删除，未暂停 update!=3,nextcreatetime<当前时间 查询重复母记事
                sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                        + CLRepeatTable.repUpdateState + " != " + 3 + " and "
                        + CLRepeatTable.repIsPuase + " != " + 1
                        + " order by repTime asc";
                break;
            case 4:
                sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                        + CLRepeatTable.repUpdateState + " != " + 3 + " and "
                        + CLRepeatTable.repIsPuase + " = " + 0
                        + " order by repTime asc";
                break;
            case 5://查询所有母记事
                sql = "select * from " + CLRepeatTable.CLRepeatTable;
                break;
            case 6://上传重复中删除的数据
                sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                        + CLRepeatTable.repUpdateState + " = " + 3
                        + " order by repInitialCreatedTime desc,repTime asc";
                break;
            default:
                break;
        }
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(CLRepeatTable.repID, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repID)));
                noticeMap.put(CLRepeatTable.repBeforeTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repBeforeTime)));
                noticeMap.put(CLRepeatTable.repColorType, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repColorType)));
                noticeMap.put(CLRepeatTable.repDisplayTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repDisplayTime)));
                noticeMap.put(CLRepeatTable.repType, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repType)));
                noticeMap.put(CLRepeatTable.repIsAlarm, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repIsAlarm)));
                noticeMap.put(CLRepeatTable.repIsPuase, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repIsPuase)));
                noticeMap.put(CLRepeatTable.repIsImportant, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repIsImportant)));
                noticeMap.put(CLRepeatTable.repSourceType, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repSourceType)));
                noticeMap.put(CLRepeatTable.repUpdateState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repUpdateState)));
                noticeMap
                        .put(CLRepeatTable.repTypeParameter,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repTypeParameter)));
                noticeMap
                        .put(CLRepeatTable.repNextCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repNextCreatedTime)));
                noticeMap
                        .put(CLRepeatTable.repLastCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repLastCreatedTime)));
                noticeMap
                        .put(CLRepeatTable.repInitialCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repInitialCreatedTime)));
                noticeMap.put(CLRepeatTable.repStartDate, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStartDate)));
                noticeMap.put(CLRepeatTable.repContent, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repContent)));
                noticeMap.put(CLRepeatTable.repCreateTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repCreateTime)));
                noticeMap.put(CLRepeatTable.repSourceDesc, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repSourceDesc)));
                noticeMap
                        .put(CLRepeatTable.repSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repSourceDescSpare)));
                noticeMap.put(CLRepeatTable.repTime, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repTime)));
                noticeMap.put(CLRepeatTable.repRingDesc, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repRingDesc)));
                noticeMap.put(CLRepeatTable.repRingCode, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repRingCode)));
                noticeMap.put(CLRepeatTable.repUpdateTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repUpdateTime)));
                noticeMap.put(CLRepeatTable.repOpenState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repOpenState)));
                noticeMap
                        .put(CLRepeatTable.repcommendedUserName,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repcommendedUserName)));
                noticeMap
                        .put(CLRepeatTable.repcommendedUserId,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repcommendedUserId)));
                noticeMap.put(CLRepeatTable.repDateOne, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repDateOne)));
                noticeMap.put(CLRepeatTable.repDateTwo, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repDateTwo)));
                noticeMap.put(CLRepeatTable.repStateOne, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStateOne)));
                noticeMap.put(CLRepeatTable.repStateTwo, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStateTwo)));
                noticeMap.put(CLRepeatTable.repAType, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repAType)));
                noticeMap.put(CLRepeatTable.repWebURL, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repWebURL)));
                noticeMap.put(CLRepeatTable.repImagePath, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repImagePath)));
                noticeMap.put(CLRepeatTable.repInSTable, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repInSTable)));
                noticeMap.put(CLRepeatTable.repEndState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repEndState)));
                noticeMap.put(CLRepeatTable.parReamrk, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.parReamrk)));
                noticeMap.put(CLRepeatTable.repRead, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repRead)));
                noticeMap.put(CLRepeatTable.repPostState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repPostState)));
                dataList.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 本地删除，就是修改updateState的状态值
     *
     * @param repId
     */
    public void deleteCLRepeatTableLocalData(String repId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int id = Integer.parseInt(repId);
        String sql;
        if (id < 0) {
            sql = "delete from " + CLRepeatTable.CLRepeatTable + " where "
                    + CLRepeatTable.repID + " = " + repId;
        } else {
            sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                    + CLRepeatTable.repUpdateState + " = " + 3 + " , "
                    + CLRepeatTable.repRead + " = " + 0 + " where "
                    + CLRepeatTable.repID + " = " + repId + " and "
                    + CLRepeatTable.repUpdateState + " != " + 3;
        }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改是暂停
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public void updateCLRepeatTableData(Map<String, String> upMap,
                                        String sqlWhere) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String updateMap = "";

        // 取出需修改的字段拼接
        Set set = upMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();

            if (isNum(mapentry.getValue().toString())) {
                updateMap = updateMap + mapentry.getKey() + "="
                        + mapentry.getValue() + ",";
            } else {
                updateMap = updateMap + mapentry.getKey() + "='"
                        + mapentry.getValue() + "',";
            }

        }
        String updateStr = updateMap.substring(0, updateMap.lastIndexOf(","));
        String[] str = updateStr.split(",");
        String updateState = str[0].toString();
        String repIsPuase = str[1].toString();
        String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                + updateState + " , " + repIsPuase + " " + sqlWhere;

        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改重复事件
     *
     * @return
     */
    public void updateCLRepeatTableData(int repId, int repBeforeTime,
                                        int repColorType, int repDisplayTime, int repType, int repIsAlarm,
                                        int repIsPuase, int repIsImportant, int repSourceType,
                                        int repUpdateState, String repTypeParameter,
                                        String repNextCreatedTime, String repLastCreatedTime,
                                        String repInitialCreatedTime, String repStartDate,
                                        String repContent, String repCreateTime, String repSourceDesc,
                                        String repSourceDescSpare, String repTime, String repRingDesc,
                                        String repRingCode, String repUpdateTime, int repOpenState,
                                        String repCommendedUserName, int repCommendedUserId,
                                        String repDateOne, String repDateTwo, int repStateOne,
                                        int repStateTwo, int repAType, String repWebURL,
                                        String repImagePath, int repInSTable, int repEndState,
                                        String parReamrk, int repRead, int repPostState) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String content = repContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                + CLRepeatTable.repBeforeTime + " = ?" + ", "
                + CLRepeatTable.repColorType + " = ?" + ", "
                + CLRepeatTable.repDisplayTime + " = ?" + ", "
                + CLRepeatTable.repType + " = ?" + ", "
                + CLRepeatTable.repIsAlarm + " = ?" + ", "
                + CLRepeatTable.repIsPuase + " = ?" + ", "
                + CLRepeatTable.repIsImportant + " = ?" + ", "
                + CLRepeatTable.repSourceType + " = ?" + ", "
                + CLRepeatTable.repUpdateState + " = ?" + ", "
                + CLRepeatTable.repTypeParameter + " = ?" + ", "
                + CLRepeatTable.repNextCreatedTime + " = ?" + ", "
                + CLRepeatTable.repLastCreatedTime + " = ?" + ", "
                + CLRepeatTable.repInitialCreatedTime + " = ?" + ", "
                + CLRepeatTable.repStartDate + " = ?" + ", "
                + CLRepeatTable.repContent + " = ?" + ", "
                + CLRepeatTable.repCreateTime + " = ?" + ", "
                + CLRepeatTable.repSourceDesc + " = ?" + ", "
                + CLRepeatTable.repSourceDescSpare + " = ?" + ", "
                + CLRepeatTable.repTime + " = ?" + ", "
                + CLRepeatTable.repRingDesc + " = ?" + ", "
                + CLRepeatTable.repRingCode + " = ?" + ", "
                + CLRepeatTable.repUpdateTime + " = ?" + ", "
                + CLRepeatTable.repOpenState + " = ?" + ", "
                + CLRepeatTable.repcommendedUserName + " = ?" + ", "
                + CLRepeatTable.repcommendedUserId + " = ?" + ", "
                + CLRepeatTable.repDateOne + " = ?" + ", "
                + CLRepeatTable.repDateTwo + " = ?" + ", "
                + CLRepeatTable.repStateOne + " = ?" + ", "
                + CLRepeatTable.repStateTwo + " = ?" + ", "
                + CLRepeatTable.repAType + " = ?" + ", "
                + CLRepeatTable.repWebURL + " = ?" + ", "
                + CLRepeatTable.repImagePath + " = ?" + ", "
                + CLRepeatTable.repInSTable + " = ?" + ", "
                + CLRepeatTable.repEndState + " = ?" + ", "
                + CLRepeatTable.parReamrk + " = ?" + ", "
                + CLRepeatTable.repRead + " = ?" + " , "
                + CLRepeatTable.repPostState + " = ?" + " where "
                + CLRepeatTable.repID + " = " + repId;
        Object[] mValue = new Object[]{repBeforeTime, repColorType,
                repDisplayTime, repType, repIsAlarm, repIsPuase,
                repIsImportant, repSourceType, repUpdateState,
                StringUtils.getIsStringEqulesNull(repTypeParameter),
                StringUtils.getIsStringEqulesNull(repNextCreatedTime),
                StringUtils.getIsStringEqulesNull(repLastCreatedTime),
                StringUtils.getIsStringEqulesNull(repInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(repStartDate),
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(repCreateTime),
                StringUtils.getIsStringEqulesNull(repSourceDesc),
                StringUtils.getIsStringEqulesNull(repSourceDescSpare),
                StringUtils.getIsStringEqulesNull(repTime),
                StringUtils.getIsStringEqulesNull(repRingDesc),
                StringUtils.getIsStringEqulesNull(repRingCode),
                StringUtils.getIsStringEqulesNull(repUpdateTime), repOpenState,
                StringUtils.getIsStringEqulesNull(repCommendedUserName),
                repCommendedUserId,
                StringUtils.getIsStringEqulesNull(repDateOne),
                StringUtils.getIsStringEqulesNull(repDateTwo), repStateOne,
                repStateTwo, repAType,
                StringUtils.getIsStringEqulesNull(repWebURL),
                StringUtils.getIsStringEqulesNull(repImagePath), repInSTable,
                repEndState, StringUtils.getIsStringEqulesNull(parReamrk),
                repRead, repPostState};
        // String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
        // + CLRepeatTable.repBeforeTime + " = " + repBeforeTime + ", "
        // + CLRepeatTable.repColorType + " = " + repColorType + ", "
        // + CLRepeatTable.repDisplayTime + " = " + repDisplayTime + ", "
        // + CLRepeatTable.repType + " = " + repType + ", "
        // + CLRepeatTable.repIsAlarm + " = " + repIsAlarm + ", "
        // + CLRepeatTable.repIsPuase + " = " + repIsPuase + ", "
        // + CLRepeatTable.repIsImportant + " = " + repIsImportant + ", "
        // + CLRepeatTable.repSourceType + " = " + repSourceType + ", "
        // + CLRepeatTable.repUpdateState + " = " + repUpdateState + ", "
        // + CLRepeatTable.repTypeParameter + " = '" + repTypeParameter
        // + "', " + CLRepeatTable.repNextCreatedTime + " = '"
        // + repNextCreatedTime + "', " + CLRepeatTable.repLastCreatedTime
        // + " = '" + repLastCreatedTime + "', "
        // + CLRepeatTable.repInitialCreatedTime + " = '"
        // + repInitialCreatedTime + "', " + CLRepeatTable.repStartDate
        // + " = '" + repStartDate + "', " + CLRepeatTable.repContent
        // + " = '" + content + "', " + CLRepeatTable.repCreateTime
        // + " = '" + repCreateTime + "', " + CLRepeatTable.repSourceDesc
        // + " = '" + repSourceDesc + "', "
        // + CLRepeatTable.repSourceDescSpare + " = '"
        // + repSourceDescSpare + "', " + CLRepeatTable.repTime + " = '"
        // + repTime + "', " + CLRepeatTable.repRingDesc + " = '"
        // + repRingDesc + "', " + CLRepeatTable.repRingCode + " = '"
        // + repRingCode + "', " + CLRepeatTable.repUpdateTime + " = '"
        // + repUpdateTime + "', " + CLRepeatTable.repOpenState + " = "
        // + repOpenState + ", " + CLRepeatTable.repcommendedUserName
        // + " = '" + repCommendedUserName + "', "
        // + CLRepeatTable.repcommendedUserId + " = " + repCommendedUserId
        // + ", " + CLRepeatTable.repDateOne + " = '" + repDateOne + "', "
        // + CLRepeatTable.repDateTwo + " = '" + repDateTwo + "', "
        // + CLRepeatTable.repStateOne + " = " + repStateOne + ", "
        // + CLRepeatTable.repStateTwo + " = " + repStateTwo + ", "
        // + CLRepeatTable.repAType + " = " + repAType + ", "
        // + CLRepeatTable.repWebURL + " = '" + repWebURL + "', "
        // + CLRepeatTable.repImagePath + " = '" + repImagePath + "', "
        // + CLRepeatTable.repInSTable + " = " + repInSTable + ", "
        // + CLRepeatTable.repEndState + " = " + repEndState + ", "
        // + CLRepeatTable.parReamrk + " = '" + parReamrk + "', "
        // + CLRepeatTable.repRead + " = " + repRead + " , "
        // + CLRepeatTable.repPostState + " = " + repPostState + " where "
        // + CLRepeatTable.repID + " = " + repId;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改重复子记事状态
     */
    public void updateChildSchState(int repId, int important) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schIsImportant + " = " + important + " where "
                + ScheduleTable.schRepeatID + " = " + repId + " and "
                + ScheduleTable.schRepeatLink + " != " + 0 + " and "
                + ScheduleTable.schRepeatLink + " != " + 2;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 插入下载的农历
     */
    public boolean insertNongLiData(String calendar, String solarTerms,
                                    String week, String lunarCalendar, String holiday,
                                    String lunarHoliday, String createTime, String isNotHoliday) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int id = getLocalId(1, "LocateSolarToLunar", LocateSolarToLunar.ID);
        // String sql = "insert into LocateSolarToLunar values( " + id + ",'"
        // + calendar + "','" + solarTerms + "','" + week + "','"
        // + lunarCalendar + "','" + holiday + "','" + lunarHoliday
        // + "','" + createTime + "','" + isNotHoliday + "')";
        String sql = "insert into LocateSolarToLunar(ID,CALENDAR,SOLAR_TERMS,WEEK,LUNAR_CALENDAR,HOLIDAY,LUNAR_HOLIDAY,CREATE_TIME,ISNOTHOLIDAY) "
                + "values(?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{id,
                StringUtils.getIsStringEqulesNull(calendar),
                StringUtils.getIsStringEqulesNull(solarTerms),
                StringUtils.getIsStringEqulesNull(week),
                StringUtils.getIsStringEqulesNull(lunarCalendar),
                StringUtils.getIsStringEqulesNull(holiday),
                StringUtils.getIsStringEqulesNull(lunarHoliday),
                StringUtils.getIsStringEqulesNull(createTime),
                StringUtils.getIsStringEqulesNull(isNotHoliday)};
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, String>> queryMaxDate() {

        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from LocateSolarToLunar order by CALENDAR desc";
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put("calendar", cursor.getString(cursor
                        .getColumnIndex(LocateSolarToLunar.CALENDAR)));
                dataList.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;

    }

    /**
     * 清空农历表的所有数据
     */
    public void deletenongliData() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from LocateSolarToLunar";
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 农历查公历 type 1是农历查询。，0是阳历
     *
     * @return
     */
    public Map<String, String> queryLunartoSolarList(String monthDay, int type) {

        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String nowDate = DateTimeHelper.formatDateTimetoString(new Date(),
                DateTimeHelper.FMT_yyyyMMdd);
        Map<String, String> sMap = new HashMap<String, String>();
        String sql = "";
        if (type == 0) {
            sql = "select * from LocateSolarToLunar where CALENDAR ='"
                    + monthDay + "' order by CALENDAR asc";
        } else {
            sql = "select * from LocateSolarToLunar where LUNAR_CALENDAR ='"
                    + monthDay + "' and CALENDAR " + " >= '" + nowDate
                    + "' order by CALENDAR asc";
        }

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    sMap.put("calendar", cursor.getString(cursor
                            .getColumnIndex(LocateSolarToLunar.CALENDAR)));
                    sMap.put("lunarCalendar", cursor.getString(cursor
                            .getColumnIndex(LocateSolarToLunar.LUNAR_CALENDAR)));
                    cursor.close();
                    return sMap;
                }
            } else {
                CalendarChangeValue changeValue = new CalendarChangeValue();
                if (changeValue.changaSZ(monthDay).length() == 2) {
                    sMap.put("calendar", nowDate.substring(0, 7) + "-"
                            + monthDay);
                } else {
                    if (monthDay.length() == 10) {
                        sMap.put("calendar", nowDate.substring(0, 4) + "-"
                                + monthDay.substring(5));
                        sMap.put("lunarCalendar",
                                changeValue.changNL(monthDay.substring(5)));
                    } else {
                        sMap.put("calendar", nowDate.substring(0, 4) + "-"
                                + monthDay);
                        sMap.put("lunarCalendar", changeValue.changNL(monthDay));
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sMap;

    }

    /**
     * 根据农历查询最近的阳历
     *
     * @param monthDay
     * @return
     */
    public List<Map<String, String>> queryNearLunartoSolarList(String monthDay) {

        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String year = "";
        String nowDate = DateTimeHelper.formatDateTimetoString(new Date(),
                DateTimeHelper.FMT_yyyyMMdd);
        year = nowDate.substring(0, 4);
        String yearDate = Integer.parseInt(year) - 1 + "-"
                + nowDate.substring(5);
        String foreverDate = Integer.parseInt(year) + 1 + "-"
                + nowDate.substring(5);
        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;

        String sql = "select * from LocateSolarToLunar where LUNAR_CALENDAR ='"
                + monthDay + "' and CALENDAR " + " >= '" + yearDate
                + "' and CALENDAR " + " <= '" + foreverDate
                + "' order by CALENDAR asc";
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put("calendar", cursor.getString(cursor
                        .getColumnIndex(LocateSolarToLunar.CALENDAR)));
                noticeMap.put("lunarCalendar", cursor.getString(cursor
                        .getColumnIndex(LocateSolarToLunar.LUNAR_CALENDAR)));
                maps.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }

    /**
     * 修改重复ID
     */
    public void UpdateRepeatID(Integer repID, Integer newRepID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                + CLRepeatTable.repID + " = " + newRepID + " where "
                + CLRepeatTable.repID + " = " + repID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 和网上同步成功后修改状态值为0
     */
    public void updateRepUpdateState(Integer repId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                + CLRepeatTable.repUpdateState + " = " + 0 + " where "
                + CLRepeatTable.repID + " = " + repId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 和网上同步删除
     */
    public void deleteRepeatData(String repId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + CLRepeatTable.CLRepeatTable + " where "
                + CLRepeatTable.repID + " = " + repId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 网上下载重复数据插入
     */
    public boolean insertDownCLRepeatTableData(int repId, int repBeforeTime,
                                               int repColorType, int repDisplayTime, int repType, int repIsAlarm,
                                               int repIsPuase, int repIsImportant, int repSourceType,
                                               int repUpdateState, String repTypeParameter,
                                               String repNextCreatedTime, String repLastCreatedTime,
                                               String repInitialCreatedTime, String repStartDate,
                                               String repContent, String repCreateTime, String repSourceDesc,
                                               String repSourceDescSpare, String repTime, String repRingDesc,
                                               String repRingCode, String repUpdateTime, int repOpenState,
                                               String repCommendedUserName, int repCommendedUserId,
                                               String repDateOne, String repDateTwo, int repStateOne,
                                               int repStateTwo, int repAType, String repWebURL,
                                               String repImagePath, int repInSTable, int repEndState,
                                               String parReamrk, int repRead, int repPostState) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "insert into CLRepeatTable(repID,repBeforeTime, repColorType,repDisplayTime,"
                + "repType,repIsAlarm,repIsPuase,repIsImportant,repSourceType,repUpdateState,"
                + "repTypeParameter, repNextCreatedTime,repLastCreatedTime,repInitialCreatedTime,"
                + "repStartDate,repContent, repCreateTime,repSourceDesc, repSourceDescSpare, "
                + "repTime,repRingDesc, repRingCode,repUpdateTime,repOpenState, "
                + "repCommendedUserName,repCommendedUserId, repDateOne, repDateTwo,"
                + "repStateOne,repStateTwo,repAType, repWebURL,repImagePath,repInSTable,"
                + "repEndState,parReamrk,repRead,repPostState) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{repId, repBeforeTime, repColorType,
                repDisplayTime, repType, repIsAlarm, repIsPuase,
                repIsImportant, repSourceType, repUpdateState,
                StringUtils.getIsStringEqulesNull(repTypeParameter),
                StringUtils.getIsStringEqulesNull(repNextCreatedTime),
                StringUtils.getIsStringEqulesNull(repLastCreatedTime),
                StringUtils.getIsStringEqulesNull(repInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(repStartDate),
                StringUtils.getIsStringEqulesNull(repContent),
                StringUtils.getIsStringEqulesNull(repCreateTime),
                StringUtils.getIsStringEqulesNull(repSourceDesc),
                StringUtils.getIsStringEqulesNull(repSourceDescSpare),
                StringUtils.getIsStringEqulesNull(repTime),
                StringUtils.getIsStringEqulesNull(repRingDesc),
                StringUtils.getIsStringEqulesNull(repRingCode),
                StringUtils.getIsStringEqulesNull(repUpdateTime), repOpenState,
                StringUtils.getIsStringEqulesNull(repCommendedUserName),
                repCommendedUserId,
                StringUtils.getIsStringEqulesNull(repDateOne),
                StringUtils.getIsStringEqulesNull(repDateTwo), repStateOne,
                repStateTwo, repAType,
                StringUtils.getIsStringEqulesNull(repWebURL),
                StringUtils.getIsStringEqulesNull(repImagePath), repInSTable,
                repEndState, StringUtils.getIsStringEqulesNull(parReamrk),
                repRead, repPostState};
        // String sql = "insert into CLRepeatTable values(" + repId + ","
        // + repBeforeTime + "," + repColorType + "," + repDisplayTime
        // + "," + repType + "," + repIsAlarm + "," + repIsPuase + ","
        // + repIsImportant + "," + repSourceType + "," + repUpdateState
        // + ",'" + repTypeParameter + "','" + repNextCreatedTime + "','"
        // + repLastCreatedTime + "','" + repInitialCreatedTime + "','"
        // + repStartDate + "','" + repContent + "','" + repCreateTime
        // + "','" + repSourceDesc + "','" + repSourceDescSpare + "','"
        // + repTime + "','" + repRingDesc + "','" + repRingCode + "','"
        // + repUpdateTime + "'," + repOpenState + ",'"
        // + repCommendedUserName + "'," + repCommendedUserId + ",'"
        // + repDateOne + "','" + repDateTwo + "'," + repStateOne + ","
        // + repStateTwo + "," + repAType + ",'" + repWebURL + "','"
        // + repImagePath + "'," + repInSTable + "," + repEndState
        // + " , '" + parReamrk + "', " + repRead + " , " + repPostState
        // + ")";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除对应的子记事
     *
     * @param repid
     */
    public void deleteChildSch(String repid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schRepeatID + " = " + repid + " and "
                + ScheduleTable.schRepeatLink + " = " + 1 + " or "
                + ScheduleTable.schRepeatLink + " = " + 3;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改子记事重复id
     */
    public void UpdateSchrepID(Integer repID, Integer newid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schRepeatID + " = " + newid + " where "
                + ScheduleTable.schRepeatID + " = " + repID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 在子日程中修改重复母记事
     *
     * @return
     */
    public void updateSchCLRepeatData(int repId, String repDateOne,
                                      String repDateTwo, int repStateOne, int repStateTwo) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                + CLRepeatTable.repUpdateState + " = " + 2 + " , "
                + CLRepeatTable.repDateOne + " = '" + repDateOne + "', "
                + CLRepeatTable.repDateTwo + " = '" + repDateTwo + "', "
                + CLRepeatTable.repStateOne + " = " + repStateOne + ", "
                + CLRepeatTable.repStateTwo + " = " + repStateTwo + " "
                + " where " + CLRepeatTable.repID + " = " + repId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询上一条和下一条的状态
     */
    public Map<String, String> QueryStateData(int repid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> noticeMap = null;
        String sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                + CLRepeatTable.repUpdateState + " != " + 3 + " and "
                + CLRepeatTable.repIsPuase + " != " + 1 + " and "
                + CLRepeatTable.repID + " = " + repid + " order by repTime asc";
        // + " and "+ "repNextCreatedTime" + " < '"
        // + DateUtil.formatDateTime(new Date())
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(CLRepeatTable.repID, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repID)));
                noticeMap.put(CLRepeatTable.repBeforeTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repBeforeTime)));
                noticeMap.put(CLRepeatTable.repColorType, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repColorType)));
                noticeMap.put(CLRepeatTable.repDisplayTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repDisplayTime)));
                noticeMap.put(CLRepeatTable.repType, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repType)));
                noticeMap.put(CLRepeatTable.repIsAlarm, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repIsAlarm)));
                noticeMap.put(CLRepeatTable.repIsPuase, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repIsPuase)));
                noticeMap.put(CLRepeatTable.repIsImportant, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repIsImportant)));
                noticeMap.put(CLRepeatTable.repSourceType, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repSourceType)));
                noticeMap.put(CLRepeatTable.repUpdateState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repUpdateState)));
                noticeMap
                        .put(CLRepeatTable.repTypeParameter,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repTypeParameter)));
                noticeMap
                        .put(CLRepeatTable.repNextCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repNextCreatedTime)));
                noticeMap
                        .put(CLRepeatTable.repLastCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repLastCreatedTime)));
                noticeMap
                        .put(CLRepeatTable.repInitialCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repInitialCreatedTime)));
                noticeMap.put(CLRepeatTable.repStartDate, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStartDate)));
                noticeMap.put(CLRepeatTable.repContent, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repContent)));
                noticeMap.put(CLRepeatTable.repCreateTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repCreateTime)));
                noticeMap.put(CLRepeatTable.repSourceDesc, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repSourceDesc)));
                noticeMap
                        .put(CLRepeatTable.repSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repSourceDescSpare)));
                noticeMap.put(CLRepeatTable.repTime, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repTime)));
                noticeMap.put(CLRepeatTable.repRingDesc, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repRingDesc)));
                noticeMap.put(CLRepeatTable.repRingCode, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repRingCode)));
                noticeMap.put(CLRepeatTable.repUpdateTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repUpdateTime)));
                noticeMap.put(CLRepeatTable.repOpenState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repOpenState)));
                noticeMap
                        .put(CLRepeatTable.repcommendedUserName,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repcommendedUserName)));
                noticeMap
                        .put(CLRepeatTable.repcommendedUserId,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repcommendedUserId)));
                noticeMap.put(CLRepeatTable.repDateOne, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repDateOne)));
                noticeMap.put(CLRepeatTable.repDateTwo, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repDateTwo)));
                noticeMap.put(CLRepeatTable.repStateOne, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStateOne)));
                noticeMap.put(CLRepeatTable.repStateTwo, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStateTwo)));
                noticeMap.put(CLRepeatTable.repAType, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repAType)));
                noticeMap.put(CLRepeatTable.repWebURL, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repWebURL)));
                noticeMap.put(CLRepeatTable.repImagePath, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repImagePath)));
                noticeMap.put(CLRepeatTable.repInSTable, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repInSTable)));
                noticeMap.put(CLRepeatTable.repEndState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repEndState)));
                noticeMap.put(CLRepeatTable.parReamrk, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.parReamrk)));
                noticeMap.put(CLRepeatTable.repRead, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repRead)));
                noticeMap.put(CLRepeatTable.repPostState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repPostState)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noticeMap;
    }

    /**
     * 查询数据库是否有相同id的数据
     */
    public int CheckCountRepData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + CLRepeatTable.CLRepeatTable
                + " where " + CLRepeatTable.repID + " = " + id;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    // ==============================好友数据表操作==================================================//
    public boolean insertMFMessageData(Integer fmID, Integer fmSendID,
                                       Integer fmGetID, Integer fmIsAlarm, Integer fmOpenState,
                                       Integer fmPostpone, Integer fmColorType, Integer fmDisplayTime,
                                       Integer fmBeforeTime, Integer fmSourceType, Integer fmType,
                                       String fmParameter, String fmContent, String fmCreateTime,
                                       String fmDate, String fmTime, String fmSourceDesc,
                                       String fmSourceDescSpare, String fmTags, String fmRingDesc,
                                       String fmRingCode, String fmStartDate, String fmInitialCreatedTime,
                                       String fmLastCreatedTime, String fmNextCreatedTime,
                                       Integer fmStatus, Integer fmAType, String fmWebURL,
                                       String fmImagePath, int fmInSTable) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "insert into FMessages values(" + fmID + "," + fmSendID
                + "," + fmGetID + "," + fmIsAlarm + "," + fmOpenState + ","
                + fmPostpone + "," + fmColorType + "," + fmDisplayTime + ","
                + fmBeforeTime + "," + fmSourceType + "," + fmType + ",'"
                + fmParameter + "','" + fmContent + "','" + fmCreateTime
                + "','" + fmDate + "','" + fmTime + "','" + fmSourceDesc
                + "','" + fmSourceDescSpare + "','" + fmTags + "','"
                + fmRingDesc + "','" + fmRingCode + "','" + fmStartDate + "','"
                + fmInitialCreatedTime + "','" + fmLastCreatedTime + "','"
                + fmNextCreatedTime + "' , " + fmStatus + "," + fmAType + ",'"
                + fmWebURL + "','" + fmImagePath + "'," + fmInSTable + ")";
        try {
            sqldb.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询聊天记录
     *
     * @return
     */
    public List<Map<String, String>> QueryAllLiaoTianData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from " + FMessages.FMessages + " where "
                + FMessages.fmID + " > " + 0 + " and (" + FMessages.fmSendID
                + " = " + id + " or " + FMessages.fmGetID + " = " + id
                + ") order by fmCreateTime desc";// fmDate asc,fmTime asc,limit
        // + ((pageIndex - 1) * 40) + " , " + 40
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap
                        .put(FMessages.fmID, cursor.getString(cursor
                                .getColumnIndex(FMessages.fmID)));
                noticeMap.put(FMessages.fmSendID, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmSendID)));
                noticeMap.put(FMessages.fmGetID, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmGetID)));
                noticeMap.put(FMessages.fmIsAlarm, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmIsAlarm)));
                noticeMap.put(FMessages.fmOpenState, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmOpenState)));
                noticeMap.put(FMessages.fmPostpone, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmPostpone)));
                noticeMap.put(FMessages.fmColorType, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmColorType)));
                noticeMap.put(FMessages.fmDisplayTime, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmDisplayTime)));
                noticeMap.put(FMessages.fmBeforeTime, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmBeforeTime)));
                noticeMap.put(FMessages.fmSourceType, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmSourceType)));
                noticeMap.put(FMessages.fmType, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmType)));
                noticeMap.put(FMessages.fmStatus, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmStatus)));
                noticeMap.put(FMessages.fmParameter, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmParameter)));
                noticeMap.put(FMessages.fmContent, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmContent)));
                noticeMap.put(FMessages.fmCreateTime, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmCreateTime)));
                noticeMap.put(FMessages.fmDate, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmDate)));
                noticeMap.put(FMessages.fmTime, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmTime)));
                noticeMap.put(FMessages.fmSourceDesc, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmSourceDesc)));
                noticeMap.put(FMessages.fmSourceDescSpare, cursor
                        .getString(cursor
                                .getColumnIndex(FMessages.fmSourceDescSpare)));
                noticeMap.put(FMessages.fmTags, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmTags)));
                noticeMap.put(FMessages.fmRingDesc, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmRingDesc)));
                noticeMap.put(FMessages.fmRingCode, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmRingCode)));
                noticeMap.put(FMessages.fmStartDate, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmStartDate)));
                noticeMap
                        .put(FMessages.fmInitialCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(FMessages.fmInitialCreatedTime)));
                noticeMap.put(FMessages.fmLastCreatedTime, cursor
                        .getString(cursor
                                .getColumnIndex(FMessages.fmLastCreatedTime)));
                noticeMap.put(FMessages.fmNextCreatedTime, cursor
                        .getString(cursor
                                .getColumnIndex(FMessages.fmNextCreatedTime)));
                noticeMap.put(FMessages.fmAType, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmAType)));
                noticeMap.put(FMessages.fmWebURL, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmWebURL)));
                noticeMap.put(FMessages.fmImagePath, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmImagePath)));
                noticeMap.put(FMessages.fmInSTable, cursor.getString(cursor
                        .getColumnIndex(FMessages.fmInSTable)));
                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // 插入发送的消息
    public boolean insertMFMessageSendData(Integer fmSendID, Integer fmGetID,
                                           Integer fmIsAlarm, Integer fmOpenState, Integer fmPostpone,
                                           Integer fmColorType, Integer fmDisplayTime, Integer fmBeforeTime,
                                           Integer fmSourceType, Integer fmType, String fmParameter,
                                           String fmContent, String fmCreateTime, String fmDate,
                                           String fmTime, String fmSourceDesc, String fmSourceDescSpare,
                                           String fmTags, String fmRingDesc, String fmRingCode,
                                           String fmStartDate, String fmInitialCreatedTime,
                                           String fmLastCreatedTime, String fmNextCreatedTime,
                                           Integer fmStatus, Integer fmAType, String fmWebURL,
                                           String fmImagePath, int fmInSTable) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int fmID = getLocalId(-1, "FMessages", FMessages.fmID);
        String sql = "insert into FMessages values(" + fmID + "," + fmSendID
                + "," + fmGetID + "," + fmIsAlarm + "," + fmOpenState + ","
                + fmPostpone + "," + fmColorType + "," + fmDisplayTime + ","
                + fmBeforeTime + "," + fmSourceType + "," + fmType + ",'"
                + fmParameter + "','" + fmContent + "','" + fmCreateTime
                + "','" + fmDate + "','" + fmTime + "','" + fmSourceDesc
                + "','" + fmSourceDescSpare + "','" + fmTags + "','"
                + fmRingDesc + "','" + fmRingCode + "','" + fmStartDate + "','"
                + fmInitialCreatedTime + "','" + fmLastCreatedTime + "','"
                + fmNextCreatedTime + "' , " + fmStatus + "," + fmAType + ",'"
                + fmWebURL + "','" + fmImagePath + "'," + fmInSTable + ")";
        try {
            sqldb.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送成功，修改消息ID
     */
    public void updateMFMessageSendData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + FMessages.FMessages + " set " + FMessages.fmID
                + " = " + id + " where " + FMessages.fmID + " < " + 0;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送失败，删除保存消息id为负的
     */
    public void deleteMFMessageSendData() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + FMessages.FMessages + " where "
                + FMessages.fmID + " < " + 0;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLiaoTianData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + FMessages.FMessages + " where "
                + FMessages.fmID + " = " + id;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重复下载生成子记事查询母记事
     *
     * @return
     */
    public List<Map<String, String>> QueryDownChongFiData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from " + CLRepeatTable.CLRepeatTable + " where "
                + CLRepeatTable.repID + " = " + id
                + " order by repInitialCreatedTime desc,repTime asc";// fmDate
        // asc,fmTime
        // asc,
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(CLRepeatTable.repID, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repID)));
                noticeMap.put(CLRepeatTable.repBeforeTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repBeforeTime)));
                noticeMap.put(CLRepeatTable.repColorType, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repColorType)));
                noticeMap.put(CLRepeatTable.repDisplayTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repDisplayTime)));
                noticeMap.put(CLRepeatTable.repType, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repType)));
                noticeMap.put(CLRepeatTable.repIsAlarm, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repIsAlarm)));
                noticeMap.put(CLRepeatTable.repIsPuase, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repIsPuase)));
                noticeMap.put(CLRepeatTable.repIsImportant, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repIsImportant)));
                noticeMap.put(CLRepeatTable.repSourceType, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repSourceType)));
                noticeMap.put(CLRepeatTable.repUpdateState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repUpdateState)));
                noticeMap
                        .put(CLRepeatTable.repTypeParameter,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repTypeParameter)));
                noticeMap
                        .put(CLRepeatTable.repNextCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repNextCreatedTime)));
                noticeMap
                        .put(CLRepeatTable.repLastCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repLastCreatedTime)));
                noticeMap
                        .put(CLRepeatTable.repInitialCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repInitialCreatedTime)));
                noticeMap.put(CLRepeatTable.repStartDate, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStartDate)));
                noticeMap.put(CLRepeatTable.repContent, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repContent)));
                noticeMap.put(CLRepeatTable.repCreateTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repCreateTime)));
                noticeMap.put(CLRepeatTable.repSourceDesc, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repSourceDesc)));
                noticeMap
                        .put(CLRepeatTable.repSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repSourceDescSpare)));
                noticeMap.put(CLRepeatTable.repTime, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repTime)));
                noticeMap.put(CLRepeatTable.repRingDesc, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repRingDesc)));
                noticeMap.put(CLRepeatTable.repRingCode, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repRingCode)));
                noticeMap.put(CLRepeatTable.repUpdateTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repUpdateTime)));
                noticeMap.put(CLRepeatTable.repOpenState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repOpenState)));
                noticeMap
                        .put(CLRepeatTable.repcommendedUserName,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repcommendedUserName)));
                noticeMap
                        .put(CLRepeatTable.repcommendedUserId,
                                cursor.getString(cursor
                                        .getColumnIndex(CLRepeatTable.repcommendedUserId)));
                noticeMap.put(CLRepeatTable.repDateOne, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repDateOne)));
                noticeMap.put(CLRepeatTable.repDateTwo, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repDateTwo)));
                noticeMap.put(CLRepeatTable.repStateOne, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStateOne)));
                noticeMap.put(CLRepeatTable.repStateTwo, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repStateTwo)));
                noticeMap.put(CLRepeatTable.repAType, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repAType)));
                noticeMap.put(CLRepeatTable.repWebURL, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repWebURL)));
                noticeMap.put(CLRepeatTable.repImagePath, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repImagePath)));
                noticeMap.put(CLRepeatTable.repInSTable, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repInSTable)));
                noticeMap.put(CLRepeatTable.repEndState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repEndState)));
                noticeMap.put(CLRepeatTable.parReamrk, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.parReamrk)));
                noticeMap.put(CLRepeatTable.repRead, cursor.getString(cursor
                        .getColumnIndex(CLRepeatTable.repRead)));
                noticeMap.put(CLRepeatTable.repPostState, cursor
                        .getString(cursor
                                .getColumnIndex(CLRepeatTable.repPostState)));
                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 好友列表
     */
    // 插入发送的消息
    public boolean insertFriendsData(Integer fId, Integer uId, Integer state,
                                     Integer attentionState, Integer type, String backImage,
                                     String titleImg, String uName, Integer attState, Integer isFrends,
                                     Integer isV) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "insert into FriendsTable(fId,uId,state,attentionState,type,backImage,titleImg,uName,attState,isFrends,isV) "
                + "values(?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{fId, uId, state, attentionState, type,
                StringUtils.getIsStringEqulesNull(backImage),
                StringUtils.getIsStringEqulesNull(titleImg),
                StringUtils.getIsStringEqulesNull(uName), attState, isFrends,
                isV};
        // String sql = "insert into FriendsTable values(" + fId + "," + uId +
        // ","
        // + state + "," + attentionState + "," + type + ",'" + backImage
        // + "','" + titleImg + "','" + uName + "'," + attState + ","
        // + isFrends + "," + isV + ")";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询好友id是否相同
     */
    /**
     * 查询数据库是否有相同id的数据
     */
    public int CheckFriendsIDData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + FriendsTable.FriendsTable
                + " where " + FriendsTable.fId + " = " + id;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    /**
     * 修改好友列表
     */
    public void updateFriendsData(Integer fId, Integer uId, Integer state,
                                  Integer attentionState, Integer type, String backImage,
                                  String titleImg, String uName, Integer attState, Integer isFrends,
                                  Integer isV) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + FriendsTable.FriendsTable + " set "
                + FriendsTable.fId + " = ?" + " , " + FriendsTable.uId + " = ?"
                + ", " + FriendsTable.state + " = ?" + ", "
                + FriendsTable.attentionState + " = ?" + ", "
                + FriendsTable.type + " = ?" + ", " + FriendsTable.backImage
                + " = ?" + ", " + FriendsTable.titleImg + " = ?" + ", "
                + FriendsTable.uName + " = ?" + ", " + FriendsTable.attState
                + " = ?" + " , " + FriendsTable.isFrends + " = ?" + " , "
                + FriendsTable.isV + " = ?" + " where " + FriendsTable.fId
                + " = " + fId;
        Object[] mValue = new Object[]{fId, uId, state, attentionState, type,
                StringUtils.getIsStringEqulesNull(backImage),
                StringUtils.getIsStringEqulesNull(titleImg),
                StringUtils.getIsStringEqulesNull(uName), attState, isFrends,
                isV};
        // String sql = "update " + FriendsTable.FriendsTable + " set "
        // + FriendsTable.fId + " = " + fId + " , " + FriendsTable.uId
        // + " = " + uId + ", " + FriendsTable.state + " = " + state
        // + ", " + FriendsTable.attentionState + " = " + attentionState
        // + ", " + FriendsTable.type + " = " + type + ", "
        // + FriendsTable.backImage + " = '" + backImage + "', "
        // + FriendsTable.titleImg + " = '" + titleImg + "', "
        // + FriendsTable.uName + " = '" + uName + "', "
        // + FriendsTable.attState + " = " + attState + " , "
        // + FriendsTable.isFrends + " = " + isFrends + " , "
        // + FriendsTable.isV + " = " + isV + " where " + FriendsTable.fId
        // + " = " + fId;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询好友列表
     *
     * @return
     */
    public List<Map<String, String>> QueryFriendsData(int userId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from " + FriendsTable.FriendsTable + " where "
                + FriendsTable.uId + " = " + userId;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(FriendsTable.fId, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.fId)));
                noticeMap.put(FriendsTable.uId, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.uId)));
                noticeMap.put(FriendsTable.state, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.state)));
                noticeMap.put(FriendsTable.attentionState, cursor
                        .getString(cursor
                                .getColumnIndex(FriendsTable.attentionState)));
                noticeMap.put(FriendsTable.type, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.type)));
                noticeMap.put(FriendsTable.backImage, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.backImage)));
                noticeMap.put(FriendsTable.titleImg, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.titleImg)));
                noticeMap.put(FriendsTable.uName, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.uName)));
                noticeMap.put(FriendsTable.attState, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.attState)));
                noticeMap.put(FriendsTable.isFrends, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.isFrends)));
                noticeMap.put(FriendsTable.isV, cursor.getString(cursor
                        .getColumnIndex(FriendsTable.isV)));
                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 取消关注，删除日程表中的关注人日程
     */
    public void deleteFocusSch(String aid, int friendid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schAID + " = " + aid + " and "
                + ScheduleTable.schFriendID + " = " + friendid;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询是否有相同的关注日程id
     */
    public int CheckFocusIDData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + ScheduleTable.ScheduleTable
                + " where " + ScheduleTable.schAID + " = " + id;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    /**
     * 根据aid查询本地日程中focusstate状态
     */
    public Map<String, String> QueryFocusStateData(int Id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> map = new HashMap<String, String>();
        String sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schAID + " = " + Id;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                map.put(ScheduleTable.schFocusState, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schFocusState)));
                return map;
            }
            cursor.close();
            // if (cursor.getCount() > 0) {
            // if (cursor.moveToNext()) {
            // map.put(ScheduleTable.schFocusState,
            // cursor.getString(cursor
            // .getColumnIndex(ScheduleTable.schFocusState)));
            // cursor.close();
            // return map;
            // }
            // } else {
            // cursor.close();
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 修改关注好友的日程
     */
    /**
     * 修改关注日程
     *
     * @return
     */
    public void updateFocusScheduleData(String schContent, String schDate,
                                        String schTime, int schIsAlarm, int schBeforeTime,
                                        int schDisplayTime, int schIsPostpone, int schIsImportant,
                                        int schColorType, int schIsEnd, String schTags, int schSourceType,
                                        String schSourceDesc, String schSourceDescSpare, int schRepeatID,
                                        String schRepeatDate, String schUpdateTime, int schUpdateState,
                                        int schOpenState, int schRepeatLink, String schRingDesc,
                                        String schRingCode, String schcRecommendName, int schRead,
                                        int schAID, int schAType, String schWebURL, String schImagePath,
                                        int schFocusState, int schFriendID, int schcRecommendId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update ScheduleTable set " + ScheduleTable.schContent
                + " = ?" + "," + ScheduleTable.schDate + " = ? " + ", "
                + ScheduleTable.schTime + " = ?" + ", "
                + ScheduleTable.schIsAlarm + " = ?" + ", "
                + ScheduleTable.schBeforeTime + " = ?" + ", "
                + ScheduleTable.schDisplayTime + " = ?" + ", "
                + ScheduleTable.schIsPostpone + " = ?" + ", "
                + ScheduleTable.schIsImportant + " = ?" + ", "
                + ScheduleTable.schColorType + " = ?" + ", "
                + ScheduleTable.schIsEnd + " = ?" + ", "
                + ScheduleTable.schTags + " = ?" + ", "
                + ScheduleTable.schSourceType + " = ?" + ", "
                + ScheduleTable.schSourceDesc + " = ?" + ", "
                + ScheduleTable.schSourceDescSpare + " = ?" + ", "
                + ScheduleTable.schRepeatID + " = ?" + ", "
                + ScheduleTable.schRepeatDate + " = ?" + ", "
                + ScheduleTable.schUpdateTime + " = ?" + ", "
                + ScheduleTable.schUpdateState + " = ?" + ", "
                + ScheduleTable.schOpenState + " = ?" + ", "
                + ScheduleTable.schRepeatLink + " = ?" + ", "
                + ScheduleTable.schRingDesc + " = ?" + ", "
                + ScheduleTable.schRingCode + " = ?" + ", "
                + ScheduleTable.schcRecommendName + " = ?" + ", "
                + ScheduleTable.schRead + " = ?" + ", " + ScheduleTable.schAID
                + " = ?" + ", " + ScheduleTable.schAType + " = ?" + ", "
                + ScheduleTable.schWebURL + " = ?" + ", "
                + ScheduleTable.schImagePath + " = ?" + ", "
                + ScheduleTable.schFocusState + " = ?" + ", "
                + ScheduleTable.schFriendID + " = ?" + ", "
                + ScheduleTable.schcRecommendId + " = ? " + " where "
                + ScheduleTable.schAID + " = " + schAID;
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(schContent),
                StringUtils.getIsStringEqulesNull(schDate),
                StringUtils.getIsStringEqulesNull(schTime), schIsAlarm,
                schBeforeTime, schDisplayTime, schIsPostpone, schIsImportant,
                schColorType, schIsEnd,
                StringUtils.getIsStringEqulesNull(schTags), schSourceType,
                StringUtils.getIsStringEqulesNull(schSourceDesc),
                StringUtils.getIsStringEqulesNull(schSourceDescSpare),
                schRepeatID, StringUtils.getIsStringEqulesNull(schRepeatDate),
                StringUtils.getIsStringEqulesNull(schUpdateTime),
                schUpdateState, schOpenState, schRepeatLink,
                StringUtils.getIsStringEqulesNull(schRingDesc),
                StringUtils.getIsStringEqulesNull(schRingCode),
                StringUtils.getIsStringEqulesNull(schcRecommendName), schRead,
                schAID, schAType, StringUtils.getIsStringEqulesNull(schWebURL),
                StringUtils.getIsStringEqulesNull(schImagePath), schFocusState,
                schFriendID, schcRecommendId};
        // String sql = "update " + ScheduleTable.ScheduleTable + " set "
        // + ScheduleTable.schContent + " = '" + schContent + "', "
        // + ScheduleTable.schDate + " = '" + schDate + "', "
        // + ScheduleTable.schTime + " = '" + schTime + "', "
        // + ScheduleTable.schIsAlarm + " = " + schIsAlarm + ", "
        // + ScheduleTable.schBeforeTime + " = " + schBeforeTime + ", "
        // + ScheduleTable.schDisplayTime + " = " + schDisplayTime + ", "
        // + ScheduleTable.schIsPostpone + " = " + schIsPostpone + ", "
        // + ScheduleTable.schIsImportant + " = " + schIsImportant + ", "
        // + ScheduleTable.schColorType + " = " + schColorType + ", "
        // + ScheduleTable.schIsEnd + " = " + schIsEnd + ", "
        // + ScheduleTable.schTags + " = '" + schTags + "', "
        // + ScheduleTable.schSourceType + " = '" + schSourceType + "', "
        // + ScheduleTable.schSourceDesc + " = '" + schSourceDesc + "', "
        // + ScheduleTable.schSourceDescSpare + " = '"
        // + schSourceDescSpare + "', " + ScheduleTable.schRepeatID
        // + " = " + schRepeatID + ", " + ScheduleTable.schRepeatDate
        // + " = '" + schRepeatDate + "', " + ScheduleTable.schUpdateTime
        // + " = '" + schUpdateTime + "', " + ScheduleTable.schUpdateState
        // + " = " + schUpdateState + ", " + ScheduleTable.schOpenState
        // + " = " + schOpenState + ", " + ScheduleTable.schRepeatLink
        // + " = " + schRepeatLink + ", " + ScheduleTable.schRingDesc
        // + " = '" + schRingDesc + "', " + ScheduleTable.schRingCode
        // + " = '" + schRingCode + "', "
        // + ScheduleTable.schcRecommendName + " = '" + schcRecommendName
        // + "', " + ScheduleTable.schRead + " = " + schRead + ", "
        // + ScheduleTable.schAID + " = " + schAID + ", "
        // + ScheduleTable.schAType + " = " + schAType + ", "
        // + ScheduleTable.schWebURL + " = '" + schWebURL + "', "
        // + ScheduleTable.schImagePath + " = '" + schImagePath + "', "
        // + ScheduleTable.schFocusState + " = " + schFocusState + ", "
        // + ScheduleTable.schFriendID + " = " + schFriendID + ", "
        // + ScheduleTable.schcRecommendId + " = " + schcRecommendId
        // + " where " + ScheduleTable.schAID + " = " + schAID;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteFocusScheduleData(int schAID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "delete from ScheduleTable where schAID = " + schAID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    /**
     * 查询关注日程
     */
    public List<Map<String, String>> QueryFocusSch(int friendid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schFriendID + " = " + friendid + " and "
                + ScheduleTable.schFocusState + " != " + 1
                + " order by schDate desc,schDisplayTime asc,schTime asc";
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(ScheduleTable.schID, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schID)));
                noticeMap.put(ScheduleTable.schContent, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schContent)));
                noticeMap.put(ScheduleTable.schDate, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schDate)));
                noticeMap.put(ScheduleTable.schTime, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schTime)));
                noticeMap.put(ScheduleTable.schIsAlarm, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schIsAlarm)));
                noticeMap.put(ScheduleTable.schBeforeTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schBeforeTime)));
                noticeMap.put(ScheduleTable.schDisplayTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schDisplayTime)));
                noticeMap.put(ScheduleTable.schIsPostpone, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schIsPostpone)));
                noticeMap.put(ScheduleTable.schIsImportant, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schIsImportant)));
                noticeMap.put(ScheduleTable.schColorType, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schColorType)));
                noticeMap.put(ScheduleTable.schIsEnd, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schIsEnd)));
                noticeMap.put(ScheduleTable.schCreateTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schCreateTime)));
                noticeMap.put(ScheduleTable.schTags, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schTags)));
                noticeMap.put(ScheduleTable.schSourceType, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schSourceType)));
                noticeMap.put(ScheduleTable.schSourceDesc, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schSourceDesc)));
                noticeMap
                        .put(ScheduleTable.schSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schSourceDescSpare)));
                noticeMap.put(ScheduleTable.schRepeatID, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatID)));
                noticeMap.put(ScheduleTable.schRepeatDate, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatDate)));
                noticeMap.put(ScheduleTable.schUpdateTime, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schUpdateTime)));
                noticeMap.put(ScheduleTable.schUpdateState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schUpdateState)));
                noticeMap.put(ScheduleTable.schOpenState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schOpenState)));
                noticeMap.put(ScheduleTable.schRepeatLink, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRepeatLink)));
                noticeMap.put(ScheduleTable.schRingDesc, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRingDesc)));
                noticeMap.put(ScheduleTable.schRingCode, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schRingCode)));
                noticeMap
                        .put(ScheduleTable.schcRecommendName,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schcRecommendName)));
                noticeMap.put(ScheduleTable.schRead, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schRead)));
                noticeMap.put(ScheduleTable.schAID, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schAID)));
                noticeMap.put(ScheduleTable.schAType, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schAType)));
                noticeMap.put(ScheduleTable.schWebURL, cursor.getString(cursor
                        .getColumnIndex(ScheduleTable.schWebURL)));
                noticeMap.put(ScheduleTable.schImagePath, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schImagePath)));
                noticeMap.put(ScheduleTable.schFocusState, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schFocusState)));
                noticeMap.put(ScheduleTable.schFriendID, cursor
                        .getString(cursor
                                .getColumnIndex(ScheduleTable.schFriendID)));
                noticeMap
                        .put(ScheduleTable.schcRecommendId,
                                cursor.getString(cursor
                                        .getColumnIndex(ScheduleTable.schcRecommendId)));
                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /************************************ 闹钟 ********************************************/
    /**
     * 日程插入到闹钟表中，重复
     */
    public boolean insertClockData(String alarmResultTime,
                                   String noticeContent, Integer beforTime, String alarmClockTime,
                                   String alarmSoundDesc, String alarmSound, Integer displayAlarm,
                                   Integer postpone, Integer alarmType, Integer schID, Integer repID,
                                   Integer isAlarmClock, Integer isEnd, String alarmTypeParamter,
                                   int stateone, int statetwo, String dateone, String datetwo) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int alarmId = getLocalId(1, "LocateAllNoticeTable",
                LocateAllNoticeTable.alarmId);
        String sql = "insert into LocateAllNoticeTable(alarmId,alarmResultTime,noticeContent,beforTime,"
                + "alarmClockTime,alarmSoundDesc,alarmSound,displayAlarm,postpone,alarmType,schID,repID,isAlarmClock,"
                + "isEnd,alarmTypeParamter,stateone,statetwo,dateone,datetwo) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{alarmId,
                StringUtils.getIsStringEqulesNull(alarmResultTime),
                StringUtils.getIsStringEqulesNull(noticeContent), beforTime,
                StringUtils.getIsStringEqulesNull(alarmClockTime),
                StringUtils.getIsStringEqulesNull(alarmSoundDesc),
                StringUtils.getIsStringEqulesNull(alarmSound), displayAlarm,
                postpone, alarmType, schID, repID, isAlarmClock, isEnd,
                StringUtils.getIsStringEqulesNull(alarmTypeParamter), stateone,
                statetwo, StringUtils.getIsStringEqulesNull(dateone),
                StringUtils.getIsStringEqulesNull(datetwo)};
        // String sql = "insert into LocateAllNoticeTable values(" + alarmId
        // + ",'" + alarmResultTime + "','" + noticeContent + "',"
        // + beforTime + ",'" + alarmClockTime + "','" + alarmSoundDesc
        // + "','" + alarmSound + "'," + displayAlarm + "," + postpone
        // + "," + alarmType + "," + schID + "," + repID + ","
        // + isAlarmClock + "," + isEnd + ",'" + alarmTypeParamter + "', "
        // + stateone + "," + statetwo + ",'" + dateone + "', '" + datetwo
        // + "')";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 每天问候
     *
     * @return
     */
    public boolean insertEveryClockData(Integer alarmId,
                                        String alarmResultTime, String noticeContent, Integer beforTime,
                                        String alarmClockTime, String alarmSoundDesc, String alarmSound,
                                        Integer displayAlarm, Integer postpone, Integer alarmType,
                                        Integer schID, Integer repID, Integer isAlarmClock, Integer isEnd,
                                        String alarmTypeParamter, int stateone, int statetwo,
                                        String dateone, String datetwo) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "insert into LocateAllNoticeTable(alarmId,alarmResultTime,noticeContent,beforTime,"
                + "alarmClockTime,alarmSoundDesc,alarmSound,displayAlarm,postpone,alarmType,schID,repID,isAlarmClock,"
                + "isEnd,alarmTypeParamter,stateone,statetwo,dateone,datetwo) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{alarmId,
                StringUtils.getIsStringEqulesNull(alarmResultTime),
                StringUtils.getIsStringEqulesNull(noticeContent), beforTime,
                StringUtils.getIsStringEqulesNull(alarmClockTime),
                StringUtils.getIsStringEqulesNull(alarmSoundDesc),
                StringUtils.getIsStringEqulesNull(alarmSound), displayAlarm,
                postpone, alarmType, schID, repID, isAlarmClock, isEnd,
                StringUtils.getIsStringEqulesNull(alarmTypeParamter), stateone,
                statetwo, StringUtils.getIsStringEqulesNull(dateone),
                StringUtils.getIsStringEqulesNull(datetwo)};
        // String sql = "insert into LocateAllNoticeTable values(" + alarmId
        // + ",'" + alarmResultTime + "','" + noticeContent + "',"
        // + beforTime + ",'" + alarmClockTime + "','" + alarmSoundDesc
        // + "','" + alarmSound + "'," + displayAlarm + "," + postpone
        // + "," + alarmType + "," + schID + "," + repID + ","
        // + isAlarmClock + "," + isEnd + ",'" + alarmTypeParamter + "', "
        // + stateone + "," + statetwo + ",'" + dateone + "', '" + datetwo
        // + "')";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改日程闹钟表
     */
    public void updateSchClock(String alarmResultTime, String noticeContent,
                               Integer beforTime, String alarmClockTime, String alarmSoundDesc,
                               String alarmSound, Integer displayAlarm, Integer postpone,
                               Integer alarmType, Integer schID, Integer repID,
                               Integer isAlarmClock, Integer isEnd) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + LocateAllNoticeTable.alarmResultTime + " = ?"
                + ", " + LocateAllNoticeTable.noticeContent + " = ?" + ", "
                + LocateAllNoticeTable.beforTime + " = ?" + ", "
                + LocateAllNoticeTable.alarmClockTime + " = ?" + ", "
                + LocateAllNoticeTable.alarmSoundDesc + " = ?" + ", "
                + LocateAllNoticeTable.alarmSound + " = ?" + ", "
                + LocateAllNoticeTable.displayAlarm + " = ?" + ", "
                + LocateAllNoticeTable.postpone + " = ?" + ", "
                + LocateAllNoticeTable.alarmType + " = ?" + ", "
                + LocateAllNoticeTable.schID + " = ?" + ", "
                + LocateAllNoticeTable.repID + " = ?" + ", "
                + LocateAllNoticeTable.isAlarmClock + " = ?" + " , "
                + LocateAllNoticeTable.isEnd + " = ?" + " where "
                + LocateAllNoticeTable.schID + " = " + schID;
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(alarmResultTime),
                StringUtils.getIsStringEqulesNull(noticeContent), beforTime,
                StringUtils.getIsStringEqulesNull(alarmClockTime),
                StringUtils.getIsStringEqulesNull(alarmSoundDesc),
                StringUtils.getIsStringEqulesNull(alarmSound), displayAlarm,
                postpone, alarmType, schID, repID, isAlarmClock, isEnd};
        // String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
        // + " set " + LocateAllNoticeTable.alarmResultTime + " = '"
        // + alarmResultTime + "', " + LocateAllNoticeTable.noticeContent
        // + " = '" + noticeContent + "', "
        // + LocateAllNoticeTable.beforTime + " = " + beforTime + ", "
        // + LocateAllNoticeTable.alarmClockTime + " = '" + alarmClockTime
        // + "', " + LocateAllNoticeTable.alarmSoundDesc + " = '"
        // + alarmSoundDesc + "', " + LocateAllNoticeTable.alarmSound
        // + " = '" + alarmSound + "', "
        // + LocateAllNoticeTable.displayAlarm + " = " + displayAlarm
        // + ", " + LocateAllNoticeTable.postpone + " = " + postpone
        // + ", " + LocateAllNoticeTable.alarmType + " = " + alarmType
        // + ", " + LocateAllNoticeTable.schID + " = " + schID + ", "
        // + LocateAllNoticeTable.repID + " = " + repID + ", "
        // + LocateAllNoticeTable.isAlarmClock + " = " + isAlarmClock
        // + " , " + LocateAllNoticeTable.isEnd + " = " + isEnd
        // + " where " + LocateAllNoticeTable.schID + " = " + schID;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改闹钟日程ID
     */
    public void UpdateClockSchID(String SchID, String newid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + LocateAllNoticeTable.schID + " = " + newid
                + " where " + LocateAllNoticeTable.schID + " = " + SchID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改重复闹钟表
     */
    public void updateRepClock(String alarmResultTime, String noticeContent,
                               Integer beforTime, String alarmClockTime, String alarmSoundDesc,
                               String alarmSound, Integer displayAlarm, Integer postpone,
                               Integer alarmType, Integer schID, Integer repID,
                               Integer isAlarmClock, Integer isEnd, String alarmTypeParamter,
                               int stateone, int statetwo, String dateone, String datetwo) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + LocateAllNoticeTable.alarmResultTime + " = ?"
                + ", " + LocateAllNoticeTable.noticeContent + " = ?" + ", "
                + LocateAllNoticeTable.beforTime + " = ?" + ", "
                + LocateAllNoticeTable.alarmClockTime + " = ?" + ", "
                + LocateAllNoticeTable.alarmSoundDesc + " = ?" + ", "
                + LocateAllNoticeTable.alarmSound + " = ?" + ", "
                + LocateAllNoticeTable.displayAlarm + " = ?" + ", "
                + LocateAllNoticeTable.postpone + " = ?" + ", "
                + LocateAllNoticeTable.alarmType + " = ?" + ", "
                + LocateAllNoticeTable.schID + " = ?" + ", "
                + LocateAllNoticeTable.repID + " = ?" + " , "
                + LocateAllNoticeTable.isAlarmClock + " = ?" + ", "
                + LocateAllNoticeTable.isEnd + " = ?" + ", "
                + LocateAllNoticeTable.alarmTypeParamter + " = ?" + ", "
                + LocateAllNoticeTable.stateone + " = ?" + ", "
                + LocateAllNoticeTable.statetwo + " = ?" + ", "
                + LocateAllNoticeTable.dateone + " = ?" + ", "
                + LocateAllNoticeTable.datetwo + " = ?" + " where "
                + LocateAllNoticeTable.repID + " = " + repID;
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(alarmResultTime),
                StringUtils.getIsStringEqulesNull(noticeContent), beforTime,
                StringUtils.getIsStringEqulesNull(alarmClockTime),
                StringUtils.getIsStringEqulesNull(alarmSoundDesc),
                StringUtils.getIsStringEqulesNull(alarmSound), displayAlarm,
                postpone, alarmType, schID, repID, isAlarmClock, isEnd,
                StringUtils.getIsStringEqulesNull(alarmTypeParamter), stateone,
                statetwo, StringUtils.getIsStringEqulesNull(dateone),
                StringUtils.getIsStringEqulesNull(datetwo)};
        // String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
        // + " set " + LocateAllNoticeTable.alarmResultTime + " = '"
        // + alarmResultTime + "', " + LocateAllNoticeTable.noticeContent
        // + " = '" + noticeContent + "', "
        // + LocateAllNoticeTable.beforTime + " = " + beforTime + ", "
        // + LocateAllNoticeTable.alarmClockTime + " = '" + alarmClockTime
        // + "', " + LocateAllNoticeTable.alarmSoundDesc + " = '"
        // + alarmSoundDesc + "', " + LocateAllNoticeTable.alarmSound
        // + " = '" + alarmSound + "', "
        // + LocateAllNoticeTable.displayAlarm + " = " + displayAlarm
        // + ", " + LocateAllNoticeTable.postpone + " = " + postpone
        // + ", " + LocateAllNoticeTable.alarmType + " = " + alarmType
        // + ", " + LocateAllNoticeTable.schID + " = " + schID + ", "
        // + LocateAllNoticeTable.repID + " = " + repID + " , "
        // + LocateAllNoticeTable.isAlarmClock + " = " + isAlarmClock
        // + ", " + LocateAllNoticeTable.isEnd + " = " + isEnd + ", "
        // + LocateAllNoticeTable.alarmTypeParamter + " = '"
        // + alarmTypeParamter + "', " + LocateAllNoticeTable.stateone
        // + " = " + stateone + ", " + LocateAllNoticeTable.statetwo
        // + " = " + statetwo + ", " + LocateAllNoticeTable.dateone
        // + " = '" + dateone + "', " + LocateAllNoticeTable.datetwo
        // + " = '" + datetwo + "' where " + LocateAllNoticeTable.repID
        // + " = " + repID;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改闹钟重复ID
     */
    public void UpdateClockRepID(String RepID, String newid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + LocateAllNoticeTable.repID + " = " + newid
                + " where " + LocateAllNoticeTable.repID + " = " + RepID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除日程闹钟
     */
    public void deleteSch(int schID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + LocateAllNoticeTable.LocateAllNoticeTable
                + " where " + LocateAllNoticeTable.schID + " = " + schID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除重复闹钟
     */
    public void deleteRep(int repID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + LocateAllNoticeTable.LocateAllNoticeTable
                + " where " + LocateAllNoticeTable.repID + " = " + repID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除每天闹钟提醒
     */
    public void deleteEveryClock(int alarmid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + LocateAllNoticeTable.LocateAllNoticeTable
                + " where " + LocateAllNoticeTable.alarmId + " = " + alarmid;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改闹钟日程是否结束
     */
    @SuppressWarnings("unchecked")
    public void updateSchIsEnd(Map<String, String> upMap, String sqlWhere) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String updateMap = "";

        // 取出需修改的字段拼接
        Set set = upMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();

            if (isNum(mapentry.getValue().toString())) {
                updateMap = updateMap + mapentry.getKey() + "="
                        + mapentry.getValue() + ",";
            } else {
                updateMap = updateMap + mapentry.getKey() + "='"
                        + mapentry.getValue() + "',";
            }

        }
        String updateStr = updateMap.substring(0, updateMap.lastIndexOf(","));
        String[] str = updateStr.split(",");
        String isEnd = str[0].toString();
        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + isEnd + " " + sqlWhere;

        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    public void updateClockDate(int id, String alarmResultTime,
                                String alarmClockTime) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + LocateAllNoticeTable.alarmResultTime + " = '"
                + alarmResultTime + "', " + LocateAllNoticeTable.alarmClockTime
                + " = '" + alarmClockTime + "' where "
                + LocateAllNoticeTable.alarmId + " = " + id;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 更改日程闹钟提醒时间
     */
    public void updateSchClockDate(int schId, String alarmResultTime,
                                   String alarmClockTime) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + LocateAllNoticeTable.alarmResultTime + " = '"
                + alarmResultTime + "', " + LocateAllNoticeTable.alarmClockTime
                + " = '" + alarmClockTime + "' where "
                + LocateAllNoticeTable.schID + " = " + schId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 更改为待办
     */
    public void updateUnTaskClockDate(int schId, String alarmSoundDesc,
                                      String alarmSound) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + LocateAllNoticeTable.LocateAllNoticeTable
                + " set " + LocateAllNoticeTable.beforTime + " = " + 0 + ", "
                + LocateAllNoticeTable.alarmSoundDesc + " = '" + alarmSoundDesc
                + "'," + LocateAllNoticeTable.alarmSound + " = '" + alarmSound
                + "'," + LocateAllNoticeTable.displayAlarm + " = " + 0 + ","
                + LocateAllNoticeTable.postpone + " = " + 1 + ","
                + LocateAllNoticeTable.isAlarmClock + " = " + 1 + ","
                + LocateAllNoticeTable.isEnd + " = " + 0 + " where "
                + LocateAllNoticeTable.schID + " = " + schId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询是否有id重复
     *
     * @param id
     * @return
     */
    public int CheckClockIDData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from "
                + LocateAllNoticeTable.LocateAllNoticeTable + " where "
                + LocateAllNoticeTable.alarmId + " = " + id;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    /**
     * 获得闹钟
     *
     * @return List<Map<String,String>>
     */
    public List<Map<String, String>> getAlarmData() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> noticeMap = null;
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        dataList.clear();
        // 当前时间
        String currentTimeSs = DateUtil.formatDateTimeSs(new Date());
        String content = "";
        String before = "";
        // try {
        // File temp = new File(Environment.getExternalStorageDirectory()
        // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
        // if (!temp.exists()) {
        // temp.mkdir();
        // }
        // FileWriter fw = new FileWriter(temp.getAbsolutePath()
        // + "/AppwritealarmDate.txt", true);
        // if (fw != null) {
        // fw.flush();
        // fw.write("  " + currentTimeSs + "\n");
        // fw.close();
        // }
        // } catch (Exception e1) {
        // e1.printStackTrace();
        // }
        // int alarmId = 0;// 闹钟ID
        // + ">='"
        // + DateTimeHelper.formatDateTimetoString(new Date(),
        // DateTimeHelper.FMT_yyyyMMddHHmm)
        // + "'
        String sql = "select * from LocateAllNoticeTable where alarmClockTime!='' and displayAlarm=1 and postpone=0 and isEnd=0 and repID=0 and alarmResultTime"
                + " >= '"
                + DateUtil.formatDateTimeSs(new Date())
                + "' order by alarmResultTime asc";

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {

                noticeMap = new HashMap<String, String>();
                noticeMap.put(LocateAllNoticeTable.alarmId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.alarmId)));

                content = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.noticeContent));
                before = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.beforTime));
                noticeMap
                        .put(LocateAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));
                noticeMap
                        .put(LocateAllNoticeTable.alarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSound)));
                String aclock = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.alarmClockTime));
                if (aclock.length() == 16) {
                    aclock = DateUtil.formatDateTimeSs(DateUtil
                            .parseDateTime(aclock));
                }
                String alarmResultTime = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.alarmResultTime));
                if (alarmResultTime.length() == 16) {
                    alarmResultTime = DateUtil.formatDateTimeSs(DateUtil
                            .parseDateTime(alarmResultTime));
                }

                noticeMap.put(LocateAllNoticeTable.noticeContent, content);
                noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        alarmResultTime);

                noticeMap
                        .put(LocateAllNoticeTable.alarmClockTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmClockTime)));

                noticeMap
                        .put(LocateAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.beforTime)));

                noticeMap.put(LocateAllNoticeTable.alarmType, "0");
                noticeMap
                        .put(LocateAllNoticeTable.isAlarmClock,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock)));
                noticeMap
                        .put(LocateAllNoticeTable.displayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.postpone)));
                noticeMap
                        .put(LocateAllNoticeTable.stateone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.stateone)));
                noticeMap
                        .put(LocateAllNoticeTable.statetwo,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.statetwo)));
                noticeMap.put(LocateAllNoticeTable.dateone, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.dateone)));
                noticeMap.put(LocateAllNoticeTable.datetwo, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.datetwo)));
                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            // try {
            // File temp = new File(Environment.getExternalStorageDirectory()
            // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
            // if (!temp.exists()) {
            // temp.mkdir();
            // }
            // FileWriter fw = new FileWriter(temp.getAbsolutePath()
            // + "/dataerror.txt", true);
            // if (fw != null) {
            // fw.flush();
            // fw.write(DateUtil.formatDateTimeSs(new Date()));
            // fw.write("  ," + e);
            // fw.close();
            // }
            // } catch (Exception e1) {
            // e1.printStackTrace();
            // }
            e.printStackTrace();
        }

        String nowDate = DateTimeHelper.formatDateTimetoString(new Date(),
                DateTimeHelper.FMT_yyyyMMdd);
        // 重复闹钟
        try {
            String sqlr = "select * from LocateAllNoticeTable where alarmClockTime!='' and displayAlarm=1 and alarmType!=0 and repID!=0 and schID=0 and isEnd=0 "
                    + " order by alarmResultTime asc";
            Cursor cursorr = sqldb.rawQuery(sqlr, null);
            while (cursorr.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                int tpDataType = cursorr.getInt(cursorr
                        .getColumnIndex(LocateAllNoticeTable.alarmType));
                String isAlarmClock = cursorr.getString(cursorr
                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock));
                noticeMap.put(LocateAllNoticeTable.alarmId, cursorr
                        .getString(cursorr
                                .getColumnIndex(LocateAllNoticeTable.alarmId)));
                content = cursorr.getString(cursorr
                        .getColumnIndex(LocateAllNoticeTable.noticeContent));
                before = cursorr.getString(cursorr
                        .getColumnIndex(LocateAllNoticeTable.beforTime));
                noticeMap
                        .put(LocateAllNoticeTable.alarmSoundDesc,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));
                noticeMap
                        .put(LocateAllNoticeTable.alarmSound,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.alarmSound)));
                noticeMap
                        .put(LocateAllNoticeTable.alarmClockTime,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.alarmClockTime)));

                noticeMap
                        .put(LocateAllNoticeTable.beforTime,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.beforTime)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmType,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.alarmType)));
                noticeMap
                        .put(LocateAllNoticeTable.isAlarmClock,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock)));
                noticeMap
                        .put(LocateAllNoticeTable.displayAlarm,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateAllNoticeTable.postpone,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.postpone)));
                noticeMap
                        .put(LocateAllNoticeTable.noticeContent,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.noticeContent)));
                noticeMap
                        .put(LocateAllNoticeTable.stateone,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.stateone)));
                noticeMap
                        .put(LocateAllNoticeTable.statetwo,
                                cursorr.getString(cursorr
                                        .getColumnIndex(LocateAllNoticeTable.statetwo)));
                noticeMap.put(LocateAllNoticeTable.dateone, cursorr
                        .getString(cursorr
                                .getColumnIndex(LocateAllNoticeTable.dateone)));
                noticeMap.put(LocateAllNoticeTable.datetwo, cursorr
                        .getString(cursorr
                                .getColumnIndex(LocateAllNoticeTable.datetwo)));
                String dateone = cursorr.getString(cursorr
                        .getColumnIndex(LocateAllNoticeTable.dateone));
                String datetwo = cursorr.getString(cursorr
                        .getColumnIndex(LocateAllNoticeTable.datetwo));
                if ("null".equals(dateone)) {
                    dateone = "";
                }
                if ("null".equals(datetwo)) {
                    datetwo = "";
                }
                Calendar datecalendar = Calendar.getInstance();
                if (!"0".equals(before)) {
                    if (!"".equals(dateone)) {
                        datecalendar.setTime(DateUtil.parseDateTime(dateone));
                        datecalendar.add(Calendar.MINUTE,
                                -Integer.parseInt(before));
                        dateone = DateUtil.formatDateTime(datecalendar
                                .getTime());
                    }
                    if (!"".equals(datetwo)) {
                        datecalendar.setTime(DateUtil.parseDateTime(datetwo));
                        datecalendar.add(Calendar.MINUTE,
                                -Integer.parseInt(before));
                        datetwo = DateUtil.formatDateTime(datecalendar
                                .getTime());
                    }
                }
                RepeatBean repeatBean = null;
                if (tpDataType == 1) {// 每天
                    String everynoteDate = "";// 记事日期
                    Calendar cals = Calendar.getInstance();
                    cals.add(Calendar.DATE, 1);
                    String tomorrow = DateTimeHelper.formatDateTimetoString(
                            cals.getTime(), DateTimeHelper.FMT_yyyyMMdd);
                    String alarmclock = "";
                    everynoteDate = nowDate;

                    String alarmResultStartTime = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmResultTime));

                    alarmclock = everynoteDate
                            + " "
                            + cursorr
                            .getString(
                                    cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                            .substring(11);

                    repeatBean = RepeatDateUtils
                            .saveCalendar(
                                    cursorr.getString(
                                            cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                                            .substring(11, 16), 1, "", "");
                    if ("1".equals(isAlarmClock)) {// 准时有，提前没有
                        // 如果闹钟时间大于当前时间
                        // if (DateUtil.parseDateTimeSs(
                        // nowDate + " "
                        // + alarmResultStartTime.substring(11))
                        // .getTime() >=
                        // DateUtil.parseDateTimeSs(currentTimeSs).getTime()) {
                        // everynoteDate = nowDate;
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // everynoteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // } else {
                        // everynoteDate = tomorrow;
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(everynoteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11))));
                        // }
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                    } else if ("2".equals(isAlarmClock)) {// 准时没有，提前有
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(
                        // nowDate + " "
                        // + alarmResultStartTime.substring(11))
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // everynoteDate = nowDate;
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // everynoteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // } else {
                        // everynoteDate = tomorrow;
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // everynoteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // }
                    } else if ("3".equals(isAlarmClock)) {
                        // if (DateUtil.parseDateTimeSs(
                        // nowDate + " "
                        // + alarmResultStartTime.substring(11))
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // everynoteDate = nowDate;
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // everynoteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // } else {
                        // everynoteDate = tomorrow;
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // everynoteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // }
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                    }
                    noticeMap
                            .put(LocateAllNoticeTable.alarmClockTime,
                                    DateUtil.formatDateTimeSs(DateUtil
                                            .parseDateTime(repeatBean.repNextCreatedTime)));
                    if (!"".equals(dateone) && !"".equals(datetwo)) {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                        if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                    } else {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if ("".equals(dateone) && "".equals(datetwo)) {
                            dataList.add(noticeMap);
                        }
                    }
                } else if (tpDataType == 2) {// 每周
                    String alarmResultStartTime = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmResultTime));
                    String paramter = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmTypeParamter));
                    repeatBean = RepeatDateUtils
                            .saveCalendar(
                                    cursorr.getString(
                                            cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                                            .substring(11, 16), 2, paramter
                                            .replace("[", "").replace("]", "")
                                            .replace("\"", ""), "");
                    if ("1".equals(isAlarmClock)) {// 准时有，提前没有
                        // 如果闹钟时间大于当前时间
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    } else if ("2".equals(isAlarmClock)) {// 准时没有，提前有
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                    } else if ("3".equals(isAlarmClock)) {
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    }
                    if (!"".equals(dateone) && !"".equals(datetwo)) {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                        if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                    } else {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if ("".equals(dateone) && "".equals(datetwo)) {
                            dataList.add(noticeMap);
                        }
                    }
                } else if (tpDataType == 3) {// 每月
                    String alarmResultStartTime = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmResultTime));
                    String paramter = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmTypeParamter));
                    repeatBean = RepeatDateUtils
                            .saveCalendar(
                                    cursorr.getString(
                                            cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                                            .substring(11, 16), 3, paramter
                                            .replace("[", "").replace("]", "")
                                            .replace("\"", ""), "");
                    if ("1".equals(isAlarmClock)) {// 准时有，提前没有
                        // 如果闹钟时间大于当前时间
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    } else if ("2".equals(isAlarmClock)) {// 准时没有，提前有
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    } else if ("3".equals(isAlarmClock)) {
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    }
                    if (!"".equals(dateone) && !"".equals(datetwo)) {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                        if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                    } else {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if ("".equals(dateone) && "".equals(datetwo)) {
                            dataList.add(noticeMap);
                        }
                    }
                } else if (tpDataType == 4) {// 每年 阳历
                    String alarmResultStartTime = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmResultTime));
                    String paramter = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmTypeParamter));
                    repeatBean = RepeatDateUtils
                            .saveCalendar(
                                    cursorr.getString(
                                            cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                                            .substring(11, 16), 4, paramter
                                            .replace("[", "").replace("]", "")
                                            .replace("\"", ""), "0");
                    if ("1".equals(isAlarmClock)) {// 准时有，提前没有
                        // 如果闹钟时间大于当前时间
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    } else if ("2".equals(isAlarmClock)) {// 准时没有，提前有
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    } else if ("3".equals(isAlarmClock)) {
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    }
                    if (!"".equals(dateone) && !"".equals(datetwo)) {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                        if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                    } else {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if ("".equals(dateone) && "".equals(datetwo)) {
                            dataList.add(noticeMap);
                        }
                    }
                } else if (tpDataType == 5) {// 工作日
                    String noteDate = "";// 记事日期
                    Calendar calendar = Calendar.getInstance();
                    String alarmclocktime = "";
                    String nowday = DateUtil.formatDate(new Date());
                    if ("周六".equals(CharacterUtil.getWeekOfDate(
                            mContextApplication, DateUtil.parseDate(nowday)))) {
                        calendar.add(Calendar.DATE, 2);
                        noteDate = DateUtil.formatDate(calendar.getTime());
                    } else if ("周日".equals(CharacterUtil.getWeekOfDate(
                            mContextApplication, DateUtil.parseDate(nowday)))) {
                        calendar.add(Calendar.DATE, 1);
                        noteDate = DateUtil.formatDate(calendar.getTime());
                    } else {
                        noteDate = nowday;
                    }
                    String alarmResultStartTime = noteDate
                            + " "
                            + cursorr
                            .getString(
                                    cursorr.getColumnIndex(LocateAllNoticeTable.alarmResultTime))
                            .substring(11);
                    alarmclocktime = noteDate
                            + " "
                            + cursorr
                            .getString(
                                    cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                            .substring(11);

                    repeatBean = RepeatDateUtils
                            .saveCalendar(
                                    cursorr.getString(
                                            cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                                            .substring(11, 16), 5, "", "");
                    if ("1".equals(isAlarmClock)) {// 准时有，提前没有
                        // 如果闹钟时间大于当前时间
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil
                        // .parseDateTimeSs(
                        // noteDate
                        // + " "
                        // + cursorr
                        // .getString(
                        // cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                        // .substring(11))
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // noteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // } else {
                        // noteDate = repeatBean.repNextCreatedTime.substring(
                        // 0, 10);
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // noteDate
                        // + " "
                        // + DateUtil
                        // .formatDateTimeSs(
                        // DateUtil.parseDateTime(repeatBean.repNextCreatedTime))
                        // .substring(11));
                        // }
                    } else if ("2".equals(isAlarmClock)) {// 准时没有，提前有
                        // if (DateUtil
                        // .parseDateTimeSs(
                        // noteDate
                        // + " "
                        // + cursorr
                        // .getString(
                        // cursorr.getColumnIndex(LocateAllNoticeTable.alarmResultTime))
                        // .substring(11))
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // noteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // } else {
                        // noteDate = repeatBean.repNextCreatedTime.substring(
                        // 0, 10);
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // noteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // }
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                    } else if ("3".equals(isAlarmClock)) {
                        // if (DateUtil.parseDateTimeSs(
                        // noteDate + " "
                        // + alarmResultStartTime.substring(11))
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // noteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // } else {
                        // noteDate = repeatBean.repNextCreatedTime.substring(
                        // 0, 10);
                        // noticeMap.put(
                        // LocateAllNoticeTable.alarmResultTime,
                        // noteDate
                        // + " "
                        // + alarmResultStartTime
                        // .substring(11));
                        // }
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                    }
                    noticeMap
                            .put(LocateAllNoticeTable.alarmClockTime,
                                    DateUtil.formatDateTimeSs(DateUtil
                                            .parseDateTime(repeatBean.repNextCreatedTime)));
                    // noticeMap
                    // .put(LocateAllNoticeTable.alarmClockTime,
                    // noteDate
                    // + " "
                    // + cursorr
                    // .getString(
                    // cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                    // .substring(11));
                    if (!"".equals(dateone) && !"".equals(datetwo)) {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                        if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                    } else {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if ("".equals(dateone) && "".equals(datetwo)) {
                            dataList.add(noticeMap);
                        }
                    }
                } else if (tpDataType == 6) {// 每年 阴历
                    String alarmResultStartTime = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmResultTime));
                    String paramter = cursorr
                            .getString(cursorr
                                    .getColumnIndex(LocateAllNoticeTable.alarmTypeParamter));
                    repeatBean = RepeatDateUtils
                            .saveCalendar(
                                    cursorr.getString(
                                            cursorr.getColumnIndex(LocateAllNoticeTable.alarmClockTime))
                                            .substring(11, 16), 4, paramter
                                            .replace("[", "").replace("]", "")
                                            .replace("\"", ""), "1");
                    if ("1".equals(isAlarmClock)) {// 准时有，提前没有
                        // 如果闹钟时间大于当前时间
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    } else if ("2".equals(isAlarmClock)) {// 准时没有，提前有
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    } else if ("3".equals(isAlarmClock)) {
                        noticeMap
                                .put(LocateAllNoticeTable.alarmResultTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        noticeMap
                                .put(LocateAllNoticeTable.alarmClockTime,
                                        DateUtil.formatDateTimeSs(DateUtil
                                                .parseDateTime(repeatBean.repNextCreatedTime)));
                        // if (DateUtil.parseDateTimeSs(alarmResultStartTime)
                        // .getTime() >= DateUtil.parseDateTimeSs(
                        // currentTimeSs).getTime()) {
                        // noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        // alarmResultStartTime);
                        // noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        // alarmResultStartTime);
                        // } else {
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmResultTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // noticeMap
                        // .put(LocateAllNoticeTable.alarmClockTime,
                        // DateUtil.formatDateTimeSs(DateUtil
                        // .parseDateTime(repeatBean.repNextCreatedTime)));
                        // }
                    }
                    if (!"".equals(dateone) && !"".equals(datetwo)) {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                        if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        }
                    } else {
                        if (!"".equals(dateone)) {
                            if (dateone.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if (!"".equals(datetwo)) {
                            if (datetwo.equals(repeatBean.repNextCreatedTime)) {

                            } else {
                                dataList.add(noticeMap);
                            }
                        } else if ("".equals(dateone) && "".equals(datetwo)) {
                            dataList.add(noticeMap);
                        }
                    }
                }
            }
            cursorr.close();
        } catch (Exception e) {
            // try {
            // File temp = new File(Environment.getExternalStorageDirectory()
            // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
            // if (!temp.exists()) {
            // temp.mkdir();
            // }
            // FileWriter fw = new FileWriter(temp.getAbsolutePath()
            // + "/dataerror.txt", true);
            // if (fw != null) {
            // fw.flush();
            // fw.write(DateUtil.formatDateTimeSs(new Date()));
            // fw.write("  ," + e);
            // fw.close();
            // }
            // } catch (Exception e1) {
            // e1.printStackTrace();
            // }
            e.printStackTrace();
        }

        // 顺延闹钟
        String sqlPostPone = "select * from LocateAllNoticeTable where displayAlarm=1 and isEnd=0 and repID=0 and alarmClockTime!='' and postpone=1 and alarmId>=0 "
                + " order by alarmResultTime asc";// and alarmResultTime +
        // " >= '"+ sdf.format(new
        // Date())
        try {

            Cursor cursor = sqldb.rawQuery(sqlPostPone, null);
            while (cursor.moveToNext()) {

                // int alarmId = 0;
                // alarmId = alarmId + 1;
                noticeMap = new HashMap<String, String>();

                // noticeMap.put(LocateAllNoticeTable.ID,
                // cursor.getString(cursor.getColumnIndex(LocateAllNoticeTable.ID)));
                noticeMap.put(LocateAllNoticeTable.alarmId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.alarmId)));

                content = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.noticeContent));
                before = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.beforTime));

                noticeMap
                        .put(LocateAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSound)));
                noticeMap
                        .put(LocateAllNoticeTable.stateone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.stateone)));
                noticeMap
                        .put(LocateAllNoticeTable.statetwo,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.statetwo)));
                noticeMap.put(LocateAllNoticeTable.dateone, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.dateone)));
                noticeMap.put(LocateAllNoticeTable.datetwo, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.datetwo)));
                String aclock = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.alarmClockTime));
                if (aclock.length() == 16) {
                    aclock = DateUtil.formatDateTimeSs(DateUtil
                            .parseDateTime(aclock));
                }
                // 计算闹钟最终执行时间
                // String nowDate = DateTimeHelper.formatDateTimetoString(
                // new Date(), DateTimeHelper.FMT_yyyyMMdd);
                String isAlarmClock = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock));
                // Calendar cd = Calendar.getInstance();
                // cd.setTime(DateUtil.parseDateTimeSs(nowDate + " "
                // + aclock.substring(11)));

                // cd.add(Calendar.MINUTE, -cursor.getInt(cursor
                // .getColumnIndex(LocateAllNoticeTable.beforTime)));
                // String alarmResultTime =
                // DateTimeHelper.formatDateTimetoString(
                // cd.getTime(), DateTimeHelper.FMT_yyyyMMddHHmmss);
                String alarmResultTime = aclock;
                RepeatBean repeatBean = RepeatDateUtils.saveCalendar(
                        alarmResultTime.substring(11, 16), 1, "", "");
                if (alarmResultTime.length() == 16) {
                    alarmResultTime = DateUtil.formatDateTimeSs(DateUtil
                            .parseDateTime(alarmResultTime));
                }
                if ("1".equals(isAlarmClock)) {
                    // if (cd.getTime().getTime() > DateUtil.parseDateTimeSs(
                    // currentTimeSs).getTime()) {
                    // // alarmResultTime = cursor
                    // // .getString(cursor
                    // // .getColumnIndex(LocateAllNoticeTable.alarmClockTime));
                    // } else {
                    // Calendar cdx = Calendar.getInstance();
                    // cdx.setTime(cd.getTime());
                    // cdx.add(cdx.DATE, 1);
                    // alarmResultTime = DateTimeHelper
                    // .formatDateTimetoString(cdx.getTime(),
                    // DateTimeHelper.FMT_yyyyMMddHHmmss);
                    //
                    // }
                } else if ("2".equals(isAlarmClock)) {
                    // if (cd.getTime().getTime() < DateUtil.parseDateTimeSs(
                    // currentTimeSs).getTime()) {
                    // Calendar cdx = Calendar.getInstance();
                    // cdx.setTime(cd.getTime());
                    // cdx.add(cdx.DATE, 1);
                    // alarmResultTime = DateTimeHelper
                    // .formatDateTimetoString(cdx.getTime(),
                    // DateTimeHelper.FMT_yyyyMMddHHmmss);
                    // }
                } else if ("3".equals(isAlarmClock)) {
                    // if (cd.getTime().getTime() < DateUtil.parseDateTimeSs(
                    // currentTimeSs).getTime()) {
                    // Calendar cd1 = Calendar.getInstance();
                    // cd1.setTime(DateUtil.parseDateTime(nowDate + " "
                    // + aclock.substring(11)));
                    // if (cd1.getTime().getTime() < System
                    // .currentTimeMillis()) {
                    // Calendar cdx = Calendar.getInstance();
                    // cdx.setTime(cd.getTime());
                    // cdx.add(cdx.DATE, 1);
                    // alarmResultTime = DateTimeHelper
                    // .formatDateTimetoString(cdx.getTime(),
                    // DateTimeHelper.FMT_yyyyMMddHHmmss);
                    // }
                    // }
                }

                noticeMap.put(LocateAllNoticeTable.noticeContent, content);
                noticeMap.put(LocateAllNoticeTable.alarmResultTime, DateUtil
                        .formatDateTimeSs(DateUtil
                                .parseDateTime(repeatBean.repNextCreatedTime)));

                noticeMap.put(LocateAllNoticeTable.alarmClockTime, DateUtil
                        .formatDateTimeSs(DateUtil
                                .parseDateTime(repeatBean.repNextCreatedTime)));

                noticeMap
                        .put(LocateAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.beforTime)));

                noticeMap.put(LocateAllNoticeTable.alarmType, "0");
                noticeMap
                        .put(LocateAllNoticeTable.isAlarmClock,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock)));
                noticeMap
                        .put(LocateAllNoticeTable.displayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.postpone)));

                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            // try {
            // File temp = new File(Environment.getExternalStorageDirectory()
            // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
            // if (!temp.exists()) {
            // temp.mkdir();
            // }
            // FileWriter fw = new FileWriter(temp.getAbsolutePath()
            // + "/dataerror.txt", true);
            // if (fw != null) {
            // fw.flush();
            // fw.write(DateUtil.formatDateTimeSs(new Date()));
            // fw.write("  ," + e);
            // fw.close();
            // }
            // } catch (Exception e1) {
            // e1.printStackTrace();
            // }
            e.printStackTrace();
        }
        // 每天早晚提醒闹钟
        String everysql = "select * from LocateAllNoticeTable where displayAlarm=1  and repID=0 and alarmClockTime!='' and postpone=1 and alarmId<0 and alarmId !=-10 "
                + " order by alarmResultTime asc";

        try {

            Cursor cursor = sqldb.rawQuery(everysql, null);
            while (cursor.moveToNext()) {

                noticeMap = new HashMap<String, String>();

                noticeMap.put(LocateAllNoticeTable.alarmId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.alarmId)));

                noticeMap
                        .put(LocateAllNoticeTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.noticeContent)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSound)));
                noticeMap
                        .put(LocateAllNoticeTable.stateone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.stateone)));
                noticeMap
                        .put(LocateAllNoticeTable.statetwo,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.statetwo)));
                noticeMap.put(LocateAllNoticeTable.dateone, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.dateone)));
                noticeMap.put(LocateAllNoticeTable.datetwo, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.datetwo)));
                String alarmtime = cursor
                        .getString(
                                cursor.getColumnIndex(LocateAllNoticeTable.alarmResultTime))
                        .substring(11);
                if (alarmtime.length() == 5) {
                    alarmtime = alarmtime + ":00";
                }
                Calendar cd = Calendar.getInstance();
                cd.setTime(DateUtil.parseDateTimeSs(nowDate + " " + alarmtime));
                String alarmResultTime = DateTimeHelper.formatDateTimetoString(
                        cd.getTime(), DateTimeHelper.FMT_yyyyMMddHHmmss);
                if (DateUtil.parseDateTimeSs(
                        DateUtil.formatDateTimeSs(cd.getTime())).getTime() > DateUtil
                        .parseDateTimeSs(currentTimeSs).getTime()) {

                } else {
                    Calendar cdx = Calendar.getInstance();
                    cdx.setTime(cd.getTime());
                    cdx.add(cdx.DATE, 1);
                    alarmResultTime = DateTimeHelper.formatDateTimetoString(
                            cdx.getTime(), DateTimeHelper.FMT_yyyyMMddHHmmss);
                }
                noticeMap
                        .put(LocateAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.beforTime)));

                noticeMap.put(LocateAllNoticeTable.alarmType, "7");
                noticeMap
                        .put(LocateAllNoticeTable.isAlarmClock,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock)));
                noticeMap
                        .put(LocateAllNoticeTable.displayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.postpone)));
                noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        alarmResultTime);

                noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        alarmResultTime);
                dataList.add(noticeMap);
            }

            cursor.close();

        } catch (Exception e) {
            // try {
            // File temp = new File(Environment.getExternalStorageDirectory()
            // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
            // if (!temp.exists()) {
            // temp.mkdir();
            // }
            // FileWriter fw = new FileWriter(temp.getAbsolutePath()
            // + "/dataerror.txt", true);
            // if (fw != null) {
            // fw.flush();
            // fw.write(DateUtil.formatDateTimeSs(new Date()));
            // fw.write("  ," + e);
            // fw.close();
            // }
            // } catch (Exception e1) {
            // e1.printStackTrace();
            // }
            e.printStackTrace();
        }
        // 每5分钟执行一次
        String mysql = "select * from LocateAllNoticeTable where alarmId=-10 "
                + " order by alarmResultTime asc";

        try {

            Cursor cursor = sqldb.rawQuery(mysql, null);
            while (cursor.moveToNext()) {

                noticeMap = new HashMap<String, String>();

                noticeMap.put(LocateAllNoticeTable.alarmId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.alarmId)));

                noticeMap
                        .put(LocateAllNoticeTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.noticeContent)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSound)));
                noticeMap
                        .put(LocateAllNoticeTable.stateone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.stateone)));
                noticeMap
                        .put(LocateAllNoticeTable.statetwo,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.statetwo)));
                noticeMap.put(LocateAllNoticeTable.dateone, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.dateone)));
                noticeMap.put(LocateAllNoticeTable.datetwo, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.datetwo)));
                String alarmtime = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.alarmResultTime));

                Calendar cd = Calendar.getInstance();
                cd.setTime(DateUtil.parseDateTimeSs(alarmtime));
                String alarmResultTime = DateTimeHelper.formatDateTimetoString(
                        cd.getTime(), DateTimeHelper.FMT_yyyyMMddHHmmss);
                // if (cd.getTime().getTime() > System.currentTimeMillis()) {
                //
                // } else {
                // Calendar cdx = Calendar.getInstance();
                // cdx.setTime(cd.getTime());
                // cdx.add(cdx.MINUTE, 5);
                // alarmResultTime = DateTimeHelper.formatDateTimetoString(
                // cdx.getTime(), DateTimeHelper.FMT_yyyyMMddHHmmss);
                // }

                noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        alarmResultTime);

                noticeMap
                        .put(LocateAllNoticeTable.alarmClockTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmClockTime)));

                noticeMap
                        .put(LocateAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.beforTime)));

                noticeMap.put(LocateAllNoticeTable.alarmType, "10");
                noticeMap
                        .put(LocateAllNoticeTable.isAlarmClock,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock)));
                noticeMap
                        .put(LocateAllNoticeTable.displayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.postpone)));

                dataList.add(noticeMap);

            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每天全天问候
        // 查询是否有待办
        String alldaysql = "select * from LocateAllNoticeTable where displayAlarm=0 and isEnd=0 and postpone=1 and repID=0 and alarmClockTime!='' "
                + " order by alarmResultTime asc";
        String alltime = sp.getString(mContextApplication, ShareFile.USERFILE,
                ShareFile.ALLTIME, "08:58");
        try {
            Cursor cursor = sqldb.rawQuery(alldaysql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                noticeMap = new HashMap<String, String>();

                noticeMap.put(LocateAllNoticeTable.alarmId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.alarmId)));

                noticeMap
                        .put(LocateAllNoticeTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.noticeContent)));
                noticeMap
                        .put(LocateAllNoticeTable.alarmType,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmType)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));
                noticeMap
                        .put(LocateAllNoticeTable.stateone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.stateone)));
                noticeMap
                        .put(LocateAllNoticeTable.statetwo,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.statetwo)));
                noticeMap.put(LocateAllNoticeTable.dateone, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.dateone)));
                noticeMap.put(LocateAllNoticeTable.datetwo, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.datetwo)));

                noticeMap.put(LocateAllNoticeTable.alarmSound, "g_207");
                String postpone = cursor.getString(cursor
                        .getColumnIndex(LocateAllNoticeTable.postpone));
                // String mytime = sp.getString(mContextApplication,
                // ShareFile.USERFILE,
                // ShareFile.ALLTIME, "08:58");
                Calendar cd = Calendar.getInstance();
                cd.setTime(DateUtil.parseDateTime(DateUtil
                        .formatDate(new Date()) + " " + alltime));
                String alarmResultTime = DateTimeHelper.formatDateTimetoString(
                        cd.getTime(), DateTimeHelper.FMT_yyyyMMddHHmmss);
                if (DateUtil.parseDateTimeSs(
                        DateUtil.formatDateTimeSs(cd.getTime())).getTime() > DateUtil
                        .parseDateTimeSs(currentTimeSs).getTime()) {

                } else {
                    Calendar cdx = Calendar.getInstance();
                    cdx.setTime(cd.getTime());
                    cdx.add(cdx.DATE, 1);
                    alarmResultTime = DateTimeHelper.formatDateTimetoString(
                            cdx.getTime(), DateTimeHelper.FMT_yyyyMMddHHmmss);
                }

                noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                        alarmResultTime);

                noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                        alarmResultTime);

                noticeMap
                        .put(LocateAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.beforTime)));
                noticeMap
                        .put(LocateAllNoticeTable.isAlarmClock,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.isAlarmClock)));
                noticeMap
                        .put(LocateAllNoticeTable.displayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.postpone)));

                dataList.add(noticeMap);
            } else {
                String alltimesql = "select * from LocateAllNoticeTable where displayAlarm=0 and isEnd=0 and postpone=0 and repID=0 and alarmClockTime!='' and alarmResultTime > '"
                        + DateUtil.formatDateTimeSs(new Date())
                        + "' order by alarmResultTime asc";
                Cursor alltimecursor = sqldb.rawQuery(alltimesql, null);
                if (null != alltimecursor && alltimecursor.getCount() > 0) {
                    alltimecursor.moveToFirst();
                    noticeMap = new HashMap<String, String>();

                    noticeMap
                            .put(LocateAllNoticeTable.alarmId,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.alarmId)));

                    noticeMap
                            .put(LocateAllNoticeTable.noticeContent,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.noticeContent)));
                    noticeMap
                            .put(LocateAllNoticeTable.alarmType,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.alarmType)));

                    noticeMap
                            .put(LocateAllNoticeTable.alarmSoundDesc,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));
                    noticeMap
                            .put(LocateAllNoticeTable.stateone,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.stateone)));
                    noticeMap
                            .put(LocateAllNoticeTable.statetwo,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.statetwo)));
                    noticeMap
                            .put(LocateAllNoticeTable.dateone,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.dateone)));
                    noticeMap
                            .put(LocateAllNoticeTable.datetwo,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.datetwo)));

                    noticeMap.put(LocateAllNoticeTable.alarmSound, "g_207");
                    String postpone = alltimecursor.getString(alltimecursor
                            .getColumnIndex(LocateAllNoticeTable.postpone));
                    String alarmResultTime = alltimecursor
                            .getString(alltimecursor
                                    .getColumnIndex(LocateAllNoticeTable.alarmResultTime));
                    if (alarmResultTime.length() == 16) {
                        alarmResultTime = DateUtil.formatDateTimeSs(DateUtil
                                .parseDateTime(alarmResultTime));
                    }
                    noticeMap.put(LocateAllNoticeTable.alarmResultTime,
                            alarmResultTime);

                    noticeMap.put(LocateAllNoticeTable.alarmClockTime,
                            alarmResultTime);

                    noticeMap
                            .put(LocateAllNoticeTable.beforTime,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.beforTime)));
                    noticeMap
                            .put(LocateAllNoticeTable.isAlarmClock,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.isAlarmClock)));
                    noticeMap
                            .put(LocateAllNoticeTable.displayAlarm,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.displayAlarm)));
                    noticeMap
                            .put(LocateAllNoticeTable.postpone,
                                    alltimecursor.getString(alltimecursor
                                            .getColumnIndex(LocateAllNoticeTable.postpone)));

                    dataList.add(noticeMap);
                }
            }
            cursor.close();

        } catch (Exception e) {
            // try {
            // File temp = new File(Environment.getExternalStorageDirectory()
            // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
            // if (!temp.exists()) {
            // temp.mkdir();
            // }
            // FileWriter fw = new FileWriter(temp.getAbsolutePath()
            // + "/dataerror.txt", true);
            // if (fw != null) {
            // fw.flush();
            // fw.write(DateUtil.formatDateTimeSs(new Date()));
            // fw.write("  ," + e);
            // fw.close();
            // }
            // } catch (Exception e1) {
            // e1.printStackTrace();
            // }
            e.printStackTrace();
        }

        try {
            // int xiangtongcount = 0;
            // final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            // "yyyy-MM-dd HH:mm:ss");
            // String resulttime = "";
            // String resulttime1 = "";
            // 闹钟排序
            // for (int i = 0; i < dataList.size(); i++) {
            // for (int j = i + 1; j < dataList.size(); j++) {
            // if (dataList.get(i)
            // .get(LocateAllNoticeTable.alarmResultTime).length() == 16) {
            // resulttime = dateFormat.format(sdf.parse(dataList
            // .get(i).get(
            // LocateAllNoticeTable.alarmResultTime)));
            // } else {
            // resulttime = dataList.get(i).get(
            // LocateAllNoticeTable.alarmResultTime);
            // }
            // if (dataList.get(j)
            // .get(LocateAllNoticeTable.alarmResultTime).length() == 16) {
            // resulttime1 = dateFormat.format(sdf.parse(dataList.get(
            // j).get(LocateAllNoticeTable.alarmResultTime)));
            // } else {
            // resulttime1 = dataList.get(j).get(
            // LocateAllNoticeTable.alarmResultTime);
            // }
            //
            // if (DateUtil.parseDateTimeSs(resulttime).getTime() == DateUtil
            // .parseDateTimeSs(resulttime1).getTime()) {
            // xiangtongcount++;
            // dataList.get(j)
            // .put(LocateAllNoticeTable.alarmResultTime,
            // simpleDateFormat
            // .format(sdf
            // .parse(dataList
            // .get(j)
            // .get(LocateAllNoticeTable.alarmResultTime))
            // .getTime()
            // + xiangtongcount
            // * 12
            // * 1000));
            // } else if (DateUtil.parseDateTimeSs(resulttime).getTime() >
            // DateUtil
            // .parseDateTimeSs(resulttime1).getTime()) {
            //
            // } else {
            //
            // }
            // }
            // }
            Collections.sort(dataList, new Comparator<Map<String, String>>() {
                public int compare(Map<String, String> o1,
                                   Map<String, String> o2) {
                    if (DateUtil.parseDateTimeSs(
                            o1.get(LocateAllNoticeTable.alarmResultTime))
                            .getTime() > DateUtil.parseDateTimeSs(
                            o2.get(LocateAllNoticeTable.alarmResultTime))
                            .getTime()) {
                        return 1;
                    } else if ((DateUtil.parseDateTimeSs(
                            o1.get(LocateAllNoticeTable.alarmResultTime))
                            .getTime() == DateUtil.parseDateTimeSs(
                            o2.get(LocateAllNoticeTable.alarmResultTime))
                            .getTime())) {
                        // try {
                        // for(int i=0;i<xiangtongcount;i++){
                        //
                        // }
                        // o1.put(LocateAllNoticeTable.alarmResultTime,
                        // simpleDateFormat.format(sdf
                        // .parse(o1
                        // .get(LocateAllNoticeTable.alarmResultTime))
                        // .getTime() + 10 * 1000));
                        // o2.put(LocateAllNoticeTable.alarmResultTime,
                        // simpleDateFormat.format(simpleDateFormat.parse(o2
                        // .get(LocateAllNoticeTable.alarmResultTime))));
                        // return 0;
                        // } catch (ParseException e) {
                        // e.printStackTrace();
                        // }
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        } catch (Exception e) {
            // try {
            // File temp = new File(Environment.getExternalStorageDirectory()
            // .getPath() + "/YourAppFolder/");// 自已缓存文件夹
            // if (!temp.exists()) {
            // temp.mkdir();
            // }
            // FileWriter fw = new FileWriter(temp.getAbsolutePath()
            // + "/dataerror.txt", true);
            // if (fw != null) {
            // fw.flush();
            // fw.write(DateUtil.formatDateTimeSs(new Date()));
            // fw.write("  ," + e);
            // fw.close();
            // }
            // } catch (Exception e1) {
            // e1.printStackTrace();
            // }
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 根据alarmid查询对应的日程id和重复id
     */
    public Map<String, String> getqueryschrepID(int alarmID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> noticeMap = new HashMap<String, String>();

        String sql = "select * from LocateAllNoticeTable where alarmId = "
                + alarmID;

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                noticeMap.put(LocateAllNoticeTable.alarmId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.alarmId)));
                noticeMap
                        .put(LocateAllNoticeTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.noticeContent)));
                noticeMap
                        .put(LocateAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSoundDesc)));
                noticeMap
                        .put(LocateAllNoticeTable.alarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmSound)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmResultTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmResultTime)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmClockTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmClockTime)));

                noticeMap
                        .put(LocateAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.beforTime)));

                noticeMap
                        .put(LocateAllNoticeTable.alarmType,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.alarmType)));
                noticeMap.put(LocateAllNoticeTable.schID, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.schID)));
                noticeMap.put(LocateAllNoticeTable.repID, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.repID)));
                noticeMap.put(LocateAllNoticeTable.isEnd, cursor
                        .getString(cursor
                                .getColumnIndex(LocateAllNoticeTable.isEnd)));
                noticeMap
                        .put(LocateAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllNoticeTable.postpone)));
                return noticeMap;

            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return noticeMap;
    }

    /**
     * 清空闹钟表的所有数据
     */
    public void deleteclockData() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + LocateAllNoticeTable.LocateAllNoticeTable
                + " where " + LocateAllNoticeTable.alarmId + " >= " + 0;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /******************************** 数据迁移 ****************************************************/
    /**
     * 删除日程表
     *
     * @return
     */
    public void deleteSchData() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        // int id = Integer.parseInt(schId);

        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schID + " <= " + 0 + " and "
                + ScheduleTable.schAID + " = " + 0;
        // String sql2 = "update " + ScheduleTable.ScheduleTable + " set "
        // + ScheduleTable.schUpdateState + " = " + 3 + " where "
        // + ScheduleTable.schID + " = " + id + " and "
        // + ScheduleTable.schUpdateState + " != " + 3;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSchData1() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        // int id = Integer.parseInt(schId);

        // String sql1 = "delete from " + ScheduleTable.ScheduleTable +
        // " where "
        // + ScheduleTable.schID + " < " + id + " and "
        // + ScheduleTable.schAID + " = " + 0 ;
        String sql = "update " + ScheduleTable.ScheduleTable + " set "
                + ScheduleTable.schUpdateState + " = " + 3 + " where "
                + ScheduleTable.schID + " > " + 0;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空重复表
     *
     * @return
     */
    public void deleteRepData() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        // int id = Integer.parseInt(repId);
        // String sql;
        // if (id < 0) {
        String sql = "delete from " + CLRepeatTable.CLRepeatTable + " where "
                + CLRepeatTable.repID + " < " + 0;
        // } else {
        // sql = "update " + CLRepeatTable.CLRepeatTable + " set "
        // + CLRepeatTable.repUpdateState + " = " + 3 + " where "
        // + CLRepeatTable.repID + " = " + id + " and "
        // + CLRepeatTable.repUpdateState + " != " + 3;
        // }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteRepData1() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        // int id = Integer.parseInt(repId);
        // String sql;
        // if (id < 0) {
        // sql = "delete from " + CLRepeatTable.CLRepeatTable + " where "
        // + CLRepeatTable.repID + " = " + id;
        // } else {
        String sql = "update " + CLRepeatTable.CLRepeatTable + " set "
                + CLRepeatTable.repUpdateState + " = " + 3 + " where "
                + CLRepeatTable.repID + " > " + 0 + " and "
                + CLRepeatTable.repUpdateState + " != " + 3;
        // }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> QueryOldSchUpdate() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.mission.schedule/databases/plan", null);
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from LocateAllNoticeTable"
                + " where tpId=0 and locateUpdateState!=3";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap
                        .put(LocateOldAllNoticeTable.alarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.alarmSound)));
                noticeMap
                        .put(LocateOldAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.alarmSoundDesc)));
                noticeMap
                        .put(LocateOldAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.beforTime)));
                noticeMap
                        .put(LocateOldAllNoticeTable.colorType,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.colorType)));
                noticeMap
                        .put(LocateOldAllNoticeTable.createTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.createTime)));
                noticeMap
                        .put(LocateOldAllNoticeTable.displayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateOldAllNoticeTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.noticeContent)));
                noticeMap
                        .put(LocateOldAllNoticeTable.noticeDate,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.noticeDate)));
                noticeMap
                        .put(LocateOldAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.postpone)));
                noticeMap.put(LocateOldAllNoticeTable.tpId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateOldAllNoticeTable.tpId)));
                noticeMap
                        .put(LocateOldAllNoticeTable.teamNoticeReadState,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.teamNoticeReadState)));
                noticeMap
                        .put(LocateOldAllNoticeTable.noticeIsStarred,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.noticeIsStarred)));
                noticeMap
                        .put(LocateOldAllNoticeTable.alarmClockTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.alarmClockTime)));
                dataList.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<Map<String, String>> QueryRepeatData() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.mission.schedule/databases/plan", null);
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from LocateRepeatNoticeTable where locateUpdateState!=3";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpAlarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpAlarmSound)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpAlarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpAlarmSoundDesc)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpBeforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpBeforTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpContent)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpCreateTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpCreateTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDataType,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDataType)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDate,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDate)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDisplayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDisplayAlarm)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.locateNextCreatTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.locateNextCreatTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpCurWeek,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpCurWeek)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDay,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDay)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpMonthDay,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpMonthDay)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpLcDate,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpLcDate)));
                dataList.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<Map<String, String>> QueryYestodayData() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.mission.schedule/databases/plan", null);
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from LocateAllMemoTable where locateUpdateState!=3";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap
                        .put(LocateAllMemoTable.createTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllMemoTable.createTime)));
                noticeMap
                        .put(LocateAllMemoTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllMemoTable.noticeContent)));
                dataList.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<Map<String, String>> QueryOldSchUpdate1() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Environment
                .getExternalStorageDirectory().getPath()
                + "/YourAppDataFolder/plan", null);
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from LocateAllNoticeTable"
                + " where tpId=0 and locateUpdateState!=3";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap
                        .put(LocateOldAllNoticeTable.alarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.alarmSound)));
                noticeMap
                        .put(LocateOldAllNoticeTable.alarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.alarmSoundDesc)));
                noticeMap
                        .put(LocateOldAllNoticeTable.beforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.beforTime)));
                noticeMap
                        .put(LocateOldAllNoticeTable.colorType,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.colorType)));
                noticeMap
                        .put(LocateOldAllNoticeTable.createTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.createTime)));
                noticeMap
                        .put(LocateOldAllNoticeTable.displayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.displayAlarm)));
                noticeMap
                        .put(LocateOldAllNoticeTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.noticeContent)));
                noticeMap
                        .put(LocateOldAllNoticeTable.noticeDate,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.noticeDate)));
                noticeMap
                        .put(LocateOldAllNoticeTable.postpone,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.postpone)));
                noticeMap.put(LocateOldAllNoticeTable.tpId, cursor
                        .getString(cursor
                                .getColumnIndex(LocateOldAllNoticeTable.tpId)));
                noticeMap
                        .put(LocateOldAllNoticeTable.teamNoticeReadState,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.teamNoticeReadState)));
                noticeMap
                        .put(LocateOldAllNoticeTable.noticeIsStarred,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.noticeIsStarred)));
                noticeMap
                        .put(LocateOldAllNoticeTable.alarmClockTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateOldAllNoticeTable.alarmClockTime)));
                dataList.add(noticeMap);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<Map<String, String>> QueryRepeatData1() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Environment
                .getExternalStorageDirectory().getPath()
                + "/YourAppDataFolder/plan", null);
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from LocateRepeatNoticeTable where locateUpdateState!=3";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpAlarmSound,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpAlarmSound)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpAlarmSoundDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpAlarmSoundDesc)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpBeforTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpBeforTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpContent)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpCreateTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpCreateTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDataType,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDataType)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDate,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDate)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDisplayAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDisplayAlarm)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.locateNextCreatTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.locateNextCreatTime)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpCurWeek,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpCurWeek)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpDay,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpDay)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpMonthDay,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpMonthDay)));
                noticeMap
                        .put(LocateRepeatNoticeTable.key_tpLcDate,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateRepeatNoticeTable.key_tpLcDate)));
                dataList.add(noticeMap);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<Map<String, String>> QueryYestodayData1() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Environment
                .getExternalStorageDirectory().getPath()
                + "/YourAppDataFolder/plan", null);
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "select * from LocateAllMemoTable where locateUpdateState!=3";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap
                        .put(LocateAllMemoTable.createTime,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllMemoTable.createTime)));
                noticeMap
                        .put(LocateAllMemoTable.noticeContent,
                                cursor.getString(cursor
                                        .getColumnIndex(LocateAllMemoTable.noticeContent)));
                dataList.add(noticeMap);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public boolean insertYestodayData(String schContent, String schTime,
                                      String schCreateTime) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) - 1);
        String yestoday = DateUtil.formatDate(calendar.getTime());
        int ID = getLocalId(1, "ScheduleTable", ScheduleTable.ID);
        schID = getLocalId(-1, "ScheduleTable", ScheduleTable.schID);
        String sql = "insert into ScheduleTable(ID,schID,schContent,schDate,schTime,"
                + "schIsAlarm, schBeforeTime,schDisplayTime, schIsPostpone,schIsImportant,"
                + "schColorType, schIsEnd, schCreateTime,schTags, schSourceType,schSourceDesc,"
                + "schSourceDescSpare, schRepeatID, schRepeatDate,schUpdateTime, schUpdateState,"
                + " schOpenState,schRepeatLink, schRingDesc, schRingCode,schcRecommendName, "
                + "schRead, schAID, schAType,schWebURL, schImagePath, schFocusState,schFriendID,"
                + " schcRecommendId) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{ID, schID,
                StringUtils.getIsStringEqulesNull(schContent), yestoday,
                StringUtils.getIsStringEqulesNull(schTime), 1, 0, 1, 0, 0, 0,
                0, StringUtils.getIsStringEqulesNull(schCreateTime), "", 0, "",
                "", 0, "", StringUtils.getIsStringEqulesNull(schCreateTime), 1,
                0, 0, "完成任务", "g_88", "", 0, 0, 0, "", "", 0, 0, 0};
        // String sql = "insert into ScheduleTable values(" + id + " , " + schID
        // + ",'" + schContent + "','" + yestoday + "','" + schTime + "',"
        // + 1 + "," + 0 + "," + 1 + "," + 0 + "," + 0 + "," + 0 + "," + 0
        // + ",'" + schCreateTime + "','" + "" + "'," + 0 + ",'" + ""
        // + "','" + "" + "'," + 0 + ",'" + "" + "','" + schCreateTime
        // + "'," + 1 + "," + 0 + "," + 0 + ",'" + "完成任务" + "','" + "g_88"
        // + "','" + "" + "'," + 0 + " , " + 0 + " , " + 0 + " , '" + ""
        // + "', '" + "" + "'," + 0 + "," + 0 + "," + 0 + ")";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*****************************************
     * 新版好友消息新表
     ************************************************/
    // 插入发送的消息
    public boolean insertMessageSendData(Integer nfmSendId, Integer nfmGetId,
                                         Integer nfmCalendarId, Integer nfmOpenState, Integer nfmStatus,
                                         Integer nfmIsAlarm, Integer nfmPostpone, Integer nfmColorType,
                                         Integer nfmDisplayTime, Integer nfmBeforeTime,
                                         Integer nfmSourceType, Integer nfmType, Integer nfmAType,
                                         Integer nfmInSTable, Integer nfmIsEnd, Integer nfmDownState,
                                         Integer nfmPostState, String nfmParameter, String nfmContent,
                                         String nfmDate, String nfmTime, String nfmSourceDesc,
                                         String nfmSourceDescSpare, String nfmTags, String nfmRingDesc,
                                         String nfmRingCode, String nfmStartDate,
                                         String nfmInitialCreatedTime, String nfmLastCreatedTime,
                                         String nfmNextCreatedTime, String nfmWebURL, String nfmImagePath,
                                         String nfmSendName, String nfmRemark, Integer nfmUpdateState,
                                         Integer nfmPId, Integer nfmSubState, String nfmSubDate,
                                         Integer nfmCState, Integer nfmSubEnd, String nfmSubEndDate,
                                         Integer nfmIsPuase, String parReamrk, String nfmCreateTime) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        nfmId = getLocalId(-1, "CLNFMessage", CLNFMessage.nfmId);
        String sql = "insert into CLNFMessage(nfmId, nfmSendId, nfmGetId,nfmCalendarId, "
                + "nfmOpenState, nfmStatus,nfmIsAlarm, nfmPostpone,nfmColorType,nfmDisplayTime,"
                + " nfmBeforeTime,nfmSourceType,nfmType,  nfmAType,nfmInSTable, "
                + "nfmIsEnd,nfmDownState,nfmPostState,nfmParameter,nfmContent,nfmDate, "
                + "nfmTime, nfmSourceDesc,nfmSourceDescSpare, nfmTags,nfmRingDesc,nfmRingCode,"
                + " nfmStartDate,nfmInitialCreatedTime, nfmLastCreatedTime,nfmNextCreatedTime,"
                + "nfmWebURL, nfmImagePath,nfmSendName, nfmRemark, nfmUpdateState,nfmPId, "
                + "nfmSubState, nfmSubDate,nfmCState, nfmSubEnd, nfmSubEndDate,nfmIsPuase,"
                + "parReamrk,nfmCreateTime) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{nfmId, nfmSendId, nfmGetId,
                nfmCalendarId, nfmOpenState, nfmStatus, nfmIsAlarm,
                nfmPostpone, nfmColorType, nfmDisplayTime, nfmBeforeTime,
                nfmSourceType, nfmType, nfmAType, nfmInSTable, nfmIsEnd,
                nfmDownState, nfmPostState,
                StringUtils.getIsStringEqulesNull(nfmParameter),
                StringUtils.getIsStringEqulesNull(nfmContent),
                StringUtils.getIsStringEqulesNull(nfmDate),
                StringUtils.getIsStringEqulesNull(nfmTime),
                StringUtils.getIsStringEqulesNull(nfmSourceDesc),
                StringUtils.getIsStringEqulesNull(nfmSourceDescSpare),
                StringUtils.getIsStringEqulesNull(nfmTags),
                StringUtils.getIsStringEqulesNull(nfmRingDesc),
                StringUtils.getIsStringEqulesNull(nfmRingCode),
                StringUtils.getIsStringEqulesNull(nfmStartDate),
                StringUtils.getIsStringEqulesNull(nfmInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmLastCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmNextCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmWebURL),
                StringUtils.getIsStringEqulesNull(nfmImagePath),
                StringUtils.getIsStringEqulesNull(nfmSendName),
                StringUtils.getIsStringEqulesNull(nfmRemark), nfmUpdateState,
                nfmPId, nfmSubState,
                StringUtils.getIsStringEqulesNull(nfmSubDate), nfmCState,
                nfmSubEnd, StringUtils.getIsStringEqulesNull(nfmSubEndDate),
                nfmIsPuase, StringUtils.getIsStringEqulesNull(parReamrk),
                StringUtils.getIsStringEqulesNull(nfmCreateTime)};
        // String sql = "insert into CLNFMessage values(" + nfmId + ","
        // + nfmSendId + "," + nfmGetId + "," + nfmCalendarId + ","
        // + nfmOpenState + "," + nfmStatus + "," + nfmIsAlarm + ","
        // + nfmPostpone + "," + nfmColorType + "," + nfmDisplayTime + ","
        // + nfmBeforeTime + "," + nfmSourceType + "," + nfmType + ","
        // + nfmAType + "," + nfmInSTable + "," + nfmIsEnd + ","
        // + nfmDownState + "," + nfmPostState + ",'" + nfmParameter
        // + "','" + nfmContent + "','" + nfmDate + "','" + nfmTime
        // + "','" + nfmSourceDesc + "','" + nfmSourceDescSpare + "','"
        // + nfmTags + "' , '" + nfmRingDesc + "','" + nfmRingCode + "','"
        // + nfmStartDate + "','" + nfmInitialCreatedTime + "','"
        // + nfmLastCreatedTime + "','" + nfmNextCreatedTime + "','"
        // + nfmWebURL + "','" + nfmImagePath + "','" + nfmSendName
        // + "','" + nfmRemark + "'," + nfmUpdateState + "," + nfmPId
        // + "," + nfmSubState + ",'" + nfmSubDate + "'," + nfmCState
        // + "," + nfmSubEnd + ",'" + nfmSubEndDate + "', " + nfmIsPuase
        // + ",'" + parReamrk + "' , '" + nfmCreateTime + "')";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从网上下载下来的数据
     *
     * @return
     */
    public boolean insertIntnetMessageSendData(Integer nfmId,
                                               Integer nfmSendId, Integer nfmGetId, Integer nfmCalendarId,
                                               Integer nfmOpenState, Integer nfmStatus, Integer nfmIsAlarm,
                                               Integer nfmPostpone, Integer nfmColorType, Integer nfmDisplayTime,
                                               Integer nfmBeforeTime, Integer nfmSourceType, Integer nfmType,
                                               Integer nfmAType, Integer nfmInSTable, Integer nfmIsEnd,
                                               Integer nfmDownState, Integer nfmPostState, String nfmParameter,
                                               String nfmContent, String nfmDate, String nfmTime,
                                               String nfmSourceDesc, String nfmSourceDescSpare, String nfmTags,
                                               String nfmRingDesc, String nfmRingCode, String nfmStartDate,
                                               String nfmInitialCreatedTime, String nfmLastCreatedTime,
                                               String nfmNextCreatedTime, String nfmWebURL, String nfmImagePath,
                                               String nfmSendName, String nfmRemark, Integer nfmUpdateState,
                                               Integer nfmPId, Integer nfmSubState, String nfmSubDate,
                                               Integer nfmCState, Integer nfmSubEnd, String nfmSubEndDate,
                                               Integer nfmIsPuase, String parReamrk, String nfmCreateTime) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "insert into CLNFMessage(nfmId, nfmSendId, nfmGetId,nfmCalendarId, "
                + "nfmOpenState, nfmStatus,nfmIsAlarm, nfmPostpone,nfmColorType,nfmDisplayTime,"
                + " nfmBeforeTime,nfmSourceType,nfmType,  nfmAType,nfmInSTable, "
                + "nfmIsEnd,nfmDownState,nfmPostState,nfmParameter,nfmContent,nfmDate, "
                + "nfmTime, nfmSourceDesc,nfmSourceDescSpare, nfmTags,nfmRingDesc,nfmRingCode,"
                + " nfmStartDate,nfmInitialCreatedTime, nfmLastCreatedTime,nfmNextCreatedTime,"
                + "nfmWebURL, nfmImagePath,nfmSendName, nfmRemark, nfmUpdateState,nfmPId, "
                + "nfmSubState, nfmSubDate,nfmCState, nfmSubEnd, nfmSubEndDate,nfmIsPuase,"
                + "parReamrk,nfmCreateTime) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{nfmId, nfmSendId, nfmGetId,
                nfmCalendarId, nfmOpenState, nfmStatus, nfmIsAlarm,
                nfmPostpone, nfmColorType, nfmDisplayTime, nfmBeforeTime,
                nfmSourceType, nfmType, nfmAType, nfmInSTable, nfmIsEnd,
                nfmDownState, nfmPostState,
                StringUtils.getIsStringEqulesNull(nfmParameter),
                StringUtils.getIsStringEqulesNull(nfmContent),
                StringUtils.getIsStringEqulesNull(nfmDate),
                StringUtils.getIsStringEqulesNull(nfmTime),
                StringUtils.getIsStringEqulesNull(nfmSourceDesc),
                StringUtils.getIsStringEqulesNull(nfmSourceDescSpare),
                StringUtils.getIsStringEqulesNull(nfmTags),
                StringUtils.getIsStringEqulesNull(nfmRingDesc),
                StringUtils.getIsStringEqulesNull(nfmRingCode),
                StringUtils.getIsStringEqulesNull(nfmStartDate),
                StringUtils.getIsStringEqulesNull(nfmInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmLastCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmNextCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmWebURL),
                StringUtils.getIsStringEqulesNull(nfmImagePath),
                StringUtils.getIsStringEqulesNull(nfmSendName),
                StringUtils.getIsStringEqulesNull(nfmRemark), nfmUpdateState,
                nfmPId, nfmSubState,
                StringUtils.getIsStringEqulesNull(nfmSubDate), nfmCState,
                nfmSubEnd, StringUtils.getIsStringEqulesNull(nfmSubEndDate),
                nfmIsPuase, StringUtils.getIsStringEqulesNull(parReamrk),
                StringUtils.getIsStringEqulesNull(nfmCreateTime)};
        // String sql = "insert into CLNFMessage values(" + nfmId + ","
        // + nfmSendId + "," + nfmGetId + "," + nfmCalendarId + ","
        // + nfmOpenState + "," + nfmStatus + "," + nfmIsAlarm + ","
        // + nfmPostpone + "," + nfmColorType + "," + nfmDisplayTime + ","
        // + nfmBeforeTime + "," + nfmSourceType + "," + nfmType + ","
        // + nfmAType + "," + nfmInSTable + "," + nfmIsEnd + ","
        // + nfmDownState + "," + nfmPostState + ",'" + nfmParameter
        // + "','" + nfmContent + "','" + nfmDate + "','" + nfmTime
        // + "','" + nfmSourceDesc + "','" + nfmSourceDescSpare + "','"
        // + nfmTags + "' , '" + nfmRingDesc + "','" + nfmRingCode + "','"
        // + nfmStartDate + "','" + nfmInitialCreatedTime + "','"
        // + nfmLastCreatedTime + "','" + nfmNextCreatedTime + "','"
        // + nfmWebURL + "','" + nfmImagePath + "','" + nfmSendName
        // + "','" + nfmRemark + "'," + nfmUpdateState + "," + nfmPId
        // + "," + nfmSubState + ",'" + nfmSubDate + "'," + nfmCState
        // + "," + nfmSubEnd + ",'" + nfmSubEndDate + "', " + nfmIsPuase
        // + ",'" + parReamrk + "' , '" + nfmCreateTime + "')";
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, String>> queryAllLocalFriendsData(int timeType,
                                                              int firendID) throws Exception {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String sql = "";
        String yestoday;// 昨天
        String today;// 今天
        String tomorrow;// 明天
        String inweek;// 一周以内
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) - 1);
        yestoday = DateUtil.formatDate(calendar.getTime());
        calendar.setTime(new Date());
        today = DateUtil.formatDate(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow = DateUtil.formatDate(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 7);
        inweek = DateUtil.formatDate(calendar.getTime());

        switch (timeType) {
            case -1:// 查询所有nfmupdate为1 新建的消息进行同步上传
                // sql = "select * from " + CLNFMessage.CLNFMessage + " where "
                // + CLNFMessage.nfmUpdateState + " = " + 1 + " and ("
                // + CLNFMessage.nfmPId + " = " + 0 + " or "
                // + CLNFMessage.nfmSubState + " = " + 1 + " or "
                // + CLNFMessage.nfmSubState + " = " + 2
                // + ") order by nfmDate desc,nfmDisplayTime asc,nfmTime asc";
                sql = "select * from " + CLNFMessage.CLNFMessage + " where "
                        + CLNFMessage.nfmUpdateState + " = " + 1
                        + " order by nfmDate desc,nfmDisplayTime asc,nfmTime asc";
                break;
            case -2:// 查询所有nfmupdate为2 修改的消息进行同步上传
                // sql = "select * from " + CLNFMessage.CLNFMessage + " where "
                // + CLNFMessage.nfmUpdateState + " = " + 2 + " and ("
                // + CLNFMessage.nfmPId + " = " + 0 + " or "
                // + CLNFMessage.nfmSubState + " = " + 1 + " or "
                // + CLNFMessage.nfmSubState + " = " + 2
                // + ") order by nfmDate desc,nfmDisplayTime asc,nfmTime asc";
                sql = "select * from " + CLNFMessage.CLNFMessage + " where "
                        + CLNFMessage.nfmUpdateState + " = " + 2
                        + " order by nfmDate desc,nfmDisplayTime asc,nfmTime asc";
                break;
            case -3:// 查询所有nfmupdate为3 删除的消息进行同步上传
                // sql = "select * from " + CLNFMessage.CLNFMessage + " where "
                // + CLNFMessage.nfmUpdateState + " = " + 3 + " and ("
                // + CLNFMessage.nfmPId + " = " + 0 + " or "
                // + CLNFMessage.nfmSubState + " = " + 1 + " or "
                // + CLNFMessage.nfmSubState + " = " + 2
                // + ") order by nfmDate desc,nfmDisplayTime asc,nfmTime asc";
                sql = "select * from " + CLNFMessage.CLNFMessage + " where "
                        + CLNFMessage.nfmUpdateState + " = " + 3
                        + " order by nfmDate desc,nfmDisplayTime asc,nfmTime asc";
                break;
            case 0:// 0 以前+未结束+顺延
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + "nfmDate<'"
                        + DateUtil.formatDate(new Date())
                        + "'and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmPostState
                        + " != "
                        + 1
                        + " and "
                        + CLNFMessage.nfmPostpone
                        + " = "
                        + 1
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate desc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;
            case 1:// 待办 今天以前+全天+未结束+自动顺延
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + "nfmDate=='"
                        + DateUtil.formatDate(new Date())
                        + "' and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmDisplayTime
                        + " != "
                        + 1
                        + " and "
                        + CLNFMessage.nfmPostpone
                        + " != "
                        + 0
                        + " and "
                        + CLNFMessage.nfmGetId
                        + " = "
                        + firendID
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate asc,nfmDisplayTime asc,nfmCreateTime desc,nfmTime desc";
                break;
            case 2:// 今天 + 不顺延 + 显示时间
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + "nfmDate =='"
                        + DateUtil.formatDate(new Date())
                        + "' and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " and "
                        + CLNFMessage.nfmGetId
                        + " = "
                        + firendID
                        + " and ("
                        + CLNFMessage.nfmDisplayTime
                        + " != "
                        + 0
                        + " or "
                        + CLNFMessage.nfmPostpone
                        + " != "
                        + 1
                        + ") order by nfmDate asc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;
            case 3:// 明天
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + "nfmDate ='"
                        + tomorrow
                        + "' and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmGetId
                        + " = "
                        + firendID
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate asc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;
            case 4:// 一周以内
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + "nfmDate <'"
                        + inweek
                        + "' and "
                        + " nfmDate >'"
                        + tomorrow
                        + "' and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmGetId
                        + " = "
                        + firendID
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate asc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;
            case 5:// 一周以外
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + "nfmDate >='"
                        + inweek
                        + "' and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmGetId
                        + " = "
                        + firendID
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate asc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;
            case 6:
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + "nfmDate =='"
                        + DateUtil.formatDate(new Date())
                        + "' and "
                        + CLNFMessage.nfmIsEnd
                        + " = "
                        + 0
                        + " and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate asc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;

            case 7:// 昨天
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + CLNFMessage.nfmDate
                        + " = '"
                        + yestoday
                        + "' and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmGetId
                        + " = "
                        + firendID
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate desc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;
            case 8:// 昨天以前
                sql = "select * from "
                        + CLNFMessage.CLNFMessage
                        + " where "
                        + CLNFMessage.nfmDate
                        + " < '"
                        + yestoday
                        + "' and "
                        + CLNFMessage.nfmUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLNFMessage.nfmGetId
                        + " = "
                        + firendID
                        + " and "
                        + CLNFMessage.nfmStatus
                        + " = "
                        + 1
                        + " order by nfmDate desc,nfmDisplayTime asc,nfmTime asc,nfmId asc";
                break;
            case 9:// 重复查询
                sql = "select * from " + CLNFMessage.CLNFMessage + " where "
                        + CLNFMessage.nfmUpdateState + " != " + 3 + " and "
                        + CLNFMessage.nfmStatus + " = " + 2 + " and "
                        + CLNFMessage.nfmGetId + " = " + firendID
                        + " order by nfmTime asc";
                break;
        }
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(CLNFMessage.nfmId, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmId)));
                noticeMap.put(CLNFMessage.nfmSendId, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmSendId)));
                noticeMap.put(CLNFMessage.nfmGetId, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmGetId)));
                noticeMap.put(CLNFMessage.nfmCalendarId, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmCalendarId)));
                noticeMap.put(CLNFMessage.nfmOpenState, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmOpenState)));
                noticeMap.put(CLNFMessage.nfmStatus, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmStatus)));
                noticeMap.put(CLNFMessage.nfmIsAlarm, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmIsAlarm)));
                noticeMap.put(CLNFMessage.nfmPostpone, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmPostpone)));
                noticeMap.put(CLNFMessage.nfmColorType, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmColorType)));
                noticeMap.put(CLNFMessage.nfmDisplayTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmDisplayTime)));
                noticeMap.put(CLNFMessage.nfmBeforeTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmBeforeTime)));
                noticeMap.put(CLNFMessage.nfmSourceType, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmSourceType)));
                noticeMap.put(CLNFMessage.nfmType, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmType)));
                noticeMap.put(CLNFMessage.nfmAType, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmAType)));
                noticeMap.put(CLNFMessage.nfmInSTable, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmInSTable)));
                noticeMap.put(CLNFMessage.nfmIsEnd, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmIsEnd)));
                noticeMap.put(CLNFMessage.nfmDownState, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmDownState)));
                noticeMap.put(CLNFMessage.nfmPostState, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmPostState)));
                noticeMap.put(CLNFMessage.nfmUpdateState, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmUpdateState)));
                noticeMap.put(CLNFMessage.nfmPId, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmPId)));
                noticeMap.put(CLNFMessage.nfmSubState, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmSubState)));
                noticeMap.put(CLNFMessage.nfmSubEnd, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmSubEnd)));
                noticeMap.put(CLNFMessage.nfmCState, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmCState)));
                noticeMap.put(CLNFMessage.nfmParameter, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmParameter)));
                noticeMap.put(CLNFMessage.nfmContent, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmContent)));
                noticeMap.put(CLNFMessage.nfmDate, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmDate)));
                noticeMap.put(CLNFMessage.nfmTime, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmTime)));
                noticeMap.put(CLNFMessage.nfmSourceDesc, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmSourceDesc)));
                noticeMap
                        .put(CLNFMessage.nfmSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(CLNFMessage.nfmSourceDescSpare)));
                noticeMap.put(CLNFMessage.nfmTags, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmTags)));
                noticeMap.put(CLNFMessage.nfmRingDesc, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmRingDesc)));
                noticeMap.put(CLNFMessage.nfmRingCode, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmRingCode)));
                noticeMap.put(CLNFMessage.nfmStartDate, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmStartDate)));
                noticeMap
                        .put(CLNFMessage.nfmInitialCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLNFMessage.nfmInitialCreatedTime)));
                noticeMap
                        .put(CLNFMessage.nfmLastCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLNFMessage.nfmLastCreatedTime)));
                noticeMap
                        .put(CLNFMessage.nfmNextCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLNFMessage.nfmNextCreatedTime)));
                noticeMap.put(CLNFMessage.nfmWebURL, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmWebURL)));
                noticeMap.put(CLNFMessage.nfmImagePath, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmImagePath)));
                noticeMap.put(CLNFMessage.nfmSendName, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmSendName)));
                noticeMap.put(CLNFMessage.nfmSubDate, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmSubDate)));
                noticeMap.put(CLNFMessage.nfmRemark, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmRemark)));
                noticeMap.put(CLNFMessage.nfmSubEndDate, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmSubEndDate)));
                noticeMap.put(CLNFMessage.nfmIsPuase, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.nfmIsPuase)));
                noticeMap.put(CLNFMessage.parReamrk, cursor.getString(cursor
                        .getColumnIndex(CLNFMessage.parReamrk)));
                noticeMap.put(CLNFMessage.nfmCreateTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLNFMessage.nfmCreateTime)));
                dataList.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 修改新好友消息信息
     */
    public void updateNewFriendsData(Integer oldID, Integer newID,
                                     Integer calendId, Integer updateState) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmId + " = " + newID + " , "
                + CLNFMessage.nfmCalendarId + " = " + calendId + " , "
                + CLNFMessage.nfmUpdateState + " = " + updateState + " where "
                + CLNFMessage.nfmId + " = " + oldID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改新好友重复消息子记事
     */
    public void updateNewFriendsChildData(Integer oldID, Integer newID,
                                          Integer calendId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmPId + " = " + newID + " , "
                + CLNFMessage.nfmCalendarId + " = " + calendId + " where "
                + CLNFMessage.nfmPId + " = " + oldID + " and ("
                + CLNFMessage.nfmSubState + " = " + 0 + " or "
                + CLNFMessage.nfmSubState + " = " + 3 + ")";
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改下载下来相同id新好友消息信息
     */
    public void alterNewFriendsData1(Integer nfmId, Integer nfmSendId,
                                     Integer nfmGetId, Integer nfmCalendarId, Integer nfmOpenState,
                                     Integer nfmStatus, Integer nfmIsAlarm, Integer nfmPostpone,
                                     Integer nfmColorType, Integer nfmDisplayTime,
                                     Integer nfmBeforeTime, Integer nfmSourceType, Integer nfmType,
                                     Integer nfmAType, Integer nfmInSTable, Integer nfmIsEnd,
                                     Integer nfmDownState, Integer nfmPostState, String nfmParameter,
                                     String nfmContent, String nfmDate, String nfmTime,
                                     String nfmSourceDesc, String nfmSourceDescSpare, String nfmTags,
                                     String nfmRingDesc, String nfmRingCode, String nfmStartDate,
                                     String nfmInitialCreatedTime, String nfmLastCreatedTime,
                                     String nfmNextCreatedTime, String nfmWebURL, String nfmImagePath,
                                     String nfmSendName, String nfmRemark, Integer nfmUpdateState,
                                     Integer nfmPId, Integer nfmSubState, String nfmSubDate,
                                     Integer nfmCState, Integer nfmSubEnd, String nfmSubEndDate,
                                     Integer nfmIsPuase, String parReamrk) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmId + " = ?" + " , " + CLNFMessage.nfmSendId
                + " = ?" + " , " + CLNFMessage.nfmGetId + " = ?" + " , "
                + CLNFMessage.nfmCalendarId + " = ?" + " , "
                + CLNFMessage.nfmOpenState + " = ?" + " , "
                + CLNFMessage.nfmStatus + " = ?" + " , "
                + CLNFMessage.nfmIsAlarm + " = ?" + " , "
                + CLNFMessage.nfmPostpone + " = ?" + " , "
                + CLNFMessage.nfmColorType + " = ?" + " , "
                + CLNFMessage.nfmDisplayTime + " = ?" + " , "
                + CLNFMessage.nfmBeforeTime + " = ?" + " , "
                + CLNFMessage.nfmSourceType + " = ?" + " , "
                + CLNFMessage.nfmType + " = ?" + " , " + CLNFMessage.nfmAType
                + " = ?" + " , " + CLNFMessage.nfmInSTable + " = ?" + " , "
                + CLNFMessage.nfmIsEnd + " = ?" + " , "
                + CLNFMessage.nfmDownState + " = ?" + " , "
                + CLNFMessage.nfmPostState + " = ?" + " , "
                + CLNFMessage.nfmParameter + " = ?" + ", "
                + CLNFMessage.nfmContent + " = ?" + " , " + CLNFMessage.nfmDate
                + " = ?" + " , " + CLNFMessage.nfmTime + " = ?" + " , "
                + CLNFMessage.nfmSourceDesc + " = ?" + ", "
                + CLNFMessage.nfmSourceDescSpare + " = ?" + ", "
                + CLNFMessage.nfmTags + " = ?" + " , "
                + CLNFMessage.nfmRingDesc + " = ?" + " , "
                + CLNFMessage.nfmRingCode + " = ?" + ", "
                + CLNFMessage.nfmStartDate + " = ?" + " , "
                + CLNFMessage.nfmInitialCreatedTime + " = ?" + ", "
                + CLNFMessage.nfmLastCreatedTime + " = ?" + ", "
                + CLNFMessage.nfmNextCreatedTime + " = ?" + ", "
                + CLNFMessage.nfmWebURL + " = ?" + ", "
                + CLNFMessage.nfmImagePath + " = ?" + ", "
                + CLNFMessage.nfmSendName + " = ?" + ", "
                + CLNFMessage.nfmRemark + " = ?" + ", "
                + CLNFMessage.nfmUpdateState + " = ?" + ", "
                + CLNFMessage.nfmPId + " = ?" + ", " + CLNFMessage.nfmSubState
                + " = ?" + " , " + CLNFMessage.nfmSubDate + " = ?" + ", "
                + CLNFMessage.nfmCState + " = ?" + ", " + CLNFMessage.nfmSubEnd
                + " = ?" + ", " + CLNFMessage.nfmSubEndDate + " = ?" + ", "
                + CLNFMessage.nfmIsPuase + " = ?" + ", "
                + CLNFMessage.parReamrk + " = ?" + " where "
                + CLNFMessage.nfmId + " = " + nfmId;
        Object[] mValue = new Object[]{nfmId, nfmSendId, nfmGetId,
                nfmCalendarId, nfmOpenState, nfmStatus, nfmIsAlarm,
                nfmPostpone, nfmColorType, nfmDisplayTime, nfmBeforeTime,
                nfmSourceType, nfmType, nfmAType, nfmInSTable, nfmIsEnd,
                nfmDownState, nfmPostState,
                StringUtils.getIsStringEqulesNull(nfmParameter),
                StringUtils.getIsStringEqulesNull(nfmContent),
                StringUtils.getIsStringEqulesNull(nfmDate),
                StringUtils.getIsStringEqulesNull(nfmTime),
                StringUtils.getIsStringEqulesNull(nfmSourceDesc),
                StringUtils.getIsStringEqulesNull(nfmSourceDescSpare),
                StringUtils.getIsStringEqulesNull(nfmTags),
                StringUtils.getIsStringEqulesNull(nfmRingDesc),
                StringUtils.getIsStringEqulesNull(nfmRingCode),
                StringUtils.getIsStringEqulesNull(nfmStartDate),
                StringUtils.getIsStringEqulesNull(nfmInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmLastCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmNextCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmWebURL),
                StringUtils.getIsStringEqulesNull(nfmImagePath),
                StringUtils.getIsStringEqulesNull(nfmSendName),
                StringUtils.getIsStringEqulesNull(nfmRemark), nfmUpdateState,
                nfmPId, nfmSubState,
                StringUtils.getIsStringEqulesNull(nfmSubDate), nfmCState,
                nfmSubEnd, StringUtils.getIsStringEqulesNull(nfmSubEndDate),
                nfmIsPuase, StringUtils.getIsStringEqulesNull(parReamrk)};
        // String sql = "update " + CLNFMessage.CLNFMessage + " set "
        // + CLNFMessage.nfmId + " = " + nfmId + " , "
        // + CLNFMessage.nfmSendId + " = " + nfmSendId + " , "
        // + CLNFMessage.nfmGetId + " = " + nfmGetId + " , "
        // + CLNFMessage.nfmCalendarId + " = " + nfmCalendarId + " , "
        // + CLNFMessage.nfmOpenState + " = " + nfmOpenState + " , "
        // + CLNFMessage.nfmStatus + " = " + nfmStatus + " , "
        // + CLNFMessage.nfmIsAlarm + " = " + nfmIsAlarm + " , "
        // + CLNFMessage.nfmPostpone + " = " + nfmPostpone + " , "
        // + CLNFMessage.nfmColorType + " = " + nfmColorType + " , "
        // + CLNFMessage.nfmDisplayTime + " = " + nfmDisplayTime + " , "
        // + CLNFMessage.nfmBeforeTime + " = " + nfmBeforeTime + " , "
        // + CLNFMessage.nfmSourceType + " = " + nfmSourceType + " , "
        // + CLNFMessage.nfmType + " = " + nfmType + " , "
        // + CLNFMessage.nfmAType + " = " + nfmAType + " , "
        // + CLNFMessage.nfmInSTable + " = " + nfmInSTable + " , "
        // + CLNFMessage.nfmIsEnd + " = " + nfmIsEnd + " , "
        // + CLNFMessage.nfmDownState + " = " + nfmDownState + " , "
        // + CLNFMessage.nfmPostState + " = " + nfmPostState + " , "
        // + CLNFMessage.nfmParameter + " = '" + nfmParameter + "' , "
        // + CLNFMessage.nfmContent + " = '" + nfmContent + "' , "
        // + CLNFMessage.nfmDate + " = '" + nfmDate + "' , "
        // + CLNFMessage.nfmTime + " = '" + nfmTime + "' , "
        // + CLNFMessage.nfmSourceDesc + " = '" + nfmSourceDesc + "' , "
        // + CLNFMessage.nfmSourceDescSpare + " = '" + nfmSourceDescSpare
        // + "' , " + CLNFMessage.nfmTags + " = '" + nfmTags + "' , "
        // + CLNFMessage.nfmRingDesc + " = '" + nfmRingDesc + "' , "
        // + CLNFMessage.nfmRingCode + " = '" + nfmRingCode + "' , "
        // + CLNFMessage.nfmStartDate + " = '" + nfmStartDate + "' , "
        // + CLNFMessage.nfmInitialCreatedTime + " = '"
        // + nfmInitialCreatedTime + "' , "
        // + CLNFMessage.nfmLastCreatedTime + " = '" + nfmLastCreatedTime
        // + "' , " + CLNFMessage.nfmNextCreatedTime + " = '"
        // + nfmNextCreatedTime + "' , " + CLNFMessage.nfmWebURL + " = '"
        // + nfmWebURL + "' , " + CLNFMessage.nfmImagePath + " = '"
        // + nfmImagePath + "' , " + CLNFMessage.nfmSendName + " = '"
        // + nfmSendName + "' , " + CLNFMessage.nfmRemark + " = '"
        // + nfmRemark + "' , " + CLNFMessage.nfmUpdateState + " = "
        // + nfmUpdateState + " , " + CLNFMessage.nfmPId + " = " + nfmPId
        // + " , " + CLNFMessage.nfmSubState + " = " + nfmSubState + " , "
        // + CLNFMessage.nfmSubDate + " = '" + nfmSubDate + "' , "
        // + CLNFMessage.nfmCState + " = " + nfmCState + " , "
        // + CLNFMessage.nfmSubEnd + " = " + nfmSubEnd + " , "
        // + CLNFMessage.nfmSubEndDate + " = '" + nfmSubEndDate + "' , "
        // + CLNFMessage.nfmIsPuase + " = " + nfmIsPuase + " , "
        // + CLNFMessage.parReamrk + " = '" + parReamrk + "' where "
        // + CLNFMessage.nfmId + " = " + nfmId;

        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alterNewFriendsData(Integer nfmId, Integer nfmSendId,
                                    Integer nfmGetId, Integer nfmCalendarId, Integer nfmOpenState,
                                    Integer nfmStatus, Integer nfmIsAlarm, Integer nfmPostpone,
                                    Integer nfmColorType, Integer nfmDisplayTime,
                                    Integer nfmBeforeTime, Integer nfmSourceType, Integer nfmType,
                                    Integer nfmAType, Integer nfmInSTable, Integer nfmIsEnd,
                                    Integer nfmDownState, Integer nfmPostState, String nfmParameter,
                                    String nfmContent, String nfmDate, String nfmTime,
                                    String nfmSourceDesc, String nfmSourceDescSpare, String nfmTags,
                                    String nfmRingDesc, String nfmRingCode, String nfmStartDate,
                                    String nfmInitialCreatedTime, String nfmLastCreatedTime,
                                    String nfmNextCreatedTime, String nfmWebURL, String nfmImagePath,
                                    String nfmSendName, String nfmRemark, Integer nfmUpdateState,
                                    Integer nfmPId, Integer nfmSubState, String nfmSubDate,
                                    Integer nfmCState, Integer nfmSubEnd, String nfmSubEndDate,
                                    Integer nfmIsPuase, String parReamrk, String nfmCreateTime) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmId + " = ?" + " , " + CLNFMessage.nfmSendId
                + " = ?" + " , " + CLNFMessage.nfmGetId + " = ?" + " , "
                + CLNFMessage.nfmCalendarId + " = ?" + " , "
                + CLNFMessage.nfmOpenState + " = ?" + " , "
                + CLNFMessage.nfmStatus + " = ?" + " , "
                + CLNFMessage.nfmIsAlarm + " = ?" + " , "
                + CLNFMessage.nfmPostpone + " = ?" + " , "
                + CLNFMessage.nfmColorType + " = ?" + " , "
                + CLNFMessage.nfmDisplayTime + " = ?" + " , "
                + CLNFMessage.nfmBeforeTime + " = ?" + " , "
                + CLNFMessage.nfmSourceType + " = ?" + " , "
                + CLNFMessage.nfmType + " = ?" + " , " + CLNFMessage.nfmAType
                + " = ?" + " , " + CLNFMessage.nfmInSTable + " = ?" + " , "
                + CLNFMessage.nfmIsEnd + " = ?" + " , "
                + CLNFMessage.nfmDownState + " = ?" + " , "
                + CLNFMessage.nfmPostState + " = ?" + " , "
                + CLNFMessage.nfmParameter + " = ?" + ", "
                + CLNFMessage.nfmContent + " = ?" + " , " + CLNFMessage.nfmDate
                + " = ?" + " , " + CLNFMessage.nfmTime + " = ?" + " , "
                + CLNFMessage.nfmSourceDesc + " = ?" + ", "
                + CLNFMessage.nfmSourceDescSpare + " = ?" + ", "
                + CLNFMessage.nfmTags + " = ?" + " , "
                + CLNFMessage.nfmRingDesc + " = ?" + " , "
                + CLNFMessage.nfmRingCode + " = ?" + ", "
                + CLNFMessage.nfmStartDate + " = ?" + " , "
                + CLNFMessage.nfmInitialCreatedTime + " = ?" + ", "
                + CLNFMessage.nfmLastCreatedTime + " = ?" + ", "
                + CLNFMessage.nfmNextCreatedTime + " = ?" + ", "
                + CLNFMessage.nfmWebURL + " = ?" + ", "
                + CLNFMessage.nfmImagePath + " = ?" + ", "
                + CLNFMessage.nfmSendName + " = ?" + ", "
                + CLNFMessage.nfmRemark + " = ?" + ", "
                + CLNFMessage.nfmUpdateState + " = ?" + ", "
                + CLNFMessage.nfmPId + " = ?" + ", " + CLNFMessage.nfmSubState
                + " = ?" + " , " + CLNFMessage.nfmSubDate + " = ?" + ", "
                + CLNFMessage.nfmCState + " = ?" + ", " + CLNFMessage.nfmSubEnd
                + " = ?" + ", " + CLNFMessage.nfmSubEndDate + " = ?" + ", "
                + CLNFMessage.nfmIsPuase + " = ?" + ", "
                + CLNFMessage.parReamrk + " = ?" + ", "
                + CLNFMessage.nfmCreateTime + " = ?" + " where "
                + CLNFMessage.nfmId + " = " + nfmId;
        Object[] mValue = new Object[]{nfmId, nfmSendId, nfmGetId,
                nfmCalendarId, nfmOpenState, nfmStatus, nfmIsAlarm,
                nfmPostpone, nfmColorType, nfmDisplayTime, nfmBeforeTime,
                nfmSourceType, nfmType, nfmAType, nfmInSTable, nfmIsEnd,
                nfmDownState, nfmPostState,
                StringUtils.getIsStringEqulesNull(nfmParameter),
                StringUtils.getIsStringEqulesNull(nfmContent),
                StringUtils.getIsStringEqulesNull(nfmDate),
                StringUtils.getIsStringEqulesNull(nfmTime),
                StringUtils.getIsStringEqulesNull(nfmSourceDesc),
                StringUtils.getIsStringEqulesNull(nfmSourceDescSpare),
                StringUtils.getIsStringEqulesNull(nfmTags),
                StringUtils.getIsStringEqulesNull(nfmRingDesc),
                StringUtils.getIsStringEqulesNull(nfmRingCode),
                StringUtils.getIsStringEqulesNull(nfmStartDate),
                StringUtils.getIsStringEqulesNull(nfmInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmLastCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmNextCreatedTime),
                StringUtils.getIsStringEqulesNull(nfmWebURL),
                StringUtils.getIsStringEqulesNull(nfmImagePath),
                StringUtils.getIsStringEqulesNull(nfmSendName),
                StringUtils.getIsStringEqulesNull(nfmRemark), nfmUpdateState,
                nfmPId, nfmSubState,
                StringUtils.getIsStringEqulesNull(nfmSubDate), nfmCState,
                nfmSubEnd, StringUtils.getIsStringEqulesNull(nfmSubEndDate),
                nfmIsPuase, StringUtils.getIsStringEqulesNull(parReamrk),
                StringUtils.getIsStringEqulesNull(nfmCreateTime)};
        // String sql = "update " + CLNFMessage.CLNFMessage + " set "
        // + CLNFMessage.nfmId + " = " + nfmId + " , "
        // + CLNFMessage.nfmSendId + " = " + nfmSendId + " , "
        // + CLNFMessage.nfmGetId + " = " + nfmGetId + " , "
        // + CLNFMessage.nfmCalendarId + " = " + nfmCalendarId + " , "
        // + CLNFMessage.nfmOpenState + " = " + nfmOpenState + " , "
        // + CLNFMessage.nfmStatus + " = " + nfmStatus + " , "
        // + CLNFMessage.nfmIsAlarm + " = " + nfmIsAlarm + " , "
        // + CLNFMessage.nfmPostpone + " = " + nfmPostpone + " , "
        // + CLNFMessage.nfmColorType + " = " + nfmColorType + " , "
        // + CLNFMessage.nfmDisplayTime + " = " + nfmDisplayTime + " , "
        // + CLNFMessage.nfmBeforeTime + " = " + nfmBeforeTime + " , "
        // + CLNFMessage.nfmSourceType + " = " + nfmSourceType + " , "
        // + CLNFMessage.nfmType + " = " + nfmType + " , "
        // + CLNFMessage.nfmAType + " = " + nfmAType + " , "
        // + CLNFMessage.nfmInSTable + " = " + nfmInSTable + " , "
        // + CLNFMessage.nfmIsEnd + " = " + nfmIsEnd + " , "
        // + CLNFMessage.nfmDownState + " = " + nfmDownState + " , "
        // + CLNFMessage.nfmPostState + " = " + nfmPostState + " , "
        // + CLNFMessage.nfmParameter + " = '" + nfmParameter + "' , "
        // + CLNFMessage.nfmContent + " = '" + nfmContent + "' , "
        // + CLNFMessage.nfmDate + " = '" + nfmDate + "' , "
        // + CLNFMessage.nfmTime + " = '" + nfmTime + "' , "
        // + CLNFMessage.nfmSourceDesc + " = '" + nfmSourceDesc + "' , "
        // + CLNFMessage.nfmSourceDescSpare + " = '" + nfmSourceDescSpare
        // + "' , " + CLNFMessage.nfmTags + " = '" + nfmTags + "' , "
        // + CLNFMessage.nfmRingDesc + " = '" + nfmRingDesc + "' , "
        // + CLNFMessage.nfmRingCode + " = '" + nfmRingCode + "' , "
        // + CLNFMessage.nfmStartDate + " = '" + nfmStartDate + "' , "
        // + CLNFMessage.nfmInitialCreatedTime + " = '"
        // + nfmInitialCreatedTime + "' , "
        // + CLNFMessage.nfmLastCreatedTime + " = '" + nfmLastCreatedTime
        // + "' , " + CLNFMessage.nfmNextCreatedTime + " = '"
        // + nfmNextCreatedTime + "' , " + CLNFMessage.nfmWebURL + " = '"
        // + nfmWebURL + "' , " + CLNFMessage.nfmImagePath + " = '"
        // + nfmImagePath + "' , " + CLNFMessage.nfmSendName + " = '"
        // + nfmSendName + "' , " + CLNFMessage.nfmRemark + " = '"
        // + nfmRemark + "' , " + CLNFMessage.nfmUpdateState + " = "
        // + nfmUpdateState + " , " + CLNFMessage.nfmPId + " = " + nfmPId
        // + " , " + CLNFMessage.nfmSubState + " = " + nfmSubState + " , "
        // + CLNFMessage.nfmSubDate + " = '" + nfmSubDate + "' , "
        // + CLNFMessage.nfmCState + " = " + nfmCState + " , "
        // + CLNFMessage.nfmSubEnd + " = " + nfmSubEnd + " , "
        // + CLNFMessage.nfmSubEndDate + " = '" + nfmSubEndDate + "' , "
        // + CLNFMessage.nfmIsPuase + " = " + nfmIsPuase + " , "
        // + CLNFMessage.parReamrk + " = '" + parReamrk + "' ,"
        // + CLNFMessage.nfmCreateTime + " = '" + nfmCreateTime
        // + "' where " + CLNFMessage.nfmId + " = " + nfmId;

        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据子记事的nfmid修改母记事状态
     */
    public void alterNewFriendParentData(Integer nfmPId, Integer nfmSubState,
                                         String nfmSubDate) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmUpdateState + " = " + 2 + " , "
                + CLNFMessage.nfmSubState + " = " + 1 + " , "
                + CLNFMessage.nfmSubDate + " = '" + nfmSubDate + "' where "
                + CLNFMessage.nfmId + " = " + nfmPId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据子记事的nfmid修改母记事状态
     */
    public void alterNewFriendParentData1(Integer nfmPId, Integer nfmSubState,
                                          String nfmSubDate) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmUpdateState + " = " + 2 + " , "
                + CLNFMessage.nfmSubState + " = " + nfmSubState + " , "
                + CLNFMessage.nfmSubDate + " = '" + nfmSubDate + "' where "
                + CLNFMessage.nfmId + " = " + nfmPId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据返回id删除本地发送消息
     */
    public void deleteNewFriendsData(Integer oldID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + CLNFMessage.CLNFMessage + " where "
                + CLNFMessage.nfmId + " = " + oldID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据返回id删除本地子记事
     */
    public void deleteNewFriendsChildData(Integer oldID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + CLNFMessage.CLNFMessage + " where "
                + CLNFMessage.nfmPId + " = " + oldID + " and ("
                + CLNFMessage.nfmSubState + " = " + 0 + " or "
                + CLNFMessage.nfmSubState + " = " + 3 + ")";
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据返回id和uid删除本地记事
     */
    public void deleteFriendsData(Integer uid, Integer dataId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + CLNFMessage.CLNFMessage + " where "
                + CLNFMessage.nfmSendId + " = " + uid + " and "
                + CLNFMessage.nfmId + " = " + dataId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询数据库是否有相同id的数据
     */
    public int CheckCountNewFriendData(int id) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + CLNFMessage.CLNFMessage
                + " where " + CLNFMessage.nfmId + " = " + id;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    /**
     * 根据nfmID删除对应的消息和重复消息的子记事
     *
     * @param nfmID
     */
    public void deleteNewFriendLocalData(Integer nfmID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql;
        if (nfmID < 0) {
            sql = "delete from " + CLNFMessage.CLNFMessage + " where "
                    + CLNFMessage.nfmId + " = " + nfmID;
        } else {
            sql = "update " + CLNFMessage.CLNFMessage + " set "
                    + CLNFMessage.nfmUpdateState + " = " + 3 + " where "
                    + CLNFMessage.nfmId + " = " + nfmID + " and "
                    + CLNFMessage.nfmUpdateState + " != " + 3;
        }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改新版好友消息子日程
     *
     * @return
     */
    public void updateNewFriendChildData(int nfmID, int nfmSubState,
                                         String nfmSubDate, int updateState) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmUpdateState + " = " + updateState + " , "
                + CLNFMessage.nfmSubState + " = " + nfmSubState + " , "
                + CLNFMessage.nfmSubDate + " = '" + nfmSubDate + "' where "
                + CLNFMessage.nfmId + " = " + nfmID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改新版好友顺延未结束日期
     *
     * @return
     */
    public void updateNewFriendChildData(int nfmID, String date) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmDate + " = '" + date + "' , "
                + CLNFMessage.nfmUpdateState + " = " + 2 + " where "
                + CLNFMessage.nfmId + " = " + nfmID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNewFriendChildData1(int nfmID, String date) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmDate + " = '" + date + "' , "
                + CLNFMessage.nfmUpdateState + " = " + 0 + " where "
                + CLNFMessage.nfmId + " = " + nfmID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改好友消息子记事结束状态和时间
     *
     * @return
     */
    public void updateNewFriendChildEndData(int nfmID, String date) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmDate + " = '" + date + "' where "
                + CLNFMessage.nfmId + " = " + nfmID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改新版好友信息是否结束
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public void updateNewFriendEndData(Map<String, String> upMap,
                                       String sqlWhere) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String updateMap = "";

        // 取出需修改的字段拼接
        Set set = upMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();

            if (isNum(mapentry.getValue().toString())) {
                updateMap = updateMap + mapentry.getKey() + "="
                        + mapentry.getValue() + ",";
            } else {
                updateMap = updateMap + mapentry.getKey() + "='"
                        + mapentry.getValue() + "',";
            }

        }
        String updateStr = updateMap.substring(0, updateMap.lastIndexOf(","));
        String[] str = updateStr.split(",");
        String updateState = str[0].toString();
        String schIsEnd = str[1].toString();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + updateState + " , " + schIsEnd + " " + sqlWhere;

        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改新版好友信息是否结束 一个参数
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public void updateNewFriendEndData1(Map<String, String> upMap,
                                        String sqlWhere) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String updateMap = "";

        // 取出需修改的字段拼接
        Set set = upMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();

            if (isNum(mapentry.getValue().toString())) {
                updateMap = updateMap + mapentry.getKey() + "="
                        + mapentry.getValue() + ",";
            } else {
                updateMap = updateMap + mapentry.getKey() + "='"
                        + mapentry.getValue() + "',";
            }

        }
        String updateStr = updateMap.substring(0, updateMap.lastIndexOf(","));
        String[] str = updateStr.split(",");
        String updateState = str[0].toString();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + updateState + " " + sqlWhere;

        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改好友日程推迟时间
     */
    public void updateNewFriendDateData(int ID, String date, String time,
                                        int updatestate) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();

        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmDate + " = '" + date + "', "
                + CLNFMessage.nfmTime + " = '" + time + "', "
                + CLNFMessage.nfmUpdateState + " = " + updatestate + " where "
                + CLNFMessage.nfmId + " = " + ID + " and "
                + CLNFMessage.nfmUpdateState + " != " + 3;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改好友消息子记事状态和修改时间
     *
     * @return
     */
    public void updateNewFriendChildStateData(int nfmID, int state, String date) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLNFMessage.CLNFMessage + " set "
                + CLNFMessage.nfmSubState + " = " + state + " , "
                + CLNFMessage.nfmSubDate + " = '" + date + "' where "
                + CLNFMessage.nfmId + " = " + nfmID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************************************* 标签表 *************************************************/
    /**
     * 本地插入
     *
     * @param ctgText
     * @param ctgOrder
     * @param ctgColor
     * @param ctgType
     * @param ctgUpdateState
     * @param ctgDesc
     * @param ctgCount
     * @return
     */
    public boolean insertTagData(String ctgText, Integer ctgOrder,
                                 String ctgColor, Integer ctgType, Integer ctgUpdateState,
                                 String ctgDesc, String ctgCount) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int ctgId = getLocalId(-1, "CLCategoryTable", CLCategoryTable.ctgId);
        // String sql = "insert into CLCategoryTable values(" + ctgId + ",'"
        // + ctgText + "'," + ctgOrder + ",'" + ctgColor + "'," + ctgType
        // + "," + ctgUpdateState + ",'" + ctgDesc + "','" + ctgCount
        // + "')";
        String sql = "insert into CLCategoryTable(ctgId,ctgType,ctgOrder,ctgUpdateState,"
                + "ctgText,ctgColor,ctgDesc,ctgCount) "
                + "values(?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{ctgId, ctgType, ctgOrder, ctgUpdateState,
                StringUtils.getIsStringEqulesNull(ctgText),
                StringUtils.getIsStringEqulesNull(ctgColor),
                StringUtils.getIsStringEqulesNull(ctgDesc),
                StringUtils.getIsStringEqulesNull(ctgCount)};
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从网上获取
     *
     * @return
     */
    public boolean insertTagIntenetData(Integer ctgId, String ctgText,
                                        Integer ctgOrder, String ctgColor, Integer ctgType,
                                        Integer ctgUpdateState, String ctgDesc, String ctgCount) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        // String sql = "insert into CLCategoryTable values(" + ctgId + ",'"
        // + ctgText + "'," + ctgOrder + ",'" + ctgColor + "'," + ctgType
        // + "," + ctgUpdateState + ",'" + ctgDesc + "','" + ctgCount
        // + "')";
        String sql = "insert into CLCategoryTable(ctgId,ctgType,ctgOrder,ctgUpdateState,"
                + "ctgText,ctgColor,ctgDesc,ctgCount) "
                + "values(?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{ctgId, ctgType, ctgOrder, ctgUpdateState,
                StringUtils.getIsStringEqulesNull(ctgText),
                StringUtils.getIsStringEqulesNull(ctgColor),
                StringUtils.getIsStringEqulesNull(ctgDesc),
                StringUtils.getIsStringEqulesNull(ctgCount)};
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询数据库是否有标签
     */
    public int CheckCountTagData() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + CLCategoryTable.CLCategoryTable
                + " where " + CLCategoryTable.ctgId + " > " + 0 + " and "
                + CLCategoryTable.ctgType + " = " + 1;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    /**
     * 查询数据库是否有标签
     */
    public int CheckCountTagData(int ctgId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + CLCategoryTable.CLCategoryTable
                + " where " + CLCategoryTable.ctgId + " = " + ctgId;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    public void updateTagData(Integer ctgId, String ctgText, Integer ctgOrder,
                              String ctgColor, Integer ctgType, Integer ctgUpdateState,
                              String ctgDesc, String ctgCount) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLCategoryTable.CLCategoryTable + " set "
                + CLCategoryTable.ctgText + " = ?" + ", "
                + CLCategoryTable.ctgOrder + " = ?" + " , "
                + CLCategoryTable.ctgColor + " = ?" + ", "
                + CLCategoryTable.ctgType + " = ?" + ", "
                + CLCategoryTable.ctgUpdateState + " = ?" + ", "
                + CLCategoryTable.ctgDesc + " = ?" + ", "
                + CLCategoryTable.ctgCount + " = ?" + " where "
                + CLCategoryTable.ctgId + " = " + ctgId;
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(ctgText), ctgOrder,
                StringUtils.getIsStringEqulesNull(ctgColor), ctgType,
                ctgUpdateState, StringUtils.getIsStringEqulesNull(ctgDesc),
                StringUtils.getIsStringEqulesNull(ctgCount)};
        // String sql = "update " + CLCategoryTable.CLCategoryTable + " set "
        // + CLCategoryTable.ctgText + " = '" + ctgText + "' , "
        // + CLCategoryTable.ctgOrder + " = " + ctgOrder + " , "
        // + CLCategoryTable.ctgColor + " = '" + ctgColor + "', "
        // + CLCategoryTable.ctgType + " = " + ctgType + ", "
        // + CLCategoryTable.ctgUpdateState + " = " + ctgUpdateState
        // + ", " + CLCategoryTable.ctgDesc + " = '" + ctgDesc + "', "
        // + CLCategoryTable.ctgCount + " = '" + ctgCount + "' where "
        // + CLCategoryTable.ctgId + " = " + ctgId;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTagData(Integer ctgId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql;
        if (ctgId < 0) {
            sql = "delete from " + CLCategoryTable.CLCategoryTable + " where "
                    + CLCategoryTable.ctgId + " = " + ctgId;
        } else {
            sql = "update " + CLCategoryTable.CLCategoryTable + " set "
                    + CLCategoryTable.ctgUpdateState + " = " + 3 + " where "
                    + CLCategoryTable.ctgId + " = " + ctgId + " and "
                    + CLCategoryTable.ctgUpdateState + " != " + 3;
        }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TagCommandBean> QueryTagData(int type) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<TagCommandBean> dataList = new ArrayList<TagCommandBean>();
        TagCommandBean noticeMap = null;
        String sql = "";
        switch (type) {
            case 0:// 查询标签所有未删除的数据
                sql = "select * from " + CLCategoryTable.CLCategoryTable
                        + " where " + CLCategoryTable.ctgUpdateState + " != " + 3
                        + " and " + CLCategoryTable.ctgType + " != " + 0
                        + " order by ctgOrder asc";
                break;
            case 1:// 新建
                sql = "select * from " + CLCategoryTable.CLCategoryTable
                        + " where " + CLCategoryTable.ctgUpdateState + " = " + 1
                        + " and " + CLCategoryTable.ctgType + " != " + 0
                        + " order by ctgOrder asc";
                break;
            case 2:// 修改
                sql = "select * from " + CLCategoryTable.CLCategoryTable
                        + " where " + CLCategoryTable.ctgUpdateState + " = " + 2
                        + " and " + CLCategoryTable.ctgType + " != " + 0
                        + " order by ctgOrder asc";
                break;
            case 3:// 删除
                sql = "select * from " + CLCategoryTable.CLCategoryTable
                        + " where " + CLCategoryTable.ctgUpdateState + " = " + 3
                        + " and " + CLCategoryTable.ctgType + " != " + 0
                        + " order by ctgOrder asc";
                break;
            case 4:// 不查询id为1和2的数据 1为生日 2为到期日
                sql = "select * from " + CLCategoryTable.CLCategoryTable
                        + " where " + CLCategoryTable.ctgUpdateState + " != " + 3
                        + " and " + CLCategoryTable.ctgType + " != " + 0
                        + " order by ctgOrder asc";
                break;
            default:
                break;
        }

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new TagCommandBean();
                noticeMap.setCtgId(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgId)));
                noticeMap.setCtgType(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgType)));
                noticeMap.setCtgOrder(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgOrder)));
                noticeMap.setCtgUpdateState(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgUpdateState)));
                noticeMap.setCtgText(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgText)));
                noticeMap.setCtgColor(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgColor)));
                noticeMap.setCtgDesc(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgDesc)));
                noticeMap.setCtgCount(cursor.getString(cursor
                        .getColumnIndex(CLCategoryTable.ctgCount)));
                dataList.add(noticeMap);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public Map<String, String> QueryTagNameData(int ctgId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> noticeMap = null;
        String sql = "select * from " + CLCategoryTable.CLCategoryTable
                + " where " + CLCategoryTable.ctgUpdateState + " != " + 3
                + " and " + CLCategoryTable.ctgId + " = " + ctgId
                + " order by ctgOrder asc";

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    noticeMap = new HashMap<String, String>();
                    noticeMap.put(CLCategoryTable.ctgId, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgId)));
                    noticeMap.put(CLCategoryTable.ctgType, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgType)));
                    noticeMap.put(CLCategoryTable.ctgOrder, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgOrder)));
                    noticeMap
                            .put(CLCategoryTable.ctgUpdateState,
                                    cursor.getString(cursor
                                            .getColumnIndex(CLCategoryTable.ctgUpdateState)));
                    noticeMap.put(CLCategoryTable.ctgText, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgText)));
                    noticeMap.put(CLCategoryTable.ctgColor, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgColor)));
                    noticeMap.put(CLCategoryTable.ctgDesc, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgDesc)));
                    noticeMap.put(CLCategoryTable.ctgCount, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgCount)));
                    return noticeMap;
                }
            } else {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(CLCategoryTable.ctgText, "未分类");
                return noticeMap;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noticeMap;
    }

    public Map<String, String> QueryTagIDData(String tagname) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> noticeMap = null;
        String sql = "select * from " + CLCategoryTable.CLCategoryTable
                + " where " + CLCategoryTable.ctgUpdateState + " != " + 3
                + " and " + CLCategoryTable.ctgText + " = '" + tagname
                + "' order by ctgOrder asc";

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    noticeMap = new HashMap<String, String>();
                    noticeMap.put(CLCategoryTable.ctgId, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgId)));
                    noticeMap.put(CLCategoryTable.ctgType, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgType)));
                    noticeMap.put(CLCategoryTable.ctgOrder, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgOrder)));
                    noticeMap
                            .put(CLCategoryTable.ctgUpdateState,
                                    cursor.getString(cursor
                                            .getColumnIndex(CLCategoryTable.ctgUpdateState)));
                    noticeMap.put(CLCategoryTable.ctgText, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgText)));
                    noticeMap.put(CLCategoryTable.ctgColor, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgColor)));
                    noticeMap.put(CLCategoryTable.ctgDesc, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgDesc)));
                    noticeMap.put(CLCategoryTable.ctgCount, cursor
                            .getString(cursor
                                    .getColumnIndex(CLCategoryTable.ctgCount)));
                    return noticeMap;
                }
            } else {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(CLCategoryTable.ctgText, "未分类");
                noticeMap.put(CLCategoryTable.ctgId, "0");
                return noticeMap;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noticeMap;
    }

    /**
     * 修改排序顺序
     */
    public void updateTagOrderData(Integer ctgId, Integer ctgOrder) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "";
        if (ctgId < 0) {
            sql = "update " + CLCategoryTable.CLCategoryTable + " set "
                    + CLCategoryTable.ctgOrder + " = " + ctgOrder + " , "
                    + CLCategoryTable.ctgUpdateState + " = " + 1 + " where "
                    + CLCategoryTable.ctgId + " = " + ctgId;
        } else {
            sql = "update " + CLCategoryTable.CLCategoryTable + " set "
                    + CLCategoryTable.ctgOrder + " = " + ctgOrder + " , "
                    + CLCategoryTable.ctgUpdateState + " = " + 2 + " where "
                    + CLCategoryTable.ctgId + " = " + ctgId;
        }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 和网上进行同步修改id
     */
    public void updateTagID(Integer ctgId, Integer newCtgId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLCategoryTable.CLCategoryTable + " set "
                + CLCategoryTable.ctgId + " = " + newCtgId + " , "
                + CLCategoryTable.ctgUpdateState + " = " + 0 + " where "
                + CLCategoryTable.ctgId + " = " + ctgId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /********************************************* 签到表 **************************************************/
    /**
     * 插入签到表
     */
    public boolean insertQianDaoData(Integer adsId, Integer adsScore,
                                     String adsDate, String adsLDate, String adsHoliday,
                                     String adsLHoliday, String adsSolarTerms, String adsImageNo,
                                     String adsImagePath, String adsWebURL) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        // String sql = "insert into CLAdsTable values(" + adsId + "," +
        // adsScore
        // + ",'" + adsDate + "','" + adsLDate + "','" + adsHoliday
        // + "','" + adsLHoliday + "','" + adsSolarTerms + "','"
        // + adsImageNo + "','" + adsImagePath + "','" + adsWebURL + "')";
        String sql = "insert into CLAdsTable(adsId,adsScore,adsDate,adsLDate,"
                + "adsHoliday,adsLHoliday,adsSolarTerms,adsImageNo,adsImagePath,adsWebURL) "
                + "values(?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{adsId, adsScore,
                StringUtils.getIsStringEqulesNull(adsDate),
                StringUtils.getIsStringEqulesNull(adsLDate),
                StringUtils.getIsStringEqulesNull(adsHoliday),
                StringUtils.getIsStringEqulesNull(adsLHoliday),
                StringUtils.getIsStringEqulesNull(adsSolarTerms),
                StringUtils.getIsStringEqulesNull(adsImageNo),
                StringUtils.getIsStringEqulesNull(adsImagePath),
                StringUtils.getIsStringEqulesNull(adsWebURL)};
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateQianDaoData(Integer adsId, Integer adsScore,
                                  String adsDate, String adsLDate, String adsHoliday,
                                  String adsLHoliday, String adsSolarTerms, String adsImageNo,
                                  String adsImagePath, String adsWebURL) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLAdsTable.CLAdsTable + " set "
                + CLAdsTable.adsScore + " = ?" + ", " + CLAdsTable.adsDate
                + " = ?" + ", " + CLAdsTable.adsLDate + " = ?" + ", "
                + CLAdsTable.adsHoliday + " = ?" + ", "
                + CLAdsTable.adsLHoliday + " = ?" + ", "
                + CLAdsTable.adsSolarTerms + " = ?" + ", "
                + CLAdsTable.adsImageNo + " = ?" + ", "
                + CLAdsTable.adsImagePath + " = ?" + ", "
                + CLAdsTable.adsWebURL + " = ?" + " where " + CLAdsTable.adsId
                + " = " + adsId;
        Object[] mValue = new Object[]{adsScore,
                StringUtils.getIsStringEqulesNull(adsDate),
                StringUtils.getIsStringEqulesNull(adsLDate),
                StringUtils.getIsStringEqulesNull(adsHoliday),
                StringUtils.getIsStringEqulesNull(adsLHoliday),
                StringUtils.getIsStringEqulesNull(adsSolarTerms),
                StringUtils.getIsStringEqulesNull(adsImageNo),
                StringUtils.getIsStringEqulesNull(adsImagePath),
                StringUtils.getIsStringEqulesNull(adsWebURL)};
        // String sql = "update " + CLAdsTable.CLAdsTable + " set "
        // + CLAdsTable.adsScore + " = " + adsScore + " , "
        // + CLAdsTable.adsDate + " = '" + adsDate + "', "
        // + CLAdsTable.adsLDate + " = '" + adsLDate + "', "
        // + CLAdsTable.adsHoliday + " = '" + adsHoliday + "', "
        // + CLAdsTable.adsLHoliday + " = '" + adsLHoliday + "', "
        // + CLAdsTable.adsSolarTerms + " = '" + adsSolarTerms + "', "
        // + CLAdsTable.adsImageNo + " = '" + adsImageNo + "', "
        // + CLAdsTable.adsImagePath + " = '" + adsImagePath + "', "
        // + CLAdsTable.adsWebURL + " = '" + adsWebURL + "'" + " where "
        // + CLAdsTable.adsId + " = " + adsId;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改对应的图片信息
     */
    public void updateQianDaoImgData(String adsImageNo, String adsImagePath,
                                     String adsWebURL) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLAdsTable.CLAdsTable + " set "
                + CLAdsTable.adsImageNo + " = ?" + ", "
                + CLAdsTable.adsImagePath + " = ?" + ", "
                + CLAdsTable.adsWebURL + " =? " + " where "
                + CLAdsTable.adsImageNo + " = '" + adsImageNo + "'";
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(adsImageNo),
                StringUtils.getIsStringEqulesNull(adsImagePath),
                StringUtils.getIsStringEqulesNull(adsWebURL)};
        // String sql = "update " + CLAdsTable.CLAdsTable + " set "
        // + CLAdsTable.adsImageNo + " = '" + adsImageNo + "', "
        // + CLAdsTable.adsImagePath + " = '" + adsImagePath + "' where "
        // + CLAdsTable.adsImageNo + " = '" + adsImageNo + "'";
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询数据库是否有标签
     */
    public int CheckCountQianDaoData(String adsDate) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + CLAdsTable.CLAdsTable
                + " where " + CLAdsTable.adsDate + " = '" + adsDate + "'";
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;

    }

    /**
     * 查询数据库是否有图片相应信息
     */
    public int CheckCountQianDaoImgData(String adsImageNo) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + CLAdsTable.CLAdsTable
                + " where " + CLAdsTable.adsImageNo + " = '" + adsImageNo + "'";
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    public Map<String, String> QueryQianDaoImgData(String date, int type) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        Map<String, String> noticeMap = new HashMap<String, String>();
        String sql = "";
        switch (type) {
            case 0:// 根据日期查询是否含有图片
                sql = "select * from " + CLAdsTable.CLAdsTable + " where "
                        + CLAdsTable.adsDate + " = '" + date
                        + "' order by adsDate desc";
                break;
            case 1:

                break;
            default:
                break;
        }

        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap.put(CLAdsTable.adsId, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsId)));
                noticeMap.put(CLAdsTable.adsScore, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsScore)));
                noticeMap.put(CLAdsTable.adsDate, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsDate)));
                noticeMap.put(CLAdsTable.adsLDate, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsLDate)));
                noticeMap.put(CLAdsTable.adsHoliday, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsHoliday)));
                noticeMap.put(CLAdsTable.adsLHoliday, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsLHoliday)));
                noticeMap.put(CLAdsTable.adsSolarTerms, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsSolarTerms)));
                noticeMap.put(CLAdsTable.adsImageNo, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsImageNo)));
                noticeMap.put(CLAdsTable.adsImagePath, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsImagePath)));
                noticeMap.put(CLAdsTable.adsWebURL, cursor.getString(cursor
                        .getColumnIndex(CLAdsTable.adsWebURL)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noticeMap;
    }

    /**
     * 删除标签
     */
    public void deleteCLAdsTable() {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + CLAdsTable.CLAdsTable;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ====================================新版发现相关数据操作======================================//

    /**
     * 删除日程表中schaid相同的日程
     */
    public void deleteSchAIDData(int schcRecommendId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schcRecommendId + " = " + schcRecommendId
                + " and " + ScheduleTable.schAID + " != " + 0;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除重复母记事
     */
    public void deleteRepFocusParentData(int repId, int recommandId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schRepeatID + " = " + repId + " and "
                + ScheduleTable.schAID + " != " + 0 + " and "
                + ScheduleTable.schcRecommendId + " = " + recommandId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除重复子记事
     */
    public void deleteRepFocusData(int repId, String repdatetwo) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schRepeatID + " = " + repId + " and "
                + ScheduleTable.schAID + " != " + 0 + " and "
                + ScheduleTable.schRepeatDate + " = '" + repdatetwo + "'";
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除普通记事
     */
    public void deleteSchFocusData(int schAID, int recommandId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "delete from " + ScheduleTable.ScheduleTable + " where "
                + ScheduleTable.schAID + " = " + schAID + " and "
                + ScheduleTable.schAID + " > " + 0 + " and "
                + ScheduleTable.schcRecommendId + " = " + recommandId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询日程表中repid为0并且schaid!=0并且schaid和传入发现中schaid有没有相同的
     */
    public int CheckCountSchFromFocusData(int schAID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + ScheduleTable.ScheduleTable
                + " where " + ScheduleTable.schAID + " = " + schAID + " and "
                + ScheduleTable.schAID + " > " + 0;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    /**
     * 查询日程表中repid不为为0并且schaid!=0并且repid和传入发现中repid有没有相同的
     */
    public int CheckCountRepFromFocusData(int repid) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "select count(*) from " + ScheduleTable.ScheduleTable
                + " where " + ScheduleTable.schRepeatID + " = " + repid
                + " and " + ScheduleTable.schAID + " > " + 0;
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    public void updateScheduleNoIDForSchData(String schContent, String schDate,
                                             String schTime, int schIsAlarm, int schBeforeTime,
                                             int schDisplayTime, int schIsPostpone, int schIsImportant,
                                             int schColorType, int schIsEnd, String createtime, String schTags,
                                             int schSourceType, String schSourceDesc, String schSourceDescSpare,
                                             int schRepeatID, String schRepeatDate, String schUpdateTime,
                                             int schUpdateState, int schOpenState, int schRepeatLink,
                                             String schRingDesc, String schRingCode, String schcRecommendName,
                                             int schRead, int schAID, int schAType, String schWebURL,
                                             String schImagePath, int schFocusState, int schFriendID,
                                             int schcRecommendId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String content = schContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String sql = "update ScheduleTable set " + ScheduleTable.schContent
                + " = ?" + "," + ScheduleTable.schDate + " = ? " + ", "
                + ScheduleTable.schTime + " = ?" + ", "
                + ScheduleTable.schIsAlarm + " = ?" + ", "
                + ScheduleTable.schBeforeTime + " = ?" + ", "
                + ScheduleTable.schDisplayTime + " = ?" + ", "
                + ScheduleTable.schIsPostpone + " = ?" + ", "
                + ScheduleTable.schIsImportant + " = ?" + ", "
                + ScheduleTable.schColorType + " = ?" + ", "
                + ScheduleTable.schIsEnd + " = ?" + ", "
                + ScheduleTable.schTags + " = ?" + ", "
                + ScheduleTable.schSourceType + " = ?" + ", "
                + ScheduleTable.schSourceDesc + " = ?" + ", "
                + ScheduleTable.schSourceDescSpare + " = ?" + ", "
                + ScheduleTable.schRepeatID + " = ?" + ", "
                + ScheduleTable.schRepeatDate + " = ?" + ", "
                + ScheduleTable.schUpdateTime + " = ?" + ", "
                + ScheduleTable.schUpdateState + " = ?" + ", "
                + ScheduleTable.schOpenState + " = ?" + ", "
                + ScheduleTable.schRepeatLink + " = ?" + ", "
                + ScheduleTable.schRingDesc + " = ?" + ", "
                + ScheduleTable.schRingCode + " = ?" + ", "
                + ScheduleTable.schcRecommendName + " = ?" + ", "
                + ScheduleTable.schRead + " = ?" + ", " + ScheduleTable.schAID
                + " = ?" + ", " + ScheduleTable.schAType + " = ?" + ", "
                + ScheduleTable.schWebURL + " = ?" + ", "
                + ScheduleTable.schImagePath + " = ?" + ", "
                + ScheduleTable.schFocusState + " = ?" + ", "
                + ScheduleTable.schFriendID + " = ?" + ", "
                + ScheduleTable.schcRecommendId + " = ? " + " where "
                + ScheduleTable.schAID + " = " + schAID;
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(schDate),
                StringUtils.getIsStringEqulesNull(schTime), schIsAlarm,
                schBeforeTime, schDisplayTime, schIsPostpone, schIsImportant,
                schColorType, schIsEnd,
                StringUtils.getIsStringEqulesNull(schTags), schSourceType,
                StringUtils.getIsStringEqulesNull(schSourceDesc),
                StringUtils.getIsStringEqulesNull(schSourceDescSpare),
                schRepeatID, StringUtils.getIsStringEqulesNull(schRepeatDate),
                StringUtils.getIsStringEqulesNull(schUpdateTime),
                schUpdateState, schOpenState, schRepeatLink,
                StringUtils.getIsStringEqulesNull(schRingDesc),
                StringUtils.getIsStringEqulesNull(schRingCode),
                StringUtils.getIsStringEqulesNull(schcRecommendName), schRead,
                schAID, schAType, StringUtils.getIsStringEqulesNull(schWebURL),
                StringUtils.getIsStringEqulesNull(schImagePath), schFocusState,
                schFriendID, schcRecommendId};
        // String sql = "update " + ScheduleTable.ScheduleTable + " set "
        // + ScheduleTable.schContent + " = '" + content + "', "
        // + ScheduleTable.schDate + " = '" + schDate + "', "
        // + ScheduleTable.schTime + " = '" + schTime + "', "
        // + ScheduleTable.schIsAlarm + " = " + schIsAlarm + ", "
        // + ScheduleTable.schBeforeTime + " = " + schBeforeTime + ", "
        // + ScheduleTable.schDisplayTime + " = " + schDisplayTime + ", "
        // + ScheduleTable.schIsPostpone + " = " + schIsPostpone + ", "
        // + ScheduleTable.schIsImportant + " = " + schIsImportant + ", "
        // + ScheduleTable.schColorType + " = " + schColorType + ", "
        // + ScheduleTable.schIsEnd + " = " + schIsEnd + ", "
        // + ScheduleTable.schCreateTime + " = '" + createtime + "', "
        // + ScheduleTable.schTags + " = '" + schTags + "', "
        // + ScheduleTable.schSourceType + " = '" + schSourceType + "', "
        // + ScheduleTable.schSourceDesc + " = '" + schSourceDesc + "', "
        // + ScheduleTable.schSourceDescSpare + " = '"
        // + schSourceDescSpare + "', " + ScheduleTable.schRepeatID
        // + " = " + schRepeatID + ", " + ScheduleTable.schRepeatDate
        // + " = '" + schRepeatDate + "', " + ScheduleTable.schUpdateTime
        // + " = '" + schUpdateTime + "', " + ScheduleTable.schUpdateState
        // + " = " + schUpdateState + ", " + ScheduleTable.schOpenState
        // + " = " + schOpenState + ", " + ScheduleTable.schRepeatLink
        // + " = " + schRepeatLink + ", " + ScheduleTable.schRingDesc
        // + " = '" + schRingDesc + "', " + ScheduleTable.schRingCode
        // + " = '" + schRingCode + "', "
        // + ScheduleTable.schcRecommendName + " = '" + schcRecommendName
        // + "', " + ScheduleTable.schRead + " = " + schRead + ", "
        // + ScheduleTable.schAID + " = " + schAID + ", "
        // + ScheduleTable.schAType + " = " + schAType + ", "
        // + ScheduleTable.schWebURL + " = '" + schWebURL + "', "
        // + ScheduleTable.schImagePath + " = '" + schImagePath + "', "
        // + ScheduleTable.schFocusState + " = " + schFocusState + ", "
        // + ScheduleTable.schFriendID + " = " + schFriendID + ", "
        // + ScheduleTable.schcRecommendId + " = " + schcRecommendId
        // + " where " + ScheduleTable.schAID + " = " + schAID;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateScheduleNoIDForRepData(String schContent, String schDate,
                                             String schTime, int schIsAlarm, int schBeforeTime,
                                             int schDisplayTime, int schIsPostpone, int schIsImportant,
                                             int schColorType, int schIsEnd, String createtime, String schTags,
                                             int schSourceType, String schSourceDesc, String schSourceDescSpare,
                                             int schRepeatID, String schRepeatDate, String schUpdateTime,
                                             int schUpdateState, int schOpenState, int schRepeatLink,
                                             String schRingDesc, String schRingCode, String schcRecommendName,
                                             int schRead, int schAID, int schAType, String schWebURL,
                                             String schImagePath, int schFocusState, int schFriendID,
                                             int schcRecommendId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String content = schContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String sql = "update ScheduleTable set " + ScheduleTable.schContent
                + " = ?" + "," + ScheduleTable.schDate + " = ? " + ", "
                + ScheduleTable.schTime + " = ?" + ", "
                + ScheduleTable.schIsAlarm + " = ?" + ", "
                + ScheduleTable.schBeforeTime + " = ?" + ", "
                + ScheduleTable.schDisplayTime + " = ?" + ", "
                + ScheduleTable.schIsPostpone + " = ?" + ", "
                + ScheduleTable.schIsImportant + " = ?" + ", "
                + ScheduleTable.schColorType + " = ?" + ", "
                + ScheduleTable.schIsEnd + " = ?" + ", "
                + ScheduleTable.schCreateTime + " = ?" + ", "
                + ScheduleTable.schTags + " = ?" + ", "
                + ScheduleTable.schSourceType + " = ?" + ", "
                + ScheduleTable.schSourceDesc + " = ?" + ", "
                + ScheduleTable.schSourceDescSpare + " = ?" + ", "
                + ScheduleTable.schRepeatID + " = ?" + ", "
                + ScheduleTable.schRepeatDate + " = ?" + ", "
                + ScheduleTable.schUpdateTime + " = ?" + ", "
                + ScheduleTable.schUpdateState + " = ?" + ", "
                + ScheduleTable.schOpenState + " = ?" + ", "
                + ScheduleTable.schRepeatLink + " = ?" + ", "
                + ScheduleTable.schRingDesc + " = ?" + ", "
                + ScheduleTable.schRingCode + " = ?" + ", "
                + ScheduleTable.schcRecommendName + " = ?" + ", "
                + ScheduleTable.schRead + " = ?" + ", " + ScheduleTable.schAID
                + " = ?" + ", " + ScheduleTable.schAType + " = ?" + ", "
                + ScheduleTable.schWebURL + " = ?" + ", "
                + ScheduleTable.schImagePath + " = ?" + ", "
                + ScheduleTable.schFocusState + " = ?" + ", "
                + ScheduleTable.schFriendID + " = ?" + ", "
                + ScheduleTable.schcRecommendId + " = ? " + " where "
                + ScheduleTable.schRepeatID + " = " + schRepeatID;
        Object[] mValue = new Object[]{
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(schDate),
                StringUtils.getIsStringEqulesNull(schTime), schIsAlarm,
                schBeforeTime, schDisplayTime, schIsPostpone, schIsImportant,
                schColorType, schIsEnd,
                StringUtils.getIsStringEqulesNull(createtime),
                StringUtils.getIsStringEqulesNull(schTags), schSourceType,
                StringUtils.getIsStringEqulesNull(schSourceDesc),
                StringUtils.getIsStringEqulesNull(schSourceDescSpare),
                schRepeatID, StringUtils.getIsStringEqulesNull(schRepeatDate),
                StringUtils.getIsStringEqulesNull(schUpdateTime),
                schUpdateState, schOpenState, schRepeatLink,
                StringUtils.getIsStringEqulesNull(schRingDesc),
                StringUtils.getIsStringEqulesNull(schRingCode),
                StringUtils.getIsStringEqulesNull(schcRecommendName), schRead,
                schAID, schAType, StringUtils.getIsStringEqulesNull(schWebURL),
                StringUtils.getIsStringEqulesNull(schImagePath), schFocusState,
                schFriendID, schcRecommendId};
        // String sql = "update " + ScheduleTable.ScheduleTable + " set "
        // + ScheduleTable.schContent + " = '" + content + "', "
        // + ScheduleTable.schDate + " = '" + schDate + "', "
        // + ScheduleTable.schTime + " = '" + schTime + "', "
        // + ScheduleTable.schIsAlarm + " = " + schIsAlarm + ", "
        // + ScheduleTable.schBeforeTime + " = " + schBeforeTime + ", "
        // + ScheduleTable.schDisplayTime + " = " + schDisplayTime + ", "
        // + ScheduleTable.schIsPostpone + " = " + schIsPostpone + ", "
        // + ScheduleTable.schIsImportant + " = " + schIsImportant + ", "
        // + ScheduleTable.schColorType + " = " + schColorType + ", "
        // + ScheduleTable.schIsEnd + " = " + schIsEnd + ", "
        // + ScheduleTable.schCreateTime + " = '" + createtime + "', "
        // + ScheduleTable.schTags + " = '" + schTags + "', "
        // + ScheduleTable.schSourceType + " = '" + schSourceType + "', "
        // + ScheduleTable.schSourceDesc + " = '" + schSourceDesc + "', "
        // + ScheduleTable.schSourceDescSpare + " = '"
        // + schSourceDescSpare + "', " + ScheduleTable.schRepeatID
        // + " = " + schRepeatID + ", " + ScheduleTable.schRepeatDate
        // + " = '" + schRepeatDate + "', " + ScheduleTable.schUpdateTime
        // + " = '" + schUpdateTime + "', " + ScheduleTable.schUpdateState
        // + " = " + schUpdateState + ", " + ScheduleTable.schOpenState
        // + " = " + schOpenState + ", " + ScheduleTable.schRepeatLink
        // + " = " + schRepeatLink + ", " + ScheduleTable.schRingDesc
        // + " = '" + schRingDesc + "', " + ScheduleTable.schRingCode
        // + " = '" + schRingCode + "', "
        // + ScheduleTable.schcRecommendName + " = '" + schcRecommendName
        // + "', " + ScheduleTable.schRead + " = " + schRead + ", "
        // + ScheduleTable.schAID + " = " + schAID + ", "
        // + ScheduleTable.schAType + " = " + schAType + ", "
        // + ScheduleTable.schWebURL + " = '" + schWebURL + "', "
        // + ScheduleTable.schImagePath + " = '" + schImagePath + "', "
        // + ScheduleTable.schFocusState + " = " + schFocusState + ", "
        // + ScheduleTable.schFriendID + " = " + schFriendID + ", "
        // + ScheduleTable.schcRecommendId + " = " + schcRecommendId
        // + " where " + ScheduleTable.schRepeatID + " = " + schRepeatID;
        try {
            sqldb.execSQL(sql, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入新版发现分享表
     *
     * @return
     */
    public boolean insertNewFocusData(Integer fstFID, Integer fstType,
                                      Integer fstBeforeTime, Integer fstIsAlarm, Integer fstDisplayTime,
                                      Integer fstColorType, Integer fstIsPostpone,
                                      Integer fstIsImportant, Integer fstIsEnd, Integer fstSourceType,
                                      Integer fstRepeatId, Integer fstOpenState, Integer fstRepeatLink,
                                      Integer fstRecommendId, Integer fstIsRead, Integer fstAID,
                                      Integer fstIsPuase, Integer fstRepStateOne, Integer fstRepStateTwo,
                                      Integer fstRepInStable, Integer fstPostState, Integer fstRepType,
                                      Integer fstAType, Integer fstUpdateState, String fstParameter,
                                      String fstContent, String fstDate, String fstTime,
                                      String fstRingCode, String fstRingDesc, String fstTags,
                                      String fstSourceDesc, String fstSourceDescSpare,
                                      String fstRepeatDate, String fstRepStartDate,
                                      String fstRpNextCreatedTime, String fstRepLastCreatedTime,
                                      String fstRepInitialCreatedTime, String fstRepDateOne,
                                      String fstRepDateTwo, String fstRecommendName, String fstWebURL,
                                      String fstImagePath, String fstParReamrk, String fstCreateTime,
                                      String fstUpdateTime, String fstReamrk1, String fstReamrk2,
                                      String fstReamrk3, String fstReamrk4, String fstReamrk5) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int fstID = getLocalId(1, "CLFindScheduleTable",
                CLFindScheduleTable.fstID);
        int fstSchID = getLocalId(-1, "CLFindScheduleTable",
                CLFindScheduleTable.fstSchID);
        String content = fstContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");

        String insertStr = "insert into CLFindScheduleTable(fstID,fstFID,fstSchID, fstType,"
                + "fstBeforeTime, fstIsAlarm, fstDisplayTime,fstColorType, fstIsPostpone,"
                + "fstIsImportant,fstIsEnd, fstSourceType,fstRepeatId, fstOpenState, "
                + "fstRepeatLink,fstRecommendId, fstIsRead, fstAID,fstIsPuase, fstRepStateOne,"
                + "fstRepStateTwo,fstRepInStable,fstPostState, fstRepType,fstAType, "
                + "fstUpdateState, fstParameter,fstContent, fstDate, fstTime,fstRingCode,"
                + " fstRingDesc, fstTags,fstSourceDesc, fstSourceDescSpare,fstRepeatDate,"
                + " fstRepStartDate,fstRpNextCreatedTime, fstRepLastCreatedTime,"
                + "fstRepInitialCreatedTime, fstRepDateOne,fstRepDateTwo, fstRecommendName,"
                + " fstWebURL,fstImagePath,fstParReamrk, fstCreateTime,fstUpdateTime, "
                + "fstReamrk1,fstReamrk2,fstReamrk3, fstReamrk4,fstReamrk5) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{fstID, fstFID, fstSchID, fstType,
                fstBeforeTime, fstIsAlarm, fstDisplayTime, fstColorType,
                fstIsPostpone, fstIsImportant, fstIsEnd, fstSourceType,
                fstRepeatId, fstOpenState, fstRepeatLink, fstRecommendId,
                fstIsRead, fstAID, fstIsPuase, fstRepStateOne, fstRepStateTwo,
                fstRepInStable, fstPostState, fstRepType, fstAType,
                fstUpdateState,
                StringUtils.getIsStringEqulesNull(fstParameter),
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(fstDate),
                StringUtils.getIsStringEqulesNull(fstTime),
                StringUtils.getIsStringEqulesNull(fstRingCode),
                StringUtils.getIsStringEqulesNull(fstRingDesc),
                StringUtils.getIsStringEqulesNull(fstTags),
                StringUtils.getIsStringEqulesNull(fstSourceDesc),
                StringUtils.getIsStringEqulesNull(fstSourceDescSpare),
                StringUtils.getIsStringEqulesNull(fstRepeatDate),
                StringUtils.getIsStringEqulesNull(fstRepStartDate),
                StringUtils.getIsStringEqulesNull(fstRpNextCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepLastCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepDateOne),
                StringUtils.getIsStringEqulesNull(fstRepDateTwo),
                StringUtils.getIsStringEqulesNull(fstRecommendName),
                StringUtils.getIsStringEqulesNull(fstWebURL),
                StringUtils.getIsStringEqulesNull(fstImagePath),
                StringUtils.getIsStringEqulesNull(fstParReamrk),
                StringUtils.getIsStringEqulesNull(fstCreateTime),
                StringUtils.getIsStringEqulesNull(fstUpdateTime),
                StringUtils.getIsStringEqulesNull(fstReamrk1),
                StringUtils.getIsStringEqulesNull(fstReamrk2),
                StringUtils.getIsStringEqulesNull(fstReamrk3),
                StringUtils.getIsStringEqulesNull(fstReamrk4),
                StringUtils.getIsStringEqulesNull(fstReamrk5)};
        // String sql = "insert into CLFindScheduleTable values(" + fstID + ","
        // + fstFID + "," + fstSchID + "," + fstType + "," + fstBeforeTime
        // + "," + fstIsAlarm + "," + fstDisplayTime + "," + fstColorType
        // + "," + fstIsPostpone + "," + fstIsImportant + "," + fstIsEnd
        // + "," + fstSourceType + "," + fstRepeatId + "," + fstOpenState
        // + "," + fstRepeatLink + "," + fstRecommendId + "," + fstIsRead
        // + "," + fstAID + "," + fstIsPuase + "," + fstRepStateOne + ","
        // + fstRepStateTwo + "," + fstRepInStable + "," + fstPostState
        // + "," + fstRepType + "," + fstAType + "," + fstUpdateState
        // + ",'" + fstParameter + "','" + content + "' , '" + fstDate
        // + "' , '" + fstTime + "' , '" + fstRingCode + "', '"
        // + fstRingDesc + "','" + fstTags + "','" + fstSourceDesc + "','"
        // + fstSourceDescSpare + "','" + fstRepeatDate + "','"
        // + fstRepStartDate + "','" + fstRpNextCreatedTime + "','"
        // + fstRepLastCreatedTime + "','" + fstRepInitialCreatedTime
        // + "','" + fstRepDateOne + "','" + fstRepDateTwo + "','"
        // + fstRecommendName + "','" + fstWebURL + "','" + fstImagePath
        // + "','" + fstParReamrk + "','" + fstCreateTime + "','"
        // + fstUpdateTime + "','" + fstReamrk1 + "','" + fstReamrk2
        // + "','" + fstReamrk3 + "','" + fstReamrk4 + "','" + fstReamrk5
        // + "')";
        try {
            sqldb.execSQL(insertStr, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 插入重复数据
     */
    public boolean insertNewFocusData(Integer fstFID, Integer fstType,
                                      Integer fstBeforeTime, Integer fstIsAlarm, Integer fstDisplayTime,
                                      Integer fstColorType, Integer fstIsPostpone,
                                      Integer fstIsImportant, Integer fstIsEnd, Integer fstSourceType,
                                      Integer fstOpenState, Integer fstRepeatLink,
                                      Integer fstRecommendId, Integer fstIsRead, Integer fstAID,
                                      Integer fstIsPuase, Integer fstRepStateOne, Integer fstRepStateTwo,
                                      Integer fstRepInStable, Integer fstPostState, Integer fstRepType,
                                      Integer fstAType, Integer fstUpdateState, String fstParameter,
                                      String fstContent, String fstDate, String fstTime,
                                      String fstRingCode, String fstRingDesc, String fstTags,
                                      String fstSourceDesc, String fstSourceDescSpare,
                                      String fstRepeatDate, String fstRepStartDate,
                                      String fstRpNextCreatedTime, String fstRepLastCreatedTime,
                                      String fstRepInitialCreatedTime, String fstRepDateOne,
                                      String fstRepDateTwo, String fstRecommendName, String fstWebURL,
                                      String fstImagePath, String fstParReamrk, String fstCreateTime,
                                      String fstUpdateTime, String fstReamrk1, String fstReamrk2,
                                      String fstReamrk3, String fstReamrk4, String fstReamrk5) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int fstID = getLocalId(1, "CLFindScheduleTable",
                CLFindScheduleTable.fstID);
        int fstSchID = getLocalId(-1, "CLFindScheduleTable",
                CLFindScheduleTable.fstSchID);
        fstRepeatId = getLocalId(-1, "CLFindScheduleTable",
                CLFindScheduleTable.fstRepeatId);
        String content = fstContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String insertStr = "insert into CLFindScheduleTable(fstID,fstFID,fstSchID, fstType,"
                + "fstBeforeTime, fstIsAlarm, fstDisplayTime,fstColorType, fstIsPostpone,"
                + "fstIsImportant,fstIsEnd, fstSourceType,fstRepeatId, fstOpenState, "
                + "fstRepeatLink,fstRecommendId, fstIsRead, fstAID,fstIsPuase, fstRepStateOne,"
                + "fstRepStateTwo,fstRepInStable,fstPostState, fstRepType,fstAType, "
                + "fstUpdateState, fstParameter,fstContent, fstDate, fstTime,fstRingCode,"
                + " fstRingDesc, fstTags,fstSourceDesc, fstSourceDescSpare,fstRepeatDate,"
                + " fstRepStartDate,fstRpNextCreatedTime, fstRepLastCreatedTime,"
                + "fstRepInitialCreatedTime, fstRepDateOne,fstRepDateTwo, fstRecommendName,"
                + " fstWebURL,fstImagePath,fstParReamrk, fstCreateTime,fstUpdateTime, "
                + "fstReamrk1,fstReamrk2,fstReamrk3, fstReamrk4,fstReamrk5) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{fstID, fstFID, fstSchID, fstType,
                fstBeforeTime, fstIsAlarm, fstDisplayTime, fstColorType,
                fstIsPostpone, fstIsImportant, fstIsEnd, fstSourceType,
                fstRepeatId, fstOpenState, fstRepeatLink, fstRecommendId,
                fstIsRead, fstAID, fstIsPuase, fstRepStateOne, fstRepStateTwo,
                fstRepInStable, fstPostState, fstRepType, fstAType,
                fstUpdateState,
                StringUtils.getIsStringEqulesNull(fstParameter),
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(fstDate),
                StringUtils.getIsStringEqulesNull(fstTime),
                StringUtils.getIsStringEqulesNull(fstRingCode),
                StringUtils.getIsStringEqulesNull(fstRingDesc),
                StringUtils.getIsStringEqulesNull(fstTags),
                StringUtils.getIsStringEqulesNull(fstSourceDesc),
                StringUtils.getIsStringEqulesNull(fstSourceDescSpare),
                StringUtils.getIsStringEqulesNull(fstRepeatDate),
                StringUtils.getIsStringEqulesNull(fstRepStartDate),
                StringUtils.getIsStringEqulesNull(fstRpNextCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepLastCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepDateOne),
                StringUtils.getIsStringEqulesNull(fstRepDateTwo),
                StringUtils.getIsStringEqulesNull(fstRecommendName),
                StringUtils.getIsStringEqulesNull(fstWebURL),
                StringUtils.getIsStringEqulesNull(fstImagePath),
                StringUtils.getIsStringEqulesNull(fstParReamrk),
                StringUtils.getIsStringEqulesNull(fstCreateTime),
                StringUtils.getIsStringEqulesNull(fstUpdateTime),
                StringUtils.getIsStringEqulesNull(fstReamrk1),
                StringUtils.getIsStringEqulesNull(fstReamrk2),
                StringUtils.getIsStringEqulesNull(fstReamrk3),
                StringUtils.getIsStringEqulesNull(fstReamrk4),
                StringUtils.getIsStringEqulesNull(fstReamrk5)};
        // String sql = "insert into CLFindScheduleTable values(" + fstID + ","
        // + fstFID + "," + fstSchID + "," + fstType + "," + fstBeforeTime
        // + "," + fstIsAlarm + "," + fstDisplayTime + "," + fstColorType
        // + "," + fstIsPostpone + "," + fstIsImportant + "," + fstIsEnd
        // + "," + fstSourceType + "," + fstRepeatId + "," + fstOpenState
        // + "," + fstRepeatLink + "," + fstRecommendId + "," + fstIsRead
        // + "," + fstAID + "," + fstIsPuase + "," + fstRepStateOne + ","
        // + fstRepStateTwo + "," + fstRepInStable + "," + fstPostState
        // + "," + fstRepType + "," + fstAType + "," + fstUpdateState
        // + ",'" + fstParameter + "','" + content + "' , '" + fstDate
        // + "' , '" + fstTime + "' , '" + fstRingCode + "', '"
        // + fstRingDesc + "','" + fstTags + "','" + fstSourceDesc + "','"
        // + fstSourceDescSpare + "','" + fstRepeatDate + "','"
        // + fstRepStartDate + "','" + fstRpNextCreatedTime + "','"
        // + fstRepLastCreatedTime + "','" + fstRepInitialCreatedTime
        // + "','" + fstRepDateOne + "','" + fstRepDateTwo + "','"
        // + fstRecommendName + "','" + fstWebURL + "','" + fstImagePath
        // + "','" + fstParReamrk + "','" + fstCreateTime + "','"
        // + fstUpdateTime + "','" + fstReamrk1 + "','" + fstReamrk2
        // + "','" + fstReamrk3 + "','" + fstReamrk4 + "','" + fstReamrk5
        // + "')";
        try {
            sqldb.execSQL(insertStr, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从网上下载数据插入到新版分享表中
     *
     * @return
     */
    public boolean insertNewFocusFromIntenetData(Integer fstFID,
                                                 Integer fstSchID, Integer fstType, Integer fstBeforeTime,
                                                 Integer fstIsAlarm, Integer fstDisplayTime, Integer fstColorType,
                                                 Integer fstIsPostpone, Integer fstIsImportant, Integer fstIsEnd,
                                                 Integer fstSourceType, Integer fstRepeatId, Integer fstOpenState,
                                                 Integer fstRepeatLink, Integer fstRecommendId, Integer fstIsRead,
                                                 Integer fstAID, Integer fstIsPuase, Integer fstRepStateOne,
                                                 Integer fstRepStateTwo, Integer fstRepInStable,
                                                 Integer fstPostState, Integer fstRepType, Integer fstAType,
                                                 Integer fstUpdateState, String fstParameter, String fstContent,
                                                 String fstDate, String fstTime, String fstRingCode,
                                                 String fstRingDesc, String fstTags, String fstSourceDesc,
                                                 String fstSourceDescSpare, String fstRepeatDate,
                                                 String fstRepStartDate, String fstRpNextCreatedTime,
                                                 String fstRepLastCreatedTime, String fstRepInitialCreatedTime,
                                                 String fstRepDateOne, String fstRepDateTwo,
                                                 String fstRecommendName, String fstWebURL, String fstImagePath,
                                                 String fstParReamrk, String fstCreateTime, String fstUpdateTime,
                                                 String fstReamrk1, String fstReamrk2, String fstReamrk3,
                                                 String fstReamrk4, String fstReamrk5) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int fstID = getLocalId(1, "CLFindScheduleTable",
                CLFindScheduleTable.fstID);
        String content = fstContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String insertStr = "insert into CLFindScheduleTable(fstID,fstFID,fstSchID, fstType,"
                + "fstBeforeTime, fstIsAlarm, fstDisplayTime,fstColorType, fstIsPostpone,"
                + "fstIsImportant,fstIsEnd, fstSourceType,fstRepeatId, fstOpenState, "
                + "fstRepeatLink,fstRecommendId, fstIsRead, fstAID,fstIsPuase, fstRepStateOne,"
                + "fstRepStateTwo,fstRepInStable,fstPostState, fstRepType,fstAType, "
                + "fstUpdateState, fstParameter,fstContent, fstDate, fstTime,fstRingCode,"
                + " fstRingDesc, fstTags,fstSourceDesc, fstSourceDescSpare,fstRepeatDate,"
                + " fstRepStartDate,fstRpNextCreatedTime, fstRepLastCreatedTime,"
                + "fstRepInitialCreatedTime, fstRepDateOne,fstRepDateTwo, fstRecommendName,"
                + " fstWebURL,fstImagePath,fstParReamrk, fstCreateTime,fstUpdateTime, "
                + "fstReamrk1,fstReamrk2,fstReamrk3, fstReamrk4,fstReamrk5) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{fstID, fstFID, fstSchID, fstType,
                fstBeforeTime, fstIsAlarm, fstDisplayTime, fstColorType,
                fstIsPostpone, fstIsImportant, fstIsEnd, fstSourceType,
                fstRepeatId, fstOpenState, fstRepeatLink, fstRecommendId,
                fstIsRead, fstAID, fstIsPuase, fstRepStateOne, fstRepStateTwo,
                fstRepInStable, fstPostState, fstRepType, fstAType,
                fstUpdateState,
                StringUtils.getIsStringEqulesNull(fstParameter),
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(fstDate),
                StringUtils.getIsStringEqulesNull(fstTime),
                StringUtils.getIsStringEqulesNull(fstRingCode),
                StringUtils.getIsStringEqulesNull(fstRingDesc),
                StringUtils.getIsStringEqulesNull(fstTags),
                StringUtils.getIsStringEqulesNull(fstSourceDesc),
                StringUtils.getIsStringEqulesNull(fstSourceDescSpare),
                StringUtils.getIsStringEqulesNull(fstRepeatDate),
                StringUtils.getIsStringEqulesNull(fstRepStartDate),
                StringUtils.getIsStringEqulesNull(fstRpNextCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepLastCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepDateOne),
                StringUtils.getIsStringEqulesNull(fstRepDateTwo),
                StringUtils.getIsStringEqulesNull(fstRecommendName),
                StringUtils.getIsStringEqulesNull(fstWebURL),
                StringUtils.getIsStringEqulesNull(fstImagePath),
                StringUtils.getIsStringEqulesNull(fstParReamrk),
                StringUtils.getIsStringEqulesNull(fstCreateTime),
                StringUtils.getIsStringEqulesNull(fstUpdateTime),
                StringUtils.getIsStringEqulesNull(fstReamrk1),
                StringUtils.getIsStringEqulesNull(fstReamrk2),
                StringUtils.getIsStringEqulesNull(fstReamrk3),
                StringUtils.getIsStringEqulesNull(fstReamrk4),
                StringUtils.getIsStringEqulesNull(fstReamrk5)};
        // String sql = "insert into CLFindScheduleTable values(" + fstID + ","
        // + fstFID + "," + fstSchID + "," + fstType + "," + fstBeforeTime
        // + "," + fstIsAlarm + "," + fstDisplayTime + "," + fstColorType
        // + "," + fstIsPostpone + "," + fstIsImportant + "," + fstIsEnd
        // + "," + fstSourceType + "," + fstRepeatId + "," + fstOpenState
        // + "," + fstRepeatLink + "," + fstRecommendId + "," + fstIsRead
        // + "," + fstAID + "," + fstIsPuase + "," + fstRepStateOne + ","
        // + fstRepStateTwo + "," + fstRepInStable + "," + fstPostState
        // + "," + fstRepType + "," + fstAType + "," + fstUpdateState
        // + ",'" + fstParameter + "','" + content + "' , '" + fstDate
        // + "' , '" + fstTime + "' , '" + fstRingCode + "', '"
        // + fstRingDesc + "','" + fstTags + "','" + fstSourceDesc + "','"
        // + fstSourceDescSpare + "','" + fstRepeatDate + "','"
        // + fstRepStartDate + "','" + fstRpNextCreatedTime + "','"
        // + fstRepLastCreatedTime + "','" + fstRepInitialCreatedTime
        // + "','" + fstRepDateOne + "','" + fstRepDateTwo + "','"
        // + fstRecommendName + "','" + fstWebURL + "','" + fstImagePath
        // + "','" + fstParReamrk + "','" + fstCreateTime + "','"
        // + fstUpdateTime + "','" + fstReamrk1 + "','" + fstReamrk2
        // + "','" + fstReamrk3 + "','" + fstReamrk4 + "','" + fstReamrk5
        // + "')";
        try {
            sqldb.execSQL(insertStr, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从网上下载重复插入
     *
     * @return
     */
    public void insertNewFocusFromIntenetRepeatData(Integer fstFID,
                                                    Integer fstType, Integer fstBeforeTime, Integer fstIsAlarm,
                                                    Integer fstDisplayTime, Integer fstColorType,
                                                    Integer fstIsPostpone, Integer fstIsImportant, Integer fstIsEnd,
                                                    Integer fstSourceType, Integer fstRepeatId, Integer fstOpenState,
                                                    Integer fstRepeatLink, Integer fstRecommendId, Integer fstIsRead,
                                                    Integer fstAID, Integer fstIsPuase, Integer fstRepStateOne,
                                                    Integer fstRepStateTwo, Integer fstRepInStable,
                                                    Integer fstPostState, Integer fstRepType, Integer fstAType,
                                                    Integer fstUpdateState, String fstParameter, String fstContent,
                                                    String fstDate, String fstTime, String fstRingCode,
                                                    String fstRingDesc, String fstTags, String fstSourceDesc,
                                                    String fstSourceDescSpare, String fstRepeatDate,
                                                    String fstRepStartDate, String fstRpNextCreatedTime,
                                                    String fstRepLastCreatedTime, String fstRepInitialCreatedTime,
                                                    String fstRepDateOne, String fstRepDateTwo,
                                                    String fstRecommendName, String fstWebURL, String fstImagePath,
                                                    String fstParReamrk, String fstCreateTime, String fstUpdateTime,
                                                    String fstReamrk1, String fstReamrk2, String fstReamrk3,
                                                    String fstReamrk4, String fstReamrk5) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int fstID = getLocalId(1, "CLFindScheduleTable",
                CLFindScheduleTable.fstID);
        int fstSchID = getLocalId(-1, "CLFindScheduleTable",
                CLFindScheduleTable.fstSchID);
        String content = fstContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String insertStr = "insert into CLFindScheduleTable(fstID,fstFID,fstSchID, fstType,"
                + "fstBeforeTime, fstIsAlarm, fstDisplayTime,fstColorType, fstIsPostpone,"
                + "fstIsImportant,fstIsEnd, fstSourceType,fstRepeatId, fstOpenState, "
                + "fstRepeatLink,fstRecommendId, fstIsRead, fstAID,fstIsPuase, fstRepStateOne,"
                + "fstRepStateTwo,fstRepInStable,fstPostState, fstRepType,fstAType, "
                + "fstUpdateState, fstParameter,fstContent, fstDate, fstTime,fstRingCode,"
                + " fstRingDesc, fstTags,fstSourceDesc, fstSourceDescSpare,fstRepeatDate,"
                + " fstRepStartDate,fstRpNextCreatedTime, fstRepLastCreatedTime,"
                + "fstRepInitialCreatedTime, fstRepDateOne,fstRepDateTwo, fstRecommendName,"
                + " fstWebURL,fstImagePath,fstParReamrk, fstCreateTime,fstUpdateTime, "
                + "fstReamrk1,fstReamrk2,fstReamrk3, fstReamrk4,fstReamrk5) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] mValue = new Object[]{fstID, fstFID, fstSchID, fstType,
                fstBeforeTime, fstIsAlarm, fstDisplayTime, fstColorType,
                fstIsPostpone, fstIsImportant, fstIsEnd, fstSourceType,
                fstRepeatId, fstOpenState, fstRepeatLink, fstRecommendId,
                fstIsRead, fstAID, fstIsPuase, fstRepStateOne, fstRepStateTwo,
                fstRepInStable, fstPostState, fstRepType, fstAType,
                fstUpdateState,
                StringUtils.getIsStringEqulesNull(fstParameter),
                StringUtils.getIsStringEqulesNull(content),
                StringUtils.getIsStringEqulesNull(fstDate),
                StringUtils.getIsStringEqulesNull(fstTime),
                StringUtils.getIsStringEqulesNull(fstRingCode),
                StringUtils.getIsStringEqulesNull(fstRingDesc),
                StringUtils.getIsStringEqulesNull(fstTags),
                StringUtils.getIsStringEqulesNull(fstSourceDesc),
                StringUtils.getIsStringEqulesNull(fstSourceDescSpare),
                StringUtils.getIsStringEqulesNull(fstRepeatDate),
                StringUtils.getIsStringEqulesNull(fstRepStartDate),
                StringUtils.getIsStringEqulesNull(fstRpNextCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepLastCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepInitialCreatedTime),
                StringUtils.getIsStringEqulesNull(fstRepDateOne),
                StringUtils.getIsStringEqulesNull(fstRepDateTwo),
                StringUtils.getIsStringEqulesNull(fstRecommendName),
                StringUtils.getIsStringEqulesNull(fstWebURL),
                StringUtils.getIsStringEqulesNull(fstImagePath),
                StringUtils.getIsStringEqulesNull(fstParReamrk),
                StringUtils.getIsStringEqulesNull(fstCreateTime),
                StringUtils.getIsStringEqulesNull(fstUpdateTime),
                StringUtils.getIsStringEqulesNull(fstReamrk1),
                StringUtils.getIsStringEqulesNull(fstReamrk2),
                StringUtils.getIsStringEqulesNull(fstReamrk3),
                StringUtils.getIsStringEqulesNull(fstReamrk4),
                StringUtils.getIsStringEqulesNull(fstReamrk5)};
        // String sql = "insert into CLFindScheduleTable values(" + fstID + ","
        // + fstFID + "," + fstSchID + "," + fstType + "," + fstBeforeTime
        // + "," + fstIsAlarm + "," + fstDisplayTime + "," + fstColorType
        // + "," + fstIsPostpone + "," + fstIsImportant + "," + fstIsEnd
        // + "," + fstSourceType + "," + fstRepeatId + "," + fstOpenState
        // + "," + fstRepeatLink + "," + fstRecommendId + "," + fstIsRead
        // + "," + fstAID + "," + fstIsPuase + "," + fstRepStateOne + ","
        // + fstRepStateTwo + "," + fstRepInStable + "," + fstPostState
        // + "," + fstRepType + "," + fstAType + "," + fstUpdateState
        // + ",'" + fstParameter + "','" + content + "' , '" + fstDate
        // + "' , '" + fstTime + "' , '" + fstRingCode + "', '"
        // + fstRingDesc + "','" + fstTags + "','" + fstSourceDesc + "','"
        // + fstSourceDescSpare + "','" + fstRepeatDate + "','"
        // + fstRepStartDate + "','" + fstRpNextCreatedTime + "','"
        // + fstRepLastCreatedTime + "','" + fstRepInitialCreatedTime
        // + "','" + fstRepDateOne + "','" + fstRepDateTwo + "','"
        // + fstRecommendName + "','" + fstWebURL + "','" + fstImagePath
        // + "','" + fstParReamrk + "','" + fstCreateTime + "','"
        // + fstUpdateTime + "','" + fstReamrk1 + "','" + fstReamrk2
        // + "','" + fstReamrk3 + "','" + fstReamrk4 + "','" + fstReamrk5
        // + "')";
        try {
            sqldb.execSQL(insertStr, mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 与网上数据进行同步，修改本地数据
     */
    public boolean updateNewFocusFromIntenetData(Integer fstFID,
                                                 Integer fstSchID, Integer fstType, Integer fstBeforeTime,
                                                 Integer fstIsAlarm, Integer fstDisplayTime, Integer fstColorType,
                                                 Integer fstIsPostpone, Integer fstIsImportant, Integer fstIsEnd,
                                                 Integer fstSourceType, Integer fstRepeatId, Integer fstOpenState,
                                                 Integer fstRepeatLink, Integer fstRecommendId, Integer fstIsRead,
                                                 Integer fstAID, Integer fstIsPuase, Integer fstRepStateOne,
                                                 Integer fstRepStateTwo, Integer fstRepInStable,
                                                 Integer fstPostState, Integer fstRepType, Integer fstAType,
                                                 Integer fstUpdateState, String fstParameter, String fstContent,
                                                 String fstDate, String fstTime, String fstRingCode,
                                                 String fstRingDesc, String fstTags, String fstSourceDesc,
                                                 String fstSourceDescSpare, String fstRepeatDate,
                                                 String fstRepStartDate, String fstRpNextCreatedTime,
                                                 String fstRepLastCreatedTime, String fstRepInitialCreatedTime,
                                                 String fstRepDateOne, String fstRepDateTwo,
                                                 String fstRecommendName, String fstWebURL, String fstImagePath,
                                                 String fstParReamrk, String fstCreateTime, String fstUpdateTime,
                                                 String fstReamrk1, String fstReamrk2, String fstReamrk3,
                                                 String fstReamrk4, String fstReamrk5) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String content = fstContent
                .replaceAll(
                        "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
                        "");
        String sql = "";
        Object[] mValue = null;
        if (fstType == 0) {
            sql = "update CLFindScheduleTable set fstFID=?,fstSchID=?, fstType=?,"
                    + "fstBeforeTime=?, fstIsAlarm=?, fstDisplayTime=?,fstColorType=?, fstIsPostpone=?,"
                    + "fstIsImportant=?,fstIsEnd=?, fstSourceType=?,fstRepeatId=?, fstOpenState=?, "
                    + "fstRepeatLink=?,fstRecommendId=?, fstIsRead=?, fstAID=?,fstIsPuase=?, fstRepStateOne=?,"
                    + "fstRepStateTwo=?,fstRepInStable=?,fstPostState=?, fstRepType=?,fstAType=?, "
                    + "fstUpdateState=?, fstParameter=?,fstContent=?, fstDate=?, fstTime=?,fstRingCode=?,"
                    + " fstRingDesc=?, fstTags=?,fstSourceDesc=?, fstSourceDescSpare=?,fstRepeatDate=?,"
                    + " fstRepStartDate=?,fstRpNextCreatedTime=?, fstRepLastCreatedTime=?,"
                    + "fstRepInitialCreatedTime=?, fstRepDateOne=?,fstRepDateTwo=?, fstRecommendName=?,"
                    + " fstWebURL=?,fstImagePath=?,fstParReamrk=?, fstCreateTime=?,fstUpdateTime=?, "
                    + "fstReamrk1=?,fstReamrk2=?,fstReamrk3=?, fstReamrk4=?,fstReamrk5=? "
                    + " where "
                    + CLFindScheduleTable.fstSchID
                    + " = "
                    + fstSchID
                    + " and "
                    + CLFindScheduleTable.fstUpdateState
                    + " != "
                    + 3
                    + " and "
                    + CLFindScheduleTable.fstRepeatId
                    + " = " + 0;
            mValue = new Object[]{
                    fstFID,
                    fstSchID,
                    fstType,
                    fstBeforeTime,
                    fstIsAlarm,
                    fstDisplayTime,
                    fstColorType,
                    fstIsPostpone,
                    fstIsImportant,
                    fstIsEnd,
                    fstSourceType,
                    fstRepeatId,
                    fstOpenState,
                    fstRepeatLink,
                    fstRecommendId,
                    fstIsRead,
                    fstAID,
                    fstIsPuase,
                    fstRepStateOne,
                    fstRepStateTwo,
                    fstRepInStable,
                    fstPostState,
                    fstRepType,
                    fstAType,
                    fstUpdateState,
                    StringUtils.getIsStringEqulesNull(fstParameter),
                    StringUtils.getIsStringEqulesNull(content),
                    StringUtils.getIsStringEqulesNull(fstDate),
                    StringUtils.getIsStringEqulesNull(fstTime),
                    StringUtils.getIsStringEqulesNull(fstRingCode),
                    StringUtils.getIsStringEqulesNull(fstRingDesc),
                    StringUtils.getIsStringEqulesNull(fstTags),
                    StringUtils.getIsStringEqulesNull(fstSourceDesc),
                    StringUtils.getIsStringEqulesNull(fstSourceDescSpare),
                    StringUtils.getIsStringEqulesNull(fstRepeatDate),
                    StringUtils.getIsStringEqulesNull(fstRepStartDate),
                    StringUtils.getIsStringEqulesNull(fstRpNextCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepLastCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepInitialCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepDateOne),
                    StringUtils.getIsStringEqulesNull(fstRepDateTwo),
                    StringUtils.getIsStringEqulesNull(fstRecommendName),
                    StringUtils.getIsStringEqulesNull(fstWebURL),
                    StringUtils.getIsStringEqulesNull(fstImagePath),
                    StringUtils.getIsStringEqulesNull(fstParReamrk),
                    StringUtils.getIsStringEqulesNull(fstCreateTime),
                    StringUtils.getIsStringEqulesNull(fstUpdateTime),
                    StringUtils.getIsStringEqulesNull(fstReamrk1),
                    StringUtils.getIsStringEqulesNull(fstReamrk2),
                    StringUtils.getIsStringEqulesNull(fstReamrk3),
                    StringUtils.getIsStringEqulesNull(fstReamrk4),
                    StringUtils.getIsStringEqulesNull(fstReamrk5)};
            // sql = "update " + CLFindScheduleTable.CLFindScheduleTable +
            // " set "
            // + CLFindScheduleTable.fstFID + " = " + fstFID + ", "
            // + CLFindScheduleTable.fstSchID + " = " + fstSchID + ", "
            // + CLFindScheduleTable.fstType + " = " + fstType + ", "
            // + CLFindScheduleTable.fstBeforeTime + " = " + fstBeforeTime
            // + ", " + CLFindScheduleTable.fstIsAlarm + " = "
            // + fstIsAlarm + ", " + CLFindScheduleTable.fstDisplayTime
            // + " = " + fstDisplayTime + ", "
            // + CLFindScheduleTable.fstColorType + " = " + fstColorType
            // + ", " + CLFindScheduleTable.fstIsPostpone + " = "
            // + fstIsPostpone + ", " + CLFindScheduleTable.fstIsImportant
            // + " = '" + fstIsImportant + "', "
            // + CLFindScheduleTable.fstIsEnd + " = " + fstIsEnd + ", "
            // + CLFindScheduleTable.fstSourceType + " = " + fstSourceType
            // + ", " + CLFindScheduleTable.fstRepeatId + " = "
            // + fstRepeatId + ", " + CLFindScheduleTable.fstOpenState
            // + " = " + fstOpenState + ", "
            // + CLFindScheduleTable.fstRepeatLink + " = " + fstRepeatLink
            // + ", " + CLFindScheduleTable.fstRecommendId + " = "
            // + fstRecommendId + ", " + CLFindScheduleTable.fstIsRead
            // + " = " + fstIsRead + ", " + CLFindScheduleTable.fstAID
            // + " = " + fstAID + ", " + CLFindScheduleTable.fstIsPuase
            // + " = " + fstIsPuase + ", "
            // + CLFindScheduleTable.fstRepStateOne + " = "
            // + fstRepStateOne + ", "
            // + CLFindScheduleTable.fstRepStateTwo + " = "
            // + fstRepStateTwo + ", "
            // + CLFindScheduleTable.fstRepInStable + " = "
            // + fstRepInStable + ", " + CLFindScheduleTable.fstPostState
            // + " = " + fstPostState + ", "
            // + CLFindScheduleTable.fstRepType + " = " + fstRepType
            // + ", " + CLFindScheduleTable.fstAType + " = " + fstAType
            // + ", " + CLFindScheduleTable.fstUpdateState + " = "
            // + fstUpdateState + ", " + CLFindScheduleTable.fstParameter
            // + " = '" + fstParameter + "', "
            // + CLFindScheduleTable.fstContent + " = '" + content + "', "
            // + CLFindScheduleTable.fstDate + " = '" + fstDate + "', "
            // + CLFindScheduleTable.fstTime + " = '" + fstTime + "', "
            // + CLFindScheduleTable.fstRingCode + " = '" + fstRingCode
            // + "', " + CLFindScheduleTable.fstRingDesc + " = '"
            // + fstRingDesc + "', " + CLFindScheduleTable.fstTags
            // + " = '" + fstTags + "', "
            // + CLFindScheduleTable.fstSourceDesc + " = '"
            // + fstSourceDesc + "', "
            // + CLFindScheduleTable.fstSourceDescSpare + " = '"
            // + fstSourceDescSpare + "', "
            // + CLFindScheduleTable.fstRepeatDate + " = '"
            // + fstRepeatDate + "' , "
            // + CLFindScheduleTable.fstRepStartDate + " = '"
            // + fstRepStartDate + "' , "
            // + CLFindScheduleTable.fstRpNextCreatedTime + " = '"
            // + fstRpNextCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepLastCreatedTime + " = '"
            // + fstRepLastCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepInitialCreatedTime + " = '"
            // + fstRepInitialCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepDateOne + " = '"
            // + fstRepDateOne + "' , "
            // + CLFindScheduleTable.fstRepDateTwo + " = '"
            // + fstRepDateTwo + "' , "
            // + CLFindScheduleTable.fstRecommendName + " = '"
            // + fstRecommendName + "' , " + CLFindScheduleTable.fstWebURL
            // + " = '" + fstWebURL + "' , "
            // + CLFindScheduleTable.fstImagePath + " = '" + fstImagePath
            // + "' , " + CLFindScheduleTable.fstParReamrk + " = '"
            // + fstParReamrk + "' , " + CLFindScheduleTable.fstCreateTime
            // + " = '" + fstCreateTime + "' , "
            // + CLFindScheduleTable.fstUpdateTime + " = '"
            // + fstUpdateTime + "' , " + CLFindScheduleTable.fstReamrk1
            // + " = '" + fstReamrk1 + "' , "
            // + CLFindScheduleTable.fstReamrk2 + " = '" + fstReamrk2
            // + "' , " + CLFindScheduleTable.fstReamrk3 + " = '"
            // + fstReamrk3 + "' , " + CLFindScheduleTable.fstReamrk4
            // + " = '" + fstReamrk4 + "' , "
            // + CLFindScheduleTable.fstReamrk5 + " = '" + fstReamrk5
            // + "' where " + CLFindScheduleTable.fstSchID + " = "
            // + fstSchID + " and " + CLFindScheduleTable.fstUpdateState
            // + " != " + 3 + " and " + CLFindScheduleTable.fstRepeatId
            // + " = " + 0;
        } else if (fstType == 2) {//
            int schID = getLocalId(-1, "CLFindScheduleTable",
                    CLFindScheduleTable.fstSchID);
            sql = "update CLFindScheduleTable set fstFID=?,fstSchID=?, fstType=?,"
                    + "fstBeforeTime=?, fstIsAlarm=?, fstDisplayTime=?,fstColorType=?, fstIsPostpone=?,"
                    + "fstIsImportant=?,fstIsEnd=?, fstSourceType=?,fstRepeatId=?, fstOpenState=?, "
                    + "fstRepeatLink=?,fstRecommendId=?, fstIsRead=?, fstAID=?,fstIsPuase=?, fstRepStateOne=?,"
                    + "fstRepStateTwo=?,fstRepInStable=?,fstPostState=?, fstRepType=?,fstAType=?, "
                    + "fstUpdateState=?, fstParameter=?,fstContent=?, fstDate=?, fstTime=?,fstRingCode=?,"
                    + " fstRingDesc=?, fstTags=?,fstSourceDesc=?, fstSourceDescSpare=?,fstRepeatDate=?,"
                    + " fstRepStartDate=?,fstRpNextCreatedTime=?, fstRepLastCreatedTime=?,"
                    + "fstRepInitialCreatedTime=?, fstRepDateOne=?,fstRepDateTwo=?, fstRecommendName=?,"
                    + " fstWebURL=?,fstImagePath=?,fstParReamrk=?, fstCreateTime=?,fstUpdateTime=?, "
                    + "fstReamrk1=?,fstReamrk2=?,fstReamrk3=?, fstReamrk4=?,fstReamrk5=? "
                    + " where "
                    + CLFindScheduleTable.fstRepeatId
                    + " = "
                    + fstRepeatId
                    + " and "
                    + CLFindScheduleTable.fstUpdateState + " != " + 3;
            mValue = new Object[]{
                    fstFID,
                    schID,
                    1,
                    fstBeforeTime,
                    fstIsAlarm,
                    fstDisplayTime,
                    fstColorType,
                    fstIsPostpone,
                    fstIsImportant,
                    fstIsEnd,
                    fstSourceType,
                    fstRepeatId,
                    fstOpenState,
                    fstRepeatLink,
                    fstRecommendId,
                    fstIsRead,
                    fstAID,
                    fstIsPuase,
                    fstRepStateOne,
                    fstRepStateTwo,
                    fstRepInStable,
                    fstPostState,
                    fstRepType,
                    fstAType,
                    fstUpdateState,
                    StringUtils.getIsStringEqulesNull(fstParameter),
                    StringUtils.getIsStringEqulesNull(content),
                    StringUtils.getIsStringEqulesNull(fstDate),
                    StringUtils.getIsStringEqulesNull(fstTime),
                    StringUtils.getIsStringEqulesNull(fstRingCode),
                    StringUtils.getIsStringEqulesNull(fstRingDesc),
                    StringUtils.getIsStringEqulesNull(fstTags),
                    StringUtils.getIsStringEqulesNull(fstSourceDesc),
                    StringUtils.getIsStringEqulesNull(fstSourceDescSpare),
                    StringUtils.getIsStringEqulesNull(fstRepeatDate),
                    StringUtils.getIsStringEqulesNull(fstRepStartDate),
                    StringUtils.getIsStringEqulesNull(fstRpNextCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepLastCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepInitialCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepDateOne),
                    StringUtils.getIsStringEqulesNull(fstRepDateTwo),
                    StringUtils.getIsStringEqulesNull(fstRecommendName),
                    StringUtils.getIsStringEqulesNull(fstWebURL),
                    StringUtils.getIsStringEqulesNull(fstImagePath),
                    StringUtils.getIsStringEqulesNull(fstParReamrk),
                    StringUtils.getIsStringEqulesNull(fstCreateTime),
                    StringUtils.getIsStringEqulesNull(fstUpdateTime),
                    StringUtils.getIsStringEqulesNull(fstReamrk1),
                    StringUtils.getIsStringEqulesNull(fstReamrk2),
                    StringUtils.getIsStringEqulesNull(fstReamrk3),
                    StringUtils.getIsStringEqulesNull(fstReamrk4),
                    StringUtils.getIsStringEqulesNull(fstReamrk5)};
            // sql = "update " + CLFindScheduleTable.CLFindScheduleTable +
            // " set "
            // + CLFindScheduleTable.fstFID + " = " + fstFID + ", "
            // + CLFindScheduleTable.fstSchID + " = " + schID + ", "
            // + CLFindScheduleTable.fstType + " = " + 1 + ", "
            // + CLFindScheduleTable.fstBeforeTime + " = " + fstBeforeTime
            // + ", " + CLFindScheduleTable.fstIsAlarm + " = "
            // + fstIsAlarm + ", " + CLFindScheduleTable.fstDisplayTime
            // + " = " + fstDisplayTime + ", "
            // + CLFindScheduleTable.fstColorType + " = " + fstColorType
            // + ", " + CLFindScheduleTable.fstIsPostpone + " = "
            // + fstIsPostpone + ", " + CLFindScheduleTable.fstIsImportant
            // + " = '" + fstIsImportant + "', "
            // + CLFindScheduleTable.fstIsEnd + " = " + fstIsEnd + ", "
            // + CLFindScheduleTable.fstSourceType + " = " + fstSourceType
            // + ", " + CLFindScheduleTable.fstRepeatId + " = "
            // + fstRepeatId + ", " + CLFindScheduleTable.fstOpenState
            // + " = " + fstOpenState + ", "
            // + CLFindScheduleTable.fstRepeatLink + " = " + fstRepeatLink
            // + ", " + CLFindScheduleTable.fstRecommendId + " = "
            // + fstRecommendId + ", " + CLFindScheduleTable.fstIsRead
            // + " = " + fstIsRead + ", " + CLFindScheduleTable.fstAID
            // + " = " + fstAID + ", " + CLFindScheduleTable.fstIsPuase
            // + " = " + fstIsPuase + ", "
            // + CLFindScheduleTable.fstRepStateOne + " = "
            // + fstRepStateOne + ", "
            // + CLFindScheduleTable.fstRepStateTwo + " = "
            // + fstRepStateTwo + ", "
            // + CLFindScheduleTable.fstRepInStable + " = "
            // + fstRepInStable + ", " + CLFindScheduleTable.fstPostState
            // + " = " + fstPostState + ", "
            // + CLFindScheduleTable.fstRepType + " = " + fstRepType
            // + ", " + CLFindScheduleTable.fstAType + " = " + fstAType
            // + ", " + CLFindScheduleTable.fstUpdateState + " = "
            // + fstUpdateState + ", " + CLFindScheduleTable.fstParameter
            // + " = '" + fstParameter + "', "
            // + CLFindScheduleTable.fstContent + " = '" + content + "', "
            // + CLFindScheduleTable.fstDate + " = '" + fstDate + "', "
            // + CLFindScheduleTable.fstTime + " = '" + fstTime + "', "
            // + CLFindScheduleTable.fstRingCode + " = '" + fstRingCode
            // + "', " + CLFindScheduleTable.fstRingDesc + " = '"
            // + fstRingDesc + "', " + CLFindScheduleTable.fstTags
            // + " = '" + fstTags + "', "
            // + CLFindScheduleTable.fstSourceDesc + " = '"
            // + fstSourceDesc + "', "
            // + CLFindScheduleTable.fstSourceDescSpare + " = '"
            // + fstSourceDescSpare + "', "
            // + CLFindScheduleTable.fstRepeatDate + " = '"
            // + fstRepeatDate + "' , "
            // + CLFindScheduleTable.fstRepStartDate + " = '"
            // + fstRepStartDate + "' , "
            // + CLFindScheduleTable.fstRpNextCreatedTime + " = '"
            // + fstRpNextCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepLastCreatedTime + " = '"
            // + fstRepLastCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepInitialCreatedTime + " = '"
            // + fstRepInitialCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepDateOne + " = '"
            // + fstRepDateOne + "' , "
            // + CLFindScheduleTable.fstRepDateTwo + " = '"
            // + fstRepDateTwo + "' , "
            // + CLFindScheduleTable.fstRecommendName + " = '"
            // + fstRecommendName + "' , " + CLFindScheduleTable.fstWebURL
            // + " = '" + fstWebURL + "' , "
            // + CLFindScheduleTable.fstImagePath + " = '" + fstImagePath
            // + "' , " + CLFindScheduleTable.fstParReamrk + " = '"
            // + fstParReamrk + "' , " + CLFindScheduleTable.fstCreateTime
            // + " = '" + fstCreateTime + "' , "
            // + CLFindScheduleTable.fstUpdateTime + " = '"
            // + fstUpdateTime + "' , " + CLFindScheduleTable.fstReamrk1
            // + " = '" + fstReamrk1 + "' , "
            // + CLFindScheduleTable.fstReamrk2 + " = '" + fstReamrk2
            // + "' , " + CLFindScheduleTable.fstReamrk3 + " = '"
            // + fstReamrk3 + "' , " + CLFindScheduleTable.fstReamrk4
            // + " = '" + fstReamrk4 + "' , "
            // + CLFindScheduleTable.fstReamrk5 + " = '" + fstReamrk5
            // + "' where " + CLFindScheduleTable.fstRepeatId + " = "
            // + fstRepeatId + " and "
            // + CLFindScheduleTable.fstUpdateState + " != " + 3;
        } else {
            sql = "update CLFindScheduleTable set fstFID=?,fstSchID=?, fstType=?,"
                    + "fstBeforeTime=?, fstIsAlarm=?, fstDisplayTime=?,fstColorType=?, fstIsPostpone=?,"
                    + "fstIsImportant=?,fstIsEnd=?, fstSourceType=?,fstRepeatId=?, fstOpenState=?, "
                    + "fstRepeatLink=?,fstRecommendId=?, fstIsRead=?, fstAID=?,fstIsPuase=?, fstRepStateOne=?,"
                    + "fstRepStateTwo=?,fstRepInStable=?,fstPostState=?, fstRepType=?,fstAType=?, "
                    + "fstUpdateState=?, fstParameter=?,fstContent=?, fstDate=?, fstTime=?,fstRingCode=?,"
                    + " fstRingDesc=?, fstTags=?,fstSourceDesc=?, fstSourceDescSpare=?,fstRepeatDate=?,"
                    + " fstRepStartDate=?,fstRpNextCreatedTime=?, fstRepLastCreatedTime=?,"
                    + "fstRepInitialCreatedTime=?, fstRepDateOne=?,fstRepDateTwo=?, fstRecommendName=?,"
                    + " fstWebURL=?,fstImagePath=?,fstParReamrk=?, fstCreateTime=?,fstUpdateTime=?, "
                    + "fstReamrk1=?,fstReamrk2=?,fstReamrk3=?, fstReamrk4=?,fstReamrk5=? "
                    + " where "
                    + CLFindScheduleTable.fstRepeatId
                    + " = "
                    + fstRepeatId
                    + " and "
                    + CLFindScheduleTable.fstUpdateState + " != " + 3;
            mValue = new Object[]{
                    fstFID,
                    fstSchID,
                    fstType,
                    fstBeforeTime,
                    fstIsAlarm,
                    fstDisplayTime,
                    fstColorType,
                    fstIsPostpone,
                    fstIsImportant,
                    fstIsEnd,
                    fstSourceType,
                    fstRepeatId,
                    fstOpenState,
                    fstRepeatLink,
                    fstRecommendId,
                    fstIsRead,
                    fstAID,
                    fstIsPuase,
                    fstRepStateOne,
                    fstRepStateTwo,
                    fstRepInStable,
                    fstPostState,
                    fstRepType,
                    fstAType,
                    fstUpdateState,
                    StringUtils.getIsStringEqulesNull(fstParameter),
                    StringUtils.getIsStringEqulesNull(content),
                    StringUtils.getIsStringEqulesNull(fstDate),
                    StringUtils.getIsStringEqulesNull(fstTime),
                    StringUtils.getIsStringEqulesNull(fstRingCode),
                    StringUtils.getIsStringEqulesNull(fstRingDesc),
                    StringUtils.getIsStringEqulesNull(fstTags),
                    StringUtils.getIsStringEqulesNull(fstSourceDesc),
                    StringUtils.getIsStringEqulesNull(fstSourceDescSpare),
                    StringUtils.getIsStringEqulesNull(fstRepeatDate),
                    StringUtils.getIsStringEqulesNull(fstRepStartDate),
                    StringUtils.getIsStringEqulesNull(fstRpNextCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepLastCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepInitialCreatedTime),
                    StringUtils.getIsStringEqulesNull(fstRepDateOne),
                    StringUtils.getIsStringEqulesNull(fstRepDateTwo),
                    StringUtils.getIsStringEqulesNull(fstRecommendName),
                    StringUtils.getIsStringEqulesNull(fstWebURL),
                    StringUtils.getIsStringEqulesNull(fstImagePath),
                    StringUtils.getIsStringEqulesNull(fstParReamrk),
                    StringUtils.getIsStringEqulesNull(fstCreateTime),
                    StringUtils.getIsStringEqulesNull(fstUpdateTime),
                    StringUtils.getIsStringEqulesNull(fstReamrk1),
                    StringUtils.getIsStringEqulesNull(fstReamrk2),
                    StringUtils.getIsStringEqulesNull(fstReamrk3),
                    StringUtils.getIsStringEqulesNull(fstReamrk4),
                    StringUtils.getIsStringEqulesNull(fstReamrk5)};
            // sql = "update " + CLFindScheduleTable.CLFindScheduleTable +
            // " set "
            // + CLFindScheduleTable.fstFID + " = " + fstFID + ", "
            // + CLFindScheduleTable.fstSchID + " = " + fstSchID + ", "
            // + CLFindScheduleTable.fstType + " = " + fstType + ", "
            // + CLFindScheduleTable.fstBeforeTime + " = " + fstBeforeTime
            // + ", " + CLFindScheduleTable.fstIsAlarm + " = "
            // + fstIsAlarm + ", " + CLFindScheduleTable.fstDisplayTime
            // + " = " + fstDisplayTime + ", "
            // + CLFindScheduleTable.fstColorType + " = " + fstColorType
            // + ", " + CLFindScheduleTable.fstIsPostpone + " = "
            // + fstIsPostpone + ", " + CLFindScheduleTable.fstIsImportant
            // + " = '" + fstIsImportant + "', "
            // + CLFindScheduleTable.fstIsEnd + " = " + fstIsEnd + ", "
            // + CLFindScheduleTable.fstSourceType + " = " + fstSourceType
            // + ", " + CLFindScheduleTable.fstRepeatId + " = "
            // + fstRepeatId + ", " + CLFindScheduleTable.fstOpenState
            // + " = " + fstOpenState + ", "
            // + CLFindScheduleTable.fstRepeatLink + " = " + fstRepeatLink
            // + ", " + CLFindScheduleTable.fstRecommendId + " = "
            // + fstRecommendId + ", " + CLFindScheduleTable.fstIsRead
            // + " = " + fstIsRead + ", " + CLFindScheduleTable.fstAID
            // + " = " + fstAID + ", " + CLFindScheduleTable.fstIsPuase
            // + " = " + fstIsPuase + ", "
            // + CLFindScheduleTable.fstRepStateOne + " = "
            // + fstRepStateOne + ", "
            // + CLFindScheduleTable.fstRepStateTwo + " = "
            // + fstRepStateTwo + ", "
            // + CLFindScheduleTable.fstRepInStable + " = "
            // + fstRepInStable + ", " + CLFindScheduleTable.fstPostState
            // + " = " + fstPostState + ", "
            // + CLFindScheduleTable.fstRepType + " = " + fstRepType
            // + ", " + CLFindScheduleTable.fstAType + " = " + fstAType
            // + ", " + CLFindScheduleTable.fstUpdateState + " = "
            // + fstUpdateState + ", " + CLFindScheduleTable.fstParameter
            // + " = '" + fstParameter + "', "
            // + CLFindScheduleTable.fstContent + " = '" + content + "', "
            // + CLFindScheduleTable.fstDate + " = '" + fstDate + "', "
            // + CLFindScheduleTable.fstTime + " = '" + fstTime + "', "
            // + CLFindScheduleTable.fstRingCode + " = '" + fstRingCode
            // + "', " + CLFindScheduleTable.fstRingDesc + " = '"
            // + fstRingDesc + "', " + CLFindScheduleTable.fstTags
            // + " = '" + fstTags + "', "
            // + CLFindScheduleTable.fstSourceDesc + " = '"
            // + fstSourceDesc + "', "
            // + CLFindScheduleTable.fstSourceDescSpare + " = '"
            // + fstSourceDescSpare + "', "
            // + CLFindScheduleTable.fstRepeatDate + " = '"
            // + fstRepeatDate + "' , "
            // + CLFindScheduleTable.fstRepStartDate + " = '"
            // + fstRepStartDate + "' , "
            // + CLFindScheduleTable.fstRpNextCreatedTime + " = '"
            // + fstRpNextCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepLastCreatedTime + " = '"
            // + fstRepLastCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepInitialCreatedTime + " = '"
            // + fstRepInitialCreatedTime + "' , "
            // + CLFindScheduleTable.fstRepDateOne + " = '"
            // + fstRepDateOne + "' , "
            // + CLFindScheduleTable.fstRepDateTwo + " = '"
            // + fstRepDateTwo + "' , "
            // + CLFindScheduleTable.fstRecommendName + " = '"
            // + fstRecommendName + "' , " + CLFindScheduleTable.fstWebURL
            // + " = '" + fstWebURL + "' , "
            // + CLFindScheduleTable.fstImagePath + " = '" + fstImagePath
            // + "' , " + CLFindScheduleTable.fstParReamrk + " = '"
            // + fstParReamrk + "' , " + CLFindScheduleTable.fstCreateTime
            // + " = '" + fstCreateTime + "' , "
            // + CLFindScheduleTable.fstUpdateTime + " = '"
            // + fstUpdateTime + "' , " + CLFindScheduleTable.fstReamrk1
            // + " = '" + fstReamrk1 + "' , "
            // + CLFindScheduleTable.fstReamrk2 + " = '" + fstReamrk2
            // + "' , " + CLFindScheduleTable.fstReamrk3 + " = '"
            // + fstReamrk3 + "' , " + CLFindScheduleTable.fstReamrk4
            // + " = '" + fstReamrk4 + "' , "
            // + CLFindScheduleTable.fstReamrk5 + " = '" + fstReamrk5
            // + "' where " + CLFindScheduleTable.fstRepeatId + " = "
            // + fstRepeatId + " and "
            // + CLFindScheduleTable.fstUpdateState + " != " + 3;
        }
        try {
            sqldb.execSQL(sql, mValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, String>> QueryNewFocusData(int type, int fstFID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map<String, String> noticeMap = null;
        String yestoday;// 昨天
        String today;// 今天
        String tomorrow;// 明天
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) - 1);
        yestoday = DateUtil.formatDate(calendar.getTime());
        calendar.setTime(new Date());
        today = DateUtil.formatDate(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow = DateUtil.formatDate(calendar.getTime());
        String sql = "";
        switch (type) {
            case -1:// 上传重复数据查询 新增
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstUpdateState
                        + " = "
                        + 1
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 1
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case -2:// 上传重复数据查询 修改
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstUpdateState
                        + " = "
                        + 2
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 1
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case -3:// 上传重复数据查询 删除
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstUpdateState
                        + " = "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 1
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case -4:// 上传日程数据查询 新增
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstUpdateState
                        + " = "
                        + 1
                        + " and ("
                        + CLFindScheduleTable.fstRepeatId
                        + " = "
                        + 0
                        + " or ("
                        + CLFindScheduleTable.fstRepeatId
                        + " != "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstRepeatLink
                        + " != "
                        + 1
                        + ")) order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case -5:// 上传日程数据查询 修改
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstUpdateState
                        + " = "
                        + 2
                        + " and ("
                        + CLFindScheduleTable.fstRepeatId
                        + " = "
                        + 0
                        + " or ("
                        + CLFindScheduleTable.fstRepeatId
                        + " != "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstRepeatLink
                        + " != "
                        + 1
                        + ")) order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case -6:// 上传日程数据查询 删除
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstUpdateState
                        + " = "
                        + 3
                        + " and ("
                        + CLFindScheduleTable.fstRepeatId
                        + " = "
                        + 0
                        + " or ("
                        + CLFindScheduleTable.fstRepeatId
                        + " != "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstRepeatLink
                        + " != "
                        + 1
                        + ")) order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 0:
                sql = "select * from " + CLFindScheduleTable.CLFindScheduleTable
                        + " where " + CLFindScheduleTable.fstUpdateState + " != "
                        + 3 + " and " + CLFindScheduleTable.fstRepeatId + " = " + 0
                        + " order by repTime asc";
                break;
            case 1:// 今天
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + "fstDate =='"
                        + today
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 2:// 明天
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + "fstDate =='"
                        + tomorrow
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 3:// 明天之后的
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + "fstDate >'"
                        + tomorrow
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 4:// 昨天
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + "fstDate =='"
                        + yestoday
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 5:// 昨天之前
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + "fstDate <'"
                        + yestoday
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 6:// 查询表中母记事
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstRepeatId
                        + " != "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 1
                        + " and "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 7:// 以前+未结束+顺延+普通记事
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + "fstDate <'"
                        + today
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " and "
                        + CLFindScheduleTable.fstIsEnd
                        + " = "
                        + 0
                        + " and "
                        + CLFindScheduleTable.fstIsPostpone
                        + " = "
                        + 1
                        + " and ("
                        + CLFindScheduleTable.fstRepeatId
                        + " = "
                        + 0
                        + " or ("
                        + CLFindScheduleTable.fstRepeatId
                        + " != 0"
                        + " and "
                        + CLFindScheduleTable.fstRepeatLink
                        + " = "
                        + 0
                        + ")) order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 8:// 查询今后的所有数据
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " and "
                        + CLFindScheduleTable.fstDate
                        + " >= '"
                        + today
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            case 9:// 查询今天之前的所有数据
                sql = "select * from "
                        + CLFindScheduleTable.CLFindScheduleTable
                        + " where "
                        + CLFindScheduleTable.fstFID
                        + " = "
                        + fstFID
                        + " and "
                        + CLFindScheduleTable.fstDate
                        + " < '"
                        + today
                        + "' and "
                        + CLFindScheduleTable.fstUpdateState
                        + " != "
                        + 3
                        + " and "
                        + CLFindScheduleTable.fstType
                        + " = "
                        + 0
                        + " order by fstDate asc,fstTime asc,fstDisplayTime asc,fstID asc";
                break;
            default:
                break;
        }
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                noticeMap = new HashMap<String, String>();
                noticeMap.put(CLFindScheduleTable.fstID, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstID)));
                noticeMap.put(CLFindScheduleTable.fstType, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstType)));
                noticeMap.put(CLFindScheduleTable.fstSchID, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstSchID)));
                noticeMap.put(CLFindScheduleTable.fstFID, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstFID)));
                noticeMap
                        .put(CLFindScheduleTable.fstBeforeTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstBeforeTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstIsAlarm,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstIsAlarm)));
                noticeMap
                        .put(CLFindScheduleTable.fstDisplayTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstDisplayTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstColorType,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstColorType)));
                noticeMap
                        .put(CLFindScheduleTable.fstIsPostpone,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstIsPostpone)));
                noticeMap
                        .put(CLFindScheduleTable.fstIsImportant,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstIsImportant)));
                noticeMap.put(CLFindScheduleTable.fstIsEnd, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstIsEnd)));
                noticeMap
                        .put(CLFindScheduleTable.fstSourceType,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstSourceType)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepeatId,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepeatId)));
                noticeMap
                        .put(CLFindScheduleTable.fstOpenState,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstOpenState)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepeatLink,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepeatLink)));
                noticeMap
                        .put(CLFindScheduleTable.fstRecommendId,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRecommendId)));
                noticeMap
                        .put(CLFindScheduleTable.fstIsRead,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstIsRead)));
                noticeMap.put(CLFindScheduleTable.fstAID, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstAID)));
                noticeMap
                        .put(CLFindScheduleTable.fstIsPuase,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstIsPuase)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepStateOne,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepStateOne)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepStateTwo,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepStateTwo)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepInStable,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepInStable)));
                noticeMap
                        .put(CLFindScheduleTable.fstPostState,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstPostState)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepType,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepType)));
                noticeMap.put(CLFindScheduleTable.fstAType, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstAType)));
                noticeMap
                        .put(CLFindScheduleTable.fstUpdateState,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstUpdateState)));
                noticeMap
                        .put(CLFindScheduleTable.fstParameter,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstParameter)));
                noticeMap
                        .put(CLFindScheduleTable.fstContent,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstContent)));
                noticeMap.put(CLFindScheduleTable.fstDate, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstDate)));
                noticeMap.put(CLFindScheduleTable.fstTime, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstRingCode,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRingCode)));
                noticeMap
                        .put(CLFindScheduleTable.fstRingDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRingDesc)));
                noticeMap.put(CLFindScheduleTable.fstTags, cursor
                        .getString(cursor
                                .getColumnIndex(CLFindScheduleTable.fstTags)));
                noticeMap
                        .put(CLFindScheduleTable.fstSourceDesc,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstSourceDesc)));
                noticeMap
                        .put(CLFindScheduleTable.fstSourceDescSpare,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstSourceDescSpare)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepeatDate,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepeatDate)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepStartDate,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepStartDate)));
                noticeMap
                        .put(CLFindScheduleTable.fstRpNextCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRpNextCreatedTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepLastCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepLastCreatedTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepInitialCreatedTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepInitialCreatedTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepDateOne,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepDateOne)));
                noticeMap
                        .put(CLFindScheduleTable.fstRepDateTwo,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRepDateTwo)));
                noticeMap
                        .put(CLFindScheduleTable.fstRecommendName,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstRecommendName)));
                noticeMap
                        .put(CLFindScheduleTable.fstWebURL,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstWebURL)));
                noticeMap
                        .put(CLFindScheduleTable.fstImagePath,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstImagePath)));
                noticeMap
                        .put(CLFindScheduleTable.fstParReamrk,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstParReamrk)));
                noticeMap
                        .put(CLFindScheduleTable.fstCreateTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstCreateTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstUpdateTime,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstUpdateTime)));
                noticeMap
                        .put(CLFindScheduleTable.fstReamrk1,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstReamrk1)));
                noticeMap
                        .put(CLFindScheduleTable.fstReamrk2,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstReamrk2)));
                noticeMap
                        .put(CLFindScheduleTable.fstReamrk3,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstReamrk3)));
                noticeMap
                        .put(CLFindScheduleTable.fstReamrk4,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstReamrk4)));
                noticeMap
                        .put(CLFindScheduleTable.fstReamrk5,
                                cursor.getString(cursor
                                        .getColumnIndex(CLFindScheduleTable.fstReamrk5)));
                dataList.add(noticeMap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 查询数据库中是否有相同数据
     *
     * @return
     */
    public int CheckCountFromFocusShareData(int fstType, int fstSchID,
                                            int fstFID, int fstRepeatId) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        int result = 0;
        String sql = "";
        if (fstType == 0) {
            sql = "select count(*) from "
                    + CLFindScheduleTable.CLFindScheduleTable + " where "
                    + CLFindScheduleTable.fstSchID + " = " + fstSchID + " and "
                    + CLFindScheduleTable.fstFID + " = " + fstFID + " and "
                    + CLFindScheduleTable.fstRepeatId + " = " + 0;
        } else {
            sql = "select count(*) from "
                    + CLFindScheduleTable.CLFindScheduleTable + " where "
                    + CLFindScheduleTable.fstRepeatId + " = " + fstRepeatId
                    + " and " + CLFindScheduleTable.fstFID + " = " + fstFID;
        }
        try {
            Cursor cursor = sqldb.rawQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    /**
     * 删除本地发现分享的数据
     */
    public void deleteNewFocusShareData(int type, int fstFID, int fstSchID,
                                        int fstRepeatId, String fstRepeatDate) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "";
        if (type == 0) {// 删除日程
            sql = "delete from " + CLFindScheduleTable.CLFindScheduleTable
                    + " where " + CLFindScheduleTable.fstFID + " = " + fstFID
                    + " and " + CLFindScheduleTable.fstSchID + " = " + fstSchID
                    + " and " + CLFindScheduleTable.fstUpdateState + " != " + 3
                    + " and " + CLFindScheduleTable.fstRepeatId + " = " + 0;
        } else if (type == 4) {// 删除日程 实际是修改
            sql = "update " + CLFindScheduleTable.CLFindScheduleTable + " set "
                    + CLFindScheduleTable.fstUpdateState + " = " + 3
                    + " where " + CLFindScheduleTable.fstFID + " = " + fstFID
                    + " and " + CLFindScheduleTable.fstSchID + " = " + fstSchID
                    + " and " + CLFindScheduleTable.fstUpdateState + " != " + 3
                    + " and " + CLFindScheduleTable.fstRepeatId + " = " + 0;
        } else if (type == 5) {// 删除控件ID相同的重复子记事
            sql = "delete from " + CLFindScheduleTable.CLFindScheduleTable
                    + " where " + CLFindScheduleTable.fstFID + " = " + fstFID
                    + " and " + CLFindScheduleTable.fstSchID + " = " + fstSchID
                    + " and " + CLFindScheduleTable.fstUpdateState + " != " + 3
                    + " and " + CLFindScheduleTable.fstRepeatId + " = "
                    + fstRepeatId;
        } else if (type == 1) {// 删除重复
            sql = "delete from " + CLFindScheduleTable.CLFindScheduleTable
                    + " where " + CLFindScheduleTable.fstFID + " = " + fstFID
                    + " and " + CLFindScheduleTable.fstRepeatId + " = "
                    + fstRepeatId + " and "
                    + CLFindScheduleTable.fstUpdateState + " != " + 3;
        } else if (type == 2) {// 删除重复子记事
            sql = "delete from " + CLFindScheduleTable.CLFindScheduleTable
                    + " where " + CLFindScheduleTable.fstFID + " = " + fstFID
                    + " and " + CLFindScheduleTable.fstRepeatId + " = "
                    + fstRepeatId + " and " + CLFindScheduleTable.fstSchID
                    + " < " + 0 + " and " + CLFindScheduleTable.fstType + " = "
                    + 0 + " and " + CLFindScheduleTable.fstRepeatLink + " = "
                    + 1 + " and " + CLFindScheduleTable.fstUpdateState + " != "
                    + 3;
        } else if (type == 6) {// 删除网上同步成功后数据
            sql = "delete from " + CLFindScheduleTable.CLFindScheduleTable
                    + " where " + CLFindScheduleTable.fstSchID + " = "
                    + fstSchID + " and " + CLFindScheduleTable.fstType + " = "
                    + 0 + " and " + CLFindScheduleTable.fstRepeatId + " = " + 0;
        } else if (type == 7) {// 删除网上同步成功后的重复母记事
            sql = "delete from " + CLFindScheduleTable.CLFindScheduleTable
                    + " where " + CLFindScheduleTable.fstRepeatId + " = "
                    + fstRepeatId + " and " + CLFindScheduleTable.fstType
                    + " = " + 1 + " and " + CLFindScheduleTable.fstRepeatId
                    + " != " + 0;
        } else if (type == 8) {// 删除网上同步成功后的重复子记事
            sql = "delete from " + CLFindScheduleTable.CLFindScheduleTable
                    + " where " + CLFindScheduleTable.fstRepeatId + " = "
                    + fstRepeatId + " and " + CLFindScheduleTable.fstSchID
                    + " < " + 0 + " and " + CLFindScheduleTable.fstType + " = "
                    + 0 + " and " + CLFindScheduleTable.fstRepeatLink + " = "
                    + 1;
        } else if (type == 9) {// 修改重复状态为删除状态
            sql = "update " + CLFindScheduleTable.CLFindScheduleTable + " set "
                    + CLFindScheduleTable.fstUpdateState + " = " + 3
                    + " where " + CLFindScheduleTable.fstFID + " = " + fstFID
                    + " and " + CLFindScheduleTable.fstRepeatId + " = "
                    + fstRepeatId + " and " + CLFindScheduleTable.fstType
                    + " = " + 1 + " and " + CLFindScheduleTable.fstUpdateState
                    + " != " + 3;
        }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 过期未结束顺延修改日期
     */
    public void updateNewFocusShareDate(int fstFID, int fstSchID, String date) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLFindScheduleTable.CLFindScheduleTable
                + " set " + CLFindScheduleTable.fstDate + " = '" + date
                + "' , " + CLFindScheduleTable.fstUpdateState + " = " + 0
                + " where " + CLFindScheduleTable.fstSchID + " = " + fstSchID
                + " and " + CLFindScheduleTable.fstFID + " = " + fstFID;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 子记事进行操作，修改母记事
	 */
    public void updateNewFocusShareRepeatData(int fstFID, int repId,
                                              String repDateOne, String repDateTwo, int repStateOne,
                                              int repStateTwo) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "update " + CLFindScheduleTable.CLFindScheduleTable
                + " set " + CLFindScheduleTable.fstUpdateState + " = " + 2
                + " , " + CLFindScheduleTable.fstRepDateOne + " = '"
                + repDateOne + "', " + CLFindScheduleTable.fstRepDateTwo
                + " = '" + repDateTwo + "', "
                + CLFindScheduleTable.fstRepStateOne + " = " + repStateOne
                + ", " + CLFindScheduleTable.fstRepStateTwo + " = "
                + repStateTwo + " " + " where " + CLFindScheduleTable.fstFID
                + " = " + fstFID + " and " + CLFindScheduleTable.fstRepeatId
                + " = " + repId;
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
	 * 修改新版发现分享中同步成功后的状态
	 */
    public void updateNewFocusShareStateData(int type, int oldfstSchID,
                                             int newfstSchID, int oldfstRepeatID, int newfstRepeatID) {
        SQLiteDatabase sqldb = helper.getReadableDatabase();
        String sql = "";
        if (type == 0) {// 日程
            sql = "update " + CLFindScheduleTable.CLFindScheduleTable + " set "
                    + CLFindScheduleTable.fstUpdateState + " = " + 0 + " , "
                    + CLFindScheduleTable.fstSchID + " = " + newfstSchID
                    + " where " + CLFindScheduleTable.fstSchID + " = "
                    + oldfstSchID + " and "
                    + CLFindScheduleTable.fstUpdateState + " != " + 0;
        } else if (type == 1) {// 重复
            sql = "update " + CLFindScheduleTable.CLFindScheduleTable + " set "
                    + CLFindScheduleTable.fstUpdateState + " = " + 0 + " , "
                    + CLFindScheduleTable.fstRepeatId + " = " + newfstRepeatID
                    + " where " + CLFindScheduleTable.fstRepeatId + " = "
                    + oldfstRepeatID + " and "
                    + CLFindScheduleTable.fstUpdateState + " != " + 0;
        } else if (type == 3) {// 修改重复子记事
            sql = "update " + CLFindScheduleTable.CLFindScheduleTable + " set "
                    + CLFindScheduleTable.fstUpdateState + " = " + 0 + " , "
                    + CLFindScheduleTable.fstRepeatId + " = " + newfstRepeatID
                    + " where " + CLFindScheduleTable.fstRepeatId + " = "
                    + oldfstRepeatID + " and "
                    + CLFindScheduleTable.fstUpdateState + " = " + 0 + " and "
                    + CLFindScheduleTable.fstType + " = " + 0 + " and "
                    + CLFindScheduleTable.fstRepeatLink + " = " + 1;
        }
        try {
            sqldb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//		CrashReport.testJavaCrash();
    }
    //*********************************************新版好友列表操作*************************************************//

}
