package com.massky.sraum.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
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

import com.AddTogenInterface.AddTogglenInterfacer;
import com.andview.refreshview.XRefreshView;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MusicUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.AirControlActivity;
import com.massky.sraum.activity.AlarmDeviceMessageActivity;
import com.massky.sraum.activity.CurtainWindowActivity;
import com.massky.sraum.activity.DeviceSettingDelRoomActivity;
import com.massky.sraum.activity.Pm25Activity;
import com.massky.sraum.activity.SelectZigbeeDeviceActivity;
import com.massky.sraum.activity.TiaoGuangLightActivity;
import com.massky.sraum.adapter.AreaListAdapter;
import com.massky.sraum.adapter.DetailDeviceHomeAdapter;
import com.massky.sraum.adapter.HomeDeviceListAdapter;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;
import com.massky.sraum.service.MyService;
import com.massky.sraum.view.ListViewAdaptWidth;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2017/11/30.
 */

public class HomeFragment extends BaseFragment1 implements AdapterView.OnItemClickListener {
    private PopupWindow popupWindow;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.project_select)
    TextView project_select;
    @InjectView(R.id.dragGridView)
    GridView mDragGridView;
    private List<Map> dataSourceList = new ArrayList<>();
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

    @Override
    protected void onData() {
        share_getData();
        sraum_getAllArea();
        test_device_data();
        init_device_onclick();
        room_list_show_adapter();
    }

    private void share_getData() {
        loginPhone = (String) SharedPreferencesUtil.getData(getActivity(), "loginPhone", "");
        SharedPreferences preferences = getActivity().getSharedPreferences("sraum" + loginPhone,
                Context.MODE_PRIVATE);
        vibflag = preferences.getBoolean("vibflag", false);
        musicflag = preferences.getBoolean("musicflag", false);
    }

    /**
     * 侧栏房间列数据显示
     */
    private void room_list_show_adapter() {

        Map map1 = new HashMap();

        map1.put("number", "1");
        map1.put("name", "客厅");
        map1.put("count", "12");
        roomList.add(map1);

        map1 = new HashMap();
        map1.put("number", "2");
        map1.put("name", "书房");
        map1.put("count", "22");
        roomList.add(map1);

        map1 = new HashMap();
        map1.put("number", "3");
        map1.put("name", "儿童房");
        map1.put("count", "23");
        roomList.add(map1);

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
     * 初始化设备点击事件
     */
    private void init_device_onclick() {
        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> mapalldevice = new HashMap<String, Object>();
                listob = new ArrayList<Map<String, Object>>();
                test_pm25();
                if(listtype.size() == 0) return;
                if (listtype.get(position).equals("1")) {
                    status = "0";
                } else {
                    status = "1";
                }
                Map<String, Object> mapdevice = new HashMap<String, Object>();
                mapdevice.put("type", deviceList.get(position).get("type").toString());
                mapdevice.put("number", deviceList.get(position).get("number").toString());
                switch (deviceList.get(position).get("type").toString()) {
                    case "11":
                        mapdevice.put("status", "0");
                        break;
                    case "15":
                        mapdevice.put("status", "1");
                        break;
                    default:
                        mapdevice.put("status", status);
                        break;
                }

                mapdevice.put("dimmer", deviceList.get(position).get("dimmer").toString());
                mapdevice.put("mode", deviceList.get(position).get("mode").toString());
                mapdevice.put("temperature", deviceList.get(position).get("temperature").toString());
                mapdevice.put("speed", deviceList.get(position).get("speed").toString());
                mapdevice.put("mac", deviceList.get(position).get("mac") == null ? "" :
                        deviceList.get(position).get("mac").toString());
                mapdevice.put("deviceId", deviceList.get(position).get("deviceId") == null ? "" :
                        deviceList.get(position).get("deviceId").toString());
                mapdevice.put("name", deviceList.get(position).get("name").toString());
                mapdevice.put("panelName", deviceList.get(position).get("panelName") == null ? "" :
                        deviceList.get(position).get("panelName").toString());
                listob.add(mapdevice);
                mapalldevice.put("token", TokenUtil.getToken(getActivity()));
                mapalldevice.put("boxNumber", TokenUtil.getBoxnumber(getActivity()));
                mapalldevice.put("deviceInfo", listob);
                String name = "";

                switch (deviceList.get(position).get("type").toString()) {
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

                    //special_type_device(mapdevice);
//                test_pm25();
                    // test_tv();
                    //test_air_control();
//                water_sensor();
                }

                switch (deviceList.get(position).get("type").toString()) {
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
//                        this.mapdevice = mapdevice;
//                        test_tv(mapdevice.get("mac").toString());
                        mapdevice1 = mapdevice;
                        break;
                    case "10":
                        mapdevice1 = mapdevice;
//                        test_pm25();
                        break;
                    case "206"://WIFI空调
                        mapdevice1 = mapdevice;
//                        test_air_control(mapdevice.get("mac").toString());
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
                        mapdevice1 = mapdevice;
//                        onitem_wifi_shexiangtou(mapdevice);
                        break;
                    default:
                        curtains_and_light(position, mapalldevice);
                        break;
                }
            }
        });


        mDragGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (singleRoomAssoList.size() > 0)
                    deivce_number = (String) deviceList.get(position).get("number");//设备编号
                deivce_type = (String) deviceList.get(position).get("type");//设备编号
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
                return true;
            }
        });
    }

    private void curtains_and_light(int position, Map<String, Object> mapalldevice) {
        if (deviceList.get(position).get("type").toString().equals("1") || deviceList.get(position).get("type").toString().equals("11")
                || deviceList.get(position).get("type").toString().equals("16")
                || deviceList.get(position).get("type").toString().equals("15")) {
//            String boxstatus = TokenUtil.getBoxstatus(getActivity());
//            if (!boxstatus.equals("0")) {
//                getBoxStatus(mapalldevice, position);
            sraum_device_control(mapalldevice,position);
//            }
//        } else {
//            /*窗帘所需要的属性值*/
//            Log.e("zhu", "chuanglian:" + "窗帘所需要的属性值");
//            Bundle bundle = new Bundle();
//            bundle.putString("type",   deviceList.get(position).get("type").toString());
//            bundle.putString("number", deviceList.get(position).get("number").toString());
//            bundle.putString("name1",  deviceList.get(position).get("name1").toString());
//            bundle.putString("name2",  deviceList.get(position).get("name2").toString());
//            bundle.putString("name",   deviceList.get(position).get("name").toString());
//            LogUtil.eLength("名字",deviceList.get(position).get("name1").toString() + deviceList.get(position).get("name2").toString());
////            IntentUtil.startActivity(getActivity(), LamplightActivity.class, bundle);
//        }
        }
    }


    /**
     * 测试pm2.5
     */
    private void test_pm25() {
        Intent intent = new Intent(getActivity(), Pm25Activity.class);
//        intent.putExtra("mapdevice", (Serializable) this.mapdevice);
        startActivity(intent);
    }

    /**
     * sraum_device_control
     * @param
     * @param position
     */
    private void sraum_device_control(final Map<String, Object> mapdevice,final int position) {
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_device_control(mapdevice, position);
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
                    MusicUtil.startMusic(getActivity(), 1,"");
                } else {
                    MusicUtil.stopMusic(getActivity(),"");
                }
                listtype.set(position, status);
                String string = listtype.get(position);
