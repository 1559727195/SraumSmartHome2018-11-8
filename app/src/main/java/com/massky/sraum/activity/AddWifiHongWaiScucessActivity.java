package com.massky.sraum.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.AddTogenInterface.AddTogglenInterfacer;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.utils.Logger;
import com.yaokan.sdk.utils.Utility;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.GizWifiCallBack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.Util.DisplayUtil.dip2px;


/**
 * Created by zhu on 2018/1/8.
 */

public class AddWifiHongWaiScucessActivity extends BaseActivity {
    @InjectView(R.id.next_step_txt)
    ImageView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private PopupWindow popupWindow;
    private String device_name;
    private List<User.panellist> panelList = new ArrayList<>();
    private DialogUtil dialogUtil;
    private List<User.device> deviceList = new ArrayList<>();
    private String panelType;
    private String panelName;
    private String panelNumber;
    private String deviceNumber;
    private String panelMAC;
    @InjectView(R.id.dev_name)
    ClearEditText dev_name;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    private String TAG = "robin debug";
    List<GizWifiDevice> wifiDevices = new ArrayList<GizWifiDevice>();
    private List<String> deviceNames = new ArrayList<String>();
    private DeviceManager mDeviceManager;
    private String wifi_name = "";
    private String macDevice;
    private Map map_device = new HashMap();
    private List<GizWifiDevice> gizWifiDevices = new ArrayList<>();
    private List<Map> list_mac_wifi = new ArrayList<>();
    private String deviceInfo = "";
    private GizWifiDevice currGizWifiDevice;
    private String deviceInfo1;

    @Override
    protected int viewId() {
        return R.layout.add_wifi_deivice_scucess;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        back.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
        dialogUtil = new DialogUtil(this);
        next_step_txt.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        get_panel_detail();
        mDeviceManager = DeviceManager
                .instanceDeviceManager(getApplicationContext());
        mDeviceManager.setGizWifiCallBack(mGizWifiCallBack);

//        wifi_name = (String) getIntent().getSerializableExtra("wifi_name");
//        //   intent.putExtra("deviceInfo",deviceInfo);
//        deviceInfo = (String) getIntent().getSerializableExtra("deviceInfo");
//        update(mDeviceManager.getCanUseGizWifiDevice());
        map_device = (Map) getIntent().getSerializableExtra("gizWifiDevice");
        currGizWifiDevice = (GizWifiDevice) getIntent().getParcelableExtra(
                "GizWifiDevice");
        add_bind_dingyue();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

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
        public void didBindDeviceCd(GizWifiErrorCode result, String did) {
            super.didBindDeviceCd(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
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
//                list_remotecontrol_air = SharedPreferencesUtil.getInfo_List(getActivity(), "remoteairlist");
                //在订阅的地方测试，保存本地

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
//                    update(deviceList);
//                    if(deviceList.get(0).getNetStatus()==GizWifiDeviceNetStatus.GizDeviceOffline)

                    break;
                default:
                    break;
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
//                    send_to_wifi();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            deviceInfo = ParceUtil.object2String(currGizWifiDevice);
//                         GizWifiDevice gizWifiDevice =  ParceUtil.unmarshall(deviceInfo,GizWifiDevice.CREATOR);
//                            Map<String, Object> map = EntityUtils.entityToMap(currGizWifiDevice);
////                            String json = new Gson().toJson(map);
//                            String json = JSON.toJSONString(map);
//                            map = EntityUtils.filterMap(map);
//                            deviceInfo = new Gson().toJson(map);
//                           GizWifiDevice gizWifiDevice =  EntityUtils.mapToEntity(map, GizWifiDevice.CREATOR);
////                            test();
//                            Log.e("robin debug", "gizWifiDevice->mac:" + gizWifiDevice.getMacAddress());
//                            String gson_str = new Gson().toJson(map);
//                            Log.e("robin debug", "gson_str:" + json);
//                            SharedPreferencesUtil.saveData(AddWifiHongWaiScucessActivity.this, "mGizWifiDevice", deviceInfo);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sraum_addWifiApple(dev_name.getText().toString().trim());
                                }
                            });
                        }
                    }).start();
                    break;
            }
        }
    };

