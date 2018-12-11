package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.AgainAutoSceneAdapter;
import com.massky.sraum.adapter.HomeDeviceListAdapter;
import com.massky.sraum.adapter.RoomListAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/1/9.
 */

public class SelectRoomActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    String[] again_elements = {"客厅", "卧室", "厨房", "客厅", "餐厅", "阳台", "儿童房", "老年房"};
    private List<Map> list_hand_scene;
    private RoomListAdapter roomlistadapter;
    private Handler mHandler = new Handler();
    @InjectView(R.id.save_select_room)
    Button save_select_room;
    @InjectView(R.id.manager_room_txt)
    TextView manager_room_txt;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private Map map_device = new HashMap();
    private List<Map> roomList = new ArrayList<>();
    private DialogUtil dialogUtil;
    private String areaNumber;
    private int position;

    @Override
    protected int viewId() {
        return R.layout.select_room_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
        map_device = (Map) getIntent().getSerializableExtra("map_deivce");
        room_list_show_adapter();
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
    }


    /**
     * 侧栏房间列数据显示
     */
    private void room_list_show_adapter() {
        roomList = new ArrayList<>();
        roomlistadapter = new RoomListAdapter(SelectRoomActivity.this, roomList, new RoomListAdapter.HomeDeviceItemClickListener() {
            @Override
            public void homedeviceClick(String number) {//获取单个房间关联信息（APP->网关）

            }
        });
        xListView_scan.setAdapter(roomlistadapter);//设备侧栏列表
        xListView_scan.setOnItemClickListener(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        manager_room_txt.setOnClickListener(this);
        save_select_room.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sraum_getRoomsInfo(areaNumber);
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
        mapdevice.put("token", TokenUtil.getToken(SelectRoomActivity.this));
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getRoomsInfo(areaNumber);
                    }
                }, SelectRoomActivity.this, dialogUtil) {
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
                        ToastUtil.showToast(SelectRoomActivity.this, "areaNumber\n" +
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

                        //加载默认房间下的设备列表
                        if (roomList.size() != 0) {
                            display_room_list(0);
                        }
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(SelectRoomActivity.this, "areaNumber 不\n" +
                                "正确");
                    }
                });
    }

    /**
     * //去显示房间列表
     */
    private void display_room_list(int position) {
        roomlistadapter.setList1(roomList);
        roomlistadapter.notifyDataSetChanged();
        for (int i = 0; i < roomList.size(); i++) {
            if (i == position) {
                RoomListAdapter.getIsSelected().put(i, true);
            } else {
                RoomListAdapter.getIsSelected().put(i, false);
            }
        }
        roomlistadapter.notifyDataSetChanged();
    }

    @Override
    protected void onData() {
        areaNumber = (String) SharedPreferencesUtil.getData(SelectRoomActivity.this, "areaNumber", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectRoomActivity.this.finish();
                break;
            case R.id.manager_room_txt://下一步
                Intent intent = new Intent(SelectRoomActivity.this
                        , RoomListActivity.class);
                intent.putExtra("doit","sraum_deviceRelatedRoom");
                startActivity(intent);
                break;
            case R.id.save_select_room:
                String roomNumber = (String) roomList.get(position).get("number");
                sraum_deviceRelatedRoom(roomNumber);
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
        sraum_getRoomsInfo(areaNumber);
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
    public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
        position = position1 - 1;
        for (int i = 0; i < roomList.size(); i++) {
            if (i == position) {
                RoomListAdapter.getIsSelected().put(i, true);
            } else {
                RoomListAdapter.getIsSelected().put(i, false);
            }
        }

        roomlistadapter.notifyDataSetChanged();

    }


    /**
     * 添加完设备关联房间
     */
    private void sraum_deviceRelatedRoom(String roomNumber) {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }

        map_device.put("token", TokenUtil.getToken(SelectRoomActivity.this));
        map_device.put("areaNumber", areaNumber);
        map_device.put("roomNumber", roomNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_deviceRelatedRoom
                , map_device, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getRoomsInfo(areaNumber);
                    }
                }, SelectRoomActivity.this, dialogUtil) {
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
                        ToastUtil.showToast(SelectRoomActivity.this, "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void onSuccess(final User user) {
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(SelectRoomActivity.this, "roomNumber 不正确");
                    }

                    @Override
                    public void fourCode() {
                        ToastUtil.showToast(SelectRoomActivity.this, "deviceId 不正确");
                    }

                    @Override
                    public void fiveCode() {
                        ToastUtil.showToast(SelectRoomActivity.this, "type 不正确");
                    }
                });
    }


}
