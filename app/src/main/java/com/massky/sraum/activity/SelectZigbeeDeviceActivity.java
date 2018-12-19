package com.massky.sraum.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.SelectDevTypeAdapter;
import com.massky.sraum.adapter.SelectWifiDevAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.ConfigDialogFragment;
import com.massky.sraum.fragment.GatewayDialogFragment;
import com.massky.sraum.widget.ListViewForScrollView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.utils.Logger;
import com.yaokan.sdk.utils.Utility;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.GizWifiCallBack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/1/3.
 */

public class SelectZigbeeDeviceActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;

    @InjectView(R.id.mineRoom_list)
    ListViewForScrollView macfragritview_id;
    @InjectView(R.id.mac_wifi_dev_id)
    ListViewForScrollView mac_wifi_dev_id;
    private SelectWifiDevAdapter adapter_wifi;
    private List<Map> gatewayList = new ArrayList<>();

    private int[] icon = {
            R.drawable.icon_type_yijiandk_90, R.drawable.icon_type_liangjiandk_90,
            R.drawable.icon_type_sanjiandk_90, R.drawable.icon_type_sijiandk_90, R.drawable.icon_type_yilutiaoguang_90,
            R.drawable.icon_type_lianglutiaoguang_90, R.drawable.icon_type_sanlutiaoguang_90, R.drawable.icon_type_chuanglianmb_90,
            R.drawable.icon_type_menci_90, R.drawable.icon_type_rentiganying_90,
            R.drawable.icon_type_toa_90, R.drawable.icon_type_yanwucgq_90, R.drawable.icon_type_tianranqibjq_90,
            R.drawable.icon_type_jinjianniu_90, R.drawable.icon_type_zhinengmensuo_90, R.drawable.icon_type_pm25,
            R.drawable.icon_type_shuijin, R.drawable.icon_type_duogongneng, R.drawable.icon_kaiguan_socket_90, R.drawable.icon_wangguan
    };

    //"B301"暂时为多功能模块
    private String[] types = {
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A401",
            "A801", "A901", "A902", "AB01", "AB04", "B001", "B201", "AD01", "AC01", "B301", "B101", "网关"
    };

    //        //type：设备类型，1-灯，2-调光，3-空调，4-窗帘，5-新风，6-地暖,7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手

    //wifi类型
    //wifi类型
    private String[] types_wifi = { //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            "hongwai", "yaokong", "101", "103"
    };

    private int[] icon_wifi = {
            R.drawable.hongwai_s,
            R.drawable.icon_type_yaokongqi,
            R.drawable.icon_type_shexiangtou_90,
            // R.drawable.icon_type_pmmofang_90,
            R.drawable.icon_keshimenling

    };
    private int[] iconNam_wifi = {R.string.hongwai, R.string.yaokongqi, R.string.shexiangtou, R.string.keshimenling};//, R.string.pm_mofang
    private int[] iconName = {R.string.yijianlight, R.string.liangjianlight, R.string.sanjianlight, R.string.sijianlight,
            R.string.yilutiaoguang1, R.string.lianglutiaoguang1, R.string.sanlutiao1, R.string.window_panel1
            , R.string.menci, R.string.rentiganying, R.string.jiuzuo, R.string.yanwu, R.string.tianranqi, R.string.jinjin_btn,
            R.string.zhineng, R.string.pm25, R.string.shuijin, R.string.duogongneng, R.string.cha_zuo_2, R.string.wangguan
    };

    private SelectDevTypeAdapter adapter;
    private ConfigDialogFragment newFragment;
    private GatewayDialogFragment newGatewayFragment;
    private DialogUtil dialogUtil;

    private List<Map> list_hand_scene = new ArrayList<>();

    /**
     * 小苹果绑定列表
     */
    private DeviceManager mDeviceManager;
    List<GizWifiDevice> wifiDevices = new ArrayList<GizWifiDevice>();
    private List<String> deviceNames = new ArrayList<String>();
    private GizWifiDevice mGizWifiDevice = null;
    private List<Map> wifi_apple_list = new ArrayList<>();

    private String TAG = "robin debug";
    private String mac;
    private String number;
    private Handler handler = new Handler();

    @Override
    protected int viewId() {
        return R.layout.add_device_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
        initDialog();
        initGatewayDialog();
        onWifi();
    }

    private void onWifi() {
        mDeviceManager = DeviceManager
                .instanceDeviceManager(getApplicationContext());
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    /*
     * 初始化dialog
     */
    private void initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigDialogFragment.newInstance(SelectZigbeeDeviceActivity.this, "", "");//初始化快配和搜索设备dialogFragment
    }

    /*
     * 初始化dialog
     */
    private void initGatewayDialog() {
        // TODO Auto-generated method stub
        newGatewayFragment = GatewayDialogFragment.newInstance(SelectZigbeeDeviceActivity.this, "", "");//初始化快配和搜索设备dialogFragment
//        newGatewayFragment = (GatewayDialogFragment) getGatewayInterfacer;
        getGatewayInterfacer = (GetGatewayInterfacer) newGatewayFragment;
    }

    /**
     * 展示全窗体dialog对话框
     */
    private void show_dialog_fragment() {
        if (!newFragment.isAdded()) {//DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(newFragment, "dialog");
            ft.commit();
        }
    }

    /**
     * 展示网关列表全窗体dialog对话框
     */
    private void show_gateway_dialog_fragment() {
        if (!newGatewayFragment.isAdded()) {//DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(newGatewayFragment, "dialog");
            ft.commit();
        }
    }

    /**
     * 获取选择区域下的所有网关
     *
     * @param map
     */
    private void sraum_getAllGateWays(final Map map) {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }

        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(SelectZigbeeDeviceActivity.this));
        String areaNumber = (String) SharedPreferencesUtil.getData(SelectZigbeeDeviceActivity.this, "areaNumber", "");
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllBox
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getAllGateWays(map);
                    }
                }, SelectZigbeeDeviceActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                    }

                    @Override
                    public void onSuccess(final User user) {
                        gatewayList = new ArrayList<>();
                        for (int i = 0; i < user.gatewayList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.gatewayList.get(i).name);
                            mapdevice.put("number", user.gatewayList.get(i).number);
                            gatewayList.add(mapdevice);
                        }
