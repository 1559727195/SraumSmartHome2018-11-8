package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.adapter.AddHandSceneAdapter;
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

public class AutoAgainSceneActivity extends BaseActivity{
    private AgainAutoSceneAdapter againAutoSceneAdapter;
    private List<Map> list_hand_scene;
    @InjectView(R.id.xListView_scan)
    ListView xListView_scan;
    @InjectView(R.id.custom_again_time)
    TextView custom_again_time;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.back)
    ImageView  back;
    String [] again_elements = {"执行一次","每天","工作日","周末"};

    @Override
    protected int viewId() {
        return R.layout.autoagain_scene_act;//
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name",element);
            map.put("type","0");
            list_hand_scene.add(map);//
        }
        againAutoSceneAdapter = new AgainAutoSceneAdapter(AutoAgainSceneActivity.this,list_hand_scene);
        xListView_scan.setAdapter(againAutoSceneAdapter);
    }

    @Override
    protected void onEvent() {
        custom_again_time.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_again_time:
                startActivity(new Intent(AutoAgainSceneActivity.this,CustomDefineDaySceneActivity.class));
                break;//自定义是哪天执行，
            case R.id.next_step_txt://下一步
                AutoAgainSceneActivity.this.finish();
                break;
            case R.id.back:
                AutoAgainSceneActivity.this.finish();
                break;
        }
    }
}
