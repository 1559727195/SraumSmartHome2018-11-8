package com.massky.sraum.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.massky.sraum.R;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.activity.AddWifiYaoKongQiScucessActivity;
import com.massky.sraum.activity.RemoteControlMatchingActivity;
import com.massky.sraum.base.Basecfragment;
import com.yaokan.sdk.ir.YKanHttpListener;
import com.yaokan.sdk.ir.YkanIRInterface;
import com.yaokan.sdk.ir.YkanIRInterfaceImpl;
import com.yaokan.sdk.model.BaseResult;
import com.yaokan.sdk.model.Brand;
import com.yaokan.sdk.model.DeviceDataStatus;
import com.yaokan.sdk.model.DeviceType;
import com.yaokan.sdk.model.KeyCode;
import com.yaokan.sdk.model.MatchRemoteControl;
import com.yaokan.sdk.model.MatchRemoteControlResult;
import com.yaokan.sdk.model.OneKeyMatchKey;
import com.yaokan.sdk.model.RemoteControl;
import com.yaokan.sdk.model.YKError;
import com.yaokan.sdk.wifi.DeviceController;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.listener.LearnCodeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import butterknife.InjectView;

import static com.massky.sraum.activity.RemoteControlMatchingActivity.ACTION_INTENT_RECEIVER_WIFI;

/**
 * Created by masskywcy on 2016-09-05.
 */
/*用于第一个fragment主界面*/
public class OneKeyMatchFragment extends Basecfragment implements LearnCodeListener, YKanHttpListener {
    private Map curr_map = new HashMap();
    private int bid;
    private String brand_name = "";
    private String rid = "";
    private String code = "";
    private String tid = "";
    private String control_number = "";
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
    private DialogUtil dialogUtil;
    private GizWifiDevice gizWifiDevice;
    private String key = "";
    List<MatchRemoteControl> list = new ArrayList<>();
    private ControlAdapter controlAdapter;
    @InjectView(R.id.first_linear)
    LinearLayout first_linear;
    private MessageReceiver mMessageReceiver;
    @InjectView(R.id.power_id)
    TextView power_id;
    @InjectView(R.id.loading_error_linear)
    LinearLayout loading_error_linear;
    @InjectView(R.id.again_btn)
    Button again_btn;
    @InjectView(R.id.learn_btn)
    Button learn_btn;


    @Override
    protected int viewId() {
        return R.layout.onekey_match_fragment;
    }

    @Override
    public void onStart() {//onStart()-这个方法在屏幕唤醒时调用。
        super.onStart();
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
                    default:
                        if (driverControl != null) {
                            driverControl.learnStop();
                        }
                        power_id.setText("电源键");
                        loading_error_linear.setVisibility(View.GONE);
                        break;
                    case 1:
                        if (driverControl != null) {
                            driverControl.startLearn();
                        }
                        break;
                }
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (driverControl != null) {
            driverControl.learnStop();
        }
    }


    @Override
    protected void onView() {
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getActivity().getApplicationContext());
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getActivity().getApplicationContext());
        init();
        initDevice();
        List<GizWifiDevice> gizWifiDevices = DeviceManager
                .instanceDeviceManager(getActivity().getApplicationContext()).getCanUseGizWifiDevice();
        if (gizWifiDevices != null) {
            Log.e("YKCodeAPIActivity", gizWifiDevices.size() + "");
        }
        dialogUtil = new DialogUtil(getActivity());
//        new DownloadThread("getMatchedDataByBrand").start();//下载遥控器列表
//        onEvent();
        switch (Integer.parseInt(tid)) {
            case DeviceType.AIRCONDITION:
                key = "on";
                break;
            case DeviceType.MULTIMEDIA:
                key = "ok";
                break;
            default:
                key = "power";
        }
