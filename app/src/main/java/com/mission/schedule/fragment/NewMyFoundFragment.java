package com.mission.schedule.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lcf.refreshlibrary.PullToRefreshBase;
import com.lcf.refreshlibrary.PullToRefreshScrollView;
import com.mission.schedule.R;
import com.mission.schedule.activity.NewFocusMobleThreeActivity;
import com.mission.schedule.activity.NewFocusMobleTwoActivity;
import com.mission.schedule.activity.NewFocusOnCRYActivity;
import com.mission.schedule.activity.NewMyFoundSouSuoActivity;
import com.mission.schedule.activity.NewMyFoundWoDeFengXiangActivity;
import com.mission.schedule.activity.NewMyFoundWoDeGuanZuActivity;
import com.mission.schedule.activity.WebViewActivity;
import com.mission.schedule.adapter.NewMyFoundFragmentGridViewAdapter;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.AVBean;
import com.mission.schedule.bean.FriendsRiChengBackBean;
import com.mission.schedule.bean.FriendsRiChengBean;
import com.mission.schedule.bean.NewMyFoundFragmentAdvBeen;
import com.mission.schedule.bean.NewMyFoundFragmentItemsBeen;
import com.mission.schedule.bean.NewMyFoundFragmentRestBeen;
import com.mission.schedule.bean.NewMyFoundShouChangBeen;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.bean.TotalFriendsCountBean;
import com.mission.schedule.clock.QueryAlarmData;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.PostSendMainActivity;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.service.UpLoadService;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.widget.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class NewMyFoundFragment extends BaseFragment implements
        OnClickListener, OnItemClickListener {
    private boolean isShow = false;
    Context context;
    public static LinearLayout top_ll_left;
    private RelativeLayout my_friend_ll_right;
    private LinearLayout outgridview_ll;
    SharedPrefUtil sharedPrefUtil = null;
    String userID;
    LinearLayout myfragment, headviewadd;
    View headview, headviewother;
    private NewMyFoundFragmentGridViewAdapter GridViewAdapter = null;
    private MyGridView myGridView;
    private SliderLayout mySlider1;
    private SliderLayout mySlider2;
    PullToRefreshScrollView mPullRefreshScrollView;
    private ScrollView mScrollView;
    private LinearLayout focussomething_ll;
    private LinearLayout mysc_ll;
    private LinearLayout myshare_ll;
    public static final int MYFOCUSSCH = 1;// 我关注的日程
    public static final int MYFRIENDSITEM = 2;// 我关注的日程
    public static final int ADDFOCUS = 3;// 加关注
    String path;
    int focuscount = 0;

    List<FriendsRiChengBean> avList = new ArrayList<FriendsRiChengBean>();
    private static List<NewMyFoundFragmentAdvBeen> newavList = new ArrayList<NewMyFoundFragmentAdvBeen>();
    /**
     * gridview数据
     */
    private List<NewMyFoundFragmentItemsBeen> newgriddata = new ArrayList<NewMyFoundFragmentItemsBeen>();
    private File cache;
    private String runableimgurl;
    private String runableid;

    FriendsRiChengBean afterAvBean = null;
    private int loadimg = 0;
    private int loadimgsize = 0;
    private int messageint = 0;

    private NewMyFoundShouChangListBeen sharebean = null;

    // 屏蔽控件
    LinearLayout more_ll, three_ll;
    String userfirstint;
    String AVPATH = "";

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!isShow) {
                isShow = true;
                init();
                Viewpager();
                // GridViewData();
                AVData();
                loadData();
                DownSCData();
                MyShareData();
            } else {
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment_myfound, container,
                false);
        mPullRefreshScrollView = (PullToRefreshScrollView) v.findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {


            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ScrollView> refreshView) {
                mPullRefreshScrollView.onRefreshComplete();
            }
        });
        mScrollView = mPullRefreshScrollView.getRefreshableView();
        return v;
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();
            AVData();
            loadData();
            super.onPostExecute(result);
        }
    }

    @SuppressLint("NewApi")
    private void init() {
        EventBus.getDefault().register(this);
        View v = getView();
        context = getActivity();
        cache = context.getExternalFilesDir(Environment.getExternalStorageDirectory().getPath()
                + "/yourAppCacheFolderadv/");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        AVPATH = Environment.getExternalStorageDirectory().getPath() + "/yourAppCacheFolderadv/";
        myfragment = (LinearLayout) v.findViewById(R.id.myfragment);
        top_ll_left = (LinearLayout) v.findViewById(R.id.top_ll_left);
        my_friend_ll_right = (RelativeLayout) v
                .findViewById(R.id.my_friend_ll_right);
        my_friend_ll_right.setOnClickListener(this);
        more_ll = (LinearLayout) v.findViewById(R.id.more_ll);
        more_ll.setVisibility(View.GONE);
        three_ll = (LinearLayout) v.findViewById(R.id.three_ll);
        outgridview_ll = (LinearLayout) v.findViewById(R.id.outgridview_ll);
        focussomething_ll = (LinearLayout) v
                .findViewById(R.id.focussomething_ll);
        focussomething_ll.setOnClickListener(this);
        mysc_ll = (LinearLayout) v.findViewById(R.id.mysc_ll);
        mysc_ll.setOnClickListener(this);
        myshare_ll = (LinearLayout) v.findViewById(R.id.myshare_ll);
        myshare_ll.setOnClickListener(this);
        // top_ll_left.setOnClickListener(this);
        headviewadd = (LinearLayout) v
                .findViewById(R.id.new_fragment_myfound_headview);
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) headviewadd
                .getLayoutParams(); // 取控件mGrid当前的布局参数
        linearParams.height = mScreenWidth * 3 / 5;// 当控件的高强制设成屏幕3/5象素
        linearParams.width = mScreenWidth;
        headviewadd.setLayoutParams(linearParams);
        sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        userID = sharedPrefUtil.getString(getActivity(), ShareFile.USERFILE,
                ShareFile.USERID, "");
        headview = LayoutInflater.from(getActivity()).inflate(
                R.layout.new_fragment_myfound_headview_slider, null);
        headviewother = LayoutInflater.from(getActivity()).inflate(
                R.layout.new_fragment_myfound_headview_slider, null);
        mySlider1 = (SliderLayout) headview.findViewById(R.id.slider);
        mySlider2 = (SliderLayout) headviewother.findViewById(R.id.slider);
        myGridView = (MyGridView) v
                .findViewById(R.id.new_fragment_myfound_mygridviewview);
        myGridView.setOnItemClickListener(this);
    }

    private void loadData() {
        userfirstint = sharedPrefUtil.getString(getActivity(),
                ShareFile.USERFILE, ShareFile.NewMyFoundFenXiangFirst, "1");
        if ("1".equals(userfirstint)) {
            chushiuser();
        }
        // path = URLConstants.关注的好友 + "?uid=" + Integer.parseInt(userID)
        // + "&nowpage=" + 1 + "&pageNum=" + 40 + "&type=" + 3;
        if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
            // Intent intent = new Intent(getActivity(), UpLoadService.class);
            // intent.setAction(Const.SHUAXINDATA);
            // context.startService(intent);
            GridViewData();
            // FocusFriendsAsync(path);
        } else {
            outgridview_ll.setVisibility(View.GONE);
            Toast.makeText(context, "请检查您的网络是否正常！", Toast.LENGTH_SHORT).show();
            // found_tv.setVisibility(View.VISIBLE);
            return;
        }

    }

    private void GridViewData() {
        outgridview_ll.setVisibility(View.VISIBLE);
        String path = URLConstants.新版发现热门推荐;
        StringRequest request = new StringRequest(Method.GET, path,
                new Response.Listener<String>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                NewMyFoundFragmentRestBeen backBean = gson
                                        .fromJson(
                                                result,
                                                NewMyFoundFragmentRestBeen.class);
                                if (backBean.status == 0) {
                                    outgridview_ll.setVisibility(View.VISIBLE);
                                    newgriddata.clear();
                                    newgriddata = backBean.page.items;
                                    GridViewAdapter = new NewMyFoundFragmentGridViewAdapter(
                                            context,
                                            newgriddata,
                                            R.layout.new_fragment_myfound_gridview_item,
                                            mScreenWidth);
                                    myGridView.setAdapter(GridViewAdapter);
                                    GridViewAdapter.notifyDataSetChanged();
                                } else {
                                    outgridview_ll.setVisibility(View.GONE);
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (newgriddata != null) {
                    outgridview_ll.setVisibility(View.VISIBLE);
                }
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    public void onEventMainThread(FristFragment event) {

        String msg = event.getMsg();
        if ("2".equals(msg)&&isShow) {
            // loadData();
            loadCount();
            countFoundTotal();
            // adapter.notifyDataSetChanged();
        }
    }
    private void countFoundTotal(){
        String foundcountPath = URLConstants.发现手机端添加统计数量 + userID;
        StringRequest request = new StringRequest(Method.GET,
                foundcountPath, new Listener<String>() {

            @Override
            public void onResponse(String result) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        request.setTag("foundcount");
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }
    private void loadCount() {
        // tv_schedule_count = MainActivity.tv_schedule_count;
        // int noEndCount =
        // App.getDBcApplication().QueryNowGuoQiWeiJieShuCount();//
        // Integer.parseInt(mainMap.get("noEndCount"));
        // EventBus.getDefault().post(new PostSendMainActivity(1, noEndCount));
        // if (noEndCount == 0) {
        // tv_schedule_count.setVisibility(View.GONE);
        // } else {
        // tv_schedule_count.setText(noEndCount + "");
        // tv_schedule_count.setVisibility(View.VISIBLE);
        // }
        // 好友统计数量
        String friendsCountPath = URLConstants.统计好友操作数量 + "?uId=" + userID;
        FriendsTotalAsync(friendsCountPath);
        // tv_my_count = MainActivity.tv_my_count;
    }

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
                                    EventBus.getDefault().post(
                                            new PostSendMainActivity(2,
                                                    countBean.bsqCount));
                                } else {
                                    EventBus.getDefault().post(
                                            new PostSendMainActivity(2, 0));
                                    // tv_my_count.setVisibility(View.GONE);
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        } else {
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

    private void AVData() {
        String path = URLConstants.新版发现获取今后日程 + "?uId=7069&page=1&num=2000";
        QueryAVAsync(path);
    }

    private void QueryAVAsync(String path) {
        StringRequest request = new StringRequest(Method.GET, path,
                new Response.Listener<String>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                avList.clear();
                                FriendsRiChengBackBean backBean = gson.fromJson(result,
                                        FriendsRiChengBackBean.class);
                                if (backBean.status == 0) {
                                    avList = backBean.list;
                                    if (newavList == null
                                            || newavList.size() == 0) {
                                        messageint = 1;
                                    } else {
                                        messageint = 0;
                                    }
                                    newavList.clear();
                                    loadimgsize = avList.size();
                                    // for (AVBean beans : avListend) {
                                    // afterAvBean = beans;
                                    // runableimgurl = beans.imgUrl.replace(
                                    // "\\/", "");
                                    // runableid = "" + beans.id;
                                    // }
                                    new Thread(runnable).start();
                                    // av_ll.setBackgroundColor(context
                                    // .getResources().getColor(
                                    // R.color.white));
                                    // Viewpager();
                                } else {
                                    // av_ll.setBackground(context.getResources()
                                    // .getDrawable(R.drawable.pic_ad));
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
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    public Uri getImageURI(String path, File cache, String imgname)
            throws Exception {
        // String name = MD5.getMD5(path) +
        // path.substring(path.lastIndexOf("."));
        File file = context.getExternalFilesDir(AVPATH+imgname);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < avList.size(); i++) {
                FriendsRiChengBean beans = avList.get(i);
                afterAvBean = beans;
                runableimgurl = beans.imgPath.replace("\\/", "");
                runableid = "" + beans.CId;
                NewMyFoundFragmentAdvBeen been = new NewMyFoundFragmentAdvBeen();
                String imageurl = URLConstants.图片
                        + runableimgurl
                        + "&imageType=14&imageSizeType=1";
                Uri myimguri = null;
                try {
                    myimguri = getImageURI(imageurl, cache, runableid);
                } catch (Exception e) {
                    e.printStackTrace();
                    myimguri = Uri.parse(imageurl);
                }
                HashMap<String, String> filename = new HashMap<String, String>();
                filename.put(imageurl, myimguri.toString());
                been.setFileUri(imageurl);
                been.setFilename(filename);
                // been.setId(runableid);
                // been.setUrl(runableurl);
                been.setAvBean(afterAvBean);
                newavList.add(been);
                loadimg++;
            }
            if (loadimg == loadimgsize && messageint == 1) {
                loadimg = 0;
                messageint = 0;
                Message m = new Message();
                hander.sendMessage(m);
            }
        }
    };
    private Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Viewpager();
        }

        ;
    };

    public void Viewpager() {
        // listViews.clear();
        if (newavList == null || newavList.size() == 0) {
            headviewadd.removeAllViews();
            HashMap<String, Integer> url_maps_null = new HashMap<String, Integer>();
            url_maps_null.put("time3", R.mipmap.av);
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            // .description(name)
            textSliderView.setScaleType(BaseSliderView.ScaleType.Fit)
                    .image(R.mipmap.av)
                    .setOnSliderClickListener(new OnSliderClickListener() {

                        @Override
                        public void onSliderClick(BaseSliderView slider) {
//							dialogHeadOnClick(2, new AVBean());
                            Intent intent = new Intent(context, NewFocusMobleThreeActivity.class);
                            if (sharebean != null) {
                                intent.putExtra("fid", sharebean.id);
                                intent.putExtra("name", sharebean.name);
                                intent.putExtra("friendsimage", sharebean.titleImg);
                                intent.putExtra("friendsbackimage", sharebean.backgroundImg);
                                intent.putExtra("imagetype", sharebean.startStateImg);
                                if ("".equals(StringUtils.getIsStringEqulesNull(sharebean.remark5))) {
                                    intent.putExtra("othername", sharebean.name);
                                } else {
                                    intent.putExtra("othername", sharebean.remark5);
                                }
                            } else {
                                intent.putExtra("fid", 7069);
                                intent.putExtra("name", "大事件");
                                intent.putExtra("friendsimage", "20160610/685437.png");
                                intent.putExtra("friendsbackimage", "20160610/898372.png");
                                intent.putExtra("imagetype", "1,1");
                                intent.putExtra("othername", "2016年大事件");
                            }
                            startActivityForResult(intent, ADDFOCUS);
                        }
                    });

            // add your extra information
            textSliderView.getBundle().putString("extra", "");
            mySlider1.addSlider(textSliderView);
            mySlider1.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mySlider1
                    .setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            // mySlider1.setCustomAnimation(new DescriptionAnimation());
            mySlider1.setDuration(4000);
            headviewadd.addView(headview);
        } else {
            headviewadd.removeAllViews();
            List<NewMyFoundFragmentAdvBeen> mylist = new ArrayList<NewMyFoundFragmentAdvBeen>();
            mylist.clear();
            if (newavList != null && newavList.size() > 4) {
                Collections.shuffle(newavList);
                for (int i = 0; i < 4; i++) {
                    mylist.add(newavList.get(i));
                }
            } else {
                mylist = newavList;
            }
            for (NewMyFoundFragmentAdvBeen been : mylist) {
                long betweenday = 1;
                if ("".equals(StringUtils.getIsStringEqulesNull(been.getAvBean().CDate))) {
                    betweenday = 1;
                } else {
                    betweenday = (DateUtil.parseDate(been.getAvBean().CDate)
                            .getTime() - DateUtil.parseDate(
                            DateUtil.formatDate(new Date())).getTime())
                            / (1000 * 3600 * 24);
                }
                if (betweenday != 0 && betweenday>0) {
                    TextSliderView textSliderView = new TextSliderView(
                            getActivity());
                    // initialize a SliderLayout
                    Bundle sliderBundle = new Bundle();
                    sliderBundle.putSerializable("avbean", been.getAvBean());
                    textSliderView.getBundle().putBundle("extra", sliderBundle);
                    textSliderView.description(betweenday + "")
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .image(been.getFileUri())
                            .setOnSliderClickListener(new OnSliderClickListener() {

                                @Override
                                public void onSliderClick(BaseSliderView slider) {
//								Bundle sliderBundle = slider.getBundle()
//										.getBundle("extra");
//								AVBean beanitem = (AVBean) sliderBundle
//										.get("avbean");
//								String url = beanitem.url.replace("null", "");
//								if ("".equals(url) || url == null) {
//									dialogHeadOnClick(0, beanitem);
//								} else {
//									dialogHeadOnClick(1, beanitem);
//								}
                                    Intent intent = new Intent(context, NewFocusMobleThreeActivity.class);
                                    if (sharebean != null) {
                                        intent.putExtra("fid", sharebean.id);
                                        intent.putExtra("name", sharebean.name);
                                        intent.putExtra("friendsimage", sharebean.titleImg);
                                        intent.putExtra("friendsbackimage", sharebean.backgroundImg);
                                        intent.putExtra("imagetype", sharebean.startStateImg);
                                        if ("".equals(StringUtils.getIsStringEqulesNull(sharebean.remark5))) {
                                            intent.putExtra("othername", sharebean.name);
                                        } else {
                                            intent.putExtra("othername", sharebean.remark5);
                                        }
                                    } else {
                                        intent.putExtra("fid", 7069);
                                        intent.putExtra("name", "大事件");
                                        intent.putExtra("friendsimage", "20160610/685437.png");
                                        intent.putExtra("friendsbackimage", "20160610/898372.png");
                                        intent.putExtra("imagetype", "1,1");
                                        intent.putExtra("othername", "2016年大事件");
                                    }
                                    startActivityForResult(intent, ADDFOCUS);
                                }
                            });

                    // add your extra information
                    mySlider2.addSlider(textSliderView);
                }
            }
            mySlider2.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mySlider2
                    .setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            // mySlider2.setCustomAnimation(new DescriptionAnimation());
            mySlider2.setDuration(4000);
            headviewadd.addView(headviewother);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        App.getHttpQueues().cancelAll("foundcount");
        App.getHttpQueues().cancelAll("down");
        super.onDestroy();
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.my_friend_ll_right:
                intent = new Intent(context, NewMyFoundSouSuoActivity.class);
                startActivityForResult(intent, MYFRIENDSITEM);
                break;
            case R.id.focussomething_ll:// 大事件
                intent = new Intent(context, NewFocusMobleThreeActivity.class);
                if (sharebean != null) {
                    intent.putExtra("fid", sharebean.id);
                    intent.putExtra("name", sharebean.name);
                    intent.putExtra("friendsimage", sharebean.titleImg);
                    intent.putExtra("friendsbackimage", sharebean.backgroundImg);
                    intent.putExtra("imagetype", sharebean.startStateImg);
                    if ("".equals(StringUtils.getIsStringEqulesNull(sharebean.remark5))) {
                        intent.putExtra("othername", sharebean.name);
                    } else {
                        intent.putExtra("othername", sharebean.remark5);
                    }
                } else {
                    intent.putExtra("fid", 7069);
                    intent.putExtra("name", "大事件");
                    intent.putExtra("friendsimage", "20160610/685437.png");
                    intent.putExtra("friendsbackimage", "20160610/898372.png");
                    intent.putExtra("imagetype", "1,1");
                    intent.putExtra("othername", "2016年大事件");
                }
                startActivityForResult(intent, ADDFOCUS);
                break;
            case R.id.mysc_ll:
                intent = new Intent(context, NewMyFoundWoDeGuanZuActivity.class);
                startActivityForResult(intent, ADDFOCUS);
                break;
            case R.id.myshare_ll:
                intent = new Intent(context, NewMyFoundWoDeFengXiangActivity.class);
                startActivityForResult(intent, ADDFOCUS);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long arg3) {
        NewMyFoundFragmentItemsBeen bean = (NewMyFoundFragmentItemsBeen) myGridView
                .getAdapter().getItem(position);
        Intent intent = null;
        if (bean != null) {
            updataclockcount(bean.id);
            if ("1".equals(bean.styleView)) {
                intent = new Intent(context, NewFocusMobleTwoActivity.class);
            } else if ("2".equals(bean.styleView)) {
                intent = new Intent(context, NewFocusMobleThreeActivity.class);
            } else {
                intent = new Intent(context, NewFocusOnCRYActivity.class);
            }
            intent.putExtra("fid", Integer.valueOf(bean.id));
            intent.putExtra("name", bean.name);
            intent.putExtra("friendsimage", bean.titleImg);
            intent.putExtra("friendsbackimage", bean.backgroundImg);
            intent.putExtra("imagetype", bean.startStateImg);
            intent.putExtra("remark6", StringUtils.getIsStringEqulesNull(bean.remark6));
            if ("".equals(StringUtils.getIsStringEqulesNull(bean.remark5))) {
                intent.putExtra("othername", bean.name);
            } else {
                intent.putExtra("othername", bean.remark5);
            }
            startActivityForResult(intent, MYFRIENDSITEM);
        }
    }

    private void updataclockcount(String id) {
        String path = URLConstants.新版发现热门推荐点击增加点击数量 + "?userId=" + id;
        StringRequest request = new StringRequest(Method.GET, path,
                new Listener<String>() {

                    @Override
                    public void onResponse(String result) {

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        request.setTag("foundcount");
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void dialogHeadOnClick(int type, AVBean bean) {
        Dialog dialog = new Dialog(context, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(context);
        View more_pop_menu = fac.inflate(R.layout.dialog_newmyfound, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth();
        dialog.show();

        new HeadOnClick(dialog, more_pop_menu, bean, type);
    }

    class HeadOnClick implements View.OnClickListener {

        private View view;
        private Dialog dialog;
        private TextView zhuanfafriends_tv;
        private TextView addricheng_tv;
        private TextView canel_tv;
        private AVBean bean;
        private int type;

        @SuppressLint("NewApi")
        public HeadOnClick(Dialog dialog, View view, AVBean bean, int type) {
            this.dialog = dialog;
            this.view = view;
            this.bean = bean;
            this.type = type;
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
            if (type == 0) {
                addricheng_tv.setVisibility(View.VISIBLE);
                zhuanfafriends_tv.setVisibility(View.GONE);
            } else if (type == 2) {
                addricheng_tv.setVisibility(View.GONE);
                zhuanfafriends_tv.setVisibility(View.VISIBLE);
            } else {
                addricheng_tv.setVisibility(View.VISIBLE);
                zhuanfafriends_tv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.zhuanfafriends_tv:
                    if (type == 2) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.keytimeapp.com/"));
                        startActivity(intent);
                    } else {
                        intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("url", bean.url);// url_maps.get(name).get("weburl")
                        intent.putExtra("urlId", bean.id);// url_maps.get(name).get("webid")
                        startActivity(intent);
                    }
                    dialog.dismiss();
                    break;
                case R.id.addricheng_tv:
                    String ringdesc = "";
                    String ringcode = "";
                    if ("".equals(bean.ringDesc) || "null".equals(bean.ringDesc)
                            || null == bean.ringDesc) {
                        ringdesc = sharedPrefUtil.getString(getActivity(),
                                ShareFile.USERFILE, ShareFile.MUSICDESC, "完成任务");
                    } else {
                        ringdesc = bean.ringDesc;
                    }
                    if ("".equals(bean.ring) || "null".equals(bean.ring)
                            || null == bean.ring) {
                        ringcode = sharedPrefUtil.getString(getActivity(),
                                ShareFile.USERFILE, ShareFile.MUSICCODE, "g_88");
                    } else {
                        ringcode = bean.ring;
                    }
                    String time = "09:00";
                    int displaytime = 0;
                    if ("".equals(bean.beforeTime)
                            || "null".equals(bean.beforeTime)
                            || null == bean.beforeTime) {
                        displaytime = 0;
                    } else {
                        displaytime = 1;
                        time = bean.beforeTime;
                    }
                    boolean isInset = App.getDBcApplication().insertScheduleData(
                            bean.title,
                            DateUtil.formatDate(DateUtil.parseDate(bean.cdate)),
                            DateUtil.formatDateTimeHm(DateUtil
                                    .parseDateTimeHm(time)), 1, 0, displaytime, 0,
                            0, 0, 0, DateUtil.formatDateTime(new Date()), "", 0,
                            "", "", 0, "", DateUtil.formatDateTime(new Date()), 1,
                            0, 0, ringdesc, ringcode, "", 0, 0, 0, bean.url, "", 0,
                            0, 0);
                    if (isInset) {
                        QueryAlarmData.writeAlarm(getActivity()
                                .getApplicationContext());
                        Toast.makeText(context, "添加成功！", Toast.LENGTH_SHORT).show();
                        isNetWork();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "添加失败！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
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

    public void DownSCData() {
        String path = URLConstants.新版发现我的收藏 + "?userId=" + userID;
        StringRequest stringRequest = new StringRequest(Method.GET, path,
                new Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            List<NewMyFoundShouChangListBeen> beens = new ArrayList<NewMyFoundShouChangListBeen>();
                            try {
                                Gson gson = new Gson();
                                NewMyFoundShouChangBeen backBean = gson
                                        .fromJson(result,
                                                NewMyFoundShouChangBeen.class);
                                beens.clear();
                                if (backBean.status == 0) {
                                    beens = backBean.list;
                                    if (beens != null && beens.size() > 0) {
                                        clearSCData(getActivity());
                                        JSONArray jsonArray = new JSONArray();
                                        for (NewMyFoundShouChangListBeen been : beens) {
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("clickCount",
                                                        been.clickCount);
                                                jsonObject.put(
                                                        "attentionState",
                                                        been.attentionState);
                                                jsonObject.put("date",
                                                        been.date);
                                                jsonObject.put("content",
                                                        been.content);
                                                jsonObject.put("id", been.id);
                                                jsonObject.put("time",
                                                        been.time);
                                                jsonObject.put("name",
                                                        been.name);
                                                jsonObject.put("titleImg",
                                                        been.titleImg.replace(
                                                                "\\/", ""));
                                                jsonObject.put("startStateImg",
                                                        been.startStateImg);
                                                jsonArray.put(jsonObject);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        save(getActivity(),
                                                jsonArray.toString());
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
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        stringRequest.setTag("down");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        App.getHttpQueues().add(stringRequest);
        // VolleyRequest.RequestGet(getActivity(), path, "down",
        // new VolleyInterface(getActivity(), VolleyInterface.listener,
        // VolleyInterface.errorListener) {
        //
        // @Override
        // public void onMySuccess(String result) {
        // if (!TextUtils.isEmpty(result)) {
        // List<NewMyFoundShouChangListBeen> beens = new
        // ArrayList<NewMyFoundShouChangListBeen>();
        // try {
        // Gson gson = new Gson();
        // NewMyFoundShouChangBeen backBean = gson
        // .fromJson(result,
        // NewMyFoundShouChangBeen.class);
        // beens.clear();
        // if (backBean.status == 0) {
        // beens = backBean.list;
        // if (beens != null && beens.size() > 0) {
        // clearSCData(getActivity());
        // JSONArray jsonArray = new JSONArray();
        // for (NewMyFoundShouChangListBeen been : beens) {
        // JSONObject jsonObject = new JSONObject();
        // try {
        // jsonObject.put("clickCount",
        // been.clickCount);
        // jsonObject.put(
        // "attentionState",
        // been.attentionState);
        // jsonObject.put("date", been.date);
        // jsonObject.put("content", been.content);
        // jsonObject.put("id", been.id);
        // jsonObject.put("time", been.time);
        // jsonObject.put("name", been.name);
        // jsonArray.put(jsonObject);
        // } catch (JSONException e) {
        // e.printStackTrace();
        // }
        //
        // }
        // save(getActivity(), jsonArray.toString());
        // }
        // }
        //
        // } catch (JsonSyntaxException e) {
        // e.printStackTrace();
        // return;
        // }
        // } else {
        // return;
        // }
        // }
        //
        // @Override
        // public void onMyError(VolleyError volleyError) {
        //
        // }
        // });
    }

    private void save(Context context, String jsonarray) {// 写入SharedPreferences
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.SHOUCANGDATA, jsonarray);
    }

    public void clearSCData(Context context) {// 清空闹钟
        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                ShareFile.SHOUCANGDATA, "");
    }

    private void MyShareData() {
        String sharepath = URLConstants.新版发现我的分享 + "?userId=7069&type=1";
        StringRequest request = new StringRequest(Method.GET, sharepath,
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
                                    if (backBean.list != null
                                            && backBean.list.size() > 0) {
                                        sharebean = backBean.list.get(0);
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
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void chushiuser() {
        String foundcountPath = URLConstants.新版发现初始化一个自己的空间 + "?userId=" + userID;
        StringRequest request = new StringRequest(Method.GET, foundcountPath,
                new Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject jb = new JSONObject(result);
                                int datastates = jb.getInt("status");
                                if (datastates == 0) {
                                    sharedPrefUtil.putString(getActivity(),
                                            ShareFile.USERFILE,
                                            ShareFile.NewMyFoundFenXiangFirst,
                                            "2");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    @Override
    public void onResume() {
        mScrollView.smoothScrollTo(0, 0);// 手机锁频重启后 显示顶部
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MYFRIENDSITEM) {
            if (resultCode == Activity.RESULT_OK) {
                loadData();
                DownSCData();
                MyShareData();
            }
        } else if (requestCode == MYFOCUSSCH) {
            if (resultCode == Activity.RESULT_OK) {
                loadData();
                DownSCData();
                MyShareData();
            }
        } else if (requestCode == ADDFOCUS) {
            if (resultCode == Activity.RESULT_OK) {
                loadData();
                DownSCData();
                MyShareData();
            }
        }
    }
}
