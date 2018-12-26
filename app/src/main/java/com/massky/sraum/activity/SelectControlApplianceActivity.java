package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.SelectWifiDevAdapter;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/3.
 */

public class SelectControlApplianceActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.mac_wifi_dev_id)
    GridView mac_wifi_dev_id;
    @InjectView(R.id.back)
    ImageView back;

    //wifi类型
    private String[] types_wifi = { //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            "202", "206","204","210"
    };

    private int[] icon_wifi = {
            R.drawable.icon_type_dianshiji_90,
            R.drawable.icon_type_kongtiao_90,
            R.drawable.icon_type_touyingyi_90,
            R.drawable.icon_type_yinxiang_90
    };
    private int[] iconNam_wifi = {R.string.dianshiji, R.string.kongtiao,
    R.string.touyingyi,R.string.yinxiang};

    private SelectWifiDevAdapter adapter_wifi;
    private DialogUtil dialogUtil;

    @Override
    protected int viewId() {
        return R.layout.sel_control_appliance_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        dialogUtil = new DialogUtil(SelectControlApplianceActivity.this);
        adapter_wifi = new SelectWifiDevAdapter(SelectControlApplianceActivity.this, icon_wifi, iconNam_wifi);
        mac_wifi_dev_id.setAdapter(adapter_wifi);

//        mac_wifi_dev_id.setAdapter(adapter);//Wi-Fi设备

        mac_wifi_dev_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_wifi = null;
                String tid = "";
                switch (types_wifi[position]) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]，
                    // DeviceType [tid=5, name=投影仪]，DeviceType [tid=13, name=音响]
                    case "202":
                        tid = "2";
                        break;
                    case "206":
                        tid = "7";
                        break;
                    case "204":
                        tid = "5";
                        break;//13
                    case "210":
                        tid = "13";
                        break;//13
                }
                intent_wifi = new Intent(SelectControlApplianceActivity.this, YKCodeAPIActivity.class);
                intent_wifi.putExtra("tid", tid);
                intent_wifi.putExtra("GizWifiDevice", getIntent().getParcelableExtra(
                        "GizWifiDevice"));
                intent_wifi.putExtra("number", getIntent().getSerializableExtra("number"));
                // intent.putExtra("number", list_hand_scene.get(position).get("number").toString())
                startActivity(intent_wifi);
            }
        });
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectControlApplianceActivity.this.finish();
                break;
        }
    }
}
