package com.mission.schedule.adapter.utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 通用ViewHolder
 * 
 * @author async
 * 
 */
public class ViewHolder {
	private SparseArray<View> saView;

	@SuppressWarnings("unused")
	private int mPosition;
	private View convertView;

	private ViewHolder(Context context, ViewGroup parent, int layoutID,
			int position) {
		this.saView = new SparseArray<View>();

		this.mPosition = position;
		convertView = LayoutInflater.from(context).inflate(layoutID, parent,
				false);
		convertView.setTag(this);
	}

	/**
	 * 获取ViewHolder的实例
	 * 
	 * @return
	 */
	public static ViewHolder getInstance(Context context, View convertView,
			ViewGroup parent, int layoutID, int position) {

		if (convertView == null) {
			return new ViewHolder(context, parent, layoutID, position);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		// convertView可以复用但是position是变化的,在此更新
		holder.mPosition = position;
		return holder;
	}

	/**
	 * 获取实例化的ConvertView
	 * 
	 * @return
	 */
	public View getConvertView() {
		return convertView;
	}

	/**
	 * 通过ViewID获取组件
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = saView.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			saView.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为textview设置显示文字
	 * 
	 * @param viewId
	 *            组件ID
	 * @param strId
	 *            要显示的字符串的整形值
	 */
	public void setText(int viewId, int strId) {
		setText(viewId, convertView.getContext().getResources()
				.getString(strId));
	}

	/**
	 * 为textview设置显示文字
	 * 
	 * @param viewId
	 *            组件ID
	 * @param str
	 *            要显示的字符串
	 */
	public void setText(int viewId, String str) {
		TextView tv = getView(viewId);
		tv.setText(str);
	}

	/**
	 * 设置progressBar进度条的值
	 * 
	 * @param viewId
	 *            组件ID
	 * @param progress
	 *            当前进度值
	 */
	public void setProgress(int viewId, int progress) {
		ProgressBar pb = getView(viewId);
		pb.setProgress(progress);
	}
}
