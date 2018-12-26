package com.massky.sraum.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.massky.sraum.adapter.SelectInfraredForwardAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.PullToRefreshLayout;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.utils.Logger;
import com.yaokan.sdk.utils.Utility;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.GizWifiCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/6/13.
 */

public class SelectInfraredForwardActivity extends BaseActivity implements
        AdapterView.OnItemClickListener,
        PullToRefreshLayout.OnRefreshListener {

    private static final int REQUEST_SENSOR = 101;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.refresh_view)
    PullToRefreshLayout refresh_view;
    @InjectView(R.id.maclistview_id)
    ListView maclistview_id;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private SelectInfraredForwardAdapter selectexcutesceneresultadapter;
    private List<Map> list_hand_scene = new ArrayList<>();
    private Handler mHandler = new Handler();
    //    String[] again_elements = {"7", "8", "9",
//            "10", "11", "12", "13", "14", "15", "16"};
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private List<Boolean> list_bool = new ArrayList<>();
    private int position;
    private DialogUtil dialogUtil;
    private String TAG = SelectInfraredForwardActivity.class.getSimpleName();
    private int CONNWIFI = 101;

    /**
     * 小苹果绑定列表
     */
    private DeviceManager mDeviceManager;
    List<GizWifiDevice> wifiDevices = new ArrayList<GizWifiDevice>();
    private List<String> deviceNames = new ArrayList<String>();
    private GizWifiDevice mGizWifiDevice = null;
    private List<Map> wifi_apple_list = new ArrayList<>();
    private String mac;
    private String number;


    @Override
    protected int viewId() {
        return R.layout.selection_infrared_forward_lay;
    }


    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        maclistview_id.setOnItemClickListener(this);
        refresh_view.setOnRefreshListener(this);
//        refresh_view.autoRefresh();
        mDeviceManager = DeviceManager
                .instanceDeviceManager(getApplicationContext());
//        onData();
    }

    @Override
    protected void onEvent() {

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

        View view = LayoutInflater.from(SelectInfraredForwardActivity.this).inflate(R.layout.promat_dialog, null);
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
        final Dialog dialog = new Dialog(SelectInfraredForwardActivity.this, R.style.BottomDialog);
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
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接wifi的相关代码,跳转到WIFI连接界面
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivityForResult(wifiSettingsIntent, CONNWIFI);
                dialog.dismiss();
            }
        });
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


    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
