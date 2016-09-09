package com.mission.schedule.activity;

import android.Manifest;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lenovo on 2016/7/21.
 */
public class NewEmailLoginActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks{

    @ViewResId(id = R.id.top_ll_back)
    private LinearLayout top_ll_back;
    @ViewResId(id = R.id.newtitle_tv)
    private TextView newtitle_tv;
    @ViewResId(id = R.id.phone_et)
    private EditText phone_et;
    @ViewResId(id = R.id.pwd_et)
    private EditText pwd_et;
    @ViewResId(id = R.id.btn_login)
    private Button btn_login;

    Context context;
    SharedPrefUtil sharedPrefUtil;
    public List<ResginBean> list = new ArrayList<ResginBean>();
    private String userid;
    ProgressUtil progressUtil = new ProgressUtil();
    ActivityManager1 activityManager = null;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    String permissionState = "0";

    @Override
    protected void setListener() {
        top_ll_back.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_newemailogin);
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
        newtitle_tv.setText("登录");
        if("0".equals(permissionState)){
            checkPhonePermission();
        }
    }

    @Override
    protected void setAdapter() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top_ll_back:
                this.finish();
                break;
            case R.id.btn_login:
                if ("".equals(userid)) {//用户未登录
                    new WriteDataBase().execute();
                } else {
                    if (Utils.isEmail(phone_et.getText().toString())) {
                        if (pwd_et.getText().toString().trim().length() >= 6) {
                            String path = URLConstants.登录
                                    + "?uname="
                                    + phone_et.getText().toString().trim()
                                    + "&pwd="
                                    + pwd_et.getText().toString().trim()
                                    + "&type="
                                    + 4
                                    + "&uClintAddr="
                                    + JPushInterface
                                    .getUdid(getApplicationContext());
                            // new LoginAsync().execute(path);
                            progressUtil.ShowProgress(context, true, true,
                                    "正在登录......");
                            StringRequest request = new StringRequest(
                                    Request.Method.GET, path,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String s) {
                                            progressUtil.dismiss();
                                            if (!TextUtils.isEmpty(s)) {
                                                try {
                                                    Gson gson = new Gson();
                                                    ResginBackBean backBean = gson
                                                            .fromJson(
                                                                    s,
                                                                    ResginBackBean.class);
                                                    if (backBean.status == 0) {
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.NewMyFoundFenXiangFirst,
                                                                        "1");
                                                        list = backBean.list;
                                                        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                                                ShareFile.ISYOUKE, "1");
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.USEREMAIL,
                                                                        list.get(0).uEmail);
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.TELEPHONE,
                                                                        list.get(0).uMobile);
                                                        sharedPrefUtil.putString(
                                                                context,
                                                                ShareFile.USERFILE,
                                                                ShareFile.USERID,
                                                                list.get(0).uId
                                                                        + "");
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.USERNAME,
                                                                        list.get(0).uNickName);
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.USERSTATE,
                                                                        "1");
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.USERBACKGROUNDPATH,
                                                                        list.get(0).uBackgroundImage);
                                                        if (!"".equals(list.get(0).uPortrait)) {
                                                            String str = list
                                                                    .get(0).uPortrait
                                                                    .toString();
                                                            str = str.replace("\\",
                                                                    "");
                                                            sharedPrefUtil
                                                                    .putString(
                                                                            context,
                                                                            ShareFile.USERFILE,
                                                                            ShareFile.USERPHOTOPATH,
                                                                            str);
                                                        }
                                                        sharedPrefUtil.putString(
                                                                context,
                                                                ShareFile.USERFILE,
                                                                ShareFile.U_ACC_NO,
                                                                list.get(0).uAccNo);
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.ISYOUKE,
                                                                        list.get(0).uIsActive);
                                                        startActivity(new Intent(
                                                                context,
                                                                MainActivity.class));
                                                        activityManager.doAllActivityFinish();
                                                    } else if (backBean.status == 1) {
                                                        alertFailZHDialog();
                                                    } else if (backBean.status == 4) {
                                                        alertFailPWDDialog();
                                                    } else {
                                                        alertFailIntenetDialog(backBean.status,backBean.message);
                                                    }
                                                } catch (JsonSyntaxException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                alertFailIntenetDialog(0,"");
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(
                                        VolleyError volleyError) {
                                    progressUtil.dismiss();
                                    alertFailIntenetDialog(0,"");
                                }
                            });
                            request.setTag("login");
                            request.setRetryPolicy(new DefaultRetryPolicy(60000, 1, 1.0f));
                            App.getHttpQueues().add(request);
                        } else {
                            alertFailIntenetDialog(-1,"密码输入有误!");
                        }
                    } else {
                        alertFailIntenetDialog(-1,"邮箱输入有误!");
                    }
                }
                break;
        }
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
                    NewEmailLoginActivity.this.finish();

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
                if (Utils.isEmail(phone_et.getText().toString())) {
                    if (pwd_et.getText().toString().trim().length() >= 6) {
                        String path = URLConstants.登录
                                + "?uname="
                                + phone_et.getText().toString().trim()
                                + "&pwd="
                                + pwd_et.getText().toString().trim()
                                + "&type="
                                + 4
                                + "&uClintAddr="
                                + JPushInterface
                                .getUdid(getApplicationContext())
                                + "&uTocode=android";
                        // new LoginAsync().execute(path);
                        progressUtil.ShowProgress(context, true, true,
                                "正在登录......");
                        StringRequest request = new StringRequest(
                                Request.Method.GET, path,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        progressUtil.dismiss();
                                        if (!TextUtils.isEmpty(s)) {
                                            try {
                                                Gson gson = new Gson();
                                                ResginBackBean backBean = gson
                                                        .fromJson(
                                                                s,
                                                                ResginBackBean.class);
                                                if (backBean.status == 0) {
                                                    sharedPrefUtil
                                                            .putString(
                                                                    context,
                                                                    ShareFile.USERFILE,
                                                                    ShareFile.NewMyFoundFenXiangFirst,
                                                                    "1");
                                                    list = backBean.list;
                                                    sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                                            ShareFile.ISYOUKE, "1");
                                                    sharedPrefUtil
                                                            .putString(
                                                                    context,
                                                                    ShareFile.USERFILE,
                                                                    ShareFile.USEREMAIL,
                                                                    list.get(0).uEmail);
                                                    sharedPrefUtil
                                                            .putString(
                                                                    context,
                                                                    ShareFile.USERFILE,
                                                                    ShareFile.TELEPHONE,
                                                                    list.get(0).uMobile);
                                                    sharedPrefUtil.putString(
                                                            context,
                                                            ShareFile.USERFILE,
                                                            ShareFile.USERID,
                                                            list.get(0).uId
                                                                    + "");
                                                    sharedPrefUtil
                                                            .putString(
                                                                    context,
                                                                    ShareFile.USERFILE,
                                                                    ShareFile.USERNAME,
                                                                    list.get(0).uNickName);
                                                    sharedPrefUtil
                                                            .putString(
                                                                    context,
                                                                    ShareFile.USERFILE,
                                                                    ShareFile.USERSTATE,
                                                                    "1");
                                                    sharedPrefUtil
                                                            .putString(
                                                                    context,
                                                                    ShareFile.USERFILE,
                                                                    ShareFile.USERBACKGROUNDPATH,
                                                                    list.get(0).uBackgroundImage);
                                                    if (!"".equals(list.get(0).uPortrait)) {
                                                        String str = list
                                                                .get(0).uPortrait
                                                                .toString();
                                                        str = str.replace("\\",
                                                                "");
                                                        sharedPrefUtil
                                                                .putString(
                                                                        context,
                                                                        ShareFile.USERFILE,
                                                                        ShareFile.USERPHOTOPATH,
                                                                        str);
                                                    }
                                                    sharedPrefUtil.putString(
                                                            context,
                                                            ShareFile.USERFILE,
                                                            ShareFile.U_ACC_NO,
                                                            list.get(0).uAccNo);
                                                    sharedPrefUtil
                                                            .putString(
                                                                    context,
                                                                    ShareFile.USERFILE,
                                                                    ShareFile.ISYOUKE,
                                                                    list.get(0).uIsActive);
                                                    startActivity(new Intent(
                                                            context,
                                                            MainActivity.class));
                                                    activityManager.doAllActivityFinish();
                                                } else if (backBean.status == 1) {
                                                    alertFailZHDialog();
                                                } else if (backBean.status == 4) {
                                                    alertFailPWDDialog();
                                                } else {
                                                    alertFailIntenetDialog(backBean.status,backBean.message);
                                                }
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            alertFailIntenetDialog(0,"");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(
                                    VolleyError volleyError) {
                                progressUtil.dismiss();
                                alertFailIntenetDialog(0,"");
                            }
                        });
                        request.setTag("login");
                        request.setRetryPolicy(new DefaultRetryPolicy(60000,1,1.0f));
                        App.getHttpQueues().add(request);
                    } else {
                        alertFailIntenetDialog(-1,"密码输入有误!");
                    }
                } else {
                    alertFailIntenetDialog(-1,"邮箱输入有误!");
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

    private void alertFailZHDialog() {
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
        delete_tv.setText("账号不存在！");
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });

    }

    private void alertFailPWDDialog() {
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
        delete_tv.setText("密码输入有误！");
        delete_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.cancel();
            }
        });

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getHttpQueues().cancelAll("login");
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
//            int checkstorgePhonePermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.STORAGE);
//            if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
//            }
//            int checkcontactPermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.CONTACTS);
//            if(checkcontactPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.READ_CONTACTS},10001);
//            }
//            int checkLocationPermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.LOCATION);
//            if(checkLocationPermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10001);
//            }
//            int checkPhoneStatePermission = ContextCompat.checkSelfPermission(NewEmailLoginActivity.this, Manifest.permission_group.LOCATION);
//            if(checkPhoneStatePermission != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(NewEmailLoginActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},10001);
//            }
//
//        }
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
