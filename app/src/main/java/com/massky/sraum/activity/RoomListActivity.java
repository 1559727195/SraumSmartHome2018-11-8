package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.google.gson.GsonBuilder;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.ManagerRoomAdapter;
import com.massky.sraum.adapter.RoomListNewAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.receiver.ApiTcpReceiveHelper;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/9.
 */

public class RoomListActivity extends BaseActivity implements XListView.IXListViewListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    @InjectView(R.id.project_select)
    TextView project_select;
    @InjectView(R.id.add_room)
    ImageView add_room;
    //    @InjectView(R.id.next_step_txt)
//    TextView next_step_txt;
    String[] again_elements = {"客厅", "卧室", "厨房", "客厅", "餐厅", "阳台", "儿童房", "老年房"};
    private List<Map> list_hand_scene;
    private Handler mHandler = new Handler();
    //    @InjectView(R.id.add_room)
//    TextView add_room;
    private RoomListNewAdapter managerroomadapter;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private List<Map> roomsInfos = new ArrayList<>();
    private String areaNumber;
    private String doit;
    private String authType;

    @Override
    protected int viewId() {
        return R.layout.room_list_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
        managerroomadapter = new RoomListNewAdapter(RoomListActivity.this, roomsInfos, new RoomListNewAdapter.RefreshListener() {
            @Override
            public void refresh() {
                get_allroominfo();
            }
        });
        xListView_scan.setAdapter(managerroomadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        get_allroominfo();
//        authType = (String) SharedPreferencesUtil.getData(RoomListActivity.this, "authType", "");
        switch (authType) {
            case "1":
                add_room.setVisibility(View.VISIBLE);
                break;
            case "2":
                add_room.setVisibility(View.GONE);
                break;
        }
    }

    private void get_allroominfo() {
        //获取网关名称（APP->网关）
        Map map = new HashMap();
        map.put("areaNumber", areaNumber);
        map.put("token", TokenUtil.getToken(RoomListActivity.this));
        MyOkHttp.postMapObject(ApiHelper.sraum_getRoomsInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        get_allroominfo();
                    }
                }, RoomListActivity.this, null) {
                    @Override
                    public void onSuccess(User user) {
//                        project_select.setText(user.name);
                        roomsInfos = new ArrayList<>();

                        for (int i = 0; i < user.roomList.size(); i++) {
                            Map map = new HashMap();
                            map.put("number", user.roomList.get(i).number);
                            map.put("name", user.roomList.get(i).name);
                            map.put("count", user.roomList.get(i).count);
                            map.put("authType",authType);
                            roomsInfos.add(map);
                        }

                        project_select.setText("房间列表" + "(" + user.roomList.size() + ")");
                        managerroomadapter.setList(roomsInfos);
                        managerroomadapter.setAreaNumber(areaNumber);
                        managerroomadapter.notifyDataSetChanged();
                    }

                    @Override
                    public void threeCode() {

                    }
                });
    }


    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
//        next_step_txt.setOnClickListener(this);
        add_room.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        doit = (String) getIntent().getSerializableExtra("doit");
        switch (doit == null ? "" : doit) {
            case "sraum_deviceRelatedRoom":
                areaNumber = (String) SharedPreferencesUtil.getData(RoomListActivity.this, "areaNumber", "");
                authType = (String) SharedPreferencesUtil.getData(RoomListActivity.this, "authType", "");
                break;
            default:
                areaNumber = (String) getIntent().getSerializableExtra("areaNumber");
                authType = (String) getIntent().getSerializableExtra("authType");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                RoomListActivity.this.finish();
                break;
//            case R.id.next_step_txt://下一步
//                ApplicationContext.getInstance().finishActivity(AutoAgainSceneActivity.class);
//                ManagerRoomActivity.this.finish();
//                break;
            case R.id.add_room://
                Intent intent = new Intent(RoomListActivity.this, AddNewRoomActivity.class);
                intent.putExtra("areaNumber", areaNumber);
                intent.putExtra("doit", getIntent().getSerializableExtra("doit"));
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
        get_allroominfo();
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