//    R.drawable.magnetic_door_s,
//    R.drawable.human_induction_s, R.drawable.water_s, R.drawable.pm25_s,
//    R.drawable.emergency_button_s
    @Override
    protected void onData() {
//        getOtherDevices("");
//        intent_wifi.g("list_hand_scene", (Serializable) list_hand_scene);

        list_hand_scene = new ArrayList<>();
        listint.clear();
        listintwo.clear();
        list_hand_scene = (List<Map>) getIntent().getSerializableExtra("list_hand_scene");
        for (int i = 0; i < list_hand_scene.size(); i++) {
//            Map<String, String> mapdevice = new HashMap<>();
//            list_hand_scene.add(mapdevice);
            setPicture(list_hand_scene.get(i).get("type").toString());
        }

//        selectexcutesceneresultadapter.setLists(list_hand_scene, listint, listintwo);
//        selectexcutesceneresultadapter.notifyDataSetChanged();

        selectexcutesceneresultadapter = new SelectInfraredForwardAdapter(SelectInfraredForwardActivity.this,
                list_hand_scene, listint, listintwo, dialogUtil, new SelectInfraredForwardAdapter.RefreshListener() {

            @Override
            public void refresh() {
//                getOtherDevices("");
            }

            @Override
            public void onItemCLick(int position) {
                choose_the_brand(position);
            }
        });
        maclistview_id.setAdapter(selectexcutesceneresultadapter);
    }

    /**
     * 跳转到选择品牌界面
     *
     * @param position
     */
    private void choose_the_brand(int position) {
//        handler.sendEmptyMessage(0);
        mac = (String) list_hand_scene.get(position).get("controllerId");
        number = list_hand_scene.get(position).get("number").toString();
        //去根据mac去服务器端下载GizWifiDevice
        String apple_name = "";
        for (int i = 0; i < list_hand_scene.size(); i++) {
            if (list_hand_scene.get(i).get("controllerId").equals(mac)) {
                apple_name = list_hand_scene.get(i).get("name").toString();

            }
        }
        if (wifiDevices.size() != 0) {
            get_to_wifi(mac, apple_name);//绑定订阅
            toControlApplianAct();
        } else {
            ToastUtil.showToast(SelectInfraredForwardActivity.this, "请与" + apple_name
                    +
                    "在同一网络后再控制");
        }
    }

    private void toControlApplianAct() {
        Intent intent = new Intent(SelectInfraredForwardActivity.this, SelectControlApplianceActivity.class);
        if (mGizWifiDevice == null) {
            return;
        }
        intent.putExtra("GizWifiDevice", mGizWifiDevice);
//        intent.putExtra("tid", getIntent().getSerializableExtra("tid"));
        intent.putExtra("number", number);
        startActivity(intent);
    }

    /**
     * get_to_wifi
     *
     * @param mac
     * @param apple_name
     */
    private void get_to_wifi(String mac, String apple_name) {
        mGizWifiDevice = null;
        for (int i = 0; i < wifiDevices.size(); i++) {
            if (wifiDevices.get(i).getMacAddress().equals(mac)) {
                mGizWifiDevice = wifiDevices.get(i);
            }
        }
        if (!Utility.isEmpty(mGizWifiDevice)) { //
//            Object json = JSON.toJSON(mGizWifiDevice);
//            String str = new Gson().toJson(json);
            mDeviceManager.bindRemoteDevice(mGizWifiDevice);
            final GizWifiDevice finalMGizWifiDevice = mGizWifiDevice;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDeviceManager.setSubscribe(finalMGizWifiDevice, true);
                }
            }, 1000);
        } else {
            ToastUtil.showToast(SelectInfraredForwardActivity.this, "请与" + apple_name
                    +
                    "在同一网络后在控制");
            return;
        }
    }

    private void setPicture(String type) {
        switch (type) {
            case "AA02":
                listint.add(R.drawable.icon_hongwaizfq_40);
                listintwo.add(R.drawable.icon_hongwaizfq_40);
                break;
            case "7":
                listint.add(R.drawable.icon_menci_40);
                listintwo.add(R.drawable.icon_menci_40_active);
                break;
            case "8":
                listint.add(R.drawable.icon_rentiganying_40);
                listintwo.add(R.drawable.icon_rentiganying_40_active);
                break;
            case "9":
                listint.add(R.drawable.icon_shuijin_40);
                listintwo.add(R.drawable.icon_shuijin_40_active);
                break;
            case "10":
                listint.add(R.drawable.icon_pm25_40);
                listintwo.add(R.drawable.icon_pm25_40_active);
                break;
            case "11":
                listint.add(R.drawable.icon_jinjianniu_40);
                listintwo.add(R.drawable.icon_jinjianniu_40_active);
                break;
            case "12":
                listint.add(R.drawable.icon_rucebjq_40);
                listintwo.add(R.drawable.icon_rucebjq_40_active);
                break;
            case "13":
                listint.add(R.drawable.icon_yanwubjq_40);
                listintwo.add(R.drawable.icon_yanwubjq_40_active);
                break;
            case "14":
                listint.add(R.drawable.icon_ranqibjq_40);
                listintwo.add(R.drawable.icon_ranqibjq_40_active);
                break;
            case "15":
                listint.add(R.drawable.icon_zhinengmensuo_40);
                listintwo.add(R.drawable.icon_zhinengmensuo_40_active);
                break;
            case "16":
                listint.add(R.drawable.ic_launcher);
                listintwo.add(R.drawable.ic_launcher);
                break;
        }
    }

    /**
     * 获取门磁等第三方设备
     *
     * @param doit
     */
    private void getOtherDevices(final String doit) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(SelectInfraredForwardActivity.this));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleInfos
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        getOtherDevices("load");
                    }
                }, SelectInfraredForwardActivity.this, dialogUtil) {
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
                        listint.clear();
                        listintwo.clear();
                        for (int i = 0; i < user.controllerList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.controllerList.get(i).name);
                            mapdevice.put("number", user.controllerList.get(i).number);
                            mapdevice.put("type", user.controllerList.get(i).type);
                            mapdevice.put("controllerId", user.controllerList.get(i).controllerId);
                            list_hand_scene.add(mapdevice);
                            setPicture(user.controllerList.get(i).type);
                        }

                        selectexcutesceneresultadapter.setLists(list_hand_scene, listint, listintwo);
                        selectexcutesceneresultadapter.notifyDataSetChanged();
                        switch (doit) {
                            case "refresh":

                                break;
                            case "load":
                                break;
                        }
                        if (list_hand_scene.size() == 1)
                            choose_the_brand(0);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectInfraredForwardActivity.this.finish();
                break;
            case R.id.next_step_txt:


                break;
            case R.id.rel_scene_set:

                break;//执行某些手动场景
        }
    }


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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

