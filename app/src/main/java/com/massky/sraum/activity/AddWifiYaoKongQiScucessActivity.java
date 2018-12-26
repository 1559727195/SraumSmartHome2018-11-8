package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.yaokan.sdk.ir.YKanHttpListener;
import com.yaokan.sdk.ir.YkanIRInterfaceImpl;
import com.yaokan.sdk.model.BaseResult;
import com.yaokan.sdk.model.KeyCode;
import com.yaokan.sdk.model.RemoteControl;
import com.yaokan.sdk.model.YKError;
import com.yaokan.sdk.wifi.DeviceController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.Util.DisplayUtil.dip2px;

/**
 * Created by zhu on 2018/1/8.
 */

public class AddWifiYaoKongQiScucessActivity extends BaseActivity {
    @InjectView(R.id.next_step_txt)
    ImageView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private PopupWindow popupWindow;

    private DialogUtil dialogUtil;

    @InjectView(R.id.dev_name)
    ClearEditText dev_name;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    private Map map = new HashMap();
    private YkanIRInterfaceImpl ykanInterface;
    private GizWifiDevice gizWifiDevice;
    private DeviceController driverControl;
    private String TAG = AddWifiYaoKongQiScucessActivity.class.getSimpleName();
    private String rid = "";
    private String control_number = "";
    private RemoteControl remoteControl;
    private String on;
    private String off;
    private String type = "";
    private List<Map> list_remotecontrol_air = new ArrayList<>();
    private Map remoteControl_map_air = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.add_wifi_deivice_scucess;
    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
        dialogUtil = new DialogUtil(this);
        next_step_txt.setOnClickListener(this);
        next_step_txt.setVisibility(View.GONE);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        get_panel_detail();

        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getApplicationContext());
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getApplicationContext());
        initData();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }


    private void initData() {
        map = (Map) getIntent().getSerializableExtra("sraum_addWifiAppleDevice_map");
        rid = (String) map.get("deviceId");
        control_number = (String) map.get("controllerNumber");
        type = (String) map.get("type");
//        new DownloadThread("getDetailByRCID").start();//下载该遥控器编码
    }


//
//        adapter.notifyDataSetChanged();


    /**
     * 添加 wifi 红外转发遥控器
     */
    private void sraum_addWifiApple(final Map map, final String name) {
        dialogUtil.loadDialog();
        map.put("token", TokenUtil.getToken(AddWifiYaoKongQiScucessActivity.this));
        String areaNumber = (String) SharedPreferencesUtil.getData(AddWifiYaoKongQiScucessActivity.this,"areaNumber","");
        map.put("name", name);
        map.put("areaNumber", areaNumber);
        MyOkHttp.postMapObject(ApiHelper.sraum_addWifiAppleDevice, map,
                new Mycallback(new AddTogglenInterfacer() {//刷新togglen获取新数据
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_addWifiApple(map, name);
                    }
                }, AddWifiYaoKongQiScucessActivity.this, dialogUtil) {
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        AddWifiHongWaiScucessActivity.this.finish();
                        ToastUtil.showToast(AddWifiYaoKongQiScucessActivity.this, "修改名字失败");
                    }

                    @Override
                    public void pullDataError() {
                        ToastUtil.showToast(AddWifiYaoKongQiScucessActivity.this, "修改名字失败");
                    }


                    @Override
                    public void onSuccess(User user) {
                        //成功添加小苹果红外模块
                        AddWifiYaoKongQiScucessActivity.this.finish();
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
//                        Map map = new HashMap();
//                        map.put("deviceId",rid);
//                        map.put("deviceType", type);
//                        map.put("type","2");
//                        Intent intent = new Intent(AddWifiYaoKongQiScucessActivity.this, SelectRoomActivity.class);
//                        intent.putExtra("map_deivce", (Serializable) map);
//                        startActivity(intent);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(AddWifiYaoKongQiScucessActivity.this, "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(AddWifiYaoKongQiScucessActivity.this, "name 已存在");
                    }

                    @Override
                    public void fourCode() {
                        ToastUtil.showToast(AddWifiYaoKongQiScucessActivity.this, "修改名字失败");
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddWifiYaoKongQiScucessActivity.this.finish();
                break;
            case R.id.next_step_txt:
                showPopWindow();
                break;
            case R.id.btn_login_gateway:
                if (dev_name.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(AddWifiYaoKongQiScucessActivity.this, "" +
                            "设备名称为空");
                    return;
                }
                new DownloadThread("getDetailByRCID").start();//下载该遥控器编码
                break;
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(AddWifiYaoKongQiScucessActivity.this).inflate(
                    R.layout.add_devsucesspopupwindow, null);
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
            int xoff = dip2px(AddWifiYaoKongQiScucessActivity.this, 20);
            popupWindow.showAsDropDown(next_step_txt, 0, dip2px(AddWifiYaoKongQiScucessActivity.this, 10));
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


    class DownloadThread extends Thread {
        private String viewId;
        String result = "";

        public DownloadThread(String viewId) {
            this.viewId = viewId;
        }

        @Override
        public void run() {

            final Message message = mHandler.obtainMessage();
            switch (viewId) {
                case "getDetailByRCID":
                    if (!control_number.equals("")) {
                        if (ykanInterface == null) return;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogUtil.loadDialog();
                            }
                        });

                        ykanInterface
                                .getRemoteDetails(control_number, rid, new YKanHttpListener() {
                                    @Override
                                    public void onSuccess(BaseResult baseResult) {
                                        if (baseResult != null) {
                                            remoteControl = (RemoteControl) baseResult;
                                            result = remoteControl.toString();
                                            //在这里保持遥控器红外码列表
                                            HashMap<String, KeyCode> map = remoteControl.getRcCommand();
                                            Map map_send = new HashMap();
                                            map_send.put("mac", control_number);
                                            map_send.put("rid", rid);
                                            Set<String> set = map.keySet();
                                            for (String s : set) {
                                                if (map.get(s) == null) {
                                                    continue;
                                                }

                                                if (map.get(s).getSrcCode() == null) {
                                                    continue;
                                                }
                                                map_send.put(s, map.get(s).getSrcCode());
                                            }
                                            list_remotecontrol_air = SharedPreferencesUtil.getInfo_List(AddWifiYaoKongQiScucessActivity.this,
                                                    "remoteairlist");
                                            list_remotecontrol_air.add(map_send);
                                            remoteControl_map_air = map_send;
                                            SharedPreferencesUtil.saveInfo_List(AddWifiYaoKongQiScucessActivity.this,
                                                    "remoteairlist", list_remotecontrol_air);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialogUtil.removeDialog();
                                                    message.what = 1;
                                                    message.obj = result;
                                                    mHandler.sendMessage(message);
                                                }
                                            });
                                            //下载好遥控码后
                                        }
                                    }

                                    @Override
                                    public void onFail(YKError ykError) {
                                        Log.e(TAG, "ykError:" + ykError.toString());
                                        dialogUtil.removeDialog();
                                    }
                                });
                    } else {
                        result = "请调用匹配数据接口";
                        Log.e(TAG, " getDetailByRCID 没有遥控器设备对象列表");
                    }
                    Log.d(TAG, " getDetailByRCID result:" + result);
                    break;
                case "upload":

                    break;
                case "sraum_getWifiAppleDeviceStatus":

                    break;
                default:
                    break;
            }
        }
    }

