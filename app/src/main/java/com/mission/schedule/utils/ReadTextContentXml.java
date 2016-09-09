package com.mission.schedule.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mission.schedule.R;
import com.mission.schedule.applcation.App;

import android.content.Context;

public class ReadTextContentXml {

	/**
	 * 微信语音识别主逻辑
	 * 
	 * @author WW
	 * @date 2015-8-17
	 * 
	 */
	public static class ReadWeiXinXml {

		static String[] han = { "零", "一", "二", "三", "四", "五", "六", "七", "八",
				"九", "两", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八",
				"十九", "二十", "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七",
				"二十八", "二十九", "三十", "三十一" };
		static int[] num = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 10, 11, 12, 13,
				14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
				30, 31 };
		static String[] regex = { "\\d{1,}天后", "\\d{1,}天以后", "\\d{1,}周后",
				"\\d{1,}个周后", "\\d{1,}星期后", "\\d{1,}个星期后", "\\d{1,}周以后",
				"\\d{1,}个周以后", "\\d{1,}星期以后", "\\d{1,}个星期以后", "\\d{1,}月后",
				"\\d{1,}个月后", "\\d{1,}月以后", "\\d{1,}个月以后", "\\d{1,}年后",
				"\\d{1,}年以后" };
		static int regexType[] = { 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4,
				4 };
		static String[] regexTwo = { "\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}",
				"\\d{4}(\\.)\\d{1,2}\\1\\d{1,2}",
				"\\d{4}(\\/)\\d{1,2}\\1\\d{1,2}", "\\d{1,2}(\\-)\\d{1,2}",
				"\\d{1,2}(\\.)\\d{1,2}", "\\d{1,2}(\\/)\\d{1,2}",
				"\\d{4}(\\-)\\d{1,2}", "\\d{4}(\\.)\\d{1,2}",
				"\\d{4}(\\/)\\d{1,2}", "\\d{4}年\\d{1,2}月\\d{1,2}日",
				"\\d{4}年\\d{1,2}月\\d{1,2}号", "\\d{4}年\\d{1,2}月\\d{1,2}",
				"\\d{4}年\\d{1,2}月", "\\d{1,2}月\\d{1,2}日", "\\d{1,2}月\\d{1,2}号",
				"\\d{1,2}月\\d{1,2}", "\\d{1,2}日", "\\d{1,2}号", "\\d{1,2}月",
				"\\d{4}年", "\\d{1,2}月初", "\\d{1,2}月上旬", "\\d{1,2}月中旬",
				"\\d{1,2}月下旬", "\\d{1,2}月末", "下月\\d{1,2}日", "下月\\d{1,2}号",
				"下月\\d{1,2}", "下个月\\d{1,2}日", "下个月\\d{1,2}号", "下个月\\d{1,2}",
				"\\d{4}年\\d{1,2}月初\\d", "\\d{4}年\\d{1,2}月初10", "\\d{1,2}月初\\d",
				"\\d{1,2}月初10", "初\\d", "初10", "正月初\\d", "正月初10", "冬月初\\d",
				"冬月初10", "腊月初\\d", "腊月初10", "正月\\d{1,2}", "冬月\\d{1,2}",
				"腊月\\d{1,2}" };
		static int regexTypeTwo[] = { 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 6,
				6, 6, 7, 7, 8, 9, 10, 10, 11, 12, 13, 14, 14, 14, 14, 14, 14,
				15, 15, 16, 16, 17, 17, 18, 18, 18, 18, 18, 18, 19, 19, 19 };

		static String regexTime[] = { "\\d{1,2}点", "\\d{1,2}时", "\\d{1,2}点一刻",
				"\\d{1,2}时一刻", "\\d{1,2}点半", "\\d{1,2}点\\d{1,2}",
				"\\d{1,2}时\\d{1,2}", "([0-1]?[0-9]|2[0-3]):([0-5][0-9])",
				"([0-1]?[0-9]|2[0-3])：([0-5][0-9])",
				"([0-1]?[0-9]|2[0-3]):([0-5][0-9])pm",
				"([0-1]?[0-9]|2[0-3]):([0-5][0-9]) pm",
				"([0-1]?[0-9]|2[0-3]):([0-5][0-9])PM",
				"([0-1]?[0-9]|2[0-3]):([0-5][0-9]) PM", "\\d{1,}分钟后",
				"\\d{1,}分钟以后", "\\d{1,}刻钟后", "\\d{1,}刻钟以后", "\\d{1,}小时后",
				"\\d{1,}个小时后", "\\d{1,}小时以后", "\\d{1,}个小时以后" };

		static int regexTimeType[] = { 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 9, 9, 9,
				10, 10, 11, 11, 12, 12, 12, 12 };