//                        if (string.equals("1")) {
//                            itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                        } else {
//                            itemrela_id.setBackgroundResource(R.drawable.markh);
//                        }
//                upload(true);刷新设备猎去显示，当前房间下的设备列表
                sraum_getOneRoomInfo(current_room_number);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
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
     * 获取单个设备或是按钮信息（APP->网关）
     *
     * @param map
     */
    private void get_single_device_info(final Map map) {
        MyOkHttp.postMapObject(ApiHelper.sraum_getOneInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, getActivity(), null) {
                    @Override
                    public void onSuccess(User user) {
//       switch (type) {//空调,PM检测,客厅窗帘,门磁,主灯
                        String type = (String) map.get("type");
                        map.put("status", user.status);
                        map.put("name", user.name);
                        switch (type) {
                            case "4"://空调
                                map.put("mode", user.mode);
                                map.put("temperature", user.temperature);
                                map.put("speed", user.speed);
//                                startActivity(new Intent(getActivity(),
//                                        AirControlActivity.class));
                                Intent intent_air_control = new Intent(getActivity(),
                                        AirControlActivity.class);
                                intent_air_control.putExtra("map_item", (Serializable) map);
                                startActivity(intent_air_control);

                                break;

                            case "12"://PM25(zigbee)

//                                map.put("mode", user.mode);
//                                map.put("pm2.5", user.temperature);
//                                map.put("speed", user.speed);
//                                startActivity(new Intent(getActivity(),
//                                        Pm25Activity.class));
//                                Intent intent_curtain = new Intent(getActivity(),
//                                        CurtainWindowActivity.class);
//                                intent_curtain.putExtra("map_item", (Serializable) map);
//                                startActivity(intent_curtain);

                                break;

                            case "3"://窗帘
                                map.put("name1", user.name1);
                                map.put("name2", user.name2);

                                Intent intent_curtain = new Intent(getActivity(),
                                        CurtainWindowActivity.class);
                                intent_curtain.putExtra("map_item", (Serializable) map);
                                startActivity(intent_curtain);

                                break;

                            case "7"://门磁
                                startActivity(new Intent(getActivity(),
                                        DeviceSettingDelRoomActivity.class));
                                break;

                            case "1"://灯
                                Map map_light = new HashMap();
                                map_light.put("command", "sraum_controlButton");
                                map_light.put("number", map.get("number"));
                                map_light.put("type", map.get("type"));
                                switch (user.status) {
                                    case "0":
                                        //去开灯，TCP
                                        map_light.put("status", "1");
                                        MyService.getInstance().sraum_send_tcp(map_light, "sraum_controlButton");
                                        break;
                                    case "1":
                                        //去关灯，TCP
                                        map_light.put("status", "0");
                                        MyService.getInstance().sraum_send_tcp(map_light, "sraum_controlButton");
                                        break;
                                }

                                break;

                            case "2"://调光灯
//                        //map_item
                                map.put("dimmer", user.dimmer);
                                Intent intent_tiaoguang = new Intent(getActivity(), TiaoGuangLightActivity.class);
                                intent_tiaoguang.putExtra("map_item", (Serializable) map);
                                startActivity(intent_tiaoguang);

                                break;

//                    default:
//                        dialogUtil.loadViewBottomdialog();
//                        break;//默认 是场景
                        }

                    }
                });
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
        project_select.setOnClickListener(this);
        add_device.setOnClickListener(this);
        back_rel.setOnClickListener(this);
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

    public void switch_room_dialog(final Context context, Map map) {

//        dialogUtil.loadDialog();
        View view = LayoutInflater.from(context).inflate(R.layout.credit_card_num_pop, null);
        final RelativeLayout xiala_shuaka_rel = (RelativeLayout) view.findViewById(R.id.xiala_shuaka_rel);
        final ImageView btn_xiala_img = (ImageView) view.findViewById(R.id.btn_xiala_img);
        final TextView xiala_shuaka_txt = (TextView) view.findViewById(R.id.xiala_shuaka_txt);
        Button forget_shuaka_btn = (Button) view.findViewById(R.id.forget_shuaka_btn);
        Button confirm_shuaka_btn = (Button) view.findViewById(R.id.confirm_shuaka_btn);
        ListView room_select = (ListView) view.findViewById(R.id.room_select);


        final List<String> roomNames = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            roomNames.add("房间：" + "i");
        }


