package com.mission.schedule.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.TagCommandBean;
import com.mission.schedule.entity.ScheduleTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyStateActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.new_dtl_back)
	private LinearLayout new_dtl_back;
	@ViewResId(id = R.id.state_lv)
	private ListView state_lv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;

	private String statename;
	private String id;
	private int lastIndex;

	Context context;
	ChooseStateAdapter adapter = null;
	List<TagCommandBean> list = new ArrayList<TagCommandBean>();
	private Map<Integer, Boolean> isSelected;

	App app = App.getDBcApplication();

	@Override
	protected void setListener() {
		new_dtl_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_stateactivity);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		id = getIntent().getStringExtra("id");
		statename = getIntent().getStringExtra("statename");
		loadData();
	}

	private void loadData() {
		list.clear();
		list = app.QueryTagData(0);
		TagCommandBean map = new TagCommandBean();
		map.setCtgText("未分类");
		map.setCtgId("0");
		list.add(0, map);
		if(list.size()>=2){
			map = list.get(1);
			list.remove(1);
			list.add(list.size(),map);
			map = list.get(1);
			list.remove(1);
			list.add(list.size(),map);
		}
		if (isSelected != null)
			isSelected = null;
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			isSelected.put(i, false);
			if(list.get(i).getCtgText().equals(statename)){
				list.get(i).setCheck(true);
				lastIndex = i;
			}
		}
		adapter = new ChooseStateAdapter(context, list,lastIndex);
		state_lv.setAdapter(adapter);
		state_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void setAdapter() {
		state_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// 当前点击的CB  
				boolean cu = !isSelected.get(position);
				// 先将所有的置为FALSE
				for(Integer p : isSelected.keySet()) {
					isSelected.put(p, false);
				}
				// 再将当前选择CB的实际状态
				isSelected.put(position, cu);
				adapter.notifyDataSetChanged();
				String str = ((TagCommandBean)state_lv.getAdapter().getItem(position)).getCtgText();
				String coclor = ((TagCommandBean)state_lv.getAdapter().getItem(position)).getCtgId();
				updateColorStateSch(ScheduleTable.schColorType, Integer.parseInt(coclor));
				finish();
			}
		});
	}
	private void updateColorStateSch(String key,int colortype) {
		try {
			Map<String, String> upMap = new HashMap<String, String>();
			upMap.put(key, colortype+"");
			App.getDBcApplication().updateSchFocusState(upMap,
					"where schID=" + id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.new_dtl_back:
				this.finish();
				break;
			case R.id.top_ll_right:
				startActivityForResult(new Intent(context, EditTagActivity.class),100);
				break;
			default:
				break;
		}
	}
	public class ChooseStateAdapter extends BaseAdapter {
		private Context context;
		private List<TagCommandBean> mList;
		private int lastIndex;

		public ChooseStateAdapter(Context context, List<TagCommandBean> mList,int lastIndex) {
			this.context = context;
			this.mList = mList;
			this.lastIndex = lastIndex;
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
		public View getView(final int position, View convertView, ViewGroup parent) {

			View view = convertView;
			ViewWapper viewWapper = null;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.adapter_choosestate, null);
				viewWapper = new ViewWapper(view);
				view.setTag(viewWapper);
			} else {
				viewWapper = (ViewWapper) view.getTag();
			}

			TextView state_item_title = viewWapper.getForDayTvTime();
			CheckBox state_item_cb = viewWapper.getForDayCb();
//			View state_item_line = viewWapper.getStateViewLine();

			state_item_title.setText(mList.get(position).getCtgText());
//			state_item_cb.setOnClickListener(new OnClickListener() {  
//	             public void onClick(View v) {  
//	            	 // 当前点击的CB  
//	                    boolean cu = !isSelected.get(position);  
//	                    // 先将所有的置为FALSE  
//	                    for(Integer p : isSelected.keySet()) {  
//	                        isSelected.put(p, false);  
//	                    }  
//	                    // 再将当前选择CB的实际状态  
//	                    isSelected.put(position, cu);  
//	                    ChooseStateAdapter.this.notifyDataSetChanged();  
//	                    beSelectedData.clear();  
//	                    if(cu) 
//	                    	beSelectedData.add(mList.get(position)); 
//	             }  
//	         }); 
//			if(lastIndex==position){
			state_item_cb.setChecked(mList.get(position).isCheck());
//			}
//			state_item_cb.setChecked(isSelected.get(position));  

			return view;
		}

		class ViewWapper {
			private View view;
			private TextView state_item_title;
			private CheckBox state_item_cb;
//			private View state_item_line;

			private ViewWapper(View view) {
				this.view = view;
			}

			private TextView getForDayTvTime() {
				if (state_item_title == null) {
					state_item_title = (TextView) view
							.findViewById(R.id.state_item_title);
				}
				return state_item_title;
			}

			private CheckBox getForDayCb() {
				if (state_item_cb == null) {
					state_item_cb = (CheckBox) view
							.findViewById(R.id.state_item_cb);
				}
				return state_item_cb;
			}

//			private View getStateViewLine() {
//				if (state_item_line == null) {
//					state_item_line = view.findViewById(R.id.state_item_line);
//				}
//				return state_item_line;
//			}
		}
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		loadData();
		adapter.notifyDataSetChanged();
	}
}
