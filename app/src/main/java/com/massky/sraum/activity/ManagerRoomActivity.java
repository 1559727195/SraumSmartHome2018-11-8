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
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.ManagerRoomAdapter;
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

public class ManagerRoomActivity extends BaseActivity implements XListView.IXListViewListener{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.xListView_scan)
   XListView xListView_scan;
//    @InjectView(R.id.next_step_txt)
//    TextView next_step_txt;
    String [] again_elements = {"客厅","卧室","厨房","客厅","餐厅","阳台","儿童房","老年房"};
    private List<Map> list_hand_scene;
    private Handler mHandler = new Handler();
    @InjectView(R.id.add_room)
   TextView add_room;
    private ManagerRoomAdapter managerroomadapter;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private List<Map> roomsInfos = new ArrayList<>();

    @Override
    protected int viewId() {
        return R.layout.manager_room_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
//        for (String element : again_elements) {
//            Map map = new HashMap();
//            map.put("name",element);
//            switch (element) {
//                case "客厅":
//                    map.put("image",R.drawable.icon_keting_sm);
//                    break;
//                case "卧室":
//                    map.put("image",R.drawable.icon_woshi_sm);
//                    break;
//                case "厨房":
//                    map.put("image",R.drawable.icon_chufang_sm);
//                    break;
//                case "餐厅":
//                    map.put("image",R.drawable.icon_canting_sm);
//                    break;
//                case "阳台":
//                    map.put("image",R.drawable.icon_yangtai_sm);
//                    break;
//                case "儿童房":
//                    map.put("image",R.drawable.icon_ertongfang_sm);
//                    break;
//                case "老年房":
//                    map.put("image",R.drawable.icon_laorenfang_sm);
//                    break;
//            }
//            map.put("type","0");
//            list_hand_scene.add(map);
//        }


        managerroomadapter = new ManagerRoomAdapter(ManagerRoomActivity.this,list_hand_scene);

        xListView_scan.setAdapter(managerroomadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);

        get_allroominfo();
        addBrodcastAction();
    }

    /**
     * 添加TCP接收广播通知
     * // add Action1
     */
    private void addBrodcastAction() {
        sraum_pushDeleteRoom();
        sraum_pushRoomName();
    }


    /**
     * 推送删除房间（网关->APP）
     */
    private void sraum_pushDeleteRoom() {
        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.sraum_pushDeleteRoom), new OnActionResponse() {

            @Override
            public void onResponse(Intent intent) {
                String tcpreceiver = intent.getStringExtra("strcontent");
//                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
                //解析json数据
                final User user = new GsonBuilder().registerTypeAdapterFactory(
                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
                if (user == null) return;
                String number = user.number;//房间编号
                ToastUtil.showToast(ManagerRoomActivity.this,"该房间被删除:" + number);

            }
        });
    }

    /**
     * 推送房间名称（网关->APP）
     */
    private void sraum_pushRoomName() {
        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.sraum_pushRoomName), new OnActionResponse() {

            @Override
            public void onResponse(Intent intent) {
                String tcpreceiver = intent.getStringExtra("strcontent");
//                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
                //解析json数据
                final User user = new GsonBuilder().registerTypeAdapterFactory(
                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
                if (user == null) return;
                String number = user.number;//房间编号
                String newName = user.newName;//卧室
                ToastUtil.showToast(ManagerRoomActivity.this,"卧室:" + newName);

            }
        });
    }


    private void get_allroominfo() {
        //获取网关名称（APP->网关）
        Map map = new HashMap();
        map.put("command", "sraum_getRoomsInfo");

        MyOkHttp.postMapObject(ApiHelper.sraum_getRoomsInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        get_allroominfo();
                    }
                },ManagerRoomActivity.this,null) {
                    @Override
                    public void onSuccess(User user) {
//                        project_select.setText(user.name);
                        roomsInfos = new ArrayList<>();

                        for (int i = 0; i < user.roomList.size(); i++) {
                            Map map = new HashMap();
                            map.put("number", user.roomList.get(i).number);
                            map.put("name", user.roomList.get(i).name);
                            map.put("count", user.roomList.get(i).count);
                            roomsInfos.add(map);
                        }

                        managerroomadapter.setList(roomsInfos);
                        managerroomadapter.notifyDataSetChanged();
                    }

                    @Override
                    public void threeCode() {
                        for (int i = 0; i < 10; i++) {
                            Map map = new HashMap();
                            map.put("number", "1");
                            map.put("name", "客厅");
                            map.put("count", "10");
                            roomsInfos.add(map);
                        }

                        managerroomadapter.setList(roomsInfos);
                        managerroomadapter.notifyDataSetChanged();
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ManagerRoomActivity.this.finish();
                break;
//            case R.id.next_step_txt://下一步
//                ApplicationContext.getInstance().finishActivity(AutoAgainSceneActivity.class);
//                ManagerRoomActivity.this.finish();
//                break;
            case R.id.add_room://
                Intent intent = new Intent(ManagerRoomActivity.this,AddRoomActivity.class);
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
}
