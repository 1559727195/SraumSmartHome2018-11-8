package com.massky.sraum.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.massky.sraum.R;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.activity.AddWifiYaoKongQiScucessActivity;
import com.massky.sraum.activity.RemoteControlMatchingActivity;
import com.massky.sraum.base.Basecfragment;
import com.yaokan.sdk.api.YkanSDKManager;
import com.yaokan.sdk.ir.YKanHttpListener;
import com.yaokan.sdk.ir.YkanIRInterface;
import com.yaokan.sdk.ir.YkanIRInterfaceImpl;
import com.yaokan.sdk.model.BaseResult;
import com.yaokan.sdk.model.Brand;
import com.yaokan.sdk.model.BrandResult;
import com.yaokan.sdk.model.DeviceType;
import com.yaokan.sdk.model.DeviceTypeResult;
import com.yaokan.sdk.model.KeyCode;
import com.yaokan.sdk.model.MatchRemoteControl;
import com.yaokan.sdk.model.MatchRemoteControlResult;
import com.yaokan.sdk.model.RemoteControl;
import com.yaokan.sdk.model.YKError;
import com.yaokan.sdk.wifi.DeviceController;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.listener.IDeviceControllerListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import butterknife.InjectView;

import static com.massky.sraum.activity.RemoteControlMatchingActivity.ACTION_INTENT_RECEIVER_WIFI;


/**
 * Created by masskywcy on 2016-09-05.
 */
/*用于第一个fragment主界面*/
public class EachMatchFragment extends Basecfragment implements View.OnTouchListener {
    private static final int DEVICE_MESSAGE_BACK = 100;
    @InjectView(R.id.pinpai_pic)
    ImageView pinpai_pic;
    @InjectView(R.id.peipai_name)
    TextView peipai_name;
    //yaokong_name_promat
    @InjectView(R.id.yaokong_name_promat)
    TextView yaokong_name_promat;
    @InjectView(R.id.yaokongqi_linear)
    LinearLayout yaokongqi_linear;
    //yaokong_name_promat_one,yaokong_name_promat_two,yaokong_name_promat_three
    @InjectView(R.id.yaokong_name_promat_one)
    TextView yaokong_name_promat_one;
    @InjectView(R.id.yaokong_name_promat_two)
    TextView yaokong_name_promat_two;
    @InjectView(R.id.yaokong_name_promat_three)
    TextView yaokong_name_promat_three;
    //add_img,delete_img
    @InjectView(R.id.add_img)
    ImageView add_img;
    @InjectView(R.id.delete_img)
    ImageView delete_img;
    //next_step_id
    @InjectView(R.id.next_step_id)
    Button next_step_id;
    @InjectView(R.id.num_txt)
    TextView num_txt;

    @InjectView(R.id.select_name)
    TextView select_name;

    private YkanIRInterface ykanInterface;

    private String TAG = RemoteControlMatchingActivity.class.getSimpleName();

    private GizWifiDevice currGizWifiDevice;

    private MatchRemoteControlResult controlResult = null;// 匹配列表

    private RemoteControl remoteControl = null; // 遥控器对象

    private MatchRemoteControl currRemoteControl = null; // 当前匹配的遥控器对象
    private List<MatchRemoteControl> remoteControls = new ArrayList<MatchRemoteControl>();// 遥控器列表

    private String deviceId;
    private List<Brand> brands = new ArrayList<Brand>(); // 品牌
    private DeviceType currDeviceType = null; // 当前设备类型
    private List<DeviceType> deviceType = new ArrayList<DeviceType>();// 设备类型
    private Brand currBrand = null; // 当前品牌
    private DeviceController driverControl = null;

    private Map curr_map = new HashMap();
    private int bid;
    private String brand_name = "";
    private int[] yaokong_wifi_before = { //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            R.string.dianshiji_yaokong_before, R.string.kongtiao_yaokong_before
    };
    private int[] yaokong_wifi_one = { //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            R.string.dianshiji_yaokong, R.string.kongtiao_yaokong
    };

