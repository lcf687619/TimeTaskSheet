package com.mission.schedule.utils;

import com.mission.schedule.applcation.App;
import com.mission.schedule.entity.CLRepeatTable;
import com.mission.schedule.entity.ScheduleTable;

import java.util.Map;

/**
 * Created by lenovo on 2016/8/2.
 */
public class RepeatSetChildEndUtils {
    App application  = App.getDBcApplication();
    public void setParentStateIsEnd(Map<String,String> mMap){
        Map<String, String> map = application.QueryStateData(Integer
                .parseInt(mMap.get(ScheduleTable.schRepeatID)));
        if (map != null) {
            String lastdate = StringUtils.getIsStringEqulesNull(map
                    .get(CLRepeatTable.repDateOne));
            String nextdate = StringUtils.getIsStringEqulesNull(map
                    .get(CLRepeatTable.repDateTwo));
            String repdate = mMap.get(ScheduleTable.schRepeatDate);
            if (repdate.equals(lastdate) || repdate.equals(nextdate)) {
                if (!"".equals(lastdate)&& repdate.equals(lastdate)) {
                    if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                        App.getDBcApplication().updateSchCLRepeatData(
                                Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)),
                                mMap.get(ScheduleTable.schRepeatDate),
                                map.get(CLRepeatTable.repDateTwo),
                                3,
                                Integer.parseInt(map
                                        .get(CLRepeatTable.repStateTwo)));
                    } else {
                        App.getDBcApplication().updateSchCLRepeatData(
                                Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)),
                                mMap.get(ScheduleTable.schRepeatDate),
                                map.get(CLRepeatTable.repDateTwo),
                                0,
                                Integer.parseInt(map
                                        .get(CLRepeatTable.repStateTwo)));
                    }
                } else if (!"".equals(nextdate) && repdate.equals(nextdate)) {
                    if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                        App.getDBcApplication()
                                .updateSchCLRepeatData(
                                        Integer.parseInt(mMap
                                                .get(ScheduleTable.schRepeatID)),
                                        map.get(CLRepeatTable.repDateOne),
                                        mMap.get(ScheduleTable.schRepeatDate),
                                        Integer.parseInt(map
                                                .get(CLRepeatTable.repStateOne)),
                                        3);
                    } else {
                        App.getDBcApplication()
                                .updateSchCLRepeatData(
                                        Integer.parseInt(mMap
                                                .get(ScheduleTable.schRepeatID)),
                                        map.get(CLRepeatTable.repDateOne),
                                        mMap.get(ScheduleTable.schRepeatDate),
                                        Integer.parseInt(map
                                                .get(CLRepeatTable.repStateOne)),
                                        0);
                    }
                }
            } else {
                if ("".equals(lastdate) && "".equals(nextdate)) {
                    if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                        App.getDBcApplication().updateSchCLRepeatData(
                                Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)),
                                mMap.get(ScheduleTable.schRepeatDate),
                                map.get(CLRepeatTable.repDateTwo),
                                3,
                                Integer.parseInt(map
                                        .get(CLRepeatTable.repStateTwo)));
                    } else {
                        App.getDBcApplication().updateSchCLRepeatData(
                                Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)),
                                mMap.get(ScheduleTable.schRepeatDate),
                                map.get(CLRepeatTable.repDateTwo),
                                0,
                                Integer.parseInt(map
                                        .get(CLRepeatTable.repStateTwo)));
                    }
                } else if ("".equals(lastdate) && !"".equals(nextdate)) {
                    if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                        App.getDBcApplication().updateSchCLRepeatData(
                                Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)),
                                mMap.get(ScheduleTable.schRepeatDate),
                                map.get(CLRepeatTable.repDateTwo),
                                3,
                                Integer.parseInt(map
                                        .get(CLRepeatTable.repStateTwo)));
                    } else {
                        App.getDBcApplication().updateSchCLRepeatData(
                                Integer.parseInt(mMap
                                        .get(ScheduleTable.schRepeatID)),
                                mMap.get(ScheduleTable.schRepeatDate),
                                map.get(CLRepeatTable.repDateTwo),
                                0,
                                Integer.parseInt(map
                                        .get(CLRepeatTable.repStateTwo)));
                    }
                } else if (!"".equals(lastdate) && "".equals(nextdate)) {
                    if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                        App.getDBcApplication()
                                .updateSchCLRepeatData(
                                        Integer.parseInt(mMap
                                                .get(ScheduleTable.schRepeatID)),
                                        map.get(CLRepeatTable.repDateOne),
                                        mMap.get(ScheduleTable.schRepeatDate),
                                        Integer.parseInt(map
                                                .get(CLRepeatTable.repStateOne)),
                                        3);
                    } else {
                        App.getDBcApplication()
                                .updateSchCLRepeatData(
                                        Integer.parseInt(mMap
                                                .get(ScheduleTable.schRepeatID)),
                                        map.get(CLRepeatTable.repDateOne),
                                        mMap.get(ScheduleTable.schRepeatDate),
                                        Integer.parseInt(map
                                                .get(CLRepeatTable.repStateOne)),
                                        0);
                    }
                } else {
                    if (DateUtil.parseDateTime(lastdate).getTime() > DateUtil
                            .parseDateTime(nextdate).getTime()) {
                        if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                            App.getDBcApplication()
                                    .updateSchCLRepeatData(
                                            Integer.parseInt(mMap
                                                    .get(ScheduleTable.schRepeatID)),
                                            map.get(CLRepeatTable.repDateOne),
                                            mMap.get(ScheduleTable.schRepeatDate),
                                            Integer.parseInt(map
                                                    .get(CLRepeatTable.repStateOne)),
                                            3);
                        } else {
                            App.getDBcApplication()
                                    .updateSchCLRepeatData(
                                            Integer.parseInt(mMap
                                                    .get(ScheduleTable.schRepeatID)),
                                            map.get(CLRepeatTable.repDateOne),
                                            mMap.get(ScheduleTable.schRepeatDate),
                                            Integer.parseInt(map
                                                    .get(CLRepeatTable.repStateOne)),
                                            0);
                        }
                    } else {
                        if ("0".equals(mMap.get(ScheduleTable.schIsEnd))) {
                            App.getDBcApplication()
                                    .updateSchCLRepeatData(
                                            Integer.parseInt(mMap
                                                    .get(ScheduleTable.schRepeatID)),
                                            mMap.get(ScheduleTable.schRepeatDate),
                                            map.get(CLRepeatTable.repDateTwo),
                                            3,
                                            Integer.parseInt(map
                                                    .get(CLRepeatTable.repStateTwo)));
                        } else {
                            App.getDBcApplication()
                                    .updateSchCLRepeatData(
                                            Integer.parseInt(mMap
                                                    .get(ScheduleTable.schRepeatID)),
                                            mMap.get(ScheduleTable.schRepeatDate),
                                            map.get(CLRepeatTable.repDateTwo),
                                            0,
                                            Integer.parseInt(map
                                                    .get(CLRepeatTable.repStateTwo)));
                        }
                    }
                }
            }
        }
    }
    public void setParentState(int repid,String repdate,String nextdate,String lastdate,Map<String,String>map){
        if (repdate.equals(lastdate) || repdate.equals(nextdate)) {
            if (!"".equals(lastdate)&& repdate.equals(lastdate)) {
                application.updateSchCLRepeatData(
                        repid,
                        repdate,
                        map.get(CLRepeatTable.repDateTwo),
                        1,
                        Integer.parseInt(map
                                .get(CLRepeatTable.repStateTwo)));
            } else if (!"".equals(nextdate) && repdate.equals(nextdate)) {
                application.updateSchCLRepeatData(
                        repid,
                        map.get(CLRepeatTable.repDateOne),
                        repdate,
                        Integer.parseInt(map
                                .get(CLRepeatTable.repStateOne)),
                        1);
            }
        } else {
            if ("".equals(lastdate) && "".equals(nextdate)) {
                application.updateSchCLRepeatData(
                        repid,
                        repdate,
                        map.get(CLRepeatTable.repDateTwo),
                        1,
                        Integer.parseInt(map
                                .get(CLRepeatTable.repStateTwo)));
            } else if ("".equals(lastdate) && !"".equals(nextdate)) {
                application.updateSchCLRepeatData(
                        repid,
                        repdate,
                        map.get(CLRepeatTable.repDateTwo),
                        1,
                        Integer.parseInt(map
                                .get(CLRepeatTable.repStateTwo)));
            } else if (!"".equals(lastdate) && "".equals(nextdate)) {
                application.updateSchCLRepeatData(
                        repid,
                        map.get(CLRepeatTable.repDateOne),
                        repdate,
                        Integer.parseInt(map
                                .get(CLRepeatTable.repStateOne)),
                        1);
            } else {
                if (DateUtil.parseDateTime(lastdate).getTime() > DateUtil
                        .parseDateTime(nextdate).getTime()) {
                    application.updateSchCLRepeatData(
                            repid,
                            map.get(CLRepeatTable.repDateOne),
                            repdate,
                            Integer.parseInt(map
                                    .get(CLRepeatTable.repStateOne)),
                            1);
                } else {
                    application.updateSchCLRepeatData(
                            repid,
                            repdate,
                            map.get(CLRepeatTable.repDateTwo),
                            1,
                            Integer.parseInt(map
                                    .get(CLRepeatTable.repStateTwo)));
                }
            }
        }
    }
}
