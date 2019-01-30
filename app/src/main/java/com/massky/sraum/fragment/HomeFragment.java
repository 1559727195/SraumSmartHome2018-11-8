package com.massky.sraum.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.andview.refreshview.XRefreshView;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.ipcamera.demo.BridgeService;
import com.ipcamera.demo.PlayActivity;
import com.ipcamera.demo.utils.ContentCommon;
import com.ipcamera.demo.utils.SystemValue;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MusicUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.App;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.activity.AirControlActivity;
import com.massky.sraum.activity.CurtainWindowActivity;
import com.massky.sraum.activity.EditMyDeviceActivity;
import com.massky.sraum.activity.LaunghActivity;
import com.massky.sraum.activity.MainGateWayActivity;
import com.massky.sraum.activity.MessageActivity;
import com.massky.sraum.activity.MyDeviceItemActivity;
import com.massky.sraum.activity.SelectZigbeeDeviceActivity;
import com.massky.sraum.activity.SettingActivity;
import com.massky.sraum.activity.TVShowActivity;
import com.massky.sraum.activity.TiaoGuangLightActivity;
import com.massky.sraum.activity.WifiAirControlActivity;
import com.massky.sraum.adapter.AreaListAdapter;
import com.massky.sraum.adapter.DetailDeviceHomeAdapter;
import com.massky.sraum.adapter.HomeDeviceListAdapter;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;
import com.massky.sraum.view.ClearLengthEditText;
import com.massky.sraum.view.ListViewAdaptWidth;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.yaokan.sdk.ir.YKanHttpListener;
import com.yaokan.sdk.ir.YkanIRInterfaceImpl;
import com.yaokan.sdk.model.BaseResult;
import com.yaokan.sdk.model.KeyCode;
import com.yaokan.sdk.model.RemoteControl;
import com.yaokan.sdk.model.YKError;
import com.yaokan.sdk.utils.Logger;
import com.yaokan.sdk.utils.Utility;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.GizWifiCallBack;
import com.ypy.eventbus.EventBus;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import butterknife.InjectView;
import io.reactivex.internal.operators.single.SingleDelayWithCompletable;
import okhttp3.Call;
import vstc2.nativecaller.NativeCaller;

import static androidx.constraintlayout.widget.StateSet.TAG;
import static com.massky.sraum.Utils.ParceUtil.*;
import static com.massky.sraum.activity.MainGateWayActivity.MESSAGE_TONGZHI;

/**
 * Created by zhu on 2017/11/30.
 */

public class HomeFragment extends BaseFragment1 implements AdapterView.OnItemClickListener, BridgeService.IpcamClientInterface, BridgeService.CallBackMessageInterface {
    private PopupWindow popupWindow;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.area_name_txt)
    TextView area_name_txt;
    @InjectView(R.id.dragGridView)
    GridView mDragGridView;
    @InjectView(R.id.add_device)
    ImageView add_device;
    @InjectView(R.id.home_listview)
    ListView home_listview;
    private List<Map> list_homedev_items;
    private HomeDeviceListAdapter homeDeviceListAdapter;
    private DetailDeviceHomeAdapter deviceListAdapter;
    private View account_view;
    private DialogUtil dialogUtil;
    private LinearLayout common_setting_linear;
    private LinearLayout rename_scene_linear;
    private LinearLayout delete_scene_linear;
    private LinearLayout cancel_scene_linear;
    private ImageView common_setting_image;
    @InjectView(R.id.back_rel)
    RelativeLayout back_rel;
    @InjectView(R.id.refresh_view)
    XRefreshView refresh_view;
    private List<Map> roomsInfos = new ArrayList<>();
    private List<Map> singleRoomAssoList = new ArrayList<>();
    //    @InjectView(R.id.dragGridView)
//    GridView dragGridView;
    @InjectView(R.id.all_room_rel)
    RelativeLayout all_room_rel;
    @InjectView(R.id.area_rel)
    RelativeLayout area_rel;
    private String deivce_number;
    private String roomNumber = "";
    private String device_type = "";
    private List<Map> areaList = new ArrayList<>();
    private Map current_area_map = new HashMap();// select the area
    private List<Map> roomList = new ArrayList<>();//fang jian lie biao
    private List<Map> deviceList = new ArrayList<>();//单个房间设备列表信息
    private String deviceCount;//设备数目
    private String deivce_type;
    private String current_room_number;
    private List<Map<String, Object>> listob = new ArrayList<>();
    private List<String> listtype = new ArrayList();
    private String status;
    private Map mapdevice1 = new HashMap();
    //震动和音乐的判断值
    private boolean vibflag, musicflag;
    private String loginPhone;
    public static String ACTION_INTENT_RECEIVER_TO_SECOND_PAGE = "com.massky.secondpage.treceiver";
    public static final String MESSAGE_TONGZHI_DOOR_FIRST = "com.fragment.massky.message.tongzhi.door.first";
    public MessageReceiver mMessageReceiver;
    public static String ACTION_INTENT_RECEIVER = "com.massky.jr.treceiver";
    private String areaNumber;
    private Map mapdevice = new HashMap();
    public static final String MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE = "com.sraum.massky.from.mydevice";
    private WifiManager manager = null;
    private MyWifiThread myWifiThread = null;
    private static final int SEARCH_TIME = 3000;
    private int option = ContentCommon.INVALID_OPTION;
    private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
    private static final String STR_DID = "did";
    private static final String STR_MSG_PARAM = "msgparam";
    //    List<Map> list_wifi_camera = new ArrayList<>();
    private boolean playactivityfinsh = true;
    private String videofrom = "";
    public static String MACFRAGMENT_PM25 = "com.fragment.pm25";
    private Map video_item = new HashMap();//来自devicefragment
    private RemoteControl remoteControl;
    private GizWifiDevice mGizWifiDevice = null;
    private List<Map> list_remotecontrol_air = new ArrayList<>();
    private Map remoteControl_map_air = new HashMap();
    private int index_wifi;
    private boolean is_control_air;
    private String doit_wifi = "";
    private GizWifiDevice mGizWifiDevice1 = null;
    private List<Map> wifi_apple_list = new ArrayList<>();
    private int index_click;
    private String deviceInfo = "";
    private List<Map> list_hand_scene = new ArrayList<>();
    private boolean blagg = false;
    private Intent intentbrod = null;
    private WifiInfo info = null;
    private String type = "";
    private String strUser;
    private String strDID;
    private String strPwd;
    private int inde_video = 1;
    private List<Map> list = new ArrayList<>();

    /**
     * 小苹果绑定列表
     */
    private DeviceManager mDeviceManager;
    List<GizWifiDevice> wifiDevices = new ArrayList<GizWifiDevice>();
    private List<String> deviceNames = new ArrayList<String>();
    private YkanIRInterfaceImpl ykanInterface;
    private int index;
    private boolean again_connection;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            ToastUtil.showToast(getActivity(), "mggiz在线");
