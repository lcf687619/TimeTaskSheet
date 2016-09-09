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
import com.mission.schedule.bean.FriendsBean;
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

public class MyFriendsFragmentAdapter extends CommonAdapter<FriendsBean> {

	private Handler handler;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;
	private List<FriendsBean> list;

	public MyFriendsFragmentAdapter(Context context, List<FriendsBean> lDatas,
			int layoutItemID,Handler handler) {
		super(context, lDatas, layoutItemID);
		this.list = lDatas;
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
	private void setOnclick(int positions, FriendsBean bean, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = bean;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public void getViewItem(ViewHolder holder, final FriendsBean item, final int position) {
		CircularImage friend_img = holder.getView(R.id.friend_img);
		TextView friendname_tv = holder.getView(R.id.friendname_tv);
		View myview = holder.getView(R.id.view);
		View bottomview = holder.getView(R.id.bottomview);
		ImageView v_iv = holder.getView(R.id.v_iv);

		friend_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, item, 0);
			}
		});
		// tv_richeng.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// setOnclick(position, list.get(position), 1);
		// }
		// });
		// tv_delete.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// setOnclick(position, list.get(position), 2);
		// }
		// });
		if (position == 0) {
			myview.setVisibility(View.GONE);
		}else {
			myview.setVisibility(View.VISIBLE);
		}
		if(list.size() == position+1){
			bottomview.setVisibility(View.VISIBLE);
		}else {
			bottomview.setVisibility(View.GONE);
		}
		friendname_tv.setText(item.uName);
		String imageUrl = "";
		if (!"".equals(item.titleImg)) {
			imageUrl = item.titleImg.toString();
			imageUrl = imageUrl.replace("\\", "");
		}
		// String imageurl = imageUrl+"&imageType=2&imageSizeType=3";
		// FileUtils.loadRoundHeadImg(context, ParameterUtil.userHeadImg,
		// friend_img, imageurl);
		String imageurl = URLConstants.图片 + imageUrl
				+ "&imageType=2&imageSizeType=3";
		imageLoader.displayImage(imageurl, friend_img, options,
				animateFirstListener);
//		if(list.get(position).readState==0){
//			tv_count.setVisibility(View.GONE);
//		}else {}
//		if (list.get(position).redCount == 0) {
//			tv_count.setVisibility(View.GONE);
//		} else {
//			tv_count.setVisibility(View.VISIBLE);
//			tv_count.setText(list.get(position).redCount + "");
//		}
		if(item.isV==0){
			v_iv.setVisibility(View.VISIBLE);
		}else {
			v_iv.setVisibility(View.GONE);
		}		
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
