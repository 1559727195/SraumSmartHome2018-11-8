package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/5.
 */

public class HandAddSceneDeviceDetailActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.air_control_linear)
    LinearLayout air_control_linear;//空调
    @InjectView(R.id.window_linear)
    LinearLayout window_linear;//窗帘
    @InjectView(R.id.tiaoguangdeng_linear)
    LinearLayout tiaoguangdeng_linear;//调光灯
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.project_select)
    TextView project_select;
    private List<Map> list_scene = new ArrayList<>();
    private List<User.device> deviceList = new ArrayList<>();
    private Map device_map = new HashMap();
    private String panelStatus;
    private String panelType;
    private String panelName;
    private String panelMAC;

    @Override
    protected int viewId() {
        return R.layout.handaddscene_devdetail;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        String type = (String) getIntent().getSerializableExtra("type");
//        String name = (String) getIntent().getSerializableExtra("name");
//        project_select.setText(name);


        deviceList = (List<User.device>) getIntent().getSerializableExtra("deviceList");
        device_map = (Map) getIntent().getSerializableExtra("device_map");
        panelType = device_map.get("panelType").toString();
        panelName = device_map.get("panelName").toString();
        panelMAC = device_map.get("panelMAC").toString();
        panelStatus = device_map.get("panelStatus").toString();


        switch (panelType) {
            case "A204"://灯光
                break;
            case "A501"://空调
                air_control_linear.setVisibility(View.VISIBLE);
                break;
            case "A401"://窗帘
                window_linear.setVisibility(View.VISIBLE);
                break;
            case "A303"://调光灯
                tiaoguangdeng_linear.setVisibility(View.VISIBLE);

                break;

        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                HandAddSceneDeviceDetailActivity.this.finish();
                break;
            case R.id.next_step_txt:
                HandAddSceneDeviceDetailActivity.this.finish();
                break;
        }
    }
}