//        for (int i = 0; i < roomList.size(); i++) {
//            roomNames.add((String) roomList.get(i).get("name"));
//        }
//


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
        final Dialog dialog = new Dialog(getActivity());
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
        //
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

        //deivce_number,roomNumber;
        //修改房间关联信息（APP->网关）
        Map map = new HashMap();
        map.put("token", TokenUtil.getToken(getActivity()));
        map.put("roomNumber", roomNumber == null ? "" : roomNumber);
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
                        //刷新某个房间下的所有设备
                        switch (current_room_number) {
                            case "":
//                                sraum_getInfos(false, device_type);//获取全部信息（APP->网关）
                                break;
                            default:
                                sraum_getOneRoomInfo(current_room_number);
                                break;
                        }
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
        StatusUtils.setFullToStatusBar(getActivity());  // StatusBar.
//
//        for (int i = 0; i < 30; i++) {
//            HashMap<String, Object> itemHashMap = new HashMap<>();
//            itemHashMap.put("item_image", R.mipmap.ic_launcher);
//            itemHashMap.put("item_text", "拖拽" + Integer.toString(i));
//            dataSourceList.add(itemHashMap);
//        }

        //title_room;//device_name,device_action
        init_grideview_data();

//        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(getActivity(), dataSourceList,
//                R.layout.drag_view_item, new String[]{"item_image", "item_text"}, new int[]{R.id.item_image, R.id.item_text});
        deviceListAdapter = new DetailDeviceHomeAdapter(getActivity()
                , deviceList);
        mDragGridView.setAdapter(deviceListAdapter);