//
//    intent =new
//
//    Intent(SelectInfraredForwardActivity.this, SelectControlApplianceActivity.class);
//
//    //        intent.putExtra("map_link", (Serializable) map);
//    startActivity(intent);


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (!isFinishing())
                        if (dialogUtil != null) dialogUtil.loadDialog();
                    break;
                case 2:
                    if (!isFinishing())
                        if (dialogUtil != null) dialogUtil.removeDialog();
                    toControlApplianAct();
                    break;
            }
        }
    };


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getOtherDevices("refresh");
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceManager.setGizWifiCallBack(mGizWifiCallBack);
        update(mDeviceManager.getCanUseGizWifiDevice());

//        getWifiApples();
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


//    /**
//     * 获取小苹果列表
//     */
//    private void getWifiApples() {
//        Map<String, String> mapdevice = new HashMap<>();
//        mapdevice.put("token", TokenUtil.getToken(SelectInfraredForwardActivity.this));
////        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
//        MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleInfos
//                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
//                    @Override
//                    public void addTogglenInterfacer() {//刷新togglen数据
//                        getWifiApples();
//                    }
//                }, SelectInfraredForwardActivity.this, dialogUtil) {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        super.onError(call, e, id);
//                    }
//
//                    @Override
//                    public void pullDataError() {
//                        super.pullDataError();
//                    }
//
//                    @Override
//                    public void emptyResult() {
//                        super.emptyResult();
//                    }
//
//                    @Override
//                    public void wrongToken() {
//                        super.wrongToken();
//                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
//
//                    }
//
//                    @Override
//                    public void wrongBoxnumber() {
//                        super.wrongBoxnumber();
//                    }
//
//                    @Override
//                    public void onSuccess(final User user) {
//                        wifi_apple_list = new ArrayList<>();
//                        for (int i = 0; i < user.controllerList.size(); i++) {
//                            Map<String, String> mapdevice = new HashMap<>();
//                            mapdevice.put("name", user.controllerList.get(i).name);
//                            mapdevice.put("number", user.controllerList.get(i).number);
//                            mapdevice.put("type", user.controllerList.get(i).type);
//                            mapdevice.put("controllerId", user.controllerList.get(i).controllerId);
//                            wifi_apple_list.add(mapdevice);
//                        }
//
//                        if (wifi_apple_list.size() != 0)
//                            get_to_wifi(wifi_apple_list.get(wifi_apple_list.size() - 1).get("controllerId").toString());
//                    }
//                });
//    }
}
