package com.mission.schedule.utils;

import com.mission.schedule.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class FragmentDialogUtils extends DialogFragment{
	
	View view = null;
	ImageView dialogfragment_iv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		view = inflater.inflate(R.layout.dialog_fragmentdialogutils, container);
		dialogfragment_iv = (ImageView) view.findViewById(R.id.dialogfragment_iv);
		dialogfragment_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return view;
	}
}
