package com.mission.schedule.adapter;

import java.util.List;

import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.ChangYongBean;
import com.mission.schedule.R;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ChangYongYuAdapter extends CommonAdapter<ChangYongBean>{

	Context context;
	private int width;
	
	public ChangYongYuAdapter(Context context, List<ChangYongBean> lDatas,
			int layoutItemID,int width) {
		super(context, lDatas, layoutItemID);
		this.context = context;
		this.width = width;
	}

	@Override
	public void getViewItem(ViewHolder holder,ChangYongBean item, int position) {
		TextView content_tv = holder.getView(R.id.content_tv);
		TextView title_tv = holder.getView(R.id.title_tv);
		LinearLayout week_ll = holder.getView(R.id.week_ll);
		
		LinearLayout.LayoutParams layoutParams = (LayoutParams) week_ll.getLayoutParams();
		layoutParams.height = width/3;
		week_ll.setLayoutParams(layoutParams);
		title_tv.setText(item.title);
		content_tv.setText(item.content);
	}

}
