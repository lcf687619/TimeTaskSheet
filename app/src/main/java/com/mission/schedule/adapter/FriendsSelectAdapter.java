package com.mission.schedule.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.FriendsBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsSelectAdapter extends CommonAdapter<FriendsBean> {

	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;

	public FriendsSelectAdapter(Context context, List<FriendsBean> lDatas,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.img_null_smal)
				.showImageForEmptyUri(R.mipmap.img_null_smal)
				.showImageOnFail(R.mipmap.img_null_smal).cacheInMemory(true)
				.cacheOnDisc(true).cacheInMemory(true)
				.bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
				.build();
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public void getViewItem(ViewHolder holder, FriendsBean item, int position) {
		CircularImage friend_img = holder.getView(R.id.friend_img);
		TextView friendname_tv = holder.getView(R.id.friendname_tv);

		friendname_tv.setText(item.uName);
		String imageUrl = "";
		if (!"".equals(item.titleImg)) {
			imageUrl = item.titleImg.toString();
			imageUrl = imageUrl.replace("\\", "");
		}
		String imageurl = URLConstants.图片+imageUrl+"&imageType=2&imageSizeType=3";
		imageLoader.displayImage(imageurl, friend_img, options,
				animateFirstListener);
//		FileUtils.loadRoundHeadImg(context, ParameterUtil.userHeadImg,
//				friend_img, imageurl);
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
