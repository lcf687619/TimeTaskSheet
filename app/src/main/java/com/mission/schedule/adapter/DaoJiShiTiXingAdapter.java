package com.mission.schedule.adapter;

import java.util.List;

import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.bean.ChangYongBean;
import com.mission.schedule.R;

import android.content.Context;
import android.widget.TextView;

public class DaoJiShiTiXingAdapter extends CommonAdapter<ChangYongBean>{

	public DaoJiShiTiXingAdapter(Context context, List<ChangYongBean> lDatas,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
	}

	@Override
	public void getViewItem(
			com.mission.schedule.adapter.utils.ViewHolder holder,
			ChangYongBean item, int position) {
		TextView title = holder.getView(R.id.title);
		TextView content_tv = holder.getView(R.id.content_tv);	
		title.setText(item.title);
		content_tv.setText(item.content);
	}

}