//        get_gateway_name();
////        get_allroominfo();
//        addBrodcastAction();//添加TCP接收广播通知
    }

//    /**
//     * 添加TCP接收广播通知
//     * // add Action1
//     */
//    private void addBrodcastAction() {
//        //推送修改网关名称（网关->APP）
//        push_gatewayName();
//        push_sraum_controlButton();
//        sraum_pushDeviceName();
//        sraum_pushDeleteDevice();
//    }
//
//    /**
//     * 推送修改设备名称（网关->APP）
//     */
//    private void sraum_pushDeviceName() {
//        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.Sraum_PushDeviceName), new OnActionResponse() {
//
//            @Override
//            public void onResponse(Intent intent) {
//                String tcpreceiver = intent.getStringExtra("strcontent");
////                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
//                //解析json数据
//                final User user = new GsonBuilder().registerTypeAdapterFactory(
//                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
//                if (user == null) return;
//                String newName = user.newName;
//                String number = user.number;
//                //更新房间下的某个设备的设备名称
//                switch (roomNumber) {
//                    case "":
//                        sraum_getInfos(false, device_type);//获取全部信息（APP->网关）
//                        break;
//                    default:
//                        sraum_getOneRoomInfo(roomNumber);
//                        break;
//                }
//            }
//        });
//    }
//
//    /**
//     * 推送删除设备（网关->APP）
//     */
//    private void sraum_pushDeleteDevice() {
//        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.Sraum_PushDeleteDevice), new OnActionResponse() {
//
//            @Override
//            public void onResponse(Intent intent) {
//                String tcpreceiver = intent.getStringExtra("strcontent");
////                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
//                //解析json数据
//                final User user = new GsonBuilder().registerTypeAdapterFactory(
//                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
//                if (user == null) return;
////                String newName = user.newName;
//                String number = user.number;
//                //更新房间下的某个设备的设备名称
//                switch (roomNumber) {
//                    case "":
//                        sraum_getInfos(false, device_type);//获取全部信息（APP->网关）
//                        break;
//                    default:
//                        sraum_getOneRoomInfo(roomNumber);
//                        break;
//                }
//            }
//        });
//    }

//    /**
//     * 接收jpush推送回来的控制按钮（APP->网关）
//     */
//    private void push_sraum_controlButton() {
//        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.Sraum_Control_Button), new OnActionResponse() {
//
//            @Override
//            public void onResponse(Intent intent) {
//                String tcpreceiver = intent.getStringExtra("strcontent");
////                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
//                //解析json数据
//                final User user = new GsonBuilder().registerTypeAdapterFactory(
//                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
//                if (user == null) return;
//                device_type = user.type;//设备类型
//                switch (roomNumber) {
//                    case "":
//                        sraum_getInfos(false, device_type);//获取全部信息（APP->网关）
//                        break;
//                    default:
//                        sraum_getOneRoomInfo(roomNumber);
//                        break;
//                }
//            }
//        });
//    }

