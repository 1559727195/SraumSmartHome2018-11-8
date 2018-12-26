package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.Util.SharedPreferencesUtil;
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

public class UnderWaterActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;
    @InjectView(R.id.checkbox)
    CheckBox checkbox;
    @InjectView(R.id.panel_scene_name_txt)
    TextView panel_scene_name_txt;
    @InjectView(R.id.project_select)
    TextView project_select;


    @InjectView(R.id.fangdao_linear)
    LinearLayout fangdao_linear;
    @InjectView(R.id.rel_fangdao_open)
    RelativeLayout rel_fangdao_open;
    @InjectView(R.id.door_open_txt)
    TextView door_open_txt;
    @InjectView(R.id.checkbox_fangdao_open)
    CheckBox checkbox_fangdao_open;
    @InjectView(R.id.rel_fangdao_close)
    RelativeLayout rel_fangdao_close;
    @InjectView(R.id.door_close_txt)
    TextView door_close_txt;
    @InjectView(R.id.checkbox_fangdao_close)
    CheckBox checkbox_fangdao_close;

    @InjectView(R.id.humancheck_linear)
    LinearLayout humancheck_linear;
    @InjectView(R.id.rel_humancheck)
    RelativeLayout rel_humancheck;
    @InjectView(R.id.humancheck_txt)
    TextView humancheck_txt;
    @InjectView(R.id.checkbox_humancheck)
    CheckBox checkbox_humancheck;


    @InjectView(R.id.jiuzuo_linear)
    LinearLayout jiuzuo_linear;
    @InjectView(R.id.rel_jiuzuo)
    RelativeLayout rel_jiuzuo;
    @InjectView(R.id.jiuzuo_txt)
    TextView jiuzuo_txt;
    @InjectView(R.id.checkbox_jiuzuo)
    CheckBox checkbox_jiuzuo;

    @InjectView(R.id.gas_linear)
    LinearLayout gas_linear;
    @InjectView(R.id.rel_gas)
    RelativeLayout rel_gas;
    @InjectView(R.id.gas_txt)
    TextView gas_txt;
    @InjectView(R.id.checkbox_gas)
    CheckBox checkbox_gas;

    @InjectView(R.id.smoke_linear)
    LinearLayout smoke_linear;
    @InjectView(R.id.rel_smoke)
    RelativeLayout rel_smoke;
    @InjectView(R.id.smoke_txt)
    TextView smoke_txt;
    @InjectView(R.id.checkbox_smoke)
    CheckBox checkbox_smoke;

    @InjectView(R.id.
            emergcybtn_linear)
    LinearLayout
            emergcybtn_linear;
    @InjectView(R.id.rel_emergcybtn)
    RelativeLayout rel_emergcybtn;
    @InjectView(R.id.emergcybtn_txt)
    TextView emergcybtn_txt;
    @InjectView(R.id.checkbox_emergcybtn)
    CheckBox checkbox_emergcybtn;
    private String condition = "0";
    private Map map_link = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.under_water_lay;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        rel_scene_set.setOnClickListener(this);
