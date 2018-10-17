package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.receiver.ApiTcpReceiveHelper;
import com.massky.sraum.service.MyService;
import com.massky.sraum.view.VolumeView;
import com.yanzhenjie.statusview.StatusUtils;
import com.zanelove.aircontrolprogressbar.ColorArcProgressBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/30.
 */

public class AirControlActivity extends BaseActivity {
    @InjectView(R.id.bar1)
    ColorArcProgressBar bar1;
    @InjectView(R.id.back)
    ImageView back;
    private String status;
    private String number;
    private String type;
    private String name;
    @InjectView(R.id.project_select)
    TextView project_select;
    private String mode;
    private String temperature;
    private String speed;
    @InjectView(R.id.volumeView)
    VolumeView volumeView;
    @InjectView(R.id.air_conrol_rel)
    RelativeLayout air_conrol_rel;
    @InjectView(R.id.moshi_rel)
    RelativeLayout moshi_rel;
    @InjectView(R.id.fengsu_rel)
    RelativeLayout fengsu_rel;
    private String speed_txt;
    private String mode_txt;

    @Override
    protected int viewId() {
        return R.layout.air_control_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        Map map_item = (Map) getIntent().getSerializableExtra("map_item");
        if (map_item == null) return;
        status = (String) map_item.get("status");
        number = (String) map_item.get("number");
        type = (String) map_item.get("type");
        name = (String) map_item.get("name");
        mode = (String) map_item.get("mode");
        temperature = (String) map_item.get("temperature");
        speed = (String) map_item.get("speed");
        project_select.setText(name);
        bar1.setCurrentValues(Integer.parseInt(temperature));
//        bar1.setUnit("你好");//风速
//        bar1.setTitle("hello");//模式
        init_value();
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApiTcpReceiveHelper.AIRCONTROL_RECEIVE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    /**
     * 初始化值
     */
    private void init_value() {
        switch (speed) {
            case "1":
                speed_txt = "低风";
                break;
            case "2":
                speed_txt = "中风";
                break;
            case "3":
                speed_txt = "高风";
                break;
            case "4":
                speed_txt = "强力";
                break;
            case "5":
                speed_txt = "送风";
                break;
            case "6":
                speed_txt = "自动";
                break;

        }

        switch (mode) {
            case "1":
                mode_txt = "制冷";
                break;
            case "2":
                mode_txt = "制热";
                break;
            case "3":
                mode_txt = "除湿";
                break;
            case "4":
                mode_txt = "自动";
                break;
            case "5":
                mode_txt = "通风";
                break;
        }

        bar1.setSpeed(speed_txt);
        bar1.setMode(mode);
        //volumeView
        volumeView.set_temperature(Integer.parseInt(temperature));
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        volumeView.setOnChangeListener(new VolumeView.OnChangeListener() {
            @Override
            public void onChange(int count) {//显示空调温度控制，
                Log.e("robin debug", "count:" + count);
            }
        });
        air_conrol_rel.setOnClickListener(this);
        //控制空调开关
        moshi_rel.setOnClickListener(this
        );
        fengsu_rel.setOnClickListener(this);

    }


    /**
     * 广播接收
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            String result = intent.getStringExtra("result");
            ToastUtil.showToast(AirControlActivity.this, "成功");
            Map map = new HashMap();
            map.put("type", type);
            map.put("number", number);
            get_single_device_info(map);
        }
    };


    /**
     * 控制空调
     */
    private void control_air() {

        switch (status) {
            case "0":
                status = "1";
                break;
            case "1":
                status = "0";
                break;
        }

        control_air_mode();
    }

    @Override
    protected void onData() {

    }

    /**
     * 获取单个设备或是按钮信息（APP->网关）
     *
     * @param map
     */
    private void get_single_device_info(final Map map) {
        MyOkHttp.postMapObject(ApiHelper.sraum_getOneInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        get_single_device_info(map);
                    }
                }, AirControlActivity.this, null) {
                    @Override
                    public void onSuccess(User user) {
//       switch (type) {//空调,PM检测,客厅窗帘,门磁,主灯
//                        String type = (String) map.get("type");
//                        map.put("status",user.status);
//                        map.put("name",user.name)
                        status = (String) user.status;
                        mode = user.mode;
                        temperature = user.temperature;
                        speed = user.speed;
//                        bar1.setCurrentValues(Integer.parseInt(temperature));
////        bar1.setUnit("你好");//风速
////        bar1.setTitle("hello");//模式
//                        bar1.setSpeed(speed);
//                        bar1.setMode(mode);
                        //volumeView
                        init_value();
//                        volumeView.set_temperature(Integer.parseInt(temperature));
                    }
                });
    }

    /**
     * 空调模式选择
     */
    private void moshi_kongtiao() {
        if (status.isEmpty()) {
            return;
        }
//        String model_kongtiao = zhire_kongtiao.getText().toString();
        int mode_index = 0;
        switch (mode_txt) {
            case "制冷":
                mode_index = 1;
                break;
            case "制热":
                mode_index = 2;
                break;
            case "除湿":
                mode_index = 3;
                break;
            case "自动":
                mode_index = 4;
                break;
            case "通风":
                mode_index = 5;
                break;
        }

        if (mode_index != 255) {
            mode_index++;
            if (mode_index > 5) {
                mode_index = 1;
            }
        }

        mode = mode_index + "";
        control_air_mode();
    }

    /**
     * 空调模式切换
     */
    private void control_air_mode() {
        Map map = new HashMap();
        map.put("number", number);
        map.put("type", type);
        map.put("mode", mode);
        MyService.getInstance().sraum_send_tcp(map, "sraum_controlButton");
    }


    /**
     * 空调风速选择
     */
    private void speed_kongtiao() {
        if (status.isEmpty()) {
            return;
        }
//        String model_kongtiao = zhire_kongtiao.getText().toString();
        int mode_index = 0;
        switch (speed_txt) {
            case "低风":
                mode_index = 1;
                break;
            case "中风":
                mode_index = 2;
                break;
            case "高风":
                mode_index = 3;
                break;
            case "强力":
                mode_index = 4;
                break;
            case "送风":
                mode_index = 5;
                break;
            case "自动":
                mode_index = 6;
                break;
        }

        if (mode_index != 255) {
            mode_index++;
            if (mode_index > 5) {
                mode_index = 1;
            }
        }

        speed = mode_index + "";
        control_air_speed();
    }

    /**
     * 控制空调风速
     */
    private void control_air_speed() {
        Map map = new HashMap();
        map.put("number", number);
        map.put("type", type);
        map.put("speed", speed);
        MyService.getInstance().sraum_send_tcp(map, "sraum_controlButton");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AirControlActivity.this.finish();
                break;
            case R.id.air_conrol_rel://控制空调开关
                control_air();
                break;
            case R.id.moshi_rel://模式
                moshi_kongtiao();
                break;
            case R.id.fengsu_rel://风速
                speed_kongtiao();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
