package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.adapter.WeekSelectAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.utils.LineGridView;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class WeekSelectActivity extends BaseActivity implements OnClickListener,OnItemClickListener{

	@ViewResId(id = R.id.week_back_linear)
	private LinearLayout week_back_linear;
	@ViewResId(id = R.id.week_gv)
	private LineGridView week_gv;
	
	Context context;
	WeekSelectAdapter adapter = null;
	List<String> list = new ArrayList<String>();
	String weeks;
	int index;
	
	@Override
	protected void setListener() {
		week_back_linear.setOnClickListener(this);
		week_gv.setOnItemClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_weekselect);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		weeks = getIntent().getStringExtra("weeks");
		if("星期一".equals(weeks)){
			index=0;
		}else if("星期二".equals(weeks)){
			index = 1;
		}else if("星期三".equals(weeks)){
			index = 2;
		}else if("星期四".equals(weeks)){
			index = 3;
		}else if("星期五".equals(weeks)){
			index = 4;
		}else if("星期六".equals(weeks)){
			index = 5;
		}else {
			index = 6;
		}
		list.add("星期一");
		list.add("星期二");
		list.add("星期三");
		list.add("星期四");
		list.add("星期五");
		list.add("星期六");
		list.add("星期日");
		list.add("");
		list.add("");
	}

	@Override
	protected void setAdapter() {
		week_gv.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					boolean isFirst = true;// 默认调用两次,这里只让他执行一次

					@Override
					public void onGlobalLayout() {
						if (isFirst) {
							isFirst = false;
							// 布局全部完成，可以获得任何view组件的宽度、高度、左边、右边等
							adapter = new WeekSelectAdapter(context, list,R.layout.adapter_weekselect,mScreenWidth,index);
							week_gv.setAdapter(adapter);
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.week_back_linear:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		LinearLayout week_ll = (LinearLayout) week_gv.getChildAt(
//				position).findViewById(R.id.week_ll);
//		week_ll.setBackgroundColor(Color.parseColor("#fef8f0"));
		String week = (String) week_gv.getAdapter().getItem(position);
		Intent intent = new Intent();
		intent.putExtra("weeks", week);
		setResult(Activity.RESULT_OK, intent);
		this.finish();
	}

}
