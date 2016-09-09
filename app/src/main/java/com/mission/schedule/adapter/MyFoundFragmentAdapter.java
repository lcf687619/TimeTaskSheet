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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.bean.FocusFriendsBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.onRightViewWidthListener;
import com.mission.schedule.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MyFoundFragmentAdapter extends BaseAdapter implements
		onRightViewWidthListener {

	private List<FocusFriendsBean> list;
	Context context;
	private Handler handler;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;
	private SwipeXListViewNoHead swipeXlistview;
	int uncount;
	int unfocuscount;

	public MyFoundFragmentAdapter(Context context, List<FocusFriendsBean> list,
			Handler handler, SwipeXListViewNoHead swipeXlistview, int uncount,
			int unfocuscount) {
		this.context = context;
		this.list = list;
		this.handler = handler;
		this.swipeXlistview = swipeXlistview;
		this.uncount = uncount;
		this.unfocuscount = unfocuscount;
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

	private void setOnclick(int positions, FocusFriendsBean bean, int what) {
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
					R.layout.adapter_myfound, null);
			viewWapper = new ViewWapper(view);
			view.setTag(viewWapper);
		} else {
			viewWapper = (ViewWapper) view.getTag();
		}
		CircularImage friend_img = viewWapper.getImageView();
		TextView friendname_tv = viewWapper.getFriendName();
		View myview = viewWapper.getMyView();
		TextView tv_delete = viewWapper.getDeleteText();
		TextView tv_cancle = viewWapper.getCancleText();
		TextView unfound_tv = viewWapper.getUnFocusText();
		LinearLayout delete_ll = viewWapper.getDelete_LL();
		ImageView v_iv = viewWapper.getV_IV();
		TextView shuaxin_tv = viewWapper.getRefresh_TV();
		TextView focus_tv = viewWapper.getFocus_TV();
		TextView found_tv = viewWapper.getFound_TV();
		TextView addfocus_rl = viewWapper.getAddFocus_TV();
		RelativeLayout content_rl = viewWapper.getContent_RL();

		friend_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, list.get(position), 0);
			}
		});
		content_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setOnclick(position, list.get(position), 0);
			}
		});
		shuaxin_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, list.get(position), 1);
			}
		});
		focus_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, list.get(position), 2);
			}
		});
		tv_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, list.get(position), 3);
			}
		});
		tv_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, list.get(position), 4);
			}
		});
		addfocus_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, list.get(position), 5);
			}
		});
		if (uncount == 0) {
			if (position == uncount) {
				found_tv.setVisibility(View.VISIBLE);
				myview.setVisibility(View.GONE);
				content_rl.setVisibility(View.GONE);
				addfocus_rl.setVisibility(View.VISIBLE);
				if ("0".equals(list.get(position).isV)) {
					unfound_tv.setVisibility(View.GONE);
					tv_delete.setVisibility(View.GONE);
					tv_cancle.setVisibility(View.GONE);
					shuaxin_tv.setVisibility(View.GONE);
					focus_tv.setVisibility(View.GONE);
				} else {
					unfound_tv.setVisibility(View.GONE);
					tv_delete.setVisibility(View.GONE);
					tv_cancle.setVisibility(View.GONE);
					shuaxin_tv.setVisibility(View.GONE);
					focus_tv.setVisibility(View.GONE);
				}
			} else {
				content_rl.setVisibility(View.VISIBLE);
				found_tv.setVisibility(View.GONE);
				addfocus_rl.setVisibility(View.GONE);
				if (unfocuscount == 0) {
					myview.setVisibility(View.GONE);
					unfound_tv.setVisibility(View.GONE);
					tv_delete.setVisibility(View.GONE);
					tv_cancle.setVisibility(View.GONE);
					shuaxin_tv.setVisibility(View.GONE);
					focus_tv.setVisibility(View.GONE);

				} else {
					if (position == 1) {
						addfocus_rl.setVisibility(View.GONE);
						myview.setVisibility(View.GONE);
						unfound_tv.setVisibility(View.VISIBLE);
					} else {
						addfocus_rl.setVisibility(View.GONE);
						myview.setVisibility(View.VISIBLE);
						unfound_tv.setVisibility(View.GONE);
					}
					if ("0".equals(list.get(position).isV)) {
						tv_delete.setVisibility(View.VISIBLE);
						tv_cancle.setVisibility(View.GONE);
						shuaxin_tv.setVisibility(View.GONE);
						focus_tv.setVisibility(View.VISIBLE);
					} else {
						tv_delete.setVisibility(View.VISIBLE);
						tv_cancle.setVisibility(View.GONE);
						shuaxin_tv.setVisibility(View.GONE);
						focus_tv.setVisibility(View.GONE);
					}
				}
			}
		} else {
			addfocus_rl.setVisibility(View.GONE);
			if (position == uncount) {
				found_tv.setVisibility(View.GONE);
				if ("0".equals(list.get(position).isV)) {
					unfound_tv.setVisibility(View.VISIBLE);
					tv_cancle.setVisibility(View.GONE);
					tv_delete.setVisibility(View.VISIBLE);
					shuaxin_tv.setVisibility(View.GONE);
					focus_tv.setVisibility(View.VISIBLE);
				} else {
					unfound_tv.setVisibility(View.VISIBLE);
					tv_cancle.setVisibility(View.GONE);
					tv_delete.setVisibility(View.VISIBLE);
					shuaxin_tv.setVisibility(View.GONE);
					focus_tv.setVisibility(View.GONE);
				}
			} else if (position > uncount) {
				found_tv.setVisibility(View.GONE);
				// addfocus_rl.setVisibility(View.GONE);
				if ("0".equals(list.get(position).isV)) {
					unfound_tv.setVisibility(View.GONE);
					tv_cancle.setVisibility(View.GONE);
					tv_delete.setVisibility(View.VISIBLE);
					shuaxin_tv.setVisibility(View.GONE);
					focus_tv.setVisibility(View.VISIBLE);
				} else {
					unfound_tv.setVisibility(View.GONE);
					tv_cancle.setVisibility(View.GONE);
					tv_delete.setVisibility(View.VISIBLE);
					shuaxin_tv.setVisibility(View.GONE);
					focus_tv.setVisibility(View.GONE);
				}
			} else {
				if (position == 0) {
					found_tv.setVisibility(View.VISIBLE);
					// addfocus_rl.setVisibility(View.GONE);
				} else {
					found_tv.setVisibility(View.GONE);
					// addfocus_rl.setVisibility(View.GONE);
				}
				unfound_tv.setVisibility(View.GONE);
				tv_delete.setVisibility(View.GONE);
				tv_cancle.setVisibility(View.VISIBLE);
				shuaxin_tv.setVisibility(View.VISIBLE);
				focus_tv.setVisibility(View.GONE);
			}
		}
		if ("0".equals(list.get(position).isV)) {
			v_iv.setVisibility(View.VISIBLE);
		} else {
			v_iv.setVisibility(View.GONE);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				Utils.dipTopx(context, 80), Utils.dipTopx(context, 60));
		if (unfound_tv.getVisibility() == View.VISIBLE
				&& found_tv.getVisibility() == View.VISIBLE) {
			params.setMargins(0, Utils.dipTopx(context, 51), 0, 0);
			delete_ll.setLayoutParams(params);
		} else if (found_tv.getVisibility() == View.VISIBLE) {
			params.setMargins(0, Utils.dipTopx(context, 51), 0, 0);
			delete_ll.setLayoutParams(params);
		} else if (unfound_tv.getVisibility() == View.VISIBLE) {
			// if(addfocus_rl.getVisibility()==View.VISIBLE){
			params.setMargins(0, Utils.dipTopx(context, 51), 0, 0);
			delete_ll.setLayoutParams(params);
			// } else {
			// params.setMargins(0, 0, 0, 0);
			// delete_ll.setLayoutParams(params);
			// }
		} else {
			// if(addfocus_rl.getVisibility()==View.VISIBLE){
			// params.setMargins(0, Utils.dipTopx(context, 10), 0, 0);
			// delete_ll.setLayoutParams(params);
			// } else {
			params.setMargins(0, 0, 0, 0);
			delete_ll.setLayoutParams(params);
			// }
		}
		if (uncount == 0) {
			if (position != uncount) {
				friendname_tv.setText(list.get(position).uname);
				String imageUrl = "";
				if (!"".equals(list.get(position).titleImg)) {
					imageUrl = list.get(position).titleImg.toString();
					imageUrl = imageUrl.replace("\\", "");
				}
				// String imageurl = imageUrl+"&imageType=2&imageSizeType=3";
				// FileUtils.loadRoundHeadImg(context,
				// ParameterUtil.userHeadImg,
				// friend_img, imageurl);
				String imageurl = URLConstants.图片 + imageUrl
						+ "&imageType=2&imageSizeType=3";
				imageLoader.displayImage(imageurl, friend_img, options,
						animateFirstListener);
			}
		} else {
			friendname_tv.setText(list.get(position).uname);
			String imageUrl = "";
			if (!"".equals(list.get(position).titleImg)) {
				imageUrl = list.get(position).titleImg.toString();
				imageUrl = imageUrl.replace("\\", "");
			}
			// String imageurl = imageUrl+"&imageType=2&imageSizeType=3";
			// FileUtils.loadRoundHeadImg(context, ParameterUtil.userHeadImg,
			// friend_img, imageurl);
			String imageurl = URLConstants.图片 + imageUrl
					+ "&imageType=2&imageSizeType=3";
			imageLoader.displayImage(imageurl, friend_img, options,
					animateFirstListener);
		}

		return view;
	}

	class ViewWapper {
		private View view;
		private CircularImage friend_img;
		private TextView friendname_tv;
		private View myview;
		private TextView tv_delete;
		private TextView tv_cancle;
		private TextView unfound_tv;
		private LinearLayout delete_ll;
		private ImageView v_iv;
		private TextView shuaxin_tv;
		private TextView focus_tv;
		private TextView found_tv;
		private TextView addfocus_rl;
		private RelativeLayout content_rl;

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

		private TextView getDeleteText() {
			if (tv_delete == null) {
				tv_delete = (TextView) view.findViewById(R.id.tv_delete);
			}
			return tv_delete;
		}

		private TextView getCancleText() {
			if (tv_cancle == null) {
				tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
			}
			return tv_cancle;
		}

		private TextView getUnFocusText() {
			if (unfound_tv == null) {
				unfound_tv = (TextView) view.findViewById(R.id.unfound_tv);
			}
			return unfound_tv;
		}

		private LinearLayout getDelete_LL() {
			if (delete_ll == null) {
				delete_ll = (LinearLayout) view.findViewById(R.id.delete_ll);
			}
			return delete_ll;
		}

		private ImageView getV_IV() {
			if (v_iv == null) {
				v_iv = (ImageView) view.findViewById(R.id.v_iv);
			}
			return v_iv;
		}

		private TextView getRefresh_TV() {
			if (shuaxin_tv == null) {
				shuaxin_tv = (TextView) view.findViewById(R.id.shuaxin_tv);
			}
			return shuaxin_tv;
		}

		private TextView getFocus_TV() {
			if (focus_tv == null) {
				focus_tv = (TextView) view.findViewById(R.id.focus_tv);
			}
			return focus_tv;
		}

		private TextView getFound_TV() {
			if (found_tv == null) {
				found_tv = (TextView) view.findViewById(R.id.found_tv);
			}
			return found_tv;
		}

		private TextView getAddFocus_TV() {
			if (addfocus_rl == null) {
				addfocus_rl = (TextView) view.findViewById(R.id.addfocus_rl);
			}
			return addfocus_rl;
		}

		private RelativeLayout getContent_RL() {
			if (content_rl == null) {
				content_rl = (RelativeLayout) view
						.findViewById(R.id.content_rl);
			}
			return content_rl;
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

	@Override
	public void onRightViewWidth(int position) {
		if (uncount == 0) {
			if (position == 3) {
				swipeXlistview.setRightViewWidth(context.getResources()
						.getDimensionPixelSize(R.dimen.friends_item_0));
			} else {
				swipeXlistview.setRightViewWidth(context.getResources()
						.getDimensionPixelSize(R.dimen.friends_item_80));
			}
		} else {
			swipeXlistview.setRightViewWidth(context.getResources()
					.getDimensionPixelSize(R.dimen.friends_item_80));
		}
	}

}
