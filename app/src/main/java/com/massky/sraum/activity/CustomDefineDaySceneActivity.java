package com.massky.sraum.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.adapter.AgainAutoDaysSelectSceneAdapter;
import com.massky.sraum.adapter.AgainAutoSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/9.
 */

public class CustomDefineDaySceneActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.xListView_scan)
    ListView xListView_scan;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    String [] again_elements = {"周一","周二","周三","周四","周五","周六","周日"};
    private List<Map> list_hand_scene;
    private AgainAutoDaysSelectSceneAdapter againAutoSceneAdapter;

    @Override
    protected int viewId() {
        return R.layout.custom_define_dayscene_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name",element);
            map.put("type","0");
            list_hand_scene.add(map);
        }
        againAutoSceneAdapter = new AgainAutoDaysSelectSceneAdapter(CustomDefineDaySceneActivity.this,list_hand_scene);
        xListView_scan.setAdapter(againAutoSceneAdapter);
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
                CustomDefineDaySceneActivity.this.finish();
                break;
            case R.id.next_step_txt://下一步
                ApplicationContext.getInstance().finishActivity(AutoAgainSceneActivity.class);
                CustomDefineDaySceneActivity.this.finish();
                break;
        }
    }
}
