package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.adapter.AddHandSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/5.
 */

public class AddHandSceneActivity extends BaseActivity implements XListView.IXListViewListener{
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


    @Override
    protected int viewId() {
        return R.layout.add_hand_scene_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
//        for (int i = 0 ; i < 10; i ++) {
//            Map map = new HashMap();
//            map.put("position:" , i);
//            list_hand_scene.add(map);
//        }
        Map map = new HashMap();
        map.put("type","A204");//四路灯控
        map.put("name","主灯");//四路灯控
        list_hand_scene.add(map);

        Map map1 = new HashMap();
        map1.put("type","A501");//空调
        map1.put("name","空调");//空调
        list_hand_scene.add(map1);

        Map map2 = new HashMap();
        map2.put("type","A401");//窗帘
        map2.put("name","窗帘");//窗帘
        list_hand_scene.add(map2);

        Map map3 = new HashMap();
        map3.put("type","A303");//调光灯
        map3.put("name","调光灯");//
        list_hand_scene.add(map3);

        addhandSceneAdapter = new AddHandSceneAdapter(AddHandSceneActivity.this,list_hand_scene);
        xListView_scan.setAdapter(addhandSceneAdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
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
                intent.putExtra("excute","hand");//自动的
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