//            if (dialogUtil != null) dialogUtil.loadDialog();
            switch (msg.what) {
                case 0:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogUtil != null) {
                                dialogUtil.loadDialog();
                            } else {
                                dialogUtil = new DialogUtil(getActivity());
                                dialogUtil.loadDialog();
                            }
                        }
                    });
                    break;
                case 1:

                    break;
                case 2:
                    switch (doit_wifi) {
                        case "air":
                            test_air_control(mapdevice.get("panelMac").toString());
                            break;
                        case "tv":
                            test_tv(mapdevice.get("panelMac").toString());
                            break;
                    }
                    break;
                case 3:
                    if (dialogUtil != null) dialogUtil.removeDialog();
                    break;
                case 6://下载码库次数过于频繁，所以提示
                    index++;
                    if (index >= 2) {
                        dialogUtil.removeDialog();
                        index = 0;
                        return;
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (doit_wifi) {
                                case "air":
                                    test_air_control(mapdevice.get("panelMac").toString());
                                    break;
                                case "tv":
                                    test_tv(mapdevice.get("panelMac").toString());
                                    break;
                            }
                        }
                    }, 10000);//两次下载遥控器码值时间间隔10s,
                    break;
                case 7://下载遥控器码没有反应
                    switch (doit_wifi) {
                        case "air":
                            test_air_control(mapdevice.get("panelMac").toString());
                            break;
                        case "tv":
                            test_tv(mapdevice.get("panelMac").toString());
                            break;
                    }
                    break;
                case 10://wifi摄像头已经在线了
                    switch (videofrom) {//      videofrom = "macfragment";
                        case "macfragment":
                            mac_fragment_video_ok();
                            break;
                        case "devicefragment"://直接跳转到devicefragmentActivity
                            tongzhi_to_video("1");
                            break;
                    }
                    break;
                case 11://连接失败，在去连
                    switch (videofrom) {
                        case "macfragment":
                            onitem_wifi_shexiangtou(mapdevice);
                            break;
                        case "devicefragment":
                            common_video(video_item);
                            break;
                    }
                    break;
            }
        }
    };
    private int intfirst_time;
    private MyBroadCast receiver;
    private Dialog dialog;
    private String authType;//（1 业主 2 成员）
    private String device_name;
    private String device_gatewayNumber;
    private String device_name1;
    private String device_name2;
    private String qiehuan;

    /**
     * 发送给MyDeviceItemActivity视频的状态，获取摄像头状态，并返回
     */
    private void tongzhi_to_video(String status) {
        MyEvent event = new MyEvent();
        event.setMsg(status);
//...设置event
        EventBus.getDefault().post(event);
    }

    /**
     * macfragment video ok
     */
    private void mac_fragment_video_ok() {
        String content = stringbuffer.toString();
        String[] splits = content.split(",");
        if (splits.length == 3) {
            if (splits[0].equals("未知状态") && splits[1].equals("正在连接") && splits[2].equals("在线")) {
                stringbuffer = new StringBuffer();
//                            AppManager.getAppManager().finishActivity_current(PlayActivity.class);
                Intent intent = new Intent("play_finish");
                getActivity().sendBroadcast(intent);
                again_connection = true;
            }
        } else {
            if (!playactivityfinsh) {
//                        AppManager.getAppManager().finishActivity_current(PlayActivity.class);

                SystemValue.deviceName = strUser;
                SystemValue.deviceId = strDID;
                SystemValue.devicePass = strPwd;
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onData() {
        share_getData();
        init_device_onclick();//监听首页设备点击事件
        room_list_show_adapter();
        device_list_show_adapter();
    }

    private void share_getData() {
        init_music_flag();
        //成员，业主accountType->addrelative_id
        String accountType = (String) SharedPreferencesUtil.getData(getActivity(), "accountType", "");
        switch (accountType) {
            case "1":
                add_device.setVisibility(View.VISIBLE);
                break;//业主
            case "2":
                add_device.setVisibility(View.GONE);
                break;//家庭成员
        }
    }

    private void init_music_flag() {
        loginPhone = (String) SharedPreferencesUtil.getData(getActivity(), "loginPhone", "");
        SharedPreferences preferences = getActivity().getSharedPreferences("sraum" + loginPhone,
                Context.MODE_PRIVATE);
        vibflag = preferences.getBoolean("vibflag", false);
//        musicflag = preferences.getBoolean("musicflag", false);
        musicflag = (boolean) SharedPreferencesUtil.getData(getActivity(),"musicflag",false);
    }

    /**
     * 侧栏房间列数据显示
     */
    private void room_list_show_adapter() {
        roomList = new ArrayList<>();
        homeDeviceListAdapter = new HomeDeviceListAdapter(getActivity(), roomList, new HomeDeviceListAdapter.HomeDeviceItemClickListener() {
            @Override
            public void homedeviceClick(String number) {//获取单个房间关联信息（APP->网关）
                sraum_getOneRoomInfo(number);
            }
        });
        home_listview.setAdapter(homeDeviceListAdapter);//设备侧栏列表
        home_listview.setOnItemClickListener(this);
    }


    /**
     * 具体房间下的设备列表显示
     */
    private void device_list_show_adapter() {
        roomList = new ArrayList<>();
        deviceListAdapter = new DetailDeviceHomeAdapter(getActivity(), deviceList);
        mDragGridView.setAdapter(deviceListAdapter);//设备侧栏列表
        home_listview.setOnItemClickListener(this);
    }

    /**
     * 初始化设备点击事件
     */
    private void init_device_onclick() {
        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> mapalldevice = new HashMap<String, Object>();
                listob = new ArrayList<Map<String, Object>>();
                if (listtype.get(position).equals("1")) {
                    status = "0";
                } else {
                    status = "1";
                }
                Map<String, Object> mapdevice1 = new HashMap<String, Object>();
                mapdevice1.put("type", list.get(position).get("type").toString());
                mapdevice1.put("number", list.get(position).get("number").toString());
                switch (list.get(position).get("type").toString()) {
                    case "11":
                        mapdevice1.put("status", "0");
                        break;
                    case "15":
//            case "17":
                        mapdevice1.put("status", "1");
                        break;
                    default:
                        mapdevice1.put("status", status);
                        break;
                }

                mapdevice1.put("dimmer", list.get(position).get("dimmer").toString());
                mapdevice1.put("mode", list.get(position).get("mode").toString());
                mapdevice1.put("temperature", list.get(position).get("temperature").toString());
                mapdevice1.put("speed", list.get(position).get("speed").toString());
                mapdevice1.put("mac", list.get(position).get("mac") == null ? "" :
                        list.get(position).get("mac").toString());
                mapdevice1.put("deviceId", list.get(position).get("deviceId") == null ? "" :
                        list.get(position).get("deviceId").toString());
                mapdevice1.put("name", list.get(position).get("name").toString());
                mapdevice1.put("panelName", list.get(position).get("panelName") == null ? "" :
                        list.get(position).get("panelName").toString());
                mapdevice1.put("panelMac", list.get(position).get("panelMac") == null ? "" :
                        list.get(position).get("panelMac").toString());

                listob.add(mapdevice1);
                mapalldevice.put("token", TokenUtil.getToken(getActivity()));
                mapalldevice.put("boxNumber", TokenUtil.getBoxnumber(getActivity()));
                mapalldevice.put("deviceInfo", listob);
                String name = "";

                switch (list.get(position).get("type").toString()) {
                    case "7":
                        name = "门磁";
                        ToastUtil.showToast(getActivity(),
                                name + "无控制功能");
                        break;
                    case "8":
                        name = "人体感应";
                        ToastUtil.showToast(getActivity(),
                                name + "无控制功能");
                        break;
                    case "9":
                        name = "水浸检测器";
                        ToastUtil.showToast(getActivity(),
                                name + "无控制功能");
                        break;
                    case "10":
                        name = "PM2.5";
//                ToastUtil.showToast(getActivity(),
//                        name + "无控制功能");
                        break;
//            case "11":
//                name = "紧急按钮";
//                ToastUtil.showToast(getActivity(),
//                        name + "无控制功能");
//                break;
                    case "12":
                        name = "久坐报警器";
                        ToastUtil.showToast(getActivity(),
                                name + "无控制功能");
                        break;
                    case "13":
                        name = "烟雾报警器";
                        ToastUtil.showToast(getActivity(),
                                name + "无控制功能");
                        break;
                    case "14":
                        name = "天然气报警器";
                        ToastUtil.showToast(getActivity(),
                                name + "无控制功能");
                        break;
//            case "15":
//                name = "智能门锁";
//                ToastUtil.showToast(getActivity(),
//                        name + "无控制功能");
//                break;
//            case "16":
//                name = "直流电阀机械手";
//                ToastUtil.showToast(getActivity(),
//                        name + "无控制功能");
//                break;

//            case "17":
//                name = "86插座一位";
//                ToastUtil.showToast(getActivity(),name + "无控制功能");
//                break;
                    //special_type_device(mapdevice);
//                test_pm25();
                    // test_tv();
                    //test_air_control();
//                water_sensor();
                }

                switch (list.get(position).get("type").toString()) {
                    case "7":
                    case "8":
                    case "9":
//            case "10":
//            case "11":
                    case "12":
                    case "13":
                    case "14":
//            case "15":
//            case "16":
                        break;
                    case "202"://电视机
                        mapdevice = mapdevice1;
                        test_tv(mapdevice.get("panelMac").toString());
                        break;
                    case "10":
                        mapdevice = mapdevice1;
                        test_pm25();
                        break;
                    case "206"://WIFI空调
                        mapdevice = mapdevice1;
                        test_air_control(mapdevice1.get("panelMac").toString());
                        //special_type_device(mapdevice);
//                test_pm25();
                        // test_tv();
                        //test_air_control();
//                water_sensor();
                        break;
                    case "101"://wifi摄像头
                    case "103":
//                map.put("token", TokenUtil.getToken(AddWifiCameraScucessActivity.this));
//                map.put("type", "AA03");
//                map.put("name", name);
//                map.put("mac", wificamera.get("strMac"));
//                map.put("controllerId", wificamera.get("strDeviceID"));
//                map.put("user", wificamera.get("strName"));
//                map.put("password", "888888");
//                map.put("wifi", wificamera.get("wifi"));
                        mapdevice = mapdevice1;
                        videofrom = "macfragment";
                        onitem_wifi_shexiangtou(mapdevice);
                        break;
                    default:
                        curtains_and_light(position, mapdevice1);
                        break;
                }
            }
        });


        mDragGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.size() > 0) {
                    deivce_number = (String) list.get(position).get("number");//设备编号
                    deivce_type = (String) list.get(position).get("type");//设备编号
                    device_name = (String) list.get(position).get("name");
                    device_gatewayNumber = (String) list.get(position).get("boxNumber");
                    switch (deivce_type) {
                        case "4":
                            device_name1 = (String) list.get(position).get("name1");
                            device_name2 = (String) list.get(position).get("name2");
                            break;
                    }

                    //这句是选中某个设备
                    switch (roomNumber) {
                        case "":
//                        common_setting_linear.setVisibility(View.GONE);
                            break;
                        default:
                            common_setting_linear.setVisibility(View.VISIBLE);
                            break;
                    }
                    dialogUtil.loadViewBottomdialog();
                }
                return true;
            }
        });
    }


    /**
     * 测试调光灯
     */
    private void test_tiaoguanglight() {
        Intent intent_tiaoguang = new Intent(getActivity(), TiaoGuangLightActivity.class);
        startActivity(intent_tiaoguang);
    }


    /**
     * 测试空调
     */
    private void test_air_control() {
        Intent intent_air_control = new Intent(getActivity(), AirControlActivity.class);
        startActivity(intent_air_control);
    }


    /**
     * 测试窗帘控制
     */
    private void test_control_curtain() {
        Intent intent_air_control = new Intent(getActivity(), CurtainWindowActivity.class);
        startActivity(intent_air_control);
    }

    /**
     * 测试数据
     */
    private void test_device_data() {
        list_homedev_items = new ArrayList<>();

        Map itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", "1");
        itemHashMap.put("name", "常用");
        itemHashMap.put("type", "0");
        list_homedev_items.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", "2");
        itemHashMap.put("name", "南卧室");
        itemHashMap.put("type", "0");
        list_homedev_items.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", "3");
        itemHashMap.put("name", "客厅");
        itemHashMap.put("type", "0");
        list_homedev_items.add(itemHashMap);
    }

    /**
     * 底部弹出拍照，相册弹出框
     */
    private void addViewid() {
        account_view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_bottom_scene, null);
        common_setting_linear = (LinearLayout) account_view.findViewById(R.id.common_setting_linear);
        rename_scene_linear = (LinearLayout) account_view.findViewById(R.id.rename_scene_linear);
        delete_scene_linear = (LinearLayout) account_view.findViewById(R.id.delete_scene_linear);
        cancel_scene_linear = (LinearLayout) account_view.findViewById(R.id.cancel_scene_linear);
        common_setting_linear.setOnClickListener(this);
        rename_scene_linear.setOnClickListener(this);
        delete_scene_linear.setOnClickListener(this);
        cancel_scene_linear.setOnClickListener(this);
        //common_setting_image
        common_setting_image = (ImageView) account_view.findViewById(R.id.common_setting_image);
        dialogUtil = new DialogUtil(getActivity(), account_view, 2);
    }

    @Override
    protected void onEvent() {
        addViewid();//底部弹出拍照，相册弹出框
        add_device.setOnClickListener(this);
        back_rel.setOnClickListener(this);
        area_rel.setOnClickListener(this);
        refresh_view.setScrollBackDuration(300);
        refresh_view.setPinnedTime(1000);
        refresh_view.setPullLoadEnable(false);
        refresh_view.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                refresh_view.stopRefresh();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
            }
        });
    }

    /*--------------------------------切换房间选项卡-----------------------------*/

    public void switch_room_dialog(final Context context) {

//        dialogUtil.loadDialog();
        View view = LayoutInflater.from(context).inflate(R.layout.credit_card_num_pop, null);
        final RelativeLayout xiala_shuaka_rel = (RelativeLayout) view.findViewById(R.id.xiala_shuaka_rel);
        final ImageView btn_xiala_img = (ImageView) view.findViewById(R.id.btn_xiala_img);
        final TextView xiala_shuaka_txt = (TextView) view.findViewById(R.id.xiala_shuaka_txt);
        Button forget_shuaka_btn = (Button) view.findViewById(R.id.forget_shuaka_btn);
        Button confirm_shuaka_btn = (Button) view.findViewById(R.id.confirm_shuaka_btn);
        final ListView room_select = (ListView) view.findViewById(R.id.room_select);

        final List<String> roomNames = new ArrayList<>();

        for (int i = 0; i < roomList.size(); i++) {
            roomNames.add((String) roomList.get(i).get("name"));
        }

        room_select.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_expandable_list_item_new, roomNames));
        room_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modify_correl_infor(position, roomNames);
            }
        });

        xiala_shuaka_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopwindow_room(xiala_shuaka_rel, xiala_shuaka_txt);
            }
        });
        //// ImageView btn_xiala_img;

        //显示数据
        dialog = new Dialog(getActivity());
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.7); //宽度设置为屏幕的0.5
        if (roomNames.size() >= 6)
            p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
