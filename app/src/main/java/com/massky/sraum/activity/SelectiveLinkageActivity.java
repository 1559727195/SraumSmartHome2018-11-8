package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.massky.sraum.adapter.SelectLinkageAdapter;
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
 * Created by zhu on 2018/6/13.
 */

public class SelectiveLinkageActivity extends BaseActivity implements
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
    @InjectView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;
    private SelectLinkageAdapter selectexcutesceneresultadapter;
    private List<Map> list_hand_scene = new ArrayList<>();
    private Handler mHandler = new Handler();
    //    String[] again_elements = {"1", "3"
//            };
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private List<Boolean> list_bool = new ArrayList<>();
    private int position;
    private DialogUtil dialogUtil;
    //    private List<User.device> list = new ArrayList<>();
    private List<String> listtype = new ArrayList();
    private List<Map> panelList = new ArrayList<>();
    private Map map_link = new HashMap();
    private List<String> listpanelNumber = new ArrayList();
    private List<String> listpanelName = new ArrayList();
    private List<String> listbox = new ArrayList();

    @Override
    protected int viewId() {
        return R.layout.selective_linkage_lay;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        rel_scene_set.setOnClickListener(this);
//        onData();
        next_step_txt.setOnClickListener(this);
        maclistview_id.setOnItemClickListener(this);
        refresh_view.setOnRefreshListener(this);
//        refresh_view.autoRefresh();
//        dialogUtil = new DialogUtil(this);
        map_link = (Map) getIntent().getSerializableExtra("link_map");
        if (map_link == null) return;
    }

    @Override
    protected void onEvent() {

    }


    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
//    R.drawable.magnetic_door_s,
//    R.drawable.human_induction_s, R.drawable.water_s, R.drawable.pm25_s,
//    R.drawable.emergency_button_s
    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        selectexcutesceneresultadapter = new SelectLinkageAdapter(SelectiveLinkageActivity.this,
                panelList, listint, listintwo);
        maclistview_id.setAdapter(selectexcutesceneresultadapter);
        getData(true);
    }

    private void getData(boolean flag) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(SelectiveLinkageActivity.this));
        String areaNumber = (String) SharedPreferencesUtil.getData(SelectiveLinkageActivity.this,
                "areaNumber", "");

        map.put("areaNumber", areaNumber);
        if (flag) {
            dialogUtil.loadDialog();
        }
        MyOkHttp.postMapObject(ApiHelper.sraum_getLinkController, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(false);
                    }
                }, SelectiveLinkageActivity.this, dialogUtil) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        panelList.clear();
