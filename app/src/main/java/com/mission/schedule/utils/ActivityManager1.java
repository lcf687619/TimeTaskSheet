package com.mission.schedule.utils;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityManager1 {
    private static ActivityManager1 instance;
    private static List<Activity> activities;

    public static ActivityManager1 getInstance() {
        if (instance == null) {
            synchronized (ActivityManager1.class) {
                if (instance == null) {
                    instance = new ActivityManager1();
                    activities = new ArrayList<Activity>();
                }
            }
        }
        return instance;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void doAllActivityFinish() {
        for (int i = 0; i < getActivities().size(); i++) {
            activities.get(i).finish();
        }
        activities.clear();
    }

    public void addActivities(Activity activitie) {
        activities.add(activitie);
    }

    //防止反序列化重新生成新的实例
    private Object readResolve() throws ObjectStreamException {
        return instance;
    }
}
