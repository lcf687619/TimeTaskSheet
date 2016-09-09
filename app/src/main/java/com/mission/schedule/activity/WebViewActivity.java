package com.mission.schedule.activity;


import cn.jpush.android.api.JPushInterface;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.bean.UserInfo;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.utils.SharedPrefUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class WebViewActivity extends BaseActivity {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.title_tv)
	private TextView title_tv;
	@ViewResId(id = R.id.webview)
	private WebView webview;

	Context context;
	String url;
	String urlId;
	String userid;
	String userName;
	String e_mail;
	SharedPrefUtil sharedPrefUtil = null;
	UserInfo info = null;

	@Override
	protected void setListener() {
		top_ll_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_webview);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		url = getIntent().getStringExtra("url");
		urlId = getIntent().getStringExtra("urlId");
		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "0");
		userName = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		e_mail = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USEREMAIL, "");
		info = new UserInfo();
		info.userId = userid;
		info.nickName = userName;
		info.e_mail = e_mail;
		info.token = JPushInterface.getUdid(getApplicationContext());
		// 启用支持javascript
		WebSettings settings = webview.getSettings();
		settings.setUseWideViewPort(true);
	    settings.setLoadWithOverviewMode(true);
	    settings.setBuiltInZoomControls(true);//显示放大缩小 controler 
	    settings.setSupportZoom(true);
		webview.getSettings().setDomStorageEnabled(true);
		settings.setJavaScriptEnabled(true);
		webview.loadUrl(url);
		webview.loadUrl("javascript:userInfo={"
				+ "userId:'"+info.userId+"',"
				+ "nickName:'"+info.nickName+"',"
				+ "e_mail:'"+info.e_mail+"',"
				+ "token:'"+info.token+"'}");
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
				title_tv.setText(title);
			}

		};
		// 设置setWebChromeClient对象

		webview.setWebChromeClient(wvcc);
	}

	@Override
	protected void setAdapter() {

	}
   
}
