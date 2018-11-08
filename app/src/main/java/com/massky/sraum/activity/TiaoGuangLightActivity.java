package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

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
import com.yanzhenjie.statusview.StatusUtils;
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
        vibflag = preferences.getBoolean("vibflag", false);
        musicflag = preferences.getBoolean("musicflag", false);
        LogUtil.i("查看值状态" + musicflag);
        boxnumber = (String) SharedPreferencesUtil.getData(TiaoGuangLightActivity.this, "boxnumber", "");
        dialogUtil = new DialogUtil(TiaoGuangLightActivity.this);
        bar1.setCurrentValues(80);
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
        if (addflag) {
//            .setText(progress + "");
            bar1.setCurrentValues(progress);
        } else {
//            tempimage_id.setText((16 + progress) + "");
            bar1.setCurrentValues(progress + 16);
        }
//        Log.e("zhu", "tempimage_id.setText: " + tempimage_id.getText().toString());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        LogUtil.i("开始滑动", "onStartTrackingTouch: ");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtil.i("停止滑动", "onStopTrackingTouch: ");
        //停止滑动是的状态
        mapflag = false;
        if (statusbo) {
            getMapdevice();//控制
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
        mapdevice.put("token", TokenUtil.getToken(TiaoGuangLightActivity.this));
        mapdevice.put("boxNumber", boxnumber);
        dialogUtil.loadDialog();
        SharedPreferencesUtil.saveData(TiaoGuangLightActivity.this, "boxnumber", boxnumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getAllDevice, mapdevice, new Mycallback(new AddTogglenInterfacer() {
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
                            LogUtil.eLength("下载数据", statusflag);
                            //不为窗帘开关状态
                            dimmer = d.dimmer;
                            Log.e("zhu", "d.dimmer:" + dimmer);
                            modeflag = d.mode;
                            temperature = d.temperature;
                            windflag = d.speed;
                            if (type.equals("2")) {
                                if (dimmer != null && !dimmer.equals("")) {
//                                    tempimage_id.setText(dimmer);
//                                    id_seekBar.setProgress(Integer.parseInt(dimmer));
                                }
                            }
//                            setModetwo();
//                            setSpeed();
                            LogUtil.eLength("查看是否进去", temperature + "进入方法" + dimmer);
                            if (d.status.equals("0")) {
//                                getBoxclose();
                                LogUtil.eLength("没有进去", "进入方法");
                            } else {
                                LogUtil.eLength("你看", "进入方法");
//                                getBoxopen();
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
        mapalldevice.put("token", TokenUtil.getToken(TiaoGuangLightActivity.this));
        mapalldevice.put("boxNumber", boxnumber);
        mapalldevice.put("deviceInfo", listob);
        LogUtil.eLength("真正传入", JSON.toJSONString(mapalldevice));
        getBoxStatus(mapalldevice);
    }

    private void getBoxStatus(final Map<String, Object> mapdevice) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(TiaoGuangLightActivity.this));
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
                map.put("token", TokenUtil.getToken(TiaoGuangLightActivity.this));
                map.put("boxNumber", boxnumber);
                getBoxStatus_read(mapdevice, map);
            }
        }, TiaoGuangLightActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(TiaoGuangLightActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                switch (user.status) {
                    case "1":
                        sraum_device_control(mapdevice);
                        break;
                    case "0":
                        //网关离线
                        ToastUtil.showDelToast(TiaoGuangLightActivity.this, "网关处于离线状态");
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
                map.put("token", TokenUtil.getToken(TiaoGuangLightActivity.this));
                map.put("boxNumber", boxnumber);
                sraum_device_control(map);

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
                    MusicUtil.startMusic(TiaoGuangLightActivity.this, 1, "");
                } else {
                    MusicUtil.stopMusic(TiaoGuangLightActivity.this, "");
                }
            }

        });
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
                mapflag = true;
                getMapdevice();
                break;
        }
    }
}
