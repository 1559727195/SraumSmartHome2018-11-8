package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.adapter.AgainAutoSceneAdapter;
import com.massky.sraum.adapter.RoomListAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.w3c.dom.Text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/9.
 */

public class SelectRoomActivity extends BaseActivity implements XListView.IXListViewListener{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.xListView_scan)
   XListView xListView_scan;
    String [] again_elements = {"客厅","卧室","厨房","客厅","餐厅","阳台","儿童房","老年房"};
    private List<Map> list_hand_scene;
    private RoomListAdapter roomlistadapter;
    private Handler mHandler = new Handler();
    @InjectView(R.id.save_select_room)
    Button save_select_room;
    @InjectView(R.id.manager_room_txt)
    TextView manager_room_txt;
    @InjectView(R.id.status_view)
    StatusView statusView;

    @Override
    protected int viewId() {
        return R.layout.select_room_act;
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

        roomlistadapter = new RoomListAdapter(SelectRoomActivity.this,list_hand_scene);
        xListView_scan.setAdapter(roomlistadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        manager_room_txt.setOnClickListener(this);
        save_select_room.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectRoomActivity.this.finish();
                break;
            case R.id.manager_room_txt://下一步
                startActivity(new Intent(SelectRoomActivity.this
                ,ManagerRoomActivity.class));
                break;
            case R.id.save_select_room:
                SelectRoomActivity.this.finish();
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
