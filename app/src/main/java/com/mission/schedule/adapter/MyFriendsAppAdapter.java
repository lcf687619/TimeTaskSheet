package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.FriendsBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.onRightViewWidthListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MyFriendsAppAdapter extends CommonAdapter<FriendsBean> implements
		onRightViewWidthListener {

	private Handler handler;
	// private int mRightWidth = 0;
	private SwipeXListViewNoHead swipeXlistview;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;

	public MyFriendsAppAdapter(Context context, List<FriendsBean> lDatas,
			int layoutItemID, Handler handler,
			SwipeXListViewNoHead swipeXlistview) {
		super(context, lDatas, layoutItemID);
		this.handler = handler;
		this.swipeXlistview = swipeXlistview;
		swipeXlistview.setonRightViewWidthListener(this);
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

	private void setOnclick(int positions, FriendsBean mapBean, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = mapBean;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public void onRightViewWidth(int position) {
		// if (position == 3) {
		// swipeXlistview.setRightViewWidth(context.getResources()
		// .getDimensionPixelSize(R.dimen.sch_item_180));
		// } else {
		swipeXlistview.setRightViewWidth(context.getResources()
				.getDimensionPixelSize(R.dimen.item_height));
		// }
	}

	@Override
	public void getViewItem(ViewHolder holder, final FriendsBean item,
			final int position) {
		CircularImage friendsImage_iv = holder.getView(R.id.friendsImage_iv);
		TextView friendsName_tv = holder.getView(R.id.friendsName_tv);
		TextView bsqstate_tv = holder.getView(R.id.bsqstate_tv);
		TextView sqstate_tv = holder.getView(R.id.sqstate_tv);
		Button tianjia_bt = holder.getView(R.id.tianjia_bt);
		Button tianjia1_bt = holder.getView(R.id.tianjia1_bt);
		TextView tv_finish = holder.getView(R.id.tv_finish);
		TextView tv_delete = holder.getView(R.id.tv_delete);
		View myview = holder.getView(R.id.myview);

		if (position == 0) {
			myview.setVisibility(View.GONE);
		} else {
			myview.setVisibility(View.VISIBLE);
		}

		int type = item.type;// 0被申请，1表示向别人申请
		String imageUrl = "";
		if (!"".equals(item.titleImg)) {
			imageUrl = item.titleImg.toString();
			imageUrl = imageUrl.replace("\\", "");
		}
		String imageurl = URLConstants.图片 + imageUrl
				+ "&imageType=2&imageSizeType=3";
		imageLoader.displayImage(imageurl, friendsImage_iv, options,
				animateFirstListener);
		// String imageurl = imageUrl + "&imageType=2&imageSizeType=3";
		// FileUtils.loadRoundHeadImg(context, ParameterUtil.userHeadImg,
		// friendsImage_iv, imageurl);
		if (type == 0) {
			bsqstate_tv.setVisibility(View.VISIBLE);
			sqstate_tv.setVisibility(View.GONE);
			tv_finish.setVisibility(View.VISIBLE);
			tv_delete.setVisibility(View.GONE);
			tianjia_bt.setVisibility(View.VISIBLE);
			tianjia1_bt.setVisibility(View.GONE);
			// friendsImage_iv.setImageResource(context.getResources().getDrawable(R.drawable.bg));
			friendsName_tv.setText(item.uName);
			tianjia_bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 接受
					setOnclick(position, item, 0);
				}
			});
			tv_finish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 右滑忽略
					setOnclick(position, item, 1);
				}
			});
		} else {
			bsqstate_tv.setVisibility(View.GONE);
			sqstate_tv.setVisibility(View.VISIBLE);
			tv_finish.setVisibility(View.GONE);
			tv_delete.setVisibility(View.VISIBLE);
			tianjia_bt.setVisibility(View.GONE);
			tianjia1_bt.setVisibility(View.VISIBLE);
			// friendsImage_iv.setImageResource(context.getResources().getDrawable(R.drawable.bg));
			friendsName_tv.setText(item.uName);
			tianjia1_bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 等待确认
					// setOnclick(position, mMap, 0);
				}
			});

			tv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 右滑删除
					setOnclick(position, item, 2);
				}
			});
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
