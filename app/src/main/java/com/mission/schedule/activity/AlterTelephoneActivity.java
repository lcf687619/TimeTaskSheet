package com.mission.schedule.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.ResginBackBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;
import com.mission.schedule.utils.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AlterTelephoneActivity extends BaseActivity implements
        OnClickListener {

    @ViewResId(id = R.id.top_ll_back)
    private LinearLayout top_ll_back;
    @ViewResId(id = R.id.top_ll_save)
    private LinearLayout top_ll_save;
    @ViewResId(id = R.id.telephone_et)
    private EditText telephone_et;
    @ViewResId(id = R.id.yanzheng_bt)
    private Button yanzheng_bt;
    @ViewResId(id = R.id.yanzheng_et)
    private EditText yanzheng_et;

    Context context;
    String path;
    SharedPrefUtil sharedPrefUtil = null;
    String userID = "";
    private Timer timer;
    private int second;

    @Override
    protected void setListener() {
        top_ll_back.setOnClickListener(this);
        top_ll_save.setOnClickListener(this);
        yanzheng_bt.setOnClickListener(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_altertelephone);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        context = this;
        sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        userID = sharedPrefUtil.getString(context, ShareFile.USERFILE, ShareFile.USERID, "");

        timer = new Timer();
        telephone_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        telephone_et.addTextChangedListener(new TextWatcher() {
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
            case R.id.top_ll_back:
                this.finish();
                break;
            case R.id.top_ll_save:
                path = URLConstants.修改手机号 + telephone_et.getText().toString().trim() + "&yzm=" + yanzheng_et.getText().toString().trim() + "&uid=" + userID;
                if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                    if (!"".equals(telephone_et.getText().toString().trim())
                            && Utils.checkMobilePhone(telephone_et.getText().toString().trim())) {
                        if (yanzheng_et.getText().toString().length() <= 6 &&
                                yanzheng_et.getText().toString().trim().length() >= 4) {
                            SaveMessageAsync(path);
                        } else {
                            alertFailDialog(-1, "验证码有误");
                        }
                    } else {
                        alertFailDialog(-1, "手机号码有误");
                    }
                } else {
                    Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case R.id.yanzheng_bt:
                if (telephone_et.getText().toString().trim().length() == 11) {
                    senPhoneMessage();
                    second = 90;
                    yanzheng_bt.setEnabled(false);
                    if (timer == null) {
                        timer = new Timer();
                    }
                    timer.schedule(new CodeTimerTash(), 500, 1000);
                } else {
                    alertFailDialog(-1, "手机号有误");
                }
                break;
            default:
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
        String path = URLConstants.短信登录获取验证码 + telephone_et.getText().toString().trim() + "&type=2&uid=" + userID;
        StringRequest request = new StringRequest(Request.Method.GET, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    ResginBackBean backBean = gson.fromJson(result,
                            ResginBackBean.class);
                    if (backBean.status == 0) {

                    } else if (backBean.status == 1) {
//                        alertFailDialog(1,"");
                    } else if (backBean.status == 2) {
//                        alertFailDialog(2,"");
                    } else {
                        alertFailDialog(backBean.status, backBean.message);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                alertFailDialog(1, "获取验证码失败!");
            }
        });
        request.setTag("send");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void SaveMessageAsync(String path) {
        final ProgressUtil progressUtil = new ProgressUtil();
        progressUtil.ShowProgress(context,true,true,"正在保存");
        StringRequest request = new StringRequest(Method.GET, path, new Listener<String>() {

            @Override
            public void onResponse(String result) {
                progressUtil.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    try {
                        SuccessOrFailBean bean = gson.fromJson(result, SuccessOrFailBean.class);
                        if (bean.status == 0) {
                            sharedPrefUtil.putString(context, ShareFile.USERFILE, ShareFile.TELEPHONE, telephone_et.getText()
                                    .toString());
                            Intent intent = new Intent();
                            intent.putExtra("telephone", telephone_et.getText()
                                    .toString());
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                            return;
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
                progressUtil.dismiss();
            }
        });
        request.setTag("down");
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        App.getHttpQueues().add(request);
    }

    private void alertFailDialog(int type, String message) {
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
        if (type == 1) {
            delete_tv.setText("验证码超时,请重新获取验证码!");
        } else if (type == 2) {
            delete_tv.setText("请联系客服!");
        } else {
            if (type == 4) {
                yanzheng_bt.setText("获取验证码");
                yanzheng_bt.setEnabled(true);
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
            delete_tv.setText(message + "!");
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
        App.getHttpQueues().cancelAll("down");
    }
}
