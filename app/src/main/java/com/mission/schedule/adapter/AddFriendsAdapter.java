package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.AddFriendsBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.constants.URLConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AddFriendsAdapter extends CommonAdapter<AddFriendsBean> {

	Context context;
	private Handler handler;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;
	
	public AddFriendsAdapter(Context context, List<AddFriendsBean> lDatas,
			int layoutItemID,Handler handler) {
		super(context, lDatas, layoutItemID);
		this.context = context;
		this.handler = handler;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.img_null_smal) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.mipmap.img_null_smal) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.mipmap.img_null_smal) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
		imageLoader = ImageLoader.getInstance();
	}

	private void setOnclick(int positions, AddFriendsBean bean,int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = bean;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public void getViewItem(ViewHolder holder, final AddFriendsBean friendsBean, final int position) {
		CircularImage friend_img = holder.getView(R.id.friend_img);
		TextView friendname_tv = holder.getView(R.id.friendname_tv);
		TextView add_tv = holder.getView(R.id.add_tv);
		TextView sure_tv = holder.getView(R.id.sure_tv);
		ImageView v_iv = holder.getView(R.id.v_iv);
		
		friendname_tv.setText(friendsBean.uname);
		String imageUrl = "";
		if (!"".equals(friendsBean.titleImg)) {
			imageUrl = friendsBean.titleImg.toString();
		}
		String imageurl =URLConstants.图片+imageUrl + "&imageType=2&imageSizeType=3";
		imageLoader.displayImage(imageurl, friend_img, options,
				animateFirstListener);
		
		// FileUtils.loadRoundHeadImg(context, ParameterUtil.friendHeadIma,
		// friend_img, imageurl);

		if(friendsBean.flag){
			add_tv.setVisibility(View.VISIBLE);
			sure_tv.setVisibility(View.GONE);
		}else {
			add_tv.setVisibility(View.GONE);
			sure_tv.setVisibility(View.VISIBLE);
		}
		if(friendsBean.isV == 0){
			v_iv.setVisibility(View.VISIBLE);
		}else {
			v_iv.setVisibility(View.GONE);
		}
		friend_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setOnclick(position, friendsBean ,1);
			}
		});
		add_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, friendsBean ,0);
			}
		});
	}

	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 * 
	 */
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
