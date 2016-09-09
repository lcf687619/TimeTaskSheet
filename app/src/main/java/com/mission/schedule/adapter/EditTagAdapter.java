package com.mission.schedule.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.bean.TagCommandBean;

public class EditTagAdapter extends CommonAdapter<TagCommandBean>{

	private List<TagCommandBean> mlList;
	private Handler handler;
	
	public EditTagAdapter(Context context, List<TagCommandBean> lDatas,Handler handler,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
		this.mlList = lDatas;
		this.handler = handler;
	}

	@Override
	public void getViewItem(ViewHolder holder, final TagCommandBean item, final int position) {
		RelativeLayout delete_rl = (RelativeLayout) holder.getView(R.id.delete_rl);
		TextView content_tv = (TextView) holder.getView(R.id.content_tv);
		RelativeLayout tuodong_rl = (RelativeLayout) holder.getView(R.id.tuodong_rl);
		if ("0".equals(item.getCtgType())) {
			delete_rl.setVisibility(View.GONE);
		} else {
			delete_rl.setVisibility(View.VISIBLE);
		}
		String content = item.getCtgText();
		content_tv.setText(content);
		delete_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 Message message = Message.obtain();
				 message.arg1 = position;
				 message.obj = item;
				 handler.sendMessage(message);
			}
		});
	}

	public void remove(TagCommandBean item) {
		if (item != null && mlList != null) {
			mlList.remove(item);
		}
	}

	public void insert(TagCommandBean item, int to) {
		if (item != null) {
			mlList.add(to, item);
		}
	}
}
