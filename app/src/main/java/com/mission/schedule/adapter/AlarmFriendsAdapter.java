//package com.mission.schedule.adapter;
//
//import java.util.List;
//import java.util.Map;
//
//import com.mission.schedule.circleview.CircularImage;
//import com.mission.schedule.entity.FMessages;
//import com.mission.schedule.utils.DateUtil;
//import com.mission.schedule.utils.FileUtils;
//import com.mission.schedule.utils.ParameterUtil;
//import com.mission.schedule.utils.Utils;
//import com.mission.schedule.R;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//public class AlarmFriendsAdapter extends BaseAdapter {
//
//	Context context;
//	List<Map<String, String>> mList;
//	String UserID;
//	String friendsheadimage;
//	String myimage;
//	private LayoutInflater mInflater;
//	int mScreenWidth;
//	int lineCount;
//
//	public AlarmFriendsAdapter(Context context,
//			List<Map<String, String>> mList, String UserID,
//			String friendsheadimage, String myimage, int mScreenWidth) {
//		this.context = context;
//		this.mList = mList;
//		this.UserID = UserID;
//		this.friendsheadimage = friendsheadimage;
//		this.myimage = myimage;
//		this.mScreenWidth = mScreenWidth;
//		mInflater = LayoutInflater.from(context);
//	}
//
//	@Override
//	public int getCount() {
//		return mList == null ? 0 : mList.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return mList.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@SuppressLint("NewApi")
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		convertView = null;
//		View view = convertView;
//		ViewHolder viewHolder = null;
//		Map<String, String> map = mList.get(position);
//		if (view == null) {
//			if (Integer.parseInt(UserID) != Integer.parseInt(map
//					.get(FMessages.fmSendID))) {
//				view = mInflater.inflate(R.layout.adapter_alarmfriends_left,
//						null);
//			} else {
//				view = mInflater.inflate(R.layout.adapter_alarmfriends_right,
//						null);
//			}
//			viewHolder = new ViewHolder(view);
//			view.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) view.getTag();
//		}
//		LinearLayout chat_ll = viewHolder.getChatLL();
//		CircularImage iv_userhead = viewHolder.getHeadImage();
//		TextView tv_sendtime = viewHolder.getSendTimeText();
//		final TextView tv_chatcontent = viewHolder.getChatContentText();
//		ImageView naozhong_iv = viewHolder.getNaoZhongImage();
//		TextView tixing_date_tv = viewHolder.getTiXingText();
//		final TextView tv_chatcontent1 = viewHolder.getChatContentText1();
//		String imageUrl = "";
//		String cdate = map.get(FMessages.fmDate);
//		String ctime = map.get(FMessages.fmTime);
//		int status = Integer.parseInt(map.get(FMessages.fmStatus));
//		// int status = 1;
//
//		if (!"".equals(friendsheadimage)) {
//			imageUrl = friendsheadimage.replace("\\", "");
//		}
//		String date = "";
//		if (status != 2) {
//			date = DateUtil.formatDate(DateUtil.parseDate(cdate)).substring(5,
//					10);
//		} else {
//
//		}
//		if (Integer.parseInt(UserID) != Integer.parseInt(map
//				.get(FMessages.fmSendID))) {
//			String imageurl = imageUrl + "&imageType=2&imageSizeType=3";
//			FileUtils.loadRoundHeadImg(context, ParameterUtil.userHeadImg,
//					iv_userhead, imageurl);
//			if (status == 1) {
//				naozhong_iv.setVisibility(View.VISIBLE);
//				tixing_date_tv.setVisibility(View.VISIBLE);
//				naozhong_iv.setImageResource(R.drawable.icon_naozhong1);
//				tixing_date_tv.setText(date + " " + ctime);
//				tixing_date_tv.setTextColor(context.getResources().getColor(
//						R.color.bg_alarm_tixing));
//				chat_ll.setBackgroundResource(R.drawable.icon_bg_tixing1);
//
//			} else if (status == 2) {
//				naozhong_iv.setVisibility(View.VISIBLE);
//				tixing_date_tv.setVisibility(View.VISIBLE);
//				naozhong_iv.setImageResource(R.drawable.icon_chongfu);
//				if ("1".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每天" + " " + ctime);
//				} else if ("2".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每周" + " " + ctime);
//				} else if ("3".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每月" + " " + ctime);
//				} else if ("4".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每年" + " " + ctime);
//				} else if ("5".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("工作日" + " " + ctime);
//				}
//				tixing_date_tv.setTextColor(context.getResources().getColor(
//						R.color.bg_alarm_tixing));
//				chat_ll.setBackgroundResource(R.drawable.icon_bg_tixing2);
//			} else {
//				naozhong_iv.setVisibility(View.GONE);
//				tixing_date_tv.setVisibility(View.GONE);
//				chat_ll.setBackgroundResource(R.drawable.icon_bg_liuyan1);
//			}
//		} else {
//			String imageurl = myimage + "&imageType=2&imageSizeType=3";
//			FileUtils.loadRoundHeadImg(context, ParameterUtil.userHeadImg,
//					iv_userhead, imageurl);
//			if (status == 1) {
//				naozhong_iv.setVisibility(View.VISIBLE);
//				tixing_date_tv.setVisibility(View.VISIBLE);
//				naozhong_iv.setImageResource(R.drawable.icon_naozhong11);
//				tixing_date_tv.setText(date + " " + ctime);
//				tixing_date_tv.setTextColor(context.getResources().getColor(
//						R.color.bg_alarm_tixingtime));
//				chat_ll.setBackgroundResource(R.drawable.icon_bg_tixing);
//			} else if (status == 2) {
//				naozhong_iv.setVisibility(View.VISIBLE);
//				tixing_date_tv.setVisibility(View.VISIBLE);
//				naozhong_iv.setImageResource(R.drawable.icon_chongfu2);
//				if ("1".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每天" + " " + ctime);
//				} else if ("2".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每周" + " " + ctime);
//				} else if ("3".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每月" + " " + ctime);
//				} else if ("4".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("每年" + " " + ctime);
//				} else if ("5".equals(map.get(FMessages.fmType))) {
//					tixing_date_tv.setText("工作日" + " " + ctime);
//				}
//				tixing_date_tv.setTextColor(context.getResources().getColor(
//						R.color.bg_alarm_tixing));
//				chat_ll.setBackgroundResource(R.drawable.icon_bg_tixing);
//			} else {
//				naozhong_iv.setVisibility(View.GONE);
//				tixing_date_tv.setVisibility(View.GONE);
//				chat_ll.setBackgroundResource(R.drawable.icon_bg_liuyan2);
//			}
//		}
//		String time = map.get(FMessages.fmCreateTime);
//		time = time.replace("T", " ");
//		tv_sendtime.setText(time.substring(0, 16));
//		tv_chatcontent1.setText(map.get(FMessages.fmContent));
//		tv_chatcontent.setText(map.get(FMessages.fmContent));
//		tv_chatcontent.setGravity(Gravity.CENTER_VERTICAL);
//
//		// tv_chatcontent.post(new Runnable() {
//		// @Override
//		// public void run() {
//		// lineCount = tv_chatcontent.getLineCount();
//		// }
//		// });
//
//		if (Integer.parseInt(UserID) != Integer.parseInt(map
//				.get(FMessages.fmSendID))) {
//			if (status == 1) {
//				tv_chatcontent1.setTextColor(context.getResources().getColor(
//						R.color.bg_red));
//				final LayoutParams p = tv_chatcontent1.getLayoutParams();
//				int index = 0;
//
//				p.width = (mScreenWidth / 5) * 4;
//				if (tv_chatcontent.getText().toString().length() >= 11) {
//					index = tv_chatcontent.getText().toString().length() / 9;
//					if (index >= 24) {
//						p.height = (index + 4) * Utils.dipTopx(context, 30);
//					} else {
//						p.height = (index) * Utils.dipTopx(context, 30);
//					}
//				} else {
//					p.height = Utils.dipTopx(context, 30);
//				}
//				// p.height = lineCount*70;
//				tv_chatcontent1.setLayoutParams(p);
//				tv_chatcontent.setTextColor(context.getResources().getColor(
//						R.color.white));
//			} else if (status == 2) {
//				tv_chatcontent1.setTextColor(context.getResources().getColor(
//						R.color.bg_green));
//				final LayoutParams p = tv_chatcontent1.getLayoutParams();
//				int index = 0;
//
//				p.width = (mScreenWidth / 5) * 4;
//				if (tv_chatcontent.getText().toString().length() >= 11) {
//					index = tv_chatcontent.getText().toString().length() / 9;
//					p.height = index * (Utils.dipTopx(context, 30));
//				} else {
//					p.height = Utils.dipTopx(context, 30);
//				}
//				// p.height = lineCount*70;
//				tv_chatcontent1.setLayoutParams(p);
//				tv_chatcontent.setTextColor(context.getResources().getColor(
//						R.color.white));
//			} else {
//				tv_chatcontent1.setTextColor(context.getResources().getColor(
//						R.color.white));
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.WRAP_CONTENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//				params.setMargins(Utils.dipTopx(context, 25),
//						Utils.dipTopx(context, 15), Utils.dipTopx(context, 20),
//						0);
//				tv_chatcontent.setLayoutParams(params);
//				if (tv_chatcontent.getText().length() <= 1) {
//					LayoutParams p = tv_chatcontent1.getLayoutParams();
//					p.width = tv_chatcontent.getText().toString().length()
//							* Utils.dipTopx(context, 20);
//					p.height = Utils.dipTopx(context, 15);
//					tv_chatcontent1.setLayoutParams(p);
//				} else if (tv_chatcontent.getText().length() >= 2
//						&& tv_chatcontent.getText().length() <= 4) {
//					LayoutParams p = tv_chatcontent1.getLayoutParams();
//					p.width = tv_chatcontent.getText().toString().length()
//							* Utils.dipTopx(context, 25);
//					p.height = Utils.dipTopx(context, 20);
//					tv_chatcontent1.setLayoutParams(p);
//				} else {
//					LayoutParams p = tv_chatcontent1.getLayoutParams();
//					int index = tv_chatcontent.getText().toString().length() / 11;
//					p.width = (mScreenWidth / 5) * 4;
//					if (index >= 3) {
//						p.height = index * Utils.dipTopx(context, 25);
//					} else {
//						if (index == 0) {
//							p.height = Utils.dipTopx(context, 25);
//						} else {
//							p.height = index * Utils.dipTopx(context, 40);
//						}
//					}
//					tv_chatcontent1.setLayoutParams(p);
//				}
//			}
//		} else {
//			final LayoutParams p = tv_chatcontent1.getLayoutParams();
//			if (status == 0) {
//				int index = tv_chatcontent.getText().toString().length() / 11;
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.WRAP_CONTENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//				params.setMargins(Utils.dipTopx(context, 20),
//						Utils.dipTopx(context, 20), Utils.dipTopx(context, 25),
//						0);
//				tv_chatcontent.setLayoutParams(params);
//				if (tv_chatcontent.getText().length() <= 2) {
//					p.width = Utils.dipTopx(context, 30);
//					p.height = Utils.dipTopx(context, 15);
//				} else if (tv_chatcontent1.getText().toString().length() >= 12) {
//					p.height = (index) * Utils.dipTopx(context, 30);
//				} else {
//					p.height = Utils.dipTopx(context, 15);
//				}
//				tv_chatcontent1.setLayoutParams(p);
//			} else if (status == 2) {
//				int index = tv_chatcontent.getText().toString().length() / 9;
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.WRAP_CONTENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//				params.setMargins(Utils.dipTopx(context, 25),
//						Utils.dipTopx(context, 15), Utils.dipTopx(context, 25),
//						Utils.dipTopx(context, 20));
//				tv_chatcontent.setLayoutParams(params);
//				p.width = (mScreenWidth / 5) * 4;
//				if (tv_chatcontent.getText().toString().length() >= 9) {
//					// if(index>=3){
//					p.height = (index) * Utils.dipTopx(context, 30);
//					// }else {
//					// p.height = (index)*Utils.dipTopx(context, 30);
//					// }
//				} else {
//					p.height = Utils.dipTopx(context, 25);
//				}
//				tv_chatcontent1.setLayoutParams(p);
//			} else {
//				int index = tv_chatcontent.getText().toString().length() / 9;
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.WRAP_CONTENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//				params.setMargins(Utils.dipTopx(context, 25),
//						Utils.dipTopx(context, 15), Utils.dipTopx(context, 25),
//						Utils.dipTopx(context, 20));
//				tv_chatcontent.setLayoutParams(params);
//				tv_chatcontent1.setLayoutParams(params);
//				p.width = (mScreenWidth / 5) * 4;
//				if (tv_chatcontent.getText().toString().length() >= 9) {
//					if (index >= 24) {
//						p.height = (index + 4) * Utils.dipTopx(context, 30);
//					} else {
//						p.height = (index) * Utils.dipTopx(context, 30);
//					}
//				} else {
//					p.height = Utils.dipTopx(context, 25);
//				}
//				tv_chatcontent1.setLayoutParams(p);
//			}
//		}
//		return view;
//	}
//
//	class ViewHolder {
//		private View view;
//		private CircularImage iv_userhead;
//		private TextView tv_sendtime;
//		private TextView tv_chatcontent;
//		private LinearLayout chat_ll;
//		private ImageView naozhong_iv;
//		private TextView tixing_date_tv;
//		private TextView tv_chatcontent1;
//
//		private ViewHolder(View view) {
//			this.view = view;
//		}
//
//		private CircularImage getHeadImage() {
//			if (iv_userhead == null) {
//				iv_userhead = (CircularImage) view
//						.findViewById(R.id.iv_userhead);
//			}
//			return iv_userhead;
//		}
//
//		private TextView getSendTimeText() {
//			if (tv_sendtime == null) {
//				tv_sendtime = (TextView) view.findViewById(R.id.tv_sendtime);
//			}
//			return tv_sendtime;
//		}
//
//		private TextView getChatContentText() {
//			if (tv_chatcontent == null) {
//				tv_chatcontent = (TextView) view
//						.findViewById(R.id.tv_chatcontent);
//			}
//			return tv_chatcontent;
//		}
//
//		private LinearLayout getChatLL() {
//			if (chat_ll == null) {
//				chat_ll = (LinearLayout) view.findViewById(R.id.chat_ll);
//			}
//			return chat_ll;
//		}
//
//		private ImageView getNaoZhongImage() {
//			if (naozhong_iv == null) {
//				naozhong_iv = (ImageView) view.findViewById(R.id.naozhong_iv);
//			}
//			return naozhong_iv;
//		}
//
//		private TextView getTiXingText() {
//			if (tixing_date_tv == null) {
//				tixing_date_tv = (TextView) view
//						.findViewById(R.id.tixing_date_tv);
//			}
//			return tixing_date_tv;
//		}
//
//		private TextView getChatContentText1() {
//			if (tv_chatcontent1 == null) {
//				tv_chatcontent1 = (TextView) view
//						.findViewById(R.id.tv_chatcontent1);
//			}
//			return tv_chatcontent1;
//		}
//	}
//
//}