//    "on" -> "KeyCode [kn=, srcCode=01HIBZYWioVUhP2H+Ck27wcgqFhyjh/bbVZAmgJwT4FCBUzhxeNeO86/4tIhhEcsNS2Nsl9YiN3QEYQ2T7eHL59g==, shortCode=, order=0]"
//
//            "off" -> "KeyCode [kn=, srcCode=01HIBZYWioVUhP2H+Ck27wckNrHb3ZlbrD+ZOnkapr90Ro5O8TXU9LubuC7GadOt74YDtHs7makaOi4Wpu0D6QyQ==, shortCode=, order=0]"

    private String power = "";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            List<Map> list_code = new ArrayList<>();
            Map map_code = new HashMap();
            switch (msg.what) {
                case 1:
                    switch (type) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                        case "202":
                            power = get_key_code("power");
                            power = power.substring(2);
                            map_code.put("code", power);
                            map_code.put("name", "power");
                            list_code.add(map_code);
                            map.put("codeList", list_code);
                            break;
                        case "206":
                            //h_s0_26_u0_l0_p0 - >电源开【遥控器默认状态制冷_风量自动_26度_上下扫风开_左右扫风开_睡眠关】
                            on = get_key_code("h_s0_26_u0_l0_p0");//h_s0_26_u0_l0_p0
                            if (on == null) {
                                on = get_key_code("on");
                            }
                            on = on.substring(2);//去掉01开头
                            off = get_key_code("off");
                            off = off.substring(2);
                            map_code.put("code", on);
                            map_code.put("name", "on");
                            list_code.add(map_code);
                            map_code = new HashMap();
                            map_code.put("code", off);
                            map_code.put("name", "off");
                            list_code.add(map_code);
                            map.put("codeList", list_code);
                            break;
                    }
                    break;
                default:
                    break;
            } //
            sraum_addWifiApple(map, dev_name.getText().toString().trim());
        }
    };


    /***
     *       sCode[i] = entry.getValue().getSrcCode();
     *       获取key-code
     */
    private String get_key_code(String mode) {

        HashMap<String, KeyCode> map = remoteControl.getRcCommand();//模式为除湿自动时是没有温度的
        Set<String> set = map.keySet();
        String key = null;
        for (String s : set) {
            if (s.contains(mode)) {
                key = s;
            }
        }
        if (map.get(key) == null) {
            return "";
        }

        if (map.get(key).getSrcCode() == null) {
            return "";
        }
        return map.get(key).getSrcCode();
    }
}
