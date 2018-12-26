package com.massky.sraum.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.yaokan.sdk.model.AirEvent;
import com.yaokan.sdk.model.AirV1Command;
import com.yaokan.sdk.model.AirV3Command;
import com.yaokan.sdk.model.KeyCode;
import com.yaokan.sdk.utils.Logger;
import com.yaokan.sdk.utils.Utility;
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

public class TVShowActivity extends BaseActivity implements IDeviceControllerListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.tv_status_linear)
    LinearLayout tv_status_linear;
    @InjectView(R.id.input_select_linear)
    LinearLayout input_select_linear;
    @InjectView(R.id.home_linear)
    LinearLayout home_linear;

    @InjectView(R.id.add_voice)
    ImageView add_voice;
    @InjectView(R.id.delete_voice)
    ImageView delete_voice;
    @InjectView(R.id.add_chanel)
    ImageView add_chanel;
    @InjectView(R.id.delete_chanel)
    ImageView delete_chanel;

    @InjectView(R.id.top_img)
    ImageView top_img;
    @InjectView(R.id.down_img)
    ImageView down_img;
    @InjectView(R.id.left_img)
    ImageView left_img;
    @InjectView(R.id.right_img)
    ImageView right_img;
    private GizWifiDevice device;
    private HashMap<String, KeyCode> codeDatas = new HashMap<String, KeyCode>();
    private DeviceController driverControl = null;
    private static final int V3 = 3;
    private int airVerSion = V3;
    private AirEvent airEvent = null;
    private Map mapdevice = new HashMap();
    private String mac = "";
    private String rid = "";
    private String code = "";
    private DialogUtil dialogUtil;
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
    @InjectView(R.id.tv_img)
    ImageView tv_img;
    @InjectView(R.id.ok_img)
    ImageView ok_img;
    @InjectView(R.id.caidan_img)
    ImageView caidan_img;
    @InjectView(R.id.fanhui_img)
    ImageView fanhui_img;
    @InjectView(R.id.img_jingyin)
    ImageView img_jingyin;
    @InjectView(R.id.quit_rel)
    RelativeLayout quit_rel;
    private String code1;
    private Map remoteControl_map_air = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.tv_show_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TVShowActivity.this.finish();
            }
        });
        dialogUtil = new DialogUtil(this);
        device = (GizWifiDevice) getIntent().getParcelableExtra("GizWifiDevice");
        mapdevice = (Map) getIntent().getSerializableExtra("mapdevice");

        initData();
        init_common();
        init_event();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void init_event() {
        tv_status_linear.setOnClickListener(this);
        input_select_linear.setOnClickListener(this);
        home_linear.setOnClickListener(this);
        add_voice.setOnClickListener(this);
        delete_voice.setOnClickListener(this);
        add_chanel.setOnClickListener(this);
        delete_chanel.setOnClickListener(this);
        top_img.setOnClickListener(this);
        down_img.setOnClickListener(this);
        left_img.setOnClickListener(this);
        right_img.setOnClickListener(this);

        ok_img.setOnClickListener(this);
        caidan_img.setOnClickListener(this);
        fanhui_img.setOnClickListener(this);
        img_jingyin.setOnClickListener(this);
        quit_rel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (status.equals("0") && v.getId() != R.id.tv_status_linear) {
            ToastUtil.showToast(TVShowActivity.this, "请先打开电视机");
            return;
        }
        switch (v.getId()) {
            case R.id.tv_status_linear:
                sendCMD("power");
                voice_rise();
                break;
            case R.id.input_select_linear:
                sendCMD("signal");
                voice_rise();
                break;
            case R.id.home_linear:
                sendCMD("boot");
                voice_rise();
                break;
            case R.id.add_voice:
                sendCMD("vol+");
                voice_rise();
                break;
            case R.id.delete_voice:
                sendCMD("vol-");
                voice_rise();
                break;
            case R.id.add_chanel:
                sendCMD("ch+");
                voice_rise();
                break;
            case R.id.delete_chanel:
                sendCMD("ch-");
                voice_rise();
                break;

            case R.id.top_img:
                voice_rise();
                sendCMD("up");
                break;
            case R.id.down_img:
                sendCMD("down");
                voice_rise();
                break;
            case R.id.left_img:
                sendCMD("left");
                voice_rise();
                break;
            case R.id.right_img:
                sendCMD("right");
                voice_rise();
                break;
            case R.id.ok_img:
                sendCMD("ok");
                voice_rise();
                break;
            case R.id.caidan_img:

                sendCMD("menu");
                voice_rise();
                break;
            case R.id.fanhui_img:
                sendCMD("back");
                voice_rise();
                break;
            case R.id.img_jingyin:
                sendCMD("mute");
                voice_rise();
                break;
            case R.id.quit_rel:
                sendCMD("exit");
                voice_rise();
                break;
        }
    }

    private void voice_rise() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
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
                tv_img.setImageResource(R.drawable.btn_open);
                break;
            case "1":
                tv_img.setImageResource(R.drawable.btn_open_active);
                break;
        }
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

    private void init_common() {
        initDevice(device);
        driverControl = new DeviceController(getApplicationContext(), device, TVShowActivity.this);
//        //获取设备硬件相关信息
        if (driverControl.getDevice() != null) {
            driverControl.getDevice().getHardwareInfo();
            //修改设备显示名称
            driverControl.getDevice().setCustomInfo("alias", "遥控中心产品");
        }
//        new DownloadThread("getDetailByRCID").start();//下载该遥控器编码
        new DownloadThread("sraum_getWifiAppleDeviceStatus").start();//下载该遥控器编码
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

    protected void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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
        map.put("token", TokenUtil.getToken(TVShowActivity.this));
        map.put("number", number);
        MyOkHttp.postMapObject(ApiHelper.sraum_getWifiAppleDeviceStatus, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, TVShowActivity.this, dialogUtil) {


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
        map.put("token", TokenUtil.getToken(TVShowActivity.this));
        map.put("type", type);
        map.put("number", number);
        map.put("status", status);
        map.put("dimmer", dimmer);
        map.put("mode", mode);
        map.put("temperature", temperature);
        map.put("speed", speed);
        map.put("UDScavenging", UDScavenging);
        map.put("LRScavenging", LRScavenging);
        map.put("code", code.substring(2));
        MyOkHttp.postMapObject(ApiHelper.sraum_setWifiAppleDeviceStatus, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, TVShowActivity.this, dialogUtil) {


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

    private AirEvent getAirEvent(HashMap<String, KeyCode> codeDatas) {
        AirEvent airEvent = null;
        if (!Utility.isEmpty(codeDatas)) {
            if (airVerSion == V3) {
                airEvent = new AirV3Command(codeDatas);
            } else {
                airEvent = new AirV1Command(codeDatas);
            }
        }
        return airEvent;
    }


    /**
     * 控制
     *
     * @param mode
     */
    private void sendCMD(String mode) {

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
        switch (mode) {
            case "power":
                if (status.equals("1")) {
                    status = "0";
                } else {
                    status = "1";
                }
                loadData();
                break;
            default:

                break;
        }

        if (code != null) {
            driverControl.sendCMD(code);
            new DownloadThread("upload").start();//下载该遥控器编码
        }
    }

}

