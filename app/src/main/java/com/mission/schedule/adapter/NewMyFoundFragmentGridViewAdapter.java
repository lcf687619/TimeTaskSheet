package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.NewMyFoundFragmentItemsBeen;
import com.mission.schedule.constants.URLConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NewMyFoundFragmentGridViewAdapter extends
		CommonAdapter<NewMyFoundFragmentItemsBeen> {
	private int mScreenWidth;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public NewMyFoundFragmentGridViewAdapter(Context context,
			List<NewMyFoundFragmentItemsBeen> lDatas, int layoutItemID,
			int mScreenWidth) {
		super(context, lDatas, layoutItemID);
		this.mScreenWidth = mScreenWidth;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.newfocusgrid) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.mipmap.newfocusgrid) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.mipmap.newfocusgrid) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
		imageLoader = ImageLoader.getInstance();
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

	@Override
	public void getViewItem(ViewHolder holder,
			NewMyFoundFragmentItemsBeen item, int position) {
		ImageView bg = holder
				.getView(R.id.new_fragment_myfound_gridview_item_img);
		TextView title = holder
				.getView(R.id.new_fragment_myfound_gridview_item_title);
		TextView count = holder
				.getView(R.id.new_fragment_myfound_gridview_item_count);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) bg
				.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.height = mScreenWidth * 2 / 7;
		linearParams.width = mScreenWidth * 2 / 7;//
		bg.setLayoutParams(linearParams);
		String state = item.startStateImg.split(",")[0];
		String imageurl = null;
		if (state.equals("0")) {
			imageurl = URLConstants.图片 + item.titleImg
					+ "&imageType=2&imageSizeType=3";
		} else if (state.equals("1")) {
			imageurl = URLConstants.图片 + item.titleImg
					+ "&imageType=11&imageSizeType=1";
		}
		imageLoader.displayImage(imageurl, bg, options, animateFirstListener);
		// Picasso.with(context).load(imageurl).into(bg);
		title.setText(item.name);
		count.setText(item.clickCount);
	}
}
