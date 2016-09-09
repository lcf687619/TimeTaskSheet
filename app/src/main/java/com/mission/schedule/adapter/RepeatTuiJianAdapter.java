package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.bean.RepeatTuiJianItemBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.utils.ParameterUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RepeatTuiJianAdapter extends BaseAdapter {

	private Context context;
	List<RepeatTuiJianItemBean> tuijians;
	int count;
	private Handler handler;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;

	public RepeatTuiJianAdapter(Context context,
			List<RepeatTuiJianItemBean> tuijians, int count,Handler handler) {
		this.context = context;
		this.tuijians = tuijians;
		this.count = count;
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
		return tuijians == null ? 0 : tuijians.size();
	}

	@Override
	public Object getItem(int position) {
		return tuijians.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private void setOnclick(int positions, RepeatTuiJianItemBean itemBean) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = itemBean;
		handler.sendMessage(message);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewWapper viewWapper = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_repeatgridview, null);
			viewWapper = new ViewWapper(view);
			view.setTag(viewWapper);
		} else {
			viewWapper = (ViewWapper) view.getTag();
		}
		CircularImage image = viewWapper.getImageView();
		TextView text = viewWapper.getTextName();
		String imageurl = ParameterUtil.webServiceUrl+tuijians.get(position).url.toString().replace("\\",
				"")+ "&imageType=2&imageSizeType=3";
//		FileUtils.loadRoundHeadImg(context, ParameterUtil.repeatHeadImg, image,
//				imageurl);
		text.setText(tuijians.get(position).uNickName);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
		if (count >= 7) {
			if (position == 7) {
//				params.width = Utils.dipTopx(context, 60);
//				params.height = Utils.dipTopx(context, 60);
//				params.setMargins(0, Utils.dipTopx(context, 3), 0, 0);
//				params1.setMargins(0, Utils.dipTopx(context, 10), 0, 0);
				image.setImageResource(R.mipmap.btn_gengduo1);
//				image.setLayoutParams(params);
				text.setText("更多");
//				text.setLayoutParams(params1);
			}else{
				imageLoader.displayImage(imageurl, image, options,
						animateFirstListener);
			}
		} else {
			if (position == count) {
//				params.width = Utils.dipTopx(context, 60);
//				params.height = Utils.dipTopx(context, 60);
//				params1.setMargins(0, Utils.dipTopx(context, 5), 0, 0);
				image.setImageResource(R.mipmap.btn_gengduo1);
//				image.setLayoutParams(params);
				text.setText("更多");
//				text.setLayoutParams(params1);
			}else{
				imageLoader.displayImage(imageurl, image, options,
						animateFirstListener);
			}
		}
		image.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setOnclick(position, tuijians.get(position));
				return false;
			}
		});
		return view;
	}

	class ViewWapper {

		private View view;
		private CircularImage iamge;
		private TextView text;

		private ViewWapper(View view) {
			this.view = view;
		}

		private CircularImage getImageView() {
			if (iamge == null) {
				iamge = (CircularImage) view.findViewById(R.id.iamge);
			}
			return iamge;
		}

		private TextView getTextName() {
			if (text == null) {
				text = (TextView) view.findViewById(R.id.text);
			}
			return text;
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
