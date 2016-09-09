package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.adapter.QingYingYongAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class QingYingYongActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.new_dtl_back)
	private LinearLayout new_dtl_back;
	@ViewResId(id = R.id.yingyong_lv)
	private ListView yingyong_lv;
	
	QingYingYongAdapter adapter = null;
	
	Context context;
	List<String> list = new ArrayList<String>();
	
	@Override
	protected void setListener() {
		new_dtl_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_qingyingyong);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		list.add("#备忘录#");
		list.add("#量体重#");
		list.add("#每日天气#");
	}

	@Override
	protected void setAdapter() {
		adapter = new QingYingYongAdapter(context, list,R.layout.adapter_qingyingyong);
		yingyong_lv.setAdapter(adapter);
		yingyong_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String str = yingyong_lv.getAdapter().getItem(position).toString();
				Intent intent = new Intent();
				intent.putExtra("name", str);
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

		default:
			break;
		}
	}

}
