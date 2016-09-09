package com.mission.schedule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.NewMyFoundSouSuoPingDaoAndRiChengItemBeen;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NewMyFoundSouSuoListAdapter extends
		CommonAdapter<NewMyFoundSouSuoPingDaoAndRiChengItemBeen> {
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public NewMyFoundSouSuoListAdapter(Context context,
			List<NewMyFoundSouSuoPingDaoAndRiChengItemBeen> lDatas,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
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
	public void getViewItem(ViewHolder holder,
			NewMyFoundSouSuoPingDaoAndRiChengItemBeen item, int position) {
		TextView title = holder
				.getView(R.id.new_adapter_myfound_sousuo_list_item_title);
		TextView content = holder
				.getView(R.id.new_adapter_myfound_sousuo_list_item_count);
		TextView dianjinum = holder
				.getView(R.id.new_adapter_myfound_sousuo_list_item_dianjiliang);
		CircularImage img = (CircularImage) holder
				.getView(R.id.new_adapter_myfound_sousuo_list_item_img);
		title.setText(item.name);
		String contenttext = "";
		if ((item.CDate != null && !item.CDate.equals("") && !item.CDate.equals("null"))
				&& (item.CTime == null || item.CTime.equals("") || item.CTime.equals("null"))) {
			contenttext=item.CDate.split("-")[1]+"-"+item.CDate.split("-")[2]+" "+"全天"+" "+ item.CContent;
		} else if ((item.CDate == null || item.CDate.equals("") || item.CDate.equals("null"))
				&& (item.CTime != null && !item.CTime.equals("") && !item.CTime.equals("null"))) {
			contenttext="今天"+" "+item.CTime+" "+ item.CContent;
		} else if ((item.CDate != null && !item.CDate.equals("") && !item.CDate.equals("null"))
				&& (item.CTime != null && !item.CTime.equals("") && !item.CTime.equals("null"))) {
			contenttext=item.CDate.split("-")[1]+"-"+item.CDate.split("-")[2]+" "+item.CTime+" "+ item.CContent;
		} else {
			if (item.CContent == null || item.CContent.equals("")|| item.CContent.equals("null")) {
				contenttext = "暂无提醒内容";
			} else {
				contenttext = item.CContent;
			}
		}		
		content.setText(contenttext);

		dianjinum.setText("浏览量: " + item.clickCount);
		Log.e("item.titleImg", "++++===" + item.titleImg);
		if ("".equals(StringUtils.getIsStringEqulesNull(item.titleImg))) {
			img.setImageResource(R.mipmap.new_icon_noimgurl);
		} else {
			String imagetype = item.startStateImg;
			String state1 = imagetype.split(",")[0];
			String state2 = imagetype.split(",")[1];
			String runableimgurl = "";
			if (state1.equals("0")) {
				runableimgurl = URLConstants.图片
						+ item.titleImg.replace("\\/", "")
						+ "&imageType=2&imageSizeType=3";
			} else if (state1.equals("1")) {
				runableimgurl = URLConstants.图片
						+ item.titleImg.replace("\\/", "")
						+ "&imageType=11&imageSizeType=1";
			}
			imageLoader.displayImage(runableimgurl, img, options,
					animateFirstListener);
		}
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
}
