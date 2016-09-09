package com.mission.schedule.adapter;

import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class QingYingYongAdapter extends CommonAdapter<String>{


	public QingYingYongAdapter(Context context, List<String> lDatas,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
	}
	@Override
	public void getViewItem(ViewHolder holder, String item, int position) {
		TextView tixingfangshi_item_title = holder.getView(R.id.tixingfangshi_item_title);
		ImageView tiaozhuan_item_img = holder.getView(R.id.tiaozhuan_item_img);
//		View state_item_line = holder.getView(R.id.state_item_line);
		tiaozhuan_item_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});

		tixingfangshi_item_title.setText(item);		
	}
}
	
