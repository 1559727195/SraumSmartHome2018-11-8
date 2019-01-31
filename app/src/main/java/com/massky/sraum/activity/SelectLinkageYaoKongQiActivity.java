package com.massky.sraum.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.SelectLinkageYaoKongQiAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.PullToRefreshLayout;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/6/13.
 */

public class SelectLinkageYaoKongQiActivity extends BaseActivity implements
        AdapterView.OnItemClickListener,
        PullToRefreshLayout.OnRefreshListener {

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
    @InjectView(R.id.project_select)
    TextView project_select;
    private SelectLinkageYaoKongQiAdapter selectexcutesceneresultadapter;
    private List<Map> list_hand_scene = new ArrayList<>();
    private Handler mHandler = new Handler();
    //    String[] again_elements = {"7", "8", "9",
//            "10", "11", "12", "13", "14", "15", "16"};
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private List<Boolean> list_bool = new ArrayList<>();
    private int position;
    private DialogUtil dialogUtil;
    private String TAG = SelectLinkageYaoKongQiActivity.class.getSimpleName();
    private int CONNWIFI = 101;
    private String panelNumber = "";
    private String panelType = "";
    private String panelName = "";
    private Map sensor_map = new HashMap();
    private String boxNumber;


    @Override
    protected int viewId() {
        return R.layout.selection_linkage_yaokongqi_lay;
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
        panelNumber = (String) getIntent().getSerializableExtra("panelNumber");
        panelType = (String) getIntent().getSerializableExtra("panelType");
        panelName = (String) getIntent().getSerializableExtra("panelName");
        boxNumber = (String) getIntent().getSerializableExtra("boxNumber");
        sensor_map = (Map) getIntent().getSerializableExtra("sensor_map");
        if (panelName != null) project_select.setText(panelName);
        onData();
//        initWifiConect();
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
//        controllerNumber = (String) getIntent().getSerializableExtra("controllerNumber");
        getOtherDevices("");
        list_hand_scene = new ArrayList<>();


        selectexcutesceneresultadapter = new SelectLinkageYaoKongQiAdapter(SelectLinkageYaoKongQiActivity.this,
                list_hand_scene, listint, listintwo, dialogUtil, new SelectLinkageYaoKongQiAdapter.RefreshListener() {

            @Override
            public void refresh() {
                getOtherDevices("");
            }
        });
        maclistview_id.setAdapter(selectexcutesceneresultadapter);
//        xListView_scan.setPullLoadEnable(false);
//        xListView_scan.setFootViewHide();
//        xListView_scan.setXListViewListener(this);
    }

    private void setPicture(String type) {
        switch (type) {
            case "AA02":
                listint.add(R.drawable.icon_zhuwo_60);
                listintwo.add(R.drawable.icon_zhuwo_60);
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
            case "202":
            case "206":
                listint.add(R.drawable.icon_yaokongqi_40);
                listintwo.add(R.drawable.icon_yaokongqi_40);
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
        mapdevice.put("token", TokenUtil.getToken(SelectLinkageYaoKongQiActivity.this));
        mapdevice.put("controllerNumber", panelNumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleDeviceInfos
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        getOtherDevices("load");
                    }
                }, SelectLinkageYaoKongQiActivity.this, dialogUtil) {
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
                        for (int i = 0; i < user.deviceList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.deviceList.get(i).device_name);
                            mapdevice.put("number", user.deviceList.get(i).number);
                            mapdevice.put("type", user.deviceList.get(i).type);
                            mapdevice.put("deviceId", user.deviceList.get(i).deviceId);
                            list_hand_scene.add(mapdevice);
                            setPicture(user.deviceList.get(i).type);
                        }

                        selectexcutesceneresultadapter.setLists(list_hand_scene, listint, listintwo,sensor_map);
                        selectexcutesceneresultadapter.notifyDataSetChanged();
                        switch (doit) {
                            case "refresh":

                                break;
                            case "load":
                                break;
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectLinkageYaoKongQiActivity.this.finish();
                break;
            case R.id.next_step_txt:


                break;
            case R.id.rel_scene_set:

                break;//执行某些手动场景
        }
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
            super.handleMessage(msg);
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
    }
}
