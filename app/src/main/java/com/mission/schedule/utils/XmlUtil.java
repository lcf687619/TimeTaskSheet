package com.mission.schedule.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Xml;

public class XmlUtil {
	
	/**
	 * 解析铃声xml
	 * 
	 * @param strResult
	 * @return
	 */
	public static List<Map<String, String>> readBellXML(Context context) {
		try {
			InputStream inStream = context.getClass().getResourceAsStream("/assets/LocalRingsName.xml");
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();
			Map<String, String> currentMap = null;
			List<Map<String, String>> currentList = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					currentList = new ArrayList<Map<String, String>>();

					break;

				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();
					if (name.equalsIgnoreCase("bell")) {
						currentMap = new HashMap<String, String>();
					} else if (currentMap != null) {
						if (name.equalsIgnoreCase("name")) {
							currentMap.put("name", parser.nextText());
						} else if (name.equalsIgnoreCase("value")) {
							currentMap.put("value", parser.nextText());
						}
					}

					break;

				case XmlPullParser.END_TAG:// 结束元素事件
					if (parser.getName().equalsIgnoreCase("bell") && currentMap != null) {
						currentList.add(currentMap);
						currentMap = null;
					}

					break;

				}

				eventType = parser.next();
			}
			inStream.close();
			return currentList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 解析铃声xml
	 * 
	 * @param strResult
	 * @return
	 */
	public static List<Map<String, String>> readBeforeBellXML(Context context) {
		try {
			InputStream inStream = context.getClass().getResourceAsStream("/assets/LocalBeforeRings.xml");
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			InputStreamReader reader = new InputStreamReader(inStream);
			parser.setInput(reader);
			int eventType = parser.getEventType();
			Map<String, String> currentMap = null;
			List<Map<String, String>> currentList = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					currentList = new ArrayList<Map<String, String>>();

					break;

				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();
					if (name.equalsIgnoreCase("bell")) {
						currentMap = new HashMap<String, String>();
					} else if (currentMap != null) {
						if (name.equalsIgnoreCase("name")) {
							currentMap.put("name", parser.nextText());
						} else if (name.equalsIgnoreCase("value")) {
							currentMap.put("value", parser.nextText());
						}
					}

					break;

				case XmlPullParser.END_TAG:// 结束元素事件
					if (parser.getName().equalsIgnoreCase("bell") && currentMap != null) {
						currentList.add(currentMap);
						currentMap = null;
					}

					break;

				}

				eventType = parser.next();
			}
			inStream.close();
			return currentList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
