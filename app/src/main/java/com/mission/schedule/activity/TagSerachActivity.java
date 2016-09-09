package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.adapter.SerachTagAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.TagCommandBean;
import com.mission.schedule.utils.ListViewForScrollView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TagSerachActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.listview)
	private ListViewForScrollView listview;
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.headtitle_tv)
	private TextView headtitle_tv;
	@ViewResId(id = R.id.sousuocontent_et)
	private EditText sousuocontent_et;
	@ViewResId(id = R.id.sousuo_iv)
	private ImageView sousuo_iv;
	
	Context context;
	SerachTagAdapter adapter = null;
	List<TagCommandBean> mList = new ArrayList<TagCommandBean>();
	App app = App.getDBcApplication();
	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		sousuo_iv.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_tagserach);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		headtitle_tv.setText("分类查询");
		loadData();
	}

	private void loadData() {
		mList.clear();
		mList = app.QueryTagData(0);
		adapter = new SerachTagAdapter(context, mList, R.layout.adapter_serachtag);
		listview.setAdapter(adapter);
	}

	@Override
	protected void setAdapter() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TagCommandBean item = (TagCommandBean) listview.getAdapter().getItem(position);
				Intent intent = new Intent(context, TagSchSerachActivity.class);
				intent.putExtra("tagname", item.getCtgText());
				intent.putExtra("tagId", item.getCtgId());
				startActivityForResult(intent, 100);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sousuo_iv:
			Intent intent = new Intent(context, MySchSerachActivity.class);
			intent.putExtra("content", sousuocontent_et.getText().toString());
			startActivityForResult(intent, 100);
			break;
		case R.id.top_ll_back:
			this.finish();
			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		
	}
	
}