		// public static void mainReadText(String args) {
		// /**
		// * 语音逻辑
		// * 1:首先读取文字中的铃声
		// * 2:转换文字
		// * 3:获取xml 中与之对应的日期 在正则表达式中判读日期 如果XML 中有 取之 没有取 正则表达式中的
		// * 4:获取XMl 中的时间 优先 XML 文件
		// */
		// ReadWeiXinXml r=new ReadWeiXinXml();//TimeRecognizer.xml
		// /*System.out.println(r.readTimeOrDateXMl("src/com/timetable/weixin/xml/DateRecognizer.xml",
		// "下个礼拜天".trim()));
		// //获取铃声
		// Map<Object,Object>
		// ma=r.readDescXML("src/com/timetable/weixin/xml/RingRecognizer.xml",
		// "回电话ok".trim());
		//
		// System.out.println(r.readDescXML("src/com/timetable/weixin/xml/BeforeRings.xml",
		// (ma.get("desc")+"").trim()));
		// //获取日期
		// r.backVal("红灯笼福建省一手房江苏两地警方吃饭，而斯蒂九");
		// System.out.println(r.readTimeOrDateXMl("src/com/timetable/weixin/xml/DateRecognizer.xml",
		// "下个礼拜天".trim()));
		// Map<Object,Object> m=r.JxRegexDateOne("12天后");
		// System.out.println("  kkk "+
		// r.backRegexTypeDateOne(Integer.parseInt(m.get("type")+""),
		// Integer.parseInt(m.get("day")+"")));
		// //huo
		// Map<Object,Object> m2=r.JxRegexDateTwo("2013/09aaa");
		// r.backRegexTypeDateTwo(Integer.parseInt(m2.get("typeTwo")+""),
		// m2.get("dayTwo")+"");
		// //获取Time
		// Map mm=
		// r.readTimeOrDateXMl("src/com/timetable/weixin/xml/TimeRecognizer.xml","aa".trim());
		// System.out.println(mm);
		// System.out.println(r.JxRegexTime("9点"));
		// if(!mm.get("value").equals("")){
		//
		// }else{
		//
		// }*/
		// //System.out.println(r.yuyinSb("后天早上办事"));
		//
		// String val="今天晚上开会";
		// //System.out.println(MD5Util.encode("123456"));
		// Map<Object, Object> map=new HashMap<Object, Object>();
		// try {
		// Map<Object,Object> uu=r.readDescXML("/assets/RingRecognizer.xml",
		// val.trim());
		// System.out.println(" uu"+ uu);
		//
		// //获取铃声Desc
		// Map<Object,Object> mDesc=r.readDescXML("/assets/RingRecognizer.xml",
		// val.trim());
		// System.out.println("mDesc  ==="+mDesc);
		// Map<Object,Object>
		// mLocalBeforeRings=r.readDescXML("/assets/BeforeRings.xml",
		// (mDesc.get("desc")+"").trim());
		// System.out.println(mLocalBeforeRings);
		// if(mLocalBeforeRings.size()>0&&null!=mLocalBeforeRings){
		// map.put("ringDesc", mLocalBeforeRings.get("desc"));
		// map.put("ringVal",mDesc.get("desc")+"");
		// }else{
		// map.put("ringDesc", "g_88");
		// map.put("ringVal","完成任务");
		// }
		// System.out.println(map);
		// //获取日期Date
		// String valChange=r.backVal(val);
		//
		// Map mDate=r.readTimeOrDateXMl("/assets/DateRecognizer.xml",
		// valChange.trim());
		//
		// Map mDateMess=null;
		//
		// if(mDate!=null&&mDate.size()>0){
		// int v=Integer.parseInt(mDate.get("value")+"");
		// //处理关键字
		// if(!(Integer.parseInt(mDate.get("type")+"")==4&&v==1)){
		// valChange=mDate.get("content")+"";
		//
		// }else{
		//
		// }
		// mDateMess=r.backXmlTypeDate(Integer.parseInt(mDate.get("type")+""),Integer.parseInt(mDate.get("value")+""));
		//
		// }
		// //获取文本
		//
		// if(null!=mDateMess&&mDateMess.size()>0){//判读读取Xml 中是否为空
		//
		// map.put("date", mDateMess.get("date"));
		// }else{
		//
		// Map mOne=r.JxRegexDateOne(valChange);//读取正则表达式规则1
		// Map mTwo=r.JxRegexDateTwo(valChange);//读取正则表达式规则2
		// if(null!=mOne&&mOne.size()>0){
		// Map
		// mOneMess=r.backRegexTypeDateOne(Integer.parseInt(mOne.get("type")+""),Integer.parseInt(mOne.get("day")+""));
		// map.put("date", mOneMess.get("date"));
		// valChange=mOne.get("content")+"";
		// }else if(null!=mTwo&&mTwo.size()>0){
		// Map
		// mTwoMess=r.backRegexTypeDateTwo(Integer.parseInt(mTwo.get("typeTwo")+""),mTwo.get("dayTwo")+"");
		// map.put("date", mTwoMess.get("date"));
		// valChange=mTwo.get("content")+"";
		// }
		// }
		// //获取时间Time
		// Map mTime=r.readTimeOrDateXMl("/assets/TimeRecognizer.xml",
		// valChange.trim());
		//
		// Map m=r.JxRegexTime(valChange.trim());
		// Map mtimeMess=null;
		// if(m!=null&&m.size()>0){
		// mtimeMess=r.backRegexTime(Integer.parseInt(m.get("type")+""),m.get("time")+"");
		// }
		// Map btime=null;
		// if(mTime!=null&&mTime.size()>0){
		//
		// btime=r.backTime(Integer.parseInt(mTime.get("type")+""),mTime.get("value")+"");
		// }
		//
		// if((mtimeMess!=null&&mtimeMess.size()>0)&&(mTime!=null&&mTime.size()>0)){
		// map.put("time", btime.get("time"));
		// valChange=mTime.get("content")+"";
		//
		// }else{
		//
		// if(mTime!=null&&mTime.size()>0){
		// map.put("time", btime.get("time"));
		// valChange=mTime.get("content")+"";
		//
		// }else if (m!=null&&m.size()>0){
		//
		// map.put("time", mtimeMess.get("time"));
		// valChange=m.get("content")+"";
		// }
		//
		// }
		// boolean bjDate=false;
		// boolean bjTime=false;
		// SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd");
		// SimpleDateFormat sh=new SimpleDateFormat("HH:mm");
		// map.put("C_POSTPONE", 0);
		// map.put("C_DISPLAY_ALARM",0);
		// if(!"".equals(map.get("date"))&&null!=map.get("date")){
		// map.put("date",f.format(f.parse(map.get("date")+"")));
		// map.put("di", "1");
		// }else{
		// bjDate=true;
		// map.put("date",f.format(new Date()));
		// map.put("di", "0");
		// }
		//
		// if(!"".equals(map.get("time"))&&null!=map.get("time")){
		// bjTime=true;
		// map.put("time", sh.format(sh.parse(map.get("time")+"")));
		// map.put("ti", "1");
		// }else{
		// map.put("ti", "0");
		// if(bjDate){
		// map.put("time",sh.format(new Date()));
		// map.put("C_POSTPONE", 1);
		// map.put("C_DISPLAY_ALARM",1);
		// }else{
		// bjTime=true;
		// map.put("time", "09:32");
		// }
		// }
		// Calendar c=Calendar.getInstance();
		// SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// if(bjTime){
		// if(sf.parse(map.get("date")+" "+map.get("time")+"").compareTo(sf.parse(sf.format(c.getTime())))<=0){
		// c.add(c.DATE, 1);
		// map.put("date", f.format(c.getTime()));
		// }
		// }
		//
		// //内容
		// map.put("value", valChange);
		// System.out.println("map :"+map);
		// }catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		/**
		 * 语音识别主逻辑
		 * 
		 * @param val
		 * @return
		 */
		public static Map<Object, Object> yuyinSb(Context context, String val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			int yeartype = 0;// 0为阳历 1为农历
			try {
				if (val.length() >= 2 && "农历".equals(val.substring(0, 2))) {
					yeartype = 1;
					val = val.substring(0, 2).replace("农历", "")
							+ val.substring(2);
				} else {
					yeartype = 0;
					val = val.trim();
				}
				map.put("da", "1");// 全天顺严状态
				map.put("ti", "1");// 全天顺严状态
				// 获取铃声Desc
				Map<Object, Object> mDesc = readDescXML(context,
						"RingRecognizer.xml", val.trim());
				Map<Object, Object> mLocalBeforeRings = readDescXML(context,
						"BeforeRings.xml", (mDesc.get("desc") + "").trim());

				if (mLocalBeforeRings.size() > 0 && null != mLocalBeforeRings) {
					map.put("ringDesc", mLocalBeforeRings.get("desc"));
					map.put("ringVal", mDesc.get("desc") + "");
				} else {
					map.put("ringDesc", "g_88");
					map.put("ringVal", "完成任务");
				}
				// 获取日期Date
				String valChange = backVal(val);

				Map mDate = readTimeOrDateXMl(context, "DateRecognizer.xml",
						valChange.trim());

				Map mDateMess = null;

				if (mDate != null && mDate.size() > 0) {
					int v = Integer.parseInt(mDate.get("value") + "");
					// 处理关键字
					if (!(Integer.parseInt(mDate.get("type") + "") == 4 && v == 1)) {
						valChange = mDate.get("content") + "";

					} else {

					}
					mDateMess = backXmlTypeDate(
							Integer.parseInt(mDate.get("type") + ""),
							Integer.parseInt(mDate.get("value") + ""));

				}

				// 获取文本

				if (null != mDateMess && mDateMess.size() > 0) {// 判读读取Xml 中是否为空

					map.put("date", mDateMess.get("date"));
				} else {

					Map mOne = JxRegexDateOne(valChange);// 读取正则表达式规则1
					Map mTwo = JxRegexDateTwo(valChange);// 读取正则表达式规则2
					if (null != mOne && mOne.size() > 0) {
						Map mOneMess = backRegexTypeDateOne(
								Integer.parseInt(mOne.get("type") + ""),
								Integer.parseInt(mOne.get("day") + ""));
						map.put("date", mOneMess.get("date"));
						valChange = mOne.get("content") + "";
					} else if (null != mTwo && mTwo.size() > 0) {
						Map mTwoMess = backRegexTypeDateTwo(yeartype, context,
								Integer.parseInt(mTwo.get("typeTwo") + ""),
								mTwo.get("dayTwo") + "", 0);
						map.put("date", mTwoMess.get("date"));
						valChange = mTwo.get("content") + "";
					}
				}

				// 获取时间Time
				Map mTime = readTimeOrDateXMl(context, "TimeRecognizer.xml",
						valChange.trim());

				Map m = JxRegexTime(valChange.trim());
				Map mtimeMess = null;
				if (m != null && m.size() > 0) {
					mtimeMess = backRegexTime(
							Integer.parseInt(m.get("type") + ""), m.get("time")
									+ "");
				}
				Map btime = null;
				int qh = -1;// 判读文件 下午 两点 吃饭 问题格式为这种时 qh=0
				if (mTime != null && mTime.size() > 0) {
					qh = 0;
					btime = backTime(Integer.parseInt(mTime.get("type") + ""),
							mTime.get("value") + "");
				}

				if ((mtimeMess != null && mtimeMess.size() > 0)
						&& (mTime != null && mTime.size() > 0)) {
					if (qh == 0) {
						int zd = 0;
						String sj = (mtimeMess.get("time") + "");
						if (Integer.parseInt(mTime.get("type") + "") == 2) {
							zd = Integer.parseInt(sj.substring(0,
									sj.indexOf(":")));
							if (zd < 12 && zd >= 1)
								zd = 12 + zd;

						} else {
							zd = Integer.parseInt(sj.substring(0,
									sj.indexOf(":")));
						}
						map.put("time", zd + "" + sj.substring(sj.indexOf(":")));
						valChange = (m.get("content") + "").replace("上午", "")
								.replace("下午", "").replace("早上", "")
								.replace("晚上", "");
					} else {
						map.put("time", btime.get("time"));
						valChange = mTime.get("content") + "";
					}

				} else {

					if (mTime != null && mTime.size() > 0) {
						map.put("time", btime.get("time"));
						valChange = mTime.get("content") + "";

					} else if (m != null && m.size() > 0) {

						map.put("time", mtimeMess.get("time"));
						valChange = m.get("content") + "";
					}

				}
				boolean bjDate = false;
				boolean bjTime = false;
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sh = new SimpleDateFormat("HH:mm");
				map.put("C_POSTPONE", 0);
				map.put("C_DISPLAY_ALARM", 0);
				if (!"".equals(map.get("date")) && null != map.get("date")) {
					map.put("date", f.format(f.parse(map.get("date") + "")));

				} else {
					bjDate = true;
					map.put("date", f.format(new Date()));
					map.put("di", "0");
				}

				if (!"".equals(map.get("time")) && null != map.get("time")) {
					bjTime = true;
					map.put("time", sh.format(sh.parse(map.get("time") + "")));
				} else {
					map.put("ti", "0");
					if (bjDate) {
						map.put("time", sh.format(new Date()));
						map.put("C_POSTPONE", 1);
						map.put("C_DISPLAY_ALARM", 1);
					} else {
						bjTime = true;
						map.put("time", "09:32");
					}
				}
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				if (bjTime) {
					if (sf.parse(map.get("date") + " " + map.get("time") + "")
							.compareTo(sf.parse(sf.format(c.getTime()))) <= 0) {
						c.add(c.DATE, 1);
						map.put("date", f.format(c.getTime()));
					}
				}

				// 内容
				map.put("value", valChange);
				return map;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 解析时间Time 规则
		 * 
		 * @return
		 */
		public static Map<Object, Object> backRegexTime(int type, String val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			try {
				if (type == 1) {

					val = val.replace("点", "");
					map.put("time", val + ":00");
				} else if (type == 2) {
					val = val.replace("时", "");
					map.put("time", val + ":00");
				} else if (type == 3) {
					val = val.replace("点一刻", "");
					map.put("time", val + ":15");
				} else if (type == 4) {
					val = val.replace("时一刻", "");
					map.put("time", val + ":15");
				} else if (type == 5) {
					val = val.replace("点半", "");
					map.put("time", val + ":30");
				} else if (type == 6) {
					val = val.replace("点", ":");
					map.put("time", val + "");
				} else if (type == 7) {
					val = val.replace("时", ":");
					map.put("time", val + "");
				} else if (type == 8) {
					map.put("time", val + "");
				} else if (type == 9) {
					val = val.replace("pm", "");
					val = val.replace("PM", "");
					map.put("time", val + "");
				} else if (type == 10) {
					val = val.replace("分钟后", "");
					val = val.replace("分钟以后", "");
					int m = Integer.parseInt(val);
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
					c.add(c.MINUTE, m);
					map.put("time", sf.format(c.getTime()));
				} else if (type == 11) {
					val = val.replace("刻钟后", "");
					val = val.replace("刻钟以后", "");
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
					int m = Integer.parseInt(val);
					c.add(c.MINUTE, m * 15);
					map.put("time", sf.format(c.getTime()));
				} else if (type == 12) {
					val = val.replace("小时后", "");
					val = val.replace("个小时后", "");
					val = val.replace("小时以后", "");
					val = val.replace("个小时以后", "");
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
					int m = Integer.parseInt(val);
					c.add(c.HOUR, m);
					map.put("time", sf.format(c.getTime()));
				}

				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * xml 时间规则
		 * 
		 * @param type
		 * @param val
		 * @return
		 */
		public static Map<Object, Object> backTime(int type, String val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			Calendar c = Calendar.getInstance();

			SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
			if (type == 3) {
				map.put("time", sf.format(c.getTime()));
			} else {
				map.put("time", val);
			}
			return map;
		}

		/**
		 * 解析时间Time 正则表达式
		 */
		public static Map<Object, Object> JxRegexTime(String val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			try {
				for (int i = 0; i < regexTime.length; i++) {
					Pattern m = Pattern.compile(regexTime[i]);
					Matcher matcher = m.matcher(val);

					while (matcher.find()) {
						if (matcher.group() != null
								&& !"".equals(matcher.group())) {

							map.put("type", regexTimeType[i] + "");
							map.put("time", matcher.group());
							map.put("content", val.replace(matcher.group(), ""));

						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}

		/**
		 * 解析日期正则表达式
		 * 
		 * @return map dayTwo typeTwo
		 */
		public static Map<Object, Object> JxRegexDateTwo(String val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			int num1 = 0, num2 = 0, len = 0;
			;
			for (int i = 0; i < regexTwo.length; i++) {
				Pattern m = Pattern.compile(regexTwo[i]);
				Matcher matcher = m.matcher(val);
				while (matcher.find()) {
					if (matcher.group() != null && !"".equals(matcher.group())) {
						System.out.println(matcher.group() + "==="
								+ matcher.group().length());
						num1 = matcher.group().length();
						break;
					}
				}
				if (num1 > num2) {
					num2 = num1;
					len = i;
				}
				if (regexTwo.length - 1 == i) {
					Pattern mm = Pattern.compile(regexTwo[len]);
					Matcher matcher1 = mm.matcher(val);
					while (matcher1.find()) {
						map.put("dayTwo", matcher1.group());
						map.put("typeTwo", regexTypeTwo[len] + "");
						map.put("content", val.replace(matcher1.group(), ""));
					}
				}
			}

			return map;
		}

		/**
		 * 解析日期正则表达式
		 * 
		 * @return
		 */
		public static Map<Object, Object> JxRegexDateOne(String val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			for (int i = 0; i < regex.length; i++) {
				Pattern m = Pattern.compile(regex[i]);
				Matcher matcher = m.matcher(val);
				while (matcher.find()) {
					if (matcher.group() != null && !"".equals(matcher.group())) {
						Pattern m1 = Pattern.compile("\\d{1,}");
						Matcher matcher1 = m1.matcher(val);
						while (matcher1.find()) {
							if (matcher1.group() != null
									&& !"".equals(matcher1.group())) {
								map.put("day", matcher1.group());
								map.put("type", regexType[i] + "");
								map.put("content",
										val.replace(matcher.group(), ""));
								break;
							}
						}
					}
				}

			}
			return map;
		}

		/**
		 * 语音识别正则表达式Type 日期转换
		 * 
		 * @return
		 */
		public static Map<Object, Object> backRegexTypeDateTwo(int yeartype,
				Context context, int type, String val, int isRepeat) {// isRepeat
																		// 0日程
																		// 1重复
			Map<Object, Object> map = new HashMap<Object, Object>();
			String[] yinlimonths = context.getResources().getStringArray(
					R.array.monthStr);
			String[] yinlidays = context.getResources().getStringArray(
					R.array.lunarstr);
			String[] yanglimonths = context.getResources().getStringArray(
					R.array.months);
			String[] yanglidays = context.getResources().getStringArray(
					R.array.lunarday);
			String[] nums = context.getResources().getStringArray(
					R.array.number);
			try {
				Calendar c = Calendar.getInstance();
				SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat f2 = new SimpleDateFormat("MM-dd");
				SimpleDateFormat f3 = new SimpleDateFormat("yyyy-MM");
				val = val.replace(".", "-");
				val = val.replace("/", "-");

				val = val.replace("号", "");
				if (type == 1) {// 年月日

					map.put("date", f1.format(f1.parse(val)));
				} else if (type == 2) {// 月日
					int mon = c.get(c.MONTH) + 1;
					int day = c.get(c.DATE);
					int monVal = Integer.parseInt(val.substring(0,
							val.indexOf("-")));
					int dayVal = Integer.parseInt(val.substring(val
							.indexOf("-") + 1));
					if (monVal < mon) {
						c.add(c.YEAR, 1);
						map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
								+ dayVal);
					} else {
						if (dayVal < day) {
							c.add(c.YEAR, 1);
							map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
									+ dayVal);
						} else {
							map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
									+ dayVal);
						}
					}
				} else if (type == 3) {
					map.put("date", val + "-" + "01");
				} else if (type == 4) {
					val = val.replace("年", "-");
					val = val.replace("月", "-");
					val = val.replace("日", "");
					map.put("date", val);
				} else if (type == 5) {
					val = val.replace("年", "-");
					val = val.replace("月", "-");
					map.put("date", val + "-" + "01");
				} else if (type == 6) {// 2月15
					val = val.replace("月", "-");
					val = val.replace("日", "");
					int mon = c.get(c.MONTH) + 1;
					int day = c.get(c.DATE);
					int monVal = Integer.parseInt(val.substring(0,
							val.indexOf("-")));
					int dayVal = Integer.parseInt(val.substring(val
							.indexOf("-") + 1));
					if (yeartype == 0) {
						if (monVal < mon) {
							c.add(c.YEAR, 1);
							map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
									+ dayVal);
						} else {
							if (monVal == mon) {
								if (dayVal < day) {
									c.add(c.YEAR, 1);
									map.put("date", c.get(c.YEAR) + "-"
											+ monVal + "-" + dayVal);
								} else {
									map.put("date", c.get(c.YEAR) + "-"
											+ monVal + "-" + dayVal);
								}
							} else {
								map.put("date", c.get(c.YEAR) + "-" + monVal
										+ "-" + dayVal);
							}
						}
					} else {
						String dayStr = "";
						String monStr = "";
						for (int i = 1; i <= yinlidays.length; i++) {
							if (dayVal == i) {
								dayStr = yinlidays[i - 1];
								break;
							}
						}
						for (int i = 1; i <= yinlimonths.length; i++) {
							if (monVal == i) {
								monStr = yinlimonths[i - 1];
								break;
							}
						}
						if (isRepeat == 1) {
							map.put("date", monStr + dayStr);
						} else {
							String date = App.getDBcApplication()
									.queryLunartoSolarList(monStr + dayStr, 1)
									.get("calendar");
							map.put("date", date);
						}
					}
				} else if (type == 7) {
					int day = c.get(c.DATE);
					int mon = c.get(c.MONTH) + 1;
					int dayVal = Integer.parseInt(val.replace("日", "").replace(
							"号", ""));
					if (dayVal < day) {
						mon += 1;
						map.put("date", c.get(c.YEAR) + "-" + mon + "-"
								+ dayVal);
					} else {
						map.put("date", c.get(c.YEAR) + "-" + mon + "-"
								+ dayVal);
					}
				} else if (type == 8) {
					val = val.replace("月", "");
					int mon = c.get(c.MONTH) + 1;
					int monVal = Integer.parseInt(val);
					if (monVal < mon) {
						c.add(c.YEAR, 1);
						map.put("date",
								c.get(c.YEAR) + "-" + monVal + "-"
										+ c.get(c.DATE));
					} else {
						map.put("date",
								c.get(c.YEAR) + "-" + monVal + "-"
										+ c.get(c.DATE));
					}
				} else if (type == 9) {
					int year = c.get(c.YEAR);
					int yearVal = Integer.parseInt(val);
					if (yearVal == year) {
						map.put("date", c.get(c.YEAR) + "-" + c.get(c.MONTH)
								+ "-" + c.get(c.DATE));
					} else {
						map.put("date", yearVal + "-" + 1 + "-" + 1);
					}
				} else if (type == 10) {// 月初
					int mon = c.get(c.MONTH) + 1;
					int monVal = Integer.parseInt(val.replace("月初", "")
							.replace("月上旬", ""));
					if (monVal <= mon) {
						c.add(c.YEAR, 1);
						map.put("date", backDate(c.get(c.YEAR), monVal, 1));
					} else {

						map.put("date", backDate(c.get(c.YEAR), monVal, 1));
					}
				} else if (type == 11) {// 月中
					int mon = c.get(c.MONTH) + 1;
					int monVal = Integer.parseInt(val.replace("月中旬", ""));
					int day = c.get(c.DATE);
					if (monVal < mon) {
						c.add(c.YEAR, 1);
						map.put("date", backDate(c.get(c.YEAR), monVal, 11));
					} else if (monVal == mon) {
						if (day <= 11) {
							c.add(c.YEAR, 1);
							map.put("date", backDate(c.get(c.YEAR), monVal, 2));
						} else {
							map.put("date", backDate(c.get(c.YEAR), monVal, 2));
						}
					} else {
						map.put("date", backDate(c.get(c.YEAR), monVal, 2));
					}
				} else if (type == 12) {
					int mon = c.get(c.MONTH) + 1;
					int monVal = Integer.parseInt(val.replace("月下旬", ""));
					int day = c.get(c.DATE);
					if (monVal < mon) {
						c.add(c.YEAR, 1);
						map.put("date", backDate(c.get(c.YEAR), monVal, 3));
					} else if (monVal == mon) {
						if (day <= 21) {
							c.add(c.YEAR, 1);
							map.put("date", backDate(c.get(c.YEAR), monVal, 3));
						} else {
							map.put("date", backDate(c.get(c.YEAR), monVal, 3));
						}
					} else {
						map.put("date", backDate(c.get(c.YEAR), monVal, 3));
					}
				} else if (type == 13) {
					int mon = c.get(c.MONTH) + 1;
					int monVal = Integer.parseInt(val.replace("月末", ""));
					int day = c.get(c.DATE);
					if (monVal < mon) {
						c.add(c.YEAR, 1);
						map.put("date", backDate(c.get(c.YEAR), monVal, -1));
					} else if (mon == monVal) {
						if (monVal != 2) {
							if (day <= 30) {
								c.add(c.YEAR, 1);
								map.put("date",
										backDate(c.get(c.YEAR), monVal, -1));
							} else {
								map.put("date",
										backDate(c.get(c.YEAR), monVal, -1));
							}
						} else {
							map.put("date", backDate(c.get(c.YEAR), monVal, -1));
						}

					} else {
						map.put("date", backDate(c.get(c.YEAR), monVal, -1));
					}
				} else if (type == 14) {
					c.add(c.MONTH, 1);
					map.put("date", c.get(c.YEAR) + "-" + (c.get(c.MONTH) + 1)
							+ "-" + val.replace("下月", "").replace("下个月", ""));
				} else if (type == 15) {// 2016年5月初10
					int year = c.get(c.YEAR);
					int yearVal = Integer.parseInt(val.split("年")[0]);
					int mon = c.get(c.MONTH) + 1;
					int monVal = Integer
							.parseInt(val.split("年")[1].split("月")[0]);
					int day = c.get(c.DATE);
					int dayVal = Integer.parseInt(val.split("月")[1].replace(
							"初", ""));
					String dayStr = "";
					String monStr = "";
					for (int i = 1; i <= yinlidays.length; i++) {
						if (dayVal == i) {
							dayStr = yinlidays[i - 1];
							break;
						}
					}
					for (int i = 1; i <= yinlimonths.length; i++) {
						if (monVal == i) {
							monStr = yinlimonths[i - 1];
							break;
						}
					}
					if (isRepeat == 1) {
						map.put("date", monStr + dayStr);
					} else {
						String date = App.getDBcApplication()
								.queryLunartoSolarList(monStr + dayStr, 1)
								.get("calendar");
						map.put("date", date);
					}
					// if (monVal < mon) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// } else if (mon == monVal) {
					// if (monVal != 2) {
					// if (day <= 30) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// }
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
					//
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
				} else if (type == 16) {// 5月初10
					int mon = c.get(c.MONTH) + 1;
					int monVal = Integer.parseInt(val.split("月")[0]);
					int day = c.get(c.DATE);
					int dayVal = Integer.parseInt(val.split("月")[1].replace(
							"初", ""));
					String dayStr = "";
					String monStr = "";
					for (int i = 1; i <= yinlidays.length; i++) {
						if (dayVal == i) {
							dayStr = yinlidays[i - 1];
							break;
						}
					}
					for (int i = 1; i <= yinlimonths.length; i++) {
						if (monVal == i) {
							monStr = yinlimonths[i - 1];
							break;
						}
					}
					if(isRepeat==1){
						map.put("date", monStr + dayStr);
					}else{
					String date = App.getDBcApplication()
							.queryLunartoSolarList(monStr + dayStr, 1)
							.get("calendar");
					map.put("date", date);
					}
					// Map<String, String> myMap = App.getDBcApplication()
					// .queryLunartoSolarList(monStr + dayStr, 1);
					//
					// if (monVal < mon) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// } else if (mon == monVal) {
					// if (monVal != 2) {
					// if (day <= 30) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// }
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
					//
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
				} else if (type == 17) {// 初**
					int day1 = Integer.parseInt(val.replace("初", ""));
					String str = "";
					for (int i = 1; i <= yinlidays.length; i++) {
						if (day1 == i) {
							str = yinlidays[i - 1];
							break;
						}
					}
					int year = c.get(c.YEAR);
					int mon = c.get(c.MONTH) + 1;
					int day = c.get(c.DATE);
					Map<String, String> myMap = App.getDBcApplication()
							.queryLunartoSolarList(
									year + "-" + (mon < 10 ? "0" + mon : mon)
											+ "-"
											+ (day < 10 ? "0" + day : day), 0);
					int month = Integer.parseInt(myMap.get("lunarCalendar")
							.split("月")[0].replace("正", "1").replace("冬", "11")
							.replace("腊", "12"));
					String yinliday = myMap.get("lunarCalendar").split("月")[1];
					int dayone = 0;
					for (int i = 0; i <= yinlidays.length; i++) {
						if (yinliday.equals(yinlidays[i])) {
							dayone = i + 1;
							break;
						}
					}
					if (dayone > day1) {// 当前农历小于设置农历
						month += 1;
						for (int i = 0; i <= yinlimonths.length; i++) {
							if (month == i) {
								Map<String, String> myMap1 = App
										.getDBcApplication()
										.queryLunartoSolarList(
												yinlimonths[i - 1] + str, 1);
								map.put("date", myMap1.get("calendar"));
								break;
							}
						}
					} else {
						for (int i = 0; i <= yinlimonths.length; i++) {
							if (month == i) {
								Map<String, String> myMap1 = App
										.getDBcApplication()
										.queryLunartoSolarList(
												yinlimonths[i - 1] + str, 1);
								map.put("date", myMap1.get("calendar"));
								break;
							}
						}
					}
				} else if (type == 18) {
					int day1 = Integer.parseInt(val.split("月")[1].replace("初",
							""));
					String str = "";
					for (int i = 1; i <= yinlidays.length; i++) {
						if (day1 == i) {
							str = yinlidays[i - 1];
						}
					}
					if(isRepeat==1){
						map.put("date", val.split("月")[0] + "月" + str);
					}else{
						String date = App
								.getDBcApplication()
								.queryLunartoSolarList(
										val.split("月")[0] + "月" + str, 1)
								.get("calendar");
						map.put("date", date);
					}
					// Map<String, String> mMap = App.getDBcApplication()
					// .queryLunartoSolarList(
					// val.split("月")[0] + "月" + str, 1);
					// String date = mMap.get("calendar");
					// int mon = c.get(c.MONTH) + 1;
					// int monVal = Integer.parseInt(date.split("-")[1]);
					// int day = c.get(c.DATE);
					// int dayVal = Integer.parseInt(date.split("-")[2]);
					// if (monVal < mon) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// } else if (mon == monVal) {
					// if (monVal != 2) {
					// if (day <= 30) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// }
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
					//
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
				} else if (type == 19) {
					int day1 = Integer.parseInt(val.split("月")[1]);
					String str = "";
					for (int i = 1; i <= yinlidays.length; i++) {
						if (day1 == i) {
							str = yinlidays[i - 1];
						}
					}
					String date = App
							.getDBcApplication()
							.queryLunartoSolarList(
									val.split("月")[0] + "月" + str, 1)
							.get("calendar");
					map.put("date", date);
					// Map<String, String> mMap = App.getDBcApplication()
					// .queryLunartoSolarList(
					// val.split("月")[0] + "月" + str, 1);
					// String date = mMap.get("calendar");
					// int mon = c.get(c.MONTH) + 1;
					// int monVal = Integer.parseInt(date.split("-")[1]);
					// int day = c.get(c.DATE);
					// int dayVal = Integer.parseInt(date.split("-")[2]);
					// if (monVal < mon) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// } else if (mon == monVal) {
					// if (monVal != 2) {
					// if (day <= 30) {
					// c.add(c.YEAR, 1);
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal
					// + "-" + dayVal);
					// }
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
					//
					// } else {
					// map.put("date", c.get(c.YEAR) + "-" + monVal + "-"
					// + dayVal);
					// }
				}

				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 语音识别正则表达式Type 日期转换
		 * 
		 * @return
		 */
		public static Map<Object, Object> backRegexTypeDateOne(int type, int val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			try {
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				if (type == 1) {
					c.add(c.DATE, val);
				} else if (type == 2) {
					c.add(c.DATE, val * 7);
				} else if (type == 3) {
					c.add(c.MONTH, val);
				} else if (type == 4) {
					c.add(c.YEAR, val);
				} else if (type == 10) {
					c.add(c.MINUTE, val);
				} else if (type == 11) {
					c.add(c.MINUTE, 15 * val);
				} else if (type == 12) {
					c.add(c.HOUR, val);
				}

				map.put("date", sf.format(c.getTime()));
				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * 语音识别汉字分析返回
		 */
		public static String backVal(String val) {
			val = val.trim();
			try {
				int k = 0;
				for (int j = han.length - 1; j >= 0; j--) {

					if (val.indexOf(han[j]) >= 0) {
						val = val.replace(han[j], num[j] + "");
					}
				}
				// val=val.replace(han[k], num[k]+"");
				return val;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		/**
		 * 语音识别Xml时间转换
		 * 
		 * @return
		 */
		public Map<Object, Object> backXmlTypeTime(int type, String val) {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
			Map<Object, Object> map = new HashMap<Object, Object>();
			if (type == 3) {
				map.put("time", sf.format(c.getTime()));
			} else {
				map.put("time", val);
			}
			return map;
		}

		/**
		 * 语音识别Xml日期转换
		 */
		public static Map<Object, Object> backXmlTypeDate(int type, int val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			try {
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				int wek = c.get(c.DAY_OF_WEEK);
				int i = backWekDay(wek);// 当前天数星期数
				int j = backWekDay(val);// 解析星期数
				if (type == 1) {// 获取日期为 周 几
					if (i > j) {
						c.add(c.DATE, 7 - (i - j));
					} else if (i < j) {
						c.add(c.DATE, j - i);
					} else {
						c.add(c.DATE, 7);
					}
					map.put("date", sf.format(c.getTime()));
				} else if (type == 2) {// 下周
					c.add(c.DATE, 7 - i + j);
					map.put("date", sf.format(c.getTime()));
				} else if (type == 3) {// 下下周
					c.add(c.DATE, 14 - i + j);
					map.put("date", sf.format(c.getTime()));
				} else if (type == 4) {//
					c.add(c.DATE, val);
					map.put("date", sf.format(c.getTime()));
				} else if (type == 5) {
					c.add(c.YEAR, 1);
					map.put("date", sf.format(c.getTime()));
				} else if (type == 6) {
					int mon = c.get(c.MONTH) + 1;
					int year = c.get(c.YEAR);
					map.put("date", backDate(year, mon, val));
				} else if (type == 7) {
					c.add(c.MONTH, 1);
					int mon = c.get(c.MONTH) + 1;
					int year = c.get(c.YEAR);
					map.put("date", backDate(year, mon, val));
				} else if (type == 8) {
					c.add(c.DATE, val);
					map.put("date", sf.format(c.getTime()));
				}

				return map;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 返回 月末 上旬 中旬 下旬 后的日期
		 * 
		 * @return
		 */
		public static String backDate(int year, int mon, int val) {

			String m = "0";
			if (mon < 10) {
				m = m + mon;
			} else {
				m = mon + "";
			}
			if (val == -1) {// 月末
				if (mon == 2) {
					return year + "-" + m + "-" + 28;
				} else {
					return year + "-" + m + "-" + 30;
				}

			} else if (val == 1) {// 月初
				return year + "-" + m + "-0" + 1;
			} else if (val == 2) {// 月中
				return year + "-" + m + "-" + 11;
			} else if (val == 3) {// 下旬
				return year + "-" + m + "-" + 21;
			}
			return "";
		}

		/**
		 * 返回具体星期数
		 * 
		 * @param wek
		 * @return
		 */
		public static int backWekDay(int wek) {
			int week = 0;
			switch (wek) {
			case 1:
				week = 7;
				break;
			case 2:
				week = 1;
				break;
			case 3:
				week = 2;
				break;
			case 4:
				return 3;
			case 5:
				week = 4;
				break;
			case 6:
				week = 5;
				break;
			case 7:
				week = 6;
				break;
			}
			return week;
		}

		/**
		 * 读取时间xml
		 */
		public static Map<Object, Object> readTimeOrDateXMl(Context context,
				String src, String val) {
			// 读取xml文件 "src/com/timetable/test/time.xml"
			InputStream inputStream = context.getClass().getResourceAsStream(
					"/assets/" + src);
			// 读取xml文件 "src/com/timetable/test/time.xml"

			// File f = new File(src);
			DocumentBuilder db = null;
			DocumentBuilderFactory dbf = null;
			Element element = null;
			Map<Object, Object> map = new HashMap<Object, Object>();
			try {
				// 初始化
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
				Document dom = db.parse(inputStream);
				// System.out.println("第一个节点：" + dom.getNodeName());
				element = dom.getDocumentElement();
				// 获取这个节点下的所有元素
				NodeList nodelist = dom.getElementsByTagName("dicts");
				// 获取所有子节点
				NodeList n = nodelist.item(0).getChildNodes();
				Vector vc = new Vector();
				for (int i = 0; i < n.getLength(); i++) {

					// 判读文本是否
					if (val.indexOf(n.item(i).getTextContent()) == 0
							&& (n.item(i).getTextContent().indexOf("月") != -1 || n
									.item(i).getTextContent().indexOf("下下周") != -1)) {

						map.put("content",
								val.replace(n.item(i).getTextContent(), ""));

						vc.add(n.item(i + 2).getChildNodes());

					}
					if ((val.indexOf(n.item(i).getTextContent()) == 0 && (n
							.item(i).getTextContent().indexOf("月") == -1 && n
							.item(i).getTextContent().indexOf("下下周") == -1))) {

						NodeList nd = n.item(i + 2).getChildNodes();
						map.put("content",
								val.replace(n.item(i).getTextContent(), ""));

						// 获取里面值
						for (int j = 0; j < nd.getLength(); j++) {

							if (nd.item(j).getTextContent().equals("type")) {
								map.put("type", nd.item(j + 2).getTextContent());

							} else if (nd.item(j).getTextContent()
									.equals("value")) {
								map.put("value", nd.item(j + 2)
										.getTextContent());

							}

						}
						break;
					}
				}

				Object[] obj = vc.toArray();

				if (obj.length > 0) {
					NodeList nd = (NodeList) obj[obj.length - 1];
					// 获取里面值
					for (int j = 0; j < nd.getLength(); j++) {

						if (nd.item(j).getTextContent().equals("type")) {
							map.put("type", nd.item(j + 2).getTextContent());

						} else if (nd.item(j).getTextContent().equals("value")) {
							map.put("value", nd.item(j + 2).getTextContent());

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}

		/**
		 * 读取铃声xml
		 */
		public static Map<Object, Object> readDescXML(Context context,
				String src, String val) {
			// 读取xml文件
			InputStream inputStream = context.getClass().getResourceAsStream(
					"/assets/" + src);
			// File f = new File(src);
			DocumentBuilder db = null;
			DocumentBuilderFactory dbf = null;
			Element element = null;
			Map<Object, Object> map = new HashMap<Object, Object>();
			try {
				// 初始化
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
				Document dom = db.parse(inputStream);
				// System.out.println("第一个节点：" + dom.getNodeName());
				element = dom.getDocumentElement();
				NodeList nodelist = dom.getElementsByTagName("dict");

				NodeList n = nodelist.item(0).getChildNodes();
				for (int i = 0; i < n.getLength(); i++) {
					if (i % 2 != 0) {
						// System.out.println(n.item(i).getTextContent()+"=== "+val.indexOf(n.item(i).getTextContent()));
						// 判读是否存在铃声
						if (val.indexOf(n.item(i).getTextContent()) >= 0) {
							System.out.println(n.item(i).getTextContent() + "");
							map.put("desc", n.item(i + 2).getTextContent());
							// map.put("descVal", n.item(i+4).getTextContent());
							break;
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}

		private static byte[] InputStreamToByte(InputStream is)
				throws IOException {
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;

		}

		public String jiaMi() {
			String zj[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" };
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm");
			String date = sf.format(new Date());
			String backVal = "";

			for (int i = 0; i < date.length(); i++) {
				String nu = date.substring(i, i + 1);
				backVal += zj[Integer.parseInt(nu)];
			}
			return backVal;
		}

		/**
		 * 生日语音识别
		 * 
		 * @param context
		 * @param val
		 * @return
		 */
		public static Map<Object, Object> yuyinBirth(Context context, String val) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			try {
				String yeartype;// 0阳历 1阴历
				String month;
				String day;
				String content = "";
				String[] yinlimonths = context.getResources().getStringArray(
						R.array.monthStr);
				String[] yinlidays = context.getResources().getStringArray(
						R.array.lunarstr);
				String[] yanglimonths = context.getResources().getStringArray(
						R.array.months);
				String[] yanglidays = context.getResources().getStringArray(
						R.array.lunarday);
				String[] nums = context.getResources().getStringArray(
						R.array.number);
				if (!val.substring(0, 2).equals("农历") && val.length() >= 2
						&& !val.substring(0, 1).equals("初")
						&& val.length() >= 2
						&& !val.substring(0, 1).equals("正")
						&& val.length() >= 2
						&& !val.substring(0, 1).equals("冬")
						&& val.length() >= 2
						&& !val.substring(0, 1).equals("腊")
						&& val.length() >= 4
						&& !val.substring(2, 3).equals("初")
						&& !val.substring(3, 4).contains("初")) {
					String valChange = backVal(val);
					if (val.length() != 0) {
						if (val.length() >= 2
								&& val.substring(0, 2).contains("初")
								|| val.length() >= 4
								&& val.substring(0, 4).contains("初")
								|| val.length() >= 2
								&& val.substring(0, 2).contains("正")
								|| val.length() >= 2
								&& val.substring(0, 2).contains("冬")
								|| val.length() >= 2
								&& val.substring(0, 2).contains("腊")) {
							String str = val.replace("正", "1")
									.replace("冬", "11").replace("腊", "12");
							// str = str.replace("正", "1").replace("冬", "11")
							// .replace("腊", "12");
							for (int i = 1; i <= nums.length; i++) {
								str = str.replace(nums[i - 1], nums.length + 1
										- i + "");
							}
							String contentStr = "";
							if ("初".equals(str.substring(0, 1))) {
								str = str.substring(1);
								val = val.substring(1);
								if (str.length() >= 2) {
									if (isNumeric(str.substring(0, 2))) {
										if (Integer.parseInt(str
												.substring(0, 2)) == 10) {
											str = Integer.parseInt(str
													.substring(0, 2))
													+ "日"
													+ val.substring(2);
										} else {
											str = Integer.parseInt(str
													.substring(0, 1))
													+ "日"
													+ val.substring(1);
										}
									} else if (isNumeric(str.substring(0, 1))) {
										str = Integer.parseInt(str.substring(0,
												1)) + "日" + val.substring(1);
									}
								}
								contentStr = str;
							} else {
								String month1 = str.split("月")[0].replace("本",
										simpleDateFormat.format(new Date())
												.subSequence(5, 7));
								String daystr = str.split("月")[1];
								String newstr = val.split("月")[1];
								String oldstr = val.split("月")[1];
								if ("初".equals(daystr.substring(0, 1))) {
									daystr = daystr.substring(1);
									newstr = newstr.substring(1);
									if (daystr.length() >= 2) {
										if (isNumeric(daystr.substring(0, 2))) {
											if (Integer.parseInt(daystr
													.substring(0, 2)) == 10) {
												daystr = Integer
														.parseInt(daystr
																.substring(0, 2))
														+ "日"
														+ newstr.substring(2);
											} else {
												daystr = Integer
														.parseInt(daystr
																.substring(0, 1))
														+ "日"
														+ newstr.substring(1);
											}
											contentStr = month1 + "月" + daystr;
										} else if (isNumeric(daystr.substring(
												0, 1))) {
											daystr = Integer.parseInt(daystr
													.substring(0, 1))
													+ "日"
													+ newstr.substring(1);
											contentStr = month1 + "月" + daystr;
										} else {
											contentStr = month1 + "月" + oldstr;
										}

									} else {
										if (!"".equals(daystr)) {
											if (isNumeric(daystr
													.substring(0, 1))) {
												daystr = Integer
														.parseInt(daystr
																.substring(0, 1))
														+ "日"
														+ newstr.substring(1);
												contentStr = month1 + "月"
														+ daystr;
											} else {
												contentStr = month1 + "月"
														+ oldstr;
											}
										} else {
											contentStr = month1;
										}
									}

								} else {
									if (isNumeric(daystr.substring(0, 2))) {
										if (Integer.parseInt(daystr.substring(
												0, 2)) >= 10
												&& Integer.parseInt(daystr
														.substring(0, 2)) <= 31) {
											daystr = Integer.parseInt(daystr
													.substring(0, 2))
													+ "日"
													+ newstr.substring(2);
										} else {
											daystr = Integer.parseInt(daystr
													.substring(0, 1))
													+ "日"
													+ newstr.substring(1);
										}
										contentStr = month1 + "月" + daystr;
									} else if (isNumeric(daystr.substring(0, 1))) {
										daystr = Integer.parseInt(daystr
												.substring(0, 1))
												+ "日"
												+ newstr.substring(1);
										contentStr = month1 + "月" + daystr;
									} else {
										contentStr = month1 + "月" + oldstr;
									}
								}
							}
							Map mOne = JxRegexDateOne(contentStr);// 读取正则表达式规则1
							Map mTwo = JxRegexDateTwo(contentStr);// 读取正则表达式规则2、
							if (null != mOne && mOne.size() > 0) {
								Map mOneMess = backRegexTypeDateOne(
										Integer.parseInt(mOne.get("type") + ""),
										Integer.parseInt(mOne.get("day") + ""));
								map.put("date", mOneMess.get("date"));
								valChange = mOne.get("content") + "";
							} else if (null != mTwo && mTwo.size() > 0) {
								Map mTwoMess = backRegexTypeDateTwo(
										0,
										context,
										Integer.parseInt(mTwo.get("typeTwo")
												+ ""), mTwo.get("dayTwo") + "",
										1);
								map.put("date", mTwoMess.get("date"));
								valChange = mTwo.get("content") + "";
							} else {
								Map mDate = readTimeOrDateXMl(context,
										"DateRecognizer.xml", valChange.trim());

								Map mDateMess = null;

								if (mDate != null && mDate.size() > 0) {
									int v = Integer.parseInt(mDate.get("value")
											+ "");
									// 处理关键字
									if (!(Integer.parseInt(mDate.get("type")
											+ "") == 4 && v == 1)) {
										valChange = mDate.get("content") + "";

									} else {

									}
									mDateMess = backXmlTypeDate(
											Integer.parseInt(mDate.get("type")
													+ ""),
											Integer.parseInt(mDate.get("value")
													+ ""));

								}
							}
							System.out.println("==============>>>>" + map);
						} else {
							String str = valChange;
							String month1 = "";
							for (int i = 1; i <= nums.length; i++) {
								str = str.replace(nums[i - 1], nums.length + 1
										- i + "");
							}
							if (str.length() >= 3
									&& str.substring(0, 3).contains("月")) {
								month1 = str.split("月")[0].replace("本",
										simpleDateFormat.format(new Date())
												.subSequence(5, 7));
								String daystr = str.split("月")[1];
								valChange = month1 + "月" + daystr;
							} else {
								valChange = str;
							}
							Map mOne = JxRegexDateOne(valChange);// 读取正则表达式规则1
							Map mTwo = JxRegexDateTwo(valChange);// 读取正则表达式规则2、
							System.out.println(mOne + "aaa" + mTwo);
							if (null != mOne && mOne.size() > 0) {
								Map mOneMess = backRegexTypeDateOne(
										Integer.parseInt(mOne.get("type") + ""),
										Integer.parseInt(mOne.get("day") + ""));
								map.put("date", mOneMess.get("date"));
								valChange = mOne.get("content") + "";
							} else if (null != mTwo && mTwo.size() > 0) {
								Map mTwoMess = backRegexTypeDateTwo(
										0,
										context,
										Integer.parseInt(mTwo.get("typeTwo")
												+ ""), mTwo.get("dayTwo") + "",
										1);
								map.put("date", mTwoMess.get("date"));
								valChange = mTwo.get("content") + "";
							} else {
								Map mDate = readTimeOrDateXMl(context,
										"DateRecognizer.xml", valChange.trim());

								Map mDateMess = null;

								if (mDate != null && mDate.size() > 0) {
									int v = Integer.parseInt(mDate.get("value")
											+ "");
									// 处理关键字
									if (!(Integer.parseInt(mDate.get("type")
											+ "") == 4 && v == 1)) {
										valChange = mDate.get("content") + "";

									} else {

									}
									mDateMess = backXmlTypeDate(
											Integer.parseInt(mDate.get("type")
													+ ""),
											Integer.parseInt(mDate.get("value")
													+ ""));
									if (mDateMess != null
											&& mDateMess.size() > 0) {
										map.put("date", mDateMess.get("date"));
									}

								}
							}
							System.out
									.println("==============>>>>******" + map);
						}
					}
					map.put("content", valChange);
					map.put("yeartype", "0");
				} else if (val.length() == 2 && val.equals("月初")) {
					String mycontent = "";
					Map mOne = JxRegexDateOne(Integer.parseInt(simpleDateFormat
							.format(new Date()).substring(5, 7)) + "月");// 读取正则表达式规则1
					Map mTwo = JxRegexDateTwo(Integer.parseInt(simpleDateFormat
							.format(new Date()).substring(5, 7)) + "月");// 读取正则表达式规则2、
					if (null != mOne && mOne.size() > 0) {
						Map mOneMess = backRegexTypeDateOne(
								Integer.parseInt(mOne.get("type") + ""),
								Integer.parseInt(mOne.get("day") + ""));
						map.put("date", mOneMess.get("date"));
						mycontent = mOne.get("content") + "";
					} else if (null != mTwo && mTwo.size() > 0) {
						Map mTwoMess = backRegexTypeDateTwo(0, context,
								Integer.parseInt(mTwo.get("typeTwo") + ""),
								mTwo.get("dayTwo") + "", 1);
						map.put("date", mTwoMess.get("date"));
						mycontent = mTwo.get("content") + "";
					} else {
						Map mDate = readTimeOrDateXMl(context,
								"DateRecognizer.xml", val.trim());

						Map mDateMess = null;

						if (mDate != null && mDate.size() > 0) {
							int v = Integer.parseInt(mDate.get("value") + "");
							// 处理关键字
							if (!(Integer.parseInt(mDate.get("type") + "") == 4 && v == 1)) {
								mycontent = mDate.get("content") + "";

							} else {

							}
							mDateMess = backXmlTypeDate(
									Integer.parseInt(mDate.get("type") + ""),
									Integer.parseInt(mDate.get("value") + ""));
							if (mDateMess != null && mDateMess.size() > 0) {
								map.put("date", mDateMess.get("date"));
							}

						}
					}
					map.put("content", mycontent);
					map.put("yeartype", "0");
				} else if (val.length() == 3 && val.substring(1).equals("月初")) {
					String mycontent = "";
					Map mOne = JxRegexDateOne(Integer.parseInt(simpleDateFormat
							.format(new Date()).substring(5, 7)) + "月");// 读取正则表达式规则1
					Map mTwo = JxRegexDateTwo(Integer.parseInt(simpleDateFormat
							.format(new Date()).substring(5, 7)) + "月");// 读取正则表达式规则2、
					if (null != mOne && mOne.size() > 0) {
						Map mOneMess = backRegexTypeDateOne(
								Integer.parseInt(mOne.get("type") + ""),
								Integer.parseInt(mOne.get("day") + ""));
						map.put("date", mOneMess.get("date"));
						mycontent = mOne.get("content") + "";
					} else if (null != mTwo && mTwo.size() > 0) {
						Map mTwoMess = backRegexTypeDateTwo(0, context,
								Integer.parseInt(mTwo.get("typeTwo") + ""),
								mTwo.get("dayTwo") + "", 1);
						map.put("date", mTwoMess.get("date"));
						mycontent = mTwo.get("content") + "";
					} else {
						Map mDate = readTimeOrDateXMl(context,
								"DateRecognizer.xml", val.trim());

						Map mDateMess = null;

						if (mDate != null && mDate.size() > 0) {
							int v = Integer.parseInt(mDate.get("value") + "");
							// 处理关键字
							if (!(Integer.parseInt(mDate.get("type") + "") == 4 && v == 1)) {
								mycontent = mDate.get("content") + "";

							} else {

							}
							mDateMess = backXmlTypeDate(
									Integer.parseInt(mDate.get("type") + ""),
									Integer.parseInt(mDate.get("value") + ""));
							if (mDateMess != null && mDateMess.size() > 0) {
								map.put("date", mDateMess.get("date"));
							}

						}
					}
					map.put("content", mycontent);
					map.put("yeartype", "0");
				} else if (val.length() == 3 && val.equals("本月初")) {
					String mycontent = "";
					Map mOne = JxRegexDateOne(Integer.parseInt(simpleDateFormat
							.format(new Date()).substring(5, 7)) + "月");// 读取正则表达式规则1
					Map mTwo = JxRegexDateTwo(Integer.parseInt(simpleDateFormat
							.format(new Date()).substring(5, 7)) + "月");// 读取正则表达式规则2、
					if (null != mOne && mOne.size() > 0) {
						Map mOneMess = backRegexTypeDateOne(
								Integer.parseInt(mOne.get("type") + ""),
								Integer.parseInt(mOne.get("day") + ""));
						map.put("date", mOneMess.get("date"));
						mycontent = mOne.get("content") + "";
					} else if (null != mTwo && mTwo.size() > 0) {
						Map mTwoMess = backRegexTypeDateTwo(0, context,
								Integer.parseInt(mTwo.get("typeTwo") + ""),
								mTwo.get("dayTwo") + "", 1);
						map.put("date", mTwoMess.get("date"));
						mycontent = mTwo.get("content") + "";
					} else {
						Map mDate = readTimeOrDateXMl(context,
								"DateRecognizer.xml", val.trim());

						Map mDateMess = null;

						if (mDate != null && mDate.size() > 0) {
							int v = Integer.parseInt(mDate.get("value") + "");
							// 处理关键字
							if (!(Integer.parseInt(mDate.get("type") + "") == 4 && v == 1)) {
								mycontent = mDate.get("content") + "";

							} else {

							}
							mDateMess = backXmlTypeDate(
									Integer.parseInt(mDate.get("type") + ""),
									Integer.parseInt(mDate.get("value") + ""));
							if (mDateMess != null && mDateMess.size() > 0) {
								map.put("date", mDateMess.get("date"));
							}

						}
					}
					map.put("content", mycontent);
					map.put("yeartype", "0");
				} else if (val.length() == 2
						&& (val.contains("号") || val.contains("日"))) {
					String mycontent = "";
					Map mOne = JxRegexDateOne(val);// 读取正则表达式规则1
					Map mTwo = JxRegexDateTwo(val);// 读取正则表达式规则2、
					if (null != mOne && mOne.size() > 0) {
						Map mOneMess = backRegexTypeDateOne(
								Integer.parseInt(mOne.get("type") + ""),
								Integer.parseInt(mOne.get("day") + ""));
						map.put("date", mOneMess.get("date"));
						mycontent = mOne.get("content") + "";
					} else if (null != mTwo && mTwo.size() > 0) {
						Map mTwoMess = backRegexTypeDateTwo(0, context,
								Integer.parseInt(mTwo.get("typeTwo") + ""),
								mTwo.get("dayTwo") + "", 1);
						map.put("date", mTwoMess.get("date"));
						mycontent = mTwo.get("content") + "";
					} else {
						Map mDate = readTimeOrDateXMl(context,
								"DateRecognizer.xml", val.trim());

						Map mDateMess = null;

						if (mDate != null && mDate.size() > 0) {
							int v = Integer.parseInt(mDate.get("value") + "");
							// 处理关键字
							if (!(Integer.parseInt(mDate.get("type") + "") == 4 && v == 1)) {
								mycontent = mDate.get("content") + "";

							} else {

							}
							mDateMess = backXmlTypeDate(
									Integer.parseInt(mDate.get("type") + ""),
									Integer.parseInt(mDate.get("value") + ""));
							if (mDateMess != null && mDateMess.size() > 0) {
								map.put("date", mDateMess.get("date"));
							}

						}
					}
					map.put("content", mycontent);
					map.put("yeartype", "0");
				} else if (val.length() == 3
						&& (val.contains("号") || val.contains("日"))) {
					String mycontent = "";
					Map mOne = JxRegexDateOne(val);// 读取正则表达式规则1
					Map mTwo = JxRegexDateTwo(val);// 读取正则表达式规则2、
					if (null != mOne && mOne.size() > 0) {
						Map mOneMess = backRegexTypeDateOne(
								Integer.parseInt(mOne.get("type") + ""),
								Integer.parseInt(mOne.get("day") + ""));
						map.put("date", mOneMess.get("date"));
						mycontent = mOne.get("content") + "";
					} else if (null != mTwo && mTwo.size() > 0) {
						Map mTwoMess = backRegexTypeDateTwo(0, context,
								Integer.parseInt(mTwo.get("typeTwo") + ""),
								mTwo.get("dayTwo") + "", 1);
						map.put("date", mTwoMess.get("date"));
						mycontent = mTwo.get("content") + "";
					} else {
						Map mDate = readTimeOrDateXMl(context,
								"DateRecognizer.xml", val.trim());

						Map mDateMess = null;

						if (mDate != null && mDate.size() > 0) {
							int v = Integer.parseInt(mDate.get("value") + "");
							// 处理关键字
							if (!(Integer.parseInt(mDate.get("type") + "") == 4 && v == 1)) {
								mycontent = mDate.get("content") + "";

							} else {

							}
							mDateMess = backXmlTypeDate(
									Integer.parseInt(mDate.get("type") + ""),
									Integer.parseInt(mDate.get("value") + ""));
							if (mDateMess != null && mDateMess.size() > 0) {
								map.put("date", mDateMess.get("date"));
							}

						}
					}
					map.put("content", mycontent);
					map.put("yeartype", "0");
				} else {
					yeartype = "1";
					val = val.trim();
					if ("农历".equals(val.substring(0, 2))) {
						val = val.replace("农历", "");
					}
					String valChange = val;// backVal(val);
					if (val.length() != 0) {
						String str = val.replace("正", "1").replace("冬", "11")
								.replace("腊", "12");
						for (int i = 1; i <= nums.length; i++) {
							str = str.replace(nums[i - 1], nums.length + 1 - i
									+ "");
						}
						String contentStr = "";
						if ("初".equals(str.substring(0, 1))) {
							str = str.substring(1);
							val = val.substring(1);
							if (str.length() >= 2) {
								if (isNumeric(str.substring(0, 2))) {
									if (Integer.parseInt(str.substring(0, 2)) == 10) {
										str = Integer.parseInt(str.substring(0,
												2)) + "日" + val.substring(2);
									} else {
										str = Integer.parseInt(str.substring(0,
												1)) + "日" + val.substring(1);
									}
								} else if (isNumeric(str.substring(0, 1))) {
									str = Integer.parseInt(str.substring(0, 1))
											+ "日" + val.substring(1);
								}
							} else {
								str = str + "日";
							}
							contentStr = str;
						} else {
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyy-MM-dd");
							String month1 = str.split("月")[0].replace(
									"本",
									dateFormat.format(new Date()).subSequence(
											5, 7));
							String daystr = str.split("月")[1];
							String newstr = val.split("月")[1];
							String oldstr = val.split("月")[1];
							if ("初".equals(daystr.substring(0, 1))) {
								daystr = daystr.substring(1);
								newstr = newstr.substring(1);
								if (daystr.length() >= 2) {
									if (isNumeric(daystr.substring(0, 2))) {
										if (Integer.parseInt(daystr.substring(
												0, 2)) == 10) {
											daystr = Integer.parseInt(daystr
													.substring(0, 2))
													+ "日"
													+ newstr.substring(2);
										} else {
											daystr = Integer.parseInt(daystr
													.substring(0, 1))
													+ "日"
													+ newstr.substring(1);
										}
										contentStr = month1 + "月" + daystr;
									} else if (isNumeric(daystr.substring(0, 1))) {
										daystr = Integer.parseInt(daystr
												.substring(0, 1))
												+ "日"
												+ newstr.substring(1);
										contentStr = month1 + "月" + daystr;
									} else {
										contentStr = month1 + "月" + oldstr;
									}

								} else {
									if (!"".equals(daystr)) {

										if (isNumeric(daystr.substring(0, 1))) {
											daystr = Integer.parseInt(daystr
													.substring(0, 1))
													+ "日"
													+ newstr.substring(1);
											contentStr = month1 + "月" + daystr;
										} else {
											contentStr = month1 + "月" + oldstr;
										}
									} else {
										if ("".equals(month1)) {
											month1 = Integer
													.parseInt(simpleDateFormat
															.format(new Date())
															.substring(5, 7))
													+ "";
										}
										contentStr = month1 + "月";
									}
								}

							} else {
								if (isNumeric(daystr.substring(0, 2))) {
									if (Integer
											.parseInt(daystr.substring(0, 2)) >= 10
											&& Integer.parseInt(daystr
													.substring(0, 2)) <= 31) {
										if (Integer.parseInt(daystr.substring(
												0, 2)) >= 21) {
											daystr = Integer.parseInt(daystr
													.substring(0, 2))
													+ "日"
													+ newstr.substring(3);
										} else {
											daystr = Integer.parseInt(daystr
													.substring(0, 2))
													+ "日"
													+ newstr.substring(2);
										}
									} else {
										daystr = Integer.parseInt(daystr
												.substring(0, 1))
												+ "日"
												+ newstr.substring(1);
									}
									contentStr = month1 + "月" + daystr;
								} else if (isNumeric(daystr.substring(0, 1))) {
									daystr = Integer.parseInt(daystr.substring(
											0, 1)) + "日" + newstr.substring(1);
									contentStr = month1 + "月" + daystr;
								} else {
									contentStr = month1 + "月" + oldstr;
								}
							}
						}
						Map mOne = JxRegexDateOne(contentStr);// 读取正则表达式规则1
						Map mTwo = JxRegexDateTwo(contentStr);// 读取正则表达式规则2、
						if (null != mOne && mOne.size() > 0) {
							Map mOneMess = backRegexTypeDateOne(
									Integer.parseInt(mOne.get("type") + ""),
									Integer.parseInt(mOne.get("day") + ""));
							map.put("date", mOneMess.get("date"));
							valChange = mOne.get("content") + "";
						} else if (null != mTwo && mTwo.size() > 0) {
							Map mTwoMess = backRegexTypeDateTwo(1, context,
									Integer.parseInt(mTwo.get("typeTwo") + ""),
									mTwo.get("dayTwo") + "", 1);
							map.put("date", mTwoMess.get("date"));
							valChange = mTwo.get("content") + "";
						} else {
							Map mDate = readTimeOrDateXMl(context,
									"DateRecognizer.xml", valChange.trim());

							Map mDateMess = null;

							if (mDate != null && mDate.size() > 0) {
								int v = Integer.parseInt(mDate.get("value")
										+ "");
								// 处理关键字
								if (!(Integer.parseInt(mDate.get("type") + "") == 4 && v == 1)) {
									valChange = mDate.get("content") + "";

								} else {

								}
								mDateMess = backXmlTypeDate(
										Integer.parseInt(mDate.get("type") + ""),
										Integer.parseInt(mDate.get("value")
												+ ""));
								if (mDateMess != null && mDateMess.size() > 0) {
									map.put("date", mDateMess.get("date"));
								}

							}
						}
						System.out.println("==============>>>>" + map);
					}
					String mymonth = "";
					String myday = "";
					if (map != null && map.size() > 0) {
						String nonglidate = (String) map.get("date");
						nonglidate = simpleDateFormat.format(simpleDateFormat
								.parse(nonglidate));
						int nonglimonth = Integer.parseInt(nonglidate
								.substring(5, 7));
						int nongliday = Integer.parseInt(nonglidate.substring(
								8, 10));
						for (int i = 1; i <= yinlimonths.length; i++) {
							if (nonglimonth == i) {
								mymonth = yinlimonths[i - 1];
								break;
							}
						}
						for (int i = 1; i < yinlidays.length; i++) {
							if (nongliday == i) {
								myday = yinlidays[i - 1];
								break;
							}
						}
					}
					map.put("date", mymonth + myday);
					map.put("content", valChange);
					map.put("yeartype", yeartype);
				}
				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}

		public static boolean isNumeric(String str) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(str);
			if (!isNum.matches()) {
				return false;
			}
			return true;
		}
	}
}
