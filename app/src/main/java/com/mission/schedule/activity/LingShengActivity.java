package com.mission.schedule.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.utils.XmlUtil;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LingShengActivity extends BaseActivity implements OnClickListener {

	@ViewResId(id = R.id.new_dtl_back)
	private LinearLayout new_dtl_back;
	@ViewResId(id = R.id.lingsheng_lv)
	private ListView lingsheng_lv;

	Context context;
	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	LingShengAdapter adapter = null;
	private String alarmSound;
	private String alarmSoundDesc;

	@Override
	protected void setListener() {
		new_dtl_back.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_lingsheng);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		list = XmlUtil.readBellXML(this);
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "");
		list.add(map);
		// list.add("完成任务");
		// list.add("到点提醒");
		// list.add("起床");
		// list.add("睡觉");
		// list.add("开会");
		// list.add("喝水");
		// list.add("吃药");
		// list.add("重要日");
		// list.add("背单词");
		// list.add("上课学习");
		// list.add("充值缴费");
		// list.add("银行业务");
		// list.add("看节目");
		// list.add("问候父母");
		// list.add("联系");
		// list.add("会面");
		// list.add("重要活动");
		// list.add("运动锻炼");
		// list.add("个人保养");
		// list.add("检查身体");
		// list.add("年检");
		// list.add("短提示音");
		// list.add("长提示音");
		adapter = new LingShengAdapter(context, handler, list);
	}

	@Override
	protected void setAdapter() {
		lingsheng_lv.setAdapter(adapter);
		lingsheng_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> mMap = (Map<String, String>) lingsheng_lv
						.getAdapter().getItem(position);
				if (position == list.size()-1) {
					return;
				} else {
					Intent intent = new Intent();
					intent.putExtra("lingshengname", mMap.get("name"));
					intent.putExtra("code", mMap.get("value"));
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Map<String, String> map = (Map<String, String>) msg.obj;
			switch (msg.what) {
			case 0:
				// popupMusic.dismiss();
				// tv_bell.setText(mMap.get("name"));
				// alarmSound = mMap.get("value") + ".ogg";
				// alarmSoundDesc = mMap.get("name");

				break;

			case 1:
				// tv_bell.setText(mMap.get("name"));
				alarmSound = map.get("value") + ".mp3".replace("\\n", "");
				alarmSoundDesc = map.get("name");
				playMusic();

				break;
			}
		}

	};

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

	public class LingShengAdapter extends BaseAdapter {
		private Context context;
		private List<Map<String, String>> mList;

		public LingShengAdapter(Context context, Handler handler,
				List<Map<String, String>> mList) {
			this.context = context;
			this.mList = mList;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View view = convertView;
			ViewWapper viewWapper = null;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.adapter_lingsheng, null);
				viewWapper = new ViewWapper(view);
				view.setTag(viewWapper);
			} else {
				viewWapper = (ViewWapper) view.getTag();
			}
			TextView lingsheng_name_tv = viewWapper.getLingShengName();
			TextView shiting_tv = viewWapper.getShiTing();
			lingsheng_name_tv.setText(mList.get(position).get("name")
					.toString());
			int position1 = position;
			if(position==list.size()-1){
				shiting_tv.setText("");
			}else{
				shiting_tv.setText("试听");
			}
			shiting_tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Message message = new Message();
					message.what = 1;
					message.obj = mList.get(position);
					handler.sendMessage(message);
				}
			});
			View state_item_line = viewWapper.getStateViewLine();

			return view;
		}

		class ViewWapper {
			private View view;
			private TextView lingsheng_name_tv;
			private TextView shiting_tv;
			private View state_item_line;

			private ViewWapper(View view) {
				this.view = view;
			}

			private TextView getLingShengName() {
				if (lingsheng_name_tv == null) {
					lingsheng_name_tv = (TextView) view
							.findViewById(R.id.lingsheng_name_tv);
				}
				return lingsheng_name_tv;
			}

			private TextView getShiTing() {
				if (shiting_tv == null) {
					shiting_tv = (TextView) view.findViewById(R.id.shiting_tv);
				}
				return shiting_tv;
			}

			private View getStateViewLine() {
				if (state_item_line == null) {
					state_item_line = view.findViewById(R.id.state_item_line);
				}
				return state_item_line;
			}
		}
	}

	private void playMusic() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.dialog_dell, null);
		// 声明一个弹出框
		final PopupWindow popupMusic = new PopupWindow(contentView,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		popupMusic.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		Drawable drawable = popupMusic.getBackground();
		drawable.setAlpha(200);
		// popupMusic.setAnimationStyle(R.style.AnimationScale);
		popupMusic.setOutsideTouchable(true);
		popupMusic.setFocusable(true);
		popupMusic.showAtLocation(findViewById(R.id.newbuild_main),
				Gravity.CENTER, 0, 0);
		popupMusic.update();

		final MediaPlayer mediaPlayer = new MediaPlayer();

		try {
			AssetFileDescriptor fileDescriptor = getAssets().openFd(alarmSound);
			mediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mediaPlayer.reset();
						mediaPlayer.release();
						if(popupMusic!=null){
							popupMusic.dismiss();
						}
					}
				});
		((TextView) contentView.findViewById(R.id.bell_tv))
				.setText(alarmSoundDesc);
		Button button = (Button) contentView.findViewById(R.id.bell_bt);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mediaPlayer.reset();
				mediaPlayer.release();
				popupMusic.dismiss();
			}
		});
	}

}
