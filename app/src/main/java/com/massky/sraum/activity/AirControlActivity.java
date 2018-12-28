package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MusicUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.VolumeView;
import com.yanzhenjie.statusview.StatusUtils;
import com.zanelove.aircontrolprogressbar.ColorArcAirControlProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.massky.sraum.fragment.HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE;

/**
 * Created by zhu on 2018/1/30.
 */

public class AirControlActivity extends BaseActivity {
    @InjectView(R.id.bar1)
    ColorArcAirControlProgressBar bar1;
    @InjectView(R.id.back)
    ImageView back;
    private String status;
    private String number;
    private String type;
    private String name;
    @InjectView(R.id.project_select)
    TextView project_select;
    private String mode;
    private String speed;
    @InjectView(R.id.volumeView)
    VolumeView volumeView;

    @InjectView(R.id.moshi_rel)
    RelativeLayout moshi_rel;
    @InjectView(R.id.fengsu_rel)
    RelativeLayout fengsu_rel;
    @InjectView(R.id.openbtn_tiao_guang)
    ImageView openbtn_tiao_guang;
    private String speed_txt;
    private String mode_txt;

    private boolean vibflag;
    private boolean musicflag;
    private String boxnumber;
    private DialogUtil dialogUtil;
    private String statusflag;
    private String dimmer;
    private String modeflag;
    private String temperature;
    private String windflag;
    private String loginPhone;
    private MessageReceiver mMessageReceiver;
    private boolean mapflag;
    private boolean addflag;
    private boolean statusbo;
    private String name1;
    private String name2;
    private String areaNumber;
    private String roomNumber;
    private Map<String, Object> mapalldevice = new HashMap<>();

    @Override
    protected int viewId() {
        return R.layout.air_control_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        registerMessageReceiver();
        loginPhone = (String) SharedPreferencesUtil.getData(AirControlActivity.this, "loginPhone", "");
        SharedPreferences preferences = getSharedPreferences("sraum" + loginPhone,
                Context.MODE_PRIVATE);
        vibflag = preferences.getBoolean("vibflag", false);
        musicflag = preferences.getBoolean("musicflag", false);
        dialogUtil = new DialogUtil(AirControlActivity.this);

    }

    private void init_Data() {
        Bundle bundle = IntentUtil.getIntentBundle(AirControlActivity.this);
        type = bundle.getString("type");
        number = bundle.getString("number");
        name1 = bundle.getString("name1");
        name2 = bundle.getString("name2");
        name = bundle.getString("name");
        statusflag = bundle.getString("status");
        areaNumber = bundle.getString("areaNumber");
        roomNumber = bundle.getString("roomNumber");//当前房间编号
        mapalldevice = (Map<String, Object>) bundle.getSerializable("mapalldevice");
        if (mapalldevice != null) {
            type = (String) mapalldevice.get("type");
            modeflag = (String) mapalldevice.get("mode");
            windflag = (String) mapalldevice.get("speed");
            temperature = (String) mapalldevice.get("temperature");
            dimmer = (String) mapalldevice.get("dimmer");
            switch (type) {
                case "3"://空调
                    moshi_rel.setVisibility(View.VISIBLE);
                    break;
                case "5"://新风
                case "6"://地暖
                    moshi_rel.setVisibility(View.GONE);
                    break;
            }
            if (type.equals("3") || type.equals("5") || type.equals("6")) {
                //初始化窗帘参数
                //空调
                //判断展示值是否加16
                addflag = false;
                bar1.setCurrentValues(Integer.parseInt(temperature));
                volumeView.set_temperature(Integer.parseInt(temperature));
                setModetwo();
                setSpeed();
                doit_open();
            }
        }
    }


