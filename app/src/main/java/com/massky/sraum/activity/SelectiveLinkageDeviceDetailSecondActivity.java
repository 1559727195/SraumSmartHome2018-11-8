package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.SelectLinkageItemSecondAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.PullToRefreshLayout;
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
 * Created by zhu on 2018/1/5.
 */

public class SelectiveLinkageDeviceDetailSecondActivity extends BaseActivity implements AdapterView.OnItemClickListener
        , PullToRefreshLayout.OnRefreshListener {

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
    @InjectView(R.id.panel_txt_promat)
    TextView panel_txt_promat;
    @InjectView(R.id.project_select)
    TextView project_select;
    private DialogUtil dialogUtil;
    private String panelNumber;
    private List<User.device> deviceList = new ArrayList<>();
    private List<Map> deviceActionList = new ArrayList<>();//执行动作集合
    private ArrayList<Map> list_type_index = new ArrayList<>();//收集某种设备类型的索引集合
    private SelectLinkageItemSecondAdapter selectexcutesceneresultadapter;
    private List<Map> listint = new ArrayList<>();
    private List<Map> listintwo = new ArrayList<>();
    private String panelType;
    private List<Map> list_map = new ArrayList<>();
    private String panelName;
    private Map sensor_map = new HashMap();//传感器map
    private String boxName;
    private String boxNumber;
    private String panelType1;
    private String panelName1;
    private String panelMAC;
    private String gatewayMAC;

    /**
     * 空调
     *
     * @return
     */

    @Override
    protected int viewId() {
        return R.layout.selective_link_devdetail_second;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        panelNumber = (String) getIntent().getSerializableExtra("panelNumber");
        panelType = (String) getIntent().getSerializableExtra("panelType");
        panelName = (String) getIntent().getSerializableExtra("panelName");
        sensor_map = (Map) getIntent().getSerializableExtra("sensor_map");
        //  intent.putExtra("boxName", (Serializable) listbox.get(position));
        boxName = (String) getIntent().getSerializableExtra("boxName");
        boxNumber = (String) getIntent().getSerializableExtra("boxNumber");
        gatewayMAC =  (String) getIntent().getSerializableExtra("gatewayMac");
        if (panelName != null) project_select.setText(panelName);
        getData(true);
        selectexcutesceneresultadapter = new SelectLinkageItemSecondAdapter(SelectiveLinkageDeviceDetailSecondActivity.this,
                list_map);
        maclistview_id.setAdapter(selectexcutesceneresultadapter);
        maclistview_id.setOnItemClickListener(this);
//        refresh_view.autoRefresh();
        refresh_view.noreleasePull();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectiveLinkageDeviceDetailSecondActivity.this.finish();
                break;
        }
    }

    private void getData(boolean flag) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(SelectiveLinkageDeviceDetailSecondActivity.this));
        String areaNumber = (String) SharedPreferencesUtil.getData(SelectiveLinkageDeviceDetailSecondActivity.this, "areaNumber", "");
        map.put("boxNumber", boxNumber);
        map.put("panelNumber", panelNumber);
        map.put("areaNumber", areaNumber);
        if (flag) {
            dialogUtil.loadDialog();
        }
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(false);
                    }
                }, SelectiveLinkageDeviceDetailSecondActivity.this, dialogUtil) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
//                        refresh_view.stopRefresh();

