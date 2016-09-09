package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.bean.FocusFriendsBean;
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

public class AddFocusOnAdapter extends BaseAdapter {

	private List<FocusFriendsBean> list;
	Context context;
	private Handler handler;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;

	public AddFocusOnAdapter(Context context, List<FocusFriendsBean> list,
			Handler handler) {
		this.context = context;
		this.list = list;
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

	private void setOnclick(int positions, FocusFriendsBean bean,int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = bean;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewWapper viewWapper = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_addfocuson, null);
			viewWapper = new ViewWapper(view);
			view.setTag(viewWapper);
		} else {
			viewWapper = (ViewWapper) view.getTag();
		}
		final FocusFriendsBean friendsBean = list.get(position);
		CircularImage friend_img = viewWapper.getImageView();
		TextView friendname_tv = viewWapper.getFriendName();
		TextView add_tv = viewWapper.getAddTextView();
		TextView sure_tv = viewWapper.getSureTextView();
		LinearLayout focus_ll = viewWapper.getFocus_LL();
		ImageView v_iv = viewWapper.getV_IV();
		friendname_tv.setText(list.get(position).uname);
		String imageUrl = "";
		if (!"".equals(friendsBean.titleImg)) {
			imageUrl = friendsBean.titleImg.toString();
		}
		String imageurl =URLConstants.图片+imageUrl + "&imageType=2&imageSizeType=3";
		imageLoader.displayImage(imageurl, friend_img, options,
				animateFirstListener);
		
		// FileUtils.loadRoundHeadImg(context, ParameterUtil.friendHeadIma,
		// friend_img, imageurl);

		if("1".equals(friendsBean.attState)){
			add_tv.setVisibility(View.VISIBLE);
			sure_tv.setVisibility(View.GONE);
		}else {
			add_tv.setVisibility(View.GONE);
			sure_tv.setVisibility(View.VISIBLE);
		}
		if("0".equals(friendsBean.isV)){
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
		focus_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setOnclick(position, friendsBean ,2);
			}
		});
		add_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, friendsBean ,0);
			}
		});
		return view;
	}

	class ViewWapper {
		private View view;
		private CircularImage friend_img;
		private TextView friendname_tv;
		private TextView add_tv;
		private TextView sure_tv;
		private LinearLayout focus_ll;
		private ImageView v_iv;

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

		private TextView getAddTextView() {
			if (add_tv == null) {
				add_tv = (TextView) view.findViewById(R.id.add_tv);
			}
			return add_tv;
		}
		private TextView getSureTextView(){
			if(sure_tv == null){
				sure_tv = (TextView) view.findViewById(R.id.sure_tv);
			}
			return sure_tv;
		}
		private CircularImage getImageView() {
			if (friend_img == null) {
				friend_img = (CircularImage) view.findViewById(R.id.friend_img);
			}
			return friend_img;
		}
		private LinearLayout getFocus_LL(){
			if(focus_ll == null){
				focus_ll = (LinearLayout) view.findViewById(R.id.focus_ll);
			}
			return focus_ll;
		}
		private ImageView getV_IV(){
			if(v_iv == null){
				v_iv = (ImageView) view.findViewById(R.id.v_iv);
			}
			return v_iv;
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