//        showDlg();;
        registerMessageReceiver();
    }

    private class ControlAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ControlAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity().getApplicationContext())
                        .inflate(R.layout.lv_control, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tv_rc_name);
                holder.btnOn = (Button) convertView.findViewById(R.id.btn_on);
                holder.btnOff = (Button) convertView.findViewById(R.id.btn_off);
                holder.btnSOff = (Button) convertView.findViewById(R.id.btn_s_off);
                holder.btnSOn = (Button) convertView.findViewById(R.id.btn_s_on);
                holder.tvDl = (TextView) convertView.findViewById(R.id.dl);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(list.get(position).getName() + "-" + list.get(position).getRmodel());

            holder.btnOff.setVisibility(View.VISIBLE);
            holder.btnOn.setVisibility(View.VISIBLE);
            holder.btnSOff.setVisibility(View.VISIBLE);
            holder.btnSOn.setVisibility(View.VISIBLE);
            if (list.get(position).getRcCommand().size() <= 2) {
                holder.btnSOff.setVisibility(View.GONE);
                holder.btnSOn.setVisibility(View.GONE);
            }
            holder.btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendCMD("on", position);
                }
            });
            holder.btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendCMD("off", position);
                }
            });
            holder.btnSOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendCMD("u0", position);
                }
            });
            holder.btnSOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendCMD("u1", position);
                }
            });
            holder.tvDl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    dialogUtils.sendMessage(ProgressDialogUtils.SHOW);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }).start();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView name = null;
            Button btnOn = null;
            Button btnOff = null;
            Button btnSOff = null;
            Button btnSOn = null;
            TextView tvDl = null;
        }
    }

    /***
     *       sCode[i] = entry.getValue().getSrcCode();
     * @param mode
     * @param position
     */
    private void sendCMD(String mode, int position) {
        if (driverControl != null) {
            HashMap<String, KeyCode> map = list.get(position).getRcCommand();
            Set<String> set = map.keySet();
            String key = null;
            for (String s : set) {
                if (s.contains(mode)) {
                    key = s;
                }
            }
            driverControl.sendCMD(map.get(key).getSrcCode());
        }
    }


    /***
     *       sCode[i] = entry.getValue().getSrcCode();
     *       获取key-code
     */
    private String get_key_code(String mode, int position) {
        if (driverControl != null) {
            HashMap<String, KeyCode> map = list.get(position).getRcCommand();
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


    private void initDevice() {
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getActivity().getApplicationContext());
        gizWifiDevice = (GizWifiDevice) getActivity().getIntent().getParcelableExtra(
                "GizWifiDevice");
        driverControl = new DeviceController(getActivity(), gizWifiDevice, null);
        driverControl.initLearn(this);
    }


    @Override
    public void initData() {//刷新数据，选择viewpager时刷新数据
//        if (driverControl != null)
//            driverControl.startLearn();
    }

    private void init() {
        again_btn.setOnClickListener(this);
        learn_btn.setOnClickListener(this);
        curr_map = (Map) getActivity().getIntent().getSerializableExtra("currBrand");// intent.putExtra("currBrand",(Serializable) currBrand);
        if (curr_map != null) {
            tid = (String) curr_map.get("tid");
            bid = (int) curr_map.get("bid");
            brand_name = (String) curr_map.get("name");
            control_number = (String) curr_map.get("number");
            switch (tid) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
                case "2":
//                    pinpai_pic.setImageResource(icon_brands[0]);
//                    peipai_name.setText(brand_name + "  电视机");
                    break;
                case "7":
//                    pinpai_pic.setImageResource(icon_brands[1]);
//                    peipai_name.setText(brand_name + "  空调");
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.again_btn:
                power_id.setText("电源键");
                loading_error_linear.setVisibility(View.GONE);
                if (driverControl != null) {
                    driverControl.startLearn();
                }
                break;
            case R.id.learn_btn:
                power_id.setText("电源键");
                loading_error_linear.setVisibility(View.GONE);
                if (driverControl != null) {
                    driverControl.startLearn();
                }
                break;
        }
    }


    public static OneKeyMatchFragment newInstance(OnDeviceMessageFragListener onDeviceMessageFragListener) {
        OneKeyMatchFragment newFragment = new OneKeyMatchFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    private void showDlg() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getActivity()).setMessage("请对准小苹果按" + key + "键").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        driverControl.startLearn();
                    }
                }).create().show();
            }
        });
    }

    @Override
    public void onSuccess(BaseResult baseResult) {
        if (getActivity().isFinishing()) {
            return;
        }
//        dialogUtils.sendMessage(ProgressDialogUtils.DISMISS);
        if (baseResult instanceof OneKeyMatchKey) {
            key = ((OneKeyMatchKey) baseResult).getNext_cmp_key();
//            showDlg();
        } else if (baseResult instanceof RemoteControl) {
            remoteControl = (RemoteControl) baseResult;
        } else if (baseResult instanceof MatchRemoteControlResult) {
            final MatchRemoteControlResult result = (MatchRemoteControlResult) baseResult;
            Log.e("tttt", result.toString());
            if (result != null && result.getRs() != null && result.getRs().size() > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        first_linear.setVisibility(View.GONE);
//                        listView.setVisibility(View.VISIBLE);
//                        list.clear();
//                        list.addAll(result.getRs());
//                        controlAdapter.notifyDataSetChanged();
                        //学习到了东西

                        list.clear();
                        list.addAll(result.getRs());
                        //开始下载完整代码库
                        rid = list.get(0).getRid();
                        new DownloadThread("getDetailByRCID").start();

                    }
                });
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            refresh_view.autoRefresh();

        }
        LogUtil.eLength("查看显示方法", hidden + "");
    }

    @Override
    public void onFail(final YKError ykError) {
//        dialogUtils.sendMessage(ProgressDialogUtils.DISMISS);
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                new AlertDialog.Builder(getActivity()).setMessage(ykError.getError()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).create().show();
//            }
//        });
    }

    @Override
    public void didReceiveData(DeviceDataStatus dataStatus, String data) {
        // TODO Auto-generated method stub
        switch (dataStatus) {
            case DATA_LEARNING_SUCCESS://学习成功
                final String studyValue = data;//data 表示学习接收到的数据
//                dialogUtils.sendMessage(ProgressDialogUtils.SHOW);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ykanInterface.oneKeyMatched(gizWifiDevice.getMacAddress(), studyValue, tid + "", bid + "", key, OneKeyMatchFragment.this);
                    }
                }).start();
