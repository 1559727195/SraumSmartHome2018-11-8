package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.adapter.ExecuteSomeHandSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/12.
 */

public class ExcuteSomeHandSceneActivity extends BaseActivity implements XListView.IXListViewListener{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private ExecuteSomeHandSceneAdapter executesome_handsceneadapter;
    String [] again_elements = {"全屋灯光全开","全屋灯光全关","回家模式","离家","吃饭模式","看电视",
    "睡觉"};
    private List<Map> list_hand_scene = new ArrayList<>();
    private Handler mHandler = new Handler();

    @Override
    protected int viewId() {
        return R.layout.execute_somehand_scene;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        next_step_txt.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name",element);
            map.put("type","0");
            switch (element) {
                case "全屋灯光全开":
                    map.put("image",R.drawable.icon_deng_sm);
                    break;
                case "全屋灯光全关":
                    map.put("image",R.drawable.icon_guandeng_sm);
                    break;
                case "回家模式":
                    map.put("image",R.drawable.icon_huijia_sm);
                    break;
                case "离家":
                    map.put("image",R.drawable.icon_lijia_sm);
                    break;
                case "吃饭模式":
                    map.put("image",R.drawable.icon_chifan_sm);
                    break;
                case "看电视":
                    map.put("image",R.drawable.icon_kandiashi_sm);
                    break;
                case "睡觉":
                    map.put("image",R.drawable.icon_shuijiao_sm);
                    break;
                case "K歌":
                    map.put("image",R.drawable.icon_kge_sm);
                    break;
            }
            list_hand_scene.add(map);
        }

        executesome_handsceneadapter = new ExecuteSomeHandSceneAdapter(ExcuteSomeHandSceneActivity.this,list_hand_scene);
        xListView_scan.setAdapter(executesome_handsceneadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ExcuteSomeHandSceneActivity.this.finish();
                break;
            case R.id.next_step_txt:
                Intent intent = new Intent(ExcuteSomeHandSceneActivity.this,
                        GuanLianSceneBtnActivity.class);
                intent.putExtra("excute","auto");//自动的
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
