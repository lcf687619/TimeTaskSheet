package com.mission.schedule.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.ResginBackBean;
import com.mission.schedule.bean.ResginBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.db.DBSourse;
import com.mission.schedule.service.DownQianDaoService;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lenovo on 2016/7/19.
 */
public class NewResiginActivity extends BaseActivity implements View.OnClickListener {

    @ViewResId(id = R.id.top_ll_back)
    private LinearLayout top_ll_back;
    @ViewResId(id = R.id.newtitle_tv)
    private TextView newtitle_tv;
    @ViewResId(id = R.id.phone_et)
    private EditText phone_et;
    @ViewResId(id = R.id.yanzheng_bt)
    private Button yanzheng_bt;
    @ViewResId(id = R.id.yanzheng_et)
    private EditText yanzheng_et;
    @ViewResId(id = R.id.pwd_et)
    private EditText pwd_et;
    @ViewResId(id = R.id.btn_login)
    private Button btn_login;

    Context context;
    SharedPrefUtil sharedPrefUtil;
    String userid;
    ProgressUtil progressUtil = new ProgressUtil();

    private Timer timer;
    private int second;
    private static ActivityManager1 instance;
    String  permissionState = "0";

    @Override
    protected void setListener() {
        top_ll_back.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        yanzheng_bt.setOnClickListener(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_newresigin);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        context = this;
        instance = ActivityManager1.getInstance();
        instance.addActivities(this);
        sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "");
        timer = new Timer();
        phone_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        initdata();
        permissionState = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "0");
        if("0".equals(permissionState)){
            checkPhonePermission();
        }
    }

    private void initdata() {
        newtitle_tv.setText("注册");
        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 11) {
                    yanzheng_bt.setBackground(getResources().getDrawable(R.drawable.bg_newresgin_send));
                    yanzheng_bt.setTextColor(getResources().getColor(R.color.white));
                    yanzheng_bt.setEnabled(true);
                } else {
                    yanzheng_bt.setBackground(getResources().getDrawable(R.drawable.bg_newresgin_unsend));
                    yanzheng_bt.setTextColor(getResources().getColor(R.color.newresgin_hintcolor));
                    yanzheng_bt.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void setAdapter() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if ("".equals(userid)) {
                    new WriteDataBase().execute();
                } else {
                    if (phone_et.getText().toString().trim().length() == 11
                            && Utils.checkMobilePhone(phone_et.getText().toString().trim())) {
                        if (yanzheng_et.getText().toString().trim().length() <= 6 &&
                                yanzheng_et.getText().toString().trim().length() >= 4) {
                            if (pwd_et.getText().toString().trim().length() >= 6) {
                                String path = URLConstants.修改个人信息 + "?uid=" + userid
                                        + "&uNickName=" + "" + "&uEmail="
                                        + "" + "&uMobile="
                                        + phone_et.getText().toString() + "&pwd=123456" + "&newPwd="
                                        + pwd_et.getText().toString() + "&sex=" + ""
                                        + "&yzm=" + yanzheng_et.getText().toString();
                                YouKeResginAsync(path);
                            } else {
                                alertFailIntenetDialog(6);
                            }
                        } else {
                            alertFailIntenetDialog(3);
                        }
                    } else {
                        alertFailIntenetDialog(5);
                    }
                }
                break;
            case R.id.yanzheng_bt:
                if (phone_et.getText().toString().trim().length()==11) {
                    senPhoneMessage();
                    second = 90;
                    yanzheng_bt.setEnabled(false);
                    if (timer == null) {
                        timer = new Timer();
                    }
                    timer.schedule(new CodeTimerTash(), 500, 1000);
                }else {
                    alertFailIntenetDialog(5);
                }
                break;
            case R.id.top_ll_back:
                this.finish();
                break;
        }
    }

    private class CodeTimerTash extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    second--;
                    if (second == 0) {
                        yanzheng_bt.setText("重新发送验证码");
                        yanzheng_bt.setEnabled(true);
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                    } else {
                        yanzheng_bt.setText(second + "秒后重试");
                    }
                }
            });
        }
    }

    private void senPhoneMessage() {
        String path = URLConstants.短信登录获取验证码 + phone_et.getText().toString().trim()+"&type=0";
        StringRequest request = new StringRequest(Request.Method.GET, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(!TextUtils.isEmpty(result)){
                    Gson gson = new Gson();
                    ResginBackBean backBean = gson.fromJson(result,
                            ResginBackBean.class);
                    if (backBean.status == 0) {

                    }else if(backBean.status==1){
//                        alertFailDialog(1,"");
                    }else if(backBean.status==2){
//                        alertFailDialog(2,"");
                    }else{
                        alertFailDialog(backBean.status,backBean.message);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                alertFailIntenetDialog(7);
            }
        });
        request.setTag("send");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void ResginAsync(String path) {
        progressUtil.ShowProgress(context, true, true, "正在努力加载......");
        StringRequest request = new StringRequest(Request.Method.GET, path,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        progressUtil.dismiss();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                ResginBackBean backBean = gson.fromJson(result,
                                        ResginBackBean.class);
                                if (backBean.status == 0) {
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.NewMyFoundFenXiangFirst,
                                            "1");
                                    List<ResginBean> list = backBean.list;
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USEREMAIL,
                                            list.get(0).uEmail);
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.TELEPHONE,
                                            list.get(0).uMobile);
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USERID, list.get(0).uId
                                                    + "");
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USERNAME,
                                            list.get(0).uNickName);
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USERBACKGROUNDPATH,
                                            list.get(0).uBackgroundImage);
                                    if (!"".equals(list.get(0).uPortrait)) {
                                        String str = list.get(0).uPortrait
                                                .toString();
                                        str = str.replace("\\", "");
                                        Log.d("TAG", str);
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.USERPHOTOPATH, str);
                                    }
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USERSTATE, "1");
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.U_ACC_NO,
                                            list.get(0).uAccNo);
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.ISYOUKE, "1");
                                    startActivity(new Intent(context,
                                            MainActivity.class));
                                    instance.doAllActivityFinish();
                                } else if (backBean.status == 1) {
                                    Toast.makeText(context, backBean.message,
                                            Toast.LENGTH_SHORT).show();
                                } else if(backBean.status==6){
                                    alertFailIntenetDialog(2);
                                } else if(backBean.status==7){
                                    alertFailIntenetDialog(3);
                                } else {
                                    alertFailIntenetDialog(4);
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressUtil.dismiss();
                alertFailIntenetDialog(1);
            }
        });
        request.setTag("resgin");
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void YouKeResginAsync(String path) {
        progressUtil.ShowProgress(context, true, true, "正在努力加载......");
        StringRequest request = new StringRequest(Request.Method.GET, path,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        progressUtil.dismiss();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Gson gson = new Gson();
                                SuccessOrFailBean backBean = gson.fromJson(
                                        result, SuccessOrFailBean.class);
                                if (backBean.status == 0) {
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.NewMyFoundFenXiangFirst,
                                            "1");
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USEREMAIL, "");
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USERSTATE, "1");
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.ISYOUKE, "1");
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.USERNAME, phone_et
                                                    .getText().toString());
                                    sharedPrefUtil.putString(context,
                                            ShareFile.USERFILE,
                                            ShareFile.TELEPHONE, phone_et
                                                    .getText().toString());
                                    startActivity(new Intent(context,
                                            MainActivity.class));
                                    instance.doAllActivityFinish();
                                } else if (backBean.status == 1) {
                                    Toast.makeText(context, backBean.message,
                                            Toast.LENGTH_SHORT).show();
                                } else if(backBean.status==6){
                                    alertFailIntenetDialog(2);
                                } else if(backBean.status==7){
                                    alertFailIntenetDialog(3);
                                } else {
                                    alertFailIntenetDialog(4);
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressUtil.dismiss();
                alertFailIntenetDialog(1);
            }
        });
        request.setTag("resgin");
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(context, "出错了...", Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(context, "存储卡不可用，请确认已插入卡后再使用本程序",
                            Toast.LENGTH_SHORT).show();
                    NewResiginActivity.this.finish();

                    break;
            }
        }

    };

    class WriteDataBase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            toWriteData(DBSourse.dataBaseName, R.raw.data);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            IntenetData();
            if (phone_et.getText().toString().trim().length() == 11
                    && Utils.checkMobilePhone(phone_et.getText().toString().trim())) {
                if (yanzheng_et.getText().toString().length() <= 6 &&
                        yanzheng_et.getText().toString().trim().length() >= 4) {
                    if (pwd_et.getText().toString().trim().length() >= 6) {
                        String path = URLConstants.注册 + "?uname="
                                + phone_et.getText().toString() + "&pwd="
                                + pwd_et.getText().toString() + "&type=" + 0
                                + "&uClintAddr=" + Utils.getSN() + "&uTocode="
                                + "android" + "&uSourceType=" + 1 + "&pushMac="
                                + JPushInterface.getUdid(getApplicationContext())
                                + "&yzm=" + yanzheng_et.getText().toString();
                        ResginAsync(path);
                    } else {
                        alertFailIntenetDialog(6);
                    }
                } else {
                    alertFailIntenetDialog(3);
                }
            } else {
                alertFailIntenetDialog(5);
            }
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

    private void IntenetData() {
        if (heigh == 0) {
            return;
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(context,
                            DownQianDaoService.class);
                    intent.putExtra("heigh", heigh);
                    intent.setAction("updateData");
                    intent.setPackage(getPackageName());
                    startService(intent);
                }
            }).start();
        }
    }

    private void alertFailIntenetDialog(int type) {
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
        if(type==1){
            delete_tv.setText("网络请求超时！");
        }else if(type==2){
            delete_tv.setText("验证码超时,请重新获取验证码！");
        }else if(type==3){
            delete_tv.setText("验证码错误！");
        }else if(type==4){
            delete_tv.setText("注册失败！");
        }else if(type==5){
            delete_tv.setText("手机号有误！");
        }else if(type==6){
            delete_tv.setText("密码至少6位！");
        }else if(type==7){
            delete_tv.setText("获取验证码超时！");
        }
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });

    }
    private void alertFailDialog(int type,String message) {
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
        if(type==1){
            delete_tv.setText("验证码超时,请重新获取验证码!");
        }else if(type==2){
            delete_tv.setText("请联系客服!");
        }else {
            if(type==4){
                yanzheng_bt.setText("获取验证码");
                yanzheng_bt.setEnabled(true);
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
            delete_tv.setText(message+"!");
        }
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getHttpQueues().cancelAll("send");
        App.getHttpQueues().cancelAll("resgin");
    }
    private void checkPhonePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPhoneStatePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.LOCATION);
            if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(NewResiginActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},10001);
            }
        }
        sharedPrefUtil.putString(context,ShareFile.USERFILE,ShareFile.PERMISSIONSTATE,"1");
    }
}