//                        adapter = new PanelAdapter(SelectiveLinkageActivity.this, panelList, mySlidingMenu, accountType
//                                , new PanelAdapter.PanelAdapterListener() {
//                            @Override
//                            public void panel_adapter_listener() {//删除item时刷新界面
//                                getData(false);
//                            }
//                        });

                        panelType = user.panelType;
                        panelName = user.panelName;
                        panelMAC    = user.panelMAC;
                        gatewayMAC = user.gatewayMAC;
                        deviceList.clear();
                        deviceActionList.clear();
                        list_type_index.clear();

                        for (int position = 0; position < user.deviceList.size(); position++) {
                            Map<String, Object> mapdevice = new HashMap<>();
                            Map<String, Object> type_index = new HashMap<>();
                            mapdevice.put("type", user.deviceList.get(position).type);
                            mapdevice.put("number", user.deviceList.get(position).number);
                            mapdevice.put("status", user.deviceList.get(position).status);
                            mapdevice.put("dimmer", user.deviceList.get(position).dimmer);
                            mapdevice.put("mode", user.deviceList.get(position).mode);
                            mapdevice.put("temperature", user.deviceList.get(position).temperature);
                            mapdevice.put("speed", user.deviceList.get(position).speed);
                            mapdevice.put("name", user.deviceList.get(position).name);
                            mapdevice.put("boxName", boxName);
                            mapdevice.put("button", user.deviceList.get(position).button);
                            mapdevice.put("panelName", user.deviceList.get(position).panelName);
                            mapdevice.put("boxNumber", boxNumber == null ? "" : boxNumber);
                            type_index.put("type", user.deviceList.get(position).type);
                            type_index.put("index", position);
                            list_type_index.add(type_index);
                            deviceActionList.add(mapdevice);
                        }
                        deviceList.addAll(user.deviceList);
                        setPicture(panelType);
                        selectexcutesceneresultadapter.setlist(list_map);
                        selectexcutesceneresultadapter.notifyDataSetChanged();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }


    private void setPicture(String type) {
        switch (type) {
            case "A201":
            case "A202":
            case "A203":
            case "A204":
                light();
                panel_txt_promat.setText("执行开关");
                break;
            case "A301":
            case "A302":
            case "A303":
            case "A311":
            case "A312":
            case "A313":
            case "A321":
            case "A322":
            case "A331":
                tiaoguang_light();
                panel_txt_promat.setText("执行开关");
                break;
            case "A401":
            case "A411":
            case "A412":
            case "A413":
            case "A414":
                curtain_window();
                panel_txt_promat.setText("执行开关");
                break;
            case "A501":// 501是空调面板，511是空调模块跳过去是空调列表，
                //直接跳转到空调设备界面，

                break;
            case "A511":
                panel_txt_promat.setText("选择空调");
                air_control_list(type);
                break;
            case "A611":
                panel_txt_promat.setText("选择新风");
                air_control_list(type);
                break;
            case "A711":
                panel_txt_promat.setText("选择地暖");
                air_control_list(type);
                break;
            case "A801":

                break;
            case "A901":

                break;
            case "A902":

            case "AB01":

                break;
            case "AB04":

                break;
            case "AC01":

                break;
            case "AD01":

                break;
            case "AD02":

                break;
            case "B001":

                break;

            case "B101":
                panel_txt_promat.setText("执行开关");
//                air_control_list(type);
                chazuo_door();
                break;
            case "B201":
                panel_txt_promat.setText("选择智能门锁");
                air_control_list(type);
                break;
            case "B301":
                panel_txt_promat.setText("选择机械手");
                air_control_list(type);
                break;

        }
//        ToastUtil.showToast(SelectiveLinkageDeviceDetailSecondActivity.this, "list_map:" + list_map.size());
    }

    /**
     * 空调控制
     *
     * @param type
     */
    private void air_control_list(String type) {

        for (int position = 0; position < deviceActionList.size(); position++) {
            Map map = new HashMap();
            map.put("type", deviceActionList.get(position).get("type"));
            map.put("number", deviceActionList.get(position).get("number"));
            map.put("status", deviceActionList.get(position).get("status"));
            map.put("dimmer", deviceActionList.get(position).get("dimmer"));
            map.put("mode", deviceActionList.get(position).get("mode"));
            map.put("temperature", deviceActionList.get(position).get("temperature"));
            map.put("speed", deviceActionList.get(position).get("speed"));
            map.put("boxName", deviceActionList.get(position).get("boxName"));
            switch (type) {
                case "A511":
                case "A611":
                case "A711":
                    map.put("name", deviceActionList.get(position).get("name"));
                    break;
//                case "B101":
                case "B201":
                    map.put("name", deviceActionList.get(position).get("name") + "打开");
                    map.put("name1", deviceActionList.get(position).get("name"));

                    break;
                case "B301":
                    map.put("name", deviceActionList.get(position).get("name") + "关闭");
                    map.put("name1", deviceActionList.get(position).get("name"));
                    break;

            }
            map.put("boxName", deviceActionList.get(position).get("boxName"));
            list_map.add(map);
        }

        selectexcutesceneresultadapter.setlist(list_map);
        selectexcutesceneresultadapter.notifyDataSetChanged();
    }

    /**
     * 窗帘控制
     */
    private void curtain_window() {
        //窗帘控制
        for (int i = 0; i < deviceActionList.size(); i++) {

            switch (deviceActionList.get(i).get("type").toString()) {
                case "1":
                    common("", deviceActionList.get(i).get("name").toString(), "1", i);
                    break;
                case "4":
                    common("", "内纱", "4", i);
                    common("", "外纱", "4", i);
                    common("", "全部", "4", i);
                    break;
                case "18":
                    common("", deviceActionList.get(i).get("name").toString(), "18", i);
                    break;
            }
        }
    }

    /**
     * 调光灯
     */
    private void tiaoguang_light() {
        for (int i = 0; i < deviceActionList.size(); i++) {

            switch (deviceActionList.get(i).get("type").toString()) {
                case "1":
                    common("第" + (i + 1) + "路灯控", deviceActionList.get(i).get("name").toString(), "1", i);
                    break;
                case "2":
                    common("第" + (i + 1) + "路调光灯带", deviceActionList.get(i).get("name").toString(), "2", i);
                    break;
            }
        }
    }


    /**
     * 入墙式智能插座
     */
    private void chazuo_door() {
        for (int i = 0; i < deviceActionList.size(); i++) {

            switch (deviceActionList.get(i).get("type").toString()) {
                case "17":
                    common("", deviceActionList.get(i).get("name").toString(), "17", i);
                    break;
            }
        }
    }


    /**
     * common
     *
     * @param
     * @param
     * @param i
     */
    private void common(String tabname, String name, String type, int i) {//"第" + (i + 1) + content

        switch (name) {
            case "全部":
                common_second(tabname, name, type, "1", "0", i);
                break;
            case "内纱":
                common_second(tabname, name, type, "4", "6", i);
                break;
            case "外纱":
                common_second(tabname, name, type, "8", "7", i);
                break;
            default:
                common_second(tabname, name, type, "1", "0", i);
                break;
        }
    }

    /**
     * 设备开关状态status字段
     *
     * @param tabname
     * @param name
     * @param type
     * @param value2
     * @param value4
     * @param position
     */
    private void common_second(String tabname, String name, String type, String value2, String value4, int position) {
        for (int j = 0; j < 2; j++) {
            Map map = new HashMap();
            switch (j) {
                case 0://开
                    map.put("name", name + "打开");
                    map.put("status", value2);
                    if (type.equals("4")) {
                        map.put("action", name + "打开");
                    } else {
                        map.put("action", "打开");
                    }
                    break;
                case 1://关
                    map.put("name", name + "关闭");
                    map.put("status", value4);
//                    map.put("action", "关闭");
                    if (type.equals("4")) {
                        map.put("action", name + "关闭");
                    } else {
                        map.put("action", "关闭");
                    }
                    break;
            }

            if (type.equals("4")) {
                map.put("name1", "窗帘");
            } else {
                map.put("name1", name);
            }
            map.put("tabname", tabname);
            map.put("type", type);
            map.put("value", "");
            map.put("tiaoguang", "");
            map.put("position", position);
            map.put("number", deviceActionList.get(position).get("number"));
//            map.put("status", deviceActionList.get(position).get("status"));
            map.put("dimmer", deviceActionList.get(position).get("dimmer"));
            map.put("mode", deviceActionList.get(position).get("mode"));
            map.put("temperature", deviceActionList.get(position).get("temperature"));
            map.put("speed", deviceActionList.get(position).get("speed"));
            map.put("boxName", deviceActionList.get(position).get("boxName"));
//            map.put("name", deviceActionList.get(position).get("name"));
            list_map.add(map);
        }
    }

    /**
     * 普通灯
     */
    private void light() {
        //普通灯
        for (int i = 0; i < deviceActionList.size(); i++) {
            common("第" + (i + 1) + "路灯控", deviceActionList.get(i).get("name").toString(), "1", i);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (panelType) {
            case "A201":
            case "A202":
            case "A203":
            case "A204":
            case "A301":
            case "A302":
            case "A303":
            case "A311":
            case "A312":
            case "A313":
            case "A321":
            case "A322":
            case "A331":
            case "B101":
                back_onclick(position, view);
                break;
            case "A401"://窗帘的话
            case "A411"://
            case "A412"://
            case "A413"://
            case "A414"://
                back_onclick_window(position);
                break;
            case "A511"://列表
                send_map_to_second(list_map.get(position));
                break;
            case "A611":
                send_map_to_second(list_map.get(position));
                break;
            case "A711":
                send_map_to_second(list_map.get(position));
                break;
            case "A801":

                break;
            case "A901":

                break;
            case "A902":

            case "AB01":

                break;
            case "AB04":

                break;
            case "AC01":

                break;
            case "AD01":

                break;
            case "AD02":

                break;
            case "B001":

                break;
            case "B201":
//            case "B101":
                send_map_to_second(list_map.get(position));
                break;
            case "B301":
                send_map_to_second(list_map.get(position));
                break;
        }
    }

    /**
     * 窗帘设备点击回调
     *
     * @param position
     */
    private void back_onclick_window(int position) {
        Map map = list_map.get(position);
        int index_device = 0;
        for (int i = 0; i < deviceActionList.size(); i++) {
            if (map.get("type").toString().equals(deviceActionList.get(i).get("type"))) {
                index_device = i;
                break;
            }
        }
        switch (map.get("type").toString()) {
            case "1":
            case "2":
                send_map_to_second(list_map.get(position));
                break;
            case "3":

                break;
            case "4":
            case "18":
//                init_device_upgrade_action(index_device, "status", map.get("status").toString());
                send_map_to_second(list_map.get(position));
                break;
            case "5":

                break;
            case "6":

                break;
        }
    }

    /**
     * 点击之后的返回响应
     *
     * @param position
     * @param view
     */
    private void back_onclick(int position, View view) {
        Map map = list_map.get(position);
        switch (map.get("type").toString()) {
            case "1":
            case "17":
                light_and_tiaogaung(position, map);
                break;
            case "2":
                light_and_tiaogaung_second(position, map, view);
                break;
            case "3":

                break;
            case "4":

                break;
            case "5":

                break;
            case "6":

                break;
        }
    }

    /**
     * 针对调光灯带的打开下拉显示
     *
     * @param position
     * @param map
     * @param view
     */
    private void light_and_tiaogaung_second(int position, Map map, final View view) {

        switch (map.get("status").toString()) {
            case "1"://打开-》添加调光值数据，
//                String open = (String) view.getTag();
                String open = (String) list_map.get(position).get("tiaoguang");
                if (open != null) {
                    switch (open) {
                        case "open"://调光展开
                        default:
                            tiaoguang_open(position, map, view);
                            break;
                        case "close"://调光收缩
                            tiaoguang_close(position, map, view);
                            break;
                    }
                } else {//去执行，选择调光值

//                    Log.e("zhu", "value:" + list_map.get(position).get("value"));
                    list_map.get(position).put("dimmer", list_map.get(position).get("value"));
                    list_map.get(position).put("action", "调光值:" +
                            list_map.get(position).get("value"));//拼接调光值
                    //最终要选择发送的map的地方
                    send_map_to_second(list_map.get(position));
                }
                break;
            case "0"://调光关闭
//                Log.e("zhu", "value:" + list_map.get(position).get("value"));
//                list_map.get(position).put("dimmer", list_map.get(position).get("value"));
                send_map_to_second(list_map.get(position));
                break;
        }
    }

    /**
     * 最终发送位置
     *
     * @param map
     */
    private void send_map_to_second(Map map) {
        Intent intent = null;
//        //传感器参数
//        map.put("sensorType", sensor_map.get("sensorType"));
//        map.put("sensorId", sensor_map.get("sensorId"));
//        map.put("sensorName", sensor_map.get("sensorName"));
//        map.put("sensorCondition", sensor_map.get("sensorCondition"));
//        map.put("sensorMinValue", sensor_map.get("sensorMinValue"));
//        map.put("sensorMaxValue", sensor_map.get("sensorMaxValue"));
        if (panelNumber != null) {
            map.put("panelNumber", panelNumber);
        }
        if (panelMAC != null) {
            map.put("panelMac", panelMAC);
        }
        if (gatewayMAC != null) {
            map.put("gatewayMac", gatewayMAC);
        }
        switch (map.get("type").toString()) {
            case "3":
            case "5":
            case "6":
                intent = new Intent(
                        SelectiveLinkageDeviceDetailSecondActivity.this,
                        AirLinkageControlActivity.class
                );
                intent.putExtra("air_control_map", (Serializable) map);
                intent.putExtra("sensor_map", (Serializable) sensor_map);
                startActivity(intent);
                break;

            default:
                //
                AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity.class);
                AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
                intent = new Intent(
                        SelectiveLinkageDeviceDetailSecondActivity.this,
                        EditLinkDeviceResultActivity.class
                );
                intent.putExtra("device_map", (Serializable) map);
                intent.putExtra("sensor_map", (Serializable) sensor_map);
                startActivity(intent);
                SelectiveLinkageDeviceDetailSecondActivity.this.finish();
                break;
            case "15":
            case "16":
                init_intent(map);
                break;
        }
    }

    private void init_intent(Map air_control_map) {
        init_action(air_control_map);
        AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity.class);
        AppManager.getAppManager().finishActivity_current(SelectiveLinkageDeviceDetailSecondActivity.class);
        AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
        Intent intent = new Intent(
                SelectiveLinkageDeviceDetailSecondActivity.this,
                EditLinkDeviceResultActivity.class);
        intent.putExtra("device_map", (Serializable) air_control_map);
        intent.putExtra("sensor_map", (Serializable) sensor_map);
        SelectiveLinkageDeviceDetailSecondActivity.this.finish();
        startActivity(intent);
    }

    private void init_action(Map air_control_map) {
        switch (air_control_map.get("type").toString()) {
            case "15":
                air_control_map.put("action", "打开");
                air_control_map.put("status", "1");
                break;
            case "16":
                air_control_map.put("action", "关闭");
                air_control_map.put("status", "0");
                break;
//            case "17":
//                air_control_map.put("action", "打开");
//                air_control_map.put("status", "1");
//                break;
        }
    }

    /**
     * 调光灯关闭
     *
     * @param position
     * @param map
     * @param view
     */
    private void tiaoguang_close(int position, Map map, View view) {//关闭时，是应该关闭哪一个灯带的,告诉它这是第几路的灯带列表，
        List<Map> first = new ArrayList<>();
        List<Map> second = new ArrayList<>();
        list_map.get(position).put("tiaoguang", "open");
        Log.e("zhu", "position:" + position + ",open-close:open");
        for (int i = 0; i < position + 1; i++) {
            first.add(list_map.get(i));
        }
        for (int i = position + 6; i < list_map.size(); i++) {
            second.add(list_map.get(i));
        }
        list_map.clear();
        list_map.addAll(first);
        list_map.addAll(second);
        selectexcutesceneresultadapter.setlist(list_map);
        selectexcutesceneresultadapter.notifyDataSetChanged();
    }

    /**
     * 调光窗体展开
     *
     * @param position
     * @param map
     * @param view
     */
    private void tiaoguang_open(int position, Map map, View view) {
        List<Map> first = new ArrayList<>();
        list_map.get(position).put("tiaoguang", "close");
        Log.e("zhu", "position:" + position + ",open-close:close");
        for (int i = 0; i < 5; i++) {
            Map map_value = new HashMap();
            map_value.put("value", "" + (i + 1) * 20);
            map_value.put("name", map.get("name"));
            map_value.put("status", map.get("status"));
            map_value.put("tabname", map.get("tabname"));
            map_value.put("type", map.get("type"));
            map_value.put("name1", map.get("name1"));
            map_value.put("action", map.get("action"));

            map_value.put("number", map.get("number"));
            map_value.put("status", map.get("status"));
            map_value.put("dimmer", map.get("dimmer"));
            map_value.put("mode", map.get("mode"));
            map_value.put("temperature", map.get("temperature"));
            map_value.put("speed", map.get("speed"));
            map_value.put("boxName", map.get("boxName"));
//            map_value.put("name", deviceActionList.get(position).get("name"));
            first.add(map_value);
        }

        list_map.addAll(position + 1, first);
        selectexcutesceneresultadapter.setlist(list_map);
        selectexcutesceneresultadapter.notifyDataSetChanged();
    }

    /**
     * 调光下面的灯的status控制
     *
     * @param position
     * @param map
     */
    private void light_and_tiaogaung(int position, Map map) {
        int index_device = 0;
        if (position % 2 == 0) {
            index_device = position / 2;
        } else {
            index_device = (position - 1) / 2;
        }
//        init_device_upgrade_action(index_device, "status", map.get("status").toString());
        send_map_to_second(list_map.get(position));
    }

//    /**
//     * 更新执行设备的动作
//     */
//    private void init_device_upgrade_action(int position, String type, String content) {
////                mapdevice.put("type", doit == true ? content : deviceList.get(position).type);
//        deviceActionList.get(position).put(type, content);
////        deviceActionList.add(mapdevice);
//    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
//        refresh_view.noreleasePull();
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }
}
