package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.adapter.EditTagAdapter;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.TagCommandBean;
import com.mission.schedule.service.UpdataTagService;
import com.mobeta.android.dslv.DragSortListView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditTagActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.new_dtl_back)
	private LinearLayout new_dtl_back;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.tag_lv)
	private DragSortListView tag_lv;

	Context context;

	App app = App.getDBcApplication();
	List<TagCommandBean> mList = new ArrayList<TagCommandBean>();
	EditTagAdapter adapter = null;

	@Override
	protected void setListener() {
		new_dtl_back.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_edittag);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		tag_lv.setDropListener(onDrop);
		// tag_lv.setRemoveListener(onRemove);
		loadData();
	}

	private void loadData() {
		mList.clear();
		mList = app.QueryTagData(4);
		adapter = new EditTagAdapter(context, mList, handler,
				R.layout.adapter_edittag);
		tag_lv.setAdapter(adapter);
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_dtl_back:
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;
		case R.id.top_ll_right:
			startActivityForResult(new Intent(context, NewTagActivity.class),
					100);
			break;
		default:
			break;
		}
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				TagCommandBean item = (TagCommandBean) adapter.getItem(from);
				if (UpdataTagService.tagbeans != null && UpdataTagService.tagbeans.size() > 0) {
					for (int i = 0; i < UpdataTagService.tagbeans.size(); i++) {
						if (UpdataTagService.tagbeans.get(i).state == 0) {
							for (int j = 0; j < mList.size(); j++) {
								if (UpdataTagService.tagbeans.get(i).dataState == 0) {
									if (UpdataTagService.tagbeans.get(i).originalId == Integer
											.parseInt(mList.get(j).getCtgId())) {
										mList.get(j).setCtgId(
												UpdataTagService.tagbeans.get(i).id + "");
										break;
									}
								}
							}
						}

					}
				}
				adapter.remove(item);
				adapter.insert(item, to);
				adapter.notifyDataSetChanged();
				List<TagCommandBean> list = new ArrayList<TagCommandBean>();
				list = mList;
				for (int i = 0; i < list.size(); i++) {
					app.updateTagOrderData(
							Integer.parseInt(list.get(i).getCtgId()), i);
				}
				Intent intent = new Intent(getApplication(), UpdataTagService.class);
				intent.setAction("updateData");
				intent.setPackage(getPackageName());
				startService(intent);
			}
		}
	};
	// private RemoveListener onRemove = new DragSortListView.RemoveListener() {
	// @Override
	// public void remove(int which) {
	//
	// }
	// };
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			TagCommandBean mMap = (TagCommandBean) msg.obj;
			dialogDetailOnClick(mMap, msg.arg1);
		};
	};

	private void dialogDetailOnClick(final TagCommandBean mMap,
			final int position) {
		final Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_deteletag, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
		dialog.show();

		TextView delete_tv = (TextView) more_pop_menu
				.findViewById(R.id.delete_tv);
		delete_tv.setText("是否删除 " + "\"" + mMap.getCtgText() + "\"");
		more_pop_menu.findViewById(R.id.delete_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (UpdataTagService.tagbeans != null && UpdataTagService.tagbeans.size() > 0) {
							for (int i = 0; i < UpdataTagService.tagbeans.size(); i++) {
								if (UpdataTagService.tagbeans.get(i).state == 0) {
									for (int j = 0; j < mList.size(); j++) {
										if (UpdataTagService.tagbeans.get(i).dataState == 0) {
											if (UpdataTagService.tagbeans.get(i).originalId == Integer
													.parseInt(mList.get(j)
															.getCtgId())) {
												mList.get(j).setCtgId(
														UpdataTagService.tagbeans.get(i).id + "");
												break;
											}
										}
									}
								}

							}
						}
						app.deleteTagData(Integer.parseInt(mMap.getCtgId()));
						mList.remove(position);
						adapter.notifyDataSetChanged();
						tag_lv.invalidate();
						for (int i = 0; i < mList.size(); i++) {
							app.updateTagOrderData(
									Integer.parseInt(mList.get(i).getCtgId()),
									i);
						}
						Intent intent = new Intent(getApplication(), UpdataTagService.class);
						intent.setAction("updateData");
						intent.setPackage(getPackageName());
						startService(intent);
						dialog.dismiss();
					}
				});
		more_pop_menu.findViewById(R.id.delete_canel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		loadData();
		adapter.notifyDataSetChanged();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(UpdataTagService.tagbeans!=null&&UpdataTagService.tagbeans.size()>0){
			UpdataTagService.tagbeans.clear();
		}
	}
}
