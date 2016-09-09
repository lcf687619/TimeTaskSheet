package com.mission.schedule.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mission.schedule.activity.AddRepeatActivity;
import com.mission.schedule.utils.MyScrollLayout;
import com.mission.schedule.utils.ScrollLinearLayout;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SolarCalendarFragment extends Fragment {

	private ScrollLinearLayout date_scroll_month;
	private MyScrollLayout date_scroll_day;

	private String[] monthStr = new String[12];
	private TextView[] mTextView = new TextView[12];
	private int viewWidth;// 屏幕的宽
	private int dayStr;
	private int monthStrT;
	private int whichPageIndex;

	private boolean isShow;// 判断是否已经显示
	private List<String> mList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_solarcalendar, container,false);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && !isShow) {
			isShow = true;
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			viewWidth = dm.widthPixels;

			init();
			initMonth();
			initViewMonth();
			initData();
			setCurPoint(monthStrT - 1);
			initDay();
		}
	}

	public void setMonthAndDay(int monthStrT, int dayStr) {
		this.monthStrT = monthStrT;
		this.dayStr = dayStr;
	}

	private void init() {
		mList = new ArrayList<String>();
		View view = getView();
		date_scroll_month = (ScrollLinearLayout) view
				.findViewById(R.id.date_scroll_month);
		date_scroll_month.setPageCount(6);
		date_scroll_month.setHandler(null);

		date_scroll_day = (MyScrollLayout) view
				.findViewById(R.id.date_scroll_day);
		date_scroll_day.setHandler(handlerDay);
		date_scroll_day
				.setOnViewChangeListener(new MyScrollLayout.OnViewChangeListener() {

					@Override
					public void OnViewChange(int page) {
						monthStrT = page + 1;
						setCurPoint(page);

						if (date_scroll_day.getDirection()) {// 右滚动
							if ((page + 1) % 6 == 1) {
								whichPageIndex++;
								date_scroll_month.snapToScreen(whichPageIndex);
							}
						} else {
							if ((page + 1) % 6 == 0 && (page + 1) != 12) {// 左滚动
								if (whichPageIndex > 0)
									whichPageIndex--;
								date_scroll_month.snapToScreen(whichPageIndex);
							}
						}

						if (whichPageIndex != date_scroll_month.getmCurScreen())
							date_scroll_month.snapToScreen(whichPageIndex);

					}
				});

	}
	private void initDay() {
		String date = monthStrT+"-"+dayStr;
		if(date.indexOf(",") != -1){
			String[] days = date.split(",");
			for(int i = 0; i < days.length; i++){
				int daySplit = Integer.parseInt(days[i]);
				mList.add(String.valueOf(daySplit));
			}
		}else{
			mList.add(date);
		}
	}
	private Handler handlerDay = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			date_scroll_day.snapToScreen(monthStrT - 1);
			handlerDay.postDelayed(runnable, 2000);
		}

	};

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if ((monthStrT - 1) >= 6) {
				date_scroll_month.snapToScreen(1);
				whichPageIndex = 1;
			} else {
				date_scroll_month.snapToScreen(0);
			}
		}
	};

	private void initMonth() {
		for (int i = 1; i <= 12; i++) {
			monthStr[i - 1] = i + "月";
		}
	}

	private void initViewMonth() {
		date_scroll_month.removeAllViews();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		ColorStateList csl = getResources().getColorStateList(
				R.color.bg_color);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				viewWidth / 6, LinearLayout.LayoutParams.FILL_PARENT);
		for (int i = 0; i < monthStr.length; i++) {
			final TextView textView = new TextView(getActivity());
			mTextView[i] = textView;
			textView.setTag(i);
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(15.0f);
			textView.setTextColor(csl);
			TextPaint paint = textView.getPaint();
			paint.setFakeBoldText(true);
			textView.setText(monthStr[i]);
			date_scroll_month.addView(textView, params);
			textView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = Integer.parseInt(textView.getTag().toString());
					if (index >= 6)
						whichPageIndex = 1;
					else
						whichPageIndex = 0;
					date_scroll_day.snapToScreen(index);
				}
			});
		}
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mTextView.length - 1) {
			return;
		}
		for (int i = 0; i < mTextView.length; i++) {
			if (index == i) {
				mTextView[i].setTextColor(getResources().getColorStateList(
						R.color.light_blue));
			} else {
				mTextView[i].setTextColor(getResources().getColorStateList(
						R.color.black));
			}
		}
	}

	private void initData() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				viewWidth / 7, ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		Calendar calendar = Calendar.getInstance();

		for (int k = 0; k < 12; k++) {
			calendar.set(Calendar.MONTH, k);
			LinearLayout mainLayout = new LinearLayout(getActivity());
			mainLayout.setOrientation(LinearLayout.VERTICAL);

			int maxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

			for (int i = 1; i <= 5; i++) {
				LinearLayout linearLayout = new LinearLayout(getActivity());
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				for (int j = 1 + (i - 1) * 7; j <= 7 + (i - 1) * 7; j++) {
					TextView textView = new TextView(getActivity());
					textView.setTextSize(15.0f);
					textView.setGravity(Gravity.CENTER);
					textView.setTextColor(getResources().getColor(R.color.black));
					if (dayStr == j
							&& monthStrT == (calendar.get(Calendar.MONTH) + 1)) {
						textView.setBackgroundResource(R.drawable.month_bg1);
						textView.setTextColor(getResources().getColor(R.color.sunday_txt));
					} else {
						if (j == 1 || j == 8 || j == 15 || j == 22 || j == 29) {
							textView.setBackgroundResource(R.drawable.month_bg);
						} else {
							textView.setBackgroundResource(R.drawable.month_bg2);
						}
					}
					if (j <= maxDate) {
						textView.setTag(j);
						textView.setText("" + j);
						textView.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								close(v);
							}
						});
					}
					linearLayout.addView(textView, params);
				}
				mainLayout.addView(linearLayout, params2);
			}
			View view = new View(getActivity());
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, 2));
			view.setBackgroundColor(getResources().getColor(R.color.bg_color));
			mainLayout.addView(view);
			date_scroll_day.addView(mainLayout, params3);
		}
	}

	private void close(View v) {
		Intent intent = new Intent(getActivity(), AddRepeatActivity.class);
		intent.putExtra("type", "0");// 公立
		intent.putExtra("month", monthStrT);
		intent.putExtra("day", v.getTag().toString());
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
	}
}
