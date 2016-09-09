package com.mission.schedule.activity;

import cn.jpush.android.api.JPushInterface;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.bean.UserInfo;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JiFenDuiHuanActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.title_tv)
	private TextView title_tv;
	@ViewResId(id = R.id.top_ll_right)
	private LinearLayout top_ll_right;
	@ViewResId(id = R.id.webview)
	private WebView webview;

	Context context;
	String userid;
	String userName;
	String e_mail;
	String path;
	SharedPrefUtil sharedPrefUtil = null;
	UserInfo info = null;

	@Override
	protected void setListener() {
		top_ll_left.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_jifenduihuan);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
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
		
		path = URLConstants.积分兑换 + userid;
		// 启用支持javascript
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
//		webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webview.loadUrl(path);
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
				// view.loadUrl(URLConstants.积分规则);
				return true;
			}
		});
		WebChromeClient wvcc = new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {

				super.onReceivedTitle(view, title);
				if ("9宫格".equals(title)) {
					title_tv.setText("积分兑换");
				} else {
					title_tv.setText(title);
				}
			}

		};
		// 设置setWebChromeClient对象

		webview.setWebChromeClient(wvcc);
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_left:
			if (webview.canGoBack()) {
				webview.goBack();
			} else {
				finish();
			}
			break;
		case R.id.top_ll_right:
			startActivity(new Intent(context, JiFenGuiZeActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 竖屏
			if (webview.canGoBack()) {
				webview.goBack();
				  return true;    //已处理   
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