//    /**
//     * 广播刷新第二个界面
//     *
//     * @param context
//     * @param action
//     * @param strcontent
//     */
//    private void processCustomMessage(Context context
//            , String action, String strcontent) {
//
//        Intent msgIntent = new Intent(action);
////            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//        msgIntent.putExtra("result", strcontent);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//    }

//    /**
//     * 广播刷新第二个界面
//     *
//     * @param device_type
//     */
//    private void broadcast_refresh_second_page(final String device_type) {
//        //发送广播，通知最新列表更新
//
//        switch (device_type) {
//            case "1":
//                //更新数据
//
//                break;//灯
//            case "2":
//                processCustomMessage(getActivity(), ApiTcpReceiveHelper.TIAO_GUANG_RECEIVE_ACTION, "100");
//                break;//调光灯
//            case "3":
//                processCustomMessage(getActivity(), ApiTcpReceiveHelper.CURTAIN_RECEIVE_ACTION, "100");
//                break;//3-窗帘
//            case "4":
//                processCustomMessage(getActivity(), ApiTcpReceiveHelper.AIRCONTROL_RECEIVE_ACTION, "100");
//                break;//4-空调
//            case "5":
//
//                break;//5-地暖
//            case "6":
//
//                break;//6-新风
//            case "7":
//
//                break;//7-门磁
//            case "8":
//
//                break;//8-人体感应
//            case "9":
//
//                break;//9-红外转发
//            case "10"://10-燃气
//
//                break;
//        }
//    }
//
//    /**
//     * 推送修改网关名称（网关->APP）
//     */
//    private void push_gatewayName() {
//        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.Sraum_PushGateWayName), new OnActionResponse() {
//
//            @Override
//            public void onResponse(Intent intent) {
//                String tcpreceiver = intent.getStringExtra("strcontent");
////                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
//                //解析json数据
//                final User user = new GsonBuilder().registerTypeAdapterFactory(
//                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
//                if (user == null) return;
//                if (user.newName != null) {
//                    project_select.setText(user.newName.toString());
//                }
//            }
//        });
//    }
//
//    private void get_gateway_name() {
//        //获取网关名称（APP->网关）
//        Map map = new HashMap();
//        map.put("command", "sraum_getGatewayName");
//        MyOkHttp.postMapObject(ApiHelper.sraum_getGatewayName, map,
//                new Mycallback(new AddTogglenInterfacer() {
//                    @Override
//                    public void addTogglenInterfacer() {
//
//                    }
//                }, getActivity(), null) {
//                    @Override
//                    public void onSuccess(User user) {
//                        project_select.setText(user.name);
//                    }
//                });
//    }