//        onEvent();
        map_link = (Map) getIntent().getSerializableExtra("map_link");
        if (map_link == null) return;
        setPicture(map_link.get("deviceType").toString());
    }

    @Override
    protected void onEvent() {
        rel_fangdao_open.setOnClickListener(this);
        rel_fangdao_close.setOnClickListener(this);
        rel_humancheck.setOnClickListener(this);
        rel_jiuzuo.setOnClickListener(this);
        rel_gas.setOnClickListener(this);
        rel_smoke.setOnClickListener(this);
        rel_emergcybtn.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    private void setPicture(String type) {
        switch (type) {
            case "7":
//                project_select.setText(map_link.get("name").toString());// project_select.setText(map_link.get("name").toString());
                fangdao_linear.setVisibility(View.VISIBLE);
                break;
            case "8":
                humancheck_linear.setVisibility(View.VISIBLE);
//                project_select.setText("人体检测");
                humancheck_txt.setText("有人经过");
                break;
            case "9":
                humancheck_linear.setVisibility(View.VISIBLE);
//                project_select.setText("主卧水浸");
                humancheck_txt.setText("报警");
                break;
//            case "10":
//
//                break;
            case "11":
                humancheck_linear.setVisibility(View.VISIBLE);
//                project_select.setText("紧急按钮");
                humancheck_txt.setText("按下");
                break;
            case "12":
                humancheck_linear.setVisibility(View.VISIBLE);
//                project_select.setText("如厕检测");
                humancheck_txt.setText("报警");
                break;
            case "13":
                humancheck_linear.setVisibility(View.VISIBLE);
//                project_select.setText("烟雾检测");
                humancheck_txt.setText("报警");
                break;
            case "14":
                humancheck_linear.setVisibility(View.VISIBLE);
//                project_select.setText("天然气检测");
                humancheck_txt.setText("报警");
                break;
            case "15":
//                project_select.setText("智能门锁");
                fangdao_linear.setVisibility(View.VISIBLE);
                break;
            case "16":

                break;
        }
        project_select.setText(map_link.get("name").toString());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                UnderWaterActivity.this.finish();
                break;
            case R.id.next_step_txt:
//                map_link.put("condition", condition);
//                map_link.put("minValue", "");
//                map_link.put("maxValue", "");
//                Intent intent = new Intent(UnderWaterActivity.this,
//                        SelectiveLinkageActivity.class);
//                intent.putExtra("link_map", (Serializable) map_link);
//                startActivity(intent);
                break;
//            case R.id.rel_scene_set:
//                checkbox.toggle();
//                if (checkbox.isChecked()) {
//                    panel_scene_name_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                } else {
//                    panel_scene_name_txt.setTextColor(getResources().getColor(R.color.black_color));
//                }
//                break;
            case R.id.rel_fangdao_open:
                condition = "1";
//                checkbox_fangdao_open.setChecked(true);
//                checkbox_fangdao_close.setChecked(false);
//                door_open_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                door_close_txt.setTextColor(getResources().getColor(R.color.black_color));
                init_intent();
                break;
            case R.id.rel_fangdao_close:
                condition = "0";
//                checkbox_fangdao_open.setChecked(false);
//                checkbox_fangdao_close.setChecked(true);
//                door_close_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                door_open_txt.setTextColor(getResources().getColor(R.color.black_color));
                init_intent();
                break;

            case R.id.rel_humancheck:
//                checkbox_humancheck.toggle();
//                if (checkbox_humancheck.isChecked()) {
//                    condition = "1";
//                    humancheck_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                } else {
//                    condition = "0";
//                    humancheck_txt.setTextColor(getResources().getColor(R.color.black_color));
//                }
                condition = "1";
                init_intent();
                break;

//            case R.id.rel_jiuzuo:
//                checkbox_jiuzuo.toggle();
//                if (checkbox_jiuzuo.isChecked()) {
//                    jiuzuo_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                } else {
//                    jiuzuo_txt.setTextColor(getResources().getColor(R.color.black_color));
//                }
//                break;
//            case R.id.rel_gas:
//                checkbox_gas.toggle();
//                if (checkbox_gas.isChecked()) {
//                    gas_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                } else {
//                    gas_txt.setTextColor(getResources().getColor(R.color.black_color));
//                }
//                break;
//            case R.id.rel_smoke:
//                checkbox_smoke.toggle();
//                if (checkbox_smoke.isChecked()) {
//                    smoke_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                } else {
//                    smoke_txt.setTextColor(getResources().getColor(R.color.black_color));
//                }
//                break;
//            case R.id.rel_emergcybtn:
//                checkbox_emergcybtn.toggle();
//                if (checkbox_emergcybtn.isChecked()) {
//                    emergcybtn_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                } else {
//                    emergcybtn_txt.setTextColor(getResources().getColor(R.color.black_color));
//                }
//                break;
        }
    }

    private void init_intent() {
        Intent intent = null;
        setAction(map_link.get("deviceType").toString(), condition);
        map_link.put("condition", condition);
        map_link.put("minValue", "");
        map_link.put("maxValue", "");
        map_link.put("name1", map_link.get("name"));

        boolean add_condition = (boolean) SharedPreferencesUtil.getData(UnderWaterActivity.this, "add_condition", false);
        if (add_condition) {
//            AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
            AppManager.getAppManager().finishActivity_current(SelectSensorActivity.class);
            AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
            intent = new Intent(UnderWaterActivity.this, EditLinkDeviceResultActivity.class);
            intent.putExtra("sensor_map", (Serializable) map_link);
            startActivity(intent);
            UnderWaterActivity.this.finish();
        } else {
            intent = new Intent(UnderWaterActivity.this,
                    SelectiveLinkageActivity.class);
            intent.putExtra("link_map", (Serializable) map_link);
            startActivity(intent);
        }
    }

    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    private Map setAction(String type, String action) {
        switch (type) {
            case "7":
                if (action.equals("1")) {
                    map_link.put("action", "打开");
                } else {
                    map_link.put("action", "闭合");
                }
                break;
            case "8":
                if (action.equals("1")) {
                    map_link.put("action", "有人经过");
                }
                break;
            case "9":

                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "10":

                break;
            case "11":
                if (action.equals("1")) {
                    map_link.put("action", "按下");
                }
                break;
            case "12":
                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "13":
                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "14":
                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "15":
                if (action.equals("1")) {
                    map_link.put("action", "打开");
                } else {
                    map_link.put("action", "闭合");
                }
                break;
            case "16":

                break;
        }
        return map_link;
    }
}
