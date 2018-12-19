package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
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

public class AddDoorLockDevActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_id)
    Button next_step_id;

//    private int[] icon = {R.drawable.icon_type_switch, R.drawable.menci_big,
//            R.drawable.human_ganying_big, R.drawable.water, R.drawable.pm25,
//            R.drawable.emergency_button};


    @InjectView(R.id.roundProgressBar2)
    RoundProgressBar_ChangePosition roundProgressBar2;
    @InjectView(R.id.txt_remain_time)
    TextView txt_remain_time;
    private boolean is_index;
    private int position;//灯控，zigbee设备
    //    public static String ACTION_SRAUM_SETBOX = "ACTION_SRAUM_SETBOX";//notifactionId = 8 ->设置网关模式，sraum_setBox
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
    private Map map = new HashMap();
    private String status;
    private String gateway_number;


    @Override
    protected int viewId() {
        return R.layout.add_door_lock_act;
    }

    @Override
    protected void onView() {
        registerMessageReceiver();
        back.setOnClickListener(this);
        next_step_id.setOnClickListener(this);
        dialogUtil = new DialogUtil(AddDoorLockDevActivity.this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);

        roundProgressBar2.setAdd_Delete("delete");
        map = (Map) getIntent().getSerializableExtra("map");
        status = (String) map.get("status");
        type = (String) map.get("type");
        gateway_number = (String) map.get("number");
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
                        AddDoorLockDevActivity.this.finish();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                common_qiut();
                break;
            case R.id.next_step_id:

                common_qiut();
        }
    }


    @Override
    public void onBackPressed() {
        common_qiut();
    }

    private void common_qiut() {
        sraum_setBox_quit("");
        is_index = false;
        AddDoorLockDevActivity.this.finish();
        AppManager.getAppManager().finishActivity_current(
                SelectSmartDoorLockActivity.class
        );
        AppManager.getAppManager().finishActivity_current(
                SelectSmartDoorLockTwoActivity.class
        );
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
                }, AddDoorLockDevActivity.this, null) {
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
                                case "B201":
                                    intent = new Intent(AddDoorLockDevActivity.this,
                                            AddZigbeeDeviceScucessActivity.class);
                                    break;//直流电阀机械手
                            }
                            intent.putExtra("panelid", pannelid);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("deviceList", (Serializable) deviceList);
//                            intent.putExtra("deviceList", (Serializable) deviceList);
                            intent.putExtra("panelType", panelType);
                            intent.putExtra("panelName", panelName);
                            intent.putExtra("panelMAC", panelMAC);
                            intent.putExtra("bundle_panel", bundle);
                            intent.putExtra("findpaneltype", "wangguan_status");
                            startActivity(intent);
                            AddDoorLockDevActivity.this.finish();
                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(AddDoorLockDevActivity.this, "该网关不存在");
                    }
                }
        );
    }

    @NonNull
    private Map send_type() {
        Map map = new HashMap();

        String regId = (String) SharedPreferencesUtil.getData(AddDoorLockDevActivity.this, "regId", "");
        map.put("regId", regId);
        map.put("token", TokenUtil.getToken(this));
//        String boxnumber = (String) SharedPreferencesUtil.getData(this, "boxnumber", "");
        map.put("boxNumber", gateway_number);

//
//        final String type = (String) map.get("type");
//        String status = (String) map.get("status");
//        String gateway_number = (String) gatewayList.get(position).get("number");
        String areaNumber = (String) SharedPreferencesUtil.getData(AddDoorLockDevActivity.this, "", "areaNumber");
        map.put("areaNumber", areaNumber);
        switch (type) {
            case "B201":
                map.put("status", "0");//进入设置模式
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
        String areaNumber = (String) SharedPreferencesUtil.getData(AddDoorLockDevActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(AddDoorLockDevActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("boxNumber", gateway_number);
        map.put("panelNumber", panelid);
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getPanel_devices(panelid);
                    }
                }, AddDoorLockDevActivity.this, dialogUtil) {

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

                        switch (panelType) {
                            case "B201":
                                //智能门锁
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
