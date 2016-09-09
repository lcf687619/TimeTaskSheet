package com.mission.schedule.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.mission.schedule.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ImageSlideView {
	private Context context;
	private View slide;
	private MyViewPager vPager;
//	private TextView showTitle;
//	private LinearLayout viewGroup;
	private View[] views;
	public List<ImageSlide> mListViews;
	private ImageSlide imageSlide;
	
	private final int SLIDE_MAXNUM = 6;
	private Timer timer; // 计时器
	private int index = 0;
	private OnItemClickListener listener;
	
	public ImageSlideView(Context context,List<ImageSlide> listView,ViewGroup phone_box){
		this.context = context;
		if(listView.size()>SLIDE_MAXNUM){
			mListViews = new ArrayList<ImageSlide>();
			for (int i = 0; i < SLIDE_MAXNUM; i++) {
				ImageSlide slide = listView.get(i);
				mListViews.add(slide);
			}
		}else this.mListViews = listView;
		
		slide = View.inflate(context, R.layout.com_view_image_slide, phone_box);
		
		vPager = (MyViewPager) slide.findViewById(R.id.vPager);
		vPager.setOffscreenPageLimit(1);//1 预加载 0 非
		vPager.setAdapter(new MainTopPagerAdapter());
		vPager.setCurrentItem(0);
		vPager.setOnPageChangeListener(new PageChangeListener());
//		showTitle = (TextView) slide.findViewById(R.id.showTitle);
//		viewGroup = (LinearLayout) slide.findViewById(R.id.viewGroup);
		
		views = new View[mListViews.size()];
		for (int i = 0; i < mListViews.size(); i++) {
			ImageView imageView = new ImageView(context);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT); 
//			lp.setMargins(5, 5, 0, 0); 
			imageView.setLayoutParams(lp);
			views[i] = imageView;
//			viewGroup.addView(views[i]);
		}
		setpic(0);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}
	
	public void startScroll() {
		startScroll(5000);
	}
	
	public void startScroll(long period) {
		timer = new Timer();
		timer.schedule(new ScrollTask(), new Date(), 5000);
	}
	
	public void stopScroll() {
		timer.cancel();
		timer = null;
	}
	
	/**
	 * 自定义计时器任务
	 */
	private class ScrollTask extends TimerTask {

		@Override
		public void run() {
			index = vPager.getCurrentItem();
			if (index == mListViews.size() - 1) index = 0;
			else index++;
			
			vPager.post(new Runnable() {
				@Override
				public void run() {
					vPager.setAdapter(new MainTopPagerAdapter());
					vPager.setCurrentItem(index);
				}
			});
		}
	}
	
	public void setpic(int position) {
		for (int i = 0; i < views.length; i++) {
			if (i == position) {
				imageSlide = mListViews.get(position);
//				showTitle.setText(StringUtils.getLimitLengthString(imageSlide.getTitle(), 13, "..."));
				views[i].setBackgroundResource(R.mipmap.com_image_slide_dian_focus);
			} else {
				views[i].setBackgroundResource(R.mipmap.com_image_slide_dian_unfocus);
			}
		}
	}

	public class MainTopPagerAdapter extends PagerAdapter {
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
//			if (mListViews.size() == 0) {
//				return 1;
//			}
			 return Integer.MAX_VALUE;//设置成最大值以便循环滑动
            		
//			return mListViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (mListViews.size() == 0) {
				ImageView imageView = new ImageView(context);
				//imageView.setImageResource(R.drawable.com_imageslide_default);
				container.addView(imageView);
				return imageView;
			}
			
			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setTag(position);
			imageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int position = (Integer) v.getTag();
					    position = position % mListViews.size();
					listener.onItemClick(null, null, position, 0);
				}
			});
			position = position % mListViews.size();
			ImageSlide slide = mListViews.get(position);
			if (slide != null) {
				Uri uri = slide.getUri();
				if (uri.getScheme().toLowerCase().equals("http")){
					DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
//					ImageLoader.init(ImageLoaderConfiguration.createDefault(context));
					ImageManager.Load(uri.toString(), imageView, options);
				}else{
					imageView.setImageURI(uri);
				}
			}
			container.addView(imageView);
			return imageView;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}
	}
	
	public class PageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,int positionOffsetPixels) {
			
		}

		@Override
		public void onPageSelected(int position) {
			setpic(position % mListViews.size());
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
	}
	
	public static class ImageSlide{
		private String title;
		private Uri uri;
		private Object obj;
		
		public ImageSlide(String title, Uri uri) {
			this.title = title;
			this.uri = uri;
		}
		
		public ImageSlide(String title, Uri uri, Object obj) {
			this.title = title;
			this.uri = uri;
			this.obj = obj;
		}

		public String getTitle() {
			return title;
		}

		public Uri getUri() {
			return uri;
		}

		public Object getObj() {
			return obj;
		}
	}
}
