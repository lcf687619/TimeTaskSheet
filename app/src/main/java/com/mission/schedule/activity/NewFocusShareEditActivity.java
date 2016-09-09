package com.mission.schedule.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.bean.RepeatBean;
import com.mission.schedule.constants.FristFragment;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.entity.CLFindScheduleTable;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.DateUtil;
import com.mission.schedule.utils.RepeatDateUtils;
import com.mission.schedule.utils.SharedPrefUtil;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewFocusShareEditActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.focusName_tv)
	private TextView focusName_tv;
	@ViewResId(id = R.id.top_ll_right_textview_text)
	private TextView top_ll_right_textview_text;
	@ViewResId(id = R.id.chongfu_tv)
	private TextView chongfu_tv;
	@ViewResId(id = R.id.richeng_tv)
	private TextView richeng_tv;
	@ViewResId(id = R.id.all_tv)
	private TextView all_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	static App app = null;
	public static NewMyFoundShouChangListBeen been = null;
	private Fragment[] mFragments = new Fragment[3];
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		top_ll_right_textview_text.setOnClickListener(this);
		top_ll_right.setOnClickListener(this);
		chongfu_tv.setOnClickListener(this);
		richeng_tv.setOnClickListener(this);
		all_tv.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_newfocusshareedit);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		app = App.getDBcApplication();
		been = (NewMyFoundShouChangListBeen) getIntent().getSerializableExtra(
				"bean");
		focusName_tv.setText(been.name);
		top_ll_right_textview_text.setText("设置");
		chongfu_tv.setTextColor(getResources().getColor(R.color.hintcolor));
		richeng_tv
				.setTextColor(getResources().getColor(R.color.mingtian_color));
		all_tv.setTextColor(getResources().getColor(R.color.hintcolor));

		updateShunyanData();
		CheckCreateRepeatSchData();
		setFragmentIndicator(1);
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
		case R.id.top_ll_right:
			intent = new Intent(this, SettingNewFocusShareActivity.class);
			intent.putExtra("bean", been);
			startActivityForResult(intent, 1000);
			break;
		case R.id.top_ll_right_textview_text:
			intent = new Intent(this, SettingNewFocusShareActivity.class);
			intent.putExtra("bean", been);
			startActivityForResult(intent, 1000);
			break;
		case R.id.chongfu_tv:
			setFragmentIndicator(0);
			break;
		case R.id.richeng_tv:
			setFragmentIndicator(1);
			break;
		case R.id.all_tv:
			setFragmentIndicator(2);
			break;
		default:
			break;
		}
	}

	private void setFragmentIndicator(int whichIsDefault) {
		if (whichIsDefault == 0) {
			EventBus.getDefault().post(new FristFragment("0"));
			chongfu_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			richeng_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			all_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			EventBus.getDefault().post(new FristFragment("0"));
		} else if (whichIsDefault == 1) {
			EventBus.getDefault().post(new FristFragment("1"));
			chongfu_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			richeng_tv.setTextColor(getResources().getColor(
					R.color.mingtian_color));
			all_tv.setTextColor(getResources().getColor(R.color.hintcolor));
		} else if (whichIsDefault == 2) {
			EventBus.getDefault().post(new FristFragment("2"));
			chongfu_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			richeng_tv.setTextColor(getResources().getColor(R.color.hintcolor));
			all_tv.setTextColor(getResources().getColor(R.color.mingtian_color));
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		mFragments[0] = fragmentManager
				.findFragmentById(R.id.fragment_RepeatFocus);
		mFragments[1] = fragmentManager
				.findFragmentById(R.id.fragment_RiChengFocus);
		mFragments[2] = fragmentManager
				.findFragmentById(R.id.fragment_AllFocus);
		fragmentManager.beginTransaction().hide(mFragments[0])
				.hide(mFragments[1]).hide(mFragments[2])
				.show(mFragments[whichIsDefault]).commit();
	}

	public static void updateShunyanData() {
		List<Map<String, String>> yiqianList = new ArrayList<Map<String, String>>();
		yiqianList.clear();
		try {
			yiqianList = app.QueryNewFocusData(7, been.id);
			if (yiqianList != null && yiqianList.size() > 0) {
				for (Map<String, String> map : yiqianList) {
					app.updateNewFocusShareDate(been.id, Integer.parseInt(map
							.get(CLFindScheduleTable.fstSchID)), DateUtil
							.formatDate(new Date()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			been = (NewMyFoundShouChangListBeen) data
					.getSerializableExtra("bean");
			focusName_tv.setText(been.name);
		}
	}

	/**
	 * 生成子记事对应的时间
	 */
	public static RepeatBean getNextChildTime(Map<String, String> bean) {
		RepeatBean repeatBean = null;
		if ("1".equals(bean.get(CLFindScheduleTable.fstRepType))) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.get(CLFindScheduleTable.fstTime), 1, "", "");
		} else if ("2".equals(bean.get(CLFindScheduleTable.fstRepType))) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.get(CLFindScheduleTable.fstTime),
					2,
					bean.get(CLFindScheduleTable.fstParameter).replace("[", "")
							.replace("]", "").replace("\n\"", "")
							.replace("\n", "").replace("\"", ""), "");
		} else if ("3".equals(bean.get(CLFindScheduleTable.fstRepType))) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.get(CLFindScheduleTable.fstTime),
					3,
					bean.get(CLFindScheduleTable.fstParameter).replace("[", "")
							.replace("]", "").replace("\n\"", "")
							.replace("\n", "").replace("\"", ""), "");
		} else if ("4".equals(bean.get(CLFindScheduleTable.fstRepType))) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.get(CLFindScheduleTable.fstTime),
					4,
					bean.get(CLFindScheduleTable.fstParameter).replace("[", "")
							.replace("]", "").replace("\n\"", "")
							.replace("\n", "").replace("\"", ""), "0");
		} else if ("6".equals(bean.get(CLFindScheduleTable.fstRepType))) {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.get(CLFindScheduleTable.fstTime),
					4,
					bean.get(CLFindScheduleTable.fstParameter).replace("[", "")
							.replace("]", "").replace("\n\"", "")
							.replace("\n", "").replace("\"", ""), "1");
		} else {
			repeatBean = RepeatDateUtils.saveCalendar(
					bean.get(CLFindScheduleTable.fstTime), 5, "", "");
		}
		return repeatBean;
	}

	/**
	 * 查询表中重复母记事生成对应的子记事
	 */
	public static void CheckCreateRepeatSchData() {
		List<Map<String, String>> replist = app.QueryNewFocusData(6, been.id);
		if (replist != null && replist.size() > 0) {
			for (Map<String, String> map : replist) {
				app.deleteNewFocusShareData(2, been.id, 0, Integer.parseInt(map
						.get(CLFindScheduleTable.fstRepeatId)), "");
				if (Integer.parseInt(map
						.get(CLFindScheduleTable.fstRepStateOne)) == 0
						&& Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 0) {
					if (DateUtil
							.formatDate(
									DateUtil.parseDateTime(map
											.get(CLFindScheduleTable.fstRepInitialCreatedTime)
											.replace("T", " "))).equals(
									DateUtil.formatDate(new Date()))) {

						if (DateUtil
								.parseDateTime(
										map.get(CLFindScheduleTable.fstRepInitialCreatedTime)
												.replace("T", " ")).after(
										DateUtil.parseDateTime(DateUtil
												.formatDateTime(new Date())))) {
							if ("0".equals(map
									.get(CLFindScheduleTable.fstRepInStable))) {
								CreateNextChildData(map);
							}
						} else if (DateUtil
								.parseDateTime(
										map.get(CLFindScheduleTable.fstRpNextCreatedTime)
												.replace("T", " ")).equals(
										DateUtil.parseDateTime(DateUtil
												.formatDateTime(new Date())))) {
							if ("0".equals(map
									.get(CLFindScheduleTable.fstRepInStable))) {
								CreateNextChildData(map);
							}
						} else {
							if ("0".equals(map
									.get(CLFindScheduleTable.fstRepInStable))) {
								CreateNextChildData(map);
							}
						}
					} else {
						if (DateUtil
								.parseDateTime(
										map.get(CLFindScheduleTable.fstRpNextCreatedTime)
												.replace("T", " ")).after(
										DateUtil.parseDateTime(DateUtil
												.formatDateTime(new Date())))) {
							if ("0".equals(map
									.get(CLFindScheduleTable.fstRepInStable))) {
								CreateNextChildData(map);
							}
						} else if (DateUtil
								.parseDateTime(
										map.get(CLFindScheduleTable.fstRpNextCreatedTime)
												.replace("T", " ")).equals(
										DateUtil.parseDateTime(DateUtil
												.formatDateTime(new Date())))) {
							if ("0".equals(map
									.get(CLFindScheduleTable.fstRepInStable))) {
								CreateNextChildData(map);
							}
						} else {
							if ("0".equals(map
									.get(CLFindScheduleTable.fstRepInStable))) {
								CreateNextChildData(map);
							}
						}
					}
				} else {
					if (!getNextChildTime(map).repLastCreatedTime.equals(map
							.get(CLFindScheduleTable.fstRepDateOne))
							&& !getNextChildTime(map).repLastCreatedTime
									.equals(map
											.get(CLFindScheduleTable.fstRepDateTwo))) {
						if (DateUtil
								.parseDateTime(
										getNextChildTime(map).repLastCreatedTime)
								.before(DateUtil.parseDateTime(map
										.get(CLFindScheduleTable.fstRepInitialCreatedTime)))) {
						} else {
						}
					} else if (getNextChildTime(map).repLastCreatedTime
							.equals(map.get(CLFindScheduleTable.fstRepDateOne))) {
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 1) {
						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 2) {
						}
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 3) {
						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 0) {
						}
					} else if (getNextChildTime(map).repLastCreatedTime
							.equals(map.get(CLFindScheduleTable.fstRepDateTwo))) {
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 1) {
						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 2) {
						}
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 3) {
						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 0) {
						}
					}
					if (!getNextChildTime(map).repNextCreatedTime.equals(map
							.get(CLFindScheduleTable.fstRepDateOne))
							&& !getNextChildTime(map).repNextCreatedTime
									.equals(map
											.get(CLFindScheduleTable.fstRepDateTwo))) {
						if (DateUtil
								.parseDateTime(
										getNextChildTime(map).repNextCreatedTime)
								.before(DateUtil.parseDateTime(map
										.get(CLFindScheduleTable.fstRepInitialCreatedTime)))) {
						} else {
							if ("0".equals(map
									.get(CLFindScheduleTable.fstRepStateTwo))) {
								CreateNextChildData(map);
							} else {
								CreateNextChildData(map);
							}
						}
					} else if (getNextChildTime(map).repNextCreatedTime
							.equals(map.get(CLFindScheduleTable.fstRepDateOne))) {
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 1) {
						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 2) {
						}
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 3) {
							CreateNextChildEndData(map);

						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateOne)) == 0) {
							CreateNextChildData(map);
						}
					} else if (getNextChildTime(map).repNextCreatedTime
							.equals(map.get(CLFindScheduleTable.fstRepDateTwo))) {
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 1) {
						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 2) {
						}
						if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 3) {
							CreateNextChildEndData(map);
						} else if (Integer.parseInt(map
								.get(CLFindScheduleTable.fstRepStateTwo)) == 0) {
							CreateNextChildData(map);
						}
					}
				}
			}
		}
	}

	/**
	 * 生成子记事
	 */
	public static void CreateNextChildData(Map<String, String> bean) {
		app.insertNewFocusData(
				Integer.parseInt(bean.get(CLFindScheduleTable.fstFID)), 0,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstBeforeTime)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsAlarm)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstDisplayTime)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstColorType)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsPostpone)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsImportant)),
				0,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstSourceType)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstRepeatId)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstOpenState)),
				1,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstRecommendId)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsRead)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstAID)), 0, 0,
				0, 0,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstPostState)),
				0, Integer.parseInt(bean.get(CLFindScheduleTable.fstAType)), 0,
				"", bean.get(CLFindScheduleTable.fstContent),
				getNextChildTime(bean).repNextCreatedTime.substring(0, 10),
				bean.get(CLFindScheduleTable.fstTime),
				bean.get(CLFindScheduleTable.fstRingCode),
				bean.get(CLFindScheduleTable.fstRingDesc),
				bean.get(CLFindScheduleTable.fstTags),
				bean.get(CLFindScheduleTable.fstSourceDesc),
				bean.get(CLFindScheduleTable.fstSourceDescSpare),
				getNextChildTime(bean).repNextCreatedTime, "",
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repLastCreatedTime, "", "", "",
				bean.get(CLFindScheduleTable.fstRecommendName),
				bean.get(CLFindScheduleTable.fstWebURL),
				bean.get(CLFindScheduleTable.fstImagePath),
				bean.get(CLFindScheduleTable.fstParReamrk),
				bean.get(CLFindScheduleTable.fstCreateTime),
				bean.get(CLFindScheduleTable.fstUpdateTime),
				bean.get(CLFindScheduleTable.fstReamrk1),
				bean.get(CLFindScheduleTable.fstReamrk2),
				bean.get(CLFindScheduleTable.fstReamrk3),
				bean.get(CLFindScheduleTable.fstReamrk4),
				bean.get(CLFindScheduleTable.fstReamrk5));
	}

	/**
	 * 生成结束子记事
	 */
	public static void CreateNextChildEndData(Map<String, String> bean) {
		app.insertNewFocusData(
				Integer.parseInt(bean.get(CLFindScheduleTable.fstFID)), 0,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstBeforeTime)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsAlarm)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstDisplayTime)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstColorType)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsPostpone)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsImportant)),
				1,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstSourceType)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstRepeatId)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstOpenState)),
				1,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstRecommendId)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstIsRead)),
				Integer.parseInt(bean.get(CLFindScheduleTable.fstAID)), 0, 0,
				0, 0,
				Integer.parseInt(bean.get(CLFindScheduleTable.fstPostState)),
				0, Integer.parseInt(bean.get(CLFindScheduleTable.fstAType)), 0,
				"", bean.get(CLFindScheduleTable.fstContent),
				getNextChildTime(bean).repNextCreatedTime.substring(0, 10),
				bean.get(CLFindScheduleTable.fstTime),
				bean.get(CLFindScheduleTable.fstRingCode),
				bean.get(CLFindScheduleTable.fstRingDesc),
				bean.get(CLFindScheduleTable.fstTags),
				bean.get(CLFindScheduleTable.fstSourceDesc),
				bean.get(CLFindScheduleTable.fstSourceDescSpare),
				getNextChildTime(bean).repNextCreatedTime, "",
				getNextChildTime(bean).repNextCreatedTime,
				getNextChildTime(bean).repLastCreatedTime, "", "", "",
				bean.get(CLFindScheduleTable.fstRecommendName),
				bean.get(CLFindScheduleTable.fstWebURL),
				bean.get(CLFindScheduleTable.fstImagePath),
				bean.get(CLFindScheduleTable.fstParReamrk),
				bean.get(CLFindScheduleTable.fstCreateTime),
				bean.get(CLFindScheduleTable.fstUpdateTime),
				bean.get(CLFindScheduleTable.fstReamrk1),
				bean.get(CLFindScheduleTable.fstReamrk2),
				bean.get(CLFindScheduleTable.fstReamrk3),
				bean.get(CLFindScheduleTable.fstReamrk4),
				bean.get(CLFindScheduleTable.fstReamrk5));
	}

}
