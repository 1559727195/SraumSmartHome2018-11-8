package com.massky.sraum.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.EditLinkDeviceCondinationAndResultAdapter;
import com.massky.sraum.base.BaseActivity;
import com.pullableview.PullableRefreshListView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/7/3.
 */

public class EditLinkDeviceResultActivity extends BaseActivity {
    @InjectView(R.id.back)
    TextView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    //    @InjectView(R.id.maclistview_id)
//    ListView maclistview_id;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.maclistview_id_condition)
    ListView maclistview_id_condition;
    @InjectView(R.id.maclistview_id_result)
    ListView maclistview_id_result;
    @InjectView(R.id.result_add)
    TextView result_add;
    @InjectView(R.id.condition_add)
    TextView condition_add;
    @InjectView(R.id.sleep_time_rel)
    RelativeLayout sleep_time_rel;
    @InjectView(R.id.get_up_rel)
    RelativeLayout get_up_rel;
    //    @InjectView(R.id.sleep_time_txt)
//    TextView sleep_time_txt;
//    @InjectView(R.id.get_up_time_txt)
//    TextView get_up_time_txt;
    private String hourPicker;
    private String minutePicker;
    String time_content;
    @InjectView(R.id.start_time_txt)
    TextView start_time_txt;
    @InjectView(R.id.end_time_txt)
    TextView end_time_txt;
    @InjectView(R.id.time_select_linear)
    LinearLayout time_select_linear;

    private DialogUtil dialogUtil;
    private Map device_map = new HashMap();
    private Map sensor_map = new HashMap();//传感器map
    private EditLinkDeviceCondinationAndResultAdapter editLinkDeviceCondinationAndResultAdapter_condition;
    private EditLinkDeviceCondinationAndResultAdapter editLinkDeviceCondinationAndResultAdapter_result;
    private List<Map> list_condition = new ArrayList<>();
    private List<Map> list_result = new ArrayList<>();
    private String linkId = "";
    private Map link_information = new HashMap();
    private TimePickerView pvCustomTime;
    private int index_select;
    private String startTime = "";
    private String endTime = "";
    private String linkName = "";
    private List<Map> link_information_list = new ArrayList<>();//
    private boolean add_condition;
    private boolean link_first;
    private String type = "";

    private EditLinkDeviceCondinationAndResultAdapter editLinkDeviceCondinationAndResultAdapter_asscoted;
    private List<Map> list_asscoted;
    //    private  List<Map> list_link = new ArrayList<>();

    @Override
    protected int viewId() {
        return R.layout.edit_link_dev_result_act;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        onData1();
        onEvent1();
//        linkId = (String) SharedPreferencesUtil.getData(EditLinkDeviceResultActivity.this, "linkId", "");

//          SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "device_map",list_result);
//          SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "device_map");
//        ToastUtil.showToast(EditLinkDeviceResultActivity.this,"ss:" + ss);
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    void onEvent1() {
        editLinkDeviceCondinationAndResultAdapter_condition = new EditLinkDeviceCondinationAndResultAdapter(EditLinkDeviceResultActivity.this,
                list_condition, new EditLinkDeviceCondinationAndResultAdapter.ExcutecuteListener() {

            @Override
            public void excute_cordition() {
                condition_add.setVisibility(View.VISIBLE);
                list_condition = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_condition");
            }

            @Override
            public void excute_result() {

            }
        });

        maclistview_id_condition.setAdapter(editLinkDeviceCondinationAndResultAdapter_condition);
        editLinkDeviceCondinationAndResultAdapter_result = new EditLinkDeviceCondinationAndResultAdapter(EditLinkDeviceResultActivity.this,
                list_result, new EditLinkDeviceCondinationAndResultAdapter.ExcutecuteListener() {
            @Override
            public void excute_cordition() {

            }

            @Override
            public void excute_result() {
                list_result = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_result");
            }
        });
        maclistview_id_result.setAdapter(editLinkDeviceCondinationAndResultAdapter_result);
        back.setOnClickListener(this);
        result_add.setOnClickListener(this);
        condition_add.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);

        initCustomTimePicker();
        sleep_time_rel.setOnClickListener(this);
        get_up_rel.setOnClickListener(this);
        startTime = start_time_txt.getText().toString();
        endTime = end_time_txt.getText().toString();
    }

