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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class JiFenGuiZeActivity extends BaseActivity {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.webview)
	private WebView webview;

	String path;
	Context context;
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
		setContentView(R.layout.activity_jifenguize);
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
		path = URLConstants.积分规则;
		// 启用支持javascript
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
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
	}

	@Override
	protected void setAdapter() {

	}

}