    //下载设备信息并且比较状态（为了显示开关状态）
    private void upload() {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(AirControlActivity.this));
        mapdevice.put("boxNumber", boxnumber);
        dialogUtil.loadDialog();
        SharedPreferencesUtil.saveData(AirControlActivity.this, "boxnumber", boxnumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getAllDevice, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//获取togglen成功后重新刷新数据
                upload();
            }
        }, AirControlActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                //拿到设备状态值
                for (User.device d : user.deviceList) {
                    if (d.number.equals(number)) {
                        //匹配状值设置当前状态
                        if (d.status != null) {
                            //进行判断是否为窗帘
                            statusflag = d.status;
                            LogUtil.eLength("下载数据", statusflag);

                            //不为窗帘开关状态
                            dimmer = d.dimmer;
                            Log.e("zhu", "d.dimmer:" + dimmer);
                            modeflag = d.mode;
                            temperature = d.temperature;
                            windflag = d.speed;
                            if (type.equals("3") || type.equals("5") || type.equals("6")) {
                                if (temperature != null && !temperature.equals("")) {
//                                        tempimage_id.setText(temperature);
                                    bar1.setCurrentValues(Integer.parseInt(temperature));
//                                        id_seekBar.setProgress(Integer.parseInt(temperature) - 16);
                                    volumeView.set_temperature(Integer.parseInt(temperature));
                                }

                            }
                            setModetwo();
                            setSpeed();
                            LogUtil.eLength("查看是否进去", temperature + "进入方法" + dimmer);
                        }
                    }
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }


    private void setModetwo() {
        String strmode = "";
        switch (modeflag) {
            case "1":
                strmode = "制冷";
                break;
            case "2":
                strmode = "制热";
                break;
            case "3":
                strmode = "除湿";
                break;
            case "4":
                strmode = "自动";
                break;
            case "5":
                strmode = "通风";
                break;
            default:
                break;
        }
//        tempstate_id.setText(strmode);
        bar1.setMode(strmode);
    }


    private void setSpeed() {
        String strwind = "";
        switch (windflag) {
            case "1":
                strwind = "低风";
                break;
            case "2":
                strwind = "中风";
                break;
            case "3":
                strwind = "高风";
                break;
            case "4":
                strwind = "强力";
                break;
            case "5":
                strwind = "送风";
                break;
            case "6":
                strwind = "自动";
                break;
            default:
                break;
        }
//        windspeedtwo_id.setText(strwind);
//        windspeed_id.setText(strwind);
        bar1.setSpeed(strwind);
    }


    /**
     * 动态注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_RECEIVER_TO_SECOND_PAGE);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(ACTION_INTENT_RECEIVER_TO_SECOND_PAGE)) {
                Log.e("zhu", "LamplightActivity:" + "LamplightActivity");
                //控制部分的二级页面进去要同步更新推送的信息显示 （推送的是消息）。
//                upload();
            }
        }
    }


    //控制设备
    private void getMapdevice(String doit) {
        Map<String, Object> mapdevice = new HashMap<String, Object>();
        mapdevice.put("type", type);
        mapdevice.put("status", statusflag);
        mapdevice.put("number", number);
        mapdevice.put("name", name);
        mapdevice.put("dimmer", dimmer);
        mapdevice.put("mode", modeflag);
        mapdevice.put("temperature", temperature);
        mapdevice.put("speed", windflag);
        sraum_device_control(mapdevice, doit);

    }


    private void sraum_device_control(Map<String, Object> mapdevice1, final String doit) {
        Map<String, Object> mapalldevice = new HashMap<>();
        List<Map> listobj = new ArrayList<>();
        Map map = new HashMap();
        map.put("type", mapdevice1.get("type").toString());
        map.put("number", mapdevice1.get("number").toString());
        map.put("name", mapdevice1.get("name").toString());
        map.put("status", statusflag);
        map.put("mode", mapdevice1.get("mode").toString());
        map.put("dimmer", mapdevice1.get("dimmer").toString());
        map.put("temperature", mapdevice1.get("temperature").toString());
        map.put("speed", mapdevice1.get("speed").toString());
        listobj.add(map);
        mapalldevice.put("token", TokenUtil.getToken(AirControlActivity.this));
        mapalldevice.put("areaNumber", areaNumber);
        mapalldevice.put("deviceInfo", listobj);
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapalldevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                Map<String, Object> map = new HashMap<>();
                map.put("token", TokenUtil.getToken(AirControlActivity.this));
                map.put("boxNumber", boxnumber);
                sraum_device_control(map, doit);

            }
        }, AirControlActivity.this, dialogUtil) {

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(AirControlActivity.this, "操作失败");
            }

            @Override
            public void defaultCode() {
                ToastUtil.showToast(AirControlActivity.this, "操作失败");
            }

            @Override
            public void pullDataError() {
                ToastUtil.showToast(AirControlActivity.this, "操作失败");
            }


            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                switch (doit) {
                    case "onclick":
                        doit_open();
                        break;
                }
                if (vibflag) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                }
                if (musicflag) {
                    MusicUtil.startMusic(AirControlActivity.this, 1, "");
                } else {
                    MusicUtil.stopMusic(AirControlActivity.this, "");
                }
            }

        });
    }

    private void doit_open() {
        if (statusflag.equals("1")) {
            openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_open_active);
            statusflag = "0";
            statusbo = true;
            volumeView.setVolumeCliable(true);
        } else {
            //调光灯开关状态
            openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_close);
            statusflag = "1";
            statusbo = false;
            volumeView.setVolumeCliable(false);
        }
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
                temperature = count + "";
                bar1.setCurrentValues(count);
                getMapdevice("");

            }
        });
        openbtn_tiao_guang.setOnClickListener(this);
        //控制空调开关
        moshi_rel.setOnClickListener(this
        );
        fengsu_rel.setOnClickListener(this);
    }


    @Override
    protected void onData() {
        init_Data();
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
        if (statusbo) {
            setMode();
            getMapdevice("");
        }

    }

    private void setMode() {
        //模式状态
        if (statusbo)
            switch (modeflag) {
                case "1":
//                tempstate_id.setText("制热");
                    bar1.setMode("制热");
                    modeflag = "2";
                    break;
                case "2":
//                tempstate_id.setText("除湿");
                    bar1.setMode("除湿");
                    modeflag = "3";
                    break;
                case "3":
//                tempstate_id.setText("自动");
                    bar1.setMode("自动");
                    modeflag = "4";
                    break;
                case "4":
//                tempstate_id.setText("通风");
                    bar1.setMode("通风");
                    modeflag = "5";
                    break;
                case "5":
//                tempstate_id.setText("制冷");
                    bar1.setMode("制冷");
                    modeflag = "1";
                    break;
                default:
                    break;
            }
    }


    /**
     * 空调风速选择
     */
    private void speed_kongtiao() {
        //风速状态
        switch (windflag) {
            case "1":
//                    windspeed_id.setText("中风");
                bar1.setSpeed("中风");
                windflag = "2";
                break;
            case "2":
//                    windspeed_id.setText("高风");
                bar1.setSpeed("高风");
                windflag = "3";
                break;
            case "3":
                bar1.setSpeed("强力");
//                    windspeed_id.setText("强力");
                windflag = "4";
                break;
            case "4":
                bar1.setSpeed("送风");
//                    windspeed_id.setText("送风");
                windflag = "5";
                break;
            case "5":
//                    windspeed_id.setText("自动");
                bar1.setSpeed("自动");
                windflag = "6";
                break;
            case "6":
//                    windspeed_id.setText("低风");
                bar1.setSpeed("低风");
                windflag = "1";
                break;
            default:
                break;
        }
        getMapdevice("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AirControlActivity.this.finish();
                break;
            case R.id.openbtn_tiao_guang://控制空调开关
                control_air("onclick");
                break;
            case R.id.moshi_rel://模式
                moshi_kongtiao();
                break;
            case R.id.fengsu_rel://风速
                speed_kongtiao();
                break;
        }
    }

    /**
     * 控制空调开关
     */
    private void control_air(String doit) {
        getMapdevice(doit);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }
}
