package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.massky.sraum.R;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.adapter.AddRoomAdapter;
import com.massky.sraum.adapter.AddSelectZigbeeDevAdapter;
import com.massky.sraum.adapter.SelectDevTypeAdapter;
import com.massky.sraum.adapter.SelectWifiDevAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ListViewForScrollView;
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

public class SelectZigbeeDeviceActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;


    //    @InjectView(R.id.maoyan_control)
//    LinearLayout maoyan_control;
//    @InjectView(R.id.shexiangtou_control)
//    LinearLayout shexiangtou_control;
//    @InjectView(R.id.yinyue_control)
//    LinearLayout yinyue_control;
    @InjectView(R.id.back)
    ImageView back;

    private List<Map> dataSourceList = new ArrayList<>();


    @InjectView(R.id.mineRoom_list)
    ListViewForScrollView macfragritview_id;
    @InjectView(R.id.mac_wifi_dev_id)
    ListViewForScrollView mac_wifi_dev_id;
    private SelectWifiDevAdapter adapter_wifi;

    private int[] icon = {
            R.drawable.icon_deng_active, R.drawable.icon_pm25_active,
            R.drawable.icon_wangguan, R.drawable.icon_chuanglian_active, R.drawable.icon_kongtiao_active
    };

    private int[] icon_wifi = {
            R.drawable.icon_maoyan, R.drawable.icon_jiankong_active,
            R.drawable.icon_yinyue
    };

    private int[] iconName = {R.string.light_panel, R.string.pm25, R.string.wangguan, R.string.chuanglian,
            R.string.kongtiao_panel, R.string.lianglutiaoguang1, R.string.sanlutiao1, R.string.window_panel1
            , R.string.menci, R.string.rentiganying, R.string.jiuzuo, R.string.yanwu, R.string.tianranqi, R.string.jinjin_btn,
            R.string.zhineng, R.string.pm25, R.string.shuijin, R.string.duogongneng
    };

    private int[] iconNam_wifi = {R.string.dianzimao, R.string.zhinengcamera, R.string.yinyuepanel, R.string.keshimenling, R.string.kongtiao};

    //"B301"暂时为多功能模块
    private String[] types = {
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A401",
            "A801", "A901", "A902", "AB01", "AB04", "B001", "B201", "AD01", "AC01", "B301", "网关"
    };


//    private int[] icon_wifi = { // ---
//            R.drawable.icon_kongtiao_active,
//            R.drawable.icon_pm25_active,
//            R.drawable.icon_chuanglian_active
//            ,
//            R.drawable.icon_deng_active,
//            R.drawable.icon_wangguan
//
//    };

    private SelectDevTypeAdapter adapter;

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
//        maoyan_control.setOnClickListener(this);
//        shexiangtou_control.setOnClickListener(this);
//        yinyue_control.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
//        HashMap<String, Object> itemHashMap = new HashMap<>();
//        itemHashMap.put("item_image", R.drawable.icon_kongtiao_active);
//        itemHashMap.put("device_name", "空调面板");
//        dataSourceList.add(itemHashMap);
//
//        itemHashMap = new HashMap<>();
//        itemHashMap.put("item_image", R.drawable.icon_pm25_active);
//        itemHashMap.put("device_name", "PM2.5");
//        dataSourceList.add(itemHashMap);
//
//        itemHashMap = new HashMap<>();
//        itemHashMap.put("item_image", R.drawable.icon_chuanglian_active);
//        itemHashMap.put("device_name", "窗帘面板");
//        dataSourceList.add(itemHashMap);
//
////        itemHashMap = new HashMap<>();
////        itemHashMap.put("item_image", R.drawable.icon_menci_active);
////        itemHashMap.put("device_name", "门磁");
////        dataSourceList.add(itemHashMap);
//
//        itemHashMap = new HashMap<>();
//        itemHashMap.put("item_image", R.drawable.icon_deng_active);
//        itemHashMap.put("device_name", "灯控面板");
//        dataSourceList.add(itemHashMap);
//
//        itemHashMap = new HashMap<>();
//        itemHashMap.put("item_image", R.drawable.icon_wangguan);
//        itemHashMap.put("device_name", "网关");
//        dataSourceList.add(itemHashMap);

//        addselectzigbeedevAdapter = new AddSelectZigbeeDevAdapter(this,dataSourceList);
//        mineRoom_list.setAdapter(addselectzigbeedevAdapter);

   /*     dialogUtil = new DialogUtil(SelectZigbeeDeviceActivity.this);
        adapter = new SelectDevTypeAdapter(SelectZigbeeDeviceActivity.this, icon, iconName);
        macfragritview_id.setAdapter(adapter);*/
        adapter = new SelectDevTypeAdapter(SelectZigbeeDeviceActivity.this, icon, iconName);
        macfragritview_id.setAdapter(adapter);


        adapter_wifi = new SelectWifiDevAdapter(SelectZigbeeDeviceActivity.this, icon_wifi, iconNam_wifi);
        mac_wifi_dev_id.setAdapter(adapter_wifi);
        macfragritview_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent_position = null;
//                switch (types[position]) {
//                    case "A501":
//                    case "A511":
//                        break;
//                    default:
//                        intent_position = new Intent(SelectZigbeeDeviceActivity.this, AddZigbeeDevActivity.class);
//                        intent_position.putExtra("type", types[position]);
//                        startActivity(intent_position);
//                        break;
//                }
                switch (types[position]) {
                    case "A201":
                    case "A202":
                    case "A203":
                    case "A204":
                    case "A301":
                    case "A302":
                    case "A303":
                    case "A401":
                    case "B301":
                    case "A902":
                    case "AD01":
                    case "A501":
//                    case "A511":
//                        sraum_setBox_accent(types[position], "normal");
                        break;
                    case "A801":
                    case "A901":
                    case "AB01":
                    case "AB04":
                    case "B001":
                    case "AC01":
//                        sraum_setBox_accent(types[position], "zigbee");
                        break;
                    case "B201":
//                        Intent intent_position = new Intent(SelectZigbeeDeviceActivity.this, SelectSmartDoorLockActivity.class);
//                        intent_position.putExtra("type", types[position]);
//                        startActivity(intent_position);
                        break;
                    case "网关":
                        break;//添加网关
                }
                ToastUtil.showToast(SelectZigbeeDeviceActivity.this, "添加网关");
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.maoyan_control:
//            case R.id.shexiangtou_control:
//            case R.id.yinyue_control:
//                startActivity(new Intent(SelectZigbeeDeviceActivity.this,
//                        AddWifiDeviceActivity.class));
//                break;

            case R.id.back:
                SelectZigbeeDeviceActivity.this.finish();
                break;
        }
    }
}