    void onData1() {
        link_information = (Map) getIntent().getSerializableExtra("link_information");
        link_information_list = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "link_information_list");
        if (link_information != null || link_information_list.size() != 0) {//来自联动列表的编辑按钮
//            list_result = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_result");
//            list_condition = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_condition");
            //获取接口的联动列表信息，设备联动信息，sraum_deviceLinkInfo，
//            linkId = (String) getIntent().getSerializableExtra("linkId");
            if (link_information != null) {
                link_information_list = new ArrayList<>();
                link_information_list.add(link_information);
                SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "link_information_list", link_information_list);
            } else if (link_information_list.size() != 0) {
                link_information = link_information_list.get(0);
            }
            next_step_txt.setText("保存");
            back.setText("返回");
//            time_select_linear.setVisibility(View.VISIBLE);
            linkId = (String) link_information.get("linkId");
            linkName = (String) link_information.get("linkName");
            startTime = (String) link_information.get("startTime");
            endTime = (String) link_information.get("endTime");
            start_time_txt.setText(startTime);
            end_time_txt.setText(endTime);
            link_first = (boolean) SharedPreferencesUtil.getData(EditLinkDeviceResultActivity.this, "link_first", false);
            type = (String) link_information.get("type") == null ? "" : (String) link_information.get("type");
            if (link_first) { //第一
                // 次编辑联动
                switch (type) {
                    case "101":
                        link_edit_from_shoudongbutton(linkId);
                        break;
                    case "100":
                        link_edit_from_tokenbutton(linkId);
                        break;
                }
            } else {
                sensor_map = (Map) getIntent().getSerializableExtra("sensor_map");
                if (sensor_map != null) {
                    list_condition.add(sensor_map);//执行条件只有一个
                    SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_condition", list_condition);
                }
                list_condition = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_condition");
                list_result = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_result");
                startTime = (String) link_information.get("startTime");
                endTime = (String) link_information.get("endTime");
                add_device();
                SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_result", list_result);
                start_time_txt.setText(startTime);
                end_time_txt.setText(endTime);
                if (list_condition.size() != 0) condition_add.setVisibility(View.GONE);
                if (sensor_map == null) {
                    time_select_linear.setVisibility(View.GONE);
                } else
                    switch (type) {
                        case "100"://自动执行条件
                            time_select_linear.setVisibility(View.VISIBLE);
                            break;
                        case "101"://手动执行条件
                            time_select_linear.setVisibility(View.GONE);
                            break;
                    }
            }
            SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "linkId", linkId);
            SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "editlink", true);//
            SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "link_first", false);
            SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "add_condition", false);
            //add_condition
            //为编辑联动动作
        } else {
            back.setText("取消");
            time_select_linear.setVisibility(View.GONE);
            next_step_txt.setText("下一步");
            device_map = (Map) getIntent().getSerializableExtra("device_map");
            sensor_map = (Map) getIntent().getSerializableExtra("sensor_map");
            type = (String) sensor_map.get("type") == null ? "" : (String) sensor_map.get("type");
            list_condition.add(sensor_map);//执行条件只有一个
            list_result = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_result");
            if (device_map != null) {
                if (list_result.size() != 0) {
                    boolean isexist = false;
                    for (int i = 0; i < list_result.size(); i++) {
                        if (device_map.get("number").toString().equals(list_result.get(i).get("number"))) {
                            list_result.remove(i);
                            list_result.add(device_map);
                            isexist = true;
                        } else {
                            isexist = false;
                        }
                    }
                    if (!isexist) {
                        list_result.add(device_map);
                    }
                } else {
                    list_result.add(device_map);
                }
            }
            if (list_condition.size() != 0) condition_add.setVisibility(View.GONE);
            SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_condition", list_condition);
            SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_result", list_result);
        }
    }

    /**
     * 获取手动场景关联信息
     *
     * @param linkId
     */
    private void link_edit_from_shoudongbutton(String linkId) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(EditLinkDeviceResultActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(EditLinkDeviceResultActivity.this));
        map.put("number", linkId);
        map.put("areaNumber", areaNumber);
        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_getOneSceneInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, EditLinkDeviceResultActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        Map map = new HashMap<>();
