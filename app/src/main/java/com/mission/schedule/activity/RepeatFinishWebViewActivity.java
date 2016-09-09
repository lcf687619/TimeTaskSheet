package com.mission.schedule.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.ActivityManager1;

public class RepeatFinishWebViewActivity extends BaseActivity {

    @ViewResId(id = R.id.webview)
    private WebView webview;
    @ViewResId(id = R.id.top_ll_back)
    private LinearLayout top_ll_back;
    @ViewResId(id = R.id.middle_tv)
    private TextView middle_tv;
    @ViewResId(id = R.id.top_ll_right)
    private RelativeLayout top_ll_right;

    ActivityManager1 activityManager = null;

    int repeatID = 0;

    @Override
    protected void setListener() {
        top_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepeatFinishWebViewActivity.this.finish();
            }
        });
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_repeatfinishwebview);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        activityManager = ActivityManager1.getInstance();
        activityManager.addActivities(this);
        top_ll_right.setVisibility(View.GONE);
        repeatID = getIntent().getIntExtra("repeatID",0);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.loadUrl(URLConstants.重复记事完成率+repeatID);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        WebChromeClient wvcc = new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {

                super.onReceivedTitle(view, title);
                middle_tv.setText(title);
            }

        };
        // 设置setWebChromeClient对象

        webview.setWebChromeClient(wvcc);
    }

    @Override
    protected void setAdapter() {

    }
}
