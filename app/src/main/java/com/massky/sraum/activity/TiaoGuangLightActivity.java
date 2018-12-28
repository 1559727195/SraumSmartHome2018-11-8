package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

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
import com.yanzhenjie.statusview.StatusUtils;
import com.zanelove.aircontrolprogressbar.ColorArcProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.massky.sraum.fragment.HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE;

/**
 * Created by zhu on 2018/1/30.
 */

public class TiaoGuangLightActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.id_seekBar)
    SeekBar id_seekBar;
    @InjectView(R.id.openbtn_tiao_guang)
    ImageView openbtn_tiao_guang;
    private String progress_now;
    private String status;
    private String number;
    private String type;
    private MessageReceiver mMessageReceiver;
    private String loginPhone;
    private boolean vibflag;
    private boolean musicflag;
    private String boxnumber;
    private DialogUtil dialogUtil;
    private String statusflag;
    private String dimmer;
    private String modeflag;
    private String temperature;
    private String windflag;

    @InjectView(R.id.bar1)
    ColorArcProgressBar bar1;
    private boolean addflag;
    private boolean mapflag;
    private boolean statusbo;
    private String name1;
    private String name2;
    private String name;
    private String areaNumber;
    private String roomNumber;
    private Map<String, Object> mapalldevice = new HashMap<>();


    @Override
    protected int viewId() {
        return R.layout.tiaoguanglight_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        registerMessageReceiver();
        loginPhone = (String) SharedPreferencesUtil.getData(TiaoGuangLightActivity.this, "loginPhone", "");
        SharedPreferences preferences = getSharedPreferences("sraum" + loginPhone,
                Context.MODE_PRIVATE);
        boxnumber = (String) SharedPreferencesUtil.getData(TiaoGuangLightActivity.this, "boxnumber", "");
        dialogUtil = new DialogUtil(TiaoGuangLightActivity.this);
        vibflag = preferences.getBoolean("vibflag", false);
        musicflag = preferences.getBoolean("musicflag", false);
        LogUtil.i("查看值状态" + musicflag);
        init_Data();
    }

    private void init_Data() {
        Bundle bundle = IntentUtil.getIntentBundle(TiaoGuangLightActivity.this);
        type = bundle.getString("type");
        number = bundle.getString("number");
        name1 = bundle.getString("name1");
        name2 = bundle.getString("name2");
        name = bundle.getString("name");

        areaNumber = bundle.getString("areaNumber");
        roomNumber = bundle.getString("roomNumber");//当前房间编号

        mapalldevice = (Map<String, Object>) bundle.getSerializable("mapalldevice");
        if (mapalldevice != null) {
            statusflag = (String) bundle.getString("status");
            dimmer = (String) mapalldevice.get("dimmer");
            bar1.setCurrentValues(Integer.parseInt(dimmer));
            id_seekBar.setProgress(Integer.parseInt(dimmer));
            doit_open();
        }
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.e("zhu", "onProgressChanged: " + progress);
        bar1.setCurrentValues(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        LogUtil.i("开始滑动", "onStartTrackingTouch: ");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtil.i("停止滑动", "onStopTrackingTouch: ");
        //停止滑动是的状态
        if (statusbo) {
            statusflag = "1";
            getMapdevice("slop");//控制
        }
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

    //下载设备信息并且比较状态（为了显示开关状态）
    private void upload() {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("roomNumber", number);
        mapdevice.put("token", TokenUtil.getToken(TiaoGuangLightActivity.this));
        dialogUtil.loadDialog();
        SharedPreferencesUtil.saveData(TiaoGuangLightActivity.this, "boxnumber", boxnumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//获取togglen成功后重新刷新数据
                        upload();
                    }
                }, TiaoGuangLightActivity.this, dialogUtil) {
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
                                    //不为窗帘开关状态
                                    dimmer = d.dimmer;
                                    Log.e("zhu", "d.dimmer:" + dimmer);
                                    if (type.equals("2")) {
                                        if (dimmer != null && !dimmer.equals("")) {
                                            bar1.setCurrentValues(Integer.parseInt(dimmer));
                                            id_seekBar.setProgress(Integer.parseInt(dimmer));
                                            doit_open();
                                        }
                                    }
                                    if (d.status.equals("0")) {
                                        statusbo = false;

                                    } else {
                                        statusbo = true;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        id_seekBar.setOnSeekBarChangeListener(this);
        id_seekBar.setOnTouchListener(new View.OnTouchListener() {//这个是根据网关状态在线情况，不在线的话，seekBar就不能滑动了，
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("zhu", "id_seekBar->:" + statusbo);
                return !statusbo;
            }
        });
        //调光控制
        openbtn_tiao_guang.setOnClickListener(this);
    }

    //控制设备
    private void getMapdevice(String slop) {
        Map<String, Object> mapalldevice = new HashMap<String, Object>();
        List<Map<String, Object>> listob = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapdevice = new HashMap<String, Object>();
        switch (type) {
            //调光灯
            case "2":
                dimmer = id_seekBar.getProgress() + "";
                dimmer = removeTrim(dimmer);
                Float item = Float.parseFloat(dimmer);
                dimmer = "" + Math.round(item);
                temperature = modeflag = "";
                windflag = "";
                break;
        }
        mapdevice.put("type", type);
        mapdevice.put("status", statusflag);
        mapdevice.put("number", number);
        mapdevice.put("name", name);
        mapdevice.put("dimmer", dimmer);
        mapdevice.put("mode", modeflag);
        mapdevice.put("temperature", temperature);
        mapdevice.put("speed", windflag);
        sraum_device_control(mapdevice, slop);
    }


    public String removeTrim(String str) {
        if (str.indexOf(".") > 0) {
            str = str.replaceAll("0+?$", "");//去掉多余的0
            str = str.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return str;
    }

    private void sraum_device_control(final Map<String, Object> mapdevice1, final String slop) {
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
        mapalldevice.put("token", TokenUtil.getToken(TiaoGuangLightActivity.this));
        mapalldevice.put("areaNumber", areaNumber);
        mapalldevice.put("deviceInfo", listobj);
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapalldevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_device_control(mapdevice1, slop);

            }
        }, TiaoGuangLightActivity.this, dialogUtil) {

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(TiaoGuangLightActivity.this, "操作失败");
            }

            @Override
            public void defaultCode() {
                ToastUtil.showToast(TiaoGuangLightActivity.this, "操作失败");
            }

            @Override
            public void pullDataError() {
                ToastUtil.showToast(TiaoGuangLightActivity.this, "操作失败");
            }


            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                switch (slop) {
                    case "onclick":
                        doit_open();
                        break;
                }
                if (vibflag) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                }
                if (musicflag) {
                    MusicUtil.startMusic(TiaoGuangLightActivity.this, 1, "");
                } else {
                    MusicUtil.stopMusic(TiaoGuangLightActivity.this, "");
                }
            }

        });
    }

    private void doit_open() {
        if (statusflag.equals("1")) {
            openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_open_active);
            statusflag = "0";
            statusbo = true;
        } else {
            //调光灯开关状态
            openbtn_tiao_guang.setImageResource(R.drawable.icon_cl_close);
            statusflag = "1";
            statusbo = false;
        }
    }


    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                TiaoGuangLightActivity.this.finish();
                break;
            //调光的开关状态
            case R.id.openbtn_tiao_guang:
                getMapdevice("onclick");
                break;
        }
    }
}
