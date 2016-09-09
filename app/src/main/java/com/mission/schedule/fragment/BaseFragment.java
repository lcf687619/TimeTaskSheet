package com.mission.schedule.fragment;

import com.mission.schedule.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public abstract class BaseFragment extends Fragment {

	public Dialog dialogFrag;
	/** 屏幕的宽度 */
	protected int mScreenWidth;
	/** 屏幕的高度 */
	protected int mScreenHeight;
	protected boolean isVisible;

	/**
	 * 在这里实现Fragment数据的缓加载.
	 * 
	 * @param isVisibleToUser
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	protected void onVisible() {
		lazyLoad();
	}

	protected abstract void lazyLoad();

	protected void onInvisible() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			DisplayMetrics metric = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay()
					.getMetrics(metric);
			mScreenWidth = metric.widthPixels;
			mScreenHeight = metric.heightPixels;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString()
							+ "must implement RefreshDataFragmentListener,refreshHomeCountListener");
		}
	}

	public void createFragDialog(String msg) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_layout, null);

		LinearLayout dialogLL = (LinearLayout) view;
		TextView tv_msg = (TextView) dialogLL.findViewById(R.id.tv_msg);
		tv_msg.setText(msg);
		dialogFrag = new Dialog(getActivity(), R.style.dialog_mass);
		Window win = dialogFrag.getWindow();// 获取所在window
		android.view.WindowManager.LayoutParams params = win.getAttributes();// 获取LayoutParams
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		win.setAttributes(params);// 设置生效
		dialogFrag.setContentView(dialogLL);
		dialogFrag.show();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (dialogFrag != null)
			dialogFrag.dismiss();
	}

}