//                        map.put("deviceId", user.deviceLinkInfo.deviceId);
                        map.put("deviceType", "");
                        map.put("deviceName", "");
                        map.put("name1", "");
                        map.put("deviceId", "");
//                        map.put("linkName", user.deviceLinkInfo.linkName);
                        map.put("condition", "");
                        map.put("minValue", "");
                        map.put("maxValue", "");
                        map.put("startTime", "");
                        map.put("endTime", "");
                        map.put("type", "");
                        map.put("boxName", "");
                        Map action_map = new HashMap();
                        action_map.put("action", "执行");
                        map.put("deviceName", "手动执行");
                        map.put("name1", "手动执行");
                        time_select_linear.setVisibility(View.GONE);


                        map.put("action", action_map.get("action"));
                        link_information.put("startTime", "");
                        link_information.put("endTime", "");
                        if (link_information != null) {
                            link_information_list = new ArrayList<>();
                            link_information_list.add(link_information);
                            SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "link_information_list", link_information_list);
                        }
                        //添加条件
                        list_condition.clear();
                        list_result.clear();
                        list_condition.add(map);

                        for (User.list_scene udsce : user.list) {
                            Map map_device = new HashMap();

                            map_device.put("type", udsce.type);
                            map_device.put("number", udsce.number);
                            map_device.put("name", udsce.name);
                            map_device.put("status", udsce.status);
                            map_device.put("dimmer", udsce.dimmer);
                            map_device.put("mode", udsce.mode);
                            map_device.put("temperature", udsce.temperature);
                            map_device.put("speed", udsce.speed);
                            map_device.put("panelName", udsce.panelName);
                            map_device.put("boxName", udsce.boxName == null ? "" : udsce.boxName);
                            map_device.put("panelMac", udsce.panelMac);
                            map_device.put("gatewayMac", udsce.gatewayMac == null ? "" : udsce.gatewayMac);

                            map_device.put("name1", udsce.name);
                            map_device.put("action", get_action(map_device));
                            if (udsce.type.equals("100")) {
                                map_device.put("name", "场景");
                                map_device.put("name1", "场景");
                                map_device.put("status", "1");
                            } else {
                                map_device.put("name", udsce.name);
                                map_device.put("name1", udsce.name);
                            }
                            list_result.add(map_device);
                        }
                        //添加结果
                        if (list_condition.size() != 0) condition_add.setVisibility(View.GONE);
                        SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_condition", list_condition);
                        SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_result", list_result);

                        add_device();
                        editLinkDeviceCondinationAndResultAdapter_condition.setlist(list_condition);
                        editLinkDeviceCondinationAndResultAdapter_result.setlist(list_result);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

    private void add_device() {
        device_map = (Map) getIntent().getSerializableExtra("device_map");
        if (device_map == null) return;
        list_result = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_result");
        if (list_result.size() != 0) {
            boolean isexist = false;
            for (int i = 0; i < list_result.size(); i++) {
                if (device_map.get("number").toString().equals(list_result.get(i).get("number"))) {
                    list_result.remove(i);
                    list_result.add(device_map);
                    isexist = true;
                } else {
                    isexist = false;
                }
            }
            if (!isexist) {
                list_result.add(device_map);
            }
        } else {
            list_result.add(device_map);
        }
    }

    /**
     * 这个是点击联动列表，之后获取的联动设备信息
     *
     * @param linkId
     */
    private void link_edit_from_tokenbutton(String linkId) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(EditLinkDeviceResultActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(EditLinkDeviceResultActivity.this));
        map.put("linkId", linkId);
        map.put("areaNumber", areaNumber);
        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceLinkInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, EditLinkDeviceResultActivity.this, dialogUtil) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        Map map = new HashMap<>();
//                        map.put("deviceId", user.deviceLinkInfo.deviceId);
                        map.put("deviceType", user.deviceLinkInfo.deviceType);
                        if (user.deviceLinkInfo.deviceName == null) {
                            map.put("deviceName", "");
                            map.put("name1", "");
                        } else {
                            map.put("deviceName", user.deviceLinkInfo.deviceName);
                            map.put("name1", user.deviceLinkInfo.deviceName);
                        }
                        map.put("deviceId", user.deviceLinkInfo.deviceId);
