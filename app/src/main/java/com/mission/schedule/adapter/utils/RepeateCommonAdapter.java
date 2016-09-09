package com.mission.schedule.adapter.utils;

import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.onRightViewWidthListener;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 适配器模板
 * 
 * @author async
 * 
 * @param <T>
 */
public abstract class RepeateCommonAdapter<T> extends BaseAdapter implements onRightViewWidthListener{

	protected Context context;
	private List<T> lDatas;
	private int layoutItemID;
	private Handler handler;
	private SwipeXListViewNoHead listViewNoHead;
	
	public RepeateCommonAdapter(Context context, List<T> lDatas, int layoutItemID, Handler handler, SwipeXListViewNoHead listViewNoHead) {
		super();
		this.context = context;
		this.lDatas = lDatas;
		this.layoutItemID = layoutItemID;
		this.handler = handler;
		this.listViewNoHead = listViewNoHead;
	}

	@Override
	public int getCount() {
		return lDatas == null ? 0 : lDatas.size();
	}

	@Override
	public T getItem(int position) {
		return lDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getInstance(context, convertView,
				parent, layoutItemID, position);

		getViewItem(position, lDatas, holder, getItem(position), handler, listViewNoHead);

		return holder.getConvertView();
	}
	
	@Override
	public void onRightViewWidth(int position) {
		// if (position == 3) {
		// swipeXlistview.setRightViewWidth(context.getResources()
		// .getDimensionPixelSize(R.dimen.sch_item_180));
		// } else {
		listViewNoHead.setRightViewWidth(context.getResources()
				.getDimensionPixelSize(R.dimen.friends_item_80));
		// }
	}
	public abstract void getViewItem(int position, List<T> lDatas, ViewHolder holder, T item, Handler handler, SwipeXListViewNoHead listViewNoHead);
}
