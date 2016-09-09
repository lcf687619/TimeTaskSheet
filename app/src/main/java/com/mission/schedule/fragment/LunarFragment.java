package com.mission.schedule.fragment;

import java.util.Calendar;
import java.util.Date;

import com.mission.schedule.activity.AddRepeatActivity;
import com.mission.schedule.applcation.App;
import com.mission.schedule.utils.MyScrollLayout;
import com.mission.schedule.utils.ScrollLinearLayout;
import com.mission.schedule.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
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

public class LunarFragment extends Fragment {

	private ScrollLinearLayout date_scroll_month;
	private MyScrollLayout date_scroll_day;
	private TextView[] mTextView = new TextView[12];
	private int viewWidth;// 屏幕的宽
	private int dayStr;
	private int monthStrT;
	private int whichPageIndex;

	private boolean isShow;// 判断是否已经显示

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_lunarfragment, container, false);
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
			initViewMonth();
			initData();
			setCurPoint(monthStrT - 1);
		}
	}

	public void setMonthAndDay(int monthStrT, int dayStr) {
		this.monthStrT = monthStrT;
		this.dayStr = dayStr;
	}

	private void init() {
		View view = getView();
		date_scroll_month = (ScrollLinearLayout) view.findViewById(R.id.date_scroll_month);
		date_scroll_month.setPageCount(6);
		date_scroll_month.setHandler(null);

		date_scroll_day = (MyScrollLayout) view.findViewById(R.id.date_scroll_day);
		date_scroll_day.setHandler(handlerDay);
		date_scroll_day.setOnViewChangeListener(new MyScrollLayout.OnViewChangeListener() {

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

	private void initViewMonth() {
		date_scroll_month.removeAllViews();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		ColorStateList csl = getResources().getColorStateList(
				R.color.left_view);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				viewWidth / 6, LinearLayout.LayoutParams.FILL_PARENT);
		
		String[] monthStr = getResources().getStringArray(R.array.monthStr);
		
		for (int i = 0; i < monthStr.length; i++) {
			final TextView textView = new TextView(getActivity());
			mTextView[i] = textView;
			textView.setTag(i);
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(15.0f);
			textView.setTextColor(csl);
			TextPaint paint = textView.getPaint();
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
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		String[] lunarStr = getResources().getStringArray(R.array.lunarstr);

		for (int k = 0; k < 12; k++) {
			calendar.set(Calendar.MONTH, k);
			LinearLayout mainLayout = new LinearLayout(getActivity());
			mainLayout.setOrientation(LinearLayout.VERTICAL);

			for (int i = 1; i <= 5; i++) {
				LinearLayout linearLayout = new LinearLayout(getActivity());
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				for (int j = 1 + (i - 1) * 7; j <= 7 + (i - 1) * 7; j++) {
					TextView textView = new TextView(getActivity());
					textView.setTextSize(15.0f);
					textView.setTypeface(Typeface.DEFAULT_BOLD);
					textView.setGravity(Gravity.CENTER);
					textView.setTextColor(getResources().getColor(R.color.left_view));
					if (dayStr == j
							&& monthStrT == (calendar.get(Calendar.MONTH) + 1)) {
						textView.setBackgroundResource(R.drawable.month_bg1);
					} else {
						if (j == 1 || j == 8 || j == 15 || j == 22 || j == 29) {
							textView.setBackgroundResource(R.drawable.month_bg);
						} else {
							textView.setBackgroundResource(R.drawable.month_bg2);
						}
					}
					textView.setTag(j);
					textView.setText(lunarStr[j - 1]);
					textView.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							int index = Integer.parseInt(v.getTag().toString());
							if (index <= 30)
								close(v);
						}
					});
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
		App dbContextApplication = App.getDBcApplication();
		int day = Integer.parseInt(v.getTag().toString());
		String strMonth = monthStrT < 10 ? "0" + monthStrT : "" + monthStrT;
		String strDay = day < 10 ? "0" + day : "" + day;
//		if (dbContextApplication.queryLunartoSolarList(strMonth + "-" + strDay) != null) {
			Intent intent = new Intent(getActivity(), AddRepeatActivity.class);
			intent.putExtra("type", "1");// 农历
			intent.putExtra("month", monthStrT);
			intent.putExtra("day", v.getTag().toString());
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
//		} else {
//			Toast.makeText(getActivity(), "该版本农历数据不完整，请及时更新版本...",Toast.LENGTH_SHORT).show();
//		}
	}
}
