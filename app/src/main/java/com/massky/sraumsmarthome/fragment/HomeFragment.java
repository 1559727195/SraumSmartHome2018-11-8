package com.massky.sraumsmarthome.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import com.andview.refreshview.XRefreshView;
import com.google.gson.GsonBuilder;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.User;
import com.massky.sraumsmarthome.Util.DialogUtil;
import com.massky.sraumsmarthome.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraumsmarthome.Utils.ApiHelper;
import com.massky.sraumsmarthome.Utils.MyOkHttp;
import com.massky.sraumsmarthome.Utils.MyStringcallback;
import com.massky.sraumsmarthome.Utils.Mycallback;
import com.massky.sraumsmarthome.activity.AirControlActivity;
import com.massky.sraumsmarthome.activity.AlarmDeviceMessageActivity;
import com.massky.sraumsmarthome.activity.CurtainWindowActivity;
import com.massky.sraumsmarthome.activity.DeviceSettingDelRoomActivity;
import com.massky.sraumsmarthome.activity.Pm25Activity;
import com.massky.sraumsmarthome.activity.SelectZigbeeDeviceActivity;
import com.massky.sraumsmarthome.activity.TiaoGuangLightActivity;
import com.massky.sraumsmarthome.adapter.DetailDeviceHomeAdapter;
import com.massky.sraumsmarthome.adapter.HomeDeviceListAdapter;
import com.massky.sraumsmarthome.base.BaseFragment1;
import com.massky.sraumsmarthome.event.MyDialogEvent;
import com.massky.sraumsmarthome.receiver.ApiTcpReceiveHelper;
import com.massky.sraumsmarthome.receiver.LocalBroadcastManager;
import com.massky.sraumsmarthome.service.MyService;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.massky.sraumsmarthome.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2017/11/30.
 */

public class HomeFragment extends BaseFragment1 {
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
    private DetailDeviceHomeAdapter detailDeviceHomeAdapter;
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


    @Override
    protected void onData() {
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


//        homeDeviceListAdapter = new HomeDeviceListAdapter(getActivity(), list_homedev_items, new HomeDeviceListAdapter.HomeDeviceItemClickListener() {
//            @Override
//            public void homedeviceClick() {//刷新
//
//            }
//        });
//
//        home_listview.setAdapter(homeDeviceListAdapter);//设备侧栏列表
        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) singleRoomAssoList.get(position).get("type");
                String status = (String) singleRoomAssoList.get(position).get("status");
                String number = (String) singleRoomAssoList.get(position).get("number");

//
//                Map map_item = singleRoomAssoList.get(position);
              final  Map map = new HashMap();
                map.put("command", "sraum_getOneInfo");
                map.put("type", type);
                map.put("number", number);
                switch (type) {
                    case "12"://pm2.5
                        MyOkHttp.postMapString(ApiHelper.sraum_getOneInfo, map,
                                new MyStringcallback(getActivity()) {
                                    @Override
                                    public void onSuccess(String result) {
                                        //解析
                                        JSONObject json = null;
                                        try {
                                            json = new JSONObject(result);
                                            if (null != json) {
//                                                json.getString("appid");
//                                                JSONArray jsonArray = json.getJSONArray("list");
                                                //
//                                                String number = json.getString("number");
                                                String name = json.getString("name");
                                                String status = json.getString("status");
                                                String pm25 = json.getString("pm2.5");
                                                String pm1_0 = json.getString("pm1.0");
                                                String pm10 = json.getString("pm10");
                                                String temperature = json.getString("temperature");
                                                String humidity = json.getString("humidity");

                                                map.put("name",name);
                                                map.put("status",status);
                                                map.put("pm25",pm25);
                                                map.put("pm1.0",pm1_0);
                                                map.put("pm10",pm10);
                                                map.put("temperature",temperature);
                                                map.put("humidity",humidity);

//
//                                                startActivity(new Intent(getActivity(),
//                                                        Pm25Activity.class));
//
                                                Intent intent_curtain = new Intent(getActivity(),
                                                        Pm25Activity.class);
                                                intent_curtain.putExtra("map_item", (Serializable) map);
                                                startActivity(intent_curtain);

                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                });

                        break;
                    default:
                        //获取单个设备或是按钮信息（APP->网关）
                        get_single_device_info(map);
                        break;
                }
            }
        });

        mDragGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (singleRoomAssoList.size() > 0)
                    deivce_number = (String) singleRoomAssoList.get(position).get("number");//设备编号
                //这句是选中某个设备
                switch (roomNumber) {
                    case "":
                        common_setting_linear.setVisibility(View.GONE);
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

    /**
     * 获取单个设备或是按钮信息（APP->网关）
     *
     * @param map
     */
    private void get_single_device_info(final Map map) {
        MyOkHttp.postMapObject(ApiHelper.sraum_getOneInfo, map,
                new Mycallback(getActivity()) {
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

    public void switch_room_dialog(final Context context) {

//        dialogUtil.loadDialog();
        View view = LayoutInflater.from(context).inflate(R.layout.credit_card_num_pop, null);
//        TextView confirm; //确定按钮
////        final TextView content; //内容
////        confirm = (TextView) view.findViewById(R.id.dialog_btn_comfirm);
////        content = (TextView) view.findViewById(R.id.dialog_txt_content);
////        content.setText(message);
//        ImageView dianti_more = (ImageView) view.findViewById(R.id.dianti_more);
//        dianti_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showLouCengDialog(getActivity(),"");
//            }
//        });
        final RelativeLayout xiala_shuaka_rel = (RelativeLayout) view.findViewById(R.id.xiala_shuaka_rel);
        final ImageView btn_xiala_img = (ImageView) view.findViewById(R.id.btn_xiala_img);
        final TextView xiala_shuaka_txt = (TextView) view.findViewById(R.id.xiala_shuaka_txt);
        Button forget_shuaka_btn = (Button) view.findViewById(R.id.forget_shuaka_btn);
        Button confirm_shuaka_btn = (Button) view.findViewById(R.id.confirm_shuaka_btn);
        ListView room_select = (ListView) view.findViewById(R.id.room_select);

        final List<String> roomNames = new ArrayList<>();
        for (int i = 0; i < roomsInfos.size(); i++) {
            roomNames.add((String) roomsInfos.get(i).get("name"));
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
        for (int i = 0; i < roomsInfos.size(); i++) {
            if (name.equals(roomsInfos.get(i).get("name"))) {
                roomNumber = (String) roomsInfos.get(i).get("number");
                break;
            }
        }

        //deivce_number,roomNumber;
        //修改房间关联信息（APP->网关）
        Map map = new HashMap();
        map.put("command", "sraum_updateRoomInfo");
        map.put("roomNumber", roomNumber);
        map.put("number", deivce_number);
        MyOkHttp.postMapObject(ApiHelper.sraum_updateRoomInfo, map,
                new Mycallback(getActivity()) {
                    @Override
                    public void onSuccess(User user) {
//                        project_select.setText(user.name);
                        //刷新某个房间下的所有设备
                        switch (roomNumber) {
                            case "":
                                sraum_getInfos(false, device_type);//获取全部信息（APP->网关）
                                break;
                            default:
                                sraum_getOneRoomInfo(roomNumber, false, device_type);
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
        detailDeviceHomeAdapter = new DetailDeviceHomeAdapter(getActivity()
                , dataSourceList);
        mDragGridView.setAdapter(detailDeviceHomeAdapter);
        get_gateway_name();
        get_allroominfo();
        addBrodcastAction();//添加TCP接收广播通知
    }

    /**
     * 添加TCP接收广播通知
     * // add Action1
     */
    private void addBrodcastAction() {
        //推送修改网关名称（网关->APP）
        push_gatewayName();
        push_sraum_controlButton();
    }

    /**
     * 接收jpush推送回来的控制按钮（APP->网关）
     */
    private void push_sraum_controlButton() {
        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.Sraum_Control_Button), new OnActionResponse() {

            @Override
            public void onResponse(Intent intent) {
                String tcpreceiver = intent.getStringExtra("strcontent");
//                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
                //解析json数据
                final User user = new GsonBuilder().registerTypeAdapterFactory(
                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
                if (user == null) return;
                device_type = user.type;//设备类型
                switch (roomNumber) {
                    case "":
                        sraum_getInfos(false, device_type);//获取全部信息（APP->网关）
                        break;
                    default:
                        sraum_getOneRoomInfo(roomNumber, false, device_type);
                        break;
                }
            }
        });
    }


    /**
     * 广播刷新第二个界面
     *
     * @param context
     * @param action
     * @param strcontent
     */
    private void processCustomMessage(Context context
            , String action, String strcontent) {

        Intent msgIntent = new Intent(action);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
        msgIntent.putExtra("result", strcontent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    }


    /**
     * 广播刷新第二个界面
     *
     * @param device_type
     */
    private void broadcast_refresh_second_page(final String device_type) {
        //发送广播，通知最新列表更新

        switch (device_type) {
            case "1":
                //更新数据

                break;//灯
            case "2":
                processCustomMessage(getActivity(), ApiTcpReceiveHelper.TIAO_GUANG_RECEIVE_ACTION, "100");
                break;//调光灯
            case "3":
                processCustomMessage(getActivity(), ApiTcpReceiveHelper.CURTAIN_RECEIVE_ACTION, "100");
                break;//3-窗帘
            case "4":
                processCustomMessage(getActivity(), ApiTcpReceiveHelper.AIRCONTROL_RECEIVE_ACTION, "100");
                break;//4-空调
            case "5":

                break;//5-地暖
            case "6":

                break;//6-新风
            case "7":

                break;//7-门磁
            case "8":

                break;//8-人体感应
            case "9":

                break;//9-红外转发
            case "10"://10-燃气

                break;
        }
    }


    /**
     * 推送修改网关名称（网关->APP）
     */
    private void push_gatewayName() {
        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.Sraum_PushGateWayName), new OnActionResponse() {

            @Override
            public void onResponse(Intent intent) {
                String tcpreceiver = intent.getStringExtra("strcontent");
//                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
                //解析json数据
                final User user = new GsonBuilder().registerTypeAdapterFactory(
                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
                if (user == null) return;
                if (user.newName != null) {
                    project_select.setText(user.newName.toString());
                }
            }
        });
    }

    private void get_gateway_name() {
        //获取网关名称（APP->网关）
        Map map = new HashMap();
        map.put("command", "sraum_getGatewayName");
        MyOkHttp.postMapObject(ApiHelper.sraum_getGatewayName, map,
                new Mycallback(getActivity()) {
                    @Override
                    public void onSuccess(User user) {
                        project_select.setText(user.name);
                    }
                });
    }

    private void get_allroominfo() {
        //获取网关名称（APP->网关）
        Map map = new HashMap();
        map.put("command", "sraum_getRoomsInfo");

        MyOkHttp.postMapObject(ApiHelper.sraum_getRoomsInfo, map,
                new Mycallback(getActivity()) {
                    @Override
                    public void onSuccess(User user) {
//                        project_select.setText(user.name);
                        roomsInfos = new ArrayList<>();
                        int all_index = 0;
                        for (int i = 0; i < user.roomList.size(); i++) {
                            Map map = new HashMap();
                            map.put("number", user.roomList.get(i).number);
                            map.put("name", user.roomList.get(i).name);
                            map.put("count", user.roomList.get(i).count);
                            map.put("is_select", "0");
                            roomsInfos.add(map);
                            int index = Integer.parseInt(user.roomList.get(i).count);
                            all_index += index;
                        }  //

                        //添加头全部所有房间综合，
                        Map map_all = new HashMap();
                        map_all.put("number", "");
                        map_all.put("name", "全部");
                        map_all.put("count", "" + all_index);
                        map_all.put("is_select", "0");
                        roomsInfos.set(0, map_all);
                        homeDeviceListAdapter = new HomeDeviceListAdapter(getActivity(), roomsInfos, new HomeDeviceListAdapter.HomeDeviceItemClickListener() {
                            @Override
                            public void homedeviceClick(String number) {//获取单个房间关联信息（APP->网关）
                                switch (number) {
                                    case "":
                                        roomNumber = "";
                                        sraum_getInfos(true, device_type);//获取全部信息（APP->网关）
                                        break;
                                    default:
                                        roomNumber = number;
                                        sraum_getOneRoomInfo(number, true, device_type);
                                        break;
                                }
                            } //
                        });
                        home_listview.setAdapter(homeDeviceListAdapter);//设备侧栏列表
                    }
                });
    } //

    /**
     * 获取单个房间关联信息（APP->网关）
     *
     * @param number
     * @param refresh
     * @param device_type
     */
    private void sraum_getOneRoomInfo(String number, final boolean refresh, final String device_type) { //
        Map map = new HashMap();
        map.put("command", "sraum_getOneRoomInfo");
        map.put("roomNumber", number);
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo, map,
                new MyStringcallback(getActivity()) {
                    @Override
                    public void onSuccess(String result) {
                        //解析
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            if (null != json) {
//                                json.getString("appid");
                                JSONArray jsonArray = json.getJSONArray("list");
                                if (jsonArray == null) return;
                                singleRoomAssoList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    singleRoomAssoList.add(detail_room_select(jsonArray.getJSONObject(i)));
                                }

                                if (refresh) {
                                    detailDeviceHomeAdapter = new DetailDeviceHomeAdapter(getActivity()
                                            , singleRoomAssoList);
                                    mDragGridView.setAdapter(detailDeviceHomeAdapter);
                                } else {
                                    detailDeviceHomeAdapter.setList(singleRoomAssoList);
                                    detailDeviceHomeAdapter.notifyDataSetChanged();
                                    broadcast_refresh_second_page(device_type);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 各个房间的详细配置和其他的设备详情
     *
     * @param json
     * @throws JSONException
     */
    private Map detail_room_select(JSONObject json) throws JSONException {
        Map map = new HashMap();
        String type = json.getString("type");
        if (type != null) {
            map.put("type", type);
        }

        String online = json.getString("online");
        if (online != null) {
            map.put("online", online);
        }

        String number = json.getString("number");
        if (number != null) {
            map.put("number", number);
        }

        String name = json.getString("name");
        if (name != null) {
            map.put("name", name);
        }

        String status = json.getString("status");
        if (status != null) {
            map.put("status", status);
        }

        String fatherName = json.getString("fatherName");
        if (fatherName != null) {
            map.put("fatherName", fatherName);
        }

        String mode = json.getString("mode");
        if (mode != null) {
            map.put("mode", mode);
        }

        String temperature = json.getString("temperature");
        if (temperature != null) {
            map.put("temperature", temperature);
        }

        String speed = json.getString("speed");
        if (speed != null) {
            map.put("speed", speed);
        }

        String electricity = json.getString("electricity");
        if (electricity != null) {
            map.put("electricity", electricity);
        }

        String vlaue = json.getString("vlaue");
        if (vlaue != null) {
            map.put("vlaue", vlaue);
        }

        String pm25 = json.getString("pm2.5");
        if (pm25 != null) {
            map.put("pm2.5", pm25);
        }

        String pm1 = json.getString("pm1.0");
        if (pm1 != null) {
            map.put("pm1.0", pm1);
        }

        String pm10 = json.getString("pm10");
        if (pm10 != null) {
            map.put("pm10", pm10);
        }

        String humidity = json.getString("humidity");
        if (humidity != null) {
            map.put("humidity", humidity);
        }

        String alarm = json.getString("alarm");
        if (alarm != null) {
            map.put("alarm", alarm);
        }

        String roomNumber = json.getString("roomNumber");
        if (roomNumber != null) {
            map.put("roomNumber", roomNumber);
        }

        String dimmer = json.getString("dimmer");
        if (dimmer != null) {
            map.put("dimmer", dimmer);
        }

        String roomName = json.getString("roomName");
        if (roomName != null) {
            map.put("roomName", roomName);
        }

        return map;
    }


    /**
     * 初始化grideview -data
     */
    private void init_grideview_data() {
        dataSourceList = new ArrayList<>();
        HashMap<String, Object> itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_kongtiao_active);
        itemHashMap.put("title_room", "客厅");
        itemHashMap.put("device_name", "空调");
        itemHashMap.put("device_action", "制冷|22|底风");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_pm25_active);
        itemHashMap.put("title_room", "客厅");
        itemHashMap.put("device_name", "PM检测");
        itemHashMap.put("device_action", "50 良");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_chuanglian_active);
        itemHashMap.put("title_room", "客厅");
        itemHashMap.put("device_name", "客厅窗帘");
        itemHashMap.put("device_action", "打开");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_menci_active);
        itemHashMap.put("title_room", "南卧室");
        itemHashMap.put("device_name", "门磁");
        itemHashMap.put("device_action", "离线");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_deng_active);
        itemHashMap.put("title_room", "客厅");
        itemHashMap.put("device_name", "主灯");
        itemHashMap.put("device_action", "客厅开关");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_deng_active);
        itemHashMap.put("title_room", "卧室");
        itemHashMap.put("device_name", "调光灯");
        itemHashMap.put("device_action", "卧室开关");
        dataSourceList.add(itemHashMap);

        //添加两个场景，吃饭，和睡觉场景
        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_scene_chifan_active);
        itemHashMap.put("title_room", "吃饭");
        itemHashMap.put("device_name", "卧室开关");
        itemHashMap.put("device_action", "");
        dataSourceList.add(itemHashMap);

        itemHashMap = new HashMap<>();
        itemHashMap.put("item_image", R.drawable.icon_scene_shuijiao_active);
        itemHashMap.put("title_room", "睡觉");
        itemHashMap.put("device_name", "卧室开关");
        itemHashMap.put("device_action", "");
        dataSourceList.add(itemHashMap);
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
                switch_room_dialog(getActivity());
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
     * 获取全部信息（APP->网关）
     *
     * @param refresh
     * @param device_type
     */
    private void sraum_getInfos(final boolean refresh, final String device_type) {
        Map map = new HashMap();
        map.put("command", "sraum_getInfos");
        MyOkHttp.postMapString(ApiHelper.sraum_getInfos, map,
                new MyStringcallback(getActivity()) {
                    @Override
                    public void onSuccess(String result) {
                        //解析
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            if (null != json) {
//                                json.getString("appid");
                                JSONArray jsonArray = json.getJSONArray("list");
                                if (jsonArray == null) return;
                                singleRoomAssoList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    singleRoomAssoList.add(detail_room_select(jsonArray.getJSONObject(i)));
                                }

                                if (refresh) {
                                    detailDeviceHomeAdapter = new DetailDeviceHomeAdapter(getActivity()
                                            , singleRoomAssoList);
                                    mDragGridView.setAdapter(detailDeviceHomeAdapter);
                                } else {
                                    detailDeviceHomeAdapter.setList(singleRoomAssoList);
                                    detailDeviceHomeAdapter.notifyDataSetChanged();
                                    broadcast_refresh_second_page(device_type);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

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
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.popupwindow, null);
            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            ColorDrawable cd = new ColorDrawable(0x00ffffff);// 背景颜色全透明
            popupWindow.setBackgroundDrawable(cd);
            int[] location = new int[2];
            project_select.getLocationOnScreen(location);//获得textview的location位置信息，绝对位置
            popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(1.0f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            popupWindow.showAsDropDown(project_select, 0, dip2px(getActivity(), 10));
            //popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
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
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
}


