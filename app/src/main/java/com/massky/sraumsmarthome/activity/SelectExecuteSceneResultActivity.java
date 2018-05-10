package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.GuanLianSceneAdapter;
import com.massky.sraumsmarthome.adapter.SelectExcuteSceneResultAdapter;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/12.
 */

public class SelectExecuteSceneResultActivity extends BaseActivity implements XListView.IXListViewListener{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    @InjectView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;
    private SelectExcuteSceneResultAdapter selectexcutesceneresultadapter;
    private List<Map> list_hand_scene = new ArrayList<>();
    private Handler mHandler = new Handler();
    String [] again_elements = {"客厅开关","主卧开关","儿童房开关",
            "书房开关","客厅窗帘","餐厅开关"};

    @Override
    protected int viewId() {
        return R.layout.select_excscene_resultact;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        rel_scene_set.setOnClickListener(this);
    }

    @Override
    protected void onData() {
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

        selectexcutesceneresultadapter = new SelectExcuteSceneResultAdapter(SelectExecuteSceneResultActivity.this,
                list_hand_scene);
        xListView_scan.setAdapter(selectexcutesceneresultadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectExecuteSceneResultActivity.this.finish();
                break;
            case R.id.next_step_txt:
                Intent intent = new Intent(SelectExecuteSceneResultActivity.this,
                        GuanLianSceneBtnActivity.class);
                intent.putExtra("excute","auto");//自动的
                startActivity(intent);
                break;
            case R.id.rel_scene_set:
                startActivity(new Intent(SelectExecuteSceneResultActivity.this,
                        ExcuteSomeHandSceneActivity.class));
                break;//执行某些手动场景
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
