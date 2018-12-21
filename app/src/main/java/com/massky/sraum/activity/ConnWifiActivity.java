package com.massky.sraum.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.massky.sraum.R;
import com.massky.sraum.Util.EyeUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.ConfigAppleDialogFragment;
import com.massky.sraum.fragment.ConfigDialogFragment;
import com.massky.sraum.receiver.LocalBroadcastManager;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.wifi.DeviceConfig;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.listener.IDeviceConfigListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/5/30.
 */

public class ConnWifiActivity extends BaseActivity implements IDeviceConfigListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.select_wlan_rel_big)
    PercentRelativeLayout select_wlan_rel_big;
    @InjectView(R.id.edit_wifi)
    ClearEditText edit_wifi;
    @InjectView(R.id.edit_password_gateway)
    ClearEditText edit_password_gateway;
    private ConfigAppleDialogFragment newFragment;
    @InjectView(R.id.eyeimageview_id_gateway)
    ImageView eyeimageview_id_gateway;
    private int CONNWIFI = 101;
    private EyeUtil eyeUtil;
    private DeviceConfig deviceConfig;
    private LinearLayout wifill;
    private String workSSID;
    @InjectView(R.id.conn_btn_dev)
    Button conn_btn_dev;
    private String wifi_name = "";
    private GizWifiDevice gizWifiDevice = null;
    private DeviceManager mDeviceManager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    edit_wifi.setEnabled(false);
                    break;
                case 1:
                    edit_wifi.setEnabled(true);
                    break;
            }
        }
    };
    private String TAG = "robin debug";

    @Override
    protected int viewId() {
        return R.layout.conn_wifi_act;
    }


    /**
     * The handler.
     */

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HandlerKey key = HandlerKey.values()[msg.what];
            switch (key) {
                case TIMER_TEXT:
//                    tvFinding.setText(timerText);
                    break;
                case START_TIMER:
//                    isStartTimer();
                    break;
                case SUCCESSFUL:
//                    Toast.makeText(getApplicationContext(), R.string.configuration_successful, toa).show();
//                    ToastUtil.showToast(ConnWifiActivity.this, R.string.configuration_successful + "");
                    if (connWifiInterfacer != null) {
                        connWifiInterfacer.conn_wifi_over();
                    }
                    stopConfig(true);
                    break;
                case FAILED:
//                    Toast.makeText(ConnWifiActivity.this, (String) msg.obj, ConnWifiActivity.this.toastTime).show();
                    ToastUtil.showToast(ConnWifiActivity.this, (String) msg.obj + "");
                    if (connWifiInterfacer != null) {
                        connWifiInterfacer.conn_wifi_over();
                    }
                    stopConfig(false);
                    break;
                default:
                    break;

            }
        }
    };


    /**
     * 启动配置遥控中心
     */
    private void startConfigAirlink() {
//        String str = etPsw.getText().toString();
        if (edit_wifi.getText().toString().trim().equals("")) {
            ToastUtil.showToast(ConnWifiActivity.this, "WIFI用户名为空");
            return;
        }

        if (edit_password_gateway.getText().toString().trim().equals("")) {
            ToastUtil.showToast(ConnWifiActivity.this, "WIFI密码为空");
            return;
        }
        show_dialog_fragment();
        workSSID = edit_wifi.getText().toString().trim();
        String str = edit_password_gateway.getText().toString().trim();
        deviceConfig.setPwdSSID(workSSID, str);
        deviceConfig.startAirlink(workSSID, str);
        mHandler.sendEmptyMessage(HandlerKey.START_TIMER.ordinal());
        mHandler.sendEmptyMessage(HandlerKey.TIMER_TEXT.ordinal());
    }

    @Override
    protected void onView() {
        //实例化配置对象
        deviceConfig = new DeviceConfig(getApplicationContext(), this);
        back.setOnClickListener(this);
        select_wlan_rel_big.setOnClickListener(this);
        eyeimageview_id_gateway.setOnClickListener(this);
        conn_btn_dev.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        onview();
        initWifiConect();
        initWifiName();
        initDialog();
        mDeviceManager = DeviceManager
                .instanceDeviceManager(getApplicationContext());
//        /**
//         * 开启机智云小苹果服务(service)
//         */
//        Intent intentService = new Intent(this,SimpleIntentService.class);
//        startService(intentService);

    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION_ConnWifi = "com.massky.sraum.ConnWifiActivity.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION_ConnWifi);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION_ConnWifi.equals(intent.getAction())) {
                    ToastUtil.showToast(ConnWifiActivity.this, "登录成功");
                }
            } catch (Exception e) {

            }
        }
    }

    private void onview() {
        eyeUtil = new EyeUtil(ConnWifiActivity.this, eyeimageview_id_gateway, edit_password_gateway, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ConnWifiActivity.this.finish();
                break;
            case R.id.select_wlan_rel_big:
                showPopwindow();
                break;
            case R.id.conn_btn_dev:
//                startActivity(new Intent(ConnWifiActivity.this,));
                //在这里弹出dialogFragment对话框
                startConfigAirlink();

                break;

            case R.id.eyeimageview_id_gateway:
                eyeUtil.EyeStatus();
                break;
        }
    }

    private void show_dialog_fragment() {
        if (!newFragment.isAdded()) {//DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(newFragment, "dialog");
            ft.commit();
        }
    }

    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(ConnWifiActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
        TextView promat_txt;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
//        promat_txt = (TextView) view.findViewById(R.id.promat_txt);
        name_gloud.setText("您的手机wifi尚未启动,请先启动您的手机wifi；并连接您的路由器在进行操作。"
        );
//        promat_txt.setText("连接");
        tv_title.setText("是否启动wifi?");
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(ConnWifiActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                handler.sendEmptyMessage(0);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接wifi的相关代码,跳转到WIFI连接界面
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivityForResult(wifiSettingsIntent, CONNWIFI);
                dialog.dismiss();
                handler.sendEmptyMessage(1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initWifiName();
    }

    /**
     * 获取连接的wifi名称
     */
    private void initWifiName() {
        // TODO Auto-generated method stub
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : "";

        if (wifiId != null && !wifiId.contains("<unknown ss")) {//wifiId在不连WIFI的情况下，去wifi快配wifiId = 0x
            //取出双引号中的字符
            String reg = "\"";
            String[] ss = wifiId.split(reg);
            if (ss.length >= 2) {
                edit_wifi.setText(ss[1]);
                wifi_name = ss[1];
                edit_password_gateway.setFocusable(true);
                edit_password_gateway.setFocusableInTouchMode(true);
                edit_password_gateway.requestFocus();
            }
        }
    }

    /**
     * 初始化wifi连接，快配前wifi一定要连接上
     */
    private void initWifiConect() {
        //初始化连接wifi dialog对话框
        // TODO Auto-generated method stub
        //获取系统服务
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取状态
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        //判断wifi已连接的条件
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {

        } else {//wifi还没有连接上弹出alertDialog对话框
            showCenterDeleteDialog();
        }
    }


    /**
     * 添加红外模块配置入网成功后关闭进度圈
     */
    private ConnWifiInterfacer connWifiInterfacer;

    public interface ConnWifiInterfacer {
        void conn_wifi_over();
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigAppleDialogFragment.newInstance(ConnWifiActivity.this, "", "",null);//初始化快配和搜索设备dialogFragment
//
        connWifiInterfacer = (ConnWifiInterfacer) newFragment;
    }


    /**
     * 显示popupWindow
     */
    @SuppressLint("WrongConstant")
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View outerView = inflater.inflate(R.layout.wheel_view, null);

//        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        ListView wv = (ListView) outerView.findViewById(R.id.wheel_view_wv);
//        wv.setOffset(1);
//        wv.setItems(Arrays.asList(PLANETS));
//        wv.setSeletion(2);
//        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//            @Override
//            public void onSelected(int selectedIndex, String item) {
//                Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
//            }
//        });

        final String[] data = {"类型一", "类型二", "类型三", "类型四"};
        ArrayAdapter<String> array = new ArrayAdapter<>(this,
                R.layout.simple_expandable_list_item_new, data);
        wv.setAdapter(array);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//水平
        // 初始化自定义的适配器
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;

        final PopupWindow window = new PopupWindow(outerView,
                displayWidth / 2,
                WindowManager.LayoutParams.WRAP_CONTENT);//高度写死

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;

        getWindow().setAttributes(lp);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.style_pop_animation);
        // 在底部显示
//        window.showAtLocation(WuYeTouSu_NewActivity.this.findViewById(R.id.tousu_select),
//                Gravity.CENTER_HORIZONTAL, 0, 0);
//        window.showAsDropDown(select_wlan_rel_big);

        // 将pixels转为dip
        int xoffInDip = pxTodip(displayWidth) / 4 * 3;

        window.showAsDropDown(select_wlan_rel_big, xoffInDip, xoffInDip / 3);
        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        wv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                tousu_txt1.setText(data[position]);
                window.dismiss();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int pxTodip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    protected void stopConfig(boolean successful) {
        if (successful) {
//            finish();
            //快配成功页面跳转
            if (gizWifiDevice != null) {
//                add_bind_dingyue();
                Intent intent = new Intent(ConnWifiActivity.this, AddWifiHongWaiScucessActivity.class);
//                intent.putExtra("wifi_name", wifi_name);
//                intent.putExtra("deviceInfo", deviceInfo);
                //            macDevice = wifiDevices.get(wifiDevices.size() - 1).getMacAddress();
                intent.putExtra("GizWifiDevice", gizWifiDevice);
                String mac = gizWifiDevice.getMacAddress();
                Map map_device = new HashMap();
                map_device.put("type", "AA02");
                map_device.put("mac", mac);
                map_device.put("controllerId", mac);
                map_device.put("wifi", wifi_name);
//                map_device.put("deviceInfo", deviceInfo);
                intent.putExtra("gizWifiDevice", (Serializable) map_device);
                startActivity(intent);
            }
        }
    }


    public enum HandlerKey {

        /**
         * 倒计时通知
         */
        TIMER_TEXT,

        /**
         * 倒计时开始
         */
        START_TIMER,

        /**
         * 配置成功
         */
        SUCCESSFUL,

        /**
         * 配置失败
         */
        FAILED,

    }

    @Override
    public void didSetDeviceOnboarding(GizWifiErrorCode result, String mac,
                                       String did, String productKey) {

    }

    @Override
    public void didSetDeviceOnboardingX(GizWifiErrorCode result, GizWifiDevice gizWifiDevice) {
        if (GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING == result) {
            return;
        }
        Message message = new Message();
        if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
            message.obj = gizWifiDevice.getMacAddress();
            message.what = HandlerKey.SUCCESSFUL.ordinal();
            this.gizWifiDevice = gizWifiDevice;
        } else {
            message.what = HandlerKey.FAILED.ordinal();
            message.obj = toastError(this, result);
        }
        mHandler.sendMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mDeviceManager.setGizWifiCallBack(mGizWifiCallBack);
    }


