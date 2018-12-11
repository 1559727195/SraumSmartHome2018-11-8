package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.ConfigZigbeeConnDialogFragment;
import com.massky.sraum.view.RoundProgressBar_ChangePosition;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.activity.MainGateWayActivity.ACTION_SRAUM_SETBOX;


/**
 * Created by zhu on 2018/5/30.
 */

public class AddZigbeeDevActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_id)
    Button next_step_id;
    private ConfigZigbeeConnDialogFragment newFragment;

//    private int[] icon = {R.drawable.icon_type_switch, R.drawable.menci_big,
//            R.drawable.human_ganying_big, R.drawable.water, R.drawable.pm25,
//            R.drawable.emergency_button};

    private int[] icon = {R.drawable.pic_zigbee_kaiguan_1, R.drawable.pic_zigbee_kaiguan_2,
            R.drawable.pic_zigbee_kaiguan_3, R.drawable.pic_zigbee_kaiguan_4,
            R.drawable.pic_zigbee_kaiguan_1_tiaoguang,
            R.drawable.pic_zigbee_kaiguan_2_tiaoguang,
            R.drawable.pic_zigbee_kaiguan_3_tiaoguang,
            R.drawable.pic_zigbee_kaiguan_chuanglian,
            R.drawable.pic_zigbee_kaiguan_1,
            R.drawable.pic_zigbee_menci,
            R.drawable.pic_zigbee_rentiganying,
            R.drawable.pic_zigbee_jiuzuo,
            R.drawable.pic_zigbee_yanwu,
            R.drawable.pic_zigbee_tianranqi,
            R.drawable.pic_zigbee_jinjianniu,
            R.drawable.pic_zigbee_zhinengmensuo,
            R.drawable.pic_zigbee_pm250,
            R.drawable.pic_zigbee_shuijin,
            R.drawable.pic_zigbee_duogongneng,
            R.drawable.pic_zigbee_chazuo
    };

    private int[] iconString = {
            R.string.yijianlight_promat,
            R.string.zigbee_other_promat, R.string.zigbee_promat_btn_down
            , R.string.zhinengchazuo_promat
    };

    @InjectView(R.id.img_show_zigbee)
    ImageView img_show_zigbee;
    @InjectView(R.id.promat_zigbee_txt)
    TextView promat_zigbee_txt;
    @InjectView(R.id.roundProgressBar2)
    RoundProgressBar_ChangePosition roundProgressBar2;
    @InjectView(R.id.txt_remain_time)
    TextView txt_remain_time;
    private boolean is_index;
    private int position;//灯控，zigbee设备
    private MessageReceiver mMessageReceiver;
    private DialogUtil dialogUtil;

    private String panelType;
    private String panelName;
    private String panelNumber;
    private String deviceNumber;
    private String panelMAC;
    private List<User.device> deviceList = new ArrayList<>();
    private List<User.panellist> panelList = new ArrayList<>();
    private String type = "";
    private String gateway_number;

    @Override
    protected int viewId() {
        return R.layout.add_zigbee_new_dev_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        registerMessageReceiver();
        back.setOnClickListener(this);
        next_step_id.setOnClickListener(this);
        dialogUtil = new DialogUtil(AddZigbeeDevActivity.this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);


        roundProgressBar2.setAdd_Delete("delete");
        initDialog();
        type = (String) getIntent().getSerializableExtra("type");
        gateway_number = (String) getIntent().getSerializableExtra("boxNumber");//网关编号
//        intent_position.putExtra("boxNumber", gateway_number);
        switch (type) {
            case "A201":
                img_show_zigbee.setImageResource(icon[0]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A202":
                img_show_zigbee.setImageResource(icon[1]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A203":
                img_show_zigbee.setImageResource(icon[2]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A204":
                img_show_zigbee.setImageResource(icon[3]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A301":
                img_show_zigbee.setImageResource(icon[4]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A302":
                img_show_zigbee.setImageResource(icon[5]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A303":
                img_show_zigbee.setImageResource(icon[6]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A401":
                img_show_zigbee.setImageResource(icon[7]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A501":
                img_show_zigbee.setImageResource(icon[8]);
                promat_zigbee_txt.setText(iconString[0]);
                break;
            case "A801":
                img_show_zigbee.setImageResource(icon[9]);
                promat_zigbee_txt.setText(iconString[1]);
                break;
            case "A901":
                img_show_zigbee.setImageResource(icon[10]);
                promat_zigbee_txt.setText(iconString[1]);
                break;
            case "A902":
                img_show_zigbee.setImageResource(icon[11]);
                promat_zigbee_txt.setText(iconString[2]);
                break;
            case "AB01":
                img_show_zigbee.setImageResource(icon[12]);
                promat_zigbee_txt.setText(iconString[1]);
                break;
            case "AB04":
                img_show_zigbee.setImageResource(icon[13]);
                promat_zigbee_txt.setText(iconString[1]);
                break;
            case "B001":
                img_show_zigbee.setImageResource(icon[14]);
                promat_zigbee_txt.setText(iconString[1]);
                break;
            case "B201":
                //智能门锁
                img_show_zigbee.setImageResource(icon[15]);
                promat_zigbee_txt.setText(iconString[2]);
                break;
            case "AD01":
                img_show_zigbee.setImageResource(icon[16]);
                promat_zigbee_txt.setText(iconString[1]);
                break;
            case "AC01":
                img_show_zigbee.setImageResource(icon[17]);
                promat_zigbee_txt.setText(iconString[1]);
                break;
            case "B301":
                img_show_zigbee.setImageResource(icon[18]);
                promat_zigbee_txt.setText(iconString[2]);
                break;
            case "B101":
                img_show_zigbee.setImageResource(icon[19]);
                promat_zigbee_txt.setText(iconString[3]);
                break;
        }
        init_status_bar();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void init_status_bar() {
        roundProgressBar2.setMax(255);
        final int[] index = {255};
//        double c = (double) 100 / 90;//c = (10.0/3) = 3.333333
//        final float process = (float) c; //剩余30秒
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                is_index = true;
                while (is_index) {
                    try {
                        Thread.sleep(1000);
                        roundProgressBar2.setProgress(i);
                        i++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                progress_loading_linear.setVisibility(View.GONE);
//                                loading_error_linear.setVisibility(View.VISIBLE);
                                if (index[0] >= 0) {
                                    txt_remain_time.setText("剩余" + index[0] + "秒");
                                } else {
                                    txt_remain_time.setText("剩余" + 0 + "秒");
                                }
                                index[0]--;
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (index[0] < 0) {
                        sraum_setBox_quit("");
                        is_index = false;
//                        if(getActivity() != null)
//                        txt_remain_time.setText("剩余" + 0 + "秒");
                        AddZigbeeDevActivity.this.finish();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                sraum_setBox_quit("");
                is_index = false;
                AddZigbeeDevActivity.this.finish();

                break;
            case R.id.next_step_id:

                sraum_setBox_quit("");
                is_index = false;
                AddZigbeeDevActivity.this.finish();
        }
    }


    @Override
    public void onBackPressed() {
        sraum_setBox_quit("");
        is_index = false;
        AddZigbeeDevActivity.this.finish();
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigZigbeeConnDialogFragment.newInstance(AddZigbeeDevActivity.this, "", "", new ConfigZigbeeConnDialogFragment.DialogClickListener() {

            @Override
            public void doRadioWifi() {//wifi快速配置

            }

            @Override
            public void doRadioScanDevice() {

            }

            @Override
            public void dialogDismiss() {

            }

        });//初始化快配和搜索设备dialogFragment

        connWifiInterfacer = (ConnWifiInterfacer) newFragment;
    }

    private ConnWifiInterfacer connWifiInterfacer;

    public interface ConnWifiInterfacer {
        void conn_wifi_interface();
    }


    private void sraum_setBox_quit(final String pannelid) {
        //在这里先调
        //设置网关模式-sraum-setBox
        Map map = send_type();
        doit(pannelid, map);
    }

    private void doit(final String pannelid, Map map) {
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
//                        sraum_setBox_quit(pannelid, position);
                    }
                }, AddZigbeeDevActivity.this, null) {
                    @Override
                    public void onSuccess(User user) {
                        //退出设置网关模式成功，后，
//                        if (!panelid.equals("")) {
//                            Intent intent = new Intent(MacdeviceActivity.this,
//                                    ChangePanelAndDeviceActivity.class);
//                            intent.putExtra("panelid", panelid);
//
//                            startActivity(intent);
//                            MacdeviceActivity.this.finish();
//                        }

//
//                        deviceList.addAll(user.deviceList);
//
//                        //面板的详细信息
//                        panelType = user.panelType;
//                        panelName = user.panelName;
//                        panelMAC = user.panelMAC;
                        Intent intent = null;
                        if (!pannelid.equals("")) {
                            switch (panelType) {
                                case "A201"://一灯控
                                case "A202"://二灯控
                                case "A203"://三灯控
                                case "A204"://四灯控
                                case "A301"://一键调光，3键灯控  设备4个
                                case "A302"://两键调光，2键灯控
                                case "A303"://三键调光，一键灯控
                                case "A311":
                                case "A312":
                                case "A313":
                                case "A321":
                                case "A322":
                                case "A331":
                                case "A401"://设备2个
                                case "A501"://设备2个
                                case "A601"://设备2个
                                case "A701"://设备2个

                                    intent = new Intent(AddZigbeeDevActivity.this,
                                            ChangePanelAndDeviceActivity.class);
                                    break;
                                case "A511"://设备2个
                                case "A611"://设备2个
                                case "A711"://设备2个
                                case "A801":
                                    //门磁
                                case "A901":
                                    //人体感应
                                case "A902":
                                    //TOA久坐报警器
                                case "AB01":
                                    //烟雾报警器
                                case "AB04":
                                    //天然气报警器
                                case "AC01":
                                    //水浸检测器
                                case "AD01":
                                    //PM2.5入墙
                                case "AD02":
                                    //PM2.5魔方
                                case "B001":
                                    //紧急按钮
                                case "B101":
                                    //86插座一位
                                case "B201":
                                case "B202":
                                    //智能门锁
                                case "B301"://直流电阀机械手
                                    intent = new Intent(AddZigbeeDevActivity.this,
                                            AddZigbeeDeviceScucessActivity.class);
                                    break;//直流电阀机械手
                            }
                            intent.putExtra("panelid", pannelid);
                            intent.putExtra("boxNumber", gateway_number);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("deviceList", (Serializable) deviceList);
//                            intent.putExtra("deviceList", (Serializable) deviceList);
                            intent.putExtra("panelType", panelType);
                            intent.putExtra("panelName", panelName);
                            intent.putExtra("panelMAC", panelMAC);
                            intent.putExtra("bundle_panel", bundle);
                            intent.putExtra("findpaneltype", "wangguan_status");
                            startActivity(intent);
                            AddZigbeeDevActivity.this.finish();
                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(AddZigbeeDevActivity.this, "该网关不存在");
                    }
                }
        );
    }

    @NonNull
    private Map send_type() {
        Map map = new HashMap();
        String regId = (String) SharedPreferencesUtil.getData(AddZigbeeDevActivity.this, "regId", "");
        map.put("regId", regId);
        map.put("token", TokenUtil.getToken(this));
//        String boxnumber = (String) SharedPreferencesUtil.getData(this, "boxnumber", "");
        map.put("boxNumber", gateway_number);

//
//        final String type = (String) map.get("type");
//        String status = (String) map.get("status");
//        String gateway_number = (String) gatewayList.get(position).get("number");
        String areaNumber = (String) SharedPreferencesUtil.getData(AddZigbeeDevActivity.this, "", "areaNumber");
        map.put("areaNumber", areaNumber);

//        map.put("phoneId", phoned);
        switch (type) {
            case "A201":
            case "A202":
            case "A203":
            case "A204":
            case "A301":
            case "A302":
            case "A303":
            case "A401":
            case "B101":
            case "B201":
            case "B301":
            case "A902":
            case "A501":
            case "AD01":
//                    case "A501":
//                    case "A511":
                map.put("status", "0");//进入设置模式
                break;
            case "A801":
            case "A901":
            case "AB01":
            case "AB04":
            case "B001":
            case "AC01":
                map.put("status", "13");//进入设置模式
                break;
        }
        return map;
    }

    /**
     * 动态注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SRAUM_SETBOX);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(ACTION_SRAUM_SETBOX)) {
                int messflag = intent.getIntExtra("notifactionId", 0);
                String panelid = intent.getStringExtra("panelid");
                if (messflag == 8) {//notifactionId = 8 ->设置网关模式，sraum_setBox
                    //收到服务器端设置网关成功以后，跳转到修改面板名称，以及该面板下设备列表名称

                    //在网关转圈界面，下去拉设备，判断设备类型，不是我们的。网关不关，是我们的设备类型；在关网关。
                    //然后跳转到显示设备列表界面。
//                    ToastUtil.showToast(MacdeviceActivity.this,"messflag:" + messflag);
                    getPanel_devices(panelid);
                }
            }
        }
    }

    /**
     * 添加面板下的设备信息
     *
     * @param panelid
     */
    private void getPanel_devices(final String panelid) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(AddZigbeeDevActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(AddZigbeeDevActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("boxNumber", gateway_number);
        map.put("panelNumber", panelid);
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getPanel_devices(panelid);
                    }
                }, AddZigbeeDevActivity.this, dialogUtil) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        panelList.clear();
                        deviceList.clear();
                        deviceList.addAll(user.deviceList);

                        //面板的详细信息
                        panelType = user.panelType;
                        panelName = user.panelName;
                        panelMAC = user.panelMAC;

                        switch (panelType) {//
                            case "A201"://一灯控
                            case "A202"://二灯控
                            case "A203"://三灯控
                            case "A204"://四灯控
                            case "A301"://一键调光，3键灯控  设备4个
                            case "A302"://两键调光，2键灯控
                            case "A303"://三键调光，一键灯控
                            case "A311":
                            case "A312":
                            case "A313":
                            case "A321":
                            case "A322":
                            case "A331":
                            case "A401"://设备2个
                            case "A511"://空调-设备1个
                            case "A611"://新风
                            case "A711"://地暖
                            case "A501"://空调-设备1个
                            case "A601"://新风
                            case "A701"://地暖
                            case "A801":
                                //门磁
                            case "A901":
                                //人体感应
                            case "A902":
                                //TOA久坐报警器
                            case "AB01":
                                //烟雾报警器
                            case "AB04":
                                //天然气报警器
                            case "AC01":
                                //水浸检测器
                            case "AD01":
                                //PM2.5入墙
                            case "AD02":
                                //PM2.5魔方
                            case "B001":
                                //紧急按钮
                            case "B101":
                            case "B201":
                                //智能门锁
                            case "B301"://直流电阀机械手
                                sraum_setBox_quit(panelid);
                                break;
                            default://其他的面板类型的面，界面不跳转并且，网关不关闭

                                break;
                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

}
