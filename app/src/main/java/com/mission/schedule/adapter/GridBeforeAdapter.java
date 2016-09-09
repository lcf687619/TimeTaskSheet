package com.mission.schedule.adapter;

import java.util.HashMap;
import java.util.Map;

import com.mission.schedule.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GridBeforeAdapter extends BaseAdapter {

	private Context context;
	private String[] beforeTime;
	private int height;
	private int lastIndex;
	Map<Integer, Boolean> map;
	String str = "准时提醒";

	public GridBeforeAdapter(Context context, String[] beforeTime, int height,
			int lastIndex) {
		this.context = context;
		this.beforeTime = beforeTime;
		this.height = height;
		this.lastIndex = lastIndex;
		map = new HashMap<Integer, Boolean>();
		initMap();
	}
	private void initMap(){
		for(int i=1;i<beforeTime.length;i++){
			map.put(i, false);
		}
	}
	@Override
	public int getCount() {
		return beforeTime.length;
	}

	@Override
	public Object getItem(int position) {
		return beforeTime[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String[] getBeforeTime() {
		return beforeTime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewWapper viewWapper = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_grid_before_item, null);
			viewWapper = new ViewWapper(view);
			view.setTag(viewWapper);
		} else {
			viewWapper = (ViewWapper) view.getTag();
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, height);
		LinearLayout before_ll = viewWapper.getBeforLL();
		before_ll.setLayoutParams(params);

		TextView before_tv = viewWapper.getBeforTime();
		TextView before_tv_state = viewWapper.getBeforState();
		String beforTime = beforeTime[position];
		if (position == 0) {
			String[] berforeState = beforTime.split("-");
			if (!str.equals(berforeState[0])) {
				before_tv.setText(berforeState[0]);
			} else {
				before_tv.setText(berforeState[0]);
				before_tv_state.setText("");
			}
			
		} else {
			String[] berforeState = beforTime.split("-");
			if (!str.equals(berforeState[0])) {
				before_tv.setText(berforeState[0]);
				before_tv_state.setText(berforeState[1]);
			} else {
				before_tv.setText(berforeState[0]);
				before_tv_state.setText("");
			}
		}
//		if(map.get(position)){
//			before_ll.setBackgroundColor(Color.parseColor("#F4E8C2"));
//			before_tv.setTextColor(Color.parseColor("#F24040"));
//		}else {
//			before_ll.setBackgroundColor(Color.parseColor("#fef8f0"));
//			before_tv.setTextColor(Color.parseColor("#938761"));
//		}
		if (position == lastIndex) {
			before_ll.setBackgroundColor(Color.parseColor("#F4E8C2"));
			before_tv.setTextColor(Color.parseColor("#F24040"));
		} else {
			before_ll.setBackgroundColor(Color.parseColor("#fef8f0"));
			before_tv.setTextColor(Color.parseColor("#938761"));
		}

		return view;
	}

	class ViewWapper {

		private View view;
		private LinearLayout before_ll;
		private TextView before_tv;
		private TextView before_state;

		private ViewWapper(View view) {
			this.view = view;
		}

		private LinearLayout getBeforLL() {
			if (before_ll == null) {
				before_ll = (LinearLayout) view.findViewById(R.id.before_ll);
			}
			return before_ll;
		}

		private TextView getBeforTime() {
			if (before_tv == null) {
				before_tv = (TextView) view.findViewById(R.id.before_tv);
			}
			return before_tv;
		}

		private TextView getBeforState() {
			if (before_state == null) {
				before_state = (TextView) view.findViewById(R.id.before_state);
			}
			return before_state;
		}
	}
}

