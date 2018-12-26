package com.massky.sraum.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.api.YkanSDKManager;
import com.yaokan.sdk.ir.YkanIRInterfaceImpl;
import com.yaokan.sdk.model.KeyCode;
import com.yaokan.sdk.utils.Logger;
import com.yaokan.sdk.wifi.DeviceController;
import com.yaokan.sdk.wifi.listener.IDeviceControllerListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/6/6.
 */

public class WifiAirControlActivity extends BaseActivity implements IDeviceControllerListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.air_text)
    TextView air_text;
    @InjectView(R.id.text_clond)
    TextView text_clond;
    @InjectView(R.id.text_model)
    TextView text_model;
    @InjectView(R.id.wendu_txt)
    TextView wendu_txt;
    //    @InjectView(R.id.air_fuhao)
//    TextView air_fuhao;
    @InjectView(R.id.feng_liang)
    ImageView feng_liang;
    @InjectView(R.id.mode_img)
    ImageView mode_img;
    @InjectView(R.id.down_up_img)
    ImageView down_up_img;
    @InjectView(R.id.zouyou_img)
    ImageView zouyou_img;
    @InjectView(R.id.add_img)
    ImageView add_img;
    @InjectView(R.id.delete_img)
    ImageView delete_img;
    @InjectView(R.id.open_close_img)
    ImageView open_close_img;
    private DialogUtil dialogUtil;
    private String mac = "";
    private String rid = "";//遥控器编码
    @InjectView(R.id.icon_shangxia)
    ImageView icon_shangxia;
    @InjectView(R.id.icon_zuoyou)
    ImageView icon_zuoyou;


    private GizWifiDevice device;

    private HashMap<String, KeyCode> codeDatas = new HashMap<String, KeyCode>();

    private DeviceController driverControl = null;

    private Map mapdevice = new HashMap();
    private YkanIRInterfaceImpl ykanInterface;
    private String TAG = "robin debug";
    private String status = "";
    private String dimmer = "";
    private String mode = "";
    private String temperature = "";
    private String speed = "";
    private String UDScavenging = "";
    private String LRScavenging = "";
    private String number = "";//遥控器编号
    private String type = "";
    private String code;
    private String code_up = "";
    private int mode_index;
    private int speed_index;
    private int downup_index;
    private int zuoyou_index;
    private int temperatrure_int;
    private Map remoteControl_map_air = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.wifi_air_control_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiAirControlActivity.this.finish();
            }
        });
        dialogUtil = new DialogUtil(this);
        word_set();

        device = (GizWifiDevice) getIntent().getParcelableExtra("GizWifiDevice");
//        remoteControl = getIntent().getParcelableExtra("remoteControl");
//        HashMap<String, KeyCode> map_rcommand = (HashMap<String, KeyCode>) getIntent().getSerializableExtra("map_rcommand");
//        remoteControl.setRcCommand(map_rcommand);
        mapdevice = (Map) getIntent().getSerializableExtra("mapdevice");