//    private void test() {
////        parcel.writeString("5CCF7FB6C07C");
////        parcel.writeString("8VjU9aAxMZCLGTKoR7u8eZ");
////        parcel.writeString("192.168 .169 .219");
////        parcel.writeString("0f998d408465430ea435b48f58a7ac3b");
////        parcel.writeString("小苹果");
////        parcel.writeString("");
////        parcel.writeString("");
////        parcel.writeString("");
////        parcel.writeByte((byte)(1));
////        parcel.writeByte((byte)(1));
////        parcel.writeByte((byte)(1));
////        parcel.writeByte((byte)(1));
////        parcel.writeByte((byte)(1));
////        parcel.writeSerializable(GizWifiDeviceNetStatus.GizDeviceControlled);
////        parcel.writeSerializable(GizWifiDeviceType.GizDeviceNormal);
//
//        Map map = new HashMap();
//        map.put("macAddress", "5CCF7FB6C07C");
//        map.put("did", "8VjU9aAxMZCLGTKoR7u8eZ");
//        map.put("ipAddress", "192.168 .169 .219");
//        map.put("productKey", "0f998d408465430ea435b48f58a7ac3b");
//        map.put("productName", "小苹果");
//        map.put("remark", "");
//        map.put("alias", "");
//        map.put("productUI", "");
//        map.put("isLAN", (byte) (1));
//        map.put("subscribed", (byte) (1));
//        map.put("isBind", (byte) (1));
//        map.put("hasProductDefine", (byte) (1));
//        map.put("isDisabled", (byte) (1));
//        map.put("netStatus", GizWifiDeviceNetStatus.GizDeviceControlled);
//        map.put("productType", GizWifiDeviceType.GizDeviceNormal);
//        deviceInfo = new Gson().toJson(map);
////        deviceInfo = (String) JSON.toJSON(map);
//    }

//    void update(List<GizWifiDevice> gizWifiDevices) {
//        this.gizWifiDevices = gizWifiDevices;
//        if (gizWifiDevices != null) {
//            Log.e("DeviceListActivity", gizWifiDevices.size() + "");
//            GizWifiDevice mGizWifiDevice = null;
//
//            //红外转发器绑定设备
//            for (int i = 0; i < gizWifiDevices.size(); i++) {
//                mGizWifiDevice = gizWifiDevices.get(i);
//                // list_hand_scene
//                // 绑定选中项
//                if (!Utility.isEmpty(mGizWifiDevice) && !mGizWifiDevice.isBind()) {
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
//        }
//        if (gizWifiDevices == null) {
//            deviceNames.clear();
//        } else if (gizWifiDevices != null && gizWifiDevices.size() >= 1) {
//            wifiDevices.clear();
//            wifiDevices.addAll(gizWifiDevices);
////            deviceNames.clear();
////            for (int i = 0; i < wifiDevices.size(); i++) {
////                deviceNames.add(wifiDevices.get(i).getProductName() + "("
////                        + wifiDevices.get(i).getMacAddress() + ") "
////                        + getBindInfo(wifiDevices.get(i).isBind()) + "\n"
////                        + getLANInfo(wifiDevices.get(i).isLAN()) + "  " + getOnlineInfo(wifiDevices.get(i).getNetStatus()));
////
////            }
//
//
//            //最新的红外模块位于最后一个索引
//            macDevice = wifiDevices.get(wifiDevices.size() - 1).getMacAddress();
//            map_device = new HashMap();
//            map_device.put("type", "AA02");
//            map_device.put("mac", macDevice);
//            map_device.put("controllerId", macDevice);
//            map_device.put("wifi", wifi_name);
////            sraum_addWifiApple();
//        }
////
////        adapter.notifyDataSetChanged();
//    }