//                        map.put("linkName", user.deviceLinkInfo.linkName);
                        map.put("condition", user.deviceLinkInfo.condition);
                        map.put("minValue", user.deviceLinkInfo.minValue);
                        map.put("maxValue", user.deviceLinkInfo.maxValue);
                        map.put("startTime", user.deviceLinkInfo.startTime);
                        map.put("endTime", user.deviceLinkInfo.endTime);
                        map.put("type", user.deviceLinkInfo.type);
                        map.put("boxName", user.deviceLinkInfo.boxName == null ? "" : user.deviceLinkInfo.boxName);
                        Map action_map = new HashMap();
                        switch (user.deviceLinkInfo.type) {
                            case "100"://自动执行条件
                                action_map = setAction(user.deviceLinkInfo.deviceType, user.deviceLinkInfo.condition, map);//action字段
                                time_select_linear.setVisibility(View.VISIBLE);
                                break;
                            case "101"://手动执行条件
                                action_map.put("action", "执行");
                                map.put("deviceName", "手动执行");
                                map.put("name1", "手动执行");
                                time_select_linear.setVisibility(View.GONE);
                                break;
                        }

                        map.put("action", action_map.get("action"));
                        link_information.put("startTime", user.deviceLinkInfo.startTime);
                        link_information.put("endTime", user.deviceLinkInfo.endTime);
                        if (link_information != null) {
                            link_information_list = new ArrayList<>();
                            link_information_list.add(link_information);
                            SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "link_information_list", link_information_list);
                        }
                        startTime = (String) link_information.get("startTime");
                        endTime = (String) link_information.get("endTime");
                        start_time_txt.setText(startTime);
                        end_time_txt.setText(endTime);
                        //添加条件

                        list_condition.clear();
                        list_result.clear();
                        list_condition.add(map);
                        for (int i = 0; i < user.deviceLinkInfo.deviceList.size(); i++) {
                            Map map_device = new HashMap();
                            map_device.put("type", user.deviceLinkInfo.deviceList.get(i).type);
                            map_device.put("number", user.deviceLinkInfo.deviceList.get(i).number);
                            map_device.put("status", user.deviceLinkInfo.deviceList.get(i).status);
                            map_device.put("dimmer", user.deviceLinkInfo.deviceList.get(i).dimmer);
                            map_device.put("mode", user.deviceLinkInfo.deviceList.get(i).mode);
                            map_device.put("temperature", user.deviceLinkInfo.deviceList.get(i).temperature);
                            map_device.put("speed", user.deviceLinkInfo.deviceList.get(i).speed);
                            map_device.put("name", user.deviceLinkInfo.deviceList.get(i).name);
                            map_device.put("boxName", user.deviceLinkInfo.deviceList.get(i).boxName == null ? "" :
                                    user.deviceLinkInfo.deviceList.get(i).boxName);
                            map_device.put("name1", user.deviceLinkInfo.deviceList.get(i).name);
                            map_device.put("action", get_action(map_device));
                            if (user.deviceLinkInfo.deviceList.get(i).type.equals("100")) {
                                map_device.put("name", "场景");
                                map_device.put("name1", "场景");
                                map_device.put("status", "1");
                            } else {
                                map_device.put("name", user.deviceLinkInfo.deviceList.get(i).name);
                                map_device.put("name1", user.deviceLinkInfo.deviceList.get(i).name);
                            }
                            map_device.put("panelMac", user.deviceLinkInfo.deviceList.get(i).panelMac);
                            map_device.put("gatewayMac", user.deviceLinkInfo.deviceList.get(i).gatewayMac);
                            list_result.add(map_device);
                        }

                        //添加结果
                        if (list_condition.size() != 0) condition_add.setVisibility(View.GONE);
                        SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_condition", list_condition);
                        SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_result", list_result);
                        add_device();
                        editLinkDeviceCondinationAndResultAdapter_condition.setlist(list_condition);
                        editLinkDeviceCondinationAndResultAdapter_result.setlist(list_result);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }


    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    private Map setAction(String type, String action, Map map_link) {
        switch (type) {
            case "7":
                if (action.equals("1")) {
                    map_link.put("action", "打开");
                } else {
                    map_link.put("action", "闭合");
                }
                break;
            case "8":
                if (action.equals("1")) {
                    map_link.put("action", "有人经过");
                }
                break;
            case "9":

                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "10":
                String text_pm = "";
                String minValue = (String) map_link.get("minValue");
                String maxValue = (String) map_link.get("maxValue");
                String temp = "";
                if (!minValue.equals("")) {
                    text_pm = minValue;
                    temp = "小于等于 ";
                }

                if (!maxValue.equals("")) {
                    text_pm = maxValue;
                    temp = "大于等于 ";
                }

                switch (action) {
                    case "1":
                        map_link.put("action", "PM2.5 " + temp + text_pm);
                        break;
                    case "2":
                        map_link.put("action", "温度 " + temp + text_pm + "℃");
                        break;
                    case "3":
                        map_link.put("action", "湿度 " + temp + text_pm + "%");
                        break;
                }
                break;
            case "11":
                if (action.equals("1")) {
                    map_link.put("action", "按下");
                }
                break;
            case "12":
                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "13":
                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "14":
                if (action.equals("1")) {
                    map_link.put("action", "报警");
                }
                break;
            case "15":
//            case "17":
                if (action.equals("1")) {
                    map_link.put("action", "打开");
                } else {
                    map_link.put("action", "闭合");
                }
                break;
            case "16":

                break;
        }
        return map_link;
    }

    /**
     * 针对调光灯带的打开下拉显示
     *
     * @param map
     */
    private String get_action(Map map) {
        String action = "";
        switch (map.get("type").toString()) {
            case "1"://调光关闭
            case "17":
                action = init_action_light((String) map.get("status"));
                break;
            case "100"://打开-》添加调光值数据，
                action = (String) map.get("name1");
                break;
            case "2":
                action = init_action_tiaoguang((String) map.get("status"), (String) map.get("dimmer"));
                break;
            case "3":
                action = init_action_kongtiao("3", (String) map.get("status"), (String) map.get("mode"), (String) map.get("speed"), (String) map.get("temperature"));
                break;
            case "4":
                action = init_action_curtain((String) map.get("status"), "4");
                break;
            case "18":
                action = init_action_curtain((String) map.get("status"), "18");
                break;
            case "5":
                action = init_action_kongtiao("5", (String) map.get("status"), (String) map.get("mode"), (String) map.get("speed"), (String) map.get("temperature"));
                break;
            case "6":
                action = init_action_kongtiao("6", (String) map.get("status"), (String) map.get("mode"), (String) map.get("speed"), (String) map.get("temperature"));
                break;
            case "202"://wifi电视
            case "206"://wifi空调
                action = init_action_wifi_device((String) map.get("status"));
                break;
            case "15":
            case "16":
//            case "17":
                action = init_action_smart_door_lock((String) map.get("status"));
                break;
        }
        return action;
    }

    /**
     * 窗帘
     */
    /**
     * 窗帘
     */
    private String init_action_curtain(String status, String type) {
        String action = "";
        switch (type) {
            case "4":
                action = old_curtain_window(status, action);
                break;
            case "18":
                action = new_curtain_window(status, action);
                break;
        }
        return action;
    }

    /**
     * A411-A414
     *
     * @param status
     * @param action
     * @return
     */
    private String new_curtain_window(String status, String action) {
        switch (status) {
            case "0":
                action = "关闭";
                break;
            case "1":
                action = "打开";
                break;
        }
        return action;
    }

    /**
     * A401
     *
     * @param status
     * @param action
     * @return
     */
    private String old_curtain_window(String status, String action) {
        switch (status) {
            case "0":
                action = "全部关闭";
                break;
            case "1":
                action = "全部打开";
                break;

            case "4":
                action = "内纱打开";
                break;

            case "6":
                action = "内纱关闭";
                break;
            case "7":
                action = "外纱关闭";
                break;
            case "8":
                action = "外纱打开";
                break;
        }
        return action;
    }


    /**
     * wifi设备
     */
    private String init_action_wifi_device(String status) {
        String action = "";
        switch (status) {
            case "0":
                action = "关";
                break;
            case "1":
                action = "开";
                break;
        }
        return action;
    }

    /**
     * 智能门锁
     */
    private String init_action_smart_door_lock(String status) {
        String action = "";
        switch (status) {
            case "0":
                action = "关闭";
                break;
            case "1":
                action = "打开";
                break;
        }
        return action;
    }

    /**
     * 空调
     *
     * @param
     * @param type
     */
    private String init_action_kongtiao(String type, String status, String model, String speed, String tempature) {
        String action = "";
        switch (status) {
            case "0":
                action = "关闭";
                break;
            case "1":
                action = init_action_air(type, model, speed, tempature);
                break;
        }

        return action;
    }

    /**
     * 初始化空调动作
     *
     * @param type
     * @param model
     * @param speed
     * @param tempature
     */
    private String init_action_air(String type, String model, String speed, String tempature) {
        StringBuffer temp = new StringBuffer();
//        String speed = (String) air_control_map.get("speed");
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
            case "4":
                temp.append("强力");
                break;
            case "5":
                temp.append("送风");
                break;

            case "6":
                temp.append("自动");
                break;
        }
