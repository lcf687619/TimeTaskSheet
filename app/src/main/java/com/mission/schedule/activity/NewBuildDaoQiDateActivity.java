package com.mission.schedule.activity;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewBuildDaoQiDateActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.right_tv)
	private TextView right_tv;
	@ViewResId(id = R.id.webview)
	private WebView webview;

	Context context;
	SharedPrefUtil prefUtil = null;
	String userid;
	String path;
	String type;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newbuilddaoqidate);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		prefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		middle_tv.setText("新建到期日");
		right_tv.setTextColor(context.getResources().getColor(
				R.color.mingtian_color));
		userid = prefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "");
		type = getIntent().getStringExtra("type");
		path = URLConstants.新建到期日 + userid + "&type=" + type;
		// 启用支持javascript
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.loadUrl(path);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;
		case R.id.top_ll_right:
			webview.loadUrl("javascript:tianjia()");
			this.finish();
			break;
		default:
			break;
		}
	}

}
