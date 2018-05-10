package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.AddRoomAdapter;
import com.massky.sraumsmarthome.adapter.AddSelectZigbeeDevAdapter;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/3.
 */

public class SelectZigbeeDeviceActivity extends BaseActivity{
    @InjectView(R.id.status_view)
    StatusView statusView;


    @InjectView(R.id.maoyan_control)
    LinearLayout maoyan_control;
    @InjectView(R.id.shexiangtou_control)
    LinearLayout shexiangtou_control;
    @InjectView(R.id.yinyue_control)
    LinearLayout yinyue_control;
    @InjectView(R.id.back)
    ImageView back;

    private List<Map> dataSourceList = new ArrayList<>();
    private AddSelectZigbeeDevAdapter addselectzigbeedevAdapter;

    @InjectView(R.id.mineRoom_list)
    GridView mineRoom_list;

    @Override
    protected int viewId() {
        return R.layout.add_device_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        maoyan_control.setOnClickListener(this);
        shexiangtou_control.setOnClickListener(this);
        yinyue_control.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        HashMap<String, Object> itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_kongtiao_active);
        itemHashMap.put("device_name", "空调面板");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_pm25_active);
        itemHashMap.put("device_name", "PM2.5");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_chuanglian_active);
        itemHashMap.put("device_name", "窗帘面板");
        dataSourceList.add(itemHashMap);

//        itemHashMap = new HashMap<>();
//        itemHashMap.put("item_image", R.drawable.icon_menci_active);
//        itemHashMap.put("device_name", "门磁");
//        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_deng_active);
        itemHashMap.put("device_name", "灯控面板");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_wangguan);
        itemHashMap.put("device_name", "网关");
        dataSourceList.add(itemHashMap);

        addselectzigbeedevAdapter = new AddSelectZigbeeDevAdapter(this,dataSourceList);
        mineRoom_list.setAdapter(addselectzigbeedevAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.maoyan_control:
            case R.id.shexiangtou_control:
            case R.id.yinyue_control:
                startActivity(new Intent(SelectZigbeeDeviceActivity.this,
                        AddWifiDeviceActivity.class));
                break;

            case R.id.back:
                SelectZigbeeDeviceActivity.this.finish();
                break;
        }
    }
}
