package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/6/19.
 */

public class OpenSmartDoorLockActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.project_select)
    TextView project_select;

    private String condition = "0";
    private Map map_link = new HashMap();
    private Map air_control_map = new HashMap();
    private Map sensor_map = new HashMap();
    @InjectView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;
    @InjectView(R.id.panel_scene_name_txt)
    TextView panel_scene_name_txt;

    @Override
    protected int viewId() {
        return R.layout.open_smart_door_lock_lay;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        onEvent();
        air_control_map = (Map) getIntent().getSerializableExtra("air_control_map");
        sensor_map = (Map) getIntent().getSerializableExtra("sensor_map");
        air_control_map.put("name1", air_control_map.get("name"));
        project_select.setText(air_control_map.get("name").toString());
        switch (air_control_map.get("type").toString()) {
            case "15":
                panel_scene_name_txt.setText("打开");
                break;
            case "16":
                panel_scene_name_txt.setText("关闭");
                break;
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        rel_scene_set.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                OpenSmartDoorLockActivity.this.finish();
                break;
            case R.id.rel_scene_set:
                init_intent();
                break;
        }
    }

    private void init_intent() {
        init_action();
        AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity.class);
        AppManager.getAppManager().finishActivity_current(SelectiveLinkageDeviceDetailSecondActivity.class);
        AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
        Intent intent = new Intent(
                OpenSmartDoorLockActivity.this,
                EditLinkDeviceResultActivity.class);
        intent.putExtra("device_map", (Serializable) air_control_map);
        intent.putExtra("sensor_map", (Serializable) sensor_map);
        OpenSmartDoorLockActivity.this.finish();
        startActivity(intent);
    }

    private void init_action() {
        switch (air_control_map.get("type").toString()) {
            case "15":
                air_control_map.put("action", "打开");
                air_control_map.put("status", "1");
                break;
            case "16":
                air_control_map.put("action", "关闭");
                air_control_map.put("status", "0");
                break;
        }
    }
}
