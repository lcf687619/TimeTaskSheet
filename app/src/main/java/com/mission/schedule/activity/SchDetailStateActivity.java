package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.R;

public class SchDetailStateActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.new_dtl_back)
	private LinearLayout new_dtl_back;
	@ViewResId(id = R.id.state_lv)
	private ListView state_lv;
	
	private String statename;
	String schid;
	private int lastIndex;
	
	Context context;
	ChooseStateAdapter adapter = null;
	List<String> list = new ArrayList<String>();
	private Map<Integer, Boolean> isSelected;  
	@Override
	protected void setListener() {
		new_dtl_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_stateactivity);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		statename = getIntent().getStringExtra("statename");
		schid = getIntent().getStringExtra("id");
		list.add("私密");
		list.add("公开");
		list.add("好友可见");
		if (isSelected != null)  
            isSelected = null;  
        isSelected = new HashMap<Integer, Boolean>();  
        for (int i = 0; i < list.size(); i++) {  
            isSelected.put(i, false);  
        }  
        
        if("私密".equals(statename)){
        	lastIndex=0;
        }else if("公开".equals(statename)){
        	lastIndex = 1;
        }else {
        	lastIndex = 2;
        }
	}

	@Override
	protected void setAdapter() {
		adapter = new ChooseStateAdapter(context, list,lastIndex);
		state_lv.setAdapter(adapter);
		state_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  
        adapter.notifyDataSetChanged();  
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
                String str = state_lv.getAdapter().getItem(position).toString();
                int state;
                if("私密".equals(str)){
                	state = 0;
    			}else if("公开".equals(str)){
    				state = 1;
    			}else {
    				state = 2;
    			}
                App.getDBcApplication().updateScheduleOpenStateData(schid, state);
		        finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_dtl_back:
			this.finish();
			break;

		default:
			break;
		}
	}
	public class ChooseStateAdapter extends BaseAdapter {
		private Context context;
		private List<String> mList;
		private int lastIndex;

		public ChooseStateAdapter(Context context, List<String> mList,int lastIndex) {
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

			state_item_title.setText(mList.get(position).toString());
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
			if(lastIndex==position){
				state_item_cb.setChecked(true);
			}
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
	
}