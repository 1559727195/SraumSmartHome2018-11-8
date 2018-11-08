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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.yanzhenjie.statusview.StatusUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;

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
    RadioGroup radio_group_out;
    @InjectView(R.id.radio_group_in)
    RadioGroup radio_group_in;
    @InjectView(R.id.radio_group_all)
    RadioGroup radio_group_all;
    @InjectView(R.id.order_process)
    RadioButton order_process;
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

    @Override
    protected int viewId() {
        return R.layout.curtain_window_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
//        init_receiver_control();
        init_event();
        change_status_toui("4", "8");
//        Map map_item = (Map) getIntent().getSerializableExtra("map_item");
//
//        loginPhone = (String) SharedPreferencesUtil.getData(CurtainWindowActivity.this, "loginPhone", "");
//        SharedPreferences preferences = getSharedPreferences("sraum" + loginPhone,
//                Context.MODE_PRIVATE);
//        vibflag = preferences.getBoolean("vibflag", false);
//        musicflag = preferences.getBoolean("musicflag", false);
//        LogUtil.i("查看值状态" + musicflag);
//        boxnumber = (String) SharedPreferencesUtil.getData(CurtainWindowActivity.this, "boxnumber", "");
//        dialogUtil = new DialogUtil(CurtainWindowActivity.this);
////        bar1.setCurrentValues(80);
//
//        switch (type) {
//            //空调
//            case "3":
//
//                break;
//        }
//
//        //下载设备信息
//        upload();
    }

    //下载设备信息并且比较状态（为了显示开关状态）
    private void upload() {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(CurtainWindowActivity.this));
        mapdevice.put("boxNumber", boxnumber);
        dialogUtil.loadDialog();
        SharedPreferencesUtil.saveData(CurtainWindowActivity.this, "boxnumber", boxnumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getAllDevice, mapdevice, new Mycallback(new AddTogglenInterfacer() {
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
                    common_select(2);

                    break;
                case "1"://全开
                    flagone = "1";
                    flagtwo = "1";
                    flagthree = "1";
                    common_select(0);
                    break;
                //暂停
                case "2"://
                    flagone = "1";
                    flagtwo = "1";
                    flagthree = "1";
                    common_select(1);
                    break;
                //3-组 1 开组 2 关
                case "3":
                    flagone = "1";
                    flagtwo = "3";
                    flagthree = "0";

                    common_select_second(0, 2);

                    break;
                //4-组 1 开组 2 暂停
                case "4":
                    flagone = "1";
                    flagtwo = "2";
                    flagthree = "0";
                    common_select_second(0, 1);


                    break;
                //5-组 1 关组 2 开
                case "5":
                    flagone = "3";
                    flagtwo = "1";
                    flagthree = "0";
                    common_select_second(2, 0);


                    break;
                //6-组 1 关组 2 暂停
                case "6":
                    flagone = "3";
                    flagtwo = "2";
                    flagthree = "0";

                    common_select_second(2, 1);

                    break;
                //7-组 1 暂停 组 2 关
                case "7":
                    flagone = "2";
                    flagtwo = "3";
                    flagthree = "0";

                    common_select_second(1, 2);

                    break;
                //8-组 1 暂停组 2 开
                case "8":
                    flagone = "2";
                    flagtwo = "1";
                    flagthree = "0";

                    common_select_second(1, 0);

                    break;
            }
            switchState(flagone, flagtwo, flagthree);
        }
    }

    /**
     * 根据状态码status，显示相应UI
     *
     * @param i2
     * @param i3
     */
    private void common_select_second(int i2, int i3) {
        for (int i = 0; i < 3; i++) {
            RadioButton radioButton_out = (RadioButton) radio_group_out.getChildAt(i);
            radioButton_out.setChecked(false);
            if (i == i2) {
                radioButton_out.setChecked(true);
            }
        }

        for (int i = 0; i < 3; i++) {
            RadioButton radioButton_in = (RadioButton) radio_group_in.getChildAt(i);
            radioButton_in.setChecked(false);
            if (i == i3) {
                radioButton_in.setChecked(true);
            }
        }

        for (int i = 0; i < 3; i++) {
            RadioButton radioButton_all = (RadioButton) radio_group_all.getChildAt(i);
            radioButton_all.setChecked(false);
//                        if (i == 1) {
//                            radioButton_all.setChecked(true);
//                        }
        }
    }

    /**
     * 根据状态码status，显示相应UI
     *
     * @param i2
     */
    private void common_select(int i2) {
        for (int i = 0; i < 3; i++) {
            RadioButton radioButton_out = (RadioButton) radio_group_out.getChildAt(i);
            radioButton_out.setChecked(false);
            RadioButton radioButton_in = (RadioButton) radio_group_in.getChildAt(i);
            radioButton_in.setChecked(false);
            RadioButton radioButton_all = (RadioButton) radio_group_all.getChildAt(i);
            radioButton_all.setChecked(false);
            if (i == i2) {
                radioButton_out.setChecked(true);
                radioButton_in.setChecked(true);
                radioButton_all.setChecked(true);
            }
        }
    }

    /*窗帘各个开关状态设置*/
    private void switchState(String flagone, String flagtwo, String flagthree) {
        //进行清空各个操作
        statusClear();
        if (flagone.equals("1")) {
//            curopenrelative_id.setBackgroundResource(R.drawable.hsmall_black);
//            curimage_id.setImageResource(R.drawable.hairopen_word_white);
        } else if (flagone.equals("2")) {
//            curoffrelative_id.setBackgroundResource(R.drawable.hsmall_black);
//            curoffima_id.setImageResource(R.drawable.hairclose_word);
        }
        if (flagtwo.equals("1")) {
//            curopenrelativetwo_id.setBackgroundResource(R.drawable.hsmall_black);
//            curimagetwo_id.setImageResource(R.drawable.hairopen_word_white);
        } else if (flagtwo.equals("2")) {
//            curoffrelativetwo_id.setBackgroundResource(R.drawable.hsmall_black);
//            curoffimatwo_id.setImageResource(R.drawable.hairclose_word);
        }
        if (flagthree.equals("1")) {
//            curopenrelativethree_id.setBackgroundResource(R.drawable.hsmall_black);
//            curimagethree_id.setImageResource(R.drawable.hairopen_word_white);
        } else if (flagthree.equals("2")) {
//            curoffrelativethree_id.setBackgroundResource(R.drawable.hsmall_black);
//            curoffimathree_id.setImageResource(R.drawable.hairclose_word);
        } else if (flagthree.equals("4")) {
            //暂停
//            sucrela.setBackgroundResource(R.drawable.hsmall_black);
//            sucrelaimage.setImageResource(R.drawable.hairpause_word_white);
        }
    }

    private void statusClear() {
        if (whriteone) {

        }

        if (whritetwo) {

        }

        if (whritethree) {

        }
    }

