package com.mission.schedule.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.RepeatBean;

public class RepeatDateUtils {

	public static RepeatBean saveCalendar(String datetime, int type,
			String repTypeParameter, String yeartype) {
		/**
		 * 根据重复类型不同的参数 每天 每周 - 1、2、3...7 每月 - 1、2、3...31 每年 -
		 * 01-01、01-02、01-03...12-31
		 */
		RepeatBean repeatBean = new RepeatBean();

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		try {
			if (type == 1) {// 每天生成
				Calendar ca = Calendar.getInstance();
				ca.add(Calendar.DATE, -1);
				// tbtwo.setcDate(sf.format(ca.getTime()));//前一天的数据

				Date vd = new Date();
				SimpleDateFormat vd1 = new SimpleDateFormat("HH:mm");
				SimpleDateFormat v = new SimpleDateFormat("HH:mm");

				if (vd1.parse(v.format(vd)).getTime() >= vd1.parse(datetime)
						.getTime()) {// 当前时间在原来时间之后
					Calendar cv = Calendar.getInstance();
					cv.add(Calendar.DATE, +1);

					// if(type==0){//第一条数据
					repeatBean.repNextCreatedTime = sf.format(cv.getTime())
							+ " " + datetime;
					repeatBean.repLastCreatedTime = sf.format(vd) + " "
							+ datetime;
					// }else{
					// tb.setcDate(DateUtilHelper.formatDate(new Date()));
					// }
				} else {
					Calendar cv = Calendar.getInstance();
					cv.add(Calendar.DATE, -1);
					repeatBean.repNextCreatedTime = sf.format(vd) + " "
							+ datetime;
					repeatBean.repLastCreatedTime = sf.format(cv.getTime())
							+ " " + datetime;
					// if(type==0){
					// tb.setcDate(DateUtilHelper.formatDate(new Date()));
					// }else{
					// tb.setcDate(sf.format(cv.getTime()));
					// }
				}

			} else if (type == 5) {
				Calendar time = Calendar.getInstance();
				int nowWek = time.get(time.DAY_OF_WEEK) - 1;// 当前星期数
				if (nowWek >= 1 && nowWek <= 5) {// 判断在工作日
					Date vd = new Date();
					SimpleDateFormat vd1 = new SimpleDateFormat("HH:mm");
					SimpleDateFormat v = new SimpleDateFormat("HH:mm");
					if (vd1.parse(v.format(vd)).getTime() >= vd1
							.parse(datetime).getTime()) {// 判读当前日期在原来日期之后
						Calendar cv = Calendar.getInstance();
						// if(type==0){//第一条数据
						if (nowWek == 5) {
							cv.add(Calendar.DATE, 3);
							// tb.setcDate(sf.format(cv.getTime()));
						} else {
							cv.add(Calendar.DATE, 1);
							// tb.setcDate(sf.format(cv.getTime()));
						}
						repeatBean.repLastCreatedTime = sf.format(vd) + " "
								+ datetime;
						repeatBean.repNextCreatedTime = sf.format(cv.getTime())
								+ " " + datetime;
						// }else{
						// tb.setcDate(DateUtilHelper.formatDate(new Date()));
						// }

					} else {
						Calendar cv = Calendar.getInstance();
						cv.add(Calendar.DATE, -1);
						// if(type==0){
						// tb.setcDate(DateUtilHelper.formatDate(new Date()));
						// }else{
						// tb.setcDate(sf.format(cv.getTime()));
						// }
						repeatBean.repLastCreatedTime = sf.format(cv.getTime())
								+ " " + datetime;
						repeatBean.repNextCreatedTime = sf.format(vd) + " "
								+ datetime;
					}
				} else {
					if (nowWek == 6) {// 星期六的时候
						Calendar c = Calendar.getInstance();
						Calendar c1 = Calendar.getInstance();
						// if(type==0){
						c.add(Calendar.DATE, 2);
						c1.add(Calendar.DATE, -1);
						repeatBean.repLastCreatedTime = sf.format(c1.getTime())
								+ " " + datetime;
						repeatBean.repNextCreatedTime = sf.format(c.getTime())
								+ " " + datetime;
						// tb.setcDate(sf.format(c.getTime()));
						// }else{
						// c.add(Calendar.DATE, -3);
						// tb.setcDate(sf.format(c.getTime()));
						// }
					} else {// 星期日
						Calendar c = Calendar.getInstance();
						Calendar c1 = Calendar.getInstance();
						// if(type==0){
						c.add(Calendar.DATE, 1);
						// tb.setcDate(sf.format(c.getTime()));
						// }else{
						c1.add(Calendar.DATE, -2);
						// tb.setcDate(sf.format(c.getTime()));
						// }
						repeatBean.repLastCreatedTime = sf.format(c1.getTime())
								+ " " + datetime;
						repeatBean.repNextCreatedTime = sf.format(c.getTime())
								+ " " + datetime;
					}
				}
			} else if (type == 2) {
				// 获取生成日期
				String wek = repTypeParameter;// ['1']
				if (wek.contains("星期")) {
					wek = wek.substring(2);
				} else {
					wek = wek.substring(0);
				}
				if ("一".equals(wek)) {
					wek = "1";
				} else if ("二".equals(wek)) {
					wek = "2";
				} else if ("三".equals(wek)) {
					wek = "3";
				} else if ("四".equals(wek)) {
					wek = "4";
				} else if ("五".equals(wek)) {
					wek = "5";
				} else if ("六".equals(wek)) {
					wek = "6";
				} else if ("日".equals(wek)) {
					wek = "7";
				} else {
					wek = wek;
				}
				// if(wek.contains("\\")){
				// wek = wek.split("\\")[0];
				// }
				// wek=wek.substring(wek.indexOf("[")+2,
				// wek.lastIndexOf("]")-1);//原来星期数
				int oldWek = Integer.parseInt(wek);
				Calendar time = Calendar.getInstance();
				Calendar time1 = Calendar.getInstance();

				int nowWek = time.get(time.DAY_OF_WEEK) - 1;// 当前星期数
				if (oldWek == 7) {// 判读是否星期天
					oldWek = 0;
				}
				// 判断日期大小 再判读时间
				if (oldWek == nowWek) {
					Date vd = new Date();
					SimpleDateFormat vd1 = new SimpleDateFormat("HH:mm");
					SimpleDateFormat v = new SimpleDateFormat("HH:mm");

					if (vd1.parse(v.format(vd)).getTime() >= vd1
							.parse(datetime).getTime()) {
						// if(type==0){//第一条数据
						time.add(Calendar.DATE, 7);
						repeatBean.repLastCreatedTime = sf.format(new Date())
								+ " " + datetime;
						repeatBean.repNextCreatedTime = sf.format(time
								.getTime()) + " " + datetime;
						// tb.setcDate(sf.format(time.getTime()));
						// }else{
						// tb.setcDate(sf.format(new Date()));
						// }
					} else {
						// if(type==0){//第一条数据
						// tb.setcDate(sf.format(new Date()));
						// }else{
						time.add(Calendar.DATE, -7);
						// tb.setcDate(sf.format(time.getTime()));
						// }
						repeatBean.repLastCreatedTime = sf.format(time
								.getTime()) + " " + datetime;
						repeatBean.repNextCreatedTime = sf.format(new Date())
								+ " " + datetime;
					}
				} else if (oldWek < nowWek) {
					Date vd = new Date();
					SimpleDateFormat vd1 = new SimpleDateFormat("yyyy-MM-dd");
					// 算出加入好多天
					// if(type==0){
					time.add(Calendar.DATE, 7 - (nowWek - oldWek));//
					// tb.setcDate(sf.format(time.getTime()));
					// }else{
					time1.add(Calendar.DATE, -(nowWek - oldWek));//
					// tb.setcDate(sf.format(time.getTime()));
					// }
					repeatBean.repLastCreatedTime = sf.format(time1.getTime())
							+ " " + datetime;
					repeatBean.repNextCreatedTime = sf.format(time.getTime())
							+ " " + datetime;
				} else {
					// 算出加入好多天
					// if(type==0){
					time.add(Calendar.DATE, -(nowWek - oldWek));//
					// tb.setcDate(sf.format(time.getTime()));
					// }else{
					time1.add(Calendar.DATE, -(nowWek - oldWek) - 7);//
					// tb.setcDate(sf.format(time.getTime()));
					// }
					repeatBean.repLastCreatedTime = sf.format(time1.getTime())
							+ " " + datetime;
					repeatBean.repNextCreatedTime = sf.format(time.getTime())
							+ " " + datetime;
				}
			} else if (type == 3) {
				String wek = repTypeParameter;// ['1']
				if (wek.contains("日")) {
					wek = wek.substring(0, wek.length() - 1);
				} else {
					wek = wek;
				}
				// wek=wek.substring(wek.indexOf("[")+2,
				// wek.lastIndexOf("]")-1);//原来星期数
				int oldTime = Integer.parseInt(wek);
				Calendar time = Calendar.getInstance();
				int year = time.get(Calendar.YEAR);// 获取当前年月判读是否是闰年
				int month = time.get(Calendar.MONTH) + 1;// 获取月份数
				int day = time.get(Calendar.DATE);// 当前天数
				int monthDay = time.getActualMaximum(Calendar.DAY_OF_MONTH);//
				int i = 0;
				while (true) {
					Calendar c = Calendar.getInstance();

					c.add(Calendar.MONTH, i);
					int newDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);// 下个月总天数
					if (oldTime < day) {// 当前天数大于原来时间

						if (oldTime <= newDay) {// 原来生成月份的天数与下个月比较看是否大于
							// if(type==0){
							Calendar cv = Calendar.getInstance();
							Calendar cv1 = Calendar.getInstance();
							cv.add(Calendar.MONTH, 1);
							cv.set(Calendar.DATE, oldTime);
							// tb.setcDate(sf.format(cv.getTime()));
							// break;
							// }else{

							cv1.set(Calendar.DATE, oldTime);
							repeatBean.repLastCreatedTime = sf.format(cv1
									.getTime()) + " " + datetime;
							repeatBean.repNextCreatedTime = sf.format(cv
									.getTime()) + " " + datetime;
							// tb.setcDate(sf.format(cv.getTime()));
							break;
							// }
						}
					} else if (oldTime > day) {
						if (oldTime <= newDay) {// 原来生成月份的天数与下个月比较看是否大于
							Calendar cv = Calendar.getInstance();
							Calendar cv1 = Calendar.getInstance();
							cv.add(Calendar.MONTH, i);
							cv.set(Calendar.DATE, oldTime);
							int j = -1;
							while (true) {
								cv1.add(Calendar.MONTH, j);
								int upOld = cv1
										.getActualMaximum(Calendar.DAY_OF_MONTH);
								if (upOld >= oldTime) {

									cv1.set(Calendar.DATE, oldTime);
									break;
								}
							}
							repeatBean.repLastCreatedTime = sf.format(cv1
									.getTime()) + " " + datetime;
							repeatBean.repNextCreatedTime = sf.format(cv
									.getTime()) + " " + datetime;
							break;
						}
					} else {
						Date vd = new Date();
						SimpleDateFormat vd1 = new SimpleDateFormat("HH:mm");
						SimpleDateFormat v = new SimpleDateFormat("HH:mm");
						if (vd1.parse(v.format(vd)).getTime() >= vd1.parse(
								datetime).getTime()) {// 当前时间在原来时间之后
							if (oldTime <= newDay) {// 原来生成月份的天数与下个月比较看是否大于
								Calendar cv = Calendar.getInstance();
								Calendar cv1 = Calendar.getInstance();
								// if(type==0){
								i++;
								cv.add(Calendar.MONTH, i);
								cv.set(Calendar.DATE, oldTime);
								// tb.setcDate(sf.format(cv.getTime()));
								// break;
								// }else{

								cv1.set(Calendar.DATE, oldTime);
								// tb.setcDate(sf.format(cv.getTime()));
								repeatBean.repLastCreatedTime = sf.format(cv1
										.getTime()) + " " + datetime;
								repeatBean.repNextCreatedTime = sf.format(cv
										.getTime()) + " " + datetime;
								break;
								// }
							}
						} else {
							if (oldTime <= newDay) {// 原来生成月份的天数与下个月比较看是否大于
								Calendar cv = Calendar.getInstance();
								Calendar cv1 = Calendar.getInstance();
								// if(type==0){
								cv.set(Calendar.DATE, oldTime);
								// tb.setcDate(sf.format(cv.getTime()));
								// break;
								// }else{
								int j = -1;
								while (true) {
									cv1.add(Calendar.MONTH, j);
									int upOld = cv1
											.getActualMaximum(Calendar.DAY_OF_MONTH);
									if (upOld >= oldTime) {
										cv1.set(Calendar.DATE, oldTime);
										// tb.setcDate(sf.format(cv.getTime()));
										// break;
									}
									// }
									break;
								}
								repeatBean.repLastCreatedTime = sf.format(cv1
										.getTime()) + " " + datetime;
								repeatBean.repNextCreatedTime = sf.format(cv
										.getTime()) + " " + datetime;
							}
						}

						break;
					}
					i++;
				}
			} else {
				String wek = repTypeParameter;
				// wek = wek.substring(wek.indexOf("[") + 2,
				// wek.lastIndexOf("-"));// 原来星期数
				if (yeartype.equals("0")) {
					wek = wek;
					int oldTime = Integer.parseInt(wek.split("-")[0]);
					// String wektwo = repTypeParameter;
					// String wekday = wektwo.substring(wektwo.indexOf("-") + 1,
					// wektwo.lastIndexOf("]") - 1);// 原来星期数
					int oldDay = Integer.parseInt(wek.split("-")[1]);
					Calendar time = Calendar.getInstance();
					int year = time.get(Calendar.YEAR);// 获取当前年月判读是否是闰年
					int mon = time.get(Calendar.MONTH) + 1;
					int d = time.get(Calendar.DATE);
					if (oldTime == 2 && oldDay == 29) {// 原来日期是2月份并且是闰年时
						int run = 0;
						int i = 0;
						while (true) {
							int ru = year;
							ru += i;
							if (ru % 4 == 0 && ru % 100 != 0 || ru % 400 == 0) {
								run = ru;
								break;
							}
							i++;
						}
						Calendar c = Calendar.getInstance();
						Calendar c1 = Calendar.getInstance();
						c.set(Calendar.MONTH, 1);
						c.set(Calendar.DATE, 29);
						c1.set(Calendar.MONTH, 1);
						c1.set(Calendar.DATE, 29);
						Date vd = new Date();
						SimpleDateFormat vd1 = new SimpleDateFormat("HH:mm");
						SimpleDateFormat v = new SimpleDateFormat("HH:mm");
						if (vd1.parse(v.format(vd)).getTime() >= vd1.parse(
								datetime).getTime()) {// 判读当前日期在原来日期之后
							// if (type == 0) {
							if (run == year) {// 当前时间是瑞年时
								SimpleDateFormat t = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm");
								if (t.parse(t.format(vd)).compareTo(
										t.parse(run + "-02-29 " + datetime)) > 0) {
									c.set(Calendar.YEAR, run + 4);
								} else {
									c.set(Calendar.YEAR, run);
								}
							} else {
								c.set(Calendar.YEAR, run);
							}
							c.set(Calendar.MONTH, 1);
							c.set(Calendar.DATE, 29);
							// tb.setcDate(sf.format(c.getTime()));
							// } else {
							// Calendar c=Calendar.getInstance();
							c1.set(Calendar.YEAR, run);
							if (run == year) {// 当前时间是瑞年时
								SimpleDateFormat t = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm");
								if (t.parse(t.format(vd)).compareTo(
										t.parse(run + "-02-29 " + datetime)) > 0) {
									c1.set(Calendar.YEAR, run);
								} else {
									c1.set(Calendar.YEAR, run - 4);
								}
							} else {
								c1.add(Calendar.YEAR, -4);
							}
							// tb.setcDate(sf.format(c.getTime()));
							// }
							repeatBean.repLastCreatedTime = sf.format(c1
									.getTime()) + " " + datetime;
							repeatBean.repNextCreatedTime = sf.format(c
									.getTime()) + " " + datetime;
						} else {
							// if (type == 0) {
							if (run == year) {
								SimpleDateFormat t = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm");
								if (t.parse(t.format(vd)).compareTo(
										t.parse(run + "-02-29 " + datetime)) > 0) {
									c.set(Calendar.YEAR, run + 4);
								} else {
									c.set(Calendar.YEAR, run);
								}
							} else {
								c.set(Calendar.YEAR, run);
							}
							// tb.setcDate(sf.format(c.getTime()));
							// } else {
							// Calendar c=Calendar.getInstance();
							if (run == year) {// 当前时间是瑞年时
								SimpleDateFormat t = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm");
								if (t.parse(t.format(vd)).compareTo(
										t.parse(run + "-02-29 " + datetime)) > 0) {
									c1.set(Calendar.YEAR, run);
								} else {
									c1.set(Calendar.YEAR, run - 4);
								}
							} else {
								c1.set(Calendar.YEAR, run);
							}

							c1.set(Calendar.MONTH, 1);
							c1.set(Calendar.DATE, 29);
							// tb.setcDate(sf.format(c.getTime()));
							// }
							repeatBean.repLastCreatedTime = sf.format(c1
									.getTime()) + " " + datetime;
							repeatBean.repNextCreatedTime = sf.format(c
									.getTime()) + " " + datetime;
						}
					} else {
						Date vd = new Date();
						SimpleDateFormat vd1 = new SimpleDateFormat("HH:mm");

						if (oldTime == mon) {// 当前天数与原来相同时
							if (oldDay == d) {
								SimpleDateFormat v = new SimpleDateFormat(
										"HH:mm");
								if (vd1.parse(v.format(vd)).after(
										vd1.parse(datetime))) {// 判读当前日期在原来日期之后
									Calendar cv = Calendar.getInstance();
									cv.add(Calendar.YEAR, 1);

									// if (type == 0) {// 第一条数据
									// tb.setcDate(sf.format(cv.getTime()));
									// } else {
									// tb.setcDate(DateUtilHelper.formatDate(new
									// Date()));
									// }
									repeatBean.repLastCreatedTime = DateUtilHelper
											.formatDate(new Date())
											+ " "
											+ datetime;
									repeatBean.repNextCreatedTime = sf
											.format(cv.getTime())
											+ " "
											+ datetime;
								} else {
									Calendar cv = Calendar.getInstance();
									cv.add(Calendar.YEAR, -1);
									// if (type == 0) {// 第一条数据
									// tb.setcDate(DateUtilHelper.formatDate(new
									// Date()));
									// } else {
									// tb.setcDate(sf.format(cv.getTime()));
									// }
									repeatBean.repLastCreatedTime = sf
											.format(cv.getTime())
											+ " "
											+ datetime;
									repeatBean.repNextCreatedTime = DateUtilHelper
											.formatDate(new Date())
											+ " "
											+ datetime;
								}
							} else if (oldDay > d) {
								Calendar c = Calendar.getInstance();
								Calendar c1 = Calendar.getInstance();
								// if (type == 0) {
								c.set(Calendar.MONTH, oldTime - 1);
								c.set(Calendar.DATE, oldDay);
								// tb.setcDate(DateUtilHelper.formatDate(c.getTime()));
								// } else {
								c1.set(Calendar.MONTH, oldTime - 1);
								c1.set(Calendar.DATE, oldDay);
								c1.add(Calendar.YEAR, -1);
								repeatBean.repLastCreatedTime = sf.format(c1
										.getTime()) + " " + datetime;
								repeatBean.repNextCreatedTime = sf.format(c
										.getTime()) + " " + datetime;
								// tb.setcDate(sf.format(c.getTime()));
								// }
							} else {
								Calendar c = Calendar.getInstance();
								Calendar c1 = Calendar.getInstance();
								// if (type == 0) {
								c.set(Calendar.MONTH, oldTime - 1);
								c.set(Calendar.DATE, oldDay);
								c.add(Calendar.YEAR, +1);
								// tb.setcDate(sf.format(c.getTime()));
								// } else {
								c1.set(Calendar.MONTH, oldTime - 1);
								c1.set(Calendar.DATE, oldDay);
								// tb.setcDate(DateUtilHelper.formatDate(c.getTime()));
								// }
								repeatBean.repLastCreatedTime = sf.format(c1
										.getTime()) + " " + datetime;
								repeatBean.repNextCreatedTime = sf.format(c
										.getTime()) + " " + datetime;
							}
						} else if (oldTime > mon) {// 生成月大于当前月
							Calendar c = Calendar.getInstance();
							Calendar c1 = Calendar.getInstance();
							// if (type == 0) {
							c.set(Calendar.MONTH, oldTime - 1);
							c.set(Calendar.DATE, oldDay);
							// tb.setcDate(DateUtilHelper.formatDate(c.getTime()));
							// } else {
							c1.set(Calendar.MONTH, oldTime - 1);
							c1.set(Calendar.DATE, oldDay);
							c1.add(Calendar.YEAR, -1);
							// tb.setcDate(sf.format(c.getTime()));
							// }
							repeatBean.repLastCreatedTime = sf.format(c1
									.getTime()) + " " + datetime;
							repeatBean.repNextCreatedTime = sf.format(c
									.getTime()) + " " + datetime;
						} else {
							Calendar c = Calendar.getInstance();
							Calendar c1 = Calendar.getInstance();
							// if (type == 0) {
							c.set(Calendar.MONTH, oldTime - 1);
							c.set(Calendar.DATE, oldDay);
							c.add(Calendar.YEAR, +1);
							// tb.setcDate(sf.format(c.getTime()));
							// } else {
							c1.set(Calendar.MONTH, oldTime - 1);
							c1.set(Calendar.DATE, oldDay);
							repeatBean.repLastCreatedTime = sf.format(c1
									.getTime()) + " " + datetime;
							repeatBean.repNextCreatedTime = sf.format(c
									.getTime()) + " " + datetime;
							// tb.setcDate(DateUtilHelper.formatDate(c.getTime()));
							// }
						}
					}
				} else {
					wek = wek.replace("1", "").replace("十一月", "冬月")
							.replace("十二月", "腊月");
					List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
					mList = App.getDBcApplication().queryNearLunartoSolarList(
							wek);
					if (mList != null && mList.size() >= 2) {
						List<Map<String, String>> yiqianList = new ArrayList<Map<String, String>>();
						List<Map<String, String>> yihouList = new ArrayList<Map<String, String>>();
						Map<String, String> todayMap = new HashMap<String, String>();
						String today = DateUtil.formatDate(new Date());
						for (Map<String, String> map : mList) {
							if (DateUtil.parseDate(today).getTime() > DateUtil
									.parseDate(map.get("calendar")).getTime()) {
								yiqianList.add(map);
							} else if (DateUtil.parseDate(today).getTime() == DateUtil
									.parseDate(map.get("calendar")).getTime()) {
								todayMap = map;
							} else {
								yihouList.add(map);
							}
						}
						if (yiqianList != null && yiqianList.size() > 0) {
							Collections.sort(yiqianList, new DateComparator());
						}
						if (yihouList != null && yihouList.size() > 0) {
							Collections.sort(yihouList, new DateComparator());
						}
						if (todayMap != null && todayMap.size() > 0) {
							if (DateUtil.parseDateTimeHm(
									DateUtil.formatDateTimeHm((new Date())))
									.getTime() >= DateUtil.parseDateTimeHm(
									datetime).getTime()) {
								repeatBean.repLastCreatedTime = todayMap
										.get("calendar") + " " + datetime;
								if(yihouList!=null&&yihouList.size()>0){
									repeatBean.repNextCreatedTime = yihouList
											.get(0).get("calendar")
											+ " "
											+ datetime;
								}else {
									repeatBean.repNextCreatedTime = DateUtil.formatDate(new Date())
											+ " "
											+ datetime;
								}
							} else {
								if(yiqianList!=null&&yiqianList.size()>0){
									repeatBean.repLastCreatedTime = yiqianList.get(
											yiqianList.size() - 1).get("calendar")
											+ " " + datetime;
								}else {
									repeatBean.repLastCreatedTime = DateUtil.formatDate(new Date())
											+ " " + datetime;
								}
								repeatBean.repNextCreatedTime = todayMap
										.get("calendar") + " " + datetime;
							}
						} else {
							if(yiqianList!=null&&yiqianList.size()>0){
								repeatBean.repLastCreatedTime = yiqianList.get(
										yiqianList.size() - 1).get("calendar")
										+ " " + datetime;
							}else{
								repeatBean.repLastCreatedTime =DateUtil.formatDate(new Date())
										+ " " + datetime;
							}
							if(yihouList!=null&&yihouList.size()>0){
								repeatBean.repNextCreatedTime = yihouList.get(0)
										.get("calendar") + " " + datetime;
							}else{
								repeatBean.repNextCreatedTime = DateUtil.formatDate(new Date()) + " " + datetime;
							}
						}
					} else {
						repeatBean.repLastCreatedTime = sf.format(new Date())
								+ " " + datetime;
						repeatBean.repNextCreatedTime = sf.format(new Date())
								+ " " + datetime;
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (repeatBean.repNextCreatedTime.length() == 16
				&& repeatBean.repLastCreatedTime.length() == 16) {
			return repeatBean;
		} else {
			try {
				repeatBean.repNextCreatedTime = sf1.format(sf1
						.parse(repeatBean.repNextCreatedTime));
				repeatBean.repLastCreatedTime = sf1.format(sf1
						.parse(repeatBean.repLastCreatedTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return repeatBean;
		}
	}

}
