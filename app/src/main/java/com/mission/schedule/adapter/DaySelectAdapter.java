package com.mission.schedule.adapter;

import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class DaySelectAdapter extends CommonAdapter<String>{

	int width;
	
	public DaySelectAdapter(Context context, List<String> lDatas,
			int layoutItemID,int width) {
		super(context, lDatas, layoutItemID);
		this.width = width;
	}
	
	@Override
	public void getViewItem(
			com.mission.schedule.adapter.utils.ViewHolder holder, String item,
			int position) {
		TextView content_tv = holder.getView(R.id.content_tv);
//		LinearLayout week_ll = holder.getView(R.id.week_ll);
		
		LayoutParams params = content_tv.getLayoutParams();
		params.width = width/7;
		content_tv.setLayoutParams(params);
		content_tv.setText(item);
	}
}
