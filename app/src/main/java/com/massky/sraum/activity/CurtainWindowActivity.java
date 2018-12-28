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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.alibaba.fastjson.JSON;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
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

public class CurtainWindowActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    private String status;
    private String number;
    private String type;
    private String name;
    private String name1;
    private String name2;
    @InjectView(R.id.project_select)
    TextView project_select;
    @InjectView(R.id.name1_txt)
    TextView name1_txt;
    @InjectView(R.id.name2_txt)
    TextView name2_txt;
    @InjectView(R.id.radio_group_out)
    LinearLayout radio_group_out;
    @InjectView(R.id.radio_group_in)
    LinearLayout radio_group_in;
    @InjectView(R.id.radio_group_all)
    LinearLayout radio_group_all;
    private String loginPhone;
    private boolean vibflag;
    private boolean musicflag;
    private String boxnumber;
    private DialogUtil dialogUtil;
    private String statusflag;
    private String flagone;
    private String flagtwo;
    private String flagthree;
    private boolean whriteone = true, whritetwo = true, whritethree = true;
    private String curtain;
    private String dimmer;
    private String modeflag;
    private String temperature;
    private String windflag;
    String statusm;
    private MessageReceiver mMessageReceiver;

    private boolean mapflag;
    private boolean statusbo;
    private String areaNumber;
    private String roomNumber;
    private Map<String, Object> mapalldevice = new HashMap<>();

    @Override
    protected int viewId() {
        return R.layout.curtain_window_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
//        init_receiver_control();
        registerMessageReceiver();
        init_event();
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


    //下载设备信息并且比较状态（为了显示开关状态）
    private void upload() {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("roomNumber", number);
        mapdevice.put("token", TokenUtil.getToken(CurtainWindowActivity.this));
        dialogUtil.loadDialog();
        SharedPreferencesUtil.saveData(CurtainWindowActivity.this, "boxnumber", boxnumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//获取togglen成功后重新刷新数据
                upload();
            }
        }, CurtainWindowActivity.this, dialogUtil) {
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
                            change_status_toui(type, d.status);
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

    /**
     * 根据status去切换UI显示
     */
    private void change_status_toui(String type, String status) {
        if (type.equals("4")) {
            switch (status) {
                case "0"://全关
                    flagone = "2";// 2 -全关，1- 开 ， 3 -
                    flagtwo = "2";
                    flagthree = "2";
//                    radio_group_out.getChildCount();
                    common_select("全关");

                    break;
                case "1"://全开
                    flagone = "1";
                    flagtwo = "1";
                    flagthree = "1";
                    common_select("全开");
                    break;
                //暂停
                case "2"://
                    flagone = "1";
                    flagtwo = "1";
                    flagthree = "1";
                    common_select("暂停");
                    break;
                //3-组 1 开组 2 关
                case "3":
                    flagone = "1";
                    flagtwo = "3";
                    flagthree = "0";
                    common_select("组1开组2关");
                    break;
                //4-组 1 开组 2 暂停
                case "4":
                    flagone = "1";
                    flagtwo = "2";
                    flagthree = "0";
                    common_select("组1开组2暂停");
                    break;
                //5-组 1 关组 2 开
                case "5":
                    flagone = "3";
                    flagtwo = "1";
                    flagthree = "0";
                    common_select("组1关组2开");
                    break;
                //6-组 1 关组 2 暂停
                case "6":
                    flagone = "3";
                    flagtwo = "2";
                    flagthree = "0";
                    common_select("组1关组2暂停");
                    break;
                //7-组 1 暂停 组 2 关
                case "7":
                    flagone = "2";
                    flagtwo = "3";
                    flagthree = "0";
                    common_select("组1暂停组2关");
                    break;
                //8-组 1 暂停组 2 开
                case "8":
                    flagone = "2";
                    flagtwo = "1";
                    flagthree = "0";
                    common_select("组1暂停组2开");
                    break;
            }
        }
    }


    /**
     * 根据状态码status，显示相应UI
     */
    private void common_select(String item) {
        common();
        switch (item) {
            case "全开":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open_active);
                ((ImageView) ((RelativeLayout) radio_group_all.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open_active);
                break;
            case "全关":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close_active);
                ((ImageView) ((RelativeLayout) radio_group_all.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close_active);
                break;

            case "暂停":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting_active);
                ((ImageView) ((RelativeLayout) radio_group_all.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting_active);
                break;
            case "组1开组2关":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close_active);
                break;
            case "组1开组2暂停":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting_active);
                break;
            case "组1关组2开":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open_active);
                break;
            case "组1关组2暂停":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting_active);
                break;
            case "组1暂停组2关":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close_active);
                break;
            case "组1暂停组2开":
                ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting_active);
                ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open_active);
                break;

        }
    }

    private void common() {
        ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open);
        ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting);
        ((ImageView) ((RelativeLayout) radio_group_out.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close);

        ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open);
        ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting);
        ((ImageView) ((RelativeLayout) radio_group_in.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close);


        ((ImageView) ((RelativeLayout) radio_group_all.getChildAt(0)).getChildAt(0)).setImageResource(R.drawable.icon_cl_open);
        ((ImageView) ((RelativeLayout) radio_group_all.getChildAt(1)).getChildAt(0)).setImageResource(R.drawable.icon_cl_zanting);
        ((ImageView) ((RelativeLayout) radio_group_all.getChildAt(2)).getChildAt(0)).setImageResource(R.drawable.icon_cl_close);
    }


    private void statusClear() {
        if (whriteone) {

        }

        if (whritetwo) {

        }

        if (whritethree) {

        }
    }


    /**
     * 初始化监听事件
     */

    private void init_event() {
        for (int i = 0; i < radio_group_out.getChildCount(); i++) {//外纱
            final RelativeLayout child = (RelativeLayout) radio_group_out.getChildAt(i);
            child.setTag(i);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch ((int) child.getTag()) {
                        case 0:
                            curtain = "1";
                            break;
                        case 1:
                            curtain = "2";
                            break;
                        case 2:
                            curtain = "3";
                            break;
                    }
                    getMapdevice();
                }
            });
        }

        for (int i = 0; i < radio_group_in.getChildCount(); i++) {//内纱
            final RelativeLayout child = (RelativeLayout) radio_group_in.getChildAt(i);
            child.setTag(i);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch ((int) child.getTag()) {
                        case 0:
                            curtain = "4";
                            break;
                        case 1:
                            curtain = "5";
                            break;
                        case 2:
                            curtain = "6";
                            break;
                    }
                    getMapdevice();
                }
            });
        }

        for (int i = 0; i < radio_group_all.getChildCount(); i++) {//全部
            final RelativeLayout child = (RelativeLayout) radio_group_all.getChildAt(i);
            child.setTag(i);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch ((int) child.getTag()) {
                        case 0:
                            curtain = "7";
                            break;
                        case 1:
                            curtain = "8";
                            break;
                        case 2:
                            curtain = "9";
                            break;
                    }
                    getMapdevice();
                }
            });
        }
    }

    //控制设备
    private void getMapdevice() {
        //两个特别全开全关设置
        if (type.equals("4")) {
            statusm = "";
            switch (curtain) {
                //窗帘1打开
                case "1":
                    //看窗帘2
                    statusm = out_click(statusm, flagtwo, "1", "4", "3");
//                    statusm = "3";
                    break;
                //窗帘1暂停
                case "2":
                    //看窗帘2
                    statusm = out_click(statusm, flagtwo, "8", "2", "7");
//                    statusm = "4";
                    break;
                //窗帘1关闭
                case "3":
                    statusm = out_click(statusm, flagtwo, "5", "6", "0");
//                    statusm = "5";
                    break;
                //窗帘2打开
                case "4":
                    statusm = out_click(statusm, flagone, "1", "8", "5");
//                    statusm = "6";
                    break;
                //窗帘2暂停
                case "5":
                    statusm = out_click(statusm, flagone, "4", "2", "6");
//                    statusm = "1";
                    break;
                //窗帘2关闭
                case "6":
                    statusm = out_click(statusm, flagone, "3", "7", "0");
//                    statusm = "0";
                    break;
                //全开
                case "7":
                    statusm = "1";
                    break;
                //全部暂停
                case "8":
                    statusm = "2";
                    break;
                //全部关闭
                case "9":
                    statusm = "0";
                    break;
                default:
                    break;
            }
            sraum_device_control();
        }
    }

    /***
     * 结合外纱和内纱控制窗帘开或者关，暂停
     * @param statusm
     * @param flagtwo
     * @param s
     * @param s2
     * @param s3
     * @return
     */
    private String out_click(String statusm, String flagtwo, String s, String s2, String s3) {
        switch (flagtwo) {
            case "1"://组一关组二开
                statusm = s;
                break;
            case "2":
                statusm = s2;
                break;
            case "3":
                statusm = s3;
                break;
        }
        return statusm;
    }

    private void sraum_device_control() {
        Map<String, Object> mapalldevice1 = new HashMap<>();
        List<Map> listobj = new ArrayList<>();
        Map map = new HashMap();
        map.put("type", mapalldevice.get("type").toString());
        map.put("number", mapalldevice.get("number").toString());
        map.put("name", mapalldevice.get("name").toString());
        map.put("status", statusm);
        map.put("mode", mapalldevice.get("mode").toString());
        map.put("dimmer", mapalldevice.get("dimmer").toString());
        map.put("temperature", mapalldevice.get("temperature").toString());
        map.put("speed", mapalldevice.get("speed").toString());
        listobj.add(map);
        mapalldevice1.put("token", TokenUtil.getToken(CurtainWindowActivity.this));
        mapalldevice1.put("areaNumber", areaNumber);
        mapalldevice1.put("deviceInfo", listobj);

        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapalldevice1, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_device_control();
            }
        }, CurtainWindowActivity.this, dialogUtil) {

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(CurtainWindowActivity.this, "areaNumber\n" +
                        "不存在");
            }

            @Override
            public void fourCode() {
                super.fourCode();
                ToastUtil.showToast(CurtainWindowActivity.this, "控制失败");
            }

            @Override
            public void threeCode() {
                super.threeCode();
                ToastUtil.showToast(CurtainWindowActivity.this, "deviceInfo 不正确");
            }

            @Override
            public void defaultCode() {
                ToastUtil.showToast(CurtainWindowActivity.this, "操作失败");
            }

            @Override
            public void pullDataError() {
                ToastUtil.showToast(CurtainWindowActivity.this, "操作失败");
            }

            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                change_status_toui(type, statusm);
                if (vibflag) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                }
                if (musicflag) {
                    MusicUtil.startMusic(CurtainWindowActivity.this, 1, "");
                } else {
                    MusicUtil.stopMusic(CurtainWindowActivity.this, "");
                }
            }
        });
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        init_Data();
    }

    private void init_Data() {
        Bundle bundle = IntentUtil.getIntentBundle(CurtainWindowActivity.this);
        type = bundle.getString("type");
        number = bundle.getString("number");
        name1 = bundle.getString("name1");
        name2 = bundle.getString("name2");
        name = bundle.getString("name");
        status = bundle.getString("status");
        areaNumber = bundle.getString("areaNumber");
        roomNumber = bundle.getString("roomNumber");//当前房间编号
        mapalldevice = (Map<String, Object>) bundle.getSerializable("mapalldevice");
        if (mapalldevice != null) {
            type = (String) mapalldevice.get("type");
            //初始化窗帘参数
            change_status_toui(type, status);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                CurtainWindowActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }
}