//        mac = mapdevice.get("mac").toString();
//        rid = mapdevice.get("deviceId").toString();
        initData();
        init_common();
        onEvent();
    }

    private void initData() {
        number = mapdevice.get("number").toString();
        type = mapdevice.get("type").toString();
        remoteControl_map_air = (Map) getIntent().getSerializableExtra("map_rcommand");
    }

    /**
     * 加载的数据
     */
    private void loadData() {
        switch (status) {
            case "0":
                open_close_img.setImageResource(R.drawable.btn_open);
                break;
            case "1":
                open_close_img.setImageResource(R.drawable.btn_open_active);
                break;
        }
        air_text.setText(temperature);
        text_clond.setText(init_action_speed(speed));
        text_model.setText(init_action_mode(mode));

        switch (UDScavenging) {
            case "0":
                icon_shangxia.setImageResource(R.drawable.icon_zuoyousaofeng_w);
                break;
            case "1":
                icon_shangxia.setImageResource(R.drawable.icon_zuoyousaofeng);
                break;
        }

        switch (LRScavenging) {
            case "0":
                icon_zuoyou.setImageResource(R.drawable.icon_shangxiasaofeng_w);
                break;
            case "1":
                icon_zuoyou.setImageResource(R.drawable.icon_shangxiasaofeng);
                break;
        }
    }


    /**
     * 初始化空调动作-code-up-down
     *
     * @param speed
     */
    private String init_action_shangxia_code(String speed) {
        StringBuffer temp = new StringBuffer();
        switch (speed) {
            case "0":
                temp.append("u0");
                break;
            case "1":
                temp.append("u1");
                break;
        }
        return temp.toString();
    }


    /**
     * 初始化空调动作-zuoyou-down
     *
     * @param speed
     */
    private String init_action_zuoyou_code(String speed) {
        StringBuffer temp = new StringBuffer();
        switch (speed) {
            case "0":
                temp.append("l0");
                break;
            case "1":
                temp.append("l1");
                break;
        }
        return temp.toString();
    }

    /**
     * 初始化空调动作
     *
     * @param speed
     */
    private String init_action_speed(String speed) {
        StringBuffer temp = new StringBuffer();
        switch (speed) {
            case "1":
                temp.append("低风");
                break;
            case "2":
                temp.append("中风");
                break;
            case "3":
                temp.append("高风");
                break;
            case "6":
                temp.append("自动");
                break;
        }
        return temp.toString();
    }


    /**
     * 初始化空调动作code
     *
     * @param speed
     */
    private String init_action_speed_code(String speed) {
        StringBuffer temp = new StringBuffer();
        switch (speed) {
            case "1":
                temp.append("s1");
                break;
            case "2":
                temp.append("s2");
                break;
            case "3":
                temp.append("s3");
                break;
            case "6":
                temp.append("s0");
                break;
        }
        return temp.toString();
    }

    /**
     * 初始化空调动作
     *
     * @param model
     */
    private String init_action_mode(String model) {
        StringBuffer temp = new StringBuffer();

        switch (model) {
            case "1":
                temp.append("制冷");
                break;
            case "2":
                temp.append("制热");
                break;
            case "3":
                temp.append("除湿");
                break;
            case "4":
                temp.append("自动");
                break;
        }
        return temp.toString();
    }


    /**
     * 初始化空调动作code
     *
     * @param model
     */
    private String init_action_mode_code(String model) {
        StringBuffer temp = new StringBuffer();

        switch (model) {
            case "1":
                temp.append("r");//制冷
                break;
            case "2":
                temp.append("h");//制热
                break;
            case "3":
                temp.append("d");//抽湿
                break;
            case "4":
                temp.append("a");//自动
                break;
        }
        return temp.toString();
    }

    @Override
    protected void onEvent() {
        open_close_img.setOnClickListener(this);
        mode_img.setOnClickListener(this);
        mode_img.setOnClickListener(this);
        feng_liang.setOnClickListener(this);
        down_up_img.setOnClickListener(this);
        zouyou_img.setOnClickListener(this);
        add_img.setOnClickListener(this);
        delete_img.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    private void init_common() {
//        initDevice(device);
        driverControl = new DeviceController(getApplicationContext(), device, this);
        //获取设备硬件相关信息
        if (driverControl.getDevice() != null) {
            driverControl.getDevice().getHardwareInfo();
            //修改设备显示名称
            driverControl.getDevice().setCustomInfo("alias", "遥控中心产品");
        }
        new DownloadThread("sraum_getWifiAppleDeviceStatus").start();//下载该遥控器编码
    }

    /**
     * 字体设置
     */
    private void word_set() {
//得到AssetManager
        AssetManager mgr = getAssets();

//根据路径得到Typeface
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/BlackSimInFasion.ttf");

//设置字体
        //textView.setTypeface(tf);
        air_text.setTypeface(tf);
        text_model.setTypeface(tf);
        text_clond.setTypeface(tf);
        wendu_txt.setTypeface(tf);
//        air_fuhao.setTypeface(tf);

    }

    private void initDevice(GizWifiDevice currGizWifiDevice) {
        if (currGizWifiDevice != null) {
            String deviceId = currGizWifiDevice.getDid();
// 在下载数据之前需要设置设备ID，用哪个设备去下载
            YkanSDKManager.getInstance().setDeviceId(deviceId);
            if (currGizWifiDevice.isSubscribed() == false) {
                currGizWifiDevice.setSubscribe(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        //空调必须先打开，才能操作其他按键
//        if (airEvent.isOff() && !(v.getId() == R.id.open_close_img)) {
//            toast("请先打开空调");
//            return;
//        }

        if (status.equals("0") && !(v.getId() == R.id.open_close_img)) {
            toast("请先打开空调");
            return;
        }

        switch (v.getId()) {
            case R.id.open_close_img:
                status_onclick();
//                mKeyCode = airEvent.getNextValueByCatogery(AirConCatogery.Power);
                break;
            case R.id.mode_img:
                mode_onclick();
//                mKeyCode = airEvent.getNextValueByCatogery(AirConCatogery.Mode);
                break;
            case R.id.feng_liang:
//                mKeyCode = airEvent.getNextValueByCatogery(AirConCatogery.Speed);
                speed_onclick();
                break;
            case R.id.down_up_img:
//                mKeyCode = airEvent.getNextValueByCatogery(AirConCatogery.WindUp);
                down_up_onclick();
                break;
            case R.id.zouyou_img:
//                mKeyCode = airEvent.getNextValueByCatogery(AirConCatogery.WindLeft);
                zuoyou_onclick();
                break;
            case R.id.add_img:
//                mKeyCode = airEvent.getNextValueByCatogery(AirConCatogery.Temp);
                temp_add_onclick();
                break;
            case R.id.delete_img:
                delete_onclick();
//                mKeyCode = airEvent.getForwardValueByCatogery(AirConCatogery.Temp);
                break;
            default:
                break;
        }
//        if (!Utility.isEmpty(mKeyCode)) {
//            code = mKeyCode.getSrcCode();
//            code_up = code.substring(2);
//            driverControl.sendCMD(code);

        String spe = init_action_speed_code(speed);
        String mod = init_action_mode_code(mode);
        String lrs = init_action_zuoyou_code(LRScavenging);
        String uds = init_action_shangxia_code(UDScavenging);

        String code_mode = null;
        switch (mod) {
            case "d":
            case "a":
                code_mode = mod + "_" + "s1" + "__"
                        + uds + "_" + lrs + "_" + "p0";
                speed = "1";//低风
                break;
            default:
                code_mode = mod + "_" + spe + "_" + temperature + "_"
                        + uds + "_" + lrs + "_" + "p0";
                break;
        }

        //
        switch (status) {
            case "0":
                sendCMD("off");
                break;
            case "1":
                sendCMD(code_mode);
                break;
        }
//        }
    }

    private void status_onclick() {
        if (status.equals("1")) {
            status = "0";
        } else {
            status = "1";
        }
    }

    private void delete_onclick() {
        temperatrure_int--;
        if (temperatrure_int < 16) {
            ToastUtil.showToast(WifiAirControlActivity.this, "最低温度");
            temperatrure_int = 16;
        }
//        temperatrure_int = Integer.parseInt(temperature);
        temperature = String.valueOf(temperatrure_int);
    }

    private void temp_add_onclick() {
        temperatrure_int++;
        if (temperatrure_int > 31) {
            ToastUtil.showToast(WifiAirControlActivity.this, "最高温度");
            temperatrure_int = 31;
        }
//        temperatrure_int = Integer.parseInt(temperature);
        temperature = String.valueOf(temperatrure_int);
    }

    private void zuoyou_onclick() {
        zuoyou_index++;
        if (zuoyou_index > 2) {
            zuoyou_index = 1;
        }
        switch (zuoyou_index) {
            case 1:
                LRScavenging = "0";
                break;
            case 2:
                LRScavenging = "1";
                break;

        }
    }

    private void zuoyou_load() {

        switch (LRScavenging) {
            case "0":
                zuoyou_index = 1;
            case "1":
                zuoyou_index = 2;
                break;

        }
    }

    private void down_up_onclick() {
        downup_index++;
        if (downup_index > 2) {
            downup_index = 1;
        }
        switch (downup_index) {
            case 1:
                UDScavenging = "0";
                break;
            case 2:
                UDScavenging = "1";
                break;

        }
    }

    private void down_up_load() {

        switch (UDScavenging) {
            case "0":
                downup_index = 1;
                break;
            case "1":
                downup_index = 2;
                break;

        }
    }

    private void speed_onclick() {
        speed_index++;
        if (speed_index > 4) {
            speed_index = 1;
        }
        switch (speed_index) {
            case 1:
                speed = "6";
                break;
            case 2:
                speed = "1";
                break;
            case 3:
                speed = "2";
                break;
            case 4:
                speed = "3";
                break;
        }
    }

    private void speed_load() {

        switch (speed) {
            case "6":
                speed_index = 1;
                break;
            case "1":
                speed_index = 2;
                break;
            case "2":
                speed_index = 3;
                break;
            case "3":
                speed_index = 4;

                break;
        }
    }


    private void mode_onclick() {
        mode_index++;
        if (mode_index > 4) {
            mode_index = 1;
        }
        switch (mode_index) {
            case 1:
                mode = "1";
                break;
            case 2:
                mode = "2";
                break;
            case 3:
                mode = "3";
                break;
            case 4:
                mode = "4";
                break;
        }
    }


    private void mode_load() {
        switch (mode) {
            case "1":
                mode_index = 1;
                break;
            case "2":
                mode_index = 2;
                break;
            case "3":
                mode_index = 3;
                break;
            case "4":
                mode_index = 4;
                break;
        }
    }

    private void voice_rise() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    protected void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void onRefreshUI() {
//        tv_show.setText(content);
        new DownloadThread("upload").start();
        loadData_fromsdk();
    }

    private void loadData_fromsdk() {
        switch (status) {
            case "0":
                open_close_img.setImageResource(R.drawable.btn_open);
                break;
            case "1":
                open_close_img.setImageResource(R.drawable.btn_open_active);
                break;
        }
        air_text.setText(temperature);
        text_clond.setText(init_action_speed(speed));
        text_model.setText(init_action_mode(mode));

        switch (UDScavenging) {
            case "0":
                icon_shangxia.setImageResource(R.drawable.icon_zuoyousaofeng_w);
                break;
            case "1":
                icon_shangxia.setImageResource(R.drawable.icon_zuoyousaofeng);
                break;
        }

        switch (LRScavenging) {
            case "0":
                icon_zuoyou.setImageResource(R.drawable.icon_shangxiasaofeng_w);
                break;
            case "1":
                icon_zuoyou.setImageResource(R.drawable.icon_shangxiasaofeng);
                break;
        }
    }


    @Override
    public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, String> hardwareInfo) {
        Logger.d(TAG, "获取设备信息 :");
        if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
            Logger.d(TAG, "获取设备信息 : hardwareInfo :" + hardwareInfo);
        } else {

        }
    }

    @Override
    public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
        Logger.d(TAG, "自定义设备信息回调");
        if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
            Logger.d(TAG, "自定义设备信息成功");
        }
    }

    @Override
    public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
        switch (device.getNetStatus()) {
            case GizDeviceOffline:
                Logger.d(TAG, "设备下线");
                break;
            case GizDeviceOnline:
                Logger.d(TAG, "设备上线");
                break;
            default:
                break;
        }
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
                case "upload":
                    upload(message);
                    break;
                case "sraum_getWifiAppleDeviceStatus":
                    sraum_getWifiAppleDeviceStatus(message);
                    break;
                default:
                    break;
            }
        }
    }

    //h_s0_26_u0_l0_p0 - >电源开【遥控器默认状态制冷_风量自动_26度_上下扫风开_左右扫风开_睡眠关】
    /* on*/
    /*电源关*/
    /* off*/

    /**
     * 获取 wifi 红外转发遥控器状态
     */
    private void sraum_getWifiAppleDeviceStatus(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogUtil.loadDialog();
            }
        });
        final Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(WifiAirControlActivity.this));
        map.put("number", number);
        MyOkHttp.postMapObject(ApiHelper.sraum_getWifiAppleDeviceStatus, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, WifiAirControlActivity.this, dialogUtil) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                    }

                    @Override
                    public void onSuccess(final User user) {
                        super.onSuccess(user);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                message.what = 3;
                                type = user.type;
                                number = user.number;
                                status = user.status;
                                dimmer = user.dimmer;
                                mode = user.mode;
                                temperature = user.temperature;
                                speed = user.speed;
                                UDScavenging = user.UDScavenging;
                                LRScavenging = user.LRScavenging;
                                mHandler.sendMessage(message);
                                mode_load();
                                speed_load();
                                down_up_load();
                                zuoyou_load();
                                temperatrure_int = Integer.parseInt(temperature);
                                String spe = init_action_speed_code(speed);
                                String mod = init_action_mode_code(mode);
                                String lrs = init_action_zuoyou_code(LRScavenging);
                                String uds = init_action_shangxia_code(UDScavenging);

                                //模式为除湿自动时是没有温度的。
                                //d_s3_25_u0_l1_p0,//除湿
                                //        a_s3_25_u0_l1_p0,//自动
                                String code_mode = null;
                                switch (mod) {
                                    case "d":
                                    case "a":
                                        code_mode = mod + "_" + "s1" + "__"
                                                + uds + "_" + lrs + "_" + "p0";
                                        speed = "1";//低风
                                        break;
                                    default:
                                        code_mode = mod + "_" + spe + "_" + temperature + "_"
                                                + uds + "_" + lrs + "_" + "p0";
                                        break;
                                }

                                //
                                switch (status) {
                                    case "0":
                                        sendCMD("off");
                                        break;
                                    case "1":
                                        sendCMD(code_mode);
                                        break;
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
     * 控制
     */
    private void sendCMD(String mode) {//"d_s1__u0_l1_p0" ->模式为除湿或者自动时风量为s1低风
        if (driverControl == null) {
            return;
        }

        if (remoteControl_map_air == null) {
            return;
        }

        Set<String> set = remoteControl_map_air.keySet();
        String key = null;
        for (String s : set) {
            if (s.contains(mode)) {
                key = s;
            }
        }
        if (remoteControl_map_air.get(key) == null) {
            return;
        }


        code = remoteControl_map_air.get(key).toString();
        code_up = code.substring(2);
        driverControl.sendCMD(code);
        onRefreshUI();
        voice_rise();
    }

    /**
     * 上传 wifi 红外转发遥控器状态
     *
     * @param message
     */
    private void upload(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogUtil.loadDialog();
            }
        });
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(WifiAirControlActivity.this));
        map.put("type", type);
        map.put("number", number);
        map.put("status", status);
        map.put("dimmer", dimmer);
        map.put("mode", mode);
        map.put("temperature", temperature);
        map.put("speed", speed);
        map.put("UDScavenging", UDScavenging);
        map.put("LRScavenging", LRScavenging);
        map.put("code", code_up);
        MyOkHttp.postMapObject(ApiHelper.sraum_setWifiAppleDeviceStatus, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, WifiAirControlActivity.this, dialogUtil) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                message.what = 2;
                                mHandler.sendMessage(message);
                            }
                        });
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                case 2:

                    break;
                case 3:
                    loadData();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