//        dialogUtil.loadDialog();
        forget_shuaka_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm_shuaka_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //充电绑定提交
                //xiala_shuaka_txt->cardTime 刷卡次数
//                charging_binding_submission(dialog, xiala_shuaka_txt.getText().toString(), driect);
            }
        });
    }

    /**
     * 修改房间关联信息（APP->网关）
     *
     * @param position
     * @param roomNames
     */
    private void modify_correl_infor(int position, final List<String> roomNames) {
        String name = roomNames.get(position);//新的房间名称
        //roomNumber = "";
        //将当前房间下的设备放到其他房间下
        //拿到房间编号，拿到设备编号
        for (int i = 0; i < roomList.size(); i++) {
            if (name.equals(roomList.get(i).get("name"))) {
                roomNumber = (String) roomList.get(i).get("number");
                break;
            }
        }


        if (current_room_number.equals(roomNumber)) {
            ToastUtil.showToast(getActivity(), "请移动到别的房间");
            return;
        }
        //deivce_number,roomNumber;
        //修改房间关联信息（APP->网关）
        Map map = new HashMap();// current_room_number
        map.put("token", TokenUtil.getToken(getActivity()));
        map.put("areaNumber", areaNumber);
        map.put("roomNumberNew", roomNumber);
        map.put("roomNumberOld", current_room_number);
        map.put("number", deivce_number);
        map.put("type", deivce_type);


//        map.put("number", deivce_number);
        MyOkHttp.postMapObject(ApiHelper.sraum_updateRoomInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, getActivity(), null) {
                    @Override
                    public void onSuccess(User user) {
//                        project_select.setText(user.name);
                        if (dialog != null)
                            dialog.dismiss();
                        if (dialogUtil != null) dialogUtil.removeviewBottomDialog();
                        //刷新某个房间下的所有设备
                        switch (current_room_number) {
                            case "":
//                                sraum_getInfos(false, device_type);//获取全部信息（APP->网关）
                                break;
                            default:
//                                sraum_getOneRoomInfo(current_room_number);
                                qiehuan = "";
                                sraum_getRoomsInfo(areaNumber, qiehuan);
                                break;
                        }
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                        ToastUtil.showToast(getActivity(), "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(getActivity(), "roomNumberNew 不存在");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                        ToastUtil.showToast(getActivity(), "number 不存在");
                    }
                });
    }

    @Override
    public void onEvent(MyDialogEvent eventData) {

    }

    @Override
    protected int viewId() {
        return R.layout.first_page_act;
    }

    @Override
    protected void onView(View view) {
        registerMessageReceiver();
        StatusUtils.setFullToStatusBar(getActivity());  // StatusBar.
//        room_list_show_adapter();
        intfirst_time = 1;
        init_first_sraum();
    }

    @Override
    public void onResume() {//视图可见后，去加载接口数据
        super.onResume();
        areaList = SharedPreferencesUtil.getInfo_List(getActivity(), "areaList");
        init_music_flag();
        show_old_areaList();
        show_old_roomList();
        show_old_deviceList();
        if (areaList.size() != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    com_from_laungh();
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sraum_getAllArea();
                }
            }).start();
        }
        blagg = true;
        mDeviceManager.setGizWifiCallBack(mGizWifiCallBack);
        update(mDeviceManager.getCanUseGizWifiDevice());
        new Thread(new Runnable() {
            @Override
            public void run() {
                getOtherDevices();
            }
        }).start();
    }

    private void show_old_roomList() {
        roomList = SharedPreferencesUtil.getInfo_List(getActivity(), "roomList_old");
        if (roomList.size() != 0) {
            display_room_list(0);
        }
    }

    /**
     * 显示历史区域
     */
    private void show_old_areaList() {
        areaList = SharedPreferencesUtil.getInfo_List(getActivity(), "areaList_old");
        if (areaList.size() != 0) {
            for (Map map : areaList) {
                if ("1".equals(map.get("sign").toString())) {
                    current_area_map = map;
                    handler_laungh.sendEmptyMessage(0);
                    areaNumber = current_area_map.get("number").toString();
                    authType = current_area_map.get("authType").toString();//（1 业主 2 成员）
                    SharedPreferencesUtil.saveData(getActivity(), "areaNumber", areaNumber);
                    SharedPreferencesUtil.saveData(getActivity(), "authType", authType);
                    handler_laungh.sendEmptyMessage(1);
                    break;
                }
            }
        }
    }

    /**
     * 首页显示历史设备数据
     */
    private void show_old_deviceList() {
        boolean isnewProcess = (boolean) SharedPreferencesUtil.getData(getActivity(), "newProcess", false);
        if (isnewProcess) {//新进程下首页默认填充设备历史数据（历史数据只供显示填补空白）（待完成）；
            SharedPreferencesUtil.saveData(getActivity(), "newProcess", false);
            list = SharedPreferencesUtil.getInfo_List(getActivity(), "list_old");
            listtype.clear();
            if (list.size() != 0) {
                for (Map map : list) {
                    listtype.add(map.get("status") == null ? "1" :
                            map.get("status").toString());
                }
                //展示首页设备列表
                handler_laungh.sendEmptyMessage(5);
            }
        }
    }

    Handler handler_laungh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (current_area_map != null)
                        switch (current_area_map.get("authType").toString()) {
                            case "1":
                                area_name_txt.setText(current_area_map.get("name").toString());
                                break;
                            case "2":
                                area_name_txt.setText(current_area_map.get("name").toString());
                                break;
                        }
                    break;
                case 1:
                    switch (authType) {//（1 业主 2 成员）
                        case "1":
                            add_device.setVisibility(View.VISIBLE);
                            break;
                        case "2":
                            add_device.setVisibility(View.GONE);
                            break;
                    }
                    break;
                case 3:
                    common_room_show();
                    break;

                case 4:
                    //加载默认房间下的设备列表
                    switch (qiehuan) {
                        case "qiehuan"://区域切换
                            current_room_number = roomList.get(0).get("number").toString();
                            sraum_getOneRoomInfo(roomList.get(0).get("number").toString());
                            display_room_list(0);
                            break;
                        default:
                            common_room_show();
                            break;
                    }
                    break;
                case 5:
                    display_home_device_list();
                    break;
                case 6:
                    //authType : 1 （1 业主 2 成员）
                    switch (authType) {
                        case "1":
                            add_device.setVisibility(View.VISIBLE);
                            break;
                        case "2":
                            add_device.setVisibility(View.GONE);
                            break;
                    }
                    switch (current_area_map.get("authType").toString()) {
                        case "1":
                            area_name_txt.setText(current_area_map.get("name").toString());
                            break;
                        case "2":
                            area_name_txt.setText(current_area_map.get("name").toString());
                            break;
                    }
                    qiehuan = "qiehuan";
                    sraum_getRoomsInfo(areaNumber, qiehuan);
                    if (popupWindow != null)
                        popupWindow.dismiss();
                    break;
            }
        }
    };

    private void common_room_show() {
        if (roomList.size() != 0) {
            list = SharedPreferencesUtil.getInfo_List(getActivity(), "list");
            listtype.clear();
            if (current_room_number != null) {
                common_device_list();
                for (int i = 0; i < roomList.size(); i++) {
                    if (roomList.get(i).get("number").toString().equals(current_room_number)) {
                        display_room_list(i);
                        break;
                    }
                }
            } else {
                current_room_number = roomList.get(0).get("number").toString();
                common_device_list();
                display_room_list(0);
            }
        }
    }

    /**
     * 显示房间下的设备列表
     */
    private void common_device_list() {
        if (list.size() != 0) {
            for (Map map : list) {
                listtype.add(map.get("status") == null ? "1" :
                        map.get("status").toString());
            }
            //展示首页设备列表
            handler_laungh.sendEmptyMessage(5);
        } else {
            sraum_getOneRoomInfo(current_room_number);
        }
    }

    private void com_from_laungh() {
        for (Map map : areaList) {
            if ("1".equals(map.get("sign").toString())) {
                current_area_map = map;
                handler_laungh.sendEmptyMessage(0);
                areaNumber = current_area_map.get("number").toString();
                authType = current_area_map.get("authType").toString();//（1 业主 2 成员）
                SharedPreferencesUtil.saveData(getActivity(), "areaNumber", areaNumber);
                SharedPreferencesUtil.saveData(getActivity(), "authType", authType);
                handler_laungh.sendEmptyMessage(1);

                roomList = SharedPreferencesUtil.getInfo_List(getActivity(), "roomList");
                if (roomList.size() != 0) {
                    handler_laungh.sendEmptyMessage(3);
                } else {
                    qiehuan = "";
                    sraum_getRoomsInfo(current_area_map.get("number").toString(), qiehuan);
                }
                break;
            }
        }
        SharedPreferencesUtil.saveInfo_List(getActivity(), "areaList", new ArrayList<Map>());
        SharedPreferencesUtil.saveInfo_List(getActivity(), "roomList", new ArrayList<Map>());
        SharedPreferencesUtil.saveInfo_List(getActivity(), "list", new ArrayList<Map>());
    }

    private void init_first_sraum() {
        EventBus.getDefault().register(this);
        init_wifi_camera();
        registerMessageReceiver();
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getActivity().getApplicationContext());
        // 遥控云数据接口分装对象对象
        ykanInterface = new YkanIRInterfaceImpl(getActivity().getApplicationContext());
        /**
         * 小苹果初始化
         */
        mDeviceManager = DeviceManager
                .instanceDeviceManager(getActivity().getApplicationContext());
