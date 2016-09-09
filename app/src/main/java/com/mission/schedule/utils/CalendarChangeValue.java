package com.mission.schedule.utils;
/**
 * 农历转化阳历格式 （正月初一 ，01-01）
 * @author Administrator
 *
 */
public class CalendarChangeValue {

	String y[] = { "正月","冬月", "腊月", "十一月", "十二月","一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月",
			"九月", "十月"  };
	String ys[] = { "01","11", "12","11", "12", "01", "02", "03", "04", "05", "06", "07", "08",
			"09", "10"  };
	String r[] = { "初", "廿", "三","二" ,"十" };
	String rs[] = { "一", "二", "三", "四", "五", "六", "七", "八", "九" };
	/**
	 * 农历转译成数字格式   （正月初一 ，01-01）
	 * @param val
	 * @return
	 */
	public  String changaSZ(String val) {

		for (int i = 0; i < y.length; i++) {
			if(val.indexOf(y[i])!=-1){
				val = val.replace(y[i], ys[i] + "-");
				break;
			}
		}
		for (int i = 0; i < r.length; i++) {
			for (int j = 0; j < rs.length; j++) {
				String newStr = "";
				if (i == 0) {
					newStr = "0";
				} else if (i == 1 || i == 2) {
					newStr = (i + 1) + "";
				} else if (i == 4) {
					newStr = "1";
				}else{
					newStr="2";
				}
				if (val.indexOf(r[i] + "" + rs[j]) != -1) {
					
						val = val.replace(r[i]+ rs[j] + "", newStr + "" + (j + 1));
					break;
					
				}else if(val.indexOf(r[i] + "十" + rs[j]) != -1){
				
					val = val.replace(r[i] +"十" + rs[j] + "", newStr + "" + (j + 1));
					break;
				}
			}
		}
		val = val.replace("初十", "10").replace("廿十", "20").replace("二十", "20").replace("三十", "30");
		return val;
	}
	/**
	 * 数字转译成农历（01-01，正月初一）
	 * @param val
	 * @return
	 */
	public String changNL(String val){
		if(val.indexOf("-")!=-1){
		String mon=val.substring(0,val.indexOf("-"));
		for(int i=0;i<ys.length;i++){
			if(mon.equals(ys[i])){
				mon=mon.replace(mon, y[i]);
				val=mon+val.substring(val.indexOf("-"));
			}
		}
		
		String day=val.substring(val.indexOf("-")+1);
	
		if(day!=null&&!day.equals("")){
			int d=Integer.parseInt(day);
			for(int i=0;i<rs.length;i++){
				int ii=0;
				if(d<10){
					ii=i+1;
					if(d==ii){
						val=val.replace(day, "初"+rs[i]);
						break;
					}
				}else if(d<20){
					ii=i+1+10;
					if(d==ii){
						val=val.replace(day, "十"+rs[i]);
						break;
					}
				}else if(d<30){
					ii=i+1+20;
					if(d==ii){
						val=val.replace(day, "廿"+rs[i]);
						break;
					}
				}else{
					break;
				}
			}
			val=val.replace("10", "初十").replace("20", "廿十").replace("30", "三十").replace("-","");
		}
		}
		return val;
	}
	

	public static void main(String[] args) {
		CalendarChangeValue t=new CalendarChangeValue();
		System.out.println(t.changaSZ("冬月二十五"));
		System.out.println(t.changNL("12-20"));
		/**System.out.println(t.changNL("11-11"));*/
	}

	
}
