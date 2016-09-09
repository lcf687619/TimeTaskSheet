package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.bean.RepeatTuiJianItemBean;
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

public class RepeatTuiJianMoreAdapter extends BaseAdapter {

	Context context;
	List<RepeatTuiJianItemBean> list;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;

	public RepeatTuiJianMoreAdapter(Context context,
			List<RepeatTuiJianItemBean> list) {
		this.context = context;
		this.list = list;
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

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewWapper viewWapper = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_repeattuijianmore, null);
			viewWapper = new ViewWapper(view);
			view.setTag(viewWapper);
		} else {
			viewWapper = (ViewWapper) view.getTag();
		}
		CircularImage friend_img = viewWapper.getImageView();
		TextView friendname_tv = viewWapper.getFriendName();
		View myview = viewWapper.getMyView();
		if (position == 0) {
			myview.setVisibility(View.VISIBLE);
		} else {
			myview.setVisibility(View.VISIBLE);
		}
		friendname_tv.setText(list.get(position).uNickName);
		String imageUrl = "";
		if (!"".equals(list.get(position).url)) {
			imageUrl = list.get(position).url.toString();
			imageUrl = imageUrl.replace("\\", "");
		}
		String imageurl = URLConstants.图片 + imageUrl
				+ "&imageType=2&imageSizeType=3";
		imageLoader.displayImage(imageurl, friend_img, options,
				animateFirstListener);
		return view;
	}

	class ViewWapper {
		private View view;
		private CircularImage friend_img;
		private TextView friendname_tv;
		private View myview;

		private ViewWapper(View view) {
			this.view = view;
		}

		private TextView getFriendName() {
			if (friendname_tv == null) {
				friendname_tv = (TextView) view
						.findViewById(R.id.friendname_tv);
			}
			return friendname_tv;
		}

		private CircularImage getImageView() {
			if (friend_img == null) {
				friend_img = (CircularImage) view.findViewById(R.id.friend_img);
			}
			return friend_img;
		}

		private View getMyView() {
			if (myview == null) {
				myview = view.findViewById(R.id.view);
			}
			return myview;
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
