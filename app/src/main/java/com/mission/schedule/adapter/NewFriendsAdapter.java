package com.mission.schedule.adapter;

import android.content.Context;

import com.mission.schedule.adapter.utils.CommonAdapter;
import com.mission.schedule.adapter.utils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/24.
 */
public class NewFriendsAdapter extends CommonAdapter<Map<String,String>> {

    public NewFriendsAdapter(Context context, List<Map<String, String>> lDatas, int layoutItemID) {
        super(context, lDatas, layoutItemID);
    }

    @Override
    public void getViewItem(ViewHolder holder, Map<String, String> item, int position) {

    }
}
