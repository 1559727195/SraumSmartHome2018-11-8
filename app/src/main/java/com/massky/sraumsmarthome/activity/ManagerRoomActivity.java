package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.ManagerRoomAdapter;
import com.massky.sraumsmarthome.adapter.RoomListAdapter;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.view.XListView;
import com.massky.sraumsmarthome.widget.ApplicationContext;
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
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name",element);
            switch (element) {
                case "客厅":
                    map.put("image",R.drawable.icon_keting_sm);
                    break;
                case "卧室":
                    map.put("image",R.drawable.icon_woshi_sm);
                    break;
                case "厨房":
                    map.put("image",R.drawable.icon_chufang_sm);
                    break;
                case "餐厅":
                    map.put("image",R.drawable.icon_canting_sm);
                    break;
                case "阳台":
                    map.put("image",R.drawable.icon_yangtai_sm);
                    break;
                case "儿童房":
                    map.put("image",R.drawable.icon_ertongfang_sm);
                    break;
                case "老年房":
                    map.put("image",R.drawable.icon_laorenfang_sm);
                    break;
            }
            map.put("type","0");
            list_hand_scene.add(map);
        }

        managerroomadapter = new ManagerRoomAdapter(ManagerRoomActivity.this,list_hand_scene);
        xListView_scan.setAdapter(managerroomadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
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
                    startActivity(new Intent(ManagerRoomActivity.this,AddRoomActivity.class));
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