//        get_wifidevice(0);
    }

    Runnable updateThread = new Runnable() {

        public void run() {
            NativeCaller.StopSearch();
            Message msg = updateListHandler.obtainMessage();
            msg.what = 1;
            updateListHandler.sendMessage(msg);
        }
    };

    Handler updateListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

            } else {

            }
        }
    };

    public static String int2ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    private void startSearch() {
//        listAdapter.ClearAll();
//        progressdlg.setMessage(getString(R.string.searching_tip));
//        progressdlg.show();
        new Thread(new SearchThread()).start();
        updateListHandler.postDelayed(updateThread, SEARCH_TIME);
    }

    private class SearchThread implements Runnable {
        @Override
        public void run() {
            Log.d("tag", "startSearch");
            NativeCaller.StartSearch();
        }
    }

    class MyTimerTask extends TimerTask {

        public void run() {
            updateListHandler.sendEmptyMessage(100000);
        }
    }

    ;

    class MyWifiThread extends Thread {
        @Override
        public void run() {
            while (blagg == true) {
                super.run();

                updateListHandler.sendEmptyMessage(100000);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    class StartPPPPThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
                startCameraPPPP();
            } catch (Exception e) {

            }
        }
    }

    private class MyBroadCast extends BroadcastReceiver {


        @Override
        public void onReceive(Context arg0, Intent arg1) {

//            AddCameraActivity.this.finish();
            Log.d("ip", "AddCameraActivity.this.finish()");
            if (arg1.getAction().equals("finish")) {
                playactivityfinsh = true;
                if (again_connection) {
                    Intent intent_new = new Intent(getActivity(), PlayActivity.class);
                    startActivity(intent_new);
                    again_connection = false;
                }
            }
        }
    }


    private void startCameraPPPP() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {

        }

        if (SystemValue.deviceId.toLowerCase().startsWith("vsta")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstd")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXSXIALKHYEIEJHUASLMHWEESUEKAUIHPHSWAOSTEMENSQPDLRLNPAPEPGEPERIBLQLKHXELEHHULOEGIAEEHYEIEK-$$", 1);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstf")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXEJIALKHYATPCHULNSVLMEELSHWIHPFIBAOHXIDICSQEHENEKPAARSTELERPDLNEPLKEILPHUHXHZEJEEEHEGEM-$$", 1);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vste")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBAKKIOGNJHEGHMFEEDGLNOHJMPHAFPBEDLADILKEKPDLBDDNPOHKKCIFKJBNNNKLCPPPNDBFDL", 0);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstg")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBOKCIGGFJPECHIFNEBGJNLHOMIHEFJBADPAGJELNKJDKANCBPJGHLAIALAADMDKPDGOENEBECCIK:vstarcam2018", 0);
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstb") || SystemValue.deviceId.toLowerCase().startsWith("vstc")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL", 0);
        } else {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "", 0);
        }
        //int result = NativeCaller.StartPPPP(SystemValue.deviceId, SystemValue.deviceName,
        //		SystemValue.devicePass,1,"");
        //Log.i("ip", "result:"+result);
    }

    private void stopCameraPPPP() {
        NativeCaller.StopPPPP(SystemValue.deviceId);
    }

    private void init_wifi_camera() {
//        BridgeService.setAddCameraInterface(this);
        BridgeService.setCallBackMessage(this);
        receiver = new MyBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        getActivity().registerReceiver(receiver, filter);
        intentbrod = new Intent("drop");
    }


    private List<Map> list_mac_wifi = new ArrayList<>();
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

                Map map = new HashMap();
                map.put("mac", device.getMacAddress());
                String str = object2String(device);
//                mGizWifiDevice1 = ParceUtil.unmarshall(str, GizWifiDevice.CREATOR);
                map.put("code", str);
                list_mac_wifi.add(map);
                SharedPreferencesUtil.saveInfo_List(getActivity(), "list_mac_wifi", list_mac_wifi);
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
//            ToastUtil.showToast(getActivity(),"wifiDevices.size():" + wifiDevices.size());
        }
//        adapter.notifyDataSetChanged();
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


    private final String PROCESS_NAME = "com.massky.sraum";//进程名称

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public boolean isAppMainProcess() {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(App.getInstance(), pid);
            if (TextUtils.isEmpty(process)) {
                //第一次创建,系统中还不存在该process,所以一定是主进程
                return true;
            } else if (PROCESS_NAME.equalsIgnoreCase(process)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 根据Pid得到进程名
     */
    public String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                //主进程的pid是否和当前的pid相同,若相同,则对应的包名就是app的包名
                return processInfo.processName;
            }
        }
        return "";
    }


    /**
     * 摄像头click
     *
     * @param mapdevice
     */
    private void onitem_wifi_shexiangtou(Map<String, Object> mapdevice) {
        playactivityfinsh = false;
        again_connection = false;
        this.mapdevice = mapdevice;
        common_video(mapdevice);
    }

    /**
     * 共同的视频
     *
     * @param mapdevice
     */
    private void common_video(Map<String, Object> mapdevice) {
        List<Map> list = SharedPreferencesUtil.getInfo_List(getActivity(), "list_wifi_camera_first");
        int tag = 0;
        for (int i = 0; i < list.size(); i++) {
            if (mapdevice.get("dimmer").toString()
                    .equals(list.get(i).get("did"))) {
                tag = (int) list.get(i).get("tag");
            }
        }

        if (tag == 1) {

//            in.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//            in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
//            in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
//            String strUser, String strPwd, String strDID

            strUser = mapdevice.get("mode").toString();
            strDID = mapdevice.get("dimmer").toString();
            strPwd = mapdevice.get("temperature").toString();
            handler.sendEmptyMessage(10);//设备已经在线了
//            Toast.makeText(getActivity(), "设备已经是在线状态了", Toast.LENGTH_SHORT).show();
        } else if (tag == 2) {

            switch (videofrom) {//      videofrom = "macfragment";
                case "macfragment":
                    Toast.makeText(getActivity(), "设备不在线", Toast.LENGTH_SHORT).show();
                    break;
                case "devicefragment"://直接跳转到devicefragmentActivity-》设备已经断线
                    tongzhi_to_video("0");
                    break;
            }
        } else {
            done(mapdevice.get("mode").toString()
                    , mapdevice.get("temperature").toString()
                    , mapdevice.get("dimmer").toString());//String strUser,String strPwd,String strDID
        }
    }

    private void done(String strUser, String strPwd, String strDID) {
        Intent in = new Intent();
//        String strUser = userEdit.getText().toString();
//        String strPwd = pwdEdit.getText().toString();
//        String strDID = didEdit.getText().toString();

        if (strDID.length() == 0) {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.input_camera_id), Toast.LENGTH_SHORT).show();
            return;
        } //

        if (strUser.length() == 0) {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.input_camera_user), Toast.LENGTH_SHORT).show();
            return;
        }

        if (option == ContentCommon.INVALID_OPTION) {
            option = ContentCommon.ADD_CAMERA;
        }

        in.putExtra(ContentCommon.CAMERA_OPTION, option);
        in.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
        in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
        in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
        in.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType);