//    private GizWifiCallBack mGizWifiCallBack = new GizWifiCallBack() {
//
//        @Override
//        public void didUnbindDeviceCd(GizWifiErrorCode result, String did) {
//            super.didUnbindDeviceCd(result, did);
//            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
//                // 解绑成功
//                Logger.d(TAG, "解除绑定成功");
//            } else {
//                // 解绑失败
//                Logger.d(TAG, "解除绑定失败");
//            }
//        }
//
//        @Override
//        public void didBindDeviceCd(GizWifiErrorCode result, String did) {
//            super.didBindDeviceCd(result, did);
//            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
//                // 绑定成功
//                Logger.d(TAG, "绑定成功");
//            } else {
//                // 绑定失败
//                Logger.d(TAG, "绑定失败");
//            }
//        }
//
//        @Override
//        public void didSetSubscribeCd(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
//            super.didSetSubscribeCd(result, device, isSubscribed);
//            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
//                // 解绑成功
//                Logger.d(TAG, (isSubscribed ? "订阅" : "取消订阅") + "成功");
//
//                if(isSubscribed){
//
//                }
//            } else {
//                // 解绑失败
//                Logger.d(TAG, "订阅失败");
//            }
//        }
//
//        @Override
//        public void discoveredrCb(GizWifiErrorCode result,
//                                  List<GizWifiDevice> deviceList) {
//            Logger.d(TAG,
//                    "discoveredrCb -> deviceList size:" + deviceList.size()
//                            + "  result:" + result);
//            switch (result) {
//                case GIZ_SDK_SUCCESS:
//                    Logger.e(TAG, "load device  sucess");
////                    update(deviceList);
////                    if(deviceList.get(0).getNetStatus()==GizWifiDeviceNetStatus.GizDeviceOffline)
//
//                    break;
//                default:
//                    break;
//
//            }
//
//        }
//    };

    private String toastError(Context ctx, GizWifiErrorCode errorCode) {
        String errorString = (String) ctx.getResources().getText(R.string.UNKNOWN_ERROR);
        switch (errorCode) {
            case GIZ_SDK_PARAM_FORM_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_PARAM_FORM_INVALID);
                break;
            case GIZ_SDK_CLIENT_NOT_AUTHEN:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_CLIENT_NOT_AUTHEN);
                break;
            case GIZ_SDK_CLIENT_VERSION_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_CLIENT_VERSION_INVALID);
                break;
            case GIZ_SDK_UDP_PORT_BIND_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_UDP_PORT_BIND_FAILED);
                break;
            case GIZ_SDK_DAEMON_EXCEPTION:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DAEMON_EXCEPTION);
                break;
            case GIZ_SDK_PARAM_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_PARAM_INVALID);
                break;
            case GIZ_SDK_APPID_LENGTH_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_APPID_LENGTH_ERROR);
                break;
            case GIZ_SDK_LOG_PATH_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_LOG_PATH_INVALID);
                break;
            case GIZ_SDK_LOG_LEVEL_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_LOG_LEVEL_INVALID);
                break;
            case GIZ_SDK_DEVICE_CONFIG_SEND_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONFIG_SEND_FAILED);
                break;
            case GIZ_SDK_DEVICE_CONFIG_IS_RUNNING:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING);
                break;
            case GIZ_SDK_DEVICE_CONFIG_TIMEOUT:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONFIG_TIMEOUT);
                break;
            case GIZ_SDK_DEVICE_DID_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_DID_INVALID);
                break;
            case GIZ_SDK_DEVICE_MAC_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_MAC_INVALID);
                break;
            case GIZ_SDK_DEVICE_PASSCODE_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_PASSCODE_INVALID);
                break;
            case GIZ_SDK_DEVICE_NOT_SUBSCRIBED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_NOT_SUBSCRIBED);
                break;
            case GIZ_SDK_DEVICE_NO_RESPONSE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_NO_RESPONSE);
                break;
            case GIZ_SDK_DEVICE_NOT_READY:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_NOT_READY);
                break;
            case GIZ_SDK_DEVICE_NOT_BINDED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_NOT_BINDED);
                break;
            case GIZ_SDK_DEVICE_CONTROL_WITH_INVALID_COMMAND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONTROL_WITH_INVALID_COMMAND);
                break;
        /*case GIZ_SDK_DEVICE_CONTROL_FAILED:
         errorString= (String)
		ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONTROL_FAILED);
		 break;*/
            case GIZ_SDK_DEVICE_GET_STATUS_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_GET_STATUS_FAILED);
                break;
            case GIZ_SDK_DEVICE_CONTROL_VALUE_TYPE_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONTROL_VALUE_TYPE_ERROR);
                break;
            case GIZ_SDK_DEVICE_CONTROL_VALUE_OUT_OF_RANGE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONTROL_VALUE_OUT_OF_RANGE);
                break;
            case GIZ_SDK_DEVICE_CONTROL_NOT_WRITABLE_COMMAND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONTROL_NOT_WRITABLE_COMMAND);
                break;
            case GIZ_SDK_BIND_DEVICE_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_BIND_DEVICE_FAILED);
                break;
            case GIZ_SDK_UNBIND_DEVICE_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_UNBIND_DEVICE_FAILED);
                break;
            case GIZ_SDK_DNS_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DNS_FAILED);
                break;
            case GIZ_SDK_M2M_CONNECTION_SUCCESS:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_M2M_CONNECTION_SUCCESS);
                break;
            case GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED);
                break;
            case GIZ_SDK_CONNECTION_TIMEOUT:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_CONNECTION_TIMEOUT);
                break;
            case GIZ_SDK_CONNECTION_REFUSED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_CONNECTION_REFUSED);
                break;
            case GIZ_SDK_CONNECTION_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_CONNECTION_ERROR);
                break;
            case GIZ_SDK_CONNECTION_CLOSED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_CONNECTION_CLOSED);
                break;
            case GIZ_SDK_SSL_HANDSHAKE_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_SSL_HANDSHAKE_FAILED);
                break;
            case GIZ_SDK_DEVICE_LOGIN_VERIFY_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_LOGIN_VERIFY_FAILED);
                break;
            case GIZ_SDK_INTERNET_NOT_REACHABLE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_INTERNET_NOT_REACHABLE);
                break;
            case GIZ_SDK_HTTP_ANSWER_FORMAT_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_HTTP_ANSWER_FORMAT_ERROR);
                break;
            case GIZ_SDK_HTTP_ANSWER_PARAM_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_HTTP_ANSWER_PARAM_ERROR);
                break;
            case GIZ_SDK_HTTP_SERVER_NO_ANSWER:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_HTTP_SERVER_NO_ANSWER);
                break;
            case GIZ_SDK_HTTP_REQUEST_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_HTTP_REQUEST_FAILED);
                break;
            case GIZ_SDK_OTHERWISE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_OTHERWISE);
                break;
            case GIZ_SDK_MEMORY_MALLOC_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_MEMORY_MALLOC_FAILED);
                break;
            case GIZ_SDK_THREAD_CREATE_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_THREAD_CREATE_FAILED);
                break;
            case GIZ_SDK_TOKEN_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_TOKEN_INVALID);
                break;
            case GIZ_SDK_GROUP_ID_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_GROUP_ID_INVALID);
                break;
            case GIZ_SDK_GROUP_PRODUCTKEY_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_GROUP_PRODUCTKEY_INVALID);
                break;
            case GIZ_SDK_GROUP_GET_DEVICE_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_GROUP_GET_DEVICE_FAILED);
                break;
            case GIZ_SDK_DATAPOINT_NOT_DOWNLOAD:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DATAPOINT_NOT_DOWNLOAD);
                break;
            case GIZ_SDK_DATAPOINT_SERVICE_UNAVAILABLE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DATAPOINT_SERVICE_UNAVAILABLE);
                break;
            case GIZ_SDK_DATAPOINT_PARSE_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DATAPOINT_PARSE_FAILED);
                break;
            // case GIZ_SDK_NOT_INITIALIZED:
            // errorString= (String)ctx.getResources().getText(R.string.GIZ_SDK_SDK_NOT_INITIALIZED);
            // break;
            case GIZ_SDK_APK_CONTEXT_IS_NULL:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_APK_CONTEXT_IS_NULL);
                break;
            case GIZ_SDK_APK_PERMISSION_NOT_SET:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_APK_PERMISSION_NOT_SET);
                break;
            case GIZ_SDK_CHMOD_DAEMON_REFUSED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_CHMOD_DAEMON_REFUSED);
                break;
            case GIZ_SDK_EXEC_DAEMON_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_EXEC_DAEMON_FAILED);
                break;
            case GIZ_SDK_EXEC_CATCH_EXCEPTION:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_EXEC_CATCH_EXCEPTION);
                break;
            case GIZ_SDK_APPID_IS_EMPTY:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_APPID_IS_EMPTY);
                break;
            case GIZ_SDK_UNSUPPORTED_API:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_UNSUPPORTED_API);
                break;
            case GIZ_SDK_REQUEST_TIMEOUT:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_REQUEST_TIMEOUT);
                break;
            case GIZ_SDK_DAEMON_VERSION_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DAEMON_VERSION_INVALID);
                break;
            case GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID);
                break;
            case GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED);
                break;
            case GIZ_SDK_NOT_IN_SOFTAPMODE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_NOT_IN_SOFTAPMODE);
                break;
            // case GIZ_SDK_PHONE_WIFI_IS_UNAVAILABLE:
            // errorString= (String)
            //ctx.getResources().getText(R.string.GIZ_SDK_PHONE_WIFI_IS_UNAVAILABLE);
            // break;
            case GIZ_SDK_RAW_DATA_TRANSMIT:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_RAW_DATA_TRANSMIT);
                break;
            case GIZ_SDK_PRODUCT_IS_DOWNLOADING:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_PRODUCT_IS_DOWNLOADING);
                break;
            case GIZ_SDK_START_SUCCESS:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SDK_START_SUCCESS);
                break;
            case GIZ_SITE_PRODUCTKEY_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SITE_PRODUCTKEY_INVALID);
                break;
            case GIZ_SITE_DATAPOINTS_NOT_DEFINED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SITE_DATAPOINTS_NOT_DEFINED);
                break;
            case GIZ_SITE_DATAPOINTS_NOT_MALFORME:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_SITE_DATAPOINTS_NOT_MALFORME);
                break;
            case GIZ_OPENAPI_MAC_ALREADY_REGISTERED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_MAC_ALREADY_REGISTERED);
                break;
            case GIZ_OPENAPI_PRODUCT_KEY_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_PRODUCT_KEY_INVALID);
                break;
            case GIZ_OPENAPI_APPID_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_APPID_INVALID);
                break;
            case GIZ_OPENAPI_TOKEN_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_TOKEN_INVALID);
                break;
            case GIZ_OPENAPI_USER_NOT_EXIST:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_USER_NOT_EXIST);
                break;
            case GIZ_OPENAPI_TOKEN_EXPIRED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_TOKEN_EXPIRED);
                break;
            case GIZ_OPENAPI_M2M_ID_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_M2M_ID_INVALID);
                break;
            case GIZ_OPENAPI_SERVER_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SERVER_ERROR);
                break;
            case GIZ_OPENAPI_CODE_EXPIRED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_CODE_EXPIRED);
                break;
            case GIZ_OPENAPI_CODE_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_CODE_INVALID);
                break;
            case GIZ_OPENAPI_SANDBOX_SCALE_QUOTA_EXHAUSTED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SANDBOX_SCALE_QUOTA_EXHAUSTED);
                break;
            case GIZ_OPENAPI_PRODUCTION_SCALE_QUOTA_EXHAUSTED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_PRODUCTION_SCALE_QUOTA_EXHAUSTED);
                break;
            case GIZ_OPENAPI_PRODUCT_HAS_NO_REQUEST_SCALE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_PRODUCT_HAS_NO_REQUEST_SCALE);
                break;
            case GIZ_OPENAPI_DEVICE_NOT_FOUND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_DEVICE_NOT_FOUND);
                break;
            case GIZ_OPENAPI_FORM_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_FORM_INVALID);
                break;
            case GIZ_OPENAPI_DID_PASSCODE_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_DID_PASSCODE_INVALID);
                break;
            case GIZ_OPENAPI_DEVICE_NOT_BOUND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_DEVICE_NOT_BOUND);
                break;
            case GIZ_OPENAPI_PHONE_UNAVALIABLE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_PHONE_UNAVALIABLE);
                break;
            case GIZ_OPENAPI_USERNAME_UNAVALIABLE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_USERNAME_UNAVALIABLE);
                break;
            case GIZ_OPENAPI_USERNAME_PASSWORD_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR);
                break;
            case GIZ_OPENAPI_SEND_COMMAND_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SEND_COMMAND_FAILED);
                break;
            case GIZ_OPENAPI_EMAIL_UNAVALIABLE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_EMAIL_UNAVALIABLE);
                break;
            case GIZ_OPENAPI_DEVICE_DISABLED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_DEVICE_DISABLED);
                break;
            case GIZ_OPENAPI_FAILED_NOTIFY_M2M:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_FAILED_NOTIFY_M2M);
                break;
            case GIZ_OPENAPI_ATTR_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_ATTR_INVALID);
                break;
            case GIZ_OPENAPI_USER_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_USER_INVALID);
                break;
            case GIZ_OPENAPI_FIRMWARE_NOT_FOUND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_FIRMWARE_NOT_FOUND);
                break;
            case GIZ_OPENAPI_JD_PRODUCT_NOT_FOUND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_JD_PRODUCT_NOT_FOUND);
                break;
            case GIZ_OPENAPI_DATAPOINT_DATA_NOT_FOUND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_DATAPOINT_DATA_NOT_FOUND);
                break;
            case GIZ_OPENAPI_SCHEDULER_NOT_FOUND:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SCHEDULER_NOT_FOUND);
                break;
            case GIZ_OPENAPI_QQ_OAUTH_KEY_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_QQ_OAUTH_KEY_INVALID);
                break;
            case GIZ_OPENAPI_OTA_SERVICE_OK_BUT_IN_IDLE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_OTA_SERVICE_OK_BUT_IN_IDLE);
                break;
            case GIZ_OPENAPI_BT_FIRMWARE_UNVERIFIED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_BT_FIRMWARE_UNVERIFIED);
                break;
            case GIZ_OPENAPI_BT_FIRMWARE_NOTHING_TO_UPGRADE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SAVE_KAIROSDB_ERROR);
                break;
            case GIZ_OPENAPI_SAVE_KAIROSDB_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SAVE_KAIROSDB_ERROR);
                break;
            case GIZ_OPENAPI_EVENT_NOT_DEFINED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_EVENT_NOT_DEFINED);
                break;
            case GIZ_OPENAPI_SEND_SMS_FAILED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SEND_SMS_FAILED);
                break;
            // case GIZ_OPENAPI_APPLICATION_AUTH_INVALID:
            // errorString= (String)
            //ctx.getResources().getText(R.string.GIZ_OPENAPI_APPLICATION_AUTH_INVALID);
            // break;
            case GIZ_OPENAPI_NOT_ALLOWED_CALL_API:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_NOT_ALLOWED_CALL_API);
                break;
            case GIZ_OPENAPI_BAD_QRCODE_CONTENT:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_BAD_QRCODE_CONTENT);
                break;
            case GIZ_OPENAPI_REQUEST_THROTTLED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_REQUEST_THROTTLED);
                break;
            case GIZ_OPENAPI_DEVICE_OFFLINE:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_DEVICE_OFFLINE);
                break;
            case GIZ_OPENAPI_TIMESTAMP_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_TIMESTAMP_INVALID);
                break;
            case GIZ_OPENAPI_SIGNATURE_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_SIGNATURE_INVALID);
                break;
            case GIZ_OPENAPI_DEPRECATED_API:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_DEPRECATED_API);
                break;
            case GIZ_OPENAPI_RESERVED:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_OPENAPI_RESERVED);
                break;
            case GIZ_PUSHAPI_BODY_JSON_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_BODY_JSON_INVALID);
                break;
            case GIZ_PUSHAPI_DATA_NOT_EXIST:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_DATA_NOT_EXIST);
                break;
            case GIZ_PUSHAPI_NO_CLIENT_CONFIG:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_NO_CLIENT_CONFIG);
                break;
            case GIZ_PUSHAPI_NO_SERVER_DATA:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_NO_SERVER_DATA);
                break;
            case GIZ_PUSHAPI_GIZWITS_APPID_EXIST:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_GIZWITS_APPID_EXIST);
                break;
            case GIZ_PUSHAPI_PARAM_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_PARAM_ERROR);
                break;
            case GIZ_PUSHAPI_AUTH_KEY_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_AUTH_KEY_INVALID);
                break;
            case GIZ_PUSHAPI_APPID_OR_TOKEN_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_APPID_OR_TOKEN_ERROR);
                break;
            case GIZ_PUSHAPI_TYPE_PARAM_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_TYPE_PARAM_ERROR);
                break;
            case GIZ_PUSHAPI_ID_PARAM_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_ID_PARAM_ERROR);
                break;
            case GIZ_PUSHAPI_APPKEY_SECRETKEY_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_APPKEY_SECRETKEY_INVALID);
                break;
            case GIZ_PUSHAPI_CHANNELID_ERROR_INVALID:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_CHANNELID_ERROR_INVALID);
                break;
            case GIZ_PUSHAPI_PUSH_ERROR:
                errorString = (String) ctx.getResources().getText(R.string.GIZ_PUSHAPI_PUSH_ERROR);
                break;
            default:
                errorString = (String) ctx.getResources().getText(R.string.UNKNOWN_ERROR);
                break;
        }
        return errorString;
    }
}