//                Toast.makeText(getActivity().getApplicationContext(), "学习成功", Toast.LENGTH_SHORT).show();
                break;
            case DATA_LEARNING_FAILED://学习失败
//                Toast.makeText(getActivity().getApplicationContext(), "学习失败", Toast.LENGTH_SHORT).show();
                driverControl.learnStop();
                if (power_id != null) {
                    power_id.setText("没有找到对应的遥控器");
                    loading_error_linear.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnDeviceMessageFragListener {
        void ondevice_message_frag();
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
                    if (gizWifiDevice.getMacAddress() != null) {
                        if (ykanInterface == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogUtil.loadDialog();
                            }
                        });
                        ykanInterface
                                .getRemoteDetails(gizWifiDevice.getMacAddress(), rid, new YKanHttpListener() {
                                    @Override
                                    public void onSuccess(BaseResult baseResult) {//
                                        if (baseResult != null) {
                                            remoteControl = (RemoteControl) baseResult;
//                                            Intent intent = new Intent();
//                                            intent.setClass(getActivity(), AirControlActivity.class);
//                                            intent.putExtra("GizWifiDevice", gizWifiDevice);
//                                            try {
//                                                intent.putExtra("remoteControl", new JsonParser().toJson(remoteControl));
//                                            } catch (JSONException e) {
//                                                Log.e(TAG, "JSONException:" + e.getMessage());
//                                            }
//                                            startActivity(intent);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    message.what = 1;
                                                    mHandler.sendMessage(message);
                                                    dialogUtil.removeDialog();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFail(final YKError ykError) {
//                                            dialogUtils.sendMessage(ProgressDialogUtils.DISMISS);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new AlertDialog.Builder(getActivity()).setMessage(ykError.getError()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create().show();
                                            }
                                        });
                                        Log.e(TAG, "ykError:" + ykError.toString());
                                    }
                                });
                    } else {
                        result = "请调用匹配数据接口";
                        Log.e(TAG, " getDetailByRCID 没有遥控器设备对象列表");
                    }
                    Log.d(TAG, " getDetailByRCID result:" + result);
                    break;
                default:
                    break;
            }
        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    add_onekey_scucess();
                    break;
                case 2:

                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 添加一键成功n
     */
    private void add_onekey_scucess() {
        Map map = new HashMap();
        switch (tid) {//DeviceType [tid=7, name=空调],DeviceType [tid=2, name=电视机]
            case "2":
                map.put("type", "202");
                break;
            case "7":
                map.put("type", "206");
                break;
        }
        map.put("deviceId", list.get(0).getRid());
        map.put("controllerNumber", control_number);
        List<Map> list_code = new ArrayList<>();
        Map map_code = new HashMap();
        code = get_key_code("on", 0);
        map_code.put("code", code);
        map_code.put("name", "on");
        list_code.add(map_code);
        map.put("codeList", list_code);
        map.put("addType", "1");
        Intent intent = new Intent(getActivity(), AddWifiYaoKongQiScucessActivity.class);
        intent.putExtra("sraum_addWifiAppleDevice_map", (Serializable) map);
        startActivity(intent);
    }
}
