package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeekActivity extends BaseActivity implements OnClickListener{

	@ViewResId(id = R.id.week_back_linear)
	private LinearLayout week_back_linear;
	@ViewResId(id = R.id.week_ok)
	private TextView week_ok;
	@ViewResId(id = R.id.week_one_rela)
	private LinearLayout week_one_rela;
	@ViewResId(id = R.id.week_two_rela)
	private LinearLayout week_two_rela;
	@ViewResId(id = R.id.week_three_rela)
	private LinearLayout week_three_rela;
	@ViewResId(id = R.id.week_four_rela)
	private LinearLayout week_four_rela;
	@ViewResId(id = R.id.week_five_rela)
	private LinearLayout week_five_rela;
	@ViewResId(id = R.id.week_six_rela)
	private LinearLayout week_six_rela;
	@ViewResId(id = R.id.week_day_rela)
	private LinearLayout week_day_rela;
	
	private String weeks = "";// 星期
	private List<LinearLayout> mListView;
	
	int type = 0;//1AddRepeatActivity 2 AddNewFocusShareRepeatActivity

	@Override
	protected void setListener() {
		week_back_linear.setOnClickListener(this);
		week_one_rela.setOnClickListener(this);
		week_two_rela.setOnClickListener(this);
		week_three_rela.setOnClickListener(this);
		week_four_rela.setOnClickListener(this);
		week_five_rela.setOnClickListener(this);
		week_six_rela.setOnClickListener(this);
		week_day_rela.setOnClickListener(this);
		
		week_ok.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.acitivity_week);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		weeks = getIntent().getStringExtra("weeks");
		type = getIntent().getIntExtra("type", 0);
		mListView = new ArrayList<LinearLayout>();
		for(int i=0;i<weeks.split(",").length;i++){
			if(weeks.split(",")[i].equals("1")){
				week_one_rela.setTag("true");// 选中的标记
				week_one_rela.setBackgroundResource(R.color.week_color);
			}else if(weeks.split(",")[i].equals("2")){
				week_two_rela.setTag("true");// 选中的标记
				week_two_rela.setBackgroundResource(R.color.week_color);
			}else if(weeks.split(",")[i].equals("3")){
				week_three_rela.setTag("true");// 选中的标记
				week_three_rela.setBackgroundResource(R.color.week_color);
			}else if(weeks.split(",")[i].equals("4")){
				week_four_rela.setTag("true");// 选中的标记
				week_four_rela.setBackgroundResource(R.color.week_color);
			}else if(weeks.split(",")[i].equals("5")){
				week_five_rela.setTag("true");// 选中的标记
				week_five_rela.setBackgroundResource(R.color.week_color);
			}else if(weeks.split(",")[i].equals("6")){
				week_six_rela.setTag("true");// 选中的标记
				week_six_rela.setBackgroundResource(R.color.week_color);
			}else if(weeks.split(",")[i].equals("7")){
				week_day_rela.setTag("true");// 选中的标记
				week_day_rela.setBackgroundResource(R.color.week_color);
			}
		}
		
		initView();
	}
	private void initView() {
		mListView.add(week_one_rela);
		mListView.add(week_two_rela);
		mListView.add(week_three_rela);
		mListView.add(week_four_rela);
		mListView.add(week_five_rela);
		mListView.add(week_six_rela);
		mListView.add(week_day_rela);
	}
	@Override
	protected void setAdapter() {
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.week_back_linear:
			this.finish();
			break;

		case R.id.week_one_rela:
			if(null!=week_one_rela.getTag()&&week_one_rela.getTag().toString().equals("true")){
				week_one_rela.setTag("false");// 选中的标记
				week_one_rela.setBackgroundResource(0);
			}else{
				week_one_rela.setTag("true");// 选中的标记
				week_one_rela.setBackgroundResource(R.color.week_color);
			}
			break;

		case R.id.week_two_rela:
			if(null!=week_two_rela.getTag()&&week_two_rela.getTag().toString().equals("true")){
				week_two_rela.setTag("false");// 选中的标记
				week_two_rela.setBackgroundResource(0);
			}else{
				week_two_rela.setTag("true");// 选中的标记
				week_two_rela.setBackgroundResource(R.color.week_color);
			}
			break;

		case R.id.week_three_rela:
			if(null!=week_three_rela.getTag()&&week_three_rela.getTag().toString().equals("true")){
				week_three_rela.setTag("false");// 选中的标记
				week_three_rela.setBackgroundResource(0);
			}else{
				week_three_rela.setTag("true");// 选中的标记
				week_three_rela.setBackgroundResource(R.color.week_color);
			}
			break;

		case R.id.week_four_rela:
			if(null!=week_four_rela.getTag()&&week_four_rela.getTag().toString().equals("true")){
				week_four_rela.setTag("false");// 选中的标记
				week_four_rela.setBackgroundResource(0);
			}else{
				week_four_rela.setTag("true");// 选中的标记
				week_four_rela.setBackgroundResource(R.color.week_color);
			}
			break;

		case R.id.week_five_rela:
			if(null!=week_five_rela.getTag()&&week_five_rela.getTag().toString().equals("true")){
				week_five_rela.setTag("false");// 选中的标记
				week_five_rela.setBackgroundResource(0);
			}else{
				week_five_rela.setTag("true");// 选中的标记
				week_five_rela.setBackgroundResource(R.color.week_color);
			}
			break;

		case R.id.week_six_rela:
			if(null!=week_six_rela.getTag()&&week_six_rela.getTag().toString().equals("true")){
				week_six_rela.setTag("false");// 选中的标记
				week_six_rela.setBackgroundResource(0);
			}else{
				week_six_rela.setTag("true");// 选中的标记
				week_six_rela.setBackgroundResource(R.color.week_color);
			}
			break;

		case R.id.week_day_rela:
			if(null!=week_day_rela.getTag()&&week_day_rela.getTag().toString().equals("true")){
				week_day_rela.setTag("false");// 选中的标记
				week_day_rela.setBackgroundResource(0);
			}else{
				week_day_rela.setTag("true");// 选中的标记
				week_day_rela.setBackgroundResource(R.color.week_color);
			}
			break;
		case R.id.week_ok:
			getData();
			break;
		}
	}
		private void getData() {
			weeks="0";
			if(null!=week_one_rela.getTag()&&week_one_rela.getTag().toString().equals("true")){
				weeks+=",1";
			}
			if(null!=week_two_rela.getTag()&&week_two_rela.getTag().toString().equals("true")){
				weeks+=",2";
			}
			if(null!=week_three_rela.getTag()&&week_three_rela.getTag().toString().equals("true")){
				weeks+=",3";
			}
			if(null!=week_four_rela.getTag()&&week_four_rela.getTag().toString().equals("true")){
				weeks+=",4";
			}
			if(null!=week_five_rela.getTag()&&week_five_rela.getTag().toString().equals("true")){
				weeks+=",5";
			}
			if(null!=week_six_rela.getTag()&&week_six_rela.getTag().toString().equals("true")){
				weeks+=",6";
			}
			if(null!=week_day_rela.getTag()&&week_day_rela.getTag().toString().equals("true")){
				weeks+=",7";
			}
			if(type==1){
				Intent intent=new Intent(WeekActivity.this, AddRepeatActivity.class);
				intent.putExtra("weeks", weeks);
				setResult(Activity.RESULT_OK, intent);
			}else if(type==2){
				Intent intent=new Intent(WeekActivity.this, AddNewFocusShareRepeatActivity.class);
				intent.putExtra("weeks", weeks);
				setResult(Activity.RESULT_OK, intent);
			}else{
				Intent intent=new Intent();
				intent.putExtra("weeks", weeks);
				setResult(Activity.RESULT_OK, intent);
			}
			
			this.finish();
		}
	
}