//        progressBar.setVisibility(View.VISIBLE);
        if (dialogUtil != null)
            dialogUtil.loadDialog();
        this.strDID = strDID;
        this.strPwd = strPwd;
        this.strUser = strUser;
        SystemValue.deviceName = strUser;
        SystemValue.deviceId = strDID;
        SystemValue.devicePass = strPwd;
        BridgeService.setIpcamClientInterface(this);
        NativeCaller.Init();
        new Thread(new StartPPPPThread()).start();
    }


    /**
     * 测试空调
     */
    private void test_air_control(String mac) {
        doit_wifi = "air";
        common_control("air", mac);
    }

    /**
     * 共同控制
     */
    private void common_control(String doit, String mac) {
        String apple_name = "";
        for (int i = 0; i < list_hand_scene.size(); i++) {
            if (list_hand_scene.get(i).get("controllerId").equals(mac)) {
                apple_name = list_hand_scene.get(i).get("name").toString();

            }
        }
        if (wifiDevices.size() != 0) {
            get_to_wifi(mac, apple_name);//绑定订阅
            to_control(doit);
        } else {
            ToastUtil.showToast(getActivity(), "请与" + apple_name
                    +
                    "在同一网络后再控制");
        }
    }

    /**
     * 获取门磁等第三方设备
     */
    private void getOtherDevices() { // ----
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (dialogUtil != null) {
//                    dialogUtil.loadDialog();
//                }
//            }
//        });

        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
        mapdevice.put("areaNumber", areaNumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleInfos
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        getOtherDevices();
                    }
                }, getActivity(), null) {
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
                    }
                });
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
            ToastUtil.showToast(getActivity(), "请与" + apple_name
                    +
                    "在同一网络后再控制");
            return;
        }
    }

    /**
     * 跳转到空调控制界面
     */
    private void to_control(String doit) {
        String mac = mapdevice.get("panelMac").toString();
        String rid = mapdevice.get("dimmer").toString();
        boolean issame = false;
        list_remotecontrol_air = SharedPreferencesUtil.getInfo_List(getActivity(), "remoteairlist");
        for (int i = 0; i < list_remotecontrol_air.size(); i++) {
            if (rid.equals(list_remotecontrol_air.get(i).get("rid"))) {
                issame = true;
                remoteControl_map_air = list_remotecontrol_air.get(i);
                break;
            } else {
                issame = false;
            }
        }

        if (issame) {
            switch (doit) {
                case "air":
                    toControl(mapdevice);
                    break;
                case "tv":
                    toControl_TV(mapdevice);
                    break;
            }
        } else {
            switch (doit) {
                case "air":
                    new DownloadThread("getDetailByRCID", mac, rid).start();//下载该遥控器编码
                    break;
                case "tv":
                    new DownloadThread("getDetailByRCID_TV", mac, rid).start();//下载该遥控器编码
                    break;
            }
        }
    }

    /**
     * 跳转到控制界面
     */
    private void toControl(Map mapdevice) {
        if (mGizWifiDevice == null) {
//            ToastUtil.showToast(getActivity(), "设备，null");
            return;
        }
        Intent intent = new Intent(getActivity(), WifiAirControlActivity.class);
        intent.putExtra("GizWifiDevice", mGizWifiDevice);
        intent.putExtra("mapdevice", (Serializable) mapdevice);
        //remoteControl
//        intent.putExtra("remoteControl", remoteControl);
//        Map map_rcommand = remoteControl.getRcCommand();
        intent.putExtra("map_rcommand", (Serializable) remoteControl_map_air);
        startActivity(intent);
    }

    /**
     * 跳转到TV 控制界面
     */
    private void toControl_TV(Map mapdevice) {
        if (mGizWifiDevice == null) {

//            ToastUtil.showToast(getActivity(), "设备，null");
            return;
        }
        Intent intent = new Intent(getActivity(), TVShowActivity.class);
        intent.putExtra("GizWifiDevice", mGizWifiDevice);
        intent.putExtra("mapdevice", (Serializable) mapdevice);
        //remoteControl
//        intent.putExtra("remoteControl", remoteControl);
//        Map map_rcommand = remoteControl.getRcCommand();
        intent.putExtra("map_rcommand", (Serializable) remoteControl_map_air);
        startActivity(intent);
    }


    class DownloadThread extends Thread {
        private String viewId;
        String result = "";
        private String mac;
        private String rid;

        public DownloadThread(String viewId, String mac, String rid) {
            this.viewId = viewId;
            this.mac = mac;
            this.rid = rid;
        }

        @Override
        public void run() {

            final Message message = mHandler.obtainMessage();
            switch (viewId) {
                case "getDetailByRCID":
                    get_air_control_brand(message);
                    break;
                case "getDetailByRCID_TV":
                    get_air_control_brand_tv(message);
                    break;
                default:
                    break;
            }
        }

        /**
         * 获取空调编码
         *
         * @param message
         */
        private void get_air_control_brand(final Message message) {
            if (!mac.equals("")) {
                if (ykanInterface == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.loadDialog();
                    }
                });
                ykanInterface
                        .getRemoteDetails(mac, rid, new YKanHttpListener() {
                            @Override
                            public void onSuccess(BaseResult baseResult) {
                                if (baseResult != null) {
                                    add_remotes((RemoteControl) baseResult);
                                }
                            }

                            /**
                             * 添加遥控器
                             * @param baseResult
                             */
                            private void add_remotes(RemoteControl baseResult) {
                                index = 0;
                                remoteControl = baseResult;
                                result = remoteControl.toString();

                                //在这里保持遥控器红外码列表
                                HashMap<String, KeyCode> map = remoteControl.getRcCommand();
                                Map map_send = new HashMap();
                                map_send.put("mac", mac);
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
                                list_remotecontrol_air.add(map_send);
                                remoteControl_map_air = map_send;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferencesUtil.saveInfo_List(getActivity(), "remoteairlist", list_remotecontrol_air);
                                        dialogUtil.removeDialog();
                                        message.what = 1;
                                        message.obj = result;
                                        mHandler.sendMessage(message);
                                    }
                                });
                                //下载好遥控码后
                            }

                            @Override
                            public void onFail(final YKError ykError) {
                                Log.e(TAG, "ykError:" + ykError.toString());
                                handler.sendEmptyMessage(6);
                                result = "error";
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        dialogUtil.removeDialog();
//                                        ToastUtil.showToast(getActivity(),
//                                                ykError.getError());
                                    }
                                });
                            }
                        });
            } else {
                result = "请调用匹配数据接口";
                Log.e(TAG, " getDetailByRCID 没有遥控器设备对象列表");
            }
            Log.d(TAG, " getDetailByRCID result:" + result);
//            if (result.equals("")) {
//                handler.sendEmptyMessage(7);//
//            }
        }

        /**
         * 获取电视机遥控码
         *
         * @param message
         */
        private void get_air_control_brand_tv(final Message message) {
            if (!mac.equals("")) {
                if (ykanInterface == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.loadDialog();
                    }
                });
                ykanInterface
                        .getRemoteDetails(mac, rid, new YKanHttpListener() {
                            @Override
                            public void onSuccess(BaseResult baseResult) {
                                if (baseResult != null) {
                                    add_remotes((RemoteControl) baseResult);
                                }
                            }

                            /**
                             * 添加遥控器
                             * @param baseResult
                             */
                            private void add_remotes(RemoteControl baseResult) {
                                index = 0;
                                remoteControl = baseResult;
                                result = remoteControl.toString();
//                                            list_remotecontrol = new ArrayList<>();
                                //在这里保持遥控器红外码列表
                                HashMap<String, KeyCode> map = remoteControl.getRcCommand();
                                Map map_send = new HashMap();
                                map_send.put("mac", mac);
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
                                list_remotecontrol_air.add(map_send);
                                remoteControl_map_air = map_send;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferencesUtil.saveInfo_List(getActivity(), "remoteairlist", list_remotecontrol_air);
                                        dialogUtil.removeDialog();
                                        message.what = 2;
                                        message.obj = result;
                                        mHandler.sendMessage(message);
                                    }
                                });
                                //下载好遥控码后
                            }

                            @Override
                            public void onFail(final YKError ykError) {
                                Log.e(TAG, "ykError:" + ykError.toString());
                                handler.sendEmptyMessage(6);
                                result = "error";
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        dialogUtil.removeDialog();
//                                        ToastUtil.showToast(getActivity(),
//                                                ykError.getError());
                                    }
                                });
                            }
                        });
            } else {
                result = "请调用匹配数据接口";
                Log.e(TAG, " getDetailByRCID 没有遥控器设备对象列表");
            }
            Log.d(TAG, " getDetailByRCID result:" + result);
//            if (result.equals("")) {
//                handler.sendEmptyMessage(7);//
//            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
//                    if (!Utility.isEmpty(remoteControl)) {
//                        airVerSion = remoteControl.getVersion();
//                        codeDatas = remoteControl.getRcCommand();
//                        airEvent = getAirEvent(codeDatas);
//                    }
                    toControl(mapdevice);
                    break;
                case 2:
                    toControl_TV(mapdevice);
                    break;
            }
        }
    };


    /**
     * 测试电视
     */
    private void test_tv(String mac) {
        doit_wifi = "tv";
        common_control("tv", mac);
    }

    /**
     * 测试pm2.5
     */
    private void test_pm25() {
//        Intent intent = new Intent(getActivity(), Pm25SecondActivity.class);
//        intent.putExtra("mapdevice", (Serializable) this.mapdevice);
//        startActivity(intent);
    }

    /**
     * 门磁，水浸，人体感应，入墙PM2.5
     *
     * @param mapalldevice
     */
    private void special_type_device(Map<String, Object> mapalldevice) {
        Bundle bundle = new Bundle();
        bundle.putString("type", (String) mapalldevice.get("type"));
        bundle.putString("name", (String) mapalldevice.get("name"));
        bundle.putString("number", (String) mapalldevice.get("number"));
        bundle.putString("name1", (String) mapalldevice.get("name1"));
        bundle.putString("name2", (String) mapalldevice.get("name2"));
        bundle.putString("panelName", (String) mapalldevice.get("panelName"));
        bundle.putString("status", (String) mapalldevice.get("status"));
        bundle.putString("dimmer", (String) mapalldevice.get("dimmer"));
        bundle.putString("mode", (String) mapalldevice.get("mode"));
//        IntentUtil.startActivity(getActivity(), MacdetailActivity.class, bundle);
    }

    /**
     * curtains and light,窗帘与灯
     *
     * @param position
     * @param mapdevice1
     */
    private void curtains_and_light(int position, Map<String, Object> mapdevice1) {
        if (list.get(position).get("type").toString().equals("1") || list.get(position).get("type").toString().equals("11")
                || list.get(position).get("type").toString().equals("16")
                || list.get(position).get("type").toString().equals("15")
                || list.get(position).get("type").toString().equals("17")
                || list.get(position).get("type").toString().equals("100")) {
            switch (list.get(position).get("type").toString()) {
                case "100"://手动场景控制
                    sraum_manualSceneControl(list.get(position).get("number").toString());
                    break;
                default:
                    sraum_device_control(position, mapdevice1);
                    break;
            }
        } else {
            /*窗帘所需要的属性值*/
            Log.e("zhu", "chuanglian:" + "窗帘所需要的属性值");
            Bundle bundle = new Bundle();
            bundle.putString("type", list.get(position).get("type").toString());
            bundle.putString("number", list.get(position).get("number").toString());
            bundle.putString("name1", list.get(position).get("name1").toString());
            bundle.putString("name2", list.get(position).get("name2").toString());
            bundle.putString("name", list.get(position).get("name").toString());
            bundle.putString("status", list.get(position).get("status").toString());
            bundle.putString("areaNumber", areaNumber);
            bundle.putString("roomNumber", current_room_number);
            bundle.putSerializable("mapalldevice", (Serializable) mapdevice1);
            LogUtil.eLength("名字", list.get(position).get("name1").toString() + list.get(position).get("name2").toString());
            switch (list.get(position).get("type").toString()) {
                case "2"://调光灯
                    IntentUtil.startActivity(getActivity(), TiaoGuangLightActivity.class, bundle);
                    break;
                case "4"://窗帘
                case "18":
                    IntentUtil.startActivity(getActivity(), CurtainWindowActivity.class, bundle);
                    break;
                case "3"://空调
                case "5"://新风-不含模式
                case "6"://地暖-不含模式
                    IntentUtil.startActivity(getActivity(), AirControlActivity.class, bundle);
                    break;
            }
        }
    }

    /**
     * 控制手动场景
     */
    private void sraum_manualSceneControl(final String sceneId) {
        Map map = new HashMap();
        map.put("token", TokenUtil.getToken(getActivity()));
        String areaNumber = (String) SharedPreferencesUtil.getData(getActivity(), "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("sceneId", sceneId);
        MyOkHttp.postMapObject(ApiHelper.sraum_manualSceneControl, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_manualSceneControl(sceneId);
            }
        }, getActivity(), dialogUtil) {
            @Override
            public void fourCode() {
                super.fourCode();
                ToastUtil.showToast(getActivity(), "控制失败");
            }

            @Override
            public void threeCode() {
                super.threeCode();
                ToastUtil.showToast(getActivity(), "sceneId 错误");
            }


            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                ToastUtil.showToast(getActivity(), "操作成功");
                if (vibflag) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                }

                if (musicflag) {
                    LogUtil.i("铃声响起");
                    MusicUtil.startMusic(getActivity(), 1, "");
                } else {
                    MusicUtil.stopMusic(getActivity(), "");
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }


    /**
     * sraum_device_control
     *
     * @param
     * @param mapdevice1
     */
    private void sraum_device_control(final int position, final Map<String, Object> mapdevice1) {
        Map<String, Object> mapalldevice = new HashMap<>();
        List<Map> listobj = new ArrayList<>();
        Map map = new HashMap();
        map.put("type", mapdevice1.get("type").toString());
        map.put("number", mapdevice1.get("number").toString());
        map.put("name", mapdevice1.get("name").toString());
        map.put("status", mapdevice1.get("status").toString());
        map.put("mode", mapdevice1.get("mode").toString());
        map.put("dimmer", mapdevice1.get("dimmer").toString());
        map.put("temperature", mapdevice1.get("temperature").toString());
        map.put("speed", mapdevice1.get("speed").toString());
        listobj.add(map);
        mapalldevice.put("token", TokenUtil.getToken(getActivity()));
        mapalldevice.put("areaNumber", areaNumber);
        mapalldevice.put("deviceInfo", listobj);

        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapalldevice, new

                Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_device_control(position, mapdevice1);
                    }
                }, getActivity(), dialogUtil) {
                    @Override
                    public void fourCode() {
                        super.fourCode();
                        switch (listob.get(0).get("type").toString()) {
                            case "11":
                                ToastUtil.showToast(getActivity(), "恢复失败");
                                break;
                            default:
                                ToastUtil.showToast(getActivity(), "操作失败");
                                break;
                        }
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);

                        switch (listob.get(0).get("type").toString()) {
                            case "11":
                                ToastUtil.showToast(getActivity(), "恢复成功");
                                break;
                            default:
                                ToastUtil.showToast(getActivity(), "操作成功");
                                break;
                        }

                        if (vibflag) {
                            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(200);
                        }

                        if (musicflag) {
                            LogUtil.i("铃声响起");
                            MusicUtil.startMusic(getActivity(), 1, "");
                        } else {
                            MusicUtil.stopMusic(getActivity(), "");
                        }
                        listtype.set(position, status);

                        if (current_room_number != null)//获取当前房间下的设备
                            sraum_getOneRoomInfo(current_room_number);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }


    private int tag = 0;

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mMessageReceiver);
        getActivity().unregisterReceiver(receiver);
        NativeCaller.Free();
        Intent intent = new Intent();
        intent.setClass(getActivity(), BridgeService.class);
        getActivity().stopService(intent);
        tag = 0;
        EventBus.getDefault().unregister(this);