//
//                        if (user.gatewayList != null && user.gatewayList.size() != 0) {//区域命名
//                            showGatewayListAdapter = new ShowGatewayListAdapter(getActivity()
//                                    , gatewayList);
//                            list_show_rev_item.setAdapter(showGatewayListAdapter);
//                        }

                        if (gatewayList.size() != 0) {
                            show_gateway_dialog_fragment();
                            if (getGatewayInterfacer != null)
                                getGatewayInterfacer.sendGateWayparams(map, gatewayList);
                        } else {
                            ToastUtil.showToast(SelectZigbeeDeviceActivity.this, "网关列表为空");
                        }
                    }
                });
    }

    /**
     * 给全屏窗体传参数
     */
    private GetGatewayInterfacer getGatewayInterfacer;

    public interface GetGatewayInterfacer {
        void sendGateWayparams(Map map, List<Map> gatewayList);
    }

    @Override
    protected void onData() {
        adapter = new SelectDevTypeAdapter(SelectZigbeeDeviceActivity.this, icon, iconName);
        macfragritview_id.setAdapter(adapter);


        adapter_wifi = new SelectWifiDevAdapter(SelectZigbeeDeviceActivity.this, icon_wifi, iconNam_wifi);
        mac_wifi_dev_id.setAdapter(adapter_wifi);
        macfragritview_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (types[position]) {
                    case "A201":
                    case "A202":
                    case "A203":
                    case "A204":
                    case "A301":
                    case "A302":
                    case "A303":
                    case "A401":
                    case "B301":
                    case "B101":
                    case "A902":
                    case "AD01":
                    case "A501":
                    case "B201":
//                    case "A511":
//                        sraum_setBox_accent(types[position], "normal");
                        sraum_setBox_accent(types[position], "normal");
                        break;
                    case "A801":
                    case "A901":
                    case "AB01":
                    case "AB04":
                    case "B001":
                    case "AC01":
//                        sraum_setBox_accent(types[position], "zigbee");
                        sraum_setBox_accent(types[position], "zigbee");
                        break;
//                    case "B201":
//                        Intent intent_position = new Intent(SelectZigbeeDeviceActivity.this, SelectSmartDoorLockActivity.class);
//                        intent_position.putExtra("type", types[position]);
//                        startActivity(intent_position);
////                        show_gateway_dialog_fragment();
//                        break;
                    case "网关":
                        show_dialog_fragment();
                        break;//添加网关
                }
//                ToastUtil.showToast(SelectZigbeeDeviceActivity.this, "添加网关");
            }
        });


        mac_wifi_dev_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_wifi = null;
                switch (types_wifi[position]) {
                    case "101":
                    case "103":
                        intent_wifi = new Intent(SelectZigbeeDeviceActivity.this, AddWifiDevActivity.class);
                        intent_wifi.putExtra("type", types_wifi[position]);
                        startActivity(intent_wifi);
                        break;
                    case "102":
                        break;
                    case "hongwai":
                        intent_wifi = new Intent(SelectZigbeeDeviceActivity.this, AddWifiDevActivity.class);
                        intent_wifi.putExtra("type", types_wifi[position]);
                        startActivity(intent_wifi);
                        break;
                    case "yaokong":
                        getOtherDevices();
                        break;
                }
            }
        });
    }

    /**
     * 设置网关开始模式
     *
     * @param type
     * @param normal
     */
    private void sraum_setBox_accent(String type, String normal) {
        Map map = new HashMap();
        map.put("type", type);
        switch (normal) {
            case "normal":
                map.put("status", "1");//普通进入设置模式
                break;
            case "zigbee":
                map.put("status", "12");//zigbee进入设置模式
                break;
        }
        sraum_getAllGateWays(map);
    }

    /**
     * 获取门磁等第三方设备
     */
    private void getOtherDevices() {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(SelectZigbeeDeviceActivity.this));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleInfos
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        getOtherDevices();
                    }
                }, SelectZigbeeDeviceActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                    }

                    @Override
                    public void onSuccess(final User user) {
                        list_hand_scene = new ArrayList<>();
                        for (int i = 0; i < user.controllerList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.controllerList.get(i).name);
                            mapdevice.put("number", user.controllerList.get(i).number);
                            mapdevice.put("type", user.controllerList.get(i).type);
                            mapdevice.put("controllerId", user.controllerList.get(i).controllerId);
                            list_hand_scene.add(mapdevice);
                        }

                        if (list_hand_scene.size() == 1) {
                            if (wifiDevices.size() != 0) {
                                choose_the_brand(0);
                            } else {
//                                update(mDeviceManager.getCanUseGizWifiDevice());
                                ToastUtil.showToast(SelectZigbeeDeviceActivity.this, "请与" + list_hand_scene.get(0)
                                        .get("name").toString()
                                        +
                                        "在同一网络后再添加");
                            }
                        } else {
                            Intent intent_wifi =
                                    new Intent(SelectZigbeeDeviceActivity.this,
                                            SelectInfraredForwardActivity.class);
                            intent_wifi.putExtra("list_hand_scene", (Serializable) list_hand_scene);
                            startActivity(intent_wifi);
                        }
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDeviceManager.setGizWifiCallBack(mGizWifiCallBack);
        update(mDeviceManager.getCanUseGizWifiDevice());
    }


    private GizWifiCallBack mGizWifiCallBack = new GizWifiCallBack() {

        @Override
        public void didUnbindDeviceCd(GizWifiErrorCode result, String did) {
            super.didUnbindDeviceCd(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, "解除绑定成功");
            } else {
                // 解绑失败
                Logger.d(TAG, "解除绑定失败");
            }
        }

        @Override
        public void didBindDeviceCd(GizWifiErrorCode result, String did) {    //
            super.didBindDeviceCd(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {//
                // 绑定成功
                Logger.d(TAG, "绑定成功");
            } else {
                // 绑定失败
                Logger.d(TAG, "绑定失败");
            }
        }

        @Override
        public void didSetSubscribeCd(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            super.didSetSubscribeCd(result, device, isSubscribed);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, (isSubscribed ? "订阅" : "取消订阅") + "成功");
            } else {
                // 解绑失败
                Logger.d(TAG, "订阅失败");
            }
        }

        @Override
        public void discoveredrCb(GizWifiErrorCode result,
                                  List<GizWifiDevice> deviceList) {
            Logger.d(TAG,
                    "discoveredrCb -> deviceList size:" + deviceList.size()
                            + "  result:" + result);
            switch (result) {
                case GIZ_SDK_SUCCESS:
                    Logger.e(TAG, "load device  sucess");
                    update(deviceList);
//                    if(deviceList.get(0).getNetStatus()==GizWifiDeviceNetStatus.GizDeviceOffline)

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 跳转到选择品牌界面
     *
     * @param position
     */
    private void choose_the_brand(int position) {
        mac = (String) list_hand_scene.get(position).get("controllerId");
        number = list_hand_scene.get(position).get("number").toString();
        //去根据mac去服务器端下载GizWifiDevice
        String apple_name = "";
        for (int i = 0; i < list_hand_scene.size(); i++) {
            if (list_hand_scene.get(i).get("controllerId").equals(mac)) {
                apple_name = list_hand_scene.get(i).get("name").toString();

            }
        }
        get_to_wifi(mac, apple_name);
    }

    private void get_to_wifi(String mac, String apple_name) {

        for (int i = 0; i < wifiDevices.size(); i++) {
            if (wifiDevices.get(i).getMacAddress().equals(mac)) {
                mGizWifiDevice = wifiDevices.get(i);
            }
        }
        if (!Utility.isEmpty(mGizWifiDevice)) { //
            mDeviceManager.bindRemoteDevice(mGizWifiDevice);
            final GizWifiDevice finalMGizWifiDevice = mGizWifiDevice;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDeviceManager.setSubscribe(finalMGizWifiDevice, true);
                }
            }, 1000);
        } else {
            ToastUtil.showToast(SelectZigbeeDeviceActivity.this, "请与" + apple_name
                    +
                    "在同一网络后再控制");
            return;
        }
        toControlApplianAct();
    }

    private void toControlApplianAct() {
        Intent intent = new Intent(SelectZigbeeDeviceActivity.this, SelectControlApplianceActivity.class);
        if (mGizWifiDevice == null) {
            return;
        }
        intent.putExtra("GizWifiDevice", mGizWifiDevice);
//        intent.putExtra("tid", getIntent().getSerializableExtra("tid"));
        intent.putExtra("number", number);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectZigbeeDeviceActivity.this.finish();
                break;
        }
    }

    void update(List<GizWifiDevice> gizWifiDevices) {
        GizWifiDevice mGizWifiDevice = null;


        if (gizWifiDevices == null) {
            deviceNames.clear();
        } else if (gizWifiDevices != null && gizWifiDevices.size() >= 1) {
//            wifiDevices.clear();
            wifiDevices.addAll(gizWifiDevices);
            HashSet<GizWifiDevice> h = new HashSet<GizWifiDevice>(wifiDevices);
            wifiDevices.clear();
            for (GizWifiDevice gizWifiDevice : h) {
                wifiDevices.add(gizWifiDevice);
            }
            deviceNames.clear();
//            for (int i = 0; i < wifiDevices.size(); i++) {
////                deviceNames.add(wifiDevices.get(i).getProductName() + "("
////                        + wifiDevices.get(i).getMacAddress() + ") "
////                        + getBindInfo(wifiDevices.get(i).isBind()) + "\n"
////                        + getLANInfo(wifiDevices.get(i).isLAN()) + "  " + getOnlineInfo(wifiDevices.get(i).getNetStatus()));
//                mGizWifiDevice = wifiDevices.get(i);
//                // list_hand_scene
//                // 绑定选中项
//                if (!Utility.isEmpty(mGizWifiDevice)) { //
//                    mDeviceManager.bindRemoteDevice(mGizWifiDevice);
//                    final GizWifiDevice finalMGizWifiDevice = mGizWifiDevice;
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mDeviceManager.setSubscribe(finalMGizWifiDevice, true);
//                        }
//                    }, 1000);
//                }
//            }
            //去绑定和订阅
        }
//        adapter.notifyDataSetChanged();
    }
}
