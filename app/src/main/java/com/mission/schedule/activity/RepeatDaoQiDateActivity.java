package com.mission.schedule.activity;


import cn.jpush.android.api.JPushInterface;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.bean.UserInfo;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RepeatDaoQiDateActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.save_ll_right)
	private RelativeLayout save_ll_right;
	@ViewResId(id = R.id.webview)
	private WebView webview;

	Context context;
	SharedPrefUtil prefUtil = null;
	String userid;
	String path;
	String userName;
	String e_mail;
	UserInfo info = null;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
		save_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_repeatdaoqidate);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userid = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		userName = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		e_mail = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USEREMAIL, "");
		
		info = new UserInfo();
		info.userId = userid;
		info.nickName = userName;
		info.e_mail = e_mail;
		info.token = JPushInterface.getUdid(getApplicationContext());
		
		middle_tv.setText("到期日管理");
		save_ll_right.setVisibility(View.GONE);
		top_ll_right.setVisibility(View.VISIBLE);
		path = URLConstants.重复到期日管理 + userid;
		webview.getSettings().setDomStorageEnabled(true);
		// 启用支持javascript
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
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
					if("新建".equals(title)){
						middle_tv.setText(title+"到期日");
						save_ll_right.setVisibility(View.VISIBLE);
						top_ll_right.setVisibility(View.GONE);
					}else if("到期日管理".equals(title)){
						middle_tv.setText("到期日管理");
						save_ll_right.setVisibility(View.GONE);
						top_ll_right.setVisibility(View.VISIBLE);
					}else if("编辑".equals(title)){
						middle_tv.setText(title);
						save_ll_right.setVisibility(View.VISIBLE);
						top_ll_right.setVisibility(View.GONE);
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
		case R.id.top_ll_back:
			if (webview.canGoBack()) {
				webview.goBack();
			} else {
				webview.clearHistory();
				webview.clearCache(true);
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			break;
		case R.id.top_ll_right:
			 webview.loadUrl("javascript:TZtianjia()");
			break;
		case R.id.save_ll_right:
			webview.loadUrl("javascript:tianjia()");
			// 清除cache
//			 webview.clearCache(true);
//			 webview.clearHistory();
//			 webview.reload();
			break;
		default:
			break;
		}
	}
}