//        over_camera_list();
        super.onDestroy();
    }


    /**
     * 动态注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_RECEIVER);
        filter.addAction(MESSAGE_TONGZHI);
        filter.addAction(MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE);
        getActivity().registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(ACTION_INTENT_RECEIVER)) {
                int messflag = intent.getIntExtra("notifactionId", 0);
                if (messflag == 1 || messflag == 3 || messflag == 4 || messflag == 5) {
//                upload(false);//控制部分，推送刷新；主动推送刷新。
                    Log.e("zhu", "upload(false):" + "upload(false)" + "messflag:" + messflag);
                    if (current_room_number != null)
                        sraum_getOneRoomInfo(current_room_number);
                    //控制部分的二级页面进去要同步更新推送的信息显示 （推送的是消息）。
                    sendBroad();
                    //推送过来的
//                    ToastUtil.showToast(getActivity(), "我控制的设备时推送过来的" + ",messflag:" + messflag);
                }
            } else if (intent.getAction().equals(MESSAGE_TONGZHI)) {//门铃视频预览
                type = (String) intent.getSerializableExtra("type");
                Map mapdevice = new HashMap();
                mapdevice.put("dimmer", (String) intent.getSerializableExtra("uid"));
                mapdevice.put("temperature", "888888");
                mapdevice.put("mode", "admin");//
                switch (type) {
                    case "52"://门铃
                    case "53"://摄像头
                        door_rill(mapdevice);
                        break;
                }
            } else if (intent.getAction().equals(MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE)) {//来自设备页获取摄像头状态的通知
                videofrom = "devicefragment";
                video_item = (Map) intent.getSerializableExtra("video_item");
                common_video(video_item);
            }
        }
    }

    /**
     * 门禁报警
     *
     * @param mapdevice
     */
    private void door_rill(Map mapdevice) {
        videofrom = "macfragment";
        onitem_wifi_shexiangtou(mapdevice);
    }

    private void sendBroad() {
        Intent mIntent = new Intent(ACTION_INTENT_RECEIVER_TO_SECOND_PAGE);
        getActivity().sendBroadcast(mIntent);
    }

    private void sendBroad_pm25(Map map) {
        Intent mIntent_pm25 = new Intent(MACFRAGMENT_PM25);
        mIntent_pm25.putExtra("mapdevice", (Serializable) mapdevice);
        getActivity().sendBroadcast(mIntent_pm25);
    }

    /**
     * 获取单个房间关联信息（APP->网关）
     *
     * @param number
     */
    private void sraum_getOneRoomInfo(final String number) {
        Map map = new HashMap();
        String areaNumber = (String) current_area_map.get("number");
        map.put("areaNumber", areaNumber);
        map.put("roomNumber", number);
        map.put("token", TokenUtil.getToken(getActivity()));
//        boolean isnewProcess = (boolean) SharedPreferencesUtil.getData(getActivity(), "newProcess", false);
//        if (isnewProcess) {
//            SharedPreferencesUtil.saveData(getActivity(), "newProcess", false);
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (dialogUtil != null) {
//                        dialogUtil.loadDialog();
//                    }
//                }
//            });
//        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo
                , map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getOneRoomInfo(number);
                    }
                }, getActivity(), dialogUtil) {
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
                        deviceCount = user.count;
                        list = new ArrayList<>();
                        listtype.clear();
                        for (int i = 0; i < user.deviceList.size(); i++) {
                            Map map = new HashMap();
                            map.put("name", user.deviceList.get(i).name == null ? "" : user.deviceList.get(i).name
                            );
                            map.put("number", user.deviceList.get(i).number);
                            map.put("type", user.deviceList.get(i).type);
                            map.put("status", user.deviceList.get(i).status == null ? "" : user.deviceList.get(i).status);
                            map.put("mode", user.deviceList.get(i).mode);
                            map.put("dimmer", user.deviceList.get(i).dimmer);
                            map.put("temperature", user.deviceList.get(i).temperature);
                            map.put("speed", user.deviceList.get(i).speed);
                            map.put("boxNumber", user.deviceList.get(i).boxNumber == null ? "" :
                                    user.deviceList.get(i).boxNumber
                            );
//                            map.put("boxNumber", user.deviceList.get(i).boxNumber);
//                            map.put("boxName", user.deviceList.get(i).boxName);
                            //name1
                            //name2
                            //flag
                            map.put("name1", user.deviceList.get(i).name1);
                            map.put("name2", user.deviceList.get(i).name2);
//                            map.put("flag", user.deviceList.get(i).flag);
                            map.put("panelName", user.deviceList.get(i).panelName);
                            map.put("panelMac", user.deviceList.get(i).panelMac);
//                            map.put("deviceId", "");
                            map.put("mac", "");
                            map.put("deviceId", "");
                            list.add(map);
                        }

                        if (user.wifiList != null)
                            for (int i = 0; i < user.wifiList.size(); i++) {
                                Map map = new HashMap();
                                map.put("name", user.wifiList.get(i).name);
                                map.put("number", user.wifiList.get(i).number);
                                map.put("type", user.wifiList.get(i).type);
                                map.put("status", user.wifiList.get(i).status);
                                map.put("mode", user.wifiList.get(i).mode);
                                map.put("dimmer", user.wifiList.get(i).dimmer);
                                map.put("temperature", user.wifiList.get(i).temperature);
                                map.put("speed", user.wifiList.get(i).speed);
                                map.put("boxNumber", "");
                                map.put("boxName", "");
                                map.put("mac", user.wifiList.get(i).mac);
                                map.put("name1", "");
                                map.put("name2", "");
                                map.put("flag", "");
                                map.put("panelName", "");
                                map.put("deviceId", user.wifiList.get(i).deviceId);
                                map.put("panelMac", user.wifiList.get(i).panelMac);
                                map.put("boxNumber", ""
                                );
                                list.add(map);
                            }

                        if (list.size() != 0) {
                            SharedPreferencesUtil.saveInfo_List(getActivity(), "list_old", list);
                        } else {
                            SharedPreferencesUtil.saveInfo_List(getActivity(), "list_old", new ArrayList<Map>());
                        }

                        if (list.size() != 0) {
                            for (Map map : list) {
                                listtype.add(map.get("status") == null ? "1" :
                                        map.get("status").toString());
                            }
                        }
                        //展示首页设备列表
                        handler_laungh.sendEmptyMessage(5);
                    }
                });
    }

    /**
     * 展示首页设备列表
     */
    private void display_home_device_list() {
        deviceListAdapter.setList(list);
        deviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.area_name_txt:

                break;//项目选择
            case R.id.add_device://为网关添加设备(即添加面板)
                startActivity(new Intent(getActivity(), SelectZigbeeDeviceActivity.class));
                break;
            case R.id.common_setting_linear://设为常用
//                common_setting_image.setImageResource(R.drawable.icon_l_changyong);
//                Map map = getmap_chage_room();
                switch (deivce_type) {
                    case "100":
                        ToastUtil.showToast(getActivity(), "场景不能进行移动");
                        dialogUtil.removeviewBottomDialog();
                        break;
                    default:
                        switch_room_dialog(getActivity());
                        break;
                }
                break;
            case R.id.rename_scene_linear://重命名
                switch (authType) {//（1 业主 2 成员）
                    case "1":
                        showRenameDialog();
                        break;
                    case "2":
                        ToastUtil.showToast(getActivity(), "成员不能重命名");
                        dialogUtil.removeviewBottomDialog();
                        break;
                }
                break;
            case R.id.delete_scene_linear://删除
                showCenterDeleteDialog();
                break;
            case R.id.cancel_scene_linear://取消
                dialogUtil.removeviewBottomDialog();
                break;
            case R.id.back_rel://设备消息(1)
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.all_room_rel:// 获取全部信息（APP->网关）,sraum_getInfos所有设备，所有房间标按妞下的所有设备

                break;
            case R.id.area_rel://区域切换
                if (areaList.size() > 1)//区域列表大于1时，显示
                    showPopWindow();
        }
    }

    /**
     * 切换房间获取map
     *
     * @return
     */
    @NonNull
    private Map getmap_chage_room() {
        Map map = new HashMap();

//                current_area_map = areaList.get(position);
//                sraum_changeArea(areaList.get(position).get("areaNumber").toString());
        if (current_area_map.get("areaNumber") != null) {
            String areaNumber = current_area_map.get("areaNumber").toString();
            if (areaNumber != null)
                map.put("areaNumber", areaNumber);
        }
        if (deivce_number != null)
            map.put("number", deivce_number);
        if (device_type != null)
            map.put("type", device_type);
        return map;
    }

    //自定义dialog,centerDialog
    public void showCenterDeleteDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
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
                dialog.dismiss();
            }
        });
    }

    //自定义dialog,自定义重命名dialog
    public void showRenameDialog() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.editscene_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        final ClearLengthEditText edit_password_gateway = (ClearLengthEditText) view.findViewById(R.id.edit_password_gateway);