//                        panelList = user.panelList;
                        listtype.clear();
                        listint.clear();
                        listintwo.clear();
                        listpanelNumber.clear();
                        for (User.panellist ud : user.panelList) {
                            Map map = new HashMap();
                            map.put("panelName", ud.panelName);
                            map.put("panelNumber", ud.panelNumber);
                            map.put("boxNumber", ud.boxNumber);
                            map.put("boxName", ud.boxName);
                            map.put("panelType", ud.panelType);
                            map.put("panelMac", ud.panelMac);
                            map.put("controllerId", "");

                            panelList.add(map);
                        }

                        if (user.wifiList != null)
                            for (User.wifi_device ud : user.wifiList) {
                                Map map = new HashMap();
                                map.put("panelName", ud.name);
                                map.put("panelNumber", ud.number);
                                map.put("boxNumber", "");
                                map.put("panelType", ud.type);
                                map.put("boxName", "");
                                map.put("controllerId", ud.controllerId);
                                panelList.add(map);
                            }

                        for (Map map : panelList) {
                            listtype.add(map.get("panelType").toString());
                            listpanelNumber.add(map.get("panelNumber").toString());
                            listpanelName.add(map.get("panelName").toString());
                            listbox.add(map.get("boxName").toString());
                            setPicture(map.get("panelType").toString());
                        }

                        selectexcutesceneresultadapter.setlist(panelList, listint, listintwo);
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
                listint.add(R.drawable.icon_yijiandk_40);
                listintwo.add(R.drawable.icon_yijiandk_40);
                break;
            case "A202":
                listint.add(R.drawable.icon_liangjiandki_40);
                listintwo.add(R.drawable.icon_liangjiandki_40);
                break;
            case "A203":
                listint.add(R.drawable.icon_sanjiandk_40);
                listintwo.add(R.drawable.icon_sanjiandk_40);
                break;
            case "A204":
                listint.add(R.drawable.icon_kaiguan_40);
                listintwo.add(R.drawable.icon_kaiguan_40_active);
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
                listint.add(R.drawable.icon_tiaoguang_40);
                listintwo.add(R.drawable.icon_tiaoguang_40_active);
                break;
            case "A401":
                listint.add(R.drawable.icon_chuanglian_40);
                listintwo.add(R.drawable.icon_chuanglian_40_active);
                break;
            case "A511":
                listint.add(R.drawable.icon_kongtiao_40);
                listintwo.add(R.drawable.icon_kongtiao_40_active);
                break;
            case "A611":
                listint.add(R.drawable.freshair);
                listintwo.add(R.drawable.freshair);
                break;
            case "A711":
                listint.add(R.drawable.floorheating);
                listintwo.add(R.drawable.floorheating);
                break;
            case "A801":
                listint.add(R.drawable.icon_menci_40);
                listintwo.add(R.drawable.icon_menci_40_active);
                break;
            case "A901":
                listint.add(R.drawable.icon_rentiganying_40);
                listintwo.add(R.drawable.icon_rentiganying_40_active);
                break;
            case "A902":
                listint.add(R.drawable.icon_rucebjq_40);
                listintwo.add(R.drawable.icon_rucebjq_40_active);
                break;
            case "AB01":
                listint.add(R.drawable.icon_yanwubjq_40);
                listintwo.add(R.drawable.icon_yanwubjq_40_active);
                break;
            case "AB04":
                listint.add(R.drawable.icon_ranqibjq_40);
                listintwo.add(R.drawable.icon_ranqibjq_40_active);
                break;
            case "AC01":
                listint.add(R.drawable.icon_shuijin_40);
                listintwo.add(R.drawable.icon_shuijin_40_active);
                break;
            case "AD01":
                listint.add(R.drawable.icon_pm25_40);
                listintwo.add(R.drawable.icon_pm25_40_active);
                break;
            case "AD02":
                listint.add(R.drawable.defaultpic);
                listintwo.add(R.drawable.defaultpic);
                break;
            case "B001":
                listint.add(R.drawable.icon_jinjianniu_40);
                listintwo.add(R.drawable.icon_jinjianniu_40_active);
                break;
            case "B101":
                listint.add(R.drawable.icon_kaiguan_socket_40);
                listintwo.add(R.drawable.icon_kaiguan_socket_40);
                break;
            case "B102":
                listint.add(R.drawable.defaultpic);
                listintwo.add(R.drawable.defaultpic);
                break;
            case "B201":
                listint.add(R.drawable.icon_zhinengmensuo_40);
                listintwo.add(R.drawable.icon_zhinengmensuo_40_active);
                break;
            case "B301":
                listint.add(R.drawable.icon_jixieshou_40);
                listintwo.add(R.drawable.icon_jixieshou_40);
                break;
            case "AA02":
                listint.add(R.drawable.icon_hongwaizfq_40);
                listintwo.add(R.drawable.icon_hongwaizfq_40);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back:
                SelectiveLinkageActivity.this.finish();
                break;
            case R.id.next_step_txt:
                intent = new Intent(SelectiveLinkageActivity.this, UnderWaterActivity.class);
//                intent.putExtra("type", (Serializable) again_elements[position]);
                startActivityForResult(intent, REQUEST_SENSOR);
                break;
            case R.id.rel_scene_set:
                intent = new Intent(SelectiveLinkageActivity.this,
                        ExcuteSomeOneSceneActivity.class);
                intent.putExtra("sensor_map", (Serializable) map_link);
                startActivity(intent);
                break;//执行某些手动场景
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;

        switch (listtype.get(position)) {
            case "A501":
                getAir501Data(listpanelNumber.get(position), listbox.get(position),
                        panelList.get(position).get("boxNumber")
                                == null ? "" : panelList.get(position).get("boxNumber").toString());
                break;
            default:
                intent = new Intent(SelectiveLinkageActivity.this, SelectiveLinkageDeviceDetailSecondActivity.class);
//            intent.putExtra("type", (Serializable) listtype.get(position));
                intent.putExtra("panelNumber", (Serializable) listpanelNumber.get(position));
                intent.putExtra("panelType", (Serializable) listtype.get(position));
                intent.putExtra("panelName", (Serializable) listpanelName.get(position));
                intent.putExtra("boxName", (Serializable) listbox.get(position) == null ? "" : listbox.get(position));
                //boxNumber
                intent.putExtra("boxNumber", (Serializable) panelList.get(position).get("boxNumber")
                        == null ? "" : panelList.get(position).get("boxNumber").toString());
                //传感器参数
//                Map mapdevice = new HashMap();
//                mapdevice.put("sensorType", map_link.get("deviceType"));
//                mapdevice.put("sensorId", map_link.get("deviceId"));
//                mapdevice.put("sensorName",map_link.get("name"));
//                mapdevice.put("sensorCondition", map_link.get("condition"));
//                mapdevice.put("sensorMinValue", map_link.get("minValue"));
//                mapdevice.put("sensorMaxValue", map_link.get("maxValue"));
                intent.putExtra("sensor_map", (Serializable) map_link);
                //
                startActivityForResult(intent, REQUEST_SENSOR);
                break;
            case "AA02"://wifi红外模块
                intent = new Intent(SelectiveLinkageActivity.this, SelectLinkageYaoKongQiActivity.class);
//            intent.putExtra("type", (Serializable) listtype.get(position));
                intent.putExtra("panelNumber", (Serializable) listpanelNumber.get(position));
                intent.putExtra("panelType", (Serializable) listtype.get(position));
                intent.putExtra("panelName", (Serializable) listpanelName.get(position));
                //传感器参数
//                Map mapdevice = new HashMap();
//                mapdevice.put("sensorType", map_link.get("deviceType"));
//                mapdevice.put("sensorId", map_link.get("deviceId"));
//                mapdevice.put("sensorName",map_link.get("name"));
//                mapdevice.put("sensorCondition", map_link.get("condition"));
//                mapdevice.put("sensorMinValue", map_link.get("minValue"));
//                mapdevice.put("sensorMaxValue", map_link.get("maxValue"));
                intent.putExtra("sensor_map", (Serializable) map_link);
                intent.putExtra("boxName", (Serializable) listbox.get(position) == null ? "" : listbox.get(position));
                intent.putExtra("boxNumber", "");
                startActivity(intent);

                break;

        }


