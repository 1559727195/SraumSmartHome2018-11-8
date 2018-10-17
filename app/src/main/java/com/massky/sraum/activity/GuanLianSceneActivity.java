package com.massky.sraum.activity;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import com.massky.sraum.R;
import com.massky.sraum.adapter.AddHandSceneAdapter;
import com.massky.sraum.adapter.GuanLianSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class GuanLianSceneActivity extends BaseActivity implements XListView.IXListViewListener{
    private List<Map> list_hand_scene;
    private GuanLianSceneAdapter guanlianSceneAdapter;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    String [] again_elements = {"客厅开关","主卧开关","儿童房开关",
            "书房开关","客厅窗帘","餐厅开关"};

    @Override
    protected int viewId() {
        return R.layout.guanlian_scene;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
            for (String element : again_elements) {
                Map map = new HashMap();
                map.put("name",element);
                switch (element) {
                    case "客厅开关":
                        map.put("image",R.drawable.icon_deng_sm);
                        break;
                    case "主卧开关":
                        map.put("image",R.drawable.icon_deng_sm);
                        break;
                    case "儿童房开关":
                        map.put("image",R.drawable.icon_deng_sm);
                        break;
                    case "书房开关":
                        map.put("image",R.drawable.icon_deng_sm);
                        break;
                    case "客厅窗帘":
                        map.put("image",R.drawable.icon_chuanglian_sm);
                        break;
                    case "餐厅开关":
                        map.put("image",R.drawable.icon_deng_sm);
                        break;
                }
            list_hand_scene.add(map);
        }

        guanlianSceneAdapter = new GuanLianSceneAdapter(GuanLianSceneActivity.this,list_hand_scene);
        xListView_scan.setAdapter(guanlianSceneAdapter);
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
        switch (v.getId()){
            case R.id.back:
                GuanLianSceneActivity.this.finish();
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