//        tv_title.setText("是否拨打119");
        edit_password_gateway.setText(device_name);
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_password_gateway.getText().toString() == null ||
                        edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(getActivity(), "按钮名称为空");
                    return;
                }

                boolean isexist = false;
                String var = edit_password_gateway.getText().toString();

                if (device_name.equals(var)) {
                } else {
                    String method = "";
                    switch (deivce_type) {
                        case "101":
                        case "103":
                            method = ApiHelper.sraum_updateWifiCameraName;
                            sraum_updateWifiAppleName(deivce_number, var, method);
                            break;
                        case "202":
                        case "206":
                            method = ApiHelper.sraum_updateWifiAppleDeviceName;
                            sraum_updateWifiAppleName(deivce_number, var, method);
                            break;
                        default:
                            method = ApiHelper.sraum_updateButtonName;
                            sraum_update_s(var, device_name1, device_name2, deivce_number, deivce_type, device_gatewayNumber, method);
                            break;
                    }
                    dialog.dismiss();
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 修改 wifi 红外转发设备名称
     *
     * @param
     */
    private void sraum_updateWifiAppleName(final String number, final String newName, final String method) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("number", number);
        mapdevice.put("name", newName);
        MyOkHttp.postMapString(method, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                sraum_updateWifiAppleName(number, newName, method);
            }
        }, getActivity(), dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void pullDataError() {
                ToastUtil.showToast(getActivity(), "更新失败");
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
            public void fourCode() {
                ToastUtil.showToast(getActivity(), "name 已存在");
            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(getActivity(), "areaNumber\n" +
                        "不存在");
            }


            @Override
            public void threeCode() {
                ToastUtil.showToast(getActivity(), "number 不存在");
            }

            @Override
            public void onSuccess(final User user) {
                if (current_room_number != null)
                    sraum_getOneRoomInfo(current_room_number);
                dialogUtil.removeviewBottomDialog();
            }
        });
    }

    /**
     * 更新按钮名称
     *
     * @param customName
     * @param name1
     * @param name2
     * @param deviceNumber
     * @param type
     * @param boxNumber
     */
    private void sraum_update_s(final String customName, final String name1, final String name2, final String deviceNumber, final String type,
                                final String boxNumber, final String method) {
        Map<String, Object> map = new HashMap<>();
//        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(getActivity()));
        map.put("areaNumber", areaNumber);
        map.put("gatewayNumber", boxNumber);
        map.put("deviceNumber", deviceNumber);
        map.put("newName", customName);
        if (type.equals("4")) {
            map.put("newName1", name1);
            map.put("newName2", name2);
        }

        MyOkHttp.postMapObject(method, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_update_s(customName, name1, name2, deviceNumber, type, boxNumber, method);
            }
        }, getActivity(), dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(getActivity(), "areaNumber\n" +
                        "不存在");
            }

            @Override
            public void threeCode() {
                ToastUtil.showToast(getActivity(), "gatewayNumber 不存在");
            }

            @Override
            public void fourCode() {
                ToastUtil.showToast(getActivity(), "deviceNumber 不存在");
            }

            @Override
            public void onSuccess(User user) {
                if (current_room_number != null)
                    sraum_getOneRoomInfo(current_room_number);
                dialogUtil.removeviewBottomDialog();
            }

            @Override
            public void fiveCode() {
                ToastUtil.showToast(getActivity(), "type\n" +
                        "类型不存在");
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    public static HomeFragment newInstance() {
        HomeFragment newFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    /**
     * 获取所有区域
     */
    private void sraum_getAllArea() {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getAllArea();
                    }
                }, getActivity(), null) {
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
                        areaList = new ArrayList<>();
                        for (int i = 0; i < user.areaList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            switch (user.areaList.get(i).authType) {
                                case "1":
                                    mapdevice.put("name", user.areaList.get(i).areaName + "(" + "业主" + ")");
                                    break;
                                case "2":
                                    mapdevice.put("name", user.areaList.get(i).areaName + "(" + "成员" + ")");
                                    break;
                            }
//
//                            mapdevice.put("name", user.areaList.get(i).areaName);
                            mapdevice.put("number", user.areaList.get(i).number);
                            mapdevice.put("sign", user.areaList.get(i).sign);
                            mapdevice.put("authType", user.areaList.get(i).authType);
                            mapdevice.put("roomCount", user.areaList.get(i).roomCount);
                            areaList.add(mapdevice);
                        }


                        if (areaList.size() != 0) {
                            SharedPreferencesUtil.saveInfo_List(getActivity(), "areaList_old", areaList);
                        } else {
                            SharedPreferencesUtil.saveInfo_List(getActivity(), "areaList_old", new ArrayList<Map>());
                        }

                        if (user.areaList != null && user.areaList.size() != 0) {//区域命名

                            for (Map map : areaList) {
                                if ("1".equals(map.get("sign").toString())) {
                                    current_area_map = map;
                                    switch (map.get("authType").toString()) {
                                        case "1":
                                            area_name_txt.setText(current_area_map.get("name").toString());
                                            break;
                                        case "2":
                                            area_name_txt.setText(current_area_map.get("name").toString());
                                            break;
                                    }
                                    qiehuan = "";
                                    sraum_getRoomsInfo(current_area_map.get("number").toString(), qiehuan);
                                    areaNumber = current_area_map.get("number").toString();
                                    authType = current_area_map.get("authType").toString();//（1 业主 2 成员）
                                    SharedPreferencesUtil.saveData(getActivity(), "areaNumber", areaNumber);
                                    SharedPreferencesUtil.saveData(getActivity(), "authType", authType);
                                    switch (authType) {//（1 业主 2 成员）
                                        case "1":
                                            add_device.setVisibility(View.VISIBLE);
                                            break;
                                        case "2":
                                            add_device.setVisibility(View.GONE);
                                            break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 切换区域
     */
    private void sraum_changeArea(final String areaNumber, final String authType,
                                  final ListViewAdaptWidth wv, final AreaListAdapter areaListAdapter) {
//        if (dialogUtil != null) {
//            dialogUtil.loadDialog();
//        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_changeArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_changeArea(areaNumber, authType, wv, areaListAdapter);
                    }
                }, getActivity(), null) {
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
//                        ToastUtil.showToast(getActivity(), "区域切换成功");
                        //qie huan cheng gong ,获取区域的所有房间信息
                        SharedPreferencesUtil.saveData(getActivity(), "areaNumber", areaNumber);
                        for (int i = 0; i < areaList.size(); i++) {

                            if (areaList.get(i).get("number").equals(areaNumber)) {
                                areaList.get(i).put("sign", "1");
                            } else {
                                areaList.get(i).put("sign", "0");
                            }

                        }
                        SharedPreferencesUtil.saveInfo_List(getActivity(), "areaList_old", areaList);
                        SharedPreferencesUtil.saveData(getActivity(), "authType", authType);
                        handler_laungh.sendEmptyMessage(6);
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(getActivity(), "areaNumber 不\n" +
                                "正确");
                    }
                });
    }

    /**
     * 获取区域的所有房间信息
     *
     * @param areaNumber
     * @param qiehuan
     */
    private void sraum_getRoomsInfo(final String areaNumber, final String qiehuan) {
//        if (dialogUtil != null) {
//            dialogUtil.loadDialog();
//        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getRoomsInfo(areaNumber, qiehuan);
                    }
                }, getActivity(), null) {
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
                        ToastUtil.showToast(getActivity(), "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void onSuccess(final User user) {
                        //qie huan cheng gong ,获取区域的所有房间信息
                        roomList = new ArrayList<>();
                        for (int i = 0; i < user.roomList.size(); i++) {
                            Map mapdevice = new HashMap<>();
                            mapdevice.put("name", user.roomList.get(i).name);
                            mapdevice.put("number", user.roomList.get(i).number);
                            mapdevice.put("count", user.roomList.get(i).count);
                            mapdevice.put("room_index", i);
                            roomList.add(mapdevice);
                        }

                        if (roomList.size() != 0) {
                            SharedPreferencesUtil.saveInfo_List(getActivity(), "roomList_old", roomList);
                        } else {
                            SharedPreferencesUtil.saveInfo_List(getActivity(), "roomList_old", new ArrayList<Map>());
                        }
                        handler_laungh.sendEmptyMessage(4);
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(getActivity(), "areaNumber 不\n" +
                                "正确");
                    }
                });
    }

    /**
     * //去显示房间列表
     */
    private void display_room_list(int position) {
        homeDeviceListAdapter.setList1(roomList);
        homeDeviceListAdapter.notifyDataSetChanged();
        for (int i = 0; i < roomList.size(); i++) {
            if (i == position) {
                HomeDeviceListAdapter.getIsSelected().put(i, true);
            } else {
                HomeDeviceListAdapter.getIsSelected().put(i, false);
            }
        }
        homeDeviceListAdapter.notifyDataSetChanged();
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.popup_listview_window, null);

            final ListViewAdaptWidth wv = (ListViewAdaptWidth) view.findViewById(R.id.wheel_view_wv);


            final AreaListAdapter areaListAdapter = new AreaListAdapter(getActivity(), areaList);
            wv.setAdapter(areaListAdapter);
            wv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    //xuan zhong mou ge quyu
                    current_area_map = areaList.get(position);
                    areaNumber = areaList.get(position).get("number").toString();
                    authType = areaList.get(position).get("authType").toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sraum_changeArea(areaList.get(position).get("number").toString(),
                                    areaList.get(position).get("authType").toString(), wv, areaListAdapter);
                        }
                    }).start();

                }
            });
            pop_set(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * popWindow  canshu peizhi
     *
     * @param view
     */
    @SuppressLint("WrongConstant")
    private void pop_set(View view) { //
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//水平
        // 初始化自定义的适配器
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;

        popupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);//高度写死

//            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable cd = new ColorDrawable(0x00ffffff);// 背景颜色全透明
        popupWindow.setBackgroundDrawable(cd);
        int[] location = new int[2];
        area_rel.getLocationOnScreen(location);//获得textview的location位置信息，绝对位置
        popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
        backgroundAlpha(1.0f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
        // 将pixels转为dip
//            int xoffInDip = pxTodip(displayWidth) / 2;
        popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xPos = (displayWidth - popupWindow.getContentView().getMeasuredWidth()) / 2;
//            popupWindow.showAsDropDown(project_select, 0, dip2px(getActivity(), 10));
        popupWindow.showAtLocation(area_rel, Gravity.NO_GRAVITY, xPos, location[1] + area_rel.getHeight()

        );
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow = null;// 当点击屏幕时，使popupWindow消失
                backgroundAlpha(1.0f);// 当点击屏幕时，使半透明效果取消，1.0f为透明
            }
        });
    }


    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //ho
