package com.mission.schedule.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.adapter.NewFocusMobleThreeRichengAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.FriendsRiChengBackBean;
import com.mission.schedule.bean.FriendsRiChengBean;
import com.mission.schedule.bean.NewFocusDeleteBackBean;
import com.mission.schedule.bean.NewFocusDeleteRepDataBean;
import com.mission.schedule.bean.NewFocusDeleteSchDataBean;
import com.mission.schedule.bean.NewIsShouCangBackBean;
import com.mission.schedule.bean.NewMyFoundShouChangBeen;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueBeen;
import com.mission.schedule.bean.NewMyFoundShouChangDingYueListBeen;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.PullListView;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewFocusMobleThreeActivity extends BaseActivity implements
        OnClickListener {

    @ViewResId(id = R.id.focusName_tv)
    private TextView focusName_tv;// 大标题
    private TextView othertitle_tv;// 小标题
    private TextView shoucang_tv;// 收藏
    private TextView dingyue_tv;// 订阅
    @ViewResId(id = R.id.top_ll_back)
    private LinearLayout top_ll_back;
    @ViewResId(id = R.id.top_ll_right)
    private RelativeLayout top_ll_right;
//    @ViewResId(id = R.id.background_rl)
    private ImageView background_rl;
    @ViewResId(id = R.id.mylistview_lv)
    private PullListView mylistview_lv;
    private TextView shoucang_tv1;
    private TextView dingyue_tv1;
    private LinearLayout select_ll;

    Context context;
    String friendName;
    int friendId;
    String friendsimage;
    String friendsbackimage;
    NewFocusMobleThreeRichengAdapter riChengAdapter = null;
    String path;
    String UserID;
    SharedPrefUtil prefUtil = null;
    List<FriendsRiChengBean> riChengList = new ArrayList<FriendsRiChengBean>();
    Bitmap bit = null;
    int displaypixels;

    FriendsRiChengBean riChengBean = null;
    String imageUrl = "";
    ProgressUtil progressUtil = new ProgressUtil();
    String newimageurl = "";
    String imagetype = "";
    String state1 = "";
    String state2 = "";
    String othername = "";

    String jsonArrayStr = "";
    boolean shoucangfag = false;
    App app = null;

    View headView, bottomView;

    boolean mfreshfag = false;
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader;

    @Override
    protected void setListener() {
        top_ll_back.setOnClickListener(this);
        top_ll_right.setOnClickListener(this);
        shoucang_tv.setOnClickListener(this);
        dingyue_tv.setOnClickListener(this);
        shoucang_tv1.setOnClickListener(this);
        dingyue_tv1.setOnClickListener(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_newfocusincry_modle_three);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        context = this;
        prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        app = App.getDBcApplication();
        UserID = prefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "");
        jsonArrayStr = prefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.SHOUCANGDATA, "");
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.newfocusbackgroud)
                .showImageForEmptyUri(R.mipmap.newfocusbackgroud)
                .showImageOnFail(R.mipmap.newfocusbackgroud).cacheInMemory(true)
                .cacheOnDisc(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                .build();
        imageLoader = ImageLoader.getInstance();
        setHeadView();
        displaypixels = mScreenWidth * mScreenHeight;

        friendName = getIntent().getStringExtra("name");
        friendId = getIntent().getIntExtra("fid", 0);
        friendsimage = getIntent().getStringExtra("friendsimage");
        friendsbackimage = getIntent().getStringExtra("friendsbackimage");
        othername = getIntent().getStringExtra("othername");
        loadData();
    }

    private void setHeadView() {
        headView = LayoutInflater.from(context).inflate(
                R.layout.activity_newfocusmodleoneandtwo_headview, null);
        bottomView = LayoutInflater.from(context).inflate(
                R.layout.activity_newfoucs_footerview, null);
        othertitle_tv = (TextView) headView.findViewById(R.id.othertitle_tv);// 小标题
        shoucang_tv = (TextView) headView.findViewById(R.id.shoucang_tv);// 收藏
        dingyue_tv = (TextView) headView.findViewById(R.id.dingyue_tv);// 订阅
        background_rl = (ImageView) headView
                .findViewById(R.id.background_rl);
        select_ll = (LinearLayout) headView.findViewById(R.id.select_ll);
        select_ll.setVisibility(View.GONE);
        shoucang_tv1 = (TextView) bottomView.findViewById(R.id.shoucang_tv1);
        dingyue_tv1 = (TextView) bottomView.findViewById(R.id.dingyue_tv1);
        mylistview_lv.addPullHeaderView();
        mylistview_lv.addHeaderView(headView);
        mylistview_lv.addFooterView(bottomView);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) background_rl
                .getLayoutParams();
        linearParams.width = mScreenWidth - Utils.dipTopx(context, 20);
        linearParams.height = mScreenWidth - Utils.dipTopx(context, 20);
        background_rl.setLayoutParams(linearParams);

        refreshData();
    }

    private void loadData() {
        focusName_tv.setText(friendName);
        if ("".equals(StringUtils.getIsStringEqulesNull(othername))) {
            othertitle_tv.setText(friendName);
        } else {
            othertitle_tv.setText(othername);
        }
        imagetype = getIntent().getStringExtra("imagetype");
        state1 = imagetype.split(",")[0];
        state2 = imagetype.split(",")[1];
        if (state1.equals("0")) {
            newimageurl = URLConstants.图片 + friendsimage
                    + "&imageType=2&imageSizeType=3";
        } else if (state1.equals("1")) {
            newimageurl = URLConstants.图片 + friendsimage
                    + "&imageType=11&imageSizeType=1";
        }
//        if (!mfreshfag) {
//            progressUtil.ShowProgress(context, true, true, "正在加载数据...");
//        }
        IsFocusData();
        String newbgimageUrl;
        if ("".equals(StringUtils.getIsStringEqulesNull(friendsbackimage))) {
            newbgimageUrl = URLConstants.背景图片;
        } else {
            if (state2.equals("0")) {
                newbgimageUrl = URLConstants.背景图片 + "="
                        + friendsbackimage;
            } else {
                newbgimageUrl = URLConstants.图片 + friendsbackimage
                        + "&imageType=12&imageSizeType=1";
            }
        }
        imageLoader.displayImage(newbgimageUrl, background_rl, options, animateFirstListener);
        loadRiChengData();
//				bit = getBitmap(newbgimageUrl);
//				Message message = new Message();
//				message.what = 0;
//				message.obj = bit;
//				handler.sendMessage(message);
    }
