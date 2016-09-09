package com.mission.schedule.adapter;

import java.util.List;

import android.content.Context;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.TagCommandBean;

public class SerachTagAdapter extends CommonAdapter<TagCommandBean>{

	public SerachTagAdapter(Context context, List<TagCommandBean> lDatas,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
	}

	@Override
	public void getViewItem(ViewHolder holder, TagCommandBean item,
			int position) {
		holder.setText(R.id.text_tv, item.getCtgText());
	}

}
