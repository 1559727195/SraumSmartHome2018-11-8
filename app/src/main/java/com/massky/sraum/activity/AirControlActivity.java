package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.alibaba.fastjson.JSON;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MusicUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.MycallbackNest;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.receiver.ApiTcpReceiveHelper;
import com.massky.sraum.service.MyService;
import com.massky.sraum.view.VolumeView;
import com.yanzhenjie.statusview.StatusUtils;
import com.zanelove.aircontrolprogressbar.ColorArcAirControlProgressBar;
import com.zanelove.aircontrolprogressbar.ColorArcProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

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
        LogUtil.i("查看值状态" + musicflag);
        boxnumber = (String) SharedPreferencesUtil.getData(AirControlActivity.this, "boxnumber", "");
        dialogUtil = new DialogUtil(AirControlActivity.this);
        bar1.setCurrentValues(80);

        switch (type) {
            //空调
            case "3":
                //判断展示值是否加16
                addflag = false;
                bar1.setCurrentValues(16);
//                id_seekBar.setMax(14);
                volumeView.set_temperature(16);
                break;
        }

        //下载设备信息
        upload();
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
                            if (d.status.equals("0")) {
                                getBoxclose();
                                LogUtil.eLength("没有进去", "进入方法");
                            } else {
                                LogUtil.eLength("你看", "进入方法");
                                getBoxopen();
                            }
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

    /*非窗帘全关状态设置*/
    private void getBoxclose() {
        //全部关闭状态
        //调光灯状态开关
//        dimmerrelative.setBackgroundResource(R.drawable.hsmall_circle);
//        dimmerimageview.setImageResource(R.drawable.hopen);
        openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_open_active);
        statusflag = "1";
        statusbo = false;
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


    /*非窗帘全开状态设置*/
    private void getBoxopen() {
        //目前是全开的状态
        //调光灯状态开关
//        dimmerrelative.setBackgroundResource(R.drawable.hsmall_black);
//        dimmerimageview.setImageResource(R.drawable.hairclose_word);
        openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_close);
        statusflag = "0";
        statusbo = true;
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
                upload();
            }
        }
    }


    //控制设备
    private void getMapdevice() {
        Map<String, Object> mapalldevice = new HashMap<String, Object>();
        List<Map<String, Object>> listob = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapdevice = new HashMap<String, Object>();
        switch (type) {
            //调光灯
            case "2":
//                dimmer = tempimage_id.getText().toString();
                dimmer = bar1.getcurrentvalue();
                temperature = modeflag = "";
                windflag = "";
                break;
        }
        mapdevice.put("type", type);
        mapdevice.put("number", number);

        if (mapflag) {
            mapdevice.put("status", statusflag);
        } else {
            mapdevice.put("status", "1");
        }

        Log.e("zhu", "dimmer:" + dimmer);
        mapdevice.put("dimmer", dimmer);
        mapdevice.put("mode", modeflag);
        mapdevice.put("temperature", temperature);
        mapdevice.put("speed", windflag);
        listob.add(mapdevice);
        mapalldevice.put("token", TokenUtil.getToken(AirControlActivity.this));
        mapalldevice.put("boxNumber", boxnumber);
        mapalldevice.put("deviceInfo", listob);
        LogUtil.eLength("真正传入", JSON.toJSONString(mapalldevice));
        getBoxStatus(mapalldevice);
    }

    private void getBoxStatus(final Map<String, Object> mapdevice) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(AirControlActivity.this));
        map.put("boxNumber", boxnumber);
        dialogUtil.loadDialog();
        getBoxStatus_read(mapdevice, map);
    }

    /**
     * getBoxStatus_read
     *
     * @param mapdevice
     * @param map
     */
    private void getBoxStatus_read(final Map<String, Object> mapdevice, final Map<String, Object> map) {
        MyOkHttp.postMapObjectnest(ApiHelper.sraum_getBoxStatus, map, new MycallbackNest(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen获取的数据
                Map<String, Object> map = new HashMap<>();
                map.put("token", TokenUtil.getToken(AirControlActivity.this));
                map.put("boxNumber", boxnumber);
                getBoxStatus_read(mapdevice, map);
            }
        }, AirControlActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(AirControlActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                switch (user.status) {
                    case "1":
                        sraum_device_control(mapdevice);
                        break;
                    case "0":
                        //网关离线
                        ToastUtil.showDelToast(AirControlActivity.this, "网关处于离线状态");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void sraum_device_control(Map<String, Object> mapdevice) {
        List<Map> list = (List<Map>) mapdevice.get("deviceInfo");
        Log.e("zhu", "mapdevice->diming:" + list.get(0).get("dimmer"));
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                Map<String, Object> map = new HashMap<>();
                map.put("token", TokenUtil.getToken(AirControlActivity.this));
                map.put("boxNumber", boxnumber);
                sraum_device_control(map);

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

                LogUtil.eLength("查看", mapflag + "");
                if (mapflag) {
                    if (statusflag.equals("1")) {
                        //调光灯开关状态
                        LogUtil.eLength("方法走起", "是否走了");
//                        openbtn_tiao_guang.setImageResource(R.drawable.guan_white_word);
//                        //空调开关状态
//                        switchrelative.setBackgroundResource(R.drawable.hsmall_black);
//                        statusopen.setImageResource(R.drawable.hairclose_word);
                        openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_close);
                        statusflag = "0";
                        statusbo = true;
                    } else {
                        //调光灯开关状态
                        LogUtil.eLength("确实走了", "是否走了");
//                                    dimmerrelative.setBackgroundResource(R.drawable.hsmall_circle);
//                                    dimmerimageview.setImageResource(R.drawable.hopen);
//                        openbtn_tiao_guang.setImageResource(R.drawable.open_black_word);
//                        //空调开关状态
//                        switchrelative.setBackgroundResource(R.drawable.hsmall_circle);
//                        statusopen.setImageResource(R.drawable.hopen);
                        openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_open_active);

                        statusflag = "1";
                        statusbo = false;
                    }
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
                bar1.setCurrentValues(count);

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
        mapflag = false;
        if (statusbo) {
            setMode();
            getMapdevice();
        }
    }

    private void setMode() {
        //模式状态
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
        mapflag = false;
        if (statusbo) {
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
            getMapdevice();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AirControlActivity.this.finish();
                break;
            case R.id.openbtn_tiao_guang://控制空调开关
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

    /**
     * 控制空调开关
     */
    private void control_air() {
        mapflag = true;
        getMapdevice();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }
}