//	public Bitmap getBitmap(String url) {
//		// 从内存缓存中获取图片
//		Bitmap result = memoryCache.getBitmapFromCache(url);
//		if (result == null) {
//			// 文件缓存中获取
//			result = fileCache.getImage(context,url);
//			if (result == null) {
//				// 从网络获取
//				result = ImageGetFromIntenet.downloadBitmap(url);
//				if (result != null) {
//					fileCache.saveBitmap(context,result, url, 1);
//					memoryCache.addBitmapToCache(url, result);
//				}
//			} else {
//				// 添加到内存缓存
//				memoryCache.addBitmapToCache(url, result);
//			}
//		}
//		return result;
//	}

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressUtil.dismiss();
            FriendsRiChengBean bean = null;
            if (msg.what != 0) {
                bean = (FriendsRiChengBean) msg.obj;
            }
            switch (msg.what) {
//                case 0:
//                    Bitmap bitmap = (Bitmap) msg.obj;
//                    // bitmap = ImageUtils.zoomBitmap(bitmap, mScreenWidth,
//                    // mScreenWidth);
//                    background_rl.setBackgroundDrawable(new BitmapDrawable(bitmap));
//                    loadRiChengData();
//                    break;
                case 3:
                    if (bean != null) {
                        dialogRiChengOnClick(bean);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private void loadRiChengData() {
        path = URLConstants.新版发现获取今后日程 + "?uId=" + friendId
                + "&page=1&num=2000";
        if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
            FriendsRiChengAsync(path);
        } else {
            mylistview_lv.refreshComplete();
            Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void setAdapter() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_ll_back:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                this.finish();
                break;
            case R.id.top_ll_right:
                dialogRightOnClick();
                break;
            case R.id.shoucang_tv:
                if (shoucangfag) {
                } else {
                    if (!"".equals(jsonArrayStr)) {
                        try {
                            JSONArray jsonArray = new JSONArray(jsonArrayStr);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                if (friendId == id) {
                                    shoucangfag = true;
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (shoucangfag) {
                    alertDialog();
                } else {
                    ShouCangData();
                }
                break;
            case R.id.shoucang_tv1:
                if (shoucangfag) {
                } else {
                    if (!"".equals(jsonArrayStr)) {
                        try {
                            JSONArray jsonArray = new JSONArray(jsonArrayStr);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                if (friendId == id) {
                                    shoucangfag = true;
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (shoucangfag) {
                    alertDialog();
                } else {
                    ShouCangData();
                }
                break;
            case R.id.dingyue_tv:
                if (shoucangfag) {
                    alertDialog();
                } else {
                    DingYueData();
                }
                break;
            case R.id.dingyue_tv1:
                // if (!"".equals(jsonArrayStr)) {
                // try {
                // JSONArray jsonArray = new JSONArray(jsonArrayStr);
                // for (int i = 0; i < jsonArray.length(); i++) {
                // JSONObject jsonObject = jsonArray.getJSONObject(i);
                // int id = jsonObject.getInt("id");
                // int attentionState = jsonObject
                // .getInt("attentionState");
                // if (friendId == id && attentionState == 0) {
                // dingyuefag = true;
                // break;
                // } else {
                // dingyuefag = false;
                // }
                // }
                // } catch (JSONException e) {
                // e.printStackTrace();
                // }
                // }
                // if (dingyuefag) {
                // Toast.makeText(context, "该频道已订阅!", Toast.LENGTH_SHORT).show();
                // } else {
                // DingYueData();
                // }
                if (shoucangfag) {
                    alertDialog();
                } else {
                    DingYueData();
                }
                break;
            default:
                break;
        }
    }

    private void FriendsRiChengAsync(String path) {
        if (!mfreshfag) {
            progressUtil.ShowProgress(context, true, true, "正在加载数据...");
        }
        StringRequest request = new StringRequest(Method.GET, path,
                new Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        progressUtil.dismiss();
                        mylistview_lv.refreshComplete();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                FriendsRiChengBackBean backBean = gson
                                        .fromJson(result,
                                                FriendsRiChengBackBean.class);
                                riChengList.clear();
                                if (backBean.status == 0) {
                                    riChengList = backBean.list;
                                    if (riChengList != null
                                            && riChengList.size() > 0) {
                                        Collections
                                                .sort(riChengList,
                                                        new Comparator<FriendsRiChengBean>() {

                                                            /*
                                                             * int
                                                             * compare(Student
                                                             * o1, Student o2)
                                                             * 返回一个基本类型的整型，
                                                             * 返回负数表示：o1 小于o2，
                                                             * 返回0 表示：o1和o2相等，
                                                             * 返回正数表示：o1大于o2。
                                                             */
                                                            public int compare(
                                                                    FriendsRiChengBean o1,
                                                                    FriendsRiChengBean o2) {

                                                                // 按照学生的年龄进行升序排列
                                                                if (DateUtil
                                                                        .parseDateTime(
                                                                                DateUtil.formatDateTime(DateUtil
                                                                                        .parseDateTime(o1.CDate
                                                                                                + " "
                                                                                                + o1.CTime)))
                                                                        .getTime() > DateUtil
                                                                        .parseDateTime(
                                                                                DateUtil.formatDateTime(DateUtil
                                                                                        .parseDateTime(o2.CDate
                                                                                                + " "
                                                                                                + o2.CTime)))
                                                                        .getTime()) {
                                                                    return 1;
                                                                }
                                                                if (DateUtil
                                                                        .parseDateTime(
                                                                                DateUtil.formatDateTime(DateUtil
                                                                                        .parseDateTime(o1.CDate
                                                                                                + " "
                                                                                                + o1.CTime)))
                                                                        .getTime() == DateUtil
                                                                        .parseDateTime(
                                                                                DateUtil.formatDateTime(DateUtil
                                                                                        .parseDateTime(o2.CDate
                                                                                                + " "
                                                                                                + o2.CTime)))
                                                                        .getTime()) {
                                                                    return 0;
                                                                }

                                                                return -1;
                                                            }
                                                        });
                                        riChengAdapter = new NewFocusMobleThreeRichengAdapter(
                                                context,
                                                riChengList,
                                                R.layout.adapter_friendsricheng_moble_three,
                                                handler, mScreenWidth);
                                        mylistview_lv
                                                .setAdapter(riChengAdapter);
                                    } else {
                                        riChengAdapter = new NewFocusMobleThreeRichengAdapter(
                                                context,
                                                riChengList,
                                                R.layout.adapter_friendsricheng_moble_three,
                                                handler, mScreenWidth);
                                        mylistview_lv
                                                .setAdapter(riChengAdapter);
                                        riChengAdapter.notifyDataSetChanged();
                                        return;
                                    }
                                } else {
                                    riChengList.clear();
                                    riChengAdapter = new NewFocusMobleThreeRichengAdapter(
                                            context,
                                            riChengList,
                                            R.layout.adapter_friendsricheng_moble_three,
                                            handler, mScreenWidth);
                                    mylistview_lv.setAdapter(riChengAdapter);
                                    riChengAdapter.notifyDataSetChanged();
                                    Toast.makeText(context, "没有日程...",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressUtil.dismiss();
                mylistview_lv.refreshComplete();
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

//	public void setListViewHeightBasedOnChildren(ListView listView) {
//		// 获取ListView对应的Adapter
//		ListAdapter adapter = listView.getAdapter();
//		if (adapter == null) {
//			return;
//		}
//
//		int totalHeight = 0;
//		int len = adapter.getCount();
//		if (len <= 2) {
//			for (int i = 0; i < len; i++) {
//				// listAdapter.getCount()返回数据项的数目
//				View listItem = adapter.getView(i, null, listView);
//				// 计算子项View 的宽高
//				listItem.measure(0, 0);
//				// 统计所有子项的总高度
//				totalHeight += (listItem.getMeasuredHeight() + 65);
//			}
//		} else {
//			for (int i = 0; i < len; i++) {
//				// listAdapter.getCount()返回数据项的数目
//				View listItem = adapter.getView(i, null, listView);
//				// 计算子项View 的宽高
//				listItem.measure(0, 0);
//				// 统计所有子项的总高度
//				totalHeight += (listItem.getMeasuredHeight() + 2);
//			}
//		}
//
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight
//				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
//		// listView.getDividerHeight()获取子项间分隔符占用的高度
//		// params.height最后得到整个ListView完整显示需要的高度
//		listView.setLayoutParams(params);
//	}

    private void alertDialog() {
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.show();
        Window window = builder.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);// 设置生效
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_alterfocus);
        TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
        delete_tv.setText("该频道已收藏!");
        TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });

    }

    private void dialogRiChengOnClick(FriendsRiChengBean mMap) {
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_friendscircle, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getWindowManager().getDefaultDisplay().getWidth();
        dialog.show();

        new RiChengOnClick(dialog, mMap, more_pop_menu);
    }

    class RiChengOnClick implements View.OnClickListener {

        private View view;
        private Dialog dialog;
        private FriendsRiChengBean mMap;
        private TextView zhuanfafriends_tv;
        private TextView addricheng_tv;
        private TextView canel_tv;

        @SuppressLint("NewApi")
        public RiChengOnClick(Dialog dialog, FriendsRiChengBean mMap, View view) {
            this.dialog = dialog;
            this.mMap = mMap;
            this.view = view;
            initview();
        }

        public void initview() {
            zhuanfafriends_tv = (TextView) view
                    .findViewById(R.id.zhuanfafriends_tv);
            zhuanfafriends_tv.setOnClickListener(this);
            addricheng_tv = (TextView) view.findViewById(R.id.addricheng_tv);
            addricheng_tv.setOnClickListener(this);
            canel_tv = (TextView) view.findViewById(R.id.canel_tv);
            canel_tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.zhuanfafriends_tv:
                    intent = new Intent(context,
                            FriendsRiChengZhuanFaActivity.class);
                    intent.putExtra("bean", mMap);
                    startActivity(intent);
                    dialog.dismiss();
                    break;
                case R.id.addricheng_tv:
                    try {
                        boolean isInset = App.getDBcApplication()
                                .insertScheduleData(mMap.CContent, mMap.CDate,
                                        mMap.CTime, mMap.CIsAlarm, mMap.CBefortime,
                                        mMap.CDisplayAlarm, mMap.CPostpone,
                                        mMap.CImportant, mMap.CColorType,
                                        mMap.CIsEnd, mMap.CCreateTime, mMap.CTags,
                                        mMap.CType, mMap.CTypeDesc,
                                        mMap.CTypeSpare, mMap.CRepeatId,
                                        mMap.CRepeatDate, mMap.CUpdateTime, 1,
                                        mMap.COpenstate, 0, mMap.CAlarmsoundDesc,
                                        mMap.CAlarmsound, "", 0, 0, mMap.atype,
                                        mMap.webUrl, mMap.imgPath, 0, 0, 0);
                        if (isInset) {
                            QueryAlarmData.writeAlarm(getApplicationContext());
                            Toast.makeText(context, "添加成功！", Toast.LENGTH_SHORT)
                                    .show();
                            isNetWork();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "添加失败！", Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.canel_tv:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void dialogRightOnClick() {
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac
                .inflate(R.layout.dialog_focusoncry_right, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
        dialog.show();

        new RightOnClick(dialog, more_pop_menu);
    }

    class RightOnClick implements View.OnClickListener {

        private View view;
        private Dialog dialog;
        private TextView onlysc_tv;
        private TextView scanddy_tv;
        private TextView share_wx_friends_tv;
        private TextView share_wx_firendscircle_tv;
        private TextView canel_tv;
        String title;
        String content;
        String path;
        String username;

        @SuppressLint("NewApi")
        public RightOnClick(Dialog dialog, View view) {
            this.dialog = dialog;
            this.view = view;
            initview();
        }

        public void initview() {
            onlysc_tv = (TextView) view.findViewById(R.id.onlysc_tv);
            onlysc_tv.setOnClickListener(this);
            scanddy_tv = (TextView) view.findViewById(R.id.scanddy_tv);
            scanddy_tv.setOnClickListener(this);
            share_wx_friends_tv = (TextView) view
                    .findViewById(R.id.share_wx_friends_tv);
            share_wx_friends_tv.setOnClickListener(this);
            share_wx_firendscircle_tv = (TextView) view
                    .findViewById(R.id.share_wx_firendscircle_tv);
            share_wx_firendscircle_tv.setOnClickListener(this);
            canel_tv = (TextView) view.findViewById(R.id.canel_tv);
            canel_tv.setOnClickListener(this);
            path = "http://www.keytimeapp.com/mytb/channel/space2.html?uid="
                    + friendId;
            title = othername;
            content = friendName;

            if (shoucangfag) {
            } else {
                if (!"".equals(jsonArrayStr)) {
                    try {
                        JSONArray jsonArray = new JSONArray(jsonArrayStr);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            if (friendId == id) {
                                shoucangfag = true;
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (shoucangfag) {
                onlysc_tv.setTextColor(getResources().getColor(
                        R.color.gongkai_txt));
                scanddy_tv.setTextColor(getResources().getColor(
                        R.color.gongkai_txt));
            } else {
                onlysc_tv.setTextColor(getResources().getColor(R.color.black));
                scanddy_tv.setTextColor(getResources().getColor(R.color.black));
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.onlysc_tv:
                    if (shoucangfag) {
                    } else {
                        ShouCangData();
                    }
                    dialog.dismiss();
                    break;
                case R.id.scanddy_tv:
                    if (shoucangfag) {
                    } else {
                        DingYueData();
                    }
                    dialog.dismiss();
                    break;
                case R.id.share_wx_friends_tv:
                    showShare(title, content, path);
                    dialog.dismiss();
                    break;
                case R.id.share_wx_firendscircle_tv:
                    showShare(title, content, path);
                    dialog.dismiss();
                    break;
                case R.id.canel_tv:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }

    }

    private void showShare(String title, String content, String path) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        // oks.setTitleUrl(path);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath(ParameterUtil.userHeadImg+imageUrl+"&imageType=2&imageSizeType=3");//
        // 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(path);
        oks.setImageUrl(newimageurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        // oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        // oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        // oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getHttpQueues().cancelAll("down");
        if (progressUtil != null) {
            progressUtil.dismiss();
        }
    }

    private void isNetWork() {
        if (NetUtil.getConnectState(this) != NetWorkState.NONE) {
            Intent intent = new Intent(this, UpLoadService.class);
            intent.setAction(Const.SHUAXINDATA);
            intent.setPackage(getPackageName());
            startService(intent);
        } else {
            return;
        }
    }

    private void DingYueData() {
        if (shoucangfag) {
            alertDialog();
            return;
        } else {
            progressUtil.ShowProgress(context, true, true, "正在订阅中...");
        }
        String shoucangpath = URLConstants.新版发现点击收藏 + "?userId=" + UserID
                + "&attentionId=" + friendId + "&attentionState=0";
        StringRequest stringRequest = new StringRequest(Method.GET,
                shoucangpath, new Listener<String>() {

            @Override
            public void onResponse(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        Gson gson = new Gson();
                        SuccessOrFailBean bean = gson.fromJson(result,
                                SuccessOrFailBean.class);
                        if (bean.status == 0) {
                            shoucangfag = true;
                            DownShcData();
                        } else {
                            Toast.makeText(context, "订阅失败!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        progressUtil.dismiss();
                    }
                }, 1000);
            }
        });
        stringRequest.setTag("focus");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(stringRequest);
        // VolleyRequest.RequestGet(context, shoucangpath, "focus",
        // new VolleyInterface(context, VolleyInterface.listener,
        // VolleyInterface.errorListener) {
        //
        // @Override
        // public void onMySuccess(String result) {
        // progressUtil.dismiss();
        // if (!TextUtils.isEmpty(result)) {
        // try {
        // Gson gson = new Gson();
        // SuccessOrFailBean bean = gson.fromJson(result,
        // SuccessOrFailBean.class);
        // if (bean.status == 0) {
        // DownShcData();
        // } else {
        // Toast.makeText(context, "订阅失败!",
        // Toast.LENGTH_SHORT).show();
        // }
        // } catch (JsonSyntaxException e) {
        // e.printStackTrace();
        // }
        // }
        // }
        //
        // @Override
        // public void onMyError(VolleyError volleyError) {
        // progressUtil.dismiss();
        // }
        // });
    }

    private void IsFocusData() {
        String path = URLConstants.新版发现判断是否收藏 + "?userId=" + UserID
                + "&attentionId=" + friendId;
        StringRequest stringRequest = new StringRequest(Method.GET, path,
                new Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                NewIsShouCangBackBean bean = gson.fromJson(
                                        result, NewIsShouCangBackBean.class);
                                if (bean.status == 0) {
                                    if (bean.list != null
                                            && bean.list.size() > 0) {
                                        if (bean.list.get(0).isAttention == 0) {
                                            shoucangfag = true;
                                        } else {
                                            shoucangfag = false;
                                        }
                                    }
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        stringRequest.setTag("focus");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(stringRequest);
        // VolleyRequest.RequestGet(context, path, "focus", new VolleyInterface(
        // context, VolleyInterface.listener,
        // VolleyInterface.errorListener) {
        //
        // @Override
        // public void onMySuccess(String result) {
        // if (!TextUtils.isEmpty(result)) {
        // try {
        // Gson gson = new Gson();
        // NewIsShouCangBackBean bean = gson.fromJson(result,
        // NewIsShouCangBackBean.class);
        // if (bean.status == 0) {
        // if (bean.list != null && bean.list.size() > 0) {
        // if (bean.list.get(0).isAttention == 0) {
        // shoucangfag = true;
        // }
        // }
        // }
        // } catch (JsonSyntaxException e) {
        // e.printStackTrace();
        // }
        // }
        // }
        //
        // @Override
        // public void onMyError(VolleyError volleyError) {
        //
        // }
        // });
    }

    private void ShouCangData() {
        if (shoucangfag) {
            alertDialog();
            return;
        } else {
            progressUtil.ShowProgress(context, true, true, "正在收藏中...");
        }
        String shoucangpath = URLConstants.新版发现点击收藏 + "?userId=" + UserID
                + "&attentionId=" + friendId + "&attentionState=1";
        StringRequest stringRequest = new StringRequest(Method.GET,
                shoucangpath, new Listener<String>() {

            @Override
            public void onResponse(String result) {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        progressUtil.dismiss();
                    }
                }, 1000);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        Gson gson = new Gson();
                        SuccessOrFailBean bean = gson.fromJson(result,
                                SuccessOrFailBean.class);
                        if (bean.status == 0) {
                            shoucangfag = true;
//									DownShcData();
                        } else {
                            Toast.makeText(context, "收藏失败!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        progressUtil.dismiss();
                    }
                }, 1000);
            }
        });
        stringRequest.setTag("focus");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(stringRequest);

        // VolleyRequest.RequestGet(context, shoucangpath, "focus",
        // new VolleyInterface(context, VolleyInterface.listener,
        // VolleyInterface.errorListener) {
        //
        // @Override
        // public void onMySuccess(String result) {
        // progressUtil.dismiss();
        // if (!TextUtils.isEmpty(result)) {
        // try {
        // Gson gson = new Gson();
        // SuccessOrFailBean bean = gson.fromJson(result,
        // SuccessOrFailBean.class);
        // if (bean.status == 0) {
        //
        // DownShcData();
        // } else {
        // Toast.makeText(context, "订阅失败!",
        // Toast.LENGTH_SHORT).show();
        // }
        // } catch (JsonSyntaxException e) {
        // e.printStackTrace();
        // }
        // }
        // }
        //
        // @Override
        // public void onMyError(VolleyError volleyError) {
        // progressUtil.dismiss();
        // }
        // });
    }

    private void DownShcData() {
        String datetime = "";
        datetime = prefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.FIRSTDOWNFOCUSSCH, "2016-01-01 00:00:00");
        if ("".equals(datetime)) {
            datetime = "2016-01-01 00:00:00";
        }
        datetime = datetime.replace(" ", "%2B");
        String downschpath = URLConstants.新版发现收藏下行数据到日程 + "?uid=" + friendId
                + "&dateTime=" + datetime + "&type=0";
        StringRequest stringRequest = new StringRequest(Method.GET,
                downschpath, new Listener<String>() {

            @Override
            public void onResponse(String result) {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        progressUtil.dismiss();
                    }
                }, 1000);
                if (!TextUtils.isEmpty(result)) {
                    List<NewMyFoundShouChangDingYueListBeen> addList = new ArrayList<NewMyFoundShouChangDingYueListBeen>();
                    try {
                        Gson gson = new Gson();
                        NewMyFoundShouChangDingYueBeen backbean = gson
                                .fromJson(
                                        result,
                                        NewMyFoundShouChangDingYueBeen.class);
                        if (backbean.status == 0) {
                            List<NewFocusDeleteBackBean> deleteList = null;
                            List<NewFocusDeleteRepDataBean> deleterepList = null;
                            List<NewFocusDeleteSchDataBean> deleteschList = null;
                            addList.clear();
                            addList = backbean.list;
                            if (addList != null && addList.size() > 0) {
                                for (NewMyFoundShouChangDingYueListBeen been : addList) {
                                    String repeatDate = "";
                                    if ("".equals(StringUtils
                                            .getIsStringEqulesNull(been.CRepeatDate))) {
                                        repeatDate = "";
                                    } else {
                                        repeatDate = been.CRepeatDate
                                                .replace("T", " ");
                                    }
                                    String alarmdesc = "";
                                    String alarmcode = "";
                                    if ("".equals(StringUtils
                                            .getIsStringEqulesNull(been.CAlarmsoundDesc))) {
                                        alarmdesc = "完成任务";
                                    } else {
                                        alarmdesc = been.CAlarmsoundDesc;
                                    }
                                    if ("".equals(StringUtils
                                            .getIsStringEqulesNull(been.CAlarmsound))) {
                                        alarmcode = "g_88";
                                    } else {
                                        alarmcode = been.CAlarmsound;
                                    }
                                    if (been.CRepeatId == 0) {
                                        int schcount = app
                                                .CheckCountSchFromFocusData(been.CId);
                                        if (schcount == 0) {
                                            app.insertScheduleData(
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CContent),
                                                    DateUtil.formatDate(DateUtil
                                                            .parseDate(been.CDate)),
                                                    DateUtil.formatDateTimeHm(DateUtil
                                                            .parseDateTimeHm(been.CTime)),
                                                    been.CIsAlarm,
                                                    been.CBefortime,
                                                    been.CDisplayAlarm,
                                                    been.CPostpone,
                                                    been.CImportant,
                                                    been.CColorType,
                                                    been.CIsEnd,
                                                    been.CCreateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    "",
                                                    been.CType,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeDesc),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeSpare),
                                                    been.CRepeatId,
                                                    repeatDate,
                                                    been.CUpdateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    0,
                                                    0,
                                                    been.CSchRepeatLink,
                                                    alarmdesc,
                                                    alarmcode,
                                                    friendName,
                                                    been.schRead,
                                                    been.CId,
                                                    been.atype,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.webUrl),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.imgPath),
                                                    0, 0, been.CUid);
                                        } else {
                                            app.updateScheduleNoIDForSchData(
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CContent),
                                                    DateUtil.formatDate(DateUtil
                                                            .parseDate(been.CDate)),
                                                    DateUtil.formatDateTimeHm(DateUtil
                                                            .parseDateTimeHm(been.CTime)),
                                                    been.CIsAlarm,
                                                    been.CBefortime,
                                                    been.CDisplayAlarm,
                                                    been.CPostpone,
                                                    been.CImportant,
                                                    been.CColorType,
                                                    been.CIsEnd,
                                                    been.CCreateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    "",
                                                    been.CType,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeDesc),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeSpare),
                                                    been.CRepeatId,
                                                    repeatDate,
                                                    been.CUpdateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    0,
                                                    0,
                                                    been.CSchRepeatLink,
                                                    alarmdesc,
                                                    alarmcode,
                                                    friendName,
                                                    been.schRead,
                                                    been.CId,
                                                    been.atype,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.webUrl),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.imgPath),
                                                    0, 0, been.CUid);
                                        }
                                    } else {
                                        int repcount = app
                                                .CheckCountRepFromFocusData(been.CRepeatId);
                                        if (repcount == 0) {
                                            app.insertScheduleData(
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CContent),
                                                    DateUtil.formatDate(DateUtil
                                                            .parseDate(been.CDate)),
                                                    DateUtil.formatDateTimeHm(DateUtil
                                                            .parseDateTimeHm(been.CTime)),
                                                    been.CIsAlarm,
                                                    been.CBefortime,
                                                    been.CDisplayAlarm,
                                                    been.CPostpone,
                                                    been.CImportant,
                                                    been.CColorType,
                                                    been.CIsEnd,
                                                    been.CCreateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    "",
                                                    been.CType,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeDesc),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeSpare),
                                                    been.CRepeatId,
                                                    repeatDate,
                                                    been.CUpdateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    0,
                                                    0,
                                                    been.CSchRepeatLink,
                                                    alarmdesc,
                                                    alarmcode,
                                                    friendName,
                                                    been.schRead,
                                                    been.CId,
                                                    been.atype,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.webUrl),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.imgPath),
                                                    0, 0, been.CUid);
                                        } else {
                                            app.updateScheduleNoIDForRepData(
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CContent),
                                                    DateUtil.formatDate(DateUtil
                                                            .parseDate(been.CDate)),
                                                    DateUtil.formatDateTimeHm(DateUtil
                                                            .parseDateTimeHm(been.CTime)),
                                                    been.CIsAlarm,
                                                    been.CBefortime,
                                                    been.CDisplayAlarm,
                                                    been.CPostpone,
                                                    been.CImportant,
                                                    been.CColorType,
                                                    been.CIsEnd,
                                                    been.CCreateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    "",
                                                    been.CType,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeDesc),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.CTypeSpare),
                                                    been.CRepeatId,
                                                    repeatDate,
                                                    been.CUpdateTime
                                                            .replace(
                                                                    "T",
                                                                    " "),
                                                    0,
                                                    0,
                                                    been.CSchRepeatLink,
                                                    alarmdesc,
                                                    alarmcode,
                                                    friendName,
                                                    been.schRead,
                                                    been.CId,
                                                    been.atype,
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.webUrl),
                                                    StringUtils
                                                            .getIsStringEqulesNull(been.imgPath),
                                                    0, 0, been.CUid);
                                        }
                                    }

                                }
                            }
                            deleteschList = backbean.delList;
                            deleterepList = backbean.tDelList;
                            if (deleteschList != null
                                    && deleteschList.size() > 0) {
                                for (NewFocusDeleteSchDataBean schDataBean : deleteschList) {
                                    if (schDataBean.state == 1) {
                                        app.deleteRepFocusParentData(
                                                schDataBean.dataId,
                                                schDataBean.uid);
                                    } else {
                                        app.deleteSchFocusData(
                                                schDataBean.dataId,
                                                schDataBean.uid);
                                    }
                                }
                            }
                            if (deleterepList != null
                                    && deleterepList.size() > 0) {
                                for (NewFocusDeleteRepDataBean deleteRepDataBean : deleterepList) {
                                    String repdate = StringUtils
                                            .getIsStringEqulesNull(deleteRepDataBean.repdatetwo);
                                    if (!"".equals(repdate)) {
                                        repdate = repdate.replace("T",
                                                " ");
                                        app.deleteRepFocusData(
                                                deleteRepDataBean.repId,
                                                repdate);
                                    }
                                }
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        progressUtil.dismiss();
                    }
                }, 1000);
            }
        });
        stringRequest.setTag("downsch");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(stringRequest);

        // VolleyRequest.RequestGet(context, downschpath, "downsch",
        // new VolleyInterface(context, VolleyInterface.listener,
        // VolleyInterface.errorListener) {
        //
        // @Override
        // public void onMySuccess(String result) {
        // if (!TextUtils.isEmpty(result)) {
        // List<NewMyFoundShouChangDingYueListBeen> addList = new
        // ArrayList<NewMyFoundShouChangDingYueListBeen>();
        // try {
        // Gson gson = new Gson();
        // NewMyFoundShouChangDingYueBeen backbean = gson
        // .fromJson(
        // result,
        // NewMyFoundShouChangDingYueBeen.class);
        // if (backbean.status == 0) {
        // addList.clear();
        // addList = backbean.list;
        // if (addList != null && addList.size() > 0) {
        //
        // }
        // }
        // } catch (JsonSyntaxException e) {
        // e.printStackTrace();
        // }
        // }
        // }
        //
        // @Override
        // public void onMyError(VolleyError volleyError) {
        //
        // }
        // });
    }

    private void LoadDataAsync(String path) {
        StringRequest request = new StringRequest(Method.GET, path,
                new Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                NewMyFoundShouChangBeen backBean = gson
                                        .fromJson(result,
                                                NewMyFoundShouChangBeen.class);
                                if (backBean.status == 0) {
                                    List<NewMyFoundShouChangListBeen> fengxingList = backBean.list;
                                    NewMyFoundShouChangListBeen newbean = null;
                                    if (fengxingList != null
                                            && fengxingList.size() > 0) {
                                        newbean = fengxingList.get(0);
                                        friendName = newbean.name;
                                        friendsimage = newbean.titleImg;
                                        friendsbackimage = newbean.backgroundImg;
                                        othername = newbean.remark5;
                                        imagetype = newbean.startStateImg;
                                        loadData();
                                    }
                                }

                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mylistview_lv.refreshComplete();
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void refreshData() {
        mylistview_lv.setOnRefreshListener(new PullListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mfreshfag = true;
                String path = URLConstants.新版发现我的分享 + "?userId=" + friendId + "&type=1";
                if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                    LoadDataAsync(path);
                } else {
                    mylistview_lv.refreshComplete();
                }
            }
        });

        mylistview_lv.setOnGetMoreListener(new PullListView.OnGetMoreListener() {

            @Override
            public void onGetMore() {
                mylistview_lv.getMoreComplete();
            }
        });
        mylistview_lv.performRefresh();
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