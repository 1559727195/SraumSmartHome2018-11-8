package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.massky.sraum.adapter.AddHandSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.adapter.AddHandSceneAdapter.getIsSelected;

/**
 * Created by zhu on 2018/1/5.
 */

public class AddHandSceneActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private List<Map> list_hand_scene;
    private Handler mHandler = new Handler();
    private AddHandSceneAdapter addhandSceneAdapter;
    private DialogUtil dialogUtil;
    private boolean isfirst_com;
    private List<Map> list_scene = new ArrayList<>();
    private List<User.device> deviceList = new ArrayList<>();

    private String roomNumber;
    private String roomName;
    private String isUse;
    private String panelType;
    private String panelName;
    private String panelMAC;
    private String panelStatus;


    @Override
    protected int viewId() {
        return R.layout.add_hand_scene_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
        dialogUtil = new DialogUtil(this);
        isfirst_com = true;

        addhandSceneAdapter = new AddHandSceneAdapter(AddHandSceneActivity.this, list_hand_scene, new AddHandSceneAdapter.AddHandSceneListener() {

            @Override
            public void addhand_scene_list(boolean ischecked, int position, String type) {
                list_scene.get(position).put("isselect", ischecked);
                String gatewayNumber = list_scene.get(position).get("boxNumber") == null ? "" :
                        list_scene.get(position).get("boxNumber").toString();
                //deviceNumber
                String deviceNumber = list_scene.get(position).get("number").toString() == null ? "" :
                        list_scene.get(position).get("number").toString();

                switch (type) {
                    case "A204":
                    case "A203":
                    case "A202":
                    case "A201":
                    case "网关":
                        break;
                    default:
                        sraum_getPanelDevices(gatewayNumber, deviceNumber);
                        break;
                }

            }
        });
        xListView_scan.setAdapter(addhandSceneAdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
    }

    /**
     * 获取单个设备详细信息
     *
     * @param gatewayNumber
     * @param deviceNumber
     */
    private void sraum_getPanelDevices(String gatewayNumber, String deviceNumber) {
//        2.areaNumber：区域编号3.boxNumber网关
        String areaNumber = (String) SharedPreferencesUtil.getData(AddHandSceneActivity.this, "areaNumber", "");
        dialogUtil.loadDialog();
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(AddHandSceneActivity.this));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("boxNumber", gatewayNumber);
        mapdevice.put("panelNumber", deviceNumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getPanelDevices
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        getOtherDevices("load");
                    }
                }, AddHandSceneActivity.this, dialogUtil) {
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

                        //面板的详细信息
                        deviceList.clear();
                        deviceList.addAll(user.deviceList);
                        for (User.device  device: user.deviceList) {

                        }

                        //面板的详细信息
                        panelType = user.panelType;
                        panelName = user.panelName;
                        panelMAC = user.panelMAC;
                        panelStatus = user.panelStatus;
                        roomNumber = user.roomNumber;
                        roomName = user.roomName;
                        isUse = user.isUse;

                        Map map = new HashMap();
                        map.put("panelType", panelType);
                        map.put("panelName", panelName);
                        map.put("panelMAC", panelMAC);
                        map.put("panelStatus", panelStatus);
                        map.put("roomNumber", roomNumber);
                        map.put("roomName", roomName);
                        map.put("isUse", isUse);
                        //获取单个设备详细信息
                        Intent intent = new Intent(AddHandSceneActivity.this, HandAddSceneDeviceDetailActivity.class);

                        intent.putExtra("deviceList", (Serializable) deviceList);
                        intent.putExtra("device_map", (Serializable) map);
                        startActivity(intent);


                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOtherDevices("");
    }

    private void getOtherDevices(final String doit) {
//        2.areaNumber：区域编号3.boxNumber网关
        String areaNumber = (String) SharedPreferencesUtil.getData(AddHandSceneActivity.this, "areaNumber", "");
        dialogUtil.loadDialog();
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(AddHandSceneActivity.this));
        mapdevice.put("areaNumber", areaNumber);
        MyOkHttp.postMapString(ApiHelper.sraum_myAllDevice
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        getOtherDevices("load");
                    }
                }, AddHandSceneActivity.this, dialogUtil) {
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

                        List<Map> list_current = new ArrayList<>();

                        for (int i = 0; i < user.gatewayList.size(); i++) {
                            Map mapdevice = new HashMap<>();
                            mapdevice.put("number", user.gatewayList.get(i).number);
                            mapdevice.put("name", user.gatewayList.get(i).name);
                            mapdevice.put("type", "网关");
                            mapdevice.put("isselect", false);
                            list_current.add(mapdevice);

                        }

                        for (int i = 0; i < user.panelList.size(); i++) {
                            Map mapdevice = new HashMap<>();
                            mapdevice.put("number", user.panelList.get(i).number);
                            mapdevice.put("name", user.panelList.get(i).name);
                            mapdevice.put("type", user.panelList.get(i).type);
                            mapdevice.put("boxNumber", user.panelList.get(i).boxNumber);
                            mapdevice.put("isselect", false);
                            list_current.add(mapdevice);

                        }

                        for (int i = 0; i < user.wifiList.size(); i++) {
                            Map mapdevice = new HashMap<>();
                            mapdevice.put("number", user.wifiList.get(i).number);
                            mapdevice.put("name", user.wifiList.get(i).name);
                            mapdevice.put("type", user.wifiList.get(i).type);
                            mapdevice.put("isselect", false);
                            list_current.add(mapdevice);

                        }


                        if (isfirst_com) {
                            isfirst_com = false;
                        } else {// 更新
                            for (int i = 0; i < list_current.size(); i++) {
                                for (Map map : list_scene) {
                                    if (map.get("number").toString().equals(list_current.get(i).get("number").toString())) {
                                        list_current.set(i, map);
                                    }
                                }
                            }
                        }

                        list_scene.clear();
                        list_scene = list_current;
                        for (int i = 0; i < list_scene.size(); i++) {
                            getIsSelected().put(i, (Boolean) list_scene.get(i).get("isselect"));
                        }
                        addhandSceneAdapter.setLists(list_scene);
                        addhandSceneAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddHandSceneActivity.this.finish();
                break;
            case R.id.next_step_txt:
                Intent intent = new Intent(AddHandSceneActivity.this,
                        GuanLianSceneBtnActivity.class);
                intent.putExtra("excute", "hand");//自动的
                startActivity(intent);
                break;
        }
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }


    @Override
    public void onRefresh() {
        onLoad();
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 1000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