    private int[] yaokong_wifi_two = { //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            R.string.dianshiji_yaokong_one, R.string.kongtiao_yaokong_one
    };
    private int[] yaokong_wifi_three = { //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            R.string.dianshiji_yaokong_two, R.string.kongtiao_yaokong_two
    };
    private List<String> nameRemote = new ArrayList<String>();//遥控器列表名称

    private int[] icon_brands = {
            R.drawable.icon_type_dianshiji_90,
            R.drawable.icon_type_kongtiaomb_90
    };
    SimpleDateFormat simpleFormatter;
    private String tid = "";
    private DialogUtil dialogUtil;
    private boolean add_long_click;
    private boolean delete_long_click;
    private String control_number;//红外模块Id
    private String rid;
    private String code;
    private MessageReceiver mMessageReceiver;
    List<MatchRemoteControl> list = new ArrayList<>();


    @Override
    protected int viewId() {
        return R.layout.each_match_fragment;
    }

    @Override
    protected void onView() {
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getActivity().getApplicationContext());
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getActivity().getApplicationContext());
        simpleFormatter = new SimpleDateFormat("HH:mm:ss");
        initData();
        initDevice();
        List<GizWifiDevice> gizWifiDevices = DeviceManager
                .instanceDeviceManager(getActivity().getApplicationContext()).getCanUseGizWifiDevice();
        if (gizWifiDevices != null) {
            Log.e("YKCodeAPIActivity", gizWifiDevices.size() + "");
        }
        dialogUtil = new DialogUtil(getActivity());
        new DownloadThread("getMatchedDataByBrand").start();//下载遥控器列表
        onEvent();
    }

    /**
     * 动态注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_RECEIVER_WIFI);
        getActivity().registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(ACTION_INTENT_RECEIVER_WIFI)) {
                int index = (int) intent.getSerializableExtra("index");
                switch (index) {
                    case 0:

                        break;
                    case 1:

                        break;
                }
            }
        }
    }


    private void onEvent() {
        add_img.setOnClickListener(this);
        delete_img.setOnClickListener(this);
        next_step_id.setOnClickListener(this);
        add_img.setOnTouchListener(this);
        add_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                add_long_click = true;
                time_go_add();
                return false;
            }
        });
        delete_img.setOnTouchListener(this);
        delete_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                delete_long_click = true;
                time_go_delete();
                return false;
            }
        });
    }

    private void initDevice() {
        currGizWifiDevice = (GizWifiDevice) getActivity().getIntent().getParcelableExtra(
                "GizWifiDevice");
        if (currGizWifiDevice != null) {
            deviceId = currGizWifiDevice.getDid();
            // 在下载数据之前需要设置设备ID，用哪个设备去下载
            YkanSDKManager.getInstance().setDeviceId(deviceId);
            if (currGizWifiDevice.isSubscribed() == false) {
                currGizWifiDevice.setSubscribe(true);
            }
        }
        //小苹果 小夜灯
        driverControl = new DeviceController(getActivity().getApplicationContext(), currGizWifiDevice, new IDeviceControllerListener() {
            @Override
            public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {

            }

            @Override
            public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, String> hardwareInfo) {

            }

            @Override
            public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
                Log.e(TAG, result.toString());
            }
        });
    }

    @Override
    public void initData() {
        curr_map = (Map) getActivity().getIntent().getSerializableExtra("currBrand");// intent.putExtra("currBrand",(Serializable) currBrand);
        if (curr_map != null) {
            tid = (String) curr_map.get("tid");
            bid = (int) curr_map.get("bid");
            brand_name = (String) curr_map.get("name");
            control_number = (String) curr_map.get("number");
            switch (tid) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                case "2":
                    pinpai_pic.setImageResource(icon_brands[0]);
                    peipai_name.setText(brand_name + "  电视机");
                    break;
                case "7":
                    pinpai_pic.setImageResource(icon_brands[1]);
                    peipai_name.setText(brand_name + "  空调");
                    break;
            }
        }
    }

    @Override
    public void onStart() {//onStart()-这个方法在屏幕唤醒时调用。
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("peng", "MacFragment->onResume:name:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static EachMatchFragment newInstance() {
        EachMatchFragment newFragment = new EachMatchFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//1,是down,2是up,3是click
//            case R.id.add_img://0-100
//                //index +"/" + remote_control_size;
//                Log.e("robin debug", "cansal button ---> click");
//                action_click = true;
//                add_remote();
//                break;
//            case R.id.delete_img://100 -0
//                action_click = true;
//                delete_remote();
//                break;
            case R.id.next_step_id:
                Map map = new HashMap();
                switch (tid) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                    case "2":
                        map.put("type", "202");
                        break;
                    case "7":
                        map.put("type", "206");
                        break;
                }
                map.put("deviceId", rid);
                map.put("controllerNumber", control_number);
                List<Map> list_code = new ArrayList<>();
                Map map_code = new HashMap();
                map_code.put("code", code);
                map_code.put("name", "power");
                list_code.add(map_code);
                map.put("codeList", list_code);
                map.put("addType", "2");
                Intent intent = new Intent(getActivity(), AddWifiYaoKongQiScucessActivity.class);
                intent.putExtra("sraum_addWifiAppleDevice_map", (Serializable) map);
                startActivity(intent);
                break;
        }
    }

    /**
     * 递减遥控器编码
     */
    private void delete_remote() {
        getActivity().runOnUiThread(new Runnable() {// ----
            @Override
            public void run() {
                if (remote_control_size != 0) {
                    index--;
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                    switch (tid) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                        case "2":
                            yaokong_name_promat_one.setText(yaokong_wifi_one[0]);
                            yaokong_name_promat_two.setText(yaokong_wifi_two[0]);
                            yaokong_name_promat_three.setText(yaokong_wifi_three[0]);
                            yaokong_name_promat.setText(yaokong_wifi_before[0]);
                            break;
                        case "7":
                            yaokong_name_promat_one.setText(yaokong_wifi_one[1]);
                            yaokong_name_promat_two.setText(yaokong_wifi_two[1]);
                            yaokong_name_promat_three.setText(yaokong_wifi_three[1]);
                            yaokong_name_promat.setText(yaokong_wifi_before[1]);
                            break;
                    }
                    if (index == -1) {
                        yaokong_name_promat.setVisibility(View.VISIBLE);
                        yaokongqi_linear.setVisibility(View.GONE);
                        index = remote_control_size;
                    } else if (index < -1) {
                        index = remote_control_size - 1;
                        num_txt.setText(index + 1 + "/" + remote_control_size);
                        select_name.setText(nameRemote.get(index));
                        yaokong_name_promat.setVisibility(View.GONE);
                        yaokongqi_linear.setVisibility(View.VISIBLE);
                        excute_pipei();
                    } else {
                        num_txt.setText(index + 1 + "/" + remote_control_size);
                        select_name.setText(nameRemote.get(index));
                        yaokong_name_promat.setVisibility(View.GONE);
                        yaokongqi_linear.setVisibility(View.VISIBLE);
                        excute_pipei();
                    }
                }
            }
        });
    } //

    /**
     * 遥控器执行匹配空调动作
     */
    private void excute_pipei() {//逐个匹配，没有回调函数，遥控者听到滴一声，则说明匹配成功，
        if (index <= -1 || index >= remote_control_size) {
            return;
        }
        currRemoteControl = remoteControls.get(index);
        if (currRemoteControl != null) {
            if (currRemoteControl.getRcCommand() != null && currRemoteControl.getRcCommand().size() > 0) {
                CharSequence c[] = new CharSequence[currRemoteControl.getRcCommand().size()];
                final String sCode[] = new String[currRemoteControl.getRcCommand().size()];
                Iterator<Map.Entry<String, KeyCode>> iterator = currRemoteControl.getRcCommand().entrySet().iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Map.Entry<String, KeyCode> entry = iterator.next();
                    c[i] = entry.getKey();
                    sCode[i] = entry.getValue().getSrcCode();
                    i++;
                }
//                builder.setTitle("测试匹配").setItems(c, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).create().show();

                rid = currRemoteControl.getRid();
                code = sCode[0];
                driverControl.sendCMD(sCode[0]);
            }
        }
    }


    /***
     *       sCode[i] = entry.getValue().getSrcCode();
     *       获取key-code
     */
    private String get_key_code(String mode, int position) {
        if (driverControl != null || currRemoteControl != null) {
            HashMap<String, KeyCode> map = currRemoteControl.getRcCommand();
            Set<String> set = map.keySet();
            String key = null;
            for (String s : set) {
                if (s.contains(mode)) {
                    key = s;
                }
            }
//            driverControl.sendCMD(map.get(key).getSrcCode());
            return map.get(key).getSrcCode();
        }
        return "";
    }

    /**
     * 递增遥控器编码
     */
    private void add_remote() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (remote_control_size != 0) {
                    index++;
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                    switch (tid) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                        case "2":
                            yaokong_name_promat_one.setText(yaokong_wifi_one[0]);
                            yaokong_name_promat_two.setText(yaokong_wifi_two[0]);
                            yaokong_name_promat_three.setText(yaokong_wifi_three[0]);
                            yaokong_name_promat.setText(yaokong_wifi_before[0]);
                            break;
                        case "7":
                            yaokong_name_promat_one.setText(yaokong_wifi_one[1]);
                            yaokong_name_promat_two.setText(yaokong_wifi_two[1]);
                            yaokong_name_promat_three.setText(yaokong_wifi_three[1]);
                            yaokong_name_promat.setText(yaokong_wifi_before[1]);
                            break;
                    }
                    if (index == remote_control_size) {
                        index = 0;
                    }
                    if (index == -1) {
                        yaokong_name_promat.setVisibility(View.VISIBLE);
                        yaokongqi_linear.setVisibility(View.GONE);
                    } else {
                        if (index >= remote_control_size) {
                            index = remote_control_size - 1;
                        }
                        num_txt.setText(index + 1 + "/" + remote_control_size);
                        select_name.setText(nameRemote.get(index));
                        yaokong_name_promat.setVisibility(View.GONE);
                        yaokongqi_linear.setVisibility(View.VISIBLE);
                        excute_pipei();
                    }
                }
            }
        });
    }

    private int index = -1;

    /**
     * 执行加减动作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    excute_thread_add();
                    break;
                case 1:
                    excute_thread_delete();
                    break;
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {//1,是down,2是up,3是click
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.add_img:
                        if (!add_long_click)
                            excute_thread_add();
                        break;
                    case R.id.delete_img:
                        if (!delete_long_click)
                            excute_thread_delete();
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.add_img:
                        if (add_long_click)
                            add_long_click = false;

                        break;
                    case R.id.delete_img:
                        if (delete_long_click)
                            delete_long_click = false;
                        break;
                }
                break;
        }
        return false;
    }

    /**
     * 计时器
     */
    private void time_go_add() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (add_long_click) {
                    try {
                        handler.sendEmptyMessage(0);
                        Thread.sleep(1000);
                        Log.e("zhu", " Thread.sleep(1000);");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 计时器
     */
    private void time_go_delete() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (delete_long_click) {
                    try {
                        handler.sendEmptyMessage(1);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    /**
     * 执行动作线程
     */
    private void excute_thread_add() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                add_remote();
            }
        }).start();
    }

    /**
     * 执行动作线程
     */
    private void excute_thread_delete() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                delete_remote();
            }
        }).start();
    }


    class DownloadThread extends Thread {
        private String doit;
        String result = "";

        public DownloadThread(String doit) {
            this.doit = doit;
        }

        @Override
        public void run() {
//            dialogUtils.sendMessage(1);
            final Message message = mHandler.obtainMessage();
            switch (doit) {
                case "getDeviceType"://获取设备列表
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogUtil.loadDialog();
                        }
                    });
                    ykanInterface.getDeviceType(currGizWifiDevice.getMacAddress(), new YKanHttpListener() {
                        @Override
                        public void onSuccess(BaseResult baseResult) {
                            DeviceTypeResult deviceResult = (DeviceTypeResult) baseResult;
                            message.what = 0;
                            deviceType = deviceResult.getRs();//获取设备类型
                            currDeviceType = deviceType.get(0);
                            Log.d(TAG, " getDeviceType result:" + result);
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onFail(YKError ykError) {
                            Log.e(TAG, "ykError:" + ykError.toString());
                            common_end_handler(message);
                        }
                    });
                    break;
                case "getBrandByType"://获取设备品牌集合
                    // int type = 7 ;//1:机顶盒，2：电视机
                    if (currDeviceType != null) { //DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                        ykanInterface
                                .getBrandsByType(currGizWifiDevice.getMacAddress(), Integer.parseInt(tid), new YKanHttpListener() {
                                    @Override
                                    public void onSuccess(BaseResult baseResult) {
                                        BrandResult brandResult = (BrandResult) baseResult;
                                        brands = brandResult.getRs();
                                        message.what = 1;
                                        Log.d(TAG, " getBrandByType result:" + brandResult);

                                        mHandler.sendMessage(message);
                                        common_end_handler(message);
                                    }

                                    @Override
                                    public void onFail(YKError ykError) {
                                        Log.e(TAG, "ykError:" + ykError.toString());
                                        common_end_handler(message);
                                    }
                                });
                    } else {
                        result = "请调用获取设备接口";
                    }
                    break;
                case "getMatchedDataByBrand":
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogUtil.loadDialog();
                        }
                    });
                    if (bid != 0) {
                        if (ykanInterface == null) {
                            return;
                        }

                        String mac = currGizWifiDevice.getMacAddress();
                        if (mac == null) return;
                        ykanInterface.getRemoteMatched(mac,
                                bid, Integer.parseInt(tid), new YKanHttpListener() {
                                    //String mac, int bid, int type, YKanHttpListener listener
                                    @Override
                                    public void onSuccess(BaseResult baseResult) {
                                        controlResult = (MatchRemoteControlResult) baseResult;
                                        remoteControls = controlResult.getRs();
                                        result = controlResult.toString();
                                        message.what = 2;
                                        mHandler.sendMessage(message);
                                        common_end_handler(message);
                                        Log.d(TAG, " getMatchedDataByBrand result:" + result);
                                    }

                                    @Override
                                    public void onFail(YKError ykError) {
                                        Log.e(TAG, "ykError:" + ykError.toString());
                                        common_end_handler(message);
                                    }
                                });
                    } else {
                        result = "请调用获取设备接口";
                    }
                    break;
                default:
                    break;
            }
//            message.obj = result;
        }
    }

    private void common_end_handler(Message message) {
//            dialogUtils.sendMessage(0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogUtil.removeDialog();
            }
        });
    }

    private int remote_control_size;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://获取设备列表成功
                    new DownloadThread("getBrandByType").start();
                    break;
                case 1://获取品牌列表成功

                    break;
                case 2:
                    nameRemote.clear();
                    for (int i = 0; i < remoteControls.size(); i++) {
                        nameRemote.add(remoteControls.get(i).getName() + "-"
                                + remoteControls.get(i).getRmodel());
                    }
                    remote_control_size = remoteControls.size();
                    break;//获取当前品牌下的遥控器列表成功
            }
        }
    };


}
