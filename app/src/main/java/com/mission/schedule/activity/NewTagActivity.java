package com.mission.schedule.activity;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.entity.CLCategoryTable;
import com.mission.schedule.service.UpdataTagService;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewTagActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.tag_et)
	private EditText tag_et;
	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.title_tv)
	private TextView title_tv;
	@ViewResId(id = R.id.top_ll_save)
	private LinearLayout top_ll_save;

	Context context;
	App app = App.getDBcApplication();

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_save.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newtag);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		title_tv.setText("编辑分类");
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.top_ll_back:
			intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;
		case R.id.top_ll_save:
			int orderID = app.getLocalId(1, CLCategoryTable.CLCategoryTable, CLCategoryTable.ctgOrder);
			app.insertTagData(tag_et.getText().toString(), orderID, "", 1, 1, "", "");
			if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
				intent = new Intent(context, UpdataTagService.class);
				intent.setAction("updateData");
				intent.setPackage(getPackageName());
				startService(intent);
			}
			this.finish();
			break;
		default:
			break;
		}
	}
}
