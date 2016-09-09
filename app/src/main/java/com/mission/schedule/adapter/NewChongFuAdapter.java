package com.mission.schedule.adapter;

import java.util.List;
import java.util.Map;
import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;
import com.mission.schedule.entity.CLNFMessage;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead;
import com.mission.schedule.swipexlistview.SwipeXListViewNoHead.onRightViewWidthListener;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.Utils;
import com.mission.schedule.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewChongFuAdapter extends CommonAdapter<Map<String,String>> implements
		onRightViewWidthListener {

	private Context context;
	private List<Map<String, String>> mList;
	private Handler handler;
	private SwipeXListViewNoHead swipeXlistview;

	public NewChongFuAdapter(Context context, List<Map<String, String>> lDatas,
			int layoutItemID,Handler handler, SwipeXListViewNoHead swipeXlistview) {
		super(context, lDatas, layoutItemID);
		this.context = context;
		this.mList = lDatas;
		this.handler = handler;
		this.swipeXlistview = swipeXlistview;
		swipeXlistview.setonRightViewWidthListener(this);
	}
	private void setOnclick(int positions, Map<String, String> map, int what) {
		Message message = Message.obtain();
		message.arg1 = positions;
		message.obj = map;
		message.what = what;
		handler.sendMessage(message);
	}

	@Override
	public void onRightViewWidth(int position) {
		// if (position == 3) {
		// swipeXlistview.setRightViewWidth(context.getResources()
		// .getDimensionPixelSize(R.dimen.sch_item_180));
		// } else {
		swipeXlistview.setRightViewWidth(context.getResources()
				.getDimensionPixelSize(R.dimen.friends_item_80));
		// }
	}

	@SuppressLint("NewApi") @Override
	public void getViewItem(ViewHolder holder, final Map<String, String> map,
			final int position) {
		RelativeLayout date_ll = holder.getView(R.id.date_ll);
		TextView date_tv = holder.getView(R.id.date_tv);
		TextView time_tv = holder.getView(R.id.time_tv);
		TextView content_tv = holder.getView(R.id.content_tv);
		TextView delete_tv = holder.getView(R.id.delete_tv);
		LinearLayout delete_ll = holder.getView(R.id.delete_ll);
		LinearLayout content_ll = holder.getView(R.id.content_ll);
		TextView personstate_tv = holder.getView(R.id.personstate_tv);
		TextView riqi_tv = holder.getView(R.id.riqi_tv);
		TextView comename_tv = holder.getView(R.id.comename_tv);
		RelativeLayout context_rl = holder.getView(R.id.context_rl);
		TextView pasue_tv = holder.getView(R.id.pasue_tv);
		LinearLayout ll = holder.getView(R.id.ll);
		TextView nongli_tv = holder.getView(R.id.nongli_tv);

		delete_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, map, 0);
			}
		});
		content_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setOnclick(position, map, 1);
			}
		});
		if (position == 0) {
			date_ll.setVisibility(View.VISIBLE);
		} else if (map.get(CLNFMessage.nfmType).equals(
				mList.get(position - 1).get(CLNFMessage.nfmType))) {
			date_ll.setVisibility(View.GONE);
		} else if (map.get(CLNFMessage.nfmType).equals("4")
				&& mList.get(position - 1).get(CLNFMessage.nfmType).equals("6")) {
			date_ll.setVisibility(View.GONE);
		} else if (map.get(CLNFMessage.nfmType).equals("6")
				&& mList.get(position - 1).get(CLNFMessage.nfmType).equals("4")) {
			date_ll.setVisibility(View.GONE);
		} else {
			date_ll.setVisibility(View.VISIBLE);
		}

		String clockTime = "";
		String colorState = "";
		String alarmClockTime = "";
		alarmClockTime = DateUtil.formatDateTimeHm(DateUtil.parseDateTimeHm(map
				.get(CLNFMessage.nfmTime)));
		int beforTime = Integer.parseInt(map.get(CLNFMessage.nfmBeforeTime));
		String beforeStr = "";

		if (beforTime == 0) {
			beforeStr = "0";
		} else if (beforTime == 5) {
			beforeStr = "-5";
		} else if (beforTime == 15) {
			beforeStr = "-15";
		} else if (beforTime == 30) {
			beforeStr = "-30";
		} else if (beforTime == 60) {
			beforeStr = "-1h";
		} else if (beforTime == 120) {
			beforeStr = "-2h";
		} else if (beforTime == 1440) {
			beforeStr = "-1d";
		} else if (beforTime == 2 * 1440) {
			beforeStr = "-2d";
		} else if (beforTime == 7 * 1440) {
			beforeStr = "-1w";
		} else {
			beforeStr = "0";
		}
		if (beforeStr.equals("0")) {
			clockTime = "<font color='" + colorState + "' size='5px'>"
					+ alarmClockTime + "</font>";
		} else {
			clockTime = "<font color='" + colorState + "' size='5px'>"
					+ alarmClockTime + "(" + beforeStr + ")" + "</font>";
		}
		String allday = "<font color='" + colorState + "' size='5px'>" + "全天"
				+ "</font>";
		String strdate = map.get(CLNFMessage.nfmParameter).replace("[", "")
				.replace("]", "").replace("\n\"", "").replace("\n", "")
				.replace("\"", "").toString();
		String weekStr;

		content_tv.setText(map.get(CLNFMessage.nfmContent));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				Utils.dipTopx(context, 80), Utils.dipTopx(context, 65));
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		if (date_ll.getVisibility() == View.VISIBLE) {
			params.setMargins(0, Utils.dipTopx(context, 55), 0, 0);
			delete_ll.setLayoutParams(params);
			if (position == (mList.size() - 1)) {
				params1.setMargins(Utils.dipTopx(context, 8),
						Utils.dipTopx(context, 20), Utils.dipTopx(context, 8),
						Utils.dipTopx(context, 60));
			} else {
				params1.setMargins(Utils.dipTopx(context, 8),
						Utils.dipTopx(context, 20), Utils.dipTopx(context, 8),
						0);
			}
			ll.setLayoutParams(params1);
		} else {
			params.setMargins(0, Utils.dipTopx(context, 13), 0, 0);
			delete_ll.setLayoutParams(params);
			if (position == (mList.size() - 1)) {
				params1.setMargins(Utils.dipTopx(context, 8),
						Utils.dipTopx(context, 0), Utils.dipTopx(context, 8),
						Utils.dipTopx(context, 60));
			} else {
				params1.setMargins(Utils.dipTopx(context, 8),
						Utils.dipTopx(context, 0), Utils.dipTopx(context, 8), 0);
			}

			ll.setLayoutParams(params1);
		}

		String downstate = map.get(CLNFMessage.nfmDownState);
		String nfmisend = map.get(CLNFMessage.nfmIsEnd);
		if ("1".equals(nfmisend)) {
			personstate_tv.setVisibility(View.VISIBLE);
			personstate_tv.setText("已结束");
			personstate_tv.setTextColor(context.getResources().getColor(
					R.color.gongkai_txt));
		} else if (downstate.equals("0")) {
			personstate_tv.setVisibility(View.GONE);
			// personstate_tv.setText("未下行");
			// personstate_tv.setTextColor(context.getResources().getColor(
			// R.color.sunday_txt));
		} else if (downstate.equals("1")) {
			personstate_tv.setVisibility(View.VISIBLE);
			personstate_tv.setText("已下行");
			personstate_tv.setTextColor(context.getResources().getColor(
					R.color.sunday_txt));
		}

		String displaytime = map.get(CLNFMessage.nfmDisplayTime);
		if ("1".equals(displaytime)) {
			if ("1".equals(map.get(CLNFMessage.nfmType))) {
				riqi_tv.setVisibility(View.GONE);
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  每天" + "  ");
				time_tv.setText(Html.fromHtml(clockTime));
			} else if ("2".equals(map.get(CLNFMessage.nfmType))) {
				riqi_tv.setVisibility(View.VISIBLE);
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  每周" + "  ");
				if (strdate.equals("1")) {
					weekStr = "一";
				} else if (strdate.equals("2")) {
					weekStr = "二";
				} else if (strdate.equals("3")) {
					weekStr = "三";
				} else if (strdate.equals("4")) {
					weekStr = "四";
				} else if (strdate.equals("5")) {
					weekStr = "五";
				} else if (strdate.equals("6")) {
					weekStr = "六";
				} else {
					weekStr = "日";
				}
				riqi_tv.setText("周" + weekStr);
				time_tv.setText(Html.fromHtml(clockTime));
			} else if ("3".equals(map.get(CLNFMessage.nfmType))) {
				riqi_tv.setVisibility(View.VISIBLE);
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  每月" + "  ");
				riqi_tv.setText(strdate + "日");
				time_tv.setText(Html.fromHtml(clockTime));
			} else if ("4".equals(map.get(CLNFMessage.nfmType))) {
				riqi_tv.setVisibility(View.VISIBLE);
				date_tv.setText("  每年" + "  ");
				nongli_tv.setVisibility(View.GONE);
				riqi_tv.setText(strdate);
				time_tv.setText(Html.fromHtml(clockTime));
			} else if ("6".equals(map.get(CLNFMessage.nfmType))) {
				riqi_tv.setVisibility(View.VISIBLE);
				date_tv.setText("  每年" + "  ");
				nongli_tv.setVisibility(View.VISIBLE);
				riqi_tv.setText(strdate);
				time_tv.setText(Html.fromHtml(clockTime));
			} else {
				riqi_tv.setVisibility(View.GONE);
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  工作日" + "  ");
				time_tv.setText(Html.fromHtml(clockTime));
			}
		} else {
			if ("1".equals(map.get(CLNFMessage.nfmType))) {
				riqi_tv.setVisibility(View.GONE);
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  每天" + "  ");
				time_tv.setText(Html.fromHtml(allday));
			} else if ("2".equals(map.get(CLNFMessage.nfmType))) {
				riqi_tv.setVisibility(View.VISIBLE);
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  每周" + "  ");
				if (strdate.equals("1")) {
					weekStr = "一";
				} else if (strdate.equals("2")) {
					weekStr = "二";
				} else if (strdate.equals("3")) {
					weekStr = "三";
				} else if (strdate.equals("4")) {
					weekStr = "四";
				} else if (strdate.equals("5")) {
					weekStr = "五";
				} else if (strdate.equals("6")) {
					weekStr = "六";
				} else {
					weekStr = "日";
				}
				riqi_tv.setText("周" + weekStr);
				time_tv.setText(Html.fromHtml(allday));
			} else if ("3".equals(map.get(CLNFMessage.nfmType))) {
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  每月" + "  ");
				riqi_tv.setText(strdate + "日");
				time_tv.setText(Html.fromHtml(allday));
			} else if ("4".equals(map.get(CLNFMessage.nfmType))) {
				nongli_tv.setVisibility(View.GONE);
				date_tv.setText("  每年" + "  ");
				riqi_tv.setText(strdate);
				time_tv.setText(Html.fromHtml(allday));
			} else if ("6".equals(map.get(CLNFMessage.nfmType))) {
				date_tv.setText("  每年" + "  ");
				nongli_tv.setVisibility(View.VISIBLE);
				riqi_tv.setText(strdate);
				time_tv.setText(Html.fromHtml(clockTime));
			} else {
				nongli_tv.setVisibility(View.GONE);
				riqi_tv.setVisibility(View.GONE);
				date_tv.setText("  工作日" + "  ");
				time_tv.setText(Html.fromHtml(allday));
			}
		}
		comename_tv.setVisibility(View.GONE);

		if ("1".equals(map.get(CLNFMessage.nfmIsPuase))) {
			pasue_tv.setVisibility(View.VISIBLE);
			pasue_tv.setText("暂停");
			context_rl.setBackground(context.getResources().getDrawable(
					R.drawable.bg_rep_pause));
		} else {
			pasue_tv.setVisibility(View.GONE);
			context_rl.setBackground(context.getResources().getDrawable(
					R.drawable.bg_sch_normal));
		}
		
	}
}
