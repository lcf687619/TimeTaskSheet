package com.mission.schedule.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mission.schedule.bean.FriendsChongFuBean;
import com.mission.schedule.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RepeatFriendsRiChengAdapter extends BaseAdapter{

	Context context;
	int mScreenWidth;
	List<FriendsChongFuBean> mList;
	private LayoutInflater mInflater;
	public static Map<Integer,Boolean> mChecked;
	
	public RepeatFriendsRiChengAdapter(Context context, List<FriendsChongFuBean> mList,int mScreenWidth) {
		this.context = context;
		this.mList = mList;
		mInflater = LayoutInflater.from(context);
		this.mScreenWidth = mScreenWidth;
		mChecked = new HashMap<Integer,Boolean>();
		for (int i = 0; i < mList.size(); i++) {
			mChecked.put(i,false);
		}
	}
	
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FriendsChongFuBean bean = mList.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_repeatfriendsricheng,
					null);

			viewHolder = new ViewHolder();
			viewHolder.date_ll = (RelativeLayout) convertView
					.findViewById(R.id.date_ll);
			viewHolder.date_tv = (TextView) convertView
					.findViewById(R.id.date_tv);
			viewHolder.time_tv = (TextView) convertView
					.findViewById(R.id.time_tv);
			viewHolder.content_tv = (TextView) convertView
					.findViewById(R.id.content_tv);
			viewHolder.content_rl = (RelativeLayout) convertView.findViewById(R.id.content_rl);
			viewHolder.select_cb = (CheckBox) convertView.findViewById(R.id.select_cb);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String strdate = bean.repTypeParameter.replace("[", "").replace("]", "").replace("\"", "").toString();
		String weekStr;
		if (bean.repType == 1) {
			viewHolder.date_tv.setText("每天");
			viewHolder.time_tv.setText(bean.repTime);
		} else if (bean.repType == 2) {
			viewHolder.date_tv.setText("每周");
			if(strdate.equals("1")){
				weekStr = "一";
			}else if(strdate.equals("2")){
				weekStr = "二";
			}else if(strdate.equals("3")){
				weekStr = "三";
			}else if(strdate.equals("4")){
				weekStr = "四";
			}else if(strdate.equals("5")){
				weekStr = "五";
			}else if(strdate.equals("6")){
				weekStr = "六";
			}else {
				weekStr = "日";
			}
			viewHolder.time_tv.setText("周"+weekStr + " "+ bean.repTime);
		} else if (bean.repType == 3) {
			viewHolder.date_tv.setText("每月");
			viewHolder.time_tv.setText(strdate +"日"+ " "+ bean.repTime);
		} else if (bean.repType == 4) {
			viewHolder.date_tv.setText("每年");
			viewHolder.time_tv.setText(strdate + " "+ bean.repTime);
		} else {
			viewHolder.date_tv.setText("工作日");
			viewHolder.time_tv.setText(bean.repTime);
		}
		if(position==0){
			viewHolder.date_ll.setVisibility(View.VISIBLE);
		}else if(mList.get(position).repType==mList.get(position-1).repType){
			viewHolder.date_ll.setVisibility(View.GONE);
		}else if(mList.get(position).repType==mList.get(position-1).repType){
			
		}
		else {
			viewHolder.date_ll.setVisibility(View.VISIBLE);
		}
		viewHolder.content_tv.setText(bean.repContent);
		final int index = position;
		viewHolder.select_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					getIsSelected().put(index, true);
				}else {
					getIsSelected().put(index, false);
				}
			}
		});
		viewHolder.select_cb.setChecked(getIsSelected().get(position));
		return convertView;
	}
	static class ViewHolder {
		public RelativeLayout date_ll;
		public TextView date_tv;
		public TextView time_tv;
		public TextView content_tv;
		public RelativeLayout content_rl;
		public CheckBox select_cb;
	}
	public static Map<Integer, Boolean> getIsSelected() {
        return mChecked;
    }
    public static void setIsSelected(Map<Integer, Boolean> isSelected) {
    	RepeatFriendsRiChengAdapter.mChecked = isSelected;
    }
}
