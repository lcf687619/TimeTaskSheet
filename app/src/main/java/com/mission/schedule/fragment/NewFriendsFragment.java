package com.mission.schedule.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mission.schedule.R;
import com.mission.schedule.adapter.NewFriendsAdapter;
import com.mission.schedule.constants.FristFragment;

import de.greenrobot.event.EventBus;

/**
 * Created by lenovo on 2016/8/24.
 */
public class NewFriendsFragment extends BaseFragment implements View.OnClickListener {

    private boolean isShow = false;// 判断是否已经显示
    Context context;
    public static LinearLayout top_ll_left;
    private TextView middleText_tv;
    RelativeLayout my_friend_ll_right;
    private LinearLayout myfriend_ll;
    private ImageView moreDown_iv;
    NewFriendsAdapter adapter = null;


    @Override
    protected void lazyLoad() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newfriends, container, false);
    }
    /**
     * 第一次进入界面加载
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !isShow) {
            isShow = true;
            init();
            loadData();
            setAdapter();
        } else {
        }
    }

    /**
     * 初始化数据，控件
     */
    private void init() {
        EventBus.getDefault().register(this);
        View view = getView();
        context = getActivity();
        top_ll_left = null;
        top_ll_left = (LinearLayout) view.findViewById(R.id.top_ll_left);
        top_ll_left.setOnClickListener(this);
        middleText_tv = (TextView) view.findViewById(R.id.myschedule_title);
        middleText_tv.setText(R.string.mysendtask);
        my_friend_ll_right = (RelativeLayout) view
                .findViewById(R.id.my_friend_ll_right);
        my_friend_ll_right.setOnClickListener(this);
        myfriend_ll = (LinearLayout) view.findViewById(R.id.myfriend_ll);
        myfriend_ll.setOnClickListener(this);
        moreDown_iv = (ImageView) view.findViewById(R.id.iv_more_down);
        moreDown_iv.setOnClickListener(this);

    }

    /**
     * 从数据库读取数据
     */
    private void loadData() {

    }
    /**
     * 设置适配器
     */
    private void setAdapter() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_friend_ll_right://点击右上方添加

                break;
            case R.id.myfriend_ll://点击头部进行切换

                break;
            case R.id.iv_more_down://点击箭头进行切换

                break;
        }
    }
    public void onEventMainThread(FristFragment event) {

        String msg = event.getMsg();
        if ("3".equals(msg)&&isShow) {
            loadData();
            adapter.notifyDataSetChanged();
        }
    }
}