//        roomList.get(position).put("isselect",true);
        for (int i = 0; i < roomList.size(); i++) {
            if (i == position) {
                HomeDeviceListAdapter.getIsSelected().put(i, true);
            } else {
                HomeDeviceListAdapter.getIsSelected().put(i, false);
            }
        }

        homeDeviceListAdapter.notifyDataSetChanged();
        //编写点击房间去显示该房间下的设备列表
        current_room_number = roomList.get(position).get("number").toString();
        sraum_getOneRoomInfo(current_room_number);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myWifiThread != null) {
            blagg = false;
        }
        NativeCaller.StopSearch();
    }

    StringBuffer stringbuffer = new StringBuffer();
    private int connection_wifi_camera_index;
    private Handler PPPPMsgHandler = new Handler() {
        public void handleMessage(Message msg) {

            Bundle bd = msg.getData();
            int msgParam = bd.getInt(STR_MSG_PARAM);
            //        bd.putString(STR_DID, did);
//            String  did = bd.getString(STR_DID);
            int msgType = msg.what;
            Log.i("aaa", "====" + msgType + "--msgParam:" + msgParam);
            String did = bd.getString(STR_DID);
            switch (msgType) {
                case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
                    int resid;
                    switch (msgParam) {
                        case ContentCommon.PPPP_STATUS_CONNECTING://0
                            resid = R.string.pppp_status_connecting;
                            Log.e("fei->", "resid:" + "正在连接");
                            if (stringbuffer.toString().contains("未知状态,"))
                                stringbuffer.append("正在连接");
                            tag = 2;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
                            resid = R.string.pppp_status_connect_failed;
                            Log.e("fei->", "resid:" + "连接失败");
                            tag = 0;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            connection_wifi_camera_index++;
                            if (connection_wifi_camera_index <= 10)
                                handler.sendEmptyMessage(11);
                            break;
                        case ContentCommon.PPPP_STATUS_DISCONNECT://4
                            resid = R.string.pppp_status_disconnect;
                            Log.e("fei->", "resid:" + "断线");
                            tag = 0;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            break;
                        case ContentCommon.PPPP_STATUS_INITIALING://1
                            resid = R.string.pppp_status_initialing;
                            Log.e("fei->", "resid:" + "已连接吗，正在初始化");
                            tag = 2;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            break;
                        case ContentCommon.PPPP_STATUS_INVALID_ID://5
                            resid = R.string.pppp_status_invalid_id;
                            Log.e("fei->", "resid:" + "ID号无效");
//                            progressBar.setVisibility(View.GONE);
                            tag = 0;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            break;
                        case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态
                            resid = R.string.pppp_status_online;
                            Log.e("fei->", "resid:" + "在线");
                            connection_wifi_camera_index = 0;
                            if (stringbuffer.toString().contains("未知状态,正在连接"))
                                stringbuffer.append(",在线");
                            //摄像机在线之后读取摄像机类型
                            String cmd = "get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
                                    + "&user=admin&pwd=" + SystemValue.devicePass;
                            NativeCaller.TransferMessage(did, cmd, 1);
                            tag = 1;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            handler.sendEmptyMessage(10);//设备已经在线了
                            break;
                        case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
                            resid = R.string.device_not_on_line;
                            Log.e("fei->", "resid:" + "摄像机不在线");
//                            ToastUtil.showToast(getActivity(),"摄像不在线");
                            tag = 0;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
                            resid = R.string.pppp_status_connect_timeout;
                            Log.e("fei->", "resid:" + "连接超时");
                            tag = 0;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
                            resid = R.string.pppp_status_pwd_error;
                            Log.e("fei->", "resid:" + "错误密码");
                            tag = 0;
                            if (dialogUtil != null) dialogUtil.removeDialog();
                            break;
                        default:
                            resid = R.string.pppp_status_unknown;
                            Log.e("fei->", "resid:" + "未知状态");
                            stringbuffer = new StringBuffer();
                            stringbuffer.append("未知状态,");
                            if (dialogUtil != null) dialogUtil.removeDialog();
                    }

                    init_Camera(did);

                    /*      textView_top_show.setText(getResources().getString(resid));*/
                    if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
                            || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                        NativeCaller.StopPPPP(did);
                    }
                    break;
                case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
                    break;
            }
        }
    };

    /**
     * 初始化摄像头列表
     *
     * @param did
     */
    private void init_Camera(String did) {//修改并完善如果Id相同就更新，没有该Id就添加
        List<Map> list_wifi_camera =
                SharedPreferencesUtil.getInfo_List(getActivity(), "list_wifi_camera_first");
        Map map = new HashMap();
        map.put("did", did);
        map.put("tag", tag);
        boolean is_has = false;
        for (int i = 0; i < list_wifi_camera.size(); i++) {
            if (list_wifi_camera.get(i).get("did").equals(did)) {
                list_wifi_camera.get(i).put("tag", tag);
                is_has = true;
                break;
            }
        }

        if (!is_has) {
            list_wifi_camera.add(map);
        }

//        if (index == list_wifi_camera.size()) {
//            list_wifi_camera.add(map);
//        }

//        Map<Integer, Integer> item = list_wifi_camera.get(position);
//        int itemplan = item.entrySet().iterator().next().getValue();
//        int itemplanKey = item.entrySet().iterator().next().getKey();

        SharedPreferencesUtil.saveInfo_List(getActivity(), "list_wifi_camera_first", list_wifi_camera);
    }

    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Log.d("ip", "type:" + type + " param:" + param);
        Bundle bd = new Bundle();
        Message msg = PPPPMsgHandler.obtainMessage();
        msg.what = type;
        bd.putInt(STR_MSG_PARAM, param);
        bd.putString(STR_DID, did);
        msg.setData(bd);
        PPPPMsgHandler.sendMessage(msg);
        if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
            intentbrod.putExtra("ifdrop", param);
            getActivity().sendBroadcast(intentbrod);
        }
    }

    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {
        // TODO Auto-generated method stub
        Log.i("ip", "BSSnapshotNotify---len" + len);
    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1,
                                   String user2, String pwd2, String user3, String pwd3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void CameraStatus(String did, int status) {

    }


    @Override
    public void CallBackGetStatus(String did, String resultPbuf, int cmd) {
        // TODO Auto-generated method stub
        if (cmd == ContentCommon.CGI_IEGET_STATUS) {
            String cameraType = spitValue(resultPbuf, "upnp_status=");
            int intType = Integer.parseInt(cameraType);
            int type14 = (int) (intType >> 16) & 1;// 14位 来判断是否报警联动摄像机
            if (intType == 2147483647) {// 特殊值
                type14 = 0;
            }

            if (type14 == 1) {
                updateListHandler.sendEmptyMessage(2);
            }
        }
    }

    private String spitValue(String name, String tag) {
        String[] strs = name.split(";");
        for (int i = 0; i < strs.length; i++) {
            String str1 = strs[i].trim();
            if (str1.startsWith("var")) {
                str1 = str1.substring(4, str1.length());
            }
            if (str1.startsWith(tag)) {
                String result = str1.substring(str1.indexOf("=") + 1);
                return result;
            }
        }
        return -1 + "";
    }


    /**
     * 从网上加载并保存到本地
     *
     * @param onclick
     */
    boolean isok;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (intfirst_time == 1) {
                intfirst_time = 2;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sraum_getAllArea();
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getOtherDevices();
                    }
                }).start();
            }
        }
    }
}
