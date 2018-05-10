package com.massky.sraumsmarthome.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.AutoSceneAdapter;
import com.massky.sraumsmarthome.base.BaseFragment1;
import com.massky.sraumsmarthome.event.MyDialogEvent;
import com.massky.sraumsmarthome.view.XListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;

/**
 * Created by zhu on 2017/11/30.
 */

public class AutoSceneFragment extends BaseFragment1  implements XListView.IXListViewListener {
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private  String [] autoElements  = {"人体传感联动","厨房联动","PM2.5联动","地下室","防盗门打开"
            ,"漏水"};
    private List<Map> list_hand_scene = new ArrayList<>();
    private AutoSceneAdapter autoSceneAdapter;

    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {

    }

    @Override
    public void onEvent(MyDialogEvent eventData) {

    }

    @Override
    protected int viewId() {
        return R.layout.auto_scene_lay;
    }

    @Override
    protected void onView(View view) {
        list_hand_scene = new ArrayList<>();
        for (String element : autoElements) {
            Map map = new HashMap();
            map.put("name",element);
            switch (element) {
                case "人体传感联动":
                    map.put("image",R.drawable.icon_deng_sm);
                    break;
                case "厨房联动":
                    map.put("image",R.drawable.icon_guandeng_sm);
                    break;
                case "PM2.5联动":
                    map.put("image",R.drawable.icon_huijia_sm);
                    break;
                case "地下室":
                    map.put("image",R.drawable.icon_lijia_sm);
                    break;
                case "防盗门打开":
                    map.put("image",R.drawable.icon_mensuo_sm);
                    break;
                case "漏水":
                    map.put("image",R.drawable.icon_shujin_sm);
                    break;
            }
            list_hand_scene.add(map);
        }

        autoSceneAdapter = new AutoSceneAdapter(getActivity(), list_hand_scene);
        xListView_scan.setAdapter(autoSceneAdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {

    }

    public static AutoSceneFragment newInstance() {
        AutoSceneFragment newFragment = new AutoSceneFragment();
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
}