//        SelectSensorSingleAdapter.ViewHolderContentType viewHolder = (SelectSensorSingleAdapter.ViewHolderContentType) view.getTag();
//        viewHolder.checkbox.toggle();// 把CheckBox的选中状态改为当前状态的反,gridview确保是单一选
    }

    /**
     * 空调面板501
     *
     * @param
     * @param boxName
     */
    private void getAir501Data(final String panelNumber, final String boxName, final String boxNumber) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(SelectiveLinkageActivity.this, "areaNumber", "");
        dialogUtil.loadDialog();
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(SelectiveLinkageActivity.this));
        map.put("boxNumber", boxNumber);
        map.put("panelNumber", panelNumber);
        map.put("areaNumber", areaNumber);
        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(false);
                    }
                }, SelectiveLinkageActivity.this, dialogUtil) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        List<Map> list = new ArrayList<>();
                        //面板的详细信息
                        String panelType = user.panelType;
                        String panelName = user.panelName;
                        String panelMAC = user.panelMAC;
                        String gatewayMAC = user.gatewayMAC;


                        for (int position = 0; position < user.deviceList.size(); position++) {
                            Map<String, Object> mapdevice = new HashMap<String, Object>();
                            mapdevice.put("type", user.deviceList.get(position).type);
                            mapdevice.put("number", user.deviceList.get(position).number);
                            mapdevice.put("status", user.deviceList.get(position).status);
                            mapdevice.put("dimmer", user.deviceList.get(position).dimmer);
                            mapdevice.put("mode", user.deviceList.get(position).mode);
                            mapdevice.put("temperature", user.deviceList.get(position).temperature);
                            mapdevice.put("speed", user.deviceList.get(position).speed);
                            mapdevice.put("name", user.deviceList.get(position).name);
                            mapdevice.put("panelName", user.deviceList.get(position).panelName);
                            mapdevice.put("button", user.deviceList.get(position).button);
                            mapdevice.put("boxName", boxName);

//                            //传感器参数
//                            mapdevice.put("sensorType", map_link.get("deviceType"));
//                            mapdevice.put("sensorId", map_link.get("deviceId"));
//                            mapdevice.put("sensorName", map_link.get("name"));
//                            mapdevice.put("sensorCondition", map_link.get("condition"));
//                            mapdevice.put("sensorMinValue", map_link.get("minValue"));
//                            mapdevice.put("sensorMaxValue", map_link.get("maxValue"));

                            //
                            list.add(mapdevice);
                        }

                        Intent intent = new Intent(
                                SelectiveLinkageActivity.this,
                                AirLinkageControlActivity.class
                        );

                        Map map_panel = new HashMap();
                        map_panel.put("panelType", panelType);
                        map_panel.put("panelName", panelName);
                        map_panel.put("panelMac", panelMAC);
                        map_panel.put("gatewayMac", gatewayMAC);
                        map_panel.put("panelNumber", panelNumber);
                        map_panel.put("boxNumber", boxNumber);
                        intent.putExtra("air_control_map", (Serializable) list.get(0));
                        intent.putExtra("panel_map", (Serializable) map_panel);
                        intent.putExtra("sensor_map", (Serializable) map_link);
                        startActivity(intent);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        getData(true);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }
}
