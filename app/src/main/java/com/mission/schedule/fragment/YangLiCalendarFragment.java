package com.mission.schedule.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mission.schedule.adapter.CalendarGridViewAdapter;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.tools.NumberHelper;
import com.mission.schedule.utils.CalendarGridView;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.ScrollLinearLayout;
import com.mission.schedule.utils.Utils;
import com.mission.schedule.R;

import de.greenrobot.event.EventBus;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

public class YangLiCalendarFragment extends Fragment implements OnTouchListener {

	// 判断手势用
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 100;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	// 动画
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	GestureDetector mGesture = null;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGesture.onTouchEvent(event);
	}

	AnimationListener animationListener = new AnimationListener() {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			// 当动画完成后调用
			CreateGirdView();
		}
	};

	class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				// if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				// return false;
				// right to left swipe
				if (velocityX < 0) {
					viewFlipper.setInAnimation(slideLeftIn);
					viewFlipper.setOutAnimation(slideLeftOut);
					viewFlipper.showNext();
					setNextViewItem();
					// CreateGirdView();
					return true;
				} else {
					viewFlipper.setInAnimation(slideRightIn);
					viewFlipper.setOutAnimation(slideRightOut);
					viewFlipper.showPrevious();
					setPrevViewItem();
					// CreateGirdView();
					return true;
				}
				// if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// viewFlipper.setInAnimation(slideLeftIn);
				// viewFlipper.setOutAnimation(slideLeftOut);
				// viewFlipper.showNext();
				// setNextViewItem();
				// // CreateGirdView();
				// return true;
				//
				// } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// viewFlipper.setInAnimation(slideRightIn);
				// viewFlipper.setOutAnimation(slideRightOut);
				// viewFlipper.showPrevious();
				// setPrevViewItem();
				// // CreateGirdView();
				// return true;
				//
				// }
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// ListView lv = getListView();
			// 得到当前选中的是第几个单元格
			int pos = gView2.pointToPosition((int) e.getX(), (int) e.getY());
			LinearLayout txtDay = (LinearLayout) gView2
					.findViewById(pos + 5000);
			if (txtDay != null) {
				if (txtDay.getTag() != null) {
					Date date = (Date) txtDay.getTag();
					calSelected.setTime(date);

					gAdapter.setSelectedDate(calSelected);
					gAdapter.notifyDataSetChanged();

					gAdapter1.setSelectedDate(calSelected);
					gAdapter1.notifyDataSetChanged();

					gAdapter3.setSelectedDate(calSelected);
					gAdapter3.notifyDataSetChanged();
				}
			}

			Log.i("TEST", "onSingleTapUp -  pos=" + pos);

			return false;
		}
	}

	// / }}}

	// 基本变量
	private Context mContext = getActivity();
	// private GridView title_gView;
	// private HorizontalListView title_view;
	private GridView gView1;// 上一个月
	private GridView gView2;// 当前月
	private GridView gView3;// 下一个月
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	private TextView tv5;
	// private GridView gView1;
	boolean bIsSelection = false;// 是否是选择事件发生
	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历
	private Calendar calToday = Calendar.getInstance(); // 今日
	private CalendarGridViewAdapter gAdapter;
	private CalendarGridViewAdapter gAdapter1;
	private CalendarGridViewAdapter gAdapter3;
	// 顶部按钮
	// private Button btnToday = null;
	private RelativeLayout mainLayout;

	//
	private int iMonthViewCurrentMonth = 0; // 当前视图月
	private int iMonthViewCurrentYear = 0; // 当前视图年
	private int iFirstDayOfWeek = Calendar.MONDAY;

	private static final int mainLayoutID = 88; // 设置主布局ID
	private static final int titleLayoutID = 77; // title布局ID
	private static final int caltitleLayoutID = 66; // title布局ID
	private static final int calLayoutID = 55; // 日历布局ID
	private static final int bottomLayoutID = 99;
	private static final int titleLineViewID = 44;
	private static final int bottomLineViewID = 33;
	int width;
	private ScrollLinearLayout linearLayout;
	private String[] monthStr = new String[12];
	private TextView[] mTextView = new TextView[12];
	private int whichPageIndex;
	private int dayStr;
	private int monthStrT = 0;
	private int screenindex = 0;
	private LinearLayout sure_ll;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return generateContentView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		width = display.getWidth();
		EventBus.getDefault().register(this);
		UpdateStartDateForMonth();
		setCurPoint(monthStrT - 1);

		slideLeftIn = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_right_out);

		slideLeftIn.setAnimationListener(animationListener);
		slideLeftOut.setAnimationListener(animationListener);
		slideRightIn.setAnimationListener(animationListener);
		slideRightOut.setAnimationListener(animationListener);

		mGesture = new GestureDetector(getActivity(), new GestureListener());

		// sure_ll = DateSetActivity.sure_ll;
		// sure_ll.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(getActivity(), AddRepeatActivity.class);
		// intent.putExtra("type", "0");// 公立
		// intent.putStringArrayListExtra("list", (ArrayList<String>) strlist);
		// getActivity().setResult(Activity.RESULT_OK, intent);
		// getActivity().finish();
		// }
		// });
	}

	// 生成内容视图
	private View generateContentView() {
		// 创建一个垂直的线性布局（整体内容）
		viewFlipper = new ViewFlipper(getActivity());
		viewFlipper.setId(calLayoutID);

		mainLayout = new RelativeLayout(getActivity()); // 创建一个垂直的线性布局（整体内容）
		RelativeLayout.LayoutParams params_main = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainLayout.setLayoutParams(params_main);
		mainLayout.setId(mainLayoutID);
		mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		mainLayout.setBackgroundColor(getResources().getColor(R.color.white));

		calStartDate = getCalendarStartDate();

		setTitleGirdView();
		RelativeLayout.LayoutParams params_cal_title = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, Utils.dipTopx(getActivity(), 40));
		params_cal_title.addRule(RelativeLayout.BELOW, titleLayoutID);
		// params_cal_title.topMargin = 5;
		mainLayout.addView(linearLayout, params_cal_title);

		RelativeLayout.LayoutParams title_line_view = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 1);
		title_line_view.addRule(RelativeLayout.BELOW, caltitleLayoutID);
		View view = new View(getActivity());
		view.setBackgroundColor(getResources().getColor(R.color.left_view));
		view.setLayoutParams(title_line_view);
		view.setId(titleLineViewID);
		mainLayout.addView(view, title_line_view);

		CreateGirdView();

		RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params_cal.addRule(RelativeLayout.BELOW, titleLineViewID);

		mainLayout.addView(viewFlipper, params_cal);

		RelativeLayout.LayoutParams bottom_line_view = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 1);
		bottom_line_view.addRule(RelativeLayout.BELOW, calLayoutID);
		View view1 = new View(getActivity());
		view1.setBackgroundColor(getResources().getColor(R.color.left_view));
		view1.setLayoutParams(title_line_view);
		view1.setId(bottomLineViewID);
		mainLayout.addView(view1, bottom_line_view);

		LinearLayout br = new LinearLayout(getActivity());
		RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 1);
		params_br.addRule(RelativeLayout.BELOW, bottomLineViewID);
		br.setBackgroundColor(getResources().getColor(R.color.white));
		mainLayout.addView(br, params_br);

		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL); // 生成顶部按钮布局

		generateTopButtons(layTopControls); // 生成顶部按钮 （上一月，下一月，当前月）
		RelativeLayout.LayoutParams params_title = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params_title.topMargin = 100;
		params_title.addRule(RelativeLayout.BELOW, bottomLineViewID);
		// params_title.addRule(RelativeLayout.ALIGN_PARENT_TOP, 20);
		layTopControls.setId(bottomLayoutID);
		mainLayout.addView(layTopControls, params_title);

		return mainLayout;

	}

	private void generateTopButtons(LinearLayout layTopControls) {
		// 创建一个当前月按钮（中间的按钮）
		tv1 = new TextView(getActivity());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = 20;
		tv1.setLayoutParams(lp);
		tv1.setTextSize(16);

		tv2 = new TextView(getActivity());
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp1.leftMargin = 20;
		tv2.setLayoutParams(lp1);
		tv2.setTextSize(16);

		tv3 = new TextView(getActivity());
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp2.leftMargin = 20;
		tv3.setLayoutParams(lp2);
		tv3.setTextSize(16);

		tv4 = new TextView(getActivity());
		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp3.leftMargin = 20;
		tv4.setLayoutParams(lp3);
		tv4.setTextSize(16);

		tv5 = new TextView(getActivity());
		LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp4.leftMargin = 20;
		tv5.setLayoutParams(lp4);
		tv5.setTextSize(16);

		layTopControls.setGravity(Gravity.CENTER_HORIZONTAL);
		layTopControls.addView(tv1);
		layTopControls.addView(tv2);
		layTopControls.addView(tv3);
		layTopControls.addView(tv4);
		layTopControls.addView(tv5);

		tv1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeleteDialog(tv1.getText().toString(), 1);
				// list.remove(0);

			}
		});
		tv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeleteDialog(tv2.getText().toString(), 2);
				// list.remove(1);
			}
		});
		tv3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeleteDialog(tv3.getText().toString(), 3);
				// list.remove(2);
			}
		});
		tv4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// list.remove(3);
				DeleteDialog(tv4.getText().toString(), 4);
			}
		});
		tv5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// list.remove(4);
				DeleteDialog(tv5.getText().toString(), 5);
			}
		});

	}

	// 创建一个线性布局
	// 参数：方向
	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(getActivity());
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,// *fill_parent，填满父控件的空白
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;
		// 设置布局参数
		lay.setLayoutParams(params);// *wrap_content，表示大小刚好足够显示当前控件里的内容
		lay.setOrientation(iOrientation);// 设置方向
		lay.setGravity(Gravity.LEFT);
		return lay;
	}

	private void setTitleGirdView() {
		linearLayout = setTitleView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, Utils.dipTopx(getActivity(), 40));
		params.gravity = Gravity.CENTER;
		linearLayout.setLayoutParams(params);
		linearLayout.setId(caltitleLayoutID);
		initMonth();
		initViewMonth();
	}

	private void CreateGirdView() {

		Calendar tempSelected1 = Calendar.getInstance(); // 临时
		Calendar tempSelected2 = Calendar.getInstance(); // 临时
		Calendar tempSelected3 = Calendar.getInstance(); // 临时
		tempSelected1.setTime(calStartDate.getTime());
		tempSelected2.setTime(calStartDate.getTime());
		tempSelected3.setTime(calStartDate.getTime());

		gView1 = new CalendarGridView(getActivity());
		tempSelected1.add(Calendar.MONTH, -1);
		gAdapter1 = new CalendarGridViewAdapter(getActivity(), tempSelected1,
				width, handler, "", strlist);
		gView1.setAdapter(gAdapter1);// 设置菜单Adapter
		gView1.setId(calLayoutID);

		gView2 = new CalendarGridView(getActivity());
		gAdapter = new CalendarGridViewAdapter(getActivity(), tempSelected2,
				width, handler, "", strlist);
		gView2.setAdapter(gAdapter);// 设置菜单Adapter
		gView2.setId(calLayoutID);

		gView3 = new CalendarGridView(getActivity());
		tempSelected3.add(Calendar.MONTH, 1);
		gAdapter3 = new CalendarGridViewAdapter(getActivity(), tempSelected3,
				width, handler, "", strlist);
		gView3.setAdapter(gAdapter3);// 设置菜单Adapter
		gView3.setId(calLayoutID);

		gView2.setOnTouchListener(this);
		gView1.setOnTouchListener(this);
		gView3.setOnTouchListener(this);

		if (viewFlipper.getChildCount() != 0) {
			viewFlipper.removeAllViews();
		}

		viewFlipper.addView(gView2);
		viewFlipper.addView(gView3);
		viewFlipper.addView(gView1);

		String s = calStartDate.get(Calendar.YEAR)
				+ "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
						.get(Calendar.MONTH) + 1);

		// btnToday.setText(s);
		for (int i = 1; i < 13; i++) {
			if (Integer.parseInt(s.substring(5)) == i) {
				whichPageIndex = i;
			}
		}
		setCurPoint(whichPageIndex - 1);
		if (whichPageIndex % 6 == 1) {
			if (whichPageIndex == 1) {
				linearLayout.snapToScreen(0);
			} else {
				linearLayout.snapToScreen(1);
			}
		} else {
			if (whichPageIndex % 6 == 0 && whichPageIndex != 12) {// 左滚动
				if (whichPageIndex > 0)
					linearLayout.snapToScreen(0);
			}
			if (screenindex != linearLayout.getmCurScreen()) {
				if (whichPageIndex == 12) {
					linearLayout.snapToScreen(1);
				}
			}
		}
	}

	private ScrollLinearLayout setTitleView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, Utils.dipTopx(getActivity(), 40));
		params.gravity = Gravity.CENTER;
		ScrollLinearLayout gridView = new ScrollLinearLayout(getActivity());
		gridView.setPageCount(6);
		gridView.setHandler(handlerDay);
		gridView.setLayoutParams(params);
		gridView.setBackgroundColor(getResources().getColor(R.color.white));
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		width = display.getWidth();

		return gridView;
	}

	// 上一个月
	private void setPrevViewItem() {
		iMonthViewCurrentMonth--;// 当前选择月--
		// 如果当前月为负数的话显示上一年
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年

	}

	// 当月
	// private void setToDayViewItem(int month) {
	//
	// calSelected.setTimeInMillis(calToday.getTimeInMillis());
	// calSelected.setFirstDayOfWeek(iFirstDayOfWeek);
	// calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
	//
	// }

	// 下一个月
	private void setNextViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);

	}

	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// 得到当前日历显示的年

		String s = calStartDate.get(Calendar.YEAR)
				+ "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
						.get(Calendar.MONTH) + 1);
		// btnToday.setText(s);
		for (int i = 1; i < 13; i++) {
			if (Integer.parseInt(s.substring(5)) == i) {
				whichPageIndex = i;
			}
		}
		setCurPoint(whichPageIndex - 1);
		if (whichPageIndex % 6 == 1) {
			if (whichPageIndex == 1) {
				linearLayout.snapToScreen(0);
			} else {
				linearLayout.snapToScreen(1);
			}
		} else {
			if (whichPageIndex % 6 == 0 && whichPageIndex != 12) {// 左滚动
				if (whichPageIndex > 0)
					linearLayout.snapToScreen(0);
			}
			if (screenindex != linearLayout.getmCurScreen()) {
				if (whichPageIndex == 12) {
					linearLayout.snapToScreen(1);
				}
			}
		}
		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}

	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		return calStartDate;
	}

	private void initMonth() {
		for (int i = 1; i <= 12; i++) {
			monthStr[i - 1] = i + "月";
		}
	}

	private void initViewMonth() {
		linearLayout.removeAllViews();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		ColorStateList csl = getResources().getColorStateList(R.color.black);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				width / 6, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		for (int i = 0; i < monthStr.length; i++) {
			final TextView textView = new TextView(getActivity());
			mTextView[i] = textView;
			textView.setTag(i);
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(15.0f);
			textView.setTextColor(csl);
			// TextPaint paint = textView.getPaint();
			// paint.setFakeBoldText(true);
			textView.setText(monthStr[i]);
			linearLayout.addView(textView, params);
			textView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = Integer.parseInt(textView.getTag().toString());
					if (index >= 6)
						screenindex = 1;
					else
						screenindex = 0;
					if (iMonthViewCurrentMonth > index) {
						iMonthViewCurrentMonth = index;
						viewFlipper.setInAnimation(slideRightIn);
						viewFlipper.setOutAnimation(slideRightOut);
						viewFlipper.showNext();
						setViewItem(index);
					} else {
						iMonthViewCurrentMonth = index;
						viewFlipper.setInAnimation(slideLeftIn);
						viewFlipper.setOutAnimation(slideLeftOut);
						viewFlipper.showNext();
						setViewItem(index);
					}
				}
			});
		}
	}

	private void setViewItem(int index) {
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, index);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);

	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mTextView.length) {
			return;
		}
		for (int i = 0; i < mTextView.length; i++) {
			if (index == i) {
				mTextView[i].setTextColor(getResources().getColorStateList(
						R.color.black));
				mTextView[i].setBackgroundDrawable(getResources().getDrawable(
						R.drawable.bg_choose_month));
			} else {
				mTextView[i].setTextColor(getResources().getColorStateList(
						R.color.black));
				mTextView[i].setBackgroundColor(getResources().getColor(
						R.color.white));
			}
		}
	}

	// public void setMonthAndDay(int monthStrT, int dayStr) {
	// this.monthStrT = monthStrT;
	// this.dayStr = dayStr;
	// strlist.add((monthStrT<10?"0"+monthStrT:monthStrT)+"-"+(dayStr<10?"0"+dayStr:dayStr));
	// }

	private Handler handlerDay = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handlerDay.postDelayed(runnable, 0);
		}

	};

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (whichPageIndex >= 6) {
				linearLayout.snapToScreen(1);
				screenindex = 1;
			} else {
				linearLayout.snapToScreen(0);
			}
		}
	};
	
	public void onEventMainThread(FristFragment event) {

		String msg = event.getMsg();
		if ("5".equals(msg)) {
			Calendar tempSelected1 = Calendar.getInstance(); // 临时
			Calendar tempSelected2 = Calendar.getInstance(); // 临时
			Calendar tempSelected3 = Calendar.getInstance(); // 临时
			tempSelected1.setTime(calStartDate.getTime());
			tempSelected2.setTime(calStartDate.getTime());
			tempSelected3.setTime(calStartDate.getTime());

			tempSelected1.add(Calendar.MONTH, -1);
			gAdapter1 = new CalendarGridViewAdapter(getActivity(), tempSelected1,
					width, handler, "", strlist);
			gView1.setAdapter(gAdapter1);// 设置菜单Adapter

			gAdapter = new CalendarGridViewAdapter(getActivity(), tempSelected2,
					width, handler, "", strlist);
			gView2.setAdapter(gAdapter);// 设置菜单Adapter

			tempSelected3.add(Calendar.MONTH, 1);
			gAdapter3 = new CalendarGridViewAdapter(getActivity(), tempSelected3,
					width, handler, "", strlist);
			gView3.setAdapter(gAdapter3);// 设置菜单Adapter
			tv1.setVisibility(View.GONE);
			tv2.setVisibility(View.GONE);
			tv3.setVisibility(View.GONE);
			tv4.setVisibility(View.GONE);
			tv5.setVisibility(View.GONE);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		strlist.clear();
	}
	// List<Integer> list = new ArrayList<Integer>();
	public static List<String> strlist = new ArrayList<String>();
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int pos = msg.arg1;
			Date date = (Date) msg.obj;
			int what = msg.what;
			String string = DateUtil.formatDateMMDD(date);
			if (what == 3) {
				Toast toast = Toast.makeText(getActivity(), "最多支持选择5个!",
						Toast.LENGTH_SHORT);
				// 可以控制toast显示的位置
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else if (what == 0) {
				// for (int i = 0; i < list.size(); i++) {
				// if (pos == list.get(i)) {
				// list.remove(i);
				// }
				// }
				for (int i = 0; i < strlist.size(); i++) {
					if (string.equals(strlist.get(i))) {
						strlist.remove(i);
					}
				}
				if (string.equals(tv1.getText().toString())) {
					tv1.setVisibility(View.GONE);
				} else if (string.equals(tv2.getText().toString())) {
					tv2.setVisibility(View.GONE);
				} else if (string.equals(tv3.getText().toString())) {
					tv3.setVisibility(View.GONE);
				} else if (string.equals(tv4.getText().toString())) {
					tv4.setVisibility(View.GONE);
				} else if (string.equals(tv5.getText().toString())) {
					tv5.setVisibility(View.GONE);
				}
			} else {
				// list.add(pos);
				strlist.add(string);
				if (strlist.size() == 1) {
					tv1.setText(string);
					tv1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv1.setVisibility(View.VISIBLE);
					tv2.setVisibility(View.GONE);
					tv3.setVisibility(View.GONE);
					tv4.setVisibility(View.GONE);
					tv5.setVisibility(View.GONE);
				} else if (strlist.size() == 2) {
					tv2.setText(string);
					tv1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setVisibility(View.VISIBLE);
					tv3.setVisibility(View.GONE);
					tv4.setVisibility(View.GONE);
					tv5.setVisibility(View.GONE);
				} else if (strlist.size() == 3) {
					tv3.setText(string);
					tv1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setVisibility(View.VISIBLE);
					tv3.setVisibility(View.VISIBLE);
					tv4.setVisibility(View.GONE);
					tv5.setVisibility(View.GONE);
				} else if (strlist.size() == 4) {
					tv4.setText(string);
					tv1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv4.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setVisibility(View.VISIBLE);
					tv3.setVisibility(View.VISIBLE);
					tv4.setVisibility(View.VISIBLE);
					tv5.setVisibility(View.GONE);
				} else if (strlist.size() == 5) {
					tv5.setText(string);
					tv1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv4.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv5.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.bg_choose_date));
					tv2.setVisibility(View.VISIBLE);
					tv3.setVisibility(View.VISIBLE);
					tv4.setVisibility(View.VISIBLE);
					tv5.setVisibility(View.VISIBLE);
				}
			}

		}

	};

	/**
	 * 点击是否删除
	 */
	private void DeleteDialog(String date, int type) {
		Context context = getActivity();
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_date_delete, null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
		dialog.show();

		new DeleteOnClick(dialog, more_pop_menu, date, type);
	}

	class DeleteOnClick implements View.OnClickListener {

		private View view;
		private Dialog dialog;
		private TextView delete_tv;
		private TextView cancle_tv;
		private TextView content_tv;
		private int type;
		private String date;

		@SuppressLint("NewApi")
		public DeleteOnClick(Dialog dialog, View view, String date, int type) {
			this.dialog = dialog;
			this.view = view;
			this.date = date;
			this.type = type;
			initview();
			initdata();
		}

		private void initview() {
			delete_tv = (TextView) view.findViewById(R.id.delete_tv);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			content_tv = (TextView) view.findViewById(R.id.content_tv);
			delete_tv.setOnClickListener(this);
			cancle_tv.setOnClickListener(this);
		}

		private void initdata() {
			content_tv.setText("删除  " + "\"" + date + "\"");
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.delete_tv:
				if (type == 1) {
					for (int i = 0; i < strlist.size(); i++) {
						if (date.equals(strlist.get(i))) {
							strlist.remove(i);
						}
					}
					Calendar tempSelected2 = Calendar.getInstance(); // 临时
					tempSelected2.setTime(calStartDate.getTime());
					gAdapter = new CalendarGridViewAdapter(getActivity(),
							tempSelected2, width, handler, date, strlist);
					gView2.setAdapter(gAdapter);// 设置菜单Adapter
					tv1.setVisibility(View.GONE);
				} else if (type == 2) {
					for (int i = 0; i < strlist.size(); i++) {
						if (date.equals(strlist.get(i))) {
							strlist.remove(i);
						}
					}
					Calendar tempSelected2 = Calendar.getInstance(); // 临时
					tempSelected2.setTime(calStartDate.getTime());
					gAdapter = new CalendarGridViewAdapter(getActivity(),
							tempSelected2, width, handler, date, strlist);
					gView2.setAdapter(gAdapter);// 设置菜单Adapter
					tv2.setVisibility(View.GONE);
				} else if (type == 3) {
					for (int i = 0; i < strlist.size(); i++) {
						if (date.equals(strlist.get(i))) {
							strlist.remove(i);
						}
					}
					Calendar tempSelected2 = Calendar.getInstance(); // 临时
					tempSelected2.setTime(calStartDate.getTime());
					gAdapter = new CalendarGridViewAdapter(getActivity(),
							tempSelected2, width, handler, date, strlist);
					gView2.setAdapter(gAdapter);// 设置菜单Adapter
					tv3.setVisibility(View.GONE);
				} else if (type == 4) {
					for (int i = 0; i < strlist.size(); i++) {
						if (date.equals(strlist.get(i))) {
							strlist.remove(i);
						}
					}
					Calendar tempSelected2 = Calendar.getInstance(); // 临时
					tempSelected2.setTime(calStartDate.getTime());
					gAdapter = new CalendarGridViewAdapter(getActivity(),
							tempSelected2, width, handler, date, strlist);
					gView2.setAdapter(gAdapter);// 设置菜单Adapter
					tv4.setVisibility(View.GONE);
				} else {
					for (int i = 0; i < strlist.size(); i++) {
						if (date.equals(strlist.get(i))) {
							strlist.remove(i);
						}
					}
					Calendar tempSelected2 = Calendar.getInstance(); // 临时
					tempSelected2.setTime(calStartDate.getTime());
					gAdapter = new CalendarGridViewAdapter(getActivity(),
							tempSelected2, width, handler, date, strlist);
					gView2.setAdapter(gAdapter);// 设置菜单Adapter
					tv5.setVisibility(View.GONE);
				}
				dialog.dismiss();
				break;
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}
}