//    private void get_allroominfo() {
//        //获取网关名称（APP->网关）
//        Map map = new HashMap();
//        map.put("command", "sraum_getRoomsInfo");
//
//        MyOkHttp.postMapObject(ApiHelper.sraum_getRoomsInfo, map,
//                new Mycallback(new AddTogglenInterfacer() {
//                    @Override
//                    public void addTogglenInterfacer() {
//
//                    }
//                }, getActivity(), null) {
//                    @Override
//                    public void onSuccess(User user) {
////                        project_select.setText(user.name);
//                        roomsInfos = new ArrayList<>();
//                        int all_index = 0;
//                        for (int i = 0; i < user.roomList.size(); i++) {
//                            Map map = new HashMap();
//                            map.put("number", user.roomList.get(i).number);
//                            map.put("name", user.roomList.get(i).name);
//                            map.put("count", user.roomList.get(i).count);
//                            map.put("is_select", "0");
//                            roomsInfos.add(map);
//                            int index = Integer.parseInt(user.roomList.get(i).count);
//                            all_index += index;
//                        }  //
//
//                        //添加头全部所有房间综合，
//                        Map map_all = new HashMap();
//                        map_all.put("number", "");
//                        map_all.put("name", "全部");
//                        map_all.put("count", "" + all_index);
//                        map_all.put("is_select", "0");
//                        roomsInfos.set(0, map_all);
//                        homeDeviceListAdapter = new HomeDeviceListAdapter(getActivity(), roomsInfos, new HomeDeviceListAdapter.HomeDeviceItemClickListener() {
//                            @Override
//                            public void homedeviceClick(String number) {//获取单个房间关联信息（APP->网关）
//                                switch (number) {
//                                    case "":
//                                        roomNumber = "";
//                                        sraum_getInfos(true, device_type);//获取全部信息（APP->网关）
//                                        break;
//                                    default:
//                                        roomNumber = number;
//                                        sraum_getOneRoomInfo(number);
//                                        break;
//                                }
//                            } //
//                        });
//                        home_listview.setAdapter(homeDeviceListAdapter);//设备侧栏列表
//                    }
//                });
//    } //

    /**
     * 获取单个房间关联信息（APP->网关）
     *
     * @param number
     */
    private void sraum_getOneRoomInfo(final String number) { //
        Map map = new HashMap();
        String areaNumber = (String) current_area_map.get("areaNumber");
        map.put("areaNumber", areaNumber);
        map.put("roomNumber", number);
        map.put("token", TokenUtil.getToken(getActivity()));
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }

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
                        deviceList = new ArrayList<>();
                        listtype.clear();
                        for (int i = 0; i < user.deviceList.size(); i++) {
                            Map map = new HashMap();
                            map.put("name", user.deviceList.get(i).name);
                            map.put("number", user.deviceList.get(i).number);
                            map.put("type", user.deviceList.get(i).type);
                            map.put("status", user.deviceList.get(i).status);
                            map.put("mode", user.deviceList.get(i).mode);
                            map.put("dimmer", user.deviceList.get(i).dimmer);
                            map.put("temperature", user.deviceList.get(i).temperature);
                            map.put("speed", user.deviceList.get(i).speed);
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
                            deviceList.add(map);
                        }

                        for (Map map : deviceList) {
                            listtype.add(map.get("status").toString());
                        }

                        //展示首页设备列表
                        display_home_device_list();
                    }
                });
    }

    /**
     * 展示首页设备列表
     */
    private void display_home_device_list() {
        deviceListAdapter.setList(deviceList);
        deviceListAdapter.notifyDataSetChanged();
    }

