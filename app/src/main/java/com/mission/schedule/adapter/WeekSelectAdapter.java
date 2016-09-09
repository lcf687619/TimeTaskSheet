package com.mission.schedule.adapter;

import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class WeekSelectAdapter extends CommonAdapter<String>{

	int width;
	int index;
	
	public WeekSelectAdapter(Context context, List<String> lDatas,
			int layoutItemID,int width,int index) {
		super(context, lDatas, layoutItemID);
		this.width = width;
		this.index = index;
	}

	@Override
	public void getViewItem(
			com.mission.schedule.adapter.utils.ViewHolder holder, String item,
			int position) {
		TextView content_tv = holder.getView(R.id.content_tv);
//		LinearLayout week_ll = holder.getView(R.id.week_ll);	
		LayoutParams params = content_tv.getLayoutParams();
		params.width = width/3;
		content_tv.setLayoutParams(params);
		content_tv.setText(item);
	}
}
