package com.massky.sraum.activity;

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
import com.massky.sraum.adapter.GuanLianSceneAdapter;
import com.massky.sraum.adapter.MyDeviceListAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/1/8.
 */

public class MyDeviceListActivity extends BaseActivity implements XListView.IXListViewListener {
    private List<Map> list_hand_scene;
    private MyDeviceListAdapter guanlianSceneAdapter;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    @InjectView(R.id.project_select)
    TextView project_select;
    private Handler mHandler = new Handler();
    private MyDeviceListAdapter mydeviceadapter;
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private String accountType;
    private DialogUtil dialogUtil;
    private String areaNumber;
    private String boxnumber;

    @Override
    protected int viewId() {
        return R.layout.mydevice_list;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
        accountType = (String) SharedPreferencesUtil.getData(MyDeviceListActivity.this, "accountType", "");
        areaNumber = (String) SharedPreferencesUtil.getData(MyDeviceListActivity.this, "areaNumber", "");
        boxnumber = (String) SharedPreferencesUtil.getData(MyDeviceListActivity.this, "boxnumber", "");
        list_hand_scene = new ArrayList<>();

        mydeviceadapter = new MyDeviceListAdapter(MyDeviceListActivity.this, list_hand_scene,
                listint, listintwo, accountType, new MyDeviceListAdapter.RefreshListener() {
            @Override
            public void refresh() {
                onResumes();//界面出现之后，调数据刷新
            }
        });
        xListView_scan.setAdapter(mydeviceadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                MyDeviceListActivity.this.finish();
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
        onResumes();
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


    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
//    R.drawable.magnetic_door_s,
//    R.drawable.human_induction_s, R.drawable.water_s, R.drawable.pm25_s,
//    R.drawable.emergency_button_s
    private void onResumes() {
        getOtherDevices("");
    } //

    /**
     * 获取门磁等第三方设备
     *
     * @param doit
     */
    private void getOtherDevices(final String doit) {
//        2.areaNumber：区域编号3.boxNumber网关
        dialogUtil.loadDialog();
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(MyDeviceListActivity.this));
        mapdevice.put("areaNumber", areaNumber);
        MyOkHttp.postMapString(ApiHelper.sraum_myAllDevice
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        getOtherDevices("load");
                    }
                }, MyDeviceListActivity.this, dialogUtil) {
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

                        for (int i = 0; i < user.gatewayList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("number", user.gatewayList.get(i).number);
                            mapdevice.put("name", user.gatewayList.get(i).name);
                            mapdevice.put("type", "网关");
                            list_hand_scene.add(mapdevice);
                            setPicture(user.gatewayList.get(i).type);
                        }

                        for (int i = 0; i < user.panelList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("number", user.panelList.get(i).number);
                            mapdevice.put("name", user.panelList.get(i).name);
                            mapdevice.put("type", user.panelList.get(i).type);
                            mapdevice.put("boxNumber", user.panelList.get(i).boxNumber);
                            list_hand_scene.add(mapdevice);
                            setPicture(user.panelList.get(i).type);
                        }

                        for (int i = 0; i < user.wifiList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("number", user.wifiList.get(i).number);
                            mapdevice.put("name", user.wifiList.get(i).name);
                            mapdevice.put("type", user.wifiList.get(i).type);
                            list_hand_scene.add(mapdevice);
                            setPicture(user.wifiList.get(i).type);
                        }

                        project_select.setText("我的设备(" + list_hand_scene.size() + ")");
                        mydeviceadapter.setLists(list_hand_scene, listint, listintwo);
                        mydeviceadapter.notifyDataSetChanged();
                        switch (doit) {
                            case "refresh":

                                break;
                            case "load":
                                break;
                        }
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
                listint.add(R.drawable.dimminglights);
                listintwo.add(R.drawable.dimminglights);
                break;
            case "A401":
                listint.add(R.drawable.home_curtain);
                listintwo.add(R.drawable.home_curtain);
                break;
            case "A501":
            case "A511":
                listint.add(R.drawable.icon_kongtiao_40);
                listintwo.add(R.drawable.icon_kongtiao_40);
                break;
            case "A801":
                listint.add(R.drawable.icon_menci_40);
                listintwo.add(R.drawable.icon_menci_40_active);
                break;
            case "A901":
                listint.add(R.drawable.icon_rentiganying_40);
                listintwo.add(R.drawable.icon_rentiganying_40);
                break;
            case "AB01":
                listint.add(R.drawable.icon_yanwubjq_40);
                listintwo.add(R.drawable.icon_yanwubjq_40);
                break;
            case "A902":
                listint.add(R.drawable.icon_rucebjq_40);
                listintwo.add(R.drawable.icon_rucebjq_40);
                break;
            case "AB04":
                listint.add(R.drawable.icon_ranqibjq_40);
                listintwo.add(R.drawable.icon_ranqibjq_40);
                break;
            case "AC01":
                listint.add(R.drawable.icon_shuijin_40);
                listintwo.add(R.drawable.icon_shuijin_40);
                break;
            case "AD01":
                listint.add(R.drawable.icon_pm25_40);
                listintwo.add(R.drawable.icon_pm25_40);
                break;
            case "B001":
                listint.add(R.drawable.icon_jinjianniu_40);
                listintwo.add(R.drawable.icon_jinjianniu_40);
                break;
            case "B101"://86插座两位
                listint.add(R.drawable.icon_kaiguan_socket_40);
                listintwo.add(R.drawable.icon_kaiguan_socket_40);
                break;
            case "B102"://86插座两位
            case "网关":
                listint.add(R.drawable.defaultpic);
                listintwo.add(R.drawable.defaultpic);
                //-----
                break;
            case "B201":
                listint.add(R.drawable.icon_zhinengmensuo_40);
                listintwo.add(R.drawable.icon_zhinengmensuo_40_active);
                break;
            case "AA02":
                listint.add(R.drawable.icon_hongwaizfq_40);
                listintwo.add(R.drawable.icon_hongwaizfq_40);
                break;
            case "AA03":
                listint.add(R.drawable.icon_shexiangtou_40);
                listintwo.add(R.drawable.icon_shexiangtou_40);
                break;
            case "AA04":
                listint.add(R.drawable.icon_keshimenling_40);
                listintwo.add(R.drawable.icon_keshimenling_40);
                break;
            case "A611":
                listint.add(R.drawable.freshair);
                listintwo.add(R.drawable.freshair);
                break;
            case "A711":
                listint.add(R.drawable.floorheating);
                listintwo.add(R.drawable.floorheating);
                break;
            case "B301":
                listint.add(R.drawable.icon_jixieshou_40);
                listintwo.add(R.drawable.icon_jixieshou_40);
                break;
            default:
                listint.add(R.drawable.defaultpic);
                listintwo.add(R.drawable.defaultpic);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumes();//界面出现之后，调数据刷新
    }
}
