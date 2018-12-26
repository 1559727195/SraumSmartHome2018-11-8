package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
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
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.wifi.DeviceManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2018/1/8.
 */

public class AddWifiCameraScucessActivity extends BaseActivity {
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
    private Map wificamera = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.add_wifi_camera_scucess;
    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
        dialogUtil = new DialogUtil(this);
        next_step_txt.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        get_panel_detail();
        wificamera = (Map) getIntent().getSerializableExtra("wificamera");
        //intent.putExtra("wificamera",(Serializable) map_result);
        next_step_txt.setVisibility(View.GONE);
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }


    /**
     * 添加 wifi 红外转发设备
     */
    private void sraum_addWifiCamera(final String name) {
    String type = "";
//        String deviceInfo  = add_bind_dingyue();
        Map map = new HashMap();
        String areaNumber = (String) SharedPreferencesUtil.getData(AddWifiCameraScucessActivity.this,
                "areaNumber","");
        dialogUtil.loadDialog();
        map.put("token", TokenUtil.getToken(AddWifiCameraScucessActivity.this));
        switch (wificamera.get("type").toString()) {
            case "101":
                map.put("type", "AA03");
                type = "AA03";
                break;
            case "103":
                map.put("type", "AA04");
                type = "AA04";
                break;
        }
        map.put("areaNumber", areaNumber);
        map.put("name", name);
        map.put("mac", wificamera.get("strMac"));
        map.put("controllerId", wificamera.get("strDeviceID"));
        map.put("user", "admin");
        map.put("password", "888888");
        map.put("wifi", wificamera.get("wifi"));

//        map.put("strMac", strMac);
//        map.put("strDeviceID", strDeviceID);
//        map.put("strName", strName);
//        map.put("wifi",wifi_name);

        final String finalType = type;
        MyOkHttp.postMapObject(ApiHelper.sraum_addWifiCamera, map,
                new Mycallback(new AddTogglenInterfacer() {//刷新togglen获取新数据
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_addWifiCamera(name);
                    }
                }, AddWifiCameraScucessActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        AddWifiCameraScucessActivity.this.finish();
                    }

                    @Override
                    public void onSuccess(User user) {
                        //成功添加小苹果红外模块
//                        AddWifiCameraScucessActivity.this.finish();
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        Map map = new HashMap();
                        map.put("deviceId",wificamera.get("strDeviceID"));
                        map.put("deviceType", finalType);
                        map.put("type","2");
                        Intent intent = new Intent(AddWifiCameraScucessActivity.this, SelectRoomActivity.class);
                        intent.putExtra("map_deivce", (Serializable) map);
                        startActivity(intent);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        ToastUtil.showToast(AddWifiCameraScucessActivity.this,"areaNumber 不正\n" +
                                "确");
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(AddWifiCameraScucessActivity.this,"名字已存在");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddWifiCameraScucessActivity.this.finish();
                break;
            case R.id.next_step_txt:
                String type = (String) map_device.get("type");
                String mac = (String) map_device.get("mac");
                showPopWindow(type, mac);
                break;
            case R.id.btn_login_gateway:
                if (dev_name.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(AddWifiCameraScucessActivity.this, "" +
                            "设备名称为空");
                    return;
                }

                sraum_addWifiCamera(dev_name.getText().toString().trim());
//                handler.sendEmptyMessage(1);
                break;
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow(String type_txt, String macDevice) {
        try {
            View view = LayoutInflater.from(AddWifiCameraScucessActivity.this).inflate(
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
            int xoff = dip2px(AddWifiCameraScucessActivity.this, 20);
            popupWindow.showAsDropDown(next_step_txt, 0, dip2px(AddWifiCameraScucessActivity.this, 10));
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

}