//    /**
//     * 各个房间的详细配置和其他的设备详情
//     *
//     * @param json
//     * @throws JSONException
//     */
//    private Map detail_room_select(JSONObject json) throws JSONException {
//        Map map = new HashMap();
//        String type = json.getString("type");
//        if (type != null) {
//            map.put("type", type);
//        }
//
//        String online = json.getString("online");
//        if (online != null) {
//            map.put("online", online);
//        }
//
//        String number = json.getString("number");
//        if (number != null) {
//            map.put("number", number);
//        }
//
//        String name = json.getString("name");
//        if (name != null) {
//            map.put("name", name);
//        }
//
//        String status = json.getString("status");
//        if (status != null) {
//            map.put("status", status);
//        }
//
//        String fatherName = json.getString("fatherName");
//        if (fatherName != null) {
//            map.put("fatherName", fatherName);
//        }
//
//        String mode = json.getString("mode");
//        if (mode != null) {
//            map.put("mode", mode);
//        }
//
//        String temperature = json.getString("temperature");
//        if (temperature != null) {
//            map.put("temperature", temperature);
//        }
//
//        String speed = json.getString("speed");
//        if (speed != null) {
//            map.put("speed", speed);
//        }
//
//        String electricity = json.getString("electricity");
//        if (electricity != null) {
//            map.put("electricity", electricity);
//        }
//
//        String vlaue = json.getString("vlaue");
//        if (vlaue != null) {
//            map.put("vlaue", vlaue);
//        }
//
//        String pm25 = json.getString("pm2.5");
//        if (pm25 != null) {
//            map.put("pm2.5", pm25);
//        }
//
//        String pm1 = json.getString("pm1.0");
//        if (pm1 != null) {
//            map.put("pm1.0", pm1);
//        }
//
//        String pm10 = json.getString("pm10");
//        if (pm10 != null) {
//            map.put("pm10", pm10);
//        }
//
//        String humidity = json.getString("humidity");
//        if (humidity != null) {
//            map.put("humidity", humidity);
//        }
//
//        String alarm = json.getString("alarm");
//        if (alarm != null) {
//            map.put("alarm", alarm);
//        }
//
//        String roomNumber = json.getString("roomNumber");
//        if (roomNumber != null) {
//            map.put("roomNumber", roomNumber);
//        }
//
//        String dimmer = json.getString("dimmer");
//        if (dimmer != null) {
//            map.put("dimmer", dimmer);
//        }
//
//        String roomName = json.getString("roomName");
//        if (roomName != null) {
//            map.put("roomName", roomName);
//        }
//
//        return map;
//    }

    /**
     * 初始化grideview -data
     */
    private void init_grideview_data() {
        deviceList = new ArrayList<>();
        HashMap<String, Object> itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_kongtiao_active);
        itemHashMap.put("title_room", "客厅123");
        itemHashMap.put("device_name", "空调");
        itemHashMap.put("device_action", "制冷|22|底风12313");
        deviceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_pm25_active);
        itemHashMap.put("title_room", "客厅");
        itemHashMap.put("device_name", "PM检测");
        itemHashMap.put("device_action", "50 良");
        deviceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_chuanglian_active);
        itemHashMap.put("title_room", "客厅");
        itemHashMap.put("device_name", "客厅窗帘3");
        itemHashMap.put("device_action", "打开");
        deviceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_menci_active);
        itemHashMap.put("title_room", "南卧室");
        itemHashMap.put("device_name", "门磁");
        itemHashMap.put("device_action", "离线");
        deviceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_deng_active);
        itemHashMap.put("title_room", "客厅");
        itemHashMap.put("device_name", "主灯");
        itemHashMap.put("device_action", "客厅开关");
        deviceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_deng_active);
        itemHashMap.put("title_room", "卧室");
        itemHashMap.put("device_name", "调光灯");
        itemHashMap.put("device_action", "卧室开关");
        deviceList.add(itemHashMap);

        //添加两个场景，吃饭，和睡觉场景
        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_scene_chifan_active);
        itemHashMap.put("title_room", "吃饭");
        itemHashMap.put("device_name", "卧室开关");
        itemHashMap.put("device_action", "");
        deviceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_scene_shuijiao_active);
        itemHashMap.put("title_room", "睡觉");
        itemHashMap.put("device_name", "卧室开关");
        itemHashMap.put("device_action", "");
        deviceList.add(itemHashMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.project_select:
                showPopWindow();
                break;//项目选择
            case R.id.add_device://为网关添加设备(即添加面板)
                startActivity(new Intent(getActivity(), SelectZigbeeDeviceActivity.class));
                break;
            case R.id.common_setting_linear://设为常用
//                common_setting_image.setImageResource(R.drawable.icon_l_changyong);
                Map map = getmap_chage_room();
                switch_room_dialog(getActivity(), map);
                break;
            case R.id.rename_scene_linear://重命名
                showRenameDialog();
                break;
            case R.id.delete_scene_linear://删除
                showCenterDeleteDialog();
                break;
            case R.id.cancel_scene_linear://取消
                dialogUtil.removeviewBottomDialog();
                break;
            case R.id.back_rel://设备消息(1)
                startActivity(new Intent(getActivity(), AlarmDeviceMessageActivity.class));
                break;
            case R.id.all_room_rel:// 获取全部信息（APP->网关）,sraum_getInfos所有设备，所有房间标按妞下的所有设备

                break;
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

//    /**
//     * 获取全部信息（APP->网关）
//     *
//     * @param refresh
//     * @param device_type
//     */
//    private void sraum_getInfos(final boolean refresh, final String device_type) {
//        Map map = new HashMap();
//        map.put("command", "sraum_getInfos");
////        MyOkHttp.postMapString(ApiHelper.sraum_getInfos, map,
////                new MyStringcallback(getActivity()) {
////                    @Override
////                    public void onSuccess(String result) {
////                        //解析
////                        JSONObject json = null;
////                        try {
////                            json = new JSONObject(result);
////                            if (null != json) {
//////                                json.getString("appid");
////                                JSONArray jsonArray = json.getJSONArray("list");
////                                if (jsonArray == null) return;
////                                singleRoomAssoList = new ArrayList<>();
////                                for (int i = 0; i < jsonArray.length(); i++) {
////                                    singleRoomAssoList.add(detail_room_select(jsonArray.getJSONObject(i)));
////                                }
////
////                                if (refresh) {
////                                    detailDeviceHomeAdapter = new DetailDeviceHomeAdapter(getActivity()
////                                            , singleRoomAssoList);
////                                    mDragGridView.setAdapter(detailDeviceHomeAdapter);
////                                } else {
////                                    detailDeviceHomeAdapter.setList(singleRoomAssoList);
////                                    detailDeviceHomeAdapter.notifyDataSetChanged();
////                                    broadcast_refresh_second_page(device_type);
////                                }
////                            }
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                });
//    }

    //自定义dialog,centerDialog
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
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.editscene_dialog, null);
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
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getAllArea();
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
                        areaList = new ArrayList<>();
                        for (int i = 0; i < user.areaList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.areaList.get(i).name);
                            mapdevice.put("number", user.areaList.get(i).number);
                            mapdevice.put("sign", user.areaList.get(i).sign);
                            mapdevice.put("authType", user.areaList.get(i).authType);
                            areaList.add(mapdevice);
                        }

                        if (user.areaList != null) {//区域命名
                            project_select.setText(user.areaList.get(0).name);
                            //，加载默认区域下默认房间
                            sraum_getRoomsInfo(user.areaList.get(0).number);
                        }
                    }
                });
    }

    /**
     * 切换区域
     */
    private void sraum_changeArea(final String areaNumber) {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_changeArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_changeArea(areaNumber);
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
                        ToastUtil.showToast(getActivity(), "区域切换成功");
                        //qie huan cheng gong ,获取区域的所有房间信息
                        sraum_getRoomsInfo(areaNumber);
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
     */
    private void sraum_getRoomsInfo(final String areaNumber) {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(getActivity()));
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getRoomsInfo(areaNumber);
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
                        ToastUtil.showToast(getActivity(), "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void onSuccess(final User user) {
                        //qie huan cheng gong ,获取区域的所有房间信息
                        roomList = new ArrayList<>();
                        for (int i = 0; i < user.roomList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.roomList.get(i).name);
                            mapdevice.put("number", user.roomList.get(i).number);
                            mapdevice.put("count", user.roomList.get(i).count);
                            roomList.add(mapdevice);
                        }
                        display_room_list();
                        //加载默认房间下的设备列表
                        if (roomList.size() != 0) {
                            sraum_getOneRoomInfo(roomList.get(0).get("number").toString());
                        }
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
    private void display_room_list() {
        homeDeviceListAdapter.setList1(roomList);
        homeDeviceListAdapter.notifyDataSetChanged();
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.popup_listview_window, null);

            ListViewAdaptWidth wv = (ListViewAdaptWidth) view.findViewById(R.id.wheel_view_wv);

           List<String> data = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
               data.add("小区yihao" + i);
            }