//        String temperature = (String) air_control_map.get("temperature");

        temp.append("  " + tempature + "℃");
        switch (type) {
            case "3":
                init_common_air(model, temp);
                break;
        }
//        common_doit("action", temp.toString());
        return temp.toString();
    }

    private void init_common_air(String model, StringBuffer temp) {
        switch (model) {
            case "1":
                temp.append("  " + "制冷");
                break;
            case "2":
                temp.append("  " + "制热");
                break;
            case "3":
                temp.append("  " + "除湿");
                break;
            case "4":
                temp.append("  " + "自动");
                break;
            case "5":
                temp.append("  " + "通风");
                break;
        }
    }

    /**
     * 调光灯
     *
     * @param dimmer
     */
    private String init_action_tiaoguang(String status, String dimmer) {
        String action = "";
        switch (status) {
            case "0":
                action = "关闭";
                break;
            case "1":
                action = "调光值:" + dimmer;
                break;
        }
        return action;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        common_second();
    }

    /**
     * 灯控
     */
    private String init_action_light(String status) {
        String action = "";
        switch (status) {
            case "0":
                action = "关闭";
                break;
            case "1":
                action = "打开";
                break;
        }

        return action;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        list_condition = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_condition");
        switch (v.getId()) {
            case R.id.back://点击取消弹出框
                switch (back.getText().toString()) {
                    case "返回":
//                        common_second();
//                        EditLinkDeviceResultActivity.this.finish();
//                        AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
                        common();
                        break;
                    case "取消":
                        showCenterDeleteDialog();
                        break;
                }
                break;

            case R.id.result_add://添加执行结果
//                AppManager.getAppManager().finishActivity_current(AirLinkageControlActivity.class);
//                AppManager.getAppManager().finishActivity_current(SelectiveLinkageDeviceDetailSecondActivity.class);
//                AppManager.getAppManager().finishActivity_current(ExcuteSomeOneSceneActivity.class);
//                AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity.class);
                if (list_condition == null || list_condition.size() == 0) {
                    ToastUtil.showToast(EditLinkDeviceResultActivity.this, "请添加执行条件");
                } else {
//                    AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                    intent = new Intent(EditLinkDeviceResultActivity.this, SelectiveLinkageActivity.class);
                    intent.putExtra("link_map", (Serializable) list_condition.get(0));
                    startActivity(intent);
//                    EditLinkDeviceResultActivity.this.finish();
                }
                break;
            case R.id.condition_add:
//                AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                intent = new Intent(EditLinkDeviceResultActivity.this, SelectSensorActivity.class);
                startActivity(intent);
                SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "add_condition", true);
//                EditLinkDeviceResultActivity.this.finish();
//                SharedPreferencesUtil.remove_current_index(EditLinkDeviceResultActivity.this, "list_condition", 0);
//                SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_result", new ArrayList<Map>());
                break;
            case R.id.next_step_txt:
                if (list_condition == null || list_condition.size() == 0) {
                    ToastUtil.showToast(EditLinkDeviceResultActivity.this, "请添加执行条件");
                    return;
                }

                if (list_result == null || list_result.size() == 0) {
                    ToastUtil.showToast(EditLinkDeviceResultActivity.this, "请添加执行结果");
                    return;
                }

                switch (next_step_txt.getText().toString()) {
                    case "下一步":
                        intent = new Intent(EditLinkDeviceResultActivity.this, SetSelectLinkActivity.class);
                        intent.putExtra("link_information", (Serializable) link_information);
                        startActivity(intent);
                        break;
                    case "保存":
                        switch (type) {
                            case "100":
                                getData(true, linkName, linkId, ApiHelper.sraum_updateDeviceLink);
                                break;
                            case "101":
                                set_Hand_Data(true, "", linkId, ApiHelper.sraum_editManuallyScene);
                                break;
                        }
                        break;
                }
                break;

            case R.id.sleep_time_rel://睡觉
                index_select = 1;
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
            case R.id.get_up_rel://起床
                index_select = 0;
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
        }
    }

    /**
     * 更新
     *
     * @param flag
     * @param linkName
     * @param linkId
     * @param apiname
     */
    private void getData(final boolean flag, final String linkName, final String linkId, final String apiname) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(EditLinkDeviceResultActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(EditLinkDeviceResultActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("deviceId", list_condition.get(0).get("deviceId"));
        map.put("deviceType", list_condition.get(0).get("deviceType"));
        map.put("linkName", linkName);
        map.put("type", list_condition.get(0).get("type"));
        map.put("condition", list_condition.get(0).get("condition"));
        map.put("minValue", list_condition.get(0).get("minValue"));
        map.put("maxValue", list_condition.get(0).get("maxValue"));
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        if (flag) {
            map.put("linkId", linkId);
        } else {

        }
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < list_result.size(); i++) {
            Map map_device = new HashMap();
            map_device.put("type", list_result.get(i).get("type"));
            map_device.put("number", list_result.get(i).get("number"));
            map_device.put("status", list_result.get(i).get("status"));
            map_device.put("dimmer", list_result.get(i).get("dimmer"));
            map_device.put("mode", list_result.get(i).get("mode"));
            map_device.put("temperature", list_result.get(i).get("temperature"));
            map_device.put("speed", list_result.get(i).get("speed"));
            map_device.put("panelMac", list_result.get(i).get("panelMac"));
            map_device.put("gatewayMac", list_result.get(i).get("gatewayMac"));
            list.add(map_device);
        }
        map.put("deviceList", list);

        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(apiname, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(flag, linkName, linkId, apiname);
                    }
                }, EditLinkDeviceResultActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                        common();
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        common();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        common();
                    }

                    @Override
                    public void pullDataError() {
                        common();
                    }

                    @Override
                    public void sevenCode() {
                        common();
                    }

                    @Override
                    public void fiveCode() {
                        common();
                    }

                    @Override
                    public void fourCode() {
                        common();
                        ToastUtil.showToast(EditLinkDeviceResultActivity.this, "104 deviceList 信息有误");
                    }

                    @Override
                    public void threeCode() {
                        common();
                        ToastUtil.showToast(EditLinkDeviceResultActivity.this, "linkId 错误 ");
                    }

                    @Override
                    public void wrongBoxnumber() {
                        common();
                        ToastUtil.showToast(EditLinkDeviceResultActivity.this, "areaNumber不正确,");
                    }
                });
    }


    /**
     * 编辑手动场景关联信息
     *
     * @param flag
     * @param linkName
     * @param linkId
     * @param apiname
     */
    private void set_Hand_Data(final boolean flag, final String linkName, final String linkId, final String apiname) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(EditLinkDeviceResultActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(EditLinkDeviceResultActivity.this));
        map.put("areaNumber", areaNumber);

        if (flag) {
            map.put("number", linkId);
        } else {
            map.put("sceneName", linkName);
            map.put("sceneType", "1");
            map.put("panelNumber", "");
            map.put("buttonNumber", "");
        }
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < list_result.size(); i++) {
            Map map_device = new HashMap();
            map_device.put("type", list_result.get(i).get("type"));
            map_device.put("number", list_result.get(i).get("number"));
            map_device.put("status", list_result.get(i).get("status"));
            map_device.put("dimmer", list_result.get(i).get("dimmer"));
            map_device.put("mode", list_result.get(i).get("mode"));
            map_device.put("temperature", list_result.get(i).get("temperature"));
            map_device.put("speed", list_result.get(i).get("speed"));
            map_device.put("panelMac", list_result.get(i).get("panelMac") == null ? "" : list_result.get(i).get("panelMac"));
            map_device.put("gatewayMac", list_result.get(i).get("gatewayMac") == null ? "" : list_result.get(i).get("gatewayMac"));
            list.add(map_device);
        }
        map.put("deviceList", list);

        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(apiname, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(flag, linkName, linkId, apiname);
                    }
                }, EditLinkDeviceResultActivity.this, dialogUtil) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                        common();
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        common();
                        String boxNumber = user.boxNumber;