//    private void send_to_wifi() {
//        ToastUtil.showToast(AddWifiHongWaiScucessActivity.this,"WIFI红外开始上传");
//        MyOkHttp.postMapObject_WIFI(ApiHelper.sraum_addWifiApple_WIFI, wifiDevices.get(wifiDevices.size() - 1),
//                new Mycallback(new AddTogglenInterfacer() {//刷新togglen获取新数据
//                    @Override
//                    public void addTogglenInterfacer() {
//
//                    }
//                }, AddWifiHongWaiScucessActivity.this, dialogUtil) {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        super.onError(call, e, id);
//                        AddWifiHongWaiScucessActivity.this.finish();
//                    }
//
//                    @Override
//                    public void onSuccess(User user) {
//                        ToastUtil.showToast(AddWifiHongWaiScucessActivity.this,"WIFI红外上传成功");
//                    }
//
//                    @Override
//                    public void wrongToken() {
//                        super.wrongToken();
//                    }
//
//                    @Override
//                    public void threeCode() {
//                        super.threeCode();
//                    }
//
//                    @Override
//                    public void fourCode() {
//                        super.fourCode();
//                    }
//                });
//    }

    /**
     * 添加 wifi 红外转发设备
     */
    private void sraum_addWifiApple(final String name) {

//        String deviceInfo  = add_bind_dingyue();
        dialogUtil.loadDialog();
        map_device.put("token", TokenUtil.getToken(AddWifiHongWaiScucessActivity.this));
        map_device.put("name", name);
        map_device.put("deviceInfo", "1234556");

        MyOkHttp.postMapObject(ApiHelper.sraum_addWifiApple, map_device,
                new Mycallback(new AddTogglenInterfacer() {//刷新togglen获取新数据
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_addWifiApple(name);
                    }
                }, AddWifiHongWaiScucessActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        AddWifiHongWaiScucessActivity.this.finish();
                    }

                    @Override
                    public void onSuccess(User user) {
                        //成功添加小苹果红外模块
                        AddWifiHongWaiScucessActivity.this.finish();
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                    }
                });
    }

