package com.massky.sraum.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.massky.sraum.R;
import com.massky.sraum.adapter.AutoSceneAdapter;
import com.massky.sraum.adapter.DeviceMessageAdapter;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;
import com.massky.sraum.view.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;


/**
 * Created by zhu on 2017/11/30.
 */

public class DevicePagerFragment extends BaseFragment1  implements XListView.IXListViewListener{
    private List<Map> list_alarm = new ArrayList<>();
    private int currentpage = 1;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private  String [] autoElements  = {"人体传感联动","厨房联动","PM2.5联动","地下室","防盗门打开"
            ,"漏水"};
    private List<Map> list_hand_scene = new ArrayList<>();
    private DeviceMessageAdapter devicemessager;

    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {

    }

    @Override
    public void onEvent(MyDialogEvent eventData) {
        currentpage = 1;
    }

    @Override
    protected int viewId() {
        return R.layout.new_fragment_lay;
    }

    @Override
    protected void onView(View view) {
        list_hand_scene = new ArrayList<>();
        for (int i = 0; i < autoElements.length; i++) {
            Map map = new HashMap();
            map.put("name", autoElements[i]);
            list_hand_scene.add(map);
        }

        devicemessager = new DeviceMessageAdapter(getActivity(), list_hand_scene);
        xListView_scan.setAdapter(devicemessager);
        xListView_scan.setPullLoadEnable(true);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {

    }

    public static DevicePagerFragment newInstance(int i) {
        DevicePagerFragment newFragment = new DevicePagerFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }

    @Override
    public void onRefresh() {
        onLoad();
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
