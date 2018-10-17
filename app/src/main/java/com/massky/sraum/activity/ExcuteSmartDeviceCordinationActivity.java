package com.massky.sraum.activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.adapter.SelectExcuteSceneResultAdapter;
import com.massky.sraum.adapter.SelectSmartExcuteCordinationAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/16.
 */

public class ExcuteSmartDeviceCordinationActivity extends BaseActivity implements XListView.IXListViewListener{

    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private List<Map> list_hand_scene = new ArrayList<>();
    private SelectSmartExcuteCordinationAdapter selectsmartexcutecordinationadapter;
    private  String [] autoElements  = {"主卫水浸","防盗门门磁","防盗门猫眼","人体检测","防盗门门锁"
            };

    @Override
    protected int viewId() {
        return R.layout.excutesmartdevice_cordination_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
//        for (int i = 0; i < autoElements.length; i++) {
//            Map map = new HashMap();
//            map.put("name", autoElements[i]);
//            list_hand_scene.add(map);
//        }
        for (String element : autoElements) {
            Map map = new HashMap();
            map.put("name", element);
            switch (element) {
                case "主卫水浸":
                    map.put("image", R.drawable.icon_shujin_sm);
                    break;
                case "防盗门门磁":
                    map.put("image", R.drawable.icon_menci_sm);
                    break;
                case "防盗门猫眼":
                    map.put("image", R.drawable.icon_maoyan_sm);
                    break;
                case "人体检测":
                    map.put("image", R.drawable.icon_rentijiance_sm);
                    break;
                case "防盗门门锁":
                    map.put("image", R.drawable.icon_mensuo_sm);
                    break;
            }

            list_hand_scene.add(map);

            selectsmartexcutecordinationadapter = new SelectSmartExcuteCordinationAdapter(
                    ExcuteSmartDeviceCordinationActivity.this,
                    list_hand_scene);
            xListView_scan.setAdapter(selectsmartexcutecordinationadapter);
            xListView_scan.setPullLoadEnable(false);
            xListView_scan.setFootViewHide();
            xListView_scan.setXListViewListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ExcuteSmartDeviceCordinationActivity.this.finish();
                break;
            case R.id.next_step_txt:
                startActivity(new Intent(ExcuteSmartDeviceCordinationActivity.this,
                        SelectExecuteSceneResultActivity.class));
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
