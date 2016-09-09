package com.mission.schedule.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.db.DBSourse;
import com.mission.schedule.service.DownQianDaoService;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.AfterPermissionGranted;
import com.mission.schedule.utils.EasyPermissions;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.Utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by lenovo on 2016/7/22.
 */
public class NewFindPWDTelephoneActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks{

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
    @ViewResId(id = R.id.text2)
    private TextView text2;

    Context context;
    SharedPrefUtil sharedPrefUtil;
    String userid;
    ProgressUtil progressUtil = new ProgressUtil();
    ActivityManager1 activityManager = null;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    String permissionState = "0";

    private Timer timer;
    private int second;

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
        activityManager = ActivityManager1.getInstance();
        activityManager.addActivities(this);
        sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "");
        permissionState = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "0");
        timer = new Timer();
        phone_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        if("0".equals(permissionState)){
            checkPhonePermission();
        }
        initdata();
    }
    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void checkPhonePermission() {
        String[] perms = { Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "该应用需要这些权限，为了保证应用正常运行!",
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
        sharedPrefUtil.putString(context,ShareFile.USERFILE,ShareFile.PERMISSIONSTATE,"1");
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkstorgePhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.STORAGE);
//            if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewFindPWDTelephoneActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
//            }
//            int checkcontactPermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.CONTACTS);
//            if(checkcontactPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewFindPWDTelephoneActivity.this,new String[]{Manifest.permission.READ_CONTACTS},10001);
//            }
//            int checkLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION);
//            if(checkLocationPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewFindPWDTelephoneActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10001);
//            }
//            int checkPhoneStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION);
//            if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewFindPWDTelephoneActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},10001);
//            }
//
//        }
    }
    private void initdata() {
        newtitle_tv.setText("找回密码");
        text2.setText("新密码");
        btn_login.setText("提交");
        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

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
                new WriteDataBase().execute();
                break;
            case R.id.yanzheng_bt:
                if (Utils.checkMobilePhone(phone_et.getText().toString())) {
                    senPhoneMessage();
                    second = 90;
                    yanzheng_bt.setEnabled(false);
                    if (timer == null) {
                        timer = new Timer();
                    }
                    timer.schedule(new CodeTimerTash(), 500, 1000);
                }else {
                    alertFailIntenetDialog(-1,"手机号码输入有误!");
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
        String path = URLConstants.短信登录获取验证码 + phone_et.getText().toString().trim()+"&type=1";
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
                alertFailIntenetDialog(0,"");
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
                                    activityManager.doAllActivityFinish();
                                } else if (backBean.status == 1) {
                                    Toast.makeText(context, backBean.message,
                                            Toast.LENGTH_SHORT).show();
                                } else if(backBean.status==4){
                                    alertFailIntenetDialog(4,backBean.message);
                                }else if(backBean.status==6){
                                    alertFailIntenetDialog(4,backBean.message);
                                } else if(backBean.status==7){
                                    alertFailIntenetDialog(4,backBean.message);
                                } else {
                                    alertFailIntenetDialog(0,"");
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
                alertFailIntenetDialog(0,"");
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
                    NewFindPWDTelephoneActivity.this.finish();

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
                        String path = URLConstants.手机号找回密码 + "?uMobile="
                                + phone_et.getText().toString() + "&newPwd="
                                + pwd_et.getText().toString()
                                + "&yzm=" + yanzheng_et.getText().toString();
                        ResginAsync(path);
                    } else {
                        alertFailIntenetDialog(-1,"密码输入有误!");
                    }
                } else {
                    alertFailIntenetDialog(-1,"验证码输入有误!");
                }
            } else {
                alertFailIntenetDialog(-1,"手机号输入有误!");
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

    private void alertFailIntenetDialog(int type,String msg) {
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
        if(type==-1){
            delete_tv.setText(msg);
        }else if(type==0){
            delete_tv.setText("网络请求超时！");
        }else {
            delete_tv.setText(msg);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
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
