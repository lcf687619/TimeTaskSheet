package com.mission.schedule.data;

import java.util.ArrayList;
import java.util.List;

import com.mission.schedule.bean.ChangYongBean;

public class MyData {
	
	public static List<ChangYongBean> getDaoJiShiList(){
		List<ChangYongBean> daojiList = new ArrayList<ChangYongBean>();
		ChangYongBean bean = new ChangYongBean();
		bean.title = "15";
		bean.content = "分钟";
		ChangYongBean bean1 = new ChangYongBean();
		bean1.title = "30";
		bean1.content = "分钟";
		ChangYongBean bean2 = new ChangYongBean();
		bean2.title = "45";
		bean2.content = "分钟";
		ChangYongBean bean3 = new ChangYongBean();
		bean3.title = "1";
		bean3.content = "小时";
		ChangYongBean bean4 = new ChangYongBean();
		bean4.title = "1.5";
		bean4.content = "小时";
		ChangYongBean bean5 = new ChangYongBean();
		bean5.title = "2";
		bean5.content = "小时";
		daojiList.add(bean);
		daojiList.add(bean1);
		daojiList.add(bean2);
		daojiList.add(bean3);
		daojiList.add(bean4);
		daojiList.add(bean5);
		
		return daojiList;
	}
	
	public static List<ChangYongBean> getChangYongList(){
		List<ChangYongBean> changyongList = new ArrayList<ChangYongBean>();
		ChangYongBean bean = new ChangYongBean();
		bean.title = "明天";
		bean.content = "开会";
		ChangYongBean bean1 = new ChangYongBean();
		bean1.title = "明天";
		bean1.content = "充值缴费";
		ChangYongBean bean2 = new ChangYongBean();
		bean2.title = "明天";
		bean2.content = "带东西";
		ChangYongBean bean3 = new ChangYongBean();
		bean3.title = "明天";
		bean3.content = "上课";
		ChangYongBean bean4 = new ChangYongBean();
		bean4.title = "明天";
		bean4.content = "会面";
		ChangYongBean bean5 = new ChangYongBean();
		bean5.title = "明天";
		bean5.content = "取东西";
		changyongList.add(bean);
		changyongList.add(bean1);
		changyongList.add(bean2);
		changyongList.add(bean3);
		changyongList.add(bean4);
		changyongList.add(bean5);
		
		return changyongList;
	}
	public static List<ChangYongBean> getChangYongWakeUpList(){
		List<ChangYongBean> changyongList = new ArrayList<ChangYongBean>();
		ChangYongBean bean = new ChangYongBean();
		bean.title = "早上";
		bean.content = "5:00";
		ChangYongBean bean1 = new ChangYongBean();
		bean1.title = "早上";
		bean1.content = "5:30";
		ChangYongBean bean2 = new ChangYongBean();
		bean2.title = "早上";
		bean2.content = "6:30";
		ChangYongBean bean3 = new ChangYongBean();
		bean3.title = "早上";
		bean3.content = "7:00";
		ChangYongBean bean4 = new ChangYongBean();
		bean4.title = "早上";
		bean4.content = "7:30";
		ChangYongBean bean5 = new ChangYongBean();
		bean5.title = "早上";
		bean5.content = "8:00";
		changyongList.add(bean);
		changyongList.add(bean1);
		changyongList.add(bean2);
		changyongList.add(bean3);
		changyongList.add(bean4);
		changyongList.add(bean5);
		
		return changyongList;
	}
}
