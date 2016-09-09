package com.mission.schedule.widget;

import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;

import android.content.Context;
import android.widget.Button;

public class TagBaseAdapter extends CommonAdapter<String> {

    public TagBaseAdapter(Context context, List<String> lDatas, int layoutItemID) {
		super(context, lDatas, layoutItemID);
	}

	@Override
	public void getViewItem(
			com.mission.schedule.adapter.utils.ViewHolder holder, String item,
			int position) {
		Button tagBtn = holder.getView(R.id.tag_btn);
        tagBtn.setText(item);
	}
}
