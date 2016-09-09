package com.mission.schedule.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.TagCommandBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.new_dtl_back)
	private LinearLayout new_dtl_back;
	@ViewResId(id = R.id.state_lv)
	private ListView state_lv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;

	private String statename;
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
		adapter = new ChooseStateAdapter(context, list,R.layout.adapter_choosestate,lastIndex);
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
				Intent intent = new Intent();
				intent.putExtra("state", str);
				intent.putExtra("coclor", coclor);
				setResult(Activity.RESULT_OK, intent);
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
			case R.id.top_ll_right:
				startActivityForResult(new Intent(context, EditTagActivity.class),100);
				break;
			default:
				break;
		}
	}
	public class ChooseStateAdapter extends CommonAdapter<TagCommandBean> {
		
		private Context context;
		private List<TagCommandBean> mList;
		private int lastIndex;
		
		public ChooseStateAdapter(Context context, List<TagCommandBean> lDatas,
				int layoutItemID,int lastIndex) {
			super(context, lDatas, layoutItemID);
			this.mList = lDatas;
			this.lastIndex = lastIndex;
		}
		@Override
		public void getViewItem(ViewHolder holder, final TagCommandBean item,
				int position) {
			TextView state_item_title = holder.getView(R.id.state_item_title);
			CheckBox state_item_cb = holder.getView(R.id.state_item_cb);

			state_item_title.setText(item.getCtgText());
			state_item_cb.setOnClickListener(new OnClickListener() {  
	             public void onClick(View v) {  
	            	 for(Integer p : isSelected.keySet()) {
	 					isSelected.put(p, false);
	 				}
	            	item.setCheck(true);
	            	notifyDataSetChanged();  
	            	String str = item.getCtgText();
					String coclor = item.getCtgId();
					Intent intent = new Intent();
					intent.putExtra("state", str);
					intent.putExtra("coclor", coclor);
					setResult(Activity.RESULT_OK, intent);
					StateActivity.this.finish();
	             }  
	         }); 
//			if(lastIndex==position){
			state_item_cb.setChecked(item.isCheck());
//			}
//			state_item_cb.setChecked(isSelected.get(position));  			
		}
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		loadData();
		adapter.notifyDataSetChanged();
	}
}
