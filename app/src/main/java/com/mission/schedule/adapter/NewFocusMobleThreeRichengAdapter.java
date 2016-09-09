package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.FriendsRiChengBean;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class NewFocusMobleThreeRichengAdapter extends
		CommonAdapter<FriendsRiChengBean> {

	private Handler handler;
	private int mScreenWidth;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public NewFocusMobleThreeRichengAdapter(Context context,
			List<FriendsRiChengBean> lDatas, int layoutItemID, Handler handler,
			int mScreenWidth) {
		super(context, lDatas, layoutItemID);
		this.handler = handler;
		this.mScreenWidth = mScreenWidth;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.av) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.mipmap.av) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.mipmap.av) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
		imageLoader = ImageLoader.getInstance();
	}

	private void setOnclick(int positions, FriendsRiChengBean item, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = item;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public void getViewItem(ViewHolder holder, final FriendsRiChengBean item,
			final int position) {
		View foot=holder.getView(R.id.bg_view_moble_three_foot);
		LinearLayout bottomitem_ll = holder.getView(R.id.bottomitem_ll_three);
		TextView timeall_tv_daycount = holder.getView(R.id.timeall_tv_daycount_three);
		TextView timeall_tv_daycount_text_three= holder.getView(R.id.timeall_tv_daycount_text_three);
		LinearLayout date_all = (LinearLayout) holder
				.getView(R.id.date_all_three);
		TextView content_tv = (TextView) holder.getView(R.id.content_tv_three);
		// TextView time_tv = (TextView) holder.getView(R.id.time_tv);
		// ImageView guoqi_img = (ImageView) holder.getView(R.id.guoqi_img);
		RelativeLayout bgimglayout = (RelativeLayout) holder
				.getView(R.id.bg_img_moble_three_all);
		RelativeLayout bgimg = (RelativeLayout) holder
				.getView(R.id.bg_img_moble_three);
		if (item.imgPath == null || item.imgPath.equals("")) {
			bgimglayout.setVisibility(View.GONE);					
			foot.setVisibility(View.VISIBLE);					
		} else {
			foot.setVisibility(View.GONE);
			bgimglayout.setVisibility(View.VISIBLE);
			String imgurlend="";
			if(item.imgPath.contains(".png@")){
				imgurlend=item.imgPath.replace("@", "");
			}else{
				imgurlend=item.imgPath;
			}
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) bgimg
					.getLayoutParams();
			linearParams.height = (mScreenWidth - Utils.dipTopx(context, 20)) * 6 / 10;
			bgimg.setLayoutParams(linearParams);
			ImageView img = (ImageView) holder
					.getView(R.id.bg_imgview_moble_three);
			String runableimgurl = URLConstants.图片
					+ imgurlend.replace("\\/", "")
					+ "&imageType=14&imageSizeType=1";
			imageLoader.displayImage(runableimgurl, img, options,
					animateFirstListener);
		}
		bottomitem_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, item, 3);
			}
		});

		String today, tomorrow;
		// String nongliday = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		today = DateUtil.formatDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.get(Calendar.DAY_OF_MONTH) + 1);
		tomorrow = DateUtil.formatDate(calendar.getTime());
		Date dateStr = DateUtil.parseDate(item.CDate);
		Date dateToday = DateUtil.parseDate(today);
		long betweem = (long) (dateToday.getTime() - dateStr.getTime()) / 1000;
		long day = betweem / (24 * 3600);

		date_all.setVisibility(View.VISIBLE);
		if (today
				.equals(DateUtil.formatDate(DateUtil.parseDate(item.CDate)))) {
			timeall_tv_daycount_text_three.setVisibility(View.GONE);
			timeall_tv_daycount.setText(context
					.getString(R.string.adapter_today));				
			
		} else if (tomorrow.equals(DateUtil.formatDate(DateUtil
				.parseDate(item.CDate)))) {
			timeall_tv_daycount_text_three.setVisibility(View.GONE);
			timeall_tv_daycount.setText(context
					.getString(R.string.adapter_tomorrow));					
			
		} else {	
			timeall_tv_daycount_text_three.setVisibility(View.VISIBLE);
			timeall_tv_daycount.setText(""+  Math.abs(day));

		}
		content_tv.setText(item.CContent);
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