//    /**
//     * 添加时绑定订阅
//     */
//    private String add_bind_dingyue() {
//        if (gizWifiDevices != null) {
//            Log.e("DeviceListActivity", gizWifiDevices.size() + "");
//            GizWifiDevice mGizWifiDevice = null;
//
//            //红外转发器绑定设备
//            for (int i = 0; i < gizWifiDevices.size(); i++) {
//                mGizWifiDevice = gizWifiDevices.get(i);
//                // list_hand_scene
//                // 绑定选中项
//                if (!Utility.isEmpty(mGizWifiDevice) && !mGizWifiDevice.isBind()) {
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
//            mGizWifiDevice = gizWifiDevices.get(gizWifiDevices.size() - 1);
//            String str = ParceUtil.object2String(mGizWifiDevice);
////            String content =    new Gson().toJson(mGizWifiDevice);
//            String mapJson = JSON.toJSONString(mGizWifiDevice);
//
//            return str;
//        }
//        return "";
//    }


    private String getBindInfo(boolean isBind) {
        String strReturn = "";
        if (isBind == true)
            strReturn = "已绑定";
        else
            strReturn = "未绑定";
        return strReturn;
    }

    private String getLANInfo(boolean isLAN) {
        String strReturn = "";
        if (isLAN == true)
            strReturn = "局域网内设备";
        else
            strReturn = "远程设备";
        return strReturn;
    }


    private String getOnlineInfo(GizWifiDeviceNetStatus netStatus) {
        String strReturn = "";
        if (mDeviceManager.isOnline(netStatus) == true)//判断是否在线的方法
            strReturn = "在线";
        else
            strReturn = "离线";
        return strReturn;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 添加时绑定订阅
     */
    private void add_bind_dingyue() {

        GizWifiDevice mGizWifiDevice = null;

        //红外转发器绑定设备
        mGizWifiDevice = currGizWifiDevice;
        if (mGizWifiDevice == null) {
            return;
        }
        if (mDeviceManager == null) {
            return;
        }

        // list_hand_scene
        // 绑定选中项
        if (!Utility.isEmpty(mGizWifiDevice)) {
            mDeviceManager.bindRemoteDevice(mGizWifiDevice);
            final GizWifiDevice finalMGizWifiDevice = mGizWifiDevice;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDeviceManager.setSubscribe(finalMGizWifiDevice, true);
                }
            }, 1000);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddWifiHongWaiScucessActivity.this.finish();
                break;
            case R.id.next_step_txt:
                String type = (String) map_device.get("type");
                String mac = (String) map_device.get("mac");
                showPopWindow(type, mac);
                break;
            case R.id.btn_login_gateway:
                if (dev_name.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(AddWifiHongWaiScucessActivity.this, "" +
                            "设备名称为空");
                    return;
                }

                handler.sendEmptyMessage(1);
                break;
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow(String type_txt, String macDevice) {
        try {
            View view = LayoutInflater.from(AddWifiHongWaiScucessActivity.this).inflate(
                    R.layout.add_devsucesspopupwindow, null);

            TextView type = (TextView) view.findViewById(R.id.type);
            TextView mac = (TextView) view.findViewById(R.id.mac);
            type.setText(type_txt);
            mac.setText(macDevice);
            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            ColorDrawable cd = new ColorDrawable(0x00ffffff);// 背景颜色全透明
            popupWindow.setBackgroundDrawable(cd);
            int[] location = new int[2];
            next_step_txt.getLocationOnScreen(location);//获得textview的location位置信息，绝对位置
            popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.5f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            int xoff = dip2px(AddWifiHongWaiScucessActivity.this, 20);
            popupWindow.showAsDropDown(next_step_txt, 0, dip2px(AddWifiHongWaiScucessActivity.this, 10));
//            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow = null;// 当点击屏幕时，使popupWindow消失
                    backgroundAlpha(1.0f);// 当点击屏幕时，使半透明效果取消，1.0f为透明
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 得到面板信息
     */
    private void get_panel_detail() {
        panelNumber = getIntent().getStringExtra("panelid");
        //根据panelid去查找相关面板信心
        //根据panelid去遍历所有面板
        Bundle bundle = getIntent().getBundleExtra("bundle_panel");
        deviceList = (List<User.device>) bundle.getSerializable("deviceList");
        panelType = getIntent().getStringExtra("panelType");
        panelName = getIntent().getStringExtra("panelName");
        panelMAC = getIntent().getStringExtra("panelMAC");
        dev_name.setText(panelName);
    }

    /**
     * 更新面板名称
     *
     * @param panelName
     * @param panelNumber
     */
    private void sraum_update_panel_name(final String panelName, final String panelNumber) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(AddWifiHongWaiScucessActivity.this));
        map.put("boxNumber", TokenUtil.getBoxnumber(AddWifiHongWaiScucessActivity.this));
        map.put("panelNumber", panelNumber);
        map.put("panelName", panelName);
        MyOkHttp.postMapObject(ApiHelper.sraum_updatePanelName, map,
                new Mycallback(new AddTogglenInterfacer() {//刷新togglen获取新数据
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_update_panel_name(panelName, panelNumber);
                    }
                }, AddWifiHongWaiScucessActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        AddWifiHongWaiScucessActivity.this.finish();
                        ToastUtil.showToast(AddWifiHongWaiScucessActivity.this,"修改名字失败");
                    }

                    @Override
                    public void pullDataError() {
                        ToastUtil.showToast(AddWifiHongWaiScucessActivity.this,"修改名字失败");
                    }


                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        //
                        AddWifiHongWaiScucessActivity.this.finish();
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(AddWifiHongWaiScucessActivity.this, panelName + ":" + "面板编号不正确");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                        ToastUtil.showToast(AddWifiHongWaiScucessActivity.this, panelName + ":" + "面板名字已存在");
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(AddWifiHongWaiScucessActivity.this, "错误");
                    }
                });
    }

    /**
     * 保存面板
     */
    private void save_panel() {
        String panelName = dev_name.getText().toString().trim();
        sraum_update_panel_name(panelName, panelNumber);
    }

}
