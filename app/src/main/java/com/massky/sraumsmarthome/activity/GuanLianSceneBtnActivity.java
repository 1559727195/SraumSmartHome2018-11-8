package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class GuanLianSceneBtnActivity extends BaseActivity{
    @InjectView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @Override
    protected int viewId() {
        return R.layout.guanlian_scene_btn;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        rel_scene_set.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        next_step_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_scene_set:
//                GuanLianSceneActivity
                        startActivity(new Intent(GuanLianSceneBtnActivity.this,
                                GuanLianSceneActivity.class));
                break;
            case R.id.next_step_txt:
                GuanLianSceneBtnActivity.this.finish();
                break;
        }
    }
}
