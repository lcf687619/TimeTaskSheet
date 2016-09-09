package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.ScheduleTable;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShareSchWebViewActivity extends BaseActivity {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.title_name_tv)
	private TextView title_name_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.webview)
	private WebView webview;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	String username;
	String userid;
	String path;
	String title;
	String content;
	String imageUrl;

	List<Map<String, String>> mlist = new ArrayList<Map<String, String>>();
	Map<String, String> map;
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		top_ll_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		top_ll_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alterWebviewDialog();
			}
		});
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_shareschwebview);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		userid = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERID, "0");
		username = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERNAME, "");
		title_name_tv.setText(username);
		imageUrl = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.USERPHOTOPATH, "");
		path = URLConstants.分享日程 + userid + "&source=1";
		webview.getSettings().setDomStorageEnabled(true);
		// 启用支持javascript
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.loadUrl(path);
		// webview.setWebViewClient(new WebViewClient() {
		// @Override
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
		// view.loadUrl(path);
		// return true;
		// }
		// });
		try {
			mlist = App.getDBcApplication().queryAllSchData(21, 0, 0);

			if (mlist != null && mlist.size() > 0) {
				map = mlist.get(0);
				title = "@" + username + "@" + "给您分享了一个日程表";
				content = map.get(ScheduleTable.schContent);
			} else {
				title = "@" + username + "@" + "给您分享了一个日程表";
				content = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setAdapter() {

	}

	private void alterWebviewDialog() {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_shareschwebview, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
		dialog.show();

		new alterImportantDialogOnClick(dialog, more_pop_menu);
	}

	class alterImportantDialogOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private TextView refresh_tv;
		private TextView sharewxfriends_tv;
		private TextView sharefriendscircle_tv;
		private TextView canel_tv;

		@SuppressLint("NewApi")
		public alterImportantDialogOnClick(Dialog dialog, View view) {
			this.dialog = dialog;
			this.view = view;
			initview();
		}

		private void initview() {
			refresh_tv = (TextView) view.findViewById(R.id.refresh_tv);
			refresh_tv.setOnClickListener(this);
			sharewxfriends_tv = (TextView) view
					.findViewById(R.id.sharewxfriends_tv);
			sharewxfriends_tv.setOnClickListener(this);
			sharefriendscircle_tv = (TextView) view
					.findViewById(R.id.sharefriendscircle_tv);
			sharefriendscircle_tv.setOnClickListener(this);
			canel_tv = (TextView) view.findViewById(R.id.canel_tv);
			canel_tv.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.refresh_tv:
				// 清除cache
				webview.clearCache(true);
				webview.clearHistory();
				webview.reload();
				// webview.setWebViewClient(new WebViewClient(){
				// @Override
				// public boolean shouldOverrideUrlLoading(WebView view, String
				// url) {
				//
				// view.loadUrl(path); //在当前的webview中跳转到新的url
				//
				// return true;
				// }
				// });
				dialog.dismiss();
				break;
			case R.id.sharewxfriends_tv:
				showShare();
				dialog.dismiss();
				break;
			case R.id.sharefriendscircle_tv:
				showShare();
				dialog.dismiss();
				break;
			case R.id.canel_tv:
				dialog.dismiss();
				break;
			}
		}
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		// oks.setTitleUrl(path);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(content);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath(ParameterUtil.userHeadImg+imageUrl+"&imageType=2&imageSizeType=3");//
		// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(path);
		oks.setImageUrl(URLConstants.图片 + imageUrl
				+ "&imageType=2&imageSizeType=3");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		// oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		// oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}

}