//            final String[] data = new String[areaList.size()];
//            for (int i = 0; i < areaList.size(); i++) {
//                data[i] = areaList.get(i).get("name").toString();
//            }
            AreaListAdapter areaListAdapter = new AreaListAdapter(getActivity(),data);
//            ArrayAdapter<String> array = new ArrayAdapter<>(getActivity(),
//                    R.layout.simple_expandable_list_item_new, data);
            wv.setAdapter(areaListAdapter);
            wv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //xuan zhong mou ge quyu
                    current_area_map = areaList.get(position);
                    sraum_changeArea(areaList.get(position).get("areaNumber").toString());
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
        project_select.getLocationOnScreen(location);//获得textview的location位置信息，绝对位置
        popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
        backgroundAlpha(1.0f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
        // 将pixels转为dip
//            int xoffInDip = pxTodip(displayWidth) / 2;
        popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xPos = (displayWidth - popupWindow.getContentView().getMeasuredWidth()) / 2;
//            popupWindow.showAsDropDown(project_select, 0, dip2px(getActivity(), 10));
        popupWindow.showAtLocation(project_select, Gravity.NO_GRAVITY, xPos, location[1] + project_select.getHeight() * 3 / 2);
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
        ToastUtil.showToast(getActivity(), "position:" + position);
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
}