//    private void init_receiver_control() {
//        //注册广播
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ApiTcpReceiveHelper.TIAO_GUANG_RECEIVE_ACTION);
//        registerReceiver(mReceiver, filter);
//
//    }

//    /**
//     * 广播接收
//     */
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            String result = intent.getStringExtra("result");
//            ToastUtil.showToast(CurtainWindowActivity.this, "成功");
//            //获取窗帘控制后状态
//            //
//            Map map = new HashMap();
//            map.put("number", number);
//            map.put("type", type);
//            get_single_device_info(map);
//        }
//    };

//    /**
//     * 获取单个设备或是按钮信息（APP->网关）
//     *
//     * @param map
//     */
//    private void get_single_device_info(final Map map) {
//        MyOkHttp.postMapObject(ApiHelper.sraum_getOneInfo, map,
//                new Mycallback(new AddTogglenInterfacer() {
//                    @Override
//                    public void addTogglenInterfacer() {
//                        get_single_device_info(map);
//                    }
//                }, CurtainWindowActivity.this, null) {
//                    @Override
//                    public void onSuccess(User user) {
////       switch (type) {//空调,PM检测,客厅窗帘,门磁,主灯
////                        String type = (String) map.get("type");
////                        map.put("status",user.status);
////                        map.put("name",user.name)
//                        status = (String) user.status;
//                        number = (String) user.number;
//                        type = (String) user.type;
//                        name = (String) user.name;
//                        name1 = (String) user.name1;
//                        name2 = (String) user.name2;
//                        switch_curtain_status();
//                    }
//                });
//    }
//

    /**
     * 初始化监听事件
     */
    private int radio_out_index;
    private int radio_in_index;
    private int radio_all_index;

    private void init_event() {
        for (int i = 0; i < radio_group_out.getChildCount(); i++) {//外纱
            final RadioButton child = (RadioButton) radio_group_out.getChildAt(i);
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
            final RadioButton child = (RadioButton) radio_group_in.getChildAt(i);
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
            final RadioButton child = (RadioButton) radio_group_all.getChildAt(i);
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
        Map<String, Object> mapalldevice = new HashMap<String, Object>();
        List<Map<String, Object>> listob = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapdevice = new HashMap<String, Object>();

        mapdevice.put("type", type);
        mapdevice.put("number", number);
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
            mapdevice.put("status", statusm);
        }
        Log.e("zhu", "dimmer:" + dimmer);
        mapdevice.put("dimmer", dimmer);
        mapdevice.put("mode", modeflag);
        mapdevice.put("temperature", temperature);
        mapdevice.put("speed", windflag);
        listob.add(mapdevice);
        mapalldevice.put("token", TokenUtil.getToken(CurtainWindowActivity.this));
        mapalldevice.put("boxNumber", boxnumber);
        mapalldevice.put("deviceInfo", listob);
        LogUtil.eLength("真正传入", JSON.toJSONString(mapalldevice));
        getBoxStatus(mapalldevice);
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

    private void getBoxStatus(final Map<String, Object> mapdevice) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(CurtainWindowActivity.this));
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
                map.put("token", TokenUtil.getToken(CurtainWindowActivity.this));
                map.put("boxNumber", boxnumber);
                getBoxStatus_read(mapdevice, map);
            }
        }, CurtainWindowActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(CurtainWindowActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                switch (user.status) {
                    case "1":
                        sraum_device_control(mapdevice);
                        break;
                    case "0":
                        //网关离线
                        ToastUtil.showDelToast(CurtainWindowActivity.this, "网关处于离线状态");
                        break;
                    default:
                        break;
                }
            }

            private void sraum_device_control(Map<String, Object> mapdevice) {
                List<Map> list = (List<Map>) mapdevice.get("deviceInfo");
                Log.e("zhu", "mapdevice->diming:" + list.get(0).get("dimmer"));
                MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        Map<String, Object> map = new HashMap<>();
                        map.put("token", TokenUtil.getToken(CurtainWindowActivity.this));
                        map.put("boxNumber", boxnumber);
                        sraum_device_control(map);

                    }
                }, CurtainWindowActivity.this, dialogUtil) {

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(CurtainWindowActivity.this, "操作失败");
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
//                        if (type.equals("4")) {
//                            switch (statusm == null ? "" : statusm) {
//                                //窗帘1打开
//                                case "1":
//
//                                    break;
//                                //窗帘1关闭
//                                case "2":
//
//                                    break;
//                                //窗帘2打开
//                                case "3":
//
//                                    break;
//                                //窗帘2关闭
//                                case "4":
//
//                                    break;
//                                //全开
//                                case "5":
//
//                                    break;
//                                //全关
//                                case "6":
//
//                                    break;
//                                //暂停
//                                case "7":
//
//                                    break;
//                                default:
//                                    break;
//                            }
////                            switchState(flagone, flagtwo, flagthree);
//                        }
                        change_status_toui(type, statusm == null ? "" : statusm);

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
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    /**
     * 去执行窗帘操作
     */
    private void do_tcp_send_curtain() {
        Map map = new HashMap();
        map.put("number", number);
        map.put("type", type);
        map.put("status", status);
        MyService.getInstance().sraum_send_tcp(map, "sraum_controlButton");
    }

//    /**
//     * 切换窗帘状态
//     */
//    private void switch_curtain_status() {
//        switch (status) {
//            case "0":
//                select_radio_out(2);//打开，暂停，关闭
//                select_radio_in(2);
//                select_radio_all(2);
//                break;
//            case "1":
//                select_radio_out(0);
//                select_radio_in(0);
//                select_radio_all(0);
//                break;
//            case "2":
//                select_radio_out(1);
//                select_radio_in(1);
//                select_radio_all(1);
//                break;
//            case "3":
//                select_radio_out(0);
//                select_radio_in(2);
//                select_radio_all(-1);
//                break;
//            case "4":
//                select_radio_out(0);
//                select_radio_in(1);
//                select_radio_all(-1);
//                break;
//            case "5":
//                select_radio_out(2);
//                select_radio_in(0);
//                select_radio_all(-1);
//                break;
//            case "6":
//                select_radio_out(2);
//                select_radio_in(1);
//                select_radio_all(-1);
//                break;
//            case "7":
//                select_radio_out(1);
//                select_radio_in(2);
//                select_radio_all(-1);
//                break;
//            case "8":
//                select_radio_out(1);
//                select_radio_in(0);
//                select_radio_all(-1);
//                break;
//        }
//    }

//    /**
//     * 外纱
//     */
//    private void select_radio_out(int index) {
//        radio_out_index = index;
//        //外纱
//        for (int i = 0; i < radio_group_out.getChildCount(); i++) {
//            final RadioButton child = (RadioButton) radio_group_out.getChildAt(i);
//            if (index == i) {
//                child.setChecked(true);
//            } else
//                child.setChecked(false);
//        }
//    }

//    /**
//     * 内纱
//     */
//    private void select_radio_in(int index) {
//        //radio_group_in,内纱
//        radio_in_index = index;
//        for (int i = 0; i < radio_group_in.getChildCount(); i++) {
//            final RadioButton child = (RadioButton) radio_group_in.getChildAt(i);
//            if (index == i) {
//                child.setChecked(true);
//            } else
//                child.setChecked(false);
//        }
//    }
//
//    /**
//     * 全部
//     */
//    private void select_radio_all(int index) {
//        //radio_group_all，全部
//        radio_all_index = index;
//        for (int i = 0; i < radio_group_all.getChildCount(); i++) {
//            final RadioButton child = (RadioButton) radio_group_all.getChildAt(i);
//            if (index == i) {
//                child.setChecked(true);
//            } else
//                child.setChecked(false);
//        }
//    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

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
//        unregisterReceiver(mReceiver);
    }
}