//                      手动场景 添加的时候我会返回一个boxNumber,这个参数有值，代表你加的是网关场景，然后你可以做关联面板，这个参数没有值，那就是云场景
//                                没有关联面板这个操作
                        if (boxNumber != null && !boxNumber.equals("")) {//去关联面板
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("sceneName", linkName);
                            bundle1.putString("sceneType", "1");
                            bundle1.putString("boxNumber", boxNumber);
                            bundle1.putString("panelType", "");
                            bundle1.putString("panelNumber", "");
                            bundle1.putString("buttonNumber", "");
                            IntentUtil.startActivity(EditLinkDeviceResultActivity.this, AssociatedpanelActivity.class, bundle1);

                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        common();
                    }

                    @Override
                    public void pullDataError() {
                        common();
                    }

                    @Override
                    public void sevenCode() {
                        common();
                    }

                    @Override
                    public void fiveCode() {
                        common();
                        ToastUtil.showToast(EditLinkDeviceResultActivity.this, "更新失败");
                    }

                    @Override
                    public void fourCode() {
                        common();
                        ToastUtil.showToast(EditLinkDeviceResultActivity.this, "deviceList 信息不正确,");
                    }

                    @Override
                    public void threeCode() {
                        common();
                        ToastUtil.showToast(EditLinkDeviceResultActivity.this, "number 不存在");
                    }

                    @Override
                    public void wrongBoxnumber() {
                        common();
                        ToastUtil.showToast(EditLinkDeviceResultActivity.this, "areaNumber\n" +
                                "不存在");
                    }
                });
    }


    /**
     * 公共方法
     */
    private void common() {
        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
//        Intent intent = new Intent(EditLinkDeviceResultActivity.this, LinkageListActivity.class);
//        startActivity(intent);
        EditLinkDeviceResultActivity.this.finish();
        common_second();
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

        View view = LayoutInflater.from(EditLinkDeviceResultActivity.this).inflate(R.layout.cancel_dialog, null);
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
        final Dialog dialog = new Dialog(EditLinkDeviceResultActivity.this, R.style.BottomDialog);
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
                common();
                dialog.dismiss();
            }
        });
    }


    private void common_second() {
        SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "linkId", "");
        SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_result", new ArrayList<Map>());
        SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "list_condition", new ArrayList<Map>());
        SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "editlink", false);
        SharedPreferencesUtil.saveInfo_List(EditLinkDeviceResultActivity.this, "link_information_list", new ArrayList<Map>());
        SharedPreferencesUtil.saveData(EditLinkDeviceResultActivity.this, "add_condition", false);
    }


    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                getTime(date);
                Log.e("robin debug", "getTime(date):" + getTime(date));
                hourPicker = String.valueOf(date.getHours());
                minutePicker = String.valueOf(date.getMinutes());
                switch (String.valueOf(minutePicker).length()) {
                    case 1:
                        minutePicker = "0" + minutePicker;
                        break;
                }

                switch (String.valueOf(hourPicker).length()) {
                    case 1:
                        hourPicker = "0" + hourPicker;
                        break;
                }

                time_content = hourPicker + ":" + minutePicker;
                handler.sendEmptyMessage(index_select);
//                start_scenetime.setText(hourPicker + ":" + minutePicker);
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
                /*.gravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final ImageView tvSubmit = (ImageView) v.findViewById(R.id.ok_bt);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.finish_bt);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentSize(18)
                .setType(new boolean[]{false, false, false, true, true, false})
                .setLabel("年", "月", "日", "小时", "分钟", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    start_time_txt.setText(time_content);
                    startTime = start_time_txt.getText().toString();
                    break;//开始
                case 1:
                    end_time_txt.setText(time_content);
                    endTime = end_time_txt.getText().toString();
                    break;//结束
            }
        }
    };

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onBackPressed() {
//        showCenterDeleteDialog();
        switch (back.getText().toString()) {
            case "返回":
//                common_second();
//                EditLinkDeviceResultActivity.this.finish();
                common();

                break;
            case "取消":
                showCenterDeleteDialog();
                break;
        }
    }
}
