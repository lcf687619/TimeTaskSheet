package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mission.schedule.R;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.annotation.ViewResId;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SetBeforeTimeActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.new_dtl_back)
	private LinearLayout new_dtl_back;
	@ViewResId(id = R.id.beforetime_lv)
	private ListView beforetime_lv;

	private String beforetime;
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
		setContentView(R.layout.activity_setbeforetime);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		beforetime = getIntent().getStringExtra("time");
		list.add("0分钟");
		list.add("5分钟");
		list.add("15分钟");
		list.add("30分钟");
		list.add("1小时");
		list.add("2小时");
		list.add("1天");
		list.add("2天");
		list.add("1周");
		list.add("");
		if (isSelected != null)
			isSelected = null;
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			isSelected.put(i, false);
		}

		if ("0".equals(beforetime)) {
			lastIndex = 0;
		} else if ("5".equals(beforetime)) {
			lastIndex = 1;
		} else if ("15".equals(beforetime)) {
			lastIndex = 2;
		} else if ("30".equals(beforetime)) {
			lastIndex = 3;
		} else if ("60".equals(beforetime)) {
			lastIndex = 4;
		} else if ("120".equals(beforetime)) {
			lastIndex = 5;
		} else if ((24 * 60 + "").equals(beforetime)) {
			lastIndex = 6;
		} else if ((48 * 60 + "").equals(beforetime)) {
			lastIndex = 7;
		} else if ((7 * 24 * 60 + "").equals(beforetime)) {
			lastIndex = 8;
		} else {
			lastIndex = 0;
		}

	}

	@Override
	protected void setAdapter() {
		adapter = new ChooseStateAdapter(context, list, R.layout.adapter_choosestate,lastIndex);
		beforetime_lv.setAdapter(adapter);
		beforetime_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter.notifyDataSetChanged();
		beforetime_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String str = beforetime_lv.getAdapter().getItem(position)
						.toString();
				if ("".equals(str)) {
					return;
				} else {
					// 当前点击的CB
					boolean cu = !isSelected.get(position);
					// 先将所有的置为FALSE
					for (Integer p : isSelected.keySet()) {
						isSelected.put(p, false);
					}
					// 再将当前选择CB的实际状态
					isSelected.put(position, cu);
					adapter.notifyDataSetChanged();
					String before;
					if ("0分钟".equals(str)) {
						before = "0";
					} else if ("5分钟".equals(str)) {
						before = "5";
					} else if ("15分钟".equals(str)) {
						before = "15";
					} else if ("30分钟".equals(str)) {
						before = "30";
					} else if ("1小时".equals(str)) {
						before = "60";
					} else if ("2小时".equals(str)) {
						before = "120";
					} else if ("1天".equals(str)) {
						before = 24 * 60 + "";
					} else if ("2天".equals(str)) {
						before = 48 * 60 + "";
					} else if ("1周".equals(str)) {
						before = 7 * 24 * 60 + "";
					} else {
						before = "0";
					}
					Intent intent = new Intent();
					intent.putExtra("beforetime", before);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
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

	public class ChooseStateAdapter extends CommonAdapter<String> {
		private List<String> mList;
		private int lastIndex;
		
		public ChooseStateAdapter(Context context, List<String> lDatas,
				int layoutItemID,int lastIndex) {
			super(context, lDatas, layoutItemID);
			this.mList = lDatas;
			this.lastIndex = lastIndex;
		}

		@Override
		public void getViewItem(ViewHolder holder, final String item, final int position) {
			TextView state_item_title = holder.getView(R.id.state_item_title);
			CheckBox state_item_cb = holder.getView(R.id.state_item_cb);

			state_item_title.setText(item);
			state_item_cb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					for (int i = 0; i < list.size(); i++) {
						isSelected.put(i, false);
					}
					lastIndex = position;
					notifyDataSetChanged();
					String before;
					if ("0分钟".equals(item)) {
						before = "0";
					} else if ("5分钟".equals(item)) {
						before = "5";
					} else if ("15分钟".equals(item)) {
						before = "15";
					} else if ("30分钟".equals(item)) {
						before = "30";
					} else if ("1小时".equals(item)) {
						before = "60";
					} else if ("2小时".equals(item)) {
						before = "120";
					} else if ("1天".equals(item)) {
						before = 24 * 60 + "";
					} else if ("2天".equals(item)) {
						before = 48 * 60 + "";
					} else if ("1周".equals(item)) {
						before = 7 * 24 * 60 + "";
					} else {
						before = "0";
					}
					Intent intent = new Intent();
					intent.putExtra("beforetime", before);
					setResult(Activity.RESULT_OK, intent);
					SetBeforeTimeActivity.this.finish();
				}
			});
			if (lastIndex == position) {
				state_item_cb.setChecked(true);
			}else{
				state_item_cb.setChecked(false);
			}
			
		}
	}

}
