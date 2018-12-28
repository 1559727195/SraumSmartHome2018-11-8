package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.ManagerRoomAdapter;
import com.massky.sraum.adapter.MyAreaListAdapter;
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
 * Created by zhu on 2018/2/12.
 */

public class HomeSettingActivity extends BaseActivity implements XListView.IXListViewListener {
    private List<Map> list_hand_scene;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_cancel_wangguan)
    Button btn_cancel_wangguan;
    @InjectView(R.id.add_room)
    ImageView add_room;
    private MyAreaListAdapter homesettingadapter;
    private DialogUtil dialogUtil;
    private List<Map> areaList = new ArrayList<>();
    private ManagerRoomAdapter manageroomadapter;
    private List<Map> roomList = new ArrayList<>();//房间列表

    @Override
    protected int viewId() {
        return R.layout.home_setting_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_cancel_wangguan.setOnClickListener(this);
        add_room.setOnClickListener(this);
    }

    /**
     * 获取所有区域
     */
    private void sraum_getAllArea() {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(HomeSettingActivity.this));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getAllArea();
                    }
                }, HomeSettingActivity.this, dialogUtil) {
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
                            mapdevice.put("name", user.areaList.get(i).areaName);
                            mapdevice.put("number", user.areaList.get(i).number);
                            mapdevice.put("sign", user.areaList.get(i).sign);
                            mapdevice.put("authType", user.areaList.get(i).authType);
                            areaList.add(mapdevice);
                        }

                        if (user.areaList != null && user.areaList.size() != 0) {//区域命名
//                            //，加载默认区域下默认房间
//                            if (areaNumber != null || !areaNumber.equals("")) {
//                                sraum_getRoomsInfo(areaNumber);
//                            } else {
//                                current_area_map = areaList.get(0);
//                                sraum_getRoomsInfo(user.areaList.get(0).number);
//                            }
                            if (user.areaList.size() == 1) {//只有一个区域的话，直接显示房间列表
                                //去获取房间列表
                                add_room.setVisibility(View.VISIBLE);
                                sraum_getRoomsInfo(user.areaList.get(0).number);

                            } else {//显示区域列表
                                add_room.setVisibility(View.GONE);
                                homesettingadapter = new MyAreaListAdapter(HomeSettingActivity.this, areaList);
                                xListView_scan.setAdapter(homesettingadapter);
                            }
                        }
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        sraum_getAllArea();
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
        mapdevice.put("token", TokenUtil.getToken(HomeSettingActivity.this));
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getRoomsInfo(areaNumber);
                    }
                }, HomeSettingActivity.this, dialogUtil) {
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
                        ToastUtil.showToast(HomeSettingActivity.this, "areaNumber\n" +
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
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(HomeSettingActivity.this, "areaNumber 不\n" +
                                "正确");
                    }
                });
    }

    /**
     * 展示房间列表信息
     */
    private void display_room_list() {
        manageroomadapter = new ManagerRoomAdapter(HomeSettingActivity.this, roomList);
        xListView_scan.setAdapter(manageroomadapter);
    }


    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                HomeSettingActivity.this.finish();
                break;
            case R.id.btn_cancel_wangguan:
            case R.id.add_room:
                startActivity(new Intent(HomeSettingActivity.this, AddAreaActivity.class));
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

}
